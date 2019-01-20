package com.data.service;

import com.data.bean.DataLog;
import com.data.utils.ResultUtil;

public interface IDataLogService {

    /**
     * 条件查询日志列表
     * @param dataLog
     * @return
     */
    ResultUtil queryDataLogByCondition(DataLog dataLog, String page, String limit) throws Exception;
}
