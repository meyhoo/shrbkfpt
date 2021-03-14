package com.shrb.versionowner.entity.api;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class ApiResponse extends AbstractResponse implements Serializable {

    private static final long serialVersionUID = 5750308905528388534L;

    private String errorCode;
    private String errorMsg;
    private Object data;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
