package jdo.hrm;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: 健康检查套餐设定Tool</p>
*
* <p>Description: 健康检查套餐设定Tool</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMFeePackTool  extends TJDOTool{
	//初始化费用设定主档初始化SQL
	public static final String INIT_PACK_MAIN="SELECT * FROM HRM_PACKAGEM";
	//初始化费用设定细档初始化SQL
	public static final String INIT_PACK_DETAIL="SELECT * FROM HRM_PACKAGED WHERE PACKAGE_CODE ='#' ORDER BY SEQ";
	//取得集合医嘱细相

	public static final String QUERY_ORDERSET=
		"SELECT B.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE  OWN_PRICE,A.OWN_PRICE*B.DOSAGE_QTY TOT_OWN_PRICE," +
		"  B.DOSAGE_QTY,A.UNIT_CODE,A.OPTITEM_CODE,B.ORDERSET_CODE,A.GOODS_DESC," +
        "  A.SPECIFICATION,A.NHI_PRICE,A.CAT1_TYPE,A.ORDER_CAT1_CODE " + // modify by wanglong 20130423
		"FROM SYS_FEE A, SYS_ORDERSETDETAIL B  " +
		"WHERE B.ORDERSET_CODE='#' AND B.ORDER_CODE=A.ORDER_CODE";
	 /**
     * 实例
     */
    public static HRMFeePackTool instanceObject;
    /**
     * 得到实例
     * @return HRMFeePackTool
     */
    public static HRMFeePackTool getInstance()
    {
        if(instanceObject == null){
        	instanceObject = new HRMFeePackTool();
        }

        return instanceObject;
    }
    /**
     * 返回取号原则取出的模板号
     * @return
     */
    public String getNewPackCode(){
		String packCode = SystemTool.getInstance().getNo("ALL", "ODO", "PACKAGE_NO","PACKAGE_NO");
		return packCode;
    }
    
    /**
	 * 查询套餐信息
	 * @param roleType 角色类型(H_健康检查,PIC_一期临床)
	 * @return result 套餐信息
	 * @author wangb 2016/7/5
	 */
	public TParm selectHrmPackageByRoleType(String roleType) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT PACKAGE_CODE AS ID,PACKAGE_DESC AS NAME,ENNAME,PY1,PY2,ROLE_TYPE ");
		sbSql.append(" FROM HRM_PACKAGEM WHERE ACTIVE_FLG = 'Y' ");
		sbSql.append("  AND ROLE_TYPE IN ('");
		sbSql.append(roleType);
		sbSql.append("') ");
		sbSql.append(" ORDER BY PACKAGE_CODE  ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + " " + result.getErrText());
		}
		
		return result;
	}
}
