package org.example.tgClient.commands;

import org.example.tgClient.utils.Consts;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements Command{
    @Override
    public SendMessage apply(Update update) {
        var chatId = String.valueOf(update.getMessage().getChatId());
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(Consts.START_MESSAGE);

        return sendMessage;
    }
}
