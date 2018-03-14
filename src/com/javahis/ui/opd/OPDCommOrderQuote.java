package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
/**
 * 
 * <p>
 * Title: 门诊医生工作站常用医嘱调用
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站常用医嘱调用
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20090406
 * @version 1.0
 */
public class OPDCommOrderQuote extends TControl {
	String deptOrDr,code,rxType,fit;
	String SQL="SELECT 'N' as CHOOSE,DEPT_OR_DR,DEPTORDR_CODE,RX_TYPE,SEQ_NO," +
				   "	PRESRT_NO,PRESRT_DESC,LINKMAIN_FLG,LINK_NO,ORDER_CODE," +
				   "	MEDI_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS," +
				   "	GIVEBOX_FLG,DESCRIPTION,SETMAIN_FLG,PACKAGE_TOT,DCTAGENT_CODE," +
				   "	DCTEXCEP_CODE,PRESENT_SEQ,SPCFYDEPT,ORDER_DESC||'  '||SPECIFICATION AS ORDER_DESC " +
				  "	FROM OPD_COMORDER";
	TParm parm;
	public void onInit() {
		out("begin->onInit");
		getInitParameter();
		initEvent();
		onFormLoad();
	}
	public void initEvent() {
		TTable table1=(TTable)this.getComponent("TABLE1");
		TTable table2=(TTable)this.getComponent("TABLE2");
		table1.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBox1");
		table2.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBox2");
	}
	public void onCheckBox1(Object obj){
		TTable table1=(TTable)obj;
		table1.acceptText();
	}
	public void onCheckBox2(Object obj){
		TTable table=(TTable)obj;
		table.acceptText();
	}
	/**
	 * 传入参数，DEPT_DR,DEPTORDR_CODE,RX_TYPE,FIT(形如：OPD_FIT_FLG，EMG_FIT_FLG，IPD_FIT_FLG)
	 */
	public void getInitParameter(){
//		TParm parm=(TParm)this.getParameter();
//		deptOrDr=parm.getValue("DEPT_DR");
//		code=parm.getValue("DEPTORDR_CODE");
//		rxType=parm.getValue("RX_TYPE");
//		fit=parm.getValue("FIT");
		deptOrDr="2";
		code="admin";
		rxType="1";
		fit="OPD_FIT_FLG";
	}
	/**
	 * 初始化界面
	 */
	public void onFormLoad(){
		parm=new TParm(TJDODBTool.getInstance().select(SQL+" WHERE DEPT_OR_DR='" +deptOrDr+
							"' AND DEPTORDR_CODE='" +code+
							"' AND RX_TYPE='" +rxType+"'" +
							"  AND " +fit+	"='Y' ORDER BY SEQ_NO" ));
		int count=parm.getCount();
		int start=count/2;
		TParm parm1=new TParm();
		TParm parm2=new TParm();
		
		String[] names=parm.getNames();
		int clmCount=names.length;
		for(int i=count-1;i>=start;i--){
			for(int j=0;j<clmCount;j++){
				parm2.addData(names[j], parm.getValue(names[j],i));
			}
			
		}
		for(int i=start-1;i>-1;i--){
			for(int j=0;j<clmCount;j++){
				parm1.addData(names[j], parm.getValue(names[j],i));
			}
		}
		TTable table1=(TTable)this.getComponent("TABLE1");
		table1.setParmValue(parm1);
		table1=(TTable)this.getComponent("TABLE2");
		table1.setParmValue(parm2);
	}
	/**
	 * 传回
	 */
	public void onOk(){
		TTable table1=(TTable)this.getComponent("TABLE1");
		TParm parm1=table1.getParmValue();
		TTable table2=(TTable)this.getComponent("TABLE2");
		TParm parm2=table2.getParmValue();
		TParm result=new TParm();
		String[] names=parm.getNames();
		int clmCount=names.length;
		boolean yN;
		int count=parm1.getCount("CHOOSE");
		for(int i=0;i<count;i++){
			yN=TCM_Transform.getBoolean(table1.getValueAt(i, 0));
			if(yN){
				for(int j=0;j<clmCount;j++){
					result.addData(names[j], parm1.getValue(names[j],i));
				}
			}
		}
		count=parm2.getCount("CHOOSE");
		for(int i=0;i<count;i++){
			yN=TCM_Transform.getBoolean(table2.getValueAt(i, 0));
			if(yN){
				for(int j=0;j<clmCount;j++){
					result.addData(names[j], parm2.getValue(names[j],i));
				}
			}
		}
		this.setReturnValue(result);
		this.closeWindow();
	}
}