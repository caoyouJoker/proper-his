package jdo.sum.ws;

import com.dongyang.Service.DataService;
import com.dongyang.config.TConfig;
import com.dongyang.config.TConfigParm;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_FileServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

import jdo.lis.LISJdo;
import jdo.lis.NISJdo;
import jdo.sys.Operator;

import com.dongyang.util.StringTool;
import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.BpelUtil;
import com.javahis.util.StringUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.jws.WebService;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.net.URL;

/**
 * <p>
 * Title: nis�ӿڷ���
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author liuyl
 * @version 1.0
 */
@WebService
@SuppressWarnings({ "unused", "unchecked" })
public class SUMPatientTemperatureServiceImpl implements
		SUMPatientTemperatureService {
	private TParm parm;
	private TConfigParm parmT = new TConfigParm();
	TParm patInfo = new TParm();// ������Ϣ
	private String hl7message;

	/**
	 * �õ�NIS�ط��ļ�ȫ������
	 */
	public String mainNISData(String hl7message) {
		this.hl7message = hl7message;
//		System.out.println("123456789::" + hl7message);
		if(hl7message.equals("")){
			return "û�л�ȡ��HL7��";
		}
		this.ControlNISProsess();
		return "����ɹ�";
	}

	/**
	 * NIS���̿���
	 */
	@SuppressWarnings("rawtypes")
	private void ControlNISProsess() {
		System.out.println("1111111������HL7������" + hl7message);
		// HL7�ļ�������; ���ж���ȡ����
		String fileData[] = StringTool.parseLine(this.hl7message, "\n");

		for(int i = 0; i < fileData.length; i++){
			System.out.println("2222222��" + i + "�У�����" + fileData[i]);
		}
		
//		Map map = new HashMap();
//		TConfig config = new TConfig("");
		
		//���������·��
//		URL url = this.getClass().getProtectionDomain().getCodeSource()
//				.getLocation();
//		
//		System.out.println("333333333333"+ url);
//		String path = url.getPath();
		
//		System.out.println("3333333���������·��" + url.getPath());
		
		
		//classes�ַ�����path�ַ����������ֵ�λ��
//		int index = path.lastIndexOf("classes");
//		path = path.substring(1, index < 0 ? path.length() : index);
//
//		 System.out.println("444444��ý�ȡ���·��Ϊ::::::" + path);

//		try {//��ȡ�������ļ�
			TConfig config = TConfig
					.getConfig("WEB-INF\\config\\system\\hl7config.x");
//			System.out.println("616161616161616:::::::" + config);
			String djb = config.getString("","TARGETARRAYINDEX");
//			System.out.println("djbdjbdjbdjb::::::" + djb);
//			config.load(new BufferedReader(new FileReader(new File(path
//					+ "\\config\\system\\hl7config.x"))), map);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		 System.out.println("555555�õ�hl7config.x�ļ��ŵ�map��::::::" + djb);
		 
		 //��;Ϊ�ָ�����ȡhl7config�е��ַ�MSH:INMEUF:2;MSH:OUTMEUF:4;
		String targetArrayIndex[] =djb.split(";");
		for(int i=0 ; i<targetArrayIndex.length ; i++){
			System.out.println("666666::"+targetArrayIndex[i]);
		}
		// if(true)
		// return;

		 System.out.println("changdu����:" + fileData.length);
		// ��������
		TParm parm = new TParm();
		for (int i = 0; i < fileData.length; i++) {
			if (fileData[i].length() == 0)
				continue;
			// �е�һ��ֵ��
			String massagesData = StringTool.parseLine(fileData[i], "|")[0];
			if (massagesData.length() == 0 || massagesData == null)
				continue;
			for (int j = 0; j < targetArrayIndex.length; j++) {
				// MSH                                  �ֵ��һ���ֶ�
				if (massagesData.equals(targetArrayIndex[j].split(":")[0])) {
					// hl7�ļ������ݣ��ѻ��HL7�ļ�fileData����ÿ�а�|�ָ�
					String temp[] = StringTool.parseLine(fileData[i], "|");
					// 4
					int arrLen = StringTool.getInt(targetArrayIndex[j]
							.split(":")[2]);
					// MSH:MEUF:4
					String tempData[] = targetArrayIndex[j].split(":");
					// Ŀ��������Hl7�ļ���������
					if (arrLen > temp.length - 1) {
						parm.addData(targetArrayIndex[j].split(":")[1], "");
						continue;
					}
					// �����д�С3��
					if (tempData.length > 3) {
						String dataStr = temp[arrLen];
						String dataS[] = dataStr.split(targetArrayIndex[j]
								.split(":")[3].equals("^") ? "\\^"
								: targetArrayIndex[j].split(":")[3]);
						parm.addData(targetArrayIndex[j].split(":")[1],
								dataS[StringTool.getInt(targetArrayIndex[j]
										.split(":")[4])]);
						continue;
					}
					String returnData = temp[arrLen];
					parm.addData(targetArrayIndex[j].split(":")[1], returnData);
					 System.out.println("666666MSH::::::" + parm);
				}
			}

		}
		TParm r1 = new TParm();
		TParm r2 = new TParm();
		System.out.println("zzzzzzzzzzzz666666MSH::::::" + parm);
		String Date = parm.getValue("EXAMINE_DATE", 0);// ���ʱ��
		System.out.println("11111111111111111111111::" + Date);
		String examineDate = Date.substring(0, 8);// ��ȡʱ��ֶ� ��/��/��
		r1.setData("EXAMINE_DATE", examineDate);// ������ʱ���ȡ
		r2.setData("EXAMINE_DATE", examineDate);// ϸ����ʱ���ȡ
		String[] a1 = parm.getValue("JCDM").replace("[", "").replace("]", "")
				.replace(" ", "").split(",");
		String[] a2 = parm.getValue("PROJECTEND").replace("[", "")
				.replace("]", "").replace(" ", "").split(",");
		// ����OBX����
		for (int i = 0; i < a1.length; i++) {
			System.out.println("key:::" + a1[i]);
			System.out.println("value:::" + a2[i]);
			r1.setData(
					"ADM_TYPE",
					parm.getValue("ADM_TYPE", 0) == null ? -1 : parm
							.getValue("ADM_TYPE").replace("[", "")
							.replace("]", "").replace(" ", ""));
			r1.setData("CASE_NO", parm.getValue("CASE_NO", 0) == null ? -1
					: parm.getValue("CASE_NO").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("IPD_NO", parm.getValue("IPD_NO", 0) == null ? -1 : parm
					.getValue("IPD_NO").replace("[", "").replace("]", "")
					.replace(" ", ""));
			r1.setData("MR_NO", parm.getValue("MR_NO", 0) == null ? -1 : parm
					.getValue("MR_NO").replace("[", "").replace("]", "")
					.replace(" ", ""));

			r1.setData("MCFLG", parm.getValue("MCFLG", 0) == null ? -1 : parm
					.getValue("MCFLG").replace("[", "").replace("]", "")
					.replace(" ", ""));
			r1.setData("INHOSPITALDAYS",
					parm.getValue("INHOSPITALDAYS", 0) == null ? -1 : parm
							.getValue("INHOSPITALDAYS").replace("[", "")
							.replace("]", "").replace(" ", ""));
			r1.setData(
					"ECTTIMES",
					parm.getValue("ECTTIMES", 0) == null ? -1 : parm
							.getValue("ECTTIMES").replace("[", "")
							.replace("]", "").replace(" ", ""));
			r1.setData(
					"HOURSOFSLEEP",
					parm.getValue("HOURSOFSLEEP", 0) == null ? -1 : parm
							.getValue("HOURSOFSLEEP").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("MINUTESOFSLEEP",
					parm.getValue("MINUTESOFSLEEP", 0) == null ? -1 : parm
							.getValue("MINUTESOFSLEEP").replace("[", "")
							.replace("]", "").replace(" ", ""));
			if ("������".equals(a1[i])) {
				r1.setData("STOOL", a2[i]);
				System.out.println("77777777777777������zhi����������" + r1.getValue("STOOL"));
			}

			r1.setData("SPECIALSTOOLNOTE",
					parm.getValue("SPECIALSTOOLNOTE", 0) == null ? -1 : parm
							.getValue("SPECIALSTOOLNOTE").replace("[", "")
							.replace("]", "").replace(" ", ""));

			if ("������".equals(a1[i])) {
				r1.setData("INTAKEFLUIDQTY", a2[i]);
				System.out.println("77777777777777������zhi����������" + r1.getValue("INTAKEFLUIDQTY"));
			}
			r1.setData("INTAKEDIETQTY",
					parm.getValue("INTAKEDIETQTY", 0) == null ? -1 : parm
							.getValue("INTAKEDIETQTY").replace("[", "")
							.replace("]", "").replace(" ", ""));
			if ("�ܳ���".equals(a1[i])) {
				r1.setData("OUTPUTURINEQTY", a2[i]);
				System.out.println("77777777777777�ܳ���zhi����������" + r1.getValue("OUTPUTURINEQTY"));
			}
			if ("������".equals(a1[i])) {
				r1.setData("OUTPUTDRAINQTY", a2[i]);
				System.out.println("77777777777777������zhi����������" + r1.getValue("OUTPUTDRAINQTY"));
			}
			r1.setData("OUTPUTOTHERQTY",
					parm.getValue("OUTPUTOTHERQTY", 0) == null ? -1 : parm
							.getValue("OUTPUTOTHERQTY").replace("[", "")
							.replace("]", "").replace(" ", ""));
			r1.setData("BATH", parm.getValue("BATH", 0) == null ? -1 : parm
					.getValue("BATH").replace("[", "").replace("]", "")
					.replace(" ", ""));
			r1.setData(
					"GUESTKIND",
					parm.getValue("GUESTKIND", 0) == null ? -1 : parm
							.getValue("GUESTKIND").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData(
					"STAYOUTSIDE",
					parm.getValue("STAYOUTSIDE", 0) == null ? -1 : parm
							.getValue("STAYOUTSIDE").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("LEAVE", parm.getValue("LEAVE", 0) == null ? -1 : parm
					.getValue("LEAVE").replace("[", "").replace("]", "")
					.replace(" ", ""));

			r1.setData("LEAVEREASONCODE",
					parm.getValue("LEAVEREASONCODE", 0) == null ? -1 : parm
							.getValue("LEAVEREASONCODE").replace("[", "")
							.replace("]", "").replace(" ", ""));
			if ("���".equals(a1[i])) {
				r1.setData("HEIGHT", a2[i]);
				System.out.println("77777777777777���zhi����������" + r1.getValue("HEIGHT"));
			}
			if ("����".equals(a1[i])) {
				r1.setData("WEIGHT", a2[i]);
				System.out.println("77777777777777����zhi����������" + r1.getValue("WEIGHT"));
			}
			r1.setData("NOTE", parm.getValue("NOTE", 0) == null ? -1 : parm
					.getValue("NOTE").replace("[", "").replace("]", "")
					.replace(" ", ""));

			r1.setData(
					"STATUS_CODE",
					parm.getValue("STATUS_CODE", 0) == null ? -1 : parm
							.getValue("STATUS_CODE").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData(
					"DISPOSAL_FLG",
					parm.getValue("DISPOSAL_FLG", 0) == null ? -1 : parm
							.getValue("DISPOSAL_FLG").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("DISPOSAL_REASON",
					parm.getValue("DISPOSAL_REASON", 0) == null ? -1 : parm
							.getValue("DISPOSAL_REASON").replace("[", "")
							.replace("]", "").replace(" ", ""));
			r1.setData("USER_DEFINE_1",
					parm.getValue("USER_DEFINE_1", 0) == null ? -1 : parm
							.getValue("USER_DEFINE_1").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("USER_DEFINE_1_VALUE",
					parm.getValue("USER_DEFINE_1_VALUE", 0) == null ? -1 : parm
							.getValue("USER_DEFINE_1_VALUE").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("USER_DEFINE_2",
					parm.getValue("USER_DEFINE_2", 0) == null ? -1 : parm
							.getValue("USER_DEFINE_2").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("USER_DEFINE_2_VALUE",
					parm.getValue("USER_DEFINE_2_VALUE", 0) == null ? -1 : parm
							.getValue("USER_DEFINE_2_VALUE").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("USER_DEFINE_3",
					parm.getValue("USER_DEFINE_3", 0) == null ? -1 : parm
							.getValue("USER_DEFINE_3").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r1.setData("USER_DEFINE_3_VALUE",
					parm.getValue("USER_DEFINE_3_VALUE", 0) == null ? -1 : parm
							.getValue("USER_DEFINE_3_VALUE").replace("[", "")
							.replace("]", "").replace(" ", ""));
			r1.setData(
					"AUTO_STOOL",
					parm.getValue("AUTO_STOOL", 0) == null ? -1 : parm
							.getValue("AUTO_STOOL").replace("[", "")
							.replace("]", "").replace(" ", ""));
			if ("�೦".equals(a1[i])) {
				r1.setData("ENEMA", a2[i]);
				System.out.println("77777777777777�೦zhi����������" + r1.getValue("ENEMA"));
			}

			r1.setData(
					"DRAINAGE",
					parm.getValue("DRAINAGE", 0) == null ? -1 : parm
							.getValue("DRAINAGE").replace("[", "")
							.replace("]", "").replace(" ", ""));
			r1.setData(
					"OPE_DAYS2",
					parm.getValue("OPE_DAYS2", 0) == null ? -1 : parm
							.getValue("OPE_DAYS2").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r2.setData("CASE_NO", parm.getValue("CASE_NO", 0) == null ? -1
					: parm.getValue("CASE_NO").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r2.setData(
					"ADM_TYPE",
					parm.getValue("ADM_TYPE", 0) == null ? -1 : parm
							.getValue("ADM_TYPE").replace("[", "")
							.replace("]", "").replace(" ", ""));
			if ("������".equals(a1[i])) {
				r2.setData("PHYSIATRICS", a2[i]);
				System.out.println("77777777777777������zhi����������" + r2.getValue("PHYSIATRICS"));
			}
			r2.setData("RECTIME", parm.getValue("RECTIME", 0) == null ? -1
					: parm.getValue("RECTIME").replace("[", "")
							.replace("]", "").replace(" ", ""));
			r2.setData(
					"SPCCONDCODE",
					parm.getValue("SPCCONDCODE", 0) == null ? -1 : parm
							.getValue("SPCCONDCODE").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r2.setData("TMPTRKINDCODE",
					parm.getValue("TMPTRKINDCODE", 0) == null ? -1 : parm
							.getValue("TMPTRKINDCODE").replace("[", "")
							.replace("]", "").replace(" ", ""));
			if ("����".equals(a1[i])) {
				r2.setData("TEMPERATURE", a2[i]);
				System.out.println("77777777777777����zhi��������" + r2.getValue("TEMPERATURE"));
			}
			if ("����".equals(a1[i])) {
				r2.setData("PLUSE", a2[i]);
				System.out.println("77777777777777����zhi����������" + r2.getValue("PLUSE"));
			}
			if ("����".equals(a1[i])) {
				r2.setData("RESPIRE", a2[i]);
				System.out.println("77777777777777����zhi����������" + r2.getValue("RESPIRE"));
			}

			if ("����ѹ".equals(a1[i])) {
				r2.setData("SYSTOLICPRESSURE", a2[i]);
				System.out
						.println("77777777777777����ѹzhi����������" + r2.getValue("SYSTOLICPRESSURE"));
			}
			if ("����ѹ".equals(a1[i])) {
				r2.setData("DIASTOLICPRESSURE", a2[i]);
				System.out.println("77777777777777����ѹzhi����������"
						+ r2.getValue("DIASTOLICPRESSURE"));
			}
			r2.setData("NOTPRREASONCODE",
					parm.getValue("NOTPRREASONCODE", 0) == null ? -1 : parm
							.getValue("NOTPRREASONCODE").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r2.setData("PTMOVECATECODE",
					parm.getValue("PTMOVECATECODE", 0) == null ? -1 : parm
							.getValue("PTMOVECATECODE").replace("[", "")
							.replace("]", "").replace(" ", ""));

			r2.setData("PTMOVECATEDESC",
					parm.getValue("PTMOVECATEDESC", 0) == null ? -1 : parm
							.getValue("PTMOVECATEDESC").replace("[", "")
							.replace("]", "").replace(" ", ""));
			if ("����".equals(a1[i])) {
				r2.setData("HEART_RATE", a2[i]);
				System.out.println("77777777777777����zhi����������" + r2.getValue("HEART_RATE"));
			}
		}
		// ��ȡʱ��
		String time = parm.getValue("TIME", 0);
		System.out.println("TIMETIMETIMETIMETIMETIMETIMETIMETIME11111111111:::::::" + time);
		String teptime = time.substring(8, 10);

		int djba = 0;
		try {
			djba = Integer.parseInt(teptime);
		} catch (NumberFormatException e) {
			System.out.println("���µ�NIS�ӿڻ�ü��ʱ�����");
		    e.printStackTrace();
		}
		
		System.out.println("ʱ��" + djba);
		String examine = "5";
		if (0 <= djba & djba < 2) {
			examine = "0";
		}else if (2 <= djba & djba < 6) {
			examine = "1";
		}else if (6 <= djba & djba < 10) {
			examine = "2";
		}else if (10 <= djba & djba < 14) {
			examine = "3";
		}else if (14 <= djba & djba < 18) {
			examine = "4";
		}else if (22 >= djba) {
			examine = "5";
		}
		System.out.println("ʱ����ʾ555555��������" + examine + "  djba::::::" + djba);
//		if ("02".equals(teptime)) { //ԭ����
//			examine = "0";
//		}
//		if ("06".equals(teptime)) {
//			examine = "1";
//		}
//		if ("10".equals(teptime)) {
//			examine = "2";
//		}
//		if ("14".equals(teptime)) {
//			examine = "3";
//		}
//		if ("18".equals(teptime)) {
//			examine = "4";
//		}
//		if ("22".equals(teptime)) {
//			examine = "5";
//		}
		r2.setData("EXAMINESESSION", examine);
		// ��Ϣ״̬(������I,���£�U)
		String status = parm.getValue("STATUSTYPE", 0);
		System.out.println("��Ϣ״̬(������I,���£�U)::" + status);
		// �����ڣ������
		// ���ʱ���ѯ
		String vitalDate = parm.getValue("EXAMINE_DATE", 0).substring(0, 8);
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-HH-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyHHdd");
		try {
			Date = formatter1.format(formatter2.parse(vitalDate));
			Date = Date + " 00:00:00";
			System.out.println("���ʱ���ѯ::" + Date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Timestamp t1 = Timestamp.valueOf(Date);
		Timestamp t2 = null;
		StringBuilder dayStr = new StringBuilder();
		// �������ڲ�ѯ
		String sql = "SELECT OP_DATE FROM OPE_OPBOOK "
				+ "WHERE OP_DATE IS NOT NULL "
				+ "AND CANCEL_FLG != 'Y'  "
				+ "AND CASE_NO = '"
				+ r1.getValue("CASE_NO")
				+ "'"
				+ "AND STATE >= (CASE  WHEN TYPE_CODE = '1' THEN '1' WHEN TYPE_CODE = '2' THEN '7' END) "
				+ "ORDER BY OP_DATE DESC";
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < sqlParm.getCount("OP_DATE"); i++) {
			t2 = Timestamp.valueOf(sqlParm.getValue("OP_DATE", i).substring(0,
					10)
					+ " 00:00:00");
			int day = StringTool.getDateDiffer(t1, t2);
			if (day > 0 && day <= 14) {
				dayStr.append(day + "/");
			}
			 System.out.println("�������ڲ�ѯday::::"+day);
		}
//		 System.out.println("dayStr::::"+dayStr);
//		 System.out.println("t1::::"+t1);
//		 System.out.println("t2::::"+t2);
		String str = dayStr.toString();
		TParm ope = new TParm();
		if (!StringUtils.isEmpty(str.toString())) {
			String opedays = str.substring(0, str.lastIndexOf("/"));
			r1.setData("OPE_DAYS", opedays);
			System.out.println(opedays);
		}
		// ��Ϣ�����������ݿ�
		if ("I".equals(status)) {
			for (int i = 0; i < 6; i++) {// ʱ����6��
				TParm result = new TParm();
				result.addData("SYSTEM", "COLUMNS", "EXAMINESESSION");
				result.addData("SYSTEM", "COLUMNS", "EXAMINE_DATE");
				result.addData("SYSTEM", "COLUMNS", "ADM_TYPE");
				result.addData("SYSTEM", "COLUMNS", "CASE_NO");

				result.setData("EXAMINESESSION", i);
//				System.out.println("����ʱ���������" + result.getValue("EXAMINESESSION"));
				result.setData("EXAMINE_DATE", examineDate);
				result.setData("ADM_TYPE", r2.getValue("ADM_TYPE"));
				result.setData("CASE_NO", r2.getValue("CASE_NO"));
				// ����ϸ�������Ϣ��6��
				 TParm b =
				 NISJdo.getInstance().getInsertNewVTSNTPRDTL(result);
			}
			// �������������Ϣ��1��
			 TParm a = NISJdo.getInstance().getInsertNewVITALSIGN(r1);
			 TParm b1 = NISJdo.getInstance().getUpdateNewVTSNTPRDTL(r2);
		} else {
			 TParm a11  = NISJdo.getInstance().getUpdateNewVITALSIGN(r1);
			 TParm b1 = NISJdo.getInstance().getUpdateNewVTSNTPRDTL(r2);
		}
	}
}
