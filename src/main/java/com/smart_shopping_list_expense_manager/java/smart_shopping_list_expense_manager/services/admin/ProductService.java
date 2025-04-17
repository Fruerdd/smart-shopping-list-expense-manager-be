package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.AddProductDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ProductEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StoreEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StorePriceEntity;
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

    public ProductService(ProductRepository productRepository,
                          StoreRepository storeRepository,
                          StorePriceRepository storePriceRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.storePriceRepository = storePriceRepository;
    }

    @Transactional
    public List<StorePriceEntity> addProducts(List<AddProductDTO> dtoList) {
        return dtoList.stream()
                .map(this::processSingleProduct)
                .collect(Collectors.toList());
    }

    private StorePriceEntity processSingleProduct(AddProductDTO dto) {
        // 1. Find or load the store
        StoreEntity store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid storeId: " + dto.getStoreId()));

        // 2. Find existing product if productId provided, else by name, or create new
        ProductEntity product;
        if (dto.getProductId() != null) {
            product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid productId: " + dto.getProductId()));
        } else {
            // Option A: Check if product with same name already exists (if you want to avoid duplicates):
            // product = productRepository.findByNameIgnoreCase(dto.getProductName()).orElse(new ProductEntity());
            product = new ProductEntity();
            product.setName(dto.getProductName());
            product.setCategory(dto.getCategory());
            product.setDescription(dto.getDescription());
            product.setActive(dto.isActive());
            productRepository.save(product);
        }

        // 3. Create the StorePriceEntity linking store + product, plus price/barcode
        StorePriceEntity storePrice = new StorePriceEntity();
        storePrice.setStore(store);
        storePrice.setProduct(product);
        storePrice.setPrice(dto.getPrice());
        storePrice.setBarcode(dto.getBarcode());

        return storePriceRepository.save(storePrice);
    }
}

