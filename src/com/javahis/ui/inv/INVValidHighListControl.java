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
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ��Ч�ڸ�ֵ����</p>
 *
 * <p>Description: ��Ч�ڸ�ֵ����</p>   
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw 20131108
 * @version 1.0
 */
public class INVValidHighListControl extends TControl  {
	
	
	public INVValidHighListControl() {
		
	}
	private static TTable detailTable;
	private static TTable mainTable;
	
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
		detailTable = (TTable) getComponent("Table");
		mainTable = (TTable) getComponent("MAIN_TABLE");
		
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
    	mainTable.removeRowAll();//�������
    	detailTable.removeRowAll();//���ϸ��
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
        //fux modify 20140313
        if (getRadioButton("VALID_DATE_E").isSelected()) {
        	parm.setData("VALID_DATE",getMonthDay(1));
        	this.setValue("VALID_DATE_VALUE", getMonthDay(1));
        }
        else if (getRadioButton("VALID_DATE_A").isSelected()) {
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
//        String sql = "SELECT D.RFID,T.INV_CODE,T.INV_CHN_DESC,T.DESCRIPTION,G.UNIT_CHN_DESC,T.COST_PRICE," +
//        		"E.SUP_CHN_DESC,F.SUP_CHN_DESC AS UP_SUP_CHN_DESC,I.SUP_CHN_DESC AS MAN_CODE,H.CATEGORY_CHN_DESC AS INV_KIND,B.VALID_DATE " +
//        		" FROM INV_BASE T, INV_DDSTOCK B, SYS_DEPT C, INV_STOCKDD D, SYS_SUPPLIER E, SYS_SUPPLIER F, SYS_UNIT G, SYS_CATEGORY H, SYS_SUPPLIER I " +
//        		" WHERE T.SEQMAN_FLG = 'Y' AND T.EXPENSIVE_FLG = 'Y' " +
//        		" AND T.INV_CODE = B.INV_CODE AND B.ORG_CODE = C.DEPT_CODE " +
//        		" AND T.INV_CODE = D.INV_CODE AND T.SUP_CODE = E.SUP_CODE AND T.UP_SUP_CODE = F.SUP_CODE" +
//        		" AND T.DISPENSE_UNIT = G.UNIT_CODE AND T.INV_KIND = H.CATEGORY_CODE " +
//        		" AND T.MAN_CODE = I.SUP_CODE";
        String sql = "		SELECT  T.INV_CODE,									" +
        		"					T.INV_CHN_DESC,								" +
        		"					C.DEPT_CHN_DESC,							" +
        		"					B.ORG_CODE,									" +
        		"					E.SUP_CHN_DESC,								" +
        		"					G.UNIT_CHN_DESC,							" +
        		"					T.SUP_CODE,									" +
        		"					F.SUP_CHN_DESC      AS UP_SUP_CHN_DESC,		" +
        		"					T.UP_SUP_CODE,								" +
        		"					I.SUP_CHN_DESC      AS MAN_CODE,			" +
        		"					T.MAN_CODE,									" +
        		//"					H.CATEGORY_CHN_DESC AS INV_KIND,			" +
        		//#4655 liuyl 2016/12/29
        		"					A.CHN_DESC AS INV_KIND,			            " +
        		"					T.INV_KIND AS INV_KIND_CODE,				" +
        		"					COUNT(T.INV_CODE) AS SUM					" +
        		"			FROM 	INV_BASE     T,								" +
        		"					INV_STOCKDD  B,								" +
        		"					SYS_DEPT     C,								" +
        		"					SYS_SUPPLIER E,								" +
        		"					SYS_SUPPLIER F,								" +
        		//"					SYS_CATEGORY H, 							" +
        		"					SYS_SUPPLIER I,								" +
        		"					SYS_UNIT     G,								" +
        		//#4655 liuyl 2016/12/29
        		"					SYS_DICTIONARY A							" +
        		"			WHERE 	T.SEQMAN_FLG = 'Y'							" +
        		"					AND T.EXPENSIVE_FLG = 'Y'					" +
        		"					AND B.ORG_CODE = C.DEPT_CODE				" +
        		"					AND B.INV_CODE = T.INV_CODE(+)				" +
        		"					AND T.SUP_CODE = E.SUP_CODE(+)				" +
        		"					AND T.UP_SUP_CODE = F.SUP_CODE(+)			" +
        		//"					AND T.INV_KIND = H.CATEGORY_CODE(+) 		" +
        		//#4655 liuyl 2016/12/29
        		"					AND T.INV_KIND = A.ID(+) 		            " +
        		"					AND T.DISPENSE_UNIT = G.UNIT_CODE			" +
        		//#4655 liuyl 2016/12/29
        		//"					AND H.RULE_TYPE = 'INV_BASE'				" +
        		"					AND A.GROUP_ID = 'INV_BASE_KIND'			" +
        		//fux modify 20160901
        		"                   AND B.WAST_FLG= 'N'                         " +
        		"					AND T.MAN_CODE = I.SUP_CODE(+)				";
        		
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
        
        
        sql += " GROUP BY 	T.INV_CODE,T.INV_CHN_DESC,C.DEPT_CHN_DESC,B.ORG_CODE, " +
        		" B.ORG_CODE,E.SUP_CHN_DESC,G.UNIT_CHN_DESC,T.SUP_CODE,F.SUP_CHN_DESC,T.UP_SUP_CODE,"+
        		//"H.CATEGORY_CHN_DESC,"+
        		//#4655 liuyl 2016/12/29
        		" A.CHN_DESC,"+
        		"I.SUP_CHN_DESC,T.MAN_CODE,T.INV_KIND " +
        		" ORDER BY 	T.INV_CODE,C.DEPT_CHN_DESC,E.SUP_CHN_DESC,F.SUP_CHN_DESC ";
        System.out.println("��Ч�ڸ�ֵsql--->"+sql);
        TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        if(resultParm.getCount()<0){   
        	this.messageBox("û��Ҫ��ѯ������");
        	return;
        }
          
        mainTable.setParmValue(resultParm);
    	
    }
    public void onMainTableClick(){
    	int selectedIndx=this.mainTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;  
    	}
    	TParm tableparm=this.mainTable.getParmValue();
    	String invCode = tableparm.getValue("INV_CODE",selectedIndx);
    	String orgCode = tableparm.getValue("ORG_CODE",selectedIndx);
    	String supCode = tableparm.getValue("SUP_CODE",selectedIndx);
    	String upSupCode = tableparm.getValue("UP_SUP_CODE",selectedIndx);
    	String manCode = tableparm.getValue("MAN_CODE",selectedIndx);
    	String invKindCode = tableparm.getValue("INV_KIND_CODE",selectedIndx);
    	String validDate = "";
    	//Ч�� 
    	//fux modify 20140313
        if (getRadioButton("VALID_DATE_E").isSelected()) {
        	validDate = getMonthDay(1);
        } 
        else if (getRadioButton("VALID_DATE_A").isSelected()) {
        	validDate = getMonthDay(3);
        }
        else if (getRadioButton("VALID_DATE_B").isSelected()) {
        	validDate = getMonthDay(6);
        }else if (getRadioButton("VALID_DATE_D").isSelected()){
        	validDate = "";
        }
        else {
        	String valid_date = getValueString("VALID_DATE");
        	validDate = valid_date.substring(0, 4) +
					valid_date.substring(5, 7) + valid_date.substring(8, 10);
        	
        }
    	
    	String detailSql = "SELECT  B.RFID,									" +
				"					B.BATCH_NO,								" + //��������20131229
    			"					T.INV_CODE,								" +
    			"					T.INV_CHN_DESC,							" +
    			"					T.DESCRIPTION,							" +
    			"					G.UNIT_CHN_DESC,						" +
    			"					T.COST_PRICE,							" +
    			"					E.SUP_CHN_DESC,							" +
    			"					F.SUP_CHN_DESC      AS UP_SUP_CHN_DESC,	" +
    			"					I.SUP_CHN_DESC      AS MAN_CODE,		" +
    			//"					H.CATEGORY_CHN_DESC AS INV_KIND,		" +
    			//#4655 liuyl 2016/12/29
    			"					A.CHN_DESC          AS INV_KIND,	    " +
    			"					B.VALID_DATE							" +
    			"			FROM 	INV_BASE     T,							" +
    			"					INV_STOCKDD  B,							" +
    			"					SYS_DEPT     C,							" +
    			"					SYS_SUPPLIER E,							" +
    			"					SYS_SUPPLIER F,							" +
    			"					SYS_UNIT     G,							" +
    			//"					SYS_CATEGORY H,							" +
    			//#4655 liuyl 2016/12/29
    			"					SYS_DICTIONARY A,     					" +
    			"					SYS_SUPPLIER I							" +
    			"			WHERE 	T.SEQMAN_FLG = 'Y'						" +
    			"					AND T.EXPENSIVE_FLG = 'Y'				" +
    			"					AND B.ORG_CODE = C.DEPT_CODE			" +
    			"					AND B.INV_CODE = T.INV_CODE(+)			" +
    			"					AND T.SUP_CODE = E.SUP_CODE(+)			" +
    			"					AND T.UP_SUP_CODE = F.SUP_CODE(+)		" +
    			"					AND T.DISPENSE_UNIT = G.UNIT_CODE		" +
    			//"					AND T.INV_KIND = H.CATEGORY_CODE(+)		" +
    			//#4655 liuyl 2016/12/29
    			"					AND T.INV_KIND = A.ID(+) 		        " +
    			//#4655 liuyl 2016/12/29
    			//"					AND H.RULE_TYPE = 'INV_BASE'			" +
    			"					AND A.GROUP_ID = 'INV_BASE_KIND'		" +
    			"					AND T.MAN_CODE = I.SUP_CODE(+)			" +
        		//fux modify 20160901
        		"                   AND B.WAST_FLG= 'N'                     " +
    			"					AND B.INV_CODE = '"+invCode+"' ";
    	
    	//����
        if(!"".equals(orgCode)){
        	detailSql += " AND B.ORG_CODE = '"+orgCode+"'";
        }
        //���ʱ���
//        if(!"".equals(inv_code)){  
//        	detailSql += " AND T.INV_CODE = '"+inv_code+"'";
//        }
        //��Ӧ��
        if(!"".equals(supCode)){
        	detailSql += " AND T.SUP_CODE = '"+supCode+"'";
        }
        //�ϼ���Ӧ��
        if(!"".equals(upSupCode)){
        	detailSql += " AND T.UP_SUP_CODE = '"+upSupCode+"'";
        }
        //���ʷ���
        if(!"".equals(invKindCode)){
        	detailSql += " AND T.INV_KIND = '"+invKindCode+"'";
        }
        //Ч��
        if(!"".equals(validDate)){
        	detailSql += " AND B.VALID_DATE <= TO_DATE('"+validDate+"','YYYYMMDD')";
        }
      //#4655 liuyl 2016/12/29
        //detailSql += " ORDER BY T.INV_CODE,H.CATEGORY_CHN_DESC ";
        detailSql += " ORDER BY T.INV_CODE,A.CHN_DESC ";
        //����
        System.out.println("detailSql="+detailSql);
        
        TParm resultParm = new TParm(TJDODBTool.getInstance().select(detailSql));
        
    	//ͳ���������ܽ��
        double allFee = 0.00;
        int count = resultParm.getCount();
        setValue("ALLCOUNT",String.valueOf(count));
        for (int i = 0; i < count; i++) {
        	allFee  += Double.parseDouble(resultParm.getValue("COST_PRICE", i));
		}
        
        DecimalFormat df = new DecimalFormat("0.00");
        String newAllFee = df.format(allFee);
        
//        int roundingMode = 4;//��ʾ�������룬����ѡ��������ֵ��ʽ������ȥβ���ȵ�.
//        BigDecimal bd = new BigDecimal((double)allFee); 
//        bd = bd.setScale(2,roundingMode); 
//        allFee = bd.doubleValue(); 

		newAllFee = feeConversion(newAllFee);//�Ӷ��Ŵ���
        this.setValue("ALLFEE",newAllFee);
        detailTable.setParmValue(resultParm);
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
    	now = now.replaceAll("-", "/");
    	int selectedIndx=this.mainTable.getSelectedRow();
    	//System.out.println("selectedIndx="+selectedIndx);
    	TParm mainTableParm = mainTable.getParmValue();
    	TParm tableParm = detailTable.getParmValue() ;
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
    	
    	if(!"".equals(orgCode) && orgCode!=null){
    		orgCode = mainTableParm.getValue("DEPT_CHN_DESC", selectedIndx);
    	}
    	if(!"".equals(supCode) && supCode!=null){
    		supCode = mainTableParm.getValue("SUP_CHN_DESC", selectedIndx);
    	}
    	if(!"".equals(upSupCode) && upSupCode!=null){
    		upSupCode = mainTableParm.getValue("UP_SUP_CHN_DESC", selectedIndx);
    	}
    	if(!"".equals(kind) && kind!=null){
    		kind = mainTableParm.getValue("INV_KIND_CODE", selectedIndx);
    	}
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		//��ӡ����
		TParm data = new TParm();
		//��ͷ����
		data.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"��Ч�ڸ�ֵ����");
		data.setData("ORG_CODE", "TEXT", "�������ƣ�" + orgCode);
		data.setData("INV_DESC", "TEXT", "�������ƣ�" + invDesc);
		data.setData("SUP_CODE", "TEXT", "��Ӧ�̣�" + supCode);
		data.setData("UP_SUP_CODE", "TEXT", "�ϼ���Ӧ�̣�" + upSupCode);
		data.setData("KIND", "TEXT", "�������" + kind);
		data.setData("VALID_DATE", "TEXT", "Ч�ڣ�" + validDate);
		data.setData("ALLCOUNT", "TEXT", "�ϼƱ�����" + allCount);
		data.setData("ALLFEE", "TEXT", "�ܽ�" + allFee);
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("RFID", tableParm.getValue("RFID", i)); //��ֵ 
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)+ 
					"("+tableParm.getValue("DESCRIPTION", i)+")"); 
			//result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
			result.addData("COST_PRICE", df.format(tableParm.getDouble("COST_PRICE", i)));
			result.addData("SUP_CHN_DESC", tableParm.getValue("SUP_CHN_DESC", i));
			result.addData("UP_SUP_CHN_DESC", tableParm.getValue("UP_SUP_CHN_DESC", i));
			result.addData("MAN_CODE", tableParm.getValue("MAN_CODE", i));
			result.addData("INV_KIND", tableParm.getValue("INV_KIND", i));
			result.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).substring(0, 10));

		}
		result.setCount(tableParm.getCount("RFID")) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "RFID");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		//result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
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
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVValidHighList.jhw", data);
    }
    
    /**
     * �������
     */
    public void onExcel(){
    	//TTable tTable = getTable("tTable");
    	if(detailTable.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(detailTable, "��Ч�ڸ�ֵ����ͳ��");
    	}else {
         this.messageBox("û�л������");
         return;
     }
    }
    /**
     * ��շ���
     */
    public void onClear() { 
    	//fux modify 20140313
    	getRadioButton("VALID_DATE_E").setSelected(true);
        getTextFormat("VALID_DATE").setEnabled(false);
        this.clearValue("VALID_DATE;INV_CODE;INV_DESC;SUP_CODE;UP_SUP_CODE;KIND;ALLCOUNT;ALLFEE;VALID_DATE_VALUE");
        detailTable.removeRowAll();
        mainTable.removeRowAll();
        
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
