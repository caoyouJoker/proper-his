package jdo.hrm.ws;

import javax.jws.WebService;

/**
 * <p>
 * Title: 总检报告数据App交互接口
 * </p>
 * 
 * <p>
 * Description: 总检报告数据App交互接口
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
	 * 根据指定证件号取得总检报告数据
	 * 
	 * @param userName 用户名
	 * @param password 密码
	 * @param idNo 证件号
	 * @param startDate 查询开始日期(YYYY/MM/DD)
	 * @return 总检报告数据
	 */
	public String getReportDataByIdNo(String userName, String password, String idNo, String startDate);
	
	/**
	 * 根据指定电话号码取得总检报告数据
	 * 
	 * @param userName 用户名
	 * @param password 密码
	 * @param tel 电话号码
	 * @param startDate 查询开始日期(YYYY/MM/DD)
	 * @return 总检报告数据
	 */
	public String getReportDataByTel(String userName, String password, String tel, String startDate);
	
	/**
	 * 根据起止时间取得总检报告数据(多人)
	 * 
	 * @param userName 用户名
	 * @param password 密码
	 * @param startDate 查询开始日期(YYYY/MM/DD)
	 * @param endDate 查询截止日期(YYYY/MM/DD)
	 * @return 总检报告数据
	 */
	public String getReportDataByDate(String userName, String password, String startDate, String endDate);

}
