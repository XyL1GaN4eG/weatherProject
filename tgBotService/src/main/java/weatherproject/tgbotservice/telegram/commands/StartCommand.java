package weatherproject.tgbotservice.telegram.commands;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;
import weatherproject.tgbotservice.utils.Constants;

import static weatherproject.tgbotservice.utils.Constants.ALREADY_USER;
@RequiredArgsConstructor
@Component
public class StartCommand implements Command {
    public final UserServiceClient userServiceClient;
    public final WeatherServiceClient weatherServiceClient;
    @Override
    public SendMessage apply(UserDTO currentUser, Update update) {
        var chatId = update.getMessage().getChatId();
        if (currentUser.getState().equals("ALREADY_USER")) {
            //Если у пользователя уже выставлен город, то говорим текущую погоду и предлагаем поставить новый город
            return (new SendMessage(chatId.toString(), ALREADY_USER
                    .replace("{city}", currentUser.getCity())
                    .replace("{weather}", weatherServiceClient.getFormattedWeatherByCity(currentUser.getCity()))
            ));
        } else {
            //Если нет, то просто добавляем пользователя в бд и ставим нулл город
            userServiceClient.createUser(new UserDTO(chatId, "null", UserState.START.toString()));
        }
        return new SendMessage(chatId.toString(), Constants.START_MESSAGE);
    }
}
