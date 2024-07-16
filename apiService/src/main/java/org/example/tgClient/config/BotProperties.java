package org.example.tgClient.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

//Спринг смотрит проперти сурс, ищет там конфиг пропертис, и сам загружает данные,
//а аннотация дата дает все геттеры сеттеры конструкторы

@Component
@ConfigurationProperties(prefix = "bot")
@Data
@PropertySource("classpath:application.yml")
public class BotProperties {
    String name;

    String token;
}
