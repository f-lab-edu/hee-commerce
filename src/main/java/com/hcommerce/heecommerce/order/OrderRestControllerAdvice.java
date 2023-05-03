package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OrderRestControllerAdvice {

    @ExceptionHandler
    public ErrorResponseDTO orderNotFoundExceptionHandler(OrderNotFoundException e) {
        return new ErrorResponseDTO(HttpStatus.NOT_FOUND.name(), e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponseDTO orderOverStockExceptionExceptionHandler(OrderOverStockException e) {
        return new ErrorResponseDTO(HttpStatus.CONFLICT.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponseDTO fallbackExceptionHandler(Exception e) {
        return new ErrorResponseDTO("INTERNAL_SERVER_ERROR", "내부 서버 오류가 발생했습니다.");
    }
}
