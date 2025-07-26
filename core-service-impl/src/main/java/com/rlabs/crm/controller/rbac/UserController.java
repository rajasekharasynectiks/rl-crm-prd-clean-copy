package com.rlabs.crm.controller.rbac;

import com.google.zxing.WriterException;
import com.rlabs.crm.api.controller.UserApi;
import com.rlabs.crm.domain.User;
import com.rlabs.crm.helper.RbacHelper;
import com.rlabs.crm.mfa.CustomGoogleMFAService;
import com.rlabs.crm.payload.request.rbac.*;
import com.rlabs.crm.payload.response.*;
import com.rlabs.crm.payload.response.rbac.*;
import com.rlabs.crm.repository.RoleRepository;
import com.rlabs.crm.repository.UserRepository;
import com.rlabs.crm.security.jwt.JwtBearerTokenProvider;
import com.rlabs.crm.service.rbac.RoleService;
import com.rlabs.crm.service.rbac.UserService;
import com.rlabs.crm.util.EncryptDecryptUtil;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.BadRequestAlertException;
import com.rlabs.crm.web.rest.errors.MandatoryFieldMissingException;
import com.rlabs.crm.web.rest.errors.file.FileDeletionFailedException;
import com.rlabs.crm.web.rest.errors.file.FileDownFailedException;
import com.rlabs.crm.web.rest.errors.file.FileUploadFailedException;
import com.rlabs.crm.web.rest.errors.security.*;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController implements UserApi {

    @Value("${org.name}")
    private String orgName;

    @Value("${file-path.image}")
    private String filePathImage;

    @Autowired
    private RbacHelper rbacHelper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtBearerTokenProvider jwtBearerTokenProvider;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EncryptDecryptUtil encryptDecryptUtil;

    @Autowired
    private CustomGoogleMFAService customGoogleMFAService;

    @Override
    public ResponseEntity<Object> addUser(Object body) {
        log.info("Request to create new user");
        AddUserRequest addUserRequest = mapperUtil.convertObject(body, AddUserRequest.class);
        if (StringUtils.isBlank(addUserRequest.getFirstName())) {
            log.error("First name missing");
            throw new MandatoryFieldMissingException("User","first name");
        }
        if (StringUtils.isBlank(addUserRequest.getEmail())) {
            log.error("Email-id missing");
            throw new MandatoryFieldMissingException("User","email-id");
        }
        if (StringUtils.isBlank(addUserRequest.getPassword())) {
            log.error("Password missing");
            throw new MandatoryFieldMissingException("User","password");
        }
        if (userRepository.existsByUsername(addUserRequest.getEmail())) {
            log.error("Username already exist");
            throw new UserNameExistException();
        }
        rbacHelper.validateAllowedDomain(addUserRequest.getEmail());
        User user = userService.addUser(addUserRequest);
        List<RoleResponse> roleCollection = roleService.getRoleResponse(user.getRoles());
        AddUserResponse addUserResponse = AddUserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(roleCollection)
            .isEnabled(user.isEnabled())
            .isMfaEnabled(user.isMfaEnabled())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(addUserResponse);
    }

    @Override
    public ResponseEntity<Object> changePassword(Object body) {
        ChangePasswordRequest changePasswordRequest = mapperUtil.convertObject(body, ChangePasswordRequest.class);
        log.info("Request to change password. username: {}", changePasswordRequest.getUsername());
        if (!userRepository.existsByUsername(changePasswordRequest.getUsername())) {
            log.error("User not found. Username: {}",changePasswordRequest.getUsername());
            throw new UserNameNotFoundException();
        }
        User user = userService.changePassword(changePasswordRequest);
        ChangePasswordResponse changePasswordResponse = ChangePasswordResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .isPasswordChange(true)
            .build();
        return ResponseEntity.ok(changePasswordResponse);
    }

    @Override
    public ResponseEntity<Object> resetUserPassword(Object body) {
        ResetUserPasswordRequest resetUserPasswordRequest = mapperUtil.convertObject(body, ResetUserPasswordRequest.class);
        log.info("Request to reset password");
        if (resetUserPasswordRequest.getId() == null) {
            log.error("User id missing");
            throw new MandatoryFieldMissingException("User","id");
        }
        if (StringUtils.isBlank(resetUserPasswordRequest.getPassword())) {
            log.error("Password missing");
            throw new MandatoryFieldMissingException("User","password");
        }
        if (!userRepository.existsById(resetUserPasswordRequest.getId())) {
            log.error("User not found. User id: {}",resetUserPasswordRequest.getId());
            throw new UserNotFoundException();
        }
        User user = userService.resetUserPassword(resetUserPasswordRequest);
        ChangePasswordResponse changePasswordResponse = ChangePasswordResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .isPasswordChange(true)
            .build();
        return ResponseEntity.ok(changePasswordResponse);
    }

    @Override
    public ResponseEntity<Object> decryptPassword(String username) {
        log.info("Request to decrypt password");
        Optional<User> oUser = userRepository.findByUsername(username);
        if (!oUser.isPresent()) {
            log.error("User not found (decryptPassword). Username: {}",username);
            throw new UserNameNotFoundException();
        }
        return ResponseEntity.ok(encryptDecryptUtil.decrypt(oUser.get().getTempPassword()));
    }

    @Override
    public ResponseEntity<Object> generateMfaQRCode(String username) {
        log.info("Request to enable MFA");
        Optional<User> oUser = userRepository.findByUsername(username);
        if (!oUser.isPresent()) {
            log.error("User not found (generateMfaQRCode). Username: {}",username);
            throw new UserNameNotFoundException();
        }
        String mfaKey = customGoogleMFAService.getGoogleAuthenticationKey(username);
        int size = 125;
        String fileType = "png";
        String keyUri = customGoogleMFAService.generateGoogleAuthenticationUri(username,orgName, mfaKey);
        File tempFile = null;
        MFAResponse mfaResponse;
        try{
            tempFile = File.createTempFile(username, "." + fileType);
            customGoogleMFAService.createQRImage(tempFile, keyUri, size, fileType);
            mfaResponse = MFAResponse.builder()
                .mfaKey(mfaKey)
                .mfa(Files.readAllBytes(tempFile.toPath()))
                .build();
        }catch (WriterException | IOException e){
            log.error("Exception in enable mfa: ",e);
            throw new RuntimeException(e);
        }finally {
            if(tempFile != null){
                tempFile.delete();
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(mfaResponse);
    }

    @Override
    public ResponseEntity<Object> enableMfa(Object body) {
        EnableMfaRequest enableMfaRequest = mapperUtil.convertObject(body, EnableMfaRequest.class);
        log.info("Request to enable MFA. username: {}", enableMfaRequest.getUsername());
        if(StringUtils.isBlank(enableMfaRequest.getUsername())){
            log.error("Username missing (enableMfa)");
            throw new UserNameNotProvidedException();
        }
        if(StringUtils.isBlank(enableMfaRequest.getMfaKey())){
            log.error("MFA key missing");
            throw new MFAKeyNotProvidedException();
        }
        if(enableMfaRequest.getToken() == null){
            log.error("MFA token missing");
            throw new MFATokenNotProvidedException();
        }

        if (!userRepository.existsByUsername(enableMfaRequest.getUsername())) {
            log.error("User not found (enableMfa). Username: {}",enableMfaRequest.getUsername());
            throw new UserNameNotFoundException();
        }
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean matches =  gAuth.authorize(enableMfaRequest.getMfaKey(), enableMfaRequest.getToken());
        if(!matches){
            throw new MFAEnableException();
        }
        userService.enableMfa(enableMfaRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Enabling MFA Successful");
    }

    @Override
    public ResponseEntity<Object> disableMfa(String username) {
        log.info("Request to disable MFA. username: {}", username);
        if(StringUtils.isBlank(username)){
            log.error("Username missing (disableMfa)");
            throw new UserNameNotProvidedException();
        }
        if (!userRepository.existsByUsername(username)) {
            log.error("User not found (disableMfa). Username: {}",username);
            throw new UserNameNotFoundException();
        }
        boolean isDisabled = userService.disableMfa(username);
        if(!isDisabled){
            throw new MFADisableException();
        }
        return ResponseEntity.status(HttpStatus.OK).body("Disabling MFA Successful");
    }

    @Override
    public ResponseEntity<Object> getAllUsers() {
        log.info("Request to get all users");
        List<User> userList = userService.getAllUsers();
        List<AddUserResponse> userResponses = new ArrayList<>();
        for(User user: userList){
            List<RoleResponse> roleCollection = roleService.getRoleResponse(user.getRoles());
            AddUserResponse addUserResponse = AddUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleCollection)
                .isEnabled(user.isEnabled())
                .isMfaEnabled(user.isMfaEnabled())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isDefault(user.isDefault())
                .build();
            userResponses.add(addUserResponse);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    @Override
    public ResponseEntity<Object> editUser(Object body) {
        EditUserRequest editUserRequest = mapperUtil.convertObject(body, EditUserRequest.class);
        log.info("Request to update user. User id: {}", editUserRequest.getId());
        if (Objects.isNull(editUserRequest.getId())) {
            log.error("User id missing");
            throw new BadRequestAlertException("User id missing", "users", "nulluserid");
        }
        if(!userRepository.existsById(editUserRequest.getId())){
            log.error("User not found");
            throw new UserNotFoundException();
        }
        User user = userService.editUser(editUserRequest);

        List<RoleResponse> roleCollection = roleService.getRoleResponse(user.getRoles());
        AddUserResponse addUserResponse = AddUserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(roleCollection)
            .isEnabled(user.isEnabled())
            .isMfaEnabled(user.isMfaEnabled())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(addUserResponse);
    }

    @Override
    public ResponseEntity<Object> disableUser(Long userId) {
        log.info("Request to disable user. User id: {}", userId);
        if (Objects.isNull(userId)) {
            log.error("User id missing (disableUser)");
            throw new BadRequestAlertException("User id missing", "users", "nulluserid");
        }
        if(!userRepository.existsById(userId)){
            log.error("User not found (disableUser)");
            throw new UserNotFoundException();
        }
        User user = userService.disableUser(userId);
        List<RoleResponse> roleCollection = roleService.getRoleResponse(user.getRoles());
        DisableUserResponse disableUserResponse = DisableUserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(roleCollection)
            .enabled(user.isEnabled())
            .mfaEnabled(user.isMfaEnabled())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .loginCount(user.getLoginCount() == null ? 0 : user.getLoginCount())
            .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null)
            .createdOn(user.getCreatedOn() != null ? user.getCreatedOn().toString() : null)
            .build();
        return ResponseEntity.status(HttpStatus.OK).body(disableUserResponse);
    }

    @Override
    public ResponseEntity<Object> enableMFAToAllUsers(){
        log.info("Request to enforce MFA to all users.");
        boolean isDone = userService.enableMFAToAllUsers();
        if(!isDone){
            log.error("Enforcing MFA to all users failed");
            throw new EnforceMFAEnableException();
        }
        MessageResponse messageResponse = MessageResponse.builder().message("MFA enabled to all users").build();
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @Override
    public ResponseEntity<Object> disableMFAToAllUsers(){
        log.info("Request to disable MFA from all users.");
        boolean isDone = userService.disableMFAToAllUsers();
        if(!isDone){
            log.error("Disabling MFA from all users failed");
            throw new EnforceMFADisableException();
        }
        MessageResponse messageResponse = MessageResponse.builder().message("MFA disabled from all users").build();
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @Override
    public ResponseEntity<Object> uploadUserProfileImage(Long id, MultipartFile file) {
        log.info("Request to upload user profile image. User id: {}", id);
        if (Objects.isNull(id)) {
            log.error("User id missing");
            throw new MandatoryFieldMissingException("Users", "id");
        }
        if(!userRepository.existsById(id)){
            log.error("User not found");
            throw new UserNotFoundException();
        }
        User user = userService.getUser(id);
        try {
            Path projectDir = Paths.get(System.getProperty("user.dir"));
            Path newDir = projectDir.resolve(filePathImage+File.separatorChar+"users");
            if (!Files.exists(newDir)) {
                Files.createDirectories(newDir);
            }
            boolean response = userService.saveFile(newDir, user, file);
            return ResponseEntity.ok("File saved");
        } catch (Exception e) {
            log.error("Failed to upload file. ", e);
            throw new FileUploadFailedException();
        }
    }

    @Override
    public ResponseEntity<Object> deleteUserProfileImage(Long id) {
        log.info("Request to delete user profile image. User id: {}", id);
        if (Objects.isNull(id)) {
            log.error("User id missing");
            throw new MandatoryFieldMissingException("Users", "id");
        }
        if(!userRepository.existsById(id)){
            log.error("User not found");
            throw new UserNotFoundException();
        }
        User user = userService.getUser(id);
        try {
            boolean response = userService.deleteFile(user);
            return ResponseEntity.ok("File deleted");
        } catch (Exception e) {
            log.error("Failed to delete file. ", e);
            throw new FileDeletionFailedException();
        }
    }

    @Override
    public ResponseEntity<Object> getUserProfileImage(Long id) {
        log.info("Request to get user profile image. User id: {}", id);
        if (Objects.isNull(id)) {
            log.error("User id missing");
            throw new MandatoryFieldMissingException("Users", "id");
        }
        if(!userRepository.existsById(id)){
            log.error("User not found");
            throw new UserNotFoundException();
        }
        User user = userService.getUser(id);
        try {
            byte[] file  = userService.getFile(user);
            return ResponseEntity.ok(Base64.encodeBase64String(file));
        } catch (Exception e) {
            log.error("Failed to retrieve file. ", e);
            throw new FileDownFailedException();
        }
    }

}
