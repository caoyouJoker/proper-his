package com.javahis.ui.ope;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.SwingUtilities;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.ui.ope.OPEDRStationControl.DiagList;
import com.javahis.ui.ope.OPEDRStationControl.OpList;
/**
 * 介入护理记录
 * 
 * @author wangqing 20180115
 *
 */
public class OPENursingRecordControl extends TControl {
	/**
	 * 住院就诊号
	 */
	private String caseNo;

	/**
	 * 介入护理记录Table
	 */
	private TTable table;

	/**
	 * 系统参数
	 */
	private TParm sysParm;

	// 表格排序
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;

	private static String TABLE = "TABLE";

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		//		this.messageBox("介入护理记录UI初始化");
		// 获取系统传入参数
		Object o = this.getParameter();
		if(o!=null && o instanceof TParm){
			sysParm = (TParm) o;	
			caseNo = sysParm.getValue("CASE_NO");
			if(caseNo==null || caseNo.trim().length()<=0){
				this.messageBox("系统传入就诊号不能为空");
				return;
			}
		}else{
			this.messageBox("系统参数不能为空");
			return;
		}	
		table = (TTable) this.getComponent("TABLE");
		OpList opList = new OpList();
		DiagList diagList = new DiagList();
		table.addItem("OpList", opList);
		table.addItem("DiagList", diagList);
		// 排序监听
		addListener(table);
		
		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");

		// 执行查询
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				onQuery();
			}
		});		
	}

	/**
	 * 查询
	 */
	public void onQuery(){
		// 清空table数据
		if(table==null){
			this.messageBox("介入护理记录Table不能为空");
			return;
		}
		table.removeRowAll();		
		// 收集查询参数
		String caseNo = this.caseNo;
		if(caseNo==null || caseNo.trim().length()<=0){
			this.messageBox("就诊号不能为空");
			return;
		}
		// 执行查询
		// 0,申请;1,排程完毕;2,接患者;3,手术室交接;4,手术等待;5,手术开始;6,关胸;7,手术结束;8,返回病房
		/*		String sql = "SELECT a.OPBOOK_SEQ, a.CASE_NO, a.MR_NO, a.IPD_NO, b.PAT_NAME, "
				+ "a.OP_DATE, a.ROOM_NO, a.OP_CODE1 OP_CODE, a.DIAG_CODE1 DIAG_CODE, "
				+ "a.MAIN_SURGEON, a.ANA_USER1 ANA_USER, "
				+ "a.URGBLADE_FLG, a.TF_FLG, a.TIME_NEED, a.STATE, a.APROVE_DATE "
				+ "FROM OPE_OPBOOK a, SYS_PATINFO b "
				+ "WHERE a.MR_NO=b.MR_NO(+) AND a.CASE_NO='"+caseNo+"' "
				+ "ORDER BY OPBOOK_SEQ ";


		sql = "SELECT CASE_NO, OPBOOK_SEQ, TIME,HEART_RATE||'次/分钟' HEART_RATE,"
				+ " BREATH||'次/分钟' BREATH,PRESSURE||'mmHg' PRESSURE,OXYGEN_SATURATION||'%' OXYGEN_SATURATION,PAIN_ASSESSMENT,ILLNESS_RECORD,ORDER_DESC"
				+ " ,SEQ_NO FROM OPE_INTERVENNURPLAT WHERE CASE_NO = '"
				+ caseNo
				+ "' AND ORDER_DESC IS NULL "
				+ "ORDER BY CASE_NO, OPBOOK_SEQ, TIME DESC ";*/

		String sql = "SELECT A.CASE_NO, A.OPBOOK_SEQ, A.TIME, A.HEART_RATE||'次/分钟' HEART_RATE, A.BREATH||'次/分钟' BREATH, "
				+ "A.PRESSURE||'mmHg' PRESSURE, A.OXYGEN_SATURATION||'%' OXYGEN_SATURATION, A.PAIN_ASSESSMENT, "
				+ "A.ILLNESS_RECORD, A.ORDER_DESC, A.SEQ_NO, "
				+ "B.OP_CODE1 OP_CODE, B.OPT_CHN_DESC "
				+ "FROM OPE_INTERVENNURPLAT A, "
				+ "(SELECT A.CASE_NO, A.OPBOOK_SEQ, A.OP_CODE1, B.OPT_CHN_DESC FROM OPE_OPBOOK A, SYS_OPERATIONICD B WHERE A.OP_CODE1=B.OPERATION_ICD(+)) B "
				+ "WHERE A.CASE_NO=B.CASE_NO(+) AND A.OPBOOK_SEQ=B.OPBOOK_SEQ(+) "
				+ "AND A.CASE_NO='"+caseNo+"' AND A.ORDER_DESC IS NULL "
				+ "ORDER BY CASE_NO, OPBOOK_SEQ, TIME DESC";


		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return;
		}
		// 为table赋值
		table.setParmValue(result);		
	}

	/**
	 * 诊断CODE替换中文 模糊查询（内部类）
	 */
	public class DiagList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER :
				dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * 手术CODE替换中文 模糊查询（内部类）
	 */
	public class OpList extends TLabel {
		TDataStore dataStore = new TDataStore();
		public OpList(){
			dataStore.setSQL("SELECT * FROM SYS_OPERATIONICD");
			dataStore.retrieve();
		}
		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER :
				dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("OPERATION_ICD");
			Vector d = (Vector) parm.getData("OPT_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
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
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
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
		table.setParmValue(parmTable);
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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * 单击事件
	 * @param row
	 */
	public void onTableClicked(int row) {
		if (row < 0)
			return;
		TParm parm = table.getParmValue().getRow(row);
		this.setValue("ILLNESS_RECORD", parm.getValue("ILLNESS_RECORD"));
	}




}
