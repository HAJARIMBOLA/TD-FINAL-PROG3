package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.agri.federation.dto.CreateMemberPaymentRequest;
import com.agri.federation.exception.NotFoundException;
import com.agri.federation.model.*;
import com.agri.federation.repository.*;

@Service
@RequiredArgsConstructor
public class MemberPaymentService {

    private final MemberPaymentRepository memberPaymentRepo;
    private final CollectivityTransactionRepository transactionRepo;
    private final MembreRepository membreRepo;
    private final MembershipFeeRepository membershipFeeRepo;
    private final CompteRepository compteRepo;

    // POST /members/{id}/payments
    @Transactional
    public List<MemberPayment> createAll(Long membreId, List<CreateMemberPaymentRequest> requests) {
        // 404 : membre introuvable
        Membre membre = membreRepo.findById(membreId)
                .orElseThrow(() -> new NotFoundException(
                        "Membre introuvable avec l'ID : " + membreId
                ));

        return requests.stream()
                .map(req -> createPayment(req, membre))
                .collect(Collectors.toList());
    }

    private MemberPayment createPayment(CreateMemberPaymentRequest req, Membre membre) {
        // 404 : cotisation introuvable
        MembershipFee fee = membershipFeeRepo.findById(req.getMembershipFeeIdentifier())
                .orElseThrow(() -> new NotFoundException(
                        "Cotisation introuvable avec l'ID : " + req.getMembershipFeeIdentifier()
                ));

        // 404 : compte introuvable
        Compte compte = compteRepo.findById(req.getAccountCreditedIdentifier())
                .orElseThrow(() -> new NotFoundException(
                        "Compte introuvable avec l'ID : " + req.getAccountCreditedIdentifier()
                ));

        // 400 : montant invalide
        if (req.getAmount() <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit être supérieur à 0.");
        }

        // 400 : mode de paiement manquant
        if (req.getPaymentMode() == null) {
            throw new IllegalArgumentException("Le mode de paiement est obligatoire.");
        }

        LocalDate today = LocalDate.now();

        // Créer le paiement du membre
        MemberPayment payment = new MemberPayment();
        payment.setAmount(req.getAmount());
        payment.setPaymentMode(req.getPaymentMode());
        payment.setAccountCredited(compte);
        payment.setCreationDate(today);
        payment.setMembre(membre);
        payment.setMembershipFee(fee);
        memberPaymentRepo.save(payment);

        // Générer automatiquement une transaction dans la collectivité du membre
        Collectivite collectivite = membre.getCollectivite();
        if (collectivite != null) {
            CollectivityTransaction transaction = new CollectivityTransaction();
            transaction.setCreationDate(today);
            transaction.setAmount(req.getAmount());
            transaction.setPaymentMode(req.getPaymentMode());
            transaction.setAccountCredited(compte);
            transaction.setMemberDebited(membre);
            transaction.setCollectivite(collectivite);
            transactionRepo.save(transaction);
        }

        return payment;
    }
}
