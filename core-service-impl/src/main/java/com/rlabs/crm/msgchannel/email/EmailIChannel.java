package com.rlabs.crm.msgchannel.email;

import com.rlabs.crm.msgchannel.Message;
import com.rlabs.crm.msgchannel.IChannel;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class EmailIChannel implements IChannel {

    @Autowired
    public JavaMailSender emailSender;

    @Override
    public boolean sendMessage(Message message) {
        log.debug("Entered sendMessage method with message {}", message);
        try {
            MimeMessage mail = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setFrom(message.getFromAddress());
            helper.setTo(message.getToAddress());
            helper.setSubject(message.getSubject());
            helper.setText(message.getMessage(), message.isHtml());
            long start = System.currentTimeMillis();
            emailSender.send(mail);
            log.info("Total time taken to send a email ::{} is :: {} ms", message.getToAddress(),
                    (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("Problem with sending email to: {}, error: {}", message.getToAddress(), e);
            return false;
        }
        return true;
    }
}
