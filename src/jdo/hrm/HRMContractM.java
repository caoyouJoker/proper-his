package jdo.hrm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
*
* <p>Title: 健康检查合同主对象</p>
*
* <p>Description: 健康检查合同主对象</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMContractM extends TDataStore{
	//初始化SQL
	private static final String INIT="SELECT * FROM HRM_CONTRACTM ORDER BY CONTRACT_CODE";
	private String id=Operator.getID();
	private String ip=Operator.getIP();
	private String companyDesc;
	private String roleType; // add by wangb 2016/06/22 角色类型(H_健康检查,PIC_一期临床)
	
	/**
	 * 查询事件
	 * @return
	 */
	public int onQuery(){
		this.setSQL(INIT);
		return this.retrieve();
	}
	/**
	 * 过滤
	 * @param companyCode String
	 * @return boolean
	 */
	public boolean filt(String companyCode){
		if(StringUtil.isNullString(companyCode)){
			return false;
		}
		this.setFilter("COMPANY_CODE='" +companyCode+ "'");
		return this.filter();
	}
	/**
	 * 插入一行数据
	 * @param comCode
	 * @return
	 */
	public int insertRow(String comCode){
		int row=this.insertRow();
		String contractCode = SystemTool.getInstance().getNo("ALL", "ODO", "CONTRACTNO","CONTRACTNO");
		
		this.setItem(row, "COMPANY_CODE", comCode);
		this.setItem(row, "CONTRACT_CODE", contractCode);
		//$$=== add by lx 2012-05-19 加入折扣率==== $$//
		this.setItem(row, "DISCNT", 1.0);
		//$$=== add by lx 2012-05-19 加入折扣率==== $$//
		this.setItem(row, "OPT_USER", id);
		this.setItem(row, "OPT_TERM", ip);
		this.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
		// add by wangb 2016/06/22 新增角色类型用于区分健检和一期临床
		this.setItem(row, "ROLE_TYPE", roleType);
		this.setActive(row,false);
		return row;
	}
	public void setCompanyDesc(String desc){
		companyDesc=desc;
	}
	/**
	 * 得到其他列数据
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
//		// System.out.println("getOtherColumnValue.parm="+parm);
		if("COMPANY_DESC".equalsIgnoreCase(column)){
			return companyDesc;
		}
		if("DISCNT_P".equalsIgnoreCase(column)){
			return StringTool.round(this.getItemDouble(row, "DISCNT")*100.00,2);
		}
		if("CP_PAY_P".equalsIgnoreCase(column)){
			return StringTool.round(this.getItemDouble(row, "CP_PAY")*100.00,2);
		}
		return "";
	}
	/**
	 * 根据给入的团体代码删除该团体的合同
	 * @param companyCode
	 * @return
	 */
	public boolean deleteContract(String companyCode){
		this.filt(companyCode);
		int count=this.rowCount();
		for(int i=count-1;i>-1;i--){
			this.deleteRow(i);
		}
		return true;
	}
	
    /**
     * 查询合同的套餐原价和套餐价
     * @param companyCode
     * @return
     */
    public TParm onQueryAmt(String companyCode) {// add by wanglong 20130502
        String sql =
                "SELECT A.COMPANY_CODE,A.CONTRACT_CODE,SUM(B.DISPENSE_QTY*B.ORIGINAL_PRICE) SUBTOTAL,SUM(B.DISPENSE_QTY*B.PACKAGE_PRICE) TOT_AMT "
                        + "  FROM HRM_CONTRACTD A, HRM_PACKAGED B "
                        + " WHERE A.PACKAGE_CODE = B.PACKAGE_CODE "
                        + "   AND A.COMPANY_CODE = '#' "
                        + "GROUP BY A.COMPANY_CODE, A.CONTRACT_CODE";
        sql = sql.replaceFirst("#", companyCode);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * 更新合同的套餐原价和套餐价
     * @param companyCode
     * @return
     */
    public TParm updateAmt(String companyCode) {// add by wanglong 20130502
        String sql1 =
                "UPDATE HRM_CONTRACTM A SET SUBTOTAL = (SELECT SUM(D.DISPENSE_QTY * D.ORIGINAL_PRICE) SUBTOTAL "
                        + "                               FROM HRM_CONTRACTD C, HRM_PACKAGED D "
                        + "                              WHERE C.PACKAGE_CODE = D.PACKAGE_CODE "
                        + "                                AND A.COMPANY_CODE = C.COMPANY_CODE "
                        + "                                AND A.CONTRACT_CODE = C.CONTRACT_CODE "
                        + "                                AND A.COMPANY_CODE = '#' "
                        + "                           GROUP BY A.COMPANY_CODE, A.CONTRACT_CODE)";
        sql1 = sql1.replaceFirst("#", companyCode);
        String sql2 =
                "UPDATE HRM_CONTRACTM A SET SUBTOTAL = (SELECT SUM(D.DISPENSE_QTY * D.ORIGINAL_PRICE) SUBTOTAL "
                        + "                               FROM HRM_CONTRACTD C, HRM_PACKAGED D "
                        + "                              WHERE C.PACKAGE_CODE = D.PACKAGE_CODE "
                        + "                                AND A.COMPANY_CODE = C.COMPANY_CODE "
                        + "                                AND A.CONTRACT_CODE = C.CONTRACT_CODE "
                        + "                                AND A.COMPANY_CODE = '#' "
                        + "                           GROUP BY A.COMPANY_CODE, A.CONTRACT_CODE)";
        sql2 = sql2.replaceFirst("#", companyCode);
        TParm result = new TParm(TJDODBTool.getInstance().update(sql1));
        if (result.getErrCode() != 0) {
            return result;
        }
        result = new TParm(TJDODBTool.getInstance().update(sql2));
        return result;
    }
    
    /**
     * 设置角色类型
     * 
     * @param roleType 角色类型
     */
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	
}
