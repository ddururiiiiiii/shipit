package io.github.seulgi.shipit.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
