package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.AddProductDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.AvailableProductDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.BulkResultDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.CategoryEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ProductEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StoreEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StorePriceEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.CategoryRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ProductRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StorePriceRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StorePriceRepository storePriceRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            StoreRepository storeRepository,
            StorePriceRepository storePriceRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository    = productRepository;
        this.storeRepository      = storeRepository;
        this.storePriceRepository = storePriceRepository;
        this.categoryRepository   = categoryRepository;
    }

    @Transactional
    public BulkResultDTO addProducts(List<AddProductDTO> dtoList) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (int idx = 0; idx < dtoList.size(); idx++) {
            AddProductDTO dto = dtoList.get(idx);
            String identifier = "index " + idx + " (productName='" + dto.getProductName() + "')";
            try {
                UUID storeId = dto.getStoreId();
                StoreEntity store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid storeId: " + storeId));

                UUID categoryId = resolveCategoryId(dto.getCategoryId(), dto.getCategoryName());

                CategoryEntity category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

                ProductEntity product = createOrUpdateProduct(dto, category);

                createOrUpdateStorePrice(store, product, dto.getPrice(), dto.getBarcode());

                successCount++;
            } catch (Exception e) {
                errors.add(identifier + ": " + e.getMessage());
            }
        }

        boolean overallSuccess = errors.isEmpty();
        return new BulkResultDTO(overallSuccess,
                overallSuccess ? null : errors,
                successCount);
    }

    private UUID resolveCategoryId(UUID providedCategoryId, String providedCategoryName) {
        if (providedCategoryId != null) {
            return providedCategoryId;
        }
        if (providedCategoryName != null && !providedCategoryName.isBlank()) {
            CategoryEntity cat = findOrCreateCategoryByName(providedCategoryName.trim());
            return cat.getId();
        }
        throw new IllegalArgumentException("Either categoryId or categoryName must be provided");
    }

    private CategoryEntity findOrCreateCategoryByName(String categoryName) {
        Optional<CategoryEntity> opt = categoryRepository.findByNameIgnoreCase(categoryName);
        if (opt.isPresent()) {
            return opt.get();
        }
        CategoryEntity newCat = new CategoryEntity();
        newCat.setName(categoryName);
        return categoryRepository.save(newCat);
    }

    private ProductEntity createOrUpdateProduct(AddProductDTO dto, CategoryEntity category) {
        if (dto.getProductId() != null) {
            UUID prodId = dto.getProductId();
            ProductEntity product = productRepository.findById(prodId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid productId: " + prodId));
            product.setName(dto.getProductName());
            product.setDescription(dto.getDescription());
            product.setActive(dto.getIsActive());
            if (product.getCategory() == null || !product.getCategory().getId().equals(category.getId())) {
                product.setCategory(category);
            }
            return productRepository.save(product);
        } else {
            Optional<ProductEntity> opt = productRepository.findByName(dto.getProductName());
            if (opt.isPresent()) {
                ProductEntity product = opt.get();
                product.setDescription(dto.getDescription());
                product.setActive(dto.getIsActive());
                if (product.getCategory() == null || !product.getCategory().getId().equals(category.getId())) {
                    product.setCategory(category);
                }
                return productRepository.save(product);
            } else {
                ProductEntity product = new ProductEntity();
                product.setName(dto.getProductName());
                product.setDescription(dto.getDescription());
                product.setActive(dto.getIsActive());
                product.setCategory(category);
                return productRepository.save(product);
            }
        }
    }

    private void createOrUpdateStorePrice(StoreEntity store,
                                          ProductEntity product,
                                          BigDecimal price,
                                          String barcode) {
        UUID storeId = store.getStoreId();
        List<StorePriceEntity> existingList = storePriceRepository.findByStore_StoreId(storeId);
        Optional<StorePriceEntity> opt = existingList.stream()
                .filter(sp -> sp.getProduct().getProductId().equals(product.getProductId()))
                .findFirst();
        if (opt.isPresent()) {
            StorePriceEntity sp = opt.get();
            sp.setPrice(price != null ? price : BigDecimal.ZERO);
            sp.setBarcode(barcode);
            storePriceRepository.save(sp);
        } else {
            StorePriceEntity sp = new StorePriceEntity();
            sp.setStore(store);
            sp.setProduct(product);
            sp.setPrice(price != null ? price : BigDecimal.ZERO);
            sp.setBarcode(barcode);
            storePriceRepository.save(sp);
        }
    }
    
    @Transactional(readOnly = true)
    public List<AvailableProductDTO> getAvailableProducts(String storeIdStr) {
        UUID storeId = UUID.fromString(storeIdStr);
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid storeId: " + storeIdStr));
        Set<UUID> ownedIds = storePriceRepository.findByStore_StoreId(storeId).stream()
                .map(sp -> sp.getProduct().getProductId())
                .collect(Collectors.toSet());
        return productRepository.findAll().stream()
                .filter(p -> !ownedIds.contains(p.getProductId()))
                .map(p -> new AvailableProductDTO(
                        p.getProductId(),
                        p.getName(),
                        p.getCategory() != null ? p.getCategory().getName() : null,
                        p.getDescription(),
                        p.isActive()))
                .collect(Collectors.toList());
    }
}
