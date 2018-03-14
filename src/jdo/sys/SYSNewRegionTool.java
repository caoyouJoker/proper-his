package jdo.sys;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SYSNewRegionTool extends TJDOTool {
	public SYSNewRegionTool() {
		setModuleName("sys\\SYSNewRegionModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	public static SYSNewRegionTool instanceObject;

	/**
	 * 得到实例
	 *
	 * @return SYSRegionTool
	 */
	public static SYSNewRegionTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSNewRegionTool();
		return instanceObject;
	}

	/**
	 * 查询页面flag的顺序,一遍拼出20位的形如“yyyyynnnnn”的数据作为state_list的字段值
	 *
	 * @param GROUP_ID
	 * @return
	 */
	public TParm getStateList(String GROUP_ID) {
		TParm parm = new TParm();
		parm.setData("GROUP_ID", GROUP_ID);
		TParm result = query("selectStateList", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据条件查询
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("selectByConditions", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 添加新数据
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TParm result = this.update("insertdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm) {
		TParm result = this.update("update", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 删除
	 *
	 * @param <any>
	 *            TParm
	 * @return TParm
	 */
	public TParm onDelete(TParm parm) {
		TParm result = this.update("delete", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/*-------------------2009-3-24 zhangk start--------------------*/
	/**
	 * 是否启用结构化病历
	 *
	 * @return boolean
	 */
	public boolean isEMR(String REGION_CODE) {

		boolean result = getState("EMR", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用LDAP
	 *
	 * @return boolean
	 */
	public boolean isLDAP(String REGION_CODE) {
		boolean result = getState("LDAP", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用联机状态
	 *
	 * @return boolean
	 */
	public boolean isONLINE(String REGION_CODE) {
		boolean result = getState("ONLINE", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用门诊合理用药
	 *
	 * @return boolean
	 */
	public boolean isOREASONABLEMED(String REGION_CODE) {
		boolean result = getState("REASONABLEMED_O", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用住院合理用药
	 *
	 * @return boolean
	 */
	public boolean isIREASONABLEMED(String REGION_CODE) {
		boolean result = getState("REASONABLEMED_I", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用医疗卡
	 *
	 * @return boolean
	 */
	public boolean isEKT(String REGION_CODE) {
		boolean result = getState("EKT", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用医保卡
	 *
	 * @return boolean
	 */
	public boolean isINS(String REGION_CODE) {
		boolean result = getState("INS", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用门诊医保调试
	 *
	 * @return boolean
	 */
	public boolean isODO(String REGION_CODE) {
		boolean result = getState("ODO", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用门诊挂号与医保链接
	 *
	 * @return boolean
	 */
	public boolean isREG(String REGION_CODE) {
		boolean result = getState("REG", REGION_CODE);
		return result;
	}

	/**
	 * 是否启用门诊收费及医保链接
	 *
	 * @return boolean
	 */
	public boolean isCHARGE(String REGION_CODE) {
		boolean result = getState("CHARGE", REGION_CODE);
		return result;
	}

	/**
	 * 根据序号查询STATE_LIST字符串中的值
	 *
	 * @param String
	 *            name 要查询的模块名
	 * @param String
	 *            REGION_CODE 院区号
	 * @return boolean
	 */
	private boolean getState(String name, String REGION_CODE) {
		TParm start = this.getStateList("SYS_REGION_FLG");
		int index = -1;
		// 循环比较模块名称，判断压迫查询的模块在第几行
		for (int i = 0; i < start.getCount("ID"); i++) {
			if (start.getValue("ID", i).equals(name)) {
				index = i;
			}
		}
		TParm parm = new TParm();
		parm.setData("REGION_CODE", REGION_CODE);
		TParm result = this.query("selectStateList2", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		String state = result.getValue("STATE_LIST");
//		System.out.println("result-===" + result);
//		System.out.println("state" + state);
		if(state==null||state.equals(""))
		{
			return false;
		}
		
		if (index < 1)
			return false;
		state = state.substring(index + 1, index + 2);
		return TCM_Transform.getBoolean(state);
	}

	/*-------------------2009-3-24 zhangk end--------------------*/

	/**
	 * 得到所有启用院区的主院区注记
	 */
	public TParm getAllRegion() {
		String sql = "SELECT MAIN_FLG,REGION_CODE FROM SYS_REGION WHERE ACTIVE_FLG = 'Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 得到医院名称(中文全称，中文简称，英文全称,英文简称)
	 *
	 * @param region_code
	 * @return TParm
	 */
	public TParm getHospitalName(String region_code) {
		String sql = "SELECT REGION_CHN_DESC, REGION_CHN_ABN, REGION_ENG_DESC, REGION_ENG_ABN "
				+ "FROM SYS_REGION "
				+ "WHERE REGION_CODE='"
				+ region_code
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}
