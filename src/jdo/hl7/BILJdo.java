package jdo.hl7;

import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.ibs.IBSTool;
import jdo.inw.InwOrderExecTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.exception.HL7Exception;

/**
 * <p>
 * Title: 医技计费工具类
 * </p>
 * 
 * <p>
 * Description:医技计费工具类
 * </p>
 * 
 * <p>
 * Copyright: BLUECORE
 * </p>
 * 
 * <p>
 * Company:BLUECORE
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
public class BILJdo extends TJDOTool {
	/**
	 * 实例
	 */
	public static BILJdo instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return JDO
	 */
	public static BILJdo getInstance() {
		if (instanceObject == null)
			instanceObject = new BILJdo();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public BILJdo() {
		onInit();
	}

	/**
	 * 住院医技计费操作
	 * 
	 * @param conn
	 * @param parm
	 * @param Cat1Type
	 * @param flg
	 * @return
	 */
	public TParm onIBilOperate(TConnection conn, TParm parm, String Cat1Type,
			String flg
			)throws HL7Exception{
		TParm result = new TParm();
		String applyNo = "";
		if (Cat1Type.equals("LIS")) {
			applyNo = parm.getValue("LAB_NUMBER");
		} else {
			applyNo = parm.getValue("MED_APPLY_NO");
		}
		TParm dataparm = Hl7Tool.getInstance().getOpdOrder(applyNo, Cat1Type);
		if (dataparm.getCount() <= 0) {
			result.setErr(-2, "没有查询到住院执行的计费医嘱");
			return result;
		}
		result = onIBilFee(conn, dataparm,flg);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 住院医技计费
	 * 
	 * @param conn
	 * @param parm
	 * @return
	 */
	public TParm onIBilFee(TConnection conn, TParm dataparm, String flg){
		TParm result = new TParm();
		String SelSql = "SELECT CTZ1_CODE,CTZ2_CODE,CTZ3_CODE "
				+ " FROM ADM_INP WHERE CASE_NO='"
				+ dataparm.getValue("CASE_NO", 0) + "'";
		// 得到该病人所有该执行展开的处置
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(SelSql));
		if (dataparm.getCount() <= 0) {
			result.setErr(-3, "没有查询到计费身份");
			return result;
		}
		// 调用IBS接口返回费用数据
		TParm forIBSParm = new TParm();
		forIBSParm.setData("M", dataparm.getData());
		forIBSParm.setData("CTZ1_CODE", ctzParm.getData("CTZ1_CODE", 0));
		forIBSParm.setData("CTZ2_CODE", ctzParm.getData("CTZ2_CODE", 0));
		forIBSParm.setData("CTZ3_CODE", ctzParm.getData("CTZ3_CODE", 0));
		forIBSParm.setData("FLG", flg);
		TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(forIBSParm);
		if (resultFromIBS.getErrCode() < 0) {
			System.out.println(resultFromIBS.getErrText());
			conn.rollback();
			return resultFromIBS;
		}
		if (resultFromIBS.getCount() <= 0) {
			result.setErr(-4, "计费医嘱组成失败");
			return result;
		}
		TParm backWriteOdiParm = new TParm();
		Timestamp now = SystemTool.getInstance().getDate();
		// 那到行数
		int count = resultFromIBS.getCount("ORDER_DATE");
		int rows = 0;
		for (int i = 0; i < count; i++) {
			backWriteOdiParm.addData("CASE_NO", resultFromIBS.getData(
					"CASE_NO", i));
			backWriteOdiParm.addData("ORDER_NO", resultFromIBS.getData(
					"ORDER_NO", i));
			backWriteOdiParm.addData("ORDER_SEQ", resultFromIBS.getData(
					"ORDER_SEQ", i));
			backWriteOdiParm.addData("START_DTTM", resultFromIBS.getData(
					"START_DTTM", i));
			backWriteOdiParm.addData("END_DTTM", resultFromIBS.getData(
					"END_DTTM", i));
			backWriteOdiParm.addData("OPT_DATE", now);
			backWriteOdiParm.addData("OPT_USER", "MEDWEBSERVICE");
			backWriteOdiParm.addData("OPT_TERM", "127.0.0.1");
			// 操作人员就是计费人员 M/D区别字段
			backWriteOdiParm.addData("CASHIER_CODE", "MEDWEBSERVICE");
			backWriteOdiParm.addData("CASHIER_USER", "MEDWEBSERVICE");
			backWriteOdiParm.addData("CASHIER_DATE", now);
			backWriteOdiParm.addData("BILL_FLG", resultFromIBS.getData(
					"BILL_FLG", i));
			backWriteOdiParm.addData("IBS_CASE_NO_SEQ", resultFromIBS.getData(
					"CASE_NO_SEQ", i));
			// M/D区别字段
			backWriteOdiParm.addData("IBS_SEQ_NO", resultFromIBS.getData(
					"SEQ_NO", i));
			backWriteOdiParm.addData("IBS_CASE_NO", resultFromIBS.getData(
					"SEQ_NO", i));
			//赋值OPT_USER
			resultFromIBS.setData("OPT_USER", i, "MEDWEBSERVICE");
			rows++;
		}
		backWriteOdiParm.setCount(rows);
		// 回写ODI的M/D数据
		result = InwOrderExecTool.getInstance().updateOdiDspndByIBS(
				backWriteOdiParm, conn);
		if (result.getErrCode() < 0) {
			result.setErr(-5, "计费医嘱更新ODI_DSPND表失败");
			conn.rollback();
			return result;
		}
		result = InwOrderExecTool.getInstance().updateOdiDspnmByIBS(
				backWriteOdiParm, conn);
		if (result.getErrCode() < 0) {
			result.setErr(-5, "计费医嘱更新ODI_DSPNM表失败");
			conn.rollback();
			return result;
		}
		 TParm lumpParm=new TParm();
         lumpParm.setData("CASE_NO",dataparm.getValue("CASE_NO", 0));
         result=ADMInpTool.getInstance().selectall(lumpParm);//套餐病患医技管理操作集合医嘱退费需要手动退费
         if (null!=result.getValue("LUMPWORK_CODE",0)&&result.getValue("LUMPWORK_CODE",0).length()>0) {
         	 if(null!=dataparm.getValue("MED_APPLY_LUMP_FLG")&&dataparm.getValue("MED_APPLY_LUMP_FLG").equals("Y")){
         		 return result;
         	 }
		}
		// 保存后台使用的数据
		TParm forIBSParm2 = new TParm();
		forIBSParm2.setData("DATA_TYPE", "6"); // 新增webservice医技调用标记
		forIBSParm2.setData("M", resultFromIBS.getData());
		forIBSParm2.setData("FLG", flg);
		// 调用IBS提供的Tool继续执行
		result = IBSTool.getInstance().insertIBSOrder(forIBSParm2, conn);
		if (result.getErrCode() < 0) {
			result.setErr(-5, "计费医嘱插入计费档失败");
			conn.rollback();
			return result;
		}
		return result;
	}

	/**
	 * 门急诊余额判断
	 * 
	 * @param parm
	 * @param Cat1Type
	 * @return
	 */
	public TParm onOEBilCheck(TParm parm, String Cat1Type) {
		TParm result = new TParm();
		String applyNo = "";
		if (Cat1Type.equals("LIS")) {
			applyNo = parm.getValue("LAB_NUMBER");
		} else {
			applyNo = parm.getValue("MED_APPLY_NO");
		}
		TParm dataparm = Hl7Tool.getInstance().getOpdOrder(applyNo, Cat1Type);
		if (dataparm.getCount() <= 0) {
			result.setErr(-2, "没有查询到计费医嘱");
			return result;
		}
		//开关
		if (EKTIO.getInstance().ektSwitch()) {
			dataparm.setData("CASE_NO", dataparm.getValue("CASE_NO", 0));
			dataparm.setData("MR_NO", dataparm.getValue("MR_NO", 0));
			result = EKTpreDebtTool.getInstance().checkMasterForExe(dataparm);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}
}
