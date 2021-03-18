package com.shrb.versionowner.controller.publicPackage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.service.SqlTemplateService;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/public")
public class SqlTemplateController {
    @Autowired
    private SqlTemplateService sqlTemplateService;

    @ApiRsp
    @RequestMapping(value = "/searchSqlTemplateList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchSqlTemplateList(HttpServletRequest request) throws Exception {
        ApiExtendResponse apiResponse = sqlTemplateService.searchSqlTemplateList(request);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/addSqlTemplate", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse addSqlTemplate(String templateId, String templateInfo, String runSqlContent, String rollbackSqlContent) throws Exception {
        ApiResponse apiResponse = sqlTemplateService.addSqlTemplate(templateId, templateInfo, runSqlContent, rollbackSqlContent);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteSqlTemplate", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteSqlTemplate(String templateId) throws Exception {
        ApiResponse apiResponse = sqlTemplateService.deleteSqlTemplate(templateId);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteSqlTemplates", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteSqlTemplates(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        JSONArray jsonArray = requestJson.getJSONArray("sqlTemplates");
        ApiResponse apiResponse = sqlTemplateService.deleteSqlTemplates(jsonArray);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/updateSqlTemplate", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse updateSqlTemplate(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        //TODO xxx
        return null;
    }
}
