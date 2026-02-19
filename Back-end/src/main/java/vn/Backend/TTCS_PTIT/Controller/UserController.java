package vn.Backend.TTCS_PTIT.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.Backend.TTCS_PTIT.Entity.ApiResponse;
import vn.Backend.TTCS_PTIT.Entity.User;
import vn.Backend.TTCS_PTIT.Service.UserService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) { // valid yeu cau request kiem
                                                                                         // tra co hop le khong

        User newUser = this.userService.createUser(user);
        var result = new ApiResponse<>(HttpStatus.CREATED, "create User", newUser, null);
        ApiResponse<User> result2 = new ApiResponse<>(HttpStatus.CREATED, "create User", newUser, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        List<User> users = this.userService.getAllUsers();
        var result = new ApiResponse<>(HttpStatus.OK, "getAllUsers", users, null);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        return this.userService.getUserById(id).map(user -> {
            var response = new ApiResponse<>(HttpStatus.OK, "getUserById", user, null);
            return ResponseEntity.ok().body(response);
        })
                .orElseGet(() -> {
                    ApiResponse<User> errorResponse = new ApiResponse<>(HttpStatus.NOT_FOUND,
                            "Không tìm thấy u ser với ID: " + id, null, "USER_NOT_FOUND");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                });
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        User newUser = this.userService.updateUser(id, user);
        var result = new ApiResponse<>(HttpStatus.OK, "updateUser", newUser, null);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable Long id) {
        this.userService.deleteUser(id);
        ApiResponse<User> result = new ApiResponse<User>(HttpStatus.NO_CONTENT, "deleteUser", null, null);
        return ResponseEntity.ok().body(result);
    }
}
