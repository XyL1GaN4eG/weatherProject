package weatherproject.tgbotservice.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.clients.WeatherServiceClient;
import weatherproject.tgbotservice.config.BotConfig;
import weatherproject.tgbotservice.dto.UserDTO;
import weatherproject.tgbotservice.telegram.callbacks.CallbackHandler;
import weatherproject.tgbotservice.telegram.commands.CommandHandler;
import weatherproject.tgbotservice.utils.Constants;

//import static weatherproject.tgbotservice.utils.Constants.CANT_UNDERSTAND;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    public final BotConfig botConfig;
    public final CommandHandler commandsHandler;
    public final CallbackHandler callbacksHandler;
    public final UserServiceClient userServiceClient;
    private final WeatherServiceClient weatherServiceClient;

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        var chatId = update.getMessage().getChatId();
        log.info("Получено новое сообщение: {}", chatId);
        var currentUser = userServiceClient.getUserById(update.getMessage().getChatId());
        if (currentUser == null) userServiceClient.createNewUser(chatId);
        if (update.hasMessage()) {
            if (update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
                sendMessage(commandsHandler.handleCommand(currentUser, update));
            } else {
                sendMessage(callbacksHandler.handleCallback(currentUser, update));
            }
        }
    }
    @RabbitListener(queues = "weather-notification-queue")
    public void handleChangeAvgTemp(String message) {
        JSONParser parser = new JSONParser();
        try {
            // Парсинг JSON строки в JSONObject
            JSONObject jsonObject = (JSONObject) parser.parse(message);

            // Извлечение данных из JSONObject
            String city = (String) jsonObject.get("city");
            String lastTemp = jsonObject.get("last_temp").toString();
            String diffTemp = jsonObject.get("diff_temp").toString();

            var users = userServiceClient.getUsersByCity(city);
            for (UserDTO currentUser : users) {
                sendMessage(new SendMessage(
                        currentUser.getChatId().toString(), Constants.CHANGE_AVG_WEATHER
                        .replace("{city}", city)
                        .replace("diff_temp", diffTemp)
                        .replace("temp_now", lastTemp)
                ));
            }
        } catch (ParseException e) {
            log.error("Произошла ошибка при парсинге строки {}, ошибка: {}", message, e.getStackTrace());
        }



    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

}

    /*
    //Метод для расписания уведомлений.
    //Здесь можно настроить расписание для регулярной отправки сообщений.
    private void scheduleNotifications() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Здесь логика отправки уведомлений
                sendNotification();
            }
        }, 0, 24 * 60 * 60 * 1000); // Интервал в миллисекундах (например, каждый день)
    }
    */

//TODO: дописать notification avg temp после того как сделаю основную логику
/*
    //TODO: добавить класс для хттп запросов, и через него обращаться к userservice
    //TODO: переписать для поддержки триггера бд
    @Listener(queues = NOTIFICATION_QUEUE)
    private void sendNotification(String jsonWeather) {
        var data = fetchData(jsonWeather);



        String chatId = "123456789"; // Укажите правильный chatId
        SendMessage sendMessage = new SendMessage(chatId, "Ежедневное уведомление");
        sendMessage(sendMessage);
    }

    private Object[] fetchData(String response) {
        JSONParser parser = new JSONParser();
        String city = "";
        String newAvgTemp = "";
        String tempChange = "";

        try {
            // Парсим строку JSON
            JSONObject jsonResponse = (JSONObject) parser.parse(response);

            // Получаем значения из JSON-объекта
            city = (String) jsonResponse.get("city");
            newAvgTemp = ((String) jsonResponse.get("new_avg_temp"));
            tempChange = ((String) jsonResponse.get("temp_change"));
            log.info(
                    "Данные успешно распарсились: Город = {}, " +
                            "Текущая температура = {}°C, " +
                            "Насколько изменилась температура= {}",
                    city,
                    newAvgTemp,
                    tempChange);
        } catch (ParseException e) {
            log.error("Произошла ошибка ParseException при парсинге JSON ответа:", e);
        }
        return new String[]{city, newAvgTemp, tempChange};
    }
 */
