package weatherproject.tgbotservice.callBacksTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;
import weatherproject.tgbotservice.telegram.callbacks.SetCityTextCallback;
import weatherproject.tgbotservice.telegram.callbacks.StartCallback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static weatherproject.tgbotservice.telegram.UserState.HAVE_SETTED_CITY;
import static weatherproject.tgbotservice.utils.Constants.*;
public class SetCityTextCallbackTest {

    @Mock
    private GoogleTranslateClient translateClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private SetCityTextCallback setCityTextCallback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteWithLegalCity() {
        // Подготовка данных
        UserDTO user = new UserDTO(123L, "Saint-Petersburg", HAVE_SETTED_CITY.toString());
        WeatherDTO weatherDTO = new WeatherDTO("Moscow", 25.0, "Clear");

        // Мокируем поведение translateClient
        when(translateClient.translateEngToRussian("Moscow, Clear")).thenReturn("Москва, Ясно");

        // Ожидаемый результат
        String expectedResponse = "Вы обновили свой город на Москва, погода в нем: 25.0, Ясно ";

        // Выполнение теста
        String result = setCityTextCallback.execute(user, weatherDTO);

        // Проверка результатов
        assertEquals(expectedResponse, result);
        verify(translateClient, times(1)).translateEngToRussian("Moscow, Clear");
    }

    @Test
    void testExecuteWithIllegalCity() {
        // Подготовка данных
        UserDTO user = new UserDTO(123L, null, HAVE_SETTED_CITY.toString());
        WeatherDTO weatherDTO = new WeatherDTO("Moscow", 25.0, "Clear");
        // Мокируем поведение translateClient
        when(translateClient.translateEngToRussian("Moscow, Clear")).thenReturn("Москва, Ясно");


        // Ожидаемый результат
        String expectedResponse = "Вы обновили свой город на Москва, погода в нем: 25.0, Ясно ";

        // Выполнение теста
        String result = setCityTextCallback.execute(user, weatherDTO);

        // Проверка результатов
        assertEquals(expectedResponse, result);
    }
}
