package weatherproject.weatherapiservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityWeather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;
    @Column(name = "temperature")
    private Double temperature;
    @Column(name = "condition")
    private String condition;

    public CityWeather(String city, Double temperature, String condition) {
        this.city = city;
        this.temperature = temperature;
        this.condition = condition;
    }
}
