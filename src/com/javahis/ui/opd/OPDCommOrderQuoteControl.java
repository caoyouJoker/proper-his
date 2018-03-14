package com.javahis.ui.opd;

import java.util.Vector;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
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
public class OPDCommOrderQuoteControl extends TControl {
	//门急住别
	private String admtype;//yanjing 20130716
	String deptOrDr,code,rxType,fit;
	//20130716 YANJ 添加门急诊适用字段
	String SQL="SELECT B.OPD_FIT_FLG,B.EMG_FIT_FLG,'N' as CHOOSE,DEPT_OR_DR,DEPTORDR_CODE,RX_TYPE,SEQ_NO," +
				   "	PRESRT_NO,PRESRT_DESC,LINKMAIN_FLG,LINK_NO,A.ORDER_CODE," +
				   "	MEDI_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,B.CHARGE_HOSP_CODE," +
				   "	GIVEBOX_FLG,A.DESCRIPTION,SETMAIN_FLG,PACKAGE_TOT,DCTAGENT_CODE," +
				   "	DCTEXCEP_CODE,PRESENT_SEQ,SPCFYDEPT,B.ORDER_DESC||'  '||B.SPECIFICATION AS ORDER_DESC ,A.TRADE_ENG_DESC,A.DESCRIPTION,B.ACTIVE_FLG" +
				  "	FROM OPD_COMORDER A, SYS_FEE B ";
	String CHN_SQL="SELECT 'Y' as CHOOSE,DEPT_OR_DR,DEPTORDR_CODE,RX_TYPE,SEQ_NO," +
	   "	PRESRT_NO,PRESRT_DESC,LINKMAIN_FLG,LINK_NO,a.ORDER_CODE," +
	   "	MEDI_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,b.CHARGE_HOSP_CODE," +
	   "	GIVEBOX_FLG,A.DESCRIPTION,SETMAIN_FLG,PACKAGE_TOT,DCTAGENT_CODE," +
	   "	DCTEXCEP_CODE,PRESENT_SEQ,A.SPCFYDEPT,B.ORDER_DESC||'  '||B.SPECIFICATION AS ORDER_DESC,B.ACTIVE_FLG " +
	  "	FROM OPD_COMORDER A, SYS_FEE B";
	String CHN_RX="SELECT DISTINCT PRESRT_NO,PRESRT_DESC FROM OPD_COMORDER";
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
		this.callFunction("UI|setX", 900);
		this.callFunction("UI|setY", 800);
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
		Object obj=this.getParameter();
		String temp[]=new String[]{};
		TParm inParm=new TParm();
	    TParm admtypeparm=(TParm)this.getParameter();//yanj 20130716 添加门急住别标记
		admtype = admtypeparm.getValue("ADM_TYPE");//yanj 20130716 添加门急住别标记
		if(obj instanceof String){
			String inString=(String)obj;
			temp=StringTool.parseLine(inString, ",");
			deptOrDr=temp[0];
			if("1".equalsIgnoreCase(deptOrDr)){
				code=Operator.getDept();
			}else{
				code=Operator.getID();
			}
			rxType=temp[1];
			fit=temp[2];
		}else {
			inParm=(TParm)obj;
			deptOrDr=inParm.getValue("DEPT_DR");
			if("1".equalsIgnoreCase(deptOrDr)){
				code=Operator.getDept();
			}else{
				code=Operator.getID();
			}
			rxType=inParm.getValue("RX_TYPE");
			fit=inParm.getValue("FIT");
		}
		if(rxType.equalsIgnoreCase("4")){
			rxType="2";
		}
		TLabel label=(TLabel)this.getComponent("LBL_RX");
		TComboBox rxNo=(TComboBox)this.getComponent("RX_NO");
		if(!"3".equalsIgnoreCase(rxType)){

			label.setVisible(false);

			rxNo.setVisible(false);
		}else{
			label.setVisible(true);

			rxNo.setVisible(true);
		}
	}
	/**
	 * 初始化界面
	 */
	public void onFormLoad(){
		String[] rxTypes=rxType.split(",");
		StringBuffer sb=new StringBuffer();
		for(String tempRxType:rxTypes){
			sb.append("'").append(tempRxType).append("',");
		}
		String rx=sb.toString();
		rx=rx.substring(0,rx.lastIndexOf(","));
//		this.messageBox_(rx);
		String sql="";
		if(rx.contains("3")){
			sql=CHN_RX+" WHERE DEPT_OR_DR='" +deptOrDr+
			"' AND DEPTORDR_CODE='" +code+
			"' AND RX_TYPE IN (" +rx+")" +
			"  AND " +fit+	"='Y' ORDER BY PRESRT_NO";
			TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
			Vector rxNoVect=parm.getVector("PRESRT_NO");
			Vector rxNoDescVect=parm.getVector("PRESRT_DESC");
			//// System.out.println("rxNoVect="+rxNoVect);
			TParm rxParm=new TParm();
			if(rxNoVect==null||rxNoVect.size()<1){
				return;
			}
			for(int i=0;i<rxNoVect.size();i++){
				rxParm.addData("ID", ((Vector) rxNoVect.get(i)).get(0));
				rxParm.addData("NAME", ((Vector) rxNoDescVect.get(i)).get(0));
			}
			rxParm.setCount(rxParm.getCount("ID"));
			TComboBox rxNo=(TComboBox)this.getComponent("RX_NO");
			rxNo.setParmValue(rxParm);
			String lastRx=((Vector)rxNoVect.get(0)).get(0)+"";
			rxNo.setSelectedID(lastRx);
			initTable();

		}else{
			sql=SQL+"WHERE  A.ORDER_CODE=B.ORDER_CODE AND  DEPT_OR_DR='" +deptOrDr+
			"' AND DEPTORDR_CODE='" +code+
			"' AND RX_TYPE IN (" +rx+")" +
			"  AND A." +fit+	"='Y' ORDER BY SEQ_NO";
//			// System.out.println("sql=========="+sql);
			parm=new TParm(TJDODBTool.getInstance().select(sql));

			int count=parm.getCount();
			int start=count/2;
			TParm parm1=new TParm();
			TParm parm2=new TParm();
			String[] names=parm.getNames();
			int clmCount=names.length;
			for(int i=0;i<count;i++){
				if(i%2==0){
					for(int j=0;j<clmCount;j++){
						parm1.addData(names[j], parm.getValue(names[j],i));
					}
				}else{
					for(int j=0;j<clmCount;j++){
						parm2.addData(names[j], parm.getValue(names[j],i));
					}
				}
			}
			parm1.setCount(parm1.getCount("ORDER_CODE"));
			parm2.setCount(parm2.getCount("ORDER_CODE"));
			TTable table1=(TTable)this.getComponent("TABLE1");
			table1.setParmValue(parm1);
			//add by huangtt 20150309 start
			for (int i = 0; i < parm1.getCount("CHOOSE"); i++) {
				if("N".equals(parm1.getValue("ACTIVE_FLG", i))){
					table1.setLockCell(i, "CHOOSE", true);
				}
			}
			//add by huangtt 20150309 end
			table1=(TTable)this.getComponent("TABLE2");
			table1.setParmValue(parm2);
			//add by huangtt 20150309 start
			for (int i = 0; i < parm2.getCount("CHOOSE"); i++) {
				if("N".equals(parm2.getValue("ACTIVE_FLG", i))){
					table1.setLockCell(i, "CHOOSE", true);
				}
			}
			//add by huangtt 20150309 end
		}
	}
	/**
	 * 处方签值改变事件
	 */
	public void onRxChange(){
		initTable();
	}
	public void initTable(){
		TComboBox rxNo=(TComboBox)this.getComponent("RX_NO");
		if(rxNo==null){
			return;
		}

		String lastRx=rxNo.getSelectedID();
		if(StringUtil.isNullString(lastRx)){
			return;
		}

		String sql=CHN_SQL+" WHERE a.order_code=b.order_code and DEPT_OR_DR='" +deptOrDr+
		"' AND DEPTORDR_CODE='" +code+
		"' AND RX_TYPE ='3'" +
		"  AND PRESRT_NO='" +lastRx+"'" +
		"  AND a." +fit+	"='Y' ORDER BY PRESRT_NO";
//		// System.out.println("onRxChange.sql==="+sql);
		parm=new TParm(TJDODBTool.getInstance().select(sql));

		int count=parm.getCount();
		int start=count/2;
		TParm parm1=new TParm();
		TParm parm2=new TParm();

		String[] names=parm.getNames();
		int clmCount=names.length;
		for(int i=0;i<count;i++){
			if(i%2==0){
				for(int j=0;j<clmCount;j++){
					parm1.addData(names[j], parm.getValue(names[j],i));
				}
			}else{
				for(int j=0;j<clmCount;j++){
					parm2.addData(names[j], parm.getValue(names[j],i));
				}
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
		int count=parm1.getCount("CHOOSE");//表格中数据的行数
		for(int i=0;i<count;i++){
			yN=TCM_Transform.getBoolean(table1.getValueAt(i, 0));//存储是否回传的标记位
			if(yN){
				//查询该药嘱是否已经停用，yanjing，20131029
				String sql = "SELECT ACTIVE_FLG FROM SYS_FEE WHERE ORDER_CODE = '"+parm1.getValue("ORDER_CODE", i)+"'";
				TParm activeParm = new TParm(TJDODBTool.getInstance().select(sql));
				if(activeParm.getValue("ACTIVE_FLG",0).equals("N")){
					this.messageBox(parm1.getValue("ORDER_DESC", i)+",已停用。");
					return;
				}
				// add by yanj 2013/07/16门急诊适用校验
				// 门诊
				if ("O".equalsIgnoreCase(admtype)) {
					// 判断是否门急诊适用医嘱
					if (!("Y".equals(parm1.getValue("OPD_FIT_FLG",i)))) {
						// 不是门诊适用医嘱！
						this.messageBox(parm1.getValue("ORDER_DESC", i)+",不是门诊适用医嘱。");
						return;
					}
				}
				// 急诊
				if ("E".equalsIgnoreCase(admtype)) {
					if (!("Y".equals(parm1.getValue("EMG_FIT_FLG",i)))) {
						// 不是门诊适用医嘱！
						this.messageBox(parm1.getValue("ORDER_DESC", i)+",不是急诊适用医嘱。");
						return;
					}
				}
				// $$===========add by yanj 2013/07/16门急诊适用校验
				for(int j=0;j<clmCount;j++){
					result.addData(names[j], parm1.getValue(names[j],i));
				}
			}
		}
		count=parm2.getCount("CHOOSE");
		for(int i=0;i<count;i++){
			yN=TCM_Transform.getBoolean(table2.getValueAt(i, 0));
			if(yN){
				//查询该药嘱是否已经停用，yanjing，20131029
				String sql = "SELECT ACTIVE_FLG FROM SYS_FEE WHERE ORDER_CODE = '"+parm2.getValue("ORDER_CODE", i)+"'";
				TParm activeParm = new TParm(TJDODBTool.getInstance().select(sql));
				if(activeParm.getValue("ACTIVE_FLG",0).equals("N")){
					this.messageBox(parm2.getValue("ORDER_DESC", i)+",已停用。");
					return;
				}
				// add by yanj 2013/07/16门急诊适用校验
				// 门诊
				if ("O".equalsIgnoreCase(admtype)) {
					// 判断是否门急诊适用医嘱
					if (!("Y".equals(parm2.getValue("OPD_FIT_FLG",i)))) {
						// 不是门诊适用医嘱！
						this.messageBox(parm2.getValue("ORDER_DESC", i)+",不是门诊适用医嘱。");
						return;
					}
				}
				// 急诊
				if ("E".equalsIgnoreCase(admtype)) {
					if (!("Y".equals(parm2.getValue("EMG_FIT_FLG",i)))) {
						// 不是门诊适用医嘱！
						this.messageBox(parm2.getValue("ORDER_DESC", i)+",不是急诊适用医嘱。");
						return;
					}
				}
				// $$===========add by yanj 2013/07/16门急诊适用校验
				for(int j=0;j<clmCount;j++){
					result.addData(names[j], parm2.getValue(names[j],i));
				}
			}
		}
		this.setReturnValue(result);
		this.closeWindow();
	}
    /**
     * 设置语种
     * @param language String
     */
    public void onChangeLanguage(String language)
    {
    	TTable table1=(TTable)this.getComponent("TABLE1");
    	TParm parm1=table1.getParmValue();
    	if(parm1==null||parm1.getCount()<=0){
    		return;
    	}
		table1.setParmValue(parm1);
		table1=(TTable)this.getComponent("TABLE2");
		TParm parm2=table1.getParmValue();
		table1.setParmValue(parm2);
    }
    //start  machao  20170317  全选
    public void onSelectAll(){
    	TTable table1=(TTable)this.getComponent("TABLE1");
    	TTable table2=(TTable)this.getComponent("TABLE2");
    	table1.acceptText();
    	table2.acceptText();
    	
    	int row1 = table1.getRowCount();
    	int row2 = table2.getRowCount();
    	
    	if(this.getCheckBox("SEL_ALL").isSelected()){
    		for(int i = 0;i < row1;i++){
    			if(!table1.isLockCell(i, 0)){
    				table1.setItem(i, "CHOOSE", true);
    			}	
        	}
    		for(int i = 0;i < row2;i++){
    			if(!table2.isLockCell(i, 0)){
    				table2.setItem(i, "CHOOSE", true);
    			}   			
    		}
    	}else{
    		for(int i = 0;i < row1;i++){
    			table1.setItem(i, "CHOOSE", false);
        	}
    		for(int i = 0;i < row2;i++){
    			table2.setItem(i, "CHOOSE", false);
    		}
    	}
    	
    }
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }
    //end  machao
}
