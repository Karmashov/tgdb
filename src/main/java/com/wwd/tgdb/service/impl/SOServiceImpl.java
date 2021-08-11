package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.dto.Response;
import com.wwd.tgdb.exception.EntityNotFoundException;
import com.wwd.tgdb.model.GPL.RusSO;
import com.wwd.tgdb.repository.PriceRepository;
import com.wwd.tgdb.repository.RusSORepository;
import com.wwd.tgdb.service.SOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SOServiceImpl implements SOService {

    private final RusSORepository soRepository;
    private final PriceRepository priceRepository;

    @Autowired
    public SOServiceImpl(RusSORepository soRepository,
                         PriceRepository priceRepository) {
        this.soRepository = soRepository;
        this.priceRepository = priceRepository;
    }

    @Override
    public Response getSerials(String partnumber) {
        StringBuilder serials = new StringBuilder();
        List<RusSO> result = soRepository.findAllByPartnumber(priceRepository.findFirstByPartnumber(partnumber));
        if (!result.isEmpty()) {
            for (RusSO so : result) {
                serials.append(so.getSerial()).append("\n");
            }
            return new Response(serials.toString());
        }
        else {
            throw new EntityNotFoundException();
        }
    }
}
