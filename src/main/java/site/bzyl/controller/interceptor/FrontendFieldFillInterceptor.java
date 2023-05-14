package site.bzyl.controller.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.bzyl.constant.HttpConstant;
import site.bzyl.constant.SystemConstant;
import site.bzyl.util.BaseContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 前台将用户id存入session用于填充字段的拦截器
 */
@Component
public class FrontendFieldFillInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId = (Long) request.getSession().getAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
        BaseContext.setCurrentId(userId);
        return true;
    }
}
