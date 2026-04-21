package com.agri.federation.service;

import com.agri.federation.repository.CotisationRepository;
import org.springframework.stereotype.Service;

@Service
public class CotisationService {

    private final CotisationRepository repo;

    public CotisationService(CotisationRepository repo) {
        this.repo = repo;
    }
}