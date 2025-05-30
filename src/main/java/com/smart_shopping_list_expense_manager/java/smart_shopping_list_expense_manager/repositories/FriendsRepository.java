package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.FriendsEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendsRepository extends JpaRepository<FriendsEntity, UUID> {
     List<FriendsEntity> findByUser(UsersEntity user);
     List<FriendsEntity> findByFriend(UsersEntity friend);
     
     // Check if friendship exists in either direction
     @Query("SELECT f FROM FriendsEntity f WHERE " +
            "(f.user = :user AND f.friend = :friend) OR " +
            "(f.user = :friend AND f.friend = :user)")
     Optional<FriendsEntity> findFriendshipBetweenUsers(@Param("user") UsersEntity user, @Param("friend") UsersEntity friend);
     
     // Find specific friendship for deletion
     Optional<FriendsEntity> findByUserAndFriend(UsersEntity user, UsersEntity friend);
}