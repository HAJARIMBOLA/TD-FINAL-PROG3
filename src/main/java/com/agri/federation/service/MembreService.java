package com.agri.federation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import com.agri.federation.model.Membre;
import com.agri.federation.repository.MembreRepository;

@Service
@RequiredArgsConstructor
public class MembreService {

    private final MembreRepository repo;

    public List<Membre> getAll() {
        return repo.findAll();
    }

    public Membre save(Membre m) {
        return repo.save(m);
    }
}