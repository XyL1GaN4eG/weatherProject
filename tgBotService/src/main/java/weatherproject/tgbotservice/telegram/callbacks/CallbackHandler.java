package weatherproject.tgbotservice.telegram.callbacks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;
import weatherproject.tgbotservice.utils.Constants;

import static weatherproject.tgbotservice.telegram.UserState.HAVE_SETTED_CITY;
import static weatherproject.tgbotservice.utils.Constants.CITY_NOT_FOUND;
import static weatherproject.tgbotservice.utils.Constants.NEW_CITY_SETTED;

@RequiredArgsConstructor
@Component
public class CallbackHandler {
    private final GeocodingClient geocodingClient;
    private final WeatherServiceClient weatherServiceClient;
    private final UserServiceClient userServiceClient;
    private final GoogleTranslateClient translateClient;


    public SendMessage handleCallback(UserDTO currentUser, Update update) {
        var chatId = update.getMessage().getChatId();
        String city = "Извините, произошла ошибка";
        String textToReply = "Просим прощения, город или погода в нем не найдены.";

        var currentState = (UserState) UserState.valueOf(currentUser.getState());
        //TODO: вынести обработку сообщения и присваивания названия города в отдельный метод
        //TODO: вынести коллбэки в отдельные классы
        //TODO: делать проверку в сообщении на то, город ли это вообще
        switch (currentState) {
            case START: {
                if (update.getMessage().hasText()) {
                    city = translateClient.translateRuToEng(update.getMessage().getText()).replace(" ", "-");
                } else if (update.getMessage().hasLocation()) {
                    city = geocodingClient.getCityByCoordinates(
                            update.getMessage().getLocation().getLatitude(),
                            update.getMessage().getLocation().getLongitude());
                }
                var weatherCity = weatherServiceClient.getWeatherByCity(city);
                if (weatherCity != null) {
                    userServiceClient.createOrUpdateUser(new UserDTO(currentUser.getChatId(), city, HAVE_SETTED_CITY.toString()));
                    textToReply = NEW_CITY_SETTED
                            .replace("{city}", translateClient.translateEngToRussian(weatherCity.getCity()))
                            .replace("{temperature}", weatherCity.getTemperature().toString())
                            .replace("{condition}", weatherCity.getCondition());
                }
                return new SendMessage(chatId.toString(), textToReply);
            }

            //Если город уже выставлен
            case HAVE_SETTED_CITY: {
                //то обращаемся к апи и возвращаем название города в на английском языке
                if (update.getMessage().hasText()) {
                    city = translateClient.translateRuToEng(update.getMessage().getText()).replace(" ", "-");
                } else if (update.getMessage().hasLocation()) {
                    city = geocodingClient.getCityByCoordinates(
                            update.getMessage().getLocation().getLatitude(),
                            update.getMessage().getLocation().getLongitude());
                }
                //получаем погоду из апи микросервиса
                var weatherCity = weatherServiceClient.getWeatherByCity(city);

                //TODO: добавить чтобы если город не найден, то написать об этом в сообщении
                if (weatherCity != null) { //если погода найдена то
                    //обновляем город пользователя
                    userServiceClient.createOrUpdateUser(new UserDTO(currentUser.getChatId(), city, HAVE_SETTED_CITY.toString()));
                    textToReply = NEW_CITY_SETTED
                            .replace("{city}", translateClient.translateEngToRussian(city))
                            .replace("{temperature}", weatherCity.getTemperature().toString())
                            .replace("{condition}", translateClient.translateEngToRussian(weatherCity.getCondition()));
                } else {
                    textToReply = CITY_NOT_FOUND.replace("city", translateClient.translateEngToRussian(city));
                }

            }
        }
        return new SendMessage(chatId.toString(), textToReply);
    }
}

