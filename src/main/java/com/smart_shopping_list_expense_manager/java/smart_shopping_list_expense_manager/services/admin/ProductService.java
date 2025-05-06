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
        // 1) Load or create the StorePriceEntity
        StorePriceEntity storePrice;
        if (dto.getStorePriceId() != null) {
            // editing existing
            storePrice = storePriceRepository.findById(dto.getStorePriceId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid storePriceId: " + dto.getStorePriceId()));
        } else {
            // creating new
            storePrice = new StorePriceEntity();
        }

        // 2) Find or load the store
        StoreEntity store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid storeId: " + dto.getStoreId()));
        storePrice.setStore(store);

        // 3) Find or create the product
        ProductEntity product;
        if (dto.getProductId() != null) {
            product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid productId: " + dto.getProductId()));
        } else if (storePrice.getProduct() != null) {
            // reuse existing product on that price record
            product = storePrice.getProduct();
        } else {
            product = new ProductEntity();
            product.setName(dto.getProductName());
            product.setCategory(dto.getCategory());
            product.setDescription(dto.getDescription());
            product.setActive(dto.isActive());
            productRepository.save(product);
        }
        storePrice.setProduct(product);

        // 4) Overwrite all the price fields
        storePrice.setPrice(dto.getPrice());
        storePrice.setBarcode(dto.getBarcode());

        // 5) Persist and return
        return storePriceRepository.save(storePrice);
    }
}

