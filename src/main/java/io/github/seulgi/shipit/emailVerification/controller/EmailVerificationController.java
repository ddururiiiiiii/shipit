package io.github.seulgi.shipit.emailVerification.controller;

import io.github.seulgi.shipit.emailVerification.dto.EmailRequest;
import io.github.seulgi.shipit.emailVerification.service.EmailVerificationService;
import io.github.seulgi.shipit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
    public String verifyEmail(@RequestParam("token") String token) {
        boolean isValid = emailVerificationService.isTokenValid(token);
        return isValid ? "verify-result" : "email-verification-fail";
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerified(@RequestParam("email") String email) {
        boolean isVerified = emailVerificationService.isEmailVerified(email);
        return ResponseEntity.ok(ApiResponse.success(isVerified));
    }
}
