package com.ideiasmidias.media.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncMediaConfig {

    /**
     * Small, dedicated pool for video transcoding so a burst of uploads never
     * starves the web server's own request-handling threads. ffmpeg is
     * CPU-heavy, so this is deliberately kept small.
     */
    @Bean(name = "videoTranscodeExecutor")
    public Executor videoTranscodeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("video-transcode-");
        executor.initialize();
        return executor;
    }

    /**
     * Recording a page view is a single cheap insert, but it must never
     * slow down or fail the public request it's piggybacking on — so it
     * always runs off-thread on its own small pool.
     */
    @Bean(name = "pageViewExecutor")
    public Executor pageViewExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("page-view-");
        executor.initialize();
        return executor;
    }
}
