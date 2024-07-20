package weatherproject.weatherapiservice.client;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Класс для работы с апи погоды
@Component
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ApiClient {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ApiClient.class);


    @Value("${weather.api.url}")
    private final String url;

    @PostConstruct //выполняется сразу после инициализации класса
    public void init() {
        log.info("Initialized ApiClient with URL: {}", url);
    }

    public Object[] getWeather(String city) {
        log.info("Метод getWeather успешно вызвался для города: {}, для запроса по следующему api: {}", city, this.url);
        var urlForRequest = url.replace("{city}", city);
        log.info("Собираем http реквест на адрес: {}", urlForRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlForRequest))
                .GET()
                .build();

        log.info("Отправляем HTTP запрос: {}", request);

        return fetchData(getResponse(request));
    }

    private HttpResponse<String> getResponse(HttpRequest request) {
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Получен HTTP ответ с кодом: " + response.statusCode());
            return response;
        } catch (IOException e) {
            log.error("Произошла ошибка IOException при отправке HTTP запроса:", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error("Произошла ошибка InterruptedException при отправке HTTP запроса:", e);
            throw new RuntimeException(e);
        }
    }

    private Object[] fetchData(HttpResponse<String> response) {
        JSONParser parser = new JSONParser();
        double temperature;
        String location;
        String condition;

        try {
            var jsonResponse = (JSONObject) parser.parse(response.body());
            JSONObject current = (JSONObject) jsonResponse.get("current");
            temperature = (double) current.get("temp_c");
            JSONObject locationObj = (JSONObject) jsonResponse.get("location");
            location = (String) locationObj.get("name");
            JSONObject conditionObj = (JSONObject) current.get("condition");
            condition = (String) conditionObj.get("text");
        } catch (ParseException e) {
            log.error("Произошла ошибка ParseException при парсинге JSON ответа:", e);
            throw new RuntimeException(e);
        }
        log.info("Данные успешно распарсились: Город = {}, Температура = {}°C, Состояние погоды = {}", location, temperature, condition);
        return new Object[]{location, temperature, condition};
    }
}
