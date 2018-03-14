package com.javahis.ui.bil;



import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;



/**
 * Title: 出院患者欠费统计表
 * Description:出院患者欠费统计表
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2017
 * @version 1.0
 */
public class BILArrearageControl extends TControl {
	private TTable table;
	 // 排序
	private Compare compare = new Compare();
	// 排序
	private boolean ascending = false;
	// 排序
	private int sortColumn = -1;
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        onClear();
        addListener(table);
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("出院开始日期不能为空");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("出院结束日期不能为空");
    		return true;
    	}		
	    return false; 
	}
    /**
     * 查询
     */
    public void onQuery() {
    	//数据检核
    	if(checkdata())
		    return;
    	String sql ="";
    	String dept ="";//科室
    	String station ="";//病区
    	TParm result = new TParm();
    	TParm result1 = new TParm();
    	TParm result2 = new TParm();
    	TParm data = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
		//科室选择
		if(!this.getValue("DEPT_CODE").equals("")){
			String deptcode = this.getValue("DEPT_CODE").toString();
			dept = " AND A.DS_DEPT_CODE = '"+ deptcode+ "'";
		}
		//病区选择
		if(!this.getValue("STATION_CODE").equals("")){
			String stationcode = this.getValue("STATION_CODE").toString();
			station = " AND A.DS_STATION_CODE = '"+ stationcode+ "'";
		}
		//非医保
		sql=" SELECT DISTINCT A.MR_NO,B.PAT_NAME, B.TEL_HOME,C.DEPT_CHN_DESC,G.STATION_DESC,D.CTZ_DESC,J.CTZ_DESC AS CTZ2_DESC,"+
			" 0 AS ARMYAI_AMT,0 AS NHI_PAY,0 AS NHI_COMMENT,0 AS ACCOUNT_PAY_AMT,0 AS ILLNESS_SUBSIDY_AMT,"+
			" A.IN_DATE,A.DS_DATE,F.UPLOAD_DATE, A.CASE_NO"+
			" FROM ADM_INP A,SYS_PATINFO B,SYS_DEPT C,SYS_CTZ D," +
			" INS_ADM_CONFIRM E,INS_IBS F,SYS_STATION G,IBS_BILLM H,SYS_CTZ J"+
			" WHERE A.DS_DATE IS NOT NULL"+
			" AND A.MR_NO = B.MR_NO"+
			" AND A.DS_DEPT_CODE = C.DEPT_CODE" +
			" AND A.DS_STATION_CODE = G.STATION_CODE"+
			" AND D.INS_CROWD_TYPE IS NULL"+
			" AND A.CTZ1_CODE = D.CTZ_CODE"+
			" AND A.CTZ2_CODE = J.CTZ_CODE(+)"+
			" AND A.CASE_NO = E.CASE_NO(+)"+
			" AND A.CASE_NO = F.CASE_NO(+)"+
			" AND A.CASE_NO  = H.CASE_NO"+
			" AND H.RECEIPT_NO IS NULL" +
			" AND H.REFUND_FLG = 'N'" +
			" AND H.AR_AMT !=0"+
			dept+
			station+
			" AND A.DS_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
			" AND A.CANCEL_FLG = 'N'"+
			//医保
			" UNION ALL" +
			" SELECT I.MR_NO,I.PAT_NAME, I.TEL_HOME,I.DEPT_CHN_DESC,I.STATION_DESC,I.CTZ_DESC,I.CTZ2_DESC, SUM(I.ARMYAI_AMT) AS ARMYAI_AMT,"+
			" SUM(I.NHI_PAY) AS NHI_PAY, SUM(I.NHI_COMMENT) AS NHI_COMMENT,SUM(I.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT,"+ 
			" SUM(I.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT,I.IN_DATE,I.DS_DATE,MAX(I.UPLOAD_DATE) AS UPLOAD_DATE,I.CASE_NO"+
			" FROM ( SELECT DISTINCT A.MR_NO,B.PAT_NAME,B.TEL_HOME, C.DEPT_CHN_DESC,G.STATION_DESC,D.CTZ_DESC,J.CTZ_DESC AS CTZ2_DESC,"+
			" F.ARMYAI_AMT , CASE WHEN E.SDISEASE_CODE IS NULL THEN "+
			" ( CASE WHEN F.TOT_PUBMANADD_AMT IS NULL  THEN 0 ELSE F.TOT_PUBMANADD_AMT END + F.NHI_PAY )"+
			" ELSE ( CASE WHEN F.TOT_PUBMANADD_AMT IS NULL THEN 0 ELSE F.TOT_PUBMANADD_AMT END + F.NHI_PAY + "+ 
			" CASE WHEN F.SINGLE_STANDARD_OWN_AMT  IS NULL THEN 0 ELSE F.SINGLE_STANDARD_OWN_AMT END -"+
			" CASE WHEN F.SINGLE_SUPPLYING_AMT IS NULL THEN 0 ELSE F.SINGLE_SUPPLYING_AMT END) END NHI_PAY , F.NHI_COMMENT,"+
			" CASE WHEN F.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE F.ACCOUNT_PAY_AMT END ACCOUNT_PAY_AMT , F.ILLNESS_SUBSIDY_AMT,"+
			" A.IN_DATE,A.DS_DATE,F.UPLOAD_DATE,A.CASE_NO"+
			" FROM ADM_INP A,SYS_PATINFO B,SYS_DEPT C,SYS_CTZ D ," +
			" INS_ADM_CONFIRM E,INS_IBS F,SYS_STATION G,IBS_BILLM H,SYS_CTZ J"+
			" WHERE A.DS_DATE IS NOT NULL"+
			" AND A.MR_NO = B.MR_NO"+
			" AND A.DS_DEPT_CODE = C.DEPT_CODE" +
			" AND A.DS_STATION_CODE = G.STATION_CODE"+
			" AND A.CTZ1_CODE = D.CTZ_CODE"+
			" AND A.CTZ2_CODE = J.CTZ_CODE(+)"+
			" AND A.CASE_NO = E.CASE_NO"+
			" AND A.CASE_NO = F.CASE_NO"+
			" AND A.CASE_NO  = H.CASE_NO"+
			" AND H.RECEIPT_NO IS NULL" +
			" AND H.REFUND_FLG = 'N'" +
			" AND H.AR_AMT !=0"+
			" AND E.IN_STATUS = '2'"+
			dept+
			station+
			" AND A.DS_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
			" AND A.CANCEL_FLG = 'N') I " +
			" GROUP BY I.MR_NO,I.PAT_NAME, I.TEL_HOME,I.DEPT_CHN_DESC,I.STATION_DESC," +
			" I.CTZ_DESC,I.CTZ2_DESC,I.IN_DATE,I.DS_DATE,I.CASE_NO";
		
		result = new TParm(TJDODBTool.getInstance().select(sql)); 
//		System.out.println("resultCOUNT========="+result.getCount());
//		System.out.println("result========="+result); 
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			((TTable) getComponent("TABLE")).removeRowAll();
			return;
		}
		
		for (int i = 0; i < result.getCount(); i++) {
			//预交金总额
        	String sql1=" SELECT nvl(SUM(PRE_AMT),0) TOT_BIL_PAY "+
        	            " FROM BIL_PAY"+
        	            " WHERE CASE_NO='"+result.getValue("CASE_NO",i)+"'"+
        	            " AND  REFUND_FLG = 'N' "+
        	            " AND TRANSACT_TYPE IN ('01', '04')"+
        	            " AND RESET_RECP_NO IS NULL";
        	result1 = new TParm(TJDODBTool.getInstance().select(sql1));        	
        	if (result1.getCount()>0) {
        		result.setData("TOT_BIL_PAY",i,result1.getDouble("TOT_BIL_PAY",0));
			}
        	//总费用
        	String sql2=" SELECT SUM(D.TOT_AMT) TOT_AMT " +
        	            " FROM ADM_INP A,IBS_ORDM M,IBS_ORDD D " +
        	            " WHERE A.CASE_NO=D.CASE_NO " +
        	            " AND M.CASE_NO = D.CASE_NO " +
        	            " AND A.CASE_NO = '"+result.getValue("CASE_NO",i)+"'"+
        	            " AND M.CASE_NO_SEQ =D.CASE_NO_SEQ" +
        	            " AND A.CANCEL_FLG = 'N'";
        	result2 = new TParm(TJDODBTool.getInstance().select(sql2));
        	if (result2.getCount()>0) {
        		result.setData("TOT_AMT",i,result2.getDouble("TOT_AMT",0));
			}
         //医保支付金额
        	double totinsamt = 0.00;
        	totinsamt = StringTool.round(result.getDouble("ARMYAI_AMT",i)+
        	            result.getDouble("NHI_PAY",i)+
        	            result.getDouble("NHI_COMMENT",i)+
        	            result.getDouble("ACCOUNT_PAY_AMT",i)+
        	            result.getDouble("ILLNESS_SUBSIDY_AMT",i),2); 	
        	result.setData("TOT_INS_AMT",i,totinsamt);        	                    	
		}
//		System.out.println("resultCOUNTFF========="+result.getCount()); 
//		System.out.println("resultFF========="+result); 
		
		for (int i = 0; i < result.getCount(); i++) {			
		   //欠费金额
			double arrearageamt = 0.00;
			arrearageamt = StringTool.round(result.getDouble("TOT_BIL_PAY",i)-
			               (result.getDouble("TOT_AMT",i)-
			                result.getDouble("TOT_INS_AMT",i)),2);					
			  result.setData("ARREARAGE_AMT",i,arrearageamt); 						
		}		
//	    System.out.println("result========="+result);
	    if(this.getValue("ARREARAGE_TYPE").equals("1")){//欠费
	    for (int i = 0; i < result.getCount(); i++) {	    	
	    	 if(result.getDouble("ARREARAGE_AMT",i)<0){
	    		 data.addData("MR_NO",result.getData("MR_NO", i));
	    		 data.addData("PAT_NAME",result.getData("PAT_NAME", i));
	    		 data.addData("TEL_HOME",result.getData("TEL_HOME", i));
	    		 data.addData("DEPT_CHN_DESC",result.getData("DEPT_CHN_DESC", i));
	    		 data.addData("STATION_DESC",result.getData("STATION_DESC", i));
	    		 data.addData("CTZ_DESC",result.getData("CTZ_DESC", i));
	    		 data.addData("CTZ2_DESC",result.getData("CTZ2_DESC", i));
	    		 data.addData("ARMYAI_AMT",result.getData("ARMYAI_AMT", i));
	    		 data.addData("NHI_PAY",result.getData("NHI_PAY", i));
	    		 data.addData("NHI_COMMENT",result.getData("NHI_COMMENT", i));
	    		 data.addData("ACCOUNT_PAY_AMT",result.getData("ACCOUNT_PAY_AMT", i));
	    		 data.addData("ILLNESS_SUBSIDY_AMT",result.getData("ILLNESS_SUBSIDY_AMT", i));
	    		 data.addData("IN_DATE",result.getData("IN_DATE", i));
	    		 data.addData("DS_DATE",result.getData("DS_DATE", i));
	    		 data.addData("UPLOAD_DATE",result.getData("UPLOAD_DATE", i));
	    		 data.addData("CASE_NO",result.getData("CASE_NO", i));
	    		 data.addData("TOT_BIL_PAY",result.getData("TOT_BIL_PAY", i));
	    		 data.addData("TOT_AMT",result.getData("TOT_AMT", i));
	    		 data.addData("TOT_INS_AMT",result.getData("TOT_INS_AMT", i));
	    		 data.addData("ARREARAGE_AMT",result.getData("ARREARAGE_AMT", i));
	    	 }	    	 
	    }
	    
	    table.setParmValue(data); 
	 }else if(this.getValue("ARREARAGE_TYPE").equals("2")){//不欠费
		    for (int i = 0; i < result.getCount(); i++) {	    	
		    	 if(result.getDouble("ARREARAGE_AMT",i)>=0){
		    		 data.addData("MR_NO",result.getData("MR_NO", i));
		    		 data.addData("PAT_NAME",result.getData("PAT_NAME", i));
		    		 data.addData("TEL_HOME",result.getData("TEL_HOME", i));
		    		 data.addData("DEPT_CHN_DESC",result.getData("DEPT_CHN_DESC", i));
		    		 data.addData("STATION_DESC",result.getData("STATION_DESC", i));
		    		 data.addData("CTZ_DESC",result.getData("CTZ_DESC", i));
		    		 data.addData("CTZ2_DESC",result.getData("CTZ2_DESC", i));
		    		 data.addData("ARMYAI_AMT",result.getData("ARMYAI_AMT", i));
		    		 data.addData("NHI_PAY",result.getData("NHI_PAY", i));
		    		 data.addData("NHI_COMMENT",result.getData("NHI_COMMENT", i));
		    		 data.addData("ACCOUNT_PAY_AMT",result.getData("ACCOUNT_PAY_AMT", i));
		    		 data.addData("ILLNESS_SUBSIDY_AMT",result.getData("ILLNESS_SUBSIDY_AMT", i));
		    		 data.addData("IN_DATE",result.getData("IN_DATE", i));
		    		 data.addData("DS_DATE",result.getData("DS_DATE", i));
		    		 data.addData("UPLOAD_DATE",result.getData("UPLOAD_DATE", i));
		    		 data.addData("CASE_NO",result.getData("CASE_NO", i));
		    		 data.addData("TOT_BIL_PAY",result.getData("TOT_BIL_PAY", i));
		    		 data.addData("TOT_AMT",result.getData("TOT_AMT", i));
		    		 data.addData("TOT_INS_AMT",result.getData("TOT_INS_AMT", i));
		    		 data.addData("ARREARAGE_AMT",result.getData("ARREARAGE_AMT", i));
		    	 }	    	 
		    } 
		    table.setParmValue(data);    
	 }
	 else {//全部
		 table.setParmValue(result);
	 } 	
		
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	 String title ="出院患者欠费统计表";  				 
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);			
    }
    	
    /**
     * 清空
     */
    public void onClear() {
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());	
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
    	this.setValue("DEPT_CODE",Operator.getDept());
    	this.setValue("STATION_CODE",Operator.getStation());
    	this.setValue("ARREARAGE_TYPE","1");
 	   this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
    /**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 *            TTable
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

				getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
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
	 * 转换parm中的列
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
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
	 * 拿到菜单
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}
}
