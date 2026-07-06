package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.WalletRequest;
import com.swp.hrtms.hrtmsbe.dto.response.WalletResponse;
import com.swp.hrtms.hrtmsbe.entity.Wallet;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.repository.WalletRepository;
import com.swp.hrtms.hrtmsbe.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WalletResponse create(WalletRequest request) {
        Wallet wallet = Wallet.builder()
                .user(userRepository.getReferenceById(request.getUserId()))
                .balance(request.getBalance() != null ? request.getBalance() : 0)
                .updatedAt(LocalDateTime.now())
                .build();
        wallet = walletRepository.save(wallet);
        return toResponse(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletResponse> getAll() {
        return walletRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getById(Integer id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with id: " + id));
        return toResponse(wallet);
    }

    @Override
    @Transactional
    public WalletResponse update(Integer id, WalletRequest request) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with id: " + id));
        if (request.getUserId() != null) {
            wallet.setUser(userRepository.getReferenceById(request.getUserId()));
        }
        if (request.getBalance() != null) {
            wallet.setBalance(request.getBalance());
        }
        wallet.setUpdatedAt(LocalDateTime.now());
        wallet = walletRepository.save(wallet);
        return toResponse(wallet);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!walletRepository.existsById(id)) {
            throw new IllegalArgumentException("Wallet not found with id: " + id);
        }
        walletRepository.deleteById(id);
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------
    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUser() != null ? wallet.getUser().getId() : null)
                .balance(wallet.getBalance())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}


