package com.wwd.tgdb.config;

import com.wwd.tgdb.bot.Bot;
import com.wwd.tgdb.bot.BotConfig;
import com.wwd.tgdb.model.User;
import com.wwd.tgdb.model.enumerated.UserRole;
import com.wwd.tgdb.repository.UserRepository;
import com.wwd.tgdb.scheduled.ScheduledTasks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    private BotConfig botConfig;
    private UserRepository userRepository;
    private RRCConfig rrcConfig;
    @Value("${upload.path}")
    private String uploadPath;

    public AppConfig(BotConfig botConfig,
                     UserRepository userRepository,
                     RRCConfig rrcConfig) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.rrcConfig = rrcConfig;
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

    @Bean
    public ScheduledTasks scheduledTasks(){
        ScheduledTasks tasks = new ScheduledTasks();
        tasks.setRrcLogin(rrcConfig.getLogin());
        tasks.setRrcPassword(rrcConfig.getPassword());
        tasks.setRrcCode(rrcConfig.getCode());
        tasks.setRrcId(rrcConfig.getId());
        tasks.setUploadPath(uploadPath);
        return tasks;
    }
}
