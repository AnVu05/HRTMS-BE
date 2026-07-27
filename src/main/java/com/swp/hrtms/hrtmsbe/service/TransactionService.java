package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.TransactionRequest;
import com.swp.hrtms.hrtmsbe.dto.response.TransactionResponse;
import java.util.List;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);
    List<TransactionResponse> getAll();
    TransactionResponse getById(Integer id);
    TransactionResponse update(Integer id, TransactionRequest request);
    void delete(Integer id);
}


