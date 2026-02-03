package com.api.cloudx.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeployDTO {
    private Long id;
    @NotBlank(message = "Name is required")
    private String projectName;
    private String gitUrl;
    private String getImageTag;
//    private String subdomain;
}
