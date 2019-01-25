package com.data.service.impl;

import com.data.bean.DataLog;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.QueryId;
import com.data.service.IDataLogService;
import com.data.utils.ResultUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataLogServiceImpl extends CommonServiceImpl implements IDataLogService {

    @Override
    public ResultUtil queryDataLogByCondition(String sysId, String logDate, String page, String limit) throws Exception {
        Map<String, Object> params = new HashMap<>(2);
        params.put("sysId", sysId);
        params.put("logDate", logDate);
        PageRecord<DataLog> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_DATA_LOG, QueryId.QUERY_DATA_LOG_BY_CONDITION, 
                params, page, limit);
        return ResultUtil.success(pageRecord);
    }

}
