package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.User;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private Configuration configuration;

    public Map<String, User> getUserMap() throws Exception {
        String userInfoFilePath = configuration.getUserInfoFilePath();
        List<String> userInfoStrList = MyFileUtils.readFileAllLines(userInfoFilePath, "utf-8");
        Map<String, User> userMap = new HashMap<>();
        for (String userInfoStr : userInfoStrList) {
            String[] userInfoArray = userInfoStr.split("\\|");
            User user = new User();
            user.setUserName(userInfoArray[0]);
            user.setPassword(userInfoArray[1]);
            user.setRole(Integer.parseInt(userInfoArray[2]));
            userMap.put(user.getUserName(), user);
        }
        return userMap;
    }
}
