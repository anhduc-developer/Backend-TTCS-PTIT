package vn.hunter.job.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hunter.job.domain.Permission;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean fetchPermissionByApiPathAndMethod(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(), permission.getMethod());
    }

    public Permission create(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Optional<Permission> fetchById(Long id) {
        return this.permissionRepository.findById(id);
    }

    public Permission update(Long id, Permission permission) {
        Permission currentPermission = this.fetchById(id).isPresent() ? this.fetchById(id).get() : null;
        currentPermission.setApiPath(permission.getApiPath());
        currentPermission.setMethod(permission.getMethod());
        currentPermission.setModule(permission.getModule());
        currentPermission.setName(permission.getName());
        return this.permissionRepository.save(currentPermission);
    }

    public Permission getPermission(Long id) {
        return this.fetchById(id).get();
    }

    public ResultPaginationDTO getAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pagePermission.getTotalPages());
        mt.setTotal(pagePermission.getTotalElements());
        res.setMeta(mt);
        res.setResult(pagePermission.getContent());
        return res;
    }

    public void delete(Long id) {
        Permission currentPermission = this.fetchById(id).get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));
        this.permissionRepository.deleteById(id);
    }

    public boolean isSameName(Permission permission) {
        Optional<Permission> permissionDB = this.fetchById(permission.getId());
        if (permissionDB.isPresent()) {
            if (permissionDB.get().getName().equals(permission.getName()))
                return true;
        }
        return false;
    }
}
