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
        log.info("Получены существующие данные о погоде в городе {}: {}", city, cityWeather);

        // Если город не найден
        boolean isCityNull = cityWeather == null;
        boolean isEnoughTimeBetweenUpdates = false;
        //..или если город найден, но прошло слишком мало времени между запросами на город
        if (cityWeather != null) {
            isEnoughTimeBetweenUpdates = Duration.between(cityWeather.getUpdatedAt(), LocalDateTime.now()).toHours() > 1;
        }
        if (isCityNull || isEnoughTimeBetweenUpdates) {
            try {
                log.info("Город не найден {} или прошло больше часа с последнего обновления {}. Начинаем запрос к API",
                        cityWeather == null,
                        Duration.between(cityWeather.getUpdatedAt(), LocalDateTime.now()).toMinutes() > 30);
            } catch (NullPointerException ignored) {
            }

            // обращаемся к апи погоды
            Object[] weatherData = apiClient.getWeather(city);
            // если город найден и данные пришли
            if (weatherData != null) {
                log.info(
                        "Данные о погоде после запроса к API " +
                                "(в processWeatherRequest): {}, {}, {}",
                        weatherData[0],
                        weatherData[1],
                        weatherData[2]);
                try {
                    log.info("Попытка распарсить данные");
                    cityWeather = new WeatherEntity(weatherData);
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
            // если данные с апи погоды не пришли
            return null;

        } else {
            // если не прошло достаточно времени или город вообще не найден
            return new WeatherDTO(cityWeather);
        }
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
