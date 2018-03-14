package com.javahis.ui.spc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:�½��վ�̬����ѯcontrol
 * </p>
 *  
 * <p>
 * Description: �½��վ�̬����ѯcontrol
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2015
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author wangjc 20150211
 * @version 1.0
 */
public class SPCDateStockQueryControl extends TControl {

	//���ڸ�ʽ��
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyyMMdd");
	//��ȡTCheckBox���
	private TCheckBox check;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		String sql = "SELECT MONTH_CYCLE FROM BIL_SYSPARM WHERE ADM_TYPE = 'I' ";
		TParm monthCycleParm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println(monthCycleParm.getInt("MONTH_CYCLE", 0));
		TTextFormat date = this.getTextFormat("TRANDATE");
		Calendar cdto = Calendar.getInstance();
		if(cdto.get(Calendar.DAY_OF_MONTH) < monthCycleParm.getInt("MONTH_CYCLE", 0)){
			if(cdto.MONTH == 1){
				cdto.add(Calendar.YEAR, -1);
				cdto.add(Calendar.MONTH, 11);
				cdto.add(Calendar.DAY_OF_MONTH, monthCycleParm.getInt("MONTH_CYCLE", 0)-cdto.get(Calendar.DAY_OF_MONTH));
			}else{
				cdto.add(Calendar.MONTH, -1);
				cdto.add(Calendar.DAY_OF_MONTH, monthCycleParm.getInt("MONTH_CYCLE", 0)-cdto.get(Calendar.DAY_OF_MONTH));
			}
		}else{
			cdto.add(Calendar.DAY_OF_MONTH, monthCycleParm.getInt("MONTH_CYCLE", 0)-cdto.get(Calendar.DAY_OF_MONTH));
		}
		date.setValue(cdto.getTime());
		// ���õ����˵�
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
        getTextField("ORDER_CODE_I").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE_I").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn_I");
        // TCheckBox���
        check = (TCheckBox)getComponent("CHECK");
	}
	
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn_I(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_I").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_I").setValue(order_desc);
    }
	
    /**
     * ��ѯ
     */
	public void onQuery(){
		TTable table = this.getTable("TABLE");
		TTextFormat date = this.getTextFormat("TRANDATE");
		String dateStr = this.formateDate.format(date.getValue());
		String orgCode = this.getValueString("ORG_CODE");
		String supCode = this.getValueString("SUP_CODE");
		String sqlStart = "SELECT A.SUP_ORDER_CODE, "//��Ӧ��ҩƷ����
			       +" A.ORDER_CODE, "//HISҩƷ����
			       +" B.ORDER_DESC, "//Ʒ��
			       +" B.SPECIFICATION, "//��� 
//			       +" D.UNIT_CHN_DESC, "//��浥λ
			       +" A.BATCH_NO, "//����
			       +" A.ORG_CHN_DESC, "//����
			       +" A.SUP_CHN_DESC, "//��Ӧ��
//			       +" A.STOCK_QTY/C.DOSAGE_QTY||D.UNIT_CHN_DESC||CASE WHEN "
//			       +" MOD(A.STOCK_QTY,C.DOSAGE_QTY)=0 THEN '' ELSE "
//			       +" MOD(A.STOCK_QTY,C.DOSAGE_QTY)||E.UNIT_CHN_DESC END AS QTY, "//�������
			       +" ROUND(A.STOCK_QTY/C.DOSAGE_QTY,2) AS STOCK_QTY, "//�������,�����װ
			       +" D.UNIT_CHN_DESC, "//��浥λ
			       +" FLOOR(A.STOCK_QTY/C.DOSAGE_QTY)||D.UNIT_CHN_DESC||CASE WHEN "
			       +" (A.STOCK_QTY - FLOOR(A.STOCK_QTY / C.DOSAGE_QTY) * C.DOSAGE_QTY)=0 THEN '' ELSE "
			       +" (A.STOCK_QTY - FLOOR(A.STOCK_QTY / C.DOSAGE_QTY) * C.DOSAGE_QTY)||E.UNIT_CHN_DESC END AS QTY, "//�����������ɢ��װ
			       +" F.CONVERSION_RATIO,A.PRICE,ROUND(A.STOCK_QTY * A.PRICE1,2) AS AMT "//����ת����  
			  +" FROM (SELECT G.SUP_CODE,G.SUP_ORDER_CODE,G.ORDER_CODE,G.BATCH_NO,"
			       		+" SUM(G.STOCK_QTY) AS STOCK_QTY, "  
			       		+" H.ORG_CHN_DESC,H.ORG_CODE,I.SUP_CHN_DESC,G.INVENT_PRICE AS PRICE,G.VERIFYIN_PRICE AS PRICE1 "
			       			+" FROM IND_DDSTOCK G,IND_ORG H,SYS_SUPPLIER I "
			       			+" WHERE G.TRANDATE='"+dateStr+"' "
			       					+" AND G.ORG_CODE=H.ORG_CODE "
									+" AND G.SUP_CODE=I.SUP_CODE ";  
		//����
		if(!orgCode.equals("")){
			sqlStart += " AND G.ORG_CODE='"+orgCode+"' ";
		}
		//��Ӧ��
		if(!supCode.equals("")){
			sqlStart += " AND G.SUP_CODE='"+supCode+"' ";
		}
		//fux modify 20150908  �Ƿ�Ϊ0    
		if(this.getValueBoolean("ZERO")){    
			sqlStart += " AND G.STOCK_QTY != 0 ";
		}      
		
		String sqlEnd = " GROUP BY G.SUP_CODE,G.SUP_ORDER_CODE,G.ORDER_CODE,G.BATCH_NO,H.ORG_CHN_DESC,H.ORG_CODE,I.SUP_CHN_DESC,G.INVENT_PRICE,G.VERIFYIN_PRICE) A, "
			           +" PHA_BASE B,PHA_TRANSUNIT C,SYS_UNIT D,SYS_UNIT E, "
			         +" IND_CODE_MAP F "
			 +" WHERE A.ORDER_CODE=B.ORDER_CODE "           
			   +" AND B.ORDER_CODE=C.ORDER_CODE "  
			   +" AND C.STOCK_UNIT=D.UNIT_CODE "
			   +" AND C.DOSAGE_UNIT=E.UNIT_CODE "
			   +" AND A.SUP_CODE=F.SUP_CODE "
			   +" AND A.SUP_ORDER_CODE=F.SUP_ORDER_CODE ";
		
		if(check.isSelected()){
			sqlEnd += " AND A.STOCK_QTY != '0' ORDER BY ORG_CODE,ORDER_CODE";
		}else{
			sqlEnd += " ORDER BY ORG_CODE,ORDER_CODE";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sqlStart+sqlEnd));
		if(result.getCount()<0){
			table.removeRowAll();
			this.messageBox("δ��ѯ������");
			return;
		}
		for(int i=0;i<result.getCount("SUP_ORDER_CODE");i++){
			String str1[] = result.getValue("UNIT_CHN_DESC", i).split("");
			String unitStr = "";
			for(int j=0;j<str1.length;j++){
				if(str1[j].equals("[") || str1[j].equals("]")){
					str1[j] = "";
				}
				unitStr += str1[j];
			}
			result.setData("UNIT_CHN_DESC", i, unitStr);
			String str2[] = result.getValue("QTY", i).split("");
			String qtyStr = "";
			for(int j=0;j<str2.length;j++){
				if(str2[j].equals("[") || str2[j].equals("]")){
					str2[j] = "";
				}
				qtyStr += str2[j];
			}
			result.setData("QTY", i, qtyStr);
		}
		table.setParmValue(result);
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		String clearNames = "ORG_CODE;SUP_CODE;ORDER_CODE_I;ORDER_DESC_I";
		//����ı�������
		this.clearValue(clearNames);
		TTextFormat date = this.getTextFormat("TRANDATE");
		Calendar cdto = Calendar.getInstance();
		date.setValue(cdto.getTime());
		this.getTable("TABLE").removeRowAll();
	}
	
    /**
     * ��λҩƷ����
     */
    public void onOrientationAction() {
    	TTable table = this.getTable("TABLE");
        if ("".equals(this.getValueString("ORDER_CODE_I"))) {
            this.messageBox("�����붨λҩƷ");
            return;
        }
        boolean flg = false;
        TParm parm = table.getParmValue();
        String order_code = this.getValueString("ORDER_CODE_I");
        int row = table.getSelectedRow();
        for (int i = row + 1; i < parm.getCount("ORDER_CODE"); i++) {
            if (order_code.equals(parm.getValue("ORDER_CODE", i))) {
                row = i;
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("δ�ҵ���λҩƷ");
        }
        else {
            table.setSelectedRow(row);
        }
    }
	
	/**
	 * ����Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "�����տ��");
	}
	
	/**
	 * ��ȡTTable�ؼ�
	 * @param tag Ԫ��TAG����
	 * @return
	 */
	public TTable getTable(String tag){
		return (TTable) this.getComponent(tag);
	}
	
	/**
	 * ��ȡTTextFormat�ؼ�
	 * @param tag Ԫ��TAG����
	 * @return
	 */
	public TTextFormat getTextFormat(String tag){
		return (TTextFormat) this.getComponent(tag);
	}
	
    /**
     * �õ�TextField����
     * @param tag Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tag) {
        return (TTextField) getComponent(tag);
    }
}
