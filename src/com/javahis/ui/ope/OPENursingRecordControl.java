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
 * ���뻤���¼
 * 
 * @author wangqing 20180115
 *
 */
public class OPENursingRecordControl extends TControl {
	/**
	 * סԺ�����
	 */
	private String caseNo;

	/**
	 * ���뻤���¼Table
	 */
	private TTable table;

	/**
	 * ϵͳ����
	 */
	private TParm sysParm;

	// �������
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;

	private static String TABLE = "TABLE";

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		//		this.messageBox("���뻤���¼UI��ʼ��");
		// ��ȡϵͳ�������
		Object o = this.getParameter();
		if(o!=null && o instanceof TParm){
			sysParm = (TParm) o;	
			caseNo = sysParm.getValue("CASE_NO");
			if(caseNo==null || caseNo.trim().length()<=0){
				this.messageBox("ϵͳ�������Ų���Ϊ��");
				return;
			}
		}else{
			this.messageBox("ϵͳ��������Ϊ��");
			return;
		}	
		table = (TTable) this.getComponent("TABLE");
		OpList opList = new OpList();
		DiagList diagList = new DiagList();
		table.addItem("OpList", opList);
		table.addItem("DiagList", diagList);
		// �������
		addListener(table);
		
		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");

		// ִ�в�ѯ
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				onQuery();
			}
		});		
	}

	/**
	 * ��ѯ
	 */
	public void onQuery(){
		// ���table����
		if(table==null){
			this.messageBox("���뻤���¼Table����Ϊ��");
			return;
		}
		table.removeRowAll();		
		// �ռ���ѯ����
		String caseNo = this.caseNo;
		if(caseNo==null || caseNo.trim().length()<=0){
			this.messageBox("����Ų���Ϊ��");
			return;
		}
		// ִ�в�ѯ
		// 0,����;1,�ų����;2,�ӻ���;3,�����ҽ���;4,�����ȴ�;5,������ʼ;6,����;7,��������;8,���ز���
		/*		String sql = "SELECT a.OPBOOK_SEQ, a.CASE_NO, a.MR_NO, a.IPD_NO, b.PAT_NAME, "
				+ "a.OP_DATE, a.ROOM_NO, a.OP_CODE1 OP_CODE, a.DIAG_CODE1 DIAG_CODE, "
				+ "a.MAIN_SURGEON, a.ANA_USER1 ANA_USER, "
				+ "a.URGBLADE_FLG, a.TF_FLG, a.TIME_NEED, a.STATE, a.APROVE_DATE "
				+ "FROM OPE_OPBOOK a, SYS_PATINFO b "
				+ "WHERE a.MR_NO=b.MR_NO(+) AND a.CASE_NO='"+caseNo+"' "
				+ "ORDER BY OPBOOK_SEQ ";


		sql = "SELECT CASE_NO, OPBOOK_SEQ, TIME,HEART_RATE||'��/����' HEART_RATE,"
				+ " BREATH||'��/����' BREATH,PRESSURE||'mmHg' PRESSURE,OXYGEN_SATURATION||'%' OXYGEN_SATURATION,PAIN_ASSESSMENT,ILLNESS_RECORD,ORDER_DESC"
				+ " ,SEQ_NO FROM OPE_INTERVENNURPLAT WHERE CASE_NO = '"
				+ caseNo
				+ "' AND ORDER_DESC IS NULL "
				+ "ORDER BY CASE_NO, OPBOOK_SEQ, TIME DESC ";*/

		String sql = "SELECT A.CASE_NO, A.OPBOOK_SEQ, A.TIME, A.HEART_RATE||'��/����' HEART_RATE, A.BREATH||'��/����' BREATH, "
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
		// Ϊtable��ֵ
		table.setParmValue(result);		
	}

	/**
	 * ���CODE�滻���� ģ����ѯ���ڲ��ࣩ
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
	 * ����CODE�滻���� ģ����ѯ���ڲ��ࣩ
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
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
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
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * �����¼�
	 * @param row
	 */
	public void onTableClicked(int row) {
		if (row < 0)
			return;
		TParm parm = table.getParmValue().getRow(row);
		this.setValue("ILLNESS_RECORD", parm.getValue("ILLNESS_RECORD"));
	}




}
