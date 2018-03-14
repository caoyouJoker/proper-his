package com.javahis.ui.sys.orm.ext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import com.javahis.ui.sys.orm.tools.QueryTool;


/**
 *
 * @author whaosoft
 *
 */
public class QueryToolExt {


	/**
	 *
	 * @param <T>
	 * @param sql
	 * @param obj
	 * @return
	 */
	public <T> List<T> queryBySql(String sql, Object obj) {

		try {
			return QueryTool.getInstance().queryBySql(sql, obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param sql
	 * @param obj
	 * @return
	 */
	public <T> List<T> queryBySql(String sql, Class<T> clazz) {

		try {
			return QueryTool.getInstance().queryBySql(sql, Class.forName(clazz.getName()).newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param sql
	 * @param obj
	 * @return
	 */
	public <T> T queryOneBySql(String sql, Class<T> clazz) {

		try {
			 List<T> list = QueryTool.getInstance().queryBySql(sql,Class.forName(clazz.getName()).newInstance());
			 if( null!=list && list.size()>0 ){
				 return list.get(0);
			 }
			 else{
				 return null;
			 }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param annotation
	 * @param field
	 * @param object
	 * @return
	 */
	public <T> T getValue(Annotation annotation, Field field, Object object){

		try {
			return QueryTool.getInstance().getValue(annotation, field, object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @param <T>
	 * @param a
	 * @param b
	 * @return
	 */
	public <T> T synClasses(Object a, Object b){

		try {
			return QueryTool.getInstance().synClasses(a, b);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}



	/**********  **********/

	/**
	 *
	 * @return
	 */
	static public QueryToolExt getInstance(){
		return QueryToolExtSub.qt;
	}

	/** */
	private QueryToolExt(){}

	/** */
	static private class QueryToolExtSub{
		static private QueryToolExt qt = new QueryToolExt();
	}

}
