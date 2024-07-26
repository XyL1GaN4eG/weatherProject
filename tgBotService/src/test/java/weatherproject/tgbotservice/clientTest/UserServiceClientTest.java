package weatherproject.tgbotservice.clientTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.UserState;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceClient userServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_Success() {
        // Пример ответа от сервиса
        UserDTO user = new UserDTO(949486045L, "Saint Petersburg", UserState.HAVE_SETTED_CITY.toString());
        UserDTO[] users = { user };

        // Настройка мока RestTemplate
        when(restTemplate.getForEntity("http://localhost:8080/users", UserDTO[].class))
                .thenReturn(new ResponseEntity<>(users, HttpStatus.OK));

        // Вызов метода
        List<UserDTO> result = userServiceClient.getAllUsers();

        // Проверка результата
        assertEquals(Collections.singletonList(user), result);
    }

    @Test
    void getUserById_Success() {
        // Пример ответа от сервиса
        UserDTO user = new UserDTO(949486045L, "Saint Petersburg", UserState.HAVE_SETTED_CITY.toString());

        // Настройка мока RestTemplate
        when(restTemplate.getForObject("http://localhost:8080/users/id/949486045", UserDTO.class))
                .thenReturn(user);

        // Вызов метода
        UserDTO result = userServiceClient.getUserById(949486045L);

        // Проверка результата
        assertEquals(user, result);
    }

    @Test
    void getUsersByCity_Success() {
        // Пример ответа от сервиса
        UserDTO user = new UserDTO(949486045L, "Saint Petersburg", UserState.HAVE_SETTED_CITY.toString());
        UserDTO[] users = { user };

        // Настройка мока RestTemplate
        when(restTemplate.getForEntity("http://localhost:8080/users/city/Saint-Petersburg", UserDTO[].class))
                .thenReturn(new ResponseEntity<>(users, HttpStatus.OK));

        // Вызов метода
        List<UserDTO> result = userServiceClient.getUsersByCity("Saint Petersburg");

        // Проверка результата
        assertEquals(Collections.singletonList(user), result);
    }

    @Test
    void createOrUpdateUser_Success() {
        UserDTO user = new UserDTO(949486045L, "Saint Petersburg", UserState.HAVE_SETTED_CITY.toString());

        // Настройка мока RestTemplate
        when(restTemplate.postForObject("http://localhost:8080/users", user, Void.class))
                .thenReturn(null);  // Здесь ничего не возвращается, так как метод `postForObject` возвращает Void

        // Вызов метода
        userServiceClient.createOrUpdateUser(user);

        // Проверка вызова метода
        verify(restTemplate, times(1)).postForObject("http://localhost:8080/users", user, Void.class);
    }

    @Test
    void createNewUser_Success() {
        // Настройка мока RestTemplate
        when(restTemplate.postForObject("http://localhost:8080/users", new UserDTO(949486045L, "null", UserState.START.toString()), Void.class))
                .thenReturn(null);  // Здесь ничего не возвращается

        // Вызов метода
        userServiceClient.createNewUser(949486045L);

        // Проверка вызова метода
        verify(restTemplate, times(1)).postForObject("http://localhost:8080/users", new UserDTO(949486045L, "null", UserState.START.toString()), Void.class);
    }

    @Test
    void deleteUser_Success() {
        // Настройка мока RestTemplate
        doNothing().when(restTemplate).delete("http://localhost:8080/users/949486045");

        // Вызов метода
        userServiceClient.deleteUser(949486045L);

        // Проверка вызова метода
        verify(restTemplate, times(1)).delete("http://localhost:8080/users/949486045");
    }

    @Test
    void getAllUsers_Failure() {
        // Настройка мока RestTemplate для выброса исключения
        when(restTemplate.getForEntity("http://localhost:8080/users", UserDTO[].class))
                .thenThrow(new RuntimeException("Service unavailable"));

        // Вызов метода
        List<UserDTO> result = userServiceClient.getAllUsers();

        // Проверка результата
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void getUserById_Failure() {
        // Настройка мока RestTemplate для выброса исключения
        when(restTemplate.getForObject("http://localhost:8080/users/id/949486045", UserDTO.class))
                .thenThrow(new RestClientException("Service unavailable"));

        // Вызов метода
        UserDTO result = userServiceClient.getUserById(949486045L);

        // Проверка результата
        assertEquals(null, result);
    }

    @Test
    void getUsersByCity_EmptyResponse() {
        // Настройка мока RestTemplate с пустым массивом
        when(restTemplate.getForEntity("http://localhost:8080/users/city/Saint-Petersburg", UserDTO[].class))
                .thenReturn(new ResponseEntity<>(new UserDTO[0], HttpStatus.OK));

        // Вызов метода
        List<UserDTO> result = userServiceClient.getUsersByCity("Saint Petersburg");

        // Проверка результата
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void createOrUpdateUser_Failure() {
        UserDTO user = new UserDTO(949486045L, "Saint Petersburg", UserState.HAVE_SETTED_CITY.toString());

        // Настройка мока RestTemplate для выброса исключения
        when(restTemplate.postForObject("http://localhost:8080/users", user, Void.class))
                .thenThrow(new RestClientException("Service unavailable"));

        // Вызов метода
        try {
            userServiceClient.createOrUpdateUser(user);
        } catch (RuntimeException e) {
            // Ожидаемое поведение: метод может выбрасывать исключение, так что проверка не требуется
        }

        // Проверка вызова метода
        verify(restTemplate, times(1)).postForObject("http://localhost:8080/users", user, Void.class);
    }

    @Test
    void createNewUser_Failure() {
        // Настройка мока RestTemplate для выброса исключения
        when(restTemplate.postForObject("http://localhost:8080/users", new UserDTO(949486045L, "null", UserState.START.toString()), Void.class))
                .thenThrow(new RestClientException("Service unavailable"));

        // Вызов метода
        try {
            userServiceClient.createNewUser(949486045L);
        } catch (RuntimeException e) {
            // Ожидаемое поведение: метод может выбрасывать исключение, так что проверка не требуется
        }

        // Проверка вызова метода
        verify(restTemplate, times(1)).postForObject("http://localhost:8080/users", new UserDTO(949486045L, "null", UserState.START.toString()), Void.class);
    }
    @Test
    void deleteUser_Failure() {
        // Настройка мока RestTemplate для выброса исключения
        doThrow(new RestClientException("Service unavailable")).when(restTemplate).delete("http://localhost:8080/users/949486045");

        // Вызов метода
        try {
            userServiceClient.deleteUser(949486045L);
        } catch (RestClientException e) {
            // Ожидаемое поведение: метод может выбрасывать исключение, так что проверка не требуется
        }

        // Проверка вызова метода
        verify(restTemplate, times(1)).delete("http://localhost:8080/users/949486045");
    }

}
