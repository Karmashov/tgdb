package com.wwd.tgdb.util;

import com.wwd.tgdb.model.UsdRate;
import com.wwd.tgdb.repository.UsdRateRepository;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class XMLCurrencyHandler extends DefaultHandler {
    private HashMap<LocalDate, Double> currency;
    private final UsdRateRepository rateRepository;
    String date, value, lastElementName;

    public XMLCurrencyHandler(UsdRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        lastElementName = qName;
        if (qName.equals("Record")) {
            date = attributes.getValue("Date");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String information = new String(ch, start, length);

        if (!information.isEmpty()) {
            if (lastElementName.equals("Value"))
                value = information;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("Record")) {
            LocalDate ld = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            if (!rateRepository.existsByDate(ld)) {
                UsdRate rate = new UsdRate();
                rate.setDate(ld);
                rate.setRate(Double.parseDouble(value.replace(",", ".")));
                rateRepository.save(rate);
            }
        }
    }
}
