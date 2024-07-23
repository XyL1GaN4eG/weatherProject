package weatherproject.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weatherproject.userservice.dto.UserDTO;
import weatherproject.userservice.entity.UserEntity;
import weatherproject.userservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})//онконструктор добавляет ко всем полям аннотацию автовайред
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public List<UserEntity> getAllUsers() {
        log.info("Получение всех пользователей");
        return userRepository.findAll();
    }

    public UserEntity getUserByChatId(Long id) {
        log.info("Получение пользователя по id");
        return userRepository.findById(id).orElse(null);
    }

    public List<UserEntity> getUserByCity(String city) {
        log.info("Получение пользователя по городу");
        return userRepository.findByCity(city);
    }

    public void createOrUpdateUser(UserDTO tgUser) {
        log.info("Создание или обновление пользователя: {}",
                tgUser.toString());
        if (((UserEntity) userRepository.findById(tgUser.getChatId()).orElse(new UserEntity())).getCity().equals("null")) {

        }
        userRepository.save(userDtoToUserEntity(tgUser));
    }

    public void deleteUser(Long id) {
        log.info("Удаление пользователя: {}", userRepository.findById(id));
        userRepository.deleteById(id);
    }

    private UserEntity userDtoToUserEntity(UserDTO tgUser) {
        log.debug("UserDTO to UserEntity: {}", tgUser.toString());
        return new UserEntity(
                tgUser.getChatId(),
                tgUser.getCity(),
                tgUser.getState()
        );
    }
}
