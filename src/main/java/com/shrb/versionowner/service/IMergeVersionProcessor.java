package com.shrb.versionowner.service;

import java.util.List;

/**
 * 版本合并处理器接口
 */
public interface IMergeVersionProcessor {
    void invoke(String versionId, List<String> committerNameList) throws Exception;
}
