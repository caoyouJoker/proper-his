package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ��Ч�ڵ�ֵ����</p>
 *
 * <p>Description: ��Ч�ڵ�ֵ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw 20131108
 * @version 1.0
 */
public class INVValidLowListControl extends TControl  {
	
	public INVValidLowListControl(){
		
	}
	
private static TTable tTable;
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	super.init();
		initPage();
    	
        this.setValue("ORG_CODE", Operator.getDept());
        
        if (this.getPopedem("ALL")) {
			this.callFunction("UI|ORG_CODE|setEnabled", false);
		}else{
			this.callFunction("UI|ORG_CODE|setEnabled", true);
		}
        
    }
    /**
     * ��ʼ��������
     */
	private void initPage() {
		//���TABLE����
		tTable = (TTable) getComponent("Table");
		
		TParm parm = new TParm();
		// ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
    
    /**
     * ��ѯ����
     */
    public void onQuery() {
    	tTable.removeRowAll();//�������
    	this.clearValue("ALLCOUNT;ALLFEE");
    	TParm parm = new TParm();
        String org_code = "";
        String inv_code = "";
        
        //���Ŵ���
        org_code = getValueString("ORG_CODE");
        if (org_code == null || org_code.length() <= 0) {
            this.messageBox("��ѡ���ѯ����");
            return;
        }
        //���ʱ���
        inv_code = getValueString("INV_CODE");
//        if (inv_code == null || inv_code.length() <= 0) {
//            this.messageBox("��ѡ���ѯ����");
//            return;
//        }
        //��Ӧ��
        String sup_code = getValueString("SUP_CODE");
        //�ϼ���Ӧ��
        String up_sup_code = getValueString("UP_SUP_CODE");
        //���ʷ���
        String kind = getValueString("KIND");
        String date = StringTool.getString(SystemTool.getInstance().getDate(),
        "yyyyMMdd");
        //Ч��
        if (getRadioButton("VALID_DATE_A").isSelected()) {
        	parm.setData("VALID_DATE",getMonthDay(3));
        	this.setValue("VALID_DATE_VALUE", getMonthDay(3));
        }
        else if (getRadioButton("VALID_DATE_B").isSelected()) {
        	parm.setData("VALID_DATE",getMonthDay(6));
        	this.setValue("VALID_DATE_VALUE", getMonthDay(6));
        }else if (getRadioButton("VALID_DATE_D").isSelected()){
        	parm.setData("VALID_DATE","");
        	this.setValue("VALID_DATE_VALUE", "99991231");
        }
        else {
        	String valid_date = getValueString("VALID_DATE");
        	parm.setData("VALID_DATE", valid_date.substring(0, 4) +
        			valid_date.substring(5, 7) + valid_date.substring(8, 10));
        	this.setValue("VALID_DATE_VALUE", valid_date.substring(0, 4) +
        			valid_date.substring(5, 7) + valid_date.substring(8, 10));
        }
        
//        System.out.println("org_code="+org_code);
//        System.out.println("inv_code="+inv_code);
//        System.out.println("sup_code="+sup_code);
//        System.out.println("up_sup_code="+up_sup_code);
//        System.out.println("kind="+kind);
//        System.out.println("valid_date="+parm.getValue("VALID_DATE"));
        
        //��ѯsql
//        String sql = "SELECT T.INV_CODE,T.INV_CHN_DESC,T.DESCRIPTION,G.UNIT_CHN_DESC,D.STOCK_QTY,T.COST_PRICE," +
//        		"E.SUP_CHN_DESC,F.SUP_CHN_DESC AS UP_SUP_CHN_DESC,I.SUP_CHN_DESC AS MAN_CODE,H.CATEGORY_CHN_DESC AS INV_KIND,B.VALID_DATE " +
//        		" FROM INV_BASE T LEFT JOIN SYS_SUPPLIER E ON T.SUP_CODE = E.SUP_CODE" +
//        		" LEFT JOIN SYS_SUPPLIER F ON T.UP_SUP_CODE = F.SUP_CODE " +
//        		" LEFT JOIN SYS_SUPPLIER I ON  T.MAN_CODE = I.SUP_CODE " +
//        		" LEFT JOIN SYS_CATEGORY H ON T.INV_KIND = H.CATEGORY_CODE," +
//        		" INV_STOCKDD B, SYS_DEPT C, INV_STOCKDD D,  SYS_UNIT G " +
//        		" WHERE (T.SEQMAN_FLG != 'Y' OR T.EXPENSIVE_FLG != 'Y') " +
//        		" AND T.INV_CODE = B.INV_CODE AND B.ORG_CODE = C.DEPT_CODE " +
//        		" AND T.INV_CODE = D.INV_CODE " +
//        		" AND T.DISPENSE_UNIT = G.UNIT_CODE " ;
        String sql = " SELECT T.INV_CODE, " +
        		" T.INV_CHN_DESC, " +
        		" T.DESCRIPTION, " +
        		" G.UNIT_CHN_DESC, " +
        		" B.STOCK_QTY, " +
        		" IA.CONTRACT_PRICE, " +
        		" E.SUP_CHN_DESC, " +
        		" F.SUP_CHN_DESC AS UP_SUP_CHN_DESC, " +
        		" I.SUP_CHN_DESC AS MAN_CODE, " +
        		" H.CATEGORY_CHN_DESC AS INV_KIND, " +
        		" C.DEPT_CHN_DESC, " +
        		" B.VALID_DATE " +
        		" FROM INV_BASE T, INV_STOCKD B, SYS_DEPT C, SYS_SUPPLIER E, SYS_SUPPLIER F, SYS_SUPPLIER I, SYS_CATEGORY H, SYS_UNIT G, INV_AGENT IA " +
        		" WHERE (T.SEQMAN_FLG <> 'Y' OR T.SEQMAN_FLG IS NULL OR T.EXPENSIVE_FLG <> 'Y' OR T.EXPENSIVE_FLG IS NULL) " +
        		" AND B.INV_CODE = T.INV_CODE(+) " +
        		" AND B.ORG_CODE = C.DEPT_CODE " +
        		" AND T.DISPENSE_UNIT = G.UNIT_CODE " +
        		" AND T.SUP_CODE = E.SUP_CODE(+) " +
        		" AND T.UP_SUP_CODE = F.SUP_CODE(+) " +
        		" AND T.MAN_CODE = I.SUP_CODE(+) " +
        		" AND T.INV_KIND = H.CATEGORY_CODE(+) " +
        		" AND H.RULE_TYPE = 'INV_BASE' " + 
        		" AND T.INV_CODE = IA.INV_CODE(+) ";
        		
        //����
        if(!"".equals(org_code)){
        	sql += " AND B.ORG_CODE = '"+org_code+"'";
        }
        //���ʱ���
        if(!"".equals(inv_code)){
        	sql += " AND T.INV_CODE = '"+inv_code+"'";
        }
        //��Ӧ��
        if(!"".equals(sup_code)){
        	sql += " AND T.SUP_CODE = '"+sup_code+"'";
        }
        //�ϼ���Ӧ��
        if(!"".equals(up_sup_code)){
        	sql += " AND T.UP_SUP_CODE = '"+up_sup_code+"'";
        }
        //���ʷ���
        if(!"".equals(kind)){
        	sql += " AND T.INV_KIND = '"+kind+"'";
        }
        //Ч��
        if(!"".equals(parm.getValue("VALID_DATE"))){
        	sql += " AND B.VALID_DATE <= TO_DATE('"+parm.getValue("VALID_DATE")+"','YYYYMMDD')";
        }
        
        
        sql += " ORDER BY T.INV_CODE,T.INV_CHN_DESC,T.SUP_CODE," +
        		"T.UP_SUP_CODE,T.INV_KIND,B.VALID_DATE";
        //System.out.println("��Ч�ڵ�ֵsql--->"+sql);
        TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        if(resultParm.getCount("INV_CODE")<0){
        	this.messageBox("û��Ҫ��ѯ������");
        	return;
        }
//        //ͳ���������ܽ��
//        TParm newparm = new TParm(); 
//        String inv_code1 = "";
//        String inv_code2 = "";
//        int qty1 = 0;
//        int qty2 = 0;
//        int allqty = 0;
//        String flg = "";
//        
//        String inv_chn_desc1 = "";
//        String description1 = "";
//        String unit_chn_desc1 = "";
//        String cost_price1 = "";
//        String dept_chn_desc1 = "";
//        String sup_chn_desc1 = "";
//        String up_sup_chn_desc1 = "";
//        String man_code1 = "";
//        String inv_kind1 = "";
//        String valid_date1 = "";
//        String inv_chn_desc2 = "";
//        String description2 = "";
//        String unit_chn_desc2 = "";
//        String cost_price2 = "";
//        String dept_chn_desc2 = "";
//        String sup_chn_desc2 = "";
//        String up_sup_chn_desc2 = "";
//        String man_code2 = "";
//        String inv_kind2 = "";
//        String valid_date2 = "";
//        int j = 0;//newparm������
        double allFee = 0.00;
        int count = resultParm.getCount();
        setValue("ALLCOUNT",String.valueOf(count));
        for (int i = 0; i < count; i++) {
        	double d  = Double.parseDouble(resultParm.getValue("STOCK_QTY", i));
        	allFee  = allFee + ( Double.parseDouble(resultParm.getValue("CONTRACT_PRICE", i))*d );
//wangming20140109ע��start        	
//        	inv_code1 = resultParm.getValue("INV_CODE", i);
//        	inv_code2 = resultParm.getValue("INV_CODE", i+1);
//        	qty1 = resultParm.getInt("STOCK_QTY", i);
//        	qty2 = resultParm.getInt("STOCK_QTY", i+1);
//        	
//        	inv_chn_desc1 = resultParm.getValue("INV_CHN_DESC", i);
//        	description1 = resultParm.getValue("DESCRIPTION", i);
//            unit_chn_desc1 = resultParm.getValue("UNIT_CHN_DESC", i);
//            cost_price1 = resultParm.getValue("COST_PRICE", i);
//            dept_chn_desc1 = resultParm.getValue("DEPT_CHN_DESC", i);
//            sup_chn_desc1 = resultParm.getValue("SUP_CHN_DESC", i);
//            up_sup_chn_desc1 = resultParm.getValue("UP_SUP_CHN_DESC", i);
//            man_code1 = resultParm.getValue("MAN_CODE", i);
//            inv_kind1 = resultParm.getValue("INV_KIND", i);
//            valid_date1 = resultParm.getValue("VALID_DATE", i);
//            
//            inv_chn_desc2 = resultParm.getValue("INV_CHN_DESC", i+1);
//        	description2 = resultParm.getValue("DESCRIPTION", i+1);
//            unit_chn_desc2 = resultParm.getValue("UNIT_CHN_DESC", i+1);
//            cost_price2 = resultParm.getValue("COST_PRICE", i+1);
//            dept_chn_desc2 = resultParm.getValue("DEPT_CHN_DESC", i+1);
//            sup_chn_desc2 = resultParm.getValue("SUP_CHN_DESC", i+1);
//            up_sup_chn_desc2 = resultParm.getValue("UP_SUP_CHN_DESC", i+1);
//            man_code2 = resultParm.getValue("MAN_CODE", i+1);
//            inv_kind2 = resultParm.getValue("INV_KIND", i+1);
//            valid_date2 = resultParm.getValue("VALID_DATE", i+1);
//        	if(i == 0){
//        		allqty = qty1;
//        	}
//        	if(i < count-1){
//        		if(inv_code1.equals(inv_code2)){
//            		allqty += qty2;
//            		flg = "true";
//            	}else{
//            		//��װ��parm
//            		newparm.setData("INV_CODE", j++, inv_code1);
//            		newparm.setData("INV_CHN_DESC", j++, inv_chn_desc1);
//            		newparm.setData("DESCRIPTION", j++, description1);
//            		newparm.setData("UNIT_CHN_DESC", j++, unit_chn_desc1);
//            		newparm.setData("INV_NUM", j++, allqty);
//            		newparm.setData("COST_PRICE", j++, cost_price1);
//            		newparm.setData("DEPT_CHN_DESC", j++, dept_chn_desc1);
//            		newparm.setData("SUP_CHN_DESC", j++, sup_chn_desc1);
//            		newparm.setData("UP_SUP_CHN_DESC", j++, up_sup_chn_desc1);
//            		newparm.setData("MAN_CODE", j++, man_code1);
//            		newparm.setData("INV_KIND", j++, inv_kind1);
//            		newparm.setData("VALID_DATE", j++, valid_date1.substring(0, 10));
//            		allqty = qty2;//��Ϊ��һ����ͬ���ʱ��������
//            		flg = "false";
//            	}
//        	}
//        	if(i == count-1){
//        		if(flg.equals("true")){
//        			//�������һ��������װparm
//        			newparm.setData("INV_CODE", j++, inv_code1);
//            		newparm.setData("INV_CHN_DESC", j++, inv_chn_desc1);
//            		newparm.setData("DESCRIPTION", j++, description1);
//            		newparm.setData("UNIT_CHN_DESC", j++, unit_chn_desc1);
//            		newparm.setData("INV_NUM", j++, allqty);
//            		newparm.setData("COST_PRICE", j++, cost_price1);
//            		newparm.setData("DEPT_CHN_DESC", j++, dept_chn_desc1);
//            		newparm.setData("SUP_CHN_DESC", j++, sup_chn_desc1);
//            		newparm.setData("UP_SUP_CHN_DESC", j++, up_sup_chn_desc1);
//            		newparm.setData("MAN_CODE", j++, man_code1);
//            		newparm.setData("INV_KIND", j++, inv_kind1);
//            		newparm.setData("VALID_DATE", j++, valid_date1.substring(0, 10));
//        		}else if(flg.equals("false")){
//        			//���һ��������װparm
//        			newparm.setData("INV_CODE", j++, inv_code2);
//            		newparm.setData("INV_CHN_DESC", j++, inv_chn_desc2);
//            		newparm.setData("DESCRIPTION", j++, description2);
//            		newparm.setData("UNIT_CHN_DESC", j++, unit_chn_desc2);
//            		newparm.setData("INV_NUM", j++, qty2);
//            		newparm.setData("COST_PRICE", j++, cost_price2);
//            		newparm.setData("DEPT_CHN_DESC", j++, dept_chn_desc2);
//            		newparm.setData("SUP_CHN_DESC", j++, sup_chn_desc2);
//            		newparm.setData("UP_SUP_CHN_DESC", j++, up_sup_chn_desc2);
//            		newparm.setData("MAN_CODE", j++, man_code2);
//            		newparm.setData("INV_KIND", j++, inv_kind2);
//            		String sss = valid_date2.substring(0, 10);
//            		newparm.setData("VALID_DATE", j++, valid_date2.substring(0, 10));
//        		}
//        	}
        	//wangming20140109ע��end           	
		}

        DecimalFormat df = new DecimalFormat("0.00");
        String newAllFee = df.format(allFee);
        newAllFee = feeConversion(newAllFee);//�Ӷ��Ŵ���
        this.setValue("ALLFEE",newAllFee);
//2014-01-09ע��        tTable.setParmValue(newparm);
        tTable.setParmValue(resultParm);
    	
    }
    /**
     * ÿ��λ�Ӷ��Ŵ���
     */
    public String feeConversion(String fee){
    	String str1 = ""; 
    	String[] s = fee.split("\\.");//��"."���ָ�
    	
        str1 = new StringBuilder(s[0].toString()).reverse().toString();     //�Ƚ��ַ����ߵ�˳��  
        String str2 = "";  
        for(int i=0;i<str1.length();i++){  
            if(i*3+3>str1.length()){  
                str2 += str1.substring(i*3, str1.length());  
                break;  
            }  
            str2 += str1.substring(i*3, i*3+3)+",";  
        }  
        if(str2.endsWith(",")){  
            str2 = str2.substring(0, str2.length()-1);  
        }  
        //����ٽ�˳��ת����  
        String str3 = new StringBuilder(str2).reverse().toString();
        //����С��������
        StringBuffer str4 = new StringBuffer(str3);
        str4 = str4.append(".").append(s[1]);
    	return str4.toString();
    }
    /**
     * ��ӡ����
     */
    public void onPrint(){
    	String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
    	
    	DecimalFormat df = new DecimalFormat("0.0000");
    	
    	String orgCode = getValueString("ORG_CODE");		//����
    	String invDesc = getValueString("INV_DESC");		//����
    	String supCode = getValueString("SUP_CODE");		//��Ӧ��
    	String upSupCode = getValueString("UP_SUP_CODE");	//�ϼ���Ӧ��
    	String kind = getValueString("KIND");				//���ʷ���
    	String validDate = getValueString("VALID_DATE_VALUE");//Ч��
    	validDate = validDate.substring(0, 4) + "/" + validDate.substring(4, 6) + "/"
			+ validDate.substring(6, 8);
    	String allCount = getValueString("ALLCOUNT");		//�ϼƱ���
    	String allFee = getValueString("ALLFEE");			//�ܽ��
    	
    	TParm tableParm = tTable.getParmValue() ;
    	
    	if(!"".equals(orgCode) && orgCode!=null){
    		orgCode = tableParm.getValue("DEPT_CHN_DESC", 0);
    	}
    	if(!"".equals(supCode) && supCode!=null){
    		supCode = tableParm.getValue("SUP_CHN_DESC", 0);
    	}
    	if(!"".equals(upSupCode) && upSupCode!=null){
    		upSupCode = tableParm.getValue("UP_SUP_CHN_DESC", 0);
    	}
    	if(!"".equals(kind) && kind!=null){
    		kind = tableParm.getValue("INV_KIND_CODE", 0);
    	}
    	
    	
    	//System.out.println("��ӡ��tableParm="+tableParm);
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount("INV_CODE")<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		//��ӡ����
		TParm data = new TParm();
		//��ͷ����
		data.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"��Ч�ڵ�ֵ����");
		data.setData("ORG_CODE", "TEXT", "�������ƣ�" + orgCode);
		data.setData("INV_DESC", "TEXT", "�������ƣ�" + invDesc);
		data.setData("SUP_CODE", "TEXT", "��Ӧ�̣�" + supCode);
		data.setData("UP_SUP_CODE", "TEXT", "�ϼ���Ӧ�̣�" + upSupCode);
		data.setData("KIND", "TEXT", "�������" + kind);
		data.setData("VALID_DATE", "TEXT", "Ч�ڣ�" + validDate);
		data.setData("ALLCOUNT", "TEXT", "�ϼƱ�����" + allCount);
		data.setData("ALLFEE", "TEXT", "�ܽ�" + allFee);
		for(int i=0;i<tableParm.getCount("INV_CODE");i++){
			//result.addData("RFID", tableParm.getValue("RFID", i)); //��ֵ 
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)+
					"("+tableParm.getValue("DESCRIPTION", i)+")"); 
			//result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
			result.addData("INV_NUM", tableParm.getValue("INV_NUM", i));
			result.addData("COST_PRICE", df.format(tableParm.getDouble("COST_PRICE", i)));
			result.addData("SUP_CHN_DESC", tableParm.getValue("SUP_CHN_DESC", i));
			result.addData("UP_SUP_CHN_DESC", tableParm.getValue("UP_SUP_CHN_DESC", i));
			result.addData("MAN_CODE", tableParm.getValue("MAN_CODE", i));
			result.addData("INV_KIND", tableParm.getValue("INV_KIND", i));
			result.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).substring(0, 10));

		}
		result.setCount(tableParm.getCount("INV_CODE")) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		//result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "INV_NUM");
		result.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		result.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "UP_SUP_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "MAN_CODE");
		result.addData("SYSTEM", "COLUMNS", "INV_KIND");
		result.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		
		data.setData("TABLE", result.getData()) ; 
		//��β����
		data.setData("OPT_DATE", "TEXT", "����ʱ�䣺"+now);
		data.setData("OPT_USER", "TEXT", "�����ˣ�"+Operator.getName());
		
		//out��־���data��Ϣ-���ڵ���
		//System.out.println("data=="+data);
		
		//���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVValidLowList.jhw", data);
    }
    
    /**
     * �������
     */
    public void onExcel(){
    	//TTable tTable = getTable("tTable");
    	if(tTable.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(tTable, "��Ч�ڵ�ֵ��ͳ��");
    	}else {
         this.messageBox("û�л������");
         return;
     }
    }
    
    /**
     * ��շ���
     */
    public void onClear() {
    	getRadioButton("VALID_DATE_A").setSelected(true);
        getTextFormat("VALID_DATE").setEnabled(false);
        this.clearValue("VALID_DATE;INV_CODE;INV_DESC;SUP_CODE;UP_SUP_CODE;KIND;ALLCOUNT;ALLFEE;VALID_DATE_VALUE");
        tTable.removeRowAll();
        
    }
    
    
    /**
     * �����ѡ��
     */
    public void onChangeRadioButton() {
        if (getRadioButton("VALID_DATE_C").isSelected()) {
            getTextFormat("VALID_DATE").setEnabled(true);
        }
        else {
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE");
        }
    }
	/**
	 * �õ�TextField����
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
	 /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
    /**
     * ����ָ�����·ݺ������Ӽ�������Ҫ���·ݺ�����
     * @param Month String �ƶ��·� ��ʽ:yyyyMM
     * @param Day String �ƶ��·� ��ʽ:dd
     * @param num String �Ӽ������� ����Ϊ��λ
     * @return String
     */
    public String rollMonth(String Month, String Day,int num){
        if(Month.trim().length()<=0){
            return "";
        }
        Timestamp time = StringTool.getTimestamp(Month,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // ��ǰ�£�num
        cal.add(cal.MONTH, num);
        // ���¸���1����Ϊ���ڳ�ʼֵ
        cal.set(cal.DATE, 1);
        Timestamp month = new Timestamp(cal.getTimeInMillis());
        String result = StringTool.getString(month, "yyyyMM");
        String lastDayOfMonth = getLastDayOfMonth(result);
        if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
            result += lastDayOfMonth;
        }
        else {
            result += Day;
        }
        return result;
    }
    /**
     * ��ȡָ���·ݵ����һ�������
     * @param date String ��ʽ YYYYMM
     * @return Timestamp
     */
    public String getLastDayOfMonth(String date) {
        if (date.trim().length() <= 0) {
            return "";
        }
        Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // ��ǰ�£�1�����¸���
        cal.add(cal.MONTH, 1);
        // ���¸���1����Ϊ���ڳ�ʼֵ
        cal.set(cal.DATE, 1);
        // �¸���1�ż�ȥһ�죬���õ���ǰ�����һ��
        cal.add(cal.DATE, -1);
        Timestamp result = new Timestamp(cal.getTimeInMillis());
        return StringTool.getString(result, "dd");
    }
    /**
     * ��ȡָ��n����ǰ������
     */
    public String getMonthDay(int no) {
    	String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		//lastDate.set(Calendar.DATE, no);// ��Ϊ��ǰ�µ�n��
		lastDate.add(Calendar.MONTH, +no);// ��n���£���Ϊ���µ�1��
		// lastDate.add(Calendar.DATE,-1);//��ȥһ�죬��Ϊ�������һ��

		str = sdf.format(lastDate.getTime());
		return str;
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

}
