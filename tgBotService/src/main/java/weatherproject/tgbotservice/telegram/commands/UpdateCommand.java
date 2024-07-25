package weatherproject.tgbotservice.telegram.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;

import static weatherproject.tgbotservice.utils.Constants.*;

//Принудительное обновление погоды
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateCommand implements Command{
    public final WeatherServiceClient weatherServiceClient;

    //TODO: дописать метод
    @Override
    public SendMessage apply(UserDTO currentUser, Update update) {
        if (currentUser.getCity().equals("null")) {
            return (new SendMessage(update.getMessage().getChatId().toString(), CITY_NOT_SET));
        } else {
            var weather = weatherServiceClient.getWeatherByCity(currentUser.getCity());
            //в ином случае предоставляем текущую погоду о городе
            return new SendMessage(update.getMessage().getChatId().toString(),
                    ALREADY_SET_CITY
                            .replace("{city}", weather.getCity())
                            .replace("{temperature}", weather.getTemperature().toString())
                            .replace("{condition}", weather.getCondition()));
        }
    }
}
