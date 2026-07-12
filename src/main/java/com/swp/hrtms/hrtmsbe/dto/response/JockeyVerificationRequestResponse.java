package com.swp.hrtms.hrtmsbe.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JockeyVerificationRequestResponse {

    @JsonProperty("notification_id")
    private Integer notificationId;

    @JsonProperty("jockey_id")
    private Integer jockeyId;

    @JsonProperty("jockey_name")
    private String jockeyName;

    @JsonProperty("pending_certificates")
    private List<String> pendingCertificates;
}

