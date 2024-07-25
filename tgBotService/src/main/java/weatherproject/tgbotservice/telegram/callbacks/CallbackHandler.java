package weatherproject.tgbotservice.telegram.callbacks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;

@RequiredArgsConstructor
@Component
public class CallbackHandler {
    private final GeocodingClient geocodingClient;
    private final WeatherServiceClient weatherServiceClient;
    private final UserServiceClient userServiceClient;


    public SendMessage handleCallback(UserDTO currentUser, Update update) {
        var chatId = update.getMessage().getChatId();
        var location = update.getMessage().getLocation();
        String city = "Извините, произошла ошибка";

        //Если пользователь отправил геолокацию, то получаем название города
//        city = location != null ? translateRuToEng(geocodingClient.getCityByCoordinates(location.getLatitude(), location.getLatitude())) : "null";
        String textToReply = "Просим прощения, город не найден.";

        var currentState = (UserState) UserState.valueOf(currentUser.getState());
//        switch (currentState) {
//            case START: {
//                if (update.getMessage().hasText()) {
//                    if (!text.trim().isEmpty()) {
//                        city = (text.replace(" ", "-"));
//                    } else if (update.getMessage().hasLocation()) {
//                        city = geocodingClient.getCityByCoordinates(
//                                update.getMessage().getLocation().getLatitude(),
//                                update.getMessage().getLocation().getLongitude());
//
//                    }
//                    textToReply = weatherServiceClient.getFormattedWeatherByCity(city);
//                }
//                break;
//            }
//            case HAVE_SETTED_CITY: {
//                textToReply = ALREADY_USER
//                        .replace("{city}", currentUser.getCity())
//                        .replace("{weather}", weatherServiceClient.getFormattedWeatherByCity(text.replace(" ", "-")));
//                break;
//            }
//            default: {
//                textToReply =
//            }
//        }

        switch (currentState) {
            case START: {
                if (update.getMessage().hasText()) {
                    city = (update.getMessage().getText().replace(" ", "-"));
                } else if (update.getMessage().hasLocation()) {
                    city = geocodingClient.getCityByCoordinates(
                            update.getMessage().getLocation().getLatitude(),
                            update.getMessage().getLocation().getLongitude());
                }
            }
            case HAVE_SETTED_CITY: {

            }
        }

        userServiceClient.createUser();
        textToReply = weatherServiceClient.getFormattedWeatherByCity(city);
        return new SendMessage(chatId.toString(), textToReply);
    }
}

