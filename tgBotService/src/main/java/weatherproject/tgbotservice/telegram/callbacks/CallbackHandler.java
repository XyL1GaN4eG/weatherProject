package weatherproject.tgbotservice.telegram.callbacks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;
import weatherproject.tgbotservice.telegram.UserState;
import weatherproject.tgbotservice.utils.Constants;

import java.util.Map;

import static weatherproject.tgbotservice.telegram.UserState.HAVE_SETTED_CITY;
import static weatherproject.tgbotservice.telegram.UserState.START;

@RequiredArgsConstructor
@Component
@Slf4j
public class CallbackHandler {
    private final GeocodingClient geocodingClient;
    private final WeatherServiceClient weatherServiceClient;
    private final GoogleTranslateClient translateClient;
    @SuppressWarnings("FieldCanBeLocal")
    private final String CITY_NAME_PATTERN = "^[A-Za-zА-Яа-яЁё]+([-\\s][A-Za-zА-Яа-яЁё]+)?$";

    private final Map<UserState, Callback> callbacks;

    @Autowired
    public CallbackHandler(GeocodingClient geocodingClient,
                           WeatherServiceClient weatherServiceClient,
                           GoogleTranslateClient translateClient,
                           StartCallback startCallback,
                           SetCityTextCallback setCityTextCallback) {
        this.geocodingClient = geocodingClient;
        this.weatherServiceClient = weatherServiceClient;
        this.translateClient = translateClient;
        this.callbacks = Map.of(
                START, startCallback,
                HAVE_SETTED_CITY, setCityTextCallback
        );
    }

    public SendMessage handleCallback(UserDTO user, Update update) {
        var message = update.getMessage();
        var chatId = update.getMessage().getChatId().toString();
        try {
            var currentState = (UserState) UserState.valueOf(user.getState());
            var weather = getWeather(message);
            var commandHandler = callbacks.get(currentState);
            var text = commandHandler.execute(user, weather);
            return new SendMessage(chatId, text);
        } catch (NullPointerException e) {
            return new SendMessage(chatId, Constants.ERROR);
        }
    }

    protected WeatherDTO getWeather(Message message) {
        var city = "";
        if (message.hasText()) {
            city = getCityByText(message);
        } else if (message.hasLocation()) {
            city = getCityByLocation(message);
        } else {
            city = null;
        }
        if (city != null) {
            log.info("Пытаемся получить погоду в городе обращаясь к weather service {}", city);
            return weatherServiceClient.getWeatherByCity(city);
        }
        return null;
    }

    protected String getCityByText(Message message) {
        return isValidCityName(message.getText()) ? translateClient.translateRuToEng(message.getText()).replace(" ", "-") : null;
    }

    protected String getCityByLocation(Message message) {
        var cityName = geocodingClient.getCityByCoordinates(
                message.getLocation().getLatitude(),
                message.getLocation().getLongitude());
        return translateClient.translateRuToEng(cityName);
    }

    protected boolean isValidCityName(String text) {
        // Регулярное выражение для проверки названия города
        return text.matches(CITY_NAME_PATTERN);
    }
}



