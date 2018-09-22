package com.data.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.service.IRedisService;
import com.data.service.ISaleService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.DateUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.data.utils.TemplateDataUtil;
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

	@Override
	public ResultUtil getSaleByWeb(CommonDTO common, String sysId, Integer page, Integer limit) throws IOException{
		PageRecord<Sale> pageRecord = null;
		logger.info("------>>>>>>开始抓取销售数据<<<<<<---------");
		
		// 抓取数据
		List<Sale> saleList = dataCaptureUtil.getDataByWeb(common, sysId, WebConstant.SALE, Sale.class);

		
		logger.info("------>>>>>>结束抓取销售数据<<<<<<---------");
		for (int i = 0, size = saleList.size(); i < size; i++) {
			Sale sale = saleList.get(i);
			sale.setSysId(sysId);
			
			// 单品编码
			String simpleCode = sale.getSimpleCode();
			
			// 门店编码
			String storeCode = sale.getStoreCode();
			
			// 地区
			String localName = sale.getLocalName();
			
			// 系统
			String sysName = sale.getSysName();			
			
			TemplateStore store = templateDataUtil.getStandardStoreMessage(sysId, storeCode);
			
			// 单品条码
			String simpleBarCode = sale.getSimpleBarCode();
			if (CommonUtil.isBlank(simpleBarCode)) {
				simpleBarCode = templateDataUtil.getBarCodeMessage(sysName, simpleCode);
			}
			if (CommonUtil.isBlank(simpleBarCode)) {
				sale.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
				continue;
			}
			
			sysName = CommonUtil.isBlank(localName) ? sysName : (localName + sysName);
			
			sale.setSysName(sysName);
			
			sale.setSimpleBarCode(simpleBarCode);
			TemplateProduct product = templateDataUtil.getStandardProductMessage(sysId, simpleBarCode);
			
			// 门店信息为空
			if (CommonUtil.isBlank(store)) {
				sale.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
			} else {
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
			}
			
			// 单品信息为空
			if (CommonUtil.isBlank(product)) {
				sale.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				continue;
			} else {
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

		}
		
		// 数据插入数据库
		dataCaptureUtil.insertData(saleList, InsertId.INSERT_BATCH_SALE);
		pageRecord = dataCaptureUtil.setPageRecord(saleList, page, limit);
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
			// 默认查询当天数据
			map.put("startDate", DateUtil.getDate());
			map.put("endDate", DateUtil.getDate());
		}
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
	

	@Override
	public ResultUtil excel(String system, String region, String province, String store, HttpServletResponse response) {
		Map<String, Object> params = new HashMap<>(8);
		params.put("system", system);
		params.put("region", region);
		params.put("province", province);
		params.put("store", store);
		//前一天的数据
		params.put("saleDate", DateUtil.getCustomDate(-1));
		//先判断数量 分批导出
		int count = queryCountByObject(QueryId.QUERY_COUNT_SALE_LIST_REPORT, params);
		if(count >= CommonValue.MAX_ROW_COUNT_2007) {
			return ResultUtil.error("下载超过excel2007最大行数");
		} else {
			int pageSize = (int) Math.ceil((double) count / 3);
			ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			for(int i = 1; i <= 3; i++) {
				//分三次
				try {
					params.put("pageNum", i);
					params.put("pageSize", pageSize);
					
					//查询导出数据集合
					executorService.execute(new Thread() {
						@Override
						public void run() {
							List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_LIST_REPORT, params);
							if(CommonUtil.isBlank(saleList)) {
								logger.error("--->>>导出销售数据异常<<<---");
								return;
							}
							buildDailyStoreSale(saleList);
							
							List<String> daysList = DateUtil.getMonthDays(DateUtil.getSystemDate());
							String dateStr = DateUtil.getCurrentDateStr();
							
							List<Map<String, Object>> saleDataList = new ArrayList<>(10);
							//日门店销售额集合
							List<Map<String, Object>> storeDailyList = new ArrayList<>(10);
							//取得系统当前时间在日期集合中的索引
							int index = daysList.indexOf(dateStr);
							for(int i = 0; i < index + 1; i++) {
								//将之前天数的数据都进行组装
								//到缓存查是否数据， 然后放置在saleDatePriceMap中
								storeDailyList = redisService.querySaleDateMessageByStore(daysList.get(i));
								for(Map<String, Object> store : storeDailyList) {
									String storeCode = (String) store.get("storeCode");
									//根据storeCode从缓存里面取得门店信息
									Map<String, Object> saleInfo = redisService.querySaleInfo(storeCode);
									//在门店信息里面添加日销售额
									saleInfo.put(daysList.get(i), store.get("salePrice"));
									//将信息放入在saleDataList集合里面
									saleDataList.add(saleInfo);
								}
							}
							//写入单元格
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("--->>>销售日报表导出异常<<<---");
				} finally {
					if(executorService != null) {
						executorService.shutdown();
					}
				}
			}
		}
		return ResultUtil.success();
	}
	
	/**
	 * 组装门店与销售额的集合
	 * @param saleList
	 * @return
	 */
	private Map<String, Object> buildDailyReportByStore(List<Sale> saleList) {
		List<Sale> list = saleList;
		Set<String> storeSet = new HashSet<>();
		for(Sale sale : list) {
			storeSet.add(sale.getStoreCode());
		}
		Map<String, Object> map = new HashMap<>();
		String storeCode;
		for(int i = 0; i < list.size(); i++) {
			//统计同一个门店的销售额
			storeCode = list.get(i).getStoreCode();
			if(storeSet.contains(storeCode)) {
				Double price = (Double) map.get(storeCode);
				if(price == null) {
					price = 0.0;
				}
				map.put(storeCode, price += list.get(i).getSellPrice());
			}
		}
		return map;
	}
	
	/***
	 * 门店一天的销售额
	 * 组装当天数据 然后放入缓存中
	 * @param saleList
	 * @return
	 */
	private void buildDailyStoreSale(List<Sale> saleList) {
		Map<String, Object> salePriceMap = buildDailyReportByStore(saleList);
		String dateStr = DateUtil.getCurrentDateStr();
		List<Map<String, Object>> storeDailySaleList = new ArrayList<>(10);
		//从缓存里面取得销售表中各个门店信息
		List<Map<String, Object>> saleMessageList = redisService.querySaleList();
		for(Map.Entry<String, Object> entry : salePriceMap.entrySet()) {
			for(Map<String, Object> map : saleMessageList) {
				String storeCode = (String) map.get("storeCode");
				if(storeCode.equals(entry.getKey())) {
					Map<String, Object> params = new HashMap<>(10);
//					params.put("sysId", map.get("sysId"));
//					params.put("sysName", map.get("sysName"));
//					params.put("region", map.get("region"));
//					params.put("provinceArea", map.get("provinceArea"));
					params.put("storeCode", map.get("storeCode"));
//					params.put("storeName", map.get("storeName"));
//					params.put("storeManager", map.get("storeManager"));
					//当天的数据
					params.put("salePrice", entry.getValue());
					storeDailySaleList.add(params);
				}
				break;
			}
		}
		//将一天的门店信息存入缓存
		redisService.setSaleDailyMessageByStore(dateStr, storeDailySaleList);
	}
	
//	public static void main(String[] args) {
//		String json = null;
//		try {
//			json = FileUtils.readFileToString(new File("E:\\baiya\\sale\\sale.txt"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		List<Sale> list = (List<Sale>) FastJsonUtil.jsonToList(json, Sale.class);
//		list.subList((CommonValue.PAGE - 1)*CommonValue.SIZE, list.size());
//		System.err.println(FastJsonUtil.objectToString(list));
//	}
	
}
