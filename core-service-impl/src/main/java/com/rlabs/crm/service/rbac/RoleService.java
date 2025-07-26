package com.rlabs.crm.service.rbac;

import com.rlabs.crm.domain.Permission;
import com.rlabs.crm.domain.Role;
import com.rlabs.crm.helper.RbacHelper;
import com.rlabs.crm.payload.request.rbac.AddRoleRequest;
import com.rlabs.crm.payload.request.rbac.EditRoleRequest;
import com.rlabs.crm.payload.request.rbac.AddPermissionRequest;
import com.rlabs.crm.payload.response.rbac.AllRoleResponse;
import com.rlabs.crm.payload.response.rbac.RoleResponse;
import com.rlabs.crm.repository.PermissionRepository;
import com.rlabs.crm.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RbacHelper rbacHelper;


    public List<Role> findAllById(List<Long> ids){
        return roleRepository.findAllById(ids);
    }

    public Role findById(Long id){
        return roleRepository.findById(id).orElse(null);
    }

    public List<AllRoleResponse> getAllRoles(){
        log.debug("Getting all roles");
        return getAllRoleResponse(roleRepository.findAll());
    }

    public List<AllRoleResponse> getAllRoleResponse(Collection<? extends Role> roles) {
        List<AllRoleResponse> roleCollection = new ArrayList<>();
        roleCollection.addAll(roles.stream()
            .map(this::getAllRoleResponse)
            .toList());
        return roleCollection;
    }
    public AllRoleResponse getAllRoleResponse(Role role) {
        Object[] userList = roleRepository.getUsersByRole(role.getId());
        AllRoleResponse roleResponse = AllRoleResponse.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .isDefault(role.getIsDefault())
            .permissions(role.getPermissions())
            .users(getUserResp(userList))
            .build();
        return roleResponse;
    }

    public List<RoleResponse> getRoleResponse(Collection<? extends Role> roles) {
        List<RoleResponse> roleCollection = new ArrayList<>();
        roleCollection.addAll(roles.stream()
            .map(this::getRoleResponse)
            .collect(Collectors.toList()));
        return roleCollection;
    }

    public RoleResponse getRoleResponse(Role role) {
        return RoleResponse.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .isDefault(role.getIsDefault())
            .permissions(role.getPermissions())
            .build();
    }

    private List<AllRoleResponse.UserResp> getUserResp(Object[] users) {
        List<AllRoleResponse.UserResp> userCollection = new ArrayList<>();
        for(int i=0; i<users.length; i++){
            Object o[] = (Object[]) users[i];
            AllRoleResponse.UserResp userResp = AllRoleResponse.UserResp.builder()
                .id((Long)o[0])
                .username((String)o[1])
                .firstName((String)o[2])
                .lastName((String)o[3])
                .build();
            userCollection.add(userResp);
        }
        return userCollection;
    }

    @Transactional
    public Role addRole(AddRoleRequest request) {
        log.debug("Adding new role");
        Set<Role> roles = new HashSet<>();
        List<Permission> permissionList = new ArrayList<>();
        for(AddPermissionRequest addPermissionRequest : request.getPermissions()){
            Permission permission = Permission.builder().category(addPermissionRequest.getCategory()).name(addPermissionRequest.getName()).build();
            permissionList.add(permission);
            permissionList = permissionRepository.saveAll(permissionList);
        }

        Role role = Role.builder()
            .name(request.getName())
            .description(request.getDescription())
            .isDefault(false)
            .permissions(permissionList.stream().collect(Collectors.toSet()))
            .build();
        role = roleRepository.save(role);
        log.debug("New role added");
        return role;
    }

    @Transactional
    public Role editRole(EditRoleRequest request) {
        log.debug("Updating role. Role id: {}",request.getId());
        Role role = roleRepository.findById(request.getId()).get();
        Set<Permission> permissionSet = role.getPermissions();

        if(request.getPermissions() != null && request.getPermissions().size() > 0){
            List<Permission> permissionList = new ArrayList<>();
            // clean up all permissions from roles_permissions
            role.setPermissions(null);
            role = roleRepository.save(role);
            // clean up from permissions
            permissionRepository.deleteAllById(permissionSet.stream().map(obj -> obj.getId()).collect(Collectors.toList()));
            for(AddPermissionRequest addPermissionRequest : request.getPermissions()){
                Permission permission = Permission.builder().category(addPermissionRequest.getCategory()).name(addPermissionRequest.getName()).build();
                permissionList.add(permission);
                permissionList = permissionRepository.saveAll(permissionList);
            }
            role.setPermissions(permissionList.stream().collect(Collectors.toSet()));
        }

        if(!StringUtils.isBlank(request.getName())){
            role.setName(request.getName());
        }
        if(!StringUtils.isBlank(request.getDescription())){
            role.setDescription(request.getDescription());
        }

        role = roleRepository.save(role);
        log.debug("Role updated");
        return role;
    }

    @Transactional
    public Role deleteRole(Long roleId){
        log.debug("Deleting role. Role id: {}",roleId);
        Object[] userList = roleRepository.getUsersByRole(roleId);
        Role role = roleRepository.findById(roleId).get();
        roleRepository.deleteRolesPermissions(roleId);
        roleRepository.deleteUsersRoles(roleId);
        permissionRepository.deleteAllById(role.getPermissions().stream().map(obj -> obj.getId()).collect(Collectors.toList()));
        roleRepository.deleteById(roleId);
        rbacHelper.sendDeleteRoleNotificationMail(userList, role.getName());
        log.debug("Role deleted");
        return role;
    }
}
