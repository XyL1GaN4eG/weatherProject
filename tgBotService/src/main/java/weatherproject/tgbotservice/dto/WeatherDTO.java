package weatherproject.tgbotservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class WeatherDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @EqualsAndHashCode.Include
    private String city;

    @EqualsAndHashCode.Include
    private Double temperature;

    @EqualsAndHashCode.Include
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;

        WeatherDTO other = (WeatherDTO) o;

        // Проверка на равенство всех полей
        return (city == null && other.city == null) &&
                (temperature == null && other.temperature == null) &&
                (condition == null && other.condition == null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, temperature, condition);
    }

    @Override
    public String toString() {
        return "[city=" + city + " , temperature=" + temperature + ", condition=" + condition + "]";
    }
}
