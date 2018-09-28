package com.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Order;
import com.data.bean.Reject;
import com.data.dto.CommonDTO;
import com.data.service.IOrderService;
import com.data.service.IRejectService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/reject")
@Api(tags = {"退单数据接口"})
//@CrossOrigin(origins="*", maxAge=3600)
public class RejectController {

	private static Logger logger = LoggerFactory.getLogger(RejectController.class);
	
	@Autowired
	private IRejectService rejectService;
	
	/**
	 * 抓取订单数据
	 * @param queryDate
	 * @param sysId
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/getRejectByWeb", method = RequestMethod.GET)
	@ApiOperation(value = "抓取退单数据", httpMethod = "GET")
	public String getRejectByWeb(String queryDate, String sysId, Integer page, Integer limit) {
		ResultUtil result = rejectService.getRejectByWeb(queryDate, sysId, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 分页多条件查询
	 * @param order
	 * @param common
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value = "/getRejectByCondition", method = RequestMethod.GET)
	@ApiOperation(value = "分页多条件查询退单", httpMethod = "GET")
	public String getRejectByCondition(Reject reject, CommonDTO common, Integer page, Integer limit) {
		ResultUtil result = ResultUtil.error();
		try {
			result = rejectService.getRejectByParam(common, reject, page, limit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
}
