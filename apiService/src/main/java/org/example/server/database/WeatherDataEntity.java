package org.example.server.database;

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "weather_data", schema = "s408614", catalog = "studs")
public class WeatherDataEntity {
    private static final Logger logger = Logger.getLogger(WeatherDataEntity.class);

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "temperature")
    private Double temperature;
    @Basic
    @Column(name = "location")
    private String location;
    @Basic
    @Column(name = "condition")
    private String condition;

    public WeatherDataEntity(Object[] data) throws ClassCastException {
        try {
            this.location = (String) data[0];
            this.temperature = (Double) data[1];
            this.condition = (String) data[2];
            logger.info("Данные в объект WeatherDataEntity успешно добавлены!");
        } catch (Exception e) {
            logger.error("Произошла ошибка при добавлении полученных данных в WeatherDataEntity: ", e);
        }

    }

    public WeatherDataEntity() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeatherDataEntity that = (WeatherDataEntity) o;

        if (id != that.id) return false;
        if (!Objects.equals(temperature, that.temperature)) return false;
        if (!Objects.equals(location, that.location)) return false;
        if (!Objects.equals(condition, that.condition)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (temperature != null ? temperature.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }
}
