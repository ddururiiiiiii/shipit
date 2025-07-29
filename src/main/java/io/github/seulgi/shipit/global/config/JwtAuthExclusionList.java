package io.github.seulgi.shipit.global.config;

import java.util.List;

public class JwtAuthExclusionList {

    public static final List<String> NO_AUTH_URLS = List.of(
            "/",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/users",
            "/api/email-verification/**",
            "/auth/**",
            "/verify-result.html",
            "email-verification-fail.html",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
            "/api/enums/**"
    );

    private JwtAuthExclusionList() {
        // 생성 방지용 private 생성자
    }

}
