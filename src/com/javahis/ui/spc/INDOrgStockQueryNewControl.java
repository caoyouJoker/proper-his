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
 * Title: ���ſ���ѯ
 * </p>
 *
 * <p>
 * Description: ���ſ���ѯ
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
public class INDOrgStockQueryNewControl
    extends TControl {
    public INDOrgStockQueryNewControl() {
    }

    private TTable table_m;    

	private int sortColumn = -1;
	private boolean ascending = false;
	private BILComparator compare = new BILComparator();

	
    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʾȫԺҩ�ⲿ��
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
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
                
        this.table_m = this.getTable("TABLE");
//		addListener(table_m);
		
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
//        if ("".equals(this.getValueString("ORG_CODE"))) {
//            this.messageBox("��ѯ���Ų���Ϊ��");
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
        parm.setData("SUP_CODE_NEW",this.getValueString("SUP_CODE_NEW"));
//        if (((TRadioButton) this.getComponent("RB_SUP")).isSelected()){//wanglong add 20150130
            parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));//��Ӧ����
//        }
//        if (((TRadioButton) this.getComponent("RB_MAT")).isSelected()){//wanglong add 20150130
//            parm.setData("TYPE_CODE", "04");//PHA_BASE.TYPE_CODE='04' ���Ĳ� 
//        }
        String mjFlg = this.getValueString("MJ_FLG");
        parm.setData("MJ_FLG",mjFlg);		
        TParm result = new TParm();
        if (!"Y".equals(this.getValueString("SHOW_BATCH"))) {
          /*  result = IndStockDTool.getInstance().onQueryOrgStockQueryNotBatch(
                parm);*/
        	//�޸� ��ѯ ������Ϳ���� by liyh 20120808
        	//result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQL(parm)));
        	if("Y".equals(mjFlg)) {
        		TParm indOrgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByCode(getValueString("ORG_CODE"))));
        		if(indOrgParm.getCount() > 0 ){
        			String orgType = indOrgParm.getValue("ORG_TYPE",0);
        			parm.setData("ORG_TYPE",orgType);
        		}
        		
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQLNewByInSubCode(parm)));
        	}else {
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQLBySupCode(parm)));
        	}	
        }
        else {		        
           // result = IndStockDTool.getInstance().onQueryOrgStockQuery(parm);
        	//��ʾ����Ч��
        	if("Y".equals(mjFlg)) {
        		TParm indOrgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByCode(getValueString("ORG_CODE"))));
        		if(indOrgParm.getCount() > 0 ){
        			String orgType = indOrgParm.getValue("ORG_TYPE",0);
        			parm.setData("ORG_TYPE",orgType);
        		}
        		 
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockDrugQueryBySupCode(parm)));
        	}else {
        		String sql = "SELECT   a.order_code, c.order_desc, c.SPECIFICATION, h.chn_desc, a.stock_qty, g.unit_chn_desc, "
        		            +" FLOOR (a.stock_qty / f.dosage_qty) || e.unit_chn_desc || MOD (a.stock_qty, f.dosage_qty) "
        		         +" || g.unit_chn_desc AS qty, a.retail_price * f.dosage_qty || '/' || e.unit_chn_desc || ';' "
        		         +" || a.retail_price || '/' || g.unit_chn_desc AS price, a.verifyin_price AS stock_price, "
        		         +" a.stock_qty * a.verifyin_price AS stock_amt, a.stock_qty * c.own_price AS own_amt, "
        		         +" (a.stock_qty * a.retail_price - a.stock_qty * a.verifyin_price ) AS diff_amt, "
        		         +" a.batch_no, a.valid_date, a.stock_flg, b.safe_qty, d.pha_type, "
        		         +" b.material_loc_code, a.retail_price AS own_price, a.active_flg ,f.dosage_qty, k.org_chn_desc "
        		    +" FROM ind_stock a, ind_stockm b, sys_fee c, pha_base d, sys_unit e, pha_transunit f, sys_unit g, "
        		         +" sys_dictionary h, ind_org k ";
        		if(parm.getValue("SUP_CODE_NEW").equals("Y")){
        			sql += " ,IND_SYSPARM M";
        		}
        		sql += " WHERE a.org_code = b.org_code AND a.order_code = b.order_code AND a.order_code = c.order_code "
        		     +" AND a.order_code = d.order_code AND d.stock_unit = e.unit_code AND a.order_code = f.order_code "
        		     +" AND d.dosage_unit = g.unit_code AND h.GROUP_ID = 'SYS_PHATYPE' AND d.type_code = h.ID "
        			 +" AND a.org_code = k.org_code ";
        		if(!parm.getValue("ORG_CODE").equals("")){
        			sql += " AND A.ORG_CODE='"+parm.getValue("ORG_CODE")+"' ";
        		}
        		if(!parm.getValue("ORDER_CODE").equals("")){
        			sql += " AND A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' ";
        		}
        		if(!parm.getValue("BATCH_NO").equals("")){
        			sql += " AND A.BATCH_NO='"+parm.getValue("BATCH_NO")+"' ";
        		}
        		if(!parm.getValue("MATERIAL_LOC_CODE").equals("")){
        			sql += " AND A.MATERIAL_LOC_CODE='"+parm.getValue("MATERIAL_LOC_CODE")+"' ";
        		}
        		if(!parm.getValue("TYPE_CODE").equals("")){
        			sql += " AND A.TYPE_CODE='"+parm.getValue("TYPE_CODE")+"' ";
        		}
        		if(parm.getValue("SAFE_QTY").equals("Y")){
        			sql += " AND B.SAFE_QTY>A.STOCK_QTY ";
        		}
        		if(!parm.getValue("STOCK_QTY").equals("")){
        			sql += " AND A.STOCK_QTY != 0 ";
        		}
        		if(parm.getValue("SUP_CODE_NEW").equals("Y")){
        			sql += " AND (A.SUP_CODE <> M.MAIN_SUP_CODE OR D.TYPE_CODE='4') ";
        		}
        		sql	+= " ORDER BY a.org_code,b.material_loc_code,a.order_code,a.valid_date ";
//        		result = IndStockDTool.getInstance().onQueryOrgStockQueryBySupCode(parm);
        		result = new TParm(TJDODBTool.getInstance().select(sql));
        	}
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
//        Map map = new HashMap();
//        map.put("01", "��ҩ");
//        map.put("02", "�г�ҩ");
//        map.put("03", "�в�ҩ");
//        map.put("04", "������");
//        map.put("05", "��Һ");
//        map.put("06", "�Ƽ�");
//        map.put("07", "�Լ�");
//        map.put("08", "ԭ��");
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
            this.messageBox("û�в�ѯ����");
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
     * ��շ���
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
        this.setValue("SUP_CODE_NEW", "N");
//        this.callFunction("UI|RB_ALL|setSelected", true);//wanglong add 20150130
//        this.callFunction("UI|SUP_CODE|setEnabled", false);
        
       
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        TTable table = getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "ҩƷ���ͳ�Ʊ�");
        date.setData("ORG_CODE", "TEXT",
                     "ͳ�Ʋ���: " +
                     this.getTextFormat("ORG_CODE").getText());
        date.setData("DATE", "TEXT",
                     "ͳ��ʱ��: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        // �������
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
        // ��β����
        date.setData("TOT", "TEXT", "�ϼƣ� "+getValueDouble("OWN_AMT"));
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDOrgStockQuery.jhw",
                             date);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        TTable table = this.getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "���ſ���ѯ");
    }

    /**
     * ����ѡ���¼�
     */
//    public void onOrgAction() {
//        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode(
//            TypeTool.getString(getTextFormat("ORG_CODE").getValue()));
//        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
//        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setSelectedIndex(
//            0);
//    }

    /**
     * ���ܷ���ֵ����
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
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    
	/**
	 * �����������������
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
															
									TParm tableData = table.getParmValue();// �����parmֵһ��,// 1.ȡparamwֵ;
									// 2.ת�� vector����, ��vector ;
									String columnName[] = tableData.getNames("Data");
									String strNames = "";
									for (String tmp : columnName) {
										strNames += tmp + ";";
									}
									strNames = strNames.substring(0, strNames.length() - 1);
									Vector vct = getVector(tableData, "Data", strNames, 0);
			
									String tblColumnName = table.getParmMap(sortColumn);// 3.���ݵ������,��vector����// ������������;
									
									int col = tranParmColIndex(columnName, tblColumnName);// ת��parm�е���
									compare.setDes(ascending);
									compare.setCol(col);
									java.util.Collections.sort(vct, compare);
									// ��������vectorת��parm;
									// Ϊ��ʹ�˷���ͨ�ã�����Ҫ��cloneVectoryParam()Ҳ��ͨ�ã����ܽ�����deptFeeTable
									cloneVectoryParam(vct, new TParm(), strNames, table);
								}
							}
				);
	}
	
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
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
	 * vectoryת��param
	 */
	@SuppressWarnings("rawtypes")
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		// ������->��
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
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
     * �龫����¼�			
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
     * ��ѡ
     */
    public void onChooseRadio() {// wanglong add 20150130
        if (((TRadioButton) this.getComponent("RB_SUP")).isSelected()) {
            this.callFunction("UI|SUP_CODE|setEnabled", true);
        } else {
            this.callFunction("UI|SUP_CODE|setEnabled", false);
        }
    }

}
