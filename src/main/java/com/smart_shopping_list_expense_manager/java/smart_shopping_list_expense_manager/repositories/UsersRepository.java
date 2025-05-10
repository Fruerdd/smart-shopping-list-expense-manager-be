package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<UsersEntity, UUID> {

    @Query("SELECT u.location, COUNT(u) FROM UsersEntity u GROUP BY u.location")
    List<Object[]> countByCity();
}
