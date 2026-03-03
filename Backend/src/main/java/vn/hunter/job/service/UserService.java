package vn.hunter.job.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hunter.job.domain.Company;
import vn.hunter.job.domain.Role;
import vn.hunter.job.domain.User;
import vn.hunter.job.domain.response.ResCreateUserDTO;
import vn.hunter.job.domain.response.ResUpdateUserDTO;
import vn.hunter.job.domain.response.ResUserDTO;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User createUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.getCompanyById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
        if (user.getRole() != null) {
            Role roleOptional = this.roleService.fetchById(user.getRole().getId());
            user.setRole(roleOptional);
        }
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
        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
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
            if (updateUser.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.getCompanyById(updateUser.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }
            if (updateUser.getRole() != null) {
                Role role = this.roleService.fetchById(updateUser.getRole().getId());
                currentUser.setRole(role);
            }
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
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.UserCompany com = new ResUserDTO.UserCompany();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        if (res.getCompany() != null) {
            com.setId(res.getCompany().getId());
            com.setName(res.getCompany().getName());
            res.setCompany(com);
        }
        if (res.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }

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
