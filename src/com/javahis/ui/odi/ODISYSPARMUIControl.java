package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TIOM_AppServer;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.javahis.util.OrderUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author Miracle
 * @version 1.0
 */
public class ODISYSPARMUIControl extends TControl {
    /**
     * 动作类名称
     */
    private String actionName = "action.odi.ODIAction";
    /**
     * 临时用药预设频次
     */
    private String uddStatCode="";
    private String odiStatCode="";
    public void onInit(){
        //初始化界面
        this.initPage();
    }
    public void initPage(){
        TParm parm = new TParm(this.getDBTool().select("SELECT * FROM ODI_SYSPARM"));
        TParm temp = parm.getRow(0);
        this.setValue("NS_CHECK_FLG",temp.getBoolean("NS_CHECK_FLG"));
        this.setValue("DSPN_TIME",StringTool.getTimestamp(temp.getValue("DSPN_TIME"),"HHmm"));
        this.setValue("START_TIME",StringTool.getTimestamp(temp.getValue("START_TIME"),"HHmm"));
        this.uddStatCode = temp.getValue("UDD_STAT_CODE");
        this.odiStatCode = temp.getValue("ODI_STAT_CODE");
        this.setValue("UDD_STAT_CODE",temp.getValue("UDD_STAT_CODE"));
        this.setValue("ODI_STAT_CODE",temp.getValue("ODI_STAT_CODE"));
        this.setValue("ODI_DEFA_FREG",temp.getValue("ODI_DEFA_FREG"));
        this.setValue("IVA_EXPANDTIME",StringTool.getTimestamp(temp.getValue("IVA_EXPANDTIME"),"HHmm"));
        this.setValue("DELAY_TIME",temp.getValue("DELAY_TIME"));
        this.setValue("DELAY_SUFFIX",temp.getValue("DELAY_SUFFIX"));
        //===zhangp 20120702 start
        this.setValue("DS_MED_DAY",temp.getValue("DS_MED_DAY"));
        //===zhangp 20120702 end
        // <------ identify by shendr 20130808 start
        this.setValue("IVA_STAT",temp.getValue("IVA_STAT"));
        this.setValue("IVA_FIRST",temp.getValue("IVA_FIRST"));
        this.setValue("IVA_UD",temp.getValue("IVA_UD"));
        this.setValue("IVA_OP",temp.getValue("IVA_OP"));//术中医嘱
        // end ----->
        //20150515 wangjc add start 不由PIVAs配置的病区
        TParm stationParm = new TParm(this.getDBTool().select("SELECT 'N' AS ACTIVE_FLG,STATION_CODE,STATION_DESC FROM SYS_STATION"));
        TParm stationFlgParm = new TParm(this.getDBTool().select("SELECT NPIVAS_STATION FROM ODI_SYSPARM"));
        String stationCode = stationFlgParm.getValue("NPIVAS_STATION",0);
        if(!stationCode.equals("") && stationCode != null){
        	String [] station = stationCode.split(";");
        	for(int i=0;i<stationParm.getCount("STATION_CODE");i++){
        		for(int j=0;j<station.length;j++){
        			if(stationParm.getValue("STATION_CODE", i).equals(station[j])){
        				stationParm.setData("ACTIVE_FLG", i, "Y");
        				break;
        			}
        		}
        	}
        }
        TTable table = (TTable) this.getComponent("STATION_TABLE");
        table.setParmValue(stationParm);
      //20150515 wangjc add end 不由PIVAs配置的病区
    }
    public void onSave(){
        TParm parm = new TParm();
        String dspnTime = StringTool.getString((Timestamp)this.getValue("DSPN_TIME"),"HHmm");
        String startTime = StringTool.getString((Timestamp)this.getValue("START_TIME"),"HHmm");
//        String ivaExpandTime = StringTool.getString((Timestamp)this.getValue("IVA_EXPANDTIME"),"HHmm");//20150515 wangjc modify
        //20150515 wangjc add start
        TTable table = (TTable) this.getComponent("STATION_TABLE");
        TParm stationCode = table.getParmValue();
        String station = "";
        for(int i=0;i<stationCode.getCount("STATION_CODE");i++){
        	if(stationCode.getValue("ACTIVE_FLG", i).equals("Y")){
        		station += stationCode.getValue("STATION_CODE", i)+";";
        	}
        }
        if(!station.equals("")){
        	station = station.substring(0, station.length()-1);
        }
      //20150515 wangjc add end
        String sqlArray[] = new String[]{"UPDATE ODI_SYSPARM SET NS_CHECK_FLG='"+this.getValue("NS_CHECK_FLG")+"',DSPN_TIME='"+dspnTime+"',START_TIME='"+startTime+"',UDD_STAT_CODE='"+this.getValue("UDD_STAT_CODE")+"',ODI_STAT_CODE='"+this.getValue("ODI_STAT_CODE")+"',ODI_DEFA_FREG='"+this.getValue("ODI_DEFA_FREG")
//        		+"',IVA_EXPANDTIME='"+ivaExpandTime//20150515 wangjc modify
        		+"',OPT_DATE=SYSDATE,OPT_USER='"+Operator.getID()+"',OPT_TERM='"+Operator.getIP()+"',DELAY_TIME='"+this.getValue("DELAY_TIME")+"',DELAY_SUFFIX='"+this.getValue("DELAY_SUFFIX")+"'" +
        		//====zhangp 20120702 start
        		",DS_MED_DAY="+this.getValue("DS_MED_DAY")
        		//====zhangp 20120702 end
        		// <--------- identify by shendr 2013.08.08
        		+",IVA_STAT='"+this.getValue("IVA_STAT")+"',IVA_FIRST='"+this.getValue("IVA_FIRST")+"',IVA_UD='"+this.getValue("IVA_UD")+"'"
        				+ ",IVA_OP='"+this.getValue("IVA_OP")+"'"//术中医嘱
        		+",NPIVAS_STATION='"+station+"'"};//20150515 wangjc add
        		// ---------->
        parm.setData("ARRAY",sqlArray);
        TParm actionParm = TIOM_AppServer.executeAction(actionName,"saveOrder", parm);
            if(actionParm.getErrCode()<0)
                this.messageBox("保存失败！");
            else
                this.messageBox("保存成功！");
    }
    /**
    * 返回数据库操作工具
    * @return TJDODBTool
    */
   public TJDODBTool getDBTool() {
       return TJDODBTool.getInstance();
   }
   /**
    * 选择事件
    */
   public void onSel(Object obj){
       String temp = "";
       if("PHA".equals(obj)){
           temp = this.getValueString("UDD_STAT_CODE");
           if (!OrderUtil.getInstance().isSTFreq(temp)) {
               this.messageBox("不是临时用药频次！");
               this.setValue("UDD_STAT_CODE", this.uddStatCode);
               return;
           }
           this.setValue("UDD_STAT_CODE",temp);
           this.uddStatCode = temp;
       }
       if("TRT".equals(obj)){
           temp = this.getValueString("ODI_STAT_CODE");
           if (!OrderUtil.getInstance().isSTFreq(temp)) {
               this.messageBox("不是临时处置频次！");
               this.setValue("ODI_STAT_CODE", this.odiStatCode);
               return;
           }
           this.setValue("ODI_STAT_CODE",temp);
           this.odiStatCode = temp;
       }
   }
   
   /**
	 * 复选框事件
	 * wangjc
	 */
	public void onTableCheckBoxClicked() {
		TTable table = this.getTTable("STATION_TABLE");
		int row = table.getSelectedRow();
		if ("N".equals(table.getItemString(row, "ACTIVE_FLG"))) {
			table.setItem(row, "ACTIVE_FLG", "Y");
		} else if("Y".equals(table.getItemString(row, "ACTIVE_FLG"))){
			table.setItem(row, "ACTIVE_FLG", "N");
		}

	}
   
   /**
	 * 全选
	 * wangjc
	 */
	public void onSelectAll() {
		TTable table = getTTable("STATION_TABLE");
		table.acceptText();
		if (table.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setItem(i, "ACTIVE_FLG", getValueString("SELECT_ALL"));
		}
	}
	
	/**
	 * 得到checkbox控件
	 * 
	 * @param tagName
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
	
	private TTable getTTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
}
