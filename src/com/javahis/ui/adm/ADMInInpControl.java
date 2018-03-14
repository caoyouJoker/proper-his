package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSBillmTool;
import jdo.inw.InwForOutSideTool;
import jdo.mro.MROTool;
import jdo.odi.OdiOrderTool;
import jdo.sum.SUMVitalSignTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ESign;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;

/**
 * <p>
 * Title:ת�ƹ���
 * </p>
 * 
 * <p>
 * Description:ת�ƹ���
 * </p>
 *
 * <p> 
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author zhangk
 * @version 1.0
 */
public class ADMInInpControl extends TControl {
	TParm acceptData = new TParm(); // �Ӳ�
	Pat pat = new Pat();
	String preFlg="";
	String bedNo="";
	String preOutNo="";
	private TWord word;
	private static final String TWORD = "WORD";


	public void onInit() {
		super.onInit();
		initWord();
		acceptData = (TParm)this.getParameter();
		this.setValueForParm("BED_NO;MR_NO;IPD_NO;PAT_NAME;SEX_CODE;",
				acceptData);
		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		TParm admInp = ADMTool.getInstance().getADM_INFO(parm);
		this.setValue("OUT_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
		this.setValue("OUT_STATION_CODE", admInp.getValue("STATION_CODE", 0));
		// ��Ժ����
		((TTextFormat)this.getComponent("IN_DATE")).setValue(acceptData
				.getData("IN_DATE"));
		// Ԥ������
		setValue("OUT_DATE", SystemTool.getInstance().getDate());

		preFlg=acceptData.getValue("PRE_FLG");//��ȡԤת���
		//bedNo=acceptData.getValue("BED_NO");//��ȡ����
		preOutNo=acceptData.getValue("PRETREAT_OUT_NO");
	}

	/**
	 * �����¼�
	 */
	public void onSave() {
		String out = getValue("OUT_INP").toString();
		if ("Y".equals(out))
			this.outAdm();
		else
			this.InOutDept();
	}

	/**
	 * ת�Ʊ���
	 */
	public void InOutDept() {
		if (!checkDate())
			return;
		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("IPD_NO", this.getValue("IPD_NO"));
		parm.setData("BED_NO", this.getValue("BED_NO"));
		parm.setData("IN_DEPT_CODE", this.getValue("IN_DEPT_CODE"));
		parm.setData("IN_STATION_CODE", this.getValue("IN_STATION_CODE"));
		parm.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
		parm.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
		parm.setData("OUT_DATE", this.getValue("OUT_DATE"));
		parm.setData("PSF_KIND", "INDP");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		// ==========pangben modify 20110617 start
		parm.setData("REGION_CODE", Operator.getRegion());
		//        //add by chenhj 2017.5.18
		//        int i=onCheckSign(acceptData.getData("CASE_NO").toString());
		//        if(i==2){
		//        	return;
		//        }

		// modified by Eric 20170606 start
		// ����ҽʦд�Ĳ������û���ϼ�ҽʦǩ������Ҫ��ʾ������ǿ��ǩ��
		// ��ѯ�������в���
		String emrSql="SELECT * FROM EMR_FILE_INDEX "
				+ "WHERE CASE_NO="
				+"'"+acceptData.getData("CASE_NO").toString()+"'";
		TParm emrParm=new TParm(TJDODBTool.getInstance().select(emrSql));
		if (word == null) {
			word = new TWord();
		}
		List<String> list=new ArrayList<String>(); // û�й���ҽʦǩ��
		List<String> list2=new ArrayList<String>();// û���ϼ�ҽʦǩ��
		for (int i = 0; i < emrParm.getCount(); i++) {
			word.onOpen(emrParm.getValue("FILE_PATH",i),emrParm.getValue("FILE_NAME",i), 3, false);
			// �ж��Ƿ��ǹ���ҽʦд�Ĳ���
			if(isPracticerWrite(word)){// �ǹ���ҽʦд�Ĳ���
				// �ж��Ƿ��й���ҽʦǩ��
				if(isDrSign(word.getFileAuthor(), this.getUseName(word.getFileAuthor()), word)){// �й���ҽʦǩ��
					// ��ѯ�˲��˵��ϼ�ҽʦ
					TParm superResult = selectDrCodes(acceptData.getData("CASE_NO").toString());
					// �ж��Ƿ��о���ҽʦǩ��
					if(!isDrSign(superResult.getValue("VS_DR_CODE", 0), superResult.getValue("VS_DR_NAME", 0), word)){// û�о���ҽʦǩ��
						// �ж��Ƿ�������ҽʦǩ��
						if(!isDrSign(superResult.getValue("ATTEND_DR_CODE", 0), superResult.getValue("ATTEND_DR_NAME", 0), word)){// û������ҽʦǩ��
							// �ж��Ƿ��п�����ǩ��
							if(!isDrSign(superResult.getValue("DIRECTOR_DR_CODE", 0), superResult.getValue("DIRECTOR_DR_NAME", 0), word)){// û�п�����ǩ��
								list2.add(emrParm.getValue("FILE_NAME",i)+"����û���ϼ�ҽʦǩ��"+"\r\n");
							}
						}
					}

				}else{// û�й���ҽʦǩ�����˳�
					list.add(emrParm.getValue("FILE_NAME",i)+"����û�й���ҽʦǩ��"+"\r\n");
				}

			}
		}  
		if(!list.isEmpty()){
			//     			list.add("�Ƿ�������棿����"+"\r\n");
			this.messageBox(list.toString().replace("[", "").replace("]", "").replace(",", ""));
			return;
		}
		if(!list2.isEmpty()){
			list2.add("�Ƿ�������棿����"+"\r\n");
			// ȡ����2��ȷ����0
			int flg = this.messageBox("", list2.toString().replace("[", "").replace("]", "").replace(",", ""), 2);
			System.out.println("======flg="+flg);
			if(flg==2){// ������
				return;
			}
		}
		// modified by Eric 20170606 end

		/***********************liuf HL7 *******************************/
		//        TParm parmCIS = new TParm();
		//        parmCIS.setData("CASE_NO", acceptData.getData("CASE_NO"));
		//        parmCIS.setData("MR_NO", this.getValue("MR_NO"));
		//        parmCIS.setData("IPD_NO", this.getValue("IPD_NO"));
		//        parmCIS.setData("BED_NO", this.getValue("BED_NO"));
		//        parmCIS.setData("IN_DEPT_CODE", this.getValue("IN_DEPT_CODE"));
		//        parmCIS.setData("IN_STATION_CODE", this.getValue("IN_STATION_CODE"));
		//        parmCIS.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
		//        parmCIS.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
		//        parmCIS.setData("OUT_DATE", this.getValue("OUT_DATE"));
		//        parmCIS.setData("PSF_KIND", "INDP");
		//        parmCIS.setData("OPT_USER", Operator.getID());
		//        parmCIS.setData("OPT_TERM", Operator.getIP());
		//        parmCIS.setData("REGION_CODE", Operator.getRegion());
		//        sendHL7Mes(parmCIS, "ADM_TRAN");             
		/***********************liuf HL7 *******************************/
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(""+acceptData.getData("CASE_NO"));
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(""+acceptData.getData("CASE_NO"));
		parm.setData("ICU_FLG", IsICU);
		parm.setData("CCU_FLG", IsCCU);

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInOutSave", parm);
		if ("F".equals(result.getData("CHECK"))) {
			this.messageBox("�β����ݲ���ת�ƣ�");
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		} else {
			this.messageBox("P0005");
			isPre();//�����Ԥת״̬��ɾ��ADM_PRETREAT���е���Ϣ
			/*********************** HL7 *******************************/
			sendHL7Mes(parm, "ADM_TRAN");
			this.closeWindow();
		}

		// add by wangb 2015/07/02 ת��ʱ�����������Ϣ��ȷ��ͳ����Ϣ׼ȷ
		TParm xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(acceptData.getValue("CASE_NO"));
		if (xmlParm.getErrCode() < 0) {
			this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
		}
		xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(acceptData.getValue("CASE_NO"));
		if (xmlParm.getErrCode() < 0) {
			this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
		}
	}
	/**
	 * �����Ԥת״̬
	 */
	public void isPre(){
		String sql="";
		if("Y".equals(preFlg)){
			sql="DELETE FROM ADM_PRETREAT WHERE PRETREAT_NO = '"+preOutNo+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
		}
		sql="UPDATE SYS_BED SET PRE_FLG='N' WHERE PRETREAT_OUT_NO='"+preOutNo+"'";
		new TParm(TJDODBTool.getInstance().update(sql));

	}
	/**
	 * ��Ժ����
	 */
	public void outAdm() {
		if (!checkDate())
			return;
		// �жϲ����Ƿ����ٻصĲ��� �������ô��Ҫѡ���Ժ����
		TParm adm_case = new TParm();
		adm_case.setData("CASE_NO", acceptData.getData("CASE_NO"));
		//        //add by chenhj 2017.5.18
		//        int i=onCheckSign(acceptData.getData("CASE_NO").toString());
		//        if(i==2){
		//        	return;
		//        }

		// modified by wangqing 20170804 start
		// ����ҽʦд�Ĳ������û���ϼ�ҽʦǩ������Ҫ��ʾ������ǿ��ǩ��
		// ��ѯ�������в���
		String emrSql="SELECT * FROM EMR_FILE_INDEX "
				+ "WHERE CASE_NO="
				+"'"+acceptData.getData("CASE_NO").toString()+"'";
		TParm emrParm=new TParm(TJDODBTool.getInstance().select(emrSql));
		if (word == null) {
			word = new TWord();
		}
		List<String> list=new ArrayList<String>(); // û�й���ҽʦǩ��
		List<String> list2=new ArrayList<String>();// û���ϼ�ҽʦǩ��
		for (int i = 0; i < emrParm.getCount(); i++) {
			word.onOpen(emrParm.getValue("FILE_PATH",i),emrParm.getValue("FILE_NAME",i), 3, false);
			// �ж��Ƿ��ǹ���ҽʦд�Ĳ���
			if(isPracticerWrite(word)){// �ǹ���ҽʦд�Ĳ���
				// �ж��Ƿ��й���ҽʦǩ��
				if(isDrSign(word.getFileAuthor(), this.getUseName(word.getFileAuthor()), word)){// �й���ҽʦǩ��
					// ��ѯ�˲��˵��ϼ�ҽʦ
					TParm superResult = selectDrCodes(acceptData.getData("CASE_NO").toString());
					// �ж��Ƿ��о���ҽʦǩ��
					if(!isDrSign(superResult.getValue("VS_DR_CODE", 0), superResult.getValue("VS_DR_NAME", 0), word)){// û�о���ҽʦǩ��
						// �ж��Ƿ�������ҽʦǩ��
						if(!isDrSign(superResult.getValue("ATTEND_DR_CODE", 0), superResult.getValue("ATTEND_DR_NAME", 0), word)){// û������ҽʦǩ��
							// �ж��Ƿ��п�����ǩ��
							if(!isDrSign(superResult.getValue("DIRECTOR_DR_CODE", 0), superResult.getValue("DIRECTOR_DR_NAME", 0), word)){// û�п�����ǩ��
								list2.add(emrParm.getValue("FILE_NAME",i)+"����û���ϼ�ҽʦǩ��"+"\r\n");
							}
						}
					}

				}else{// û�й���ҽʦǩ�����˳�
					list.add(emrParm.getValue("FILE_NAME",i)+"����û�й���ҽʦǩ��"+"\r\n");
				}

			}
		}  
		if(!list.isEmpty()){
			//     			list.add("�Ƿ�������棿����"+"\r\n");
			this.messageBox(list.toString().replace("[", "").replace("]", "").replace(",", ""));
			return;
		}
		if(!list2.isEmpty()){
			list2.add("�Ƿ�������棿����"+"\r\n");
			// ȡ����2��ȷ����0
			int flg = this.messageBox("", list2.toString().replace("[", "").replace("]", "").replace(",", ""), 2);
			System.out.println("======flg="+flg);
			if(flg==2){// ������
				return;
			}
		}
		// modified by wangqing 20170804 end


		TParm check = ADMTool.getInstance().getADM_INFO(adm_case);
		Timestamp ds_date;
		String outDept=this.getValueString("OUT_DEPT_CODE");
		String outStation=this.getValueString("OUT_STATION_CODE");
		//===zhangp 20130105 start �ٻ������봲�Ĳ��˻���Ϊ���ű����ѯ�������õ���״̬Ϊ��Ժ���˵�
		TParm parm = new TParm();
		// Ŀǰ��û���ٻع��� ����ע�͵�
		if (check.getValue("LAST_DS_DATE", 0).length() > 0) { // ҽ�Ƴ�Ժ���ڲ�Ϊ��
			// ��ô���ٻصĲ���
			Object obj = this.openDialog("%ROOT%/config/adm/ADMChangeDsDate.x",
					check.getTimestamp("LAST_DS_DATE", 0));
			if (obj == null) {
				return;
			}
			ds_date = (Timestamp) obj;
			if(ds_date.equals(check.getTimestamp("LAST_DS_DATE", 0))){// SHIBL ADD ��Ժ���Ҳ���ȡ��һ�εļ�¼
				outDept=check.getValue("DS_DEPT_CODE", 0).equals("")?outDept:check.getValue("DS_DEPT_CODE", 0);
				outStation=check.getValue("DS_STATION_CODE", 0).equals("")?outStation:check.getValue("DS_STATION_CODE", 0);
			}
			parm.setData("ADMS", "1");
			//===zhangp 20130105 end

		} else {
			//�ٴγ�Ժ���ı��Ժ����
			if (check.getValue("DS_DATE", 0).length() > 0)
				ds_date = check.getTimestamp("DS_DATE", 0);
			else
				ds_date = SystemTool.getInstance().getDate();
		}
		// ����סԺ����
		int in_days = StringTool.getDateDiffer(
				(Timestamp)this.getValue("OUT_DATE"),
				acceptData.getTimestamp("IN_DATE"));
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("IPD_NO", this.getValue("IPD_NO"));
		parm.setData("BED_NO", this.getValue("BED_NO"));
		parm.setData("OUT_DEPT_CODE", outDept);
		parm.setData("OUT_STATION_CODE", outStation);
		parm.setData("IN_DAYS", in_days);
		parm.setData("OUT_DATE", ds_date);
		parm.setData("VS_NURSE_CODE", check.getValue("VS_NURSE_CODE", 0));
		parm.setData("DIRECTOR_DR_CODE", check.getValue("DIRECTOR_DR_CODE", 0));
		parm.setData("ATTEND_DR_CODE", check.getValue("ATTEND_DR_CODE", 0));
		parm.setData("VS_DR_CODE", check.getValue("VS_DR_CODE", 0));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		// =========pangben modify 20110617
		parm.setData("REGION_CODE", Operator.getRegion());
		// System.out.println("�ٴγ�Ժ���" + parm);
		/***********************liuf HL7 *******************************/
		//        TParm parmCIS = new TParm();
		//        parmCIS.setData("CASE_NO", acceptData.getData("CASE_NO"));
		//        parmCIS.setData("MR_NO", this.getValue("MR_NO"));
		//        parmCIS.setData("IPD_NO", this.getValue("IPD_NO"));
		//        parmCIS.setData("BED_NO", this.getValue("BED_NO"));
		//        parmCIS.setData("OUT_DEPT_CODE", this.getValue("OUT_DEPT_CODE"));
		//        parmCIS.setData("OUT_STATION_CODE", this.getValue("OUT_STATION_CODE"));
		//        parmCIS.setData("IN_DAYS", in_days);
		//        parmCIS.setData("OUT_DATE", ds_date);
		//        parmCIS.setData("VS_NURSE_CODE", check.getValue("VS_NURSE_CODE", 0));
		//        parmCIS.setData("DIRECTOR_DR_CODE", check.getValue("DIRECTOR_DR_CODE", 0));
		//        parmCIS.setData("ATTEND_DR_CODE", check.getValue("ATTEND_DR_CODE", 0));
		//        parmCIS.setData("VS_DR_CODE", check.getValue("VS_DR_CODE", 0));
		//        parmCIS.setData("OPT_USER", Operator.getID());
		//        parmCIS.setData("OPT_TERM", Operator.getIP());
		//        parmCIS.setData("REGION_CODE", Operator.getRegion());
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(""+acceptData.getData("CASE_NO"));
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(""+acceptData.getData("CASE_NO"));
		parm.setData("ICU_FLG", IsICU);
		parm.setData("CCU_FLG", IsCCU);
		//        sendHL7Mes(parmCIS, "ADM_OUT");
		/***********************liuf HL7 *******************************/

		//�ж��˵�״̬
		TParm backBillParm = IBSBillmTool.getInstance().selBackBill(parm);
		if (backBillParm.getErrCode() < 0) {
			err(backBillParm.getErrText());
		}
		int backBillCount = backBillParm.getCount("CASE_NO");


		Timestamp today = SystemTool.getInstance().getDate();
		Timestamp yesterday = StringTool.rollDate(today, -1);
		String yesDayStr = StringTool.getString(yesterday, "yyyyMMdd");
		String todayStr = StringTool.getString(today, "yyyyMMdd");
		String countDaySql = " SELECT SUM(D.TOT_AMT) AS TOT_AMT "
				+ "   FROM IBS_ORDM M,IBS_ORDD D " + "  WHERE D.CASE_NO = '"
				+ parm.getData("CASE_NO") + "' "
				+ "    AND M.CASE_NO = D.CASE_NO "
				+ "    AND M.DATA_TYPE = '0' "
				+ "    AND D.BILL_DATE BETWEEN TO_DATE('" + yesDayStr
				+ "','YYYYMMDD') " + "                      AND TO_DATE('"
				+ todayStr + "','YYYYMMDD') ";
		TParm countDayParm = new TParm(TJDODBTool.getInstance().select(
				countDaySql));  
		double autoFee = 0.00;     
		if (countDayParm.getCount() > 0)
			autoFee = countDayParm.getDouble("TOT_AMT", 0);
		if (parm.getInt("IN_DAYS") <= 0 && backBillCount <= 0 && autoFee == 0) {   
			//this.messageBox("��ʾ", "�Ѳ�������,�Ƿ���չ", 2) == 0
			if (messageBox("��ʾ��Ϣ", "�Ƿ���㵱����Ժ���ճ�Ժ�����Ĺ̶����ã�", this.YES_NO_OPTION) == 0) {
				String todayFlg = "Y";           
				parm.setData("TODAY", todayFlg);      
			}
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "outAdmSave", parm);
		if (result.getErrCode() < 0)
			this.messageBox("E0005");
		else {
			this.messageBox("P0005");

			isPre();//�����Ԥת״̬��ɾ��ADM_PRETREAT���е���Ϣ

			// wanglong add 20141103 ��Ժʱ���²�����ҳ������Ϣ
			TParm mro = new TParm();
			mro.setData("MR_NO", this.getValue("MR_NO"));
			mro.setData("CASE_NO", acceptData.getData("CASE_NO"));
			mro.setData("IN_DATE", acceptData.getData("IN_DATE"));
			mro.setData("OPT_USER", Operator.getID());
			mro.setData("OPT_TERM", Operator.getIP());
			result = MROTool.getInstance().updateMROPatInfo(mro);
			if (result.getErrCode() < 0) {
				this.messageBox("���²�����ҳ��Ϣʧ�� " + result.getErrText());
			}
			// ���Խӿ� wanglong add 20140731
			TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(acceptData.getValue("CASE_NO"), "A03");
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
			}
			xmlParm = ADMXMLTool.getInstance().creatXMLFile(acceptData.getValue("CASE_NO"));
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
			}
			// �������ӿ� wanglong add 20141010
			xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(acceptData.getValue("CASE_NO"));
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
			}
			xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(acceptData.getValue("CASE_NO"));
			if (xmlParm.getErrCode() < 0) {
				this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
			}
			/*********************** HL7 *******************************/
			sendHL7Mes(parm, "ADM_OUT");

			// add by wangb 2017/6/2 �Զ��������µ���Ժ���� START
			int hour = DateUtil.getHour(ds_date);
			TParm updateParm = new TParm();
			updateParm.setData("ADM_TYPE", "I");
			updateParm.setData("CASE_NO", acceptData.getData("CASE_NO"));
			updateParm.setData("EXAMINE_DATE", ds_date.toString().replace("-",
					"").substring(0, 8));
			updateParm.setData("EXAMINESESSION", hour / 4);
			updateParm.setData("PTMOVECATECODE", "05");
			updateParm.setData("PTMOVECATEDESC", DateUtil
					.transferHMToChinese(ds_date.toString()));

			// ���²�����̬��Ϣ
			updateParm = SUMVitalSignTool.getInstance().updatePatientTrends(
					updateParm);

			if (updateParm.getErrCode() < 0) {
				System.out.println("�������µ���Ժʱ��ʧ�ܣ�" + updateParm.getErrText());
			}
			// add by wangb 2017/6/2 �Զ��������µ���Ժ���� END

			this.closeWindow();
		}
	}
	//add by chenhj start
	/**
	 * add by chenhj ������ǩ�����
	 * @param caseNo
	 */
	public int  onCheckSign(String caseNo) { 
		//��ѯ��������ҽ��
		String sql = " SELECT VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE "
				+ " FROM ADM_INP " + " WHERE CASE_NO = '"
				+ caseNo + "'";
		TParm signParm=new TParm(TJDODBTool.getInstance().select(sql));
		//��������ҽ��code
		String vsDrCode=signParm.getValue("VS_DR_CODE",0);
		//��ѯ�������в���
		String emrSql="SELECT * FROM EMR_FILE_INDEX "
				+ "WHERE CASE_NO="
				+"'"+caseNo+"'";
		TParm emrParm=new TParm(TJDODBTool.getInstance().select(emrSql));
		if (word == null) {
			word = new TWord();
		}
		List<String> list=new ArrayList<String>(); 
		list.add("������δǩ����\r\n");
		for (int i = 0; i < emrParm.getCount(); i++) {
			word.onOpen(emrParm.getValue("FILE_PATH",i),emrParm.getValue("FILE_NAME",i), 3, false);
			boolean bb=isSign();
			if(!bb){
				String fileName=emrParm.getValue("FILE_NAME",i);
				String optDat=emrParm.getValue("OPT_DATE",i).substring(0,19);
				list.add(optDat+" "+fileName+"\r\n");
			}
		}
		int i=this.messageBox("δǩ���嵥",list.toString().replace("[", "").replace("]", "").replace(",", ""), 2);
		return i;
	}
	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
	}
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}
	public void setWord(TWord word) {
		this.word = word;
	}
	public TWord getWord() {
		return this.word;
	}
	private boolean isSign(){
		boolean flg=false;   	
		//ͬ�����    2����ͬǩ��
		ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
		ESign  sign2=(ESign)this.getWord().findObject(Operator.getID()+"_1", EComponent.SIGN_TYPE);
		if(sign1!=null){
			flg=true;
		}
		//
		//
		if(sign2!=null){
			flg=true;
		}
		return flg;
	}
	//add by chenhj end
	/**
	 * ��Ժ��ѡ�¼�
	 */
	public void onOUT_INP() {
		String check = getValue("OUT_INP").toString();
		if ("Y".equals(check)) {
			callFunction("UI|IN_DEPT_CODE|setEnabled", false);
			callFunction("UI|IN_STATION_CODE|setEnabled", false);
			TParm parm = new TParm();
			// ��鳤��ҽ��
			if (OdiOrderTool.getInstance().getUDOrder(
					acceptData.getValue("CASE_NO"))) {
				this.messageBox( "�ò�����ҽ��վδͣ�õĳ���ҽ������������ơ�");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			// ��黤ʿ���
			parm = new TParm();
			parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
			if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
				this.messageBox( "�ò����л�ʿվδ��˵�ҽ������������ơ�");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			//=============================  chenxi  add  20130321 ��Ժ��������ҩ��δ��ˣ�ҩ��δ��ҩ���
			// ���סԺҩ�����ִ�� true����δ���,false:û��δ���ҩ
			parm = new TParm();
			parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
			if(InwForOutSideTool.getInstance().checkDrug(parm)){
				this.messageBox( "סԺҩ���иò���δ��˵�ҽ������������ơ�");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			// �����ҩִ�� true����δ���ҩ,false:û��δ���ҩ
			parm = new TParm();
			parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
			if(InwForOutSideTool.getInstance().exeDrug(parm)){
				this.messageBox( "סԺҩ���иò���δ��ɵ���ҩ����������ơ�");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			//=============================  chenxi  add  20130321 ��Ժ��������ҩ��δ��ˣ�ҩ��δ��ҩ���
			// ��黤ʿִ��
			parm = new TParm();
			parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
			if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
				this.messageBox( "�ò����л�ʿվδִ�е�ҽ������������ơ�");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			//=============================  duzhw  add  20130917 ��Ժ����������ҩ�����Ƿ���ɣ�δ��ɲ����Ժ
			// 
			if (OdiOrderTool.getInstance().getRtnCfmM(
					acceptData.getValue("CASE_NO"))) {
				this.messageBox( "�ò�����ҩ����ҩδȷ�ϵ���Ϣ���������Ժ��");
				callFunction("UI|save|setEnabled", false);
				return   ;
			}
			//start У�����PDF���棬����б���δ�ش���������ʾ machao
			TParm result = onPdfParm();
			String orderDesc = "";
			for(int i = 0;i<result.getCount();i++){
				orderDesc = result.getValue("ORDER_DESC", i);
				this.messageBox(orderDesc+":ҽ������δ����!");
			}
			//end
		} else {
			callFunction("UI|IN_DEPT_CODE|setEnabled", true);
			callFunction("UI|IN_STATION_CODE|setEnabled", true);
			callFunction("UI|save|setEnabled", true);
		}
		// this.messageBox_(getValue("OUT_INP"));

	}

	public TParm onPdfParm(){
		TParm result = null;
		String sql = "SELECT * "+
					 "FROM MED_APPLY "+
					 "WHERE     MR_NO = '"+acceptData.getValue("MR_NO")+"' "+
					 "AND CASE_NO = '"+acceptData.getValue("CASE_NO")+"' "+
				     "AND CAT1_TYPE = 'LIS' "+   
				     "AND ADM_TYPE = 'I' "  + 
				     "AND (PDFRE_FLG = 'N' OR PDFRE_FLG IS NULL)"   ;
		//System.out.println("sql----- 1:"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));		       
		return result;
	}
	/**
	 * ѡ��ת������¼�
	 */
	public void onIN_DEPT_CODE() {
		this.setValue("IN_STATION_CODE", "");
	}

	/**
	 * ���ת�벡��
	 */
	public void onIN_STATION_CODE() {
		String inStation = this.getValue("IN_STATION_CODE").toString();
		String outStation = this.getValue("OUT_STATION_CODE").toString();
		if (inStation.equals(outStation)) {
			this.messageBox("ת��ת��������ͬ��");
			this.setValue("IN_STATION_CODE", "");
			return;
		}
	}

	/**
	 * �������
	 *
	 * @return boolean
	 */
	public boolean checkDate() {
		if (this.getValue("OUT_DEPT_CODE") == null
				|| this.getValue("OUT_DEPT_CODE").equals("")) {
			this.messageBox_("��ѡ��ת�����ң�");
			return false;
		}
		if (this.getValue("OUT_STATION_CODE") == null
				|| this.getValue("OUT_STATION_CODE").equals("")) {
			this.messageBox_("��ѡ��ת��������");
			return false;
		}
		String out = getValue("OUT_INP").toString();
		if ("N".equals(out)) {
			if (this.getValue("IN_DEPT_CODE") == null
					|| this.getValue("IN_DEPT_CODE").equals("")) {
				this.grabFocus("IN_DEPT_CODE");
				this.messageBox_("��ѡ��ת����ң�");
				return false;
			}
			//shibl 20120524 add
			//            if(this.getValue("IN_DEPT_CODE").equals(this.getValue("OUT_DEPT_CODE"))){
			//            	this.grabFocus("IN_DEPT_CODE");
			//                this.messageBox_("ת�������ת��������ͬ,������ѡ��");
			//                return false;
			//            }
			if (this.getValue("IN_STATION_CODE") == null
					|| this.getValue("IN_STATION_CODE").equals("")) {
				this.grabFocus("IN_STATION_CODE");
				this.messageBox_("��ѡ��ת�벡����");
				return false;
			}
		}
		TParm parm = new TParm();
		// ��鳤��ҽ��
		if (OdiOrderTool.getInstance().getUDOrder(
				acceptData.getValue("CASE_NO"))) {
			this.messageBox("�ò�����ҽ��վδͣ�õĳ���ҽ�������������");
			return false;

		}
		// ��黤ʿ���
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
			this.messageBox("�ò����л�ʿվδ��˵�ҽ��,���������");
			return false;

		}
		// ��黤ʿִ��
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
			this.messageBox("�ò����л�ʿվδִ�е�ҽ�������������");
			return false;
		}
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if(InwForOutSideTool.getInstance().checkDrug(parm)){
			this.messageBox( "ҩ���иò���δ��˵�ҽ������������ơ�");
			return  false;
		}
		// �����ҩִ�� true����δ���ҩ,false:û��δ���ҩ
		parm = new TParm();
		parm.setData("CASE_NO", acceptData.getValue("CASE_NO"));
		if(InwForOutSideTool.getInstance().exeDrug(parm)){
			this.messageBox( "ҩ���иò���δ��ɵ���ҩ����������ơ�");
			return  false ;
		}
		if (OdiOrderTool.getInstance().getRtnCfmM(acceptData.getValue("CASE_NO"))) {
			this.messageBox( "�ò�����ҩ����ҩδȷ�ϵ���Ϣ����������ơ�");
			return   false;
		}
		return true;
	}

	//$$==========liuf =============$$//
	/**
	 * hl7�ӿ�
	 * @param parm TParm
	 * @param type String
	 */
	private void sendHL7Mes(TParm parm, String type) {
		System.out.println("parm:"+parm);
		String caseNo = parm.getValue("CASE_NO");
		//ת��
		if (type.equals("ADM_TRAN")) 
		{
			String InDeptCode = parm.getValue("IN_DEPT_CODE");
			boolean IsICU = parm.getBoolean("ICU_FLG");
			boolean IsCCU = parm.getBoolean("CCU_FLG");
			//CIS
			if (InDeptCode.equals("0303")||InDeptCode.equals("0304")||IsICU||IsCCU) 
			{
				List list = new ArrayList();
				parm.setData("ADM_TYPE", "I");
				parm.setData("SEND_COMP", "CIS");
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
				if (resultParm.getErrCode() < 0)
					this.messageBox(resultParm.getErrText());
			}
			////////////////////////////////////////////zhangs add start
			//��Һ��
			System.out.println("��Һ��:"+this.checkIsCS5(caseNo));
			if (this.checkIsCS5(caseNo))
			{ 
				List list = new ArrayList();
				parm.setData("ADM_TYPE", "I");
				parm.setData("SEND_COMP", "CS5");
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
				if (resultParm.getErrCode() < 0)
					messageBox(resultParm.getErrText());
			}
			///////////////////////////////////////////////zhangs add end

			//Ѫ��
			List list = new ArrayList();
			parm.setData("ADM_TYPE", "I");
			parm.setData("SEND_COMP", "NOVA");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0)
				this.messageBox(resultParm.getErrText());

			// add by wangb 2017/3/24 ҽԺͬʱ����ŵ�ߺ�ǿ������Ѫ�ǳ��̽ӿڣ�һ����Ϣͬʱ���������̷���
			// ��ǿ��Ѫ�ǽӿڷ�����Ϣ START
			list = new ArrayList();
			parm.setData("SEND_COMP", "JNJ");
			list.add(parm);
			resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0) {
				messageBox(resultParm.getErrText());
			}
			// ��ǿ��Ѫ�ǽӿڷ�����Ϣ END

			//add by lij 20170413 ���� NIS��HL7��Ϣ
			list = new ArrayList();
			parm.setData("SEND_COMP", "NIS");
			list.add(parm);
			resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0) {
				messageBox(resultParm.getErrText());
			}

		}
		//��Ժ
		if (type.equals("ADM_OUT")) 
		{
			boolean IsICU = parm.getBoolean("ICU_FLG");
			boolean IsCCU = parm.getBoolean("CCU_FLG");
			//CIS
			if (IsICU||IsCCU) 
			{
				List list = new ArrayList();
				parm.setData("ADM_TYPE", "I");
				parm.setData("SEND_COMP", "CIS");
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
				if (resultParm.getErrCode() < 0)
					messageBox(resultParm.getErrText());
			}
			////////////////////////////////////////////zhangs add start
			//��Һ��
			System.out.println("��Һ��:"+this.checkIsCS5(caseNo));
			if (this.checkIsCS5(caseNo))
			{ 
				List list = new ArrayList();
				parm.setData("ADM_TYPE", "I");
				parm.setData("SEND_COMP", "CS5");
				list.add(parm);
				TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
				if (resultParm.getErrCode() < 0)
					messageBox(resultParm.getErrText());
			}
			///////////////////////////////////////////////zhangs add end

			//Ѫ��
			List list = new ArrayList();
			parm.setData("ADM_TYPE", "I");
			parm.setData("SEND_COMP", "NOVA");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0)
				this.messageBox(resultParm.getErrText());

			// add by wangb 2017/3/24 ҽԺͬʱ����ŵ�ߺ�ǿ������Ѫ�ǳ��̽ӿڣ�һ����Ϣͬʱ���������̷���
			// ��ǿ��Ѫ�ǽӿڷ�����Ϣ START
			list = new ArrayList();
			parm.setData("SEND_COMP", "JNJ");
			list.add(parm);
			resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0) {
				messageBox(resultParm.getErrText());
			}
			// ��ǿ��Ѫ�ǽӿڷ�����Ϣ END

			//add by lij 20170413 ���� NIS��HL7��Ϣ
			list = new ArrayList();
			parm.setData("SEND_COMP", "NIS");
			list.add(parm);
			resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
			if (resultParm.getErrCode() < 0) {
				messageBox(resultParm.getErrText());
			}
		}
	}
	//$$==========liuf =============$$//
	//////////////////////////////////////////////zhangs add start
	/**
	 * �Ƿ���CS5
	 * @param parm
	 * @return
	 */
	public boolean checkIsCS5(String caseNO) {
		TParm result = new TParm();
		TParm inparm=new TParm();
		boolean cs5Flg=false;
		inparm.setData("CASE_NO", caseNO);
		result = query(inparm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			return false;
		}
		//System.out.println(result.getBoolean("ICU_FLG",0)+"------------flg---------"+result.getBoolean("ICU_FLG"));
		cs5Flg=result.getBoolean("CS5_FLG",0);
		return cs5Flg;
	}


	private TParm query(TParm inparm) {
		String Sql =" SELECT B.CS5_FLG "+
				" FROM ADM_INP A,SYS_DEPT B "+
				" WHERE A.CASE_NO='"+inparm.getValue("CASE_NO")+"' "+
				" AND B.DEPT_CODE=A.DEPT_CODE ";
		//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("CS5_FLG") < 0) {
			this.messageBox("û�в�ѯ����Ӧ��¼");
			return null;
		}
		return tabParm;
	}
	//////////////////////////////////////////////////////zhangs add end

	/**
	 * <p>�ж��Ƿ��ǹ���ҽʦд�Ĳ���</p>
	 * 
	 * @return
	 */
	public boolean isPracticerWrite(TWord word){
		String fileAuthor = word.getFileAuthor();
		String roleIdSql = " SELECT ROLE_ID FROM SYS_OPERATOR WHERE USER_ID ='"+fileAuthor+"' ";
		TParm roleIdResult = new TParm(TJDODBTool.getInstance().select(roleIdSql));
		String roleId = roleIdResult.getValue("ROLE_ID", 0);
		// 1���ж��Ƿ��ǹ���ҽʦ
		if(roleId != null && roleId.equals("T")){// ����ҽʦ
			return true;
		}
		return false;
	}

	/**
	 * <p>��ѯ�˲����������ϼ�ҽʦ������ҽʦ������ҽʦ�������Σ�</p>
	 * 
	 * @return
	 */
	public TParm selectDrCodes(String caseNo){
		String superSql= " SELECT A.VS_DR_CODE, A.ATTEND_DR_CODE, A.DIRECTOR_DR_CODE, "
				+ " B.USER_NAME AS VS_DR_NAME, C.USER_NAME AS ATTEND_DR_NAME, D.USER_NAME AS DIRECTOR_DR_NAME "
				+ " FROM ADM_INP A "
				+ " LEFT JOIN SYS_OPERATOR B ON A.VS_DR_CODE = B.USER_ID "
				+ " LEFT JOIN SYS_OPERATOR C ON A.ATTEND_DR_CODE = C.USER_ID "
				+ " LEFT JOIN SYS_OPERATOR D ON A.DIRECTOR_DR_CODE = D.USER_ID "
				+ " WHERE A.CASE_NO='"+caseNo+"' ";
		TParm superResult = new TParm(TJDODBTool.getInstance().select(superSql));
		return superResult;
	}
	
	/**
	 * <p>��ȡҽ������</p>
	 * @param userId
	 * @return
	 */
	public String getUseName(String userId){
		String sql = " SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+userId+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()>0){
			return result.getValue("USER_NAME", 0);
		}
		return "";
	}

	/**
	 * <p>�жϲ����Ƿ�����Ӧҽʦ��ǩ��</p>
	 * 
	 * @return
	 */
	public boolean isDrSign(String drCode, String drName, TWord word){
		
		// 1��ǩ���ؼ� 2���̶��ı� 3���꣨����û����ȡֵ��
		if(drCode != null && drCode.length()>0){
			ESign sign=(ESign)word.findObject(drCode, EComponent.SIGN_TYPE);
			// modified by wangqing 20170804 start
			EFixed f1 = (EFixed)word.findObject("DR_SIGN", EComponent.FIXED_TYPE);
			EFixed f2 = (EFixed)word.findObject("SUPERIOR_SIGN", EComponent.FIXED_TYPE);
			if(sign!=null){
				return true;
			}
			if(f1 != null && f1.getText() != null && f1.getText().equals(drName)){
				return true;
			}
			if(f2 != null && f2.getText() != null && f2.getText().equals(drName)){
				return true;
			} 
			// modified by wangqing 20170804 end
		}
		return false;
	}


}
