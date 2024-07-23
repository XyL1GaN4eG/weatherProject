package weatherproject.weatherapiservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import weatherproject.weatherapiservice.entity.CityWeather;

@Repository
public interface WeatherRepository extends JpaRepository<CityWeather, Long> {
    CityWeather findByCity(String city);

    @Query(value = "SELECT * FROM city_weather WHERE city = :city ORDER BY id DESC LIMIT 1", nativeQuery = true)
    CityWeather findLatestByCity(@Param("city") String city);
}
