package io.github.seulgi.shipit.auth.controller;

import io.github.seulgi.shipit.auth.dto.LoginRequest;
import io.github.seulgi.shipit.auth.dto.LoginResponse;
import io.github.seulgi.shipit.auth.service.AuthService;
import io.github.seulgi.shipit.global.response.ApiResponse;
import io.github.seulgi.shipit.user.security.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request
                                                            , HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);

        Cookie accessTokenCookie = new Cookie("accessToken", loginResponse.accessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // 로컬환경이면 false, 운영이면 true
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 30); // 30분

        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    /**
     * refresh token 재발급
     * @param bearerToken
     * @return
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshAccessToken(
            @RequestHeader("Authorization") String bearerToken) {

        String refreshToken = bearerToken.replace("Bearer ", "");
        LoginResponse response = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 로그아웃
     * @param request
     * @param response
     * @param userDetails
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 1. accessToken 쿠키에서 꺼냄
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
            }
        }

        // 2. accessToken 유효하면 블랙리스트 처리
        if (StringUtils.hasText(accessToken)) {
            authService.logout(userDetails.getId(), accessToken);
        }

        // 3. accessToken 쿠키 삭제
        Cookie deleteCookie = new Cookie("accessToken", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(false); // 운영이면 true
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

