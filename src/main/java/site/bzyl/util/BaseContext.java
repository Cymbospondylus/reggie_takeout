package site.bzyl.util;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录的用户id
 */

// 不应该写成EmployeeHolder, 缺乏通用性, 取名为BaseContext可以同时用于用户和员工的登录信息获取
public class BaseContext {
    private BaseContext(){}

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
