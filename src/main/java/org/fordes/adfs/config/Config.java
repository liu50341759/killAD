package org.fordes.adfs.config;

import lombok.Data;
import org.fordes.adfs.util.BloomFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fordes on 2024/4/9
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application.config")
public class Config {

    private Double faultTolerance = 0.0001;
    private Integer expectedQuantity = 2000000;
    private Integer warnLimit = 6;

    @Bean
    public BloomFilter<String> bloomFilter() {
        double falsePositiveProbability = Optional.ofNullable(faultTolerance).orElse(0.0001);
        int expectedNumberOfElements = Optional.ofNullable(expectedQuantity).orElse(2000000);
        return new BloomFilter<>(falsePositiveProbability, expectedNumberOfElements);
    }

    @Bean("producer")
    public ExecutorService producerExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean("consumer")
    public ExecutorService consumerExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean("singleExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor sentinel = new ThreadPoolTaskExecutorBuilder()
                .awaitTermination(false)
                .corePoolSize(1)
                .queueCapacity(1)
                .maxPoolSize(1)
                .threadNamePrefix("sentinel-")
                .build();
        sentinel.initialize();
        return sentinel;
    }
}