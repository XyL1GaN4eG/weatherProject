package weatherproject.tgbotservice.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import weatherproject.tgbotservice.clients.UserServiceClient;
import weatherproject.tgbotservice.config.BotConfig;
import weatherproject.tgbotservice.telegram.callbacks.CallbackHandler;
import weatherproject.tgbotservice.telegram.commands.CommandHandler;

//import static weatherproject.tgbotservice.utils.Constants.CANT_UNDERSTAND;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    public final BotConfig botConfig;
    public final CommandHandler commandsHandler;
    public final CallbackHandler callbacksHandler;
    public final UserServiceClient userServiceClient;

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

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
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
    @RabbitListener(queues = NOTIFICATION_QUEUE)
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
}
