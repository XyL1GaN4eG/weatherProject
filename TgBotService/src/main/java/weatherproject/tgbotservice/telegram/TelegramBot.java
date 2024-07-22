//package weatherproject.tgbotservice.telegram;
//
//import com.pengrad.telegrambot.UpdatesListener;
//import com.pengrad.telegrambot.model.Update;
//import com.pengrad.telegrambot.request.SendMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import weatherproject.tgbotservice.config.BotProperties;
//import weatherproject.tgbotservice.telegram.commands.CommandHandler;
//
//import javax.annotation.PostConstruct;
//
//import static weatherproject.tgbotservice.utils.Constants.CANT_UNDERSTAND;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class TelegramBot {
//
//    private final BotProperties botProperties;
//    private final CommandHandler commandsHandler;
//    private final CallbackHandler callbackHandler;
//    private com.pengrad.telegrambot.TelegramBot bot;
//
//    @PostConstruct
//    public void init() {
//        bot = new com.pengrad.telegrambot.TelegramBot(botProperties.getToken());
//
//        bot.setUpdatesListener(updates -> {
//            for (Update update : updates) {
//                onUpdateReceived(update);
//            }
//            return UpdatesListener.CONFIRMED_UPDATES_ALL;
//        });
//    }
//
//    private void onUpdateReceived(Update update) {
//        if (update.message() != null && update.message().text() != null) {
//            String chatId = update.message().chat().id().toString();
//            //Если /start
//            if ("/".equals(update.message().text())) {
//                sendMessage(commandsHandler.handleCommand(update));
//            } else {
//                sendMessage(new SendMessage(chatId, CANT_UNDERSTAND));
//            }
//        } else if (update.callbackQuery() != null) {
//            sendMessage(callbackHandler.handleCallback(update));
//        }
//    }
//
//    private void sendMessage(SendMessage sendMessage) {
//        try {
//            bot.execute(sendMessage);
//        } catch (Exception e) {
//            log.error("Error while sending message: {}", e.getMessage());
//        }
//    }
//
//
//    /*
//    //Метод для расписания уведомлений.
//    //Здесь можно настроить расписание для регулярной отправки сообщений.
//    private void scheduleNotifications() {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // Здесь логика отправки уведомлений
//                sendNotification();
//            }
//        }, 0, 24 * 60 * 60 * 1000); // Интервал в миллисекундах (например, каждый день)
//    }
//    */
//
////TODO: дописать notification avg temp после того как сделаю основную логику
///*
//    //TODO: добавить класс для хттп запросов, и через него обращаться к userservice
//    //TODO: переписать для поддержки триггера бд
//    @RabbitListener(queues = NOTIFICATION_QUEUE)
//    private void sendNotification(String jsonWeather) {
//        var data = fetchData(jsonWeather);
//
//
//
//        String chatId = "123456789"; // Укажите правильный chatId
//        SendMessage sendMessage = new SendMessage(chatId, "Ежедневное уведомление");
//        sendMessage(sendMessage);
//    }
//
//    private Object[] fetchData(String response) {
//        JSONParser parser = new JSONParser();
//        String city = "";
//        String newAvgTemp = "";
//        String tempChange = "";
//
//        try {
//            // Парсим строку JSON
//            JSONObject jsonResponse = (JSONObject) parser.parse(response);
//
//            // Получаем значения из JSON-объекта
//            city = (String) jsonResponse.get("city");
//            newAvgTemp = ((String) jsonResponse.get("new_avg_temp"));
//            tempChange = ((String) jsonResponse.get("temp_change"));
//            log.info(
//                    "Данные успешно распарсились: Город = {}, " +
//                            "Текущая температура = {}°C, " +
//                            "Насколько изменилась температура= {}",
//                    city,
//                    newAvgTemp,
//                    tempChange);
//        } catch (ParseException e) {
//            log.error("Произошла ошибка ParseException при парсинге JSON ответа:", e);
//        }
//        return new String[]{city, newAvgTemp, tempChange};
//    }
// */
//}
