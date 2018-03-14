package jdo.sys;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.adm.ADMDiagTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMXMLTool;
import jdo.reg.ws.RegQETool;

import org.apache.commons.lang.StringUtils;

import action.reg.REGAction;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.StringUtil;

/**
 * �ֵ�ͳһ�������ݷ�����
 * 
 * @author lixiang
 * 
 */
public class SYSDictionaryServiceTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	private static SYSDictionaryServiceTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatTool
	 */
	public static SYSDictionaryServiceTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSDictionaryServiceTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SYSDictionaryServiceTool() {
		// setModuleName("sys\\SYSDictionaryModule.x");
		onInit();
	}

	/**
	 * �õ���������
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param tableName
	 *            String
	 * @return String[]
	 */
	public String[] getData(String code, String password, String tableName) {
		String result = checkAuthority(code, password, tableName, "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool.getInstance().selectList(
				"select ID,CHN_DESC,ENG_DESC from SYS_DICTIONARY where GROUP_ID='"
						+ tableName + "' Order By ID");
	}

	/*
	 * �õ�������Ϣ
	 * 
	 * @param code String
	 * 
	 * @param password String
	 * 
	 * @return String[]
	 */
	public String[] getDeptInf(String code, String password) {
		String result = checkAuthority(code, password, "DEPT_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"select DEPT_CODE,CASE WHEN DEPT_ANOTHER_CHN_DESC IS NULL THEN DEPT_CHN_DESC ELSE DEPT_CHN_DESC || '(' || DEPT_ANOTHER_CHN_DESC || ')' END DEPT_CHN_DESC,DEPT_ENG_DESC,DEPT_ABS_DESC from SYS_DEPT Order By DEPT_CODE");
	}

	/**
	 * �õ�����
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getStation(String code, String password) {
		String result = checkAuthority(code, password, "DEPT_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"SELECT STATION_CODE,STATION_DESC,PY1,PY2,ENG_DESC,DEPT_CODE,ORG_CODE FROM SYS_STATION ORDER BY STATION_CODE");
	}

	/**
	 * ͬ����ɫ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getReulInf(String code, String password) {
		String result = checkAuthority(code, password, "ROLE_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='ROLE' Order By ID");
	}

	/**
	 * �õ�������Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getDeptInfCode(String code, String password, String deptcode) {
		String result = checkAuthority(code, password, "DEPT_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"select DEPT_CODE,CASE WHEN DEPT_ANOTHER_CHN_DESC IS NULL THEN DEPT_CHN_DESC ELSE DEPT_CHN_DESC || '(' || DEPT_ANOTHER_CHN_DESC || ')' END DEPT_CHN_DESC,DEPT_ENG_DESC,DEPT_ABS_DESC from SYS_DEPT WHERE DEPT_CODE='"
								+ deptcode + "' Order By DEPT_CODE");
	}

	/**
	 * �û���Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getOperatorInf(String code, String password) {
		String result = checkAuthority(code, password, "OPERATOR_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"SELECT A.USER_ID,A.USER_NAME,A.SEX_CODE,A.E_MAIL,A.ROLE_ID,B.DEPT_CODE,C.POS_CHN_DESC,C.POS_TYPE from SYS_OPERATOR A,SYS_OPERATOR_DEPT B,SYS_POSITION C WHERE A.USER_ID=B.USER_ID AND B.MAIN_FLG='Y' AND A.END_DATE > SYSDATE  AND A.POS_CODE = C.POS_CODE(+) Order By A.SEQ");
	}
	
	/**
	 * �õ�ҩƷ���������
	 * 
	 * @return int[]
	 */
	private int[] getPhaRule() {
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5 FROM SYS_RULE WHERE  RULE_TYPE='PHA_RULE'"));
		return new int[] { parm.getInt("CLASSIFY1", 0),
				parm.getInt("CLASSIFY2", 0), parm.getInt("CLASSIFY3", 0),
				parm.getInt("CLASSIFY4", 0), parm.getInt("CLASSIFY5", 0) };
	}

	/**
	 * LISҽ����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getLisOrder(String code, String password) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"SELECT ORDER_CODE,ORDER_DESC from SYS_FEE WHERE CAT1_TYPE='LIS' AND ORDERSET_FLG='Y' Order By ORDER_CODE");
	}

	/**
	 * ҩƷ�����ѯ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param name
	 *            String
	 * @return String[]
	 */
	public String[] getPhaClassify(String code, String password, String name) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT CATEGORY_CODE,CATEGORY_CHN_DESC,CATEGORY_ENG_DESC FROM SYS_CATEGORY"
				+ " WHERE RULE_TYPE='PHA_RULE' ";
		if (name == null || name.length() == 0)
			sql += "AND LENGTH(CATEGORY_CODE)=2";
		else {
			int[] pule = getPhaRule();
			int l = name.length();
			int count = 0;
			for (int i = 0; i < pule.length; i++) {
				count += pule[i];
				if (count > l)
					break;
			}
			if (l >= count)
				return new String[] {};
			sql += "AND LENGTH(CATEGORY_CODE)=" + count
					+ " AND CATEGORY_CODE LIKE '" + name
					+ "%' ORDER BY CATEGORY_CODE";
		}
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ҩƷ��ѯ����������
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param classify
	 *            String
	 * @return String[]
	 */
	public String[] getPhaInf(String code, String password, String classify) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		if (classify == null || classify.length() == 0)
			return new String[] {};
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.TRADE_ENG_DESC,A.GOODS_DESC,A.ALIAS_DESC,"
				+ "       A.SPECIFICATION,A.OWN_PRICE,B.UNIT_CHN_DESC,A.PY1,C.MAN_CHN_DESC"
				+ "  FROM SYS_FEE A, SYS_UNIT B,SYS_MANUFACTURER C"
				+ " WHERE A.ORDER_CODE LIKE '"
				+ classify
				+ "%' AND A.UNIT_CODE = B.UNIT_CODE(+) AND A.MAN_CODE=C.MAN_CODE(+)";
				
		//System.out.println("--------getPhaInf--------"+sql);
		return TJDODBTool.getInstance().selectList(sql);
	}
	
	/**
	 * 
	 * @param code
	 * @param password
	 * @param cateType
	 *            (PHA|OTH)
	 * @return
	 */
	public String[] getOrderByCatType(String code, String password,
			String cateType) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		//
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.TRADE_ENG_DESC,A.GOODS_DESC,A.ALIAS_DESC,"
				+ "       A.SPECIFICATION,A.OWN_PRICE,B.UNIT_CHN_DESC,A.PY1,C.MAN_CHN_DESC"
				+ "  FROM SYS_FEE A, SYS_UNIT B,SYS_MANUFACTURER C"
				+ " WHERE 1=1";
				if(cateType!=null&&!cateType.equals("")){
					sql+= " AND A.CAT1_TYPE ='"+cateType+"'";
				}
				sql+= " AND A.UNIT_CODE = B.UNIT_CODE(+) AND A.MAN_CODE=C.MAN_CODE(+)";
		return TJDODBTool.getInstance().selectList(sql);

	}
	

	/**
	 * �õ�ҩƷ��Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param orderCode
	 *            String
	 * @return String[]
	 */
	public String[] getPhaOrder(String code, String password, String ordercode) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		if (ordercode == null || ordercode.length() == 0)
			return new String[] {};
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.TRADE_ENG_DESC,A.GOODS_DESC,A.ALIAS_DESC,"
				+ "       A.SPECIFICATION,A.OWN_PRICE,B.UNIT_CHN_DESC,A.PY1,C.MAN_CHN_DESC"
				+ "  FROM SYS_FEE A, SYS_UNIT B,SYS_MANUFACTURER C"
				+ " WHERE A.ORDER_CODE LIKE '"
				+ ordercode
				+ "%' AND A.UNIT_CODE = B.UNIT_CODE(+) AND A.MAN_CODE=C.MAN_CODE(+)";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * סԺ��ҩ���õ�HISϵͳҩƷ�ֵ�(ͬ����)
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getODIPhaOrderInfo(String code, String password) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.TRADE_ENG_DESC,A.SPECIFICATION,"
			    + "B.UNIT_CHN_DESC,D.DOSE_CHN_DESC,A.PY1,'' AS MATERIAL_CHN_DESC,E.MAN_CHN_DESC,C.ROUTE_CODE,C.MEDI_QTY,C.MEDI_UNIT,C.DOSAGE_UNIT"
				+ " FROM SYS_FEE A,SYS_UNIT B,PHA_BASE C,PHA_DOSE D,SYS_MANUFACTURER E"
				+ " WHERE A.CAT1_TYPE='PHA' AND A.ACTIVE_FLG='Y' AND A.UNIT_CODE=B.UNIT_CODE AND A.ORDER_CODE=C.ORDER_CODE(+) AND C.DOSE_CODE=D.DOSE_CODE(+)"
				+ " AND A.MAN_CODE=E.MAN_CODE(+)"
				+ "  ORDER BY A.ORDER_CODE";
		//System.out.println("------getODIPhaOrderInfo-------"+sql);
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * סԺ��ҩ���õ�HISϵͳҩƷ�ֵ䵥ҽ����ѯ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getODIPhaOrderInfoItem(String code, String password,
			String ordercode) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		if (ordercode == null || ordercode.length() == 0)
			return new String[] { "����ȱ�٣�ҩƷ���룡" };
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.TRADE_ENG_DESC,A.SPECIFICATION,B.UNIT_CHN_DESC,D.DOSE_CHN_DESC,A.PY1,'' AS MATERIAL_CHN_DESC,E.MAN_CHN_DESC "
				+ " FROM SYS_FEE A,SYS_UNIT B,PHA_BASE C,PHA_DOSE D,SYS_MANUFACTURER E"
				+ " WHERE A.ORDER_CODE='"
				+ ordercode
				+ "' AND A.CAT1_TYPE='PHA' AND A.ACTIVE_FLG='Y' AND A.UNIT_CODE=B.UNIT_CODE AND A.ORDER_CODE=C.ORDER_CODE(+) AND C.DOSE_CODE=D.DOSE_CODE(+)"
				+ " AND A.MAN_CODE=E.MAN_CODE(+)";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ҩƷ��λ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getIndMaterialloc(String code, String password) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT ORG_CODE,MATERIAL_LOC_CODE,MATERIAL_CHN_DESC,MATERIAL_ENG_DESC,PY1,DESCRIPTION FROM IND_MATERIALLOC ORDER BY ORG_CODE";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ҩƷ��λ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getIndMateriallocOrder(String code, String password,
			String orgcode, String ordercode) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		if (orgcode == null || orgcode.length() == 0)
			return new String[] { "����ȱ�٣�ҩ�����룡(ͬ�����ұ���;��ҩ������ͨ��HIS������ҩ����ҩ������õ�)" };
		if (ordercode == null || ordercode.length() == 0)
			return new String[] { "����ȱ�٣�ҩƷ���룡" };
		String sql = "SELECT A.MATERIAL_LOC_CODE,B.MATERIAL_CHN_DESC FROM IND_STOCKM A,IND_MATERIALLOC B WHERE A.ORG_CODE='"
				+ orgcode
				+ "' AND ORDER_CODE='"
				+ ordercode
				+ "' AND A.MATERIAL_LOC_CODE=B.MATERIAL_LOC_CODE(+)";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * �õ���Һϵͳҽ����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param ordercode
	 *            String
	 * @return String
	 */
	public String[] getPhaEsyOrder(String code, String password,
			String ordercode) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		if (ordercode == null || ordercode.length() == 0)
			return new String[] {};
		String sql = "SELECT B.ORDER_DESC,B.ALIAS_DESC,A.MEDI_QTY,C.UNIT_CHN_DESC,D.UNIT_CHN_DESC,B.SPECIFICATION,B.PY1,B.ALIAS_PYCODE,A.ORDER_CODE,TO_CHAR(A.OPT_DATE,'YYYYMMDDHH24MISS') FROM PHA_BASE A,SYS_FEE B,SYS_UNIT C,SYS_UNIT D WHERE A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE='"
				+ ordercode
				+ "' AND A.MEDI_UNIT=C.UNIT_CODE AND A.DOSAGE_UNIT=D.UNIT_CODE";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * �Ա��ֵ�ͬ��
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getSexInf(String code, String password) {
		String result = checkAuthority(code, password, "SYS_SEX", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT CHN_DESC,ID,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX' ORDER BY ID";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * Ѫ��
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getBloodType(String code, String password) {
		String result = checkAuthority(code, password, "SYS_BLOOD", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT CHN_DESC,ID,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_BLOOD' ORDER BY ID";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ��ݣ��������
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getCtzInf(String code, String password) {
		String result = checkAuthority(code, password, "SYS_CTZ", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT CTZ_DESC,CTZ_CODE,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_CTZ ORDER BY CTZ_CODE";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * �õ�������Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getDeptInfSY(String code, String password) {
		String result = checkAuthority(code, password, "DEPT_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"select DEPT_CODE,CASE WHEN DEPT_ANOTHER_CHN_DESC IS NULL THEN DEPT_CHN_DESC ELSE DEPT_CHN_DESC || '(' || DEPT_ANOTHER_CHN_DESC || ')' END DEPT_CHN_DESC,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') from SYS_DEPT Order By DEPT_CODE");
	}

	/**
	 * �õ�Ƶ��
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getPhaFreqInfSY(String code, String password) {
		String result = checkAuthority(code, password, "PHAFREQ_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT FREQ_CHN_DESC,'��' AS FREQ_UNIT,CASE WHEN CYCLE=0 THEN 7 ELSE CYCLE END CYCLE,FREQ_TIMES,FREQ_CODE,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS'),FREQ_UNIT_48 FROM SYS_PHAFREQ ORDER BY SEQ";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * �õ���ҩ��ʽ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getRouteInf(String code, String password) {
		String result = checkAuthority(code, password, "ROUTE_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT ROUTE_CHN_DESC,ROUTE_CODE,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_PHAROUTE ORDER BY SEQ";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ͬ�����
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param admtype
	 *            String
	 * @param startdate
	 *            String
	 * @param enddate
	 *            String
	 * @return String[]
	 */
	public String[] getRegWorkList(String code, String password,
			String admtype, String startdate, String enddate) {
		String result = checkAuthority(code, password, "WORK_LIST", "read");
		if (result.length() > 0)
			return new String[] { result };
		if (admtype == null || admtype.length() == 0 || startdate == null
				|| startdate.length() == 0 || enddate == null
				|| enddate.length() == 0)
			return new String[] {};
		String sql = "SELECT A.ADM_DATE,B.DEPT_CHN_DESC,D.CLINICROOM_DESC,C.USER_NAME,E.SESSION_DESC "
				+ " FROM REG_SCHDAY A,SYS_DEPT B,SYS_OPERATOR C,REG_CLINICROOM D,REG_SESSION E "
				+ " WHERE A.ADM_TYPE='"
				+ admtype.toUpperCase()
				+ "' "
				+ " AND  A.CLINICROOM_NO=D.CLINICROOM_NO "
				+ " AND A.DEPT_CODE=B.DEPT_CODE "
				+ " AND DR_CODE=C.USER_ID "
				+ " AND E.ADM_TYPE='"
				+ admtype.toUpperCase()
				+ "' "
				+ " AND A.SESSION_CODE=E.SESSION_CODE "
				+ " AND TO_DATE(ADM_DATE,'YYYYMMDDHH24MISS') BETWEEN TO_DATE('"
				+ startdate
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ enddate
				+ "','YYYYMMDDHH24MISS')";
		//System.out.println("sql" + sql);
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * �õ�ҩƷ��Ϣ(ƴ����)
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param py1
	 *            String
	 * @param startrow
	 *            int
	 * @param endrow
	 *            int
	 * @return String[]
	 */
	public String[] getPhaOrderPY1(String code, String password, String py1,
			int startrow, int endrow) {
		String result = checkAuthority(code, password, "PHA_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		if (py1 == null || py1.length() == 0)
			return new String[] {};
		if (startrow == 0)
			startrow = 0;
		if (endrow == 0)
			endrow = 100000;
		String sql = "SELECT ORDER_CODE,ORDER_DESC,TRADE_ENG_DESC,GOODS_DESC,ALIAS_DESC,"
				+ "       SPECIFICATION,OWN_PRICE,UNIT_CHN_DESC"
				+ "  FROM SYS_FEE, SYS_UNIT"
				+ " WHERE SYS_FEE.PY1 LIKE '%"
				+ py1.toUpperCase()
				+ "%' AND SYS_FEE.UNIT_CODE = SYS_UNIT.UNIT_CODE ORDER BY ORDER_CODE";

		return selectList(sql, startrow, endrow, "ORDER_CODE");
	}

	/**
	 * �ֶβ�ѯ
	 * 
	 * @param sql
	 *            String
	 * @param startrow
	 *            int
	 * @param endrow
	 *            int
	 * @return String[]
	 */
	private String[] selectList(String sql, int startrow, int endrow,
			String columnName) {
		TParm parm = new TParm(TJDODBTool.getInstance().select("", sql, true,
				startrow, endrow));
		if (parm.getErrCode() < 0)
			return new String[] { "ERR:" + parm.getErrCode() + " "
					+ parm.getErrText() };
		ArrayList list = new ArrayList();
		int count = parm.getCount(columnName);
		Vector columns = (Vector) parm.getData("SYSTEM", "COLUMNS");
		for (int i = 0; i < count; i++) {
			StringBuffer s = new StringBuffer();
			for (int j = 0; j < columns.size(); j++) {
				String name = (String) columns.get(j);
				if (j > 0)
					s.append(";");
				s.append("" + parm.getData(name, i));
			}
			list.add(s.toString());
		}
		return (String[]) list.toArray(new String[] {});
	}

	/**
	 * �ֶβ�ѯ
	 * 
	 * @param sql
	 *            String
	 * @param startrow
	 *            int
	 * @param endrow
	 *            int
	 * @return String[]
	 */
	private String[] selectList(TParm parm, String columnName) {
		ArrayList list = new ArrayList();
		int count = parm.getCount(columnName);
		Vector columns = (Vector) parm.getData("SYSTEM", "COLUMNS");
		for (int i = 0; i < count; i++) {
			StringBuffer s = new StringBuffer();
			for (int j = 0; j < columns.size(); j++) {
				String name = (String) columns.get(j);
				if (j > 0)
					s.append(";");
				s.append("" + parm.getData(name, i));
			}
			list.add(s.toString());
		}
		return (String[]) list.toArray(new String[] {});
	}

	/**
	 * �õ�Ƶ��
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getPhaFreqInf(String code, String password) {
		String result = checkAuthority(code, password, "PHAFREQ_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT FREQ_CODE,FREQ_CHN_DESC,FREQ_ENG_DESC FROM SYS_PHAFREQ ORDER BY SEQ";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * �����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param classify
	 *            String
	 * @param type
	 *            String
	 * @return String[]
	 */
	public String[] getDiagnosisInf(String code, String password,
			String classify, String type) {
		String result = checkAuthority(code, password, "DIAGNOSIS_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		// if(classify == null || classify.length() == 0)
		// return new String[]{};
		String sql = "SELECT ICD_CODE,ICD_CHN_DESC,ICD_ENG_DESC,ICD_TYPE"
				+ "  FROM SYS_DIAGNOSIS";
		// " WHERE ICD_CODE LIKE '" + classify + "%'";
		if (classify != null && classify.length() > 0) {
			sql += " WHERE ICD_CODE LIKE '" + classify + "%'";
		}
		if (type != null && type.length() > 0) {
			if (sql.contains("WHERE")) {
				sql += " AND ICD_TYPE='" + type + "'";
			} else {
				sql += " WHERE ICD_TYPE='" + type + "'";
			}
		}
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ��ȡ�����ֵ�
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getSysOperationICD(String code, String password) {
		String result = checkAuthority(code, password, "ICD_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT OPERATION_ICD,OPT_CHN_DESC,OPT_ENG_DESC FROM SYS_OPERATIONICD ORDER BY SEQ";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ע����Ϣ
	 * 
	 * @param code
	 *            String ���
	 * @param chnDesc
	 *            String ��������
	 * @param engDesc
	 *            String Ӣ������
	 * @param contactsName
	 *            String ��ϵ��
	 * @param tel
	 *            String �绰
	 * @param email
	 *            String mail
	 * @param password
	 *            String ����
	 * @return String Success ��������
	 *         http://zj:8080/axis2/services/DictionaryService
	 *         /regist?code=LIS&chnDesc
	 *         =����&engDesc=LIS&contactsName=test&tel=123&
	 *         email=aa@qq.com&password=123
	 */
	public String regist(String code, String chnDesc, String engDesc,
			String contactsName, String tel, String email, String password) {
		if (code == null || code.length() == 0)
			return "ERR:code is null";
		if (chnDesc == null || chnDesc.length() == 0)
			return "ERR:chnDesc is null";
		if (engDesc == null || engDesc.length() == 0)
			return "ERR:engDesc is null";
		if (contactsName == null || contactsName.length() == 0)
			return "ERR:contactsName is null";
		if (password == null || password.length() == 0)
			return "ERR:password is null";
		String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0)
			return "ERR:" + code + " Exist";
		sql = "INSERT INTO SYS_IO_INF VALUES('" + code + "','" + chnDesc
				+ "','" + engDesc + "'," + "'" + contactsName + "','" + tel
				+ "','" + email + "','" + password + "',"
				+ "'NEW','SYS_SERVICE',SYSDATE,'','','')";
		parm = new TParm(TJDODBTool.getInstance().update(sql));
		if (parm.getErrCode() != 0)
			return "ERR:" + parm.getErrText();
		return "Success";
	}

	/**
	 * ��ȡע��״̬
	 * 
	 * @param code
	 *            String
	 * @return String No Find û���ҵ� NEW ����,��ȷ�� ENABLED ��Ч�� DISABLED ��Ч��
	 */
	public String getRegistStatus(String code) {
		if (code == null || code.length() == 0)
			return "ERR:code is null";
		String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
		//System.out.println("======SQL======="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0)
			return "Err:No Find " + code;
		return parm.getValue("STATUS", 0);
	}

	/**
	 * �޸�����
	 * 
	 * @param code
	 *            String
	 * @param oldPassword
	 *            String
	 * @param newPassword
	 *            String
	 * @return String
	 */
	public String modifyPassword(String code, String oldPassword,
			String newPassword) {
		if (code == null || code.length() == 0)
			return "ERR:code is null";
		if (oldPassword == null || oldPassword.length() == 0)
			return "ERR:oldPassword is null";
		if (newPassword == null || newPassword.length() == 0)
			return "ERR:newPassword is null";
		String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0)
			return "Err:No Find " + code;
		if (!oldPassword.equals(parm.getValue("PASSWORD", 0)))
			return "Err:old password is not fitful";
		sql = "update SYS_IO_INF set PASSWORD='" + newPassword + "'";
		parm = new TParm(TJDODBTool.getInstance().update(sql));
		if (parm.getErrCode() != 0)
			return "ERR:" + parm.getErrText();
		return "Success";
	}

	/**
	 * �õ������ֵ���Ϣ
	 * 
	 * @return String[]
	 */
	public String[] getShareTable() {
		return TJDODBTool
				.getInstance()
				.selectList(
						"SELECT TABLE_NAME,TABLE_DESC,READ,WRITE,LISTEN FROM SYS_IO_SHARETABLE");
	}

	/**
	 * ���Գ�������
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param flg
	 *            boolean
	 * @return String
	 */
	private String checkCode(String code, String password, boolean flg) {
		if (code == null || code.length() == 0)
			return "ERR:code is null";
		if (password == null || password.length() == 0)
			return "ERR:password is null";
		String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0)
			return "Err:No Find " + code;
		if (!password.equals(parm.getValue("PASSWORD", 0)))
			return "Err:password err";
		if (flg)
			if (!"ENABLED".equals(parm.getValue("STATUS", 0)))
				return "Err:" + code + " is DISABLED";
		return "";
	}

	/**
	 * ���Ա���
	 * 
	 * @param tableName
	 *            String
	 * @param action
	 *            String
	 * @return String
	 */
	private String checkTable(String tableName, String action) {
		if (tableName == null || tableName.length() == 0)
			return "ERR:tableName is null";
		if (action == null || action.length() == 0)
			return "ERR:action is null (read or write or listen)";
		String sql = "SELECT READ,WRITE,LISTEN FROM SYS_IO_SHARETABLE WHERE TABLE_NAME='"
				+ tableName.toUpperCase() + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0)
			return "Err:No Find " + tableName;
		if (action.equalsIgnoreCase("read"))
			if (!"Y".equals(parm.getValue("READ", 0)))
				return "Err:not allowed read";
			else
				return "";
		if (action.equalsIgnoreCase("write"))
			if (!"Y".equals(parm.getValue("WRITE", 0)))
				return "Err:not allowed write";
			else
				return "";
		if (action.equalsIgnoreCase("listen"))
			if (!"Y".equals(parm.getValue("LISTEN", 0)))
				return "Err:not allowed listen";
			else
				return "";
		return "ERR:action is invalid (read or write or listen)";
	}

	/**
	 * ע�����Ϣ
	 * 
	 * @param code
	 *            String ���̱��
	 * @param password
	 *            String ����
	 * @param tableName
	 *            String ����
	 * @param action
	 *            String ����
	 * @return String ����
	 *         http://zj:8080/axis2/services/DictionaryService/registTable
	 *         ?code=LIS&password=aaa&tableName=PHA_INF&action=listen
	 */
	public String registTable(String code, String password, String tableName,
			String action) {
		String result = checkCode(code, password, false);
		if (result.length() > 0)
			return result;
		result = checkTable(tableName, action);
		if (result.length() > 0)
			return result;
		tableName = tableName.toUpperCase();
		String sql = "SELECT * FROM SYS_IO_TABLE WHERE IO_CODE='" + code
				+ "' AND TABLE_NAME='" + tableName + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			String s = "";
			if (action.equalsIgnoreCase("read"))
				s = "'1','N','N'";
			else if (action.equalsIgnoreCase("write"))
				s = "'N','1','N'";
			else if (action.equalsIgnoreCase("listen"))
				s = "'N','N','1'";
			sql = "INSERT INTO SYS_IO_TABLE VALUES('" + code + "','"
					+ tableName + "'," + s + ",'SYS_SERVICE',SYSDATE,'')";
		} else {
			String s = "";
			if (action.equalsIgnoreCase("read"))
				s = "READ='1'";
			else if (action.equalsIgnoreCase("write"))
				s = "WRITE='1'";
			else if (action.equalsIgnoreCase("listen"))
				s = "LISTEN='1'";
			sql = "update SYS_IO_TABLE set " + s;

		}
		parm = new TParm(TJDODBTool.getInstance().update(sql));
		if (parm.getErrCode() != 0)
			return "ERR:" + parm.getErrText();
		return "Success";
	}

	/**
	 * �õ���ע����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param tableName
	 *            String
	 * @return String[]
	 */
	public String[] getRegistTableInf(String code, String password,
			String tableName) {
		String result = checkCode(code, password, false);
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT TABLE_NAME,READ,WRITE,LISTEN FROM SYS_IO_TABLE WHERE IO_CODE='"
				+ code + "'";
		if (tableName != null && tableName.length() > 0)
			sql += " AND TABLE_NAME='" + tableName.toUpperCase() + "'";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ����Ȩ��
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param tableName
	 *            String
	 * @param action
	 *            String
	 * @return String
	 */
	private String checkAuthority(String code, String password,
			String tableName, String action) {
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return result;
		String sql = "SELECT TABLE_NAME,READ,WRITE,LISTEN FROM SYS_IO_TABLE WHERE IO_CODE='"
				+ code
				+ "'"
				+ " AND TABLE_NAME='"
				+ tableName.toUpperCase()
				+ "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0)
			return "Err:No Authority";
		if (action.equalsIgnoreCase("read"))
			if (!"Y".equals(parm.getValue("READ", 0)))
				return "Err:not allowed read";
			else
				return "";
		if (action.equalsIgnoreCase("write"))
			if (!"Y".equals(parm.getValue("WRITE", 0)))
				return "Err:not allowed write";
			else
				return "";
		if (action.equalsIgnoreCase("listen"))
			if (!"Y".equals(parm.getValue("LISTEN", 0)))
				return "Err:not allowed listen";
			else
				return "";
		return "ERR:action is invalid (read or write or listen)";
	}

	/**
	 * �õ��б仯�ı�
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param status
	 *            String
	 * @return String[]
	 */
	public String[] getModifyTable(String code, String password, String status) {
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool.getInstance().selectList(
				"select distinct TABLE_NAME from SYS_IO_LOG where IO_CODE='"
						+ code + "' AND STATUS='" + status + "'");
	}

	/**
	 * �õ��仯��Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param status
	 *            String
	 * @param tableName
	 *            String
	 * @return String[]
	 */
	public String[] getModifyInf(String code, String password, String status,
			String tableName) {
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return new String[] { result };
		String sql = "select TABLE_NAME,SEQ,ACTION,IO_DATA from SYS_IO_LOG where IO_CODE='"
				+ code + "' AND STATUS='" + status + "'";
		if (tableName != null && tableName.length() > 0)
			sql += " AND TABLE_NAME='" + tableName + "'";
		sql += " order by TABLE_NAME,SEQ";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * ȡ����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param tableName
	 *            String
	 * @param index
	 *            String
	 * @return String
	 */
	public String fetchInf(String code, String password, String tableName,
			String index) {
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return result;
		if (tableName == null || tableName.length() == 0)
			return "ERR:tableName is null";
		if (index == null || index.length() == 0)
			return "ERR:index is null";
		try {
			Integer.parseInt(index);
		} catch (Exception e) {
			return "ERR:index type err(int)";
		}
		String sql = "select STATUS from SYS_IO_LOG" + " where IO_CODE='"
				+ code + "' AND TABLE_NAME='" + tableName + "' AND SEQ="
				+ index;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0)
			return "ERR:no find";
		String status = parm.getValue("STATUS", 0);
		if (!"NEW".equals(status))
			return "ERR:status is " + status;
		sql = "update SYS_IO_LOG set STATUS='FETCH'" + " where IO_CODE='"
				+ code + "' AND TABLE_NAME='" + tableName + "' AND SEQ="
				+ index;
		parm = new TParm(TJDODBTool.getInstance().update(sql));
		if (parm.getErrCode() != 0)
			return "ERR:" + parm.getErrText();
		return "Success";
	}

	/**
	 * ȷ��
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param tableName
	 *            String
	 * @param index
	 *            String
	 * @return String
	 */
	public String confirmedInf(String code, String password, String tableName,
			String index) {
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return result;
		if (tableName == null || tableName.length() == 0)
			return "ERR:tableName is null";
		if (index == null || index.length() == 0)
			return "ERR:index is null";
		try {
			Integer.parseInt(index);
		} catch (Exception e) {
			return "ERR:index type err(int)";
		}
		String sql = "select STATUS from SYS_IO_LOG" + " where IO_CODE='"
				+ code + "' AND TABLE_NAME='" + tableName + "' AND SEQ="
				+ index;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0)
			return "ERR:no find";
		String status = parm.getValue("STATUS", 0);
		if ("CONFIRMED".equals(status))
			return "ERR:status is " + status;
		sql = "update SYS_IO_LOG set STATUS='CONFIRMED'" + " where IO_CODE='"
				+ code + "' AND TABLE_NAME='" + tableName + "' AND SEQ="
				+ index;
		parm = new TParm(TJDODBTool.getInstance().update(sql));
		if (parm.getErrCode() != 0)
			return "ERR:" + parm.getErrText();
		return "Success";
	}

	/**
	 * ɾ��ͬ����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param tableName
	 *            String
	 * @param index
	 *            String
	 * @return String
	 */
	public String deleteInf(String code, String password, String tableName,
			String index) {
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return result;
		String sql = "delete SYS_IO_LOG" + " where IO_CODE='" + code + "'";
		if (tableName != null && tableName.length() > 0)
			sql += " AND TABLE_NAME='" + tableName + "'";
		if (index != null && index.length() > 0) {
			try {
				Integer.parseInt(index);
			} catch (Exception e) {
				return "ERR:index type err(int)";
			}
			sql += " AND SEQ=" + index;
		}
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		if (parm.getErrCode() != 0)
			return "ERR:" + parm.getErrText();
		return "Success";
	}

	/**
	 * �û���Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getOperatorInfSY(String code, String password) {
		String result = checkAuthority(code, password, "OPERATOR_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		TParm action = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT A.USER_ID,A.USER_NAME,A.USER_PASSWORD,B.DEPT_CODE,A.USER_ID AS ID,TO_CHAR(A.OPT_DATE,'YYYYMMDDHH24MISS'),C.POS_CHN_DESC FROM SYS_OPERATOR A,SYS_OPERATOR_DEPT B,SYS_POSITION C WHERE A.USER_ID=B.USER_ID AND B.MAIN_FLG='Y' AND A.POS_CODE=C.POS_CODE Order By A.SEQ"));
		int rowConut = action.getCount();
		TJDOTool decryptTool = new TJDOTool();
		for (int i = 0; i < rowConut; i++) {
			String pass = action.getValue("USER_PASSWORD", i);
			action.setData("USER_PASSWORD", i, decryptTool.decrypt(pass));
		}
		return this.selectList(action, "USER_ID");
	}

	/**
	 * Ƥ�Ի�д�ż�ҽ����ע
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @param seqNo
	 *            String
	 * @return String[]
	 */
	public String[] readOpdOrderPS(String code, String password, String caseno,
			String rxno, String seqno, String value) {
		if (caseno == null || caseno.length() == 0)
			return new String[] { "ERR:caseno is null" };
		if (rxno == null || rxno.length() == 0)
			return new String[] { "ERR:rxno is null" };
		;
		if (seqno == null || seqno.length() == 0)
			return new String[] { "ERR:seqno is null" };
		String result = checkAuthority(code, password, "OPD_ORDERINF", "write");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "UPDATE OPD_ORDER SET NS_NOTE='" + value
				+ "' WHERE CASE_NO='" + caseno + "' AND RX_NO='" + rxno
				+ "' AND SEQ_NO='" + seqno + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		if (parm.getErrCode() != 0)
			return new String[] { "N" };
		return new String[] { "Y" };
	}

	/**
	 * �õ��ż�ס��
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getAdmType(String code, String password) {
		String result = checkAuthority(code, password, "SYS_ADMTYPE", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT CHN_DESC,ID,TO_CHAR(OPT_DATE,'YYYYMMDDHH24MISS') FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ADMTYPE' ORDER BY ID";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * �õ�ҽʦ�Ű�
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param startDate
	 *            String ��ʼ���� 20100921
     * @param endDate
     *            String �������� 20101021
	 * @return String[] ʱ��,�ż�ס��,����,����,ҽʦ���ѹ�����
	 */
	public String[] getRegSchDay(String code, String password, String startDate, String endDate) {
		String result = checkAuthority(code, password, "REG_SCHDAY", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "";
         sql =
                "SELECT A.ADM_DATE, A.SESSION_CODE, A.ADM_TYPE, A.DEPT_CODE, A.CLINICROOM_NO, A.DR_CODE, '' AS QUE_NO,A.STOP_SESSION,A.VIP_FLG," +
                " '' AS W_QUE_NO, '' AS M_MAX_QUE, A.MAX_QUE, (A.QUE_NO - 1) AS QUE,COUNT(B.CASE_NO)  COUNT"
                        + "  FROM REG_SCHDAY A, REG_PATADM B    "
                        + " WHERE A.CLINICROOM_NO = B.CLINICROOM_NO(+) "
                        + "   AND A.SESSION_CODE = B.SESSION_CODE(+) "
                        + "   AND A.DEPT_CODE = B.DEPT_CODE(+) "
                        + "   AND A.ADM_TYPE = B.ADM_TYPE(+) "
                        + "   AND B.REGCAN_USER(+) IS NULL "
                        + "   AND A.DR_CODE = B.DR_CODE(+) "
                        + "   AND B.ADM_DATE(+) = TO_DATE( A.ADM_DATE, 'YYYYMMDD') # "
                        + "GROUP BY A.ADM_DATE, A.SESSION_CODE, A.ADM_TYPE, A.DEPT_CODE, A.CLINICROOM_NO, A.DR_CODE,A.STOP_SESSION,A.VIP_FLG,A.MAX_QUE,A.QUE_NO "
                        + "ORDER BY A.ADM_DATE, A.SESSION_CODE, A.ADM_TYPE, A.DEPT_CODE, A.CLINICROOM_NO, A.DR_CODE";
        if (StringUtil.isNullString(startDate)) {
            return new String[]{};
        } else if (StringUtil.isNullString(endDate)) {
        	sql = sql.replaceFirst("#", " AND A.ADM_DATE = '" + startDate + "' ");
        } else {
        	sql =
        		sql.replaceFirst("#", " AND A.ADM_DATE BETWEEN '" + startDate + "' AND '"
                            + endDate + "' ");
        }
		
//		sql = "SELECT A.ADM_DATE, A.SESSION_CODE, A.ADM_TYPE, A.DEPT_CODE, A.CLINICROOM_NO, A.DR_CODE," +
//				" '' AS QUE_NO, A.STOP_SESSION, A.VIP_FLG, '' AS W_QUE_NO, '' AS M_MAX_QUE, A.MAX_QUE," +
//				" (A.QUE_NO - 1) AS QUE, (SELECT COUNT (CASE_NO) FROM REG_PATADM WHERE DR_CODE = A.DR_CODE" +
//				" AND SESSION_CODE = A.SESSION_CODE AND CLINICROOM_NO = A.CLINICROOM_NO" +
//				" AND ADM_DATE = TO_DATE (A.ADM_DATE, 'YYYY-MM-DD') AND REGCAN_USER IS NULL) AS COUNT" +
//				" FROM REG_SCHDAY A" +
//				" WHERE 1 = 1 #" +
//				" ORDER BY A.ADM_DATE, A.SESSION_CODE, A.ADM_TYPE, A.DEPT_CODE, A.CLINICROOM_NO, A.DR_CODE";
//		if (StringUtil.isNullString(startDate)) {
//			return new String[] {};
//		} else if (StringUtil.isNullString(endDate)) {
//			sql = sql.replaceFirst("#", " AND A.ADM_DATE = '" + startDate
//					+ "' ");
//		} else {
//			sql = sql.replaceFirst("#", " AND A.ADM_DATE BETWEEN '"
//					+ startDate + "' AND '" + endDate + "' ");
//		}
        return TJDODBTool.getInstance().selectList(sql);
	}
	

	/**
	 * �õ�������
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String
	 */
	public String[] getRegionJD(String code, String password) {
		String result = checkAuthority(code, password, "PHL_HISREGION", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT REGION_CODE,REGION_DESC FROM PHL_REGION ORDER BY REGION_CODE";
		return TJDODBTool.getInstance().selectList(sql);
	}

	/**
	 * �õ��ż�ҽ���Ƿ��˷�
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param caseno
	 *            String
	 * @param rxno
	 *            String
	 * @param seqno
	 *            String
	 * @return String[]
	 */
	public String[] getHisCancelOrder(String code, String password,
			String caseno, String rxno, String seqno) {
		String result = checkAuthority(code, password, "OPD_ORDERINF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String sql = "SELECT BILL_FLG FROM OPD_ORDER CASE_NO='" + caseno
				+ "' AND RX_NO='" + rxno + "' AND SEQ_NO='" + seqno + "'";
		TParm action = new TParm(TJDODBTool.getInstance().select(sql));
		if (action.getCount() <= 0) {
			return new String[] { "Y" };
		} else {
			if (action.getBoolean("BILL_FLG", 0)) {
				return new String[] { "N" };
			} else {
				return new String[] { "Y" };
			}
		}
	}

	/**
	 * �õ�����������Ϣ�ż�ҽ����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param rxno
	 *            String
	 * @return String[]
	 */
	public String[] getPatInfAndOrder(String code, String password, String rxno) {
		// 100113000062
		String result = checkAuthority(code, password, "OPERATOR_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		// ҽ����Ϣ
		TParm orderParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM OPD_ORDER WHERE RX_NO='" + rxno + "'"));
		if (orderParm.getCount() <= 0) {
			return new String[] { "N" };
		}
		String mrNo = orderParm.getValue("MR_NO", 0);
		String caseNo = orderParm.getValue("CASE_NO", 0);
		String admType = orderParm.getValue("ADM_TYPE", 0);
		String deptCode = orderParm.getValue("DEPT_CODE", 0);
		String drugStr = "";
		// ����������Ϣ
		TParm patInfParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT C.PAT_NAME,C.MR_NO,C.SEX_CODE,C.CTZ1_CODE,TO_CHAR(C.BIRTH_DATE,'YYYYMMDDHH24MISS'),C.IDNO,C.TEL_HOME,C.ADDRESS,C.BLOOD_TYPE,C.MR_NO AS VCBINGAN,TO_CHAR(A.ADM_DATE,'YYYYMMDDHH24MISS') AS ADM_DATE,A.CASE_NO,TO_CHAR(A.REG_DATE,'YYYYMMDDHH24MISS') AS REG_DATE,0 AS COUNT,B.PHL_REGION_CODE FROM REG_PATADM A,REG_CLINICROOM B,SYS_PATINFO C WHERE A.CLINICROOM_NO=B.CLINICROOM_NO AND A.MR_NO=C.MR_NO AND  CASE_NO='"
										+ caseNo + "'"));
		if (patInfParm.getCount() <= 0) {
			return new String[] { "N" };
		}
		// ����ʷ
		TParm drugParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT A.DRUG_TYPE,B.CHN_DESC,A.DRUGORINGRD_CODE FROM OPD_DRUGALLERGY A,SYS_DICTIONARY B WHERE A.DRUG_TYPE=B.ID AND MR_NO='"
										+ mrNo
										+ "' AND B.GROUP_ID='SYS_ALLERGY'"));
		if (drugParm.getCount() <= 0) {
			drugStr = "";
		} else {
			int rowCount = drugParm.getCount("DRUG_TYPE");
			for (int i = 0; i < rowCount; i++) {
				TParm temp = drugParm.getRow(i);
				if ("A".equals(temp.getValue("DRUG_TYPE"))) {
					drugStr += temp.getValue("CHN_DESC")
							+ ":"
							+ getDictionary("PHA_INGREDIENT", temp
									.getValue("DRUGORINGRD_CODE"));
				}
				if ("C".equals(temp.getValue("DRUG_TYPE"))) {
					drugStr += temp.getValue("CHN_DESC")
							+ ":"
							+ getDictionary("SYS_ALLERGYTYPE", temp
									.getValue("DRUGORINGRD_CODE"));
				}
			}
		}
		TParm tem = patInfParm.getRow(0);
		String[] patRes = new String[] { tem.getValue("PAT_NAME") + ";"
				+ tem.getValue("MR_NO") + ";" + tem.getValue("SEX_CODE") + ";"
				+ tem.getValue("CTZ1_CODE") + ";" + tem.getValue("BIRTH_DATE")
				+ ";" + tem.getValue("IDNO") + ";" + tem.getValue("TEL_HOME")
				+ ";" + tem.getValue("ADDRESS") + ";" + drugStr + ";" + mrNo
				+ ";" + tem.getValue("BLOOD_TYPE") + ";" + deptCode + ";"
				+ tem.getValue("ADM_DATE") + ";" + caseNo + ";"
				+ tem.getValue("REG_DATE") + ";" + tem.getValue("COUNT") + ";"
				+ tem.getValue("PHL_REGION_CODE") + ";" + admType };
		// String[] orderOE =
		// TJDODBTool.getInstance().selectList("SELECT 'ORDER_INF' AS ORDER_INF,A.CASE_NO,A.RX_NO,A.LINK_NO,A.SEQ_NO,A.ORDER_CODE,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.MEDI_QTY,A.ROUTE_CODE,A.FREQ_CODE,NVL(B.PS_FLG,'N') AS PS_FLG,A.DR_CODE,TO_CHAR(A.ORDER_DATE,'YYYYMMDDHH24MISS') AS ORDER_DATE,A.TAKE_DAYS,CASE_NO||RX_NO||SEQ_NO AS ROWIDNUM,TO_CHAR(A.OPT_DATE,'YYYYMMDDHH24MISS') AS OPT_DATE,0 AS NINTIMES FROM OPD_ORDER A,SYS_PHAROUTE B WHERE A.ROUTE_CODE=B.ROUTE_CODE AND A.RX_NO='"+rxno+"' AND A.DOSE_TYPE IN ('I','F')");
		// patRes = copyArray(patRes,orderOE);
		return patRes;
	}

	/**
	 * �õ���Һҽ����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param rxno
	 *            String
	 * @return String[]
	 */
	public String[] getOrderSY(String code, String password, String rxno) {
		String result = checkAuthority(code, password, "OPD_ORDERINF", "read");
		if (result.length() > 0)
			return new String[] { result };
		String[] orderOE = TJDODBTool
				.getInstance()
				.selectList(
						"SELECT A.CASE_NO,A.RX_NO,A.LINK_NO,A.SEQ_NO,A.ORDER_CODE,A.DISPENSE_QTY,C.UNIT_CHN_DESC,A.MEDI_QTY,A.ROUTE_CODE,A.FREQ_CODE,NVL(B.PS_FLG,'N') AS PS_FLG,A.DR_CODE,TO_CHAR(A.ORDER_DATE,'YYYYMMDDHH24MISS') AS ORDER_DATE,A.TAKE_DAYS,CASE_NO||RX_NO||SEQ_NO AS ROWIDNUM,TO_CHAR(A.OPT_DATE,'YYYYMMDDHH24MISS') AS OPT_DATE,0 AS NINTIMES FROM OPD_ORDER A,SYS_PHAROUTE B,SYS_UNIT C WHERE A.ROUTE_CODE=B.ROUTE_CODE AND A.RX_NO='"
								+ rxno
								+ "' AND A.DOSE_TYPE IN ('I','F') AND A.DISPENSE_UNIT=C.UNIT_CODE ");
		return orderOE;
	}

	/**
	 * ���鿽������
	 * 
	 * @param sql1
	 *            String[]
	 * @param sql2
	 *            String[]
	 * @return String[]
	 */
	// public static String[] copyArray(String sql1[], String sql2[]) {
	// if (sql1.length == 0)
	// return sql2;
	// if (sql2.length == 0)
	// return sql1;
	// String data[] = new String[sql1.length + sql2.length];
	// System.arraycopy(sql1, 0, data, 0, sql1.length);
	// System.arraycopy(sql2, 0, data, sql1.length, sql2.length);
	// return data;
	// }
	/**
	 * �õ��ֵ���Ϣ
	 * 
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getDictionary(String groupId, String id) {
		String result = "";
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "' AND ID='" + id + "'"));
		result = parm.getValue("CHN_DESC", 0);
		return result;
	}

	/**
	 * ���Կ���
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] testServiceDept(String code, String password) {
		if (!"LIS".equals(code)) {
			return new String[] { "ERR:" + code + " System no Find!" };
		}
		if (!"123".equals(password)) {
			return new String[] { "ERR: Password is err!" };
		}
		String[] str = new String[] { "10101;�����ڿ�;HXNK;�����ڿ�",
				"10102;�����ڿ�;;�����ڿ�" };
		return str;
	}

	/**
	 * ������Ա
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] testServiceUser(String code, String password) {
		if (!"LIS".equals(code)) {
			return new String[] { "ERR:" + code + " System no Find!" };
		}
		if (!"123".equals(password)) {
			return new String[] { "ERR: Password is err!" };
		}
		String[] str = new String[] { "hwb;С��;1;haiwenbobo@163.com",
				"ehui;���;1;1@11.com" };
		return str;
	}

	/**
	 * ���没����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param name
	 *            String ����
	 * @param birthday
	 *            String ��������
	 * @param sex
	 *            String �Ա�
	 * @param SID
	 *            String ���֤��
	 * @param tel
	 *            String �绰
	 * @param address
	 *            String ��ַ
	 * @return String
	 */
	public String savePat(String code, String password, String name,
			String birthday, String sex, String SID, String tel, String address) {
		String result = checkAuthority(code, password, "PAT_INF", "WRITE");
		if (result.length() > 0)
			return result;
		if (name == null || name.length() == 0)
			return "Err:��������Ϊ��";
		if (birthday == null || birthday.length() == 0)
			return "Err:�������ڲ���Ϊ��";
		if (sex == null || sex.length() == 0)
			return "Err:�Ա𲻿�Ϊ��";
		if (SID == null || SID.length() == 0)
			return "Err:���֤�Ų���Ϊ��";
		TParm parmPatInf = new TParm(TJDODBTool.getInstance().select(
				" SELECT MR_NO " + " FROM SYS_PATINFO " + " WHERE IDNO='" + SID
						+ "'"));
		if (parmPatInf.getErrCode() < 0)
			return "Err:" + parmPatInf;
		if (parmPatInf.getCount() > 0)
			return "Err:�Ѵ��ڴ˲�����Ϣ�޷���������";
		TParm defCtzParm = getDefCtz();
		if (defCtzParm.getErrCode() < 0)
			return "Err:" + defCtzParm;
		if (defCtzParm.getCount() <= 0)
			return "Err:δȡ�ò���Ĭ�����";
		TJDOTool tJDOTool = new TJDOTool();
		tJDOTool.setModuleName("sys\\SYSSystemModule.x");
		String mrNo = tJDOTool.getResultString(tJDOTool.call("getMrNo"),
				"MR_NO");
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.update(
								" INSERT INTO SYS_PATINFO ("
										+ "       PAT_NAME,PY1,FOREIGNER_FLG,BIRTH_DATE,SEX_CODE,"
										+ "       IDNO,TEL_HOME,ADDRESS,CTZ1_CODE,MR_NO,"
										+ "       OPT_USER,OPT_DATE,OPT_TERM)"
										+ " VALUES('" + name + "','"
										+ TMessage.getPy(name)
										+ "','N',TO_DATE('" + birthday
										+ "','YYYYMMDDHH24MISS'),'" + sex
										+ "'," + "       '" + SID + "','" + tel
										+ "','" + address + "','"
										+ defCtzParm.getValue("CTZ_CODE", 0)
										+ "','" + mrNo + "',"
										+ "       'web',SYSDATE,'web')"));
		if (parm.getErrCode() < 0)
			return "Err:" + parm;
		return mrNo;
	}

	/**
	 * ȡ�ò���Ĭ�����
	 * 
	 * @return TParm
	 */
	private TParm getDefCtz() {
		/**" SELECT CTZ_CODE " + " FROM SYS_CTZ "
		+ " WHERE DEF_CTZ_FLG = 'Y'")*/
		TParm defCtzParm = new TParm(TJDODBTool.getInstance().select(
				" SELECT CTZ_CODE " + " FROM SYS_CTZ "
				+ " WHERE DEF_CTZ_FLG = 'Y'"));
		return defCtzParm;
	}
	
	/**
	 * ȡ�ò���Ĭ�����
	 * 
	 * @return TParm
	 */
	private TParm getDefCtz(String mrNo) {
		/**" SELECT CTZ_CODE " + " FROM SYS_CTZ "
		+ " WHERE DEF_CTZ_FLG = 'Y'")*/
		String sql="SELECT CTZ1_CODE AS CTZ_CODE FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
		//System.out.println("-----getDefCtz sql-------"+sql);
		TParm defCtzParm = new TParm(TJDODBTool.getInstance().select(
				sql));
		return defCtzParm;
	}

	/**
	 * �������֤�ŵõ����߱��
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param SID
	 *            String ���֤��
	 * @param name
	 *            String ����
	 * @return String
	 */
	public String[] getPatForSID(String code, String password, String SID,
			String name) {
		String result = checkAuthority(code, password, "PAT_INF", "READ");
		if (result.length() > 0)
			return new String[] { result };
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT MR_NO " + " FROM SYS_PATINFO " + " WHERE IDNO='" + SID
						+ "' " + " AND PAT_NAME LIKE '%" + name + "%'"));
		if (parm.getErrCode() < 0)
			return new String[] { "Err:" + parm };
		if (parm.getCount() <= 0)
			return new String[] { "" };
		String[] returnString = new String[parm.getCount()];
		for (int i = 0; i < parm.getCount(); i++) {
			returnString[i] = parm.getValue("MR_NO", i);
		}
		return returnString;
	}

	/**
	 * ԤԼ�Һ�
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param mrNo
	 *            String ������
     * @param patName
     *            String ����
     * @param cellPhone
     *            String �ֻ���
	 * @param date
	 *            String �Һ�����
	 * @param sessionCode
	 *            String ʱ��
	 * @param admType
	 *            String �ż���
	 * @param deptCode
	 *            String ����
	 * @param clinicRoomNo
	 *            String ���
	 * @param drCode
	 *            String ҽ������
	 * @param regionCode
	 *            String ����
	 * @param ctz1Code
	 *            String �������
	 * @param serviceLevel
	 *            String ����ȼ�
	 * @return String
	 */
    public String regAppt(String code, String password, String mrNo, String patName,
                          String cellPhone, String date, String sessionCode, String admType,
                          String deptCode, String clinicRoomNo, String drCode, String regionCode,
                          String ctz1Code, String serviceLevel) {
		String result = checkAuthority(code, password, "REG_INF", "WRITE");
		if (result.length() > 0)
			return result;
		if (mrNo == null || mrNo.length() == 0)
			return "Err:�����Ų���Ϊ��";		
		mrNo = PatTool.getInstance().checkMrno(mrNo);  //add by huangtt 20141017
        TParm patInfo = PatTool.getInstance().getInfoForMrno(mrNo);
        if (patInfo.getCount() < 1) {//wanglong add 20141117
            return "Err:�����Ų�����";
        }
        if (patName == null || patName.length() == 0) {
            return "Err:��������Ϊ��";
        }
        try {
            patName = URLDecoder.decode(patName, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!patInfo.getValue("PAT_NAME", 0).equals(patName)) {
            return "Err:�ṩ�������벡���Ŷ�Ӧ����������";
        }
        if (cellPhone == null)
            return "Err:�ֻ��Ų���Ϊ��";
        if (!cellPhone.equals("")&&!cellPhone.matches("1\\d{10}")) {
            return "Err:�ֻ��Ÿ�ʽ����ȷ";
        }
		if (date == null || date.length() == 0)
			return "Err:ԤԼ�Һ����ڲ���Ϊ��";
		if (sessionCode == null || sessionCode.length() == 0)
			return "Err:�Һ�ʱ�β���Ϊ��";
		if (admType == null || admType.length() == 0)
			return "Err:�ż��𲻿�Ϊ��";
		if (deptCode == null || deptCode.length() == 0)
			return "Err:�Ʊ𲻿�Ϊ��";
		if (clinicRoomNo == null || clinicRoomNo.length() == 0)
			return "Err:��䲻��Ϊ��";
		if (drCode == null || drCode.length() == 0)
			return "Err:ҽ������Ϊ��";
		if (regionCode == null || regionCode.length() == 0)
			return "Err:���򲻿�Ϊ��";
		// ȡ�������Ϣ
		TParm parmClinicRoom = getClinicRoomInf(clinicRoomNo);
		if (parmClinicRoom.getErrCode() < 0)
			return "Err:" + parmClinicRoom;
		if (parmClinicRoom.getCount() <= 0)
			return "Err:δȡ�������Ϣ";
		// ȡ�ð����Ϣ
		TParm parmSchDay = getREGSchdayInfo(clinicRoomNo, regionCode, admType,
				date, sessionCode);
		if (parmSchDay.getErrCode() < 0)
			return "Err:" + parmSchDay;
		if (parmSchDay.getCount() <= 0)
			return "Err:δȡ�ð����Ϣ";
		// ȡ�����ݿ�����
		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		//System.out.println("-----conn1111-----"+conn);
		if (conn == null)
			return "Err:δȡ�����ݿ�����";
		
		// ȡ��������
		String queNo = getQueNo(parmSchDay, clinicRoomNo, admType, date,
				sessionCode, regionCode, conn);
		//System.out.println("-----queNo----"+queNo);
		
		if (queNo.length() == 0) {
			conn.rollback();
			conn.close();
			return "Err:���������";
		}
		// ȷ���������
		if (ctz1Code == null || ctz1Code.length() == 0 ||ctz1Code.equals("null")) {
			TParm defCtzParm = getDefCtz(mrNo) ;
			if (defCtzParm.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return "Err:" + defCtzParm;
			}
			if (defCtzParm.getCount() <= 0) {
				conn.rollback();
				conn.close();
				return "Err:δȡ�ò���Ĭ�����";
			}
			ctz1Code = defCtzParm.getValue("CTZ_CODE", 0);
			
		}
		//System.out.println("-----CTZ_CODE----"+ctz1Code);
		
		// ȷ������ȼ���Ϣ
		if (serviceLevel == null || serviceLevel.length() == 0 || serviceLevel.equals("null"))
			serviceLevel = "1";
		// ȡ���������˳���
		String caseNo =SystemTool.getInstance().getNo("ALL", "REG",
                "CASE_NO", "CASE_NO");
			//getNo("ALL", "REG", "CASE_NO", "CASE_NO");
		
		//System.out.println("-----caseNo----"+caseNo);
		//
		if (caseNo.length() == 0)
			return "Err:δȡ������˳���";
		TParm parm = insertRegPatadm(mrNo, date, sessionCode, admType,
				deptCode, clinicRoomNo, drCode, regionCode, ctz1Code,
				serviceLevel, queNo, caseNo, parmClinicRoom, parmSchDay, conn);
		if (parm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return "Err:" + parm;
		}
		conn.commit();
		conn.close();
        // ===============wanglong add 20141201 ���ӷ����Ź���
        if (cellPhone.equals("")) {
            return caseNo;
        }
        TParm smsParm = new TParm();
        smsParm.addData("MrNo", mrNo);
        smsParm.addData("Name", patName);
        String sessionSql =
                "SELECT SESSION_DESC FROM REG_SESSION WHERE SESSION_CODE = '" + sessionCode
                        + "' AND ADM_TYPE='" + admType + "'";
        TParm sc = new TParm(TJDODBTool.getInstance().select(sessionSql));
        String content =
                "����ԤԼ�ɹ�"
                        + StringTool.getString(StringTool.getTimestamp(date, "yyyyMMdd"),
                                               "yyyy/MM/dd")
                        + " "
                        + sc.getValue("SESSION_DESC", 0)
                        + "��"
                        + queNo
                        + "��"
                        + StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='" + drCode
                                + "'") + "ҽ��������";
        content += "������" + patName + "���ˡ�����ȡ��������ǰһ�첦�����绰4001568568��Ϊ�˱�֤��׼ʱ���������ǰ����ԤԼ��������";
        smsParm.addData("Content", content);
        smsParm.addData("TEL1", cellPhone);
//        TIOM_AppServer.executeAction("action.reg.REGAction", "orderMessage", smsParm);//������
        REGAction reg = new REGAction();
        reg.orderMessage(smsParm);// ������
        String updateTelSQL =
                "UPDATE SYS_PATINFO SET TEL_HOME='#',OPT_USER='&',OPT_DATE=SYSDATE,OPT_TERM='&' WHERE MR_NO='@'";//wanglong 20141216 ��CELL_PHONE��ΪTEL_HOME
        updateTelSQL = updateTelSQL.replaceFirst("#", cellPhone);
        updateTelSQL = updateTelSQL.replaceAll("&", "web");
        updateTelSQL = updateTelSQL.replaceFirst("@", mrNo);
        TJDODBTool.getInstance().update(updateTelSQL);//����sys_patinfo���cell_phone
//        Pat pat = Pat.onQueryByMrNo(mrNo);
//        pat.modifyCellPhone(cellPhone);//����sys_patinfo���cell_phone
//        pat.onSave();
        // ===============add end
		return caseNo;
	}

	/**
	 * ȡ�þ������
	 * 
	 * @param parmSchDay
	 *            TParm
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param regionCode
	 *            String
	 * @param conn
	 *            TConnection
	 * @return String
	 */
	private String getQueNo(TParm parmSchDay, String clinicRoomNo,
			String admType, String date, String sessionCode, String regionCode,
			TConnection conn) {
		if (parmSchDay.getValue("VIP_FLG", 0).equals("Y")) {
			TParm parmQueNo = getVIPQueNo(clinicRoomNo, admType, date,
					sessionCode);
//			System.out.println("parmQueNo==="+parmQueNo);
			if (parmQueNo.getErrCode() < 0)
				return "";
			if (parmQueNo.getCount() <= 0)
				return "";
			if (parmQueNo.getValue("QUE_NO", 0) == null
					|| parmQueNo.getInt("QUE_NO", 0) == 0
					|| parmQueNo.getValue("QUE_NO", 0).equalsIgnoreCase("null"))
				return "";
			TParm parm = updateVIPQueNo(clinicRoomNo, admType, date,
					sessionCode, parmQueNo.getValue("QUE_NO", 0), "Y", conn);
			if (parm.getErrCode() < 0)
				return "";
			//VIPҲ���¾������
			TParm parm1 = updateQueNo(clinicRoomNo, admType, date, sessionCode,
					regionCode, conn);
			if (parm1.getErrCode() < 0)
				return "";
			//
			return parmQueNo.getValue("QUE_NO", 0);
			//
		} else {
			
			if(parmSchDay.getInt("QUE_NO", 0) > parmSchDay.getInt("MAX_QUE", 0)){
				return "";
			}
			
			TParm parm = updateQueNo(clinicRoomNo, admType, date, sessionCode,
					regionCode, conn);
			if (parm.getErrCode() < 0)
				return "";
			return parmSchDay.getValue("QUE_NO", 0);
		}
	}

	/**
	 * ȡ�������Ϣ
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @return TParm
	 */
	private TParm getClinicRoomInf(String clinicRoomNo) {
		TParm parmClinicRoom = new TParm(TJDODBTool.getInstance().select(
				" SELECT CLINICAREA_CODE " + " FROM REG_CLINICROOM "
						+ " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"));
		return parmClinicRoom;
	}

	/**
	 * ȡ�ð����Ϣ
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param regionCode
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @return TParm
	 */
	private TParm getREGSchdayInfo(String clinicRoomNo, String regionCode,
			String admType, String date, String sessionCode) {
		TParm parmSchDay = new TParm(TJDODBTool.getInstance().select(
				" SELECT VIP_FLG,CLINICTYPE_CODE,QUE_NO,MAX_QUE " + " FROM REG_SCHDAY "
						+ " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"
						+ " AND   REGION_CODE = '" + regionCode + "'"
						+ " AND   ADM_TYPE = '" + admType + "'"
						+ " AND   ADM_DATE = '" + date + "'"
						+ " AND   SESSION_CODE = '" + sessionCode + "'"
//						+ " AND   QUE_NO != MAX_QUE"
						));
		return parmSchDay;
	}

	/**
	 * ȡ��VIP�������
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @return TParm
	 */
	private TParm getVIPQueNo(String clinicRoomNo, String admType, String date,
			String sessionCode) {
		TParm parmQueNo = new TParm(TJDODBTool.getInstance().select(
				" SELECT MIN(QUE_NO) QUE_NO " + " FROM REG_CLINICQUE "
						+ " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"
						+ " AND   ADM_TYPE = '" + admType + "'"
						+ " AND   ADM_DATE = '" + date + "'"
						+ " AND   SESSION_CODE = '" + sessionCode + "'"
						+ " AND   QUE_STATUS = 'N'"));
		return parmQueNo;
	}

	/**
	 * ����VIP����˳���
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param queNo
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateVIPQueNo(String clinicRoomNo, String admType,
			String date, String sessionCode, String queNo, String queStatus,
			TConnection conn) {
		TParm parm = new TParm(TJDODBTool.getInstance().update(
				" UPDATE REG_CLINICQUE" + " SET QUE_STATUS = '" + queStatus
						+ "'" + " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"
						+ " AND   ADM_TYPE = '" + admType + "'"
						+ " AND   ADM_DATE = '" + date + "'"
						+ " AND   SESSION_CODE = '" + sessionCode + "'"
						+ " AND   QUE_NO = '" + queNo + "'", conn));
		return parm;
	}

	/**
	 * ���¾������
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param regionCode
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateQueNo(String clinicRoomNo, String admType, String date,
			String sessionCode, String regionCode, TConnection conn) {
		TParm parm = new TParm(TJDODBTool.getInstance()
				.update(
						" UPDATE REG_SCHDAY" + " SET QUE_NO = QUE_NO + 1"
								+ " WHERE CLINICROOM_NO = '" + clinicRoomNo
								+ "'" + " AND   REGION_CODE = '" + regionCode
								+ "'" + " AND   ADM_TYPE = '" + admType + "'"
								+ " AND   ADM_DATE = '" + date + "'"
								+ " AND   SESSION_CODE = '" + sessionCode + "'"
								+ " AND   CLINICROOM_NO = '" + clinicRoomNo
								+ "'", conn));
		return parm;
	}

	/**
	 * д�Һ�����
	 * 
	 * @param mrNo
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param admType
	 *            String
	 * @param deptCode
	 *            String
	 * @param clinicRoomNo
	 *            String
	 * @param drCode
	 *            String
	 * @param regionCode
	 *            String
	 * @param ctz1Code
	 *            String
	 * @param serviceLevel
	 *            String
	 * @param queNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param parmClinicRoom
	 *            TParm
	 * @param parmSchDay
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm insertRegPatadm(String mrNo, String date, String sessionCode,
			String admType, String deptCode, String clinicRoomNo,
			String drCode, String regionCode, String ctz1Code,
			String serviceLevel, String queNo, String caseNo,
			TParm parmClinicRoom, TParm parmSchDay, TConnection conn) {
		
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.update(
								" INSERT INTO REG_PATADM ("
										+ "       REALDEPT_CODE,OPT_TERM,DEPT_CODE,APPT_CODE,QUE_NO,"
										+ "       REGMETHOD_CODE,ADM_STATUS,DR_CODE,HEAT_FLG,OPT_USER,"
										+ "       MR_NO,CLINICROOM_NO,ADM_DATE,SESSION_CODE,VISIT_CODE,"
										+ "       CLINICAREA_CODE,VIP_FLG,CLINICTYPE_CODE,REGION_CODE,REPORT_STATUS,"
										+ "       CASE_NO,ADM_REGION,ARRIVE_FLG,CTZ1_CODE,SERVICE_LEVEL,"
										+ "       ADM_TYPE,REG_DATE,REALDR_CODE,OPT_DATE)"
										+ " VALUES('"
										+ deptCode
										+ "','web','"
										+ deptCode
										+ "','Y','"  //modify by huangtt  0  ��ΪY
										+ queNo     //1-5
										+ "',"
										+ "       'W','1','" //modify by huangtt 20160224 N ��ΪW ΢��ԤԼ
										+ drCode
										+ "','N','web'," //6-10
										+ "       '"
										+ mrNo
										+ "','"
										+ clinicRoomNo
										+ "',TO_DATE('"
										+ date
										+ "','YYYYMMDD'),'"
										+ sessionCode
										+ "','0',"    //11-15
										+ "       '"
										+ parmClinicRoom.getValue(
												"CLINICAREA_CODE", 0)
										+ "','"
										+ parmSchDay.getValue("VIP_FLG", 0)
										+ "','"
										+ parmSchDay.getValue(
												"CLINICTYPE_CODE", 0)
										+ "','"
										+ regionCode
										+ "','1',"  //16-20
										+ "       '"
										+ caseNo
										+ "','"
										+ regionCode
										+ "','N','"
										+ ctz1Code
										+ "','"
										+ serviceLevel   //21-25
										+ "',"
										+ "       '"
										+ admType
										+ "',SYSDATE,'"
										+ drCode + "',SYSDATE)", conn));
		//System.out.println("=====sql=============="+sql);
		return parm;
	}

	/**
	 * ȡ��ԭ��ȡ��CASE_NO
	 * 
	 * @param regionCode
	 *            String
	 * @param systemCode
	 *            String
	 * @param operation
	 *            String
	 * @param section
	 *            String
	 * @return String
	 */
	private String getNo(String regionCode, String systemCode,
			String operation, String section) {
		String caseNo="";
		try{
		TParm parm = new TParm();
		parm.setData("REGION_CODE", regionCode);
		parm.setData("SYSTEM_CODE", systemCode);
		parm.setData("OPERATION", operation);
		parm.setData("SECTION", section);
		TJDOTool tJDOTool = new TJDOTool();
		tJDOTool.setModuleName("sys\\SYSSystemModule.x");
		caseNo=tJDOTool.getResultString(tJDOTool.call("getNo", parm), "NO");
		}catch(Exception e){
			e.printStackTrace();
		}
		return caseNo;
	}

	/**
	 * ԤԼ�˺�
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String regUnAppt(String code, String password, String caseNo) {
		String result = checkAuthority(code, password, "REG_INF", "WRITE");
		if (result.length() > 0)
			return result;
		if (caseNo == null || caseNo.length() == 0)
			return "Err:����Ų���Ϊ��";
		TParm parmRegInf = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								" SELECT ARRIVE_FLG,VIP_FLG,CLINICROOM_NO,ADM_TYPE,"
										+ "        TO_CHAR(ADM_DATE,'YYYYMMDD') ADM_DATE,SESSION_CODE,"
										+ "        QUE_NO,DEPT_CODE,REGION_CODE"
										+ " FROM REG_PATADM "
										+ " WHERE CASE_NO = '" + caseNo + "'"));
		if (parmRegInf.getErrCode() < 0)
			return "Err" + parmRegInf;
		if (parmRegInf.getCount() <= 0)
			return "Err:δ�ҵ�����ԤԼ��Ϣ";
		if (parmRegInf.getValue("ARRIVE_FLG", 0).equals("Y"))
			return "Err:�����Ѿ�����,�뵽�շѹ�̨�����˹�ҵ��";
		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		if (conn == null)
			return "Err:δȡ������";
		TParm parm = new TParm(TJDODBTool.getInstance().update(
				" UPDATE REG_PATADM" + " SET REGCAN_USER = 'web',"
						+ "     REGCAN_DATE = SYSDATE" + " WHERE CASE_NO = '"
						+ caseNo + "'", conn));
		if (parm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return "Err" + parm;
		}
//		TParm parmRegParm = new TParm(TJDODBTool.getInstance().select(
//				" SELECT QUEREUSE_FLG " + " FROM REG_SYSPARM"));
//		if (parmRegParm.getErrCode() < 0) {
//			conn.rollback();
//			conn.close();
//			return "Err" + parmRegParm;
//		}
//		if (parmRegParm.getCount() < 0) {
//			conn.rollback();
//			conn.close();
//			return "Err:δȡ�ùҺŲ�������Ϣ";
//		}
		//�˹�
//		if (parmRegParm.getValue("QUEREUSE_FLG", 0).equals("Y")) {
			TParm parmQue = new TParm();
			if(parmRegInf.getValue("VIP_FLG", 0).equals("Y")){
				parmQue = updateVIPQueNo(parmRegInf.getValue("CLINICROOM_NO",
						0), parmRegInf.getValue("ADM_TYPE", 0), parmRegInf
						.getValue("ADM_DATE", 0), parmRegInf.getValue(
						"SESSION_CODE", 0), parmRegInf.getValue("QUE_NO", 0), "N",
						conn);
				if (parmQue.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return "Err" + parmQue;
				}

			}
			
			
			
//		}
		conn.commit();
		conn.close();
		return "Y";
	}

	/**
	 * ȡ�ò�����ǰ����ԤԼδ������Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param mrNo
	 *            String
	 * @return String[]
	 */
	public String[] getPatAppRegInfo(String code, String password, String mrNo) {
		String result = checkAuthority(code, password, "REG_INF", "READ");
		if (result.length() > 0)
			return new String[] { result };
		String sql=" SELECT CASE_NO||';'||TO_CHAR(ADM_DATE,'YYYYMMDD')||';'||SESSION_CODE||';'||DEPT_CODE||';'||QUE_NO INFO"
			+ " FROM   REG_PATADM"
			+ " WHERE  MR_NO = '"
			+ mrNo
			+ "'"
			+ " AND    APPT_CODE = '0'"
			+ " AND    ARRIVE_FLG = 'N'"
			+ " AND    REGCAN_USER IS NULL";
		
		//System.out.println("----Sql2222-----"+sql);
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.select(sql));
		
		if (parm.getErrCode() < 0)
			return new String[] { "Err:" + parm };
		if (parm.getCount() <= 0)
			return new String[] { "" };
		String[] appRegInfo = new String[parm.getCount()];
		for (int i = 0; i < parm.getCount(); i++) {
			appRegInfo[i] = parm.getValue("INFO", i);
		}
		return appRegInfo;
	}

	/**
	 * ȡ��ʱ����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @param admType
	 *            String
	 * @return String[]
	 */
	public String[] getSessionInf(String code, String password, String admType) {
		String result = checkAuthority(code, password, "SESSION_INF", "READ");
		if (result.length() > 0)
			return new String[] { result };
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT SESSION_CODE||';'||SESSION_DESC INFO"
						+ " FROM   REG_SESSION" + " WHERE  ADM_TYPE = '"
						+ admType + "'"));
		if (parm.getErrCode() < 0)
			return new String[] { "Err:" + parm };
		if (parm.getCount() <= 0)
			return new String[] { "" };
		String[] sessionInfo = new String[parm.getCount()];
		for (int i = 0; i < parm.getCount(); i++) {
			sessionInfo[i] = parm.getValue("INFO", i);
		}
		return sessionInfo;
	}

	/**
	 * ȡ�������Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getClinicRoom(String code, String password) {
		String result = checkAuthority(code, password, "CLINICROOM_INF", "READ");
		if (result.length() > 0)
			return new String[] { result };
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT CLINICROOM_NO||';'||CLINICROOM_DESC||';'||CLINICAREA_CODE INFO"
						+ " FROM   REG_CLINICROOM"));
		if (parm.getErrCode() < 0)
			return new String[] { "Err:" + parm };
		if (parm.getCount() <= 0)
			return new String[] { "" };
		String[] clinicRoomInfo = new String[parm.getCount()];
		for (int i = 0; i < parm.getCount(); i++) {
			clinicRoomInfo[i] = parm.getValue("INFO", i);
		}
		return clinicRoomInfo;
	}

	/**
	 * ȡ��������Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getClinicArea(String code, String password) {
		String result = checkAuthority(code, password, "CLINICAREA_INF", "READ");
		if (result.length() > 0)
			return new String[] { result };
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT CLINICAREA_CODE||';'||CLINIC_DESC||';'||REGION_CODE INFO"
						+ " FROM   REG_CLINICAREA"));
		if (parm.getErrCode() < 0)
			return new String[] { "Err:" + parm };
		if (parm.getCount() <= 0)
			return new String[] { "" };
		String[] clinicArea = new String[parm.getCount()];
		for (int i = 0; i < parm.getCount(); i++) {
			clinicArea[i] = parm.getValue("INFO", i);
		}
		return clinicArea;
	}

	/**
	 * ȡ�ÿ��ҷ�����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getDeptClassRule(String code, String password) {
		String result = checkAuthority(code, password, "DEPTCLASS_INF", "READ");
		if (result.length() > 0)
			return new String[] { result };
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT CATEGORY_CODE||';'||CATEGORY_CHN_DESC INFO"
						+ " FROM   SYS_CATEGORY"
						+ " WHERE  RULE_TYPE = 'SYS_DEPT'"));
		if (parm.getErrCode() < 0)
			return new String[] { "Err:" + parm };
		if (parm.getCount() <= 0)
			return new String[] { "" };
		String[] deptClassRule = new String[parm.getCount()];
		for (int i = 0; i < parm.getCount(); i++) {
			deptClassRule[i] = parm.getValue("INFO", i);
		}
		return deptClassRule;
	}
	/**
	 * 
	 * @param code
	 * @param password
	 * @return
	 */
	public String[] getSysFee(String code, String password) {
		String result = checkAuthority(code, password, "SYS_FEE", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"select * from SYS_FEE ORDER BY ORDER_CODE");
	}
	
	/**
	 * 
	 * @param code
	 * @param password
	 * @return
	 */
	public String[] getSysFeeHistory(String code, String password) {
		String result = checkAuthority(code, password, "SYS_FEE_HISTORY", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"select * from SYS_FEE_HISTORY ORDER BY ORDER_CODE");
	}
	
	public String[] getPatByMrNo(String mrNo){

		return TJDODBTool
		.getInstance()
		.selectList(
				"SELECT * FROM SYS_PATINFO WHERE MR_NO="+mrNo);
		
	}
	
	/**
	 * ȡ��ѪƷ��Ϣ
	 * @param code
	 * @param password
	 * @return
	 */
	public String [] getBLDInfo(String code, String password){
		String result = checkAuthority(code, password,"BLD_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		
		return TJDODBTool
		.getInstance()
		.selectList(
				"select * from BMS_BLDCODE ORDER BY BLD_CODE");
		
	}
	
	
	/**
	 * 
	 * @param mrNo
	 * @return
	 */
	public String[] getPatByMrNo(String code, String password,String mrNo){
		String result = checkAuthority(code, password,"PAT_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
		.getInstance()
		.selectList(
				"SELECT * FROM SYS_PATINFO WHERE MR_NO="+mrNo);
		//Patinfo patinfo=new Patinfo();
/*		TParm parm=new TParm(TJDODBTool
		.getInstance().select("SELECT * FROM SYS_PATINFO WHERE MR_NO="+mrNo));
		parm=parm.getRow(0);
		patinfo.setMR_NO(parm.getValue("MR_NO"));
		patinfo.setIPD_NO(parm.getValue("IPD_NO"));
		patinfo.setDELETE_FLG(parm.getValue("DELETE_FLG"));
		patinfo.setMERGE_FLG(parm.getValue("MERGE_FLG"));
		patinfo.setMOTHER_MRNO(parm.getValue("MOTHER_MRNO"));
		patinfo.setPAT_NAME(parm.getValue("PAT_NAME"));
		patinfo.setPAT_NAME1(parm.getValue("pATNAME1"));
		patinfo.setPY1(parm.getValue("pY1"));
		patinfo.setPY2(parm.getValue("pY2"));
		patinfo.setFOREIGNER_FLG(parm.getValue("FOREIGNER_FLG"));
		patinfo.setIDNO(parm.getValue("iDNO"));
		patinfo.setBIRTH_DATE(StringTool.getString(parm.getTimestamp("BIRTH_DATE"),
        "yyyy-MM-dd"));
		patinfo.setCTZ1_CODE(parm.getValue("CTZ1_CODE"));
		patinfo.setCTZ2_CODE(parm.getValue("CTZ2_CODE"));
		patinfo.setCTZ3_CODE(parm.getValue("CTZ3_CODE"));
		patinfo.setTEL_COMPANY(parm.getValue("TEL_COMPANY"));
		patinfo.setTEL_HOME(parm.getValue("TEL_HOME"));
		patinfo.setTEL_HOME(parm.getValue("TEL_HOME"));
		patinfo.setCELL_PHONE(parm.getValue("CELL_PHONE"));
		patinfo.setCOMPANY_DESC(parm.getValue("COMPANY_DESC"));
		patinfo.setE_MAIL(parm.getValue("E_MAIL"));
		patinfo.setBLOOD_TYPE(parm.getValue("BLOOD_TYPE"));
		patinfo.setBLOOD_RH_TYPE(parm.getValue("BLOOD_RH_TYPE"));
		patinfo.setSEX_CODE(parm.getValue("SEX_CODE"));
		patinfo.setMARRIAGE_CODE(parm.getValue("MARRIAGE_CODE"));
		patinfo.setPOST_CODE(parm.getValue("POST_CODE"));
		patinfo.setADDRESS(parm.getValue("ADDRESS"));
		patinfo.setRESID_POST_CODE(parm.getValue("RESID_POST_CODE"));
		patinfo.setRESID_ADDRESS(parm.getValue("RESID_ADDRESS"));
		patinfo.setCONTACTS_NAME(parm.getValue("CONTACTS_NAME"));
		patinfo.setRELATION_CODE(parm.getValue("RELATION_CODE"));
		patinfo.setCONTACTS_TEL(parm.getValue("CONTACTS_TEL"));
		patinfo.setCONTACTS_ADDRESS(parm.getValue("CONTACTS_ADDRESS"));
		patinfo.setSPOUSE_IDNO(parm.getValue("SPOUSE_IDNO"));
		patinfo.setFATHER_IDNO(parm.getValue("FATHER_IDNO"));
		patinfo.setMOTHER_IDNO(parm.getValue("MOTHER_IDNO"));
		patinfo.setRELIGION_CODE(parm.getValue("RELIGION_CODE"));
		patinfo.setEDUCATION_CODE(parm.getValue("EDUCATION_CODE"));
		patinfo.setOCC_CODE(parm.getValue("OCC_CODE"));
		patinfo.setNATION_CODE(parm.getValue("NATION_CODE"));
		patinfo.setSPECIES_CODE(parm.getValue("SPECIES_CODE"));
		patinfo.setFIRST_ADM_DATE(StringTool.getString(parm.getTimestamp("FIRST_ADM_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		patinfo.setRCNT_OPD_DATE(StringTool.getString(parm.getTimestamp("RCNT_OPD_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		patinfo.setRCNT_OPD_DEPT(parm.getValue("RCNT_OPD_DEPT"));
		patinfo.setRCNT_IPD_DATE(StringTool.getString(parm.getTimestamp("RCNT_IPD_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		patinfo.setRCNT_IPD_DEPT(parm.getValue("RCNT_IPD_DEPT"));
		
		patinfo.setRCNT_EMG_DATE(StringTool.getString(parm.getTimestamp("RCNT_EMG_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		
		patinfo.setRCNT_EMG_DEPT(parm.getValue("RCNT_EMG_DEPT"));
		
		patinfo.setRCNT_MISS_DATE(StringTool.getString(parm.getTimestamp("RCNT_MISS_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		patinfo.setRCNT_MISS_DEPT(parm.getValue("RCNT_MISS_DEPT"));   
		
		patinfo.setKID_EXAM_RCNT_DATE(StringTool.getString(parm.getTimestamp("KID_EXAM_RCNT_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		
		patinfo.setKID_INJ_RCNT_DATE(StringTool.getString(parm.getTimestamp("KID_INJ_RCNT_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		
		patinfo.setADULT_EXAM_DATE(StringTool.getString(parm.getTimestamp("ADULT_EXAM_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		
		patinfo.setSMEAR_RCNT_DATE(StringTool.getString(parm.getTimestamp("SMEAR_RCNT_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		
		patinfo.setDEAD_DATE(StringTool.getString(parm.getTimestamp("DEAD_DATE"),
        "yyyy-MM-dd HH:mm:ss"));
		patinfo.setHEIGHT(parm.getValue("HEIGHT"));
		patinfo.setWEIGHT(parm.getValue("WEIGHT"));
		patinfo.setDESCRIPTION(parm.getValue("DESCRIPTION"));
		patinfo.setBORNIN_FLG(parm.getValue("BORNIN_FLG"));
		
		return patinfo;*/

	}
	
	/**
	 * ��ô�λ�ֵ�
	 * 
	 * @param code
	 * @param password
	 * @return
	 */
	public String[] getBeds(String code, String password) {
		String result = checkAuthority(code, password, "BED_INF", "read");
		String sql="SELECT BED_NO,BED_NO_DESC,PY1,PY2,SEQ,";
		       sql+="DESCRIPTION,ROOM_CODE,STATION_CODE,REGION_CODE,BED_CLASS_CODE,";
		       sql+="BED_TYPE_CODE,ACTIVE_FLG,APPT_FLG,ALLO_FLG,BED_OCCU_FLG,";
		       sql+="RESERVE_BED_FLG,SEX_CODE,OCCU_RATE_FLG,DR_APPROVE_FLG,BABY_BED_FLG,";
		       sql+="ADM_TYPE,MR_NO,CASE_NO,IPD_NO,DEPT_CODE,BED_STATUS";
		       sql+=" FROM SYS_BED ORDER BY BED_NO,SEQ";
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(sql);
	}
	
	/**
	 * ��÷����ֵ�
	 * 
	 * @param code
	 * @param password
	 * @return
	 */
	public String[] getRooms(String code, String password) {
		String result = checkAuthority(code, password, "ROOM_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"SELECT ROOM_CODE,ROOM_DESC,PY1,PY2,SEQ,ENG_DESC,DESCRIPT,STATION_CODE,REGION_CODE,SEX_LIMIT_FLG,RED_SIGN,YELLOW_SIGN FROM SYS_ROOM ORDER BY ROOM_CODE,SEQ");
	}

	/**
     * �õ����Ҳ�����Ӧ��Ϣ
     * 
     * @param code
     * @param password
     * @return
     */
    public String[] getDeptStationList(String code, String password) {
        String result = checkAuthority(code, password, "DEPTSTATION_LIST", "read");
        if (result.length() > 0)
            return new String[] { result };
        String sql="SELECT DEPT_CODE,STATION_CODE,COST_CENTER_CODE FROM SYS_STADEP_LIST ORDER BY DEPT_CODE,STATION_CODE";
        return TJDODBTool.getInstance().selectList(sql);
    }
    
    /**
	 * ���¾������
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param regionCode
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm deleteQueNo(String clinicRoomNo, String admType, String date,
			String sessionCode, String regionCode,String deptCode, TConnection conn) {
		TParm parm = new TParm(TJDODBTool.getInstance()
				.update(
						" UPDATE REG_SCHDAY" + " SET QUE_NO = QUE_NO-1"
								+ " WHERE CLINICROOM_NO = '" + clinicRoomNo
								+ "'" + " AND   REGION_CODE = '" + regionCode
								+ "'" + " AND   ADM_TYPE = '" + admType + "'"
								+ " AND   ADM_DATE = '" + date + "'"
								+ " AND   DEPT_CODE = '" + deptCode + "'"
								+ " AND   SESSION_CODE = '" + sessionCode 
								+ "'", conn));
		return parm;
	}
    
    /**
     * �õ���Ժ������Ϣ
     * 
     * @param code
     * @param password
     * @return
     */
    public String[] getInPats(String code, String password, String stationCode) {// wanglong add
                                                                                 // 20141010
        String result = checkAuthority(code, password, "PATS_LIST", "read");
        if (result.length() > 0) return new String[]{result };
        String patSql =
                "SELECT CASE_NO FROM ADM_INP WHERE DS_DATE IS NULL AND (CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL)";
        if (!StringUtil.isNullString(stationCode)) {
            patSql += " AND STATION_CODE='#'".replaceFirst("#", stationCode);
        }
        TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patParm.getErrCode() < 0 || patParm.getCount() <= 0) {
            return new String[]{};
        }
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < patParm.getCount(); i++) {
            list.add(ADMXMLTool.getInstance().creatPatXMLStr(patParm.getValue("CASE_NO", i)));
        }
        return (String[]) list.toArray(new String[0]);
    }

    /**
     * �õ������ų̡�״̬������Ϣ
     * 
     * @param code
     * @param password
     * @return
     */
    public String[] getReadyOPs(String code, String password, String stationCode) {// wanglong add
                                                                                   // 20141010
        String result = checkAuthority(code, password, "OPERATION_LIST", "read");
        if (result.length() > 0) return new String[]{result };
        String opeSql =
                "SELECT A.CASE_NO, A.OPBOOK_SEQ FROM OPE_OPBOOK A, ADM_INP B "
                        + " WHERE A.STATE IS NOT NULL AND A.STATE <> '0' "
                        + "   AND A.CASE_NO = B.CASE_NO AND B.DS_DATE IS NULL # "
                        + "   AND (B.CANCEL_FLG <> 'Y' OR B.CANCEL_FLG IS NULL) "
                        + "ORDER BY A.OPBOOK_SEQ";
        if (!StringUtil.isNullString(stationCode)) {
            opeSql =
                    opeSql.replaceFirst("#",
                                        " AND B.STATION_CODE='#' ".replaceFirst("#", stationCode));
        } else {
            opeSql = opeSql.replaceFirst("#", "");
        }
        TParm opeParm = new TParm(TJDODBTool.getInstance().select(opeSql));
        if (opeParm.getErrCode() < 0 || opeParm.getCount() <= 0) {
            return new String[]{};
        }
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < opeParm.getCount(); i++) {
            list
                    .add(ADMXMLTool.getInstance()
                            .creatOPEInfoXMLStr(opeParm.getValue("CASE_NO", i),
                                                opeParm.getValue("OPBOOK_SEQ", i)));
        }
        return (String[]) list.toArray(new String[0]);
    }

    /**
     * �õ�APP���˹���Ϣ
     * @param code String
     * @param password String
     * @param startDate String �Һ����ڿ�ʼ 20141201
     * @param endDate String �Һ����ڽ��� 20141231
     * @return String[] �����,������,����,���֤��,�ֻ�,�Һ�ʱ��,�˺�ʱ��
     */
    public String[] getRegCancelInfo(String code, String password, String startDate, String endDate) {
        String result = checkAuthority(code, password, "REG_CANCEL", "read");
        if (result.length() > 0) return new String[]{result };
        String sql =
                "SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME, B.IDNO, B.TEL_HOME, A.REG_DATE, A.REGCAN_DATE "
                        + "  FROM REG_PATADM A, SYS_PATINFO B "
                        + " WHERE A.MR_NO = B.MR_NO           "
                        + "   AND A.REGCAN_USER IS NOT NULL   "
                        + "   AND A.REGCAN_USER = 'web'       "
                        + "   AND A.REG_DATE BETWEEN TO_DATE( '#', 'YYYYMMDD') AND @ "
                        + "ORDER BY A.CASE_NO";
        if (StringUtil.isNullString(startDate)) {
            return new String[]{};
        } else {
            sql = sql.replaceFirst("#", startDate.trim().substring(0, 8));
        }
        if (StringUtil.isNullString(endDate)) {
            sql = sql.replaceFirst("@", "SYSDATE");
        } else {
            sql =
                    sql.replaceFirst("@", " TO_DATE( '#235959', 'YYYYMMDDHH24MISS')"
                            .replaceFirst("#", endDate.trim().substring(0, 8)));
        }
        return TJDODBTool.getInstance().selectList(sql);
    }
    
	/**
	 * RISҽ����Ϣ
	 * 
	 * @param code
	 *            String
	 * @param password
	 *            String
	 * @return String[]
	 */
	public String[] getRisOrder(String code, String password) {
		String result = checkAuthority(code, password, "RIS_ORDER_INF", "read");
		if (result.length() > 0)
			return new String[] { result };
		return TJDODBTool
				.getInstance()
				.selectList(
						"SELECT ORDER_CODE,ORDER_DESC from SYS_FEE WHERE CAT1_TYPE='RIS' AND ORDERSET_FLG='Y' ORDER BY ORDER_CODE");
	}
	
	/**
	 * ���ݲ����ţ����ڲ�ѯ����ֵ
	 * @param patName
	 * @param mrNo
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public String[] getCheckDate(String code, String password, String mrNo,String sDate,String eDate){		
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return new String[] { result };
		
		if (StringUtil.isNullString(sDate) || StringUtil.isNullString(eDate)) {
            return new String[]{};
        }
		
		int mrnoLength = PatTool.getInstance().getMrNoLength();

		
		String sql = "  SELECT A.PAT_NAME,A.MR_NO, D.IDNO,A.ORDER_DESC,C.LIS_DESC," +
				" B.TESTITEM_CHN_DESC,B.TEST_VALUE,B.TEST_UNIT," +
				" TO_CHAR(B.OPT_DATE,'YYYY/MM/DD HH24:MI:SS') OPT_DATE" +
				",B.UPPE_LIMIT, B.LOWER_LIMIT" +
				" FROM MED_APPLY A, MED_LIS_RPT B,MED_LIS_MAP C,SYS_PATINFO D" +
				" WHERE A.APPLICATION_NO = B.APPLICATION_NO" + 
				" AND A.ADM_TYPE <> 'I'" +
				" AND B.CAT1_TYPE = 'LIS'" +
				" AND A.ORDER_NO = B.ORDER_NO AND A.SEQ_NO = B.SEQ_NO" +
				"  AND A.MR_NO = D.MR_NO " +
				" AND B.TESTITEM_CODE = C.LIS_ID(+)" +
				" AND B.OPT_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" ;
		if(mrNo.length() == mrnoLength){
			sql += " AND A.MR_NO='"+mrNo+"'";
		}else{
			sql += " AND D.IDNO ='"+mrNo+"'";
		}
		
		String sql1 = "  SELECT A.PAT_NAME,A.MR_NO, D.IDNO,A.ORDER_DESC,C.LIS_DESC," +
				" B.TESTITEM_CHN_DESC,B.TEST_VALUE,B.TEST_UNIT," +
				" TO_CHAR(B.OPT_DATE,'YYYY/MM/DD HH24:MI:SS') OPT_DATE" +
				",B.UPPE_LIMIT, B.LOWER_LIMIT" +
				" FROM MED_APPLY A, MED_LIS_RPT B,MED_LIS_MAP C,SYS_PATINFO D,ADM_INP E" +
				" WHERE A.APPLICATION_NO = B.APPLICATION_NO" +
				" AND A.ADM_TYPE = 'I'" +
				" AND B.CAT1_TYPE = 'LIS'" +
				" AND A.ORDER_NO = B.ORDER_NO AND A.SEQ_NO = B.SEQ_NO" +
				"  AND A.MR_NO = D.MR_NO " +
				" AND A.CASE_NO = E.CASE_NO AND E.DS_DATE IS NOT NULL"+
				" AND B.TESTITEM_CODE = C.LIS_ID(+)" +
				" AND B.OPT_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" ;
		if(mrNo.length() == mrnoLength){
			sql1 += " AND A.MR_NO='"+mrNo+"'";
		}else{
			sql1 += " AND D.IDNO ='"+mrNo+"'";
		}
		
		sql += " UNION ALL "+sql1;

		sql +=" ORDER BY MR_NO, OPT_DATE DESC";
		
		System.out.println(sql);
		
		return TJDODBTool.getInstance().selectList(sql);
		
	}
	
	public String[] getRisDate(String code, String password, String mrNo,String sDate,String eDate){
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return new String[] { result };
		
		if (StringUtil.isNullString(sDate) || StringUtil.isNullString(eDate)) {
            return new String[]{};
        }
		
		int mrnoLength = PatTool.getInstance().getMrNoLength();
		
		String sql = "SELECT A.PAT_NAME, A.MR_NO, C.IDNO, A.ORDER_DESC," +
				" B.OUTCOME_TYPE, B.OUTCOME_DESCRIBE, B.OUTCOME_CONCLUSION,  " +
				" TO_CHAR (B.OPT_DATE, 'YYYY/MM/DD HH24:MI:SS') OPT_DATE" +
				" FROM MED_APPLY A, MED_RPTDTL B, SYS_PATINFO C " +
				" WHERE A.APPLICATION_NO = B.APPLICATION_NO AND A.MR_NO = C.MR_NO" +
				" AND A.ADM_TYPE <> 'I' AND B.CAT1_TYPE = 'RIS' "+
				" AND B.OPT_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND  TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')";
		
		String sql1 = "SELECT A.PAT_NAME, A.MR_NO, C.IDNO, A.ORDER_DESC," +
				" B.OUTCOME_TYPE, B.OUTCOME_DESCRIBE, B.OUTCOME_CONCLUSION,  " +
				" TO_CHAR (B.OPT_DATE, 'YYYY/MM/DD HH24:MI:SS') OPT_DATE" +
				" FROM MED_APPLY A, MED_RPTDTL B, SYS_PATINFO C,ADM_INP E" +
				" WHERE A.APPLICATION_NO = B.APPLICATION_NO AND A.MR_NO = C.MR_NO" +
				" AND A.ADM_TYPE = 'I' AND B.CAT1_TYPE = 'RIS' "+
				" AND A.CASE_NO = E.CASE_NO AND E.DS_DATE IS NOT NULL"+
				" AND B.OPT_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND  TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')";
		
		if(mrNo.length() == mrnoLength){
			sql += " AND A.MR_NO='"+mrNo+"'";
			sql1 += " AND A.MR_NO='"+mrNo+"'";
		}else{
			sql += " AND C.IDNO ='"+mrNo+"'";
			sql1 += " AND C.IDNO ='"+mrNo+"'";
		}
		
		sql += " UNION ALL "+sql1;
		sql +=" ORDER BY MR_NO, OPT_DATE DESC";
		
		System.out.println(sql);
		
		return TJDODBTool.getInstance().selectList(sql);
		
	}
	
	public String getMrNo(String code, String password, String patName,
			String sex, String birthDay, String idNo, String tel) {
		String result = checkCode(code, password, true);
		if (result.length() > 0)
			return result;
		
		if(patName == null || patName.length() == 0){
			return "Err:��������Ϊ��";
		}
		
		if (sex == null || sex.length() == 0)
			return "Err:�Ա𲻿�Ϊ��";	
		
		if (birthDay == null || birthDay.length() == 0)
			return "Err:�������ڲ���Ϊ��";	
		
		if (idNo == null || idNo.length() == 0)
			return "Err:���֤�Ų���Ϊ��";	
		
		if (tel == null || tel.length() == 0)
			return "Err:�绰����Ϊ��";	
		
		String sql = "SELECT MR_NO FROM SYS_PATINFO WHERE IDNO = '"+idNo+"' AND PAT_NAME='"+patName+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			return parm.getValue("MR_NO", 0);
		}
		
		// ȡ�����ݿ�����
		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		if (conn == null){
			return "Err:δȡ�����ݿ�����";
		
		}
		
		String newMrNo = SystemTool.getInstance().getMrNo();
		TParm patParm = new TParm();
		patParm.setData("MR_NO", newMrNo);	
		patParm.setData("PAT_NAME", patName);
		patParm.setData("IDNO", idNo);
		patParm.setData("BIRTH_DATE", birthDay.replaceAll("-", ""));				
		patParm.setData("SEX_CODE", sex);
		patParm.setData("ADDRESS", "");
		patParm.setData("CTZ1_CODE", "99");
		patParm.setData("OPT_USER", "web");
		patParm.setData("OPT_TERM", "0.0.0.0");
		TParm patRe = RegQETool.getInstance().insertPat(patParm,conn);
		if(patRe.getErrCode() < 0){
			conn.rollback();
			conn.close();
			return "ERR:�����û�ʧ��";
		}
		
		TParm p = new TParm();
        p.setData("CARD_NO",newMrNo+"001"); //����
        p.setData("MR_NO", newMrNo); //������
        p.setData("CARD_SEQ", "001"); //���
        p.setData("ISSUERSN_CODE", "1"); //����ԭ��
        p.setData("FACTORAGE_FEE", 0); //������
        p.setData("PASSWORD",  OperatorTool.getInstance().encrypt("0000")); //����
        p.setData("WRITE_FLG", "Y"); //д������ע��
        p.setData("OPT_USER", "QeApp");
        p.setData("OPT_TERM", "0.0.0.0");
        
        patRe =  RegQETool.getInstance().insertEkt(p, conn);
        if(patRe.getErrCode() < 0){
			conn.rollback();
			conn.close();
			return "ERR:�����û�ʧ��";
		}
        
        p.setData("ID_NO", patParm.getData("IDNO")); //���֤��
        p.setData("NAME", patParm.getData("PAT_NAME")); //����
        p.setData("CURRENT_BALANCE", 0); //���
        patRe =  RegQETool.getInstance().insertEktMaster(p, conn);
        if(patRe.getErrCode() < 0){
			conn.rollback();
			conn.close();
			return "ERR:�����û�ʧ��";
		}
		
        conn.commit();
		conn.close();
		
		return newMrNo;
		
		
		
	}
	
	/**
	 * ��ȡ��Ժ���ߵ��������
	 * 
	 * @param code �û���
	 * @param password ����
	 * @param mrNo ������
	 * @return �������(�����+�����)
	 */
	public String getAdmInpDiagByMrNo(String code, String password, String mrNo) {
		// ��֤Ȩ��
		String checkResult = checkAuthority(code, password, "ADM_INPDIAG_INF", "read");
		if (checkResult.length() > 0) {
			return checkResult;
		}

		if (null == mrNo || mrNo.length() == 0) {
			return "Err:�����Ų���Ϊ��";
		}

		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		// ��ѯ��Ժ������Ϣ
		TParm result = ADMInpTool.getInstance().selectInHosp(parm);

		if (result.getCount() < 1) {
			return "Err:δ�ҵ��ò�������Ժ����";
		}

		parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
		// ��ѯ�������
		result = ADMDiagTool.getInstance().queryData(parm);

		if (result.getErrCode() < 0 || result.getCount() < 1) {
			return "";
		} else {
			int count = result.getCount();
			// �����
			String mainDiag = "";
			// �����
			String secondaryDiag = "";
			// �ż�������
			String opdSecondaryDiag = "";
			// ��Ժ�����
			String admInSecondaryDiag = "";
			// ��Ժ������
			String admOutSecondaryDiag = "";

			for (int i = 0; i < count; i++) {
				// �ż������
				if ("I".equals(result.getValue("IO_TYPE", i))) {
					if ("Y".equals(result.getValue("MAINDIAG_FLG", i))) {
						mainDiag = result.getValue("ICD_CHN_DESC", i);
					} else {
						opdSecondaryDiag = opdSecondaryDiag
								+ result.getValue("ICD_CHN_DESC", i) + ",";
					}
				}

				// ��Ժ���
				if ("M".equals(result.getValue("IO_TYPE", i))) {
					if ("Y".equals(result.getValue("MAINDIAG_FLG", i))) {
						mainDiag = result.getValue("ICD_CHN_DESC", i);
					} else {
						admInSecondaryDiag = admInSecondaryDiag
								+ result.getValue("ICD_CHN_DESC", i) + ",";
					}
				}

				// ��Ժ���
				if ("O".equals(result.getValue("IO_TYPE", i))) {
					if ("Y".equals(result.getValue("MAINDIAG_FLG", i))) {
						mainDiag = result.getValue("ICD_CHN_DESC", i);
					} else {
						admOutSecondaryDiag = admOutSecondaryDiag
								+ result.getValue("ICD_CHN_DESC", i) + ",";
					}
				}
			}

			// �����
			if (admOutSecondaryDiag.length() > 0) {
				secondaryDiag = admOutSecondaryDiag.substring(0,
						admOutSecondaryDiag.length() - 1);
			} else if (admInSecondaryDiag.length() > 0) {
				secondaryDiag = admInSecondaryDiag.substring(0,
						admInSecondaryDiag.length() - 1);
			} else if (admOutSecondaryDiag.length() > 0) {
				secondaryDiag = admOutSecondaryDiag.substring(0,
						admOutSecondaryDiag.length() - 1);
			}

			if (secondaryDiag.length() > 0) {
				return mainDiag + ";" + secondaryDiag;
			} else {
				return mainDiag;
			}
		}
	}
	
	/**
	 * ��ȡ��Ժ���ߵ�������ʽ
	 * 
	 * @param code �û���
	 * @param password ����
	 * @param mrNo ������
	 * @return ������ʽ(��+��)
	 */
	public String getOpeDescByMrNo(String code, String password, String mrNo) {
		// ��֤Ȩ��
		String checkResult = checkAuthority(code, password, "ICD_INF", "read");
		if (checkResult.length() > 0) {
			return checkResult;
		}

		if (null == mrNo || mrNo.length() == 0) {
			return "Err:�����Ų���Ϊ��";
		}

		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		// ��ѯ��Ժ������Ϣ
		TParm result = ADMInpTool.getInstance().selectInHosp(parm);

		if (result.getCount() < 1) {
			return "Err:δ�ҵ��ò�������Ժ����";
		}

		String caseNo = result.getValue("CASE_NO", 0);
		// ��ѯ�ò�����Ժ�ڼ����һ��������¼
		String sql = "SELECT * FROM OPE_OPBOOK WHERE CASE_NO = '" + caseNo
				+ "' AND CANCEL_FLG = 'N' ORDER BY OPBOOK_SEQ DESC";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// ��������
		String opBookSeq = "";
		// ������ICD
		String mainOpCode = result.getValue("OP_CODE1", 0);
		// ������ICD
		String secondaryOpCode = result.getValue("OP_CODE2", 0);

		if (result.getErrCode() < 0 || result.getCount() < 1) {
			return "";
		} else {
			opBookSeq = result.getValue("OPBOOK_SEQ", 0);
		}

		sql = "SELECT * FROM INW_TRANSFERSHEET WHERE CASE_NO = '" + caseNo
				+ "' AND OPBOOK_SEQ = '" + opBookSeq + "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));

		if (result.getCount() > 0
				&& StringUtils.isNotEmpty(result.getValue("OP_DESC", 0))) {
			return result.getValue("OP_DESC", 0);
		} else {
			String mainOpDesc = "";
			String secondaryOpDesc = "";
			// ��ѯ��������ֵ�
			sql = "SELECT OPERATION_ICD,OPT_CHN_DESC,OPT_ENG_DESC FROM SYS_OPERATIONICD ORDER BY SEQ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			int count = result.getCount();
			Map<String, String> opeIcdMap = new HashMap<String, String>();
			for (int i = 0; i < count; i++) {
				opeIcdMap.put(result.getValue("OPERATION_ICD", i), result
						.getValue("OPT_CHN_DESC", i));
			}

			mainOpDesc = opeIcdMap.get(mainOpCode);
			if (StringUtils.isEmpty(secondaryOpCode)) {
				return mainOpDesc;
			} else {
				secondaryOpDesc = opeIcdMap.get(secondaryOpCode);
				return mainOpDesc + ";" + secondaryOpDesc;
			}
		}
	}

}
