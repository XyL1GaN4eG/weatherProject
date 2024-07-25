package weatherproject.tgbotservice.utils;


public class Constants {
    public static final String PLEASE_SET_CITY = "Введите, пожалуйста, название города или отправьте свою геолокацию";
    public static final String START_MESSAGE = "Приветствую! " +
            "Это WeatherBot - бот, с помощью которого можно получить информацию о погоде." + PLEASE_SET_CITY;
    public static final String HELP_MESSAGE =
            "Команды:\n" +
            "/start - начало работы\n" +
            "/update - получить информацию о текущей погоде";
    public static final String ALREADY_USER =
            "Вы уже пользовались этим ботом, сейчас ваш новый город: {city}, текущая погода в нем: {weather}. " +
                    "Если хотите сменить город, то " + PLEASE_SET_CITY.toLowerCase();
    public static final String ALREADY_SET_CITY =
            "Вы уже пользовались этим ботом, сейчас ваш текущий город: {city}, погода в нем: {weather}. " +
                    "Если хотите сменить город, то " + PLEASE_SET_CITY.toLowerCase();
    public static final String JUST_SET_CITY = "Поздравляю, ваш текущий город - {city}, погода в нем: {temperature}, condition";
    public static final String CITY_NOT_FOUND = "Город не найден";
    public static final String CITY_NOT_SET = "Извините, не могу предоставить погоду, так как вы еще не выбрали город. " + PLEASE_SET_CITY;
    public static final String UNKNOWN_COMMAND = "Извините, я не знаю такой команды";
    public static final String CANT_UNDERSTAND = "Извините, я не понял, что вы имеете ввиду";
    public static final String ERROR = "Внутренняя ошибка";

    public static final String CHOSE_MESSAGE = "Поздравляем, вы выбрали %s вашим городом.";
    public static final String YES = "Да";

    public static final String NO = "НЕТ";

    public static final String RESTART = "Можете написать /start, чтобы начать заново. Спасибо за использование бота.";
    public static final String PIN_CORRECT_BYE = "Мы рады, что смогли помочь вам. " + RESTART;
}
