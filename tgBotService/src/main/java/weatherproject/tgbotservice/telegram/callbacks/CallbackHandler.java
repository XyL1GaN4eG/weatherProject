package weatherproject.tgbotservice.telegram.callbacks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;

import static weatherproject.tgbotservice.telegram.UserState.HAVE_SETTED_CITY;
import static weatherproject.tgbotservice.utils.Constants.ILLEGAL_CITY;
import static weatherproject.tgbotservice.utils.Constants.NEW_CITY_SETTED;

@RequiredArgsConstructor
@Component
@Slf4j
public class CallbackHandler {
    private final GeocodingClient geocodingClient;
    private final WeatherServiceClient weatherServiceClient;
    private final UserServiceClient userServiceClient;
    private final GoogleTranslateClient translateClient;


    public SendMessage handleCallback(UserDTO currentUser, Update update) {
        var message = update.getMessage();
        var chatId = message.getChatId().toString();
        String city = "Извините, произошла ошибка";
        String textToReply = "Просим прощения, город или погода в нем не найдены.";

        var currentState = (UserState) UserState.valueOf(currentUser.getState());
        log.info("Обрабатываем {} от пользователя {}", message, currentUser);
        //TODO: вынести обработку сообщения и присваивания названия города в отдельный метод
        //TODO: вынести коллбэки в отдельные классы
        //TODO: делать проверку в сообщении на то, город ли это вообще
        //TODO: удалить дубликаты
        //TODO: пробовать перевести город из сообщения, а не из пользователя
        //TODO: добавить перевод и на русский и на английский города в бд юзера и погоды (но потом)


        switch (currentState) {
            case START: {
                if (message.hasText()) {
                    if (isValidCityName(message.getText())) {
                        city = translateClient.translateRuToEng(
                                        message.getText())
                                .replace(" ", "-");
                    }
                } else if (message.hasLocation()) {
                    city = translateClient.translateRuToEng(
                            geocodingClient.getCityByCoordinates(
                                    message.getLocation().getLatitude(),
                                    message.getLocation().getLongitude()));
                }
                log.info("Пытаемся получить погоду в городе {}", city);
                var weatherCity = weatherServiceClient.getWeatherByCity(city);
                if (weatherCity != null) {
                    log.info("Получена погода: {}", weatherCity);
                    userServiceClient.createOrUpdateUser(new UserDTO(currentUser.getChatId(), city, HAVE_SETTED_CITY.toString()));
                    textToReply = NEW_CITY_SETTED
                            .replace("{city}", translateClient.translateEngToRussian(weatherCity.getCity()))
                            .replace("{temperature}", weatherCity.getTemperature().toString())
                            .replace("{condition}", translateClient.translateEngToRussian(weatherCity.getCondition()));
                    log.info("Сообщение для отправки: {}", textToReply);
                }
                return new SendMessage(chatId, textToReply);
            }

            //Если город уже выставлен
            case HAVE_SETTED_CITY: {

                if (message.hasText()) {
                    if (!isValidCityName(message.getText())) {
                        return new SendMessage(update.getMessage().getChatId().toString(), ILLEGAL_CITY
                                .replace("{city}", translateClient.translateEngToRussian(currentUser.getCity()))
                                .replace("{temperature}",
                                        weatherServiceClient.getWeatherByCity(currentUser.getCity()).getTemperature().toString())
                                .replace("{condition}", translateClient.translateEngToRussian(weatherServiceClient.getWeatherByCity(currentUser.getCity()).getCondition())));
                    }
                }

                //то обращаемся к апи и возвращаем название города на английском языке
                if (message.hasText()) {
                    city = translateClient.translateRuToEng(message.getText()).replace(" ", "-");
                } else if (message.hasLocation()) {
                    city = translateClient.translateRuToEng(geocodingClient.getCityByCoordinates(
                            message.getLocation().getLatitude(),
                            message.getLocation().getLongitude()));
                }
                log.info("Пытаемся получить погоду в городе {}", city);
                //получаем погоду из апи микросервиса
                var weatherCity = weatherServiceClient.getWeatherByCity(city);

                if (weatherCity != null) { //если погода найдена то
                    log.info("Получена погода: {}", weatherCity);
                    //обновляем город пользователя
                    userServiceClient.createOrUpdateUser(new UserDTO(currentUser.getChatId(), city, HAVE_SETTED_CITY.toString()));
                    textToReply = NEW_CITY_SETTED
                            .replace("{city}", translateClient.translateEngToRussian(city))
                            .replace("{temperature}", weatherCity.getTemperature().toString())
                            .replace("{condition}", translateClient.translateEngToRussian(weatherCity.getCondition()));
                } else {
                    return new SendMessage(chatId, ILLEGAL_CITY
                            .replace("{city}", translateClient.translateEngToRussian(currentUser.getCity()))
                            .replace("{temperature}",
                                    weatherServiceClient.getWeatherByCity(currentUser.getCity()).getTemperature().toString())
                            .replace("{condition}", translateClient.translateEngToRussian(weatherServiceClient.getWeatherByCity(currentUser.getCity()).getCondition())));
                }

            }
        }
        return new SendMessage(chatId, textToReply);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final String CITY_NAME_PATTERN = "^[A-Za-zА-Яа-яЁё]+([-\\s][A-Za-zА-Яа-яЁё]+)?$";

    public boolean isValidCityName(String text) {
        // Регулярное выражение для проверки названия города
        return text.matches(CITY_NAME_PATTERN);
    }
}

