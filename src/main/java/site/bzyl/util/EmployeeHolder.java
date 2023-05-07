package site.bzyl.util;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录的用户id
 */
public class EmployeeHolder {
    private EmployeeHolder(){}

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentEmployeeId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentEmployeeId() {
        return threadLocal.get();
    }
}
