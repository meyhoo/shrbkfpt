package com.shrb.versionowner.controller.publicPackage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.service.SystemUrlService;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/public")
public class SystemUrlController {
    @Autowired
    private SystemUrlService systemUrlService;

    @ApiRsp
    @RequestMapping(value = "/searchSystemUrlList", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse searchSystemUrlList(HttpServletRequest request) throws Exception {
        ApiExtendResponse apiResponse = systemUrlService.searchSystemUrlList(request);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/addSystemUrl", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse addSystemUrl(String systemUrlId, String systemUrlInfo, String devUrl,
                                      String testUrl, String zscUrl, String zbUrl, String prdUrl) throws Exception {
        ApiResponse apiResponse = systemUrlService.addSystemUrl(systemUrlId, systemUrlInfo, devUrl, testUrl, zscUrl, zbUrl, prdUrl);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteSystemUrl", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteSystemUrl(String systemUrlId) throws Exception {
        ApiResponse apiResponse = systemUrlService.deleteSystemUrl(systemUrlId);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/deleteSystemUrls", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse deleteSystemUrls(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        JSONArray jsonArray = requestJson.getJSONArray("systemUrls");
        ApiResponse apiResponse = systemUrlService.deleteSystemUrls(jsonArray);
        return apiResponse;
    }

    @ApiRsp
    @RequestMapping(value = "/updateSystemUrl", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse updateSystemUrl(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        ApiResponse apiResponse = systemUrlService.updateSystemUrl(requestJson);
        return apiResponse;
    }
}
