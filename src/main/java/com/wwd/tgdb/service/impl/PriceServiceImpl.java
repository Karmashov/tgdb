package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.GPL.GPLUpload;
import com.wwd.tgdb.model.GPL.Price;
import com.wwd.tgdb.repository.CategoryRepository;
import com.wwd.tgdb.repository.GPLUploadRepository;
import com.wwd.tgdb.repository.PriceRepository;
import com.wwd.tgdb.repository.UsdRateRepository;
import com.wwd.tgdb.service.PriceService;
import com.wwd.tgdb.util.XMLCategoryHandler;
import com.wwd.tgdb.util.XMLItemHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final CategoryRepository categoryRepository;
    private final GPLUploadRepository gplUploadRepository;
    private final UsdRateRepository rateRepository;

    public PriceServiceImpl(PriceRepository priceRepository, CategoryRepository categoryRepository, GPLUploadRepository gplUploadRepository, UsdRateRepository rateRepository) {
        this.priceRepository = priceRepository;
        this.categoryRepository = categoryRepository;
        this.gplUploadRepository = gplUploadRepository;
        this.rateRepository = rateRepository;
    }

    @Override
    public String uploadGPL(File file) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            XMLItemHandler handler = new XMLItemHandler(priceRepository, categoryRepository);
            XMLCategoryHandler categoryHandler = new XMLCategoryHandler(categoryRepository);

            categoryRepository.deleteAll();
            parser.parse(file, categoryHandler);
            priceRepository.deleteAll();
            parser.parse(file,handler);
            GPLUpload upload = new GPLUpload();
            upload.setUploadDate(LocalDateTime.now());
            gplUploadRepository.save(upload);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        //@TODO Сделать Response
        return "GPL uploaded";
    }

    @Override
    public String getPrice(String partnumber) {
        Price result;
        //@TODO Exception
        if (priceRepository.existsByPartnumber(partnumber)) {
            result = priceRepository.findFirstByPartnumber(partnumber);
        } else {
            return "Не найдено";
        }
        return result.getPriceUsd().toString();
    }

    @Override
    public String getPriceWithDiscount(String partnumber, String discount) {
        Price result;
        //@TODO Exception
        if (priceRepository.existsByPartnumber(partnumber)) {
            result = priceRepository.findFirstByPartnumber(partnumber);
        } else {
            return "Не найдено";
        }
        BigDecimal price = BigDecimal.valueOf(
                result.getPriceUsd() * (100 - Integer.parseInt(discount.replaceAll("-", ""))) / 100
                ).setScale(2, RoundingMode.UP);
        return price.toString();
    }
//
//    public void getPriceRub(String partnumber, String discount, String dateOfRate, String chatId) {
//        Price result = null;
//        //@TODO Exception
//        if (priceRepository.existsByPartnumber(partnumber)) {
//            result = priceRepository.findFirstByPartnumber(partnumber);
//        } else {
//            sendMessage(chatId, "Not found");
//        }
//        assert result != null;
////        double price = result.getPriceUsd() * (100 - Integer.parseInt(discount.replaceAll("-", ""))) / 100;
//        BigDecimal price = BigDecimal.valueOf(
//                result.getPriceUsd() * (100 - Integer.parseInt(discount.replaceAll("-", ""))) / 100
//                ).setScale(2, RoundingMode.UP);
////        price = bd.doubleValue();
//        LocalDate date = LocalDate.now();
//        if (dateOfRate.equalsIgnoreCase("завтра")) {
//            date = date.plusDays(1);
//        }
//        if (rateRepository.existsByDate(date)) {
//            price = price.multiply(BigDecimal.valueOf(rateRepository.findFirstByDate(date).getRate()));
//        } else {
//            sendMessage(chatId, "Курс отсутствует на сайте ЦБ");
//        }
//        String message = price.toString();
//        sendMessage(chatId, message);
//    }
//
//    private void sendMessage(String chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(text);
//        bot.sendQueue.add(message);
//    }
}
