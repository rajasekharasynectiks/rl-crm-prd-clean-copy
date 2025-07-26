package com.rlabs.crm.helper;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.msgchannel.Message;
import com.rlabs.crm.msgchannel.MessageChannelFactory;
import com.rlabs.crm.web.rest.errors.security.ExternalDomainNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.StringTokenizer;

@Slf4j
@Component
public class RbacHelper {

    @Value("${rbac.allowed-domain}")
    private String allowedDomain;

    @Value("${spring.mail.username}")
    private String fromEmailAddress;

    @Autowired
    private TemplateEngine emailTemplateEngine;

    @Autowired
    private MessageChannelFactory messageChannelFactory;


    public void validateAllowedDomain(String username) {
        StringTokenizer stringTokenizer= new StringTokenizer(allowedDomain, ",");
        boolean isAllowed = false;
        while (stringTokenizer.hasMoreTokens()) {
            if(username.contains(stringTokenizer.nextToken().trim())){
                isAllowed = true;
            }
        }
        if(!isAllowed){
            log.error("User sign-in prohibited. domain trying to access the application is: {}, username: {}", username.split("@")[1], username);
            throw new ExternalDomainNotAllowedException("user from "+ username.split("@")[1] + " is trying to access the application");
        }
    }

    public boolean sendForgotPasswordOTPMail(String username, String otp, String validationTime) {
        final Context ctx = new Context();
        ctx.setVariable("username", username);
        ctx.setVariable("otp", otp);
        ctx.setVariable("validationTime", validationTime);

        final String htmlContent = this.emailTemplateEngine.process("html/forgot-password", ctx);

        Message message = Message.builder().fromAddress(fromEmailAddress)
            .replyTo(fromEmailAddress).toAddress(new String[] {username})
            .message(htmlContent).html(true).subject("OTP to reset password")
            .build();
        return messageChannelFactory.getInstance(Constants.EMAIL).sendMessage(message);
    }

    public boolean sendNewUserMail(String username, String password) {
        final Context ctx = new Context();
        ctx.setVariable("username", username);
        ctx.setVariable("password", password);

        final String htmlContent = this.emailTemplateEngine.process("html/new-registration.html", ctx);

        Message message = Message.builder().fromAddress(fromEmailAddress)
            .replyTo(fromEmailAddress).toAddress(new String[] {username})
            .message(htmlContent).html(true).subject("RasiLabs - New User")
            .build();
        return messageChannelFactory.getInstance(Constants.EMAIL).sendMessage(message);
    }

    public void sendDeleteRoleNotificationMail(Object[] userList, String roleName) {
        for(int i=0; i<userList.length; i++){
            String username = null;
            try{
                Object o[] = (Object[]) userList[i];
                username =  (String)o[1];
                log.debug("Sending role deletion mail to user: {}",username);
                Context ctx = new Context();
                ctx.setVariable("username", username);
                ctx.setVariable("roleName", roleName);
                final String htmlContent = this.emailTemplateEngine.process("html/delete-role-notification.html", ctx);

                Message message = Message.builder().fromAddress(fromEmailAddress)
                    .replyTo(fromEmailAddress).toAddress(new String[] {username})
                    .message(htmlContent).html(true).subject(String.format("RasiLabs - Role %s deleted From CRM Application",roleName))
                    .build();
                messageChannelFactory.getInstance(Constants.EMAIL).sendMessage(message);
            }catch (Exception e){
                log.error("Role Deletion notification mail to user: {} failed. Exception: {}",username, e.getMessage());
            }
        }
    }
}
