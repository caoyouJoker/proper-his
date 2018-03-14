package jdo.opd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 门诊医生工作站组套调用</p>
*
* <p>Description:门诊医生工作站组套调用</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20090503
* @version 1.0
*/
public class OpdComPackQuoteTool extends TJDOTool{
	//初始化主DATASTORE用
	public  static final String INIT_SQL="SELECT * FROM OPD_PACK_MAIN";
	public  static final String INIT_ORDER_SQL="SELECT * FROM OPD_PACK_ORDER";
	public  static final String INIT_DIAG_SQL="SELECT * FROM OPD_PACK_DIAG";
	//初始化诊断用
	private static final String INIT_DIAG_SQL_QUOTE=
		"SELECT 'Y' AS CHOOSE,B.ICD_CHN_DESC,B.ICD_ENG_DESC,A.DEPT_OR_DR,A.DEPTORDR_CODE," +
			"	A.PACK_CODE,A.ICD_TYPE,A.ICD_CODE,A.MAIN_DIAG_FLG,A.DIAG_NOTE" +
		" FROM OPD_PACK_DIAG A,SYS_DIAGNOSIS B";
	//初始化医嘱用
	private static final String INIT_ORDER_SQL_QUOTE=
		"SELECT 'Y' AS CHOOSE,DEPT_OR_DR,DEPTORDR_CODE,PACK_CODE,PRESRT_NO," +
		"		SEQ_NO,RX_TYPE,LINKMAIN_FLG,LINK_NO,ORDER_CODE," +
		"		MEDI_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS," +
		"		GIVEBOX_FLG,DESCRIPTION,EXEC_DEPT_CODE,SETMAIN_FLG,URGENT_FLG," +
		"		DCTAGENT_CODE,DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,OPT_USER," +
		"		OPT_DATE,OPT_TERM,ORDER_DESC,SPECIFICATION " +
		"	FROM OPD_PACK_ORDER ORDER BY RX_TYPE,SEQ_NO";
	//初始化combo用
	private static final String INIT_COMBO_SQL="SELECT PACK_CODE AS ID,PACK_DESC AS NAME FROM OPD_PACK_MAIN ORDERY BY PACK_CODE";
	/**
     * 实例
     */
    public static OpdComPackQuoteTool instanceObject;
    /**
     * 得到实例
     * @return OPDMainTool
     */
    public static OpdComPackQuoteTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new OpdComPackQuoteTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public OpdComPackQuoteTool()
    {
//        setModuleName("opd\\OPDComPackQuoteModule.x");
        onInit();
    }
    /**
     * 初始化界面方法
     * @param parm DEPT_OR_DR,DEPTORDR_CODE
     * @return String
     */
    public String initQuote(TParm parm){
    	StringBuffer result=new StringBuffer(INIT_SQL);
    	result.append(" WHERE DEPT_OR_DR='" +parm.getValue("DEPT_OR_DR")+"'").append(" AND DEPTORDR_CODE='" +parm.getValue("DEPTORDR_CODE")+"'");
    	return result.toString();
    }
    /**
     * 初始化模板诊断 
     * @param parm DEPT_OR_DR,DEPTORDR_CODE
     * @return String
     */
    public TParm initQuoteDiag(TParm parm){
    	String sql=initQuoteDiagSQl(INIT_DIAG_SQL_QUOTE,parm);
    	//System.out.println("diag sql----============="+sql);
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql.toString()));
    	return result;
    }
    public String initQuoteDiagSQl(String sql,TParm parm){
    	StringBuffer result=new StringBuffer(sql);
    	result.append(" WHERE DEPT_OR_DR='" +parm.getValue("DEPT_OR_DR")+"'").append(" AND DEPTORDR_CODE='" 
    			+parm.getValue("DEPTORDR_CODE")+"'AND PACK_CODE='" +parm.getValue("PACK_CODE")+
    			"' AND A.ICD_CODE=B.ICD_CODE ");
    	return result.toString();
    }
    /**
     * 初始化模板诊断
     * @param parm DEPT_OR_DR,DEPTORDR_CODE
     * @return String
     */
    public String initQuoteOrder(TParm parm){
    	StringBuffer result=new StringBuffer(INIT_ORDER_SQL);
    	result.append(" WHERE DEPT_OR_DR='" +parm.getValue("DEPT_OR_DR")+"'").append(" AND DEPTORDR_CODE='" +parm.getValue("DEPTORDR_CODE")+"'");
    	return result.toString();
    }
    /**
     * 初始化界面主COMBO
     * @param parm
     * @return
     */
    public TParm initCombo(TParm parm){
    	TParm result=new TParm();
    	if(parm==null){
    		return result;
    	}
    	String sql=INIT_COMBO_SQL+
    			" WHERE DEPT_OR_DR='" +parm.getValue("DEPT_OR_DR")+
    				 "' AND DEPTORDR_CODE='"+parm.getValue("DEPTORDR_CODE")+
    				 "'";
    	result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 按主键取得处置数据
     * @param parm
     * @return
     */
    public TParm initOp(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			return result;
		}
		String sql = initOpSql(parm);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
    /**
     * 获得处置的初始化SQL
     * @param parm
     * @return
     */
    public String initOpSql(TParm parm){
    	TParm result = new TParm();
		if (parm == null) {
			return "";
		}
		String sql = INIT_ORDER_SQL + " WHERE DEPT_OR_DR='"
				+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
				+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
				+ parm.getValue("PACK_CODE") + "' AND RX_TYPE='4' ORDER BY SEQ_NO";
		return sql;
    }
    /**
     * 按主键取得处置数据
     * @param parm
     * @return
     */
    public TParm initOrder(TParm parm){

		TParm result = new TParm();
		if (parm == null) {
			return result;
		}
		String sql = initOrderSQL(INIT_ORDER_SQL_QUOTE, parm);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
    }
    /**
     * 取得西成药的SQL语句
     * @param parm DEPT_OR_DR,DEPTORDR_CODE,PACK_CODE
     * @return
     */
    public String initOrderSQL(String sql,TParm parm){
    	if(parm==null){
    		return "";
    	}
    	String result = sql + " WHERE DEPT_OR_DR='"
		+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
		+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
		+ parm.getValue("PACK_CODE") + "' AND RX_TYPE IN('1','2') ORDER BY SEQ_NO";
    	return result;
    }
    /**
     * 取得中药的SQL语句
     * @param sql 给入SQL
     * @param parm
     * @return
     */
    public String initChnSQL(String sql,TParm parm){
      	if(parm==null){
    		return "";
    	}
    	String result = sql + " WHERE DEPT_OR_DR='"
		+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
		+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
		+ parm.getValue("PACK_CODE") + "' AND RX_TYPE ='3' ORDER BY PRESRT_NO DESC, SEQ_NO";
    	return result;
    }
    /**
     * 按主键取得处置数据
     * @param parm
     * @return
     */
    public TParm initChn(TParm parm){

		TParm result = new TParm();
		if (parm == null) {
			return result;
		}
		String sql = INIT_ORDER_SQL + " WHERE DEPT_OR_DR='"
				+ parm.getValue("DEPT_OR_DR") + "' AND DEPTORDR_CODE='"
				+ parm.getValue("DEPTORDR_CODE") + "' AND PACK_CODE='"
				+ parm.getValue("PACK_CODE") + "' AND RX_TYPE ='3' ORDER BY SEQ_NO";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
    }
}
