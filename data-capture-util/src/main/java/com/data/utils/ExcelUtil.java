package com.data.utils;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			SXSSFWorkbook workBook = new SXSSFWorkbook(1000);
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
					Object value = method.invoke(method, new Object[] {});
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
							HSSFRichTextString richText = new HSSFRichTextString(text);
							cell.setCellValue(richText);
						}
					}
				}
			}
			workBook.write(out);
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
			SXSSFWorkbook workBook = new SXSSFWorkbook(1000);
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
					Object value = method.invoke(method, new Object[] {});
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
		}
	}
	/**
	 * 创建Workbook
	 * @return
	 */
	public SXSSFWorkbook createWorkBook() {
		SXSSFWorkbook wb = new SXSSFWorkbook();
		return wb;
	}
	/**
	 * 构建行
	 * @param row
	 * @param header
	 */
	public void createRow(Row row, String[] header) {
		for (int i = 0, size = header.length; i < size; i++) {
			row.createCell(i).setCellValue(header[i]);
		}
	}
	/**
	 * 设置单元格样式
	 * @param fill 图案样式
	 * @param color 背景颜色
	 * @return 
	 */
	public CellStyle setCellStyle(SXSSFWorkbook wb, short fill, short color) {
		CellStyle cellStyle = wb.createCellStyle();
		
		// 填充单元格
		cellStyle.setFillPattern(fill); 
		
		// 背景颜色
		cellStyle.setFillForegroundColor(color); 
		return cellStyle;
	}
	/**
	 * 设置字体样式
	 * @param wb
	 * @param color 颜色
	 * @return
	 */
	public Font setFont(SXSSFWorkbook wb, short color) {
		Font font = wb.createFont();
		font.setColor(color);
		return font;
	}
}
