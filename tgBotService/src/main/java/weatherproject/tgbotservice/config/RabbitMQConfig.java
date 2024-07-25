package weatherproject.tgbotservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class RabbitMQConfig {

    public static final String NOTIFICATION_QUEUE = "weather-notification-queue";

    @Bean
    public Queue weatherNotificationQueue() {
        return new Queue(NOTIFICATION_QUEUE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
