package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
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


