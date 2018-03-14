package com.javahis.ui.bms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
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
 * Title: 血库标本接收
 * </p>
 *
 * <p>
 * Description: 血库标本接收
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
 * @author yangjj 2015.04.8
 * @version 1.0
 */


public class BMSSamplesReceiveControl extends TControl {
	//条码文本框
	private TTextField BAR_CODE;
	
	//表格
	private TTable TABLE;
	
	//科室下拉列表
	private TTextFormat DEPT;
	
	//病区下拉列表
	private TTextFormat STATION;
	
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
		UNCONFIRM = (TRadioButton) this.getComponent("UNCONFIRM");
		CONFIRM = (TRadioButton) this.getComponent("CONFIRM");
		UNCONFIRM.setSelected(true);
		 
		//初始化默认接收人员不可用
		
		
		//初始化光标文本框
		BAR_CODE = (TTextField) this.getComponent("BAR_CODE");
		
		//初始化表格
		TABLE = (TTable) this.getComponent("TABLE");
		TABLE.removeRowAll();
		
		//初始化科室
		DEPT = (TTextFormat) this.getComponent("DEPT");
		
		//初始化病区
		STATION = (TTextFormat) this.getComponent("STATION");
		
		//初始化确认开始日期
		CONFIRM_START_DATE = (TTextFormat) this.getComponent("CONFIRM_START_DATE");
		CONFIRM_START_DATE.setEnabled(false);
		
		//初始化确认结束日期
		CONFIRM_END_DATE = (TTextFormat) this.getComponent("CONFIRM_END_DATE");
		CONFIRM_END_DATE.setEnabled(false);
		
		//初始化接收人
		RECEIVER = (TTextFormat) this.getComponent("RECEIVER");
		
		//表头排序
		addListener((TTable)this.getComponent("TABLE"));
		
		TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    	
    	BAR_CODE.grabFocus();
    	
    	this.openDialog("%ROOT%\\config\\sys\\SYSOpenAndCloseDialog.x");
    	
    }
    
    //选中未确认单选框事件
    public void onUnConfirm(){
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	save.setEnabled(true);
    	CONFIRM_START_DATE.setEnabled(false);
    	CONFIRM_START_DATE.setValue("");
    	CONFIRM_END_DATE.setEnabled(false);
    	CONFIRM_END_DATE.setValue("");
    	RECEIVER.setEnabled(false);
    	
    	TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    }
    
    //选中已确认单选框事件
    public void onConfirm(){
    	TMenuItem save = (TMenuItem) this.getComponent("save");
    	Timestamp date = StringTool.getTimestamp(new Date());
    	save.setEnabled(false);
    	CONFIRM_START_DATE.setEnabled(true);
    	CONFIRM_START_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 00:00:00");
    	CONFIRM_END_DATE.setEnabled(true);
    	CONFIRM_END_DATE.setValue(date.toString().substring(0, 10).replace('-', '/')+" 23:59:59");
    	TABLE.removeRowAll();
    	//初始化接收人，为当前操作员
		setValue("RECEIVER", Operator.getID());
		RECEIVER.setEnabled(true);
    }
    
    //清空
    public void onClear(){
    	onInit();
    	DEPT.setValue("");
    	STATION.setValue("");
    	BAR_CODE.setValue("");
    	TABLE.removeRowAll();
    	setValue("RECEIVER", "");
    	setValue("CONFIRM_START_DATE", "");
    	setValue("CONFIRM_END_DATE", "");
    	BAR_CODE.grabFocus();
    }
    
    //扫条码查询
    public void onBarCodeEnter(){
    	String barCode = BAR_CODE.getValue();
    	if("".equals(barCode)){
    		this.messageBox("请扫描血管条码");
    		return ;
    	}
    	
    	String sql = getQuerySql();
    	TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	
    	if (sqlParm.getErrCode() < 0) {
    		messageBox(sqlParm.getErrText());
	    	 return;      
	    }
	    if (sqlParm.getCount() <= 0) {
	    	messageBox("查无数据");
	    	onClear();
	        return;
	    } 
	    
	    TABLE.removeRowAll();
	    try {
	    	BAR_CODE.setValue("");
	    	TABLE.setParmValue(sqlParm);
		} catch (Exception e) {
			// TODO: handle exception
			this.messageBox(""+e);
		}
	    
    }
    
    //查询
    public void onQuery(){
    	
    	String sql = getQuerySql();
    	TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
    	
    	if (sqlParm.getErrCode() < 0) {
    		messageBox(sqlParm.getErrText());
	    	 return;      
	    }
	    if (sqlParm.getCount() <= 0) {
	    	messageBox("查无数据");
	    	TABLE.removeRowAll();
	        return;
	    } 
	    
	    TABLE.removeRowAll();
	    TABLE.setParmValue(sqlParm);
    }
    
    
    //接收
    public void onSave(){
    	TABLE.acceptText();
    	if (TABLE.getRowCount() <= 0) {
	    	this.messageBox("无需要接收的标本");
	        return;
	    }
    	
    	TParm parm = new TParm();
    	parm = TABLE.getParmValue();
    	
    	//需要接收的标本
    	List<TParm> saveParm = new ArrayList<TParm>();
    	for(int i = 0 ; i < parm.getCount() ; i++ ){
    		
    		TParm p = parm.getRow(i);
    		
    		//判断是否勾选数据
    		if("Y".equals(p.getData("CHECKED")+"")){
    			//true为该标本未接收，false为该标本已接收
    			boolean s = checkReceive(p);
    			
    			if(!s){
    				this.messageBox("该标本已接收");
    				return ;
    			}
    			saveParm.add(p);
    		}
    	}
    	if(saveParm.size() <= 0){
    		this.messageBox("无需要接收的标本");
	        return;
    	}
    	
    	//进行身份确认
    	if(!"OK".equals(checkPW()) ){
    		BAR_CODE.grabFocus();
    		return;
    	}
    	String userCode = Operator.getID();
    	for(int i = 0 ; i < saveParm.size() ; i++ ){
    		TParm p = saveParm.get(i);
    		String application_no = p.getValue("BAR_CODE");
    		String sqlUpdate = getUpdateSql(userCode,application_no);
    		TParm result = new TParm(TJDODBTool.getInstance().update(sqlUpdate)) ;
    		if(result.getErrCode() < 0){
    			this.messageBox("接收失败！");
    			return ;
    		}
    	}
    	this.messageBox("接收成功！");
    	onClear();
    }
    
    /**
	 * 调用密码验证
	 * 
	 * @return result
	 */
	public String checkPW() {
		String result = (String) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", "Y");
		return result;
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
    
	//判断标本是否已接收，true为未接受，false为已接收
    public boolean checkReceive(TParm parm){
    	String sql = getReceiverSql(parm);
    	TParm sqlParm =  new TParm(TJDODBTool.getInstance().select(sql)) ;
    	return (sqlParm.getCount()<=0 || "".equals(sqlParm.getData("HANDOVER_USER", 0)));
    }
    
    //查询SQL
    public String getQuerySql(){
    	String barCode="";//条码
		String receiver="";//接收人
		String dept_code="";//科室
		String station_code="";//病区
		String confirm_start_time="";//确认开始时间
		String confirm_end_time="";//确认结束时间
		
		
    	receiver = this.getValueString("RECEIVER");
    	dept_code = this.getValueString("DEPT");
    	station_code = this.getValueString("STATION");
    	confirm_start_time = this.getValueString("CONFIRM_START_DATE");
    	confirm_end_time = this.getValueString("CONFIRM_END_DATE");
    	barCode = this.getValueString("BAR_CODE");
    	
    	
    	String sql = "";
    	
    	sql += " SELECT 'Y' AS CHECKED, "//选中
        + " A.BED_NO AS BED_NO, "//床号
        + " A.MR_NO AS MR_NO, "//病案号
        + " M.PAT_NAME AS NAME, "//姓名
        + " S.SEX_CODE AS SEX_CODE, "//性别
        + " FLOOR (MONTHS_BETWEEN (SYSDATE, S.BIRTH_DATE) / 12)||'岁' AS AGE, "//年龄
        + " S.BLOOD_TYPE AS BLOOD_TYPE, "//血型
        + " M.ORDER_DESC AS ORDER_DESC, "//项目名称
        + " M.DEPT_CODE AS DEPT_CODE, "//科室
        + " M.STATION_CODE AS STATION_CODE, "//病区
        + " M.APPLICATION_NO AS BAR_CODE, "//条码号
        + " O.NS_EXEC_CODE_REAL AS EXEC_CODE, "//执行护士
        + " O.NS_EXEC_DATE_REAL AS EXEC_DATE, "//执行日期
        + " M.HANDOVER_USER AS RECEIVE_CODE, "//接收人
        + " M.HANDOVER_TIME AS RECEIVE_DATE "//接收日期
        + " FROM MED_APPLY M, "
        + " ADM_INP A, "
        + " SYS_PATINFO S, "
        + " ODI_ORDER B, "
        + " ODI_DSPND O "
        + " WHERE " 
        + " M.CASE_NO = A.CASE_NO "
        + " AND B.CASE_NO = M.CASE_NO "
        + " AND B.ORDER_SEQ = M.SEQ_NO " 
        + " AND B.ORDER_NO = M.ORDER_NO " 
        + " AND A.MR_NO = S.MR_NO " 
        + " AND B.MED_APPLY_NO = M.APPLICATION_NO "
        + " AND B.CAT1_TYPE=M.CAT1_TYPE "
        + " AND B.CASE_NO = O.CASE_NO " 
        + " AND B.ORDER_SEQ = O.ORDER_SEQ " 
        + " AND B.ORDER_NO = O.ORDER_NO " 
        //开发测试时暂关闭该限制，正式版本应打开
        + " AND M.ORDER_CAT1_CODE='BMS' "
        
        //测试用过滤时间
        //+ " AND M.ORDER_DATE > TO_DATE('20150320000000', 'YYYYMMDDHH24MISS')"
        
        
        + " AND M.CAT1_TYPE='LIS' ";
        
    	//条码号不为空
    	if(!"".equals(barCode)){
    		sql += " AND M.APPLICATION_NO = '"+barCode+"'";
    		//return sql;
    	}
    	
    	//接收人不为空
    	if(!"".equals(receiver)){
    		sql += " AND M.HANDOVER_USER = '"+receiver+"'";
    	}
    	
    	//科室不为空
    	if(!"".equals(dept_code)){
    		sql += " AND M.DEPT_CODE = '"+dept_code+"'";
    	}
    	
    	//病区不为空
    	if(!"".equals(station_code)){
    		sql += " AND M.STATION_CODE = '"+station_code+"'";
    	}
    	
    	//确认开始时间不为空
    	if(!"".equals(confirm_start_time)){
    		
			sql += " AND M.HANDOVER_TIME > TO_DATE('" + confirm_start_time.replace("-", "").replace(" ", "").replace(".0", "")  + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	//确认结束时间不为空
    	if(!"".equals(confirm_end_time)){
    		sql += " AND M.HANDOVER_TIME < TO_DATE('" + confirm_end_time.replace("-", "").replace(" ", "").replace(".0", "") + "', 'YYYYMMDDHH24:MI:SS')";
    	}
    	
    	//true为血库已接收，false为血库未接受，""为状态未知
    	
    	if(CONFIRM.isSelected()){
    		sql += " AND M.HANDOVER_USER IS NOT NULL";
    	}else if(UNCONFIRM.isSelected()){
    		sql += " AND M.HANDOVER_USER IS NULL";
    	}
    	
    	return sql;
    }
    
    //检验是否已接收SQL
    public String getReceiverSql(TParm parm){
    	String application_no = parm.getData("BAR_CODE")+"";
    	String sql = "SELECT HANDOVER_USER FROM MED_APPLY WHERE CAT1_TYPE='LIS' AND APPLICATION_NO = '"+application_no+"'";
    	return sql;
    }
    
    //更新接收人和接收日期SQL
    public String getUpdateSql(String user,String application_no){
    	String sql = "";
    	Timestamp date = TJDODBTool.getInstance().getDBTime();
    	String d = date.toString();
    	d = d.substring(0, d.indexOf(".")).replace("-", "").replace(":", "").trim();
    	sql += " UPDATE MED_APPLY "
    		 + " SET HANDOVER_USER = '"+user+"', "
    		 + " HANDOVER_TIME = TO_DATE('" + d + "', 'YYYYMMDDHH24MISS')"
    		 + " WHERE CAT1_TYPE='LIS' AND APPLICATION_NO = '"+application_no+"'";
    	System.out.println("update:"+sql);
    	return sql;
    }
}

                                                                                                                                                                                                                                                                      