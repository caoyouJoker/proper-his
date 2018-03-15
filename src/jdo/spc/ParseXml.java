package jdo.spc;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * <p>
 * Title: 杩滅▼璁块棶瑙ｆ瀽杈呭姪
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
public class ParseXml {

	/**
	 * 瑙ｆ瀽 XML 涓篗ap
	 * @param xml
	 * @return
	 */
	public static Map<String, Object> xmlToMap(String xml) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
			Element rootElement = document.getRootElement();
			List<Element> childList = rootElement.elements();
			for (Element it : childList) {
				map.put(it.getName(), it.getTextTrim());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return map;

	}

	/**
	 * 瑙ｆ瀽json 锟�?Map
	 * @param json
	 * @return
	 */
	/**
	public static Map<String, Object> jsonToMap(String json) {
		if(StringUtils.isNotBlank(json)){
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			Map<String, Object> map = gson.fromJson(json, type);
			return map;
		}
		
		

		return null;
	}*/
	

	public static Map parserToMap(String s){
		Map map=new HashMap();
		JSONObject json= JSONObject.fromObject(s);
		Iterator keys=json.keys();
		while(keys.hasNext()){
			String key=(String) keys.next();
			String value=json.get(key).toString();
			if(value.startsWith("{")&&value.endsWith("}")){
				map.put(key, parserToMap(value));
			}else{
				map.put(key, value);
			}

		}
		return map;
	}
	
	private static HashMap<String, Object> parse() {
		Date d = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", "1");
		map.put("key2", 2);
		map.put("key3", d);
		map.put("key4", 'c');
		Gson gson = new Gson();

		String json = gson.toJson(map);
		System.out.println("json=======:"+json);
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> map2 = gson.fromJson("{\"Status\":\"10000\",\"ReturnObject\":\"\",\"Message\":\"鍙戯拷?鎴愬姛\"}", type);
		return (HashMap<String, Object>) map2;
	}

	public  static void main(String[] args) {
		HashMap<String, Object> map = parse();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
					.next();
			System.out.println(entry.getKey() + "===================="
					+ entry.getValue());
		}
	}

}
