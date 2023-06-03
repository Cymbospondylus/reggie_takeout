package site.bzyl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import site.bzyl.commom.JacksonObjectMapper;
import site.bzyl.controller.interceptor.BackendFieldFillInterceptor;
import site.bzyl.controller.interceptor.BackendLoginInterceptor;
import site.bzyl.controller.interceptor.FrontendFieldFillInterceptor;
import site.bzyl.controller.interceptor.FrontendLoginInterceptor;

import java.util.List;


@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired /* 和添加资源直接输入路径不同，添加拦截器需要Interceptor对象，要自己注入 */
    private BackendLoginInterceptor backendLoginInterceptor;

    @Autowired
    private FrontendLoginInterceptor frontendLoginInterceptor;

    @Autowired
    private BackendFieldFillInterceptor backendFieldFillInterceptor;

    @Autowired
    private FrontendFieldFillInterceptor frontendFieldFillInterceptor;



    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 默认只能访问static和template目录下的静态资源，其他的要自己配置 「classpath:」指的是resources目录下
        // 配置静态资源映射
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");
    }


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        /**
         * 后台登录校验，未登录的用户会被重定向到登录界面
         */
        registry.addInterceptor(backendLoginInterceptor)
                .excludePathPatterns("/backend/page/login/**",  "/demo/upload.html")
                .addPathPatterns("/backend/page/**", "/backend/index.html", "/employee", "/category");

        /**
         * 前台登录校验
         */
        registry.addInterceptor(frontendLoginInterceptor)
                .excludePathPatterns("/front/page/login.html", "/demo/upload.html")
                // 必须是"/addressBook/**", 如果只拦截了"/addressBook"
                // 那么请求"http://localhost:8080/addressBook/default"并不会将当前id存入threadLocal
                .addPathPatterns(
                        "/front/page/**", "/front/index.html",
                        "/addressBook/**", "/shoppingCart/**",
                        "/order/**", "/dish/**", "/setmeal/**"
                );


        /**
         * 在新增和修改员工时，用拦截器向ThreadLocal变量中存入当前操作者id, 用于填充公共字段
         */
        registry.addInterceptor(backendFieldFillInterceptor)
                .addPathPatterns("/backend/**", "/employee/**", "/category/**", "/setmeal/**");

        /**
         * 修改地址等前台信息时，使用拦截器填充公共字段id
         */
        registry.addInterceptor(frontendFieldFillInterceptor)
                .addPathPatterns("/front")
                .addPathPatterns("/addressBook");
    }



    /**
     * 扩展SpringMVC框架的消息转换器
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建一个自定义的消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用JackSon将Java对象序列化为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将消息转换器对象追加到SpringMVC默认的转换器集合, index为0表示添加到首位，优先执行自定义的转换器
        converters.add(0, messageConverter);
    }
}
