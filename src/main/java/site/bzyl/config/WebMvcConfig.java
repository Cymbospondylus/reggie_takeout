package site.bzyl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import site.bzyl.controller.interceptor.LoginInterceptor;


@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired /* 和添加资源直接输入路径不同，添加拦截器需要Interceptor对象，要自己注入 */
    private LoginInterceptor loginInterceptor;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("MvcConfig配置静态资源...");
        // 默认只能访问static和template目录下的静态资源，其他的要自己配置 「classpath:」指的是resources目录下
        // 配置静态资源映射
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/backend/page/**", "/backend/index.html")
                .excludePathPatterns("/backend/page/login/**");
    }
}
