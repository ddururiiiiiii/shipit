package io.github.seulgi.shipit.emailVerification.service;

import io.github.seulgi.shipit.global.error.BaseException;
import io.github.seulgi.shipit.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailSender emailSender;

    @Value("${mail.verification.expiration}")
    private long expirationSeconds;

    public void sendVerificationLink(String email) {
        String token = UUID.randomUUID().toString();
        String key = buildRedisKey(token);

        redisTemplate.opsForValue().set(key, email, Duration.ofSeconds(expirationSeconds));

        String verificationUrl = "http://localhost:8080/api/email-verification/verify?token=" + token;
        String subject = "[Shipit] 회원가입을 위한 이메일 인증 링크입니다.";
        String content = "아래 링크를 클릭하여 이메일 인증을 완료하세요 : <br>" +
                "<a href=\"" + verificationUrl + "\">이메일 인증하기</a>";

        emailSender.sendEmail(email, subject, content);
    }

    public void verifyEmailToken(String token) {
        String key = buildRedisKey(token);
        String email = redisTemplate.opsForValue().get(key);

        if (email == null) {
            throw new BaseException(UserErrorCode.INVALID_EMAIL_TOKEN);
        }

        redisTemplate.opsForValue().set("email:verified:" + email, "true");
        redisTemplate.delete(key);
    }

    private String buildRedisKey(String token) {
        return "email_verification:" + token;
    }
}

