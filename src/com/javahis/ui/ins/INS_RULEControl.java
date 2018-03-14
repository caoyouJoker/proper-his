package com.javahis.ui.ins;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.ins.INSRuleTXTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:三目字典
 * </p>
 * 
 * <p>
 * Description: 医保三目字典对应：药品
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 2.0 (c) 2011
 * </p>
 * 
 */

public class INS_RULEControl extends TControl {
	/**
	 * 医嘱TABLE
	 */
	private String type; //三目字典类型:ORDERA 药品,ORDERC 诊疗
	private TTable tableFee;// SYS_FEE数据
	private TParm regionParm;// 医保区域代码
	private TTable tableRule;// 医保数据
	private TTable tableHistory;// 历史记录
	private TTable tableShare;// 共用代码
	// 获得一条空行数据 条件匹配查询数据操作使用 在SYS_FEE表格中添加一条空行数据
	private TParm tempSysFeeParm;
	private String kssj;//开始时间
	/**
	 * 医嘱查询TABLE值改变监听
	 */
	private static String TABLE_FEE = "TABLE_FEE";
	
	// 排序
	private Compare compare = new Compare();
	private Compare compareOne = new Compare();
	private int sortColumnOne = -1;
	private boolean ascendingOne = false;
	private int sortColumn = -1;
	private boolean ascending = false;

	public void onInit() { // 初始化程序
		super.onInit();
		type = (String) this.getParameter();//三目字典类型:ORDERA 药品,ORDERC 诊疗
		//初始化界面title
		initParm();		
		tableFee = (TTable) this.getComponent("TABLE_FEE");//SYS_FEE收费数据
		tableRule = (TTable) this.getComponent("TABLE_RULE");//医保数据
		tableHistory = (TTable) this.getComponent("TABLE_HISTORY");//历史记录数据
		tableShare = (TTable) this.getComponent("TABLE_SHARE");//共用代码数据
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());//获得医保区域代码
		//获得添加空行数据
		getTempSysParm();
		callFunction("UI|save|setEnabled", false);//保存按钮置灰不可使用
		callFunction("UI|query|setEnabled", false);//查询按钮置灰不可使用
		callFunction("UI|removeUpdate|setEnabled", false);//移除修改医嘱按钮置灰不可使用
		callFunction("UI|SAVE_BUTTON|setEnabled",false );//保存选中不可使用
		this.setValue("ADM_DATE", SystemTool.getInstance().getDate());//查询时间（界面显示）
		//获得共用代码数据
		getsharedata();
		//获得SYS_FEE数据
		GetOrderInf("","");
		//单击获得历史数据
		callFunction("UI|TABLE_FEE|addEventListener", "TABLE_FEE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		//双击从TABLE_RULE中获得三目编码和医保名称
		callFunction("UI|TABLE_RULE|addEventListener", "TABLE_RULE->"
				+ TTableEvent.DOUBLE_CLICKED, this, "ondoubleTableClicked");
		//双击从TABLE_SHARE中获得三目编码
		callFunction("UI|TABLE_SHARE|addEventListener", "TABLE_SHARE->"
				+ TTableEvent.DOUBLE_CLICKED, this, "ondoubleshareTableClicked");
		addListener(tableFee);//SYS_FEE数据排序
		addListenerOne(tableRule);//SYS_RULE数据排序
	}
   //初始化界面title
	private void initParm() {
		TParm parm = new TParm();
		if ("ORDERA".equalsIgnoreCase(type)) {
			this.setTitle("三目字典药品");
		} else if ("ORDERC".equalsIgnoreCase(type)) {
			this.setTitle("三目字典诊疗");
		}
	}
	/**
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * 获得添加空行数据
	 */
	private void getTempSysParm() {
		String[] tempSysFee = {"ORDER_CODE", "ORDER_DESC",
				"NHI_CODE_I", "NHI_CODE_O", "NHI_CODE_E", "NHI_FEE_DESC",
				"NHI_PRICE", "OWN_PRICE", "DOSE_CHN_DESC","SPECIFICATION", "HYGIENE_TRADE_CODE","MAN_CODE","ADDPAY_RATE" };
		tempSysFeeParm = new TParm();
		tempSysFeeParm.setCount(tempSysFee.length);
	}
	/**
	 * 移除表格数据
	 */
	private void removeTable() {
		tableFee.setParmValue(new TParm());
		tableRule.setParmValue(new TParm());
		tableHistory.setParmValue(new TParm());
		tableFee.removeRowAll();
		tableRule.removeRowAll();
		tableHistory.removeRowAll();
	}

	/**
	 * 保存操作
	 */
	public void onSave() {
		String type = "";//用于判断历史数据如何操作
		int Row = tableFee.getSelectedRow();//行数
		//若没有数据返回
		if (Row < 0) 
		    return;
		//医保码检核
		if(checkNhiCode(Row))
		    return;
		TParm parm = tableFee.getParmValue().getRow(Row);//获得SYS_FEE数据
		String startdate = getTableHistorystartdate(parm.getValue("ORDER_CODE"));
		String updatestartdate = startdate;//开始日期
		startdate = startdate.substring(0,8);//获得历史有效数据的开始时间
		//获得医保开始时间（双击TABLE_RULE中获得将TABLE_RULE中的开始时间赋值到设定修改日期栏）
		String kssj = parm.getValue("DATE").substring(0, 10);
		String date  =kssj+" 00:00:00";//转换类型成YYYY-MM-DD 00:00:00
		Timestamp yesterday = StringTool.rollDate(
				StringTool.getTimestamp(date, "yyyy-MM-dd HH:mm:ss"), -1);
		kssj =kssj.substring(0, 10).replace("-", "");
		String kssjyesterday = StringTool.getString(yesterday, "yyyyMMdd");//医保开始时间前一天
		//若历史有效数据的开始时间>=医保时间执行更新操作反之执行插入操作
    	if(Double.parseDouble(startdate)>=Double.parseDouble(kssj))
    		type ="update";	
    	else
    		type ="insert";	
    	parm.setData("OPT_USER", Operator.getID());//操作人员
    	parm.setData("OPT_TERM", Operator.getIP());//操作地址
    	parm.setData("INSDATE", kssj);//医保开始时间
    	parm.setData("YESTERDAYDATE", kssjyesterday);//医保开始时间前一天
    	parm.setData("TYPE", type);//判断历史数据如何操作
    	parm.setData("UPDATESTARTDATE", updatestartdate);//开始日期    	
    	parm.setData("ADDPAY_RATE", parm.getValue("ADDPAY_RATE"));//增付比率
//    	System.out.println("parm===1"+parm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSRuleTXAction", "Save", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");//保存失败
		} else {
			this.messageBox("P0001");//保存成功
		}
		String smrj = this.getValueString("XMRJ");//获得界面拼音查询条件	
		String datejm = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "ADM_DATE")), "yyyyMMdd"); //拿到界面的查询时间	
		//获得sys_fee数据
		GetOrderInf(smrj,datejm);
		//获得历史数据
		getTableHistory(parm.getValue("ORDER_CODE"));
		
	}
	/**
	 * 医保码检核
	 */
	private boolean checkNhiCode(int row){
		TParm feeParm = tableFee.getParmValue();
	    if(feeParm.getValue("NHI_CODE_I",row).trim().length() == 0 ||
	       feeParm.getValue("NHI_CODE_O",row).trim().length() == 0 ||
	       feeParm.getValue("NHI_CODE_E",row).trim().length() == 0){
	      messageBox("医保码不可为空");
	      return true;
	    }
	    if(feeParm.getValue("NHI_CODE_I",row).trim().length() > 6 ||
	       feeParm.getValue("NHI_CODE_O",row).trim().length() > 6 ||
	       feeParm.getValue("NHI_CODE_E",row).trim().length() > 6){
	      messageBox("医保码长度不能超过六位");
	      return true;	      
	    }
	    return false; 
	}
	/**
	 * 获得历史数据
	 * @param order
	 */
	public void onTableClicked(int row){
		TParm sysFeeParm = tableFee.getParmValue();// SYS_FEE医嘱
		//获得历史数据
		getTableHistory(sysFeeParm.getValue("ORDER_CODE", row));
	}
	/**
	 * 从TABLE_RULE中获得三目编码和医保名称
	 * @param order
	 */
	public void ondoubleTableClicked(int row){
		int rowFee = tableFee.getSelectedRow();// SYS_FEE医嘱数据获得选中行	
		if (rowFee < 0) {
			this.messageBox("请选择一笔需要修改的医嘱");
			return;
		}
		TParm ruleParm = tableRule.getParmValue();// 医保数据
		TParm sysFeeParm = tableFee.getParmValue();// SYS_FEE医嘱
		//重新获得医保码和医保名称
		sysFeeParm.setData("NHI_CODE_I", rowFee, ruleParm.getValue("SFXMBM",
				row));//住院医保码
		sysFeeParm.setData("NHI_CODE_O", rowFee, ruleParm.getValue("SFXMBM",
				row));//门诊医保码
		sysFeeParm.setData("NHI_CODE_E", rowFee, ruleParm.getValue("SFXMBM",
				row));//急诊医保码
		sysFeeParm.setData("NHI_FEE_DESC", rowFee, ruleParm.getValue("XMMC",
				row));//医保名称
		String kssj = ruleParm.getValue("KSSJ",row).substring(0, 10); //医保开始时间
		sysFeeParm.setData("DATE",rowFee,kssj);//将医保开始时间赋值到设定修改日期栏位
		sysFeeParm.setData("ADDPAY_RATE",rowFee,ruleParm.getValue("ZFBL1",row));//将医保开始时间赋值到设定修改日期栏位	
		tableFee.setParmValue(sysFeeParm);// 重新赋值
		tableFee.setSelectedRow(rowFee);// 选中当前行
		tableFee.setLockColumns("0,1,7,8,9,10,11,12,13");//医保码可以修改
		callFunction("UI|SAVE_BUTTON|setEnabled", true);// 保存选中可以使用
	}
	/**
	 * 从TABLE_SHARE中获得三目编码
	 * @param order
	 */
	public void ondoubleshareTableClicked(int row){
		int rowFee = tableFee.getSelectedRow();// SYS_FEE医嘱数据获得选中行	
		if (rowFee < 0) {
			this.messageBox("请选择一笔需要修改的医嘱");
			return;
		}
		TParm ruleParm = tableShare.getParmValue();// 共用数据
		TParm sysFeeParm = tableFee.getParmValue();// SYS_FEE医嘱
		//重新获得医保码
		sysFeeParm.setData("NHI_CODE_I", rowFee, ruleParm.getValue("SFXMBM",
				row));//住院医保码
		sysFeeParm.setData("NHI_CODE_O", rowFee, ruleParm.getValue("SFXMBM",
				row));//门诊医保码
		sysFeeParm.setData("NHI_CODE_E", rowFee, ruleParm.getValue("SFXMBM",
				row));//急诊医保码	
		 String now = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd"); //拿到当前的时间
		 sysFeeParm.setData("DATE",rowFee,now);//将当前的时间赋值到设定修改日期栏位
		tableFee.setParmValue(sysFeeParm);// 重新赋值
		tableFee.setSelectedRow(rowFee);// 选中当前行
		callFunction("UI|SAVE_BUTTON|setEnabled", true);// 保存选中可以使用
		tableFee.setLockColumns("0,1,2,3,4,6,7,8,9,10,11,12,13");//医保码不可修改
	}
	/**
	 * 历史数据查询
	 * 
	 * @param order
	 */
	private void getTableHistory(String order) {
		String sql = " SELECT ORDER_CODE, ORDER_DESC, "
				+ " DESCRIPTION, SPECIFICATION,MAN_CODE, "
				+ " OWN_PRICE, NHI_PRICE,HYGIENE_TRADE_CODE, "
				+ " UNIT_CODE,NHI_FEE_DESC,  "
				+ " IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG, "
				+ " EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, "
				+ " ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, " + " NHI_CODE_I, "
				+ " SYS_GRUG_CLASS, NOADDTION_FLG, "
				+ " SYS_PHA_CLASS,START_DATE,END_DATE "
				+ " FROM SYS_FEE_HISTORY  WHERE  " + " ORDER_CODE = '" + order
				+ "' ORDER BY START_DATE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
		tableHistory.setParmValue(result);
	}
	/**
	 * 查询历史表里有效数据
	 * （99991231125959这一笔）
	 * @param order
	 */
	private String getTableHistorystartdate(String order) {
		//获得当前时间
		String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMddHHmmss");
//		System.out.println("sysdate"+sysdate);
		String enddate = "";
			String sql = " SELECT START_DATE "+ 
			             " FROM SYS_FEE_HISTORY " +
					     " WHERE ORDER_CODE = '" + order+ "' " +
					     " AND '" + sysdate+ "'BETWEEN START_DATE AND END_DATE "+
					     " ORDER BY START_DATE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return null;
		}
		//result.getData("START_DATE")=null,result.getValue("START_DATE")=""
		if (result.getData("START_DATE")==null) {
			this.messageBox("当前日期不在开始日期和结束日期范围之内,无法保存");//没有数据
			return null;
		}
		enddate = result.getValue("START_DATE",0);
		return enddate;
	}
	/**
	 * 移除选中的医嘱 医保数据
	 */
	public void onRemoveUpdate() {
		TParm parm = tableFee.getParmValue();// 已匹配医嘱数据
		if (null == parm) {
			this.messageBox("请选择要执行的数据");
			return;
		}
		boolean flg = false;
		// TParm exeParm=new TParm();//执行的数据
		StringBuffer exeOrderCode = new StringBuffer();// 执行的数据
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("FLG", i)) {// 选中数据
				flg = true;
				exeOrderCode
						.append("'" + parm.getValue("ORDER_CODE", i) + "',");
			}
		}
		if (!flg) {
			this.messageBox("请选择要执行的数据");
			return;
		}
		if (this.messageBox("提示", "是否执行移除操作", 2) != 0) {
			return;
		}
		String exeOrder = exeOrderCode.toString().substring(0,
				exeOrderCode.toString().lastIndexOf(","));// 执行的数据去掉末尾","
		// 执行sql
		String sql = " UPDATE SYS_FEE SET NHI_CODE_O=NULL,NHI_CODE_E=NULL,NHI_CODE_I=NULL,NHI_FEE_DESC=NULL,"
				+ "NHI_PRICE = NULL,INSPAY_TYPE = NULL, OPT_USER='"
				+ Operator.getID()
				+ "',OPT_DATE=SYSDATE"
				+ ", OPT_TERM='"
				+ Operator.getIP()
				+ "' WHERE  ORDER_CODE IN ("
				+ exeOrder
				+ ")";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));// 修改移除操作
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
		} else {
			this.messageBox("P0005");// 执行成功
//			onQuery();
		}
	}

	/**
	 * 清空操作
	 */
	public void onClear() {
		removeTable();
		callFunction("UI|removeUpdate|setEnabled", true);// 撤销操作
		this.setValue("XMRJ", "");//界面中拼音查询条件为空
		callFunction("UI|XMRJ|setEnabled", true);//界面中拼音查询可操作
		this.setValue("ADM_DATE", SystemTool.getInstance().getDate());
		callFunction("UI|SAVE_BUTTON|setEnabled",false );// 保存选中不可使用
		tableFee.setLockColumns("0,1,2,3,4,5,6,7,8,9,10,11,12,13");//医保码不可修改
		
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}

				// 表格中parm值一致
				// 1.取paramw值;
				TParm tableData = tableFee.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.根据点击的列,对vector排序
				// 表格排序的列名;
				String tblColumnName = tableFee.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParamOne(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		tableRule.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);
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
	private Vector getVectorOne(TParm parm, String group, String names, int size) {
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndexOne(String columnName[], String tblColumnName) {
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
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListenerOne(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumnOne) {
					ascendingOne = !ascendingOne;
				} else {
					ascendingOne = true;
					sortColumnOne = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = tableRule.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVectorOne(tableData, "Data", strNames, 0);
				// 3.根据点击的列,对vector排序
				// 表格排序的列名;
				String tblColumnName = tableRule.getParmMap(sortColumnOne);
				// 转成parm中的列
				int col = tranParmColIndexOne(columnName, tblColumnName);
				compareOne.setDes(ascendingOne);
				compareOne.setCol(col);
				java.util.Collections.sort(vct, compareOne);
				// 将排序后的vector转成parm;
				cloneVectoryParamOne(vct, new TParm(), strNames);
			}
		});
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		tableFee.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}

		return index;
	}
	
	/**
	 * 
	 * @param 获得sys_fee数据
	 * @param 按钮
	 * @return
	 */
	public void onQuerySysfee(){
		//判断是否为空	
		if(checkQuery())
		   return;		
		String smrj = this.getValueString("XMRJ");//获得界面拼音查询条件	
		String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "ADM_DATE")), "yyyyMMdd"); //拿到界面的时间	
		//获得sys_fee数据
		GetOrderInf(smrj,date);	
	}
	/**
	 * 
	 * @param 获得ins_rule数据
	 * @param 按钮
	 * @return
	 */
	public void onQueryInsrule(){
		//判断是否为空	
		if(checkQuery())
		   return;		
		String smrj = this.getValueString("XMRJ");//获得界面拼音查询条件		
		String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "ADM_DATE")), "yyyyMMdd"); //拿到界面的时间	
		//获得ins_rule的数据
		GetNhiOrderInf(smrj,date);	
	}
	/**
	 * 
	 * @param 获得当前对应数据
	 * @param 按钮
	 * @return
	 */
	public void onQueryCorresonp(){
		String date = StringTool.getString(TCM_Transform.
				getTimestamp(getValue("ADM_DATE")), "yyyyMMdd"); //拿到界面的时间	
		int row = tableFee.getSelectedRow();// SYS_FEE医嘱数据获得选中行
		TParm ruleParm = tableRule.getParmValue();// 医保数据
		if (row < 0) {
			this.messageBox("请选择一笔对应医嘱");
			tableRule.setParmValue(ruleParm);
			return;
		}
		TParm feeParm = tableFee.getParmValue();//获得YS_FEE医嘱数据
		String nhicodeI =feeParm.getValue("NHI_CODE_I",row);//获得住院医保码
		String nhicodeO =feeParm.getValue("NHI_CODE_O",row);//获得门诊医保码
		String nhicodeE =feeParm.getValue("NHI_CODE_E",row);//获得急诊医保码
//    	System.out.println("nhicodeI==="+nhicodeI);	
		if (nhicodeI.equals("") && nhicodeO.equals("") && nhicodeE.equals("")) {
			this.messageBox("E0116");//没有数据
			return;
		}
		tableRule.setParmValue(new TParm());
		tableRule.removeRowAll();
		//获得当前对应数据
		onPresent(nhicodeI,nhicodeO,nhicodeE,date);
	}
	//获得当前对应数据
	public void onPresent(String nhicodeI,String nhicodeO,
			String nhicodeE,String date){
		String sql =  
			" SELECT SFXMBM, XMMC, BZJG, " +
			"CASE MZYYBZ WHEN '1' THEN 'Y' ELSE 'N' END AS MZYYBZ,"+
	        "CASE ETYYBZ WHEN '1' THEN 'Y'  ELSE 'N' END AS ETYYBZ,"+
	        "CASE YKD242 WHEN '1' THEN 'Y' ELSE 'N' END AS YKD242,"+
	        "JX, GG,PZWH, SCQY, TO_CHAR(KSSJ,'yyyy/mm/dd HH:mm:ss') AS KSSJ, " +
	        "TO_CHAR(JSSJ,'yyyy/mm/dd HH:mm:ss') AS JSSJ,ZFBL1 " +
	        " FROM INS_RULE" +
	        " WHERE SFXMBM IN ('" + nhicodeI + "','" + nhicodeO + "'," +
	        "'" + nhicodeE + "') " +
	        " AND   TO_DATE('" + date +
	        "','YYYYMMDDHH24MISS') BETWEEN KSSJ AND JSSJ" +
	        " ORDER BY SFXMBM";	
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				this.messageBox("E0116");//没有数据
				return;
			}
			//将开始时间和结束时间重新转换
			for (int i = 0; i < result.getCount(); i++) {
				String Sdate = result.getValue("KSSJ", i).substring(0, 10);
				Sdate += " 00:00:00";
				result.setData("KSSJ", i, Sdate);
				String Edate = result.getValue("JSSJ", i).substring(0, 10);
				Edate += " 23:59:59";
				result.setData("JSSJ", i, Edate);
			}
			tableRule.setParmValue(result);					
		}
	//获得共用代码的数据
	public void getsharedata() {
    String now = StringTool.getString(SystemTool.getInstance().getDate(),
                                      "yyyyMMdd"); //拿到当前的时间
    
		String sql =  " SELECT SFXMBM, XMMC, BZJG, " +
        "JX, GG,PZWH, SCQY, TO_CHAR(KSSJ,'yyyy/mm/dd HH:mm:ss') AS KSSJ, " +
        "TO_CHAR(JSSJ,'yyyy/mm/dd HH:mm:ss') AS JSSJ,ZFBL1 " +
        " FROM INS_RULE" +
        " WHERE (XMMC LIKE '%自费%' OR XMMC LIKE '%公费%' OR XMMC LIKE '%累计增负%')" +
        " AND   TO_DATE('" + now +
        "','YYYYMMDDHH24MISS') BETWEEN KSSJ AND JSSJ" +
        " ORDER BY SFXMBM";
	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	
	if (result.getErrCode() < 0) {
		this.messageBox("E0116");//没有数据
		return;
	}
	//将开始时间和结束时间重新转换
	for (int i = 0; i < result.getCount(); i++) {
		String Sdate = result.getValue("KSSJ", i).substring(0, 10);
		Sdate += " 00:00:00";
		result.setData("KSSJ", i, Sdate);
		String Edate = result.getValue("JSSJ", i).substring(0, 10);
		Edate += " 23:59:59";
		result.setData("JSSJ", i, Edate);
	}
	tableShare.setParmValue(result);
	}
	//分别获得sys_fee和ins_rule的数据
	public void onQutryData() {
	//判断是否为空	
	if(checkQuery())
	   return;	
	String smrj = this.getValueString("XMRJ");//获得界面拼音查询条件			
	String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
     "ADM_DATE")), "yyyyMMdd"); //拿到界面的时间	
	//获得sys_fee数据
	GetOrderInf(smrj,date);
	//获得ins_rule的数据
	GetNhiOrderInf(smrj,date);	
	}
	//获得sys_fee数据
	public void GetOrderInf(String smrj,String date) {
		 String now = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd"); //拿到当前的时间
		String sql1="";
		String py1 ="";
		if ("ORDERA".equalsIgnoreCase(type)) {
			sql1=" AND A.ORDER_CAT1_CODE LIKE '%PHA%'";//查询药品条件	
		}
		if ("ORDERC".equalsIgnoreCase(type)) {
			sql1=" AND A.ORDER_CAT1_CODE NOT LIKE '%PHA%'";//查询诊疗条件	
		}
		if(!smrj.equals(""))
		py1 =smrj.toUpperCase();//转换成大写形式	
		String sql = 
		"SELECT A.ORDER_CODE,A.ORDER_DESC,A.NHI_CODE_I,A.NHI_CODE_O,A.NHI_CODE_E," +
		"A.NHI_FEE_DESC,A.NHI_PRICE,A.OWN_PRICE,C.DOSE_CHN_DESC," +
		"A.SPECIFICATION,A.HYGIENE_TRADE_CODE,A.MAN_CODE,A.ADDPAY_RATE " +
	    " FROM SYS_FEE A LEFT JOIN PHA_BASE B " +
	    " ON A.ORDER_CODE = B.ORDER_CODE" +
	    " LEFT JOIN PHA_DOSE C ON B.DOSE_CODE = C.DOSE_CODE " +
	    " WHERE A.PY1 LIKE '%" + py1 + "%'" +
	    " AND A.OWN_PRICE !=0"
		+ sql1;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			result.setData("DATE",i,now);	
			
		}
		tableFee.setParmValue(result);
	}
	//获得ins_rule的数据
	public void GetNhiOrderInf(String smrj,String date) {
		String sql =  
		" SELECT SFXMBM, XMMC, BZJG, " +
		"CASE MZYYBZ WHEN '1' THEN 'Y' ELSE 'N' END AS MZYYBZ,"+
        "CASE ETYYBZ WHEN '1' THEN 'Y'  ELSE 'N' END AS ETYYBZ,"+
        "CASE YKD242 WHEN '1' THEN 'Y' ELSE 'N' END AS YKD242,"+
        "JX, GG,PZWH, SCQY, TO_CHAR(KSSJ,'yyyy/mm/dd HH:mm:ss') AS KSSJ, " +
        "TO_CHAR(JSSJ,'yyyy/mm/dd HH:mm:ss') AS JSSJ,ZFBL1 " +
        " FROM INS_RULE" +
        " WHERE (XMRJ  LIKE  '%" + smrj.toLowerCase() + "%' OR SFXMBM = '" + smrj + "')"+
        " AND   TO_DATE('" + date +
        "','YYYYMMDDHH24MISS') BETWEEN KSSJ AND JSSJ" +
        " ORDER BY SFXMBM";	
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
		//将开始时间和结束时间重新转换
		for (int i = 0; i < result.getCount(); i++) {
			String Sdate = result.getValue("KSSJ", i).substring(0, 10);
			Sdate += " 00:00:00";
			result.setData("KSSJ", i, Sdate);
			String Edate = result.getValue("JSSJ", i).substring(0, 10);
			Edate += " 23:59:59";
			result.setData("JSSJ", i, Edate);
		}
		tableRule.setParmValue(result);	
	}
	//判断是否为空
	private boolean checkQuery(){
	   if(this.getValueString("XMRJ").length() == 0){
		messageBox("拼音查询不能为空");
	    return true;
    }
	   if(StringTool.getString(TCM_Transform.getTimestamp(getValue(
		"ADM_DATE")), "yyyyMMdd").length() == 0){
		messageBox("查询时间不能为空");
		return true;
	}
	return false;
 }
	
}
