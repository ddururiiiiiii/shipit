package io.github.seulgi.shipit.user.controller;

import io.github.seulgi.shipit.global.response.ApiResponse;
import io.github.seulgi.shipit.user.dto.UserCreateRequest;
import io.github.seulgi.shipit.user.dto.UserResponse;
import io.github.seulgi.shipit.user.dto.UserUpdateRequest;
import io.github.seulgi.shipit.user.security.CustomUserDetails;
import io.github.seulgi.shipit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createUser(@RequestBody UserCreateRequest request) {
        Long userId = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userId));
    }


    /**
     * 회원정보 수정
     * @param id
     * @param request
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("회원 정보가 수정되었습니다."));
    }

    /**
     * 내 정보 조회
     * @param userDetails
     * @return
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.getMyInfo(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 회원탈퇴
     * @param user
     * @return
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal CustomUserDetails user) {
        userService.withdraw(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
