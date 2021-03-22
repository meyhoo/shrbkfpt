package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeveloperVersionService {
    private static final Logger log = LoggerFactory.getLogger(DeveloperVersionService.class);

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Autowired
    private Configuration configuration;


}
