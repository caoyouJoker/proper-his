package com.javahis.ui.inp;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
*
* <p>Title: 取消会诊</p>
*
* <p>Description: 取消会诊</p>
*
* <p>Copyright: Copyright (c) caoyong 20139029</p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/
public class INPConsCancelControl extends TControl {
	/**
	 * 传入参数
	 */
	private TParm parmmeter;
	/**
	 * 会诊单号
	 */
	private String consCode;
	
	public void setConsCode(String consCode){
		this.consCode=consCode;
	}
	
	public String getConsCode(){
		return this.consCode;
	}
	/**
	 * 初始化
	 */
    public void onInit(){
    	this.parmmeter = new TParm();
		Object obj = this.getParameter();
		if (obj.toString().length() != 0 || obj != null) {
			this.parmmeter = (TParm) obj;
		}
		Timestamp date = SystemTool.getInstance().getDate();
		this.consCode = parmmeter.getValue("CONS_CODE");// 
		this.setValue("MR_NO", parmmeter.getValue("MR_NO"));
		this.setValue("IPD_NO", parmmeter.getValue("IPD_NO"));
		this.setValue("PAT_NAME", parmmeter.getValue("PAT_NAME"));
		this.setValue("SEX_CODE", parmmeter.getValue("SEX_CODE"));
		this.setValue("DEPT_CODE", parmmeter.getValue("DEPT_CODE"));
		this.setValue("STATION_CODE", parmmeter.getValue("STATION_CODE"));
		this.setValue("VS_DR_CODE", parmmeter.getValue("VS_DR_CODE"));
		this.setValue("IN_DATE", parmmeter.getValue("IN_DATE"));
		this.setValue("CANCEL_DATE", parmmeter.getValue("CONS_CODE"));
		this.setValue("CANCEL_DATE",date.toString().substring(0,10).replace('-', '/'));//初始化取消时间
		this.setValue("CANCEL_DR_CODE",parmmeter.getValue("CANCEL_DR_CODE"));
		this.setValue("CONCEL_CAUSE_CODE", "01");
    }
    /**
     * 取消会诊
     */
    public void onSave(){
    		
    	if("".equals(this.getValueString("CONCEL_CAUSE_CODE"))||this.getValueString("CONCEL_CAUSE_CODE")==null){
    		this.messageBox("取消原因不能为空");
    		this.grabFocus("CONCEL_CAUSE_CODE");
    		return;
    	}
    	
    	String canceldate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "CANCEL_DATE")), "yyyy/MM/dd");
    	String sql="UPDATE INP_CONS " +
    			   "SET CONCEL_CAUSE_CODE='"+this.getValueString("CONCEL_CAUSE_CODE")+"', " +
    			   "OPT_DATE=to_date('"+canceldate+"','yyyy/mm/dd'), "+
    			   "OPT_USER='"+this.getValueString("CANCEL_DR_CODE")+"', "+
    			   "OPT_TERM='"+Operator.getIP()+"' " +
    			   "WHERE CONS_CODE='"+this.consCode+"'" ;
    	TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
      		 this.messageBox("取消失败");
      		 return ;
      	     }
      		 this.messageBox("取消成功");
      		 this.callFunction("UI|onClose");
    	
    }
}
