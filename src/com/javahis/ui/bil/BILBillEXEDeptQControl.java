package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import jdo.bil.BILComparator;

//import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.ui.TComboBox;
//import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
//import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
* <p>Title: 开单执行科室查询</p>
*
* <p>Description:开单执行科室查询 </p>
*
* <p>Copyright: Copyright (c) </p>
*
* <p>Company: </p>
*
* @author zhangs 20151022
* @version 1.0
*/


public class BILBillEXEDeptQControl extends TControl {
//	private static final String TParm = null;
	private static TTable table;
//	private static TComboBox comboBox;
	private boolean ascending = false;// 用于排序
    private int sortColumn = -1;// 用于排序
    private BILComparator compare = new BILComparator();// 用于排序
	
	public void onInit(){
		 table = (TTable) getComponent("TABLE");  
		 Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 StringTool.rollDate(date, -30).toString().substring(0, 10).replace('-', '/')
							+ " 00:00:00");
		 this.setValue("END_DATE", date.toString()
					.substring(0, 10).replace('-', '/')
					+ " 23:59:59");
		 addListener(table);
	}
	
	public void onQuery(){
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		String billDept=getValueString("BILLDEPT");
		String exeDept=getValueString("EXEDEPT");
		this.CardialSurgeryBldUseD( date_s, date_e,billDept,exeDept);		
	}
	
	
	public void onClear(){
		table.setParmValue(new TParm());
//		clearValue("BILLDEPT");
//		clearValue("EXEDEPT");
		Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 StringTool.rollDate(date, -30).toString().substring(0, 10).replace('-', '/')
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
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "开单执行科室查询");
	}

	//心外科手术患者用血明细表
	public void CardialSurgeryBldUseD(String date_s,String  date_e,String deptCode,String exeDeptCode) {
		String Sql =" SELECT A.BILL_DATE,F.DEPT_ABS_DESC,E.COST_CENTER_ABS_DESC,A.ORDER_CODE,B.ORDER_DESC, "+
                    " A.DOSAGE_QTY,G.UNIT_CHN_DESC,B.SPECIFICATION,A.TOT_AMT,C.MR_NO,A.CASE_NO,D.PAT_NAME, "+
                    " A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,A.INDV_FLG,A.CASE_NO_SEQ,A.SEQ_NO "+
                    " FROM IBS_ORDD A ,SYS_FEE B,ADM_INP C,SYS_PATINFO D,SYS_COST_CENTER E,SYS_DEPT F,SYS_UNIT G "+
                    " WHERE TO_CHAR(A.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' ";
        if(!StringUtil.isNullString(deptCode)){            
        	Sql=Sql+" AND A.DEPT_CODE='"+deptCode+"' ";
		}
        if(!StringUtil.isNullString(exeDeptCode)){
            Sql=Sql+" AND A.EXE_DEPT_CODE='"+exeDeptCode+"' ";
        }
               Sql=Sql+" AND A.ORDER_CAT1_CODE<>'PLN' "+
                    " AND B.ORDER_CODE=A.ORDER_CODE "+
                    " AND C.CASE_NO=A.CASE_NO "+
                    " AND D.MR_NO=C.MR_NO "+
                    " AND E.COST_CENTER_CODE=A.EXE_DEPT_CODE "+
                    " AND F.DEPT_CODE=A.DEPT_CODE "+
                    " AND G.UNIT_CODE=A.DOSAGE_UNIT "+
                    " ORDER BY A.BILL_DATE " ;
//		System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		if(tabParm.getCount("DEPT_ABS_DESC")<0){
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
		TParm tableinparm = new TParm();
		// 删除细项方法
		int count = 0;
		double tot_amt=0;
		for (int i = 0; i < tabParm.getCount(); i++) {
			tot_amt=tot_amt+tabParm.getDouble("TOT_AMT", i);
			if (tabParm.getValue("INDV_FLG", i).length() <= 0
					|| "N".equals(tabParm.getValue("INDV_FLG", i))) { // 集合遗嘱
				tableinparm.addRowData(tabParm, i);
				count++;
			}
		}
		tableinparm.setCount(count);
		tableinParm(tabParm, tableinparm);
	    
//		System.out.println("tableinparm==="+tableinparm);
		
//		table.setHeader("计价日期,150,timestamp,yyyy/mm/dd hh:mm:ss;开单科室,100;执行科室,100;医嘱代码,100;医嘱名称,250;数量,50;单位,50,DOSAGE_UNIT;规格,80;总价,80,double,#########0.00;病案号,100;病例号,100;姓名,80");
//		table.setParmMap("BILL_DATE;DEPT_ABS_DESC;COST_CENTER_ABS_DESC;ORDER_CODE;ORDER_DESC;DOSAGE_QTY;DOSAGE_UNIT;SPECIFICATION;TOT_AMT;MR_NO;CASE_NO;PAT_NAME");
//		table.setItem("DOSAGE_UNIT");
//		table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,left;8,right;9,left;10,left;11,left");
//		table.setParmValue(tableinparm);
		this.callFunction("UI|TABLE|setParmValue", tableinparm);
		int rowNO=table.addRow();
		table.setItem(rowNO, "ORDER_DESC", "合计");
		table.setItem(rowNO, "TOT_AMT", tot_amt);
	}
	/**
	 * 显示集合医嘱金额
	 * 
	 * @param tempParm
	 *            TParm
	 * @param tableinparm
	 *            TParm
	 */
	private void tableinParm(TParm tempParm, TParm tableinparm) {

		for (int z = 0; z < tableinparm.getCount(); z++) {
			double sumTotAmt = 0.00;
			boolean isSet = false;
			for (int j = 0; j < tempParm.getCount(); j++) {
				if (("Y".equals(tempParm.getValue("INDV_FLG", j)) || tempParm
						.getValue("INDV_FLG", j).length() <= 0)
						&& tableinparm.getValue("CASE_NO", z).equals(
								tempParm.getValue("CASE_NO", j))
						&& tableinparm.getValue("ORDER_CODE", z).equals(
								tempParm.getValue("ORDERSET_CODE", j))
						&& tempParm.getValue("CASE_NO_SEQ", j).equals(
								tableinparm.getValue("CASE_NO_SEQ", z))
						&& tempParm.getValue("ORDERSET_GROUP_NO", j).equals(
								tableinparm.getValue("ORDERSET_GROUP_NO", z))) { // 集合遗嘱
					sumTotAmt += tempParm.getDouble("TOT_AMT", j);
					isSet = true;
				}
			}
			if (isSet) {
				tableinparm.setData("TOT_AMT", z, sumTotAmt);
			}
		}
	}
    // =================================排序功能开始==================================
    /**
     * 加入表格排序监听方法
     * 
     * @param table
     */
    public void addListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // 调用排序方法;
                // 转换出用户想排序的列和底层数据的列，然后判断
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
                TParm totAmtRow = tableData.getRow(tableData.getCount() - 1);// add by wanglong 20130108
                tableData.removeRow(tableData.getCount() - 1);// add by wanglong 20130108
                // System.out.println("tableData:"+tableData);
                tableData.removeGroupData("SYSTEM");
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
                TParm lastResultParm = new TParm();// 记录最终结果
                lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// 加入中间数据
                for (int k = 0; k < columnName.length; k++) {// add by wanglong 20130108
                    lastResultParm.addData(columnName[k], totAmtRow.getData(columnName[k]));
                }
                lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add by wanglong 20130108
                table.setParmValue(lastResultParm);
            }
        });
    }
    /**
     * 列名转列索引值
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

    /**
     * 得到 Vector 值
     * 
     * @param group
     *            String 组名
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int 最大行数
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
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
    private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
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
        return parmTable;
    }
    // ================================排序功能结束==================================

}
