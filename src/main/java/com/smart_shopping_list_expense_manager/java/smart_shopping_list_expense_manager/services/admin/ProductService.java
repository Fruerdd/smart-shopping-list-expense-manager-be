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
        // 1) Load or create StorePriceEntity
        StorePriceEntity storePrice = dto.getStorePriceId() != null
                ? storePriceRepository.findById(dto.getStorePriceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid storePriceId: " + dto.getStorePriceId()))
                : new StorePriceEntity();

        // 2) Associate store
        StoreEntity store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid storeId: " + dto.getStoreId()));
        storePrice.setStore(store);

        // 3) Find or create product
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

        // 4) Set basic product fields
        product.setName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setActive(dto.isActive());

        // 5) Resolve or create category
        CategoryEntity category = categoryRepository
                .findByName(dto.getCategory())
                .orElseGet(() -> {
                    CategoryEntity c = new CategoryEntity();
                    c.setName(dto.getCategory());
                    return categoryRepository.save(c);
                });
        product.setCategory(category);

        // 6) Associate store on product
        product.setStore(store);

        // 7) Persist product
        productRepository.save(product);
        storePrice.setProduct(product);

        // 8) Set price & barcode
        storePrice.setPrice(dto.getPrice());
        storePrice.setBarcode(dto.getBarcode());

        // 9) Persist store-price record
        return storePriceRepository.save(storePrice);
    }
}
