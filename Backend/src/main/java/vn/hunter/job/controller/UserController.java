package vn.hunter.job.controller;

import vn.hunter.job.domain.User;
import vn.hunter.job.domain.dto.ResCreateUserDTO;
import vn.hunter.job.domain.dto.ResUpdateUserDTO;
import vn.hunter.job.domain.dto.ResUserDTO;
import vn.hunter.job.domain.dto.ResultPaginationDTO;
import vn.hunter.job.service.UserService;
import vn.hunter.job.util.annotation.ApiMessage;
import vn.hunter.job.util.errors.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spect, Pageable pageable) {
        return ResponseEntity.ok().body(this.userService.getAllUsers(spect, pageable));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExists = this.userService.isEmailExists(user.getEmail());
        if (isEmailExists) {
            throw new IdInvalidException(
                    "Email" + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
        }
        String hashCode = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashCode);
        User newUser = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertResCreateUserDTO(newUser));
    }

    @PutMapping("/users/{id}")
    @ApiMessage("Update a new user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user, @PathVariable("id") Long id)
            throws Exception {
        User currentUser = this.userService.updateUser(user);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(currentUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại!");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok().body(null);
    }
}