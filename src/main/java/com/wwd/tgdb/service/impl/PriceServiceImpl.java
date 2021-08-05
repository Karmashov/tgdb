package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.GPL.GPLUpload;
import com.wwd.tgdb.model.GPL.Price;
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
    public String uploadGPL(File file) {
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

        //@TODO Сделать Response
        return "GPL uploaded";
    }

    @Override
    public String getPrice(String partnumber) {
        Price result;
        //@TODO Exception, вынести общую логику
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
        //@TODO Exception, вынести общую логику
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

    @Override
    public String getPriceRub(String partnumber, String discount, String dateOfRate) {
        BigDecimal price = new BigDecimal(getPriceWithDiscount(partnumber, discount));

        LocalDate date = LocalDate.now();
        if (dateOfRate.equalsIgnoreCase("завтра")) {
            date = date.plusDays(1);
        }

        double rate = getRates(date);
        if (rate != 0) {
            return price.multiply(BigDecimal.valueOf(rate)).setScale(2, RoundingMode.UP).toString();
        } else {
            return "Курс отсутствует на сайте ЦБ";
        }
    }

    private double getRates(LocalDate date) {
        if (rateRepository.existsByDate(date)) {
            return rateRepository.findFirstByDate(date).getRate();
        } else {
            String result = rateService.downloadRates();
            if (result.contains(date.toString())) {
                return getRates(date);
            }
            return 0;
        }
    }
}
