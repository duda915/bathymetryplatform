package com.mdud.bathymetryplatform.regression;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegressionPointsListResponse {
    @JsonProperty("depths")
    private List<RegressionPointResponse> regressionPointResponseList;
}
