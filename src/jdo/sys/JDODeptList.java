package jdo.sys;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title: 部门jdoList
 * </p>
 * 
 * <p>
 * Description:部门jdoList
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
 * @author ehui 200801112
 * @version 1.0
 */
public class JDODeptList extends TModifiedList{

	public JDODeptList() {
		StringBuffer sb=new StringBuffer();
		sb.append("userId:USER_ID;");
		sb.append("deptCode:DEPT_CODE;");
		sb.append("mainFlg:MAIN_FLG;");
		sb.append("optUser:OPT_USER;");
		sb.append("optTerm:OPT_TERM");
		setMapString(sb.toString());
	}
	/**
	 * 新增部门
	 * @return JDODept
	 */
	public JDODept newJDODept() {
		JDODept jdoDept = new JDODept();
		this.newData(jdoDept);
		return jdoDept;
	}

	/**
	 * 得到部门
	 * @param index int
	 * @return JDODept
	 */
	public JDODept getJDODept(int index) {
		return (JDODept) get(index);
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
			JDODept jdoDept = new JDODept();
			jdoDept.setMapString(getMapString());
			if (!jdoDept.initParm(parm, i))
				return false;
			add(jdoDept);
		}
		return true;
	}
}
