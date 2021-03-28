package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class MergeVersionService implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(MergeVersionService.class);

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Autowired
    private Configuration configuration;

    private ApplicationContext applicationContext;

    public void mergeVersion(String versionId) throws Exception {

        //清空已合并的版本目录
        String administratorVersionBasePath = configuration.getAdministratorVersionBasePath();
        String versionContentDirPath = administratorVersionBasePath + versionId + "/versionContent/" + versionId + "/";
        File dir = new File(versionContentDirPath);
        if (dir.exists()) {
            MyFileUtils.deleteDirOrFile(dir);
        }

        //版本提交者id
        List<String> committerNames = runtimeCacheService.getVersionCommitters(versionId);

        Map<String, IMergeVersionProcessor> map = applicationContext.getBeansOfType(IMergeVersionProcessor.class);
        map.forEach((k, v) -> {
            try {
                v.invoke(versionId, committerNames);
            } catch (Exception e) {
                log.error("{}.invoke()方法执行异常", k, e);
            }
        });

    }

    private Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
