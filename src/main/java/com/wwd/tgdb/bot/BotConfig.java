package com.wwd.tgdb.bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
public class BotConfig {
    private String userName;
    private String token;
    private String adminId;
    private String adminName;
}
