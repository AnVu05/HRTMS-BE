package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.HorseRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseResponse;
import com.swp.hrtms.hrtmsbe.service.HorseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/horses")
public class HorseController {

    private final HorseService horseService;

    public HorseController(HorseService horseService) {
        this.horseService = horseService;
    }

    @PostMapping
    public ResponseEntity<HorseResponse> createHorse(@RequestBody HorseRequest request) {
        HorseResponse response = horseService.createHorse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<HorseResponse>> getAllHorses() {
        return ResponseEntity.ok(horseService.getAllHorses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorseResponse> getHorseById(@PathVariable Integer id) {
        return ResponseEntity.ok(horseService.getHorseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorseResponse> updateHorse(@PathVariable Integer id, @RequestBody HorseRequest request) {
        return ResponseEntity.ok(horseService.updateHorse(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorse(@PathVariable Integer id) {
        horseService.deleteHorse(id);
        return ResponseEntity.noContent().build();
    }
}
