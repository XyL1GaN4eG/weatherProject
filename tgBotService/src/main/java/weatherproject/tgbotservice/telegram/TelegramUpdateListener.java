package weatherproject.tgbotservice.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramUpdateListener {

    private final TelegramBot bot;
    private final BotHandler botHandler;

    private int lastUpdateId = 0;
    @PostConstruct
    @Scheduled(fixedRate = 1000) // Проверяем обновления каждую секунду
    public void getUpdates() {
        log.info("Начали прослушивать данные из телеграмма");
        GetUpdates getUpdates = new GetUpdates().offset(lastUpdateId + 1).limit(100).timeout(0);
        log.info("Получаем апдейты из телеграма");
        GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
        List<Update> updates = updatesResponse.updates();

        for (Update update : updates) {
            lastUpdateId = update.updateId();
            botHandler.handleUpdate(update);
        }
    }
}
