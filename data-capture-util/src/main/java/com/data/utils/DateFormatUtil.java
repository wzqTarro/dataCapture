package com.data.utils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



/** 
 * @Title: DateFormatUtil.redstudy
 * @Description: 日期格式化工具类 
 */
public class DateFormatUtil
{
	//默认时间格式
	public final static String DEFAULT_FORMAT = "yyyy-MM-dd";
	public final static String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 
     * @Title: dateFormat
     * @Description: 对输入不正确日期进行格式化 -- 格式为：yyyy-MM-dd
     *
     */
    public static Date dateFormat(Date date) throws ParseException{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = java.sql.Date.valueOf(sdf.format(date));  
        return newDate;
    }
    /**
     * 
     * @Description:
     * @param date    日期
     * @param format  时间格式
     * @return
     * @throws ParseException
     * Date
     */
    public static String dateFormat(Date date,String pattern) throws ParseException{
    	if (date == null) {
			return "";
		}
		DateFormat df = null;
		if (StringUtils.isEmpty(pattern)) {
			df = new SimpleDateFormat(DEFAULT_FORMAT);
		} else {
			df = new SimpleDateFormat(pattern);
		}
		return df.format(date);
    }
    /**
     * 功能：比较日期是否相等
     * @param FirstDate String  输入为yyyy-MM-DD或者yyyy/MM/DD
     * @param SecondDate String
     * @return int 1：大于，0：相等，-1：小于
     */
    public static int compareDate(String FirstDate,String SecondDate) throws Exception
    {
      int intReturn   = 0 ; //返回值
      Date firstDate  = null;
      Date secondDate = null;
      try
      {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //StringConvert.replace(FirstDate,"/","-");  //将“/”的分隔符
        String strReturn  = "";
        int intStartIndex = 0,
        intEndIndex   = 0;
        if(FirstDate!=null || !"".equals(FirstDate)) {
        	while((intEndIndex=FirstDate.indexOf("/",intStartIndex))>-1)
        	{
        		strReturn = strReturn + FirstDate.substring(intStartIndex,intEndIndex) + "-";
        		intStartIndex = intEndIndex +1;
        	}
        }
        firstDate = format.parse(FirstDate);
        secondDate = format.parse(SecondDate);
        if (firstDate.after(secondDate)) {
          intReturn = 1;
        }
        else if (firstDate.before(secondDate)) {
          intReturn = -1;
        }
        else if (firstDate.equals(secondDate)) {
          intReturn = 0;
        }
        return intReturn;
      } catch(Exception e) {
        throw e;
      }
    }
    
    /**
	  * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	  */
	public static Date getNextDay(String nowdate, String delay) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date d = format.parse(nowdate, pos);
		long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
		d.setTime(myTime * 1000);
		return d;
	}
	
	/**
     * 根据字符串格式去转换相应格式的日期和时间
     * **/
    public static Date reverseDate(String date) {
        SimpleDateFormat simple = null;
        switch (date.trim().length()) {
        case 19:// 日期+时间
            simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            break;
        case 14:// 日期+时间
            simple = new SimpleDateFormat("yyyyMMddHHmmss");
            break;
        case 10:// 仅日期
            simple = new SimpleDateFormat("yyyy-MM-dd");
            break;
        case 8:// 仅时间
            simple = new SimpleDateFormat("HH:mm:ss");
            break;
        default:
            break;
        }
        try {
            return simple.parse(date.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @Description: TODO(某个时间与当前时间进行求差 （天）)  
     * @throws
     */
    public static long getAppointTimeDifference(Date startDate,Date endDate){     
        long l=endDate.getTime() - startDate.getTime();
        long day=l / (24*60*60*1000);
        return day;
    }
    
    /**
	 * @desc 日期类型格式化为字符串
     * @param nowdate
     * @param formString(格式)
     * @return
     */
	public static String formDateToString(Date nowdate, String formString) {
		SimpleDateFormat format = new SimpleDateFormat(formString);
		return format.format(nowdate);
	}
	
	/**
	 * @desc 根据日期及时间获得对应的日期数据
	 * @param dateStr
	 * @param time
	 * @return
	 */
	public static Map<String,Date> getWholeDatetime(String dateStr,int time){
		Map<String,Date> map = new HashMap<String,Date>();
		String start = dateStr;
		String end = dateStr;
		if(time>=10){
			start = start+" "+time+":00:00";
			end = end+" "+time+":59:59";
		}
		else{
			start = start+" 0"+time+":00:00";
			end = end+" 0"+time+":59:59";
		}
		map.put("startTime",reverseDate(start));
		map.put("endTime", reverseDate(end));
		return map;
	}
	
	/**
	 * @desc 格式化字符串时间
	 * @param dateStr
	 * @param time
	 * @return
	 */
	public static String formContactDate(String dateStr,int time){
		if(time>=10){
			dateStr = dateStr+" "+time+":00:00";
		}
		else{
			dateStr = dateStr+" 0"+time+":00:00";
		}
		return dateStr;
	}
	
	/**
	 * @desc取日期中的年月日、时\分\秒
	 * @param dateStr
	 * @return
	 */
	public static Map<String,Object> getHoursByDate(String dateStr){
		Map<String,Object> map = new HashMap<String,Object>();
		String[] arr =  dateStr.split(" ");
		map.put("hour", arr[1].split(":")[0]);
		map.put("date",arr[0]);
		return map;
	}
}

