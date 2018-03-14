package action.emr;

import jdo.mro.EMRSortDicTool;
import jdo.sys.Operator;

import org.apache.commons.lang.StringUtils;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;

public class EMRTransferWordAction extends TAction {
    public EMRTransferWordAction() {
    	
    }

    public TParm saveTransferFile(TParm parm) {
    	TConnection connection = getConnection();
		//		System.out.println("saveTransferFile======"+parm);
		TParm result = new TParm();
		String optUser = parm.getValue("OPT_USER");//操作人员
		String optTerm = parm.getValue("OPT_TERM");//操作终端
		String transferCode = parm.getValue("TRANSFER_CODE");//交接单号
		String transferFilePath = parm.getValue("TRANSFER_FILE_PATH");//文件路径
		String transferFileName = parm.getValue("TRANSFER_FILE_NAME");//文件名称
		String mrNo =  parm.getValue("MR_NO");//病案号
		String caseNo =  parm.getValue("CASE_NO");//就诊号
		String patName =  parm.getValue("PAT_NAME");//姓名
		String fromDept =  parm.getValue("FROM_DEPT");//转出科室
		String toDept =  parm.getValue("TO_DEPT");//转入科室
		String statusFlg = parm.getValue("STATUS_FLG");//接收状态
		String transferClass = parm.getValue("TRANSFER_CLASS");//交接单类型
		String fromUser = parm.getValue("FROM_USER");
		String toUser = parm.getValue("TO_USER");
		if ("NEW".equals(parm.getValue("ONLY_EDIT_TYPE"))) {
			if(StringUtils.isEmpty(parm.getValue("TRANSFER_NO"))){
				String sql= " INSERT INTO INW_TRANSFERSHEET(TRANSFER_CODE,TRANSFER_FILE_PATH," +
						" TRANSFER_FILE_NAME,MR_NO,CASE_NO,PAT_NAME,FROM_DEPT,TO_DEPT," +
						" STATUS_FLG,TRANSFER_CLASS,CRE_USER,CRE_DATE," +
						" OPT_USER,OPT_DATE,OPT_TERM,OPBOOK_SEQ,OP_DESC)"+ 
						" VALUES ('"+ transferCode+ "','"+ transferFilePath+"','"+transferFileName+ "'," +
						" '"+ mrNo+ "','"+ caseNo+ "','"+ patName+ "','"+ fromDept+ "','"+ toDept+ "'," +
						" '"+ statusFlg+ "','"+ transferClass+ "'," +
						" '"+optUser+ "',SYSDATE,'"+optUser+ "',SYSDATE,'"+optTerm+ "','"+parm.getValue("OPBOOK_SEQ")+"','"+parm.getValue("OP_DESC")+"')";
				//            System.out.println("sql=========="+sql); 	
				result = new TParm(TJDODBTool.getInstance().update(sql,connection));
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				String unlockRoomSql = "UPDATE OPE_IPROOM SET OPBOOK_SEQ= NULL,OPT_USER='"+optUser+"',OPT_DATE=SYSDATE,OPT_TERM='"+optTerm+"' WHERE OPBOOK_SEQ='"+parm.getValue("OPBOOK_SEQ")+"'";
				result = new TParm(TJDODBTool.getInstance().update(unlockRoomSql,connection));
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
			}
		}else if ("ONLYONE".equals(parm.getValue("ONLY_EDIT_TYPE"))) {
			String sql= " UPDATE INW_TRANSFERSHEET SET STATUS_FLG ='"+ statusFlg + "'," +
					" FROM_USER = '"+fromUser+ "'," +
					" TO_USER = '"+toUser+ "'," +
					" TRANSFER_DATE =SYSDATE" +
					" WHERE TRANSFER_CODE = '"+transferCode+ "'";
			result = new TParm(TJDODBTool.getInstance().update(sql,connection));
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

}
