package org.example.server.database;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

//TODO: переписать для работы на сервере

@Deprecated
public class HibernateUtil {
    private static final Logger logger = Logger.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    private static int port = 5433;  // Порт по умолчанию

    public static void setPort(int port) {
        HibernateUtil.port = port;
    }

    public static int getPort() {
        return port;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            buildSessionFactory(port);
        }
            return sessionFactory;
    }

    private static void buildSessionFactory(int port) {
        try {
            // Создание SessionFactory из hibernate.cfg.xml
            logger.info("Создание абстрактной фабрики Hibernate");
            var sesFactory = new Configuration().configure()
                    .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:" + port + "/studs")
                    .buildSessionFactory();
            logger.info("Абстрактная фабрика создана");
            sessionFactory = sesFactory;
//            return sesFactory;
        } catch (Throwable e) {
            // Выводим ошибку и завершаем приложение
            logger.error("Ошибка при создании SessionFactory: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdown() {
        // Закрытие кеша и пулов соединений
        getSessionFactory().close();
    }
}
