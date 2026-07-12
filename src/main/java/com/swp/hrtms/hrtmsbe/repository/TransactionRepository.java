package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findFirstByWallet_User_IdAndRace_IdAndTypeOrderByCreatedAtDesc(
            Integer userId,
            Integer raceId,
            String type);
}
