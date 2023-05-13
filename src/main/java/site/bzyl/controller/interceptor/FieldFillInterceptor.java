package site.bzyl.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import site.bzyl.constant.HttpConstant;
import site.bzyl.util.BaseContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 在新增和修改员工时，用拦截器向ThreadLocal变量中存入当前操作者id, 用于填充公共字段
 */
@Component
@Slf4j
public class FieldFillInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long id = (Long) request.getSession().getAttribute(HttpConstant.CURRENT_LOGIN_EMPLOYEE_ID);
        BaseContext.setCurrentId(id);
        return true;
    }
}
