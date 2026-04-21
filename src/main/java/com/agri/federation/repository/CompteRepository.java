package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.Compte;

public interface CompteRepository extends JpaRepository<Compte, Long> {}