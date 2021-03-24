package com.shrb.versionowner.controller.developer;

import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.User;
import com.shrb.versionowner.service.DeveloperVersionService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/developer")
public class DeveloperVersionController {

    @Autowired
    private DeveloperVersionService developerVersionService;

    @ApiRsp
    @RequestMapping(value = "/searchAdministratorVersionList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchAdministratorVersionList(HttpServletRequest request) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiExtendResponse apiResponse = developerVersionService.searchDeveloperVersionList(request, user.getUserName());
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/searchDeveloperVersionTaskList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchDeveloperVersionTaskList(HttpServletRequest request) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiExtendResponse apiResponse = developerVersionService.searchDeveloperVersionTaskList(request, user.getUserName());
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/addDeveloperVersionTask", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse addDeveloperVersionTask(String versionId, String taskInfo) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.addDeveloperVersionTask(user.getUserName(), versionId, taskInfo);
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/updateDeveloperVersionTaskState", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse updateDeveloperVersionTaskState(String versionId, String taskInfo, String state) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.updateDeveloperVersionTaskState(user.getUserName(), versionId, taskInfo, state);
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/updateDeveloperVersionPriority", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse updateDeveloperVersionPriority(String versionId, String priority) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.updateDeveloperVersionPriority(user.getUserName(), versionId, priority);
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/deleteDeveloperVersionTask", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteDeveloperVersionTask(String versionId, String taskInfo) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.deleteDeveloperVersionTask(user.getUserName(), versionId, taskInfo);
            return apiResponse;
        }
        throw new Exception("登录超时");
    }

    @ApiRsp
    @RequestMapping(value = "/becomeCommitter", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse becomeCommitter(String versionId) throws Exception {
        Object obj = SecurityUtils.getSubject().getPrincipal();
        if (obj != null) {
            User user = (User) obj;
            ApiResponse apiResponse = developerVersionService.becomeCommitter(versionId, user.getUserName());
            return apiResponse;
        }
        throw new Exception("登录超时");
    }
}
