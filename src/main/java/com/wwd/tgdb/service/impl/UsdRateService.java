package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.Bot;
import com.wwd.tgdb.model.UsdRate;
import com.wwd.tgdb.repository.UsdRateRepository;
import org.apache.poi.ss.formula.functions.Rate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cglib.core.Local;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UsdRateService {

    private Bot bot;
    private final UsdRateRepository rateRepository;

    public UsdRateService(Bot bot, UsdRateRepository rateRepository) {
        this.bot = bot;
        this.rateRepository = rateRepository;
    }

    public void getRates() {
        StringBuilder sbNow = new StringBuilder("http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=");
        LocalDate dateNow = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        sbNow.append(dateNow.format(formatter))
                .append("&date_req2=")
                .append(dateNow.plusDays(1).format(formatter))
                .append("&VAL_NM_RQ=R01235");

        try {
            Document doc = Jsoup.connect(sbNow.toString()).get();

            Elements src = doc.select("Record");

            for (Element el : src) {
                LocalDate ld = LocalDate.parse(el.attr("Date"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                if (rateRepository.existsByDate(ld)) {
                    continue;
                } else {
                    UsdRate rate = new UsdRate();
                    rate.setDate(ld);
                    rate.setRate(Double.parseDouble(el.select("Value").text().replace(",", ".")));
                    rateRepository.save(rate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
