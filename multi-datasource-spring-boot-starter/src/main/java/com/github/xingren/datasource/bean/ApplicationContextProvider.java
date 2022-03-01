package com.github.xingren.datasource.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;

/**
 * @author HuZhenSha
 * @since 2021/10/28
 */
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public synchronized void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    /**
     * 通过class 获取Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过name获取 Bean.
     */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }
}
