package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Web 层统一配置类。
 *
 * <p>继承 WebMvcConfigurationSupport 后，Spring MVC 的默认配置会失效，
 * 由本类接管拦截器注册、静态资源映射等 Web 层组件。</p>
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    /** 管理端 JWT 校验拦截器，用于拦截 /admin/** 下的请求 */
    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器。
     *
     * <p>拦截 /admin/** 下的所有请求做令牌校验，但排除登录接口 ——
     * 登录时用户还没有令牌，若被拦截就永远登不进去。</p>
     *
     * @param registry 拦截器注册器，由 Spring MVC 注入
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                // 管理端接口统一走 /admin 前缀
                .addPathPatterns("/admin/**")
                // 登录接口必须放行，否则无法获取令牌
                .excludePathPatterns("/admin/employee/login");
    }

    /**
     * 通过 knife4j 生成接口文档。
     *
     * <p>启动后访问 http://localhost:8080/doc.html 查看在线接口文档。</p>
     *
     * @return Docket 是 springfox 的文档汇总对象
     */
    @Bean
    public Docket docket() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                // 只扫描 com.sky.controller 包下的接口，避免把框架自带接口也收录进来
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 设置静态资源映射。
     *
     * <p>knife4j 的文档页面与前端资源都在 jar 包的 META-INF/resources 下，
     * 必须手动映射，否则 /doc.html 会 404。</p>
     *
     * @param registry 资源处理器注册器，由 Spring MVC 注入
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 文档页面：http://localhost:8080/doc.html
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        // 文档页面依赖的 js/css 等静态资源
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
