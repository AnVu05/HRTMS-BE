package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.entity.Prediction;
import java.util.List;

public interface RefundService {
    void refundPredictions(List<Prediction> predictions, String reason);

    void refundForRace(Integer raceId, String reason);

    void refundForTournament(Integer tournamentId, String reason);
}
