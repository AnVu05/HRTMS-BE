package com.swp.hrtms.hrtmsbe.service.impl;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.RaceFormat;
import com.swp.hrtms.hrtmsbe.repository.RaceFormatRepository;
import com.swp.hrtms.hrtmsbe.service.RaceFormatService;
import com.swp.hrtms.hrtmsbe.dto.request.RaceFormatRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceFormatResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RaceFormatServiceImpl implements RaceFormatService {
    
    private final RaceFormatRepository repository;

    @Override
    public RaceFormatResponse create(RaceFormatRequest request) {
        RaceFormat entity = new RaceFormat();
        BeanUtils.copyProperties(request, entity);
        RaceFormat saved = repository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    public List<RaceFormatResponse> getAll() {
        return repository.findAll().stream()
                //khai
                .filter(r -> !"INACTIVE".equalsIgnoreCase(r.getStatus() == null ? "" : r.getStatus().name()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RaceFormatResponse getById(Integer id) {
        RaceFormat entity = repository.findById(id)
                //khai
                .filter(r -> !"INACTIVE".equalsIgnoreCase(r.getStatus() == null ? "" : r.getStatus().name()))
                .orElseThrow(() -> new RuntimeException("RaceFormat not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    public RaceFormatResponse update(Integer id, RaceFormatRequest request) {
        RaceFormat entity = repository.findById(id)
                //khai
                .filter(r -> !"INACTIVE".equalsIgnoreCase(r.getStatus() == null ? "" : r.getStatus().name()))
                .orElseThrow(() -> new RuntimeException("RaceFormat not found with id: " + id));
        BeanUtils.copyProperties(request, entity);
        entity.setId(id);
        RaceFormat updated = repository.save(entity);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Integer id) {
        RaceFormat entity = repository.findById(id)
                //khai
                .filter(r -> !"INACTIVE".equalsIgnoreCase(r.getStatus() == null ? "" : r.getStatus().name()))
                .orElseThrow(() -> new RuntimeException("RaceFormat not found with id: " + id));
        entity.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus.INACTIVE);
        repository.save(entity);
    }

    private RaceFormatResponse mapToResponse(RaceFormat entity) {
        RaceFormatResponse response = new RaceFormatResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}



