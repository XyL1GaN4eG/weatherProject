package weatherproject.tgbotservice.telegram.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;
import weatherproject.tgbotservice.utils.Constants;

import static weatherproject.tgbotservice.utils.Constants.*;
@RequiredArgsConstructor
@Component
@Slf4j
public class StartCommand implements Command {
    private final UserServiceClient userServiceClient;
    private final WeatherServiceClient weatherServiceClient;
    private final GoogleTranslateClient translateClient;

    //TODO: добавить кнопку с запросом геолокации пользователя
    @Override
    public SendMessage apply(UserDTO currentUser, Update update) {
        log.info("Вызвана команда START для {}", currentUser);
        var chatId = update.getMessage().getChatId();
        if (currentUser.getState().equals(UserState.HAVE_SETTED_CITY.toString())) {
            log.info("У пользователя {} уже поставлен город {}", currentUser.getChatId(), currentUser.getCity());
            //Если у пользователя уже выставлен город, то говорим текущую погоду и предлагаем поставить новый город
            var weather = weatherServiceClient.getWeatherByCity(currentUser.getCity());
            log.info("Возвращаем пользователю {} погоду: {}", currentUser.getChatId(), weather);
            //TODO: переписать с replace на string format
            return (new SendMessage(chatId.toString(), ALREADY_SET_CITY
                    .replace("{city}", translateClient.translateEngToRussian(currentUser.getCity()))
                    .replace("{temperature}", weather.getTemperature().toString())
                    .replace("{condition}", translateClient.translateEngToRussian(weather.getCondition()))
            ));
        } else {
            //Если нет, то просто добавляем пользователя в бд и ставим нулл город
            userServiceClient.createOrUpdateUser(new UserDTO(chatId, "null", UserState.START.toString()));
        }
        return new SendMessage(chatId.toString(), Constants.START_MESSAGE);
    }
}
