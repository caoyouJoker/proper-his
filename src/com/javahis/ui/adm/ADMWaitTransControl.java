package com.javahis.ui.adm;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.adm.ADMResvTool;
import jdo.adm.ADMTransLogTool;
import jdo.adm.ADMWaitTransTool;
import jdo.adm.ADMXMLTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.StringTool;

import jdo.sys.Operator;
import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SYSBedTool;
import jdo.adm.ADMSQLTool;
import jdo.bil.BILPayTool;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSOrdermTool;
import jdo.inw.InwForOutSideTool;
import jdo.sys.SystemTool;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title: ���ת����
 * </p>
 *
 * <p>
 * Description: ���ת����    
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008  
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMWaitTransControl extends TControl {
	TParm patInfo = null;// ��ת��Ĳ�����Ϣ
	TParm admPat = null;// ��Ժ�Ĳ�����Ϣ
	TParm admInfo;// ��¼������λ״̬
	TTable in;//��Ժ������Ϣ��
	TTable inTable;//Ԥ��ת���
	TTable outTable;//Ԥ��ת����	
	public void onInit() {
		super.onInit();
		this.setValue("PRE_DATE", StringTool.rollDate(SystemTool.getInstance().getDate(), 1).toString().substring(0,10).replaceAll("-", "/")+" 23:59:59");
		inTable=(TTable) this.getComponent("TABLE_IN");
		outTable=(TTable) this.getComponent("TABLE_OUT");
		in=(TTable) this.getComponent("in");
		pageInit();
	}

	/**
	 * ҳ���ʼ��
	 */
	private void pageInit() {
		//============add  by  chenxi
		callFunction("UI|WAIT_IN|addEventListener",
                "WAIT_IN->" + TTableEvent.CLICKED, this, "onTABLEClicked");
		onQuery();
		onInit_Dept_Station();
		initInStation();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					chose();
				} catch (Exception e) {
				}
			}
		});
	}
	//====================chenxi add  
	public void onTABLEClicked(int row ){
		if (row < 0)
            return;
		TTable table = (TTable)this.getComponent("WAIT_IN") ;
		TTable inTable = (TTable)this.getComponent("in");
		int selectRow = 0 ;
		String bedNo= table.getValueAt(row, 4).toString().trim() ;
		if(!bedNo.equals("") || bedNo.length()<0){
		
			int check =	this.messageBox("��Ϣ", "�˲�����ԤԼ"+bedNo+"����,�Ƿ�Ժ���ס?", 0) ;
		    if(check!=0){
		    	String updatesql = "UPDATE SYS_BED SET APPT_FLG = 'N'  WHERE BED_NO_DESC = '"+bedNo+"'"  ;
				TParm bedParm = new TParm(TJDODBTool.getInstance().update(updatesql)) ;
				if (bedParm.getErrCode() < 0) { 
					this.messageBox("E0005");
					return;
				} 
				return ;
		    }
		    	
		    else {
		    	for(int i=0 ;i<inTable.getRowCount();i++){
		    		if(inTable.getValueAt(i, 3).equals(bedNo))//2-->3
		    			selectRow = i ;
		    	}
		    	this.onCheckin(selectRow) ;
		    }
		    	
		}
	
	}

	/**
	 * ���Ų����봲
	 */
	public void onCheckin(int selectRow) {
		// �õ���ת��table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// �õ���ת��DS
		TDataStore ds = waitIn.getDataStore();
		// �õ���Ժ����table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		checkIn.setSelectedRow(selectRow);
		int waitIndex = waitIn.getSelectedRow();// ��ת���ѡ���к�
		if (waitIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĳ���!");
			return;
		}
		int checkIndex = checkIn.getSelectedRow();// ��Ժ�����б�ѡ���к�
		if (checkIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĵ�λ!");
			return;
		}
		
		String mrNo=ds.getItemString(waitIndex, "MR_NO");
		String sql="SELECT EXEC_FLG FROM ADM_PRETREAT  WHERE MR_NO = '"+mrNo+"'";
		TParm data=new TParm(TJDODBTool.getInstance().select(sql));
		sql ="SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
		TParm data1=new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(data.getValue("EXEC_FLG",0))){
			if(!mrNo.equals(in.getParmValue().getValue("PRE_MRNO",in.getSelectedRow()))){
				if(JOptionPane.showConfirmDialog(null, "�ô�λ����ԤԼ��λ�Ƿ������", "��Ϣ",
	    				JOptionPane.YES_NO_OPTION) == 0){//ѡ��Ĵ�λ��ԤԼ�Ĵ�λ����ͬ���� ��ʾ
				}else{
					return;
				}
			}
		}
		String selectSql=" SELECT B.SEX_CODE FROM SYS_BED A,SYS_PATINFO B WHERE " +
		" A.ROOM_CODE='"+in.getParmValue().getValue("ROOM_CODE",in.getSelectedRow())+"' " +
		" AND A.MR_NO IS NOT NULL AND A.MR_NO=B.MR_NO ";
		TParm parm=new TParm(TJDODBTool.getInstance().select(selectSql));
		if(parm.getCount()>0){
			for(int i=0;i<parm.getCount();i++){
				if(!data1.getValue("SEX_CODE",0).equals(parm.getValue("SEX_CODE",i))){
					if(JOptionPane.showConfirmDialog(null, "�Ա���ͬ���Ƿ������", "��Ϣ",
		    				JOptionPane.YES_NO_OPTION) == 0){
						
					}else{
						return;
					}
				}
			}
		}
		
		//ˢ�¼��
		if(!check()){// shibl 20130117 add 
			return;
		}
		// �˻��ߴ�ת�벡�����Ǳ�����
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("�˻��ߴ�ת�벡�����Ǳ�����");
			return;
		}
		// �õ���ת��ѡ���е�����
		TParm updata = new TParm();
		// ��λ��
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 0));
		// ԤԼע��
		updata.setData("APPT_FLG", "N");
		// ռ��ע��
		updata.setData("ALLO_FLG", "Y");
		// ��ת�벡����
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// ��ת������
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// ��ת��סԺ��
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// ռ��ע��
		updata.setData("BED_STATUS", "1");
		// ��λ���ڲ���
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		// ��λ��
		updata.setData("BED_NO",checkIn.getParmValue().getValue("BED_NO",checkIn.getSelectedRow()));
				//checkIn.getValueAt(checkIn.getValue(), 1));
		// ����
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex, "IN_DEPT_CODE"));
		// dataStore

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// ��鲡���Ƿ����
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// ��ʾ�ò������й���������
			// ����ò����а��� ��ô�жϲ�����ס�Ĵ�λ�ǲ��Ǹò���ָ���Ĵ�λ���������Ҫ�������ѣ�������Ϣ�ᱻȡ��
			// ���ת��Ĵ�λ��MR_NOΪ�ջ����벡����MR_NO����ͬ ��ʾ�ô�λ���ǲ���ָ���Ĵ�λ
			if (checkIn.getValueAt(checkIndex, 3) == null
					|| "".equals(checkIn.getValueAt(checkIndex, 5))//3-->5
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 3)//2-->3
									.toString())) {
				int check = this.messageBox("��Ϣ",
						"�˲����Ѱ���������סָ����λ��ȡ���ò����İ������Ƿ������", 0);
				if (check != 0) {
					return;
				}
				updata.setData("CHANGE_FLG", "Y");// ��ʾ��������ס��ָ����λ����ոò����İ�����Ϣ
			} else {
				updata.setData("CHANGE_FLG", "N");// ��ʾ������ס��ָ����λ
			}
		} else {
			updata.setData("OCCU_FLG", "N");// ��ʾ�ò���û�а���
		}
	    String caseNo = ds.getItemString(waitIndex, "CASE_NO"); // wanglong add 20140731
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());
		// =========pangben modify 20110617 start
		updata.setData("REGION_CODE", Operator.getRegion());
//		System.out.println("----------------updata----------"+updata);
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // �봲����
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		}
		else {
			this.messageBox("P0005");
			
			//���´�λ״̬
			String upsql="UPDATE SYS_BED SET APPT_FLG='N' ,BED_STATUS='1'," +
					" PRETREAT_DATE='',PRE_MRNO=''," +
					" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO=''" +
					" WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",in.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(upsql));
			upsql="UPDATE ADM_PRETREAT SET EXEC_FLG ='Y' WHERE MR_NO='"+mrNo+"'";
			new TParm(TJDODBTool.getInstance().update(upsql));
			initInStation();
            // ���Խӿ�[A01(��Ժ)��A02(ת��)��A03(��Ժ)] wanglong add 20140731
            TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A02");
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            // �������ӿ� wanglong add 20141010
            xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
			sendHL7Mes(updata);
			initInStation();
			chose();
		}
	}
	/**
	 * ����������Ϣ
	 */
	public void onReload() {
		pageInit();
	}

	/**
	 * ��ѯ�������������Ա𣬳�������
	 */
	public void onQuery() {
		TParm parm = new TParm();
		// =============pangben modify 20110512 start ��������ѯ
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		// ===pangben modify ��Ӳ���
		patInfo = ADMWaitTransTool.getInstance().selpatInfo(parm); // ��ת��Ĳ�����Ϣ
		admPat = ADMWaitTransTool.getInstance().selAdmPat(parm); // ��Ժ�Ĳ�����Ϣ
	}

	/**
	 * ��ת��ת�� ����combo ��ѡ�¼�
	 */
	public void chose() {
		this.onSelectIn();
		this.onSelectOut();
	}

	/**
	 * ��ת��ת��TABLE ��ʾ
	 *
	 * @param tag
	 *            String
	 */
	public void creatDataStore(String tag) {
		Pat pat = null;
		TParm parm = new TParm();
		TParm result = new TParm();
		if (patInfo == null)
			return;
		TTable table = (TTable) this.callFunction("UI|" + tag + "|getThis");
		String mrNo = "";//
		String caseNo = "";//
		//System.out.println("row count===="+table.getRowCount());
		Timestamp date=SystemTool.getInstance().getDate() ;   //=======  chenxi modify 20130228
		/**
		 * ѭ��table ��ʾ�����������Ա�����
		 */
		for (int i = 0; i < table.getRowCount(); i++) {
			// �õ�table�е�ֵ
			mrNo = table.getValueAt(i, 0).toString().trim();
			caseNo = table.getValueAt(i, 1).toString().trim();
			parm = new TParm();
			result = new TParm();
			pat = new Pat();
			// �õ�pat�����õ�����
			pat = pat.onQueryByMrNo(mrNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("CASE_NO", caseNo);
			result = ADMInpTool.getInstance().selectBedNo(parm) ;
			// �õ���������
			String[] AGE = StringTool.CountAgeByTimestamp(pat.getBirthday(),
					date);
			//=================  chenxi modify 20130228
			// ��table��ֵ
			if(tag.equals("WAIT_IN")){
			table.setValueAt(pat.getName(), i, 2);
			table.setValueAt(pat.getSexCode(), i, 3);
			table.setValueAt(result.getValue("BED_NO_DESC", 0), i, 4);   //=====ԤԼ����
			table.setValueAt(AGE[0], i, 5);
			}
			else {
			table.setValueAt(pat.getName(), i, 3);
			table.setValueAt(pat.getSexCode(), i, 4);
			table.setValueAt(AGE[0], i, 5);}

		}
	}

	/**
	 * ��Ժ������ѡ�¼�
	 */
	public void onInStation() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("��ѡ�񲡻���");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲����ǰ�������ѡ�񲡻�ʵס������");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲���δ��ס��");
			return;
		}
		TParm parm = table.getParmValue();
		TParm sendParm = new TParm();
		// ���
		sendParm.setData("ADM", "ADM");
		// ������
		sendParm.setData("MR_NO", parm.getData("MR_NO", selectRow));
		// סԺ��
		sendParm.setData("IPD_NO", parm.getData("IPD_NO", selectRow));
		// �����
		sendParm.setData("CASE_NO", parm.getData("CASE_NO", selectRow));
		// ����
		sendParm.setData("PAT_NAME", parm.getData("PAT_NAME", selectRow));
		// �Ա�
		sendParm.setData("SEX_CODE", parm.getData("SEX_CODE", selectRow));
		// ����
		sendParm.setData("AGE", parm.getData("AGE", selectRow));
		// ����
		sendParm.setData("BED_NO", parm.getData("BED_NO", selectRow));
		// ����
		sendParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		// ����
		sendParm.setData("STATION_CODE",
				parm.getData("STATION_CODE", selectRow));
		// ����ҽʦ
		sendParm.setData("VS_DR_CODE", parm.getData("VS_DR_CODE", selectRow));
		// ����ҽʦ
		sendParm.setData("ATTEND_DR_CODE",
				parm.getData("ATTEND_DR_CODE", selectRow));
		// ������
		sendParm.setData("DIRECTOR_DR_CODE",
				parm.getData("DIRECTOR_DR_CODE", selectRow));
		// ���ܻ�ʿ
		sendParm.setData("VS_NURSE_CODE",
				parm.getData("VS_NURSE_CODE", selectRow));
		// ��Ժ״̬
		sendParm.setData("PATIENT_CONDITION",
				parm.getData("PATIENT_CONDITION", selectRow));
		// ������
		sendParm.setData("DIRECTOR_DR_CODE",
				parm.getData("DIRECTOR_DR_CODE", selectRow));
		// BED_OCCU_FLG
		sendParm.setData("BED_OCCU_FLG",
				parm.getData("BED_OCCU_FLG", selectRow));
		// ���水ť״̬
		sendParm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		
		sendParm.setData("PRETREAT_OUT_NO", in.getParmValue().getValue("PRETREAT_OUT_NO",in.getSelectedRow()));//Ԥת��
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\AdmPatinfo.x", sendParm); 
		initInStation();
		
	}
	
	/**
	 * ��Ժ������Ϣ��ѯ
	 */
	public void initInStation() {
		TParm parm = new TParm();
		parm.setData("STATION_CODE", getValue("STATION_CODE").toString()==null?"":getValue("STATION_CODE").toString());
		//==================shibl
//		parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString()==null?"":getValue("DEPT_CODE").toString());
		// =============pangben modify 20110512 start ��Ӳ���
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
//		System.out.println("-----------parm-----"+parm);
		admInfo = ADMInpTool.getInstance().queryInStation(parm);
//		System.out.println("-------admInfo--1------------"+admInfo);
		if (admInfo.getErrCode() < 0) {
			this.messageBox_(admInfo.getErrText());
			return;
		}
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		// �������û��ס�� �������0ȥ��
		for (int i = 0; i < admInfo.getCount(); i++) {

			if (admInfo.getInt("AGE", i) == 0) {
				// ���ס�в������Ҵ˲������ǰ��� ����Ϊ0�� ��ô�Զ���һ
				if (admInfo.getValue("MR_NO", i).length() > 0
						&& !admInfo.getValue("BED_STATUS", i).equals("3"))
					admInfo.setData("AGE", i, "1");
				else
					// û�в����ڴ���0��Ϊ��
					admInfo.setData("AGE", i, "");
			} else {
				// �õ���������========pangb 2011-11-18 �������һ��
				String[] AGE = StringTool.CountAgeByTimestamp(
						admInfo.getTimestamp("BIRTH_DATE", i),
						admInfo.getTimestamp("IN_DATE", i));
				admInfo.setData("AGE", i, AGE[0]);
			}
			if (admInfo.getData("IN_DATE", i) != null
					&& admInfo.getValue("MR_NO", i).length() > 0
					&& !admInfo.getValue("BED_STATUS", i).equals("3")) {
				int days = StringTool.getDateDiffer(SystemTool.getInstance()
						.getDate(), admInfo.getTimestamp("IN_DATE", i));
				if (days > 0) {
					admInfo.setData("DAYNUM", i, days);
				} else {
					admInfo.setData("DAYNUM", i, "1");
				}
			} else
				admInfo.setData("DAYNUM", i, "");

		}
//		System.out.println("-------admInfo--------------"+admInfo);
		//table.setParmValue(admInfo);
		tableColor(admInfo);
		//ͳ�ƴ�λ
		int nullBed=0;
		int notNullBed=0;
		for(int i=0;i<admInfo.getCount();i++){
			if("0".equals(admInfo.getValue("BED_STATUS",i))){
				nullBed++;
			}else{
				notNullBed++;
			}
		}
		
		this.setValue("FILL_NUM", notNullBed+"");//ռ��
		this.setValue("NULL_NUM", nullBed+"");//�մ�
		initPre();
	}
	/**
	 * ԤԼ���ݲ�ѯ
	 */
	public void initPre(){
		String date=SystemTool.getInstance().getDate().toString().substring(0,19).replaceAll("-", "/");
		TParm inParam=new TParm();
		TParm outParm=new TParm();
		if (this.getValue("STATION_CODE") != null
				&& !"".equals(this.getValue("STATION_CODE"))) {
			inParam.setData("PRETREAT_IN_STATION", this
					.getValue("STATION_CODE"));
			outParm.setData("PRETREAT_OUT_STATION", this
					.getValue("STATION_CODE"));
		}
		
		if (this.getValue("PRE_DATE") != null
				&& !"".equals(this.getValue("PRE_DATE"))) {
			inParam.setData("START_DATE", date);
			inParam.setData("END_DATE", this.getValue("PRE_DATE").toString().substring(0,19).replaceAll("-", "/"));
			outParm.setData("START_DATE", date);
			outParm.setData("END_DATE", this.getValue("PRE_DATE").toString().substring(0,19).replaceAll("-", "/"));
			
		}
		//inParam.setData("EXEC_FLG","N");
		initPreIn(inParam);
		initPreOut(outParm);
	}
	/**
	 * ԤԼ���ݲ�ѯ
	 */
	public void initPreIn(TParm parm){
		TParm result = ADMResvTool.getInstance().queryPretreat(parm);
		for(int i=0;i<result.getCount();i++){
			result.setData("AGE",i,this.patAge(result.getTimestamp("BIRTH_DATE",i)));
		}
		
		String sql="SELECT MR_NO  FROM ADM_WAIT_TRANS WHERE IN_STATION_CODE='"+this.getValueString("STATION_CODE")+"'";
		TParm countParm=new TParm(TJDODBTool.getInstance().select(sql));
		int inNum=(result.getCount()>0?result.getCount():0)+(countParm.getCount()>0?countParm.getCount():0);
		this.setValue("PRE_IN_NUM", inNum+"");
		inTable.setParmValue(result);
	}
	/**
	 * Ԥת���ݲ�ѯ
	 */
	public void initPreOut(TParm parm){
		TParm result = ADMResvTool.getInstance().queryPretreat(parm);
		for(int i=0;i<result.getCount();i++){
			result.setData("AGE",i,this.patAge(result.getTimestamp("BIRTH_DATE",i)));
		}
		outTable.setParmValue(result);
		
		if(result.getCount()>0){
			this.setValue("PRE_OUT_NUM", result.getCount()+"");//��ת��
		}else{
			this.setValue("PRE_OUT_NUM", 0+"");//��ת��
		}
	}
	/**
	 * ��ת�����COMBO��ѡ�¼�
	 */
	public void onSelectIn() {
		//=========modify lim 20120323 begin
//		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
//		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", ""));
//		WAIT_IN.retrieve();		
		//=========modify lim 20120323 end
		
		String filter = "";
		if (this.getValueString("IN_STATION_CODE").length() > 0)
			filter += " IN_STATION_CODE ='" + this.getValueString("IN_STATION_CODE")
					+ "'";
		if (this.getValueString("IN_DEPT_CODE").length() > 0)
			filter += " AND IN_DEPT_CODE ='"
					+ this.getValueString("IN_DEPT_CODE") + "'";
		TTable table = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		table.getDataStore().showDebug();
		creatDataStore("WAIT_IN");

	}

	/**
	 * ��ת������COMBO��ѡ�¼�
	 */
	public void onSelectOut() {
		String filter = "";
		if (this.getValueString("OUT_STATION_CODE").length() > 0)
			filter += " OUT_STATION_CODE ='"
					+ this.getValueString("OUT_STATION_CODE") + "'";
		if (this.getValueString("OUT_DEPT_CODE").length() > 0)
			filter += " AND OUT_DEPT_CODE ='"
					+ this.getValueString("OUT_DEPT_CODE") + "'";

		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		creatDataStore("WAIT_OUT");
	}

	/**
	 * ���Ų����봲
	 */
	public void onCheckin() {
		// �õ���ת��table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// �õ���ת��DS
		TDataStore ds = waitIn.getDataStore();
		// �õ���Ժ����table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		int waitIndex = waitIn.getSelectedRow();// ��ת���ѡ���к�
		if (waitIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĳ���!");
			return;
		}
		int checkIndex = checkIn.getSelectedRow();// ��Ժ�����б�ѡ���к�
		if (checkIndex < 0) {
			this.messageBox_("��ѡ��Ҫ��ס�Ĵ�λ!");
			return;
		}
		
		String mrNo=ds.getItemString(waitIndex, "MR_NO");
		String sql="SELECT EXEC_FLG FROM ADM_PRETREAT  WHERE MR_NO = '"+mrNo+"'";
		TParm data=new TParm(TJDODBTool.getInstance().select(sql));
		sql ="SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
		TParm data1=new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(data.getValue("EXEC_FLG",0))){
			if(!mrNo.equals(in.getParmValue().getValue("PRE_MRNO",in.getSelectedRow()))){
				if(JOptionPane.showConfirmDialog(null, "�ô�λ����ԤԼ��λ�Ƿ������", "��Ϣ",
	    				JOptionPane.YES_NO_OPTION) == 0){//ѡ��Ĵ�λ��ԤԼ�Ĵ�λ����ͬ���� ��ʾ
				}else{
					return;
				}
			}
		}
		String selectSql=" SELECT B.SEX_CODE FROM SYS_BED A,SYS_PATINFO B WHERE " +
		" A.ROOM_CODE='"+in.getParmValue().getValue("ROOM_CODE",in.getSelectedRow())+"' " +
		" AND A.MR_NO IS NOT NULL AND A.MR_NO=B.MR_NO ";
		TParm parm=new TParm(TJDODBTool.getInstance().select(selectSql));
		if(parm.getCount()>0){
			for(int i=0;i<parm.getCount();i++){
				if(!data1.getValue("SEX_CODE",0).equals(parm.getValue("SEX_CODE",i))){
					if(JOptionPane.showConfirmDialog(null, "�Ա���ͬ���Ƿ������", "��Ϣ",
		    				JOptionPane.YES_NO_OPTION) == 0){
						break;
					}else{
						return;
					}
					
				}
			}
		}
		
		
		
		//ˢ�¼��
		if(!check()){// shibl 20130117 add 
			return;
		}
		// �˻��ߴ�ת�벡�����Ǳ�����
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("�˻��ߴ�ת�벡�����Ǳ�����");
			return;
		}
		//========================  chenxi modify 20130228 
//		// �õ���ת���벡������
//		if ("1".equals(checkIn.getValueAt(checkIn.getSelectedRow(), 0))) {
//			this.messageBox_("�˴���ռ�ã�");
//			return;
//		}
//		// �ж�ѡ�еĴ����Ƿ��Ѿ���Ԥ��
//		if (admInfo.getBoolean("APPT_FLG", checkIndex)) {
//			int check = this.messageBox("��Ϣ", "�˴��ѱ�Ԥ�����Ƿ��������", 0);
//			if (check != 0) {
//				return;
//			}
//		}
//		// �ж�ѡ�еĴ����Ƿ񱻰���
//		if (admInfo.getBoolean("BED_OCCU_FLG", checkIndex)) {
//			this.messageBox_("�˴�λ�ѱ�������������ס��");
//			return;
//		}
		// �õ���ת��ѡ���е�����
		TParm updata = new TParm();
		// ��λ��
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 0));//????
		// ԤԼע��
		updata.setData("APPT_FLG", "N");
		// ռ��ע��
		updata.setData("ALLO_FLG", "Y");
		// ��ת�벡����
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// ��ת������
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// ��ת��סԺ��
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// ռ��ע��
		updata.setData("BED_STATUS", "1");
		// ��λ���ڲ���
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		// ��λ��
		updata.setData("BED_NO",checkIn.getParmValue().getValue("BED_NO",checkIn.getSelectedRow()));
				//checkIn.getValueAt(checkIn.getSelectedRow(), 1));
		// ����
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex, "IN_DEPT_CODE"));
		// dataStore

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// ��鲡���Ƿ����
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// ��ʾ�ò������й���������
			// ����ò����а��� ��ô�жϲ�����ס�Ĵ�λ�ǲ��Ǹò���ָ���Ĵ�λ���������Ҫ�������ѣ�������Ϣ�ᱻȡ��
			// ���ת��Ĵ�λ��MR_NOΪ�ջ����벡����MR_NO����ͬ ��ʾ�ô�λ���ǲ���ָ���Ĵ�λ
			if (checkIn.getValueAt(checkIndex, 5) == null//3-->5
					|| "".equals(checkIn.getValueAt(checkIndex, 5))//3-->5
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 3)//2-->3
									.toString())) {
				int check = this.messageBox("��Ϣ",
						"�˲����Ѱ���������סָ����λ��ȡ���ò����İ������Ƿ������", 0);
				if (check != 0) {
					return;
				}
				updata.setData("CHANGE_FLG", "Y");// ��ʾ��������ס��ָ����λ����ոò����İ�����Ϣ
			} else {
				updata.setData("CHANGE_FLG", "N");// ��ʾ������ס��ָ����λ
			}
		} else {
			updata.setData("OCCU_FLG", "N");// ��ʾ�ò���û�а���
		}
        String caseNo = ds.getItemString(waitIndex, "CASE_NO"); // wanglong add 20140731
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());
		// =========pangben modify 20110617 start
		updata.setData("REGION_CODE", Operator.getRegion());
//		System.out.println("----------------updata----------"+updata);
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // �봲����
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		} 
		else {
			this.messageBox("P0005");
			
			
			
			//���´�λ״̬
			String upsql="UPDATE SYS_BED SET APPT_FLG='N' ,BED_STATUS='1'," +
					" PRETREAT_DATE='',PRE_MRNO=''," +
					" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO=''" +
					" WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",in.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(upsql));
			upsql="UPDATE ADM_PRETREAT SET EXEC_FLG ='Y' WHERE MR_NO='"+mrNo+"'";
			new TParm(TJDODBTool.getInstance().update(upsql));
			initInStation();
			
            // ���Խӿ�[A01(��Ժ)��A02(ת��)��A03(��Ժ)] wanglong add 20140731
			TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A02");
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            // �������ӿ� wanglong add 20141010
            xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            sendHL7Mes(updata);
            initInStation();
			chose();
		}
	}
	
	/**
	 * ���ĳһ�����Ƿ����
	 *
	 * @param caseNo
	 *            String
	 * @return boolean true������ false��δ����
	 */
	public boolean checkOccu(String caseNo) {
		TParm qParm = new TParm();
		qParm.setData("CASE_NO", caseNo);
		TParm occu = SYSBedTool.getInstance().queryAll(qParm);
		int count = occu.getCount("BED_OCCU_FLG");
		String check = "N";
		for (int i = 0; i < count; i++) {
			if ("Y".equals(occu.getData("BED_OCCU_FLG", i))) {
				check = "Y";
			}
		}
		if ("Y".equals(check)) {
			return true;
		} else {
			return false;
		}
	}
	/**
     * ��˴�λ
     * @return boolean
     */
    public boolean check() {
        TTable table = (TTable)this.callFunction("UI|in|getThis");
        TTable waitTable = (TTable)this.callFunction("UI|WAIT_IN|getThis");  //chenxi modify 20130308
        if (table.getSelectedRow() < 0) {
            this.messageBox("δѡ��λ");
            return false;
        }
        //=============shibl 20130106 add======���˵�ͬһ����δˢ��ҳ��=============================
        TParm  parm=table.getParmValue().getRow(table.getSelectedRow());
        TParm  inParm=new TParm();
        inParm.setData("BED_NO", parm.getValue("BED_NO"));
        TParm result = ADMInpTool.getInstance().QueryBed(inParm);
        String APPT_FLG=result.getCount()>0?result.getValue("APPT_FLG",0):"";
        String ALLO_FLG=result.getCount()>0?result.getValue("ALLO_FLG",0):"";
        String BED_STATUS=result.getCount()>0?result.getValue("BED_STATUS",0):"";
        if (ALLO_FLG.equals("Y")) {
            this.messageBox("�˴���ռ��");
            onReload();
            return false;
        }
        if (BED_STATUS.equals("1")) {
            this.messageBox("�˴��ѱ�����");
            onReload();
            return false;
        }
        //=================  chenxi modify 20130308
        if (APPT_FLG.equals("Y")) {
        	if(!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(parm.getValue("BED_NO_DESC"))){
        		int check = this.messageBox("��Ϣ", "�˴��ѱ�Ԥ�����Ƿ��������", 0);
    			if (check != 0) {
    				onReload();
    				return  false;
    			}
                return true;
        	}
        	
        }
        return true;
    }
	/**
	 * ������ס���
	 *
	 * @return boolean
	 */
	public boolean checkSysBed() {
		// �õ���ת��table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// �õ���Ժ����table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		String waitMr = waitIn.getValueAt(waitIn.getSelectedRow(), 0)
				.toString();
		String bedMr = checkIn.getValueAt(checkIn.getSelectedRow(), 5) == null ? ""//3-->5
				: checkIn.getValueAt(checkIn.getSelectedRow(), 5).toString();//3-->5
		if (bedMr == null || "".equals(bedMr))
			return true;
		else if (waitMr.equals(bedMr)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ת�ƹ���
	 */
	public void onOutDept() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		if (selectRow == -1) {
			this.messageBox("��ѡ����Ժ������");
			return;
		}
		if (table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox("��ѡ����Ժ������");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲����ǰ�������ѡ�񲡻�ʵס������");
			return;
		}

		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox("�˲���δ��ס��");
			return;
		}
		TParm parm = table.getParmValue();
		TParm sendParm = new TParm();
		// ������
		sendParm.setData("MR_NO", parm.getData("MR_NO", selectRow));
		// סԺ��
		sendParm.setData("IPD_NO", parm.getData("IPD_NO", selectRow));
		// �����
		sendParm.setData("CASE_NO", parm.getData("CASE_NO", selectRow));
		// ����
		sendParm.setData("PAT_NAME", parm.getData("PAT_NAME", selectRow));
		// �Ա�
		sendParm.setData("SEX_CODE", parm.getData("SEX_CODE", selectRow));
		// ����
		sendParm.setData("AGE", parm.getData("AGE", selectRow));
		// ����
		sendParm.setData("BED_NO", parm.getData("BED_NO", selectRow));
		// ����
		sendParm.setData("OUT_DEPT_CODE", parm.getData("DEPT_CODE", selectRow));
		// ����
		sendParm.setData("OUT_STATION_CODE",
				parm.getData("STATION_CODE", selectRow));
		// ��Ժʱ��
		sendParm.setData("IN_DATE", parm.getData("IN_DATE", selectRow));
		sendParm.setData("PRE_FLG",parm.getData("PRE_FLG", selectRow));//����Ԥת���
		//sendParm.setData("BED_NO",parm.getData("BED_NO", selectRow));//���봲��
		sendParm.setData("PRETREAT_OUT_NO",parm.getData("PRETREAT_OUT_NO", selectRow));//��ȡԤת�ǼǺ�ʱ
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMOutInp.x", sendParm);
		initInStation();
		TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		//System.out.println("=======WAIT_OUT======"+outTable.getFilter());
		//$$=========add by lx===========$$//
		//outTable.retrieve();
		//onSelectOut();
		outTable.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT(this.getValueString("OUT_DEPT_CODE"), this.getValueString("OUT_STATION_CODE")));
		outTable.retrieve();
		//$$==================$$//
		creatDataStore("WAIT_OUT");
	}

	/**
	 * ��������
	 */
	public void onBed() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("��ѡ�񲡻���");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲���δ��ס��");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲����ǰ�������ѡ�񲡻�ʵס������");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", admInfo.getValue("CASE_NO", selectRow));
		sendParm.setData("MR_NO", admInfo.getValue("MR_NO", selectRow));
		sendParm.setData("IPD_NO", admInfo.getValue("IPD_NO", selectRow));
		sendParm.setData("DEPT_CODE", admInfo.getValue("DEPT_CODE", selectRow));
		sendParm.setData("STATION_CODE",
				admInfo.getValue("STATION_CODE", selectRow));
		sendParm.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm bed = new TParm();
		bed.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = admInfo.getValue("CASE_NO", selectRow);
		int count = check.getCount("BED_NO");
		boolean flg = false ;
		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				flg = true ;
		}
			}
		if(flg==true){
			int checkFlg=	this.messageBox("��Ϣ","�˲���������������!�Ƿ����������",0);
			if(checkFlg!=0)
				return;
			}	
		
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);
		initInStation();
		chose();
	}

	/**
	 * ��ʼ������Ĭ�Ͽ��� ����
	 */
	public void onInit_Dept_Station() {
		String userId = Operator.getID();
		String station = Operator.getStation();
		String dept = Operator.getDept();
		TComboBox admstation = (TComboBox) this.getComponent("STATION_CODE");
		TParm Station = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		admstation.setParmValue(Station);
		admstation.onQuery();
		TComboBox admdept = (TComboBox) this.getComponent("DEPT_CODE");
		TParm Dept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		admdept.setParmValue(Dept);
		admdept.onQuery();
		TComboBox in_station = (TComboBox) this.getComponent("IN_STATION_CODE");
		TParm inStation = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		in_station.setParmValue(inStation);
		in_station.onQuery();
		TComboBox out_station = (TComboBox) this.getComponent("OUT_STATION_CODE");
		//===========modify lim  begin
		//TParm outStaion = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserStationList(userId)));
		TParm outStaion = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserStationListForDynaSch()));
		//===========modify lim  end
		out_station.setParmValue(outStaion);
		out_station.onQuery();
		TComboBox in_dept = (TComboBox) this.getComponent("IN_DEPT_CODE");
		TParm inDept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserDeptList(userId)));
		in_dept.setParmValue(inDept);
		in_dept.onQuery();
		TComboBox out_dept = (TComboBox) this.getComponent("OUT_DEPT_CODE");
		TParm outDept = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserDeptList(userId)));
		//TParm outDept = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserDeptListForDynaSch()));
		out_dept.setParmValue(outDept);
		out_dept.onQuery();
		// ��ת��ʹ�ת�� Grid ��ֵ
		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		TTable WAIT_OUT = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", station));
		WAIT_IN.retrieve();
		WAIT_OUT.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT("", ""));
		WAIT_OUT.retrieve();
		// �����û�����Ĭ�Ͽ��ҺͲ���
		setValue("IN_DEPT_CODE", "");
		setValue("DEPT_CODE", dept);
		setValue("OUT_DEPT_CODE", "");
		setValue("IN_STATION_CODE", station);
		setValue("STATION_CODE", station);
		setValue("OUT_STATION_CODE", station);
	}

	/**
	 * ȡ������
	 */
	public void onCancelBed() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("��ѡ�񲡻���");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("�˲���δ��ס��");
			return;
		}
		int re = this.messageBox("��ʾ", "ȷ��Ҫȡ���ò����İ�����", 0);
		if (re != 0) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", admInfo.getValue("CASE_NO", selectRow));
		TParm result = SYSBedTool.getInstance().clearOCCUBed(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		initInStation();
		chose();
	}

    //$$==========liuf==========$$//
	/**
	 * CIS��Ѫ�ǲ�����ס����
	 * @param parm
	 */
	private void sendHL7Mes(TParm parm) {
		System.out.println("sendHL7Mes()");
		// ICU��CCUע��
		String caseNO = parm.getValue("CASE_NO");		
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		String type="ADM_IN";
		parm.setData("ADM_TYPE", "I");
		//CIS
		if (IsICU||IsCCU)
		{ 
		  List list = new ArrayList();
		  parm.setData("SEND_COMP", "CIS");
		  list.add(parm);
		  TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		  if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		////////////////////////////////////////////zhangs add start
		//��Һ��
		System.out.println("��Һ��:"+this.checkIsCS5(caseNO));
		if (this.checkIsCS5(caseNO))
		{ 
		  List list = new ArrayList();
		  parm.setData("SEND_COMP", "CS5");
		  list.add(parm);
		  TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		  if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		///////////////////////////////////////////////zhangs add end
		//Ѫ��
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");	
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,type);
		if (resultParm.getErrCode() < 0)
		  messageBox(resultParm.getErrText());

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
		
		//add by lij 2017/04/11 ���� NIS��HL7��Ϣ
//		this.messageBox("1111");
		list = new ArrayList();
		parm.setData("SEND_COMP", "NIS");
		list.add(parm);
		resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
		if (resultParm.getErrCode() < 0) {
			messageBox(resultParm.getErrText());
		}
//		this.messageBox("2222");
	} 
	  //$$==========liuf==========$$//
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
	 * ȡ��ת��
	 * @param parm 
	 */	
	public void onCancelTrans(){ 
		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		int selectRow = table.getSelectedRow();
		if(selectRow<0){
			this.messageBox("��ѡ��Ҫȡ��ת�ƵĲ���.");
			return ;
		}
		String caseNo = (String)table.getValueAt(selectRow, 2) ;
		TParm parm = new TParm() ;
		parm.setData("OPT_USER",Operator.getID()) ;
		parm.setData("OPT_TERM",Operator.getIP()) ;
		parm.setData("DATE",SystemTool.getInstance().getDate()) ;
		parm.setData("CASE_NO", caseNo);

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onUpdateTransAndLog", parm); 	
		if(result.getErrCode()<0){
			messageBox("ȡ��ת��ʧ��.") ;
		}else{
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");	
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");			
			messageBox("ȡ��ת�Ƴɹ�.") ;
			
			// add by wangb 2015/11/27 ȡ��ת�Ʒ��ʹ�����Ϣ START
            TParm xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            // add by wangb 2015/11/27 ȡ��ת�Ʒ��ʹ�����Ϣ END
		}
	}
	/**
	 * ȡ��סԺ         chenxi   modify  20130417
	 */
	public void onCancelInHospital(){
		 if (!checkDate())
	            return;
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		TParm  tableParm = table.getParmValue() ;
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString() ; 
		TParm result = new TParm();
		//=================ִ��ȡ��סԺ����
      int check = this.messageBox("��Ϣ", "�Ƿ�ȡ����", 0);
      if (check == 0) {
          TParm parm = new TParm();
          parm.setData("CASE_NO", caseNo);
          parm.setData("PSF_KIND", "INC");
          parm.setData("PSF_HOSP", "");
          parm.setData("CANCEL_FLG", "Y");
          parm.setData("CANCEL_DATE", SystemTool.getInstance().getDate());
          parm.setData("CANCEL_USER", Operator.getID());
          parm.setData("OPT_USER", Operator.getID());
          parm.setData("OPT_TERM", Operator.getIP());
          if (null != Operator.getRegion()
              && Operator.getRegion().length() > 0) {
              parm.setData("REGION_CODE", Operator.getRegion());
          }
         result = TIOM_AppServer.executeAction(
                  "action.adm.ADMInpAction", "ADMCanInp", parm); //
          if (result.getErrCode() < 0) {
              this.messageBox("E0005");
          } else {
              this.messageBox("P0005");
                // ���Խӿ� wanglong add 20140731
                TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(caseNo, "A04");
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
                }
                xmlParm = ADMXMLTool.getInstance().creatXMLFile(caseNo);
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("��Ϣ����ӿڷ���ʧ�� " + xmlParm.getErrText());
                }
                // �������ӿ� wanglong add 20141010
                xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
                }
                xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
                }
              initInStation();
              chose();
          }
	}
	}
	  /**
     * �������
     *
     * @return boolean
     */
    public boolean checkDate() {
    	TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("��ѡ�񲡻���");
			return false;
		}
		TParm  tableParm = table.getParmValue() ;
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString() ; 
		//==============Ԥ����δ��,����ȡ��סԺ
		 TParm result = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrName()) ;
            return false;
        }
        if (result.getDouble("PRE_AMT", 0) > 0) {
        	 this.messageBox("�˲�������Ԥ����δ��,����ȡ��סԺ");
            return false;
        }
        //==================�ԼƷѲ���ȡ��סԺ
        boolean checkflg =  IBSOrdermTool.getInstance().existFee(tableParm.getRow(selectRow));
        if(!checkflg){
      	  messageBox("�Ѳ�������,����ȡ��סԺ") ;
      	  return false;
        }
        // ���ҽ���Ƿ���ҽ��
     TParm    parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        if (this.checkOrderisEXIST(parm)) {    
        	this.messageBox( "�ò����ѿ���ҽ��������ȡ��סԺ��");
        	  callFunction("UI|save|setEnabled", false);
        	  return false  ;     
        }
      
    	return true ;
    }
    /**
	 * ���ò����Ƿ���ҽ��
	 * 
	 * 
	 */
	public boolean checkOrderisEXIST(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND DC_DATE IS NULL ";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		// ���û��Ϊִ�е����ݷ������ݼ�����Ϊ0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * ȡ�����
	 */
	public void  onCancleInDP(){
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		if (selectRow < 0 || table.getValueAt(selectRow, 5) == null) {//3-->5
			this.messageBox_("��ѡ�񲡻���");
			return ;
		}
		TParm  tableParm = table.getParmValue() ;
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString() ;
		TParm parm=new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm tranLogDept = ADMTransLogTool.getInstance().getTranDeptData(parm);
		if(tranLogDept.getCount()<=0){
			this.messageBox("��ѯת�Ƽ�¼����");
			return;
		}
		if(!isEnableCancleInDP(tranLogDept.getRow(0))){
			return;
		}
		TParm trandParm=tableParm.getRow(selectRow);
		trandParm.setData("OPT_USER", Operator.getID());
		trandParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "onCancleInDP", trandParm);
		if(result.getErrCode()<0){
			this.messageBox("ִ��ʧ��");
			return;
		}else{
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");	
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");	
		    this.messageBox("ִ�гɹ�");
		    
		    // add by wangb 2015/11/27 ȡ����Ʒ��ʹ�����Ϣ START
            TParm xmlParm = ADMXMLTool.getInstance().creatDeptXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
            if (xmlParm.getErrCode() < 0) {
                this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
            }
            // add by wangb 2015/11/27 ȡ��ת�Ʒ��ʹ�����Ϣ END
		}
		
	}
	/**
	 * ��֤ȡ�����
	 * @param caseNo
	 * @return
	 */
	public  boolean  isEnableCancleInDP(TParm parm){
		if(InwForOutSideTool.getInstance().checkOrderisExistExec(parm)){
			this.messageBox("������ƺ���ִ�е�ҽ��,����ȡ�����");
			return false;
		}
		if(InwForOutSideTool.getInstance().checkOrderisExistCheck(parm)){
			this.messageBox("������ƺ�����˵�ҽ��,����ȡ�����");
			return false;
		}
		if(InwForOutSideTool.getInstance().checkOrderisExist(parm)){
			this.messageBox("������ƺ��ѿ�����ҽ��,����ȡ�����");
			return false;
		}
		TParm parmfee=InwForOutSideTool.getInstance().checkOrderFee(parm);
		if(parmfee.getDouble("TOT_AMT", 0)!=0){
			this.messageBox("��ƺ���ܷ���Ϊ:"+parmfee.getDouble("TOT_AMT", 0)+",����ȡ�����");
			return false;
		}
		return true;
	}
	/**
	 * Ԥ�Ǽ�
	 */
	public void onAdmPreInp(){
		int patRow=in.getSelectedRow();
		TParm tableParm=in.getParmValue();
		if(patRow<0){
			this.messageBox("��ѡ��һ����Ժ����");
			return;
		}
		if("".equals(in.getParmValue().getValue("MR_NO",patRow))){
			this.messageBox("�ô�λû����Ժ������Ϣ");
			return;
		}
		if("Y".equals(tableParm.getValue("PRE_FLG",patRow))){
			this.messageBox("�ò����Ѿ�Ԥת");
			return;
		}
		TParm parm=new TParm();
		parm.setData("MR_NO",tableParm.getValue("MR_NO",patRow));
		parm.setData("CASE_NO",tableParm.getValue("CASE_NO",patRow)); //add by huangtt 20170502 ���CASE_NO
		parm.setData("IPD_NO",tableParm.getValue("IPD_NO",patRow));
		parm.setData("PAT_NAME",tableParm.getValue("PAT_NAME",patRow));
		parm.setData("SEX_CODE",tableParm.getValue("SEX_CODE",patRow));
		parm.setData("AGE",tableParm.getValue("AGE",patRow));
		parm.setData("BED_NO",tableParm.getValue("BED_NO",patRow));
		parm.setData("PRETREAT_OUT_DEPT",this.getValue("DEPT_CODE"));
		parm.setData("PRETREAT_OUT_STATION",this.getValue("STATION_CODE"));
		parm.setData("NURSING_CLASS_CODE",tableParm.getValue("NURSING_CLASS",patRow));
		parm.setData("PATIENT_CONDITION",tableParm.getValue("PATIENT_CONDITION",patRow));
		this.openDialog("%ROOT%\\config\\adm\\ADMPreInp.x",parm);
		initInStation();
	}
	
	/**
	 * ȡ��Ԥ�Ǽ� 
	 */
	public void onCancelPreInp(){
		int patRow=in.getSelectedRow();
		TParm tableParm=in.getParmValue();
		if(patRow<0){
			this.messageBox("��ѡ��һ����Ժ����");
			return;
		}
		
		if("".equals(in.getParmValue().getValue("MR_NO",patRow))){
			this.messageBox("�ô�λû����Ժ������Ϣ");
			return;
		}
		if(!tableParm.getValue("PRE_FLG",patRow).equals("Y")){
			this.messageBox("�ò���û��Ԥת��Ϣ");
			return;
		}
		String sql="DELETE FROM ADM_PRETREAT WHERE PRETREAT_NO='"+tableParm.getValue("PRETREAT_OUT_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		sql="UPDATE SYS_BED SET PRE_FLG='N' , PRETREAT_OUT_NO='' WHERE PRETREAT_OUT_NO='"+tableParm.getValue("PRETREAT_OUT_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		this.messageBox("ȡ���ɹ�");
		// add by huangtt 20170502 start ���Ͳ���������Ϣ
		TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(tableParm.getValue("CASE_NO",patRow));
		if (xmlParm.getErrCode() < 0) {
			this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
		}
		// add by huangtt 20170502 end ���Ͳ���������Ϣ
		initInStation();
	}
	/**
	 * ��������
	 * 
	 * @param date
	 * @return
	 */
	private String patAge(Timestamp date) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = date == null ? sysDate : date;
		String age = "0";
		age = OdiUtil.showAge(temp, sysDate);
		return age;
	}
	
	/**
	 * ԤԼ 
	 */
	public void onPre(){
		int inRow=inTable.getSelectedRow();
		int patRow=in.getSelectedRow();
		if(inRow<0){
			this.messageBox("��ѡ��ԤԼ����");
			return;
		}
		if(patRow<0){
			this.messageBox("��ѡ��λ����");
			return ;
		}
		if(!"".equals(in.getParmValue().getValue("PRE_MRNO",patRow))){
			this.messageBox("�ô��Ѿ���ԤԼ");
			return;
		}
		if("Y".equals(inTable.getParmValue().getValue("EXEC_FLG",inRow))){
			this.messageBox("�˲����Ѿ�ԤԼ");
			return;
		}
		String patSex=in.getParmValue().getValue("SEX_CODE",patRow);
		String preSex=inTable.getParmValue().getValue("SEX_CODE",inRow);
		String date=SystemTool.getInstance().getDate().toString().replaceAll("-", "/").substring(0,19);
		TParm tableParm=inTable.getParmValue();
		if(!"".equals(in.getParmValue().getValue("MR_NO",patRow))){//�ô����в���
			
			if(!preSex.equals(patSex)){
				
				if(JOptionPane.showConfirmDialog(null, "�Ա���ͬ���Ƿ������", "��Ϣ",
	    				JOptionPane.YES_NO_OPTION) == 0){
					
				}else{
					return;
				}
			}
			String sql=" UPDATE ADM_PRETREAT SET EXEC_FLG='Y' WHERE PRETREAT_NO='"+inTable.getParmValue().getValue("PRETREAT_NO",inTable.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			sql="UPDATE SYS_BED SET APPT_FLG='Y',PRETREAT_DATE=TO_DATE('"+inTable.getParmValue().
			getValue("PRETREAT_DATE",inTable.getSelectedRow()).toString().substring(0,19).replaceAll("-", "/")+"','yyyy/MM/dd HH24:mi:ss')," +
			" PRE_MRNO='"+tableParm.getValue("MR_NO",inRow)+"'," +
			" PRETREAT_TYPE='"+tableParm.getValue("PRETREAT_TYPE",inRow)+"'," +
			" PRE_PATNAME='"+tableParm.getValue("PAT_NAME",inRow)+"',PRE_SEX='"+tableParm.getValue("SEX_CODE",inRow)+"'," +
			" PRETREAT_NO='"+tableParm.getValue("PRETREAT_NO",inRow)+"' " +
			" WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",in.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			this.messageBox("ԤԼ�ɹ�");
		}else{//�ô�û�в���,����Ҫ��֤ͬһ������Ĳ����Ա���ͬ
			String sql=" SELECT B.SEX_CODE FROM SYS_BED A,SYS_PATINFO B WHERE " +
					" A.ROOM_CODE='"+in.getParmValue().getValue("ROOM_CODE",patRow)+"' " +
					" AND A.MR_NO IS NOT NULL AND A.MR_NO=B.MR_NO ";
			TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getCount()>0){
				for(int i=0;i<parm.getCount();i++){
					if(!preSex.equals(parm.getValue("SEX_CODE",i))){
						if(JOptionPane.showConfirmDialog(null, "�Ա���ͬ���Ƿ������", "��Ϣ",
			    				JOptionPane.YES_NO_OPTION) == 0){
							break;
						}else{
							return;
						}
						
					}
				}
			}
			sql=" UPDATE ADM_PRETREAT SET EXEC_FLG='Y' WHERE PRETREAT_NO='"+inTable.getParmValue().getValue("PRETREAT_NO",inTable.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			sql="UPDATE SYS_BED SET APPT_FLG='Y',PRETREAT_DATE=TO_DATE('"+inTable.getParmValue().
			getValue("PRETREAT_DATE",inTable.getSelectedRow()).toString().substring(0,19).replaceAll("-", "/")+"'," +
			"'yyyy/MM/dd HH24:mi:ss')," +
			" PRE_MRNO='"+tableParm.getValue("MR_NO",inRow)+"'," +
			" PRETREAT_TYPE='"+tableParm.getValue("PRETREAT_TYPE",inRow)+"'," +
			" PRE_PATNAME='"+tableParm.getValue("PAT_NAME",inRow)+"',PRE_SEX='"+tableParm.getValue("SEX_CODE",inRow)+"'," +
			" PRETREAT_NO='"+tableParm.getValue("PRETREAT_NO",inRow)+"' " +
			" WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",in.getSelectedRow())+"'";
			new TParm(TJDODBTool.getInstance().update(sql));
			this.messageBox("ԤԼ�ɹ�");
		}
		initInStation();
	}
	
	/**
	 * ȡ��ԤԼ
	 */
	public void onCancelPre(){
		int patRow=in.getSelectedRow();
		if(patRow<0){
			this.messageBox("��ѡ��һ����ԤԼ������");
			return;
		}
		if("".equals(in.getParmValue().getValue("PRE_SEX_CODE",patRow))){
			this.messageBox("û��ԤԼ��Ϣ");
			return;
		}
		String sql="UPDATE SYS_BED SET APPT_FLG='N',PRETREAT_DATE='',PRE_MRNO=''," +
		" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO='' WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		sql=" UPDATE ADM_PRETREAT SET EXEC_FLG='N' WHERE PRETREAT_NO='"+in.getParmValue().getValue("PRETREAT_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		this.messageBox("ȡ���ɹ�");
		initInStation();
	}
	/**
	 * ɾ��ԤԼ
	 */
	public void onDeletePre(){
		int patRow=in.getSelectedRow();
		if(patRow<0){
			this.messageBox("��ѡ��һ����ԤԼ������");
			return;
		}
		if("".equals(in.getParmValue().getValue("PRE_SEX_CODE",patRow))){
			this.messageBox("û��ԤԼ��Ϣ");
			return;
		}
		String sql="UPDATE SYS_BED SET APPT_FLG='N',PRETREAT_DATE='',PRE_MRNO=''," +
		" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO='' WHERE BED_NO='"+in.getParmValue().getValue("BED_NO",patRow)+"'";
		new TParm(TJDODBTool.getInstance().update(sql));
		sql=" DELETE ADM_PRETREAT WHERE  PRETREAT_NO='"+in.getParmValue().getValue("PRETREAT_NO",patRow)+"' ";
		new TParm(TJDODBTool.getInstance().update(sql));
		this.messageBox("ɾ���ɹ�");
		initInStation();
	}
	/**
	 * ˢ�°�ť
	 */
	public void onFresh(){
		initInStation();
	}
	/**
	 * ���շ���� ��ʾ ����ɫ
	 * @param result
	 */
	public void tableColor(TParm result){
		in.setParmValue(result);
		in.setRowColor(0, Color.white);
		TParm tableParm=in.getParmValue();
		String romDesc=tableParm.getValue("ROOM_DESC",0);
		for(int i=1;i<result.getCount();i++){//���շ���� �����ʾ ��ɫ ����ɫ
			if(tableParm.getValue("ROOM_DESC",i).equals(romDesc)){
				in.setRowColor(i, in.getRowColor(i-1));//�������ͬ����ȡ��һ�е���ɫ
			}else{
				if(in.getRowColor(i-1)==Color.white)//����� ��ͬ�������ж���һ�� ����ɫ��ʲô����Ϊ��ɫ������һ��Ϊ��ɫ
					in.setRowColor(i, Color.lightGray);
				else //��Ϊ��ɫ������һ��Ϊ��ɫ
					in.setRowColor(i, Color.white);
				romDesc=tableParm.getValue("ROOM_DESC",i);
			}
		}
	}
	/**
	 * ���ɽ��ӵ�
	 */
	public void onCreate(){
		TParm action = new TParm();
		TTable waitOut = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		int row = waitOut.getSelectedRow();// ��ת����ѡ���к�
		if (row < 0) {
			this.messageBox("��ѡ�񲡻�");
			return;	
		}
		TDataStore ds = waitOut.getDataStore();	
		action.setData("MR_NO", ds.getItemString(row, "MR_NO"));//������
		action.setData("CASE_NO", ds.getItemString(row,"CASE_NO"));//�����
		Pat pat = new Pat();
		pat = pat.onQueryByMrNo(ds.getItemString(row, "MR_NO"));		
		action.setData("PAT_NAME", pat.getName());//����
		action.setData("FROM_DEPT",ds.getItemString(row,"OUT_DEPT_CODE")); //ת������
	    action.setData("TO_DEPT",ds.getItemString(row,"IN_DEPT_CODE")); //ת�����
	    // modify by wangb 2016/1/18 ICU����ICU-�������ӵ����������ɲ���-�������ӵ�
	    String transferClass = "";
	    String subClassCode = "";
		if ("I01,I02".contains(ds.getItemString(row, "OUT_STATION_CODE"))) {
			// ICU-����
			transferClass = "IW";
			subClassCode = "EMR06030701";
		} else {
			// ����-����
			transferClass = "WW";
			subClassCode = "EMR0603011";
		}
	    action.setData("TRANSFER_CLASS",transferClass); //��������
		//��ѯģ����Ϣ
	    TParm actionParm = this.getEmrFilePath(subClassCode);
	    action.setData("TEMPLET_PATH",
	    		actionParm.getValue("TEMPLET_PATH",0));//���ӵ�·��
	    action.setData("EMT_FILENAME",
	    		actionParm.getValue("EMT_FILENAME",0));//���ӵ�����
	    action.setData("FLG",false);//��ģ��
//        System.out.println("---action----------------"+action);
	    //����ģ��
	    this.openWindow("%ROOT%\\config\\emr\\EMRTransferWordUI.x", action);
	}
	/**
	 * ����һ����
	 */
	public void onTransfer(){
		TParm parm = new TParm();
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		int waitIndex = waitIn.getSelectedRow();// ��ת���ѡ���к�
		if (waitIndex < 0) {				
			TTable waitOut = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			int row = waitOut.getSelectedRow();// ��ת����ѡ���к�
			if (row < 0) {				
				TTable table = (TTable) this.callFunction("UI|in|getThis");
				int selectRow = table.getSelectedRow();
				if (selectRow < 0) {//��Ժ���˻�δѡ�в���
										
				}else{//����ס	
				TParm data = table.getParmValue();
			    parm.setData("MR_NO", data.getData("MR_NO", selectRow));// ������	
				parm.setData("CASE_NO", data.getData("CASE_NO", selectRow));// �����
				}	
			}else{//��ת��				
			TDataStore ds = waitOut.getDataStore();
			parm.setData("MR_NO", ds.getItemString(row, "MR_NO"));//������
			parm.setData("CASE_NO", ds.getItemString(row,"CASE_NO"));//�����
			}			
		}else {//��ת��
			// �õ���ת��DS
			TDataStore ds = waitIn.getDataStore();		
			parm.setData("MR_NO", ds.getItemString(waitIndex, "MR_NO"));//������
			parm.setData("CASE_NO", ds.getItemString(waitIndex,"CASE_NO"));//�����
		}
//		System.out.println("---parm----------------"+parm);	
		this.openWindow("%ROOT%\\config\\inw\\INWTransferSheet.x",parm);
	}
	
	 /**
     * �õ�EMR·��
     */
    public TParm getEmrFilePath(String subClassCode){
    	String sql=" SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE," +
    			   " A.TEMPLET_PATH FROM EMR_TEMPLET A"+
                   " WHERE A.SUBCLASS_CODE = '" + subClassCode + "'";
     	TParm result = new TParm();
    	result = new TParm(TJDODBTool.getInstance().select(sql)); 
//    	System.out.println("---result----------------getEmrFilePath"+result);
    	return result;
    }
}
