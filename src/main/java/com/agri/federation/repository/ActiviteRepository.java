package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.Activite;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {}