package weatherproject.weatherapiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import weatherproject.weatherapiservice.client.ApiClient;

public class ApiCientConfig {

    @Value("${weather.api.url}")
    private String apiUrl;

    @Bean
    public ApiClient apiClient() {
        return new ApiClient(apiUrl);
    }
}

