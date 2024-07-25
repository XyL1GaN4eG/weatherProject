package weatherproject.tgbotservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long chatId;
    private String city;
    private String state;
    @Override
    public String toString() {
        return "[chatId=" + chatId + ", city=" + city + ", state=" + state + "]";
    }
}
