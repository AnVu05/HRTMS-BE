package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.WalletRequest;
import com.swp.hrtms.hrtmsbe.dto.response.WalletResponse;
import java.util.List;

public interface WalletService {
    WalletResponse create(WalletRequest request);
    List<WalletResponse> getAll();
    WalletResponse getById(Integer id);
    WalletResponse update(Integer id, WalletRequest request);
    void delete(Integer id);
}


