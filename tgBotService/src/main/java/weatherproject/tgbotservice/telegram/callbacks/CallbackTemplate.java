package weatherproject.tgbotservice.telegram.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface CallbackTemplate {
    SendMessage apply(Callback callback, Update update);
}
