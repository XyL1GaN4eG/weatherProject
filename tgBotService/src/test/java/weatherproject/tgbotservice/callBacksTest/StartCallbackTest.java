package weatherproject.tgbotservice.callBacksTest;

import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import weatherproject.tgbotservice.clients.GoogleTranslateClient;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;
import weatherproject.tgbotservice.telegram.callbacks.StartCallback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static weatherproject.tgbotservice.utils.Constants.*;

class StartCallbackTest {

    @Mock
    private GoogleTranslateClient translateClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private StartCallback startCallback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteWithLegalCity() {
        // Подготовка данных
        UserDTO user = new UserDTO(123L, null, "START");
        WeatherDTO weatherDTO = new WeatherDTO("Moscow", 25.0, "Clear");

        // Мокируем поведение translateClient
        when(translateClient.translateEngToRussian("Moscow, Clear")).thenReturn("Москва, Ясно");

        // Ожидаемый результат
        String expectedResponse = "Поздравляю, вы выбрали Москва, погода в нем: 25.0, Ясно ";

        // Выполнение теста
        String result = startCallback.execute(user, weatherDTO);

        // Проверка результатов
        assertEquals(expectedResponse, result);
        verify(translateClient, times(1)).translateEngToRussian("Moscow, Clear");
    }

    @Test
    void testExecuteWithIllegalCity() {
        // Подготовка данных
        UserDTO user = new UserDTO(123L, null, "START");
        WeatherDTO weatherDTO = new WeatherDTO();

        // Ожидаемый результат
        String expectedResponse = CITY_NOT_FOUND;

        // Выполнение теста
        String result = startCallback.execute(user, weatherDTO);

        // Проверка результатов
        assertEquals(expectedResponse, result);
    }
}
