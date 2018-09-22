package com.data.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 日期操作辅助类
* @author Tarro
* @create 2018年8月6日 17:09
**/
public final class DateUtil {
	
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN.YYYYMM);
	
	private DateUtil() {
	}
	
	/** 日期格式 **/
    public interface DATE_PATTERN {
    	String YYYYMM = "yyyyMM";
        String HHMMSS = "HHmmss";
        String HH_MM_SS = "HH:mm:ss";
        String YYYYMMDD = "yyyyMMdd";
        String YYYY_MM_DD = "yyyy-MM-dd";
        String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
        String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    }

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static final String format(Object date) {
		return format(date, DATE_PATTERN.YYYYMMDD);
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static final String format(Object date, String pattern) {
		if (date == null) {
			return null;
		}
		if (pattern == null) {
			return format(date);
		}
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 获取日期
	 * 
	 * @return
	 */
	public static final String getDate() {
		return format(new Date());
	}

	/**
	 * 获取日期时间
	 * 
	 * @return
	 */
	public static final String getDateTime() {
		return format(new Date(), DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
	}

	/**
	 * 获取日期
	 * 
	 * @param pattern
	 * @return
	 */
	public static final String getDateTime(String pattern) {
		return format(new Date(), pattern);
	}

	/**
	 * 日期计算
	 * 
	 * @param date
	 * @param field
	 * @param amount
	 * @return
	 */
	public static final Date addDate(Date date, int field, int amount) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}
	
	/**
	 * 获取系统日期
	 * @return
	 */
	public static final Date getSystemDate() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 字符串转换为日期:不支持yyM[M]d[d]格式
	 * 
	 * @param date
	 * @return
	 */
	public static final Date stringToDate(String date) {
		if (date == null) {
			return null;
		}
		String separator = String.valueOf(date.charAt(4));
		String pattern = "yyyyMMdd";
		if (!separator.matches("\\d*")) {
			pattern = "yyyy" + separator + "MM" + separator + "dd";
			if (date.length() < 10) {
				pattern = "yyyy" + separator + "M" + separator + "d";
			}
	        pattern += " HH:mm:ss.SSS";
		} else if (date.length() < 8) {
			pattern = "yyyyMd";
		} else {
	        pattern += "HHmmss.SSS";
        }
		pattern = pattern.substring(0, Math.min(pattern.length(), date.length()));
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 间隔天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static final Integer getDayBetween(Date startDate, Date endDate) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);

		long n = end.getTimeInMillis() - start.getTimeInMillis();
		return (int) (n / (60 * 60 * 24 * 1000L));
	}

	/**
	 * 间隔月
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static final Integer getMonthBetween(Date startDate, Date endDate) {
		if (startDate == null || endDate == null || !startDate.before(endDate)) {
			return null;
		}
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		int year1 = start.get(Calendar.YEAR);
		int year2 = end.get(Calendar.YEAR);
		int month1 = start.get(Calendar.MONTH);
		int month2 = end.get(Calendar.MONTH);
		int n = (year2 - year1) * 12;
		n = n + month2 - month1;
		return n;
	}
	
	/**
	 * 得到输入日期月份的最小天数
	 * @param date
	 * @return
	 */
	public static final String getMonthStartDate(String date) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(date));
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			return sdf.format(calendar.getTime());
		} catch (ParseException e) {
			logger.error("--->>>日期解析失败<<<---");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 得到输入日期月份的最大天数
	 * @param date
	 * @return
	 */
	public static final String getMonthEndDate(String date) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(date));
			
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			return sdf.format(calendar.getTime());
		} catch (ParseException e) {
			logger.error("--->>>日期解析失败<<<---");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 得到固定26号开始的一个月
	 * @param date
	 * @return
	 */
	public static final List<String> getMonthDays(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		calendar.set(year, month, 26);
		Date limitDate = calendar.getTime();
		//处理过月日期处理
		if(date.before(limitDate)) {
			calendar.add(Calendar.MONTH, -1);
		}
		List<String> daysList = new ArrayList<>();
		String endDay = getMonthMaxDay(calendar.getTime());
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM");
		String yearMonth = s.format(calendar.getTime());
		String day;
		//组装日期
		int days = getBetweenDays(getMonthStartDay(calendar.getTime()), endDay);
		int index = 27;
		while(days > 0) {
			day = yearMonth + "-" + String.valueOf(index);
			daysList.add(day);
			index++;
			days--;
		}
		calendar.add(Calendar.MONTH, 1);
		yearMonth = s.format(calendar.getTime());
		days = 26;
		index = 1;
		while(days > 0) {
			if(index < 10) {
				day = yearMonth + "-0" + String.valueOf(index);				
			} else {
				day = yearMonth + "-" + String.valueOf(index);
			}
			daysList.add(day);
			index++;
			days--;
		}
		return daysList;
	}
	
	/**
	 * 得到每月开始日
	 * @return
	 */
	public static final String getMonthStartDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM");
		String day = s.format(calendar.getTime()) + "-26"; 
		return day;
	}
	
	/**
	 * 得到这个月最大天数
	 * @return
	 */
	public static final String getMonthMaxDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * 计算两个日期相差天数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static final Integer getBetweenDays(String startDate, String endDate) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(startDate));
			long start = c.getTimeInMillis();
			c.setTime(sdf.parse(endDate));
			long end = c.getTimeInMillis();
			long days = (end - start) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(days));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 格式化当前日期
	 * @param pattern
	 * @return
	 */
	public static final String getCurrentDateStr() {
		return new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD).format(getSystemDate());	
	}
	
	/**
	 * 根据days天数来得到几天前或几天后的数据
	 * @param days
	 * @return
	 */
	public static final Date getCustomDate(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getSystemDate());
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}
	
	public static void main(String[] args) {
		System.out.println(getMonthEndDate("2018-02"));
	}

}
