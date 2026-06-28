package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Khai
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RacePrizeRequest {

    private Integer rank;

    private Long amount;
}
