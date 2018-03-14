package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>Title: 用户管理</p>
 *
 * <p>Description:用户管理</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author ehui 20090106
 * @version 1.0
 */
public class SYSOrderSetDetailTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static SYSOrderSetDetailTool instanceObject;

	/**
	 * 得到实例
	 * @return SYSOrderSetDetailTool
	 */
	public static SYSOrderSetDetailTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSOrderSetDetailTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSOrderSetDetailTool() {
		setModuleName("sys\\SYSOrderSetDetailModule.x");
		onInit();
	}
	/**
	 * 根据集合医嘱主项检索关联了sys_fee的数据
	 * @param orderSetCode 集合医嘱检索
	 * @return
	 */
        public TParm selectByOrderSetCode(String orderSetCode) {
                TParm parm = new TParm();
                parm.setData("ORDERSET_CODE", orderSetCode);
                TParm result = new TParm();
                result = query("selectByOrderSetCode", parm);
                if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                }
                //System.out.println("result====="+result);
                return result;

	}
        public TParm selectByOrderSetCodeNew(String orderSetCode) {
            TParm parm = new TParm();
            parm.setData("ORDERSET_CODE", orderSetCode);
            TParm result = new TParm();
            result = query("selectByOrderSetCodeNew", parm);
           // System.out.println("result"+result);
            if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                                    + result.getErrName());
            }
            //System.out.println("result====="+result);
            return result;

}
        /**
         * 根据集合医嘱主项检索关联了sys_fee的数据
         * @param parm TParm
         * @return TParm
         */
        public TParm selSyeFeeData(TParm parm) {
            TParm result = new TParm();
            result = query("selSyeFeeData", parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
            }
            return result;

        }

}
