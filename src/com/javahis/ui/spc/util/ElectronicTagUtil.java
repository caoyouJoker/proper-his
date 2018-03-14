package com.javahis.ui.spc.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;


/**
 * 电子标签工具类
 * 
 * @author liyh
 * @date 20121112
 * 
 */
public class ElectronicTagUtil {

	/**
	 * 实例
	 */
	public static ElectronicTagUtil instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static ElectronicTagUtil getInstance() {
		if (instanceObject == null)
			instanceObject = new ElectronicTagUtil();
		return instanceObject;
	}

	/**
	 * 登陆
	 */
	public static void login() {
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		Map<String, Object> map = eti.login("admin", "123");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
			Constant.parameters.clear();
		}

		Constant.parameters.add(0, new BasicNameValuePair("Token", (String) map.get("Token")));
		Constant.parameters.add(1, new BasicNameValuePair("UserId", (String) map.get("UserId")));
		Constant.parameters.add(2, new BasicNameValuePair("RoleId", (String) map.get("RoleId")));
	}

	/**
	 * 更新电子标签
	 * 
	 * @param bastkId  电子标签ID
	 * @param nameInfo 第一行显示内容
	 * @param spec 第二回显示内容
	 * @param num 第二回显示内容
	 * @param lightNum 灯亮次数
	 * @return
	 * @author liyh
	 * @date 20120919
	 */
	public boolean sendEleTag(String bastkId, String nameInfo, String spec, String num, int lightNum) {
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		if (null == uuid)
			uuid = UUID.randomUUID();

		m.put("ObjectId", uuid.toString());
		m.put("ObjectType", 3);
		m.put("ObjectName", "medBasket");
		// m.put("LabelNo", "01048A");
		// 电子标签id
		m.put("LabelNo", bastkId);
		// 基站id
		m.put("StationID", "2");
		// 第一行 显示用户名和年龄
		m.put("ProductName", nameInfo);
		// 第二回 显示病案号 和性别
		m.put("Spec", spec + " " + num);
		// 第三行 电子标签二维条码含义
		m.put("ShelfNo", bastkId);

		// 闪烁次数
		m.put("Light", lightNum);
		// 是否亮灯：true:亮
		m.put("Enabled", true);

		Iterator it = m.entrySet().iterator();
//		System.out.println("------------发药--电子标签更新内容---------start----------");
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		// 调用电子标签接口
		Map<String, Object> map = eti.cargoUpdate(m);
		
		System.out.println("发药================================begin");
		Iterator it1 = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it1.next();
			System.out.println(entry.getKey() + "===================="+ entry.getValue());
		}
		System.out.println("发药================================end");
		
		if (null != map) {
			it = map.entrySet().iterator();
			String status = (String) map.get("Status");
			if (null != status && "10000".equals(status)) {// 更新电子标签状态成功
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}
	
	public static void main(String[] args){
		ElectronicTagUtil.getInstance().login();
		ElectronicTagUtil.getInstance().sendEleTag("", "" , "","",1);
	}
}
