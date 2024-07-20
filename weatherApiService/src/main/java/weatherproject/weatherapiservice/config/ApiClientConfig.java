package weatherproject.weatherapiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import weatherproject.weatherapiservice.client.ApiClient;

@Configuration
public class ApiClientConfig {

    @Value("${weather.api.url}")
    private String apiUrl;

    @Bean
    public ApiClient apiClient() {
        return new ApiClient(apiUrl);
    }
}

