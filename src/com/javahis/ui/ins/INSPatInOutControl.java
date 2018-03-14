package com.javahis.ui.ins;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

public class INSPatInOutControl extends TControl{
	
	TTable table;
	private int radio = 1;//radio
	private int row = -1;//选中行
	
    public void onInit() {
    	Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		// 入院开始时间
		setValue("START_DATE", yesterday);
		// 入院结束时间
		setValue("END_DATE", SystemTool.getInstance().getDate());
    	table = (TTable)this.getComponent("TABLE");
    }
    /**
     * radio监听
     * @param obj
     */
    public void onCheck(Object obj) {
    	if ("1".equals(obj.toString())) {
    		radio = 1;
    		this.setEnable("YEAR_MON",false);
    		this.setEnable("START_DATE",true);
    		this.setEnable("END_DATE",true);
    		this.setEnable("DEPT",true);
        }
    	if ("2".equals(obj.toString())) {
    		radio = 2;
    		this.setEnable("START_DATE",false);
    		this.setEnable("END_DATE",false);
    		this.setEnable("DEPT",false);
    		this.setEnable("YEAR_MON",true);
        }
    	onClear();
    }
    /**
     * 设置为不能编辑
     * @param tag
     */
    public void setEnable(String tag,boolean flg){
    	TTextFormat date = (TTextFormat)getComponent(tag);
		date.setEnabled(flg);
    }
    /**
     * 查询
     */
    public void onQuery(){
    	StringBuilder sql = null;
    	String startDate = "";
    	String endDate = "";
    	if(radio==1){
    		String dept = getValueString("DEPT");
    		if (!"".equals(this.getValueString("START_DATE")) &&
    	            !"".equals(this.getValueString("END_DATE"))) {
    			startDate = getValueString("START_DATE").substring(0, 19);
    			endDate = getValueString("END_DATE").substring(0, 19);
    			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
    			startDate.substring(8, 10);
    		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
    			endDate.substring(8, 10);
    		}
    		sql = new StringBuilder();
    		sql.append("SELECT DISTINCT YEAR_MON, NHI_NUM FROM INS_IBS WHERE 1=1 ");
    		if (!"".equals(this.getValueString("START_DATE")) &&
    	            !"".equals(this.getValueString("END_DATE"))) {
        		sql.append(" AND IN_DATE BETWEEN TO_DATE('"+startDate+
        		"000000','YYYYMMDDHH24MISS') AND TO_DATE('"+startDate+
        		"235959','YYYYMMDDHH24MISS') AND DS_DATE BETWEEN TO_DATE('"+endDate+
        		"000000','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+
        		"235959','YYYYMMDDHH24MISS') ");
    		}
    		if(!"".equals(getValueString("DEPT"))&&getValueString("DEPT")!=null){
    			sql.append(" AND DEPT_CODE = '"+dept+"' ");
    		}
    		sql.append(" AND NHI_NUM IS NOT NULL");
    		
    	}
    	if(radio==2){
    		String year_mon = getValueString("YEAR_MON");
    		if(!"".equals(year_mon)){
    			year_mon = year_mon.substring(0, 19);
    			year_mon = year_mon.substring(0, 4) + year_mon.substring(5, 7);
    		}
    		sql = new StringBuilder();
    		sql.append("SELECT DISTINCT YEAR_MON, NHI_NUM FROM INS_IBS WHERE YEAR_MON = '"+
            		year_mon+"' AND NHI_NUM IS NOT NULL");
    	}
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    	}
    	if(result.getCount()<0){
    		messageBox("无数据");
    	}
    	this.callFunction("UI|TABLE|setParmValue", result);
    }
    /**
     * 清空
     */
    public void onClear(){
    	clearValue("YEAR_MON;START_DATE;END_DATE;DEPT");
    }
    /**
     * 打印
     */
	public void onPrint() {
		TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
		row = table.getSelectedRow();
		if (row == -1) {
			messageBox("请选择要补印的记录");
			return;
		} else {
			String nhiNum = table.getValueAt(row, 1).toString();
			TParm result = query(nhiNum);
			double arAmts = 0.00;
			double zifeis = 0.00;
			double shenbaos = 0.00;
			double jufus = 0.00;
			double nhiPays = 0.00;
			double nhiComments = 0.00;
			double nhiPayReals = 0.00;
			double nhiComment1s = 0.00;
			for (int i = 0; i < result.getCount(); i++) {
				arAmts = arAmts + result.getDouble("AR_AMT", i);
				zifeis = zifeis + result.getDouble("ZIFAI", i);
				shenbaos = shenbaos + result.getDouble("SHENBAO", i);
				jufus = jufus + result.getDouble("JUFU", i);
				nhiPays = nhiPays + result.getDouble("NHI_PAY", i);
				nhiComments = nhiComments + result.getDouble("NHI_COMMENT", i);
				nhiPayReals = nhiPayReals + result.getDouble("NHI_PAY_REAL", i);
				nhiComment1s = nhiComment1s + result.getDouble("NHI_COMMENT_1", i);
			}
			TParm data = new TParm();// 打印的数据
			data.setData("NHI_NUM", "TEXT", "报盘批号: "+nhiNum);
			String date = SystemTool.getInstance().getDate().toString();
			data.setData("PRINTDATE", "TEXT", "创表日期: "+date.substring(0, 4)+
	    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
			data.setData("OPT_USER", "TEXT", "创表人: "+Operator.getName());
			data.setData("AR_AMTS", "TEXT", arAmts);
			data.setData("ZIFAIS", "TEXT", zifeis);
			data.setData("SHENBAOS", "TEXT", shenbaos);
			data.setData("JUFUS", "TEXT", jufus);
			data.setData("NHI_PAYS", "TEXT", nhiPays);
			data.setData("NHI_COMMENTS", "TEXT", nhiComments);
			data.setData("NHI_PAY_REALS", nhiPayReals);
			data.setData("NHI_COMMENT_1S", nhiComment1s);
			data.setData("TABLE", result.getData());
			this.openPrintDialog("%ROOT%\\config\\prt\\INS\\INSPatInOut.jhw",data);
		}
	}
    /**
     * 查询打印结果
     * @param nhiNum
     * @return
     */
    public TParm query(String nhiNum){
    	String sql =
			   "SELECT I.MR_NO, M.CONFIRM_NO, M.PAT_NAME, S.CHN_DESC, A.CATEGORY_CHN_DESC, "+
			   " C.CTZ_DESC, M.STATION_DESC, M.INP_TIME INCOUNT, " +
			   " TO_CHAR(I.IN_DATE, 'YYYYMMDD') AS IN_DATE, TO_CHAR(I.DS_DATE, 'YYYYMMDD') AS DS_DATE, " +
			   " TO_CHAR(I.DS_DATE, 'YYYYMMDD') AS CHARGE_DATE, " +
			   " TO_DATE(I.DS_DATE, 'YYYYMMDD') - TO_DATE(I.IN_DATE, 'YYYYMMDD') AS INDATE, " +
			   " (PHA_AMT+EXM_AMT+TREAT_AMT+OP_AMT+BED_AMT+MATERIAL_AMT+OTHER_AMT+BLOODALL_AMT+BLOOD_AMT) AR_AMT, " +
			   " (M.RESTART_STANDARD_AMT+M.STARTPAY_OWN_AMT+M.OWN_AMT+M.PERCOPAYMENT_RATE_AMT+M.ADD_AMT+" +
			   "INS_HIGHLIMIT_AMT+M.TRANBLOOD_OWN_AMT) ZIFAI, " +
			   "(M.PHA_NHI_AMT + M.EXM_NHI_AMT + M.TREAT_NHI_AMT + M.OP_NHI_AMT + M.BED_NHI_AMT +" +
			   " M.MATERIAL_NHI_AMT + M.OTHER_NHI_AMT + M.BLOODALL_NHI_AMT + M.BLOOD_NHI_AMT) SHENBAO, " +
			   " (M.NHI_PAY - M.NHI_PAY_REAL) AS JUFU, M.NHI_PAY, M.NHI_COMMENT, M.NHI_PAY_REAL,M.NHI_COMMENT " +
			   " FROM INS_IBS M, ADM_INP I, SYS_CTZ C, " +
			   "(SELECT CHN_DESC,ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX') S, SYS_CATEGORY A " +
			   " WHERE M.REGION_CODE = I. REGION_CODE AND M.CTZ1_CODE = C.CTZ_CODE AND M.CASE_NO = I.CASE_NO " +
			   " AND M.SEX_CODE = S.ID AND A.CATEGORY_CODE = M.ADM_CATEGORY AND M.NHI_NUM = '"+nhiNum+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return result;
    	}
    	if(result.getCount()<0){
    		messageBox("查无数据");
    		return result;
    	}
    	return result;
    }

}
