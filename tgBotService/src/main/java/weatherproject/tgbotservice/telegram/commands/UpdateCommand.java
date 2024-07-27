package weatherproject.tgbotservice.telegram.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;

import static weatherproject.tgbotservice.utils.Constants.ALREADY_SET_CITY;
import static weatherproject.tgbotservice.utils.Constants.CITY_NOT_SET;

//Принудительное обновление погоды
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateCommand implements Command {
    private final WeatherServiceClient weatherServiceClient;
    private final GoogleTranslateClient translateClient;

    @Override
    public SendMessage apply(UserDTO currentUser, Update update) {
        if (currentUser.getCity().equals("null")) {
            return (new SendMessage(update.getMessage().getChatId().toString(), CITY_NOT_SET));
        } else {
            log.info("Получаем погоду для пользователя {} для города {}", currentUser.getChatId(), currentUser.getCity());
            var weather = weatherServiceClient.getWeatherByCity(currentUser.getCity());
            log.info("Успешно получена погода для пользователя {}: {}", currentUser.getChatId(), weather);
            return new SendMessage(update.getMessage().getChatId().toString(), String.format(
                    ALREADY_SET_CITY,
                    translateClient.translateEngToRussian(weather.getCity()),
                    weather.getTemperature().toString(),
                    translateClient.translateEngToRussian(weather.getCondition())));
        }
    }
}
