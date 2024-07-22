package weatherproject.tgbotservice.telegram.callbacks;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public class CityChooseCallback implements CallbackTemplate {

    @Override
    public SendMessage apply(Callback callback, Update update) {
        String chatId = update.message().chat().id().toString();
        String answer = "Введите название города или отправьте свою геолокацию";

        return new SendMessage(chatId, answer);
    }
}
