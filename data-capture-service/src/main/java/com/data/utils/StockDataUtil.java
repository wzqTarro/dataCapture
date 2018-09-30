package com.data.utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 生成蓝色字体样式日期行
	 * @param wb
	 * @param sheet 
	 * @param rowIndex 行号
	 * @param cellName 
	 * @param cellValue
	 */
	public void createDateRow(Workbook wb, Row row, String cellName, Object...cellValue) {
		Cell firstCell = row.createCell(0);
		ExcelUtil<Stock> excelUtil = new ExcelUtil<>();
		// 蓝色字体
		CellStyle firstCellStyle = wb.createCellStyle();
		firstCellStyle.setFont(excelUtil.getColorFont(wb, IndexedColors.LIGHT_BLUE.index));
		
		firstCell.setCellValue(cellName);
		firstCell.setCellStyle(firstCellStyle);
		
		for (int i = 0, size = cellValue.length; i < size; i++) {
			Cell firstCell2 = row.createCell(i+1);
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
		
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, cellWidth - 1));

		// 粗体、居中
		CellStyle cellStyle = excelUtil.getBolderTitle(wb);
		
		// 标题
		Row titleRow = sheet.createRow(rowIndex);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(title);
		titleCell.setCellStyle(cellStyle);
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
			excelUtil.createRow(row, cellValue, false);
			rowIndex++;
		}
	}
}
