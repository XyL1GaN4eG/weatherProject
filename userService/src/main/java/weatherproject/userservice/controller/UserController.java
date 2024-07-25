package weatherproject.userservice.controller;


import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import weatherproject.userservice.dto.UserDTO;
import weatherproject.userservice.entity.UserEntity;
import weatherproject.userservice.service.UserService;

import java.util.ArrayList;
import java.util.List;


//Класс для работы с пользователями через рест апи
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserController {

    private final UserService userService;

    //TODO: добавить для каждого метода нормальную обработку не userentity, а userdto
    //гетмаппинг обрабатывает хттп гет запросы на URI /users
    @GetMapping
    //метод возвращает всех пользователей
    public List<UserDTO> getAllUsers() {
        List<UserDTO> list = new ArrayList<UserDTO>();
        for (UserEntity user :  userService.getAllUsers()) {
            list.add(new UserDTO(user));
        }
            return list;
    }

    @GetMapping("/id/{id}")
    public UserDTO getUserById(@PathVariable String id) {
        return new UserDTO(userService.getUserByChatId(Long.valueOf(id)));
    }

    @GetMapping("/city/{city}")
    public List<UserDTO> getUserByCity(@PathVariable String city) {
        List<UserDTO> list = new ArrayList<UserDTO>();
        for (UserEntity user :  userService.getUserByCity(city)) {
            list.add(new UserDTO(user));
        }
        return list;
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
