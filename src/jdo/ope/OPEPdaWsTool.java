package jdo.ope;

import javax.jws.WebService;
/**
 * <p>Title: ����PDA werbservice�ӿ�</p>
 *
 * <p>Description: ����PDA werbservice�ӿ�</p>
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
	
	/**����pda��ѯ�ӿ�*/
	public String getOP_Data(String mrNo,String dateStr,String roomNo);
	
	/**����pda����ӿ�*/
	public String saveOP_Data(String opeBookSeq,String roomNo, 
			String transferUser,String transfer_Date,
			String timeoutUser,String timeoutDate,
			String drConformFlg,String anaConformFlg,String type);
	/**��ȡ������Ϣ*/
	public String getHisRooms();
	/**��ȡ������������*/
	public String getOPType(String roomNo);
	/**���� ope_check���밲ȫ�˲��*/
	public String saveOpe_check(String str);
}
