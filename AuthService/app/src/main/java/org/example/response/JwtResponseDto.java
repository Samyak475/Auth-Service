package org.example.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponseDto {
    private String accessToken;
    private String token;
}
