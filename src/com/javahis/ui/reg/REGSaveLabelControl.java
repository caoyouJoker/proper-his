package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 * �������ȼ�¼
 * @author WangQing 20170327
 *
 */
public class REGSaveLabelControl extends TControl{
	TTable table;// �������
	TTable orderT;// ��ͷҽ��������أ�

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		orderT = (TTable) this.getComponent("ORDER");
		// table�����¼�
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");
		// table֮checkBox�¼�
		this.callFunction("UI|TABLE|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxChangeValue");		
	}

	/**
	 * ���˺Żس���ѯ
	 * 
	 * @author wangqing 20170627
	 */
	public void onTriageNo(){
		// ��ʼ����ѯ����
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("��������˺ţ�����");
			return;
		}
		String mrNo = this.getValueString("MR_NO");	
		String vsTimeS = this.getValueString("VS_TIME_S");// ��ʼ��ѯ����
		String vsTimeE = this.getValueString("VS_TIME_E");// ������ѯ����	
		this.onClear();
		this.setValue("TRIAGE_NO", triageNo);
		this.setValue("MR_NO", mrNo);
		if(vsTimeS != null && vsTimeS.trim().length()>0){
			vsTimeS = vsTimeS.replace("-", "/").substring(0, 16);
			this.setValue("VS_TIME_S", vsTimeS);
		}
		if(vsTimeE != null && vsTimeE.trim().length()>0){
			vsTimeE = vsTimeE.replace("-", "/").substring(0, 16);
			this.setValue("VS_TIME_E", vsTimeE);
		}
		// ��ѯ
		String sql = "";
		TParm result = new TParm();
		sql = " SELECT A.TRIAGE_NO, A.CASE_NO, A.MR_NO, A.LEVEL_CODE, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE FROM ERD_EVALUTION A, SYS_PATINFO B WHERE A.MR_NO = B.MR_NO(+) AND A.TRIAGE_NO='"+triageNo+"' ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("û�д˼��˺�����");
			return;
		}	
		this.setValue("CASE_NO", result.getValue("CASE_NO", 0));// add by wangqing 20170922 ������������
		this.setValue("MR_NO", result.getValue("MR_NO", 0));
		this.setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		this.setValue("SEX_CODE", result.getValue("SEX_CODE", 0));
		this.setValue("AGE", this.getAge(result.getTimestamp("BIRTH_DATE", 0)));	
		this.setValue("DISEASE_CLASS", result.getValue("LEVEL_CODE", 0));// add by wangqing 20170921 ����ּ�ȡ���˵ȼ�
		// ��ʼ��������������
		onSelectVSData();
	}

	/**
	 * �����Żس���ѯ
	 * 
	 * @author wangqing 20170627
	 */
	public void onMrNo(){
		// ��ʼ������
		String mrNo = this.getValueString("MR_NO");
		if(mrNo == null || mrNo.trim().length()==0){
			this.messageBox("�����벡���ţ�����");
			return;
		}
		String triageNo = this.getValueString("TRIAGE_NO");
		String vsTimeS = this.getValueString("VS_TIME_S");	
		String vsTimeE = this.getValueString("VS_TIME_E");
		this.onClear();
		this.setValue("TRIAGE_NO", triageNo);
		//		this.setValue("MR_NO", mrNo);
		if(vsTimeS != null && vsTimeS.trim().length()>0){
			vsTimeS = vsTimeS.replace("-", "/").substring(0, 16);
			this.setValue("VS_TIME_S", vsTimeS);
		}
		if(vsTimeE != null && vsTimeE.trim().length()>0){
			vsTimeE = vsTimeE.replace("-", "/").substring(0, 16);
			this.setValue("VS_TIME_E", vsTimeE);
		}
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
		if(pat == null){
			this.messageBox("û�д˲�����Ϣ������");
			return;
		}
		String srcMrNo = PatTool.getInstance().checkMrno(mrNo);// ����
		if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());			
		}
		this.setValue("MR_NO", pat.getMrNo());
		// ��ѯ
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		Object obj = this.openDialog("%ROOT%\\config\\erd\\ERDSavePat.x", parm);
		if(obj != null && obj instanceof TParm){
			TParm result = (TParm) obj;
			this.setValue("TRIAGE_NO", result.getValue("TRIAGE_NO"));
		}else{
			return;
		}
		onTriageNo();
	}

	/**
	 * ��ѯ�����˺Ų�ѯ���ȣ�
	 */
	public void onQuery(){
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo != null && triageNo.trim().length()>0){
			this.onTriageNo();
			return;
		}
		String mrNo = this.getValueString("MR_NO");
		if(mrNo != null && mrNo.trim().length()>0){
			this.onMrNo();
			return;
		}
		this.messageBox("��������˺Ż��߲����Ų�ѯ������");	
	}

	/**
	 * ˢ��
	 */
	public void onResets(){
		this.onQuery();
	}

	/**
	 * ��ѯ��������
	 * @param triageNo
	 */
	public void onSelectVSData(){
		TParm result = new TParm();
		result = this.queryVSData();
		if(result == null){
//			this.messageBox("result is null");
			return;
		}
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return;
		}
		TablePublicTool.setParmValue(table, result);	
	}

	/**
	 * ��ѯ������������
	 */
	public TParm queryVSData(){
		// 1����ˢ������
		transferVSData();
		// 2���ٸ�������
		String triageNo = this.getValueString("TRIAGE_NO");
		String startTime = this.getValueString("VS_TIME_S");
		String endTime = this.getValueString("VS_TIME_E");

		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("��������˺ţ�����");
			return null;
		}
		String sql ="";
		TParm result = new TParm();
		sql = "SELECT 'N' AS SEL_FLG, TRIAGE_NO, BED_NO, VS_TIME, TEMPERATURE, CARDIOTACH, RESPIRATORY_RATE, SPO2, PAIN,NBPS,NBPD, OXY_SUPPLY_TYPE, OXY_SUPPLY_RATE, CONDITION, SIGN FROM AMI_ERD_VTS_RECORD WHERE TRIAGE_NO = '" + triageNo + "' @ ORDER BY VS_TIME DESC ";// ����
		String where = "";
		if(startTime != null && startTime.trim().length()>0){
			startTime = startTime.replace("-", "").replace(" ", "").replace(":", "").substring(0, 12);// 201706281507
			where += " AND VS_TIME >= '"+startTime+"' " ;
		}
		if(endTime != null && endTime.trim().length()>0){
			endTime = endTime.replace("-", "").replace(" ", "").replace(":", "").substring(0, 12);// 201706281507
			where += " AND VS_TIME <= '"+endTime+"' ";
		}
		if(startTime != null && startTime.trim().length()>0 && endTime != null && endTime.trim().length()>0){
			if(startTime.compareTo(endTime)>0){
				this.messageBox("��ʼʱ�䲻�ܴ��ڽ���ʱ�䣡����");
				return null;
			}			
			where += " AND '"+startTime+"' <= '"+endTime+"' ";
		}
		sql = sql.replace("@", where);


		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return result;
		}
		if(result.getCount()<=0){
//			this.messageBox("result.getCount()<=0");
			return null;
		}
		return result;
	}

	/**
	 * ����������������
	 */
	public void transferVSData(){
		String triageNo = this.getValueString("TRIAGE_NO");
		String sql ="";
		TParm result = new TParm();
		sql = " SELECT TRIAGE_NO, BED_NO, TO_CHAR (S_M_TIME, 'yyyyMMddHH24MI') AS S_M_TIME, TO_CHAR (E_M_TIME, 'yyyyMMddHH24MI') AS E_M_TIME FROM AMI_E_S_RECORD WHERE TRIAGE_NO='"+triageNo+"' ORDER BY S_M_TIME ASC ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("����������������err!!!");
			return;
		}
		if(result.getCount()<=0){
			return;
		}
		// ����ÿһ�����ݣ����������Ĳ��뵽AMI_ERD_VTS_RECORD����
		// 1����ȡAMI_ERD_VTS_RECORD.MAX(VS_TIME)
		// 2�����MAX(VS_TIME)Ϊ�գ�flg1=true;����flg1=false
		// 3.ִ��ѭ��
		// 3�����flg1=true��ֱ�Ӳ����������������ݣ����flg1=false�������ж�flg2
		// 4�����MAX(VS_TIME)�ڴ������ڣ���flg2=true������flg2=false
		// 5�����flg2=true�������MAX(VS_TIME)���������ʱ���ڵ����м�¼�����򣬲���������

		boolean flg1 = false;
		boolean flg2= false;
		String sTime = "";
		String sql2 ="";
		TParm result2 = new TParm();
		sql2 = "SELECT MAX(VS_TIME) VS_TIME FROM AMI_ERD_VTS_RECORD WHERE TRIAGE_NO = '" + triageNo + "'";
		result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if(result2.getErrCode()<0){
			this.messageBox("����������������err!!!");
			return;
		}
		if(result2.getValue("VS_TIME", 0) == null || result2.getValue("VS_TIME", 0).trim().length()<=0){
			flg1 = true;
			sTime = "";
		}else{
			flg1 = false;
			sTime = result2.getValue("VS_TIME", 0);// 201706271516
		}	
		String now = this.dateToString(new Date(), "yyyyMMddHHmm");

		for(int i=0; i<result.getCount(); i++){			
			String bedNo = result.getValue("BED_NO", i);
			String startMontorTime = result.getValue("S_M_TIME", i);
			String endMontorTime = result.getValue("E_M_TIME", i);
			if(endMontorTime == null || endMontorTime.trim().length()<=0){
				endMontorTime = now;
			}			
			if(flg1){				
				//ֱ�Ӳ����������������� 
				insertVSData(startMontorTime, endMontorTime, triageNo, bedNo, true);
				continue;
			}
			if(startMontorTime.compareTo(sTime) <=0 &&  endMontorTime.compareTo(sTime)>=0 && startMontorTime.compareTo(endMontorTime)<=0){
				flg1 = true;
				flg2 = true;
			}else{
				flg1 = false;
				flg2 = false; 
			}
			if(flg2){
				// �����MAX(VS_TIME)���������ʱ���ڵ����м�¼
				insertVSData(sTime, endMontorTime, triageNo, bedNo, false);
			}
		}

	}

	/**
	 * ����������������
	 * @author wangqing 20170627
	 * @param startTime
	 * @param endTime
	 * @param triageNo
	 * @param bedNo
	 * @param flg 
	 */
	public void insertVSData(String startTime, String endTime, String triageNo, String bedNo, boolean flg){
		if(startTime == null || startTime.trim().length()<=0 
				|| endTime == null || endTime.trim().length()<=0 
				|| triageNo == null || triageNo.trim().length()<=0 
				|| bedNo == null || bedNo.trim().length()<=0){
			return;
		}
		String sql = "";
		String where = "";
		TParm result = new TParm();
		sql = "INSERT INTO AMI_ERD_VTS_RECORD (TRIAGE_NO, BED_NO, VS_TIME, TEMPERATURE, "

					+ "CARDIOTACH, RESPIRATORY_RATE, SPO2, PAIN,NBPS,NBPD, OXY_SUPPLY_TYPE, OXY_SUPPLY_RATE) "

					+ "SELECT DISTINCT '" + triageNo + "' AS TRIAGE_NO, A.BED_NO, A.MONITOR_TIME AS VS_TIME, "

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='BT2' ) AS TEMPERATURE, "// �¶�

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='HR' ) AS CARDIOTACH, "// ����

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='RR' ) AS RESPIRATORY_RATE, "// ����Ƶ��

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='SPO2' ) AS SPO2, "// �����Ͷ�

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='PAIN' ) AS PAIN, "// ��ʹ

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='NBPS' ) AS NBPS, "// NBPS ����ѹ

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='NBPD' ) AS NBPD, "// NBPD ����ѹ

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='OXY_SUPPLY_TYPE' ) AS OXY_SUPPLY_TYPE, "// ������ʽ

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='OXY_SUPPLY_RATE' ) AS OXY_SUPPLY_RATE " // ����(��/��)

	                + "FROM ERD_CISVITALSIGN A "

	                //					+ "WHERE A.MONITOR_TIME >= '" + startTime + "' "

					+ " @ "

					+ "AND A.MONITOR_TIME <= '" + endTime + "' "

					+ "AND "+startTime + "<="+endTime+" "

					+ "AND A.BED_NO = '" + bedNo + "' "

					//					+ "AND A.BED_NO = 'E002' "

					+ "ORDER BY A.MONITOR_TIME"; 
		if(flg){// >=
			where += "WHERE A.MONITOR_TIME >= '" + startTime + "' ";
		}else{// >
			where += "WHERE A.MONITOR_TIME > '" + startTime + "' ";
		}		
		sql = sql.replace("@", where);
		result = new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * ����ժҪ����
	 */
	public void onCondition(){
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		//inParm.setData("DR_CODE", "000498");
		//inParm.setData("DEPT_CODE", "000498");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onConditionReturn");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setVisible(true);
	}

	/**
	 * Ƭ���¼����ش�ֵ
	 * 
	 * @param value
	 *            String
	 */
	public void onConditionReturn(String value) {
		TTextArea CONDITION = (TTextArea) this.getComponent("CONDITION");
		CONDITION.setText(value);
		this.onQuery();
	}

	/**
	 * table�����¼�
	 */
	public void onTABLEClicked(int row){
		if(row<0){
			this.messageBox("row<0");
			return;
		}
		if(table == null){
			this.messageBox("table is null");
			return;

		}
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		TParm selectRowParm = tableParm.getRow(row);
		setValueForParm("VS_TIME;TEMPERATURE;CARDIOTACH;RESPIRATORY_RATE;NBPS;NBPD;SPO2;PAIN;OXY_SUPPLY_TYPE;OXY_SUPPLY_RATE;CONDITION",selectRowParm);	
	}

	/**
	 * Ԥ����Ӧ�����ݿ��в�ѯ
	 */
	public void onPrint(){
		// ��������
		this.onQuery();
		TParm data = new TParm();
		data.setData("TITLE", "TEXT", "�������ȼ�¼");
		data.setData("PAT_NAME_TXT", "TEXT", this.getValue("PAT_NAME"));
		data.setData("SEX_CODE_TXT", "TEXT", this.getSex(getValueString("SEX_CODE")));
		data.setData("AGE_TXT", "TEXT", getValue("AGE"));
		data.setData("MR_NO_TXT", "TEXT", getValue("MR_NO"));
		data.setData("TRIAGE_NO_TXT", "TEXT", getValue("TRIAGE_NO"));
		data.setData("DISEASE_CLASS_TXT", "TEXT", getValue("DISEASE_CLASS"));// ����ּ�
		data.setData("CASE_NO_TEXT", "TEXT", getValue("CASE_NO"));// ��������
		TParm tableParm = table.getShowParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		TParm parm = new TParm();
		int count = 0;
		for(int i=0; i<table.getRowCount(); i++){
			// �л�ʿǩ���Ĵ�ӡ
			if(tableParm.getValue("SIGN", i) != null && tableParm.getValue("SIGN", i).trim().length()>0){	
				parm.addData("VS_TIME", tableParm.getValue("VS_TIME",i).substring(0, 4)+"/"+tableParm.getValue("VS_TIME",i).substring(4, 6)+"/"+tableParm.getValue("VS_TIME",i).substring(6, 8)+" "+tableParm.getValue("VS_TIME",i).substring(8, 10)+":"+tableParm.getValue("VS_TIME",i).substring(10, 12));//ʱ��
				parm.addData("TEMPERATURE", tableParm.getValue("TEMPERATURE",i));//���£��棩
				parm.addData("CARDIOTACH", tableParm.getValue("CARDIOTACH",i));//���� ����/�֣�
				parm.addData("RESPIRATORY_RATE", tableParm.getValue("RESPIRATORY_RATE",i));//��������/�֣�
				parm.addData("BP", tableParm.getValue("NBPS",i)+"/"+tableParm.getValue("NBPD",i));// Ѫѹ ������ѹ /����ѹ��			
				parm.addData("SPO2", tableParm.getValue("SPO2",i));// �����Ͷȣ�%��
				parm.addData("PAIN", tableParm.getValue("PAIN",i));// ��ʹ���֣�
				parm.addData("OXY_SUPPLY_TYPE", tableParm.getValue("OXY_SUPPLY_TYPE",i));//������ʽ
				parm.addData("OXY_SUPPLY_RATE", tableParm.getValue("OXY_SUPPLY_RATE",i));//����(��/��)
				parm.addData("CONDITION", tableParm.getValue("CONDITION",i));//����ժҪ
				parm.addData("SIGN", tableParm.getValue("SIGN",i));//ǩ��
				count++;
			}
		}
		parm.setCount(count);
		parm.addData("SYSTEM", "COLUMNS", "VS_TIME");
		parm.addData("SYSTEM", "COLUMNS", "TEMPERATURE");
		parm.addData("SYSTEM", "COLUMNS", "CARDIOTACH");
		parm.addData("SYSTEM", "COLUMNS", "RESPIRATORY_RATE");
		parm.addData("SYSTEM", "COLUMNS", "BP");		
		parm.addData("SYSTEM", "COLUMNS", "SPO2");
		parm.addData("SYSTEM", "COLUMNS", "PAIN");
		parm.addData("SYSTEM", "COLUMNS", "OXY_SUPPLY_TYPE");
		parm.addData("SYSTEM", "COLUMNS", "OXY_SUPPLY_RATE");
		parm.addData("SYSTEM", "COLUMNS", "CONDITION");
		parm.addData("SYSTEM", "COLUMNS", "SIGN");
		data.setData("TABLE",parm.getData());
		// ��ͷҽ��
		TParm orderResult = this.onQueryOrder();
		TablePublicTool.setParmValue(orderT, orderResult);
		TParm orderP = orderT.getShowParmValue();// ע��
		TParm parm2 = new TParm();
		int count2 = 0;
		for(int i=0; i<orderT.getRowCount(); i++){
			if(orderP.getValue("NOTE_DATE", i) != null && orderP.getValue("NOTE_DATE", i).trim().length()>16){
				parm2.addData("NOTE_DATE", orderP.getValue("NOTE_DATE",i).substring(0, 16));//����ʱ��
			}else{
				parm2.addData("NOTE_DATE", "");//����ʱ��
			}
			int mediQty = orderP.getInt("MEDI_QTY", i);
			if(mediQty != 0){
				parm2.addData("ORDER_DESC", orderP.getValue("ORDER_DESC",i)+" "+orderP.getValue("MEDI_QTY",i)+" "+orderP.getValue("MEDI_UNIT",i)+" "+orderP.getValue("ROUTE_CODE",i));//��ҩ�����ã���ͷҽ����
			}else{
				parm2.addData("ORDER_DESC", orderP.getValue("ORDER_DESC",i)+" "+orderP.getValue("ROUTE_CODE",i));//��ҩ�����ã���ͷҽ����);//��ҩ�����ã���ͷҽ����
			}
			parm2.addData("SIGN_DR", orderP.getValue("SIGN_DR",i));//ҽ��ǩ��
			parm2.addData("SIGN_NS", orderP.getValue("SIGN_NS",i));//��ʿǩ��
			count2++;
		}
		parm2.setCount(count2);
		parm2.addData("SYSTEM", "COLUMNS", "NOTE_DATE");
		parm2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm2.addData("SYSTEM", "COLUMNS", "SIGN_DR");
		parm2.addData("SYSTEM", "COLUMNS", "SIGN_NS");
		data.setData("TABLE_ORDER",parm2.getData());
		
		data.setData("PRINT_USER", Operator.getName());// ��ӡ��Ա
		data.setData("PRINT_DATE", StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));// ��ӡ����
		data.setData("PRINT_DEPT", "TEXT", this.getDeptName(Operator.getDept()));// ��ӡ����
		
		
		TFrame f = (TFrame)this.openWindow("%ROOT%\\config\\reg\\REGSavePrtAndPreview.x", data, false);
		f.showMaxWindow();
	}

	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("CASE_NO;MR_NO;TRIAGE_NO;PAT_NAME;VS_TIME;TEMPERATURE;"
				+ "CARDIOTACH;RESPIRATORY_RATE;NBPS;NBPD;SPO2;PAIN;OXY_SUPPLY_TYPE;"
				+ "OXY_SUPPLY_RATE;SEX_CODE;AGE;DISEASE_CLASS;VS_TIME_S;VS_TIME_E");
		// table���ݳ�ʼ��
		TParm parmValue1 = new TParm();
		parmValue1.setCount(0);
		table.setParmValue(parmValue1);
		// orderT���ݳ�ʼ��
		TParm parmValue2 = new TParm();
		parmValue2.setCount(0);
		orderT.setParmValue(parmValue2);
	}

	/**
	 * table�ϵ�checkBoxע�����
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxChangeValue(Object obj) {
		TTable table = (TTable)obj;		
		table.acceptText();
	}

	/**
	 * Timestamp-->String
	 * @param ts
	 * @param format
	 * @return
	 */
	public String timestampToString(Timestamp ts, String format){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat(format);// yyyy/MM/dd HH:mm
		try {
			tsStr = sdf.format(ts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	/**
	 * Date-->String
	 * @author wangqing 20170627
	 * @param date
	 * @return
	 */
	public String dateToString(Date date, String format){
		if(date == null){
			return null;
		}
		//		Date date = new Date();
		String dateStr = "";
		//format�ĸ�ʽ��������
		DateFormat sdf2 = new SimpleDateFormat(format);// yyyy/MM/dd HH/mm/ss
		try {
			dateStr = sdf2.format(date);
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	public String getAge(Timestamp birthDate){
		//		Timestamp birthDate = TypeTool.getTimestamp(getValue("BIRTH_DATE"));
		String age = OdoUtil.showAge( birthDate,SystemTool.getInstance().getDate());
		return age;
	}

	public String getSex(String sexCode){
		if(sexCode == null || sexCode.trim().length()<=0){
			return "";
		}
		if(sexCode.equals("0")){
			return "";
		}
		if(sexCode.equals("1")){
			return "��";
		}
		if(sexCode.equals("2")){
			return "Ů";
		}
		return "";
	}

	/**
	 * ��ȡ��������
	 * @param deptCode
	 * @return
	 */
	public String getDeptName(String deptCode){
		String sql = " SELECT DEPT_CODE, DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0 || result.getCount()<=0){
			return "";
		}
		return result.getValue("DEPT_CHN_DESC", 0);
	}
	
	
	/**
	 * ��ʿǩ��
	 */
	public void onSign(){
		if(table == null){
			this.messageBox("table is null");
			return;
		}
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		boolean flg = false;// ����Ƿ��й�ѡ��
		for(int i=0; i<tableParm.getCount(); i++){
			// �Ѿ�ǩ���Ĳ���ǩ��
			if(tableParm.getValue("SIGN", i) != null 
					&& tableParm.getValue("SIGN", i).trim().length()>0 
					&& tableParm.getValue("SEL_FLG", i) != null 
					&& tableParm.getValue("SEL_FLG", i).equals("Y") ){
				this.messageBox("�Ѿ�ǩ�����в���ǩ��������");		
				return;
			}
			for(int j=0; j<tableParm.getCount(); j++){
				if(tableParm.getValue("SEL_FLG", j) != null 
						&& tableParm.getValue("SEL_FLG", j).equals("Y")){
					flg = true;					
				}
			}		
		}
		if(!flg){
			this.messageBox("��ѡ��ǩ���У�����");
			return;
		}
		TParm parm = new TParm();
		Object obj = this.openDialog("%ROOT%\\config\\reg\\REGSavePassWordCheck.x", parm);
		if(obj != null && obj instanceof TParm){
			parm = (TParm) obj;
			// ȡ��ǩ��
			if(parm.getValue("RESULT") != null && parm.getValue("RESULT").equals("CANCLE")){
				// ȡ����ѡ
				for(int i=0; i<tableParm.getCount(); i++){
					if(tableParm.getValue("SEL_FLG", i) != null 
							&& tableParm.getValue("SEL_FLG", i).equals("Y")){
						TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", tableParm.getValue("SEL_FLG", i), "N");
					}			
				}
				this.messageBox("ǩ��ʧ�ܣ�����");
				return;
			}
			for(int i=0; i<tableParm.getCount(); i++){ 
				if(tableParm.getValue("SEL_FLG", i) != null 
						&& tableParm.getValue("SEL_FLG", i).equals("Y")){
					TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", tableParm.getValue("SEL_FLG", i), "N");// ȡ����ѡ 
					TablePublicTool.modifyRow(table, i, 13, "SIGN", tableParm.getValue("SIGN", i), parm.getValue("USER_ID"));// ��ʿǩ�� 
				}			
			}	
			// ����
			if(this.onSave1()){
				this.messageBox("ǩ���ɹ�������");
			}else{
				this.messageBox("ǩ��ʧ�ܣ�����");
			}			
		}else{
			// ȡ����ѡ
			for(int i=0; i<tableParm.getCount(); i++){
				if(tableParm.getValue("SEL_FLG", i) != null 
						&& tableParm.getValue("SEL_FLG", i).equals("Y")){
					TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", tableParm.getValue("SEL_FLG", i), "N");
				}			
			}	
			this.messageBox("ǩ��ʧ�ܣ�����");
			return;
		}
	}

	/**
	 * ȡ��ǩ��
	 */
	public void onCancelSign(){
		if(table == null){
			this.messageBox("table is null");
			return;
		}
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		boolean flg = false;// ����Ƿ��й�ѡ��
		for(int i=0; i<tableParm.getCount(); i++){
			// δǩ���Ĳ���ȡ��ǩ��
			if( (tableParm.getValue("SIGN", i) == null 
					|| tableParm.getValue("SIGN", i).trim().length()==0) 
					&& tableParm.getValue("SEL_FLG", i) != null 
					&& tableParm.getValue("SEL_FLG", i).equals("Y") ){
				this.messageBox("δǩ���Ĳ���ȡ��ǩ��");			
				return;
			}
			for(int j=0; j<tableParm.getCount(); j++){
				if(tableParm.getValue("SEL_FLG", j) != null 
						&& tableParm.getValue("SEL_FLG", j).equals("Y")){
					flg = true;					
				}
			}		
		}
		if(!flg){
			this.messageBox("��ѡ��ȡ��ǩ���У�����");
			return;
		}
		for(int i=0; i<tableParm.getCount(); i++){
			if(tableParm.getValue("SEL_FLG", i) != null 
					&& tableParm.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", tableParm.getValue("SEL_FLG", i), "N");// ȡ����ѡ 
				TablePublicTool.modifyRow(table, i, 13, "SIGN", tableParm.getValue("SIGN", i), "");// ��ʿǩ�� 
			}
		}		
		// ����
		if(this.onSave2()){
			this.messageBox("ȡ��ǩ���ɹ�������");
		}else{
			this.messageBox("ȡ��ǩ��ʧ�ܣ�����");
		}		
	}

	/**
	 * ��ѯ��ͷҽ���б�
	 */
	public TParm onQueryOrder(){
		TParm result = new TParm();
		// ��ʼ����ѯ����
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("��������˺ţ�����");
			return result;
		}
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);		
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "selectOnwOrder", parm);
		if(result.getErrCode()<0){
			System.out.println("��ѯ��ͷҽ���б�err!!!");
		}
		return result;
	}

	/**
	 * ������������
	 */
	public void onSave(){
		if(table == null){
			this.messageBox("table is null");
			return;
		}	
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		int row= table.getSelectedRow();
		if(row<0){
			this.messageBox("row<0");
			return;
		}
		TablePublicTool.modifyRow(table, row, 3, "TEMPERATURE", 
				tableParm.getValue("TEMPERATURE", row), this.getValue("TEMPERATURE"));// ����
		TablePublicTool.modifyRow(table, row, 4, "CARDIOTACH", 
				tableParm.getValue("CARDIOTACH", row), this.getValue("CARDIOTACH"));// ����
		TablePublicTool.modifyRow(table, row, 5, "RESPIRATORY_RATE", 
				tableParm.getValue("RESPIRATORY_RATE", row), this.getValue("RESPIRATORY_RATE"));// ����
		TablePublicTool.modifyRow(table, row, 6, "NBPS", 
				tableParm.getValue("NBPS", row), this.getValue("NBPS"));// ����ѹ
		TablePublicTool.modifyRow(table, row, 7, "NBPD", 
				tableParm.getValue("NBPD", row), this.getValue("NBPD"));// ����ѹ
		TablePublicTool.modifyRow(table, row, 8, "SPO2", 
				tableParm.getValue("SPO2", row), this.getValue("SPO2"));// Ѫ�����Ͷ�
		TablePublicTool.modifyRow(table, row, 9, "PAIN", 
				tableParm.getValue("PAIN", row), this.getValue("PAIN"));// ��ʹ����
		TablePublicTool.modifyRow(table, row, 10, "OXY_SUPPLY_TYPE", 
				tableParm.getValue("OXY_SUPPLY_TYPE", row), this.getValue("OXY_SUPPLY_TYPE"));// ������ʽ
		TablePublicTool.modifyRow(table, row, 11, "OXY_SUPPLY_RATE", 
				tableParm.getValue("OXY_SUPPLY_RATE", row), this.getValue("OXY_SUPPLY_RATE"));// ������
		TablePublicTool.modifyRow(table, row, 12, "CONDITION", 
				tableParm.getValue("CONDITION", row), this.getValue("CONDITION"));// ����ժҪ
		// У������
		checkData(tableParm, "TRIAGE_NO;VS_TIME");
		TParm saveResult= new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSaveVSData", tableParm);
		if(saveResult==null 
				|| saveResult.getErrCode()<0){
			this.messageBox("����ʧ�ܣ�����");
			return;
		}
		this.messageBox("����ɹ�������");	
		// ˢ����������
		onSelectVSData();		
	}

	/**
	 * У������
	 */
	public void checkData(TParm parm, String names1, String names2){
		String [] nameArr1 = names1.split(";");
		String [] nameArr2 = names2.split(";");	
		for(int i=parm.getCount()-1; i>=0; i--){
			for(int j=0; j<nameArr1.length; j++){
				if(parm.getData(nameArr1[j], i)==null){
					parm.setData(nameArr1[j], i, "");
				}
			}
			for(int k=0; k<nameArr2.length; k++){
				if(parm.getData(nameArr2[k], i)==null || parm.getData(nameArr2[k], i).toString().trim().length()==0){
					parm.removeRow(i);
					break;
				}
			}	   
		}
	}

	/**
	 * У������
	 * @param parm
	 * @param names2
	 */
	public void checkData(TParm parm, String names2){
		String[] names = parm.getNames(TParm.DEFAULT_GROUP);
		StringBuffer namesStr = new StringBuffer();
		for(int i=0; i<names.length; i++){
			if(namesStr.length()>0){
				namesStr.append(";");
			}
			namesStr.append(names[i]);
		}	
		String names1 = namesStr.toString();
		this.checkData(parm, names1, names2);
	}

	/**
	 * ��ͷҽ��
	 */
	public void onOrder(){
		// ��ʼ����ѯ����
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("��������˺ţ�����");
			return;
		}
		
		// ��ѯ
		String sql = "";
		TParm result = new TParm();
		sql = " SELECT A.TRIAGE_NO, A.CASE_NO, A.MR_NO, A.LEVEL_CODE, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE FROM ERD_EVALUTION A, SYS_PATINFO B WHERE A.MR_NO = B.MR_NO(+) AND A.TRIAGE_NO='"+triageNo+"' ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("û�д˼��˺���Ϣ");
			return;
		}	
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);// ���˺�
		parm.setData("MR_NO", result.getValue("MR_NO", 0));// ������
		parm.setData("PAT_NAME", result.getValue("PAT_NAME", 0));// ��������
		parm.setData("PAT_SEX", result.getValue("SEX_CODE", 0));// �����Ա�
		parm.setData("PAT_AGE", this.getAge(result.getTimestamp("BIRTH_DATE", 0)));// ��������
		this.openWindow("%ROOT%\\config\\onw\\ONWOrder.x", parm);
		//		this.openDialog("%ROOT%\\config\\onw\\ONWOrder.x", parm);
	}

	/**
	 * ��ʿǩ������
	 * @return
	 */
	public boolean onSave1(){ 
		if(table == null){
			this.messageBox("table is null");
			return false;
		}	
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return false;
		}
		// У������
		checkData(tableParm, "TRIAGE_NO;VS_TIME;SIGN");
		TParm saveResult= new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "updateAmiErdVtsRecord1", tableParm);
		if(saveResult==null 
				|| saveResult.getErrCode()<0){
			return false;
		}	
		// ˢ����������
		onSelectVSData();	
		return true;
	}

	/**
	 * ��ʿȡ��ǩ������
	 * @return
	 */
	public boolean onSave2(){ 
		if(table == null){
			this.messageBox("table is null");
			return false;
		}	
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return false;
		}
		// У������
		checkData(tableParm, "TRIAGE_NO;VS_TIME");
		TParm saveResult= new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "updateAmiErdVtsRecord2", tableParm);
		if(saveResult==null 
				|| saveResult.getErrCode()<0){
			return false;
		}
		// ˢ����������
		onSelectVSData();	
		return true;
	}







}
