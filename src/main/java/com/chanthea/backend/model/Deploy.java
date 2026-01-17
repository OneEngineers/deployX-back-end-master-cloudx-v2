package com.chanthea.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deployments")
public class Deploy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectName;
    private String gitUrl;
    private String status;      // "BUILDING", "READY", "FAILED"
    private String subdomain;   // "my-app.chanthea.com"
    private String imageTag;    // Docker tag from Jenkins
}
