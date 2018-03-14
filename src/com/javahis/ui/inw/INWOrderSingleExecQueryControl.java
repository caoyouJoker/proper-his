package com.javahis.ui.inw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.OdiUtil;
import com.tiis.ui.TiLabel;
import com.tiis.ui.TiMultiPanel;
import com.tiis.ui.TiPanel;
import com.tiis.util.TiString;

/**
 * 
 * δִ��ҽ����ѯ
 * 
 * @author yangjj 
 * add by yangjj 20151022
 * 
 */
public class INWOrderSingleExecQueryControl extends TControl  {
	TTextFormat startTime;
	TTextFormat endTime;
	TTextFormat deptCode;
	TTextFormat stationCode;
	TTextFormat nurse;
	TTextField mrNo;
	
	TParm para=null;
	String status = "";
	
	
	/*����*/
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
	

	public INWOrderSingleExecQueryControl() {

	}

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		para = (TParm) this.getParameter();
		status = para.getValue("STATUS");
		initUI();
	}
	
	public void initUI(){
		startTime = (TTextFormat) this.getComponent("START_TIME");
		endTime = (TTextFormat) this.getComponent("END_TIME");
		deptCode = (TTextFormat) this.getComponent("DEPT_CODE");
		stationCode = (TTextFormat) this.getComponent("STATION_CODE");
		nurse = (TTextFormat) this.getComponent("NURSE");
		mrNo = (TTextField) this.getComponent("MR_NO");
		
		//Ӧִ�п�ʼʱ��
		Timestamp now = SystemTool.getInstance().getDate();
		startTime.setValue(now.toString().replace("-", "/").substring(0, 19));
		
		//Ӧִ�н���ʱ��
		long time = now.getTime();
		time += 60*60*1000;
		Timestamp nowAddOneHour = new Timestamp(time);
		endTime.setValue(nowAddOneHour.toString().replace("-", "/").substring(0, 19));
		
		deptCode.setValue(Operator.getDept());
		stationCode.setValue(Operator.getStation());
		
		if("SINGLE".equals(status)){
			nurse.setValue(Operator.getID());
		}else{
			nurse.setValue("");
		}
		
		
		//��ͷ����
		addListener((TTable)this.getComponent("TABLE"));
		onQuery();
		
	}


	public void onQuery(){
		
		String start = "";
		if(startTime.getValue().toString().length()>1){
			start = startTime.getValue().toString().replace("/", "-").substring(0, 19);
		}
		
		String end = "";
		if(endTime.getValue().toString().length()>1){
			end = endTime.getValue().toString().replace("/", "-").substring(0, 19);
		}
			
		String dept = deptCode.getValue().toString();
		String station = stationCode.getValue().toString();
		String mr = mrNo.getValue().toString();
		String n = nurse.getValue().toString();
		
		String sql = "";
		sql += " SELECT " +
					" DISTINCT " +
					" A.BED_NO , " +
					" P.PAT_NAME , " +
					" P.MR_NO , " + 
					" P.SEX_CODE , " +
					" P.BIRTH_DATE , " + 
					" M.MEDI_QTY , " + 
					" M.MEDI_UNIT , " +
					" M.ORDER_DESC , " +
					" M.DR_NOTE , " + 
					" (TO_DATE(substr(D.ORDER_DATE || D.ORDER_DATETIME,1,12), 'yyyyMMddhh24mi')) AS NS_EXEC_DATE " + 
				" FROM " +
					" ADM_INP A , " + 
					" SYS_PATINFO P , " +
					" ODI_DSPND D, " +
					" ODI_DSPNM M, " +
					" SYS_BED B " +
				" WHERE" +
					" A.MR_NO = P.MR_NO " +
					" AND A.CASE_NO = D.CASE_NO " + 
					" AND A.CASE_NO = M.CASE_NO " +
					" AND D.ORDER_NO = M.ORDER_NO " + 
					" AND D.ORDER_SEQ = M.ORDER_SEQ " +
					" AND D.CASE_NO = M.CASE_NO " + 
					" AND M.CAT1_TYPE = 'PHA' " + 
					" AND D.NS_EXEC_DATE_REAL IS NULL " +
					" AND M.DSPN_KIND IN ('ST','F','UD')" +
					" AND A.DS_DATE IS NULL " +
			  		" AND A.CASE_NO = B.CASE_NO " + 
	                " AND A.BED_NO = B.BED_NO " +
			  		" AND B.CASE_NO IS NOT NULL ";
		
		if(!"".equals(start)){
			sql += " AND TO_DATE (substr(D.ORDER_DATE || D.ORDER_DATETIME,1,12), 'yyyyMMddhh24mi') > to_date('"+start+"','yyyy-mm-dd hh24:mi:ss') " ;
		}
		
		if(!"".equals(end)){
			sql += " AND TO_DATE (substr(D.ORDER_DATE || D.ORDER_DATETIME,1,12), 'yyyyMMddhh24mi') < to_date('"+end+"','yyyy-mm-dd hh24:mi:ss') " ;
		}
		
		if(!"".equals(dept)){
			sql += " AND A.DEPT_CODE = '"+dept+"' " ;
		}
		
		if(!"".equals(station)){
			sql += " AND A.STATION_CODE = '"+station+"' " ;
		}
		
		if(!"".equals(n)){
			sql += " AND A.VS_NURSE_CODE = '"+n+"' " ;
		}
			
		if(!"".equals(mr)){
			sql += " AND A.MR_NO = '"+mr+"' ";
		}
				
		//System.out.println("yangjj sql:"+sql);
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
		if(p.getCount() == 0){
			this.messageBox("��������ݣ�");
			return ;
		}
		
		
		Timestamp sysDate = SystemTool.getInstance().getDate();
		for(int i = 0 ; i < p.getCount() ; i++){
			String age = "0";
			
			age = OdiUtil.getInstance().showAge(
					p.getTimestamp("BIRTH_DATE", i),sysDate);
			
			p.setData("AGE", i, age);
		}
		
		((TTable)this.getComponent("TABLE")).setParmValue(p);
					
	}
	
	/*
	 * ���
	 * */
	public void onClear(){
		
		//Ӧִ�п�ʼʱ��
		Timestamp now = SystemTool.getInstance().getDate();
		startTime.setValue(now.toString().replace("-", "/").subSequence(0, 19));
		
		//Ӧִ�н���ʱ��
		long time = now.getTime();
		time += 60*60*1000;
		Timestamp nowAddOneHour = new Timestamp(time);
		endTime.setValue(nowAddOneHour.toString().replace("-", "/").subSequence(0, 19));
		
		deptCode.setValue("");
		stationCode.setValue("");
		nurse.setValue("");
		
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
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
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
				 //System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

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
		((TTable)this.getComponent("TABLE")).setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

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
