package com.zsuper.mytest.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

/**
 * 日期转换工具类工具类
 * 
 * @author 903792
 * @version [V200R001, 2010-10-28]
 * @see [相关类/方法]
 * @since [DHM.Core.IEPGM-V200R001]
 */
public class DateFormatUtil
{

    public final static String YYMMDD = "yy/MM/dd";

    public final static String HHMMSS = "HH:mm:ss";

    public final static String YYMMDDHHMMSS = "yy/MM/dd HH:mm:ss";

    public final static String PROGRAMDATE = "yyyyMMddHHmmss";
    public final static String YYYY = "yyyy";

    public final static String TTVPROGRAMDATE = "yyyyMMdd";

    // ngod专用时间格式
    public final static String NGOD_TIME = "yyyyMMddHHmmss";

    public static final long DAYMILLIS = 24 * 3600 * 1000;

    /**
     * 把Timestamp 格式转换成 yy/MM/dd
     * 
     * @param inTime
     * @return yy/MM/dd
     */
    public static String timestampToDate(Timestamp inTime)
    {
        String retValue = null;
        if (inTime == null)
        {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(YYMMDD);
        java.util.Date tempDate = new Date(inTime.getTime());
        retValue = sdf.format(tempDate);
        return retValue;
    }

    /**
     * 把Timestamp 格式转换成 HH:mm:ss
     * 
     * @param inTime
     * @return HH:mm:ss
     */
    public static String timestampToTime(Timestamp inTime)
    {
        String retValue = null;
        if (inTime == null)
        {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(HHMMSS);
        java.util.Date tempDate = new Date(inTime.getTime());
        retValue = sdf.format(tempDate);
        return retValue;
    }

    public static Date getCurrentDate() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 功能：将一个Date类型转换成String类型
     * 
     * @param dateTime 所要转换的时间
     * @param format 转换的格式
     * @return 返回预定格式的时间字符串 String
     */
    public static String dateTimeToString(Date dateTime, String format)
    {
        if (dateTime == null || TextUtils.isEmpty(format))
        {
            return "N/A";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(dateTime);
    }

    /**
     * 功能：将字符转转换成时间类型
     * 
     * @param dateTime 所要转换的字符串
     * @param format 转换的时间格式
     * @return Date 返回预定格式的时间类型
     * @throws ParseException Date
     */
    public static Date stringToDate(String dateTime, String format)
    {
        Date result = null;
        if (TextUtils.isEmpty(dateTime))
        {
            result = new Date();
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            try {
                result = dateFormat.parse(dateTime);
            } catch (ParseException e) {
                result = new Date();
            }
        }

        return result;
    }

    /**
     * 功能描述：日期相减
     * 
     * @param now Date 日期
     * @param date1 Date 日期
     * @return 返回相减后的日期
     */
    public static int diffDate(Date now, Date date1)
    {
        return (int) ((getMillis(now) - getMillis(date1)) / DAYMILLIS);
    }

    public static int diffDateBySecond(Date now, Date date1)
    {
        return (int) ((getMillis(now) - getMillis(date1)) / 1000);
    }

    /**
     * 功能描述：返回毫秒
     * 
     * @param date 日期
     * @return 返回毫秒
     */
    public static long getMillis(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    /* 时间比大小 */
    public static int timeCompare(String t1, String t2)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(YYMMDDHHMMSS);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try
        {
            c1.setTime(formatter.parse(t1));
            c2.setTime(formatter.parse(t2));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        int result = c1.compareTo(c2);
        return result;
    }

    public static void main(String[] args) throws ParseException
    {
        String YYMMDDHHMMSS = "yy/MM/dd HH:mm:ss";
        String PROGRAMDATE = "yyyyMMddHHmmss";

        Calendar c1 = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat(PROGRAMDATE);

        String t1 = "20010202121212";
        c1.setTime(formatter.parse(t1));

        System.out.println(c1.getTime());

    }
    
    
    
    public static String getTimestamp()
    {
        String retValue = null;
        SimpleDateFormat sdf = new SimpleDateFormat(HHMMSS);
        java.util.Date tempDate = new Date();
        retValue = sdf.format(tempDate);
        return retValue;
    }
    
	public static String getDelayHour(int second){
		Calendar c = Calendar.getInstance();  
		Date date = new Date();
		c.setTime(date);  
		int nowSecond = c.get(Calendar.SECOND);  
		 c.set(Calendar.SECOND, nowSecond - second);  
		 String delayHour = new SimpleDateFormat(HHMMSS).format(c.getTime());  
		return delayHour;
	}
	
	public static String getNowTimestamp()
    {
        String retValue = null;
        SimpleDateFormat sdf = new SimpleDateFormat(NGOD_TIME);
        java.util.Date tempDate = new Date();
        retValue = sdf.format(tempDate);
        return retValue;
    }
	
	public static String getDuration(String startTime,String endTime){
		Date startDate = stringToDate(startTime,NGOD_TIME);
		Date endDate = stringToDate(endTime,NGOD_TIME);		
		String duration = diffDateBySecond(endDate,startDate)+"";
		return duration;
	}

}
