package com.shrb.versionowner.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.User;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.lock.LockFactory;
import com.shrb.versionowner.utils.CollectionUtils;
import com.shrb.versionowner.utils.MyFileUtils;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private Configuration configuration;
    @Autowired
    private RuntimeCacheService runtimeCacheService;

    /**
     * 把缓存中的用户数据批量写入到文件中
     * @throws Exception
     */
    public void rewriteUserInfoToFile() throws Exception {
        String userInfoFilePath = configuration.getUserInfoFilePath();
        List<String> lines = new ArrayList<>();
        runtimeCacheService.getUserMap().forEach((key, value)->{
            String line = value.getUserName()+"|"+value.getPassword()+"|"+value.getRole();
            lines.add(line);
        });
        MyFileUtils.writeLinesToFileFromHead(lines, userInfoFilePath, "utf-8");
    }

    /**
     * 读文件获取userMap
     * @return
     * @throws Exception
     */
    public ConcurrentHashMap<String, User> getUserMap() throws Exception {
        String userInfoFilePath = configuration.getUserInfoFilePath();
        List<String> userInfoStrList = MyFileUtils.readFileAllLines(userInfoFilePath, "utf-8");
        ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();
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
        CollectionUtils.ResultInfo<Map<String, Object>> resultInfo = collectionUtils.getListByFuzzyMatch();
        ApiExtendResponse apiExtendResponse = new ApiExtendResponse();
        apiExtendResponse.setData(resultInfo.getList());
        apiExtendResponse.setDraw(draw);
        apiExtendResponse.setPageCount(pageSize);
        apiExtendResponse.setDataMaxCount(resultInfo.getCount());
        apiExtendResponse.setDataMaxPage(resultInfo.getCount() % pageSize == 0 ? resultInfo.getCount() / pageSize : resultInfo.getCount() / pageSize + 1);
        return apiExtendResponse;
    }

    /**
     * 校验并添加用户，写入缓存和文件中
     * @param userName
     * @param password
     * @param role
     * @return
     */
    public ApiResponse addUser(String userName, String password, String role) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        //校验缓存中是否存在用户，存在就拒绝，不存在就添加到缓存
        User user = runtimeCacheService.getUser(userName);
        if(user != null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("用户已存在");
            return apiResponse;
        }
        user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        user.setRole(Integer.parseInt(role));
        synchronized (LockFactory.getLock("userInfo")) {
            runtimeCacheService.getUserMap().put(userName, user);
            String line = userName + "|" + password + "|" + role;
            List<String> lines = new ArrayList<>();
            lines.add(line);
            //文件尾部写入数据
            String path = configuration.getUserInfoFilePath();
            MyFileUtils.writeLineToFileFromTail(lines, path, "utf-8");
        }
        return apiResponse;
    }

    /**
     * 从缓存中删除用户，并重新批量写入文件中
     * @param userName
     * @return
     * @throws Exception
     */
    public ApiResponse deleteUser(String userName) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        synchronized (LockFactory.getLock("userInfo")) {
            runtimeCacheService.getUserMap().remove(userName);
            rewriteUserInfoToFile();
        }
        return apiResponse;
    }

    public ApiResponse deleteUsers(JSONArray array) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        synchronized (LockFactory.getLock("userInfo")) {
            for(int i=0; i<array.size(); i++) {
                String userName = array.getString(i);
                runtimeCacheService.getUserMap().remove(userName);
            }
            rewriteUserInfoToFile();
        }
        return apiResponse;
    }

    public ApiResponse updateUser(Map<String, String> map) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        String userName = map.get("userName");
        if(StringUtils.isEmpty(userName)) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("用户名不能为空");
            return apiResponse;
        }
        User user = runtimeCacheService.getUser(userName);
        if(user == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("用户不存在");
            return apiResponse;
        }
        String password = map.get("password");
        String role = map.get("role");
        if(!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }
        if(!StringUtils.isEmpty(role)) {
            user.setRole(Integer.parseInt(role));
        }
        synchronized (LockFactory.getLock("userInfo")) {
            runtimeCacheService.getUserMap().put(userName, user);
            rewriteUserInfoToFile();
        }
        return apiResponse;
    }
}
