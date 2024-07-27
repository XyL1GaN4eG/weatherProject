package weatherproject.tgbotservice.clients;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class GeocodingClient {

    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/reverse?format=json";

    private final RestTemplate restTemplate;

    // Конструктор для инъекции RestTemplate
    @Autowired
    public GeocodingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCityByCoordinates(double latitude, double longitude) {
        // Построение URI с параметрами запроса
        String url = UriComponentsBuilder.fromHttpUrl(NOMINATIM_API_URL)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .toUriString();
        log.info("Пробуем получить название города с координатами {}, {} по следующему API запросу {}", latitude, longitude, url);

        try {
            // Выполнение GET запроса и получение ответа в виде строки
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                // Обработка JSON ответа
                return parseCityFromJson(response.getBody());
            } else {
                log.error("GET request failed with response code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Exception occurred while making request: {}", e.getStackTrace());
        }
        return null;
    }

    private String parseCityFromJson(String jsonResponse) {
        String city = null;
        try {
            // Создаем JSONParser для парсинга строки
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

            // Получаем объект address из jsonObject
            JSONObject address = (JSONObject) jsonObject.get("address");

            // Извлекаем название города из объекта address
            city = (String) address.get("city");

            // Если не найдено значение "city", пробуем найти "town" или "village"
            if (city == null) {
                city = (String) address.get("town");
            }
            if (city == null) {
                city = (String) address.get("village");
            }

        } catch (ParseException e) {
            log.error("Ошибка парсинга JSON от openstreetmap: {}", e.getMessage());
        }
        return city;
    }
}
