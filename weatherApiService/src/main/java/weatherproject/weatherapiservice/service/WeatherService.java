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

        if (isNeedToUpdateDataOrNot(city, cityWeather)) {
            // обращаемся к апи погоды
            Object[] weatherData = apiClient.getWeather(city);

            // если город найден и данные пришли
            return parseWeatherFromApi(weatherData);

        } else {
            log.info("Отправляем уже существующие данные о погоде: {}", cityWeather);
            // если не прошло достаточно времени или город вообще не найден
            return new WeatherDTO(cityWeather);
        }
    }

    private WeatherDTO parseWeatherFromApi(Object[] weatherData) {
        WeatherEntity cityWeather;
        log.info("Данные о погоде после запроса к API " +
                        "(в processWeatherRequest): {}, {}, {}",
                weatherData[0],
                weatherData[1],
                weatherData[2]);
        try {
            log.info("Попытка распарсить данные");
            cityWeather = new WeatherEntity(weatherData);
            try {
                weatherRepository.save(cityWeather);
                log.info("Данные о погоде в городе {} сохранены в базу данных", cityWeather.getCity());
            } catch (Exception e) {
                log.error("Произошла ошибка при сохранении данных в таблицу: {}", e.getMessage());
            }
            return new WeatherDTO(cityWeather);
        } catch (ClassCastException e) {
            log.error("Данные о погоде пришли в некорректном формате:{}", e.getMessage());
        }
        return null;
    }

    private static boolean isNeedToUpdateDataOrNot(String city, WeatherEntity cityWeather) {
        // Если город не найден
        boolean isCityNull = cityWeather == null;
        boolean isEnoughTimeBetweenUpdates = false;
        //..или если город найден, но прошло слишком мало времени между запросами на город
        if (cityWeather != null) {
            log.info("Получены существующие данные о погоде в городе {}: {}", city, cityWeather);
            isEnoughTimeBetweenUpdates = Duration.between(cityWeather.getUpdatedAt(), LocalDateTime.now()).toHours() > 1;
            log.info("Времени с последнего обновления прошло: {}", Duration.between(cityWeather.getUpdatedAt(), LocalDateTime.now()));
        }

        return isCityNull || isEnoughTimeBetweenUpdates;
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
