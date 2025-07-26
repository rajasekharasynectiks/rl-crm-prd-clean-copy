package com.rlabs.crm.controller.rbac;

import com.rlabs.crm.api.controller.SignInApi;
import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.User;
import com.rlabs.crm.helper.OtpHelper;
import com.rlabs.crm.helper.RbacHelper;
import com.rlabs.crm.payload.request.otp.OtpRequest;
import com.rlabs.crm.payload.request.otp.OtpSignInRequest;
import com.rlabs.crm.payload.request.rbac.ResetPasswordRequest;
import com.rlabs.crm.payload.request.rbac.SignInRequest;
import com.rlabs.crm.payload.response.rbac.ChangePasswordResponse;
import com.rlabs.crm.payload.response.rbac.RoleResponse;
import com.rlabs.crm.payload.response.rbac.SignInResponse;
import com.rlabs.crm.repository.RoleRepository;
import com.rlabs.crm.repository.UserRepository;
import com.rlabs.crm.security.jwt.JwtBearerTokenProvider;
import com.rlabs.crm.service.UserDetailsImpl;
import com.rlabs.crm.service.rbac.RoleService;
import com.rlabs.crm.service.rbac.UserService;
import com.rlabs.crm.util.DateTimeUtil;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.util.RandomUtil;
import com.rlabs.crm.web.rest.errors.mail.EmailDeliveryFailureException;
import com.rlabs.crm.web.rest.errors.security.*;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController implements SignInApi {

    @Value("${rbac.otp.code.length}")
    private Integer otpLength;

    @Value("${rbac.otp.code.exp-time-in-sec}")
    private long otpExpTimeInSec;

    @Autowired
    private RbacHelper rbacHelper;

    @Autowired
    private OtpHelper otpHelper;

    @Autowired
    private DateTimeUtil dateTimeUtil;

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

    @Override
    public ResponseEntity<Object> userSignIn(Object body) {
        log.info("Request of user sign-in");
        SignInRequest signInRequest = mapperUtil.convertObject(body, SignInRequest.class);
        rbacHelper.validateAllowedDomain(signInRequest.getUsername());
        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtAccessToken = jwtBearerTokenProvider.createValidTokenForUser(userDetails);
        List<RoleResponse> roleCollection = roleService.getRoleResponse(userDetails.getRoles());
        User user = userService.updateLastLoginDetails(userDetails.getId());
        SignInResponse signInResponse = getSignInResponse(user, jwtAccessToken, roleCollection);
        return ResponseEntity.ok(signInResponse);
    }

    private static SignInResponse getSignInResponse(User user, String jwtAccessToken, List<RoleResponse> roleCollection) {
        SignInResponse signInResponse = SignInResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .mfaEnabled(user.isMfaEnabled())
            .mfaEnforced(user.isMfaEnforced())
            .enabled(user.isEnabled())
            .loginCount(user.getLoginCount() == null ? 0 : user.getLoginCount())
            .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null)
            .createdOn(user.getCreatedOn() != null ? user.getCreatedOn().toString() : null)
            .token(jwtAccessToken)
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .type(Constants.BEARER)
            .roles(roleCollection)
            .build();
        return signInResponse;
    }

    @Override
    public ResponseEntity<Object> forgotPasswordOTP(String username) {
        log.info("Request to send OTP for reset password");
        if(StringUtils.isBlank(username)){
            log.error("Username missing (forgotPasswordOTP)");
            throw new UserNameNotProvidedException();
        }
        if (!userRepository.existsByUsername(username)) {
            log.error("User not found (forgotPasswordOTP). Username: {}",username);
            throw new UserNameNotFoundException();
        }
        rbacHelper.validateAllowedDomain(username);
        String otp = RandomUtil.getRandomNumber(otpLength);
        OtpRequest otpRequest = OtpRequest.builder()
            .otp(otp)
            .startTime(System.currentTimeMillis())
            .build();
        otpHelper.put(username, otpRequest);
        long minutes = dateTimeUtil.convertSecondsToMinutes(otpExpTimeInSec);
        boolean isMailSend = rbacHelper.sendForgotPasswordOTPMail(username, otp, String.valueOf(minutes));
        if(!isMailSend){
            throw new EmailDeliveryFailureException();
        }
        return ResponseEntity.status(HttpStatus.OK).body(String.format("OTP sent to user's email: %s", username));
    }

    @Override
    public ResponseEntity<Object> resetPassword(Object body) {
        log.info("Request to reset password");
        ResetPasswordRequest resetPasswordRequest = mapperUtil.convertObject(body, ResetPasswordRequest.class);
        if(StringUtils.isBlank(resetPasswordRequest.getUsername())){
            log.error("Username missing (resetPassword)");
            throw new UserNameNotProvidedException();
        }
        if(StringUtils.isBlank(resetPasswordRequest.getNewPassword())){
            log.error("Password missing (resetPassword)");
            throw new PasswordNotProvidedException();
        }
        if(StringUtils.isBlank(resetPasswordRequest.getOtp())){
            log.error("OTP missing (resetPassword)");
            throw new OtpNotProvidedException();
        }
        if(resetPasswordRequest.getOtp().length() != otpLength.intValue()){
            log.error("OTP invalid (resetPassword). OTP should have {} digits",otpLength);
            throw new OtpInvalidException();
        }
        try{
            Integer.parseInt(resetPasswordRequest.getOtp());
        }catch (NumberFormatException e){
            log.error("OTP invalid (resetPassword). OTP should have digits only");
            throw new OtpInvalidException();
        }
        if(!otpHelper.containKey(resetPasswordRequest.getUsername())){
            log.error("OTP expired. Not found in cache (resetPassword)");
            throw new OtpExpiredException();
        }
        OtpRequest otpRequest = otpHelper.get(resetPasswordRequest.getUsername());
        long timeDiff = dateTimeUtil.getTimeDifferenceInSeconds(otpRequest.getStartTime(), System.currentTimeMillis());
        if(timeDiff > otpExpTimeInSec){
            log.error("OTP expired");
            throw new OtpExpiredException();
        }
        if (!userRepository.existsByUsername(resetPasswordRequest.getUsername())) {
            log.error("User not found (resetPassword). Username: {}",resetPasswordRequest.getUsername());
            throw new UserNameNotFoundException();
        }
        User user = userService.resetPassword(resetPasswordRequest);
        otpHelper.remove(resetPasswordRequest.getUsername());
        ChangePasswordResponse changePasswordResponse = ChangePasswordResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .isPasswordChange(true)
            .build();
        return ResponseEntity.ok(changePasswordResponse);
    }

    @Override
    public ResponseEntity<Object> userOtpSignIn(Object body) {
        log.info("Request to otp-sign-in");
        OtpSignInRequest signInRequest = mapperUtil.convertObject(body, OtpSignInRequest.class);
        if(StringUtils.isBlank(signInRequest.getUsername())){
            log.error("Username missing (userOtpSignIn)");
            throw new UserNameNotProvidedException();
        }
        if(StringUtils.isBlank(signInRequest.getToken())){
            log.error("OTP missing");
            throw new MFATokenNotProvidedException();
        }
        userService.validate(signInRequest.getUsername());
        User user = userRepository.findByUsername(signInRequest.getUsername()).get();
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean matches =  gAuth.authorize(user.getMfaKey(), Integer.parseInt(signInRequest.getToken()));
        if(!matches){
            throw new OTPAuthenticationFailureException();
        }
        return ResponseEntity.ok("otp authenticated");
    }
}
