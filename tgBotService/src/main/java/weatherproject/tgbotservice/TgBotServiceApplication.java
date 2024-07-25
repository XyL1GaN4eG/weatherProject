package weatherproject.tgbotservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class TgBotServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TgBotServiceApplication.class, args);
    }

}
