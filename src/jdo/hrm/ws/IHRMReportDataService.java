package jdo.hrm.ws;

import javax.jws.WebService;

/**
 * <p>
 * Title: �ܼ챨������App�����ӿ�
 * </p>
 * 
 * <p>
 * Description: �ܼ챨������App�����ӿ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wangb 2017.2.16
 * @version 1.0
 */
@WebService
public interface IHRMReportDataService {
	
	/**
	 * ����ָ��֤����ȡ���ܼ챨������
	 * 
	 * @param userName �û���
	 * @param password ����
	 * @param idNo ֤����
	 * @param startDate ��ѯ��ʼ����(YYYY/MM/DD)
	 * @return �ܼ챨������
	 */
	public String getReportDataByIdNo(String userName, String password, String idNo, String startDate);
	
	/**
	 * ����ָ���绰����ȡ���ܼ챨������
	 * 
	 * @param userName �û���
	 * @param password ����
	 * @param tel �绰����
	 * @param startDate ��ѯ��ʼ����(YYYY/MM/DD)
	 * @return �ܼ챨������
	 */
	public String getReportDataByTel(String userName, String password, String tel, String startDate);
	
	/**
	 * ������ֹʱ��ȡ���ܼ챨������(����)
	 * 
	 * @param userName �û���
	 * @param password ����
	 * @param startDate ��ѯ��ʼ����(YYYY/MM/DD)
	 * @param endDate ��ѯ��ֹ����(YYYY/MM/DD)
	 * @return �ܼ챨������
	 */
	public String getReportDataByDate(String userName, String password, String startDate, String endDate);

}
