package org.example.server.api;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.apache.log4j.Logger;

//TODO: сделать чтобы пользователь мог сам ввести при запуске свой ключ к апи и город который ему нужен
public class WeatherApiClient {
    private static final Logger logger = Logger.getLogger(WeatherApiClient.class);
    private static final HttpClient client = HttpClient.newHttpClient();

    public Object[] getData(String city, String apiKey)  {
//        var url = "http://api.weatherapi.com/v1/current.json?key=b754ca0088e64db9afe102203240306&q=Saint-Petersburg&aqi=yes";
        var url = String.format("http://api.weatherapi.com/v1/current.json?key=%s&q=%s&aqi=yes", apiKey, city);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        logger.info("Отправляем HTTP запрос");

        return fetchData(getResponse(request));
    }

    private HttpResponse<String> getResponse(HttpRequest request) {
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Получен HTTP ответ с кодом: " + response.statusCode());
            return response;
        } catch (IOException e) {
            logger.error("Произошла ошибка IOException при отправке HTTP запроса:", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.error("Произошла ошибка InterruptedException при отправке HTTP запроса:", e);
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
            logger.error("Произошла ошибка ParseException при парсинге JSON ответа:", e);
            throw new RuntimeException(e);
        }
        logger.info("Данные успешно распарсились: " +
                "Город = " + location + ", Температура = " + temperature + "°C, Состояние погоды = " + condition);
        return new Object[]{location, temperature, condition};
    }
}
