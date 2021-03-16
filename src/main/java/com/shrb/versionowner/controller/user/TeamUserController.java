package com.shrb.versionowner.controller.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.service.RuntimeCacheService;
import com.shrb.versionowner.service.UserService;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class TeamUserController {
    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Autowired
    private UserService userService;

    @Autowired
    private Configuration configuration;

    @ApiRsp
    @RequestMapping(value = "/searchUserList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchUserList(HttpServletRequest request) throws Exception {
        ApiExtendResponse apiResponse = userService.searchUserList(request);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse addUser(String userName, String pwd, String role) throws Exception {
        ApiResponse apiResponse = userService.addUser(userName, pwd, role);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteUser(String userName) throws Exception {
        ApiResponse apiResponse = userService.deleteUser(userName);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteUsers", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteUsers(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        JSONArray jsonArray = requestJson.getJSONArray("userNames");
        ApiResponse apiResponse = userService.deleteUsers(jsonArray);
        return apiResponse;
    }
}
