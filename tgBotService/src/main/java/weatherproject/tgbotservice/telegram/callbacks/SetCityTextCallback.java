package weatherproject.tgbotservice.telegram.callbacks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;

import static weatherproject.tgbotservice.utils.Constants.ILLEGAL_CITY;
import static weatherproject.tgbotservice.utils.Constants.NEW_CITY_SETTED;

@RequiredArgsConstructor
@Component
@Slf4j
public class SetCityTextCallback implements Callback {
    private final WeatherServiceClient weatherServiceClient;
    private final GoogleTranslateClient translateClient;
    private final UserServiceClient userServiceClient;


    @Override
    public String execute(UserDTO user, WeatherDTO weatherDTO) {
        log.info("Начинаем обрабатывать коллбек setCity от пользователя {} со следующей новой полученной погодой: {}", user.toString(), weatherDTO.toString());

        // если погода не пустая
        if (!weatherDTO.equals(new WeatherDTO())) {
            user.setCity(weatherDTO.getCity());
            userServiceClient.createOrUpdateUser(user);
            return String.format(NEW_CITY_SETTED,
                    (Object[]) weatherDtoToArray(weatherDTO));
        }
        WeatherDTO newWeatherDTO = weatherServiceClient.getWeatherByCity(user.getCity());
        return String.format(ILLEGAL_CITY,
                (Object[]) weatherDtoToArray(newWeatherDTO));
    }

    private String[] weatherDtoToArray(WeatherDTO weatherDTO) {
        var translatedWeather = translateClient.translateEngToRussian(weatherDTO.getCity() + ", " + weatherDTO.getCondition()).split(", ");
        return new String[]{
                translatedWeather[0],
                weatherDTO.getTemperature().toString(),
                translatedWeather[1]};
    }
}
