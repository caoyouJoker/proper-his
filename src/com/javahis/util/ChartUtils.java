/**
 * @className ChartUtils.java 
 * @author litong
 * @Date 2013-1-24 
 * @version V 1.0 
 */
package com.javahis.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import com.alibaba.fastjson.JSON;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * @author litong
 * @Date 2013-1-24 
 */
public class ChartUtils {
	/**
	 * @param table TTable对象
	 * @param sDate 开始日期或按月统计那个日期
	 * @param eDate 结束日期  如果按月传入“”即可
	 * @param title 模块名称
	 */
	@SuppressWarnings("unchecked")
	public static void name(TTable table,String sDate,String eDate,String title) {
		TParm parm=table.getShowParmValue();
    	List<Map<String, String>> lt= new ArrayList<Map<String,String>>();
    		Map map=parm.getData();
    		String nameArray[] = StringTool.parseLine(table.getParmMap(), ";");
    		Map<String, Vector<String>> map2=(Map<String, Vector<String>>)map.get("Data");
    		for (int i = 0; i < table.getRowCount(); i++) {
    			Map<String, String> mainMap=new HashMap<String, String>();
    			for (int j = 0; j < nameArray.length; j++) {
    				mainMap.put(nameArray[j], map2.get(nameArray[j]).get(i));
				}
    			lt.add(mainMap);
			}
    		JsonModule jsonModule=new JsonModule();
    		jsonModule.setTitle(getHead(table));
    		jsonModule.setList(lt);
    		
    		  if("".equals(eDate)){
    			  jsonModule.setKind("0");
    			  jsonModule.setDate(sDate);
    	        }
    	        else {
    	        	jsonModule.setKind("1");
    	        	 jsonModule.setSdate(sDate);
    	        	 jsonModule.setEdate(eDate);
    	        }	
    		  jsonModule.setName(title);
    	String cString=	JSON.toJSONString(jsonModule);
    	String fileName=UUID.randomUUID().toString();
    	File file = new File("////C://"+fileName+".txt");
    	
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter resultFile = null;
		try {
			resultFile = new FileWriter(file);
			PrintWriter myNewFile = new PrintWriter(resultFile);
			myNewFile.println(cString);
			resultFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Runtime rt = Runtime.getRuntime();
    	
    	try {
			Process proc = rt.exec("C:\\Program Files\\Internet Explorer\\iexplore.exe http://127.0.0.1:8080/web/FlexServlet?fileName="+fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		
    		
		
	}
	   private static Map getHead(TTable table) {
	    	String[] tStrings=table.getHeader().split(";"); 
	    	String nameArray[] = StringTool.parseLine(table.getParmMap(), ";");
	    	Map<String,String> map=new HashMap<String, String>();
	    	for (int i = 0; i < tStrings.length; i++) {
	    		map.put(nameArray[i], tStrings[i].split(",")[0]);
			}
			return map;
		}

}
