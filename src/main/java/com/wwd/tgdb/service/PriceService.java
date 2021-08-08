package com.wwd.tgdb.service;

import com.wwd.tgdb.dto.Response;

import java.io.File;
import java.time.LocalDate;

public interface PriceService {

    String uploadGPL(File file);

    Response getPriceWithDiscount(String partnumber, int discount, LocalDate dateOfRate);

    String getPrice(String partnumber);

//    String getPriceRub(String partnumber, String discount, String dateOfRate);
}
