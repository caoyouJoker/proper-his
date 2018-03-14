package jdo.odo;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: 处方量统计表</p>
*
* <p>Description:处方量统计表</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20091016
* @version 1.0
*/
public class OdoCaseQuatityStaticTool extends TJDOTool {
	/**
     * 实例
     */
    public static OdoCaseQuatityStaticTool instanceObject;
    //=================pangben modify 20110418 添加区域显示
    private static final String QUERY="SELECT A.REGION_CHN_DESC,A.ADM_TYPE,A.DEPT_CODE,A.DR_CODE,COUNT(A.RX_NO) COUNT,SUM(A.AR_AMT) AR_AMT" +
    		"  FROM  (SELECT" +
    		"     B.REGION_CHN_DESC,C.RX_NO,C.RX_TYPE,C.REGION_CODE,C.PRINTTYPEFLG_INFANT,C.DEPT_CODE,C.DR_CODE,C.ADM_TYPE,SUM(C.AR_AMT) AR_AMT" +
    		"      FROM OPD_ORDER C,SYS_REGION B" +
    		"      WHERE C.REGION_CODE=B.REGION_CODE AND C.ORDER_DATE >=TO_DATE('#','YYYYMMDDHH24MISS')" +
    		"	         AND C.ORDER_DATE<=TO_DATE('#','YYYYMMDDHH24MISS') # GROUP BY B.REGION_CHN_DESC,C.RX_NO,C.RX_TYPE,C.REGION_CODE,C.PRINTTYPEFLG_INFANT,C.DEPT_CODE,C.DR_CODE,C.ADM_TYPE) A " +
    		"      GROUP BY A.REGION_CHN_DESC,A.ADM_TYPE,A.DEPT_CODE,A.DR_CODE ORDER BY A.REGION_CHN_DESC";
    /**
     * 根据RX_NO查询一张处方签的金额
     */
    private static final String GET_AR_AMT="SELECT SUM(AR_AMT) FROM OPD_ORDER WHERE RX_NO='#' ";
    /**
     * 得到实例
     * @return OPDMainTool
     */
    public static OdoCaseQuatityStaticTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new OdoCaseQuatityStaticTool();
        return instanceObject;
    }
    /**
     * 领票插入新数据数据
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
    	TParm result=new TParm();
    	String sql=getSql(parm);
   //	System.out.println("OdoCaseQuatityStaticTool.sql======="+sql);
    	result=new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getErrCode() < 0)
        {
            //System.out.println(result.getErrText());
            //System.out.println("err sql="+sql);
            return result;
        }
        return result;
    }
    /**
     * 根据给入进参生成SQL
     * @param parm
     * @return
     */
    public String getSql(TParm parm){
    	String sql="";
    	if(parm==null){
    		return sql;
    	}
    	String startTime=parm.getValue("START_TIME");
    	String endTime=parm.getValue("END_TIME");
    	//System.out.println("parm="+parm);
    	parm.removeData("START_TIME");
    	parm.removeData("END_TIME");
    	//System.out.println("parm="+parm);
    	String where=getWhere(parm);
    	sql=QUERY.replaceFirst("#", startTime).replaceFirst("#", endTime).replaceFirst("#", where);
            //System.out.println("getsql::"+sql);
    	return sql;
    }
    /**
     * 取得WHERE条件
     * @param parm
     * @return
     */
    private String getWhere(TParm parm){
    	String where="";
    	String[] names=parm.getNames();
    	for(int i=0;i<names.length;i++){
    		where+=" AND C."+parm.getValue(names[i]);
    	}
          //  System.out.println("where::"+where);
    	return where;
    }
}
