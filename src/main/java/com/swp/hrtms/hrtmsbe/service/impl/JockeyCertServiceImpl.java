package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.service.JockeyCertService;
import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collections;

@Service
public class JockeyCertServiceImpl implements JockeyCertService {
    @Override public JockeyCertResponse create(JockeyCertRequest request) { return null; }
    @Override public List<JockeyCertResponse> getAll() { return Collections.emptyList(); }
    @Override public JockeyCertResponse getById(Integer id) { return null; }
    @Override public JockeyCertResponse update(Integer id, JockeyCertRequest request) { return null; }
    @Override public void delete(Integer id) { }
}


