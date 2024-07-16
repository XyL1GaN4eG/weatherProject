package org.example.tgClient.callbacks;

import org.springframework.cglib.proxy.Callback;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    SendMessage apply(Callback callback, Update update);
}
