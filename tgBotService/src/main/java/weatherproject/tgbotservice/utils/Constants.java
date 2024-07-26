package weatherproject.tgbotservice.utils;


public class Constants {
    public static final String PLEASE_SET_CITY = "Введите, пожалуйста, название города или отправьте свою геолокацию";
    private static final String WEATHER_IN_CITY_IS = "{city}, погода в нем: {temperature}, {condition}. ";
    public static final String START_MESSAGE = "Приветствую! " +
            "Это WeatherBot - бот, с помощью которого можно получить информацию о погоде." + PLEASE_SET_CITY;
    public static final String HELP_MESSAGE =
            "Команды:\n" +
            "/start - начало работы\n" +
            "/update - получить информацию о текущей погоде";
    public static final String ALREADY_SET_CITY =
            "Вы уже пользовались этим ботом, сейчас ваш текущий город: " + WEATHER_IN_CITY_IS +
                    "Если хотите сменить город, то " + PLEASE_SET_CITY.toLowerCase();
    public static final String NEW_CITY_SETTED = "Вы обновили свой город на " + WEATHER_IN_CITY_IS;
    public static final String CITY_NOT_FOUND = "Город {} не найден";
    public static final String CITY_NOT_SET = "Извините, не могу предоставить погоду, так как вы еще не выбрали город. " + PLEASE_SET_CITY;
    public static final String UNKNOWN_COMMAND = "Извините, я не знаю такой команды";
    public static final String CHANGE_AVG_WEATHER = "Погода в городе {city} за последние 2 часа изменилась на целых {diff_temp}, текущая температура: {temp_now}";
    public static final String ILLEGAL_CITY = "Извините, некорректное название города. Ваш текущий город: "
            + WEATHER_IN_CITY_IS + "Если хотите получить погоду в другом городе, то "
            + PLEASE_SET_CITY.toLowerCase();
    public static final String CANT_UNDERSTAND = "Извините, я не понял, что вы имеете ввиду";
    public static final String ERROR = "Внутренняя ошибка";

    public static final String CHOSE_MESSAGE = "Поздравляем, вы выбрали %s вашим городом.";
    public static final String YES = "Да";

    public static final String NO = "НЕТ";

}
