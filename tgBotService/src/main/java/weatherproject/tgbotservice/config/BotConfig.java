package weatherproject.tgbotservice.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

//    @Value("${bot.token}")
//    private static String botToken;
    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(
//                botToken
                "7322702173:AAHYHycNmHgeVULZEh5KWCtAHaBxqApUDS8"
        );
    }
}