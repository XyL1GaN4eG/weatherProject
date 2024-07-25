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
import weatherproject.tgbotservice.utils.Constants;

import static weatherproject.tgbotservice.telegram.UserState.HAVE_SETTED_CITY;

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
        String textToReply = "Просим прощения, город или погода в нем не найдены.";

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
                var weatherCity = weatherServiceClient.getFormattedWeatherByCity(city);
                if (weatherCity != null) {
                    userServiceClient.createOrUpdateUser(new UserDTO(currentUser.getChatId(), city, HAVE_SETTED_CITY.toString()));
                    textToReply = weatherCity;
                }
                return new SendMessage(chatId.toString(), textToReply);
            }
            case HAVE_SETTED_CITY: {
                if (update.getMessage().hasText()) {
                    city = (update.getMessage().getText().replace(" ", "-"));
                } else if (update.getMessage().hasLocation()) {
                    city = geocodingClient.getCityByCoordinates(
                            update.getMessage().getLocation().getLatitude(),
                            update.getMessage().getLocation().getLongitude());
                }
                var weatherCity = weatherServiceClient.getFormattedWeatherByCity(city);
                if (weatherCity != null) {
                    userServiceClient.createOrUpdateUser(new UserDTO(currentUser.getChatId(), city, HAVE_SETTED_CITY.toString()));
                    textToReply = weatherCity;
                    return new SendMessage(chatId.toString(), textToReply);
                } else {
                    return new SendMessage(chatId.toString(), Constants.ALREADY_USER);
                }
            }
        }
        return new SendMessage(chatId.toString(), textToReply);
    }
}

