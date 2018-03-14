package com.javahis.ui.reg;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * <p>Title: 医生预约挂号查询</p>
 *
 * <p>Description:医生预约挂号查询 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author huangtt 20131104
 * @version 1.0
 */

public class REGTomorrowOrderControl extends TControl{
	
	//表格排序
	private boolean ascending = false;
	private int sortColumn =-1;
	private Compare compare = new Compare();
	
	private static TTable table; 
	private static TCheckBox telAll;
	
	public void onInit(){
		
		//初始化REGION登陆默认登录区域
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE"))); 
        
        TComboBox admType = (TComboBox)this.getComponent("ADM_TYPE");
        admType.setValue("O");
        
        callFunction("UI|DEPT_CODE|onQuery");
        table = (TTable) getComponent("table");        
        Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				StringTool.rollDate(date, +1).toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", StringTool.rollDate(date, +1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		
		telAll = (TCheckBox) getComponent("TEL_ALL");
		
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onSelect");
	
     
	}
	
	public void onQuery(){
		table.removeRowAll();
		this.setValue("SUM", "");
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		
		String sql="SELECT 'N' FLG,A.REGION_CODE, A.ADM_TYPE, B.MR_NO, B.PAT_NAME, B.TEL_HOME, A.ADM_DATE, A.REG_DATE," +
				" A.SESSION_CODE, A.CLINICROOM_NO, A.DEPT_CODE, A.DR_CODE, A.REALDEPT_CODE," +
				" A.REALDR_CODE, A.CLINICTYPE_CODE, A.QUE_NO, C.USER_NAME, A.OPT_DATE" +
				" FROM REG_PATADM A, SYS_PATINFO B, SYS_OPERATOR C" +
				" WHERE A.MR_NO=B.MR_NO  AND A.APPT_CODE='Y' AND A.REGCAN_USER IS NULL AND A.REGCAN_DATE IS NULL" +
				" AND A.ADM_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
			    " AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')"+
			    " AND A.OPT_USER=C.USER_ID(+)";
		
//		if (!getValueString("ADM_TYPE").equals("")) {
			sql += " AND A.ADM_TYPE = 'O'";
//		}
		if (!getValueString("SESSION_CODE").equals("")) {
			sql += " AND A.SESSION_CODE = '" + getValueString("SESSION_CODE") + "'";
		}
		if (!getValueString("CLINICROOM_NO").equals("")) {
			sql += " AND A.CLINICROOM_NO = '" + getValueString("CLINICROOM_NO") + "'";
		}
		if (!getValueString("DEPT_CODE").equals("")) {
			sql += " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		}
		if (!getValueString("DR_CODE").equals("")) {
			sql += " AND A.DR_CODE = '" + getValueString("DR_CODE") + "'";
		}
		sql += " ORDER BY A.REG_DATE";
//		System.out.println(sql);
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<0){
			this.messageBox("没有要查询的数据");
			return;
		}
		table.setParmValue(result);
		this.setValue("SUM", result.getCount()+"");
		//加入表格排序
		addListener(this.getTable("TABLE")); 
	}
	/**
	 * 判断是否为手机号
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	public void onTelAll(){
		table.acceptText();
		String flg= "N";
		if(telAll.isSelected()){
			flg = "Y";
		}else{
			flg = "N";
		}
		TParm tableParm = table.getParmValue();
		int count =0;
		for(int i=0;i<tableParm.getCount("FLG");i++ ){
			String tel=tableParm.getValue("TEL_HOME", i);
			if(this.isMobileNO(tel)){
				tableParm.setData("FLG", i, flg);
			}
		}
		table.setParmValue(tableParm);
		for(int i=0;i<tableParm.getCount("FLG");i++){
			if(tableParm.getBoolean("FLG", i)){
				count++;
			}
		}
		if(telAll.isSelected()){
			this.setValue("TELNUM", count+"");
		}else{
			this.setValue("TELNUM", "");
		}
		
	}
	
	
	
	public void onSelect(Object obj){
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableP = table.getParmValue();
		int count = 0;
		for(int i=0;i<tableP.getCount("FLG");i++){
			if(tableP.getBoolean("FLG", i)){
				count++;
			}
		}
		this.setValue("TELNUM", count+"");
	}
	
	
	public void onMessage(){
		table.acceptText();
		TParm tableParm = table.getShowParmValue();
		TParm parm = new TParm();
		for(int i=0;i<tableParm.getCount("MR_NO");i++){
			if(tableParm.getBoolean("FLG", i)){
				parm.addData("MrNo", tableParm.getValue("MR_NO", i));
				parm.addData("Name", tableParm.getValue("PAT_NAME", i));
				 String content = "您预约了泰心医院"+
				 	tableParm.getValue("ADM_DATE", i)+" "+
				 	tableParm.getValue("SESSION_CODE", i) +
					"第"+tableParm.getValue("QUE_NO", i)+"号"+
					tableParm.getValue("DR_CODE", i) +"医生的门诊，仅限"+tableParm.getValue("PAT_NAME", i)+"本人，请勿爽约，如需取消，请提前一天拨打服务电话4001568568，为了保证您准时就诊，您需提前办理挂号手续";
				 parm.addData("Content", content);
				 parm.addData("TEL1", tableParm.getValue("TEL_HOME", i));
			}
			
		}
		TParm  r = TIOM_AppServer.executeAction(
					"action.reg.REGAction", "orderMessage", parm);
		 this.messageBox("发送成功！");
	}
	
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "明日预定就诊查明细");
	}
	
	public void onPrint(){
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "/").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "/").replace(" ", "");
		
		TTable table = this.getTable("TABLE");
    	if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        TParm data = new TParm();
        data.setData("TITLE", "TEXT", "明日预定就诊明细报表");
        data.setData("DATE", "TEXT", date_s+"~"+date_e);
        data.setData("USER", "TEXT", "制表人："+Operator.getName());
		TParm tableParm = table.getShowParmValue();
		TParm parm = new TParm();
//REGION_CODE;ADM_TYPE;;;TEL_HOME;;;;;;;REALDEPT_CODE;REALDR_CODE;;
		for(int i=0;i<table.getRowCount();i++){
    		parm.addData("MR_NO", tableParm.getValue("MR_NO",i));
    		parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME",i));
    		parm.addData("TEL_HOME", tableParm.getValue("TEL_HOME",i));
    		parm.addData("ADM_DATE", tableParm.getValue("ADM_DATE",i));
    		parm.addData("SESSION_CODE", tableParm.getValue("SESSION_CODE",i));
    		parm.addData("CLINICROOM_NO", tableParm.getValue("CLINICROOM_NO",i));
    		parm.addData("DEPT_CODE", tableParm.getValue("DEPT_CODE",i));
    		parm.addData("DR_CODE", tableParm.getValue("DR_CODE",i));
    		parm.addData("CLINICTYPE_CODE", tableParm.getValue("CLINICTYPE_CODE",i));
    		parm.addData("QUE_NO", tableParm.getValue("QUE_NO",i));
    		
    	}
    	
    
    	parm.setCount(parm.getCount("MR_NO"));
    	parm.addData("SYSTEM", "COLUMNS", "MR_NO");
    	parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
    	parm.addData("SYSTEM", "COLUMNS", "TEL_HOME");
    	parm.addData("SYSTEM", "COLUMNS", "ADM_DATE");
    	parm.addData("SYSTEM", "COLUMNS", "SESSION_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "CLINICROOM_NO");
    	parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "DR_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "CLINICTYPE_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "QUE_NO");
    	
    	data.setData("TABLE",parm.getData());
    	this.openPrintWindow("%ROOT%\\config\\prt\\reg\\REGTomorrowOrderPrint.jhw", data);
	}
	
	public void onClear(){
		table.removeRowAll();
		clearValue("START_DATE;END_DATE;ADM_TYPE;SESSION_CODE;CLINICROOM_NO;DEPT_CODE;DR_COD;SUM;TELNUM");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				StringTool.rollDate(date, +1).toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", StringTool.rollDate(date, +1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");

		telAll.setSelected(false);
		TComboBox admType = (TComboBox)this.getComponent("ADM_TYPE");
        admType.setValue("O");
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
	 

}
