package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.AddProductDTO;
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

import java.util.List;
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
    public List<StorePriceEntity> addProducts(List<AddProductDTO> dtoList) {
        return dtoList.stream()
                .map(this::processSingleProduct)
                .collect(Collectors.toList());
    }

    private StorePriceEntity processSingleProduct(AddProductDTO dto) {
        StorePriceEntity storePrice = dto.getStorePriceId() != null
                ? storePriceRepository.findById(dto.getStorePriceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid storePriceId: " + dto.getStorePriceId()))
                : new StorePriceEntity();

        StoreEntity store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid storeId: " + dto.getStoreId()));
        storePrice.setStore(store);

        ProductEntity product;
        if (dto.getProductId() != null) {
            product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid productId: " + dto.getProductId()));
        } else if (storePrice.getProduct() != null) {
            product = storePrice.getProduct();
        } else {
            product = new ProductEntity();
        }

        product.setName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setActive(dto.isActive());

        CategoryEntity category = categoryRepository
                .findFirstByNameIgnoreCase(dto.getCategory())
                .orElseGet(() -> {
                    CategoryEntity c = new CategoryEntity();
                    c.setName(dto.getCategory());
                    return categoryRepository.save(c);
                });
        product.setCategory(category);

        product.setStore(store);

        productRepository.save(product);
        storePrice.setProduct(product);

        storePrice.setPrice(dto.getPrice());
        storePrice.setBarcode(dto.getBarcode());

        return storePriceRepository.save(storePrice);
    }
}
