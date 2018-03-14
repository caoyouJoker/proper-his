package com.javahis.ui.sys.orm.tools;

/**
 * 类型
 *
 * @author zhangp
 *
 */
public class Type {
	/**
	 * 计算参数
	 */
	public static final int PARAMETER = 0;
	/**
	 * 计算结果
	 */
	public static final int RESULT = 1;
	/**
	 * 字符
	 */
	public static final int CHAR = 0;
	/**
	 * 数字型
	 */
	public static final int NUM = 1;
	/**
	 * String 格式:YYYYMMDDHH24MISS
	 * <p>
	 * 例子：20140418094720
	 * </p>
	 */
	public static final int DATE = 2;

	/**
	 * 数据库状态：默认
	 */
	public static final int DEFAULT = 0;

	/**
	 * 数据库状态：插入
	 */
	public static final int INSERT = 1;

	/**
	 * 数据库状态：更新
	 */
	public static final int UPDATE = 2;

	/**
	 * 数据库状态：删除
	 */
	public static final int DELETE = 3;
}
