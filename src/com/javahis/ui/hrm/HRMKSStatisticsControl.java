package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.hrm.HRMCompanyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title:科室检查人数统计 </p>
 *
 * <p>Description:科室检查人数统计 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class HRMKSStatisticsControl extends TControl
{
	public TTable getTTable(String tag) {
		return (TTable)this.getComponent(tag);
	}
	private String start_Date;
	private String end_Date;
        private TTextFormat patName;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 得到当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("end_Date",
				date.toString().substring(0, 10).replace('-', '/'));
		this.setValue("start_Date",
				StringTool.rollDate(date, -7).toString().substring(0, 10).
				replace('-', '/'));
		setValue("PURORDER_DATE", date);
                patName=(TTextFormat)this.getComponent("EXEC_DR_CODE");
//                this.messageBox_(patName);
                String sql = "SELECT A.EXEC_DR_CODE AS ID,B.USER_NAME AS NAME FROM HRM_ORDER A,SYS_OPERATOR B WHERE A.DEPT_ATTRIBUTE IS NOT NULL AND A.EXEC_DR_CODE=B.USER_ID GROUP BY A.EXEC_DR_CODE,B.USER_NAME";
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
//                this.messageBox_(patParm);
                patName.setPopupMenuData(patParm);
                patName.setComboSelectRow();
                patName.popupMenuShowData();
		//this.onQuery();
	}

	/**
	 *  查询方法
	 */
	public void onQuery() {
		start_Date=this.getValue("start_Date").toString().substring(0, 10).replace('-', '/') + " 00:00:00";
		end_Date=this.getValue("end_Date").toString().substring(0, 10).replace('-', '/') + " 23:59:59";
		String DEPT_ATTRIBUTE=this.getValueString("DEPT_ATTRIBUTE");
		String EXEC_DR_CODE=this.getValueString("EXEC_DR_CODE");
		String sql="";
		//============xueyf modify 20120222 start
		//如果 需要 过滤已缴费尚未检查的病患条件中 增加 AND a.exec_dr_code is not null
		//============xueyf modify 20120222 stop
		
        // add by wangb 2016/06/27 增加角色类型筛选过滤
		String roleType = "";
		String roleTypeFilter = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
			roleTypeFilter = " AND C.ROLE_TYPE IN ('" + roleType + "') ";
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
			roleTypeFilter = " AND C.ROLE_TYPE = '" + roleType + "' ";
		}
		
       if("".equals(DEPT_ATTRIBUTE)&&"".equals(EXEC_DR_CODE))
       {
		sql=" SELECT  B.CHN_DESC, COUNT (A.CASE_NO)"+
		" FROM   HRM_ORDER A, SYS_DICTIONARY B, HRM_CONTRACTD C "+
		" WHERE  A.DEPT_ATTRIBUTE IS NOT NULL"+
		" AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE'"+
		" AND A.DEPT_ATTRIBUTE = B.ID"+
		" AND  A.SETMAIN_FLG='Y' "+   
		" AND BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS') "+
		" AND A.CONTRACT_CODE = C.CONTRACT_CODE AND A.MR_NO = C.MR_NO " + roleTypeFilter +
		" GROUP BY A.DEPT_ATTRIBUTE, B.CHN_DESC";
       }
       if(!"".equals(DEPT_ATTRIBUTE)&&!"".equals(EXEC_DR_CODE))
       {
    	   sql=" SELECT  B.CHN_DESC, A.EXEC_DR_CODE,COUNT (A.CASE_NO)"+
   		" FROM   HRM_ORDER A, SYS_DICTIONARY B, HRM_CONTRACTD C "+
   		" WHERE  A.DEPT_ATTRIBUTE IS NOT NULL"+
   		" AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE'"+
   		" AND A.DEPT_ATTRIBUTE = B.ID"+
   		" AND  A.SETMAIN_FLG='Y' "+   
   		" AND B.ID='"+DEPT_ATTRIBUTE+"'"+
   		" AND A.EXEC_DR_CODE='"+EXEC_DR_CODE+"'"+
   		" AND BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
   		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS') "+
   		" AND A.CONTRACT_CODE = C.CONTRACT_CODE AND A.MR_NO = C.MR_NO " + roleTypeFilter +
   		" GROUP BY A.DEPT_ATTRIBUTE, B.CHN_DESC,A.EXEC_DR_CODE";
       }
       if("".equals(DEPT_ATTRIBUTE)&&!"".equals(EXEC_DR_CODE))
       {
    	   sql=" SELECT  B.CHN_DESC,A.EXEC_DR_CODE, COUNT (A.CASE_NO)"+
   		" FROM   HRM_ORDER A, SYS_DICTIONARY B, HRM_CONTRACTD C "+
   		" WHERE  A.DEPT_ATTRIBUTE IS NOT NULL"+
   		" AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE'"+
   		" AND A.DEPT_ATTRIBUTE = B.ID"+
   		" AND  A.SETMAIN_FLG='Y' "+   
   		" AND A.EXEC_DR_CODE='"+EXEC_DR_CODE+"'"+
   		" AND BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
   		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS')  "+
   		" AND A.CONTRACT_CODE = C.CONTRACT_CODE AND A.MR_NO = C.MR_NO " + roleTypeFilter +
   		" GROUP BY A.DEPT_ATTRIBUTE, B.CHN_DESC,A.EXEC_DR_CODE";
       }
       if(!"".equals(DEPT_ATTRIBUTE)&&"".equals(EXEC_DR_CODE))
       {
    	   sql=" SELECT  B.CHN_DESC, COUNT (A.CASE_NO)"+
   		" FROM   HRM_ORDER A, SYS_DICTIONARY B, HRM_CONTRACTD C "+
   		" WHERE  A.DEPT_ATTRIBUTE IS NOT NULL"+
   		" AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE'"+
   		" AND A.DEPT_ATTRIBUTE = B.ID"+
   		" AND  A.SETMAIN_FLG='Y' "+   
   		" AND B.ID='"+DEPT_ATTRIBUTE+"'"+
   		" AND BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
   		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS')  "+
   		" AND A.CONTRACT_CODE = C.CONTRACT_CODE AND A.MR_NO = C.MR_NO " + roleTypeFilter +
   		" GROUP BY A.DEPT_ATTRIBUTE, B.CHN_DESC";
       }
       
		TParm selParm = new TParm();

		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.getTTable("tbl1").setParmValue(selParm);
		if(getTTable("tbl1").getRowCount()<1)
			this.messageBox("E0008");//弹出提示对话框（“没有数据”）
	}

	/**
	 *  打印方法
	 */
	public void onPrint() {
		int ATM = 0;
		TTable table_d = getTTable("tbl1");
		if (table_d.getRowCount() > 0) {
			// 打印数据OPT_USER;COUNT(CASE_NO)
			TParm date = new TParm();

			// 表头数据
			date.setData("Title", "TEXT",
					Manager.getOrganization().
					getHospitalCHNFullName(Operator.getRegion()) +
					"科室体检人数统计报表");
			date.setData("createDate", "TEXT",
					"制表日期: " +
					SystemTool.getInstance().getDate().toString().
					substring(0, 10).replace('-', '/'));
			date.setData("start_Date", "TEXT",
					"开始时间: " +
					start_Date.
					substring(0, 10).replace('-', '/'));
			date.setData("end_Date", "TEXT",
					"结束时间: " +
					end_Date.
					substring(0, 10).replace('-', '/'));
			// 表格数据 B.CHN_DESC, COUNT (A.CASE_NO)
			TParm parm = new TParm();
			TParm tableParm = table_d.getParmValue();
			// System.out.println("tableParm====" + tableParm);
			int count = tableParm.getCount();

			for (int i = 0; i < count; i++) {
				parm.addData("CHN_DESC",
						tableParm.getValue("CHN_DESC", i));
				parm.addData("COUNT(A.CASE_NO)",
						tableParm.getValue("COUNT(A.CASE_NO)", i));
				// System.out.println("num=" +
				//		tableParm.getValue("COUNT(A.CASE_NO)", i));
				ATM +=
					StringTool.getInt(tableParm.getValue("COUNT(A.CASE_NO)", i));
			}
			parm.setCount(parm.getCount("COUNT(A.CASE_NO)"));
			// System.out.println("parm====" + parm);
			parm.addData("SYSTEM", "COLUMNS", "CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "COUNT(A.CASE_NO)");
			// System.out.println("AFTER parm====" + parm);
			date.setData("tbl1", parm.getData());
			// 表尾数据
			date.setData("createUser", "TEXT", "制表人: " + Operator.getName());
			date.setData("pass", "TEXT", "审核人: ");
			date.setData("ATM", "TEXT",
					"总计人数：" + ATM);
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMKSStatistics.jhw",
					date);
		}
		else {
			this.messageBox("E0010");//弹出没有打印数据
			return;
		}
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		//得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table_d = getTTable("tbl1");
		ExportExcelUtil.getInstance().exportExcel(table_d, "各科问诊人次统计报表");
	}
	/**
	 * 清空
	 */
	public void onClear()
	{
		this.clearValue("EXEC_DR_CODE;DEPT_ATTRIBUTE");
		this.getTTable("tbl1").setSelectionMode(0);
		this.getTTable("tbl1").removeRowAll();
		onInit();
	}

}
