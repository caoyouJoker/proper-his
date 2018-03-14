package com.javahis.ui.emr;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;

/**
 * 
 * 泰心记录外挂功程序功能;
 * @author lixiang
 *
 */
public class EMRMedRecTXControl extends TControl {
	
	TComboBox boxFileName ;
	private String caseNo="";
	
	public EMRMedRecTXControl() {
    }
	
	 public void onInit() {
	        Object obj = this.getParameter();
	        if (obj != null) {
	            String type = ( (TParm) obj).getValue("TYPE");
	            if ("1".equals(type)) {
	                getTRadioButton("ADDLOW").setSelected(true);
	            }
	            if ("2".equals(type)) {
	                getTRadioButton("ADDAFTER").setSelected(true);
	            }
	            caseNo = ( (TParm) obj).getValue("CASE_NO");
	            //String fileName = ( (TParm) obj).getValue("FILE_NAME");
	            String emtFileName = ( (TParm) obj).getValue("SUBTEMPLET_FILE");
	            //String emtFileName=( (TParm) obj).getValue("EMT_FILENAME");

	            boxFileName = (TComboBox)this.getComponent("FILE_NAME");
	           //this.messageBox("caseNo" + caseNo);
	           //this.messageBox("EMRMedRecTXControl emtFileName" + emtFileName);
	            //取依附文件
	            boxFileName.setSQL(
	                "SELECT FILE_NAME,DESIGN_NAME FROM EMR_FILE_INDEX WHERE CASE_NO='" +
	                caseNo + "' AND FILE_NAME LIKE '%"+emtFileName+"%' ORDER BY CREATOR_DATE DESC");
	            
	            boxFileName.retrieve();
	            boxFileName.setSelectedIndex(1);

	            //this.setValue("FILE_NAME", fileName);
	            //通过case_no 取得 file_name及design_name;

	            this.setValue("SYS_DATE", StringTool.getTimestamp(new Date()));
	            
	            //上限为入院时间
	            this.setValue("ADM_DATE",
	                          StringTool.getString( ( (TParm) obj).
	                                               getTimestamp("ADM_DATE"),
	                                               "yyyy/MM/dd HH:mm:ss"));
	            
	            //上限为出院时间
	            if ( ( (TParm) obj).getValue("DS_DATE").length() == 0) {
	                this.setValue("DS_DATE", "");
	            }
	            else {
	                this.setValue("DS_DATE",
	                              StringTool.getString( ( (TParm) obj).
	                    getTimestamp("DS_DATE"), "yyyy/MM/dd HH:mm:ss"));
	            }

	        }

	    }

	    /**
	     * 确定
	     */
	    public void onOk() {
	        //必须选择下拉框;
	        String strFileNameMain=boxFileName.getValue();
	        //this.messageBox("==strFileNameMain=="+strFileNameMain);
	        if(strFileNameMain.equals("")){
	            this.messageBox("请选择病程标题！");
	            return;
	        }
	        
	        String sql =  "SELECT SEALED_STATUS FROM EMR_FILE_INDEX WHERE CASE_NO='" +
	                caseNo + "' AND FILE_NAME = '"+strFileNameMain+"'";
	        TParm sealedParm = new TParm(TJDODBTool.getInstance().select(sql));
	        if(sealedParm.getCount() > 0){
	        	 if("3".equals(sealedParm.getValue("SEALED_STATUS", 0))){
	        		 this.messageBox("当前主病程已封存,不允许添加子病程");
	 	             return;
	 	        }
	        }
	       

	        //当前插入时间是否已经超过下限
	        Timestamp sysDate = (Timestamp)this.getValue("SYS_DATE");
	        long sDate = strToDate(StringTool.getString(sysDate,
            "yyyy/MM/dd HH:mm:ss"), "yyyy/MM/dd HH:mm:ss").getTime();
	        //
	        long aDate = 0;
	        if (this.getValueString("ADM_DATE").length() != 0) {
	            Timestamp admDate = StringTool.getTimestamp(this.getValueString(
	                "ADM_DATE"), "yyyy/MM/dd HH:mm:ss");
	            aDate = strToDate(StringTool.getString(admDate,
	                "yyyy/MM/dd HH:mm:ss"), "yyyy/MM/dd HH:mm:ss").getTime();
	        }
	        //
	        if (sDate < aDate && aDate != 0) {
	            this.messageBox("已经超过病程时间上限！");
	            return;
	        }	        
	       
	        //
	        long dDate = 0;
	        if (this.getValueString("DS_DATE").length() != 0) {
	            Timestamp dsDate = StringTool.getTimestamp(this.getValueString(
	                "DS_DATE"), "yyyy/MM/dd HH:mm:ss");
	            dDate = strToDate(StringTool.getString(dsDate,
	                "yyyy/MM/dd HH:mm:ss"), "yyyy/MM/dd HH:mm:ss").getTime();
	        }
	        if (sDate > dDate && dDate != 0) {
	            this.messageBox("已经超过病程时间上限！");
	            return;
	        }
	        //
	        TParm returnParm = new TParm();

	        if (getTRadioButton("ADDAFTER").isSelected()) {
	            returnParm.setData("EMRADD_DATE",
	                               StringTool.getString(sysDate, "yyyy/MM/dd HH:mm:ss"));
	        }
	        if (getTRadioButton("ADDLOW").isSelected()) {
	            returnParm.setData("EMR_DATE",
	                               StringTool.getString(sysDate, "yyyy/MM/dd HH:mm:ss"));
	        }
	            //记录的时间戳   ， 为动态为抓取框赋值 	        
	             returnParm.setData("RECORD_TIME", sysDate);
	         
	             //追加病历文件名
	        	returnParm.setData("FILE_NAME_MAIN", strFileNameMain);
	        

	        this.setReturnValue(returnParm);
	        this.closeWindow();
	    }


	    /**
	     * 将短时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	     * @param strDate String
	     * @return Date
	     */
	    public Date strToDate(String strDate, String forMat) {
	        SimpleDateFormat formatter = new SimpleDateFormat(forMat);
	        ParsePosition pos = new ParsePosition(0);
	        Date strtodate = formatter.parse(strDate, pos);
	        return strtodate;
	    }

	    /**
	     * 得到TRadioButton
	     * @param tag String
	     * @return TRadioButton
	     */
	    public TRadioButton getTRadioButton(String tag) {
	        return (TRadioButton)this.getComponent(tag);
	    }

	    /**
	     * 返回数据库操作工具
	     * @return TJDODBTool
	     */
	    public TJDODBTool getDBTool() {
	        return TJDODBTool.getInstance();
	    }
}
