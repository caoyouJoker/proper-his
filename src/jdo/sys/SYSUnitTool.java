/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * <p>
 * Title: 药品单位
 * </p>
 *
 * <p>
 * Description:药品单位
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080901
 * @version 1.0
 */
public class SYSUnitTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static SYSUnitTool instanceObject;

	/**
	 * 得到实例
	 *
	 * @return SYSPhaRouteTool
	 */
	public static SYSUnitTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSUnitTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSUnitTool() {
		setModuleName("sys\\SYSUnitModule.x");
		onInit();
	}
        /**
         *得到单位的map
         * @return Map
         */
        public Map getUnitMap() {
            Map map = new HashMap();
            TParm parm=getUnitCodeAndUnitDesc();
            if(parm.getErrCode()<0)
                return map;
            int count=parm.getCount();
            for(int i=0;i<count;i++){
                map.put(parm.getValue("UNIT_CODE",i),parm.getValue("UNIT_CHN_DESC",i));
            }
            return map;
        }
        /**
        * 初始化界面，查询所有的数据
        *
        * @return TParm
        */
       public TParm getUnitCodeAndUnitDesc() {
               TParm parm = new TParm();
               TParm result = query("getUnitAndDesc", parm);
               if (result.getErrCode() < 0)
                       err("ERR:" + result.getErrCode() + result.getErrText()
                                       + result.getErrName());
               return result;
       }

        /**
         * 初始化界面，查询所有的数据
	 *
	 * @return TParm
	 */
	public TParm selectall() {
		TParm parm = new TParm();
		// parm.setData("CODE",CODE);
		TParm result = query("selectall", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据单位代码查询数据
	 *
	 * @param UNIT_CODE
	 *            String 单位代码
	 * @return TParm
	 */
	public TParm selectdata(String UNIT_CODE) {
		TParm parm = new TParm();
		parm.setData("UNIT_CODE", UNIT_CODE);
		TParm result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 新增指定单位代码得到数据
	 *
	 * @param UNIT_CODE
	 *            String
	 * @return TParm
	 */
	public TParm insertdata(TParm parm) {
		String UNIT_CODE = parm.getValue("UNIT_CODE");
		// System.out.println("UNIT_CODE"+UNIT_CODE);
		TParm result = new TParm();
		if (!existsUNITCODE(UNIT_CODE)) {
			result = update("insertdata", parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		} else {
			result.setErr(-1, "单位代码 " + UNIT_CODE + " 已经存在!");
			return result;
		}

		return result;
	}

	/**
	 * 判断是否存在数据
	 *
	 * @param UNIT_CODE
	 *            String
	 * @return boolean TRUE 存在 FALSE 不存在
	 */
	public boolean existsUNITCODE(String UNIT_CODE) {
		TParm parm = new TParm();
		parm.setData("UNIT_CODE", UNIT_CODE);
		// System.out.println("existsUNITCODE"+UNIT_CODE);
		return getResultInt(query("existsUNITCODE", parm), "COUNT") > 0;
	}

	/**
	 * 更新指定UNIT_CODE数据
	 *
	 * @param UNIT_CODE
	 *            String
	 * @return TParm
	 */
	public TParm updatedata(TParm parm) {
		TParm result = new TParm();
		String UNIT_CODE = parm.getValue("UNIT_CODE");
		// System.out.println("true or false"+existsUNITCODE(UNIT_CODE));
		if (existsUNITCODE(UNIT_CODE)) {
			result = update("updatedata", parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		} else {
			result.setErr(-1, "单位代码 " + UNIT_CODE + " 刚刚被删除！");
			return result;
		}

		return result;
	}

	/**
	 * 删除指定单位代码数据
	 *
	 * @param UNIT_CODE
	 *            String
	 * @return boolean
	 */
	public TParm deletedata(String UNIT_CODE) {
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("UNIT_CODE", UNIT_CODE);
		if (!allowupdate(UNIT_CODE)) {
			result = update("deletedata", parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
		} else {
			result.setErr(-1, "单位代码 " + UNIT_CODE + " 正在被使用，不允许删除！");
			return result;
		}

	}

	/**
	 * 根据UNIT_CODE判断SYS_FEE中有没有该UNIT_CODE，如有则不允许删除
	 *
	 * @param UNIT_CODE
	 * @return
	 */
	public boolean allowupdate(String UNIT_CODE) {
		TParm parm = new TParm();
		parm.setData("UNIT_CODE", UNIT_CODE);
		return getResultInt(query("allowupdate", parm), "COUNT") > 0;
	}
}
