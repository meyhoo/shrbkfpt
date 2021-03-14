package com.shrb.versionowner.entity.api;

public abstract class AbstractResponse {

    public abstract String getErrorCode();

    public abstract void setErrorCode(String errorCode);

    public abstract String getErrorMsg();

    public abstract void setErrorMsg(String errorMsg);

    public abstract Object getData();

    public abstract void setData(Object data);

    public abstract String toString();
}
