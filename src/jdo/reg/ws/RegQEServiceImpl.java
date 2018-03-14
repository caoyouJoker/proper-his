package jdo.reg.ws;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONSerializer;
import jdo.bil.BIL;
import jdo.bil.BILPayTool;
import jdo.ekt.EKTTool;
import jdo.hl7.Hl7Communications;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.odo.OpdOrderHistory;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.reg.services.BodyParams;
import jdo.reg.services.Detail;
import jdo.reg.services.RegRequest;
import jdo.reg.services.RegResponse;
import jdo.reg.services.Result;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.Pat;
import jdo.sys.SYSDictionaryServiceTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.testOpb.tools.AssembleTool;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class RegQEServiceImpl implements RegQEService {
	
	private DecimalFormat df = new DecimalFormat("##########0.00");
	
	@Override
	public String Process(String TransCode, String InXml) {
		
		writerLog2("�ӿ����ƣ�"+TransCode);
		writerLog2("�ӿڲ���xml=="+InXml); 

		// TODO Auto-generated method stub
		String outXml="";
		//��ȡ��ǰָ��ҽ����ʣ��Һ���
		if("getDrTodayRestCount".equals(TransCode)){
			
			outXml=getDrTodayRestCount(InXml);
			
			//ָ��������ҽ����ʣ��Һ���
		}else if("getDrAllRestCount".equals(TransCode)){	
				
				outXml=getDrAllRestCount(InXml);
				
		
		//�Һ�������
		}else if("regOrUnReg".equals(TransCode)){
			
			outXml=regOrUnReg(InXml);
		
		//��ѯ���߹Һ��б�
		}else if("getPatRegPatadm".equals(TransCode)){
			
			outXml=getPatRegPatadm(InXml);
		
		//ִ��ԤԼ
		}else if("regApp".equals(TransCode)){
			
			outXml=regApp(InXml);
		
		//��ԤԼ
		}else if("unRegApp".equals(TransCode)){
			
			outXml=unRegApp(InXml);
		
			//��ȡҽ�������Ű���Ϣ
		}else if("getRegSchday".equals(TransCode)){
			
			outXml=getRegSchday(InXml);
			
			//ͬ���������ع�ϵ
		}else if("getRegDeptMT".equals(TransCode)){
			
			outXml=getRegDeptMT();
			
			//��ȡ�ű���Ϣ
		}else if("getRegClinictype".equals(TransCode)){
			
			outXml=getRegClinictype();
			
			//��ȡҽ����Ϣ
		}else if("getRegDR".equals(TransCode)){
			
			outXml=getRegDR();
			
			//��ȡ������Ϣ
		}else if("getRegDept".equals(TransCode)){
			
			outXml=getRegDept();
			
		}else if("getPatInfo".equals(TransCode)){//������Ϣ
			
			outXml = getPatInfo(InXml);
			
		}else if("getRemainsSumBy".equals(TransCode)){//��ѯָ��ҽ������ʱ��ε�ʣ�����
			
			outXml = getRemainsSumBy(InXml);
			
		}else if("getRemaingSum".equals(TransCode)){//��ȡҽ��ָ�����ں�ʱ�ε�ʣ�����
			
			outXml = getRemaingSum(InXml);
			
		}else if("getAppList".equals(TransCode)){//��ѯԤԼ�б�
			
			outXml = getAppList(InXml);
			
			
		}else if(TransCode.equals("getAllDoctorNum")){//	������ͬ������ҽ����ʣ�����
			outXml = getAllDoctorNum(InXml);
		}
		//ҽ���ָ�
		else if(TransCode.equals("onInsSplit")){
			outXml = onInsSplit(InXml);
		}
		//ҽ��ȷ��  5.	�Һ�ȷ��
		else if(TransCode.equals("onInsConfirm")){
			outXml = onInsConfirm(InXml);
		}
    	//ҽ������
		else  if(TransCode.equals("onInsCancel")){
			outXml = onInsCancel(InXml);
		}
		//���￨����
		else if("ektPay".equals(TransCode)){
			outXml = ektPay(InXml);
		}
		//���￨�����ۿ�
		else if("ektRevockPay".equals(TransCode)){
			outXml = ektRevockPay(InXml);
		}
		//���ݶ����Ų�ѯ֧������
		else if("getEktPayList".equals(TransCode)){
			outXml = getEktPayList(InXml);
		}
		//�Һ�����ͬ��
		else if("getRegPatadmList".equals(TransCode)){
			outXml=getRegPatadmList(InXml);
		}
		//��ѯ���ɷ���Ŀ�б��������ϸ
		else if("getRxNoNotBillList".equals(TransCode)){
			outXml=getRxNoNotBillList(InXml);
		}
		//�����վݱ�Ų�ѯ�վ�����Ϣ
		else if("getBilRegRecpList".equals(TransCode)){
			outXml=getBilRegRecpList(InXml);
		}
		//�����վݱ�Ų�ѯ�վ���ϸ��
		else if("getBilOpbRecpDetail".equals(TransCode)){
			outXml=getBilOpbRecpDetail(InXml);
		}
		//���ݴ����Ų�ѯ��ҩ״̬
		else if("getRxNoPhaStatus".equals(TransCode)){
			outXml=getRxNoPhaStatus(InXml);
		}
		//����ʱ��β�ѯ�����б���ʱ��
		else if("getRxNoBillList".equals(TransCode)){
			outXml=getRxNoBillList(InXml);
		}
		//���ݴ����Ų�ѯ������ϸ����ʱ��
		else if("getRxNoBillDetail".equals(TransCode)){
			outXml=getRxNoBillDetail(InXml);
		}
		//����ʱ��λ�ȡ���鵵�������б���ʱ��
		else if("getLisReportList".equals(TransCode)){
			outXml=getLisReportList(InXml);
		}
		//���鵵��������ϸ����ʱ��
		else if("getLisReportDetail".equals(TransCode)){
			outXml=getLisReportDetail(InXml);
		}
		//����ʱ��λ�ȡӰ�񵵰��б���ʱ��
		else if("getRisReportList".equals(TransCode)){
			outXml=getRisReportList(InXml);
		}
		//��ȡӰ�񵵰���ϸ����ʱ��
		else if("getRisReportDetail".equals(TransCode)){
			outXml=getRisReportDetail(InXml);
		}
		//�ɷ�ҽ���ָ�
	    else if(TransCode.equals("onInsSplitOpb")){	   
	    	outXml=onInsSplitOpb(InXml);
	    }
		//�ɷ�ҽ��ȷ��
		else if(TransCode.equals("onInsConfirmOpb")){
			outXml=onInsConfirmOpb(InXml);

		}
		//����
		else if(TransCode.equals("getEktPayDate")){
			outXml=getEktPayDate(InXml);

		}
		//ҽ�ƿ���Ǯ��Ǯ��ϸ   
		else if(TransCode.equals("getEktAccntDetail")){
			outXml=getEktAccntDetail(InXml);

		}
		//�ɷ��б�ͬ��
		else if(TransCode.equals("getPaymentList")){
			outXml=getPaymentList(InXml);

		}
		//���˷�״̬�Ľӿ�
		else if(TransCode.equals("getOverTimeOrderStatus")){
			outXml=getOvertimeOrderStatus(InXml);
		}
		//ҽ���˷�
		else  if(TransCode.equals("onBackIns")){
			outXml = onBackIns(InXml);
		}
		
		

		writerLog2("�ӿڳ�����outXml=="+TransCode+"====="+outXml); 

		return outXml;
	}
	
	public String getRegSchday(String inXml){
		
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><StartDate>2017-06-30</StartDate>" +
//				"<EndDate>2017-06-30</EndDate></BodyParams></Request>";
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		String sDate = bodyP.getStartDate().replaceAll("-", "").replaceAll("/", "").substring(0, 8);
		String eDate = bodyP.getEndDate().replaceAll("-", "").replaceAll("/", "").substring(0, 8);
		
		
		TParm re = RegQETool.getInstance().getRegSchday(sDate, eDate);
		xStream = new XStream();
		RegResponse res = new RegResponse();
		if(re.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			return xStream.toXML(res);
		}
		
		if (re.getCount() > 0) {
			res.setIsSuccessed("0");
			res.setErrorMsg("�ɹ�");
			List<Result> results = new ArrayList<Result>();
			Result result = new Result();
			for (int i = 0; i < re.getCount(); i++) {
				result = new Result();
				result.setVisitCode(re.getValue("ID", i));
				result.setVisitDate(re.getValue("ADM_DATE", i));
				result.setWeek(re.getValue("WEEK_DAY", i));
				result.setSxw(re.getValue("SESSION_CODE", i));
				result.setDoctorCode(re.getValue("REALDR_CODE", i));
				result.setDeptCode(re.getValue("REALDEPT_CODE", i));
				result.setVisitTypeCode(re.getValue("CLINICTYPE_CODE", i));
				result.setTotalNumber(re.getValue("MAX_QUE", i));
				result.setRemainsNumber(re.getValue("QUE", i));
				result.setIsEnabled(re.getValue("ISENABLED", i));
				result.setIsAppoint(re.getValue("ISAPPOINT", i));
				result.setStopType(re.getValue("STOP_SESSION",i));
				result.setPeriodCode(re.getValue("START_TIME", i));
//				result.setRegSpecialNumber(re.getValue("REG_SPECIAL_NUMBER", i));
				results.add(result);

			}
			res.setResults(results);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);// �滻����
			xStream.aliasField("Results", List.class, "Results");
		}else {
				res.setIsSuccessed("0");
				res.setErrorMsg("");
				xStream.alias("Response", RegResponse.class);
		}

			String outXml = xStream.toXML(res);
			return outXml;
		
		
	}
	
	public String getRegDeptMT(){
		TParm re = RegQETool.getInstance().getRegDeptMT();
		
		XStream xStream = new XStream();
		RegResponse res = new RegResponse();
		if (re.getCount() > 0) {
			res.setIsSuccessed("0");
			res.setErrorMsg("�ɹ�");
			List<Result> results = new ArrayList<Result>();
			Result result = new Result();
			for (int i = 0; i < re.getCount(); i++) {
				result = new Result();
				result.setDeptCode(re.getValue("DEPT_CODE", i));
				result.setMtCode(re.getValue("MT_DISEASE_CODE", i));
				result.setIsEnabled(re.getValue("ISENABLED", i));

				results.add(result);

			}
			res.setResults(results);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);// �滻����
			xStream.aliasField("Results", List.class, "Results");
		}else {
				res.setIsSuccessed("1");
				res.setErrorMsg("��ѯʧ��");
				xStream.alias("Response", RegResponse.class);
		}

			String outXml = xStream.toXML(res);
			return outXml;
	}
	
	public String getRegClinictype(){
		TParm re = RegQETool.getInstance().getRegClinictype();
		
		XStream xStream = new XStream();
		RegResponse res = new RegResponse();
		if (re.getCount() > 0) {
			res.setIsSuccessed("0");
			res.setErrorMsg("�ɹ�");
			List<Result> results = new ArrayList<Result>();
			Result result = new Result();
			for (int i = 0; i < re.getCount(); i++) {
				result = new Result();
				result.setVisitTypeCode(re.getValue("CLINICTYPE_CODE", i));
				result.setVisitTypeName(re.getValue("CLINICTYPE_DESC", i));
				result.setTotalFee(re.getValue("SUM_PRICE", i));
				result.setRegFee(re.getValue("REG_PRICE", i));
				result.setInspectFee(re.getValue("CLINIC_PRICE", i));
				result.setIsEnabled("1");
				
				results.add(result);

			}
			res.setResults(results);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);// �滻����
			xStream.aliasField("Results", List.class, "Results");
		}else {
				res.setIsSuccessed("1");
				res.setErrorMsg("��ѯʧ��");
				xStream.alias("Response", RegResponse.class);
		}

			String outXml = xStream.toXML(res);
			return outXml;
	}
	
	public String getRegDR(){
		TParm re = RegQETool.getInstance().getRegDR();
		
		XStream xStream = new XStream();
		RegResponse res = new RegResponse();
		if (re.getCount() > 0) {
			res.setIsSuccessed("0");
			res.setErrorMsg("�ɹ�");
			List<Result> results = new ArrayList<Result>();
			Result result = new Result();
			for (int i = 0; i < re.getCount(); i++) {
				result = new Result();
				result.setDoctorCode(re.getValue("DR_CODE", i));
				result.setDoctorName(re.getValue("USER_NAME", i));
				result.setDoctorLevel("");
				result.setDoctorProf(re.getValue("POS_CHN_DESC", i));
				result.setDoctorProfCode(re.getValue("POS_CODE", i));
				result.setSex(re.getValue("CHN_DESC", i));
				result.setIDCardNo(re.getValue("ID_NO", i));
				result.setInsureDocID("");
				result.setPhotourl("");
				result.setIsEnabled(re.getValue("IS_ENABLED", i));

				results.add(result);

			}
			res.setResults(results);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);// �滻����
			xStream.aliasField("Results", List.class, "Results");
		}else {
				res.setIsSuccessed("1");
				res.setErrorMsg("��ѯʧ��");
				xStream.alias("Response", RegResponse.class);
		}

			String outXml = xStream.toXML(res);
			return outXml;
	}
	
	
	/**
	 * ����
	 * @param inXml
	 * @return
	 */
	public String getRegDept(){
		TParm re = RegQETool.getInstance().getRegDept();
		
		XStream xStream = new XStream();
		RegResponse res = new RegResponse();
		if (re.getCount() > 0) {
			res.setIsSuccessed("0");
			res.setErrorMsg("�ɹ�");
			List<Result> results = new ArrayList<Result>();
			Result result = new Result();
			for (int i = 0; i < re.getCount(); i++) {
				result = new Result();
				result.setDeptCode(re.getValue("DEPT_CODE", i));
				result.setDeptName(re.getValue("DEPT_CHN_DESC", i));
				result.setDeptDesc(re.getValue("DESCRIPTION", i));
				result.setParentCode("");
				result.setOrderNum(re.getValue("SEQ", i));
				result.setDeptType("");
				result.setDeptAddress("");
				result.setIsEnabled(re.getValue("ACTIVE_FLG", i));

				results.add(result);

			}
			res.setResults(results);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);// �滻����
			xStream.aliasField("Results", List.class, "Results");
		}else {
				res.setIsSuccessed("1");
				res.setErrorMsg("��ѯʧ��");
				xStream.alias("Response", RegResponse.class);
		}

			String outXml = xStream.toXML(res);
			return outXml;
		
	}
	
	public String getDrTodayRestCount(String inXml) {
		// TODO Auto-generated method stub
		String outXml = "";

//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams><DoctorCode>000773</DoctorCode>"
//				+ "<DeptCode>020101</DeptCode>"
//				+ "<Sxw>P</Sxw><RegDate>2016-06-12</RegDate><TerminalCode>01</TerminalCode>"
//				+ "<VisitCode>H01#O#20160612#02#A0103#000773</VisitCode></BodyParams></Request>";

		inXml = RegQETool.getInstance().updateXml(inXml);

		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();

		TParm parm = new TParm();
		parm.setData("DoctorCode", bodyP.getDoctorCode().trim());
		parm.setData("DeptCode", bodyP.getDeptCode().trim());
		parm.setData("Sxw", bodyP.getSxw().trim());
		parm.setData("RegDate", bodyP.getRegDate().trim());
		parm.setData("TerminalCode", bodyP.getTerminalCode().trim());
		parm.setData("VisitCode", bodyP.getVisitCode().trim());

		
		TParm re = RegQETool.getInstance().getDrTodayRestCount(parm);
		
		xStream = new XStream();
		RegResponse res = new RegResponse();
		
		if(re.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			return xStream.toXML(res);
		}
		
		if (re.getCount() > 0) {
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			Result result = new Result();
			result.setTotalNum(re.getValue("MAX_QUE", 0));
			result.setRemainsNum(re.getValue("REG_QUE", 0));
			result.setMtRemainsNum("");
			res.setResult(result);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);

		} else {
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
		}

		outXml = xStream.toXML(res);
		return outXml;
	}

	
	public String regOrUnReg(String inXml) {
		// TODO Auto-generated method stub

//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams><PatientID>000000558983</PatientID>" +
//				"<DoctorCode>000875</DoctorCode><DeptCode>030105</DeptCode><Sxw>A</Sxw><RegDate>2017/06/19 10:30:00</RegDate>" +
//				"<VisitCode>H01#O#20170619#01#A0104#000875</VisitCode><BusinessFrom>01</BusinessFrom><LockType>01</LockType>" +
//				"<InsureSequenceNo>170619000003|MT05511705043931387</InsureSequenceNo></BodyParams></Request>";

		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		
		BodyParams bodyP = request.getBodyParams();
		String [] names = bodyP.getInsureSequenceNo().trim().split("\\|");
		String confirmNo="";
		if(names.length>1){
			confirmNo = names[1];
		}
		String caseNo = names[0];
		
//		writerLog2("caseNo==11=="+bodyP.getInsureSequenceNo().trim()+"--");
//		writerLog2("caseNo=="+caseNo+"--");

		
		TParm parm = new TParm();
		parm.setData("PatientID", bodyP.getPatientID().trim());
		parm.setData("DoctorCode", bodyP.getDoctorCode().trim());
		parm.setData("DeptCode", bodyP.getDeptCode().trim());
		parm.setData("Sxw", bodyP.getSxw().trim());
		parm.setData("RegDate", bodyP.getRegDate());
		parm.setData("BusinessFrom", getBussinessDesc(bodyP.getBusinessFrom().trim()));
		parm.setData("VisitCode", bodyP.getVisitCode().trim());
		parm.setData("LockType", bodyP.getLockType().trim());
		parm.setData("caseNo",caseNo);

		TParm re = RegQETool.getInstance().regOrUnReg(parm);
		xStream = new XStream();
		RegResponse res = new RegResponse();
		res.setIsSuccessed(re.getValue("IsSuccessed"));
		res.setErrorMsg(re.getValue("ErrorMsg"));
		
		//0 �ɹ� 1ʧ��
		if(re.getValue("IsSuccessed").equals("0")){
			
			Result result = new Result();			
			result.setRemainsNum(re.getValue("RemainsNum"));
			result.setInsureSequenceNo(re.getValue("RegId")+"|"+confirmNo);
			res.setResult(result);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);
	
		}else{
			xStream.alias("Response", RegResponse.class);
		}

		String outXml = xStream.toXML(res);
		return outXml;
	}

	
	public String getPatRegPatadm(String inXml) {
		// TODO Auto-generated method stub

//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams>"
//				+ "<CardNo>000000000332</CardNo><CardType>04</CardType><TerminalCode>020101</TerminalCode></BodyParams></Request>";
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		TParm parm = new TParm();
		parm.setData("CardNo", bodyP.getCardNo().trim());
		parm.setData("CardType", bodyP.getCardType().trim());
		parm.setData("TerminalCode", bodyP.getTerminalCode().trim());

		TParm re = RegQETool.getInstance().getPatRegPatadm(parm);
		xStream = new XStream();
		RegResponse res = new RegResponse();

		if(re.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			return xStream.toXML(res);
		}
		
		if (re.getCount() > 0) {
			res.setIsSuccessed("0");
			res.setErrorMsg("�ɹ�");
			List<Result> results = new ArrayList<Result>();
			Result result = new Result();
			for (int i = 0; i < re.getCount(); i++) {
				result = new Result();
				result.setRegID(re.getValue("CASE_NO", i));
				result.setPatientName(re.getValue("PAT_NAME", i));
				result.setPatientID(re.getValue("MR_NO", i));
				result.setRegTime(re.getValue("REG_DATE", i));
				result.setDoctorName(re.getValue("USER_NAME", i));
				result.setDeptName(re.getValue("DEPT_CHN_DESC", i));
				result.setSxwMeaning(re.getValue("SESSION_DESC", i));
				result.setVisitTypeName(re.getValue("QUEGROUP_DESC", i));
				//�жϹҺ�״̬
				result.setRegStatus(getRegStatus(re.getValue("CASE_NO", i),re.getValue("ADM_TYPE", i)));
				
				//�ж�ҽ������(��ְ ��ͨ����ְ���� ���Ǿ�����)
				String insType = "0"; //0 �Է�  1ҽ��  2����
				String inscrowdType =re.getValue("INS_CROWD_TYPE", i);
				String inspayType =re.getValue("INS_PAT_TYPE", i);
				if(inscrowdType.equals("1")&&inspayType.equals("1"))  //��ְ ��ͨ
					insType = "1";
				if(inscrowdType.equals("1")&&inspayType.equals("2"))  //��ְ����
					insType = "2";
				if(inscrowdType.equals("2")&&inspayType.equals("2"))  //�Ǿ�����
					insType = "2";

				result.setInsureTypeCode(insType);
				
				results.add(result);

			}
			res.setResults(results);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);// �滻����
			xStream.aliasField("Results", List.class, "Results");

		} else {
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
		}

		String outXml = xStream.toXML(res);
		return outXml;
	}
	
	//�жϹҺ�״̬
	public String getRegStatus(String caseNo,String admType){
		TParm opdAllParm = RegQETool.getInstance().getRegStatus(caseNo,admType, false);		
		int countAll = opdAllParm.getCount();//����ҽ������
		if(countAll < 0){
			return "1";  // 1-û�нɷ���Ϣ�ĹҺ�
		}
		
		TParm opdBilParm = RegQETool.getInstance().getRegStatus(caseNo,admType, true);
		int countBil = opdBilParm.getCount();//��Ҫ�ɷѵ�ҽ������
		
		if(countBil < 0){
			return "3";  //3-�ѽɷ���Ϣ�ĹҺ�
		}
		
		return "2"; //2-���ɷ���Ϣ�ĹҺ�
	}

	
	public String regApp(String inXml) {
		// TODO Auto-generated method stub

//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams><PatientID>000000558983</PatientID>" +
//				"<PatientName>κ����</PatientName><PatientSex></PatientSex><PhoneNo>15222020663</PhoneNo>" +
//				"<IDCardNo>120107195005101215</IDCardNo><GIDCardNo></GIDCardNo><CureCardNo></CureCardNo>" +
//				"<VisitCode>H01#O#20170619#01#A0104#000875</VisitCode><VisitDate>2017-06-19</VisitDate><Sxw>A</Sxw>" +
//				"<PeriodCode>10</PeriodCode><DoctorCode>000875</DoctorCode><DeptCode>030105</DeptCode>" +
//				"<VisitTypeCode></VisitTypeCode><AppointType>01</AppointType><PaymentType>02</PaymentType>" +
//				"<OrderNo>12121212</OrderNo><TerminalCode>0.0.0.0</TerminalCode><BusinessFrom>02</BusinessFrom>" +
//				"</BodyParams></Request>";

		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		TParm parm = new TParm();
		parm.setData("PatientID", bodyP.getPatientID().trim());
		parm.setData("DoctorCode", bodyP.getDoctorCode().trim());
		parm.setData("DeptCode", bodyP.getDeptCode().trim());
		parm.setData("Sxw", bodyP.getSxw().trim());
		parm.setData("BusinessFrom", getBussinessDesc(bodyP.getBusinessFrom().trim()));
		parm.setData("TerminalCode", bodyP.getTerminalCode().trim());
		parm.setData("VisitCode", bodyP.getVisitCode().trim());
		parm.setData("PeriodCode", bodyP.getPeriodCode().trim());
		parm.setData("AppointType", getHisAppointType(bodyP.getAppointType().trim()));
		parm.setData("IDNO", bodyP.getiDCardNo().trim());
		parm.setData("orderNo", bodyP.getOrderNo().trim());
		parm.setData("phoneNo", bodyP.getPhoneNo().trim());


		TParm re = RegQETool.getInstance().regApp(parm);
		xStream = new XStream();
		RegResponse res = new RegResponse();
		res.setIsSuccessed(re.getValue("IsSuccessed"));
		res.setErrorMsg(re.getValue("ErrorMsg"));
		if("0".equals(re.getValue("IsSuccessed"))){
			Result result = new Result();
			result.setAppointCode(re.getValue("AppointCode"));
			result.setComment(re.getValue("Comment"));
			res.setResult(result);
			xStream.alias("Response", RegResponse.class);
			xStream.alias("Result", Result.class);
		}else{
			xStream.alias("Response", RegResponse.class);
		}
	

		String outXml = xStream.toXML(res);
		return outXml;
	}

	
	public String unRegApp(String inXml) {
		// TODO Auto-generated method stub
		
//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams><PatientID>000000558982</PatientID>" +
//				"<AppointCode>160324000002</AppointCode><AppointType></AppointType>" +
//				"<TerminalCode>0.0.0.0</TerminalCode><BusinessFrom>02</BusinessFrom></BodyParams></Request>";
		
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		TParm parm = new TParm();
		parm.setData("PatientID", bodyP.getPatientID().trim());
		parm.setData("caseNo", bodyP.getAppointCode().trim());
		parm.setData("BusinessFrom", getBussinessDesc(bodyP.getBusinessFrom().trim()));
		parm.setData("TerminalCode", bodyP.getTerminalCode().trim());
		
		TParm re = RegQETool.getInstance().unRegApp(parm);
		xStream = new XStream();
		RegResponse res = new RegResponse();
		
		res.setIsSuccessed(re.getValue("IsSuccessed"));
		res.setErrorMsg(re.getValue("ErrorMsg"));
		
	
		
		xStream.alias("Response", RegResponse.class);
		String outXml = xStream.toXML(res);
		return outXml;

	}

	//====================================================================
	
	public  String getPatInfo(String inXml){
		writerLog2("getPatInfo--date--����-"+SystemTool.getInstance().getDate());
//		inXml = "<Request>    <HeaderParams>    </HeaderParams>    <BodyParams>     " +
//				" <CardNo>130922199209144823</CardNo>      <CardType>02</CardType>      " +
//				"<TerminalCode>APP</TerminalCode>      <Name>������</Name>     " +
//				" <BirthDay>1991-09-14</BirthDay>      <Sex>1</Sex>      " +
//				"<Address>      </Address>    </BodyParams>  </Request>";
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);//�滻����
		RegRequest request = (RegRequest)xStream.fromXML(inXml);
		BodyParams bodyParams = request.getBodyParams();
		Result result = new Result();
		xStream = new XStream();
		RegResponse response = new RegResponse();
		
		String cardType = bodyParams.getCardType().trim();
		String cardNo = bodyParams.getCardNo().trim();
		String patName = "";
		String insNo="";
		
		
		if("03".equals(cardType)){
			insNo=cardNo;
			TParm parm = new TParm();
			parm.setData("TEXT", ";"+cardNo+"=");
			TParm regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
			SystemTool.getInstance().getDate().toString();
			String admDate  = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd"); //�õ���ǰ��ʱ��
			writerLog2("admDate===="+admDate);
			parm.setData("ADVANCE_CODE",regionParm.getValue("NHI_NO", 0)+"@"+
					admDate+"@"+"1");//ҽԺ����@���÷���ʱ��@���
			writerLog2("parm=========="+parm);
			 TParm insParm = RegQETool.getInstance().readCardPat(parm);
			 writerLog2("insParm=="+insParm);
			 if(insParm.getErrCode()<0){
				response.setIsSuccessed("1");
				response.setErrorMsg(insParm.getErrText());
				xStream.alias("Response", RegResponse.class);//�滻����
				xStream.alias("Result", Result.class);//�滻����
				return xStream.toXML(response);
			 }		 
			 patName = insParm.getValue("PAT_NAME");  //????����
			 cardNo = insParm.getValue("SID").trim();  //????���֤

			 cardType="02";
			 
		}


		
		TParm parm = RegQETool.getInstance().getPatInfo(cardType,cardNo);
		writerLog2("getPatInfo--��ѯsql---"+SystemTool.getInstance().getDate());
		writerLog2("getPatInfo--����Parm---"+parm);
		
		if(parm.getErrCode() < 0){
			response.setIsSuccessed("1");
			response.setErrorMsg(parm.getErrText());
			xStream.alias("Response", RegResponse.class);//�滻����
			xStream.alias("Result", Result.class);//�滻����
			return xStream.toXML(response);
		}
		if(parm.getCount() > 0){
			
			if(parm.getCount() > 1){
				response.setIsSuccessed("1");
				response.setErrorMsg("�ò��˴��ڶ���������Ϣ����������ʶ����ȥ��̨����ҵ��");
				xStream.alias("Response", RegResponse.class);//�滻����
				xStream.alias("Result", Result.class);//�滻����
				return xStream.toXML(response);
			}
			
			result.setPatientID(parm.getValue("MR_NO",0));
			result.setCureCardNo(parm.getValue("CURECARDNO",0));
			if(parm.getValue("IDCARDNO",0).length() == 15 || 
					parm.getValue("IDCARDNO",0).length() == 18 ){
				result.setIDCardNo(parm.getValue("IDCARDNO",0));
			}else{
				result.setIDCardNo("");
			}
			if(insNo.length() > 0){
				result.setMedCardNo(insNo);
			}else{
				result.setMedCardNo(RegQETool.getInstance().getMedCardNo(parm.getValue("MR_NO",0)));
			}
			
			result.setPatientName(parm.getValue("PAT_NAME",0));
			result.setPatientAge(parm.getValue("AGE",0));
			result.setPatientSex(parm.getValue("SEX_CODE",0));
			result.setBirthday(parm.getValue("BIRTH_DATE",0));
			result.setPhoneNo(parm.getValue("TEL_HOME",0));
			result.setAddress(parm.getValue("ADDRESS",0));
			if(parm.getValue("CARDSTATUS",0).length() == 0){
				result.setCardStatus("0");
			}else{
				result.setCardStatus(parm.getValue("CARDSTATUS",0));
			}
			
			result.setBalance(parm.getValue("CURRENT_BALANCE", 0));
			
			result.setGIDCardNo("");
			result.setHealthCardNo("");
			
			response.setResult(result);
			response.setIsSuccessed("0");
			response.setErrorMsg("�ɹ�");
			xStream.alias("Response", RegResponse.class);//�滻����
			xStream.alias("Result", Result.class);//�滻����
		}else{
			if("02".equals(bodyParams.getCardType().trim()) || 
					"03".equals(bodyParams.getCardType().trim())
					){
				
				String newMrNo = SystemTool.getInstance().getMrNo();
				if (newMrNo == null || newMrNo.length() == 0) {
					response.setIsSuccessed("1");
					response.setErrorMsg("ERR:-1 ȡ�����Ŵ���!");
					xStream.alias("Response", RegResponse.class);//�滻����
					xStream.alias("Result", Result.class);//�滻����
					return xStream.toXML(response);
				}
				
				
				// ȡ�����ݿ�����
				TAction tAction = new TAction();
				TConnection conn = tAction.getConnection();
				if (conn == null){
					response.setIsSuccessed("1");
					response.setErrorMsg("Err:δȡ�����ݿ�����");
					xStream.alias("Response", RegResponse.class);//�滻����
					xStream.alias("Result", Result.class);//�滻����
					return xStream.toXML(response);
				}
				
				
				TParm patParm = new TParm();
				patParm.setData("MR_NO", newMrNo);				
				if("02".equals(bodyParams.getCardType().trim())){
					patParm.setData("PAT_NAME", bodyParams.getName().trim());
					patParm.setData("IDNO", bodyParams.getCardNo().trim());
					patParm.setData("BIRTH_DATE", bodyParams.getBirthDay().trim().replaceAll("-", ""));				
					patParm.setData("SEX_CODE", bodyParams.getSex().trim());
					patParm.setData("ADDRESS", bodyParams.getAddress().trim());
				}else if("03".equals(bodyParams.getCardType().trim())){
					
					patParm.setData("PAT_NAME", patName);
					patParm.setData("IDNO", cardNo);
					patParm.setData("BIRTH_DATE", "");				
					patParm.setData("SEX_CODE", "");
					String idNo =cardNo;
					if(idNo.length() == 15){
						idNo = IdcardUtils.conver15CardTo18(idNo);
					}
					if(idNo.length() == 18){
						patParm.setData("BIRTH_DATE", IdcardUtils.getBirthByIdCard(idNo));				
						patParm.setData("SEX_CODE", IdcardUtils.getGenderByIdCard(idNo));
					}
					
					patParm.setData("ADDRESS", "");
				}
				
				patParm.setData("CTZ1_CODE", "99");
				patParm.setData("OPT_USER", "QeApp");
				patParm.setData("OPT_TERM", "0.0.0.0");

				TParm patRe = RegQETool.getInstance().insertPat(patParm,conn);
//				if(patRe.getErrCode() < 0){
//					conn.rollback();
//					conn.close();
//					response.setIsSuccessed("1");
//					response.setErrorMsg("ERR:�����û�ʧ��");
//					xStream.alias("Response", RegResponse.class);//�滻����
//					xStream.alias("Result", Result.class);//�滻����
//					return xStream.toXML(response);
//					
//				}
//				
//				TParm p = new TParm();
//		         p.setData("CARD_NO",newMrNo+"001"); //����
//		         p.setData("MR_NO", newMrNo); //������
//		         p.setData("CARD_SEQ", "001"); //���
//		         p.setData("ISSUERSN_CODE", "1"); //����ԭ��
//		         p.setData("FACTORAGE_FEE", 0); //������
//		         p.setData("PASSWORD",  OperatorTool.getInstance().encrypt("0000")); //����
//		         p.setData("WRITE_FLG", "Y"); //д������ע��
//		         p.setData("OPT_USER", "QeApp");
//		         p.setData("OPT_TERM", "0.0.0.0");
//		         
//		         patRe =  RegQETool.getInstance().insertEkt(p, conn);
//		         if(patRe.getErrCode() < 0){
//						conn.rollback();
//						conn.close();
//						response.setIsSuccessed("1");
//						response.setErrorMsg("ERR:�����û�ʧ��");
//						xStream.alias("Response", RegResponse.class);//�滻����
//						xStream.alias("Result", Result.class);//�滻����
//						return xStream.toXML(response);
//						
//					}
//		         
//		         p.setData("ID_NO", patParm.getData("IDNO")); //���֤��
//		         p.setData("NAME", patParm.getData("PAT_NAME")); //����
//		         p.setData("CURRENT_BALANCE", 0); //���
//		         patRe =  RegQETool.getInstance().insertEktMaster(p, conn);
				
				if(patRe.getErrCode() < 0){
					conn.rollback();
					conn.close();
					response.setIsSuccessed("1");
					response.setErrorMsg("ERR:�����û�ʧ��");
					xStream.alias("Response", RegResponse.class);//�滻����
					xStream.alias("Result", Result.class);//�滻����
					return xStream.toXML(response);
					
				}else{

					conn.commit();
					conn.close();
					
					
					patParm = RegQETool.getInstance().getPatInfo(cardType,cardNo);
					result.setPatientID(patParm.getValue("MR_NO", 0));
				      result.setCureCardNo(patParm.getValue("CURECARDNO", 0));

				      if ((patParm.getValue("IDCARDNO", 0).length() == 15) || 
				        (patParm.getValue("IDCARDNO", 0).length() == 18))
				        result.setIDCardNo(patParm.getValue("IDCARDNO", 0));
				      else {
				        result.setIDCardNo("");
				      }
				      if (insNo.length() > 0)
				        result.setMedCardNo(insNo);
				      else {
				        result.setMedCardNo(RegQETool.getInstance().getMedCardNo(patParm.getValue("MR_NO", 0)));
				      }

				      result.setPatientName(patParm.getValue("PAT_NAME", 0));
				      result.setPatientAge(patParm.getValue("AGE", 0));
				      result.setPatientSex(patParm.getValue("SEX_CODE", 0));
				      result.setBirthday(patParm.getValue("BIRTH_DATE", 0));
				      result.setPhoneNo(patParm.getValue("TEL_HOME", 0));
				      result.setAddress(patParm.getValue("ADDRESS", 0));
				      if(patParm.getValue("CARDSTATUS", 0).length() == 0){
				    	  result.setCardStatus("0"); 
				      }else{
				    	  result.setCardStatus(patParm.getValue("CARDSTATUS", 0));
				      }
				      
				      result.setBalance(patParm.getValue("CURRENT_BALANCE", 0));

					result.setGIDCardNo("");
					result.setHealthCardNo("");
					
					response.setResult(result);
					response.setIsSuccessed("0");
					response.setErrorMsg("�ɹ�");
					xStream.alias("Response", RegResponse.class);//�滻����
					xStream.alias("Result", Result.class);//�滻����

					
				}
				
			}else{
				response.setIsSuccessed("1");
				response.setErrorMsg("û�в�ѯ����");
				xStream.alias("Response", RegResponse.class);//�滻���� 
			}

		}
		
		writerLog2("getPatInfo--���ؽ��---"+SystemTool.getInstance().getDate());
		return xStream.toXML(response);
	}
	
	/**
	 * ��ѯָ��ҽ������ʱ��ε�ʣ�����
	 * @param parm
	 * @return
	 */
	public String getRemainsSumBy(String inXml){
		
//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams><StartDate>2016-05-13</StartDate>" +
//		"<EndDate>2016-05-13</EndDate><DoctorCode>000886</DoctorCode><DeptCode>020101</DeptCode><AppointType>01</AppointType>" +
//		"<TerminalCode>192.168.8.231</TerminalCode></BodyParams></Request>";
		
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);//�滻����
		RegRequest request = (RegRequest)xStream.fromXML(inXml);
		BodyParams bodyParams = request.getBodyParams();
		TParm parm = RegQETool.getInstance().getRemainsSumBy(bodyParams);
		RegResponse response = new RegResponse();
		if(parm.getErrCode() < 0){
			response.setIsSuccessed("1");
			response.setErrorMsg(parm.getErrText());
			xStream.alias("Response", RegResponse.class);//�滻����
			xStream.alias("Result", Result.class);//�滻����
			return xStream.toXML(response);
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		if(parm.getCount()>0){
			List<Result> resultList = new ArrayList<Result>();
			for(int i = 0; i < parm.getCount(); i++){
				Result result = new Result();
				result.setRemainsNum(parm.getValue("REMAINSNUMBER",i));
				result.setSxw(parm.getValue("SXW",i));
				result.setVisitDate(sdf.format(parm.getTimestamp("VISITDATE",i)).toString());

				resultList.add(result);
			}
			response.setIsSuccessed("0");
			response.setErrorMsg("�ɹ�");
			response.setResults(resultList);
		}else{
			response.setIsSuccessed("1");
			response.setErrorMsg("��ѯʧ��");
		}
		xStream.alias("Response", RegResponse.class);//�滻����
		xStream.alias("Result", Result.class);//�滻����
		return xStream.toXML(response);
	}

	/**
	 * ��ȡҽ��ָ�����ں�ʱ�ε�ʣ�����
	 * @return
	 */
	public String getRemaingSum(String inXml){
//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams><VisitDate>2012-09-29</VisitDate>" +
//		"<Sxw>A</Sxw><DoctorCode>000488</DoctorCode><DeptCode>020103</DeptCode><AppointType>01</AppointType>" +
//		"<TerminalCode>192.168.8.231</TerminalCode></BodyParams></Request>";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);//�滻����
		RegRequest request = (RegRequest)xStream.fromXML(inXml);
		BodyParams bodyParams = request.getBodyParams();
		TParm parm = RegQETool.getInstance().getRemaingSum(bodyParams);
		RegResponse response = new RegResponse();
		
		if(parm.getErrCode() < 0){
			response.setIsSuccessed("1");
			response.setErrorMsg(parm.getErrText());
			xStream.alias("Response", RegResponse.class);//�滻����
			xStream.alias("Result", Result.class);//�滻����
			return xStream.toXML(response);
		}
		//writerLog2("parm::"+parm);
		if(parm.getCount() > 0){
			List<Result> resultList = new ArrayList<Result>();
			for(int i = 0; i < parm.getCount(); i++){
				Result result = new Result();
				result.setPeriodCode(parm.getValue("PERIODCODE",i));
				result.setPeriodMeaning(parm.getValue("PERIODMEANING",i));
				result.setRemainingNum(parm.getValue("REMAINSNUMBER",i));
				
				resultList.add(result);
			}
			response.setIsSuccessed("0");
			response.setErrorMsg("�ɹ�");
			response.setResults(resultList);
		}else{
			response.setIsSuccessed("1");
			response.setErrorMsg("��ѯʧ��");
		}
		
		xStream.alias("Response", RegResponse.class);//�滻����
		xStream.alias("Result", Result.class);//�滻����
		return xStream.toXML(response);
	}
	
	/**
	 * ��ѯԤԼ�б�
	 * @return
	 */
	public String getAppList(String inXml){
		/*if(inXml.equals("1")){
			 inXml = "<Request>" +
			 "<HeaderParams></HeaderParams>" +
			 "<BodyParams>" +
			 "<StartDate>2016-05-25</StartDate>" +
			 "<EndDate>2016-05-29</EndDate>" +
			 "</BodyParams>" +
			 "</Request>";
		}else{
			inXml = "<Request>" +
			 "<HeaderParams></HeaderParams>" +
			 "<BodyParams>" +
			 "<StartDate>2016-04-15</StartDate>" +
			 "<EndDate>2016-04-29</EndDate>" +
			 "</BodyParams>" +
			 "</Request>";
		}*/
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);// �滻����
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyParams = request.getBodyParams();
		TParm parm = RegQETool.getInstance().getAppList(bodyParams);
		RegResponse response = new RegResponse();
		if (parm.getErrCode() < 0) {
			response.setIsSuccessed("1");
			response.setErrorMsg(parm.getErrText());
			xStream.alias("Response", RegResponse.class);// �滻����
			xStream.alias("Result", Result.class);// �滻����
			return xStream.toXML(response);
		}
		List<Result> resultList = new ArrayList<Result>();
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				Result result = new Result();
				result.setPatientName(parm.getValue("PATIENTNAME", i));
				result.setAppointCode(parm.getValue("APPOINTCODE", i));
				result.setHISAppointCode(parm.getValue("HISAPPOINTCODE", i));
				result.setAppointType(getAppointType(parm.getValue("APPOINTTYPE", i)));// 01�ֻ�
																		// 02������
																		// ��03����ҽ��
																		// ��HIS�Һŷ�ʽ����
				result.setAppointStatus(parm.getValue("APPOINTSTATUS", i));
				result.setAppointStatusMeaning(parm.getValue(
						"APPOINTSTATUSMEANING", i));
				result.setAppointTime(parm.getValue("APPOINTTIME", i));
				result.setVisitDate(parm.getValue("VISITDATE", i));
				result.setAPW(parm.getValue("SXW", i));
				result.setAPWMeaning(parm.getValue("APWMEANING", i));
				result.setPeriodCode(parm.getValue("PERIODCODE", i));
				result.setPeriodMeaning(parm.getValue("PERIODMEANING", i));
				result.setDeptCode(parm.getValue("DEPTCODE", i));
				result.setDeptName(parm.getValue("DEPTNAME", i));
				result.setDoctorCode(parm.getValue("DOCTORCODE", i));
				result.setDoctorName(parm.getValue("DOCTORNAME", i));
				result.setVisitTypeCode(parm.getValue("VISITTYPECODE", i));
				result.setVisitTypeName(parm.getValue("VISITTYPENAME", i));
				result.setIsEmergency(parm.getValue("ISEMERGENCY", i));
				result.setIsCost(parm.getValue("ISCOST", i));
				
				result.setHISPatientID(parm.getValue("HISPATIENTID",i));
				resultList.add(result);
			}
			
		} 
		response.setIsSuccessed("0");
		response.setErrorMsg("�ɹ�");
		response.setResults(resultList);
		xStream.alias("Response", RegResponse.class);// �滻����
		xStream.alias("Result", Result.class);// �滻����
		String outStr = xStream.toXML(response);
		if(outStr.indexOf("<Results/>") > 0){
			outStr = outStr.replaceAll("<Results/>", "<Results></Results>");
		}
		return outStr;
	}
	
	/**
	 * ������ͬ�����е�ҽ����ʣ����� 
	 * @param inXml
	 * @return
	 */
	public String getAllDoctorNum(String inXml){
//		 inXml = "<Request>" +
//		 "<HeaderParams></HeaderParams>" +
//		 "<BodyParams>" +
//		 "<StartDate>2016-02-29</StartDate>" +
//		 "<EndDate>2016-02-29</EndDate>" +
//		 "</BodyParams>" +
//		 "</Request>";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);// �滻����
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyParams = request.getBodyParams();
		TParm parm = RegQETool.getInstance().getAllDoctorNum(bodyParams);
		RegResponse response = new RegResponse();

		if (parm.getErrCode() < 0) {
			response.setIsSuccessed("1");
			response.setErrorMsg(parm.getErrText());
			xStream.alias("Response", RegResponse.class);// �滻����
			xStream.alias("Result", Result.class);// �滻����
			return xStream.toXML(response);
		}
		if (parm.getCount() > 0) {
			List<Result> resultList = new ArrayList<Result>();
			for (int i = 0; i < parm.getCount(); i++) {
				Result result = new Result();
				result.setVisitCode(parm.getValue("VISITCODE", i));
				result.setAppointType(parm.getValue("APPOINTTYPE", i));// 01�ֻ�
																		// 02������
																		// ��03����ҽ��
																		// ��HIS�Һŷ�ʽ����
				result.setPeriod((parm.getValue("PERIOD", i)));
				result.setTotalNum(parm.getValue("TOTALNUM", i));
				result.setRemainsNum(parm.getValue("REMAINSNUM", i));
				resultList.add(result);
			}
			response.setIsSuccessed("0");
			response.setErrorMsg("�ɹ�");
			response.setResults(resultList);
		} else {
			response.setIsSuccessed("1");
			response.setErrorMsg("��ѯʧ��");
		}
		xStream.alias("Response", RegResponse.class);// �滻����
		xStream.alias("Result", Result.class);// �滻����
		return xStream.toXML(response);
	}
	
	public String getDrAllRestCount(String inXml){

//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams>" +
//		"<DeptCode>030104</DeptCode><VisitDate>2016-05-13</VisitDate>" +
//		"<TerminalCode>ZZJ002</TerminalCode><MachineIP>64.58.254.173</MachineIP>" +
//		"</BodyParams></Request>"; 
 
	inXml = RegQETool.getInstance().updateXml(inXml);
	
	XStream xStream = new XStream();
	xStream.alias("request", RegRequest.class);
	RegRequest request = (RegRequest) xStream.fromXML(inXml);
	BodyParams bodyP = request.getBodyParams();

	TParm parm = new TParm();
	parm.setData("DeptCode", bodyP.getDeptCode().trim());
	parm.setData("VisitDate", bodyP.getVisitDate().trim());

	//writerLog2(parm);
	TParm re = RegQETool.getInstance().getDrAllRestCount(parm);
	
	xStream = new XStream();
	RegResponse res = new RegResponse();

	if(re.getErrCode() < 0){
		res.setIsSuccessed("1");
		res.setErrorMsg("��ѯʧ��");
		xStream.alias("Response", RegResponse.class);
		return xStream.toXML(res);
	}
	
	if (re.getCount() > 0) {
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		for (int i = 0; i < re.getCount(); i++) {
			result = new Result();
			result.setDoctorCode(re.getValue("REALDR_CODE", i));
			if("01".equals(re.getValue("SESSION_CODE", i))){
				result.setAPW("A");
			}else if ("02".equals(re.getValue("SESSION_CODE", i))){
				result.setAPW("P");
			}
			result.setVisitCode(re.getValue("ID", i));
			result.setTotalNum(re.getValue("MAX_QUE", i));
			result.setRemainsNum(re.getValue("REG_QUE", i));
			result.setMtRemainsNum("");
			results.add(result);

		}
		res.setResults(results);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
	}else {
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
	}

		String outXml = xStream.toXML(res);
		return outXml;
	}
	
	public String ektPay(String inXml){
//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams>"
//			+ "<CardID>000000000332002</CardID><PatientID>000000000332</PatientID>" 
//			+ "<RegID>170421000006</RegID><OrderNo>17082100003001</OrderNo><Money>3.5</Money>"
//			+ "<BusinessType>1</BusinessType>"
//			+ "<OrderTime>2017-08-21 15:02:22</OrderTime><BusinessFrom>02</BusinessFrom>"
//			+ "<TerminalCode>0.0.0.0</TerminalCode></BodyParams></Request>";

		inXml = RegQETool.getInstance().updateXml(inXml);

		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse re = new RegResponse();
		
		String cardNo = bodyP.getCardID().trim();
		String optUser = getBussinessDesc(bodyP.getBusinessFrom().trim());
		String optTerm = bodyP.getTerminalCode().trim();
//		String caseNo = bodyP.getRegID();
		String mrNo = bodyP.getPatientID().trim();
		
		double money = Double.parseDouble(bodyP.getMoney());
		TParm ektParm = RegQETool.getInstance().getEktIssuelog(cardNo);
		if(ektParm.getCount() < 0){
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:δȡ�þ��￨��Ϣ");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		
		ektParm = RegQETool.getInstance().getEktMaster(cardNo);
		if(ektParm.getCount() < 0){
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:δȡ�þ��￨��Ϣ");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		
		double currentBalance = ektParm.getDouble("CURRENT_BALANCE", 0);
		if(currentBalance-money < 0){
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:���￨���㣬ʣ��"+currentBalance+"Ԫ");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		if(mrNo.length() == 0){
			mrNo = ektParm.getValue("MR_NO", 0);
		}
		
		// ȡ�����ݿ�����
		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		if (conn == null){
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:δȡ�����ݿ�����");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		
		String treadNo = SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO","TRADE_NO");
		if(treadNo.length() == 0){			
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:ҽ�ƿ����׺����������");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		
		TParm ektTradeParm = new TParm();
		ektTradeParm.setData("TRADE_NO", treadNo);
		ektTradeParm.setData("CARD_NO", cardNo);
		ektTradeParm.setData("MR_NO", mrNo);
		ektTradeParm.setData("CASE_NO", "");
		ektTradeParm.setData("PAT_NAME", RegQETool.getInstance().getPatName(mrNo));
		ektTradeParm.setData("OLD_AMT", currentBalance);
		ektTradeParm.setData("AMT", money);
		ektTradeParm.setData("STATE", "1");
		if("1".equals(bodyP.getBusinessType())){
			ektTradeParm.setData("BUSINESS_TYPE", "REG");
		}
		if("2".equals(bodyP.getBusinessType())){
			ektTradeParm.setData("BUSINESS_TYPE", "OPB");
		}
		
		ektTradeParm.setData("GREEN_BALANCE", "0");
		ektTradeParm.setData("GREEN_BUSINESS_AMT", "0");
		ektTradeParm.setData("OPT_USER", optUser);		
		ektTradeParm.setData("OPT_TERM", optTerm);
		ektTradeParm.setData("BUSINESS_APP_TYPE", bodyP.getBusinessType());
		String orderTime = bodyP.getOrderTime().replaceAll("-", "").replaceAll(":", "").replace(" ", "").substring(0, 14);
		ektTradeParm.setData("ORDER_TIME", orderTime);
		ektTradeParm.setData("ORDER_NO", bodyP.getOrderNo());
		ektTradeParm.setData("BIL_BUSINESS_NO", "");
		
		
		TParm resultParm = RegQETool.getInstance().insertEktAppTrade(ektTradeParm, conn);
		if (resultParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("insertEktAppTrade Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
			
		}
		
		resultParm = RegQETool.getInstance().insertEktTrade(ektTradeParm, conn);
		if (resultParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("insertEktTrade Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
			
		}
		
		
		BigDecimal cAmt  =new BigDecimal(currentBalance);
		BigDecimal mAmt  =new BigDecimal(money);
		BigDecimal bAmt = cAmt.subtract(mAmt);
		
		double balance = bAmt.doubleValue(); // ���
		
		
		ektTradeParm.setData("HISTORY_NO", RegQETool.getInstance().getHistoryNo());// �ڲ����׺�
		ektTradeParm.setData("SEQ", RegQETool.getInstance().getHistorySeq(mrNo));// �ڲ����׺�
		ektTradeParm.setData("EKT_AMT", balance);// �ڲ����׺�
		ektTradeParm.setData("EKT_BUSINESS_NO", "");
		ektTradeParm.setData("CONFIRM_NO", "");
		ektTradeParm.setData("CASE_NO", "T-"+treadNo);
		

		
		resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeParm, conn);
		if (resultParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("insertEktMasterHistorySql Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
			
		}
		
		
		
		resultParm = RegQETool.getInstance().updateEktMaster(cardNo, balance, optUser, optTerm, conn);
		if (resultParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("updateEktMaster Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
			
		}
		
		conn.commit();
		conn.close();
		xStream = new XStream();
		re.setIsSuccessed("0");
		re.setErrorMsg("�ɹ�");
		Result result = new Result();
		
		result.setBusinessSN(treadNo);
		result.setMoney(money+"");
		result.setBalance(balance+"");
		
		Date date =SystemTool.getInstance().getDate();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		result.setTraceTime(df.format(date));
		
		re.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);
		
		String outXml = xStream.toXML(re);
		return outXml;
		
	}
	
	public String getEktPayList (String inXml){
		
//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams>"
//			+ "<OrderNo>20160412103908</OrderNo>"
//			+ "</BodyParams></Request>";

		inXml = RegQETool.getInstance().updateXml(inXml);

		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse re = new RegResponse();
		
		String orderNo = bodyP.getOrderNo().trim();
		
		
		String tradeNo = RegQETool.getInstance().getTradeNo(orderNo);
		
		 xStream = new XStream();
		
		if(tradeNo == null || tradeNo.length() <=0){
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:û���ҵ�ҽ�ƿ����׺���");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}

		re.setIsSuccessed("0");
		re.setErrorMsg("�ɹ�");
		Result result = new Result();
		result.setBusinessSN(tradeNo);
		re.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		
		String outXml = xStream.toXML(re);
		return outXml;
		
	}
	
	public String ektRevockPay(String inXml){
		
//		inXml = "<Request><HeaderParams></HeaderParams><BodyParams>"
//			+ "<CardID>000000000332002</CardID>" 
//			+ "<OriginalBusinessSN>170821000003</OriginalBusinessSN>" 
//			+ "<OriginalOrderNo>17082100003001</OriginalOrderNo><Money>3.5</Money>"
//			+ "<BusinessType>01</BusinessType>"
//			+ "</BodyParams></Request>";

		inXml = RegQETool.getInstance().updateXml(inXml);

		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse re = new RegResponse();
		
		String cardNo = bodyP.getCardID().trim();
		String tradeNo = bodyP.getOriginalBusinessSN().trim();
		String orderNo = bodyP.getOriginalOrderNo().trim();
		double money = Double.parseDouble(bodyP.getMoney().trim());
		
		TParm ektParm = RegQETool.getInstance().getEktMaster(cardNo);
		if(ektParm.getCount() < 0){
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:δȡ�þ��￨��Ϣ");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		double currentBalance = ektParm.getDouble("CURRENT_BALANCE", 0);
		double balance = currentBalance + money;
		String optUser = ektParm.getValue("OPT_USER", 0);
		String optTerm = ektParm.getValue("OPT_TERM", 0);

		// ȡ�����ݿ�����
		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		if (conn == null) {
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:δȡ�����ݿ�����");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		
		if(tradeNo.length() == 0 && orderNo.length() > 0){
			tradeNo = RegQETool.getInstance().getTradeNo(orderNo);
		}
		
		String treadNoR = RegQETool.getInstance().getHistoryNo();
		if(treadNoR.length() == 0){			
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:ҽ�ƿ����׺����������");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		
		
		TParm resultParm = RegQETool.getInstance().updateEktTrade(tradeNo, optUser, optTerm,treadNoR, conn);
		if (resultParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("updateEktTrade Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
			
		}
		
		TParm ektTradeParm = RegQETool.getInstance().getEktTrade(tradeNo);
		if(ektTradeParm.getCount() < 0){
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("Err:δȡ�þ�ҽ�ƿ��ۿ���Ϣ");
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
		}
		
		TParm reParm = RegQETool.getInstance().updateEktMasterHistoryByCaseNo("T-"+tradeNo, conn);
		if (reParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("EktMasterHistory Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
			
		}
		
		TParm ektTradeHisParm = new TParm();
		ektTradeHisParm.setData("MR_NO", ektTradeParm.getValue("MR_NO",0));
		ektTradeHisParm.setData("BUSINESS_TYPE", ektTradeParm.getValue("BUSINESS_TYPE",0));
		ektTradeHisParm.setData("CARD_NO", cardNo);
		ektTradeHisParm.setData("CASE_NO", ektTradeParm.getValue("CASE_NO",0));
		ektTradeHisParm.setData("OPT_USER", optUser);
		ektTradeHisParm.setData("OPT_TERM", optTerm);
		ektTradeHisParm.setData("EKT_BUSINESS_NO", "");
		ektTradeHisParm.setData("CONFIRM_NO", "");
		ektTradeHisParm.setData("OLD_AMT", currentBalance);
		ektTradeHisParm.setData("AMT", -money);
		ektTradeHisParm.setData("EKT_AMT", balance);
		ektTradeHisParm.setData("HISTORY_NO", treadNoR);
		ektTradeHisParm.setData("SEQ", RegQETool.getInstance().getHistorySeq(ektTradeParm.getValue("MR_NO",0)));// �ڲ����׺�
		
		resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeHisParm, conn);
		if (resultParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("insertEktMasterHistorySql Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
			
		}
		
		
		
		
		resultParm = RegQETool.getInstance().updateEktMaster(cardNo, balance, optUser, optTerm, conn);
		if (resultParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			xStream = new XStream();
			re.setIsSuccessed("1");
			re.setErrorMsg("updateEktMaster Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			String outXml = xStream.toXML(re);
			return outXml;
			
		}
		conn.commit();
		conn.close();
		xStream = new XStream();
		re.setIsSuccessed("0");
		re.setErrorMsg("�ɹ�");
		Result result = new Result();
		result.setBalance(balance+"");	
		result.setBusinessSN(treadNoR);
		re.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);		
		String outXml = xStream.toXML(re);
		return outXml;
		
	}
	
	
	
	

	
	public String onInsConfirm(String InXml){
		String outXml="";
//		InXml="<?xml version=\"1.0\"?><Request><HeaderParams></HeaderParams><BodyParams>" +
//				"<PatientID>000000002222</PatientID><MedCardNo></MedCardNo>" +
//				"<IDCardNo>120112195210110437</IDCardNo><GIDCardNo></GIDCardNo>" +
//				"    <TerminalCode>ZZJ002</TerminalCode>" +
//				"    <InsureSequenceNo>180102000001|</InsureSequenceNo>" +
//				"    <RegID>180102000001|</RegID>    <MedCardPwd>111111</MedCardPwd>" +
//				"    <InsureTypeCode>2</InsureTypeCode>    <MtTypeCode>4</MtTypeCode>" +
//				"    <DeptCode>030101</DeptCode>    <DoctorCode>000110</DoctorCode>    <Sxw>P</Sxw>" +
//				"    <RegDate>20180102</RegDate>    <VisitTypeCode>b376046e-3ce0-4eda-8269-b6427c63f355</VisitTypeCode>" +
//				"    <VisitCode>H01#O#20170821#02#A0105#000815</VisitCode>    <BusinessFrom>01</BusinessFrom>" +
//				"    <PatientName>����ϲ</PatientName>    <PatientSex>1</PatientSex>    <PatientAge>54</PatientAge>" +
//				"    <PhoneNo>15022266492</PhoneNo>    <PaymentType>02</PaymentType>    <CashPay>30.0000</CashPay>" +
//				"    <AppointCode></AppointCode>    <OrderNo>180102000001001</OrderNo>  </BodyParams></Request>";
		
		InXml = RegQETool.getInstance().updateXml(InXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(InXml);
		BodyParams bodyP = request.getBodyParams();

		RegResponse res = new RegResponse();
		TParm parm = new TParm();
	    TParm resultData = new TParm();
		TParm insresultData = new TParm();
		String [] Array=new String [0];
		String insureSequenceNo = bodyP.getInsureSequenceNo();//ҽ��˳���			
		if(insureSequenceNo.length()>0)
			Array=insureSequenceNo.split("\\|");			
		String caseNo = Array[0];//�Һ�HISΨһ��	
		String confirmNo = "";
		if(Array.length > 1){
			 confirmNo = Array[1];//ҽ��ȷ������
		}
		
		String orderNo = bodyP.getOrderNo();
		
		writerLog2("orderNo=========="+caseNo+"===="+orderNo);
		writerLog2("caseNo=========="+caseNo+"===="+caseNo);
		writerLog2("confirmNo=========="+caseNo+"===="+confirmNo);
		Double insurepay = 0.00;
		Double accountPay = 0.00;
		writerLog2("cashPay===11="+caseNo+"===="+bodyP.getCashPay());
		Double cashpay = Double.parseDouble(bodyP.getCashPay()); //ҽ�ƿ�֧�����
		String inscrowdType ="";
		String inspayType="";
		String insType = "";
		writerLog2("cashpay=222="+caseNo+"===="+cashpay);
		
		TParm statusParm = new TParm();
		statusParm.setData("ORDER_NO", bodyP.getOrderNo());
		statusParm.setData("STATUS", "2"); //0ʧ�� 1�ɹ� 2ִ����
		statusParm.setData("OPT_USER", "QeApp");
		statusParm.setData("OPT_TERM", "0.0.0.0");
		statusParm.setData("TYPE", "REG");
		
		TParm reStatusParm = RegQETool.getInstance().insertEktAppStatus(statusParm);
		if(reStatusParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:���ױ�д��ʧ�� ");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		//ԤԼȷ��ʱ�жϸò����Ƿ���ҽ�ƿ�
		xStream = new XStream();
		String apptCode = bodyP.getAppointCode().trim();
		String mrNo = bodyP.getPatientID().trim();
		if(apptCode.length() > 0){
			TParm ektParm = RegQETool.getInstance().getEktIssuelogByMrNo(mrNo);
			writerLog2("ektParm===="+caseNo+"===="+ektParm);
			if(ektParm.getCount() < 0){
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				res.setIsSuccessed("1");
				res.setErrorMsg("ERR:�ò���û�о��￨���뵽��̨����");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}
			
		}
		writerLog2("apptCode===="+caseNo+"===="+apptCode);
		TParm insParm =null;
		//ҽ������
		if(confirmNo.length() > 0) {
		parm.setData("CASE_NO", caseNo);				
        parm.setData("CONFIRM_NO",confirmNo);	
		// ��ȡҽ������
		insresultData = RegQETool.getInstance().getInsOpd(parm);
		writerLog2("insresultData=========="+caseNo+"===="+insresultData);
		if (insresultData.getErrCode() < 0 || insresultData.getCount() <= 0) {	
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			res.setIsSuccessed("1");
			res.setErrorMsg("��������");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		insParm = new TParm();
		insParm.setData("CONFIRM_NO",confirmNo);
		TParm regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
		//�ж�ҽ������(��ְ ��ͨ����ְ���� ���Ǿ�����)
		
		inscrowdType =insresultData.getValue("INS_CROWD_TYPE", 0);
		inspayType =insresultData.getValue("INS_PAT_TYPE", 0);
		if(inscrowdType.equals("1")&&inspayType.equals("1"))
			insType = "1";
		if(inscrowdType.equals("1")&&inspayType.equals("2"))
			insType = "2";
		if(inscrowdType.equals("2")&&inspayType.equals("2"))
			insType = "3";
		insParm.setData("INS_TYPE", insType);//1.��ְ ��ͨ 2.��ְ���� 3.�Ǿ�����
		insParm.setData("CASE_NO", caseNo);
		insParm.setData("OPT_USER", "QeApp");
		insParm.setData("OPT_TERM",  "0.0.0.0");
		insParm.setData("RECP_TYPE", "REG");
		insParm.setData("DISEASE_CODE", insresultData.getValue("DISEASE_CODE", 0));//���ز���
		insParm.setData("FeeY", insresultData.getDouble("INS_TOT_AMT", 0));//(�ܽ��)�Ǿ�����ʹ��
		writerLog2("insParm=========="+caseNo+"===="+insParm);
		TParm opbReadCardParm = new TParm();		
		opbReadCardParm.setData("TOT_AMT", insresultData.getDouble("TOT_AMT", 0));
		opbReadCardParm.setData("PERSON_ACCOUNT_AMT", insresultData.getDouble("TOT_AMT", 0));
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		opbReadCardParm.setData("PAT_TYPE", insresultData.getValue("PAT_TYPE", 0));
		opbReadCardParm.setData("PAY_KIND", insresultData.getValue("PAY_KIND", 0));			
		TParm settlementDetailsParm = new TParm();
		settlementDetailsParm.setData("INS_PAY_AMT", insresultData.getDouble(
				"INS_PAY_AMT", 0));
		settlementDetailsParm.setData("UNREIM_AMT", insresultData.getDouble(
				"UNREIM_AMT", 0));
		settlementDetailsParm.setData("OINSTOT_AMT", insresultData.getDouble(
				"OINSTOT_AMT", 0));
		settlementDetailsParm.setData("OWN_AMT", insresultData.getDouble(
				"OWN_AMT", 0));
		settlementDetailsParm.setData("ILLNESS_SUBSIDY_AMT", insresultData.getDouble(
				"ILLNESS_SUBSIDY_AMT", 0));
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		insParm.setData("settlementDetailsParm", settlementDetailsParm
				.getData());
		writerLog2("insParm=====WW====="+caseNo+"===="+insParm);
		resultData = INSTJReg.getInstance().insCommFunction(insParm.getData());
		if (resultData.getErrCode() < 0) {
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			res.setIsSuccessed("1");
			res.setErrorMsg("ҽ��֧��ʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;					
		}
		if(insType.equals("1")||insType.equals("2")){
			insurepay = insresultData.getDouble("INS_TOT_AMT", 0) - // �ܽ��
			insresultData.getDouble("UNACCOUNT_PAY_AMT", 0) - // ���˻�֧��
			insresultData.getDouble("UNREIM_AMT", 0);// ����δ����
			cashpay =insresultData.getDouble("UNACCOUNT_PAY_AMT", 0)+         
			insresultData.getDouble("UNREIM_AMT", 0);// �ֽ�֧�����
	        accountPay = insresultData.getDouble("ACCOUNT_PAY_AMT", 0);	
		}
        if(insType.equals("3")){
        	if (null != insresultData.getValue("REIM_TYPE", 0)
					&& insresultData.getInt("REIM_TYPE", 0) == 1) {
        		insurepay = insresultData.getDouble("TOTAL_AGENT_AMT", 0)
						+ insresultData.getDouble("ARMY_AI_AMT", 0)
						+ insresultData.getDouble("FLG_AGENT_AMT", 0)
						+ insresultData.getDouble("ILLNESS_SUBSIDY_AMT", 0)//����󲡽��
						- insresultData.getDouble("UNREIM_AMT", 0);
			} else {
				insurepay = insresultData.getDouble("TOTAL_AGENT_AMT", 0)
						+ insresultData.getDouble("FLG_AGENT_AMT", 0)
						+ insresultData.getDouble("ARMY_AI_AMT", 0)
				        + insresultData.getDouble("ILLNESS_SUBSIDY_AMT", 0);//����󲡽��
			}
			// ����ʵ��֧��
        	cashpay = insresultData.getDouble("INS_TOT_AMT", 0)
					- insresultData.getDouble("TOTAL_AGENT_AMT", 0)
					- insresultData.getDouble("FLG_AGENT_AMT", 0)
					- insresultData.getDouble("ARMY_AI_AMT", 0)
					- insresultData.getDouble("ILLNESS_SUBSIDY_AMT", 0)//����󲡽��
					+ insresultData.getDouble("UNREIM_AMT", 0);	
		}			
	}
		//д�����ݿ�ʱҽ��ע�����
		//д��reg_patadm��ʱע��confirmNo��Ϊ��
		//д��bil_reg_recp��ʱpay_ins_cardҽ�����Ϊinsurepay+accountPay֮��	
		
		//---start---�Һ�
		String visitCode=bodyP.getVisitCode();
		//Ժ��+�������+��������+ʱ��+���
		String [] names= visitCode.split("#");
		String clinicRoomNo=names[4];
		String regionCode=names[0];
		String admType=names[1];
		String date=names[2];
		String sessionCode=names[3];
		
		TParm regInfo = RegQETool.getInstance().getRegInfo(caseNo);
		if(regInfo.getCount() < 0){
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("Err:δȡ�ùҺ���Ϣ");
			xStream.alias("Response", RegResponse.class);
			 outXml = xStream.toXML(res);
			return outXml;
		}
		String clinictypeCode=regInfo.getValue("CLINICTYPE_CODE", 0);
		String queNo = regInfo.getValue("QUE_NO", 0);
		
		// ȡ�����ݿ�����
		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		if (conn == null){
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("Err:δȡ�����ݿ�����");
			xStream.alias("Response", RegResponse.class);
			 outXml = xStream.toXML(res);
			return outXml;
		}

		
		
		double rFee = BIL.getRegDetialFee("O", TypeTool
				.getString(clinictypeCode), "REG_FEE", TypeTool
				.getString(regInfo.getValue("CTZ1_CODE", 0)), TypeTool
				.getString(regInfo.getValue("CTZ2_CODE", 0)), TypeTool
				.getString(regInfo.getValue("CTZ3_CODE", 0)), TypeTool
				.getString(regInfo.getValue("SERVICE_LEVEL", 0)));
		
		
		double cFee = BIL.getRegDetialFee("O", TypeTool
				.getString(clinictypeCode), "CLINIC_FEE", TypeTool
				.getString(regInfo.getValue("CTZ1_CODE", 0)), TypeTool
				.getString(regInfo.getValue("CTZ2_CODE", 0)), TypeTool
				.getString(regInfo.getValue("CTZ3_CODE", 0)), TypeTool
				.getString(regInfo.getValue("SERVICE_LEVEL", 0)));
		//������Ҫ��������10ԪǮ  add by huangtt 20170330
		cFee = cFee + PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee");
		
		double arAmt = rFee+cFee;

		TParm regParm = new TParm();
		regParm.setData("CASE_NO", caseNo);
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG", "RECEIPT_NO", "RECEIPT_NO");
		if (receiptNo.length() == 0) {
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			conn.rollback();
			conn.close();
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("Err:�վݺ�����");
			xStream.alias("Response", RegResponse.class);
			 outXml = xStream.toXML(res);
			return outXml;
		}
		regParm.setData("RECEIPT_NO", receiptNo);
		regParm.setData("ADM_TYPE", admType);
		regParm.setData("REGION_CODE", regionCode);
		regParm.setData("MR_NO", mrNo);
		
		regParm.setData("REG_FEE", rFee);
		regParm.setData("CLINIC_FEE", cFee);
		regParm.setData("AR_AMT", insurepay+cashpay);
		regParm.setData("PAY_CASH", 0);  //�ֽ�֧�� 
		regParm.setData("PAY_BANK_CARD", 0); //���п�֧�� 
		regParm.setData("PAY_CHECK", 0); //֧Ʊ֧��
		regParm.setData("PAY_MEDICAL_CARD", 0); //ҽ�ƿ�֧��
		regParm.setData("PAY_INS_CARD", insurepay); //ҽ����֧�� 
		regParm.setData("PAY_DEBIT", 0); //����֧��
		regParm.setData("PAY_INS", 0); //�ż����������
		regParm.setData("REMARK", 0); //��ע(д֧Ʊ��)
		regParm.setData("ALIPAY", 0); //֧����
		regParm.setData("QE_PAY_TYPE", ""); //Q֧������
		
		//ҽ�ƿ�֧����ʽ
		if(bodyP.getPaymentType().equals("01")){
			regParm.setData("PAY_MEDICAL_CARD", cashpay); //ҽ�ƿ�֧��
		}
		//֧����֧����ʽ 02 ֧����  6 ΢�� 
		if(bodyP.getPaymentType().equals("02") ){
			regParm.setData("ALIPAY", cashpay); //֧����
			regParm.setData("QE_PAY_TYPE", "2"); //֧����
		}
		
		if( bodyP.getPaymentType().equals("6")){
			regParm.setData("ALIPAY", cashpay); //΢�� 
			regParm.setData("QE_PAY_TYPE", "6");
		}
		

		regParm.setData("CASH_CODE", "QeApp");
		regParm.setData("RE_SOURCE", getBussinessDesc(bodyP.getBusinessFrom())); 
		regParm.setData("OPT_USER", getBussinessDesc(bodyP.getBusinessFrom())); 
		regParm.setData("OPT_TERM", bodyP.getTerminalCode()); 
		regParm.setData("ORDER_NO", bodyP.getOrderNo()); 
		
		TParm resultParm = RegQETool.getInstance().saveBilRegRecp(regParm, conn);
		if (resultParm.getErrCode() < 0) {
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			conn.rollback();
			conn.close();
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("saveBilRegRecp Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
			
		}
		resultParm = RegQETool.getInstance().updateRegPatadm(caseNo,insType,confirmNo, conn);
		if (resultParm.getErrCode() < 0) {
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			conn.rollback();
			conn.close();
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("updateRegPatadm Err:"+resultParm);
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
			
		}
		//ҽ������
		if(confirmNo.length() > 0) {
	  //����ҽ�����ױ�INS_OPD�е�receipt_no 
		TParm resultUpdate = RegQETool.getInstance().updateInsOpd(receiptNo,confirmNo,caseNo);
		if (resultUpdate.getErrCode() < 0) {
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			res.setIsSuccessed("1");
			res.setErrorMsg("����ҽ�����ױ�ʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;					
	 	   }
	    }
	
		if(bodyP.getPaymentType().equals("01")){
			String treadNo=RegQETool.getInstance().getTradeNo(bodyP.getOrderNo());
			resultParm = RegQETool.getInstance().updateEktTradeCaseNo(treadNo, caseNo, conn);
			if (resultParm.getErrCode() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("updateEktTradeCaseNo Err:"+resultParm);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
				
			}
			
			resultParm = RegQETool.getInstance().updateEktMasterHistoryCaseNo(treadNo, caseNo, confirmNo, conn);
			if (resultParm.getErrCode() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("updateEktMasterHistoryCaseNo Err:"+resultParm);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
				
			}
			
		}
		
		//֧����֧����ʽ
		if(bodyP.getPaymentType().equals("02") || bodyP.getPaymentType().equals("6")){
			
			 //����caseNo�õ�ҽ�ƿ��ţ������ţ������
			 TParm ektParm = RegQETool.getInstance().getEktParm(caseNo);
			if (ektParm.getCount() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("ERR:�ò���û�о��￨���뵽��̨����");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}
			 
			 //��ҽ�ƿ��г�Ǯ����ҽ�ƿ���Ǯ
			 TParm bilDetailParm = new TParm();
			 String billBusinessNo=SystemTool.getInstance().getNo("ALL", "EKT", "BUSINESS_NO","BUSINESS_NO");
			 if (billBusinessNo.length() == 0) {
				 RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("Err:ҽ�ƿ���ֵ���׺����������");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
			 
			 bilDetailParm.setData("BUSINESS_NO", billBusinessNo);
			 bilDetailParm.setData("BUSINESS_SEQ", "0");
			 bilDetailParm.setData("CARD_NO", ektParm.getValue("CARD_NO",0));
			 bilDetailParm.setData("MR_NO", ektParm.getValue("MR_NO",0));
			 bilDetailParm.setData("CASE_NO", "none");	 
			 bilDetailParm.setData("SEQ_NO", 0);
			 if(bodyP.getPaymentType().equals("02")){
				 bilDetailParm.setData("ORDER_CODE", "֧������ֵ");
				 bilDetailParm.setData("RX_NO", "֧������ֵ");
				 bilDetailParm.setData("CHARGE_FLG", "0"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����;0,֧������ֵ;9,΢�ų�ֵ)
				
			 }
			 if(bodyP.getPaymentType().equals("6")){
				 bilDetailParm.setData("ORDER_CODE", "΢�ų�ֵ");
				 bilDetailParm.setData("RX_NO", "΢�ų�ֵ");
				 bilDetailParm.setData("CHARGE_FLG", "9"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����;0,֧������ֵ;9,΢�ų�ֵ)
				
			 }
			 
			
			 bilDetailParm.setData("ORIGINAL_BALANCE", ektParm.getDouble("CURRENT_BALANCE",0)); //�շ�ǰ���
			 bilDetailParm.setData("BUSINESS_AMT", cashpay);			 
			 double currentBalance = cashpay+ektParm.getDouble("CURRENT_BALANCE",0);
			 bilDetailParm.setData("CURRENT_BALANCE", currentBalance);
			 bilDetailParm.setData("CASHIER_CODE", "QeApp");
			 bilDetailParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
			 bilDetailParm.setData("BUSINESS_STATUS", "1");
			 bilDetailParm.setData("ACCNT_STATUS", "0");
			 bilDetailParm.setData("OPT_USER", "QeApp");
			 bilDetailParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
			 bilDetailParm.setData("OPT_TERM", "0.0.0.0");
			 TParm insertEKTDetail = RegQETool.getInstance().insertEKTDetail(bilDetailParm, conn);
			 if(insertEKTDetail.getErrCode() < 0){
				 RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				    conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEKTDetail Err:"+insertEKTDetail);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
			 }
			 
			//��ֵдҽ�ƿ���ʷ��
			 int seq = RegQETool.getInstance().getHistorySeq(bilDetailParm.getValue("MR_NO"));
			 TParm ektMasHisParm = new TParm();  
			 ektMasHisParm.setData("MR_NO", bilDetailParm.getData("MR_NO"));
			 ektMasHisParm.setData("OLD_AMT", bilDetailParm.getDouble("ORIGINAL_BALANCE"));
			 ektMasHisParm.setData("AMT", bilDetailParm.getDouble("BUSINESS_AMT"));
			 ektMasHisParm.setData("EKT_AMT", bilDetailParm.getDouble("CURRENT_BALANCE"));
			 ektMasHisParm.setData("BUSINESS_TYPE", "EKT_IN"); //��ֵ
			 ektMasHisParm.setData("CARD_NO", bilDetailParm.getValue("CARD_NO"));
			 ektMasHisParm.setData("OPT_USER", bilDetailParm.getValue("OPT_USER"));
			 ektMasHisParm.setData("OPT_TERM", bilDetailParm.getValue("OPT_TERM"));
			 ektMasHisParm.setData("CASE_NO", "");
			 ektMasHisParm.setData("CONFIRM_NO", "");		
			 ektMasHisParm.setData("EKT_BUSINESS_NO", bilDetailParm.getValue("BUSINESS_NO"));
			 ektMasHisParm.setData("HISTORY_NO", RegQETool.getInstance().getHistoryNo());
			 ektMasHisParm.setData("SEQ", seq);
			TParm insertEktMasterHistory = RegQETool.getInstance().insertEktMasterHistory(ektMasHisParm, conn);
			if(insertEktMasterHistory.getErrCode() < 0){
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				 conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktMasterHistory Err:"+insertEktMasterHistory);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
			}

		        
		        
			TParm billParm = new TParm();
			billParm.setData("BIL_BUSINESS_NO", billBusinessNo);
			billParm.setData("CARD_NO", ektParm.getValue("CARD_NO", 0)); // ����
			billParm.setData("CURT_CARDSEQ", 0); // ���
			if(bodyP.getPaymentType().equals("6")){
				billParm.setData("ACCNT_TYPE", "8"); // ��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�,7:֧������ֵ��8��΢�ų�ֵ)
			}
			if(bodyP.getPaymentType().equals("02")){
				billParm.setData("ACCNT_TYPE", "7"); // ��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�,7:֧������ֵ)
			}
			
			billParm.setData("MR_NO", ektParm.getValue("MR_NO", 0)); // ������
			billParm.setData("ID_NO", ektParm.getValue("IDNO", 0)); // ���֤��
			billParm.setData("NAME", ektParm.getValue("PAT_NAME", 0)); // ��������
			billParm.setData("AMT", cashpay); // ��ֵ���
			billParm.setData("CREAT_USER", "QeApp"); // ִ����Ա
			billParm.setData("OPT_USER", "QeApp"); // ������Ա
			billParm.setData("OPT_TERM", "0.0.0.0"); // ִ��ip
			billParm.setData("GATHER_TYPE", "A"); // ֧����ʽ
			// zhangp 20120109
			billParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());
			billParm.setData("PROCEDURE_AMT", "0");
			TParm insertEKTBilPay = RegQETool.getInstance().insertEKTBilPay(billParm, conn);
			if(insertEKTBilPay.getErrCode() < 0){
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("insertEKTBilPay Err:" + insertEKTBilPay);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}
			
			
			
			TParm ektTradeParm = new TParm();
			String treadNo = SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO","TRADE_NO");
			if(treadNo.length() == 0){	
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("Err:ҽ�ƿ����׺����������");
				xStream.alias("Response", RegResponse.class);
				 outXml = xStream.toXML(res);
				return outXml;
			}
			ektTradeParm.setData("TRADE_NO", treadNo);
			ektTradeParm.setData("CARD_NO", ektParm.getValue("CARD_NO", 0));
			ektTradeParm.setData("MR_NO", ektParm.getValue("MR_NO", 0));
			ektTradeParm.setData("CASE_NO", caseNo);
			ektTradeParm.setData("PAT_NAME", ektParm.getValue("PAT_NAME", 0));
			ektTradeParm.setData("OLD_AMT", currentBalance);
			ektTradeParm.setData("AMT", cashpay);
			ektTradeParm.setData("STATE", "1");
			ektTradeParm.setData("BUSINESS_TYPE", "REG");
			
			ektTradeParm.setData("GREEN_BALANCE", "0");
			ektTradeParm.setData("GREEN_BUSINESS_AMT", "0");
			ektTradeParm.setData("OPT_USER", "QeApp");		
			ektTradeParm.setData("OPT_TERM", "0.0.0.0");
			
			ektTradeParm.setData("BIL_BUSINESS_NO", billBusinessNo);
			if(bodyP.getPaymentType().equals("6")){
				ektTradeParm.setData("BUSINESS_APP_TYPE", "10");  //10���Һ�΢�ų�ֵ 11���ɷ�΢�ų�ֵ
			}
			if(bodyP.getPaymentType().equals("02")){
				ektTradeParm.setData("BUSINESS_APP_TYPE", "8");  //8���Һ�֧������ֵ 9���ɷ�֧������ֵ
			}
			
			
			String orderTime =SystemTool.getInstance().getDate().toString();
			orderTime=orderTime.replaceAll("-", "").replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14);
			ektTradeParm.setData("ORDER_TIME", orderTime);
			ektTradeParm.setData("ORDER_NO", orderNo);
			
			 resultParm = RegQETool.getInstance().insertEktAppTrade(ektTradeParm, conn);
			if (resultParm.getErrCode() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("insertEktAppTrade Err:"+resultParm);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
				
			}
			
			resultParm = RegQETool.getInstance().insertEktTrade(ektTradeParm, conn);
			if (resultParm.getErrCode() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("insertEktTrade Err:"+resultParm);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
				
			}
			
			seq = seq + 1;
			ektTradeParm.setData("HISTORY_NO", RegQETool.getInstance().getHistoryNo());// �ڲ����׺�
			ektTradeParm.setData("EKT_AMT", currentBalance-cashpay);// �ڲ����׺�
			ektTradeParm.setData("EKT_BUSINESS_NO", "");
			ektTradeParm.setData("CONFIRM_NO", confirmNo);
			ektTradeParm.setData("SEQ", seq);
			
			resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeParm, conn);
			if (resultParm.getErrCode() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("insertEktMasterHistory Err:"+resultParm);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
				
			}
			
		}
		

		conn.commit();
		conn.close();
		callNo("REG", caseNo);
		
		//---end---�Һ�
		
		
		xStream = new XStream();
		res.setIsSuccessed("0");//�Ƿ�ɹ�(0:�ɹ�,1:ʧ��)
		res.setErrorMsg("");//������Ϣ
		Result result = new Result();
		result.setQueueNo(queNo);//�ŶӺ�  
		result.setRegID(caseNo);//�Һ�ID 
		result.setInsurePay(df.format(insurepay-accountPay));//ҽ��֧��
		result.setAccountPay(df.format(accountPay));//�����˻�֧��
		result.setCashPay(df.format(cashpay));//����ʵ��֧��
		String visitAddrss = RegQETool.getInstance().getClinicroom(clinicRoomNo);
		result.setVisitAddress(visitAddrss);//�����ַ
		result.setComment("ҽ�ƿ���"+RegQETool.getInstance().getEktParm(caseNo).getValue("CURRENT_BALANCE", 0));//��ע
		res.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);
		outXml = xStream.toXML(res);
		
		TParm sParm = new TParm();
		sParm.setData("ORDER_NO", bodyP.getOrderNo());
		sParm.setData("STATUS", "1");
		sParm.setData("QUEUN_NO", queNo);
		sParm.setData("VISITADDRESS", visitAddrss);
		sParm.setData("CASE_NO", caseNo);
		sParm.setData("RECEIPT_NO", receiptNo);
		sParm.setData("ACCOUNTPAY", df.format(accountPay));
		sParm.setData("CASHPAY", df.format(cashpay));
		sParm.setData("INSUREPAY", df.format(insurepay-accountPay));
		sParm.setData("PAYMENTTYPE", bodyP.getPaymentType());
		sParm.setData("CARD_NO", RegQETool.getInstance().getEktAppTrade(bodyP.getOrderNo()).getValue("CARD_NO", 0));
		
		RegQETool.getInstance().updateEktAppStatusParm(sParm);
		
		return outXml;
	}
	
	public String onInsSplit(String InXml){
		String outXml="";
		
//		InXml="<?xml version=\"1.0\"?><Request><HeaderParams></HeaderParams>"
//				+ " <BodyParams><PatientID>000000000332</PatientID> <MedCardNo>6221511100057373249</MedCardNo>"
//				+ " <IDCardNo>120111</IDCardNo><GIDCardNo></GIDCardNo><MedCardPwd>111111</MedCardPwd>"
//				+ "<InsureTypeCode>0</InsureTypeCode><MtTypeCode>4</MtTypeCode><InsureSequenceNo>170524000001|MT05511705043931387</InsureSequenceNo>"
//				+ "<RegFee>0.0000</RegFee><InspectFee>15</InspectFee><TotalFee>30.0000</TotalFee><DeptCode>030101</DeptCode>"
//				+ "<IsEmergency></IsEmergency><DoctorCode>000110</DoctorCode><VisitTypeCode>01</VisitTypeCode>"
//				+ "<VisitCode>H01#O#20170524#01#A0104#000110</VisitCode><AppointCode>170524000001</AppointCode><PatientName>����ϲ</PatientName>"
//				+ "<PatientAge>65</PatientAge><PatientSex>1</PatientSex><BusinessFrom>01</BusinessFrom><TerminalCode>ZZJ002</TerminalCode>"
//				+ "</BodyParams></Request>";
		
		InXml = RegQETool.getInstance().updateXml(InXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(InXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		Result result = new Result();
		String caseNo ="";
		String confirmNo = "";
		boolean regUpdateFlg = false;
		
		// Ժ��+�������+��������+ʱ��+���		
		String[] names = bodyP.getVisitCode().split("#");
		String clinicRoomNo = names[4];
		String regionCode = names[0];
		String admType = names[1];
		String admDate = names[2];
		String sessionCode = names[3];
		String drCode = names[5];
		// ȡ�ð����Ϣ
		TParm parmSchDay =RegQETool.getInstance().getREGSchdayInfo(clinicRoomNo, regionCode, admType,
				admDate, sessionCode, drCode);
		if (!parmSchDay.getValue("CLINICTYPE_CODE", 0).equals(
				bodyP.getVisitTypeCode())) {
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:����ű���HISϵͳ ����кű�ͬ������ϵQҽ��Ա���к˲�");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}

		//��ѯ�����Ƿ�Ϊҽ�������غţ��еĻ��Ͳ�����Һ�         
		String mrNo = bodyP.getPatientID();
		
		TParm insTypeParm = RegQETool.getInstance().getInsType(mrNo, admDate);
		writerLog2("insTypeParm----"+insTypeParm);
		writerLog2("insTypeCode----"+bodyP.getInsureTypeCode());
		if(bodyP.getInsureTypeCode().equals("1")){
			if(insTypeParm.getCount()>0){
				if(insTypeParm.getValue("INS_CROWD_TYPE", 0).equals("1") &&
						insTypeParm.getValue("INS_PAT_TYPE", 0).equals("1")
						){
					  res.setIsSuccessed("1");
					  res.setErrorMsg("�������Ѵ���ҽ���Һţ�����ѯ��̨");
					  xStream.alias("Response", RegResponse.class);
					  outXml = xStream.toXML(res);
					  return outXml;
				}
			}
		}
		if(bodyP.getInsureTypeCode().equals("2")){
			if(insTypeParm.getCount()>0){
				if((insTypeParm.getValue("INS_CROWD_TYPE", 0).equals("1") &&
						insTypeParm.getValue("INS_PAT_TYPE", 0).equals("2")) ||
						(insTypeParm.getValue("INS_CROWD_TYPE", 0).equals("2") &&
								insTypeParm.getValue("INS_PAT_TYPE", 0).equals("2"))
						){
					  res.setIsSuccessed("1");
					  res.setErrorMsg("�������Ѵ���ҽ���Һţ�����ѯ��̨");
					  xStream.alias("Response", RegResponse.class);
					  outXml = xStream.toXML(res);
					  return outXml;
				}
			}
		}
		
		if(bodyP.getInsureTypeCode().equals("1") ||
				bodyP.getInsureTypeCode().equals("2")){
			if(insTypeParm.getCount()>0){
				if(drCode.equals(insTypeParm.getValue("REALDR_CODE", 0))){
					 res.setIsSuccessed("1");
					  res.setErrorMsg("�������Ѵ���ҽ���Һţ�����ѯ��̨");
					  xStream.alias("Response", RegResponse.class);
					  outXml = xStream.toXML(res);
					  return outXml;
				}
			}
		}
		
		
		
		
		 //���չҺ�

		if(bodyP.getAppointCode().trim()==null|| 
		   bodyP.getAppointCode().trim().length() == 0){
			if(bodyP.getInsureSequenceNo()==null|| 
					bodyP.getInsureSequenceNo().trim().length() == 0){
				
				 caseNo =SystemTool.getInstance().getNo("ALL", "REG",
			                "CASE_NO", "CASE_NO");

			}else{
				String [] Array=new String [0];
				String insureSequenceNo = bodyP.getInsureSequenceNo();//ҽ��˳���			
				if(insureSequenceNo.length()>0)
					Array=insureSequenceNo.split("\\|");			
			caseNo = Array[0];//�Һ�HISΨһ��	
			regUpdateFlg = true;

			}					
		}
		else{
			//ԤԼ����
			caseNo = bodyP.getAppointCode();
			regUpdateFlg = true;

		}
			
		writerLog2("caseNo=========="+caseNo+"==");
		 writerLog2("caseNo=========="+caseNo.length());
		 
		 
		TParm patInfo = new TParm(TJDODBTool.getInstance().select("SELECT * FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'"));				
		String ctz2Code=RegQETool.getInstance().getCtz2Code(parmSchDay.getValue("CLINICTYPE_CODE", 0),patInfo);
		TParm regInfo = RegQETool.getInstance().getRegInfo(caseNo);
		if(regInfo.getCount() > 0){	
			//����йҺ���Ϣ���жϹҺ���Ϣ�봫�����Ƿ���ͬ
			
			if(!(admDate.equals(regInfo.getValue("ADM_DATE", 0).replaceAll("/", "").replaceAll("-", "").substring(0, 8)) &&
					sessionCode.equals(regInfo.getValue("SESSION_CODE", 0)) &&
					admType.equals(regInfo.getValue("ADM_TYPE", 0)) &&
					regionCode.equals(regInfo.getValue("REGION_CODE", 0)) &&
					clinicRoomNo.equals(regInfo.getValue("CLINICROOM_NO", 0)) &&
					drCode.equals(regInfo.getValue("DR_CODE", 0)) &&
					bodyP.getVisitTypeCode().equals(regInfo.getValue("CLINICTYPE_CODE", 0)))
					){
				String r1=RegQETool.getInstance().getClinicroomDesc(regInfo.getValue("CLINICROOM_NO", 0)) +" "+
						RegQETool.getInstance().getDeptDesc(regInfo.getValue("DEPT_CODE", 0)) +" "+
						RegQETool.getInstance().getClinictypeDesc(regInfo.getValue("CLINICTYPE_CODE", 0))+" "+
						RegQETool.getInstance().getDrDesc(regInfo.getValue("DR_CODE", 0));
				
				String r2=RegQETool.getInstance().getClinicroomDesc(clinicRoomNo) +" "+
						RegQETool.getInstance().getDeptDesc(parmSchDay.getValue("REALDEPT_CODE", 0)) +" "+
						RegQETool.getInstance().getClinictypeDesc(bodyP.getVisitTypeCode())+" "+
						RegQETool.getInstance().getDrDesc(drCode);
				
				
				res.setIsSuccessed("1");
				res.setErrorMsg("ERR:�Һ���Ϣ�봫������Ϣ�������Һ���ϢΪ��"+r1+"����������ϢΪ��"+r2+"������ϵQҽ��Ա���к˲�");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}
			
			
			
			if(regInfo.getValue("CTZ2_CODE", 0).length() > 0){
				ctz2Code=regInfo.getValue("CTZ2_CODE", 0);
			}else{
				if(ctz2Code.length() > 0){
					TParm updateRegCtz = RegQETool.getInstance().updateRegCTZ2(caseNo, ctz2Code);
				}
			}
		}

		//ҽ������(0.�Է�  1.ҽ��   2.���� )
		if(bodyP.getInsureTypeCode().equals("1")||
		   bodyP.getInsureTypeCode().equals("2"))
		{				
		 writerLog2("MedCardNo=========="+bodyP.getMedCardNo());
		//��ҽ����
		if (bodyP.getMedCardNo().trim() == null
				|| bodyP.getMedCardNo().trim().length() <= 0) {
			res.setIsSuccessed("1");
			res.setErrorMsg("ҽ������Ϊ��,�����»��ҽ����");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
			TParm parm = new TParm();
			TParm resultData = new TParm();
		    TParm insParm = new TParm(); // ��ҽ�������ز���
			TParm insFeeParm = new TParm();
			TParm regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
			parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));
			parm.setData("DISEASE_CODE", bodyP.getMtTypeCode());//��������(4.����)
			parm.setData("TEXT", ";"+(String)bodyP.getMedCardNo()+"=");
			parm.setData("OPT_USER", "QeApp");
			parm.setData("OPT_TERM", "0.0.0.0");
			parm.setData("PASSWORD", bodyP.getMedCardPwd());//����
			parm.setData("MR_NO", bodyP.getPatientID());//������
			
			 writerLog2("admDate=========="+admDate);
			parm.setData("ADVANCE_CODE",regionParm.getValue("NHI_NO", 0)+"@"+
					admDate+"@"+"1");//ҽԺ����@���÷���ʱ��@���
			 writerLog2("parm=========="+parm);
			insParm = RegQETool.getInstance().readCard(parm);	
			writerLog2("insParm=========="+insParm);
			// ҽ������
			if (null == insParm || insParm.getErrCode() < 0
					|| null == insParm.getValue("CARD_NO")
					|| insParm.getValue("CARD_NO").length() <= 0) {
		    	  writerLog2("ҽ��========"); 
		    	  res.setIsSuccessed("1");
				  res.setErrorMsg("ҽ������ȡʧ��");
				  xStream.alias("Response", RegResponse.class);
				  outXml = xStream.toXML(res);
				  return outXml;
			}	
			
			//��ѯ�����Ƿ����ҽ��У��
			int crowdType = insParm.getInt("CROWD_TYPE"); // ҽ����ҽ��� 1.��ְ 2.�Ǿ�
			String insType = insParm.getValue("INS_TYPE"); // ҽ����������: 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
			//�жϲ��˵�ҽ������
			if(crowdType==2&&bodyP.getInsureTypeCode().equals("1")){
				res.setIsSuccessed("1");
				  res.setErrorMsg("��ҽ����Ϊ�Ǿӿ���������غ�");
				  xStream.alias("Response", RegResponse.class);
				  outXml = xStream.toXML(res);
				  return outXml;		
			}
			TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
			String sql = "";
			String name = "";
			if (insType.equals("1")) {
				name = opbReadCardParm.getValue("NAME");
				sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
						+ opbReadCardParm.getValue("SID").trim()
						+ "' AND PAT_NAME='" + name.trim() + "'";
			} else {
				name = opbReadCardParm.getValue("PAT_NAME");
				sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
						+ opbReadCardParm.getValue("SID").trim()
						+ "' AND PAT_NAME='" + name.trim() + "'";
			}
			TParm insPresonParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (insPresonParm.getErrCode() < 0) {
		    	  res.setIsSuccessed("1");
				  res.setErrorMsg("��ò�����Ϣʧ��");
				  xStream.alias("Response", RegResponse.class);
				  outXml = xStream.toXML(res);
				  return outXml;
			}
			if (insPresonParm.getCount("MR_NO") <= 0) {
	              res.setIsSuccessed("1");
	              if(opbReadCardParm.getValue("SID").length()<=0){
	            	  if(bodyP.getInsureTypeCode().equals("1"))
	            	  res.setErrorMsg("ҽ�������ڴ����û�");
	            	  else if(bodyP.getInsureTypeCode().equals("2"))
	            	  res.setErrorMsg("���ز����ڴ����û�");	  	            		  
	              }	            	  		             
		           else	              
				  res.setErrorMsg("��ҽ������������ҽ�ƿ���Ϣ,\nҽ����Ϣ:���֤����:"
							+ opbReadCardParm.getValue("SID") + "\nҽ����������:" + name);
				  xStream.alias("Response", RegResponse.class);
				  outXml = xStream.toXML(res);
				  return outXml;
			}
			if (insPresonParm.getCount("MR_NO") == 1) {
				if (bodyP.getPatientID().length() > 0) {
					if (!insPresonParm.getValue("MR_NO", 0).equals(
						bodyP.getPatientID())) {
						 res.setIsSuccessed("1");
						  res.setErrorMsg("ҽ����Ϣ�벡����Ϣ����,ҽ����������:" + name);
						  xStream.alias("Response", RegResponse.class);
						  outXml = xStream.toXML(res);
						  return outXml;	
					}
				}
			} else if (insPresonParm.getCount("MR_NO") > 1) {
				int flg = -1;
				if (bodyP.getPatientID().length() > 0) {
					for (int i = 0; i < insPresonParm.getCount("MR_NO"); i++) {
						if (insPresonParm.getValue("MR_NO", i).equals(
								bodyP.getPatientID())) {
							flg = i;
							break;
						}
					}
					if (flg == -1) {						
						res.setIsSuccessed("1");
						  res.setErrorMsg("ҽ����Ϣ�벡����Ϣ����,ҽ����������:" + name);
						  xStream.alias("Response", RegResponse.class);
						  outXml = xStream.toXML(res);
						  return outXml;
					}
				}
			}			
			confirmNo = (String) insParm.getData("CONFIRM_NO");
			if(confirmNo.length() == 0 ){
				  res.setIsSuccessed("1");
				  res.setErrorMsg("ҽ��ȷ�Ϻ�Ϊ��");
				  xStream.alias("Response", RegResponse.class);
				  outXml = xStream.toXML(res);
				  return outXml;	
			}
			
			parm.setData("CONFIRM_NO", confirmNo); // ����ҽ��ȷ�Ϻ�
			parm.setData("CTZ1_CODE", insParm.getData("CTZ_CODE")); // �������					
			parm.setData("CASE_NO", caseNo);//�����
			parm.setData("ADM_TYPE", "O");//�ż����
			parm.setData("CLINICTYPE_CODE", bodyP.getVisitTypeCode());//�ű���
//			String admType = (String) parm.getData("ADM_TYPE");

			double reg_fee = BIL.getRegDetialFee("O", TypeTool
					.getString(bodyP.getVisitTypeCode()), "REG_FEE", TypeTool
					.getString(insParm.getData("CTZ_CODE")),  TypeTool
					.getString(ctz2Code), "", "1");
			
			parm.setData("REG_FEE", reg_fee);
			
			double clinic_fee = BIL.getRegDetialFee("O", TypeTool
					.getString(bodyP.getVisitTypeCode()), "CLINIC_FEE", TypeTool
					.getString(insParm.getData("CTZ_CODE")),  TypeTool
					.getString(ctz2Code), "", "1");
			parm.setData("CLINIC_FEE", clinic_fee);
			
			//������Ҫ��������10ԪǮ  add by huangtt 20170330
			
			TParm mtParm=PatAdmTool.getInstance().getMTClinicFee(insParm);

			parm.setData("MT_CLINIC_FEE", mtParm.getDouble("fee"));
			parm.setData("MT_CLINIC_FEE_CODE", mtParm.getValue("mrCliniFeeCode"));
			
			
			
			// �Һŷ�
//			double RegFee  = Double.parseDouble(bodyP.getRegFee());
//			parm.setData("REG_FEE", RegFee);
//			// ����
//			double InspectFee  = Double.parseDouble(bodyP.getInspectFee());
//			parm.setData("CLINIC_FEE", InspectFee);
			
			parm.setData("DEPT_CODE", bodyP.getDeptCode());//���ұ��
			parm.setData("DR_CODE", bodyP.getDoctorCode());//ҽʦ���
			parm.setData("OPT_DATE", SystemTool.getInstance().getDate());				
//			parm.setData("QUE_NO", "");
			 writerLog2("parm=========="+parm);
			// �ָ���ǰ����
			insFeeParm = RegQETool.getInstance().saveCardBefore(parm, insParm);
			writerLog2("insFeeParm=========="+insFeeParm);
			if (insFeeParm.getErrCode() < 0) {
				 res.setIsSuccessed("1");
				 res.setErrorMsg("ҽ������ȡʧ��");
				 xStream.alias("Response", RegResponse.class);
				 outXml = xStream.toXML(res);
				 return outXml;
			}
			parm.setData("PAT_NAME", bodyP.getPatientName());//��������
			// �ָ���
			resultData = RegQETool.getInstance().insExeFee(true, insFeeParm, parm);
			writerLog2("resultData=========="+resultData);
			if (resultData == null || resultData.getErrCode() < 0) {
				res.setIsSuccessed("1");
				res.setErrorMsg("���÷ָ�ʧ��");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}
			
			//���¹Һŵ��ĹҺ����
			if(regUpdateFlg){
				TParm updateRegCtz = RegQETool.getInstance().updateRegCTZ(caseNo, insParm.getData("CTZ_CODE").toString());
				if(updateRegCtz.getErrCode() < 0){
					res.setIsSuccessed("1");
					res.setErrorMsg("ҽ����ݸ���ʧ��");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
			}
			
			
			Double insurepay  =resultData.getDouble("OTOT_AMT")-
			                   resultData.getDouble("ACCOUNT_AMT");
			result.setInsurePay(df.format(insurepay));//ҽ��֧��
			result.setAccountPay(df.format(resultData.getDouble("ACCOUNT_AMT")));//�����˻�֧��
			result.setCashPay(df.format(resultData.getDouble("UACCOUNT_AMT")));//����ʵ��֧��						
		}else {	
			
			double sumAmt =new Double(bodyP.getTotalFee());
			regInfo = RegQETool.getInstance().getRegInfo(caseNo);
			
			if(regInfo.getCount() > 0){
				writerLog2("regInfo=========="+regInfo);
				
				double rFee = BIL.getRegDetialFee("O", TypeTool
						.getString(regInfo.getValue("CLINICTYPE_CODE", 0)), "REG_FEE", TypeTool
						.getString(regInfo.getValue("CTZ1_CODE", 0)), TypeTool
						.getString(regInfo.getValue("CTZ2_CODE", 0)), TypeTool
						.getString(regInfo.getValue("CTZ3_CODE", 0)), TypeTool
						.getString(regInfo.getValue("SERVICE_LEVEL", 0)));
				
				
				double cFee = BIL.getRegDetialFee("O", TypeTool
						.getString(regInfo.getValue("CLINICTYPE_CODE", 0)), "CLINIC_FEE", TypeTool
						.getString(regInfo.getValue("CTZ1_CODE", 0)), TypeTool
						.getString(regInfo.getValue("CTZ2_CODE", 0)), TypeTool
						.getString(regInfo.getValue("CTZ3_CODE", 0)), TypeTool
						.getString(regInfo.getValue("SERVICE_LEVEL", 0)));
				
				
				
				sumAmt = rFee+cFee;
				
				writerLog2("rFee=========="+rFee);
				writerLog2("cFee=========="+cFee);
				writerLog2("sumAmt=========="+sumAmt);
				
			}else{
				
				String ctz1Code=patInfo.getValue("CTZ1_CODE", 0);
				if(ctz1Code.length() == 0){
					ctz1Code="99";
				}
				double rFee = BIL.getRegDetialFee("O", TypeTool
						.getString(parmSchDay.getValue("CLINICTYPE_CODE", 0)), "REG_FEE", TypeTool
						.getString(ctz1Code), TypeTool
						.getString(ctz2Code), TypeTool
						.getString(""), TypeTool
						.getString("1"));
				
				
				double cFee = BIL.getRegDetialFee("O", TypeTool
						.getString(parmSchDay.getValue("CLINICTYPE_CODE", 0)), "CLINIC_FEE", TypeTool
						.getString(ctz1Code), TypeTool
						.getString(ctz2Code), TypeTool
						.getString(""), TypeTool
						.getString("1"));

				sumAmt = rFee+cFee;
				
				writerLog2("rFee=========="+rFee);
				writerLog2("cFee=========="+cFee);
				writerLog2("sumAmt=========="+sumAmt);
				
			}

			result.setInsurePay("0.00");//ҽ��֧��
			result.setAccountPay("0.00");//�����˻�֧��
			result.setCashPay(df.format(sumAmt));//����ʵ��֧��				
		}
		xStream = new XStream();			
		res.setIsSuccessed("0");//�Ƿ�ɹ�(0:�ɹ�,1:ʧ��)
		res.setErrorMsg("");//������Ϣ
		String insureSequenceNo = caseNo+"|"+confirmNo;
		result.setInsureSequenceNo(insureSequenceNo);//ҽ��˳���caseNo|comfirm_no(ͬ������)
		res.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);	
		outXml = xStream.toXML(res);
		return outXml;
	}
	
	public String onInsCancel(String InXml){
		String outXml="";
//		InXml="<?xml version=\"1.0\"?><Request><HeaderParams></HeaderParams><BodyParams>"
//				+ "<InsureSequenceNo>170504000011|MT05511705043931387</InsureSequenceNo>"
//				+ "<RegID>170504000011</RegID><PatientID>000000309651</PatientID><BusinessFrom>02</BusinessFrom>"
//				+ "<TerminalCode>APP</TerminalCode><BusinessType>1</BusinessType></BodyParams></Request>";
		
		InXml = RegQETool.getInstance().updateXml(InXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(InXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		TParm regionParm = null;// ���ҽ���������
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������	
		TParm insParm = new TParm();
		TParm resultData = new TParm();
		TParm insData = new TParm();
		String insType = "";
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
		if(bodyP.getBusinessType().equals("1"))//�Һ�
		insParm.setData("RECP_TYPE", "REG");
		if(bodyP.getBusinessType().equals("2"))//�շ�
		insParm.setData("RECP_TYPE", "OPB");			
		String insureSequenceNo = bodyP.getInsureSequenceNo();
		String [] Array=new String [0];
		if(insureSequenceNo.length()>0)
			Array=insureSequenceNo.split("\\|");			
		String caseNo = Array[0];//�Һ�HISΨһ��	
		String confirmNo = Array[1];//ҽ��ȷ������
		writerLog2("caseNo=========="+caseNo);
		writerLog2("confirmNo=========="+confirmNo);
		insParm.setData("CONFIRM_NO",confirmNo);			
		// ��ȡҽ������
		insData = RegQETool.getInstance().getInsData(caseNo,confirmNo);
		writerLog2("ҽ������==="+insData);
		insParm.setData("CASE_NO", caseNo);
		if(insData.getValue("INS_CROWD_TYPE",0).equals("1")&&
		   insData.getValue("INS_PAT_TYPE",0).equals("1"))
			insType = "1";
		if(insData.getValue("INS_CROWD_TYPE",0).equals("1")&&
		   insData.getValue("INS_PAT_TYPE",0).equals("2"))
			insType = "2";
		if(insData.getValue("INS_CROWD_TYPE",0).equals("2")&&
		   insData.getValue("INS_PAT_TYPE",0).equals("2"))
			insType = "3";
		insParm.setData("INS_TYPE", insType);//1.��ְ ��ͨ 2.��ְ���� 3.�Ǿ�����
		
		if(insType.length()==0){
			res.setIsSuccessed("1");
			res.setErrorMsg("�����ڳ�������");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;	
		}
		
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		 writerLog2("onInsCancel=====insParm=========="+insParm);
		resultData = INSTJFlow.getInstance().cancelBalance(insParm);// ȡ�����ý������
		if (resultData.getErrCode() < 0) {
			res.setIsSuccessed("1");
			res.setErrorMsg("ҽ�����׳���ʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;			
		} else {
			INSOpdTJTool.getInstance().deleteINSOpd(insParm);// ɾ������
			INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		}		
		xStream = new XStream();
		res.setIsSuccessed("0");//�Ƿ�ɹ�(0:�ɹ�,1:ʧ��)
		res.setErrorMsg("ҽ�������ɹ�");//������Ϣ
		xStream.alias("Response", RegResponse.class);
		outXml = xStream.toXML(res);	
		return outXml;
	}
	
	public String getBussinessDesc(String businessFrom){
		String desc = "QeApp";
		if("01".equals(businessFrom)){
			//������ 
			desc = "selSerMachine";
			
		}else if("02".equals(businessFrom)){
			//�ֻ�APP 
			desc = "mobileAPP";
			
		}else if("03".equals(businessFrom)){
			//��վ
			desc = "website";
			
		}else if("04".equals(businessFrom)){
			//֧��������
			desc = "aliSerWindow";
			
		}else if("05".equals(businessFrom)){
			//���� 
			desc = "window";
			
		}
		return desc;
	}
	
	public String getBussiness(String businessFrom){
		String desc = "";
		if("selSerMachine".equals(businessFrom)){
			//������ 
			desc = "01";
			
		}else if("mobileAPP".equals(businessFrom)){
			//�ֻ�APP 
			desc = "02";
			
		}else if("website".equals(businessFrom)){
			//��վ
			desc = "03";
			
		}else if("aliSerWindow".equals(businessFrom)){
			//֧��������
			desc = "04";
			
		}else if("window".equals(businessFrom)){
			//���� 
			desc = "05";
			
		}
		return desc;
	}
	
	/**
	 * ԤԼ��ʽȡ��HIS�ж�Ӧ�Ĳ���
	 * @param code
	 * @return
	 */
	public String getHisAppointType(String code){
		String desc = code;
		if("01".equals(code)){
			//������ 
			desc = "M";
			
		}else if("02".equals(code)){
			//�ֻ�APP 
			desc = "S";
			
		}else if("03".equals(code)){
			//��վ
			desc = "C";
			
		}else if("04".equals(code)){
			//֧��������
			desc = "P";
			
		}else if("05".equals(code)){
			//���� 
			desc = "F";
			
		}
		return desc;
	}
	
	public String getAppointType(String code){
		String desc = code;
		if("M".equals(code)){
			//������ 
			desc = "01";
			
		}else if("S".equals(code)){
			//�ֻ�APP 
			desc = "02";
			
		}else if("C".equals(code)){
			//��վ
			desc = "03";
			
		}else if("P".equals(code)){
			//֧��������
			desc = "04";
			
		}else if("F".equals(code)){
			//���� 
			desc = "05";
			
		}
		return desc;
	}
	
	public String getRegPatadmList(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><StartTime>2016-03-03 00:00:00</StartTime>" +
//		"<EndTime>2016-03-03 23:59:59</EndTime></BodyParams></Request>";
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		String sDate = bodyP.getStartTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		String eDate = bodyP.getEndTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");

		
		TParm regParm = RegQETool.getInstance().getRegPatadmList(sDate, eDate);
		xStream = new XStream();
		if(regParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
			
		}
		if(regParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		for (int i = 0; i < regParm.getCount(); i++) {
			
			//�ж�ҽ������(��ְ ��ͨ����ְ���� ���Ǿ�����)
			String insType = "";
			String inscrowdType =regParm.getValue("INS_CROWD_TYPE", i);
			String inspayType =regParm.getValue("INS_PAT_TYPE", i);
			String diseaseCode =regParm.getValue("DISEASE_CODE", i);
			if(inscrowdType.equals("1")&&inspayType.equals("1"))
				insType = "1";
			if(inscrowdType.equals("1")&&inspayType.equals("2"))
				insType = "2";
			if(inscrowdType.equals("2")&&inspayType.equals("2"))
				insType = "3";
			
			double insurepay = 0;
			double cashpay = regParm.getDouble("AR_AMT", i);
			double accountPay = 0;
			
			if(insType.equals("1")||insType.equals("2")){
				insurepay = regParm.getDouble("INS_TOT_AMT", i) - // �ܽ��
				regParm.getDouble("UNACCOUNT_PAY_AMT", i) - // ���˻�֧��
				regParm.getDouble("UNREIM_AMT", i);// ����δ����
				cashpay =regParm.getDouble("UNACCOUNT_PAY_AMT", i)+         
				regParm.getDouble("UNREIM_AMT", i);// �ֽ�֧�����
		        accountPay = regParm.getDouble("ACCOUNT_PAY_AMT", i);	
			}
	        if(insType.equals("3")){
	        	if (null != regParm.getValue("REIM_TYPE", i)
						&& regParm.getInt("REIM_TYPE", i) == 1) {
	        		insurepay = regParm.getDouble("TOTAL_AGENT_AMT", i)
							+ regParm.getDouble("ARMY_AI_AMT", i)
							+ regParm.getDouble("FLG_AGENT_AMT", i)
							+ regParm.getDouble("ILLNESS_SUBSIDY_AMT", i)//����󲡽��
							- regParm.getDouble("UNREIM_AMT", i);
				} else {
					insurepay = regParm.getDouble("TOTAL_AGENT_AMT", i)
							+ regParm.getDouble("FLG_AGENT_AMT", i)
							+ regParm.getDouble("ARMY_AI_AMT", i)
					        + regParm.getDouble("ILLNESS_SUBSIDY_AMT", i);//����󲡽��
				}
				// ����ʵ��֧��
	        	cashpay = regParm.getDouble("INS_TOT_AMT", i)
						- regParm.getDouble("TOTAL_AGENT_AMT", i)
						- regParm.getDouble("FLG_AGENT_AMT", i)
						- regParm.getDouble("ARMY_AI_AMT", i)
						- regParm.getDouble("ILLNESS_SUBSIDY_AMT", i)//����󲡽��
						+ regParm.getDouble("UNREIM_AMT", i);	
			}	
			
			result = new Result();
			result.setPatientID(regParm.getValue("MR_NO", i));
			result.setMedCardNo(regParm.getValue("INSCARD_NO", i));
			result.setIDCardNo(regParm.getValue("IDNO", i));
			//??�������ͣ�ҽ����������ȡ
			if(insType.length() == 0){
				result.setInsureTypeCode("0"); //ҽ������
				result.setMtTypeCode("0"); //��������
			}else if("1".equals(insType)){
				result.setInsureTypeCode("1"); //ҽ������
				result.setMtTypeCode("0"); //��������
			}else if("2".equals(insType)){
				if(diseaseCode.length() > 0){
					result.setInsureTypeCode("2"); //ҽ������
					result.setMtTypeCode(diseaseCode); //��������
				}else{
					result.setInsureTypeCode("1"); //ҽ������
					result.setMtTypeCode("0"); //��������
				}

			}else if("3".equals(insType)){
				if(diseaseCode.length() > 0){
					result.setInsureTypeCode("2"); //ҽ������
					result.setMtTypeCode(diseaseCode); //��������
				}else{
					result.setInsureTypeCode("1"); //ҽ������
					result.setMtTypeCode("0"); //��������
				}

			}
			
			
			result.setRegFee(regParm.getValue("REG_FEE_REAL", i));
			result.setInspectFee(regParm.getValue("CLINIC_FEE_REAL", i));
			result.setTotalFee(regParm.getValue("AR_AMT", i));
			result.setDeptCode(regParm.getValue("REALDEPT_CODE", i));
			result.setIsEmergency("0");
			result.setDoctorCode(regParm.getValue("REALDR_CODE", i));
			result.setVisitTypeCode(regParm.getValue("CLINICTYPE_CODE", i));
			result.setVisitCode(regParm.getValue("ID", i));
			result.setAppointCode(regParm.getValue("CASE_NO", i));
			result.setPatientName(regParm.getValue("PAT_NAME", i));
			result.setPatientAge(regParm.getValue("AGE", i)); //����
			result.setPatientSex(regParm.getValue("SEX_CODE", i));
			String bussinessF = getBussiness(regParm.getValue("OPT_USER", i));
			if(bussinessF.length() == 0){
				if(regParm.getValue("REGMETHOD_CODE", i).equals("T")){
					result.setBusinessFrom("06");
				}else{
					result.setBusinessFrom("05");
				}
				
			}else{
				result.setBusinessFrom(bussinessF);
			}
			
			result.setInsurePay(df.format(insurepay-accountPay));//ҽ��֧��
			result.setAccountPay(df.format(accountPay));//�����˻�֧��
			result.setCashPay(df.format(cashpay));//����ʵ��֧��
			result.setInsureSequenceNo(regParm.getValue("CONFIRM_NO", i));
			result.setRegID(regParm.getValue("REGID", i));
			result.setRegTime(regParm.getValue("REG_DATE", i));
			result.setVisitAddress("");
			result.setQueueNo(regParm.getValue("QUE_NO", i));
			result.setDeptName(regParm.getValue("DEPT_DESC", i));
			result.setDoctorName(regParm.getValue("DR_DESC", i));
			result.setRegDate(regParm.getValue("ADM_DATE", i));
			if("01".equals(regParm.getValue("SESSION_CODE", i))){
				result.setAPW("A");
			}
			if("02".equals(regParm.getValue("SESSION_CODE", i))){
				result.setAPW("P");
			}
			
			
			results.add(result);
	
		}
		
		res.setResults(results);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		
		return outXml;
	}
	
	public String getRxNoNotBillList(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><RegID>170315000004</RegID>" +
//		"</BodyParams></Request>";
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		xStream = new XStream();
		TParm billParm = RegQETool.getInstance().getRxNoNotBillList(bodyP.getRegID());
		if(billParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		if(billParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		List rxNoList = new ArrayList();
		TParm mParm = new TParm();
		TParm sParm = new TParm();
		
		for (int i = 0; i < billParm.getCount(); i++) {
			if(!rxNoList.contains(billParm.getValue("RX_NO", i))){
				rxNoList.add(billParm.getValue("RX_NO", i));
			}
			
			String ordersetCode = billParm.getValue("ORDERSET_CODE", i);
			String setmainFlg = billParm.getValue("SETMAIN_FLG", i);
			if((ordersetCode != null || ordersetCode.length() > 0) && setmainFlg.equals("N")){
				sParm.addRowData(billParm, i);
			}
			
			if(ordersetCode == null || ordersetCode.length() == 0){
				mParm.addRowData(billParm, i);
			}
			
			if((ordersetCode != null || ordersetCode.length() > 0) && setmainFlg.equals("Y")){
				mParm.addRowData(billParm, i);
			}
			
		}
		//������ҽ��ϸ���Ǯ�������ŵ�������
		for (int i = 0; i < mParm.getCount("RX_NO"); i++) {
			if("Y".equals(mParm.getValue("SETMAIN_FLG", i))){
				double arAmt = 0;
				double ownPrice = 0;
				for (int j = 0; j < sParm.getCount("RX_NO"); j++) {
					if((sParm.getValue("ORDERSET_CODE", j) != null || sParm.getValue("ORDERSET_CODE", j).length() > 0) 
							&& "N".equals(sParm.getValue("SETMAIN_FLG", j))
							&& mParm.getValue("RX_NO", i).equals(sParm.getValue("RX_NO", j))
							&& mParm.getValue("ORDERSET_CODE", i).equals(sParm.getValue("ORDERSET_CODE", j))
							&& mParm.getValue("ORDERSET_GROUP_NO", i).equals(sParm.getValue("ORDERSET_GROUP_NO", j))
							){
						arAmt += sParm.getDouble("AR_AMT", j);
						ownPrice += sParm.getDouble("OWN_PRICE", j);
					}
				}
				
				mParm.setData("AR_AMT", i, arAmt);
				mParm.setData("OWN_PRICE", i, ownPrice);
				
			}
			
		}
		//����ÿ����������Ǯ��
		for (int i = 0; i < rxNoList.size(); i++) {
			double rxAmt =0;
			for (int j = 0; j < mParm.getCount("RX_NO"); j++) {
				if(rxNoList.get(i).equals(mParm.getValue("RX_NO", j))){
					rxAmt += mParm.getDouble("AR_AMT", j);
				}
				
			}
			for (int j = 0; j < mParm.getCount("RX_NO"); j++) {
				if(rxNoList.get(i).equals(mParm.getValue("RX_NO", j))){
					mParm.setData("RX_AMT", j, rxAmt);
				}
				
			}
			
		}
		
		//���ز���
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		for (int i = 0; i < mParm.getCount("RX_NO"); i++) {
			result = new Result();
			result.setPrescriptionCode(mParm.getValue("RX_NO", i));
			result.setPrescriptionType(getRxType(mParm.getValue("RX_TYPE", i)));
			
			if("PHA".equals(mParm.getValue("CAT1_TYPE", i))){
				result.setDrugRoomName(mParm.getValue("DEPT_CHN_DESC", i));
				result.setExecuteDeptName("");
			}else{
				result.setDrugRoomName("");
				result.setExecuteDeptName(mParm.getValue("DEPT_CHN_DESC", i));
			}
			
			result.setFeeName(mParm.getValue("CHN_DESC", i));			
			result.setTotalAmount(df.format(mParm.getDouble("RX_AMT", i)));
			result.setFeeDate(mParm.getValue("ORDER_DATE", i));
			result.setItemCode(mParm.getValue("ORDER_CODE", i));
			result.setItemName(mParm.getValue("ORDER_DESC",i));
			result.setItemSpecs(mParm.getValue("SPECIFICATION", i));
			result.setDoseNoce(mParm.getValue("MEDI_QTY", i));
			result.setDoseUnit(mParm.getValue("UNIT_CHN_DESC",i));
			result.setNumber(mParm.getValue("DOSAGE_QTY", i));
			result.setPrePrice(df.format(mParm.getDouble("OWN_PRICE", i)));
			result.setPriceUnit(mParm.getValue("PRICE_UNIT", i));
			result.setCost(df.format(mParm.getDouble("AR_AMT", i)));
			
			result.setComment("");
			
			results.add(result);			
		}
		
		res.setResults(results);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		
		return outXml;
		
	}
	
	public String getRxType(String rxType){
		String desc = "";
		if("1".equals(rxType)){
			desc = "����ҩ";
		}else if("2".equals(rxType)){
			desc = "����ҩƷ";
		}else if("3".equals(rxType)){
			desc = "��ҩ��Ƭ";
		}else if("4".equals(rxType)){
			desc = "������Ŀ";
		}else if("5".equals(rxType)){
			desc = "������";
		}
//		else if("7".equals(rxType)){
//			desc = "����Ƽ�";
//		}
		return desc;
	}
	
	public String getBilRegRecpList(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><ReceiptCode>160216000286</ReceiptCode>" +
//		"</BodyParams></Request>";
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		
		TParm bilParm = RegQETool.getInstance().getBilRegRecpList(bodyP.getReceiptCode());
		xStream = new XStream();
		if(bilParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		if(bilParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		//���ز���
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");

		Result result = new Result();
		
		for (int i = 0; i < bilParm.getCount(); i++) {

			String insTypeDesc=bilParm.getValue("INS_TYPE", i);
			
			double insurepay = bilParm.getDouble("PAY_INS_CARD", i)-bilParm.getDouble("ACCOUNT_PAY", i);
			double cashpay = bilParm.getDouble("AR_AMT", i)-bilParm.getDouble("PAY_INS_CARD", i);
			double accountPay = bilParm.getDouble("ACCOUNT_PAY", i);
			
			
	        result = new Result();
	        result.setHISRegID(bilParm.getValue("CASE_NO", i));
	        result.setPatientName(bilParm.getValue("PAT_NAME", i));
	        result.setExecuteDeptName("");
	        result.setTotalMoney(bilParm.getValue("AR_AMT", i));
	        result.setInsurePay(""+insurepay);//ҽ��֧��
			result.setAccountPay(""+accountPay);//�����˻�֧��
			result.setCashPay(""+cashpay);//����ʵ��֧��
			result.setOpTime(bilParm.getValue("BILL_DATE", i));
			result.setOpName(getBussiness(bilParm.getValue("OPT_USER", i)));
			result.setReceiptCode(bilParm.getValue("RECEIPT_NO", i));
			result.setTipMessages("");
			Map map = new HashMap();
			map.put("CureCardBalance",""+RegQETool.getInstance().getEktAmt(bilParm.getValue("MR_NO", i)));
			map.put("insType", insTypeDesc);
			result.setComment(JSONSerializer.toJSON(map).toString());

		}
		
		res.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);
		outXml = xStream.toXML(res);
		
		return outXml;
		
		
	}
	
	public String getBilOpbRecpDetail(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><ReceiptCode>170725000003</ReceiptCode>" +
//		"</BodyParams></Request>";
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		
		TParm detailParm = RegQETool.getInstance().getBilOpbRecpDetail(bodyP.getReceiptCode());
		xStream = new XStream();
		if(detailParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		if(detailParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		
		TParm mParm = new TParm();
		TParm sParm = new TParm();
		
		for (int i = 0; i < detailParm.getCount(); i++) {
			
			String ordersetCode = detailParm.getValue("ORDERSET_CODE", i);
			String setmainFlg = detailParm.getValue("SETMAIN_FLG", i);
			if((ordersetCode != null || ordersetCode.length() > 0) && setmainFlg.equals("N")){
				sParm.addRowData(detailParm, i);
			}
			
			if(ordersetCode == null || ordersetCode.length() == 0){
				mParm.addRowData(detailParm, i);
			}
			
			if((ordersetCode != null || ordersetCode.length() > 0) && setmainFlg.equals("Y")){
				mParm.addRowData(detailParm, i);
			}
			
		}
		//������ҽ��ϸ���Ǯ�������ŵ�������
		for (int i = 0; i < mParm.getCount("RX_NO"); i++) {
			if("Y".equals(mParm.getValue("SETMAIN_FLG", i))){
				double arAmt = 0;
				
				for (int j = 0; j < sParm.getCount("RX_NO"); j++) {
					if((sParm.getValue("ORDERSET_CODE", j) != null || sParm.getValue("ORDERSET_CODE", j).length() > 0) 
							&& "N".equals(sParm.getValue("SETMAIN_FLG", j))
							&& mParm.getValue("RX_NO", i).equals(sParm.getValue("RX_NO", j))
							&& mParm.getValue("ORDERSET_CODE", i).equals(sParm.getValue("ORDERSET_CODE", j))
							&& mParm.getValue("ORDERSET_GROUP_NO", i).equals(sParm.getValue("ORDERSET_GROUP_NO", j))
							){
						arAmt += sParm.getDouble("AR_AMT", j);

					}
				}
				
				mParm.setData("AR_AMT", i, arAmt);
				
			}
			
		}
		
		//���ز���
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		for (int i = 0; i < mParm.getCount("RX_NO"); i++) {
			result = new Result();
			result.setItemName(mParm.getValue("ORDER_DESC", i));
			result.setItemSpecs(mParm.getValue("SPECIFICATION", i));
			result.setNumber(mParm.getValue("DOSAGE_QTY", i));
			result.setCost(mParm.getValue("AR_AMT", i));
			
			results.add(result);
			
		}
		
		res.setResults(results);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		
		return outXml;
		
		
	}
	
	public String getRxNoPhaStatus(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><PrescriptionCode>17031500016</PrescriptionCode>" +
//		"</BodyParams></Request>";
//		writerLog2(inXml);
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		String rxNo = bodyP.getPrescriptionCode();
		TParm phaCountParm = RegQETool.getInstance().getRxNoPhaCount(rxNo); 
		xStream = new XStream();
		if(phaCountParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(phaCountParm.getCount() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:�ô���û��ҩƷ����");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		int count = 0;
		for (int i = 0; i < phaCountParm.getCount(); i++) {
			if(phaCountParm.getValue("PHA_DISPENSE_CODE", i).length() > 0
					&& phaCountParm.getValue("PHA_DISPENSE_CODE", i) != null){
				count++;
			}
		}
		
		res.setIsSuccessed("0");
		res.setErrorMsg("��ѯ�ɹ�");
		Result result = new Result();
		
		if(count == 0){
			result.setIsSend("0"); //δ��
		}
		if(count == phaCountParm.getCount()){
			result.setIsSend("1"); //�ѷ�
		}
		res.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		outXml = xStream.toXML(res);
		
		return outXml;
		
		
	}
	
	public String getRxNoBillList(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><StartTime>2017-03-15 11:21:20</StartTime>" +
//		"<EndTime>2017-03-15 15:29:51</EndTime></BodyParams></Request>";
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		String sDate = bodyP.getStartTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		String eDate = bodyP.getEndTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		
		TParm billParm = RegQETool.getInstance().getRxNoBillList(sDate, eDate);
		
		xStream = new XStream();
		if(billParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(billParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		res.setIsSuccessed("0");
		res.setErrorMsg("��ѯ�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result(); 
		for (int i = 0; i < billParm.getCount(); i++) {
			result = new Result(); 
			result.setRegID(billParm.getValue("CASE_NO", i));
			result.setPrescriptionCode(billParm.getValue("RX_NO", i));
			result.setPrescriptionType(billParm.getValue("RX_TYPE", i));
			result.setPrescriptionName(getRxType(billParm.getValue("RX_TYPE", i)));
			result.setTotalAmount(billParm.getValue("AR_AMT", i));
			result.setDrugMaker("");
			result.setDrugSender("");
			result.setOrderNum((i+1)+"");
			TParm diagesParm = RegQETool.getInstance().getOpdDiage(billParm.getValue("CASE_NO", i));
			String diages = "";
			for (int j = 0; j < diagesParm.getCount(); j++) {
				diages = diagesParm.getValue("ICD_CHN_DESC", j)+";";
			}
			if(diages.length() > 0){
				diages = diages.substring(0, diages.length()-1);
				result.setDiagnosis(diages);
//				result.setInputTime(diagesParm.getValue("ORDER_DATE", 0));
			}else{
				result.setDiagnosis(diages);
//				result.setInputTime("");
			}
			
			result.setInputTime(RegQETool.getInstance().getOpdOrderBillDate(billParm.getValue("CASE_NO", i), billParm.getValue("RX_NO", i)));
			
			results.add(result);
			
		}
		res.setResults(results);
		
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		
		return outXml;

	}
	
	public String getRxNoBillDetail(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><PrescriptionCode>17031300089</PrescriptionCode>" +
//		"</BodyParams></Request>";
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		
		String rxNo = bodyP.getPrescriptionCode();
		TParm detailParm = RegQETool.getInstance().getRxNoBillDetail(rxNo);
		
		xStream = new XStream();
		if(detailParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(detailParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		TParm mParm = new TParm();
		TParm sParm = new TParm();
		
		for (int i = 0; i < detailParm.getCount(); i++) {
			
			String ordersetCode = detailParm.getValue("ORDERSET_CODE", i);
			String setmainFlg = detailParm.getValue("SETMAIN_FLG", i);
			if((ordersetCode != null || ordersetCode.length() > 0) && setmainFlg.equals("N")){
				sParm.addRowData(detailParm, i);
			}
			
			if(ordersetCode == null || ordersetCode.length() == 0){
				mParm.addRowData(detailParm, i);
			}
			
			if((ordersetCode != null || ordersetCode.length() > 0) && setmainFlg.equals("Y")){
				mParm.addRowData(detailParm, i);
			}
			
		}
		//������ҽ��ϸ���Ǯ�������ŵ�������
		for (int i = 0; i < mParm.getCount("RX_NO"); i++) {
			if("Y".equals(mParm.getValue("SETMAIN_FLG", i))){
				double arAmt = 0;
				double onwPrice = 0;
				
				for (int j = 0; j < sParm.getCount("RX_NO"); j++) {
					if((sParm.getValue("ORDERSET_CODE", j) != null || sParm.getValue("ORDERSET_CODE", j).length() > 0) 
							&& "N".equals(sParm.getValue("SETMAIN_FLG", j))
							&& mParm.getValue("RX_NO", i).equals(sParm.getValue("RX_NO", j))
							&& mParm.getValue("ORDERSET_CODE", i).equals(sParm.getValue("ORDERSET_CODE", j))
							&& mParm.getValue("ORDERSET_GROUP_NO", i).equals(sParm.getValue("ORDERSET_GROUP_NO", j))
							){
						arAmt += sParm.getDouble("AR_AMT", j);
						onwPrice += sParm.getDouble("OWN_PRICE", j);

					}
				}
				
				mParm.setData("AR_AMT", i, arAmt);
				mParm.setData("OWN_PRICE", i, onwPrice);
				
			}
			
		}
		
		
		//���ز���
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		for (int i = 0; i < mParm.getCount("RX_NO"); i++) {
			result = new Result();
			result.setItemCode(mParm.getValue("ORDER_CODE", i));
			result.setItemName(mParm.getValue("ORDER_DESC", i));
			result.setItemSpecs(mParm.getValue("SPECIFICATION", i));
			result.setUseDays(mParm.getValue("TAKE_DAYS", i));
			result.setDailyTimes(mParm.getValue("FREQ_CHN_DESC", i));
			result.setDoseNoce(mParm.getValue("MEDI_QTY", i));
			result.setDoseUnit(mParm.getValue("MEDI_UNIT", i));
			result.setNumber(mParm.getValue("DOSAGE_QTY",i));
			result.setNumberUnit(mParm.getValue("DOSAGE_UNIT",i));
			result.setPrePrice(mParm.getValue("OWN_PRICE",i));
			result.setPriceUnit(mParm.getValue("DOSAGE_UNIT",i));
			result.setCost(mParm.getValue("AR_AMT",i));
			result.setUsage(mParm.getValue("ROUTE_CHN_DESC",i));
			result.setOrderNum(mParm.getValue("SEQ_NO",i));
			
			results.add(result);
			
		}
		
		res.setResults(results);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		
		return outXml;

	}
	
	public String getLisReportList(String inXml){
		writerLog2("getLisReportList----����--"+SystemTool.getInstance().getDate());
		
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><StartTime>2016-02-02 00:00:00</StartTime>" +
//		"<EndTime>2016-02-02 09:10:59</EndTime></BodyParams></Request>";
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		String sDate = bodyP.getStartTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		String eDate = bodyP.getEndTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		
		TParm lisParm = RegQETool.getInstance().getLisReportList(sDate, eDate);
		writerLog2("getLisReportList----lisParm--"+SystemTool.getInstance().getDate());

		xStream = new XStream();
		if(lisParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(lisParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		//���ز���
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		
		for (int i = 0; i < lisParm.getCount(); i++) {
			result = new Result();
			result.setRegID(lisParm.getValue("CASE_NO", i));
			result.setInspection_id(lisParm.getValue("APPLICATION_NO", i));
			result.setTest_order_code(lisParm.getValue("ORDER_CODE", i));
			result.setTest_order_name(lisParm.getValue("ORDER_DESC", i));
			result.setPatient_name(lisParm.getValue("PAT_NAME", i));
			result.setSample_class_name(lisParm.getValue("RPTTYPE_DESC", i));
			result.setSample_number(lisParm.getValue("RPTTYPE_CODE", i));
			result.setPatient_sex(lisParm.getValue("SEX_CODE", i));
			result.setPatient_dept_name(lisParm.getValue("DEPT_DESC", i));
			result.setSampling_time(lisParm.getValue("PRINT_DATE", i));
			result.setRequisition_person(lisParm.getValue("DR_DESC", i));
			result.setAge_input(lisParm.getValue("AGE", i));
			result.setClinical_diagnoses_name(lisParm.getValue("ICD_CHN_DESC", i));
			result.setIncept_time(lisParm.getValue("REPORT_DATE", i));
			result.setCheck_time(lisParm.getValue("EXAMINE_DATE", i));
			result.setInspection_person(lisParm.getValue("REPORT_DR", i));
			result.setCheck_person(lisParm.getValue("EXAMINE_DR", i));
			
			//��ϸ
			List<Detail> details = new ArrayList<Detail>();
			
			String applicationNo = lisParm.getValue("APPLICATION_NO", i);
			TParm lisParmD = RegQETool.getInstance().getLisReportDetail(applicationNo);
			Detail detail = new Detail();
			for (int j = 0; j < lisParmD.getCount(); j++) {
				detail = new Detail();
				detail.setXh(lisParmD.getValue("RPDTL_SEQ", j));
				detail.setChinese_name(lisParmD.getValue("TESTITEM_CHN_DESC", j));
				detail.setQuantitative_result(lisParmD.getValue("TEST_VALUE", j));
				detail.setTest_item_reference(lisParmD.getValue("LIMIT", j));
				detail.setTest_item_unit(lisParmD.getValue("TEST_UNIT", j));
				
				details.add(detail);
				
			}
			result.setDetails(details);
			
			results.add(result);
			
		}

		res.setResults(results);
		xStream = new XStream(new XppDriver(new XmlFriendlyReplacer("_-", "_")));
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.alias("Detail", Detail.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		xStream.aliasField("Details", List.class, "Details");

		outXml = xStream.toXML(res);
		writerLog2("getLisReportList----��ȥ--"+SystemTool.getInstance().getDate());
		return outXml;
		
	}
	
	public String getLisReportDetail(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><Inspection_id>160202500068</Inspection_id>" +
//		"</BodyParams></Request>";
		
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream(new XppDriver(new XmlFriendlyReplacer("_-", "_")));
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		
		String applicationNo = bodyP.getInspection_id();
		TParm lisParm = RegQETool.getInstance().getLisReportDetail(applicationNo);
		
		xStream = new XStream();
		if(lisParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(lisParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		//���ز���
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		for (int i = 0; i < lisParm.getCount(); i++) {
			result = new Result();
			result.setXh(lisParm.getValue("RPDTL_SEQ", i));
			result.setChinese_name(lisParm.getValue("TESTITEM_CHN_DESC", i));
			result.setQuantitative_result(lisParm.getValue("TEST_VALUE", i));
			result.setTest_item_reference(lisParm.getValue("LIMIT", i));
			result.setTest_item_unit(lisParm.getValue("TEST_UNIT", i));
			
			results.add(result);
			
		}
		
		
		res.setResults(results);
		xStream = new XStream(new XppDriver(new XmlFriendlyReplacer("_-", "_")));
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		
		return outXml;
		
	}
	
	public String getRisReportList(String inXml){
		writerLog2("getRisReportList--date--����-"+SystemTool.getInstance().getDate());
		
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><StartTime>2016-02-02 00:00:00</StartTime>" +
//		"<EndTime>2016-02-02 09:15:59</EndTime></BodyParams></Request>";
		
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		String sDate = bodyP.getStartTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		String eDate = bodyP.getEndTime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		
		TParm risParm = RegQETool.getInstance().getRisReportList(sDate, eDate);
		writerLog2("getRisReportList--risParm--����-"+SystemTool.getInstance().getDate());
		xStream = new XStream();
		if(risParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(risParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		//���ز���
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		
		for (int i = 0; i < risParm.getCount(); i++) {
			result = new Result();
			result.setRegID(risParm.getValue("CASE_NO", i));
			result.setPacsID(risParm.getValue("APPLICATION_NO", i));
			result.setPacsNam(risParm.getValue("ORDER_DESC", i));
			result.setPacsCode(risParm.getValue("ORDER_CODE", i));			
			
			//��ϸ
			List<Detail> details = new ArrayList<Detail>();
			
			String applicationNo = risParm.getValue("APPLICATION_NO", i);
			TParm risParmD = RegQETool.getInstance().getRisReportDetail(applicationNo);
			Detail detail = new Detail();
			for (int j = 0; j < risParmD.getCount(); j++) {
				detail = new Detail();
				detail.setPatientName(risParmD.getValue("PAT_NAME", j));
				detail.setPatientSex(risParmD.getValue("SEX_DESC", j));
				detail.setAppDept(risParmD.getValue("DEPT_DESC", j));
				detail.setAppDoctor(risParmD.getValue("DR_DESC", j));
				detail.setAge(risParmD.getValue("AGE", j));
				detail.setItemName(risParmD.getValue("ORDER_DESC", j));
				detail.setYinYang(risParmD.getValue("OUTCOME_TYPE", j));
				detail.setPatientId(risParmD.getValue("MR_NO", j));
				detail.setSource("����");
				detail.setR_date(risParmD.getValue("REGISTER_DATE", j));
				detail.setR_time(risParmD.getValue("REGISTER_TIME", j));
				detail.setReportDate(risParmD.getValue("REPORT_DATE", j));
				detail.setReportTime(risParmD.getValue("REPORT_TIME", j));
				detail.setReportDoctor(risParmD.getValue("REPORT_DR", j));
				detail.setCheckDate(risParmD.getValue("EXAMINE_DATE", j));
				detail.setCheckTime(risParmD.getValue("EXAMINE_TIME", j));
				detail.setCheckDoctor(risParmD.getValue("EXAMINE_DR", j));
				detail.setDiagnosis(risParmD.getValue("ICD_CHN_DESC", j));
				detail.setSymptom("");
				detail.setConclusion(risParmD.getValue("OUTCOME_CONCLUSION", j));
				detail.setImpression(risParmD.getValue("OUTCOME_DESCRIBE", j));
				detail.setBodyPart(risParmD.getValue("OPTITEM_CHN_DESC", j));
				
				details.add(detail);
				
			}
			result.setDetails(details);
			
			
			results.add(result);
			
		}
		
		res.setResults(results);
		xStream = new XStream(new XppDriver(new XmlFriendlyReplacer("_-", "_")));
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.alias("Detail", Detail.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		xStream.aliasField("Details", List.class, "Details");
		outXml = xStream.toXML(res);
		writerLog2("getRisReportList----��ȥ--"+SystemTool.getInstance().getDate());
		return outXml;
		
	}
	
	public String getRisReportDetail(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><PacsID>160202500078</PacsID>" +
//		"</BodyParams></Request>";
		String outXml= "";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		
		String applicationNo = bodyP.getPacsID();
		
		TParm risParm = RegQETool.getInstance().getRisReportDetail(applicationNo);
		xStream = new XStream();
		if(risParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(risParm.getCount() < 0){
			res.setIsSuccessed("0");
			res.setErrorMsg("");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		//���ز���
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		
		for (int i = 0; i < risParm.getCount(); i++) {
			result = new Result();
			result.setPatientName(risParm.getValue("PAT_NAME", i));
			result.setPatientSex(risParm.getValue("SEX_DESC", i));
			result.setAppDept(risParm.getValue("DEPT_DESC", i));
			result.setAppDoctor(risParm.getValue("DR_DESC", i));
			result.setAge(risParm.getValue("AGE", i));
			result.setItemName(risParm.getValue("ORDER_DESC", i));
			result.setYinYang(risParm.getValue("OUTCOME_TYPE", i));
			result.setPatientId(risParm.getValue("MR_NO", i));
			result.setSource("����");
			result.setR_date(risParm.getValue("REGISTER_DATE", i));
			result.setR_time(risParm.getValue("REGISTER_TIME", i));
			result.setReportDate(risParm.getValue("REPORT_DATE", i));
			result.setReportTime(risParm.getValue("REPORT_TIME", i));
			result.setReportDoctor(risParm.getValue("REPORT_DR", i));
			result.setCheckDate(risParm.getValue("EXAMINE_DATE", i));
			result.setCheckTime(risParm.getValue("EXAMINE_TIME", i));
			result.setCheckDoctor(risParm.getValue("EXAMINE_DR", i));
			result.setDiagnosis(risParm.getValue("ICD_CHN_DESC", i));
			result.setSymptom("");
			result.setConclusion(risParm.getValue("OUTCOME_CONCLUSION", i));
			result.setImpression(risParm.getValue("OUTCOME_DESCRIBE", i));
			result.setBodyPart(risParm.getValue("OPTITEM_CHN_DESC", i));
			
			results.add(result);
			
		}
		
		res.setResults(results);
		xStream = new XStream(new XppDriver(new XmlFriendlyReplacer("_-", "_")));
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		
		return outXml;
		
	}
	
	

	/**
	 * �ɷѷָ�
	 * @return
	 */
	public String onInsSplitOpb(String inXml){
//		inXml="<?xml version=\"1.0\"?><Request><HeaderParams />  <BodyParams>" +
//				"   <PatientID>000000558982</PatientID>    <TerminalCode>ZZJ002</TerminalCode>" +
//				"    <MedCardNo>    </MedCardNo>    <MedCardPwd>111111</MedCardPwd>" +
//				"    <RegID>170619000001</RegID>    <TotalAmount>    </TotalAmount>" +
//				"	<PrescriptionCode>17061900003</PrescriptionCode>" +
//				"    <PrescriptionType>    </PrescriptionType>" +
//				"    <BusinessFrom>01</BusinessFrom>  </BodyParams></Request>";
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		String outXml = "";
		Result result = new Result();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		writerLog2("MedCardNo=========="+bodyP.getMedCardNo());
//		String caseNo = "";
//		String [] Array=new String [0];
//		String regID = bodyP.getRegID();//�Һ�ID		
//		if(regID.length()>0){
//		   Array=regID.split("\\|");			
//	       caseNo = Array[0];//�Һ�HISΨһ��	
//		}
		String caseNo = bodyP.getRegID().trim();// �Һ�ID
		String confirmNo = "";
		writerLog2("caseNo==========" + caseNo);

		String prescriptioncode = bodyP.getPrescriptionCode().trim();// ������
		writerLog2("prescriptioncode==========" + prescriptioncode);
		String[] Array = new String[0];
		if (prescriptioncode.length() > 0) {
			Array = prescriptioncode.split("\\|");
		}
		int count = Array.length;
		writerLog2("count==========" + count);
		StringBuffer allrxNo = new StringBuffer();
		for (int i = 0; i < count; i++) {
			String rxno = "";
			rxno = Array[i];
			if (allrxNo.length() > 0)
				allrxNo.append(",");
			allrxNo.append(rxno);
		}
		String rxNo = allrxNo.toString();
		writerLog2("rxNo==========" + rxNo);

	    // ��ȡҽ������
	    TParm insmzconfirmData = RegQETool.getInstance().queryInsmzconfirm(caseNo);
	    writerLog2("insmzconfirmData=========="+insmzconfirmData);
	    if (insmzconfirmData.getCount()>0){
	    	TParm parm = new TParm();
			TParm insParm = new TParm(); // ��ҽ�������ز���
			TParm resultData = new TParm();
			if(insmzconfirmData.getValue("DISEASE_CODE",0).length()==0)
			   parm.setData("DISEASE_CODE", "0");
			else
		       parm.setData("DISEASE_CODE", insmzconfirmData.getValue("DISEASE_CODE",0));//��������(4.����)			    
			    
		//��ҽ����
		if (bodyP.getMedCardNo() == null
			|| bodyP.getMedCardNo().toString().length() <= 0) {
				res.setIsSuccessed("1");
				res.setErrorMsg("ҽ������Ϊ��,�����»��ҽ����");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}			
		TParm regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));
		parm.setData("TEXT", ";"+(String)bodyP.getMedCardNo()+"=");
		parm.setData("OPT_USER", "QeApp");
		parm.setData("OPT_TERM", "0.0.0.0");
		parm.setData("PASSWORD", bodyP.getMedCardPwd());//����
		String mrNo = bodyP.getPatientID();
		parm.setData("MR_NO", mrNo);//������
		// ��÷��÷���ʱ��			
		String admDate ="";
		TParm date = RegQETool.getInstance().getAdmDate(caseNo);			
		writerLog2("date=========="+date);
		admDate= StringTool.getString(date.getTimestamp("ADM_DATE",0),"yyyyMMdd");
		writerLog2("admDate=========="+admDate);
		parm.setData("ADVANCE_CODE",regionParm.getValue("NHI_NO", 0)+"@"+
				admDate+"@"+"1");//ҽԺ����@���÷���ʱ��@���
		 writerLog2("parm=========="+parm);
		insParm = RegQETool.getInstance().readCard(parm);
		// ҽ��ȷ������
		if (null == insParm || insParm.getErrCode() < 0
				|| null == insParm.getValue("CARD_NO")
				|| insParm.getValue("CARD_NO").length() <= 0) {
	    	  writerLog2("ҽ��========"); 
	    	  res.setIsSuccessed("1");
			  res.setErrorMsg("ҽ������ȡʧ��");
			  xStream.alias("Response", RegResponse.class);
			  outXml = xStream.toXML(res);
			  return outXml;
		}
		confirmNo = (String) insParm.getData("CONFIRM_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		
		 //��ȡҽ���ɷ�ҽ������	
		TParm selectParm = RegQETool.getInstance().getInsOrder(caseNo,rxNo);
		writerLog2("selectParm=========="+selectParm);
		// �ָ���ǰ����
		TParm insFeeParm = new TParm();
		insFeeParm = RegQETool.getInstance().saveCardBeforeOPB(selectParm,insParm,pat,reg);
		if (insFeeParm.getErrCode() < 0) {
			 res.setIsSuccessed("1");
			 res.setErrorMsg("ҽ������ȡʧ��");
			 xStream.alias("Response", RegResponse.class);
			 outXml = xStream.toXML(res);
			 return outXml;
		}
		//�ж��ܽ���Ƿ�һ��
		String TotalAmount = bodyP.getTotalAmount().trim();
		writerLog2("TotalAmount=========="+TotalAmount);
		
		writerLog2("FeeY=========="+insFeeParm.getDouble("FeeY"));
		if(TotalAmount.length()>0){
			
			writerLog2("TotalAmountdddd=========="+Double.parseDouble(TotalAmount));
			
			if(Double.parseDouble(TotalAmount)!=insFeeParm.getDouble("FeeY")){
			 res.setIsSuccessed("1");
			 res.setErrorMsg("�ܽ���ȷ,�ָ�ʧ��");
			 xStream.alias("Response", RegResponse.class);
			 outXml = xStream.toXML(res);
			 return outXml;
			}
		}		
		// �ָ���
		parm.setData("PAT_NAME", pat.getName());//��������
		resultData = RegQETool.getInstance().insExeFee(true, insFeeParm, parm);
		if (resultData == null || resultData.getErrCode() < 0) {
			res.setIsSuccessed("1");
			res.setErrorMsg("���÷ָ�ʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}				
		Double insurepay  =resultData.getDouble("OTOT_AMT")-
		                   resultData.getDouble("ACCOUNT_AMT");
		result.setInsurePay(df.format(insurepay));//ҽ��֧��
		result.setAccountPay(df.format(resultData.getDouble("ACCOUNT_AMT")));//�����˻�֧��
		result.setCashPay(df.format(resultData.getDouble("UACCOUNT_AMT")));//����ʵ��֧��							
	    }
	    //�Է�
	    else{
	    	
	    	//�ж��ܽ���Ƿ�һ��
	    	String TotalAmount=bodyP.getTotalAmount().trim();
			TParm sumParm = RegQETool.getInstance().getOpdOrderSumArAmt(caseNo, rxNo);
			if(sumParm.getErrCode() < 0){
				res.setIsSuccessed("1");
				res.setErrorMsg("���÷ָ�ʧ��");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}
			writerLog2("�ָ�ʱ�ԷѲ�ѯ=="+sumParm);
			double arAmt = sumParm.getDouble("AR_AMT", 0);
			if(TotalAmount.length()> 0){
				if(Double.parseDouble(TotalAmount)!=arAmt){
					 res.setIsSuccessed("1");
					 res.setErrorMsg("�ܽ���ȷ,�ָ�ʧ��");
					 xStream.alias("Response", RegResponse.class);
					 outXml = xStream.toXML(res);
					 return outXml;
				}
				
			}

	    	result.setInsurePay("0.00");//ҽ��֧��
			result.setAccountPay("0.00");//�����˻�֧��
			result.setCashPay(""+arAmt);//����ʵ��֧������������������������������������	
	    }
		xStream = new XStream();			
		res.setIsSuccessed("0");//�Ƿ�ɹ�(0:�ɹ�,1:ʧ��)
	    res.setErrorMsg("");//������Ϣ
	    String insureSequenceNo = caseNo+"|"+confirmNo;
		result.setInsureSequenceNo(insureSequenceNo);//ҽ��˳���caseNo|comfirm_no
		result.setComment("");
		res.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);
		outXml = xStream.toXML(res);
		return outXml;
	}
	/**
	 * �ɷ�ȷ��
	 * @return
	 */
	public String onInsConfirmOpb(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><InsureSequenceNo>170821000001|</InsureSequenceNo>" +
//				"<PrescriptionCode>17082100006|17082100007</PrescriptionCode><PaymentType>02</PaymentType><OrderNo>170821000001004</OrderNo>" +
//		"</BodyParams></Request>";
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		String outXml = "";
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		TParm parm = new TParm();
	    TParm resultData = new TParm();
		TParm insresultData = new TParm();
		String [] Array=new String [0];
		String caseNo ="";
		String confirmNo = "";
		String insureSequenceNo = bodyP.getInsureSequenceNo().trim();//ҽ��˳���			
		if(insureSequenceNo.length()>0){
			Array=insureSequenceNo.split("\\|");			
		caseNo = Array[0];//�Һ�HISΨһ��	
		if(Array.length > 1){
			confirmNo = Array[1];//ҽ��ȷ������
		}		
		}
		writerLog2("caseNo=========="+caseNo+"===="+caseNo);
		writerLog2("confirmNo=========="+caseNo+"===="+confirmNo);
		
		String prescriptioncode = bodyP.getPrescriptionCode().trim();// ������
		writerLog2("prescriptioncode=========="+caseNo+"====" + prescriptioncode);
		String[] ArrayR = new String[0];
		if (prescriptioncode.length() > 0) {
			ArrayR = prescriptioncode.split("\\|");
		}
		int count = ArrayR.length;
		writerLog2("count=========="+caseNo+"====" + count);
		StringBuffer allrxNo = new StringBuffer();
		for (int i = 0; i < count; i++) {
			String rxno = "";
			rxno = ArrayR[i];
			if (allrxNo.length() > 0)
				allrxNo.append(",");
			allrxNo.append(rxno);
		}
		String rxNo = allrxNo.toString();
		writerLog2("rxNo=========="+caseNo+"====" + rxNo);

		
		
		Double insurepay = 0.00;
		Double accountPay = 0.00;
		Double cashpay = 0.00;
		String insTypeDesc="�Է�";
		
		TParm statusParm = new TParm();
		statusParm.setData("ORDER_NO", bodyP.getOrderNo());
		statusParm.setData("STATUS", "2");
		statusParm.setData("OPT_USER", "QeApp");
		statusParm.setData("OPT_TERM", "0.0.0.0");
		statusParm.setData("TYPE", "OPB");
		TParm reStatusParm = RegQETool.getInstance().insertEktAppStatus(statusParm);
		if(reStatusParm.getErrCode() < 0){
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:���ױ�д��ʧ�� ");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		
		
		//ҽ������
		if(confirmNo.length()>0) {
		parm.setData("CASE_NO", caseNo);				
        parm.setData("CONFIRM_NO",confirmNo);	
		// ��ȡҽ������
		insresultData = RegQETool.getInstance().getInsOpd(parm);
		 writerLog2("insresultData=========="+caseNo+"===="+insresultData);
		if (insresultData.getErrCode() < 0 || insresultData.getCount() <= 0) {	
			
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			
			res.setIsSuccessed("1");
			res.setErrorMsg("��������");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		TParm insParm = new TParm();
		insParm.setData("CONFIRM_NO",confirmNo);
		TParm regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
		//�ж�ҽ������(��ְ ��ͨ����ְ���� ���Ǿ�����)
		String insType = "";
		String inscrowdType =insresultData.getValue("INS_CROWD_TYPE", 0);
		String inspayType =insresultData.getValue("INS_PAT_TYPE", 0);
		if(inscrowdType.equals("1")&&inspayType.equals("1")){
			insType = "1";
			insTypeDesc="ҽ��";
		}
			
		if(inscrowdType.equals("1")&&inspayType.equals("2")){
			insType = "2";
			insTypeDesc="����";
		}
			
		if(inscrowdType.equals("2")&&inspayType.equals("2")){
			insType = "3";
			insTypeDesc="����";
		}
			
		insParm.setData("INS_TYPE", insType);//1.��ְ ��ͨ 2.��ְ���� 3.�Ǿ�����
		insParm.setData("CASE_NO", caseNo);
		insParm.setData("OPT_USER", "QeApp");
		insParm.setData("OPT_TERM",  "0.0.0.0");
		insParm.setData("RECP_TYPE", "OPB");
		insParm.setData("DISEASE_CODE", insresultData.getValue("DISEASE_CODE", 0));//���ز���
		insParm.setData("FeeY", insresultData.getDouble("INS_TOT_AMT", 0));//(�ܽ��)�Ǿ�����ʹ��
		 writerLog2("insParm=========="+caseNo+"===="+insParm);
		TParm opbReadCardParm = new TParm();		
		opbReadCardParm.setData("TOT_AMT", insresultData.getDouble("TOT_AMT", 0));
		opbReadCardParm.setData("PERSON_ACCOUNT_AMT", insresultData.getDouble("TOT_AMT", 0));
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		opbReadCardParm.setData("PAT_TYPE", insresultData.getValue("PAT_TYPE", 0));
		opbReadCardParm.setData("PAY_KIND", insresultData.getValue("PAY_KIND", 0));			
		TParm settlementDetailsParm = new TParm();
		settlementDetailsParm.setData("INS_PAY_AMT", insresultData.getDouble(
				"INS_PAY_AMT", 0));
		settlementDetailsParm.setData("UNREIM_AMT", insresultData.getDouble(
				"UNREIM_AMT", 0));
		settlementDetailsParm.setData("OINSTOT_AMT", insresultData.getDouble(
				"OINSTOT_AMT", 0));
		settlementDetailsParm.setData("OWN_AMT", insresultData.getDouble(
				"OWN_AMT", 0));
		settlementDetailsParm.setData("ILLNESS_SUBSIDY_AMT", insresultData.getDouble(
				"ILLNESS_SUBSIDY_AMT", 0));
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		insParm.setData("settlementDetailsParm", settlementDetailsParm
				.getData());
		 writerLog2("insParm=======WW==="+caseNo+"===="+insParm);
		resultData = INSTJReg.getInstance().insCommFunction(insParm.getData());
		if (resultData.getErrCode() < 0) {
			
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			
			res.setIsSuccessed("1");
			res.setErrorMsg("ҽ��֧��ʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;					
		}
		if(insType.equals("1")||insType.equals("2")){
			insurepay = insresultData.getDouble("INS_TOT_AMT", 0) - // �ܽ��
			insresultData.getDouble("UNACCOUNT_PAY_AMT", 0) - // ���˻�֧��
			insresultData.getDouble("UNREIM_AMT", 0);// ����δ����
			cashpay =insresultData.getDouble("UNACCOUNT_PAY_AMT", 0)+         
			insresultData.getDouble("UNREIM_AMT", 0);// �ֽ�֧�����
	        accountPay = insresultData.getDouble("ACCOUNT_PAY_AMT", 0);	
		}
        if(insType.equals("3")){
        	if (null != insresultData.getValue("REIM_TYPE", 0)
					&& insresultData.getInt("REIM_TYPE", 0) == 1) {
        		insurepay = insresultData.getDouble("TOTAL_AGENT_AMT", 0)
						+ insresultData.getDouble("ARMY_AI_AMT", 0)
						+ insresultData.getDouble("FLG_AGENT_AMT", 0)
						+ insresultData.getDouble("ILLNESS_SUBSIDY_AMT", 0)//����󲡽��
						- insresultData.getDouble("UNREIM_AMT", 0);
			} else {
				insurepay = insresultData.getDouble("TOTAL_AGENT_AMT", 0)
						+ insresultData.getDouble("FLG_AGENT_AMT", 0)
						+ insresultData.getDouble("ARMY_AI_AMT", 0)
				        + insresultData.getDouble("ILLNESS_SUBSIDY_AMT", 0);//����󲡽��
			}
			// ����ʵ��֧��
        	cashpay = insresultData.getDouble("INS_TOT_AMT", 0)
					- insresultData.getDouble("TOTAL_AGENT_AMT", 0)
					- insresultData.getDouble("FLG_AGENT_AMT", 0)
					- insresultData.getDouble("ARMY_AI_AMT", 0)
					- insresultData.getDouble("ILLNESS_SUBSIDY_AMT", 0)//����󲡽��
					+ insresultData.getDouble("UNREIM_AMT", 0);	
		}	
        
        
        
        
	}
		
		
		TParm opdOrderParm = RegQETool.getInstance().getOpdOrderNotBill(caseNo, rxNo);

		if(opdOrderParm.getErrCode() < 0){
			
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(opdOrderParm.getCount() < 0){
			
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			
			res.setIsSuccessed("1");
			res.setErrorMsg("ERR:�޽ɷ�����");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		
		// ȡ�����ݿ�����
		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		if (conn == null){
			
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("Err:δȡ�����ݿ�����");
			xStream.alias("Response", RegResponse.class);
			 outXml = xStream.toXML(res);
			return outXml;
		}
		
		//����opd_order��������
		String businessNo="";
		String billType = "E";
		String payType = bodyP.getPaymentType();
		String orderNo = bodyP.getOrderNo();
		String qeFlg ="";
		String historyNo= ""; //ҽ�ƿ���ʷ���׺�
		
		// 01 ҽ�ƿ�  02 ֧����  4����֧�� 6΢��
		if("01".equals(payType)){
			 businessNo= RegQETool.getInstance().getBussinessNo(orderNo);
			 historyNo = RegQETool.getInstance().getektMasterHistoryNo(businessNo);
			 qeFlg ="E";

		}
		if("02".equals(payType) || "6".equals(payType) ){
			
			 if("02".equals(payType)){
				 qeFlg ="A";
			 }
			 if("6".equals(payType)){
				 qeFlg ="W";
			 }
			 
			 businessNo=SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO","TRADE_NO");
			if (businessNo.length() == 0) {
				
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("Err:ҽ�ƿ����׺����������");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}
			 historyNo= RegQETool.getInstance().getHistoryNo();
			

		}
		
		if("4".equals(payType)){
			qeFlg ="Y";
			if(confirmNo.length()>0) {
				businessNo=SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO","TRADE_NO");
				if (businessNo.length() == 0) {
					
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("Err:ҽ�ƿ����׺����������");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
				 historyNo= RegQETool.getInstance().getHistoryNo();
			}

		}
		
		
		 //ȡ��ԭ��õ�Ʊ�ݺ�
        String receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                "RECEIPT_NO",
                "RECEIPT_NO");
        if (receiptNo.length() < 0) {
        	RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			conn.rollback();
			conn.close();

			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("Err:�վݺ�ȡ��ʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;

		}
        
        TParm opdHistroyParm = new TParm();
		//��opd_order���е�ҽ��bill_flg��ΪY   
		for (int i = 0; i < opdOrderParm.getCount(); i++) {
		 String	sql = "UPDATE OPD_ORDER SET BUSINESS_NO='"
				+ businessNo + "', BILL_FLG='Y', QE_BILL_FLG='"+qeFlg+"',  RECEIPT_NO='"+receiptNo
				+ "' , BILL_DATE=SYSDATE ,BILL_USER='QeApp'"
				+ ",BILL_TYPE='"+billType+"' WHERE CASE_NO='"
				+ opdOrderParm.getValue("CASE_NO",i) + "' " + "AND RX_NO ='"
				+ opdOrderParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
				+ opdOrderParm.getValue("SEQ_NO", i) + "' ";
		 writerLog2("update_opdorder==="+caseNo+"===="+sql);
		 TParm updateOpd = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (updateOpd.getErrCode() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();

				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("UPDATE OPD_ORDER Err:�շ�ʧ��");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;

			}
			
			sql= "SELECT * FROM OPD_ORDER WHERE CASE_NO='"
				+ opdOrderParm.getValue("CASE_NO",i) + "' " + "AND RX_NO ='"
				+ opdOrderParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
				+ opdOrderParm.getValue("SEQ_NO", i) + "' ";
			TParm hParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(hParm.getCount() > 0){
				hParm.setData("BUSINESS_NO", 0, businessNo);
				hParm.setData("BILL_FLG", 0, "Y");
				hParm.setData("QE_BILL_FLG", 0, qeFlg);
				hParm.setData("RECEIPT_NO", 0, receiptNo);
				hParm.setData("BILL_DATE", 0, SystemTool.getInstance().getDate().toString());
				hParm.setData("BILL_USER", 0, "QeApp");
				hParm.setData("BILL_TYPE", 0, billType);
				opdHistroyParm.addRowData(hParm, 0);
			}
			
			
		}
		
		//����ҽ����ʷ��
		if(opdHistroyParm.getCount("ORDER_CODE") > 0){
			TParm inParm = new TParm();
			inParm.setData("orderParm", opdHistroyParm.getData());
			inParm.setData("EKT_HISTORY_NO",historyNo);
			inParm.setData("OPT_TYPE", "UPDATE");
			inParm.setData("OPT_USER", "QeApp");
			inParm.setData("OPT_TERM", "0.0.0.0");
			inParm.setData("MZCONFIRM_NO", confirmNo);
			TParm historyParm = AssembleTool.getInstance().parmToSql(inParm);
			TParm sqlParm = historyParm.getParm("sqlParm");
			String ids = historyParm.getValue("LastHistoryIds");

			for (int j = 0; j < sqlParm.getCount("SQL"); j++) {
//				writerLog2("������ʷ��:==="+sqlTemp[j]);
				TParm reParm = new TParm(TJDODBTool.getInstance().update(sqlParm.getValue("SQL",j),conn));
				if(reParm.getErrCode() < 0){
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();

					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("UPDATE OPD_ORDER_HISTORY Err:�շ�ʧ��");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
			}
			
			if(ids.length() > 0){
				ids = ids.substring(0, ids.length()-1);
				String sql="UPDATE OPD_ORDER_HISTORY_NEW SET ACTIVE_FLG='N' WHERE HISTORY_ID IN ("+ids+")";
				TParm reParm = new TParm(TJDODBTool.getInstance().update(sql,conn));
				if(reParm.getErrCode() < 0){
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();

					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("UPDATE OPD_ORDER_HISTORY Err:�շ�ʧ��");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
			}
			
			
		}
		
		
		  //����hl7Ϊ�շ�״̬

		TParm hl7ParmEnd = new TParm(); // HL7���ݼ��� ������� �ļ���ҽ������ ���ͽӿ�ʹ��
        for (int i = 0; i < opdOrderParm.getCount(); i++) {
        	if ((opdOrderParm.getValue("CAT1_TYPE", i).equals("RIS") || opdOrderParm
    				.getValue("CAT1_TYPE", i).equals("LIS"))
    				&& opdOrderParm.getBoolean("SETMAIN_FLG", i)
    				&& opdOrderParm.getValue("ORDERSET_CODE", i).equals(
    						opdOrderParm.getValue("ORDER_CODE", i))) {
//        		//���hl7��������
	
        		hl7ParmEnd.addData("ORDER_CAT1_CODE", opdOrderParm.getData(
    					"ORDER_CAT1_CODE", i));
    			hl7ParmEnd.addData("TEMPORARY_FLG", opdOrderParm.getData(
    					"TEMPORARY_FLG", i));
    			hl7ParmEnd.addData("ADM_TYPE", opdOrderParm.getData("ADM_TYPE", i));
    			hl7ParmEnd.addData("RX_NO", opdOrderParm.getData("RX_NO", i));
    			hl7ParmEnd.addData("SEQ_NO", opdOrderParm.getData("SEQ_NO", i));
    			hl7ParmEnd.addData("MED_APPLY_NO", opdOrderParm.getData(
    					"MED_APPLY_NO", i));
    			hl7ParmEnd.addData("CAT1_TYPE", opdOrderParm.getData("CAT1_TYPE", i));
    			hl7ParmEnd.addData("BILL_FLG", "Y");
    			hl7ParmEnd.addData("PAT_NAME", opdOrderParm.getValue("PAT_NAME1", i));
        		
    			
        		
        	String	sql = "UPDATE MED_APPLY SET BILL_FLG='Y'" 
				+ " WHERE APPLICATION_NO='"
				+ opdOrderParm.getValue("MED_APPLY_NO", i)
				+ "' AND CASE_NO='" + opdOrderParm.getValue("CASE_NO",i)
				+ "' AND ORDER_NO='"
				+ opdOrderParm.getValue("RX_NO", i)
				+ "' AND SEQ_NO='"
				+ opdOrderParm.getValue("SEQ_NO", i) + "'";
		    TParm hl7Tparm = new TParm(TJDODBTool.getInstance().update(sql,conn));
		    if (hl7Tparm.getErrCode() < 0) {
		    	RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();

				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg(" MED_APPLY Err:�շ�ʧ��");
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;

			}
        		
        	}
        }

        
        //ҩ�������
        boolean phaFlg = false;
		for (int j = 0; j < opdOrderParm.getCount(); j++) {
			if (opdOrderParm.getValue("CAT1_TYPE", j).equals("PHA")) {
				phaFlg = true;
				break;

			}

		}
		if(phaFlg){
			 SocketLink client1 = SocketLink.running("","ODO", "ODO");
				if (client1.isClose()) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();

					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg(" Err1:��ҩ�����������ʧ��");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;

				}
				client1.sendMessage("PHAMAIN", "RX_NO:"//PHAMAIN :SKT_USER ���������
						+ rxNo + "|MR_NO:" +  opdOrderParm.getValue("MR_NO", 0)+ "|PAT_NAME:" + opdOrderParm.getValue("PAT_NAME1", 0));
				if (client1 == null){
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();

					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg(" Err2:��ҩ�����������ʧ��");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
				client1.close();
		}
       
		

		//����bil_opb_recp Ʊ�ݱ�

        String rexpCode = "";
        double arAmt = 0.00;
        double allArAmt = 0.00;
        String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE' ORDER BY SEQ";
        TParm sysChargeParm = new TParm(TJDODBTool.getInstance().select(sql));
        sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
                + " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
                + " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
                + " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
                + " FROM BIL_RECPPARM WHERE ADM_TYPE ='O'";
        TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
        // �����ʷ��¼��ѯ�˲�������δ��Ʊ�������ܽ��
         parm=new TParm();
        parm.setData("CASE_NO",caseNo);
        int chargeCount = sysChargeParm.getCount("ID");
        String[] chargeName = new String[30];
        int index = 1;
        for (int i = 0; i < 30; i++) {
            String chargeTemp = "CHARGE";
            if (i < 9) {
                chargeName[i] = bilRecpParm.getData(chargeTemp + "0" + index, 0).
                                toString();
            } else {
                chargeName[i] = bilRecpParm.getData(chargeTemp + index, 0).
                                toString();
            }
            index++;
        }
        TParm p = new TParm();
        for (int i = 0; i < chargeCount; i++) {
            String sysChargeId = sysChargeParm.getData("ID", i).toString();
            for (int j = 0; j < chargeName.length; j++) {
                if (sysChargeId.equals(chargeName[j])) {
                    p.setData("CHARGE", i, j);
                    p.setData("ID", i, sysChargeParm.getData("ID", i));
                    p.setData("CHN_DESC", i,
                              sysChargeParm.getData("CHN_DESC", i));
                    break;
                }
            }
        }
        
        double[] chargeDouble = new double[30]; // ������
        String idCharge = "";
        int charge = 0;
        for (int i = 0; i < opdOrderParm.getCount(); i++) {
            rexpCode = opdOrderParm.getValue("REXP_CODE", i);
            arAmt = opdOrderParm.getDouble("AR_AMT", i);
            allArAmt = allArAmt + arAmt;
            for (int j = 0; j < p.getCount("ID"); j++) {
                idCharge = p.getData("ID", j).toString();
                charge = p.getInt("CHARGE", j);
                if (rexpCode.equals(idCharge))
                    chargeDouble[charge] = chargeDouble[charge] + arAmt;
            }
        }
		
        
        TParm opbreceipt = new TParm();
        opbreceipt.setData("CASE_NO", opdOrderParm.getData("CASE_NO",0));
        opbreceipt.setData("ADM_TYPE", opdOrderParm.getValue("ADM_TYPE",0));
        opbreceipt.setData("REGION_CODE", opdOrderParm.getData("REGION_CODE",0));
        opbreceipt.setData("MR_NO", opdOrderParm.getData("MR_NO",0));
        opbreceipt.setData("RESET_RECEIPT_NO", "");
        opbreceipt.setData("PRINT_NO", "");
        opbreceipt.setData("BILL_DATE", SystemTool.getInstance().getDate());
        opbreceipt.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        opbreceipt.setData("PRINT_DATE",  "");
         index = 1;
        // д������
        for (int i = 0; i < chargeDouble.length; i++) {
            String chargeTemp = "CHARGE";
            if (i < 9) {
                opbreceipt.setData(chargeTemp + "0" + index, chargeDouble[i]);
            } else {
                opbreceipt.setData(chargeTemp + index, chargeDouble[i]);
            }
            index++;
        }
        
       
        opbreceipt.setData("TOT_AMT", allArAmt); //�ܽ��
        opbreceipt.setData("RECEIPT_NO", receiptNo);
        opbreceipt.setData("REDUCE_REASON", "");
        opbreceipt.setData("REDUCE_AMT", 0.00);
        opbreceipt.setData("REDUCE_DATE", "");
        opbreceipt.setData("REDUCE_DEPT_CODE", "");
        opbreceipt.setData("REDUCE_RESPOND", "");
        opbreceipt.setData("QE_PAY_TYPE", "");
        opbreceipt.setData("AR_AMT", allArAmt); //�ܽ��
        double payInsCard = insurepay;
        if(payInsCard == 0){
        	cashpay = allArAmt;
        }
        
        if("01".equals(payType)){
        	opbreceipt.setData("PAY_MEDICAL_CARD", allArAmt - payInsCard); // �۳�ҽ�����
        	opbreceipt.setData("ALIPAY", 0.00);
        	 
		}else if("02".equals(payType)){
			opbreceipt.setData("PAY_MEDICAL_CARD", 0.00); // �۳�ҽ�����
       	 	opbreceipt.setData("ALIPAY", allArAmt - payInsCard);
       	 opbreceipt.setData("QE_PAY_TYPE", "2");
       	 
		}else if("6".equals(payType)){
			opbreceipt.setData("PAY_MEDICAL_CARD", 0.00); // �۳�ҽ�����
       	 	opbreceipt.setData("ALIPAY", allArAmt - payInsCard);
       	 opbreceipt.setData("QE_PAY_TYPE", "6");
				
		}else{
			opbreceipt.setData("PAY_MEDICAL_CARD", 0.00); // �۳�ҽ�����
       	 	opbreceipt.setData("ALIPAY", 0.00);
		}
       
        opbreceipt.setData("PAY_CASH", 0.00);
        opbreceipt.setData("PAY_OTHER1", 0.00); //��ɫͨ�����        
        opbreceipt.setData("PAY_BANK_CARD", 0.00);
        opbreceipt.setData("PAY_INS_CARD", payInsCard);
        opbreceipt.setData("PAY_CHECK", 0.00);
        opbreceipt.setData("PAY_DEBIT", 0.00);
        opbreceipt.setData("PAY_BILPAY", 0.00);
        opbreceipt.setData("PAY_INS", 0.00);
        opbreceipt.setData("PAY_OTHER2", 0.00);
        opbreceipt.setData("PAY_REMARK", "");
        opbreceipt.setData("CASHIER_CODE", "QeApp");
        opbreceipt.setData("OPT_USER", "QeApp");
        opbreceipt.setData("OPT_DATE", SystemTool.getInstance().getDate());
        opbreceipt.setData("OPT_TERM", "0.0.0.0");
        opbreceipt.setData("ORDER_NO", orderNo);
        opbreceipt.setData("INS_TYPE", insTypeDesc);
        opbreceipt.setData("ACCOUNT_PAY", accountPay);
        opbreceipt.setData("RE_SOURCE", getBussinessDesc(bodyP.getBusinessFrom())); 
        opbreceipt.setData("CONFIRM_NO", confirmNo); 

        
        TParm insertOpbRecp = RegQETool.getInstance().insertOpbReceipt(opbreceipt, conn);
        if (insertOpbRecp.getErrCode() < 0) {
        	RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			conn.rollback();
			conn.close();
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("insertOpbRecp Err:"+insertOpbRecp);
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
			
		}
    	//ҽ������
		if(confirmNo.length() > 0) {
	  //����ҽ�����ױ�INS_OPD�е�receipt_no
		TParm resultUpdate = RegQETool.getInstance().updateInsOpd(receiptNo,confirmNo,caseNo);
		if (resultUpdate.getErrCode() < 0) {
			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
			res.setIsSuccessed("1");
			res.setErrorMsg("����ҽ�����ױ�ʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;					
	 	   }
	    }
        
        //����caseNo�õ�ҽ�ƿ��ţ������ţ������
		 TParm ektParm = RegQETool.getInstance().getEktParm(caseNo);
		 int seq = RegQETool.getInstance().getHistorySeq(ektParm.getValue("MR_NO",0));
        
        if("02".equals(payType) || "6".equals(payType)){

			 //��ҽ�ƿ��г�Ǯ����ҽ�ƿ���Ǯ
			 TParm bilDetailParm = new TParm();
			 String billBusinessNo=SystemTool.getInstance().getNo("ALL", "EKT", "BUSINESS_NO","BUSINESS_NO");
			 if (billBusinessNo.length() == 0) {
				 RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("Err:ҽ�ƿ���ֵ���׺����������");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
			 
			 bilDetailParm.setData("BUSINESS_NO", billBusinessNo);
			 bilDetailParm.setData("BUSINESS_SEQ", "0");
			 bilDetailParm.setData("CARD_NO", ektParm.getValue("CARD_NO",0));
			 bilDetailParm.setData("MR_NO", ektParm.getValue("MR_NO",0));
			 bilDetailParm.setData("CASE_NO", "none");			
			 bilDetailParm.setData("SEQ_NO", 0);
			 if("02".equals(payType)){
				 bilDetailParm.setData("ORDER_CODE", "֧������ֵ");
				 bilDetailParm.setData("RX_NO", "֧������ֵ");
				 bilDetailParm.setData("CHARGE_FLG", "0"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����;0,֧������ֵ;9,΢�ų�ֵ)
			 }
			 if("6".equals(payType)){
				 bilDetailParm.setData("ORDER_CODE", "΢�ų�ֵ");
				 bilDetailParm.setData("RX_NO", "΢�ų�ֵ");
				 bilDetailParm.setData("CHARGE_FLG", "9"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����;0,֧������ֵ;9,΢�ų�ֵ)
			 }
			
			 
			 bilDetailParm.setData("ORIGINAL_BALANCE", ektParm.getDouble("CURRENT_BALANCE",0)); //�շ�ǰ���
			 bilDetailParm.setData("BUSINESS_AMT", allArAmt - payInsCard);			 
			 double currentBalance = allArAmt - payInsCard+ektParm.getDouble("CURRENT_BALANCE",0);
			 bilDetailParm.setData("CURRENT_BALANCE", currentBalance);
			 bilDetailParm.setData("CASHIER_CODE", "QeApp");
			 bilDetailParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
			 bilDetailParm.setData("BUSINESS_STATUS", "1");
			 bilDetailParm.setData("ACCNT_STATUS", "0");
			 bilDetailParm.setData("OPT_USER", "QeApp");
			 bilDetailParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
			 bilDetailParm.setData("OPT_TERM", "0.0.0.0");
			 TParm insertEKTDetail = RegQETool.getInstance().insertEKTDetail(bilDetailParm, conn);
			 if(insertEKTDetail.getErrCode() < 0){
				 RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				    conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEKTDetail Err:"+insertEKTDetail);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
			 }
			
			//��ֵдҽ�ƿ���ʷ��
			 
			 TParm ektMasHisParm = new TParm();  
			 ektMasHisParm.setData("MR_NO", bilDetailParm.getData("MR_NO"));
			 ektMasHisParm.setData("OLD_AMT", bilDetailParm.getDouble("ORIGINAL_BALANCE"));
			 ektMasHisParm.setData("AMT", bilDetailParm.getDouble("BUSINESS_AMT"));
			 ektMasHisParm.setData("EKT_AMT", bilDetailParm.getDouble("CURRENT_BALANCE"));
			 ektMasHisParm.setData("BUSINESS_TYPE", "EKT_IN"); //��ֵ
			 ektMasHisParm.setData("CARD_NO", bilDetailParm.getValue("CARD_NO"));
			 ektMasHisParm.setData("OPT_USER", bilDetailParm.getValue("OPT_USER"));
			 ektMasHisParm.setData("OPT_TERM", bilDetailParm.getValue("OPT_TERM"));
			 ektMasHisParm.setData("CASE_NO", "");
			 ektMasHisParm.setData("CONFIRM_NO", "");		
			 ektMasHisParm.setData("EKT_BUSINESS_NO", bilDetailParm.getValue("BUSINESS_NO"));
			 ektMasHisParm.setData("HISTORY_NO", RegQETool.getInstance().getHistoryNo());
			 ektMasHisParm.setData("SEQ", seq);
			TParm insertEktMasterHistory = RegQETool.getInstance().insertEktMasterHistory(ektMasHisParm, conn);
			if(insertEktMasterHistory.getErrCode() < 0){
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				 conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktMasterHistory Err:"+insertEktMasterHistory);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
			}
			 

		        
		        
			TParm billParm = new TParm();
			billParm.setData("BIL_BUSINESS_NO", billBusinessNo);
			billParm.setData("CARD_NO", ektParm.getValue("CARD_NO", 0)); // ����
			billParm.setData("CURT_CARDSEQ", 0); // ���
			
			if(payType.equals("6")){
				billParm.setData("ACCNT_TYPE", "8"); // ��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�,7:֧������ֵ��8��΢�ų�ֵ)
			}
			if(payType.equals("02")){
				billParm.setData("ACCNT_TYPE", "7"); // ��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�,7:֧������ֵ)
			}
			
			
			billParm.setData("MR_NO", ektParm.getValue("MR_NO", 0)); // ������
			billParm.setData("ID_NO", ektParm.getValue("IDNO", 0)); // ���֤��
			billParm.setData("NAME", ektParm.getValue("PAT_NAME", 0)); // ��������
			billParm.setData("AMT", allArAmt - payInsCard); // ��ֵ���
			billParm.setData("CREAT_USER", "QeApp"); // ִ����Ա
			billParm.setData("OPT_USER", "QeApp"); // ������Ա
			billParm.setData("OPT_TERM", "0.0.0.0"); // ִ��ip
			billParm.setData("GATHER_TYPE", "A"); // ֧����ʽ
			// zhangp 20120109
			billParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());
			billParm.setData("PROCEDURE_AMT", "0.0.0.0");
			TParm insertEKTBilPay = RegQETool.getInstance().insertEKTBilPay(billParm, conn);
			if(insertEKTBilPay.getErrCode() < 0){
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("insertEKTBilPay Err:" + insertEKTBilPay);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
			}
			
			
			
			TParm ektTradeParm = new TParm();
			ektTradeParm.setData("TRADE_NO", businessNo);
			ektTradeParm.setData("CARD_NO", ektParm.getValue("CARD_NO", 0));
			ektTradeParm.setData("MR_NO", ektParm.getValue("MR_NO", 0));
			ektTradeParm.setData("CASE_NO", caseNo);
			ektTradeParm.setData("PAT_NAME", ektParm.getValue("PAT_NAME", 0));
			ektTradeParm.setData("OLD_AMT", currentBalance);
			ektTradeParm.setData("AMT", allArAmt - payInsCard);
			ektTradeParm.setData("STATE", "1");
			ektTradeParm.setData("BUSINESS_TYPE", "OPB");
			
			ektTradeParm.setData("GREEN_BALANCE", "0");
			ektTradeParm.setData("GREEN_BUSINESS_AMT", "0");
			ektTradeParm.setData("OPT_USER", "QeApp");		
			ektTradeParm.setData("OPT_TERM", "0.0.0.0");
			
			ektTradeParm.setData("BIL_BUSINESS_NO", billBusinessNo);
			
			if(payType.equals("6")){
				ektTradeParm.setData("BUSINESS_APP_TYPE", "11");  //10���Һ�΢�ų�ֵ 11���ɷ�΢�ų�ֵ
			}
			if(payType.equals("02")){
				ektTradeParm.setData("BUSINESS_APP_TYPE", "9");  //8���Һ�֧������ֵ 9���ɷ�֧������ֵ
			}
			
			String orderTime =SystemTool.getInstance().getDate().toString();
			orderTime=orderTime.replaceAll("-", "").replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14);
			ektTradeParm.setData("ORDER_TIME", orderTime);
			ektTradeParm.setData("ORDER_NO", orderNo);
			
			TParm resultParm = RegQETool.getInstance().insertEktAppTrade(ektTradeParm, conn);
			if (resultParm.getErrCode() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("insertEktAppTrade Err:"+resultParm);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
				
			}
			
			//ҽ����Ҫ��ҽ�ƿ�ekt_trade���л�д����
			if(confirmNo.length()>0) {
				//ȫ���
				ektTradeParm.setData("AMT", allArAmt);
				resultParm = RegQETool.getInstance().insertEktTrade(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktTrade Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				ektTradeParm.setData("HISTORY_NO", historyNo);// �ڲ����׺�
				ektTradeParm.setData("EKT_AMT", currentBalance-allArAmt);// �ڲ����׺�
				ektTradeParm.setData("EKT_BUSINESS_NO", "");
				ektTradeParm.setData("CONFIRM_NO", "");
				seq = seq + 1;
				ektTradeParm.setData("SEQ", seq);
				
				resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktMasterHistory Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				
				
				
				ektTradeParm.setData("AMT", -payInsCard);
				ektTradeParm.setData("BUSINESS_TYPE", "OPBT");
				ektTradeParm.setData("OLD_AMT", currentBalance-allArAmt);
				
				String businessNoIns=SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO","TRADE_NO");
					if (businessNoIns.length() == 0) {
						RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
						conn.rollback();
						conn.close();
						xStream = new XStream();
						res.setIsSuccessed("1");
						res.setErrorMsg("Err:02ҽ�ƿ����׺����������");
						xStream.alias("Response", RegResponse.class);
						outXml = xStream.toXML(res);
						return outXml;
					}
				ektTradeParm.setData("TRADE_NO", businessNoIns);
				resultParm = RegQETool.getInstance().insertEktTrade(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktTrade Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				
				
				ektTradeParm.setData("HISTORY_NO", RegQETool.getInstance().getHistoryNo());// �ڲ����׺�
				ektTradeParm.setData("EKT_AMT", currentBalance-allArAmt+payInsCard);// �ڲ����׺�
				ektTradeParm.setData("EKT_BUSINESS_NO", "");
				ektTradeParm.setData("CONFIRM_NO", confirmNo);
				seq = seq +1;
				ektTradeParm.setData("SEQ", seq);
				
				
				resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktMasterHistory Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				
				
				
				
			}else{
				resultParm = RegQETool.getInstance().insertEktTrade(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktTrade Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				ektTradeParm.setData("HISTORY_NO", historyNo);// �ڲ����׺�
				ektTradeParm.setData("EKT_AMT", currentBalance-allArAmt+payInsCard);
				ektTradeParm.setData("EKT_BUSINESS_NO", "");
				ektTradeParm.setData("CONFIRM_NO", "");
				seq = seq +1;
				ektTradeParm.setData("SEQ", seq);
				
				resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktMasterHistory Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
			}

        }
        
        if("01".equals(payType)){
        	String treadNo=RegQETool.getInstance().getTradeNo(bodyP.getOrderNo());
     		TParm resultParm = RegQETool.getInstance().updateEktTradeCaseNo(treadNo, caseNo, conn);
     		if (resultParm.getErrCode() < 0) {
     			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
     			conn.rollback();
     			conn.close();
     			xStream = new XStream();
     			res.setIsSuccessed("1");
     			res.setErrorMsg("updateEktTradeCaseNo Err:"+resultParm);
     			xStream.alias("Response", RegResponse.class);
     			outXml = xStream.toXML(res);
     			return outXml;
     			
     		}
     		
     		TParm confirmParm = new TParm();
     		if(confirmNo.length()>0) {
     			confirmParm = RegQETool.getInstance().selEktMasterHistory(treadNo);
     		}
     		
     		resultParm = RegQETool.getInstance().updateEktMasterHistoryCaseNo(treadNo, caseNo, confirmNo, conn);
			if (resultParm.getErrCode() < 0) {
				RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
				conn.rollback();
				conn.close();
				xStream = new XStream();
				res.setIsSuccessed("1");
				res.setErrorMsg("updateEktMasterHistoryCaseNo Err:"+resultParm);
				xStream.alias("Response", RegResponse.class);
				outXml = xStream.toXML(res);
				return outXml;
				
			}
     		
     		
     		//ҽ��֧��ʱ��Ҫ��дҽ�ƿ����ױ�����
     		if(confirmNo.length()>0) {
     			resultParm = RegQETool.getInstance().updateEktTradeAmt(treadNo, allArAmt, conn);
         		if (resultParm.getErrCode() < 0) {
         			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
         			conn.rollback();
         			conn.close();
         			xStream = new XStream();
         			res.setIsSuccessed("1");
         			res.setErrorMsg("updateEktTradeAmt Err:"+resultParm);
         			xStream.alias("Response", RegResponse.class);
         			outXml = xStream.toXML(res);
         			return outXml;
         			
         		}

         		resultParm = RegQETool.getInstance().updateEktMasterHistoryAmt(confirmParm.getValue("MR_NO"),confirmParm.getValue("SEQ_NO") ,allArAmt, conn);
         		if (resultParm.getErrCode() < 0) {
         			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
         			conn.rollback();
         			conn.close();
         			xStream = new XStream();
         			res.setIsSuccessed("1");
         			res.setErrorMsg("updateEktMasterHistoryAmt Err:"+resultParm);
         			xStream.alias("Response", RegResponse.class);
         			outXml = xStream.toXML(res);
         			return outXml;
         			
         		}
         		
         		TParm ektTrade = RegQETool.getInstance().getEktTrade(treadNo);
         		if(ektTrade.getCount() < 0){
         			RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
         			conn.rollback();
         			conn.close();
         			xStream = new XStream();
         			res.setIsSuccessed("1");
         			res.setErrorMsg("ektTrade Err:"+ektTrade);
         			xStream.alias("Response", RegResponse.class);
         			outXml = xStream.toXML(res);
         			return outXml;
         		}
         		
         		TParm ektTradeParm = new TParm();
         		String businessNoIns=SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO","TRADE_NO");
				if (businessNoIns.length() == 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("Err:01ҽ�ƿ����׺����������");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
    			ektTradeParm.setData("TRADE_NO", businessNoIns);
    			ektTradeParm.setData("CARD_NO", ektTrade.getValue("CARD_NO", 0));
    			ektTradeParm.setData("MR_NO", ektTrade.getValue("MR_NO", 0));
    			ektTradeParm.setData("CASE_NO", caseNo);
    			ektTradeParm.setData("PAT_NAME", ektTrade.getValue("PAT_NAME", 0));
    			ektTradeParm.setData("OLD_AMT", ektTrade.getDouble("OLD_AMT",0)-allArAmt);
    			ektTradeParm.setData("AMT", - payInsCard);
    			ektTradeParm.setData("STATE", "1");
    			ektTradeParm.setData("BUSINESS_TYPE", "OPBT");
    			
    			ektTradeParm.setData("GREEN_BALANCE", "0");
    			ektTradeParm.setData("GREEN_BUSINESS_AMT", "0");
    			ektTradeParm.setData("OPT_USER", "QeApp");		
    			ektTradeParm.setData("OPT_TERM", "0.0.0.0");
    			resultParm = RegQETool.getInstance().insertEktTrade(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktTrade Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				
				ektTradeParm.setData("HISTORY_NO", RegQETool.getInstance().getHistoryNo());// �ڲ����׺�
				ektTradeParm.setData("EKT_AMT", ektTrade.getDouble("OLD_AMT",0)-allArAmt+payInsCard);// �ڲ����׺�
				ektTradeParm.setData("EKT_BUSINESS_NO", "");
				ektTradeParm.setData("CONFIRM_NO", confirmNo);
				ektTradeParm.setData("SEQ", seq);
				
				resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktMasterHistory Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
         		
     			
     		}
     		
        }
        
        if("4".equals(payType)){
        	//ҽ��֧��ʱ��Ҫ��дҽ�ƿ����ױ�����
     		if(confirmNo.length()>0) {
     			TParm ektTradeParm = new TParm();
    			ektTradeParm.setData("TRADE_NO", businessNo);
    			ektTradeParm.setData("CARD_NO", ektParm.getValue("CARD_NO", 0));
    			ektTradeParm.setData("MR_NO", ektParm.getValue("MR_NO", 0));
    			ektTradeParm.setData("CASE_NO", caseNo);
    			ektTradeParm.setData("PAT_NAME", ektParm.getValue("PAT_NAME", 0));
    			ektTradeParm.setData("OLD_AMT", ektParm.getDouble("CURRENT_BALANCE",0));
    			ektTradeParm.setData("AMT", allArAmt);
    			ektTradeParm.setData("STATE", "1");
    			ektTradeParm.setData("BUSINESS_TYPE", "OPB");   			
    			ektTradeParm.setData("GREEN_BALANCE", "0");
    			ektTradeParm.setData("GREEN_BUSINESS_AMT", "0");
    			ektTradeParm.setData("OPT_USER", "QeApp");		
    			ektTradeParm.setData("OPT_TERM", "0.0.0.0");
    			TParm resultParm = RegQETool.getInstance().insertEktTrade(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktTrade Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				ektTradeParm.setData("HISTORY_NO", historyNo);// �ڲ����׺�
				ektTradeParm.setData("EKT_AMT", ektParm.getDouble("CURRENT_BALANCE",0)-allArAmt);// �ڲ����׺�
				ektTradeParm.setData("EKT_BUSINESS_NO", "");
				ektTradeParm.setData("CONFIRM_NO", "");
				ektTradeParm.setData("SEQ", seq);
				
				resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktMasterHistory Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				
				
				
				ektTradeParm.setData("AMT", -allArAmt);
				ektTradeParm.setData("BUSINESS_TYPE", "OPBT");
				ektTradeParm.setData("OLD_AMT", ektParm.getDouble("CURRENT_BALANCE",0)-allArAmt);
				
				String businessNoIns=SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO","TRADE_NO");
					if (businessNoIns.length() == 0) {
						RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
						conn.rollback();
						conn.close();
						xStream = new XStream();
						res.setIsSuccessed("1");
						res.setErrorMsg("Err:4ҽ�ƿ����׺����������");
						xStream.alias("Response", RegResponse.class);
						outXml = xStream.toXML(res);
						return outXml;
					}
				ektTradeParm.setData("TRADE_NO", businessNoIns);
				resultParm = RegQETool.getInstance().insertEktTrade(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktTrade Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				
				ektTradeParm.setData("HISTORY_NO", RegQETool.getInstance().getHistoryNo());// �ڲ����׺�
				ektTradeParm.setData("EKT_AMT", ektParm.getDouble("CURRENT_BALANCE",0));// �ڲ����׺�
				ektTradeParm.setData("EKT_BUSINESS_NO", "");
				ektTradeParm.setData("CONFIRM_NO", confirmNo);
				seq = seq + 1;
				ektTradeParm.setData("SEQ", seq);
				
				resultParm = RegQETool.getInstance().insertEktMasterHistory(ektTradeParm, conn);
				if (resultParm.getErrCode() < 0) {
					RegQETool.getInstance().updateEktAppStatus( bodyP.getOrderNo(), "0");
					conn.rollback();
					conn.close();
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("insertEktMasterHistory Err:"+resultParm);
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
					
				}
				

     		}
        }
       
        
     
        conn.commit();
        conn.close();
        
        //���뷢����Ϣ       
        List list = new ArrayList();
//        writerLog2("hl7ParmEnd==="+hl7ParmEnd);
		for (int i = 0; i < hl7ParmEnd.getCount("ADM_TYPE"); i++) {
			TParm temp = hl7ParmEnd.getRow(i);
			if (temp.getValue("TEMPORARY_FLG").length() == 0) {
				continue;
			}
			TParm parm1 = new TParm();
			parm1.setData("PAT_NAME",  temp.getValue("PAT_NAME"));
			parm1.setData("ADM_TYPE", temp.getValue("ADM_TYPE"));
			parm1.setData("FLG", 0);
			parm1.setData("CASE_NO", caseNo);
			parm1.setData("LAB_NO", temp.getValue("MED_APPLY_NO"));
			parm1.setData("CAT1_TYPE", temp.getValue("CAT1_TYPE"));
			parm1.setData("ORDER_NO", temp.getValue("RX_NO"));
			parm1.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			list.add(parm1);
		}
		// ���ýӿ�
		TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(list);
        writerLog2("hl7===="+caseNo+"===="+hl7Parm);
       

		xStream = new XStream();
		res.setIsSuccessed("0");//�Ƿ�ɹ�(0:�ɹ�,1:ʧ��)
		res.setErrorMsg("�ɷѳɹ�");//������Ϣ
		Result result = new Result();
		result.setInsurePay(df.format(insurepay-accountPay));//ҽ��֧��
		result.setAccountPay(df.format(accountPay));//�����˻�֧��
		result.setCashPay(df.format(cashpay));//����ʵ��֧��
		result.setReceiptCode(receiptNo);//�ɷѵ��ţ�����������������
		result.setComment("");//��ע
		res.setResult(result);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);
		outXml = xStream.toXML(res);
				
		TParm sParm = new TParm();
		sParm.setData("ORDER_NO", bodyP.getOrderNo());
		sParm.setData("STATUS", "1");
		sParm.setData("QUEUN_NO", "");
		sParm.setData("VISITADDRESS", "");
		sParm.setData("CASE_NO", caseNo);
		sParm.setData("RECEIPT_NO", receiptNo);
		sParm.setData("ACCOUNTPAY", df.format(accountPay));
		sParm.setData("CASHPAY", df.format(cashpay));
		sParm.setData("INSUREPAY", df.format(insurepay-accountPay));
		sParm.setData("PAYMENTTYPE", bodyP.getPaymentType());
		sParm.setData("CARD_NO", RegQETool.getInstance().getEktAppTrade(bodyP.getOrderNo()).getValue("CARD_NO", 0));
		
		RegQETool.getInstance().updateEktAppStatusParm(sParm);
		return outXml;
		
	}
	
	public String getEktPayDate(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><StartTime>2017-03-04 00:00:00</StartTime>" +
//		"<EndTime>2017-03-04 23:59:59</EndTime><patientId></patientId><cardID></cardID></BodyParams></Request>";
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		String outXml = "";
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		
		String sDate = bodyP.getStartTime().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		String eDate = bodyP.getEndTime().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		
//		String sDate = "20160525000000";
//		String eDate = "20160525235959";
		
		String mrNo = bodyP.getPatientId().trim();
		String cardNo = bodyP.getCardID().trim();
		
		
		TParm ektParm = RegQETool.getInstance().getEktPay(sDate, eDate,cardNo,mrNo);
		if(ektParm.getErrCode() < 0){
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(ektParm.getCount() < 0){
			xStream = new XStream();
			res.setIsSuccessed("0");
			res.setErrorMsg("û�в�ѯ����");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		for (int i = 0; i < ektParm.getCount(); i++) {
			result = new Result();
			result.setOrderNo(ektParm.getValue("ORDER_NO", i));
			result.setRegID(ektParm.getValue("CASE_NO", i));
			result.setCardId(ektParm.getValue("CARD_NO", i));
			result.setPatientId(ektParm.getValue("MR_NO", i));
			result.setPayMoney(df.format(ektParm.getDouble("AMT", i)));
			result.setCurrentBalance(df.format(ektParm.getDouble("CURRENT_BALANCE", i)));
			result.setBussinessFlg(ektParm.getValue("STATE", i));
			if(ektParm.getValue("BUSINESS_TYPE", i).equals("REG")){
				result.setBussType("1");
			}else{
				result.setBussType("2");
			}
			
			
			result.setOrderTime(ektParm.getValue("ORDER_TIME", i));
			if(ektParm.getValue("BUSINESS_APP_TYPE", i).equals("1") || 
					ektParm.getValue("BUSINESS_APP_TYPE", i).equals("2")
					){
				result.setPayType("01"); //ҽ�ƿ�
			}
			if(ektParm.getValue("BUSINESS_APP_TYPE", i).equals("8") || 
					ektParm.getValue("BUSINESS_APP_TYPE", i).equals("9")
					){
				result.setPayType("02"); //֧����
			}
			if(ektParm.getValue("BUSINESS_APP_TYPE", i).equals("10") || 
					ektParm.getValue("BUSINESS_APP_TYPE", i).equals("11")
					){
				result.setPayType("6"); //΢�� 
			}

			result.setBussTime(ektParm.getValue("ORDER_TIME", i));
			result.setBussinessCode(ektParm.getValue("TRADE_NO", i));
			results.add(result);

		}
		
		res.setResults(results);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		return outXml;
		
		
		
	}
	
	public String getEktAccntDetail(String inXml){
		
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><StartTime>2017-03-04 00:00:00</StartTime>" +
//		"<EndTime>2017-03-04 23:59:59</EndTime><CureCardNo></CureCardNo><HISPatientID></HISPatientID></BodyParams></Request>";
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		String outXml = "";
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		
		String sDate = bodyP.getStartTime().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		String eDate = bodyP.getEndTime().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		String cardNo = bodyP.getCureCardNo().trim();
		String mrNo = bodyP.gethISPatientID().trim();
		
		TParm ektParm = RegQETool.getInstance().getEktAccntDetail(cardNo,mrNo,sDate,eDate);
		
		if(ektParm.getErrCode() < 0 ){
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("��ѯʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		if(ektParm.getCount() < 0 ){
			xStream = new XStream();
			res.setIsSuccessed("0");
			res.setErrorMsg("û�в�ѯ����");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		for (int i = 0; i < ektParm.getCount(); i++) {
			result = new Result();
			result.setCardId(ektParm.getValue("CARD_NO", i));
			result.setPatientId(ektParm.getValue("MR_NO", i));
			result.setPayMoney(df.format(ektParm.getDouble("AMT", i)));
			result.setCurrentBalance(df.format(ektParm.getDouble("CURRENT_BALANCE", i)));
			result.setBussinessFlg(ektParm.getValue("STATE", i));
			if(ektParm.getValue("BUSINESS_TYPE", i).equals("REG")){
				result.setBussType("1");
			}else{
				result.setBussType("2");
			}

			result.setBussinessCode(ektParm.getValue("TRADE_NO", i));
			result.setOrderNo(ektParm.getValue("ORDER_NO", i));
			result.setBussTime(ektParm.getValue("ORDER_TIME", i));
			results.add(result);
			
		}
		

		res.setResults(results);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		return outXml;
	
		
	}
	
	/**
	 * 9.	�ɷ��б�ͬ��
	 * @param inXml
	 * @return
	 */
	public String getPaymentList(String inXml){
		
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><StartTime>2016-04-14 00:00:00</StartTime>" +
//		"<EndTime>2016-04-14 23:59:59</EndTime><PatientID></PatientID></BodyParams></Request>";
		writerLog2("getPaymentList--date--����-"+SystemTool.getInstance().getDate());
		
		inXml = RegQETool.getInstance().updateXml(inXml);
		XStream xStream = new XStream();
		String outXml = "";
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(inXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		
		String sDate = bodyP.getStartTime().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		String eDate = bodyP.getEndTime().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		String mrNo = bodyP.getPatientID().trim();
		
		TParm parmList = new TParm();
		parmList = RegQETool.getInstance().getRegRecp(sDate, eDate, mrNo, parmList);
		writerLog2("getPaymentList--date--getRegRecp-"+SystemTool.getInstance().getDate());
		parmList =RegQETool.getInstance().getOpbRecp(sDate, eDate, mrNo, parmList);
		writerLog2("getPaymentList--date--getOpbRecp-"+SystemTool.getInstance().getDate());
		
		if(parmList.getCount("MR_NO") < 0){
			xStream = new XStream();
			res.setIsSuccessed("0");
			res.setErrorMsg("û�в�ѯ����");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		res.setIsSuccessed("0");
		res.setErrorMsg("�ɹ�");
		List<Result> results = new ArrayList<Result>();
		Result result = new Result();
		
		for (int i = 0; i < parmList.getCount("MR_NO"); i++) {
			result = new Result();
			result.setIDNO(parmList.getValue("IDNO", i));
			result.setPatientID(parmList.getValue("MR_NO", i));
			result.setPatientName(parmList.getValue("PAT_NAME", i));
			result.setBusinessFrom(getBussiness(parmList.getValue("RE_SOURCE", i)));
			result.setInsureType(parmList.getValue("INS_TYPE", i));
			result.setYBCard(parmList.getValue("INSCARD_NO", i));
			result.setRegCode(parmList.getValue("REG_CODE", i));
			result.setReceiptCode(parmList.getValue("RECEIPT_CODE", i));
			result.setOpTime(parmList.getValue("BILL_DATE", i));
			result.setTotalMoney(df.format(parmList.getDouble("AR_AMT", i)));
			result.setCashPay(df.format(parmList.getDouble("CASH_PAY", i)));
			result.setInsurePay(df.format(parmList.getDouble("INS_PAY", i)));
			result.setAccountPay(df.format(parmList.getDouble("ACCOUNT_PAY", i)));
			result.setReceiptNO(parmList.getValue("RECEIPT_NO", i));
			result.setComment(parmList.getValue("COMMENT", i));
			results.add(result);
			
		}
		
		res.setResults(results);
		xStream.alias("Response", RegResponse.class);
		xStream.alias("Result", Result.class);// �滻����
		xStream.aliasField("Results", List.class, "Results");
		outXml = xStream.toXML(res);
		writerLog2("getPaymentList--date--��ȥ-"+SystemTool.getInstance().getDate());
		return outXml;
		
		
	}
	
	public String getOvertimeOrderStatus(String inXml){
//		inXml="<Request><HeaderParams></HeaderParams><BodyParams><OrderNo>170707000002001</OrderNo>" +
//				"<BussType></BussType><TerminalCode></TerminalCode><MachineIP></MachineIP></BodyParams></Request>";
				
				
				inXml = RegQETool.getInstance().updateXml(inXml);
				XStream xStream = new XStream();
				String outXml = "";
				xStream.alias("request", RegRequest.class);
				RegRequest request = (RegRequest) xStream.fromXML(inXml);
				BodyParams bodyP = request.getBodyParams();
				RegResponse res = new RegResponse();
				
				String type = "";
				if(bodyP.getBusinessType().equals("01")){
					type="REG";
				}else if(bodyP.getBusinessType().equals("02")){
					type="OPB";
				}
				
				TParm ektParm = RegQETool.getInstance().getEktAppStatus(bodyP.getOrderNo(),type);
				
				if(ektParm.getErrCode() < 0 ){
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("��ѯʧ��");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
				if(ektParm.getCount() < 0 ){
					xStream = new XStream();
					res.setIsSuccessed("1");
					res.setErrorMsg("û�в�ѯ����");
					res.setOverTimeStatus("0");
					xStream.alias("Response", RegResponse.class);
					outXml = xStream.toXML(res);
					return outXml;
				}
				
				res.setIsSuccessed("0");
				res.setErrorMsg("�ɹ�");
				res.setOverTimeStatus(ektParm.getValue("STATUS", 0));
				
				if(ektParm.getValue("STATUS", 0).equals("1")){
					Result result = new Result();
					result.setQueueNo(ektParm.getValue("QUEUN_NO", 0));//�ŶӺ�  
					result.setRegID(ektParm.getValue("CASE_NO", 0));//�Һ�ID 
					result.setInsurePay(ektParm.getValue("INSUREPAY", 0));//ҽ��֧��
					result.setAccountPay(ektParm.getValue("ACCOUNTPAY", 0));//�����˻�֧��
					result.setCashPay(ektParm.getValue("CASHPAY", 0));//����ʵ��֧��		
					result.setVisitAddress(ektParm.getValue("VISITADDRESS", 0));//�����ַ
					result.setReceiptCode(ektParm.getValue("RECEIPT_NO", 0));
					result.setCardId(ektParm.getValue("CARD_NO", 0));
					result.setPayType(ektParm.getValue("PAYMENTTYPE", 0)); //01 ҽ�ƿ� 02֧���� 
					res.setResult(result);
					xStream.alias("Response", RegResponse.class);
					xStream.alias("Result", Result.class);
				}else{
					xStream.alias("Response", RegResponse.class);
				}

				outXml = xStream.toXML(res);
				return outXml;
				
	}



	/**
	 * ̩�ĹҺ��Ŷӽк�
	 * 
	 * @param type
	 *            String
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String callNo(String type, String caseNo) {
		TParm inParm = new TParm();
		// writerLog2("========caseNo=========="+caseNo);
		String sql = "SELECT CASE_NO, A.MR_NO,A.CLINICROOM_NO,A.ADM_TYPE,A.QUE_NO,A.REGION_CODE,";
		sql += "TO_CHAR (ADM_DATE, 'YYYY-MM-DD') ADM_DATE,A.SESSION_CODE,";
		sql += "A.CLINICAREA_CODE, A.CLINICROOM_NO, QUE_NO, REG_ADM_TIME,";
		sql += "B.DEPT_CHN_DESC, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE,";
		sql += "VISIT_CODE, REGMETHOD_CODE, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE,";
		sql += "C.USER_NAME,D.CLINICTYPE_DESC, F.CLINICROOM_DESC, E.PAT_NAME,";
		sql += "TO_CHAR (E.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE, G.CHN_DESC SEX,H.SESSION_DESC";
		sql += " FROM REG_PATADM A,";
		sql += "SYS_DEPT B,";
		sql += "SYS_OPERATOR C,";
		sql += "REG_CLINICTYPE D,";
		sql += "SYS_PATINFO E,";
		sql += "REG_CLINICROOM F,";
		sql += "(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX') G,";
		sql += "REG_SESSION H";
		sql += " WHERE CASE_NO = '" + caseNo + "'";
		sql += " AND A.DEPT_CODE = B.DEPT_CODE(+)";
		sql += " AND A.DR_CODE = C.USER_ID(+)";
		sql += " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE(+)";
		sql += " AND A.MR_NO = E.MR_NO(+)";
		sql += " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+)";
		sql += " AND E.SEX_CODE = G.ID";
		sql += " AND A.SESSION_CODE=H.SESSION_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// �Һ�����
		String sendString = result.getValue("ADM_DATE", 0) + "|";
		// �������
		sendString += result.getValue("DEPT_CHN_DESC", 0) + "|";
		// ҽʦ����
		sendString += result.getValue("DR_CODE", 0) + "|";
		// ҽʦ����
		sendString += result.getValue("USER_NAME", 0) + "|";
		// �ű�
		sendString += result.getValue("CLINICTYPE_DESC", 0) + "|";

		// ���
		sendString += result.getValue("CLINICROOM_DESC", 0) + "|";

		// ���߲�����
		sendString += result.getValue("MR_NO", 0) + "|";

		// ��������
		sendString += result.getValue("PAT_NAME", 0) + "|";
		// �����Ա�

		sendString += result.getValue("SEX", 0) + "|";
		// ��������
		sendString += result.getValue("BIRTH_DATE", 0) + "|";

		// �������
		sendString += result.getValue("QUE_NO", 0) + "|";

		// writerLog2("==adm date=="+result.getValue("ADM_DATE",0));

		String noSql = "SELECT QUE_NO,MAX_QUE FROM REG_SCHDAY";
		noSql += " WHERE REGION_CODE ='" + result.getValue("REGION_CODE", 0)
				+ "'";
		noSql += " AND ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		noSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		noSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		noSql += " AND CLINICROOM_NO ='" + result.getValue("CLINICROOM_NO", 0)
				+ "'";
		//
		TParm noParm = new TParm(TJDODBTool.getInstance().select(noSql));
		// writerLog2("===noSql=="+noSql);
		// �޹�����
		sendString += noParm.getValue("MAX_QUE", 0) + "|";
		// �ѹ����� noParm.getValue("QUE_NO", 0)+ "|";
		sendString += (Integer.valueOf(noParm.getValue("QUE_NO", 0)) - 1) + "|";
		// this.messageBox("SESSION_CODE"+((TComboBox)
		// this.getComponent("SESSION_CODE")).getSelectedText());
		// ʱ���
		sendString += result.getValue("SESSION_DESC", 0);

		String timeSql = "SELECT START_TIME FROM REG_CLINICQUE";
		timeSql += " WHERE ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		timeSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		timeSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		timeSql += " AND CLINICROOM_NO ='"
				+ result.getValue("CLINICROOM_NO", 0) + "'";
		timeSql += " AND QUE_NO ='" + result.getValue("QUE_NO", 0) + "'";
		TParm startTimeParm = new TParm(TJDODBTool.getInstance()
				.select(timeSql));
		// writerLog2("===timeSql==="+timeSql);

		// �˹ҽк�
		if ("UNREG".equals(type)) {
			// ԤԼ����

			inParm.setData("msg", sendString);
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + Que_No.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 **/
			TIOM_AppServer.executeAction("action.device.CallNoAction",
					"doUNReg", inParm);
			// this.messageBox("�˹ҽк�!");

		} else if ("REG".equals(type)) {
			// writerLog2("adm time===="+this.reg.getRegAdmTime());
			sendString += "|";
			// ԤԼ����
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				// sendString += result.getValue("ADM_DATE", 0).replaceAll("-",
				// "").substring(
				// 0, 8)+startTimeParm.getValue("START_TIME", 0) + "00";
				// writerLog2("========ԤԼsendString=========="+sendString);
				sendString += startTimeParm.getValue("START_TIME", 0) + "00";
			} else {
				sendString += "";
			}
			// 2012-04-02|�ڷ��ڴ�л��|000875|�����|����ҽʦ|06����|000000001009|������|Ů|1936-01-05|2|60|2|����|
			inParm.setData("msg", sendString);
			// this.messageBox("�ҺŽк�!");
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + QueNo.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 * writerLog2("Reg_sendString--->" + sendString);
			 **/

			inParm.setData("msg", sendString);

			TSocket socket = new TSocket("127.0.0.1", 8080, "web");
			TIOM_AppServer.executeAction(socket,"action.device.CallNoAction",
					"doReg", inParm);

		}

		return "true";

	}
	
	
	/**
	 * �����õ���־�ļ�-2
	 * @param msg
	 * @author zhangp
	 */
	public static void writerLog2(String msg) {
		msg = getNowDate()+"::::"+msg;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String name = "QeAppLogs";
		name +=format.format(new Date());
		File f = new File("D:\\JavaHisQeApp\\logs\\"+name+".log");
		BufferedWriter out = null;
		try {
			if (!f.exists()) {
				f.createNewFile();// ���SPC.log�����ڣ��򴴽�һ�����ļ�
			}
			out = new BufferedWriter(new FileWriter(f, true));// ����true��ʾ�����׷�ӵ��ļ����ݵ�ĩβ��������ԭ��������
			out.write(msg);
			out.newLine(); // ����
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getNowDate(){
		SimpleDateFormat  df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		String dd = df.format(new Date());
		return dd;
	}
	/**
	 * ҽ���˷�
	 * @param
	 */
	public String onBackIns(String InXml){
		String outXml="";
//		InXml="<?xml version=\"1.0\"?><Request><HeaderParams></HeaderParams><BodyParams>"
//				+ "<InsureSequenceNo>170504000011|MT05511705043931387</InsureSequenceNo>"
//				+ "<RegID>170504000011</RegID><PatientID>000000309651</PatientID><BusinessFrom>02</BusinessFrom>"
//				+ "<TerminalCode>APP</TerminalCode><BusinessType>1</BusinessType></BodyParams></Request>";
		
		InXml = RegQETool.getInstance().updateXml(InXml);
		XStream xStream = new XStream();
		xStream.alias("request", RegRequest.class);
		RegRequest request = (RegRequest) xStream.fromXML(InXml);
		BodyParams bodyP = request.getBodyParams();
		RegResponse res = new RegResponse();
		TParm regionParm = null;// ���ҽ���������
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������	
		TParm insParm = new TParm();
		TParm resultData = new TParm();
		TParm insData = new TParm();
		String insType = "";
		String tradeNo = "";//���׺�
		String insureSequenceNo = bodyP.getInsureSequenceNo();
		String [] Array=new String [0];
		if(insureSequenceNo.length()>0)
			Array=insureSequenceNo.split("\\|");			
		String caseNo = Array[0];//�Һ�HISΨһ��	
		String confirmNo = Array[1];//ҽ��ȷ������
		writerLog2("caseNo=========="+caseNo);
		writerLog2("confirmNo=========="+confirmNo);
		insParm.setData("CONFIRM_NO",confirmNo);			
		// ��ȡҽ������
		insData = RegQETool.getInstance().getInsBackData(caseNo,confirmNo);
		writerLog2("ҽ������==="+insData);
		if(insData.getCount("CASE_NO")<=0){
			res.setIsSuccessed("1");
			res.setErrorMsg("�����ڳ�������,����ҽ���˷�");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;	
		}
		TParm ektTradeParm = RegQETool.getInstance().getEktTrade(tradeNo);
		if(ektTradeParm.getCount() < 0){
			xStream = new XStream();
			res.setIsSuccessed("1");
			res.setErrorMsg("δȡ�þ�ҽ�ƿ��ۿ���Ϣ,����ҽ���˷�");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;
		}
		
		if(insData.getValue("INS_CROWD_TYPE",0).equals("1")&&
		   insData.getValue("INS_PAT_TYPE",0).equals("1"))
			insType = "1";
		if(insData.getValue("INS_CROWD_TYPE",0).equals("1")&&
		   insData.getValue("INS_PAT_TYPE",0).equals("2"))
			insType = "2";
		if(insData.getValue("INS_CROWD_TYPE",0).equals("2")&&
		   insData.getValue("INS_PAT_TYPE",0).equals("2"))
			insType = "3";
		insParm.setData("CASE_NO", caseNo);// �����
		insParm.setData("INS_TYPE", insType);//1.��ְ ��ͨ 2.��ְ���� 3.�Ǿ�����					
		insParm.setData("CONFIRM_NO", confirmNo);// ҽ�������
		if(bodyP.getBusinessType().equals("1")){//�Һ�
			insParm.setData("RECP_TYPE", "REG");
			insParm.setData("UNRECP_TYPE", "REGT");// �˷�����
		}
		else if(bodyP.getBusinessType().equals("2")){//�շ�
			insParm.setData("RECP_TYPE", "OPB");
			insParm.setData("UNRECP_TYPE", "OPBT");// �˷�����
		}
		insParm.setData("OPT_USER", "QeApp");
		insParm.setData("OPT_TERM", "0.0.0.0");
		insParm.setData("INV_NO", "");// Ʊ�ݺ�
		TParm regInfo = RegQETool.getInstance().getRegInfo(caseNo);
		writerLog2("onBackIns=====regInfo=========="+regInfo);
		insParm.setData("PAT_TYPE", regInfo.getValue("CTZ1_CODE", 0));// ���
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));//ҽ���������
		writerLog2("onBackIns=====insParm=========="+insParm);
		resultData = INSTJReg.getInstance().insResetCommFunction(
				insParm.getData());//ȡ�����ý������
		if (resultData.getErrCode() < 0) {
			res.setIsSuccessed("1");
			res.setErrorMsg("ҽ�����׳���ʧ��");
			xStream.alias("Response", RegResponse.class);
			outXml = xStream.toXML(res);
			return outXml;			
		} 
		xStream = new XStream();
		res.setIsSuccessed("0");//�Ƿ�ɹ�(0:�ɹ�,1:ʧ��)
		res.setErrorMsg("ҽ�������ɹ�");//��Ϣ
		xStream.alias("Response", RegResponse.class);
		outXml = xStream.toXML(res);	
		return outXml;
	}
	
	

}
