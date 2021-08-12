package com.wwd.tgdb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rrc")
@Data
public class RRCConfig {
    private String login;
    private String password;
    private String code;
    private String id;
}
