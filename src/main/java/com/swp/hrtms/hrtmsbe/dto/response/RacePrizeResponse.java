package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Khai
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RacePrizeResponse {

    private Integer rank;

    private Long amount;
}
