package jdo.hrm.ws;

import javax.jws.WebService;

import jdo.hrm.HRMReportDataTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 总检报告数据App交互接口实现类
 * </p>
 * 
 * <p>
 * Description: 总检报告数据App交互接口实现类
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
			return "查询参数不能为空";
		}
		

		// 校验密码
		String checkResult = this.checkCode(userName, password);
		if (checkResult.length() > 0) {
			return checkResult;
		}

		TParm parm = new TParm();
		parm.setData("QUERY_START_DATE", startDate);
		parm.setData("IDNO", idNo);

		// 获得体检报告数据
		TParm result = HRMReportDataTool.getInstance().getReportData(parm);

		if (result.getErrCode() < 0) {
			return result.getErrText();
		} else if (result.getCount("CASE_NO") < 1) {
			return "查无数据";
		} else {
			// 将取得的总检报告内容组装为xml格式数据
			return HRMReportDataTool.getInstance().createReportDataXML(result);
		}
	}

	@Override
	public String getReportDataByTel(String userName, String password,
			String tel, String startDate) {
		if (StringUtils.isEmpty(tel) || StringUtils.isEmpty(startDate)) {
			return "查询参数不能为空";
		}

		// 校验密码
		String checkResult = this.checkCode(userName, password);
		if (checkResult.length() > 0) {
			return checkResult;
		}

		TParm parm = new TParm();
		parm.setData("QUERY_START_DATE", startDate);
		parm.setData("TEL", tel);

		// 获得体检报告数据
		TParm result = HRMReportDataTool.getInstance().getReportData(parm);

		if (result.getErrCode() < 0) {
			return result.getErrText();
		} else if (result.getCount("CASE_NO") < 1) {
			return "查无数据";
		} else {
			// 将取得的总检报告内容组装为xml格式数据
			return HRMReportDataTool.getInstance().createReportDataXML(result);
		}
	}

	@Override
	public String getReportDataByDate(String userName, String password,
			String startDate, String endDate) {
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			return "查询参数不能为空";
		}

		// 校验密码
		String checkResult = this.checkCode(userName, password);
		if (checkResult.length() > 0) {
			return checkResult;
		}

		TParm parm = new TParm();
		parm.setData("QUERY_START_DATE", startDate);
		parm.setData("QUERY_END_DATE", endDate);

		// 获得体检报告数据
		TParm result = HRMReportDataTool.getInstance().getReportData(parm);

		if (result.getErrCode() < 0) {
			return result.getErrText();
		} else if (result.getCount("CASE_NO") < 1) {
			return "查无数据";
		} else {
			// 将取得的总检报告内容组装为xml格式数据
			return HRMReportDataTool.getInstance().createReportDataXML(result);
		}
	}

	/**
	 * 校验密码
	 * 
	 * @param code
	 *            用户名
	 * @param password
	 *            密码
	 * @return 校验结果
	 */
	private String checkCode(String code, String password) {
		if (StringUtils.isEmpty(code)) {
			return "ERR:用户名不能为空";
		}

		if (StringUtils.isEmpty(password)) {
			return "ERR:密码不能为空";
		}
		String sql = "SELECT * FROM SYS_IO_INF WHERE IO_CODE='" + code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			return "ERR:该用户不存在";
		}
		if (!password.equals(parm.getValue("PASSWORD", 0))) {
			return "ERR:密码错误";
		}
		return "";
	}
}
