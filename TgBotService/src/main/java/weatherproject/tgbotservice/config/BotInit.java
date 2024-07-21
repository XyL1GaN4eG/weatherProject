package weatherproject.tgbotservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import weatherproject.tgbotservice.telegram.TelegramBot;

@Component
@Slf4j
public class BotInit {
    private final TelegramBot bot;

    @Autowired
    public BotInit(TelegramBot bot) {
        this.bot = bot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            var tgbotapi = new TelegramBotsApi(DefaultBotSession.class);
            tgbotapi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка при инициализации бота: {}", e.getMessage());
        }
    }
}
