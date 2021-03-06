package com.data.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.data.exception.DataException;

/**
 * excel操作工具类
 * @author Alex
 *
 */
public class ExcelUtil<T> {
	
	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
	
	/**excel2003最大支持列数**/
	public static final int MAX_COL_COUNT_2003 = 256;
	
	/**excel2003最大支持行数**/
	public static final int MAX_ROW_COUNT_2003 = 65536;
	
	/**excel2007最大支持列数**/
	public static final int MAX_COL_COUNT_2007 = 16384;
	
	/**excel2007最大支持行数**/
	public static final int MAX_ROW_COUNT_2007 = 1048576;
	
	public String[] orderExcelHeaderArray = {"sysId","sysName","region","provinceArea","storeCode","storeName","receiptCode","simpleCode","simpleBarCode","stockCode","simpleName","taxRate","boxStandard","simpleAmount","buyingPriceWithRate","buyingPrice","deliverStartDate","deliverEndDate","deliverAddress","discountPrice","discountStartDate","discountEndDate","orderEffectiveJudge","balanceWay","diffPriceDiscount","diffPriceDiscountTotal","discountAlarmFlag","contractPrice","diffPriceContract","diffPriceContractTotal","contractAlarmFlag","diffPrice","remark"};
	
	public String[] promotionDetailHeaderArray = {"sysId","sysName", "effectiveStore", "exceptStore","supplyPriceStartDate","supplyPriceEndDate","sellPriceStartDate","sellPriceEndDate","supplyName","supplyType","controlType","compensationType","compensationCost","productCode","originPrice","normalSupplyPrice","supplyPrice","supplyOrderType","normalSellPrice","supplySellPrice","profit","discount"};
	
	public String[] rejectHeaderArray = {"sysId","sysName","rejectDepartmentId","region","provinceArea","rejectDepartmentName","receiptCode","simpleCode","simpleBarCode","stockCode","simpleName","taxRate","simpleAmount","rejectPriceWithRate","rejectPrice","rejectDate","discountPrice","discountStartDate","discountEndDate","diffPriceDiscount","diffPriceDiscountTotal","discountAlarmFlag","contractPrice","diffPriceContract","diffPriceContractTotal","contractAlarmFlag","diffPrice","remark"};
	
	public String[] saleHeaderArray = {"sysId","sysName","storeCode","storeName","region","provinceArea","ascription","ascriptionSole","simpleCode","simpleBarCode","stockCode","simpleName","brand","series","dayNight","material","piecesNum","boxStandard","stockNo","sellNum","sellPrice","createTime","remark","storeManager","localName"};
	
	public String[] simpleCodeHeaderArray = {"simple_name","bar_code","bbg","rrl","jrd","yc","hnth","os","bl","bjhl","ws","gcs","oycb","jh","drf","wem","zb","cb"};
	
	public String[] stockHeaderArray = {"sysId","sysName","storeCode","storeName","region","provinceArea","ascription","ascriptionSole","simpleCode","simpleBarCode","stockCode","simpleName","brand","classify","series","dayNight","material","piecesNum","boxStandard","stockNo","stockNum","taxCostPrice","stockPrice","createTime","remark","localName"};
	
	public String[] templateOrderHeaderArray = {"order_id","no","contract_code","good_name","good_bar_code","good_code","order_type","good_type","order_shop","address","sell_price","count","simple_count","little_count","real_count","unit","stage_count","order_state","end_time","create_time","operator_name","company_code","brand","part_content","persent_num","contain_tax_price","contain_tax_money","remark"};
	
	public String[] templateProductHeaderArray = {"productId","sysId","sysName","simpleCode","simpleBarCode","stockCode","simpleName","standardName","brand","classify","series","func","material","piecesNum","boxStandard","stockNo","sellPrice","excludeTaxPrice","includeTaxPrice"};
	
	public String[] templateStoreExcelHeaderArray = {"sysId","sysName","storeCode","orderStoreName","returnStoreName","saleStoreName","standardStoreName","storeMarket","storeCity","storeManager","logisticsModel","practiceTime","distributionCode","distributionName","distributionUser","region","provinceArea","ascription","ascriptionSole"};
	
	public String[] templateSupplyHeaderArray = {"region","sys_name","url","login_user_name","login_password","company_code","controller_name","is_val","sys_id","parent_id"};
	
	public String[] userHeaderArray = {"work_no","username","password","gender","mobile_code","email","department","position","login_times","last_login_date","remark","is_alive"};
	
	/**
	 * 导出2003excel表
	 * @param title 标题
	 * @param collection 集合
	 * @param out 输出流
	 * @throws Exception 
	 */
	public void exportExcel2003(String title, Collection<T> collection, OutputStream out) throws Exception {
		excel2003(title, null, collection, out);
	}
	
	/**
	 * 导出2003excel表
	 * @param title 标题
	 * @param header 头部
	 * @param collection 集合
	 * @param out 输出流
	 * @throws Exception 
	 */
	public void exportExcel2003(String title, String[] header, Collection<T> collection, OutputStream out) throws Exception {
		excel2003(title, header, collection, out);
	}
	
	/**
	 * 导出2003excel表
	 * @param title 标题
	 * @param header 头部
	 * @param collection 集合
	 * @param out 输出流
	 * @throws Exception 
	 */
	public void excel2003(String title, String[] header, Collection<T> collection, OutputStream out) throws Exception {
		if(null != header && header.length > MAX_COL_COUNT_2003) {
			logger.info("--->>>导出excel2003表头部大于最大限定列值<<<---");
			throw new DataException("507");
		}
		if(null != collection && collection.size() > MAX_ROW_COUNT_2003) {
			logger.info("--->>>导出excel2003内容大于最大限定行值<<<---");
			throw new DataException("508");
		}
		if(CommonUtil.isNotBlank(title)) {
			//将数据放置在磁盘中 减缓压力
			SXSSFWorkbook workBook = new SXSSFWorkbook();
			Sheet sheet = workBook.createSheet(title);
			
			Row row = null;
			if(CommonUtil.isNotBlank(header)) {
				//构建头部
				row = sheet.createRow(0);
				for(int i = 0; i < header.length; i++) {
					Cell cell = row.createCell(i);
					HSSFRichTextString text = new HSSFRichTextString(header[i]);
					cell.setCellValue(text);
				}				
			}
			//构建内容
			Iterator<T> it = collection.iterator();
			int index = 0;
			while(it.hasNext()) {
				index++;
				row = sheet.createRow(index);
				T t = (T) it.next();
				Field[] fields = t.getClass().getDeclaredFields();
				Field[] newFields = new Field[fields.length - 1];
				//去除序列化编号一列
				System.arraycopy(fields, 1, newFields, 0, fields.length - 1);
				
				int cellIndex = 0;
				for(int i = 0; i < newFields.length; i++) {
					Field field = newFields[i];
					String fieldName = field.getName();
					if("serialVersionUID".equals(fieldName)) {
						continue;
					}
					String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					Class<? extends Object> clazz = t.getClass();
					Method method = clazz.getMethod(methodName, new Class[] {});
					//动态调用方法得到字段值
					Object value = method.invoke(t, new Object[] {});
					if(value == null) {
						value = "";
					}
					
					String text = "";
					if(value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
						text = sdf.format(date);
					} else if(value instanceof Boolean) {
						text = String.valueOf((Boolean) value);
					} else {
						text = value.toString();
					}
					
					Cell cell = row.createCell(cellIndex);
					if(CommonUtil.isNotBlank(text)) {
						//如果是非负浮点数则转换
						Pattern pattern = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = pattern.matcher(text);
						if(matcher.matches()) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							HSSFRichTextString richText = new HSSFRichTextString(text);
							cell.setCellValue(richText);
						}
					}
					cellIndex ++;
				}
			}
			workBook.write(out);
			out.close();
		}
	}
	
	/**
	 * 导出2007excel表
	 * @param title 标题
	 * @param collection 集合
	 * @param out 输出流
	 * @throws Exception 
	 */
	public void exportExcel2007(String title, Collection<T> collection, OutputStream out) throws Exception {
		excel2007(title, null, collection, out);
	}
	
	/**
	 * 导出2007excel表
	 * @param title 标题
	 * @param header 头部
	 * @param collection 集合
	 * @param out 输出流
	 * @throws Exception 
	 */
	public void exportExcel2007(String title, String[] header, Collection<T> collection, OutputStream out) throws Exception {
		excel2007(title, header, collection, out);
	}
	
	/**
	 * 导出2007excel表
	 * @param title 标题
	 * @param header 头部
	 * @param collection 集合
	 * @param out 输出流
	 * @throws Exception 
	 */
	public void excel2007(String title, String[] header, Collection<T> collection, OutputStream out) throws Exception {
		if(null != header && header.length > MAX_COL_COUNT_2007) {
			logger.info("--->>>导出excel表头部大于最大限定列值<<<---");
			throw new DataException("509");
		}
		if(null != collection && collection.size() > MAX_ROW_COUNT_2007) {
			logger.info("--->>>导出excel内容大于最大限定行值<<<---");
			throw new DataException("510");
		}
		if(CommonUtil.isNotBlank(title)) {
			SXSSFWorkbook workBook = new SXSSFWorkbook();
			Sheet sheet = workBook.createSheet(title);
			
			Row row = null;
			//构建头部
			if(CommonUtil.isNotBlank(header)) {
				row = sheet.createRow(0);
				for(int i = 0; i < header.length; i++) {
					Cell cell = row.createCell(i);
					XSSFRichTextString text = new XSSFRichTextString(header[i]);
					cell.setCellValue(text);
				}				
			}
			//构建内容
			Iterator<T> it = collection.iterator();
			int index = 0;
			while(it.hasNext()) {
				index++;
				row = sheet.createRow(index);
				T t = (T) it.next();
				Field[] fields = t.getClass().getDeclaredFields();
				Field[] newFields = new Field[fields.length - 1];
				//去除序列化编号一列
				System.arraycopy(fields, 1, newFields, 0, fields.length - 1);
				for(int i = 0; i < newFields.length; i++) {
					Field field = newFields[i];
					String fieldName = field.getName();
					if("serialVersionUID".equals(fieldName)) {
						continue;
					}
					String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					Class<? extends Object> clazz = t.getClass();
					Method method = clazz.getMethod(methodName, new Class[] {});
					//动态调用方法得到字段值
					Object value = method.invoke(t, new Object[] {});
					if(value == null) {
						value = "";
					}
					
					String text = "";
					if(value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
						text = sdf.format(date);
					} else if(value instanceof Boolean) {
						text = String.valueOf((Boolean) value);
					} else {
						text = value.toString();
					}
					
					Cell cell = row.createCell(i);
					if(CommonUtil.isNotBlank(text)) {
						//如果是非负浮点数则转换
						Pattern pattern = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = pattern.matcher(text);
						if(matcher.matches()) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							XSSFRichTextString richText = new XSSFRichTextString(text);
							cell.setCellValue(richText);
						}
					}
				}
			}
			workBook.write(out);
			out.close();
		}
	}
	
	/**
	 * excel2007
	 * @param headers
	 * @param dataList
	 * @param codeList
	 * @param out
	 * @throws IOException 
	 */
	public void excel2007(String[] header, List<Map<String, Object>> dataList, String title, List<String> codeList, OutputStream out) throws IOException {
		if(null != header && header.length > MAX_COL_COUNT_2007) {
			logger.info("--->>>导出excel表头部大于最大限定列值<<<---");
			throw new DataException("509");
		}
		if(null != dataList && dataList.size() > MAX_ROW_COUNT_2007) {
			logger.info("--->>>导出excel内容大于最大限定行值<<<---");
			throw new DataException("510");
		}
		if(CommonUtil.isNotBlank(title)) {
			SXSSFWorkbook workBook = new SXSSFWorkbook();
			Sheet sheet = workBook.createSheet(title);
			
			Row row = null;
			//构建头部
			Date nowDate = DateUtil.getSystemDate();
			List<String> daysList = DateUtil.getMonthDays(nowDate);
			if(CommonUtil.isNotBlank(header)) {
				row = sheet.createRow(0);
				List<String> headerList = new ArrayList<>(10);
				for(String str : header) {
					headerList.add(str);
				}
				//将动态日期添加至头部
				headerList.addAll(daysList);
				header = headerList.toArray(new String[0]);
				for(int i = 0; i < header.length; i++) {
					Cell cell = row.createCell(i);
					XSSFRichTextString text = new XSSFRichTextString(header[i]);
					cell.setCellValue(text);
				}				
			}
			//构建内容
			Iterator<Map<String, Object>> it = dataList.iterator();
			int index = 0;
			while(it.hasNext()) {
				index++;
				row = sheet.createRow(index);
				Map<String, Object> map = it.next();
				
				for(int i = 0; i < header.length; i++) {
					Object value;
					codeList.addAll(daysList);
					for(Map.Entry<String, Object> entry : map.entrySet()) {
						if(codeList.get(i).equals(entry.getKey())) {
							value = entry.getValue();
							if(value == null) {
								value = "";
							}
							
							String text = "";
							if(value instanceof Date) {
								Date date = (Date) value;
								SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
								text = sdf.format(date);
							} else if(value instanceof Boolean) {
								text = String.valueOf((Boolean) value);
							} else {
								text = value.toString();
							}
							
							Cell cell = row.createCell(i);
							if(CommonUtil.isNotBlank(text)) {
								//如果是非负浮点数则转换
								Pattern pattern = Pattern.compile("^//d+(//.//d+)?$");
								Matcher matcher = pattern.matcher(text);
								if(matcher.matches()) {
									cell.setCellValue(Double.parseDouble(text));
								} else {
									XSSFRichTextString richText = new XSSFRichTextString(text);
									cell.setCellValue(richText);
								}
							}
							break;
						} 
					}
				}
			}
			workBook.write(out);
			workBook.close();
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 按条件导出模板
	 * @param header
	 * @param dataList
	 * @param title
	 * @param codeList
	 * @param out
	 * @throws Exception 
	 */
	public void exportTemplateByMap(String[] header, List<Map<String, Object>> dataList, String title, List<String> codeList, OutputStream out) throws Exception {
		if(null != header && header.length > MAX_COL_COUNT_2007) {
			logger.info("--->>>导出excel表头部大于最大限定列值<<<---");
			throw new DataException("509");
		}
		if(null != dataList && dataList.size() > MAX_ROW_COUNT_2007) {
			logger.info("--->>>导出excel内容大于最大限定行值<<<---");
			throw new DataException("510");
		}
		if(CommonUtil.isNotBlank(title)) {
			SXSSFWorkbook workBook = new SXSSFWorkbook();
			Sheet sheet = workBook.createSheet(title);
			
			Row row = null;
			//构建头部
			if(CommonUtil.isNotBlank(header)) {
				row = sheet.createRow(0);
				for(int i = 0; i < header.length; i++) {
					Cell cell = row.createCell(i);
					XSSFRichTextString text = new XSSFRichTextString(header[i]);
					cell.setCellValue(text);
				}				
			}
			//构建内容
			Iterator<Map<String, Object>> it = dataList.iterator();
			int index = 0;
			while(it.hasNext()) {
				index++;
				row = sheet.createRow(index);
				Map<String, Object> map = it.next();
				
				for(int i = 0; i < header.length; i++) {
					Object value;
					for(Map.Entry<String, Object> entry : map.entrySet()) {
						if(codeList.get(i).equals(entry.getKey())) {
							value = entry.getValue();
							if(value == null) {
								value = "";
							}
							
							String text = "";
							if(value instanceof Date) {
								Date date = (Date) value;
								SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
								text = sdf.format(date);
							} else if(value instanceof Boolean) {
								text = String.valueOf((Boolean) value);
							} else {
								text = value.toString();
							}
							
							Cell cell = row.createCell(i);
							if(CommonUtil.isNotBlank(text)) {
								//如果是非负浮点数则转换
								Pattern pattern = Pattern.compile("^//d+(//.//d+)?$");
								Matcher matcher = pattern.matcher(text);
								if(matcher.matches()) {
									cell.setCellValue(Double.parseDouble(text));
								} else {
									XSSFRichTextString richText = new XSSFRichTextString(text);
									cell.setCellValue(richText);
								}
							}
							break;
						} else {
							Cell cell = row.createCell(i);
							cell.setCellValue(0);
						}
					}
				}
			}
			workBook.write(out);
			workBook.close();
			out.flush();
			out.close();
		}
	}
	/**
	 * 自定义字段导出Excel2007
	 * @param title
	 * @param header
	 * @param columnName
	 * @param dataList
	 * @param output
	 * @throws Exception
	 */
	public <T> void exportCustom2007(String title,  String[] header, String[] methodNameArray, Collection<T> dataList, 
			OutputStream output) throws Exception {
		if(null != header && header.length > MAX_COL_COUNT_2007) {
			logger.info("--->>>导出excel表头部大于最大限定列值<<<---");
			throw new DataException("509");
		}
		if(null != dataList && dataList.size() > MAX_ROW_COUNT_2007) {
			logger.info("--->>>导出excel内容大于最大限定行值<<<---");
			throw new DataException("510");
		}
		if (header.length != methodNameArray.length) {
			throw new DataException("535");
		}
		if (CommonUtil.isNotBlank(title)) {
			SXSSFWorkbook wb = new SXSSFWorkbook();
			Sheet sheet = wb.createSheet(title);
			int rowIndex = 0;
			if (CommonUtil.isNotBlank(header)) {
				Row row = sheet.createRow(rowIndex);
				createRow(row, header, true);
				rowIndex ++;
			}
			Iterator<T> it = dataList.iterator();
			while (it.hasNext()) {
				Row row = sheet.createRow(rowIndex);
				T t = (T) it.next();
				Class clazz = t.getClass();
				for (int i = 0, size = methodNameArray.length; i < size; i++) {
					String methodName = methodNameArray[i];
					Method method = clazz.getMethod(methodName, new Class[] {});
					Object value = method.invoke(t, new Object[] {});
					
					if (value == null) {
						value = "";
					}
					String text = "";
					if (value instanceof Date) {
						text = DateUtil.format((Date)value, "yyyy-MM-dd");
					} else if(value instanceof Boolean) {
						text = String.valueOf((Boolean) value);
					} else {
						text = value.toString();
					}
					Cell cell = row.createCell(i);
					if(CommonUtil.isNotBlank(text)) {
						//如果是非负浮点数则转换
						Pattern pattern = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = pattern.matcher(text);
						if(matcher.matches()) {
							cell.setCellValue(Double.parseDouble(text));
						} else {
							XSSFRichTextString richText = new XSSFRichTextString(text);
							cell.setCellValue(richText);
						}
					}
				}		
				rowIndex ++;		
			}
			wb.write(output);
			output.flush();
			output.close();
		}
	}
	
	/**
	 * 读取模板文件
	 * @param uploadType
	 * @return
	 */
	public static List<String> getTemplateHeaderList(String uploadType) {
		InputStream in = null;
		Workbook wb = null;
		List<String> headerList = null;
		try {
			in = ExcelUtil.class.getClassLoader().getResourceAsStream("template/" + uploadType + "_template_excel.xlsx");
			wb = new XSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			int size = row.getLastCellNum();
			headerList = new ArrayList<>(size);
			Cell cell = null;
			for(int i = 0; i < size; i++) {
				cell = row.getCell(i);
				headerList.add(cell.getStringCellValue().trim());
			}
			return headerList;
		} catch (FileNotFoundException e) {
			logger.error("--->>>未找到模板文件！<<<---");
		} catch (IOException e) {
			logger.error("--->>>读取模板文件时异常<<<---");
		} finally {
			if(wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					logger.error("--->>>导入文件读取模板文件关闭workbook对象异常<<<---");
				}
			}
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("--->>>导入文件读取模板文件关闭输入流异常<<<---");
				}
			}
			
		}
		return null;
	}
	
	/**
	 * 得到上传文件内容数据集合
	 * @param file 上传文件
	 * @param headers 上传文件的头部  已改为在模板文件里面读取
	 * @param uploadType 上传表类型   在ExcelEnum枚举类中做了定义   需要严格按照此方式来传值 否则会找不到模板文件
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public List<Map<String, Object>> getExcelList(MultipartFile file, String uploadType) throws IOException {
		if (file == null) {
			return null;
		}
		String fileName = file.getOriginalFilename();
		String subfix = fileName.substring(fileName.lastIndexOf(".") + 1);
		Workbook wb = null;
		
		if ("xlsx".equals(subfix)) {
			wb = new XSSFWorkbook(file.getInputStream());
		} else if ("xls".equals(subfix)) {
			wb = new HSSFWorkbook(file.getInputStream());
		} else {
			return null;
		}
		Sheet sheet = wb.getSheetAt(0);
		//去读取模板文件
		List<String> headerList = getTemplateHeaderList(uploadType);
		Row headerRow = sheet.getRow(0);
		Cell cell = null;
		//校验上传excel是否按照模板格式
		for (int i = 0, cellNum = headerRow.getLastCellNum(); i < cellNum; i++) {
			cell = headerRow.getCell(i);
			if(!CommonUtil.isInList(cell.getStringCellValue().trim(), headerList)) {
				return null;
			}
		}
		Row row = null;
		
		String[] codeArray = null;
		codeArray = switchCodeArray(uploadType);
		
		List<Map<String, Object>> list = new ArrayList<>(sheet.getLastRowNum());
		Row firstRow = sheet.getRow(0);
		for (int i = 1, size = sheet.getLastRowNum(); i <= size; i++) {
			row = sheet.getRow(i);
			Map<String, Object> map = new HashMap<>(row.getLastCellNum() + 1);
			for (int j = 0, cellSize = firstRow.getLastCellNum(); j < cellSize; j++) {
				cell = row.getCell(j);
				Object value = null;
				if (cell == null) {
					cell = row.createCell(j);
				}
				switch (cell.getCellTypeEnum()) {
				case NUMERIC: // 数字
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
						Date date = cell.getDateCellValue(); 
						value = sdf.format(date);
					} else {
						value = cell.getNumericCellValue();
						BigDecimal bd1 = new BigDecimal(Double.toString((double)value));
				        // 去掉后面无用的零  如小数点后面全是零则去掉小数点
	                    value = bd1.toPlainString().replaceAll("0+?$", "").replaceAll("[.]$", "");
					}
					break;
				case BOOLEAN: 
					value = cell.getBooleanCellValue() ? "TRUE" : "FALSE";
					break;
				case FORMULA:
					value = cell.getCellFormula() + "";
					break;
				case STRING: // 字符串
					value = cell.getStringCellValue();
					break;
				case BLANK: 
					//value = null;
					break;
				case ERROR: 
					value = "非法字符";
					break;
				default:
					value = "未知类型";
					break;
				}
				//此处不能拿中文匹配  当前做法是按照excel模板的顺序来写header
				//map.put(firstRow.getCell(j).getStringCellValue(), value);
				map.put(codeArray[j], value);
			}
			list.add(map);
		}
		return list;
	}
	
	private String[] switchCodeArray(String uploadType) {
		String[] arr = null;
		switch(uploadType) {
		case "order" : {
			arr = this.orderExcelHeaderArray;
			break;
		}
		case "promotion_detail": {
			arr = this.promotionDetailHeaderArray;
			break;
		}
		case "reject": {
			arr = this.rejectHeaderArray;
			break;
		}
		case "sale": {
			arr = this.saleHeaderArray;
			break;
		}
		case "simple_code": {
			arr = this.simpleCodeHeaderArray;
			break;
		}
		case "stock": {
			arr = this.stockHeaderArray;
			break;
		}
		case "template_product": {
			arr = this.templateProductHeaderArray;
			break;
		}
		case "template_store": {
			arr = this.templateStoreExcelHeaderArray;
			break;
		}
		case "template_supply": {
			arr = this.templateSupplyHeaderArray;
			break;
		}
		case "user": {
			arr = this.userHeaderArray;
			break;
		}
		default : {
			break;
		}
		}
		return arr;
	}
	
	/**
	 * 创建Workbook
	 * @return
	 */
	public SXSSFWorkbook createWorkBook() {
		SXSSFWorkbook wb = new SXSSFWorkbook();
		return wb;
	}
	public void createTitle(Row row, String title) {
		
	}
	/**
	 * 构建行
	 * @param row
	 * @param header
	 */
	public void createRow(Row row, String[] header, boolean heightFlag) {
		for (int i = 0, size = header.length; i < size; i++) {
			if (heightFlag) {
				row.setHeightInPoints((short)20);
				row.createCell(i).setCellValue(header[i]);
			} else {
				row.createCell(i).setCellValue(header[i]);
			}
			
		}
	}
	/**
	 * 设置单元格颜色
	 * @param fill 图案样式
	 * @param color 背景颜色
	 * @return 
	 */
	public CellStyle getCellStyle(Workbook wb, short color) {
		CellStyle cellStyle = wb.createCellStyle();
		
		// 填充单元格
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
		
		// 背景颜色
		cellStyle.setFillForegroundColor(color); 
		return cellStyle;
	}
	/**
	 * 设置字体颜色
	 * @param wb
	 * @param color 颜色
	 * @return
	 */
	public Font getColorFont(Workbook wb, short color) {
		Font font = wb.createFont();
		font.setColor(color);
		return font;
	}
	/**
	 * 粗体、居中样式
	 * @param wb
	 * @return
	 */
	public CellStyle getBolderTitle(Workbook wb) {
		// 字体设置
		Font font = wb.createFont();
		font.setBold(true);// 加粗
		font.setFontHeightInPoints((short) 20);

		// 单元格样式
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
		return cellStyle;
	}
}
