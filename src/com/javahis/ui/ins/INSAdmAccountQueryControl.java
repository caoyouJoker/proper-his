package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Calendar;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TRadioButton;

/**
 * <p>Title: 住院医保综合对账查询</p>
 *
 * <p>Description:住院医保综合对账查询</p>
 *
 * <p>Company: javahis</p>
 *
 * @author yufh
 */
public class INSAdmAccountQueryControl extends TControl {

    private TTable Table;
    // 排序
	private Compare compare = new Compare();
	// 排序
	private boolean ascending = false;
	// 排序
	private int sortColumn = -1;
	
    /**
     * 初始化
     */
    public void onInit() {
        Timestamp date = SystemTool.getInstance().getDate() ;
        this.setValue("START_DATE",date); 
        this.setValue("END_DATE", date) ;
        this.setValue("PAT_TYPE", "1") ;
        this.setValue("INS_TYPE", "1") ;

        this.Table = ((TTable) getComponent("TABLE"));
        addListener(Table);
    }

    /**
     * 查询
     */
    public void onQuery() {   
    	//数据检核
    	if(checkdata())
		    return;
    String startDate = "";
    String endDate = "";
    String sql1 = "";
    String sqlCZ = ""; 
    String sqlCX = "";
    String sqlALL = "";
    String sqlHEAD = "";
    String sql2 = "";
    TParm result = new TParm();
    startDate = StringTool.getString(TypeTool
			.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
	endDate = StringTool.getString(TypeTool
			.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
	// 入院时间
	if (this.getRadioButton("IN_DATE").isSelected()) {
		sql1 = " AND  A.IN_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') " +
			   " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";	
	}
	// 出院时间
	if (this.getRadioButton("DS_DATE").isSelected()) {
		sql1 = " AND  B.DS_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') " +
			   " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";	
	}	
	// 申报时间
	if (this.getRadioButton("UPLOAD_DATE").isSelected()) {
		sql1 = " AND  B.UPLOAD_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') " +
			   " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";	
	}
	// 出票时间
	if (this.getRadioButton("CHARGE_DATE").isSelected()) {
		sql1 = " AND  D.CHARGE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') " +
			   " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";	
	}
	//普通病人(实际自付金额)
	if(this.getValue("PAT_TYPE").equals("1")){	
		sql2 = " CASE WHEN B.RESTART_STANDARD_AMT IS NULL THEN 0 ELSE B.RESTART_STANDARD_AMT END+"+
		" CASE WHEN B.STARTPAY_OWN_AMT IS NULL THEN 0 ELSE B.STARTPAY_OWN_AMT END+ "+
		" CASE WHEN B.OWN_AMT IS NULL THEN 0 ELSE B.OWN_AMT END+"+
		" CASE WHEN B.PERCOPAYMENT_RATE_AMT IS NULL THEN 0 ELSE B.PERCOPAYMENT_RATE_AMT END+ "+
		" CASE WHEN B.ADD_AMT IS NULL THEN 0 ELSE B.ADD_AMT END+ "+
		" CASE WHEN B.INS_HIGHLIMIT_AMT IS NULL THEN 0 ELSE B.INS_HIGHLIMIT_AMT END -"+ 
		" CASE WHEN B.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE B.ACCOUNT_PAY_AMT END  - "+
		" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE B.ARMYAI_AMT END - "+
		" CASE WHEN B.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE B.ILLNESS_SUBSIDY_AMT END AS SJZFJE,";
	}
	//单病种病人(实际自付金额)
	else if(this.getValue("PAT_TYPE").equals("2")){
		sql2 = " CASE WHEN B.STARTPAY_OWN_AMT IS NULL THEN 0 ELSE B.STARTPAY_OWN_AMT END+ "+
		" CASE WHEN B.BED_SINGLE_AMT IS NULL THEN 0 ELSE B.BED_SINGLE_AMT END + "+
		" CASE WHEN B.MATERIAL_SINGLE_AMT IS NULL THEN 0 ELSE B.MATERIAL_SINGLE_AMT END +"+ 
		" CASE WHEN B.PERCOPAYMENT_RATE_AMT IS NULL THEN 0 ELSE B.PERCOPAYMENT_RATE_AMT END + "+
		" CASE WHEN B.INS_HIGHLIMIT_AMT IS NULL THEN 0 ELSE B.INS_HIGHLIMIT_AMT END - "+
		" CASE WHEN B.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE B.ACCOUNT_PAY_AMT END - "+
		" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE B.ARMYAI_AMT END  - "+
		" CASE WHEN B.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE B.ILLNESS_SUBSIDY_AMT END AS SJZFJE,";
	}
	sqlHEAD = " SELECT A.MR_NO,A.CONFIRM_NO,A.PAT_NAME,"+
	" CASE A.SEX_CODE WHEN '1' THEN '男' WHEN '2' THEN '女' ELSE '' END  AS CHN_DESC,"+
	" A.IDNO,C.CTZ_DESC,A.IN_STATUS,"+
	" CASE WHEN B.PHA_AMT  IS NULL THEN 0 ELSE  B.PHA_AMT END +"+
	" CASE WHEN B.EXM_AMT  IS NULL THEN 0 ELSE  B.EXM_AMT END +"+
	" CASE WHEN B.TREAT_AMT  IS NULL THEN 0 ELSE  B.TREAT_AMT END+"+
	" CASE WHEN B.OP_AMT  IS NULL THEN 0 ELSE  B.OP_AMT END+"+
	" CASE WHEN B.BED_AMT  IS NULL THEN 0 ELSE  B.BED_AMT END+"+
	" CASE WHEN B.MATERIAL_AMT  IS NULL THEN 0 ELSE  B.MATERIAL_AMT END+"+
	" CASE WHEN B.BLOODALL_AMT  IS NULL THEN 0 ELSE  B.BLOODALL_AMT END+"+
	" CASE WHEN B.BLOOD_AMT  IS NULL THEN 0 ELSE  B.BLOOD_AMT END+"+
	" CASE WHEN B.OTHER_AMT  IS NULL THEN 0 ELSE  B.OTHER_AMT END AS TOT_AMT,"+
	sql2+
	" CASE WHEN B.NHI_PAY  IS NULL THEN 0 ELSE  B.NHI_PAY END  AS NHI_PAY,"+
	" CASE WHEN B.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE  B.ILLNESS_SUBSIDY_AMT END  AS ILLNESS_SUBSIDY_AMT," +
	" A.IN_DATE,B.DS_DATE,B.UPLOAD_DATE ,D.CHARGE_DATE,";
	//普通病人
	if(this.getValue("PAT_TYPE").equals("1")){		
	//城职
	if(this.getValue("INS_TYPE").equals("1")){
	 sqlCZ = sqlHEAD +
		" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE  B.ARMYAI_AMT END  AS ARMYAI_AMT,"+
		" CASE WHEN B.NHI_COMMENT  IS NULL THEN 0 ELSE  B.NHI_COMMENT END  AS NHI_COMMENT,"+
		" 0 AS YLJZ,"+
		" CASE WHEN B.ACCOUNT_PAY_AMT  IS NULL THEN 0 ELSE  B.ACCOUNT_PAY_AMT END  AS ACCOUNT_PAY_AMT " +
		" FROM INS_ADM_CONFIRM A,INS_IBS B,SYS_CTZ C ,BIL_IBS_RECPM D "+
		" WHERE A.CASE_NO = B.CASE_NO(+)"+
		" AND B.CASE_NO = D.CASE_NO(+)"+
		" AND A.CONFIRM_NO  = B.CONFIRM_NO(+)"+
		" AND A.HIS_CTZ_CODE = C.CTZ_CODE "+
		" AND B.RECEIPT_NO = D.RECEIPT_NO(+)"+
		sql1 +
		" AND C.NHI_CTZ_FLG = 'Y' "+
		" AND A.SDISEASE_CODE IS NULL "+
		" AND C.INS_CROWD_TYPE = '1'";
//		System.out.println("sqlCZ=====:"+sqlCZ); 
	 result = new TParm(TJDODBTool.getInstance().select(sqlCZ));
	}
	//城乡
	else if(this.getValue("INS_TYPE").equals("2")){
		 sqlCX = sqlHEAD +
		" 0 AS ARMYAI_AMT,"+
		" 0 AS NHI_COMMENT,"+
		" CASE WHEN B.NHI_COMMENT  IS NULL THEN 0 ELSE  B.NHI_COMMENT END +"+
		" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE  B.ARMYAI_AMT END AS YLJZ,"+
		" CASE WHEN B.ACCOUNT_PAY_AMT  IS NULL THEN 0 ELSE  B.ACCOUNT_PAY_AMT END  AS ACCOUNT_PAY_AMT " +
		" FROM INS_ADM_CONFIRM A,INS_IBS B,SYS_CTZ C ,BIL_IBS_RECPM D "+
		" WHERE A.CASE_NO = B.CASE_NO(+)"+
		" AND B.CASE_NO = D.CASE_NO(+)"+
		" AND A.CONFIRM_NO  = B.CONFIRM_NO(+)"+
		" AND A.HIS_CTZ_CODE = C.CTZ_CODE "+
		" AND B.RECEIPT_NO = D.RECEIPT_NO(+)"+
		sql1 +
		" AND C.NHI_CTZ_FLG = 'Y' "+
		" AND A.SDISEASE_CODE IS NULL "+
		" AND C.INS_CROWD_TYPE = '2'";	
//		 System.out.println("sqlCX=====:"+sqlCX); 
		 result = new TParm(TJDODBTool.getInstance().select(sqlCX));		 
	}
	//城职、城乡
	else {
		sqlCZ = sqlHEAD +
		" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE  B.ARMYAI_AMT END  AS ARMYAI_AMT,"+
		" CASE WHEN B.NHI_COMMENT  IS NULL THEN 0 ELSE  B.NHI_COMMENT END  AS NHI_COMMENT,"+
		" 0 AS YLJZ,"+
		" CASE WHEN B.ACCOUNT_PAY_AMT  IS NULL THEN 0 ELSE  B.ACCOUNT_PAY_AMT END  AS ACCOUNT_PAY_AMT " +
		" FROM INS_ADM_CONFIRM A,INS_IBS B,SYS_CTZ C ,BIL_IBS_RECPM D "+
		" WHERE A.CASE_NO = B.CASE_NO(+)"+
		" AND B.CASE_NO = D.CASE_NO(+)"+
		" AND A.CONFIRM_NO  = B.CONFIRM_NO(+)"+
		" AND A.HIS_CTZ_CODE = C.CTZ_CODE "+
		" AND B.RECEIPT_NO = D.RECEIPT_NO(+)"+
		sql1 +
		" AND C.NHI_CTZ_FLG = 'Y' "+
		" AND A.SDISEASE_CODE IS NULL "+
		" AND C.INS_CROWD_TYPE = '1'";
		sqlCX = sqlHEAD +
		" 0 AS ARMYAI_AMT,"+
		" 0 AS NHI_COMMENT,"+
		" CASE WHEN B.NHI_COMMENT  IS NULL THEN 0 ELSE  B.NHI_COMMENT END +"+
		" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE  B.ARMYAI_AMT END AS YLJZ,"+
		" CASE WHEN B.ACCOUNT_PAY_AMT  IS NULL THEN 0 ELSE  B.ACCOUNT_PAY_AMT END  AS ACCOUNT_PAY_AMT " +
		" FROM INS_ADM_CONFIRM A,INS_IBS B,SYS_CTZ C ,BIL_IBS_RECPM D "+
		" WHERE A.CASE_NO = B.CASE_NO(+)"+
		" AND B.CASE_NO = D.CASE_NO(+)"+
		" AND A.CONFIRM_NO  = B.CONFIRM_NO(+)"+
		" AND A.HIS_CTZ_CODE = C.CTZ_CODE "+
		" AND B.RECEIPT_NO = D.RECEIPT_NO(+)"+
		sql1 +
		" AND C.NHI_CTZ_FLG = 'Y' "+
		" AND A.SDISEASE_CODE IS NULL "+
		" AND C.INS_CROWD_TYPE = '2'";
		sqlALL = sqlCZ  + " UNION ALL " + sqlCX ;
//		System.out.println("sqlALL=====:"+sqlALL); 
		result = new TParm(TJDODBTool.getInstance().select(sqlALL));
	}
					
	}
	//单病种病人
	else if(this.getValue("PAT_TYPE").equals("2")){
		//城职
		if(this.getValue("INS_TYPE").equals("1")){
		 sqlCZ = sqlHEAD +
			" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE  B.ARMYAI_AMT END  AS ARMYAI_AMT,"+
			" CASE WHEN B.HOSP_APPLY_AMT  IS NULL THEN 0 ELSE  B.HOSP_APPLY_AMT END  AS NHI_COMMENT,"+
			" 0 AS YLJZ,"+
			" CASE WHEN B.ACCOUNT_PAY_AMT  IS NULL THEN 0 ELSE  B.ACCOUNT_PAY_AMT END  AS ACCOUNT_PAY_AMT "+
			" FROM INS_ADM_CONFIRM A,INS_IBS B,SYS_CTZ C ,BIL_IBS_RECPM D "+
			" WHERE A.CASE_NO = B.CASE_NO(+)"+
			" AND B.CASE_NO = D.CASE_NO(+)"+
			" AND A.CONFIRM_NO  = B.CONFIRM_NO(+)"+
			" AND A.HIS_CTZ_CODE = C.CTZ_CODE "+
			" AND B.RECEIPT_NO = D.RECEIPT_NO(+)"+
			sql1 +
			" AND C.NHI_CTZ_FLG = 'Y' "+
			" AND A.SDISEASE_CODE IS  NOT NULL "+
			" AND C.INS_CROWD_TYPE = '1'";
//		 System.out.println("sqlCZ=====:"+sqlCZ); 
		 result = new TParm(TJDODBTool.getInstance().select(sqlCZ));
		}
		//城乡
		else if(this.getValue("INS_TYPE").equals("2")){
			 sqlCX = sqlHEAD +
			" 0 AS ARMYAI_AMT,"+
			" 0 AS NHI_COMMENT,"+
			" CASE WHEN B.HOSP_APPLY_AMT  IS NULL THEN 0 ELSE  B.HOSP_APPLY_AMT END +"+
			" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE  B.ARMYAI_AMT END AS YLJZ,"+
			" CASE WHEN B.ACCOUNT_PAY_AMT  IS NULL THEN 0 ELSE  B.ACCOUNT_PAY_AMT END  AS ACCOUNT_PAY_AMT "+
			" FROM INS_ADM_CONFIRM A,INS_IBS B,SYS_CTZ C ,BIL_IBS_RECPM D "+
			" WHERE A.CASE_NO = B.CASE_NO(+)"+
			" AND B.CASE_NO = D.CASE_NO(+)"+
			" AND A.CONFIRM_NO  = B.CONFIRM_NO(+)"+
			" AND A.HIS_CTZ_CODE = C.CTZ_CODE "+
			" AND B.RECEIPT_NO = D.RECEIPT_NO(+)"+
			sql1 +
			" AND C.NHI_CTZ_FLG = 'Y' "+
			" AND A.SDISEASE_CODE IS NOT NULL "+
			" AND C.INS_CROWD_TYPE = '2'";
//			 System.out.println("sqlCX=====:"+sqlCX); 
			 result = new TParm(TJDODBTool.getInstance().select(sqlCX));
		}
		//城职、城乡
		else {
			sqlCZ = sqlHEAD +
			" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE  B.ARMYAI_AMT END  AS ARMYAI_AMT,"+
			" CASE WHEN B.HOSP_APPLY_AMT  IS NULL THEN 0 ELSE  B.HOSP_APPLY_AMT END  AS NHI_COMMENT,"+
			" 0 AS YLJZ,"+
			" CASE WHEN B.ACCOUNT_PAY_AMT  IS NULL THEN 0 ELSE  B.ACCOUNT_PAY_AMT END  AS ACCOUNT_PAY_AMT "+
			" FROM INS_ADM_CONFIRM A,INS_IBS B,SYS_CTZ C ,BIL_IBS_RECPM D "+
			" WHERE A.CASE_NO = B.CASE_NO(+)"+
			" AND B.CASE_NO = D.CASE_NO(+)"+
			" AND A.CONFIRM_NO  = B.CONFIRM_NO(+)"+
			" AND A.HIS_CTZ_CODE = C.CTZ_CODE "+
			" AND B.RECEIPT_NO = D.RECEIPT_NO(+)"+
			sql1 +
			" AND C.NHI_CTZ_FLG = 'Y' "+
			" AND A.SDISEASE_CODE IS  NOT NULL "+
			" AND C.INS_CROWD_TYPE = '1'";
			sqlCX = sqlHEAD +
			" 0 AS ARMYAI_AMT,"+
			" 0 AS NHI_COMMENT,"+
			" CASE WHEN B.HOSP_APPLY_AMT  IS NULL THEN 0 ELSE  B.HOSP_APPLY_AMT END +"+
			" CASE WHEN B.ARMYAI_AMT IS NULL THEN 0 ELSE  B.ARMYAI_AMT END AS YLJZ,"+
			" CASE WHEN B.ACCOUNT_PAY_AMT  IS NULL THEN 0 ELSE  B.ACCOUNT_PAY_AMT END  AS ACCOUNT_PAY_AMT "+
			" FROM INS_ADM_CONFIRM A,INS_IBS B,SYS_CTZ C ,BIL_IBS_RECPM D "+
			" WHERE A.CASE_NO = B.CASE_NO(+)"+
			" AND B.CASE_NO = D.CASE_NO(+)"+
			" AND A.CONFIRM_NO  = B.CONFIRM_NO(+)"+
			" AND A.HIS_CTZ_CODE = C.CTZ_CODE "+
			" AND B.RECEIPT_NO = D.RECEIPT_NO(+)"+
			sql1 +
			" AND C.NHI_CTZ_FLG = 'Y' "+
			" AND A.SDISEASE_CODE IS NOT NULL "+
			" AND C.INS_CROWD_TYPE = '2'";
			sqlALL =  sqlCZ  + " UNION ALL " + sqlCX ;
//			System.out.println("sqlALL=====:"+sqlALL); 
			result = new TParm(TJDODBTool.getInstance().select(sqlALL));	
		}	
	}
//	System.out.println("result=====:"+result); 
	if (result.getErrCode() < 0) {
		messageBox(result.getErrText());
		messageBox("执行失败");
		return;
	}
	if (result.getCount()<= 0) {
		messageBox("查无资料");
		((TTable) getComponent("TABLE")).removeRowAll();			
		return;
	}			
	((TTable) getComponent("TABLE")).setParmValue(result);
	
    }

    /**
    *
    * 核查查询条件
    * @return
    */
   private boolean checkdata() {
       String startDate = this.getValueString("START_DATE"); //起始时间
       String endDate = this.getValueString("END_DATE"); //结束时间
       String patType = this.getValueString("PAT_TYPE"); //病患类别
       if ("".equals(startDate)) {
           this.messageBox("开始时间不能为空");   
           return true;
       }
       if ("".equals(endDate)) {
           this.messageBox("结束时间不能为空");
           return true;
       }
       if ("".equals(patType)) {
           this.messageBox("病患类别不能为空");
           return true;
       }
       return false;
   }
    /**
     * 汇出
     */
    public void onExport() { 
    TTable table = (TTable)this.getComponent("TABLE");
    if (table.getRowCount() <= 0) {
         this.messageBox("没有汇出数据");
          return;
     }
    ExportExcelUtil.getInstance().exportExcel(table, "住院医保综合对账查询");
        
    }

    /**
     * 清空
     */
    public void onClear() {
        Timestamp date = SystemTool.getInstance().getDate() ;
        this.setValue("START_DATE", date);
        this.setValue("END_DATE", date);
        this.setValue("PAT_TYPE", "1") ;
        this.setValue("INS_TYPE", "1") ;
        this.getRadioButton("IN_DATE").setSelected(true);
        ((TTable) getComponent("TABLE")).removeRowAll();

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
				TParm tableData =Table.getParmValue();
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
				String tblColumnName = Table.getParmMap(sortColumn);
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
	    Table.setParmValue(parmTable);
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
    /**
	* 获得单选控件
	* 
	* @param name
	* @return
	*/
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}	
	
	
}
