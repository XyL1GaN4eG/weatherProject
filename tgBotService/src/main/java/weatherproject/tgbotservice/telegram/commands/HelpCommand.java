package weatherproject.tgbotservice.telegram.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.utils.Constants;


@Component
public class HelpCommand implements Command{
    @Override
    public SendMessage apply(UserDTO userDTO, Update update) {
        return new SendMessage(update.getMessage().getChatId().toString(), Constants.HELP_MESSAGE);
    }
}
