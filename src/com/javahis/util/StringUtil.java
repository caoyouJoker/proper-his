package com.javahis.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.opd.OPDSysParmTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title:文字共用方法
 * </p>
 * 
 * <p>
 * Description:文字共用方法
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20080917
 * @version 1.0
 */
public class StringUtil {
	private static StringUtil instanceObject;
	/**
	 * @alias 数字大写
	 */
	private static final String[] NUMBERS = { "零", "壹", "贰", "叁", "肆", "伍",
			"陆", "柒", "捌", "玖" };
	/**
	 * @alias 金额位数名称
	 */
	private static final String[] IUNIT = { "元", "拾", "佰", "仟", "万", "拾", "佰",
			"仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟" };
	private static final String[] DUNIT = { "角", "分", "厘" };

	public StringUtil() {
	}

	public static synchronized StringUtil getInstance() {
		if (instanceObject == null) {
			instanceObject = new StringUtil();
		}
		return instanceObject;
	}

	/**
	 * 根据汉字返回拼音首字母
	 * 
	 * @param chinese
	 * @return 拼音首字母
	 */
	public static String onCode(String chinese) {
		SystemTool st = new SystemTool();
		String value = st.charToCode(chinese);
		if (null == value || value.length() < 1) {
			return null;
		}
		return value;

	}

	/**
	 * @alias 计算年龄
	 * @param String
	 *            birthday 出生日期(20050810)
	 * @return String 年龄(1岁2个月3天) 功能说明：<br>
	 *         1、取AP时间月<br>
	 */
	public String countAge(String birthday, Timestamp admDate, String type) {
		String rString = ""; // 年龄
		String ayear = ""; // 年
		String amonth = ""; // 月
		String aday = ""; // 日
		if (birthday.length() == 0) {
			return rString;
		} else {
			// 1. 取得系统日期
			String sysDate = "";
			if (type.equals("Y")) {
				sysDate = this.getDate(admDate);
			} else {
				sysDate = this.getDate();
			}
			char ch2[] = new char[sysDate.length()];
			sysDate.getChars(0, sysDate.length(), ch2, 0);
			String syear2 = new String(ch2, 0, ch2.length - 4);
			String smonth2 = new String(ch2, ch2.length - 4, 2);
			String sday2 = new String(ch2, ch2.length - 2, 2);
			// 把年月日转换成整数
			int iyear2 = Integer.parseInt(syear2);
			if (syear2.length() != 4) {
				iyear2 = (iyear2 + 1911);
			}
			int imonth2 = Integer.parseInt(smonth2);
			int iday2 = Integer.parseInt(sday2);
			// 取得输入的年月日字串
			char ch[] = new char[birthday.length()];
			birthday.getChars(0, birthday.length(), ch, 0);
			String syear1 = new String(ch, 0, ch.length - 4);
			String smonth1 = new String(ch, ch.length - 4, 2);
			String sday1 = new String(ch, ch.length - 2, 2);
			// 把年月日转换成整数
			int iyear1 = Integer.parseInt(syear1);
			if (syear1.length() != 4) {
				iyear1 = (iyear1 + 1911);
			}
			int imonth1 = Integer.parseInt(smonth1);
			int iday1 = Integer.parseInt(sday1);
			// 设定变数初始值
			int year = 0;
			int month = 0;
			int day = 0;
			// 取得两个日期日数差
			if (iday2 < iday1) {
				if ((imonth2 == 2) || (imonth2 == 4) || (imonth2 == 6)
						|| (imonth2 == 9) || (imonth2 == 11)) {
					day = ((iday2 + 31) - iday1) + 1;
					imonth2 = (imonth2 - 1);
				} else if (imonth2 == 3) {
					String g = "" + iyear2;
					String a = this.IsLeapYear(g);
					if (a == "True") {
						day = (iday2 + 29) - iday1 + 1;
					} else if (a == "false") {
						day = (iday2 + 28) - iday1 + 1;
					} else {
						day = (iday2 + 28) - iday1 + 1;
					}
					imonth2 = (imonth2 - 1);
				} else if (imonth2 == 1) {
					day = (iday2 - iday1) + 31;
					imonth2 = 12;
					iyear2--;
				} else {
					day = ((iday2 + 30) - iday1) + 1;
					imonth2 = (imonth2 - 1);
				}
			} else {
				day = (iday2 - iday1) + 1;
			}
			// 取得两日期的月份差
			if (imonth2 < imonth1) {
				month = (imonth2 + 12) - imonth1;
				iyear2 = iyear2 - 1;
			} else {
				month = imonth2 - imonth1;
			}

			// 取得两日期年份差
			year = iyear2 - iyear1;
			// 将结果转成字串
			ayear = Integer.toString(year);
			amonth = Integer.toString(month);
			aday = Integer.toString(day);

			rString = ayear + "岁" + amonth + "个月" + aday + "天";
		}
		return rString;
	}

	/**
	 * 根据病患生日和传入的截至日期，计算病人年龄并根据是否为儿童以不同的形式显示年龄，如是儿童则显示X岁X月X日，如成人则显示x岁
	 * 
	 * @param odo
	 * @return String 界面显示的年龄
	 */
	public static String showAge(Timestamp birthday, Timestamp t) {
		String age = "";
		String[] res;
		res = StringTool.CountAgeByTimestamp(birthday, t);
		if (OPDSysParmTool.getInstance().isChild(birthday, t)) {//wanglong modify 20150119
			age = res[0] + "岁" + res[1] + "月" + res[2] + "日";
		} else {
			age = res[0] + "岁";
		}
		return age;
	}

	/**
	 * 获得系统当前时间
	 * 
	 * @return String
	 */
	public String getDate() {
		String result = "";
		// 获得当前时间
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
		result = time.format(new Date()).toString();
		return result;
	}

	/**
	 * 获得系统当前时间
	 * 
	 * @return String
	 */
	public String getDate(Timestamp admDate) {
		String result = "";
		// 获得当前时间
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
		result = time.format(admDate).toString();
		return result;
	}

	/**
	 * 确定某个给定日期是否是闰年的日期
	 * 
	 * @param a
	 *            String
	 * @return String
	 */
	public String IsLeapYear(String a) {
		String temp = "False";
		int yearleap = 0;
		yearleap = Integer.parseInt(a);
		GregorianCalendar gcalendar = new GregorianCalendar();
		if (gcalendar.isLeapYear(yearleap))
			temp = "True";
		else
			temp = "False";
		return temp;
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 * 
	 * @param date
	 *            String
	 * @return String
	 */
	public String getNextDay(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24
					* 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 * 
	 * @param date
	 *            String
	 * @return long
	 */
	public long getNextDayMiss(String nowdate, String delay) {
		try {
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24
					* 60 * 60;
			d.setTime(myTime * 1000);
			return d.getTime();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 *            String
	 * @return Date
	 */
	public Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 取数字转大写
	 * 
	 * @param num
	 *            double
	 * @return String
	 */
	public String numberToWord(double num) {
		
		DecimalFormat df = new DecimalFormat("0.00");
		boolean flg;
		if (num == 0) {
			return "零元整";
		}
		if (num < 0) {
			num = 0 - num;
			flg = true;
		} else
			flg = false;
		String str=df.format(num);
		str = str.replaceAll(",", "");// 去掉","
		String integerStr;// 整数部分数字
		String decimalStr;// 小数部分数字

		// 初始化：分离整数部分和小数部分
		if (str.indexOf(".") > 0) {
			integerStr = str.substring(0, str.indexOf("."));
			decimalStr = str.substring(str.indexOf(".") + 1);
		} else if (str.indexOf(".") == 0) {
			integerStr = "";
			decimalStr = str.substring(1);
		} else {
			integerStr = str;
			decimalStr = "";
		}
		// integerStr去掉首0，不必去掉decimalStr的尾0(超出部分舍去)
		if (!integerStr.equals("")) {
			integerStr = Long.toString(Long.parseLong(integerStr));
			if (integerStr.equals("0")) {
				integerStr = "";
			}
		}
		// overflow超出处理能力，直接返回
		if (integerStr.length() > IUNIT.length) {
			System.out.println(str + ":超出处理能力");
			return str;
		}

		int[] integers = toArray(integerStr);// 整数部分数字
		boolean isMust5 = isMust5(integerStr);// 设置万单位
		int[] decimals = toArray(decimalStr);// 小数部分数字
		String result=getChineseInteger(integers, isMust5)
		+ getChineseDecimal(decimals);
		if (flg)
			return "负" + result.toString() + "整";
		else
			if(result.toString().indexOf("分")!=-1){
				return result.toString();
			}else{
				return result.toString() + "整";
			}
	}

	private static int[] toArray(String number) {
		int[] array = new int[number.length()];
		for (int i = 0; i < number.length(); i++) {
			array[i] = Integer.parseInt(number.substring(i, i + 1));
		}
		return array;
	}

	private static String getChineseInteger(int[] integers, boolean isMust5) {
		StringBuffer chineseInteger = new StringBuffer("");
		int length = integers.length;
		for (int i = 0; i < length; i++) {
			// 0出现在关键位置：1234(万)5678(亿)9012(万)3456(元)
			// 特殊情况：10(拾元、壹拾元、壹拾万元、拾万元)
			String key = "";
			if (integers[i] == 0) {
				if ((length - i) == 13)// 万(亿)(必填)
					key = IUNIT[4];
				else if ((length - i) == 9)// 亿(必填)
					key = IUNIT[8];
				else if ((length - i) == 5 && isMust5)// 万(不必填)
					key = IUNIT[4];
				else if ((length - i) == 1)// 元(必填)
					key = IUNIT[0];
				// 0遇非0时补零，不包含最后一位
				if ((length - i) > 1 && integers[i + 1] != 0)
					key += NUMBERS[0];
			}
			chineseInteger.append(integers[i] == 0 ? key
					: (NUMBERS[integers[i]] + IUNIT[length - i - 1]));
		}
		return chineseInteger.toString();
	}

	private static String getChineseDecimal(int[] decimals) {
		StringBuffer chineseDecimal = new StringBuffer("");
		for (int i = 0; i < decimals.length; i++) {
			// 舍去3位小数之后的
			if (i == 3)
				break;
			chineseDecimal.append(decimals[i] == 0 ? ""
					: (NUMBERS[decimals[i]] + DUNIT[i]));
		}
		return chineseDecimal.toString();
	}

	private static boolean isMust5(String integerStr) {
		int length = integerStr.length();
		if (length > 4) {
			String subInteger = "";
			if (length > 8) {
				// 取得从低位数，第5到第8位的字串
				subInteger = integerStr.substring(length - 8, length - 4);
			} else {
				subInteger = integerStr.substring(0, length - 4);
			}
			return Integer.parseInt(subInteger) > 0;
		} else {
			return false;
		}
	}
	/**
	 * 判断是不是空字符串
	 * 
	 * @param s
	 * @return boolean Y:空，N:不空
	 */
	public static boolean isNullString(String s) {
		return (s == null || s.length() < 1);
	}

	/**
	 * 数组拷贝方法
	 * 
	 * @param sql1
	 *            String[]
	 * @param sql2
	 *            String[]
	 * @return String[]
	 */
	public static String[] copyArray(String sql1[], String sql2[]) {
		if (sql1.length == 0)
			return sql2;
		if (sql2.length == 0)
			return sql1;
		String data[] = new String[sql1.length + sql2.length];
		System.arraycopy(sql1, 0, data, 0, sql1.length);
		System.arraycopy(sql2, 0, data, sql1.length, sql2.length);
		return data;
	}

	/**
	 * 判断给入的List是否为空
	 * 
	 * @param list
	 * @return boolean 如是则返回真，如不是则返回假
	 */
	public static boolean isNullList(List list) {
		return (list == null || list.size() < 1);
	}

	/**
	 * 判断给入的List是否为空，以及SIZE是否大于等于给入的SIZE
	 * 
	 * @param list
	 *            List
	 * @param size
	 *            int
	 * @return boolean 如为空或LIST的SIZE不满足给入值，则返回真，否则返回假
	 */
	public static boolean isSatisefyListSize(List list, int size) {
		return (list == null || list.size() < size);
	}

	/**
	 * 判断给入的数组是否为空
	 * 
	 * @param list
	 * @return boolean 如是则返回真，如不是则返回假
	 */
	public static boolean isNullArray(Object[] obj) {
		return (obj == null || obj.length < 1);
	}

	/**
	 * 判断给入的数组是否为空，以及SIZE是否大于等于给入的SIZE
	 * 
	 * @param list
	 *            List
	 * @param size
	 *            int
	 * @return boolean 如为空或LIST的SIZE不满足给入值，则返回真，否则返回假
	 */
	public static boolean isSatisefyListSize(Object[] obj, int size) {
		return (obj == null || obj.length < size);
	}

	/**
	 * 通过身份证得到出生地Code
	 * 
	 * @param idNo
	 *            String
	 * @return String
	 */
	public static String getIdNoToHomeCode(String idNo) {
		if (idNo.trim().length() < 15) {
			return idNo;
		}
		return idNo.substring(0, 6);

	}

	/**
	 * 根据给入TABLE名，列名,WHERE条件得到查询列的值
	 * 
	 * @param tableName
	 * @param columnName
	 * @param whereStr
	 * @return
	 */
	public static String getDesc(String tableName, String columnName,
			String whereStr) {
		if (StringUtil.isNullString(tableName)
				|| StringUtil.isNullString(columnName)
				|| StringUtil.isNullString(whereStr)) {
			// System.out.println("StringUtil.getDesc-> inParameter is null");
			return "";
		}
		String sql = "SELECT # FROM # WHERE #";
		sql = sql.replaceFirst("#", columnName).replaceFirst("#", tableName)
				.replaceFirst("#", whereStr);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0) {
			return "";
		}
		return parm.getValue(columnName, 0);
	}
	
	public static Timestamp getNextTime(Timestamp t1, long l) {
		return new Timestamp(t1.getTime() + l);
	}
	
	
	/**
	 * 计算 时间差  格式 XX分XX秒
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static String getDiffInMinAndSec(Timestamp t1, Timestamp t2) {
		if((t1 == null || t2 == null) || (t2.getTime() < t1.getTime()) ) {
			return "";
		}

		if(t2.getTime() == t1.getTime()) {
			return "0分0秒";
		}
		
	    double diffSec =	(t2.getTime()  - t1.getTime()) / 1000D;
	    
		return  ((int)(diffSec / 60D)) + "分" + ((int)(diffSec % 60D)) + "秒";
	}
	
	public static void main(String[] args) {
		
		Timestamp t1 = Timestamp.valueOf("2017-03-27 12:00:00");
		
		
		Timestamp t2 = Timestamp.valueOf("2017-03-28 12:00:00");
		  
		  
		System.out.println(getDiffInMinAndSec(t1, t2));
	}
	
}
