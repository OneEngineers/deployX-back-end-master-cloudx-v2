package com.api.cloudx.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deployments")
public class DeployEntities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectName;
    private String gitUrl;
    @Column(length = 20)
    private String status;      // "BUILDING", "READY", "FAILED"
    private String subdomain;   // "my-app.chanthea.com"
    private String imageTag;    // Docker tag from Jenkins
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
