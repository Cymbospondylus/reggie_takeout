package site.bzyl.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.bzyl.constant.HttpConstant;
import site.bzyl.util.BaseContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* 拦截器写在 controller 包，因为拦截的都是表现层的控制器
* */
@Component
@Slf4j
public class BackendLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long employeeId = (Long) request.getSession().getAttribute(HttpConstant.CURRENT_LOGIN_EMPLOYEE_ID);
        Long userId = (Long) request.getSession().getAttribute(HttpConstant.CURRENT_LOGIN_EMPLOYEE_ID);
        // 员工已登陆
        if (employeeId != null) {
            BaseContext.setCurrentId(employeeId);
            return true;
        }

        // 用户已登陆
        if (employeeId != null) {
            BaseContext.setCurrentId(employeeId);
            return true;
        }

        // 未登录，打印拦截信息
        log.info("拦截到请求：{}", request.getRequestURI());
        // 后端自己重定向，视频里用过滤器有点麻烦，写的和前端耦合度太高
        response.sendRedirect("/backend/page/login/login.html");
        /* response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN"))); */
        return false;
    }
}
