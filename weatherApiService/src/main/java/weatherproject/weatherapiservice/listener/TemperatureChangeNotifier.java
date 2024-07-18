package weatherproject.weatherapiservice.listener;
import org.postgresql.PGNotification;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import weatherproject.weatherapiservice.config.RabbitMQConfig;
import weatherproject.weatherapiservice.service.WeatherService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class TemperatureChangeNotifier {
    private final RabbitTemplate rabbitTemplate;

    // Автоматически подставляемый DataSource, который настроен в конфигурации Spring
    @Autowired
    private DataSource dataSource;

    private final Queue notificationQueue;

    // Переменная для хранения соединения с базой данных
    private Connection connection;

    // Поток, в котором будет выполняться прослушивание уведомлений
    private Thread listenerThread;

    @Autowired
    public TemperatureChangeNotifier(RabbitTemplate rabbitTemplate, RabbitMQConfig rabbitMQConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationQueue = rabbitMQConfig.weatherNotificationQueue();
    }

    // Метод, который будет выполнен после создания бина (инициализация)
    @PostConstruct
    public void start() {
        // Создаем и запускаем новый поток для прослушивания уведомлений
        listenerThread = new Thread(() -> {
            try {
                // Получаем соединение из DataSource
                connection = dataSource.getConnection();
                // Создаем SQL-запрос для подписки на уведомления
                connection.createStatement().execute("LISTEN my_notification");

                // Бесконечный цикл для постоянного прослушивания
                while (!Thread.currentThread().isInterrupted()) {
                    try (Statement stmt = connection.createStatement()) {
                        // Повторно подписываемся на уведомления (периодически)
                        stmt.execute("LISTEN my_notification");

                        // Проверяем наличие новых уведомлений с таймаутом в 5000 мс
                        PGNotification[] notificate = connection.unwrap(org.postgresql.PGConnection.class).getNotifications(5000);

                        // Обработка уведомлений (тут можно добавить вашу логику)
                        System.out.println("Notification received" + notificate[0]);

                        rabbitTemplate.convertAndSend(notificationQueue.getName(), notificate[0].getParameter());

                    } catch (SQLException e) {
                        // Выводим сообщение об ошибке в консоль
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                // Обрабатываем исключение при установке соединения
                e.printStackTrace();
            }
        });
        // Запускаем поток
        listenerThread.start();
    }

    // Метод, который будет выполнен перед уничтожением бина (очистка ресурсов)
    @PreDestroy
    public void stop() {
        // Прерываем поток, если он активен
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
        }
        // Закрываем соединение с базой данных, если оно открыто
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Выводим сообщение об ошибке в консоль
                e.printStackTrace();
            }
        }
    }
}
