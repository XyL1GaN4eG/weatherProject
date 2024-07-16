package org.example.server.cli;

import org.apache.log4j.Logger;
import org.example.server.api.WeatherApiClient;
import org.example.server.database.HibernateUtil;
import org.example.server.database.WeatherDataEntity;
import org.example.server.service.PortForwarding;
import org.hibernate.Session;


//TODO: добавить поддержку нескольких клиентов
public class CliApp {

    private static final Logger logger = Logger.getLogger(CliApp.class);

    public static void start(String[] args) {
        int availablePort = PortForwarding.portForwarding();
        HibernateUtil.setPort(availablePort);
        var sessionFactory = HibernateUtil.getSessionFactory();

        Session session = sessionFactory.openSession();

        var weatherData = retrieveWeatherData();
        logger.info("Данные о погоде получены");


        saveWeatherData(weatherData, session);
        logger.info("Данные сохранены в базу данных");

        HibernateUtil.shutdown();
        logger.info("Программа завершила свою работу. Спасибо за использование!");

        PortForwarding.closeSession();
    }

    private static WeatherDataEntity retrieveWeatherData() {
        var apiClient = new WeatherApiClient();

        //TODO: убрать хардкод и занести в конфиг
        var city = "Saint-Petersburg";
        var apiKey = "b754ca0088e64db9afe102203240306";

        var maxRetries = 5;
        var retryCount = 0;

        while (retryCount <= maxRetries) {
            try {
                var data = apiClient.getData(city, apiKey);
                return new WeatherDataEntity(data);
            } catch (RuntimeException e) {
                retryCount++;
                logger.error("Произошла ошибка при добавлении данных!");
            }
        }

        logger.fatal("Не удалось получить данные о погоде после нескольких попыток, вызываю панику");
        throw new RuntimeException();
    }

    private static void saveWeatherData(WeatherDataEntity weatherData, Session session) {
        var transaction = session.beginTransaction();
        session.save(weatherData);
        transaction.commit();
    }


}