package com.shrb.versionowner.utils;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * http交互工具类
 */
public class WebHttpUtils {
    public static JSONObject getHttpRequestJson(HttpServletRequest httpServletRequest) throws Exception {
        JSONObject params = new JSONObject();
        Set<String> names = httpServletRequest.getParameterMap().keySet();
        String[] array = names.toArray(new String[0]);
        for (String name : array) {
            String value = httpServletRequest.getParameter(name);
            params.put(name, value);
        }
        return params;
    }
}
