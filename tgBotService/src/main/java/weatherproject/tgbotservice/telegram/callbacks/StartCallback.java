package weatherproject.tgbotservice.telegram.callbacks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;

import static weatherproject.tgbotservice.utils.Constants.CITY_NOT_FOUND;
import static weatherproject.tgbotservice.utils.Constants.FIRST_CITY_SET;

@RequiredArgsConstructor
@Component
@Slf4j
public class StartCallback implements Callback {

    private final GoogleTranslateClient translateClient;

    @Override
    public String execute(UserDTO user, WeatherDTO weatherDTO) {
        if (weatherDTO != null) {
            return String.format(FIRST_CITY_SET, (Object[]) weatherDtoToArray(weatherDTO));
        }
        return (CITY_NOT_FOUND);
    }

    private String[] weatherDtoToArray(WeatherDTO weatherDTO) {
        var translatedWeather = translateClient.translateEngToRussian(weatherDTO.getCity() + ", " + weatherDTO.getCondition()).split(", ");
        return new String[]{
                translatedWeather[0],
                weatherDTO.getTemperature().toString(),
                translatedWeather[1]};
    }
}
