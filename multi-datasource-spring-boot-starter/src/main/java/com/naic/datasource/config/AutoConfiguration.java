package com.naic.datasource.config;

import com.naic.datasource.bean.ApplicationContextProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

/**
 * @author HuZhenSha
 * @since 2021/11/1
 */
@Configuration
public class AutoConfiguration {

    @Bean
    public RequestContextListener requestContextListenerBean() {
        // 用来初始化 ApplicationContextProvider 上下文
        return new RequestContextListener();
    }

    @Bean
    public ApplicationContextProvider applicationContextProvider(){
        return new ApplicationContextProvider();
    }

    @Bean
    public FeignInterceptor feignInterceptor(){
        return new FeignInterceptor();
    }


}
