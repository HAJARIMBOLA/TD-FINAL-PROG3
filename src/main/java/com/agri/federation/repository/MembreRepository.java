package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.Membre;
import com.agri.federation.model.MemberOccupation;
import java.util.List;

public interface MembreRepository extends JpaRepository<Membre, Long> {

    // Récupérer les membres d'une collectivité
    List<Membre> findByCollectiviteId(Long collectiviteId);

    // Vérifier qu'un membre est SENIOR (membre confirmé) — règle B-2
    List<Membre> findByIdInAndOccupation(List<Long> ids, MemberOccupation occupation);
}