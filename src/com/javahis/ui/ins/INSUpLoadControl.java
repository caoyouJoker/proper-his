package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.dongyang.data.TParm;
import jdo.ins.InsManager;
import com.dongyang.control.TControl;
import jdo.ins.INSUpLoadTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import jdo.ins.INSTJTool;
import jdo.mro.MRORecordTool;

/**
 * <p>Title: 医保申报控制类</p>
 *
 * <p>Description: 医保申报控制类</p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2011</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2012.02.10
 * @version 1.0
 */
public class INSUpLoadControl extends TControl {
    private String case_no; // 就诊号
    private String mr_no; // 病患号码
    String insPayType = "";
    String singleFlg = "";
    String invNo = "";
    //医保医院代码
    private String nhi_hosp_code;
    //医院名称
    private String nhi_hosp_desc;
    
    private TTable table;

    private int selectedCheckBoxCount = 0;

    private TParm insParm;//获得医保信息
    

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        TParm parm = (TParm) getParameter();
        if (null == parm) {
            return;
        }
        case_no = "";
        invNo ="";
        case_no = parm.getValue("CASE_NO");
        insPayType = parm.getValue("INS_PAT_TYPE");
        singleFlg = parm.getValue("SINGLE_TYPE");
        invNo = parm.getValue("INV_NO");
        parm.setData("CASE_NO", case_no);
        //System.out.println("医保类别"+ this.getValue("INS_PAT_TYPE"));
        //System.out.println("单病种类别"+ this.getValue("SINGLE_TYPE"));
        this.setValue("INS_PAT_TYPE",insPayType);
        this.setValue("SINGLE_TYPE",singleFlg);
        parm.setData("INS_PAT_TYPE", this.getValue("INS_PAT_TYPE"));
        parm.setData("SINGLE_TYPE", this.getValue("SINGLE_TYPE"));
        TParm patParm = INSUpLoadTool.getInstance().getPatInfo(parm);
        //给table赋值
        //System.out.println("patParm" + patParm);
        this.callFunction("UI|Table|setParmValue", patParm);
        this.table = (TTable)this.getComponent("Table");
        this.table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                    "onTableComponent");

        TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
        this.nhi_hosp_desc = hospParm.getValue("REGION_CHN_DESC", 0);
        if (this.getValueInt("INS_PAT_TYPE")==2) {
        	 callFunction("UI|readCard|setEnabled", false);
        }
    }

    /**
     * table监听checkBox事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int allRow = table.getRowCount();
        for (int i = 0; i < allRow; i++) {
            if ("Y".equals(tableParm.getValue("FLG", i))) {
                this.selectedCheckBoxCount++;
            }
        }
        return true;
    }

    /**
     * 保存
     */
    public void onSave() {
        this.insPayType = this.getValueString("INS_PAT_TYPE");
        this.singleFlg = this.getValueString("SINGLE_TYPE");
//        if (this.getValueInt("INS_PAT_TYPE")==1) {
//        	  if (null==insParm || null==insParm.getValue("PERSONAL_NO") ||insParm.getValue("PERSONAL_NO").length()<=0 ) {
//      			this.messageBox("请执行读卡操作");
//      			return;
//      		}
//		}

//        判断下拉框是否被选中.
        if ("".equals(this.insPayType)) {
            messageBox("请选择医保类别.");
            return;
        }
        if ("".equals(this.singleFlg)) {
            messageBox("请选择单病种类别.");
            return;
        }

        TParm parm = new TParm();

        /************************四种情况要传的信息************************************/
//        parm.setData("REGION_CODE", Operator.getRegion());
//        parm.setData("YEAR_MON", "201112");
//        parm.setData("CASE_NO", "111221000006");
//        parm.setData("DS_DATE", "2012/2/12");
//        parm.setData("CONFIRM_NO", "000000001");
//        parm.setData("MR_NO", "000000001133");
//        parm.setData("ADM_SEQ", "15");
//        parm.setData("OPT_USER", Operator.getID());
//        parm.setData("OPT_TERM", Operator.getIP());
//        Timestamp sysTime = SystemTool.getInstance().getDate();
//        String datestr = StringTool.getString(sysTime, "yyyyMMddHHmmss");
//        parm.setData("OPT_DATE", datestr);
        table = (TTable)this.getComponent("Table");
        TParm tableParm = table.getParmValue();
        parm = tableParm.getRow(table.getSelectedRow());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        Timestamp sysTime = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(sysTime, "yyyyMMddHHmmss");
        parm.setData("OPT_DATE", datestr);
        parm.setData("REGION_CODE", Operator.getRegion());
//        System.out.println("行数据" + parm);

        String dsDateStr = StringTool.getString(parm.getTimestamp("DS_DATE"),
                                                "yyyyMMdd");
//        System.out.println("出院日期"+dsDateStr);
        parm.setData("DS_DATE", dsDateStr);
        //System.out.println("table显示数据"+parm);
        /************************************************************/

        TParm result = new TParm();
        if (this.selectedCheckBoxCount == 0) {
            this.messageBox("请选择申报数据");
            return;
        }
	    String sql =" SELECT SUM(A.TOTAL_AMT) AS TOTAL_AMT" +
	        		" FROM INS_IBS_UPLOAD A,INS_ADM_CONFIRM B" +
	        		" WHERE A.ADM_SEQ = B.ADM_SEQ" +
	        		" AND B.CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
	        		" AND B.ADM_SEQ = '"+ parm.getValue("ADM_SEQ") + "'" +
	        		" AND A.NHI_ORDER_CODE  NOT LIKE '***%'";       
		TParm ibsUpLoadParm = new TParm(TJDODBTool.getInstance().select(sql));
//      System.out.println("ibsUpLoadParm===" + ibsUpLoadParm);
		if (ibsUpLoadParm.getErrCode() < 0) {
			return;
		}
		// 判断是否跨年操作处理 获得结束时间
		DateFormat df1 = new SimpleDateFormat("yyyy");
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String tempDate = df1.format(sysTime);//当前年份
		String startDate = StringTool.getString(parm.getTimestamp("IN_DATE"),
                           "yyyyMMdd");//开始时间
		//判断是否是在别家医院未出院又在心血管医院住院的病人
		String sql3 =" SELECT TO_CHAR(IN_DATE,'yyyyMMdd') AS IN_DATE" +
	     " FROM ADM_INP" +
	     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'";		
		TParm adminpParm = new TParm(TJDODBTool.getInstance().select(sql3));
		String in_date =(String)adminpParm.getData("IN_DATE",0);
		if((Integer.parseInt(startDate)!=Integer.parseInt(in_date))&&
		   (Integer.parseInt(startDate)!=Integer.parseInt(tempDate + "0101")))
			startDate = in_date;	     
		String endDate = "";//结束时间
		if (Integer.parseInt(startDate) < Integer.parseInt(tempDate + "0101")) 
			endDate = ""+ (Integer.parseInt(tempDate) -1) + "1231"+"235959";
		else 
			endDate = df.format(sysTime)+"235959";
		startDate =startDate + "000000";
		String sql1 =" SELECT SUM(TOT_AMT) AS TOT_AMT" +
				     " FROM IBS_ORDD" +
				     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
				     " AND BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
                     " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
		TParm ibsOrddParm = new TParm(TJDODBTool.getInstance().select(sql1));
//		System.out.println("ibsOrddParm===" + ibsOrddParm);
		if (ibsOrddParm.getErrCode() < 0) {
			return;
		}
		if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0) != ibsOrddParm
				.getDouble("TOT_AMT", 0)){
			messageBox("申报数据有问题");
			return; 
		}
		//判断跨年医保是否上传病案首页和手术记录
		String date = ""+ (Integer.parseInt(tempDate) -1) + "1231"+"000000";
		String sql2 =" SELECT * FROM ADM_INP A"+
                     " WHERE A.CASE_NO = '"+ parm.getValue("CASE_NO") + "'"+
                     " AND (A.DS_DATE IS NULL OR A.DS_DATE> TO_DATE('"+date+"','YYYYMMDDHH24MISS'))"+
                     " AND A.IN_DATE <=TO_DATE('"+date+"','YYYYMMDDHH24MISS')"+ 
                     " AND A.CANCEL_FLG = 'N'";
        TParm flgParm = new TParm(TJDODBTool.getInstance().select(sql2));
        if (flgParm.getErrCode() < 0) {
			return;
		}
        startDate =startDate.substring(0, 8);
        String flg ="";
        if(flgParm==null)
        	flg = "Y";
        else if(Integer.parseInt(startDate) == Integer.parseInt(tempDate + "0101"))
        	flg = "Y";
             else 
        	flg = "N";
        parm.setData("FLG", flg);
        if (insPayType.equals("1")) { //城职
            if (singleFlg.equals("2")) { //单病种
                if (!onSaveCZSingle(parm, result))
                    return;
            } else { //普通
                if (!onSaveCZGeneral(parm, result))
                    return;
            }
        } else { //城居
            if (singleFlg.equals("2")) { //单病种
                if (!onSaveCJSingle(parm, result))
                    return;
            } else { //普通
                if (!onSaveCJGeneral(parm, result))
                    return;
            }
        }
        this.messageBox("申报成功!");
        this.closeWindow();
    }

    /**
     * 城职单病种保存
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    public boolean onSaveCZSingle(TParm parm, TParm result) {
        //得到结算资料
        TParm result1 = INSUpLoadTool.getInstance().getIBSData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }

        //查询上传资料
        TParm result2 = INSUpLoadTool.getInstance().getIBSUploadData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        //撤销住院费用申报
      this.DataDown_sp_H(parm);
//        if (this.DataDown_sp_H(parm).getErrCode() < 0) {
//            return false;
//        }
        //查询同意住院书是否被审核
        if (this.DataDown_sp_Q(parm).getErrCode() < 0) {
            return false;
        }
        //5.1得到补助金额，补助金额2
        TParm result3 = INSUpLoadTool.getInstance().getIBSHelpAmt(parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return false;
        }
        //5.2得到医师证照号
        TParm result4 = INSUpLoadTool.getInstance().getDrQualifyCode(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return false;
        }

        //6.1得到上传病案首页信息
        /**TParm result5 = INSUpLoadTool.getInstance().getMROUploadData(parm);
        if (result5.getErrCode() < 0) {
            this.messageBox(result5.getErrText());
            return false;

        }

        //6.2得到单病种费用分割中病历首页的内容
        TParm result6 = INSUpLoadTool.getInstance().getMROAllData(parm);
        if (result6.getErrCode() < 0) {
            this.messageBox(result6.getErrText());
            return false;
        }
        result6.addData("L_TIMES", result5.getData("L_TIMES", 0));
        result6.addData("M_TIMES", result5.getData("M_TIMES", 0));
        result6.addData("S_TIMES", result5.getData("S_TIMES", 0));
        result6.addData("FP_NOTE", result5.getData("FP_NOTE", 0));
        result6.addData("DS_SUMMARY", result5.getData("DS_SUMMARY", 0));
        //门急诊诊断信息
        parm.setData("IO_TYPE","I");
		TParm oeDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
		String oeDiag = "";
		for (int i = 0; i < oeDiagParm.getCount(); i++) 
		{
       	  oeDiag += (oeDiagParm.getData("ICD_CODE", i)+"_"+oeDiagParm.getData("ICD_DESC", i));
		}		
		result6.setData("OE_DIAG_CODE", 0, oeDiag);
        //入院诊断信息
        parm.setData("IO_TYPE","M");
		TParm inDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
		String inDiag = "";
		for (int i = 0; i < inDiagParm.getCount(); i++) 
		{
       	  inDiag += (inDiagParm.getData("ICD_CODE", i)+"_"+inDiagParm.getData("ICD_DESC", i));
		}
		result6.setData("IN_DIAG_CODE", 0, inDiag);
		//出院诊断信息
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);		
        for (int i = 0; i < outDiagParm.getCount(); i++) 
        {
          String icdCode = "" + outDiagParm.getData("ICD_CODE", i);
		  String icdDesc = "" + outDiagParm.getData("ICD_DESC", i);
		  String icdStatusDesc = outDiagParm.getData("ICD_STATUS", i)==null?"5":("" +  outDiagParm.getData("ICD_STATUS", i));
		  result6.setData("OUT_ICD_CODE"+(i+1), 0, icdCode);
		  result6.setData("OUT_ICD_DESC"+(i+1), 0, icdDesc);
		  result6.setData("ICD_STATUS_DESC"+(i+1), 0, icdStatusDesc);
		}
        //6.3病历首页上传
        if (this.DataDown_sp_E2(result6, "1").getErrCode() < 0) {
            return false;
        }
        //7.1得到单病种费用分割中病历首页之手术资料的内容
        TParm result7 = INSUpLoadTool.getInstance().getMROOpData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        result7.addData("ADM_SEQ", parm.getData("ADM_SEQ"));
        //7.2病历首页之手术及操作上传//
//        this.DataDown_sp_E3(result7, "1");
        if (this.DataDown_sp_E3(result7, "1").getErrCode() < 0) {
            return false;
        } */
        //得到城职病案首页的内容
        TParm CZMRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (CZMRO.getErrCode() < 0) {
            this.messageBox(CZMRO.getErrText());
            return false;
        }
        if (CZMRO.getData("SUM_TOT", 0) == null||
            CZMRO.getData("SUM_TOT", 0).equals("")) {
          	 this.messageBox("首页费用未转入,请联系病案室");
               return false;
          }       
       //出院诊断信息
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return false;
	        }
		//主诊断
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
		CZMRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//次诊断
		String secDiag = "";
        for (int i = 1; i < outDiagParm.getCount(); i++) 
        {
        	secDiag+=outDiagParm.getData("ICD_DESC", i)+"@"+
	         outDiagParm.getData("ICD_CODE", i)+"@"+
	         outDiagParm.getData("IN_PAT_CONDITION", i)+"%";
		}
        CZMRO.setData("OUT_DIAG_OTHER",0,secDiag.length()>0? 
        		secDiag.substring(0, secDiag.length() - 1):"");
        //城职病案首页上传
        if (this.DataUpload_G(CZMRO,"CZ").getErrCode() < 0) {
            return false;
        }
        //得到城职案首页之手术及操作的内容
        TParm CZMROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (CZMROOP.getErrCode() < 0) {
            this.messageBox(CZMROOP.getErrText());
            return false;
        }
        if(CZMROOP.getCount()>0){
        //城职住院病案首页之手术及操作上传
        if (this.DataUpload_H(CZMROOP,"CZ").getErrCode() < 0) {
            return false;
        }
       }
        //8.得到单病种结算信息和出院信息上传部分信息查询
        TParm result8 = INSUpLoadTool.getInstance().getSingleIBSData(parm);
        if (result8.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        
        //9.单病种结算信息和出院信息上传
        //医师编码
        result1.setData("DR_QUALIFY_CODE",result4.getData("DR_QUALIFY_CODE"));
        TParm spE1Parm = this.DataDown_sp_E1(result1,result8);
        if (spE1Parm.getErrCode() < 0) {
            return false;
        }
        String newConfirmNo = spE1Parm.getValue("NEW_CONFIRM_NO");
        result2.setData("NEW_CONFIRM_NO", newConfirmNo);
        //10.住院上传费用明细
//        this.DataUpload_A(result2);
        if (this.DataUpload_A(result2).getErrCode() < 0) {
            return false;
        }
        //11.住院帐户支付确认
        TParm spE8Parm = this.DataDown_sp_E8(parm);
        if (spE8Parm.getErrCode() < 0) {
            return false;
        }
        double accountPayAmt = spE8Parm.getDouble("ACCOUNT_PAY_AMT");
        double personAccountAmt = spE8Parm.getDouble("PERSON_ACCOUNT_AMT");
        parm.setData("ACCOUNT_PAY_AMT", accountPayAmt);
        parm.setData("PERSON_ACCOUNT_AMT", personAccountAmt);
        parm.setData("NEW_CONFIRM_NO", newConfirmNo);     
        //更新对应数据库标记位
        result = TIOM_AppServer.executeAction("action.ins.INSUpLoadAction",
                                              "saveUpLoadData", parm);

        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        return true;
    }
    /**
	 * 刷卡操作
	 */
	public void onReadCard() {
		TParm parm = new TParm();
//		parm.setData("MR_NO", this.getValue("MR_NO"));// 病案号
		// 人群类别
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCardOne.x", parm);
		if (null == insParm)
			return;
		int returnType = insParm.getInt("RETURN_TYPE");// 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			insParm=null;
			this.messageBox("读取医保卡失败");
			return;
		}
		this.messageBox("读卡成功");
	}
    /**
     * 城职普通保存
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    public boolean onSaveCZGeneral(TParm parm, TParm result) {
        //1.得到结算资料
        TParm result1 = INSUpLoadTool.getInstance().getIBSData(parm);
        if (result1.getErrCode() < 0) {
            this.messageBox(result1.getErrText());
            return false;
        }
        //2.查询上传资料
        TParm result2 = INSUpLoadTool.getInstance().getIBSUploadData(parm);
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText());
            return false;
        }
        //System.out.println("查询上传资料>>>>>>>>>>>>"+result2);
        //System.out.println("撤销住院费用申报"+parm);
        //3.撤销住院费用申报
        this.DataDown_sp_H(parm);
        //4.查询同意住院书是否被审核
//        this.DataDown_sp_Q(parm);
        if (this.DataDown_sp_Q(parm).getErrCode() < 0) {
            return false;
        }
        if(parm.getValue("FLG").equals("Y")){
        //得到城职病案首页的内容
        TParm CZMRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (CZMRO.getErrCode() < 0) {
            this.messageBox(CZMRO.getErrText());
            return false;
        }
        if (CZMRO.getData("SUM_TOT", 0) == null||
        	CZMRO.getData("SUM_TOT", 0).equals("")){ 
       	 this.messageBox("首页费用未转入,请联系病案室");
            return false;
       }
       //出院诊断信息
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return false;
	        }
		//主诊断
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
//		System.out.println("mainDiag>>>>>>>>>>>>"+mainDiag);
		CZMRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//次诊断
		String secDiag = "";
        for (int i = 1; i < outDiagParm.getCount(); i++) 
        {
        	secDiag+=outDiagParm.getData("ICD_DESC", i)+"@"+
	         outDiagParm.getData("ICD_CODE", i)+"@"+
	         outDiagParm.getData("IN_PAT_CONDITION", i)+"%";
		}
//        System.out.println("secDiag>>>>>>>>>>>>"+secDiag);
        CZMRO.setData("OUT_DIAG_OTHER",0,secDiag.length()>0? 
        		secDiag.substring(0, secDiag.length() - 1):"");
        //城职病案首页上传
        if (this.DataUpload_G(CZMRO,"CZ").getErrCode() < 0) {
            return false;
        }
        //得到城职案首页之手术及操作的内容
        TParm CZMROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (CZMROOP.getErrCode() < 0) {
            this.messageBox(CZMROOP.getErrText());
            return false;
        }
        if(CZMROOP.getCount()>0){
        //城职住院病案首页之手术及操作上传
        if (this.DataUpload_H(CZMROOP,"CZ").getErrCode() < 0) {
            return false;
        }
      }
    }
        //5.1得到补助金额，补助金额2
        TParm result3 = INSUpLoadTool.getInstance().getIBSHelpAmt(parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return false;
        }
        //5.2得到医师证照号
        TParm result4 = INSUpLoadTool.getInstance().getDrQualifyCode(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return false;
        }
        result1.setData("DRQUALIFYCODE", result4.getData("DRQUALIFYCODE", 0));
        //6.结算信息和出院信息上传
        result1.addData("ARMYAI_AMT", result3.getData("ARMYAI_AMT", 0));
        result1.addData("TOT_PUBMANADD_AMT",
                        result3.getData("TOT_PUBMANADD_AMT", 0));
//        System.out.println("上传入参"+result1);
        TParm upParm = this.DataDown_sp_E(result1);
        if (upParm.getErrCode() < 0) {
            return false;
        }
//        System.out.println("上传出参"+upParm);
        result2.setData("NEW_CONFIRM_NO", upParm.getValue("NEW_CONFIRM_NO"));
        //7.住院上传费用明细
//        System.out.println("上传明细入参"+result2);
//        this.DataUpload_A(result2);
        if (this.DataUpload_A(result2).getErrCode() < 0) {
            return false;
        }
//        System.out.println("上传明细成功");
        //8.住院帐户支付确认
        TParm spE8Parm = this.DataDown_sp_E8(parm);
        if (spE8Parm.getErrCode() < 0) {
            return false;
        }
//        System.out.println("spE8Parm========="+spE8Parm);
        double accountPayAmt = spE8Parm.getDouble("ACCOUNT_PAY_AMT");
        double personAccountAmt = spE8Parm.getDouble("PERSON_ACCOUNT_AMT");
        parm.setData("ACCOUNT_PAY_AMT", accountPayAmt);
        parm.setData("PERSON_ACCOUNT_AMT", personAccountAmt);
        parm.setData("NEW_CONFIRM_NO", upParm.getValue("NEW_CONFIRM_NO"));
//        //System.out.println("更新对应数据库标记位〉〉〉〉〉〉〉〉〉〉〉>>"+parm);
        //更新对应数据库标记位
        result = TIOM_AppServer.executeAction("action.ins.INSUpLoadAction",
                                              "saveUpLoadData", parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * 城居单病种保存
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    public boolean onSaveCJSingle(TParm parm, TParm result) {
    	//System.out.println("onSaveCJSingle()");
        //1.撤销申报
        //(1)	调用医保接口函数(撤销申报)
        this.DataDown_czys_I(parm);
        
        //(2),(3)撤销申报更新
        TParm result0 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAppData", parm);
        if (result0.getErrCode() < 0) {
            this.messageBox(result0.getErrText());
            return false;
        }
        //2.查询资格确认书审核情况
        if (this.DataDown_czys_D(parm).getErrCode() < 0) {
            return false;
        }
        //(2)	得到医保状态,(3)	审核资格确认书
        TParm result1 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "checkConfirmData", parm);
        if (result1.getErrCode() < 0) {
            this.messageBox(result1.getErrText());
            return false;
        }
        //3.病案首页上传
        //(1).获得上传病案首页时查询部分上传信息
        /**TParm result2 = INSUpLoadTool.getInstance().getMROUploadData(parm);
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText());
            return false;
        }

        //(2)初始化单病种费用分割中病历首页的内容
        TParm result3 = INSUpLoadTool.getInstance().getMROAllData(parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return false;
        }
        result3.addData("L_TIMES", result2.getData("L_TIMES", 0));
        result3.addData("M_TIMES", result2.getData("M_TIMES", 0));
        result3.addData("S_TIMES", result2.getData("S_TIMES", 0));
        result3.addData("FP_NOTE", result2.getData("FP_NOTE", 0));
        result3.addData("DS_SUMMARY", result2.getData("DS_SUMMARY", 0));

        //门急诊诊断信息
        parm.setData("IO_TYPE","I");
		TParm oeDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
		String oeDiag = "";
		for (int i = 0; i < oeDiagParm.getCount(); i++) 
		{
       	  oeDiag += (oeDiagParm.getData("ICD_CODE", i)+"_"+oeDiagParm.getData("ICD_DESC", i));
		}		
		result3.setData("OE_DIAG_CODE", 0, oeDiag);
        //入院诊断信息
        parm.setData("IO_TYPE","M");
		TParm inDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
		String inDiag = "";
		for (int i = 0; i < inDiagParm.getCount(); i++) 
		{
       	  inDiag += (inDiagParm.getData("ICD_CODE", i)+"_"+inDiagParm.getData("ICD_DESC", i));
		}
		result3.setData("IN_DIAG_CODE", 0, inDiag);
		//出院诊断信息
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);		
        for (int i = 0; i < outDiagParm.getCount(); i++) 
        {
          String icdCode = "" + outDiagParm.getData("ICD_CODE", i);
		  String icdDesc = "" + outDiagParm.getData("ICD_DESC", i);
		  String icdStatusDesc = outDiagParm.getData("ICD_STATUS", i)==null?"5":("" +  outDiagParm.getData("ICD_STATUS", i));
		  result3.setData("OUT_ICD_CODE"+(i+1), 0, icdCode);
		  result3.setData("OUT_ICD_DESC"+(i+1), 0, icdDesc);
		  result3.setData("ICD_STATUS_DESC"+(i+1), 0, icdStatusDesc);
		}		
		
        //(3)病历首页上传
        if (this.DataDown_sp_E2(result3, "3").getErrCode() < 0) {
            return false;
        }

        //4.手术资料上传
        //(1).初始化单病种费用分割中病历首页之手术资料的内容
        TParm result4 = INSUpLoadTool.getInstance().getMROOpData(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return false;
        }
        result4.addData("ADM_SEQ", parm.getData("ADM_SEQ"));

        //(2).病历首页之手术及操作上传
//        this.DataDown_sp_E3(result4, "3");
        if (this.DataDown_sp_E3(result4, "3").getErrCode() < 0) {
            return false;
        } */
        //得到城居病案首页的内容
        TParm CJMRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (CJMRO.getErrCode() < 0) {
            this.messageBox(CJMRO.getErrText());
            return false;
        }
        if (CJMRO.getData("SUM_TOT", 0) == null||
        	CJMRO.getData("SUM_TOT", 0).equals("")) {
       	 this.messageBox("首页费用未转入,请联系病案室");
            return false;
       }
       //出院诊断信息
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return false;
	        }
		//主诊断
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
		CJMRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//次诊断
		String secDiag = "";
        for (int i = 1; i < outDiagParm.getCount(); i++) 
        {
        	secDiag+=outDiagParm.getData("ICD_DESC", i)+"@"+
	         outDiagParm.getData("ICD_CODE", i)+"@"+
	         outDiagParm.getData("IN_PAT_CONDITION", i)+"%";
		}
        CJMRO.setData("OUT_DIAG_OTHER",0,secDiag.length()>0? 
        		secDiag.substring(0, secDiag.length() - 1):"");
        //城居病案首页上传
        if (this.DataUpload_G(CJMRO,"CJ").getErrCode() < 0) {
            return false;
        }
        //得到城居案首页之手术及操作的内容
        TParm CJMROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (CJMROOP.getErrCode() < 0) {
            this.messageBox(CJMROOP.getErrText());
            return false;
        }
        if(CJMROOP.getCount()>0){
        //城居住院病案首页之手术及操作上传
        if (this.DataUpload_H(CJMROOP,"CJ").getErrCode() < 0) {
            return false;
        }
       }
        //5.出院信息上传
        //(1).获得医保申报信息
        TParm result5 = INSUpLoadTool.getInstance().getINSMedAppInfo(parm);
        if (result5.getErrCode() < 0) {
            this.messageBox(result5.getErrText());
            return false;
        }
        
        //(3).单病种结算信息和出院信息上传 部分信息查询
        TParm result6 = INSUpLoadTool.getInstance().getSingleIBSData(parm);
        if (result6.getErrCode() < 0) {
            this.messageBox(result6.getErrText());
            return false;
        }        
        
        //(2).单病种结算信息和出院信息上传
       //System.out.println("result6:"+result6);
       TParm  czysHParm = this.DataDown_czys_H1(result5,result6);
        if (czysHParm.getErrCode() < 0) {
            return false;
        }
        String newAdmSeq = czysHParm.getValue("NEWADM_SEQ");
        parm.setData("NEWADM_SEQ", newAdmSeq);
        //(4),(5)更新新的就诊顺序号
        TParm result7 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAdmSeqData", parm);
        if (result7.getErrCode() < 0) {
            this.messageBox(result7.getErrText());
            return false;
        }

        //6.明细上传
        //(1)获得费用上传明细
        TParm result8 = INSUpLoadTool.getInstance().getInsMedInfo(parm);
        if (result8.getErrCode() < 0) {
            this.messageBox(result8.getErrText());
            return false;
        }
        //(2)住院上传费用明细
//        this.DataUpload_E(result8);
        if (this.DataUpload_E(result8).getErrCode() < 0) {
            return false;
        }

        //(3)费用明细上传回写(4)费用明细上传回写INS_ADM_CONFIRM  //
        TParm result9 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdInsIbsBackData", parm);
        if (result9.getErrCode() < 0) {
            this.messageBox(result9.getErrText());
            return false;
        }

        return true;

    }

    /**
     * 城居普通保存
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    public boolean onSaveCJGeneral(TParm parm, TParm result) {
        //1.撤销申报
        //(1).撤销申报
        this.DataDown_czys_I(parm);
//        System.out.println("(1).撤销申报");
        //(2),(3)撤销申报更新
        TParm result0 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAppData", parm);
//        System.out.println("(2),(3)撤销申报更新");
        if (result0.getErrCode() < 0) {
            this.messageBox(result0.getErrText());
            return false;
        }
        //2.查询资格确认书审核情况
        //(1)查询资格确认书审核情况
        TParm czysDParm = this.DataDown_czys_D(parm);
        if (czysDParm.getErrCode() < 0) {
            return false;
        }
//        System.out.println("(1)查询资格确认书审核情况"+czysDParm);
        if (!czysDParm.getBoolean("ALLOW_FLG_FLG"))
            return false;
//        System.out.println("审核资格确认书入参"+parm);
        ///(2)	得到医保状态,(3)	审核资格确认书
        //========pangben 2012-8-16 取消INS_ADM_CONFIRM IN_STATUS=7状态
//      TParm result1 = TIOM_AppServer.executeAction(
//              "action.ins.INSUpLoadAction",
//              "checkConfirmData", parm);
////      System.out.println("(2)	得到医保状态,(3)	审核资格确认书");
//      if (result1.getErrCode() < 0) {
//          this.messageBox(result1.getErrText());
//          return false;
//      }
      //========pangben 2012-8-16 stop
        if(parm.getValue("FLG").equals("Y")){ 
        //得到城居病案首页的内容
        TParm CJMRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (CJMRO.getErrCode() < 0) {
            this.messageBox(CJMRO.getErrText());
            return false;
        }
        if (CJMRO.getData("SUM_TOT", 0) == null||
            CJMRO.getData("SUM_TOT", 0).equals(""))  {
        	 this.messageBox("首页费用未转入,请联系病案室");
             return false;
        }
       //出院诊断信息
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return false;
	        }
		//主诊断
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
//		System.out.println("mainDiag>>>>>>>>>>>>"+mainDiag);
		CJMRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//次诊断
		String secDiag = "";
        for (int i = 1; i < outDiagParm.getCount(); i++) 
        {
        	secDiag+=outDiagParm.getData("ICD_DESC", i)+"@"+
	         outDiagParm.getData("ICD_CODE", i)+"@"+
	         outDiagParm.getData("IN_PAT_CONDITION", i)+"%";
		}
//        System.out.println("secDiag>>>>>>>>>>>>"+secDiag);
        CJMRO.setData("OUT_DIAG_OTHER",0,secDiag.length()>0? 
        		secDiag.substring(0, secDiag.length() - 1):"");
        //城居病案首页上传
        if (this.DataUpload_G(CJMRO,"CJ").getErrCode() < 0) {
            return false;
        }
        //得到城居案首页之手术及操作的内容
        TParm CJMROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (CJMROOP.getErrCode() < 0) {
            this.messageBox(CJMROOP.getErrText());
            return false;
        }
        if(CJMROOP.getCount()>0){
        //城居住院病案首页之手术及操作上传
        if (this.DataUpload_H(CJMROOP,"CJ").getErrCode() < 0) {
            return false;
        }
    }
  }
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("DS_DATE", parm.getData("DS_DATE"));
        //System.out.println("获得医保申报信息入参"+parm);
        //3.出院信息上传
        //(1).获得医保申报信息
        TParm result2 = INSUpLoadTool.getInstance().getINSMedAppInfo(parm);
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText());
            return false;
        }
        //System.out.println("结算信息和出院信息上传《《《《《入参"+result2);
        //(2).结算信息和出院信息上传
        TParm czysHParm = this.DataDown_czys_H(result2);
        if (czysHParm.getErrCode() < 0) {
            return false;
        }
        String newAdmSeq = czysHParm.getValue("NEWADM_SEQ");
        parm.setData("NEWADM_SEQ", newAdmSeq);
        //(3),(4)更新新的就诊顺序号
        TParm result3 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAdmSeqData", parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return false;
        }
        //System.out.println("获得费用上传明细<<<<<<<<<<入参"+parm);
        //6.明细上传
        //(1)获得费用上传明细
        TParm result4 = INSUpLoadTool.getInstance().getInsMedInfo(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return false;
        }
        //(2)住院上传费用明细
//        this.DataUpload_E(result4);
        if (this.DataUpload_E(result4).getErrCode() < 0) {
            return false;
        }
        //(3)费用明细上传回写(4)费用明细上传回写INS_ADM_CONFIRM
        TParm result5 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdInsIbsBackData", parm);
        if (result5.getErrCode() < 0) {
            this.messageBox(result5.getErrText());
            return false;
        }
        return true;
    }

    /**
     * 撤销住院费用申报
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_sp_H(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_sp");
        confInfoParm.setData("PLOT_TYPE", "H");
        confInfoParm.addData("CONFIRM_NO", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("HOSP_NHI_NO", this.nhi_hosp_code);
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询同意住院书是否被审核
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_sp_Q(TParm parm) {
        TParm result = new TParm();

        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_sp");
        confInfoParm.setData("PLOT_TYPE", "Q");

        confInfoParm.addData("CONFIRM_NO", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("HOSP_NHI_NO", this.nhi_hosp_code);
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 病历首页上传
     * @param parm TParm
     * @param flg String
     * @return TParm
     */
    public TParm DataDown_sp_E2(TParm parm, String flg) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_sp");
        confInParm.setData("PLOT_TYPE", "E2");
        confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));
        confInParm.addData("IN_TIMES", parm.getData("IN_TIMES", 0));
        confInParm.addData("MR_NO", parm.getData("MR_NO", 0));
        confInParm.addData("COM_ADDR", parm.getData("O_ADDRESS", 0));
        confInParm.addData("COM_TEL", parm.getData("O_TEL", 0));
        confInParm.addData("HOME_ADDR", parm.getData("H_ADDRESS", 0));
        confInParm.addData("HOME_TEL", parm.getData("H_TEL", 0));
        confInParm.addData("CONCACT_NAME", parm.getData("CONTACTER", 0));
        confInParm.addData("CONCACT_RELATION", parm.getData("RELATION_DESC", 0));
        confInParm.addData("CONCACT_TEL", parm.getData("CONT_TEL", 0));
        confInParm.addData("CONCACT_ADDR", parm.getData("CONT_ADDRESS", 0));
        confInParm.addData("DIAG_CODE", parm.getData("OE_DIAG_CODE", 0));
        confInParm.addData("IN_DEPT", parm.getData("IN_DEPT", 0));
        confInParm.addData("IN_ROOM", parm.getData("N_ROOM_NO", 0));
        confInParm.addData("TURN_DEPT", parm.getData("TRANS_DEPT", 0));
        confInParm.addData("OUT_DEPT", parm.getData("OUT_DEPT", 0));
        confInParm.addData("OUT_ROOM", parm.getData("OUT_ROOM_NO", 0));
        confInParm.addData("IN_STATE", parm.getData("IN_CONDITION", 0));
        confInParm.addData("IN_DIAG",  parm.getData("IN_DIAG_CODE", 0));
        confInParm.addData("OUT_DIAG", parm.getData("OUT_ICD_DESC1", 0));
        confInParm.addData("OUT_DIAG1", parm.getData("OUT_ICD_DESC2", 0));
        confInParm.addData("OUT_DIAG2", parm.getData("OUT_ICD_DESC3", 0));
        confInParm.addData("OUT_DIAG3", parm.getData("OUT_ICD_DESC4", 0));
        confInParm.addData("OUT_DIAG4", parm.getData("OUT_ICD_DESC5", 0));
        confInParm.addData("OUT_DIAG_STATE", parm.getData("ICD_STATUS_DESC1", 0));
        confInParm.addData("OUT_DIAG1_STATE", parm.getData("ICD_STATUS_DESC2", 0));
        confInParm.addData("OUT_DIAG2_STATE", parm.getData("ICD_STATUS_DESC3", 0));
        confInParm.addData("OUT_DIAG3_STATE", parm.getData("ICD_STATUS_DESC4", 0));
        confInParm.addData("OUT_DIAG4_STATE", parm.getData("ICD_STATUS_DESC5", 0));
        confInParm.addData("OUT_DIAG_ICD", parm.getData("OUT_ICD_CODE1", 0));
        confInParm.addData("OUT_DIAG1_ICD", parm.getData("OUT_ICD_CODE2", 0));
        confInParm.addData("OUT_DIAG2_ICD", parm.getData("OUT_ICD_CODE3", 0));
        confInParm.addData("OUT_DIAG3_ICD", parm.getData("OUT_ICD_CODE4", 0));
        confInParm.addData("OUT_DIAG4_ICD", parm.getData("OUT_ICD_CODE5", 0));
        confInParm.addData("HOSP_INFACT_NAME", parm.getData("INTE_DIAG_CODE", 0));
        confInParm.addData("ILL_DIAG", parm.getData("PATHOLOGY_DIAG", 0));
        confInParm.addData("EXT_FACTOR", parm.getData("EX_RSN", 0));
        confInParm.addData("RESCUE_B", parm.getData("L_TIMES", 0));
        confInParm.addData("RESCUE_M", parm.getData("M_TIMES", 0));
        confInParm.addData("RESCUE_S", parm.getData("S_TIMES", 0));
        confInParm.addData("TREAT_DOCT", parm.getData("VS_DR_NAME1", 0));
        confInParm.addData("TREAT_DOCT_NO", parm.getData("DR_QUALIFY_CODE", 0));
        confInParm.addData("IN_DOCT", parm.getData("VS_DR_NAME1", 0));
        confInParm.addData("MAIN_DOCT", parm.getData("USER_NAME", 0));
        confInParm.addData("HEAD_DOCT", parm.getData("PROF_DR_NAME", 0));
        confInParm.addData("DEPT_HEAD", parm.getData("DIRECTOR_DR_NAME", 0));
        confInParm.addData("FIRST_RECORD", parm.getData("FP_NOTE", 0));
        confInParm.addData("OUT_SUMMARY", parm.getData("DS_SUMMARY", 0));
        confInParm.addData("OTHER1", "");
        confInParm.addData("OTHER2", "");
        confInParm.addData("OTHER3", "");
        confInParm.addData("OTHER4", "");
        confInParm.addData("OTHER5", "");
        confInParm.addData("OTHER6", "");
        confInParm.addData("INSURANCE_TYPE", flg); //城职住院 1 ;城居住院 3

        confInParm.addData("PARM_COUNT", 55);
        result = InsManager.getInstance().safe(confInParm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 病历首页之手术及操作上传
     * @param parm TParm
     * @param flg String
     * @return TParm
     */
    public TParm DataDown_sp_E3(TParm parm, String flg) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();        
        int count = parm.getCount("NAME");
        confInfoParm.setData("PIPELINE", "DataDown_sp");
        confInfoParm.setData("PLOT_TYPE", "E3");
        for (int i = 0; i < count; i++) {
            confInfoParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ", 0));
            confInfoParm.addData("ADM_DATE", parm.getValue("OP_DATE", i));
            confInfoParm.addData("NAME", parm.getValue("NAME", i));
            confInfoParm.addData("DOCT_NAME", parm.getValue("DOCT_NAME", i));
            confInfoParm.addData("1ASSISTANT_NAME",
                                 parm.getValue("ASSISTANT_NAME", i));
            confInfoParm.addData("MAZUI_MOD", parm.getValue("MAZUI_MOD", i));
            confInfoParm.addData("MAZUI_DOCT", parm.getValue("MAZUI_DOCT", i));
            confInfoParm.addData("HEAL_LEV", parm.getValue("HEAL_LEV", i));
            confInfoParm.addData("SEQ", parm.getValue("SEQ", i));
            confInfoParm.addData("INSURANCE_TYPE", flg); //城职住院 1; 城居住院 2
            confInfoParm.addData("PARM_COUNT", 10);
        }
//        System.out.println("confInfoParm:"+confInfoParm);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 单病种结算信息和出院信息上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_sp_E1(TParm parm,TParm dataParm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_sp");
        confInfoParm.setData("PLOT_TYPE", "E1");
        confInfoParm.addData("CONFIRM_NO", parm.getData("ADM_SEQ", 0));
        confInfoParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));
        confInfoParm.addData("SID", parm.getData("IDNO", 0));
        confInfoParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));
        confInfoParm.addData("HOSP_CLEFT_CENTER",
                             parm.getData("INSBRANCH_CODE", 0));
        confInfoParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
        confInfoParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));
        confInfoParm.addData("IN_DATE", parm.getData("IN_DATE", 0));
        confInfoParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));
        //String diagCode  =  ""+parm.getData("DIAG_CODE", 0);
        //匹配银海诊断
        confInfoParm.addData("DIAG_CODE",INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        confInfoParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //次诊断截取传入
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }            
        confInfoParm.addData("DIAG_DESC2", diagdesc2);
        confInfoParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));
        confInfoParm.addData("OWN_RATE",
                             parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                             parm.getDouble("OWN_RATE", 0) / 100);
        confInfoParm.addData("DECREASE_RATE",
                             parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
                             parm.getDouble("DECREASE_RATE", 0) / 100);
        confInfoParm.addData("REALOWN_RATE",
                             parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
                             parm.getDouble("REALOWN_RATE", 0) / 100);
        confInfoParm.addData("INSOWN_RATE",
                             parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
                             parm.getDouble("INSOWN_RATE", 0) / 100);
        confInfoParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        confInfoParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));
        confInfoParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));
        confInfoParm.addData("DEPT", parm.getData("DEPT_DESC", 0));
        confInfoParm.addData("BASEMED_BALANCE",
                             parm.getData("BASEMED_BALANCE", 0));
        confInfoParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));
        confInfoParm.addData("STANDARD_AMT",
                             parm.getData("START_STANDARD_AMT", 0));
        confInfoParm.addData("ISSUE", parm.getData("YEAR_MON", 0));
        confInfoParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));
        confInfoParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));
        confInfoParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));
        confInfoParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));
        confInfoParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));
        confInfoParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));
        confInfoParm.addData("OP_AMT", parm.getData("OP_AMT", 0));
        confInfoParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));
        confInfoParm.addData("BED_AMT", parm.getData("BED_AMT", 0));
        confInfoParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));
        confInfoParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));
        confInfoParm.addData("MATERIAL_NHI_AMT",
                             parm.getData("MATERIAL_NHI_AMT", 0));
        confInfoParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));
        confInfoParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));
        confInfoParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));
        confInfoParm.addData("BLOODALL_NHI_AMT",
                             parm.getData("BLOODALL_NHI_AMT", 0));
        confInfoParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));
        confInfoParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));
        confInfoParm.addData("NHI_OWN_AMT", parm.getData("SINGLE_NHI_AMT", 0)); //病种申报金额
        confInfoParm.addData("EXT_OWN_AMT",
                             parm.getData("SINGLE_STANDARD_OWN_AMT", 0)); //医院超病种标准自负金额
        confInfoParm.addData("COMP_AMT", parm.getData("SINGLE_SUPPLYING_AMT", 0)); //基本医疗保险补足金额
        confInfoParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        confInfoParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        //统筹基金自负标准金额
        confInfoParm.addData("APPLY_OWN_AMT_STD", dataParm.getData("STARTPAY_OWN_AMT", 0));
        //医疗救助自负标准金额
        confInfoParm.addData("INS_OWN_AMT_STD", dataParm.getData("PERCOPAYMENT_RATE_AMT", 0)); 
        confInfoParm.addData("INS_HIGHLIMIT_AMT",
                             parm.getData("INS_HIGHLIMIT_AMT", 0));
        confInfoParm.addData("TRANBLOOD_OWN_AMT",
                             parm.getData("BLOODALL_OWN_AMT", 0));
        confInfoParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));
        confInfoParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));
        confInfoParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        confInfoParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));
        confInfoParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));
        confInfoParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));
        //补助金额
        confInfoParm.addData("ARMYAI_AMT", parm.getData("ARMYAI_AMT", 0));
        //社区编码
        confInfoParm.addData("COMU_NO", "");
        //单病种编码
        confInfoParm.addData("SIN_DISEASE_CODE", dataParm.getData("SDISEASE_CODE", 0)); 
        //医师编码
        confInfoParm.addData("DR_CODE", parm.getData("DR_QUALIFY_CODE", 0));
        //补助金额2
        confInfoParm.addData("PUBMANAI_AMT", parm.getData("PUBMANAI_AMT", 0));
        
        //特需自费金额
        double BED_SINGLE_AMT = dataParm.getDouble("BED_SINGLE_AMT", 0);
        double MATERIAL_SINGLE_AMT = dataParm.getDouble("MATERIAL_SINGLE_AMT", 0);
        double specNeedAmt = BED_SINGLE_AMT + MATERIAL_SINGLE_AMT;
        confInfoParm.addData("SPEC_NEED_AMT", specNeedAmt);
        //其它出院诊断
        confInfoParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        confInfoParm.addData("PARM_COUNT", 65);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 住院上传费用明细
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_A(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        //System.out.println("上传明细个数+++++++++++++"+parm.getCount("ADM_SEQ")+"<>"+parm);
        //上传明细
        int count = parm.getCount("ADM_SEQ");
        for (int m = 0; m < count; m++) {
            //System.out.println("进入循环"+m+parm.getRow(m));
            confInfoParm.addData("CONFIRM_NO", parm.getData("ADM_SEQ", m));
            confInfoParm.addData("NEW_CONFIRM_NO",
                                 parm.getData("NEW_CONFIRM_NO")); //TODO:?正确吗
            confInfoParm.addData("HOSP_CLEFT_CENTER",
                                 parm.getData("INSBRANCH_CODE", m));

//            String chargeDateF =parm.getValue("CHARGE_DATE", m);
//            String chargeDateE = chargeDateF.substring(0,4)+"-"+chargeDateF.substring(4,6)+"-"+chargeDateF.substring(6,8)+
//                                 " "+chargeDateF.substring(8,10)+":"+chargeDateF.substring(10,12)+":"+
//                                 chargeDateF.substring(12,14);
            //明细输入时间
            confInfoParm.addData("CHARGE_DATE", parm.getValue("CHARGE_DATE",m));
            confInfoParm.addData("SEQ_NO", parm.getData("SEQ_NO", m));
            confInfoParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", m));
            confInfoParm.addData("NHI_ORDER_CODE",
                                 parm.getData("NHI_ORDER_CODE", m));
            confInfoParm.addData("ORDER_DESC", parm.getData("ORDER_DESC", m));
            confInfoParm.addData("OWN_RATE",
                                 parm.getDouble("OWN_RATE", m) == 0 ? 0.00 :
                                 parm.getDouble("OWN_RATE", m) );
            confInfoParm.addData("DOSE_CODE", parm.getData("JX", m));
            confInfoParm.addData("STANDARD", parm.getData("GG", m));
            confInfoParm.addData("PRICE", parm.getData("PRICE", m));
            confInfoParm.addData("QTY", parm.getData("QTY", m));
            confInfoParm.addData("TOTAL_AMT", parm.getData("TOTAL_AMT", m));
            confInfoParm.addData("TOTAL_NHI_AMT",
                                 parm.getData("TOTAL_NHI_AMT", m));
            confInfoParm.addData("OWN_AMT", parm.getData("OWN_AMT", m));
            confInfoParm.addData("ADDPAY_AMT", parm.getData("ADDPAY_AMT", m));
            confInfoParm.addData("OP_FLG", parm.getValue("OP_FLG", m).equals("Y")?"1":"0");
            confInfoParm.addData("ADDPAY_FLG", parm.getValue("ADDPAY_FLG", m).equals("Y")?"1":"0");
            confInfoParm.addData("NHI_ORD_CLASS_CODE",
                                 parm.getData("NHI_ORD_CLASS_CODE", m));
            confInfoParm.addData("PHAADD_FLG", parm.getValue("PHAADD_FLG", m).equals("Y")?"1":"0");
//            System.out.println("出院带药注记传入前"+parm.getValue("CARRY_FLG", m));
            confInfoParm.addData("CARRY_FLG", parm.getValue("CARRY_FLG", m).equals("Y")?"1":"0");
//            System.out.println("出院带药注记传入后"+confInfoParm.getValue("CARRY_FLG",m));
            confInfoParm.addData("PZWH", parm.getData("PZWH", m));
//            confInfoParm.addData("INVNO", invNo); //医保票据号//TODO:不知数据来源
            confInfoParm.addData("INVNO", invNo); //医保票据号//TODO:不知数据来源

            confInfoParm.addData("PARM_COUNT", 24);
        }
        confInfoParm.setData("PIPELINE", "DataUpload");
        confInfoParm.setData("PLOT_TYPE", "A");
        result = InsManager.getInstance().safe(confInfoParm);
//        System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 住院帐户支付确认
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_sp_E8(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_sp");
        confInfoParm.setData("PLOT_TYPE", "E8");

        confInfoParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("OWN_NO", parm.getValue("PERSONAL_NO"));
        //暂时不传值
        confInfoParm.addData("PASS_WORD", "");
        confInfoParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);
        //===============pangben 2012-4-13 添加刷卡验证码
//        confInfoParm.addData("CHECK_CODES", insParm.getValue("CHECK_CODES"));
        confInfoParm.addData("CHECK_CODES", "");
        //===============pangben 2012-4-13 stop
        confInfoParm.addData("PARM_COUNT", 5);
        //System.out.println("DataDown_sp_E8confInfoParm:"+confInfoParm);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("DataDown_sp_E8:" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 结算信息和出院信息上传INS_ORDER
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_sp_E(TParm parm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_sp");
        confInParm.setData("PLOT_TYPE", "E");

        confInParm.addData("CONFIRM_NO", parm.getData("ADM_SEQ", 0));
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));
        confInParm.addData("SID", parm.getData("IDNO", 0));
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));
        confInParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));
//        System.out.println("诊断嘛"+parm.getValue("DIAG_CODE", 0));
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //次诊断截取传入
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }        
        confInParm.addData("DIAG_DESC2", diagdesc2);
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);
        confInParm.addData("DECREASE_RATE",
                           parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("DECREASE_RATE", 0) / 100);
        confInParm.addData("REALOWN_RATE",
                           parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("REALOWN_RATE", 0) / 100);
        confInParm.addData("INSOWN_RATE",
                           parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("INSOWN_RATE", 0) / 100);
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));
//        System.out.println("时间》》》》》》》》》》》》》》》" + parm.getValue("YEAR_MON", 0));
        confInParm.addData("ISSUE", parm.getValue("YEAR_MON", 0).substring(0, 6));
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));
        //补助金额
        confInParm.addData("ARMYAI_AMT", parm.getData("ARMYAI_AMT", 0));
        //社区编码
        confInParm.addData("COMU_NO", "");
        confInParm.addData("DR_CODE", parm.getData("DRQUALIFYCODE"));
        //补助金额2
        confInParm.addData("PUBMANAI_AMT", parm.getData("TOT_PUBMANADD_AMT", 0));
        //其它出院诊断
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        confInParm.addData("PARM_COUNT", 61);
//       System.out.println("DataDown_sp_E接口入参======"+confInParm);
        result = InsManager.getInstance().safe(confInParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 撤销申报(城居)
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_I(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_czys");
        confInfoParm.setData("PLOT_TYPE", "I");

        confInfoParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("HOSP_NHI_CODE", this.nhi_hosp_code);
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询资格确认书审核情况
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_D(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_czys");
        confInfoParm.setData("PLOT_TYPE", "D");

        confInfoParm.addData("CONFIRM_NO", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("HOSP_NHI_NO", this.nhi_hosp_code);
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 单病种结算信息和出院信息上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_H1(TParm parm, TParm dataParm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_czys");
        confInParm.setData("PLOT_TYPE", "H1");
        //就医顺序
        confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));
        //资格确认书来
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));
        //身份证号
        confInParm.addData("SID", parm.getData("IDNO", 0));
        //医院编码
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));
        //医院所属分中心
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));
        //人员类别
        confInParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
        //就医类别
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));
        //入院时间
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));
        //出院时间
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));
        //出院诊断
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        //出院诊断名称
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //出院诊断描述
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }
        confInParm.addData("DIAG_DESC2", diagdesc2);
        //出院清空
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));
        //自负比例
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);
        //减负比例
        confInParm.addData("DECREASE_RATE", parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("DECREASE_RATE", 0) / 100);
        //实际自负比例
        confInParm.addData("REALOWN_RATE", parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("REALOWN_RATE", 0) / 100);
        //医疗救助自负比例
        confInParm.addData("INSOWN_RATE", parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("INSOWN_RATE", 0) / 100);
        
        //住院号
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        //住院病区
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));
        //住院床位
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));
        //住院科别
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));
        //基本医疗剩余额
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));
        //医疗救助额
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));
        //实际起付标准
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));
        //期号
        confInParm.addData("ISSUE", parm.getData("YEAR_MON", 0));
        //药品发生额
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));
        //药品申报额
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));
        //检查费发生额
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));
        //检查费申报额
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));
        //治疗费发生额
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));
        //治疗费申报额
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));
        //手术费发生额
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));
        //手术费申报额
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));
        //床位费发生额
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));
        //床位费申报额
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));
        //医用材料发生金额
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));
        //医用材料申报金额
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));
        //其他发生额
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));
        //其他申报额
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));
        //输全血发生额
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));
        //输全血申报额
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));
        //成分输血发生额
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));
        //成分输血申报额
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));
        //本次实收起付标准金额
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));
        //病种申报金额
        confInParm.addData("NHI_OWN_AMT", dataParm.getData("SINGLE_NHI_AMT", 0));
        //医院超病种标准自负金额
        confInParm.addData("EXT_OWN_AMT",dataParm.getData("SINGLE_STANDARD_OWN_AMT", 0));
        //基本医疗保险补足金额
        confInParm.addData("COMP_AMT", dataParm.getData("SINGLE_SUPPLYING_AMT", 0));
        //自费项目金额
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        //增付项目金额
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        //起付标准以上自负比例金额
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));
        //医疗救助个人按比例负担金额
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));
        //医疗救助最高限额以上金额
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));
        //输血自负金额
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));
        //基本医疗社保申请金额
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));
        //医疗救助社保申请金额
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));
        //住院科别代码
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        //化验说明
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));
        //就医项目
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));
        //门特类别
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));
        //社区编码
        confInParm.addData("COMU_NO", ""); //固定空值
        //单病种编码
        confInParm.addData("SIN_DISEASE_CODE", parm.getData("SDISEASE_CODE", 0));
        //医师编码
        confInParm.addData("DR_CODE", parm.getData("LCS_NO", 0));
        //补助金额1
        double armyaiAmt = parm.getDouble("ARMYAI_AMT",0);
        //补助金额2
        double pubmanaiAmt = parm.getDouble("PUBMANAI_AMT",0);
        double agentAmt = armyaiAmt + pubmanaiAmt;
        //补助金额
        confInParm.addData("AGENT_AMT", agentAmt);
        //床位费特需金额
        double bedSingleAmt = dataParm.getDouble("BED_SINGLE_AMT",0);
        //医用材料费特需金额
        double materialSingleAmt = dataParm.getDouble("MATERIAL_SINGLE_AMT",0);
        double specNeedAmt = bedSingleAmt + materialSingleAmt;
        //System.out.println("specNeedAmt:"+specNeedAmt);
        //特需项目金额
        confInParm.addData("SPEC_NEED_AMT", specNeedAmt);
        //城乡大病救助
        confInParm.addData("ILLNESS_SUBSIDY_AMT", parm.getData("ILLNESS_SUBSIDY_AMT", 0));
        //其它出院诊断
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0)); 
        //入参个数
        confInParm.addData("PARM_COUNT", 66);
        //System.out.println("DataDown_czys_H1confInParm:"+confInParm);
        result = InsManager.getInstance().safe(confInParm);
        //System.out.println("DataDown_czys_H1:" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 住院上传费用明细
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_E(TParm parm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        //上传明细INS_ORDER
        int count = parm.getCount("ADM_SEQ");

        for (int m = 0; m < count; m++) {
            //就医顺序号
            confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", m));
            //医保专用票据号
//            confInParm.addData("INVNO", invNo);
            confInParm.addData("INVNO", invNo);
            //医院编码
            confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", m));
            //收费项目编码
            confInParm.addData("NHI_ORDER_CODE",
                               parm.getData("NHI_ORDER_CODE", m));
            //医院服务项目名称
            confInParm.addData("ORDER_DESC", parm.getData("ORDER_DESC", m));
            //自负比例
            confInParm.addData("OWN_RATE",
                               parm.getDouble("OWN_RATE", m) == 0 ? 0.00 :
                               parm.getDouble("OWN_RATE", m) );
            //剂型
            confInParm.addData("JX", parm.getData("JX", m));
            //规格
            confInParm.addData("GG", parm.getData("GG", m));
            //单价
            confInParm.addData("PRICE", parm.getData("PRICE", m));
            //数量
            confInParm.addData("QTY", parm.getData("QTY", m));
            //发生金额
            confInParm.addData("TOTAL_AMT", parm.getData("TOTAL_AMT", m));
            //申报金额
            confInParm.addData("TOTAL_NHI_AMT", parm.getData("TOTAL_NHI_AMT", m));
            //全自费金额
            confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", m));
            //增付金额
            confInParm.addData("ADDPAY_AMT", parm.getData("ADDPAY_AMT", m));
            //手术费用标志
            confInParm.addData("OP_FLG", parm.getData("OP_FLG", m).equals("Y")?"1":"0");
            //累计增付标志
            confInParm.addData("ADDPAY_FLG", parm.getData("ADDPAY_FLG", m).equals("Y")?"1":"0");
            //统计代码
            confInParm.addData("NHI_ORD_CLASS_CODE",
                               parm.getData("NHI_ORD_CLASS_CODE", m));
            //增负药品标志
            confInParm.addData("PHAADD_FLG", parm.getData("PHAADD_FLG", m).equals("Y")?"1":"0");
            //出院带药标志
//            System.out.println("城居出院带药注记传入前"+parm.getData("CARRY_FLG", m));
            confInParm.addData("CARRY_FLG", parm.getData("CARRY_FLG", m).equals("Y")?"1":"0");
//            System.out.println("城居出院带药注记传入前后"+confInParm.getValue("CARRY_FLG",m));
            //批准文号
            confInParm.addData("PZWH", parm.getData("PZWH", m));
            //序号
            confInParm.addData("SEQ_NO", parm.getData("SEQ_NO", m));
            String chargeDateF = parm.getValue("CHARGE_DATE", m);
            String chargeDateE = chargeDateF.substring(0, 4) + "-" +
                                 chargeDateF.substring(4, 6) + "-" +
                                 chargeDateF.substring(6, 8) +
                                 " " + chargeDateF.substring(8, 10) + ":" +
                                 chargeDateF.substring(10, 12) + ":" +
                                 chargeDateF.substring(12, 14);
            //明细输入时间
            confInParm.addData("CHARGE_DATE", chargeDateE);
            //新就医顺序号
            confInParm.addData("NEWADM_SEQ", parm.getData("NEWADM_SEQ", m));
            //医院所属分中心
            confInParm.addData("INSBRANCH_CODE",
                               parm.getData("INSBRANCH_CODE", m));
            //入参数量
            confInParm.addData("PARM_COUNT", 24);
        }
        confInParm.setData("PIPELINE", "DataUpload");
        confInParm.setData("PLOT_TYPE", "E");
        result = InsManager.getInstance().safe(confInParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 结算信息和出院信息上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_H(TParm parm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_czys");
        confInParm.setData("PLOT_TYPE", "H");

        confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));
        confInParm.addData("SID", parm.getData("IDNO", 0));
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));
        //中心医保身份
//        TParm ctzParm = INSUpLoadTool.getInstance().getNhiCtzCode(parm.getValue(
//                "CTZ1_CODE", 0));
//        String nhiCtzCode = ctzParm.getValue("NHI_NO", 0);
//        confInParm.addData("CTZ1_CODE", nhiCtzCode);
        confInParm.addData("CTZ1_CODE", parm.getValue(
                "CTZ1_CODE", 0));
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));
//        confInParm.addData("DIAG_CODE", parm.getData("DIAG_CODE", 0));
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
       //次诊断截取传入
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }       
        confInParm.addData("DIAG_DESC2", diagdesc2);
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);
        confInParm.addData("DECREASE_RATE",
                           parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("DECREASE_RATE", 0) / 100);
        confInParm.addData("REALOWN_RATE",
                           parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("REALOWN_RATE", 0) / 100);
        confInParm.addData("INSOWN_RATE",
                           parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("INSOWN_RATE", 0) / 100);
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));
//        System.out.println("ISSUE》》》》》》》》》》》》" +
//                           parm.getValue("YEAR_MON", 0).substring(0, 6));
        confInParm.addData("ISSUE", parm.getValue("YEAR_MON", 0).substring(0, 6));
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));

        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));
        confInParm.addData("BEARING_OPERATIONS_TYPE",
                           parm.getData("BEARING_OPERATIONS_TYPE", 0));
        confInParm.addData("SOAR_CODE", "");
        confInParm.addData("DR_QUALIFY_CODE", parm.getData("LCS_NO", 0));
        //补助金额
        confInParm.addData("AGENT_AMT", parm.getData("ARMYAI_AMT", 0));
        //生育方式
        confInParm.addData("BIRTH_TYPE", "");
        //分娩胎儿数量
        confInParm.addData("BABY_NO", 0);
        //城乡大病救助
        confInParm.addData("ILLNESS_SUBSIDY_AMT", parm.getData("ILLNESS_SUBSIDY_AMT", 0));
        //其它出院诊断
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0)); 
        confInParm.addData("PARM_COUNT", 64);
        result = InsManager.getInstance().safe(confInParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 病案首页上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_G(TParm parm,String type) {
        DecimalFormat df = new DecimalFormat("##########0.00");
    	 TParm result = new TParm();
         TParm mroParm = new TParm();
         if(type.equals("CZ"))
         mroParm.setData("PIPELINE", "DataDown_zjks");
         else if(type.equals("CJ"))
         mroParm.setData("PIPELINE", "DataDown_cjks");	 
         mroParm.setData("PLOT_TYPE", "G");
         mroParm.addData("HOSP_DESC", this.nhi_hosp_desc);//医院名称
         mroParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//医院代码
         mroParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//就医顺序号
         mroParm.addData("PAY_WAY", parm.getValue("PAY_WAY", 0).length()>0?
        		 parm.getValue("PAY_WAY", 0):"9");//医疗付费方式
         mroParm.addData("CARD_NO", parm.getData("CARD_NO", 0));//健康卡号
         mroParm.addData("IN_TIMES", parm.getData("IN_TIMES", 0));//年住院次数
         mroParm.addData("MR_NO", parm.getData("MR_NO", 0));//病案号
         mroParm.addData("PAT_NAME", parm.getData("PAT_NAME", 0));//姓名
         mroParm.addData("SEX", parm.getData("SEX", 0));//性别
         mroParm.addData("BIRTH_DATE", parm.getData("BIRTH_DATE", 0));//出生日期
         //获得年龄
        String age = parm.getValue("AGE", 0);
        String age1 ="";
        String age2 ="";
        String age3 ="";
         int ageflg = Integer.valueOf(age.substring(0,age.indexOf("岁")));
        if(ageflg>=1)
        	age1 = age.substring(0,age.indexOf("岁"));
        else if(ageflg<1){
        	if(age.length()>3){
        	age1 = "0";
        	age2 = age.substring(age.indexOf("岁")+1,age.indexOf("月"));
        	age3 = age.substring(age.indexOf("月")+1,age.indexOf("日"));
        	}
        	else
        	age1 = "0";	
        }
//        System.out.println("age1====:"+age1);
//        System.out.println("age2====:"+age2);
//        System.out.println("age3====:"+age3);
         mroParm.addData("AGE1", age1.length()>0?age1:"0");//年龄1
         mroParm.addData("NATION", parm.getData("NATION", 0));//国籍
         mroParm.addData("AGE2", age2.length()>0?age2:"0");//年龄2(月)
         mroParm.addData("AGE3", age3.length()>0?age3:"0");//年龄2(日)
         mroParm.addData("NB_WEIGHT", parm.getValue("NB_WEIGHT", 0).length()>0?
        		 parm.getValue("NB_WEIGHT", 0):"0");//新生儿出生体重
         mroParm.addData("NB_IN_WEIGHT", parm.getValue("NB_IN_WEIGHT", 0).length()>0?
        		 parm.getValue("NB_IN_WEIGHT", 0):"0");//新生儿入院体重
         mroParm.addData("BIRTH_ADDRESS", parm.getData("BIRTH_ADDRESS", 0));//出生地
         mroParm.addData("BIRTHPLACE", parm.getData("BIRTHPLACE", 0));//籍贯
         mroParm.addData("FOLK", parm.getData("FOLK", 0));//民族
         mroParm.addData("ID_NO", parm.getData("ID_NO", 0));//身份证号
         mroParm.addData("OCCUPATION", parm.getData("OCCUPATION", 0));//职业
         mroParm.addData("MARRIGE", parm.getValue("MARRIGE", 0).length()>0?
        		 parm.getValue("MARRIGE", 0):"9");//婚姻状况
         mroParm.addData("ADDRESS", parm.getData("ADDRESS", 0));//现住址
         mroParm.addData("ADDRESS_TEL", parm.getData("ADDRESS_TEL", 0));//现住址电话
         mroParm.addData("POST_NO", parm.getData("POST_NO", 0));//现住址邮编
         mroParm.addData("H_ADDRESS", parm.getData("H_ADDRESS", 0));//户口地址
         mroParm.addData("POST_CODE", parm.getData("POST_CODE", 0));//户口所在地邮编
         mroParm.addData("O_ADDRESS", parm.getData("O_ADDRESS", 0));//工作单位及地址
         mroParm.addData("O_TEL", parm.getData("O_TEL", 0));//工作单位电话
         mroParm.addData("O_POSTNO", parm.getData("O_POSTNO", 0));//单位邮编
         mroParm.addData("CONTACTER", parm.getData("CONTACTER", 0));//联系人姓名
         mroParm.addData("RELATIONSHIP", parm.getData("RELATIONSHIP", 0));//与患者关系
         mroParm.addData("CONT_ADDRESS", parm.getData("CONT_ADDRESS", 0));//联系人地址
         mroParm.addData("CONT_TEL", parm.getData("CONT_TEL", 0));//联系人电话
         mroParm.addData("ADM_SOURCE", parm.getValue("ADM_SOURCE", 0).length()>0?
        		 parm.getValue("ADM_SOURCE", 0):"9");//入院途径
         mroParm.addData("IN_DATE", parm.getData("IN_DATE", 0));//入院时间
         mroParm.addData("IN_DEPT", parm.getData("IN_DEPT", 0));//入院科别
         mroParm.addData("IN_STATION", parm.getData("IN_STATION", 0));//入院病房
         mroParm.addData("TRANS_DEPT", parm.getData("TRANS_DEPT", 0));//转科科别
         mroParm.addData("OUT_DATE", parm.getData("OUT_DATE", 0));//出院时间
         mroParm.addData("OUT_DEPT", parm.getData("OUT_DEPT", 0));//出院科别
         mroParm.addData("OUT_STATION", parm.getData("OUT_STATION", 0));//出院病房
         mroParm.addData("REAL_STAY_DAYS", parm.getData("REAL_STAY_DAYS", 0));//实际住院天数
         mroParm.addData("OE_DIAG_DESC", parm.getData("OE_DIAG_DESC", 0));//门（急）诊诊断
         mroParm.addData("OE_DIAG_CODE", parm.getData("OE_DIAG_CODE", 0));//门（急）诊疾病编码
         mroParm.addData("OUT_DIAG_MAIN", parm.getData("OUT_DIAG_MAIN", 0));//出院主要诊断
         mroParm.addData("OUT_DIAG_OTHER", parm.getData("OUT_DIAG_OTHER", 0));//出院其它诊断
         mroParm.addData("EX_RSN_DESC", parm.getData("EX_RSN_DESC", 0));//损伤、中毒的外部原因
         mroParm.addData("EX_RSN_CODE", parm.getData("EX_RSN_CODE", 0));//损伤、中毒的疾病编码
         mroParm.addData("PATHOLOGY_DIAG", parm.getData("PATHOLOGY_DIAG", 0));//病理诊断
         mroParm.addData("PATHOLOGY_DIAG_CODE", parm.getData("PATHOLOGY_DIAG_CODE", 0));//病理诊断疾病编码
         mroParm.addData("PATHOLOGY_NO", parm.getData("PATHOLOGY_NO", 0));//病理号
         mroParm.addData("ALLEGIC_FLG", parm.getValue("ALLEGIC_FLG", 0).length()>0?
        		 parm.getValue("ALLEGIC_FLG", 0):"1");//药物过敏标志
         mroParm.addData("ALLEGIC", parm.getData("ALLEGIC", 0));//过敏药物
         mroParm.addData("BODY_CHECK", parm.getValue("BODY_CHECK", 0).length()>0?
        		 parm.getValue("BODY_CHECK", 0):"1");//死亡患者尸检标志
         mroParm.addData("BLOOD_TYPE", parm.getValue("BLOOD_TYPE", 0).length()>0?
        		 parm.getValue("BLOOD_TYPE", 0):"6");//血型
         mroParm.addData("RH_TYPE", parm.getValue("RH_TYPE", 0).length()>0?
        		 parm.getValue("RH_TYPE", 0):"4");//RH
         mroParm.addData("DIRECTOR_DR_CODE", parm.getData("DIRECTOR_DR_CODE", 0));//科主任
         mroParm.addData("PROF_DR_CODE", parm.getData("PROF_DR_CODE", 0));//主任（副主任）医师
         mroParm.addData("ATTEND_DR_CODE", parm.getData("ATTEND_DR_CODE", 0));//主治医师
         mroParm.addData("VS_DR_CODE", parm.getData("VS_DR_CODE", 0));//住院医师
         mroParm.addData("VS_NURSE_CODE", parm.getData("VS_NURSE_CODE", 0));//责任护士
         mroParm.addData("INDUCATION_DR_CODE", parm.getData("INDUCATION_DR_CODE", 0));//进修医师
         mroParm.addData("INTERN_DR_CODE", parm.getData("INTERN_DR_CODE", 0));//实习医师
         mroParm.addData("ENCODER", parm.getData("ENCODER", 0));//编码员
         mroParm.addData("QUALITY", parm.getData("QUALITY", 0));//病案质量
         mroParm.addData("CTRL_DR", parm.getData("CTRL_DR", 0));//质控医师
         mroParm.addData("CTRL_NURSE", parm.getData("CTRL_NURSE", 0));//质控护士
         mroParm.addData("CTRL_DATE", parm.getData("CTRL_DATE", 0));//质控日期
         mroParm.addData("OUT_TYPE", parm.getValue("OUT_TYPE", 0).length()>0?
        		 parm.getValue("OUT_TYPE", 0):"9");//离院方式
         mroParm.addData("TRAN_HOSP", parm.getData("TRAN_HOSP", 0));//拟接收医疗机构名称
         mroParm.addData("AGN_PLAN_FLG", parm.getValue("AGN_PLAN_FLG", 0).length()>0?
        		 parm.getValue("AGN_PLAN_FLG", 0):"1");//出院31天内再住院
         mroParm.addData("AGN_INTENTION", parm.getData("AGN_PLAN_INTENTION", 0));//再住院目的
         //颅脑损伤患者昏迷入院前时间
         String becomatime = parm.getValue("BE_COMA_TIME", 0).length()>0? 
        		             parm.getValue("BE_COMA_TIME", 0):"000000";
         becomatime = becomatime.substring(0, 2)+"@"+
                      becomatime.substring(2, 4)+"@"+
                      becomatime.substring(4, 6);
         //颅脑损伤患者昏迷入院后时间
         String afcomatime = parm.getValue("AF_COMA_TIME", 0).length()>0? 
	                         parm.getValue("AF_COMA_TIME", 0):"000000";;
         afcomatime = afcomatime.substring(0, 2)+"@"+
                      afcomatime.substring(2, 4)+"@"+
                      afcomatime.substring(4, 6);
         System.out.println("afcomatime:"+afcomatime);
         mroParm.addData("BE_COMA_TIME", becomatime);//颅脑损伤患者昏迷入院前时间
         mroParm.addData("AF_COMA_TIME", afcomatime);//颅脑损伤患者昏迷入院后时间
         mroParm.addData("SUM_TOT", parm.getData("SUM_TOT", 0));//住院总金额
         mroParm.addData("OWN_TOT", parm.getData("OWN_TOT", 0));//住院自付金额
         mroParm.addData("CHARGE_01", parm.getData("CHARGE_01", 0));//一般医疗服务费
         mroParm.addData("CHARGE_02", parm.getData("CHARGE_02", 0));//一般治疗操作费
         mroParm.addData("CHARGE_03", parm.getData("CHARGE_03", 0));//护理费
         mroParm.addData("CHARGE_04", parm.getData("CHARGE_04", 0));//综合医疗其他费用
         mroParm.addData("CHARGE_05", parm.getData("CHARGE_05", 0));//病理诊断费
         mroParm.addData("CHARGE_06", parm.getData("CHARGE_06", 0));//实验室诊断费
         mroParm.addData("CHARGE_07", parm.getData("CHARGE_07", 0));//影像学诊断费
         mroParm.addData("CHARGE_08", parm.getData("CHARGE_08", 0));//临床诊断项目费
         
         //计算非手术治疗项目费
         double charge09 = parm.getDouble("CHARGE_09",0);//临床物理治疗费
         double charge10 = parm.getDouble("CHARGE_10",0);//非临床物理治疗费    
         mroParm.addData("CHARGE_09", parm.getData("CHARGE_09", 0));//临床物理治疗费
         mroParm.addData("CHARGE_10", df.format(charge09+charge10));//非手术治疗项目费
       
         //计算手术治疗费
         double charge11 = parm.getDouble("CHARGE_11",0);//麻醉费
         double charge12 = parm.getDouble("CHARGE_12",0);//手术费
         double charge13 = parm.getDouble("CHARGE_13",0);//手术治疗费其他        
         mroParm.addData("CHARGE_13", df.format(charge11+charge12+charge13));//手术治疗费
         mroParm.addData("CHARGE_11", parm.getData("CHARGE_11", 0));//麻醉费
         mroParm.addData("CHARGE_12", parm.getData("CHARGE_12", 0));//手术费
         mroParm.addData("CHARGE_14", parm.getData("CHARGE_14", 0));//康复费
         mroParm.addData("CHARGE_15", parm.getData("CHARGE_15", 0));//中医治疗费
         //计算西药费
         double charge16 = parm.getDouble("CHARGE_16",0);
         double charge17 = parm.getDouble("CHARGE_17",0);
         mroParm.addData("CHARGE_16_17", df.format(charge16+charge17));//西药费
         mroParm.addData("CHARGE_16", parm.getData("CHARGE_16", 0));//抗菌药物费用
         mroParm.addData("CHARGE_18", parm.getData("CHARGE_18", 0));//中成药费
         mroParm.addData("CHARGE_19", parm.getData("CHARGE_19", 0));//中草药费
         mroParm.addData("CHARGE_20", parm.getData("CHARGE_20", 0));//血费
         mroParm.addData("CHARGE_21", parm.getData("CHARGE_21", 0));//白蛋白类制品费
         mroParm.addData("CHARGE_22", parm.getData("CHARGE_22", 0));//球蛋白类制品费
         mroParm.addData("CHARGE_23", parm.getData("CHARGE_23", 0));//凝血因子类制品费
         mroParm.addData("CHARGE_24", parm.getData("CHARGE_24", 0));//细胞因子类制品费
         mroParm.addData("CHARGE_25", parm.getData("CHARGE_25", 0));//检查用一次性医用材料费
         mroParm.addData("CHARGE_26", parm.getData("CHARGE_26", 0));//治疗用一次性医用材料费
         mroParm.addData("CHARGE_27", parm.getData("CHARGE_27", 0));//手术用一次性医用材料费
         mroParm.addData("CHARGE_28", parm.getData("CHARGE_28", 0));//其他费   
         mroParm.addData("PARM_COUNT", 105);//入参数量
//         System.out.println("mroParm:"+mroParm);
         result = InsManager.getInstance().safe(mroParm);
//         System.out.println("result" + result);
         if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return result;
         }
    	 return result; 
    }
    /**
     * 住院病案首页之手术及操作上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_H(TParm parm,String type) {
    	TParm result = new TParm();
        TParm mroopParm = new TParm();        
        int count = parm.getCount("ADM_SEQ");
        if(type.equals("CZ"))
        mroopParm.setData("PIPELINE", "DataDown_zjks");
        else if(type.equals("CJ"))
        mroopParm.setData("PIPELINE", "DataDown_cjks");	
        mroopParm.setData("PLOT_TYPE", "H");
        for (int i = 0; i < count; i++) {
        	mroopParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ", i));//就医顺序号
        	mroopParm.addData("OPT_CODE", parm.getValue("OPT_CODE", i));//操作编码
        	mroopParm.addData("OP_DATE", parm.getValue("OP_DATE", i));//日期
        	mroopParm.addData("OP_LEVEL", parm.getValue("OP_LEVEL", i));//手术级别
        	mroopParm.addData("OP_NAME", parm.getValue("OP_NAME", i));//手术名称
        	mroopParm.addData("OP_DR_NAME", parm.getValue("OP_DR_NAME", i));//手术医师姓名
        	mroopParm.addData("AST_DR1", parm.getValue("AST_DR1", i));//1助姓名
        	mroopParm.addData("AST_DR2", parm.getValue("AST_DR2", i));//2助姓名
        	mroopParm.addData("HEAL_LEV", parm.getValue("HEAL_LEV", i));//切口愈合等级
        	mroopParm.addData("ANA_WAY", parm.getValue("ANA_WAY", i));//麻醉方式
        	mroopParm.addData("ANA_DR", parm.getValue("ANA_DR", i));//麻醉医师
        	mroopParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));//序号
        	mroopParm.addData("PARM_COUNT", 12);//入参数量
        }
//        System.out.println("mroopParm:"+mroopParm);
        result = InsManager.getInstance().safe(mroopParm);
//        System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
    	 return result; 
    }
}
