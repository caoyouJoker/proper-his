package action.clp;

import java.sql.Timestamp;
import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import jdo.clp.CLPSingleDiseTool;

/**
 * <p>Title: 单病种action</p>
 *
 * <p>Description: 单病种action</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author WangLong 20120926
 * @version 1.0
 */
public class CLPSingleDiseAction extends TAction {
	
	/**
	 * 插入单病种数据
	 * @param saveParm
	 * @return
	 */
	public TParm insertSDData(TParm parm) {
		TConnection connection = getConnection();
		TParm sdParm = new TParm();
		sdParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));// 门级别
		sdParm.setData("DISE_CODE", parm.getValue("DISE_CODE"));// 单病种分类
		sdParm.setData("CASE_NO", parm.getValue("CASE_NO"));// 就诊号
		sdParm.setData("MR_NO", parm.getValue("MR_NO"));// 病案号
		sdParm.setData("IPD_NO", parm.getValue("IPD_NO"));// 住院号
		sdParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));// 姓名
		sdParm.setData("SEX_CODE", parm.getValue("SEX_CODE"));// 性别
		sdParm.setData("AGE", parm.getValue("AGE"));// 年龄
		//Timestamp inDate = parm.getTimestamp("IN_DATE");
		if (parm.getData("IN_DATE") == null) {
			sdParm.setData("IN_DATE", new TNull(Timestamp.class));// 入院日期
		} else{
			sdParm.setData("IN_DATE", parm.getTimestamp("IN_DATE"));// 入院日期
		}
		// Timestamp outDate = parm.getTimestamp("OUT_DATE");
		if (parm.getData("OUT_DATE") == null) {
			sdParm.setData("OUT_DATE", new TNull(Timestamp.class));// 出院日期
		} else {
			sdParm.setData("OUT_DATE", parm.getTimestamp("OUT_DATE"));// 出院日期
		}
		sdParm.setData("STAY_DAYS", parm.getValue("STAY_DAYS"));// 住院天数
		sdParm.setData("ICD_CODE", parm.getValue("ICD_CODE"));// 诊断代码
		sdParm.setData("ICD_CHN_DESC", parm.getValue("ICD_CHN_DESC"));// 诊断名称
		sdParm.setData("TBYS", parm.getValue("TBYS"));// 填表医师
		sdParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		sdParm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
		sdParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		// sdParm.setData("FILE_NO", parm.getValue("FILE_NO"));
		sdParm.setData("FILE_PATH", parm.getValue("FILE_PATH"));
		sdParm.setData("FILE_NAME", parm.getValue("FILE_NAME"));
		TParm result = new TParm();
		result = CLPSingleDiseTool.getInstance().deleteSDData(sdParm, connection);// 删除旧数据
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		result = CLPSingleDiseTool.getInstance().insertSDData(sdParm, connection);// 插入新数据
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	
	/**
	 * 单病种历史病历合并
	 */
	public TParm mergeEMRhistory(TParm actionParm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
//		result = CLPSingleDiseTool.getInstance().clearSDDBHistory(actionParm, connection);//删除旧的单病种主档记录
//		if (result.getErrCode() < 0) {
//			connection.rollback();
//			connection.close();
//			return result;
//		}
//        result = CLPSingleDiseTool.getInstance().clearSDFileHistory(actionParm, connection);// 删除旧的病历文件记录
//        if (result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
		result = CLPSingleDiseTool.getInstance().copySDDBHistory(actionParm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			result.setErrText("从数据库复制单病种记录出错");
			return result;
		}
		result = CLPSingleDiseTool.getInstance().copySDFileHistory(actionParm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			result.setErrText("从数据库复制病历记录出错");
			return result;
		}
		if(!CLPSingleDiseTool.getInstance().copySDEMRFile(actionParm)){
			connection.rollback();
			connection.close();
			result.setErrCode(-1);
			result.setErrText("复制病历文件出错");
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	
}
