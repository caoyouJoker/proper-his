package com.javahis.ui.ibs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
/**
 * 介入费用明细查询
 * @author wangqing 20171226
 *
 */
public class IBSFeeDetailControl extends TControl {
	/**
	 * 费用明细table
	 */
	private TTable feeDetailTable;
	
	// 表格排序
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;
	
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		feeDetailTable = (TTable)this.getComponent("feeDetailTable");
		this.setValue("EXE_DEPT_CODE", "0306");// 执行科室默认为介入治疗中心	
		Timestamp today = SystemTool.getInstance().getDate();
		String sDateStr = StringTool.getString(today, "yyyy/MM/dd");
		String sTimeStr = "00:00:00";
		String eTimeStr = StringTool.getString(today, "HH:mm:ss");
		Timestamp sDate = StringTool.getTimestamp(sDateStr +" "+sTimeStr,
				"yyyy/MM/dd HH:mm:ss");
		Timestamp eDate = StringTool.getTimestamp(sDateStr +" "+ eTimeStr,
				"yyyy/MM/dd HH:mm:ss");
		this.setValue("START_BILL_DATE", sDate);// 开始日期
		this.setValue("END_BILL_DATE", eDate);// 结束日期
//		this.setValue("START_BILL_DATE", StringTool.rollDate(sDate, -1));
		
		// 排序监听
		addListener(feeDetailTable);
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		feeDetailTable.removeRowAll();
		String startBillDate = getValueString("START_BILL_DATE");
		String endBillDate = getValueString("END_BILL_DATE");
		// 校验查询条件
		if (startBillDate!=null && startBillDate.trim().length()>0
				&& endBillDate!=null && endBillDate.trim().length()>0) {
			if(getValueString("END_BILL_DATE").compareTo(getValueString("START_BILL_DATE")) > 0){
				
			}else{
				this.messageBox("结束时间不能大于开始时间");
				return;
			}
		}else{
			this.messageBox("查询时间区间必填");
			return;
		}
		startBillDate = StringTool.getString((Timestamp)this.getValue("START_BILL_DATE"), "yyyy/MM/dd HH:mm:ss");
		endBillDate = StringTool.getString((Timestamp)this.getValue("END_BILL_DATE"), "yyyy/MM/dd HH:mm:ss");
		
//		System.out.println("{startBillDate:"+startBillDate+"}");
//		System.out.println("{endBillDate:"+endBillDate+"}");
		
		String sql = "SELECT A.BILL_DATE,A.ORDER_CODE,A.DOSAGE_UNIT, "
				+ "B.ORDER_DESC||CASE WHEN B.SPECIFICATION IS NOT NULL  THEN '('||B.SPECIFICATION||')' ELSE '' END AS ORDER_DESC, "
				+ "A.OWN_FLG,A.DOSAGE_QTY,A.OWN_PRICE, A.TOT_AMT,A.OPT_DATE,C.USER_NAME,A.CASE_NO,A.CASE_NO_SEQ,A.SEQ_NO,"
				+ "A.ORDERSET_CODE,ORDERSET_GROUP_NO, A.INDV_FLG, D.COST_CENTER_ABS_DESC "
				
				+", E.IPD_NO, E.MR_NO, E.PAT_NAME "// add by wangqing 20171226
				
				
				+ "FROM IBS_ORDD A,SYS_FEE B,SYS_OPERATOR C,SYS_COST_CENTER D "
				
				+ ", (SELECT A.CASE_NO, A.MR_NO, A.IPD_NO, B.PAT_NAME FROM ADM_INP A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO(+)) E "// add by wangqing 20171226
				
				+ "WHERE A.ORDER_CODE =B.ORDER_CODE(+) AND A.OPT_USER = C.USER_ID(+) AND A.EXE_DEPT_CODE = D.COST_CENTER_CODE(+) "
				
				+ " AND A.CASE_NO=E.CASE_NO(+)"
				
				// add by wangqing 20180109 筛选药嘱
				+ " AND A.CAT1_TYPE='PHA' "
				
				+ " @ "
				
				+ "ORDER BY A.BILL_DATE,A.CASE_NO,A.CASE_NO_SEQ,A.SEQ_NO";
		String sql1 = "";	
		sql1 += " AND A.BILL_DATE BETWEEN TO_DATE('"+startBillDate+"', 'yyyy/MM/dd HH24:MI:SS') AND TO_DATE('"+endBillDate+"', 'yyyy/MM/dd HH24:MI:SS') ";
		String exeDeptCode = this.getValueString("EXE_DEPT_CODE");
		if(exeDeptCode!=null && exeDeptCode.trim().length()>0){
			sql1 += " AND A.EXE_DEPT_CODE='"+exeDeptCode+"' ";
		}
		String mrNo = this.getValueString("MR_NO");
		if(mrNo!=null && mrNo.trim().length()>0){
			sql1 += " AND E.MR_NO='"+mrNo+"' ";
		}
//		System.out.println("{sql1:"+sql1+"}");
		sql = sql.replace("@", sql1);
		System.out.println("{sql:"+sql+"}");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("bug:::查询费用明细出错");
			return;
		}
		feeDetailTable.setParmValue(result);
			
	}

	/**
	 * 病案号回车
	 */
	public void onMrNo(){
		// 合并病案号
		if(getValueString("MR_NO").length() > 0){
			// 补零
			String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			setValue("MR_NO", mrNo);
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));				
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
		}
		this.onQuery();
	}
	
	/**
	 * 右击MENU弹出事件
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("feeDetailTable");
		table.setPopupMenuSyntax("显示集合医嘱细项,openRigthPopMenu");
	}

	/**
	 * 打开集合医嘱细想查询
	 */
	public void openRigthPopMenu() {
		TTable table = (TTable) this.getComponent("feeDetailTable");
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getValue("CASE_NO", row);
		String orderCode = tableParm.getValue("ORDER_CODE", row);
		String orderSetCode = tableParm.getValue("ORDERSET_CODE", row);
		String caseNoSeq = tableParm.getValue("CASE_NO_SEQ", row);
		// 校验是否是集合医嘱
		if(tableParm.getValue("ORDERSET_GROUP_NO", row)==null 
				|| tableParm.getValue("ORDERSET_GROUP_NO", row).trim().length()<=0){
			return;
		}
		int orderSetGroupNo = Integer.parseInt(tableParm.getValue(
				"ORDERSET_GROUP_NO", row));
		TParm parm = getOrderSetDetails(orderSetGroupNo, orderSetCode, caseNo,
				caseNoSeq, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}
	
	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @param groupNo
	 *            int
	 * @param orderSetCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode,
			String caseNo, String caseNoSeq, String orderCode) {

		TParm result = new TParm();
		if (groupNo < 0) {
			System.out
					.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			System.out
					.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		String sql = "SELECT * FROM IBS_ORDD WHERE CASE_NO='" + caseNo
				+ "' AND CASE_NO_SEQ='" + caseNoSeq
				+ "' AND ORDERSET_GROUP_NO=" + groupNo + "";

		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = parm.getCount();
		if (count < 0) {
			//System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// temperr细项价格
		for (int i = 0; i < count; i++) {
			if (!orderCode.equals(parm.getValue("ORDER_CODE", i))
					&& orderSetCode.equals(parm.getValue("ORDERSET_CODE", i))) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// 查询单价
				TParm orderParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE,ORDER_DESC,SPECIFICATION,OPTITEM_CODE,INSPAY_TYPE "
								+ "FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				result
						.addData("OWN_PRICE", orderParm.getDouble("OWN_PRICE",
								0));
				// modify by wangbin 20141104 外部错误 #326 住院计价的显示集合医嘱细项错误 START
				// 总价直接取IBS_ORDD表的发生金额(TOT_AMT)
				result.addData("OWN_AMT", parm.getValue("TOT_AMT", i));
				// modify by wangbin 20141104 外部错误 #326 住院计价的显示集合医嘱细项错误 END
				result.addData("ORDER_DESC", orderParm
						.getValue("ORDER_DESC", 0));
				result.addData("SPECIFICATION", orderParm.getValue(
						"SPECIFICATION", 0));
				result.addData("EXEC_DEPT_CODE", parm.getValue("EXE_DEPT_CODE",
						i));
				result.addData("OPTITEM_CODE", orderParm.getValue(
						"OPTITEM_CODE", 0));
				result.addData("INSPAY_TYPE", orderParm.getValue("INSPAY_TYPE",
						0));
			}
		}
		return result;
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {//add by wanglong 20130819
		if (feeDetailTable.getRowCount()<1) {
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(feeDetailTable, "介入费用明细");
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
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
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
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = feeDetailTable.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

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
		feeDetailTable.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	
}
