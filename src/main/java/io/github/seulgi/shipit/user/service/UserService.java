package io.github.seulgi.shipit.user.service;

import io.github.seulgi.shipit.auth.jwt.JwtAuthenticationFilter;
import io.github.seulgi.shipit.global.error.BaseException;
import io.github.seulgi.shipit.global.error.UserErrorCode;
import io.github.seulgi.shipit.user.domain.User;
import io.github.seulgi.shipit.user.domain.UserStatus;
import io.github.seulgi.shipit.user.dto.UserCreateRequest;
import io.github.seulgi.shipit.user.dto.UserResponse;
import io.github.seulgi.shipit.user.dto.UserUpdateRequest;
import io.github.seulgi.shipit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Transactional
    public Long createUser(UserCreateRequest request) {
        String email = request.email();
        validateEmailVerified(email);
        validateDuplicateEmail(email);

        User user = User.create(
                request.username(),
                email,
                passwordEncoder.encode(request.password()),
                request.team(),
                request.role()
        );

        userRepository.save(user);
        redisTemplate.delete("email:verified:" + email);
        return user.getId();
    }

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

        user.changeUsername(request.username());
        user.changePassword(passwordEncoder.encode(request.password()));
        user.changeTeam(request.team());
        user.changeRole(request.role());
    }

    private void validateEmailVerified(String email) {
        String verified = redisTemplate.opsForValue().get(buildEmailVerifiedKey(email));
        if (!"true".equals(verified)) {
            throw new BaseException(UserErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BaseException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    private String buildEmailVerifiedKey(String email) {
        return "email:verified:" + email;
    }

    @Transactional
    public void withdraw(Long userId, String accessToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new BaseException(UserErrorCode.ALREADY_WITHDRAWN);
        }
        user.withdraw();
        redisTemplate.delete("RT:" + userId);
        jwtAuthenticationFilter.addToBlacklist(accessToken);
    }
}
