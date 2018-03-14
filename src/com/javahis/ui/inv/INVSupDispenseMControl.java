package com.javahis.ui.inv;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

import jdo.inv.INVSQL;
import jdo.inv.INVSUPSQL;
import jdo.inv.InvSupDispenseDTool;
import jdo.inv.InvSupDispenseMTool;
import jdo.inv.InvSupRequestDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.TTableCellEditor;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;

/**
 * <p>Title: ��Ӧ�ҳ���</p>
 *
 * <p>Description: ��Ӧ�ҳ���</p>   
 *  
 * <p>Copyright: Copyright (c) 2008</p> 
 *
 * <p>Company: </p> 
 *
 * @author zhangy 2010.3.8
 * @version 1.0  
 */
public class INVSupDispenseMControl
    extends TControl {

    // ��������
    private TTable tableM;
    // �Ĳĳ���
    private TTable tableInv;
    // ����������  
    private TTable tablePack;
    // ��Ź�������
    private TTable tableDD;
    // ���ⷽʽ
    private String pack_mode;
    //��ʽ��ʱ�� 20150130 wangjingchun add
    private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
    
    TDS sysFeeOrdPackD = new TDS();

    public INVSupDispenseMControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        
        tableInv = getTable("TABLED");
        tablePack = getTable("TABLED2");
        tableDD = getTable("TABLEDD");

        this.setValue("CHECK_USER", Operator.getID());
        this.setValue("CHECK_DATE", SystemTool.getInstance().getDate());

        // ��������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE", 
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");  
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");

        
        // ��ʼ��tableInv��Parm
        TParm parmInv = new TParm();
        String[] verInv = {
            "DISPENSE_NO", "SEQ_NO", "PACK_MODE", "INV_CODE",
            "INV_CHN_DESC", "INVSEQ_NO", "QTY", "STOCK_UNIT",
            "COST_PRICE", "REQUEST_SEQ", "BATCH_NO", "BATCH_SEQ",
            "VALID_DATE", "DISPOSAL_FLG", "OPT_USER", "OPT_DATE",
            "OPT_TERM"};
        for (int i = 0; i < verInv.length; i++) {
        	parmInv.setData(verInv[i], new Vector());
        }
        
        tableInv.setParmValue(parmInv);
        // ��ʼ��tablePack��Parm
        // ����tableע������¼�  20150121 wangjingchun add start
        tablePack.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
        		"onCreateSYSFEE");
        initTableD2();
        this.getTable("TABLED2").addRow();
        //tableʧȥ����ʱȡ���༭״̬
        this.getTable("TABLED2").getTable().putClientProperty("terminateEditOnFocusLost",
																	Boolean.TRUE);
        //20150121 wangjingchun add end
//        TParm parmPack = new TParm();
//        String[] verPack = {"INV_CHN_DESC","INVSEQ_NO","QTY","COST_PRICE",
//        					"BARCODE","PACK_BATCH_NO"};
//        for (int i = 0; i < verPack.length; i++) {
//        	parmPack.setData(verPack[i], "");
//        	parmPack.setData(verPack[i], new Vector());
//        }
//        tablePack.setParmValue(parmPack);
        // ��������¼�
        addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
                         "onTableInvChangeValue");
        // ��������¼�
        addEventListener("TABLED2->" + TTableEvent.CHANGE_VALUE,
                         "onTablePackChangeValue");
        
        onClear();
    }
    
    /**
     * ��ʼ��tableD2��
     * 20150121 wangjingchun add
     */
    public void initTableD2(){
//    	String column = "INV_CHN_DESC;INVSEQ_NO;QTY;COST_PRICE;BARCODE;PACK_BATCH_NO;INV_CODE";
    	String column = "DISPENSE_NO;SEQ_NO;PACK_MODE;INV_CODE;INV_CHN_DESC;"
    					+"INVSEQ_NO;QTY;COST_PRICE;DISPOSAL_FLG;"
    					+"OPT_USER;OPT_DATE;OPT_TERM;BARCODE;PACK_BATCH_NO";
    	String stringMap[] = StringTool.parseLine(column, ";");
		TParm tableDDParm = new TParm();
		for (int i = 0; i < stringMap.length; i++) {
			tableDDParm.addData(stringMap[i], "");
		}
		this.getTable("TABLED2").setParmValue(tableDDParm);
		this.getTable("TABLED2").removeRow(0);
    }
    
    /**
     * ΪtableD2����һ��
     * 20150121 wangjingchun add
     */
    public void addDRow(){
    	String column = "INV_CHN_DESC;INVSEQ_NO;QTY;COST_PRICE;BARCODE;PACK_BATCH_NO";
    	String stringMap[] = StringTool.parseLine(column, ";");
		TParm tableDDParm = new TParm();
		for (int i = 0; i < stringMap.length; i++) {
			tableDDParm.addData(stringMap[i], "");
		}
		this.getTable("TABLED2").addRow(tableDDParm);
    }
    
    /**
	 * ��TABLE�����༭�ؼ�ʱ
	 * 20150121 wangjingchun add
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateSYSFEE(Component com, int row, int column) {
		if(this.getValue("FROM_ORG_CODE").equals("")){
			this.messageBox("����ѡ��Ӧ���ţ�");
			return;
		}
		if(this.getValue("SUPTYPE_CODE").equals("")){
			this.messageBox("����ѡ���������");
			return;
		}
		if(!this.getValue("SUPTYPE_CODE").equals("100001")){
			this.messageBox("�������Ϊ������������ʱ�ſ����������ţ�");
			return;
		}
		if (column != 0)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		TParm parm = new TParm();
		if(this.getValue("SUPTYPE_CODE").equals("100003")){
			parm.setData("SEQ_FLG", "0");
		}else if(this.getValue("SUPTYPE_CODE").equals("100001")){
			parm.setData("SEQ_FLG", "1");
		}
		textFilter.setPopupMenuParameter("UD",
				getConfigParm().newConfig(
						"%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * ���ܷ���ֵ����
	 * 20150121 wangjingchun add
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// ����ת����TParm
		TParm result = (TParm) obj;
		String packCode = result.getValue("PACK_CODE");
		String packDesc = result.getValue("PACK_DESC");
		if(this.getValue("SUPTYPE_CODE").equals("100003")){
			TParm oldParm = new TParm();
			oldParm = this.getTable("TABLED2").getParmValue();
			for(int k=0;k<oldParm.getCount();k++){
				if(oldParm.getValue("INV_CODE",k).equals(packCode)){
					this.messageBox(packDesc+"�����");
					return;
				}
			}
			// ���ݷ��ص�CODE����table��TDS��ֵ
			setDownTableAndTDS(packDesc,packCode);
		}else if(this.getValue("SUPTYPE_CODE").equals("100001")){
			TParm parmNew = new TParm();
			parmNew.setData("PACK_CODE", packCode);
			parmNew.setData("ORG_CODE", this.getValue("FROM_ORG_CODE"));
			parmNew.setData("PACK_DESC", packDesc);
			Object obj2 = this.openDialog(
		            "%ROOT%\\config\\inv\\INVSupPackStockDDNew.x",parmNew);
	        if (obj2 == null)
	            return;
	        TParm parm = (TParm) obj2;
	//        System.out.println("obj2==="+parm);
	        pack_mode = parm.getValue("PACK_MODE");
	
	        TParm tableD2ParmOrld = tablePack.getParmValue();
	        ArrayList<String> list = new ArrayList<String>();
	        String warmStr = "";
	        for(int i=0;i<parm.getCount("INVSEQ_NO");i++){
	        	for(int j=0;j<tableD2ParmOrld.getCount("INVSEQ_NO");j++){
	        		if(parm.getValue("INVSEQ_NO", i).equals(tableD2ParmOrld.getValue("INVSEQ_NO", j))
	        				&& parm.getValue("INV_CHN_DESC", i).equals(tableD2ParmOrld.getValue("INV_CHN_DESC", j))){
	        			if(warmStr.equals("")){
	        				warmStr += parm.getValue("INVSEQ_NO", i);
	        			}else{
	        				warmStr += ","+parm.getValue("INVSEQ_NO", i);
	        			}
	        			list.add(parm.getValue("INVSEQ_NO", i));
	        		}
	        	}
	        }
	        if(list.size()>0){
	        	this.messageBox("��������ţ�"+warmStr+"֮ǰ�����");
	        }
	        // ע�ⲿ�ֳ������ϸ���治������!
	        // �����������������쵥���ص�����
	        onSetTablePackDataNew(parm,list);
	        tablePack.addRow();
		}
	}
	
    /**
     * �������������ⷵ�ص�����
     * 20150128 wangjingchun add
     * @param parm TParm
     */
    private void onSetTablePackDataNew(TParm parm,ArrayList<String> list) {
        tableInv.setVisible(false);
        tablePack.setVisible(true);
        TParm tableParm = new TParm();
//        System.out.println("parm="+tablePack.getParmValue());
        tableParm = tablePack.getParmValue();
        int count = 1;
//        System.out.println(parm.getCount("INV_CODE"));
        boolean a = true;
        for(int i=0;i<parm.getCount("INV_CODE");i++){
        	if(list.size() <= 0){
        		insertNewRowTablePackNew(tableParm, parm.getRow(i),count);
        		count++;
        	}else{
        		for(int k=0;k<list.size();k++){
        			if(parm.getValue("INVSEQ_NO", i).equals(list.get(k))){
        				a = false;
        			}
        		}
        		if(a){
        			insertNewRowTablePackNew(tableParm, parm.getRow(i),count);
        			count++;
        		}
        		a = true;
        	}
        }
        if(tableParm.getCount()>0){
        	for(int j=0;j<tableParm.getCount();j++){
        		if(tableParm.getValue("INVSEQ_NO", j).equals("")){
        			tableParm.removeRow(j);
        		}
        	}
        }
        // ��ӱ������
        tablePack.setParmValue(tableParm);
    }
    
    /**
     * ��������ʱ�����벿������ⲿ���Զ��ı�
     * 20150203 wangjingchun add
     */
    public void onChange(){
    	this.getTextFormat("IN_ORG_CODE").setValue(this.getTextFormat("TO_ORG_CODE").getValue());
    }
    
    /**
     * ����һ��������
     * @param parm TParm
     */
    private TParm insertNewRowTablePackNew(TParm tableParm, TParm invParm,int count) {
        tableParm.addData("DISPENSE_NO", "");
        tableParm.addData("SEQ_NO", count);
        tableParm.addData("PACK_MODE", "1");
        tableParm.addData("INV_CODE", invParm.getValue("INV_CODE"));
        tableParm.addData("INV_CHN_DESC", invParm.getValue("INV_CHN_DESC"));
        tableParm.addData("INVSEQ_NO", invParm.getInt("INVSEQ_NO"));
        tableParm.addData("QTY", invParm.getDouble("QTY"));
        //tableParm.addData("STOCK_UNIT", "");
        tableParm.addData("COST_PRICE", invParm.getDouble("COST_PRICE"));
        //tableParm.addData("BATCH_NO", "");
        //tableParm.addData("BATCH_SEQ", "");
        //tableParm.addData("VALID_DATE", "");
        tableParm.addData("DISPOSAL_FLG", "N");
        tableParm.addData("OPT_USER", Operator.getID());
        tableParm.addData("OPT_DATE", SystemTool.getInstance().getDate());
        tableParm.addData("OPT_TERM", Operator.getIP());
        tableParm.addData("BARCODE", invParm.getValue("BARCODE"));
        tableParm.addData("PACK_BATCH_NO", invParm.getInt("PACK_BATCH_NO"));
        return tableParm;
    }

	/**
	 * ����table��TDS��ֵ
	 * 20150121 wangjingchun add
	 * @param code
	 * @param ordDesc
	 * @param unitcode
	 */
	public void setDownTableAndTDS(String packDesc,String packCode) {
		tablePack.acceptText();
		TParm result = new TParm();
		int selrow = tablePack.getSelectedRow();
		result.setData("INV_CODE", packCode);
		result.setData("INV_CHN_DESC", packDesc);
		result.setData("INVSEQ_NO", "0");
		result.setData("QTY", "1");
		result.setData("COST_PRICE", "");
		result.setData("BARCODE", "");
		result.setData("PACK_BATCH_NO", "");
		this.getTable("TABLED2").removeRow(selrow);
		this.getTable("TABLED2").addRow(result);
		this.getTable("TABLED2").addRow();
        TParm tableDDParm = new TParm();
        String sql = "SELECT B.INV_ABS_DESC AS INV_CHN_DESC, C.QTY * "
                + 1+" AS QTY, C.STOCK_UNIT, G.CONTRACT_PRICE AS COST_PRICE, CASE WHEN C.PACK_TYPE = '1' THEN 'Y' WHEN C.PACK_TYPE = '0' THEN 'N' END AS ONCE_USE_FLG, "
                + " C.INV_CODE "
                + " FROM INV_PACKD C, INV_BASE B, INV_AGENT G "
                + " WHERE C.INV_CODE = B.INV_CODE "
                + " AND C.INV_CODE = G.INV_CODE "
                + " AND  C.PACK_CODE = '" +
                packCode + "' ";
        tableDDParm = new TParm(TJDODBTool.getInstance().select(sql));
        tableDD.setParmValue(tableDDParm);
	}
	
    /**
     * ���淽��
     */
    public void onSave() {
    	//20150121 wangjingchun add start
    	//�жϱ������Ϊ��
    	if(this.getValue("FROM_ORG_CODE").equals("")){
			this.messageBox("����ѡ��Ӧ���ţ�");
			return;
		}
		if(this.getValue("TO_ORG_CODE").equals("")){
			this.messageBox("����ѡ����ⲿ�ţ�");
			return;
		}
		if(this.getValue("REQUEST_TYPE").equals("")){
			this.messageBox("����ѡ�񵥺����ͣ�");
			return;
		}
		if(this.getValue("PACK_MODE").equals("")){
			this.messageBox("����ѡ����ⷽʽ��");
			return;
		}
		if(this.getValue("SUPTYPE_CODE").equals("")){
			this.messageBox("����ѡ���������");
			return;
		}

		//20150121 wangjingchun add end
        TParm parm = new TParm();
        TParm result = new TParm();
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            // �������Ᵽ��
            if ("0".equals(pack_mode)) {
                // ���һ�����ʵĿ����
                if (!checkSupInv()) {
                    return;
                }
                /* ����һ�����ʵĳ��ⵥ */
                //ȡ��INV_SUP_DISPENSEM����
                getInvSupDispenseMData(parm);
                //ȡ��INV_SUP_DISPENSED����
                getInvSupDispenseDInvData(parm);
                //ȡ��INV_STOCKM����
                getInvStockMData(parm);
                //ȡ��INV_STOCKD����
                getInvStockDData(parm);
                //ȡ��INV_STOCKDD����
                getInvStockDDData(parm);
                //ȡ��INV_SUPREQUESTD����
                getInvSupRequestDData(parm);
                //ȡ��INV_SUPREQUESTM����
                getInvSupRequestMData(parm);
                //System.out.println("parm===="+parm);
                // ����һ�����ʳ���
                result = result = TIOM_AppServer.executeAction(
                    "action.inv.INVSupDispenseAction",
                    "onInsertInvSupDispenseByInv", parm);
            }
            else {
                // ����������Ŀ����
                if (!checkSupPack()) {
                    return;
                }  
                /* �����������ĳ��ⵥ */
                //ȡ��INV_SUP_DISPENSEM����
                getInvSupDispenseMData(parm);
                //ȡ��INV_SUP_DISPENSED����
                getInvSupDispenseDPackData(parm);
                //ȡ��INV_SUP_DISPENSMDD����(����������) 
                getInvSupDispenseDDData(parm);
                //ȡ��INV_PACKSTOCKM����
                getInvPackStockMData(parm);
                //ȡ��INV_PACKSTOCKD���� 
                getInvPackStockDData(parm);
                //ȡ��INV_SUPREQUESTD���� 
                getInvSupRequestDData(parm);    
                //ȡ��INV_SUPREQUESTM����   
                getInvSupRequestMData(parm);  
//                System.out.println("parm==>"+parm);
                // ��������������      
                result = result = TIOM_AppServer.executeAction(
                    "action.inv.INVSupDispenseAction",     
                    "onInsertInvSupDispenseByPack", parm);
            }
        }
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ��");
            return;
        }
        this.messageBox("����ɹ�");
        this.setValue("DISPENSE_NO",
                      parm.getParm("INV_SUP_DISPENSEM").getValue("DISPENSE_NO"));
   //2013-11-24ע��     onPrint();
        this.onClear();  //2014-01-06���
    }

    /**
     * ���һ�����ʵĿ����
     * @return boolean
     */
    private boolean checkSupInv() {
        TParm parm = tableInv.getParmValue();
        String org_code = this.getValueString("FROM_ORG_CODE");
        String inv_code = "";
        TParm parmStock = new TParm();
        for (int i = 0; i < parm.getCount("INV_CODE")-1; i++) {
            inv_code = parm.getValue("INV_CODE", i); 
            parmStock = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getINVStockQty(org_code, inv_code)));
            if (parmStock == null || parmStock.getCount() <= 0) {
                this.messageBox(parm.getValue("INV_CHN_DESC", i) + "�޿��");
                return false;
            }
            if (parmStock.getDouble("STOCK_QTY", 0) < parm.getDouble("QTY", i)) {
                this.messageBox(parm.getValue("INV_CHN_DESC", i) + "��治��");
                return false;
            }
        }
        return true;
    }

    /**
     * ����������Ŀ����
     * @return boolean
     */
    private boolean checkSupPack() {
        TParm parm = this.getTable("TABLED2").getParmValue();
        String org_code = this.getValueString("FROM_ORG_CODE");
        String pack_code = "";
        int pack_seq_no = 0;
        int batch_no = 0;
        TParm parmStock = new TParm();
        for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
        	if(!parm.getValue("INVSEQ_NO", i).equals("")){//20150129 wangjingchun add
	    		pack_code = parm.getValue("INV_CODE", i);
	    		pack_seq_no = parm.getInt("INVSEQ_NO", i);  
	    		batch_no = parm.getInt("PACK_BATCH_NO", i); 
	    		parmStock = new TParm(TJDODBTool.getInstance().select(INVSQL.
	    				getINVPackStockQty(org_code, pack_code, pack_seq_no, batch_no)));
	    		if (parmStock == null || parmStock.getCount() <= 0) {
	    			this.messageBox(parm.getValue("INV_CHN_DESC", i) + "�޿��");
	    			return false; 
	    		}
	    		if (parmStock.getDouble("STOCK_QTY", 0) < parm.getDouble("QTY", i)) {
	    			this.messageBox(parm.getValue("INV_CHN_DESC", i) + "��治��");
	    			return false;
	    		}
        	}
        }
        return true;
    }

    /**
     * ȡ��INV_SUP_DISPENSEM����
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupDispenseMData(TParm parm) {
    	//20150121 wangjingchun add start
    	TParm tableMParm = new TParm();
    	if(tableM.getSelectedRow()>=0){
    		tableMParm = tableM.getParmValue().getRow(tableM.getSelectedRow());
    	}else{
    		//���벿��
    		tableMParm.setData("TO_ORG_CODE", this.getValue("IN_ORG_CODE"));
    		//��ⲿ��
    		tableMParm.setData("IN_ORG_CODE", this.getValue("TO_ORG_CODE"));
    		//�������
    		tableMParm.setData("SUPTYPE_CODE", this.getValue("SUPTYPE_CODE"));
    		//��Ӧ����
    		tableMParm.setData("FROM_ORG_CODE", this.getValue("FROM_ORG_CODE"));
    		//�������
    		tableMParm.setData("REQUEST_TYPE", this.getValue("REQUEST_TYPE"));
    		//�������
    		tableMParm.setData("CHECK_DATE", this.getValue("CHECK_DATE"));
    		//�����Ա
    		tableMParm.setData("CHECK_USER", this.getValue("CHECK_USER"));
    		//���뵥��
    		tableMParm.setData("REQUEST_NO", "");
    		//���ʱ��
    		tableMParm.setData("DISPENSE_DATE", SystemTool.getInstance().getDate());
    		//�����Ա
    		tableMParm.setData("DISPENSE_USER", this.getValue("CHECK_USER"));
    		//��ע
    		tableMParm.setData("REMARK", "");
    		//
    		tableMParm.setData("DISPOSAL_FLG", "N");
    		//
    		tableMParm.setData("REN_CODE", "");
    		//
    		tableMParm.setData("URGENT_FLG", "N");
    		//
    		tableMParm.setData("REQUEST_DATE", SystemTool.getInstance().getDate());
    		tableMParm.setData("OPT_USER", Operator.getID());
    		tableMParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
    		tableMParm.setData("OPT_TERM", Operator.getIP());
    	}
    	//20150121 wangjingchun add end
		if ("".equals(this.getValueString("DISPENSE_NO"))) {
			String dispense_no = SystemTool.getInstance().getNo("ALL", "INV",
					"INV_SUPDISPENSE", "No");
			tableMParm.setData("DISPENSE_NO", dispense_no);
		}
		tableMParm.setData("FINA_FLG", "Y");
		if ("0".equals(pack_mode)) {
			tableMParm.setData("PACK_MODE", "0");
		}
		else {
			tableMParm.setData("PACK_MODE", "1");
		}
		
		//2013-11-22�����λ
		tableMParm.setData("MATERIAL_LOCATION", this.getValueString("MLOCATION"));
		
		parm.setData("INV_SUP_DISPENSEM", tableMParm.getData());
        return parm;
    }

    /**
     * ȡ��INV_SUP_DISPENSED����(һ������)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupDispenseDInvData(TParm parm) {
        TNull tnull = new TNull(Timestamp.class);
        String dispense_no = parm.getParm("INV_SUP_DISPENSEM").getValue(
            "DISPENSE_NO");
        TParm tableInvParm = tableInv.getParmValue();
        TParm inv_sup_dispensed = new TParm();
        String org_code = this.getValueString("FROM_ORG_CODE");
        String inv_code = "";
        // ������
        double qty = 0;
        // �����
        double stock_qty = 0;
        // ���ⵥϸ�����
        int seq_no = 1;
        for (int i = 0; i < tableInvParm.getCount("INV_CODE"); i++) {
            inv_code = tableInvParm.getValue("INV_CODE", i);
            qty = tableInvParm.getDouble("QTY", i);
            TParm inv_stockd = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvStockD(org_code, inv_code)));
            //System.out.println("inv_stockd==="+inv_stockd);
            for (int j = 0; j < inv_stockd.getCount("INV_CODE"); j++) {
                stock_qty = inv_stockd.getDouble("STOCK_QTY", j);
                //System.out.println("stock_qty=="+stock_qty);
                if (qty > stock_qty) {
                    inv_sup_dispensed.addData("DISPENSE_NO", dispense_no);
                    inv_sup_dispensed.addData("SEQ_NO", seq_no);
                    inv_sup_dispensed.addData("PACK_MODE", tableInvParm.
                                              getValue("PACK_MODE", i));
                    inv_sup_dispensed.addData("INV_CODE", inv_code);
                    inv_sup_dispensed.addData("INVSEQ_NO",
                                              tableInvParm.getInt("INVSEQ_NO",
                        i)); 
                    inv_sup_dispensed.addData("QTY", stock_qty);
                    inv_sup_dispensed.addData("STOCK_UNIT", tableInvParm.
                                              getValue("STOCK_UNIT", i));
                    inv_sup_dispensed.addData("COST_PRICE", tableInvParm.
                                              getDouble("COST_PRICE", i));
                    inv_sup_dispensed.addData("REQUEST_SEQ", tableInvParm.
                                              getInt("REQUEST_SEQ", i));
                    inv_sup_dispensed.addData("BATCH_NO",
                                              inv_stockd.getValue("BATCH_NO", j));
                    inv_sup_dispensed.addData("BATCH_SEQ",
                                              inv_stockd.getInt("BATCH_SEQ", j));
                    inv_sup_dispensed.addData("VALID_DATE",
                                              inv_stockd.getData("VALID_DATE",
                        j) == null ? tnull : inv_stockd.getTimestamp(
                            "VALID_DATE", j));
                    inv_sup_dispensed.addData("DISPOSAL_FLG", tableInvParm.
                                              getValue("DISPOSAL_FLG", i));
                    inv_sup_dispensed.addData("OPT_USER",
                                              tableInvParm.getValue("OPT_USER",
                        i));
                    inv_sup_dispensed.addData("OPT_DATE", tableInvParm.
                                              getTimestamp("OPT_DATE", i));
                    inv_sup_dispensed.addData("OPT_TERM",
                                              tableInvParm.getValue("OPT_TERM",
                        i));
                    
                    //2013-11-27���  start
                    inv_sup_dispensed.addData("BARCODE","-1");
                    inv_sup_dispensed.addData("PACK_BATCH_NO", -1);
                    //2013-11-27���  end
                    
                    //2013-11-22�����λ  start
                    inv_sup_dispensed.addData("ACTUAL_QTY", stock_qty);
                    inv_sup_dispensed.addData("MATERIAL_LOCATION", this.getValueString("MLOCATION"));
                    //2013-11-22�����λ  end
                    
                    
                    qty = qty - stock_qty;
                    seq_no++;
                }
                else {
                    inv_sup_dispensed.addData("DISPENSE_NO", dispense_no);
                    inv_sup_dispensed.addData("SEQ_NO", seq_no);
                    inv_sup_dispensed.addData("PACK_MODE", tableInvParm.
                                              getValue("PACK_MODE", i));
                    inv_sup_dispensed.addData("INV_CODE", inv_code);
                    inv_sup_dispensed.addData("INVSEQ_NO",
                                              tableInvParm.getInt("INVSEQ_NO",
                        i));
                    inv_sup_dispensed.addData("QTY", qty);
                    inv_sup_dispensed.addData("STOCK_UNIT", tableInvParm.
                                              getValue("STOCK_UNIT", i));
                    inv_sup_dispensed.addData("COST_PRICE", tableInvParm.
                                              getDouble("COST_PRICE", i));
                    inv_sup_dispensed.addData("REQUEST_SEQ", tableInvParm.
                                              getInt("REQUEST_SEQ", i));
                    inv_sup_dispensed.addData("BATCH_NO",
                                              inv_stockd.getValue("BATCH_NO", j));
                    inv_sup_dispensed.addData("BATCH_SEQ",
                                              inv_stockd.getInt("BATCH_SEQ", j));
                    inv_sup_dispensed.addData("VALID_DATE",
                                              inv_stockd.getData("VALID_DATE",
                        j) == null ? tnull : inv_stockd.getTimestamp(
                            "VALID_DATE", j));
                    inv_sup_dispensed.addData("DISPOSAL_FLG", tableInvParm.
                                              getValue("DISPOSAL_FLG", i));
                    inv_sup_dispensed.addData("OPT_USER",
                                              tableInvParm.getValue("OPT_USER",
                        i));
                    inv_sup_dispensed.addData("OPT_DATE", tableInvParm.
                                              getTimestamp("OPT_DATE", i));
                    inv_sup_dispensed.addData("OPT_TERM",
                                              tableInvParm.getValue("OPT_TERM",
                        i));
                    
                    //2013-11-27���  start
                    inv_sup_dispensed.addData("BARCODE","-1");
                    inv_sup_dispensed.addData("PACK_BATCH_NO", -1);
                    //2013-11-27���  end
                    
                    //2013-11-22�����λ  start
                    inv_sup_dispensed.addData("ACTUAL_QTY", qty);
                    inv_sup_dispensed.addData("MATERIAL_LOCATION", this.getValueString("MLOCATION"));
                    //2013-11-22�����λ  end
                    
                    seq_no++;
                    break;
                }
            }
        }
        parm.setData("INV_SUP_DISPENSED", inv_sup_dispensed.getData());
        return parm;
    }

    /**
     * ȡ��INV_SUP_DISPENSED����(������)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupDispenseDPackData(TParm parm) {
        String dispense_no = parm.getParm("INV_SUP_DISPENSEM").getValue(
            "DISPENSE_NO");
        TNull tnull = new TNull(Timestamp.class);
        
        TParm tablePackParm = new TParm();
    	tablePackParm = tablePack.getParmValue();
//        System.out.println("tablePackParm=="+tablePackParm);
    	for(int n=tablePackParm.getCount("INV_CODE")-1;n>0;n--){
    		if(tablePackParm.getValue("INV_CODE", n).equals("")){
    			tablePackParm.removeRow(n);
    		}
    	}
        TParm inv_sup_dispensed = new TParm();
        for (int i = 0; i < tablePackParm.getCount("INV_CODE"); i++) {
//        	if(!tablePackParm.getValue("INVSEQ_NO", i).equals("")){//20150129 wangjingchun add
	            inv_sup_dispensed.addData("DISPENSE_NO", dispense_no);
	            inv_sup_dispensed.addData("SEQ_NO", i + 1);
	//            inv_sup_dispensed.addData("PACK_MODE", tablePackParm.
	//                                      getValue("PACK_MODE", i));
	            inv_sup_dispensed.addData("PACK_MODE", this.getValue("PACK_MODE"));
	            inv_sup_dispensed.addData("INV_CODE",
	                                      tablePackParm.getValue("INV_CODE", i));
	            inv_sup_dispensed.addData("INVSEQ_NO",
	                                      tablePackParm.getInt("INVSEQ_NO", i));
	            inv_sup_dispensed.addData("QTY", tablePackParm.getDouble("QTY", i));
	            inv_sup_dispensed.addData("STOCK_UNIT", "");
	            inv_sup_dispensed.addData("COST_PRICE", tablePackParm.
	                                      getDouble("COST_PRICE", i));
	            inv_sup_dispensed.addData("REQUEST_SEQ", tablePackParm.
	                                      getInt("REQUEST_SEQ", i));
	            inv_sup_dispensed.addData("BATCH_NO", "");
	            inv_sup_dispensed.addData("BATCH_SEQ", "");
	            inv_sup_dispensed.addData("VALID_DATE", tnull);
	//            inv_sup_dispensed.addData("DISPOSAL_FLG", tablePackParm.
	//                                      getValue("DISPOSAL_FLG", i));
	//            inv_sup_dispensed.addData("OPT_USER",
	//                                      tablePackParm.getValue("OPT_USER", i));
	//            inv_sup_dispensed.addData("OPT_DATE",
	//                                      tablePackParm.getTimestamp("OPT_DATE", i));
	//            inv_sup_dispensed.addData("OPT_TERM",
	//                                      tablePackParm.getValue("OPT_TERM", i));
	            inv_sup_dispensed.addData("DISPOSAL_FLG", "N");
				inv_sup_dispensed.addData("OPT_USER", Operator.getID());
				inv_sup_dispensed.addData("OPT_DATE", SystemTool.getInstance().getDate());
				inv_sup_dispensed.addData("OPT_TERM", Operator.getIP());
				
	            inv_sup_dispensed.addData("BARCODE",tablePackParm.getValue("BARCODE", i));
	            inv_sup_dispensed.addData("PACK_BATCH_NO",tablePackParm.getInt("PACK_BATCH_NO", i));
	            
	            //2013-11-22�����λ  start
	            inv_sup_dispensed.addData("ACTUAL_QTY", tablePackParm.getDouble("QTY", i));
	            inv_sup_dispensed.addData("MATERIAL_LOCATION", this.getValueString("MLOCATION"));
	            //2013-11-22�����λ  end
//        	}
        }
//        System.out.println("INV_SUP_DISPENSED=="+inv_sup_dispensed.getData());
        parm.setData("INV_SUP_DISPENSED", inv_sup_dispensed.getData());
        return parm;
    }


    /**
     * ȡ��INV_SUP_DISPENSEDD����(����������)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupDispenseDDData(TParm parm) {
        TNull tnull = new TNull(Timestamp.class);
        String dispense_no = parm.getParm("INV_SUP_DISPENSEM").getValue(
            "DISPENSE_NO");
        TParm inv_sup_dispensed = parm.getParm("INV_SUP_DISPENSED");
        String org_code = this.getValueString("FROM_ORG_CODE");
        String pack_code = "";
        int pack_seq_no = 0;
        int seq_no = 1;
        int batch_no = 0;
        double qty = 0;
        TParm inv_sup_dispensedd = new TParm();
        Timestamp datetime = SystemTool.getInstance().getDate();
        TParm tablePackParm = tablePack.getParmValue();    
        for (int i = 0; i < inv_sup_dispensed.getCount("DISPENSE_NO"); i++) {
            pack_code = inv_sup_dispensed.getValue("INV_CODE", i);
//            pack_seq_no = inv_sup_dispensed.getInt("INVSEQ_NO",i);
            batch_no = inv_sup_dispensed.getInt("PACK_BATCH_NO",i);
            
            
            qty = inv_sup_dispensed.getDouble("QTY", i);		//������������
            TParm packDInfo = new TParm(TJDODBTool.getInstance().select(INVSUPSQL.
            		getPackDInfo(pack_code)));  //����packd��Ϣ
            
            //2014-01-16���start
            TParm packMInfo = new TParm(TJDODBTool.getInstance().select(INVSUPSQL.
            		getPackMInfo(pack_code)));  //����packm��Ϣ
            
            if(packMInfo.getValue("SEQ_FLG",0).toString().equals("1")){//��ܰ�
            	pack_seq_no = 1;
            }else{//����ܰ�  
            	pack_seq_no = 0; 
            }
            //2014-01-16���end  
            
            double stockQty = 0;//��Ҫ��ȫ��������������
            for(int m = 0; m<packDInfo.getCount("INV_CODE"); m ++){
            	stockQty = qty * packDInfo.getDouble("QTY",m);	
            	TParm supParm = new TParm(TJDODBTool.getInstance().select(INVSUPSQL.
                        getInvSupDispenseDD(org_code, pack_code, packDInfo.getValue("INV_CODE",m).toString(), batch_no)));
//                System.out.println("supParm"+supParm); 
            	for (int j = 0; j < supParm.getCount("INV_CODE"); j++) {
            		if(stockQty == 0){
            			break;
            		}
            		
            		if(stockQty>=supParm.getDouble("QTY", j)){  
            			inv_sup_dispensedd.addData("DISPENSE_NO", dispense_no);
                		inv_sup_dispensedd.addData("SEQ_NO", seq_no);
                        inv_sup_dispensedd.addData("PACK_MODE", "1");
                        inv_sup_dispensedd.addData("PACK_CODE", pack_code);
                        //fux modify 20140321 �޸��������
                        //inv_sup_dispensedd.addData("PACK_SEQ_NO", pack_seq_no);
                        inv_sup_dispensedd.addData("PACK_SEQ_NO",tablePackParm.getInt("INVSEQ_NO", i));
                        inv_sup_dispensedd.addData("INV_CODE",
                                                       supParm.getValue("INV_CODE", j));
                        inv_sup_dispensedd.addData("INVSEQ_NO",
                                                       supParm.getInt("INVSEQ_NO", j));
                        inv_sup_dispensedd.addData("ONCE_USE_FLG",
                                                       supParm.getValue("ONCE_USE_FLG", j));
                        inv_sup_dispensedd.addData("QTY",supParm.getDouble("QTY", j));
//                        inv_sup_dispensedd.addData("QTY",
//                                                       supParm.getDouble("QTY", j) *
//                                                       inv_sup_dispensed.getDouble("QTY", i));
                        inv_sup_dispensedd.addData("STOCK_UNIT",
                                                       supParm.getValue("STOCK_UNIT", j));
                        inv_sup_dispensedd.addData("COST_PRICE",
                                                       supParm.getDouble("COST_PRICE", j));
                        inv_sup_dispensedd.addData("REQUEST_SEQ",
                                                       inv_sup_dispensed.
                                                       getInt("REQUEST_NO", i));
                        inv_sup_dispensedd.addData("BATCH_SEQ",
                                supParm.getValue("BATCH_SEQ", j));
                        inv_sup_dispensedd.addData("BATCH_NO",   
                                                       supParm.getValue("BATCH_NO", j));
                        inv_sup_dispensedd.addData("VALID_DATE",
                                                       supParm.getData("VALID_DATE", j) == null ?
                                                       tnull :
                                                       supParm.getTimestamp("VALID_DATE", j));
                        inv_sup_dispensedd.addData("DISPOSAL_FLG",
                                                       inv_sup_dispensed.
                                                       getValue("DISPOSAL_FLG", i));
                        inv_sup_dispensedd.addData("OPT_USER", Operator.getID());
                        inv_sup_dispensedd.addData("OPT_DATE", datetime);   
                        inv_sup_dispensedd.addData("OPT_TERM", Operator.getIP());
                        inv_sup_dispensedd.addData("BARCODE", supParm.getValue("BARCODE", i));

                        inv_sup_dispensedd.addData("PACK_BATCH_NO", supParm.getInt("PACK_BATCH_NO", j));
                        seq_no++;
                        
                        stockQty = stockQty - supParm.getDouble("QTY", j);
            		}else if(stockQty<supParm.getDouble("QTY", j)){
            			inv_sup_dispensedd.addData("DISPENSE_NO", dispense_no);
                		inv_sup_dispensedd.addData("SEQ_NO", seq_no);
                        inv_sup_dispensedd.addData("PACK_MODE", "1");
                        inv_sup_dispensedd.addData("PACK_CODE", pack_code);
                        //fux modify 20140321  �޸��������
                        //inv_sup_dispensedd.addData("PACK_SEQ_NO", pack_seq_no);
                        inv_sup_dispensedd.addData("PACK_SEQ_NO", tablePackParm.getInt("INVSEQ_NO", i));
                        inv_sup_dispensedd.addData("INV_CODE",
                                                       supParm.getValue("INV_CODE", j));
                        inv_sup_dispensedd.addData("INVSEQ_NO",
                                                       supParm.getInt("INVSEQ_NO", j));
                        inv_sup_dispensedd.addData("ONCE_USE_FLG",
                                                       supParm.getValue("ONCE_USE_FLG", j));
                        inv_sup_dispensedd.addData("QTY",stockQty); 
//                        inv_sup_dispensedd.addData("QTY",   
//                                                       supParm.getDouble("QTY", j) *
//                                                       inv_sup_dispensed.getDouble("QTY", i));
                        inv_sup_dispensedd.addData("STOCK_UNIT",
                                                       supParm.getValue("STOCK_UNIT", j));
                        inv_sup_dispensedd.addData("COST_PRICE",
                                                       supParm.getDouble("COST_PRICE", j));
                        inv_sup_dispensedd.addData("REQUEST_SEQ",
                                                       inv_sup_dispensed.
                                                       getInt("REQUEST_NO", i));
                        inv_sup_dispensedd.addData("BATCH_SEQ",
                                supParm.getValue("BATCH_SEQ", j));
                        inv_sup_dispensedd.addData("BATCH_NO",
                                                       supParm.getValue("BATCH_NO", j));
                        inv_sup_dispensedd.addData("VALID_DATE",
                                                       supParm.getData("VALID_DATE", j) == null ?
                                                       tnull :
                                                       supParm.getTimestamp("VALID_DATE", j));
                        inv_sup_dispensedd.addData("DISPOSAL_FLG",
                                                       inv_sup_dispensed.
                                                       getValue("DISPOSAL_FLG", i));     
                        inv_sup_dispensedd.addData("OPT_USER", Operator.getID());
                        inv_sup_dispensedd.addData("OPT_DATE", datetime);
                        inv_sup_dispensedd.addData("OPT_TERM", Operator.getIP());  
                        inv_sup_dispensedd.addData("BARCODE", supParm.getValue("BARCODE", i));
                        inv_sup_dispensedd.addData("PACK_BATCH_NO", supParm.getInt("PACK_BATCH_NO", j));
                        seq_no++;
                        stockQty = 0;
            		}
            		 
                }
            }
        }
        parm.setData("INV_SUP_DISPENSEDD", inv_sup_dispensedd.getData());
        return parm;
    }

    /**
     * ȡ��INV_STOCKM����
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvStockMData(TParm parm) {
        String org_code = this.getValueString("FROM_ORG_CODE");
        TParm tableInvParm = tableInv.getParmValue();
        for (int i = 0; i < tableInvParm.getCount("INV_CODE"); i++) {
            tableInvParm.addData("ORG_CODE", org_code);
        }
        parm.setData("INV_STOCKM", tableInvParm.getData());
        return parm;
    }

    /**
     * ȡ��INV_STOCKD����
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvStockDData(TParm parm) {
        String org_code = this.getValueString("FROM_ORG_CODE");
        TParm inv_stockd = parm.getParm("INV_SUP_DISPENSED");
        //System.out.println("inv_stockd==111==" + inv_stockd);
        for (int i = 0; i < inv_stockd.getCount("INV_CODE"); i++) {
            inv_stockd.addData("ORG_CODE", org_code);
        }
        //System.out.println("inv_stockd==222==" + inv_stockd);
//        System.out.println("inv_stockd==333==" +
//                           parm.getParm("INV_SUP_DISPENSED"));
        parm.setData("INV_STOCKD", inv_stockd.getData());
        return parm;
    }

    /**
     * ȡ��INV_STOCKDD����
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvStockDDData(TParm parm) {
        TParm inv_stockd = parm.getParm("INV_STOCKD");
        TParm inv_stockdd = new TParm();
        String inv_code = "";
        String org_code = this.getValueString("TO_ORG_CODE");
        for (int i = 0; i < inv_stockd.getCount("INV_CODE"); i++) {
            inv_code = inv_stockd.getValue("INV_CODE", i);
            //System.out.println("sqlStr==="+sqlStr);
            TParm inv_base = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvBase(inv_code)));
            if ("N".equals(inv_base.getValue("SEQMAN_FLG", 0))) {
                continue;
            }
            else {
                inv_stockdd.addData("ORG_CODE", org_code);
                inv_stockdd.addData("INV_CODE",
                                    inv_stockd.getValue("INV_CODE", i));
                inv_stockdd.addData("BATCH_NO",
                                    inv_stockd.getValue("BATCH_NO", i));
                inv_stockdd.addData("DISPENSE_NO",
                                    inv_stockd.getValue("DISPENSE_NO", i));
                inv_stockdd.addData("OPT_TERM",
                                    inv_stockd.getValue("OPT_TERM", i));
                inv_stockdd.addData("OPT_DATE",
                                    inv_stockd.getTimestamp("OPT_DATE", i));
                inv_stockdd.addData("REQUEST_SEQ",
                                    inv_stockd.getInt("REQUEST_SEQ", i));
                inv_stockdd.addData("SEQ_NO", inv_stockd.getInt("SEQ_NO", i));
                inv_stockdd.addData("OPT_USER",
                                    inv_stockd.getValue("OPT_USER", i));
                inv_stockdd.addData("INVSEQ_NO",
                                    inv_stockd.getInt("INVSEQ_NO", i));
                inv_stockdd.addData("STOCK_UNIT",
                                    inv_stockd.getValue("STOCK_UNIT", i));
                inv_stockdd.addData("BATCH_SEQ",
                                    inv_stockd.getInt("BATCH_SEQ", i));
                inv_stockdd.addData("VALID_DATE",
                                    inv_stockd.getData("VALID_DATE", i));
                inv_stockdd.addData("QTY", inv_stockd.getDouble("QTY", i));
                inv_stockdd.addData("PACK_MODE",
                                    inv_stockd.getValue("PACK_MODE", i));
                inv_stockdd.addData("DISPOSAL_FLG",
                                    inv_stockd.getValue("DISPOSAL_FLG", i));
                inv_stockdd.addData("COST_PRICE",
                                    inv_stockd.getDouble("COST_PRICE", i));
            }
        }
        parm.setData("INV_STOCKDD", inv_stockdd.getData());
        return parm;
    }

    /**
     * ȡ��INV_PACKSTOCKM����
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvPackStockMData(TParm parm) {
        TParm packParm = tablePack.getParmValue();
        String org_code = this.getValueString("FROM_ORG_CODE");
        TParm inv_packstockm = new TParm();
        Timestamp datetime = SystemTool.getInstance().getDate();
        for (int i = 0; i < packParm.getCount("INV_CODE"); i++) {
            inv_packstockm.addData("ORG_CODE", org_code);
            inv_packstockm.addData("PACK_CODE", packParm.getValue("INV_CODE", i));
            inv_packstockm.addData("PACK_SEQ_NO",
                                   packParm.getInt("INVSEQ_NO", i));
            inv_packstockm.addData("QTY", packParm.getDouble("QTY", i));
            if (packParm.getInt("INVSEQ_NO", i) == 0) {
                inv_packstockm.addData("STATUS", "0");
            }
            else {
                inv_packstockm.addData("STATUS", "1");
            }
            inv_packstockm.addData("OPT_USER", Operator.getID());
            inv_packstockm.addData("OPT_DATE", datetime);
            inv_packstockm.addData("OPT_TERM", Operator.getIP());
            inv_packstockm.addData("BARCODE", packParm.getValue("BARCODE", i));
            inv_packstockm.addData("PACK_BATCH_NO", packParm.getInt("PACK_BATCH_NO", i));
        }
        parm.setData("INV_PACKSTOCKM",inv_packstockm.getData());
        return parm;
    }
     //fux 20140331
    /**
     * ȡ��INV_PACKSTOCKD����
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvPackStockDData(TParm parm) {     
    	TParm inv_sup_dispensed = parm.getParm("INV_SUP_DISPENSED");
        TParm inv_sup_dispensedd = parm.getParm("INV_SUP_DISPENSEDD"); 
        String org_code = this.getValueString("FROM_ORG_CODE");
        TParm inv_packstockd = new TParm(); 
        for(int j = 0; j < inv_sup_dispensed.getCount("INV_CODE"); j++){
        for (int i = 0; i < inv_sup_dispensedd.getCount("INV_CODE"); i++) {
            inv_packstockd.addData("ORG_CODE", org_code);  
            inv_packstockd.addData("PACK_CODE",
                                   inv_sup_dispensedd.getValue("PACK_CODE", i));
            // modify 20140321 �޸�������packϵ�����  �@�eֻȡ ��������M��INVSEQ_NO����PACK_SEQ_NO
            inv_packstockd.addData("PACK_SEQ_NO",         
                                   inv_sup_dispensed.getInt("INVSEQ_NO",j)); 
            inv_packstockd.addData("INV_CODE",    
                                   inv_sup_dispensedd.getValue("INV_CODE", i)); 
            inv_packstockd.addData("INVSEQ_NO",
                                   inv_sup_dispensedd.getInt("INVSEQ_NO", i));
            inv_packstockd.addData("QTY", inv_sup_dispensedd.getDouble("QTY", i));
            inv_packstockd.addData("OPT_USER",
                                   inv_sup_dispensedd.getValue("OPT_USER", i)); 
            inv_packstockd.addData("OPT_DATE",
                                   inv_sup_dispensedd.getTimestamp("OPT_DATE",
                i));
            inv_packstockd.addData("OPT_TERM",  
                                   inv_sup_dispensedd.getValue("OPT_TERM", i));
            inv_packstockd.addData("ONCE_USE_FLG",
                                   inv_sup_dispensedd.getValue("ONCE_USE_FLG",
                i));  
            //BARCODE�޸ėl�a  
            inv_packstockd.addData("BARCODE", inv_sup_dispensed.getValue("BARCODE", j));
            inv_packstockd.addData("PACK_BATCH_NO", inv_sup_dispensedd.getValue("PACK_BATCH_NO", i));
            inv_packstockd.addData("BATCH_SEQ", inv_sup_dispensedd.getValue("BATCH_SEQ", i));//2013-12-26����
        }
        }    
        parm.setData("INV_PACKSTOCKD", inv_packstockd.getData());
        return parm;
    }

    /**
     * ȡ��INV_SUPREQUESTM����
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupRequestMData(TParm parm) {
        TParm inv_requestd = parm.getParm("INV_SUPREQUESTD");  
//        System.out.println("inv_requestd:"+inv_requestd);
        TParm inv_requestm = new TParm(); 
        inv_requestm.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));
        String update_flg = "3";
        for (int i = 0; i < inv_requestd.getCount("REQUEST_NO"); i++) {
            if ("1".equals(inv_requestd.getValue("UPDATE_FLG", i))) {
                update_flg = "1";
                break;
            }
        }
        inv_requestm.setData("UPDATE_FLG", update_flg);
        inv_requestm.setData("OPT_USER", Operator.getID());
        inv_requestm.setData("OPT_DATE",
                             SystemTool.getInstance().getDate());
        inv_requestm.setData("OPT_TERM", Operator.getIP());
        parm.setData("INV_SUPREQUESTM", inv_requestm.getData());
        return parm;
    }

    /**
     * ȡ��INV_SUPREQUESTD����
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupRequestDData(TParm parm) {
        TParm inv_requestd = new TParm();
        TParm requestParm = new TParm();
        String request_no = this.getValueString("REQUEST_NO");
        requestParm.setData("REQUEST_NO", request_no);
        requestParm = InvSupRequestDTool.getInstance().onQuery(requestParm);
        String sql = " SELECT QTY  FROM INV_SUPREQUESTD" +
		" WHERE REQUEST_NO = '"+request_no+"' ";
        Double qty = 0.00;  
        TParm requestQty = new TParm(TJDODBTool.getInstance().select(sql));
        String sqlAct = " SELECT ACTUAL_QTY  FROM INV_SUPREQUESTD" +
		" WHERE REQUEST_NO = '"+request_no+"' ";
        Double Actqty = 0.00; 
        TParm requestActQty = new TParm(TJDODBTool.getInstance().select(sqlAct));
        if ("0".equals(pack_mode)) {
            TParm inv_parm = tableInv.getParmValue();
            String update_flg = "";
            for (int i = 0; i < inv_parm.getCount("INV_CODE"); i++) {
            	qty = requestQty.getDouble("QTY", i);
            	Actqty = requestActQty.getDouble("ACTUAL_QTY", i);
                for (int j = 0; j < requestParm.getCount("INV_CODE"); j++) {
                    if (inv_parm.getInt("REQUEST_SEQ", i) ==
                        requestParm.getInt("SEQ_NO", j)) {
                        inv_requestd.addData("REQUEST_NO", request_no);
                        inv_requestd.addData("SEQ_NO",
                                             inv_parm.getInt("REQUEST_SEQ", i));
                        inv_requestd.addData("ACTUAL_QTY",
                                             inv_parm.getDouble("QTY", i));
                        if (inv_parm.getDouble("QTY", i) + Actqty<
                        		qty) { 
                            update_flg = "1";
                        }    
                        else {
                            update_flg = "3";
                        }
                        inv_requestd.addData("UPDATE_FLG", update_flg);
                        inv_requestd.addData("OPT_USER", Operator.getID());
                        inv_requestd.addData("OPT_DATE",
                                             SystemTool.getInstance().getDate());
                        inv_requestd.addData("OPT_TERM", Operator.getIP());
                        break;
                    }
                }
            }
        }
        else {
            TParm pack_parm = tablePack.getParmValue();
            String update_flg = "";
            for (int i = 0; i < pack_parm.getCount("INV_CODE"); i++) {
            	qty = requestQty.getDouble("QTY", i);
            	Actqty = requestActQty.getDouble("ACTUAL_QTY", i);
                for (int j = 0; j < requestParm.getCount("INV_CODE"); j++) {
                    if (pack_parm.getInt("REQUEST_SEQ", i) ==    
                        requestParm.getInt("SEQ_NO", j)) {
                        inv_requestd.addData("REQUEST_NO", request_no);
                        inv_requestd.addData("SEQ_NO",
                                             pack_parm.getInt("REQUEST_SEQ", i));
                        inv_requestd.addData("ACTUAL_QTY",
                                             pack_parm.getDouble("QTY", i));
                        if (pack_parm.getDouble("QTY", i)+Actqty<
                        		qty) {   
                            update_flg = "1"; 
                        }   
                        else {  
                            update_flg = "3";
                        }
                        inv_requestd.addData("UPDATE_FLG", update_flg);
                        inv_requestd.addData("OPT_USER", Operator.getID());
                        inv_requestd.addData("OPT_DATE",
                                             SystemTool.getInstance().getDate());
                        inv_requestd.addData("OPT_TERM", Operator.getIP());
                        break;
                    }
                }
            }
        }
        parm.setData("INV_SUPREQUESTD", inv_requestd.getData()); 
//        System.out.println("INV_SUPREQUESTD:"+inv_requestd.getData());
        return parm;
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
    	
    	String start_date = this.formateDate.format(this.getValue("START_DATE"));
    	String end_date = this.formateDate.format(this.getValue("END_DATE"));
    	String sqlStart = "SELECT A.DISPENSE_NO, A.TO_ORG_CODE, A.FROM_ORG_CODE, "
    								+"A.REQUEST_TYPE, A.CHECK_DATE, A.CHECK_USER, "
    								+"A.REQUEST_NO, A.PACK_MODE, '' AS SUPTYPE_CODE, "
    								+"A.REN_CODE,A.URGENT_FLG, A.FINA_FLG,A.REMARK, "
    								+"A.MATERIAL_LOCATION, IN_ORG_CODE "
    							+"FROM INV_SUP_DISPENSEM A WHERE A.CHECK_DATE BETWEEN "
    								+"TO_DATE ('"
    								+start_date
    								+" 00:00:00','YYYY-MM-DD HH24:MI:SS') AND TO_DATE ('"
    								+end_date
    								+" 23:59:59','YYYY-MM-DD HH24:MI:SS') "
    								+" AND A.FINA_FLG = 'Y' ";
    	String sqlEnd = " ORDER BY A.DISPENSE_NO ";
    	
//        TParm parm = new TParm();
//        parm.setData("START_DATE", this.getValue("START_DATE"));
//        parm.setData("END_DATE", this.getValue("END_DATE"));
//        if (getRadioButton("UPDATE_A").isSelected()) {
//            parm.setData("UPDATE_FLG_A", "Y");
//        }
//        else {
//            parm.setData("UPDATE_FLG_B", "Y");
//        }
        if (!"".equals(this.getValueString("PACK_MODE_Q"))) {
//            parm.setData("PACK_MODE", getValueString("PACK_MODE_Q"));
            sqlStart += " AND A.PACK_MODE='"+this.getValueString("PACK_MODE_Q")+"' ";
        }
        if (!"".equals(this.getValueString("DISPENSE_NO_Q"))) {
//            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO_Q"));
            sqlStart += " AND A.DISPENSE_NO='"+this.getValueString("DISPENSE_NO_Q")+"' ";
        }
        if (!"".equals(this.getValueString("FROM_ORG_CODE_Q"))) {
//            parm.setData("FROM_ORG_CODE", getValueString("FROM_ORG_CODE_Q"));
            sqlStart += " AND A.APP_ORG_CODE='"+this.getValueString("FROM_ORG_CODE_Q")+"' ";
        }
        if (!"".equals(this.getValueString("TO_ORG_CODE_Q"))) {
//            parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE_Q"));
            sqlStart += " AND A.TO_ORG_CODE='"+this.getValueString("TO_ORG_CODE_Q")+"' ";
        }
//        System.out.println("sql====>"+sqlStart+sqlEnd);
//        TParm result = InvSupDispenseMTool.getInstance().onQuery(parm);
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlStart+sqlEnd));
//        String sql = "SELECT SUPTYPE_CODE FROM INV_SUPREQUESTM WHERE DISPENSE_NO='";
        for(int i=0;i<result.getCount("DISPENSE_NO");i++){
        	if(!result.getValue("REQUEST_NO",i).equals("")){
        		String sql = "SELECT SUPTYPE_CODE FROM INV_SUPREQUESTM WHERE REQUEST_NO='"+result.getValue("REQUEST_NO",i)+"' ";
//        		System.out.println(sql);
        		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        		result.setData("SUPTYPE_CODE", i, parm.getValue("SUPTYPE_CODE", 0));
        	}else{
        		result.setData("SUPTYPE_CODE", i, "100001");
        	}
        }
//        System.out.println(result);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        tableM.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.clearValue("PACK_MODE_Q;DISPENSE_NO_Q;FROM_ORG_CODE_Q;"
                        +
                        "TO_ORG_CODE_Q;START_DATE;END_DATE;DISPENSE_NO;REQUEST_NO;"
                        +
                        "TO_ORG_CODE;"
                        + "REN_CODE;MLOCATION;IN_ORG_CODE");
        //Ĭ������
        this.setValue("FROM_ORG_CODE", Operator.getDept());
        this.setValue("REQUEST_TYPE", "REQ");
        this.setValue("PACK_MODE", "1");
        this.setValue("SUPTYPE_CODE", "100001");
        ( (TRadioButton)this.getComponent("UPDATE_A")).setSelected(true);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("CHECK_DATE", date);
        tableM.removeRowAll();
        tableInv.removeRowAll();  
//        tablePack.removeRowAll();
        initTableD2();
        tablePack.addRow();
        tableDD.removeRowAll();
        tableInv.setVisible(false);
        tablePack.setVisible(true);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        ( (TMenuItem) getComponent("import")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int tableM_row = tableM.getSelectedRow();
        if ("0".equals(pack_mode)) {
            // ɾ������
            int tableInv_row = tableInv.getSelectedRow();
            if (tableInv_row > -1) {
                tableInv.removeRow(tableInv_row);
            }
            else {
                tableInv.removeRowAll();
                tableM.removeRow(tableM_row);
            }
        }
        else {
            // ɾ��������
            int tablePack_row = tablePack.getSelectedRow();
            if (tablePack_row > -1) {
                tablePack.removeRow(tablePack_row);
            }
            else {
                tablePack.removeRowAll();
                tableM.removeRow(tableM_row);
            }
        }
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        if ("0".equals(pack_mode)) {
            // һ�����ʳ���
            onPrintInv();
        }
        else {
            // ����������
            onPrintPack();
        }
    }

    /**
     * ��ӡһ�����ʳ��ⵥ
     */
    private void onPrintInv() {
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "��Ӧ�ҳ��ⵥ");
        date.setData("DISPENSE_NO", "TEXT",
                     "���ⵥ��: " + this.getValueString("DISPENSE_NO"));
        date.setData("DISPENSE_DATE", "TEXT",
                     "��������: " +
                     this.getValueString("CHECK_DATE").substring(0, 19).
                     replace("-", "/"));
        date.setData("DATE", "TEXT",
                     "�Ʊ�����: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 19).
                     replace("-", "/"));
        date.setData("FROM_ORG_CODE", "TEXT",
                     "���ⲿ��: " + this.getTextFormat("FROM_ORG_CODE").getText());
        date.setData("TO_ORG_CODE", "TEXT",
                     "��ⲿ��: " + this.getTextFormat("TO_ORG_CODE").getText());
        date.setData("PACK_MODE", "TEXT",
                     "���ⷽʽ: " + this.getComboBox("PACK_MODE").getSelectedName());
        date.setData("SUPTYPE_CODE", "TEXT",
                     "�������: " + this.getTextFormat("SUPTYPE_CODE").getText());
        //System.out.println("date===" + date);
        // �������
        String dispense_no = this.getValueString("DISPENSE_NO");
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvSupDispenseInvForPrint(dispense_no)));
        parm.setCount(parm.getCount("INV_CHN_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        parm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "AMT");
        parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        double sum_money = 0;
        for (int i = 0; i < parm.getCount("INV_CHN_DESC"); i++) {
            sum_money += parm.getDouble("AMT", i);
            if (parm.getData("VALID_DATE", i) == null ||
                "null".equals(parm.getValue("VALID_DATE", i))) {
                parm.setData("VALID_DATE", i, "");
            }
            else if (parm.getData("VALID_DATE", i) != null &&
                     parm.getValue("VALID_DATE", i).length() >= 10) {
                parm.setData("VALID_DATE", i,
                             parm.getValue("VALID_DATE", i).substring(0, 10).
                             replace("-", "/"));
            }
        }
        date.setData("TABLE", parm.getData());
        // ��β����
        date.setData("CHECK", "TEXT", "��ˣ� ");
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());

        date.setData("TOT", "TEXT",
                     "�ܽ�" + StringTool.round(sum_money, 2));
        //System.out.println("date==="+date);
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupDispensetInv.jhw",
                             date);
    }

    
    /**
     * ��ӡ���������ⵥ
     */
    private void onPrintPack() {
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("SUPDNO", "TEXT", this.getValueString("DISPENSE_NO"));
        date.setData("DDATE", "TEXT",this.getValueString("CHECK_DATE").substring(0, 10).
                     replace("-", "/"));
        date.setData("CDATE", "TEXT",SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace("-", "/"));
        date.setData("DDEPT", "TEXT",this.getTextFormat("FROM_ORG_CODE").getText());
        date.setData("SDEPT", "TEXT",this.getTextFormat("TO_ORG_CODE").getText());
//        date.setData("PACK_MODE", "TEXT",
//                     "���ⷽʽ: " + this.getComboBox("PACK_MODE").getSelectedName());
//        date.setData("SUPTYPE_CODE", "TEXT",
//                     "�������: " + this.getTextFormat("SUPTYPE_CODE").getText());
        // �������
      //�������
        TParm tableParm = new TParm();
		for(int i=0;i<tablePack.getRowCount();i++){
			tableParm.addData("BARCODE", tablePack.getItemString(i, "BARCODE"));
			tableParm.addData("INV_CHN_DESC", tablePack.getItemString(i, "INV_CHN_DESC"));
			tableParm.addData("PACK_BATCH_NO",  tablePack.getItemString(i, "PACK_BATCH_NO"));
			tableParm.addData("COST_PRICE", tablePack.getItemString(i, "COST_PRICE"));
			tableParm.addData("QTY", tablePack.getItemString(i, "QTY"));
			
			String sss = tablePack.getItemString(i, "COST_PRICE").toString();
			String mmm = tablePack.getItemString(i, "QTY").toString();
			
			double sumRow = Double.parseDouble(tablePack.getItemString(i, "COST_PRICE").toString())*Double.parseDouble(tablePack.getItemString(i, "QTY").toString());
			
			tableParm.addData("SUM", sumRow);
			
		}
		tableParm.setCount(tableParm.getCount("BARCODE"));
		tableParm.addData("SYSTEM", "COLUMNS", "BARCODE");
		tableParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "PACK_BATCH_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		tableParm.addData("SYSTEM", "COLUMNS", "QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "SUM");
        date.setData("TABLE", tableParm.getData());
        // ��β����
        date.setData("OPTUSER", "TEXT", Operator.getName());

        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupDispensetPackNew.jhw",
                             date);
    }
    
    
//    /**
//     * ��ӡ���������ⵥ
//     */
//    private void onPrintPack() {
//        // ��ӡ����
//        TParm date = new TParm();
//        // ��ͷ����
//        date.setData("TITLE", "TEXT", Manager.getOrganization().
//                     getHospitalCHNFullName(Operator.getRegion()) +
//                     "��Ӧ�ҳ��ⵥ");
//        date.setData("DISPENSE_NO", "TEXT",
//                     "���ⵥ��: " + this.getValueString("DISPENSE_NO"));
//        date.setData("DISPENSE_DATE", "TEXT",
//                     "��������: " +
//                     this.getValueString("CHECK_DATE").substring(0, 19).
//                     replace("-", "/"));
//        date.setData("DATE", "TEXT",
//                     "�Ʊ�����: " +
//                     SystemTool.getInstance().getDate().toString().substring(0, 19).
//                     replace("-", "/"));
//        date.setData("FROM_ORG_CODE", "TEXT",
//                     "���ⲿ��: " + this.getTextFormat("FROM_ORG_CODE").getText());
//        date.setData("TO_ORG_CODE", "TEXT",
//                     "��ⲿ��: " + this.getTextFormat("TO_ORG_CODE").getText());
//        date.setData("PACK_MODE", "TEXT",
//                     "���ⷽʽ: " + this.getComboBox("PACK_MODE").getSelectedName());
//        date.setData("SUPTYPE_CODE", "TEXT",
//                     "�������: " + this.getTextFormat("SUPTYPE_CODE").getText());
//        // �������
//        String dispense_no = this.getValueString("DISPENSE_NO");
//        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
//            getInvSupDispensePackForPrint(dispense_no)));
//        parm.setCount(parm.getCount("PACK_DESC"));
//        parm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
//        parm.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
//        parm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
//        parm.addData("SYSTEM", "COLUMNS", "QTY");
//        parm.addData("SYSTEM", "COLUMNS", "AMT");
//        date.setData("TABLE", parm.getData());
//        // ��β����
//        date.setData("CHECK", "TEXT", "��ˣ� ");
//        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
//        double sum_money = 0;
//        for (int i = 0; i < parm.getCount("PACK_DESC"); i++) {
//            sum_money += parm.getDouble("AMT", i);
//        }
//        date.setData("TOT", "TEXT",
//                     "�ܽ�" + StringTool.round(sum_money, 2));
//        //System.out.println("date==="+date);
//        // ���ô�ӡ����
//        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupDispensetPack.jhw",
//                             date);
//    }

    /**
     * �������¼�
     */
    public void onTableMClicked() {
        pack_mode = tableM.getParmValue().getValue("PACK_MODE",
            tableM.getSelectedRow());
        tableInv.setSelectionMode(-1);
        tablePack.setSelectionMode(-1);
        if (!"".equals(tableM.getItemString(tableM.getSelectedRow(),
                                            "DISPENSE_NO"))) {
            TParm parm = tableM.getParmValue().getRow(tableM.getSelectedRow());
            this.setValueForParm(
                "DISPENSE_NO;CHECK_USER;REQUEST_NO;CHECK_DATE;"
                + "FROM_ORG_CODE;TO_ORG_CODE;REQUEST_TYPE;"
                + "PACK_MODE;SUPTYPE_CODE;REN_CODE;IN_ORG_CODE", parm);
            
            this.setValue("MLOCATION", parm.getData("MATERIAL_LOCATION"));
            this.setValue("TO_ORG_CODE", parm.getData("IN_ORG_CODE"));
            this.setValue("IN_ORG_CODE", parm.getData("TO_ORG_CODE"));//20150127 wangjingchu add
            
            if ("0".equals(pack_mode)) {
                tableInv.setVisible(true);
                tablePack.setVisible(false);
                TParm parmInv = InvSupDispenseDTool.getInstance().onQueryInv(
                    parm);
                tableInv.setParmValue(parmInv);
            }
            else {
                //System.out.println("-----------");
                tableInv.setVisible(false);
                tablePack.setVisible(true);
                TParm parmPack = InvSupDispenseDTool.getInstance().onQueryPack(
                    parm);
                tablePack.setParmValue(parmPack);
            }
        }
    }

    /**
     * ��������񵥻��¼�
     */
    public void onTableD2clicked() {
        TParm parm = tablePack.getParmValue().getRow(tablePack.getSelectedRow());
        String org_code = this.getValueString("FROM_ORG_CODE");
        String pack_code = parm.getValue("INV_CODE");
        int pack_seq_no = parm.getInt("INVSEQ_NO");
        int batch_no = parm.getInt("PACK_BATCH_NO");
        double qty = parm.getDouble("QTY");
        TParm tableDDParm = new TParm();
        if ("".equals(this.getValueString("DISPENSE_NO"))) {

//2013-12-26ע��            tableDDParm = new TParm(TJDODBTool.getInstance().select(
//                INVSQL.getINVPackStockDInfoDisp(org_code, pack_code, pack_seq_no, qty, batch_no)));
            tableDDParm = new TParm(TJDODBTool.getInstance().select(
                    INVSUPSQL.getINVPackStockDInfoDisp(org_code, pack_code, pack_seq_no, qty, batch_no)));
        }
        else {
            tableDDParm = new TParm(TJDODBTool.getInstance().select(
                INVSQL.getINVSupDispenseDDInfo(getValueString("DISPENSE_NO"),
                                               pack_code,pack_seq_no,batch_no)));
        }
        tableDD.setParmValue(tableDDParm);
    }

    /**
     * �������쵥
     */
    public void onExport() {
        Object obj = this.openDialog(
            "%ROOT%\\config\\inv\\INVSuprequestChoose.x");
        if (obj == null)
            return;
        TParm parm = (TParm) obj;
        pack_mode = parm.getValue("PACK_MODE");
        // ������ⵥ���������
        onSetTableMData(parm);
        if ("0".equals(pack_mode)) {
            // ����һ��������쵥���ص�����
            onSetTableInvData(parm);
        }
        else {
        	// ע�ⲿ�ֳ������ϸ���治������!
            // �����������������쵥���ص�����
            onSetTablePackData(parm);
        }
        TParm tableMParm = tableM.getParmValue().getRow(0);
        
        
        this.setValueForParm("DISPENSE_NO;SUPTYPE_CODE;"
                             + "FROM_ORG_CODE;REQUEST_TYPE;CHECK_DATE"
                             + ";CHECK_USER;REQUEST_NO;PACK_MODE;REN_CODE", tableMParm);
        this.setValue("TO_ORG_CODE", tableMParm.getValue("IN_ORG_CODE"));
        this.setValue("IN_ORG_CODE", tableMParm.getValue("TO_ORG_CODE"));
        
        //20131119�����λstart
        String outDept = tableMParm.getValue("FROM_ORG_CODE");	//��Ӧ���ţ���Ӧ�ң�
        String inDept = tableMParm.getValue("TO_ORG_CODE");		//���첿�ţ����룩
        
//      String sql = getOutDeptInfoSql(outDept);
        TParm tp = new TParm(TJDODBTool.getInstance().select(getOutDeptInfoSql(outDept)));
        
        if(null!=tp && tp.getCount("CON_FLG")>0){
        	
        	if(null==tp.getValue("CON_FLG", 0) || "".equals(tp.getValue("CON_FLG", 0)) || "N".equals(tp.getValue("CON_FLG", 0)) ){
        		this.setValue("TO_ORG_CODE", tableMParm.getValue("TO_ORG_CODE"));
        	}else{
        		this.setValue("TO_ORG_CODE", tableMParm.getValue("FROM_ORG_CODE"));
        		
//        		sql = getMateriallocInfoSql(inDept);
        		TParm temp = new TParm(TJDODBTool.getInstance().select(getMateriallocInfoSql(inDept,outDept)));
        		if(null!=temp && temp.getCount("MATERIAL_LOC_CODE")>0){
        			this.setValue("MLOCATION", temp.getValue("MATERIAL_LOC_CODE", 0));
        		}
        	}

        }
        //20131119�����λend
    }
    

    private String getOutDeptInfoSql(String orgCode){
    	return " SELECT CON_FLG FROM INV_ORG WHERE ORG_CODE = '"+orgCode+"' ";
    }
    
    private String getMateriallocInfoSql(String orgCode, String outDept){
    	return "SELECT MATERIAL_LOC_CODE FROM INV_MATERIALLOC WHERE CORRSUPDEPT = '"+orgCode+"' AND ORG_CODE = '"+outDept+"'";
    }

    /**
     * ������ⵥ���������
     * @param parm TParm
     */
    private void onSetTableMData(TParm parm) {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TParm requestM = parm.getParm("REQUEST_M");
        
        
        //20131122ȷ����ⲿ��start
        String outDept = requestM.getValue("TO_ORG_CODE");		//��Ӧ���ţ���Ӧ�ң�
        
        TParm tp = new TParm(TJDODBTool.getInstance().select(getOutDeptInfoSql(outDept)));
        
        if(null!=tp && tp.getCount("CON_FLG")>0){
        	
        	if(null==tp.getValue("CON_FLG", 0) || "".equals(tp.getValue("CON_FLG", 0)) || "N".equals(tp.getValue("CON_FLG", 0)) ){
        		outDept = requestM.getValue("APP_ORG_CODE");
        	}else{
        		outDept = requestM.getValue("TO_ORG_CODE");
        		
        	}

        }else{
        	outDept = requestM.getValue("APP_ORG_CODE");
        }
        //20131122ȷ����ⲿ��end
        
        
        TParm tableMParm = new TParm();
        tableMParm.addData("DISPENSE_NO", "");
        tableMParm.addData("PACK_MODE", parm.getValue("PACK_MODE"));
        tableMParm.addData("REQUEST_TYPE", "REQ");
        tableMParm.addData("REQUEST_NO", requestM.getValue("REQUEST_NO"));
        tableMParm.addData("REQUEST_DATE", requestM.getTimestamp("REQUEST_DATE"));
        tableMParm.addData("FROM_ORG_CODE", requestM.getValue("TO_ORG_CODE"));
        tableMParm.addData("TO_ORG_CODE", requestM.getValue("APP_ORG_CODE"));
        tableMParm.addData("DISPENSE_DATE", this.getValue("CHECK_DATE"));
        tableMParm.addData("DISPENSE_USER", this.getValueString("CHECK_USER"));
        tableMParm.addData("URGENT_FLG", requestM.getValue("URGENT_FLG"));
        tableMParm.addData("REMARK", requestM.getValue("DESCRIPTION"));
        tableMParm.addData("DISPOSAL_FLG", "N");
        tableMParm.addData("CHECK_DATE", this.getValue("CHECK_DATE"));
        tableMParm.addData("CHECK_USER", this.getValueString("CHECK_USER"));
        tableMParm.addData("REN_CODE", requestM.getValue("REASON_CHN_DESC"));
        tableMParm.addData("FINA_FLG", "N");
        tableMParm.addData("OPT_USER", Operator.getID());
        tableMParm.addData("OPT_DATE", datetime);
        tableMParm.addData("OPT_TERM", Operator.getIP());
        tableMParm.addData("SUPTYPE_CODE", requestM.getValue("SUPTYPE_CODE"));
        
        tableMParm.addData("IN_ORG_CODE", outDept);  //������ⵥλ
        
        tableM.setParmValue(tableMParm);
        tableM.setSelectedRow(0);
    }

    /*
     * ����һ��������쵥���ص�����
     * @param parm TParm
     */
    private void onSetTableInvData(TParm parm) {
        tablePack.setVisible(false);
        tableInv.setVisible(true);
        TParm requestD = parm.getParm("REQUEST_D");
        TParm tableParm = new TParm();
        TParm invParm = new TParm();
        int count = 1;
        for (int i = 0; i < requestD.getCount("INV_CODE"); i++) {
            invParm = requestD.getRow(i);
            invParm.setData("ORG_CODE",
                            parm.getParm("REQUEST_M").getValue("TO_ORG_CODE"));
            if ("Y".equals(requestD.getValue("SEQMAN_FLG", i))) {
                // ��������Ź������
                TParm resultParm = onOpenInvDilog(invParm);
                for (int j = 0; j < resultParm.getCount("QTY"); j++) {
                    resultParm.setData("SEQ_NO", j, invParm.getInt("SEQ_NO"));
                    insertNewRowTableInv(tableParm, resultParm.getRow(j), count);
                    count++;
                }
            }
            else {
                insertNewRowTableInv(tableParm, invParm, count);
                count++;
            }
        }
        //System.out.println("tableParm=="+tableParm);
        // ��ӱ������
        tableInv.setParmValue(tableParm);
    }

    /**
     * ��������Ź���ѡ�����
     * @param parm TParm
     * @return boolean
     */
    private TParm onOpenInvDilog(TParm parm) {
        Object result = openDialog("%ROOT%\\config\\inv\\INVSupInvStockDD.x",
                                   parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return null;
            }
            return addParm;
        }
        return null;
    }

    /**
     * ����һ������
     * @param parm TParm
     */
    private TParm insertNewRowTableInv(TParm tableParm, TParm invParm,
                                       int count) {
        tableParm.addData("DISPENSE_NO", "");
        tableParm.addData("SEQ_NO", count);
        tableParm.addData("PACK_MODE", "0");
        tableParm.addData("INV_CODE", invParm.getValue("INV_CODE"));
        tableParm.addData("INV_CHN_DESC", invParm.getValue("INV_CHN_DESC"));
        tableParm.addData("INVSEQ_NO", invParm.getInt("INVSEQ_NO"));
        tableParm.addData("QTY", invParm.getDouble("QTY"));
        tableParm.addData("STOCK_UNIT", invParm.getValue("STOCK_UNIT"));
        tableParm.addData("COST_PRICE", invParm.getDouble("COST_PRICE"));
        tableParm.addData("REQUEST_SEQ", invParm.getInt("SEQ_NO"));
        tableParm.addData("BATCH_NO", invParm.getValue("BATCH_NO"));
        tableParm.addData("BATCH_SEQ", "");
        tableParm.addData("VALID_DATE", invParm.getData("VALID_DATE"));
        tableParm.addData("DISPOSAL_FLG", "N");
        tableParm.addData("OPT_USER", Operator.getID());
        tableParm.addData("OPT_DATE", SystemTool.getInstance().getDate());
        tableParm.addData("OPT_TERM", Operator.getIP());
        return tableParm;
    }

    /*
     * �����������������쵥���ص�����
     * @param parm TParm
     */
    private void onSetTablePackData(TParm parm) {
        tableInv.setVisible(false);
        tablePack.setVisible(true);
        TParm requestD = parm.getParm("REQUEST_D");
        //System.out.println("requestD==="+requestD);
        TParm tableParm = new TParm();
        TParm packParm = new TParm();
        int count = 1;
        for (int i = 0; i < requestD.getCount("INV_CODE"); i++) {
            packParm = requestD.getRow(i);
            packParm.setData("ORG_CODE",
                             parm.getParm("REQUEST_M").getValue("TO_ORG_CODE"));
            //������
            if ("1".equals(requestD.getValue("SEQ_FLG", i))) {
                // ����������Ź������
                TParm resultParm = onOpenPackDilog(packParm);
                for (int j = 0; j < resultParm.getCount("QTY"); j++) {
                    resultParm.setData("SEQ_NO", j, packParm.getInt("SEQ_NO"));
                    insertNewRowTablePack(tableParm, resultParm.getRow(j),
                                          count);
                    count++;
                }
            }
            //���ư�
            else {
                TParm resultParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvPackCostPrice(parm.getParm("REQUEST_M").
                                               getValue("TO_ORG_CODE"),
                                               packParm.getValue("INV_CODE"))));
                if (resultParm == null || resultParm.getCount() <= 0) {
                    this.messageBox(packParm.getValue("INV_CHN_DESC") + "û�п��");
                    continue;
                }
                packParm.setData("COST_PRICE",
                                 resultParm.getDouble("COST_PRICE", 0));
                
                //��ѯ���������ư���ȫ�����Σ�����>0��
                TParm condition = new TParm();
                condition.setData("ORG_CODE", parm.getParm("REQUEST_M").getValue("TO_ORG_CODE"));
                condition.setData("PACK_CODE", packParm.getValue("INV_CODE"));
                TParm tp = InvSupDispenseMTool.getInstance().queryPackByBatch(condition);
                if (tp.getErrCode() < 0) {
                    err("ERR:" + tp.getErrCode() + tp.getErrText()
                        + tp.getErrName());
                    return ;
                }
                int qty = packParm.getInt("QTY");
                //�����οۿ��
                for(int m=0;m<tp.getCount("BARCODE");m++){
                	if(qty>0){
                		if(tp.getInt("QTY",m)>=qty){
                			packParm.setData("BARCODE", tp.getData("BARCODE", m));
                        	packParm.setData("PACK_BATCH_NO", tp.getInt("PACK_BATCH_NO", m));
                        	packParm.setData("QTY",qty);
                        	insertNewRowTablePack(tableParm, packParm, count);
                            count++;
                            qty = 0;
                            break;
                		}else{
                			packParm.setData("BARCODE", tp.getData("BARCODE", m));
                        	packParm.setData("PACK_BATCH_NO", tp.getInt("PACK_BATCH_NO", m));
                        	packParm.setData("QTY",tp.getInt("QTY",m));
                        	insertNewRowTablePack(tableParm, packParm, count);
                        	count++;
                        	qty = qty - tp.getInt("QTY",m);
                		}
                	}
                }//forѭ������
                if(qty!=0){
                	this.messageBox(packParm.getValue("INV_CHN_DESC") + "��治�㣬ֻ�ܲ��ַ�����");
                    continue;
                }
            }
        }
        // ��ӱ������
        tablePack.setParmValue(tableParm);
    }

    /**
     * ����������Ź���ѡ�����
     * @param parm TParm
     * @return boolean
     */
    private TParm onOpenPackDilog(TParm parm) {
        Object result = openDialog("%ROOT%\\config\\inv\\INVSupPackStockDD.x",
                                   parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return null;
            }
            return addParm;
        }
        return null;
    }

    /**
     * ����һ��������
     * @param parm TParm
     */
    private TParm insertNewRowTablePack(TParm tableParm, TParm invParm,
                                        int count) {
        tableParm.addData("DISPENSE_NO", "");
        tableParm.addData("SEQ_NO", count);
        tableParm.addData("PACK_MODE", "1");
        tableParm.addData("INV_CODE", invParm.getValue("INV_CODE"));
        tableParm.addData("INV_CHN_DESC", invParm.getValue("INV_CHN_DESC"));
        tableParm.addData("INVSEQ_NO", invParm.getInt("INVSEQ_NO"));
        tableParm.addData("QTY", invParm.getDouble("QTY"));
        //tableParm.addData("STOCK_UNIT", "");
        tableParm.addData("COST_PRICE", invParm.getDouble("COST_PRICE"));
        tableParm.addData("REQUEST_SEQ", invParm.getInt("SEQ_NO"));
        //tableParm.addData("BATCH_NO", "");
        //tableParm.addData("BATCH_SEQ", "");
        //tableParm.addData("VALID_DATE", "");
        tableParm.addData("DISPOSAL_FLG", "N");
        tableParm.addData("OPT_USER", Operator.getID());
        tableParm.addData("OPT_DATE", SystemTool.getInstance().getDate());
        tableParm.addData("OPT_TERM", Operator.getIP());
        tableParm.addData("BARCODE", invParm.getValue("BARCODE"));
        tableParm.addData("PACK_BATCH_NO", invParm.getInt("PACK_BATCH_NO"));
        return tableParm;
    }

    /**
     * �����ѡ��ť
     */
    public void onChangeRadioButton() {
        this.clearValue("PACK_MODE_Q;DISPENSE_NO_Q;FROM_ORG_CODE_Q;"
                        +
                        "TO_ORG_CODE_Q;START_DATE;END_DATE;DISPENSE_NO;REQUEST_NO;"
                        +
                        "FROM_ORG_CODE;TO_ORG_CODE;REQUEST_TYPE;PACK_MODE;SUPTYPE_CODE;"
                        + "REN_CODE");
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("CHECK_DATE", date);
        tableM.removeRowAll();
        tableInv.removeRowAll();
        tablePack.removeRowAll();
        tableDD.removeRowAll();
        tableInv.setVisible(true);
        tablePack.setVisible(false);

        if (getRadioButton("UPDATE_B").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("import")).setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("import")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableInvChangeValue(Object obj) {
        tableInv.acceptText();
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("������������С�ڻ����0");
                return true;
            }
            double invseq_no = tableInv.getItemDouble(tableInv.getSelectedRow(),
                                                "INVSEQ_NO");
            if (invseq_no != 0 && qty != 1) {
                this.messageBox("����������������ܴ���1");
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTablePackChangeValue(Object obj) {
        tablePack.acceptText();
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("������������С�ڻ����0");
                return true;
            }
            double invseq_no = tablePack.getItemDouble(tablePack.getSelectedRow(),
                "INVSEQ_NO");
            if (invseq_no != 0 && qty != 1) {
                this.messageBox("������������������ܴ���1");
                return true;
            }
            return false;
        }
        return true;
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
     * �õ�TRadioButton����
     * @param tagName String
     * @return TRadioButton
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
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
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }
    /**
	 * ɨ������
	 */
	public void onScream() {
		if(this.getValue("FROM_ORG_CODE").equals("")){
			this.messageBox("����ѡ��Ӧ���ţ�");
			return;
		}
		if(this.getValue("SUPTYPE_CODE").equals("")){
			this.messageBox("����ѡ���������");
			return;
		}
		if(!this.getValue("SUPTYPE_CODE").equals("100001")){
			this.messageBox("�������Ϊ������������ʱ�ſ����������ţ�");
			return;
		}

		String barcode = this.getValueString("SCREAM");
		if(barcode==null&&barcode.length()<0){
			return;
		}
        String sql = "SELECT 'N' AS SELECT_FLG, A.PACK_SEQ_NO, "
            + " A.PACK_CODE,B.PACK_DESC, " 
            + " A.USE_COST + A.ONCE_USE_COST AS COST_PRICE, A.ORG_CODE, A.BARCODE, A.PACK_BATCH_NO "
            + " FROM INV_PACKSTOCKM A,INV_PACKM B "
            + " WHERE A.BARCODE = '" + barcode +"' AND A.STATUS = '9' "
            + " AND B.PACK_CODE=A.PACK_CODE "
            + " ORDER BY A.PACK_SEQ_NO"; 
    TParm obj2 = new TParm(TJDODBTool.getInstance().select(sql));

    if (obj2.getCount("PACK_SEQ_NO") < 0) {
		this.messageBox("û�в�ѯ����Ӧ��¼");
		return;
	}
        TParm parm =onReturn(obj2);
//        pack_mode = parm.getValue("PACK_MODE");

        TParm tableD2ParmOrld = tablePack.getParmValue();
        ArrayList<String> list = new ArrayList<String>();
        String warmStr = "";
        for(int i=0;i<parm.getCount("INVSEQ_NO");i++){
        	for(int j=0;j<tableD2ParmOrld.getCount("INVSEQ_NO");j++){
        		if(parm.getValue("INVSEQ_NO", i).equals(tableD2ParmOrld.getValue("INVSEQ_NO", j))
        				&& parm.getValue("INV_CHN_DESC", i).equals(tableD2ParmOrld.getValue("INV_CHN_DESC", j))){
        			if(warmStr.equals("")){
        				warmStr += parm.getValue("INVSEQ_NO", i);
        			}else{
        				warmStr += ","+parm.getValue("INVSEQ_NO", i);
        			}
        			list.add(parm.getValue("INVSEQ_NO", i));
        		}
        	}
        }
        if(list.size()>0){
        	this.messageBox("��������ţ�"+warmStr+"֮ǰ�����");
        }
        // ע�ⲿ�ֳ������ϸ���治������!
        // �����������������쵥���ص�����
        onSetTablePackDataNew(parm,list);
        tablePack.addRow();
		((TTextField)getComponent("SCREAM")).setValue("");
	}

    /**
     * ���ط���
     */
    public TParm onReturn(TParm tParm) {
        TParm result = tParm;
        TParm resultParm = new TParm();
//        for (int i = result.getCount("PACK_SEQ_NO") - 1; i >= 0; i--) {

        	     resultParm.addData("INVSEQ_NO",
        	    		 result.getValue("PACK_SEQ_NO", 0));
			     resultParm.addData("ORG_CODE",
			                        result.getValue("ORG_CODE",0));
			     //����
			     resultParm.addData("COST_PRICE",
			                        result.getDouble("COST_PRICE",0));
			     //����
			     resultParm.addData("QTY", 1);
			     
			     resultParm.addData("INV_CODE", result.getValue("PACK_CODE",0));
			     //����������
			     resultParm.addData("INV_CHN_DESC", result.getValue("PACK_DESC",0));
			     resultParm.addData("BARCODE", result.getValue("BARCODE",0));
			     //����
			     resultParm.addData("PACK_BATCH_NO", result.getInt("PACK_BATCH_NO",0));
            
//        }
        double return_qty = resultParm.getCount("INV_CODE");
        if (return_qty <= 0) {
            this.messageBox("û��ѡ������");
            return null;
        }
//        if (return_qty > qty) {
//            this.messageBox("ѡ������������������");
//            return;
//        }
        return resultParm;
    }


}
