package jdo.sys;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title: 证照jdoList
 * </p>
 * 
 * <p>
 * Description:证照jdoList
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20081111
 * @version 1.0
 */
public class JDOLicenceList extends TModifiedList  {
	
	public JDOLicenceList(){
		StringBuffer sb=new StringBuffer();
		sb.append("userId:USER_ID;");
		sb.append("lcsClassCode:LCS_CLASS_CODE");
		sb.append("lcsNo:LCS_NO;");
		sb.append("effLcsDate:EFF_LCS_DATE;");
		sb.append("endLcsDate:END_LCS_DATE;");
		sb.append("optUser:OPT_USER;");
		sb.append("optTerm:OPT_TERM");
		setMapString(sb.toString());
	}
	/**
	 * 新增部门
	 * @return JDOLicense
	 */
	public JDOLicense newJDOLicense() {
		JDOLicense jdoLicense = new JDOLicense();
		this.newData(jdoLicense);
		return jdoLicense;
	}

	/**
	 * 得到部门
	 * @param index int
	 * @return JDOLicense
	 */
	public JDOLicense getJDOLicense(int index) {
		return (JDOLicense) get(index);
	}

	/**
	 * 初始化TPARM
	 * @param parm
	 * @return 真：成功，假：失败
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			JDOLicense jdoLicense = new JDOLicense();
			jdoLicense.setMapString(getMapString());
			if (!jdoLicense.initParm(parm, i))
				return false;
			add(jdoLicense);
		}
		return true;
	}
}
