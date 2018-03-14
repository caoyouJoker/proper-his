package jdo.dev;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title:设备折旧计算</p>
 *
 * <p>Description:设备折旧计算 </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:javahis </p>
 *
 * @author  fux
 * @version 1.0
 */
public class DevDepTool extends TJDOTool{ 
	
	/**
	 * 实例
	 */ 
	private static DevDepTool instanceObject;
	 
	/**
	 * 得到实例 
	 * @return
	 */
	public static DevDepTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevDepTool();
		}
		return instanceObject;
	}
	
	/**
	 * 折旧算法
	 * @param parm
	 * @return
	 */
	public TParm selectSeqDevInf(TParm parm,double unitPrice,String inWarehouseDate) {
		DecimalFormat formatObject = new DecimalFormat("###########0.00");	
		//传回折旧年限和购入日期   
		TParm parmDep = new TParm();
		Double depYear  = parm.getDouble("DEP_DEADLINE",0);  
		Double depMonth  =  depYear*12;
		//月折旧 
		Double mdepPrice = unitPrice/depMonth ;
		formatObject.format(mdepPrice); 
		//(非无形资产)已折旧月数
		Double MMonth1 = 0.00;  
		Double MMonth2 = 0.00;
		MMonth1 = (double) Month(inWarehouseDate); 
		//(无形资产)已折旧月数  
	    MMonth2 = (double) (Month(inWarehouseDate) + 1); 		
		//判断无形资产标记 
		if("Y".equals(parm.getData("INTANGIBLE_FLG",0))){
			//折旧月从当月算起
			//累计折旧   
			Double depPrice = mdepPrice * MMonth2; 
			Double currPrice =  unitPrice -  depPrice;
			parmDep.setData("DEP_PRICE",formatObject.format(depPrice));
			parmDep.setData("CURR_PRICE",formatObject.format(currPrice));
		}
		else{ 
			//折旧月从下一个月算起
			Double depPrice = mdepPrice * MMonth1;
			Double currPrice =  unitPrice -  depPrice;
			parmDep.setData("DEP_PRICE",formatObject.format(depPrice));
			parmDep.setData("CURR_PRICE",formatObject.format(currPrice));
		}  
		parmDep.setData("MDEP_PRICE",formatObject.format(mdepPrice));
		//得到折旧年限
		//折旧年限换算成折旧月数 
		//原值/折旧月数 = 月折旧值
		//已折旧月数* 月折旧值 = 累计折旧数
		//原值-累计折旧数 = 现值
		//非无形设备当月不折旧(现值为0无折旧) 
		//无形设备单月计提(当月就算)
		//增加月底过账，计算折旧？？？     
		return parmDep; 
	} 
	/** 
	 * 计算已折旧月数方法
	 * @param parm
	 * @return 
	 */ 
	public int Month(String InwareDate){
		//depDate入库日期
		GregorianCalendar ApDate = new GregorianCalendar(); //系统日期
	    String sYear = Integer.toString(ApDate.get(Calendar.YEAR));
	    String sMonth = Integer.toString(ApDate.get(Calendar.MONTH) + 1);
		int year1 = Integer.parseInt(sYear);
		int year2 = Integer.parseInt(InwareDate.substring(0,4));
		int month1 = Integer.parseInt(sMonth);
		int month2 = Integer.parseInt(InwareDate.substring(6,7));
	    int year = (year2-year1)*12;
	    int month = month2-month1;
	    int depMonth = year-month; 
		//当日日期 ---取得年数,再取得月数  
		//入库日期 ---取得年数,再取得月数 
		//年数相减得到差一个就是12
		//月数相减 
		return depMonth; 
	} 
	
	/** 
	 * 月结更新折旧相关金额
	 * @param parm
	 * @return  
	 */   
	public TParm UpdateMonthDep(TParm parm){ 
           String sql = " UPDATE DEV_STOCKDD SET "+ 
				        " DEP_PRICE='"+parm.getValue("DEP_PRICE",0)+"' ,"+  
				        " CURR_PRICE='"+parm.getValue("CURR_PRICE",0)+"' "+  
			            " WHERE DEV_CODE='"+parm.getValue("DEV_CODE",0)+"' " + 
			            " AND DEVSEQ_NO ='"+parm.getValue("DEVSEQ_NO",0)+"' " +
			            " AND REGION_CODE = '"+parm.getValue("REGION_CODE",0)+"' "; 
           TParm result = new TParm(TJDODBTool.getInstance().update(sql)); 
			if (result.getErrCode() < 0) {
				return result; 
			} 
           return result; 
              
	}


}
