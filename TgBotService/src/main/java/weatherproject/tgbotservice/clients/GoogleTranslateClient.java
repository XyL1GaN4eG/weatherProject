package weatherproject.tgbotservice.clients;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
public class GoogleTranslateClient {
    private boolean isRussian(String text) {
        return text.matches("[а-яА-ЯёЁ\\s]+");
    }

    private boolean isEnglish(String text) {
        return text.matches("[a-aA-Z\\s]+");
    }

    public static String translateEngToRussian(String text) {
        return translateFromTo("en", "ru", text);
    }

    public static String translateRuToEng(String text) {
        return translateFromTo("ru", "en", text);
    }

    private static String translateFromTo(String langFrom, String langTo, String text) {
        try {
            String urlStr = "https://script.google.com/macros/s/AKfycbzO8nojwkOWKi3DjljSEf8byUYIwzNHIIhSRcPn4lGkE_1-m_LuqwU1s5SLJ0TRiarj/exec" +
                    "?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&target=" + langTo +
                    "&source=" + langFrom;
            URL url = new URL(urlStr);
            StringBuilder response = new StringBuilder();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException e) {
            log.error("Произошла ошибка при переводе с  {} на {} следующего текста: {}. Подробности: {}", langFrom, langTo, text, e.getMessage());
            return "null";
        }
    }

}
