package weatherproject.weatherapiservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import weatherproject.weatherapiservice.entity.CityWeather;

@Repository
public interface WeatherRepository extends JpaRepository<CityWeather, Long> {
    CityWeather findByCity(String city);
}
