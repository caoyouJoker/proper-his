package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.util.Date;
/**
*
* <p>Title:�޸ļ������ʱ��</p>
*
* <p>Description: �޸ļ������ʱ��</p>
*
* <p>Copyright: Copyright (c) caoyong 20131227/p>
*
* <p>Company: BlueCore</p>
*
* @author caoyong
* @version 1.0
*/

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

public class OdoMainSeeDrTimeControl extends TControl{
	
	/**
	 * �������
	 */
	private TParm parmmeter;
	private String mrno;
	private String caseno;
	/**
	 * ��ʼ��
	 */
    public void onInit(){
    	Timestamp date = StringTool.getTimestamp(new Date());
    	this.parmmeter = new TParm();
		Object obj = this.getParameter();
		if (obj.toString().length() != 0 || obj != null) {
			this.parmmeter = (TParm) obj;
			if(parmmeter.getValue("SEEN_DR_TIME").length()>0){
				this.setValue("SEEN_DR_TIME", parmmeter.getValue("SEEN_DR_TIME"));
			 }else{
				 this.setValue("SEEN_DR_TIME",date.toString().substring(0, 19).replace("-","/"));
			 }
			
			       mrno=parmmeter.getValue("MR_NO");
			       caseno=parmmeter.getValue("CASE_NO");
		}
    	 
		
    	
    }
    /**
     * �޸ļ������ʱ��
     */
    public void onSeeDrTime(){
    	String seedate = StringTool.getString(TypeTool.getTimestamp(getValue("SEEN_DR_TIME")), "yyyy/MM/dd HH:mm:ss");
    	
    	String sql="UPDATE REG_PATADM SET SEEN_DR_TIME=to_date('"+seedate+"','yyyy/MM/dd hh24:mi:ss') " +
    			   "WHERE MR_NO='"+mrno+"' AND CASE_NO='"+caseno+"' AND ADM_TYPE='E' ";
    	
    	TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
      		 this.messageBox("�޸�ʧ��");
      		 return ;
      	     }
      		 this.messageBox("�޸ĳɹ�");
      		 this.callFunction("UI|onClose");
    }

}
