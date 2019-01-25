package com.data.controller;

import com.data.service.IDataLogService;
import com.data.utils.FastJsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多条件查询日志列表控制器
 * alex
 */
@RestController
@RequestMapping("/dataLog")
@Api(tags = {"抓取数据日志接口"})
@CrossOrigin(origins="*", maxAge=3600)
public class DataLogController {

    @Autowired
    private IDataLogService dataLogService;

    /**
     * 多条件查询日志列表
     * @param dataLog
     * @param page
     * @param limit
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryDataLogByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件查询日志列表", httpMethod = "GET")
    public String queryDataLogByCondition(String sysId, String logDate, String page, String limit) throws Exception {
        return FastJsonUtil.objectToString(dataLogService.queryDataLogByCondition(sysId, logDate, page, limit));
    }
}
