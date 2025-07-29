package io.github.seulgi.shipit.user.dto;

import io.github.seulgi.shipit.user.domain.RoleType;
import io.github.seulgi.shipit.user.domain.TeamType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
        @NotBlank(message = "이름 입력은 필수 입니다.")
        String username,

        @NotBlank(message = "이메일 입력은 필수 입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호 입력은 필수 입니다.")
        String password,

        @NotNull(message = "팀 선택은 필수입니다.")
        TeamType team,

        @NotNull(message = "권한 선택은 필수입니다.")
        RoleType role
) {}