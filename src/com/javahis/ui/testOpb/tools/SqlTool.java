package com.javahis.ui.testOpb.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.javahis.ui.testOpb.annotation.Column;
import com.javahis.ui.testOpb.annotation.PKey;
import com.javahis.ui.testOpb.annotation.Table;

/**
 * sql工具
 * 
 * @author zhangp
 * 
 */
public class SqlTool {

	private final String sqlSelecta = " SELECT ";
	private final String sqlSelectb = " FROM ";
	private final String sqlSelectc = " WHERE ";
	private final String sqlInserta = " INSERT INTO ";
	private final String sqlInsertb = " ) VALUES ( ";
	private final String sqlInsertc = " ) ";
	private final String sqlInsertd = " ( ";
	private final String sqlUpdatea = " UPDATE ";
	private final String sqlUpdateb = " SET ";
	private final String sqlUpdatec = " WHERE ";
	private final String sqlDeletea = " DELETE ";
	private final String sqlDeleteb = " WHERE ";

	private Object obj;
	private Class<? extends Object> objClass;
	private Field[] fields;
	private int modifyState;

	private static SqlTool instanceObject;
	
	public static SqlTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SqlTool();
        return instanceObject;
    }
	
	/**
	 * 构造
	 * 
	 * @param obj
	 * @throws Exception
	 */
	private void init(Object obj) throws Exception {
		this.obj = obj;
		this.objClass = obj.getClass();
		this.fields = objClass.getDeclaredFields();
		Class<?> superClass = objClass.getSuperclass();
		Field state = superClass.getDeclaredField("modifyState");
		modifyState = Integer.valueOf(""+state.get(obj));
	}

	/**
	 * 取得查询语句
	 *  
	 * @param obj
	 * @return
	 * @throws Exception 
	 */
	public String getSqlSelect(Object obj) throws Exception {
		init(obj);
		String a = "", b = "", c = "", sql = "";
		a += sqlSelecta;
		b += sqlSelectb;
		c += sqlSelectc;
		if (objClass.isAnnotationPresent(Table.class)) {
			Table table = (Table) objClass.getAnnotation(Table.class);
			b += table.tableName();
		}
		for (int i = 0; i < fields.length; i++) {
			Annotation[] ans = fields[i].getAnnotations();
			for (int j = 0; j < ans.length; j++) {
				if(ans[j] instanceof PKey){
					PKey pk = fields[i].getAnnotation(PKey.class);
					a += pk.name() + ",";
				}
				if(ans[j] instanceof Column){
					Column col = fields[i].getAnnotation(Column.class);
					a += col.name() + ",";
				}
				if (ans[j] instanceof PKey
						&& (fields[i].get(obj).toString().length() > 0 && !fields[i].get(obj).toString().equals("0"))) {
					PKey pk = fields[i].getAnnotation(PKey.class);
					c += pk.name() + " = " + getValueStr(ans[j], fields[i]) +" AND ";
				}
			}
		}
		sql = a.substring(0, a.length() - 1)
				+ b + c.substring(0, c.length() - 4);
		return sql;
	}

	/**
	 * 取得插入语句
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String getSqlInsert() throws Exception {
		String a = "", b = "", sql = "";
		a += sqlInserta;
		b += sqlInsertb;
		if (objClass.isAnnotationPresent(Table.class)) {
			Table table = (Table) objClass.getAnnotation(Table.class);
			a += table.tableName() + sqlInsertd;
		}
		for (int i = 0; i < fields.length; i++) {
			Annotation[] ans = fields[i].getAnnotations();
			for (int j = 0; j < ans.length; j++) {
				if(ans[j] instanceof PKey){
					PKey pk = fields[i].getAnnotation(PKey.class);
					a += pk.name() + ",";
					b += getValueStr(ans[j], fields[i]) + ",";
				}
				if(ans[j] instanceof Column){
					Column col = fields[i].getAnnotation(Column.class);
					a += col.name() + ",";
					b += getValueStr(ans[j], fields[i]) + ",";
				}
			}
		}
		sql = a.substring(0, a.length() - 1) + b.substring(0, b.length() - 1) + sqlInsertc;
		return sql;
	}
	
	/**
	 * 取得更新语句
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String getSqlUpdate() throws Exception {
		String a = "", b = "", c = "", sql = "";
		a += sqlUpdatea;
		b += sqlUpdateb;
		c += sqlUpdatec;
		if (objClass.isAnnotationPresent(Table.class)) {
			Table table = (Table) objClass.getAnnotation(Table.class);
			a += table.tableName();
		}
		for (int i = 0; i < fields.length; i++) {
			Annotation[] ans = fields[i].getAnnotations();
			for (int j = 0; j < ans.length; j++) {
				if(ans[j] instanceof PKey){
					PKey pk = fields[i].getAnnotation(PKey.class);
					c += pk.name() + " = " + getValueStr(ans[j], fields[i]) + " AND ";
				}
				if(ans[j] instanceof Column){
					Column col = fields[i].getAnnotation(Column.class);
					b += col.name() + " = " + getValueStr(ans[j], fields[i]) + ",";
				}
			}
		}
		sql = a + b.substring(0, b.length() - 1) + c.substring(0, c.length() - 4);
		return sql;
	}
	
	/**
	 * 取得删除语句
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String getSqlDelete() throws Exception {
		String a = "", b = "", sql = "";
		a += sqlDeletea;
		b += sqlDeleteb;
		if (objClass.isAnnotationPresent(Table.class)) {
			Table table = (Table) objClass.getAnnotation(Table.class);
			a += table.tableName();
		}
		for (int i = 0; i < fields.length; i++) {
			Annotation[] ans = fields[i].getAnnotations();
			for (int j = 0; j < ans.length; j++) {
				if(ans[j] instanceof PKey){
					PKey pk = fields[i].getAnnotation(PKey.class);
					b += pk.name() + " = " + getValueStr(ans[j], fields[i]) + " AND ";
				}
			}
		}
		sql = a + b.substring(0, b.length() - 4);
		return sql;
	}

	/**
	 * 取得值在sql中的字符串
	 * @param annotation
	 * @param field
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private String getValueStr(Annotation annotation, Field field) throws IllegalArgumentException, IllegalAccessException {
		if(annotation instanceof PKey){
			PKey pk = field.getAnnotation(PKey.class);
			if(pk.type() == Type.CHAR){
				return "'" + (field.get(obj) == null ? "" : field.get(obj)) + "'";
			}
			if(pk.type() == Type.NUM){
				return "" + (field.get(obj) == null ? 0 : field.get(obj));
			}
			if(pk.type() == Type.DATE){
				return "TO_DATE('" + (field.get(obj) == null ? "" : field.get(obj)) + "','YYYYMMDDHH24MISS')";
			}
		}
		if(annotation instanceof Column){
			Column col = field.getAnnotation(Column.class);
			if(col.type() == Type.CHAR){
				return "'" + (field.get(obj) == null ? "" : field.get(obj)) + "'";
			}
			if(col.type() == Type.NUM){
				return "" + (field.get(obj) == null ? 0 : field.get(obj));
			}
			if(col.type() == Type.DATE){
				return "TO_DATE('" + (field.get(obj) == null ? "" : field.get(obj)) + "','YYYYMMDDHH24MISS')";
			}
		}
		return "";
	}
	
	/**
	 * 取sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public String getSql(Object obj) throws Exception{
		String sql = "";
		init(obj);
		switch (modifyState) {
		case Type.INSERT:
			sql = this.getSqlInsert();
			break;
		case Type.UPDATE:
			sql = this.getSqlUpdate();
			break;
		case Type.DELETE:
			sql = this.getSqlDelete();
			break;
		}
		return sql;
	}
}
