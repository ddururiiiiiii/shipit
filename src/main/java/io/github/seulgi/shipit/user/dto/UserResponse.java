package io.github.seulgi.shipit.user.dto;

public record UserResponse(
        Long id,
        String email,
        String username,
        String role
) {}
