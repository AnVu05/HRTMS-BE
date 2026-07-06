package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.RacePlacementRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RacePlacementResponse;
import com.swp.hrtms.hrtmsbe.entity.RacePlacement;
import com.swp.hrtms.hrtmsbe.repository.RacePlacementRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceResultRepository;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
import com.swp.hrtms.hrtmsbe.service.RacePlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RacePlacementServiceImpl implements RacePlacementService {

    private final RacePlacementRepository racePlacementRepository;
    private final RaceResultRepository raceResultRepository;
    private final RegistrationFormRepository registrationFormRepository;

    @Override
    @Transactional
    public RacePlacementResponse create(RacePlacementRequest request) {
        RacePlacement placement = RacePlacement.builder()
                .raceResult(request.getRaceResultId() != null ? raceResultRepository.getReferenceById(request.getRaceResultId()) : null)
                .registrationForm(request.getRegistrationFormId() != null ? registrationFormRepository.getReferenceById(request.getRegistrationFormId()) : null)
                .finishPosition(request.getFinishPosition())
                .finishTime(request.getFinishTime())
                .weighInWeight(request.getWeighInWeight())
                .build();
        placement = racePlacementRepository.save(placement);
        return toResponse(placement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RacePlacementResponse> getAll() {
        return racePlacementRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RacePlacementResponse getById(Integer id) {
        RacePlacement placement = racePlacementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RacePlacement not found with id: " + id));
        return toResponse(placement);
    }

    @Override
    @Transactional
    public RacePlacementResponse update(Integer id, RacePlacementRequest request) {
        RacePlacement placement = racePlacementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RacePlacement not found with id: " + id));
        // Partial update — chỉ set nếu request không null
        if (request.getRaceResultId() != null)
            placement.setRaceResult(raceResultRepository.getReferenceById(request.getRaceResultId()));
        if (request.getRegistrationFormId() != null)
            placement.setRegistrationForm(registrationFormRepository.getReferenceById(request.getRegistrationFormId()));
        if (request.getFinishPosition() != null)
            placement.setFinishPosition(request.getFinishPosition());
        if (request.getFinishTime() != null)
            placement.setFinishTime(request.getFinishTime());
        if (request.getWeighInWeight() != null)
            placement.setWeighInWeight(request.getWeighInWeight());
        placement = racePlacementRepository.save(placement);
        return toResponse(placement);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // RacePlacement không có status field → hard delete
        if (!racePlacementRepository.existsById(id)) {
            throw new IllegalArgumentException("RacePlacement not found with id: " + id);
        }
        racePlacementRepository.deleteById(id);
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------
    private RacePlacementResponse toResponse(RacePlacement placement) {
        return RacePlacementResponse.builder()
                .id(placement.getId())
                .raceResultId(placement.getRaceResult() != null ? placement.getRaceResult().getId() : null)
                .registrationFormId(placement.getRegistrationForm() != null ? placement.getRegistrationForm().getId() : null)
                .finishPosition(placement.getFinishPosition())
                .finishTime(placement.getFinishTime())
                .weighInWeight(placement.getWeighInWeight())
                .build();
    }
}


