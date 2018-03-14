package com.javahis.ui.sta;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatSYSDeptForOprt;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.system.textFormat.TextFormatSYSOperatorStation;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �ż��ﲡ�ֹ�����ͳ�Ʊ���
 * </p>
 * 
 * <p>
 * Description: �ż��ﲡ�ֹ�����ͳ�Ʊ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author lij 2017-3-20
 * @version 1.0
 */
public class STAOut_7Control extends TControl{
	TTable table;

	public STAOut_7Control(){
	}
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * ��ʼ������
	 */
	public void onInit(){
		super.onInit();
		initUI();
		table = (TTable)this.getComponent("Table");
		addListener(getTTable("Table"));
	}
	/**
	 * ��ʼ������
	 */
	public void initUI(){
		this.setValue("S_DATE", SystemTool.getInstance().getDate());// ��������
		this.setValue("E_DATE", SystemTool.getInstance().getDate());
		//		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("ADM_TYPE", "O");
		this.setValue("DEPT_CODE", "");// ���������
		this.setValue("CLINICTYPE_CODE", "");
		this.setValue("CTZ1_CODE", "");
		this.callFunction("UI|Table|removeRowAll");// ��ձ��
	}
	/**
	 * ���
	 */
	public void onClear() {
		initUI();
	}
	/**
	 * ��ѯ
	 * @throws ParseException 
	 */
	public void onQuery() throws ParseException {
		this.callFunction("UI|Table|removeRowAll");// ��ձ��
		// �ж��ż�������
//		if (this.getValueString("ADM_TYPE").equals("")) {
//			this.messageBox("��ѡ���ż�������");
//			return;
//		}
//		String admType = "";// ���Ｖ��ID
//		if (!this.getValue("ADM_TYPE").equals("")) {
//			admType = " AND A.ADM_TYPE = '" + this.getValue("ADM_TYPE") + "'  ";
//		}
		String deptCode = "";// ����ID
		if (!this.getValue("DEPT_CODE").equals("")) {
			deptCode = " AND A.DEPT_CODE = '" + this.getValue("DEPT_CODE")
					+ "'  ";
		}
		String clinictypeCode = "";// �ű�
		if (!this.getValue("CLINICTYPE_CODE").equals("")) {
			clinictypeCode = " AND A.CLINICTYPE_CODE= '" + this.getValue("CLINICTYPE_CODE")
					+ "' ";
		}
		String ctzCode = "";//���ʽ
		if (!this.getValue("CTZ1_CODE").equals("")) {
			clinictypeCode = " AND A.CTZ1_CODE= '" + this.getValue("CTZ1_CODE")
					+ "' ";
		}
		//ͳ������
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		String startDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("S_DATE")), "yyyyMMdd");
		String endDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("E_DATE")), "yyyyMMdd");
		Date startTime=sdf.parse(startDate);
		Date endTime=sdf.parse(endDate);
		long betweenDate = (endTime.getTime() - startTime.getTime())/(1000*60*60*24);
		Calendar sDate = Calendar.getInstance();
		sDate.setTime(startTime); 
//		System.out.println("betweenDate:"+betweenDate);
		String date = "";
		String dateStr1 = "";
		String dateStr2 = "";
		String dateStr3 = "";
		String dateStr4 = "";
		String dateStr5 = "";
		String dateStr6 = "";
		//��ȡͳ�����ڵ�ÿһ��
		for(int i=0;i<=betweenDate;i++){
			date = sdf.format(sDate.getTime());
//			this.messageBox(date);
			//��ͬʱ���ƴ��sql
			dateStr1 = dateStr1 + " A.SEEN_DR_TIME BETWEEN TO_DATE('" + date  
					+ "083000','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ date + "093059','yyyyMMddHH24miss') OR";

			dateStr2 = dateStr2 + " A.SEEN_DR_TIME BETWEEN TO_DATE('" + date  
					+ "093100','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ date + "103059','yyyyMMddHH24miss') OR";

			dateStr3 = dateStr3 + " A.SEEN_DR_TIME BETWEEN TO_DATE('" + date  
					+ "103100','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ date + "113059','yyyyMMddHH24miss') OR";

			dateStr4 = dateStr4 + " A.SEEN_DR_TIME BETWEEN TO_DATE('" + date  
					+ "133000','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ date + "143059','yyyyMMddHH24miss') OR";

			dateStr5 = dateStr5 + " A.SEEN_DR_TIME BETWEEN TO_DATE('" + date  
					+ "143100','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ date + "153059','yyyyMMddHH24miss') OR";

			dateStr6 = dateStr6 + " A.SEEN_DR_TIME BETWEEN TO_DATE('" + date  
					+ "153100','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ date + "170059','yyyyMMddHH24miss') OR";
			sDate.add(Calendar.DATE,1);
		}
		//ȥ�����һ��OR
		dateStr1 = dateStr1.substring(0, dateStr1.length()-2);
		dateStr2 = dateStr2.substring(0, dateStr2.length()-2);
		dateStr3 = dateStr3.substring(0, dateStr3.length()-2);
		dateStr4 = dateStr4.substring(0, dateStr4.length()-2);
		dateStr5 = dateStr5.substring(0, dateStr5.length()-2);
		dateStr6 = dateStr6.substring(0, dateStr6.length()-2);
		//8:30-9:30��
		String sql1 = "SELECT B.ICD_CODE AS ICD_CODE,C.ICD_CHN_DESC,COUNT(A.CASE_NO) "
				+ "FROM REG_PATADM A, OPD_DIAGREC B,SYS_DIAGNOSIS C "
				+ "WHERE A.CASE_NO = B.CASE_NO "
				+ "AND B.ICD_TYPE = C.ICD_TYPE "
				+ "AND B.ICD_CODE = C.ICD_CODE "
				+ "AND B.MAIN_DIAG_FLG = 'Y' "
				+ " AND A.ADM_TYPE = 'O' "
				+ deptCode + clinictypeCode + ctzCode  
				+ " AND (" + dateStr1 +")"
				+ " GROUP BY B.ICD_CODE,ICD_CHN_DESC ORDER BY B.ICD_CODE ";
//		System.out.println("sql1:"+sql1);
		TParm tableParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		System.out.println("tableParm:"+tableParm1);
		//9:31-10:30��
		String sql2 = "SELECT B.ICD_CODE AS ICD_CODE,C.ICD_CHN_DESC,COUNT(A.CASE_NO) "
				+ "FROM REG_PATADM A, OPD_DIAGREC B,SYS_DIAGNOSIS C "
				+ "WHERE A.CASE_NO = B.CASE_NO "
				+ "AND B.ICD_TYPE = C.ICD_TYPE "
				+ "AND B.ICD_CODE = C.ICD_CODE "
				+ "AND B.MAIN_DIAG_FLG = 'Y' "
				+ " AND A.ADM_TYPE = 'O' "
				+ deptCode + clinictypeCode + ctzCode  
				+ " AND (" + dateStr2 +")"
				+ " GROUP BY B.ICD_CODE,ICD_CHN_DESC ORDER BY B.ICD_CODE ";
		System.out.println("sql2:"+sql2);
		TParm tableParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//		System.out.println("tableParm:"+tableParm2);
		//10:31-11:30��
		String sql3 = "SELECT B.ICD_CODE AS ICD_CODE,C.ICD_CHN_DESC,COUNT(A.CASE_NO) "
				+ "FROM REG_PATADM A, OPD_DIAGREC B,SYS_DIAGNOSIS C "
				+ "WHERE A.CASE_NO = B.CASE_NO "
				+ "AND B.ICD_TYPE = C.ICD_TYPE "
				+ "AND B.ICD_CODE = C.ICD_CODE "
				+ "AND B.MAIN_DIAG_FLG = 'Y' "
				+ " AND A.ADM_TYPE = 'O' "
				+ deptCode + clinictypeCode + ctzCode  
				+ " AND (" + dateStr3 +")"
				+ " GROUP BY B.ICD_CODE,ICD_CHN_DESC ORDER BY B.ICD_CODE ";
		//		System.out.println("sql3:"+sql3);
		TParm tableParm3 = new TParm(TJDODBTool.getInstance().select(sql3));
		//13:30-14:30��
		String sql4 = "SELECT B.ICD_CODE AS ICD_CODE,C.ICD_CHN_DESC,COUNT(A.CASE_NO) "
				+ "FROM REG_PATADM A, OPD_DIAGREC B,SYS_DIAGNOSIS C "
				+ "WHERE A.CASE_NO = B.CASE_NO "
				+ "AND B.ICD_TYPE = C.ICD_TYPE "
				+ "AND B.ICD_CODE = C.ICD_CODE "
				+ "AND B.MAIN_DIAG_FLG = 'Y' "
				+ " AND A.ADM_TYPE = 'O' "
				+ deptCode + clinictypeCode + ctzCode  
				+ " AND (" + dateStr4 +")"
				+ " GROUP BY B.ICD_CODE,ICD_CHN_DESC ORDER BY B.ICD_CODE ";
		//		System.out.println("sql4:"+sql4);
		TParm tableParm4 = new TParm(TJDODBTool.getInstance().select(sql4));
		//14:31-15:30��
		String sql5 = "SELECT B.ICD_CODE AS ICD_CODE,C.ICD_CHN_DESC,COUNT(A.CASE_NO) "
				+ "FROM REG_PATADM A, OPD_DIAGREC B,SYS_DIAGNOSIS C "
				+ "WHERE A.CASE_NO = B.CASE_NO "
				+ "AND B.ICD_TYPE = C.ICD_TYPE "
				+ "AND B.ICD_CODE = C.ICD_CODE "
				+ "AND B.MAIN_DIAG_FLG = 'Y' "
				+ " AND A.ADM_TYPE = 'O' "
				+ deptCode + clinictypeCode + ctzCode  
				+ " AND (" + dateStr5 +")"
				+ " GROUP BY B.ICD_CODE,ICD_CHN_DESC ORDER BY B.ICD_CODE ";
		//System.out.println("sql5:"+sql5);
		TParm tableParm5 = new TParm(TJDODBTool.getInstance().select(sql5));
		//15:31-17:00��
		String sql6 = "SELECT B.ICD_CODE AS ICD_CODE,C.ICD_CHN_DESC,COUNT(A.CASE_NO) "
				+ "FROM REG_PATADM A, OPD_DIAGREC B,SYS_DIAGNOSIS C "
				+ "WHERE A.CASE_NO = B.CASE_NO "
				+ "AND B.ICD_TYPE = C.ICD_TYPE "
				+ "AND B.ICD_CODE = C.ICD_CODE "
				+ "AND B.MAIN_DIAG_FLG = 'Y' "
				+ " AND A.ADM_TYPE = 'O' "
				+ deptCode + clinictypeCode + ctzCode  
				+ " AND (" + dateStr6 +")"
				+ " GROUP BY B.ICD_CODE,ICD_CHN_DESC ORDER BY B.ICD_CODE ";
//		System.out.println("sql6:"+sql6);
		TParm tableParm6 = new TParm(TJDODBTool.getInstance().select(sql6));
		// ��ѯ���е������
		String sql = " SELECT DISTINCT C.ICD_CODE AS ICDS_CODE,C.ICD_CHN_DESC AS ICD_DESC FROM REG_PATADM A,OPD_DIAGREC B,SYS_DIAGNOSIS C "
				+ " WHERE B.ICD_CODE=C.ICD_CODE AND A.CASE_NO = B.CASE_NO AND B.MAIN_DIAG_FLG = 'Y' AND A.ADM_TYPE = 'O' "
				+ " AND A.SEEN_DR_TIME BETWEEN TO_DATE('" + startDate  
				+ "000000','yyyyMMddHH24miss') " + " AND TO_DATE(' "
				+ endDate + "235959','yyyyMMddHH24miss')" + deptCode + clinictypeCode + ctzCode;
//		System.out.println("sql1111111:"+sql); 
		TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));		
		TParm parmValue = new TParm();
		int amount1 = 0;
		int amount2 = 0;
		int amount3 = 0;
		int amount4 = 0;
		int amount5 = 0;
		int amount6 = 0;
		int amounts = 0;
		for(int i = 0;i<tableParm.getCount();i++){
			String icdCode = tableParm.getValue("ICDS_CODE",i);
			parmValue.addData("ICD_CHN_DESC",tableParm.getValue("ICD_DESC",i));

			//8:30-9:30��
			int amount = 0;
			if(tableParm1.getValue("ICD_CODE").contains(icdCode)){
				for(int j = 0 ;j<tableParm1.getCount();j++){
					if(tableParm1.getValue("ICD_CODE",j).equals(icdCode)){
						parmValue.addData("AMOUNT1",tableParm1.getValue("COUNT(A.CASE_NO)",j));
						amount += tableParm1.getDouble("COUNT(A.CASE_NO)",j);
						amount1 += tableParm1.getDouble("COUNT(A.CASE_NO)",j);;
					}else{
						continue;
					}
				}
			}else{
				parmValue.addData("AMOUNT1",0);
				amount += 0;
				amount1 += 0;
			}

			//9:31-10:30��
			if(tableParm2.getValue("ICD_CODE").contains(icdCode)){
				for(int j = 0 ;j<tableParm2.getCount();j++){
					if(tableParm2.getValue("ICD_CODE",j).equals(icdCode)){
						parmValue.addData("AMOUNT2",tableParm2.getValue("COUNT(A.CASE_NO)",j));
						amount += tableParm2.getDouble("COUNT(A.CASE_NO)",j);
						amount2 += tableParm2.getDouble("COUNT(A.CASE_NO)",j);;
					}else{
						continue;
					}
				}
			}else{
				parmValue.addData("AMOUNT2",0);
				amount += 0;
				amount2 += 0;
			}

			//10:31-11:30��

			if(tableParm3.getValue("ICD_CODE").contains(icdCode)){
				for(int j = 0 ;j<tableParm3.getCount();j++){
					if(tableParm3.getValue("ICD_CODE",j).equals(icdCode)){
						parmValue.addData("AMOUNT3",tableParm3.getValue("COUNT(A.CASE_NO)",j));
						amount += tableParm3.getDouble("COUNT(A.CASE_NO)",j);
						amount3 += tableParm3.getDouble("COUNT(A.CASE_NO)",j);;
					}else{
						continue;
					}
				}
			}else{
				parmValue.addData("AMOUNT3",0);
				amount += 0;
				amount3 += 0;
			}
			//13:30-14:30��

			if(tableParm4.getValue("ICD_CODE").contains(icdCode)){
				for(int j = 0 ;j<tableParm4.getCount();j++){
					if(tableParm4.getValue("ICD_CODE",j).equals(icdCode)){
						parmValue.addData("AMOUNT4",tableParm4.getValue("COUNT(A.CASE_NO)",j));
						amount += tableParm4.getDouble("COUNT(A.CASE_NO)",j);
						amount4 += tableParm4.getDouble("COUNT(A.CASE_NO)",j);;
					}else{
						continue;
					}
				}
			}else{
				parmValue.addData("AMOUNT4",0);
				amount += 0;
				amount4 += 0;
			}

			//14:31-15:30��
			if(tableParm5.getValue("ICD_CODE").contains(icdCode)){
				for(int j = 0 ;j<tableParm5.getCount();j++){
					if(tableParm5.getValue("ICD_CODE",j).equals(icdCode)){
						parmValue.addData("AMOUNT5",tableParm5.getValue("COUNT(A.CASE_NO)",j));
						amount += tableParm5.getDouble("COUNT(A.CASE_NO)",j);
						amount5 += tableParm5.getDouble("COUNT(A.CASE_NO)",j);;
					}else{
						continue;
					}
				}
			}else{
				parmValue.addData("AMOUNT5",0);
				amount += 0;
				amount5 += 0;
			}

			//15:31-17:00��
			if(tableParm6.getValue("ICD_CODE").contains(icdCode)){
				for(int j = 0 ;j<tableParm6.getCount();j++){
					if(tableParm6.getValue("ICD_CODE",j).equals(icdCode)){
						parmValue.addData("AMOUNT6",tableParm6.getValue("COUNT(A.CASE_NO)",j));
						amount += tableParm6.getDouble("COUNT(A.CASE_NO)",j);
						amount6 += tableParm6.getDouble("COUNT(A.CASE_NO)",j);;
					}else{
						continue;
					}
				}
			}else{
				parmValue.addData("AMOUNT6",0);
				amount += 0;
				amount6 += 0;
			}
			parmValue.addData("AMOUNT",amount);
			amounts += amount;

		}
		parmValue.addData("ICD_CHN_DESC", "�ϼ�");
		parmValue.addData("AMOUNT1", amount1);
		parmValue.addData("AMOUNT2", amount2);
		parmValue.addData("AMOUNT3", amount3);
		parmValue.addData("AMOUNT4", amount4);
		parmValue.addData("AMOUNT5", amount5);
		parmValue.addData("AMOUNT6", amount6);
		parmValue.addData("AMOUNT", amounts);
		if(amounts<=0){
			this.messageBox("��������");
		}
		table.setParmValue(parmValue);				
		if (table.getRowCount() < 1) {
			// �������� 
			this.messageBox("��������");
		}
	}
	/**
	 * ���Excel
	 */
	public void onExport() {
		TTable table = (TTable) callFunction("UI|Table|getThis");
		TParm parm = table.getShowParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ���������");
			return;
		}
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "�ż��ﲡ�ֹ�����ͳ�Ʊ���");
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
				TParm tableData = getTTable("Table").getParmValue();
				System.out.println("$$$$$$$$:"+tableData);
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
				String tblColumnName = getTTable("Table")
						.getParmMap(sortColumn);
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
		getTTable("Table").setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

}
