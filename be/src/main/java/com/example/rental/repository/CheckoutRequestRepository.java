package com.example.rental.repository;

import com.example.rental.entity.CheckoutRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckoutRequestRepository extends JpaRepository<CheckoutRequest, Long> {
    List<CheckoutRequest> findByTenantId(Long tenantId);
    List<CheckoutRequest> findByContractId(Long contractId);
}
