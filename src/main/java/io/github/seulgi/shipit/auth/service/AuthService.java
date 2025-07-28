package io.github.seulgi.shipit.auth.service;

import io.github.seulgi.shipit.auth.dto.LoginRequest;
import io.github.seulgi.shipit.auth.dto.LoginResponse;
import io.github.seulgi.shipit.auth.jwt.JwtAuthenticationFilter;
import io.github.seulgi.shipit.auth.jwt.JwtProvider;
import io.github.seulgi.shipit.global.error.BaseException;
import io.github.seulgi.shipit.global.error.UserErrorCode;
import io.github.seulgi.shipit.user.domain.RoleType;
import io.github.seulgi.shipit.user.domain.User;
import io.github.seulgi.shipit.user.domain.UserStatus;
import io.github.seulgi.shipit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new BaseException(UserErrorCode.ALREADY_WITHDRAWN);
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BaseException(UserErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        redisTemplate.opsForValue().set(
                "RT:" + user.getId(),
                refreshToken,
                Duration.ofDays(7)
        );
        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse reissueAccessToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BaseException(UserErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtProvider.getUserId(refreshToken);

        String redisToken = redisTemplate.opsForValue().get("RT:" + userId);
        if (redisToken == null || !redisToken.equals(refreshToken)) {
            throw new BaseException(UserErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtProvider.createAccessToken(userId, getUserRole(userId));
        return new LoginResponse(newAccessToken, refreshToken);
    }

    private RoleType getUserRole(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND))
                .getRole();
    }

    public void logout(Long userId, String accessToken) {
        jwtAuthenticationFilter.addToBlacklist(accessToken);
    }



}
