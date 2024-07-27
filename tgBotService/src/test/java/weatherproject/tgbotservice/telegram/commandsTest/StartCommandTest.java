package weatherproject.tgbotservice.telegram.commandsTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;
import weatherproject.tgbotservice.telegram.UserState;
import weatherproject.tgbotservice.telegram.commands.StartCommand;
import weatherproject.tgbotservice.utils.Constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartCommandTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private WeatherServiceClient weatherServiceClient;

    @Mock
    private GoogleTranslateClient translateClient;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @InjectMocks
    private StartCommand startCommand;

    @BeforeEach
    void setUp() {
        when(update.getMessage()).thenReturn(message);
    }

    @Test
    void test_START_state_VALID_city() {
        Long chatId = 123L;
        var user = new UserDTO(chatId, "null", UserState.START.toString());

        when(message.getChatId()).thenReturn(chatId);

        var sendMessage = startCommand.apply(user, update);

        assertEquals(chatId.toString(), sendMessage.getChatId());
        assertEquals(Constants.START_MESSAGE, sendMessage.getText());
        verify(userServiceClient).createOrUpdateUser(user);
    }

    @Test
    void test_SETTED_CITY_State_VALID_city() {
        Long chatId = 123L;
        var city = "Moscow";
        var temperature = 25.0;
        var condition = "Sunny";

        var user = new UserDTO(chatId, city, UserState.HAVE_SETTED_CITY.toString());
        var weather = mock(WeatherDTO.class);

        when(message.getChatId()).thenReturn(chatId);
        when(weatherServiceClient.getWeatherByCity(city)).thenReturn(weather);
        when(weather.getTemperature()).thenReturn(temperature);
        when(weather.getCondition()).thenReturn(condition);
        when(translateClient.translateEngToRussian(city)).thenReturn("Москва");
        when(translateClient.translateEngToRussian(condition)).thenReturn("Солнечно");

        var expectedText = String.format(Constants.ALREADY_SET_CITY, "Москва", temperature, "Солнечно");
        var sendMessage = startCommand.apply(user, update);

        assertEquals(chatId.toString(), sendMessage.getChatId());
        assertEquals(expectedText, sendMessage.getText());
    }

}
