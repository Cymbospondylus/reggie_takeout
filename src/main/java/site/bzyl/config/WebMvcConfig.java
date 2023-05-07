package site.bzyl.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import site.bzyl.commom.JacksonObjectMapper;
import site.bzyl.controller.interceptor.FieldFillInterceptor;
import site.bzyl.controller.interceptor.LoginInterceptor;

import java.util.List;


@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired /* 和添加资源直接输入路径不同，添加拦截器需要Interceptor对象，要自己注入 */
    private LoginInterceptor loginInterceptor;

    @Autowired
    private FieldFillInterceptor fieldFillInterceptor;

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
         * 登录校验，未登录的用户会被重定向到登录界面
         */
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/backend/page/**", "/backend/index.html")
                .addPathPatterns("/employee")
                .addPathPatterns("/category")
                .excludePathPatterns("/backend/page/login/**");

        /**
         * 在新增和修改员工时，用拦截器向ThreadLocal变量中存入当前操作者id, 用于填充公共字段
         */
        registry.addInterceptor(fieldFillInterceptor)
                .addPathPatterns("/employee")
                .addPathPatterns("/category");
    }



    /**
     * 扩展SpringMVC框架的消息转换器
     * @param converters
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
