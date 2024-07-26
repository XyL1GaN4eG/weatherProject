package weatherproject.tgbotservice.clientTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import weatherproject.tgbotservice.clients.GeocodingClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GeocodingClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeocodingClient geocodingClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCityByCoordinates_Success() {
        // Пример JSON ответа, который возвращает OpenStreetMap API
        String jsonResponse = "{ \"address\": { \"city\": \"Сан-Франциско\" } }";

        // Настройка мока RestTemplate
        when(restTemplate.getForEntity(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=37.7749&lon=-122.4194",
                String.class))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        // Вызов метода
        String city = geocodingClient.getCityByCoordinates(37.7749, -122.4194);

        // Проверка результата
        assertEquals("Сан-Франциско", city);
    }

    @Test
    void getCityByCoordinates_EmptyResponse() {
        // Пример пустого JSON ответа
        String jsonResponse = "{}";

        // Настройка мока RestTemplate
        when(restTemplate.getForEntity(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=34.0522&lon=-118.2437",
                String.class))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        // Вызов метода
        String city = geocodingClient.getCityByCoordinates(34.0522, -118.2437);

        // Проверка результата
        assertEquals(null, city);
    }

    @Test
    void getCityByCoordinates_RequestFailed() {
        // Настройка мока RestTemplate для неуспешного ответа
        when(restTemplate.getForEntity(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=51.5074&lon=-0.1278",
                String.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        // Вызов метода
        String city = geocodingClient.getCityByCoordinates(51.5074, -0.1278);

        // Проверка результата
        assertEquals(null, city);
    }


}
