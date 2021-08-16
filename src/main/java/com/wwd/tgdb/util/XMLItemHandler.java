package com.wwd.tgdb.util;

import com.wwd.tgdb.model.GPL.Price;
import com.wwd.tgdb.repository.CategoryRepository;
import com.wwd.tgdb.repository.PriceRepository;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;

public class XMLItemHandler extends DefaultHandler {
    private PriceRepository priceRepository;
    private CategoryRepository categoryRepository;
    String information, name, vendor_pn, partnumber, vendor, section_code, price_usd, lastElementName;

    public XMLItemHandler(PriceRepository priceRepository, CategoryRepository categoryRepository) {
        this.priceRepository = priceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        lastElementName = qName;
        information = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        information += new String(ch, start, length);

        if (!information.isEmpty()) {
            if (lastElementName.equals("name"))
                name = information;
            if (lastElementName.equals("section_code"))
                section_code = information;
            if (lastElementName.equals("partnumber"))
                partnumber = information;
            if (lastElementName.equals("vendor"))
                vendor = information;
            if (lastElementName.equals("price_usd"))
                price_usd = information;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        try {
            if (lastElementName.equals("price_usd")) {
                if (vendor.equals("CISCO")) {

//                    System.out.println(name +
//                            " - " + partnumber +
//                            " - " + vendor +
//                            " - " + section_code +
//                            " - " + price_usd);
                    Price price = priceRepository.existsByPartnumber(partnumber) ?
                            priceRepository.findFirstByPartnumber(partnumber) :
                            new Price();

                    if (price.getPartnumber() == null) {
                        price.setName(name);
                        price.setPartnumber(partnumber);
                        price.setCategory(categoryRepository.findFirstBySectionId(section_code));
                        try {
                            price.setPriceUsd(Double.parseDouble(price_usd));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.out.println("Ошибка в " + name + ". Проблема с ценой " + price_usd);
                        }
                    } else if (price.getPriceUsd() != Double.parseDouble(price_usd)) {
                        price.setPriceUsd(Double.parseDouble(price_usd));
                        price.setChanged(LocalDate.now());
                    }
                    priceRepository.save(price);

                    name = null;
                    vendor_pn = null;
                    partnumber = null;
                    vendor = null;
                    price_usd = null;
                }
            }
//            if (qName.equals("catalog_item")) {
//                System.out.println("ITEMS - END");
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
