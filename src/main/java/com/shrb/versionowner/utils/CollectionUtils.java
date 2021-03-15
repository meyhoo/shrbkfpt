package com.shrb.versionowner.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

    public List<T> getListByFuzzyMatch() {
        AtomicReference<List<T>> resultList = new AtomicReference<>(srcList);
        condition.extendConditionMap.forEach((key, value)->{
            resultList.set(getListByFuzzyMatch(resultList.get(), key, (String) value));
        });
        return resultList.get();
    }

    private List<T> getListByPageInfo(List<T> currentSrcList) {
        Integer pageSize = condition.pageInfo.pageSize;
        Integer from = condition.pageInfo.from;
        //TODO xxx
        return null;
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

    public static void main(String[] args) {
        User user1 = new User();
        user1.setUserName("admin");
        user1.setRole(1);
        User user2 = new User();
        user2.setUserName("zhongzijie");
        user2.setRole(2);
        User user3 = new User();
        user3.setUserName("xubingnan");
        user3.setRole(2);
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        Map<String, Object> map = new HashMap<>();
        map.put("userName", "");
        map.put("role", "");
        map.put("pageSize", 10);
        map.put("from", 0);
        CollectionUtils<User> collectionUtils = new CollectionUtils<User>(userList, map);
        List<User> resultList = collectionUtils.getListByFuzzyMatch();
        for(User currentUser : resultList) {
            System.out.println(currentUser.getUserName());
        }

    }
}