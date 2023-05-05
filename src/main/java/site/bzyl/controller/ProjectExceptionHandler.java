package site.bzyl.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.bzyl.commom.Result;

@RestControllerAdvice
public class ProjectExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Result DuplicateKeyExceptionHandler(DuplicateKeyException e) {
        e.printStackTrace();
        return Result.error("账号已存在，请重新输入！");
    }

    @ExceptionHandler(Exception.class)
    public Result doException(Exception e) {

        e.printStackTrace();
        return Result.error("服务器异常，请重试！");
    }
}
