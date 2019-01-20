package com.data.service.impl;

import com.data.bean.DataLog;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.QueryId;
import com.data.service.IDataLogService;
import com.data.utils.CommonUtil;
import com.data.utils.ResultUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataLogServiceImpl extends CommonServiceImpl implements IDataLogService {

    @Override
    public ResultUtil queryDataLogByCondition(DataLog dataLog, String page, String limit) throws Exception {
        if(CommonUtil.isBlank(dataLog)) {
            return ResultUtil.error("查询参数错误");
        }
        Map<String, Object> params = new HashMap<>(6);
        params.put("sysId", dataLog.getSysId());
        params.put("sysName", dataLog.getSysName());
        params.put("logDate", dataLog.getLogDate());
        PageRecord<DataLog> pageRecord = queryPageByObject(QueryId.QUERY_DATA_LOG_BY_CONDITION, QueryId.QUERY_COUNT_DATA_LOG,
                params, page, limit);
        return ResultUtil.success(pageRecord);
    }
}
