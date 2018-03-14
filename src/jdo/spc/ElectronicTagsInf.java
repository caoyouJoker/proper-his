package jdo.spc;

import java.util.Map;


/**
 * <p>
 * Title: 电子标签接口
 * </p>
 *
 * <p>
 * Description: 电子标签接口
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company:BlueCore
 * </p>
 *
 * @author Yuanxm 2012.08.30
 * @version 1.0
 */
public interface ElectronicTagsInf {

	/**
	 * 服务状态
	 * 
	 * @return 返回Map 详细键值在国药天津公司电子标签软件设计方案
	 *       
	 */
	public Map<String, Object> findServerStatus();

	/**
	 * 用户登录
	 * 
	 * @param userId
	 *            用户名
	 * @param password
	 *            密码
	 * @return 返回MAP
	 */
	public Map<String, Object> login(String userId, String password);

	/**
	 * 用户查询
	 * 
	 * @param userId
	 *            用户名
	 * @return   返回MAP
	 */
	public Map<String, Object> findUser(String userId);

	/**
	 * 药房货位更新
	 * 
	 * @param map
	 * 
	 * @return   返回MAP
	 */
	public Map<String, Object> cargoUpdate(Map<String, Object> map);

	/**
	 * 药筐更新
	 * 
	 * @param map
	 * 
	 * @return    返回MAP
	 */
	public Map<String, Object> drugBasketUpdate(Map<String, Object> map);

	/**
	 * 药箱更新
	 * 
	 * @param map
	 * 
	 * @return   返回MAP
	 */
	public Map<String, Object> medicineChestUpdate(Map<String, Object> map);

	/**
	 * 药盒
	 * 
	 * @param map
	 * 
	 * @return   返回MAP
	 */
	public Map<String, Object> pcsUpdate(Map<String, Object> map);

	/**
	 * 获取标签
	 * 
	 * @param map
	 * 
	 * @return   返回MAP
	 */
	public Map<String, Object> getLable(Map<String, Object> map);

}
