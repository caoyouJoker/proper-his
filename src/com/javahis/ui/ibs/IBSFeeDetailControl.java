package com.javahis.ui.ibs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
/**
 * ���������ϸ��ѯ
 * @author wangqing 20171226
 *
 */
public class IBSFeeDetailControl extends TControl {
	/**
	 * ������ϸtable
	 */
	private TTable feeDetailTable;
	
	// �������
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		feeDetailTable = (TTable)this.getComponent("feeDetailTable");
		this.setValue("EXE_DEPT_CODE", "0306");// ִ�п���Ĭ��Ϊ������������	
		Timestamp today = SystemTool.getInstance().getDate();
		String sDateStr = StringTool.getString(today, "yyyy/MM/dd");
		String sTimeStr = "00:00:00";
		String eTimeStr = StringTool.getString(today, "HH:mm:ss");
		Timestamp sDate = StringTool.getTimestamp(sDateStr +" "+sTimeStr,
				"yyyy/MM/dd HH:mm:ss");
		Timestamp eDate = StringTool.getTimestamp(sDateStr +" "+ eTimeStr,
				"yyyy/MM/dd HH:mm:ss");
		this.setValue("START_BILL_DATE", sDate);// ��ʼ����
		this.setValue("END_BILL_DATE", eDate);// ��������
//		this.setValue("START_BILL_DATE", StringTool.rollDate(sDate, -1));
		
		// �������
		addListener(feeDetailTable);
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		feeDetailTable.removeRowAll();
		String startBillDate = getValueString("START_BILL_DATE");
		String endBillDate = getValueString("END_BILL_DATE");
		// У���ѯ����
		if (startBillDate!=null && startBillDate.trim().length()>0
				&& endBillDate!=null && endBillDate.trim().length()>0) {
			if(getValueString("END_BILL_DATE").compareTo(getValueString("START_BILL_DATE")) > 0){
				
			}else{
				this.messageBox("����ʱ�䲻�ܴ��ڿ�ʼʱ��");
				return;
			}
		}else{
			this.messageBox("��ѯʱ���������");
			return;
		}
		startBillDate = StringTool.getString((Timestamp)this.getValue("START_BILL_DATE"), "yyyy/MM/dd HH:mm:ss");
		endBillDate = StringTool.getString((Timestamp)this.getValue("END_BILL_DATE"), "yyyy/MM/dd HH:mm:ss");
		
//		System.out.println("{startBillDate:"+startBillDate+"}");
//		System.out.println("{endBillDate:"+endBillDate+"}");
		
		String sql = "SELECT A.BILL_DATE,A.ORDER_CODE,A.DOSAGE_UNIT, "
				+ "B.ORDER_DESC||CASE WHEN B.SPECIFICATION IS NOT NULL  THEN '('||B.SPECIFICATION||')' ELSE '' END AS ORDER_DESC, "
				+ "A.OWN_FLG,A.DOSAGE_QTY,A.OWN_PRICE, A.TOT_AMT,A.OPT_DATE,C.USER_NAME,A.CASE_NO,A.CASE_NO_SEQ,A.SEQ_NO,"
				+ "A.ORDERSET_CODE,ORDERSET_GROUP_NO, A.INDV_FLG, D.COST_CENTER_ABS_DESC "
				
				+", E.IPD_NO, E.MR_NO, E.PAT_NAME "// add by wangqing 20171226
				
				
				+ "FROM IBS_ORDD A,SYS_FEE B,SYS_OPERATOR C,SYS_COST_CENTER D "
				
				+ ", (SELECT A.CASE_NO, A.MR_NO, A.IPD_NO, B.PAT_NAME FROM ADM_INP A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO(+)) E "// add by wangqing 20171226
				
				+ "WHERE A.ORDER_CODE =B.ORDER_CODE(+) AND A.OPT_USER = C.USER_ID(+) AND A.EXE_DEPT_CODE = D.COST_CENTER_CODE(+) "
				
				+ " AND A.CASE_NO=E.CASE_NO(+)"
				
				// add by wangqing 20180109 ɸѡҩ��
				+ " AND A.CAT1_TYPE='PHA' "
				
				+ " @ "
				
				+ "ORDER BY A.BILL_DATE,A.CASE_NO,A.CASE_NO_SEQ,A.SEQ_NO";
		String sql1 = "";	
		sql1 += " AND A.BILL_DATE BETWEEN TO_DATE('"+startBillDate+"', 'yyyy/MM/dd HH24:MI:SS') AND TO_DATE('"+endBillDate+"', 'yyyy/MM/dd HH24:MI:SS') ";
		String exeDeptCode = this.getValueString("EXE_DEPT_CODE");
		if(exeDeptCode!=null && exeDeptCode.trim().length()>0){
			sql1 += " AND A.EXE_DEPT_CODE='"+exeDeptCode+"' ";
		}
		String mrNo = this.getValueString("MR_NO");
		if(mrNo!=null && mrNo.trim().length()>0){
			sql1 += " AND E.MR_NO='"+mrNo+"' ";
		}
//		System.out.println("{sql1:"+sql1+"}");
		sql = sql.replace("@", sql1);
		System.out.println("{sql:"+sql+"}");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("bug:::��ѯ������ϸ����");
			return;
		}
		feeDetailTable.setParmValue(result);
			
	}

	/**
	 * �����Żس�
	 */
	public void onMrNo(){
		// �ϲ�������
		if(getValueString("MR_NO").length() > 0){
			// ����
			String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			setValue("MR_NO", mrNo);
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));				
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
		}
		this.onQuery();
	}
	
	/**
	 * �һ�MENU�����¼�
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("feeDetailTable");
		table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,openRigthPopMenu");
	}

	/**
	 * �򿪼���ҽ��ϸ���ѯ
	 */
	public void openRigthPopMenu() {
		TTable table = (TTable) this.getComponent("feeDetailTable");
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getValue("CASE_NO", row);
		String orderCode = tableParm.getValue("ORDER_CODE", row);
		String orderSetCode = tableParm.getValue("ORDERSET_CODE", row);
		String caseNoSeq = tableParm.getValue("CASE_NO_SEQ", row);
		// У���Ƿ��Ǽ���ҽ��
		if(tableParm.getValue("ORDERSET_GROUP_NO", row)==null 
				|| tableParm.getValue("ORDERSET_GROUP_NO", row).trim().length()<=0){
			return;
		}
		int orderSetGroupNo = Integer.parseInt(tableParm.getValue(
				"ORDERSET_GROUP_NO", row));
		TParm parm = getOrderSetDetails(orderSetGroupNo, orderSetCode, caseNo,
				caseNoSeq, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}
	
	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @param groupNo
	 *            int
	 * @param orderSetCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode,
			String caseNo, String caseNoSeq, String orderCode) {

		TParm result = new TParm();
		if (groupNo < 0) {
			System.out
					.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			System.out
					.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		String sql = "SELECT * FROM IBS_ORDD WHERE CASE_NO='" + caseNo
				+ "' AND CASE_NO_SEQ='" + caseNoSeq
				+ "' AND ORDERSET_GROUP_NO=" + groupNo + "";

		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = parm.getCount();
		if (count < 0) {
			//System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// temperrϸ��۸�
		for (int i = 0; i < count; i++) {
			if (!orderCode.equals(parm.getValue("ORDER_CODE", i))
					&& orderSetCode.equals(parm.getValue("ORDERSET_CODE", i))) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// ��ѯ����
				TParm orderParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE,ORDER_DESC,SPECIFICATION,OPTITEM_CODE,INSPAY_TYPE "
								+ "FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				result
						.addData("OWN_PRICE", orderParm.getDouble("OWN_PRICE",
								0));
				// modify by wangbin 20141104 �ⲿ���� #326 סԺ�Ƽ۵���ʾ����ҽ��ϸ����� START
				// �ܼ�ֱ��ȡIBS_ORDD��ķ������(TOT_AMT)
				result.addData("OWN_AMT", parm.getValue("TOT_AMT", i));
				// modify by wangbin 20141104 �ⲿ���� #326 סԺ�Ƽ۵���ʾ����ҽ��ϸ����� END
				result.addData("ORDER_DESC", orderParm
						.getValue("ORDER_DESC", 0));
				result.addData("SPECIFICATION", orderParm.getValue(
						"SPECIFICATION", 0));
				result.addData("EXEC_DEPT_CODE", parm.getValue("EXE_DEPT_CODE",
						i));
				result.addData("OPTITEM_CODE", orderParm.getValue(
						"OPTITEM_CODE", 0));
				result.addData("INSPAY_TYPE", orderParm.getValue("INSPAY_TYPE",
						0));
			}
		}
		return result;
	}

	/**
	 * ���Excel
	 */
	public void onExport() {//add by wanglong 20130819
		if (feeDetailTable.getRowCount()<1) {
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(feeDetailTable, "���������ϸ");
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
				String tblColumnName = feeDetailTable.getParmMap(sortColumn);
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
		feeDetailTable.setParmValue(parmTable);
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

	
}
