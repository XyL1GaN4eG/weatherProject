package weatherproject.weatherapiservice.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import weatherproject.weatherapiservice.service.WeatherService;

@Component
@Slf4j
public class WeatherUpdateScheduler {
    private final WeatherService weatherService;

    public WeatherUpdateScheduler(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    //Каждый час сохраняет погоду в БД, чтобы если вдруг сработал триггер
    @Scheduled(cron = "0 0 * * * *")
    public void updateWeather() {
        log.info("Ежечасное обновление погоды началось...");
        weatherService.updateAllCitiesWeather();
    }

}
