package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
* <p>Title: 预约挂号月统计表</p>
*
* <p>Description:预约挂号月统计表 </p>
*
* <p>Copyright: Copyright (c) </p>
*
* <p>Company:bluecore </p>
*
* @author huangtt 20131230
* @version 1.0
*/

public class STAOutOrderDeptIbsControl extends TControl {
	private static TTable table;

	  public void onInit()
	  {
	    table = (TTable)getComponent("TABLE");
	    Timestamp date = StringTool.getTimestamp(new Date());
	    setValue("START_DATE", 
	      StringTool.rollDate(date, -30L).toString().substring(0, 10).replace('-', '/') + 
	      " 00:00:00");
	    setValue("END_DATE", date.toString()
	      .substring(0, 10).replace('-', '/') + 
	      " 23:59:59");
	  }

	  public void onQuery() {
	    String date_s = getValueString("START_DATE");
	    String date_e = getValueString("END_DATE");
	    date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
	      .replace("-", "").replace(" ", "");
	    date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
	      .replace("-", "").replace(" ", "");
	    String dept = "";
	    if (!StringUtil.isNullString(getValueString("DEPT_CODE"))) {
	      dept = "AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
	    }
	    //-----设置表头----start---------
	    String header = "就诊号,100;姓名,80;";
	    String parmMap = "CASE_NO;PAT_NAME;";
	    String data = "0,left;1,left;";

	    String sql = "SELECT C.DEPT_CHN_DESC,A.DEPT_CODE FROM IBS_ORDD A,  SYS_DEPT C WHERE C.DEPT_CODE = A.DEPT_CODE AND A.BILL_DATE BETWEEN TO_DATE ('" + 
	      date_s + "'," + 
	      " 'YYYYMMDDHH24MISS'" + 
	      ")" + 
	      " AND TO_DATE ('" + date_e + "'," + 
	      " 'YYYYMMDDHH24MISS'" + 
	      " )" + 
	      dept + 
	      " GROUP BY C.DEPT_CHN_DESC,A.DEPT_CODE" + 
	      " ORDER BY A.DEPT_CODE";
	    TParm headerParm = new TParm(TJDODBTool.getInstance().select(sql));

	    String sql1 = "SELECT * FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE = 'IBS' ";
	    String sql2 = "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE'";
	    TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
	    TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
	    String heard1 = "";
	    String t = "";
	    for (int i = 1; i < 21; i++) {
	      if (i / 10 < 1)
	        t = "CHARGE0" + i;
	      else {
	        t = "CHARGE" + i;
	      }
	      heard1 = heard1 + parm1.getValue(t, 0) + ";";
	    }
	    String[] h = heard1.split(";");
	    String heard2 = "";
	    String parmMap1 = "";
	    for (int i = 0; i < h.length; i++) {
	      for (int j = 0; j < parm2.getCount(); j++) {
	        if (h[i].equals(parm2.getValue("ID", j))) {
	          heard2 = heard2 + h[i] + parm2.getValue("CHN_DESC", j) + ";";
	          parmMap1 = parmMap1 + "charge" + h[i] + ";";
	        }
	      }
	    }

	    heard2 = heard2.substring(0, heard2.length() - 1);
	    parmMap1 = parmMap1.substring(0, parmMap1.length() - 1);
	    String[] h2 = heard2.split(";");
	    String[] p1 = parmMap1.split(";");

	    for (int i = 0; i < headerParm.getCount(); i++) {
	      for (int j = 0; j < h2.length; j++) {
	        header = header + headerParm.getValue("DEPT_CHN_DESC", i) + h2[j] + ",150;";
	      }
	      for (int k = 0; k < p1.length; k++) {
	        parmMap = parmMap + p1[k] + headerParm.getValue("DEPT_CODE", i) + ";";
	        data = data + (k + 2 + 20 * i) + ",right;";
	      }
	    }

//	    System.out.println("header==" + header);
//	    System.out.println("parmMap==" + parmMap);
//	    System.out.println("data==" + data);
	    table.setHeader(header.substring(0, header.length() - 1));
	    table.setParmMap(parmMap.substring(0, parmMap.length() - 1));
	    table.setLockColumns("all");
	    table.setColumnHorizontalAlignmentData(data.substring(0, data.length() - 1));
	  //-----设置表头----end---------
	    //往表格覆值
	    sql = "SELECT  A.CASE_NO,E.PAT_NAME, A.REXP_CODE, B.CHN_DESC,C.DEPT_CHN_DESC, A.DEPT_CODE,SUM (A.TOT_AMT) TOT_AMT FROM IBS_ORDD A, SYS_DICTIONARY B,SYS_DEPT C,ADM_INP D,SYS_PATINFO E WHERE A.REXP_CODE = B.ID AND B.GROUP_ID = 'SYS_CHARGE' AND C.DEPT_CODE = A.DEPT_CODE AND D.CASE_NO = A.CASE_NO AND D.MR_NO = E.MR_NO AND A.BILL_DATE BETWEEN TO_DATE('" + 
	      date_s + "', 'YYYYMMDDHH24MISS')" + 
	      " AND TO_DATE('" + date_e + "', 'YYYYMMDDHH24MISS')" + 
	      dept + 
	      " GROUP BY A.REXP_CODE, C.DEPT_CHN_DESC,B.CHN_DESC, A.CASE_NO,E.PAT_NAME,A.DEPT_CODE" + 
	      " ORDER BY  A.CASE_NO,A.DEPT_CODE,A.REXP_CODE";
	    TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

	    TParm tableParm = new TParm();
	    int count = 0;
	    for (int i = 0; i < parm.getCount() - 1; i++) {
	      if (!parm.getValue("CASE_NO", i).equals(parm.getValue("CASE_NO", i + 1))) {
	        count++;
	      }
	    }
	    String[] aa = parmMap.substring(0, parmMap.length() - 1).split(";");
	    for (int i = 0; i <= count; i++) {
	      for (int j = 0; j < aa.length; j++) {
	        tableParm.addData(aa[j], Integer.valueOf(0));
	      }
	    }
	    count = 0;
	    for (int i = 0; i < parm.getCount() - 1; i++) {
	      tableParm.setData("charge" + parm.getValue("REXP_CODE", i) + parm.getValue("DEPT_CODE", i), count, parm.getValue("TOT_AMT", i));
	      if (!parm.getValue("CASE_NO", i).equals(parm.getValue("CASE_NO", i + 1))) {
	        tableParm.setData("CASE_NO", count, parm.getValue("CASE_NO", i));
	        tableParm.setData("PAT_NAME", count, parm.getValue("PAT_NAME", i));
	        count++;
	      }
	    }

	    tableParm.setData("charge" + parm.getValue("REXP_CODE", parm.getCount() - 1) + parm.getValue("DEPT_CODE", parm.getCount() - 1), count, parm.getValue("TOT_AMT", parm.getCount() - 1));
	    tableParm.setData("CASE_NO", count, parm.getValue("CASE_NO", parm.getCount() - 1));
	    tableParm.setData("PAT_NAME", count, parm.getValue("PAT_NAME", parm.getCount() - 1));
//	    System.out.println("tableParm==" + tableParm);
	    if(tableParm.getCount("CASE_NO")<0){
	    	this.messageBox("没有要查询的数据！");
	    	onClear();
	    	return;
	    }
	    table.setParmValue(tableParm);
	  }

	  public void onClear()
	  {
	    table.removeRowAll();
	    table.setHeader("");
	    clearValue("DEPT_CODE");
	    Timestamp date = StringTool.getTimestamp(new Date());
	    setValue("START_DATE", 
	      StringTool.rollDate(date, -30L).toString().substring(0, 10).replace('-', '/') + 
	      " 00:00:00");
	    setValue("END_DATE", date.toString()
	      .substring(0, 10).replace('-', '/') + 
	      " 23:59:59");
	  }

	  public void onExport()
	  {
	    TTable table = (TTable)callFunction("UI|TABLE|getThis", new Object[0]);
	    ExportExcelUtil.getInstance().exportExcel(table, "出院患者开单科室费用报表");
	  }

}
