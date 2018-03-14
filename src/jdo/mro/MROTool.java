package jdo.mro;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import jdo.adm.ADMTransLogTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.OdiUtil;
import com.javahis.util.OdoUtil;

/**
 * <p>
 * Title: MRO接口方法(后台Tool，前后台都要部署)
 * </p>
 * 
 * <p>
 * Description: MRO接口方法(后台Tool，前后台都要部署)
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-4-28
 * @version 1.0
 */
public class MROTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static MROTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return SYSRegionTool
	 */
	public static MROTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MROTool();
		return instanceObject;
	}

	public MROTool() {
		setModuleName("mro\\MROToolModule.x");
		onInit();
	}

	/**
	 * 转床、转出科
	 * 
	 * @param parm
	 *            TParm 参数信息： CASE_NO,OPT_USER,OPT_TERM为必须参数，其他 诊断参数自选 ==>
	 *            TRANS_DEPT 转科科别 OUT_DATE 出院日期 OUT_DEPT 出院科别 OUT_STATION 出院病区
	 *            OUT_ROOM_NO 出院病室 REAL_STAY_DAYS 实际住院天数
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTransDept(TParm parm, TConnection conn) {
		TParm result = new TParm();
		TParm nursParm=this.getIbsNusParm(parm);
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("OUT_DATE") != null) // 出院日期
		{
			sql += " OUT_DATE=TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("OUT_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS'),";
			sql += " BILCHK_FLG='Y',";
		}
		if (parm.getData("IN_ROOM_NO") != null) // 入院病室
			sql += " IN_ROOM_NO='" + parm.getValue("IN_ROOM_NO") + "',";
		if (parm.getData("OUT_DEPT") != null) // 出院科别
			sql += " OUT_DEPT='" + parm.getValue("OUT_DEPT") + "',";
		if (parm.getData("OUT_STATION") != null) // 出院病区
			sql += " OUT_STATION='" + parm.getValue("OUT_STATION") + "',";
		if (parm.getData("OUT_ROOM_NO") != null) // 出院病室
			sql += " OUT_ROOM_NO='" + parm.getValue("OUT_ROOM_NO") + "',";
		if (parm.getData("REAL_STAY_DAYS") != null) // 实际住院天数
			sql += " REAL_STAY_DAYS='" + parm.getValue("REAL_STAY_DAYS") + "',";
		if (!parm.getValue("VS_NURSE_CODE").equals("")) // 责任护士
			sql += " VS_NURSE_CODE='" + parm.getValue("VS_NURSE_CODE") + "',";
		if (!parm.getValue("DIRECTOR_DR_CODE").equals("")) // 科主任
			sql += " DIRECTOR_DR_CODE='" + parm.getValue("DIRECTOR_DR_CODE")+ "',";
		if (!parm.getValue("ATTEND_DR_CODE").equals("")) // 主治医师
			sql += " ATTEND_DR_CODE='" + parm.getValue("ATTEND_DR_CODE") + "',";
		if (!parm.getValue("VS_DR_CODE").equals("")) // 经治医师
			sql += " VS_DR_CODE='" + parm.getValue("VS_DR_CODE") + "',";
		if (!parm.getValue("CLNCPATH_CODE").equals("")) // 临床路径 wanglong add 20140604
            sql += " CLNCPATH_CODE='" + parm.getValue("CLNCPATH_CODE") + "',";
		if (nursParm.getData("N0") != null) // 特级护理天数
			sql += " SPENURS_DAYS='" + nursParm.getValue("N0") + "',";
		if (nursParm.getData("N1") != null) // 一级护理天数
			sql += " FIRNURS_DAYS='" + nursParm.getValue("N1") + "',";
		if (nursParm.getData("N2") != null) // 二级护理天数
			sql += " SECNURS_DAYS='" + nursParm.getValue("N2") + "',";
		if (nursParm.getData("N3") != null) // 三级护理天数
			sql += " THRNURS_DAYS='" + nursParm.getValue("N3") + "',";
		if (nursParm.getData("03") != null) // 呼吸机小时
			sql += " VENTI_TIME=" + nursParm.getInt("03") + ",";
		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		if (parm.getData("CASE_NO") != null)
			sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateTransDept==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 得到计费费中各级护理天数
	 * 
	 * @return
	 */
	public TParm getIbsNusParm(TParm inparm) {
		TParm result = new TParm();
		//初始化
		TParm parm=new TParm();
		String caseNo = inparm.getValue("CASE_NO");
		String sql = " SELECT  SUM(A.TAKE_DAYS*A.DOSAGE_QTY) AS QTY,B.ORD_SUPERVISION FROM IBS_ORDD A,SYS_FEE B "
				+ " WHERE   A.CASE_NO='"
				+ caseNo
				+ "'"
				+ " AND A.ORDER_CODE=B.ORDER_CODE "
				+ " AND  B.ORD_SUPERVISION IN ('N0','N1','N2','N3','03')"
				+ " GROUP BY B.ORD_SUPERVISION";
		result=new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return parm;
		}
		if(result.getCount()<0)
			return parm;
		for(int i=0;i<result.getCount();i++){
			if(result.getValue("ORD_SUPERVISION", i).equals("N0")){
				parm.setData("N0", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N1")){
				parm.setData("N1", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N2")){
				parm.setData("N2", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N3")){
				parm.setData("N3", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("03")){
				parm.setData("03", result.getInt("QTY", i));
			}
		}
		return parm;

	}
	/**
	 * 修改病案诊断
	 * 
	 * @param parm
	 *            TParm 必须参数：CASE_NO 参数说明： OE_DIAG_CODE:门急诊诊断CODE
	 *            IN_DIAG_CODE：入院诊断CODE OUT_DIAG_CODE1：出院主诊断CODE
	 *            CODE1_REMARK：出院主诊断备注 CODE1_STATUS：出院主诊断转归状态
	 *            OUT_DIAG_CODE2：出院诊断CODE CODE2_REMARK：出院诊断备注
	 *            CODE2_STATUS：出院诊断转归状态 OUT_DIAG_CODE3：出院诊断CODE
	 *            CODE3_REMARK：出院诊断备注 CODE3_STATUS：出院诊断转归状态
	 *            OUT_DIAG_CODE4：出院诊断CODE CODE4_REMARK：出院诊断备注
	 *            CODE4_STATUS：出院诊断转归状态 OUT_DIAG_CODE5：出院诊断CODE
	 *            CODE5_REMARK：出院诊断备注 CODE5_STATUS：出院诊断转归状态
	 *            OUT_DIAG_CODE6：出院诊断CODE CODE6_REMARK：出院诊断备注
	 *            CODE6_STATUS：出院诊断转归状态 INTE_DIAG_CODE：院内感染诊断CODE
	 *            CASE_NO：病患case_no COMPLICATION_DIAG：院内并发诊断CODE
	 * @return TParm
	 */
	public TParm updateMRODiag(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = MRORecordTool.getInstance().updateMRODiag(parm, conn);
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateMRODiag==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 修改病案诊断2 用于临床诊断页面修改数据同步病案表-duzhw add 20131017
	 * 
	 * @param parm
	 *            TParm 必须参数：CASE_NO 参数说明： OE_DIAG_CODE:门急诊诊断CODE
	 *            IN_DIAG_CODE：入院诊断CODE OUT_DIAG_CODE1：出院主诊断CODE
	 *            CODE1_REMARK：出院主诊断备注 CODE1_STATUS：出院主诊断转归状态
	 *            OUT_DIAG_CODE2：出院诊断CODE CODE2_REMARK：出院诊断备注
	 *            CODE2_STATUS：出院诊断转归状态 OUT_DIAG_CODE3：出院诊断CODE
	 *            CODE3_REMARK：出院诊断备注 CODE3_STATUS：出院诊断转归状态
	 *            OUT_DIAG_CODE4：出院诊断CODE CODE4_REMARK：出院诊断备注
	 *            CODE4_STATUS：出院诊断转归状态 OUT_DIAG_CODE5：出院诊断CODE
	 *            CODE5_REMARK：出院诊断备注 CODE5_STATUS：出院诊断转归状态
	 *            OUT_DIAG_CODE6：出院诊断CODE CODE6_REMARK：出院诊断备注
	 *            CODE6_STATUS：出院诊断转归状态 INTE_DIAG_CODE：院内感染诊断CODE
	 *            CASE_NO：病患case_no COMPLICATION_DIAG：院内并发诊断CODE
	 * @return TParm
	 */
	public TParm updateMRODiag2(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = MRORecordTool.getInstance().updateMRODiag2(parm, conn);
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROTool.updateMRODiag2==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改 药品过敏
	 * 
	 * @param parm
	 *            TParm 参数信息： CASE_NO,ALLEGIC,OPT_USER,OPT_TERM为必须参数
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateALLEGIC(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("ALLEGIC") != null)// 过敏记录
			sql += " ALLEGIC='" + parm.getValue("ALLEGIC") + "',";

		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateALLEGIC==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 插入 手术信息
	 * 
	 * @param <any>
	 *            TParm
	 * @return TParm
	 */
	public TParm insertOP(TParm parm, TConnection conn) {
		TParm result;
		result = MRORecordTool.getInstance().insertOP(parm, conn);
		return result;
	}

	/**
	 * 修改 手术信息
	 * 
	 * @param parm
	 *            TParm 参数信息： CASE_NO,SEQ_NO,OPT_USER,OPT_TERM为必须参数
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateOP(TParm parm, TConnection conn) {
		TParm result = new TParm();
		if (parm.getData("CASE_NO") == null || parm.getData("SEQ_NO") == null) {
			result.setErr(-1, "必要条件参数不存在！");
			return result;
		}
		String sql = "UPDATE MRO_RECORD_OP SET ";
		if (parm.getData("IPD_NO") != null)// 住院号
			sql += " IPD_NO='" + parm.getValue("IPD_NO") + "',";
		if (parm.getData("MR_NO") != null)// 病历号
			sql += " MR_NO='" + parm.getValue("MR_NO") + "',";
		if (parm.getData("OP_CODE") != null)// 手术代码
			sql += " OP_CODE='" + parm.getValue("OP_CODE") + "',";
		if (parm.getData("OP_DESC") != null)// 手术名称
			sql += " OP_DESC='" + parm.getValue("OP_DESC") + "',";
		if (parm.getData("OP_REMARK") != null)// 手术备注
			sql += " OP_REMARK='" + parm.getValue("OP_REMARK") + "',";
		if (parm.getData("OP_DATE") != null)// 手术时间
			sql += " OP_DATE=TO_DATE('" + parm.getValue("OP_DATE")
					+ "','YYYYMMDDHH24MISS'),";
		if (parm.getData("ANA_WAY") != null)// 麻醉方式
			sql += " ANA_WAY='" + parm.getValue("ANA_WAY") + "',";
		if (parm.getData("ANA_DR") != null)// 麻醉医师
			sql += " ANA_DR='" + parm.getValue("ANA_DR") + "',";
		if (parm.getData("MAIN_SUGEON") != null)// 术者
			sql += " MAIN_SUGEON='" + parm.getValue("MAIN_SUGEON") + "',";
		if (parm.getData("AST_DR1") != null)// 助刀一
			sql += " AST_DR1='" + parm.getValue("AST_DR1") + "',";
		if (parm.getData("AST_DR2") != null)// 助刀二
			sql += " AST_DR2='" + parm.getValue("AST_DR2") + "',";
		if (parm.getData("HEALTH_LEVEL") != null)// 切口愈合等级
			sql += " HEALTH_LEVEL='" + parm.getValue("HEALTH_LEVEL") + "',";
		if (parm.getData("OP_LEVEL") != null)// 手术等级
			sql += " OP_LEVEL='" + parm.getValue("OP_LEVEL") + "',";
		if (parm.getData("OPT_USER") != null)
			sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',";
		if (parm.getData("OPT_TERM") != null)
			sql += " OPT_TERM='" + parm.getValue("OPT_TERM") + "',";
		sql += " OPT_DATE=SYSDATE "; // 修改时间
		sql += " WHERE CASE_NO = '" + parm.getValue("CASE_NO")
				+ "' AND SEQ_NO = '" + parm.getValue("SEQ_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改 输血信息
	 * 
	 * @param parm
	 *            TParm CASE_NO,OPT_USER,OPT_TERM为必须参数
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBlood(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("BLOOD_TYPE") != null)// 血型
			sql += " BLOOD_TYPE='" + parm.getValue("BLOOD_TYPE") + "',";
		if (parm.getData("RH_TYPE") != null)// RH
			sql += " RH_TYPE='" + parm.getValue("RH_TYPE") + "',";
		if (parm.getData("TRANS_REACTION") != null)// 输血反应
			sql += " TRANS_REACTION='" + parm.getValue("TRANS_REACTION") + "',";
		if (parm.getData("RBC") != null)// 红血球
			sql += " RBC='" + parm.getValue("RBC") + "',";
		if (parm.getData("PLATE") != null)// 血小板
			sql += " PLATE='" + parm.getValue("PLATE") + "',";
		if (parm.getData("PLASMA") != null)// 血浆
			sql += " PLASMA='" + parm.getValue("PLASMA") + "',";
		if (parm.getData("WHOLE_BLOOD") != null)// 全血
			sql += " WHOLE_BLOOD='" + parm.getValue("WHOLE_BLOOD") + "',";
		if (parm.getData("OTH_BLOOD") != null)// 其它血品种类
			sql += " OTH_BLOOD='" + parm.getValue("OTH_BLOOD") + "',";
		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改账务信息
	 * 
	 * @param parm
	 *            TParm CASE_NO,OPT_USER,OPT_TERM为必须参数 其他参数自选==> CHARGE_01
	 *            床位费(住);CHARGE_02 护理费(住);CHARGE_03 西药费(住);CHARGE_04
	 *            中成药费(住);CHARGE_05 中草药费(住); CHARGE_06 放射费(住);CHARGE_07
	 *            化验费(住);CHARGE_08 输氧费(住); CHARGE_09 输血费(住);CHARGE_10
	 *            诊疗费(住);CHARGE_11 手术费(住); CHARGE_12 接生费(住);CHARGE_13
	 *            检查费(住);CHARGE_14 家床费(住); CHARGE_15 麻醉费(住);CHARGE_16
	 *            婴儿费(住);CHARGE_17 其他; CHARGE_18 （暂无）;CHARGE_19 （暂无）;CHARGE_20
	 *            （暂无）; 费用名称是根据 SYS_DICTIONARY 表来的 GROUP_ID='MRO_CHARGE'
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateCharge(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("CHARGE_01") != null)// 床位费(住)
			sql += " CHARGE_01='" + parm.getValue("CHARGE_01") + "',";
		if (parm.getData("CHARGE_02") != null)// 护理费(住)
			sql += " CHARGE_02='" + parm.getValue("CHARGE_02") + "',";
		if (parm.getData("CHARGE_03") != null)// 西药费(住)
			sql += " CHARGE_03='" + parm.getValue("CHARGE_03") + "',";
		if (parm.getData("CHARGE_04") != null)// 中成药费(住)
			sql += " CHARGE_04='" + parm.getValue("CHARGE_04") + "',";
		if (parm.getData("CHARGE_05") != null)// 中草药费(住)
			sql += " CHARGE_05='" + parm.getValue("CHARGE_05") + "',";
		if (parm.getData("CHARGE_06") != null)// 放射费(住)
			sql += " CHARGE_06='" + parm.getValue("CHARGE_06") + "',";
		if (parm.getData("CHARGE_07") != null)// 化验费(住)
			sql += " CHARGE_07='" + parm.getValue("CHARGE_07") + "',";
		if (parm.getData("CHARGE_08") != null)// 输氧费(住)
			sql += " CHARGE_08='" + parm.getValue("CHARGE_08") + "',";
		if (parm.getData("CHARGE_09") != null)// 输血费(住)
			sql += " CHARGE_09='" + parm.getValue("CHARGE_09") + "',";
		if (parm.getData("CHARGE_10") != null)// 诊疗费(住)
			sql += " CHARGE_10='" + parm.getValue("CHARGE_10") + "',";
		if (parm.getData("CHARGE_11") != null)// 手术费(住)
			sql += " CHARGE_11='" + parm.getValue("CHARGE_11") + "',";
		if (parm.getData("CHARGE_12") != null)// 接生费(住)
			sql += " CHARGE_12='" + parm.getValue("CHARGE_12") + "',";
		if (parm.getData("CHARGE_13") != null)// 检查费(住)
			sql += " CHARGE_13='" + parm.getValue("CHARGE_13") + "',";
		if (parm.getData("CHARGE_14") != null)// 家床费(住)
			sql += " CHARGE_14='" + parm.getValue("CHARGE_14") + "',";
		if (parm.getData("CHARGE_15") != null)// 麻醉费(住)
			sql += " CHARGE_15='" + parm.getValue("CHARGE_15") + "',";
		if (parm.getData("CHARGE_16") != null)// 婴儿费(住)
			sql += " CHARGE_16='" + parm.getValue("CHARGE_16") + "',";
		if (parm.getData("CHARGE_17") != null)// 其他
			sql += " CHARGE_17='" + parm.getValue("CHARGE_17") + "',";
		if (parm.getData("CHARGE_18") != null)
			sql += " CHARGE_18='" + parm.getValue("CHARGE_18") + "',";
		if (parm.getData("CHARGE_19") != null)
			sql += " CHARGE_19='" + parm.getValue("CHARGE_19") + "',";
		if (parm.getData("CHARGE_20") != null)
			sql += " CHARGE_20='" + parm.getValue("CHARGE_20") + "',";

		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 住院登记时插入病案首页表记录
	 * 
	 * @param parm
	 *            TParm MR_NO,IPD_NO,CASE_NO IN_DATE 入院日; IN_DEPT 入院科别;
	 *            IN_STATION 入院病区; IN_ROOM_NO 入院病室; OE_DIAG_CODE 门急诊诊断;
	 *            IN_CONDITION 入院情况; PG_OWNER 首页建立者;
	 * @return TParm
	 */
	public TParm insertMRORecord(TParm p, TConnection conn) {
		TParm result = new TParm();
		TParm parm = new TParm();
		Pat pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
		parm.setData("MR_NO", p.getValue("MR_NO"));
		parm.setData("IPD_NO", p.getValue("IPD_NO"));
		// Patinfo取得参数
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("IDNO", pat.getIdNo());
		parm.setData("SEX", pat.getSexCode());
		parm.setData("BIRTH_DATE", pat.getBirthday());
		parm.setData("CASE_NO", p.getValue("CASE_NO"));
		parm.setData("MARRIGE", pat.getMarriageCode());
		parm.setData("AGE",
				OdoUtil.showAge(pat.getBirthday(), p.getTimestamp("IN_DATE")));// 计算年龄
																				// 根据生日和入院时间计算
		parm.setData("NATION", pat.getNationCode());
		parm.setData("FOLK", pat.getSpeciesCode());
		parm.setData("CTZ1_CODE", pat.getCtz1Code());
		parm.setData("H_TEL", pat.getTelHome());// 户口电话 用的 家庭电话
		parm.setData("H_ADDRESS", pat.getResidAddress());// 户籍地址
		parm.setData("H_POSTNO", pat.getResidPostCode());// 户籍邮编
		parm.setData("OCCUPATION", pat.getOccCode());// 职业
		parm.setData("OFFICE", pat.getCompanyDesc());// 单位
		parm.setData("O_TEL", pat.getTelCompany());// 单位电话
		parm.setData("O_ADDRESS", "");// 单位地址
		parm.setData("O_POSTNO", "");// 单位电话
		parm.setData("CONTACTER", pat.getContactsName());// 联系人
		parm.setData("RELATIONSHIP", pat.getRelationCode());// 联系人关系
		parm.setData("CONT_TEL", pat.getContactsTel());// 联系人电话
		parm.setData("CONT_ADDRESS", pat.getContactsAddress());// 联系人地址
		parm.setData("HOEMPLACE_CODE", pat.gethomePlaceCode());// 出生地代码
		// 住院登记传入参数
		parm.setData("IN_DATE", p.getData("IN_DATE"));// 入院日
		parm.setData("IN_DEPT", p.getData("IN_DEPT"));// 入院科别
		parm.setData("IN_STATION", p.getData("IN_STATION"));// 入院病区
		parm.setData("IN_ROOM_NO", p.getData("IN_ROOM_NO"));// 入院病室
		parm.setData("OE_DIAG_CODE", p.getData("OE_DIAG_CODE"));// 门急诊诊断
		parm.setData("IN_CONDITION", p.getData("IN_CONDITION"));// 入院情况
		parm.setData("PG_OWNER", p.getData("PG_OWNER"));// 首页建立者

		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		result = this.update("insertPatInfo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 创建首页信息
	 * 
	 * @param parm
	 *            TParm 参数：CASE_NO，MR_NO，OPT_USER,OPT_TERM;HOSP_ID
	 * @return TParm
	 */
	public TParm insertMRO(TParm parm) {
		TParm result = MRORecordTool.getInstance().insertMRO(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改住院信息（住院登记接口）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateADMData(TParm parm) {
		TParm result = MRORecordTool.getInstance().updateADMData(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改首页病患基本信息（住院管理接口）
	 * 
	 * @param parm
	 *            TParm 参数传入：MR_NO，CASE_NO，OPT_USER，OPT_TERM
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateMROPatInfo(TParm p) {
		TParm result = MRORecordTool.getInstance().updateMROPatInfo(p);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改病案的入院病房
	 * 
	 * @param roomCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateInRoom(String roomCode, String caseNo, TConnection conn) {
		String sql = "UPDATE MRO_RECORD SET IN_ROOM_NO = '" + roomCode
				+ "' WHERE CASE_NO='" + caseNo + "' AND IN_ROOM_NO IS NULL";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 得到转科科室
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm getTranDept(TParm parm) {
		TParm tranParm = new TParm();
		// 转科数据
		TParm opeParm = ADMTransLogTool.getInstance().getTranHospFormro(parm);
		// // TParm iNAmdParm = ADMTransLogTool.getInstance()
		// // .getOldTranDeptData(parm);
		// //入院科室
		// String ComPstr = opeParm.getValue("CASE_NO",0)
		// + iNAmdParm.getValue("IN_DATE", 0);
		int count = 0;
        for (int i = opeParm.getCount() - 1; i >= 0; i--) {//wanglong modify 20150115
			// String deptStr = opeParm.getValue("IN_DEPT_CODE", i);
			// String str = opeParm.getValue("CASE_NO", i)
			// + opeParm.getValue("IN_DATE", i);
			// if(str.equals(ComPstr))
			// continue;
			// 变更类别 转科 科室类别 ICU(重症监护科室) NOR(临床科室） 注,OPE(手术科室)不算转科 （未实现）
			tranParm.setData("TRAN_DEPT", count,
					opeParm.getData("OUT_DEPT_CODE", i));//wanglong modify 20150115
			count++;
		}
		tranParm.setCount(count);
		return tranParm;
	}

	/**
	 * 得到病患药物过敏数据
	 * 
	 * @param mrNo
	 *            String
	 * @return TParm
	 */
	public TParm getDrugAllErgy(String mrNo) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.CASE_NO,A.MR_NO,A.DRUG_TYPE,");
		sbSql.append(" CASE A.DRUG_TYPE ");
		sbSql.append("   WHEN 'A' THEN (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID = 'PHA_INGREDIENT' AND B.ID = A.DRUGORINGRD_CODE) ");
		sbSql.append("   WHEN 'B' THEN (SELECT B.ORDER_DESC FROM SYS_FEE B WHERE B.ORDER_CODE = A.DRUGORINGRD_CODE) ");
		sbSql.append("   WHEN 'D' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE = 'PHA_RULE' AND B.CATEGORY_CODE = A.DRUGORINGRD_CODE) ");
		sbSql.append("   WHEN 'E' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE = 'PHA_RULE' AND B.CATEGORY_CODE = A.DRUGORINGRD_CODE) ");
		sbSql.append(" END AS ALLERGY_NAME,A.ALLERGY_NOTE,OPT_DATE ");
		sbSql.append(" FROM OPD_DRUGALLERGY A ");
		sbSql.append(" WHERE A.DRUG_TYPE IN ('A','B','D','E') ");
		sbSql.append(" AND A.MR_NO = '");
		sbSql.append(mrNo);
		sbSql.append("' ORDER BY A.ADM_DATE, A.OPT_DATE ");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 得到重症监护信息
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getICUParm(String caseNo) {
		String sql = "SELECT TO_DATE(A.IN_DATE,'YYYY/MM/DD HH24:MI:SS') IN_DATE,A.OUT_DATE,A.IN_DEPT_CODE AS DEPT_CODE "
				+ " FROM ADM_TRANS_LOG A, SYS_DEPT B "
				+ " WHERE A.IN_DEPT_CODE=B.DEPT_CODE "
				+ " AND B.ICU_TYPE IS NOT NULL"
				+ " AND A.CASE_NO='"
				+ caseNo
				+ "' AND A.PSF_KIND IS NOT NULL  " + " ORDER BY A.IN_DATE ASC ";
		TParm result = new TParm(this.getDBTool().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	
	/**
	 * 查询病案首页数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryHomePageInfo(TParm parm) {
		TParm result = new TParm();
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE 1 = 1 ";
		if (StringUtils.isEmpty(parm.getValue("CASE_NO"))
				&& StringUtils.isEmpty(parm.getValue("CASE_NO_LIST"))) {
			result.setErr(-1, "入参为空");
			return result;
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sql = sql + " AND CASE_NO = '" + parm.getValue("CASE_NO") + "' ";
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO_LIST"))) {
			sql = sql + " AND CASE_NO IN (" + parm.getValue("CASE_NO_LIST")
					+ ") ";
		}

		sql = sql + " AND FILE_NAME like '%病案首页%' ORDER BY CASE_NO,CREATOR_DATE DESC";

		result = new TParm(this.getDBTool().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询病案首页合并信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryMergeHomePageInfo(TParm parm) {
		TParm result = new TParm();
		String sql = "SELECT * FROM MRO_MRV_TECH WHERE HP_MERGE_CODE IS NOT NULL ";
		if (StringUtils.isEmpty(parm.getValue("CASE_NO"))
				&& StringUtils.isEmpty(parm.getValue("CASE_NO_LIST"))) {
			result.setErr(-1, "入参为空");
			return result;
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sql = sql + " AND CASE_NO = '" + parm.getValue("CASE_NO") + "' ";
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO_LIST"))) {
			sql = sql + " AND CASE_NO IN (" + parm.getValue("CASE_NO_LIST") + ") ";
		}
		
		result = new TParm(this.getDBTool().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新病案首页合并信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateMergeHomePageInfo(TParm parm) {
		TParm result = new TParm();
		StringBuffer sbSql = new StringBuffer();

		if (StringUtils.isEmpty(parm.getValue("CASE_NO"))
				&& StringUtils.isEmpty(parm.getValue("CASE_NO_LIST"))) {
			result.setErr(-1, "入参为空");
			return result;
		}

		sbSql.append("UPDATE MRO_MRV_TECH SET HP_MERGE_CODE = '");
		sbSql.append(parm.getValue("HP_MERGE_CODE"));
		sbSql.append("',HP_MERGE_DATE = ");
		sbSql.append(parm.getData("HP_MERGE_DATE"));
		sbSql.append(",OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("' WHERE 1 = 1 ");

		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO_LIST"))) {
			sbSql.append(" AND CASE_NO IN (");
			sbSql.append(parm.getValue("CASE_NO_LIST"));
			sbSql.append(") ");
		}
		
		result = new TParm(this.getDBTool().update(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询外籍人员住院数据
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryForeignerData(TParm parm) {
		TParm result = new TParm();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,A.IN_DATE,");
		sbSql.append("A.DS_DATE,A.VS_DR_CODE,A.DEPT_CODE,C.ICD_CODE,D.ICD_CHN_DESC");
		sbSql.append(" FROM ADM_INP A,SYS_PATINFO B,ADM_INPDIAG C,SYS_DIAGNOSIS D ");
		sbSql.append(" WHERE A.MR_NO = B.MR_NO AND A.CANCEL_FLG = 'N' ");
		sbSql.append(" AND A.CASE_NO = C.CASE_NO(+) AND C.IO_TYPE(+) = 'O' AND C.MAINDIAG_FLG(+) = 'Y' ");
		sbSql.append(" AND C.ICD_CODE = D.ICD_CODE(+) AND NATION_CODE IS NOT NULL AND NATION_CODE <> '86' ");
		
		sbSql.append(" AND A.IN_DATE >= TO_DATE('");
		sbSql.append(parm.getValue("START_DATE"));
		sbSql.append(" 00:00:00','YYYY/MM/DD HH24:MI:SS') AND A.IN_DATE <= TO_DATE('");
		sbSql.append(parm.getValue("END_DATE"));
		sbSql.append(" 23:59:59','YYYY/MM/DD HH24:MI:SS')");
		
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(parm.getValue("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		// 住院状态
		if ("IN".equals(parm.getValue("STATUS"))) {
			sbSql.append(" AND A.DS_DATE IS NULL ");
		} else if ("OUT".equals(parm.getValue("STATUS"))) {
			sbSql.append(" AND A.DS_DATE IS NOT NULL ");
		}
		
		sbSql.append(" ORDER BY IN_DATE,MR_NO ");
		
		result = new TParm(this.getDBTool().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		int count = result.getCount();
		// 年龄
		String age = "";
		// 住院天数
		String inDays = "";
		int days = 0;
		Timestamp sysDate = SystemTool.getInstance().getDate();
		
		for (int i = 0; i < count; i++) {
			// 计算年龄
			if (null != result.getTimestamp("IN_DATE", i)
					&& null != result.getTimestamp("BIRTH_DATE", i)) {
				age = OdiUtil.showAge(result.getTimestamp("BIRTH_DATE", i),
						result.getTimestamp("IN_DATE", i));
			} else {
				age = "";
			}
			
			result.addData("AGE", age);
			
			// 计算在院天数
			if (null == result.getTimestamp("IN_DATE", i)) {
				inDays = "";
			} else if (null != result.getTimestamp("DS_DATE", i)) {
				days = StringTool
						.getDateDiffer(StringTool.setTime(result.getTimestamp(
								"DS_DATE", i), "00:00:00"), StringTool.setTime(
								result.getTimestamp("IN_DATE", i), "00:00:00"));
				if (days == 0) {
					inDays = "1";
				} else {
					inDays = String.valueOf(days);
				}
			} else if (null == result.getTimestamp("DS_DATE", i)) {
				days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
						"00:00:00"), StringTool.setTime(result.getTimestamp(
						"IN_DATE", i), "00:00:00"));
				if (days == 0) {
					inDays = "1";
				} else {
					inDays = String.valueOf(days);
				}
			}
			
			result.addData("IN_DAYS", inDays);
		}
		return result;
	}
	
	/**
	 * 查询CCPC-AMI出院数据
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryCcpcAmiData(TParm parm) {
		TParm result = new TParm();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.CASE_NO,A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,A.IN_DATE,");
		sbSql.append("A.DS_DATE,A.VS_DR_CODE,A.DEPT_CODE,C.ICD_CODE,D.ICD_CHN_DESC,");
		sbSql.append("G.TOT_AMT,H.DRUG_TOT_AMT,I.MATERIAL_TOT_AMT ");
		sbSql.append(" FROM ADM_INP A,SYS_PATINFO B,ADM_INPDIAG C,SYS_DIAGNOSIS D, ADM_RESV E, REG_PATADM F, ");
		// 查询总费用
		sbSql.append("(SELECT CASE_NO, SUM (TOT_AMT) AS TOT_AMT FROM IBS_ORDD GROUP BY CASE_NO) G,");
		// 查询药品费用
		sbSql.append("(SELECT CASE_NO, SUM (TOT_AMT) AS DRUG_TOT_AMT FROM IBS_ORDD WHERE REXP_CODE IN ('022.01', '022.02', '023') GROUP BY CASE_NO) H,");
		// 查询材料费用
		sbSql.append("(SELECT CASE_NO, SUM (TOT_AMT) AS MATERIAL_TOT_AMT FROM IBS_ORDD WHERE REXP_CODE IN ('035') GROUP BY CASE_NO) I ");
		
		sbSql.append(" WHERE A.MR_NO = B.MR_NO AND A.DS_DATE IS NOT NULL AND A.CANCEL_FLG = 'N' ");
		sbSql.append(" AND A.CASE_NO = E.IN_CASE_NO AND E.OPD_CASE_NO = F.CASE_NO AND F.PATH_KIND = 'P01' ");
		sbSql.append(" AND A.CASE_NO = G.CASE_NO(+) AND A.CASE_NO = H.CASE_NO(+) AND A.CASE_NO = I.CASE_NO(+) ");
		sbSql.append(" AND A.CASE_NO = C.CASE_NO(+) AND C.IO_TYPE(+) = 'O' AND C.MAINDIAG_FLG(+) = 'Y' ");
		sbSql.append(" AND C.ICD_CODE = D.ICD_CODE(+) ");
		
		sbSql.append(" AND A.DS_DATE >= TO_DATE('");
		sbSql.append(parm.getValue("START_DATE"));
		sbSql.append(" 00:00:00','YYYY/MM/DD HH24:MI:SS') AND A.DS_DATE <= TO_DATE('");
		sbSql.append(parm.getValue("END_DATE"));
		sbSql.append(" 23:59:59','YYYY/MM/DD HH24:MI:SS')");
		
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(parm.getValue("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		sbSql.append(" ORDER BY A.DS_DATE,A.CASE_NO ");
		
		result = new TParm(this.getDBTool().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		int count = result.getCount();
		// 年龄
		String age = "";
		// 住院天数
		String inDays = "";
		int days = 0;
		Timestamp sysDate = SystemTool.getInstance().getDate();
		
		for (int i = 0; i < count; i++) {
			// 计算年龄
			if (null != result.getTimestamp("IN_DATE", i)
					&& null != result.getTimestamp("BIRTH_DATE", i)) {
				age = OdiUtil.showAge(result.getTimestamp("BIRTH_DATE", i),
						result.getTimestamp("IN_DATE", i));
			} else {
				age = "";
			}
			
			result.addData("AGE", age);
			
			// 计算在院天数
			if (null == result.getTimestamp("IN_DATE", i)) {
				inDays = "";
			} else if (null != result.getTimestamp("DS_DATE", i)) {
				days = StringTool
						.getDateDiffer(StringTool.setTime(result.getTimestamp(
								"DS_DATE", i), "00:00:00"), StringTool.setTime(
								result.getTimestamp("IN_DATE", i), "00:00:00"));
				if (days == 0) {
					inDays = "1";
				} else {
					inDays = String.valueOf(days);
				}
			} else if (null == result.getTimestamp("DS_DATE", i)) {
				days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
						"00:00:00"), StringTool.setTime(result.getTimestamp(
						"IN_DATE", i), "00:00:00"));
				if (days == 0) {
					inDays = "1";
				} else {
					inDays = String.valueOf(days);
				}
			}
			
			result.addData("IN_DAYS", inDays);
		}
		return result;
	}
}
