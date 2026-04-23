package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

import com.agri.federation.exception.NotFoundException;
import com.agri.federation.model.CollectivityTransaction;
import com.agri.federation.repository.CollectiviteRepository;
import com.agri.federation.repository.CollectivityTransactionRepository;

@Service
@RequiredArgsConstructor
public class CollectivityTransactionService {

    private final CollectivityTransactionRepository transactionRepo;
    private final CollectiviteRepository collectiviteRepo;

    // GET /collectivities/{id}/transactions?from=...&to=...
    public List<CollectivityTransaction> getByPeriod(Long collectiviteId, LocalDate from, LocalDate to) {
        // 400 : paramètres invalides
        if (from == null || to == null) {
            throw new IllegalArgumentException("Les paramètres 'from' et 'to' sont obligatoires.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("La date 'from' ne peut pas être postérieure à la date 'to'.");
        }

        // 404 : collectivité introuvable
        if (!collectiviteRepo.existsById(collectiviteId)) {
            throw new NotFoundException("Collectivité introuvable avec l'ID : " + collectiviteId);
        }

        return transactionRepo.findByCollectiviteIdAndCreationDateBetween(collectiviteId, from, to);
    }
}
