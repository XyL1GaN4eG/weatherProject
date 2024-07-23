package weatherproject.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long chatId;
    private String city;
    private String state;
    @Override
    public String toString() {
        return "[chatId=" + chatId + ", city=" + city + ", state=" + state + "]";
    }
}
