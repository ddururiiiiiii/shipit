package io.github.seulgi.shipit.user.dto;

import io.github.seulgi.shipit.user.domain.RoleType;
import io.github.seulgi.shipit.user.domain.TeamType;

public record UserUpdateRequest(
        String username,
        String password,
        TeamType team,
        RoleType role
) {}
