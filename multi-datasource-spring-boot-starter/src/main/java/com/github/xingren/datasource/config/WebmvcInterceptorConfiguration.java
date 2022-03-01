package com.github.xingren.datasource.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * springboot interceptors configuration class
 * @author HuZhenSha
 * @date 2021/7/29 12:01
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebmvcInterceptorConfiguration implements WebMvcConfigurer{
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new DataSourceInterceptor());
    }
}