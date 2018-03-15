package com.javahis.ui.odi;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;

import com.javahis.util.OdiUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;


/**
 * <p>
 * Title: 崻�����
 * </p>
 *
 * <p>
 * Description: 崻�����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 *
 * <p>
 * Company: ProperSoft
 * </p>
 *
 * @author wangjc
 * @version 1.0
 */
public class OPDBackupDataToPDFControl extends PdfPageEventHelper {

	// ST��ͷ
	// �� �� ҽ������ ���� ��λ �÷� Ƶ�� ִ�п��� ����ʱ�� ҽ����ע ��ʿ��ע
	private final String[] tableHeaderST = { "��", "��", "ҽ������", "����",
			"��λ", "�÷�", "Ƶ��", "ִ�п���", "����ʱ��", "ҽ����ע", "��ʿ��ע" };
	// UT��ͷ
	// �� �� ҽ������ ���� ��λ Ƶ�� �÷� �� �� ҽ����ע ִ�п��� ����ʱ��
	private final String[] tableHeaderUT = { "��", "��", "ҽ������", "����",
			"��λ", "Ƶ��", "�÷�", "��", "��", "ҽ����ע", "ִ�п���", "����ʱ��" };
	// OP��ͷ
	// �� �� ҽ������ ҽ����ע ���� ��λ �÷� Ƶ�� ִ�п��� ����ʱ�� ��ʿ��ע
	private final String[] tableHeaderOP = { "��", "��", "ҽ������", "ҽ����ע",
			"����", "��λ", "�÷�", "Ƶ��", "ִ�п���", "����ʱ��", "��ʿ��ע" };
	// CK��ͷ
	// ��� ������Ŀ Ӣ������ ��� ״̬ ��λ �ο�ֵ
	private final String[] tableHeaderCK = { "ִ��ʱ��","������Ŀ", "ϸ������", "����ֵ",
			"��λ", "��׼����", "��׼����" };
	// ��������
	private final float spacing = (float) 0.3;

	// ��������
	private final int padding = 2;

	private PdfTemplate tpl;
	private BaseFont bf;

	private String mrNo = "";
	private String patName = "";
	private String bedNo = "";

	private String caseNo = "";

	public OPDBackupDataToPDFControl(String case_no){
		this.caseNo = case_no;
	}

	public OPDBackupDataToPDFControl(){

	}

	public void backupDataToPDF(String case_no, TControl tc) {
		//		String relativelyPath=System.getProperty("java.class.path"); 

		//		case_no = "150529000007";
		//		case_no = "150615000002";
		// ������Pdf�ĵ�50, 50, 50,50�������¾���
		Document document = new Document(PageSize.A4, 50, 50, 55, 50);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();

		try {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -5);
			//������Ϣ
			String basicDataSql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,"
					+ "C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,B.CTZ1_CODE,"
					+ "B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
					+ "B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,"
					+ "B.VS_DR_CODE,B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,"
					+ "C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,"
					+ "B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,"
					+ "B.SERVICE_LEVEL,B.BILL_STATUS,B.DISE_CODE,B.SCHD_CODE"
					+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E"
					+ " WHERE   A.BED_NO = B.BED_NO(+)"
					+ " AND A.CASE_NO = B.CASE_NO(+)"
					+ " AND A.MR_NO = B.MR_NO(+)"
					+ " AND A.MR_NO = C.MR_NO(+)"
					+ " AND A.CASE_NO = E.CASE_NO(+)"
					+ " AND A.MR_NO = E.MR_NO(+)"
					+ " AND A.ACTIVE_FLG = 'Y'"
					+ " AND B.DS_DATE IS NULL"
					+ " AND A.ALLO_FLG = 'Y'"
					+ " AND B.CANCEL_FLG <> 'Y'"
					+ " AND A.BED_STATUS = '1'"
					+ " AND B.REGION_CODE = 'H01'"
					+ " AND B.MAINDIAG = D.ICD_CODE(+)"
					+ " AND B.CASE_NO = '"
					+ case_no + "'" + " ORDER BY A.BED_NO";
			// �������
			//			String allergyNoteSql = "SELECT A.ALLERGY_NOTE FROM OPD_DRUGALLERGY A,"
			//					+ "ADM_INP B WHERE A.MR_NO = B.MR_NO AND B.CASE_NO='"
			//					+ case_no + "'";
			// ��ʱҽ��
			String stSql = "SELECT A.TAKE_DAYS,TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY,"
					+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME,A.ORDER_DR_CODE,"
					+ " A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE,C.FREQ_CHN_DESC,"
					+ " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,"
					+ " A.CAT1_TYPE,A.ORDER_NO,TO_CHAR (B.NS_EXEC_DATE, 'MM/DD HH24:MI') AS NS_EXEC_DATE,"
					+ " B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,A.NS_NOTE,D.DEPT_CHN_DESC, "
					+ " TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL "
					+ " FROM ODI_ORDER A, SYS_UNIT F, ODI_DSPNM B,SYS_PHAFREQ C,SYS_DEPT D "
					+ " WHERE     A.CASE_NO = '" + case_no + "' "
					+ " AND A.EFF_DATE BETWEEN TO_DATE('" + sdf2.format(date)
					+ " 00:00:00','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
					+ sdf2.format(date)
					+ " 23:59:59','YYYY/MM/DD HH24:MI:SS') "
					+ " AND A.CASE_NO = B.CASE_NO(+) "
					+ " AND A.ORDER_NO = B.ORDER_NO(+) "
					+ " AND A.ORDER_SEQ = B.ORDER_SEQ(+) "
					+ " AND A.RX_KIND = 'ST' " + " AND A.HIDE_FLG = 'N' "
					+ " AND A.MEDI_UNIT = F.UNIT_CODE(+) "
					+ " AND A.OPBOOK_SEQ IS NULL "
					+ " AND A.FREQ_CODE = C.FREQ_CODE "
					+ " AND A.EXEC_DEPT_CODE = D.DEPT_CODE "
					+ " ORDER BY A.EFF_DATE,A.LINK_NO,A.LINKMAIN_FLG DESC";
			// ����ҽ��
			String utSql = "SELECT TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY,"
					+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME,"
					+ " A.ORDER_DR_CODE,A.NS_CHECK_CODE,A.ORDER_DESC,A.MEDI_QTY,"
					+ " F.UNIT_CHN_DESC,A.FREQ_CODE,B.FREQ_CHN_DESC,A.DOSE_TYPE,"
					+ " A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE,"
					+ " TO_CHAR (A.DC_DATE, 'MM/DD') AS DC_DATE_DAY,"
					+ " TO_CHAR (A.DC_DATE, 'HH24:MI') AS DC_DATE_TIME,"
					+ " A.DC_DR_CODE,A.DC_NS_CHECK_CODE,A.RX_KIND,A.ROUTE_CODE,"
					+ " A.NS_NOTE,C.DEPT_CHN_DESC,A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
					+ " TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL,"
					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL,"
					+ " A.DISPENSE_FLG,A.URGENT_FLG "
					+ " FROM ODI_ORDER A, SYS_UNIT F,SYS_PHAFREQ B,SYS_DEPT C "
					+ " WHERE     A.CASE_NO = '"+ case_no+ "' "
					+ " AND A.RX_KIND = 'UD' "
					+ " AND A.HIDE_FLG = 'N' "
					+ " AND A.CAT1_TYPE = 'PHA' "
					+ " AND A.MEDI_UNIT = F.UNIT_CODE(+) "
					+ " AND A.DC_DATE IS NULL "
					+ " AND A.OPBOOK_SEQ IS NULL "
					+ " AND A.FREQ_CODE = B.FREQ_CODE "
					+ " AND A.EXEC_DEPT_CODE = C.DEPT_CODE "
					+ " ORDER BY A.EFF_DATE,A.LINK_NO,A.LINKMAIN_FLG DESC";
			// ����ҽ��
			String opSql = "SELECT A.TAKE_DAYS,TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY,"
					+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A.ORDER_DR_CODE,"
					+ " A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE,C.FREQ_CHN_DESC,"
					+ " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,"
					+ " A.CAT1_TYPE,A.ORDER_NO,TO_CHAR (B.NS_EXEC_DATE, 'MM/DD HH24:MI') AS NS_EXEC_DATE,"
					+ " B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,A.NS_NOTE,D.DEPT_CHN_DESC, "
					+ " TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL "
					+ " FROM ODI_ORDER A, SYS_UNIT F, ODI_DSPNM B,SYS_PHAFREQ C,SYS_DEPT D "
					+ " WHERE     A.CASE_NO = '" + case_no + "' "
					+ " AND A.EFF_DATE BETWEEN TO_DATE('" + sdf2.format(date)
					+ " 00:00:00','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
					+ sdf2.format(date)
					+ " 23:59:59','YYYY/MM/DD HH24:MI:SS') "
					+ " AND A.CASE_NO = B.CASE_NO(+) "
					+ " AND A.ORDER_NO = B.ORDER_NO(+) "
					+ " AND A.ORDER_SEQ = B.ORDER_SEQ(+) "
					+ " AND (A.RX_KIND = 'OP' OR A.OPBOOK_SEQ IS NOT NULL) "
					+ " AND A.HIDE_FLG = 'N' "
					+ " AND A.MEDI_UNIT = F.UNIT_CODE(+) "
					//						+ " AND A.OPBOOK_SEQ IS NOT NULL "
					+ " AND A.FREQ_CODE = C.FREQ_CODE "
					+ " AND A.EXEC_DEPT_CODE = D.DEPT_CODE "
					+ " ORDER BY A.EFF_DATE,A.LINK_NO,A.LINKMAIN_FLG DESC";
			//������
			String ckSql = "SELECT A.ORDER_DESC,A.CAT1_TYPE,A.APPLICATION_NO,"
					+ " A.ORDER_NO,A.SEQ_NO,B.* FROM MED_APPLY A, MED_LIS_RPT B WHERE "
					+ " A.REPORT_DATE BETWEEN TO_DATE('" + sdf2.format(c.getTime())
					+ " 00:00:00','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
					+ sdf2.format(date)
					+ " 23:59:59','YYYY/MM/DD HH24:MI:SS') "
					+ "	AND A.CASE_NO = '" + case_no + "' "
					+ " AND A.APPLICATION_NO = B.APPLICATION_NO";
			//			 System.out.println("basicDataSql----"+basicDataSql);
			// System.out.println("allergyNoteSql----"+allergyNoteSql);
			// System.out.println("stSql----"+stSql);
			// System.out.println("utSql----"+utSql);
			//			System.out.println("ckSql-----"+ckSql);
			TParm basicDataParm = new TParm(TJDODBTool.getInstance().select(
					basicDataSql));
			if(basicDataParm.getCount() <= 0){
				return;
			}
			mrNo = basicDataParm.getValue("MR_NO", 0);
			patName = basicDataParm.getValue("PAT_NAME", 0);
			bedNo = basicDataParm.getValue("BED_NO_DESC", 0);

			// TParm allergyNoteParm = new TParm(TJDODBTool.getInstance().select(allergyNoteSql));
			TParm stParm = new TParm(TJDODBTool.getInstance().select(stSql));
			TParm utParm = new TParm(TJDODBTool.getInstance().select(utSql));
			TParm opParm = new TParm(TJDODBTool.getInstance().select(opSql));
			TParm ckParmTemp = new TParm(TJDODBTool.getInstance().select(ckSql));
			TParm ckParm = new TParm();
			int num = 0;
			if(ckParmTemp.getCount() > 0){
				for(int i=0;i<ckParmTemp.getCount();i++){
					if(isNumeric(ckParmTemp.getValue("TEST_VALUE", i))
							&&isNumeric(ckParmTemp.getValue("UPPE_LIMIT", i))
							&&isNumeric(ckParmTemp.getValue("LOWER_LIMIT", i))){
						double testValue = ckParmTemp.getDouble("TEST_VALUE", i);
						double uppeLimit = ckParmTemp.getDouble("UPPE_LIMIT", i);
						double lowerLimit = ckParmTemp.getDouble("LOWER_LIMIT", i);
						if(testValue > uppeLimit || testValue<lowerLimit){
							//							ckParm.addRowData(ckParmTemp.getRow(i), num);
							String tmpSql = "SELECT B.NS_EXEC_DATE_REAL "
									+ " FROM MED_APPLY A, ODI_DSPND B "
									+ " WHERE     A.CAT1_TYPE = '"+ckParmTemp.getValue("CAT1_TYPE", i)+"' "
									+ " AND A.APPLICATION_NO = '"+ckParmTemp.getValue("APPLICATION_NO", i)+"' "
									+ " AND A.ORDER_NO = '"+ckParmTemp.getValue("ORDER_NO", i)+"' "
									+ " AND A.SEQ_NO = '"+ckParmTemp.getValue("SEQ_NO", i)+"' "
									+ " AND A.CASE_NO = B.CASE_NO "
									+ " AND A.ORDER_NO = B.ORDER_NO "
									+ " AND A.ORDER_CODE = B.ORDER_CODE "
									+ " AND A.SEQ_NO = B.ORDER_SEQ";
							//							System.out.println("tmpSql-----"+tmpSql);
							TParm tmpParm = new TParm(TJDODBTool.getInstance().select(tmpSql));
							if(tmpParm.getValue("NS_EXEC_DATE_REAL", 0) == null
									|| tmpParm.getValue("NS_EXEC_DATE_REAL", 0).equals("")){
								ckParm.addData("NS_EXEC_DATE_REAL", "");
							}else{
								ckParm.addData("NS_EXEC_DATE_REAL", sdf4.format(sdf4.parse(tmpParm.getValue("NS_EXEC_DATE_REAL", 0))));
							}

							ckParm.addData("ORDER_DESC", ckParmTemp.getValue("ORDER_DESC", i));
							ckParm.addData("TESTITEM_CODE", ckParmTemp.getValue("TESTITEM_CODE", i));
							ckParm.addData("TESTITEM_CHN_DESC", ckParmTemp.getValue("TESTITEM_CHN_DESC", i));
							ckParm.addData("TEST_VALUE", ckParmTemp.getValue("TEST_VALUE", i));
							ckParm.addData("TEST_UNIT", ckParmTemp.getValue("TEST_UNIT", i));
							ckParm.addData("UPPE_LIMIT", ckParmTemp.getValue("UPPE_LIMIT", i));
							ckParm.addData("LOWER_LIMIT", ckParmTemp.getValue("LOWER_LIMIT", i));
							num++;
						}
					}else{
						if(ckParmTemp.getValue("TEST_VALUE", i).indexOf("����") != -1){
							String tmpSql = "SELECT B.NS_EXEC_DATE_REAL "
									+ " FROM MED_APPLY A, ODI_DSPND B "
									+ " WHERE     A.CAT1_TYPE = '"+ckParmTemp.getValue("CAT1_TYPE", i)+"' "
									+ " AND A.APPLICATION_NO = '"+ckParmTemp.getValue("APPLICATION_NO", i)+"' "
									+ " AND A.ORDER_NO = '"+ckParmTemp.getValue("ORDER_NO", i)+"' "
									+ " AND A.SEQ_NO = '"+ckParmTemp.getValue("SEQ_NO", i)+"' "
									+ " AND A.CASE_NO = B.CASE_NO "
									+ " AND A.ORDER_NO = B.ORDER_NO "
									+ " AND A.ORDER_CODE = B.ORDER_CODE "
									+ " AND A.SEQ_NO = B.ORDER_SEQ";
							//							System.out.println("tmpSql-----"+tmpSql);
							TParm tmpParm = new TParm(TJDODBTool.getInstance().select(tmpSql));
							if(tmpParm.getValue("NS_EXEC_DATE_REAL", 0) == null
									|| tmpParm.getValue("NS_EXEC_DATE_REAL", 0).equals("")){
								ckParm.addData("NS_EXEC_DATE_REAL", "");
							}else{
								ckParm.addData("NS_EXEC_DATE_REAL", sdf4.format(sdf4.parse(tmpParm.getValue("NS_EXEC_DATE_REAL", 0))));
							}
							ckParm.addData("ORDER_DESC", ckParmTemp.getValue("ORDER_DESC", i));
							ckParm.addData("TESTITEM_CODE", ckParmTemp.getValue("TESTITEM_CODE", i));
							ckParm.addData("TESTITEM_CHN_DESC", ckParmTemp.getValue("TESTITEM_CHN_DESC", i));
							ckParm.addData("TEST_VALUE", ckParmTemp.getValue("TEST_VALUE", i));
							ckParm.addData("TEST_UNIT", ckParmTemp.getValue("TEST_UNIT", i));
							ckParm.addData("UPPE_LIMIT", ckParmTemp.getValue("UPPE_LIMIT", i));
							ckParm.addData("LOWER_LIMIT", ckParmTemp.getValue("LOWER_LIMIT", i));
							num++;
						}
					}
				}
				ckParm.setCount(num);
			}
			//			System.out.println("ckParm:"+ckParm);
			// ʹ��PDFWriter����д�ļ�����
			String dir = "C:\\backup\\";
			String str = sdf1.format(date);
			dir += str;
			dir += "\\" + basicDataParm.getValue("BED_NO_DESC", 0) + "_"
					+ basicDataParm.getValue("MR_NO", 0) + "_"
					+ basicDataParm.getValue("PAT_NAME", 0) + "\\";
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
			String fileName = case_no + "_"
					+ basicDataParm.getValue("PAT_NAME", 0);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dir + fileName
					+ ".pdf"));

			// ���ҳü 
			//			ImageIcon im = TIOM_AppServer.getImage("%ROOT%\\image\\ImageIcon\\1.jpg");
			//		    Image image = Image.getInstance(im.getImage(),Color.black);
			//			image.scaleToFit(180f, 1200f);
			//			
			//			document.add(image);

			writer.setPageEvent(new OPDBackupDataToPDFControl(case_no));

			document.open();
			writer.getDirectContent().saveState();// add by wangqing 20170921 ���סԺ����ʱ���ݳ��������
			
			// ��������
			// BaseFont bfChinese =BaseFont.createFont("STSong-Light",
			// "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
			BaseFont bfChinese = BaseFont.createFont(
					"C:/WINDOWS/Fonts/SIMSUN.TTC,1", BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);
			Font fontChinese = new Font(bfChinese, 10, Font.NORMAL);
			Font titleChinese = new Font(bfChinese, 14, Font.NORMAL);
			Font tableFontChinese = new Font(bfChinese, 8, Font.NORMAL);

			// -------------------------------��������
			// start-----------------------------------------------
			Paragraph t = new Paragraph("崻�����\n", titleChinese);
			t.setAlignment(1); 
			//			Chunk chunk1 = new Chunk("崻�����\n", titleChinese);
			//			p1.add(chunk1);
			document.add(t);
			writer.getDirectContent().restoreState(); // add by wangqing 20170921 ���סԺ����ʱ���ݳ��������
			
			Phrase p1 = new Phrase();
			Chunk chunk1 = new Chunk("һ����������\n", fontChinese);
			p1.add(chunk1);
			document.add(p1);
			String age = "";
			Timestamp sysDate = SystemTool.getInstance().getDate();
			Timestamp temp = basicDataParm.getTimestamp("BIRTH_DATE",0) == null ? sysDate
					: basicDataParm.getTimestamp("BIRTH_DATE",0);
			age = OdiUtil.getInstance().showAge(temp,basicDataParm.getTimestamp("IN_DATE", 0));
			int days = 0;
			days = StringTool.getDateDiffer(StringTool.setTime(sysDate,"00:00:00"), 
					StringTool.setTime(basicDataParm.getTimestamp("IN_DATE", 0), "00:00:00"));
			if(days == 0){
				days = 1;
			}
			String userSql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+basicDataParm.getValue("VS_DR_CODE", 0)+"'";
			TParm userParm = new TParm(TJDODBTool.getInstance().select(userSql));
			String sexSql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID='"+basicDataParm.getValue("SEX_CODE", 0)+"'";
			TParm sexParm = new TParm(TJDODBTool.getInstance().select(sexSql));
			String nursingSql = "SELECT NURSING_CLASS_DESC FROM ADM_NURSING_CLASS WHERE NURSING_CLASS_CODE='"+basicDataParm.getValue("NURSING_CLASS", 0)+"'";
			TParm nursingParm = new TParm(TJDODBTool.getInstance().select(nursingSql));
			String ctzSql = "SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE = '"+basicDataParm.getValue("CTZ1_CODE", 0)+"'";
			TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
			String clnSql = "SELECT CLNCPATH_CHN_DESC FROM CLP_BSCINFO WHERE CLNCPATH_CODE = '"+basicDataParm.getValue("CLNCPATH_CODE", 0)+"'";
			TParm clnParm = new TParm(TJDODBTool.getInstance().select(clnSql));
			String patientSql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'ADM_PATIENT_STATUS' AND ID='"+basicDataParm.getValue("PATIENT_STATUS", 0)+"'";
			TParm patientParm = new TParm(TJDODBTool.getInstance().select(patientSql));
			String schdSql = "SELECT DURATION_CHN_DESC FROM CLP_DURATION WHERE DURATION_CODE = '"+basicDataParm.getValue("SCHD_CODE", 0)+"'";
			TParm schdParm = new TParm(TJDODBTool.getInstance().select(schdSql));
			String serviceLevelSql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SERVICE_LEVEL' AND ID='"+basicDataParm.getValue("SERVICE_LEVEL", 0)+"'";
			TParm serviceLevelParm = new TParm(TJDODBTool.getInstance().select(serviceLevelSql));

			String basicData = "����:" + basicDataParm.getValue("BED_NO_DESC", 0)
			+ "  ����:" + basicDataParm.getValue("PAT_NAME", 0)
			+ "  ������:" + basicDataParm.getValue("MR_NO", 0) 
			+ "  �Ա�:" + sexParm.getValue("CHN_DESC", 0) 
			+ "  ����:" + age
			+ "  ��Ժ����:" + basicDataParm.getValue("IN_DATE", 0).substring(0, 10) 
			+ "  סԺ����:" + days +"��" 
			+ "\n����ҽʦ:" + userParm.getValue("USER_NAME", 0) 
			+ "  �������:" + basicDataParm.getValue("MAINDIAG", 0) 
			+ "  ����ȼ�:" + nursingParm.getValue("NURSING_CLASS_DESC", 0) 
			+ "  ����״̬:" + patientParm.getValue("CHN_DESC", 0) 
			+ "  ���ʽ:" + ctzParm.getValue("CTZ_DESC", 0) 
			+ "  Ԥ�������:" + basicDataParm.getValue("CUR_AMT", 0) 
			+ "\n�ٴ�·��:" + clnParm.getValue("CLNCPATH_CHN_DESC", 0) 
			+ "  ʱ��:" + schdParm.getValue("DURATION_CHN_DESC", 0) 
			+ "  ������:" + basicDataParm.getValue("DISE_CODE", 0) 
			+ "  סԺ��:" + basicDataParm.getValue("IPD_NO", 0) 
			+ "  �������:" + basicDataParm.getValue("MRO_CHAT_FLG", 0) 
			+ "  ����ȼ�:" + serviceLevelParm.getValue("CHN_DESC", 0) 
			+ "\n\n";
			p1 = new Phrase();
			chunk1 = new Chunk(basicData, tableFontChinese);
			p1.add(chunk1);
			document.add(p1);
			// -------------------------------��������
			// end-----------------------------------------------
			// -------------------------------�������
			// start-----------------------------------------------
			// chunk1 = new Chunk("�������\n", fontChinese);
			// p1 = new Phrase();
			// p1.add(chunk1);
			// document.add(p1);
			// String allergyNote =
			// allergyNoteParm.getValue("ALLERGY_NOTE")+"\n\n";
			// chunk1 = new Chunk(allergyNote, tableFontChinese);
			// p1 = new Phrase();
			// p1.add(chunk1);
			// document.add(p1);
			// -------------------------------�������
			// end-----------------------------------------------
			// -------------------------------�������
			// start-----------------------------------------------
			// chunk1 = new Chunk("�������\n", fontChinese);
			// p1 = new Phrase();
			// p1.add(chunk1);
			// document.add(p1);
			// String maindiag = basicDataParm.getValue("ICD_CHN_DESC")+"\n\n";
			// chunk1 = new Chunk(maindiag, tableFontChinese);
			// p1 = new Phrase();
			// p1.add(chunk1);
			// document.add(p1);
			// -------------------------------������� end-----------------------------------------------
			// -------------------------------��ʱҽ����24Сʱ�ڣ� start-----------------------------------------------
			if(stParm.getCount() > 0){
				chunk1 = new Chunk("������ʱҽ��(24Сʱ��)", fontChinese);
				p1 = new Phrase();
				p1.add(chunk1);
				document.add(p1);
				// ������colNumber(6)�еı��
				PdfPTable datatableST = new PdfPTable(tableHeaderST.length);
				// ������Ŀ��
				int[] cellsWidthST = { 2, 2, 8, 4, 4, 6, 6, 6, 10, 6, 6 };
				datatableST.setWidths(cellsWidthST);


				// ���Ŀ�Ȱٷֱ�
				datatableST.setWidthPercentage(100);
				datatableST.getDefaultCell().setPadding(padding);
				datatableST.getDefaultCell().setBorderWidth(spacing);
				// ���ñ��ĵ�ɫ
				// datatable.getDefaultCell().setBackgroundColor(BaseColor.GREEN);
				datatableST.getDefaultCell().setHorizontalAlignment(
						1);
				// ��ӱ�ͷԪ��
				for (int i = 0; i < tableHeaderST.length; i++) {
					datatableST
					.addCell(new Paragraph(tableHeaderST[i], fontChinese));
				}
				for (int j = 0; j < stParm.getCount(); j++) {
					// �����Ԫ��
					// for (int i = 0; i <tableHeaderST.length; i++) {
					Paragraph paragraph = new Paragraph();
					datatableST.addCell(new Paragraph(
							stParm.getValue("LINK_NO", j), tableFontChinese));// ��
					if (stParm.getValue("LINKMAIN_FLG", j).equals("Y")) {
						datatableST.addCell(new Paragraph("��", tableFontChinese));// ��
					} else {
						datatableST.addCell(new Paragraph("", tableFontChinese));// ��
					}
					paragraph = new Paragraph(stParm.getValue("ORDER_DESC", j),
							tableFontChinese);
					paragraph.setAlignment(0);
					PdfPCell cell = new PdfPCell();
					cell.setPhrase(paragraph);
					datatableST.addCell(cell);// ҽ������
					Paragraph paragraph1 = new Paragraph(stParm.getValue(
							"MEDI_QTY", j), tableFontChinese);
					paragraph1.setAlignment(2);
					PdfPCell cell1 = new PdfPCell();
					cell1.setPhrase(paragraph1);
					datatableST.addCell(cell1);// ����
					paragraph = new Paragraph(stParm.getValue("UNIT_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableST.addCell(cell);// ��λ
					String s = "SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE='"
							+ stParm.getValue("ROUTE_CODE", j) + "'";
					//					 System.out.println("s-----"+s);
					TParm sParm = new TParm(TJDODBTool.getInstance().select(s));
					paragraph = new Paragraph(sParm.getValue("ROUTE_CHN_DESC", 0),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableST.addCell(cell);// �÷�
					paragraph = new Paragraph(stParm.getValue("FREQ_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableST.addCell(cell);// Ƶ��
					paragraph = new Paragraph(stParm.getValue("DEPT_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableST.addCell(cell);// ִ�п���
					paragraph = new Paragraph(stParm.getValue("EFF_DATE_FULL", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableST.addCell(cell);// ����ʱ��
					paragraph = new Paragraph(stParm.getValue("DR_NOTE", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableST.addCell(cell);// ҽ����ע
					paragraph = new Paragraph(stParm.getValue("NS_NOTE", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableST.addCell(cell);// ��ʿ��ע
				}
				document.add(datatableST);
			}else{
				chunk1 = new Chunk("������ʱҽ��(24Сʱ��)\n\n", fontChinese);
				p1 = new Phrase();
				p1.add(chunk1);
				document.add(p1);
			}
			// -------------------------------��ʱҽ����24Сʱ�ڣ�end-----------------------------------------------
			// -------------------------------����ҽ�� start-----------------------------------------------
			if(utParm.getCount() > 0){
				chunk1 = new Chunk("��������ҽ��(��ǰʹ���е�)", fontChinese);
				p1 = new Phrase();
				p1.add(chunk1);
				document.add(p1);
				PdfPTable datatableUT = new PdfPTable(tableHeaderUT.length);
				// ������Ŀ��
				int[] cellsWidthUT = { 2, 2, 8, 4, 4, 6, 6, 2, 2, 6, 6, 10 };
				datatableUT.setWidths(cellsWidthUT);
				// ���Ŀ�Ȱٷֱ�
				datatableUT.setWidthPercentage(100);
				datatableUT.getDefaultCell().setPadding(padding);
				datatableUT.getDefaultCell().setBorderWidth(spacing);
				// ���ñ��ĵ�ɫ
				// datatable.getDefaultCell().setBackgroundColor(BaseColor.GREEN);
				datatableUT.getDefaultCell().setHorizontalAlignment(
						1);
				// ��ӱ�ͷԪ��
				for (int i = 0; i < tableHeaderUT.length; i++) {
					datatableUT
					.addCell(new Paragraph(tableHeaderUT[i], fontChinese));
				}
				for (int j = 0; j < utParm.getCount(); j++) {
					// �����Ԫ��
					// �� �� ҽ������ ���� ��λ Ƶ�� �÷� �� �� ҽ����ע ִ�п��� ����ʱ��
					datatableUT.addCell(new Paragraph(
							utParm.getValue("LINK_NO", j), tableFontChinese));// ��
					if (utParm.getValue("LINKMAIN_FLG", j).equals("Y")) {
						datatableUT.addCell(new Paragraph("��", tableFontChinese));// ��
					} else {
						datatableUT.addCell(new Paragraph("", tableFontChinese));// ��
					}
					Paragraph paragraph = new Paragraph();
					paragraph = new Paragraph(utParm.getValue("ORDER_DESC", j),
							tableFontChinese);
					paragraph.setAlignment(0);
					PdfPCell cell = new PdfPCell();
					cell.setPhrase(paragraph);
					datatableUT.addCell(cell);// ҽ������
					paragraph = new Paragraph(utParm.getValue("MEDI_QTY", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableUT.addCell(cell);// ����
					paragraph = new Paragraph(utParm.getValue("UNIT_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableUT.addCell(cell);// ��λ

					paragraph = new Paragraph(utParm.getValue("FREQ_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableUT.addCell(cell);// Ƶ��
					String s = "SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE='"
							+ utParm.getValue("ROUTE_CODE", j) + "'";
					//					System.out.println("s-----"+s);
					TParm sParm = new TParm(TJDODBTool.getInstance().select(s));
					paragraph = new Paragraph(sParm.getValue("ROUTE_CHN_DESC", 0),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableUT.addCell(cell);// �÷�
					if (utParm.getValue("URGENT_FLG", j).equals("Y")) {
						datatableUT.addCell(new Paragraph("��", tableFontChinese));// ��
					} else {
						datatableUT.addCell(new Paragraph("", tableFontChinese));// ��
					}
					if (utParm.getValue("DISPENSE_FLG", j).equals("Y")) {
						datatableUT.addCell(new Paragraph("��", tableFontChinese));// ��
					} else {
						datatableUT.addCell(new Paragraph("", tableFontChinese));// ��
					}
					paragraph = new Paragraph(utParm.getValue("DR_NOTE", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableUT.addCell(cell);// ҽ����ע
					paragraph = new Paragraph(utParm.getValue("DEPT_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableUT.addCell(cell);// ִ�п���
					paragraph = new Paragraph(utParm.getValue("EFF_DATE_FULL", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableUT.addCell(cell);// ����ʱ��
				}
				document.add(datatableUT);
			}else{
				chunk1 = new Chunk("��������ҽ��(��ǰʹ���е�)\n\n", fontChinese);
				p1 = new Phrase();
				p1.add(chunk1);
				document.add(p1);
			}
			// -------------------------------����ҽ��  end-----------------------------------------------
			// -------------------------------����ҽ�� start-----------------------------------------------
			if(opParm.getCount() > 0){
				chunk1 = new Chunk("�ġ�����ҽ��(24Сʱ��)", fontChinese);
				p1 = new Phrase();
				p1.add(chunk1);
				document.add(p1);
				// ������colNumber(6)�еı��
				PdfPTable datatableOP = new PdfPTable(tableHeaderOP.length);
				// ������Ŀ��
				int[] cellsWidthOP = { 2, 2, 8, 6, 4, 4, 6, 6, 6, 10, 6 };
				datatableOP.setWidths(cellsWidthOP);
				// ���Ŀ�Ȱٷֱ�
				datatableOP.setWidthPercentage(100);
				datatableOP.getDefaultCell().setPadding(padding);
				datatableOP.getDefaultCell().setBorderWidth(spacing);
				// ���ñ��ĵ�ɫ
				// datatable.getDefaultCell().setBackgroundColor(BaseColor.GREEN);
				datatableOP.getDefaultCell().setHorizontalAlignment(
						1);
				// ��ӱ�ͷԪ��
				for (int i = 0; i < tableHeaderOP.length; i++) {
					datatableOP
					.addCell(new Paragraph(tableHeaderOP[i], fontChinese));
				}
				for (int j = 0; j < opParm.getCount(); j++) {
					// �����Ԫ��
					Paragraph paragraph = new Paragraph();
					datatableOP.addCell(new Paragraph(
							opParm.getValue("LINK_NO", j), tableFontChinese));// ��
					if (opParm.getValue("LINKMAIN_FLG", j).equals("Y")) {
						datatableOP.addCell(new Paragraph("��", tableFontChinese));// ��
					} else {
						datatableOP.addCell(new Paragraph("", tableFontChinese));// ��
					}
					paragraph = new Paragraph(opParm.getValue("ORDER_DESC", j),
							tableFontChinese);
					paragraph.setAlignment(0);
					PdfPCell cell = new PdfPCell();
					cell.setPhrase(paragraph);
					datatableOP.addCell(cell);// ҽ������
					paragraph = new Paragraph(opParm.getValue("DR_NOTE", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableOP.addCell(cell);// ҽ����ע
					Paragraph paragraph1 = new Paragraph(opParm.getValue(
							"MEDI_QTY", j), tableFontChinese);
					paragraph1.setAlignment(2);
					PdfPCell cell1 = new PdfPCell();
					cell1.setPhrase(paragraph1);
					datatableOP.addCell(cell1);// ����
					paragraph = new Paragraph(opParm.getValue("UNIT_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableOP.addCell(cell);// ��λ
					String s = "SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE='"
							+ opParm.getValue("ROUTE_CODE", j) + "'";
					// System.out.println("s-----"+s);
					TParm sParm = new TParm(TJDODBTool.getInstance().select(s));
					paragraph = new Paragraph(sParm.getValue("ROUTE_CHN_DESC", 0),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableOP.addCell(cell);// �÷�
					paragraph = new Paragraph(opParm.getValue("FREQ_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableOP.addCell(cell);// Ƶ��
					paragraph = new Paragraph(opParm.getValue("DEPT_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableOP.addCell(cell);// ִ�п���
					paragraph = new Paragraph(opParm.getValue("EFF_DATE_FULL", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableOP.addCell(cell);// ����ʱ��
					paragraph = new Paragraph(opParm.getValue("NS_NOTE", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableOP.addCell(cell);// ��ʿ��ע
				}
				document.add(datatableOP);
			}else{
				chunk1 = new Chunk("�ġ�����ҽ��(24Сʱ��)\n\n", fontChinese);
				p1 = new Phrase();
				p1.add(chunk1);
				document.add(p1);
			}
			// -------------------------------����ҽ�� end-----------------------------------------------
			// -------------------------------���鱨����(�쳣���Ǳ���סԺ�ڵ��쳣����) start-----------------------------------------------
			chunk1 = new Chunk("�塢���鱨����(5����)", fontChinese);
			p1 = new Phrase();
			p1.add(chunk1);
			document.add(p1);
			if(ckParm.getCount()>0){
				// ������colNumber(6)�еı��
				PdfPTable datatableCK = new PdfPTable(tableHeaderCK.length);
				// ������Ŀ��
				int[] cellsWidthCK = {8, 10, 10, 4, 4, 4, 4};
				datatableCK.setWidths(cellsWidthCK);
				// ���Ŀ�Ȱٷֱ�
				datatableCK.setWidthPercentage(100);
				datatableCK.getDefaultCell().setPadding(padding);
				datatableCK.getDefaultCell().setBorderWidth(spacing);
				// ���ñ��ĵ�ɫ
				// datatable.getDefaultCell().setBackgroundColor(BaseColor.GREEN);
				datatableCK.getDefaultCell().setHorizontalAlignment(
						1);
				// ��ӱ�ͷԪ��
				for (int i = 0; i < tableHeaderCK.length; i++) {
					datatableCK
					.addCell(new Paragraph(tableHeaderCK[i], fontChinese));
				}
				for (int j = 0; j < ckParm.getCount(); j++) {
					// �����Ԫ��
					Paragraph paragraph = new Paragraph();
					paragraph = new Paragraph(ckParm.getValue("NS_EXEC_DATE_REAL", j),
							tableFontChinese);
					paragraph.setAlignment(0);
					PdfPCell cell = new PdfPCell();
					cell.setPhrase(paragraph);
					datatableCK.addCell(cell);//ִ��ʱ��
					paragraph = new Paragraph(ckParm.getValue("ORDER_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableCK.addCell(cell);//��Ŀ����
					paragraph = new Paragraph(ckParm.getValue("TESTITEM_CHN_DESC", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableCK.addCell(cell);//ϸ������
					paragraph = new Paragraph(ckParm.getValue("TEST_VALUE", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableCK.addCell(cell);//����ֵ
					paragraph = new Paragraph(ckParm.getValue("TEST_UNIT", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableCK.addCell(cell);//��λ
					paragraph = new Paragraph(ckParm.getValue("UPPE_LIMIT", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableCK.addCell(cell);//��׼����
					paragraph = new Paragraph(ckParm.getValue("LOWER_LIMIT", j),
							tableFontChinese);
					cell.setPhrase(paragraph);
					datatableCK.addCell(cell);//��׼����
				}
				document.add(datatableCK);
			}
			//			this.messageBox("���ݳɹ�");
			// -------------------------------���鱨����(�쳣���Ǳ���סԺ�ڵ��쳣����) end-----------------------------------------------
		} catch (Exception e) {
			tc.messageBox(sdf3.format(date)+" ����:"+bedNo+" ������:"+mrNo+" ��������:"+patName+",�����ļ�ʧ��!");
			e.printStackTrace();
		}
		document.close();
	}

	/**
	 * ɾ��һ��ǰ�ı����ļ�
	 */
	public void deleteFile(){
		String path = "C:\\backup\\";
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		File file = new File(path);
		//        File [] files = file.listFiles();
		String [] names = file.list();
		try {
			for(int i=0;i<names.length;i++){
				//	        	System.out.println(names[i]);
				String lastWeek = lastWeek();
				Date lastWeekDate = sdf1.parse(lastWeek);
				Date d = sdf1.parse(names[i]);
				if(d.getTime() < lastWeekDate.getTime()){
					File delFile = new File(path+names[i]);  
					// �ж�Ŀ¼���ļ��Ƿ����  
					if (!delFile.exists()) {  // �����ڷ��� false  
						return;  
					} else {  
						// �ж��Ƿ�Ϊ�ļ�  
						if (delFile.isFile()) {  // Ϊ�ļ�ʱ����ɾ���ļ�����  
							deleteFile(path+names[i]);  
						} else {  // ΪĿ¼ʱ����ɾ��Ŀ¼����  
							deleteDirectory(path+names[i]);  
						}  
					}
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String lastWeek(){
		Date date = new Date();
		int year=Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		int month=Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int day=Integer.parseInt(new SimpleDateFormat("dd").format(date))-6;

		if(day<1){
			month-=1;
			if(month==0){
				year-=1;month=12;
			}
			if(month==4||month==6||month==9||month==11){
				day=30+day;
			}else if(month==1||month==3||month==5||month==7||month==8||month==10||month==12)
			{
				day=31+day;
			}else if(month==2){
				if(year%400==0||(year %4==0&&year%100!=0))day=29+day;
				else day=28+day;
			}     
		}
		String y = year+"";String m ="";String d ="";
		if(month<10) m = "0"+month;
		else m=month+"";
		if(day<10) d = "0"+day;
		else d = day+"";

		return y+m+d;
	}

	/**
	 * ɾ��Ŀ¼���ļ��У��Լ�Ŀ¼�µ��ļ�
	 * @param   sPath ��ɾ��Ŀ¼���ļ�·��
	 * @return  Ŀ¼ɾ���ɹ�����true�����򷵻�false
	 */
	public boolean deleteDirectory(String sPath) {
		//���sPath�����ļ��ָ�����β���Զ�����ļ��ָ���
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		//���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		//ɾ���ļ����µ������ļ�(������Ŀ¼)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			//ɾ�����ļ�
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) break;
			} //ɾ����Ŀ¼
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) break;
			}
		}
		if (!flag) return false;
		//ɾ����ǰĿ¼
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ɾ�������ļ�
	 * @param   sPath    ��ɾ���ļ����ļ���
	 * @return �����ļ�ɾ���ɹ�����true�����򷵻�false
	 */
	public boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	//	 public static void main(String[] args)throws Exception {
	//		 backupDataToPDF("120511000383");
	////		 deleteFile();
	//	 }

	/**�ж��Ƿ�������
	 * 
	 */
	public boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+"); 
		return pattern.matcher(str).matches(); 
	}


	public void onOpenDocument(PdfWriter writer, Document document) {
		try {
			tpl = writer.getDirectContent().createTemplate(100, 100);
			bf = BaseFont.createFont(
					"C:/WINDOWS/Fonts/SIMSUN.TTC,1", BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);

		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	public void onStartPage(PdfWriter writer, Document document){
		// ���ҳü 
		try {
			PdfContentByte cb = writer.getDirectContent();
			cb.saveState();// add by wangqing 20170921 ���סԺ����ʱ���ݳ��������
			ImageIcon im = TIOM_AppServer.getImage("%ROOT%\\image\\ImageIcon\\1.jpg");
			Image image = Image.getInstance(im.getImage(),Color.black);
			image.scaleToFit(180f, 1200f);
			image.setAbsolutePosition(50, 800);
			document.add(image);

			cb.setLineWidth(Float.parseFloat("1"));
			//				cb.setColorStroke(Color.gray);
			cb.setColorStroke(BaseColor.GRAY);
			cb.moveTo(50, 795);
			cb.lineTo(548, 795);
			cb.stroke(); 

			cb.setLineWidth(Float.parseFloat("1"));
			//				cb.setColorStroke(Color.gray);
			cb.setColorStroke(BaseColor.GRAY);
			cb.moveTo(50, 40);
			cb.lineTo(548, 40);
			cb.stroke();
			cb.restoreState();// add by wangqing 20170921 ���סԺ����ʱ���ݳ��������
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onEndPage(PdfWriter writer, Document document) {
		// ��ÿҳ������ʱ��ѡ���xҳ����Ϣд��ģ��ָ��λ��
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();// add by wangqing 20170921 ���סԺ����ʱ���ݳ��������
		String text = "��" + writer.getPageNumber() + "ҳ,��";
		cb.beginText();
		cb.setFontAndSize(bf, 8);
		cb.setTextMatrix(470, 25);// ��λ����xҳ,���� �ھ����ҳ�����ʱ����Ҫ������xy������
		cb.showText(text);
		cb.endText();
		cb.addTemplate(tpl, 502, 25);// ��λ��yҳ�� �ھ����ҳ�����ʱ����Ҫ������xy������

		String sql = "SELECT A.MR_NO,B.PAT_NAME FROM ADM_INP A,SYS_PATINFO B WHERE A.CASE_NO = '"
				+this.caseNo
				+"' AND A.MR_NO = B.MR_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			String mrNo = "�����ţ�"+parm.getValue("MR_NO", 0);
			String patName = "��  ����"+parm.getValue("PAT_NAME", 0);
			cb.beginText();
			cb.setFontAndSize(bf, 8);
			cb.setTextMatrix(450, 810);// ��λ����xҳ,���� �ھ����ҳ�����ʱ����Ҫ������xy������
			cb.showText(mrNo);
			cb.endText();
			cb.beginText();
			cb.setFontAndSize(bf, 8);
			cb.setTextMatrix(450, 800);// ��λ����xҳ,���� �ھ����ҳ�����ʱ����Ҫ������xy������
			cb.showText(patName);
			cb.endText();
		}

//		cb.saveState();// delete by wangqing 20170921 ���סԺ����ʱ���ݳ��������
		cb.stroke();
		cb.restoreState();
		cb.closePath();// sanityCheck();
	}


	public void onCloseDocument(PdfWriter writer, Document document) {
		// �ر�document��ʱ���ȡ��ҳ����������ҳ����ģ��д��֮ǰԤ����λ��
		tpl.beginText();
		tpl.setFontAndSize(bf, 8);
		tpl.showText(Integer.toString(writer.getPageNumber() - 1) + "ҳ");
		tpl.endText();
		tpl.closePath();// sanityCheck();
	}
}
