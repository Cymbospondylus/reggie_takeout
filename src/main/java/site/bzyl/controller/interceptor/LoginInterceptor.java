package site.bzyl.controller.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* 拦截器写在 controller 包，因为拦截的都是表现层的控制器
* */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long id = (Long) request.getSession().getAttribute("session");
        // 未登录
        if (id == null) {
            response.sendRedirect("/backend/page/login/login.html");
            return false;
        }
        // 已登录
        return true;
    }
}
