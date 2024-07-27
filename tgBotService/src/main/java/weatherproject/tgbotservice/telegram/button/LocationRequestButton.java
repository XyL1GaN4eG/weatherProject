package weatherproject.tgbotservice.telegram.button;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collections;

public class LocationRequestButton {
    public static ReplyKeyboardMarkup requestLocation() {
        KeyboardButton locationButton = new KeyboardButton("Обновить геопозицию");
        locationButton.setRequestLocation(true); // Устанавливаем, что кнопка будет отправлять геолокацию

        // Создание строки клавиатуры с кнопкой
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(locationButton);

        // Создание клавиатуры
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
        keyboardMarkup.setResizeKeyboard(true); // Опционально, чтобы клавиатура занимала меньше места

        return keyboardMarkup;
    }
}
