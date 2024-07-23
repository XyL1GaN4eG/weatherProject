package weatherproject.tgbotservice;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
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
                if (update.message() != null) {
                    long chatId = update.message().chat().id();
                    String messageText = update.message().text();

                    // Отправка сообщения обратно
                    SendMessage message = new SendMessage(chatId, messageText)
                            .parseMode(ParseMode.Markdown);
                    SendResponse response = telegramBot.execute(message);

                    // Логирование статуса ответа
                    if (response.isOk()) {
                        System.out.println("Message sent: " + messageText);
                    } else {
                        System.err.println("Failed to send message: " + response.description());
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}