package weatherproject.weatherapiservice.service;

import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import weatherproject.weatherapiservice.client.ApiClient;
import weatherproject.weatherapiservice.entity.CityWeather;
import weatherproject.weatherapiservice.repository.WeatherRepository;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "weather.api")
public class WeatherService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WeatherService.class);
    private ApiClient apiClient;
    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public Object[] processWeatherRequest(String city) {
        log.info("Начинаем собирать данные о погоде в городе: {}", city);
        CityWeather cityWeather = weatherRepository.findByCity(city);
        log.info("Полученные данные о погоде:", cityWeather);
        // Если город не найден
        if (cityWeather == null) {
            log.info("Город не найден. Начинаем запрос к API");
            Object[] weatherData = apiClient.getWeather(city);
            log.info("Данные о погоде после запроса к API (в processWeatherRequest): {}", weatherData);
            try {
                log.info("Попытка распарсить данные");
                cityWeather = new CityWeather((Long) weatherData[0], (String) weatherData[1], (Double) weatherData[2], (String) weatherData[3]);
                weatherRepository.save(cityWeather);
                log.info("Данные о погоде в городе {} сохранены в базу данных", city);
                return weatherData;
            } catch (ClassCastException e) {
                log.error("Данные о погоде пришли в некорректном формате:{}", e.getMessage());
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
            var weatherDataFromApi = apiClient.getWeather(cityWeather.getCity());
            cityWeather.setCity(weatherDataFromApi[0].toString());
            cityWeather.setTemperature((Double) weatherDataFromApi[1]);
            cityWeather.setCondition((String) weatherDataFromApi[2]);
            weatherRepository.save(cityWeather);
        }
    }
}
