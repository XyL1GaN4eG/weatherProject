package org.example.tgClient.callbacks;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CallbacksHandler {

    public CallbacksHandler(
            @Autowired
    )

    public SendMessage handleCallbacks(Update update) {

    }
}
