package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import com.agri.federation.model.Activite;
import com.agri.federation.repository.ActiviteRepository;

@Service
@RequiredArgsConstructor
public class ActiviteService {

    private final ActiviteRepository repo;

    public List<Activite> getAll() {
        return repo.findAll();
    }

    public Activite save(Activite a) {
        return repo.save(a);
    }
}