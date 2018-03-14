package jdo.spc;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/** */
	/**
	 * 默认日期格式
	 */
	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** */
	/**
	 * <默认构造函数>
	 */
	private DateUtil() {
	}

	/** */
	/**
	 * <字符串转换成日期> <如果转换格式为空，则利用默认格式进行转换操作>
	 * 
	 * @param str
	 *            字符串
	 * @param format
	 *            日期格式
	 * @return 日期
	 * @see [类、类#方法、类#成员]
	 */
	public static Date str2Date(String str, String format) {
		if (null == str || "".equals(str)) {
			return null;
		}
		// 如果没有指定字符串转换的格式，则用默认格式进行转换
		if (null == format || "".equals(format)) {
			format = DEFAULT_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/** */
	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @param date
	 *            日期
	 * @param format
	 *            日期格式
	 * @return 字符串
	 * @see [类、类#方法、类#成员]
	 */
	public static String date2Str(Date date, String format) {
		if (null == date) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/** */
	/**
	 * <时间戳转换为字符串> <功能详细描述>
	 * 
	 * @param time
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String timestamp2Str(Timestamp time) {
		Date date = new Date(time.getTime());
		return date2Str(date, DEFAULT_FORMAT);
	}

	/** */
	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @param str
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static Timestamp str2Timestamp(String str) {
		Date date = str2Date(str, DEFAULT_FORMAT);
		return new Timestamp(date.getTime());
	}
}
