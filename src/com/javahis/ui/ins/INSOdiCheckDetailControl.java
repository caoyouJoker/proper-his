package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
/**
 * <p>Title: 住院医保对明细帐不平信息 </p>
 *
 * <p>Description:  住院医保对明细帐不平信息</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20120211
 * @version 1.0
 */
public class INSOdiCheckDetailControl extends TControl{
	
	TParm acceptData = new TParm(); //接参
	
	/**
	 * 初始化
	 */
	public void onInit() {
		//接参
    	Object obj = this.getParameter();
        if (obj instanceof TParm) {
            acceptData = (TParm) obj;
//            中心与本地数据不符,120;本地有记录中心端没有,140;中心端有记录本地没有,140;
//            就医顺序号,130;资格确认书编号,120;期号,80;姓名,90;本地发生金额,160,double,#########0.00;
//            中心端发生金额,160,double,#########0.00;本地申报金额,160,double,#########0.00;
//            中心端申报金额,160,double,#########0.00;本地全自费金额,160,double,#########0.00;
//            中心端全自费金额,160,double,#########0.00;本地增负金额,160,double,#########0.00;
//            中心端增负金额,160,double,#########0.00
//            STATUS_ONE;STATUS_TWO;STATUS_THREE;ADM_SEQ;CONFIRN_NO;YEAR_MON;NAME;TOT_AMT_LOCAL;TOT_AMT_CENTER;NHI_AMT_LOCAL;NHI_AMT_CENTER;OWN_AMT_LOCAL;OWN_AMT_CENTER;ADD_AMT_LOCAL;ADD_AMT_CENTER
            this.callFunction("UI|TABLE|setParmValue", acceptData);
        }
  }

}
