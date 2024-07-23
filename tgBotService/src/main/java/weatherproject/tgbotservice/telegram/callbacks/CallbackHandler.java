package weatherproject.tgbotservice.telegram.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;

import static weatherproject.tgbotservice.clients.GoogleTranslateClient.translateRuToEng;
import static weatherproject.tgbotservice.utils.Constants.ALREADY_USER;

@RequiredArgsConstructor
@Component
public class CallbackHandler {
    private final GeocodingClient geocodingClient;
    private final WeatherServiceClient weatherServiceClient;

    public SendMessage handleCallback(UserDTO currentUser, Update update) {
        var chatId = update.message().chat().id();
        var location = update.message().location();
        var text = update.message().text();
        String city;

        //Если пользователь отправил геолокацию, то получаем название города
        city = location != null ? translateRuToEng(geocodingClient.getCityByCoordinates(location.latitude(), location.longitude())) : "null";
        String textToReply = "Просим прощения, город или команда не найдены.";

        var currentState = (UserState) UserState.valueOf(currentUser.getState());
        switch (currentState) {
            case START: {
                if (!text.trim().isEmpty()) {
                    textToReply = weatherServiceClient.getFormattedWeatherByCity(text.replace(" ", "-"));
                }
                break;
            }
            case HAVE_SETTED_CITY: {
                textToReply = ALREADY_USER
                        .replace("{city}", currentUser.getCity())
                        .replace("{weather}", weatherServiceClient.getFormattedWeatherByCity(text.replace(" ", "-")));
                break;
            }
        }

        return new SendMessage(chatId, textToReply);
    }
}
