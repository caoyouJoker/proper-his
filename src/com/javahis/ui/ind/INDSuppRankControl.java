package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Calendar;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 供应商排名Control</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lij 2016.11.3
 * @version 1.0
 */

public class INDSuppRankControl extends TControl{
	TTable table;
	public INDSuppRankControl(){
	}
	/**
     * 初始化方法
     */
	public void onInit(){
		super.onInit();
		initDate();
		table = (TTable)this.getComponent("TABLE_S");
	}
	public void initDate(){
		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月31)
		setValue("END_DATE", dateTime);

		// 起始时间(本月1号)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
		//设置区域
		setValue("ORG_CODE", "040101");
	}
	/**
	 * 根据科室以及验收时间进行查询
	 */
	public void onQuery(){
		String org_code=this.getValueString("ORG_CODE");
		org_code="in ("+org_code+") ";
        
		String startDate = this.getValueString("START_DATE");
    	String endDate = this.getValueString("END_DATE");
    	String verifyin_date = "";
    	startDate = startDate.substring(0, 19).replace(" ", "").replace("/", "").
        replace(":", "").replace("-", "");
    	verifyin_date +=" AND A.VERIFYIN_DATE > TO_DATE('" + startDate +
        "','YYYYMMDDHH24MISS') ";
    	endDate = endDate.substring(0, 19).replace(" ", "").replace("/", "").
        replace(":", "").replace("-", "");
    	verifyin_date +=" AND A.VERIFYIN_DATE < TO_DATE('" + endDate +
        "','YYYYMMDDHH24MISS') ";
		String sql = "SELECT G.ORDER_CODE AS ORDER_CODE, D.ORDER_DESC AS ORDER_DESC, D.GOODS_DESC AS GOODS_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC," +
       "H.DOSE_CHN_DESC, I.ROUTE_CHN_DESC, SUM(B.VERIFYIN_QTY) AS QTY, B.VERIFYIN_PRICE," +
       "SUM(B.VERIFYIN_QTY) * B.VERIFYIN_PRICE AS VER_AMT, B.RETAIL_PRICE AS OWN_PRICE," +
       "B.RETAIL_PRICE * SUM(B.VERIFYIN_QTY) AS OWN_AMT," +
       "B.RETAIL_PRICE * SUM(B.VERIFYIN_QTY) - SUM(B.VERIFYIN_QTY) * B.VERIFYIN_PRICE AS DIFF_AMT," +
       "D.MAN_CODE, J.SUP_CHN_DESC " +
       "FROM IND_VERIFYINM A, IND_VERIFYIND B, SYS_FEE D, SYS_UNIT E, PHA_TRANSUNIT F," +         
       "PHA_BASE G, PHA_DOSE H, SYS_PHAROUTE I, SYS_SUPPLIER J " +
       "WHERE A.VERIFYIN_NO = B.VERIFYIN_NO " +
       "AND B.ORDER_CODE = D.ORDER_CODE " +
       "AND B.BILL_UNIT = E.UNIT_CODE " +
       "AND B.ORDER_CODE = F.ORDER_CODE " +
       "AND D.ORDER_CODE = F.ORDER_CODE " +
       "AND B.UPDATE_FLG IN ('3') " +
	   "AND B.ORDER_CODE = G.ORDER_CODE " +
	   "AND D.ORDER_CODE = G.ORDER_CODE " +
	   "AND G.DOSE_CODE = H.DOSE_CODE(+) " +
	   "AND G.ROUTE_CODE = I.ROUTE_CODE " +
	   "AND F.ORDER_CODE = F.ORDER_CODE " +
	   "AND A.ORG_CODE " +org_code+
	   "AND A.SUP_CODE = J.SUP_CODE " +verifyin_date+
	   "GROUP BY G.ORDER_CODE, D.GOODS_DESC, D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, B.RETAIL_PRICE, B.ORDER_CODE," +
	   "D.MAN_CODE, H.DOSE_CHN_DESC, I.ROUTE_CHN_DESC, J.SUP_CHN_DESC " +
       "ORDER BY B.ORDER_CODE";
//		System.out.println(sql);
		TParm newdata = new TParm(TJDODBTool.getInstance().select(sql));
		if(newdata.getErrCode() < 0 ){
    		this.messageBox(newdata.getErrText());
    		return;
    	}
        if(newdata.getCount() <= 0)
        {
        	this.messageBox("查无数据");
        }
		//在table中显示查询信息
        table.setParmValue(newdata);
	}
	/**
	 * 打印
	 */
	public void onPrint(){
		TTable table = this.getTable("TABLE_S");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		} else {
			TParm parm = new TParm();
			parm.setData("TITLE", "TEXT",  Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion())
					+"供应商排名报表");
			parm.setData("ORG_CODE", "TEXT",
                    "验收部门: " +
                    this.getTTextFormat("ORG_CODE").getText());
			String start_date = getValueString("START_DATE");
			String end_date = getValueString("END_DATE");
			parm.setData("DATE_AREA", "TEXT", "验收区间: "
					+ start_date.substring(0, 4) + "/"
					+ start_date.substring(5, 7) + "/"
					+ start_date.substring(8, 10) + " "
					+ start_date.substring(11, 13) + ":"
					+ start_date.substring(14, 16) + ":"
					+ start_date.substring(17, 19) + " ~ "
					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
					+ "/" + end_date.substring(8, 10) + " "
					+ end_date.substring(11, 13) + ":"
					+ end_date.substring(14, 16) + ":"
					+ end_date.substring(17, 19));
			
			TParm result = table.getShowParmValue();
			result.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			result.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
			result.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			result.addData("SYSTEM", "COLUMNS", "DOSE_CHN_DESC");
			result.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
			result.addData("SYSTEM", "COLUMNS", "QTY");
			result.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
			result.addData("SYSTEM", "COLUMNS", "VER_AMT");
			result.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			result.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			result.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
			result.addData("SYSTEM", "COLUMNS", "MAN_CODE");
			result.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
			
//			for (int i = 0; i < table.getRowCount(); i++) {
//				parm.addData("ORDER_CODE",result.getValue("ORDER_CODE",i));
//				parm.addData("ORDER_DESC",result.getValue("ORDER_DESC",i));
//				parm.addData("GOODS_DESC",result.getValue("GOODS_DESC",i));
//				parm.addData("SPECIFICATION",result.getValue("SPECIFICATION",i));
//				parm.addData("UNIT_CHN_DESC",result.getValue("UNIT_CHN_DESC",i));
//				parm.addData("DOSE_CHN_DESC",result.getValue("DOSE_CHN_DESC",i));
//				parm.addData("ROUTE_CHN_DESC",result.getValue("ROUTE_CHN_DESC",i));
//				parm.addData("QTY",result.getValue("QTY",i));
//				parm.addData("VERIFYIN_PRICE",result.getValue("VERIFYIN_PRICE",i));
//				parm.addData("VER_AMT",result.getValue("VER_AMT",i));
//				parm.addData("OWN_PRICE",result.getValue("OWN_PRICE",i));
//				parm.addData("OWN_AMT",result.getValue("OWN_AMT",i));
//				parm.addData("DIFF_AMT",result.getValue("DIFF_AMT",i));
//				parm.addData("MAN_CODE",result.getValue("MAN_CODE",i));
//				parm.addData("SUP_CHN_DESC",result.getValue("SUP_CHN_DESC",i));
//			}
//			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
//			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//			parm.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
//			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//			parm.addData("SYSTEM", "COLUMNS", "DOSE_CHN_DESC");
//			parm.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
//			parm.addData("SYSTEM", "COLUMNS", "QTY");
//			parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
//			parm.addData("SYSTEM", "COLUMNS", "VER_AMT");
//			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
//			parm.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
//			parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
//			parm.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
//			
			parm.setData("DATE", "TEXT", "制表时间: "
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			parm.setData("USER", "TEXT", "制表人: " + Operator.getName());
			parm.setData("ISR", result.getData());
			this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDSuppRand.jhw",
					parm);
		}
	}
	/**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "ORG_CODE,START_DATE,END_DATE";
        this.clearValue(clearStr);

		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/31 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月25)
		setValue("END_DATE", dateTime);

		// 起始时间(上个月26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, 0);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/01 00:00:00");
        setValue("ORG_CODE", "040101");
		TTable  table = this.getTable("TABLE_S");
        table.removeRowAll();
    }
	/**
     * 汇出Excel
     */
    public void onExport() {
        TTable table = this.getTable("TABLE_S");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "供应商排名报表");
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
     * 得到TTextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
}
