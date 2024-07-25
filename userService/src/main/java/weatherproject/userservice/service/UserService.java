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
        var user =userRepository.findById(id).orElse(null);
        if (user != null) {
            log.info("Пользователь {} найден, возвращаем его", user);
            return user;
        }
        log.info("Пользователь {} не найден, создаем его", user);
        createOrUpdateUser(new UserEntity(id, "null", "START"));
        return userRepository.findById(id).orElse(null);
    }

    public List<UserEntity> getUserByCity(String city) {
        log.info("Получение пользователя по городу");
        return userRepository.findByCity(city);
    }

    public void createOrUpdateUser(UserEntity userEntity) {
        log.info("Создание или обновление пользователя с chatId={}",
                userEntity.getChatId());
        userRepository.save(userEntity);
    }

    public void createOrUpdateUser(UserDTO userDTO) {
        log.info("Создание или обновление пользователя с chatId={}",
                userDTO.getChatId());
        userRepository.save(userDtoToUserEntity(userDTO));
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
