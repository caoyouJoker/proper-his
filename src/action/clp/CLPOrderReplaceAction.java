package action.clp;

import jdo.clp.CLPOrderReplaceTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>
 * Title: 路径医嘱替换
 * </p>
 * 
 * <p>
 * Description: 临床路径医嘱替换操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 2014.08.29
 * @version 4.0
 */
public class CLPOrderReplaceAction extends TAction{
	
	/**
	 * 替换界面保存操作
	 * @return
	 */
	public TParm onSave(TParm parm){
		TConnection conn = getConnection();
		TParm result=new TParm();
		String clncpathCode=parm.getValue("CLNCPATH_CODE",0);//临床路径代码
		//String schdCode=parm.getValue("SCHD_CODE",0);//时程
		TParm clnParm=new TParm();
		TParm temp=new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if (clncpathCode.equals(parm.getValue("CLNCPATH_CODE",i))) {
				//相同路径的医嘱一起操作
				clnParm.addRowData(parm, i);
			}else{
				temp.setData("CLNCPATH_CODE",clncpathCode);
				result=getOnsaveParm(clnParm, temp, result, conn, parm, i);
				if (result.getErrCode()<0) {
					conn.rollback();
					conn.close();
					return result;
				}
				clncpathCode=parm.getValue("CLNCPATH_CODE",i);
				clnParm=new TParm();
				clnParm.addRowData(parm, i);
			}
			if (i==parm.getCount()-1) {
				temp.setData("CLNCPATH_CODE",clncpathCode);
				result=getOnsaveParm(clnParm, temp, result, conn, parm, i);
				if (result.getErrCode()<0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 公用方法
	 * @param clnParm
	 * @param temp
	 * @param result
	 * @param conn
	 * @param parm
	 * @param i
	 * @return
	 */
	private TParm  getOnsaveParm(TParm clnParm,TParm temp,TParm result,
			TConnection conn,TParm parm,int i){
		TParm clpPackParm=CLPOrderReplaceTool.getInstance().selectClpPackSum(temp);//查询所有医嘱
		int version=clpPackParm.getInt("VERSION",0);//获得版本号码
		for (int j = 0; j < clnParm.getCount("CLNCPATH_CODE"); j++) {//相同的临床路径代码
			//修改路径医嘱
			result=CLPOrderReplaceTool.getInstance().updateClpPack(clnParm.getRow(j), conn);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		temp.setData("VERSION",version+1);//修改版本号码
		temp.setData("OPT_USER",parm.getValue("OPT_USER_T"));
		temp.setData("OPT_TERM",parm.getValue("OPT_TERM_T"));
		result=CLPOrderReplaceTool.getInstance().updateClpPackVersion(temp, conn);
		if (result.getErrCode()<0) {
			return result;
		}
		result=CLPOrderReplaceTool.getInstance().updateClpBscinfoVersion(temp, conn);
		if (result.getErrCode()<0) {
			return result;
		}
		temp.setData("VERSION_NEW",version);//原来版本号码
		//同步路径医嘱历史表数据
		//更新历史表版本号码
		result=CLPOrderReplaceTool.getInstance().updateClpPackHistoryVersion(temp, conn);
		if (result.getErrCode()<0) {
			return result;
		}
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMddhhmm");
		TParm clpHistoryParm=new TParm();
		for (int j = 0; j < clnParm.getCount("CLNCPATH_CODE"); j++) {//相同的临床路径代码					
			clpHistoryParm.setData("VERSION_NEW",version);
			clpHistoryParm.setData("CLNCPATH_CODE",clnParm.getValue("CLNCPATH_CODE",j));
			clpHistoryParm.setData("SCHD_CODE",clnParm.getValue("SCHD_CODE",j));
			clpHistoryParm.setData("ORDER_TYPE",clnParm.getValue("ORDER_TYPE",j));
			clpHistoryParm.setData("CHKTYPE_CODE",clnParm.getValue("CHKTYPE_CODE",j));
			clpHistoryParm.setData("ORDER_SEQ_NO",clnParm.getValue("ORDER_SEQ_NO",j));
			clpHistoryParm.setData("ORDER_CODE_OLD",clnParm.getValue("ORDER_CODE_OLD",j));
			clpHistoryParm.setData("END_DATE",sysDate+"00");
			clpHistoryParm.setData("VERSION",version+1);
			clpHistoryParm.setData("START_DATE",sysDate+"01");
			//修改路径医嘱
			result=CLPOrderReplaceTool.getInstance().updateClpPackHistory(clpHistoryParm, conn);
			if (result.getErrCode()<0) {
				return result;
			}
			clpHistoryParm.setData("END_DATE","99991231235959");
			clpHistoryParm.setData("ORDER_CODE",clnParm.getValue("ORDER_CODE",j));
			clpHistoryParm.setData("DOSE",clnParm.getDouble("MEDI_QTY",j));
			clpHistoryParm.setData("REGION_CODE",clnParm.getValue("REGION_CODE",j));
			clpHistoryParm.setData("DOSE_UNIT",clnParm.getValue("UNIT_CODE",j));
			clpHistoryParm.setData("ROUT_CODE",clnParm.getValue("ROUTE_CODE",j));
			clpHistoryParm.setData("FREQ_CODE",clnParm.getValue("FREQ_CODE",j));
			clpHistoryParm.setData("DOSE_DAYS",clnParm.getValue("DOSE_DAYS",j));
			clpHistoryParm.setData("NOTE",clnParm.getValue("NOTE",j));
			clpHistoryParm.setData("ORDER_FLG",clnParm.getValue("ORDER_FLG",j));
			clpHistoryParm.setData("SEQ",clnParm.getValue("SEQ",j));
			clpHistoryParm.setData("RBORDER_DEPT_CODE",clnParm.getValue("RBORDER_DEPT_CODE",j));
			clpHistoryParm.setData("URGENT_FLG",clnParm.getValue("URGENT_FLG",j));
			clpHistoryParm.setData("CHKUSER_CODE",clnParm.getValue("CHKUSER_CODE",j));
			clpHistoryParm.setData("EXEC_FLG",clnParm.getValue("EXEC_FLG",j));
			clpHistoryParm.setData("ORDTYPE_CODE",clnParm.getValue("ORDTYPE_CODE",j));
			clpHistoryParm.setData("STANDARD",clnParm.getValue("STANDARD",j));
			clpHistoryParm.setData("OPT_USER",parm.getValue("OPT_USER_T"));
			clpHistoryParm.setData("OPT_TERM",parm.getValue("OPT_TERM_T"));
			clpHistoryParm.setData("START_DAY",clnParm.getValue("START_DAY",j));
			clpHistoryParm.setData("OWN_PRICE",clnParm.getDouble("OWN_PRICE",j));
			//添加新历史路径医嘱
			result=CLPOrderReplaceTool.getInstance().insertClpPackHistory(clpHistoryParm, conn);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		return result;
	}
}
