package vn.hunter.job.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hunter.job.domain.Company;
import vn.hunter.job.domain.Role;
import vn.hunter.job.domain.User;
import vn.hunter.job.domain.request.ReqChangePassword;
import vn.hunter.job.domain.request.ReqUpdateProfile;
import vn.hunter.job.domain.response.ResCreateUserDTO;
import vn.hunter.job.domain.response.ResUpdateUserDTO;
import vn.hunter.job.domain.response.ResUserDTO;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.repository.ResumeRepository;
import vn.hunter.job.repository.UserRepository;
import vn.hunter.job.util.SecurityUtil;
import vn.hunter.job.util.errors.IdInvalidException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ResumeRepository resumeRepository;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService,
            PasswordEncoder passwordEncoder, ResumeRepository resumeRepository) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.resumeRepository = resumeRepository;
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
            if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
                // Lưu ý: Nếu bạn dùng Spring Security, hãy encode password trước khi lưu
                // Ví dụ:
                // currentUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
                currentUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
            }
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

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }
        this.resumeRepository.deleteByUserId(id);

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

    public void changePassword(ReqChangePassword request) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().get();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IdInvalidException("User khong ton tai");
        }
        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng mật khẩu cũ");
        }
        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());

        user.setPassword(newEncodedPassword);
        user.setRefreshToken(null);
        this.userRepository.save(user);
    }

    public ResUpdateUserDTO updateProfile(ReqUpdateProfile request) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().get();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IdInvalidException("User not found");
        }
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        User updatedUser = this.userRepository.save(user);
        return this.convertToResUpdateUserDTO(updatedUser);
    }
}
