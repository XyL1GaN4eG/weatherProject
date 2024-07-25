package weatherproject.tgbotservice.telegram.commands;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.utils.Constants;

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

    public SendMessage handleCommand(UserDTO currentUser, Update update) {
        var messageText = update.getMessage().getText();
        var command = messageText.split(" ")[0];
        var chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            log.info("Обрабатываем команду {}", update.getMessage().getText());
            return commandHandler.apply(currentUser, update);
        }
        return new SendMessage(chatId.toString(), Constants.UNKNOWN_COMMAND);
    }


}
