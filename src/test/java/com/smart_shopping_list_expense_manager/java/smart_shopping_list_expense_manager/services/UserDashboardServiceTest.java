package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.LoyaltyTierEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.PermissionEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.exceptions.ResourceNotFoundException;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserDashboardServiceTest {

    @Mock private UsersRepository usersRepository;
    @Mock private ShoppingListService shoppingListService;
    @Mock private FavoriteProductRepository favoriteProductRepository;
    @Mock private FavoriteStoreRepository favoriteStoreRepository;
    @Mock private ProductRepository productRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private StorePriceRepository storePriceRepository;
    @Mock private ShoppingListRepository shoppingListRepository;
    @Mock private CollaboratorRepository collaboratorRepository;

    @InjectMocks
    private UserDashboardService userDashboardService;

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final UUID storeId = UUID.randomUUID();
    private final UUID listId = UUID.randomUUID();
    private final UUID itemId = UUID.randomUUID();
    private final UUID collaboratorId = UUID.randomUUID();

    @Test
    void testGetUserById_Success() {
        UsersEntity user = createMockUser();
        List<ShoppingListDTO> shoppingLists = Arrays.asList(createMockShoppingListDTO());
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListService.getShoppingListsByUserId(userId)).thenReturn(shoppingLists);

        UserDTO result = userDashboardService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals(100, result.getBonus_points());
        assertEquals(shoppingLists, result.getShoppingLists());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userDashboardService.getUserById(userId)
        );
        assertEquals("User not found with id: " + userId, exception.getMessage());
    }

    @Test
    void testGetFavoriteProducts_Success() {
        UsersEntity user = createMockUser();
        FavoriteProductEntity favoriteProduct = createMockFavoriteProduct();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteProductRepository.findAll()).thenReturn(Arrays.asList(favoriteProduct));

        List<FavoriteProductDTO> result = userDashboardService.getFavoriteProducts(userId);

        assertEquals(1, result.size());
        FavoriteProductDTO dto = result.get(0);
        assertEquals(favoriteProduct.getId(), dto.getId());
        assertEquals(productId, dto.getProductId());
        assertEquals("Test Product", dto.getProductName());
    }

    @Test
    void testGetFavoriteProducts_UserNotFound() {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> userDashboardService.getFavoriteProducts(userId));
    }

    @Test
    void testAddFavoriteProduct_Success() {
        UsersEntity user = createMockUser();
        ProductEntity product = createMockProduct();
        FavoriteProductEntity favoriteProduct = createMockFavoriteProduct();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.empty());
        when(favoriteProductRepository.save(any(FavoriteProductEntity.class)))
                .thenReturn(favoriteProduct);

        FavoriteProductDTO result = userDashboardService.addFavoriteProduct(userId, productId);

        assertNotNull(result);
        assertEquals(favoriteProduct.getId(), result.getId());
        assertEquals(productId, result.getProductId());
        assertEquals("Test Product", result.getProductName());
    }

    @Test
    void testAddFavoriteProduct_AlreadyExists() {
        UsersEntity user = createMockUser();
        ProductEntity product = createMockProduct();
        FavoriteProductEntity existingFavorite = createMockFavoriteProduct();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.of(existingFavorite));

        FavoriteProductDTO result = userDashboardService.addFavoriteProduct(userId, productId);

        assertNotNull(result);
        assertEquals(existingFavorite.getId(), result.getId());
        verify(favoriteProductRepository, never()).save(any());
    }

    @Test
    void testAddFavoriteProduct_UserNotFound() {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.addFavoriteProduct(userId, productId));
    }

    @Test
    void testAddFavoriteProduct_ProductNotFound() {
        UsersEntity user = createMockUser();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.addFavoriteProduct(userId, productId));
    }

    @Test
    void testRemoveFavoriteProduct_Success() {
        UsersEntity user = createMockUser();
        FavoriteProductEntity favoriteProduct = createMockFavoriteProduct();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.of(favoriteProduct));

        userDashboardService.removeFavoriteProduct(userId, productId);

        verify(favoriteProductRepository).delete(favoriteProduct);
    }

    @Test
    void testRemoveFavoriteProduct_UserNotFound() {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.removeFavoriteProduct(userId, productId));
    }

    @Test
    void testRemoveFavoriteProduct_FavoriteNotFound() {
        UsersEntity user = createMockUser();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteProductRepository.findByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.removeFavoriteProduct(userId, productId));
    }

    @Test
    void testGetFavoriteStores_Success() {
        UsersEntity user = createMockUser();
        FavoriteStoreEntity favoriteStore = createMockFavoriteStore();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteStoreRepository.findAll()).thenReturn(Arrays.asList(favoriteStore));

        List<FavoriteStoreDTO> result = userDashboardService.getFavoriteStores(userId);

        assertEquals(1, result.size());
        FavoriteStoreDTO dto = result.get(0);
        assertEquals(favoriteStore.getId(), dto.getId());
        assertEquals(storeId, dto.getStoreId());
        assertEquals("Test Store", dto.getStoreName());
    }

    @Test
    void testGetFavoriteStores_UserNotFound() {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> userDashboardService.getFavoriteStores(userId));
    }

    @Test
    void testAddFavoriteStore_Success() {
        UsersEntity user = createMockUser();
        StoreEntity store = createMockStore();
        FavoriteStoreEntity favoriteStore = createMockFavoriteStore();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(favoriteStoreRepository.findByUserIdAndStoreId(userId, storeId))
                .thenReturn(Optional.empty());
        when(favoriteStoreRepository.save(any(FavoriteStoreEntity.class)))
                .thenReturn(favoriteStore);

        FavoriteStoreDTO result = userDashboardService.addFavoriteStore(userId, storeId);

        assertNotNull(result);
        assertEquals(favoriteStore.getId(), result.getId());
        assertEquals(storeId, result.getStoreId());
        assertEquals("Test Store", result.getStoreName());
    }

    @Test
    void testAddFavoriteStore_AlreadyExists() {
        UsersEntity user = createMockUser();
        StoreEntity store = createMockStore();
        FavoriteStoreEntity existingFavorite = createMockFavoriteStore();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(favoriteStoreRepository.findByUserIdAndStoreId(userId, storeId))
                .thenReturn(Optional.of(existingFavorite));

        FavoriteStoreDTO result = userDashboardService.addFavoriteStore(userId, storeId);

        assertNotNull(result);
        assertEquals(existingFavorite.getId(), result.getId());
        verify(favoriteStoreRepository, never()).save(any());
    }

    @Test
    void testAddFavoriteStore_UserNotFound() {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.addFavoriteStore(userId, storeId));
    }

    @Test
    void testAddFavoriteStore_StoreNotFound() {
        UsersEntity user = createMockUser();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.addFavoriteStore(userId, storeId));
    }

    @Test
    void testRemoveFavoriteStore_Success() {
        UsersEntity user = createMockUser();
        FavoriteStoreEntity favoriteStore = createMockFavoriteStore();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteStoreRepository.findByUserIdAndStoreId(userId, storeId))
                .thenReturn(Optional.of(favoriteStore));

        userDashboardService.removeFavoriteStore(userId, storeId);

        verify(favoriteStoreRepository).delete(favoriteStore);
    }

    @Test
    void testRemoveFavoriteStore_UserNotFound() {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.removeFavoriteStore(userId, storeId));
    }

    @Test
    void testRemoveFavoriteStore_FavoriteNotFound() {
        UsersEntity user = createMockUser();
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteStoreRepository.findByUserIdAndStoreId(userId, storeId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.removeFavoriteStore(userId, storeId));
    }

    @Test
    void testSearchStores_Success() {
        UsersEntity user = createMockUser();
        StoreEntity store = createMockStore();
        String query = "test";
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteStoreRepository.findAll()).thenReturn(Collections.emptyList());
        when(storeRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Arrays.asList(store));

        List<StoreDTO> result = userDashboardService.searchStores(userId, query);

        assertEquals(1, result.size());
        assertEquals(storeId, result.get(0).getId());
        assertEquals("Test Store", result.get(0).getName());
    }

    @Test
    void testSearchStores_ExcludesFavorites() {
        UsersEntity user = createMockUser();
        StoreEntity favoriteStore = createMockStore();
        FavoriteStoreEntity favorite = createMockFavoriteStore();
        String query = "test";
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteStoreRepository.findAll()).thenReturn(Arrays.asList(favorite));
        when(storeRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Arrays.asList(favoriteStore));

        List<StoreDTO> result = userDashboardService.searchStores(userId, query);

        assertEquals(0, result.size());
    }

    @Test
    void testSearchProducts_Success() {
        UsersEntity user = createMockUser();
        ProductEntity product = createMockProduct();
        String query = "test";
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteProductRepository.findAll()).thenReturn(Collections.emptyList());
        when(productRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Arrays.asList(product));

        List<ProductDTO> result = userDashboardService.searchProducts(userId, query);

        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getId());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testSearchProducts_ExcludesFavorites() {
        UsersEntity user = createMockUser();
        ProductEntity favoriteProduct = createMockProduct();
        FavoriteProductEntity favorite = createMockFavoriteProduct();
        String query = "test";
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(favoriteProductRepository.findAll()).thenReturn(Arrays.asList(favorite));
        when(productRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Arrays.asList(favoriteProduct));

        List<ProductDTO> result = userDashboardService.searchProducts(userId, query);

        assertEquals(0, result.size());
    }

    @Test
    void testGetAllProducts_Success() {
        UsersEntity user = createMockUser();
        ProductEntity product = createMockProduct();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<ProductDTO> result = userDashboardService.getAllProducts(userId);

        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getId());
        assertEquals("Test Product", result.get(0).getName());
    }

    @Test
    void testGetAllStores_Success() {
        UsersEntity user = createMockUser();
        StoreEntity store = createMockStore();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findAll()).thenReturn(Arrays.asList(store));

        List<StoreDTO> result = userDashboardService.getAllStores(userId);

        assertEquals(1, result.size());
        assertEquals(storeId, result.get(0).getId());
        assertEquals("Test Store", result.get(0).getName());
    }

    @Test
    void testGetProductPrices_Success() {
        UsersEntity user = createMockUser();
        ProductEntity product = createMockProduct();
        StorePriceEntity storePrice = createMockStorePrice();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(storePriceRepository.findByProduct_ProductId(productId))
                .thenReturn(Arrays.asList(storePrice));

        List<StorePriceDTO> result = userDashboardService.getProductPrices(userId, productId);

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(10.99), result.get(0).getPrice());
        assertEquals("Test Store", result.get(0).getStoreName());
    }

    @Test
    void testGetAllShoppingLists_Success() {
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByOwner_UserId(userId)).thenReturn(Arrays.asList(shoppingList));
        when(shoppingListRepository.findByCollaborators_User_UserId(userId)).thenReturn(Collections.emptyList());

        List<ShoppingListDTO> result = userDashboardService.getAllShoppingLists(userId);

        assertEquals(1, result.size());
        assertEquals(listId, result.get(0).getId());
        assertEquals("Test List", result.get(0).getName());
    }

    @Test
    void testCreateShoppingList_Success() {
        UsersEntity user = createMockUser();
        ShoppingListDTO dto = createMockShoppingListDTO();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListService.createShoppingList(any(ShoppingListDTO.class))).thenReturn(dto);

        ShoppingListDTO result = userDashboardService.createShoppingList(userId, dto);

        assertNotNull(result);
        assertEquals(listId, result.getId());
        assertEquals("Test List", result.getName());
    }

    @Test
    void testUpdateShoppingList_Success() {
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        ShoppingListDTO dto = createMockShoppingListDTO();
        dto.setName("Updated List");
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(shoppingListService.updateShoppingList(any(UUID.class), any(ShoppingListDTO.class))).thenReturn(dto);

        ShoppingListDTO result = userDashboardService.updateShoppingList(userId, listId, dto);

        assertNotNull(result);
        verify(shoppingListService).updateShoppingList(any(UUID.class), any(ShoppingListDTO.class));
    }

    @Test
    void testUpdateShoppingList_ListNotFound() {
        UsersEntity user = createMockUser();
        ShoppingListDTO dto = createMockShoppingListDTO();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.updateShoppingList(userId, listId, dto));
    }

    @Test
    void testDeleteShoppingList_Success() {
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));

        userDashboardService.deleteShoppingList(userId, listId);

        verify(shoppingListService).softDeleteShoppingList(listId);
    }

    @Test
    void testDeleteShoppingList_ListNotFound() {
        UsersEntity user = createMockUser();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.deleteShoppingList(userId, listId));
    }

    @Test
    void testUpdateShoppingListItem_Success() {
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        ShoppingListItemEntity item = createMockShoppingListItem();
        shoppingList.setItems(Arrays.asList(item));
        ShoppingListItemDTO dto = createMockShoppingListItemDTO();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any(ShoppingListEntity.class))).thenReturn(shoppingList);

        ShoppingListItemDTO result = userDashboardService.updateShoppingListItem(userId, listId, itemId, dto);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
    }

    @Test
    void testUpdateShoppingListItem_ItemNotFound() {
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        shoppingList.setItems(Collections.emptyList());
        ShoppingListItemDTO dto = createMockShoppingListItemDTO();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.updateShoppingListItem(userId, listId, itemId, dto));
    }

    @Test
    void testAddCollaborator_Success() {
        UsersEntity user = createMockUser();
        UsersEntity collaboratorUser = createMockCollaboratorUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        CollaboratorDTO dto = createMockCollaboratorDTO();
        dto.setUserId(collaboratorUser.getUserId());
        CollaboratorEntity collaborator = createMockCollaborator();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(usersRepository.findById(dto.getUserId())).thenReturn(Optional.of(collaboratorUser));
        when(collaboratorRepository.findByShoppingList_IdAndUser_UserId(listId, collaboratorUser.getUserId()))
                .thenReturn(Optional.empty());
        when(collaboratorRepository.save(any(CollaboratorEntity.class))).thenReturn(collaborator);

        userDashboardService.addCollaborator(userId, listId, dto);

        verify(collaboratorRepository).save(any(CollaboratorEntity.class));
    }

    @Test
    void testAddCollaborator_UserNotFound() {
        CollaboratorDTO dto = createMockCollaboratorDTO();
        dto.setUserId(UUID.randomUUID());
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(usersRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.addCollaborator(userId, listId, dto));
    }

    @Test
    void testAddCollaborator_AlreadyExists() {
        UsersEntity user = createMockUser();
        UsersEntity collaboratorUser = createMockCollaboratorUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        CollaboratorDTO dto = createMockCollaboratorDTO();
        dto.setUserId(collaboratorUser.getUserId());
        CollaboratorEntity existingCollaborator = createMockCollaborator();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(usersRepository.findById(dto.getUserId())).thenReturn(Optional.of(collaboratorUser));
        when(collaboratorRepository.findByShoppingList_IdAndUser_UserId(listId, collaboratorUser.getUserId()))
                .thenReturn(Optional.of(existingCollaborator));

        assertThrows(IllegalStateException.class,
                () -> userDashboardService.addCollaborator(userId, listId, dto));
    }

    @Test
    void testRemoveCollaborator_Success() {
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        CollaboratorEntity collaborator = createMockCollaborator();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(collaboratorRepository.findByShoppingList_IdAndUser_UserId(listId, collaboratorId))
                .thenReturn(Optional.of(collaborator));

        userDashboardService.removeCollaborator(userId, listId, collaboratorId);

        verify(collaboratorRepository).delete(collaborator);
    }

    @Test
    void testRemoveCollaborator_NotFound() {
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(collaboratorRepository.findByShoppingList_IdAndUser_UserId(listId, collaboratorId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userDashboardService.removeCollaborator(userId, listId, collaboratorId));
    }

    @Test
    void testUpdateCollaborator_Success() {
        UsersEntity user = createMockUser();
        ShoppingListEntity shoppingList = createMockShoppingList();
        CollaboratorEntity collaborator = createMockCollaborator();
        CollaboratorDTO dto = createMockCollaboratorDTO();
        dto.setPermission("VIEW");
        
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findById(listId)).thenReturn(Optional.of(shoppingList));
        when(collaboratorRepository.findByShoppingList_IdAndUser_UserId(listId, collaboratorId))
                .thenReturn(Optional.of(collaborator));
        when(collaboratorRepository.save(any(CollaboratorEntity.class))).thenReturn(collaborator);

        userDashboardService.updateCollaborator(userId, listId, collaboratorId, dto);

        verify(collaboratorRepository).save(collaborator);
        assertEquals(PermissionEnum.VIEW, collaborator.getPermission());
    }

    @Test
    void testGetUserLoyaltyTier_Bronze() {
        UsersEntity user = createMockUser();
        user.setBonusPoints(50);
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        LoyaltyTierEnum result = userDashboardService.getUserLoyaltyTier(userId);

        assertEquals(LoyaltyTierEnum.BRONZE, result);
    }

    @Test
    void testGetUserLoyaltyTier_Silver() {
        UsersEntity user = createMockUser();
        user.setBonusPoints(150);
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        LoyaltyTierEnum result = userDashboardService.getUserLoyaltyTier(userId);

        assertEquals(LoyaltyTierEnum.SILVER, result);
    }

    @Test
    void testGetUserLoyaltyTier_Gold() {
        UsersEntity user = createMockUser();
        user.setBonusPoints(500);
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        LoyaltyTierEnum result = userDashboardService.getUserLoyaltyTier(userId);

        assertEquals(LoyaltyTierEnum.GOLD, result);
    }

    private UsersEntity createMockUser() {
        UsersEntity user = new UsersEntity();
        user.setUserId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setBonusPoints(100);
        return user;
    }

    private UsersEntity createMockCollaboratorUser() {
        UsersEntity user = new UsersEntity();
        user.setUserId(UUID.randomUUID());
        user.setEmail("collaborator@example.com");
        user.setName("Collaborator User");
        return user;
    }

    private ProductEntity createMockProduct() {
        ProductEntity product = new ProductEntity();
        product.setProductId(productId);
        product.setName("Test Product");
        product.setDescription("Test Description");
        return product;
    }

    private StoreEntity createMockStore() {
        StoreEntity store = new StoreEntity();
        store.setStoreId(storeId);
        store.setName("Test Store");
        return store;
    }

    private FavoriteProductEntity createMockFavoriteProduct() {
        FavoriteProductEntity favorite = new FavoriteProductEntity();
        favorite.setId(UUID.randomUUID());
        favorite.setUser(createMockUser());
        favorite.setProduct(createMockProduct());
        return favorite;
    }

    private FavoriteStoreEntity createMockFavoriteStore() {
        FavoriteStoreEntity favorite = new FavoriteStoreEntity();
        favorite.setId(UUID.randomUUID());
        favorite.setUser(createMockUser());
        favorite.setStore(createMockStore());
        return favorite;
    }

    private StorePriceEntity createMockStorePrice() {
        StorePriceEntity storePrice = new StorePriceEntity();
        storePrice.setProduct(createMockProduct());
        storePrice.setStore(createMockStore());
        storePrice.setPrice(BigDecimal.valueOf(10.99));
        return storePrice;
    }

    private ShoppingListEntity createMockShoppingList() {
        ShoppingListEntity shoppingList = new ShoppingListEntity();
        shoppingList.setId(listId);
        shoppingList.setName("Test List");
        shoppingList.setOwner(createMockUser());
        shoppingList.setStore(createMockStore());
        shoppingList.setCreatedAt(Instant.now());
        shoppingList.setItems(new ArrayList<>());
        shoppingList.setCollaborators(new ArrayList<>());
        return shoppingList;
    }

    private ShoppingListItemEntity createMockShoppingListItem() {
        ShoppingListItemEntity item = new ShoppingListItemEntity();
        item.setId(itemId);
        item.setProduct(createMockProduct());
        item.setQuantity(BigDecimal.valueOf(2));
        item.setChecked(false);
        return item;
    }

    private CollaboratorEntity createMockCollaborator() {
        CollaboratorEntity collaborator = new CollaboratorEntity();
        collaborator.setId(collaboratorId);
        collaborator.setUser(createMockCollaboratorUser());
        collaborator.setShoppingList(createMockShoppingList());
        collaborator.setPermission(PermissionEnum.EDIT);
        return collaborator;
    }

    private ShoppingListDTO createMockShoppingListDTO() {
        ShoppingListDTO dto = new ShoppingListDTO();
        dto.setId(listId);
        dto.setName("Test List");
        dto.setItems(new ArrayList<>());
        dto.setCollaborators(new ArrayList<>());
        return dto;
    }

    private ShoppingListItemDTO createMockShoppingListItemDTO() {
        ShoppingListItemDTO dto = new ShoppingListItemDTO();
        dto.setId(itemId);
        dto.setProductId(productId);
        dto.setProductName("Test Product");
        dto.setQuantity(2.0);
        dto.setIsChecked(false);
        return dto;
    }

    private CollaboratorDTO createMockCollaboratorDTO() {
        CollaboratorDTO dto = new CollaboratorDTO();
        dto.setUserId(collaboratorId);
        dto.setUserName("Collaborator User");
        dto.setPermission("EDIT");
        return dto;
    }
} 