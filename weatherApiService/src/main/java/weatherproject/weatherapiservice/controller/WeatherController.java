package weatherproject.weatherapiservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

}