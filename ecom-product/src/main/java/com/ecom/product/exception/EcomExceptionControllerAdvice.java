package com.ecom.product.exception;

import com.ecom.common.utils.R;
import com.ecom.common.exception.BizCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice(basePackages = "com.ecom.product.controller")
public class EcomExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R exceptionHandler(MethodArgumentNotValidException e) {
        log.error("data invalid {}, com.ecom.common.exception type is {}", e.getMessage(), e.getClass());
        return R.error(BizCodeEnum.VALID_EXCEPTION)
                .put("data", e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(
                                Collectors.toMap(FieldError::getField,
                                        (FieldError o) -> Objects.toString(o.getDefaultMessage(), "")
                                        , (pre, cur) -> pre
                                )
                        )
                );
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Exception e) {
        log.error("Error: ", e);
        return R.error();
    }
}
