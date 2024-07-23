package weatherproject.tgbotservice.telegram.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weatherproject.tgbotservice.utils.Constants;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CommandHandler {

    private final Map<String, Command> commands;

    public CommandHandler(@Autowired StartCommand startCommand,
                          @Autowired UpdateCommand updateCommand,
                          @Autowired HelpCommand helpCommand) {
        this.commands = Map.of(
                "/start", startCommand,
                "/update", updateCommand,
                "/help", helpCommand
        );
    }

    public SendMessage handleCommand(Update update) {
        var messageText = update.message().text();
        var command = messageText.split(" ")[0];
        var chatId = update.message().chat().id();

        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        }
        return new SendMessage(chatId, Constants.UNKNOWN_COMMAND);
    }


}
