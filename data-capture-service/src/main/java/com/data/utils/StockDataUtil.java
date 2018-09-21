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
import org.springframework.beans.factory.annotation.Autowired;
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
	 * 生成缺货门店表的上部
	 * @param wb 
	 * @param sheet 工作簿
	 * @param queryDate 查询时间
	 * @param title 标题
	 * @return 起始行数
	 */
	public int createMissStockTop(SXSSFWorkbook wb, Sheet sheet, String queryDate, String title) {
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		Row firstRow = sheet.createRow(0);
		Cell firstCell = firstRow.createCell(0);
		
		// 蓝色字体
		CellStyle firstCellStyle = wb.createCellStyle();
		firstCellStyle.setFont(excelUtil.getColorFont(wb, HSSFColor.BLUE.index));
		
		firstCell.setCellValue("报表日期");
		firstCell.setCellStyle(firstCellStyle);
		
		Cell firstCell2 = firstRow.createCell(1);
		firstCell2.setCellValue(queryDate);
		firstCell2.setCellStyle(firstCellStyle);
		
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

		// 粗体、居中
		CellStyle cellStyle = excelUtil.getBolderTitle(wb);
		
		// 标题
		Row titleRow = sheet.createRow(2);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(title);
		titleCell.setCellStyle(cellStyle);
		
		return 3;
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
	public double calculateStockDay(String queryDay, String simpleBarCode, String storeName, Double stockPrice) {
		// 查询前一天
		LocalDate lastDay = LocalDate.parse(queryDay).minusDays(1L);
		
		Map<String, Object> param = new HashMap<>(3);
		param.put("queryDate", lastDay.toString());
		param.put("simpleBarCode", simpleBarCode);
		param.put("storeName", storeName);
		
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
}
