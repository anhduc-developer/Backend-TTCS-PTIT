package vn.Backend.TTCS_PTIT.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.Backend.TTCS_PTIT.Entity.User;
import vn.Backend.TTCS_PTIT.Repository.UserRepository;
import vn.Backend.TTCS_PTIT.Service.iplm.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    // thành phần fake, không có thật
    @Mock
    private UserRepository userRepository;
    // thành phần thật nhưng + phần fake ở trên UserRepository + UserService
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    public void createUser_shouldReturnUser_WhenEmailValid() {
        // arrange: chuan bi data
        // => data nay duoc chay truoc
        User inputUser = new User(null, "Mai Anh", "duckTomorrow@gmail.com");
        User outputUser = new User(1L, inputUser.getName(), inputUser.getEmail());
        when(userRepository.existsByEmail(inputUser.getEmail())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(outputUser);
        // act: action - hanh dong la gi
        User result = this.userServiceImpl.createUser(inputUser);
        System.out.println(result);
        // assert: so sanh
        assertEquals(1L, result.getId());
    }

    @Test
    public void createUser_shouldThrowException_WhenEmailInvalid() {
        User inputUser = new User(null, "duck", "duckuro.ptit@gmail.com");

        when(this.userRepository.existsByEmail(inputUser.getEmail())).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            this.userServiceImpl.createUser(inputUser);
        });
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    public void getAllUsers_shouldReturnAllUsers() {
        // arrange
        List<User> outputUsers = new ArrayList<>();
        outputUsers.add(new User(1L, "duc", "duc@gmail.com"));
        outputUsers.add(new User(2L, "canh", "canh@gmail.com"));
        // gia lap
        when(this.userRepository.findAll()).thenReturn(outputUsers);
        // action
        List<User> result = this.userServiceImpl.getAllUsers();
        // assert
        assertEquals(2, result.size());
        assertEquals("duc@gmail.com", result.get(0).getEmail());
    }

    @Test
    public void getUserById_shouldReturnOptionalUser() {
        // arrange
        Long inputId = 1L;
        User inputUser = new User(1L, "duc", "ducTomorrow@gmail.com");
        Optional<User> userOptionalOutput = Optional.of(inputUser);
        when(this.userRepository.findById(inputId)).thenReturn(userOptionalOutput);

        // action
        Optional<User> result = this.userServiceImpl.getUserById(inputId);
        // assert
        assertEquals(true, result.isPresent());
    }

    @Test
    public void deleteUser_shouldReturnVoid_WhenUserExists() {
        // arrange
        Long inputId = 1L;
        when(this.userRepository.existsById(inputId)).thenReturn(true);
        // action
        this.userServiceImpl.deleteUser(inputId);
        // assert
        verify(this.userRepository).deleteById(1L);
    }

    @Test
    public void deleteUser_shouldReturnException_WhenUserNotExists() {
        // arrange
        Long inputId = 1L;
        when(this.userRepository.existsById(inputId)).thenReturn(false);

        // action
        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            this.userServiceImpl.deleteUser(inputId);
        });

        // assert
        assertEquals("User not found!", ex.getMessage());
    }

    @Test
    public void updateUser_shouldReturnUser_WhenValid() {
        // arrange
        Long inputId = 1L;
        User inputUser = new User(1L, "old name", "old@gmail.com");
        User outputUser = new User(1L, "new", "new@gmail.com");
        when(this.userRepository.findById(inputId)).thenReturn(Optional.of(inputUser));
        when(this.userRepository.save(any())).thenReturn(outputUser);

        // action
        User result = this.userServiceImpl.updateUser(inputId, inputUser);
        // assert
        assertEquals("new", result.getName());
    }

    @Test
    public void updateUser_shouldReturnThrowException_WhenInvalid() {
        // arrange
        Long inputId = 9L;

        // action
        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            this.userServiceImpl.deleteUser(inputId);
        });
        // assert
        assertEquals("User not found!", ex.getMessage());
    }
}