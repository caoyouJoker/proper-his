package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.ins.INSTJTool;
import jdo.ins.INSTool;
import jdo.ins.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.sun.xml.xsom.impl.Ref.Term;

/**
 * <p>Title: ��������ҽ�Ʒ����������</p>
 *
 * <p>Description:��������ҽ�Ʒ����������</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangs  
 * @version 1.0
 */
public class INSOpdCostControl extends TControl {
	
	private TTable tTable ;
	
	private TRadioButton cz ;
	
	private TRadioButton cj ;
	
	private String nhi_no ;
	private String nhi_name;
	public DecimalFormat df = new DecimalFormat("##########0.00");
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.tTable = (TTable) this.getComponent("TTABLE") ;
		this.cz = (TRadioButton)this.getComponent("CZ") ;
		this.cj = (TRadioButton)this.getComponent("CJ") ;
		this.cz.setValue(true) ;
		//onQuery();// ��ѯtable1
//		TParm parm = null;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		this.nhi_no = regionParm.getData("NHI_NO", 0).toString();// ��ȡHOSP_NHI_NO
		this.nhi_name= regionParm.getData("REGION_CHN_DESC", 0).toString();// ��ȡREGION_CHN_DESC
	    this.setValue("BEGIN_TIME", SystemTool.getInstance().getDate());
	    
//	    this.nhi_no="000139";
//	    this.setValue("BEGIN_TIME", "2015/07");
	}
	
	public void onQuery(){
		onDownload() ;
	}
	/**
	 * ����
	 */
	public void onDownload() { 
		
		// ��֤�û�����ĺϷ���.
		String beginTime = this.getValueString("BEGIN_TIME");
//		String endTime = this.getValueString("END_TIME");
		boolean czValue = this.getValueBoolean("CZ");

		if ("".equals(beginTime)) {
			messageBox("�ںŲ���Ϊ��");
			return;
		}
		String[] beginTimeArray = beginTime.split("-");
		String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);

//		String[] endTimeArray = endTime.split("-");
//		String newEndTime = endTimeArray[0].concat(endTimeArray[1]);

//		if (Integer.parseInt(newBeginTime) > Integer.parseInt(newEndTime)) {
//			messageBox("��ѯ���ڲ���");
//			return;
//		}
		
		TParm parm = new TParm(); 
		parm.setData("PIPELINE", "DataDown_zjkd");
		parm.setData("PLOT_TYPE", "N");
        parm.addData("NHI_HOSP_NO", this.nhi_no);
		parm.addData("YEAR_MON", newBeginTime);
		if(czValue){
		    parm.addData("TYPE", "01");//��ְ��ѡ��.
		}else{
			parm.addData("TYPE", "02");//�Ǿӱ�ѡ��.
		}
		parm.addData("PARM_COUNT", 3);
//		System.out.println("parm:"+parm);
		TParm resultParm = InsManager.getInstance().safe(parm,null);
//		TParm resultParm = INSTJTool.getInstance().DataDown_zjkd_N(parm);
//		System.out.println("parm:"+parm);
//System.out.println("resultParm:"+resultParm);
		tTable.setHeader("��������,100;���,100;�����ں�,100;����ָ��,100;���������渶ͳ������,100;�����渶������,100;���������渶����ʣ��,100;ɸ����˾ܸ����,100; ����ɸ����˾ܸ�ʣ��,100;ɸ����˾ܸ�ʣ�����ۼ�,100;������Ч��ָ��,100;��������ͳ������,100;����ͳ�����֧��,100;������֧�����,100;����������˾ܸ�,100;����ɸ����˾ܸ�����,100;����������˾ܸ�����,100;����ͳ�����볬��,100;������Чͳ������,100;����ͳ�����볬��,100;������ϼ�,100;����ָ�����,100;����ͳ��Ӧ֧�����,100;���¿���Ԥ�����,100;����ͳ��֧�����,100;����Ӧ�ջؽ��,100;����ʵ���ջؽ��,100;�����ջ�ʣ���,100;����ʵ��ͳ��֧��,100;����Ա�������,100;���в������,100;����������,100;�����Ÿ����,100;�ǵ䲹�����,100;�����˻����,100;����֧���ϼ�,100;����ָ�����,100");
        tTable.setParmMap("BATCH_NO;SEQ_NO;YEAR_MON;MON_QUOTA_AMT;PRE_PAY_AMT;PRE_PAY_EXC_AMT;LMON_PRE_OVER_AMT;VERIFY_REFUSE_AMT;LMON_OVERREF_AMT;VREF_OVER_TOTAL_AMT;TPER_QUOTA_AMT;NHI_AMT;CHANGE_PAY_AMT;CHANGE_TPAY_AMT;REFUSE_AMT;CHANGE_VREFUSE_AMT;CHANGE_REFUSE_AMT;OVER_TOTAL_AMT;TPER_NHI_AMT;OVER_NHI_AMT;OVER_AMT;QUOTA_OVER_AMT;TPER_VERIFY_AMT;EXA_AMT;TPER_PAY_AMT;LYEAR_GET_AMT;LYEAR_GETREAL_AMT;LYEAR_OVERGET_AMT;TMON_PAY_REAL_AMT;SERVANT_AMT;ARMAY_AMT;CANDC_AMT;CIVIL_AMT;SARS_AMT;ACCOUT_PAY_AMT;TPER_TOTAL_AMT;LPER_OVER_AMT;");
		tTable.setColumnHorizontalAlignmentData("0,left");
        if (!INSTJTool.getInstance().getErrParm(resultParm)) {
	        messageBox("����������!");
	        this.callFunction("UI|TTABLE|setParmValue", new TParm());
	        return;
	    }
		
		this.callFunction("UI|TTABLE|setParmValue", resultParm);
	}

	/**
	 * ��ӡ����л���ҽ�Ʊ��ն���������������
	 */
	public void onPrint(){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("��ѡ��һ�ʼ�¼��������") ;
			return ;
		}
		boolean czValue = this.getValueBoolean("CZ");
		TParm resultParm=this.DataDown_zjkd_N(selectedRow);
		if(resultParm==null){
			return;
		}
		
		print_N(resultParm,czValue) ;
		
	}
	
	  /**
	   * ��ӡ����л���ҽ�Ʊ��ն���������������
	   */
	  public TParm DataDown_zjkd_N(int selectedRow)
	  {
			//BATCH_NO;SEQ_NO;YEAR_MON;MON_QUOTA_AMT;PRE_PAY_AMT;PRE_PAY_EXC_AMT;LMON_PRE_OVER_AMT;
			//��������;���;�����ں�;����ָ��;���������渶ͳ������;�����渶������;���������渶����ʣ��;
			//VERIFY_REFUSE_AMT;LMON_OVERREF_AMT;VREF_OVER_TOTAL_AMT;
			//ɸ����˾ܸ����;����ɸ����˾ܸ�ʣ��;ɸ����˾ܸ�ʣ�����ۼ�;
			//TPER_QUOTA_AMT;NHI_AMT;CHANGE_PAY_AMT;CHANGE_TPAY_AMT;REFUSE_AMT;CHANGE_VREFUSE_AMT;
			//������Ч��ָ��;��������ͳ������;����ͳ�����֧��;������֧�����;����������˾ܸ�;����ɸ����˾ܸ�����;
			//CHANGE_REFUSE_AMT;OVER_TOTAL_AMT;TPER_NHI_AMT;OVER_NHI_AMT;OVER_AMT;QUOTA_OVER_AMT;
			//����������˾ܸ�����;����ͳ�����볬��;������Чͳ������;����ͳ�����볬��;������ϼ�;����ָ�����;
			//TPER_VERIFY_AMT;EXA_AMT;TPER_PAY_AMT;LYEAR_GET_AMT;LYEAR_GETREAL_AMT;
			//����ͳ��Ӧ֧�����;���¿���Ԥ�����;����ͳ��֧�����;����Ӧ�ջؽ��;����ʵ���ջؽ��;
			//LYEAR_OVERGET_AMT;TMON_PAY_REAL_AMT;SERVANT_AMT;ARMAY_AMT;CANDC_AMT;CIVIL_AMT;
			//�����ջ�ʣ���;����ʵ��ͳ��֧��;����Ա�������;���в������;����������;�����Ÿ����;    
			//SARS_AMT;ACCOUT_PAY_AMT;TPER_TOTAL_AMT;LPER_OVER_AMT
			//�ǵ䲹�����;�����˻����;����֧���ϼ�;����ָ�����;
			TTable table = (TTable) this.getComponent("TTABLE");// TABLE1
			if (table.getParmValue() == null ) {
				messageBox("��ѡ��һ����¼");
				return null;
			}
		  TParm tempParm=table.getParmValue();
//		  resultParm.addData("NHI_HOSP_NO", this.nhi_no) ;
//		  resultParm.addData("REGION_CHN_DESC", this.nhi_name) ;
//		  resultParm.addData("YEAR",this.getYear(resultParm.getValue("YEAR_MON", 0))) ;
			TParm resultParm=new TParm();
//			resultParm.setData("YEAR_MON", "201507");
			resultParm.setData("YEAR_MON","TEXT", tempParm.getValue("YEAR_MON", selectedRow).substring(0, 4)+
					"��"+tempParm.getValue("YEAR_MON", selectedRow).substring(5, 6)+"��");//�ں�
			resultParm.setData("BATCH_NO","TEXT", tempParm.getValue("BATCH_NO", selectedRow));//��������
			resultParm.setData("NHI_HOSP_NO","TEXT",this.nhi_no) ;//��������
			resultParm.setData("YEAR","TEXT",this.getYear(tempParm.getValue("YEAR_MON", selectedRow))) ;//Э�����
			resultParm.setData("REGION_CHN_DESC","TEXT",this.nhi_name) ;//��������

			resultParm.setData("MON_QUOTA_AMT","TEXT",df.format(tempParm.getDouble("MON_QUOTA_AMT", selectedRow))) ;//����ָ��
			resultParm.setData("LPER_OVER_AMT","TEXT",df.format(tempParm.getDouble("LPER_OVER_AMT", selectedRow))) ;//����ָ�����
			resultParm.setData("PRE_PAY_AMT","TEXT",df.format(tempParm.getDouble("PRE_PAY_AMT", selectedRow))) ;//���������渶ͳ������
			resultParm.setData("LMON_PRE_OVER_AMT","TEXT",df.format(tempParm.getDouble("LMON_PRE_OVER_AMT", selectedRow))) ;//���������渶����ʣ��
			resultParm.setData("VERIFY_REFUSE_AMT","TEXT",df.format(tempParm.getDouble("VERIFY_REFUSE_AMT", selectedRow))) ;//����ɸ����˾ܸ�
			resultParm.setData("LMON_OVERREF_AMT","TEXT",df.format(tempParm.getDouble("LMON_OVERREF_AMT", selectedRow))) ;//����ɸ����˾ܸ�ʣ��
			resultParm.setData("CHANGE_VREFUSE_AMT","TEXT",df.format(tempParm.getDouble("CHANGE_VREFUSE_AMT", selectedRow))) ;//����ɸ����˾ܸ�����
			resultParm.setData("TPER_QUOTA_AMT","TEXT",df.format(tempParm.getDouble("TPER_QUOTA_AMT", selectedRow))) ;//������Чָ��
			
			resultParm.setData("NHI_AMT","TEXT",df.format(tempParm.getDouble("NHI_AMT", selectedRow))) ;//��������ͳ������
			resultParm.setData("CHANGE_PAY_AMT","TEXT",df.format(tempParm.getDouble("CHANGE_PAY_AMT", selectedRow))) ;//����ͳ�����֧��
			resultParm.setData("REFUSE_AMT","TEXT",df.format(tempParm.getDouble("REFUSE_AMT", selectedRow))) ;//����������˾ܸ�
			resultParm.setData("CHANGE_REFUSE_AMT","TEXT",df.format(tempParm.getDouble("CHANGE_REFUSE_AMT", selectedRow))) ;//����������˾ܸ�����
			resultParm.setData("OVER_TOTAL_AMT","TEXT",df.format(tempParm.getDouble("OVER_TOTAL_AMT", selectedRow))) ;//����ͳ�����볬��
			resultParm.setData("TPER_NHI_AMT","TEXT",df.format(tempParm.getDouble("TPER_NHI_AMT", selectedRow))) ;//������Чͳ������
			resultParm.setData("TPER_VERIFY_AMT","TEXT",df.format(tempParm.getDouble("TPER_VERIFY_AMT", selectedRow))) ;//����ͳ��Ӧ֧�����
			resultParm.setData("EXA_AMT","TEXT",df.format(tempParm.getDouble("EXA_AMT", selectedRow))) ;//���¿���Ԥ�����
			resultParm.setData("TPER_PAY_AMT","TEXT",df.format(tempParm.getDouble("TPER_PAY_AMT", selectedRow))) ;//����ͳ��֧�����
			resultParm.setData("QUOTA_OVER_AMT","TEXT",df.format(tempParm.getDouble("QUOTA_OVER_AMT", selectedRow))) ;//����ָ�����
			resultParm.setData("OVER_NHI_AMT","TEXT",df.format(tempParm.getDouble("OVER_NHI_AMT", selectedRow))) ;//����ͳ�����볬��
			resultParm.setData("OTHER_AMT","TEXT","0.00") ;//����
			
			resultParm.setData("LYEAR_GET_AMT","TEXT",df.format(tempParm.getDouble("LYEAR_GET_AMT", selectedRow))) ;//����Ӧ�ջؽ��
			resultParm.setData("LYEAR_GETREAL_AMT","TEXT",df.format(tempParm.getDouble("LYEAR_GETREAL_AMT", selectedRow))) ;//����ʵ���ջؽ��
			resultParm.setData("LYEAR_OVERGET_AMT","TEXT",df.format(tempParm.getDouble("LYEAR_OVERGET_AMT", selectedRow))) ;//�����ջ�ʣ���
			
			resultParm.setData("TMON_PAY_REAL_AMT","TEXT",df.format(tempParm.getDouble("TMON_PAY_REAL_AMT", selectedRow))) ;//����ʵ��ͳ��֧��
			resultParm.setData("SERVANT_AMT","TEXT",df.format(tempParm.getDouble("SERVANT_AMT", selectedRow))) ;//����Ա�������
			resultParm.setData("ARMAY_AMT","TEXT",df.format(tempParm.getDouble("ARMAY_AMT", selectedRow))) ;//���в������
			resultParm.setData("CANDC_AMT","TEXT",df.format(tempParm.getDouble("CANDC_AMT", selectedRow))) ;//�����������
			resultParm.setData("CIVIL_AMT","TEXT",df.format(tempParm.getDouble("CIVIL_AMT", selectedRow))) ;//�����Ÿ����
			resultParm.setData("ACCOUT_PAY_AMT","TEXT",df.format(tempParm.getDouble("ACCOUT_PAY_AMT", selectedRow))) ;//�����˻�
			resultParm.setData("TPER_TOTAL_AMT","TEXT",df.format(tempParm.getDouble("TPER_TOTAL_AMT", selectedRow))) ;//����֧���ϼ�
			Timestamp date = SystemTool.getInstance().getDate();
			String d=date.toString().substring(0, 10).replace("-", "");
			resultParm.setData("DATE1","TEXT",d.substring(0,4)+"��"+
					d.substring(4,6)+"��"+d.substring(6,8)+"��") ;//�������
			resultParm.setData("DATE2","TEXT",d.substring(0,4)+"��"+
					d.substring(4,6)+"��"+d.substring(6,8)+"��") ;//�������

			return resultParm;
	  }  
	  /**
	   * ��ӡ����л���ҽ�Ʊ��ն���������������
	   */
   private void print_N(TParm resultParm,boolean czValue){
		  if(czValue){//��ְ��ѡ��.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSCityCost.jhw",resultParm);
		  }else{//�Ǿӱ�ѡ��.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSCountryCost.jhw",resultParm);
		  }
	}
   /**
    * ��������������ӡ
    */
   public void print_O(){
	   boolean czValue = this.getValueBoolean("CZ");
	   String type="";
		if(czValue){
			type= "01";//��ְ��ѡ��.
		}else{
			type= "02";//�Ǿӱ�ѡ��.
		}
		TParm resultParm=getPrintData(DataDown_zjkd_O(type));
		  if(czValue){//��ְ��ѡ��.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSOpdCostCz.jhw",resultParm);
		  }else{//�Ǿӱ�ѡ��.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSOpdCostCj.jhw",resultParm);
		  }
   }
   /**
    * סԺ����������ӡ
    */
   public void print_P(){
	   boolean czValue = this.getValueBoolean("CZ");
	   String type="";
		if(czValue){
			type= "01";//��ְ��ѡ��.
		}else{
			type= "02";//�Ǿӱ�ѡ��.
		}
		TParm resultParm=getPrintData(DataDown_zjkd_P(type));
		  if(czValue){//��ְ��ѡ��.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSInpCostCz.jhw",resultParm);
		  }else{//�Ǿӱ�ѡ��.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSInpCostCj.jhw",resultParm);
		  }
   }
   /**
    * �ܸ�����
    */
   public void print_Q(){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("��ѡ��һ����¼") ;
			return ;
		}
		String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  
		  TParm parm = new TParm() ;
		  parm.addData("HOSP_NHI_NO", this.nhi_no) ;
		  parm.addData("ADM_SEQ", reportNo) ;
		  
		  boolean czValue = this.getValueBoolean("CZ");
		  String type="";
          String typeDesc="";
		  if(czValue){
			  type= "01";//��ְ��ѡ��.
			  typeDesc="��ְ";
			  parm.setData("PIPELINE", "DataDown_zjks") ;
			  parm.setData("PLOT_TYPE", "B") ;
		  }else{
			  type= "02";//�Ǿӱ�ѡ��.
			  typeDesc="�Ǿ�";
			  parm.setData("PIPELINE", "DataDown_cjks") ;
			  parm.setData("PLOT_TYPE", "B") ;
		  }
		  parm.addData("PARM_COUNT", 2);
		  TParm resultParm = InsManager.getInstance().safe(parm,null);
//		  System.out.println("DataDown_zjks:"+resultParm);
			double tot = resultParm.getDouble("THE_PRO_AMT", 0);

	   double aiOpdMzTot=getDataDown_zjkd_Q_Tot(DataDown_zjkd_Q(type,"01"));
	   double aiOpdMtTot=getDataDown_zjkd_Q_Tot(DataDown_zjkd_Q(type,"02"));
	   double aiInp=getDataDown_zjkd_Q_Tot(DataDown_zjkd_Q(type,"03"));
	   double countTot=aiInp+aiOpdMzTot+aiOpdMtTot+tot;
//	   resultParm.setData("NHI_HOSP_NO","TEXT",this.nhi_no) ;//��������
	   String yearMon=(String) this.tTable.getValueAt_(selectedRow, 2);
//	   printParm.setData("YEAR_MON", "TEXT",(String) this.tTable.getValueAt_(selectedRow, 2)) ;
		TParm printParm =new TParm();
	   printParm.setData("YEAR_MON","TEXT", yearMon.substring(0, 4)+
				"��"+yearMon.substring(5, 6)+"��");//�ں�
	   printParm.setData("BATCH_NO", "TEXT",reportNo) ;
	   printParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
	   printParm.setData("REGION_CHN_DESC","TEXT", this.nhi_name) ;
	   printParm.setData("TYPEDESC","TEXT",typeDesc) ;
	   printParm.setData("AIINPTOT","TEXT",df.format(aiInp)) ;
	   printParm.setData("AIOPDTOT","TEXT",df.format(aiOpdMzTot+aiOpdMtTot)) ;
	   printParm.setData("AITOT","TEXT",df.format(aiInp+aiOpdMzTot+aiOpdMtTot)) ;
	   printParm.setData("TOT","TEXT",df.format(tot)) ;
	   printParm.setData("COUNTTOT","TEXT",df.format(countTot)) ;
	   printParm.setData("UCOUNTTOT","TEXT",StringUtil.getInstance().numberToWord(countTot)) ;
	   
	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSReviewPayment.jhw",printParm);
   } 
   /**
    * ȡ��DataDown_zjkd_Q�оܸ����ϼ�
    */
   private double getDataDown_zjkd_Q_Tot(TParm parm){
//	   System.out.println("getDataDown_zjkd_Q_Tot:"+parm);
	   double tot=0;
	   int rowCount=parm.getCount("REFUSE_AMT");
	   for(int i=0;i<rowCount;i++){
//		   System.out.println(tot+"::::::"+parm.getDouble("REFUSE_AMT", i));
		   tot=tot+parm.getDouble("REFUSE_AMT", i);
	   }
	   return tot;
   }
   /**
    * ȡ��Э�����
    */
   public String getYear(String YearMon){
	   String year="";
//	   System.out.println("getYear:"+YearMon.substring(5, 6));
	   if(Integer.parseInt(YearMon.substring(5, 6))>=4){
		   year=YearMon.substring(0, 4);
	   }else{
		   year=String.valueOf(Integer.parseInt(YearMon.substring(0, 4))-1);
	   }
	   return year;
   }
   /**
    * ȡ��סԺ��������������ӡ����
    */
   private TParm getPrintData(TParm parm){
//	   BATCH_NO	�걨����
//	   SEQ_NO	���
//	   YEAR_MON	�����ں�
//	   START_DATE	���㿪ʼʱ��
//	   END_DATE	�������ʱ��
//	   SUM_TYPE	������� 
//	   PAY_TYPE	֧�����
//	   PRE_PAY_B	ͳ�������渶�����˴�
//	   PRE_PAY_AMT	ͳ�������渶������  
//	   VERIFY_REFUSE_AMT	ɸ����˾ܸ���� 
//	   NHI_B	����ͳ�������˴�
//	   NHI_AMT	����ͳ��������  
//	   NHIR_B	����ͳ���˷��˴�
//	   NHIR_AMT	����ͳ���˷ѽ�� 
//	   CHANGE_PAY_AMT	ͳ�����֧�����
//	   CHANGE_PAY_B	ͳ�����֧���˴�
//	   CHANGE_TPAY_AMT	������֧�����
//	   CHANGE_TPAY_B	������֧���˴�
//	   REFUSE_B	������˾ܸ��˴� 
//	   REFUSE_AMT	������˾ܸ����
//	   CHANGE_REF_AMT	������˾ܸ��������
//	   CHANGE_VREF_AMT	ɸ����˾ܸ��������
//	   String BATCH_NO=parm.getValue("BATCH_NO", 0);//	�걨����
//	   String SEQ_NO="";//	���
//	   String YEAR_MON=parm.getValue("YEAR_MON", 0);//	�����ں�
//	   String START_DATE="";//	���㿪ʼʱ��
//	   String END_DATE="";//	�������ʱ��
//	   String SUM_TYPE="";//	������� 
//	   String PAY_TYPE="";//	֧�����
//	   int PRE_PAY_B=0;//	ͳ�������渶�����˴�
//	   double PRE_PAY_AMT=0;//	ͳ�������渶������  
//	   double VERIFY_REFUSE_AMT=0;//	ɸ����˾ܸ���� 
//	   int NHI_B=0;//	����ͳ�������˴�
//	   double NHI_AMT=0;//	����ͳ��������  
//	   int NHIR_B=0;//	����ͳ���˷��˴�
//	   double NHIR_AMT=0;//	����ͳ���˷ѽ�� 
//	   double CHANGE_PAY_AMT=0;//	ͳ�����֧�����
//	   int CHANGE_PAY_B=0;//	ͳ�����֧���˴�
//	   double CHANGE_TPAY_AMT=0;//	������֧�����
//	   int CHANGE_TPAY_B=0;//	������֧���˴�
//	   int REFUSE_B=0;//	������˾ܸ��˴� 
//	   double REFUSE_AMT=0;//	������˾ܸ����
//	   double CHANGE_REF_AMT=0;//	������˾ܸ��������
//	   double CHANGE_VREF_AMT=0;//	ɸ����˾ܸ��������
//	   
//	   int countRow=parm.getCount("BATCH_NO");
//	   double SERVANT_AMT=0;//	����Ա�������
//	   double ARMAY_AMT=0;//	���в������
//	   double CANDC_AMT=0;//	�����������
//	   double CIVIL_AMT=0;//	�����Ÿ����
//	   double ACCOUT_PAY_AMT=0;//	�����˻���� 
//	   for(int i=0;i<countRow;i++){
//	        if(parm.getValue("PAY_TYPE", i).equals("03")){
//	        	ARMAY_AMT=ARMAY_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }else if(parm.getValue("PAY_TYPE", i).equals("04")){
//	        	SERVANT_AMT=SERVANT_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }else if(parm.getValue("PAY_TYPE", i).equals("10")){
//	        	ACCOUT_PAY_AMT=ACCOUT_PAY_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }else if(parm.getValue("PAY_TYPE", i).equals("11")){
//	        	CANDC_AMT=CANDC_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }else if(parm.getValue("PAY_TYPE", i).equals("12")){
//	        	CIVIL_AMT=CIVIL_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }
//		    PRE_PAY_B=PRE_PAY_B+parm.getInt("PRE_PAY_B", i);//	ͳ�������渶�����˴�
//		    PRE_PAY_AMT=PRE_PAY_AMT+parm.getDouble("PRE_PAY_AMT", i);//	ͳ�������渶������  
//		    VERIFY_REFUSE_AMT=VERIFY_REFUSE_AMT+parm.getDouble("VERIFY_REFUSE_AMT", i);//	ɸ����˾ܸ���� 
//		    NHI_B=NHI_B+parm.getInt("NHI_B", i);//	����ͳ�������˴�
//		    NHI_AMT=NHI_AMT+parm.getDouble("NHI_AMT", i);//	����ͳ��������  
//		    NHIR_B=NHIR_B+parm.getInt("NHIR_B", i);//	����ͳ���˷��˴�
//		    NHIR_AMT=NHIR_AMT+parm.getDouble("NHIR_AMT", i);//	����ͳ���˷ѽ�� 
//		    CHANGE_PAY_AMT=CHANGE_PAY_AMT+parm.getDouble("CHANGE_PAY_AMT", i);//	ͳ�����֧�����
//		    CHANGE_PAY_B=CHANGE_PAY_B+parm.getInt("CHANGE_PAY_B", i);//	ͳ�����֧���˴�
//		    CHANGE_TPAY_AMT=CHANGE_TPAY_AMT+parm.getDouble("CHANGE_TPAY_AMT", i);//	������֧�����
//		    CHANGE_TPAY_B=CHANGE_TPAY_B+parm.getInt("CHANGE_TPAY_B", i);//	������֧���˴�
//		    REFUSE_B=REFUSE_B+parm.getInt("REFUSE_B", i);//	������˾ܸ��˴� 
//		    REFUSE_AMT=REFUSE_AMT+parm.getDouble("REFUSE_AMT", i);//	������˾ܸ����
//		    CHANGE_REF_AMT=CHANGE_REF_AMT+parm.getDouble("CHANGE_REF_AMT", i);//	������˾ܸ��������
//		    CHANGE_VREF_AMT=CHANGE_VREF_AMT+parm.getDouble("CHANGE_VREF_AMT", i);//	ɸ����˾ܸ��������
//	   } 
//	   NHI_SUM_B	����ͳ������ϼ��˴�
//	   NHI_SUM_AMT	����ͳ������ϼƽ��    
//	   NHI_B	����ͳ�������˴�
//	   NHI_AMT	����ͳ��������  
//	   PRE_PAY_B	ͳ�������渶�����˴�
//	   PRE_PAY_AMT	ͳ�������渶������  
//	   CHANGE_PAY_B	ͳ�����֧���˴�
//	   CHANGE_PAY_AMT	ͳ�����֧�����
//	   REFUSE_AMT	������˾ܸ����
//	   VERIFY_REFUSE_AMT	ɸ����˾ܸ���� 
//	   CHANGE_REF_AMT	������˾ܸ��������
//	   CHANGE_VREF_AMT	ɸ����˾ܸ��������
//	   SERVER_AMT	����Ա���������   
//	   ARMY_AMT	���в������
//	   MZJZ_AMT	�����������
//	   MZYF_AMT	�����Ÿ����
	   int selectedRow = this.tTable.getSelectedRow() ;
		TTable table = (TTable) this.getComponent("TTABLE");// TABLE1

		if (selectedRow==-1) {
			messageBox("��ѡ��һ����¼");
			return null;
		}

	  TParm tempParm=table.getParmValue();
	  
	   TParm resultParm = new TParm() ;

		resultParm.setData("YEAR_MON","TEXT", tempParm.getValue("YEAR_MON", selectedRow).substring(0, 4)+
				"��"+tempParm.getValue("YEAR_MON", selectedRow).substring(5, 6)+"��");//�ں�
		resultParm.setData("BATCH_NO","TEXT", tempParm.getValue("BATCH_NO", selectedRow));//��������
	   resultParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
	   resultParm.setData("REGION_CHN_DESC", "TEXT",this.nhi_name) ;
	   resultParm.setData("YEAR","TEXT",this.getYear(tempParm.getValue("YEAR_MON", selectedRow))) ;
	  
	   resultParm.setData("NHI_B_TOT", "TEXT",parm.getValue("NHI_SUM_B", 0)) ;
	   resultParm.setData("NHI_AMT_TOT", "TEXT",df.format(parm.getDouble("NHI_SUM_AMT", 0))) ;
	   resultParm.setData("NHI_B", "TEXT",parm.getValue("NHI_B", 0)) ;
	   resultParm.setData("NHI_AMT", "TEXT",df.format(parm.getDouble("NHI_AMT", 0))) ;
	   resultParm.setData("PRE_PAY_B", "TEXT",parm.getValue("PRE_PAY_B", 0)) ;
	   resultParm.setData("PRE_PAY_AMT", "TEXT",df.format(parm.getDouble("PRE_PAY_AMT", 0))) ;
	   resultParm.setData("CHANGE_PAY_B","TEXT", parm.getValue("CHANGE_PAY_B", 0)) ;
	   resultParm.setData("CHANGE_PAY_AMT","TEXT", df.format(parm.getDouble("CHANGE_PAY_AMT", 0))) ;
	   resultParm.setData("REFUSE_AMT","TEXT", df.format(parm.getDouble("REFUSE_AMT", 0))) ;
	   resultParm.setData("VERIFY_REFUSE_AMT","TEXT", df.format(parm.getDouble("VERIFY_REFUSE_AMT", 0))) ;
	   resultParm.setData("CHANGE_REF_AMT","TEXT", df.format(parm.getDouble("CHANGE_REF_AMT", 0))) ;
	   resultParm.setData("CHANGE_VREF_AMT","TEXT", df.format(parm.getDouble("CHANGE_VREF_AMT", 0))) ;
//	           �����������
	   resultParm.setData("CANDC_AMT","TEXT", df.format(parm.getDouble("MZJZ_AMT", 0))) ;
//		�����Ÿ����
	   resultParm.setData("CIVIL_AMT","TEXT", df.format(parm.getDouble("MZYF_AMT", 0))) ;
//		����Ա�������
	   resultParm.setData("SERVANT_AMT","TEXT", df.format(parm.getDouble("SERVER_AMT", 0))) ;
//		���в������
	   resultParm.setData("ARMAY_AMT","TEXT", df.format(parm.getDouble("ARMY_AMT", 0))) ;
//		�����˻���� 
	   resultParm.setData("ACCOUT_PAY_AMT","TEXT", df.format(parm.getDouble("ACCOUNT_PAY_AMT", 0))) ;
	   
	   return resultParm;
   }
   /**
	 * �����������������
	 */
   private TParm DataDown_zjkd_O(String type){
	   TParm parm = this.getInsParm("DataDown_zjkd","O",type, null);
		  TParm resultParm = InsManager.getInstance().safe(parm,null) ;
		  
		  if(resultParm == null){
			  messageBox("����������ʧ��!") ;
			  return null;
		  }
	   return resultParm;
   }

   /**
	 * סԺ�������������
	 */
   private TParm DataDown_zjkd_P(String type){
	   TParm parm = this.getInsParm("DataDown_zjkd","P",type, null);
		  TParm resultParm = InsManager.getInstance().safe(parm,null) ;
		  
		  if(resultParm == null){
			  messageBox("����������ʧ��!") ;
			  return null;
		  }
	   return resultParm;
   }
   /**
	 * ������˾ܸ���������
	 */
  private TParm DataDown_zjkd_Q(String type,String payType){
	   TParm parm = this.getInsParm("DataDown_zjkd","Q",type, payType);
//	   System.out.printf("DataDown_zjkd_Q:"+parm);
		  TParm resultParm = InsManager.getInstance().safe(parm,null) ;
		  
		  if(resultParm == null){
			  messageBox("����������ʧ��!") ;
			  return null;
		  }
	   return resultParm;
  }
   /**
    * ȡ�õ���ҽ����������
    */
   private TParm getInsParm(String pipeline,String plotType,
		   String type,String payType){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("��ѡ��һ����¼") ;
			return null;
		}
//		boolean czValue = this.getValueBoolean("CZ");
//		if(czValue){//��ְ��ѡ��.
//			
//		}else{//�Ǿӱ�ѡ��.
//			
//		}
		  String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  
			  TParm parm = new TParm() ;
			  parm.setData("PIPELINE", pipeline) ;
			  parm.setData("PLOT_TYPE", plotType) ;
			  parm.addData("NHI_HOSP_NO", this.nhi_no) ;
			  parm.addData("BATCH_NO", reportNo) ;
			  parm.addData("TYPE", type) ;
			  if(StringUtil.isNullString(payType)){
			      parm.addData("PARM_COUNT", 3);
			  }else{
				  parm.addData("PAY_TYPE", payType) ;
				  parm.addData("PARM_COUNT", 4);
			  }
			  return parm;
   }
	/**
	 * ���
	 */
	public void onClear(){
		this.setValue("BEGIN_TIME", "") ;
		this.setValue("END_TIME", "");
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	
	/**
	 * ��ʽ��
	 */
	private String round2(double d){
		 return new DecimalFormat("###########0.00").format(StringTool.round(d, 2));
	}

	public TTable gettTable() {
		return tTable;
	}

	public void settTable(TTable tTable) {
		this.tTable = tTable;
	}

	public TRadioButton getCz() {
		return cz;
	}

	public void setCz(TRadioButton cz) {
		this.cz = cz;
	}

	public TRadioButton getCj() {
		return cj;
	}

	public void setCj(TRadioButton cj) {
		this.cj = cj;
	}

	public String getNhi_no() {
		return nhi_no;
	}

	public void setNhi_no(String nhiNo) {
		nhi_no = nhiNo;
	}
	////////////////////////////////////////////////////////////////////////////
	/**
	 * ����֧����ϸ����
	 */
	public void APDDown() { 
		
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("��ѡ��һ����¼") ;
			return ;
		}
		String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  
		  TParm parm = new TParm() ;
		  parm.addData("BATCH_NO", reportNo) ;
//		  parm.addData("BATCH_NO", "ZKZ000000201612CS1") ;
//		  parm.addData("BATCH_NO", "ZKX000000201612CS1") ;
		  parm.addData("NHI_HOSP_NO", this.nhi_no) ;
		  
		  
		  boolean czValue = this.getValueBoolean("CZ");
//		  String type="";
//          String typeDesc="";
		  if(czValue){
//			  type= "01";//��ְ��ѡ��.
//			  typeDesc="��ְ";
			  parm.setData("PIPELINE", "DataDown_zjkd") ;
			  parm.setData("PLOT_TYPE", "V") ;
		  }else{
//			  type= "02";//�Ǿӱ�ѡ��.
//			  typeDesc="�Ǿ�";
			  parm.setData("PIPELINE", "DataDown_cjkd") ;
			  parm.setData("PLOT_TYPE", "V") ;
		  }
		  parm.addData("PARM_COUNT", 2);
		  TParm resultParm = InsManager.getInstance().safe(parm,null);

		  tTable.setHeader("������˳���,100;���֤����,100;���� ,100;��Ա���,100;������Ա���,100;ͳ�����֧�����,100;��������֧�����,100;�����ʻ�����֧�����,100;������Դ,100");
	      tTable.setParmMap("ADJUST_SEQ_NO;IDNO;PAT_NAME;CTZ_TYPE;SPC_CTZ_TYPE;ADJUST_NHI_AMT;ADJUST_SUB_AMT;ADJUST_ACCOUT_AMT;DATA_SOURCE;");
//		  tTable.setColumnHorizontalAlignmentData("0,left");
          if (!INSTJTool.getInstance().getErrParm(resultParm)) {
	          messageBox("����������!");
	          this.callFunction("UI|TTABLE|setParmValue", new TParm());
	          return;
          }
		  this.callFunction("UI|TTABLE|setParmValue", resultParm);
	}
	/**
	 * ����֧����ϸ����Excel
	 * */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
//		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		String Title="����֧����ϸ";
		ExportExcelUtil.getInstance().exportExcel(tTable, Title);
	}

}
