package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ������������Դ����</p>
 *
 * <p>Description: ������������Դ����</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2013</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author duzhw
 * @version 1.0
 */
public class INVQuerySourceControl extends TControl {
	public INVQuerySourceControl(){}
	private static TTable Table_IN;
	private static TTable Table_OUT;
	
	/**
	 * ��ʼ��
	 */
	public void init(){
		super.init();
		initPage();

	}
	/**
     * ��ʼ��������
     */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		String currday = getFirstDayOfMonth(1);//����1��
		// ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("START_DATE",
        		currday.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        //���TABLE����
        Table_IN = (TTable) getComponent("Table_IN");
        Table_OUT = (TTable) getComponent("Table_OUT");
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
		//�����ϸ��ѯsql
		String sqlIn = "SELECT A.VERIFYIN_NO AS DISPENSE_NO,A.INV_CODE,B.INV_CHN_DESC," +
				" B.DESCRIPTION,A.VALID_DATE, A.IN_QTY AS  QTY " +
				" FROM INV_VERIFYIND A, INV_BASE B " +
				" WHERE A.INV_CODE = B.INV_CODE ";	
		//������ϸ��ѯsql
		String sqlOut = "SELECT A.DISPENSE_NO,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,E.ORG_DESC AS FROM_ORG_DESC, " +
				" F.ORG_DESC AS TO_ORG_DESC,D.DISPENSE_DATE AS VALID_DATE, A.QTY " +
				" FROM INV_DISPENSED A, INV_BASE B, INV_DISPENSEM D, INV_ORG E, INV_ORG F " +
				" WHERE A.INV_CODE = B.INV_CODE AND   A.DISPENSE_NO = D.DISPENSE_NO " +
				" AND  D.FROM_ORG_CODE = E.ORG_CODE AND D.TO_ORG_CODE = F.ORG_CODE AND A.IO_FLG ='2' ";		
		// ��ѯʱ��
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
        	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "START_DATE")), "yyyyMMdd")+" 00:00:00";
            String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "END_DATE")), "yyyyMMdd")+" 23:59:59";
            //�����ϸ��ѯʱ��ƴsql
            sqlIn += " AND A.OPT_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
			+ "','yyyymmdd hh24:mi:ss')";
            //������ϸ��ѯʱ��ƴsql
            sqlOut += " AND D.DISPENSE_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
			+ "','yyyymmdd hh24:mi:ss')";
            
        }else{
        	this.messageBox("���� ����Ϊ��");
        	return;
        }
       //����
       if (!"".equals(this.getValueString("INV_CODE"))) {
        	String invCode=TypeTool.getString(getValue("INV_CODE"));
        	sqlIn +=" AND A.INV_CODE = '" + invCode + "'"; 
        	sqlOut +=" AND A.INV_CODE = '" + invCode + "'"; 
       }else {
        	sqlIn +=" AND A.INV_CODE LIKE '08%'";
        	sqlOut +=" AND A.INV_CODE LIKE '08%'";
	   }
       sqlIn += " GROUP BY A.INV_CODE,A.VERIFYIN_NO,B.INV_CHN_DESC,B.DESCRIPTION,A.VALID_DATE,A.IN_QTY ";
       sqlOut += " GROUP BY A.INV_CODE,A.DISPENSE_NO,B.INV_CHN_DESC,B.DESCRIPTION,E.ORG_DESC,F.ORG_DESC,D.DISPENSE_DATE,A.QTY ";
       //System.out.println("���sql="+sqlIn);
       //System.out.println("����sql="+sqlOut);
       TParm selInParm = new TParm(TJDODBTool.getInstance().select(sqlIn));
       TParm selOutParm = new TParm(TJDODBTool.getInstance().select(sqlOut));
       if(selInParm.getCount()<0){
        	this.messageBox("�����Ϣû��Ҫ��ѯ������");
        }
       if(selOutParm.getCount()<0){
    	   this.messageBox("������Ϣû��Ҫ��ѯ������");
       }
       //System.out.println("��⣺selInParm="+selInParm);
       //System.out.println("���⣺selOutParm="+selOutParm);
        //����table�ϵ�����
        Table_IN.setParmValue(selInParm);
        Table_OUT.setParmValue(selOutParm);
	}
	    
	    
	    
	/**
     * ��ӡ����
     */
    public void onPrint() {
    	TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
	    if(tab.getSelectedIndex() == 0) { //ҳǩһ�������ϸ
	    	onPrint1();
		}
		if(tab.getSelectedIndex() == 1) { //ҳǩ����������ϸ
			onPrint2();
		}
    	
    }
	//��ӡ�����ϸ
    public void onPrint1() {
    	String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		String invCode = getValueString("INV_CODE");//���ʱ���
		TParm tableParm = Table_IN.getParmValue() ;
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
				"�����ϸ����");
		data.setData("S_DATE", "TEXT", "��ʼʱ�䣺" + startDate);
		data.setData("E_DATE", "TEXT", "����ʱ�䣺" + endDate);
		data.setData("INV_CODE", "TEXT", "���ʱ��룺" + invCode);
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); //��ֵ 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("DISPENSE_NO", tableParm.getValue("DISPENSE_NO", i)); 
			result.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).substring(0, 10));
			result.addData("QTY", tableParm.getValue("QTY", i));
	
		}
		result.setCount(tableParm.getCount("INV_CODE")) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "DISPENSE_NO");
		result.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		result.addData("SYSTEM", "COLUMNS", "QTY");

		data.setData("TABLE", result.getData()) ; 
		//��β����
		data.setData("OPT_DATE", "TEXT", "����ʱ�䣺"+now);
		data.setData("OPT_USER", "TEXT", "�����ˣ�"+Operator.getName());
		
		//out��־���data��Ϣ-���ڵ���
		System.out.println("data=="+data);
		
		//���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVInDetailDispense.jhw", data);
    }
    
    //��ӡ������ϸ
    public void onPrint2() {
    	String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		String invCode = getValueString("INV_CODE");//���ʱ���
		TParm tableParm = Table_OUT.getParmValue() ;
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
				"������ϸ����");
		data.setData("S_DATE", "TEXT", "��ʼʱ�䣺" + startDate);
		data.setData("E_DATE", "TEXT", "����ʱ�䣺" + endDate);
		data.setData("INV_CODE", "TEXT", "���ʱ��룺" + invCode);
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); //��ֵ 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("DISPENSE_NO", tableParm.getValue("DISPENSE_NO", i)); 
			result.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).substring(0, 10));
			result.addData("QTY", tableParm.getValue("QTY", i));
			result.addData("FROM_ORG_DESC", tableParm.getValue("FROM_ORG_DESC", i));
			result.addData("TO_ORG_DESC", tableParm.getValue("TO_ORG_DESC", i));
	
		}
		result.setCount(tableParm.getCount("INV_CODE")) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "DISPENSE_NO");
		result.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "FROM_ORG_DESC");
		result.addData("SYSTEM", "COLUMNS", "TO_ORG_DESC");

		data.setData("TABLE", result.getData()) ; 
		//��β����
		data.setData("OPT_DATE", "TEXT", "����ʱ�䣺"+now);
		data.setData("OPT_USER", "TEXT", "�����ˣ�"+Operator.getName());
		
		//out��־���data��Ϣ-���ڵ���
		System.out.println("data=="+data);
		
		//���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVOutDetailDispense.jhw", data);
    	
    }
    
    /**
     * ��շ���
     */
	
	public void onClear() {
		Timestamp date = StringTool.getTimestamp(new Date());
		String currday = getFirstDayOfMonth(1);//����1��
		// ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("START_DATE",
        		currday.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		this.setValue("INV_CODE", "");
		
	}    
	    
	/**
	  * ����excel
	  * */
	 public void onExcel(){
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
	    if(tab.getSelectedIndex() == 0) { //ҳǩһ�������ϸ
	    	onExcel1();
		}
		if(tab.getSelectedIndex() == 1) { //ҳǩ����������ϸ
			onExcel2();
		}
	 }   
	 //���������ϸ
	 public void onExcel1(){
		 TTable table = getTable("Table_IN");
	    	if(table.getRowCount() > 0){
	    		ExportExcelUtil.getInstance().exportExcel(table, "�����ϸ��ͳ��");
	    	}else {
	         this.messageBox("û�л������");
	         return;
	     }
		 
	 } 
	 //����������ϸ
	 public void onExcel2(){
		 TTable table = getTable("Table_OUT");
	    	if(table.getRowCount() > 0){
	    		ExportExcelUtil.getInstance().exportExcel(table, "������ϸ��ͳ��");
	    	}else {
	         this.messageBox("û�л������");
	         return;
	     }
		 
	 }   
	// ��ȡ���µ�һ��
	public static String getFirstDayOfMonth(int no) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar lastDate = Calendar.getInstance();
		lastDate.set(Calendar.DATE, no);// ��Ϊ��ǰ�µ�n��
		str = sdf.format(lastDate.getTime());
		return str;
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

}
