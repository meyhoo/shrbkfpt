package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MergeVersionService {
    private static final Logger log = LoggerFactory.getLogger(MergeVersionService.class);

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Autowired
    private Configuration configuration;

    public void mergeVersion(String versionId) throws Exception {
        //所有版本提交者id
        List<String> committerNames = runtimeCacheService.getVersionCommitters(versionId);

    }
}
