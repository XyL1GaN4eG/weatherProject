package org.example.tgClient.commands;

import lombok.extern.slf4j.Slf4j;
import org.example.tgClient.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Slf4j
public class CommandsHandler {
    private final Map<String, Command> commands;

    public CommandsHandler(@Autowired StartCommand startCommand) {
        this.commands = Map.of(
                "/start", startCommand
        );
    }

    public SendMessage handleCommands(Update update) {
        var messageText = update.getMessage().getText();
        var command = messageText.split(" ")[0];
        var chatId = String.valueOf(update.getMessage().getChatId());

        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        }
        return new SendMessage(chatId, Consts.UNKNOWN_COMMAND);
    }
}
