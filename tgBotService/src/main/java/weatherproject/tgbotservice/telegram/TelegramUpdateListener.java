package weatherproject.tgbotservice.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramUpdateListener {

    private final TelegramBot bot;
    private final BotHandler botHandler;

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null) {
                    botHandler.handleUpdate(update);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
