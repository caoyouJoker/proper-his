package jdo.bil;

import java.util.ArrayList;

import javax.jws.WebService;
/**
 * <p>
 * Title: PDA werbservice接口
 * </p>
 * 
 * <p>
 * Description: PDA werbservice接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author caowl 2012-6-27
 * @version 4.0
 */
@WebService
public interface BILPDAWsTool {

	//PDA耗用记录保存
	public String onSavePda(String BarCode,String mr_no,String case_no,String qty,String userId,String deptCode ,String stationCode,String regionCode ,String optTerm,String patName,String deptCodeOfPat,String OP_ROOM);
	
	
	
	//PDA耗用记录查询	
	public String onQueryPda(String mrNo,String caseNo,String optUser,String deptCode,String stationCode,String regionCode,String optTerm,String flg);

	
	//PDA病案号回车查询
	public String onMrNo(String mr_no,String adm_type);
	
	//扫条码查询信息
	//public String queryByBarcode(String mr_no,String case_no,String bar_code,String optUser,String deptCode,String stationCode,String regionCode,String optTerm);
	
	//扫条码查询信息
	public String queryByBarcode(String mr_no,String case_no,String bar_code,String optUser,String deptCode,String stationCode,String regionCode,String optTerm,String patName,String deptCodeOfPat,String packBarCode,String packDesc,String packGroupNo);
	
	//PDA用户登录界面
	public String onLogin(String user_id,String password);

	//PDA取得用户的区域，主科室，主病区
	public String onQueryOptUser(String user_id);
	
	/**手术交接查询接口*/
	public String getOP_Data(String mrNo,String dateStr);
	
	/**手术交接保存接口*/
	public String saveOP_Data(String opeBookSeq,String roomNo, 
			String transferUser,String transfer_Date,
			String timeoutUser,String timeoutDate,
			String drConformFlg,String anaConformFlg);
	
	/**获取术间信息*/
	public String getHisRooms();
	  
	/**接受确认(查询)*/  
	public String getInvPack(String barCode);
	  
	  
	/**接受确认(保存)*/    
	public String saveInvPack(String res);    
	
	/**术中使用(保存)*/  
	public String saveInvPackCheck(String BarCode);   
	
	/**术前清点(查询)*/
	public String queryOpeCheckBf(String bar);
	  
	/**关前 术后清点(查询)*/
	public String queryOpeCheckOth(String bar,String statues);
	
	/**术前  关前 术后清点(校验)*/
	public String queryOpeCheckBfCon(String bar, String seq,String statues);
	
	/**术前(插入) */
	public String saveOpeCheckBf(String bar,String statues);
	/**关前(更新)  术后(更新) 保存*/
	public String saveOpeCheckOth(String bar,String statues);
	/**查询病患*/
	public String queryPat(String room);
	
	/**审核人 1,2 更新*/
	public String saveCheckUser(String bar,
			String checkuser1 ,String checkuser2,String ip);
	  
	/**术间解除绑定*/
	public String updateRoom(String room,String ip);  
	

}
