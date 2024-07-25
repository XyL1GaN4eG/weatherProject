package weatherproject.weatherapiservice.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
//TODO: переписать с использованием ResponseEntity, чтобы можно было более удобно передавать коды ошибок и дальше их обрабатывать
//TODO: но перед этим написать тесты хихи
@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
@Slf4j
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
        WeatherDTO weatherDTO = weatherService.processWeatherRequest(city);
        log.info("Отправляем ответ {} по рест апи", weatherDTO);
        return weatherDTO;
    }

}
