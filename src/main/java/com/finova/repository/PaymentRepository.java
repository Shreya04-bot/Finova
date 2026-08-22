package com.finova.repository;

import com.finova.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}