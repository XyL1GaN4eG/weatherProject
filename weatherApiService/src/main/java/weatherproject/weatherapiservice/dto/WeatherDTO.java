package weatherproject.weatherapiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weatherproject.weatherapiservice.entity.WeatherEntity;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String city;
    private Double temperature;
    private String condition;

    public WeatherDTO(WeatherEntity weatherEntity) {
        this.city = weatherEntity.getCity();
        this.temperature = weatherEntity.getTemperature();
        this.condition = weatherEntity.getCondition();
    }
}
