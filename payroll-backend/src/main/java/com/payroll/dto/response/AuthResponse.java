package com.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String role;
    private String tokenType;

    public static AuthResponse of(String accessToken, String refreshToken, String email, String role) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(email)
                .role(role)
                .tokenType("Bearer") //ignorei18n_start //ignorei18n_end
                .build();
    }
}
