package com.javahis.ui.cts;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class CTSTimesControl extends TControl {
	private static TTable table1;
	private static TTable table2;
	
	//表格排序
	private boolean ascending = false;
	private int sortColumn =-1;
	private Compare compare = new Compare();

	  /**
	 * 初始化方法
	 */
	public void onInit() {
		table1 = (TTable) getComponent("TABLE1");
		table2 = (TTable) getComponent("TABLE2");
		
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		
		// 面的table注册CHECK_BOX_CLICKED点击监听事件
		this.callFunction("UI|TABLE1|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTmSelected");
		
	}
	
	public void onTmSelected(Object obj){
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");

		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		table1 = (TTable) obj;
		table1.acceptText();
		TParm parmM = table1.getParmValue();
		String clothNos = "";
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			if(parmM.getBoolean("FLG", i)){
				clothNos += "'" + parmM.getValue("CLOTH_NO", i) + "',";
			}
		}
		if(clothNos.length()>0){
			clothNos = clothNos.substring(0, clothNos.length() - 1);
		}
		TParm parmD = getOutD(clothNos);
		String inWashNO="";
		for(int i=0;i<parmD.getCount();i++){
			if(!parmD.getValue("IN_WASH_NO", i).equals("")){
				inWashNO += "'" + parmD.getValue("IN_WASH_NO", i) + "',";
			}
		}
		if(inWashNO.length()>0){
			inWashNO = inWashNO.substring(0, inWashNO.length() - 1);
			String sql="SELECT WASH_NO IN_WASH_NO,OPT_DATE IN_DATE FROM CTS_WASHD WHERE CLOTH_NO IN("+clothNos+") AND WASH_NO IN ("+inWashNO+")"+
			" AND OPT_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
			 " AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')" +
			 " ORDER BY WASH_NO DESC";
			 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			 for(int i=0;i<parmD.getCount();i++){
				 int flg = -1;
				 for(int j=0;j<result.getCount();j++){
					 if(parmD.getValue("IN_WASH_NO", i).equals(result.getValue("IN_WASH_NO", j))){
						 String inDate=result.getValue("IN_DATE", j);
						 parmD.setData("IN_DATE", i, inDate.substring(0, inDate.length()-2)); 
					 }
				 }				 
			 }
		}

		table2.setParmValue(parmD);
		
	}
	
	private TParm getOutD(String clothNos) {
		
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");

		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		
//		String sql = "SELECT A.IN_WASH_NO, B.OPT_DATE IN_OPT_DATE, A.OPT_DATE OUT_OPT_DATE,B.OUT_WASH_NO" 
//			+" FROM CTS_OUTD A, CTS_WASHD B" 
//			+" WHERE A.IN_WASH_NO = B.WASH_NO AND B.OUT_WASH_NO = A.WASH_NO AND A.CLOTH_NO=B.CLOTH_NO AND A.CLOTH_NO IN("+clothNos+")" 
//			+" AND A.OPT_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
//		    " AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')"
//			+" ORDER BY A.IN_WASH_NO DESC";
		
		String sql = "SELECT WASH_NO,IN_WASH_NO,OPT_DATE OUT_DATE, '' IN_DATE FROM CTS_OUTD" +
					 " WHERE CLOTH_NO IN ("+clothNos+")" +
					 " AND OPT_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
					 " AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')" +
					 " ORDER BY WASH_NO DESC";
			
		
//		System.out.println(sql);
	    TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public void onQuery(){
		table1.removeRowAll();
		table2.removeRowAll();
		
		// 设置查询条件
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		
//		String sql = "SELECT 'N' FLG, A.CLOTH_NO, C.INV_CHN_DESC,B.OWNER_CODE,B.CTS_COST_CENTRE STATION_DESC,B.TURN_POINT,B.ACTIVE_FLG, B.OPT_DATE,COUNT(A.CLOTH_NO) AS TIMES" +
//				" FROM CTS_OUTD A, INV_STOCKDD B, INV_BASE C " +
//				" WHERE A.CLOTH_NO = B.RFID AND B.INV_CODE = C.INV_CODE" +
//				" AND A.OPT_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
//			    " AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')";
		
		String sql ="SELECT 'N' FLG, A.RFID CLOTH_NO, B.INV_CHN_DESC, A.OWNER_CODE, A.CTS_COST_CENTRE STATION_DESC," +
				" A.TURN_POINT,A.ACTIVE_FLG, 0 TIMES" +
				" FROM INV_STOCKDD A, INV_BASE B" +
				" WHERE A.INV_CODE = B.INV_CODE" +
				" AND B.INV_KIND = '08'" +
				" AND A.INV_CODE LIKE 'A2%'" +
				" AND A.WRITE_FLG = 'Y'";
				
				
		  
		if (!getValueString("CLOTH_NO").equals("")) {
			sql += " AND A.RFID = '" + getValueString("CLOTH_NO") + "'";
		}
		
		if (!getValueString("OWNER").equals("")) {
			sql += " AND A.OWNER = '" + getValueString("OWNER") + "'";
		}
		
		if (!getValueString("INV_CODE").equals("")) {
			sql += " AND A.INV_CODE = '" + getValueString("INV_CODE") + "'";
		}
		
		if (!getValueString("OWNER_CODE").equals("")) {
			sql += " AND A.OWNER_CODE = '" + getValueString("OWNER_CODE") + "'";
		}
		
		if (!getValueString("TURN_POINT").equals("")) {
			sql += " AND A.TURN_POINT = '" + getValueString("TURN_POINT") + "'";
		}
		
		sql += " ORDER BY A.RFID";
		
//		sql += " GROUP BY A.CLOTH_NO, C.INV_CHN_DESC,B.OWNER_CODE,B.CTS_COST_CENTRE,B.TURN_POINT,B.ACTIVE_FLG, B.OPT_DATE" +
//			   " ORDER BY C.INV_CHN_DESC,B.TURN_POINT";
//		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		String sql1="SELECT CLOTH_NO, COUNT (CLOTH_NO) AS TIMES FROM CTS_OUTD" +
				" WHERE OPT_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
				" AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')"+
				" GROUP BY CLOTH_NO ORDER BY CLOTH_NO";
//		System.out.println(sql1);
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		for(int i=0;i<result1.getCount();i++){
			String clothNos = result1.getValue("CLOTH_NO", i);
			for(int j=0;j<result.getCount();j++){
				if(result.getValue("CLOTH_NO", j).equals(clothNos)){
					result.setData("TIMES", j, result1.getValue("TIMES",i));
				}
			}
		}
		
		

		table1.setParmValue(result);
		//加入表格排序
		addListener(this.getTTable("TABLE1")); 
	}
	
	public void showOwnerCode(){
		String owner = getValueString("OWNER");
		String sql="SELECT USER_ID, USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+owner+"'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("OWNER_CODE", selParm.getValue("USER_NAME", 0));
	}
	
	public void onClear(){
		table1.removeRowAll();
		table2.removeRowAll();
		clearValue("CLOTH_NO;STATION_CODE;TURN_POINT;INV_CODE;OWNER;OWNER_CODE");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
	}
	
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE1|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "洗衣次数统计单");
	}
	
	public void onPrint(){
		
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "/").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "/").replace(" ", "");
		
		TTable table = this.getTable("TABLE1");
    	if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        TParm data = new TParm();
        data.setData("TITLE", "TEXT", "洗衣次数报表");
        data.setData("DATE", "TEXT", date_s+"~"+date_e);
		TParm tableParm = table1.getShowParmValue();
		TParm parm = new TParm();

		for(int i=0;i<table.getRowCount();i++){
    		parm.addData("CLOTH_NO", tableParm.getValue("CLOTH_NO",i));
    		parm.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC",i));
    		parm.addData("OWNER_CODE", tableParm.getValue("OWNER_CODE",i));
    		parm.addData("STATION_DESC", tableParm.getValue("STATION_DESC",i));
    		parm.addData("TURN_POINT", tableParm.getValue("TURN_POINT",i));
    		parm.addData("TIMES", tableParm.getValue("TIMES",i));
    		parm.addData("ACTIVE_FLG", tableParm.getValue("ACTIVE_FLG",i));
    		
    	}
    	
    
    	parm.setCount(parm.getCount("CLOTH_NO"));
    	parm.addData("SYSTEM", "COLUMNS", "CLOTH_NO");
    	parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "OWNER_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "TURN_POINT");
    	parm.addData("SYSTEM", "COLUMNS", "TIMES");
    	parm.addData("SYSTEM", "COLUMNS", "ACTIVE_FLG");
    	
    	data.setData("TABLE",parm.getData());
    	this.openPrintWindow("%ROOT%\\config\\prt\\cts\\CTSTimesPrint.jhw", data);
		
		
	}
	
	 /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
	
	
	 /**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public  void addListener(final TTable table) {
//		 System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		 TParm tableDate = table.getParmValue();
		 
//		 System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
					int rowCount = table.getRowCount();
//					Map m1=new HashMap();
//					for (int i = 0; i < rowCount; i++) {
//						m1.put(i,new Color(255,255,0));
//					}
//					 table.setRowColorMap(m1);
//				 System.out.println("===排rrrrrrr前===");		
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
//				 System.out.println("+i+"+i);
//				 System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
//				 System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.根据点击的列,对vector排序
//				 System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				
				java.util.Collections.sort(vct, compare);
				cloneVectoryParam(vct, new TParm(), strNames,table);
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
	 * vector转成TParm
	 */
	 void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames,TTable table) {
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
	     * 得到TTable
	     * @param tag String
	     * @return TTable
	     */
	    public TTable getTTable(String tag) {
	        return (TTable)this.getComponent(tag);
	    }
		

}
