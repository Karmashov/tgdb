package com.wwd.tgdb.config;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.bot.BotConfig;
import com.wwd.tgdb.model.User;
import com.wwd.tgdb.model.enumerated.UserRole;
import com.wwd.tgdb.repository.UserRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    private BotConfig botConfig;
    private UserRepository userRepository;

    public AppConfig(BotConfig botConfig,
                     UserRepository userRepository) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public Bot bot() {
        Bot bot = new Bot();
        bot.setUserName(botConfig.getUserName());
        bot.setToken(botConfig.getToken());
        bot.botConnect();

        //Добавляем админа
        if (!userRepository.existsUserByUserId(botConfig.getAdminId())) {
            User admin = new User();
            admin.setUserId(botConfig.getAdminId());
            admin.setUsername(botConfig.getAdminName());
            admin.setUserRole(UserRole.ADMIN);
            userRepository.save(admin);
        }
        return bot;
    }
}
