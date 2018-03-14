package jdo.sys;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title: 用户jdoList
 * </p>
 * 
 * <p>
 * Description:用户jdoList
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
public class JDOOperatorList extends TModifiedList{

	public JDOOperatorList(){
		StringBuffer sb=new StringBuffer();
		sb.append("userId:USER_ID;");
		sb.append("userName:USER_NAME;");
		sb.append("py1:PY1;");
		sb.append("py2:PY2;");
		sb.append("deptCode:DEPT_CODE;");
		sb.append("idCode:ID_CODE;");
		sb.append("sexCode:SEX_CODE;");
		sb.append("userPassword:USER_PASSWORD;");
		sb.append("posCode:POS_CODE;");
		sb.append("roleId:ROLE_ID;");
		sb.append("activeDate:ACTIVE_DATE;");
		sb.append("endDate:END_DATE;");
		sb.append("pubFunction:PUB_FUNCTION;");
		sb.append("email:E_MAIL;");
		sb.append("lcsNo:LCS_NO;");
		sb.append("effLcsDate:EFF_LCS_DATE;");
		sb.append("endLcsDate:END_LCS_DATE;");
		sb.append("fullTimeFlg:FULLTIME_FLG;");
		sb.append("ctrlFlg:CTRL_FLG;");
		sb.append("regionCode:REGION_CODE;");
		sb.append("rcntLoginDate:RCNT_LOGIN_DATE;");
		sb.append("rcntLogoutDate:RCNT_LOGOUT_DATE;");
		sb.append("rcntIp:RCNT_IP;");
		sb.append("optUser:OPT_USER;");
		sb.append("optTerm:OPT_TERM");
		
		setMapString(sb.toString());
	}
	/**
	 * 新增用户
	 * @return JDOStation
	 */
	public JDOOperator newJDOOperator() {
		JDOOperator jdoOperator = new JDOOperator();
		this.newData(jdoOperator);
		return jdoOperator;
	}

	/**
	 * 得到用户
	 * @param index int
	 * @return JDOOperator
	 */
	public JDOOperator getJDOOperator(int index) {
		return (JDOOperator) get(index);
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
			JDOOperator jdoOperator = new JDOOperator();
			jdoOperator.setMapString(getMapString());
			if (!jdoOperator.initParm(parm, i))
				return false;
			add(jdoOperator);
		}
		return true;
	}

}
