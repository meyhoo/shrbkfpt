package com.shrb.versionowner.controller.administrator;

import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.service.AdministratorVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/administrator")
public class AdministratorVersionController {

    @Autowired
    private AdministratorVersionService administratorVersionService;

    @ApiRsp
    @RequestMapping(value = "/searchAdministratorVersionList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchAdministratorVersionList(HttpServletRequest request) throws Exception {
        ApiExtendResponse apiResponse = administratorVersionService.searchAdministratorVersionList(request);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/searchCommitterTaskList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchCommitterTaskList(HttpServletRequest request) throws Exception {
        ApiExtendResponse apiResponse = administratorVersionService.searchCommitterTaskList(request);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/addAdministratorVersion", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse addAdministratorVersion(String versionId, String versionInfo) throws Exception {
        ApiResponse apiResponse = administratorVersionService.addAdministratorVersion(versionId, versionInfo);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteAdministratorVersion", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteAdministratorVersion(String versionId) throws Exception {
        ApiResponse apiResponse = administratorVersionService.deleteAdministratorVersion(versionId);
        return apiResponse;
    }
}
