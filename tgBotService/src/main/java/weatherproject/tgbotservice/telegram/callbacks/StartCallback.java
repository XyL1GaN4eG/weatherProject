package weatherproject.tgbotservice.telegram.callbacks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;

import java.util.Objects;

import static weatherproject.tgbotservice.utils.Constants.CITY_NOT_FOUND;
import static weatherproject.tgbotservice.utils.Constants.FIRST_CITY_SET;

@RequiredArgsConstructor
@Component
@Slf4j
public class StartCallback implements Callback {

    private final GoogleTranslateClient translateClient;
    private final UserServiceClient userServiceClient;
    @Override
    public String execute(UserDTO user, WeatherDTO weatherDTO) {
        // Если weatherDTO не пустой
        if (!Objects.equals(weatherDTO, new WeatherDTO())) {
            user.setCity(weatherDTO.getCity());
            userServiceClient.createOrUpdateUser(user);
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
