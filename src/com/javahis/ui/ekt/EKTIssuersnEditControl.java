package com.javahis.ui.ekt;

import jdo.sys.Operator;
//import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
/**
 * <p>
 * Title: 发卡原因
 * </p>
 * 
 * <p>
 * Description: 发卡原因
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author zhangpeng
 *
 */
public class EKTIssuersnEditControl extends TControl{
	String issuersnCode = "";//原因代码
	String name ="";//原因名称
	double factorage = 0.00;//手续费
	String optIp = "";
	String optId = "";
	TCheckBox flg = null;//默认注记
	TTable table = null;
	int row = -1;
	TParm rowParm = null;
	TTextField code = null;
	String flgword = "N";
	public EKTIssuersnEditControl(){
		
	}
	
    /**
	 * 初始化方法
	 */
	public void onInit() {
		optIp = Operator.getIP();
		optId = Operator.getID();
		table = (TTable) getComponent("TABLE");
		flg = (TCheckBox) getComponent("FLG");
		code = (TTextField) getComponent("ISSUERSN_CODE");
		onQuery();
	}
	/**
	 * 保存
	 */
	public void onSave(){
		getThisValue();
		TParm result = null;
		if(row < 0){
			result = save();
		}else{
			result = update();
		}
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		messageBox("操作成功");
		onClear();
		onQuery();
	}
	/**
	 * 保存
	 */
	public TParm save(){
		String sql =
			"INSERT INTO EKT_ISSUERSN VALUES('"+issuersnCode+"','"+name+"',"+factorage+"," +
					"'"+optId+"',SYSDATE,'"+optIp+"','"+flgword+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	/**
	 * 查询
	 */
	public void onQuery(){
		getThisValue();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT ISSUERSN_CODE,ISSUERSN_DESC,FACTORAGE_FEE,OPT_USER,OPT_DATE,OPT_TERM,FLG FROM EKT_ISSUERSN WHERE 1=1 ");
		if(!"".equals(issuersnCode)){
			sql.append(" AND ISSUERSN_CODE = '"+issuersnCode+"' ");
		}
		if(!"".equals(name)){
			sql.append(" AND ISSUERSN_DESC = '"+name+"' ");
		}
		sql.append(" ORDER BY ISSUERSN_CODE");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		table.setParmValue(result);
	}
	/**
	 * table监听器
	 */
	public void onClickTable(){
		code.setEnabled(false);
		row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		rowParm = tableParm.getRow(row);
		setValue("ISSUERSN_CODE", rowParm.getValue("ISSUERSN_CODE"));
		setValue("NAME", rowParm.getValue("ISSUERSN_DESC"));
		setValue("FACTORAGE_FEE", rowParm.getValue("FACTORAGE_FEE"));
		if(rowParm.getValue("FLG").equals("Y")){
			flg.setSelected(true);
		}else{
			flg.setSelected(false);
		}
	}
	/**
	 * 清空
	 */
	public void onClear(){
		code.setEnabled(true);
		clearValue("ISSUERSN_CODE;NAME;FACTORAGE_FEE");
		flg.setSelected(false);
		row = -1;
		flgword = "N";
		onQuery();
	}
	/**
	 * 删除
	 */
	public void onDelete(){
		if(row < 0){
			messageBox("请先选择");
			return;
		}
		getThisValue();
		String sql = 
			"DELETE EKT_ISSUERSN WHERE ISSUERSN_CODE = '"+issuersnCode+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		messageBox("操作成功");
		onClear();
		onQuery();
	}
	/**
	 * 修改
	 */
	public TParm update(){
		String sql =
			"UPDATE EKT_ISSUERSN SET ISSUERSN_DESC = '"+name+
			"',FACTORAGE_FEE = "+factorage+
			",FLG = '"+flgword+"',OPT_USER = '"+optId+
			"',OPT_TERM = '"+optIp+"',OPT_DATE = SYSDATE WHERE ISSUERSN_CODE = '"+issuersnCode+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	/**
	 * 取得值
	 */
	public void getThisValue(){
		issuersnCode = getValueString("ISSUERSN_CODE");
		name = getValueString("NAME");
		factorage = StringTool.getDouble(getValueString("FACTORAGE_FEE"));
		if(flg.isSelected()){
			flgword = "Y";
		}else{
			flgword = "N";
		}
	}
	
}
