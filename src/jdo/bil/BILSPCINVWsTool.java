package jdo.bil;


import java.util.List;

import javax.jws.WebService;
/**
 * <p>Title: 耗用记录计费 werbservice接口</p>
 *
 * <p>Description:耗用记录计费  werbservice接口</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author caowl 2013-7-31
 * @version 4.0
 */
@WebService
public interface BILSPCINVWsTool {
	
	
		
	//住院计费方法  返回值为 SEQ_NO,CASE_NO_SEQ 回写SPC_INV_RECORD表		
	public String insertIBSOrder(String inString1,String inString2,String inStringM);
	
	//门诊计费方法
	public String insertOpdOrder(String inString);
	
	//获得病人信息
	public String onMrNo(String mr_no,String adm_type);
	
	//获得病人计费信息
	public String onFeeData(String inString);
	
	//检查计费状态
    public boolean onCheckFeeState(String inString1,String inString2);//wanglong add 20141014
	
}
