package com.javahis.ui.ekt;


import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

import jdo.bil.BILInvoiceTool;
import jdo.bil.BilInvoice;
import jdo.bil.InvoiceTool;
import jdo.ekt.EKTIO;
//脱卡还原     import jdo.ekt.EKTReadCard;
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
 * <p>Title: 医疗卡购卡</p>
 *
 * <p>Description: 医疗卡购卡</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110928
 * @version 1.0
 */
public class EKTWorkControl extends TControl{
	// 传入数据
	Object paraObject;
	String onwType="";
	BilInvoice bilInvoice;
	   private Pat  pat;//  病患信息
	   //private boolean txEKT=false;//医疗卡读取写卡操作
	   private TParm parmEKT;//医疗卡集合
	   private boolean ektFlg=false;//医疗卡创建:true:第一次创建
	   private TParm EKTTemp;//医疗卡最初值
	   private TParm parmSum;//执行退款操作参数
	   private boolean bankFlg=false;//银行卡关联设置
	   /**
	    * zhangp 20121216 传入病案号
	    */
	   private TParm acceptData = new TParm(); //接参
	   String systemCode = "";
	   //20120113 zhangp 新建卡判断 1新 2补
	   private int newFlag = 2;
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	   // private boolean dev_flg= false;   kangy 脱卡还原   
    public EKTWorkControl() {
    }
    /**
    * 初始化方法
    */
   public void onInit() {
	   callFunction("UI|BIL_CODE|Enable", false);
      setValue("CTZ1_CODE", "99");
      /**
       * zhangp 20121216 国籍默认中国
       */
      setValue("NATION_CODE", "86");
      /**
       * zhangp 20121216 传入病案号
       */
      Object obj = this.getParameter();
      if (obj instanceof TParm) {
          acceptData = (TParm) obj;
          String mrNo = acceptData.getData("MR_NO").toString();
          //zhangp 20120113
          this.setValue("MR_NO", mrNo);
          this.onQueryNO();
      }
      //zhangp 20111227 下拉框默认值
            TParm result = EKTTool.getInstance().sendCause();
      if(result.getCount()>0){
    	  setValue("SEND_CAUSE", result.getData("ID", 0));
      }
      //zhangp 20120201 手续费
      result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
      if(result.getCount()>0){
    	  setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
      }
      //=====zhangp 20120224 支付方式    modify start 
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
				onwType = paraParm.getValue("ONW_TYPE");//=====pangben 2013-5-15 门急诊护士站解锁使用，监听不同的界面
			}
			
		}
		// 初始化票据
		 bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("EKT"));
		
		 callFunction("UI|TOP_UP_PRICE|setValue", "0");
		 //kangy 脱卡还原   dev_flg=EKTTool.getInstance().Equipment(Operator.getIP());// add by kangy 20170622 判断新旧设备   旧设备==false
			
   }
	   
   /**
    * 查询方法
    */
   public void onQuery(){
       onQueryNO(true);
   }

   /**
    * 病案号文本框事件
    */
   public void onQueryNO() {
       onQueryNO(true);
   }

   /**
    * 查询方法
    */
   public void onQueryNO(boolean flg) {
//       if (pat != null)
//           PatTool.getInstance().unLockPat(pat.getMrNo());
       pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
       if (pat == null) {
           this.messageBox("无此病案号!");
           this.grabFocus("PAT_NAME");
           if(!flg){
               this.setValue("MR_NO", "");
               callFunction("UI|MR_NO|setEnabled", false); //病案号可编辑
           }
           return;
       }
    // modify by huangtt 20160930 EMPI患者查重提示 start
       String mrNo =  PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
       if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
		}
    // modify by huangtt 20160930 EMPI患者查重提示 end  
       setValue("MR_NO",mrNo);
        setValue("PAT_NAME", pat.getName());
        setValue("PAT_NAME1", pat.getName1());
        setValue("PY1", pat.getPy1());
        setValue("IDNO", pat.getIdNo());
        setValue("NATION_CODE",pat.getNationCode());
      //zhangp 20111223 国籍
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
        //锁病患信息
//            this.messageBox_("加锁成功!");//测试专用
        this.grabFocus("onFee");
        callFunction("UI|MR_NO|setEnabled", false); //病案号不可编辑
        TParm parm=new TParm();
       parm.setData("MR_NO",pat.getMrNo());
       parmEKT= EKTTool.getInstance().selectEKTIssuelog(parm);
       if (parmEKT.getCount() <= 0) {
//           this.messageBox("此病患没有医疗卡信息");
           //新卡创建
           this.setValue("CARD_CODE", pat.getMrNo() + "001");
           //不存在医疗卡，写卡时操作创建EKT_MASTER表信息
           ektFlg = true;
       } else {
//           this.messageBox("此病患已经存在医疗卡信息");//=====zhangp 20120225 modify start
           this.setValue("CARD_CODE", parmEKT.getValue("CARD_NO", 0));
           this.setValue("EKT_CARD_NO", parmEKT.getValue("EKT_CARD_NO", 0));
           //zhangp 20111230
           setValue("EKTMR_NO", getValue("MR_NO"));
           setValue("EKTCARD_CODE", getValue("EKT_CARD_NO"));
           this.setValue("CURRENT_BALANCE",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2) );
           bankFlg=true;//银行卡执行关联
       }
   }
   /**
    * 无身份注记事件
    */
   public void onSelForeieignerFlg() {
       if (this.getValue("FOREIGNER_FLG").equals("Y"))
           this.grabFocus("BIRTH_DATE");
       if (this.getValue("FOREIGNER_FLG").equals("N"))
           this.grabFocus("IDNO");
   }

   /**
    * 新建
    */
   //kangy 脱卡还原        public void onNew(String buyCardType){
   public void onNew(){
//       if (!txReadEKT()) {
//           return;
//       }
       TParm parm = new TParm();
       String card= this.getValue("CARD_CODE").toString();
       /**
        * zhangp 20111216 加字段
        */
       String ektCardNo= this.getValue("EKT_CARD_NO").toString();
       if (pat == null || pat.getMrNo().length()<=0) {
           this.messageBox("请先查询病患信息");
           return;
       }
       //ektFlg=false 代表不是新建医疗卡信息不可以执行
       if(!ektFlg){
           this.messageBox("此病患存在医疗卡信息,请执行挂失/补卡操作");
           return ;
       }

//       if(card.length()!=15){
//           this.messageBox("卡号输入错误,卡号字符必须要求十五位");
//           return ;
//       }
       if (this.messageBox("新建卡", "是否执行制卡操作", 0) != 0) {
           return;
       }
       //密码
      if (this.getValue("CARD_PWD").toString().length() <= 0) {
          this.messageBox("请输入密码");
          return;
      }
      //===zhangp 20120315 start
      if(!passWord()){
    	  return;
      }
      //===zhangp 20120315 end
       if(((TTextFormat)this.getComponent("SEND_CAUSE")).getText().length()<=0){
           this.messageBox("发卡原因不可以为空值");
           return;
       }
       TParm result=null;
       parm.setData("SEQ","001"); //编号
       parm.setData("CURRENT_BALANCE", 0.00); //金额
       parm.setData("MR_NO", pat.getMrNo()); //病案号
       // kangy  脱卡还原        start
    /*   if("ST".equals(buyCardType)){
    	  if(dev_flg){
    	   result = EKTReadCard.getInstance().writeEKT(parm);
    	  } else
       result = EKTIO.getInstance().TXwriteEKT(parm);
       
       if (result.getErrCode() < 0) {
           this.messageBox("新建医疗卡操作失败,请查看读卡机是否设置");
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
           this.messageBox("新建医疗卡操作失败,请查看读卡机是否设置");
       } else{
    	   cardEKT(parm, ektCardNo, card);
           //txEKT = true;
       }
       // kangy 脱卡还原      end
   }
   
   
   public void writeEKT(){
	   
	   TParm result=null;
	   TParm parm=new TParm();
	   parm.setData("SEQ","001"); //编号
       parm.setData("MR_NO", pat.getMrNo()); //病案号
       parm.setData("CURRENT_BALANCE",0.00); //金额
       // kangy 脱卡还原      start
       /*if(dev_flg){
		   result = EKTReadCard.getInstance().writeEKT(parm);
       } else*/
       // kangy 脱卡还原      end
		   result = EKTIO.getInstance().TXwriteEKT(parm);
	   if (result.getErrCode() < 0) {
           this.messageBox("医疗卡写卡操作失败,请查看读卡机是否设置");
       }else{
    	   this.messageBox("卡片写入成功");
       }
   }
   
   /**
    * 卡片信息写入数据库
    */
   public void cardEKT(TParm parm,String ektCardNo,String card){// add by kangy 

       TParm p = new TParm();
       p.setData("CARD_NO",card); //卡号
       p.setData("MR_NO", pat.getMrNo()); //病案号
       p.setData("CARD_SEQ", parm.getValue("SEQ")); //序号
       p.setData("ISSUE_DATE", TJDODBTool.getInstance().getDBTime()); //发卡时间
       p.setData("ISSUERSN_CODE", this.getValue("SEND_CAUSE")); //发卡原因
       p.setData("FACTORAGE_FEE", this.getValueDouble("PROCEDURE_PRICE")); //手续费
       p.setData("PASSWORD",  OperatorTool.getInstance().encrypt(this.getValue("CARD_PWD").toString())); //密码
       p.setData("WRITE_FLG", "Y"); //写卡操作注记
       p.setData("OPT_USER", Operator.getID());
       p.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
       p.setData("OPT_TERM", Operator.getIP());
       p.setData("ID_NO", pat.getIdNo()); //身份证号码
       p.setData("CASE_NO", "none"); //就诊号
       p.setData("NAME", pat.getName()); //病患名称
       p.setData("CURRENT_BALANCE", 0.00); //金额，默认金额
       p.setData("CREAT_USER", Operator.getID()); //操作人
       /**
        * zhangp 20111216 加字段
        */
       p.setData("EKT_CARD_NO", ektCardNo); //卡号（卡上印的号码）
       //zhangp 20120113 注
//       if(ektFlg){
//           p.setData("CHARGE_FLG", "4"); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
//       } else {
//           p.setData("CHARGE_FLG", "5"); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
//       }
       //zhangp 20120113 判断是否存在case_no 如果存在 补卡 不存在 制卡
//			String sql = "SELECT * FROM REG_PATADM WHERE MR_NO = '"+pat.getMrNo()+"'";
//			TParm par = new TParm(TJDODBTool.getInstance().select(sql));
//			if(par.getErrCode()<0){
//				messageBox(par.getErrText());
//			}
//			if(par.getCount()<=0){
//				newFlag = 1;
//			}
       //制卡
       if(newFlag == 1){
    	   p.setData("CHARGE_FLG", getValue("SEND_CAUSE").toString()); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
       }
       //补卡
       if(newFlag == 2){
    	   p.setData("CHARGE_FLG", getValue("SEND_CAUSE").toString()); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
       }
       //zhangp 20111222 EKT_BIL_PAY
       TParm bilParm = new TParm();
       //===zhangp 20120322 start
       int accntType = 1;
       String sendCause = getValue("SEND_CAUSE").toString();
       if(sendCause.equals("4")){//制卡
    	   accntType = 1;
       }
       if(sendCause.equals("5")){//补卡
    	   accntType = 3;
       }
       if(sendCause.equals("8")){//换卡
    	   accntType = 2;
       }
       bilParm.setData("ACCNT_TYPE", accntType);	//明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)(EKT_BIL_PAY)
       //===zhangp 20120322 end
       bilParm.setData("CURT_CARDSEQ", parm.getValue("SEQ"));	//卡片序号(EKT_BIL_PAY)
       bilParm.setData("GATHER_TYPE", getValue("GATHER_TYPE"));	//充值方式(EKT_BIL_PAY)
       bilParm.setData("AMT", "0");	//AMT(EKT_BIL_PAY)
       //zhangp 20120109 加字段
       bilParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//售卡操作时间
       bilParm.setData("PROCEDURE_AMT", this.getValueDouble("PROCEDURE_PRICE"));	//PROCEDURE_AMT
       p.setData("bilParm", getBilParm(p,bilParm).getData());
       // 明细档数据
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
           this.messageBox("新建医疗卡操作失败");
           parm = new TParm();
           parm.setData("SEQ", "000"); //编号
           parm.setData("CURRENT_BALANCE",0.00); //金额
           parm.setData("MR_NO", "000000000000"); //病案号
           parm = EKTIO.getInstance().TXwriteEKT(parm);
           if (parm.getErrCode() < 0) {
             System.out.println("回写医疗卡失败");
         }

       } else {
           this.messageBox("新建医疗卡操作成功");
           onClear();
       }
   }
   /**
    * 卡片打印
    */
   public void onPrint(){}
   /**
    * 医保卡操作
    */
   public void onMRcard(){

   }
   /**
    * 医疗卡操作
    */
   public void onEKTcard(){
		//==start====add by kangy 20160912 ===读取医疗卡刷新下一票号
   	bilInvoice = new BilInvoice();
	callFunction("UI|BIL_CODE|setValue", bilInvoice.initBilInvoice("EKT").getUpdateNo());
   	//==end====add by kangy 20160912
       //读取医疗卡
       parmEKT = EKTIO.getInstance().TXreadEKT();
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox(parmEKT.getErrText());
           parmEKT = null;
           return;
       }
       //卡片余额
       //this.setValue("CURRENT_BALANCE", parmEKT.getDouble("CURRENT_BALANCE"));
       //卡片号码
       this.setValue("CARD_CODE",
                     parmEKT.getValue("MR_NO") + parmEKT.getValue("SEQ"));
       //callFunction("UI|CARD_CODE|setEnabled", false); //卡号不可编辑
       this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
       onQueryNO(false);
     //  txEKT = true;

   }

   /**
    * 强制加锁方法
    */
   private void patLock() {
       String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
       //判断是否加锁
       if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
           if (this.messageBox("是否解锁",
                               PatTool.getInstance().getLockParmString(pat.
                   getMrNo()), 0) == 0) {
               PatTool.getInstance().unLockPat(pat.getMrNo());
               PATLockTool.getInstance().log("ODO->" +
                                             SystemTool.getInstance().getDate() +
                                             " " +
                                             Operator.getID() + " " +
                                             Operator.getName() +
                                             " 强制解锁[" + aa + " 病案号：" +
                                             pat.getMrNo() + "]");
           } else {
               pat = null;
               return;
           }
       }

   }
   /**
    * 二代身份证
    * ============pangben 2013-3-18
    */
   public void onIdCard(){
	   TParm idParm=IdCardO.getInstance().readIdCard();
		this.messageBox(idParm.getValue("MESSAGE"));
		if(idParm.getCount()>0){//多行数据显示
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", idParm);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				this.setValue("MR_NO", patParm.getValue("MR_NO"));
				onQueryNO(true);
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));//简拼
				setPatName1();//设置英文
			}

		}else{
			this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
			this.setValue("IDNO", idParm.getValue("IDNO"));
			this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
			this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
			this.setValue("ADDRESS", idParm.getValue("RESID_ADDRESS"));//地址
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));//简拼
			setPatName1();//设置英文
		}
   }
   /**
    * 补写卡
    */
   public void onRenew(){
       //===pangben modify 20110916 泰心医疗卡写卡操作
//       if(this.messageBox("补写卡", "是否执行写卡操作", 0) != 0){
//           return;
//       }
//       if (null != pat && null != pat.getMrNo() && pat.getMrNo().length() > 0 && txEKT) {
//           TParm parm = new TParm();
//           String card = this.getValue("CARD_CODE").toString();
//           if (card.length() != 15) {
//               this.messageBox("卡号输入错误,卡号字符必须要求十五位");
//               return;
//           }
//           parm.setData("SEQ", card.substring(13, card.length())); //编号
//           parm.setData("CURRENT_BALANCE", parmEKT.getValue("CURRENT_BALANCE")); //金额
//           parm.setData("MR_NO", card.substring(0, 13)); //病案号
//
//           parm = EKTIO.getInstance().TXwriteEKT(parm);
//           if (parm.getErrCode() < 0) {
//               this.messageBox("医疗卡写卡失败");
//           } else
//               this.messageBox("医疗卡写卡操作成功");
//       }else{
//           this.messageBox("补写卡操作失败,没有病患信息");
//       }
	   
	   /**
	    * zhangp 20121216
	    * 点击带入病案号
	    */
        TParm sendParm = new TParm();
        sendParm.setData("MR_NO", this.getValue("MR_NO"));
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\ekt\\EKTRenewCard_M.x", sendParm);

   }
   /**
    * 通信邮编的得到省市
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
       * 通过城市带出邮政编码
       */
      public void selectCode() {
          this.setValue("POST_CODE", this.getValue("CITY"));
    }
      // kangy 脱卡还原      start
    /*  public void onSaveST(){// add by kangy 20170310
    	  onSave("ST");
    	
      }
      public void onSaveXN(){// add by kangy 20170310
    	  onSave("XN");
      }*/
      // kangy 脱卡还原      end
    /**
     * 保存病患信息
     */
   //kangy 脱卡还原        public void onSave(String BuyCardType) {
      public void onSave() {
//        if(!txEKT){
//            this.messageBox("请获得医疗卡信息");
//            return;
//        }
//        if (pat != null)
//            PatTool.getInstance().unLockPat(pat.getMrNo());
        //不能输入空值
        if (getValue("BIRTH_DATE") == null) {
            this.messageBox("出生日期不能为空!");
            return;
        }
        if (!this.emptyTextCheck("PAT_NAME,SEX_CODE,CTZ1_CODE"))
            return;
        pat = new Pat();
        //病患姓名
        pat.setName(TypeTool.getString(getValue("PAT_NAME")));
        //英文名
        pat.setName1(TypeTool.getString(getValue("PAT_NAME1")));
        //姓名拼音
        pat.setPy1(TypeTool.getString(getValue("PY1")));
        //身份证号
        pat.setIdNo(TypeTool.getString(getValue("IDNO")));
        //国籍
        pat.setNationCode(TypeTool.getString(getValue("NATION_CODE")));
        //外国人注记
        pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG")));
        //出生日期
        pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
        //性别
        pat.setSexCode(TypeTool.getString(getValue("SEX_CODE")));
        //电话TEL_HOME
        pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
        //邮编
        pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
        //地址
        pat.setAddress(TypeTool.getString(getValue("ADDRESS")));
        //身份1
        pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
        //身份2
        pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
        //身份3
        pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
        //医保卡市民卡
        pat.setNhiNo(TypeTool.getString(""));
        //====zhangp 20120309 modify start
//        if (this.messageBox("病患信息", "是否保存", 0) != 0)
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
        //yanjing 20131119 校验
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
        patParm.setData("NHI_NO", new TNull(String.class));//医保卡号
        TParm result = new TParm();
        if (getValue("MR_NO").toString().length() != 0) {
            //更新病患
            result = PatTool.getInstance().upDateForReg(patParm);
            setValue("MR_NO", getValue("MR_NO"));
            pat.setMrNo(getValue("MR_NO").toString());
        } else {
            //新增病患
            //pat.setTLoad(StringTool.getBoolean("" + getValue("tLoad")));
            if(!pat.onNew()){
                result.setErr(-1,"新建病患信息失败");
                ektFlg = true; //医疗卡创建管控
            }else{
                ektFlg=true;
                setValue("MR_NO", pat.getMrNo());
                callFunction("UI|MR_NO|setEnabled", false); //病案号可编辑
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
        //zhangp 20111230 保存制卡
     //kangy  脱卡还原      onNew(BuyCardType);
        onNew();
    }

    /**
     *清空
     */
    public void onClear() {
        clearValue(" MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; " +
                   " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;ADDRESS; " +
                   " CTZ2_CODE;CTZ3_CODE;CARD_CODE;CARD_PWD; " +
                   " CURRENT_BALANCE;TOP_UP_PRICE;PROCEDURE_PRICE;GATHER_PRICE;NATION_CODE;DESCRIPTION;EKT_CARD_NO;" +
                   "EKTMR_NO;EKTCARD_CODE;BANK_CARD_NO;RE_CARD_PWD");
        //callFunction("UI|FOREIGNER_FLG|setEnabled", true); //其他证件可编辑======pangben modify 20110808
        callFunction("UI|MR_NO|setEnabled", true); //病案号可编辑
        callFunction("UI|CARD_CODE|setEnabled", true); //医疗卡号可以编辑
        setValue("CTZ1_CODE", "99");
        //设置默认服务等级
       // txEKT = false; //泰心医疗卡写卡管控
        parmEKT = null; //医疗卡读卡parm
        ektFlg=false;
        EKTTemp=null;
        parmSum=null;//执行充值操作参数
        bankFlg=false;
        //解锁病患
//        if (pat != null)
//            PatTool.getInstance().unLockPat(pat.getMrNo());
        //=====zhangp 20120224 支付方式    modify start 
        String id = EKTTool.getInstance().getPayTypeDefault();
        setValue("GATHER_TYPE", id);
        //=======zhangp 20120224 modify end
        setPassWord();
        onInit();
    }

    /**
    * 读卡操作
    * @return boolean
    */
   private boolean txReadEKT(){
       //读取医疗卡操作
       if (EKTTemp == null)
           EKTTemp = EKTIO.getInstance().readEkt();
       if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
           this.messageBox("此医疗卡无效");
           return false;
       }
       return true;
   }

    /**
     * 收费方法
     */
    public void onFEE(){
    	bilInvoice=new BilInvoice();
    	if(null==this.getValue("BIL_CODE")||this.getValue("BIL_CODE").toString().length()<=0){
    		this.messageBox("无可用票据！");
    		return;
    	}
    	TParm checkparm=new TParm();
    
    	checkparm.setData("RECP_TYPE","EKT");
    	checkparm.setData("INV_NO",this.getValue("BIL_CODE"));
       	checkparm.setData("CASHIER_CODE",Operator.getID());
    	TParm res=BILInvoiceTool.getInstance().checkUpdateNo(checkparm);
    	if(res.getCount("RECP_TYPE")>0){
    		this.messageBox("该票据已使用！");
    		onClear();
    		return;
    	}
    	if(!compareInvno(bilInvoice.initBilInvoice("EKT").getStartInvno(),bilInvoice.initBilInvoice("EKT").getEndInvno(),this.getValue("BIL_CODE").toString())){
			this.messageBox("票号超出范围");
			onClear();
			return;
		}
//        if(!txEKT){
//            this.messageBox("请读取医疗卡信息");
//            return;
//        }
    	//zhangp 20121227 收费建卡一体
//    	if(ektFlg==true){
//    		onNew();
//    		ektFlg=false;
//    	}
    	ektFlg = false;
        if (!txReadEKT()) {
            return;
        }
        if (ektFlg || parmEKT == null || parmEKT.getCount() <= 0) {
            this.messageBox("没有获得医疗卡信息");
            return;
        }
        //密码
//        if (this.getValue("CARD_PWD").toString().length() <= 0) {
//            this.messageBox("请输入密码");
//            return;
//        }

        if (this.getValue("TOP_UP_PRICE").toString().length() <= 0) {
            this.messageBox("请输入要充值的金额");
            return;
        }
        if(this.getValueDouble("TOP_UP_PRICE")<=0){
            this.messageBox("充值金额不正确");
            return;
        }
        if (((TTextFormat)this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
            this.messageBox("支付方式不可以为空值");
            return;
        }
        //zhangp 20111230 注
//        if (this.messageBox("收费", "是否执行充值操作", 0) != 0) {
//            return;
//        }

//        //密码校验
//        if (!this.getValue("CARD_PWD").toString().trim().equals(parmEKT.
//                getValue("PASSWORD", 0).trim())) {
//            this.messageBox("密码不符,请重新输入");
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
            parmSum.setData("ISSUERSN_CODE", this.getText("SEND_CAUSE")); //发卡原因
            parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //支付方式
            parmSum.setData("OPT_USER", Operator.getID());
            parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parmSum.setData("OPT_TERM", Operator.getIP());
            parmSum.setData("FLG", ektFlg);
            parmSum.setData("CHARGE_FLG", "3"); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
            parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //支付方式
            parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); //支付方式名称
            parmSum.setData("BUSINESS_AMT",
                            StringTool.round(this.getValueDouble("TOP_UP_PRICE"), 2)); //充值金额
            parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); //性别
            parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); //备注DESCRIPTION
            parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); //票据号
           //  add-----kangy-----EKT_BIL_PAY添加字段
            parmSum.setData("PRINT_NO", bilInvoice.initBilInvoice("EKT").getUpdateNo()); //票据号
            parmSum.setData("CREAT_USER", Operator.getID()); //执行人员//=====yanjing
            //-----add by kangy  20160804医疗卡充值
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
             //票号自加一
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
            
            //明细表参数
            TParm feeParm=new TParm();
            feeParm.setData("ORIGINAL_BALANCE",StringTool.round(EKTTemp.getDouble("CURRENT_BALANCE"),2));
            feeParm.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("TOP_UP_PRICE"), 2));
            feeParm.setData("CURRENT_BALANCE",StringTool.round(EKTTemp.getDouble("CURRENT_BALANCE"),2)+StringTool.round(this.getValueDouble("TOP_UP_PRICE"),2));
            parmSum.setData("businessParm",getBusinessParm(parmSum,feeParm).getData());
            //zhangp 20120109 EKT_BIL_PAY 加字段
            parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//售卡操作时间
            parmSum.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
            //bil_pay 充值表数据
            parmSum.setData("billParm",getBillParm(parmSum,feeParm).getData());
            //更新余额
            result = TIOM_AppServer.executeAction(
                    "action.ekt.EKTAction",
                    "TXEKTonFee", parmSum); //
            if (result.getErrCode() < 0) {
               this.messageBox("医疗卡充值失败");
           } else{
               this.messageBox("充值成功");
        
        String bil_business_no = result.getValue("BIL_BUSINESS_NO"); //收据号
        onPrint(bil_business_no);//打印票据
        // onClear();
               onInit();
           }
    }
    /**
    * 充值档添加数据参数
    * @param parm TParm
    * @return TParm
    */
   private TParm getBillParm(TParm parm,TParm feeParm){
       TParm billParm=new TParm();
       billParm.setData("CARD_NO", parm.getValue("CARD_NO")); //卡号
       billParm.setData("CURT_CARDSEQ", 0); //序号
       billParm.setData("ACCNT_TYPE", "4"); //明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
       billParm.setData("MR_NO", parm.getValue("MR_NO"));//病案号
       billParm.setData("ID_NO", parm.getValue("ID_NO"));//身份证号
       billParm.setData("NAME", parm.getValue("NAME"));//病患名称
       billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT"));//充值金额
       billParm.setData("CREAT_USER", Operator.getID());//执行人员
       billParm.setData("OPT_USER", Operator.getID());//操作人员
       billParm.setData("OPT_TERM", Operator.getIP());//执行ip
       billParm.setData("GATHER_TYPE",parm.getValue("GATHER_TYPE"));//支付方式
	   //zhangp 20120109
       billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
       billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
       return billParm;
   }

    /**
     * 医疗卡明细表插入数据
     * @param p TParm
     * @param feeParm TParm
     * @return TParm
     */
    private TParm getBusinessParm(TParm p, TParm feeParm) {
        // 明细档数据
        TParm bilParm = new TParm();
        bilParm.setData("BUSINESS_SEQ", 0);
        bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
        bilParm.setData("MR_NO", pat.getMrNo());
        bilParm.setData("CASE_NO", "none");
        bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
        bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
        bilParm.setData("SEQ_NO", 0);
        bilParm.setData("CHARGE_FLG", p.getValue("CHARGE_FLG")); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
        bilParm.setData("ORIGINAL_BALANCE", feeParm.getValue("ORIGINAL_BALANCE")); //收费前余额
        bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
        bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
        bilParm.setData("CASHIER_CODE", Operator.getID());
        bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
        //1：交易执行完成
        //2：双方确认完成
        bilParm.setData("BUSINESS_STATUS", "1");
        //1：未对帐
        //2：对账成功
        //3：对账失败
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
     * 检测病患相同姓名
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
        //选择病患信息
//        if (same.getCount("MR_NO") > 0) {
//            int sameCount = this.messageBox("提示信息", "已有相同姓名病患信息,是否继续保存此人信息", 0);
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
     * 设置英文名
     */
    public void setPatName1() {
        String patName1 = SYSHzpyTool.getInstance().charToAllPy(TypeTool.
                getString(getValue("PAT_NAME")));
        setValue("PAT_NAME1", patName1);
    }
   /**
    * 充值金额回车事件
    */
   public void topUpFee(){
       //将充值金额显示在应收金额中
       double price=this.getValueDouble("TOP_UP_PRICE")+this.getValueDouble("PROCEDURE_PRICE");
       this.setValue("GATHER_PRICE",price);
       //zhangp 20111223
       this.grabFocus("GATHER_PRICE");
   }
   /**
    * 医疗卡修改密码
    */
   public void updateEKTPwd(){
       TParm sendParm = new TParm();
           TParm reParm = (TParm)this.openDialog(
               "%ROOT%\\config\\ekt\\EKTUpdatePassWord.x", sendParm);
   }

   /**
    * 充值打印
    */
   private void onPrint(String bil_business_no) {
       TParm parm = new TParm();
       parm.setData("TITLE", "TEXT",
                    (Operator.getRegion() != null &&
                     Operator.getRegion().length() > 0 ?
                     Operator.getHospitalCHNFullName() : "所有医院") );
       parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //病案号
       parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //姓名
     //====zhangp 20120525 start
//     parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
     parm.setData("GATHER_NAME", "TEXT", ""); //收款方式
     //====zhangp 20120525 end
       parm.setData("TYPE", "TEXT", "预 收"); //文本预收金额
       parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //收款方式
       parm.setData("AMT", "TEXT",
                    StringTool.round(parmSum.getDouble("BUSINESS_AMT"), 2)); //金额
       parm.setData("SEX_TYPE", "TEXT",
                    parmSum.getValue("SEX_TYPE").equals("1") ? "男" : "女"); //性别
       parm.setData("AMT_AW", "TEXT",
                    StringUtil.getInstance().numberToWord(
                    parmSum.getDouble("BUSINESS_AMT"))); //大写金额
       parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); //台头一
       String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
               getInstance().getDBTime()), "yyyyMMdd"); //年月日
       String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
               getInstance().getDBTime()), "hhmmss"); //时分秒
       parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //台头二
       yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
               getDBTime()), "yyyy/MM/dd"); //年月日
       hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); //时分秒
       parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); //备注
       parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); //票据号
       if (null == bil_business_no)
           bil_business_no = EKTTool.getInstance().getBillBusinessNo(); //补印操作
       parm.setData("ONFEE_NO", "TEXT", bil_business_no); //收据号
       
       
       
       
       parm.setData("PRINT_NO", "TEXT", parmSum.getValue("PRINT_NO")); //票号
       parm.setData("PRINT_DATE", "TEXT", yMd); //打印时间
       parm.setData("DATE", "TEXT", yMd + "    " + hms); //日期
       parm.setData("USER_NAME", "TEXT", Operator.getID()); //收款人
       //=========modify by lim 2012/02/24 begin
       //===zhangp 20120525 start
       parm.setData("O", "TEXT", ""); 

//       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw", parm,true);
       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
       //===zhangp 20120525 end
     //=========modify by lim 2012/02/24 begin
   }
   /**
    * 银行卡读卡操作
    */
   public void onBankCard(){
       //读取银行卡信息
       //输入密码
       this.setValue("BANK_CARD_NO","111111111111111");

   }
   /**
    * 银行卡执行操作
    */
   public void onBankSave(){
       if(bankFlg){
           if(this.getValue("BANK_CARD_NO").toString().length()<=0){
               this.messageBox("先读取银行卡信息");
               return;
           }
           TParm parm = new TParm();
           parm.setData("CARD_NO", this.getValue("CARD_CODE"));
           parm.setData("BANK_CARD_NO", this.getValue("BANK_CARD_NO"));
           TParm result=EKTIO.getInstance().updateEKTAndBank(parm);
           if(result.getErrCode()<0){
               this.messageBox("银行卡关联失败");
           }else{
               this.messageBox("银行卡关联成功");
           }
       }
       else{
           this.messageBox("只有存在医疗卡信息才可以执行银行卡关联");
       }


   }
   
   /**
    * 查询病患
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
    * 医疗卡充值退款档插入数据
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
    * 发卡原因监听
    * ====zhangp 20120202
    */
   public void onSendCause(){
	   TParm result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
	      if(result.getCount()>0){
	    	  setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
	      }
   }
   /**
    * 密码确认
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
    * 密码确认
    * ===zhangp 20120315
    * @return
    */
   public boolean passWord(){
	   String passWord = getValueString("CARD_PWD");
	   String repassWord = getValueString("RE_CARD_PWD");
	   if(!passWord.equals(repassWord)){
		   messageBox("密码不一致");
		   return false;
	   }
	   return true;
   }
   /**
    * 4位密码
    * ===zhangp 20120319
    * @return
    */
   public boolean passWord4(){
	   String passWord = getValueString("CARD_PWD");
	   if(passWord.length()!=4){
		   messageBox("密码为4位");
		   return false;
	   }
	   grabFocus("RE_CARD_PWD");
	   return true;
   }
   /**
    * 初始密码 
    */
   private void setPassWord(){
	   this.setValue("CARD_PWD", "0000");
	   this.setValue("RE_CARD_PWD", "0000");
   }
	/**
	 * 初始化票据
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// 检核开关帐
		if (bilInvoice == null) {
			this.messageBox_("你尚未开账!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("无可打印的票据!");
			// this.onClear();
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("最后一张票据!");
		}
		String endNo_num = bilInvoice.getEndInvno().replaceAll("[^0-9]", "");
		String endNo_word = bilInvoice.getEndInvno().replaceAll("[0-9]", "");
		String nowNo_num = bilInvoice.getUpdateNo().replaceAll("[^0-9]", "");
		String nowNo_word = bilInvoice.getUpdateNo().replaceAll("[0-9]", "");
		if(nowNo_word.equals(endNo_word)&&Long.valueOf(nowNo_num)- Long.valueOf(endNo_num)==1){
			this.messageBox("票据已使用完，请重新领票");
			this.setValue("BIL_CODE","");
			return false;
		}
	if(!compareInvno(bilInvoice.getStartInvno(),bilInvoice.getEndInvno(),bilInvoice.getUpdateNo())){
		this.messageBox("票号超出范围");
		onClear();
		return false;
	}
		callFunction("UI|BIL_CODE|setValue", bilInvoice.getUpdateNo());
		return true;
	}
	/**
	 * 比较票号
	 * @return
	 */
	private boolean compareInvno(String StartInvno, String EndInvno,String UpdateNo) {
		String startNo_num = StartInvno.replaceAll("[^0-9]", "");// 去非数字
		String startNo_word = StartInvno.replaceAll("[0-9]", "");// 去数字
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

