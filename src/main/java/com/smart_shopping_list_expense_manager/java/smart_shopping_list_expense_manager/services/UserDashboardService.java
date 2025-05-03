package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.PermissionEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.exceptions.ResourceNotFoundException;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserDashboardService {
    private final UsersRepository usersRepository;
    private final ShoppingListService shoppingListService;
    private final FavoriteProductRepository favoriteProductRepository;
    private final FavoriteStoreRepository favoriteStoreRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StorePriceRepository storePriceRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final CollaboratorRepository collaboratorRepository;

    public UserDashboardService(UsersRepository usersRepository,
                                ShoppingListService shoppingListService,
                                FavoriteProductRepository favoriteProductRepository,
                                FavoriteStoreRepository favoriteStoreRepository,
                                ProductRepository productRepository,
                                StoreRepository storeRepository,
                                StorePriceRepository storePriceRepository,
                                ShoppingListRepository shoppingListRepository,
                                CollaboratorRepository collaboratorRepository) {
        this.usersRepository = usersRepository;
        this.shoppingListService = shoppingListService;
        this.favoriteProductRepository = favoriteProductRepository;
        this.favoriteStoreRepository = favoriteStoreRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.storePriceRepository = storePriceRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.collaboratorRepository = collaboratorRepository;
    }

    public UserDTO getUserById(UUID id) {
        UsersEntity user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUserId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setShoppingLists(shoppingListService.getShoppingListsByUserId(id));
        return userDTO;
    }

    public List<FavoriteProductDTO> getFavoriteProducts(UUID userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return favoriteProductRepository.findAll().stream()
                .filter(fp -> fp.getUser().getUserId().equals(userId))
                .map(fp -> {
                    FavoriteProductDTO dto = new FavoriteProductDTO();
                    dto.setId(fp.getId());
                    dto.setProductId(fp.getProduct().getProductId());
                    dto.setProductName(fp.getProduct().getName());
                    return dto;
                }).collect(Collectors.toList());
    }

    public FavoriteProductDTO addFavoriteProduct(UUID userId, UUID productId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Optional<FavoriteProductEntity> existingFavorite = favoriteProductRepository.findByUserIdAndProductId(userId, productId);

        if (existingFavorite.isPresent()) {
            FavoriteProductEntity favoriteProduct = existingFavorite.get();
            FavoriteProductDTO dto = new FavoriteProductDTO();
            dto.setId(favoriteProduct.getId());
            dto.setProductId(favoriteProduct.getProduct().getProductId());
            dto.setProductName(favoriteProduct.getProduct().getName());
            return dto;
        }

        FavoriteProductEntity favoriteProduct = new FavoriteProductEntity();
        favoriteProduct.setUser(user);
        favoriteProduct.setProduct(product);
        FavoriteProductEntity saved = favoriteProductRepository.save(favoriteProduct);
        FavoriteProductDTO dto = new FavoriteProductDTO();
        dto.setId(saved.getId());
        dto.setProductId(saved.getProduct().getProductId());
        dto.setProductName(saved.getProduct().getName());
        return dto;
    }

    public void removeFavoriteProduct(UUID userId, UUID productId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        FavoriteProductEntity favoriteProduct = favoriteProductRepository.findAll().stream()
                .filter(fp -> fp.getUser().getUserId().equals(userId) && fp.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Favorite product not found for user: " + userId));
        favoriteProductRepository.delete(favoriteProduct);
    }

    public List<FavoriteStoreDTO> getFavoriteStores(UUID userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return favoriteStoreRepository.findAll().stream()
                .filter(fs -> fs.getUser().getUserId().equals(userId))
                .map(fs -> {
                    FavoriteStoreDTO dto = new FavoriteStoreDTO();
                    dto.setId(fs.getId());
                    dto.setStoreId(fs.getStore().getStoreId());
                    dto.setStoreName(fs.getStore().getName());
                    return dto;
                }).collect(Collectors.toList());
    }

    public FavoriteStoreDTO addFavoriteStore(UUID userId, UUID storeId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));

        Optional<FavoriteStoreEntity> existingFavorite = favoriteStoreRepository.findByUserIdAndStoreId(userId, storeId);

        if (existingFavorite.isPresent()) {
            FavoriteStoreEntity favoriteStore = existingFavorite.get();
            FavoriteStoreDTO dto = new FavoriteStoreDTO();
            dto.setId(favoriteStore.getId());
            dto.setStoreId(favoriteStore.getStore().getStoreId());
            dto.setStoreName(favoriteStore.getStore().getName());
            return dto;
        }

        FavoriteStoreEntity favoriteStore = new FavoriteStoreEntity();
        favoriteStore.setUser(user);
        favoriteStore.setStore(store);
        FavoriteStoreEntity saved = favoriteStoreRepository.save(favoriteStore);
        FavoriteStoreDTO dto = new FavoriteStoreDTO();
        dto.setId(saved.getId());
        dto.setStoreId(saved.getStore().getStoreId());
        dto.setStoreName(saved.getStore().getName());
        return dto;
    }

    public void removeFavoriteStore(UUID userId, UUID storeId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        FavoriteStoreEntity favoriteStore = favoriteStoreRepository.findAll().stream()
                .filter(fs -> fs.getUser().getUserId().equals(userId) && fs.getStore().getStoreId().equals(storeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Favorite store not found for user: " + userId));
        favoriteStoreRepository.delete(favoriteStore);
    }

    public List<StoreDTO> searchStores(UUID userId, String query) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<FavoriteStoreDTO> favoriteStores = getFavoriteStores(userId);
        List<UUID> favoriteStoreIds = favoriteStores.stream()
                .map(FavoriteStoreDTO::getStoreId)
                .toList();
        return storeRepository.findByNameContainingIgnoreCase(query).stream()
                .filter(store -> !favoriteStoreIds.contains(store.getStoreId()))
                .map(this::convertToStoreDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(UUID userId, String query) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<FavoriteProductDTO> favoriteProducts = getFavoriteProducts(userId);
        List<UUID> favoriteProductIds = favoriteProducts.stream()
                .map(FavoriteProductDTO::getProductId)
                .toList();
        return productRepository.findByNameContainingIgnoreCase(query).stream()
                .filter(product -> !favoriteProductIds.contains(product.getProductId()))
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getAllProducts(UUID userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return productRepository.findAll().stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    public List<StoreDTO> getAllStores(UUID userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return storeRepository.findAll().stream()
                .map(this::convertToStoreDTO)
                .collect(Collectors.toList());
    }

    public List<StorePriceDTO> getProductPrices(UUID userId, UUID productId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        List<StorePriceEntity> storePrices = storePriceRepository.findByProduct_ProductId(productId);
        return storePrices.stream()
                .map(price -> new StorePriceDTO(
                        price.getStore().getStoreId(),
                        price.getStore().getName(),
                        price.getPrice()
                )).collect(Collectors.toList());
    }

    public List<ShoppingListDTO> getAllShoppingLists(UUID userId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<ShoppingListEntity> ownedLists = shoppingListRepository.findByOwner_UserId(userId);
        List<ShoppingListEntity> sharedLists = shoppingListRepository.findByCollaborators_User_UserId(userId);
        List<ShoppingListEntity> allLists = new ArrayList<>();
        allLists.addAll(ownedLists);
        allLists.addAll(sharedLists.stream().filter(sl -> !ownedLists.contains(sl)).toList());
        return allLists.stream().map(this::convertToShoppingListDTO).collect(Collectors.toList());
    }

    public ShoppingListDTO createShoppingList(UUID userId, ShoppingListDTO shoppingListDTO) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        shoppingListDTO.setOwnerId(userId);
        return shoppingListService.createShoppingList(shoppingListDTO);
    }

    public ShoppingListDTO updateShoppingList(UUID userId, UUID listId, ShoppingListDTO shoppingListDTO) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        ShoppingListEntity list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + listId));
        if (!list.getOwner().getUserId().equals(userId) && !hasEditPermission(userId, listId)) {
            throw new ResourceNotFoundException("User does not have permission to update this list");
        }
        shoppingListDTO.setId(listId);
        return shoppingListService.updateShoppingList(listId, shoppingListDTO);
    }

    public void deleteShoppingList(UUID userId, UUID listId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        ShoppingListEntity list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + listId));
        if (!list.getOwner().getUserId().equals(userId) && !hasEditPermission(userId, listId)) {
            throw new ResourceNotFoundException("User does not have permission to delete this list");
        }
        shoppingListService.softDeleteShoppingList(listId);
    }

    public ShoppingListItemDTO updateShoppingListItem(UUID userId, UUID listId, UUID itemId, ShoppingListItemDTO itemDTO) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        ShoppingListEntity list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + listId));
        if (!list.getOwner().getUserId().equals(userId) && !hasEditPermission(userId, listId)) {
            throw new ResourceNotFoundException("User does not have permission to update this item");
        }
        // Logic to update item (assuming a new method in ShoppingListService or direct repository access)
        ShoppingListItemEntity item = list.getItems().stream()
                .filter(i -> i.getProduct().getProductId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        item.setChecked(itemDTO.isChecked());
        item.setQuantity(itemDTO.getQuantity() != null ? BigDecimal.valueOf(itemDTO.getQuantity()) : item.getQuantity());
        shoppingListRepository.save(list); // Save to persist changes
        return convertToShoppingListItemDTO(item);
    }

    public void addCollaborator(UUID userId, UUID listId, CollaboratorDTO collaboratorDTO) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        ShoppingListEntity list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + listId));
        if (!list.getOwner().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Only the owner can add collaborators");
        }
        UsersEntity collaborator = usersRepository.findById(collaboratorDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found with id: " + collaboratorDTO.getUserId()));
        CollaboratorEntity collabEntity = new CollaboratorEntity();

        Optional<CollaboratorEntity> existingCollaborator = collaboratorRepository.findByShoppingList_IdAndUser_UserId(listId, collaboratorDTO.getUserId());
        if (existingCollaborator.isPresent()) {
            throw new IllegalStateException("User is already a collaborator on this list");
        }

        collabEntity.setShoppingList(list);
        collabEntity.setUser(collaborator);
        collabEntity.setPermission(PermissionEnum.valueOf(collaboratorDTO.getPermission()));
        collaboratorRepository.save(collabEntity);
    }

    public void removeCollaborator(UUID userId, UUID listId, UUID collaboratorId) {
        usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        ShoppingListEntity list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + listId));
        if (!list.getOwner().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Only the owner can remove collaborators");
        }
        CollaboratorEntity collaborator = collaboratorRepository.findByShoppingList_IdAndUser_UserId(listId, collaboratorId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found for user id: " + collaboratorId + " in list: " + listId));
        collaboratorRepository.delete(collaborator);
    }

    private boolean hasEditPermission(UUID userId, UUID listId) {
        return collaboratorRepository.findByShoppingList_IdAndUser_UserId(listId, userId)
                .stream().anyMatch(c -> c.getPermission() == PermissionEnum.EDIT);
    }

    private ShoppingListDTO convertToShoppingListDTO(ShoppingListEntity shoppingList) {
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
        dto.setItems(shoppingList.getItems().stream().map(this::convertToShoppingListItemDTO).toList());
        dto.setCollaborators(shoppingList.getCollaborators().stream().map(this::convertToCollaboratorDTO).toList());
        return dto;
    }

    private StoreDTO convertToStoreDTO(StoreEntity store) {
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getStoreId());
        dto.setName(store.getName());
        return dto;
    }

    private ProductDTO convertToProductDTO(ProductEntity product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getProductId());
        dto.setName(product.getName());
        return dto;
    }

    private ShoppingListItemDTO convertToShoppingListItemDTO(ShoppingListItemEntity item) {
        ShoppingListItemDTO dto = new ShoppingListItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity() != null ? item.getQuantity().doubleValue() : null);
        dto.setChecked(item.isChecked());
        dto.setStatus(item.getStatus());
        return dto;
    }

    private CollaboratorDTO convertToCollaboratorDTO(CollaboratorEntity collaborator) {
        CollaboratorDTO dto = new CollaboratorDTO();
        dto.setUserId(collaborator.getUser().getUserId());
        dto.setUserName(collaborator.getUser().getName());
        dto.setPermission(collaborator.getPermission() != null ? collaborator.getPermission().name() : "VIEW");
        return dto;
    }
}