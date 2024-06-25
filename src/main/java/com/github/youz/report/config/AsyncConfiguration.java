package com.github.youz.report.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@Log4j2
@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    public static final String THREAD_NAME_PREFIX = "report-async";

    public static final int KEEP_ALIVE_SECONDS = 60;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processNum = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processNum);
        executor.setMaxPoolSize(processNum);
        executor.setQueueCapacity(processNum);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new ReportAsyncExceptionHandler();
    }

    private static class ReportAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            log.error("Async method exception!", ex);
        }
    }
}
