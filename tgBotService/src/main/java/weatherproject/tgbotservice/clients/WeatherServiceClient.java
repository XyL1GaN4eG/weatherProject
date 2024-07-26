package weatherproject.tgbotservice.clients;

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
    private final String baseUrl = "http://localhost:8081/weather";

    /**
     * Получение погоды для всех городов.
     *
     * @return Список объектов CityWeather
     */
//    public List<Object[]> getAllCitiesWeather() {
//        String url = baseUrl;
//        Object[] response = restTemplate.getForObject(url, Object[].class);
//        return Arrays.asList(response);
//    }

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
