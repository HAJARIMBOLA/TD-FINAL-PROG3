package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.CollectivityTransaction;
import java.time.LocalDate;
import java.util.List;

public interface CollectivityTransactionRepository extends JpaRepository<CollectivityTransaction, Long> {

    List<CollectivityTransaction> findByCollectiviteIdAndCreationDateBetween(
            Long collectiviteId,
            LocalDate from,
            LocalDate to
    );
}
