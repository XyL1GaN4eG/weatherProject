package weatherproject.tgbotservice.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import weatherproject.tgbotservice.dto.WeatherDTO;

@Service
@RequiredArgsConstructor
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
        return new WeatherDTO(restTemplate.getForObject(url, Object[].class));
    }

    public String getFormattedWeatherByCity(String city) {
        var weather = getWeatherByCity(city);

        if (weather != null) {
            return String.format("погода в городе %s: %s, %s",
                    translateClient.translateEngToRussian(city),
                    weather.getTemperature(),
                    translateClient.translateEngToRussian(weather.getCondition()));
        }
        return null;
    }
}
