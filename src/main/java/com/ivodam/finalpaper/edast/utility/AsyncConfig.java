//package com.ivodam.finalpaper.edast.utility;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//
//@Configuration
//public class AsyncConfig {
//
//    @Bean("taskExecutor")
//    public TaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5); // Set the number of core threads as per your requirement
//        executor.setMaxPoolSize(10); // Set the maximum number of threads as per your requirement
//        executor.setThreadNamePrefix("AsyncThread-");
//        executor.initialize();
//        return executor;
//    }
//}
