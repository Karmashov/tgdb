package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.repository.UsdRateRepository;
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
public class UsdRateService {

    private final UsdRateRepository rateRepository;

    public UsdRateService(UsdRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public String getRates() {
        LocalDate ld = LocalDate.now();
        String today = ld.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String tomorrow = ld.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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

        String result = checkRates(ld);
        return result;
    }

    private String checkRates(LocalDate ld) {
        if (rateRepository.existsByDate(ld) && rateRepository.existsByDate(ld.plusDays(1))) {
            return "Загружен курс USD на " + ld + " и " + ld.plusDays(1);
        }
        return "Загружен курс USD на " + ld;
    }
}
