package com.earthquake.core.ingestion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsgsGeometry {
    private List<Double> coordinates;

    public Double longitude() { return at(0); }
    public Double latitude()  { return at(1); }
    public Double depthKm()   { return at(2); }

    private Double at(int index) {
        return coordinates != null && coordinates.size() > index ? coordinates.get(index) : null;
    }
}
