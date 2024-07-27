package weatherproject.tgbotservice.telegram.commands;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.dto.UserDTO;

public interface Command {
    SendMessage apply(UserDTO userDTO, Update update);
}
