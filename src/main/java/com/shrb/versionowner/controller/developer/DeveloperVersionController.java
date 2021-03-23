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
