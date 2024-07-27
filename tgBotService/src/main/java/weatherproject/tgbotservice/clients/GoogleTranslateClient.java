package weatherproject.tgbotservice.clients;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
@NoArgsConstructor
@Service
public class GoogleTranslateClient {

    @Value("${translate_script.api}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    private boolean isRussian(String text) {
        return text.matches("[а-яА-ЯёЁ\\s]+");
    }

    private boolean isEnglish(String text) {
        return text.matches("[a-aA-Z\\s]+");
    }

    public String translateEngToRussian(String text) {
        if (isRussian(text)) {
            log.info("Слово {} уже на русском, перевод не требуется", text);
            return text;
        }
        return translateFromTo("en", "ru", text);
    }

    public String translateRuToEng(String text) {
        if (isEnglish(text)) {
            log.info("Слово уже на английском, перевод не требуется: {}", text);
            return text;
        }
        log.info("Слово {} на русском, переводим", text);
        return translateFromTo("ru", "en", text);
    }

    private String translateFromTo(String langFrom, String langTo, String text) {
        try {
            String urlStr = "https://script.google.com/macros/s/{apiKey}/exec"
                    .replace("{apiKey}", apiKey) +
                    "?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&target=" + langTo +
                    "&source=" + langFrom;

            log.info("Переводим слово {} с {} на {}", text, langFrom, langTo);
            log.info("Сформировали следующий API запрос: {}", urlStr);

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(urlStr, String.class);
            String translatedWord = responseEntity.getBody();

            log.info("Слово {} переведено: {}", text, translatedWord);
            return translatedWord;
        } catch (Exception e) {
            log.error("Произошла ошибка при переводе с {} на {} следующего текста: {}. Подробности: {}", langFrom, langTo, text, e.getMessage());
            return "null";
        }
    }
}
