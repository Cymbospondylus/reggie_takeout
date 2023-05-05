package site.bzyl.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.bzyl.commom.Result;



@RestControllerAdvice
@Slf4j
@Order(0)
public class ProjectExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {

        log.error(e.getMessage());
        return Result.error("服务器异常，请重试！");
    }
}
