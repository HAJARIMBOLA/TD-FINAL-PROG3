package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import com.agri.federation.dto.CreateMembershipFeeRequest;
import com.agri.federation.exception.NotFoundException;
import com.agri.federation.model.ActivityStatus;
import com.agri.federation.model.Collectivite;
import com.agri.federation.model.MembershipFee;
import com.agri.federation.repository.CollectiviteRepository;
import com.agri.federation.repository.MembershipFeeRepository;

@Service
@RequiredArgsConstructor
public class MembershipFeeService {

    private final MembershipFeeRepository membershipFeeRepo;
    private final CollectiviteRepository collectiviteRepo;

    // GET /collectivities/{id}/membershipFees
    public List<MembershipFee> getByCollectivite(Long collectiviteId) {
        // 404 si collectivité introuvable
        if (!collectiviteRepo.existsById(collectiviteId)) {
            throw new NotFoundException("Collectivité introuvable avec l'ID : " + collectiviteId);
        }
        return membershipFeeRepo.findByCollectiviteId(collectiviteId);
    }

    // POST /collectivities/{id}/membershipFees
    public List<MembershipFee> createAll(Long collectiviteId, List<CreateMembershipFeeRequest> requests) {
        // 404 si collectivité introuvable
        Collectivite collectivite = collectiviteRepo.findById(collectiviteId)
                .orElseThrow(() -> new NotFoundException(
                        "Collectivité introuvable avec l'ID : " + collectiviteId
                ));

        return requests.stream()
                .map(req -> create(req, collectivite))
                .collect(Collectors.toList());
    }

    private MembershipFee create(CreateMembershipFeeRequest req, Collectivite collectivite) {
        // 400 : montant négatif
        if (req.getAmount() < 0) {
            throw new IllegalArgumentException(
                    "Le montant d'une cotisation ne peut pas être inférieur à 0."
            );
        }

        // 400 : fréquence manquante
        if (req.getFrequency() == null) {
            throw new IllegalArgumentException(
                    "La fréquence de la cotisation est invalide ou non reconnue."
            );
        }

        MembershipFee fee = new MembershipFee();
        fee.setEligibleFrom(req.getEligibleFrom());
        fee.setFrequency(req.getFrequency());
        fee.setAmount(req.getAmount());
        fee.setLabel(req.getLabel());
        fee.setStatus(ActivityStatus.ACTIVE);
        fee.setCollectivite(collectivite);

        return membershipFeeRepo.save(fee);
    }
}
