package weatherproject.tgbotservice.telegram.callbacks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;

public interface Callback {
    String execute(UserDTO user, WeatherDTO weather);
}
