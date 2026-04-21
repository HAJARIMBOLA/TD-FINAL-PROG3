package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.Cotisation;

public interface CotisationRepository extends JpaRepository<Cotisation, Long> {}