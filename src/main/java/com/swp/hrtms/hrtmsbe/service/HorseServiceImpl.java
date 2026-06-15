package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.HorseRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseResponse;
import com.swp.hrtms.hrtmsbe.entity.Horse;
import com.swp.hrtms.hrtmsbe.entity.HorseOwner;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseRepository;
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
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HorseResponse getHorseById(Integer id) {
        Horse horse = findHorseById(id);
        return toResponse(horse);
    }

    @Override
    @Transactional
    public void deleteHorse(Integer id) {
        Horse horse = findHorseById(id);
        horseRepository.delete(horse);
    }

    private Horse findHorseById(Integer id) {
        return horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));
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
                horse.getStatus());
    }

    private void applyRequestToHorse(Horse horse, HorseRequest request) {
        HorseOwner owner = horseOwnerRepository.findById(request.getOwnerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Horse owner not found with id: " + request.getOwnerId()));

        horse.setOwner(owner);
        horse.setName(request.getName());
        horse.setAge(request.getAge());
        horse.setBreed(request.getBreed());
        horse.setStatus(request.getStatus());
    }

    @Override
    @Transactional
    public HorseResponse updateHorse(Integer id, HorseRequest request) {
        Horse horse = findHorseById(id);
        applyRequestToHorse(horse, request);

        Horse updatedHorse = horseRepository.save(horse);
        return toResponse(updatedHorse);
    }

    @Override
    @Transactional
    public HorseResponse createHorse(HorseRequest request) {
        Horse horse = new Horse();
        applyRequestToHorse(horse, request);

        Horse savedHorse = horseRepository.save(horse);
        return toResponse(savedHorse);
    }
}
