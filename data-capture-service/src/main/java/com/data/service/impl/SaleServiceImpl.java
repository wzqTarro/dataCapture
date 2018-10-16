package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.Sale;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.TemplateSupply;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.CodeEnum;
import com.data.constant.enums.SaleEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.service.ICodeDictService;
import com.data.service.IRedisService;
import com.data.service.ISaleService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.DateUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.ExportUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.JsonUtil;
import com.data.utils.ResultUtil;
import com.data.utils.TemplateDataUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;

@Service
public class SaleServiceImpl extends CommonServiceImpl implements ISaleService {

	private static final Logger logger = LoggerFactory.getLogger(SaleServiceImpl.class);
	
	private static final int PAGE_NUM = 1;
	
	private static final int PAGE_SIZE = 50000;
	
	@Autowired
	private DataCaptureUtil dataCaptureUtil;
	
	@Autowired
	private TemplateDataUtil templateDataUtil;
	
	@Autowired
	private IRedisService redisService;
	
	@Autowired
	private ICodeDictService codeDictService;
	
	@Autowired
	private ExportUtil exportUtil;

	@Override
	public ResultUtil getSaleByWeb(String queryDate, String sysId, Integer limit) throws Exception{
		PageRecord<Sale> pageRecord = null;
		logger.info("------>>>>>>开始抓取销售数据<<<<<<---------");
		logger.info("------>>>>>>系统编号sysId:{},查询时间queryDate:{}<<<<<<<-------", sysId, queryDate);
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.DATE_IS_NULL.getValue());
		}
		if (CommonUtil.isBlank(sysId)) {
			throw new DataException("503");
		}
		// 同步
		synchronized (sysId) {
			logger.info("------->>>>>>>进入抓取销售同步代码块<<<<<<<-------");
			Map<String, Object> queryParam = new HashMap<>(2);
			queryParam.put("queryDate", queryDate);
			queryParam.put("sysId", sysId);
			int count = queryCountByObject(QueryId.QUERY_COUNT_SALE_BY_PARAM, queryParam);
			
			logger.info("------>>>>>>原数据库中销售数据数量count:{}<<<<<<-------", count);
			List<Sale> saleList = null;
			if (count == 0) {
				
				TemplateSupply supply = (TemplateSupply)queryObjectByParameter(QueryId.QUERY_SUPPLY_BY_CONDITION, queryParam);
				
				boolean flag = true;
				
				while (flag) {
					try {
						// 抓取数据
						saleList = dataCaptureUtil.getDataByWeb(queryDate, supply, WebConstant.SALE, Sale.class);
						if (saleList != null) {
							flag = false;
							logger.info("----->>>>抓取销售数据结束<<<<------");
						}
					} catch (Exception e) {
						flag = true;
					}
				}
				
				if (saleList.size() == 0) {
					pageRecord = dataCaptureUtil.setPageRecord(saleList, limit);
					return ResultUtil.success(pageRecord);
				}
				
				List<TemplateStore> storeList = redisService.queryTemplateStoreList();
				List<TemplateProduct> productList = redisService.queryTemplateProductList();
				for (int i = 0, size = saleList.size(); i < size; i++) {
					Sale sale = saleList.get(i);
					sale.setCreateTime(DateUtil.stringToDate(queryDate));
					sale.setSysId(sysId);
					
					// 单品编码
					String simpleCode = sale.getSimpleCode();
					
					// 门店编码
					String storeCode = sale.getStoreCode();
					
					// 系统
					String sysName = supply.getSysName();			
					
					// 单品条码
					String simpleBarCode = sale.getSimpleBarCode();
					simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
					if (CommonUtil.isBlank(simpleBarCode)) {
						sale.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						continue;
					}
	
					sale.setSysName(supply.getRegion() + sysName);
					
					sale.setSimpleBarCode(simpleBarCode);
					
					// 单品模板信息
					TemplateProduct product = null;
					String tempSysId = null;
					String tempSimpleBarCode = null;
					for (int j = 0, len = productList.size(); j < len; j++) {
						product = productList.get(j);
						tempSysId = product.getSysId();
						tempSimpleBarCode = product.getSimpleBarCode();
						if (sysId.equals(tempSysId) && simpleBarCode.equals(tempSimpleBarCode)) {
							break;
						}
						product = null;
					}
					
					// 单品门店信息
					TemplateStore store = null;
					String tempStoreCode = null;
					for (int j = 0, len = storeList.size(); j < len; j++) {
						store = storeList.get(j);
						tempSysId = store.getSysId();
						tempStoreCode = store.getStoreCode();
						if (sysId.equals(tempSysId) && storeCode.equals(tempStoreCode)) {
							break;
						}
						store = null;
					}
					
					// 门店信息为空
					if (CommonUtil.isBlank(store)) {
						sale.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
						continue;
					} 
					// 大区
					sale.setRegion(store.getRegion());
						
					// 省区
					sale.setProvinceArea(store.getProvinceArea());
						
					// 门店名称
					sale.setStoreName(store.getStandardStoreName());
						
					// 归属
					sale.setAscription(store.getAscription());
						
					// 业绩归属
					sale.setAscriptionSole(store.getAscriptionSole());
						
					// 门店负责人
					sale.setStoreManager(store.getStoreManager());
					
					// 单品信息为空
					if (CommonUtil.isBlank(product)) {
						sale.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
						continue;
					} 
					// 单品名称
					sale.setSimpleName(product.getStandardName());
					
					// 品牌
					sale.setBrand(product.getBrand());
						
					// 销售价格
					// sale.setSellPrice(product.getSellPrice());
						
					// 系列
					sale.setSeries(product.getSeries());
						
					// 材质
					sale.setMaterial(product.getMaterial());
						
					// 片数
					sale.setPiecesNum(product.getPiecesNum());
						
					// 日夜
					sale.setDayNight(product.getFunc());
						
					// 货号
					sale.setStockNo(product.getStockNo());
						
					// 箱规
					sale.setBoxStandard(product.getBoxStandard());
						
					// 库存编号
					sale.setStockCode(product.getStockCode());
				}			
				// 数据插入数据库
				logger.info("------>>>>>开始插入销售数据<<<<<-------");
				dataCaptureUtil.insertData(saleList, InsertId.INSERT_BATCH_SALE);
			} else {
				saleList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, queryParam);
			}
					
			pageRecord = dataCaptureUtil.setPageRecord(saleList, limit);	
		}
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil getSaleByParam(CommonDTO common, Sale sale, Integer page, Integer limit) throws Exception {
		if (null == common) {
			common = new CommonDTO();
		}
		Map<String, Object> map = Maps.newHashMap();
		logger.info("--------->>>>>>>>common:" + FastJsonUtil.objectToString(common) + "<<<<<<<-----------");		
		if (StringUtils.isNoneBlank(common.getStartDate()) && StringUtils.isNoneBlank(common.getEndDate())) {
			map.put("startDate", common.getStartDate());
			map.put("endDate", common.getEndDate());
		} else {
			throw new DataException("534");
		}
		logger.info("--------->>>>>>map:{}<<<<<---------", FastJsonUtil.objectToString(map));
		logger.info("--------->>>>>>>>sale:" + FastJsonUtil.objectToString(sale) + "<<<<<<<<----------");
		if (null != sale) {
			
			// 门店编号
			if (StringUtils.isNoneBlank(sale.getStoreCode())) {
				map.put("storeCode", sale.getStoreCode());
			}
			 
			// 区域
			if (StringUtils.isNoneBlank(sale.getRegion())) {
				map.put("region", sale.getRegion());
			}
			
			// 省区
			if (StringUtils.isNoneBlank(sale.getProvinceArea())) {
				map.put("provinceArea", sale.getProvinceArea());
			}
			
			// 品牌
			if (StringUtils.isNoneBlank(sale.getBrand())) {
				map.put("brand", sale.getBrand());
			}
			
			// 单品条码
			if (StringUtils.isNoneBlank(sale.getSimpleBarCode())) {
				map.put("simpleBarCode", sale.getSimpleBarCode());
			}
			
			// 系统ID
			if (StringUtils.isNoneBlank(sale.getSysId())) {
				map.put("sysId", sale.getSysId());
			}
			
		}
		PageRecord<Sale> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_SALE_BY_PARAM, 
				QueryId.QUERY_SALE_BY_PARAM, map, page, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil querySaleList(Sale sale) {
		//防止一次性查询数据过多 分批查询
		Map<String, Object> params = new HashMap<>();
		params.put("pageNum", PAGE_NUM);
		params.put("pageSize", PAGE_SIZE);
		//此处为测试数据样式
		params.put("createTime", sale.getCreateTime());
		
		int count = queryCountByObject(QueryId.QUERY_COUNT_SALE_LIST, params);
		if(count == 0) {
			logger.info("--->>>销售查询列表数量为0<<<---");
			return ResultUtil.success(Collections.EMPTY_LIST);
		}
		
		/**
		 * TODO
		 * sql语句还没写 此处只是为了写出模板
		 */
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, params);
		int totalCount = count;
		//此处也是为了具体行为检测 如果数据量太大不能将数据打印 否则日志过大 
		logger.info("--->>>第一页销售查询结合列表为: {}, 总数为: {}<<<---", FastJsonUtil.objectToString(saleList), totalCount);
		List<Sale> resultList = new ArrayList<>();
		resultList.addAll(saleList);
		if(totalCount > PAGE_SIZE) {
			int pages = totalCount % PAGE_SIZE == 0 ? totalCount / PAGE_SIZE : totalCount / PAGE_SIZE + 1;
			for(int i = 2; i <= pages; i++) {
				saleList.clear();
				params.put("pageNum", i);
				saleList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, params);
				resultList.addAll(saleList);
			}
		}
		return ResultUtil.success(resultList);
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ResultUtil storeDailyexcel(String system, String region, String province, String stored, HttpServletResponse response) throws Exception {
		Map<String, Object> params = new HashMap<>(8);
		params.put("system", system);
		params.put("region", region);
		params.put("province", province);
		params.put("store", stored);
		//前一天的数据
		params.put("saleDate", new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getCustomDate(-1)));
		//先判断数量 分批导出
		int count = queryCountByObject(QueryId.QUERY_COUNT_SALE_LIST_REPORT, params);
		if(count >= CommonValue.MAX_ROW_COUNT_2007) {
			return ResultUtil.error("下载超过excel2007最大行数");
		} else {
			//CountDownLatch latch = new CountDownLatch(3);
			String fileName = "销售日报表-" + DateUtil.getCurrentDateStr();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
			ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			try {
				OutputStream out = response.getOutputStream();
				/**
				 * TODO 当前不开线程
				 */
				// executorService.execute(new ExcelThread(params, latch, out));
				List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_LIST_REPORT, params);
				if (CommonUtil.isBlank(saleList)) {
					logger.error("--->>>查询该天数据为空<<<---");
				}
				try {
					buildDailyStoreSale(saleList);
				} catch (Exception e) {
					logger.error("--->>>组装日销售门店信息异常: {}<<<---", e.getMessage());
				}

				List<String> daysList = DateUtil.getMonthDays(DateUtil.getSystemDate());
				String dateStr = DateUtil.getCurrentDateStr();
				List<Map<String, Object>> saleInfoList = redisService.querySaleList();
				List<Map<String, Object>> saleDataList = new ArrayList<>(10);
				// 日门店销售额集合
				List<Map<String, Object>> storeDailyList = new ArrayList<>(10);
				// 取得系统当前时间在日期集合中的索引
				int index = daysList.indexOf(dateStr);
				for (int i = 0; i < index + 1; i++) {
					// 将之前天数的数据都进行组装
					// 到缓存查是否数据， 然后放置在saleDatePriceMap中
					storeDailyList = redisService.querySaleDateMessageByStore(daysList.get(i));
					// 如果日查詢為空 則每一個門店銷售額直接為空
					boolean flag = CommonUtil.isBlank(storeDailyList);
					Map<String, Object> map = new HashMap<>(10);
					for (int k = 0; k < storeDailyList.size(); k++) {
						map.put((String) storeDailyList.get(k).get("storeCode"), storeDailyList.get(k));
					}
					for (int j = 0; j < saleInfoList.size(); j++) {
						String storeCode = (String) saleInfoList.get(j).get("storeCode");
						Map<String, Object> storeDailyMap = (Map<String, Object>) map.get(storeCode);
						if (map.get(storeCode) != null) {
							Map<String, Object> sale = saleInfoList.get(j);
							if (flag) {
								sale.put(daysList.get(i), 0.0);
							} else {
								sale.put(daysList.get(i), storeDailyMap.get("salePrice"));
							}
							saleInfoList.set(j, sale);
						}
					}
				}
				Map<String, Object> resultMap = new HashMap<>(10);
				for(int o = 0; o < saleInfoList.size(); o++) {
					resultMap.put((String) saleInfoList.get(o).get("storeCode"), saleInfoList.get(o));
				}
				List<Map<String, Object>> resultList = new ArrayList<>(10);
				//去除重复数据
				saleList = removeDuplicate(saleList);
				for(int i = 0; i < saleList.size(); i++) {
					Sale saleModel = saleList.get(i);
					Map<String, Object> resultData = (Map<String, Object>) resultMap.get(saleModel.getStoreCode());
					if(CommonUtil.isNotBlank(resultData)) {
						resultList.add(resultData);
					}
				}
				saleDataList.addAll(resultList);
				try {
					logger.info("--->>>日报表:  {}<<<---" + JsonUtil.toJson(saleDataList));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				// 写入单元格
				List<String> codeList = codeDictService
						.queryCodeListByServiceCode(CodeEnum.CODE_DICT_DAILY_STORE_REPORT.getValue());
				String title = "销售日报表";
				ExcelUtil excelUtil = new ExcelUtil();
				try {
					excelUtil.excel2007(CommonValue.STORE_DAILY_REPORT, saleDataList, title, codeList, out);
				} catch (IOException e) {
					logger.error("--->>>门店销售日报表导出异常: {}<<<---", e.getMessage());
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							logger.error("--->>>导出门店日销售报表关闭输出流异常: {}<<<---", e.getMessage());
						}
					}
				}

			} catch (Exception e) {
				logger.error("--->>>销售日报表导出异常: {}<<<---", e.getMessage());
			} finally {
				if (executorService != null) {
					executorService.shutdown();
				}
			}
		}
		return ResultUtil.success();
	}
	
	/**
	 * 去除重复数据
	 * @param saleList
	 * @return
	 */
	private List<Sale> removeDuplicate(List<Sale> saleList) {
		for(int i = 0; i < saleList.size() - 1; i++) {
			for(int j = saleList.size() - 1; j > i; j--) {
				if(saleList.get(j).getStoreCode().equals(saleList.get(i).getStoreCode())) {
					saleList.remove(j);
				}
			}
		}
		return saleList;
 	}
	
	/***
	 * 门店一天的销售额
	 * 组装当天数据 然后放入缓存中
	 * @param saleList
	 * @return
	 * @throws Exception 
	 */
	private void buildDailyStoreSale(List<Sale> saleList) throws Exception {
		String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getCustomDate(-1));
		List<Map<String, Object>> dataList = queryListByObject(QueryId.QUERY_DAILY_STORE_SALE_LIST_BY_GROUP, dateStr);
		//将一天的门店信息存入缓存
		redisService.setSaleDailyMessageByStore(dateStr, dataList);
	}

	@Override
	public void exportSaleExcel(String stockNameStr, CommonDTO common, Sale sale, OutputStream output) throws Exception {
		logger.info("----->>>>自定义字段：{}<<<<------", stockNameStr);
		logger.info("----->>>>common：{}<<<<------", FastJsonUtil.objectToString(common));
		logger.info("----->>>>sale：{}<<<<------", FastJsonUtil.objectToString(sale));
		exportUtil.exportConditionJudge(common, sale, stockNameStr);
		String[] header = CommonUtil.parseIdsCollection(stockNameStr, ",");
		Map<String, Object> map = exportUtil.joinColumn(SaleEnum.class, header);
		
		// 调用方法名
		String[] methodNameArray = (String[]) map.get("methodNameArray");
		
		// 导出字段
		String column = (String) map.get("column");
		
		// 自选导出excel表查询字段
		Map<String, Object> param = exportUtil.joinParam(common.getStartDate(), common.getEndDate(), column,
				sale.getSysId());

		// 门店编号
		if (StringUtils.isNoneBlank(sale.getStoreCode())) {
			param.put("storeCode", sale.getStoreCode());
		}

		// 区域
		if (StringUtils.isNoneBlank(sale.getRegion())) {
			param.put("region", sale.getRegion());
		}

		// 省区
		if (StringUtils.isNoneBlank(sale.getProvinceArea())) {
			param.put("provinceArea", sale.getProvinceArea());
		}

		// 品牌
		if (StringUtils.isNoneBlank(sale.getBrand())) {
			param.put("brand", sale.getBrand());
		}

		// 单品条码
		if (StringUtils.isNoneBlank(sale.getSimpleBarCode())) {
			param.put("simpleBarCode", sale.getSimpleBarCode());
		}

		// 系统ID
		if (StringUtils.isNoneBlank(sale.getSysId())) {
			param.put("sysId", sale.getSysId());
		}
		logger.info("------->>>>>导出条件：{}<<<<<-------", FastJsonUtil.objectToString(param));
		List<Sale> dataList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		ExcelUtil<Sale> excelUtil = new ExcelUtil<>();
		excelUtil.exportCustom2007("销售信息", header, methodNameArray, dataList, output);
	}

	/**
	 * TODO 
	 * 需注意的问题  服务器一崩溃   缓存数据全没 需手动来做处理  此处想一下怎么来做
	 * 应该当前此种手动方式可以 
	 */
	@Override
	public ResultUtil calculateStoreDailySale() throws Exception {
		List<String> daysList = DateUtil.getMonthDays(DateUtil.getSystemDate());
		String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getCustomDate(-1));
		int index = daysList.indexOf(dateStr);
		for(int i = 0; i < index + 1; i++) {
			Map<String, Object> params = new HashMap<>(4);
			String saleDate = daysList.get(i);
			params.put("saleDate", saleDate);
			List<Map<String, Object>> dataList = queryListByObject(QueryId.QUERY_DAILY_STORE_SALE_LIST_BY_GROUP, saleDate);
			//将一天的门店信息存入缓存
			redisService.setSaleDailyMessageByStore(daysList.get(i), dataList);
		}
		return ResultUtil.success();
	}

	@Override
	public ResultUtil queryStoreDailySaleReport(String system, String region, String province, String store, Integer page, Integer limit) throws Exception {
		Map<String, Object> params = new HashMap<>(8);
		params.put("system", system);
		params.put("region", region);
		params.put("province", province);
		params.put("store", store);
		//前一天的数据
		params.put("saleDate", new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getCustomDate(-1)));
		PageRecord<Sale> salePageRecord = queryPageByObject(QueryId.QUERY_COUNT_SALE_LIST_REPORT,
				QueryId.QUERY_SALE_LIST_REPORT, params, page, limit);
		return ResultUtil.success(salePageRecord);
	}

	@Override
	public void exportSaleExcelByRegion(String region) {
		Map<String, Object> params = new HashMap<>(8);
		params.put("region", region);
		//前一天的数据
		params.put("saleDate", new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getCustomDate(-1)));
		int oldSaleNum = queryCountByObject(QueryId.QUERY_COUNT_SALE_LIST_REPORT, params);
		List<Sale> oldSaleList = queryListByObject(QueryId.QUERY_SALE_LIST_REPORT, params);
		
		//区域集合 包括区域名称 门店数量 昨日销售额
		List<Map<String, Object>> regionDataList = new ArrayList<>(10);
		regionDataList = queryListByObject(QueryId.QUERY_STORE_NUM_BY_REGION, null);
		//得到前一个月26号到昨天的日期字符串
		List<String> daysList = DateUtil.getMonthDays(DateUtil.getSystemDate());
		String dateStr = DateUtil.getCurrentDateStr();
		int index = daysList.indexOf(dateStr);
		List<String> newDaysList = new ArrayList<>(10);
		for(int i = 0; i < index; i++) {
			newDaysList.add(daysList.get(i));
		}
		//需要计算每个区域上个月26号到昨天的销售额
		Map<String, Object> dateParams = new HashMap<>(4);
		dateParams.put("startDate", daysList.get(0));
		dateParams.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getCustomDate(-1)));
		List<Map<String, Object>> regionMonthSaleList = new ArrayList<>(10);
		regionMonthSaleList = queryListByObject(QueryId.QUERY_REGION_SALE_BY_DATE, dateParams);
		// TODO 将本月销售添加到regionDataList里面
//		for(Map<String, Object> regionMonthSaleMap : regionMonthSaleList) {
//			for(Map.Entry<String, Object> entry : regionMonthSaleMap.entrySet()) {
//				if(entry.getKey().equals()) {
//					
//				}
//			}
//		}
		
		
		
		
	}
	
}
