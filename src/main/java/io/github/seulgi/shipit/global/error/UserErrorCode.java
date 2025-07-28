package io.github.seulgi.shipit.global.error;

import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    INVALID_EMAIL_TOKEN("U001", "유효하지 않은 인증 링크입니다."),
    EMAIL_SEND_FAIL("U002", "이메일 전송에 실패했습니다."),
    EMAIL_NOT_VERIFIED("U003", "이메일 인증이 완료되지 않았습니다."),
    DUPLICATE_EMAIL("U004", "이미 사용 중인 이메일입니다."),

    USER_NOT_FOUND("U010", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD("U011", "비밀번호가 일치하지 않습니다."),
    ALREADY_WITHDRAWN("U012","이미 탈퇴한 회원입니다."),

    INVALID_TOKEN("401", "유효하지 않은 토큰입니다.");

    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
