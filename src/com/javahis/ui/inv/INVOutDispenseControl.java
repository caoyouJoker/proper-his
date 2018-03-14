package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.inv.INVsettlementTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:���ʳ��ⵥ����
 *
 * <p>Description: ���ʳ��ⵥ����
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx  
 * @version 4.0
 */
public class INVOutDispenseControl extends TControl{
	private TTable table_in_all ;
	private TTable table_in_detail ;
	private TTable table_out_detail ;
//	private TTable table ;
	private String date  ; //ͳ������     
	
	/**
	 * ��ʼ��
	 */

	public void onInit(){
		super.onInit() ;
		this.onInitPage() ;
	}
	/**
	 * ��ʼ������
	 */
	public void onInitPage(){  
		String now = SystemTool.getInstance().getDate().toString().replace("-", "") ;
		this.setValue("START_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //��ʼʱ��
		this.setValue("END_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //����ʱ��
		table_in_all = (TTable)this.getComponent("Table_IN_ALL");
		table_in_detail = (TTable)this.getComponent("Table_IN_DETAIL");
		table_out_detail = (TTable)this.getComponent("Table_OUT_DETAIL");
		
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		//TParm parm =INVsettlementTool.getInstance().queryOutDispense(getSearchParm()) ;
		TParm parm =INVsettlementTool.getInstance().queryInAllDispense(getSearchParm());
		//System.out.println("parm====="+parm);
		if(parm.getCount()<=0){
			this.messageBox("��������") ;
			this.onClear();
			return ;
		}
		//double money = 0.00 ;
		int inQty = 0;
		for(int i=0;i<parm.getCount();i++){
			inQty += parm.getInt("IN_QTY", i);
		}
		parm.addData("INV_CODE", "�ϼ�") ;
		parm.addData("INV_CHN_DESC", "") ;
		parm.addData("DESCRIPTION", "") ;
		parm.addData("IN_QTY", inQty) ;

		table_in_all.setParmValue(parm) ;
		date = StringTool.getString((Timestamp) this.getValue("START_DATE"),
		"yyyy/MM/dd ")
		+ " �� "
		+ StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy/MM/dd ");

	}
	 /**
     * ��������Ϣ���(TABLE)�����¼�
     */
    public void onTableClicked() {
    	
    	// �õ���ѡ���е�����
        //TParm checkparm = table_in_all.getDataStore().getRowParm(table_in_all.getSelectedRow());
        int row = table_in_all.getTable().getTable().getSelectedRow();
        if((row+1) != table_in_all.getRowCount()){
        	//��ȡ��ӦINV_CODE
            String invCode  = table_in_all.getItemString(row, "INV_CODE");
            this.setValue("INV_CODE", invCode);
            //��ѯ��Ӧ�����ϸ���ڶ�ҳǩ��
            TParm inParm = INVsettlementTool.getInstance().queryInDetailDispense(getInOutSearchParm(invCode));
            table_in_detail.setParmValue(inParm) ;
            //��ѯ��Ӧ������ϸ������ҳǩ��
            TParm outParm = INVsettlementTool.getInstance().queryOutDetailDispense(getInOutSearchParm(invCode));
            table_out_detail.setParmValue(outParm) ;
        }else{
        	//System.out.println("ѡ�е����Ǻϼ��У�");
        }
        
        
    }
    /**
     * ҳǩ�����¼�
     */
    public void onTablePaneClicked(){
    	TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
    	if(tab.getSelectedIndex() == 0) { //ҳǩһ����֤������
    		this.callFunction("UI|query|setEnabled", true);
    		this.callFunction("UI|export|setEnabled", true);
			}
		if(tab.getSelectedIndex() == 1) { //ҳǩ���������ϸ
			this.callFunction("UI|query|setEnabled", false);
    		this.callFunction("UI|export|setEnabled", false);
			}
		if(tab.getSelectedIndex() == 2) { //ҳǩ����������ϸ
			this.callFunction("UI|query|setEnabled", false);
    		this.callFunction("UI|export|setEnabled", false);
		}
    	
    }
	  /**
     * ��ȡ��ѯ��������
     * @return
     * */
 	private TParm getSearchParm() {     
 		TParm searchParm = new TParm();
 		String startDate = getValueString("START_DATE").substring(0, 10).replace("-", "");
 		String endDate = getValueString("END_DATE").substring(0, 10).replace("-", "");
 		searchParm.setData("START_DATE",startDate+"000000"); 
 		searchParm.setData("END_DATE",endDate+"235959");  
 		return searchParm;
 	}
 	  /**
     * ��ȡ������ѯ��������
     * @return
     * */
 	private TParm getInOutSearchParm(String invCode) {
 		TParm inOutSearchParm = new TParm();
 		String startDate = getValueString("START_DATE").substring(0, 10).replace("-", "");
 		String endDate = getValueString("END_DATE").substring(0, 10).replace("-", "");
 		inOutSearchParm.setData("START_DATE",startDate+"000000"); 
 		inOutSearchParm.setData("END_DATE",endDate+"235959"); 
 		inOutSearchParm.setData("INV_CODE", invCode);
 		return inOutSearchParm;
 	}
	/**
	 * ���
	 */
	public void onClear(){
//		this.clearValue("ORG_CODE;REQUEST_TYPE") ;
//		TParm clearParm = new TParm() ;
//		table.setParmValue(clearParm) ;
	}
	
	
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
    	if(tab.getSelectedIndex() == 0) { //ҳǩһ����֤������
    		onPrint1();
			}
		if(tab.getSelectedIndex() == 1) { //ҳǩ���������ϸ
			onPrint2();
			}
		if(tab.getSelectedIndex() == 2) { //ҳǩ����������ϸ
			onPrint3();
		}

	}
	//��ӡ��֤������
	public void onPrint1(){
		String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		TParm tableParm = table_in_all.getParmValue() ;
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
				"���������ܱ���");
		data.setData("S_DATE", "TEXT", "��ʼʱ�䣺" + startDate);
		data.setData("E_DATE", "TEXT", "����ʱ�䣺" + endDate);
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); //��ֵ 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("IN_QTY", tableParm.getValue("IN_QTY", i)); 
	
		}
		result.setCount(tableParm.getCount("INV_CODE")) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "IN_QTY");

		data.setData("TABLE", result.getData()) ; 
		//��β����
		data.setData("OPT_DATE", "TEXT", "����ʱ�䣺"+now);
		data.setData("OPT_USER", "TEXT", "�����ˣ�"+Operator.getName());
		
		//out��־���data��Ϣ-���ڵ���
		System.out.println("data=="+data);
		
		//���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVInAllDispense.jhw", data);
		
	}
	
	//��ӡ�����ϸ
	public void onPrint2(){
		String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		String invCode = getValueString("INV_CODE");//���ʱ���
		TParm tableParm = table_in_detail.getParmValue() ;
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
	public void onPrint3(){
		String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
		String startDate = StringTool.getString((Timestamp) this.getValue("START_DATE"),
				"yyyy-MM-dd ");
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy-MM-dd ");
		String invCode = getValueString("INV_CODE");//���ʱ���
		TParm tableParm = table_out_detail.getParmValue() ;
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
     * ���Excel
     */
    public void onExport() {
        //�õ�UI��Ӧ�ؼ�����ķ���
        TParm parm = table_in_all.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("û����Ҫ����������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table_in_all, "�������������ܱ���");
    }	
}
