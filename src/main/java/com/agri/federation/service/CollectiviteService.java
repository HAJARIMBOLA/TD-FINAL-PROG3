package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import com.agri.federation.model.Collectivite;
import com.agri.federation.repository.CollectiviteRepository;

@Service
@RequiredArgsConstructor
public class CollectiviteService {

    private final CollectiviteRepository repo;

    public List<Collectivite> getAll() {
        return repo.findAll();
    }

    public Collectivite create(Collectivite c) {

        if (c.getNom() == null || c.getVille() == null) {
            throw new RuntimeException("Données invalides");
        }

        return repo.save(c);
    }
}