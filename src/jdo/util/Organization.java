package jdo.util;

import jdo.sys.SYSNewRegionTool;

import com.dongyang.jdo.TStrike;

/**
 *
 * <p>
 * Title: 组织共用
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Organization extends TStrike {

	/**
	 * 得到医院中文全称
	 *
	 * @param region_code
	 *            区域代码
	 * @return
	 */
	public String getHospitalCHNFullName(String region_code) {
		if (isClientlink())
			return (String) callServerMethod(region_code);
		return SYSNewRegionTool.getInstance().getHospitalName(region_code)
				.getValue("REGION_CHN_DESC", 0);
	}

        /**
         * 得到医院英文全称
         *
         * @param region_code
         *            区域代码
         * @return
         */
        public String getHospitalENGFullName(String region_code) {
                if (isClientlink())
                        return (String) callServerMethod(region_code);
                return SYSNewRegionTool.getInstance().getHospitalName(region_code)
                                .getValue("REGION_ENG_DESC", 0);
        }

	/**
	 * 得到医院中文简称
	 *
	 * @param region_code
	 *            区域代码
	 * @return
	 */
	public String getHospitalCHNShortName(String region_code) {
		if (isClientlink())
			return (String) callServerMethod(region_code);
		return SYSNewRegionTool.getInstance().getHospitalName(region_code)
				.getValue("REGION_CHN_ABN", 0);
	}

	/**
	 * 是否启用结构化病历
	 *
	 * @param region_code
	 *            区域代码
	 * @return
	 */
	public boolean getHospitalEMR(String region_code) {
		if (isClientlink())
			return (Boolean) callServerMethod(region_code);
		return SYSNewRegionTool.getInstance().isEMR(region_code);
	}

	/**
	 * 是否启用LDAP
	 *
	 * @param region_code
	 *            区域代码
	 * @return
	 */
	public boolean getHospitalLDAP(String region_code) {
		if (isClientlink())
			return (Boolean) callServerMethod(region_code);
		return SYSNewRegionTool.getInstance().isLDAP(region_code);
	}
}
