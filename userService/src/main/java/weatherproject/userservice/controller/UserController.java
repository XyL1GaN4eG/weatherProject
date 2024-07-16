package weatherproject.userservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import weatherproject.userservice.entity.TGUser;
import weatherproject.userservice.service.UserService;

import java.util.List;


@RestController //означает что класс будет обрабатывать хттп запросы и возвращать жсончики
//реквест маппинг указывает базовый uri с которого будут начинаться все эндпоинты этого контроллера
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserController {

    private final UserService userService;

    //гетмаппинг обрабатывает хттп гет запросы на URI /users
    @GetMapping
    //метод возвращает всех пользователей
    public List<TGUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public TGUser getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public TGUser createUser(@RequestBody TGUser TGUser) {
        return userService.createUser(TGUser);
    }

    @PutMapping("/{id}")
    public TGUser updateUser(@PathVariable Long id, @RequestBody TGUser TGUser) {
        return userService.updateUser(id, TGUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
