package com.shrb.versionowner.enums;

public enum ErrorCodeEnums {
    success("000000", "请求成功"),
    failure("999999", "请求失败")
    ;
    private String code;
    private String msg;
    private ErrorCodeEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
