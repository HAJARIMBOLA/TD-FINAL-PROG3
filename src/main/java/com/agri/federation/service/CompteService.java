package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.agri.federation.model.Compte;
import com.agri.federation.repository.CompteRepository;

@Service
@RequiredArgsConstructor
public class CompteService {

    private final CompteRepository repo;

    public Compte save(Compte c) {
        return repo.save(c);
    }
}