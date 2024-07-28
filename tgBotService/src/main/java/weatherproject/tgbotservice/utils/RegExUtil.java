package weatherproject.tgbotservice.utils;

public class RegExUtil {
    public static String removeNonLettersAndSpaces(String input) {
        // Регулярное выражение для замены всех символов, кроме букв и пробелов
        String regex = "[^a-zA-Z ]";

        // Замена всех ненужных символов на пустую строку
        return input.replaceAll(regex, "");
    }
}
