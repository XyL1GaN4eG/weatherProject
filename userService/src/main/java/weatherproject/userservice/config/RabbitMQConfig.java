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


    //Имя обменника
    public static final String EXCHANGE_NAME = "user-exchange";
    //Имя очереди
    public static final String QUEUE_NAME = "user-queue";
    // Маршрутизирующие ключи для разных операций
    public static final String ROUTING_KEY_CREATED = "user.created";
    public static final String ROUTING_KEY_UPDATED = "user.updated";
    public static final String ROUTING_KEY_DELETED = "user.deleted";


    //Создает и возвращает новую очередь с именем "user-queue".
    @Bean
    public Queue userQueue() {
        return new Queue(QUEUE_NAME);
    }

    //Создает и возвращает новый обменник типа "topic" с именем "user-exchange".
    // Обменник типа "topic" позволяет отправлять сообщения в различные очереди,
    // основываясь на ключе маршрутизации (routing key).
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    //Привязка очереди к обменнику с маршрутизирующим ключом для создания пользователей
    @Bean
    public Binding bindingCreated(Queue userQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding bindingUpdated(Queue userQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with(ROUTING_KEY_UPDATED);
    }

    @Bean
    public Binding bindingDeleted(Queue userQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with(ROUTING_KEY_DELETED);
    }

    //Настройка шаблона для отправки сообщений
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
