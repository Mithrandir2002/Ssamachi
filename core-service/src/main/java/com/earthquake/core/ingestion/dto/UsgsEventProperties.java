package com.earthquake.core.ingestion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsgsEventProperties {
    private Double mag;
    private String place;
    private Long time;
    private Long updated;
    private String magType;
    private String status;
    private Integer tsunami;
    private Integer felt;
    private String type;
}
