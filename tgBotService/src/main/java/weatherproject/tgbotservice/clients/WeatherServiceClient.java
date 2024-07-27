package weatherproject.tgbotservice.clients;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import weatherproject.tgbotservice.dto.WeatherDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceClient {

    private final RestTemplate restTemplate;
    private final GoogleTranslateClient translateClient;
    // URL для обращения к Weather API Service
    @Getter
    private final String baseUrl = "http://localhost:8081/weather";

    /**
     * Получение погоды по названию города.
     *
     * @param city Название города
     * @return Массив объектов погоды
     */
    public WeatherDTO getWeatherByCity(String city) {
        String url = getBaseUrl() + "/city/" + city;
        try {
            return restTemplate.getForObject(url, WeatherDTO.class);
        } catch (RestClientException e) {
            return null;
        }
    }

}
