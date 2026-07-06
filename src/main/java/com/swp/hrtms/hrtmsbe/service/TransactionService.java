package com.swp.hrtms.hrtmsbe.service;

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


