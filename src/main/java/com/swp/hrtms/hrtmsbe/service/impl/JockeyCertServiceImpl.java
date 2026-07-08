package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertResponse;
import com.swp.hrtms.hrtmsbe.service.JockeyCertService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JockeyCertServiceImpl implements JockeyCertService {
    @Override public JockeyCertResponse create(JockeyCertRequest request) { return null; }
    @Override public List<JockeyCertResponse> getAll() { return Collections.emptyList(); }
    @Override public JockeyCertResponse getById(Integer id) { return null; }
    @Override public JockeyCertResponse update(Integer id, JockeyCertRequest request) { return null; }
    @Override public void delete(Integer id) { }
}


