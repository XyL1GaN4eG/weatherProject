package weatherproject.tgbotservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class WeatherDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    private String city;
    private Double temperature;
    private String condition;

    public WeatherDTO(Object[] objects) {
        try {
            this.city = objects[0].toString();
            this.temperature = (Double) objects[1];
            this.condition = (String) objects[2];
        } catch (ClassCastException e) {
            log.error("Не удалось привести полученную погоду из weather service к корректному WeatherDTO");
        }
    }
    @Override
    public String toString() {
       return "[city=" + city + " , temperature=" + temperature + ", condition=" + condition + "]";
    }
}