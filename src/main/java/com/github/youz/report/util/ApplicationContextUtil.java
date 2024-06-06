package com.github.youz.report.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring容器上下文工具类
 */
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

}
