package com.javahis.ui.ekt;

import jdo.sys.Operator;
//import jdo.sys.SystemTool;



import jdo.sys.SYSRegionTool;

import com.dongyang.data.TNull;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

import jdo.sys.Pat;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import jdo.bil.BILInvoiceTool;
import jdo.bil.BilInvoice;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.xalan.xsltc.runtime.Operators;

import com.dongyang.control.TControl;

import jdo.ekt.EKTIO;
//�ѿ���ԭ      import jdo.ekt.EKTReadCard;
import jdo.ekt.EKTTool;
import jdo.sid.IdCardO;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;

import jdo.sys.PatTool;

import com.javahis.device.card.IccCardRWUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ҽ�ƿ��˷�</p>
 *
 * <p>Description: ҽ�ƿ��˷�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111008
 * @version 1.0
 */
public class EKTUnFeeControl extends TControl {
    public EKTUnFeeControl() {
    } 
 // ��������
	Object paraObject;
	String onwType="";
	String systemCode = "";
	BilInvoice bilInvoice;
    private Pat pat; //������Ϣ
    private TParm parmEKT;
    private boolean ektFlg = false;
    private boolean printBil=false;//��ӡƱ��ʱʹ��
    private TParm parmSum;//ִ���˿��������
    //zhangp 20111227
    private int row = -1;//ѡ����
    private TParm regionParm;
    private TParm insParm;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
    private IccCardRWUtil DEV= new IccCardRWUtil();
    private Boolean dev_flg=true;

   /**
    * ��ʼ������
    */
   public void onInit() {
       //=====zhangp 20120224 ֧����ʽ    modify start 
       String id = EKTTool.getInstance().getPayTypeDefault();
       setValue("GATHER_TYPE", id);
       //=======zhangp 20120224 modify end

		 bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("EKT"));
		callFunction("UI|BIL_CODE|Enable", false);
		callFunction("UI|UN_FEE|setValue", "0");
		 regionParm = SYSRegionTool.getInstance().selectdata(
					Operator.getRegion()); // ���ҽ���������
   }
   /**      
    * ��������
    */
   	public void onReadCard(){
		 /*     //kangy �ѿ���ԭ     start
		  * dev_flg=EKTTool.getInstance().Equipment(Operator.getIP());// add by kangy 20170622 �ж��¾��豸   ���豸==false
   			if(dev_flg){
   				String cardType=DEV.getCardType();
   				if("EKTCard".equals(cardType)){
   					onReadEKT();
   				} else if("IDCard".equals(cardType)){
   					onReadIdCard();
   				} else if("INSCard".equals(cardType)){
   					onReadInsCard();
   				}
   			}else */    //kangy �ѿ���ԭ     end
   				onReadEKT();
}
   /**
    * ��ҽ�ƿ�
    */
   public void onReadEKT() {
   	//==start====add by kangy 20160912 ===��ȡҽ�ƿ�ˢ����һƱ��
   	bilInvoice = new BilInvoice();
		 callFunction("UI|BIL_CODE|setValue", bilInvoice.initBilInvoice("EKT").getUpdateNo());
   	//==end====add by kangy 20160912
	   
       //��ȡҽ�ƿ�   kangy     �ѿ���ԭ     start
	/*	 if(dev_flg){
   		  parmEKT=EKTReadCard.getInstance().readEKT();
   	}else{
   		 parmEKT = EKTReadCard.getInstance().TXreadEKT();
   	}*/
		 parmEKT=EKTIO.getInstance().TXreadEKT();
		 // kangy  �ѿ���ԭ      end
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox(parmEKT.getErrText());
           parmEKT = null;
           return;
       }
       EKT(parmEKT);
 /*      //��Ƭ���
       this.setValue("OLD_EKTFEE", parmEKT.getDouble("CURRENT_BALANCE"));
       //�˿���
       this.setValue("UN_FEE", parmEKT.getDouble("CURRENT_BALANCE"));
       //��Ƭ���
       this.setValue("SEQ", parmEKT.getValue("SEQ"));
       //callFunction("UI|CARD_CODE|setEnabled", false); //���Ų��ɱ༭
       this.setValue("MR_NO", parmEKT.getValue("MR_NO"));

       onQuery();
       //20120104 zhangp
       if(this.getValueString("MR_NO")!=null&&!this.getValueString("MR_NO").equals("")){
    	   onTable();
       }
       //====201202026 zhangp modify start
       grabFocus("UN_FEE");
       //====201202026 zhangp modify end
*/   }

   public void EKT(TParm parmEKT){
	      //��Ƭ���
    this.setValue("OLD_EKTFEE", parmEKT.getDouble("CURRENT_BALANCE"));
    //�˿���
    this.setValue("UN_FEE", parmEKT.getDouble("CURRENT_BALANCE"));
    //��Ƭ���
    this.setValue("SEQ", parmEKT.getValue("SEQ"));
    this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
    onQuery();
    if(this.getValueString("MR_NO")!=null&&!this.getValueString("MR_NO").equals("")){
 	   onTable();
    }
    grabFocus("UN_FEE");
}
//  kangy �ѿ���ԭ   start
/*public void onReadIdCard(){
	//TParm idParm = IdCardO.getInstance().readIdCard();
	TParm idParm= EKTReadCard.getInstance().readIDCard();
	parmEKT=new TParm();
	if(idParm.getCount()==0){
		this.messageBox("�ò���û��ҽ�ƿ�����ִ���ƿ�����");
		return;
	}
	if(idParm.getCount()>1){
		parmEKT = (TParm) openDialog(
				"%ROOT%\\config\\reg\\REGPatMrNoSelect.x", idParm);
	}else
		parmEKT=idParm.getRow(0);
		EKT(parmEKT);
}

public void onReadInsCard(){
	TParm parm = new TParm();
	parm.setData("MR_NO", "");
	//ҽԺ����@���÷���ʱ��@���
	String admDate = StringTool.getString((Timestamp) this
			.getValue("ADM_DATE"), "yyyyMMdd");// ���÷���ʱ��	
	String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+admDate+"@"+"1";
	parm.setData("ADVANCE_CODE",advancecode);
	parm.setData("ADVANCE_TYPE","1");//����
	insParm = (TParm) openDialog(
			"%ROOT%\\config\\ins\\INSReadInsCard.x");
	int returnType = insParm.getInt("RETURN_TYPE"); // ��ȡ״̬ 1.�ɹ� 2.ʧ��
	if (returnType == 0 || returnType == 2) {
		this.messageBox("��ȡҽ����ʧ��");
		return;
	}
	String sql="SELECT  B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E,SYS_PATINFO D"
               + " WHERE "
               //+" E.NHI_NO='6217250200000958634'"
               +" E.NHI_NO='"+insParm.getParm("opbReadCardParm").getValue("CARD_NO").trim()+"' "
               //+" D.IDNO='"+insParm.getParm("opbReadCardParm").getValue("SID").trim()+"'"
               +" AND E.MR_NO=A.MR_NO "
               + " AND A.MR_NO=D.MR_NO "
                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
	TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));
		parmEKT=insParm.getRow(0);
		EKT(parmEKT);
}*/
//kangy �ѿ���ԭ   end   
   /**
    * ����
    */
   public void onSave() {
       //TParm parm=new TParm();
	   bilInvoice = new BilInvoice();
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox("��ȡҽ�ƿ�ʧ��");
           parmEKT = null;
           return;
       }
       if(!ReadKET()){
            this.messageBox("��鿴ҽ�ƿ��Ƿ�����");
             return;
       }
       TParm parm=new TParm();
       parm.setData("MR_NO", pat.getMrNo());
       parm = EKTTool.getInstance().selectEKTIssuelog(parm);
       if (parm.getCount() <= 0) {
           this.messageBox("�˲���û��ҽ�ƿ���Ϣ,�������ƿ�");
           return;
       }
      //=====add by kangy start====20160801====
       if(null==this.getValue("BIL_CODE")||this.getValue("BIL_CODE").toString().length()<=0){
    	   this.messageBox("�޿ɴ�ӡƱ�ݣ�");
    	   return;
       }
       TParm checkparm=new TParm();
       checkparm.setData("RECP_TYPE","EKT");
       checkparm.setData("INV_NO",this.getValue("BIL_CODE"));
   		checkparm.setData("CASHIER_CODE",Operator.getID());
   		TParm res=BILInvoiceTool.getInstance().checkUpdateNo(checkparm);
   		if(res.getCount("RECP_TYPE")>0){
   			this.messageBox("��Ʊ����ʹ�ã�");
   			onClear();
   			return;
   		}
   		if(!compareInvno(bilInvoice.initBilInvoice("EKT").getStartInvno(),bilInvoice.initBilInvoice("EKT").getEndInvno(),this.getValue("BIL_CODE").toString())){
			this.messageBox("Ʊ�ų�����Χ");
			onClear();
			return;
		}
       //=end====add by kangy end====20160801====
       if (this.getValueDouble("UN_FEE") <=0) {
           this.messageBox("�˿����ȷ");
           return;
       }
       if(tempFee()==-1)
           return;
       if (((TTextFormat)this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
          this.messageBox("�˷ѷ�ʽ������Ϊ��ֵ");
          return;
       }
       TParm r = (TParm) openDialog(
               "%ROOT%\\config\\ekt\\EKTPassWordSure.x", parmEKT);
       if (r == null || null== r.getValue("FLG")) {
           return;
       }


       if (r.getValue("FLG").equals("Y"))
           onFEE();
       //parm.setData("CARD_NO", parmEKT.getValue("MR_NO")+parmEKT.getValue("SEQ"));
       onReadCard();
   }

   /**
    * ��ѯ������Ϣ
    */
   public void onQuery() {
       pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
       if (pat == null) {
           this.messageBox("�޴˲�����Ϣ");
           this.grabFocus("PAT_NAME");
           this.setValue("MR_NO", "");
           callFunction("UI|MR_NO|setEnabled", true); //�����ſɱ༭
           return;
       }
       //������
       this.setValue("MR_NO", pat.getMrNo());
       //����
       this.setValue("PAT_NAME", pat.getName());
       //��������
       setValue("BIRTH_DATE", pat.getBirthday());
       //�Ա�
       setValue("SEX_CODE", pat.getSexCode());
       callFunction("UI|MR_NO|setEnabled", false); //�����ſɱ༭
     //===zhangp 20120328 start
       TParm parm=new TParm();
       parm.setData("MR_NO",pat.getMrNo());
       TParm EKTparm= EKTTool.getInstance().selectEKTIssuelog(parm);
       if (EKTparm.getCount() <= 0) {
       	messageBox("��ҽ�ƿ�");
       	return;
       }
       this.setValue("OLD_EKTFEE",StringTool.round(EKTparm.getDouble("CURRENT_BALANCE", 0),2) );
       //===zhangp 20120328 end
   }

   /**
    * ��ֵ�ı���س��¼�
    */
   public void addFee() {
       if (this.getValueDouble("UN_FEE") < 0) {
           this.messageBox("�˿������Ϊ��ֵ");
           return;
       }
       double tempFee=tempFee();
       this.setValue("SUM_EKTFEE",
                     tempFee==-1?0.00:tempFee);

   }
   /**
    * �˷ѽ����Դ��ڿ������
    * @return boolean
    */
   private double tempFee() {
       double tempFee = this.getValueDouble("OLD_EKTFEE") -
                        this.getValueDouble("UN_FEE");
       if (tempFee < 0) {
           this.messageBox("�˿�����Գ����������");
           return -1;
       }
       return tempFee;
   }
   /**
    * �˷ѷ���
    */
   private void onFEE() {
	   bilInvoice = new BilInvoice();

       TParm result = null;
           parmSum = new TParm();
           parmSum.setData("CARD_NO", pat.getMrNo() + this.getValue("SEQ"));
           parmSum.setData("CURRENT_BALANCE", StringTool.round(parmEKT.getDouble("CURRENT_BALANCE"),
                   2)-
                   StringTool.round(this.getValueDouble("UN_FEE"), 2));
           parmSum.setData("CASE_NO", "none");
           parmSum.setData("NAME", pat.getName());
           parmSum.setData("MR_NO", pat.getMrNo());
           parmSum.setData("ID_NO",
                     null != pat.getIdNo() && pat.getIdNo().length() > 0 ?
                     pat.getIdNo() : "none");
           parmSum.setData("OPT_USER", Operator.getID());
           parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
           parmSum.setData("OPT_TERM", Operator.getIP());
           parmSum.setData("FLG", ektFlg);
           parmSum.setData("ISSUERSN_CODE", "�˷�"); //����ԭ��
           parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //֧����ʽ
           parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); //֧����ʽ����
           parmSum.setData("BUSINESS_AMT",
                           StringTool.round(this.getValueDouble("UN_FEE"), 2)); //��ֵ���
           parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); //�Ա�
           parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); //��ע
           parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); //Ʊ�ݺ�
           parmSum.setData("PRINT_NO", bilInvoice.initBilInvoice("EKT").getUpdateNo()); //Ʊ�ݺ�
           parmSum.setData("CREAT_USER", Operator.getID()); //ִ����Ա//=====yanjing
           //-----------add by kangy   20160804
           TParm outFeeParm=new TParm();
			outFeeParm.setData("RECP_TYPE","EKT");
			outFeeParm.setData("INV_NO",this.getValueString("BIL_CODE"));
			//outFeeParm.setData("RECEIPT_NO",bil_business_no);
			outFeeParm.setData("CASHIER_CODE",Operator.getID());
			outFeeParm.setData("AR_AMT",this.getValue("UN_FEE"));
			outFeeParm.setData("CANCEL_FLG","1");
			outFeeParm.setData("CANCEL_USER","");
			outFeeParm.setData("CANCEL_DATE","");
			outFeeParm.setData("OPT_USER",Operator.getID().toString());
			outFeeParm.setData("OPT_TERM",Operator.getIP().toString());
			outFeeParm.setData("ACCOUNT_FLG","");
			outFeeParm.setData("ACCOUNT_SEQ","");
			outFeeParm.setData("ACCOUNT_USER","");
			outFeeParm.setData("ACCOUNT_DATE","");
			outFeeParm.setData("PRINT_USER",Operator.getID());
			outFeeParm.setData("PRINT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
			outFeeParm.setData("ADM_TYPE","T");
	        outFeeParm.setData("STATUS","0");
	        parmSum.setData("infeeparm",outFeeParm.getData());
	        //BILInvoiceTool.getInstance().insertFeeDate(outFeeParm);
           
           
           String updateno = StringTool.addString(bilInvoice.initBilInvoice("EKT").getUpdateNo());
           TParm updatanoParm=new TParm();
           BilInvoice bilInvo=bilInvoice.initBilInvoice("EKT");
           updatanoParm.setData("UPDATE_NO",updateno);
           updatanoParm.setData("RECP_TYPE","EKT");
           updatanoParm.setData("STATUS",bilInvo.getStatus());
           updatanoParm.setData("CASHIER_CODE",bilInvo.getCashierCode());
           updatanoParm.setData("START_INVNO",bilInvo.getStartInvno());
           parmSum.setData("updatanoparm",updatanoParm.getData());
           // BILInvoiceTool.getInstance().updateDatePrint(updatenoParm);
          
           //------add by kangy  20160804
           //��ϸ�����
           TParm feeParm = new TParm();
           feeParm.setData("ORIGINAL_BALANCE", 
                           StringTool.round(parmEKT.getDouble("CURRENT_BALANCE"),2)); //ԭ���
           feeParm.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("UN_FEE"), 2)); //�˷ѽ��
           feeParm.setData("CURRENT_BALANCE",
                           StringTool.round(parmEKT.
                                            getDouble("CURRENT_BALANCE"), 2) -
                           StringTool.round(this.getValueDouble("UN_FEE"),
                                            2));
           parmSum.setData("businessParm", getBusinessParm(parmSum, feeParm).getData());
           //zhangp 20120109 EKT_BIL_PAY ���ֶ�
           parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
           parmSum.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
           //bil_pay ��ֵ������
           parmSum.setData("billParm", getBillParm(parmSum, feeParm).getData());
           //System.out.println("parm______"+parmSum);
           //�������
           result = TIOM_AppServer.executeAction(
                   "action.ekt.EKTAction",
                   "TXEKTonFee", parmSum); 
           if (result.getErrCode() < 0) {
               this.messageBox("ҽ�ƿ��˷�ʧ��");
		} else {
			this.messageBox("�˷ѳɹ�");
            
			printBil = true;
			String bil_business_no = result.getValue("BIL_BUSINESS_NO");// �վݺ�
			try {
				onPrint(bil_business_no, "");
			} catch (Exception e) {
				this.messageBox("��ӡ��������,��ִ�в�ӡ����");
				// TODO: handle exception
			}
			
			
		}
       //zhangp 20120131 �˷���ɺ����²�ѯ�˷Ѽ�¼
       onTable();
       onClear();
       onInit();
   }

   /**
    * ҽ�ƿ���ϸ���������
    * @param p TParm
    * @param feeParm TParm
    * @return TParm
    */
   private TParm getBusinessParm(TParm p, TParm feeParm) {
       // ��ϸ������
       TParm bilParm = new TParm();
       bilParm.setData("BUSINESS_SEQ", 0);
       bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
       bilParm.setData("MR_NO", pat.getMrNo());
       bilParm.setData("CASE_NO", "none");
       bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
       bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
       bilParm.setData("SEQ_NO", 0);
       bilParm.setData("CHARGE_FLG", "7"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,6���ϣ�7��ҽ�ƿ��˷�)
       bilParm.setData("ORIGINAL_BALANCE", feeParm.getValue("ORIGINAL_BALANCE")); //�շ�ǰ���
       bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
       bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
       bilParm.setData("CASHIER_CODE", Operator.getID());
       bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
       //1������ִ�����
       //2��˫��ȷ�����
       bilParm.setData("BUSINESS_STATUS", "1");
       //1��δ����
       //2�����˳ɹ�
       //3������ʧ��
       bilParm.setData("ACCNT_STATUS", "1");
       bilParm.setData("ACCNT_USER", new TNull(String.class));
       bilParm.setData("ACCNT_DATE", new TNull(Timestamp.class));
       bilParm.setData("OPT_USER", Operator.getID());
       bilParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
       bilParm.setData("OPT_TERM", Operator.getIP());
       // p.setData("bilParm",bilParm.getData());
       return bilParm;
   }

   /**
    * ��ֵ��������ݲ���
    * @param parm TParm
    * @return TParm
    */
   private TParm getBillParm(TParm parm, TParm feeParm) {
       TParm billParm = new TParm();
       billParm.setData("CARD_NO", parm.getValue("CARD_NO")); //����
       billParm.setData("CURT_CARDSEQ", 0); //���
       billParm.setData("ACCNT_TYPE", "6"); //��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
       billParm.setData("MR_NO", parm.getValue("MR_NO")); //������
       billParm.setData("ID_NO", parm.getValue("ID_NO")); //���֤��
       billParm.setData("NAME", parm.getValue("NAME")); //��������
       billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT")); //�˷ѽ��
       billParm.setData("CREAT_USER", Operator.getID()); //ִ����Ա
       billParm.setData("OPT_USER", Operator.getID()); //������Ա
       billParm.setData("OPT_TERM", Operator.getIP()); //ִ��ip
       billParm.setData("GATHER_TYPE", parm.getValue("GATHER_TYPE")); //֧����ʽ
	   //zhangp 20120109
       billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
       billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
       return billParm;
   }

   private boolean ReadKET() {
       //��ȡҽ�ƿ�
       TParm parm = EKTIO.getInstance().TXreadEKT();
       if (null == parm || parm.getErrCode() < 0 ||
           parm.getValue("MR_NO").length() <= 0) {
           return false;
       }
       return true;
   }
   /**
    *���
    */
   public void onClear() {
       clearValue(" MR_NO;PAT_NAME;SEQ; " +
                  " BIRTH_DATE;SEX_CODE;UN_FEE; " +
                  " DESCRIPTION; " +
                  " OLD_EKTFEE;SUM_EKTFEE");
       //����Ĭ�Ϸ���ȼ�
      // txEKT = false; //̩��ҽ�ƿ�д���ܿ�
       parmEKT = null; //ҽ�ƿ�����parm
       ektFlg=false;
       printBil=false;
       printBil=false;//��ӡƱ��ʱʹ��
       parmSum=null;//ִ���˿��������
       //��������
       if (pat != null)
           PatTool.getInstance().unLockPat(pat.getMrNo());
       String id = EKTTool.getInstance().getPayTypeDefault();
       setValue("GATHER_TYPE", id);
       //===zhangp 20120328 start
       callFunction("UI|MR_NO|setEnabled", true); //�����ſɱ༭
       //===zhangp 20120328 end
       onInit();
   }
   /**
    * �˿��ӡ
    */
   private void onPrint(String bil_business_no,String copy) {
       if (!printBil) {
           this.messageBox("����ҽ�ƿ���ֵ�����ſ��Դ�ӡ");
           return;
       }
       TParm parm = new TParm();
       parm.setData("TITLE", "TEXT",
                    (Operator.getRegion() != null &&
                     Operator.getRegion().length() > 0 ?
                     Operator.getHospitalCHNFullName() : "����ҽԺ"));
       parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //������
       parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //����
       //====zhangp 20120525 start
//       parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�˿ʽ
       parm.setData("GATHER_NAME", "TEXT", "��"); //�տʽ
     //====zhangp 20120525 end
       parm.setData("TYPE", "TEXT", "Ԥ ��"); //�ı�Ԥ�˽��
       parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //�տʽ
       parm.setData("AMT", "TEXT", StringTool.round(parmSum.getDouble("BUSINESS_AMT"),2)); //���
       parm.setData("SEX_TYPE", "TEXT", parmSum.getValue("SEX_TYPE").equals("1")?"��":"Ů"); //�Ա�
       parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(parmSum.getDouble("BUSINESS_AMT"))); //��д���
       parm.setData("TOP1", "TEXT", "EKTRT001 FROM "+Operator.getID()); //̨ͷһ
       String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
               getInstance().getDBTime()), "yyyyMMdd"); //������
       String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
               getInstance().getDBTime()), "hhmmss"); //ʱ����
       parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //̨ͷ��
       yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
               getDBTime()), "yyyy/MM/dd"); //������
       hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); //ʱ����
       parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); //��ע
       parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); //Ʊ�ݺ�
       parm.setData("PRINT_NO", "TEXT", parmSum.getValue("PRINT_NO")); //Ʊ�ݺ�
       if(null == bil_business_no)
            bil_business_no=  EKTTool.getInstance().getBillBusinessNo();//��ӡ����
       parm.setData("ONFEE_NO", "TEXT", bil_business_no); //�վݺ�
       parm.setData("PRINT_DATE", "TEXT", yMd); //��ӡʱ��
       parm.setData("DATE", "TEXT", yMd + "    " + hms); //����
       parm.setData("USER_NAME", "TEXT", Operator.getID()); //�տ���
       parm.setData("COPY", "TEXT", copy); //��ӡע��
       //===zhangp 20120525 start
       parm.setData("O", "TEXT", "o"); 
//       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_UNFEE.jhw", parm,true);
       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
       //===zhangp 20120525 end
   }
   /**
    * ��ӡ 
    * 20111228 zhangp
    */
   public void onRePrint(){
		//=start===add by kangy ==20160926===
	   bilInvoice = new BilInvoice();
	   if (null == parmEKT || parmEKT.getErrCode() < 0 ||
	            parmEKT.getValue("MR_NO").length() <= 0) {
	            this.messageBox("����ִ�ж�������");
	            parmEKT = null;
	            return;
	        }
   	//===end===add by kangy ==20160926===
	   TParm result=null;
	   TTable table = (TTable)this.callFunction("UI|TABLE|getThis");
 		row = table.getSelectedRow();
	   if(row==-1){
		   messageBox("��ѡ��Ҫ��ӡ�ļ�¼");
		   return;
	   }else{
		   if(null==this.getValue("BIL_CODE")||this.getValue("BIL_CODE").toString().length()<=0){
	        	this.messageBox("�޿ɴ�ӡƱ�ݣ�");
	        	return;
	        }
		     
	        TParm checkparm=new TParm();
	    	checkparm.setData("RECP_TYPE","EKT");
	    	checkparm.setData("INV_NO",this.getValue("BIL_CODE"));
	    	checkparm.setData("CASHIER_CODE",Operator.getID());
	    	TParm res=BILInvoiceTool.getInstance().checkUpdateNo(checkparm);
	    	if(res.getCount("RECP_TYPE")>0){
	    		this.messageBox("��Ʊ���ѱ�ʹ�ã�");
	    		onClear();
	    		return;
	    	}
			if(!compareInvno(bilInvoice.initBilInvoice("EKT").getStartInvno(),bilInvoice.initBilInvoice("EKT").getEndInvno(),this.getValue("BIL_CODE").toString())){
				this.messageBox("Ʊ�ų�����Χ");
				onClear();
				return;
			}
		   
	    	String bilBusinessNo = table.getValueAt(row, 1).toString();
	  		String print_no = table.getValueAt(row, 7).toString();
	  		String sql = "SELECT MR_NO FROM EKT_BIL_PAY WHERE BIL_BUSINESS_NO = '"+bilBusinessNo+"'";
	  		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
	  		String mrNo = resultParm.getData("MR_NO", 0).toString();
	  		pat = pat.onQueryByMrNo(mrNo);
		   TParm parm = new TParm();
	       parm.setData("TITLE", "TEXT",
	                    (Operator.getRegion() != null &&
	                     Operator.getRegion().length() > 0 ?
	                     Operator.getHospitalCHNFullName() : "����ҽԺ"));
	       parm.setData("MR_NO", "TEXT", mrNo); //������
	       parm.setData("PAT_NAME", "TEXT", pat.getName()); //����
	       //====zhangp 20120525 start
//	       parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�˿ʽ
	       parm.setData("GATHER_NAME", "TEXT", "��"); //�տʽ
	       //====zhangp 20120525 end
	       parm.setData("TYPE", "TEXT", "Ԥ ��"); //�ı�Ԥ�˽��
	       String gatherType = table.getValueAt(row, 4).toString();
	 	      String sqlgt =
	 	            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' AND ID = '"+gatherType+"' ORDER BY SEQ,ID ";
	 	      TParm resultgt = new TParm(TJDODBTool.getInstance().select(sqlgt));
	 	       parm.setData("GATHER_TYPE", "TEXT", resultgt.getData("NAME", 0).toString()); //�տʽ
	       parm.setData("AMT", "TEXT", StringTool.round((Double)table.getValueAt(row, 3),2)); //���
	       parm.setData("SEX_TYPE", "TEXT", pat.getSexString()); //�Ա�
	       parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord((Double)table.getValueAt(row, 3))); //��д���
	       parm.setData("TOP1", "TEXT", "EKTRT001 FROM "+Operator.getID()); //̨ͷһ
	       String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
	               getInstance().getDBTime()), "yyyyMMdd"); //������
	       String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
	               getInstance().getDBTime()), "hhmmss"); //ʱ����
	       parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //̨ͷ��
	       yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
	               getDBTime()), "yyyy/MM/dd"); //������
	       hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
	               getDBTime()), "hh:mm"); //ʱ����
	       parm.setData("DESCRIPTION", "TEXT", ""); //��ע
	       parm.setData("BILL_NO", "TEXT", ""); //Ʊ�ݺ�
	       parm.setData("INV_NO", "TEXT", this.getValue("BIL_CODE").toString());//Ʊ�ݺ�
 	     /*  String s="SELECT PRINT_NO FROM EKT_BIL_PAY WHERE BIL_BUSINESS_NO='"+bilBusinessNo+"'";
 	       TParm printNoparm=new TParm(TJDODBTool.getInstance().select(s));
 	       parm.setData("PRINT_NO","TEXT",printNoparm.getData("PRINT_NO",0));*/
	       parm.setData("PRINT_NO","TEXT",bilInvoice.initBilInvoice("EKT").getUpdateNo());
	       parm.setData("ONFEE_NO", "TEXT", bilBusinessNo); //�վݺ�
	       parm.setData("PRINT_DATE", "TEXT", yMd); //��ӡʱ��
	       parm.setData("DATE", "TEXT", yMd + "    " + hms); //����
	       parm.setData("USER_NAME", "TEXT", Operator.getID()); //�տ���
	       parm.setData("COPY", "TEXT", "(copy)"); //��ӡע��
	       //===zhangp 20120525 start
	       parm.setData("O", "TEXT", "o"); 
	      
	       BilInvoice bilInvo=bilInvoice.initBilInvoice("EKT");
	 	      parm.setData("START_INVNO",bilInvo.getStartInvno());
	 	      
	/* 	     String sqls="SELECT ACCOUNT_FLG,ACCOUNT_SEQ,ACCOUNT_USER,ACCOUNT_DATE FROM BIL_INVRCP WHERE " +
				" RECP_TYPE='EKT'  AND  RECEIPT_NO='"+bilBusinessNo+"' AND INV_NO='"+print_no+"'";
			TParm reprint=new TParm(TJDODBTool.getInstance().select(sqls));
			System.out.println(reprint);*/
			TParm insertparm=new TParm();
	 	       insertparm.setData("RECP_TYPE","EKT");
	 	       insertparm.setData("INV_NO",bilInvoice.initBilInvoice("EKT").getUpdateNo());
	 	       insertparm.setData("RECEIPT_NO",bilBusinessNo);
	 	       insertparm.setData("CASHIER_CODE",Operator.getID());
	 	       insertparm.setData("AR_AMT",StringTool.round((Double)table.getValueAt(row, 3),2));
	 	       insertparm.setData("CANCEL_FLG","1");
	 	       insertparm.setData("CANCEL_USER","");
	 	       insertparm.setData("CANCEL_DATE","");
	 	       insertparm.setData("OPT_USER",Operator.getID());
	 	       insertparm.setData("OPT_DAT",sdf.format(TJDODBTool.getInstance().getDBTime()));
	 	       insertparm.setData("OPT_TERM",Operator.getIP());
	 	   
	 	       insertparm.setData("ACCOUNT_FLG","");
	 	       insertparm.setData("ACCOUNT_SEQ","");
	 	       insertparm.setData("ACCOUNT_USER","");
	 	       insertparm.setData("ACCOUNT_DATE","");
	 	       
	 	       insertparm.setData("PRINT_USER",Operator.getID());
	 	       insertparm.setData("PRINT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
	 	       insertparm.setData("ADM_TYPE","T");
	 	       insertparm.setData("STATUS","0");
	 	       parm.setData("insertparm",insertparm.getData());
	 	      
	 	      TParm updateparm=new TParm();
	 	      updateparm.setData("RECP_TYPE","EKT");
	 	      updateparm.setData("INV_NO",table.getParmValue().getValue("PRINT_NO",row));
	 	      updateparm.setData("RECEIPT_NO",bilBusinessNo);
	 	      updateparm.setData("CANCEL_FLG","3");//��ӡ����
	 	     updateparm.setData("CANCEL_USER",Operator.getID());
	 	      updateparm.setData("CANCEL_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
	 	      parm.setData("updateparm",updateparm.getData());
	 	      
	 	      TParm updateinvoiceparm=new TParm();
	 	     updateinvoiceparm.setData("RECP_TYPE","EKT");
	 	     updateinvoiceparm.setData("START_INVNO",bilInvo.getStartInvno());
	 	     updateinvoiceparm.setData("STATUS",bilInvo.getStatus());
	 	     updateinvoiceparm.setData("UPDATE_NO",StringTool.addString(bilInvoice.initBilInvoice("EKT").getUpdateNo()));
	 	     updateinvoiceparm.setData("CASHIER_CODE",bilInvo.getCashierCode());
	 	     parm.setData("updateinvoiceparm",updateinvoiceparm.getData()); 
	 	   
	 	     
	 	     TParm updateektbilpayparm=new TParm();
	 	    updateektbilpayparm.setData("PRINT_NO",bilInvoice.initBilInvoice("EKT").getUpdateNo());
	 	    updateektbilpayparm.setData("BIL_BUSINESS_NO",table.getValueAt(row, 1));
	 	     parm.setData("updateektbilpayparm",updateektbilpayparm.getData()); 
	 	    //System.out.println("ddddddddd"+parm);
	 	     result = TIOM_AppServer.executeAction(
	                   "action.ekt.EKTAction",
	                   "TXEKTReprint", parm); 
	 	    if (result.getErrCode() < 0) {
                this.messageBox("��ӡʧ��");
            } else {
//	       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_UNFEE.jhw", parm,true);
	       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
	       //===zhangp 20120525 end
	       onInit();
	       onQuery();
	   }
	 	    }
    }
   /**
    *ҽ�ƿ���ֵ�˷Ѽ�¼table
    *zhangp 20111228
    */
   public void onTable(){
	   String mrNo = getValueString("MR_NO");
	   StringBuilder sql = new StringBuilder();
	   String select = "SELECT B.EKT_CARD_NO,A.BIL_BUSINESS_NO," +
	   		"A.OPT_DATE,A.AMT,A.GATHER_TYPE,A.CREAT_USER,A.ACCNT_TYPE,A.PRINT_NO FROM EKT_BIL_PAY A, " +
	   		"EKT_ISSUELOG B where A.CARD_NO = B.CARD_NO AND " +
	   		"A.ACCNT_TYPE = '6'";
	   sql.append(select);
	   if(!mrNo.equals("")&&mrNo!=null){
		   sql.append(" AND A.MR_NO = '"+mrNo+"'");
	   }
	   sql.append(" ORDER BY A.OPT_DATE");
	   TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
       if (result.getErrCode() < 0) {
         messageBox(result.getErrText());
         return;
       }
       ((TTable)getComponent("TABLE")).setParmValue(result);
   }
   
   /**
    * ��ѯ��������
    * =====zhangp 20120328
    */
   public void onQueryPatName(){
   	TParm sendParm = new TParm();
       sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
       TParm reParm = (TParm)this.openDialog(
           "%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
       if(reParm==null)
           return;
       this.setValue("MR_NO", reParm.getValue("MR_NO"));
       this.onQuery();
   }
	/**
	 * ��ʼ��Ʊ��
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// ��˿�����
		if (bilInvoice == null) {
			this.messageBox_("����δ����!");
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
			// this.onClear();
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("���һ��Ʊ��!");
		}
		String endNo_num = bilInvoice.getEndInvno().replaceAll("[^0-9]", "");
		String endNo_word = bilInvoice.getEndInvno().replaceAll("[0-9]", "");
		String nowNo_num = bilInvoice.getUpdateNo().replaceAll("[^0-9]", "");
		String nowNo_word = bilInvoice.getUpdateNo().replaceAll("[0-9]", "");
		if(nowNo_word.equals(endNo_word)&&Long.valueOf(nowNo_num)- Long.valueOf(endNo_num)==1){
			this.messageBox("Ʊ����ʹ���꣬��������Ʊ");
			this.setValue("BIL_CODE","");
			return false;
		}
		if(!compareInvno(bilInvoice.getStartInvno(),bilInvoice.getEndInvno(),bilInvoice.getUpdateNo())){
			this.messageBox("Ʊ�ų�����Χ");
			onClear();
			return false;
		}
		callFunction("UI|BIL_CODE|setValue", bilInvoice.getUpdateNo());
		return true;
	}
	/**
	 * �Ƚ�Ʊ��
	 * @return
	 */
	private boolean compareInvno(String StartInvno, String EndInvno,String UpdateNo) {
		String startNo_num = StartInvno.replaceAll("[^0-9]", "");// ȥ������
		String startNo_word = StartInvno.replaceAll("[0-9]", "");// ȥ����
		String endNo_num = EndInvno.replaceAll("[^0-9]", "");
		String endNo_word = EndInvno.replaceAll("[0-9]", "");
		String nowNo_num = UpdateNo.replaceAll("[^0-9]", "");
		String nowNo_word = UpdateNo.replaceAll("[0-9]", "");
		if (startNo_word.equals(endNo_word)&&startNo_word.equals(nowNo_word)){
			if(Long.valueOf(endNo_num)- Long.valueOf(nowNo_num)>=0&&Long.valueOf(nowNo_num)- Long.valueOf(startNo_num)>=0){
			return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}
}
