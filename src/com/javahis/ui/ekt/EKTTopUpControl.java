package com.javahis.ui.ekt;

import com.dongyang.control.TControl;

import jdo.reg.SysParmTool;
import jdo.sid.IdCardO;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.util.TypeTool;

import jdo.bil.BILInvoiceTool;
import jdo.bil.BilInvoice;
import jdo.ekt.EKTIO;
//脱卡还原     import jdo.ekt.EKTReadCard;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import jdo.sys.Operator;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.ekt.EKTTool;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;

import jdo.sys.PatTool;

import com.javahis.device.card.IccCardRWUtil;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.sum.GetDateSectionControl;
import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TComboBox;

/**
 * <p>Title: 医疗卡充值</p>
 *
 * <p>Description: 医疗卡充值 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 *
 * <p>Company: </p>
 *
 * @author pangben 20110930
 * @version 1.0
 */
public class EKTTopUpControl extends TControl {
	// 传入数据
	Object paraObject;
	String onwType="";
	String systemCode = "";
	BilInvoice bilInvoice;
    private Pat pat; //病患信息
    private TParm parmEKT;
    private  String  flg ;
    private boolean ektFlg=false;
    private boolean printBil=false;//打印票据时使用
    private TParm parmSum;//执行充值操作参数
    //zhangp 20111227
    private int row = -1;//选中行
    private TParm regionParm;
    private TParm insParm;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
    /*private IccCardRWUtil DEV= new IccCardRWUtil();
    private Boolean dev_flg=false;*/   // kangy 脱卡还原
    public EKTTopUpControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        //=====zhangp 20120224 支付方式    modify start 
    	Object obj  =this.getParameter() ;
    	  if (obj instanceof TParm) {
            TParm  parm = (TParm) obj;
              flg = parm.getValue("FLG");}
    	if("Y".equals(flg)){
    		onReadEKT();
    	}
    	 String id = EKTTool.getInstance().getPayTypeDefault();
        setValue("GATHER_TYPE", id);
        
        //=======zhangp 20120224 modify end
    	// =====kangy===20160801===初始化票据
		 bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("EKT"));
		callFunction("UI|BIL_CODE|Enable", false);
		 callFunction("UI|TOP_UPFEE|setValue", "0");
		 regionParm = SYSRegionTool.getInstance().selectdata(
					Operator.getRegion()); // 获得医保区域代码
    }
    /**      
     * 读卡操作
     */
   
    	public void onReadCard(){
    		 /*   // kangy 脱卡还原  start
   		 dev_flg=EKTTool.getInstance().Equipment(Operator.getIP());// add by kangy 20170622 判断新旧设备   旧设备==false
    			if(dev_flg){
    				String cardType=DEV.getCardType();
    				if("EKTCard".equals(cardType)){
    					onReadEKT();
    				} else if("IDCard".equals(cardType)){
    					onReadIdCard();
    				} else if("INSCard".equals(cardType)){
    					onReadInsCard();
    				}
    			}else 
    			*/       // kangy 脱卡还原  end
    				onReadEKT();
    				
 }
    
    /**      
     * 读医疗卡
     */
    public void onReadEKT() {
    	//==start====add by kangy 20160912 ===读取医疗卡刷新下一票号
    	bilInvoice = new BilInvoice();
		 callFunction("UI|BIL_CODE|setValue", bilInvoice.initBilInvoice("EKT").getUpdateNo());
    	//==end====add by kangy 20160912
        //读取医疗卡
		 // kangy 脱卡还原   start
		 /*	if(dev_flg){
	    		  parmEKT=EKTReadCard.getInstance().readEKT();
	    	}else{
	    		 parmEKT = EKTReadCard.getInstance().TXreadEKT();
	    	}*/ 
		 parmEKT=EKTIO.getInstance().TXreadEKT();
		 // kangy 脱卡还原     end
        if (null == parmEKT || parmEKT.getErrCode() < 0 ||
            parmEKT.getValue("MR_NO").length() <= 0) {
            this.messageBox(parmEKT.getErrText());
            parmEKT = null;
            return;
        }
        EKT(parmEKT);
      /*  //卡片余额
        this.setValue("OLD_EKTFEE", parmEKT.getDouble("CURRENT_BALANCE"));
        //卡片序号
        this.setValue("SEQ", parmEKT.getValue("SEQ"));
        //callFunction("UI|CARD_CODE|setEnabled", false); //卡号不可编辑
        this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
        onQuery();
        //20111228 zhangp
//        if(this.getValueString("MR_NO")!=null&&!this.getValueString("MR_NO").equals("")){
//        	onTable();
//        }
        //====201202026 zhangp modify start
        grabFocus("TOP_UPFEE");
        //====201202026 zhangp modify end
*/    }
    
    public void EKT(TParm parmEKT){  // add by kangy 
        //卡片余额
        this.setValue("OLD_EKTFEE", parmEKT.getDouble("CURRENT_BALANCE"));
        //退款金额
        this.setValue("UN_FEE", parmEKT.getDouble("CURRENT_BALANCE"));
        //卡片序号
        this.setValue("SEQ", parmEKT.getValue("SEQ"));
        this.setValue("MR_NO", parmEKT.getValue("MR_NO"));

        onQuery();
      
        if(this.getValueString("MR_NO")!=null&&!this.getValueString("MR_NO").equals("")){
     	   onTable();
        }
        grabFocus("UN_FEE");
    }
    // kangy 脱卡还原    start
   /* public void onReadIdCard(){ // add by kangy 读取身份证
    	//TParm idParm = IdCardO.getInstance().readIdCard();
    	TParm idParm= EKTReadCard.getInstance().readIDCard();
		if(idParm.getCount()==0){
			this.messageBox("该病人没有医疗卡，请执行制卡操作");
			return;
		}
		if(idParm.getCount()>1){
			parmEKT = (TParm) openDialog(
					"%ROOT%\\config\\reg\\REGPatMrNoSelect.x", idParm);
		}else
			parmEKT=idParm.getRow(0);
   		EKT(parmEKT);
    }
    
    public void onReadInsCard(){ //add by kangy 读取 医保卡
    	TParm parm = new TParm();
		parm.setData("MR_NO", "");
		//医院编码@费用发生时间@类别
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// 费用发生时间	
		String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+admDate+"@"+"1";
		parm.setData("ADVANCE_CODE",advancecode);
		parm.setData("ADVANCE_TYPE","1");//正常
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSReadInsCard.x");
		int returnType = insParm.getInt("RETURN_TYPE"); // 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
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
 // kangy 脱卡还原    end

    /**
     * 保存
     */
    public void onSave() {
        //TParm parm=new TParm();
        if (null == parmEKT || parmEKT.getErrCode() < 0 ||
            parmEKT.getValue("MR_NO").length() <= 0) {
            this.messageBox("读取医疗卡失败");
            parmEKT = null;
            return;

        }
        TParm parm=new TParm();
        parm.setData("MR_NO", pat.getMrNo());
        parm = EKTTool.getInstance().selectEKTIssuelog(parm);
        if (parm.getCount() <= 0) {
            this.messageBox("此病患没有医疗卡信息,请重新制卡");
            return;
        }
        if(null==this.getValue("BIL_CODE")||this.getValue("BIL_CODE").toString().length()<=0){
        	this.messageBox("无可打印票据！");
        	return;
        }
        onFEE();
        onReadCard();
        //parm.setData("CARD_NO", parmEKT.getValue("MR_NO")+parmEKT.getValue("SEQ"));
    }

    /**
     * 查询病患信息
     */
    public void onQuery() {
        pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("无此病案信息");
            this.grabFocus("PAT_NAME");
            this.setValue("MR_NO", "");
            callFunction("UI|MR_NO|setEnabled", true); //病案号可编辑
            return;
        }
        //病案号
        this.setValue("MR_NO", pat.getMrNo());
        //姓名
        this.setValue("PAT_NAME", pat.getName());
        //出生日期
        setValue("BIRTH_DATE", pat.getBirthday());
        //性别
        setValue("SEX_CODE", pat.getSexCode());
        callFunction("UI|MR_NO|setEnabled", false); //病案号可编辑
        //===zhangp 20120328 start
        TParm parm=new TParm();
        parm.setData("MR_NO",pat.getMrNo());
        TParm EKTparm= EKTTool.getInstance().selectEKTIssuelog(parm);
        if (EKTparm.getCount() <= 0) {
        	messageBox("无医疗卡");
        	return;
        }
        this.setValue("OLD_EKTFEE",StringTool.round(EKTparm.getDouble("CURRENT_BALANCE", 0),2) );
        if(this.getValueString("MR_NO")!=null&&!this.getValueString("MR_NO").equals("")){
        	onTable();
        }
        //===zhangp 20120328 end
    }

    /**
     * 充值文本框回车事件
     */
    public void addFee() {
        if (this.getValueDouble("TOP_UPFEE") < 0) {
            this.messageBox("充值金额不可以为负值");
            return;
        }
        this.setValue("SUM_EKTFEE",
                      this.getValueDouble("TOP_UPFEE") +
                      this.getValueDouble("OLD_EKTFEE"));
    }

    /**
     * 收费方法
     */
    private void onFEE() {
//        if(!txEKT){
//            this.messageBox("请读取医疗卡信息");
//            return;
//        }
    	bilInvoice=new BilInvoice();
        if (this.getValue("TOP_UPFEE").toString().length() <= 0) {
            this.messageBox("请输入要充值的金额");
            return;
        }
        if (this.getValueDouble("TOP_UPFEE") <= 0) {
            this.messageBox("充值金额不正确");
            return;
        }
        if (((TTextFormat)this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
            this.messageBox("支付方式不可以为空值");
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
        //20120104 zhangp 注 充值不需要验证
//        if (this.messageBox("收费", "是否执行充值操作", 0) != 0) {
//            return;
//        }
        //密码校验
//        if (!this.getValue("CARD_PWD").toString().trim().equals(parmEKT.
//                getValue("PASSWORD", 0).trim())) {
//            this.messageBox("密码不符,请重新输入");
//            this.setValue("CARD_PWD", "");
//            this.grabFocus("CARD_PWD");
//            return;
//        }
            TParm result = null;
            parmSum= new TParm();
            parmSum.setData("CARD_NO", pat.getMrNo() + this.getValue("SEQ"));
            parmSum.setData("CURRENT_BALANCE", StringTool.round(parmEKT.getDouble("CURRENT_BALANCE"),
                    2) +
                    StringTool.round(this.getValueDouble("TOP_UPFEE"), 2));
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
            parmSum.setData("ISSUERSN_CODE", "充值"); //发卡原因
            parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //支付方式
            parmSum.setData("GATHER_TYPE_NAME",this.getText("GATHER_TYPE")); //支付方式名称
            parmSum.setData("BUSINESS_AMT",StringTool.round( this.getValueDouble("TOP_UPFEE"),2)); //充值金额
            parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); //性别
            parmSum.setData("DESCRIPTION",this.getValue("DESCRIPTION"));//备注
            parmSum.setData("BIL_CODE",this.getValue("BIL_CODE"));//票据号
            parmSum.setData("PRINT_NO",bilInvoice.initBilInvoice("EKT").getUpdateNo());//票据号
            parmSum.setData("CREAT_USER", Operator.getID()); //执行人员//=====yanjing
           //add by kangy 
        	TParm inFeeParm=new TParm();
			inFeeParm.setData("RECP_TYPE","EKT");
			inFeeParm.setData("INV_NO",bilInvoice.initBilInvoice("EKT").getUpdateNo());
			//inFeeParm.setData("RECEIPT_NO",bil_business_no);
			inFeeParm.setData("CASHIER_CODE",Operator.getID());
			inFeeParm.setData("AR_AMT",this.getValue("TOP_UPFEE"));
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
				
            TParm updatanoParm=new TParm();
            BilInvoice bilInvo=bilInvoice.initBilInvoice("EKT");
            String updateno = StringTool.addString(bilInvo.getUpdateNo());
            updatanoParm.setData("UPDATE_NO",updateno);
            updatanoParm.setData("RECP_TYPE","EKT");
            updatanoParm.setData("STATUS",bilInvo.getStatus());
            updatanoParm.setData("CASHIER_CODE",bilInvo.getCashierCode());
            updatanoParm.setData("START_INVNO",bilInvo.getStartInvno());
            parmSum.setData("updatanoparm",updatanoParm.getData());
                
               // BILInvoiceTool.getInstance().updateDatePrint(updatenoParm);	
            
            //明细表参数
            TParm feeParm = new TParm();
            feeParm.setData("ORIGINAL_BALANCE",
                            StringTool.round(parmEKT.getDouble("CURRENT_BALANCE"),2)); //原金额
            feeParm.setData("BUSINESS_AMT", StringTool.round(this.getValueDouble("TOP_UPFEE"),2)); //充值金额
            feeParm.setData("CURRENT_BALANCE",
                            StringTool.round(parmEKT.
                                             getDouble("CURRENT_BALANCE"), 2) +
                            StringTool.round(this.getValueDouble("TOP_UPFEE"),
                                             2));
            //EKT_ACCNTDETAIL 数据
            parmSum.setData("businessParm", getBusinessParm(parmSum, feeParm).getData());
            //zhangp 20120109 EKT_BIL_PAY 加字段
            parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//售卡操作时间
            parmSum.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
            //bil_pay 充值表数据
            parmSum.setData("billParm", getBillParm(parmSum, feeParm).getData());
            //更新余额
            result = TIOM_AppServer.executeAction(
                    "action.ekt.EKTAction",
                    "TXEKTonFee", parmSum);
//            System.out.println("***888 parmSum is:"+parmSum);
            if (result.getErrCode() < 0) {
                this.messageBox("医疗卡充值失败");
            } else {
                this.messageBox("充值成功");
                printBil=true;
                String bil_business_no=result.getValue("BIL_BUSINESS_NO");//收据号
                try {
                	  onPrint(bil_business_no,"");
				} catch (Exception e) {
					this.messageBox("打印出现问题,请执行补印操作");
					// TODO: handle exception
				}
            }
//        }
        //zhangp 20120131 充值完成后重新查询充值记录
        onTable();
        onClear();
        onInit();
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
        bilParm.setData("CHARGE_FLG", "3"); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
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
     * 充值档添加数据参数
     * @param parm TParm
     * @return TParm
     */
    private TParm getBillParm(TParm parm, TParm feeParm) {
        TParm billParm = new TParm();
        billParm.setData("CARD_NO", parm.getValue("CARD_NO")); //卡号
        billParm.setData("CURT_CARDSEQ", 0); //序号
        billParm.setData("ACCNT_TYPE", "4"); //明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
        billParm.setData("MR_NO", parm.getValue("MR_NO")); //病案号
        billParm.setData("ID_NO", parm.getValue("ID_NO")); //身份证号
        billParm.setData("NAME", parm.getValue("NAME")); //病患名称
        billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT")); //充值金额
        billParm.setData("CREAT_USER", Operator.getID()); //执行人员
        billParm.setData("OPT_USER", Operator.getID()); //操作人员
        billParm.setData("OPT_TERM", Operator.getIP()); //执行ip
        billParm.setData("GATHER_TYPE", parm.getValue("GATHER_TYPE")); //支付方式
 	   //zhangp 20120109
        billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
        billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
        return billParm;
    }
    /**
     *清空
     */
    public void onClear() {
        clearValue(" MR_NO;PAT_NAME;SEQ; " +
                   " BIRTH_DATE;SEX_CODE; " +
                   " DESCRIPTION; " +
                   " OLD_EKTFEE;TOP_UPFEE;SUM_EKTFEE");
        //设置默认服务等级
       // txEKT = false; //泰心医疗卡写卡管控
        parmEKT = null; //医疗卡读卡parm
        ektFlg=false;
        printBil=false;
        parmSum=null;//执行退款操作参数
        //解锁病患
        if (pat != null)
            PatTool.getInstance().unLockPat(pat.getMrNo());
        String id = EKTTool.getInstance().getPayTypeDefault();
        setValue("GATHER_TYPE", id);
        //===zhangp 20120328 start
        callFunction("UI|MR_NO|setEnabled", true); //病案号可编辑
        //===zhangp 20120328 end
        onInit();
    }
    /**
     * 充值打印
     */
    private void onPrint(String bil_business_no,String copy) {
        if (!printBil) {
            this.messageBox("进行医疗卡充值操作才可以打印");
            return;
        }
        TParm parm = new TParm();
        parm.setData("TITLE", "TEXT",
                     (Operator.getRegion() != null &&
                      Operator.getRegion().length() > 0 ?
                      Operator.getHospitalCHNFullName() : "所有医院"));
        parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //病案号
        parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //姓名
        parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //收款方式
        parm.setData("AMT", "TEXT", StringTool.round(parmSum.getDouble("BUSINESS_AMT"),2)); //金额
        //====zhangp 20120525 start
//        parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
        parm.setData("GATHER_NAME", "TEXT", ""); //收款方式
        //====zhangp 20120525 end
        parm.setData("TYPE", "TEXT", "预 收"); //文本预收金额
        parm.setData("SEX_TYPE", "TEXT", parmSum.getValue("SEX_TYPE").equals("1")?"男":"女"); //性别
        parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(parmSum.getDouble("BUSINESS_AMT"))); //大写金额
        parm.setData("TOP1", "TEXT", "EKTRT001 FROM "+Operator.getID()); //台头一
        String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
                getInstance().getDBTime()), "yyyyMMdd"); //年月日
        String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
                getInstance().getDBTime()), "hhmmss"); //时分秒
        parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //台头二
        yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
                getDBTime()), "yyyy/MM/dd"); //年月日
        hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
                getDBTime()), "HH:mm"); //时分秒
        parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); //备注
        parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); //票据号
        parm.setData("PRINT_NO", "TEXT", parmSum.getValue("PRINT_NO")); //票据号
        if(null == bil_business_no)
             bil_business_no=  EKTTool.getInstance().getBillBusinessNo();//补印操作
        parm.setData("ONFEE_NO", "TEXT", bil_business_no); //收据号
        parm.setData("PRINT_DATE", "TEXT", yMd); //打印时间
        parm.setData("DATE", "TEXT", yMd + "    " + hms); //日期
        parm.setData("USER_NAME", "TEXT", Operator.getID()); //收款人
        parm.setData("COPY", "TEXT", copy); //收款人
        //===zhangp 20120525 start
        parm.setData("O", "TEXT", ""); 
//        this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw", parm,true);
        this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
        //===zhangp 20120525 end
    }
    /**
     * 补印 
     * 20111228 zhangp
     */
    public void onRePrint(){
    	//=start===add by kangy ==20160926===
    	 bilInvoice = new BilInvoice();
    	   if (null == parmEKT || parmEKT.getErrCode() < 0 ||
    	            parmEKT.getValue("MR_NO").length() <= 0) {
    	            this.messageBox("请先执行读卡操作");
    	            parmEKT = null;
    	            return;
    	        }
    	//===end===add by kangy ==20160926===
    	
    	
 	   TTable table = (TTable)this.callFunction("UI|TABLE|getThis");
  		row = table.getSelectedRow();
 	   if(row==-1){
 		   messageBox("请选择要补印的记录");
 		   return;
 	   }else{
 		  if(null==this.getValue("BIL_CODE")||this.getValue("BIL_CODE").toString().length()<=0){
 	        	this.messageBox("无可打印票据！");
 	        	return;
 	        }
 		     
 	        TParm checkparm=new TParm();
 	    	checkparm.setData("RECP_TYPE","EKT");
 	    	checkparm.setData("INV_NO",this.getValue("BIL_CODE"));
 	    	checkparm.setData("CASHIER_CODE",Operator.getID());
 	    	TParm res=BILInvoiceTool.getInstance().checkUpdateNo(checkparm);
 	    	if(res.getCount("RECP_TYPE")>0){
 	    		this.messageBox("该票据号已被使用！");
 	    		onClear();
 	    		return;
 	    	}
 	    	if(!compareInvno(bilInvoice.initBilInvoice("EKT").getStartInvno(),bilInvoice.initBilInvoice("EKT").getEndInvno(),this.getValue("BIL_CODE").toString())){
				this.messageBox("票号超出范围");
				onClear();
				return;
			}
 		   
 	  		String bilBusinessNo = table.getValueAt(row, 1).toString();
 	  		String print_no = table.getValueAt(row, 7).toString();
 	  		String sql = "SELECT MR_NO FROM EKT_BIL_PAY WHERE BIL_BUSINESS_NO = '"+bilBusinessNo+"'";
 	  		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
 	  		String mrNo = result.getData("MR_NO", 0).toString();
 	  		pat = pat.onQueryByMrNo(mrNo);
 		   TParm parm = new TParm();
 	       parm.setData("TITLE","TEXT",
 	                    (Operator.getRegion() != null &&
 	                     Operator.getRegion().length() > 0 ?
 	                     Operator.getHospitalCHNFullName() : "所有医院"));
 	       parm.setData("MR_NO","TEXT", mrNo); //病案号
 	       parm.setData("PAT_NAME","TEXT", pat.getName()); //姓名
 	        //====zhangp 20120525 start
//         parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
         parm.setData("GATHER_NAME", "TEXT", ""); //收款方式
         //====zhangp 20120525 end
 	        parm.setData("TYPE","TEXT", "预 收"); //文本预收金额
 	       String gatherType = table.getValueAt(row, 4).toString();
 	      String sqlgt =
 	            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' AND ID = '"+gatherType+"' ORDER BY SEQ,ID ";
 	      TParm resultgt = new TParm(TJDODBTool.getInstance().select(sqlgt));
 	       parm.setData("GATHER_TYPE", "TEXT", resultgt.getData("NAME", 0).toString()); //收款方式
 	       parm.setData("AMT", "TEXT", StringTool.round((Double)table.getValueAt(row, 3),2)); //金额
 	       parm.setData("SEX_TYPE","TEXT", pat.getSexString()); //性别
 	       parm.setData("AMT_AW","TEXT",  StringUtil.getInstance().numberToWord((Double)table.getValueAt(row, 3))); //大写金额
 	       parm.setData("TOP1", "TEXT", "EKTRT001 FROM "+Operator.getID()); //台头一
 	       String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
 	               getInstance().getDBTime()), "yyyyMMdd"); //年月日
 	       String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
 	               getInstance().getDBTime()), "hhmmss"); //时分秒
 	       parm.setData("TOP2","TEXT", "Send On " + yMd + " At " + hms); //台头二
 	       yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
 	               getDBTime()), "yyyy/MM/dd"); //年月日
 	       hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
 	               getDBTime()), "HH:mm"); //时分秒
 	       parm.setData("DESCRIPTION","TEXT", ""); //备注
 	       parm.setData("BILL_NO", "TEXT", ""); //票据号
 	       parm.setData("ONFEE_NO", "TEXT", bilBusinessNo); //收据号
	 	  /* String s="SELECT PRINT_NO FROM EKT_BIL_PAY WHERE BIL_BUSINESS_NO='"+bilBusinessNo+"'";
	 	   TParm printNoparm=new TParm(TJDODBTool.getInstance().select(s));
 	       parm.setData("PRINT_NO","TEXT",printNoparm.getData("PRINT_NO",0).toString());*/
 	      
	 	   parm.setData("PRINT_NO","TEXT",bilInvoice.initBilInvoice("EKT").getUpdateNo());
 	       parm.setData("PRINT_DATE","TEXT",  yMd); //打印时间
 	       parm.setData("DATE","TEXT",yMd + "    " + hms); //日期
 	       parm.setData("USER_NAME","TEXT",  Operator.getID()); //收款人
 	       parm.setData("COPY","TEXT", "(copy)"); //补印注记
 	       parm.setData("INV_NO","TEXT",this.getValue("BIL_CODE")); 
 	        //===zhangp 20120525 start
 	        parm.setData("O", ""); 
 	       BilInvoice bilInvo=bilInvoice.initBilInvoice("EKT");
 	      parm.setData("START_INVNO","TEXT",bilInvo.getStartInvno());
 	        
 	        //补印操作 =======add by kangy=======20160805
 	/*      String sqls="SELECT ACCOUNT_FLG,ACCOUNT_SEQ,ACCOUNT_USER,ACCOUNT_DATE FROM BIL_INVRCP WHERE " +
			" RECP_TYPE='EKT'  AND  RECEIPT_NO='"+bilBusinessNo+"' AND INV_NO='"+print_no+"'";
		TParm reprint=new TParm(TJDODBTool.getInstance().select(sqls));*/
	       TParm insertparm=new TParm();
 	       insertparm.setData("RECP_TYPE","EKT");
 	       insertparm.setData("INV_NO",bilInvoice.initBilInvoice("EKT").getUpdateNo());
 	       insertparm.setData("RECEIPT_NO",bilBusinessNo);
 	       insertparm.setData("CASHIER_CODE",Operator.getID());
 	       insertparm.setData("AR_AMT",StringTool.round((Double)table.getValueAt(row, 3),2));
 	       insertparm.setData("CANCEL_FLG","0");
 	       insertparm.setData("CANCEL_USER","");
 	       insertparm.setData("CANCEL_DATE","");
 	       insertparm.setData("OPT_USER",Operator.getID());
 	       insertparm.setData("OPT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
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
 	      updateparm.setData("CANCEL_FLG","3");//补印作废
 	      updateparm.setData("CANCEL_USER",Operator.getID());
 	      updateparm.setData("CANCEL_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
 	      parm.setData("updateparm",updateparm.getData());
 	      
 	      TParm updateinvoiceparm=new TParm();
 	     updateinvoiceparm.setData("RECP_TYPE","EKT");
 	     updateinvoiceparm.setData("START_INVNO",bilInvo.getStartInvno());
 	     updateinvoiceparm.setData("STATUS",bilInvo.getStatus());
 	     updateinvoiceparm.setData("UPDATE_NO",StringTool.addString(bilInvo.getUpdateNo()));
 	     updateinvoiceparm.setData("CASHIER_CODE",bilInvo.getCashierCode());
 	     parm.setData("updateinvoiceparm",updateinvoiceparm.getData()); 
 	   
 	     
 	     TParm updateektbilpayparm=new TParm();
 	    updateektbilpayparm.setData("PRINT_NO",bilInvoice.initBilInvoice("EKT").getUpdateNo());
 	    updateektbilpayparm.setData("BIL_BUSINESS_NO",table.getValueAt(row, 1));
 	     parm.setData("updateektbilpayparm",updateektbilpayparm.getData()); 
		//System.out.println("parm++++sssss+++"+parm);
		result = TIOM_AppServer.executeAction(
                "action.ekt.EKTAction",
                "TXEKTReprint", parm); 

          //  System.out.println("result"+result);
          if (result.getErrCode() < 0) {
                this.messageBox("补印失败");
            } else {
// 	        this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw", parm,true);
 	        this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
 	        //===zhangp 20120525 end
 	        onInit();
 	        onQuery();
 	   }
            }
     }
    /**
     *医疗卡充值记录table
     *zhangp 20111228
     */
    public void onTable(){
 	   String mrNo = getValueString("MR_NO");
 	   StringBuilder sql = new StringBuilder();
 	   String select = "SELECT B.EKT_CARD_NO,A.BIL_BUSINESS_NO," +
 	   		"A.OPT_DATE,A.AMT,A.GATHER_TYPE,A.CREAT_USER,A.ACCNT_TYPE,A.PRINT_NO FROM EKT_BIL_PAY A, " +
 	   		"EKT_ISSUELOG B WHERE A.CARD_NO = B.CARD_NO AND " +
 	   		"A.ACCNT_TYPE = '4'";
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
     * 查询病患名称
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
        //====201202026 zhangp modify start
        grabFocus("TOP_UPFEE");
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
