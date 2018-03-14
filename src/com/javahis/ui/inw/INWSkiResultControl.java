package com.javahis.ui.inw;

import java.sql.Timestamp;

import jdo.inw.InwForOdiTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: 皮试结果
 * </p>
 * 
 * <p>
 * Description: 皮试结果
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author YanJ 2013-11-07
 * @version 1.0
 */
public class INWSkiResultControl extends TControl{
	private TParm parmmeter;//接受护士执行界面的参数
	private String caseNo = "";//就诊号
	private String orderCode = "";//药嘱代码
	private String phl="";//add caoyong 2014/03/27静点执行
	private String batchNo;
	private String skinflg;
	private String orderNo;
	private String orderSeq;
	private String rxNo;
	/**
	 * 初始化参数
	 */
	public void onInit() {
		this.parmmeter = new TParm();
		Object obj = this.getParameter();
		if (obj.toString().length() != 0 || obj != null) {
			this.parmmeter = (TParm) obj;
			this.setValue("BATCH_NO", parmmeter.getValue("BATCH_NO"));//初始化批号
			this.setValue("SKINTEST_NOTE", parmmeter.getValue("SKINTEST_FLG"));//初始化皮试结果
		}
		//phl=parmmeter.getValue("PHL");//add caoyong 2014/03/27静点执行
		caseNo = parmmeter.getValue("CASE_NO");
		orderCode = parmmeter.getValue("ORDER_CODE");
		orderNo = parmmeter.getValue("ORDER_NO");
		orderSeq = parmmeter.getValue("SEQ_NO");
		rxNo = parmmeter.getValue("RX_NO");
		phl=parmmeter.getValue("PHL");//add caoyong 2014/03/27静点执行
		//皮试结果 add caoyong 2014/4/3 end 
		batchNo=parmmeter.getValue("BATCH_NO");
		skinflg=parmmeter.getValue("SKINTEST_FLG");
		 if(!"".equals(skinflg)||skinflg!=null){
			   if("(-)阴性".equals(skinflg.trim())){
				   skinflg="0";
			   }
			   if("(+)阳性".equals(skinflg.trim())){
				   skinflg="1";
			   }
			   this.setValue("SKINTEST_NOTE", skinflg);
		   }
	}
	/**
	 * 皮试批号修改
	 */
	public void onFecth() {
		TParm result = new TParm();
		String skiNo = this.getValueString("BATCH_NO");// 获取界面的皮试批号
		String skiResult = this.getValueString("SKINTEST_NOTE");//获取界面的皮试结果
		if(skiNo.equals(null)||"".equals(skiNo)){
		   this.messageBox("皮试批号不能为空。");
		   return;
	    }
	    if(skiResult.equals(null)||"".equals(skiResult)){
		   this.messageBox("皮试结果不能为空。");
		   return;
	    }
		if (!checkPW()) {
			return;
		}
		result.addData("SKINTEST_NOTE", skiResult);
		result.addData("BATCH_NO", skiNo);
		this.setReturnValue(result);
		// 同步修改pha_anti表中的皮试批号和皮试结果
		// 查询最近的皮试结果
		  //同步修改pha_anti表中的皮试批号和皮试结果
		   //查询最近的皮试结果
		   if("".equals(phl)||phl==null){//如果不是静点执行界面调用 add caoyong 20140327
			   TParm inParm = new TParm();
			   inParm.setData("ORDER_CODE", orderCode);
			   inParm.setData("SKINTEST_NOTE", skiResult);
			   inParm.setData("BATCH_NO", skiNo);
			   inParm.setData("CASE_NO", caseNo);
			   inParm.setData("ORDER_NO", orderNo);//
			   inParm.setData("ORDER_SEQ", orderSeq);//
			   inParm.setData("OPT_USER", Operator.getID());//
			   inParm.setData("OPT_TERM", Operator.getIP());//
		// 调用action执行事务
	       TParm result1 = TIOM_AppServer.executeAction(
	               "action.inw.InwOrderExecAction", "insertSkinNote", inParm);
	       if(result1.getErrCode()<0){
	    	   this.messageBox(result1.getErrText());
	    	   return;
	       }
		   }else{//静点调用
			   TParm inParm = new TParm();
			   inParm.setData("RX_NO", rxNo);
		       inParm.setData("ORDER_CODE", orderCode);
		       inParm.setData("SKINTEST_FLG", skiResult);
		       inParm.setData("BATCH_NO", skiNo);
		       inParm.setData("CASE_NO", caseNo);
		       inParm.setData("ORDER_NO", orderNo);//
		       inParm.setData("SEQ_NO", orderSeq);//
		       inParm.setData("OPT_USER", Operator.getID());//
		       inParm.setData("OPT_TERM", Operator.getIP());//
	         // 调用action执行事务
	           TParm result1 = TIOM_AppServer.executeAction(
	           "action.inw.InwOrderExecAction", "insertSkinNotePHL", inParm);
	           if(result1.getErrCode()<0){
	        	   this.messageBox(result1.getErrText());
	        	   return;
	           }
	           }
			   this.closeWindow();
	}
	 /**
     * 调用密码验证
     * 
     * @return boolean
     */
    public boolean checkPW() {
        String inwExe = "inwExe";
        String value = (String) this.openDialog(
                "%ROOT%\\config\\inw\\passWordCheck.x", inwExe);
        if (value == null) {
            return false;
        }
        return value.equals("OK");
    }
}
