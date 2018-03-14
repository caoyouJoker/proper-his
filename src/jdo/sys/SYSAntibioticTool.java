package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title:抗生素等级Tool
 * </p>
 *
 * <p>
 * Description:抗生素等级Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.06.14
 * @version 1.0
 */
public class SYSAntibioticTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SYSAntibioticTool instanceObject;

	/**
	 * 得到实例
	 *
	 * @return PatTool
	 */
	public static SYSAntibioticTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSAntibioticTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSAntibioticTool() {
		setModuleName("sys\\SYSAntibioticModule.x");
		onInit();
	}

	/**
	 * 查询
	 * @param antibiotic_code
	 * @param antibiotic_desc
	 * @return
	 */
	public TParm selectdata(String antibiotic_code, String antibiotic_desc) {
		TParm parm = new TParm();
		if (antibiotic_code != null && antibiotic_code.length() > 0
				&& antibiotic_desc != null && antibiotic_desc.length() > 0) {
			parm.setData("ANTIBIOTIC_CODE", antibiotic_code);
			parm.setData("ANTIBIOTIC_DESC", antibiotic_desc);
		}
		TParm result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

        /**
         * 查询抗生素最大使用天数
         * @param order_code String
         * @return int
         */
        public int getAntibioticTakeDays(String order_code) {
        	String sql=SYSSQL.getAntibioticTakeDays(order_code);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return -1;
            }
            if (result.getCount("TAKE_DAYS") == 0) {
                return -1;
            }
            return result.getInt("TAKE_DAYS", 0);
        }
}
