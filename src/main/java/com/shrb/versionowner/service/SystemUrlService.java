package com.shrb.versionowner.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.SystemUrl;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.lock.LockFactory;
import com.shrb.versionowner.utils.CollectionUtils;
import com.shrb.versionowner.utils.MyFileUtils;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SystemUrlService {
    private static final Logger log = LoggerFactory.getLogger(SystemUrlService.class);

    @Autowired
    private Configuration configuration;
    @Autowired
    private RuntimeCacheService runtimeCacheService;

    public void rewriteSystemUrlInfoToFile() throws Exception {
        String systemUrlInfoFilePath = configuration.getSystemUrlInfoFilePath();
        List<String> lines = new ArrayList<>();
        runtimeCacheService.getSystemUrlMap().forEach((key, value) -> {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getSystemUrlId()).append("|")
                    .append(value.getDevUrl()).append("|")
                    .append(value.getTestUrl()).append("|")
                    .append(value.getZscUrl()).append("|")
                    .append(value.getZbUrl()).append("|")
                    .append(value.getPrdUrl()).append("|")
                    .append(value.getSystemUrlInfo());
            lines.add(sb.toString());
        });
        MyFileUtils.writeLinesToFileFromHead(lines, systemUrlInfoFilePath, "utf-8");
    }

    public ConcurrentHashMap<String, SystemUrl> getSystemUrlMap() throws Exception {
        String systemUrlInfoFilePath = configuration.getSystemUrlInfoFilePath();
        List<String> systemUrlInfoStrList = MyFileUtils.readFileAllLines(systemUrlInfoFilePath, "utf-8");
        ConcurrentHashMap<String, SystemUrl> systemUrlMap = new ConcurrentHashMap<>();
        for (String systemUrlInfoStr : systemUrlInfoStrList) {
            String[] systemUrlInfoArray = systemUrlInfoStr.split("\\|");
            SystemUrl systemUrl = new SystemUrl();
            systemUrl.setSystemUrlId(systemUrlInfoArray[0]);
            systemUrl.setDevUrl(systemUrlInfoArray[1]);
            systemUrl.setTestUrl(systemUrlInfoArray[2]);
            systemUrl.setZscUrl(systemUrlInfoArray[3]);
            systemUrl.setZbUrl(systemUrlInfoArray[4]);
            systemUrl.setPrdUrl(systemUrlInfoArray[5]);
            systemUrl.setSystemUrlInfo(systemUrlInfoArray[6]);
            systemUrlMap.put(systemUrl.getSystemUrlId(), systemUrl);
        }
        return systemUrlMap;
    }

    public ApiExtendResponse searchSystemUrlList(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        Integer draw = Integer.parseInt(requestJson.getString("draw"));
        Integer from = Integer.parseInt(requestJson.getString("start"));
        Integer pageSize = Integer.parseInt(requestJson.getString("pageCount"));
        String systemUrlId = requestJson.getString("systemUrlId");
        Map<String, Object> map = new HashMap<>();
        map.put("systemUrlId", systemUrlId);
        map.put("pageSize", pageSize);
        map.put("from", from);
        List<SystemUrl> list = runtimeCacheService.getSystemUrlList();
        CollectionUtils<SystemUrl> collectionUtils = new CollectionUtils<SystemUrl>(list, map);
        CollectionUtils.ResultInfo<SystemUrl> resultInfo = collectionUtils.getListByFuzzyMatch();
        ApiExtendResponse apiExtendResponse = new ApiExtendResponse();
        apiExtendResponse.setData(resultInfo.getList());
        apiExtendResponse.setDraw(draw);
        apiExtendResponse.setPageCount(pageSize);
        apiExtendResponse.setDataMaxCount(resultInfo.getCount());
        apiExtendResponse.setDataMaxPage(resultInfo.getCount() % pageSize == 0 ? resultInfo.getCount() / pageSize : resultInfo.getCount() / pageSize + 1);
        return apiExtendResponse;
    }

    public ApiResponse addSystemUrl(String systemUrlId, String systemUrlInfo, String devUrl,
                                    String testUrl, String zscUrl, String zbUrl, String prdUrl) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        SystemUrl systemUrl = runtimeCacheService.getSystemUrl(systemUrlId);
        if(systemUrl != null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("环境清单已存在");
            return apiResponse;
        }
        systemUrl = new SystemUrl();
        systemUrl.setSystemUrlId(systemUrlId);
        systemUrl.setSystemUrlInfo(systemUrlInfo);
        systemUrl.setDevUrl(devUrl);
        systemUrl.setTestUrl(testUrl);
        systemUrl.setZscUrl(zscUrl);
        systemUrl.setZbUrl(zbUrl);
        systemUrl.setPrdUrl(prdUrl);
        synchronized (LockFactory.getLock("systemUrlInfo")) {
            if (runtimeCacheService.getSystemUrl(systemUrlId) != null) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("环境清单已存在");
                return apiResponse;
            }
            runtimeCacheService.getSystemUrlMap().put(systemUrlId, systemUrl);
            StringBuffer sb = new StringBuffer();
            sb.append(systemUrl.getSystemUrlId()).append("|")
                    .append(systemUrl.getDevUrl()).append("|")
                    .append(systemUrl.getTestUrl()).append("|")
                    .append(systemUrl.getZscUrl()).append("|")
                    .append(systemUrl.getZbUrl()).append("|")
                    .append(systemUrl.getPrdUrl()).append("|")
                    .append(systemUrl.getSystemUrlInfo());
            List<String> lines = new ArrayList<>();
            lines.add(sb.toString());
            //文件尾部写入数据
            String path = configuration.getSystemUrlInfoFilePath();
            MyFileUtils.writeLineToFileFromTail(lines, path, "utf-8");
        }
        return apiResponse;
    }

    public ApiResponse deleteSystemUrl(String systemUrlId) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        SystemUrl systemUrl = runtimeCacheService.getSystemUrl(systemUrlId);
        if (systemUrl == null) {
            return apiResponse;
        }
        synchronized (LockFactory.getLock("systemUrlInfo")) {
            if (runtimeCacheService.getSystemUrl(systemUrlId) == null) {
                return apiResponse;
            }
            runtimeCacheService.getSystemUrlMap().remove(systemUrlId);
            rewriteSystemUrlInfoToFile();
        }
        return apiResponse;
    }

    public ApiResponse deleteSystemUrls(JSONArray array) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        synchronized (LockFactory.getLock("systemUrlInfo")) {
            for(int i=0; i<array.size(); i++) {
                String systemUrlId = array.getString(i);
                runtimeCacheService.getSystemUrlMap().remove(systemUrlId);
            }
            rewriteSystemUrlInfoToFile();
        }
        return apiResponse;
    }

    public ApiResponse updateSystemUrl(JSONObject jsonObject) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        String systemUrlId = jsonObject.getString("systemUrlId");
        if(StringUtils.isEmpty(systemUrlId)) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("环境清单名不能为空");
            return apiResponse;
        }
        SystemUrl systemUrl = runtimeCacheService.getSystemUrl(systemUrlId);
        if(systemUrl == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("环境清单不存在");
            return apiResponse;
        }
        String systemUrlInfo = jsonObject.getString("systemUrlInfo");
        String devUrl = jsonObject.getString("devUrl");
        String testUrl = jsonObject.getString("testUrl");
        String zscUrl = jsonObject.getString("zscUrl");
        String zbUrl = jsonObject.getString("zbUrl");
        String prdUrl = jsonObject.getString("prdUrl");
        if(!StringUtils.isEmpty(systemUrlInfo)) {
            systemUrl.setSystemUrlInfo(systemUrlInfo);
        }
        if(!StringUtils.isEmpty(devUrl)) {
            systemUrl.setDevUrl(devUrl);
        }
        if(!StringUtils.isEmpty(testUrl)) {
            systemUrl.setTestUrl(testUrl);
        }
        if(!StringUtils.isEmpty(zscUrl)) {
            systemUrl.setZscUrl(zscUrl);
        }
        if(!StringUtils.isEmpty(zbUrl)) {
            systemUrl.setZbUrl(zbUrl);
        }
        if(!StringUtils.isEmpty(prdUrl)) {
            systemUrl.setPrdUrl(prdUrl);
        }
        synchronized (LockFactory.getLock("systemUrlInfo")) {
            runtimeCacheService.getSystemUrlMap().put(systemUrlId, systemUrl);
            rewriteSystemUrlInfoToFile();
        }
        return apiResponse;
    }
}
