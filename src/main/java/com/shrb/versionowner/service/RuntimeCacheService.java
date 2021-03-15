package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class RuntimeCacheService {
    private static final Logger log = LoggerFactory.getLogger(RuntimeCacheService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BeforeRunAction beforeRunAction;

    private Map<String, User> userMap;

    @PostConstruct
    private void init() throws Exception {
        beforeRunAction.prepareUserInfoFile();
        this.userMap = initUserMap();
    }

    private Map<String, User> initUserMap() {
        try{
            Map<String, User> map = userService.getUserMap();
            return map;
        } catch (Exception e) {
            log.error("initUserMap failed. ", e);
            return null;
        }
    }

    public User getUser(String userName) {
        return this.userMap.get(userName);
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

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }
}
