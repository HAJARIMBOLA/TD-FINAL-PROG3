package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.Mandat;

public interface MandatRepository extends JpaRepository<Mandat, Long> {}