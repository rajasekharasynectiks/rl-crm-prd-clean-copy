package com.rlabs.crm.controller.rbac;

import com.rlabs.crm.api.controller.RoleApi;
import com.rlabs.crm.domain.Role;
import com.rlabs.crm.payload.request.rbac.AddRoleRequest;
import com.rlabs.crm.payload.request.rbac.EditRoleRequest;
import com.rlabs.crm.repository.RoleRepository;
import com.rlabs.crm.service.rbac.RoleService;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.BadRequestAlertException;
import com.rlabs.crm.web.rest.errors.security.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api")
public class RoleController implements RoleApi {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private RoleService roleService;

    @Override
    public ResponseEntity<Object> getAllRoles(){
        log.info("Request to get all roles");
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getAllRoles());
    }

    @Override
    public ResponseEntity<Object> addRole(Object body) {
        log.info("Request to create new role");
        AddRoleRequest addRoleRequest = mapperUtil.convertObject(body, AddRoleRequest.class);
        if (StringUtils.isBlank(addRoleRequest.getName())) {
            log.error("Role name missing");
            throw new BadRequestAlertException("Role Name missing", "roles", "nullrolename");
        }
        if (roleRepository.existsByName(addRoleRequest.getName())) {
            log.error("Role already exist");
            throw new RoleExistException();
        }
        Role role = roleService.addRole(addRoleRequest);
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getRoleResponse(role));
    }

    @Override
    public ResponseEntity<Object> editRole(Object body) {
        log.info("Request to edit role");
        EditRoleRequest editRoleRequest = mapperUtil.convertObject(body, EditRoleRequest.class);
        if (Objects.isNull(editRoleRequest.getId())) {
            log.error("Role id missing");
            throw new BadRequestAlertException("Role id missing", "roles", "nullroleid");
        }
        if(!roleRepository.existsById(editRoleRequest.getId())){
            log.error("role not found");
            throw new RoleNotFoundException();
        }
        Role role = roleService.editRole(editRoleRequest);
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getRoleResponse(role));
    }

    @Override
    public ResponseEntity<Object> deleteRole(Long roleId){
        log.info("Request to delete role");
        if (Objects.isNull(roleId)) {
            log.error("Role id missing");
            throw new BadRequestAlertException("Role id missing", "roles", "nullroleid");
        }
        if(!roleRepository.existsById(roleId)){
            log.error("role not found");
            throw new RoleNotFoundException();
        }
        Role role = roleService.deleteRole(roleId);
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getRoleResponse(role));
    }

}
