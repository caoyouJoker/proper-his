package com.javahis.ui.ibs;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TStrike;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: 红黄警戒及身份比例
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author yanmm 2017/07/20
 * @version 1.0
 */
public class IBSDictionaryCordonControl extends TControl {

	
	private static TTable mainTable;
	
	public void onInit() {
		mainTable = (TTable) getComponent("TABLE");
		super.onInit();
		this.initQuery();
		
	}

	/**
	 * 初始化查询
	 */
	public void initQuery() {
		String sql = "SELECT RED_SIGN,YELLOW_SIGN FROM ODI_SYSPARM ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		setValue("RED_SIGN", result.getValue("RED_SIGN", 0));
		setValue("YELLOW_SIGN", result.getValue("YELLOW_SIGN", 0));
		tableQuery();
	}
	
	public void tableQuery() {
		String sqlT = "SELECT B.CTZ_DESC,A.DISCOUNT_RATE,A.OPT_USER,A.OPT_DATE,A.LOCK_CTZ_FLG FROM "
				+ "SYS_CTZ_REBATE A ,SYS_CTZ B WHERE A.CTZ_CODE = B.CTZ_CODE";
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sqlT));
		mainTable.setParmValue(resultParm);
	}

	/**
	 * 回车查询
	 */
	public void onCtz() {
		String sql = "SELECT DISCOUNT_RATE,LOCK_CTZ_FLG FROM SYS_CTZ_REBATE WHERE CTZ_CODE='"
				+ this.getValue("CTZ") + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (!this.getValue("CTZ").equals("")) {
			if (null == result.getValue("DISCOUNT_RATE", 0)
					|| result.getValue("DISCOUNT_RATE", 0).length() <= 0) {
				this.messageBox("当前身份未设置比例");
			}
		}
		if(result.getValue("LOCK_CTZ_FLG", 0).equals("Y")) {
			setValue("LOCK_CTZ_FLG", "Y");
        }else{
        	setValue("LOCK_CTZ_FLG", "N");
        } 
		setValue("CTZ_P", result.getValue("DISCOUNT_RATE", 0));
	}

	 private TCheckBox getCheckBx(String tagNam) {				
			return (TCheckBox) getComponent("LOCK_CTZ_FLG");
		}
	
	/**
	 * 保存事件
	 */
	public void onSave() {
		Timestamp date = SystemTool.getInstance().getDate();
		TParm parm = new TParm();
		parm.setData("RED_SIGN", this.getValue("RED_SIGN"));
		parm.setData("YELLOW_SIGN", this.getValue("YELLOW_SIGN"));
		parm.setData("CTZ_CODE", this.getValue("CTZ"));
		parm.setData("DISCOUNT_RATE", this.getValue("CTZ_P"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE",
				date.toString().substring(0, 19).replace('-', '/'));
		parm.setData("OPT_TERM", Operator.getIP());
		double totAmt3 = 0.00;
		double totAmt2 = 0.00;
		String sysDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 19).replace("-", "/");
		if(this.getCheckBx("LOCK_CTZ_FLG").isSelected()) {
			parm.setData("LOCK_CTZ_FLG", 'Y');
			this.getDBTool().update(
					"UPDATE ADM_INP SET SMS_COUNT='0',STOP_BILL_FLG = 'N',UNLOCKED_FLG = '2' WHERE CTZ1_CODE = '"
				+this.getValue("CTZ")+"' OR CTZ2_CODE = '"
				+this.getValue("CTZ")+"'");
           }else{
        	   
        	   String sql1=" SELECT CASE_NO FROM ADM_INP WHERE CTZ1_CODE = '"+this.getValue("CTZ")+"'"
        	   		+ " OR CTZ2_CODE = '"+this.getValue("CTZ")+"' ";
        	   TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
        	   for(int i=0; i<result1.getCount();i++){
        		// 身份比例
        			String sqlD = "SELECT A.CASE_NO,B.LOCK_CTZ_FLG AS LOCK_CTZO,C.LOCK_CTZ_FLG AS LOCK_CTZT,CASE WHEN B.DISCOUNT_RATE IS NULL THEN 1 ELSE B.DISCOUNT_RATE END "
        					+ "DISCOUNT_RATE "
        					+ "FROM ADM_INP A, SYS_CTZ_REBATE B, SYS_CTZ_REBATE C   "
        					+ "WHERE A.CTZ1_CODE=B.CTZ_CODE(+)  AND A.CTZ2_CODE = C.CTZ_CODE(+) "
        					+ "AND A.CASE_NO='"
        					+ result1.getValue("CASE_NO",i) + "' ";
        			TParm resultD = new TParm(TJDODBTool.getInstance().select(sqlD));
        			
        			
        			// 取红黄警戒线
        			String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
        			TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));

        			// 消费总金额
        			String sql2 = "SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD "
        					+ "WHERE CASE_NO='" + result1.getValue("CASE_NO",i) + "' AND BILL_DATE <= TO_DATE('"
        					+ sysDate + "','YYYY/MM/DD HH24:MI:SS')";
        			TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
        			if (result2.getCount() > 0
        					&& result2.getValue("TOT_AMT", 0).length() > 0) {
        				totAmt2 = StringTool.round(result2.getDouble("TOT_AMT", 0), 2);
        			}

        			// 预交金
        			String sql3 = " SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT "
        					+ "FROM BIL_PAY WHERE REFUND_FLG = 'N' "
        					+ "AND TRANSACT_TYPE IN ('01', '03', '04') "
        					+ "AND CHARGE_DATE <= TO_DATE ('" + sysDate
        					+ "','YYYY/MM/DD HH24:MI:SS') AND CASE_NO = '" + result1.getValue("CASE_NO",i)
        					+ "' GROUP BY CASE_NO";
        			TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
        			if (result3.getCount() > 0
        					&& result3.getValue("PRE_AMT", 0).length() > 0) {
        				totAmt3 = StringTool.round(result3.getDouble("PRE_AMT", 0), 2);
        			}

        			// 余额
        			double balance = StringTool.round(totAmt3
        					- (totAmt2 * resultD.getDouble("DISCOUNT_RATE", 0)), 2);
        			
        			if (balance > resultSign.getDouble("RED_SIGN", 0)) {
    					String sqlR = "UPDATE ADM_INP SET STOP_BILL_FLG='N',UNLOCKED_FLG='0' WHERE CASE_NO='"
    							+ result1.getValue("CASE_NO",i) + "' ";
    					TJDODBTool.getInstance().update(sqlR);
    				}else{
    					String sqlNR = "UPDATE ADM_INP SET SMS_COUNT='0',STOP_BILL_FLG='Y',UNLOCKED_FLG='0' WHERE CASE_NO='"
    							+ result1.getValue("CASE_NO",i) + "' ";
    					TJDODBTool.getInstance().update(sqlNR);
    				}

        			if (balance >= resultSign.getDouble("YELLOW_SIGN", 0)) {
    					String sqlY = "UPDATE ADM_INP SET SMS_COUNT='0' WHERE CASE_NO='"
    							+ result1.getValue("CASE_NO",i) + "' ";
    					TJDODBTool.getInstance().update(sqlY);
    				}
        	   }
        	   
        	   
        	   
        	parm.setData("LOCK_CTZ_FLG", 'N');
           }
		if(this.getValue("RED_SIGN").equals("")){
			parm.setData("RED_SIGN", "0.00");
		}
		if(this.getValue("YELLOW_SIGN").equals("")){
			parm.setData("YELLOW_SIGN", "0.00");
		}
		TParm NewParm = TIOM_AppServer.executeAction(
				"action.adm.ADMUnlockAction", "updateCordon", parm);
		if(this.getValue("CTZ").equals("")){
			
		}else{
		String sqlC = "SELECT CTZ_CODE,DISCOUNT_RATE FROM SYS_CTZ_REBATE "
				+ "WHERE CTZ_CODE='" + this.getValue("CTZ") + "' ";
		TParm resultC = new TParm(TJDODBTool.getInstance().select(sqlC));
		if(this.getValue("CTZ_P").equals("")){
			parm.setData("DISCOUNT_RATE", "1");
		}
		if (resultC.getValue("CTZ_CODE").equals("")) {
			TIOM_AppServer.executeAction("action.adm.ADMUnlockAction",
					"insertCtz", parm);
		} else {
			TIOM_AppServer.executeAction("action.adm.ADMUnlockAction",
					"updateCtz", parm);
		}
	}
		if (NewParm.getErrCode() < 0) {
			this.messageBox("更新失败");
			return;
		}
		this.messageBox("更新成功");
		onCtz();
		initQuery();
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

}
