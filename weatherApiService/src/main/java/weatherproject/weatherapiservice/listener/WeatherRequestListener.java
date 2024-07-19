package weatherproject.weatherapiservice.listener;

import weatherproject.weatherapiservice.config.RabbitMQConfig;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import weatherproject.weatherapiservice.service.WeatherService;

@Component
public class WeatherRequestListener {
    private final WeatherService weatherService;
    private final RabbitTemplate rabbitTemplate;

    @Value(RabbitMQConfig.RESPONSE_QUEUE_NAME)
    private String responseQueue;

    public WeatherRequestListener(WeatherService weatherService, RabbitTemplate rabbitTemplate) {
        this.weatherService = weatherService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.REQUEST_QUEUE_NAME)
    public void receiveMessages(String city) {
        Object[] weather = weatherService.processWeatherRequest(city);
        rabbitTemplate.convertAndSend(responseQueue, weather);
    }
}
