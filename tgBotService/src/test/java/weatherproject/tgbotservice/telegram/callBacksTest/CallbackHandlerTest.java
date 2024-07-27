package weatherproject.tgbotservice.telegram.callBacksTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GeocodingClient;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.WeatherDTO;
import weatherproject.tgbotservice.telegram.callbacks.Callback;
import weatherproject.tgbotservice.telegram.callbacks.CallbackHandler;
import weatherproject.tgbotservice.telegram.callbacks.SetCityTextCallback;
import weatherproject.tgbotservice.telegram.callbacks.StartCallback;
import weatherproject.tgbotservice.telegram.UserState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static weatherproject.tgbotservice.utils.Constants.ERROR;

@ExtendWith(MockitoExtension.class)
public class CallbackHandlerTest {

    @Mock
    private GeocodingClient geocodingClient;

    @Mock
    private WeatherServiceClient weatherServiceClient;

    @Mock
    private GoogleTranslateClient translateClient;

    @Mock
    private StartCallback startCallback;

    @Mock
    private SetCityTextCallback setCityTextCallback;

    private CallbackHandler callbackHandler;

    private Update update;
    private Message message;

    @BeforeEach
    void setUp() {
        callbackHandler = new CallbackHandler(geocodingClient, weatherServiceClient, translateClient, startCallback, setCityTextCallback);
        update = mock(Update.class);
        message = mock(Message.class);
//        when(update.getMessage()).thenReturn(message);
    }

    @Test
    void testGetWeather_withValidTextMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("Moscow");
        when(translateClient.translateRuToEng("Moscow")).thenReturn("Moscow");
        when(weatherServiceClient.getWeatherByCity("Moscow")).thenReturn(new WeatherDTO());

        Method getWeather = CallbackHandler.class.getDeclaredMethod("getWeather", Message.class);
        getWeather.setAccessible(true);

        WeatherDTO weatherDTO = (WeatherDTO) getWeather.invoke(callbackHandler, message);

        assertNotNull(weatherDTO);
        verify(weatherServiceClient).getWeatherByCity("Moscow");
    }

    @Test
    void testGetWeather_withLocationMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Location location = mock(Location.class);
        when(location.getLatitude()).thenReturn(55.751244);
        when(location.getLongitude()).thenReturn(37.618423);

        when(message.hasLocation()).thenReturn(true);
        when(message.getLocation()).thenReturn(location);
        when(geocodingClient.getCityByCoordinates(55.751244, 37.618423)).thenReturn("Moscow");
        when(translateClient.translateRuToEng("Moscow")).thenReturn("Moscow");
        when(weatherServiceClient.getWeatherByCity("Moscow")).thenReturn(new WeatherDTO());

        Method getWeather = CallbackHandler.class.getDeclaredMethod("getWeather", Message.class);
        getWeather.setAccessible(true);

        WeatherDTO weatherDTO = (WeatherDTO) getWeather.invoke(callbackHandler, message);

        assertNotNull(weatherDTO);
        verify(weatherServiceClient).getWeatherByCity("Moscow");
    }


    @Test
    void testGetWeather_noTextOrLocation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(message.hasText()).thenReturn(false);
        when(message.hasLocation()).thenReturn(false);

        Method getWeather = CallbackHandler.class.getDeclaredMethod("getWeather", Message.class);
        getWeather.setAccessible(true);

        WeatherDTO weatherDTO = (WeatherDTO) getWeather.invoke(callbackHandler, message);

        assertNull(weatherDTO);
        verify(weatherServiceClient, never()).getWeatherByCity(anyString());
    }

    @Test
    void testGetWeather_withInvalidTextMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("ASdkljaklsdlkjsdkfjsklfkl");
        when(translateClient.translateRuToEng("ASdkljaklsdlkjsdkfjsklfkl")).thenReturn("ASdkljaklsdlkjsdkfjsklfkl");

        Method getWeather = CallbackHandler.class.getDeclaredMethod("getWeather", Message.class);
        getWeather.setAccessible(true);

        WeatherDTO weatherDTO = (WeatherDTO) getWeather.invoke(callbackHandler, message);

        assertNull(weatherDTO);
    }
}
