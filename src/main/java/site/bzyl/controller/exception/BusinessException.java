package site.bzyl.controller.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String msg) {
        super(msg);
    }
}
