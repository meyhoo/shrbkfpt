package com.shrb.versionowner.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.business.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionUtils<T> {
    private List<T> srcList;
    private Condition condition;

    public CollectionUtils(List<T> srcList, Map<String, Object> map) {
        this.srcList = srcList;
        this.condition = new Condition(map);
    }

    public ResultInfo<T> getListByFuzzyMatch() {
        AtomicReference<List<T>> resultList = new AtomicReference<>(srcList);
        condition.extendConditionMap.forEach((key, value)->{
            resultList.set(getListByFuzzyMatch(resultList.get(), key, (String) value));
        });
        Integer count = resultList.get().size();
        resultList.set(getListByPageInfo(resultList.get()));
        ResultInfo<T> resultInfo = new ResultInfo<T>(resultList.get(), count);
        return resultInfo;
    }

    private List<T> getListByPageInfo(List<T> currentSrcList) {
        Integer pageSize = condition.pageInfo.pageSize;
        Integer from = condition.pageInfo.from;
        Integer listSize = currentSrcList.size();
        List<T> resultList = new ArrayList<T>();
        if(from>=listSize) {
            return resultList;
        }
        for(int i = from; i < pageSize+from; i++) {
            resultList.add(currentSrcList.get(i));
            if(i == currentSrcList.size()-1) {
                break;
            }
        }
        return resultList;
    }

    private List<T> getListByFuzzyMatch(List<T> currentSrcList, String conditionalKey, String conditionalValue) {
        List<T> resultList = new ArrayList<>();
        Pattern pattern = Pattern.compile(conditionalValue);
        for(T result : currentSrcList) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(result);
            String value = jsonObject.get(conditionalKey).toString();
            Matcher matcher = pattern.matcher(value);
            if(matcher.find()) {
                resultList.add(result);
            }
        }
        return resultList;
    }

    public static class ResultInfo<T> {
        private List<T> list;
        private Integer count;

        public ResultInfo(List<T> list, Integer count) {
            this.list = list;
            this.count = count;
        }

        public List<T> getList() {
            return list;
        }

        public Integer getCount() {
            return count;
        }
    }

    private static class Condition {
        private PageInfo pageInfo;
        private Map<String, Object> extendConditionMap;
        public Condition(Map<String, Object> map) {
            pageInfo = new PageInfo();
            extendConditionMap = new HashMap<>();
            map.forEach((key, value)->{
                if("pageSize".equals(key)) {
                    pageInfo.setPageSize((Integer) value);
                } else if("from".equals(key)) {
                    pageInfo.setFrom((Integer) value);
                } else {
                    extendConditionMap.put(key, value);
                }
            });
        }
    }

    private static class PageInfo {
        private Integer pageSize;
        private Integer from;

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getFrom() {
            return from;
        }

        public void setFrom(Integer from) {
            this.from = from;
        }
    }
}
