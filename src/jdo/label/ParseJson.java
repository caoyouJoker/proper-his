package jdo.label;

import java.util.Map;

import net.sf.json.JSONObject;

public class ParseJson {

	/**
	 * ½âÎöJSONÎªMAP
	 * @param json
	 * @return
	 */
	public static Map<String, Object> jsonToMap(String json) {
		JSONObject jasonObject = JSONObject.fromObject(json);
		Map<String, Object> map = (Map) jasonObject;
		return map;
	}
}
