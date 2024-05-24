package com.example.servicoderemessawallet.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String TRANSACTION_QUEUE = "transactionQueue";

    @Bean
    public Queue transactionQueue() {
        return new Queue(TRANSACTION_QUEUE, true);
    }
}
