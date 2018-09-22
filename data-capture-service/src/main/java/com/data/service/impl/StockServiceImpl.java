package com.data.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import org.apache.poi.ss.usermodel.Font;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.bean.TemplateProduct;
import com.data.bean.TemplateStore;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.StockEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.service.IStockService;
import com.data.utils.CommonUtil;
import com.data.utils.DataCaptureUtil;
import com.data.utils.DateUtil;
import com.data.utils.ExcelUtil;
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
	
	@Override
	public ResultUtil getStockByWeb(CommonDTO common, String sysId, Integer page, Integer limit) throws IOException {
		logger.info("------>>>>>>前端传递common：{}<<<<<<<-------", FastJsonUtil.objectToString(common));
		List<Stock> stockList = dataCaptureUtil.getDataByWeb(common, sysId, WebConstant.STOCK, Stock.class);
		for (int i = 0, size = stockList.size(); i < size; i++) {
			Stock stock = stockList.get(i);
			
			// 单品编号
			String simpleCode = stock.getSimpleCode();
			
			// 系统名称
			String sysName = stock.getSysName();
			
			// 门店编号
			String storeCode = stock.getStockCode();
		
			// 标准门店信息
			TemplateStore store = templateDataUtil.getStandardStoreMessage(sysId, storeCode);
			
			// 商品条码
			String simpleBarCode = stock.getSimpleBarCode();		
			
			// 地区
			String localName = stock.getLocalName();
			if (CommonUtil.isBlank(simpleBarCode)) {
				
				// 标准条码匹配信息
				simpleBarCode = templateDataUtil.getBarCodeMessage(sysName, simpleCode);
			}
			if (CommonUtil.isBlank(simpleBarCode)) {
				stock.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
				continue;
			}
			stock.setSimpleBarCode(simpleBarCode);
			
			sysName = CommonUtil.isBlank(localName) ? sysName : (localName + sysName);
			stock.setSysName(sysName);
			
			// 标准单品信息
			TemplateProduct product = templateDataUtil.getStandardProductMessage(sysId, simpleBarCode);
			
			// 门店信息为空
			if (CommonUtil.isBlank(store)) {
				stock.setRemark(TipsEnum.STORE_MESSAGE_IS_NULL.getValue());
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
				stock.setRemark(TipsEnum.PRODUCT_MESSAGE_IS_NULL.getValue());
				continue;
			} else {
				// 单品名称
				stock.setSimpleName(product.getStandardName());
				
				// 品牌
				stock.setBrand(product.getBrand());
				
				// 价格
				stock.setTaxCostPrice(product.getIncludeTaxPrice() == null ? 0 : product.getIncludeTaxPrice());
				
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
	
				// 库存金额
				stock.setStockPrice(stock.getTaxCostPrice() * stock.getStockNum());
			}
		}
		logger.info("---->>>开始插入数据<<<-----");
		dataCaptureUtil.insertData(stockList, InsertId.INSERT_BATCH_STOCK);
		PageRecord<Stock> pageRecord = dataCaptureUtil.setPageRecord(stockList, page, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil getStockByParam(CommonDTO common, Stock stock, Integer page, Integer limit) throws Exception {
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
		logger.info("--------->>>>>>>>stock:" + FastJsonUtil.objectToString(stock) + "<<<<<<<<----------");
		if (null != stock) {
			
			// 门店名称
			if (StringUtils.isNoneBlank(stock.getStoreName())) {
				map.put("storeName", stock.getStoreName());
			}
			 
			// 区域
			if (StringUtils.isNoneBlank(stock.getRegion())) {
				map.put("region", stock.getRegion());
			}
			
			// 系列
			if (StringUtils.isNoneBlank(stock.getSeries())) {
				map.put("series", stock.getSeries());
			}
			
			// 单品名称
			if (StringUtils.isNoneBlank(stock.getSimpleName())) {
				map.put("simpleName", stock.getSimpleName());
			}
		}
		PageRecord<Stock> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_STOCK_BY_PARAM, QueryId.QUERY_STOCK_BY_PARAM, map, page, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public ResultUtil expertStoreProductExcel(String queryDate, String storeName, OutputStream output) throws IOException {
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.QUERY_DATE_IS_NULL.getValue());
		}
		if (CommonUtil.isBlank(storeName)) {
			return ResultUtil.error(TipsEnum.STORE_NAME_IS_NULL.getValue());
		}
		
		// 当天的库存数据
		Map<String, Object> param = new HashMap<>(4);
		param.put("queryDate", queryDate);
		param.put("storeName", storeName);
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_PARAM, param);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("门店单品表");
		Row title = sheet.createRow(0);
		
		// 表头
		excelUtil.createRow(title, new String[]{"门店", "单品名称", "单品编码", "单品条码", "前一周销售数量", "库存数量", "库存天数"});
		
		// 填写数据
		for (int i = 0, size = stockList.size(); i < size; i++) {
			Stock stock = stockList.get(i);
			Row row = sheet.createRow(i+1);
			
			// 上周一
			LocalDate lastMonday = LocalDate.parse(queryDate).minusWeeks(1L).with(DayOfWeek.MONDAY);
			
			// 上周末
			LocalDate lastSunday = lastMonday.with(DayOfWeek.SUNDAY);
			
			// 查询上周销量
			param.clear();
			param.put("startDate", lastMonday.toString());
			param.put("endDate", lastSunday.toString());
			param.put("simpleBarCode", stock.getSimpleBarCode());
			param.put("storeName", stock.getStoreName());
			List<Sale> saleWeekList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, param);
			
			// 上周该单品的销售数量
			Integer sumSaleNumByWeek = 0; 
			
			// 如果上周存在销售记录，否则默认为0
			if (CommonUtil.isNotBlank(saleWeekList)) {
				sumSaleNumByWeek = saleWeekList.stream().collect(Collectors.summingInt(s -> s.getSellNum()));
			}
			
			// 行值
			String[] cellValue = new String[]{
					stock.getStoreName(),
					stock.getSimpleName(),
					stock.getSimpleCode(),
					stock.getSimpleBarCode(),
					String.valueOf(CommonUtil.toIntOrZero(sumSaleNumByWeek)),
					String.valueOf(CommonUtil.toIntOrZero(stock.getStockNum()))
			};
			excelUtil.createRow(row, cellValue);
			
			// 库存金额
			double stockPrice = CommonUtil.toDoubleOrZero(stock.getStockPrice());
			
			// 库存天数
			double stockDayNum = stockDataUtil.calculateStockDay(queryDate, stock.getSimpleBarCode(), stock.getStoreName(), stockPrice);		
						
			CellStyle cellStyle = null;
			
			// 库存天数小于3 ，单元格黄底
			if (stockDayNum < 3) {
				cellStyle = excelUtil.getCellStyle(wb, CellStyle.SOLID_FOREGROUND, HSSFColor.YELLOW.index);
			} else if (stockDayNum == 0) { // 库存天数等于0，单元格红底，字体白色
				// 红底
				cellStyle = excelUtil.getCellStyle(wb, CellStyle.SOLID_FOREGROUND, HSSFColor.RED.index);
				
				// 字体样式
				Font font = excelUtil.getColorFont(wb, HSSFColor.WHITE.index); 
				cellStyle.setFont(font);
			}
			
			Cell stockDayNumCell = row.createCell(cellValue.length);
			stockDayNumCell.setCellValue(stockDayNum);
			if (null != cellStyle) {
				stockDayNumCell.setCellStyle(cellStyle);
			}
		}
		wb.write(output);
		return ResultUtil.success();
	}
	
	public static void main(String[] args) {
		List<Stock> stockList = new ArrayList<>();
		Stock s1 = new Stock();
		s1.setStockNum(2);
		s1.setBrand("火腿");
		stockList.add(s1);
		Stock s2 = new Stock();
		s2.setStockNum(null);
		s2.setBrand("火腿");
		stockList.add(s2);
		Map<String, IntSummaryStatistics> map = stockList.parallelStream()
				.filter(s -> s.getStockNum() != null)
				.collect(Collectors.groupingBy(Stock::getBrand, Collectors.summarizingInt(Stock::getStockNum)));
		System.err.println(map.get("火腿").getSum());
	}

	@Override
	public ResultUtil expertSysStoreExcel(String queryDate, String sysName, OutputStream output) throws IOException {
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.QUERY_DATE_IS_NULL.getValue());
		}
		if (CommonUtil.isBlank(sysName)) {
			return ResultUtil.error(TipsEnum.SYS_NAME_IS_NULL.getValue());
		}
		
		// 按系统名称和时间查询库存
		Map<String, Object> param = new HashMap<>(2);
		param.put("queryDate", queryDate);
		param.put("sysName", sysName);
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_PARAM, param);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("系统门店表");
		
		// 标题
		String title = sysName + "系统直营KA门店缺货日报表";
		
		// 生成表上部分
		int index = stockDataUtil.createMissStockTop(wb, sheet, queryDate, title);
			
		// 表头
		String[] headers = new String[]{"系统", "门店", "单品数量", "库存低于3天的单品", "单品数量", "库存等于0的单品数量"};
		Row headerRow = sheet.createRow(index);
		
		// 设置表头
		excelUtil.createRow(headerRow, headers);
		
		index++;
		// 生成表下部分数据
		stockDataUtil.createMissStockMessage(stockList, sheet, queryDate, sysName, index);
		wb.write(output);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil expertRegionStoreExcel(String queryDate, String region, OutputStream output) throws IOException {
		if (CommonUtil.isBlank(queryDate)) {
			return ResultUtil.error(TipsEnum.QUERY_DATE_IS_NULL.getValue());
		}
		if (CommonUtil.isBlank(region)) {
			return ResultUtil.error(TipsEnum.REGION_IS_NULL.getValue());
		}
		
		// 按系统名称和时间查询库存
		Map<String, Object> param = new HashMap<>(2);
		param.put("queryDate", queryDate);
		param.put("region", region);
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_PARAM, param);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("区域门店表");
		
		// 标题
		String title = region + "大区直营KA门店缺货日报表";
		
		// 生成表上部分
		int index = stockDataUtil.createMissStockTop(wb, sheet, queryDate, title);
			
		// 表头
		String[] headers = new String[]{"大区", "门店", "单品数量", "库存低于3天的单品", "单品数量", "库存等于0的单品数量"};
		Row headerRow = sheet.createRow(index);
		
		// 设置表头
		excelUtil.createRow(headerRow, headers);
	
		index++;
		// 生成表下部分数据
		stockDataUtil.createMissStockMessage(stockList, sheet, queryDate, region, index);
		wb.write(output);
		return ResultUtil.success();
	}
	@Override
	public ResultUtil expertStockExcel(String stockNameStr, CommonDTO common, OutputStream output) throws Exception {
		logger.info("----->>>>自定义字段：{}<<<<------", stockNameStr);
		logger.info("----->>>>common：{}<<<<------", FastJsonUtil.objectToString(common));
		if (null == common || CommonUtil.isBlank(common.getStartDate()) || CommonUtil.isBlank(common.getEndDate())) {
			return ResultUtil.error(TipsEnum.DATE_IS_NULL.getValue());
		}
		if (CommonUtil.isBlank(stockNameStr)) {
			return ResultUtil.error(TipsEnum.COLUMN_IS_NULL.getValue());
		}
		String[] stockNameArray = CommonUtil.parseIdsCollection(stockNameStr, ",");
		StringBuilder builder = new StringBuilder();
		
		// 拼接查询字段
		for (String s : stockNameArray) {
			StockEnum e = StockEnum.getEnum(s.trim());
			builder.append(e.getValue());
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		
		Map<String, Object> param = new HashMap<>(2);
		param.put("column", builder.toString());
		param.put("startDate", common.getStartDate());
		param.put("endDate", common.getEndDate());
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_ANY_COLUMN, param);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		excelUtil.excel2003("库存处理表", stockNameArray, stockList, output);
		return null;
	}

}
