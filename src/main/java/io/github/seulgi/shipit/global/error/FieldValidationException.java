package io.github.seulgi.shipit.global.error;

import lombok.Getter;

import java.util.Map;

@Getter
public class FieldValidationException extends RuntimeException {

    public static final String DEFAULT_CODE = "VALIDATION_ERROR";
    public static final String DEFAULT_MESSAGE = "입력값이 유효하지 않습니다.";

    private final String code;
    private final Map<String, String> fieldErrors;

    public FieldValidationException(Map<String, String> fieldErrors) {
        this(DEFAULT_CODE, DEFAULT_MESSAGE, fieldErrors);
    }

    public FieldValidationException(String code, String message, Map<String, String> fieldErrors) {
        super(message);
        this.code = code;
        this.fieldErrors = fieldErrors;
    }
}
