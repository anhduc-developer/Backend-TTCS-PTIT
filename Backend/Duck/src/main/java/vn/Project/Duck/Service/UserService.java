package vn.Project.Duck.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.Project.Duck.Domain.User;
import vn.Project.Duck.Repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUser() {
        List<User> users = this.userRepository.findAll();
        return users;
    }

    public User createUser(User newUser) {
        return this.userRepository.save(newUser);
    }
}
