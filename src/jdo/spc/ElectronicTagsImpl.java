package jdo.spc;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

/**
 * <p>
 * Title: 电子标签接口实现类
 * </p>
 *
 * <p>
 * Description: 药筐绑定Tool
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
public class ElectronicTagsImpl implements ElectronicTagsInf {

	@Override
	public Map<String, Object> findUser(String userId) {
		if(StringUtils.isBlank(userId)){
			return null;
		}
		String json = HttpClientUtil.get(Constant.FIND_USER_URL+userId);
		return ParseXml.parserToMap(json);
	}

	@Override
	public Map<String, Object> findServerStatus() {
		String json = HttpClientUtil.get(Constant.SERVER_STATUS_URL);
		return ParseXml.parserToMap(json);
	}

	@Override
	public Map<String, Object> login(String userId,String password) {
		if(StringUtils.isBlank(userId) || StringUtils.isBlank(password)){
			return null;
		}
		String json = HttpClientUtil.get(Constant.LOGIN_URL+userId+"/"+password);
		return ParseXml.parserToMap(json);
	}

	public Map<String, Object> cargoUpdate(Map<String, Object> map) {
		Map<String, Object> returnMap = getMap(map, Constant.CARGO_URL);
		return returnMap;
	}

	public Map<String, Object> drugBasketUpdate(Map<String, Object> map) {
		Map<String, Object> returnMap = getMap(map, Constant.DRUGBASKET_URL);
		return returnMap;
	}

	public Map<String, Object> medicineChestUpdate(Map<String, Object> map) {
		Map<String, Object> returnMap = getMap(map, Constant.MEDICINECHEST_URL);
		return returnMap;
	}

	public Map<String, Object> pcsUpdate(Map<String, Object> map) {
		Map<String, Object> returnMap = getMap(map, Constant.PCS_URL);
		return returnMap;
	}

	public Map<String, Object> getLable(Map<String, Object> map) {
		String json = builderJson(map);
		String xml = HttpClientUtil.post(Constant.LABLE_URL, json);
		if (StringUtils.isNotBlank(xml)) {
			return ParseXml.parserToMap(xml);
		}
		return null;
	}
	 

 
	private Map<String, Object> getMap(Map<String, Object> map, String url) {
		String jsonParam = builderPostUrlData(map);
		String xml = HttpClientUtil.post(url, jsonParam);
		if (StringUtils.isNotBlank(xml)) {
			return ParseXml.parserToMap(xml);
		}
		return null;
	}

	
	
	protected String builderPostUrlData(Map<String, Object> map) {
		Gson json = new Gson();
		String mapjson = json.toJson(map);
		StringBuilder sb = new StringBuilder("{\"data\":").append(mapjson)
				.append(" }");
		return sb.toString();
	}
	
	protected String builderJson(Map<String, Object> map){
		Gson json = new Gson();
		String mapjson = json.toJson(map);
		return mapjson;
	}

	
}
