package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

import com.agri.federation.model.CollectivityTransaction;
import com.agri.federation.service.CollectivityTransactionService;

@RestController
@RequiredArgsConstructor
public class CollectivityTransactionController {

    private final CollectivityTransactionService service;

    // GET /collectivities/{id}/transactions?from=...&to=...
    @GetMapping("/collectivities/{id}/transactions")
    public List<CollectivityTransaction> getTransactions(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getByPeriod(id, from, to);
    }
}
