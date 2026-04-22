package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.agri.federation.dto.CreateMemberRequest;
import com.agri.federation.exception.NotFoundException;
import com.agri.federation.model.Collectivite;
import com.agri.federation.model.Membre;
import com.agri.federation.model.MemberOccupation;
import com.agri.federation.repository.CollectiviteRepository;
import com.agri.federation.repository.MembreRepository;

@Service
@RequiredArgsConstructor
public class MembreService {

    private final MembreRepository membreRepo;
    private final CollectiviteRepository collectiviteRepo;

    public List<Membre> getAll() {
        return membreRepo.findAll();
    }

    // ✅ POST /members — fonctionnalité B-2
    public List<Membre> createAll(List<CreateMemberRequest> requests) {
        return requests.stream()
                .map(this::admettreNouveauMembre)
                .collect(Collectors.toList());
    }

    private Membre admettreNouveauMembre(CreateMemberRequest req) {

        // 400 : frais d'adhésion non payés
        if (!req.isRegistrationFeePaid()) {
            throw new IllegalArgumentException(
                    "Les frais d'adhésion de 50 000 MGA doivent être réglés."
            );
        }

        // 400 : cotisations annuelles non payées
        if (!req.isMembershipDuesPaid()) {
            throw new IllegalArgumentException(
                    "L'intégralité des cotisations annuelles obligatoires doit être réglée."
            );
        }

        // 404 : collectivité cible introuvable
        Collectivite collectivite = collectiviteRepo.findById(req.getCollectivityIdentifier())
                .orElseThrow(() -> new NotFoundException(
                        "Collectivité introuvable avec l'ID : " + req.getCollectivityIdentifier()
                ));

        // 404 : parrains introuvables
        if (req.getReferees() == null || req.getReferees().size() < 2) {
            throw new IllegalArgumentException(
                    "Au moins 2 parrains membres confirmés sont requis."
            );
        }

        List<Membre> parrains = membreRepo.findAllById(req.getReferees());
        if (parrains.size() != req.getReferees().size()) {
            throw new NotFoundException("Un ou plusieurs parrains sont introuvables.");
        }

        // Règle B-2 : tous les parrains doivent être SENIOR (membres confirmés)
        boolean tousConfirmes = parrains.stream()
                .allMatch(p -> p.getOccupation() == MemberOccupation.SENIOR
                        || p.getOccupation() == MemberOccupation.SECRETARY
                        || p.getOccupation() == MemberOccupation.TREASURER
                        || p.getOccupation() == MemberOccupation.VICE_PRESIDENT
                        || p.getOccupation() == MemberOccupation.PRESIDENT);

        if (!tousConfirmes) {
            throw new IllegalArgumentException(
                    "Tous les parrains doivent être des membres confirmés."
            );
        }

        // Règle B-2 : parrains de la collectivité cible >= parrains des autres collectivités
        long parrainsDansCollectivite = parrains.stream()
                .filter(p -> p.getCollectivite() != null
                        && p.getCollectivite().getId().equals(collectivite.getId()))
                .count();

        long parrainsDansAutres = parrains.size() - parrainsDansCollectivite;

        if (parrainsDansCollectivite < parrainsDansAutres) {
            throw new IllegalArgumentException(
                    "Le nombre de parrains issus de la collectivité cible doit être "
                            + "supérieur ou égal au nombre de parrains des autres collectivités."
            );
        }

        // Construction du membre
        Membre membre = new Membre();
        membre.setFirstName(req.getFirstName());
        membre.setLastName(req.getLastName());
        membre.setBirthDate(req.getBirthDate());
        membre.setGender(req.getGender());
        membre.setAddress(req.getAddress());
        membre.setProfession(req.getProfession());
        membre.setPhoneNumber(req.getPhoneNumber());
        membre.setEmail(req.getEmail());
        membre.setOccupation(MemberOccupation.JUNIOR); // tout nouveau membre commence JUNIOR
        membre.setDateAdhesion(LocalDate.now());
        membre.setCollectivite(collectivite);
        membre.setReferees(parrains);

        return membreRepo.save(membre);
    }
}