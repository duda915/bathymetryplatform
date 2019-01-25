package com.mdud.bathymetryplatform.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceIdResponse
{
    private Long id;
    private String response;
}
