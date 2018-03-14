package com.javahis.ui.spc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jdo.ind.Constant;
import jdo.ind.ElectronicTagsImpl;
import jdo.ind.ElectronicTagsInf;
import jdo.label.LabelImpl;
import jdo.label.LabelInf;

import org.apache.http.message.BasicNameValuePair;

import com.dongyang.control.TControl;

/**
 * 
 * <p>Title:电子标签总控制 </p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class EleTagControl  extends TControl  {
	
	private static final EleTagControl INSTANCE =new EleTagControl();
	
	public static EleTagControl getInstance(){
        return INSTANCE;
    }

	
	public  void login(){
		ElectronicTagsInf eti = new ElectronicTagsImpl() ;
		Map<String, Object> map = eti.login("admin", "123");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
					.next();
			Constant.parameters.clear();
		}
		 
		Constant.parameters.add(0,new BasicNameValuePair("Token", (String)map.get("Token")));
		Constant.parameters.add(1,new BasicNameValuePair("UserId", (String)map.get("UserId")));
		Constant.parameters.add(2,new BasicNameValuePair("RoleId", (String)map.get("RoleId")));
	}
	
    /**
     * 更新电子标签
     * @param bastkId  电子标签ID
     * @param nameInfo  
     * @param spec
     * @param num
     * @param lightNum TODO
     * @return
     * @author 
     * @date 
     */
	  public boolean sendEleTag(String bastkId,String nameInfo,String spec ,String num, int lightNum){
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		if(null == uuid )
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
		
		if(spec != null && !spec.equals("")){
			spec = spec.substring(0,12);
		}
		// 第二回 显示病案号 和性别
		m.put("Spec", spec + " " + num);
		// 第三行 电子标签二维条码含义
		 
		m.put("ShelfNo", bastkId);
	 
		// 闪烁次数
		m.put("Light", lightNum);
		// 是否亮灯：true:亮
		m.put("Enabled", true);
		
		Iterator it = m.entrySet().iterator();
	 
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		// 调用电子标签接口
		Map<String, Object> map = eti.cargoUpdate(m);
		
		
		if(null != map){
			it = map.entrySet().iterator();
			 
	    	String status = (String) map.get("Status");
	    	if(null != status && "10000".equals(status)){//更新电子标签状态成功
	    		return true;
	    	}else{
	    		return false;
	    	}			
		}else{
			return false;
		}
	  }
	
	
	  /**
	   * 二代电子标签
	   * @param list
	   * @param url
	   * @return
	   */
	  public boolean sendNewEleTag(List<Map<String, Object>> list,String url ){
		  
		  List<Map<String, Object>> newList = new ArrayList<Map<String,Object>>();
		  
		  String objectId =  getObjectId();
		  for(int i = 0 ; i < list.size() ; i++){
			   Map<String, Object> map = (Map<String, Object>)list.get(i);
			   Map<String ,Object> newMap = new LinkedHashMap<String, Object>();
			   newMap.put("ObjectId",objectId);
			   newMap.put("ObjectType", 1);
			   newMap.put("ObjectName", "medBasket");
			   newMap.put("TagNo", map.get("TagNo"));
			   newMap.put("APRegion", map.get("APRegion"));
			   newMap.put("ShelfNo", "10411515515");
			   newMap.put("ProductName",map.get("ProductName"));
			   String spec = (String) map.get("SPECIFICATION") ;
			   //电子标签显示第二行数据 规格 加 数量
			   newMap.put("Spec", spec);
			   newMap.put("Light", map.get("Light"));
				
				//map.put("LightTimes", 3);
			   newMap.put("RSend", "1");
			   newMap.put("RSendSum", 3);
			   newMap.put("Enabled", false);
			   newList.add(newMap);
		  }
		  LabelInf labelInf = new LabelImpl();
		  Map<String, Object> returnMap  = labelInf.labelData(newList, url);
		  if(null != returnMap){
			  String status = (String) returnMap.get("Status");
		    	if(null != status && "10000".equals(status)){//更新电子标签状态成功
		    		return true;
		    	}else{
		    		return false;
		    	}			
		  }else{
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
	
	public static String getObjectId(){
		String dateStr = getNowTime("yyyyMMddHHmmssSSS");
		return "10"+dateStr ;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
