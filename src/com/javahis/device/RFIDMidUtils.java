package com.javahis.device;

import org.jawin.COMException;
import org.jawin.DispatchPtr;
import org.jawin.win32.Ole32;
/**
 * RFID桌面设备接口类
 */
public class RFIDMidUtils {
	public static DispatchPtr app = null;

	static {

		try {
			app = new DispatchPtr("Com.Sunray.SunrayRfidMidClass"); // 载入dll
		} catch (Throwable ex) {

		}
	}

	/**
	 * 连接
	 */
	public static void connect() {
		// 连接
		try {
			app.invoke("Connect");
		} catch (COMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 释放连接
	 */
	public static void disconnect() {

		try {
			app.invoke("Disconnect");
		} catch (COMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 写标签
	 */
	public static void writeTag(String oldValue, String newValue) {

		try {
			// Ole32.CoInitialize();

			// 写入
			app.invoke("WriteEPC", oldValue, newValue);
			// 释放连接
			// app.invoke("Disconnect");
			// Ole32.CoUninitialize();
		} catch (COMException e) {
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * 读标签
	 * 
	 * @return
	 */
	public static String[] readTags() {
		Object o;
		try {
			// app.invoke("Connect");
			o = app.invoke("GetTagEPCList");
			String[] array = (String[]) o;
			// app.invoke("Disconnect");
			return array;
		} catch (COMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void test() {
		System.out.println("--------------come in------------------");
		try {
			// Ole32.CoInitialize();
			app.invoke("Connect");
			app.invoke("WriteEPC", "213126820080430320001001",
					"213126820080430320001002");
			System.out.println("-----lxtest end ----");

			Object o = app.invoke("GetTagEPCList");
			String[] array = (String[]) o;
			for (String s : array) {
				System.out.println("-----lxtest----" + s);
			}

			app.invoke("Disconnect");
			// Ole32.CoUninitialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("--------------come end------------------");

	}

}
