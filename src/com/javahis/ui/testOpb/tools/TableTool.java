package com.javahis.ui.testOpb.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.javahis.ui.testOpb.annotation.Column;
import com.javahis.ui.testOpb.annotation.PKey;

/**
 * TABLE����
 * @author zhangp
 *
 */
@SuppressWarnings("unchecked")
public class TableTool {

	private TTable t;
	private List list = new ArrayList();
	private List deleteList = new ArrayList();
	private Class<? extends Object> objClass;
	private String[] syncFieldsNames;
	private String[] mutiplyFieldsNames;

	/**
	 * 
	 * @param table
	 * @param syncFieldsNames	ͬ����
	 * @param mutiplyFieldsNames	���ͬ����
	 */
	public TableTool(TTable table, String[] syncFieldsNames, String[] mutiplyFieldsNames) {
		this.t = table;
		this.syncFieldsNames = syncFieldsNames;
		this.mutiplyFieldsNames = mutiplyFieldsNames;
	}

	/**
	 * ��Bean��List����
	 * @param list
	 * @throws Exception
	 */
	public void setList(List list) throws Exception {
		this.list = list;
		Object obj = this.list.get(0);
		objClass = obj.getClass();
	}
	
	/**
	 * ȡ��beanList
	 * @return
	 */
	public <T> List<T> getList() {
		return list;
	}
	
	/**
	 * ȡ��deleteList
	 * @return
	 */
	public <T> List<T> getDeleteList() {
		return deleteList;
	}

	/**
	 * table��ʾ
	 * @throws Exception
	 */
	public void show() throws Exception {
		t.acceptText();
		int selectRow = t.getSelectedRow();
		int selectCol = 0;
		if(selectRow >= 0 ){
			selectCol = t.getSelectedColumn();
		}
		TParm nullParm = new TParm();
		t.setParmValue(nullParm);
		for (int i = 0; i <= this.list.size(); i++) {
			t.addRow();
		}
		String pm = t.getParmMap();
		String[] pms = pm.split(";");
		for (int i = 0; i < this.list.size(); i++) {
			Object obj = this.list.get(i);
			Field[] fields = objClass.getDeclaredFields();
			for (int k = 0; k < fields.length; k++) {
				String name = fields[k].getName();
				for (int j = 0; j < pms.length; j++) {
					//System.out.println("hehe = "+name+ " " +pms[j]);
					if (name.equals(pms[j])) {
						t.setItem(i, j, transTypeForShow(fields[k].get(obj)));
					}
				}
			}
		}
		if(selectRow >= 0 ){
			t.setSelectedRow(selectRow);
			t.setSelectedColumn(selectCol);
		}
	}

	/**
	 * ����ҽ���ؼ��ش���ֵ��bean��ֵ
	 * @param <T>
	 * @param obj
	 * @param parm
	 * @return
	 * @throws Exception
	 */
	public <T> T onNew(Object obj, TParm parm) throws Exception {
		t.acceptText();
		String[] sb = parm.getNames();
		QueryTool queryTool = new QueryTool();
		Class<? extends Object> objClass = obj.getClass();
		Field[] fields = objClass.getDeclaredFields();
		String colName = "";
		for (int j = 0; j < fields.length; j++) {
			Annotation[] ans = fields[j].getAnnotations();
			for (int k = 0; k < ans.length; k++) {
				for (int j2 = 0; j2 < sb.length; j2++) {
					colName = sb[j2];
					if (ans[k] instanceof PKey) {
						PKey pk = fields[j].getAnnotation(PKey.class);
						if (pk.name().equals(colName)) {
							fields[j].set(obj, queryTool.getValue(ans[k],
									fields[j], parm.getData(colName)));
						}
					}
					if (ans[k] instanceof Column) {
						Column col = fields[j].getAnnotation(Column.class);
						if (col.name().equals(colName)) {
							fields[j].set(obj, queryTool.getValue(ans[k],
									fields[j], parm.getData(colName)));
						}
					}
				}
			}
		}
		Class<?> superClass = objClass.getSuperclass();
		Field field = superClass.getDeclaredField("modifyState");
		field.set(obj, Type.INSERT);
		return (T) obj;
	}
	
	/**
	 * ֵ�ı��¼�
	 * @param tNode
	 * @throws Exception
	 */
	public void changeValue(TTableNode tNode) throws Exception{
		int column = tNode.getColumn();
		String colName = tNode.getTable().getParmMap(column);
		int row = tNode.getRow();
		if(list.size() > row && row >= 0){
			Object obj = list.get(row);
			Field field = objClass.getDeclaredField(colName);
			Object lastValue = field.get(obj);
			field.set(obj, getValue(tNode.getValue(), field));
			list.set(row, obj);
			updateState(obj);
			syncListValue(lastValue);
		}
	}
	
	/**
	 * ɾ����
	 * @throws Exception
	 */
	public void deleteRow() throws Exception{
		t.acceptText();
		int index = t.getSelectedRow();
		Object obj = list.get(index);
		
		Class<?> superClass = objClass.getSuperclass();
		Field field = superClass.getDeclaredField("modifyState");
		int state = Integer.valueOf(""+field.get(obj));
		if(Type.INSERT != state){
			field.set(obj, Type.DELETE);
			deleteList.add(obj);
		}
		list.remove(index);
	}
	
	
	
	/**
	 * ͬ��
	 * @throws Exception
	 */
	private void syncListValue(Object lastValue) throws Exception{
		Field[] fields = objClass.getDeclaredFields();
		Class listClass = List.class;
		for (Field field : fields) {
			if(field.getType() == listClass){
				for (int i = 0; i < this.list.size(); i++) {
					Object obj = list.get(i);
					List list = (List) field.get(obj);
					List newList = new ArrayList();
					if(list.size() > 0 && list.get(0).getClass() == objClass){
						for (int j = 0; j < list.size(); j++) {
							Object obj2 = list.get(j);
							updateState(obj2);
							for (int j2 = 0; j2 < syncFieldsNames.length; j2++) {
								Field field2 = objClass.getDeclaredField(syncFieldsNames[j2]);
								field2.set(obj2, field2.get(obj));
							}
							for (int j2 = 0; j2 < mutiplyFieldsNames.length; j2++) {
								Field field2 = objClass.getDeclaredField(mutiplyFieldsNames[j2]);
								Class numClass = BigDecimal.class;
								if(field2.getType() == numClass){
									BigDecimal a = (BigDecimal) field2.get(obj);
									BigDecimal aa = (BigDecimal) lastValue;
									BigDecimal b = (BigDecimal) field2.get(obj2);
									if(b.compareTo(new BigDecimal(0)) == 0){
										field2.set(obj2, new BigDecimal(1));
										b = new BigDecimal(1);
									}
									MathContext mc = new MathContext(3, RoundingMode.HALF_DOWN);
									BigDecimal rate = a.divide(aa, mc);
									field2.set(obj2, b.multiply(rate));
								}
							}
							newList.add(obj2);
						}
					}
					field.set(obj, newList);
					this.list.set(i, obj);
				}
			}
		}
	}
	
	/**
	 * ȡֵ
	 * @param <T>
	 * @param obj
	 * @param field
	 * @return
	 * @throws Exception
	 */
	private <T> T getValue(Object obj, Field field) throws Exception{
		Class stringClass = String.class;
		Class numClass = BigDecimal.class;
		if(field.getType() == stringClass){
			return (T) obj;
		}
		if(field.getType() == numClass){
			return (T) new BigDecimal(""+obj);
		}
		return (T) obj;
	}
	
	/**
	 * ����������ת��Ϊtable����ʾ����������
	 * @param <T>
	 * @param obj
	 * @return
	 */
	private <T> T transTypeForShow(Object obj){
		Class numClass = BigDecimal.class;
		if(obj != null && obj.getClass() == numClass){
			return (T) Double.valueOf(""+obj);
		}else{
			return (T) obj;
		}
	}
	
	/**
	 * �޸�״̬
	 * @param obj
	 * @throws Exception
	 */
	private void updateState(Object obj) throws Exception{
		Class<?> superClass = objClass.getSuperclass();
		Field mField = superClass.getDeclaredField("modifyState");
		int state = Integer.valueOf(""+mField.get(obj));
		if(Type.INSERT != state){
			mField.set(obj, Type.UPDATE);
		}
	}
}
