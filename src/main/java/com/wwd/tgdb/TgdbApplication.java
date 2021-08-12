package com.wwd.tgdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.ApiContextInitializer;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class TgdbApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(TgdbApplication.class, args);
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Scheduled(fixedDelay = 5000)
	public void scheduledTask() {
		System.out.println(LocalDateTime.now());
	}
}
