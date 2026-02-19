package vn.Backend.TTCS_PTIT.Service;

import java.util.List;
import java.util.Optional;

import vn.Backend.TTCS_PTIT.Entity.User;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User createUser(User user);

    public User updateUser(Long id, User updateUser);

    public void deleteUser(Long id);
}
