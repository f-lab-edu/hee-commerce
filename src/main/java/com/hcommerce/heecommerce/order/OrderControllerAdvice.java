package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OrderControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponseDto orderNotFoundExceptionHandler(OrderNotFoundException e) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponseDto orderOverStockExceptionExceptionHandler(OrderOverStockException e) {
        return new ErrorResponseDto(HttpStatus.CONFLICT.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponseDto fallbackExceptionHandler(Exception e) {
        return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.name(), "내부 서버 오류가 발생했습니다.");
    }
}
