package weatherproject.weatherapiservice.client;


import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//Класс для работы с апи погоды
@Component
@NoArgsConstructor
public class ApiClient {
    @Value("${weather.api.url}")
    private String url;

    private static final HttpClient client = HttpClient.newHttpClient();

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ApiClient.class);

    public Object[] getWeather(String city)  {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.replace("{city}", city)))
                .GET()
                .build();

        log.info("Отправляем HTTP запрос");

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
        log.info("Данные успешно распарсились: " +
                "Город = " + location + ", Температура = " + temperature + "°C, Состояние погоды = " + condition);
        return new Object[]{location, temperature, condition};
    }
}
