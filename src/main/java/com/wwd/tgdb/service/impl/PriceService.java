package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.model.Price;
import com.wwd.tgdb.repository.PriceRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PriceService {

    private Bot bot;
    private final PriceRepository priceRepository;

    public PriceService(Bot bot, PriceRepository priceRepository) {
        this.bot = bot;
        this.priceRepository = priceRepository;
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
        //@TODO разобраться с BigDecimal
        double price = (Double.parseDouble(String.valueOf(result.getPriceUsd())) * (100 - Integer.parseInt(discount.replaceAll("-", ""))) / 100);
//        String message = Double.toString(price);
        String message = String.format("%.2f", price);
        sendMessage(chatId, message);
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        bot.sendQueue.add(message);
    }
}
