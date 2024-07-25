package weatherproject.tgbotservice.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WeatherServiceClient {

    private final RestTemplate restTemplate;

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
    public Object[] getWeatherByCity(String city) {
        String url = baseUrl + "/city/" + city;
        return restTemplate.getForObject(url, Object[].class);
    }

    public String getFormattedWeatherByCity(String city) {
        var unformattedWeather = getWeatherByCity(city);

        //TODO: вынести в константы "текущая погода" и "погода не найдена"
        if (unformattedWeather != null) {
            return String.format("Текущая погода в городе %s: %s, %s", city,
                    GoogleTranslateClient.translateEngToRussian(unformattedWeather[1].toString()),
                    GoogleTranslateClient.translateEngToRussian(unformattedWeather[2].toString()));
        }
        return String.format("Погода в городе %s не найдена", city);
    }
}
