package com.javahis.ui.reg;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * JTable工具类
 * 
 * @author wangqing 20171023
 *
 */
public class TablePublicTool {
	/**
	 * 状态：增
	 */
	private static int STATUS_INSERT = 0;
	/**
	 * 状态：删
	 */
//	private static int STATUS_DELETE = 1;
	/**
	 * 状态：改
	 */
	private static int STATUS_UPDATE = 2;
	/**
	 * 状态：查
	 */
	private static int STATUS_SELECT = 3;
	

	/**
	 * 查
	 * @param table
	 * @param parm
	 */
	public static void setParmValue(TTable table, TParm parm){
		setParmValue(table, parm, STATUS_SELECT);	
	}
	
	/**
	 * 查
	 * @param table
	 * @param parm
	 * @param status
	 */
	public static void setParmValue(TTable table, TParm parm, int status){
		if(table == null) {
			System.out.println("table is null");
			return;
		}
		if(parm == null){
			System.out.println("parm is null");
			return;
		}
		if(parm.getCount()<0){
			parm.setCount(0);
		}
		int count = parm.getCount();
		for(int i=0; i<count; i++){
			parm.addData("#STATUS", status);
		}		
		table.setParmValue(parm);		
	}
	
	/**
	 * 增
	 * @param table
	 * @return
	 */
	public static int addRow(TTable table){
		if(table == null) {
			System.out.println("table is null");
			return -1;
		}
		TParm parm = table.getParmValue();
		if(parm == null){
			System.out.println("parm is null");
			return -1;
		}
		if(parm.getCount()<0){
			parm.setCount(0);
		}
		int row = table.addRow();
		parm.setData("#STATUS", row, STATUS_INSERT);
		return row;	
	}
	
	/**
	 * 删
	 * @param table
	 * @param row
	 * @param deleteParm
	 * @param pks
	 * @param pkValues
	 */
	public static void removeRow(TTable table, int row, TParm deleteParm, String[] pks, Object[] pkValues){
		if(table == null){
			System.out.println("table is null");
			return;
		}
		if(row<0){
			System.out.println("row<0");
			return;
		}
		if(pks == null){
			System.out.println("pks is null");
			return;
		}
		if(pkValues == null){
			System.out.println("pkValues is null");
			return;
		}
		TParm parm = table.getParmValue();
		if(parm == null){
			System.out.println("parm is null");
			return;
		}
		if(parm.getCount()<=0){
			System.out.println("parm.getCount()<=0");
			return;
		}
		if(deleteParm == null){
			System.out.println("deleteParm is null");
			return;
		}
		if(deleteParm.getCount()<0){
			deleteParm.setCount(0);	
		}	
		if(parm.getInt("#STATUS", row) != STATUS_INSERT){
			for(int i=0; i<pks.length; i++){
				deleteParm.addData(pks[i], pkValues[i]);
			}
			int count = deleteParm.getCount();	
			deleteParm.setCount(++count);
		}
		table.removeRow(row);
	}
	
	/**
	 * 改
	 * @param table
	 * @param row
	 */
	public static void modifyRow(TTable table, int row){
		if(table == null){
			System.out.println("table is null");
			return;
		}
		if(row<0){
			System.out.println("row<0");
			return;
		}
		TParm parm = table.getParmValue();
		if(parm == null){
			System.out.println("parmValue is null");
			return;
		}
		if(parm.getCount()<=0){
			System.out.println("parm.getCount()<=0");
			return;
		}
		if(parm.getInt("#STATUS", row) != STATUS_INSERT){
			parm.setData("#STATUS", row, STATUS_UPDATE);
		}	
	}
	
	/**
	 * 改
	 * @param table
	 * @param row
	 * @param col
	 * @param name
	 * @param oldValue
	 * @param newValue
	 */
	public static void modifyRow(TTable table, int row, int col, String name, Object oldValue, Object newValue){
		if(table == null){
			System.out.println("table is null");
			return;
		}
		if(row<0){
			System.out.println("row<0");
			return;
		}
		TParm parm = table.getParmValue();
		if(parm == null){
			System.out.println("parmValue is null");
			return;
		}
		if(parm.getCount()<=0){
			System.out.println("parm.getCount()<=0");
			return;
		}
		if(oldValue == newValue){
			return;
		}
		table.setValueAt(newValue, row, col);
		parm.setData(name, row, newValue);
		if(parm.getInt("#STATUS", row) == STATUS_INSERT){
			return;
		}
		parm.setData("#STATUS", row, STATUS_UPDATE);		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
