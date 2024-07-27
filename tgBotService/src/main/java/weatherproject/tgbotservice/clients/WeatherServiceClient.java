package weatherproject.tgbotservice.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import weatherproject.tgbotservice.dto.WeatherDTO;

@Service
@Slf4j
public class WeatherServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public WeatherServiceClient(RestTemplate restTemplate, @Value("${weather-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Получение погоды по названию города.
     *
     * @param city Название города
     * @return Массив объектов погоды
     */
    public WeatherDTO getWeatherByCity(String city) {
        String url = baseUrl + "/city/" + city;
        try {
            return restTemplate.getForObject(url, WeatherDTO.class);
        } catch (RestClientException e) {
            return null;
        }
    }

}
