package com.spzx.channel.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
// @SpringBootConfiguration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        int processorsCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                processorsCount * 2,
                processorsCount * 2,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(processorsCount * 10),
                Executors.defaultThreadFactory(),
                (runnable, executor) -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    executor.submit(runnable);
                }
        );
        threadPoolExecutor.prestartAllCoreThreads();
        return threadPoolExecutor;
    }
}
