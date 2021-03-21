package com.shrb.versionowner.controller.developer;

import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.annotataions.ApiRsp;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/developer")
public class SqlAutoCreateController {

    @ApiRsp
    @RequestMapping(value = "/createSql", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse createSql(HttpServletRequest request) throws Exception {
        JSONObject jsonObject = WebHttpUtils.getHttpRequestJson(request);
        System.out.println(jsonObject.toJSONString());
        //TODO xxx
        return new ApiResponse();
    }
}
