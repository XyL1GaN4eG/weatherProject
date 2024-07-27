package weatherproject.weatherapiservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import weatherproject.weatherapiservice.dto.WeatherDTO;
import weatherproject.weatherapiservice.entity.WeatherEntity;
import weatherproject.weatherapiservice.service.WeatherService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    @Test
    void getWeather_ShouldReturnWeatherList() throws Exception {
        WeatherDTO weatherDTO1 = new WeatherDTO("City1", 25.0, "Sunny");
        WeatherDTO weatherDTO2 = new WeatherDTO("City2", 20.0, "Cloudy");
        List<WeatherDTO> weatherList = Arrays.asList(weatherDTO1, weatherDTO2);

        when(weatherService.getAllCitiesWeather()).thenReturn(Arrays.asList(
                new WeatherEntity("City1", 25.0, "Sunny", null),
                new WeatherEntity("City2", 20.0, "Cloudy", null)
        ));

        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].city").value("City1"))
                .andExpect(jsonPath("$[0].temperature").value(25.0))
                .andExpect(jsonPath("$[0].condition").value("Sunny"))
                .andExpect(jsonPath("$[1].city").value("City2"))
                .andExpect(jsonPath("$[1].temperature").value(20.0))
                .andExpect(jsonPath("$[1].condition").value("Cloudy"));
    }

    @Test
    void getWeather_ShouldReturnEmptyList() throws Exception {
        when(weatherService.getAllCitiesWeather()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getWeatherByCity_ShouldReturnWeatherDTO() throws Exception {
        WeatherDTO weatherDTO = new WeatherDTO("City1", 25.0, "Sunny");

        when(weatherService.processWeatherRequest(anyString())).thenReturn(weatherDTO);

        mockMvc.perform(get("/weather/city/City1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("City1"))
                .andExpect(jsonPath("$.temperature").value(25.0))
                .andExpect(jsonPath("$.condition").value("Sunny"));
    }

    @Test
    void getWeatherByCity_ShouldReturnNull() throws Exception {
        when(weatherService.processWeatherRequest(anyString())).thenReturn(null);

        mockMvc.perform(get("/weather/city/City1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

}
