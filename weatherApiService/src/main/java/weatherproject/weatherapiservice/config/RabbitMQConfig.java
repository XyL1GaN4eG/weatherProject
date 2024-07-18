package weatherproject.weatherapiservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Имя обменника
    public static final String EXCHANGE_NAME = "weather-exchange";
    // Имя очередей
    public static final String REQUEST_QUEUE_NAME = "weather-request-queue";
    public static final String NOTIFICATION_QUEUE_NAME = "weather-notification-queue";
    public static final String RESPONSE_QUEUE_NAME = "weather-response-queue";
    // Маршрутизирующие ключи для запросов и ответов
    public static final String ROUTING_KEY_REQUEST = "weather.request";
    public static final String ROUTING_KEY_RESPONSE = "weather.response";
    public static final String ROUTING_KEY_NOTIFICATION = "weather.notification";




    // Создаем новую очередь для уведомлений с постгрес о среднем значении
    @Bean
    public Queue weatherNotificationQueue() {
        return new Queue(NOTIFICATION_QUEUE_NAME);
    }
    @Bean
    public Queue weatherRequestQueue() {
        return new Queue(REQUEST_QUEUE_NAME);
    }

    @Bean
    public Queue weatherResponseQueue() {
        return new Queue(RESPONSE_QUEUE_NAME);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Привязка очереди запросов к обменнику с маршрутизирующим ключом
    @Bean
    public Binding bindingRequest(Queue weatherRequestQueue, TopicExchange exchange) {
        return BindingBuilder.bind(weatherRequestQueue).to(exchange).with(ROUTING_KEY_REQUEST);
    }

    // Привязка очереди ответов к обменнику с маршрутизирующим ключом
    @Bean
    public Binding bindingResponse(Queue weatherResponseQueue, TopicExchange exchange) {
        return BindingBuilder.bind(weatherResponseQueue).to(exchange).with(ROUTING_KEY_RESPONSE);
    }

    // Привязываем очередь уведомлений с постгрес к обменнику
    @Bean
    public Binding bindingNotification(Queue weatherNotificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(weatherNotificationQueue).to(exchange).with(ROUTING_KEY_NOTIFICATION);
    }

    // Настройка шаблона для отправки сообщений
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
