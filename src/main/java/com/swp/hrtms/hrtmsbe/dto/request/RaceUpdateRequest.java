package com.swp.hrtms.hrtmsbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceUpdateRequest {

    private Integer laps;

    @JsonProperty("num_horse")
    private Integer numHorse;

    @JsonProperty("referee_id")
    private Integer refereeId;
}
