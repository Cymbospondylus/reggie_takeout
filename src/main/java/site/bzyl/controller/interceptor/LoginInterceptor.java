package site.bzyl.controller.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.bzyl.commom.Result;
import site.bzyl.constant.HttpConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* 拦截器写在 controller 包，因为拦截的都是表现层的控制器
* */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long id = (Long) request.getSession().getAttribute(HttpConstant.CURRENT_LOGIN_EMPLOYEE_ID);
        // 未登录
        if (id == null) {
            // 后端自己重定向，视频里用过滤器有点麻烦，写的和前端耦合度太高
            response.sendRedirect("/backend/page/login/login.html");
            log.info("拦截到请求：{}", request.getRequestURI());
            /* response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN"))); */
            return false;
        }
        // 已登录
        return true;
    }
}
