package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.PermissionEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.exceptions.ResourceNotFoundException;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.*;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingListService {
    private final ShoppingListRepository shoppingListRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final StorePriceRepository storePriceRepository;
    private final EntityManager entityManager;
    private final NotificationService notificationService;

    public ShoppingListService(ShoppingListRepository shoppingListRepository,
                               ProductRepository productRepository,
                               UsersRepository usersRepository,
                               StoreRepository storeRepository,
                               CategoryRepository categoryRepository,
                               StorePriceRepository storePriceRepository,
                               EntityManager entityManager,
                               NotificationService notificationService) {
        this.shoppingListRepository = shoppingListRepository;
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.storePriceRepository = storePriceRepository;
        this.entityManager = entityManager;
        this.notificationService = notificationService;
    }

    public ShoppingListDTO createShoppingList(ShoppingListDTO shoppingListDTO) {
        UsersEntity owner = usersRepository.findById(shoppingListDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + shoppingListDTO.getOwnerId()));
        if (owner.getIsActive() == null || !owner.getIsActive()) {
            throw new IllegalStateException("Only active users can create shopping lists.");
        }
        StoreEntity store = storeRepository.findById(shoppingListDTO.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + shoppingListDTO.getStoreId()));

        entityManager.clear();

        ShoppingListEntity shoppingList = new ShoppingListEntity();
        shoppingList.setName(shoppingListDTO.getName());
        shoppingList.setDescription(shoppingListDTO.getDescription());
        shoppingList.setListType(shoppingListDTO.getListType());
        shoppingList.setActive(shoppingListDTO.isActive());
        shoppingList.setOwner(owner);
        shoppingList.setStore(store);

        List<ShoppingListItemEntity> items = shoppingListDTO.getItems().stream().map(itemDTO -> {
            ProductEntity product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));
            ShoppingListItemEntity item = new ShoppingListItemEntity();
            item.setShoppingList(shoppingList);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity() != null ? BigDecimal.valueOf(itemDTO.getQuantity()) : null);
            item.setChecked(itemDTO.isChecked());
            item.setStatus(itemDTO.getStatus());
            return item;
        }).collect(Collectors.toList());
        shoppingList.setItems(items);

        List<CollaboratorEntity> collaborators = shoppingListDTO.getCollaborators().stream().map(collabDTO -> {
            UsersEntity user = usersRepository.findById(collabDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + collabDTO.getUserId()));
            CollaboratorEntity collaborator = new CollaboratorEntity();
            collaborator.setShoppingList(shoppingList);
            collaborator.setUser(user);
            collaborator.setPermission(PermissionEnum.valueOf(collabDTO.getPermission()));
            
            if (!user.getUserId().equals(owner.getUserId())) {
                notificationService.createCollaboratorAddedNotification(owner, user, shoppingListDTO.getName());
            }
            
            return collaborator;
        }).collect(Collectors.toList());
        shoppingList.setCollaborators(collaborators);

        ShoppingListEntity saved = shoppingListRepository.save(shoppingList);
        return convertToDTO(saved);
    }

    public ShoppingListDTO getShoppingListById(UUID id) {
        ShoppingListEntity shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + id));
        return convertToDTO(shoppingList);
    }

    public List<ShoppingListDTO> getShoppingListsByUserId(UUID userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return shoppingListRepository.findAll().stream()
                .filter(sl -> sl.getOwner().getUserId().equals(userId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public ShoppingListDTO updateShoppingList(UUID id, ShoppingListDTO shoppingListDTO) {
        ShoppingListEntity shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + id));

        shoppingList.setName(shoppingListDTO.getName());
        shoppingList.setDescription(shoppingListDTO.getDescription());
        shoppingList.setListType(shoppingListDTO.getListType());
        shoppingList.setActive(shoppingListDTO.isActive());
        StoreEntity store = storeRepository.findById(shoppingListDTO.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + shoppingListDTO.getStoreId()));
        shoppingList.setStore(store);

        shoppingList.getItems().clear();
        List<ShoppingListItemEntity> items = shoppingListDTO.getItems().stream().map(itemDTO -> {
            ProductEntity product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDTO.getProductId()));
            ShoppingListItemEntity item = new ShoppingListItemEntity();
            item.setShoppingList(shoppingList);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity() != null ? BigDecimal.valueOf(itemDTO.getQuantity()) : null);
            item.setChecked(itemDTO.isChecked());
            item.setStatus(itemDTO.getStatus());
            return item;
        }).toList();
        shoppingList.getItems().addAll(items);

        updateCollaborators(shoppingList, shoppingListDTO.getCollaborators());

        ShoppingListEntity saved = shoppingListRepository.save(shoppingList);
        return convertToDTO(saved);
    }

    private void updateCollaborators(ShoppingListEntity shoppingList, List<CollaboratorDTO> newCollaborators) {
        List<CollaboratorEntity> existingCollaborators = new ArrayList<>(shoppingList.getCollaborators());

        Set<UUID> newCollaboratorUserIds = newCollaborators.stream()
                .map(CollaboratorDTO::getUserId)
                .collect(Collectors.toSet());

        existingCollaborators.removeIf(collaborator -> {
            UUID userId = collaborator.getUser().getUserId();
            if (!newCollaboratorUserIds.contains(userId)) {
                shoppingList.getCollaborators().remove(collaborator);
                return true;
            }
            return false;
        });

        for (CollaboratorDTO collabDTO : newCollaborators) {
            UUID userId = collabDTO.getUserId();

            Optional<CollaboratorEntity> existingCollaborator = shoppingList.getCollaborators().stream()
                    .filter(c -> c.getUser().getUserId().equals(userId))
                    .findFirst();

            if (existingCollaborator.isPresent()) {
                existingCollaborator.get().setPermission(PermissionEnum.valueOf(collabDTO.getPermission()));
            } else {
                UsersEntity user = usersRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

                CollaboratorEntity newCollaborator = new CollaboratorEntity();
                newCollaborator.setShoppingList(shoppingList);
                newCollaborator.setUser(user);
                newCollaborator.setPermission(PermissionEnum.valueOf(collabDTO.getPermission()));

                shoppingList.getCollaborators().add(newCollaborator);
                
                if (!user.getUserId().equals(shoppingList.getOwner().getUserId())) {
                    notificationService.createCollaboratorAddedNotification(shoppingList.getOwner(), user, shoppingList.getName());
                }
            }
        }
    }

    public void softDeleteShoppingList(UUID id) {
        ShoppingListEntity shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + id));
        shoppingList.setActive(false);
        shoppingListRepository.save(shoppingList);
    }

    public List<CollaboratorDTO> getCollaborators(UUID shoppingListId) {
        ShoppingListEntity shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + shoppingListId));
        return shoppingList.getCollaborators().stream()
                .map(this::convertToCollaboratorDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    CategoryDTO dto = new CategoryDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setIcon(category.getIcon());
                    dto.setProducts(category.getProducts().stream()
                            .map(this::convertToShoppingListItemDTO)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getSidebarCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    CategoryDTO dto = new CategoryDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setIcon(category.getIcon());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ShoppingListItemDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToShoppingListItemDTO)
                .collect(Collectors.toList());
    }

    public List<ShoppingListItemDTO> searchProducts(String term) {
        if (term == null || term.trim().isEmpty()) {
            return getAllProducts();
        }
        String lowerTerm = term.toLowerCase();
        return productRepository.findByNameContainingIgnoreCase(lowerTerm).stream()
                .map(this::convertToShoppingListItemDTO)
                .collect(Collectors.toList());
    }

    public List<ShoppingListItemDTO> getProductsByCategory(UUID categoryId) {
        return productRepository.findAll().stream()
                .filter(product -> product.getCategory() != null && product.getCategory().getId().equals(categoryId))
                .map(this::convertToShoppingListItemDTO)
                .collect(Collectors.toList());
    }

    public List<StoreItemDTO> getItemPriceComparisons(UUID itemId) {
        productRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemId));
        List<StorePriceEntity> storePrices = storePriceRepository.findByProduct_ProductId(itemId);
        return storePrices.stream()
                .map(price -> new StoreItemDTO(
                        price.getStore().getStoreId(),
                        price.getStore().getName(),
                        price.getStore().getIcon(),
                        price.getPrice().doubleValue()
                ))
                .collect(Collectors.toList());
    }

    public List<StoreDTO> getAllAvailableStores() {
        return storeRepository.findAll().stream()
                .map(store -> {
                    StoreDTO dto = new StoreDTO();
                    dto.setId(store.getStoreId());
                    dto.setName(store.getName());
                    dto.setIcon(store.getIcon());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ShoppingListDTO convertToDTO(ShoppingListEntity shoppingList) {
        ShoppingListDTO dto = new ShoppingListDTO();
        dto.setId(shoppingList.getId());
        dto.setName(shoppingList.getName());
        dto.setDescription(shoppingList.getDescription());
        dto.setListType(shoppingList.getListType());
        dto.setActive(shoppingList.isActive());
        dto.setOwnerId(shoppingList.getOwner().getUserId());
        dto.setOwnerName(shoppingList.getOwner().getName());
        dto.setStoreId(shoppingList.getStore().getStoreId());
        dto.setStoreName(shoppingList.getStore().getName());
        dto.setCreatedAt(shoppingList.getCreatedAt());
        dto.setUpdatedAt(shoppingList.getUpdatedAt());

        List<ShoppingListItemDTO> itemDTOs = shoppingList.getItems().stream()
                .map(item -> convertToItemDTO(item, shoppingList.getStore().getName()))
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        List<CollaboratorDTO> collaboratorDTOs = shoppingList.getCollaborators().stream()
                .map(this::convertToCollaboratorDTO)
                .collect(Collectors.toList());
        dto.setCollaborators(collaboratorDTOs);

        return dto;
    }

    private ShoppingListItemDTO convertToItemDTO(ShoppingListItemEntity item, String storeName) {
        ShoppingListItemDTO itemDTO = new ShoppingListItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setProductId(item.getProduct().getProductId());
        itemDTO.setProductName(item.getProduct().getName());
        itemDTO.setImage(item.getProduct().getImage());
        itemDTO.setCategoryId(item.getProduct().getCategory() != null ? item.getProduct().getCategory().getId() : null);
        itemDTO.setQuantity(item.getQuantity() != null ? item.getQuantity().doubleValue() : null);
        itemDTO.setIsChecked(item.isChecked());
        itemDTO.setStatus(item.getStatus());
        itemDTO.setStoreName(storeName);
        List<StorePriceEntity> prices = storePriceRepository.findByProduct_ProductId(item.getProduct().getProductId());
        itemDTO.setPrice(prices.stream()
                .filter(p -> p.getStore().getName().equals(storeName))
                .findFirst()
                .map(p -> p.getPrice().doubleValue())
                .orElse(null));
        return itemDTO;
    }

    private CollaboratorDTO convertToCollaboratorDTO(CollaboratorEntity collab) {
        CollaboratorDTO dto = new CollaboratorDTO();
        dto.setUserId(collab.getUser().getUserId());
        dto.setUserName(collab.getUser().getName());
        dto.setPermission(collab.getPermission() != null ? collab.getPermission().name() : "VIEW");
        return dto;
    }

    private ShoppingListItemDTO convertToShoppingListItemDTO(ProductEntity product) {
        ShoppingListItemDTO dto = new ShoppingListItemDTO();
        dto.setId(product.getProductId());
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getName());
        dto.setImage(product.getImage());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setQuantity(null);
        dto.setIsChecked(false);
        dto.setStatus(null);
        return dto;
    }
}