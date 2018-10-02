package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.Reject;
import com.data.bean.Sale;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.CodeEnum;
import com.data.constant.enums.OrderEnum;
import com.data.constant.enums.SaleEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
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

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
		
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.DATE_IS_NULL.getValue());
		}
		
		Map<String, Object> queryParam = new HashMap<>(2);
		queryParam.put("queryDate", queryDate);
		queryParam.put("sysId", sysId);
		int count = queryCountByObject(QueryId.QUERY_COUNT_SALE_BY_PARAM, queryParam);
		
		logger.info("------>>>>>>count:{}<<<<<<-------", count);
		List<Sale> saleList = null;
		if (count == 0) {
			// 抓取数据
			saleList = dataCaptureUtil.getDataByWeb(queryDate, sysId, WebConstant.SALE, Sale.class);
			logger.info("------>>>>>>结束抓取销售数据<<<<<<---------");
			
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
				
				// 地区
				String localName = sale.getLocalName();
				
				// 系统
				String sysName = sale.getSysName();			
				
				// 单品条码
				String simpleBarCode = sale.getSimpleBarCode();
				simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
				if (CommonUtil.isBlank(simpleBarCode)) {
					sale.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
					continue;
				}
				
				sysName = CommonUtil.isBlank(localName) ? sysName : (localName + sysName);
				
				sale.setSysName(sysName);
				
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
			String now = DateUtil.format(new Date(), "yyyy-MM-dd");
			map.put("startDate", now);
			map.put("endDate", now);
		}
		logger.info("--------->>>>>>map:{}<<<<<---------", FastJsonUtil.objectToString(map));
		logger.info("--------->>>>>>>>sale:" + FastJsonUtil.objectToString(sale) + "<<<<<<<<----------");
		if (null != sale) {
			
			// 门店名称
			if (StringUtils.isNoneBlank(sale.getStoreName())) {
				map.put("storeName", sale.getStoreName());
			}
			 
			// 区域
			if (StringUtils.isNoneBlank(sale.getRegion())) {
				map.put("region", sale.getRegion());
			}
			
			// 系列
			if (StringUtils.isNoneBlank(sale.getSeries())) {
				map.put("series", sale.getSeries());
			}
			
			// 单品名称
			if (StringUtils.isNoneBlank(sale.getSimpleName())) {
				map.put("simpleName", sale.getSimpleName());
			}
			
			// 系统ID
			if (StringUtils.isNoneBlank(sale.getSysId())) {
				map.put("sysId", sale.getSysId());
			}
			
			// 系统名称
			if (StringUtils.isNoneBlank(sale.getSysName())) {
				map.put("sysName", sale.getSysName());
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
		System.err.println("--->>>params: " + FastJsonUtil.objectToString(params));
		//先判断数量 分批导出
		int count = queryCountByObject(QueryId.QUERY_COUNT_SALE_LIST_REPORT, params);
		System.err.println("report`s count: " + count);
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
					e.printStackTrace();
					logger.error("--->>>组装日销售门店信息异常<<<---");
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
				saleDataList.addAll(saleInfoList);
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
					logger.info("--->>>门店销售日报表导出异常<<<---");
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("--->>>销售日报表导出异常<<<---");
			} finally {
				if (executorService != null) {
					executorService.shutdown();
				}
			}
		}
		return ResultUtil.success();
	}
	
	private class ExcelThread implements Runnable {
		
		private Map<String, Object> params;
		
		private CountDownLatch latch;
		
		private OutputStream out;
		
		public ExcelThread(Map<String, Object> params, CountDownLatch latch, OutputStream out) {
			this.params = params;
			this.latch = latch;
			this.out = out;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void run() {
			//synchronized (latch) {
				
				List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_LIST_REPORT, params);
				if(CommonUtil.isBlank(saleList)) {
					logger.error("--->>>查询该天数据为空<<<---");
				}
				try {
					buildDailyStoreSale(saleList);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("--->>>组装日销售门店信息异常<<<---");
				}
				
				List<String> daysList = DateUtil.getMonthDays(DateUtil.getSystemDate());
				String dateStr = DateUtil.getCurrentDateStr();
				//String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getCustomDate(1));
				
				List<Map<String, Object>> saleDataList = new ArrayList<>(10);
				//日门店销售额集合
				List<Map<String, Object>> storeDailyList = new ArrayList<>(10);
				//取得系统当前时间在日期集合中的索引
				int index = daysList.indexOf(dateStr);
				for(int i = 0; i < index + 1; i++) {
					//将之前天数的数据都进行组装
					//到缓存查是否数据， 然后放置在saleDatePriceMap中
					storeDailyList = redisService.querySaleDateMessageByStore(daysList.get(i));
					//如果日查詢為空 則每一個門店銷售額直接為空
					if(CommonUtil.isBlank(storeDailyList)) {
						Map<String, Object> saleInfo = new HashMap<>(10);
						List<String> storeCodeList = queryListByObject(QueryId.QUERY_STORE_CODE_LIST, null);
						if(CommonUtil.isNotBlank(storeCodeList)) {
							for(String storeCode : storeCodeList) {
								try {
									saleInfo = redisService.queryTempSaleInfo(storeCode);
									saleInfo.put(daysList.get(i), 0.0);
									redisService.setTempSaleInfo(storeCode, saleInfo);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
					for(Map<String, Object> store : storeDailyList) {
						String storeCode = (String) store.get("storeCode");
						//根据storeCode从缓存里面取得门店信息
						Map<String, Object> saleInfo;
						try {
							//saleInfo = redisService.querySaleInfo(storeCode);
							saleInfo = redisService.queryTempSaleInfo(storeCode);
							//在门店信息里面添加日销售额
							saleInfo.put(daysList.get(i), store.get("salePrice"));
							//先暂存到缓存中
							redisService.setTempSaleInfo(storeCode, saleInfo);
							//将信息放入在saleDataList集合里面
							//saleDataList.add(saleInfo);
						} catch (Exception e) {
							logger.error("--->>>查询门店信息异常<<<---");
							e.printStackTrace();
						}
					}
				}
				List<Map<String, Object>> storeList = new ArrayList<>(10);
				try {
					storeList = redisService.querySaleList();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				for(int i = 0; i < storeList.size(); i++) {
					Map<String, Object> sale = storeList.get(i);
					String storeCode = (String) sale.get("storeCode");
					try {
						saleDataList.add(redisService.queryTempSaleInfo(storeCode));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					System.err.println("日报表： " + JsonUtil.toJson(saleDataList));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				//写入单元格
				List<String> codeList = codeDictService.queryCodeListByServiceCode(CodeEnum.CODE_DICT_DAILY_STORE_REPORT.getValue());
				String title = "销售日报表";
				ExcelUtil excelUtil = new ExcelUtil();
				try {
					excelUtil.excel2007(CommonValue.STORE_DAILY_REPORT, saleDataList, title, codeList, out);
				} catch (IOException e) {
					logger.info("--->>>门店销售日报表导出异常<<<---");
					e.printStackTrace();
				} finally {
					if(out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			//}
			
		}
		
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
		if (CommonUtil.isNotBlank(sale.getStoreCode())) {
			param.put("storeCode", sale.getStoreCode());
		}

		// 品牌
		if (CommonUtil.isNotBlank(sale.getBrand())) {
			param.put("brand", sale.getBrand());
		}
		
		// 单品条码
		if (CommonUtil.isNotBlank(sale.getSimpleBarCode())) {
			param.put("simpleBarCode", sale.getSimpleBarCode());
		}
		List<Sale> dataList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		ExcelUtil<Sale> excelUtil = new ExcelUtil<>();
		excelUtil.exportCustom2007("销售信息", header, methodNameArray, dataList, output);
	}

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
	
}
