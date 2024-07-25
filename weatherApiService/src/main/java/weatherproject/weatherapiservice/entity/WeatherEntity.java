package weatherproject.weatherapiservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;
    @Column(name = "temperature")
    private Double temperature;
    @Column(name = "condition")
    private String condition;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public WeatherEntity(String city, Double temperature, String condition) {
        this.city = city;
        this.temperature = temperature;
        this.condition = condition;
        this.updatedAt = LocalDateTime.now();
    }
}
