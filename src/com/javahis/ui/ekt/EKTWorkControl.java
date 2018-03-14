package com.javahis.ui.ekt;


import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

import jdo.bil.BILInvoiceTool;
import jdo.bil.BilInvoice;
import jdo.bil.InvoiceTool;
import jdo.ekt.EKTIO;
//�ѿ���ԭ     import jdo.ekt.EKTReadCard;
import jdo.sid.IdCardO;
import jdo.sys.PatTool;
import jdo.sys.Pat;

import com.dongyang.util.TypeTool;

import jdo.sys.SYSPostTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.StringUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.sys.SYSHzpyTool;
import jdo.ekt.EKTTool;

import com.dongyang.util.StringTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTextFormat;

import jdo.sys.OperatorTool;

/**
 * <p>Title: ҽ�ƿ�����</p>
 *
 * <p>Description: ҽ�ƿ�����</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110928
 * @version 1.0
 */
public class EKTWorkControl extends TControl{
	// ��������
	Object paraObject;
	String onwType="";
	BilInvoice bilInvoice;
	   private Pat  pat;//  ������Ϣ
	   //private boolean txEKT=false;//ҽ�ƿ���ȡд������
	   private TParm parmEKT;//ҽ�ƿ�����
	   private boolean ektFlg=false;//ҽ�ƿ�����:true:��һ�δ���
	   private TParm EKTTemp;//ҽ�ƿ����ֵ
	   private TParm parmSum;//ִ���˿��������
	   private boolean bankFlg=false;//���п���������
	   /**
	    * zhangp 20121216 ���벡����
	    */
	   private TParm acceptData = new TParm(); //�Ӳ�
	   String systemCode = "";
	   //20120113 zhangp �½����ж� 1�� 2��
	   private int newFlag = 2;
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	   // private boolean dev_flg= false;   kangy �ѿ���ԭ   
    public EKTWorkControl() {
    }
    /**
    * ��ʼ������
    */
   public void onInit() {
	   callFunction("UI|BIL_CODE|Enable", false);
      setValue("CTZ1_CODE", "99");
      /**
       * zhangp 20121216 ����Ĭ���й�
       */
      setValue("NATION_CODE", "86");
      /**
       * zhangp 20121216 ���벡����
       */
      Object obj = this.getParameter();
      if (obj instanceof TParm) {
          acceptData = (TParm) obj;
          String mrNo = acceptData.getData("MR_NO").toString();
          //zhangp 20120113
          this.setValue("MR_NO", mrNo);
          this.onQueryNO();
      }
      //zhangp 20111227 ������Ĭ��ֵ
            TParm result = EKTTool.getInstance().sendCause();
      if(result.getCount()>0){
    	  setValue("SEND_CAUSE", result.getData("ID", 0));
      }
      //zhangp 20120201 ������
      result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
      if(result.getCount()>0){
    	  setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
      }
      //=====zhangp 20120224 ֧����ʽ    modify start 
      String id = EKTTool.getInstance().getPayTypeDefault();
      setValue("GATHER_TYPE", id);
      setPassWord();
      //=======zhangp 20120224 modify end
      paraObject = this.getParameter();
		if (paraObject != null && paraObject.toString().length() > 0) {
			// System.out.println("this.getParameter()+"+this.getParameter());
			TParm paraParm = (TParm) this.getParameter();
			if (paraParm != null && paraParm.getData("SYSTEM") != null) {
				systemCode = paraParm.getValue("SYSTEM");
			}
			if (paraParm != null && paraParm.getData("ONW_TYPE") != null) {
				onwType = paraParm.getValue("ONW_TYPE");//=====pangben 2013-5-15 �ż��ﻤʿվ����ʹ�ã�������ͬ�Ľ���
			}
			
		}
		// ��ʼ��Ʊ��
		 bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("EKT"));
		
		 callFunction("UI|TOP_UP_PRICE|setValue", "0");
		 //kangy �ѿ���ԭ   dev_flg=EKTTool.getInstance().Equipment(Operator.getIP());// add by kangy 20170622 �ж��¾��豸   ���豸==false
			
   }
	   
   /**
    * ��ѯ����
    */
   public void onQuery(){
       onQueryNO(true);
   }

   /**
    * �������ı����¼�
    */
   public void onQueryNO() {
       onQueryNO(true);
   }

   /**
    * ��ѯ����
    */
   public void onQueryNO(boolean flg) {
//       if (pat != null)
//           PatTool.getInstance().unLockPat(pat.getMrNo());
       pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
       if (pat == null) {
           this.messageBox("�޴˲�����!");
           this.grabFocus("PAT_NAME");
           if(!flg){
               this.setValue("MR_NO", "");
               callFunction("UI|MR_NO|setEnabled", false); //�����ſɱ༭
           }
           return;
       }
    // modify by huangtt 20160930 EMPI���߲�����ʾ start
       String mrNo =  PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
       if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
		}
    // modify by huangtt 20160930 EMPI���߲�����ʾ end  
       setValue("MR_NO",mrNo);
        setValue("PAT_NAME", pat.getName());
        setValue("PAT_NAME1", pat.getName1());
        setValue("PY1", pat.getPy1());
        setValue("IDNO", pat.getIdNo());
        setValue("NATION_CODE",pat.getNationCode());
      //zhangp 20111223 ����
        if(pat.getNationCode().equals("")||pat.getNationCode()==null){
     	   setValue("NATION_CODE", "86");
        }
        setValue("FOREIGNER_FLG", pat.isForeignerFlg());
        setValue("BIRTH_DATE", pat.getBirthday());
        setValue("SEX_CODE", pat.getSexCode());
        setValue("TEL_HOME", pat.getTelHome());
        setValue("POST_CODE", pat.getPostCode());
        onPost();
        setValue("ADDRESS", pat.getAddress());
        setValue("CTZ1_CODE", pat.getCtz1Code());
        setValue("CTZ2_CODE", pat.getCtz2Code());
        setValue("CTZ3_CODE", pat.getCtz3Code());
//        setValue("REG_CTZ3", getValue("CTZ3_CODE"));
        //=====zhangp 20120227 modify start
//        patLock();
        //=====zhangp 20120227 modify end
        //��������Ϣ
//            this.messageBox_("�����ɹ�!");//����ר��
        this.grabFocus("onFee");
        callFunction("UI|MR_NO|setEnabled", false); //�����Ų��ɱ༭
        TParm parm=new TParm();
       parm.setData("MR_NO",pat.getMrNo());
       parmEKT= EKTTool.getInstance().selectEKTIssuelog(parm);
       if (parmEKT.getCount() <= 0) {
//           this.messageBox("�˲���û��ҽ�ƿ���Ϣ");
           //�¿�����
           this.setValue("CARD_CODE", pat.getMrNo() + "001");
           //������ҽ�ƿ���д��ʱ��������EKT_MASTER����Ϣ
           ektFlg = true;
       } else {
//           this.messageBox("�˲����Ѿ�����ҽ�ƿ���Ϣ");//=====zhangp 20120225 modify start
           this.setValue("CARD_CODE", parmEKT.getValue("CARD_NO", 0));
           this.setValue("EKT_CARD_NO", parmEKT.getValue("EKT_CARD_NO", 0));
           //zhangp 20111230
           setValue("EKTMR_NO", getValue("MR_NO"));
           setValue("EKTCARD_CODE", getValue("EKT_CARD_NO"));
           this.setValue("CURRENT_BALANCE",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2) );
           bankFlg=true;//���п�ִ�й���
       }
   }
   /**
    * �����ע���¼�
    */
   public void onSelForeieignerFlg() {
       if (this.getValue("FOREIGNER_FLG").equals("Y"))
           this.grabFocus("BIRTH_DATE");
       if (this.getValue("FOREIGNER_FLG").equals("N"))
           this.grabFocus("IDNO");
   }

   /**
    * �½�
    */
   //kangy �ѿ���ԭ        public void onNew(String buyCardType){
   public void onNew(){
//       if (!txReadEKT()) {
//           return;
//       }
       TParm parm = new TParm();
       String card= this.getValue("CARD_CODE").toString();
       /**
        * zhangp 20111216 ���ֶ�
        */
       String ektCardNo= this.getValue("EKT_CARD_NO").toString();
       if (pat == null || pat.getMrNo().length()<=0) {
           this.messageBox("���Ȳ�ѯ������Ϣ");
           return;
       }
       //ektFlg=false �������½�ҽ�ƿ���Ϣ������ִ��
       if(!ektFlg){
           this.messageBox("�˲�������ҽ�ƿ���Ϣ,��ִ�й�ʧ/��������");
           return ;
       }

//       if(card.length()!=15){
//           this.messageBox("�����������,�����ַ�����Ҫ��ʮ��λ");
//           return ;
//       }
       if (this.messageBox("�½���", "�Ƿ�ִ���ƿ�����", 0) != 0) {
           return;
       }
       //����
      if (this.getValue("CARD_PWD").toString().length() <= 0) {
          this.messageBox("����������");
          return;
      }
      //===zhangp 20120315 start
      if(!passWord()){
    	  return;
      }
      //===zhangp 20120315 end
       if(((TTextFormat)this.getComponent("SEND_CAUSE")).getText().length()<=0){
           this.messageBox("����ԭ�򲻿���Ϊ��ֵ");
           return;
       }
       TParm result=null;
       parm.setData("SEQ","001"); //���
       parm.setData("CURRENT_BALANCE", 0.00); //���
       parm.setData("MR_NO", pat.getMrNo()); //������
       // kangy  �ѿ���ԭ        start
    /*   if("ST".equals(buyCardType)){
    	  if(dev_flg){
    	   result = EKTReadCard.getInstance().writeEKT(parm);
    	  } else
       result = EKTIO.getInstance().TXwriteEKT(parm);
       
       if (result.getErrCode() < 0) {
           this.messageBox("�½�ҽ�ƿ�����ʧ��,��鿴�������Ƿ�����");
       } else{
    	   cardEKT(parm, ektCardNo, card);
           //txEKT = true;
       }
       }else if("XN".equals(buyCardType)){
    	   cardEKT(parm, ektCardNo, card);
       }*/
//       onEKTcard();
       result = EKTIO.getInstance().TXwriteEKT(parm);
       if (result.getErrCode() < 0) {
           this.messageBox("�½�ҽ�ƿ�����ʧ��,��鿴�������Ƿ�����");
       } else{
    	   cardEKT(parm, ektCardNo, card);
           //txEKT = true;
       }
       // kangy �ѿ���ԭ      end
   }
   
   
   public void writeEKT(){
	   
	   TParm result=null;
	   TParm parm=new TParm();
	   parm.setData("SEQ","001"); //���
       parm.setData("MR_NO", pat.getMrNo()); //������
       parm.setData("CURRENT_BALANCE",0.00); //���
       // kangy �ѿ���ԭ      start
       /*if(dev_flg){
		   result = EKTReadCard.getInstance().writeEKT(parm);
       } else*/
       // kangy �ѿ���ԭ      end
		   result = EKTIO.getInstance().TXwriteEKT(parm);
	   if (result.getErrCode() < 0) {
           this.messageBox("ҽ�ƿ�д������ʧ��,��鿴�������Ƿ�����");
       }else{
    	   this.messageBox("��Ƭд��ɹ�");
       }
   }
   
   /**
    * ��Ƭ��Ϣд�����ݿ�
    */
   public void cardEKT(TParm parm,String ektCardNo,String card){// add by kangy 

       TParm p = new TParm();
       p.setData("CARD_NO",card); //����
       p.setData("MR_NO", pat.getMrNo()); //������
       p.setData("CARD_SEQ", parm.getValue("SEQ")); //���
       p.setData("ISSUE_DATE", TJDODBTool.getInstance().getDBTime()); //����ʱ��
       p.setData("ISSUERSN_CODE", this.getValue("SEND_CAUSE")); //����ԭ��
       p.setData("FACTORAGE_FEE", this.getValueDouble("PROCEDURE_PRICE")); //������
       p.setData("PASSWORD",  OperatorTool.getInstance().encrypt(this.getValue("CARD_PWD").toString())); //����
       p.setData("WRITE_FLG", "Y"); //д������ע��
       p.setData("OPT_USER", Operator.getID());
       p.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
       p.setData("OPT_TERM", Operator.getIP());
       p.setData("ID_NO", pat.getIdNo()); //���֤����
       p.setData("CASE_NO", "none"); //�����
       p.setData("NAME", pat.getName()); //��������
       p.setData("CURRENT_BALANCE", 0.00); //��Ĭ�Ͻ��
       p.setData("CREAT_USER", Operator.getID()); //������
       /**
        * zhangp 20111216 ���ֶ�
        */
       p.setData("EKT_CARD_NO", ektCardNo); //���ţ�����ӡ�ĺ��룩
       //zhangp 20120113 ע
//       if(ektFlg){
//           p.setData("CHARGE_FLG", "4"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
//       } else {
//           p.setData("CHARGE_FLG", "5"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
//       }
       //zhangp 20120113 �ж��Ƿ����case_no ������� ���� ������ �ƿ�
//			String sql = "SELECT * FROM REG_PATADM WHERE MR_NO = '"+pat.getMrNo()+"'";
//			TParm par = new TParm(TJDODBTool.getInstance().select(sql));
//			if(par.getErrCode()<0){
//				messageBox(par.getErrText());
//			}
//			if(par.getCount()<=0){
//				newFlag = 1;
//			}
       //�ƿ�
       if(newFlag == 1){
    	   p.setData("CHARGE_FLG", getValue("SEND_CAUSE").toString()); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
       }
       //����
       if(newFlag == 2){
    	   p.setData("CHARGE_FLG", getValue("SEND_CAUSE").toString()); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
       }
       //zhangp 20111222 EKT_BIL_PAY
       TParm bilParm = new TParm();
       //===zhangp 20120322 start
       int accntType = 1;
       String sendCause = getValue("SEND_CAUSE").toString();
       if(sendCause.equals("4")){//�ƿ�
    	   accntType = 1;
       }
       if(sendCause.equals("5")){//����
    	   accntType = 3;
       }
       if(sendCause.equals("8")){//����
    	   accntType = 2;
       }
       bilParm.setData("ACCNT_TYPE", accntType);	//��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)(EKT_BIL_PAY)
       //===zhangp 20120322 end
       bilParm.setData("CURT_CARDSEQ", parm.getValue("SEQ"));	//��Ƭ���(EKT_BIL_PAY)
       bilParm.setData("GATHER_TYPE", getValue("GATHER_TYPE"));	//��ֵ��ʽ(EKT_BIL_PAY)
       bilParm.setData("AMT", "0");	//AMT(EKT_BIL_PAY)
       //zhangp 20120109 ���ֶ�
       bilParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
       bilParm.setData("PROCEDURE_AMT", this.getValueDouble("PROCEDURE_PRICE"));	//PROCEDURE_AMT
       p.setData("bilParm", getBilParm(p,bilParm).getData());
       // ��ϸ������
       TParm feeParm = new TParm();
       feeParm.setData("ORIGINAL_BALANCE", 0.00);
       feeParm.setData("BUSINESS_AMT", 0.00);
       feeParm.setData("CURRENT_BALANCE", 0.00);
       p.setData("businessParm", getBusinessParm(p, feeParm).getData());
       TParm result=null;
       result = TIOM_AppServer.executeAction(
               "action.ekt.EKTAction",
               "TXEKTRenewCard", p); //
       if (result.getErrCode() < 0) {
           this.messageBox("�½�ҽ�ƿ�����ʧ��");
           parm = new TParm();
           parm.setData("SEQ", "000"); //���
           parm.setData("CURRENT_BALANCE",0.00); //���
           parm.setData("MR_NO", "000000000000"); //������
           parm = EKTIO.getInstance().TXwriteEKT(parm);
           if (parm.getErrCode() < 0) {
             System.out.println("��дҽ�ƿ�ʧ��");
         }

       } else {
           this.messageBox("�½�ҽ�ƿ������ɹ�");
           onClear();
       }
   }
   /**
    * ��Ƭ��ӡ
    */
   public void onPrint(){}
   /**
    * ҽ��������
    */
   public void onMRcard(){

   }
   /**
    * ҽ�ƿ�����
    */
   public void onEKTcard(){
		//==start====add by kangy 20160912 ===��ȡҽ�ƿ�ˢ����һƱ��
   	bilInvoice = new BilInvoice();
	callFunction("UI|BIL_CODE|setValue", bilInvoice.initBilInvoice("EKT").getUpdateNo());
   	//==end====add by kangy 20160912
       //��ȡҽ�ƿ�
       parmEKT = EKTIO.getInstance().TXreadEKT();
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox(parmEKT.getErrText());
           parmEKT = null;
           return;
       }
       //��Ƭ���
       //this.setValue("CURRENT_BALANCE", parmEKT.getDouble("CURRENT_BALANCE"));
       //��Ƭ����
       this.setValue("CARD_CODE",
                     parmEKT.getValue("MR_NO") + parmEKT.getValue("SEQ"));
       //callFunction("UI|CARD_CODE|setEnabled", false); //���Ų��ɱ༭
       this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
       onQueryNO(false);
     //  txEKT = true;

   }

   /**
    * ǿ�Ƽ�������
    */
   private void patLock() {
       String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
       //�ж��Ƿ����
       if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
           if (this.messageBox("�Ƿ����",
                               PatTool.getInstance().getLockParmString(pat.
                   getMrNo()), 0) == 0) {
               PatTool.getInstance().unLockPat(pat.getMrNo());
               PATLockTool.getInstance().log("ODO->" +
                                             SystemTool.getInstance().getDate() +
                                             " " +
                                             Operator.getID() + " " +
                                             Operator.getName() +
                                             " ǿ�ƽ���[" + aa + " �����ţ�" +
                                             pat.getMrNo() + "]");
           } else {
               pat = null;
               return;
           }
       }

   }
   /**
    * �������֤
    * ============pangben 2013-3-18
    */
   public void onIdCard(){
	   TParm idParm=IdCardO.getInstance().readIdCard();
		this.messageBox(idParm.getValue("MESSAGE"));
		if(idParm.getCount()>0){//����������ʾ
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", idParm);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				this.setValue("MR_NO", patParm.getValue("MR_NO"));
				onQueryNO(true);
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));//��ƴ
				setPatName1();//����Ӣ��
			}

		}else{
			this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
			this.setValue("IDNO", idParm.getValue("IDNO"));
			this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
			this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
			this.setValue("ADDRESS", idParm.getValue("RESID_ADDRESS"));//��ַ
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));//��ƴ
			setPatName1();//����Ӣ��
		}
   }
   /**
    * ��д��
    */
   public void onRenew(){
       //===pangben modify 20110916 ̩��ҽ�ƿ�д������
//       if(this.messageBox("��д��", "�Ƿ�ִ��д������", 0) != 0){
//           return;
//       }
//       if (null != pat && null != pat.getMrNo() && pat.getMrNo().length() > 0 && txEKT) {
//           TParm parm = new TParm();
//           String card = this.getValue("CARD_CODE").toString();
//           if (card.length() != 15) {
//               this.messageBox("�����������,�����ַ�����Ҫ��ʮ��λ");
//               return;
//           }
//           parm.setData("SEQ", card.substring(13, card.length())); //���
//           parm.setData("CURRENT_BALANCE", parmEKT.getValue("CURRENT_BALANCE")); //���
//           parm.setData("MR_NO", card.substring(0, 13)); //������
//
//           parm = EKTIO.getInstance().TXwriteEKT(parm);
//           if (parm.getErrCode() < 0) {
//               this.messageBox("ҽ�ƿ�д��ʧ��");
//           } else
//               this.messageBox("ҽ�ƿ�д�������ɹ�");
//       }else{
//           this.messageBox("��д������ʧ��,û�в�����Ϣ");
//       }
	   
	   /**
	    * zhangp 20121216
	    * ������벡����
	    */
        TParm sendParm = new TParm();
        sendParm.setData("MR_NO", this.getValue("MR_NO"));
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\ekt\\EKTRenewCard_M.x", sendParm);

   }
   /**
    * ͨ���ʱ�ĵõ�ʡ��
    */
   public void onPost() {
       String post = getValueString("POST_CODE");
       TParm parm = SYSPostTool.getInstance().getProvinceCity(post);
       if (parm.getErrCode() != 0 || parm.getCount() == 0) {
           return;
       }
       setValue("STATE",
                parm.getData("POST_CODE", 0).toString().substring(0, 2));
       setValue("CITY", parm.getData("POST_CODE", 0).toString());
       this.grabFocus("ADDRESS");
   }
   /**
       * ͨ�����д�����������
       */
      public void selectCode() {
          this.setValue("POST_CODE", this.getValue("CITY"));
    }
      // kangy �ѿ���ԭ      start
    /*  public void onSaveST(){// add by kangy 20170310
    	  onSave("ST");
    	
      }
      public void onSaveXN(){// add by kangy 20170310
    	  onSave("XN");
      }*/
      // kangy �ѿ���ԭ      end
    /**
     * ���没����Ϣ
     */
   //kangy �ѿ���ԭ        public void onSave(String BuyCardType) {
      public void onSave() {
//        if(!txEKT){
//            this.messageBox("����ҽ�ƿ���Ϣ");
//            return;
//        }
//        if (pat != null)
//            PatTool.getInstance().unLockPat(pat.getMrNo());
        //���������ֵ
        if (getValue("BIRTH_DATE") == null) {
            this.messageBox("�������ڲ���Ϊ��!");
            return;
        }
        if (!this.emptyTextCheck("PAT_NAME,SEX_CODE,CTZ1_CODE"))
            return;
        pat = new Pat();
        //��������
        pat.setName(TypeTool.getString(getValue("PAT_NAME")));
        //Ӣ����
        pat.setName1(TypeTool.getString(getValue("PAT_NAME1")));
        //����ƴ��
        pat.setPy1(TypeTool.getString(getValue("PY1")));
        //���֤��
        pat.setIdNo(TypeTool.getString(getValue("IDNO")));
        //����
        pat.setNationCode(TypeTool.getString(getValue("NATION_CODE")));
        //�����ע��
        pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG")));
        //��������
        pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
        //�Ա�
        pat.setSexCode(TypeTool.getString(getValue("SEX_CODE")));
        //�绰TEL_HOME
        pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
        //�ʱ�
        pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
        //��ַ
        pat.setAddress(TypeTool.getString(getValue("ADDRESS")));
        //���1
        pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
        //���2
        pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
        //���3
        pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
        //ҽ��������
        pat.setNhiNo(TypeTool.getString(""));
        //====zhangp 20120309 modify start
//        if (this.messageBox("������Ϣ", "�Ƿ񱣴�", 0) != 0)
//            return;
        //=====zhangp 20120309 modify end
        TParm patParm = new TParm();
        patParm.setData("MR_NO", getValue("MR_NO"));
        patParm.setData("PAT_NAME", getValue("PAT_NAME"));
        patParm.setData("PAT_NAME1", getValue("PAT_NAME1"));
        patParm.setData("PY1", getValue("PY1"));
        patParm.setData("IDNO", getValue("IDNO"));
        patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
        patParm.setData("TEL_HOME", getValue("TEL_HOME"));
        patParm.setData("SEX_CODE", getValue("SEX_CODE"));
        patParm.setData("POST_CODE", getValue("POST_CODE"));
        patParm.setData("ADDRESS", getValue("ADDRESS"));
        patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
        //yanjing 20131119 У��
        if(getValue("CTZ2_CODE").equals(null)){
        	patParm.setData("CTZ2_CODE", "");
        }else{
        patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
        }
        patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
        if (StringUtil.isNullString(getValue("MR_NO").toString())) {
            patParm.setData("MR_NO", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("PAT_NAME").toString())) {
            patParm.setData("PAT_NAME", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("PAT_NAME1").toString())) {
            patParm.setData("PAT_NAME1", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("PY1").toString())) {
            patParm.setData("PY1", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("IDNO").toString())) {
            patParm.setData("IDNO", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("BIRTH_DATE").toString())) {
            patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
        }
        if (StringUtil.isNullString(getValue("TEL_HOME").toString())) {
            patParm.setData("TEL_HOME", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("SEX_CODE").toString())) {
            patParm.setData("SEX_CODE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("POST_CODE").toString())) {
            patParm.setData("POST_CODE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("ADDRESS").toString())) {
            patParm.setData("ADDRESS", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("CTZ1_CODE").toString())) {
            patParm.setData("CTZ1_CODE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("CTZ2_CODE").toString())) {
            patParm.setData("CTZ2_CODE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValue("CTZ3_CODE").toString())) {
            patParm.setData("CTZ3_CODE", new TNull(String.class));
        }
        patParm.setData("NHI_NO", new TNull(String.class));//ҽ������
        TParm result = new TParm();
        if (getValue("MR_NO").toString().length() != 0) {
            //���²���
            result = PatTool.getInstance().upDateForReg(patParm);
            setValue("MR_NO", getValue("MR_NO"));
            pat.setMrNo(getValue("MR_NO").toString());
        } else {
            //��������
            //pat.setTLoad(StringTool.getBoolean("" + getValue("tLoad")));
            if(!pat.onNew()){
                result.setErr(-1,"�½�������Ϣʧ��");
                ektFlg = true; //ҽ�ƿ������ܿ�
            }else{
                ektFlg=true;
                setValue("MR_NO", pat.getMrNo());
                callFunction("UI|MR_NO|setEnabled", false); //�����ſɱ༭
                setValue("CARD_CODE", pat.getMrNo() + "001");
            }
        }
        //===zhangp 20120309 modify start
//        if (result.getErrCode() != 0) {
//            this.messageBox("E0005");
//        } else {
//            this.messageBox("P0005");
//        }
        //=====zhangp 20120309 modify end
//        if (ektCard != null || ektCard.length() != 0) {
//            EKTIO.getInstance().saveMRNO(this.getValueString("MR_NO"), this);
//        }
       // onClear();
        //zhangp 20111230 �����ƿ�
     //kangy  �ѿ���ԭ      onNew(BuyCardType);
        onNew();
    }

    /**
     *���
     */
    public void onClear() {
        clearValue(" MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; " +
                   " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;ADDRESS; " +
                   " CTZ2_CODE;CTZ3_CODE;CARD_CODE;CARD_PWD; " +
                   " CURRENT_BALANCE;TOP_UP_PRICE;PROCEDURE_PRICE;GATHER_PRICE;NATION_CODE;DESCRIPTION;EKT_CARD_NO;" +
                   "EKTMR_NO;EKTCARD_CODE;BANK_CARD_NO;RE_CARD_PWD");
        //callFunction("UI|FOREIGNER_FLG|setEnabled", true); //����֤���ɱ༭======pangben modify 20110808
        callFunction("UI|MR_NO|setEnabled", true); //�����ſɱ༭
        callFunction("UI|CARD_CODE|setEnabled", true); //ҽ�ƿ��ſ��Ա༭
        setValue("CTZ1_CODE", "99");
        //����Ĭ�Ϸ���ȼ�
       // txEKT = false; //̩��ҽ�ƿ�д���ܿ�
        parmEKT = null; //ҽ�ƿ�����parm
        ektFlg=false;
        EKTTemp=null;
        parmSum=null;//ִ�г�ֵ��������
        bankFlg=false;
        //��������
//        if (pat != null)
//            PatTool.getInstance().unLockPat(pat.getMrNo());
        //=====zhangp 20120224 ֧����ʽ    modify start 
        String id = EKTTool.getInstance().getPayTypeDefault();
        setValue("GATHER_TYPE", id);
        //=======zhangp 20120224 modify end
        setPassWord();
        onInit();
    }

    /**
    * ��������
    * @return boolean
    */
   private boolean txReadEKT(){
       //��ȡҽ�ƿ�����
       if (EKTTemp == null)
           EKTTemp = EKTIO.getInstance().readEkt();
       if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
           this.messageBox("��ҽ�ƿ���Ч");
           return false;
       }
       return true;
   }

    /**
     * �շѷ���
     */
    public void onFEE(){
    	bilInvoice=new BilInvoice();
    	if(null==this.getValue("BIL_CODE")||this.getValue("BIL_CODE").toString().length()<=0){
    		this.messageBox("�޿���Ʊ�ݣ�");
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
//        if(!txEKT){
//            this.messageBox("���ȡҽ�ƿ���Ϣ");
//            return;
//        }
    	//zhangp 20121227 �շѽ���һ��
//    	if(ektFlg==true){
//    		onNew();
//    		ektFlg=false;
//    	}
    	ektFlg = false;
        if (!txReadEKT()) {
            return;
        }
        if (ektFlg || parmEKT == null || parmEKT.getCount() <= 0) {
            this.messageBox("û�л��ҽ�ƿ���Ϣ");
            return;
        }
        //����
//        if (this.getValue("CARD_PWD").toString().length() <= 0) {
//            this.messageBox("����������");
//            return;
//        }

        if (this.getValue("TOP_UP_PRICE").toString().length() <= 0) {
            this.messageBox("������Ҫ��ֵ�Ľ��");
            return;
        }
        if(this.getValueDouble("TOP_UP_PRICE")<=0){
            this.messageBox("��ֵ����ȷ");
            return;
        }
        if (((TTextFormat)this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
            this.messageBox("֧����ʽ������Ϊ��ֵ");
            return;
        }
        //zhangp 20111230 ע
//        if (this.messageBox("�շ�", "�Ƿ�ִ�г�ֵ����", 0) != 0) {
//            return;
//        }

//        //����У��
//        if (!this.getValue("CARD_PWD").toString().trim().equals(parmEKT.
//                getValue("PASSWORD", 0).trim())) {
//            this.messageBox("���벻��,����������");
//            this.setValue("CARD_PWD", "");
//            this.grabFocus("CARD_PWD");
//            return;
//        }
        TParm result =null;
            parmSum = new TParm();
            parmSum.setData("CARD_NO", pat.getMrNo() + parmEKT.getValue("CARD_SEQ",0));
            parmSum.setData("CURRENT_BALANCE", StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2) + StringTool.round(this.getValueDouble("TOP_UP_PRICE"),2));
            parmSum.setData("CASE_NO", "none");
            parmSum.setData("NAME", pat.getName());
            parmSum.setData("MR_NO", pat.getMrNo());
            parmSum.setData("ID_NO",
                      null != pat.getIdNo() && pat.getIdNo().length() > 0 ?
                      pat.getIdNo() : "none");
            parmSum.setData("ISSUERSN_CODE", this.getText("SEND_CAUSE")); //����ԭ��
            parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //֧����ʽ
            parmSum.setData("OPT_USER", Operator.getID());
            parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parmSum.setData("OPT_TERM", Operator.getIP());
            parmSum.setData("FLG", ektFlg);
            parmSum.setData("CHARGE_FLG", "3"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
            parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //֧����ʽ
            parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); //֧����ʽ����
            parmSum.setData("BUSINESS_AMT",
                            StringTool.round(this.getValueDouble("TOP_UP_PRICE"), 2)); //��ֵ���
            parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); //�Ա�
            parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); //��עDESCRIPTION
            parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); //Ʊ�ݺ�
           //  add-----kangy-----EKT_BIL_PAY����ֶ�
            parmSum.setData("PRINT_NO", bilInvoice.initBilInvoice("EKT").getUpdateNo()); //Ʊ�ݺ�
            parmSum.setData("CREAT_USER", Operator.getID()); //ִ����Ա//=====yanjing
            //-----add by kangy  20160804ҽ�ƿ���ֵ
            TParm inFeeParm=new TParm();
            inFeeParm.setData("RECP_TYPE","EKT");
             inFeeParm.setData("INV_NO",bilInvoice.initBilInvoice("EKT").getUpdateNo());
             //inFeeParm.setData("RECEIPT_NO",bil_business_no);
             inFeeParm.setData("CASHIER_CODE",Operator.getID());
             inFeeParm.setData("AR_AMT",this.getValue("TOP_UP_PRICE"));
             inFeeParm.setData("CANCEL_FLG","0");
             inFeeParm.setData("CANCEL_USER","");
             inFeeParm.setData("CANCEL_DATE","");
             inFeeParm.setData("OPT_USER",Operator.getID().toString());
             //infeeParm.setData("OPT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
             inFeeParm.setData("OPT_TERM",Operator.getIP().toString());
             inFeeParm.setData("ACCOUNT_FLG","");
             inFeeParm.setData("ACCOUNT_SEQ","");
             inFeeParm.setData("ACCOUNT_USER","");
             inFeeParm.setData("ACCOUNT_DATE","");
             inFeeParm.setData("PRINT_USER",Operator.getID());
             inFeeParm.setData("PRINT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
             inFeeParm.setData("ADM_TYPE","T");
             inFeeParm.setData("STATUS","0");
             parmSum.setData("infeeparm",inFeeParm.getData());
             
            // BILInvoiceTool.getInstance().insertFeeDate(inFeeParm);
             //Ʊ���Լ�һ
             String updateno = StringTool.addString(bilInvoice.initBilInvoice("EKT").getUpdateNo());
             TParm updatenoParm=new TParm();
             BilInvoice bilInvo=bilInvoice.initBilInvoice("EKT");
             updatenoParm.setData("UPDATE_NO",updateno);
             updatenoParm.setData("RECP_TYPE","EKT");
             updatenoParm.setData("STATUS",bilInvo.getStatus());
             updatenoParm.setData("CASHIER_CODE",bilInvo.getCashierCode());
             updatenoParm.setData("START_INVNO",bilInvo.getStartInvno());
             //BILInvoiceTool.getInstance().updateDatePrint(updatenoParm);
            parmSum.setData("updatanoparm",updatenoParm.getData());
            
            //��ϸ�����
            TParm feeParm=new TParm();
            feeParm.setData("ORIGINAL_BALANCE",StringTool.round(EKTTemp.getDouble("CURRENT_BALANCE"),2));
            feeParm.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("TOP_UP_PRICE"), 2));
            feeParm.setData("CURRENT_BALANCE",StringTool.round(EKTTemp.getDouble("CURRENT_BALANCE"),2)+StringTool.round(this.getValueDouble("TOP_UP_PRICE"),2));
            parmSum.setData("businessParm",getBusinessParm(parmSum,feeParm).getData());
            //zhangp 20120109 EKT_BIL_PAY ���ֶ�
            parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
            parmSum.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
            //bil_pay ��ֵ������
            parmSum.setData("billParm",getBillParm(parmSum,feeParm).getData());
            //�������
            result = TIOM_AppServer.executeAction(
                    "action.ekt.EKTAction",
                    "TXEKTonFee", parmSum); //
            if (result.getErrCode() < 0) {
               this.messageBox("ҽ�ƿ���ֵʧ��");
           } else{
               this.messageBox("��ֵ�ɹ�");
        
        String bil_business_no = result.getValue("BIL_BUSINESS_NO"); //�վݺ�
        onPrint(bil_business_no);//��ӡƱ��
        // onClear();
               onInit();
           }
    }
    /**
    * ��ֵ��������ݲ���
    * @param parm TParm
    * @return TParm
    */
   private TParm getBillParm(TParm parm,TParm feeParm){
       TParm billParm=new TParm();
       billParm.setData("CARD_NO", parm.getValue("CARD_NO")); //����
       billParm.setData("CURT_CARDSEQ", 0); //���
       billParm.setData("ACCNT_TYPE", "4"); //��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
       billParm.setData("MR_NO", parm.getValue("MR_NO"));//������
       billParm.setData("ID_NO", parm.getValue("ID_NO"));//���֤��
       billParm.setData("NAME", parm.getValue("NAME"));//��������
       billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT"));//��ֵ���
       billParm.setData("CREAT_USER", Operator.getID());//ִ����Ա
       billParm.setData("OPT_USER", Operator.getID());//������Ա
       billParm.setData("OPT_TERM", Operator.getIP());//ִ��ip
       billParm.setData("GATHER_TYPE",parm.getValue("GATHER_TYPE"));//֧����ʽ
	   //zhangp 20120109
       billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
       billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
       return billParm;
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
        bilParm.setData("CHARGE_FLG", p.getValue("CHARGE_FLG")); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
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
     * ��ⲡ����ͬ����
     */
    public void onPatName() {
//        String patName = this.getValueString("PAT_NAME"); 
//        if (StringUtil.isNullString(patName)) {
//            return;
//        }
//        //REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
//        String selPat =
//                " SELECT OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE," +
//                "        POST_CODE,ADDRESS,MR_NO " +
//                "   FROM SYS_PATINFO " +
//                "  WHERE PAT_NAME = '" + patName + "' " +
//                "  ORDER BY OPT_DATE ";
//        TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
//        if (same.getErrCode() != 0) {
//            this.messageBox_(same.getErrText());
//        }
//        setPatName1();
        //ѡ�񲡻���Ϣ
//        if (same.getCount("MR_NO") > 0) {
//            int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
//            if (sameCount != 1) {
//                this.grabFocus("PY1");
//                return;
//            }
//            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
//                                    same);
//            TParm patParm = new TParm();
//            if (obj != null) {
//                patParm = (TParm) obj;
//                this.setValue("MR_NO",patParm.getValue("MR_NO"));
//                onQueryNO();
//                return;
//            }
//        }
        this.grabFocus("PY1");
        this.onQueryPat();
    }

    /**
     * ����Ӣ����
     */
    public void setPatName1() {
        String patName1 = SYSHzpyTool.getInstance().charToAllPy(TypeTool.
                getString(getValue("PAT_NAME")));
        setValue("PAT_NAME1", patName1);
    }
   /**
    * ��ֵ���س��¼�
    */
   public void topUpFee(){
       //����ֵ�����ʾ��Ӧ�ս����
       double price=this.getValueDouble("TOP_UP_PRICE")+this.getValueDouble("PROCEDURE_PRICE");
       this.setValue("GATHER_PRICE",price);
       //zhangp 20111223
       this.grabFocus("GATHER_PRICE");
   }
   /**
    * ҽ�ƿ��޸�����
    */
   public void updateEKTPwd(){
       TParm sendParm = new TParm();
           TParm reParm = (TParm)this.openDialog(
               "%ROOT%\\config\\ekt\\EKTUpdatePassWord.x", sendParm);
   }

   /**
    * ��ֵ��ӡ
    */
   private void onPrint(String bil_business_no) {
       TParm parm = new TParm();
       parm.setData("TITLE", "TEXT",
                    (Operator.getRegion() != null &&
                     Operator.getRegion().length() > 0 ?
                     Operator.getHospitalCHNFullName() : "����ҽԺ") );
       parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //������
       parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //����
     //====zhangp 20120525 start
//     parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�տʽ
     parm.setData("GATHER_NAME", "TEXT", ""); //�տʽ
     //====zhangp 20120525 end
       parm.setData("TYPE", "TEXT", "Ԥ ��"); //�ı�Ԥ�ս��
       parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //�տʽ
       parm.setData("AMT", "TEXT",
                    StringTool.round(parmSum.getDouble("BUSINESS_AMT"), 2)); //���
       parm.setData("SEX_TYPE", "TEXT",
                    parmSum.getValue("SEX_TYPE").equals("1") ? "��" : "Ů"); //�Ա�
       parm.setData("AMT_AW", "TEXT",
                    StringUtil.getInstance().numberToWord(
                    parmSum.getDouble("BUSINESS_AMT"))); //��д���
       parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); //̨ͷһ
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
       if (null == bil_business_no)
           bil_business_no = EKTTool.getInstance().getBillBusinessNo(); //��ӡ����
       parm.setData("ONFEE_NO", "TEXT", bil_business_no); //�վݺ�
       
       
       
       
       parm.setData("PRINT_NO", "TEXT", parmSum.getValue("PRINT_NO")); //Ʊ��
       parm.setData("PRINT_DATE", "TEXT", yMd); //��ӡʱ��
       parm.setData("DATE", "TEXT", yMd + "    " + hms); //����
       parm.setData("USER_NAME", "TEXT", Operator.getID()); //�տ���
       //=========modify by lim 2012/02/24 begin
       //===zhangp 20120525 start
       parm.setData("O", "TEXT", ""); 

//       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw", parm,true);
       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
       //===zhangp 20120525 end
     //=========modify by lim 2012/02/24 begin
   }
   /**
    * ���п���������
    */
   public void onBankCard(){
       //��ȡ���п���Ϣ
       //��������
       this.setValue("BANK_CARD_NO","111111111111111");

   }
   /**
    * ���п�ִ�в���
    */
   public void onBankSave(){
       if(bankFlg){
           if(this.getValue("BANK_CARD_NO").toString().length()<=0){
               this.messageBox("�ȶ�ȡ���п���Ϣ");
               return;
           }
           TParm parm = new TParm();
           parm.setData("CARD_NO", this.getValue("CARD_CODE"));
           parm.setData("BANK_CARD_NO", this.getValue("BANK_CARD_NO"));
           TParm result=EKTIO.getInstance().updateEKTAndBank(parm);
           if(result.getErrCode()<0){
               this.messageBox("���п�����ʧ��");
           }else{
               this.messageBox("���п������ɹ�");
           }
       }
       else{
           this.messageBox("ֻ�д���ҽ�ƿ���Ϣ�ſ���ִ�����п�����");
       }


   }
   
   /**
    * ��ѯ����
    * ===========zhangp 20111216
    */
   public void onQueryPat(){
	        TParm sendParm = new TParm();
	        sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
	        TParm reParm = (TParm)this.openDialog(
	            "%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
	        if(reParm==null)
	            return;
	        this.setValue("MR_NO", reParm.getValue("MR_NO"));
	        this.onQueryNO();
	        //zhangp 20111223
	        this.grabFocus("PY1");
   }
   /**
    * ҽ�ƿ���ֵ�˿��������
    * zhangp 20111222
    */
   public TParm getBilParm(TParm parm,TParm bparm){
	   TParm bilParm = new TParm();
	   bilParm.setData("CARD_NO", parm.getData("CARD_NO"));
	   bilParm.setData("CURT_CARDSEQ", bparm.getData("CURT_CARDSEQ"));
	   bilParm.setData("ACCNT_TYPE", bparm.getData("ACCNT_TYPE"));
	   bilParm.setData("MR_NO", parm.getData("MR_NO"));
	   bilParm.setData("ID_NO", parm.getData("ID_NO"));
	   bilParm.setData("NAME", parm.getData("NAME"));
	   bilParm.setData("CREAT_USER", parm.getData("CREAT_USER"));
	   bilParm.setData("OPT_USER", parm.getData("OPT_USER"));
	   bilParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
	   bilParm.setData("GATHER_TYPE", bparm.getData("GATHER_TYPE"));
	   bilParm.setData("AMT", bparm.getData("AMT"));
	   //zhangp 20120109
	   bilParm.setData("STORE_DATE", bparm.getData("STORE_DATE"));
	   bilParm.setData("PROCEDURE_AMT", bparm.getData("PROCEDURE_AMT"));
	   return bilParm;
   }
   /**
    * ����ԭ�����
    * ====zhangp 20120202
    */
   public void onSendCause(){
	   TParm result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
	      if(result.getCount()>0){
	    	  setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
	      }
   }
   /**
    * ����ȷ��
    * ===zhangp 20120315
    */
   public void onPassWord(){
	   if(!passWord4()){
		   return;
	   }
	   if(getValueString("RE_CARD_PWD").length()==0){
		   
	   }else{
		   if(!passWord()){
			   return;
		   }
		   grabFocus("SAVEBUTTON");
	   }
   }
   /**
    * ����ȷ��
    * ===zhangp 20120315
    * @return
    */
   public boolean passWord(){
	   String passWord = getValueString("CARD_PWD");
	   String repassWord = getValueString("RE_CARD_PWD");
	   if(!passWord.equals(repassWord)){
		   messageBox("���벻һ��");
		   return false;
	   }
	   return true;
   }
   /**
    * 4λ����
    * ===zhangp 20120319
    * @return
    */
   public boolean passWord4(){
	   String passWord = getValueString("CARD_PWD");
	   if(passWord.length()!=4){
		   messageBox("����Ϊ4λ");
		   return false;
	   }
	   grabFocus("RE_CARD_PWD");
	   return true;
   }
   /**
    * ��ʼ���� 
    */
   private void setPassWord(){
	   this.setValue("CARD_PWD", "0000");
	   this.setValue("RE_CARD_PWD", "0000");
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

