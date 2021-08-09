package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.dto.QuestionResponse;
import com.wwd.tgdb.dto.Response;
import com.wwd.tgdb.exception.EntityNotFoundException;
import com.wwd.tgdb.model.GPL.GPLUpload;
import com.wwd.tgdb.model.GPL.Price;
import com.wwd.tgdb.model.UsdRate;
import com.wwd.tgdb.repository.CategoryRepository;
import com.wwd.tgdb.repository.GPLUploadRepository;
import com.wwd.tgdb.repository.PriceRepository;
import com.wwd.tgdb.repository.UsdRateRepository;
import com.wwd.tgdb.service.PriceService;
import com.wwd.tgdb.service.UsdRateService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final CategoryRepository categoryRepository;
    private final GPLUploadRepository gplUploadRepository;
    private final UsdRateRepository rateRepository;
    private final UsdRateService rateService;

    public PriceServiceImpl(PriceRepository priceRepository,
                            CategoryRepository categoryRepository,
                            GPLUploadRepository gplUploadRepository,
                            UsdRateRepository rateRepository,
                            UsdRateService rateService) {
        this.priceRepository = priceRepository;
        this.categoryRepository = categoryRepository;
        this.gplUploadRepository = gplUploadRepository;
        this.rateRepository = rateRepository;
        this.rateService = rateService;
    }

    @Override
    public Response uploadGPL(File file) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            XMLItemHandler handler = new XMLItemHandler(priceRepository, categoryRepository);
            XMLCategoryHandler categoryHandler = new XMLCategoryHandler(categoryRepository);

            categoryRepository.deleteAll();
            parser.parse(file, categoryHandler);
            priceRepository.deleteAll();
            parser.parse(file, handler);
            GPLUpload upload = new GPLUpload();
            upload.setUploadDate(LocalDateTime.now());
            gplUploadRepository.save(upload);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return new Response("GPL загружен");
    }

    @Override
    public Response getPrice(String partnumber, int discount, LocalDate dateOfRate) throws EntityNotFoundException {
        BigDecimal price = getResultPrice(partnumber, discount);

        String response;
        if (dateOfRate != null) {
            response = "Цена " + partnumber + " со скидкой " + discount + "%, по курсу на ";
            response = getRubPrice(price, dateOfRate, response);
        } else if (discount != 0) {
            response = "Цена " + partnumber + " со скидкой " + discount + "%: $" + price;
            return new QuestionResponse(response + "\n\nИли дать цену в ₽?",
                    new String[]{"Да", "Нет"},
                    new String[]{"сегодня"},
                    3);
        } else {
            response = "GPL " + partnumber + ": $" + price;
        }

        return new Response(response);
    }

    private String getRubPrice(BigDecimal price, LocalDate dateOfRate, String response) {
        UsdRate rate = getRates(dateOfRate);

        price = price.multiply(BigDecimal.valueOf(rate.getRate())).setScale(2, RoundingMode.UP);

        LocalDate date = rate.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        if (!rate.getDate().equals(dateOfRate)) {
            //TODO эксепшен
            return "Курс на " + dateOfRate.format(formatter) + " не найден.\n\n" +
                    response + date.format(formatter) + ": ₽" + price;
        } else if (rate.getDate().equals(dateOfRate) && dateOfRate.equals(LocalDate.now())) {
            return response + "сегодня: ₽" + price;
        } else {
            return response + "завтра: ₽" + price;
        }
    }

    private BigDecimal getResultPrice(String partnumber, int discount) throws EntityNotFoundException {
        Price result;
        if (priceRepository.existsByPartnumber(partnumber)) {
            result = priceRepository.findFirstByPartnumber(partnumber);
        } else {
            throw new EntityNotFoundException();
        }

        return BigDecimal.valueOf(result.getPriceUsd() * (100 + discount) / 100)
                .setScale(2, RoundingMode.UP);
    }

    private UsdRate getRates(LocalDate date) {
        rateService.downloadRates();
        return rateRepository.existsByDate(date) ?
                rateRepository.findFirstByDate(date) : rateRepository.findTopByOrderByIdDesc();
    }
}
