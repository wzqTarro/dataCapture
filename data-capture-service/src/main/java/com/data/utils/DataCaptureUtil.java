package com.data.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.data.bean.Order;
import com.data.bean.Reject;
import com.data.bean.Sale;
import com.data.bean.Stock;
import com.data.bean.TemplateSupply;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.WebConstant;
import com.data.constant.enums.SupplyEnum;
import com.data.exception.DataException;
import com.data.service.impl.CommonServiceImpl;

/**
 * 数据抓取方法工具类
 * @author Alex
 *
 */
@Component
public class DataCaptureUtil extends CommonServiceImpl {
	
	private static Logger logger = LoggerFactory.getLogger(DataCaptureUtil.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 获取python抓取的数据
	 * @param common
	 * @param dataType
	 * @return
	 * @throws IOException 
	 */
	public String getDataByWeb(String queryDate, TemplateSupply supply, String dataType) throws IOException{		
		String start = null;
		String end = null;
		
		if (CommonUtil.isNotBlank(queryDate)) {
			if (!"1900-01-01".equals(queryDate)) {
				start = queryDate.trim();
				end = start;
				
				// 禁止查询当天
				if (Integer.parseInt(DateUtil.format(new Date(), "yyyyMMdd"))<=Integer.valueOf(start.replace("-", ""))) {
					throw new DataException("只可以查询今天之前的数据");
				}		
			}
		} else {
			throw new DataException("查询时间不能为空");
		}		
		if (false == supply.getIsVal()) {
			throw new DataException("供应链尚未开通");
		}
		// 订单
		if (WebConstant.ORDER.equals(dataType)) {
			if (supply.getSysId().equals(SupplyEnum.XSJ.getCode())) {
				throw new DataException("供应链此功能尚未开通");
			}
		}
		
		// 退单
		if (WebConstant.REJECT.equals(dataType)) {
			if (supply.getSysId().equals(SupplyEnum.BBG.getCode()) 
					|| supply.getSysId().equals(SupplyEnum.XSJ.getCode())
					|| supply.getSysId().equals(SupplyEnum.CQ_ZB.getCode())) {
				throw new DataException("供应链此功能尚未开通");
			}
		}
		
		// 销售
		if (WebConstant.SALE.equals(dataType)) {
			if (supply.getSysId().equals(SupplyEnum.BBG.getCode())) {
				throw new DataException("供应链此功能尚未开通");
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(WebConstant.WEB);
		sb.append(supply.getControllerName());
		sb.append(dataType);
		sb.append("/?");
		sb.append("day1=");
		sb.append(start);
		sb.append("&day2=");
		sb.append(end);
		sb.append("&user=");
		sb.append(supply.getLoginUserName());
		sb.append("&pwd=");
		sb.append(supply.getLoginPassword());
		sb.append("&venderCode=");
		sb.append(supply.getCompanyCode());
		logger.info("------>>>>>>抓取数据Url：" + sb.toString() + "<<<<<<--------");
		String json = restTemplate.getForObject(sb.toString(), String.class);
		
		
		
		// 测试数据
		// String json = FileUtils.readFileToString(new File("D:\\sale.txt"));
		// json转List
		// List<T> list = (List<T>) FastJsonUtil.jsonToList(json, clazz);
		// logger.info("抓取数据数量:" + list.size());
 		return json;
	}
	/**
	 * 读取订单excel
	 * @param json
	 * @param sysId
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<Order> getOrderExcel(String sysId) throws IOException, ParseException {
		List<Order> orderList = new ArrayList<>();
		// 永辉
		if (SupplyEnum.YH_AH.getCode().equals(sysId) || SupplyEnum.YH_BJ.getCode().equals(sysId)
				|| SupplyEnum.YH_CQ.getCode().equals(sysId)
				|| SupplyEnum.YH_GZ.getCode().equals(sysId)
				|| SupplyEnum.YH_HENAN.getCode().equals(sysId)
				|| SupplyEnum.YH_HEBEI.getCode().equals(sysId)
				|| SupplyEnum.YH_HUBEI.getCode().equals(sysId)
				|| SupplyEnum.YH_HUNAN.getCode().equals(sysId)
				|| SupplyEnum.YH_JX.getCode().equals(sysId)
				|| SupplyEnum.YH_SC.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\yh\\订单.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String receiptCode = row.getCell(1).getStringCellValue();
				String storeCode = row.getCell(4).getStringCellValue();
				String simpleBarCode = row.getCell(9).getStringCellValue();
				Double buyNum = row.getCell(12).getNumericCellValue();
				Double buyPriceWithRate = row.getCell(13).getNumericCellValue();
				String deliveryStartTime = row.getCell(6).getStringCellValue();
				String deliveryEndTime = row.getCell(7).getStringCellValue();
				String simpleCode = row.getCell(8).getStringCellValue();
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Order order = new Order();
				order.setBuyingPriceWithRate(BigDecimal.valueOf(buyPriceWithRate));
				order.setSimpleBarCode(simpleBarCode);
				order.setReceiptCode(receiptCode);
				order.setSimpleAmount(buyNum.longValue());
				order.setStoreCode(storeCode);
				order.setDeliverStartDate(format.parse(deliveryStartTime));
				order.setDeliverEndDate(format.parse(deliveryEndTime));
				order.setSimpleCode(simpleCode);
				orderList.add(order);
			}
			wb.close();
		} else if (SupplyEnum.BBG.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\bbg\\订单.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String receiptCode = row.getCell(1).getStringCellValue();
				String storeCode = row.getCell(6).getStringCellValue();
				storeCode = storeCode.substring(storeCode.lastIndexOf("(")+1, storeCode.lastIndexOf(")"));
				String simpleBarCode = row.getCell(9).getStringCellValue();
				Double buyNum = Double.valueOf(row.getCell(13).getStringCellValue());
				Double buyPriceWithRate = Double.valueOf(row.getCell(12).getStringCellValue());
				String deliveryStartTime = row.getCell(3).getStringCellValue();
				String deliveryEndTime = row.getCell(4).getStringCellValue();
				String simpleCode = row.getCell(7).getStringCellValue();
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Order order = new Order();
				order.setBuyingPriceWithRate(BigDecimal.valueOf(buyPriceWithRate));
				order.setSimpleBarCode(simpleBarCode);
				order.setReceiptCode(receiptCode);
				order.setSimpleAmount(buyNum.longValue());
				order.setStoreCode(storeCode);
				order.setDeliverStartDate(format.parse(deliveryStartTime));
				order.setDeliverEndDate(format.parse(deliveryEndTime));
				order.setSimpleCode(simpleCode);
				orderList.add(order);
			}
			wb.close();
		}
		return orderList;
	}
	
	/**
	 * 读取销售excel
	 * @param json
	 * @param sysId
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<Sale> getSaleExcel(String sysId) throws IOException, ParseException {
		List<Sale> saleList = new ArrayList<>();
		// 永辉
		if (SupplyEnum.YH_AH.getCode().equals(sysId) || SupplyEnum.YH_BJ.getCode().equals(sysId)
				|| SupplyEnum.YH_CQ.getCode().equals(sysId)
				|| SupplyEnum.YH_GZ.getCode().equals(sysId)
				|| SupplyEnum.YH_HENAN.getCode().equals(sysId)
				|| SupplyEnum.YH_HEBEI.getCode().equals(sysId)
				|| SupplyEnum.YH_HUBEI.getCode().equals(sysId)
				|| SupplyEnum.YH_HUNAN.getCode().equals(sysId)
				|| SupplyEnum.YH_JX.getCode().equals(sysId)
				|| SupplyEnum.YH_SC.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\yh\\销售.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String storeCode = row.getCell(0).getStringCellValue();
				String simpleCode = row.getCell(2).getStringCellValue();
				String simpleBarCode = row.getCell(3).getStringCellValue();
				Double sellNum = row.getCell(6).getNumericCellValue();
				Double sellPrice = row.getCell(7).getNumericCellValue();
				
				Sale sale = new Sale();
				sale.setStoreCode(storeCode);
				sale.setSimpleCode(simpleCode);
				sale.setSimpleBarCode(simpleBarCode);
				sale.setSellNum(sellNum.intValue());
				sale.setSellPrice(sellPrice);
				saleList.add(sale);
			}
			wb.close();
		} else if (SupplyEnum.CQ_ZB.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\cqzb\\销售.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String storeCode = row.getCell(1).getStringCellValue();
				String simpleCode = row.getCell(3).getStringCellValue();
				String simpleBarCode = row.getCell(5).getStringCellValue();
				Double sellNum = Double.valueOf(row.getCell(8).getStringCellValue());
				Double simplePrice = Double.valueOf(row.getCell(10).getStringCellValue());
				Double sellPrice = sellNum * simplePrice;
				
				Sale sale = new Sale();
				sale.setStoreCode(storeCode);
				sale.setSimpleCode(simpleCode);
				sale.setSimpleBarCode(simpleBarCode);
				sale.setSellNum(sellNum.intValue());
				sale.setSellPrice(sellPrice);
				saleList.add(sale);
			}
			wb.close();
		} else if (SupplyEnum.XSJ.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\xsj\\销售.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String storeCode = row.getCell(0).getStringCellValue();
				storeCode = storeCode.substring(0, 4);
				String simpleCode = row.getCell(1).getStringCellValue();
				String simpleBarCode = row.getCell(2).getStringCellValue();
				Double sellNum = Double.valueOf(row.getCell(4).getStringCellValue());
				Double simplePrice = Double.valueOf(row.getCell(6).getStringCellValue());
				Double sellPrice = sellNum * simplePrice;
				
				Sale sale = new Sale();
				sale.setStoreCode(storeCode);
				sale.setSimpleCode(simpleCode);
				sale.setSimpleBarCode(simpleBarCode);
				sale.setSellNum(sellNum.intValue());
				sale.setSellPrice(sellPrice);
				saleList.add(sale);
			}
			wb.close();
		}
		return saleList;
	}
	
	/**
	 * 读取库存excel
	 * @param json
	 * @param sysId
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static List<Stock> getStockExcel(String sysId) throws IOException, ParseException {
		List<Stock> stockList = new ArrayList<>();
		// 永辉
		if (SupplyEnum.YH_AH.getCode().equals(sysId) || SupplyEnum.YH_BJ.getCode().equals(sysId)
				|| SupplyEnum.YH_CQ.getCode().equals(sysId)
				|| SupplyEnum.YH_GZ.getCode().equals(sysId)
				|| SupplyEnum.YH_HENAN.getCode().equals(sysId)
				|| SupplyEnum.YH_HEBEI.getCode().equals(sysId)
				|| SupplyEnum.YH_HUBEI.getCode().equals(sysId)
				|| SupplyEnum.YH_HUNAN.getCode().equals(sysId)
				|| SupplyEnum.YH_JX.getCode().equals(sysId)
				|| SupplyEnum.YH_SC.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\yh\\库存.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String storeCode = row.getCell(0).getStringCellValue();
				String simpleCode = row.getCell(2).getStringCellValue();
				String simpleBarCode = row.getCell(3).getStringCellValue();
				Double stockNum = row.getCell(12).getNumericCellValue();
				Double stockPrice = row.getCell(13).getNumericCellValue();
				
				Stock stock = new Stock();
				stock.setStoreCode(storeCode);
				stock.setSimpleCode(simpleCode);
				stock.setSimpleBarCode(simpleBarCode);
				stock.setStockNum(stockNum.intValue());
				stock.setStockPrice(stockPrice);
				stockList.add(stock);
			}
			wb.close();
		} else if (SupplyEnum.CQ_ZB.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\cqzb\\库存.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String storeCode = row.getCell(4).getStringCellValue();
				String simpleCode = row.getCell(1).getStringCellValue();
				String simpleBarCode = row.getCell(3).getStringCellValue();
				Double stockNum = Double.valueOf(row.getCell(14).getStringCellValue());
				Double stockPrice = Double.valueOf(row.getCell(15).getStringCellValue());
				
				Stock stock = new Stock();
				stock.setStoreCode(storeCode);
				stock.setSimpleCode(simpleCode);
				stock.setSimpleBarCode(simpleBarCode);
				stock.setStockNum(stockNum.intValue());
				stock.setStockPrice(stockPrice);
				stockList.add(stock);
			}
			wb.close();
		} else if (SupplyEnum.BBG.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\bbg\\库存.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String storeCode = row.getCell(2).getStringCellValue();
				storeCode = storeCode.substring(storeCode.lastIndexOf("(")+1, storeCode.lastIndexOf(")"));
				String simpleCode = row.getCell(4).getStringCellValue();
				simpleCode = simpleCode.substring(simpleCode.lastIndexOf("(")+1, simpleCode.lastIndexOf(")"));
				Double stockNum = Double.valueOf(row.getCell(6).getStringCellValue());
				Double stockPrice = Double.valueOf(row.getCell(9).getStringCellValue());
				
				Stock stock = new Stock();
				stock.setStoreCode(storeCode);
				stock.setSimpleCode(simpleCode);
				stock.setStockNum(stockNum.intValue());
				stock.setStockPrice(stockPrice);
				stockList.add(stock);
			}
			wb.close();
		} else if (SupplyEnum.XSJ.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\xsj\\库存.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String storeCode = row.getCell(0).getStringCellValue();
				String simpleCode = row.getCell(3).getStringCellValue();
				String simpleBarCode = row.getCell(5).getStringCellValue();
				Double stockNum = Double.valueOf(row.getCell(7).getStringCellValue());
				Double stockPrice = Double.valueOf(row.getCell(8).getStringCellValue());
				
				Stock stock = new Stock();
				stock.setStoreCode(storeCode);
				stock.setSimpleCode(simpleCode);
				stock.setSimpleBarCode(simpleBarCode);
				stock.setStockNum(stockNum.intValue());
				stock.setStockPrice(stockPrice);
				stockList.add(stock);
			}
			wb.close();
		}
		return stockList;
	}
	
	/**
	 * 读取退单excel
	 * @param json
	 * @param sysId
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public List<Reject> getRejectExcel(String sysId) throws IOException, ParseException {
		List<Reject> rejectList = new ArrayList<>();
		// 永辉
		if (SupplyEnum.YH_AH.getCode().equals(sysId) || SupplyEnum.YH_BJ.getCode().equals(sysId)
				|| SupplyEnum.YH_CQ.getCode().equals(sysId)
				|| SupplyEnum.YH_GZ.getCode().equals(sysId)
				|| SupplyEnum.YH_HENAN.getCode().equals(sysId)
				|| SupplyEnum.YH_HEBEI.getCode().equals(sysId)
				|| SupplyEnum.YH_HUBEI.getCode().equals(sysId)
				|| SupplyEnum.YH_HUNAN.getCode().equals(sysId)
				|| SupplyEnum.YH_JX.getCode().equals(sysId)
				|| SupplyEnum.YH_SC.getCode().equals(sysId)) {
			String filePath = "E:\\excel\\yh\\退单.xls";
			FileInputStream input = new FileInputStream(filePath);
			Workbook wb = new XSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				String receiptCode = row.getCell(1).getStringCellValue();
				String rejectDepartmentId = row.getCell(4).getStringCellValue();
				String simpleCode = row.getCell(7).getStringCellValue();
				String simpleBarCode = row.getCell(8).getStringCellValue();
				Double rejectNum = row.getCell(10).getNumericCellValue();
				Double rejectWithPrice = row.getCell(11).getNumericCellValue();
				String rejectTime = row.getCell(6).getStringCellValue();
				
				SimpleDateFormat format = new SimpleDateFormat("yyyyy-MM-dd");
				Reject reject = new Reject();
				reject.setRejectDate(format.parse(rejectTime));
				reject.setSimpleCode(simpleCode);
				reject.setRejectDepartmentId(rejectDepartmentId);
				reject.setSimpleBarCode(simpleBarCode);
				reject.setSimpleAmount(rejectNum.longValue());
				reject.setRejectPriceWithRate(new BigDecimal(rejectWithPrice));
				reject.setReceiptCode(receiptCode);
				reject.setRejectPrice(new BigDecimal(rejectWithPrice));
				rejectList.add(reject);
			}
			wb.close();
		}
		return rejectList;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		getSaleExcel("by321");
	}
	/**
	 * 设置分页
	 * @param list
	 * @param common
	 * @return
	 */
	public <T> PageRecord<T> setPageRecord(List<T> list, Integer limit) {
		PageRecord<T> pageRecord = new PageRecord<>();			
		pageRecord.setPageNum(1);
		if (null == limit ||  0 == limit ) {
			pageRecord.setPageSize(CommonValue.SIZE);
		} else {
			pageRecord.setPageSize(limit);
		}
		pageRecord.setPageTotal(list.size());
		if (list.size() > pageRecord.getPageSize()) {
			pageRecord.setList(list.subList((pageRecord.getPageNum() - 1)*pageRecord.getPageSize(), pageRecord.getPageSize()));
		} else {
			pageRecord.setList(list.subList((pageRecord.getPageNum() - 1)*pageRecord.getPageSize(), list.size()));
		}
		return pageRecord;
	}
	/**
	 * 批量插入数据库
	 * @param dataList
	 * @param clazz
	 * @param mapper
	 * @return
	 */
	public <T> void insertData(List<T> dataList, String mapper) {
		double rowNum = 1000;
		double size = Math.ceil(dataList.size() / rowNum);
		
		if (size == 1) {
			insert(mapper, dataList.subList(0, dataList.size()));
		} else {
			insert(mapper, dataList.subList(0, (int)rowNum));
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		try {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					
					for (int i = 1; i < size; i++) {
						if (i == (size-1)) {
							insert(mapper, dataList.subList(i * (int)rowNum, dataList.size()));
							break;
						}
						insert(mapper, dataList.subList(i * (int)rowNum, i * (int)rowNum + (int)rowNum));
					}
				}
			});
		} finally {
			if (null != executorService) {
				executorService.shutdown();
			}
		}
	}
}
