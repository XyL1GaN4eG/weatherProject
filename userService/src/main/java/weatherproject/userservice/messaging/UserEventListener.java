package weatherproject.userservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weatherproject.userservice.dto.UserDTO;
import weatherproject.userservice.service.UserService;

import static weatherproject.userservice.config.RabbitMQConfig.*;

//Класс для работы с пользователями через RabbitMQ
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserEventListener {

    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;


    @RabbitListener(queues = USER_GET_QUEUE_NAME)
    public void handleUserGet(Long chatId) {
        var user = userService.getUserByChatId(chatId);
        rabbitTemplate.convertAndSend(USER_EXCHANGE_NAME, ROUTING_KEY_USER_GET_RESPONSE, user);
    }

    @RabbitListener(queues = USER_GET_QUEUE_NAME)
    public void handleUserGet(UserDTO userDto) {
        var user = userService.getUserByChatId(userDto.getChatId());
        rabbitTemplate.convertAndSend(USER_EXCHANGE_NAME, ROUTING_KEY_USER_GET_RESPONSE, user);
    }

    @RabbitListener(queues = USER_CREATED_QUEUE_NAME)
    public void handleUserCreated(UserDTO createUser) {
        userService.createOrUpdateUser(createUser);
    }


}