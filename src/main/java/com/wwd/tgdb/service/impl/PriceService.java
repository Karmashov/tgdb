package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.model.Price;
import com.wwd.tgdb.repository.PriceRepository;
import com.wwd.tgdb.repository.UsdRateRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class PriceService {

    private Bot bot;
    private final PriceRepository priceRepository;
    private final UsdRateRepository rateRepository;

    public PriceService(Bot bot, PriceRepository priceRepository, UsdRateRepository rateRepository) {
        this.bot = bot;
        this.priceRepository = priceRepository;
        this.rateRepository = rateRepository;
    }

    public void getPrice(String partnumber, String chatId) {
        Price result = null;
        //@TODO Exception
        if (priceRepository.existsByPartnumber(partnumber)) {
            result = priceRepository.findFirstByPartnumber(partnumber);
        } else {
            sendMessage(chatId, "Not found");
        }
        assert result != null;
        String message = result.getPriceUsd().toString();
        sendMessage(chatId, message);
    }

    public void getPriceWithDiscount(String partnumber, String discount, String chatId) {
        Price result = null;
        //@TODO Exception
        if (priceRepository.existsByPartnumber(partnumber)) {
            result = priceRepository.findFirstByPartnumber(partnumber);
        } else {
            sendMessage(chatId, "Not found");
        }
        assert result != null;
        BigDecimal price = BigDecimal.valueOf(
                result.getPriceUsd() * (100 - Integer.parseInt(discount.replaceAll("-", ""))) / 100
                ).setScale(2, RoundingMode.UP);
//        String message = Double.toString(price);
        String message = price.toString();
        sendMessage(chatId, message);
    }

    public void getPriceRub(String partnumber, String discount, String dateOfRate, String chatId) {
        Price result = null;
        //@TODO Exception
        if (priceRepository.existsByPartnumber(partnumber)) {
            result = priceRepository.findFirstByPartnumber(partnumber);
        } else {
            sendMessage(chatId, "Not found");
        }
        assert result != null;
//        double price = result.getPriceUsd() * (100 - Integer.parseInt(discount.replaceAll("-", ""))) / 100;
        BigDecimal price = BigDecimal.valueOf(
                result.getPriceUsd() * (100 - Integer.parseInt(discount.replaceAll("-", ""))) / 100
                ).setScale(2, RoundingMode.UP);
//        price = bd.doubleValue();
        LocalDate date = LocalDate.now();
        if (dateOfRate.equalsIgnoreCase("завтра")) {
            date = date.plusDays(1);
        }
        if (rateRepository.existsByDate(date)) {
            price = price.multiply(BigDecimal.valueOf(rateRepository.findFirstByDate(date).getRate()));
        } else {
            sendMessage(chatId, "Курс отсутствует на сайте ЦБ");
        }
        String message = price.toString();
        sendMessage(chatId, message);
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        bot.sendQueue.add(message);
    }
}
