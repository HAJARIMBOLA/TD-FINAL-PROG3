package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.MemberPayment;

public interface MemberPaymentRepository extends JpaRepository<MemberPayment, Long> {
}
