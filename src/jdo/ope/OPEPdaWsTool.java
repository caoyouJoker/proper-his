package jdo.ope;

import javax.jws.WebService;
/**
 * <p>Title: 手术PDA werbservice接口</p>
 *
 * <p>Description: 手术PDA werbservice接口</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author liuzhen 2012-6-27    
 * @version 4.0 
 */
@WebService
public interface OPEPdaWsTool {
	
	public String login(String userID,String password);
	
	/**手术pda查询接口*/
	public String getOP_Data(String mrNo,String dateStr,String roomNo);
	
	/**手术pda保存接口*/
	public String saveOP_Data(String opeBookSeq,String roomNo, 
			String transferUser,String transfer_Date,
			String timeoutUser,String timeoutDate,
			String drConformFlg,String anaConformFlg,String type);
	/**获取术间信息*/
	public String getHisRooms();
	/**获取术间手术类型*/
	public String getOPType(String roomNo);
	/**插入 ope_check介入安全核查表*/
	public String saveOpe_check(String str);
}
