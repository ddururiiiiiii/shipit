package io.github.seulgi.shipit.emailVerification.controller;

import io.github.seulgi.shipit.emailVerification.dto.EmailRequest;
import io.github.seulgi.shipit.emailVerification.service.EmailVerificationService;
import io.github.seulgi.shipit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email-verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<?>> sendVerificationEmail(@RequestBody @Valid EmailRequest request) {
        emailVerificationService.sendVerificationLink(request.email());
        return ResponseEntity.ok(ApiResponse.success("이메일이 전송되었습니다."));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestParam("token") String token) {
        emailVerificationService.verifyEmailToken(token);
        return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다."));
    }
}
