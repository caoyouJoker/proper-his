package jdo.bil;


import java.util.List;

import javax.jws.WebService;
/**
 * <p>Title: ���ü�¼�Ʒ� werbservice�ӿ�</p>
 *
 * <p>Description:���ü�¼�Ʒ�  werbservice�ӿ�</p>
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
	
	
		
	//סԺ�Ʒѷ���  ����ֵΪ SEQ_NO,CASE_NO_SEQ ��дSPC_INV_RECORD��		
	public String insertIBSOrder(String inString1,String inString2,String inStringM);
	
	//����Ʒѷ���
	public String insertOpdOrder(String inString);
	
	//��ò�����Ϣ
	public String onMrNo(String mr_no,String adm_type);
	
	//��ò��˼Ʒ���Ϣ
	public String onFeeData(String inString);
	
	//���Ʒ�״̬
    public boolean onCheckFeeState(String inString1,String inString2);//wanglong add 20141014
	
}
