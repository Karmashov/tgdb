package com.wwd.tgdb.service.impl;

import com.wwd.tgdb.model.Alias;
import com.wwd.tgdb.repository.AliasRepository;
import com.wwd.tgdb.service.AliasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AliasServiceImpl implements AliasService {

    private final AliasRepository aliasRepository;

    @Autowired
    public AliasServiceImpl(AliasRepository aliasRepository) {
        this.aliasRepository = aliasRepository;
    }

    @Override
    public String addAlias(String alias, String replacement) {
        Alias result = new Alias();
        result.setAlias(alias);
        result.setReplacement(replacement);
        return null;
    }
}
