package weatherproject.weatherapiservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import weatherproject.weatherapiservice.client.ApiClient;
import weatherproject.weatherapiservice.dto.WeatherDTO;
import weatherproject.weatherapiservice.entity.WeatherEntity;
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

    public WeatherDTO processWeatherRequest(String city) {
        log.info("Начинаем собирать данные о погоде в городе: {}", city);
        var cityWeather = weatherRepository.findLatestByCity(city);
        log.info("Получены данные о погоде в городе {}: {}", city, cityWeather);
        // Если город не найден
        if (cityWeather == null || Duration.between(cityWeather.getUpdatedAt(), LocalDateTime.now()).toMinutes() > 30) {
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
                    cityWeather = new WeatherEntity((String) weatherData[0], (Double) weatherData[1], (String) weatherData[2]);
                    try {
                        weatherRepository.save(cityWeather);
                        log.info("Данные о погоде в городе {} сохранены в базу данных", city);
                    } catch (Exception e) {
                        log.error("Произошла ошибка при сохранении данных в таблицу: {}", e.getMessage());
                    }
                    return new WeatherDTO(cityWeather);
                } catch (ClassCastException e) {
                    log.error("Данные о погоде пришли в некорректном формате:{}", e.getMessage());
                }

            }
        } else {
            return new WeatherDTO(cityWeather);
        }
        return null;
    }

    public List<WeatherEntity> getAllCitiesWeather() {
        return weatherRepository.findAll();
    }

    public void updateAllCitiesWeather() {
        var cities = weatherRepository.findAll();
        for (WeatherEntity weatherEntity : cities) {
            processWeatherRequest(weatherEntity.getCity());
        }
    }
}
