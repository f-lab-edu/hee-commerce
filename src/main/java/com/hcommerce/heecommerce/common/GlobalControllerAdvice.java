package com.hcommerce.heecommerce.common;

import com.hcommerce.heecommerce.auth.InvalidTokenException;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.order.exception.InvalidPaymentAmountException;
import com.hcommerce.heecommerce.order.exception.MaxOrderQuantityExceededException;
import com.hcommerce.heecommerce.order.exception.OrderNotFoundException;
import com.hcommerce.heecommerce.order.exception.OrderOverStockException;
import com.hcommerce.heecommerce.order.exception.TimeDealProductNotFoundException;
import com.hcommerce.heecommerce.payment.TosspaymentsException;
import com.hcommerce.heecommerce.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ResponseDto userNotFoundExceptionHandler(UserNotFoundException e) {
        return ResponseDto.builder()
            .code(HttpStatus.NOT_FOUND.name())
            .message(e.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ResponseDto orderNotFoundExceptionHandler(OrderNotFoundException e) {
        return ResponseDto.builder()
                .code(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ResponseDto timeDealProductNotFoundExceptionHandler(TimeDealProductNotFoundException e) {
        return ResponseDto.builder()
            .code(HttpStatus.NOT_FOUND.name())
            .message(e.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ResponseDto orderOverStockExceptionExceptionHandler(OrderOverStockException e) {
        return ResponseDto.builder()
                .code(HttpStatus.CONFLICT.name())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ResponseDto maxOrderQuantityExceededExceptionHandler(MaxOrderQuantityExceededException e) {
        return ResponseDto.builder()
            .code(HttpStatus.CONFLICT.name())
            .message(e.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseDto methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return ResponseDto.builder()
            .code(HttpStatus.BAD_REQUEST.name())
            .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ResponseDto invalidPaymentAmountExceptionHandler(InvalidPaymentAmountException e) {
        return ResponseDto.builder()
            .code(HttpStatus.BAD_REQUEST.name())
            .message(e.getMessage())
            .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    public ResponseDto invalidTokenExceptionHandler(InvalidTokenException e) {
        log.error("class = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());

        return ResponseDto.builder()
            .code(HttpStatus.UNAUTHORIZED.name())
            .message(e.getMessage())
            .build();
    }

    /**
     * @ResponseStatus 어노테이션을 안 붙인 이유는 토스페이먼츠의 에러 코드가 400대일 수도 있고 500일 수도 있어어서, 동적으로 상태코드를 변경할 수 있도록 하기 위해서이다.
     */
    @ExceptionHandler
    public ResponseEntity tosspaymentsExceptionHandler(TosspaymentsException e) {
        ResponseDto responseDto = ResponseDto.builder()
                                        .code(e.getCode())
                                        .message(e.getMessage()+"aaaaa")
                                        .build();

        return new ResponseEntity(responseDto, e.getTossHttpStatusCode());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseDto fallbackExceptionHandler(Exception e) {
        log.error("class = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
        log.error("stackTrace = {}", e.getStackTrace());

        return ResponseDto.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message("내부 서버 오류가 발생했습니다.")
                .build();
    }
}
