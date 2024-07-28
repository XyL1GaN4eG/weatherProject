package weatherproject.weatherapiservice.listener;

import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weatherproject.weatherapiservice.config.RabbitMQConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class TemperatureChangeNotifier {
    private final RabbitTemplate rabbitTemplate;

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TemperatureChangeNotifier.class);

    // Автоматически подставляемый DataSource, который настроен в конфигурации Spring
    private final DataSource dataSource;

    private final Queue notificationQueue;

    // Переменная для хранения соединения с базой данных
    private Connection connection;

    // Поток, в котором будет выполняться прослушивание уведомлений
    private Thread listenerThread;

    @Autowired
    public TemperatureChangeNotifier(RabbitTemplate rabbitTemplate, RabbitMQConfig rabbitMQConfig, DataSource dataSource) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationQueue = rabbitMQConfig.weatherNotificationQueue();
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        log.info("TemperatureChangeNotifier инициализирован");
    }

    // Метод, который будет выполнен после создания бина (инициализация)
    @PostConstruct
    public void start() {
        // Создаем и запускаем новый поток для прослушивания уведомлений
        listenerThread = new Thread(() -> {
            log.info("TemperatureChangeNotifier начал поток");
            try {
                log.info("Получаем соединение в бд");
                connection = dataSource.getConnection();
                log.info("Создаем SQL-запрос для подписки на уведомления");
                connection.createStatement().execute("LISTEN my_notification");

                log.info("Начинаем бесконечный цикл для постоянного прослушивания");
                while (!Thread.currentThread().isInterrupted()) {
                    try (Statement stmt = connection.createStatement()) {
                        log.info("Повторно создаем SQL-запрос для подписки на уведомления");
                        stmt.execute("LISTEN my_notification");

                        // Проверяем наличие новых уведомлений с таймаутом в 5000 мс
                        PGNotification[] notifications = connection.unwrap(org.postgresql.PGConnection.class).getNotifications(5000);

                        for (PGNotification notification : notifications) {
                            // Обработка уведомлений (тут можно добавить вашу логику)
                            if (notification != null) {
                                log.info("Получено уведомление от триггера с бд: {}", notification);
                                rabbitTemplate.convertAndSend(notificationQueue.getName(), notification.getParameter());
                            } else {
                                log.info("Уведомления нет или массив уведомлений пуст");
                            }
                        }
                    } catch (SQLException e) {
                        // Выводим сообщение об ошибке в консоль
                        log.error("Произошла ошибка во время прослушивания уведомлений: {}", e.getMessage());
                    }
                }
            } catch (SQLException e) {
                log.error("Исключение при установке соединения: {}", e.getMessage());
            }
        });
        log.info("Начинаем поток для TemperatureChangeNotifier");
        listenerThread.start();
    }

    // Метод, который будет выполнен перед уничтожением бина (очистка ресурсов)
    @PreDestroy
    public void stop() {
        // Прерываем поток, если он активен
        if (listenerThread != null && listenerThread.isAlive()) {
            log.info("Прерываем поток для TemperatureChangeNotifier");
            listenerThread.interrupt();
        }
        // Закрываем соединение с базой данных, если оно открыто
        if (connection != null) {
            log.info("Закрываем соединение прослушивателя триггера с бд");
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Ошибка при закрытии соединения: {}", e.getMessage());
            }
        }
    }
}
