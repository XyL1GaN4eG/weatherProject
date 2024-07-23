package weatherproject.tgbotservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import weatherproject.tgbotservice.telegram.TelegramBot;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
@PropertySource("classpath:application.yml")
public class BotConfig {

    String name;

    String token;

}