package com.agri.federation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agri.federation.model.MembershipFee;
import java.util.List;

public interface MembershipFeeRepository extends JpaRepository<MembershipFee, Long> {

    List<MembershipFee> findByCollectiviteId(Long collectiviteId);
}
