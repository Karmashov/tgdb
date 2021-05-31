package com.wwd.tgdb.service;

import java.io.File;

public interface PriceService {

    String uploadGPL(File file);

    String getPriceWithDiscount(String partnumber, String discount);

    String getPrice(String partnumber);
}
