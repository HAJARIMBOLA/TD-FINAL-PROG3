package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.Membre;

public interface MembreRepository extends JpaRepository<Membre, Long> {}