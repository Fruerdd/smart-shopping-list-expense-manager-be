package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.CityAllocationDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityAllocationService {

    private final UsersRepository usersRepo;

    public CityAllocationService(UsersRepository usersRepo) {
        this.usersRepo = usersRepo;
    }

    public CityAllocationDTO getCityAllocation() {
        List<Object[]> rows = usersRepo.countByCity();

        List<String> labels = rows.stream()
                .map(r -> (String) r[0])
                .collect(Collectors.toList());

        List<Long> data = rows.stream()
                .map(r -> ((Number) r[1]).longValue())
                .collect(Collectors.toList());

        return new CityAllocationDTO(labels, data);
    }
}
