package com.shrb.versionowner.entity.api;

import java.io.Serializable;

public class ApiExtendResponse extends ApiResponse implements Serializable {

    private static final long serialVersionUID = 5750308905748388113L;

    //总页数
    private Integer dataMaxPage;
    //每页行数
    private Integer pageCount;
    //当页页码
    private Integer curPage;
    //数据总数
    private Integer dataMaxCount;
    private Integer draw;

    public Integer getDraw() {
        return draw;
    }
    public void setDraw(Integer draw) {
        this.draw = draw;
    }
    public Integer getDataMaxPage() {
        return dataMaxPage;
    }
    public void setDataMaxPage(Integer dataMaxPage) {
        this.dataMaxPage = dataMaxPage;
    }
    public Integer getPageCount() {
        return pageCount;
    }
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
    public Integer getCurPage() {
        return curPage;
    }
    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }
    public Integer getDataMaxCount() {
        return dataMaxCount;
    }
    public void setDataMaxCount(Integer dataMaxCount) {
        this.dataMaxCount = dataMaxCount;
    }
}
