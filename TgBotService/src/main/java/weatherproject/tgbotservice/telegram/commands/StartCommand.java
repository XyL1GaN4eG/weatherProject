package weatherproject.tgbotservice.telegram.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import weatherproject.tgbotservice.utils.Constants;

@Component
public class StartCommand implements Command {
    @Override
    public SendMessage apply(Update update) {
        var chatId = update.message().chat().id();
        return new SendMessage(chatId, Constants.START_MESSAGE);
    }
}
