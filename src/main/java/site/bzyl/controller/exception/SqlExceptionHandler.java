package site.bzyl.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.bzyl.commom.Result;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 多写一个异常处理类是因为同一个异常处理类下 exceptionHandler的优先级是内部处理的，不能主动修改
 * 如果想实现把重复key的异常作为 SQLIntegrityConstraintViolationException 捕获而不是 Exception 捕获
 * 就需要重新写一个类并且设置 @Order 注解的value小于 ProjectException 的value （优先级更高）
 */


@RestControllerAdvice
@Slf4j
@Order(-1)
public class SqlExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result DuplicateKeyExceptionHandler(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage());
        e.printStackTrace();
        /**
         * 之前一直在想发生重复key异常的时候怎么返回消息才能耦合度比较低，老师这里处理的很好
         * 用分割字符串的方式把重复的键返回，这样就不用具体对某一张表的重复键做区分了
         */
        if (e.getMessage().contains("Duplicate entry")) {
            String[] split = e.getMessage().split(" ");
            String msg = split[2] + " 已存在，请重新输入！";
            return Result.error(msg);
        }

        return Result.error("未知错误，请重试！");
    }
}
