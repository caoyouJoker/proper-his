package com.javahis.ui.spc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.LabelImpl;
import jdo.label.LabelInf;

import com.dongyang.control.TControl;

/**
 * 
 * <p>Title:二代电子标签总控制 </p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author Yuanxm
 * @version 1.0
 */

public class LabelControl extends TControl {

	private static final LabelControl INSTANCE = new LabelControl();

	public static LabelControl getInstance() {
		return INSTANCE;
	}

	/**
	 * 二代电子标签
	 * 
	 * @param list
	 * @param url
	 * @return
	 */
	public boolean sendLabelDate(List<Map<String, Object>> list, String url) {

		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();

		String objectId = getObjectId();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) list.get(i);
			Map<String, Object> newMap = new LinkedHashMap<String, Object>();
			newMap.put("ObjectId", objectId);
			newMap.put("ObjectType", 1);
			newMap.put("ObjectName", "medBasket");
			//电子标签ID
			newMap.put("TagNo", map.get("TagNo"));
			
			//区域
			newMap.put("APRegion", map.get("APRegion"));
			newMap.put("ShelfNo", "10411515515");
			
			//显示的药品名，电子标签第一行显示内容 
			newMap.put("ProductName", map.get("ProductName"));
			
			//第二行显示内容
			String spec = (String) map.get("SPECIFICATION");
			if(spec != null && !spec.equals("") &&  (spec.length() > 12)){
				spec = spec.substring(0,12);
			}
			
			String num = (String)map.get("NUM");
			if(num == null || num.equals("null") || num.equals("")){
				num = "";
			}
			
			spec = spec + " "+num;
			
			//System.out.println("Spec--------------:"+spec +" 长度为："+spec.length());
			// 电子标签显示第二行数据 规格 加 数量
			newMap.put("Spec", spec);
			
			//闪烁次数
			newMap.put("Light", map.get("Light"));

			// map.put("LightTimes", 3);
			newMap.put("RSend", "1");
			newMap.put("RSendSum", 1);
			newMap.put("Enabled", false);
			newList.add(newMap);
		}
		LabelInf labelInf = new LabelImpl();
		Map<String, Object> returnMap = labelInf.labelData(newList, url);
		if (null != returnMap) {
			String status = (String) returnMap.get("Status");
			if (null != status && "10000".equals(status)) {// 更新电子标签状态成功
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	// 获取当天时间
	public static String getNowTime(String dateformat) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// 可以方便地修改日期格式
		String hehe = dateFormat.format(now);
		return hehe;
	}

	public static String getObjectId() {
		String dateStr = getNowTime("yyyyMMddHHmmssSSS");
		return "10" + dateStr;
	}

}
