package weatherproject.weatherapiservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import weatherproject.weatherapiservice.entity.CityWeather;

@Repository
public interface WeatherRepository extends JpaRepository<CityWeather, Long> {
    CityWeather findByCity(String city);

    @Query("SELECT cw FROM city_weather cw WHERE cw.city = :city ORDER BY cw.id DESC")
    CityWeather findLatestByCity(@Param("city") String city);
}
