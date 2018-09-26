package com.data.utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.constant.dbSql.QueryId;
import com.data.service.impl.CommonServiceImpl;

/**
 * 库存计算
 * @author Administrator
 *
 */
@Component
public class StockDataUtil extends CommonServiceImpl{
	
	/**
	 * 生成蓝色字体样式日期行
	 * @param wb
	 * @param sheet 
	 * @param rowIndex 行号
	 * @param cellName 
	 * @param cellValue
	 */
	public void createDateRow(SXSSFWorkbook wb, Sheet sheet, int rowIndex, String cellName, Object...cellValue) {
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Row firstRow = sheet.createRow(rowIndex);
		Cell firstCell = firstRow.createCell(0);
		
		// 蓝色字体
		CellStyle firstCellStyle = wb.createCellStyle();
		firstCellStyle.setFont(excelUtil.getColorFont(wb, HSSFColor.BLUE.index));
		
		firstCell.setCellValue(cellName);
		firstCell.setCellStyle(firstCellStyle);
		
		for (int i = 0, size = cellValue.length; i < size; i++) {
			Cell firstCell2 = firstRow.createCell(i+1);
			firstCell2.setCellValue(cellValue[i].toString());
			firstCell2.setCellStyle(firstCellStyle);
		}
	}
	
	/**
	 * 生成居中粗体标题
	 * @param wb
	 * @param sheet 工作簿
	 * @param title 标题
	 * @param rowIndex 行号
	 * @param cellWidth 合并列数
	 */
	public void createBolderTitle(SXSSFWorkbook wb, Sheet sheet, String title, int rowIndex, int cellWidth) {
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, cellWidth));

		// 粗体、居中
		CellStyle cellStyle = excelUtil.getBolderTitle(wb);
		
		// 标题
		Row titleRow = sheet.createRow(rowIndex);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(title);
		titleCell.setCellStyle(cellStyle);
	}
	/**
	 * 生成缺货门店数据
	 * @param sheet 工作簿
	 * @param queryDate 查询日期
	 * @param firstCellValue 第一列值
	 * @param stockMap 数据
	 * @param index 起始行号
	 */
	public void createMissStockMessage(List<Stock> stockList, Sheet sheet, String queryDate, 
			String firstCellValue, int index) {
		Map<String, List<Stock>> stockMap = stockList.parallelStream()
				.collect(Collectors.groupingBy(Stock::getStoreCode));
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		index ++;
		for (List<Stock> currentStock : stockMap.values()) {
			Row row = sheet.createRow(index);
			index++;
			Stock stock = currentStock.get(0);
			String storeName = stock.getStoreName();
			String simpleNum = String.valueOf(currentStock.size());
			
			// 库存天数低于三天的单品数量
			int sumThreeStockDay = 0;
			
			// 库存天数等于0的单品数量
			int sumZeroStockDay = 0;
			
			// 库存天数低于三天的单品总库存数量
			int sumSimpleStock = 0;
			for (int i = 0, size = currentStock.size(); i < size; i++) {
				Stock temp = currentStock.get(i);
				
				// 库存天数
				double stockDay = calculateStockDay(queryDate, temp.getSimpleBarCode(), temp.getStoreName(), temp.getStockPrice());
				if (stockDay < 3 && stockDay >0) {
					sumThreeStockDay ++;
				} else if (stockDay == 0) {
					sumZeroStockDay ++;
				}
				
			}
			String[] cellValue = new String []{
				firstCellValue, // 系统名
				stock.getStoreName(), //门店名称
				"", // 单品数量
				String.valueOf(sumThreeStockDay), //库存天数低于三天的单品数量
				"", // 单品数量
				String.valueOf(sumZeroStockDay) // 库存天数低于三天的单品总库存数量
			};
			excelUtil.createRow(row, cellValue);
		}
	}

	/**
	 * 计算库存天数
	 * @param queryDay 查询时间
	 * @param simpleBarCode 单品条码
	 * @param storeName 门店名称
	 * @param stockPrice 库存金额
	 */
	public double calculateStockDay(String queryDay, String simpleBarCode, String storeCode, Double stockPrice) {
		// 查询前一天
		LocalDate lastDay = LocalDate.parse(queryDay).minusDays(1L);
		
		Map<String, Object> param = new HashMap<>(3);
		param.put("queryDate", lastDay.toString());
		if (CommonUtil.isBlank(simpleBarCode)) {
			param.put("simpleBarCode", simpleBarCode);
		}	
		param.put("storeCode", storeCode);
		
		// 查询前一天销量
		List<Sale> saleDayList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, param);
	
		// 前一天单品的销售总量
		Integer sumSaleNumByDay = 0;
					
		// 如果前一天存在销售记录，否则默认为0
		if (CommonUtil.isNotBlank(saleDayList)) {
			sumSaleNumByDay = CommonUtil.toIntOrZero(saleDayList.stream().collect(Collectors.summingInt(s -> s.getSellNum())));
		}
					
		// 库存金额
		stockPrice = CommonUtil.toDoubleOrZero(stockPrice);
					
		// 库存天数
		double stockDayNum = 0;
		if (sumSaleNumByDay != 0 && sumSaleNumByDay != null) {
			stockDayNum = CommonUtil.setScale("0.00", stockPrice / sumSaleNumByDay);
		}
		
		return stockDayNum;
		
	}
	/**
	 * 生成库存日报表
	 * @param sheet
	 * @param stockMap 导出数据
	 * @param paramKey 查询字段
	 */
	public void createStockDayData(Sheet sheet, Map<String, List<Stock>> stockMap, String paramKey, int rowIndex) {
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		int index = 0;
		Map<String, Object> param = new HashMap<>(2);
		for (Map.Entry<String, List<Stock>> m : stockMap.entrySet()) {
			List<Stock> tempList = m.getValue();
			
			Stock stock = tempList.get(index);
			index ++;
			
			// 系统名
			String sysName = stock.getSysName();
			
			// 门店数量
			long storeNum = tempList.stream().map(Stock::getStoreCode).count();
			
			// 库存总金额
			double stockPriceSum = tempList.stream().mapToDouble(Stock::getStockPrice).sum();
			
			// 库存总数量
			int stockNumSum = tempList.stream().mapToInt(Stock::getStockNum).sum();
	
			// 店均库存
			double stockAverage = stockNumSum/storeNum;
			
			// 昨日销售信息
			LocalDate lastDay = LocalDate.now().minusDays(1L);
			param.clear();
			param.put("queryDate", lastDay);
			param.put(paramKey, m.getKey());
			List<Sale> lastSaleList = queryListByObject(QueryId.QUERY_SALE_BY_PARAM, param);
			
			// 昨日销售数量
			int lastSaleNum = lastSaleList.stream().mapToInt(Sale::getSellNum).sum();
			
			// 库存天数
			double stockDay = stockPriceSum/lastSaleNum;
			
			String[] cellValue = new String[]{
					sysName, // 系统名称
					String.valueOf(storeNum), // 门店数量
					String.valueOf(stockPriceSum), // 库存金额
					String.valueOf(stockAverage), // 店均库存
					String.valueOf(lastSaleNum), // 昨日销量
					String.valueOf(stockDay) + "天", // 库存天数
			};
			
			// 行
			Row row = sheet.createRow(rowIndex);
			excelUtil.createRow(row, cellValue);
			rowIndex++;
		}
	}
}
