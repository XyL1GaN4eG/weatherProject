package weatherproject.weatherapiservice.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import weatherproject.weatherapiservice.client.ApiClient;
import weatherproject.weatherapiservice.entity.CityWeather;
import weatherproject.weatherapiservice.repository.WeatherRepository;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "weather.api")
public class WeatherService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(WeatherService.class);
    private final ApiClient apiClient = new ApiClient();
    private WeatherRepository weatherRepository;

    @Value("$weather.api.url")
    private String url;

    public Object[] processWeatherRequest(String city) {
        CityWeather cityWeather = weatherRepository.findByCity(city);

        //Если город не найден
        if (cityWeather == null) {
            Object[] weatherData = apiClient.getWeather(city);
            try {
                cityWeather = new CityWeather((Long) weatherData[0], (String) weatherData[1], (Double) weatherData[2], (String) weatherData[3]);
                weatherRepository.save(cityWeather);
                return weatherData;
            } catch (ClassCastException e) {
                log.error("Данные о погоде пришли в некорректном формате", e.getMessage());
            }
        }

        var result = new Object[3];
        assert cityWeather != null;
        result[0] = cityWeather.getCity();
        result[1] = cityWeather.getTemperature();
        result[2] = cityWeather.getCondition();
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
