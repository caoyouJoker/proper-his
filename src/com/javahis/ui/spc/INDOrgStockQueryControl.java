package com.javahis.ui.spc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.spc.INDSQL;
import jdo.spc.IndStockDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 部门库存查询
 * </p>
 *
 * <p>
 * Description: 部门库存查询
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDOrgStockQueryControl
    extends TControl {
    public INDOrgStockQueryControl() {
    }

    private TTable table_m;    

	private int sortColumn = -1;
	private boolean ascending = false;
	private BILComparator compare = new BILComparator();

	
    /**
     * 初始化方法
     */
    public void onInit() {
        // 显示全院药库部门
        if (!this.getPopedem("deptAll")) {
            //((TextFormatINDOrg)this.getComponent("ORG_CODE")).o
            //getTextFormat("ORG_CODE")
//            if (parm.getCount("NAME") > 0) {
//                getComboBox("ORG_CODE").setSelectedIndex(1);
//            }
        }
        setValue("ACTIVE_FLG", "Y");
    	setValue("STOP_FLG", "N");    
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
                
        this.table_m = this.getTable("TABLE");
//		addListener(table_m);
		
    }

    /**
     * 查询方法
     */
    public void onQuery() {
//        if ("".equals(this.getValueString("ORG_CODE"))) {
//            this.messageBox("查询部门不能为空");
//            return;
//        }
        this.getTable("TABLE").removeRowAll();
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }
        if (!"".equals(this.getValueString("BATCH_NO"))) {
            parm.setData("BATCH_NO", this.getValueString("BATCH_NO"));
        }
/*        if (!"".equals(this.getValueString("MAN_CODE"))) {
            parm.setData("MATERIAL_LOC_CODE", this.getValueString("MAN_CODE"));
        }*/
        if (!"".equals(this.getValueString("TYPE_CODE"))) {
            parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
        }
        if ("Y".equals(this.getValueString("SAFE_QTY"))) {
            parm.setData("SAFE_QTY", "SAFE_QTY");
        }
        if ("Y".equals(this.getValueString("CHECK_NOT_ZERO"))) {
            parm.setData("STOCK_QTY", "CHECK_NOT_ZERO");
        }

        if ("Y".equals(this.getValueString("ORDER_TYPE_W")) &&
            "Y".equals(this.getValueString("ORDER_TYPE_C")) &&
            "Y".equals(this.getValueString("ORDER_TYPE_G"))) {
            parm.setData("PHA_TYPE", "('W', 'C', 'G')");
        }
        if ("Y".equals(this.getValueString("ACTIVE_FLG"))){
        	parm.setData("ACTIVE_FLG",'Y');
        }   
        if  ("Y".equals(this.getValueString("STOP_FLG"))){
        	parm.setData("ACTIVE_FLG",'N');
        }
        if (((TRadioButton) this.getComponent("RB_SUP")).isSelected()){//wanglong add 20150130
            parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));//供应厂商
        }
//        if (((TRadioButton) this.getComponent("RB_MAT")).isSelected()){//wanglong add 20150130
//            parm.setData("TYPE_CODE", "04");//PHA_BASE.TYPE_CODE='04' 卫耗材 
//        }
        String mjFlg = this.getValueString("MJ_FLG");
        parm.setData("MJ_FLG",mjFlg);		
        TParm result = new TParm();
        if (!"Y".equals(this.getValueString("SHOW_BATCH"))) {
          /*  result = IndStockDTool.getInstance().onQueryOrgStockQueryNotBatch(
                parm);*/
        	//修改 查询 低于最低库存量 by liyh 20120808
        	//result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQL(parm)));
        	if("Y".equals(mjFlg)) {
        		TParm indOrgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByCode(getValueString("ORG_CODE"))));
        		if(indOrgParm.getCount() > 0 ){
        			String orgType = indOrgParm.getValue("ORG_TYPE",0);
        			parm.setData("ORG_TYPE",orgType);
        		}
        		
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQLNew(parm)));
        	}else {
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQL(parm)));
        	}	
        }
        else {		        
           // result = IndStockDTool.getInstance().onQueryOrgStockQuery(parm);
        	//显示批号效期
        	if("Y".equals(mjFlg)) {
        		TParm indOrgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByCode(getValueString("ORG_CODE"))));
        		if(indOrgParm.getCount() > 0 ){
        			String orgType = indOrgParm.getValue("ORG_TYPE",0);
        			parm.setData("ORG_TYPE",orgType);
        		}
        		 
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockDrugQuery(parm)));
        	}else {
        		result = IndStockDTool.getInstance().onQueryOrgStockQuery(parm);
        	}
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
//        Map map = new HashMap();
//        map.put("01", "西药");
//        map.put("02", "中成药");
//        map.put("03", "中草药");
//        map.put("04", "消毒剂");
//        map.put("05", "输液");
//        map.put("06", "制剂");
//        map.put("07", "试剂");
//        map.put("08", "原料");
        double stock_amt = 0;
        double own_amt = 0;
        for (int i = result.getCount() - 1; i >= 0; i--) {
        	 if ("N".equals(this.getValueString("ORDER_TYPE_W")) &&
                     "W".equals(result.getValue("PHA_TYPE", i))&&!"Y".equals(result.getValue("CTRL_FLG", i))) {							
                     result.removeRow(i);
                     continue;
             }
            if ("N".equals(this.getValueString("ORDER_TYPE_C")) &&
                "C".equals(result.getValue("PHA_TYPE", i))) {
                result.removeRow(i);
                continue;
            }
            if ("N".equals(this.getValueString("ORDER_TYPE_G")) &&
                "G".equals(result.getValue("PHA_TYPE", i))) {
                result.removeRow(i);
                continue;
            }
//            result.setData("TYPE_CODE", i,
//                           map.get(result.getData("TYPE_CODE", i)));
            stock_amt += result.getDouble("STOCK_AMT", i);
            own_amt += result.getDouble("OWN_AMT", i);
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        this.getTable("TABLE").setParmValue(new TParm());
        this.getTable("TABLE").setParmValue(result);
        this.setValue("STOCK_AMT", StringTool.round(stock_amt, 2));
        this.setValue("OWN_AMT", StringTool.round(own_amt, 2));
        this.setValue("DIFF_AMT", StringTool.round(own_amt - stock_amt, 2));
        this.setValue("SUM_TOT", this.getTable("TABLE").getRowCount());
    }

    /**
     * 清空方法
     */
    public void onClear() {
    	  this.getTable("TABLE").removeRowAll();
        String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC;BATCH_NO;TYPE_CODE;"
            + "SAFE_QTY;CHECK_NOT_ZERO;SHOW_BATCH;MAN_CODE;STOCK_AMT;OWN_AMT;"
            + "DIFF_AMT;SUM_TOT";
        this.clearValue(clearStr);
//        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode("");
//        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
        this.setValue("ORDER_TYPE_W", "Y");
        this.setValue("ORDER_TYPE_C", "Y");
        this.setValue("ORDER_TYPE_G", "Y");
//        this.callFunction("UI|RB_ALL|setSelected", true);//wanglong add 20150130
//        this.callFunction("UI|SUP_CODE|setEnabled", false);
        
       
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        TTable table = getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "药品库存统计表");
        date.setData("ORG_CODE", "TEXT",
                     "统计部门: " +
                     this.getTextFormat("ORG_CODE").getText());
        date.setData("DATE", "TEXT",
                     "统计时间: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "制表人: " + Operator.getName());
        // 表格数据
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION", i));
            parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
            parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
            parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
            parm.addData("MATERIAL_CHN_DESC",
                         tableParm.getValue("MATERIAL_CHN_DESC", i));
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        parm.addData("SYSTEM", "COLUMNS", "MATERIAL_CHN_DESC");
        date.setData("TABLE", parm.getData());
        // 表尾数据
        date.setData("TOT", "TEXT", "合计： "+getValueDouble("OWN_AMT"));
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDOrgStockQuery.jhw",
                             date);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        TTable table = this.getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "部门库存查询");
    }

    /**
     * 部门选择事件
     */
//    public void onOrgAction() {
//        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode(
//            TypeTool.getString(getTextFormat("ORG_CODE").getValue()));
//        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
//        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setSelectedIndex(
//            0);
//    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }


    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) { 
		table.getTable().
			getTableHeader().
				addMouseListener(
					new MouseAdapter() {
							public void mouseClicked(MouseEvent mouseevent) {
									int i = table.getTable().columnAtPoint(mouseevent.getPoint());
									int j = table.getTable().convertColumnIndexToModel(i);
									if (j == sortColumn) {
										ascending = !ascending;
									} else {
										ascending = true;
										sortColumn = j;
									}
															
									TParm tableData = table.getParmValue();// 表格中parm值一致,// 1.取paramw值;
									// 2.转成 vector列名, 行vector ;
									String columnName[] = tableData.getNames("Data");
									String strNames = "";
									for (String tmp : columnName) {
										strNames += tmp + ";";
									}
									strNames = strNames.substring(0, strNames.length() - 1);
									Vector vct = getVector(tableData, "Data", strNames, 0);
			
									String tblColumnName = table.getParmMap(sortColumn);// 3.根据点击的列,对vector排序// 表格排序的列名;
									
									int col = tranParmColIndex(columnName, tblColumnName);// 转成parm中的列
									compare.setDes(ascending);
									compare.setCol(col);
									java.util.Collections.sort(vct, compare);
									// 将排序后的vector转成parm;
									// 为了使此方法通用，所以要求cloneVectoryParam()也得通用，不能仅限于deptFeeTable
									cloneVectoryParam(vct, new TParm(), strNames, table);
								}
							}
				);
	}
	
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" })
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
	 * @param columnName
	 * @param tblColumnName
	 * @return
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
	 * vectory转成param
	 */
	@SuppressWarnings("rawtypes")
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		// 行数据->列
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
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
	
    /**
     * 麻精点击事件			
     */
    public void onClick() {
    	//String mjFlg = this.getValueString("MJ_FLG");
    	/*if("Y".equals(mjFlg)) {
    		this.setValue("ORDER_TYPE_W", "N");
    		this.setValue("ORDER_TYPE_C", "N");
    		this.setValue("ORDER_TYPE_G", "N");
    	}else {
    		this.setValue("ORDER_TYPE_W", "Y");
    		this.setValue("ORDER_TYPE_C", "Y");
    		this.setValue("ORDER_TYPE_G", "Y");
    	}*/
    	
    	String flg = this.getValueString("MJ_FLG");
    	/*if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}*/
    }
                          
    public void onClickW() {
    	String flg = this.getValueString("ORDER_TYPE_W");
    	/*if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}*/
    }
    
    public void onClickC() {
    	String flg = this.getValueString("ORDER_TYPE_C");
    	/*if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}*/
    }
    
    public void onClickG() {			
    	String flg = this.getValueString("ORDER_TYPE_G");
    	/*if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}*/
    }

    /**
     * 单选
     */
    public void onChooseRadio() {// wanglong add 20150130
        if (((TRadioButton) this.getComponent("RB_SUP")).isSelected()) {
            this.callFunction("UI|SUP_CODE|setEnabled", true);
        } else {
            this.callFunction("UI|SUP_CODE|setEnabled", false);
        }
    }

}
