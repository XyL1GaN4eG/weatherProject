package weatherproject.tgbotservice.telegram.commands;

import lombok.RequiredArgsConstructor;
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
public class StartCommand implements Command {
    public final UserServiceClient userServiceClient;
    public final WeatherServiceClient weatherServiceClient;
    public GoogleTranslateClient translateClient;
    @Override
    public SendMessage apply(UserDTO currentUser, Update update) {
        var chatId = update.getMessage().getChatId();
        if (currentUser.getState().equals(UserState.HAVE_SETTED_CITY.toString())) {
            //Если у пользователя уже выставлен город, то говорим текущую погоду и предлагаем поставить новый город
            var weather = weatherServiceClient.getWeatherByCity(currentUser.getCity());
            //TODO: добавить перевод города и состояния температуры
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
