package weatherproject.tgbotservice.telegram.callbacks;

import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.dto.WeatherDTO;

public interface Callback {
    String execute(UserDTO user, WeatherDTO weather);
}
