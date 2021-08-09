package com.wwd.tgdb.service;

import com.wwd.tgdb.dto.Response;

import java.io.File;
import java.time.LocalDate;

public interface PriceService {

    Response uploadGPL(File file);

    Response getPrice(String partnumber, int discount, LocalDate dateOfRate);

}
