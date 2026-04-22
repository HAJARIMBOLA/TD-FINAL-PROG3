package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.agri.federation.dto.AssignFederationAttributesRequest;
import com.agri.federation.exception.ConflictException;
import com.agri.federation.exception.NotFoundException;
import com.agri.federation.model.Collectivite;
import com.agri.federation.model.Membre;
import com.agri.federation.repository.CollectiviteRepository;
import com.agri.federation.repository.MembreRepository;

@Service
@RequiredArgsConstructor
public class CollectiviteService {

    private final CollectiviteRepository collectiviteRepo;
    private final MembreRepository membreRepo;

    public List<Collectivite> getAll() {
        return collectiviteRepo.findAll();
    }

    // ✅ POST /collectivities — fonctionnalité A
    public List<Collectivite> createAll(List<Collectivite> collectivites) {
        return collectivites.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    private Collectivite create(Collectivite c) {

        // Règle A : autorisation formelle obligatoire
        if (!c.isFederationApproval()) {
            throw new IllegalArgumentException(
                    "La collectivité doit avoir l'autorisation formelle de la fédération."
            );
        }

        // Règle A : structure complète obligatoire
        if (c.getPresident() == null || c.getVicePresident() == null
                || c.getTresorier() == null || c.getSecretaire() == null) {
            throw new IllegalArgumentException(
                    "La structure de la collectivité doit être complète (président, vice-président, trésorier, secrétaire)."
            );
        }

        // Règle A : au moins 10 membres
        if (c.getMembres() == null || c.getMembres().size() < 10) {
            throw new IllegalArgumentException(
                    "La collectivité doit avoir au moins 10 membres."
            );
        }

        // Règle A : au moins 5 membres avec +6 mois d'ancienneté
        LocalDate seuilSixMois = LocalDate.now().minusMonths(6);
        long membresAnciens = c.getMembres().stream()
                .filter(m -> m.getDateAdhesion() != null
                        && m.getDateAdhesion().isBefore(seuilSixMois))
                .count();

        if (membresAnciens < 5) {
            throw new IllegalArgumentException(
                    "Au moins 5 membres doivent avoir une ancienneté de 6 mois dans la fédération."
            );
        }

        c.setDateCreation(LocalDate.now());
        return collectiviteRepo.save(c);
    }

    // ✅ PUT /collectivities/{id}/federation-attributes — fonctionnalité J
    public Collectivite assignFederationAttributes(Long id, AssignFederationAttributesRequest request) {

        // Validation 400 : champs vides
        if (request.getUniqueNumber() == null || request.getUniqueNumber().isBlank()) {
            throw new IllegalArgumentException("Le uniqueNumber ne peut pas être vide.");
        }
        if (request.getUniqueName() == null || request.getUniqueName().isBlank()) {
            throw new IllegalArgumentException("Le uniqueName ne peut pas être vide.");
        }

        // 404 : collectivité introuvable
        Collectivite collectivite = collectiviteRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Aucune collectivité trouvée avec l'ID : " + id
                ));

        // 409 : déjà attribués → ne peuvent plus être modifiés
        if (collectivite.getUniqueNumber() != null || collectivite.getUniqueName() != null) {
            throw new ConflictException(
                    "Le numéro et le nom ont déjà été attribués à cette collectivité et ne peuvent plus être modifiés.",
                    "alreadyAssigned"
            );
        }

        // 409 : uniqueNumber déjà utilisé par une autre collectivité
        if (collectiviteRepo.existsByUniqueNumberAndIdNot(request.getUniqueNumber(), id)) {
            throw new ConflictException(
                    "Le uniqueNumber '" + request.getUniqueNumber() + "' est déjà utilisé par une autre collectivité.",
                    "uniqueNumber"
            );
        }

        // 409 : uniqueName déjà utilisé par une autre collectivité
        if (collectiviteRepo.existsByUniqueNameAndIdNot(request.getUniqueName(), id)) {
            throw new ConflictException(
                    "Le uniqueName '" + request.getUniqueName() + "' est déjà utilisé par une autre collectivité.",
                    "uniqueName"
            );
        }

        collectivite.setUniqueNumber(request.getUniqueNumber());
        collectivite.setUniqueName(request.getUniqueName());

        return collectiviteRepo.save(collectivite);
    }
}