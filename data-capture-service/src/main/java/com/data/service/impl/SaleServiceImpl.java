package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.TemplateSupply;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.CodeEnum;
import com.data.constant.enums.ExcelEnum;
import com.data.constant.enums.SaleEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.model.RegionSaleModel;
import com.data.model.StoreSaleModel;
import com.data.model.SysSaleModel;
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
import com.data.utils.StockDataUtil;
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
	
	@Autowired
	private StockDataUtil stockDataUtil;

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
					} catch (DataException e) {
						return ResultUtil.error(e.getMessage());
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
						.queryCodeListByServiceCode(CodeEnum.CODE_DICT_DAILY_STORE_REPORT.value());
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
		LinkedList<Sale> saleLinkList = new LinkedList<>(saleList);
		for(int i = 0; i < saleLinkList.size() - 1; i++) {
			for(int j = saleLinkList.size() - 1; j > i; j--) {
				if(saleLinkList.get(j).getStoreCode().equals(saleLinkList.get(i).getStoreCode())) {
					saleLinkList.remove(j);
				}
			}
		}
		saleList.clear();
		saleList.addAll(saleLinkList);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void exportSaleExcelByRegion(String saleDate, HttpServletResponse response) throws Exception {
		if(CommonUtil.isBlank(saleDate)) {
			throw new DataException("534");
		}
		Map<String, Object> params = new HashMap<>(8);
		//区域集合 包括区域名称 门店数量 指定日期的销售额
		List<Map<String, Object>> regionDataList = new ArrayList<>(10);
		params.put("saleDate", saleDate);
		regionDataList = queryListByObject(QueryId.QUERY_STORE_NUM_BY_REGION, params);
		logger.info("--->>>regionDataList.first: {}<<<", JsonUtil.toJson(regionDataList));
		//得到前一个月26号到指定日期的日期字符串
		List<String> daysList = DateUtil.getMonthDays(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		String dateStr = DateUtil.getCurrentDateStr();
		int index = daysList.indexOf(dateStr);
		List<String> newDaysList = new ArrayList<>(10);
		for(int i = 0; i < index; i++) {
			newDaysList.add(daysList.get(i));
		}
		//需要计算每个区域上个月26号到指定日期的销售额
		Map<String, Object> dateParams = new HashMap<>(4);
		dateParams.put("startDate", daysList.get(0));
		dateParams.put("endDate", saleDate);
		List<Map<String, Object>> regionMonthSaleList = new ArrayList<>(10);
		regionMonthSaleList = queryListByObject(QueryId.QUERY_REGION_SALE_BY_DATE, dateParams);
		logger.info("-->>通过: {}<<--", JsonUtil.toJson(regionMonthSaleList));
		
		Map<String, Object> dataMap = new HashMap<>(10);
		for(Map<String, Object> map : regionMonthSaleList) {
			dataMap.put((String) map.get("region"), map);
		}
		//区域集合 包括区域名称 门店数量 本月销售目标 昨日销售额 月销售 昨日销售占比 本月达成率
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> monthSaleMap = (Map<String, Object>) dataMap.get(regionData.get("region"));
			if(CommonUtil.isNotBlank(monthSaleMap)) {
				double monthSale = CommonUtil.isBlank(monthSaleMap.get("monthSale")) ? 0.0 : (double) monthSaleMap.get("monthSale");
				double price = CommonUtil.isBlank(regionData.get("price")) ? 0.0 : (double) regionData.get("price");
				//月销售
				regionData.put("monthSale", monthSale);
				if(monthSale != 0) {
					//昨日销售占比
					regionData.put("yesterdayMonthSale", price / monthSale);
				} else {
					regionData.put("yesterdayMonthSale", 0);
				}
				//本月销售目标 暂无  百亚自行维护
				regionData.put("monthGoalTarget", 0);
				//本月达成率 (本月销售)/(本月销售目标)
				regionData.put("monthGoalSale", 0);
			}
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.MONTH, -1);
		String dateString = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD).format(calendar.getTime());
		logger.info("--->>>上月今日:{}<<<---", dateString);
		//上月今日销售额
		List<Map<String, Object>> lastMonthDateList = queryListByObject(QueryId.QUERY_REGION_SALE_BY_DATE_STR, dateString);
		params.clear();
		params.put("saleDate", dateString);
		//可比上月今日销售集合
		List<Sale> lastMonthDataDiffList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		dataMap.clear();
		for(int i = 0; i < lastMonthDateList.size(); i++) {
			dataMap.put((String) lastMonthDateList.get(i).get("region"), lastMonthDateList.get(i));
		}
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> lastMonthSaleMap = (Map<String, Object>) dataMap.get(regionData.get("region"));
			if(CommonUtil.isNotBlank(lastMonthSaleMap)) {
				//上月同期销售金额
				regionData.put("lastMonthDateSale", lastMonthSaleMap.get("dateSale"));
			}
		}
		logger.info("-->>通过上月同期: {}<<--", JsonUtil.toJson(lastMonthDateList));
		
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.YEAR, -1);
		dateString = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD).format(calendar.getTime());
		//去年今日销售额
		List<Map<String, Object>> lastYearDateList = queryListByObject(QueryId.QUERY_REGION_SALE_BY_DATE_STR, dateString);
		params.clear();
		params.put("saleDate", dateString);
		//可比去年今日销售集合
		List<Sale> lastYearDataDiffList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		dataMap.clear();
		for(int i = 0; i < lastYearDateList.size(); i++) {
			dataMap.put((String) lastYearDateList.get(i).get("region"), lastYearDateList.get(i));
		}
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> lastYearSaleMap = (Map<String, Object>) dataMap.get(regionData.get("region"));
			if(CommonUtil.isNotBlank(lastYearSaleMap)) {
				//去年同月同期销售金额
				regionData.put("lastYearDateSale", lastYearSaleMap.get("dateSale"));
				double lastMonthDateSale = CommonUtil.isBlank(regionData.get("lastMonthDateSale")) ? 0.0 : (double) regionData.get("lastMonthDateSale");
				double monthSale = CommonUtil.isBlank(regionData.get("monthSale")) ? 0.0 : (double) regionData.get("monthSale");
				double lastYearDateSale = CommonUtil.isBlank(regionData.get("lastYearDateSale")) ? 0.0 : (double) regionData.get("lastYearDateSale");
				if(monthSale != 0) {
					//环比
					regionData.put("chaimIndex", lastMonthDateSale / monthSale);
					//同比
					regionData.put("yearBasis", lastYearDateSale / monthSale);
				} else {
					regionData.put("chaimIndex", 0.0);
					regionData.put("yearBasis", 0.0);
				}
			}
		}
		logger.info("-->>通过去年同期<<--");
		params.clear();
		params.put("saleDate", saleDate);
		//指定日期销售门店
		List<Sale> saleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//去年门店
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.YEAR, -1);
		Date lastYearDate = calendar.getTime();
		dateStr = DateUtil.format(lastYearDate, DateUtil.DATE_PATTERN.YYYY_MM_DD);
		params.clear();
		params.put("saleDate", dateStr);
		List<Sale> lastYearSaleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//指定日期与上年同时有销售的销售集合
		Set<Sale> saleSet = new HashSet<>(10);
		for(int i = 0, size = saleList.size(); i < size; i++) {
			for(int j = 0, num = lastYearSaleList.size(); j < num; j++) {
				Sale newSale = saleList.get(i);
				Sale oldSale = lastYearSaleList.get(j);
				if((newSale.getRegion().equals(oldSale.getRegion())) && (newSale.getStoreCode().equals(oldSale.getStoreCode()))) {					
					saleSet.add(newSale);
				}
			}
		}
		//可比门店数量
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			int num = 0;
			for(Sale sale : saleSet) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("region")) && CommonUtil.isNotBlank(sale.getRegion())) {
					if(sale.getRegion().equals(regionDataMap.get("region"))) {
						regionDataMap.put("differNum", num++);
					}
				}
			}
		}
		logger.info("-->>通过可比门店<<--");
		Date yesterday = DateUtil.addDate(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD), Calendar.DATE, -1);
		//去年前一天销售
		Date lastYearYesterday = DateUtil.addDate(yesterday, Calendar.YEAR, -1);
		lastYearSaleList.clear();
		params.clear();
		params.put("saleDate", DateUtil.format(lastYearYesterday, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		lastYearSaleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//去除不同的门店销售
		for(Sale sale : saleSet) {
			for(int i = 0; i < lastYearSaleList.size(); i++) {
				if(!sale.getStoreCode().equals(lastYearSaleList.get(i).getStoreCode())) {
					lastYearSaleList.remove(i);
				}
			}
		}
		//可比门店昨日销售
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for(Sale sale : lastYearSaleList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("region")) && CommonUtil.isNotBlank(sale.getRegion())) {
					if(sale.getRegion().equals(regionDataMap.get("region"))) {
						regionDataMap.put("differYesterDaySale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比昨日销售<<--");
		List<Sale> monthDataList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, dateParams);
		//logger.info(">>>可比本月数据: {}<<<", JsonUtil.toJson(monthDataList));
		LinkedList<Sale> monthDataLinkList = new LinkedList<>();
		monthDataLinkList.addAll(monthDataList);
		//去除不同的门店销售
		for(Sale sale : saleSet) {
			for(int i = 0, size = monthDataLinkList.size(); i < size; i++) {
				if(!sale.getStoreCode().equals(monthDataLinkList.get(i).getStoreCode())) {
					monthDataLinkList.remove(i);
				}
			}
		}
		monthDataList.clear();
		monthDataList.addAll(monthDataLinkList);
		//可比本月销售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : monthDataList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("region")) && CommonUtil.isNotBlank(sale.getRegion())) {
					if (sale.getRegion().equals(regionDataMap.get("region"))) {
						regionDataMap.put("differMonthSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比本月销售<<--");
		//去除不同的门店销售
		LinkedList<Sale> lastMonthDataDiffLinkList = new LinkedList<>();
		lastMonthDataDiffLinkList.addAll(lastMonthDataDiffList);
		for(Sale sale : saleSet) {
			for(int i = 0, size = lastMonthDataDiffLinkList.size(); i < size; i++) {
				if(!sale.getStoreCode().equals(lastMonthDataDiffLinkList.get(i).getStoreCode())) {
					lastMonthDataDiffLinkList.remove(i);
				}
			}
		}
		lastMonthDataDiffList.clear();
		lastMonthDataDiffList.addAll(lastMonthDataDiffLinkList);
		//可比上月同期銷售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : lastMonthDataDiffList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("region")) && CommonUtil.isNotBlank(sale.getRegion())) {
					if (sale.getRegion().equals(regionDataMap.get("region"))) {
						regionDataMap.put("differLastMonthDateSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比上月同期銷售<<--");
		//去除不同的门店销售
		LinkedList<Sale> lastYearDataDiffLinkList = new LinkedList<>();
		lastYearDataDiffLinkList.addAll(lastYearDataDiffList);
		for (Sale sale : saleSet) {
			for (int i = 0, size = lastYearDataDiffLinkList.size(); i < size; i++) {
				if (!sale.getStoreCode().equals(lastYearDataDiffLinkList.get(i).getStoreCode())) {
					lastYearDataDiffLinkList.remove(i);
				}
			}
		}
		lastYearDataDiffList.clear();
		lastYearDataDiffList.addAll(lastYearDataDiffLinkList);
		//可比去年同期銷售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : lastYearDataDiffList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("region")) && CommonUtil.isNotBlank(sale.getRegion())) {
					if (sale.getRegion().equals(regionDataMap.get("region"))) {
						regionDataMap.put("differLastYearDateSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比去年同期銷售 <<--");
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			//可比月銷售
			double differMonthSale = CommonUtil.isBlank(regionDataMap.get("differMonthSale")) ? 0.0 : (double) regionDataMap.get("differMonthSale");
			//可比上月銷售
			double differLastMonthDateSale = CommonUtil.isBlank(regionDataMap.get("differLastMonthDateSale")) ? 0.0 : (double) regionDataMap.get("differLastMonthDateSale");
			//可比去年銷售
			double differLastYearDateSale = CommonUtil.isBlank(regionDataMap.get("differLastYearDateSale")) ? 0.0 : (double) regionDataMap.get("differLastYearDateSale");
			if(differMonthSale != 0) {
				//可比环比
				regionDataMap.put("differChaimIndex", differLastMonthDateSale / differMonthSale);
				//可比同比
				regionDataMap.put("differYearBasis", differLastYearDateSale / differMonthSale);
			} else {
				regionDataMap.put("differChaimIndex", 0.0);
				regionDataMap.put("differYearBasis", 0.0);
			}
			
		}
		logger.info("--->>>组装数据完成<<<---");
		String title = "全公司直营KA分大区日报表";
		String[] header = new String[] {"大区", "门店数量", "本月销售目标", "昨日销售", "本月销售", 
				"昨日销售占比(%)", "本月达成率", "上月同期销售金额", "去年同月同期销售金额", "环比(%)", "同比(%)", 
				"可比门店数量", "可比门店昨日销售", "可比门店本月", "可比门店上月", "可比门店去年", "可比门店环比(%)",
				"可比门店同比(%)"};
		List<String> codeList = codeDictService
				.queryCodeListByServiceCode(CodeEnum.CODE_DICT_REGION_COMPANY_REPORT.value());
		String fileName = "按区域一级报表-" + DateUtil.getCurrentDateStr();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
		ExcelUtil excelUtil = new ExcelUtil();
		excelUtil.exportTemplateByMap(header, regionDataList, title, codeList, response.getOutputStream());
	}
	

	@Override
	public void exportCompanyExcelBySys(String queryDate, OutputStream output) throws Exception {
		if (StringUtils.isBlank(queryDate)) {
			throw new DataException("534");
		}
		
		String title = "全公司直营KA分系统日报表";
		String[] headers = new String[] {"系统", "门店数量", "本月销售目标", "昨日销售", "本月销售", 
				"昨日销售占比", "本月达成率", "上月同期销售金额", "去年同月同期销售金额", "环比", "同比", 
				"可比门店数量", "可比门店昨日销售", "可比门店本月", "可比门店上月", "可比门店去年", "可比门店环比",
				"可比门店同比"};
		
		// 获取查询时间当天销售数据
		Map<String, Object> param = new HashMap<>();
		param.put("queryDate", queryDate);
		List<SysSaleModel> sysSaleList = queryListByObject(QueryId.QUERY_SYS_SALE_BY_CONDTION, param);
		
		// 查询时间的前一天
		String lastDay = LocalDate.parse(queryDate).minusDays(1L).toString();
		param.clear();
		param.put("queryDate", lastDay);
		param.put("column", " sys_id, sell_price ");
		List<Sale> lastDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 上个月同期
		String lastMonth = LocalDate.parse(queryDate).minusMonths(1L).toString();
		param.clear();
		param.put("queryDate", lastMonth);
		param.put("column", " sys_id, sell_price ");
		List<Sale> lastMonthDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 本月销售数据
		String startDate = LocalDate.parse(queryDate).minusMonths(1L).withDayOfMonth(26).toString();
		param.clear();
		param.put("startDate", startDate);
		param.put("endDate", lastDay);
		param.put("column", " sys_id, sell_price ");
		List<Sale> lastMonthSaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期
		String lastYearDay = LocalDate.parse(queryDate).minusYears(1L).toString();
		param.clear();
		param.put("queryDate", lastYearDay);
		param.put("column", " sys_id, sell_price, store_code ");
		List<Sale> lastYearDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期前一天销售数据
		String lastYearLastDay = LocalDate.parse(queryDate).minusYears(1L).minusDays(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastDay);
		param.put("column", " sys_id, sell_price, store_code ");
		List<Sale> lastYearLastDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的一个月销售数据
		String lastYear26Day = LocalDate.parse(queryDate).minusYears(1L).minusMonths(1L).withDayOfMonth(26).toString();	
		param.clear();
		param.put("startDate", lastYear26Day);
		param.put("endDate", lastYearLastDay);
		param.put("column", " sys_id, sell_price, store_code ");
		List<Sale> lastYearMonthSaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的上一个月同期销售数据
		String lastYearLastMonthDay = LocalDate.parse(queryDate).minusYears(1L).minusMonths(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastMonthDay);
		param.put("column", " sys_id, sell_price, store_code ");
		List<Sale> lastYearLastMonthDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的去年同期销售数据
		String lastYearLastYearDay = LocalDate.parse(queryDate).minusYears(1L).minusYears(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastYearDay);
		param.put("column", " sys_id, sell_price, store_code ");
		List<Sale> lastYearLastYearDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		SysSaleModel sysSale = null;
		String sysId = null;
		String sysName = null;
		List<Sale> saleList = null;
		Sale sale = null;
		
		// 品牌
		Set<String> brandSet = new HashSet<>();
		
		// 当前门店
		Set<String> storeSet = new HashSet<>();
		
		// 可比门店
		Set<String> lastStoreSet = new HashSet<>();
		
		
		String[][] rowValue = new String[sysSaleList.size()][headers.length];
		for (int i = 0, size = sysSaleList.size(); i<size; i++) {
			sysSale = sysSaleList.get(i);
			sysId = sysSale.getSysId();
			sysName = sysSale.getSysName();
			saleList = sysSale.getSaleList();
			
			storeSet.clear();
			
			// 当天销售
			for (int j = 0, sysSize = saleList.size(); j<sysSize; j++) {
				sale = saleList.get(j);
				if (StringUtils.isNoneBlank(sale.getBrand())) {
					brandSet.add(sale.getBrand());
				}
				storeSet.add(sale.getStoreCode());
			}

			// 昨日销售
			double nowSellPriceSum = 0;
			for (int j = 0, daySize = lastDaySaleList.size(); j < daySize; j++) {
				sale = lastDaySaleList.get(j);
				if (sale.getSysId().equals(sysId)) {
					nowSellPriceSum += (sale.getSellPrice() == null) ? 0 : sale.getSellPrice();
				}
			}
			
			// 本月销售
			double monthSellPriceSum = 0;
			for (int j = 0, monthSize = lastMonthSaleList.size(); j<monthSize; j++) {
				sale = lastMonthSaleList.get(j);
				if (sale.getSysId().equals(sysId)) {
					monthSellPriceSum += (sale.getSellPrice() == null) ? 0 : sale.getSellPrice();
				}
			}
			
			// 昨日销售占比
			double lastSellPercent = 0;
			if (monthSellPriceSum != 0) {
				lastSellPercent = nowSellPriceSum / monthSellPriceSum;
			}
		
			// 上月同期销售金额
			double lastMonthDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastMonthDaySaleList.size(); j < monthSize; j++) {
				sale = lastMonthDaySaleList.get(j);
				if (sale.getSysId().equals(sysId)) {
					lastMonthDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
				}
			}
			
			// 去年同期销售
			double lastYearDaySellPriceSum = 0;
			lastStoreSet.clear();
			for (int j = 0, monthSize = lastYearDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearDaySaleList.get(j);
				if (sale.getSysId().equals(sysId)) {
					lastYearDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					if (storeSet.contains(sale.getStoreCode())) {
						lastStoreSet.add(sale.getStoreCode());
					}
				}
			}
			
			// 环比
			double aroundCompare = 0;
			if (monthSellPriceSum != 0) {
				aroundCompare = lastMonthDaySellPriceSum / monthSellPriceSum;
			}
			
			// 同比
			double yearCompare = 0;
			if (monthSellPriceSum != 0) {
				yearCompare = lastYearDaySellPriceSum / monthSellPriceSum;
			}
			
			// 去年同期前一天销售
			double lastYearLastDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastDaySaleList.get(j);
				if (sale.getSysId().equals(sysId)) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期一个月销售
			double lastYearMonthSellPriceSum = 0;
			for (int j = 0, monthSize = lastYearMonthSaleList.size(); j < monthSize; j++) {
				sale = lastYearMonthSaleList.get(j);
				if (sale.getSysId().equals(sysId)) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearMonthSellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期的上个月同期销售
			double lastYearLastMonthDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastMonthDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastMonthDaySaleList.get(j);
				if (sale.getSysId().equals(sysId)) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastMonthDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期的去年同期销售
			double lastYearLastYearDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastYearDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastYearDaySaleList.get(j);
				if (sale.getSysId().equals(sysId)) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastYearDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 可比门店环比
			double storeAroundCompare = 0;
			if (lastYearMonthSellPriceSum != 0) {
				storeAroundCompare = lastYearLastMonthDaySellPriceSum/lastYearMonthSellPriceSum;
			}

			// 可比门店同比
			double storeYearCompare = 0;
			if (lastYearMonthSellPriceSum != 0) {
				storeYearCompare = lastYearLastYearDaySellPriceSum / lastYearMonthSellPriceSum;
			}
			rowValue[i] = new String[] {
				sysName, // 系统名称
				String.valueOf(storeSet.size()), // 门店数量
				"" , //本月目标
				String.valueOf(CommonUtil.setScale("0.00", nowSellPriceSum)), // 昨日销量
				String.valueOf(CommonUtil.setScale("0.00", monthSellPriceSum)), // 本月销售 
				String.valueOf(CommonUtil.setScale("0.00", lastSellPercent*100) + "%"), // 昨日销售占比
				String.valueOf(0), // 本月达成率
				String.valueOf(CommonUtil.setScale("0.00", lastMonthDaySellPriceSum)), // 上月同期销售金额
				String.valueOf(CommonUtil.setScale("0.00", lastYearDaySellPriceSum)), // 去年同月同期销售金额
				String.valueOf(CommonUtil.setScale("0.00", aroundCompare*100)), // 环比
				String.valueOf(CommonUtil.setScale("0.00", yearCompare*100)), // 同比 
				String.valueOf(lastStoreSet.size()), // 可比门店数量
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastDaySellPriceSum)), // 可比门店昨日销售
				String.valueOf(CommonUtil.setScale("0.00", lastYearMonthSellPriceSum)), // 可比门店本月
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastMonthDaySellPriceSum)), // 可比门店上月
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastYearDaySellPriceSum)), // 可比门店去年
				String.valueOf(CommonUtil.setScale("0.00", storeAroundCompare*100)), // 可比门店环比
				String.valueOf(CommonUtil.setScale("0.00", storeYearCompare*100))// 可比门店同比
			};
		}
		
		
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("公司一级表");
		
		// 报表时间
		Row dateRow = sheet.createRow(0);
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);
		
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());
		
		// 标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, headers.length);
		
		// 表头
		Row headerRow = sheet.createRow(3);
		excelUtil.createRow(headerRow, headers, true);
		
		// 红底样式
		CellStyle cellStyle = excelUtil.getCellStyle(wb, IndexedColors.RED.index);
		
		// 白字
		Font font = excelUtil.getColorFont(wb, IndexedColors.WHITE.index);
		cellStyle.setFont(font);
		
		Row row = null;
		for (int i = 0, size = rowValue.length; i<size; i++) {
			String[] value = rowValue[i];
			row = sheet.createRow(i+4);
			row.createCell(0).setCellValue(value[0]);
			row.createCell(1).setCellValue(value[1]);
			row.createCell(2).setCellValue(value[2]);
			row.createCell(3).setCellValue(value[3]);
			row.createCell(4).setCellValue(value[4]);
			row.createCell(5).setCellValue(value[5]);
			
			// 本月达成率
			Cell reachCell = row.createCell(6);
			reachCell.setCellValue(value[6]);
			
			row.createCell(7).setCellValue(value[7]);
			row.createCell(8).setCellValue(value[8]);
			
			// 环比
			double monthCompare = Double.valueOf(value[9]);
			Cell monthCompareCell = row.createCell(9);
			if (monthCompare < 100) {
				monthCompareCell.setCellStyle(cellStyle);
			}
			monthCompareCell.setCellValue(monthCompare + "%");

			// 同比
		    double yearCompare = Double.valueOf(value[10]);
			Cell yearCompareCell = row.createCell(10);
			if (yearCompare < 100) {
				yearCompareCell.setCellStyle(cellStyle);
			}
			yearCompareCell.setCellValue(yearCompare + "%");
			
			row.createCell(11).setCellValue(value[11]);
			row.createCell(12).setCellValue(value[12]);
			row.createCell(13).setCellValue(value[13]);
			row.createCell(14).setCellValue(value[14]);
			row.createCell(15).setCellValue(value[15]);
			
			// 可比门店环比
			double lastMonthCompare = Double.valueOf(value[16]);
			Cell lastMonthCompareCell = row.createCell(16);
			if (lastMonthCompare < 100) {
				lastMonthCompareCell.setCellStyle(cellStyle);
			}
			lastMonthCompareCell.setCellValue(lastMonthCompare + "%");

			// 同比
			double lastYearCompare = Double.valueOf(value[17]);
			Cell lastYearCompareCell = row.createCell(17);
			if (lastYearCompare < 100) {
				lastYearCompareCell.setCellStyle(cellStyle);
			}
			lastYearCompareCell.setCellValue(lastYearCompare + "%");
		}
		
		wb.write(output);
		output.flush();
		output.close();
	}

	@Override
	public void exportRegionFirstExcelBySys(String queryDate, String sysId, OutputStream output) throws Exception {
		if (StringUtils.isBlank(queryDate)) {
			throw new DataException("534");
		}
		if (StringUtils.isBlank(sysId)) {
			throw new DataException("538");
		}
		
		// 表头
		String[] headers = new String[] {"系统", "大区", "门店数量", "本月销售目标", "昨日销售", "本月销售", 
				"昨日销售占比", "本月达成率", "上月同期销售金额", "去年同月同期销售金额", "环比", "同比", 
				"可比门店数量", "可比门店昨日销售", "可比门店本月", "可比门店上月", "可比门店去年", "可比门店环比",
				"可比门店同比"};
		
		// 获取查询时间当天销售数据
		Map<String, Object> param = new HashMap<>();
		param.put("queryDate", queryDate);
		param.put("sysId", sysId);
		List<RegionSaleModel> regionSaleList = queryListByObject(QueryId.QUERY_REGION_SALE_BY_CONDITION, param);
		
		// 系统名称
		String sysName = regionSaleList.get(0).getSaleList().get(0).getSysName();
		
		// 标题
		String title = sysName + "直营KA分大区日报表";
		
		// 查询时间的前一天
		String lastDay = LocalDate.parse(queryDate).minusDays(1L).toString();
		param.clear();
		param.put("queryDate", lastDay);
		param.put("sysId", sysId);
		param.put("column", " region, sell_price, store_code ");
		List<Sale> lastDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 上个月同期
		String lastMonth = LocalDate.parse(queryDate).minusMonths(1L).toString();
		param.clear();
		param.put("queryDate", lastMonth);
		param.put("sysId", sysId);
		param.put("column", " region, sell_price, store_code ");
		List<Sale> lastMonthDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 本月销售数据
		String startDate = LocalDate.parse(queryDate).minusMonths(1L).withDayOfMonth(26).toString();
		param.clear();
		param.put("startDate", startDate);
		param.put("endDate", lastDay);
		param.put("sysId", sysId);
		param.put("column", " region, sell_price, store_code ");
		List<Sale> lastMonthSaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期
		String lastYearDay = LocalDate.parse(queryDate).minusYears(1L).toString();
		param.clear();
		param.put("queryDate", lastYearDay);
		param.put("sysId", sysId);
		param.put("column", " region, sell_price, store_code ");
		List<Sale> lastYearDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期前一天销售数据
		String lastYearLastDay = LocalDate.parse(queryDate).minusYears(1L).minusDays(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastDay);
		param.put("sysId", sysId);
		param.put("column", " region, sell_price, store_code ");
		List<Sale> lastYearLastDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的一个月销售数据
		String lastYear26Day = LocalDate.parse(queryDate).minusYears(1L).minusMonths(1L).withDayOfMonth(26).toString();	
		param.clear();
		param.put("startDate", lastYear26Day);
		param.put("endDate", lastYearLastDay);
		param.put("sysId", sysId);
		param.put("column", " region, sell_price, store_code ");
		List<Sale> lastYearMonthSaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的上一个月同期销售数据
		String lastYearLastMonthDay = LocalDate.parse(queryDate).minusYears(1L).minusMonths(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastMonthDay);
		param.put("sysId", sysId);
		param.put("column", " region, sell_price, store_code ");
		List<Sale> lastYearLastMonthDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的去年同期销售数据
		String lastYearLastYearDay = LocalDate.parse(queryDate).minusYears(1L).minusYears(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastYearDay);
		param.put("sysId", sysId);
		param.put("column", " region, sell_price, store_code ");
		List<Sale> lastYearLastYearDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		RegionSaleModel regionSale = null;
		String region = null;
		List<Sale> saleList = null;
		Sale sale = null;
		
		// 品牌
		Set<String> brandSet = new HashSet<>();
		
		// 当前门店
		Set<String> storeSet = new HashSet<>();
		
		// 可比门店
		Set<String> lastStoreSet = new HashSet<>();
		
		
		String[][] rowValue = new String[regionSaleList.size()][headers.length];
		for (int i = 0, size = regionSaleList.size(); i<size; i++) {
			regionSale = regionSaleList.get(i);
			region = regionSale.getRegion() == null ? "" : regionSale.getRegion();
			saleList = regionSale.getSaleList();
			
			storeSet.clear();
			
			// 当天销售
			for (int j = 0, regionSize = saleList.size(); j<regionSize; j++) {
				sale = saleList.get(j);
				if (StringUtils.isNoneBlank(sale.getBrand())) {
					brandSet.add(sale.getBrand());
				}
				storeSet.add(sale.getStoreCode());
			}

			// 昨日销售
			double nowSellPriceSum = 0;
			for (int j = 0, daySize = lastDaySaleList.size(); j < daySize; j++) {
				sale = lastDaySaleList.get(j);
				if (region.equals(sale.getRegion())) {
					nowSellPriceSum += (sale.getSellPrice() == null) ? 0 : sale.getSellPrice();
				}
			}
			
			// 本月销售
			double monthSellPriceSum = 0;
			for (int j = 0, monthSize = lastMonthSaleList.size(); j<monthSize; j++) {
				sale = lastMonthSaleList.get(j);
				if (region.equals(sale.getRegion())) {
					monthSellPriceSum += (sale.getSellPrice() == null) ? 0 : sale.getSellPrice();
				}
			}
			
			// 昨日销售占比
			double lastSellPercent = 0;
			if (monthSellPriceSum != 0) {
				lastSellPercent = nowSellPriceSum / monthSellPriceSum;
			}
		
			// 上月同期销售金额
			double lastMonthDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastMonthDaySaleList.size(); j < monthSize; j++) {
				sale = lastMonthDaySaleList.get(j);
				if (region.equals(sale.getRegion())) {
					lastMonthDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
				}
			}
			
			// 去年同期销售
			double lastYearDaySellPriceSum = 0;
			lastStoreSet.clear();
			for (int j = 0, monthSize = lastYearDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearDaySaleList.get(j);
				if (region.equals(sale.getRegion())) {
					lastYearDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					if (storeSet.contains(sale.getStoreCode())) {
						lastStoreSet.add(sale.getStoreCode());
					}
				}
			}
			
			// 环比
			double aroundCompare = 0;
			if (monthSellPriceSum != 0) {
				aroundCompare = lastMonthDaySellPriceSum / monthSellPriceSum;
			}
			
			// 同比
			double yearCompare = 0;
			if (monthSellPriceSum != 0) {
				yearCompare = lastYearDaySellPriceSum / monthSellPriceSum;
			}
			
			// 去年同期前一天销售
			double lastYearLastDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastDaySaleList.get(j);
				if (region.equals(sale.getRegion())) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期一个月销售
			double lastYearMonthSellPriceSum = 0;
			for (int j = 0, monthSize = lastYearMonthSaleList.size(); j < monthSize; j++) {
				sale = lastYearMonthSaleList.get(j);
				if (region.equals(sale.getRegion())) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearMonthSellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期的上个月同期销售
			double lastYearLastMonthDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastMonthDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastMonthDaySaleList.get(j);
				if (region.equals(sale.getRegion())) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastMonthDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期的去年同期销售
			double lastYearLastYearDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastYearDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastYearDaySaleList.get(j);
				if (region.equals(sale.getRegion())) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastYearDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 可比门店环比
			double storeAroundCompare = 0;
			if (lastYearMonthSellPriceSum != 0) {
				storeAroundCompare = lastYearLastMonthDaySellPriceSum/lastYearMonthSellPriceSum;
			}

			// 可比门店同比
			double storeYearCompare = 0;
			if (lastYearMonthSellPriceSum != 0) {
				storeYearCompare = lastYearLastYearDaySellPriceSum / lastYearMonthSellPriceSum;
			}
			rowValue[i] = new String[] {
				sysName, // 系统名称
				region, // 大区
				String.valueOf(storeSet.size()), // 门店数量
				"" , //本月目标
				String.valueOf(CommonUtil.setScale("0.00", nowSellPriceSum)), // 昨日销量
				String.valueOf(CommonUtil.setScale("0.00", monthSellPriceSum)), // 本月销售 
				String.valueOf(CommonUtil.setScale("0.00", lastSellPercent*100) + "%"), // 昨日销售占比
				String.valueOf(0), // 本月达成率
				String.valueOf(CommonUtil.setScale("0.00", lastMonthDaySellPriceSum)), // 上月同期销售金额
				String.valueOf(CommonUtil.setScale("0.00", lastYearDaySellPriceSum)), // 去年同月同期销售金额
				String.valueOf(CommonUtil.setScale("0.00", aroundCompare*100)), // 环比
				String.valueOf(CommonUtil.setScale("0.00", yearCompare*100)), // 同比 
				String.valueOf(lastStoreSet.size()), // 可比门店数量
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastDaySellPriceSum)), // 可比门店昨日销售
				String.valueOf(CommonUtil.setScale("0.00", lastYearMonthSellPriceSum)), // 可比门店本月
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastMonthDaySellPriceSum)), // 可比门店上月
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastYearDaySellPriceSum)), // 可比门店去年
				String.valueOf(CommonUtil.setScale("0.00", storeAroundCompare*100)), // 可比门店环比
				String.valueOf(CommonUtil.setScale("0.00", storeYearCompare*100))// 可比门店同比
			};
		}
		
		
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("区域表一级表");
		
		// 报表时间
		Row dateRow = sheet.createRow(0);
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);
		
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());
		
		// 标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, headers.length);
		
		// 表头
		Row headerRow = sheet.createRow(3);
		excelUtil.createRow(headerRow, headers, true);
		
		// 红底样式
		CellStyle cellStyle = excelUtil.getCellStyle(wb, IndexedColors.RED.index);
		
		// 白字
		Font font = excelUtil.getColorFont(wb, IndexedColors.WHITE.index);
		cellStyle.setFont(font);
		
		Row row = null;
		for (int i = 0, size = rowValue.length; i<size; i++) {
			String[] value = rowValue[i];
			row = sheet.createRow(i+4);
			row.createCell(0).setCellValue(value[0]);
			row.createCell(1).setCellValue(value[1]);
			row.createCell(2).setCellValue(value[2]);
			row.createCell(3).setCellValue(value[3]);
			row.createCell(4).setCellValue(value[4]);
			row.createCell(5).setCellValue(value[5]);
			row.createCell(6).setCellValue(value[6]);
			
			// 本月达成率
			Cell reachCell = row.createCell(7);
			reachCell.setCellValue(value[7]);
			
			row.createCell(8).setCellValue(value[8]);
			row.createCell(9).setCellValue(value[9]);
			
			// 环比
			double monthCompare = Double.valueOf(value[10]);
			Cell monthCompareCell = row.createCell(10);
			if (monthCompare < 100) {
				monthCompareCell.setCellStyle(cellStyle);
			}
			monthCompareCell.setCellValue(monthCompare + "%");

			// 同比
		    double yearCompare = Double.valueOf(value[11]);
			Cell yearCompareCell = row.createCell(11);
			if (yearCompare < 100) {
				yearCompareCell.setCellStyle(cellStyle);
			}
			yearCompareCell.setCellValue(yearCompare + "%");
			
			row.createCell(12).setCellValue(value[12]);
			row.createCell(13).setCellValue(value[13]);
			row.createCell(14).setCellValue(value[14]);
			row.createCell(15).setCellValue(value[15]);
			row.createCell(16).setCellValue(value[16]);
			
			// 可比门店环比
			double lastMonthCompare = Double.valueOf(value[17]);
			Cell lastMonthCompareCell = row.createCell(17);
			if (lastMonthCompare < 100) {
				lastMonthCompareCell.setCellStyle(cellStyle);
			}
			lastMonthCompareCell.setCellValue(lastMonthCompare + "%");

			// 同比
			double lastYearCompare = Double.valueOf(value[18]);
			Cell lastYearCompareCell = row.createCell(18);
			if (lastYearCompare < 100) {
				lastYearCompareCell.setCellStyle(cellStyle);
			}
			lastYearCompareCell.setCellValue(lastYearCompare + "%");
		}
		
		wb.write(output);
		output.flush();
		output.close();
	}

	@Override
	public void exportRegionSecondExcelBySys(String queryDate, String sysId, String region, OutputStream output)
			throws Exception {
		if (StringUtils.isBlank(queryDate)) {
			throw new DataException("534");
		}
		if (StringUtils.isBlank(sysId)) {
			throw new DataException("538");
		}
		if (StringUtils.isBlank(region)) {
			throw new DataException("539");
		}
		// 表头
		String[] headers = new String[] {"系统", "大区", "门店编号", "门店名称", "门店数量", "本月销售目标", "昨日销售", "本月销售", 
				"昨日销售占比", "本月达成率", "上月同期销售金额", "去年同月同期销售金额", "环比", "同比", 
				"可比门店数量", "可比门店昨日销售", "可比门店本月", "可比门店上月", "可比门店去年", "可比门店环比",
				"可比门店同比"};
		
		// 获取查询时间当天销售数据
		Map<String, Object> param = new HashMap<>();
		param.put("queryDate", queryDate);
		param.put("sysId", sysId);
		param.put("region", region);
		List<StoreSaleModel> storeSaleList = queryListByObject(QueryId.QUERY_STORE_SALE_BY_CONDITION, param);
		
		// 系统名称
		String sysName = storeSaleList.get(0).getSaleList().get(0).getSysName();
		
		// 标题
		String title = sysName + "直营KA分大区日报表";
		
		// 查询时间的前一天
		String lastDay = LocalDate.parse(queryDate).minusDays(1L).toString();
		param.clear();
		param.put("queryDate", lastDay);
		param.put("sysId", sysId);
		param.put("region", region);
		param.put("column", " sell_price, store_code ");
		List<Sale> lastDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 上个月同期
		String lastMonth = LocalDate.parse(queryDate).minusMonths(1L).toString();
		param.clear();
		param.put("queryDate", lastMonth);
		param.put("sysId", sysId);
		param.put("region", region);
		param.put("column", " sell_price, store_code ");
		List<Sale> lastMonthDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 本月销售数据
		String startDate = LocalDate.parse(queryDate).minusMonths(1L).withDayOfMonth(26).toString();
		param.clear();
		param.put("startDate", startDate);
		param.put("endDate", lastDay);
		param.put("sysId", sysId);
		param.put("region", region);
		param.put("column", " sell_price, store_code ");
		List<Sale> lastMonthSaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期
		String lastYearDay = LocalDate.parse(queryDate).minusYears(1L).toString();
		param.clear();
		param.put("queryDate", lastYearDay);
		param.put("sysId", sysId);
		param.put("region", region);
		param.put("column", " sell_price, store_code ");
		List<Sale> lastYearDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期前一天销售数据
		String lastYearLastDay = LocalDate.parse(queryDate).minusYears(1L).minusDays(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastDay);
		param.put("sysId", sysId);
		param.put("region", region);
		param.put("column", " sell_price, store_code ");
		List<Sale> lastYearLastDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的一个月销售数据
		String lastYear26Day = LocalDate.parse(queryDate).minusYears(1L).minusMonths(1L).withDayOfMonth(26).toString();	
		param.clear();
		param.put("startDate", lastYear26Day);
		param.put("endDate", lastYearLastDay);
		param.put("sysId", sysId);
		param.put("region", region);
		param.put("column", " sell_price, store_code ");
		List<Sale> lastYearMonthSaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的上一个月同期销售数据
		String lastYearLastMonthDay = LocalDate.parse(queryDate).minusYears(1L).minusMonths(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastMonthDay);
		param.put("sysId", sysId);
		param.put("region", region);
		param.put("column", " sell_price, store_code ");
		List<Sale> lastYearLastMonthDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		// 去年同期的去年同期销售数据
		String lastYearLastYearDay = LocalDate.parse(queryDate).minusYears(1L).minusYears(1L).toString();
		param.clear();
		param.put("queryDate", lastYearLastYearDay);
		param.put("sysId", sysId);
		param.put("region", region);
		param.put("column", " sell_price, store_code ");
		List<Sale> lastYearLastYearDaySaleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		StoreSaleModel storeSale = null;
		String storeCode = null;
		String storeName = null;
		List<Sale> saleList = null;
		Sale sale = null;
		
		// 品牌
		Set<String> brandSet = new HashSet<>();
		
		// 当前门店
		Set<String> storeSet = new HashSet<>();
		
		// 可比门店
		Set<String> lastStoreSet = new HashSet<>();
		
		
		String[][] rowValue = new String[storeSaleList.size()][headers.length];
		for (int i = 0, size = storeSaleList.size(); i<size; i++) {
			storeSale = storeSaleList.get(i);
			storeName = storeSale.getStoreName() == null ? "" : storeSale.getStoreName();
			storeCode = storeSale.getStoreCode();
			saleList = storeSale.getSaleList();
			
			storeSet.clear();
			
			// 当天销售
			for (int j = 0, regionSize = saleList.size(); j<regionSize; j++) {
				sale = saleList.get(j);
				if (StringUtils.isNoneBlank(sale.getBrand())) {
					brandSet.add(sale.getBrand());
				}
				storeSet.add(sale.getStoreCode());
			}

			// 昨日销售
			double nowSellPriceSum = 0;
			for (int j = 0, daySize = lastDaySaleList.size(); j < daySize; j++) {
				sale = lastDaySaleList.get(j);
				if (storeCode.equals(sale.getStoreCode())) {
					nowSellPriceSum += (sale.getSellPrice() == null) ? 0 : sale.getSellPrice();
				}
			}
			
			// 本月销售
			double monthSellPriceSum = 0;
			for (int j = 0, monthSize = lastMonthSaleList.size(); j<monthSize; j++) {
				sale = lastMonthSaleList.get(j);
				if (storeCode.equals(sale.getStoreCode())) {
					monthSellPriceSum += (sale.getSellPrice() == null) ? 0 : sale.getSellPrice();
				}
			}
			
			// 昨日销售占比
			double lastSellPercent = 0;
			if (monthSellPriceSum != 0) {
				lastSellPercent = nowSellPriceSum / monthSellPriceSum;
			}
		
			// 上月同期销售金额
			double lastMonthDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastMonthDaySaleList.size(); j < monthSize; j++) {
				sale = lastMonthDaySaleList.get(j);
				if (storeCode.equals(sale.getStoreCode())) {
					lastMonthDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
				}
			}
			
			// 去年同期销售
			double lastYearDaySellPriceSum = 0;
			lastStoreSet.clear();
			for (int j = 0, monthSize = lastYearDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearDaySaleList.get(j);
				if (storeCode.equals(sale.getStoreCode())) {
					lastYearDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					if (storeSet.contains(sale.getStoreCode())) {
						lastStoreSet.add(sale.getStoreCode());
					}
				}
			}
			
			// 环比
			double aroundCompare = 0;
			if (monthSellPriceSum != 0) {
				aroundCompare = lastMonthDaySellPriceSum / monthSellPriceSum;
			}
			
			// 同比
			double yearCompare = 0;
			if (monthSellPriceSum != 0) {
				yearCompare = lastYearDaySellPriceSum / monthSellPriceSum;
			}
			
			// 去年同期前一天销售
			double lastYearLastDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastDaySaleList.get(j);
				if (storeCode.equals(sale.getStoreCode())) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期一个月销售
			double lastYearMonthSellPriceSum = 0;
			for (int j = 0, monthSize = lastYearMonthSaleList.size(); j < monthSize; j++) {
				sale = lastYearMonthSaleList.get(j);
				if (storeCode.equals(sale.getStoreCode())) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearMonthSellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期的上个月同期销售
			double lastYearLastMonthDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastMonthDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastMonthDaySaleList.get(j);
				if (storeCode.equals(sale.getStoreCode())) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastMonthDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 去年同期的去年同期销售
			double lastYearLastYearDaySellPriceSum = 0;
			for (int j = 0, monthSize = lastYearLastYearDaySaleList.size(); j < monthSize; j++) {
				sale = lastYearLastYearDaySaleList.get(j);
				if (storeCode.equals(sale.getStoreCode())) {
					if (storeSet.contains(sale.getStoreCode())) {
						lastYearLastYearDaySellPriceSum += sale.getSellPrice() == null ? 0 : sale.getSellPrice();
					}
				}
			}

			// 可比门店环比
			double storeAroundCompare = 0;
			if (lastYearMonthSellPriceSum != 0) {
				storeAroundCompare = lastYearLastMonthDaySellPriceSum/lastYearMonthSellPriceSum;
			}

			// 可比门店同比
			double storeYearCompare = 0;
			if (lastYearMonthSellPriceSum != 0) {
				storeYearCompare = lastYearLastYearDaySellPriceSum / lastYearMonthSellPriceSum;
			}
			rowValue[i] = new String[] {
				sysName, // 系统名称
				region, // 大区
				storeCode, // 门店编号
				storeName, // 门店名称
				String.valueOf(storeSet.size()), // 门店数量
				"" , //本月目标
				String.valueOf(CommonUtil.setScale("0.00", nowSellPriceSum)), // 昨日销量
				String.valueOf(CommonUtil.setScale("0.00", monthSellPriceSum)), // 本月销售 
				String.valueOf(CommonUtil.setScale("0.00", lastSellPercent*100) + "%"), // 昨日销售占比
				String.valueOf(0), // 本月达成率
				String.valueOf(CommonUtil.setScale("0.00", lastMonthDaySellPriceSum)), // 上月同期销售金额
				String.valueOf(CommonUtil.setScale("0.00", lastYearDaySellPriceSum)), // 去年同月同期销售金额
				String.valueOf(CommonUtil.setScale("0.00", aroundCompare*100)), // 环比
				String.valueOf(CommonUtil.setScale("0.00", yearCompare*100)), // 同比 
				String.valueOf(lastStoreSet.size()), // 可比门店数量
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastDaySellPriceSum)), // 可比门店昨日销售
				String.valueOf(CommonUtil.setScale("0.00", lastYearMonthSellPriceSum)), // 可比门店本月
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastMonthDaySellPriceSum)), // 可比门店上月
				String.valueOf(CommonUtil.setScale("0.00", lastYearLastYearDaySellPriceSum)), // 可比门店去年
				String.valueOf(CommonUtil.setScale("0.00", storeAroundCompare*100)), // 可比门店环比
				String.valueOf(CommonUtil.setScale("0.00", storeYearCompare*100))// 可比门店同比
			};
		}
		
		
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("区域表一级表");
		
		// 报表时间
		Row dateRow = sheet.createRow(0);
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);
		
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());
		
		// 标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, headers.length);
		
		// 表头
		Row headerRow = sheet.createRow(3);
		excelUtil.createRow(headerRow, headers, true);
		
		// 红底样式
		CellStyle cellStyle = excelUtil.getCellStyle(wb, IndexedColors.RED.index);
		
		// 白字
		Font font = excelUtil.getColorFont(wb, IndexedColors.WHITE.index);
		cellStyle.setFont(font);
		
		Row row = null;
		for (int i = 0, size = rowValue.length; i<size; i++) {
			String[] value = rowValue[i];
			row = sheet.createRow(i+4);
			row.createCell(0).setCellValue(value[0]);
			row.createCell(1).setCellValue(value[1]);
			row.createCell(2).setCellValue(value[2]);
			row.createCell(3).setCellValue(value[3]);
			row.createCell(4).setCellValue(value[4]);
			row.createCell(5).setCellValue(value[5]);
			row.createCell(6).setCellValue(value[6]);
			row.createCell(7).setCellValue(value[7]);
			row.createCell(8).setCellValue(value[8]);
			
			// 本月达成率
			Cell reachCell = row.createCell(9);
			reachCell.setCellValue(value[9]);
			
			row.createCell(10).setCellValue(value[10]);
			row.createCell(11).setCellValue(value[11]);
			
			// 环比
			double monthCompare = Double.valueOf(value[12]);
			Cell monthCompareCell = row.createCell(12);
			if (monthCompare < 100) {
				monthCompareCell.setCellStyle(cellStyle);
			}
			monthCompareCell.setCellValue(monthCompare + "%");

			// 同比
		    double yearCompare = Double.valueOf(value[13]);
			Cell yearCompareCell = row.createCell(13);
			if (yearCompare < 100) {
				yearCompareCell.setCellStyle(cellStyle);
			}
			yearCompareCell.setCellValue(yearCompare + "%");
			
			row.createCell(14).setCellValue(value[14]);
			row.createCell(15).setCellValue(value[15]);
			row.createCell(16).setCellValue(value[16]);
			row.createCell(17).setCellValue(value[17]);
			row.createCell(18).setCellValue(value[18]);
			
			// 可比门店环比
			double lastMonthCompare = Double.valueOf(value[19]);
			Cell lastMonthCompareCell = row.createCell(19);
			if (lastMonthCompare < 100) {
				lastMonthCompareCell.setCellStyle(cellStyle);
			}
			lastMonthCompareCell.setCellValue(lastMonthCompare + "%");

			// 同比
			double lastYearCompare = Double.valueOf(value[20]);
			Cell lastYearCompareCell = row.createCell(20);
			if (lastYearCompare < 100) {
				lastYearCompareCell.setCellStyle(cellStyle);
			}
			lastYearCompareCell.setCellValue(lastYearCompare + "%");
			// 哦豁尴尬了
		}
		
		wb.write(output);
		output.flush();
		output.close();
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void exportSaleExcelBySysId(String region, String saleDate, HttpServletResponse response) throws Exception {
		if(CommonUtil.isBlank(region)) {
			throw new DataException("102");
		}
		if(CommonUtil.isBlank(saleDate)) {
			throw new DataException("534");
		}
		Map<String, Object> params = new HashMap<>(8);
		//区域集合 包括区域名称 门店数量 指定日期的销售额
		List<Map<String, Object>> regionDataList = new ArrayList<>(10);
		params.put("region", region);
		params.put("saleDate", saleDate);
		regionDataList = queryListByObject(QueryId.QUERY_SALE_REPORT_BY_REGION, params);
		logger.info("--->>>regionDataList.first: {}<<<", JsonUtil.toJson(regionDataList));
		//得到前一个月26号到指定日期的日期字符串
		List<String> daysList = DateUtil.getMonthDays(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		String dateStr = DateUtil.getCurrentDateStr();
		int index = daysList.indexOf(dateStr);
		List<String> newDaysList = new ArrayList<>(10);
		for(int i = 0; i < index; i++) {
			newDaysList.add(daysList.get(i));
		}
		//需要计算每个区域上个月26号到指定日期的销售额
		Map<String, Object> dateParams = new HashMap<>(4);
		dateParams.put("region", region);
		dateParams.put("startDate", daysList.get(0));
		dateParams.put("endDate", saleDate);
		List<Map<String, Object>> regionMonthSaleList = new ArrayList<>(10);
		regionMonthSaleList = queryListByObject(QueryId.QUERY_SALE_REPORT_BY_DATE, dateParams);
		logger.info("-->>通过: {}<<--", JsonUtil.toJson(regionMonthSaleList));
		
		Map<String, Object> dataMap = new HashMap<>(10);
		for(Map<String, Object> map : regionMonthSaleList) {
			dataMap.put((String) map.get("sysId"), map);
		}
		//区域集合 包括区域名称 门店数量 本月销售目标 昨日销售额 月销售 昨日销售占比 本月达成率
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> monthSaleMap = (Map<String, Object>) dataMap.get(regionData.get("sysId"));
			if(CommonUtil.isNotBlank(monthSaleMap)) {
				double monthSale = CommonUtil.isBlank(monthSaleMap.get("monthSale")) ? 0.0 : (double) monthSaleMap.get("monthSale");
				double price = CommonUtil.isBlank(regionData.get("price")) ? 0.0 : (double) regionData.get("price");
				//月销售
				regionData.put("monthSale", monthSale);
				if(monthSale != 0) {
					//昨日销售占比
					regionData.put("yesterdayMonthSale", price / monthSale);
				} else {
					regionData.put("yesterdayMonthSale", 0);
				}
				//本月销售目标 暂无  百亚自行维护
				regionData.put("monthGoalTarget", 0);
				//本月达成率 (本月销售)/(本月销售目标)
				regionData.put("monthGoalSale", 0);
			}
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.MONTH, -1);
		String dateString = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD).format(calendar.getTime());
		logger.info("--->>>上月今日:{}<<<---", dateString);
		params.clear();
		params.put("dateString", dateString);
		params.put("region", region);
		//上月今日销售额
		List<Map<String, Object>> lastMonthDateList = queryListByObject(QueryId.QUERY_SALE_REPORT_BY_PARAMS, params);
		params.clear();
		params.put("region", region);
		params.put("saleDate", dateString);
		//可比上月今日销售集合
		List<Sale> lastMonthDataDiffList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		dataMap.clear();
		for(int i = 0; i < lastMonthDateList.size(); i++) {
			dataMap.put((String) lastMonthDateList.get(i).get("sysId"), lastMonthDateList.get(i));
		}
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> lastMonthSaleMap = (Map<String, Object>) dataMap.get(regionData.get("sysId"));
			if(CommonUtil.isNotBlank(lastMonthSaleMap)) {
				//上月同期销售金额
				regionData.put("lastMonthDateSale", lastMonthSaleMap.get("dateSale"));
			}
		}
		logger.info("-->>通过上月同期: {}<<--", JsonUtil.toJson(lastMonthDateList));
		
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.YEAR, -1);
		dateString = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD).format(calendar.getTime());
		params.clear();
		params.put("dateString", dateString);
		params.put("region", region);
		//去年今日销售额
		List<Map<String, Object>> lastYearDateList = queryListByObject(QueryId.QUERY_SALE_REPORT_BY_PARAMS, params);
		params.clear();
		params.put("region", region);
		params.put("saleDate", dateString);
		//可比去年今日销售集合
		List<Sale> lastYearDataDiffList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		dataMap.clear();
		for(int i = 0; i < lastYearDateList.size(); i++) {
			dataMap.put((String) lastYearDateList.get(i).get("sysId"), lastYearDateList.get(i));
		}
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> lastYearSaleMap = (Map<String, Object>) dataMap.get(regionData.get("sysId"));
			if(CommonUtil.isNotBlank(lastYearSaleMap)) {
				//去年同月同期销售金额
				regionData.put("lastYearDateSale", lastYearSaleMap.get("dateSale"));
				double lastMonthDateSale = CommonUtil.isBlank(regionData.get("lastMonthDateSale")) ? 0.0 : (double) regionData.get("lastMonthDateSale");
				double monthSale = CommonUtil.isBlank(regionData.get("monthSale")) ? 0.0 : (double) regionData.get("monthSale");
				double lastYearDateSale = CommonUtil.isBlank(regionData.get("lastYearDateSale")) ? 0.0 : (double) regionData.get("lastYearDateSale");
				if(monthSale != 0) {
					//环比
					regionData.put("chaimIndex", lastMonthDateSale / monthSale);
					//同比
					regionData.put("yearBasis", lastYearDateSale / monthSale);
				} else {
					regionData.put("chaimIndex", 0.0);
					regionData.put("yearBasis", 0.0);
				}
			}
		}
		logger.info("-->>通过去年同期<<--");
		params.clear();
		params.put("region", region);
		params.put("saleDate", saleDate);
		//指定日期销售门店
		List<Sale> saleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//去年门店
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.YEAR, -1);
		Date lastYearDate = calendar.getTime();
		dateStr = DateUtil.format(lastYearDate, DateUtil.DATE_PATTERN.YYYY_MM_DD);
		params.clear();
		params.put("region", region);
		params.put("saleDate", dateStr);
		List<Sale> lastYearSaleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//指定日期与上年同时有销售的销售集合
		Set<Sale> saleSet = new HashSet<>(10);
		for(int i = 0, size = saleList.size(); i < size; i++) {
			for(int j = 0, num = lastYearSaleList.size(); j < num; j++) {
				Sale newSale = saleList.get(i);
				Sale oldSale = lastYearSaleList.get(j);
				if((newSale.getSysId().equals(oldSale.getSysId())) && (newSale.getStoreCode().equals(oldSale.getStoreCode()))) {					
					saleSet.add(newSale);
				}
			}
		}
		//可比门店数量
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			int num = 0;
			for(Sale sale : saleSet) {
				if(CommonUtil.isNotBlank(regionDataMap.get("sysId")) && CommonUtil.isNotBlank(sale.getSysId())) {
					if(sale.getSysId().equals(regionDataMap.get("sysId"))) {
						regionDataMap.put("differNum", num++);
					}
				}
			}
		}
		logger.info("-->>通过可比门店<<--");
		Date yesterday = DateUtil.addDate(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD), Calendar.DATE, -1);
		//去年前一天销售
		Date lastYearYesterday = DateUtil.addDate(yesterday, Calendar.YEAR, -1);
		lastYearSaleList.clear();
		params.clear();
		params.put("region", region);
		params.put("saleDate", DateUtil.format(lastYearYesterday, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		lastYearSaleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//去除不同的门店销售
		for(Sale sale : saleSet) {
			for(int i = 0; i < lastYearSaleList.size(); i++) {
				if(!sale.getStoreCode().equals(lastYearSaleList.get(i).getStoreCode())) {
					lastYearSaleList.remove(i);
				}
			}
		}
		//可比门店昨日销售
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for(Sale sale : lastYearSaleList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("sysId")) && CommonUtil.isNotBlank(sale.getSysId())) {
					if(sale.getSysId().equals(regionDataMap.get("sysId"))) {
						regionDataMap.put("differYesterDaySale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比昨日销售<<--");
		List<Sale> monthDataList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, dateParams);
		//logger.info(">>>可比本月数据: {}<<<", JsonUtil.toJson(monthDataList));
		LinkedList<Sale> monthDataLinkList = new LinkedList<>();
		monthDataLinkList.addAll(monthDataList);
		//去除不同的门店销售
		for(Sale sale : saleSet) {
			for(int i = 0, size = monthDataLinkList.size(); i < size; i++) {
				if(!sale.getStoreCode().equals(monthDataLinkList.get(i).getStoreCode())) {
					monthDataLinkList.remove(i);
				}
			}
		}
		monthDataList.clear();
		monthDataList.addAll(monthDataLinkList);
		//可比本月销售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : monthDataList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("sysId")) && CommonUtil.isNotBlank(sale.getSysId())) {
					if (sale.getSysId().equals(regionDataMap.get("sysId"))) {
						regionDataMap.put("differMonthSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比本月销售<<--");
		//去除不同的门店销售
		LinkedList<Sale> lastMonthDataDiffLinkList = new LinkedList<>();
		lastMonthDataDiffLinkList.addAll(lastMonthDataDiffList);
		for(Sale sale : saleSet) {
			for(int i = 0, size = lastMonthDataDiffLinkList.size(); i < size; i++) {
				if(!sale.getStoreCode().equals(lastMonthDataDiffLinkList.get(i).getStoreCode())) {
					lastMonthDataDiffLinkList.remove(i);
				}
			}
		}
		lastMonthDataDiffList.clear();
		lastMonthDataDiffList.addAll(lastMonthDataDiffLinkList);
		//可比上月同期銷售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : lastMonthDataDiffList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("sysId")) && CommonUtil.isNotBlank(sale.getSysId())) {
					if (sale.getSysId().equals(regionDataMap.get("sysId"))) {
						regionDataMap.put("differLastMonthDateSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比上月同期銷售<<--");
		//去除不同的门店销售
		LinkedList<Sale> lastYearDataDiffLinkList = new LinkedList<>();
		lastYearDataDiffLinkList.addAll(lastYearDataDiffList);
		for (Sale sale : saleSet) {
			for (int i = 0, size = lastYearDataDiffLinkList.size(); i < size; i++) {
				if (!sale.getStoreCode().equals(lastYearDataDiffLinkList.get(i).getStoreCode())) {
					lastYearDataDiffLinkList.remove(i);
				}
			}
		}
		lastYearDataDiffList.clear();
		lastYearDataDiffList.addAll(lastYearDataDiffLinkList);
		//可比去年同期銷售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : lastYearDataDiffList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("sysId")) && CommonUtil.isNotBlank(sale.getSysId())) {
					if (sale.getSysId().equals(regionDataMap.get("sysId"))) {
						regionDataMap.put("differLastYearDateSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比去年同期銷售 <<--");
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			//可比月銷售
			double differMonthSale = CommonUtil.isBlank(regionDataMap.get("differMonthSale")) ? 0.0 : (double) regionDataMap.get("differMonthSale");
			//可比上月銷售
			double differLastMonthDateSale = CommonUtil.isBlank(regionDataMap.get("differLastMonthDateSale")) ? 0.0 : (double) regionDataMap.get("differLastMonthDateSale");
			//可比去年銷售
			double differLastYearDateSale = CommonUtil.isBlank(regionDataMap.get("differLastYearDateSale")) ? 0.0 : (double) regionDataMap.get("differLastYearDateSale");
			if(differMonthSale != 0) {
				//可比环比
				regionDataMap.put("differChaimIndex", differLastMonthDateSale / differMonthSale);
				//可比同比
				regionDataMap.put("differYearBasis", differLastYearDateSale / differMonthSale);
			} else {
				regionDataMap.put("differChaimIndex", 0.0);
				regionDataMap.put("differYearBasis", 0.0);
			}
			
		}
		logger.info("--->>>组装数据完成<<<---");
		String title = "大区2直营KA分系统日报表";
		String[] header = new String[] {"大区", "系统", "门店数量", "本月销售目标", "昨日销售", "本月销售", 
				"昨日销售占比(%)", "本月达成率", "上月同期销售金额", "去年同月同期销售金额", "环比(%)", "同比(%)", 
				"可比门店数量", "可比门店昨日销售", "可比门店本月", "可比门店上月", "可比门店去年", "可比门店环比(%)",
				"可比门店同比(%)"};
		List<String> codeList = codeDictService
				.queryCodeListByServiceCode(CodeEnum.CODE_DICT_REGION_SYSTEM_REPORT.value());
		String fileName = "按区域 区域表一级报表-" + DateUtil.getCurrentDateStr();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
		ExcelUtil excelUtil = new ExcelUtil();
		excelUtil.exportTemplateByMap(header, regionDataList, title, codeList, response.getOutputStream());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void exportSaleExcelByProvinceArea(String region, String saleDate, HttpServletResponse response)
			throws Exception {
		if(CommonUtil.isBlank(region)) {
			throw new DataException("102");
		}
		if(CommonUtil.isBlank(saleDate)) {
			throw new DataException("534");
		}
		Map<String, Object> params = new HashMap<>(8);
		//区域集合 包括区域名称 门店数量 指定日期的销售额
		List<Map<String, Object>> regionDataList = new ArrayList<>(10);
		params.put("region", region);
		params.put("saleDate", saleDate);
		regionDataList = queryListByObject(QueryId.QUERY_SALE_REPORT_BY_PROVINCE_AREA, params);
		logger.info("--->>>regionDataList.first: {}<<<", JsonUtil.toJson(regionDataList));
		//得到前一个月26号到指定日期的日期字符串
		List<String> daysList = DateUtil.getMonthDays(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		String dateStr = DateUtil.getCurrentDateStr();
		int index = daysList.indexOf(dateStr);
		List<String> newDaysList = new ArrayList<>(10);
		for(int i = 0; i < index; i++) {
			newDaysList.add(daysList.get(i));
		}
		//需要计算每个区域上个月26号到指定日期的销售额
		Map<String, Object> dateParams = new HashMap<>(4);
		dateParams.put("region", region);
		dateParams.put("startDate", daysList.get(0));
		dateParams.put("endDate", saleDate);
		List<Map<String, Object>> regionMonthSaleList = new ArrayList<>(10);
		regionMonthSaleList = queryListByObject(QueryId.QUERY_SALE_REPORT_BY_PROVINCE_AREA_AND_DATE, dateParams);
		logger.info("-->>通过: {}<<--", JsonUtil.toJson(regionMonthSaleList));
		
		Map<String, Object> dataMap = new HashMap<>(10);
		for(Map<String, Object> map : regionMonthSaleList) {
			dataMap.put((String) map.get("provinceArea"), map);
		}
		//区域集合 包括区域名称 门店数量 本月销售目标 昨日销售额 月销售 昨日销售占比 本月达成率
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> monthSaleMap = (Map<String, Object>) dataMap.get(regionData.get("provinceArea"));
			if(CommonUtil.isNotBlank(monthSaleMap)) {
				double monthSale = CommonUtil.isBlank(monthSaleMap.get("monthSale")) ? 0.0 : (double) monthSaleMap.get("monthSale");
				double price = CommonUtil.isBlank(regionData.get("price")) ? 0.0 : (double) regionData.get("price");
				//月销售
				regionData.put("monthSale", monthSale);
				if(monthSale != 0) {
					//昨日销售占比
					regionData.put("yesterdayMonthSale", price / monthSale);
				} else {
					regionData.put("yesterdayMonthSale", 0);
				}
				//本月销售目标 暂无  百亚自行维护
				regionData.put("monthGoalTarget", 0);
				//本月达成率 (本月销售)/(本月销售目标)
				regionData.put("monthGoalSale", 0);
			}
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.MONTH, -1);
		String dateString = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD).format(calendar.getTime());
		logger.info("--->>>上月今日:{}<<<---", dateString);
		params.clear();
		params.put("dateString", dateString);
		params.put("region", region);
		//上月今日销售额
		List<Map<String, Object>> lastMonthDateList = queryListByObject(QueryId.QUERY_SALE_REPORT_SECOND_BY_PARAMS, params);
		params.clear();
		params.put("region", region);
		params.put("saleDate", dateString);
		//可比上月今日销售集合
		List<Sale> lastMonthDataDiffList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		dataMap.clear();
		for(int i = 0; i < lastMonthDateList.size(); i++) {
			dataMap.put((String) lastMonthDateList.get(i).get("provinceArea"), lastMonthDateList.get(i));
		}
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> lastMonthSaleMap = (Map<String, Object>) dataMap.get(regionData.get("provinceArea"));
			if(CommonUtil.isNotBlank(lastMonthSaleMap)) {
				//上月同期销售金额
				regionData.put("lastMonthDateSale", lastMonthSaleMap.get("dateSale"));
			}
		}
		logger.info("-->>通过上月同期: {}<<--", JsonUtil.toJson(lastMonthDateList));
		
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.YEAR, -1);
		dateString = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD).format(calendar.getTime());
		params.clear();
		params.put("dateString", dateString);
		params.put("region", region);
		//去年今日销售额
		List<Map<String, Object>> lastYearDateList = queryListByObject(QueryId.QUERY_SALE_REPORT_SECOND_BY_PARAMS, params);
		params.clear();
		params.put("region", region);
		params.put("saleDate", dateString);
		//可比去年今日销售集合
		List<Sale> lastYearDataDiffList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		dataMap.clear();
		for(int i = 0; i < lastYearDateList.size(); i++) {
			dataMap.put((String) lastYearDateList.get(i).get("provinceArea"), lastYearDateList.get(i));
		}
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> lastYearSaleMap = (Map<String, Object>) dataMap.get(regionData.get("provinceArea"));
			if(CommonUtil.isNotBlank(lastYearSaleMap)) {
				//去年同月同期销售金额
				regionData.put("lastYearDateSale", lastYearSaleMap.get("dateSale"));
				double lastMonthDateSale = CommonUtil.isBlank(regionData.get("lastMonthDateSale")) ? 0.0 : (double) regionData.get("lastMonthDateSale");
				double monthSale = CommonUtil.isBlank(regionData.get("monthSale")) ? 0.0 : (double) regionData.get("monthSale");
				double lastYearDateSale = CommonUtil.isBlank(regionData.get("lastYearDateSale")) ? 0.0 : (double) regionData.get("lastYearDateSale");
				if(monthSale != 0) {
					//环比
					regionData.put("chaimIndex", lastMonthDateSale / monthSale);
					//同比
					regionData.put("yearBasis", lastYearDateSale / monthSale);
				} else {
					regionData.put("chaimIndex", 0.0);
					regionData.put("yearBasis", 0.0);
				}
			}
		}
		logger.info("-->>通过去年同期<<--");
		params.clear();
		params.put("region", region);
		params.put("saleDate", saleDate);
		//指定日期销售门店
		List<Sale> saleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//去年门店
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.YEAR, -1);
		Date lastYearDate = calendar.getTime();
		dateStr = DateUtil.format(lastYearDate, DateUtil.DATE_PATTERN.YYYY_MM_DD);
		params.clear();
		params.put("region", region);
		params.put("saleDate", dateStr);
		List<Sale> lastYearSaleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//指定日期与上年同时有销售的销售集合
		Set<Sale> saleSet = new HashSet<>(10);
		for(int i = 0, size = saleList.size(); i < size; i++) {
			for(int j = 0, num = lastYearSaleList.size(); j < num; j++) {
				Sale newSale = saleList.get(i);
				Sale oldSale = lastYearSaleList.get(j);
				if((newSale.getSysId().equals(oldSale.getSysId())) && (newSale.getStoreCode().equals(oldSale.getStoreCode()))) {					
					saleSet.add(newSale);
				}
			}
		}
		//可比门店数量
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			int num = 0;
			for(Sale sale : saleSet) {
				if(CommonUtil.isNotBlank(regionDataMap.get("provinceArea")) && CommonUtil.isNotBlank(sale.getProvinceArea())) {
					if(sale.getProvinceArea().equals(regionDataMap.get("provinceArea"))) {
						regionDataMap.put("differNum", num++);
					}
				}
			}
		}
		logger.info("-->>通过可比门店<<--");
		Date yesterday = DateUtil.addDate(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD), Calendar.DATE, -1);
		//去年前一天销售
		Date lastYearYesterday = DateUtil.addDate(yesterday, Calendar.YEAR, -1);
		lastYearSaleList.clear();
		params.clear();
		params.put("region", region);
		params.put("saleDate", DateUtil.format(lastYearYesterday, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		lastYearSaleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//去除不同的门店销售
		for(Sale sale : saleSet) {
			for(int i = 0; i < lastYearSaleList.size(); i++) {
				if(!sale.getStoreCode().equals(lastYearSaleList.get(i).getStoreCode())) {
					lastYearSaleList.remove(i);
				}
			}
		}
		//可比门店昨日销售
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for(Sale sale : lastYearSaleList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("provinceArea")) && CommonUtil.isNotBlank(sale.getProvinceArea())) {
					if(sale.getProvinceArea().equals(regionDataMap.get("provinceArea"))) {
						regionDataMap.put("differYesterDaySale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比昨日销售<<--");
		List<Sale> monthDataList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, dateParams);
		//logger.info(">>>可比本月数据: {}<<<", JsonUtil.toJson(monthDataList));
		LinkedList<Sale> monthDataLinkList = new LinkedList<>();
		monthDataLinkList.addAll(monthDataList);
		//去除不同的门店销售
		for(Sale sale : saleSet) {
			for(int i = 0, size = monthDataLinkList.size(); i < size; i++) {
				if(!sale.getStoreCode().equals(monthDataLinkList.get(i).getStoreCode())) {
					monthDataLinkList.remove(i);
				}
			}
		}
		monthDataList.clear();
		monthDataList.addAll(monthDataLinkList);
		//可比本月销售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : monthDataList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("provinceArea")) && CommonUtil.isNotBlank(sale.getProvinceArea())) {
					if (sale.getProvinceArea().equals(regionDataMap.get("provinceArea"))) {
						regionDataMap.put("differMonthSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比本月销售<<--");
		//去除不同的门店销售
		LinkedList<Sale> lastMonthDataDiffLinkList = new LinkedList<>();
		lastMonthDataDiffLinkList.addAll(lastMonthDataDiffList);
		for(Sale sale : saleSet) {
			for(int i = 0, size = lastMonthDataDiffLinkList.size(); i < size; i++) {
				if(!sale.getStoreCode().equals(lastMonthDataDiffLinkList.get(i).getStoreCode())) {
					lastMonthDataDiffLinkList.remove(i);
				}
			}
		}
		lastMonthDataDiffList.clear();
		lastMonthDataDiffList.addAll(lastMonthDataDiffLinkList);
		//可比上月同期銷售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : lastMonthDataDiffList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("provinceArea")) && CommonUtil.isNotBlank(sale.getProvinceArea())) {
					if (sale.getProvinceArea().equals(regionDataMap.get("provinceArea"))) {
						regionDataMap.put("differLastMonthDateSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比上月同期銷售<<--");
		//去除不同的门店销售
		LinkedList<Sale> lastYearDataDiffLinkList = new LinkedList<>();
		lastYearDataDiffLinkList.addAll(lastYearDataDiffList);
		for (Sale sale : saleSet) {
			for (int i = 0, size = lastYearDataDiffLinkList.size(); i < size; i++) {
				if (!sale.getStoreCode().equals(lastYearDataDiffLinkList.get(i).getStoreCode())) {
					lastYearDataDiffLinkList.remove(i);
				}
			}
		}
		lastYearDataDiffList.clear();
		lastYearDataDiffList.addAll(lastYearDataDiffLinkList);
		//可比去年同期銷售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : lastYearDataDiffList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("provinceArea")) && CommonUtil.isNotBlank(sale.getProvinceArea())) {
					if (sale.getProvinceArea().equals(regionDataMap.get("provinceArea"))) {
						regionDataMap.put("differLastYearDateSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比去年同期銷售 <<--");
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			//可比月銷售
			double differMonthSale = CommonUtil.isBlank(regionDataMap.get("differMonthSale")) ? 0.0 : (double) regionDataMap.get("differMonthSale");
			//可比上月銷售
			double differLastMonthDateSale = CommonUtil.isBlank(regionDataMap.get("differLastMonthDateSale")) ? 0.0 : (double) regionDataMap.get("differLastMonthDateSale");
			//可比去年銷售
			double differLastYearDateSale = CommonUtil.isBlank(regionDataMap.get("differLastYearDateSale")) ? 0.0 : (double) regionDataMap.get("differLastYearDateSale");
			if(differMonthSale != 0) {
				//可比环比
				regionDataMap.put("differChaimIndex", differLastMonthDateSale / differMonthSale);
				//可比同比
				regionDataMap.put("differYearBasis", differLastYearDateSale / differMonthSale);
			} else {
				regionDataMap.put("differChaimIndex", 0.0);
				regionDataMap.put("differYearBasis", 0.0);
			}
			
		}
		logger.info("--->>>组装数据完成<<<---");
		String title = "大区2直营KA分省区日报表";
		String[] header = new String[] {"大区", "省区", "门店数量", "本月销售目标", "昨日销售", "本月销售", 
				"昨日销售占比(%)", "本月达成率", "上月同期销售金额", "去年同月同期销售金额", "环比(%)", "同比(%)", 
				"可比门店数量", "可比门店昨日销售", "可比门店本月", "可比门店上月", "可比门店去年", "可比门店环比(%)",
				"可比门店同比(%)"};
		List<String> codeList = codeDictService
				.queryCodeListByServiceCode(CodeEnum.CODE_DICT_REGION_PROVINCE_AREA_REPORT.value());
		String fileName = "按区域 区域表二级报表-" + DateUtil.getCurrentDateStr();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
		ExcelUtil excelUtil = new ExcelUtil();
		excelUtil.exportTemplateByMap(header, regionDataList, title, codeList, response.getOutputStream());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void exportSaleExcelByStoreCode(String provinceArea, String saleDate,
			HttpServletResponse response) throws Exception {
		if(CommonUtil.isBlank(provinceArea)) {
			throw new DataException("103");
		}
		if(CommonUtil.isBlank(saleDate)) {
			throw new DataException("534");
		}
		Map<String, Object> params = new HashMap<>(8);
		//区域集合 包括区域名称 门店数量 指定日期的销售额
		List<Map<String, Object>> regionDataList = new ArrayList<>(10);
		params.put("provinceArea", provinceArea);
		params.put("saleDate", saleDate);
		regionDataList = queryListByObject(QueryId.QUERY_SALE_REPORT_BY_STORE_CODE, params);
		logger.info("--->>>regionDataList.first: {}<<<", JsonUtil.toJson(regionDataList));
		//得到前一个月26号到指定日期的日期字符串
		List<String> daysList = DateUtil.getMonthDays(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		String dateStr = DateUtil.getCurrentDateStr();
		int index = daysList.indexOf(dateStr);
		List<String> newDaysList = new ArrayList<>(10);
		for(int i = 0; i < index; i++) {
			newDaysList.add(daysList.get(i));
		}
		//需要计算每个区域上个月26号到指定日期的销售额
		Map<String, Object> dateParams = new HashMap<>(4);
		dateParams.put("provinceArea", provinceArea);
		dateParams.put("startDate", daysList.get(0));
		dateParams.put("endDate", saleDate);
		List<Map<String, Object>> regionMonthSaleList = new ArrayList<>(10);
		regionMonthSaleList = queryListByObject(QueryId.QUERY_SALE_REPORT_BY_STORE_CODE_AND_DATE, dateParams);
		logger.info("-->>通过: {}<<--", JsonUtil.toJson(regionMonthSaleList));
		
		Map<String, Object> dataMap = new HashMap<>(10);
		for(Map<String, Object> map : regionMonthSaleList) {
			dataMap.put((String) map.get("storeCode"), map);
		}
		//区域集合 包括区域名称 门店数量 本月销售目标 昨日销售额 月销售 昨日销售占比 本月达成率
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> monthSaleMap = (Map<String, Object>) dataMap.get(regionData.get("storeCode"));
			if(CommonUtil.isNotBlank(monthSaleMap)) {
				double monthSale = CommonUtil.isBlank(monthSaleMap.get("monthSale")) ? 0.0 : (double) monthSaleMap.get("monthSale");
				double price = CommonUtil.isBlank(regionData.get("price")) ? 0.0 : (double) regionData.get("price");
				//月销售
				regionData.put("monthSale", monthSale);
				if(monthSale != 0) {
					//昨日销售占比
					regionData.put("yesterdayMonthSale", price / monthSale);
				} else {
					regionData.put("yesterdayMonthSale", 0);
				}
				//本月销售目标 暂无  百亚自行维护
				regionData.put("monthGoalTarget", 0);
				//本月达成率 (本月销售)/(本月销售目标)
				regionData.put("monthGoalSale", 0);
			}
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.MONTH, -1);
		String dateString = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD).format(calendar.getTime());
		logger.info("--->>>上月今日:{}<<<---", dateString);
		params.clear();
		params.put("dateString", dateString);
		params.put("provinceArea", provinceArea);
		//上月今日销售额
		List<Map<String, Object>> lastMonthDateList = queryListByObject(QueryId.QUERY_SALE_REPORT_THIRD_BY_PARAMS, params);
		params.clear();
		params.put("provinceArea", provinceArea);
		params.put("saleDate", dateString);
		//可比上月今日销售集合
		List<Sale> lastMonthDataDiffList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		dataMap.clear();
		for(int i = 0; i < lastMonthDateList.size(); i++) {
			dataMap.put((String) lastMonthDateList.get(i).get("storeCode"), lastMonthDateList.get(i));
		}
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> lastMonthSaleMap = (Map<String, Object>) dataMap.get(regionData.get("storeCode"));
			if(CommonUtil.isNotBlank(lastMonthSaleMap)) {
				//上月同期销售金额
				regionData.put("lastMonthDateSale", lastMonthSaleMap.get("dateSale"));
			}
		}
		logger.info("-->>通过上月同期: {}<<--", JsonUtil.toJson(lastMonthDateList));
		
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.YEAR, -1);
		dateString = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD).format(calendar.getTime());
		params.clear();
		params.put("dateString", dateString);
		params.put("provinceArea", provinceArea);
		//去年今日销售额
		List<Map<String, Object>> lastYearDateList = queryListByObject(QueryId.QUERY_SALE_REPORT_THIRD_BY_PARAMS, params);
		params.clear();
		params.put("provinceArea", provinceArea);
		params.put("saleDate", dateString);
		//可比去年今日销售集合
		List<Sale> lastYearDataDiffList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		dataMap.clear();
		for(int i = 0; i < lastYearDateList.size(); i++) {
			dataMap.put((String) lastYearDateList.get(i).get("storeCode"), lastYearDateList.get(i));
		}
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionData = regionDataList.get(i);
			Map<String, Object> lastYearSaleMap = (Map<String, Object>) dataMap.get(regionData.get("storeCode"));
			if(CommonUtil.isNotBlank(lastYearSaleMap)) {
				//去年同月同期销售金额
				regionData.put("lastYearDateSale", lastYearSaleMap.get("dateSale"));
				double lastMonthDateSale = CommonUtil.isBlank(regionData.get("lastMonthDateSale")) ? 0.0 : (double) regionData.get("lastMonthDateSale");
				double monthSale = CommonUtil.isBlank(regionData.get("monthSale")) ? 0.0 : (double) regionData.get("monthSale");
				double lastYearDateSale = CommonUtil.isBlank(regionData.get("lastYearDateSale")) ? 0.0 : (double) regionData.get("lastYearDateSale");
				if(monthSale != 0) {
					//环比
					regionData.put("chaimIndex", lastMonthDateSale / monthSale);
					//同比
					regionData.put("yearBasis", lastYearDateSale / monthSale);
				} else {
					regionData.put("chaimIndex", 0.0);
					regionData.put("yearBasis", 0.0);
				}
			}
		}
		logger.info("-->>通过去年同期<<--");
		params.clear();
		params.put("provinceArea", provinceArea);
		params.put("saleDate", saleDate);
		//指定日期销售门店
		List<Sale> saleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//去年门店
		calendar.setTime(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		calendar.add(Calendar.YEAR, -1);
		Date lastYearDate = calendar.getTime();
		dateStr = DateUtil.format(lastYearDate, DateUtil.DATE_PATTERN.YYYY_MM_DD);
		params.clear();
		params.put("provinceArea", provinceArea);
		params.put("saleDate", dateStr);
		List<Sale> lastYearSaleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//指定日期与上年同时有销售的销售集合
		Set<Sale> saleSet = new HashSet<>(10);
		for(int i = 0, size = saleList.size(); i < size; i++) {
			for(int j = 0, num = lastYearSaleList.size(); j < num; j++) {
				Sale newSale = saleList.get(i);
				Sale oldSale = lastYearSaleList.get(j);
				if((newSale.getSysId().equals(oldSale.getSysId())) && (newSale.getStoreCode().equals(oldSale.getStoreCode()))) {					
					saleSet.add(newSale);
				}
			}
		}
		//可比门店数量
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			int num = 0;
			for(Sale sale : saleSet) {
				if(CommonUtil.isNotBlank(regionDataMap.get("storeCode")) && CommonUtil.isNotBlank(sale.getStoreCode())) {
					if(sale.getStoreCode().equals(regionDataMap.get("storeCode"))) {
						regionDataMap.put("differNum", num++);
					}
				}
			}
		}
		logger.info("-->>通过可比门店<<--");
		Date yesterday = DateUtil.addDate(DateUtil.getDateFromString(saleDate, DateUtil.DATE_PATTERN.YYYY_MM_DD), Calendar.DATE, -1);
		//去年前一天销售
		Date lastYearYesterday = DateUtil.addDate(yesterday, Calendar.YEAR, -1);
		lastYearSaleList.clear();
		params.clear();
		params.put("provinceArea", provinceArea);
		params.put("saleDate", DateUtil.format(lastYearYesterday, DateUtil.DATE_PATTERN.YYYY_MM_DD));
		lastYearSaleList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, params);
		//去除不同的门店销售
		for(Sale sale : saleSet) {
			for(int i = 0; i < lastYearSaleList.size(); i++) {
				if(!sale.getStoreCode().equals(lastYearSaleList.get(i).getStoreCode())) {
					lastYearSaleList.remove(i);
				}
			}
		}
		//可比门店昨日销售
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for(Sale sale : lastYearSaleList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("storeCode")) && CommonUtil.isNotBlank(sale.getStoreCode())) {
					if(sale.getStoreCode().equals(regionDataMap.get("storeCode"))) {
						regionDataMap.put("differYesterDaySale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比昨日销售<<--");
		List<Sale> monthDataList = queryListByObject(QueryId.QUERY_STORE_BY_SALE_DATE, dateParams);
		//logger.info(">>>可比本月数据: {}<<<", JsonUtil.toJson(monthDataList));
		LinkedList<Sale> monthDataLinkList = new LinkedList<>();
		monthDataLinkList.addAll(monthDataList);
		//去除不同的门店销售
		for(Sale sale : saleSet) {
			for(int i = 0, size = monthDataLinkList.size(); i < size; i++) {
				if(!sale.getStoreCode().equals(monthDataLinkList.get(i).getStoreCode())) {
					monthDataLinkList.remove(i);
				}
			}
		}
		monthDataList.clear();
		monthDataList.addAll(monthDataLinkList);
		//可比本月销售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : monthDataList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("storeCode")) && CommonUtil.isNotBlank(sale.getStoreCode())) {
					if (sale.getStoreCode().equals(regionDataMap.get("storeCode"))) {
						regionDataMap.put("differMonthSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比本月销售<<--");
		//去除不同的门店销售
		LinkedList<Sale> lastMonthDataDiffLinkList = new LinkedList<>();
		lastMonthDataDiffLinkList.addAll(lastMonthDataDiffList);
		for(Sale sale : saleSet) {
			for(int i = 0, size = lastMonthDataDiffLinkList.size(); i < size; i++) {
				if(!sale.getStoreCode().equals(lastMonthDataDiffLinkList.get(i).getStoreCode())) {
					lastMonthDataDiffLinkList.remove(i);
				}
			}
		}
		lastMonthDataDiffList.clear();
		lastMonthDataDiffList.addAll(lastMonthDataDiffLinkList);
		//可比上月同期銷售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : lastMonthDataDiffList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("storeCode")) && CommonUtil.isNotBlank(sale.getStoreCode())) {
					if (sale.getStoreCode().equals(regionDataMap.get("storeCode"))) {
						regionDataMap.put("differLastMonthDateSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比上月同期銷售<<--");
		//去除不同的门店销售
		LinkedList<Sale> lastYearDataDiffLinkList = new LinkedList<>();
		lastYearDataDiffLinkList.addAll(lastYearDataDiffList);
		for (Sale sale : saleSet) {
			for (int i = 0, size = lastYearDataDiffLinkList.size(); i < size; i++) {
				if (!sale.getStoreCode().equals(lastYearDataDiffLinkList.get(i).getStoreCode())) {
					lastYearDataDiffLinkList.remove(i);
				}
			}
		}
		lastYearDataDiffList.clear();
		lastYearDataDiffList.addAll(lastYearDataDiffLinkList);
		//可比去年同期銷售
		for (int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			double differSale = 0;
			for (Sale sale : lastYearDataDiffList) {
				if(CommonUtil.isNotBlank(saleSet) && CommonUtil.isNotBlank(regionDataMap.get("storeCode")) && CommonUtil.isNotBlank(sale.getStoreCode())) {
					if (sale.getStoreCode().equals(regionDataMap.get("storeCode"))) {
						regionDataMap.put("differLastYearDateSale", differSale++);
					}
				}
			}
		}
		logger.info("-->>通过可比去年同期銷售 <<--");
		for(int i = 0; i < regionDataList.size(); i++) {
			Map<String, Object> regionDataMap = regionDataList.get(i);
			//可比月銷售
			double differMonthSale = CommonUtil.isBlank(regionDataMap.get("differMonthSale")) ? 0.0 : (double) regionDataMap.get("differMonthSale");
			//可比上月銷售
			double differLastMonthDateSale = CommonUtil.isBlank(regionDataMap.get("differLastMonthDateSale")) ? 0.0 : (double) regionDataMap.get("differLastMonthDateSale");
			//可比去年銷售
			double differLastYearDateSale = CommonUtil.isBlank(regionDataMap.get("differLastYearDateSale")) ? 0.0 : (double) regionDataMap.get("differLastYearDateSale");
			if(differMonthSale != 0) {
				//可比环比
				regionDataMap.put("differChaimIndex", differLastMonthDateSale / differMonthSale);
				//可比同比
				regionDataMap.put("differYearBasis", differLastYearDateSale / differMonthSale);
			} else {
				regionDataMap.put("differChaimIndex", 0.0);
				regionDataMap.put("differYearBasis", 0.0);
			}
			
		}
		logger.info("--->>>组装数据完成<<<---");
		String title = "省区1直营KA门店日报表";
		String[] header = new String[] {"省区", "门店", "门店数量", "本月销售目标", "昨日销售", "本月销售", 
				"昨日销售占比(%)", "本月达成率", "上月同期销售金额", "去年同月同期销售金额", "环比(%)", "同比(%)", 
				"可比门店数量", "可比门店昨日销售", "可比门店本月", "可比门店上月", "可比门店去年", "可比门店环比(%)",
				"可比门店同比(%)"};
		List<String> codeList = codeDictService
				.queryCodeListByServiceCode(CodeEnum.CODE_DICT_REGION_STORE_CODE_REPORT.value());
		String fileName = "按区域 区域表三级报表-" + DateUtil.getCurrentDateStr();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
		ExcelUtil excelUtil = new ExcelUtil();
		excelUtil.exportTemplateByMap(header, regionDataList, title, codeList, response.getOutputStream());
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil uploadSaleData(MultipartFile file) throws Exception {
		ExcelUtil<Sale> excelUtil = new ExcelUtil<>();
		List<Map<String, Object>> saleMapList = excelUtil.getExcelList(file, ExcelEnum.SALE_TEMPLATE_TYPE.value());
		if (saleMapList == null) {
			return ResultUtil.error("格式不符，导入失败");
		}
		insert(InsertId.INSERT_BATCH_SALE, saleMapList);
		return ResultUtil.success();
	}
	
}
