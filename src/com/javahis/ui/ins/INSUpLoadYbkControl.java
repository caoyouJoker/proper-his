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
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

import jdo.ins.INSTJTool;
//import jdo.mro.MRORecordTool;

/**
 * <p>Title: ҽ���걨(ҽ����)</p>
 *
 * <p>Description: ҽ���걨(ҽ����)</p>
 *
 * <p>Company: ProperSoft</p>
 */
public class INSUpLoadYbkControl extends TControl {
    private String case_no; // �����
    private String mr_no; // ��������
    String insPayType = "";
    String localFlg = "";//��ر��
    String singleFlg = "";
    String invNo = "";
    
    //ҽ��ҽԺ����
    private String nhi_hosp_code;
    //ҽԺ����
    private String nhi_hosp_desc;
    
    private TTable table;

    private int selectedCheckBoxCount = 0;

    private TParm insParm;//���ҽ����Ϣ
    

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
        this.nhi_hosp_desc = hospParm.getValue("REGION_CHN_DESC", 0);
    }

    /**
     * table����checkBox�¼�
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
	 * �������ı���س��¼�
	 */
	public void onQuery() {
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}
		
		 // modify by huangtt 20160930 EMPI���߲�����ʾ start
		 String mrNo = PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
	        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			}
	     // modify by huangtt 20160930 EMPI���߲�����ʾ end
		
		this.setValue("MR_NO", pat.getMrNo());
		TParm parm = new TParm();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MR_NO,CASE_NO FROM ADM_INP WHERE CANCEL_FLG = 'N' ");
		String temp = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
			temp = " AND  REGION_CODE='" + Operator.getRegion() + "'";
		}
		parm.setData("MR_NO", pat.getMrNo());
		sql.append(" AND MR_NO='" + pat.getMrNo() + "'" + temp);
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount()<=0) {
			this.messageBox("�˲���û��סԺ��Ϣ");
			this.setValue("MR_NO", "");
			return;
		}
		String case_No = "";
		parm.setData("FLG","Y");
		if (result.getCount("MR_NO") > 1) {
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\ins\\INSAdmNClose.x", parm);
			case_No=result.getValue("CASE_NO");
//			System.out.println("case_No:========"+case_No);
		} else {
			case_No= result.getValue("CASE_NO", 0);
//			System.out.println("case_No:;;;;========"+case_No);
		}
		//��ѯ�걨����
		onPatQuery(case_No);
	} 
    /**
     * ��ѯ�걨����
     */
    public void onPatQuery(String case_No) {
    	//�����������
    	Timestamp sysTime = SystemTool.getInstance().getDate();
    	DateFormat df1 = new SimpleDateFormat("yyyy");
    	//��ǰ���
    	String tempDate = df1.format(sysTime);
    	String indate  = ""+(Integer.parseInt(tempDate) -1) + "1231"+"235959";
        String repsql=
            " SELECT A.CASE_NO,B.NHI_CTZ_FLG,A.BILL_STATUS, A.DS_DATE "+
            " FROM ADM_INP A, SYS_CTZ B,INS_ADM_CONFIRM C"+
            " WHERE A.CASE_NO = '"+case_No+"'"+
            " AND A.CASE_NO =C.CASE_NO"+
            " AND C.IN_STATUS = '1'"+
            " AND A.CTZ1_CODE = B.CTZ_CODE"+
            " AND (A.DS_DATE IS NOT NULL OR (A.DS_DATE IS NULL  AND A.IN_DATE <= TO_DATE('"+indate+"','YYYYMMDDHH24MISS')))"+
            " AND B.NHI_CTZ_FLG ='Y'";
//        System.out.println("repsql:"+repsql);
        TParm rstParm = new TParm(TJDODBTool.getInstance().select(repsql));
//        System.out.println("�걨��rstParm:"+rstParm);
        //���ݿ��Ƿ��м�¼
        if(rstParm.getCount()>0)
        {   //����δ��Ժ���߲�����˵�
        	if(rstParm.getValue("A.DS_DATE").length()!=0)
        	{
        	  //�жϷ����Ƿ����
              if(!rstParm.getValue("BILL_STATUS",0).equals("3"))
              {
                messageBox("����δ���");
                return;
              }
        	}
        }
        else
        {
             messageBox("�޼�¼");
             return;
        }
        String caseNo = rstParm.getValue("CASE_NO",0);
//        System.out.println("�걨��caseNo:"+caseNo);
        TParm sendParm = new TParm();
      // �����
      sendParm.setData("CASE_NO", caseNo);
      String selInsIbs =
              " SELECT C.INS_CROWD_TYPE AS CTZ1_CODE, B.SDISEASE_CODE,B.LOCAL_FLG " +
              " FROM INS_IBS A,INS_ADM_CONFIRM B,SYS_CTZ C  " +
              " WHERE A.CASE_NO = '" + caseNo + "' " +
              " AND A.CASE_NO = B.CASE_NO " +
              " AND B.HIS_CTZ_CODE = C.CTZ_CODE" +
              " AND B.IN_STATUS !='5'";
      TParm selInsIbsParm = new TParm(TJDODBTool.getInstance().select(
              selInsIbs));
      if (selInsIbsParm.getErrCode() < 0) {
          this.messageBox("" + selInsIbsParm.getErrName());
          return;
      }
      if (selInsIbsParm.getCount() <= 0) {
          this.messageBox("���걨����");
          return;
      }
      //1.��ְ 2.�Ǿ�
      sendParm.setData("INS_PAT_TYPE", selInsIbsParm.getValue("CTZ1_CODE", 0));
      //��ر��
      sendParm.setData("LOCAL_FLG", selInsIbsParm.getValue("LOCAL_FLG", 0));
      //1.��ͨ 2.������
      sendParm.setData("SINGLE_TYPE",
                       selInsIbsParm.getValue("SDISEASE_CODE", 0).length() > 0 ?
                       2 : 1);
      //Ʊ�ݺ�
      sendParm.setData("INV_NO", "11111111");
      TParm parm = sendParm;
//      System.out.println("parm==========:"+parm);
      case_no = "";
      invNo ="";
      case_no = parm.getValue("CASE_NO");
      insPayType = parm.getValue("INS_PAT_TYPE");
      localFlg = parm.getValue("LOCAL_FLG");
      singleFlg = parm.getValue("SINGLE_TYPE");
      invNo = parm.getValue("INV_NO");
      parm.setData("CASE_NO", case_no);
//      System.out.println("ҽ�����====="+ this.getValue("INS_PAT_TYPE"));
//      System.out.println("���������====="+ this.getValue("SINGLE_TYPE"));
      this.setValue("INS_PAT_TYPE",insPayType);
      this.setValue("SINGLE_TYPE",singleFlg);
      parm.setData("INS_PAT_TYPE", this.getValue("INS_PAT_TYPE"));
      parm.setData("SINGLE_TYPE", this.getValue("SINGLE_TYPE"));
      TParm patParm = INSUpLoadTool.getInstance().getPatInfo(parm);
      //��table��ֵ
//      System.out.println("patParm=======" + patParm);
      this.callFunction("UI|Table|setParmValue", patParm);
      this.table = (TTable)this.getComponent("Table");
      this.table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                  "onTableComponent");
      if (this.getValueInt("INS_PAT_TYPE")==2) {
      	 callFunction("UI|readCard|setEnabled", false);
      }
    }
    /**
     * ����
     */
    public void onSave() {
        if (this.selectedCheckBoxCount == 0) {
            this.messageBox("��ѡ���걨����");
            return;
        }
        this.insPayType = this.getValueString("INS_PAT_TYPE");
        this.singleFlg = this.getValueString("SINGLE_TYPE");
//        if (this.getValueInt("INS_PAT_TYPE")==1) {
//        	  if (null==insParm || null==insParm.getValue("PERSONAL_NO") ||insParm.getValue("PERSONAL_NO").length()<=0 ) {
//      			this.messageBox("��ִ�ж�������");
//      			return;
//      		}
//		}

//        �ж��������Ƿ�ѡ��.
        if ("".equals(this.insPayType)) {
            messageBox("��ѡ��ҽ�����.");
            return;
        }
        if ("".equals(this.singleFlg)) {
            messageBox("��ѡ�񵥲������.");
            return;
        }

        TParm parm = new TParm();

        /************************�������Ҫ������Ϣ************************************/
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
//        System.out.println("������" + parm);

        String dsDateStr = StringTool.getString(parm.getTimestamp("DS_DATE"),
                                                "yyyyMMdd");
//        System.out.println("��Ժ����"+dsDateStr);
        parm.setData("DS_DATE", dsDateStr);
        //System.out.println("table��ʾ����"+parm);
        /************************************************************/

        TParm result = new TParm();
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
		// �ж��Ƿ����������� ��ý���ʱ��
		DateFormat df1 = new SimpleDateFormat("yyyy");
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String tempDate = df1.format(sysTime);//��ǰ���
		String startDate = StringTool.getString(parm.getTimestamp("IN_DATE"),
                           "yyyyMMdd");//��ʼʱ��
		//�ж��Ƿ����ڱ��ҽԺδ��Ժ������Ѫ��ҽԺסԺ�Ĳ���
		String sql3 =" SELECT TO_CHAR(IN_DATE,'yyyyMMdd') AS IN_DATE" +
	     " FROM ADM_INP" +
	     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'";		
		TParm adminpParm = new TParm(TJDODBTool.getInstance().select(sql3));
		String in_date =(String)adminpParm.getData("IN_DATE",0);
		if((Integer.parseInt(startDate)!=Integer.parseInt(in_date))&&
		   (Integer.parseInt(startDate)!=Integer.parseInt(tempDate + "0101")))
			startDate = in_date;	     
		String endDate = "";//����ʱ��
		if(localFlg.equals("Y")){
		if (Integer.parseInt(startDate) < Integer.parseInt(tempDate + "0101")) 
			endDate = ""+ (Integer.parseInt(tempDate) -1) + "1231"+"235959";
		else 
			endDate = df.format(sysTime)+"235959";
		}else{
			endDate = df.format(sysTime)+"235959";
		}
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
			messageBox("�걨����������");
			return; 
		}
		//�жϿ���ҽ���Ƿ��ϴ�������ҳ��������¼
		String date = ""+ (Integer.parseInt(tempDate) -1) + "1231"+"235959";
		String sql2 =" SELECT CASE_NO FROM ADM_INP A"+
                     " WHERE A.CASE_NO = '"+ parm.getValue("CASE_NO") + "'"+
                     " AND (A.DS_DATE IS NULL OR A.DS_DATE> TO_DATE('"+date+"','YYYYMMDDHH24MISS'))"+
                     " AND A.IN_DATE <=TO_DATE('"+date+"','YYYYMMDDHH24MISS')"+ 
                     " AND A.CANCEL_FLG = 'N'";		
        TParm flgParm = new TParm(TJDODBTool.getInstance().select(sql2));
//        System.out.println("flgParm===" + flgParm.getValue("CASE_NO"));
        if (flgParm.getErrCode() < 0) {
			return;
		}
        startDate =startDate.substring(0, 8);
        String flg ="";
        if(flgParm.getValue("CASE_NO").equals(""))
        	flg = "Y";
        else if(Integer.parseInt(startDate) == Integer.parseInt(tempDate + "0101"))
        	flg = "Y";
             else 
        	flg = "N";
        parm.setData("FLG", flg);
        if (insPayType.equals("1")||
           (insPayType.equals("2")&&localFlg.equals("N"))) {//���س�ְ ����س�ְ����سǾ�
            if (singleFlg.equals("2")) { //������
                if (!onSaveCZSingle(parm, result))
                    return;
            } else { //��ͨ
                if (!onSaveCZGeneral(parm, result))
                    return;
            }
        } else if(insPayType.equals("2")&&localFlg.equals("Y")){ //���سǾ�
            if (singleFlg.equals("2")) { //������
                if (!onSaveCJSingle(parm, result))
                    return;
            } else { //��ͨ
                if (!onSaveCJGeneral(parm, result))
                    return;
            }
        }
        this.messageBox("�걨�ɹ�!");
        this.onClear();
 
    }
    /**
     * ���
     */
    public void onClear() {
    	this.setValue("MR_NO", "");
    	this.setValue("INS_PAT_TYPE", "");
    	this.setValue("SINGLE_TYPE", "");
    	this.callFunction("UI|Table|removeRowAll");    	
    }
    /**
     * ��ְ�����ֱ���
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    public boolean onSaveCZSingle(TParm parm, TParm result) {
        //�õ���������
        TParm result1 = INSUpLoadTool.getInstance().getIBSData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }

        //��ѯ�ϴ�����
        TParm result2 = INSUpLoadTool.getInstance().getIBSUploadData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        if(localFlg.equals("Y")){
        //���������˻����
        this.DataDown_sp_E9(parm,result1);
        }       
        //����סԺ�����걨
        this.DataDown_sp_H(parm);
//        if (this.DataDown_sp_H(parm).getErrCode() < 0) {
//            return false;
//        }
        //��ѯͬ��סԺ���Ƿ����
        if (this.DataDown_sp_Q(parm).getErrCode() < 0) {
            return false;
        }
        //5.1�õ��������������2
        TParm result3 = INSUpLoadTool.getInstance().getIBSHelpAmt(parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return false;
        }
        //5.2�õ�ҽʦ֤�պ�
        TParm result4 = INSUpLoadTool.getInstance().getDrQualifyCode(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return false;
        }

        //6.1�õ��ϴ�������ҳ��Ϣ
        /**TParm result5 = INSUpLoadTool.getInstance().getMROUploadData(parm);
        if (result5.getErrCode() < 0) {
            this.messageBox(result5.getErrText());
            return false;

        }

        //6.2�õ������ַ��÷ָ��в�����ҳ������
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
        //�ż��������Ϣ
        parm.setData("IO_TYPE","I");
		TParm oeDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
		String oeDiag = "";
		for (int i = 0; i < oeDiagParm.getCount(); i++) 
		{
       	  oeDiag += (oeDiagParm.getData("ICD_CODE", i)+"_"+oeDiagParm.getData("ICD_DESC", i));
		}		
		result6.setData("OE_DIAG_CODE", 0, oeDiag);
        //��Ժ�����Ϣ
        parm.setData("IO_TYPE","M");
		TParm inDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
		String inDiag = "";
		for (int i = 0; i < inDiagParm.getCount(); i++) 
		{
       	  inDiag += (inDiagParm.getData("ICD_CODE", i)+"_"+inDiagParm.getData("ICD_DESC", i));
		}
		result6.setData("IN_DIAG_CODE", 0, inDiag);
		//��Ժ�����Ϣ
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
        //6.3������ҳ�ϴ�
        if (this.DataDown_sp_E2(result6, "1").getErrCode() < 0) {
            return false;
        }
        //7.1�õ������ַ��÷ָ��в�����ҳ֮�������ϵ�����
        TParm result7 = INSUpLoadTool.getInstance().getMROOpData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        result7.addData("ADM_SEQ", parm.getData("ADM_SEQ"));
        //7.2������ҳ֮�����������ϴ�//
//        this.DataDown_sp_E3(result7, "1");
        if (this.DataDown_sp_E3(result7, "1").getErrCode() < 0) {
            return false;
        } */
        //�õ���ְ������ҳ������
        TParm CZMRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (CZMRO.getErrCode() < 0) {
            this.messageBox(CZMRO.getErrText());
            return false;
        }
        if (CZMRO.getData("SUM_TOT", 0) == null||
            CZMRO.getData("SUM_TOT", 0).equals("")) {
          	 this.messageBox("��ҳ����δת��,����ϵ������");
               return false;
          }       
       //��Ժ�����Ϣ
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return false;
	        }
		//�����
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
		CZMRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//�����
		String secDiag = "";
        for (int i = 1; i < outDiagParm.getCount(); i++) 
        {
        	secDiag+=outDiagParm.getData("ICD_DESC", i)+"@"+
	         outDiagParm.getData("ICD_CODE", i)+"@"+
	         outDiagParm.getData("IN_PAT_CONDITION", i)+"%";
		}
        CZMRO.setData("OUT_DIAG_OTHER",0,secDiag.length()>0? 
        		secDiag.substring(0, secDiag.length() - 1):"");
        //סԺ������Ϣ�ϴ�
        if(CZMRO.getData("OUT_TYPE", 0).equals("5")){
        	TParm statusParm = INSUpLoadTool.getInstance().getStatusdata(parm);
        	if(statusParm.getData("DEATH_FLG", 0).equals("N")){
        	statusParm.setData("ICD_CODE",0,outDiagParm.getData("ICD_CODE", 0));
        	statusParm.setData("ICD_DESC",0,outDiagParm.getData("ICD_DESC", 0));
        	 if (this.DataDown_zjks_A1(statusParm,"CZ").getErrCode() < 0) {
                 return false;
             }
        }
      }
         //��ְ������ҳ����
    	this.DataUpload_G1(CZMRO,"CZ");
        //��ְ������ҳ�ϴ�
        if (this.DataUpload_G(CZMRO,"CZ").getErrCode() < 0) {       	
            return false;
        }
        //�õ���ְ����ҳ֮����������������
        TParm CZMROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (CZMROOP.getErrCode() < 0) {
            this.messageBox(CZMROOP.getErrText());
            return false;
        }
        if(CZMROOP.getCount()>0){
        //��ְסԺ������ҳ֮�����������ϴ�
        if (this.DataUpload_H(CZMROOP,"CZ").getErrCode() < 0) {
            return false;
        }
       }
        //8.�õ������ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�������Ϣ��ѯ
        TParm result8 = INSUpLoadTool.getInstance().getSingleIBSData(parm);
        if (result8.getErrCode() < 0) {
            this.messageBox(result8.getErrText());
            return false;
        }
        
        //9.�����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�
        //ҽʦ����
        result1.setData("DR_QUALIFY_CODE",result4.getData("DR_QUALIFY_CODE"));
        TParm spE1Parm = this.DataDown_sp_E1(result1,result8);
        if (spE1Parm.getErrCode() < 0) {
            return false;
        }
        String newConfirmNo = spE1Parm.getValue("NEW_CONFIRM_NO");
        result2.setData("NEW_CONFIRM_NO", newConfirmNo);
        //10.סԺ�ϴ�������ϸ
//        this.DataUpload_A(result2);
        if (this.DataUpload_A(result2).getErrCode() < 0) {
            return false;
        }
        //11.סԺ�ʻ�֧��ȷ��
        TParm spE8Parm = this.DataDown_sp_E8(parm);
        if (spE8Parm.getErrCode() < 0) {
            return false;
        }
        double accountPayAmt = spE8Parm.getDouble("ACCOUNT_PAY_AMT");
        double personAccountAmt = spE8Parm.getDouble("PERSON_ACCOUNT_AMT");
        parm.setData("ACCOUNT_PAY_AMT", accountPayAmt);
        parm.setData("PERSON_ACCOUNT_AMT", personAccountAmt);
        parm.setData("NEW_CONFIRM_NO", newConfirmNo);     
        //���¶�Ӧ���ݿ���λ
        result = TIOM_AppServer.executeAction("action.ins.INSUpLoadAction",
                                              "saveUpLoadData", parm);

        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        return true;
    }
    /**
	 * ˢ������
	 */
	public void onReadCard() {
		 if (this.selectedCheckBoxCount == 0) {
	            this.messageBox("��ѡ���걨����");
	            return;
	       }
		 table = (TTable)this.getComponent("Table");
	     TParm  tableParm = table.getParmValue().getRow(table.getSelectedRow());		
		//ҽԺ����@סԺʱ��@���		
		String inDate = StringTool.getString(tableParm.getTimestamp("IN_DATE"),
                "yyyyMMdd");//סԺʱ��
//		 System.out.println("inDate=========="+inDate);		
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		String advancecode = regionParm.getValue("NHI_NO", 0)+","+"@"+inDate+"@"+"1";
		TParm parm = new TParm();
		parm.setData("ADVANCE_CODE",advancecode);//ҽԺ����@סԺʱ��@���		
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCardOneYD.x", parm);
		if (null == insParm)
			return;
		int returnType = insParm.getInt("RETURN_TYPE");// ��ȡ״̬ 1.�ɹ� 2.ʧ��
		if (returnType == 0 || returnType == 2) {
			insParm=null;
			this.messageBox("��ȡҽ����ʧ��");
			return;
		}
		this.messageBox("�����ɹ�");
	}
    /**
     * ��ְ��ͨ����
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    public boolean onSaveCZGeneral(TParm parm, TParm result) {
        //1.�õ���������
        TParm result1 = INSUpLoadTool.getInstance().getIBSData(parm);
        if (result1.getErrCode() < 0) {
            this.messageBox(result1.getErrText());
            return false;
        }
        //2.��ѯ�ϴ�����
        TParm result2 = INSUpLoadTool.getInstance().getIBSUploadData(parm);
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText());
            return false;
        }
        //System.out.println("��ѯ�ϴ�����>>>>>>>>>>>>"+result2);
        //System.out.println("����סԺ�����걨"+parm);
        if(localFlg.equals("Y")){
        //���������˻����
        this.DataDown_sp_E9(parm,result1);
        }      
        //3.����סԺ�����걨
        this.DataDown_sp_H(parm);
        //4.��ѯͬ��סԺ���Ƿ����
//        this.DataDown_sp_Q(parm);
//        if (this.DataDown_sp_Q(parm).getErrCode() < 0) {
//            return false;
//        }
        if(parm.getValue("FLG").equals("Y")){
        //�õ���ְ������ҳ������
        TParm CZMRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (CZMRO.getErrCode() < 0) {
            this.messageBox(CZMRO.getErrText());
            return false;
        }
        if (CZMRO.getData("SUM_TOT", 0) == null||
        	CZMRO.getData("SUM_TOT", 0).equals("")){ 
       	 this.messageBox("��ҳ����δת��,����ϵ������");
            return false;
       }
       //��Ժ�����Ϣ
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return false;
	        }
		//�����
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
//		System.out.println("mainDiag>>>>>>>>>>>>"+mainDiag);
		CZMRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//�����
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
        
        //סԺ������Ϣ�ϴ�
        if(CZMRO.getData("OUT_TYPE", 0).equals("5")){
        	TParm statusParm = INSUpLoadTool.getInstance().getStatusdata(parm);
        	if(statusParm.getData("DEATH_FLG", 0).equals("N")){
        	statusParm.setData("ICD_CODE",0,outDiagParm.getData("ICD_CODE", 0));
        	statusParm.setData("ICD_DESC",0,outDiagParm.getData("ICD_DESC", 0));
        	 if (this.DataDown_zjks_A1(statusParm,"CZ").getErrCode() < 0) {
                 return false;
             }
        }
      }
        //��ְ������ҳ����
    	this.DataUpload_G1(CZMRO,"CZ");
        //��ְ������ҳ�ϴ�
        if (this.DataUpload_G(CZMRO,"CZ").getErrCode() < 0) {
            return false;
        }
        //�õ���ְ����ҳ֮����������������
        TParm CZMROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (CZMROOP.getErrCode() < 0) {
            this.messageBox(CZMROOP.getErrText());
            return false;
        }
        if(CZMROOP.getCount()>0){
        //��ְסԺ������ҳ֮�����������ϴ�
        if (this.DataUpload_H(CZMROOP,"CZ").getErrCode() < 0) {
            return false;
        }
      }
    }
        //5.1�õ��������������2
        TParm result3 = INSUpLoadTool.getInstance().getIBSHelpAmt(parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return false;
        }
        //5.2�õ�ҽʦ֤�պ�
        TParm result4 = INSUpLoadTool.getInstance().getDrQualifyCode(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return false;
        }
        result1.setData("DRQUALIFYCODE", result4.getData("DRQUALIFYCODE", 0));
        //6.������Ϣ�ͳ�Ժ��Ϣ�ϴ�
        result1.addData("ARMYAI_AMT", result3.getData("ARMYAI_AMT", 0));
        result1.addData("TOT_PUBMANADD_AMT",
                        result3.getData("TOT_PUBMANADD_AMT", 0));
//        System.out.println("�ϴ����"+result1);
        TParm upParm = this.DataDown_sp_E(result1);
        if (upParm.getErrCode() < 0) {
            return false;
        }
//        System.out.println("�ϴ�����"+upParm);
        TParm spE8Parm = new TParm();
        if(result1.getData("INS_CROWD_TYPE", 0).equals("3")){
        //8.סԺ�ʻ�֧��ȷ��(���ҽ��ʹ��)
        spE8Parm = this.DataDown_sp_E8(parm);
        if (spE8Parm.getErrCode() < 0) {
            return false;
        } 
       }
        result2.setData("NEW_CONFIRM_NO", upParm.getValue("NEW_CONFIRM_NO"));
        //7.סԺ�ϴ�������ϸ
//        System.out.println("�ϴ���ϸ���"+result2);
//        this.DataUpload_A(result2);
        if (this.DataUpload_A(result2).getErrCode() < 0) {
            return false;
        }
//        System.out.println("�ϴ���ϸ�ɹ�");
        if(result1.getData("INS_CROWD_TYPE", 0).equals("1")){
        //8.סԺ�ʻ�֧��ȷ��
         spE8Parm = this.DataDown_sp_E8(parm);
        if (spE8Parm.getErrCode() < 0) {
            return false;
        }
       }
//        System.out.println("spE8Parm========="+spE8Parm);
        double accountPayAmt = spE8Parm.getDouble("ACCOUNT_PAY_AMT");
        double personAccountAmt = spE8Parm.getDouble("PERSON_ACCOUNT_AMT");
        parm.setData("ACCOUNT_PAY_AMT", accountPayAmt);
        parm.setData("PERSON_ACCOUNT_AMT", personAccountAmt);
        parm.setData("NEW_CONFIRM_NO", upParm.getValue("NEW_CONFIRM_NO"));
//        //System.out.println("���¶�Ӧ���ݿ���λ����������������������>>"+parm);
        //���¶�Ӧ���ݿ���λ
        result = TIOM_AppServer.executeAction("action.ins.INSUpLoadAction",
                                              "saveUpLoadData", parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * �Ǿӵ����ֱ���
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    public boolean onSaveCJSingle(TParm parm, TParm result) {
    	//System.out.println("onSaveCJSingle()");
        //1.�����걨
        //(1)	����ҽ���ӿں���(�����걨)
        this.DataDown_czys_I(parm);
        
        //(2),(3)�����걨����
        TParm result0 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAppData", parm);
        if (result0.getErrCode() < 0) {
            this.messageBox(result0.getErrText());
            return false;
        }
        //2.��ѯ�ʸ�ȷ����������
        //(1)��ѯ�ʸ�ȷ����������
        TParm czysDParm = this.DataDown_czys_D(parm);
        if (czysDParm.getErrCode() < 0) {
            return false;
        }
//        System.out.println("(1)��ѯ�ʸ�ȷ����������"+czysDParm);
        if (!czysDParm.getBoolean("ALLOW_FLG_FLG"))
            return false;

//        if (this.DataDown_czys_D(parm).getErrCode() < 0) {
//            return false;
//        }
//        //(2)	�õ�ҽ��״̬,(3)	����ʸ�ȷ����
//        TParm result1 = TIOM_AppServer.executeAction(
//                "action.ins.INSUpLoadAction",
//                "checkConfirmData", parm);
//        if (result1.getErrCode() < 0) {
//            this.messageBox(result1.getErrText());
//            return false;
//        }
        //3.������ҳ�ϴ�
        //(1).����ϴ�������ҳʱ��ѯ�����ϴ���Ϣ
        /**TParm result2 = INSUpLoadTool.getInstance().getMROUploadData(parm);
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText());
            return false;
        }

        //(2)��ʼ�������ַ��÷ָ��в�����ҳ������
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

        //�ż��������Ϣ
        parm.setData("IO_TYPE","I");
		TParm oeDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
		String oeDiag = "";
		for (int i = 0; i < oeDiagParm.getCount(); i++) 
		{
       	  oeDiag += (oeDiagParm.getData("ICD_CODE", i)+"_"+oeDiagParm.getData("ICD_DESC", i));
		}		
		result3.setData("OE_DIAG_CODE", 0, oeDiag);
        //��Ժ�����Ϣ
        parm.setData("IO_TYPE","M");
		TParm inDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
		String inDiag = "";
		for (int i = 0; i < inDiagParm.getCount(); i++) 
		{
       	  inDiag += (inDiagParm.getData("ICD_CODE", i)+"_"+inDiagParm.getData("ICD_DESC", i));
		}
		result3.setData("IN_DIAG_CODE", 0, inDiag);
		//��Ժ�����Ϣ
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
		
        //(3)������ҳ�ϴ�
        if (this.DataDown_sp_E2(result3, "3").getErrCode() < 0) {
            return false;
        }

        //4.���������ϴ�
        //(1).��ʼ�������ַ��÷ָ��в�����ҳ֮�������ϵ�����
        TParm result4 = INSUpLoadTool.getInstance().getMROOpData(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return false;
        }
        result4.addData("ADM_SEQ", parm.getData("ADM_SEQ"));

        //(2).������ҳ֮�����������ϴ�
//        this.DataDown_sp_E3(result4, "3");
        if (this.DataDown_sp_E3(result4, "3").getErrCode() < 0) {
            return false;
        } */
        //�õ��ǾӲ�����ҳ������
        TParm CJMRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (CJMRO.getErrCode() < 0) {
            this.messageBox(CJMRO.getErrText());
            return false;
        }
        if (CJMRO.getData("SUM_TOT", 0) == null||
        	CJMRO.getData("SUM_TOT", 0).equals("")) {
       	 this.messageBox("��ҳ����δת��,����ϵ������");
            return false;
       }
       //��Ժ�����Ϣ
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return false;
	        }
		//�����
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
		CJMRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//�����
		String secDiag = "";
        for (int i = 1; i < outDiagParm.getCount(); i++) 
        {
        	secDiag+=outDiagParm.getData("ICD_DESC", i)+"@"+
	         outDiagParm.getData("ICD_CODE", i)+"@"+
	         outDiagParm.getData("IN_PAT_CONDITION", i)+"%";
		}
        CJMRO.setData("OUT_DIAG_OTHER",0,secDiag.length()>0? 
        		secDiag.substring(0, secDiag.length() - 1):"");
        //סԺ������Ϣ�ϴ�
        if(CJMRO.getData("OUT_TYPE", 0).equals("5")){
        	TParm statusParm = INSUpLoadTool.getInstance().getStatusdata(parm);
        	if(statusParm.getData("DEATH_FLG", 0).equals("N")){
        	statusParm.setData("ICD_CODE",0,outDiagParm.getData("ICD_CODE", 0));
        	statusParm.setData("ICD_DESC",0,outDiagParm.getData("ICD_DESC", 0));
        	 if (this.DataDown_zjks_A1(statusParm,"CJ").getErrCode() < 0) {
                 return false;
             }
        }
      }
         //��ְ������ҳ����
    	this.DataUpload_G1(CJMRO,"CJ");
        //�ǾӲ�����ҳ�ϴ�
        if (this.DataUpload_G(CJMRO,"CJ").getErrCode() < 0) {      	
            return false;
        }
        //�õ��ǾӰ���ҳ֮����������������
        TParm CJMROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (CJMROOP.getErrCode() < 0) {
            this.messageBox(CJMROOP.getErrText());
            return false;
        }
        if(CJMROOP.getCount()>0){
        //�Ǿ�סԺ������ҳ֮�����������ϴ�
        if (this.DataUpload_H(CJMROOP,"CJ").getErrCode() < 0) {
            return false;
        }
       }
        //5.��Ժ��Ϣ�ϴ�
        //(1).���ҽ���걨��Ϣ
        TParm result5 = INSUpLoadTool.getInstance().getINSMedAppInfo(parm);
        if (result5.getErrCode() < 0) {
            this.messageBox(result5.getErrText());
            return false;
        }
        
        //(3).�����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ� ������Ϣ��ѯ
        TParm result6 = INSUpLoadTool.getInstance().getSingleIBSData(parm);
        if (result6.getErrCode() < 0) {
            this.messageBox(result6.getErrText());
            return false;
        }        
        
        //(2).�����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�
       //System.out.println("result6:"+result6);
       TParm  czysHParm = this.DataDown_czys_H1(result5,result6);
        if (czysHParm.getErrCode() < 0) {
            return false;
        }
        String newAdmSeq = czysHParm.getValue("NEWADM_SEQ");
        parm.setData("NEWADM_SEQ", newAdmSeq);
        //(4),(5)�����µľ���˳���
        TParm result7 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAdmSeqData", parm);
        if (result7.getErrCode() < 0) {
            this.messageBox(result7.getErrText());
            return false;
        }

        //6.��ϸ�ϴ�
        //(1)��÷����ϴ���ϸ
        TParm result8 = INSUpLoadTool.getInstance().getInsMedInfo(parm);
        if (result8.getErrCode() < 0) {
            this.messageBox(result8.getErrText());
            return false;
        }
        //(2)סԺ�ϴ�������ϸ
//        this.DataUpload_E(result8);
        if (this.DataUpload_E(result8).getErrCode() < 0) {
            return false;
        }

        //(3)������ϸ�ϴ���д(4)������ϸ�ϴ���дINS_ADM_CONFIRM  //
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
     * �Ǿ���ͨ����
     * @param parm TParm
     * @param result TParm
     * @return boolean
     */
    public boolean onSaveCJGeneral(TParm parm, TParm result) {
        //1.�����걨
        //(1).�����걨
        this.DataDown_czys_I(parm);
//        System.out.println("(1).�����걨");
        //(2),(3)�����걨����
        TParm result0 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAppData", parm);
//        System.out.println("(2),(3)�����걨����");
        if (result0.getErrCode() < 0) {
            this.messageBox(result0.getErrText());
            return false;
        }
        //2.��ѯ�ʸ�ȷ����������
        //(1)��ѯ�ʸ�ȷ����������
        TParm czysDParm = this.DataDown_czys_D(parm);
        if (czysDParm.getErrCode() < 0) {
            return false;
        }
//        System.out.println("(1)��ѯ�ʸ�ȷ����������"+czysDParm);
        if (!czysDParm.getBoolean("ALLOW_FLG_FLG"))
            return false;
//        System.out.println("����ʸ�ȷ�������"+parm);
        ///(2)	�õ�ҽ��״̬,(3)	����ʸ�ȷ����
        //========pangben 2012-8-16 ȡ��INS_ADM_CONFIRM IN_STATUS=7״̬
//      TParm result1 = TIOM_AppServer.executeAction(
//              "action.ins.INSUpLoadAction",
//              "checkConfirmData", parm);
////      System.out.println("(2)	�õ�ҽ��״̬,(3)	����ʸ�ȷ����");
//      if (result1.getErrCode() < 0) {
//          this.messageBox(result1.getErrText());
//          return false;
//      }
      //========pangben 2012-8-16 stop
        if(parm.getValue("FLG").equals("Y")){ 
        //�õ��ǾӲ�����ҳ������
        TParm CJMRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (CJMRO.getErrCode() < 0) {
            this.messageBox(CJMRO.getErrText());
            return false;
        }
        if (CJMRO.getData("SUM_TOT", 0) == null||
            CJMRO.getData("SUM_TOT", 0).equals(""))  {
        	 this.messageBox("��ҳ����δת��,����ϵ������");
             return false;
        }
       //��Ժ�����Ϣ
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return false;
	        }
		//�����
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
//		System.out.println("mainDiag>>>>>>>>>>>>"+mainDiag);
		CJMRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//�����
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
        //סԺ������Ϣ�ϴ�
        if(CJMRO.getData("OUT_TYPE", 0).equals("5")){
        	TParm statusParm = INSUpLoadTool.getInstance().getStatusdata(parm);
        	if(statusParm.getData("DEATH_FLG", 0).equals("N")){
        	statusParm.setData("ICD_CODE",0,outDiagParm.getData("ICD_CODE", 0));
        	statusParm.setData("ICD_DESC",0,outDiagParm.getData("ICD_DESC", 0));
        	 if (this.DataDown_zjks_A1(statusParm,"CJ").getErrCode() < 0) {
                 return false;
             }
           }
        }
        //��ְ������ҳ����
    	this.DataUpload_G1(CJMRO,"CJ");
        //�ǾӲ�����ҳ�ϴ�
        if (this.DataUpload_G(CJMRO,"CJ").getErrCode() < 0) {       	
            return false;
        }
        //�õ��ǾӰ���ҳ֮����������������
        TParm CJMROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (CJMROOP.getErrCode() < 0) {
            this.messageBox(CJMROOP.getErrText());
            return false;
        }
        if(CJMROOP.getCount()>0){
        //�Ǿ�סԺ������ҳ֮�����������ϴ�
        if (this.DataUpload_H(CJMROOP,"CJ").getErrCode() < 0) {
            return false;
        }
    }
  }
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("DS_DATE", parm.getData("DS_DATE"));
        //System.out.println("���ҽ���걨��Ϣ���"+parm);
        //3.��Ժ��Ϣ�ϴ�
        //(1).���ҽ���걨��Ϣ
        TParm result2 = INSUpLoadTool.getInstance().getINSMedAppInfo(parm);
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText());
            return false;
        }
        //System.out.println("������Ϣ�ͳ�Ժ��Ϣ�ϴ��������������"+result2);
        //(2).������Ϣ�ͳ�Ժ��Ϣ�ϴ�
        TParm czysHParm = this.DataDown_czys_H(result2);
        if (czysHParm.getErrCode() < 0) {
            return false;
        }
        String newAdmSeq = czysHParm.getValue("NEWADM_SEQ");
        parm.setData("NEWADM_SEQ", newAdmSeq);
        //(3),(4)�����µľ���˳���
        TParm result3 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAdmSeqData", parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return false;
        }
        //System.out.println("��÷����ϴ���ϸ<<<<<<<<<<���"+parm);
        //6.��ϸ�ϴ�
        //(1)��÷����ϴ���ϸ
        TParm result4 = INSUpLoadTool.getInstance().getInsMedInfo(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return false;
        }
        //(2)סԺ�ϴ�������ϸ
//        this.DataUpload_E(result4);
        if (this.DataUpload_E(result4).getErrCode() < 0) {
            return false;
        }
        //(3)������ϸ�ϴ���д(4)������ϸ�ϴ���дINS_ADM_CONFIRM
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
     * ����סԺ�����걨
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
//        if (result.getErrCode() < 0) {
//            this.messageBox(result.getErrText());
//            return result;
//        }
        return result;
    }
    /**
     * ���������˻����
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_sp_E9(TParm parm,TParm parm1) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_sp");
        confInfoParm.setData("PLOT_TYPE", "E9");
        confInfoParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("ACCOUNT_PAY_AMT", parm1.getDouble(
                "ACCOUNT_PAY_AMT",0)); // �����ʻ�֧�����
        confInfoParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);
        confInfoParm.addData("PARM_COUNT", 3);
        result = InsManager.getInstance().safe(confInfoParm);
//        if (result.getErrCode() < 0) {
//            this.messageBox(result.getErrText());
//            return result;
//        }
        return result;
    }
    /**
     * ��ѯͬ��סԺ���Ƿ����
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
     * ������ҳ�ϴ�
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
        confInParm.addData("INSURANCE_TYPE", flg); //��ְסԺ 1 ;�Ǿ�סԺ 3

        confInParm.addData("PARM_COUNT", 55);
        result = InsManager.getInstance().safe(confInParm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ������ҳ֮�����������ϴ�
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
            confInfoParm.addData("INSURANCE_TYPE", flg); //��ְסԺ 1; �Ǿ�סԺ 2
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
     * �����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�
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
        //ƥ���������
        confInfoParm.addData("DIAG_CODE",INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        confInfoParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //����Ͻ�ȡ����
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
//        confInfoParm.addData("STANDARD_AMT",
//                             parm.getData("START_STANDARD_AMT", 0));
        confInfoParm.addData("STANDARD_AMT",
                parm.getData("RESTART_STANDARD_AMT", 0));       
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
        confInfoParm.addData("NHI_OWN_AMT", parm.getData("SINGLE_NHI_AMT", 0)); //�����걨���
        confInfoParm.addData("EXT_OWN_AMT",
                             parm.getData("SINGLE_STANDARD_OWN_AMT", 0)); //ҽԺ�����ֱ�׼�Ը����
        confInfoParm.addData("COMP_AMT", parm.getData("SINGLE_SUPPLYING_AMT", 0)); //����ҽ�Ʊ��ղ�����
        confInfoParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        confInfoParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        //ͳ������Ը���׼���
        confInfoParm.addData("APPLY_OWN_AMT_STD", dataParm.getData("STARTPAY_OWN_AMT", 0));
        //ҽ�ƾ����Ը���׼���
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
        //�������
        confInfoParm.addData("ARMYAI_AMT", parm.getData("ARMYAI_AMT", 0));
        //��������
        confInfoParm.addData("COMU_NO", "");
        //�����ֱ���
        confInfoParm.addData("SIN_DISEASE_CODE", dataParm.getData("SDISEASE_CODE", 0)); 
        //ҽʦ����
        confInfoParm.addData("DR_CODE", parm.getData("DR_QUALIFY_CODE", 0));
        //�������2
        confInfoParm.addData("PUBMANAI_AMT", parm.getData("PUBMANAI_AMT", 0));
        
        //�����Էѽ��
        double BED_SINGLE_AMT = dataParm.getDouble("BED_SINGLE_AMT", 0);
        double MATERIAL_SINGLE_AMT = dataParm.getDouble("MATERIAL_SINGLE_AMT", 0);
        double specNeedAmt = BED_SINGLE_AMT + MATERIAL_SINGLE_AMT;
        confInfoParm.addData("SPEC_NEED_AMT", specNeedAmt);
        //������Ժ���
        confInfoParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        //�����ϴ���ʽ
        confInfoParm.addData("SINGLE_UPLOAD_TYPE", parm.getValue("SINGLE_UPLOAD_TYPE", 0).length()>0?
          		 parm.getValue("SINGLE_UPLOAD_TYPE", 0):"");
        confInfoParm.addData("PARM_COUNT", 66);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * סԺ�ϴ�������ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_A(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        //System.out.println("�ϴ���ϸ����+++++++++++++"+parm.getCount("ADM_SEQ")+"<>"+parm);
        //�ϴ���ϸ
        int count = parm.getCount("ADM_SEQ");
        for (int m = 0; m < count; m++) {
            //System.out.println("����ѭ��"+m+parm.getRow(m));
            confInfoParm.addData("CONFIRM_NO", parm.getData("ADM_SEQ", m));
            confInfoParm.addData("NEW_CONFIRM_NO",
                                 parm.getData("NEW_CONFIRM_NO")); //TODO:?��ȷ��
            confInfoParm.addData("HOSP_CLEFT_CENTER",
                                 parm.getData("INSBRANCH_CODE", m));

//            String chargeDateF =parm.getValue("CHARGE_DATE", m);
//            String chargeDateE = chargeDateF.substring(0,4)+"-"+chargeDateF.substring(4,6)+"-"+chargeDateF.substring(6,8)+
//                                 " "+chargeDateF.substring(8,10)+":"+chargeDateF.substring(10,12)+":"+
//                                 chargeDateF.substring(12,14);
            //��ϸ����ʱ��
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
//            System.out.println("��Ժ��ҩע�Ǵ���ǰ"+parm.getValue("CARRY_FLG", m));
            confInfoParm.addData("CARRY_FLG", parm.getValue("CARRY_FLG", m).equals("Y")?"1":"0");
//            System.out.println("��Ժ��ҩע�Ǵ����"+confInfoParm.getValue("CARRY_FLG",m));
            confInfoParm.addData("PZWH", parm.getData("PZWH", m));
//            confInfoParm.addData("INVNO", invNo); //ҽ��Ʊ�ݺ�//TODO:��֪������Դ
            confInfoParm.addData("INVNO", invNo); //ҽ��Ʊ�ݺ�//TODO:��֪������Դ

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
     * סԺ�ʻ�֧��ȷ��
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
        //��ʱ����ֵ
        confInfoParm.addData("PASS_WORD", "");
        confInfoParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);
        //===============pangben 2012-4-13 ����ˢ����֤��
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
     * ������Ϣ�ͳ�Ժ��Ϣ�ϴ�INS_ORDER
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
//        System.out.println("�����"+parm.getValue("DIAG_CODE", 0));
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //����Ͻ�ȡ����
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
//        System.out.println("ʱ�䡷����������������������������" + parm.getValue("YEAR_MON", 0));
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
        //�������
        confInParm.addData("ARMYAI_AMT", parm.getData("ARMYAI_AMT", 0));
        //��������
        confInParm.addData("COMU_NO", "");
        confInParm.addData("DR_CODE", parm.getData("DRQUALIFYCODE",0));
        //�������2
        confInParm.addData("PUBMANAI_AMT", parm.getData("TOT_PUBMANADD_AMT", 0));
        //������Ժ���
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        //���ָ����˳�ԭ��
        confInParm.addData("QUIT_REMARK", parm.getValue("QUIT_REMARK", 0).length()>0?
          		 parm.getValue("QUIT_REMARK", 0):"");
        
        confInParm.addData("PARM_COUNT", 62);
//       System.out.println("DataDown_sp_E�ӿ����======"+confInParm);
        result = InsManager.getInstance().safe(confInParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����걨(�Ǿ�)
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
//        if (result.getErrCode() < 0) {
//            this.messageBox(result.getErrText());
//            return result;
//        }
        return result;
    }

    /**
     * ��ѯ�ʸ�ȷ����������
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
     * �����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_H1(TParm parm, TParm dataParm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_czys");
        confInParm.setData("PLOT_TYPE", "H1");
        //��ҽ˳��
        confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));
        //�ʸ�ȷ������
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));
        //����֤��
        confInParm.addData("SID", parm.getData("IDNO", 0));
        //ҽԺ����
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));
        //ҽԺ����������
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));
        //��Ա���
        confInParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
        //��ҽ���
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));
        //��Ժʱ��
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));
        //��Ժʱ��
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));
        //��Ժ���
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        //��Ժ�������
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //��Ժ�������
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
        //��Ժ���
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));
        //�Ը�����
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);
        //��������
        confInParm.addData("DECREASE_RATE", parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("DECREASE_RATE", 0) / 100);
        //ʵ���Ը�����
        confInParm.addData("REALOWN_RATE", parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("REALOWN_RATE", 0) / 100);
        //ҽ�ƾ����Ը�����
        confInParm.addData("INSOWN_RATE", parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("INSOWN_RATE", 0) / 100);
        
        //סԺ��
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        //סԺ����
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));
        //סԺ��λ
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));
        //סԺ�Ʊ�
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));
        //����ҽ��ʣ���
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));
        //ҽ�ƾ�����
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));
        //ʵ���𸶱�׼
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));
        //�ں�
        confInParm.addData("ISSUE", parm.getData("YEAR_MON", 0));
        //ҩƷ������
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));
        //ҩƷ�걨��
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));
        //���ѷ�����
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));
        //�����걨��
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));
        //���Ʒѷ�����
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));
        //���Ʒ��걨��
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));
        //�����ѷ�����
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));
        //�������걨��
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));
        //��λ�ѷ�����
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));
        //��λ���걨��
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));
        //ҽ�ò��Ϸ������
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));
        //ҽ�ò����걨���
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));
        //����������
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));
        //�����걨��
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));
        //��ȫѪ������
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));
        //��ȫѪ�걨��
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));
        //�ɷ���Ѫ������
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));
        //�ɷ���Ѫ�걨��
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));
        //����ʵ���𸶱�׼���
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));
        //�����걨���
        confInParm.addData("NHI_OWN_AMT", dataParm.getData("SINGLE_NHI_AMT", 0));
        //ҽԺ�����ֱ�׼�Ը����
        confInParm.addData("EXT_OWN_AMT",dataParm.getData("SINGLE_STANDARD_OWN_AMT", 0));
        //����ҽ�Ʊ��ղ�����
        confInParm.addData("COMP_AMT", dataParm.getData("SINGLE_SUPPLYING_AMT", 0));
        //�Է���Ŀ���
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        //������Ŀ���
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        //�𸶱�׼�����Ը��������
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));
        //ҽ�ƾ������˰������������
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));
        //ҽ�ƾ�������޶����Ͻ��
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));
        //��Ѫ�Ը����
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));
        //����ҽ���籣������
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));
        //ҽ�ƾ����籣������
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));
        //סԺ�Ʊ����
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        //����˵��
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));
        //��ҽ��Ŀ
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));
        //�������
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));
        //��������
        confInParm.addData("COMU_NO", ""); //�̶���ֵ
        //�����ֱ���
        confInParm.addData("SIN_DISEASE_CODE", parm.getData("SDISEASE_CODE", 0));
        //ҽʦ����
        confInParm.addData("DR_CODE", parm.getData("LCS_NO", 0));
        //�������1
        double armyaiAmt = parm.getDouble("ARMYAI_AMT",0);
        //�������2
        double pubmanaiAmt = parm.getDouble("PUBMANAI_AMT",0);
        double agentAmt = armyaiAmt + pubmanaiAmt;
        //�������
        confInParm.addData("AGENT_AMT", agentAmt);
        //��λ��������
        double bedSingleAmt = dataParm.getDouble("BED_SINGLE_AMT",0);
        //ҽ�ò��Ϸ�������
        double materialSingleAmt = dataParm.getDouble("MATERIAL_SINGLE_AMT",0);
        double specNeedAmt = bedSingleAmt + materialSingleAmt;
        //System.out.println("specNeedAmt:"+specNeedAmt);
        //������Ŀ���
        confInParm.addData("SPEC_NEED_AMT", specNeedAmt);
        //����󲡾���
        confInParm.addData("ILLNESS_SUBSIDY_AMT", parm.getData("ILLNESS_SUBSIDY_AMT", 0));
        //������Ժ���
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        //�����ϴ���ʽ
        confInParm.addData("SINGLE_UPLOAD_TYPE", parm.getValue("SINGLE_UPLOAD_TYPE", 0).length()>0?
          		 parm.getValue("SINGLE_UPLOAD_TYPE", 0):"");
        //��θ���
        confInParm.addData("PARM_COUNT", 67);
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
     * סԺ�ϴ�������ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_E(TParm parm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        //�ϴ���ϸINS_ORDER
        int count = parm.getCount("ADM_SEQ");

        for (int m = 0; m < count; m++) {
            //��ҽ˳���
            confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", m));
            //ҽ��ר��Ʊ�ݺ�
//            confInParm.addData("INVNO", invNo);
            confInParm.addData("INVNO", invNo);
            //ҽԺ����
            confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", m));
            //�շ���Ŀ����
            confInParm.addData("NHI_ORDER_CODE",
                               parm.getData("NHI_ORDER_CODE", m));
            //ҽԺ������Ŀ����
            confInParm.addData("ORDER_DESC", parm.getData("ORDER_DESC", m));
            //�Ը�����
            confInParm.addData("OWN_RATE",
                               parm.getDouble("OWN_RATE", m) == 0 ? 0.00 :
                               parm.getDouble("OWN_RATE", m) );
            //����
            confInParm.addData("JX", parm.getData("JX", m));
            //���
            confInParm.addData("GG", parm.getData("GG", m));
            //����
            confInParm.addData("PRICE", parm.getData("PRICE", m));
            //����
            confInParm.addData("QTY", parm.getData("QTY", m));
            //�������
            confInParm.addData("TOTAL_AMT", parm.getData("TOTAL_AMT", m));
            //�걨���
            confInParm.addData("TOTAL_NHI_AMT", parm.getData("TOTAL_NHI_AMT", m));
            //ȫ�Էѽ��
            confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", m));
            //�������
            confInParm.addData("ADDPAY_AMT", parm.getData("ADDPAY_AMT", m));
            //�������ñ�־
            confInParm.addData("OP_FLG", parm.getData("OP_FLG", m).equals("Y")?"1":"0");
            //�ۼ�������־
            confInParm.addData("ADDPAY_FLG", parm.getData("ADDPAY_FLG", m).equals("Y")?"1":"0");
            //ͳ�ƴ���
            confInParm.addData("NHI_ORD_CLASS_CODE",
                               parm.getData("NHI_ORD_CLASS_CODE", m));
            //����ҩƷ��־
            confInParm.addData("PHAADD_FLG", parm.getData("PHAADD_FLG", m).equals("Y")?"1":"0");
            //��Ժ��ҩ��־
//            System.out.println("�Ǿӳ�Ժ��ҩע�Ǵ���ǰ"+parm.getData("CARRY_FLG", m));
            confInParm.addData("CARRY_FLG", parm.getData("CARRY_FLG", m).equals("Y")?"1":"0");
//            System.out.println("�Ǿӳ�Ժ��ҩע�Ǵ���ǰ��"+confInParm.getValue("CARRY_FLG",m));
            //��׼�ĺ�
            confInParm.addData("PZWH", parm.getData("PZWH", m));
            //���
            confInParm.addData("SEQ_NO", parm.getData("SEQ_NO", m));
            String chargeDateF = parm.getValue("CHARGE_DATE", m);
            String chargeDateE = chargeDateF.substring(0, 4) + "-" +
                                 chargeDateF.substring(4, 6) + "-" +
                                 chargeDateF.substring(6, 8) +
                                 " " + chargeDateF.substring(8, 10) + ":" +
                                 chargeDateF.substring(10, 12) + ":" +
                                 chargeDateF.substring(12, 14);
            //��ϸ����ʱ��
            confInParm.addData("CHARGE_DATE", chargeDateE);
            //�¾�ҽ˳���
            confInParm.addData("NEWADM_SEQ", parm.getData("NEWADM_SEQ", m));
            //ҽԺ����������
            confInParm.addData("INSBRANCH_CODE",
                               parm.getData("INSBRANCH_CODE", m));
            //�������
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
     * ������Ϣ�ͳ�Ժ��Ϣ�ϴ�
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
        //����ҽ������
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
        //����Ͻ�ȡ����
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }         
        confInParm.addData("DIAG_DESC2",diagdesc2);
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
//        System.out.println("ISSUE������������������������" +
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
        //�������
        confInParm.addData("AGENT_AMT", parm.getData("ARMYAI_AMT", 0));
        //������ʽ
        confInParm.addData("BIRTH_TYPE", "");
        //����̥������
        confInParm.addData("BABY_NO", 0);
        //����󲡾���
        confInParm.addData("ILLNESS_SUBSIDY_AMT", parm.getData("ILLNESS_SUBSIDY_AMT", 0));
        //������Ժ���
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        //���ָ����˳�ԭ��
        confInParm.addData("QUIT_REMARK", parm.getValue("QUIT_REMARK", 0).length()>0?
          		 parm.getValue("QUIT_REMARK", 0):"");
        confInParm.addData("PARM_COUNT", 65);
        result = InsManager.getInstance().safe(confInParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * סԺ������Ϣ�ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_zjks_A1(TParm parm,String type) {
    	 TParm result = new TParm();
    	 TParm ParmData = new TParm();
    	 ParmData.setData("PIPELINE", "DataDown_zjks");
    	 ParmData.setData("PLOT_TYPE", "A1");
    	 if(type.equals("CZ"))
    		ParmData.addData("INS_TYPE", "310");//����
          else if(type.equals("CJ"))
        	ParmData.addData("INS_TYPE", "390");//����
    	 ParmData.addData("HOSP_NHI_NO", this.nhi_hosp_code);//ҽԺ����
    	 ParmData.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//��ҽ˳���
    	 ParmData.addData("CONFIRM_NO", parm.getData("CONFIRM_NO", 0));//�ʸ�ȷ������
    	 ParmData.addData("OWN_NO", parm.getData("PERSONAL_NO", 0));//���˱���
    	 ParmData.addData("ID_NO", parm.getData("IDNO", 0));//����֤����
    	 ParmData.addData("PAT_NAME", parm.getData("PAT_NAME", 0));//����
    	 ParmData.addData("SEX_CODE", parm.getData("SEX_CODE", 0));//�Ա�
         ParmData.addData("AGE", parm.getData("PAT_AGE", 0));//����
    	 ParmData.addData("BIRTH_DATE", parm.getData("BIRTH_DATE", 0));//��������
    	 ParmData.addData("ICD_CODE", parm.getData("ICD_CODE", 0));//��������Ҫ�������
    	 ParmData.addData("ICD_DESC", parm.getData("ICD_DESC", 0));//��������
    	 ParmData.addData("PARM_COUNT", 12);//�������
//       System.out.println("ParmData:======="+ParmData);
       result = InsManager.getInstance().safe(ParmData);
//       System.out.println("result" + result);
       if (result.getErrCode() < 0) {
           this.messageBox(result.getErrText());
           return result;
       } else {
    	 //����������Ǹ���
    	   String sql =
               " UPDATE INS_ADM_CONFIRM " +
               " SET DEATH_FLG = 'Y' " +
               " WHERE CASE_NO = '" + parm.getData("CASE_NO", 0) + "' " +
               " AND ADM_SEQ = '" + parm.getData("ADM_SEQ", 0) + "' " +
               " AND IN_STATUS !='5'";
//            System.out.println("sql" + sql);
    	   result = new TParm(TJDODBTool.getInstance().update(sql)); 
    	   if (result.getErrCode() < 0)
               return result;    	         
      }	   
    	 return result; 
    }
    /**
     * ������ҳ�ϴ�
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
         mroParm.addData("HOSP_DESC", this.nhi_hosp_desc);//ҽԺ����
         mroParm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//ҽԺ����
         mroParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//��ҽ˳���
         mroParm.addData("PAY_WAY", parm.getValue("PAY_WAY", 0).length()>0?
        		 parm.getValue("PAY_WAY", 0):"9");//ҽ�Ƹ��ѷ�ʽ
         mroParm.addData("CARD_NO", parm.getData("CARD_NO", 0));//��������
         mroParm.addData("IN_TIMES", parm.getData("IN_TIMES", 0));//��סԺ����
         mroParm.addData("MR_NO", parm.getData("MR_NO", 0));//������
         mroParm.addData("PAT_NAME", parm.getData("PAT_NAME", 0));//����
         mroParm.addData("SEX", parm.getData("SEX", 0));//�Ա�
         mroParm.addData("BIRTH_DATE", parm.getData("BIRTH_DATE", 0));//��������
         //�������
        String age = parm.getValue("AGE", 0);
        String age1 ="";
        String age2 ="";
        String age3 ="";
         int ageflg = Integer.valueOf(age.substring(0,age.indexOf("��")));
        if(ageflg>=1)
        	age1 = age.substring(0,age.indexOf("��"));
        else if(ageflg<1){
        	if(age.length()>3){
        	age1 = "0";
        	age2 = age.substring(age.indexOf("��")+1,age.indexOf("��"));
        	age3 = age.substring(age.indexOf("��")+1,age.indexOf("��"));
        	}
        	else
        	age1 = "0";	
        }
//        System.out.println("age1====:"+age1);
//        System.out.println("age2====:"+age2);
//        System.out.println("age3====:"+age3);
         mroParm.addData("AGE1", age1.length()>0?age1:"0");//����1
         mroParm.addData("NATION", parm.getData("NATION", 0));//����
         mroParm.addData("AGE2", age2.length()>0?age2:"0");//����2(��)
         mroParm.addData("AGE3", age3.length()>0?age3:"0");//����2(��)
         mroParm.addData("NB_WEIGHT", parm.getValue("NB_WEIGHT", 0).length()>0?
        		 parm.getValue("NB_WEIGHT", 0):"0");//��������������
         mroParm.addData("NB_IN_WEIGHT", parm.getValue("NB_IN_WEIGHT", 0).length()>0?
        		 parm.getValue("NB_IN_WEIGHT", 0):"0");//��������Ժ����
         mroParm.addData("BIRTH_ADDRESS", parm.getData("BIRTH_ADDRESS", 0));//������
         mroParm.addData("BIRTHPLACE", parm.getData("BIRTHPLACE", 0));//����
         mroParm.addData("FOLK", parm.getData("FOLK", 0));//����
         mroParm.addData("ID_NO", parm.getData("ID_NO", 0));//����֤��
         mroParm.addData("OCCUPATION", parm.getData("OCCUPATION", 0));//ְҵ
         mroParm.addData("MARRIGE", parm.getValue("MARRIGE", 0).length()>0?
        		 parm.getValue("MARRIGE", 0):"9");//����״��
         mroParm.addData("ADDRESS", parm.getData("ADDRESS", 0));//��סַ
         mroParm.addData("ADDRESS_TEL", parm.getData("ADDRESS_TEL", 0));//��סַ�绰
         mroParm.addData("POST_NO", parm.getData("POST_NO", 0));//��סַ�ʱ�
         mroParm.addData("H_ADDRESS", parm.getData("H_ADDRESS", 0));//���ڵ�ַ
         mroParm.addData("POST_CODE", parm.getData("POST_CODE", 0));//�������ڵ��ʱ�
         mroParm.addData("O_ADDRESS", parm.getData("O_ADDRESS", 0));//������λ����ַ
         mroParm.addData("O_TEL", parm.getData("O_TEL", 0));//������λ�绰
         mroParm.addData("O_POSTNO", parm.getData("O_POSTNO", 0));//��λ�ʱ�
         mroParm.addData("CONTACTER", parm.getData("CONTACTER", 0));//��ϵ������
         mroParm.addData("RELATIONSHIP", parm.getData("RELATIONSHIP", 0));//�뻼�߹�ϵ
         mroParm.addData("CONT_ADDRESS", parm.getData("CONT_ADDRESS", 0));//��ϵ�˵�ַ
         mroParm.addData("CONT_TEL", parm.getData("CONT_TEL", 0));//��ϵ�˵绰
         mroParm.addData("ADM_SOURCE", parm.getValue("ADM_SOURCE", 0).length()>0?
        		 parm.getValue("ADM_SOURCE", 0):"9");//��Ժ;��
         mroParm.addData("IN_DATE", parm.getData("IN_DATE", 0));//��Ժʱ��
         mroParm.addData("IN_DEPT", parm.getData("IN_DEPT", 0));//��Ժ�Ʊ�
         mroParm.addData("IN_STATION", parm.getData("IN_STATION", 0));//��Ժ����
         mroParm.addData("TRANS_DEPT", parm.getData("TRANS_DEPT", 0));//ת�ƿƱ�
         mroParm.addData("OUT_DATE", parm.getData("OUT_DATE", 0));//��Ժʱ��
         mroParm.addData("OUT_DEPT", parm.getData("OUT_DEPT", 0));//��Ժ�Ʊ�
         mroParm.addData("OUT_STATION", parm.getData("OUT_STATION", 0));//��Ժ����
         mroParm.addData("REAL_STAY_DAYS", parm.getData("REAL_STAY_DAYS", 0));//ʵ��סԺ����
         mroParm.addData("OE_DIAG_DESC", parm.getData("OE_DIAG_DESC", 0));//�ţ����������
         //�ţ������Ｒ�������ȡ
         String oediagcode= parm.getValue("OE_DIAG_CODE", 0);
         int i = 1;
         while (i==1){
      	 byte[] buf= oediagcode.getBytes(); 
      	 if(buf.length>=10)    
      		oediagcode= oediagcode.substring(0,oediagcode.lastIndexOf("+"));
      	 else
      		 break;
         }          
         mroParm.addData("OE_DIAG_CODE", oediagcode);//�ţ������Ｒ������
         mroParm.addData("OUT_DIAG_MAIN", parm.getData("OUT_DIAG_MAIN", 0));//��Ժ��Ҫ���
         mroParm.addData("OUT_DIAG_OTHER", parm.getData("OUT_DIAG_OTHER", 0));//��Ժ�������
         mroParm.addData("EX_RSN_DESC", parm.getData("EX_RSN_DESC", 0));//���ˡ��ж����ⲿԭ��
         mroParm.addData("EX_RSN_CODE", parm.getData("EX_RSN_CODE", 0));//���ˡ��ж��ļ�������
         mroParm.addData("PATHOLOGY_DIAG", parm.getData("PATHOLOGY_DIAG", 0));//�������
         mroParm.addData("PATHOLOGY_DIAG_CODE", parm.getData("PATHOLOGY_DIAG_CODE", 0));//������ϼ�������
         mroParm.addData("PATHOLOGY_NO", parm.getData("PATHOLOGY_NO", 0));//������
         mroParm.addData("ALLEGIC_FLG", parm.getValue("ALLEGIC_FLG", 0).length()>0?
        		 parm.getValue("ALLEGIC_FLG", 0):"1");//ҩ�������־
         mroParm.addData("ALLEGIC", parm.getData("ALLEGIC", 0));//����ҩ��
         mroParm.addData("BODY_CHECK", parm.getValue("BODY_CHECK", 0).length()>0?
        		 parm.getValue("BODY_CHECK", 0):"1");//��������ʬ���־
         mroParm.addData("BLOOD_TYPE", parm.getValue("BLOOD_TYPE", 0).length()>0?
        		 parm.getValue("BLOOD_TYPE", 0):"6");//Ѫ��
         mroParm.addData("RH_TYPE", parm.getValue("RH_TYPE", 0).length()>0?
        		 parm.getValue("RH_TYPE", 0):"4");//RH
         mroParm.addData("DIRECTOR_DR_CODE", parm.getData("DIRECTOR_DR_CODE", 0));//������
         mroParm.addData("PROF_DR_CODE", parm.getData("PROF_DR_CODE", 0));//���Σ������Σ�ҽʦ
         mroParm.addData("ATTEND_DR_CODE", parm.getData("ATTEND_DR_CODE", 0));//����ҽʦ
         mroParm.addData("VS_DR_CODE", parm.getData("VS_DR_CODE", 0));//סԺҽʦ
         mroParm.addData("VS_NURSE_CODE", parm.getData("VS_NURSE_CODE", 0));//���λ�ʿ
         mroParm.addData("INDUCATION_DR_CODE", parm.getData("INDUCATION_DR_CODE", 0));//����ҽʦ
         mroParm.addData("INTERN_DR_CODE", parm.getData("INTERN_DR_CODE", 0));//ʵϰҽʦ
         mroParm.addData("ENCODER", parm.getData("ENCODER", 0));//����Ա
         mroParm.addData("QUALITY", parm.getData("QUALITY", 0));//��������
         mroParm.addData("CTRL_DR", parm.getData("CTRL_DR", 0));//�ʿ�ҽʦ
         mroParm.addData("CTRL_NURSE", parm.getData("CTRL_NURSE", 0));//�ʿػ�ʿ
         mroParm.addData("CTRL_DATE", parm.getData("CTRL_DATE", 0));//�ʿ�����
         mroParm.addData("OUT_TYPE", parm.getValue("OUT_TYPE", 0).length()>0?
        		 parm.getValue("OUT_TYPE", 0):"9");//��Ժ��ʽ
         mroParm.addData("TRAN_HOSP", parm.getData("TRAN_HOSP", 0));//�����ҽ�ƻ�������
         mroParm.addData("AGN_PLAN_FLG", parm.getValue("AGN_PLAN_FLG", 0).length()>0?
        		 parm.getValue("AGN_PLAN_FLG", 0):"1");//��Ժ31������סԺ
         mroParm.addData("AGN_INTENTION", parm.getData("AGN_PLAN_INTENTION", 0));//��סԺĿ��
         //­�����˻��߻�����Ժǰʱ��
         String becomatime = parm.getValue("BE_COMA_TIME", 0).length()>0? 
        		             parm.getValue("BE_COMA_TIME", 0):"000000";
         becomatime = becomatime.substring(0, 2)+"@"+
                      becomatime.substring(2, 4)+"@"+
                      becomatime.substring(4, 6);
         //­�����˻��߻�����Ժ��ʱ��
         String afcomatime = parm.getValue("AF_COMA_TIME", 0).length()>0? 
	                         parm.getValue("AF_COMA_TIME", 0):"000000";
         afcomatime = afcomatime.substring(0, 2)+"@"+
                      afcomatime.substring(2, 4)+"@"+
                      afcomatime.substring(4, 6);
         mroParm.addData("BE_COMA_TIME", becomatime);//­�����˻��߻�����Ժǰʱ��
         mroParm.addData("AF_COMA_TIME", afcomatime);//­�����˻��߻�����Ժ��ʱ��
         mroParm.addData("SUM_TOT", parm.getData("SUM_TOT", 0));//סԺ�ܽ��
         mroParm.addData("OWN_TOT", parm.getData("OWN_TOT", 0));//סԺ�Ը����
         mroParm.addData("CHARGE_01", parm.getData("CHARGE_01", 0));//һ��ҽ�Ʒ����
         mroParm.addData("CHARGE_02", parm.getData("CHARGE_02", 0));//һ�����Ʋ�����
         mroParm.addData("CHARGE_03", parm.getData("CHARGE_03", 0));//������
         mroParm.addData("CHARGE_04", parm.getData("CHARGE_04", 0));//�ۺ�ҽ����������
         mroParm.addData("CHARGE_05", parm.getData("CHARGE_05", 0));//������Ϸ�
         mroParm.addData("CHARGE_06", parm.getData("CHARGE_06", 0));//ʵ������Ϸ�
         mroParm.addData("CHARGE_07", parm.getData("CHARGE_07", 0));//Ӱ��ѧ��Ϸ�
         mroParm.addData("CHARGE_08", parm.getData("CHARGE_08", 0));//�ٴ������Ŀ��
         
         //���������������Ŀ��
         double charge09 = parm.getDouble("CHARGE_09",0);//�ٴ��������Ʒ�
         double charge10 = parm.getDouble("CHARGE_10",0);//���ٴ��������Ʒ�    
         mroParm.addData("CHARGE_09", parm.getData("CHARGE_09", 0));//�ٴ��������Ʒ�
         mroParm.addData("CHARGE_10", df.format(charge09+charge10));//������������Ŀ��
       
         //�����������Ʒ�
         double charge11 = parm.getDouble("CHARGE_11",0);//������
         double charge12 = parm.getDouble("CHARGE_12",0);//������
         double charge13 = parm.getDouble("CHARGE_13",0);//�������Ʒ�����        
         mroParm.addData("CHARGE_13", df.format(charge11+charge12+charge13));//�������Ʒ�
         mroParm.addData("CHARGE_11", parm.getData("CHARGE_11", 0));//������
         mroParm.addData("CHARGE_12", parm.getData("CHARGE_12", 0));//������
         mroParm.addData("CHARGE_14", parm.getData("CHARGE_14", 0));//������
         mroParm.addData("CHARGE_15", parm.getData("CHARGE_15", 0));//��ҽ���Ʒ�
         //������ҩ��
         double charge16 = parm.getDouble("CHARGE_16",0);
         double charge17 = parm.getDouble("CHARGE_17",0);
         mroParm.addData("CHARGE_16_17", df.format(charge16+charge17));//��ҩ��
         mroParm.addData("CHARGE_16", parm.getData("CHARGE_16", 0));//����ҩ�����
         mroParm.addData("CHARGE_18", parm.getData("CHARGE_18", 0));//�г�ҩ��
         mroParm.addData("CHARGE_19", parm.getData("CHARGE_19", 0));//�в�ҩ��
         mroParm.addData("CHARGE_20", parm.getData("CHARGE_20", 0));//Ѫ��
         mroParm.addData("CHARGE_21", parm.getData("CHARGE_21", 0));//�׵�������Ʒ��
         mroParm.addData("CHARGE_22", parm.getData("CHARGE_22", 0));//�򵰰�����Ʒ��
         mroParm.addData("CHARGE_23", parm.getData("CHARGE_23", 0));//��Ѫ��������Ʒ��
         mroParm.addData("CHARGE_24", parm.getData("CHARGE_24", 0));//ϸ����������Ʒ��
         mroParm.addData("CHARGE_25", parm.getData("CHARGE_25", 0));//�����һ����ҽ�ò��Ϸ�
         mroParm.addData("CHARGE_26", parm.getData("CHARGE_26", 0));//������һ����ҽ�ò��Ϸ�
         mroParm.addData("CHARGE_27", parm.getData("CHARGE_27", 0));//������һ����ҽ�ò��Ϸ�
         mroParm.addData("CHARGE_28", parm.getData("CHARGE_28", 0));//������   
         //��֢�໤
         String icuRoom = "";
         if(parm.getValue("ICU_ROOM1", 0).length()==0&&
        	parm.getValue("ICU_ROOM2", 0).length()==0&&
        	parm.getValue("ICU_ROOM3", 0).length()==0&&
        	parm.getValue("ICU_ROOM4", 0).length()==0&&
        	parm.getValue("ICU_ROOM5", 0).length()==0)
            mroParm.addData("ICU_ROOM",icuRoom);//��֢�໤  
         else{
        	 for (int j = 1; j < 6; j++) {
        		 System.out.println("ICU_ROOM"+j+":"+parm.getValue("ICU_ROOM"+j, 0));
            	 System.out.println("ICU_IN_DATE"+j+":"+parm.getValue("ICU_IN_DATE"+j, 0));
            	 System.out.println("ICU_OUT_DATE"+j+":"+parm.getValue("ICU_OUT_DATE"+j, 0)); 
        	    if(parm.getValue("ICU_ROOM"+j, 0).length()>0&&
        	       parm.getValue("ICU_IN_DATE"+j, 0).length()>0&&
        	       parm.getValue("ICU_OUT_DATE"+j, 0).length()>0){
        	     String indate = StringTool.getString(
        	        parm.getTimestamp("ICU_IN_DATE"+j, 0), "yyyy-MM-dd HH");
        	     String outdate = StringTool.getString(
        	        parm.getTimestamp("ICU_OUT_DATE"+j, 0), "yyyy-MM-dd HH");       	
        	     icuRoom += parm.getValue("ICU_ROOM"+j, 0)+"@"+indate+"@"+outdate+"%"; 
        	 }
           } 
//             System.out.println("icuRoom:======"+icuRoom);
             mroParm.addData("ICU_ROOM",icuRoom.substring(0, icuRoom.length() - 1));//��֢�໤ 
         }
         mroParm.addData("VENTI_TIME",parm.getValue("VENTI_TIME", 0).length()>0?
        		 parm.getValue("VENTI_TIME", 0):"0");//�ۼ�ʹ��Сʱ��
         mroParm.addData("PARM_COUNT", 107);//�������
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
     * סԺ������ҳ֮�����������ϴ�
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
        	mroopParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ", i));//��ҽ˳���
        	mroopParm.addData("OPT_CODE", parm.getValue("OPT_CODE", i));//��������
        	mroopParm.addData("OP_DATE", parm.getValue("OP_DATE", i));//����
        	mroopParm.addData("OP_LEVEL", parm.getValue("OP_LEVEL", i));//��������
        	mroopParm.addData("OP_NAME", parm.getValue("OP_NAME", i));//��������
        	mroopParm.addData("OP_DR_NAME", parm.getValue("OP_DR_NAME", i));//����ҽʦ����
        	mroopParm.addData("AST_DR1", parm.getValue("AST_DR1", i));//1������
        	mroopParm.addData("AST_DR2", parm.getValue("AST_DR2", i));//2������
        	mroopParm.addData("HEAL_LEV", parm.getValue("HEAL_LEV", i));//�п����ϵȼ�
        	mroopParm.addData("ANA_WAY", parm.getValue("ANA_WAY", i));//������ʽ
        	mroopParm.addData("ANA_DR", parm.getValue("ANA_DR", i));//����ҽʦ
        	mroopParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));//���
        	mroopParm.addData("PARM_COUNT", 12);//�������
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
    /**
     * ������ҳ����
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_G1(TParm parm,String type) {
    	TParm result = new TParm();
    	 TParm mroParm = new TParm();
         if(type.equals("CZ"))
         mroParm.setData("PIPELINE", "DataDown_zjks");
         else if(type.equals("CJ"))
         mroParm.setData("PIPELINE", "DataDown_cjks");	 
         mroParm.setData("PLOT_TYPE", "G1");
         mroParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//��ҽ˳���
         mroParm.addData("HOSP_NHI_NO", this.nhi_hosp_code);//ҽԺ����
         mroParm.addData("PARM_COUNT", 2);//�������
         result = InsManager.getInstance().safe(mroParm);
    	 return result;
    }
}