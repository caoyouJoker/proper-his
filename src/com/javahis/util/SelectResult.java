package com.javahis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.dongyang.data.TParm;
/**
*
* <p>Title: SQL查询结果TParm公用</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: </p>
*
* @author kevin
* @version 1.0
*/
public class SelectResult {
	
	int count;//数据笔数
	Vector<String> columns;//字段名
	HashMap<String,Vector> data = new HashMap<String,Vector>();
	
	int errorCode;
	String errorText;
	
	/**
	 * 由TParm建构SQL查询结果
	 * @param result TParm
	 */
	public SelectResult(TParm result){
		count = result.getData("ACTION","COUNT")==null?0:result.getInt("ACTION","COUNT");
		//count = result.getData("SYSTEM","COUNT")==null?0:result.getInt("SYSTEM","COUNT");
		errorCode = result.getData(TParm.ERR_GROUP,"Code")==null?0:result.getInt(TParm.ERR_GROUP,"Code");
		errorText = result.getData(TParm.ERR_GROUP,"Text")==null?"":(String) result.getData(TParm.ERR_GROUP,"Text");		
		data = (HashMap<String, Vector>) result.getGroupData(TParm.DEFAULT_GROUP);
		if(result.getData("SYSTEM","COLUMNS")!=null){
			columns = (Vector<String>) result.getData("SYSTEM","COLUMNS");
		}else{
			columns = new Vector<String>();
			if(data != null){
				Set keyset = data.keySet();
				Object[] oa = keyset.toArray();
				for(int i=0;i<oa.length;i++){
					columns.add((String)oa[i]);
				}
			}
		}
	}
	
	public SelectResult(){	
	}
	
	
	public int size(){		
		return count;
	}
	
	/**
	 * 将SQL查询结果组成TParm
	 * @return
	 */
	public TParm getTParm(){
		TParm result = new TParm();
		result.setDataN("ACTION","COUNT", count);
		result.setDataN("SYSTEM","COUNT", count);
		result.setDataN("SYSTEM","COLUMNS", columns);
		result.setDataN(TParm.ERR_GROUP,"Code", errorCode);
		result.setDataN(TParm.ERR_GROUP,"Text", errorText);
		result.setGroupData(TParm.DEFAULT_GROUP, data);		
		return result;
	}
	
	/**
	 * 取得与自身相同结构之空SQL查询结果
	 * @return
	 */
	public SelectResult getEmptyResult(){
		SelectResult result = new SelectResult();
		result.count = 0;
		result.columns = (Vector<String>) columns.clone();
		result.data = getEmptyData();
		return result;
	}
	
	/**
	 * 取得空结果数据
	 * @return
	 */
	public HashMap<String,Vector> getEmptyData(){
		HashMap<String,Vector> result = new HashMap<String,Vector>();
		for(int j=0;j<columns.size();j++){
			result.put(columns.get(j), new Vector());
		}
		return result;
	}
	
	/**
	 * 取得所有行数据
	 * @param i
	 * @return
	 */
	public Vector<Vector<Object>> getAllRow(){
		Vector<Vector<Object>> result = new Vector<Vector<Object>>();
		for(int i=0;i<count;i++){
			Vector<Object> row = new Vector<Object>();
			for(int j=0;j<columns.size();j++){
				Object o = data.get(columns.get(j)).get(i);
				row.add(o);
			}
			result.add(row);
		}
		return result;
	}
	
	/**
	 * 取得某行数据
	 * @param i
	 * @return
	 */
	public Vector<Object> getRow(int i){
		Vector<Object> result = new Vector<Object>();
		for(int j=0;j<columns.size();j++){
			Object o = data.get(columns.get(j)).get(i);
			result.add(o);
		}
		return result;
	}
	
	/**
	 * 加入某行数据
	 * @param row
	 */
	public void addRow(Vector<Object> row){
		if(row.size()==columns.size()){
			for(int j=0;j<columns.size();j++){
				data.get(columns.get(j)).add(row.get(j));
			}
			count = count + 1;
		}
	}
	
	/**
	 * 删除某行数据
	 * @param rowNum
	 */
	public void removeRow(int rowNum){
		if(count>rowNum){
			HashMap<String,Vector> data2 = getEmptyData();
			for(int i=0;i<count;i++){
				if(rowNum!=i){
					for(int j=0;j<columns.size();j++){
						Object o = data.get(columns.get(j)).get(i);
						data2.get(columns.get(j)).add(o);
					}
				}else{
					
				}
			}
			data = data2;
			count = count - 1;
		}
	}
	
	/**
	 * 取得取得某行数据的字段值
	 * @param i
	 * @param fieldName
	 * @return
	 */
	public Object getRowField(int i, String fieldName){
		Vector datav = data.get(fieldName);
		Object result = datav==null?null:datav.get(i);
		return result;
	}

}
