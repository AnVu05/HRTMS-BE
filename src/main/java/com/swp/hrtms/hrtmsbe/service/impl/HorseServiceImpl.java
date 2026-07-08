package com.swp.hrtms.hrtmsbe.service.impl;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.HorseRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseResponse;
import com.swp.hrtms.hrtmsbe.entity.Horse;
import com.swp.hrtms.hrtmsbe.entity.HorseOwner;
import com.swp.hrtms.hrtmsbe.entity.HorseStatus;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseRepository;
import com.swp.hrtms.hrtmsbe.service.HorseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;
    private final HorseOwnerRepository horseOwnerRepository;

    public HorseServiceImpl(HorseRepository horseRepository, HorseOwnerRepository horseOwnerRepository) {
        this.horseRepository = horseRepository;
        this.horseOwnerRepository = horseOwnerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorseResponse> getAllHorses() {
        return horseRepository.findAll()
                .stream()
                // Lọc loại bỏ những con ngựa đã bị soft-delete (RETIRED)
                .filter(horse -> horse.getStatus() != HorseStatus.RETIRED)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorseResponse> getHorsesByOwner(Integer ownerId) {
        return horseRepository.findByOwnerUserId(ownerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HorseResponse getHorseById(Integer id) {
        Horse horse = findActiveHorseById(id);
        return toResponse(horse);
    }

    @Override
    @Transactional
    public void deleteHorse(Integer id) {
        Horse horse = findActiveHorseById(id);
        horse.setStatus(HorseStatus.RETIRED);
        horseRepository.save(horse);
    }

    private Horse findActiveHorseById(Integer id) {
        Horse horse = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));
        if (horse.getStatus() == HorseStatus.RETIRED) {
            throw new ResourceNotFoundException("Horse not found (or retired) with id: " + id);
        }
        return horse;
    }

    private HorseResponse toResponse(Horse horse) {
        HorseOwner owner = horse.getOwner();
        Integer ownerId = owner != null ? owner.getUserId() : null;
        String ownerName = owner != null ? owner.getOwnerName() : null;

        return new HorseResponse(
                horse.getId(),
                ownerId,
                ownerName,
                horse.getName(),
                horse.getAge(),
                horse.getBreed(),
                horse.getSex(),
                horse.getWeightKg(),
                horse.getStatus());
    }

    @Override
    @Transactional
    public HorseResponse updateHorse(Integer id, HorseRequest request) {
        Horse horse = findActiveHorseById(id);

        // Partial update — chỉ cập nhật trường nếu request KHÔNG null
        if (request.getOwnerId() != null) {
            HorseOwner owner = horseOwnerRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Horse owner not found with id: " + request.getOwnerId()));
            horse.setOwner(owner);
        }
        if (request.getName() != null) {
            horse.setName(request.getName());
        }
        if (request.getAge() != null) {
            horse.setAge(request.getAge());
        }
        if (request.getBreed() != null) {
            horse.setBreed(request.getBreed());
        }
        if (request.getSex() != null) {
            horse.setSex(request.getSex());
        }
        if (request.getWeightKg() != null) {
            horse.setWeightKg(request.getWeightKg());
        }
        if (request.getStatus() != null) {
            horse.setStatus(request.getStatus());
        }

        Horse updatedHorse = horseRepository.save(horse);
        return toResponse(updatedHorse);
    }

    @Override
    @Transactional
    public HorseResponse createHorse(HorseRequest request) {
        if (request.getOwnerId() == null) {
            throw new IllegalArgumentException("Horse owner ID is required when creating a horse");
        }
        HorseOwner owner = horseOwnerRepository.findById(request.getOwnerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Horse owner not found with id: " + request.getOwnerId()));

        Horse horse = Horse.builder()
                .owner(owner)
                .name(request.getName())
                .age(request.getAge())
                .breed(request.getBreed())
                .sex(request.getSex())
                .weightKg(request.getWeightKg())
                //khai
                .status(request.getStatus() != null ? request.getStatus() : HorseStatus.WORK)
                .build();

        Horse savedHorse = horseRepository.save(horse);
        return toResponse(savedHorse);
    }
}



