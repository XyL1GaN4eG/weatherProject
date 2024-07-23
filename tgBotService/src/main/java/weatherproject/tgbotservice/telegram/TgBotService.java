package weatherproject.tgbotservice.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;

@Component
public class TgBotService {
    private final TelegramBot telegramBot;

    @Autowired
    public TgBotService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                handleUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String chatId = update.message().chat().id().toString();
            String receivedText = update.message().text();

            SendMessage request = new SendMessage(chatId, receivedText);
            SendResponse sendResponse = telegramBot.execute(request);

            if (!sendResponse.isOk()) {
                // Обработка ошибок при отправке сообщения
                System.err.println("Ошибка отправки сообщения: " + sendResponse.description());
            }
        }
    }
}