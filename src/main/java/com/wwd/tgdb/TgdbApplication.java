package com.wwd.tgdb;

import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.repository.*;
import com.wwd.tgdb.service.MessageReceiver;
import com.wwd.tgdb.service.MessageSender;
import com.wwd.tgdb.service.impl.AdminService;
import com.wwd.tgdb.service.impl.MessageService;
import com.wwd.tgdb.service.impl.PriceService;
import com.wwd.tgdb.service.impl.UsdRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class TgdbApplication {

	private static String username = "ciscodbbot";
	private static String token = "1427942456:AAGYexfiYcYoGOf3r8h1TD_uYPXWIX8N2x0";

	private static final int PRIORITY_FOR_SENDER = 1;
	private static final int PRIORITY_FOR_RECEIVER = 3;
	private static ChatRepository chatRepository;
	private static MessageRepository messageRepository;
	private static UserRepository userRepository;
	private static JavaMailSender mailSender;
	private static PriceRepository priceRepository;
	private static UsdRateRepository rateRepository;

	@Autowired
	public TgdbApplication(ChatRepository chatRepository,
						   MessageRepository messageRepository,
						   UserRepository userRepository,
						   JavaMailSender mailSender,
						   PriceRepository priceRepository,
						   UsdRateRepository rateRepository) {
		TgdbApplication.chatRepository = chatRepository;
		TgdbApplication.messageRepository = messageRepository;
		TgdbApplication.userRepository = userRepository;
		TgdbApplication.mailSender = mailSender;
		TgdbApplication.priceRepository = priceRepository;
		TgdbApplication.rateRepository = rateRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(TgdbApplication.class, args);
		botInit();
	}

	private static void botInit() {
		ApiContextInitializer.init();
		Bot bot = new Bot(username, token);

		AdminService adminService = new AdminService(bot, chatRepository, messageRepository, userRepository, mailSender);
		MessageService messageService = new MessageService(bot, chatRepository, messageRepository, userRepository);
		PriceService priceService = new PriceService(bot, priceRepository, rateRepository);
		UsdRateService rateService = new UsdRateService(bot, rateRepository);

		MessageReceiver messageReceiver = new MessageReceiver(bot, chatRepository, userRepository, adminService, messageService, priceService, rateService);
		MessageSender messageSender = new MessageSender(bot);


		bot.botConnect();

		Thread receiver = new Thread(messageReceiver);
		receiver.setDaemon(true);
		receiver.setName("MsgReceiver");
		receiver.setPriority(PRIORITY_FOR_RECEIVER);
		receiver.start();

		Thread sender = new Thread(messageSender);
		sender.setDaemon(true);
		sender.setName("MsgSender");
		sender.setPriority(PRIORITY_FOR_SENDER);
		sender.start();
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}
