package jdo.sta;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 * Title: 医保程序
 * </p>
 * 
 * <p>
 * Description: 内嵌式医保程序
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author Miracle
 * @version JavaHis 1.0
 */
public class STADeptListTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static STADeptListTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static STADeptListTool getInstance() {
		if (instanceObject == null)
			instanceObject = new STADeptListTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public STADeptListTool() {
		setModuleName("sta\\STADeptListModule.x");
		onInit();
	}

	/**
	 * 新增科室
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertdata(TParm parm) {
		TParm result = new TParm();
		result = update("insertdata", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改科室信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatedata(TParm parm) {
		TParm result = new TParm();
		result = update("updatedata", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 删除科室
	 * 
	 * @param regMethod
	 *            String
	 * @return boolean ============pangben modify 20110525 添加区域参数
	 */
	public TParm deletedata(String staSeq, String reginCode) {
		TParm parm = new TParm();
		parm.setData("STA_SEQ", staSeq);
		parm.setData("REGION_CODE", reginCode);
		TParm result = update("deletedata", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据部门CODE查询信息(右忽略)
	 * 
	 * @param dept_code
	 *            String 部门CODE
	 * @param regionCode
	 *            String 区域CODE
	 * @return TParm //============pangben modify 20110519 添加区域参数
	 */
	public TParm selectdata(String dept_code, String regionCode) {
		TParm parm = new TParm();
		dept_code += "%";
		parm.setData("DEPT_CODE", dept_code);
		// ============pangben modify 20110519 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ============pangben modify 20110519 stop
		TParm result = query("selectdata", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询最大排序号
	 * 
	 * @return String
	 */
	public String getMaxSEQ() {
		TParm result = query("selectMaxSEQ");
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return "";
		}
		return result.getValue("SEQ", 0);
	}

	/**
	 * 查询部门中间表中的门诊部门
	 * 
	 * @return TParm =========pangben modify 20110519 添加区域参数
	 */
	public TParm selectOE_DEPT(String regionCode) {
		TParm parm = new TParm();
		// =========pangben modify 20110519 start 添加区域参数
		if (null != regionCode && !"".equals(regionCode))
			parm.setData("REGION_CODE", regionCode);
		// =========pangben modify 20110519 stop
		TParm result = query("selectOE_DEPT", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询部门中间表的住院部门
	 * 
	 * @return TParm ===============pangben modify 20110520 添加区域参数
	 */
	public TParm selectIPD_DEPT(TParm parm) {
		TParm result = query("selectIPD_DEPT", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据CODE查询对应的部门信息
	 * 
	 * @param Code
	 *            String
	 * @return TParm
	 */
	public TParm selectDeptByCode(String Code, String StationCode,
			String regionCode) {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", Code);
		if (null != StationCode && StationCode.length() > 0)
			parm.setData("STATION_CODE", StationCode);
		// ==========pangben modify 20110523 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ==========pangben modify 20110523 stop
		TParm result = this.query("selectDeptByCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据CODE查询对应的部门信息
	 * 
	 * @param Code
	 *            String
	 * @return TParm
	 */
	public TParm selectNewDeptByCode(String Code, String regionCode) {
		String sql = " SELECT DISTINCT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE"
				+ " FROM STA_OEI_DEPT_LIST "
				+ " WHERE DEPT_CODE ='"
				+ Code
				+ "'";
		if (null != regionCode && regionCode.length() > 0)
			sql += sql + " AND REGION_CODE ='" + regionCode + "'";
		// ==========pangben modify 20110523 stop
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据CODE查询对应的部门信息
	 * 
	 * @param Code
	 *            String
	 * @return TParm
	 */
	public TParm selectNewIPDDeptCode(String Code, String regionCode) {
		String sql = "SELECT DISTINCT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE  "//modify by wanglong 20131210
				+ "FROM STA_OEI_DEPT_LIST "
				+ "WHERE IPD_DEPT_CODE IS NOT NULL  ";		
		if (null != regionCode && regionCode.length() > 0)
			sql += sql + " AND REGION_CODE ='" + regionCode + "'";
		if (null != Code && Code.length() > 0)
			sql += sql + " AND DEPT_CODE ='" + Code + "'";
		if(sql.length()>0)
			sql += sql + "ORDER BY TO_NUMBER(SEQ) ";
		// ==========pangben modify 20110523 stop
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据住院科室科室CODE查询科室信息
	 * 
	 * @param Code
	 *            String
	 * @return TParm ==============pangben modify 20110525 添加区域参数
	 */
	public TParm selectDeptByIPDCODE(String Code, String regionCode) {
		TParm parm = new TParm();
		parm.setData("IPD_DEPT_CODE", Code);
		// ===================pangben modify 20110525 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===================pangben modify 20110525 stop
		TParm result = this.query("selectDeptByIPDCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据门诊科室科室CODE查询科室信息
	 * 
	 * @param Code
	 *            String
	 * @return TParm
	 */
	public TParm selectDeptByOECODE(String Code, String regionCode) {
		TParm parm = new TParm();
		parm.setData("OE_DEPT_CODE", Code);
		// ===================pangben modify 20110525 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===================pangben modify 20110525 stop
		TParm result = this.query("selectDeptByOECode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询所有中间档科室
	 * 
	 * @return TParm ===========pangben modify 20110524 添加区域参数
	 */
	public TParm selectAll(String regionCode) {
		// ==========pangben modify 20110524 start
		TParm parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		TParm result = this.query("selectAll", parm);
		// ==========pangben modify 20110524 stop
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 获取门急诊科室字段的Map，用于前台判断某一科室是否为门急诊科室
	 * 
	 * @return TParm ==============pangben modify 20110524 添加区域参数
	 */
	public Map getOEDeptMap(String regionCode) {
		TParm data = this.selectAll(regionCode);// ==============pangben modify
		// 20110524
		Map result = new HashMap();
		for (int i = 0; i < data.getCount(); i++) {
			result.put(data.getValue("DEPT_CODE", i), data.getValue(
					"OE_DEPT_CODE", i));
		}
		return result;
	}

	/**
	 * 获取住院科室字段的Map，用于前台判断某一科室是否为住院科室
	 * 
	 * @return TParm ==============pangben modify 20110524 添加区域参数
	 */
	public Map getIPDDeptMap(String regionCode) {
		TParm data = this.selectAll(regionCode);// ==============pangben modify
		// 20110524
		Map result = new HashMap();
		for (int i = 0; i < data.getCount(); i++) {
			result.put(data.getValue("DEPT_CODE", i), new String[] {
					data.getValue("IPD_DEPT_CODE", i),
					data.getValue("STATION_CODE", i) });
		}
		return result;
	}

}
