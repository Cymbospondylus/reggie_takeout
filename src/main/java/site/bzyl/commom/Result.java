package site.bzyl.commom;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    /* 响应代码，先按资料里前端代码的写，1成功，0和其他数字失败 */
    private Integer code;
    // 错误消息
    private String msg;

    /* 使用 泛型 而不是 Object 也许更有通用性？ */
    private T data;
    // 动态数据？
    private Map map = new HashMap<>();

    /*
    * 静态方法无法访问类上定义的泛型；如果静态方法操作的引用数据类型不确定的时候，必须要将泛型定义在方法上
    * 这里的 T 和 泛型类上的 T 不必是一个类型
    * <T> 是泛型方法的固定写法
    * */
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = msg;
        return result;
    }

    /*
    * 这个方法就不是泛型方法，因为在访问修饰符和返回结果之间没有 <T>
    * 但是他可以使用泛型作为返回结果，因为他是泛型类的成员方法，可以使用类定义的泛型
    * */
    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
