package com.javahis.ui.opd;

import java.util.List;

import jdo.cdss.SysUtil;
import jdo.odi.OdiObject;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public abstract class CDSSStationDrools {
	
	protected TParm freqParm;

	protected List<String> freqList;
	
	private final String SQL = "SELECT FREQ_CODE, CYCLE, FREQ_TIMES FROM SYS_PHAFREQ";
	
	private static final String SQL_CDSS_FLG = "SELECT CDSS_FLG, CDSS_O, CDSS_I FROM SYS_REGION WHERE REGION_CODE = '#'";
	
	protected SysUtil sysUtil = new SysUtil();
	
	@SuppressWarnings("unchecked")
	public CDSSStationDrools(){
		freqParm = new TParm(TJDODBTool.getInstance().select(SQL));
		freqList = (List<String>) freqParm.getData("FREQ_CODE");
	}
	
	protected int getFreqFieldByKey(String field, String key) {
		int index = freqList.indexOf(key);
		if (index >= 0) {
			return freqParm.getInt(field, index);
		} else {
			return index;
		}

	}

	protected int getFreqCycle(String key) {
		return getFreqFieldByKey("CYCLE", key);
	}

	protected int getFreqFreqTimes(String key) {
		return getFreqFieldByKey("FREQ_TIMES", key);
	}
	
	/**
	 * 执行规则
	 * 
	 * @return
	 */
	public abstract boolean fireRules();
	
	public abstract void onCdssCal(boolean flg);
	
	 public abstract void updateCkbLog(TParm updateParm); 
	 
	 public abstract boolean fireRulesOrder();
	
	/**
	 * 执行规则
	 * 
	 * @return
	 */
	public abstract boolean fireRules(OdiObject odiObject, int i);
	
	public static boolean isCdssOn(String regionCode){
		String sql = SQL_CDSS_FLG.replace("#", regionCode);
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(p.getValue("CDSS_FLG", 0))){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isCdssOnO(String regionCode){
		String sql = SQL_CDSS_FLG.replace("#", regionCode);
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(p.getValue("CDSS_O", 0))){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isCdssOnI(String regionCode){
		String sql = SQL_CDSS_FLG.replace("#", regionCode);
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(p.getValue("CDSS_I", 0))){
			return true;
		}else{
			return false;
		}
	}
	
    

}
