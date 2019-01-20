package com.data.controller;

import com.data.bean.DataLog;
import com.data.service.IDataLogService;
import com.data.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多条件查询日志列表控制器
 * alex
 */
@RestController
@RequestMapping("/dataLog")
public class DataLogController {

    @Autowired
    private IDataLogService dataLogService;

    /**
     * 多田间查询日志列表
     * @param dataLog
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryDataLogByCondition", method = RequestMethod.GET)
    public ResultUtil queryDataLogByCondition(DataLog dataLog, String page, String limit) throws Exception {
        return dataLogService.queryDataLogByCondition(dataLog, page, limit);
    }
}
