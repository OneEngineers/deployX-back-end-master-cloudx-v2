package com.api.cloudx.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class AuthResponseDTO {
    @NonNull
    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";
}
