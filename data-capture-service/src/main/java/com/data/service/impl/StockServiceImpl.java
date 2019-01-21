package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.data.bean.DataLog;
import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.bean.TemplateSupply;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.CodeEnum;
import com.data.constant.enums.ExcelEnum;
import com.data.constant.enums.StockEnum;
import com.data.constant.enums.TipsEnum;
import com.data.exception.DataException;
import com.data.exception.GlobalException;
import com.data.model.ProvinceAreaStockModel;
import com.data.model.RegionStockModel;
import com.data.model.StoreStockModel;
import com.data.model.SysStockModel;
import com.data.service.IDataService;
import com.data.service.IRedisService;
import com.data.service.IStockService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.DateUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.ExportUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;
import com.data.utils.StockDataUtil;
import com.data.utils.TemplateDataUtil;
import com.google.common.collect.Maps;

@Service
public class StockServiceImpl extends CommonServiceImpl implements IStockService{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DataCaptureUtil dataCaptureUtil;
	
	@Autowired
	private TemplateDataUtil templateDataUtil;
	
	@Autowired
	private StockDataUtil stockDataUtil;
	
	@Autowired
	private IRedisService redisService;
	
	@Autowired
	private ExportUtil exportUtil;
	
	@Autowired
	private IDataService dataService;
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultUtil getStockByWeb(Integer id, Integer limit) throws Exception {
		logger.info("------>>>>>>开始抓取库存数据<<<<<<---------");
		logger.info("------>>>>>>前端传递Id:{}<<<<<<<-------", id);
		
		PageRecord<Stock> pageRecord = null;
		
		// 同步
		synchronized(id) {
			logger.info("------>>>>>进入抓取库存同步代码块<<<<<-------");
			
			Map<String, Object> queryParam = new HashMap<>(1);
			queryParam.put("id", id);	
			TemplateSupply supply = (TemplateSupply)queryObjectByParameter(QueryId.QUERY_SUPPLY_BY_CONDITION, queryParam);
			if (supply == null) {
				return ResultUtil.error("供应链未找到");
			}	
			String sysId = supply.getSysId();
			
			List<Stock> stockList = null;
			String stockStr = null;
			//boolean flag = true;
			
			/*while (flag) {
				try {*/
					// 抓取数据
					stockStr = dataCaptureUtil.getDataByWeb("1900-01-01", supply, WebConstant.STOCK);
					/*if (stockStr != null) {
						flag = false;
						logger.info("------>>>>>>结束抓取库存数据<<<<<<---------");
					}
				} catch (DataException e) {
					return ResultUtil.error(e.getMessage());
				} catch (Exception e) {
					flag = true;
				}
			}*/

			stockList = (List<Stock>) FastJsonUtil.jsonToList(stockStr, Stock.class);
			
			if (CollectionUtils.isEmpty(stockList)) {
				pageRecord = dataCaptureUtil.setPageRecord(stockList, limit);
				return ResultUtil.success(pageRecord);
			}
	
			mateData(supply, stockList);
			
			logger.info("---->>>开始删除库存数据<<<------");
			delete(DeleteId.DELETE_STOCK_BY_SYS_ID, sysId);
			
			logger.info("---->>>开始插入库存数据<<<-----");
			insert(InsertId.INSERT_STOCK_BATCH, stockList);
			
			pageRecord = dataCaptureUtil.setPageRecord(stockList, limit);
		}
		return ResultUtil.success(pageRecord);
	}
	/**
	 * 匹配数据
	 * @param supply
	 * @param stockList
	 * @throws Exception 
	 */
	private void mateData(TemplateSupply supply, List<Stock> stockList) throws Exception {
		List<TemplateStore> storeList = redisService.queryTemplateStoreList();
		List<TemplateProduct> productList = redisService.queryTemplateProductList();
		String sysId = supply.getSysId();
		Date now = DateUtil.getSystemDate();
		for (int i = 0, size = stockList.size(); i < size; i++) {
			Stock stock = stockList.get(i);
			
			String region = supply.getRegion();
			
			// 单品编号
			String simpleCode = stock.getSimpleCode();

			// 系统名称
			String sysName = supply.getSysName();

			// 门店编号
			String storeCode = stock.getStoreCode();

			// 商品条码
			String simpleBarCode = stock.getSimpleBarCode();
			
			stock.setSysId(sysId);
			stock.setCreateTime(now);
			sysName = supply.getRegion() + sysName;
			stock.setSysName(sysName);
			stock.setStatus(1);
			
			Double stockPrice = stock.getStockPrice();
			Integer stockNum = stock.getStockNum();
			if (stockPrice != null && stockNum != null) {
				stock.setTaxCostPrice(stock.getStockPrice() / stock.getStockNum());
			}

			// 标准条码匹配信息
			if (StringUtils.isBlank(simpleBarCode)) {				
				try {
					simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
					if (CommonUtil.isBlank(simpleBarCode)) {

						// 错误日志
						DataLog log = new DataLog();
						log.setRegion(region);
						log.setLogDate(now);
						log.setSysId(sysId);
						log.setSysName(sysName);
						log.setLogRemark("编码" + simpleCode + "商品" + TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						insert(InsertId.INSERT_DATA_LOG, log);
						stock.setStatus(0);
						// order.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
						continue;
					}
				} catch (GlobalException e) {
					// 错误日志
					DataLog log = new DataLog();
					log.setRegion(region);
					log.setLogDate(now);
					log.setSysId(sysId);
					log.setSysName(sysName);
					log.setLogRemark(e.getErrorMsg());
					insert(InsertId.INSERT_DATA_LOG, log);
					stock.setStatus(0);
					continue;
				}
			}
			stock.setSimpleBarCode(simpleBarCode);

			// 标准单品信息
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
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(now);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码" + simpleBarCode + "商品" + TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				stock.setStatus(0);
				//stock.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
			} else {
				// 大区
				stock.setRegion(store.getRegion());

				// 省区
				stock.setProvinceArea(store.getProvinceArea());

				// 门店名称
				stock.setStoreName(store.getStandardStoreName());

				// 归属
				stock.setAscription(store.getAscription());

				// 业绩归属
				stock.setAscriptionSole(store.getAscriptionSole());
			}

			// 单品信息为空
			if (CommonUtil.isBlank(product)) {
				// 错误日志
				DataLog log = new DataLog();
				log.setRegion(region);
				log.setLogDate(now);
				log.setSysId(sysId);
				log.setSysName(sysName);
				log.setLogRemark("条码" + simpleBarCode + "商品" + TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				insert(InsertId.INSERT_DATA_LOG, log);
				stock.setStatus(0);
				//stock.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				//continue;
			} else {
				// 单品名称
				stock.setSimpleName(product.getStandardName());

				// 品牌
				stock.setBrand(product.getBrand());

				// 系列
				stock.setSeries(product.getSeries());

				// 材质
				stock.setMaterial(product.getMaterial());

				// 片数
				stock.setPiecesNum(product.getPiecesNum());

				// 日夜
				stock.setDayNight(product.getFunc());

				// 货号
				stock.setStockNo(product.getStockNo());

				// 箱规
				stock.setBoxStandard(product.getBoxStandard());

				// 库存编号
				stock.setStockCode(product.getStockCode());

				// 类别
				stock.setClassify(product.getClassify());

				
			}
		}
	}
	@Override
	public ResultUtil getStockByParam(Stock stock, Integer page, Integer limit) throws Exception {

		Map<String, Object> map = Maps.newHashMap();
		logger.info("--------->>>>>>>>stock:" + FastJsonUtil.objectToString(stock) + "<<<<<<<<----------");
		if (null != stock) {
			
			// 门店编号
			if (StringUtils.isNoneBlank(stock.getStoreCode())) {
				map.put("storeCode", stock.getStoreCode());
			}
			 
			// 区域
			if (StringUtils.isNoneBlank(stock.getRegion())) {
				map.put("region", stock.getRegion());
			}
			
			// 品牌
			if (StringUtils.isNoneBlank(stock.getBrand())) {
				map.put("brand", stock.getBrand());
			}
			
			// 系统ID
			if (StringUtils.isNoneBlank(stock.getSysId())) {
				map.put("sysId", stock.getSysId());
			}
			
			// 省区
			if (StringUtils.isNoneBlank(stock.getProvinceArea())) {
				map.put("provinceArea", stock.getProvinceArea());
			}

		}
		PageRecord<Stock> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_STOCK_BY_PARAM, QueryId.QUERY_STOCK_BY_PARAM, map, page, limit);
		return ResultUtil.success(pageRecord);
	}

	/**
	 * 当前库存是否存在
	 * @param param
	 * @param output
	 * @throws IOException
	 */
	public void checkNowStock(Map<String, Object> param, OutputStream output) throws IOException {
		int count = queryCountByObject(QueryId.QUERY_COUNT_STOCK_BY_PARAM, param);
		if (count == 0) {
			XSSFWorkbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue(TipsEnum.NOW_STOCK_IS_NULL.getValue());
			wb.write(output);
			output.flush();
			output.close();
			throw new DataException("541");
		}
		
	}
	/**
	 * 检查门店编号
	 * @param storeCode
	 * @param output
	 * @throws IOException
	 */
	public void checkStoreCode(String storeName, OutputStream output) throws IOException {
		if (CommonUtil.isBlank(storeName)) {
			XSSFWorkbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue(TipsEnum.STORE_NAME_IS_NULL.getValue());
			wb.write(output);
			output.flush();
			output.close();
			throw new DataException("544");
		}
	}
	/**
	 * 检查系统编号
	 * @param storeCode
	 * @param output
	 * @throws IOException
	 */
	public void checkSysId(String sysId, OutputStream output) throws IOException {
		if (CommonUtil.isBlank(sysId)) {
			XSSFWorkbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue(TipsEnum.SYS_ID_IS_NULL.getValue());
			wb.write(output);
			output.flush();
			output.close();
			throw new DataException("537");
		}
	}
	/**
	 * 检查大区
	 * @param storeCode
	 * @param output
	 * @throws IOException
	 */
	public void checkRegion(String region, OutputStream output) throws IOException {
		if (CommonUtil.isBlank(region)) {
			XSSFWorkbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue(TipsEnum.REGION_IS_NULL.getValue());
			wb.write(output);
			output.flush();
			output.close();
			throw new DataException("539");
		}
	}
	@Override
	public void exportStoreProductExcel(String storeName, OutputStream output) throws IOException {
		logger.info("-------->>>>>>>>>storeName:{}<<<<<<<<---------", storeName);
		
		checkStoreCode(storeName, output);
		
		// 当天的库存数据
		Map<String, Object> param = new HashMap<>(4);
		String now = LocalDate.now().toString();
		param.put("queryDate", now);
		checkNowStock(param, output);
		
		param.put("storeName", storeName);
		param.put("column", " simple_bar_code, simple_name, stock_price, simple_code, stock_num ");
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_ANY_COLUMN, param);

		String queryDate = LocalDate.now().toString();
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("门店单品表");
		Row firstRow = sheet.createRow(0);
		
		stockDataUtil.createDateRow(wb, firstRow, "日期", queryDate);
		
		Row title = sheet.createRow(2);
		
		// 表头
		String[] header = new String[]{"门店", "单品名称", "单品编码", "单品条码", "前一周销售数量", "库存数量", "库存天数"};		
		excelUtil.createRow(title, header, true);
		
		// 前一天
		LocalDate lastDay = LocalDate.parse(queryDate).minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("storeName", storeName);
		param.put("column", " simple_bar_code, sell_num ");
		List<Sale> saleDayList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		if (CommonUtil.isBlank(saleDayList)) {
			/*firstRow.createCell(0).setCellValue(TipsEnum.LAST_DAY_SALE_IS_NULL.getValue());
			wb.write(output);
			output.flush();
			output.close();
			throw new DataException("542");*/
			saleDayList = Collections.EMPTY_LIST;
			Row row = sheet.getRow(2);
			row.createCell(header.length).setCellValue("该门店前一天单品销售数量");
		}
		
		// 上周一
		LocalDate lastMonday = LocalDate.parse(queryDate).minusWeeks(1L).with(DayOfWeek.MONDAY);
					
		// 上周末
		LocalDate lastSunday = lastMonday.with(DayOfWeek.SUNDAY);
		
		// 查询上周销量
		param.clear();
		param.put("startDate", lastMonday.toString());
		param.put("endDate", lastSunday.toString());
		param.put("storeName", storeName);
		param.put("column", " simple_bar_code, sell_num ");
		List<Sale> saleWeekList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		if (CommonUtil.isBlank(saleDayList)) {
			saleWeekList = Collections.EMPTY_LIST;
		}
		
		int rowIndex = 3;
		// 填写数据
		for (int i = 0, size = stockList.size(); i < size; i++) {
			Stock stock = stockList.get(i);
			Row row = sheet.createRow(rowIndex);

			if (CommonUtil.isBlank(saleDayList)) {
				row.createCell(header.length).setCellValue(0);
			}
			// 上周该单品的销售数量
			int sumSaleNumByWeek = 0; 
			
			// 如果上周存在销售记录，否则默认为0
			if (CommonUtil.isNotBlank(saleWeekList)) {
				Sale sale = null;
				for (int j = 0, weekSize = saleWeekList.size(); j < weekSize; j++) {
					sale = saleWeekList.get(j);
					if (sale.getSimpleBarCode().equals(stock.getSimpleBarCode())) {
						sumSaleNumByWeek += (sale.getSellNum() == null) ? 0 : sale.getSellNum();
					}
				}
			}

			// 行值
			String[] cellValue = new String[]{
					storeName,
					stock.getSimpleName(),
					stock.getSimpleCode(),
					stock.getSimpleBarCode(),
					String.valueOf(sumSaleNumByWeek),
					String.valueOf(CommonUtil.toIntOrZero(stock.getStockNum()))
			};
			excelUtil.createRow(row, cellValue, false);
			
			// 库存金额
			double stockPrice = CommonUtil.toDoubleOrZero(stock.getStockPrice());
					
			// 前一天单品的销售总量
			int sumSaleNumByDay = 0;
						
			// 如果前一天存在销售记录，否则默认为0
			if (CommonUtil.isNotBlank(saleDayList)) {
				Sale sale = null;
				for (int j = 0, saleSize = saleDayList.size(); j < saleSize; j++) {
					sale = saleDayList.get(j);
					if (sale.getSimpleBarCode().equals(stock.getSimpleBarCode())) {
						sumSaleNumByDay += (sale.getSellNum()==null) ? 0 : sale.getSellNum();
					}
				}
			}

			// 库存金额
			stockPrice = CommonUtil.toDoubleOrZero(stockPrice);
						
			// 库存天数
			double stockDayNum = 0;
			if (sumSaleNumByDay != 0) {
				stockDayNum = CommonUtil.setScale("0.00", stockPrice / sumSaleNumByDay);
			}	
			
			
			CellStyle cellStyle = null;
			
			// 库存天数小于3 ，单元格黄底
			if (stockDayNum < 3 && stockDayNum > 0) {
				cellStyle = excelUtil.getCellStyle(wb, IndexedColors.YELLOW.index);
			} else if (stockDayNum <= 0) { // 库存天数等于0，单元格红底，字体白色
				// 红底
				cellStyle = excelUtil.getCellStyle(wb, IndexedColors.RED.index);
				
				// 字体样式
				Font font = excelUtil.getColorFont(wb, IndexedColors.WHITE.index); 
				cellStyle.setFont(font);
			}
			
			Cell stockDayNumCell = row.createCell(cellValue.length);
			stockDayNumCell.setCellValue(stockDayNum);
			if (null != cellStyle) {
				stockDayNumCell.setCellStyle(cellStyle);
			}
			rowIndex++;
		}
		wb.write(output);
		output.flush();
		output.close();
	}
	
	@Override
	public void exportSysStoreExcel(String sysId, OutputStream output) throws IOException {
		logger.info("------>>>>>>>>sysId:{}<<<<<<<-------", sysId);
		checkSysId(sysId, output);
		
		String queryDate = LocalDate.now().toString();
		// 按系统名称和时间查询库存
		Map<String, Object> param = new HashMap<>(3);
		param.put("sysId", sysId);
		param.put("queryDate", queryDate);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);
	
		List<StoreStockModel> storeStockModelList = queryListByObject(QueryId.QUERY_STORE_STOCK_MODEL_BY_PARAM, param);
		String sysName = storeStockModelList.get(0).getStockList().get(0).getSysName();
		
		// 标题
		String title = sysName + "系统直营KA门店缺货日报表";
			
		// 表头
		String[] header = new String[]{"系统", "门店编号", "门店", "单品数量", "库存低于3天的单品", "库存等于0的单品数量"};
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("系统门店表");
		Row dateRow = sheet.createRow(0);
		
		// 生成日期
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);
				
		// 生成粗体居中标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, header.length);
		
		// 前一天销售数据
		LocalDate lastDay = LocalDate.parse(queryDate).minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " sell_num, simple_bar_code, store_name, store_code ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		if (CommonUtil.isBlank(saleList)) {
			/*
			 * firstRow.createCell(0).setCellValue(TipsEnum.
			 * LAST_DAY_SALE_IS_NULL.getValue()); wb.write(output);
			 * output.flush(); output.close(); throw new DataException("542");
			 */
			saleList = Collections.EMPTY_LIST;
			Row row = sheet.getRow(2);
			row.createCell(header.length).setCellValue("该系统昨日销售数量为0");
		}
		
		exportXStoreExcel(storeStockModelList, saleList, header, sysName, wb, sheet, excelUtil, output);
	}
	
	@Override
	public void exportRegionStoreExcel(String region, OutputStream output) throws IOException {
		logger.info("------->>>>>>>region:{}<<<<<<--------", region);
		checkRegion(region, output);
		
		String queryDate = LocalDate.now().toString();
		
		// 按系统名称和时间查询库存
		Map<String, Object> param = new HashMap<>(3);
		param.put("queryDate", queryDate);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);
		
		param.put("region", region);
		List<StoreStockModel> storeStockModelList = queryListByObject(QueryId.QUERY_STORE_STOCK_MODEL_BY_PARAM, param);
		
		// 标题
		String title = region + "大区直营KA门店缺货日报表";
			
		// 表头
		String[] header = new String[]{"大区", "门店编号", "门店", "单品数量", "库存低于3天的单品", "库存等于0的单品数量"};
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("区域门店表");
		
		Row dateRow = sheet.createRow(0);
		
		// 生成日期
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);
				
		// 生成粗体居中标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, header.length);

		// 前一天销售数据
		LocalDate lastDay = LocalDate.parse(queryDate).minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " sell_num, simple_bar_code, store_name, store_code ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		if (CommonUtil.isBlank(saleList)) {
			/*
			 * firstRow.createCell(0).setCellValue(TipsEnum.
			 * LAST_DAY_SALE_IS_NULL.getValue()); wb.write(output);
			 * output.flush(); output.close(); throw new DataException("542");
			 */
			saleList = Collections.EMPTY_LIST;
			Row row = sheet.getRow(2);
			row.createCell(header.length).setCellValue("该大区昨日销售数量为0");
		}
		
		exportXStoreExcel(storeStockModelList, saleList, header, region, wb, sheet, excelUtil, output);
	}
	/**
	 * 导出区域/系统缺货日报表
	 * @param queryDate
	 * @param sheetName
	 * @param title
	 * @param name
	 * @param header
	 * @param storeCodeModelList
	 * @param output
	 * @throws IOException
	 */
	public void exportXStoreExcel(List<StoreStockModel> storeStockModelList, List<Sale> saleList, String[] header, String name,
			Workbook wb, Sheet sheet, ExcelUtil<Stock> excelUtil, OutputStream output)
			throws IOException {
		// 设置表头
		Row headerRow = sheet.createRow(3);
		excelUtil.createRow(headerRow, header, true);

		if (CommonUtil.isBlank(storeStockModelList)) {
			wb.write(output);
			output.flush();
			output.close();
		}

		// 生成表下部分数据
		StoreStockModel storeCodeModel = null;
		String storeName = null;
		String storeCode = null;
		int rowIndex = 4;
		int i, j, k;
		int size, saleSize, stockSize;
		List<Stock> stockList = null;
		Row row = null;
		Stock stock = null;
		Sale tempSale = null;

		for (i = 0, size = storeStockModelList.size(); i < size; i++) {
			row = sheet.createRow(rowIndex);
			storeCodeModel = storeStockModelList.get(i);
			storeName = storeCodeModel.getStoreName() == null ? "" : storeCodeModel.getStoreName();
			storeCode = storeCodeModel.getStoreCode();
			stockList = storeCodeModel.getStockList();

			// 库存天数低于三天的单品数量
			int sumThreeStockDay = 0;

			// 库存天数等于0的单品数量
			int sumZeroStockDay = 0;

			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				double stockPrice = stock.getStockPrice() == null ? 0 : stock.getStockPrice();

				// 单品销售总数
				int saleNumSum = 0;

				// 遍历销售，找出门店编号和单品条码匹配的记录
				for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
					tempSale = saleList.get(k);
					if (tempSale.getStoreCode().equals(storeCode) && storeName.equals(tempSale.getStoreName())
							&& tempSale.getSimpleBarCode().equals(stock.getSimpleBarCode())) {
						saleNumSum += tempSale.getSellNum() == null ? 0 : tempSale.getSellNum();
					}
				}
				// 库存天数
				double stockDayNum = 0;
				if (saleNumSum != 0) {
					stockDayNum = CommonUtil.setScale("0.00", stockPrice / saleNumSum);
				}
				if (stockDayNum < 3 && stockDayNum > 0) {
					sumThreeStockDay++;
				} else if (stockDayNum <= 0) {
					sumZeroStockDay++;
				}
			}
			String[] cellValue = new String[] { 
					name, 
					storeCode, // 门店编号
					storeName, // 门店名称
					String.valueOf(stockList.size()), // 单品数量
					String.valueOf(sumThreeStockDay), // 库存天数低于三天的单品数量
					String.valueOf(sumZeroStockDay) // 库存天数低于三天的单品总库存数量
			};
			excelUtil.createRow(row, cellValue, false);
			rowIndex++;

		}
		wb.write(output);
		output.flush();
		output.close();
	}
	@Override
	public void exportMissFirstComExcel(OutputStream output) throws Exception {
		// 检查当天是否有符合条件的库存
		Map<String, Object> param = new HashMap<>(2);
		
		// 生成日期
		String queryDate = LocalDate.now().toString();
		param.put("queryDate", queryDate);
		checkNowStock(param, output);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		XSSFWorkbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("公司一级表");
		Row dateRow = sheet.createRow(0);
		
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);
		
		// 标题
		int rowIndex = 2;
		Row titleRow = sheet.createRow(rowIndex);
		
		// 系统缺货日报表表头
		String[] sysHeader = new String[]{"系统", "门店数量", "库存低于3天的门店", "单品数量", "库存低于1天的单品数量"}; 
		
		// 生成粗体居中标题
		CellStyle cellStyle = excelUtil.getBolderTitle(wb);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, sysHeader.length - 1));
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("系统直营KA分大区门店缺货日报表");
		titleCell.setCellStyle(cellStyle);
		
		// 大区缺货日报表表头
		String[] regionHeader = new String[]{"大区", "门店数量", "库存低于3天的门店", "单品数量", "库存低于1天的单品数量"}; 
				
		// 生成粗体居中标题
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, sysHeader.length, sysHeader.length + regionHeader.length - 1));
		titleCell = titleRow.createCell(sysHeader.length);
		titleCell.setCellValue("大区直营KA分大区门店缺货日报表");
		titleCell.setCellStyle(cellStyle);	
		
		// 表头
		Row headerRow = sheet.createRow(3);
		for (int i = 0, size = sysHeader.length; i < size; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(sysHeader[i]);
		}
		for (int i = 0, size = regionHeader.length; i < size; i++) {
			Cell cell = headerRow.createCell(i + sysHeader.length);
			cell.setCellValue(regionHeader[i]);
		}
		
		// 前一天的销售
		String lastDay = LocalDate.parse(queryDate).minusDays(1L).toString();
		param.put("queryDate", lastDay);
		param.put("column", " sys_id, region, sell_num, store_code, simple_bar_code ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		
		CountDownLatch latch = new CountDownLatch(2);
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			executorService.execute(new createSysMiss(sheet, saleList, excelUtil, latch));
			executorService.execute(new createRegionMiss(sheet, saleList, excelUtil, latch));
		} finally {
			if (executorService != null) {
				executorService.shutdown();
			}
		}
		latch.await();
		wb.write(output);
		output.flush();
		output.close();
		
	}
	public class createSysMiss implements Runnable {

		private Sheet sheet;
		private List<Sale> saleList;
		private CountDownLatch latch;
		private ExcelUtil<Stock> excelUtil;
		
		public createSysMiss(Sheet sheet, List<Sale> saleList, ExcelUtil<Stock> excelUtil, CountDownLatch latch) {
			this.sheet = sheet;
			this.saleList = saleList;
			this.excelUtil = excelUtil;
			this.latch = latch;
		}
		
		@Override
		public void run() {
			// 按系统分组
			List<SysStockModel> sysStockList = queryListByObject(QueryId.QUERY_SYS_STOCK_MODEL_BY_PARAM, null);
			SysStockModel sysStock = null;
			List<Stock> stockList = null;
			
			Stock stock = null;
			Sale sale = null;
			double sellNumSum = 0;
			
			// 每一个门店的库存金额
			Map<String, Double> storeMap = new HashMap<>();
			
			// 每一个单品的库存金额
			Map<String, Double> productMap = new HashMap<>();
			
			for (int i = 0, size = sysStockList.size(); i < size; i++) {
				Row row = sheet.getRow(i + 4);
				if (row == null) {
					row = sheet.createRow(i + 4);
				}
				sysStock = sysStockList.get(i);
				stockList = sysStock.getStockList();
			
				storeMap.clear();
				productMap.clear();
				for (int j = 0, stockSize = stockList.size(); j < stockSize; j++) {
					stock = stockList.get(j);
					String storeCode = stock.getStoreCode();
					Double storePrice = storeMap.get(storeCode);
					
					// 计算每一个门店的库存总金额
					if (null == storePrice) {
						storeMap.put(storeCode, stock.getStockPrice());
					} else {
						storeMap.put(storeCode, storePrice + stock.getStockPrice());
					}
					
					String simpleBarCode = stock.getSimpleBarCode();
					storePrice = productMap.get(simpleBarCode);
					
					// 计算每一个单品的库存总金额
					if (null == storePrice) {
						productMap.put(simpleBarCode, stock.getStockPrice());
					} else {
						productMap.put(simpleBarCode, storePrice + stock.getStockPrice());
					}
				}
				
				int storeStockDayNum = 0;
				
				for (Map.Entry<String, Double> map : storeMap.entrySet()) {
					String storeCode = map.getKey();
					double stockPrice = map.getValue();
					sellNumSum = 0;
					for (int k = 0, saleSize = saleList.size(); k < saleSize; k++) {
						sale = saleList.get(k);
						if (sysStock.getSysId().equals(sale.getSysId()) && storeCode.equals(sale.getStoreCode())) {
							sellNumSum += (sale.getSellNum() == null) ? 0 : sale.getSellNum();
						}
					}
					
					// 库存天数
					double stockDay = 0;
					if (sellNumSum != 0) {
						stockDay = stockPrice/sellNumSum;
					}
					
					if (stockDay < 3) {
						storeStockDayNum ++;
					}
				}
				
				int productStockDayNum = 0;
				for (Map.Entry<String, Double> map : productMap.entrySet()) {
					String simpleBarCode = map.getKey();
					double stockPrice = map.getValue();
					sellNumSum = 0;
					for (int k = 0, saleSize = saleList.size(); k < saleSize; k++) {
						sale = saleList.get(k);
						if (sysStock.getSysId().equals(sale.getSysId()) && simpleBarCode.equals(sale.getSimpleBarCode())) {
							sellNumSum += (sale.getSellNum() == null) ? 0 : sale.getSellNum();
						}
					}
					
					// 库存天数
					double stockDay = 0;
					if (sellNumSum != 0) {
						stockDay = stockPrice/sellNumSum;
					}
					
					if (stockDay < 1) {
						productStockDayNum ++;
					}
				}
				
				
				String[] rowValue = new String[]{
						sysStock.getSysName(), // 系统名
						String.valueOf(storeMap.size()), // 门店数量
						String.valueOf(storeStockDayNum), // 库存天数小于3的门店数量
						String.valueOf(productMap.size()), // 单品数量
						String.valueOf(productStockDayNum) // 库存天数小于1的单品数量
				};
				
				excelUtil.createRow(row, rowValue, false);
			}
			latch.countDown();
		}
		
	}
	public class createRegionMiss implements Runnable {

		private Sheet sheet;
		private List<Sale> saleList;
		private ExcelUtil<Stock> excelUtil;
		private CountDownLatch latch;
		
		public createRegionMiss(Sheet sheet, List<Sale> saleList, ExcelUtil<Stock> excelUtil, CountDownLatch latch) {
			this.sheet = sheet;
			this.saleList = saleList;
			this.excelUtil = excelUtil;
			this.latch = latch;
		}
		
		@Override
		public void run() {
			// 按大区分组
			List<RegionStockModel> regionStockList = queryListByObject(QueryId.QUERY_REGION_STOCK_MODEL_BY_PARAM, null);
			
			RegionStockModel regionStock = null;
			List<Stock> stockList = null;
			
			// 每一个门店的库存金额
			Map<String, Double> storeMap = new HashMap<>();
						
			// 每一个单品的库存金额
			Map<String, Double> productMap = new HashMap<>();
			
			Stock stock = null;
			
			Sale sale = null;
			
			double sellNumSum = 0;
			for (int i = 0, size = regionStockList.size(); i < size; i++) {
				regionStock = regionStockList.get(i);
				stockList = regionStock.getStockList();
				
				Row row = sheet.getRow(i + 4);
				if (row == null) {
					row = sheet.createRow(i + 4);
				}
				String region = regionStockList.get(i).getRegion() == null ? "" : regionStockList.get(i).getRegion();
				
				storeMap.clear();
				productMap.clear();
				for (int j = 0, stockSize = stockList.size(); j < stockSize; j++) {
					stock = stockList.get(j);
					String storeCode = stock.getStoreCode();
					String sysId = stock.getSysId();
					
					String storeKey = sysId + "-" + storeCode;
					Double storePrice = storeMap.get(storeKey);
					
					// 计算每一个门店的库存总金额
					if (null == storePrice) {
						storeMap.put(storeKey, stock.getStockPrice());
					} else {
						storeMap.put(storeKey, storePrice + stock.getStockPrice());
					}
					
					String simpleBarCode = stock.getSimpleBarCode();
					String simpleBarCodeKey = sysId + "-" + simpleBarCode;
					storePrice = productMap.get(simpleBarCodeKey);
					
					// 计算每一个单品的库存总金额
					if (null == storePrice) {
						productMap.put(simpleBarCodeKey, stock.getStockPrice());
					} else {
						productMap.put(simpleBarCodeKey, storePrice + stock.getStockPrice());
					}
				}
				
				int storeStockDayNum = 0;
				for (Map.Entry<String, Double> map : storeMap.entrySet()) {
					String key = map.getKey();
					String storeCode = key.substring(key.lastIndexOf("-") + 1);
					String sysId = key.substring(0, key.indexOf("-"));
					double stockPrice = map.getValue();
					sellNumSum = 0;
					for (int k = 0, saleSize = saleList.size(); k < saleSize; k++) {
						sale = saleList.get(k);
						if (sysId.equals(sale.getSysId()) && storeCode.equals(sale.getStoreCode()) && region.equals(sale.getRegion())) {
							sellNumSum += (sale.getSellNum() == null) ? 0 : sale.getSellNum();
						}
					}
					
					// 库存天数
					double stockDay = 0;
					if (sellNumSum != 0) {
						stockDay = stockPrice/sellNumSum;
					}
					
					if (stockDay < 3) {
						storeStockDayNum ++;
					}
				}
				
				int productStockDayNum = 0;
				for (Map.Entry<String, Double> map : productMap.entrySet()) {
					String key = map.getKey();
					String simpleBarCode = key.substring(key.lastIndexOf("-") + 1);
					String sysId = key.substring(0, key.indexOf("-"));
					double stockPrice = map.getValue();
					sellNumSum = 0;
					for (int k = 0, saleSize = saleList.size(); k < saleSize; k++) {
						sale = saleList.get(k);
						if (sysId.equals(sale.getSysId()) && simpleBarCode.equals(sale.getSimpleBarCode()) && region.equals(sale.getRegion())) {
							sellNumSum += (sale.getSellNum() == null) ? 0 : sale.getSellNum();
						}
					}
					
					// 库存天数
					double stockDay = 0;
					if (sellNumSum != 0) {
						stockDay = stockPrice/sellNumSum;
					}
					
					if (stockDay < 1) {
						productStockDayNum ++;
					}
				}
				String[] rowValue = new String[]{
						region, // 省区
						String.valueOf(storeMap.size()), // 门店数量
						String.valueOf(storeStockDayNum), // 库存天数小于3的门店数量
						String.valueOf(productMap.size()), // 单品数量
						String.valueOf(productStockDayNum) // 库存天数小于1的单品数量
				};
				for (int j = 0, len = rowValue.length; j < len; j++) {
					Cell cell = row.createCell(j+5);
					cell.setCellValue(rowValue[j]);
				}
			}
			latch.countDown();
		}
		
	}
	@Override
	public void exportStockExcel(Stock stock, String stockNameStr, OutputStream output) throws Exception {
		logger.info("----->>>>自定义字段：{}<<<<------", stockNameStr);
		logger.info("----->>>>stock：{}<<<<------", FastJsonUtil.objectToString(stock));
		if (CommonUtil.isBlank(stockNameStr)) {
			throw new DataException("540");
		}
		if (stock == null) {
			throw new DataException("538");
		}
		if (CommonUtil.isBlank(stock.getSysId())) {
			throw new DataException("538");
		}
		
		
		String[] header = CommonUtil.parseIdsCollection(stockNameStr, ",");
		Map<String, Object> map = exportUtil.joinColumn(StockEnum.class, header);
		
		// 调用方法名
		String[] methodNameArray = (String[]) map.get("methodNameArray");
		
		// 导出字段
		String column = (String) map.get("column");
		
		// 自选导出excel表查询字段
		Map<String, Object> param = exportUtil.joinParam(null, null, column, stock.getSysId());
		
		// 系统编号
		if (CommonUtil.isNotBlank(stock.getSysId())) {
			param.put("sysId", stock.getSysId());
		}
		
		// 门店编号
		if (CommonUtil.isNotBlank(stock.getStoreCode())) {
			param.put("storeCode", stock.getStoreCode());
		}

		// 品牌
		if (CommonUtil.isNotBlank(stock.getBrand())) {
			param.put("brand", stock.getBrand());
		}
		
		// 单品条码
		if (CommonUtil.isNotBlank(stock.getSimpleBarCode())) {
			param.put("simpleBarCode", stock.getSimpleBarCode());
		}
		
		// 大区
		if (CommonUtil.isNotBlank(stock.getRegion())) {
			param.put("region", stock.getRegion());
		}
				
		// 省区
		if (CommonUtil.isNotBlank(stock.getProvinceArea())) {
			param.put("provinceArea", stock.getProvinceArea());
		}
		logger.info("------->>>>>导出条件：{}<<<<<-------", FastJsonUtil.objectToString(param));
		List<Stock> dataList = queryListByObject(QueryId.QUERY_STOCK_BY_ANY_COLUMN, param);

		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		excelUtil.exportCustom2007("库存信息", header, methodNameArray, dataList, output);
	}

	/**
	 * 日报表上半部分
	 * @return
	 * @throws IOException 
	 */
	public <T> SXSSFWorkbook createRegionTop(String sheetName, String title, String[] headers, String queryDate, 
			List<T> dataList, OutputStream output) throws IOException {
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet(sheetName);

		Row dateRow = sheet.createRow(0);

		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);

		Row headerRow = sheet.createRow(3);

		// 生成粗体剧中标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, headers.length);

		// 设置表头
		excelUtil.createRow(headerRow, headers, true);
		
		if (CommonUtil.isBlank(dataList)) {
			wb.write(output);
			output.flush();
			output.close();
		}
		
		return wb;
	}
	@Override
	public void exportCompanyExcelBySys(OutputStream output) throws IOException {
		// 今天
		LocalDate nowDay = LocalDate.now();
		String queryDate = nowDay.toString();

		Map<String, Object> param = new HashMap<>(2);
		param.put("queryDate", queryDate);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);

		List<SysStockModel> sysStockList = queryListByObject(QueryId.QUERY_SYS_STOCK_MODEL_BY_PARAM, null);

		// 工作簿名称
		String sheetName = "公司一级表";

		// 标题
		String title = "全公司直营KA分系统库存日报表";

		// 表头
		String[] headers = new String[] { "系统", "门店数量", "库存金额", "店均库存", "昨日销售", "库存天数" };

		SXSSFWorkbook wb = createRegionTop(sheetName, title, headers, queryDate, sysStockList, output);

		
		LocalDate lastDay = nowDay.minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " sys_id, sell_num ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		int i, j, k;
		int size, stockSize, saleSize;
		SysStockModel sysStock = null;
		List<Stock> stockList = null;
		Stock stock = null;
		Sale sale = null;

		// 系统编号
		String sysId = null;
		
		// 系统名
		String sysName = null;

		// 库存金额
		double stockPriceSum = 0;

		// 昨日销量
		int sellNumSum = 0;

		// 库存天数
		double stockDay = 0;

		// 门店集合
		Set<String> storeSet = new HashSet<>();
		
		// 品牌集合
		Set<String> brandSet = new HashSet<>();
		brandSet.add("全部");
		String[][] rowValue = new String[sysStockList.size()][6];
		for (i = 0, size = sysStockList.size(); i < size; i++) {

			sysStock = sysStockList.get(i);
			stockList = sysStock.getStockList();
			sysName = sysStock.getSysName() == null ? "" : sysStock.getSysName();
			sysId = sysStock.getSysId() == null ? "" : sysStock.getSysId();
			stockPriceSum = 0;
			sellNumSum = 0;
			
			storeSet.clear();
			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				storeSet.add(stock.getStoreCode());
				stockPriceSum += stock.getStockPrice() == null ? 0 : stock.getStockPrice();

				if (stock.getBrand() != null) {
					brandSet.add(stock.getBrand());
				}
			}
			for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
				sale = saleList.get(k);
				if (sysId.equals(sale.getSysId())) {
					sellNumSum += sale.getSellNum() == null ? 0 : sale.getSellNum();
				}
			}
			if (sellNumSum == 0) {
				stockDay = 0;
			} else {
				stockDay = stockPriceSum / sellNumSum;
			}
			rowValue[i] = new String[] { sysName, // 系统名称
					String.valueOf(storeSet.size()), // 门店数量
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum)), // 库存金额
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum / storeSet.size())), // 店均库存
					String.valueOf(sellNumSum), // 昨日销量
					String.valueOf(CommonUtil.setScale("0.00", stockDay) + "天") // 库存天数
			};

		}
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Sheet sheet = wb.getSheet(sheetName);
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());

		Row row = null;

		int rowIndex = 4;
		for (i = 0, size = rowValue.length; i < size; i++) {
			row = sheet.createRow(rowIndex);
			String[] value = rowValue[i];
			excelUtil.createRow(row, value, false);
			rowIndex++;
		}

		wb.write(output);
		output.flush();
		output.close();
	}

	@Override
	public void exportRegionExcelBySys(String sysId, OutputStream output) throws IOException {
		logger.info("----->>>>>sysId:{}<<<<<----", sysId);
		// 今天
		LocalDate nowDay = LocalDate.now();
		String queryDate = nowDay.toString();

		Map<String, Object> param = new HashMap<>(3);
		param.put("queryDate", queryDate);
		param.put("sysId", sysId);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);
		
		List<RegionStockModel> regionStockList = queryListByObject(QueryId.QUERY_REGION_STOCK_MODEL_BY_PARAM, param);

		// 工作簿名称
		String sheetName = "区域表一级表";

		// 系统名称
		String sysName = regionStockList.get(0).getStockList().get(0).getSysName();
		
		// 标题
		String title = sysName + "直营KA分大区库存日报表";

		// 表头
		String[] headers = new String[] { "系统", "大区", "门店数量", "库存金额", "店均库存", "昨日销售", "库存天数" };

		SXSSFWorkbook wb = createRegionTop(sheetName, title, headers, queryDate, regionStockList, output);

		LocalDate lastDay = nowDay.minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " region, sell_num ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		int i, j, k;
		int size, stockSize, saleSize;
		RegionStockModel regionStock = null;
		List<Stock> stockList = null;
		Stock stock = null;
		Sale sale = null;

		// 大区
		String region = null;

		// 库存金额
		double stockPriceSum = 0;

		// 昨日销量
		int sellNumSum = 0;

		// 库存天数
		double stockDay = 0;

		// 门店集合
		Set<String> storeSet = new HashSet<>();
		
		// 品牌集合
		Set<String> brandSet = new HashSet<>();
		brandSet.add("全部");
		String[][] rowValue = new String[regionStockList.size()][6];
		for (i = 0, size = regionStockList.size(); i < size; i++) {

			regionStock = regionStockList.get(i);
			stockList = regionStock.getStockList();
			region = regionStock.getRegion() == null ? "" : regionStock.getRegion();

			stockPriceSum = 0;
			sellNumSum = 0;
			
			storeSet.clear();
			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				storeSet.add(stock.getStoreCode());
				stockPriceSum += stock.getStockPrice() == null ? 0 : stock.getStockPrice();

				if (stock.getBrand() != null) {
					brandSet.add(stock.getBrand());
				}
			}
			for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
				sale = saleList.get(k);
				if (region.equals(sale.getRegion())) {
					sellNumSum += sale.getSellNum() == null ? 0 : sale.getSellNum();
				}
			}
			if (sellNumSum == 0) {
				stockDay = 0;
			} else {
				stockDay = stockPriceSum / sellNumSum;
			}
			rowValue[i] = new String[] { 
					sysName, //系统名
					region, // 大区
					String.valueOf(storeSet.size()), // 门店数量
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum)), // 库存金额
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum / storeSet.size())), // 店均库存
					String.valueOf(sellNumSum), // 昨日销量
					String.valueOf(CommonUtil.setScale("0.00", stockDay) + "天") // 库存天数
			};

		}
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Sheet sheet = wb.getSheet(sheetName);
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());

		Row row = null;

		int rowIndex = 4;
		for (i = 0, size = rowValue.length; i < size; i++) {
			row = sheet.createRow(rowIndex);
			String[] value = rowValue[i];
			excelUtil.createRow(row, value, false);
			rowIndex++;
		}

		wb.write(output);
		output.flush();
		output.close();
	}

	@Override
	public void exportRegionSecondExcelBySys(String sysId, String region, OutputStream output)
			throws IOException {
		logger.info("----->>>>>region:{},sysId:{}<<<<<-------", region, sysId);
		// 今天
		LocalDate nowDay = LocalDate.now();
		String queryDate = nowDay.toString();

		Map<String, Object> param = new HashMap<>(4);
		param.put("sysId", sysId);	
		param.put("queryDate", queryDate);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);

		param.put("region", region);
		List<StoreStockModel> storeStockList = queryListByObject(QueryId.QUERY_STORE_STOCK_MODEL_BY_PARAM, param);

		// 工作簿名称
		String sheetName = "区域表二级表";

		// 系统名称
		String sysName = storeStockList.get(0).getStockList().get(0).getSysName();

		// 标题
		String title = sysName + "直营KA分大区库存日报表";

		// 表头
		String[] headers = new String[] { "系统", "大区", "门店编号", "门店", "门店数量", "库存金额", "店均库存", "昨日销售", "库存天数" };

		SXSSFWorkbook wb = createRegionTop(sheetName, title, headers, queryDate, storeStockList, output);

		
		LocalDate lastDay = nowDay.minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " store_code, sell_num ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		int i, j, k;
		int size, stockSize, saleSize;
		StoreStockModel storeStock = null;
		List<Stock> stockList = null;
		Stock stock = null;
		Sale sale = null;

		// 门店编号
		String storeCode = null;
		
		// 门店名称
		String storeName = null;

		// 库存金额
		double stockPriceSum = 0;

		// 昨日销量
		int sellNumSum = 0;

		// 库存天数
		double stockDay = 0;

		// 门店集合 
		Set<String> storeSet = new HashSet<>();
		
		// 品牌集合
		Set<String> brandSet = new HashSet<>();
		brandSet.add("全部");
		String[][] rowValue = new String[storeStockList.size()][6];
		for (i = 0, size = storeStockList.size(); i < size; i++) {

			storeStock = storeStockList.get(i);
			stockList = storeStock.getStockList();
			storeName = storeStock.getStoreName() == null ? "" : storeStock.getStoreName();
			storeCode = storeStock.getStoreCode() == null ? "" : storeStock.getStoreCode();
			
			stockPriceSum = 0;
			sellNumSum = 0;
			
			storeSet.clear();
			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				storeSet.add(stock.getStoreCode());
				stockPriceSum += stock.getStockPrice() == null ? 0 : stock.getStockPrice();

				if (stock.getBrand() != null) {
					brandSet.add(stock.getBrand());
				}
			}
			for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
				sale = saleList.get(k);
				if (storeCode.equals(sale.getStoreCode())) {
					sellNumSum += sale.getSellNum() == null ? 0 : sale.getSellNum();
				}
			}
			if (sellNumSum == 0) {
				stockDay = 0;
			} else {
				stockDay = stockPriceSum / sellNumSum;
			}
			rowValue[i] = new String[] { sysName, // 系统名
					region, // 大区
					Arrays.toString(storeSet.toArray()), // 门店编号
					storeName, // 门店名称
					String.valueOf(storeSet.size()), // 门店数量
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum)), // 库存金额
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum / storeSet.size())), // 店均库存
					String.valueOf(sellNumSum), // 昨日销量
					String.valueOf(CommonUtil.setScale("0.00", stockDay) + "天") // 库存天数
			};

		}
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Sheet sheet = wb.getSheet(sheetName);
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());

		Row row = null;

		int rowIndex = 4;
		for (i = 0, size = rowValue.length; i < size; i++) {
			row = sheet.createRow(rowIndex);
			String[] value = rowValue[i];
			excelUtil.createRow(row, value, false);
			rowIndex++;
		}

		wb.write(output);
		output.flush();
		output.close();
	}
	@Override
	public void exportCompanyExcelByRegion(OutputStream output) throws IOException {
		// 今天
		LocalDate nowDay = LocalDate.now();
		String queryDate = nowDay.toString();

		Map<String, Object> param = new HashMap<>(3);
		param.put("queryDate", queryDate);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);
		
		List<RegionStockModel> regionStockList = queryListByObject(QueryId.QUERY_REGION_STOCK_MODEL_BY_PARAM, param);

		// 工作簿名称
		String sheetName = "公司一级表";

		// 标题
		String title = "全公司直营KA分系统库存日报表";

		// 表头
		String[] headers = new String[] {"大区", "门店数量", "库存金额", "店均库存", "昨日销售", "库存天数"};

		SXSSFWorkbook wb = createRegionTop(sheetName, title, headers, queryDate, regionStockList, output);
		
		LocalDate lastDay = nowDay.minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " region, store_code, sell_num ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		int i, j, k;
		int size, stockSize, saleSize;
		RegionStockModel regionStock = null;
		List<Stock> stockList = null;
		Stock stock = null;
		Sale sale = null;

		// 大区
		String region = null;

		// 库存金额
		double stockPriceSum = 0;

		// 昨日销量
		int sellNumSum = 0;

		// 库存天数
		double stockDay = 0;

		// 品牌集合
		Set<String> brandSet = new HashSet<>();
		
		// 门店集合
		Set<String> storeSet = new HashSet<>();
		brandSet.add("全部");
		String[][] rowValue = new String[regionStockList.size()][6];
		for (i = 0, size = regionStockList.size(); i < size; i++) {

			regionStock = regionStockList.get(i);
			stockList = regionStock.getStockList();
			region = regionStock.getRegion() == null ? "" : regionStock.getRegion();
			stockPriceSum = 0;
			sellNumSum = 0;
			
			storeSet.clear();
			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				storeSet.add(stock.getSysId() + stock.getStoreCode());
				stockPriceSum += stock.getStockPrice() == null ? 0 : stock.getStockPrice();

				if (stock.getBrand() != null) {
					brandSet.add(stock.getBrand());
				}
			}
			for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
				sale = saleList.get(k);
				if (region.equals(sale.getRegion())) {
					sellNumSum += sale.getSellNum() == null ? 0 : sale.getSellNum();
				}
			}
			if (sellNumSum == 0) {
				stockDay = 0;
			} else {
				stockDay = stockPriceSum / sellNumSum;
			}
			rowValue[i] = new String[] { regionStock.getRegion(), // 大区
					String.valueOf(storeSet.size()), // 门店数量
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum)), // 库存金额
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum / storeSet.size())), // 店均库存
					String.valueOf(sellNumSum), // 昨日销量
					String.valueOf(CommonUtil.setScale("0.00", stockDay) + "天") // 库存天数
			};

		}
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Sheet sheet = wb.getSheet(sheetName);
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());

		Row row = null;

		int rowIndex = 4;
		for (i = 0, size = rowValue.length; i < size; i++) {
			row = sheet.createRow(rowIndex);
			String[] value = rowValue[i];
			excelUtil.createRow(row, value, false);
			rowIndex++;
		}

		wb.write(output);
		output.flush();
		output.close();
	}
	@Override
	public void exportRegionExcelByRegion(String region, OutputStream output) throws IOException {
		logger.info("------>>>>>>region:{}<<<<<<--------", region);
		// 今天
		LocalDate nowDay = LocalDate.now();
		String queryDate = nowDay.toString();

		Map<String, Object> param = new HashMap<>(3);
		param.put("queryDate", queryDate);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);

		param.put("region", region);
		List<SysStockModel> sysStockList = queryListByObject(QueryId.QUERY_SYS_STOCK_MODEL_BY_PARAM, param);
		
		// 工作簿名称
		String sheetName = "区域表一级表";

		// 标题
		String title = region + "直营KA分大区库存日报表";

		// 表头
		String[] headers = new String[] {"大区", "系统", "门店数量", "库存金额", "店均库存", "昨日销售", "库存天数"};

		SXSSFWorkbook wb = createRegionTop(sheetName, title, headers, queryDate, sysStockList, output);

		LocalDate lastDay = nowDay.minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " sell_num, sys_id ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		int i, j, k;
		int size, stockSize, saleSize;
		SysStockModel sysStock = null;
		List<Stock> stockList = null;
		Stock stock = null;
		Sale sale = null;
		
		// 系统名称
		String sysName;

		// 库存金额
		double stockPriceSum = 0;

		// 昨日销量
		int sellNumSum = 0;

		// 库存天数
		double stockDay = 0;

		// 门店集合
		Set<String> storeSet = new HashSet<>();
		
		// 品牌集合
		Set<String> brandSet = new HashSet<>();
		brandSet.add("全部");
		String[][] rowValue = new String[sysStockList.size()][6];
		for (i = 0, size = sysStockList.size(); i < size; i++) {
			sysStock = sysStockList.get(i);
			stockList = sysStock.getStockList();
			
			// 系统名称
			sysName = sysStock.getSysName();
			
			// 库存总金额
			stockPriceSum = 0;
			
			// 销售总数量
			sellNumSum = 0;
			
			storeSet.clear();
			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				storeSet.add(stock.getStoreCode());
				stockPriceSum += stock.getStockPrice() == null ? 0 : stock.getStockPrice();

				if (stock.getBrand() != null) {
					brandSet.add(stock.getBrand());
				}
			}
			for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
				sale = saleList.get(k);
				if (sysStock.getSysId().equals(sale.getSysId())) {
					sellNumSum += sale.getSellNum() == null ? 0 : sale.getSellNum();
				}
			}
			if (sellNumSum == 0) {
				stockDay = 0;
			} else {
				stockDay = stockPriceSum / sellNumSum;
			}
			rowValue[i] = new String[] { 
					region, // 大区
					sysName, // 系统名称
					String.valueOf(storeSet.size()), // 门店数量
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum)), // 库存金额
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum / storeSet.size())), // 店均库存
					String.valueOf(sellNumSum), // 昨日销量
					String.valueOf(CommonUtil.setScale("0.00", stockDay) + "天") // 库存天数
			};

		}

		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Sheet sheet = wb.getSheet(sheetName);
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());

		Row row = null;

		int rowIndex = 4;
		for (i = 0, size = rowValue.length; i < size; i++) {
			row = sheet.createRow(rowIndex);
			String[] value = rowValue[i];
			excelUtil.createRow(row, value, false);
			rowIndex++;
		}

		wb.write(output);
		output.flush();
		output.close();

	}
	@Override
	public void exportProvinceAreaExcelByRegion(String region, OutputStream output) throws IOException {
		logger.info("------->>>>>region:{}<<<<<-------", region);
		// 今天
		LocalDate nowDay = LocalDate.now();
		String queryDate = nowDay.toString();
		
		Map<String, Object> param = new HashMap<>(3);
		param.put("queryDate", queryDate);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);

		param.put("region", region);
		List<ProvinceAreaStockModel> provinceAreaStockList = queryListByObject(QueryId.QUERY_PROVINCE_MODEL_BY_PARAM, param);

		// 工作簿名称
		String sheetName = "区域表二级表";
		
		// 标题
		String title = region + "直营KA分大区门店库存日报表";

		// 表头
		String[] headers = new String[] { "大区", "省区", "门店数量", "库存金额", "店均库存", "昨日销售", "库存天数" };
		
		SXSSFWorkbook wb = createRegionTop(sheetName, title, headers, queryDate, provinceAreaStockList, output);

		LocalDate lastDay = LocalDate.parse(queryDate).minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " sell_num, province_area ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		int i, j, k;
		int size, stockSize, saleSize;
		ProvinceAreaStockModel provinceAreaStock = null;
		List<Stock> stockList = null;
		Stock stock = null;
		Sale sale = null;

		// 省区
		String provinceArea = null;

		// 库存金额
		double stockPriceSum = 0;

		// 昨日销量
		int sellNumSum = 0;

		// 库存天数
		double stockDay = 0;

		// 门店集合
		Set<String> storeSet = new HashSet<>();
		
		// 品牌集合
		Set<String> brandSet = new HashSet<>();
		brandSet.add("全部");
		String[][] rowValue = new String[provinceAreaStockList.size()][6];
		for (i = 0, size = provinceAreaStockList.size(); i < size; i++) {
			provinceAreaStock = provinceAreaStockList.get(i);
			stockList = provinceAreaStock.getStockList();

			// 省区
			provinceArea = provinceAreaStock.getProvinceArea() == null ? "" : provinceAreaStock.getProvinceArea();

			// 库存总金额
			stockPriceSum = 0;

			// 销售总数量
			sellNumSum = 0;
			
			storeSet.clear();
			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				storeSet.add(stock.getSysId() + "-" + stock.getStoreCode());
				stockPriceSum += stock.getStockPrice() == null ? 0 : stock.getStockPrice();

				if (stock.getBrand() != null) {
					brandSet.add(stock.getBrand());
				}
			}
			for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
				sale = saleList.get(k);
				if (provinceArea.equals(sale.getProvinceArea())) {
					sellNumSum += sale.getSellNum() == null ? 0 : sale.getSellNum();
				}
			}
			if (sellNumSum == 0) {
				stockDay = 0;
			} else {
				stockDay = stockPriceSum / sellNumSum;
			}
			rowValue[i] = new String[] { region, // 大区
					provinceArea, // 省区
					String.valueOf(storeSet.size()), // 门店数量
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum)), // 库存金额
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum / storeSet.size())), // 店均库存
					String.valueOf(sellNumSum), // 昨日销量
					String.valueOf(CommonUtil.setScale("0.00", stockDay) + "天") // 库存天数
			};

		}

		Sheet sheet = wb.getSheet(sheetName);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());

		Row row = null;

		int rowIndex = 4;
		for (i = 0, size = rowValue.length; i < size; i++) {
			row = sheet.createRow(rowIndex);
			String[] value = rowValue[i];
			excelUtil.createRow(row, value, false);
			rowIndex++;
		}

		wb.write(output);
		output.flush();
		output.close();
		
	}

	@Override
	public void exportStoreExcelByRegion(String provinceArea, OutputStream output) throws IOException {
		logger.info("------->>>>>>>provinceArea:{}<<<<<<<<-------", provinceArea);
		// 今天
		LocalDate nowDay = LocalDate.now();
		String queryDate = nowDay.toString();

		Map<String, Object> param = new HashMap<>(3);
		param.put("queryDate", queryDate);
		
		// 检查当天是否有符合条件的库存
		checkNowStock(param, output);

		param.put("provinceArea", provinceArea);
		List<StoreStockModel> storeStockList = queryListByObject(QueryId.QUERY_STORE_STOCK_MODEL_BY_PARAM, param);

		// 工作簿名称
		String sheetName = "区域表三级表";

		// 标题
		String title = provinceArea + "直营KA分大区门店库存日报表";

		// 表头
		String[] headers = new String[] { "省区", "门店编号", "门店", "门店数量", "库存金额", "店均库存", "昨日销售", "库存天数" };

		SXSSFWorkbook wb = createRegionTop(sheetName, title, headers, queryDate, storeStockList, output);

		LocalDate lastDay = LocalDate.parse(queryDate).minusDays(1L);
		
		param.put("queryDate", lastDay.toString());
		param.put("column", " sell_num, store_code, store_name ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		int i, j, k;
		int size, stockSize, saleSize;
		StoreStockModel storeStock = null;
		List<Stock> stockList = null;
		Stock stock = null;
		Sale sale = null;

		// 门店名称
		String storeName = null;

		// 门店编号
		String storeCode = null;

		// 库存金额
		double stockPriceSum = 0;

		// 昨日销量
		int sellNumSum = 0;

		// 库存天数
		double stockDay = 0;

		// 门店集合
		Set<String> storeSet = new HashSet<>();
		
		// 品牌集合
		Set<String> brandSet = new HashSet<>();
		brandSet.add("全部");
		String[][] rowValue = new String[storeStockList.size()][6];
		for (i = 0, size = storeStockList.size(); i < size; i++) {
			storeStock = storeStockList.get(i);
			stockList = storeStock.getStockList();

			// 门店名称
			storeName = storeStock.getStoreName() == null ? "" : storeStock.getStoreName();
			
			// 门店编号
			storeCode = storeStock.getStoreCode() == null ? "" : storeStock.getStoreCode();
			
			// 库存总金额
			stockPriceSum = 0;

			// 销售总数量
			sellNumSum = 0;
			
			storeSet.clear();
			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				storeSet.add(stock.getSysName() + stock.getSysId() + "-" + stock.getStoreCode());
				stockPriceSum += stock.getStockPrice() == null ? 0 : stock.getStockPrice();

				if (stock.getBrand() != null) {
					brandSet.add(stock.getBrand());
				}
			}
			for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
				sale = saleList.get(k);
				if (storeCode.equals(sale.getStoreCode()) && storeName.equals(sale.getStoreName())) {
					sellNumSum += sale.getSellNum() == null ? 0 : sale.getSellNum();
				}
			}
			if (sellNumSum == 0) {
				stockDay = 0;
			} else {
				stockDay = stockPriceSum / sellNumSum;
			}
			String storeCodeArray = null;
			if (CommonUtil.isBlank(storeName)) {
				storeCodeArray = Arrays.toString(storeSet.toArray());
			}
			rowValue[i] = new String[] { provinceArea, // 省区
					storeCodeArray, // 门店编号
					storeName, // 门店名称
					String.valueOf(storeSet.size()), // 门店数量
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum)), // 库存金额
					String.valueOf(CommonUtil.setScale("0.00", stockPriceSum / storeSet.size())), // 店均库存
					String.valueOf(sellNumSum), // 昨日销量
					String.valueOf(CommonUtil.setScale("0.00", stockDay) + "天") // 库存天数
			};

		}

		Sheet sheet = wb.getSheet(sheetName);

		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		// 生成品牌
		Row brandRow = sheet.createRow(1);
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", brandSet.toArray());

		Row row = null;

		int rowIndex = 4;
		for (i = 0, size = rowValue.length; i < size; i++) {
			row = sheet.createRow(rowIndex);
			String[] value = rowValue[i];
			excelUtil.createRow(row, value, false);
			rowIndex++;
		}

		wb.write(output);
		output.flush();
		output.close();
	}
	
	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil uploadStockData(MultipartFile file) {
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		List<Map<String, Object>> stockMapList;
		try {
			stockMapList = excelUtil.getExcelList(file, ExcelEnum.STOCK_TEMPLATE_TYPE.value());
			if (stockMapList == null) {
				return ResultUtil.error(CodeEnum.EXCEL_FORMAT_ERROR_DESC.value());
			}
			if(stockMapList.size() == 0) {
				return ResultUtil.error(CodeEnum.DATA_EMPTY_ERROR_DESC.value());
			}
			String stockStr = JSON.toJSONString(stockMapList);
			List<Stock> stockList = JSON.parseArray(stockStr, Stock.class);
			// 模板门店列表
			List<TemplateStore> storeList = redisService.queryTemplateStoreList();
			// 模板商品列表
			List<TemplateProduct> productList = redisService.queryTemplateProductList();
			// 供应链列表
			List<TemplateSupply> supplyList = queryListByObject(QueryId.QUERY_SUPPLY_BY_CONDITION, new HashMap<>(1));
			dataService.mateStockData(supplyList, storeList, productList, stockList);
			insert(InsertId.INSERT_STOCK_BATCH, stockList);
			//insert(InsertId.INSERT_BATCH_STOCK, stockMapList);
		} catch (IOException e) {
			return ResultUtil.error(CodeEnum.UPLOAD_ERROR_DESC.value());
		} catch (Exception se) {
			return ResultUtil.error(CodeEnum.SQL_ERROR_DESC.value());
		}
		return ResultUtil.success();
	}
}
