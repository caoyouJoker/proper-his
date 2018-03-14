package com.javahis.ui.sys;

import jdo.sys.SYSRuleTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.RunClass;
import com.dongyang.util.TypeTool;
/**
*
* <p>Title: 引用表单控制类</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author ehui 2009.05.11
* @version 1.0
*/
public class SysFeeExaSheetQuote extends TControl {
	TTable table;
	Object main;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		getInitParameter();
		initTable();
	}
	/**
	 * 初始化TABLE
	 */
	public void initTable(){
		TParm t0=SYSRuleTool.getExaRoot();
		String tCode=t0.getValue("CATEGORY_CODE",0);
		TParm t1=SYSRuleTool.getExaMid(tCode);
		table=(TTable)this.getComponent("MENU");
		table.setParmValue(t1);
		int count=t1.getCount();
		String[] names=t1.getNames();
		TParm t2=new TParm();
		for(int i=0;i<count;i++){
			TParm temp=SYSRuleTool.getExaDetail(t1.getValue("CATEGORY_CODE",i));
			int countTemp=temp.getCount();
			for(int k=0;k<countTemp;k++){
				for(int j=0;j<names.length;j++){
					t2.addData(names[j], temp.getValue(names[j],k));
				}
			}

		}
		table.setParmValue(t2);
	}
	/**
	 * 行点击事件
	 */
	public void onClick(){
		int row=table.getSelectedRow();
		TParm tableParm=table.getParmValue();
		String code="'"+tableParm.getValue("CATEGORY_CODE",row)+"%'";
		TParm sysFee=this.queryExa(code);
		int count=sysFee.getCount();
		TParm parm1=new TParm();
		TParm parm2=new TParm();
		String[] names=sysFee.getNames();
		for(int i=0;i<count;i+=2){
			if(i>=count)
				break;
			for(int j=0;j<names.length;j++){
				parm1.addData(names[j], sysFee.getValue(names[j],i));
			}
		}
		for(int i=1;i<count;i+=2){
			if(i>=count)
				break;
			for(int j=0;j<names.length;j++){
				parm2.addData(names[j], sysFee.getValue(names[j],i));
			}
		}
		//System.out.println("parm1========"+parm1);
		//System.out.println("parm2========"+parm2);
		TTable table1=(TTable)this.getComponent("TABLE1");
		TTable table2=(TTable)this.getComponent("TABLE2");
		table1.setParmValue(parm1);
		table2.setParmValue(parm2);
	}
	/**
	 *
	 * @param code String
	 * @return 查询sys_fee
	 */
	public TParm queryExa(String code){
		String sql=
			"SELECT 'N' AS EXEC ,ORDER_CODE,ORDER_DESC,PY1,PY2," +
				"	SEQ,DESCRIPTION,TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE," +
				"	ALIAS_DESC,ALIAS_PYCODE,SPECIFICATION,NHI_FEE_DESC,HABITAT_TYPE," +
				"	MAN_CODE,HYGIENE_TRADE_CODE,ORDER_CAT1_CODE,CHARGE_HOSP_CODE,OWN_PRICE,NHI_PRICE," +
				"	GOV_PRICE,UNIT_CODE,LET_KEYIN_FLG,DISCOUNT_FLG,EXPENSIVE_FLG," +
				"	OPD_FIT_FLG,EMG_FIT_FLG,IPD_FIT_FLG,HRM_FIT_FLG,DR_ORDER_FLG," +
				"	INTV_ORDER_FLG,LCS_CLASS_CODE,TRANS_OUT_FLG,TRANS_HOSP_CODE,USEDEPT_CODE," +
				"	EXEC_ORDER_FLG,EXEC_DEPT_CODE,INSPAY_TYPE,ADDPAY_RATE,ADDPAY_AMT," +
				"	NHI_CODE_O,NHI_CODE_E,NHI_CODE_I,CTRL_FLG,CLPGROUP_CODE," +
				"	ORDERSET_FLG,INDV_FLG,SUB_SYSTEM_CODE,RPTTYPE_CODE,DEV_CODE," +
				"	OPTITEM_CODE,MR_CODE,DEGREE_CODE,CIS_FLG,OPT_USER," +
				"	OPT_DATE,OPT_TERM,CAT1_TYPE " +
			" FROM SYS_FEE " +
			" WHERE ORDER_CODE LIKE " +code;
		//System.out.println("SQL=============="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println("RESULT==============="+result);
		return result;
	}
	/**
	 * 回传事件
	 */
	public void onFetchBack(String tableName){
		TTable table=(TTable)this.getComponent(tableName);
		int row=table.getSelectedRow();
		int column=table.getSelectedColumn();
		if("ORDER_DESC".equalsIgnoreCase(table.getParmMap(column))){
			return;
		}
		table.setValueAt("Y", row, column);
		TParm parm=table.getParmValue().getRow(row);
		String code=TypeTool.getString(parm.getValue("ORDER_CODE"));
		boolean result=TypeTool.getBoolean(RunClass.runMethod(main, "onQuoteSheet", new Object[]{parm}));
		if(result){
			table.setValueAt("N", row, column);
		}
	}
	/**
	 * 初始化参数
	 */
	public void getInitParameter(){
		main=this.getParameter();
	}
        /**
         * 关闭
         */
        public boolean onClosing(){
            this.setReturnValue("Y");
            return true;
        }
}
