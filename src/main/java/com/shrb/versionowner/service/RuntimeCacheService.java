package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.SqlTemplate;
import com.shrb.versionowner.entity.business.SqlTemplateGroup;
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
    private SqlTemplateGroupService sqlTemplateGroupService;

    @Autowired
    private BeforeRunAction beforeRunAction;

    private volatile ConcurrentHashMap<String, User> userMap;
    private volatile ConcurrentHashMap<String, SqlTemplate> sqlTemplateMap;
    private volatile ConcurrentHashMap<String, SqlTemplateGroup> sqlTemplateGroupMap;

    @PostConstruct
    private void init() throws Exception {
        beforeRunAction.prepareUserInfoFile();
        this.userMap = initUserMap();
        beforeRunAction.prepareSqlTemplateDir();
        this.sqlTemplateMap = initSqlTemplateMap();
        beforeRunAction.prepareSqlTemplateGroupDir();
        this.sqlTemplateGroupMap = initSqlTemplateGroupMap();
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

    private ConcurrentHashMap<String, SqlTemplateGroup> initSqlTemplateGroupMap() {
        try {
            ConcurrentHashMap<String, SqlTemplateGroup> map = sqlTemplateGroupService.getSqlTemplateGroupMap();
            return map;
        } catch (Exception e) {
            log.error("initSqlTemplateGroupMap failed. ", e);
            return null;
        }
    }

    public User getUser(String userName) {
        return this.userMap.get(userName);
    }

    public SqlTemplate getSqlTemplate(String templateId) {
        return this.sqlTemplateMap.get(templateId);
    }

    public SqlTemplateGroup getSqlTemplateGroup(String templateGroupId) {
        return this.sqlTemplateGroupMap.get(templateGroupId);
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
            map.put("templateInfo", value.getTemplateInfo());
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

    public List<Map<String, Object>> getSqlTemplateGroupList() {
        List<Map<String, Object>> sqlTemplateGroupList = new ArrayList<>();
        sqlTemplateGroupMap.forEach((key, value) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("templateGroupId", value.getTemplateGroupId());
            map.put("templateGroupInfo", value.getTemplateGroupInfo());
            map.put("templateIds", value.getTemplateIdStr());
            sqlTemplateGroupList.add(map);
        });
        Collections.sort(sqlTemplateGroupList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int i = o1.get("templateGroupId").toString().compareTo(o2.get("templateGroupId").toString());
                return i>0?1:-1;
            }
        });
        return sqlTemplateGroupList;
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

    public ConcurrentHashMap<String, SqlTemplateGroup> getSqlTemplateGroupMap() {
        return sqlTemplateGroupMap;
    }

    public void setSqlTemplateGroupMap(ConcurrentHashMap<String, SqlTemplateGroup> sqlTemplateGroupMap) {
        this.sqlTemplateGroupMap = sqlTemplateGroupMap;
    }
}
