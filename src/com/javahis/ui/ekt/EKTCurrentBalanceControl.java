package com.javahis.ui.ekt;

import jdo.ekt.EKTIO;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;


/**
 * <p>Title: 医疗卡余额查询</p>
 *
 * <p>Description: 医疗卡余额查询 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20111226
 * @version 1.0
 */
public class EKTCurrentBalanceControl extends TControl {
	
    private Pat pat; //病患信息
    private TParm parmEKT; //医疗卡信息
	public EKTCurrentBalanceControl() {
		
    }
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	
    }
    
    /**
     * 查询方法
     */
	public void onQuery(){
		String mrNo = ""+getValue("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		mrNo = pat.getMrNo();
		setValue("MR_NO", mrNo);
		StringBuilder sql = new StringBuilder();
		String sql1 = "SELECT A.MR_NO,A.NAME,CURRENT_BALANCE " +
				"FROM EKT_MASTER A , EKT_ISSUELOG B WHERE " +
				"A.CARD_NO = B.CARD_NO AND B.WRITE_FLG = 'Y'";
		sql.append(sql1);
		
		if(!mrNo.equals("")&& mrNo!=null){
			if (pat != null)
				PatTool.getInstance().unLockPat(pat.getMrNo());
			pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
			setValue("MR_NO", pat.getMrNo());
			String sql2 = " AND A.MR_NO = '"+pat.getMrNo()+"'";
			sql.append(sql2);
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        if (result.getErrCode() < 0) {
          messageBox(result.getErrText());
          return;
        }
        ((TTable)getComponent("TABLE")).setParmValue(result);
        
		
	}
	/**
	 * 清空
	 */
	public void onClear(){
		clearValue("MR_NO");
	}
	/**
	 * 读医疗卡
	 */
	public void onEKTcard(){
		parmEKT = EKTIO.getInstance().TXreadEKT();
	    if (null == parmEKT || parmEKT.getErrCode() < 0 ||
	    		parmEKT.getValue("MR_NO").length() <= 0) {
	    	this.messageBox(parmEKT.getErrText());
	    	parmEKT = null;
	    	return;
	       }
	    String mrno = parmEKT.getValue("MR_NO");
	    setValue("MR_NO", mrno);
	    onQuery();
	    
	}
	
}
