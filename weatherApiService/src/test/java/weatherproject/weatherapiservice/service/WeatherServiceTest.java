package weatherproject.weatherapiservice.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import weatherproject.weatherapiservice.client.ApiClient;
import weatherproject.weatherapiservice.dto.WeatherDTO;
import weatherproject.weatherapiservice.entity.WeatherEntity;
import weatherproject.weatherapiservice.repository.WeatherRepository;

public class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private ApiClient apiClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessWeatherRequest_NewCity() {
        String city = "New City";
        WeatherEntity weatherEntity = new WeatherEntity(city, 20.0, "Sunny", LocalDateTime.now());
        Object[] weatherData = {city, 20.0, "Sunny"};

        when(weatherRepository.findLatestByCity(city)).thenReturn(null);
        when(apiClient.getWeather(city)).thenReturn(weatherData);
        when(weatherRepository.save(any(WeatherEntity.class))).thenReturn(weatherEntity);

        WeatherDTO result = weatherService.processWeatherRequest(city);

        assertNotNull(result);
        assertEquals(city, result.getCity());
        assertEquals(20.0, result.getTemperature());
        assertEquals("Sunny", result.getCondition());
    }

    @Test
    public void testProcessWeatherRequest_ExistingCityWithRecentUpdate() {
        String city = "Existing City";
        WeatherEntity weatherEntity = new WeatherEntity(city, 15.0, "Cloudy", LocalDateTime.now().minusMinutes(30));

        when(weatherRepository.findLatestByCity(city)).thenReturn(weatherEntity);

        WeatherDTO result = weatherService.processWeatherRequest(city);

        assertNotNull(result);
        assertEquals(city, result.getCity());
        assertEquals(15.0, result.getTemperature());
        assertEquals("Cloudy", result.getCondition());
    }

    @Test
    public void testProcessWeatherRequest_ExistingCityWithStaleData() {
        String city = "Stale City";
        WeatherEntity weatherEntity = new WeatherEntity(city, 10.0, "Rainy", LocalDateTime.now().minusHours(2));
        Object[] weatherData = {city, 12.0, "Partly Cloudy"};

        when(weatherRepository.findLatestByCity(city)).thenReturn(weatherEntity);
        when(apiClient.getWeather(city)).thenReturn(weatherData);
        when(weatherRepository.save(any(WeatherEntity.class))).thenReturn(new WeatherEntity(city, 12.0, "Partly Cloudy", LocalDateTime.now()));

        WeatherDTO result = weatherService.processWeatherRequest(city);

        assertNotNull(result);
        assertEquals(city, result.getCity());
        assertEquals(12.0, result.getTemperature());
        assertEquals("Partly Cloudy", result.getCondition());
    }

    @Test
    public void testGetAllCitiesWeather() {
        WeatherEntity weatherEntity1 = new WeatherEntity("City1", 25.0, "Clear", LocalDateTime.now());
        WeatherEntity weatherEntity2 = new WeatherEntity("City2", 18.0, "Windy", LocalDateTime.now());
        List<WeatherEntity> weatherEntities = List.of(weatherEntity1, weatherEntity2);

        when(weatherRepository.findAll()).thenReturn(weatherEntities);

        List<WeatherEntity> result = weatherService.getAllCitiesWeather();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(weatherEntity1));
        assertTrue(result.contains(weatherEntity2));
    }

    @Test
    public void testUpdateAllCitiesWeather() {
        WeatherEntity weatherEntity1 = new WeatherEntity("City1", 25.0, "Clear", LocalDateTime.now());
        WeatherEntity weatherEntity2 = new WeatherEntity("City2", 18.0, "Windy", LocalDateTime.now());

        when(weatherRepository.findAll()).thenReturn(List.of(weatherEntity1, weatherEntity2));
        when(apiClient.getWeather(weatherEntity1.getCity())).thenReturn(new Object[]{weatherEntity1.getCity(), weatherEntity1.getTemperature(), weatherEntity1.getCondition()});
        when(apiClient.getWeather(weatherEntity2.getCity())).thenReturn(new Object[]{weatherEntity2.getCity(), weatherEntity2.getTemperature(), weatherEntity2.getCondition()});
        when(weatherRepository.save(any(WeatherEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Метод не должен вызывать processWeatherRequest напрямую, а должен вызывать его внутри updateAllCitiesWeather
        weatherService.updateAllCitiesWeather();

        // Проверяем вызовы на моке weatherRepository
        verify(weatherRepository, times(2)).save(any(WeatherEntity.class));
    }

}
