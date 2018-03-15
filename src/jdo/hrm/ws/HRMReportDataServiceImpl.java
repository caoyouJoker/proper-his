package jdo.hrm.ws;

import javax.jws.WebService;

import jdo.hrm.HRMReportDataTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: �ܼ챨������App�����ӿ�ʵ����
 * </p>
 * 
 * <p>
 * Description: �ܼ챨������App�����ӿ�ʵ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangb 2017.2.16
 * @version 1.0
 */
@WebService
public class HRMReportDataServiceImpl implements IHRMReportDataService {

	@Override
	public String getReportDataByIdNo(String userName, String password,
			String idNo, String startDate) {
		if (StringUtils.isEmpty(idNo) || StringUtils.isEmpty(startDate)) {
			return "��ѯ��������Ϊ��";
		}
		

		// У������
		String checkResult = this.checkCode(userName, password);
		if (checkResult.length() > 0) {
			return checkResult;
		}

		TParm parm = new TParm();
		parm.setData("QUERY_START_DATE", startDate);
		parm.setData("IDNO", idNo);

		// �����챨������
		TParm result = HRMReportDataTool.getInstance().getReportData(parm);

		if (result.getErrCode() < 0) {
			return result.getErrText();
		} else if (result.getCount("CASE_NO") < 1) {
			return "��������";
		} else {
			// ��ȡ�õ��ܼ챨��������װΪxml��ʽ����
			return HRMReportDataTool.getInstance().createReportDataXML(result);
		}
	}

	@Override
	public String getReportDataByTel(String userName, String password,
			String tel, String startDate) {
		if (StringUtils.isEmpty(tel) || StringUtils.isEmpty(startDate)) {
			return "��ѯ��������Ϊ��";
		}

		// У������
		String checkResult = this.checkCode(userName, password);
		if (checkResult.length() > 0) {
			return checkResult;
		}

		TParm parm = new TParm();
		parm.setData("QUERY_START_DATE", startDate);
		parm.setData("TEL", tel);

		// �����챨������
		TParm result = HRMReportDataTool.getInstance().getReportData(parm);

		if (result.getErrCode() < 0) {
			return result.getErrText();
		} else if (result.getCount("CASE_NO") < 1) {
			return "��������";
		} else {
			// ��ȡ�õ��ܼ챨��������װΪxml��ʽ����
			return HRMReportDataTool.getInstance().createReportDataXML(result);
		}
	}

	@Override
	public String getReportDataByDate(String userName, String password,
			String startDate, String endDate) {
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			return "��ѯ��������Ϊ��";
		}

		// У������
		String checkResult = this.checkCode(userName, password);
		if (checkResult.length() > 0) {
			return checkResult;
		}

		TParm parm = new TParm();
		parm.setData("QUERY_START_DATE", startDate);
		parm.setData("QUERY_END_DATE", endDate);

		// �����챨������
		TParm result = HRMReportDataTool.getInstance().getReportData(parm);

		if (result.getErrCode() < 0) {
			return result.getErrText();
		} else if (result.getCount("CASE_NO") < 1) {
			return "��������";
		} else {
			// ��ȡ�õ��ܼ챨��������װΪxml��ʽ����
			return HRMReportDataTool.getInstance().createReportDataXML(result);
		}
	}

	/**
	 * У������
	 * 
	 * @param code
	 *            �û���
	 * @param password
	 *            ����
	 * @return У����
	 */
	private String checkCode(String code, String password) {
		if (StringUtils.isEmpty(code)) {
			return "ERR:�û�������Ϊ��";
		}

		if (StringUtils.isEmpty(password)) {
			return "ERR:���벻��Ϊ��";
		}
		String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			return "ERR:���û�������";
		}
		if (!password.equals(parm.getValue("PASSWORD", 0))) {
			return "ERR:�������";
		}
		return "";
	}
}
