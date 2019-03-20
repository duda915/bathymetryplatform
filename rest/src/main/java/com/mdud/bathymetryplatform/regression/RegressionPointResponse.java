package com.mdud.bathymetryplatform.regression;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegressionPointResponse {
    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;

    @JsonProperty("depth")
    private double depth;
}
