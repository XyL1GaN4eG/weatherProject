package weatherproject.tgbotservice.telegram.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;


public interface Command {

    SendMessage apply(Update update);
}
