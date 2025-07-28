package io.github.seulgi.shipit.global.error;

import io.github.seulgi.shipit.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 Not Found (요청 경로 없음)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail("C404", "요청하신 경로를 찾을 수 없습니다."));
    }

    /**
     * 모든 예상치 못한 예외 처리 (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnexpectedException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("C500", "서버 내부 오류가 발생했습니다."));
    }

    /**
     * 공통 예외 처리기
     * BaseException (ErrorCode 포함한 커스텀 예외) 처리
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail(ex.getErrorCode()));
    }
}
