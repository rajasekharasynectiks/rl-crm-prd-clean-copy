package com.rlabs.crm.service.rbac;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Role;
import com.rlabs.crm.domain.User;
import com.rlabs.crm.helper.RbacHelper;
import com.rlabs.crm.payload.request.rbac.*;
import com.rlabs.crm.repository.UserRepository;
import com.rlabs.crm.service.UserDetailsImpl;
import com.rlabs.crm.util.EncryptDecryptUtil;
import com.rlabs.crm.util.RandomUtil;
import com.rlabs.crm.web.rest.errors.mail.EmailDeliveryFailureException;
import com.rlabs.crm.web.rest.errors.security.IncorrectPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Value("${rbac.tmp-password-length}")
    private Integer tmpPasswordLength;

    @Autowired
    private RbacHelper rbacHelper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AccountStatusUserDetailsChecker accountStatusUserDetailsChecker;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EncryptDecryptUtil encryptDecryptUtil;

    @Transactional
    public User addUser(AddUserRequest request) {
        log.debug("Adding new user");
        List<Role> roles = new ArrayList<>();
        if(request.getRole() != null && request.getRole().size() > 0){
            roles = roleService.findAllById(request.getRole().stream().toList());
        }

        User user = User.builder()
            .username(request.getEmail())
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .password(encoder.encode(request.getPassword()))
            .tempPassword(encryptDecryptUtil.encrypt(request.getPassword()))
            .enabled(true)
            .build();
        if(roles != null && roles.size() > 0){
            user.setRoles(roles.stream().collect(Collectors.toSet()));
        }
        user = userRepository.save(user);
        log.debug("New user added successfully");
        boolean isMailSend = rbacHelper.sendNewUserMail(user.getUsername(), request.getPassword());
        if(!isMailSend){
            log.error("Email delivery failure for new user registration");
            throw new EmailDeliveryFailureException();
        }
        return user;
    }

    @Transactional
    public User changePassword(ChangePasswordRequest request) {
        log.debug("Changing password");
        User user = userRepository.findByUsername(request.getUsername()).get();
        boolean matches = encoder.matches(request.getOldPassword(), user.getPassword());
        if(!matches){
            log.error("User's old password does not match. Username: {}", request.getUsername());
            throw new IncorrectPasswordException();
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        user.setTempPassword(encryptDecryptUtil.encrypt(request.getNewPassword()));
        user = userRepository.save(user);
        log.debug("Password changed");
        return user;
    }

    @Transactional
    public User resetUserPassword(ResetUserPasswordRequest request) {
        log.debug("Changing user password");
        User user = userRepository.findById(request.getId()).get();
        user.setPassword(encoder.encode(request.getPassword()));
        user.setTempPassword(encryptDecryptUtil.encrypt(request.getPassword()));
        user = userRepository.save(user);
        log.debug("Password changed successfully");
        return user;
    }

    public User validate(String username){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found. username: %s", username)));
        accountStatusUserDetailsChecker.check(UserDetailsImpl.build(user));
        return user;
    }

    public User validate(String username, String password){
        User user = userRepository.findByUsernameAndPassword(username, encoder.encode(password))
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found. username: %s",username)));
        accountStatusUserDetailsChecker.check(UserDetailsImpl.build(user));
        return user;
    }

    @Transactional
    public User enableMfa(EnableMfaRequest enableMfaRequest){
        User user = userRepository.findByUsername(enableMfaRequest.getUsername()).get();
        user.setMfaKey(enableMfaRequest.getMfaKey());
        user.setMfaEnabled(true);
        user = userRepository.save(user);
        log.debug("MFA enabled");
        return user;
    }

    @Transactional
    public boolean disableMfa(String username){
        User user = userRepository.findByUsername(username).get();
        user.setMfaKey(null);
        user.setMfaEnabled(false);
        user = userRepository.save(user);
        log.debug("MFA disabled");
        return true;
    }

    @Transactional
    public User resetPassword(ResetPasswordRequest request) {
        log.debug("Resetting password");
        User user = userRepository.findByUsername(request.getUsername()).get();
        user.setPassword(encoder.encode(request.getNewPassword()));
        user.setTempPassword(encryptDecryptUtil.encrypt(request.getNewPassword()));
        user = userRepository.save(user);
        log.debug("Password reset");
        return user;
    }

    public List<User> getAllUsers(){
        log.debug("Getting all users");
        return userRepository.findAll();
    }

    @Transactional
    public synchronized User updateLastLoginDetails(Long userId){
        log.debug("Updating login time and login count");
        User user = userRepository.findById(userId).get();
        Integer loginCount = user.getLoginCount() != null ? user.getLoginCount() : 0;
        loginCount = loginCount+1;
        user.setLoginCount(loginCount);
        user.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public User editUser(EditUserRequest request) {
        log.debug("Updating user. User id: {}",request.getId());
        User user = userRepository.findById(request.getId()).get();
        if(!StringUtils.isBlank(request.getEmail())){
            rbacHelper.validateAllowedDomain(user.getUsername());
        }

        if(request.getRole() != null && request.getRole().size() > 0){
            List<Role> roles = roleService.findAllById(request.getRole().stream().toList());
            user.setRoles(roles.stream().collect(Collectors.toSet()));
        }

        if(!StringUtils.isBlank(request.getFirstName())){
            user.setFirstName(request.getFirstName());
        }
        if(!StringUtils.isBlank(request.getLastName())){
            user.setLastName(request.getLastName());
        }
        if(!StringUtils.isBlank(request.getEmail())){
            user.setEmail(request.getEmail());
        }
        user = userRepository.save(user);
        log.debug("User updated");
        return user;
    }

    @Transactional
    public User disableUser(Long userId){
        log.debug("Disabling user. User id: {}", userId);
        User user = userRepository.findById(userId).get();
        user.setEnabled(false);
        return userRepository.save(user);
    }

    @Transactional
    public boolean enableMFAToAllUsers(){
        log.debug("Enforcing MFA to all users");
        List<User> userList = userRepository.findAll();
        List<User> updatedList = userList.stream().peek(obj -> obj.setMfaEnforced(true)).toList();
        userRepository.saveAll(updatedList);
        return true;
    }

    @Transactional
    public boolean disableMFAToAllUsers(){
        log.debug("Disabling MFA from all users");
        List<User> userList = userRepository.findAll();
        List<User> updatedList = userList.stream().peek(obj -> obj.setMfaEnforced(false)).toList();
        userRepository.saveAll(updatedList);
        return true;
    }

    public User getUser(Long id){
        log.debug("Getting user. Id: {}",id);
        return userRepository.findById(id).get();
    }

    @Transactional
    public boolean saveFile(Path newDir, User user, MultipartFile file) throws IOException {
        String extension= file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String fileName = user.getFirstName()+"_"+user.getId()+"."+extension;
        Path path = Paths.get(newDir.toString() + File.separatorChar + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        user.setProfileImageFileName(fileName);
        user.setProfileImageAccessUri(newDir.toString());
        user.setProfileImageLocation(Constants.LOCAL_STORAGE);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean deleteFile(User user) throws IOException {
        if(Constants.LOCAL_STORAGE.equalsIgnoreCase(user.getProfileImageLocation())){
            Path path = Paths.get(user.getProfileImageAccessUri() + File.separatorChar + user.getProfileImageFileName());
            Files.delete(path);
            user.setProfileImageAccessUri(null);
            user.setProfileImageFileName(null);
            user.setProfileImageLocation(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public byte[] getFile(User user) throws IOException {
        if(Constants.LOCAL_STORAGE.equalsIgnoreCase(user.getProfileImageLocation())){
            Path path = Paths.get(user.getProfileImageAccessUri() + File.separatorChar + user.getProfileImageFileName());
            return Files.readAllBytes(path);
        }
        return null;
    }

}
