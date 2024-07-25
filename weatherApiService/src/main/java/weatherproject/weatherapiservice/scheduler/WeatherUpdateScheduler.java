package weatherproject.weatherapiservice.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import weatherproject.weatherapiservice.service.WeatherService;

@Service
@Slf4j
public class WeatherUpdateScheduler {
    private final WeatherService weatherService;

    public WeatherUpdateScheduler(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    //Каждый час сохраняет погоду в БД, чтобы если вдруг сработал триггер
    @Scheduled(cron = "58 * * * * *")
    public void updateWeather() {
        log.info("Ежечасное обновление погоды началось...");
        weatherService.updateAllCitiesWeather();
    }

}
