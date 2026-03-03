package vn.hunter.job.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hunter.job.domain.Permission;
import vn.hunter.job.domain.Role;
import vn.hunter.job.domain.User;
import vn.hunter.job.service.UserService;
import vn.hunter.job.util.SecurityUtil;
import vn.hunter.job.util.errors.PermissionException;

@Transactional
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService usersService;

    @Override

    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();

        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email != null && !email.isEmpty()) {
            User currentUser = this.usersService.handleGetUserByUsername(email);
            if (currentUser != null) {
                Role role = currentUser.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream()
                            .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if (!isAllow) {
                        throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                    }
                } else {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này. ");
                }
            }
        }
        return true;
    }
}
