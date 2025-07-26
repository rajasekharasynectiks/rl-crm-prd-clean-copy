package com.rlabs.crm.defaultdata.sysuser;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.defaultdata.permissions.PermissionCache;
import com.rlabs.crm.domain.Permission;
import com.rlabs.crm.domain.Role;
import com.rlabs.crm.domain.User;
import com.rlabs.crm.repository.PermissionRepository;
import com.rlabs.crm.repository.RoleRepository;
import com.rlabs.crm.repository.UserRepository;
import com.rlabs.crm.util.EncryptDecryptUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@DependsOn("permissionLoader")
public class SetupSystemUser {

    @Value("${rbac.default.user.name}")
    private String defaultUser;

    @Value("${rbac.default.user.password}")
    private String defaultpassword;

    @Value("${rbac.default.role.name}")
    private String defaultRole;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EncryptDecryptUtil encryptDecryptUtil;


    @Transactional
    @PostConstruct
    public void setup(){
        if (userRepository.existsByUsername(defaultUser)) {
            log.info("Default user found");
            return;
        }

        List<Permission> permissionList = new ArrayList<>();

        for(String key: PermissionCache.getKeys()){
            List<String> permissionNames = PermissionCache.get(key);
            for(String permissionName: permissionNames){
                Permission permission = Permission.builder().category(key).name(permissionName).build();
                permissionList.add(permission);
            }
        }
        permissionList = permissionRepository.saveAll(permissionList);

        Role role = Role.builder()
            .name(defaultRole)
            .description("default admin role")
            .isDefault(true)
            .permissions(permissionList.stream().collect(Collectors.toSet()))
            .build();
        role = roleRepository.save(role);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = User.builder()
            .username(defaultUser)
            .email(defaultUser)
            .firstName(Constants.DEFAULT)
            .lastName(Constants.USER)
            .password(encoder.encode(defaultpassword))
            .tempPassword(encryptDecryptUtil.encrypt(defaultpassword))
            .roles(roles)
            .isDefault(true)
            .enabled(true)
            .build();
        userRepository.save(user);
        log.info("Default user setup done");
    }
}
