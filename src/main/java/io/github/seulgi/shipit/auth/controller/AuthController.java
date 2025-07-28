package io.github.seulgi.shipit.auth.controller;

import io.github.seulgi.shipit.auth.dto.LoginRequest;
import io.github.seulgi.shipit.auth.dto.LoginResponse;
import io.github.seulgi.shipit.auth.service.AuthService;
import io.github.seulgi.shipit.global.response.ApiResponse;
import io.github.seulgi.shipit.user.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
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
     * @param userDetails
     * @param bearerToken
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.replace("Bearer ", "");
        authService.logout(userDetails.getId(), accessToken);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

