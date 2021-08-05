package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.repository.UsdRateRepository;
import com.wwd.tgdb.service.UsdRateService;
import com.wwd.tgdb.util.XMLCurrencyHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class UsdRateServiceImpl implements UsdRateService {

    private final UsdRateRepository rateRepository;

    public UsdRateServiceImpl(UsdRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    @Override
    public String downloadRates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate ld = LocalDate.now();
        String today = ld.format(formatter);
        String tomorrow = ld.plusDays(1).format(formatter);
        try {
            URL url = new URL("http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" +
                    today + "&date_req2=" + tomorrow + "&VAL_NM_RQ=R01235");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String rateResponse = br.readLine();
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                XMLCurrencyHandler handler = new XMLCurrencyHandler(rateRepository);
                parser.parse(new InputSource(new StringReader(rateResponse)), handler);
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return checkRates(ld);
    }

    private String checkRates(LocalDate ld) {
        if (rateRepository.existsByDate(ld)) {
            if (rateRepository.existsByDate(ld.plusDays(1))) {
                return "Загружен курс USD на " + ld + " и " + ld.plusDays(1);
            } else {
                return "Загружен курс USD на " + ld;
            }
        } else {
            return "Курс USD не был загружен";
        }
    }
}
