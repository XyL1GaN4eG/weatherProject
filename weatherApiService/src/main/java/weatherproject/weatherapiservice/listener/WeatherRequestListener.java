package weatherproject.weatherapiservice.listener;

import lombok.extern.slf4j.Slf4j;
import weatherproject.weatherapiservice.config.RabbitMQConfig;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import weatherproject.weatherapiservice.service.WeatherService;

@Component
@Slf4j
public class WeatherRequestListener {
    private final WeatherService weatherService;
    private final RabbitTemplate rabbitTemplate;

    @Value(RabbitMQConfig.RESPONSE_QUEUE)
    private String responseQueue;

    public WeatherRequestListener(WeatherService weatherService, RabbitTemplate rabbitTemplate) {
        this.weatherService = weatherService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.REQUEST_QUEUE)
    public void receiveRequest(String city) {
        log.info("Получено сообщение от внешнего микросервиса получить погоду по городу: {}", city);
        Object[] weather = weatherService.processWeatherRequest(city);
        rabbitTemplate.convertAndSend(responseQueue, weather);
    }
}
