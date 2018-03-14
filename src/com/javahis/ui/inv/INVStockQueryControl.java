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
	//===========������==================add by wanglong 20121212
    private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	
	/**
	 * ��ʼ��
	 */
	public void init(){
		super.init();
		initPage();

	}

	private void initPage() {
		//���TABLE����
        table=(	TTable)this.getComponent("TABLE");
         
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		
	}
	
	/**
     * �õ�TextField����
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    /**
     * ���ܷ���ֵ����
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
     * ��ѯ
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
    		this.messageBox("û��Ҫ��ѯ������");
    		this.onClear() ;
    		return;
    	}
    	table.setParmValue(parm);
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	if(tableParm.getCount()<0){
    		this.messageBox("û��Ҫ��ӡ������");
    	}
    	Timestamp datetime = SystemTool.getInstance().getDate();
    	TParm data = new TParm();
    	data.setData("TITLE", "TEXT", "���ʿ��ͳ�Ƶ�");
    	data.setData("USER", "TEXT", "�Ʊ��ˣ�"+Operator.getName());
    	data.setData("DATE", "TEXT", "�Ʊ����ڣ�"+datetime.toString().substring(0, 10).replace('-', '/'));
    	
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
     * ��շ���
     */
	
	public void onClear() {
		String clearString ="INV_CODE;INV_DESC;ORG_CODE"; 	
        this.clearValue(clearString);
        table.removeRowAll();
	}
	  
	// ====================������begin======================add by wanglong 20121212
	/**
	 * �����������������
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
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
	 * ����ָ���������������е�index
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
	 * �����������ݣ���Vectorת��Parm
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
	// ====================������end======================
   

	

}
