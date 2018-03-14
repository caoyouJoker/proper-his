package com.javahis.ui.inv;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.inv.INVRegressGoodTool;
import jdo.inv.INVSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �������ü�¼
 * </p>
 * 
 * <p>
 * Description: �������ü�¼
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) BlueCore 2013
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author zhangh
 * @version 1.0
 */
public class INVBaseQtyControl extends TControl {

	TTable table;
	TTable tablem;

	/**
	 * ��ʼ��
	 * */
	public void onInit() {
		
		this.table = (TTable) this.getComponent("TABLED");
		this.tablem = (TTable) this.getComponent("TABLED");
		this.setValue("ORG_CODE", Operator.getDept());
		this.setValue("OPT_USER", Operator.getID());
		this.setValue("BASE_DATE", new Date());
		this.setValue("ORG_CODE_Q", Operator.getDept());
		
		
		
		 Timestamp date = StringTool.getTimestamp(new Date());
	        // ��ʼ����ѯ����
	        this.setValue("END_DATE",
	                      date.toString().substring(0, 10).replace('-', '/') +
	                      " 23:59:59");
	        this.setValue("START_DATE",
	                      StringTool.rollDate(date, -7).toString().substring(0, 10).
	                      replace('-', '/') + " 00:00:00");
		
		

		callFunction("UI|INV_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "getBarCode");
		// ע�ἤ��INDSupOrder�������¼�
        table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                 this, "onCreateEditComoponentUD");
        
        createNewRow();
	}
	
	
	  /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        int row = tablem.getSelectedRow();
        
        if (row != -1) {
            table.setSelectionMode(0);
            // ������Ϣ(TABLE��ȡ��)
            setValue("BASE_NO", tablem.getItemString(row, "BASE_NO"));
            setValue("BASE_DATE", tablem.getItemTimestamp(row,
                "BASE_DATE"));
            setValue("ORG_CODE", tablem.getItemString(row, "ORG_CODE"));

            // ��ϸ��Ϣ
            TParm parm = new TParm();
            parm.setData("BASE_NO",
                         tablem.getItemString(row, "BASE_NO"));
            String sql="select a.INV_CODE ,b.INV_CHN_DESC,b.DESCRIPTION,c.UNIT_CHN_DESC UNIT,a.QTY,a.old_qty from inv_baseqty a " +
            		" left join inv_base b on b.inv_code=a.inv_code" +
            		" left join sys_unit c on c.UNIT_CODE=b.stock_unit  " +
            		" where a.base_no='"+ tablem.getItemString(row, "BASE_NO")+"'" +
            				" group by a.seq ";
           TParm  result=new TParm(TJDODBTool.getInstance().select(sql));
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û��������ϸ");
                return;
            }
            table.removeRowAll();
            table.setParmValue(result);
            
            
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);

        }
    }
	
	
    /**
     * ��TABLE�����༭�ؼ�ʱ����
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
   
        TParm parm = new TParm();
        parm.setData("ORG_CODE", Operator.getDept());
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }
    
    
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        table.acceptText();
        String inv_desc = parm.getValue("INV_CHN_DESC");
        String inv_code = parm.getValue("INV_CODE");
        String d = parm.getValue("DESCRIPTION");
        String ui = parm.getValue("STOCK_UNIT");
        String qty="";
        if(ui!=null&&ui.length()>=1){
        String sqlString="select a.base_qty,c.UNIT_CHN_DESC from inv_stockm a " +
        		"left join inv_base b on  a.inv_code=b.inv_code" +
        		" left join sys_unit c on c.UNIT_CODE=b.stock_unit  " +
        		"  where a.inv_code='"+inv_code+"'" +
        				" and a.org_code='"+Operator.getDept()+"'";
    	TParm parm1 = new TParm(TJDODBTool.getInstance().select(sqlString));
    	System.out.println(sqlString);
    	if (parm1==null||parm1.getCount()<=0) {
			messageBox("�ÿ��Ҳ����� �����ʣ���˶Ժ����룡");
			return;
		}
    	ui=parm1.getData("UNIT_CHN_DESC", 0).toString();
    	qty=parm1.getData("BASE_QTY", 0).toString();
        }
        if (!StringUtil.isNullString(inv_desc)) {
            // ������Ź���������Ƿ��ظ�
//            if ("N".equals(seqmain_flg)) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    if ("".equals(table.getItemString(i, "INV_CODE"))) {
                        continue;
                    }
                    else if (table.getSelectedRow() == i) {
                        continue;
                    }
                    else {
                        if (inv_code.equals(table.getItemString(i, "INV_CODE")) ) {
                            this.messageBox("���������ظ�");
                            table.removeRow(i);
                            createNewRow();
                            return;
                        }
                    }
                }
//            }
            table.setItem(table.getSelectedRow(), "INV_CODE", inv_code);
            table.setItem(table.getSelectedRow(), "INV_CHN_DESC", inv_desc);
            table.setItem(table.getSelectedRow(), "DESCRIPTION", d);
            table.setItem(table.getSelectedRow(), "QTY",1);
            table.setItem(table.getSelectedRow(), "OLD_QTY",qty);
            table.setItem(table.getSelectedRow(), "UNIT",ui);
            if (table.getRowCount() == table.getSelectedRow() + 1) { 
                createNewRow();
            }
        }
    }
    
    /**
     * ����ϸ��������
     * @return int
     */
    private int createNewRow() {
        int row = table.addRow();
        return row;
    }
    
    /**
     * �򿪽���
     */
	public void onExport1(){
		
		  
          TParm parm = new TParm();
          Object result = openDialog("%ROOT%\\config\\inv\\INVSingleUseSelect.x", parm);
          if (result != null) {
        	  table.removeRow(table.getRowCount()-1);
              TParm addParm = (TParm) result;
              if (addParm == null) {
                  return;}
    		for (int i = 0; i < addParm.getCount("INV_CODE"); i++) {
    			 TParm mainParm = new TParm();
    			mainParm.setData("INV_CODE", addParm.getValue("INV_CODE", i));
    			mainParm.setData("UNIT", addParm.getValue("UNIT_DESC", i));
    			mainParm.setData("INV_CHN_DESC", addParm.getValue("INV_CHN_DESC", i));
    			mainParm.setData("DESCRIPTION", addParm.getValue("DESCRIPTION", i));
//    			mainParm.setData("BATCH_NO", parm1.getValue("BATCH_NO", i));
    			mainParm.setData("QTY", 1);
    			mainParm.setData("UP_SUP_CODE", addParm.getValue("UP_SUP_CODE", i));
    			mainParm.setData("MAN_CODE", addParm.getValue("MAN_CODE", i));
    			mainParm.setData("ORG_CODE", Operator.getDept());
    			table.addRow(mainParm);
    		}
    		createNewRow();
          }
    } 


	/**
	 * �õ������
	 * */
	public void getBarCode() {
		callFunction("UI|save|setEnabled", true);
		if (getValueString("INV_CODE") != null
				&& getValueString("INV_CODE").trim().length() > 0) {
			getTextField("INV_CODE").grabFocus();
		} else {
			return;
		}
		String barCode = this.getValueString("INV_CODE").toUpperCase();
		   for (int i = 0; i < table.getRowCount(); i++) {
               if ("".equals(table.getItemString(i, "INV_CODE"))) {
                   continue;
               }
               else if (table.getSelectedRow() == i) {
                   continue;
               }
               else {
                   if (barCode.equals(table.getItemString(i, "INV_CODE")) ) {
                       this.messageBox("���������ظ�");
                       return;
                   }
               }
           }
		
		
		
		String sql1 = "";
		TParm parm1 = new TParm();
		int stockQty = 0;
			
			sql1 = " SELECT A.INV_CODE,A.INV_CHN_DESC,A.DESCRIPTION,A.UP_SUP_CODE,A.MAN_CODE,'' AS QTY,d.UNIT_CHN_DESC UNIT,m.base_qty  " +
				   " FROM INV_BASE A " +
				   " left join SYS_UNIT d on A.STOCK_UNIT= d.UNIT_CODE" +
				   " LEFT JOIN SYS_MANUFACTURER B ON B.MAN_CODE = A.MAN_CODE" +
				   " left join inv_stockm m on m.inv_code=a.inv_code and m.org_code='"+Operator.getDept()+"' " +
			 	   " WHERE  A.INV_CODE='" + barCode +"'";
		//	System.out.println(sql1);
			parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		if (parm1.getCount("INV_CODE") <= 0) {
			messageBox("����ı�Ż�ÿ��Ҳ����ڸ����ʣ�����ɨ��");
			return;
		}
		TParm mainParm = new TParm();
		for (int i = 0; i < parm1.getCount("INV_CODE"); i++) {
			mainParm.setData("INV_CODE", parm1.getValue("INV_CODE", i));
			mainParm.setData("UNIT", parm1.getValue("UNIT", i));
			mainParm.setData("INV_CHN_DESC", parm1.getValue("INV_CHN_DESC", i));
			mainParm.setData("DESCRIPTION", parm1.getValue("DESCRIPTION", i));
//			mainParm.setData("BATCH_NO", parm1.getValue("BATCH_NO", i));
				mainParm.setData("QTY", stockQty + 1);
				mainParm.setData("OLD_QTY", parm1.getValue("BASE_QTY", i));
			mainParm.setData("UP_SUP_CODE", parm1.getValue("UP_SUP_CODE", i));
			mainParm.setData("MAN_CODE", parm1.getValue("MAN_CODE", i));
			mainParm.setData("ORG_CODE", Operator.getDept());
		}
//		table.removeRowAll();
		table.removeRow(table.getRowCount()-1);
		table.addRow(mainParm);
		
		
		//setQtyIsLock(isHigh);
		this.setValue("INV_CODE", "");
		createNewRow();
	}

	//����Ǹ�ֵ����ѱ����QTY����ס��������ǣ���ſ�
	private void setQtyIsLock(boolean isHigh) {
		if(isHigh == true){
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setLockCell(i, 4, true);
			}
		}else{
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setLockCell(i, 4, false);
			}
		}
	}
	Map<String,String> map=new HashMap<String,String>();

	/**
	 * ����
	 * */
	public void onSave() {
		 map=new HashMap<String,String>();
		table.acceptText();
//		TParm parm = new TParm();// �õ�¼������Ϊ����������
		TParm invRegressParm = new TParm();// �����˻�����
		TParm invStockParm = new TParm();// �ۿ����
		
		TParm tableParm = table.getShowParmValue();
		int count = tableParm.getCount("INV_CODE");
		if(count <= 0){
			this.messageBox("�ޱ������ݣ�");
			return;
		}
		String returnNo = SystemTool.getInstance().getNo("ALL",
				"INV", "INV_BASEQTY", "No");
//		HashMap<String, Double> invMap = new HashMap<String, Double>();
		
		for (int i = 0; i < count; i++) {
			TParm parmV = tableParm.getRow(i);
//			TParm returnTparm = INVRegressGoodTool.getInstance().selInvReturnMaxSeq();
			//����ȡ��ԭ��õ��˻����
			if ("".equals(parmV.getValue("INV_CODE"))||null==parmV.getValue("INV_CODE")||parmV.getValue("INV_CODE").length()<=0) {
				continue;
				
			}
			invRegressParm.addData("BASE_NO", returnNo);
			invRegressParm.addData("SEQ", i + 1);
			invRegressParm.addData("INV_CODE", parmV.getValue("INV_CODE"));
			invRegressParm.addData("BASE_USER", Operator.getID());
			invRegressParm.addData("BASE_DATE", getValue("BASE_DATE"));
			invRegressParm.addData("ORG_CODE", Operator.getDept());
			invRegressParm.addData("REASON", "");
			invRegressParm.addData("OPT_USER", Operator.getID());
			invRegressParm.addData("OPT_TERM", Operator.getIP());
			invRegressParm.addData("QTY", parmV.getInt("QTY"));
			invRegressParm.addData("OLD_QTY", parmV.getInt("OLD_QTY"));
			
			
			if (parmV.getInt("QTY")>0) {
				invStockParm.addData("INV", "add");
			}else {
				invStockParm.addData("INV", "sub");
			}
			invStockParm.addData("INV_CODE", parmV.getData("INV_CODE"));
			invStockParm.addData("QTY", parmV.getData("QTY"));
			invStockParm.addData("ORG_CODE", Operator.getDept());  
			invStockParm.addData("RFID", "");
			invStockParm.addData("BATCH_NO", parmV.getData("BATCH_NO"));
			invStockParm.addData("VALID_DATE", parmV.getData("VALID_DATE"));
			invStockParm.addData("OPT_USER", Operator.getID());
			invStockParm.addData("OPT_TERM", Operator.getIP());
			invStockParm.addData("FLG", "LOW");
			

		
			}
		TParm Main = new TParm();
		Main.setData("INVBaseQty", invRegressParm.getData());// �˻���ϸ�����
		Main.setData("INVStock", invStockParm.getData());// �ۿ����
		TParm result = TIOM_AppServer.executeAction(
				"action.inv.INVRegressGoodAction", "onSaveForBaseQty", Main);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		}
		this.messageBox("����ɹ���");
		callFunction("UI|save|setEnabled", false);
		onClear();
	}
	
	/**
	 * ���
	 * */
	public void onClear() {
		table.clearSelection();
		table.removeRowAll();
		callFunction("UI|save|setEnabled", true);
		//((TTextFormat)this.getComponent("INV_CODE")).setEnabled(true);
		this.clearValue("INV_CODE");
		this.setValue("ORG_CODE", Operator.getDept());
		this.setValue("OPT_USER", Operator.getID());
		this.setValue("USE_DATE", new Date());
		
		//this.addDefaultRowForTable("TABLE");
		this.setValue("INV_CODE", "");
		//table.removeRowAll();
		//this.addDefaultRowForTable("TABLE");
		createNewRow();
	}

	/**
	 * ����Ĭ����
	 * 
	 * @param tableName
	 *            String
	 */
	private void addDefaultRowForTable(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		table.acceptText();
		TParm tableParm = table.getParmValue();
		if (table.equals(tableName)) {
			if (tableParm.getCount("RFID") > 0
					&& "".equals(tableParm.getValue("RFID",
							(tableParm.getCount("RFID") - 1)).trim())) {
				return;
			}
			// Ĭ�ϼ���һ��������
			tableParm.addData("INV_CODE", "");
			tableParm.addData("INV_CHN_DESC", "");
			tableParm.addData("RFID", "");
			tableParm.addData("DESCRIPTION", "");
			tableParm.addData("QTY", "");
			tableParm.addData("SUP_CODE", "");
			tableParm.addData("ORG_CODE", "");
		}
		table.setParmValue(tableParm);
	}

	/**
	 * �õ�TTextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
	
	/*
     * ����excel����
     */
    public void onExcel(){
    	if(table.getRowCount() <= 0){
			this.messageBox("û�����ݣ�");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(table, "�����˻�");
    }
    
    /*
     * ɾ������
     */
    public void onDelete(){
    	TParm parm = table.getShowParmValue();
		if (parm.getCount("INV_CODE") <= 0) {
			this.messageBox("û��Ҫɾ�������ݣ���ѡ��Ҫɾ�����У�");
			return;
		}
		table.removeRow(table.getSelectedRow());
    }
    
    /*
     * ��ӡ����
     */
    public void onPrint(){
		if (table.getRowCount() <= 0) {
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		}
		TParm tableData = table.getShowParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("RFID", tableData.getData("RFID", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("UP_SUP_CODE", tableData.getData("UP_SUP_CODE", i));
			printData.addData("MAN_CODE", tableData.getData("MAN_CODE", i));
			printData.addData("ORG_CODE", tableData.getData("ORG_CODE", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "RFID");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "UP_SUP_CODE");
		printData.addData("SYSTEM", "COLUMNS", "MAN_CODE");
		printData.addData("SYSTEM", "COLUMNS", "ORG_CODE");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "������ϸ");
		printParm.setData("DATE", "TEXT", "�Ʊ�ʱ�䣺" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVRegressGood.jhw",
				printParm);
    }
    
    /**
     * ��ѯ���˻��Żس��¼�
     */
    public void onQuery(){
    	String returnNo = this.getValueString("NO_Q");
    	String supCode = this.getValueString("ORG_CODE_Q");
    	TParm parm = new TParm();
    	if(returnNo != null && !"".equals(returnNo.trim())){
    		parm.setData("BASE_NO", returnNo);	
    	}
    	if(supCode != null && !"".equals(supCode.trim())){
    		parm.setData("ORG_CODE_Q", supCode);
    	}
    	TParm result = INVRegressGoodTool.getInstance().onQueryForBaseQty(parm);
    	if(result.getCount("BASE_NO") <= 0){
    		this.messageBox("��ѯʧ�ܣ�");
    		return;
    	}
    	tablem.setParmValue(result);
    	callFunction("UI|save|setEnabled", false);
    }
    
    /**
     * ȡ�ÿ����ϸ������(INV_STOCKD)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertInvStockDData(TParm parm) {
        TParm stockD = new TParm();
        TParm parmD = parm.getParm("VER_D");
        String org_code ="";
    
        Timestamp date = SystemTool.getInstance().getDate();
        String inv_code = "";
        String batch_no = "";
        String valid_date = "";
        int batch_seq = 0;
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            inv_code = parmD.getValue("INV_CODE", i);
            batch_no = parmD.getValue("BATCH_NO", i);
            valid_date = parmD.getValue("VALID_DATE", i);
            TParm stockDParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvBatchSeq(org_code, inv_code, batch_no, valid_date)));
            if (stockDParm.getCount("BATCH_SEQ") > 0) {
                stockD.addData("FLG", "UPDATE");
                batch_seq = stockDParm.getInt("BATCH_SEQ", i);
            }
            else {
                stockD.addData("FLG", "INSERT");
                // ץȡ���BATCH_SEQ+1
                TParm batchSeqParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvStockMaxBatchSeq(org_code, inv_code)));
//                System.out.println("===========bat====="+org_code+"00"+inv_code);
//                System.out.println("===========bat====="+batchSeqParm);
                if (batchSeqParm == null || batchSeqParm.getCount() <= 0) {
                    batch_seq = 1;
                }
                else {
                    batch_seq = batchSeqParm.getInt("BATCH_SEQ", 0) + 1;
                }
            }
            stockD.addData("ORG_CODE", org_code);
            stockD.addData("INV_CODE", inv_code);
            stockD.addData("BATCH_SEQ", batch_seq);
            
            
            //Ϊ��������ⵥbatch seq
            
            stockD.addData("REGION_CODE", Operator.getRegion());
            stockD.addData("BATCH_NO", parmD.getValue("BATCH_NO", i));
            stockD.addData("VALID_DATE", parmD.getData("VALID_DATE", i));
          
            stockD.addData("LASTDAY_TOLSTOCK_QTY", 0);
            stockD.addData("DAYIN_QTY", parmD.getDouble("IN_QTY", i));
            stockD.addData("DAYOUT_QTY", 0);
            stockD.addData("DAY_CHECKMODI_QTY", 0);
            stockD.addData("DAY_VERIFYIN_QTY", parmD.getDouble("IN_QTY", i));
            stockD.addData("DAY_VERIFYIN_AMT",
                           parmD.getDouble("QTY", i) *
                           parmD.getDouble("UNIT_PRICE", i));
            stockD.addData("GIFTIN_QTY", parmD.getDouble("GIFT_QTY", i));
            stockD.addData("DAY_REGRESSGOODS_QTY", 0);
            stockD.addData("DAY_REGRESSGOODS_AMT", 0);
            stockD.addData("DAY_REQUESTIN_QTY", 0);
            stockD.addData("DAY_REQUESTOUT_QTY", 0);
            stockD.addData("DAY_CHANGEIN_QTY", 0);
            stockD.addData("DAY_CHANGEOUT_QTY", 0);
            stockD.addData("DAY_TRANSMITIN_QTY", 0);
            stockD.addData("DAY_TRANSMITOUT_QTY", 0);
            stockD.addData("DAY_WASTE_QTY", 0);
            stockD.addData("DAY_DISPENSE_QTY", 0);
            stockD.addData("DAY_REGRESS_QTY", 0);
            stockD.addData("FREEZE_TOT", 0);
            stockD.addData("UNIT_PRICE", parmD.getDouble("UNIT_PRICE", i));
            stockD.addData("STOCK_UNIT", parmD.getValue("STOCK_UNIT", i));
            stockD.addData("OPT_USER", Operator.getID());
            stockD.addData("OPT_DATE", date);
            stockD.addData("OPT_TERM", Operator.getIP());
        }
        System.out.println("stockD.getData()=========="+stockD.getData());
        parm.setData("STOCK_D", stockD.getData());
        return parm;
    }
}
