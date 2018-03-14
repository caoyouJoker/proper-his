package com.javahis.ui.ins;

//import java.sql.Timestamp;
import java.sql.Timestamp;
import java.text.DecimalFormat;
//import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.ins.INSTJTool;
//import jdo.ins.INSTool;
import jdo.ins.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
//import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
//import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
//import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
//import com.sun.xml.xsom.impl.Ref.Term;

/**
 * <p>Title: ��ͷ�����ָ����������</p>
 *
 * <p>Description:��ͷ�����ָ����������</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangs  
 * @version 1.0
 */
public class INSSinDiseaseDownControl extends TControl {
	
	private TTable tTable ;
	
	private TComboBox INS_TYPE ;
	
	private TComboBox PAY_TYPE ;
	
	private String nhi_no ;
	private String nhi_name;
	public DecimalFormat df = new DecimalFormat("##########0.00");
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.tTable = (TTable) this.getComponent("TTABLE") ;
		this.INS_TYPE = (TComboBox)this.getComponent("INS_TYPE") ;
		this.PAY_TYPE = (TComboBox)this.getComponent("PAY_TYPE") ;
		PAY_TYPE.setValue("01");
		//onQuery();// ��ѯtable1
//		TParm parm = null;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		this.nhi_no = regionParm.getData("NHI_NO", 0).toString();// ��ȡHOSP_NHI_NO
		this.nhi_name= regionParm.getData("REGION_CHN_DESC", 0).toString();// ��ȡREGION_CHN_DESC
	    this.setValue("YEAR_MON", SystemTool.getInstance().getDate());
	    
//	    this.nhi_no="000139";
//	    this.setValue("BEGIN_TIME", "2015/07");
	}
	
	public void onQuery(){
		this.onDownload() ;
	}
	/**
	 * ����
	 */
	public void onDownload() { 
		
		// ��֤�û�����ĺϷ���.
		String yearMon = this.getValueString("YEAR_MON");
		String insType = INS_TYPE.getValue();
		String payType = PAY_TYPE.getValue();

		if ("".equals(yearMon)) {
			messageBox("�ںŲ���Ϊ��");
			return;
		}
		if ("".equals(insType)) {
			messageBox("���ֲ���Ϊ��");
			return;
		}
		if ("".equals(payType)) {
			messageBox("���ѷ�ʽ����Ϊ��");
			return;
		}
		String[] beginTimeArray = yearMon.split("-");
		String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);
		
		TParm parm = new TParm(); 
		parm.setData("PIPELINE", "DataDown_zjkd");
		parm.setData("PLOT_TYPE", "S");
        parm.addData("NHI_HOSP_NO", this.nhi_no);
		parm.addData("YEAR_MON", newBeginTime);			
		parm.addData("INS_TYPE", insType);
		parm.addData("PAY_TYPE", payType);
		parm.addData("PARM_COUNT", 4);
//		System.out.println("parm:"+parm);
		TParm resultParm = InsManager.getInstance().safe(parm,null);
//		System.out.println("parm:"+parm);
//        System.out.println("resultParm:"+resultParm);
        if (!INSTJTool.getInstance().getErrParm(resultParm)) {
	        messageBox("����������!");
	        this.callFunction("UI|TTABLE|setParmValue", new TParm());
	        return;
	    }
		
		this.callFunction("UI|TTABLE|setParmValue", resultParm);
	}

   /**
    * ��ͷ�������������
    */
   public void onPrint(){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("��ѡ��һ����¼") ;
			return ;
		}
		  TParm tableParm=this.tTable.getParmValue();
		  TParm parm = new TParm() ;
		  parm.addData("HOSP_NHI_NO", this.nhi_no) ;
		  parm.addData("YEAR_MON", tableParm.getValue("SUM_YEARMON", selectedRow));	
		  parm.addData("MON_BATCH_NO", tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
		  parm.addData("SINGLE_BATCH_NO", tableParm.getValue("SINGLE_BATCH_NO", selectedRow)) ;
		  parm.addData("INS_TYPE", INS_TYPE.getValue()) ;
		  parm.setData("PIPELINE", "DataDown_zjkd") ;
		  parm.setData("PLOT_TYPE", "T") ;
		  parm.addData("PARM_COUNT", 5);
		  TParm resultParm = InsManager.getInstance().safe(parm,null);

       int childrenTime=0;
       double childrenAmt=0;
       int adultHighTime=0;
       double adultHighAmt=0;
       int adultMedianTime=0;
       double adultMedianAmt=0;
       int adultLowTime=0;
       double adultLowAmt=0;
       int referralTime=0;
       double referralAmt=0;
       ///////////////////////////////////////////////////���ݼ���
	   int rowCount=resultParm.getCount("REFUSE_AMT");
	   for(int i=0;i<rowCount;i++){
//		   System.out.println(tot+"::::::"+parm.getDouble("REFUSE_AMT", i));
		   if(resultParm.getValue("SUM_TYPE", i).equals("12")){
			   if(resultParm.getValue("PAT_TYPE", i).equals("01")||
					   resultParm.getValue("PAT_TYPE", i).equals("11")){
				   childrenTime = resultParm.getInt("SUM_P", i);
				   childrenAmt = resultParm.getDouble("TOT_NHI_AMT", i);
			   }else if(resultParm.getValue("PAT_TYPE", i).equals("02")||
					   resultParm.getValue("PAT_TYPE", i).equals("21")){
				   adultHighTime = resultParm.getInt("SUM_P", i);
				   adultHighAmt = resultParm.getDouble("TOT_NHI_AMT", i);
			   }
			   else if(resultParm.getValue("PAT_TYPE", i).equals("03")||
					   resultParm.getValue("PAT_TYPE", i).equals("51")){
				   adultMedianTime = resultParm.getInt("SUM_P", i);
				   adultMedianAmt = resultParm.getDouble("TOT_NHI_AMT", i);
			   }else if(resultParm.getValue("PAT_TYPE", i).equals("04")){
				   adultLowTime = resultParm.getInt("SUM_P", i);
				   referralAmt = resultParm.getDouble("TOT_NHI_AMT", i);
			   }

		   }else if(resultParm.getValue("SUM_TYPE", i).equals("14")){
			   referralTime=referralTime+resultParm.getInt("SUM_P", i);
			   referralAmt=referralAmt+resultParm.getDouble("TOT_NHI_AMT", i);
		   }
	   }
       //////////////////////////////////////////////////
       double tot=childrenAmt+adultHighAmt+adultMedianAmt+adultLowAmt-referralAmt;
       
	   String yearMon=tableParm.getValue("SUM_YEARMON", selectedRow);
	   TParm printParm =new TParm();
	   printParm.setData("YEAR_MON","TEXT", yearMon.substring(0, 4)+
				"��"+yearMon.substring(5, 6)+"��");//�ں�
	   printParm.setData("INS_TYPE","TEXT", INS_TYPE.getText());//�������
	   printParm.setData("MON_BATCH_NO", "TEXT",tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
	   printParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
	   printParm.setData("REGION_CHN_DESC","TEXT", this.nhi_name) ;
	   printParm.setData("YEAR","TEXT",tableParm.getValue("YEAR", selectedRow)) ;
	   printParm.setData("CHILDREN_TIME","TEXT",df.format(childrenTime)) ;//ѧ����ͯ
	   printParm.setData("CHILDREN_AMT","TEXT",df.format(childrenAmt)) ;
	   printParm.setData("ADULT_HIGH_TIME","TEXT",df.format(adultHighTime)) ;//�����ˣ��ߵ��ɷѣ�
	   printParm.setData("ADULT_HIGH_AMT","TEXT",df.format(adultHighAmt)) ;
	   printParm.setData("ADULT_MEDIAN_TIME","TEXT",df.format(adultMedianTime)) ;//�����ˣ����нɷѣ�
	   printParm.setData("ADULT_MEDIAN_AMT","TEXT",df.format(adultMedianAmt)) ;
	   printParm.setData("ADULT_LOW_TIME","TEXT",df.format(adultLowTime)) ;//�����ˣ��ߵͽɷѣ�
	   printParm.setData("ADULT_LOW_AMT","TEXT",df.format(adultLowAmt)) ;
	   printParm.setData("REFERRAL_TIME","TEXT",df.format(referralTime)) ;//ת��
	   printParm.setData("REFERRAL_AMT","TEXT",df.format(referralAmt)) ;
	   printParm.setData("TOT","TEXT",df.format(tot)) ;//�ϼ�
	   printParm.setData("PAY_AMT","TEXT",df.format(tot)) ;
	   printParm.setData("AMT_IN_WORDS","TEXT",StringUtil.getInstance().numberToWord(tot)) ;
       if(INS_TYPE.getValue().equals("310")){
    	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinDiseaseCZ.jhw",printParm);    	   
       }else if(INS_TYPE.getValue().equals("390")){
    	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinDiseaseCJ.jhw",printParm);    
       }
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
	 * ���
	 */
	public void onClear(){
		this.setValue("YEAR_MON", "") ;
		this.setValue("INS_TYPE", "") ;
		this.setValue("PAY_TYPE", "") ;
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	
	/**
	 * ��ʽ��
	 */
	private String round2(double d){
		 return new DecimalFormat("###########0.00").format(StringTool.round(d, 2));
	}
	public void onInsType(){
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	  /**
	    * ���ֻ��ܱ�
	    */
	   public void onSinPrint(){
			int selectedRow = this.tTable.getSelectedRow() ;

			TParm tableParm=this.tTable.getParmValue();
			TParm parm = new TParm() ;
			parm.addData("NHI_HOSP_NO", this.nhi_no) ;
			String yearMon="";
			if(selectedRow<0){
				String[] beginTimeArray = this.getValueString("YEAR_MON").split("-");
				yearMon=beginTimeArray[0].concat(beginTimeArray[1]);
				parm.addData("YEAR_MON", yearMon);	
				parm.addData("MON_BATCH_NO", "") ;
				parm.addData("SINGLE_BATCH_NO", "") ;
				selectedRow=0;
			}else{
				yearMon=tableParm.getValue("SUM_YEARMON", selectedRow);
			    parm.addData("YEAR_MON", yearMon);	
				parm.addData("MON_BATCH_NO", tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
				parm.addData("SINGLE_BATCH_NO", tableParm.getValue("SINGLE_BATCH_NO", selectedRow)) ;
			}
			parm.addData("INS_TYPE", INS_TYPE.getValue()) ;
			parm.setData("PIPELINE", "DataDown_zjkd") ;
			parm.setData("PLOT_TYPE", "T2") ;
			parm.addData("PARM_COUNT", 5);
//			System.out.println(parm);
			TParm resultParm = InsManager.getInstance().safe(parm,null);
//			System.out.println(resultParm);
		       ///////////////////////////////////////////////////���ݼ���
			int applyVisits=0;
			double applyAmt=0;
			double applyAmtShould=0;
			double retentionMoney=0;
			int varianceCaseVisits=0;
			double varianceCaseAmt=0;
			double servantAmt=0;
			double armyAiAmt=0;
			double medicalHelpAmt=0;
			double civilAmt=0;
			double accountPayAmt=0;
			double refoseAmt=0;
			double totalAmtAdjust=0;
			double insPayAmt=0;

			int rowCount=resultParm.getCount("APPLY_AMT");
			   for(int i=0;i<rowCount;i++){
//				   System.out.println(tot+"::::::"+parm.getDouble("REFUSE_AMT", i));
				   applyVisits=+resultParm.getInt("APPLY_VISITS", i);
				   applyAmt=+resultParm.getDouble("APPLY_AMT", i);//ͳ���������֧�����
				   applyAmtShould=+resultParm.getDouble("APPLY_AMT_SHOULD", i);//ͳ�����Ӧ֧�����
				   retentionMoney=+resultParm.getDouble("RETENTION_MONEY", i);//������֤��
				   varianceCaseVisits=+resultParm.getInt("VARIANCE_CASE_VISITS", i);//���첡���˴�
				   varianceCaseAmt=+resultParm.getDouble("VARIANCE_CASE_AMT", i);//���첡�������ֱ�׼���
				   servantAmt=+resultParm.getDouble("SERVANT_AMT", i);//����Ա����
				   armyAiAmt=+resultParm.getDouble("ARMY_AI_AMT", i);//���в���
				   medicalHelpAmt=+resultParm.getDouble("MEDICAL_HELP_AMT", i);//����ҽ�ƾ���
				   civilAmt=+resultParm.getDouble("CIVIL_AMT", i);//�����Ÿ�
				   accountPayAmt=+resultParm.getDouble("ACCOUNT_PAY_AMT", i);//�����ʻ�
				   refoseAmt=+resultParm.getDouble("REFOSE_AMT", i);//�ܸ����
				   totalAmtAdjust=+resultParm.getDouble("TOTAL_AMT_ADJUST", i);//����֧��
				   insPayAmt=+resultParm.getDouble("INS_PAY_AMT", i);//ʵ��֧�����
			   }
		       //////////////////////////////////////////////////
		       double tot=
		       insPayAmt+
		       servantAmt+
		       armyAiAmt+
		       medicalHelpAmt+
		       civilAmt+
		       accountPayAmt;
	       
		   TParm printParm =new TParm();
		   printParm.setData("YEAR_MON","TEXT", yearMon.substring(0, 4)+
					"��"+yearMon.substring(5, 6)+"��");//�ں�
		   printParm.setData("INS_TYPE","TEXT", INS_TYPE.getSelectedText());//�������
		   printParm.setData("MON_BATCH_NO", "TEXT",tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
		   printParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
		   printParm.setData("REGION_CHN_DESC","TEXT", this.nhi_name) ;
		   printParm.setData("APPLY_AMT","TEXT",df.format(applyAmt)) ;//ͳ���������֧�����01
		   printParm.setData("APPLY_AMT_SHOULD","TEXT",df.format(applyAmtShould)) ;//ͳ�����Ӧ֧�����04
		   printParm.setData("RETENTION_MONEY","TEXT",df.format(retentionMoney)) ;//������֤��05
		   printParm.setData("VARIANCE_CASE_VISITS","TEXT",df.format(varianceCaseVisits)) ;//���첡���˴�07
		   printParm.setData("VARIANCE_CASE_AMT","TEXT",df.format(varianceCaseAmt)) ;//���첡�������ֱ�׼���08
		   printParm.setData("SERVANT_AMT","TEXT",df.format(servantAmt)) ;//����Ա����09
		   printParm.setData("ARMY_AI_AMT","TEXT",df.format(armyAiAmt)) ;//���в���10
		   printParm.setData("MEDICAL_HELP_AMT","TEXT",df.format(medicalHelpAmt)) ;//����ҽ�ƾ���11
		   printParm.setData("CIVIL_AMT","TEXT",df.format(civilAmt)) ;//�����Ÿ�12
		   printParm.setData("ACCOUNT_PAY_AMT","TEXT",df.format(accountPayAmt)) ;//�����ʻ�13
		   printParm.setData("REFOSE_AMT","TEXT",df.format(refoseAmt)) ;//�ܸ����02
		   printParm.setData("TOTAL_AMT_ADJUST","TEXT",df.format(totalAmtAdjust)) ;//����֧��03
		   printParm.setData("INS_PAY_AMT","TEXT",df.format(insPayAmt)) ;//ʵ��֧�����06
		   printParm.setData("TOT","TEXT",df.format(tot)) ;//�ϼ�
		   printParm.setData("APPLY_VISITS","TEXT",df.format(applyVisits)) ;//�ϼ�
//		   printParm.setData("PAY_AMT","TEXT",df.format(tot)) ;
//		   printParm.setData("AMT_IN_WORDS","TEXT",StringUtil.getInstance().numberToWord(tot)) ;
	       if(INS_TYPE.getValue().equals("310")){
	    	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinTotCZ.jhw",printParm);    	   
	       }else if(INS_TYPE.getValue().equals("390")){
	    	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinTotCJ.jhw",printParm);    
	       }
	   } 
		  /**
		   * ��ӡ����л���ҽ�Ʊ��ն���������������
		   */
		  public void DataDown_zjkd_N()
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
				int selectedRow = this.tTable.getSelectedRow() ;

				TParm tableParm=this.tTable.getParmValue();
				TParm parm = new TParm() ;
				parm.addData("NHI_HOSP_NO", this.nhi_no) ;
				String yearMon="";
				if(selectedRow<0){
					String[] beginTimeArray = this.getValueString("YEAR_MON").split("-");
					yearMon=beginTimeArray[0].concat(beginTimeArray[1]);
					parm.addData("YEAR_MON", yearMon);	
					parm.addData("MON_BATCH_NO", "") ;
					parm.addData("SINGLE_BATCH_NO", "") ;
					selectedRow=0;
				}else{
					yearMon=tableParm.getValue("SUM_YEARMON", selectedRow);
				    parm.addData("YEAR_MON", yearMon);	
					parm.addData("MON_BATCH_NO", tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
					parm.addData("SINGLE_BATCH_NO", tableParm.getValue("SINGLE_BATCH_NO", selectedRow)) ;
				}
				parm.addData("INS_TYPE", INS_TYPE.getValue()) ;
				parm.setData("PIPELINE", "DataDown_zjkd") ;
				parm.setData("PLOT_TYPE", "T2") ;
				parm.addData("PARM_COUNT", 5);
//				System.out.println(parm);
				TParm tempParm = InsManager.getInstance().safe(parm,null);
//				System.out.println(""+tempParm);
//			  resultParm.addData("NHI_HOSP_NO", this.nhi_no) ;
//			  resultParm.addData("REGION_CHN_DESC", this.nhi_name) ;
//			  resultParm.addData("YEAR",this.getYear(resultParm.getValue("YEAR_MON", 0))) ;
				TParm resultParm=new TParm();
//				resultParm.setData("YEAR_MON", "201507");
				resultParm.setData("YEAR_MON","TEXT", yearMon.substring(0, 4)+
						"��"+yearMon.substring(5, 6)+"��");//�ں�
				resultParm.setData("INS_TYPE","TEXT", INS_TYPE.getSelectedText());//�������
				resultParm.setData("BATCH_NO","TEXT", tableParm.getValue("SINGLE_BATCH_NO", selectedRow));//��������
				resultParm.setData("NHI_HOSP_NO","TEXT",this.nhi_no) ;//��������
				resultParm.setData("YEAR","TEXT",yearMon.substring(0, 4)) ;//Э�����
				resultParm.setData("REGION_CHN_DESC","TEXT",this.nhi_name) ;//��������

				resultParm.setData("MON_QUOTA_AMT","TEXT",df.format(0)) ;//����ָ��
				resultParm.setData("LPER_OVER_AMT","TEXT",df.format(0)) ;//����ָ�����
				resultParm.setData("PRE_PAY_AMT","TEXT",df.format(0)) ;//���������渶ͳ������
				resultParm.setData("LMON_PRE_OVER_AMT","TEXT",df.format(0)) ;//���������渶����ʣ��
				resultParm.setData("VERIFY_REFUSE_AMT","TEXT",df.format(0)) ;//����ɸ����˾ܸ�
				resultParm.setData("LMON_OVERREF_AMT","TEXT",df.format(0)) ;//����ɸ����˾ܸ�ʣ��
				resultParm.setData("CHANGE_VREFUSE_AMT","TEXT",df.format(0)) ;//����ɸ����˾ܸ�����
				resultParm.setData("TPER_QUOTA_AMT","TEXT",df.format(0)) ;//������Чָ��
				
				resultParm.setData("NHI_AMT","TEXT",df.format(tempParm.getDouble("APPLY_AMT", 0))) ;//��������ͳ������
				resultParm.setData("CHANGE_PAY_AMT","TEXT",df.format(0)) ;//����ͳ�����֧��
				resultParm.setData("REFUSE_AMT","TEXT",df.format(tempParm.getDouble("REFOSE_AMT", 0))) ;//����������˾ܸ�
				resultParm.setData("CHANGE_REFUSE_AMT","TEXT",df.format(0)) ;//����������˾ܸ�����
				resultParm.setData("OVER_TOTAL_AMT","TEXT",df.format(0)) ;//����ͳ�����볬��
				resultParm.setData("TPER_NHI_AMT","TEXT",df.format(tempParm.getDouble("APPLY_AMT", 0))) ;//������Чͳ������
				resultParm.setData("TPER_VERIFY_AMT","TEXT",df.format(0)) ;//����ͳ��Ӧ֧�����
				resultParm.setData("EXA_AMT","TEXT",df.format(tempParm.getDouble("RETENTION_MONEY", 0))) ;//���¿���Ԥ�����

				   double applyAmtShould=+tempParm.getDouble("APPLY_AMT_SHOULD", 0);//ͳ�����Ӧ֧�����
				   double retentionMoney=+tempParm.getDouble("RETENTION_MONEY", 0);//������֤��
				   double insPayAmt=applyAmtShould-retentionMoney;
				resultParm.setData("TPER_PAY_AMT","TEXT",df.format(insPayAmt)) ;//����ͳ��֧�����
				resultParm.setData("QUOTA_OVER_AMT","TEXT",df.format(0)) ;//����ָ�����
				resultParm.setData("OVER_NHI_AMT","TEXT",df.format(0)) ;//����ͳ�����볬��
				resultParm.setData("OTHER_AMT","TEXT","0.00") ;//����
				
				resultParm.setData("LYEAR_GET_AMT","TEXT",df.format(0)) ;//����Ӧ�ջؽ��
				resultParm.setData("LYEAR_GETREAL_AMT","TEXT",df.format(0)) ;//����ʵ���ջؽ��
				resultParm.setData("LYEAR_OVERGET_AMT","TEXT",df.format(0)) ;//�����ջ�ʣ���
				
				resultParm.setData("TMON_PAY_REAL_AMT","TEXT",df.format(insPayAmt)) ;//����ʵ��ͳ��֧��
				resultParm.setData("SERVANT_AMT","TEXT",df.format(tempParm.getDouble("SERVANT_AMT", 0))) ;//����Ա�������
				resultParm.setData("ARMAY_AMT","TEXT",df.format(tempParm.getDouble("ARMY_AI_AMT", 0))) ;//���в������
				resultParm.setData("CANDC_AMT","TEXT",df.format(tempParm.getDouble("MEDICAL_HELP_AMT", 0))) ;//�����������
				resultParm.setData("CIVIL_AMT","TEXT",df.format(tempParm.getDouble("CIVIL_AMT", 0))) ;//�����Ÿ����
				resultParm.setData("ACCOUT_PAY_AMT","TEXT",df.format(tempParm.getDouble("ACCOUNT_PAY_AMT", 0))) ;//�����˻�
				resultParm.setData("TPER_TOTAL_AMT","TEXT",df.format(tempParm.getDouble("INS_PAY_AMT", 0))) ;//����֧���ϼ�

				resultParm.setData("TMON_PAY_REAL_AMT_D","TEXT",StringUtil.getInstance().numberToWord(insPayAmt)) ;//����ʵ��ͳ��֧��
				resultParm.setData("SERVANT_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("SERVANT_AMT", 0))) ;//����Ա�������
				resultParm.setData("ARMAY_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("ARMY_AI_AMT", 0))) ;//���в������
				resultParm.setData("CANDC_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("MEDICAL_HELP_AMT", 0))) ;//�����������
				resultParm.setData("CIVIL_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("CIVIL_AMT", 0))) ;//�����Ÿ����
				resultParm.setData("ACCOUT_PAY_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("ACCOUNT_PAY_AMT", 0))) ;//�����˻�
				resultParm.setData("TPER_TOTAL_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("INS_PAY_AMT", 0))) ;//����֧���ϼ�

				Timestamp date = SystemTool.getInstance().getDate();
				String d=date.toString().substring(0, 10).replace("-", "");
				resultParm.setData("DATE1","TEXT",d.substring(0,4)+"��"+
						d.substring(4,6)+"��"+d.substring(6,8)+"��") ;//�������
				resultParm.setData("DATE2","TEXT",d.substring(0,4)+"��"+
						d.substring(4,6)+"��"+d.substring(6,8)+"��") ;//�������
			    this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinCityCost.jhw",resultParm);
				
		  }  


}
