package com.shrb.versionowner.service;

import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.business.User;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.CollectionUtils;
import com.shrb.versionowner.utils.MyFileUtils;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private Configuration configuration;
    @Autowired
    private RuntimeCacheService runtimeCacheService;

    /**
     * 读文件获取userMap
     * @return
     * @throws Exception
     */
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

    /**
     * 从缓存中查询userList
     * @param request
     * @return
     * @throws Exception
     */
    public ApiExtendResponse searchUserList(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        Integer draw = Integer.parseInt(requestJson.getString("draw"));
        Integer from = Integer.parseInt(requestJson.getString("start"));
        Integer pageSize = Integer.parseInt(requestJson.getString("pageCount"));
        String userName = requestJson.getString("userName");
        String role = requestJson.getString("role");
        Map<String, Object> map = new HashMap<>();
        map.put("userName", userName);
        map.put("role", role);
        map.put("pageSize", pageSize);
        map.put("from", from);
        List<Map<String, Object>> list = runtimeCacheService.getUserList();
        CollectionUtils<Map<String, Object>> collectionUtils = new CollectionUtils<Map<String, Object>>(list, map);
        List<Map<String, Object>> resultList = collectionUtils.getListByFuzzyMatch();

        return null;
    }
}
