package weatherproject.userservice.config;

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
    public static final String USER_EXCHANGE_NAME = "user-exchange";

    // Имена очередей
    public static final String USER_GET_QUEUE_NAME = "user-get-queue";
    public static final String USER_GET_RESPONSE_QUEUE_NAME = "user-get-response-queue";
    public static final String USER_CREATED_QUEUE_NAME = "user-created-queue";
    public static final String USER_DELETED_QUEUE_NAME = "user-deleted-queue";

    // Маршрутизирующие ключи для запросов и ответов
    public static final String ROUTING_KEY_USER_GET = "user.get";
    public static final String ROUTING_KEY_USER_GET_RESPONSE = "user.get.response";

    // Создание очередей
    @Bean
    public Queue userGetQueue() {
        return new Queue(USER_GET_QUEUE_NAME);
    }

    @Bean
    public Queue userGetResponseQueue() {
        return new Queue(USER_GET_RESPONSE_QUEUE_NAME);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(USER_CREATED_QUEUE_NAME);
    }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue(USER_DELETED_QUEUE_NAME);
    }

    // Создание обменника
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE_NAME);
    }

    // Привязка очередей к обменнику с маршрутизацией
    @Bean
    public Binding bindingUserGet(Queue userGetQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userGetQueue).to(userExchange).with(ROUTING_KEY_USER_GET);
    }

    @Bean
    public Binding bindingUserGetResponse(Queue userGetResponseQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userGetResponseQueue).to(userExchange).with(ROUTING_KEY_USER_GET_RESPONSE);
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
