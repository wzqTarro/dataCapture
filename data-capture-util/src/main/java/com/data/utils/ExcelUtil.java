package com.data.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
	
	@SuppressWarnings("resource")
	public List<Map<String, Object>> getExcelList(MultipartFile file) throws IOException {
		if (file == null) {
			return null;
		}
		String fileName = file.getOriginalFilename();
		String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
		Workbook wb = null;
		
		if ("xlxs".equals(prefix)) {
			wb = new XSSFWorkbook(file.getInputStream());
		} else if ("xls".equals(prefix)) {
			wb = new HSSFWorkbook(file.getInputStream());
		} else {
			return null;
		}
		Sheet sheet = wb.getSheetAt(0);
		Row row = null;
		Cell cell = null;
		List<Map<String, Object>> list = new ArrayList<>(sheet.getLastRowNum());
		for (int i = 1, size = sheet.getLastRowNum(); i <= size; i++) {
			row = sheet.getRow(i);
			Map<String, Object> map = new HashMap<>(row.getLastCellNum() + 1);
			for (int j = 0, cellSize = row.getLastCellNum(); j <= cellSize; j++) {
				cell = row.getCell(j);
				Object value = null;
				switch (cell.getCellTypeEnum()) {
				case NUMERIC: // 数字
					value = cell.getNumericCellValue();
					break;
				case STRING: // 字符串
					value = cell.getStringCellValue();
					break;
				default:
					return null;
				}
				map.put(sheet.getRow(0).getCell(j).getStringCellValue(), value);
			}
			list.add(map);
		}
		return list;
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
