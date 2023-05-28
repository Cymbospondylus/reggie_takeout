package site.bzyl.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.bzyl.constant.HttpConstant;
import site.bzyl.util.BaseContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 移动端前台用户登录校验
@Component
@Slf4j
public class FrontendLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 获取当前登录用户的id
        Long userId = (Long) request.getSession().getAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
        // 当前用户已登录
        if (userId != null) {
            // 保存到当前线程的ThreadLocal中
            BaseContext.setCurrentId(userId);
            return true;
        }

        /**
         * todo 这段代码是用来在后台能读到前端信息不被拦截, 但是有个bug会导致后台登录过后会在服务器保存session信息
         * 这个时候访问前台不需要登录也可以查看菜品, 暂时还没想到好的解决方法
         */
        // 获取当前登录员工id
        Long empId = (Long) request.getSession().getAttribute(HttpConstant.CURRENT_LOGIN_EMPLOYEE_ID);
        // 当前员工已登录
        if (empId != null) {
            return true;
        }


        // 当前用户未登录
        log.info("拦截到请求：{}", request.getRequestURI());
        // 重定向到用户登录界面
        response.sendRedirect("/front/page/login.html");
        return false;
    }
}
