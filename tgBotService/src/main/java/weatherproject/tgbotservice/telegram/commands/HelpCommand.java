//package weatherproject.tgbotservice.telegram.commands;
//
//import com.pengrad.telegrambot.model.Update;
//import com.pengrad.telegrambot.request.SendMessage;
//import org.springframework.stereotype.Component;
//import weatherproject.tgbotservice.utils.Constants;
//
//
//@Component
//public class HelpCommand implements Command{
//    @Override
//    public SendMessage apply(Update update) {
//        return new SendMessage(update.message().chat().id(), Constants.HELP_MESSAGE);
//    }
//}
