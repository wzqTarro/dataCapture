package com.data.service;

import com.data.utils.ResultUtil;

public interface IDataLogService {

    /**
     * 条件查询日志列表
     * @param dataLog
     * @return
     */
    ResultUtil queryDataLogByCondition(String sysId, String logDate, String page, String limit) throws Exception;
}
