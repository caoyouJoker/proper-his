package com.javahis.ui.spc;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;
import jdo.spc.SPCDispenseAssignWithLiquidTool;
import jdo.spc.SPCDispenseCountWithLiquidHosStrTool;
import jdo.spc.SPCDispenseOutAssginNormalTool;
import jdo.spc.SPCSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.ui.spc.util.ElectronicTagUtil;


/**
 * 
 * <p>
 * Title:住院药房普药-静配配药核对control
 * </p>
 * 
 * <p>
 * Description:住院药房普药-静配配药核对control
 * </p>
 * 
 * <p>
 * Copyright (c) ProperSoft 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author liyh 20121114
 * @version 1.0
 */
public class SPCDispenseAssignWithLiquidControl  extends TControl {

	//周转箱 亮灯次数
	private static final int LIGHT_NUM = 3;
	//配液审核标示
	private static final String IS_CHECK_STR = "是";	
	private static final String SELECT_FLG_Y = "Y";

    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;
    
    // 细项表格选中行
    private int row_d;
    
	
	/**开始时间*/
	TTextFormat start_date;
	
	/**结束时间*/
	TTextFormat end_date;
	
	/**病区*/
//	TTextFormat station_id;
	
	/**统药单table*/
	TTable table_order;
	
	/**统药单号控件*/
	TTextField order_id;
	
	/**统药单号 查询控件*/
	TTextField order_id_query;

    public SPCDispenseAssignWithLiquidControl() {
        super();
    }

    /**初始化方法*/
    public void onInit() {
        initPage();
    }
    
    /**初始画面数据*/
    private void initPage() {
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        
		start_date = (TTextFormat) getComponent("START_DATE");
		end_date = (TTextFormat) getComponent("END_DATE");
		table_order = this.getTable("TABLE_ORDER");
		order_id = (TTextField) getComponent("INTGMED_NO");
		order_id_query = (TTextField) getComponent("INTGMED_NO_QUERY");
		
		// 初始化查询区间
		Timestamp date = SystemTool.getInstance().getDate();        
        this.setValue("START_DATE",date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("END_DATE",date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        onQuery();
    }

    /**查询方法*/
    public void onQuery() {
		TParm parm = new TParm();
		
		parm.setData("START_DATE", start_date.getValue());
		parm.setData("END_DATE", end_date.getValue());
		parm.setData("STATION_ID",this.getValueString("STATION_ID"));
		parm.setData("INTGMED_NO",order_id_query.getValue());
		
		TParm result = SPCDispenseOutAssginNormalTool.getInstance().query(parm);
		table_order.setParmValue(result);
    	
    }
    
	/**table_order 单击事件*/
	public void tableOrderClicked(){
       int row_m = table_order.getSelectedRow();
        if (row_m != -1) {
        	order_id.setValue((String)table_order.getItemData(row_m,"INTGMED_NO"));
    		onIntgmEdNo();    		
        }
	}
	
	/**统药单的回车事件*/
	public void onIntgmEdNo() {
    	String intgmedNo = (String) getValue("INTGMED_NO");
		if (StringUtils.isNotBlank(intgmedNo)) {
			//查询病患配药情况
			System.out.println("SPCDispenseAssignWithLiquidControl----------------131---sql"+SPCSQL.getOrderCodeInfoLiquidByPation(intgmedNo));
			TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrderCodeInfoLiquidByPation(intgmedNo)));
			table_m.setParmValue(parm);
			if (null != parm && parm.getCount() > 0) {
				String stationDesc = (String) parm.getData("STATION_DESC", 0);
				setValue("STATION_DESC", stationDesc);
				this.getTextField("INTGMED_NO").setEditable(false);
				//瓶签获得焦点
			    //this.getTextField("BOTTLE_CODE").grabFocus();
				//周转箱获得
				this.getTextField("BIG_BOX_CODE").grabFocus();
			    table_m.setSelectedRow(0);
			    onTableMClicked();
			}			
		}
	}
	

    /**清空周转箱*/
    public void onClearTurnEslId(){
    	setValue("BIG_BOX_CODE", "");
    	this.getTextField("BIG_BOX_CODE").grabFocus();
    }
    
    /**保存方法*/
    public void onSave() {

    }
    
    /**全选复选框选中事件*/
    public void onCheckSelectAll() {
        table_m.acceptText();
        if (table_m.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_m.getRowCount(); i++) {
            	table_m.setItem(i, "SELECT_FLG", true);
            }
        }
        else {
            for (int i = 0; i < table_m.getRowCount(); i++) {
            	table_m.setItem(i, "SELECT_FLG", false);
            }
        }
    }
    
    /**核对瓶签*/
    public void onBottleCode(){
    	table_m.acceptText();
    	String intgmedNo = getValueString("INTGMED_NO");
    	TParm parmM = table_m.getParmValue();
    	if(null == parmM && parmM.getCount()<1){
    		this.messageBox("配液主档不能为空");
    		this.getTextField("INTGMED_NO").setEditable(true);
    		this.getTextField("INTGMED_NO").grabFocus();
    		return ;
    	}
    	if(row_d < 0){
    		this.messageBox("先选择配液");
    		return ;
    	}
    	String caseNoM = parmM.getValue("CASE_NO", row_d);
    	String bottleCode = getValueString("BOTTLE_CODE").toUpperCase();
    	TTextField BOTTLE_CODE = (TTextField) getComponent("BOTTLE_CODE");
    	BOTTLE_CODE.select(0,bottleCode.length());
    	
    	//通过瓶签查询配液明细
    	System.out.println("-----通过瓶签查询配液明细----------sql:"+SPCSQL.getOrderCodeAssignLiquid(caseNoM, bottleCode));
    	TParm parmDParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrderCodeAssignLiquid(caseNoM, bottleCode)));
    	if(null != parmDParm && parmDParm.getCount() > 0){
    		table_d.setParmValue(parmDParm);
    		row_d += 1;
    		table_m.setSelectedRow(row_d);
    	}
    	setValue("BOTTLE_CODE", "");
    	getTextField("BOTTLE_CODE").grabFocus(); 

    }
    
    /**保存方法*/   
    public void onAssignCode(){
    	onQuery();
    }
    
    /** 主项表格(TABLE_M)单击事件*/
    public void onTableMClicked() {
        row_d = table_m.getSelectedRow();
        if(row_d > -1){
        	getTextField("BOTTLE_CODE").grabFocus();
        	onBottleCode();
/*        	TParm parmM = table_m.getParmValue();
        	String linkNo = parmM.getValue("LINK_NO",row_d);
        	String caseNo = parmM.getValue("CASE_NO",row_d);
        	String intgmedNo = parmM.getValue("INTGMED_NO",row_d);
        	System.out.println("-----------203---sql: "+SPCSQL.getOrderCodeInfoDLiquidByPation(intgmedNo,caseNo,linkNo));
        	TParm parmD = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrderCodeInfoDLiquidByPation(intgmedNo,caseNo,linkNo)));
        	table_d.setParmValue(parmD);*/
        }
    }

    /**绑定周转箱  */
    public void onBigBoxCode(){
    	String bigBoxCode = getValueString("BIG_BOX_CODE");
    	String stattionDesc = getValueString("STATION_DESC");
    	String intgmedNo = getValueString("INTGMED_NO");
    	if(StringUtils.isNotBlank(bigBoxCode)){
    		TParm parmM = table_m.getParmValue();
    		int count = parmM.getCount();
    		for (int i = 0; i < count; i++) {
				String selectFlg = parmM.getValue("SELECT_FLG", i);
				String turnEslId = parmM.getValue("TURN_ESL_ID", i);
				if(SELECT_FLG_Y.equalsIgnoreCase(selectFlg) && StringUtils.isBlank(turnEslId)){
					String caseNo = parmM.getValue("CASE_NO",i);
					String orderNo = parmM.getValue("ORDER_NO",i);
					String orderSeq = parmM.getValue("ORDER_SEQ",i);
					String startDttm = parmM.getValue("START_DTTM",i);
//					table_m.setItem(i, "TURN_ESL_ID", bigBoxCode);
					System.out.println("-------update turnEslId sql : "+SPCSQL.updateTrunElsIdDspnM(intgmedNo, caseNo, orderNo, orderSeq, startDttm, bigBoxCode));
					TParm resultParm = new TParm(TJDODBTool.getInstance().update(SPCSQL.updateTrunElsIdDspnM(intgmedNo, caseNo, orderNo, orderSeq, startDttm, bigBoxCode)));
				}
			}
    		ElectronicTagUtil.getInstance().login();
    		ElectronicTagUtil.getInstance().sendEleTag(bigBoxCode, stattionDesc, "", "", LIGHT_NUM);
    	}
    	
    }
    /**得到Table对象*/
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**得到TextField对象*/
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**得到ComboBox对象*/
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**得到RadioButton对象*/
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**得到CheckBox对象*/
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**取得SYS_FEE信息，放置在状态栏上*/
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "药品代码:" + order.getValue("ORDER_CODE")
            + " 药品名称:" + order.getValue("ORDER_DESC")
            + " 商品名:" + order.getValue("GOODS_DESC")
            + " 规格:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }

    /**清空方法*/
    public void onClear() {
       setValue("INTGMED_NO", "");
       setValue("STATION_DESC", "");
       setValue("BOTTLE_CODE", "");
       setValue("BIG_BOX_CODE", "");
       this.getTextField("INTGMED_NO").setEditable(true);
       table_d.removeRowAll();
       table_m.removeRowAll();
    }

    
    /**得到TextFormat对象*/
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**药库参数信息*/
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }


}
