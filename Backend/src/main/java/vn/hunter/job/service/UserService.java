package vn.hunter.job.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hunter.job.domain.User;
import vn.hunter.job.domain.response.ResCreateUserDTO;
import vn.hunter.job.domain.response.ResUpdateUserDTO;
import vn.hunter.job.domain.response.ResUserDTO;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return this.userRepository.save(user);
    }

    public ResultPaginationDTO getAllUsers(Specification<User> spect, Pageable pagable) {
        Page<User> pageUser = this.userRepository.findAll(spect, pagable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pagable.getPageNumber() + 1);
        mt.setPageSize(pagable.getPageSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageUser.getContent());
        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getEmail(),
                        item.getName(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getUpdatedAt(),
                        item.getCreatedAt()))
                .collect(Collectors.toList());
        rs.setResult(listUser);
        return rs;
    }

    public User fetchUserById(Long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        return optionalUser.isPresent() ? optionalUser.get() : null;
    }

    public User updateUser(User updateUser) {
        Optional<User> optionalUser = this.userRepository.findById(updateUser.getId());
        User currentUser = optionalUser.isPresent() ? optionalUser.get() : null;
        if (currentUser != null) {
            currentUser.setAddress(updateUser.getAddress());
            currentUser.setGender(updateUser.getGender());
            currentUser.setAge(updateUser.getAge());
            currentUser.setName(updateUser.getName());
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExists(String email) {
        return this.userRepository.findByEmail(email) == null ? false : true;
    }

    public ResCreateUserDTO convertResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdateAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
