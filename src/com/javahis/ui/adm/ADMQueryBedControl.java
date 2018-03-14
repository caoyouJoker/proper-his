package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import jdo.adm.ADMInpTool;
import jdo.sys.SYSBedTool;
import com.dongyang.ui.TCheckBox;

/**
 * <p>Title: 床位选择</p>
 *
 * <p>Description: 床位选择</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk
 * @version 1.0
 */
public class ADMQueryBedControl
    extends TControl {

    TParm acceptParm = new TParm();
    private String type ;
    public void onInit() {
        acceptParm = (TParm)this.getParameter();
        type = acceptParm.getValue("TYPE") ;  //chenxi modify 判断入口是预约住院、住院登记、转床
        initCombo();
        initTable();

    }

    /**
     * 接受参数赋值
     */
    public void initCombo() {
        this.setValue("DEPT_CODE", acceptParm.getData("DEPT_CODE"));
        this.setValue("STATION_CODE", acceptParm.getData("STATION_CODE"));
       
    }

    /**
     * 检索床位TABLE显示
     */
    public void initTable() {
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString());
        parm.setData("STATION_CODE", getValue("STATION_CODE").toString());
        if(type.equals("RESV")){
        	 parm.setData("DR_APPROVE_FLG", "Y");	
        }
        TParm result = ADMInpTool.getInstance().QueryBed(parm);
        TTable table = (TTable)this.callFunction("UI|BED|getThis");
        table.setParmValue(result);
    }

    /**
     * 床位检索
     */
    public void onSelectBed() {
        TCheckBox checkbox = (TCheckBox)this.callFunction("UI|ALLO|getThis");
        TParm parm = new TParm();
        if (checkbox.isSelected()) {
            parm.setData("ALLO_FLG", "N");
        }
        if(type.equals("RESV")){
       	 parm.setData("DR_APPROVE_FLG", "Y");	
       }
        parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString());
        parm.setData("STATION_CODE", getValue("STATION_CODE").toString());
        parm.setData("STATION_CODE", getValue("STATION_CODE").toString());
        parm.setDataN("ROOM_CODE", this.getValue("ROOM_CODE"));
        parm.setDataN("BED_CLASS_CODE", this.getValue("BED_CLASS_CODE"));
        TParm result = ADMInpTool.getInstance().QueryBed(parm);
        TTable table = (TTable)this.callFunction("UI|BED|getThis");
        table.setParmValue(result);
    }

    /**
     * 点选床位
     */
    public void onBedTable() {
        if (!check()) {//审核是否被占用
            return;
        }
        TTable table = (TTable)this.callFunction("UI|BED|getThis");

        TParm parm = table.getParmValue();

        TParm queryParm = new TParm();
        //病床CODE
        queryParm.setData("BED_NO",
                          parm.getValue("BED_NO", table.getSelectedRow()));
        TParm sendParm = SYSBedTool.getInstance().queryBedYellowRed(queryParm);
        this.setReturnValue(sendParm);
        this.closeWindow();
    }
    /**
     * 检核床位
     * @return boolean
     */
    public boolean check() {
    	if(!acceptParm.getValue("HAVEBEDNO").equals("")&&acceptParm.getValue("HAVEBEDNO")!=null){
    		
    		this.messageBox("该病患已有床位,不可重复预约");
    		return false;
    	}
        TTable table = (TTable)this.callFunction("UI|BED|getThis");
        if (table.getSelectedRow() < 0) {
            this.messageBox("未选床位");
            return false;
        }
        String pat = table.getValueAt(table.getSelectedRow(), 1).toString();
        //=============shibl 20130106 add======多人点同一个床未刷新页面=============================
        TParm  parm=table.getParmValue().getRow(table.getSelectedRow());
        TParm  inParm=new TParm();
        inParm.setData("BED_NO", parm.getValue("BED_NO"));
        TParm result = ADMInpTool.getInstance().QueryBed(inParm);
        String APPT_FLG=result.getCount()>0?result.getValue("APPT_FLG",0):"";
        String ALLO_FLG=result.getCount()>0?result.getValue("ALLO_FLG",0):"";
        String BED_STATUS=result.getCount()>0?result.getValue("BED_STATUS",0):"";
        //======================  chenxi modify 占床可以被预约的修改
        if(type.equals("RESV") ){
        	 if ("Y".equals(table.getValueAt(table.getSelectedRow(), 0).toString())||APPT_FLG.equals("Y")) {
        		 this.messageBox("此床已预约！不可再次预约");
                 initTable();
                 return false; 
        	 }
        	 if((parm.getValue("MEDDISCH_DATE").toString().equals("") || parm.getValue("MEDDISCH_DATE")==null) 
        			 && ("Y".equals(pat)||ALLO_FLG.equals("Y"))){
        		 this.messageBox("此病患没有预约出院时间，此床不可预约");
                 initTable();
                 return false;
        	 }
        }
        else {
        if ("Y".equals(pat)||ALLO_FLG.equals("Y")) {
            this.messageBox("此床已占用");
            initTable();
            return false;
        }
        if ("Y".equals(table.getValueAt(table.getSelectedRow(), 2).toString())||BED_STATUS.equals("1")) {
            this.messageBox("此床已被包床");
            initTable();
            return false;
        }
        if ("Y".equals(table.getValueAt(table.getSelectedRow(), 0).toString())||APPT_FLG.equals("Y")) {
        	int check = this.messageBox("消息", "此床已被预订，是否进继续？", 0);
			if (check != 0) {
				 initTable();
				return  false;
			}
            return true;
        }
        }
        return true;
    }
    /**
     * 空床
     */
    public void onAllo() {
        TCheckBox checkbox = (TCheckBox)this.callFunction("UI|ALLO|getThis");
        if (checkbox.isSelected()) {
            TParm parm = new TParm();
            parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString());
            parm.setData("STATION_CODE", getValue("STATION_CODE").toString());
            parm.setData("ALLO_FLG", "N");
            if(type.equals("RESV")){
           	 parm.setData("DR_APPROVE_FLG", "Y");	
           }
            TParm result = ADMInpTool.getInstance().QueryBed(parm);
            TTable table = (TTable)this.callFunction("UI|BED|getThis");
            table.setParmValue(result);
        }
        else {
            onSelectBed();
        }
    }
}
