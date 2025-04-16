
package com.hust.ewsystem.common.util;

import com.hust.ewsystem.common.constant.CommonConstant;
import com.hust.ewsystem.common.exception.EwsException;
import org.apache.commons.lang3.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类
 */
public class DateUtil {



	
	public static Date nowDatetime(){
        return new Date(System.currentTimeMillis());
    }
	
	
	/**
     * 得到当前日期 yyyy-MM-dd
     */
	public static String getNowDate() {
        return LocalDate.now().toString();
    }
    
 	/**
     * 得到当前时间 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String dateTime(){
        LocalDateTime time=LocalDateTime.now();
        return format(time, CommonConstant.DATETIME_FORMAT_1);
    }

    /**
     * 得到当前日期格式:yyyy-MM-dd
     * @param date
     */
    public static String getNowDate(Date date){
        if (date == null) {
            return null;
        }
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), CommonConstant.DATE_FORMAT_1);
    }

    /**
     * 得到当前时间格式:yyyy-MM-dd HH:mm:ss
     * @param date
     */
    public static String getTime(Date date){
        if (date == null) {
            return null;
        }
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), CommonConstant.DATETIME_FORMAT_1);
    }

    /**
     * 日期转字符串
     * @param date Date
     * @param format 转换的格式
     * @return
     */
    public static String dateToString(Date date, String format) {
        if (date == null) {
            return null;
        }

        if (StringUtils.isEmpty(format)) {
            format = CommonConstant.DATE_FORMAT_1;
        }
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), format);
    }

    
    /**
     * localDateTime转换为格式化时间
     * @param localDateTime
     * @param pattern 格式
     */
    public static String format(LocalDateTime localDateTime, String pattern){
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }
    
    public static Date format(String dateStr, DateTimeFormatter format) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
    	LocalDate parse = LocalDate.parse(dateStr, format);
    	ZonedDateTime atStartOfDay = parse.atStartOfDay(ZoneId.systemDefault());
    	return Date.from(atStartOfDay.toInstant());
    }


    /**
     * 日期转LocalDateTime
     * @param dateStr
     * @return
     */
    public static LocalDateTime getLocalDateTime(String dateStr) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return LocalDateTime.parse(dateStr,DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT_1));
    }

    /**
     * 取本地日期时间.
     * @return 格式化后的日期时间字符
     */
    public static String getCurrDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT_2));
    }

    /**
     * 取本地日期时间.
     * @return 格式化后的日期时间字符
     */
    public static String getCurrDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstant.DATE_FORMAT_2));
    }

    /**
     * 取本地日期时间.
     * @return
     */
    public static String getCurrLocalDateTimeString(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT_2));
    }

    /**
     * 获取前一天时间
     * @throws ParseException
     */
    public static Date getLastDate() {
        return Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当前系统年度
     */
    public static int getCurrYear(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取UTC 时间 形如：20190812101530+0800
     * @return
     */
    public static String getUTCDate() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter UTCDateTimeFormatter =
                DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT_6);
        return zonedDateTime.format(UTCDateTimeFormatter);
    }

    /**
     * 把字符串格式转换成日期格式，默认时间格式为yyyy-mm-dd
     * @param strDate
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strDate, String format) {
        if(StringUtils.isEmpty(strDate)){
            return null;
        }
        if (format == null || format.length() == 0) {
            format = CommonConstant.DATE_FORMAT_1;
        }
        LocalDate localDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(format));
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 日期转LocalTime
     * @param date
     * @return
     */
    public static LocalTime getLocalTime(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        if (date.contains("-")) {
            date = date.substring(date.lastIndexOf(" ")+1);
        }
        date = date.trim();
        return LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     @param: 入参是当前时间2020-03-01
     @return:返参是前一天的日期,理应为2020-02-29(闰年)
     */
    public static String getBeforeDay(String dateTime){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(CommonConstant.DATE_FORMAT_2);
        Date date = null;
        try{
            date = simpleDateFormat.parse(dateTime);
        }catch (ParseException e){
            throw new EwsException("日期格式转换失败");
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        //往前一天
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String longToString(Long time, String format) {
        if (time==null) {
            return null;
        }
        return dateToString(new Date(time), format);
    }
    /**
     * 获取前n天时间
     * @throws ParseException
     */
    public static Date getAnotherLastDate(Long days) {
        return Date.from(LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取前n天时间(LocalDate)
     * @throws ParseException
     */
    public static LocalDate getAnotherLastLocalDate(Long days) {
        return LocalDate.now().minusDays(days);
    }

    /**
     * 获取前n天时间 yyyy-MM-dd 00:00:00
     * @return
     */
    public static String dateBeginTime(Long day){
        return LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN).minusDays(day).format(DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT_1));
    }
    /**
     * 获取前n天时间 yyyy-MM-dd  23:59:59
     * @return
     */
    public static String dateEndTime(Long day){
        return LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX).minusDays(day).format(DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT_1));
    }
    /**
     * 获取某天时间 yyyy-MM-dd 00:00:00
     * @return
     */
    public static String dateOfBeginTime(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MIN);
        return endOfDay.format(DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT_1));
    }
    /**
     * 获取某天时间 yyyy-MM-dd  23:59:59
     * @return
     */
    public static String dateOfEndTime(Date date){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return endOfDay.format(DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT_1));    }
    /**
     * 组装开票时间信息
     * @param date
     * @param time
     * @return
     */
    public static Date initStringTimeToDateTime(Date date, String time) {
        if (date == null || StringUtils.isEmpty(time)) {
            return null;
        }
        LocalTime localTime = DateUtil.getLocalTime(time);
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        Date dateTime = Date.from(LocalDateTime.of(localDate, localTime).atZone(ZoneId.systemDefault()).toInstant());
        return dateTime;
    }

    /**
     * 比较两个日期是否是同一天
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    /**
     * 计算两个日期相隔的月份
     * @param date1
     * @param date2
     * @return
     */
    public static int monthsBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return CommonConstant.NUM_COMMON_0;
        }
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(localDate1, localDate2);
        return period.getMonths();
    }

    /**
     * 获取当前时间戳(13位)
     * @return String
     */
    public static String getTime(){
        TimeZone.setDefault(TimeZone.getTimeZone(CommonConstant.TIME_ZONE));
        long timeInMillis = Calendar.getInstance(TimeZone.getTimeZone(CommonConstant.TIME_ZONE)).getTimeInMillis();
        return String.valueOf(timeInMillis);
    }

    /**
     * dateTimeToDateString 使用DateTimeFormatter会更具优势，因为它是新的日期时间API的一部分，具有更好的性能和线程安全性
     * @param date
     * @param pattern
     * @return
     */
    public static String dateTimeToDateString(Date date,String pattern){

        // 将Date对象转换为Instant
        Instant instant = date.toInstant();

        // 将Instant转换为LocalDateTime，并指定时区为东八区（GMT+8）
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(CommonConstant.TIME_ZONE));

        // 定义日期格式模板
        DateTimeFormatter dtf =DateTimeFormatter.ofPattern(pattern);

        // 将LocalDateTime对象按照指定格式转换为String
        return localDateTime.format(dtf);
    }
}
