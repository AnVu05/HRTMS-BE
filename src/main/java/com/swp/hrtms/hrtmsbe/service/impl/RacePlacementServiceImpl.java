package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.RacePlacementRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RacePlacementResponse;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.RacePlacement;
import com.swp.hrtms.hrtmsbe.entity.RaceResult;
import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.RacePlacementRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceResultRepository;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
import com.swp.hrtms.hrtmsbe.service.RacePlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                .raceResult(findRaceResultOrNull(request.getRaceResultId()))
                .registrationForm(findRegistrationFormOrNull(request.getRegistrationFormId()))
                .finishPosition(request.getFinishPosition())
                .finishTime(request.getFinishTime())
                .weighInWeight(request.getWeighInWeight())
                .build();
        validateFinishTimeAndPosition(placement);
        placement = racePlacementRepository.save(placement);
        return toResponse(placement);
    }

    private void shiftRanksForPositionChange(RacePlacement placement, Integer newPosition) {
        if (newPosition == null) {
            return;
        }
        if (newPosition <= 0) {
            throw new IllegalArgumentException("Finish position must be greater than 0.");
        }
        if (placement.getRaceResult() == null || placement.getRaceResult().getId() == null) {
            return;
        }

        Integer oldPosition = placement.getFinishPosition();
        if (oldPosition != null && oldPosition.equals(newPosition)) {
            return;
        }

        List<RacePlacement> sameRaceResultPlacements =
                racePlacementRepository.findByRaceResult_Id(placement.getRaceResult().getId());
        for (RacePlacement existing : sameRaceResultPlacements) {
            if (placement.getId() != null && placement.getId().equals(existing.getId())) {
                continue;
            }
            if (existing.getFinishPosition() == null) {
                continue;
            }

            if (oldPosition == null) {
                if (existing.getFinishPosition() >= newPosition) {
                    existing.setFinishPosition(existing.getFinishPosition() + 1);
                    racePlacementRepository.save(existing);
                }
            } else if (newPosition < oldPosition) {
                if (existing.getFinishPosition() >= newPosition && existing.getFinishPosition() < oldPosition) {
                    existing.setFinishPosition(existing.getFinishPosition() + 1);
                    racePlacementRepository.save(existing);
                }
            } else if (newPosition > oldPosition) {
                if (existing.getFinishPosition() <= newPosition && existing.getFinishPosition() > oldPosition) {
                    existing.setFinishPosition(existing.getFinishPosition() - 1);
                    racePlacementRepository.save(existing);
                }
            }
        }
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
                .orElseThrow(() -> new ResourceNotFoundException("RacePlacement not found with id: " + id));
        return toResponse(placement);
    }

    @Override
    @Transactional
    public RacePlacementResponse update(Integer id, RacePlacementRequest request) {
        RacePlacement placement = racePlacementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RacePlacement not found with id: " + id));
        // Partial update: only set fields that are present in the request.
        if (request.getRaceResultId() != null
                && (placement.getRaceResult() == null
                        || !request.getRaceResultId().equals(placement.getRaceResult().getId()))) {
            throw new IllegalArgumentException("RaceResult cannot be changed after a placement is created.");
        }
        if (request.getRegistrationFormId() != null
                && (placement.getRegistrationForm() == null
                        || !request.getRegistrationFormId().equals(placement.getRegistrationForm().getId()))) {
            throw new IllegalArgumentException("RegistrationForm cannot be changed after a placement is created.");
        }
        if (request.getFinishPosition() != null) {
            shiftRanksForPositionChange(placement, request.getFinishPosition());
            placement.setFinishPosition(request.getFinishPosition());
        }
        if (request.getFinishTime() != null)
            placement.setFinishTime(request.getFinishTime());
        if (request.getWeighInWeight() != null)
            placement.setWeighInWeight(request.getWeighInWeight());
        validateFinishTimeAndPosition(placement);
        placement = racePlacementRepository.save(placement);
        return toResponse(placement);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // RacePlacement has no status field, so delete is a hard delete.
        if (!racePlacementRepository.existsById(id)) {
            throw new ResourceNotFoundException("RacePlacement not found with id: " + id);
        }
        racePlacementRepository.deleteById(id);
    }

    // -------------------------------------------------------
    // Helper
    // -------------------------------------------------------
    private RaceResult findRaceResultOrNull(Integer raceResultId) {
        if (raceResultId == null) {
            return null;
        }
        return raceResultRepository.findById(raceResultId)
                .orElseThrow(() -> new ResourceNotFoundException("RaceResult not found with id: " + raceResultId));
    }

    private RegistrationForm findRegistrationFormOrNull(Integer registrationFormId) {
        if (registrationFormId == null) {
            return null;
        }
        return registrationFormRepository.findById(registrationFormId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RegistrationForm not found with id: " + registrationFormId));
    }

    private void validateFinishTimeAndPosition(RacePlacement placement) {
        if (placement.getFinishTime() == null) {
            throw new IllegalArgumentException("Finish time is required.");
        }
        if (placement.getFinishPosition() == null) {
            throw new IllegalArgumentException("Finish position is required.");
        }
        if (placement.getFinishPosition() <= 0) {
            throw new IllegalArgumentException("Finish position must be greater than 0.");
        }
        if (placement.getRaceResult() == null) {
            throw new IllegalArgumentException("RaceResult is required to validate finish time.");
        }

        Race race = placement.getRaceResult().getRace();
        if (race == null) {
            throw new IllegalArgumentException("RaceResult must belong to a race.");
        }
        validateRaceAcceptsManualPlacement(race);
        if (race.getDate() == null || race.getStartTime() == null) {
            throw new IllegalArgumentException("Race date and start time are required to validate finish time.");
        }

        LocalDateTime raceStart = LocalDateTime.of(race.getDate(), race.getStartTime());
        if (!placement.getFinishTime().isAfter(raceStart)) {
            throw new IllegalArgumentException("Finish time must be after race start time.");
        }

        List<RacePlacement> sameRaceResultPlacements =
                racePlacementRepository.findByRaceResult_Id(placement.getRaceResult().getId());
        for (RacePlacement existing : sameRaceResultPlacements) {
            if (placement.getId() != null && placement.getId().equals(existing.getId())) {
                continue;
            }
            if (existing.getFinishPosition() == null || existing.getFinishTime() == null) {
                continue;
            }
            if (placement.getFinishPosition().equals(existing.getFinishPosition())) {
                continue;
            }
        }
    }

    // Blocks manual placement entry for races closed by health-check cancellation or automatic walk-over.
    private void validateRaceAcceptsManualPlacement(Race race) {
        if (race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED) {
            throw new IllegalArgumentException("Race placement cannot be created for a cancelled race.");
        }
        if (race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.WALK_OVER) {
            throw new IllegalArgumentException(
                    "Race placement cannot be created manually for a walk-over race.");
        }
    }

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
