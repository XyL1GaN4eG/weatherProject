package weatherproject.weatherapiservice.listener;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TemperatureChangeNotifier {
    private final RabbitTemplate rabbitTemplate;
    private final DataSource dataSource;
    private final Queue notificationQueue;
    private Connection connection;
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
        startListening();
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                initializeConnection();
                listenForNotifications();
            } catch (SQLException e) {
                log.error("Исключение при установке соединения: {}", e.getMessage());
            }
        });
        log.info("Начинаем поток для TemperatureChangeNotifier");
        listenerThread.start();
    }

    private void initializeConnection() throws SQLException {
        log.info("Получаем соединение в бд");
        connection = dataSource.getConnection();
        log.info("Создаем SQL-запрос для подписки на уведомления");
        connection.createStatement().execute("LISTEN my_notification");
    }

    private void listenForNotifications() {
        log.info("Начинаем бесконечный цикл для постоянного прослушивания");
        while (!Thread.currentThread().isInterrupted()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("LISTEN my_notification");

                PGNotification[] notifications = connection.unwrap(org.postgresql.PGConnection.class).getNotifications(5000);
                processNotifications(notifications);
            } catch (SQLException e) {
                log.error("Произошла ошибка во время прослушивания уведомлений: {}", e.getMessage());
            }
        }
    }

    private void processNotifications(PGNotification[] notifications) {
        if (notifications != null) {
            for (PGNotification notification : notifications) {
                if (notification != null) {
                    log.info("Получено уведомление от триггера с бд: {}", notification);
                    rabbitTemplate.convertAndSend(notificationQueue.getName(), notification.getParameter());
                } else {
                    log.info("Уведомления нет или массив уведомлений пуст");
                }
            }
        }
    }

    @PreDestroy
    public void stop() {
        if (listenerThread != null && listenerThread.isAlive()) {
            log.info("Прерываем поток для TemperatureChangeNotifier");
            listenerThread.interrupt();
        }
        closeConnection();
    }

    private void closeConnection() {
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
