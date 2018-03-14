package com.javahis.ui.inv;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

public class INVStockQueryControl extends TControl{
	
	private TTable table;
	//===========排序功能==================add by wanglong 20121212
    private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	
	/**
	 * 初始化
	 */
	public void init(){
		super.init();
		initPage();

	}

	private void initPage() {
		//获得TABLE对象
        table=(	TTable)this.getComponent("TABLE");
         
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// 设置弹出菜单
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// 定义接受返回值方法
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		
	}
	
	/**
     * 得到TextField对象
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC").setValue(order_desc);
    }
    
    /**
     * 查询
     */
    public void onQuery(){
    	String sql=" SELECT A.INV_CODE,C.ORG_DESC, B.INV_CHN_DESC, B.DESCRIPTION, " +
    			"  SUM (A.STOCK_QTY) QTY,D.SUP_CHN_DESC " +
    			"  FROM INV_STOCKM A, INV_BASE B,INV_ORG C ,SYS_SUPPLIER D" +
    			"  WHERE A.INV_CODE = B.INV_CODE " +
    			"  AND  A.ORG_CODE = C.ORG_CODE" +
    			"  AND B.UP_SUP_CODE=D.SUP_CODE "+
    			"  AND A.STOCK_QTY>0";
    			
    	if(this.getValueString("INV_CODE")!=null&&!"".equals(this.getValueString("INV_CODE"))){
    		sql += " AND A.INV_CODE='"+this.getValueString("INV_CODE")+"'";
    	}
    	if(this.getValueString("ORG_CODE")!=null&&!"".equals(this.getValueString("ORG_CODE"))){
    		sql += " AND A.ORG_CODE='"+this.getValueString("ORG_CODE")+"'";
    	}
    	
    	sql +=" GROUP BY A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION,D.SUP_CHN_DESC,C.ORG_DESC";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()<0){
    		this.messageBox("没有要查询的数据");
    		this.onClear() ;
    		return;
    	}
    	table.setParmValue(parm);
    }
    /**
     * 打印
     */
    public void onPrint(){
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	if(tableParm.getCount()<0){
    		this.messageBox("没有要打印的数据");
    	}
    	Timestamp datetime = SystemTool.getInstance().getDate();
    	TParm data = new TParm();
    	data.setData("TITLE", "TEXT", "物资库存统计单");
    	data.setData("USER", "TEXT", "制表人："+Operator.getName());
    	data.setData("DATE", "TEXT", "制表日期："+datetime.toString().substring(0, 10).replace('-', '/'));
    	
    	TParm parm = new TParm();
    	for(int i=0;i<tableParm.getCount();i++){
    		parm.addData("ORG_DESC", tableParm.getValue("ORG_DESC", i));
    		parm.addData("INV_CODE", tableParm.getValue("INV_CODE", i));
    		parm.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i));
    		parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i));
    		parm.addData("QTY", tableParm.getValue("QTY", i));
    		parm.addData("MAN_CHN_DESC", tableParm.getValue("SUP_CHN_DESC", i));
    	}
    	
    	parm.setCount(parm.getCount("INV_CODE"));  
    	 parm.addData("SYSTEM", "COLUMNS", "ORG_DESC");
        parm.addData("SYSTEM", "COLUMNS", "INV_CODE");
        parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "MAN_CHN_DESC");
        
        data.setData("TABLE",parm.getData());
       	
        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVStockQueryPrint.jhw", data);
    	
    }
    

    /**
     * 清空方法
     */
	
	public void onClear() {
		String clearString ="INV_CODE;INV_DESC;ORG_CODE"; 	
        this.clearValue(clearString);
        table.removeRowAll();
	}
	  
	// ====================排序功能begin======================add by wanglong 20121212
	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 返回指定列在列名数组中的index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * 根据列名数据，将Vector转成Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================排序功能end======================
   

	

}
