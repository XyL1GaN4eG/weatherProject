package weatherproject.tgbotservice.telegram.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;
import weatherproject.tgbotservice.utils.Constants;
import weatherproject.tgbotservice.utils.JsonHandler;

import java.util.List;
import java.util.Map;

import static weatherproject.tgbotservice.clients.GoogleTranslateClient.translateRuToEng;

@RequiredArgsConstructor
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
        if (currentUser.getState().equals(UserState.START.toString())) {
            if (!text.trim().isEmpty()) {
                textToReply = weatherServiceClient.getFormattedWeatherByCity(text.replace(" ", "-"));
            }
        }

        return new SendMessage(chatId, textToReply);
    }
}
