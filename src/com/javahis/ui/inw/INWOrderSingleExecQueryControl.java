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
 * 未执行医嘱查询
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
	
	
	/*排序*/
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
	

	public INWOrderSingleExecQueryControl() {

	}

	/**
	 * 初始化
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
		
		//应执行开始时间
		Timestamp now = SystemTool.getInstance().getDate();
		startTime.setValue(now.toString().replace("-", "/").substring(0, 19));
		
		//应执行结束时间
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
		
		
		//表头排序
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
			this.messageBox("无相关数据！");
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
	 * 清空
	 * */
	public void onClear(){
		
		//应执行开始时间
		Timestamp now = SystemTool.getInstance().getDate();
		startTime.setValue(now.toString().replace("-", "/").subSequence(0, 19));
		
		//应执行结束时间
		long time = now.getTime();
		time += 60*60*1000;
		Timestamp nowAddOneHour = new Timestamp(time);
		endTime.setValue(nowAddOneHour.toString().replace("-", "/").subSequence(0, 19));
		
		deptCode.setValue("");
		stationCode.setValue("");
		nurse.setValue("");
		
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
	
}
