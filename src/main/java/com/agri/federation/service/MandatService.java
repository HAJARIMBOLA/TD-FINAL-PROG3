package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.agri.federation.model.Mandat;
import com.agri.federation.repository.MandatRepository;

@Service
@RequiredArgsConstructor
public class MandatService {

    private final MandatRepository repo;

    public Mandat save(Mandat m) {
        return repo.save(m);
    }
}