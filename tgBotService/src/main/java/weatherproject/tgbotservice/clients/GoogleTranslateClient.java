package weatherproject.tgbotservice.clients;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            String urlStr = "https://script.google.com/macros/s/AKfycbzO8nojwkOWKi3DjljSEf8byUYIwzNHIIhSRcPn4lGkE_1-m_LuqwU1s5SLJ0TRiarj/exec" +
                    "?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&target=" + langTo +
                    "&source=" + langFrom;
            URL url = new URL(urlStr);
            log.info("Переводим слово {} с {} на {}", text, langFrom, langTo);
            StringBuilder response = new StringBuilder();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            var translatedWord = response.toString();
            log.info("Слово {} переведено: {}", text, response);
            return translatedWord;
        } catch (IOException e) {
            log.error("Произошла ошибка при переводе с  {} на {} следующего текста: {}. Подробности: {}", langFrom, langTo, text, e.getMessage());
            return "null";
        }
    }
}
