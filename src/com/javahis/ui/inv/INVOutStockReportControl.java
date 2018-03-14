package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.inv.INVOutStockReportTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ���ư��˰��˶Ա���</p>
 *
 * <p>Description: ���ư��˰��˶Ա���</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.11.13
 * @version 1.0
 */
	public class INVOutStockReportControl  extends TControl{
		private TTable table;
		private TTable table1;
		/**
		 * ��ֵ
		 */
		private String lflg;
		
		public String getLflg() {
			return lflg;
		}
		public void setLflg(String lflg) {
			this.lflg = lflg;
		}
		public void onInit() {
			initPage();
			 TParm parm = new TParm();
	         parm.setData("SUP_CODE", "");
	         callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//
			 getTextField("INV_CODE").setPopupMenuParameter("UD",
			            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
			            parm);
			        // ������ܷ���ֵ����
			        getTextField("INV_CODE").addEventListener(
			            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
	/**
	 * �õ�TABLE����
	 * @param tagName
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
		
	}
	/* �õ�TextField����
	 *
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
	    return (TTextField) getComponent(tagName);
	}
	/**
	 *��ʼ��
	 */
	public void initPage() {
		     table = getTable("TABLE");
		     table1 = getTable("TABLE1");
		     //this.setValue("TO_ORG_CODE", Operator.getDept());
		   //����ѯʱ��θ�ֵ
		     Timestamp date = StringTool.getTimestamp(new Date());
			// ʱ����Ϊ1��
			// ��ʼ����ѯ����
			this.setValue("END_TIME", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
			this.setValue("START_TIME", StringTool.rollDate(date, -1).toString().substring(0,10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		table.removeRowAll();
		double totAmt=0;
		DecimalFormat dc=new DecimalFormat("###,###.00");
		
		//��ѯ����
		TParm result=INVOutStockReportTool.getInstance().getSelectdata(this.getScondiTion());
		if(result.getErrCode()<0){
			this.messageBox("��ѯ���ִ���");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("û�в�ѯ����");
			table.removeRowAll();
			this.setValue("TOT_NUM",""); //�ϼƱ���
			this.setValue("TOT_AMT","");//������
			return;
		}
		for(int i=0;i<result.getCount();i++){
			totAmt+=result.getDouble("CONTRACT_AMT",i);
		}
		this.setValue("TOT_NUM",""+result.getCount()); //�ϼƱ���
		this.setValue("TOT_AMT", dc.format(totAmt));//������
	
		     table.setParmValue(result);
		     table1.removeRowAll();//�Ƴ�table1
	}
	/**
	 *  ��ѯ����
	 */
	public TParm getScondiTion(){
		TParm parm = new TParm();
		//�˻�������
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("START_TIME")), "yyyyMMddHHmmss");
		//�˻�������
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("END_TIME")), "yyyyMMddHHmmss");
		parm.setData("S_DATE", sDate);
		parm.setData("E_DATE", eDate);
		//���ⲿ��
		if(this.getValueString("TO_ORG_CODE").length()>0){
			parm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		}
		//��ⲿ��
		if(this.getValueString("FROM_ORG_CODE").length()>0){
			parm.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE"));
		}
		//��������
		if(this.getValueString("INV_CODE").length()>0){
			parm.setData("INV_CODE", this.getValueString("INV_CODE"));
		}
		//��������
		if(this.getValueString("INV_KIND").length()>0){
			parm.setData("INV_KIND", this.getValueString("INV_KIND"));
		}
		//��ֵ
		if("Y".equals(this.getValueString("H_FLG"))){
			parm.setData("H_FLG", "Y");
			this.setLflg("N");
		}
		//��ֵ
		if("Y".equals(this.getValueString("L_FLG"))){
			parm.setData("L_FLG","Y");
			this.setLflg("Y");
		}
		//����
		if("Y".equals(this.getValueString("CONSIGN_FLG"))){
			parm.setData("CONSIGN_FLG", "Y");
		}
		
		//��Ӧ��
		if(this.getValueString("SUP_CODE").length()>0){
			parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
		}
		//�ϼ���Ӧ��
		if(this.getValueString("UP_SUP_CODE").length()>0){
			parm.setData("UP_SUP_CODE", this.getValueString("UP_SUP_CODE"));
		}
		//���ⵥ��
		if(this.getValueString("DISPENSE_NO").length()>0){
			parm.setData("DISPENSE_NO", this.getValueString("DISPENSE_NO"));
		}
		
		
		return parm;
	}
	
	 /**
	 * ���ܷ���ֵ����
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
	    TParm parm = (TParm) obj;
	    if(parm == null){
	        return;
	    }
	    String inv_code = parm.getValue("INV_CODE");
	    if (!StringUtil.isNullString(inv_code))
	        getTextField("INV_CODE").setValue(inv_code);
	    String inv_desc = parm.getValue("INV_CHN_DESC");
	    if (!StringUtil.isNullString(inv_desc))
	        getTextField("INV_DESC").setValue(inv_desc);
	}
	/**
	 *��ѯtable ,table1 ���
	 */
	
	public void onClear() {
		String clearString="TO_ORG_CODE;FROM_ORG_CODE;INV_CODE;SUP_CODE;UP_SUP_CODE;INV_DESC;INV_KIND;TOT_AMT;TOT_NUM;CONSIGN_FLG;H_FLG;L_FLG;DISPENSE_NO";
		
		this.setValue("CONSIGN_FLG", "N");
		this.setValue("H_FLG", "Y");
		clearValue(clearString);
		
		table.removeRowAll();
		table1.removeRowAll();
		initPage();
		
	}
	/**
	 * �����¼�
	 */
	public void onTableClicked(){
		
		int row = table.getSelectedRow();
		if(row<0){
			return;
		}
//		String invCode = table.getParmValue().getValue("INV_CODE", row);
//		String dipsNo = table.getParmValue().getValue("DISPENSE_NO", row);


		TParm dparm=new TParm();
		TParm tparm= new TParm();
		TParm dresult=new TParm();
	    tparm = table.getParmValue().getRow(row);
	     
	     //this.setValue("TO_ORG_CODE", tparm.getValue("TO_ORG_CODE"));
	     //this.setValue("FROM_ORG_CODE", tparm.getValue("FROM_ORG_CODE"));
	    // this.setValue("SUP_CODE", tparm.getValue("SUP_CODE"));
	    // this.setValue("UP_SUP_CODE", tparm.getValue("UP_SUP_CODE"));
	     //this.setValue("INV_KIND", tparm.getValue("INV_KIND"));*/
	     
	     //������ϸ
//	    dparm=this.getScondiTion();//��ѯ����
	    dparm.setData("INV_CODE",tparm.getValue("INV_CODE"));
	    dparm.setData("DISPENSE_NO",tparm.getValue("DISPENSE_NO"));
	     
	     
	    if(!"Y".equals(this.getLflg())){//����ǵ�ֵ������û����ϸ����
	    	 dresult=INVOutStockReportTool.getInstance().getSelectdetail(dparm);
	    	  
	    	 System.out.println("dresult"+dresult);
	    
	    	 if(dresult.getErrCode()<0){
	    		 this.messageBox("��ѯ����");
	    		 return;
	    	 }
	    	 table1.setParmValue(dresult); 
	    }
	}
	/**
	 * ���Excel
	 */
	public void onExecl() {
		if(table.getRowCount()<=0){
			this.messageBox("û�л������");
			return;
		}
		//���ʳ������
	     ExportExcelUtil.getInstance().exportExcel(table,"���ʳ������");
	    
	    if(table1.getRowCount()<=0){
	    	return;
	    }
	     //���ʳ�����ϸ
	     ExportExcelUtil.getInstance().exportExcel(table1,"���ʳ�����ϸ");
	}
	
}
