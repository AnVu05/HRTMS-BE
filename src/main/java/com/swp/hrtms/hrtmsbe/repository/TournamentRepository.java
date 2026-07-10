package com.swp.hrtms.hrtmsbe.repository;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    @Query("SELECT new com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse(t.id, t.name, t.startDate, t.endDate, (SELECT COUNT(r.id) FROM Race r WHERE r.tournament.id = t.id), t.status) "
            +
            "FROM Tournament t " +
            "ORDER BY t.startDate DESC")
    java.util.List<com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse> getTournamentsForDashboard();

    java.util.List<Tournament> findByStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus status);
}
