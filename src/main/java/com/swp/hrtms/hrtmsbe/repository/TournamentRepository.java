package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    @Query("SELECT new com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse(t.id, t.name, t.startDate, t.endDate, COUNT(r.id), t.status) "
            +
            "FROM Tournament t LEFT JOIN Race r ON r.tournament.id = t.id " +
            "GROUP BY t.id, t.name, t.startDate, t.endDate, t.status " +
            "ORDER BY t.startDate DESC")
    java.util.List<com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse> getTournamentsForDashboard();

    java.util.List<Tournament> findByStatus(String status);
}
