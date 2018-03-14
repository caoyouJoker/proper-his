package jdo.nss;

import jdo.ibs.IBSTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.OdiUtil;

/**
 * <p>Title: 肠内营养工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangbin 2015.3.16
 * @version 1.0
 */
public class NSSEnteralNutritionTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static NSSEnteralNutritionTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return NSSMealTool
	 */
	public static NSSEnteralNutritionTool getInstance() {
		if (instanceObject == null)
			instanceObject = new NSSEnteralNutritionTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public NSSEnteralNutritionTool() {
		setModuleName("nss\\NSSEnteralNutritionModule.x");
		onInit();
	}

	/**
	 * 查询开立肠内营养饮食医嘱的病患信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryENOrderPatInfo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT DISTINCT A.STATION_CODE AS ENO_STATION_CODE,A.DEPT_CODE AS ENO_DEPT_CODE,");
		sbSql.append(" A.MR_NO,A.IN_DATE,TO_CHAR(A.WEIGHT) || 'Kg' AS WEIGHT,");
		sbSql.append(" A.HEIGHT || 'cm' AS HEIGHT,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,");
		sbSql.append(" D.BED_NO_DESC,B.CASE_NO,B.ORDER_NO,B.ORDER_SEQ ");
		sbSql.append(" FROM ADM_INP A,ODI_ORDER B,SYS_PATINFO C,SYS_BED D,NSS_EN_ORDERM E,NSS_EN_ORDERD F ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.BED_NO = D.BED_NO ");
		sbSql.append(" AND A.DS_DATE IS NULL ");
		sbSql.append(" AND B.NS_CHECK_CODE IS NOT NULL ");
		sbSql.append(" AND B.NS_CHECK_DATE IS NOT NULL ");
		sbSql.append(" AND B.DC_DR_CODE IS NULL ");
		sbSql.append(" AND B.DC_DATE IS NULL ");
		sbSql.append(" AND B.CASE_NO = E.CASE_NO(+) ");
		sbSql.append(" AND B.ORDER_NO = E.ORDER_NO(+) ");
		sbSql.append(" AND B.ORDER_SEQ = E.ORDER_SEQ(+) ");
		sbSql.append(" AND E.EN_ORDER_NO = F.EN_ORDER_NO(+) ");

		// 饮食医嘱
		if (StringUtils.isNotEmpty(parm.getValue("DR_DIET"))) {
			sbSql.append(" AND B.ORDER_CODE = '");
			sbSql.append(parm.getValue("DR_DIET"));
			sbSql.append("' ");
		} else {
			sbSql.append(" AND B.ORDER_CODE IN (SELECT EC.ORDER_CODE FROM NSS_EN_CATEGORY EC) ");
		}
		
		// 科室
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(parm.getValue("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		// 病区
		if (StringUtils.isNotEmpty(parm.getValue("STATION_CODE"))) {
			sbSql.append(" AND A.STATION_CODE = '");
			sbSql.append(parm.getValue("STATION_CODE"));
			sbSql.append("' ");
		}
		
		// 病案号
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			sbSql.append(" AND A.MR_NO = '");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append("' ");
		}
		
		// 完成状态
		if (StringUtils.equals(parm.getValue("ORDER_STATUS"), "N")) {
			sbSql.append(" AND (E.EN_ORDER_NO IS NULL ");
			sbSql.append(" OR (E.DC_DR_CODE IS NOT NULL ");
			sbSql.append(" AND E.DC_DATE IS NOT NULL) OR F.CASE_NO IS NULL) ");
		} else {
			sbSql.append(" AND E.EN_ORDER_NO IS NOT NULL ");
			sbSql.append(" AND E.DC_DR_CODE IS NULL ");
			sbSql.append(" AND E.DC_DATE IS NULL ");
			sbSql.append(" AND F.CASE_NO IS NOT NULL ");
		}
		
		// 定制界面临时医嘱只在当天显示
		if (StringUtils.isNotEmpty(parm.getValue("SYSDATE"))) {
			sbSql.append(" AND ((B.RX_KIND = 'ST' AND B.ORDER_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("SYSDATE"));
			sbSql.append("000000','YYYYMMDDHH24MISS') AND TO_DATE('");
			sbSql.append(parm.getValue("SYSDATE"));
			sbSql.append("235959','YYYYMMDDHH24MISS')) OR B.RX_KIND = 'UD') ");
		}
		
		sbSql.append(" ORDER BY ENO_DEPT_CODE,ENO_STATION_CODE,BED_NO_DESC ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
    	int count = result.getCount();
    	String age = "";
    	for (int i = 0;i < count; i++) {
    		// 根据出生日期计算当前年龄
    		age = OdiUtil.getInstance().showAge(result.getTimestamp("BIRTH_DATE", i),
					SystemTool.getInstance().getDate());
    		result.addData("AGE", age);
    	}

		return result;
	}
	
	/**
	 * 查询住院医生下达的饮食医嘱信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryENDrOrderInfo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT * FROM ODI_ORDER ");
		sbSql.append(" WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' AND ORDER_SEQ = ");
		sbSql.append(parm.getInt("ORDER_SEQ"));
		
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
	 * 查询营养师医嘱主项数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryENOrderM(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT 'N' AS FLG,A.*,C.PAT_NAME FROM NSS_EN_ORDERM A,ODI_ORDER B,SYS_PATINFO C ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO AND A.ORDER_NO = B.ORDER_NO ");
		sbSql.append(" AND A.ORDER_SEQ = B.ORDER_SEQ AND A.MR_NO = C.MR_NO ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND A.CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_NO"))) {
			sbSql.append(" AND A.ORDER_NO = '");
			sbSql.append(parm.getValue("ORDER_NO"));
			sbSql.append("' ");
		}
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ"))) {
			sbSql.append(" AND A.ORDER_SEQ = '");
			sbSql.append(parm.getInt("ORDER_SEQ"));
			sbSql.append("' ");
		}
		
		// 饮食种类
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sbSql.append(" AND A.ORDER_CODE = '");
			sbSql.append(parm.getValue("ORDER_CODE"));
			sbSql.append("' ");
		}
		
		// 科室
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(parm.getValue("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		// 病区
		if (StringUtils.isNotEmpty(parm.getValue("STATION_CODE"))) {
			sbSql.append(" AND A.STATION_CODE = '");
			sbSql.append(parm.getValue("STATION_CODE"));
			sbSql.append("' ");
		}
		
		// 病案号
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			sbSql.append(" AND A.MR_NO = '");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append("' ");
		}
		
		// 使用中营养师医嘱
		if (StringUtils.equals(parm.getValue("DIE_ORDER_STATUS"), "Y")) {
			sbSql.append(" AND A.DC_DR_CODE IS NULL ");
			sbSql.append(" AND A.DC_DATE IS NULL ");
		} else if (StringUtils.equals(parm.getValue("DIE_ORDER_STATUS"), "N")) {
			sbSql.append(" AND A.DC_DR_CODE IS NOT NULL ");
			sbSql.append(" AND A.DC_DATE IS NOT NULL ");
		}
		
		// 医嘱是否停用
		if (StringUtils.equals(parm.getValue("DR_ORDER_STATUS"), "N")) {
			sbSql.append(" AND B.DC_DR_CODE IS NOT NULL ");
			sbSql.append(" AND B.DC_DATE IS NOT NULL ");
		} else if (StringUtils.equals(parm.getValue("DR_ORDER_STATUS"), "Y")) {
			sbSql.append(" AND B.DC_DR_CODE IS NULL ");
			sbSql.append(" AND B.DC_DATE IS NULL ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("RX_KIND"))) {
			// 长期医嘱
			if (StringUtils.equals("UD", parm.getValue("RX_KIND"))) {
				sbSql.append(" AND A.RX_KIND = 'UD' ");
			} else {
				// 临时医嘱
				sbSql.append(" AND A.RX_KIND = 'ST' ");
				if (StringUtils.isNotEmpty(parm.getValue("NOW_DATE"))) {
					sbSql.append(" AND A.ORDER_DATE BETWEEN TO_DATE('");
					sbSql.append(parm.getValue("NOW_DATE"));
					sbSql.append("000000', 'YYYYMMDDHH24MISS') AND TO_DATE('");
					sbSql.append(parm.getValue("NOW_DATE"));
					sbSql.append("235959', 'YYYYMMDDHH24MISS') ");
				}
			}
		}
		
		// 历史配方界面按照操作时间倒序排列显示
		if (StringUtils.equals("Y", parm.getValue("ORDER_DATE_SORT"))) {
			sbSql.append(" ORDER BY A.ORDER_DATE DESC,A.OPT_DATE DESC ");
		} else {
			sbSql.append(" ORDER BY A.DEPT_CODE,A.STATION_CODE,A.BED_NO ");
		}
		
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
	 * 保存营养师医嘱主项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm insertNSSENOrderM(TParm parm) {
        TParm result = this.update("insertNSSENOrderM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * 删除营养师医嘱主项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm deleteNSSENOrderM(TParm parm) {
		TParm result = this.update("deleteNSSENOrderM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * 删除营养师医嘱配方细项
	 * 
	 * @param sql
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 * @author wangbin
	 */
	public TParm deleteNSSENOrderD(String sql, TConnection conn) {
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * 保存营养师展开医嘱主项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm insertNSSENDspnM(TParm parm, TConnection conn) {
        TParm result = this.update("insertNSSENDspnM", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * 保存营养师展开医嘱细项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm insertNSSENDspnD(TParm parm, TConnection conn) {
        TParm result = this.update("insertNSSENDspnD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * 根据营养师医嘱主项查询对应的配方明细SQL
	 * 
	 * @param parm
	 *            TParm
	 * @return sbSql
	 * @author wangbin
	 */
	public String queryENOrderDSql(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT EN_ORDER_NO,SEQ,MR_NO,CASE_NO,FORMULA_CODE,");
		sbSql.append(" FORMULA_CHN_DESC,MEDI_QTY,MEDI_UNIT,");
		sbSql.append(" OPT_USER,OPT_DATE,OPT_TERM");
		sbSql.append(" FROM NSS_EN_ORDERD ");
		sbSql.append(" WHERE EN_ORDER_NO = '");
		sbSql.append(parm.getValue("EN_ORDER_NO"));
		sbSql.append("' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("FORMULA_CODE"))) {
			sbSql.append(" FORMULA_CODE = '");
			sbSql.append(parm.getValue("FORMULA_CODE"));
			sbSql.append("' ");
		}
		
		sbSql.append(" ORDER BY SEQ");
		
		return sbSql.toString();
	}
	
	/**
	 * 根据营养师医嘱主项查询对应的配方明细
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryENOrderD(TParm parm) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				this.queryENOrderDSql(parm)));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 根据输入的配方名称动态模糊匹配相应的配方数据
	 * 
	 * @param parm
	 *            TParm
	 * @return sbSql
	 * @author wangbin
	 */
	public String queryENFormulaPopSql(String inputStr) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT FORMULA_CODE,FORMULA_CHN_DESC,PY1,PY2,MEDI_UNIT FROM NSS_EN_FORMULAM WHERE ACTIVE_FLG = 'Y' ");
		
		if (StringUtils.isNotEmpty(inputStr)) {
			sbSql.append(" AND (FORMULA_CODE LIKE '%");
			sbSql.append(inputStr);
			sbSql.append("%' OR FORMULA_CHN_DESC LIKE '%");
			sbSql.append(inputStr);
			sbSql.append("%' OR PY1 LIKE '%");
			sbSql.append(inputStr);
			sbSql.append("%' OR PY2 LIKE '%");
			sbSql.append(inputStr);
			sbSql.append("%')");
		}
		
		return sbSql.toString();
	}
	
	/**
	 * 更新营养师医嘱主项数据
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm updateENOrderM(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE NSS_EN_ORDERM ");
		sbSql.append(" SET MEDI_QTY = '");
		sbSql.append(parm.getInt("MEDI_QTY"));
		sbSql.append("',MEDI_UNIT = '");
		sbSql.append(parm.getValue("MEDI_UNIT"));
		sbSql.append("',FREQ_CODE = '");
		sbSql.append(parm.getValue("FREQ_CODE"));
		sbSql.append("',TAKE_DAYS = '");
		sbSql.append(parm.getValue("TAKE_DAYS"));
		sbSql.append("',TOTAL_QTY = '");
		sbSql.append(parm.getInt("TOTAL_QTY"));
		sbSql.append("',TOTAL_UNIT = '");
		sbSql.append(parm.getValue("TOTAL_UNIT"));
		sbSql.append("',CONTAINER_CODE = '");
		sbSql.append(parm.getValue("CONTAINER_CODE"));
		sbSql.append("',LABEL_QTY = '");
		sbSql.append(parm.getValue("LABEL_QTY"));
		sbSql.append("',LABEL_CONTENT = '");
		sbSql.append(parm.getValue("LABEL_CONTENT"));
		sbSql.append("',OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("'");
		sbSql.append(" WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' AND ORDER_SEQ = ");
		sbSql.append(parm.getInt("ORDER_SEQ"));
		sbSql.append(" AND EN_ORDER_NO = '");
		sbSql.append(parm.getValue("EN_ORDER_NO"));
		sbSql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新营养师医嘱主项的定制人员信息
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm updateENOrderMOrderInfo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE NSS_EN_ORDERM ");
		sbSql.append(" SET ORDER_DEPT_CODE = '");
		sbSql.append(parm.getValue("ORDER_DEPT_CODE"));
		sbSql.append("',ORDER_DR_CODE = '");
		sbSql.append(parm.getValue("ORDER_DR_CODE"));
		sbSql.append("',ORDER_DATE = SYSDATE ");
		sbSql.append(" WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' AND ORDER_SEQ = ");
		sbSql.append(parm.getInt("ORDER_SEQ"));
		sbSql.append(" AND EN_ORDER_NO = '");
		sbSql.append(parm.getValue("EN_ORDER_NO"));
		sbSql.append("'");

		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 停用营养师医嘱
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm updateDCENOrderM(TParm parm) {
		
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE NSS_EN_ORDERM ");
		sbSql.append(" SET DC_DR_CODE = '");
		sbSql.append(parm.getValue("DC_DR_CODE"));
		sbSql.append("',DC_DATE = SYSDATE");
		sbSql.append(",OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("'");
		sbSql.append(" WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' AND ORDER_SEQ = ");
		sbSql.append(parm.getInt("ORDER_SEQ"));
		sbSql.append(" AND EN_ORDER_NO = '");
		sbSql.append(parm.getValue("EN_ORDER_NO"));
		sbSql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询肠内营养容器字典数据
	 * 
	 * @param containerCode
	 *            容器代码
	 * @return result
	 * @author wangbin
	 */
	public TParm queryENContainer(String containerCode) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT CAPACITY,CAPACITY_UNIT FROM NSS_EN_CONTAINER ");
		sbSql.append(" WHERE CONTAINER_CODE = '");
		sbSql.append(containerCode);
		sbSql.append("'");

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
	 * 查询批次数据
	 * 
	 * @param freqCode
	 *            频次代码
	 * @return result
	 * @author wangbin
	 */
	public TParm queryPhaFreq(String freqCode) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT FREQ_TIMES FROM SYS_PHAFREQ ");
		sbSql.append(" WHERE FREQ_CODE = '");
		sbSql.append(freqCode);
		sbSql.append("'");

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
	 * 查询饮食种类字典
	 * 
	 * @param orderCode
	 *            饮食种类代码
	 * @return result
	 * @author wangbin
	 */
	public TParm queryENCategory(String orderCode) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT ORDER_CODE,ORDER_DESC,");
		sbSql.append(" VALID_PERIOD,UNIT_CODE,FREQ_PRINT_FLG,");
		sbSql.append(" TOTAL_PRINT_FLG,SHOW_NUTRITION_FLG ");
		sbSql.append(" FROM NSS_EN_CATEGORY ");
		sbSql.append(" WHERE ORDER_CODE = '");
		sbSql.append(orderCode);
		sbSql.append("'");
		
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
	 * 查询营养师医嘱展开主项数据
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryENDspnM(TParm parm) {
        TParm result = this.query("queryENDspnM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 查询营养师医嘱展开主项数据
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryENDspnMData(TParm parm) {
		String sql = "SELECT * FROM NSS_EN_DSPNM WHERE EN_PREPARE_NO = '"
				+ parm.getValue("EN_PREPARE_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 统计配方明细总量
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryENOrderDDataAccount(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT A.FORMULA_CODE,A.FORMULA_CHN_DESC,SUM(A.MEDI_QTY) AS MEDI_QTY,A.MEDI_UNIT ");
		sbSql.append(" FROM NSS_EN_ORDERD A,NSS_EN_DSPNM B ");
		sbSql.append(" WHERE A.EN_ORDER_NO = B.EN_ORDER_NO ");
		sbSql.append(" AND A.EN_ORDER_NO IN ('");
		sbSql.append(parm.getValue("EN_ORDER_NO"));
		sbSql.append("') AND B.EN_PREPARE_NO IN ('");
		sbSql.append(parm.getValue("EN_PREPARE_NO"));
		sbSql.append("') GROUP BY A.FORMULA_CODE,A.FORMULA_CHN_DESC,A.MEDI_UNIT ");
		sbSql.append(" ORDER BY A.FORMULA_CODE");
		
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
	 * 配制人员完成配制时更新肠内营养执行主档表数据
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return result
	 * @author wangbin
	 */
	public TParm updateENDspnMByPrepare(TParm parm, TConnection conn) {
		StringBuilder sbSql = new StringBuilder();
		TParm result = new TParm();
		sbSql.append(" UPDATE NSS_EN_DSPNM SET PREPARE_DR_CODE = '");
		sbSql.append(parm.getValue("PREPARE_DR_CODE"));
		sbSql.append("',PREPARE_DATE = SYSDATE");
		sbSql.append(",PREPARE_STATUS = '1',OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("'");
		
		// 配方中存在收费明细
		if (parm.getBoolean("CHARGE_FLG")) {
			sbSql.append(",BILL_FLG = 'Y' ");
		}
		
		sbSql.append(" WHERE 1 = 1");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("EN_ORDER_NO"))) {
			sbSql.append(" AND EN_ORDER_NO = '");
			sbSql.append(parm.getValue("EN_ORDER_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("EN_PREPARE_NO"))) {
			sbSql.append(" AND EN_PREPARE_NO = '");
			sbSql.append(parm.getValue("EN_PREPARE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("EN_PREPARE_DATE"))) {
			sbSql.append(" AND EN_PREPARE_DATE = TO_DATE('");
			sbSql.append(parm.getValue("EN_PREPARE_DATE"));
			sbSql.append("', 'YYYY/MM/DD') ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("CANCEL_FLG"))) {
			sbSql.append(" AND CANCEL_FLG = '");
			sbSql.append(parm.getValue("CANCEL_FLG"));
			sbSql.append("' ");
		}
		
		if (conn == null) {
			result = new TParm(TJDODBTool.getInstance()
					.update(sbSql.toString()));
		} else {
			result = new TParm(TJDODBTool.getInstance().update(
					sbSql.toString(), conn));
		}
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 取消已展开的营养师医嘱数据
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm updateENDspnMByCancel(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE NSS_EN_DSPNM SET CANCEL_FLG = 'Y',OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("' WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("EN_ORDER_NO"))) {
			sbSql.append(" AND EN_ORDER_NO = '");
			sbSql.append(parm.getValue("EN_ORDER_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("EN_PREPARE_NO"))) {
			sbSql.append(" AND EN_PREPARE_NO = '");
			sbSql.append(parm.getValue("EN_PREPARE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("EN_PREPARE_DATE"))) {
			sbSql.append(" AND EN_PREPARE_DATE = TO_DATE('");
			sbSql.append(parm.getValue("EN_PREPARE_DATE"));
			sbSql.append("', 'YYYY/MM/DD') ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
    /**
     * 肠内营养配制完成计费(同时写入IBS_ORDD,IBS_ORDM)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
	 * @author wangbin
     */
    public TParm insertIBSOrder(TParm parm, TConnection conn) {
        // 结果集
        TParm result = new TParm();
        // 数据检核
        if (parm == null) {
        	result.setErr(-1, "传参错误");
            return result;
        }
        
        TParm ibsParm = new TParm();
        ibsParm.setData("M", parm.getData());
        ibsParm.setData("DATA_TYPE", "7");
        ibsParm.setData("FLG", "ADD");
        
        result = IBSTool.getInstance().insertIBSOrder(ibsParm, conn);
        
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }
    
	/**
	 * 查询病患配方收费明细
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryNSSENOrderDPay(TParm parm) {
        TParm result = this.query("queryNSSENOrderDPay", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 查询住院医生以及营养师的医嘱情况
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryOrderInfo(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT A.DC_DATE AS EN_DC_DATE,A.DC_DR_CODE AS EN_DC_DR_CODE, ");
		sql.append(" B.DC_DATE,B.DC_DR_CODE,B.DR_NOTE ");
		sql.append(" FROM NSS_EN_ORDERM A, ODI_ORDER B ");
		sql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sql.append(" AND A.ORDER_NO = B.ORDER_NO ");
		sql.append(" AND A.ORDER_SEQ = B.ORDER_SEQ ");
		sql.append(" AND A.ORDER_CODE = B.ORDER_CODE ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sql.append(" AND A.CASE_NO = '");
			sql.append(parm.getValue("CASE_NO"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("EN_ORDER_NO"))) {
			sql.append(" AND A.EN_ORDER_NO = '");
			sql.append(parm.getValue("EN_ORDER_NO"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_NO"))) {
			sql.append(" AND B.ORDER_NO = '");
			sql.append(parm.getValue("ORDER_NO"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ"))) {
			sql.append(" AND B.ORDER_SEQ = ");
			sql.append(parm.getInt("ORDER_SEQ"));
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sql.append(" AND B.ORDER_CODE = '");
			sql.append(parm.getValue("ORDER_CODE"));
			sql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 根据配方含量查询计算该配方下的营养成分含量
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryNutritionContentQty(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT A.FORMULA_CODE,A.FORMULA_CHN_DESC,A.NUTRITION_CONTENT AS NUTRITION_CONTENT_UNIT,A.NUTRITION_UNIT,");
		sql.append("B.NUTRITION_CODE,C.NUTRITION_CHN_DESC,B.NUTRITION_CONTENT,C.UNIT_CODE,");
		sql.append(parm.getDouble("MEDI_QTY"));
		sql.append("/A.NUTRITION_CONTENT * B.NUTRITION_CONTENT AS CONTENT_QTY");
		sql.append(" FROM NSS_EN_FORMULAM A, NSS_EN_FORMULAD B, NSS_NUTRITION C ");
		sql.append(" WHERE A.FORMULA_CODE = B.FORMULA_CODE AND B.NUTRITION_CODE = C.NUTRITION_CODE");
		sql.append(" AND A.FORMULA_CODE = '");
		sql.append(parm.getValue("FORMULA_CODE"));
		sql.append("' ORDER BY B.NUTRITION_CODE");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 查询营养成分字典数据
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryNSSNutrition() {
		String sql = "SELECT * FROM NSS_NUTRITION ORDER BY NUTRITION_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 查询展开医嘱执行情况SQL
	 * 
	 * @param parm
	 *            TParm
	 * @return sql
	 * @author wangbin
	 */
	public String queryENDspnDSql(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT CASE WHEN B.EXEC_FLG = 'N' THEN 'Y' ELSE 'N' END AS SEL_FLG,B.EXEC_FLG AS EXE_FLG,");
		sbSql.append("D.LINKMAIN_FLG,D.LINK_NO,B.SEQ,A.EN_PREPARE_DATE AS NS_EXEC_DATE,A.ORDER_CODE,A.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,");
		sbSql.append("A.LABEL_CONTENT AS DOSAGE_QTY,A.MEDI_UNIT AS DOSAGE_UNIT,A.FREQ_CODE,D.ROUTE_CODE,D.DR_NOTE,");
		sbSql.append("D.ORDER_DR_CODE,D.DC_DATE,D.DC_DR_CODE,'' AS CANCELRSN_CODE,'' AS INV_CODE,A.EN_PREPARE_NO AS BAR_CODE, ");
		sbSql.append("A.TOTAL_QTY,A.TOTAL_ACCU_QTY,A.CASE_NO,A.EN_ORDER_NO,A.MR_NO,A.PREPARE_DATE,A.RX_KIND,C.ORDER_NO,C.ORDER_SEQ ");
		sbSql.append(",D.PUMP_CODE,D.INFLUTION_RATE ");    //modify by wukai 20160603
		sbSql.append(" FROM NSS_EN_DSPNM A,NSS_EN_DSPND B,NSS_EN_ORDERM C,ODI_ORDER D ");
		sbSql.append(" WHERE A.EN_PREPARE_NO = B.EN_PREPARE_NO AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.CASE_NO = C.CASE_NO AND A.EN_ORDER_NO = C.EN_ORDER_NO AND C.CASE_NO = D.CASE_NO ");
		sbSql.append(" AND C.ORDER_NO = D.ORDER_NO AND C.ORDER_SEQ = D.ORDER_SEQ ");
		
		// 日期范围
		if (StringUtils.isNotEmpty(parm.getValue("START_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("END_DATE"))) {
			sbSql.append(" AND A.EN_PREPARE_DATE BETWEEN TO_DATE ('");
			sbSql.append(parm.getValue("START_DATE"));
			sbSql.append("', 'YYYYMMDD') AND TO_DATE ('");
			sbSql.append(parm.getValue("END_DATE"));
			sbSql.append("', 'YYYYMMDD')");
		}
		
		// 扫描条码号
		if (StringUtils.isNotEmpty(parm.getValue("EN_PREPARE_NO"))) {
			sbSql.append(" AND A.EN_PREPARE_NO = '");
			sbSql.append(parm.getValue("EN_PREPARE_NO"));
			sbSql.append("' ");
			// 完成状态
			sbSql.append(" AND A.PREPARE_STATUS = '1' ");
		}
		
		// 执行状态
		if (StringUtils.isNotEmpty(parm.getValue("EXEC_FLG"))) {
			sbSql.append(" AND B.EXEC_FLG = '");
			sbSql.append(parm.getValue("EXEC_FLG"));
			sbSql.append("'");
		}
		
		// 查询同一配制单号下数据的显示条数限制
		if (StringUtils.isNotEmpty(parm.getValue("ROWNUM_LIMIT_FLG"))) {
			sbSql.append(" AND ROWNUM < 2");
		}
		
		sbSql.append(" ORDER BY B.SEQ ");
		
        return sbSql.toString();
	}
	
	/**
	 * 护士单次执行保存操作更新医嘱展开主项
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm updateENDspnMBySingleExe(TParm parm, TConnection conn) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE NSS_EN_DSPNM ");
		sbSql.append(" SET TOTAL_ACCU_QTY = ");
		sbSql.append(parm.getDouble("TOTAL_ACCU_QTY"));
		sbSql.append(",EXEC_STATUS = '");
		sbSql.append(parm.getValue("EXEC_STATUS"));
		sbSql.append("',OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,");
		sbSql.append("OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("' ");
		sbSql.append(" WHERE EN_PREPARE_NO = '");
		sbSql.append(parm.getValue("EN_PREPARE_NO"));
		sbSql.append("' ");

		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("'");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString(), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
	 * 护士单次执行保存操作更新医嘱展开细项
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm updateENDspnDBySingleExe(TParm parm, TConnection conn) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE NSS_EN_DSPND ");
		sbSql.append(" SET EXEC_FLG = '");
		sbSql.append(parm.getValue("EXEC_FLG"));
		sbSql.append("', EXEC_USER = '");
		sbSql.append(parm.getValue("EXEC_USER"));
		sbSql.append("', EXEC_DATE = SYSDATE,OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("' ");
		sbSql.append(" WHERE EN_PREPARE_NO = '");
		sbSql.append(parm.getValue("EN_PREPARE_NO"));
		sbSql.append("' AND SEQ = ");
		sbSql.append(parm.getDouble("SEQ"));
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString(), conn));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
	 * 查询ODI_DSPND表展开明细
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryODIDspnDInfo(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME ");
		sbSql.append(" FROM ODI_DSPND ");
		sbSql.append(" WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sbSql.append(" AND ORDER_CODE = '");
			sbSql.append(parm.getValue("ORDER_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_NO"))) {
			sbSql.append(" AND ORDER_NO = '");
			sbSql.append(parm.getValue("ORDER_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ"))) {
			sbSql.append(" AND ORDER_SEQ = ");
			sbSql.append(parm.getValue("ORDER_SEQ"));
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_DATE"))) {
			sbSql.append(" AND ORDER_DATE = '");
			sbSql.append(parm.getValue("ORDER_DATE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.equals("Y", parm.getValue("EXEC_FLG"))) {
			sbSql.append(" AND NS_EXEC_CODE_REAL IS NOT NULL ");
		} else if (StringUtils.equals("N", parm.getValue("EXEC_FLG"))) {
			sbSql.append(" AND NS_EXEC_CODE_REAL IS NULL ");
		}
		
		sbSql.append(" ORDER BY ORDER_DATETIME ");
		
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
	 * 护士单次执行保存操作更新医嘱展开细项ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return result
	 * @author wangbin
	 */
	public TParm updateODIDspnD(TParm parm, TConnection conn) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE ODI_DSPND ");
		sbSql.append(" SET NS_EXEC_CODE_REAL = '");
		sbSql.append(parm.getValue("NS_EXEC_CODE_REAL"));
		sbSql.append("', NS_EXEC_DATE_REAL = SYSDATE");
		sbSql.append(" WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sbSql.append(" AND ORDER_CODE = '");
			sbSql.append(parm.getValue("ORDER_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_NO"))) {
			sbSql.append(" AND ORDER_NO = '");
			sbSql.append(parm.getValue("ORDER_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ"))) {
			sbSql.append(" AND ORDER_SEQ = ");
			sbSql.append(parm.getValue("ORDER_SEQ"));
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_DATE"))) {
			sbSql.append(" AND ORDER_DATE = '");
			sbSql.append(parm.getValue("ORDER_DATE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_DATE"))) {
			sbSql.append(" AND ORDER_DATETIME = '");
			sbSql.append(parm.getValue("ORDER_DATETIME"));
			sbSql.append("' ");
		}
		
		TParm result = new TParm();
		if (conn != null) {
			result = new TParm(TJDODBTool.getInstance().update(
					sbSql.toString(), conn));
		} else {
			result = new TParm(TJDODBTool.getInstance()
					.update(sbSql.toString()));
		}
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
	 * 
	 * 肠内营养分类字典
	 * 插入操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm insertDataFL(TParm parm){
		String sql = "INSERT INTO NSS_EN_CATEGORY (ORDER_CODE, ORDER_DESC, VALID_PERIOD, " +
				" UNIT_CODE, FREQ_PRINT_FLG,TOTAL_PRINT_FLG, SHOW_NUTRITION_FLG," +
				" OPT_USER, OPT_DATE, OPT_TERM) "
				+ "VALUES ('"
				+ parm.getValue("ORDER_CODE")+ "','"
				+ parm.getValue("ORDER_DESC")+ "','"
				+ parm.getValue("VALID_PERIOD")+ "','"
				+ parm.getValue("UNIT_CODE")+ "','"
				+ parm.getValue("FREQ_PRINT_FLG")+ "','"
				+ parm.getValue("TOTAL_PRINT_FLG")+ "','"
				+ parm.getValue("SHOW_NUTRITION_FLG") + "','"
				+ parm.getValue("OPT_USER")+"',TO_DATE('"+ parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'),'"
				+ parm.getValue("OPT_TERM")+"')";
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 
	 * 肠内营养分类字典
	 * 修改操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm updateDataFL(TParm parm){
		String sql = " UPDATE NSS_EN_CATEGORY SET "+
					" ORDER_DESC = '" + parm.getValue("ORDER_DESC")+"'," +
					" VALID_PERIOD ='" + parm.getValue("VALID_PERIOD")+"'," +
					" UNIT_CODE = '" + parm.getValue("UNIT_CODE")+"'," +
					" FREQ_PRINT_FLG = '" + parm.getValue("FREQ_PRINT_FLG")+"'," +
					" TOTAL_PRINT_FLG = '" + parm.getValue("TOTAL_PRINT_FLG")+"'," +
					" SHOW_NUTRITION_FLG = '" + parm.getValue("SHOW_NUTRITION_FLG")+"'," +
					" OPT_USER = '" + parm.getValue("OPT_USER")+"'," +
					" OPT_DATE = TO_DATE('" + parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
					" OPT_TERM = '" + parm.getValue("OPT_TERM")+"' " +
					" WHERE ORDER_CODE = '" + parm.getValue("ORDER_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 肠内营养分类字典
	 * 查询
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm selectDataFL(TParm parm){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ORDER_CODE,ORDER_DESC,VALID_PERIOD,UNIT_CODE,");
		sql.append("FREQ_PRINT_FLG,TOTAL_PRINT_FLG,SHOW_NUTRITION_FLG,");
		sql.append("OPT_USER,OPT_DATE,OPT_TERM FROM NSS_EN_CATEGORY WHERE 1=1 ");
		if(StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))){
			sql.append(" AND ORDER_CODE = '"+ parm.getValue("ORDER_CODE")+"'");
		}
		sql.append(" ORDER BY ORDER_CODE ");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 肠内营养分类字典
	 * 删除操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm deleteDataFL(TParm parm){
		String sql = "DELETE FROM NSS_EN_CATEGORY WHERE " +
					"ORDER_CODE = '"+ parm.getValue("ORDER_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 
	 * 营养成分字典
	 * 插入操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm insertDataCF(TParm parm){
		String sql = "INSERT INTO NSS_NUTRITION (NUTRITION_CODE, NUTRITION_CHN_DESC, NUTRITION_ENG_DESC, " +
				" PY1, PY2, NRV, UNIT_CODE," +
				" OPT_USER, OPT_DATE, OPT_TERM) "
				+ "VALUES ('"
				+ parm.getValue("NUTRITION_CODE")+ "','"
				+ parm.getValue("NUTRITION_CHN_DESC")+ "','"
				+ parm.getValue("NUTRITION_ENG_DESC")+ "','"
				+ parm.getValue("PY1")+ "','"
				+ parm.getValue("PY2")+ "','"
				+ parm.getDouble("NRV")+ "','"
				+ parm.getValue("UNIT_CODE") + "','"
				+ parm.getValue("OPT_USER")+"',TO_DATE('"+ parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'),'"
				+ parm.getValue("OPT_TERM")+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 
	 * 营养成分字典
	 * 修改操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm updateDataCF(TParm parm){
		String sql = " UPDATE NSS_NUTRITION SET "+
					" NUTRITION_CHN_DESC = '" + parm.getValue("NUTRITION_CHN_DESC")+"'," +
					" NUTRITION_ENG_DESC ='" + parm.getValue("NUTRITION_ENG_DESC")+"'," +
					" PY1 = '" + parm.getValue("PY1")+"'," +
					" PY2 = '" + parm.getValue("PY2")+"'," +
					" NRV = '" + parm.getValue("NRV")+"'," +
					" UNIT_CODE = '" + parm.getValue("UNIT_CODE")+"'," +
					" OPT_USER = '" + parm.getValue("OPT_USER")+"'," +
					" OPT_DATE = TO_DATE('" + parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
					" OPT_TERM = '" + parm.getValue("OPT_TERM")+"' " +
					" WHERE NUTRITION_CODE = '" + parm.getValue("NUTRITION_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 营养成分字典
	 * 删除操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm deleteDataCF(TParm parm){
		String sql = "DELETE FROM NSS_NUTRITION WHERE " +
					"NUTRITION_CODE = '"+ parm.getValue("NUTRITION_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 营养成分字典
	 * 查询
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm selectDataCF(TParm parm) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NUTRITION_CODE,NUTRITION_CHN_DESC,NUTRITION_ENG_DESC,");
		sql.append("PY1,PY2,NRV,UNIT_CODE,OPT_USER,OPT_TERM,OPT_DATE ");
		sql.append(" FROM NSS_NUTRITION WHERE 1 = 1");

		if (StringUtils.isNotEmpty(parm.getValue("NUTRITION_CODE"))) {
			sql.append(" AND NUTRITION_CODE = '"
					+ parm.getValue("NUTRITION_CODE") + "'");
		}
		if (StringUtils.isNotEmpty(parm.getValue("NUTRITION_CHN_DESC"))) {
			sql.append(" AND NUTRITION_CHN_DESC = '"
					+ parm.getValue("NUTRITION_CHN_DESC") + "'");
		}

		sql.append(" ORDER BY NUTRITION_CODE ");
		
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询营养成分代码是该否存在
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm isExistCF(TParm parm){
		String sql = "SELECT COUNT(NUTRITION_CODE) AS COUNT " +
				"FROM NSS_NUTRITION WHERE NUTRITION_CODE = '" + parm.getValue("NUTRITION_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
		return result;
	}
	
	/**
	 * 查询营养成分代码是该否存在
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm getMaxNutritionCode(){
		String sql = "SELECT MAX(NUTRITION_CODE) AS MAX " +
				"FROM NSS_NUTRITION ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
		return result;
	}

	/**
	 * 
	 * 肠内营养容器
	 * 插入操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm insertDataRQ(TParm parm){
		String sql = " INSERT INTO NSS_EN_CONTAINER (CONTAINER_CODE, CONTAINER_DESC, CAPACITY, " +
				" CAPACITY_UNIT, ACTIVE_FLG," +
				" OPT_USER, OPT_DATE, OPT_TERM) "
				+ "VALUES ('"
				+ parm.getValue("CONTAINER_CODE")+ "','"
				+ parm.getValue("CONTAINER_DESC")+ "','"
				+ parm.getDouble("CAPACITY")+ "','"
				+ parm.getValue("CAPACITY_UNIT")+ "','"
				+ parm.getValue("ACTIVE_FLG")+ "','"
				+ parm.getValue("OPT_USER")+"',TO_DATE('"+ parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'),'"
				+ parm.getValue("OPT_TERM")+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 肠内营养容器
	 * 查询
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm selectDataRQ(TParm parm){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CONTAINER_CODE,CONTAINER_DESC,CAPACITY,CAPACITY_UNIT,ACTIVE_FLG,");
		sql.append("OPT_USER,OPT_DATE,OPT_TERM FROM NSS_EN_CONTAINER A WHERE 1 = 1 ");
		
		if(StringUtils.isNotEmpty(parm.getValue("CONTAINER_CODE"))){
			sql.append(" AND CONTAINER_CODE = '"+ parm.getValue("CONTAINER_CODE")+"'");
		}
		
		sql.append(" ORDER BY CONTAINER_CODE ");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 肠内营养容器
	 * 修改操作
	 * @param parm
	 * @return
	 * add by lich
	 */

	public TParm updateDataRQ(TParm parm){
		String sql = " UPDATE NSS_EN_CONTAINER SET "+
					" CONTAINER_DESC = '" + parm.getValue("CONTAINER_DESC")+"'," +
					" CAPACITY ='" + parm.getDouble("CAPACITY")+"'," +
					" CAPACITY_UNIT = '" + parm.getValue("CAPACITY_UNIT")+"'," +
					" ACTIVE_FLG = '" + parm.getValue("ACTIVE_FLG")+"'," +
					" OPT_USER = '" + parm.getValue("OPT_USER")+"'," +
					" OPT_DATE = TO_DATE('" + parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
					" OPT_TERM = '" + parm.getValue("OPT_TERM")+"' " +
					" WHERE CONTAINER_CODE = '" + parm.getValue("CONTAINER_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 肠内营养容器
	 * 删除操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm deleteDataRQ(TParm parm){
		String sql = "DELETE FROM NSS_EN_CONTAINER WHERE " +
					"CONTAINER_CODE = '"+ parm.getValue("CONTAINER_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 肠内营养容器
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm isExistRQ(TParm parm){
		String sql = "SELECT COUNT(CONTAINER_CODE) AS COUNT " +
				"FROM NSS_EN_CONTAINER WHERE CONTAINER_CODE = '" + parm.getValue("CONTAINER_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
		return result;
	}
	
	/**
	 * 肠内营养容器代码是该否存在
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm getMaxContainerCode(){
		String sql = "SELECT MAX(CONTAINER_CODE) AS MAX " +
				"FROM NSS_EN_CONTAINER ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
		return result;
	}

	/**
	 * 
	 * 饮食禁忌字典
	 * 插入操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm insertDataJJ(TParm parm){
		String sql = " INSERT INTO NSS_TABOO (TABOO_CODE, TABOO_CHN_DESC, TABOO_ENG_DESC, " +
				" PY1,PY2, SEQ, DESCRIPTION, TABOO_FLG,TABOO_SQL," +
				" OPT_USER, OPT_DATE, OPT_TERM) "
				+ "VALUES ('"
				+ parm.getValue("TABOO_CODE")+ "','"
				+ parm.getValue("TABOO_CHN_DESC")+ "','"
				+ parm.getValue("TABOO_ENG_DESC")+ "','"
				+ parm.getValue("PY1")+ "','"
				+ parm.getValue("PY2")+ "','"
				+ parm.getDouble("SEQ")+ "','"
				+ parm.getValue("DESCRIPTION") + "','"
				+ parm.getValue("TABOO_FLG") + "','"
				+ parm.getValue("TABOO_SQL") + "','"
				+ parm.getValue("OPT_USER")+"',TO_DATE('"+ parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'),'"
				+ parm.getValue("OPT_TERM")+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 饮食禁忌字典
	 * 修改操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm updateDataJJ(TParm parm){
		String sql = " UPDATE NSS_TABOO SET "+
					" TABOO_CHN_DESC = '" + parm.getValue("TABOO_CHN_DESC")+"'," +
					" TABOO_ENG_DESC ='" + parm.getValue("TABOO_ENG_DESC")+"'," +
					" PY1 = '" + parm.getValue("PY1")+"'," +
					" PY2 = '" + parm.getValue("PY2")+"'," +
					" SEQ = '" + parm.getDouble("SEQ")+"'," +
					" DESCRIPTION = '" + parm.getValue("DESCRIPTION")+"'," +
					" TABOO_FLG = '" + parm.getValue("TABOO_FLG")+"'," +
					" TABOO_SQL = '" + parm.getValue("TABOO_SQL")+"'," +
					" OPT_DATE = TO_DATE('" + parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
					" OPT_TERM = '" + parm.getValue("OPT_TERM")+"' " +
					" WHERE TABOO_CODE = '" + parm.getValue("TABOO_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 饮食禁忌字典
	 * 查询
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm selectDataJJ(TParm parm){
		StringBuffer sql = new StringBuffer(" SELECT  A.TABOO_CODE,A.TABOO_CHN_DESC,A.TABOO_ENG_DESC," +
        			" A.PY1,A.PY2 ,A.SEQ ,A.DESCRIPTION ,A.TABOO_FLG,A.TABOO_SQL ," +
        			"  (SELECT USER_NAME FROM SYS_OPERATOR  WHERE USER_ID = A.OPT_USER) AS OPT_USER,A.OPT_DATE,A.OPT_TERM FROM NSS_TABOO A WHERE 1 = 1 ");
		if(null != parm.getValue("TABOO_CODE")&& !"".equals(parm.getValue("TABOO_CODE"))){
			sql.append(" AND TABOO_CODE = '"+ parm.getValue("TABOO_CODE")+"'");
		}
		if(null != parm.getValue("TABOO_CHN_DESC")&& !"".equals(parm.getValue("TABOO_CHN_DESC"))){
			sql.append(" AND TABOO_CHN_DESC = '"+ parm.getValue("TABOO_CHN_DESC")+"'");
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 饮食禁忌字典
	 * 删除操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm deleteDataJJ(TParm parm){
		String sql = "DELETE FROM NSS_TABOO WHERE " +
					"TABOO_CODE = '"+ parm.getValue("TABOO_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 饮食禁忌字典分代码是该否存在
	 * @param parm
	 * @return  
	 * add by lich
	 */
	public TParm isExistJJ(TParm parm){
		String sql = "SELECT COUNT(TABOO_CODE) AS COUNT " +
				"FROM NSS_TABOO WHERE TABOO_CODE = '" + parm.getValue("TABOO_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
		return result;
	}
	
	/**
	 * 饮食禁忌字典分代码是该否存在
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm getMaxTabooCode(){
		String sql = "SELECT MAX(TABOO_CODE) AS MAX ,MAX(SEQ) AS SEQ  " +
				"FROM NSS_TABOO ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
		return result;
	}
	
	/**
	 * 
	 * 肠内营养配方字典
	 * 插入操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm insertDataPFM(TParm parm){
		String sql = " INSERT INTO NSS_EN_FORMULAM (FORMULA_CODE, FORMULA_CHN_DESC, FORMULA_ENG_DESC, " +
				" PY1,PY2, ACTIVE_FLG, CLINIC_PROJECT_FLG, ORDER_CODE,MEDI_UNIT,NUTRITION_CONTENT,NUTRITION_UNIT," +
				" OPT_USER, OPT_DATE, OPT_TERM) "
				+ "VALUES ('"
				+ parm.getValue("FORMULA_CODE")+ "','"
				+ parm.getValue("FORMULA_CHN_DESC")+ "','"
				+ parm.getValue("FORMULA_ENG_DESC")+ "','"
				+ parm.getValue("PY1")+ "','"
				+ parm.getValue("PY2")+ "','"
				+ parm.getValue("ACTIVE_FLG")+ "','"
				+ parm.getValue("CLINIC_PROJECT_FLG") + "','"
				+ parm.getValue("ORDER_CODE") + "','"
				+ parm.getValue("MEDI_UNIT") + "','"
				+ parm.getValue("NUTRITION_CONTENT") + "','"
				+ parm.getValue("NUTRITION_UNIT") + "','"
				+ parm.getValue("OPT_USER")+"',TO_DATE('"+ parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'),'"
				+ parm.getValue("OPT_TERM")+"')";
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 肠内营养配方字典
	 * 修改操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm updateDataPFM(TParm parm){
		String sql = " UPDATE NSS_EN_FORMULAM SET "+
					" FORMULA_CODE = '" + parm.getValue("FORMULA_CODE")+"'," +
					" FORMULA_CHN_DESC ='" + parm.getValue("FORMULA_CHN_DESC")+"'," +
					" FORMULA_ENG_DESC ='" + parm.getValue("FORMULA_ENG_DESC")+"'," +
					" PY1 = '" + parm.getValue("PY1")+"'," +
					" PY2 = '" + parm.getValue("PY2")+"'," +
					" ACTIVE_FLG = '" + parm.getValue("ACTIVE_FLG")+"'," +
					" CLINIC_PROJECT_FLG = '" + parm.getValue("CLINIC_PROJECT_FLG")+"'," +
					" ORDER_CODE = '" + parm.getValue("ORDER_CODE")+"'," +
					" MEDI_UNIT = '" + parm.getValue("MEDI_UNIT")+"'," +
					" NUTRITION_CONTENT = '" + parm.getValue("NUTRITION_CONTENT")+"'," +
					" NUTRITION_UNIT = '" + parm.getValue("NUTRITION_UNIT")+"'," +
					
					" OPT_DATE = TO_DATE('" + parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
					" OPT_TERM = '" + parm.getValue("OPT_TERM")+"' " +
					" WHERE FORMULA_CODE = '" + parm.getValue("FORMULA_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 肠内营养配方字典
	 * 删除操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm deleteDataPFM(TParm parm){
		String sql = "DELETE FROM NSS_EN_FORMULAM WHERE " +
					"FORMULA_CODE = '"+ parm.getValue("FORMULA_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 肠内营养配方字典
	 * 删除操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm deleteDataPFD(TParm parm){
		String sql = "DELETE FROM NSS_EN_FORMULAD WHERE " +
					"FORMULA_CODE = '"+ parm.getValue("FORMULA_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 查询肠内营养配方字典主项
	 * 
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm selectDataPFM(TParm parm){
		// modify by wangb 2015/05/29
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.FORMULA_CODE,A.FORMULA_CHN_DESC,A.FORMULA_ENG_DESC,A.PY1,A.PY2,A.ACTIVE_FLG,");
		sql.append("A.CLINIC_PROJECT_FLG,A.ORDER_CODE,B.ORDER_DESC,A.MEDI_UNIT,A.NUTRITION_CONTENT,");
		sql.append("A.NUTRITION_UNIT,A.OPT_USER, A.OPT_DATE,A.OPT_TERM ");
		sql.append(" FROM NSS_EN_FORMULAM A, SYS_FEE B ");
		sql.append(" WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("FORMULA_CODE"))) {
			sql.append(" AND A.FORMULA_CODE = '"
					+ parm.getValue("FORMULA_CODE") + "'");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("FORMULA_CHN_DESC"))) {
			sql.append(" AND A.FORMULA_CHN_DESC LIKE '%"
					+ parm.getValue("FORMULA_CHN_DESC") + "%'");
		}
		
		sql.append(" AND A.ORDER_CODE = B.ORDER_CODE(+) ");
		
		sql.append(" ORDER BY FORMULA_CODE ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 *肠内营养配方代码是该否存在
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm getMaxFormulaCode(){
		String sql = "SELECT MAX(FORMULA_CODE) AS MAX " +
				"FROM NSS_EN_FORMULAM ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
		return result;
	}
	
	/**
	 * 查询细项信息
	 * @param formulaCode
	 * @return
	 */
	public String getOrderDByNo(String formulaCode) {
		if ("".equals(formulaCode)) {
			return "";
		}
		return "SELECT A.NUTRITION_CODE,(SELECT NUTRITION_CHN_DESC FROM NSS_NUTRITION WHERE NUTRITION_CODE = A.NUTRITION_CODE)," +
				" A.NUTRITION_CONTENT,"
				+ " A.OPT_USER, A.OPT_DATE, A.OPT_TERM "
				+ " FROM NSS_EN_FORMULAD A "
				+ " WHERE FORMULA_CODE='"
				+ formulaCode
				+ " ' "
				+ " ORDER BY A.NUTRITION_CODE";
	} 
	
	/**
	 * 肠内营养配方代码是该否存在
	 * @param parm
	 * @return  
	 * add by lich
	 */
	public TParm isExistPFM(TParm parm){
		String sql = "SELECT COUNT(FORMULA_CODE) AS COUNT " +
				"FROM NSS_EN_FORMULAM WHERE FORMULA_CODE = '" + parm.getValue("FORMULA_CODE")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
		return result;
	}
	
	
	/**
	 * 
	 * 肠内营养配方字典细表NSS_EN_FORMULAD
	 * 插入操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm insertDataPFD(TParm parm){
		String sql = " INSERT INTO NSS_EN_FORMULAD (FORMULA_CODE, NUTRITION_CODE, NUTRITION_CONTENT, " +
				" OPT_USER, OPT_DATE, OPT_TERM) "
				+ "VALUES ('"
				+ parm.getValue("FORMULA_CODE")+ "','"
				+ parm.getValue("NUTRITION_CODE")+ "','"
				+ parm.getValue("NUTRITION_CONTENT")+ "','"
				+ parm.getValue("OPT_USER")+"',TO_DATE('"+ parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'),'"
				+ parm.getValue("OPT_TERM")+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 肠内营养配方字典NSS_EN_FORMULAD
	 * 修改操作
	 * @param parm
	 * @return
	 * add by lich
	 */
	public TParm updateDataPFD(TParm parm){
		String sql = " UPDATE NSS_EN_FORMULAD SET "+
					" NUTRITION_CONTENT ='" + parm.getValue("NUTRITION_CONTENT")+"'," +		
					" OPT_DATE = TO_DATE('" + parm.getValue("OPT_DATE")+"','yyyy-mm-dd hh24:mi:ss'), " +
					" OPT_TERM = '" + parm.getValue("OPT_TERM")+"' " +
					" WHERE FORMULA_CODE = '" + parm.getValue("FORMULA_CODE")+"'" +
					" AND NUTRITION_CODE = '"+ parm.getValue("NUTRITION_CODE")+"'" ;
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	        return result;
	    }
	    return result;
	}
	
	/**
	 * 查询营养粉使用情况
	 * 
	 * @param parm
	 *            TParm
	 * @return result
	 * @author wangbin
	 */
	public TParm queryNutritionalPowderUsage(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT ORDER_CODE,ORDER_CHN_DESC,SUM(DOSAGE_QTY) AS TOT_QTY,");
		sbSql.append("DOSAGE_UNIT,SUM(TOT_AMT) AS TOT_AMT ");
		sbSql.append(" FROM IBS_ORDD ");
		sbSql.append(" WHERE CASE_NO IN (");
		sbSql.append(" SELECT A.CASE_NO ");
		sbSql.append(" FROM NSS_EN_ORDERD A, NSS_EN_FORMULAM B, NSS_EN_DSPNM C ");
		sbSql.append(" WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("NUTRITIONAL_POWDER"))) {
			sbSql.append(" AND A.FORMULA_CODE = '");
			sbSql.append(parm.getValue("NUTRITIONAL_POWDER"));
			sbSql.append("' ");
		}
		sbSql.append(" AND A.FORMULA_CODE = B.FORMULA_CODE AND A.CASE_NO = C.CASE_NO ");
		sbSql.append(" AND A.EN_ORDER_NO = C.EN_ORDER_NO AND C.PREPARE_STATUS = '1' ");
		sbSql.append(" AND C.CANCEL_FLG = 'N' AND C.PREPARE_DATE BETWEEN TO_DATE ('");
		sbSql.append(parm.getValue("QUERY_DATE_S"));
		sbSql.append("','YYYYMMDDHH24MISS') AND TO_DATE ('");
		sbSql.append(parm.getValue("QUERY_DATE_E"));
		sbSql.append("','YYYYMMDDHH24MISS'))");
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sbSql.append(" AND ORDER_CODE = '");
			sbSql.append(parm.getValue("ORDER_CODE"));
			sbSql.append("' ");
		} else {
			sbSql.append(" AND ORDER_CODE IN (");
			sbSql.append("SELECT ORDER_CODE FROM NSS_EN_FORMULAM WHERE ORDER_CODE IS NOT NULL");
			sbSql.append(") ");
		}
		sbSql.append(" AND BILL_DATE BETWEEN TO_DATE ('");
		sbSql.append(parm.getValue("QUERY_DATE_S"));
		sbSql.append("','YYYYMMDDHH24MISS') AND TO_DATE ('");
		sbSql.append(parm.getValue("QUERY_DATE_E"));
		sbSql.append("','YYYYMMDDHH24MISS')");
		sbSql.append(" GROUP BY ORDER_CODE,ORDER_CHN_DESC,DOSAGE_UNIT ");
		sbSql.append(" ORDER BY ORDER_CODE ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 查询肠内营养交接数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryENHandOverData(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT CASE_NO,EN_ORDER_NO,EN_PREPARE_NO,EN_PREPARE_DATE,BED_NO,PREPARE_DATE,STATION_CODE,DEPT_CODE,");
		sbSql.append(" ORDER_CODE,ORDER_DESC,LABEL_QTY,HAND_OVER_USER,HAND_OVER_DATE,ACTUAL_HAND_OVER_QTY,HAND_OVER_REMARK,B.PAT_NAME");
		sbSql.append(" FROM NSS_EN_DSPNM A, SYS_PATINFO B ");
		sbSql.append(" WHERE PREPARE_DATE BETWEEN TO_DATE ('");
		sbSql.append(parm.getValue("PREPARE_DATE"));
		sbSql.append("000000','YYYYMMDDHH24MISS') AND TO_DATE ('");
		sbSql.append(parm.getValue("PREPARE_DATE"));
		sbSql.append("235959','YYYYMMDDHH24MISS') ");
		sbSql.append(" AND PREPARE_STATUS = '1' ");
		sbSql.append(" AND CANCEL_FLG = 'N' ");
		sbSql.append(" AND A.MR_NO = B.MR_NO ");
		
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sbSql.append(" AND DEPT_CODE = '");
			sbSql.append(parm.getValue("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("STATION_CODE"))) {
			sbSql.append(" AND STATION_CODE = '");
			sbSql.append(parm.getValue("STATION_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.equals("N", parm.getValue("HAND_OVER_STATUS"))) {
			sbSql.append(" AND HAND_OVER_USER IS NULL ");
		} else {
			sbSql.append(" AND HAND_OVER_USER IS NOT NULL ");
		}
		
		sbSql.append(" ORDER BY PREPARE_DATE ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 更新肠内营养交接数据
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 * @author wangbin
	 */
	public TParm onSaveByENHandOver(TParm parm, TConnection conn) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE NSS_EN_DSPNM SET HAND_OVER_USER = '");
		sbSql.append(parm.getValue("HAND_OVER_USER"));
		sbSql.append("',HAND_OVER_DATE = SYSDATE,");
		sbSql.append("ACTUAL_HAND_OVER_QTY = ");
		sbSql.append(parm.getInt("ACTUAL_HAND_OVER_QTY"));
		sbSql.append(",HAND_OVER_REMARK = '");
		sbSql.append(parm.getValue("HAND_OVER_REMARK"));
		sbSql.append("' WHERE 1 = 1 ");

		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("EN_ORDER_NO"))) {
			sbSql.append(" AND EN_ORDER_NO = '");
			sbSql.append(parm.getValue("EN_ORDER_NO"));
			sbSql.append("' ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("EN_PREPARE_NO"))) {
			sbSql.append(" AND EN_PREPARE_NO = '");
			sbSql.append(parm.getValue("EN_PREPARE_NO"));
			sbSql.append("' ");
		}

		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString(), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询肠内营养附加费用
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryENExtraFee(TParm parm) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.*,B.ORDER_DESC FROM NSS_EN_EXTRA_FEE A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CATEGORY_CODE"))) {
			sql.append(" AND A.CATEGORY_CODE = '");
			sql.append(parm.getValue("CATEGORY_CODE"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sql.append(" AND A.ORDER_CODE = '");
			sql.append(parm.getValue("ORDER_CODE"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ACTIVE_FLG"))) {
			sql.append(" AND A.ACTIVE_FLG = '");
			sql.append(parm.getValue("ACTIVE_FLG"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("CHARGE_TYPE"))) {
			sql.append(" AND A.CHARGE_TYPE = '");
			sql.append(parm.getValue("CHARGE_TYPE"));
			sql.append("' ");
		}
		
		sql.append(" ORDER BY A.CATEGORY_CODE,A.ORDER_CODE ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询计费明细
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryIbsOrdd(TParm parm) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM IBS_ORDD WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sql.append(" AND CASE_NO = '");
			sql.append(parm.getValue("CASE_NO"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sql.append(" AND ORDER_CODE = '");
			sql.append(parm.getValue("ORDER_CODE"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("BILL_DATE"))) {
			sql.append(" AND BILL_DATE > TO_DATE('");
			sql.append(parm.getValue("BILL_DATE"));
			sql.append("000000', 'YYYYMMDDHH24MISS') AND BILL_DATE < TO_DATE('");
			sql.append(parm.getValue("BILL_DATE"));
			sql.append("235959', 'YYYYMMDDHH24MISS') ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询肠内营养应收附加费构建计费数据
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryENExtraFeeForIbsOrdd(TParm parm) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.CASE_NO,E.ORDER_NO,E.ORDER_SEQ,A.MR_NO,A.REGION_CODE,A.STATION_CODE,");
		sql.append("A.DEPT_CODE,A.BED_NO,C.PAT_NAME,C.IPD_NO,C.CTZ1_CODE,C.CTZ2_CODE,C.CTZ3_CODE,");
		sql.append("D.ORDER_CODE,G.ORDER_CAT1_CODE,G.CAT1_TYPE,F.ORDERSET_GROUP_NO,F.ORDERSET_CODE,");
		sql.append("F.HIDE_FLG,F.ORDER_DEPT_CODE,F.ORDER_DR_CODE,G.UNIT_CODE ");
		sql.append(" FROM NSS_EN_DSPNM A,SYS_PATINFO C,NSS_EN_EXTRA_FEE D,NSS_EN_ORDERM E,ODI_ORDER F,SYS_FEE G ");
		sql.append(" WHERE A.MR_NO = C.MR_NO AND E.ORDER_CODE = D.CATEGORY_CODE ");
		sql.append(" AND A.EN_ORDER_NO = E.EN_ORDER_NO AND E.ORDER_NO = F.ORDER_NO AND E.ORDER_SEQ = F.ORDER_SEQ ");
		sql.append(" AND A.CASE_NO = F.CASE_NO AND D.ORDER_CODE = G.ORDER_CODE AND D.ACTIVE_FLG = 'Y' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sql.append(" AND A.CASE_NO = '");
			sql.append(parm.getValue("CASE_NO"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("EN_PREPARE_NO"))) {
			sql.append(" AND A.EN_PREPARE_NO = '");
			sql.append(parm.getValue("EN_PREPARE_NO"));
			sql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sql.append(" AND D.ORDER_CODE IN ('");
			sql.append(parm.getValue("ORDER_CODE"));
			sql.append("') ");
		}
		
		sql.append(" ORDER BY D.CHARGE_TYPE,D.ORDER_CODE ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新肠内营养附加费用字典
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm updateENExtraFee(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("UPDATE NSS_EN_EXTRA_FEE SET CHARGE_TYPE = '");
		sbSql.append(parm.getValue("CHARGE_TYPE"));
		sbSql.append("',ACTIVE_FLG = '");
		sbSql.append(parm.getValue("ACTIVE_FLG"));
		sbSql.append("',OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("' WHERE CATEGORY_CODE = '");
		sbSql.append(parm.getValue("CATEGORY_CODE"));
		sbSql.append("' AND ORDER_CODE = '");
		sbSql.append(parm.getValue("ORDER_CODE"));
		sbSql.append("' ");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 新增肠内营养附加费用字典
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm insertENExtraFee(TParm parm) {
		TParm result = this.update("insertENExtraFee", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * 删除肠内营养附加费用字典
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm deleteENExtraFee(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("DELETE FROM NSS_EN_EXTRA_FEE WHERE CATEGORY_CODE = '");
		sbSql.append(parm.getValue("CATEGORY_CODE"));
		sbSql.append("' AND ORDER_CODE = '");
		sbSql.append(parm.getValue("ORDER_CODE"));
		sbSql.append("' ");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
}
