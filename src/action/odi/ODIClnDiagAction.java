package action.odi;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

import jdo.mro.MROTool;
import jdo.adm.ADMTool;

/**
 * <p>
 * Title: 住院诊断
 * </p>
 * 
 * <p>
 * Description: 住院诊断
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.07.17
 * @version 1.0
 */

public class ODIClnDiagAction extends TAction {

	/**
	 * 执行保存事物
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		// 获得DataStore中的SQL语句
		try {
			//update by duzhw 20131011
			TParm delParm = parm.getParm("DELDATA");
			//System.out.println("action-delParm="+delParm);
			for (int j = 0; j < delParm.getCount("CASE_NO"); j++) {
				TParm del_parm=delParm.getRow(j);
				String delSql=this.delSql(del_parm);
				result=new TParm(TJDODBTool.getInstance().update(delSql, conn));
			}
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// 保存病患诊断信息表(ADM_INPDIAG)
			TParm insertParm = parm.getParm("DIAGDATA");
			for (int i = 0; i < insertParm.getCount("CASE_NO"); i++) {
				TParm inparm=insertParm.getRow(i);
				String insertsql=this.getSql(inparm);
				TParm badParm = new TParm(TJDODBTool.getInstance().update(insertsql, conn));
				if (badParm.getErrCode() < 0) {
					conn.close();
					return badParm;
				}
			}
			//duzhw add 20131017
			TParm diagParm = new TParm();
			diagParm.setData("DELDATA", delParm.getData());
            for (int i = insertParm.getCount() - 1; i >= 0; i--) {//wanglong add 20140416
                if (insertParm.getValue("STATUS", i).equals("0")) {
                    insertParm.removeRow(i);
                }
            }
			diagParm.setData("DIAGDATA", insertParm.getData());
			// 保存病患病历记录信息表(MRO_RECORD)updateMRODiag
			result = MROTool.getInstance().updateMRODiag2(diagParm, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			// 回写最近诊断
			result = ADMTool.getInstance().updateNewDaily(parm, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	

	/**
	 * 插入语句
	 * adminp_diag诊断sql
	 * @return
	 */
	private String getSql(TParm parm) {
		String icddate=StringTool.getString(parm.getTimestamp("ICD_DATE"), "yyyyMMddHHmmss");
		String icdsql=icddate.equals("")?"NULL":"TO_DATE('"+icddate+"','YYYYMMDDHH24MISS')";
		String sql = "INSERT INTO ADM_INPDIAG "
				+ "(CASE_NO, IO_TYPE, ICD_CODE, MAINDIAG_FLG, ICD_TYPE, "
				+ "SEQ_NO, MR_NO, IPD_NO, DESCRIPTION, OPT_USER, "
				//wanglong 20140416 增加状态，转归，入院病情，附加码，附加诊断
				+ "OPT_DATE, OPT_TERM, STATUS, ICD_STATUS, IN_PAT_CONDITION, ADDITIONAL_CODE, ADDITIONAL_DESC,ICD_DATE)"
				+ " VALUES "
				+ "('"+parm.getValue("CASE_NO")+"', '"+parm.getValue("IO_TYPE")+"', '"+parm.getValue("ICD_CODE")+"'," 
				+ " '"+parm.getValue("MAIN_FLG")+"', '"+parm.getValue("ICD_KIND")+"', "
				+ parm.getInt("SEQ_NO")+", '"+parm.getValue("MR_NO")+"', '"+parm.getValue("IPD_NO")+"','"+parm.getValue("DESCRIPTION")+"'," 
				+"'"+parm.getValue("OPT_USER")+"',SYSDATE, '"+parm.getValue("OPT_TERM")+
				//wanglong 20140416
				"','"+parm.getValue("STATUS")+"','"+parm.getValue("ICD_STATUS")+"','"+parm.getValue("IN_PAT_CONDITION")+
				"','"+parm.getValue("ADDITIONAL_CODE")+"','"+parm.getValue("ADDITIONAL_DESC")+"',"+icdsql+")";
		return sql;
		
	}
	/**
	 * 删除表
	 * @return
	 */
	private String delSql(TParm parm){
		String caseNo=parm.getValue("CASE_NO");
		String seqNo = parm.getValue("SEQ_NO");
		String sql="DELETE FROM ADM_INPDIAG WHERE CASE_NO='"+caseNo+"' AND SEQ_NO='"+seqNo+"'";
		return sql;	
	}
}
