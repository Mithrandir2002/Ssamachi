package com.earthquake.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class IngestionExecutorConfig {

    @Bean(name = "ingestionTaskExecutor")
    public ThreadPoolTaskExecutor ingestionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core size IS the real parallelism here. A ThreadPoolExecutor only creates
        // threads beyond the core size once the queue is full, so with a 100-slot queue
        // and ~80 chunks the max size would never be reached: leaving core at 2 would
        // cap the whole backfill at two concurrent fetches.
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);

        // Four concurrent requests is already generous towards USGS, which serves this
        // API for free and without an API key. Firing all 80 windows at once is how you
        // get throttled.
        executor.setQueueCapacity(100);

        // If the queue ever does fill up, run the task on the submitting thread instead
        // of throwing it away — the default AbortPolicy would drop the work silently
        // from the caller's point of view.
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setThreadNamePrefix("ingestion-");
        executor.initialize();
        return executor;
    }
}
