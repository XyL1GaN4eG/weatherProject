//package weatherproject.tgbotservice.telegram;
//
//import com.pengrad.telegrambot.TelegramBot;
//import com.pengrad.telegrambot.model.Update;
//import com.pengrad.telegrambot.request.SendMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import weatherproject.tgbotservice.clients.GeocodingClient;
//import weatherproject.tgbotservice.clients.UserServiceClient;
//import weatherproject.tgbotservice.clients.WeatherServiceClient;
//import weatherproject.tgbotservice.dto.UserDTO;
//import weatherproject.tgbotservice.telegram.callbacks.CallbackHandler;
//
//import static weatherproject.tgbotservice.utils.Constants.*;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class BotHandler {
//    private final TelegramBot bot;
//    private final UserServiceClient userServiceClient;
//    private final WeatherServiceClient weatherServiceClient;
//    private final CallbackHandler callbackHandler;
//
//    public void handleUpdate(Update update) {
//        log.info("Начинаем обрабатывать запрос {}", update);
//        var chatId = update.message().chat().id();
//
//        var currentUser = userServiceClient.getUserById(chatId);
//        UserState currentState = UserState.valueOf(currentUser.getState());
//
//        var text = update.message().text();
//        log.info("Распрарсенные данные из update: {}", currentUser);
//        //TODO: вынести свитч кейс в отдельный класс обработчик команды
//        //Проверяем если введена слеш команда
//        switch (text) {
//            case "/start":
//                if (currentUser.getState().equals("ALREADY_USER")) {
//                    //Если у пользователя уже выставлен город, то говорим текущую погоду и предлагаем поставить новый город
//                    bot.execute(new SendMessage(chatId, ALREADY_USER
//                            .replace("{city}", currentUser.getCity())
//                            .replace("{weather}", weatherServiceClient.getFormattedWeatherByCity(currentUser.getCity()))
//                    ));
//                } else {
//                    //Если нет, то просто добавляем пользователя в бд и ставим нулл город
//                    userServiceClient.createUser(new UserDTO(chatId, "null", UserState.START.toString()));
//                }
//                break;
//
//            case "/help":
//                bot.execute(new SendMessage(chatId, HELP_MESSAGE));
//                break;
//
//            case "/update":
//                //Если у пользователя нет города, то выводим сообщение, что город not set
//                if (currentUser.getCity().equals("null")) {
//                    bot.execute(new SendMessage(chatId, CITY_NOT_SET));
//                } else {
//                    //в ином случае предоставляем текущую погоду о городе
//                    bot.execute(new SendMessage(chatId, weatherServiceClient.getFormattedWeatherByCity(currentUser.getCity())));
//                }
//                break;
//            default: {
//                callbackHandler.handleCallback(currentUser, update);
//            }
//        }
//
//
//    }
//}
//
