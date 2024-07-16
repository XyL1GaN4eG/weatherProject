package org.example.tgClient;

import lombok.extern.slf4j.Slf4j;
import org.example.tgClient.callbacks.CallbacksHandler;
import org.example.tgClient.commands.CommandsHandler;
import org.example.tgClient.config.BotProperties;
import org.example.tgClient.utils.Consts;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/*
TODO:
Логика для тг бота:

/start -> бот отправляет приветственное сообщение

1)пользователь скидывает геопозицию и мы просто смотрим название населенного пункта
2) Пользователь пишет название города, бот ищет населенный пункт, который больше всего похож, находит и просит подтвердить

->

Скидывается погода в данный момент, и если вдруг средняя погода изменилась, то тоже присылается сообщение
 */


//TODO: это копипаст, переписать под себя
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    public final BotProperties botProperties;

    public final CommandsHandler commandsHandler;

    public final CallbacksHandler callbacksHandler;

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            if (update.getMessage().getText().startsWith("/")) {
                sendMessage(commandsHandler.handleCommands(update));
            } else {
                sendMessage(new SendMessage(chatId, Consts.CANT_UNDERSTAND));
            }
        } else if (update.hasCallbackQuery()) {
            sendMessage(callbacksHandler.handleCallbacks(update));
        }
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}