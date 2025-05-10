package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;


import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.OverviewItemDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ProductSearchLogRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Service
public class OverviewService {
    private final ProductSearchLogRepository searchRepo;
    private final UsersRepository userRepo;

    public OverviewService(ProductSearchLogRepository searchRepo,
                           UsersRepository userRepo) {
        this.searchRepo = searchRepo;
        this.userRepo   = userRepo;
    }

    public List<OverviewItemDTO> getOverview(String period) {
        // define time windows
        Instant now = Instant.now();
        Instant startCurrent, startPrevious, endPrevious = now;

        if ("lastWeek".equals(period)) {
            startCurrent  = now.minus(7, ChronoUnit.DAYS);
            startPrevious = now.minus(14, ChronoUnit.DAYS);
        } else { // default "today"
            LocalDate today = LocalDate.now();
            startCurrent  = today.atStartOfDay(ZoneOffset.UTC).toInstant();
            startPrevious = today.minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        }

        // fetch raw counts
        long viewsCurrent    = searchRepo.countByCreatedAtBetween(startCurrent, now);
        long viewsPrevious   = searchRepo.countByCreatedAtBetween(startPrevious, startCurrent);

        long searchesCurrent  = viewsCurrent;  // same as "views" here, or you could count distinct terms
        long searchesPrevious = viewsPrevious;

        long newUsersCurrent  = userRepo.countNewUsersBetween(startCurrent, now);
        long newUsersPrevious = userRepo.countNewUsersBetween(startPrevious, startCurrent);

        long activeCurrent    = userRepo.countActiveUsersAt(now);
        long activePrevious   = userRepo.countActiveUsersAt(startPrevious);

        // helper to format +/-% change
        Function<long[], String> fmtChange = arr -> {
            long curr = arr[0], prev = arr[1];
            if (prev == 0) return curr == 0 ? "0%" : "+100%";
            long pct = Math.round((curr - prev) * 100.0 / prev);
            return (pct >= 0 ? "+" : "") + pct + "%";
        };

        return List.of(
                new OverviewItemDTO("Views",        viewsCurrent,    fmtChange.apply(new long[]{viewsCurrent,    viewsPrevious})),
                new OverviewItemDTO("Searches",     searchesCurrent, fmtChange.apply(new long[]{searchesCurrent, searchesPrevious})),
                new OverviewItemDTO("New Users",    newUsersCurrent, fmtChange.apply(new long[]{newUsersCurrent, newUsersPrevious})),
                new OverviewItemDTO("Active Users", activeCurrent,   fmtChange.apply(new long[]{activeCurrent,   activePrevious}))
        );
    }
}

