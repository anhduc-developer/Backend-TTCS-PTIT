package vn.Project.Duck.Controller;

import org.springframework.web.bind.annotation.RestController;

import vn.Project.Duck.Domain.User;
import vn.Project.Duck.Service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = this.userService.getAllUser();
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        User user = this.userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
