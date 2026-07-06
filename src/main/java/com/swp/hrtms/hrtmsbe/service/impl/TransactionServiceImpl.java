package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.TransactionRequest;
import com.swp.hrtms.hrtmsbe.dto.response.TransactionResponse;
import com.swp.hrtms.hrtmsbe.entity.Transaction;
import com.swp.hrtms.hrtmsbe.repository.*;
import com.swp.hrtms.hrtmsbe.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TournamentRepository tournamentRepository;
    private final RaceRepository raceRepository;
    private final HorseRepository horseRepository;

    @Override
    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .wallet(request.getWalletId() != null ? walletRepository.getReferenceById(request.getWalletId()) : null)
                .tournament(request.getTournamentId() != null ? tournamentRepository.getReferenceById(request.getTournamentId()) : null)
                .race(request.getRaceId() != null ? raceRepository.getReferenceById(request.getRaceId()) : null)
                .horse(request.getHorseId() != null ? horseRepository.getReferenceById(request.getHorseId()) : null)
                .amount(request.getAmount())
                .type(request.getType())
                .createdAt(LocalDateTime.now()) // server tự set timestamp
                .build();
        transaction = transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll() {
        return transactionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getById(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        return toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse update(Integer id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        // Partial update — chỉ set nếu request không null
        if (request.getWalletId() != null)
            transaction.setWallet(walletRepository.getReferenceById(request.getWalletId()));
        if (request.getTournamentId() != null)
            transaction.setTournament(tournamentRepository.getReferenceById(request.getTournamentId()));
        if (request.getRaceId() != null)
            transaction.setRace(raceRepository.getReferenceById(request.getRaceId()));
        if (request.getHorseId() != null)
            transaction.setHorse(horseRepository.getReferenceById(request.getHorseId()));
        if (request.getAmount() != null)
            transaction.setAmount(request.getAmount());
        if (request.getType() != null)
            transaction.setType(request.getType());
        // createdAt giữ nguyên, không cập nhật lại
        transaction = transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // Transaction không có status field → hard delete
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------
    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .walletId(transaction.getWallet() != null ? transaction.getWallet().getId() : null)
                .tournamentId(transaction.getTournament() != null ? transaction.getTournament().getId() : null)
                .raceId(transaction.getRace() != null ? transaction.getRace().getId() : null)
                .horseId(transaction.getHorse() != null ? transaction.getHorse().getId() : null)
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}



