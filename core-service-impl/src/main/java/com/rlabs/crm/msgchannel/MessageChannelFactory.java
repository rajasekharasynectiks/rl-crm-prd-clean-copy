package com.rlabs.crm.msgchannel;

import com.rlabs.crm.msgchannel.email.EmailIChannel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageChannelFactory {

    @Autowired
    private EmailIChannel emailChannel;

    public IChannel getInstance(String key) {
        if (StringUtils.isBlank(key)) {
            key = "";
        }
        switch (key) {
            case "EMAIL":
                return emailChannel;
            default:
                return emailChannel;
        }
    }
}
