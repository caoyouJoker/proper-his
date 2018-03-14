package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
/**
 *
 * <p>
 * Title: 门诊医生工作站显示集合医嘱
 * </p>
 *
 * <p>
 * Description:门诊医生工作站显示集合医嘱
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
 * @author ehui 200800901
 * @version 1.0
 */
public class OPDOrderSetShowControl extends TControl {
	TTable table;
	TParm parm;
	public void onInit() {
		super.onInit();
		initParam();
		initForm();
	}
	/**
	 * 初始化界面接受的参数
	 */
	public void initParam(){
		parm=(TParm)this.getParameter();
//		// System.out.println("OPDOrderSetShowControl.init parm"+parm);
	}
	/**
	 * 初始化界面
	 */
	public void initForm(){
		/*
		 *
		 * 医嘱,200;规格,100;数量,40,double;单位,40,UNIT_CODE;单价,50,double;总价,60,double;执行科室,80,TECH_DEPT;检体,80,OPTITEM_CODE;给付类别,80,INSPAY_TYPE
		 * 0,right;1,left;2,right;3,right;4,right;5,left;6,left;7,left
		 * ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
		 * 医嘱,200;规格,100;数量,40,double;单位,40,UNIT_CODE;单价,50,double;总价,60,double;执行科室,80,TECH_DEPT;检体,80,OPT_ITEM;给付类别,80,INS_PAY
		 */
		table=(TTable)this.getComponent("TABLEORDERSET");
		table.setParmValue(parm);
		int count=parm.getCount("ORDER_DESC");
		double arAmt=0.0;
		for(int i=0;i<count;i++){
			arAmt+=parm.getDouble("OWN_AMT",i);
		}
		this.setValue("AR_AMT", arAmt);
	}
}
