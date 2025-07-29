package io.github.seulgi.shipit.user.service;

import io.github.seulgi.shipit.auth.jwt.JwtAuthenticationFilter;
import io.github.seulgi.shipit.global.error.BaseException;
import io.github.seulgi.shipit.global.error.FieldValidationException;
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

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Transactional
    public Long createUser(UserCreateRequest request) {
        validateCreateRequest(request);

        String email = request.email();

        User user = User.create(
                request.username(),
                email,
                passwordEncoder.encode(request.password()),
                request.team(),
                request.role()
        );

        userRepository.save(user);
        redisTemplate.delete(buildEmailVerifiedKey(email));
        return user.getId();
    }

    private void validateCreateRequest(UserCreateRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (request.username() == null || request.username().isBlank()) {
            errors.put("username", "이름을 입력해주세요.");
        }

        if (request.team() == null) {
            errors.put("team", "팀을 선택해주세요.");
        }

        if (request.role() == null) {
            errors.put("role", "권한을 선택해주세요.");
        }

        String email = request.email();
        if (email == null || email.isBlank()) {
            errors.put("email", "이메일을 입력해주세요.");
        } else {
            if (!isEmailVerified(email)) {
                errors.put("email", UserErrorCode.EMAIL_NOT_VERIFIED.getMessage());
            }
            if (isDuplicateEmail(email)) {
                errors.put("email", UserErrorCode.DUPLICATE_EMAIL.getMessage());
            }
        }

        String password = request.password();
        if (password == null || password.isBlank()) {
            errors.put("password", "비밀번호를 입력해주세요.");
        } else if (isWeakPassword(password)) {
            errors.put("password", UserErrorCode.WEAK_PASSWORD.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new FieldValidationException("VALIDATION_ERROR", "입력값이 유효하지 않습니다.", errors);
        }
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

    private boolean isEmailVerified(String email) {
        String verified = redisTemplate.opsForValue().get(buildEmailVerifiedKey(email));
        return "true".equals(verified);
    }

    private boolean isDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
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

    private boolean isWeakPassword(String password) {
        boolean lengthCheck = password.length() >= 8 && password.length() <= 20;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return !(lengthCheck && hasUpper && hasLower && hasDigit && hasSpecial);
    }
}
