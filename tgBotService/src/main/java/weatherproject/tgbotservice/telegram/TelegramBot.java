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

//    @Override
//    public String getBotToken() {
//        return botConfig.getToken();
//    }

    @Override
    public void onUpdateReceived(Update update) {
        var chatId = update.getMessage().getChatId();
        log.info("Получено новое сообщение: {}", chatId);
        var currentUser = userServiceClient.getUserById(chatId);
        if (currentUser == null) {
            log.info("Пользователь {}, добавляем его в бд", chatId);
            userServiceClient.createNewUser(chatId);
            currentUser = userServiceClient.getUserById(chatId);
        }
        if (update.hasMessage()) {
            if (update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
                log.info("Пользователь {} отправил команду: {}", chatId, update.getMessage().getText());
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
                        currentUser.getChatId().toString(), String.format(Constants.CHANGE_AVG_WEATHER,
                        city,
                        lastTemp,
                        diffTemp
                )));
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