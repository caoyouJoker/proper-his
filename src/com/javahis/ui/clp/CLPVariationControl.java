package com.javahis.ui.clp;

import java.util.ArrayList;
import java.util.List;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.*;

import jdo.clp.CLPDurationTool;
import jdo.clp.CLPVariationTool;
import jdo.sys.Operator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;

import com.dongyang.ui.event.TTableEvent;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.util.Compare;

import java.util.Map;
import com.dongyang.data.TNull;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.PatTool;

/**
 * <p>
 * Title: 临床路径变异分析
 * </p>
 * 
 * <p>
 * Description: 临床路径变异分析
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class CLPVariationControl extends TControl {
	public CLPVariationControl() {

	}

	// checkbox列记录，用户根据配置的列判断表格中是否点击了需要做处理的checkbox
	// 表格中变异check框名称
	private String varianceFlg = "VARIANCEFLG";
	// 表格中是否执行check框名称
	private String isExe = "ISEXE";
	// 执行科室check框名称
	private String rDeptFlg = "RDEPTFLG";
	// 表格中变异类别字段名称
	private String medicalMoncat = "MEDICAL_MONCAT";
	// 表格中变异原因字段名称
	private String medicalVariance = "MEDICAL_VARIANCE";
	// 表格中管理师变异类别名称
	private String manageMoncat = "MANAGE_MONCAT";
	// 表格中管理师变异原因名称
	private String manageVariance = "MANAGE_VARIANCE";
	// 表格中进度状态名称
	private String progressCode = "PROGRESS_CODE";
	// 表格中责任科室名称
	private String rDeptCode = "R_DEPT_CODE";
	private int[] chks = new int[] { 12, 15, 22 };
	private String[] chkskeys = new String[] { varianceFlg, isExe, rDeptFlg };
	// 记录当前选中行
	private int selectRow = -1;
	// 病人住院号
	private String case_no;
	// 病患临床路径信息
	private String clncPathCode;
	// table 在新数据时的可编辑列
	private int[] newTableDataColumn = { 2, 3, 4, 10, 11, 12 };
	// table在原数据时的可编辑列
	private int[] editTableDataColumn = { 13, 14, 15, 16, 17, 18, 19, 20, 21,
			22, 23, 24, 25 };
	// 新数据标示
	private String newDataStr = "新";
	// 旧数据标示
	private String editDataStr = "编辑";
	private TComboBox cmbSysCharge;
	private Compare compare = new Compare();
	private TTable TABLEVARIATION;
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * 页面初始化方法
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 页面初始化方法
	 */
	private void initPage() {
		onClear();
		// 初始化传入参数
		initInParm();
		// //初始化病人信息
		// initPatientInfo();
		// 初始化时程树
		// 注：时程树的click事件放在页面中进行绑定
		initTree();
		// 初始化执行控件
		initMonCatCode();
		// 初始化表格编辑时的事件
		initTableEdit();
	}

	/**
	 * 初始化传入参数
	 */
	private void initInParm() {
		TParm inParm = (TParm) this.getParameter();
		this.case_no = inParm.getValue("CLP", "CASE_NO");
		this.clncPathCode = inParm.getValue("CLP", "CLNCPATH_CODE");
		String flg = inParm.getValue("CLP", "FLG");
		if (null != flg && flg.equals("Y")) {
			callFunction("UI|close|setEnabled", false);
		}
		TABLEVARIATION= (TTable) this.getComponent("TABLEVARIATION");
	}

	/**
	 * 初始化表格编辑时的事件
	 */
	private void initTableEdit() {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onEditTable");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		addListener(TABLEVARIATION);
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
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
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
				TParm tableData = TABLEVARIATION.getParmValue();
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
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = TABLEVARIATION.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

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
		TABLEVARIATION.setParmValue(parmTable);
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
	 * 变异分析更改
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		int col = table.getSelectedColumn();
		String columnName = table.getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		if ("VARIANCEFLG".equals(columnName)) {
			if (table.getParmValue().getValue(columnName, row).equals("Y")) {
				setRowStatus(row, RecordStatus.editRecord, "TABLEVARIATION");
				setVarianCe(true, row);
			} else {
				setRowStatus(row, RecordStatus.normalRecord, "TABLEVARIATION");
				setVarianCe(false, row);
			}
		}
		return false;

	}

	/**
	 * 变异分析类别原因赋值 flg =true 勾选状态 =======pangben 2012-6-12
	 */
	private void setVarianCe(boolean flg, int row) {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		if (flg) {
			tableParm.setData("MEDICAL_MONCAT", row, this
					.getValue("MONCAT_CODE"));
			tableParm.setData("MEDICAL_VARIANCE", row, this
					.getValue("VARIANCE_CODE"));
		} else {
			tableParm.setData("MEDICAL_MONCAT", row, "");
			tableParm.setData("MEDICAL_VARIANCE", row, "");
		}
		table.setParmValue(tableParm);
	}

	/**
	 * 表格编辑时弹框
	 */
	public void onEditTable(Component com, int row, int column) {
		int selectedRow = this.getSelectedRow("TABLEVARIATION");
		if (selectedRow < 0) {
			return;
		}
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		// 判断列是否可以编辑
		// 用户开医嘱
		if (column == 10) {
			String orderFlg = tableParm.getValue("ORDER_FLG", selectedRow);
			String url = "";
			if ("Y".equals(orderFlg)) {
				url = "%ROOT%\\config\\clp\\CLPSysFeePopup.x";
			} else if ("N".equals(orderFlg)) {
				url = "%ROOT%\\config\\clp\\CLPChkItemPopup.x";
			} else if ("O".equals(orderFlg)) {
				url = "%ROOT%\\config\\clp\\CLPNursOrderPopup.x";
			}
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", orderFlg);
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("1", getConfigParm()
					.newConfig(url), parm);
			// 定义接受返回值方法
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturn");
		}
	}

	// 得到表格中编辑临床路径的值,若新增行有此值，则再次加入一行
	public void popReturn(String tag, Object obj) {
		// 判断对象是否为空和是否为TParm类型
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// 类型转换成TParm
		TParm result = (TParm) obj;
		if ("1".equals(tag)) {
			String typeCode = result.getValue("ORDER_CODE");
			String typeCHNDesc = result.getValue("ORDER_CHN_DESC");
			TTable table = (TTable) this.getComponent("TABLEVARIATION");
			table.acceptText();
			TParm tableParm = table.getParmValue();
			int row = this.getSelectedRow("TABLEVARIATION");
			tableParm.setData("MAINORD_CODE", row, typeCode);
			tableParm.setData("MAIN_ORDER_CODE_DESC", row, typeCHNDesc);
			table.setParmValue(tableParm);
			if (!"".equals(typeCHNDesc)) {
				this.addNewRow();
			}
		}
	}

	/**
	 * 初始化执行控件
	 */
	private void initMonCatCode() {
		TTextFormat tf = (TTextFormat) this.getComponent("MONCAT_CODE");
		// ========pangben 2012-06-27 start 将门诊的收据类别过滤,只显示住院的
		cmbSysCharge = (TComboBox) this.getComponent("IPD_CHARGE_CODE");
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ADM_TYPE, RECP_TYPE,CHARGE01, "
										+ "CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, "
										+ "CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
										+ " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, CHARGE26, CHARGE27, CHARGE28, "
										+ "CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS'"));
		// 显示中文
		TParm sysParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,DATA FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE' ORDER BY SEQ,ID"));
		TParm data = new TParm();
		int index = 0;
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				if (parm.getValue("CHARGE0" + i, 0).length() > 0) {
					index++;
					// 将中文添加到列表中
					data.addData("id", parm.getValue("CHARGE0" + i, 0));
					for (int j = 0; j < sysParm.getCount(); j++) {
						if (parm.getValue("CHARGE0" + i, 0).equals(
								sysParm.getValue("ID", j))) {
							data.addData("text", sysParm.getValue("NAME", j));
							break;
						}
					}

				}
			} else {
				if (parm.getValue("CHARGE" + i, 0).length() > 0) {
					index++;
					data.addData("id", parm.getValue("CHARGE" + i, 0));
					for (int j = 0; j < sysParm.getCount(); j++) {
						if (parm.getValue("CHARGE" + i, 0).equals(
								sysParm.getValue("ID", j))) {
							data.addData("text", sysParm.getValue("NAME", j));
							break;
						}
					}
				}
			}
		}
		data.setCount(index);
		StringBuffer stringData = new StringBuffer();
		stringData.append("[[id,text],[,],");
		// 整理显示格式
		for (int i = 0; i < data.getCount(); i++) {
			stringData.append("[").append(data.getValue("id", i)).append(",")
					.append(data.getValue("text", i)).append("],");
		}
		String stringValue = stringData.substring(0, stringData
				.lastIndexOf(","));
		stringValue += "]";
		cmbSysCharge.setStringData(stringValue);
		// ========pangben 2012-06-27 stop
		tf
				.setPopupMenuSQL("SELECT MONCAT_CODE AS ID , MONCAT_CHN_DESC AS NAME , MONCAT_ENG_DESC AS ENNAME   FROM CLP_VARMONCAT WHERE REGION_CODE='"
						+ Operator.getRegion() + "'");
		tf.onQuery();
		TCheckBox chkExe=(TCheckBox)this.getComponent("EXEC_FLG");
		chkExe.setSelected(true);
		this.setValue("CHK_USER", "001");
	}

	/**
	 * 初始化时程树
	 */
	private void initTree() {
		TTree tree = (TTree) callMessage("UI|TTree|getThis");
		// 初始化Tree的基本信息
		// 得到Tree的基础数据
		TParm selectTParm = new TParm();
		this.putBasicSysInfoIntoParm(selectTParm);
		// TParm result = CLPDurationTool.getInstance().selectData(selectTParm);
		TTreeNode root = (TTreeNode) callMessage("UI|TTree|getRoot");
		root.setText("临床路径时程");
		root.setType("Root");
		root.setID("");
		root.removeAllChildren();
		List dataList = getTreeDataList();
		for (Object elem : dataList) {
			TParm dataParm = (TParm) elem;
			String noteType = "Path"; // UI
			TTreeNode treeNode = new TTreeNode("KPILEAVE", noteType);
			if (dataParm.getValue("PARENT_DURATION_CODE") != null
					&& (!"".equals(dataParm.getValue("PARENT_DURATION_CODE")))) {
				treeNode.setText(dataParm.getValue("PARENT_DURATION_CHN_DESC"));
				treeNode.setID(dataParm.getValue("PARENT_DURATION_CODE"));
				if (root.findChildNodeForID(treeNode.getID()) != null) {
					treeNode = root.findNodeForID(treeNode.getID());
				}
				TTreeNode childNode = new TTreeNode("KPILEAVE", noteType);
				childNode.setText(dataParm.getValue("DURATION_CHN_DESC"));
				childNode.setID(dataParm.getValue("DURATION_CODE"));
				treeNode.add(childNode);
			} else {
				treeNode.setText(dataParm.getValue("DURATION_CHN_DESC"));
				treeNode.setID(dataParm.getValue("DURATION_CODE"));
			}

			root.add(treeNode);
		}
		tree.update();
	}

	/**
	 * 得到时程树数据
	 * 
	 * @return List
	 */
	private List<TParm> getTreeDataList() {
		List list = new ArrayList();
		TParm selectParm = new TParm();
		selectParm.setData("CASE_NO", case_no);
		selectParm.setData("REGION_CODE", Operator.getRegion());
		TParm result = CLPVariationTool.getInstance().selectDuringInfo(
				selectParm);
		for (int i = 0; i < result.getCount(); i++) {
			TParm parm = result.getRow(i);
			list.add(parm);
		}
		return list;
	}

	/**
	 * 变异类别变动方法
	 */
	public void onMonCatChange() {
		// 得到变异类别
		String monCatCode = this.getValueString("MONCAT_CODE");
		TTextFormat varianceCode = (TTextFormat) this
				.getComponent("VARIANCE_CODE");
		varianceCode.setValue("");
		StringBuffer sqlbf = new StringBuffer();
		sqlbf
				.append(" SELECT VARIANCE_CODE AS ID , VARIANCE_CHN_DESC AS NAME , VARIANCE_ENG_DESC AS ENNAME ");
		sqlbf.append(" FROM CLP_VARIANCE WHERE MONCAT_CODE = '"
				+ monCatCode.trim() + "' ");
		sqlbf.append(" AND REGION_CODE='" + Operator.getRegion() + "'");
		varianceCode.setPopupMenuSQL(sqlbf.toString());
		varianceCode.onQuery();
	}

	/**
	 * 时程树点击方法
	 */
	public void duringTreeClick() {
		this.onQuery();
	}

	/**
	 * 变异按钮点击
	 */
	public void varianceChange() {
		// 触发变异单选框
		boolean flag = (Boolean) this.callFunction("UI|varianceFlg|isSelected");
		String varianceFlg = "N";
		String varianceType = this.getValueString("MONCAT_CODE");
		String varianceStr = this.getValueString("VARIANCE_CODE");
		if (flag) {
			varianceFlg = "Y";
		} else {
			varianceFlg = "N";
			varianceType = "";
			varianceStr = "";
		}
		HashMap map = new HashMap();
		map.put("VARIANCEFLG", varianceFlg);
		map.put("MEDICAL_MONCAT", varianceType);
		map.put("MEDICAL_VARIANCE", varianceStr);
		map.put("MANAGE_MONCAT", varianceType);
		map.put("MANAGE_VARIANCE", varianceStr);
		// 给表格TParm的对应栏位统一赋值
		setTableTParmValue(map);
		// 设置记录状态
		if (flag) {
			TTable table = (TTable) this.getComponent("TABLEVARIATION");
			TParm tableParm = table.getParmValue();
			for (int i = 0; i < tableParm.getCount(); i++) {
				String status = tableParm.getRow(i).getValue("STATUS");
				if (newDataStr.equals(status)) {
					continue;
				}
				setRowStatus(i, RecordStatus.editRecord, "TABLEVARIATION");
			}
		}
	}

	/**
	 * 执行单选点击
	 */
	public void execFlagChange() {
		boolean flag = (Boolean) this.callFunction("UI|IsExeFlag|isSelected");
		String isexe = "";
		String progressStr = this.getValueString("PROGRESS_CODE");
		if (flag) {
			isexe = "Y";
		} else {
			isexe = "N";
			progressStr = "";
		}
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		for (int i = 0; i < tableParm.getCount(); i++) {
			String status = tableParm.getRow(i).getValue("STATUS");
			if (newDataStr.equals(status)) {
				continue;
			}
//			if ("N".equals(tableParm.getRow(i).getValue("ORDER_FLG"))) {
//				tableParm.setData("ISEXE", i, isexe);
//				tableParm.setData("PROGRESS_CODE", i, progressStr);
//				// 设置记录状态--只有关键诊疗才有此功能
//				if (flag) {
//					setRowStatus(i, RecordStatus.editRecord, "TABLEVARIATION");
//				}
//			}
			tableParm.setData("ISEXE", i, isexe);
			tableParm.setData("PROGRESS_CODE", i, progressStr);
			// 设置记录状态--只有关键诊疗才有此功能
			if (flag) {
				setRowStatus(i, RecordStatus.editRecord, "TABLEVARIATION");
			}
		}
		table.setParmValue(tableParm);
	}

	/**
	 * 责任科室按钮点击
	 */
	public void rdeptChange() {
		boolean flag = (Boolean) this.callFunction("UI|rdeptFlg|isSelected");
		String rdeptFlg = "N";
		String rdeptStr = this.getValueString("R_DEPT_CODE");
		if (flag) {
			rdeptFlg = "Y";
		} else {
			rdeptFlg = "N";
			rdeptStr = "";
		}
		HashMap map = new HashMap();
		map.put("RDEPTFLG", rdeptFlg);
		map.put("R_DEPT_CODE", rdeptStr);
		// 给表格TParm的对应栏位统一赋值
		setTableTParmValue(map);
		// 设置记录状态
		if (flag) {
			TTable table = (TTable) this.getComponent("TABLEVARIATION");
			TParm tableParm = table.getParmValue();
			for (int i = 0; i < tableParm.getCount(); i++) {
				String status = tableParm.getRow(i).getValue("STATUS");
				if (newDataStr.equals(status)) {
					continue;
				}
				setRowStatus(i, RecordStatus.editRecord, "TABLEVARIATION");
			}

		}
	}

	/**
	 * 设置记录状态
	 * 
	 * @param row
	 *            int
	 * @param recordStatus
	 *            RecordStatus
	 */
	private void setRowStatus(int row, RecordStatus recordStatus,
			String tableName) {
		String str = "";
		switch (recordStatus) {
		case newRecord:
			str = "新";
			break;
		case editRecord:
			str = "编辑";
			break;
		default:
			str = "";
			break;
		}
		// System.out.println("记录状态更新的值：" + str);
		TTable table = (TTable) this.getComponent(tableName);
		TParm tableParm = table.getParmValue();
		tableParm.setData("STATUS", row, str);
		table.setParmValue(tableParm);
	}

	/**
	 * 设置整个表格的状态
	 * 
	 * @param tableName
	 *            String
	 */
	private void setTableRowStatus(String tableName, RecordStatus recordStatus) {
		// System.out.println("设置整个表格的状态");
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		for (int i = 0; i < tableParm.getCount(); i++) {
			setRowStatus(i, recordStatus, tableName);
		}
	}

	public enum RecordStatus {
		newRecord, editRecord, normalRecord;
	}

	/**
	 * 给表格TParm的对应栏位统一赋值
	 * 
	 * @param map
	 *            HashMap
	 */
	private void setTableTParmValue(HashMap map) {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		for (int i = 0; i < tableParm.getCount(); i++) {
			Set set = map.keySet();
			Iterator itr = set.iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				// 只有非新加入数据才有此功能
				String flagstr = tableParm.getValue("STATUS", i);
				if (!"新".equals(flagstr)) {
					tableParm.setData(key, i, map.get(key));
				}
			}
		}
		// System.out.println("tableParm:" + tableParm);
		table.setParmValue(tableParm);
	}

	/**
	 * 根据实际情况判断表格的选择列是否可以编辑|
	 * 
	 */
	public void setTableEnabledWithValidColumn() {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		int column = table.getSelectedColumn();
		int row = table.getSelectedRow();
		String status = table.getParmValue().getValue("STATUS", row);
		boolean isNewRow = "新".equals(status);
		// 判断列是否可以编辑
		boolean isRowCanEdit = false;
		// 可编辑列额
		int[] source = null;
		// 新数据
		if (isNewRow) {
			source = this.newTableDataColumn;
			// 原数据
		} else {
			source = this.editTableDataColumn;
		}
		for (int tmp : source) {
			if (tmp == column) {
				isRowCanEdit = true;
			}
		}
		// 如果不是可以编辑的列直接return
		if (!isRowCanEdit) {
			return;
		}
		setTableEnabled("TABLEVARIATION", table.getSelectedRow(), table
				.getSelectedColumn());
		// 处理checkbox的选中状态，如果选中的为checkbox则将其做点击操作
		if (!isNewRow) {
			for (int i = 0; i < chks.length; i++) {
				if (chks[i] == column) {
					TParm parm = table.getParmValue();
					// 在执行进度状态时只有关键诊疗项目才有进度状态，故非进度状态不显示
					if (chkskeys[i].equals(this.isExe)
							&& !"N".equals(parm.getRow(row).getValue(
									"ORDER_FLG"))) {
						break;
					}
					if ("Y".equals(parm.getValue(chkskeys[i], row))) {
						parm.setData(chkskeys[i], row, "N");
						clearDefaultValueWithChekKey(chkskeys[i], row, parm);
					} else {
						parm.setData(chkskeys[i], row, "Y");
						addDefaultValueWithChekKey(chkskeys[i], row, parm);
					}
					table.setParmValue(parm);
				}
			}
		}
		// 根据实际情况判断表格的编辑状态，并在表格的第一列做标记
		setRowStatusWithTabelValueChangeAction(column, row);
	}

	/**
	 * 根据选中的check框加入默认值（变异原因，变异类别，责任科室，是否执行）
	 * 
	 * @param chkKey
	 *            String
	 * @param row
	 *            int
	 */
	private void addDefaultValueWithChekKey(String chkKey, int row,
			TParm tableParm) {
		// 变异原因，变异类别
		if (chkKey.equals(this.varianceFlg)) {
			tableParm.setData(this.medicalMoncat, row, this
					.getValue("MONCAT_CODE"));
			tableParm.setData(this.medicalVariance, row, this
					.getValue("VARIANCE_CODE"));
			tableParm.setData(this.manageMoncat, row, this
					.getValue("MONCAT_CODE"));
			tableParm.setData(this.manageVariance, row, this
					.getValue("VARIANCE_CODE"));
		}
		if (chkKey.equals(this.isExe)
				&& "N".equals(tableParm.getValue("ORDER_FLG", row))) {
			tableParm.setData(this.progressCode, row, this
					.getValue("PROGRESS_CODE"));
		}
		if (chkKey.equals(this.rDeptFlg)) {
			tableParm
					.setData(this.rDeptCode, row, this.getValue("R_DEPT_CODE"));
		}

	}

	/**
	 * 根据选中的check框清空默认值（变异原因，变异类别，责任科室，是否执行）
	 * 
	 * @param chkKey
	 *            String
	 * @param row
	 *            int
	 */
	private void clearDefaultValueWithChekKey(String chkKey, int row,
			TParm tableParm) {
		// 变异原因，变异类别
		if (chkKey.equals(this.varianceFlg)) {
			tableParm.setData(this.medicalMoncat, row, "");
			tableParm.setData(this.medicalVariance, row, "");
			tableParm.setData(this.manageMoncat, row, "");
			tableParm.setData(this.manageVariance, row, "");
		}
		if (chkKey.equals(this.isExe)
				&& "N".equals(tableParm.getValue("ORDER_FLG", row))) {
			tableParm.setData(this.progressCode, row, "");
		}
		if (chkKey.equals(this.rDeptFlg)) {
			tableParm.setData(this.rDeptCode, row, "");
		}

	}

	/**
	 * 根据实际情况判断表格的编辑状态，并在表格的第一列做标记
	 */
	public void setRowStatusWithTabelValueChangeAction(int column, int row) {
		// System.out.println("进入状态设置方法-----------------");
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		// 如果是最后一行的话，状态不用更新成编辑
		if (row == (table.getRowCount() - 1)) {
			return;
		}
		// 根据实际情况判断表格的编辑状态，并在表格的第一列做标记
		TParm tableParm = table.getParmValue();
		if ("Y".equals(tableParm.getValue("VARIANCEFLG", row))) {
			this.setRowStatus(row, RecordStatus.editRecord, "TABLEVARIATION");
		}
		if ("Y".equals(tableParm.getValue("ISEXE", row))
				&& "N".equals(tableParm.getValue("ORDER_FLG", row))) {
			this.setRowStatus(row, RecordStatus.editRecord, "TABLEVARIATION");
		}
		// System.out.println("--------------" +
		// tableParm.getValue("RDEPTFLG", row));
		if ("Y".equals(tableParm.getValue("RDEPTFLG", row))) {
			this.setRowStatus(row, RecordStatus.editRecord, "TABLEVARIATION");
		}

	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 检查时程代码
		TTree duringTree = (TTree) this.getComponent("TTree");
		TTreeNode node = duringTree.getSelectionNode();
		if (node == null) {
			this.messageBox("请选择时程!");
			return;
		}
		// //得到查询条件处理type2 情况---标准有，实际没有，有分组注记
		// TParm parmType2 = this.getSearchTParm();
		// parmType2.setData("type", "2");
		// TParm data2 =
		// CLPVariationTool.getInstance().selectVariation(parmType2);
		// //得到查询条件处理type3 情况---标准没有，实际有，有分组注记
		// TParm parmType3 = this.getSearchTParm();
		// parmType3.setData("type", "3");
		// TParm data3 =
		// CLPVariationTool.getInstance().selectVariation(parmType3);
		// 得到查询条件处理type4 情况 全部
		TParm parmType4 = this.getSearchTParm();
		parmType4.setData("type", "4");

		// //处理查询出的数据
		// System.out.println("data4:::::::"+data4);
		// compare2And3(data2, data3);
		// //将处理后的2，3 的数据加入到4后
		// append2and3To4(data2, data3, data4);
		// 处理合并同类项的情况-默认表中的数据已经合并同类项
		//TCheckBox cmbChk = (TCheckBox) this.getComponent("combination");
		TParm data4 = CLPVariationTool.getInstance().selectVariation(parmType4);
		// 不需要合并数据时将查询出的数据拆开（数据库中的数据默认是合并的）
		// if (!cmbChk.isSelected()) {
		// combinationDataToSeparate(data4);
		// }
		// else{
		// //System.out.println("data4::"+data4.getCount());
		// data4=combinationDataToSeparateSum(data4);
		// }
		// else{
		// //data4=
		// CLPVariationTool.getInstance().selectSumVariation(parmType4);
		// }
		// 行数
		if (data4.getCount() > 0) {
			// 差异选择
			checkDiffer(data4);
			// 费用差异选择时进行处理
			differNumberOperate(data4);
			// 给表格赋值
			this.callFunction("UI|TABLEVARIATION|setParmValue", data4);
		} else {
			this.messageBox("查无数据!");
			this.callFunction("UI|TABLEVARIATION|setParmValue", new TParm());
		}
		// 在选择时程不是父时程时，提供用户新增功能
		if (node.getChildCount() <= 0 && node != null) {
			// 都提供用户新增功能
			addNewRow();
		}
	}

	/**
	 * 合并医嘱
	 * 
	 * @return
	 */
	private TParm getTalbeParmValue(TParm parm) {
		TParm result = new TParm();
		String orderCode = parm.getValue("ORDER_CODE", 0);// 医嘱名称
		// String schd_code=parm.getValue("SCHD_CODE",0);//时程
		String chkUserCode = parm.getValue("CHKUSER_CODE", 0);// 执行人员
		String chkTypeCode = parm.getValue("CHKTYPE_CODE", 0);// 执行类型
		// String exec_flg=parm.getValue("EXEC_FLG",0);//执行状态
		String dispenseUnit = parm.getValue("DISPENSE_UNIT", 0);// 标准单位
		double tot = 0.00;// 标准数量
		double mainTot = 0.00;// 实际数量
		double totAmt = 0.00;// 标准金额
		double mainTotAmt = 0.00;// 实际金额
		int index = 0;// 累计数量
		int nowIndex = 0;// 获得上一行的数据 给TParm 赋值
		boolean flg = false;// 如果只有一条数据 不执行重新赋值动作
		for (int i = 0; i < parm.getCount(); i++) {
			flg = false;
			// 合并
			if (parm.getValue("ORDER_CODE", i).equals(orderCode)
					&& parm.getValue("CHKUSER_CODE", i).equals(chkUserCode)
					&& parm.getValue("CHKTYPE_CODE", i).equals(chkTypeCode)
					&& parm.getValue("DISPENSE_UNIT", i).equals(dispenseUnit)) {
				tot += parm.getDouble("TOT", i);
				mainTot += parm.getDouble("MAINTOT", i);
				totAmt += parm.getDouble("TOT_AMT", i);
				mainTotAmt += parm.getDouble("MAIN_AMT", i);
				nowIndex = i;
				flg = true;
			} else {
				orderCode = parm.getValue("ORDER_CODE", i);// 医嘱名称
				// schd_code=parm.getValue("SCHD_CODE",i);//时程
				chkUserCode = parm.getValue("CHKUSER_CODE", i);// 执行人员
				chkTypeCode = parm.getValue("CHKTYPE_CODE", i);// 执行类型
				// exec_flg=parm.getValue("EXEC_FLG",i);//执行状态
				dispenseUnit = parm.getValue("DISPENSE_UNIT", i);// 标准单位
				// 获得一行数据

				if (flg) {
					result.setRowData(index, parm, nowIndex);
					result.setData("TOT", index, tot);
					result.setData("MAINTOT", index, mainTot);
					result.setData("TOT_AMT", index, totAmt);
					result.setData("MAIN_AMT", index, mainTotAmt);
				} else {
					result.setRowData(index, parm, i);
				}
				tot = 0.00;// 标准数量
				mainTot = 0.00;// 实际数量
				totAmt = 0.00;// 标准金额
				mainTotAmt = 0.00;// 实际金额
				index++;
			}
		}
		result.setCount(index);
		return result;
	}

	/**
	 * 将相同医嘱的数据合并
	 * 
	 * @param parm
	 */
	private void combinationData(TParm parm) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			TParm rowParm = parm.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE");
			String mainOrderCode = rowParm.getValue("MAINORD_CODE");
			if (orderCode.equals(mainOrderCode)) {

			}
		}
	}

	/**
	 * 将原始数据中的合并数据拆开
	 */
	public void combinationDataToSeparate(TParm sourceParm) {
		for (int i = 0; i < sourceParm.getCount(); i++) {
			TParm rowParm = sourceParm.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE");
			String mainOrderCode = rowParm.getValue("MAINORD_CODE");
			if (!orderCode.equalsIgnoreCase(mainOrderCode)
					&& (this.checkNullAndEmpty(orderCode))
					&& (this.checkNullAndEmpty(mainOrderCode))) {
				TParm newParm = this.cloneTParm(rowParm);
				newParm.setData("ORDER_DESC", "");
				newParm.setData("ORDER_CODE", "");
				newParm.setData("DISPENSE_UNIT", "");
				newParm.setData("TOT", "");
				// luhai add 2011-07-25
				// 差异分析时需要根据费用判断，故新旧数据的费用相关项要同步
				newParm.setData("TOT_AMT", "");
				addTParmToAnotherTParm(newParm, sourceParm);
				// 清空本行记录的实际记录
				sourceParm.setData("MAIN_ORDER_CODE_DESC", i, "");
				sourceParm.setData("MAINDISPENSE_UNIT", i, "");
				sourceParm.setData("MAINTOT", i, "");
				// luhai add 2011-07-25
				// 差异分析时需要根据费用判断，故新旧数据的费用相关项要同步
				sourceParm.setData("MAIN_AMT", i, "");
				sourceParm.setData("MAINORD_CODE", i, "");
			}
		}
	}

	public void onDelete() {
		selectRow = this.getSelectedRow("TABLEVARIATION");
		if (selectRow == -1) {
			this.messageBox("请选择需要删除的数据");
			return;
		}
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		String status = tableParm.getValue("STATUS", selectRow);
		// 判断是否含有其他的新数据，若只有一条新数据则不提供删除功能
		boolean ishasMoreNewRow = "新".equals(tableParm.getValue("STATUS",
				(selectRow - 1)))
				|| "新".equals(tableParm.getValue("STATUS", (selectRow + 1)));
		if ("新".equals(status) && ishasMoreNewRow) {
			table.removeRow(selectRow);
			table.acceptText();
		} else if ("新".equals(status)) {
			this.messageBox("仅有一条新数据无法删除");
		} else {
			this.messageBox("非新加入数据无法删除");
		}
	}

	/**
	 * 添加空行方法
	 */
	private void addNewRow() {
		// 在有数据时默认给表格加入一行 ，以便用户添加
		this.callFunction("UI|TABLEVARIATION|addRow");
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		tableParm.setData("STATUS", (tableParm.getCount() - 1), "新");
		tableParm.setData("ORDER_FLG", (tableParm.getCount() - 1), "Y");
		tableParm.setData("CASE_NO", (tableParm.getCount() - 1), this.case_no);
		tableParm.setData("CLNCPATH_CODE", (tableParm.getCount() - 1),
				this.clncPathCode);
		tableParm.setData("SCHD_CODE", (tableParm.getCount() - 1),
				getCurrentDuration()); // 取上一条数据的时程
		tableParm.setData("REGION_CODE", (tableParm.getCount() - 1), Operator
				.getRegion());
		tableParm.setData("OPT_USER", (tableParm.getCount() - 1), Operator
				.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		tableParm.setData("OPT_DATE", (tableParm.getCount() - 1), datestr);
		tableParm.setData("OPT_TERM", (tableParm.getCount() - 1), Operator
				.getIP());
		table.setParmValue(tableParm);
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		// 验证数据
		if (!validSaveData()) {
			return;
		}
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		TParm saveParm = new TParm();
		TParm parm = table.getParmValue();
		saveParm.setData("varianceParm", parm.getData());
		saveParm.setData("operatorMap", this.getBasicOperatorMap());
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPVariantionAction", "saveVariance", saveParm);
		if (result.getErrCode() <= 0) {
			this.messageBox("P0001");
			this.onQuery();
		} else {
			this.messageBox("E0001");
		}
	}

	/**
	 * 验证保存数据
	 */
	private boolean validSaveData() {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		TParm parm = table.getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			TParm rowParm = parm.getRow(i);
			if (rowParm.getValue("STATUS").equals("新")
					&& this.checkNullAndEmpty(rowParm
							.getValue("MAIN_ORDER_CODE_DESC"))) {
				// 总量
				String mainTot = rowParm.getValue("MAINTOT");
				if ("".equals(mainTot)) {
					this.messageBox("请输入总量");
					return false;
				}
				if ((!"".equals(mainTot)) && (!this.validDouble(mainTot))) {
					this.messageBox("总量请输入合法数值");
					return false;
				}
				if (Double.parseDouble(mainTot) <= 0) {
					this.messageBox("总量请输入合法数值");
					return false;
				}
				// 单位
				String despenseUnit = rowParm.getValue("MAINDISPENSE_UNIT");
				if (!this.checkNullAndEmpty(despenseUnit)) {
					this.messageBox("请输入单位");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 费用分析
	 */
	public void feeAnalyse() {
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", this.case_no);
		sendParm.setData("CLNCPATH_CODE", this.clncPathCode);
		// 检查时程代码
		TTree duringTree = (TTree) this.getComponent("TTree");
		TTreeNode node = duringTree.getSelectionNode();
		if (node != null) {
			String schdCode = node.getID();
			sendParm.setData("DURATION_CODE", schdCode);
		}
		// System.out.println("sendParm::::::"+sendParm);
		String resultstr = (String) this.openDialog(
				"%ROOT%\\config\\clp\\CLPBill.x", sendParm);
	}

	public void onClear() {
		TCheckBox combination = (TCheckBox) this.getComponent("combination");
		combination.setSelected(false);
		this.setValue("CHK_USER", "");
		this.setValue("ORDER_FLG", "");
		this.setValue("IPD_CHARGE_CODE", "");
		this.setValue("ORDTYPE_CODE", "");
		this.setValue("PROGRESS_CODE", "");
		this.setValue("MONCAT_CODE", "");
		this.setValue("VARIANCE_CODE", "");
		this.setValue("R_DEPT_CODE", "");
		this.setValue("CLP_DEPT_CODE", "");
		this.setValue("EXE_DEPT_CODE", "");
		this.setValue("DifferNumber", "");
		this.setValue("CLP_STATION_CODE", "");
		this.callFunction("UI|TABLEVARIATION|setParmValue", new TParm());
		TCheckBox chkExe=(TCheckBox)this.getComponent("EXEC_FLG");
		chkExe.setSelected(true);
		this.setValue("CHK_USER", "001");
	}

	/**
	 * 病历书写
	 */
	public void onEmrWrite() {
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", case_no);
		parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("IPD_NO", this.getValue("IPD_NO"));
		parm.setData("ADM_DATE", this.getValue("ADM_DATE"));
		parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
		// if (this.isOidrFlg()) {
		parm.setData("RULETYPE", "3");
		// 写入类型(会诊)
		parm.setData("WRITE_TYPE", "OIDR");
		/**
		 * }else { parm.setData("RULETYPE", "2"); //写入类型(会诊)
		 * parm.setData("WRITE_TYPE",""); }
		 **/
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);

	}

	/**
	 * 处理当前TOOLBAR，该页面使用了公共的病患选择页面 在选择其他菜单再次返回该页面时防止菜单变为病患选择页面,需加入此方法
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		return true;
	}

	/**
	 * 费用差异处理
	 */
	private void differNumberOperate(TParm parm) {
		TRadioButton differD = (TRadioButton) this.getComponent("differD");
		if (!differD.isSelected()) {
			return;
		}
		String differNumberstr = this.getValueString("DifferNumber");
		if (!this.checkNullAndEmpty(differNumberstr)) {
			return;
		}
		if (!this.validDouble(differNumberstr)) {
			this.messageBox("请输入正确费用差异百分比!");
			return;
		}
		double differNumber = Double.parseDouble(differNumberstr);
		for (int i = 0; i < parm.getCount(); i++) {
			TParm datarow = parm.getRow(i);
			String orderCode = datarow.getValue("ORDER_CODE");
			String mainOrdCode = datarow.getValue("MAINORD_CODE");
			// luhai 2011-07-25 删除 根据总量判断差异 begin
			// String tot = datarow.getValue("TOT");
			// tot = "".equals(tot) ? "0" : tot;
			// String mainTot = datarow.getValue("MAINTOT");
			// mainTot = "".equals(mainTot) ? "0" : mainTot;
			// //System.out.println("实际总量:" + mainTot + "标准总量:" + tot + "差异数:" +
			// differNumberstr);
			// //System.out.println("orderCode");
			// luhai 2011-07-25 删除 根据总量判断差异 end
			if ("".equals(orderCode) || "".equals(mainOrdCode)) {
				continue;
			}
			// luhai 2011-07-25 删除 根据总量判断差异 begin
			// if (!(Math.abs(Double.parseDouble(mainTot) -
			// Double.parseDouble(tot)) / Double.parseDouble(tot) >=
			// Double.parseDouble(differNumberstr) / 100)) {
			// parm.removeRow(i);
			// i--;
			// }
			// luhai 2011-07-05 删除 根据总量判断差异
			// luhai 加入根据实际费用判断差异 begin
			double standardAMT = datarow.getDouble("TOT_AMT");
			double mainAMT = datarow.getDouble("MAIN_AMT");
			if (!(Math.abs(mainAMT - standardAMT) / standardAMT >= differNumber / 100)) {
				parm.removeRow(i);
				i--;
			}
			// luhai 加入根据实际费用判断差异 end
		}
	}

	/**
	 * 差异选择
	 */
	private void checkDiffer(TParm parm) {
		TRadioButton differA = (TRadioButton) this.getComponent("differA");
		// 查询全部情况
		if (differA.isSelected()) {
			return;
		}
		// 合并功能 差异 分析 数据
		// ================pangben 2012-7-11
		TCheckBox cmbChk = (TCheckBox) this.getComponent("combination");
		TRadioButton differN = (TRadioButton) this.getComponent("differN");
		TRadioButton differD = (TRadioButton) this.getComponent("differD");
		for (int i = 0; i < parm.getCount(); i++) {
			TParm parmdata = parm.getRow(i);
			if (!cmbChk.isSelected()) {
				if (differN.isSelected()) { // 无差异
					if (checkDataIsDifference(parmdata)) {
						// 差异数据时处理
						parm.removeRow(i);
						i--;
					}
				} else if (differD.isSelected()) {
					// 非差异数据时处理
					// 有差异
					if (!checkDataIsDifference(parmdata)) {
						parm.removeRow(i);
						i--;
					}
				}
			} else {
				if (checkDataIsSame(parmdata)) {
					// 差异数据时处理
					if (differN.isSelected()) { // 无差异
						parm.removeRow(i);
						i--;
					}
				} else if (differD.isSelected()) {
					// 非差异数据时处理
					// 有差异
					if (!checkDataIsSame(parmdata)) {
						parm.removeRow(i);
						i--;
					}
				}
			}
		}
	}

	/**
	 * 合并选择
	 * 
	 * @return ================pangben 2012-7-11
	 */
	private boolean checkDataIsSame(TParm parmdata) {
		String orderFlag = parmdata.getValue("ORDER_FLG");
		String ordTypeCode = parmdata.getValue("ORDTYPE_CODE");// 临床路径编码
		String mainOrdTypeCode = parmdata.getValue("MAINORDTYPE_CODE");// 实际临床路径编码
		// 费用相关
		double totAMT = parmdata.getDouble("TOT_AMT");
		double mainAMT = parmdata.getDouble("MAIN_AMT");
		// 关键诊疗不参与差异
		if ("N".equals(orderFlag)) {
			return false;
		}
		// 标准临床路径编码为空
		// 临床路径差异
		if (null == ordTypeCode || null == mainOrdTypeCode
				|| null != ordTypeCode && null != mainOrdTypeCode
				&& !ordTypeCode.equals(mainOrdTypeCode)) {
			return true;
		}
		// 费用差异
		if (totAMT != mainAMT) {
			return true;
		}
		return false;
	}

	/**
	 * 检查数据是否是差异
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private boolean checkDataIsDifference(TParm parmdata) {
		String orderFlag = parmdata.getValue("ORDER_FLG");
		String orderCode = parmdata.getValue("ORDER_CODE");
		String mainOrdCode = parmdata.getValue("MAINORD_CODE");
		// 费用相关
		double tot = parmdata.getDouble("TOT");
		double mainTot = parmdata.getDouble("MAINTOT");
		// String dispenseUnit = parmdata.getValue("DISPENSE_UNIT");
		// String mainDispenseUnit = parmdata.getValue("MAINDISPENSE_UNIT");
		// String tot = parmdata.getValue("TOT");
		// String mainTot = parmdata.getValue("MAINTOT");
		// 关键诊疗不参与差异
		if ("N".equals(orderFlag)) {
			return false;
		}
		// System.out.println("--进入是否差异判断");
		// 标准项目为空
		// if (!this.checkNullAndEmpty(orderCode)) {
		// return true;
		// }
		// // 实际项目为空
		// if (!this.checkNullAndEmpty(mainOrdCode)) {
		// return true;
		// }
		// 项目差异
		if (null == orderCode || null == mainOrdCode || null != orderCode
				&& null != mainOrdCode && !orderCode.equals(mainOrdCode)) {
			return true;
		}
		// luhai 2011-07-25 修改 begin
		// //在标准项目和实际项目都存在的情况下，单位不同
		// if (!dispenseUnit.equals(mainDispenseUnit)
		// &&
		// (!"".equals(mainTot) &&
		// Double.parseDouble(tot) != Double.parseDouble(mainTot))
		// ) {
		// return true;
		// }
		// luhai 2011-07-25 删除根据单位和数量判断差异-改用费用判断差异 begin
		// 单位不同即为差异
		// if (!dispenseUnit.equals(mainDispenseUnit)&&(!"".equals(mainTot))){
		// return true;
		// }
		// //单位相同，数量不同差异
		// if (dispenseUnit.equals(mainDispenseUnit)
		// &&
		// (!"".equals(mainTot) &&
		// Double.parseDouble(tot) != Double.parseDouble(mainTot))
		// ) {
		// return true;
		// }
		// luhai 2011-07-25 修改 end
		// luhai 2011-07-25 删除根据单位和数量判断差异-改用费用判断差异 begin
		// luhai 2011-07-25 加入根据费用进行判断 begin
		if (tot != mainTot) {
			return true;
		}
		// luhai 2011-07-25 加入根据费用进行判断 end
		return false;
	}

	/**
	 * 将处理后的2，3 的数据加入到4后 append2and3To4
	 * 
	 * @param data2
	 *            TParm
	 * @param data3
	 *            TParm
	 * @param data4
	 *            TParm
	 */
	private void append2and3To4(TParm data2, TParm data3, TParm data4) {
		for (int i = 0; i < data2.getCount(); i++) {
			TParm parm = data2.getRow(i);
			String[] names = parm.getNames();
			for (int k = 0; k < names.length; k++) {
				data4.addData(names[k], parm.getValue(names[k]));
			}
		}
		for (int i = 0; i < data3.getCount(); i++) {
			TParm parm = data3.getRow(i);
			String[] names = parm.getNames();
			for (int k = 0; k < names.length; k++) {
				data4.addData(names[k], parm.getValue(names[k]));
			}
		}
	}

	/**
	 * 把一个TParm加入到另一个TParm
	 * 
	 * @param parm
	 *            TParm
	 * @param toParm
	 *            TParm
	 */
	public void addTParmToAnotherTParm(TParm parm, TParm toParm) {
		String[] names = parm.getNames();
		for (int k = 0; k < names.length; k++) {
			toParm.addData(names[k], parm.getValue(names[k]));
		}
		toParm.setCount(toParm.getCount() + 1);
	}

	/**
	 * 分析2，3 数据
	 */
	private void compare2And3(TParm data2, TParm data3) {
		for (int i = 0; i < data2.getCount(); i++) {
			TParm data2Row = data2.getRow(i);
			String standCode2 = data2Row.getValue("ORDER_CODE");
			String standTot2 = data2Row.getValue("TOT");
			String standUnit2 = data2Row.getValue("DISPENSE_UNIT");
			String actualCode2 = data2Row.getValue("MAINORD_CODE");
			String actualTot2 = data2Row.getValue("MAINTOT");
			String actualUnit2 = data2Row.getValue("MAINDISPENSE_UNIT");
			String standardGroupNo2 = data2Row.getValue("S_GROUP") == null ? ""
					: data2Row.getValue("S_GROUP");
			for (int y = 0; y < data3.getCount(); y++) {
				TParm data3Parm = data3.getRow(y);
				String standCode3 = data3Parm.getValue("ORDER_CODE");
				String standTot3 = data3Parm.getValue("TOT");
				String standlUnit3 = data3Parm.getValue("DISPENSE_UNIT");
				String actualCode3 = data3Parm.getValue("MAINORD_CODE");
				String actualTot3 = data3Parm.getValue("MAINTOT");
				String actualUnit = data3Parm.getValue("MAINDISPENSE_UNIT");
				String actualGroupNo3 = data3Parm.getValue("A_GROUP");
				String mainOrderCodeDesc = data3Parm
						.getValue("MAIN_ORDER_CODE_DESC");
				// 在2，3 可以比对的情况下，将3的数据加入2 ，并把3 的标准值赋值，以便下次循环将其滤去
				if ((!this.checkNullAndEmpty(actualCode2))
						&& (!this.checkNullAndEmpty(actualTot2))
						&& (!this.checkNullAndEmpty(actualUnit2))
						&& (!this.checkNullAndEmpty(standCode3))
						&& (!this.checkNullAndEmpty(standTot3))
						&& (!this.checkNullAndEmpty(standlUnit3))
						&& standardGroupNo2.trim().equals(actualGroupNo3)) {
					data2Row.setData("MAINORD_CODE", actualCode3);
					data2Row.setData("MAINTOT", actualTot3);
					data2Row.setData("MAINDISPENSE_UNIT", actualUnit);
					data2Row.setData("MAIN_ORDER_CODE_DESC", mainOrderCodeDesc);
					// //并把3 的标准值赋值，以便下次循环将其滤去
					// data3Parm.setData("ORDER_CODE","used");
					// data3Parm.setData("TOT","used");
					// data3Parm.setData("DISPENSE_UNIT","used");
					data3.removeRow(y);
					break;
				}

			}
		}

	}

	/**
	 * 得到查询条件
	 * 
	 * @return TParm
	 */
	private TParm getSearchTParm() {
		TParm parm = new TParm();
		TCheckBox tcbExecFlag = (TCheckBox) this.getComponent("EXEC_FLG");
		String execFlag = "Y";
		if (tcbExecFlag.isSelected()) {
			execFlag = "Y";
		} else {
			execFlag = "N";
		}
		parm.setData("EXEC_FLG", execFlag);
		TCheckBox combination = (TCheckBox) this.getComponent("combination");
		String combinationStr = "Y";
		if (combination.isSelected()) {
			combinationStr = "Y";
		} else {
			combinationStr = "N";
		}
		parm.setData("combination", combinationStr);
		parm.setData("CHK_USER", this.getValue("CHK_USER"));
		parm.setData("ORDER_FLG", this.getValue("ORDER_FLG"));
		parm.setData("IPD_CHARGE_CODE", this.getValue("IPD_CHARGE_CODE"));
		parm.setData("DEPT_CODE", this.getValue("CLP_DEPT_CODE"));
		parm.setData("ORDTYPE_CODE", this.getValue("ORDTYPE_CODE"));
		parm.setData("EXE_DEPT_CODE", this.getValue("EXE_DEPT_CODE"));
		parm.setData("CLNCPATH_CODE", clncPathCode);
		parm.setData("R_DEPT_CODE", this.getValue("R_DEPT_CODE"));
		parm.setData("PROGRESS_CODE", this.getValue("PROGRESS_CODE"));
		// parm.setData("MONCAT_CODE", this.getValue("MONCAT_CODE"));
		// parm.setData("VARIANCE_CODE", this.getValue("VARIANCE_CODE"));
		parm.setData("CLP_STATION_CODE", this.getValue("CLP_STATION_CODE"));
		// case_no
		parm.setData("CASE_NO", this.case_no);
		// 设置region
		parm.setData("REGION_CODE", Operator.getRegion());
		// 设置during
		// 检查时程代码
		TTree duringTree = (TTree) this.getComponent("TTree");
		TTreeNode node = duringTree.getSelectionNode();
		if (node != null) {
			String schdCode = node.getID();
			parm.setData("DURATION_CODE", schdCode);
		}
		return parm;
	}

	/**
	 * 得到当前时程
	 * 
	 * @return String
	 */
	private String getCurrentDuration() {
		// 检查时程代码
		String schdCode = "";
		TTree duringTree = (TTree) this.getComponent("TTree");
		TTreeNode node = duringTree.getSelectionNode();
		if (node != null) {
			schdCode = node.getID();
		}
		return schdCode;
	}

	// /**
	// * 初始化病人信息
	// */
	// private void initPatientInfo() {
	// TParm parm = new TParm();
	// parm.setData("CASE_NO", case_no);
	// TParm resultParm = CLPVariationTool.getInstance().selectPatientInfo(
	// parm);
	// //System.out.println("查询出的病人信息resultParm:" + resultParm);
	// if (resultParm.getCount() <= 0) {
	// //System.out.println("初始化病人信息，没有查出对应的病人信息");
	// return;
	// }
	// //赋值
	// TParm data = resultParm.getRow(0);
	// this.setValue("MR_NO", data.getValue("MR_NO"));
	// this.setValue("SEX", data.getValue("SEX"));
	// this.setValue("BED_NO", data.getValue("BED_NO"));
	// this.setValue("VS_DR_CODE", data.getValue("VS_DR_CODE"));
	// this.setValue("MR_NO", data.getValue("MR_NO"));
	// this.setValue("STAYHOSP_DAYS", data.getValue("STAYHOSP_DAYS"));
	// this.setValue("DS_DATE", data.getValue("DS_DATE"));
	// this.setValue("PAT_NAME", data.getValue("PAT_NAME"));
	// this.setValue("CLNCPATH_CODE", data.getValue("CLNCPATH_CODE"));
	// this.setValue("IN_DATE", data.getValue("IN_DATE"));
	// this.setValue("AVERAGECOST", data.getValue("AVERAGECOST"));
	// this.setValue("CASE_NO", data.getValue("CASE_NO"));
	// }

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * 数字验证方法
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validNumber(String validData) {
		Pattern p = Pattern.compile("[0-9]{1,}");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 数字验证方法
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validDouble(String validData) {
		Pattern p = Pattern.compile("[0-9]{1,2}([.][0-9]{1,2}){0,1}");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 将表格的对应单元格设置成可写，其他的设置成不可写
	 * 
	 * @param tableName
	 *            String
	 * @param rowNum
	 *            int
	 * @param columnNum
	 *            int
	 */
	private void setTableEnabled(String tableName, int rowNum, int columnNum) {
		TTable table = (TTable) this.getComponent(tableName);
		int totalColumnMaxLength = table.getColumnCount();
		int totalRowMaxLength = table.getRowCount();
		// System.out.println("列总数：" + totalColumnMaxLength + "行总数:" +
		// totalRowMaxLength);
		// 锁列
		String lockColumnStr = "";
		for (int i = 0; i < totalColumnMaxLength; i++) {
			if (!(i + "").equals(columnNum + "")) {
				lockColumnStr += i + ",";
			}
		}
		lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
		// System.out.println("锁列串：" + lockColumnStr);
		table.setLockColumns(lockColumnStr);
		// 锁行
		String lockRowStr = "";
		for (int i = 0; i < totalRowMaxLength; i++) {
			if (!(i + "").equals(rowNum + "")) {
				lockRowStr += i + ",";
			}
		}
		// System.out.println("锁行串前：" + lockRowStr + "总行" + totalRowMaxLength);
		if (!"".equals(lockRowStr)) {
			lockRowStr = lockRowStr.substring(0,
					((lockRowStr.length() - 1) < 0 ? 0
							: (lockRowStr.length() - 1)));
			// System.out.println("锁行串：" + lockRowStr);
			if (lockRowStr.length() > 0) {
				table.setLockRows(lockRowStr);
			}
		} else {
			table.setLockRows("");
		}

	}

	/**
	 * 将控件值放入TParam方法(可以传入放置参数值)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		objstr = objstr;
		parm.setData(paramName, objstr);
	}

	/**
	 * 将控件值放入TParam方法(放置参数值与控件名相同)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		// System.out.println(objstr);
		objstr = objstr;
		// 参数值与控件名相同
		parm.setData(objName, objstr);
	}

	/**
	 * 将控件值放入TParam方法(可以传入放置参数值)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamLikeWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr + "%";
			parm.setData(paramName, objstr);
		}

	}

	/**
	 * 将控件值放入TParam方法(放置参数值与控件名相同)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamLikeWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr.trim() + "%";
			// 参数值与控件名相同
			parm.setData(objName, objstr);
		}
	}

	/**
	 * 用于放置用于完全匹配进行查询的控件
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm) {
		putParamWithObjNameForQuery(objName, parm, objName);
	}

	/**
	 * 用于放置用于完全匹配进行查询的控件
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 * @param paramName
	 *            String
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			// 参数值与控件名相同
			parm.setData(paramName, objstr.trim());
		}
	}

	/**
	 * 检查控件是否为空
	 * 
	 * @param componentName
	 *            String
	 * @return boolean
	 */
	private boolean checkComponentNullOrEmpty(String componentName) {
		if (componentName == null || "".equals(componentName)) {
			return false;
		}
		String valueStr = this.getValueString(componentName);
		if (valueStr == null || "".equals(valueStr)) {
			return false;
		}
		return true;
	}

	/**
	 * 得到指定table的选中行
	 * 
	 * @param tableName
	 *            String
	 * @return int
	 */
	private int getSelectedRow(String tableName) {
		int selectedIndex = -1;
		if (tableName == null || tableName.length() <= 0) {
			return -1;
		}
		Object componentObj = this.getComponent(tableName);
		if (!(componentObj instanceof TTable)) {
			return -1;
		}
		TTable table = (TTable) componentObj;
		selectedIndex = table.getSelectedRow();
		return selectedIndex;
	}

	/**
	 * 向TParm中加入系统默认信息
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		int total = parm.getCount();
		// System.out.println("total" + total);
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", Operator.getRegion());
	}

	/**
	 * 根据Operator得到map
	 * 
	 * @return Map
	 */
	private Map getBasicOperatorMap() {
		Map map = new HashMap();
		map.put("REGION_CODE", Operator.getRegion());
		map.put("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		map.put("OPT_DATE", datestr);
		map.put("OPT_TERM", Operator.getRegion());
		return map;
	}

	/**
	 * 得到当前时间字符串方法
	 * 
	 * @param dataFormatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateStr(String dataFormatStr) {
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, dataFormatStr);
		return datestr;
	}

	/**
	 * 得到当前时间字符串方法
	 * 
	 * @return String
	 */
	private String getCurrentDateStr() {
		return getCurrentDateStr("yyyyMMdd");
	}

	/**
	 * 拷贝TParm
	 * 
	 * @param from
	 *            TParm
	 * @param to
	 *            TParm
	 * @param row
	 *            int
	 */
	private void cloneTParm(TParm from, TParm to, int row) {
		for (int i = 0; i < from.getNames().length; i++) {
			to.addData(from.getNames()[i], from.getValue(from.getNames()[i],
					row));
		}
	}

	/**
	 * 克隆对象
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm cloneTParm(TParm from) {
		TParm returnTParm = new TParm();
		for (int i = 0; i < from.getNames().length; i++) {
			returnTParm.setData(from.getNames()[i], from.getValue(from
					.getNames()[i]));
		}
		return returnTParm;
	}

	/**
	 * 处理TParm 里的null的方法
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNullVector(TParm parm, String keyStr, Class type) {
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getData(keyStr, i) == null) {
				// System.out.println("处理为空情况");
				TNull tnull = new TNull(type);
				parm.setData(keyStr, i, tnull);
			}
		}
	}

	/**
	 * 处理TParm 里null值方法
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNull(TParm parm, String keyStr, Class type) {
		if (parm.getData(keyStr) == null) {
			// System.out.println("处理为空情况");
			TNull tnull = new TNull(type);
			parm.setData(keyStr, tnull);
		}
	}
	/**
	 * 
	* @Title: onCombineClick
	* @Description: TODO(合并同类项)
	* @author pangben
	* @throws
	 */
	public void onCombineClick(){
		TCheckBox cmbChk = (TCheckBox) this.getComponent("combination");
		TABLEVARIATION.setParmValue(new TParm());
		if (cmbChk.isSelected()) {
			callFunction("UI|IsExeFlag|setEnabled", false);
			callFunction("UI|varianceFlg|setEnabled", false);
			callFunction("UI|rdeptFlg|setEnabled", false);	
			TABLEVARIATION.setHeader("状,50;必执,30,boolean;查核类别,100,CHKTYPE_CODE;类别,60,ORDER_FLG;标准项目,300;标总量,50;单位,45,UNIT;,15;项目,300; 总量,40;单位,70,UNIT");
			TABLEVARIATION.setColumnHorizontalAlignmentData("0,left;1,center;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,right;10,left");
			TABLEVARIATION.setParmMap("STATUS;EXEC_FLG;CHKTYPE_CODE;ORDER_FLG;ORDER_DESC;TOT;DISPENSE_UNIT;FLG;MAIN_ORDER_CODE_DESC;MAINTOT;MAINDISPENSE_UNIT");
		}else{
			callFunction("UI|IsExeFlag|setEnabled", true);
			callFunction("UI|varianceFlg|setEnabled", true);
			callFunction("UI|rdeptFlg|setEnabled", true);
			TABLEVARIATION.setHeader("状,50;必执,30,boolean;执行人,60,CHK_USER_SHOW;查核类别,100,CHKTYPE_CODE;类别,60,ORDER_FLG;标准项目,300;标总量,50;单位,45,UNIT;标准差,60;,15;项目,300;总量,40;单位,70,UNIT;变异,30,boolean;变异类别,150,MONCAT_CODE_SHOW;变异原因,150,VARIANCE_CODE_SHOW;执行,30,boolean;进度状态,150,PROGRESS_CODE; 执行护士,100,USER_SHOW;备注,120; 变异类别,200,MONCAT_CODE_SHOW;变异原因,200,VARIANCE_CODE_SHOW;备注,200;责任,30,boolean; 责任科室,130,R_DEPT_CODE;责任人员,100,CHK_USER");
			TABLEVARIATION.setColumnHorizontalAlignmentData("0,left;1,center;2,left;3,left;4,left;5,left;6,right;7,left;8,right;10,left;11,right;12,left;13,center;14,left;15,left;16,center;17,left;18,left;19,left;20,left;21,left;22,center;23,center;24,left;25,left");
			TABLEVARIATION.setParmMap("STATUS;EXEC_FLG;CHKUSER_CODE;CHKTYPE_CODE;ORDER_FLG;ORDER_DESC;TOT;DISPENSE_UNIT;STANDARD;FLG;MAIN_ORDER_CODE_DESC;MAINTOT;MAINDISPENSE_UNIT;VARIANCEFLG;MEDICAL_MONCAT;MEDICAL_VARIANCE;ISEXE;PROGRESS_CODE;MAINCFM_USER;MEDICAL_NOTE;MANAGE_MONCAT;MANAGE_VARIANCE;MANAGE_NOTE;RDEPTFLG;R_DEPT_CODE;R_USER");
		}
		
	}
}
