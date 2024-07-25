package weatherproject.weatherapiservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import weatherproject.weatherapiservice.entity.CityWeather;
import weatherproject.weatherapiservice.service.WeatherService;

import java.util.List;


//Если вдруг понадобится рест апи то дописать
@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public List<CityWeather> getWeather() {
        return weatherService.getAllCitiesWeather();
    }

    @GetMapping("/city/{city}")
    public Object[] getWeatherByCity(@PathVariable String city) {
        return weatherService.processWeatherRequest(city);
    }

}
