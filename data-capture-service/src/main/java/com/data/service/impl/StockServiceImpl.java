package com.data.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
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
import com.data.constant.dbSql.DeleteId;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.enums.StockEnum;
import com.data.constant.enums.TipsEnum;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;
import com.data.model.RegionStockModel;
import com.data.model.StoreStockModel;
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
	
	@Override
	public ResultUtil getStockByWeb(String sysId, Integer limit) throws Exception {
		logger.info("------>>>>>>前端传递sysId:{}<<<<<<<-------", sysId);
		
		Map<String, Object> queryParam = new HashMap<>(2);
		queryParam.put("sysId", sysId);
		int count = queryCountByObject(QueryId.QUERY_COUNT_STOCK_BY_PARAM, queryParam);
		List<Stock> stockList = null;
		stockList = dataCaptureUtil.getDataByWeb("1900-01-01", sysId, WebConstant.STOCK, Stock.class);
		logger.info("------>>>>>>结束抓取库存数据<<<<<<---------");

		List<TemplateStore> storeList = redisService.queryTemplateStoreList();
		List<TemplateProduct> productList = redisService.queryTemplateProductList();
		
		Date now = DateUtil.getSystemDate();
		for (int i = 0, size = stockList.size(); i < size; i++) {
			Stock stock = stockList.get(i);
			stock.setCreateTime(now);
			// 单品编号
			String simpleCode = stock.getSimpleCode();

			// 系统名称
			String sysName = stock.getSysName();

			// 门店编号
			String storeCode = stock.getStoreCode();

			// 商品条码
			String simpleBarCode = stock.getSimpleBarCode();

			// 地区
			String localName = stock.getLocalName();

			// 标准条码匹配信息
			simpleBarCode = templateDataUtil.getBarCodeMessage(simpleBarCode, sysName, simpleCode);
			if (CommonUtil.isBlank(simpleBarCode)) {
				stock.setRemark(TipsEnum.SIMPLE_CODE_IS_NULL.getValue());
				continue;
			}
			stock.setSimpleBarCode(simpleBarCode);

			sysName = CommonUtil.isBlank(localName) ? sysName : (localName + sysName);
			stock.setSysName(sysName);

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

				stock.setTaxCostPrice(stock.getStockPrice() / stock.getStockNum());
			}
		}
		
		logger.info("---->>>开始删除数据<<<------");
		delete(DeleteId.DELETE_STOCK_BY_SYS_ID, sysId);
		
		logger.info("---->>>开始插入数据<<<-----");
		dataCaptureUtil.insertData(stockList, InsertId.INSERT_BATCH_STOCK);
		
		PageRecord<Stock> pageRecord = dataCaptureUtil.setPageRecord(stockList, limit);
		return ResultUtil.success(pageRecord);
	}
	public static void main(String[] args) throws IOException {
		String file = FileUtils.readFileToString(new File("E:\\baiya\\sale\\sale.txt"));
		List<Stock> list = (List<Stock>) FastJsonUtil.jsonToList(file, Stock.class);
		System.err.println(list.size());
	}
	@Override
	public ResultUtil getStockByParam(Stock stock, Integer page, Integer limit) throws Exception {

		Map<String, Object> map = Maps.newHashMap();
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
			
			// 系统ID
			if (StringUtils.isNoneBlank(stock.getSysId())) {
				map.put("sysId", stock.getSysId());
			}
						
			// 系统名称
			if (StringUtils.isNoneBlank(stock.getSysName())) {
				map.put("sysName", stock.getSysName());
			}
		}
		PageRecord<Stock> pageRecord = queryPageByObject(QueryId.QUERY_COUNT_STOCK_BY_PARAM, QueryId.QUERY_STOCK_BY_PARAM, map, page, limit);
		return ResultUtil.success(pageRecord);
	}

	@Override
	public void exportStoreProductExcel(String storeCode, OutputStream output) throws IOException {
		if (CommonUtil.isBlank(storeCode)) {
			throw new DataException("536");
		}
		
		// 当天的库存数据
		Map<String, Object> param = new HashMap<>(4);
		String now = LocalDate.now().toString();
		param.put("queryDate", now);
		int count = queryCountByObject(QueryId.QUERY_COUNT_STOCK_BY_PARAM, param);
		if (count == 0) {
			throw new DataException("541");
		}
		
		param.clear();
		param.put("storeCode", storeCode);
		param.put("column", " simple_bar_code, stock_price, store_name, simple_code, stock_num ");
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_ANY_COLUMN, param);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("门店单品表");
		
		String queryDate = LocalDate.now().toString();
		Row firstRow = sheet.createRow(0);
		stockDataUtil.createDateRow(wb, firstRow, "日期", queryDate);
		
		Row title = sheet.createRow(2);
		
		// 表头
		excelUtil.createRow(title, new String[]{"门店", "单品名称", "单品编码", "单品条码", "前一周销售数量", "库存数量", "库存天数"}, true);
		
		// 前一天
		LocalDate lastDay = LocalDate.parse(queryDate).minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " simple_bar_code, sell_num ");
		List<Sale> saleDayList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		if (CommonUtil.isBlank(saleDayList)) {
			throw new DataException("542");
		}
		
		// 上周一
		LocalDate lastMonday = LocalDate.parse(queryDate).minusWeeks(1L).with(DayOfWeek.MONDAY);
					
		// 上周末
		LocalDate lastSunday = lastMonday.with(DayOfWeek.SUNDAY);
		
		// 查询上周销量
		param.clear();
		param.put("startDate", lastMonday.toString());
		param.put("endDate", lastSunday.toString());
		param.put("storeCode", storeCode);
		param.put("column", " simple_bar_code, sell_num ");
		List<Sale> saleWeekList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
		if (CommonUtil.isBlank(saleDayList)) {
			throw new DataException("543");
		}
		
		int rowIndex = 3;
		// 填写数据
		for (int i = 0, size = stockList.size(); i < size; i++) {
			Stock stock = stockList.get(i);
			Row row = sheet.createRow(rowIndex);

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
					stock.getStoreName(),
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
		if (CommonUtil.isBlank(sysId)) {
			throw new DataException("538");
		}
		
		// 按系统名称和时间查询库存
		Map<String, Object> param = new HashMap<>(2);
		param.put("sysId", sysId);
		List<StoreStockModel> storeCodeModelList = queryListByObject(QueryId.QUERY_STORE_STOCK_MODEL_BY_PARAM, param);
		String sysName = storeCodeModelList.get(0).getStockList().get(0).getSysName();
		
		// 标题
		String title = sysName + "系统直营KA门店缺货日报表";
			
		// 表头
		String[] headers = new String[]{"系统", "门店编号", "门店", "单品数量", "库存低于3天的单品", "库存等于0的单品数量"};
		exportXStoreExcel(LocalDate.now().toString(), "系统门店表", title, sysName, headers, storeCodeModelList, output);
	}
	
	@Override
	public void exportRegionStoreExcel(String region, OutputStream output) throws IOException {
		if (CommonUtil.isBlank(region)) {
			throw new DataException("539");
		}
		
		// 按系统名称和时间查询库存
		Map<String, Object> param = new HashMap<>(2);
		param.put("region", region);
		List<StoreStockModel> storeCodeModelList = queryListByObject(QueryId.QUERY_STORE_STOCK_MODEL_BY_PARAM, param);
		
		// 标题
		String title = region + "大区直营KA门店缺货日报表";
			
		// 表头
		String[] headers = new String[]{"大区", "门店编号", "门店", "单品数量", "库存低于3天的单品", "库存等于0的单品数量"};
		exportXStoreExcel(LocalDate.now().toString(), "区域门店表", title, region, headers, storeCodeModelList, output);
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
	public void exportXStoreExcel(String queryDate, String sheetName, String title, String name, String[] header, 
			List<StoreStockModel> storeCodeModelList, OutputStream output) throws IOException {
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet(sheetName);
		
		Row dateRow = sheet.createRow(0);
		
		// 生成日期
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);
			
		Row headerRow = sheet.createRow(3);
				
		// 生成粗体居中标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, header.length);
		
		// 设置表头
		excelUtil.createRow(headerRow, header, true);
		
		if (CommonUtil.isBlank(storeCodeModelList)) {
			wb.write(output);
			output.flush();
			output.close();
		}
		
		// 前一天销售数据
		LocalDate lastDay = LocalDate.parse(queryDate).minusDays(1L);
		Map<String, Object> param = new HashMap<>(2);
		param.put("queryDate", lastDay.toString());
		param.put("column", " sell_num, simple_bar_code, store_code ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);
			
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
		for (i = 0, size = storeCodeModelList.size(); i < size; i++) {
			row = sheet.createRow(rowIndex);
			storeCodeModel = storeCodeModelList.get(i);
			storeName = storeCodeModel.getStoreName();
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
					if (tempSale.getStoreCode().equals(storeCode) && 
							tempSale.getSimpleBarCode().equals(stock.getSimpleBarCode())) {
						saleNumSum += tempSale.getSellNum() == null ? 0 : tempSale.getSellNum();
					}
				}
				// 库存天数
				double stockDayNum = 0;
				if (saleNumSum != 0) {
					stockDayNum = CommonUtil.setScale("0.00", stockPrice / saleNumSum);
				}
				if (stockDayNum < 3 && stockDayNum >0) {
					sumThreeStockDay ++;
				} else if (stockDayNum <= 0) {
					sumZeroStockDay ++;
				}			
			}
			String[] cellValue = new String []{
					name, 
					storeCode, // 门店编号
					storeName, //门店名称
					String.valueOf(stockList.size()), // 单品数量
					String.valueOf(sumThreeStockDay), //库存天数低于三天的单品数量
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
	public void exportMissFirstComExcel(OutputStream output) throws IOException {
		// TODO Auto-generated method stub
	}
	@Override
	public void exportStockExcel(String sysId, String stockNameStr, OutputStream output) throws Exception {
		logger.info("----->>>>自定义字段：{}<<<<------", stockNameStr);
		if (CommonUtil.isBlank(stockNameStr)) {
			throw new DataException("540");
		}
		String[] header = CommonUtil.parseIdsCollection(stockNameStr, ",");
		StringBuilder builder = new StringBuilder();
		String[] methodNameArray = exportUtil.joinColumn(StockEnum.class, builder, header);
		exportUtil.exportExcel(Stock.class, null, null, sysId, builder.toString(), 
				QueryId.QUERY_STOCK_BY_ANY_COLUMN, "库存信息表", methodNameArray, header, output);
	}
	@Override
	public void exportCompanyExcelBySys(OutputStream output) throws IOException {
		
	}
	@Override
	public ResultUtil exportRegionExcelBySys(String sysId, OutputStream output) throws IOException {
		// 按时间查询库存
		Map<String, Object> param = new HashMap<>(2);
		param.put("sysId", sysId);
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_PARAM, param);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("区域表一级表");
		
		// 标题
		String title = stockList.get(0).getSysName() + "直营KA分大区库存日报表";
		
		Row dateRow = sheet.createRow(0);
		
		// 生成日期
		String queryDate = LocalDate.now().toString();
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);
		
		List<String> brandList = stockList.parallelStream()
				.map(Stock::getBrand)
				.collect(Collectors.toList());
		Row brandRow = sheet.createRow(2);
		// 生成品牌
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", "全部", brandList.toArray());
			
		// 表头
		String[] headers = new String[]{"大区", "门店", "单品数量", "库存低于3天的单品", "单品数量", "库存等于0的单品数量"};
		Row headerRow = sheet.createRow(3);
		
		// 生成粗体剧中标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, headers.length);
		
		// 设置表头
		excelUtil.createRow(headerRow, headers, true);
		
		// 按大区分组
		Map<String, List<Stock>> stockMap = stockList.parallelStream()
				.collect(Collectors.groupingBy(Stock::getRegion));
	
		stockDataUtil.createStockDayData(sheet, stockMap, "regin", 4);
		
		wb.write(output);
		return ResultUtil.success();
	}
	@Override
	public ResultUtil exportRegionSecondExcelBySys(String sysId, String region, OutputStream output) throws IOException {
		// 按时间查询库存
		Map<String, Object> param = new HashMap<>(2);
		param.put("sysId", sysId);
		List<Stock> stockList = queryListByObject(QueryId.QUERY_STOCK_BY_PARAM, param);
		
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("区域表一级表");
		
		// 标题
		String title = stockList.get(0).getSysName() + "直营KA分大区库存日报表";
		
		Row dateRow = sheet.createRow(0);
		
		// 生成日期
		String queryDate = LocalDate.now().toString();
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);	
		
		List<String> brandList = stockList.parallelStream()
				.map(Stock::getBrand)
				.collect(Collectors.toList());
		Row brandRow = sheet.createRow(2);
		// 生成品牌
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", "全部", brandList.toArray());	
			
		// 表头
		String[] headers = new String[]{"大区", "门店", "单品数量", "库存低于3天的单品", "单品数量", "库存等于0的单品数量"};
		Row headerRow = sheet.createRow(3);
		
		// 生成粗体剧中标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, headers.length);
		
		// 设置表头
		excelUtil.createRow(headerRow, headers, true);
		
		// 按大区分组
		Map<String, List<Stock>> stockMap = stockList.parallelStream()
				.collect(Collectors.groupingBy(Stock::getRegion));
	
		/**
		 * TODO 下半部分数据
		 */
		
		return ResultUtil.success();
	}
	/**
	 * TODO
	 */
	@Override
	public void exportCompanyExcelByRegion(OutputStream output) throws IOException {
		// 按时间查询库存
		Map<String, Object> param = new HashMap<>(2);
		List<RegionStockModel> regionStockList = queryListByObject(QueryId.QUERY_REGION_STOCK_MODEL_BY_PARAM, param);

		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		SXSSFWorkbook wb = excelUtil.createWorkBook();
		Sheet sheet = wb.createSheet("公司一级表");

		// 标题
		String title = "全公司直营KA分系统库存日报表";

		Row dateRow = sheet.createRow(0);

		// 生成日期
		String queryDate = LocalDate.now().toString();
		stockDataUtil.createDateRow(wb, dateRow, "报表日期", queryDate);

		// 表头
		String[] headers = new String[] { "大区", "门店数量", "库存金额", "店均库存", "昨日销售", "库存天数" };
		Row headerRow = sheet.createRow(3);

		// 生成粗体剧中标题
		stockDataUtil.createBolderTitle(wb, sheet, title, 2, 0, headers.length);

		// 设置表头
		excelUtil.createRow(headerRow, headers, true);

		if (CommonUtil.isBlank(regionStockList)) {
			wb.write(output);
			output.flush();
			output.close();
		}

		LocalDate lastDay = LocalDate.parse(queryDate).minusDays(1L);
		param.put("queryDate", lastDay.toString());
		param.put("column", " region, store_code, sell_num ");
		List<Sale> saleList = queryListByObject(QueryId.QUERY_SALE_BY_ANY_COLUMN, param);

		int i, j, k;
		int size, stockSize, saleSize;
		RegionStockModel regionStock = null;
		List<Stock> stockList = null;
		Stock stock = null;
		Sale sale = null;

		// 门店编号
		String storeCode = null;

		// 大区
		String region = null;

		// 库存金额
		double stockPriceSum = 0;

		// 库存总数
		double stockNumSum = 0;

		// 昨日销量
		int sellNumSum = 0;

		// 库存天数
		double stockDay = 0;

		// 品牌集合
		Set<String> brandSet = new HashSet<>();
		String[][] rowValue = new String[regionStockList.size()][6];
		for (i = 0, size = regionStockList.size(); i < size; i++) {

			regionStock = regionStockList.get(i);
			stockList = regionStock.getStockList();
			region = regionStock.getRegion() == null ? "" : regionStock.getRegion();
			stockPriceSum = 0;
			stockNumSum = 0;
			sellNumSum = 0;
			for (j = 0, stockSize = stockList.size(); j < stockSize; j++) {
				stock = stockList.get(j);
				storeCode = stock.getStoreCode();
				stockPriceSum += stock.getStockPrice() == null ? 0 : stock.getStockPrice();
				stockNumSum += stock.getStockNum() == null ? 0 : stock.getStockNum();

				if (stock.getBrand() != null) {
					brandSet.add(stock.getBrand());
				}

				for (k = 0, saleSize = saleList.size(); k < saleSize; k++) {
					sale = saleList.get(k);
					if (region.equals(sale.getRegion()) && storeCode.equals(sale.getStoreCode())) {
						sellNumSum += sale.getSellNum() == null ? 0 : sale.getSellNum();
					}
				}
			}
			if (sellNumSum == 0) {
				stockDay = 0;
			} else {
				stockDay = stockPriceSum / sellNumSum;
			}
			rowValue[i] = new String[] { regionStock.getRegion(), // 大区
					String.valueOf(stockList.size()), // 门店数量
					String.valueOf(stockPriceSum), // 库存金额
					String.valueOf(stockNumSum / stockList.size()), // 店均库存
					String.valueOf(sellNumSum), // 昨日销量
					String.valueOf(stockDay) // 库存天数
			};

		}
		Row brandRow = sheet.createRow(2);

		
		/**
		 * TODO 生成品牌
		 */
		stockDataUtil.createDateRow(wb, brandRow, "品牌:", "全部", brandSet.toArray());

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
}
