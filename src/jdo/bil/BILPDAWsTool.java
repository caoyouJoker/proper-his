package jdo.bil;

import java.util.ArrayList;

import javax.jws.WebService;
/**
 * <p>
 * Title: PDA werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Description: PDA werbservice�ӿ�
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

	//PDA���ü�¼����
	public String onSavePda(String BarCode,String mr_no,String case_no,String qty,String userId,String deptCode ,String stationCode,String regionCode ,String optTerm,String patName,String deptCodeOfPat,String OP_ROOM);
	
	
	
	//PDA���ü�¼��ѯ	
	public String onQueryPda(String mrNo,String caseNo,String optUser,String deptCode,String stationCode,String regionCode,String optTerm,String flg);

	
	//PDA�����Żس���ѯ
	public String onMrNo(String mr_no,String adm_type);
	
	//ɨ�����ѯ��Ϣ
	//public String queryByBarcode(String mr_no,String case_no,String bar_code,String optUser,String deptCode,String stationCode,String regionCode,String optTerm);
	
	//ɨ�����ѯ��Ϣ
	public String queryByBarcode(String mr_no,String case_no,String bar_code,String optUser,String deptCode,String stationCode,String regionCode,String optTerm,String patName,String deptCodeOfPat,String packBarCode,String packDesc,String packGroupNo);
	
	//PDA�û���¼����
	public String onLogin(String user_id,String password);

	//PDAȡ���û������������ң�������
	public String onQueryOptUser(String user_id);
	
	/**�������Ӳ�ѯ�ӿ�*/
	public String getOP_Data(String mrNo,String dateStr);
	
	/**�������ӱ���ӿ�*/
	public String saveOP_Data(String opeBookSeq,String roomNo, 
			String transferUser,String transfer_Date,
			String timeoutUser,String timeoutDate,
			String drConformFlg,String anaConformFlg);
	
	/**��ȡ������Ϣ*/
	public String getHisRooms();
	  
	/**����ȷ��(��ѯ)*/  
	public String getInvPack(String barCode);
	  
	  
	/**����ȷ��(����)*/    
	public String saveInvPack(String res);    
	
	/**����ʹ��(����)*/  
	public String saveInvPackCheck(String BarCode);   
	
	/**��ǰ���(��ѯ)*/
	public String queryOpeCheckBf(String bar);
	  
	/**��ǰ �������(��ѯ)*/
	public String queryOpeCheckOth(String bar,String statues);
	
	/**��ǰ  ��ǰ �������(У��)*/
	public String queryOpeCheckBfCon(String bar, String seq,String statues);
	
	/**��ǰ(����) */
	public String saveOpeCheckBf(String bar,String statues);
	/**��ǰ(����)  ����(����) ����*/
	public String saveOpeCheckOth(String bar,String statues);
	/**��ѯ����*/
	public String queryPat(String room);
	
	/**����� 1,2 ����*/
	public String saveCheckUser(String bar,
			String checkuser1 ,String checkuser2,String ip);
	  
	/**��������*/
	public String updateRoom(String room,String ip);  
	

}
