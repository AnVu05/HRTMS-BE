package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;
import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public TournamentResponse createTournament(TournamentCreateRequest request) {
        // Find Admin
        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with id: " + request.getAdminId()));

        // Check for overlapping tournaments
        if (request.getStartDate() != null && request.getEndDate() != null) {
            boolean isOverlapping = tournamentRepository.existsOverlappingTournament(request.getStartDate(), request.getEndDate());
            if (isOverlapping) {
                throw new IllegalArgumentException("Tournament dates overlap with an existing tournament");
            }
        }

        // Create Tournament entity
        Tournament tournament = Tournament.builder()
                .admin(admin)
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .allowedBreed(request.getAllowedBreed())
                .allowedHorseAge(request.getAllowedHorseAge())
                .status(request.getStatus() != null ? request.getStatus() : "PUBLIC")
                .build();
        
        // Save to DB
        tournament = tournamentRepository.save(tournament);

        // Map to Response DTO
        return TournamentResponse.builder()
                .id(tournament.getId())
                .adminId(tournament.getAdmin().getId())
                .name(tournament.getName())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                .allowedBreed(tournament.getAllowedBreed())
                .allowedHorseAge(tournament.getAllowedHorseAge())
                .status(tournament.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentDashboardResponse> getTournamentsForDashboard() {
        return tournamentRepository.getTournamentsForDashboard();
    }
}
