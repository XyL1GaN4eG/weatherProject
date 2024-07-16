package weatherproject.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weatherproject.userservice.config.RabbitMQConfig;
import weatherproject.userservice.entity.TGUser;
import weatherproject.userservice.repository.UserRepository;

import java.util.List;

import static weatherproject.userservice.config.RabbitMQConfig.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
//конструктор добавляет ко всем полям аннотацию автовайред
public class UserService {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public List<TGUser> getAllUsers() {
        return userRepository.findAll();
    }

    public TGUser getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public TGUser createUser(TGUser TGUser) {
        var createdUser = userRepository.save(TGUser);
        rabbitTemplate.convertAndSend("create-TGUser", createdUser);
        return createdUser;
    }

    public TGUser updateUser(Long id, TGUser TGUserDetails) {
        var user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setName(user.getName());
            user.setCity(TGUserDetails.getCity());
            var updatedUser = userRepository.save(user);
            sendMessage(ROUTING_KEY_UPDATED, updatedUser);
            return updatedUser;
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        sendMessage(RabbitMQConfig.ROUTING_KEY_DELETED, "TGUser deleted: " + id);
    }


    private void sendMessage(String routingKey, Object message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
    }

}
