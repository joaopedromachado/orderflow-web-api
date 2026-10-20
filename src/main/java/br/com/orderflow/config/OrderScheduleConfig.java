package br.com.orderflow.config;

import br.com.orderflow.service.order.OrderProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class OrderScheduleConfig {

    private static final String EVERY_MINUTE = "0 * * * * *";
    private final OrderProcessor orderProcessor;

    public OrderScheduleConfig(final OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }

    @Scheduled(cron = EVERY_MINUTE)
    public void start() {
        this.orderProcessor.process();
    }
}
