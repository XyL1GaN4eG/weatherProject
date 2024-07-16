package weatherproject.userservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weatherproject.userservice.config.RabbitMQConfig;
import weatherproject.userservice.service.UserService;

@Component
public class MessageListener {

    private final UserService userService;

    @Autowired
    public MessageListener(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMessage(String message) {
        System.out.println(message);
    }
}
