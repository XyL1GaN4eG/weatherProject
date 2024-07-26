package weatherproject.tgbotservice.callBacksTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;
import weatherproject.tgbotservice.telegram.UserState;
import weatherproject.tgbotservice.telegram.callbacks.CallbackHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static weatherproject.tgbotservice.utils.Constants.PLEASE_SET_CITY;

public class CallbackHandlerTest {

    @Mock
    private GeocodingClient geocodingClient;

    @Mock
    private WeatherServiceClient weatherServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private GoogleTranslateClient translateClient;

    @InjectMocks
    private CallbackHandler callbackHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandleCallback_STARTState_withValidCityNameInText() {
        UserDTO user = new UserDTO(123L, null, UserState.START.toString());
        Update update = new Update();
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(123L);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("Москва");

        update.setMessage(message);

        when(translateClient.translateRuToEng("Москва")).thenReturn("Moscow");
        when(weatherServiceClient.getWeatherByCity("Moscow")).thenReturn(new WeatherDTO("Moscow", 25.0, "Clear"));
        when(translateClient.translateEngToRussian("Moscow")).thenReturn("Москва");
        when(translateClient.translateEngToRussian("Clear")).thenReturn("Ясно");

        SendMessage response = callbackHandler.handleCallback(user, update);

        assertEquals("Вы обновили свой город на Москва, погода в нем: 25.0, Ясно. ", response.getText());
    }

    @Test
    public void testHandleCallback_STARTState_withLocation() {
        UserDTO user = new UserDTO(123L, null, UserState.START.toString());
        Update update = new Update();
        Message message = mock(Message.class);
        Location location = mock(Location.class);

        when(message.getChatId()).thenReturn(123L);
        when(message.hasLocation()).thenReturn(true);
        when(message.getLocation()).thenReturn(location);
        when(location.getLatitude()).thenReturn(55.7558);
        when(location.getLongitude()).thenReturn(37.6176);

        update.setMessage(message);

        when(geocodingClient.getCityByCoordinates(55.7558, 37.6176)).thenReturn("Москва");
        when(translateClient.translateEngToRussian("Moscow")).thenReturn("Москва");
        when(translateClient.translateRuToEng("Москва")).thenReturn("Moscow");
        when(weatherServiceClient.getWeatherByCity("Moscow")).thenReturn(new WeatherDTO("Moscow", 25.0, "Clear"));
        when(translateClient.translateEngToRussian("Clear")).thenReturn("Ясно");


        SendMessage response = callbackHandler.handleCallback(user, update);

        assertEquals("Вы обновили свой город на Москва, погода в нем: 25.0, Ясно. ", response.getText());
    }

    @Test
    public void testHandleCallback_withInvalidCityNameInText() {
        UserDTO user = new UserDTO(123L, null, UserState.START.toString());
        Update update = new Update();
        Message message = mock(Message.class);

        when(message.getChatId()).thenReturn(123L);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("InvalidCity");

        update.setMessage(message);

        when(translateClient.translateRuToEng("InvalidCity")).thenReturn("InvalidCity");
        when(translateClient.translateEngToRussian("InvalidCity")).thenReturn("ИнвалидСити");

        SendMessage response = callbackHandler.handleCallback(user, update);

        assertEquals("Просим прощения, город или погода в нем не найдены.", response.getText());
    }

    @Test
    public void testHandleCallback_HAVE_SETTED_CITY_STATE_withValidCityName() {
        UserDTO user = new UserDTO(123L, null, UserState.HAVE_SETTED_CITY.toString());
        Update update = new Update();
        Message message = mock(Message.class);
        Location location = mock(Location.class);

        when(message.getChatId()).thenReturn(123L);
        when(message.hasLocation()).thenReturn(true);
        when(message.getLocation()).thenReturn(location);
        when(location.getLatitude()).thenReturn(55.7558);
        when(location.getLongitude()).thenReturn(37.6176);

        update.setMessage(message);

        when(geocodingClient.getCityByCoordinates(55.7558, 37.6176)).thenReturn("Москва");
        when(translateClient.translateEngToRussian("Moscow")).thenReturn("Москва");
        when(translateClient.translateRuToEng("Москва")).thenReturn("Moscow");
        when(weatherServiceClient.getWeatherByCity("Moscow")).thenReturn(new WeatherDTO("Moscow", 25.0, "Clear"));
        when(translateClient.translateEngToRussian("Clear")).thenReturn("Ясно");


        SendMessage response = callbackHandler.handleCallback(user, update);

        assertEquals("Вы обновили свой город на Москва, погода в нем: 25.0, Ясно. ", response.getText());
    }

    @Test
    public void testHandleCallback_HAVE_SETTED_CITY_STATE_withInvalidCityName() {
        UserDTO user = new UserDTO(123L, "Moscow", UserState.HAVE_SETTED_CITY.toString());
        Update update = new Update();
        Message message = mock(Message.class);
        Location location = mock(Location.class);

        when(message.getChatId()).thenReturn(123L);
        when(message.hasLocation()).thenReturn(true);
        when(message.getLocation()).thenReturn(location);
        when(location.getLatitude()).thenReturn(55.7558);
        when(location.getLongitude()).thenReturn(37.6176);

        update.setMessage(message);

        when(geocodingClient.getCityByCoordinates(55.7558, 37.6176)).thenReturn("Москва");
        when(translateClient.translateEngToRussian("alkdsfjlajfl")).thenReturn("алкдсфйлайфл");
        when(translateClient.translateRuToEng("alkdsfjlajfl")).thenReturn("alkdsfjlajfl");
        when(weatherServiceClient.getWeatherByCity("Moscow")).thenReturn(new WeatherDTO("Moscow", 25.0, "Clear"));
        when(translateClient.translateEngToRussian("Clear")).thenReturn("Ясно");
        when(translateClient.translateEngToRussian("Moscow")).thenReturn("Москва");


        SendMessage response = callbackHandler.handleCallback(user, update);

        assertEquals("Извините, некорректное название города. Ваш текущий город: Москва, погода в нем: 25.0, Ясно. Если хотите получить погоду в другом городе, то "
                + PLEASE_SET_CITY.toLowerCase(), response.getText());
    }
}
