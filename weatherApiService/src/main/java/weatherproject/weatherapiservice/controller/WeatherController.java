package weatherproject.weatherapiservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import weatherproject.weatherapiservice.dto.WeatherDTO;
import weatherproject.weatherapiservice.entity.WeatherEntity;
import weatherproject.weatherapiservice.service.WeatherService;

import java.util.ArrayList;
import java.util.List;


//Если вдруг понадобится рест апи то дописать
@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public List<WeatherDTO> getWeather() {
        var weatherList = new ArrayList<WeatherDTO>();
        for (WeatherEntity weather : weatherService.getAllCitiesWeather()) {
            weatherList.add(new WeatherDTO(weather));
        }
        return weatherList;
    }

    @GetMapping("/city/{city}")
    public WeatherDTO getWeatherByCity(@PathVariable String city) {
        return weatherService.processWeatherRequest(city);
    }

}
