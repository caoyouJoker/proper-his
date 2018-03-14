package com.javahis.ui.bms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.SYSOpdComOrderControl;
/** 
 * <p>
 * Title: 血品跟踪报表
 * Description: 血品跟踪报表
 * </p>
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author kangy 2016.05.30
 * @version 1.0
 */
public class BMSFollowQueryControl extends TControl {
private TTable table;
private TRadioButton radioButton01,radioButton02;
	public void onInit() {
		table=this.getTable("TABLE");
		radioButton02=this.getRadioButton("XPMC");
		radioButton01=this.getRadioButton("HSSJ");
		  Timestamp date = SystemTool.getInstance().getDate();
		 // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        table.removeRowAll();
    }
 
    /**
     * 查询方法
     */
    public void onQuery() {
    	SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
    	String startDate=df.format(this.getValue("START_DATE"));
    	String endDate=df.format(this.getValue("END_DATE"));
    	String sql="SELECT BLD_CODE,SUBCAT_CODE,BLOOD_VOL,RECEIVED_DATE,BLDTRANS_TIME,BLDTRANS_END_TIME,OUT_DATE" +
    			" FROM BMS_BLOOD WHERE  OUT_DATE BETWEEN TO_DATE ('"+startDate+"','YYYYMMDDHH24MISS')" +
    			" AND TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')";
    	   String bld_code = this.getValueString("BLD_CODE");
           if (bld_code != null && bld_code.length() > 0) {
             sql+=" AND BLD_CODE= '"+bld_code+"'";
           }
           if(radioButton01.isSelected()){//核收时间排序
           sql+="ORDER BY RECEIVED_DATE"; 
           }
           if(radioButton02.isSelected()){//血品名称排序
        	   sql+="ORDER BY SUBCAT_CODE"; 
           }
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    if(result.getCount()<=0){
    	messageBox("没有符合条件的数据");
    	onClear();
    return;
    }
    	 this.getTable("TABLE").setParmValue(result);
    	 
    }

    /**
     * 清空方法
     */
    public void onClear() {
    	setValue("BLD_CODE", "");
       onInit();
        
    }
    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
    	return (TRadioButton) getComponent(tagName);
    }
}
