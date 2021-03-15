package com.shrb.versionowner.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.service.RuntimeCacheService;
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
    private Configuration configuration;

    @ApiRsp
    @RequestMapping(value = "/searchUserList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchUserList(HttpServletRequest request) {
        ApiExtendResponse apiResponse = new ApiExtendResponse();
        JSONObject dataObject = new JSONObject();
        dataObject.put("userList", runtimeCacheService.getUserList());
        apiResponse.setData(dataObject);
        apiResponse.setDataMaxCount(1);
        return apiResponse;
    }
}
