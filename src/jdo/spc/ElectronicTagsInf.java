package jdo.spc;

import java.util.Map;


/**
 * <p>
 * Title: 鐢靛瓙鏍囩鎺ュ彛
 * </p>
 *
 * <p>
 * Description: 鐢靛瓙鏍囩鎺ュ彛
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: ProperSoft
 * </p>
 *
 * @author Yuanxm 2012.08.30
 * @version 1.0
 */
public interface ElectronicTagsInf {

	/**
	 * 鏈嶅姟鐘舵��
	 * 
	 * @return 杩斿洖Map 璇︾粏閿�煎湪鍥借嵂澶╂触鍏徃鐢靛瓙鏍囩杞欢璁捐鏂规
	 *       
	 */
	public Map<String, Object> findServerStatus();

	/**
	 * 鐢ㄦ埛鐧诲綍
	 * 
	 * @param userId
	 *            鐢ㄦ埛鍚�
	 * @param password
	 *            瀵嗙爜
	 * @return 杩斿洖MAP
	 */
	public Map<String, Object> login(String userId, String password);

	/**
	 * 鐢ㄦ埛鏌ヨ
	 * 
	 * @param userId
	 *            鐢ㄦ埛鍚�
	 * @return   杩斿洖MAP
	 */
	public Map<String, Object> findUser(String userId);

	/**
	 * 鑽埧璐т綅鏇存柊
	 * 
	 * @param map
	 * 
	 * @return   杩斿洖MAP
	 */
	public Map<String, Object> cargoUpdate(Map<String, Object> map);

	/**
	 * 鑽瓙鏇存柊
	 * 
	 * @param map
	 * 
	 * @return    杩斿洖MAP
	 */
	public Map<String, Object> drugBasketUpdate(Map<String, Object> map);

	/**
	 * 鑽鏇存柊
	 * 
	 * @param map
	 * 
	 * @return   杩斿洖MAP
	 */
	public Map<String, Object> medicineChestUpdate(Map<String, Object> map);

	/**
	 * 鑽洅
	 * 
	 * @param map
	 * 
	 * @return   杩斿洖MAP
	 */
	public Map<String, Object> pcsUpdate(Map<String, Object> map);

	/**
	 * 鑾峰彇鏍囩
	 * 
	 * @param map
	 * 
	 * @return   杩斿洖MAP
	 */
	public Map<String, Object> getLable(Map<String, Object> map);

}
