package com.javahis.ui.ope;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import jdo.bms.BMSDeptReceiveTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.system.textFormat.TextFormatStation;

/**
 * <p>
 * Title: 血袋接收
 * </p>
 *
 * <p>
 * Description: 血袋接收
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author wangjc 2017.09.20
 * @version 1.0
 */


public class OPEBloodbagCheckControl extends TControl {
	
	//院内条码文本框
	private TTextField BLOOD_NO;
	
	//用血量
	private TTextField FACT_VOL;
	
	//表格
	private TTable TABLE;
	
	//接收人下拉列表
	private TTextFormat RECEIVER;
	
	//未确认单选按钮
	private TRadioButton UNCONFIRM;
	
	//已确认单选按钮
	private TRadioButton CONFIRM;
	
	//确认开始日期
	private TTextFormat CONFIRM_START_DATE;
	
	//确认结束日期
	private TTextFormat CONFIRM_END_DATE;
	
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();

	/**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
    }
    
    /**
     * 初始画面数据
     */
    public void initPage(){
    	//初始化区域
    	setValue("REGION_CODE", Operator.getRegion());
    	TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		
		//设置默认选择未确认单选框
		this.UNCONFIRM = (TRadioButton) this.getComponent("UNCONFIRM");
		this.CONFIRM = (TRadioButton) this.getComponent("CONFIRM");
		this.UNCONFIRM.setSelected(true);
		
		//初始化默认接收人员不可用
		
		
		//初始化文本框
		this.BLOOD_NO = (TTextField) this.getComponent("BLOOD_NO");
		this.FACT_VOL = (TTextField) this.getComponent("FACT_VOL");
		
		//初始化表格
		this.TABLE = (TTable) this.getComponent("TABLE");
		this.TABLE.removeRowAll();
		
		//初始化确认开始日期
		this.CONFIRM_START_DATE = (TTextFormat) this.getComponent("CONFIRM_START_DATE");
		this.CONFIRM_START_DATE.setEnabled(false);
		
		//初始化确认结束日期
		this.CONFIRM_END_DATE = (TTextFormat) this.getComponent("CONFIRM_END_DATE");
		this.CONFIRM_END_DATE.setEnabled(false);
		
		//初始化接收人
		this.RECEIVER = (TTextFormat) this.getComponent("RECEIVER");
		
		//表头排序
		addListener((TTable)this.getComponent("TABLE"));
		
		TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    }
    
    /**
     * 选中未确认单选框事件
     */
    public void onUnConfirm(){
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    	this.CONFIRM_START_DATE.setEnabled(false);
    	this.CONFIRM_START_DATE.setValue("");
    	this.CONFIRM_END_DATE.setEnabled(false);
    	this.CONFIRM_END_DATE.setValue("");
    	this.RECEIVER.setEnabled(false);
    	//OUT_NO.setEnabled(true);
    	//BLOOD_NO.setEnabled(true);
    	
    	this.TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    }
    
    /**
     * 选中已确认单选框事件
     */
    public void onConfirm(){
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	Timestamp date = StringTool.getTimestamp(new Date());
    	save.setEnabled(false);
    	//OUT_NO.setValue("");
    	//OUT_NO.setEnabled(false);
    	//BLOOD_NO.setValue("");
    	//BLOOD_NO.setEnabled(false);
    	this.CONFIRM_START_DATE.setEnabled(true);
    	this.CONFIRM_START_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 00:00:00");
    	this.CONFIRM_END_DATE.setEnabled(true);
    	this.CONFIRM_END_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 23:59:59");
    	this.TABLE.removeRowAll();
    	//初始化接收人，为当前操作员
		setValue("RECEIVER", Operator.getID());
		this.RECEIVER.setEnabled(true);
    }
    
    /**
     * 清空
     */
    public void onClear(){
    	onInit();
    	this.BLOOD_NO.setValue("");
    	this.FACT_VOL.setValue("");
    	this.TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    	setValue("CONFIRM_START_DATE", "");
    	setValue("CONFIRM_END_DATE", "");
    	this.BLOOD_NO.setEnabled(true);
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    }
    
    /**
     * 查询
     */
    public void onQuery(){
    	
    	String sql = getQuerySql();
    	TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	
    	if (sqlParm.getErrCode() < 0) {
    		messageBox(sqlParm.getErrText());
	    	 return;      
	    }
	    if (sqlParm.getCount() <= 0) {
	    	messageBox("查无数据");
	        return;
	    } 
	    
	    this.TABLE.removeRowAll();
	    this.TABLE.setParmValue(sqlParm);
    }
    
    
    /**
     * 接收
     */
    public void onSave(){
    	this.TABLE.acceptText();
    	if (this.TABLE.getParmValue().getCount() <= 0) {
	    	this.messageBox("无需要核查的数据");
	        return;
	    }
    	
    	TParm parm = new TParm();
    	parm = this.TABLE.getParmValue();
    	int row = this.TABLE.getSelectedRow();
    	if(row < 0){
    		this.messageBox("请选中要核查的数据");
    		return;
    	}
    	String factVol = this.getValue("FACT_VOL").toString();
    	if(StringUtils.isEmpty(factVol)){
    		this.messageBox("请填写用血量");
    		return;
    	}
    	Pattern pattern = Pattern.compile("[0-9]*"); 
	    Matcher isNum = pattern.matcher(factVol);
	    if(!isNum.matches()){
	    	this.messageBox("用血量应为数字");
    		return;
	    }
    	//进行身份确认
	    String outType = "bloodbagCheck";
    	TParm checkUser1Parm = checkPW(outType);
    	if("FALSE".equals(checkUser1Parm.getValue("RESULT"))){
    		return;
    	}
    	if(!"OK".equals(checkUser1Parm.getValue("RESULT"))){
    		this.messageBox(checkUser1Parm.getValue("USER_ID")+"密码输入错误！");
    		return;
    	}
    	
    	String userCode1 = checkUser1Parm.getValue("USER_ID");
    	String bldtransEndTime = checkUser1Parm.getValue("DATE");
    	outType = "singleExe";
    	TParm checkUser2Parm = checkPW(outType);
    	if("FALSE".equals(checkUser2Parm.getValue("RESULT"))){
    		return;
    	}
    	if(!"OK".equals(checkUser2Parm.getValue("RESULT")) ){
    		this.messageBox(checkUser2Parm.getValue("USER_ID")+"密码输入错误！");
    		return;
    	}
    	String userCode2 = checkUser2Parm.getValue("USER_ID");
    	TParm saveParm = parm.getRow(row);
    	saveParm.setData("BLDTRANS_END_USER", userCode1);
    	saveParm.setData("BLDTRANS_END_TIME", bldtransEndTime.substring(0, 19).replace("-", "/"));
    	saveParm.setData("CHECK_USER", userCode2);
    	saveParm.setData("FACT_VOL", this.getValue("FACT_VOL"));
    	TParm result = TIOM_AppServer.executeAction("action.ope.OPEBloodbagAction", "updateRecheck", saveParm);
    	if(result.getErrCode() < 0){
			this.messageBox("核查失败！");
			return ;
		}
    	this.messageBox("核查成功！");
    	onClear();
    }
    
    /**
	 * 调用密码验证
	 * 
	 * @return boolean
	 */
	public TParm checkPW(String outType) {
//		String singleExe = "singleExe";
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\ope\\passWordCheck.x", outType);
		return parm;
	}
	
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				 //System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		((TTable)this.getComponent("TABLE")).setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
    
	/**
	 * 判断标本是否已接收，true为未接受，false为已接收
	 * @param parm
	 * @return
	 */
    public boolean checkReceive(TParm parm){
    	String sql = getReceiverSql(parm);
    	TParm sqlParm =  new TParm(TJDODBTool.getInstance().select(sql)) ;
    	return (sqlParm.getCount()<=0 || "".equals(sqlParm.getData("RECEIVED_USER", 0)));
    }
    
    /**
     * 查询SQL
     * @return
     */
    public String getQuerySql(){
		//接收人
		String receiver = this.getValueString("RECEIVER");
		//核查开始日期
		String confirm_start_time = this.getValueString("CONFIRM_START_DATE");
		//核查结束日期
		String confirm_end_time = this.getValueString("CONFIRM_END_DATE");
    	String bloodNo = this.getValueString("BLOOD_NO");
    	String sql = "";
    	sql +=" SELECT "
    		+ " A.ORG_BARCODE, "
    		+ " A.BLOOD_NO, "
    		+ " A.OUT_NO, "
    		+ " A.BLD_CODE, "
    		+ " A.BLD_TYPE, "
    		+ " A.RH_FLG, "
    		+ " A.SUBCAT_CODE, " 
    		+ " A.MR_NO, "
    		+ " C.PAT_NAME AS NAME, "
    		+ " C.SEX_CODE AS SEX, "
    		+ " B.BED_NO, "
    		+ " FLOOR (MONTHS_BETWEEN (SYSDATE, C.BIRTH_DATE) / 12)||'岁' AS AGE, "
    		+ " A.OUT_USER, "
    		+ " A.OUT_DATE, " 
    		+ " A.FACT_VOL, " 
    		+ " A.RECHECK_USER, "
    		+ " A.CHECK_USER, "
    		+ " A.CHECK_DATE, "
    		+ " A.BLDTRANS_END_USER, "
    		+ " A.BLDTRANS_END_TIME, "
    		+ " A.RECHECK_TIME "
    		+ " FROM "
    		+ " BMS_BLOOD A , " 
    		+ " ADM_INP B , "
    		+ " SYS_PATINFO C "
    		+ " WHERE "
    		+ " A.CASE_NO = B.CASE_NO " 
    		+ " AND B.MR_NO = C.MR_NO ";
    	if(!"".equals(bloodNo)){
    		sql += " AND A.BLOOD_NO = '"+bloodNo+"' ";
    	}
    
    	if(!"".equals(receiver)){
    		sql += " AND A.BLDTRANS_END_USER = '"+receiver+"'";
    	}
    	
    	if(!"".equals(confirm_start_time)){
    		sql += " AND A.BLDTRANS_END_TIME > TO_DATE('" + confirm_start_time.replace("-", "").replace(" ", "").replace(".0", "") + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	if(!"".equals(confirm_end_time)){
    		sql += " AND A.BLDTRANS_END_TIME < TO_DATE('" + confirm_end_time.replace("-", "").replace(" ", "").replace(".0", "") + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	
    		
    	if(this.UNCONFIRM.isSelected()){
    		sql += " AND A.BLDTRANS_END_USER IS NULL AND RECEIVED_USER IS NOT NULL";
    	}else if(CONFIRM.isSelected()){
    		sql += " AND A.BLDTRANS_END_USER IS NOT NULL ";
    	}
    	
    	return sql;
    }

   
    
    //检验是否已接收SQL
    public String getReceiverSql(TParm parm){
    	String bloodNo = parm.getData("BLOOD_NO")+"";
    	String sql = "SELECT RECEIVED_USER FROM BMS_BLOOD WHERE BLOOD_NO = '"+bloodNo+"'";
    	return sql;
    }
    
}

                                                                                                                                                                                                                                                                      