package com.shrb.versionowner.controller.publicPackage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.service.SqlTemplateGroupService;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/public")
public class SqlTemplateGroupController {
    @Autowired
    private SqlTemplateGroupService sqlTemplateGroupService;

    @ApiRsp
    @RequestMapping(value = "/searchSqlTemplateGroupList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchSqlTemplateGroupList(HttpServletRequest request) throws Exception {
        ApiExtendResponse apiResponse = sqlTemplateGroupService.searchSqlTemplateGroupList(request);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/addSqlTemplateGroup", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse addSqlTemplateGroup(String templateGroupId, String templateGroupInfo, String templateIds) throws Exception {
        ApiResponse apiResponse = sqlTemplateGroupService.addSqlTemplateGroup(templateGroupId, templateGroupInfo, templateIds);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteSqlTemplateGroup", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteSqlTemplateGroup(String templateGroupId) throws Exception {
        ApiResponse apiResponse = sqlTemplateGroupService.deleteSqlTemplateGroup(templateGroupId);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteSqlTemplateGroups", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteSqlTemplateGroups(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        JSONArray jsonArray = requestJson.getJSONArray("sqlTemplateGroups");
        ApiResponse apiResponse = sqlTemplateGroupService.deleteSqlTemplateGroups(jsonArray);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/updateSqlTemplateGroup", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse updateSqlTemplateGroup(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        ApiResponse apiResponse = sqlTemplateGroupService.updateSqlTemplateGroup(requestJson);
        return apiResponse;
    }

}
