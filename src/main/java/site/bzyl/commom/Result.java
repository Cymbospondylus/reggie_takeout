package site.bzyl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {
    private Integer code;
    private Object data;
    private String msg;

    public static Result ok() {
        return new Result(200, null, "ok");
    }

    public static Result ok(Object data) {
        return new Result(200, data, "ok");
    }

    public static Result ok(Object data, String msg) {
        return new Result(200, data, msg);
    }

    public static Result fail(Integer code, String msg) {
        return new Result(code, null, msg);
    }
}
