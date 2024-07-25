package weatherproject.weatherapiservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import weatherproject.weatherapiservice.client.ApiClient;
import weatherproject.weatherapiservice.entity.CityWeather;
import weatherproject.weatherapiservice.repository.WeatherRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "weather.api")
@Slf4j
public class WeatherService {
    private final ApiClient apiClient;
    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherService(WeatherRepository weatherRepository, ApiClient apiClient) {
        this.weatherRepository = weatherRepository;
        this.apiClient = apiClient;
    }

    public Object[] processWeatherRequest(String city) {
        log.info("Начинаем собирать данные о погоде в городе: {}", city);
        var cityWeather = weatherRepository.findLatestByCity(city);
        log.info("Получены данные о погоде в городе {}: {}", city, cityWeather);
        // Если город не найден
        if (cityWeather == null || Duration.between(cityWeather.getUpdatedAt(), LocalDateTime.now()).toHours() > 1) {
            log.info("Город не найден или прошло больше часа с последнего обновления. Начинаем запрос к API");
            Object[] weatherData = apiClient.getWeather(city);
            if (weatherData != null) {
                log.info(
                        "Данные о погоде после запроса к API " +
                                "(в processWeatherRequest): {}, {}, {}",
                        weatherData[0],
                        weatherData[1],
                        weatherData[2]);
                try {
                    log.info("Попытка распарсить данные");
                    cityWeather = new CityWeather((String) weatherData[0], (Double) weatherData[1], (String) weatherData[2]);
                    weatherRepository.save(cityWeather);
                    log.info("Данные о погоде в городе {} сохранены в базу данных", city);
                    return weatherData;
                } catch (ClassCastException e) {
                    log.error("Данные о погоде пришли в некорректном формате:{}", e.getMessage());
                }
            } else {
                log.info("Город {} не найден, возвращаем null");
                return null;
            }
        }

        var result = new Object[3];
        //Данные точно не нулл, потому что мы это уже проверили ранее
        assert cityWeather != null;
        result[0] = cityWeather.getCity();
        result[1] = cityWeather.getTemperature();
        result[2] = cityWeather.getCondition();
        log.info("Возвращаем данные о погоде в городе");
        return result;
    }

    public List<CityWeather> getAllCitiesWeather() {
        return weatherRepository.findAll();
    }

    public void updateAllCitiesWeather() {
        var cities = weatherRepository.findAll();
        for (CityWeather cityWeather : cities) {

            processWeatherRequest(cityWeather.getCity());
        }
    }
}
