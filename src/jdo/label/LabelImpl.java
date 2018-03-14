package jdo.label;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

public class LabelImpl implements LabelInf {

	public Map<String, Object> apRegion(List<Map<String, Object>> list,String url) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> apTag(List<Map<String, Object>> list,String url) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> checkRegionLabel(List<Map<String, Object>> list,String url) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getLabel(String labelNo,String url) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getRegionAP(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getRegionLabel(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> labelData(List<Map<String, Object>> list, String url) {
		Map<String ,Object> map = getMap(list, url);
		return map;
	}

	public Map<String, Object> sendResult() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Map<String, Object> getMap(List<Map<String, Object>>  list, String url) {
		String jsonParam = builderPostUrlData(list);
		String json = HttpClientUtil.post(url, jsonParam,list);
		if (StringUtils.isNotBlank(json)) {
			//json != null && !json.equals("")
			return ParseJson.jsonToMap(json);
		}
		return null;
	}

	
	protected String builderPostUrlData(List<Map<String, Object>> list) {
		Gson json = new Gson();
		String mapjson = json.toJson(list);
		StringBuilder sb = new StringBuilder("{\"data\":").append(mapjson)
				.append("}");
		return sb.toString();
	}

	public Map<String, Object> sendResult(String url) {
		// TODO Auto-generated method stub
		return null;
	}

}
