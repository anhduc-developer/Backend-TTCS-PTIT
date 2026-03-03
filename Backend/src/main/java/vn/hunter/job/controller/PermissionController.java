package vn.hunter.job.controller;

import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hunter.job.domain.Permission;
import vn.hunter.job.domain.response.ResultPaginationDTO;
import vn.hunter.job.service.PermissionService;
import vn.hunter.job.util.annotation.ApiMessage;
import vn.hunter.job.util.errors.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a Permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        boolean isPermissionExists = this.permissionService.fetchPermissionByApiPathAndMethod(permission);
        if (isPermissionExists) {
            throw new IdInvalidException("Permission đã tổn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @PutMapping("/permissions/{id}")
    @ApiMessage("Update a Permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission,
            @PathVariable("id") Long id)
            throws IdInvalidException {
        Optional<Permission> permissionOptional = this.permissionService.fetchById(id);
        if (permissionOptional.isEmpty()) {
            throw new IdInvalidException("Permission không tồn tại");
        }
        if (this.permissionService.fetchPermissionByApiPathAndMethod(permission)) {
            if (this.permissionService.isSameName(permission)) {
                throw new IdInvalidException("Permission đã có trong DB");
            }
        }
        return ResponseEntity.ok().body(this.permissionService.update(id, permission));
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Fetch Permission By Id")
    public ResponseEntity<Permission> getPermission(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Permission> permissionOptional = this.permissionService.fetchById(id);
        if (permissionOptional.isEmpty()) {
            throw new IdInvalidException("Permission with id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(this.permissionService.getPermission(id));
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch All Permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.getAllPermissions(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a Permission")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws Throwable {
        Optional<Permission> permissionOptional = this.permissionService.fetchById(id);
        if (permissionOptional.isEmpty()) {
            throw new IdInvalidException("Permission with id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(null);
    }
}
