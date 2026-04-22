package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.Collectivite;
import java.util.Optional;

public interface CollectiviteRepository extends JpaRepository<Collectivite, Long> {

    // ✅ Fonctionnalité J : vérifier unicité avant attribution
    boolean existsByUniqueNumber(String uniqueNumber);
    boolean existsByUniqueName(String uniqueName);

    // Vérifier unicité en excluant la collectivité courante
    boolean existsByUniqueNumberAndIdNot(String uniqueNumber, Long id);
    boolean existsByUniqueNameAndIdNot(String uniqueName, Long id);
}