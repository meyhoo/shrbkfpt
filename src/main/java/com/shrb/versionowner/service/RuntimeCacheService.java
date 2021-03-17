package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.SqlTemplate;
import com.shrb.versionowner.entity.business.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuntimeCacheService {
    private static final Logger log = LoggerFactory.getLogger(RuntimeCacheService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SqlTemplateService sqlTemplateService;

    @Autowired
    private BeforeRunAction beforeRunAction;

    private ConcurrentHashMap<String, User> userMap;
    private ConcurrentHashMap<String, SqlTemplate> sqlTemplateMap;

    @PostConstruct
    private void init() throws Exception {
        beforeRunAction.prepareUserInfoFile();
        this.userMap = initUserMap();
        beforeRunAction.prepareSqlTemplateDir();
        this.sqlTemplateMap = initSqlTemplateMap();
    }

    private ConcurrentHashMap<String, User> initUserMap() {
        try{
            ConcurrentHashMap<String, User> map = userService.getUserMap();
            return map;
        } catch (Exception e) {
            log.error("initUserMap failed. ", e);
            return null;
        }
    }

    private ConcurrentHashMap<String, SqlTemplate> initSqlTemplateMap() {
        try{
            ConcurrentHashMap<String, SqlTemplate> map = sqlTemplateService.getSqlTemplateMap();
            return map;
        } catch (Exception e) {
            log.error("initSqlTemplateMap failed. ", e);
            return null;
        }
    }

    public User getUser(String userName) {
        return this.userMap.get(userName);
    }

    public SqlTemplate getSqlTemplate(String templateId) {
        return this.sqlTemplateMap.get(templateId);
    }

    public List<Map<String, Object>> getUserList() {
        List<Map<String, Object>> userList = new ArrayList<>();
        userMap.forEach((key, value) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userName", value.getUserName());
            map.put("role", value.getRole());
            userList.add(map);
        });
        Collections.sort(userList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int i = o1.get("userName").toString().compareTo(o2.get("userName").toString());
                return i>0?1:-1;
            }
        });
        return userList;
    }

    public List<Map<String, Object>> getSqlTemplateList() {
        List<Map<String, Object>> sqlTemplateList = new ArrayList<>();
        sqlTemplateMap.forEach((key, value) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("templateId", value.getTemplateId());
            map.put("runSqlContent", value.getRunSqlContent());
            map.put("rollbackSqlContent", value.getRollbackSqlContent());
            sqlTemplateList.add(map);
        });
        Collections.sort(sqlTemplateList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int i = o1.get("templateId").toString().compareTo(o2.get("templateId").toString());
                return i>0?1:-1;
            }
        });
        return sqlTemplateList;
    }

    public ConcurrentHashMap<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(ConcurrentHashMap<String, User> userMap) {
        this.userMap = userMap;
    }

    public ConcurrentHashMap<String, SqlTemplate> getSqlTemplateMap() {
        return sqlTemplateMap;
    }

    public void setSqlTemplateMap(ConcurrentHashMap<String, SqlTemplate> sqlTemplateMap) {
        this.sqlTemplateMap = sqlTemplateMap;
    }
}
