package weatherproject.userservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import weatherproject.userservice.dto.UserDTO;
import weatherproject.userservice.entity.UserEntity;
import weatherproject.userservice.service.UserService;

import java.util.List;


//Класс для работы с пользователями через рест апи
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserController {

    private final UserService userService;

    //гетмаппинг обрабатывает хттп гет запросы на URI /users
    @GetMapping
    //метод возвращает всех пользователей
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    //TODO: понять как обращаться к этим рест апи запросам и переписать
    @GetMapping("/id/{id}")
    public UserEntity getUserById(@PathVariable String id) {
        return userService.getUserByChatId(Long.valueOf(id));
    }

    @GetMapping("/city/{city}")
    public List<UserEntity> getUserByCity(@PathVariable String city) {
        return userService.getUserByCity(city);
    }

    @PostMapping
    public void createUser(@RequestBody UserDTO userDTO) {
        userService.createOrUpdateUser(userDTO);
    }

    @DeleteMapping("/id/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
