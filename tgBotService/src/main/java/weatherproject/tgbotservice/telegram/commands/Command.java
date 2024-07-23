package weatherproject.tgbotservice.telegram.commands;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.dto.UserDTO;

public interface Command {
    //TODO: переписать чтобы принимал не юзердто, а чатайди
    SendMessage apply(UserDTO userDTO, Update update);
}
