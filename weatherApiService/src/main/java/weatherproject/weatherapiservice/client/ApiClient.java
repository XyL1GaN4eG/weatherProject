package weatherproject.weatherapiservice.client;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class ApiClient {
    private static final HttpClient client = HttpClient.newHttpClient();

    private final String url;

    @Autowired
    public ApiClient(@Value("${weather.api.url}") String url) {
        this.url = url;
    }

    @PostConstruct //выполняется сразу после инициализации класса
    public void init() {
        log.info("Initialized ApiClient with URL: {}", url);
    }

    public Object[] getWeather(String city) {
        var urlForRequest = url.replace("{city}", city.replace(" ", "-"));
        log.info("Метод getWeather успешно вызвался для города: {}, для запроса по следующему api: {}", city, urlForRequest);
        log.info("Собираем http реквест на адрес: {}", urlForRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlForRequest))
                .GET()
                .build();

        log.info("Отправляем HTTP запрос: {}", request);
        var response = getResponse(request);

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            log.info("Получен успешный HTTP ответ с кодом: " + response.statusCode());
            return fetchData(response);
        }
        log.info("Получен HTTP ответ с ошибкой: {}", response.statusCode());
        return null;
    }

    private HttpResponse<String> getResponse(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
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
        try {
            var jsonResponse = (JSONObject) parser.parse(response.body());
            var current = (JSONObject) jsonResponse.get("current");
            var temperature = (double) current.get("temp_c");
            var locationObj = (JSONObject) jsonResponse.get("location");
            var location = (String) locationObj.get("name");
            var conditionObj = (JSONObject) current.get("condition");
            var condition = (String) conditionObj.get("text");


            log.info("Данные успешно распарсились: Город = {}, Температура = {}°C, Состояние погоды = {}", location, temperature, condition);
            return new Object[]{location, temperature, condition};
        } catch (ParseException e) {
            log.error("Произошла ошибка ParseException при парсинге JSON ответа:", e);
            throw new RuntimeException(e);
        }
    }
}
