package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.*;
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
    private SystemUrlService systemUrlService;

    @Autowired
    private SqlTemplateService sqlTemplateService;

    @Autowired
    private SqlTemplateGroupService sqlTemplateGroupService;

    @Autowired
    private AdministratorVersionService administratorVersionService;

    @Autowired
    private BeforeRunAction beforeRunAction;

    private volatile ConcurrentHashMap<String, User> userMap;
    private volatile ConcurrentHashMap<String, SqlTemplate> sqlTemplateMap;
    private volatile ConcurrentHashMap<String, SqlTemplateGroup> sqlTemplateGroupMap;
    private volatile ConcurrentHashMap<String, SystemUrl> systemUrlMap;
    private volatile ConcurrentHashMap<String, AdministratorVersion> administratorVersionMap;
    private volatile ConcurrentHashMap<String, ArrayList<String>> versionCommitterMap;
    private volatile ConcurrentHashMap<String, HashMap<String, DeveloperVersion>> developerVersionMap;

    @PostConstruct
    private void init() throws Exception {
        beforeRunAction.prepareUserInfoFile();
        this.userMap = initUserMap();
        beforeRunAction.prepareSqlTemplateDir();
        this.sqlTemplateMap = initSqlTemplateMap();
        beforeRunAction.prepareSqlTemplateGroupDir();
        this.sqlTemplateGroupMap = initSqlTemplateGroupMap();
        beforeRunAction.prepareSystemUrlInfoFile();
        this.systemUrlMap = initSystemUrlMap();
        beforeRunAction.prepareAdministratorVersionDir();
        this.administratorVersionMap = initAdministratorVersionMap();
        this.versionCommitterMap = initVersionCommitterMap();

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

    private ConcurrentHashMap<String, SystemUrl> initSystemUrlMap() {
        try{
            ConcurrentHashMap<String, SystemUrl> map = systemUrlService.getSystemUrlMap();
            return map;
        } catch (Exception e) {
            log.error("initSystemUrlMap failed. ", e);
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

    private ConcurrentHashMap<String, AdministratorVersion> initAdministratorVersionMap() {
        try {
            ConcurrentHashMap<String, AdministratorVersion> map = administratorVersionService.getAdministratorVersionMap();
            return map;
        } catch (Exception e) {
            log.error("initAdministratorVersionMap failed. ", e);
            return null;
        }
    }

    private ConcurrentHashMap<String, ArrayList<String>> initVersionCommitterMap() {
        try {
            ConcurrentHashMap<String, ArrayList<String>> map = administratorVersionService.getVersionCommitterMap();
            return map;
        } catch (Exception e) {
            log.error("initVersionCommitterMap failed. ", e);
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

    public SystemUrl getSystemUrl(String systemUrlId) {
        return this.systemUrlMap.get(systemUrlId);
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

    public List<SystemUrl> getSystemUrlList() {
        List<SystemUrl> systemUrlList = new ArrayList<>();
        systemUrlMap.forEach((key, value) -> {
            systemUrlList.add(value);
        });
        Collections.sort(systemUrlList, new Comparator<SystemUrl>() {
            @Override
            public int compare(SystemUrl o1, SystemUrl o2) {
                int i = o1.getSystemUrlId().compareTo(o2.getSystemUrlId());
                return i>0?1:-1;
            }
        });
        return systemUrlList;
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

    public ConcurrentHashMap<String, SystemUrl> getSystemUrlMap() {
        return systemUrlMap;
    }

    public void setSystemUrlMap(ConcurrentHashMap<String, SystemUrl> systemUrlMap) {
        this.systemUrlMap = systemUrlMap;
    }
}
