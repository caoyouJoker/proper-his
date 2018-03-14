package com.javahis.ui.sys.orm.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.ui.sys.orm.annotation.PKey;
import com.javahis.ui.sys.orm.annotation.Column;;


/**
 * 查询工具
 * @author zhangp
 *
 */
@SuppressWarnings("unchecked")
public class QueryTool {

	private static QueryTool instanceObject;

	public static QueryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new QueryTool();
        return instanceObject;
    }

	/**
	 * sql查询
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> queryBySql(String sql, Object obj) throws Exception{

		Class<? extends Object> objClass = obj.getClass();
		Field[] fields = objClass.getDeclaredFields();

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Map<String, Vector> map = result.getGroupData("SYSTEM");
		Vector<String> v = map.get("COLUMNS");

		List l = new ArrayList();
		TParm rowParm;
		for (int i = 0; i < result.getCount(); i++) {
			obj = Class.forName(objClass.getName()).newInstance();
			rowParm = result.getRow(i);
			String colName = "";
			for (int j = 0; j < fields.length; j++) {
				Annotation[] ans = fields[j].getAnnotations();
				for (int k = 0; k < ans.length; k++) {
					for (int j2 = 0; j2 < v.size(); j2++) {
						colName = v.get(j2);
						if(ans[k] instanceof PKey){
							PKey pk = fields[j].getAnnotation(PKey.class);
							if(pk.name().equals(colName)){
								fields[j].set(obj, getValue(ans[k], fields[j], rowParm.getData(colName)));
							}
						}
						if(ans[k] instanceof Column){
							Column col = fields[j].getAnnotation(Column.class);
							if(col.name().equals(colName)){
								fields[j].set(obj, getValue(ans[k], fields[j], rowParm.getData(colName)));
							}
						}
					}
				}
			}
			l.add(obj);
		}
		return l;
	}

	public <T> T getValue(Annotation annotation, Field field, Object object){
		if(annotation instanceof PKey){
			PKey pk = field.getAnnotation(PKey.class);
			if(pk.type() == Type.CHAR){
				if(object == null){
					return null;
				}else{
					return (T) (""+object);
				}
			}
			if(pk.type() == Type.NUM){
				if(object == null){
					return null;
				}else{
					return (T) (BigDecimal.valueOf(Double.valueOf(""+object)));
				}
			}
			if(pk.type() == Type.DATE){
				if(object == null){
					return null;
				}else{
					String d = ""+object;
					d = d.substring(0, 4)+d.substring(5, 7)+d.substring(8, 10)+d.substring(11, 13)+d.substring(14, 16)+d.substring(17, 19);
					return (T) d;
				}
			}
		}
		if(annotation instanceof Column){
			Column col = field.getAnnotation(Column.class);
			if(col.type() == Type.CHAR){
				if(object == null){
					return null;
				}else{
					return (T) (""+object);
				}
			}
			if(col.type() == Type.NUM){
				if(object == null){
					return null;
				}else{
					return (T) (BigDecimal.valueOf(Double.valueOf(""+object)));
				}
			}
			if(col.type() == Type.DATE){
				if(object == null){
					return null;
				}else{
					String d = ""+object;
					d = d.substring(0, 4)+d.substring(5, 7)+d.substring(8, 10)+d.substring(11, 13)+d.substring(14, 16)+d.substring(17, 19);
					return (T) d;
				}
			}
		}
		return null;
	}

	/**
	 * 将a中的属性赋值到b，规则为属性名称相同
	 * @param <T>
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public <T> T synClasses(Object a, Object b) throws Exception{
		Class<? extends Object> aClass = a.getClass();
		Class<? extends Object> bClass = b.getClass();
		Field[] aFields = aClass.getDeclaredFields();
		Field[] bFields = bClass.getDeclaredFields();

		String aName, bName;
		for (Field aField : aFields) {
			aName = aField.getName();
			for (Field bField : bFields) {
				bName = bField.getName();
				if(aName.equals(bName)){
					bField.set(b, aField.get(a));
				}
			}
		}
		return (T) b;
	}

}
