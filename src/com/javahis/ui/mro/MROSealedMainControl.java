package com.javahis.ui.mro;

import java.awt.print.PrinterJob;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;



/**
 * <p>Title: ��没�������� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author huangtt 20161108
 * @version 1.0
 */

public class MROSealedMainControl extends TControl{
	
	private TTable table;
	private TTable tableOld;
	private TTable tableSeal;
	private String fileServerRoot = TConfig.getSystemValue("FileServer.Main.Root");
	String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	
	public void onInit(){
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("S_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("E_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		
		this.setValue("IN", true);
		
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicke");
		
		table = (TTable) this.getComponent("TABLE");
		tableOld = (TTable) this.getComponent("TABLE_OLD");
		tableSeal = (TTable) this.getComponent("TABLE_NEW");
		
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			TParm acceptData = (TParm) obj;
			this.setValue("MR_NO", acceptData.getValue("MR_NO"));
			if(this.getValueString("MR_NO").length() > 0){
				this.onQuery();
			}
			
	
		}
		
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		
	}
	
	public void onQuery(){
		
		// ���ò�ѯ����
		String date_s = getValueString("S_DATE");
		String date_e = getValueString("E_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		
		
		String sql = "SELECT  A.MR_NO,C.PAT_NAME, TO_CHAR(A.IN_DATE,'YYYY/MM/DD HH24:MI:SS')  IN_DATE,D.DEPT_CHN_DESC IN_DEPT," +
				" TO_CHAR(A.DS_DATE,'YYYY/MM/DD HH24:MI:SS')  DS_DATE,E.DEPT_CHN_DESC DS_DEPT,B.SEALED_STATUS," +
				" TO_CHAR(B.SEALED_DATE,'YYYY/MM/DD HH24:MI:SS') SEALED_DATE,B.SEALED_USER," +
				" TO_CHAR(B.SEALED_PRINT_DATE,'YYYY/MM/DD HH24:MI:SS') SEALED_PRINT_DATE,B.SEALED_PRINT_USER,A.CASE_NO,B.SEALED_PROBLEM" +
				" FROM ADM_INP A,MRO_MRV_TECH B ,SYS_PATINFO C,SYS_DEPT D,SYS_DEPT E" +
				" WHERE A.CASE_NO = B.CASE_NO(+)" +
				" AND A.MR_NO = C.MR_NO" +
				" AND A.IN_DEPT_CODE = D.DEPT_CODE" +
				" AND A.DS_DEPT_CODE = E.DEPT_CODE(+)" +
				" AND A.IN_DATE BETWEEN TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') " +
						" AND TO_DATE('"+date_e+"','YYYYMMDDHH24MISS')";
		
		if(this.getValueBoolean("IN")){
			sql += " AND DS_DATE IS NULL ";
		}
		
		if(this.getValueBoolean("OUT")){
			sql += " AND DS_DATE IS NOT NULL ";
		}
		

		
		if(getValueString("DS_DEPT_CODE").length() > 0){
			sql += " AND A.DS_DEPT_CODE='"+this.getValueString("DS_DEPT_CODE")+"'";
		}
		
		if(getValueString("DS_STATION_CODE").length() > 0){
			sql += " AND A.DS_STATION_CODE='"+this.getValueString("DS_STATION_CODE")+"'";
		}
		
		if(this.getValueString("MR_NO").length() > 0){
			sql += " AND A.MR_NO='"+this.getValueString("MR_NO")+"'";
		}
		
		if(this.getValueString("SEALED_STATUS").length() > 0){
			sql += " AND B.SEALED_STATUS='"+this.getValueString("SEALED_STATUS")+"'";
		}
		
		System.out.println(sql);
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			table.removeRowAll();
			this.messageBox("û��Ҫ��ѯ������ ");
			return;
		}
		table.setParmValue(parm);
		
	}
	
	public void onMrNo(){
		if(this.getValueString("MR_NO").length() > 0){
			String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
			this.setValue("MR_NO", srcMrNo);
			onQuery();
		}
	}
	
	/**
	 * �����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicke(int row) {
		if (row < 0)
			return;
		
		table.acceptText();
		
		String caseNo = table.getParmValue().getValue("CASE_NO", row);
		
		String sql = "SELECT A.FILE_PATH,A.FILE_NAME,A.SEALED_STATUS,'JHW' FILE_TYPE,A.FILE_SEQ  " +
				" FROM EMR_FILE_INDEX A,EMR_TEMPLET B" +
				" WHERE A.CASE_NO='"+caseNo+"'" +
				" AND A.SUBCLASS_CODE = B.SUBCLASS_CODE" +
				" AND B.SEALED_TYPE = 'Y' ORDER BY A.SEALED_STATUS DESC";
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		
		if(parm.getCount() < 0){
			tableOld.removeRowAll();
			tableSeal.removeRowAll();
			this.messageBox("û�в�ѯ����");
			return;
		}
		
		String fileName = parm.getValue("FILE_NAME",0);
		String serverPath = getEmrDataDir(fileName) + "\\"
		+ parm.getValue("FILE_PATH",0).replaceFirst("JHW", "PDF");
		
		String filePath = TConfig.getSystemValue("FileServer.Main.Root")
		+ "\\��没��"
		+ parm.getValue("FILE_PATH",0).replaceFirst("JHW", "");
		
//		System.out.println(getEmrDataDir(fileName));
//		System.out.println(filePath);
		
		String f[] = TIOM_FileServer.listFile(getFileServerAddress(fileName),
				filePath);

		String s[] = TIOM_FileServer.listFile(getFileServerAddress(fileName),
				serverPath);
		
		TParm fParm = new TParm();
		
		if(f != null){
			
			for (int i = 0; i < f.length; i++) {
				String fileN = f[i];
				fileN = fileN.replaceAll(".pdf", "");
				
				fParm.addData("FLG", "N");
				fParm.addData("FILE_NAME", fileN);
				fParm.addData("FILE_PATH", parm.getValue("FILE_PATH",0).replaceFirst("JHW", ""));
				
			}
	
		}
		
		
		
		TParm result = new TParm();

		List<String> names = new ArrayList<String>();
		
		if (s != null) {
			for (int i = 0; i < s.length; i++) {
//				System.out.println(i+"----"+s[i]);
				String fileN = s[i];
				fileN = fileN.replaceAll(".pdf", "");
				
				boolean flg = true;
				for (int j = 0; j < parm.getCount() ; j++) {
					
					if(fileN.equals(parm.getValue("FILE_NAME", j))){
						
						result.addData("FLG", "N");
						result.addData("SEALED_STATUS", parm.getValue("SEALED_STATUS",j));
						result.addData("FILE_NAME", fileN);
						result.addData("FILE_TYPE", "PDF");
						result.addData("FILE_SEQ", parm.getValue("FILE_SEQ",j));
						result.addData("FILE_PATH",  parm.getValue("FILE_PATH",j).replaceFirst("JHW", "PDF"));
						flg = false;
						
						names.add(parm.getValue("FILE_NAME", j));
						break;
					}
					
				}
				
//				System.out.println("result1=="+result);
				
				if(flg){
					
					String sealedStatus = "1";
					
					for (int k = 0; k < fParm.getCount("FLG"); k++) {
						
						if(fileN.equals(fParm.getValue("FILE_NAME", k))){
							sealedStatus = "3";
							break;
						}
						
					}

					result.addData("FLG", "N");
					result.addData("SEALED_STATUS", sealedStatus);
					result.addData("FILE_NAME", fileN);
					result.addData("FILE_TYPE", "PDF");
					result.addData("FILE_SEQ", "");
					result.addData("FILE_PATH",  parm.getValue("FILE_PATH",0).replaceFirst("JHW", "PDF"));
				}
				
			}
			
		}
		
		//��没����ԭʼ�����Ƚϣ���ʾ ���״̬
//		for (int i = 0; i < result.getCount("FLG"); i++) {
//			for (int k = 0; k < fParm.getCount("FLG"); k++) {
//				
//				if(result.getValue("FILE_NAME", i).equals(fParm.getValue("FILE_NAME", k)) &&
//						result.getValue("FILE_SEQ", i).length()==0){
//					result.setData("SEALED_STATUS", i, "3");
//					break;
//				}
//				
//			}
//		}
		
		
		TParm tablePrm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			
			if(!names.contains(parm.getValue("FILE_NAME", i))){
				
				tablePrm.addData("FLG", "N");
				tablePrm.addData("SEALED_STATUS", parm.getValue("SEALED_STATUS", i));
				tablePrm.addData("FILE_NAME", parm.getValue("FILE_NAME", i));
				tablePrm.addData("FILE_TYPE", parm.getValue("FILE_TYPE", i));
				tablePrm.addData("FILE_SEQ", parm.getValue("FILE_SEQ", i));
				tablePrm.addData("FILE_PATH", parm.getValue("FILE_PATH", i));
			}
			
			
		}
		
		for (int i = 0; i < result.getCount("FILE_NAME"); i++) {
			tablePrm.addData("FLG", "N");
			tablePrm.addData("SEALED_STATUS", result.getValue("SEALED_STATUS", i));
			tablePrm.addData("FILE_NAME", result.getValue("FILE_NAME", i));
			tablePrm.addData("FILE_TYPE", result.getValue("FILE_TYPE", i));
			tablePrm.addData("FILE_SEQ", result.getValue("FILE_SEQ", i));
			tablePrm.addData("FILE_PATH", result.getValue("FILE_PATH", i));
		}

		tablePrm.setCount(tablePrm.getCount("FILE_NAME"));
		fParm.setCount(fParm.getCount("FILE_NAME"));
		
		
		
		tableOld.setParmValue(tablePrm);
		tableSeal.setParmValue(fParm);
	}
	
	public void onAllNew(){
		tableSeal.acceptText();
		TCheckBox all = (TCheckBox) getComponent("ALL_NEW");
		
		TParm parmM = tableSeal.getParmValue();
		String flg= "N";
		if(all.isSelected()){
			flg = "Y";
		}else{
			flg = "N";
		}
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			parmM.setData("FLG", i, flg);
		}
		tableSeal.setParmValue(parmM);
		
	}
	
	public void onAllOld(){
		tableOld.acceptText();
		
		TCheckBox all = (TCheckBox) getComponent("ALL_OLD");
		
		TParm parmM = tableOld.getParmValue();
		String flg= "N";
		if(all.isSelected()){
			flg = "Y";
		}else{
			flg = "N";
		}
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			parmM.setData("FLG", i, flg);
		}
		tableOld.setParmValue(parmM);
		
	}
	
	public void onSelOld(){
		tableOld.acceptText();
		String selText = this.getValueString("SEL_TEXT");
		if(selText.length()==0){
			this.messageBox("������ɸѡ�Ĺؼ���");
			this.setValue("SEL_OLD", false);
			return;
		}
		
		TCheckBox all = (TCheckBox) getComponent("SEL_OLD");
		
		TParm parmM = tableOld.getParmValue();
		String flg= "N";
		if(all.isSelected()){
			flg = "Y";
		}else{
			flg = "N";
		}
		for (int i = 0; i < parmM.getCount("FLG"); i++) {
			
			if(parmM.getValue("FILE_NAME", i).contains(selText)){
				parmM.setData("FLG", i, flg);
			}
		}
		
		tableOld.setParmValue(parmM);

	}
	
	public void onClear(){
		clearTable();
		this.clearValue("SEALED_STATUS;MR_NO;DS_STATION_CODE;DS_STATION_CODE;S_DATE;E_DATE");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("S_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("E_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		
		this.setValue("IN", true);
	}
	
	public void clearTable(){
		table.removeRowAll();
		tableOld.removeRowAll();
		tableSeal.removeRowAll();
	}
	
	/**
	 * ��ӡ����
	 */
	public void onPrint(){
		tableSeal.acceptText();
		table.acceptText();
		if(table.getSelectedRow() < 0)
			return;
		
		TParm tableParm = tableSeal.getParmValue();
		TParm printParm = new TParm();
		for (int i = 0; i < tableParm.getCount("FLG"); i++) {
			if(tableParm.getBoolean("FLG", i)){
				printParm.addData("FILE_NAME", tableParm.getValue("FILE_NAME", i));
				printParm.addData("FILE_PATH", tableParm.getValue("FILE_PATH", i));
			}
		}
		
		if(printParm.getCount("FILE_NAME") < 0){
			this.messageBox("û��Ҫ��ӡ������");
			return;
		}
		
		for (int i = 0; i < printParm.getCount("FILE_NAME"); i++) {
			String fileName = printParm.getValue("FILE_NAME",i);

			String filePath = TConfig.getSystemValue("FileServer.Main.Root")+ "\\��没��"
					+ printParm.getValue("FILE_PATH",i) + "\\"
					+ fileName + ".pdf";

			byte data[] = TIOM_FileServer.readFile(getFileServerAddress(fileName),
					filePath);
				

			if (data == null) {
				messageBox_("��������û���ҵ��ļ� " + filePath);
			}
			try {
				FileTool.setByte(tempPath + "\\" + fileName
						+ ".pdf", data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Runtime runtime = Runtime.getRuntime();
			try {
				// ���ļ�
				runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
						+ "\\" + fileName+ ".pdf");
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		
		
		
		TParm parm = new TParm();
		parm.setData("CASE_NO", table.getItemString(table.getSelectedRow(), "CASE_NO"));
		parm.setData("SEALED_PRINT_USER", Operator.getID());
		
		TParm result = TIOM_AppServer.executeAction("action.mro.MROSealedAction",
				"updateMroMreTechSealedPrint", parm);
		
		if(result.getErrCode()< 0){
			this.messageBox("���´�ӡ��������Աʧ��");
			return;
		}
		
		this.messageBox("��ӡ�ɹ�");
		clearTable();
		this.onQuery();
		
	}
	
	/**
	 * ȡ�����
	 */
	public void onRemoveSealed(){
		tableOld.acceptText();
		table.acceptText();
		if(table.getSelectedRow() < 0)
			return;
		
		TParm tableParm = tableOld.getParmValue();
		TParm parm = new TParm();
		for (int i = 0; i < tableParm.getCount("FLG"); i++) {
			if(tableParm.getBoolean("FLG", i) && !tableParm.getValue("SEALED_STATUS", i).equals("1")){
				parm.addData("CASE_NO", table.getItemData(table.getSelectedRow(), "CASE_NO"));
				parm.addData("FILE_SEQ", tableParm.getValue("FILE_SEQ",i));
				parm.addData("SEALED_STATUS", "1");
				parm.addData("FILE_NAME", tableParm.getValue("FILE_NAME",i));
				parm.addData("FILE_PATH", tableParm.getValue("FILE_PATH",i));
			}
			
		}
		
		if(parm.getCount("CASE_NO") < 0){
			this.messageBox("û��Ҫȡ�������ļ�");
			return;
		}
		
		TParm mroParm = new TParm();
		mroParm.setData("CASE_NO", table.getItemString(table.getSelectedRow(), "CASE_NO"));
		mroParm.setData("SEALED_STATUS", "1");
		mroParm.setData("SEALED_USER", Operator.getID());
		mroParm.setData("OPT_USER", Operator.getID());
		mroParm.setData("OPT_TERM", Operator.getIP());
		mroParm.setData("emrParm", parm.getData());
		
		TParm result = TIOM_AppServer.executeAction("action.mro.MROSealedAction",
				"updateMroSealed", mroParm);
		if(result.getErrCode() < 0){
			this.messageBox("ȡ�����ʧ��");
			return;
		}
		
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			String fileName = parm.getValue("FILE_NAME",i);
			String filePath = TConfig.getSystemValue("FileServer.Main.Root")
			+ "\\��没��"
			+ parm.getValue("FILE_PATH",i).replaceFirst("PDF", "").replaceFirst("JHW", "")
			+ "\\" + fileName + ".pdf";
			
			
			try {
				if(!TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
						filePath)){
					System.out.println(filePath);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
		
		this.messageBox("ȡ�����ɹ�");
		clearTable();
		this.onQuery();

	}
	
	/**
	 * ���
	 */
	public void onUnSealed(){
		tableOld.acceptText();
		table.acceptText();
		if(table.getSelectedRow() < 0)
			return;
		
		TParm tableParm = tableOld.getParmValue();
		
		TParm parm = new TParm();
		for (int i = 0; i < tableParm.getCount("FLG"); i++) {
			
			if(tableParm.getBoolean("FLG", i) && tableParm.getValue("SEALED_STATUS", i).equals("3") &&
					tableParm.getValue("FILE_SEQ", i).length() > 0 ){
				parm.addData("CASE_NO", table.getItemData(table.getSelectedRow(), "CASE_NO"));
				parm.addData("FILE_SEQ", tableParm.getValue("FILE_SEQ",i));
				parm.addData("SEALED_STATUS", "2");
				
			}
			
		}
		
		if(parm.getCount("CASE_NO") < 0){
			this.messageBox("û��Ҫ�����ļ�");
			return;
		}
		
		TParm mroParm = new TParm();
		mroParm.setData("CASE_NO", table.getItemString(table.getSelectedRow(), "CASE_NO"));
		mroParm.setData("SEALED_STATUS", "2");
		mroParm.setData("SEALED_USER", Operator.getID());
		mroParm.setData("OPT_USER", Operator.getID());
		mroParm.setData("OPT_TERM", Operator.getIP());
		mroParm.setData("emrParm", parm.getData());
		
		TParm result = TIOM_AppServer.executeAction("action.mro.MROSealedAction",
				"updateMroSealed", mroParm);
		if(result.getErrCode() < 0){
			this.messageBox("���ʧ��");
			return;
		}
		
		this.messageBox("���ɹ�");
		clearTable();
		this.onQuery();
		
	}
	
	
	/**
	 * ��� 
	 */
	public void onSealed(){
		tableOld.acceptText();
		table.acceptText();
		if(table.getSelectedRow() < 0)
			return;
		
		int rowCount = tableOld.getRowCount();
		
		boolean pdfFlg = true;

		boolean flg = true;

		File fDir = new File(tempPath);
		if (!fDir.exists()) {
			fDir.mkdirs();
		}
		Map fileMap = new HashMap();
		this.delAllFile(tempPath);
		File file;
		Date beginDate;
		Date endDate;
		// Ĭ�ϳ�ʱʱ��Ϊ30s
		int timeOut = 30;
		String pdfTransTimeOut = TConfig.getSystemValue("PDF_TRANS_TIME_OUT");
		boolean completeFlg = false;
		int time = 0;
		double diff = 0;
		if (StringUtils.isNotEmpty(pdfTransTimeOut)) {
			timeOut = Integer.parseInt(pdfTransTimeOut);
		}
		
		TParm updateParm = new TParm();
		
		for (int i = 0; i < rowCount; i++) {
			TParm parm = tableOld.getParmValue().getRow(i);//һ����¼
			parm.setData("TEMP_PATH", tempPath);//��ʱ·��
			if (parm.getValue("FLG").equals("Y") ) {//�Ƿ�ѡ��
				
				if(parm.getValue("FILE_SEQ").length() > 0){
					updateParm.addData("CASE_NO", table.getItemData(table.getSelectedRow(), "CASE_NO"));
					updateParm.addData("FILE_SEQ", parm.getValue("FILE_SEQ"));
					updateParm.addData("SEALED_STATUS", "3");
				}

				if("JHW".equals(parm.getValue("FILE_TYPE"))){

					TWord word = new TWord();
					word.onOpen(parm.getValue("FILE_PATH"), parm//��ģ��
							.getValue("FILE_NAME")
							+ ".jhw", 3, true);
					//$$ =========add by lx 2012/08/10 ����һ�����¾�ʼ���� Start===============$$//
					TParm sexP = new TParm(TJDODBTool.getInstance().select(
							"SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"
									+ table.getParmValue().getValue("MR_NO",table.getSelectedRow()) + "'"));
					//System.out.println("===MR_NO==="+parm.getValue("MR_NO"));

					if (sexP.getInt("SEX_CODE", 0) == 9) {
						word.setSexControl(0);
					} else {
						word.setSexControl(sexP.getInt("SEX_CODE", 0));
					}				
					//$$ =========add by lx 2012/08/10 ����һ�����¾�ʼ���� end===============$$//
					// word.print();
					try {
						word.getPageManager().setOrientation(1);
						word.getPageManager().print(PrinterJob.getPrinterJob(),
								parm.getValue("FILE_NAME"));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					fileMap.put(parm.getValue("FILE_NAME"), parm);
					
					
					
					// ��ʼ����PDF����ʼʱ��
					beginDate = SystemTool.getInstance().getDate();
					completeFlg = false;
					do {
						// ���ɹ����м�����ʱ������ָ��ʱ�����϶�Ϊ�ļ�Ŀ¼�趨������ļ�������������ѭ��
						endDate = SystemTool.getInstance().getDate();
						diff = endDate.getTime() - beginDate.getTime();
						// ʵʱ����ʱ����������30s��Ϊʧ��
						time = (int) Math.floor(diff / (1000));
//						System.out.println("time---"+time);
						if (time > timeOut) {
							this.messageBox("�ļ���" + parm.getValue("FILE_NAME")
									+ "�����ɴ����ļ������³�ʱ��PDF�洢·�����ô���");
							return ;
						}
						file = new File(tempPath + "\\"
								+ parm.getValue("FILE_NAME") + ".pdf");
						if (file.exists()) {
							completeFlg = true;
						}
					} while (!completeFlg);
					
					fDir.deleteOnExit();
					
					
				}
				
				if("PDF".equals(parm.getValue("FILE_TYPE"))){
					String fileName = parm.getValue("FILE_NAME");
					String serverPath = getEmrDataDir(fileName) + "\\"
					+ parm.getValue("FILE_PATH")+"\\"+fileName+".pdf";
					
					String filePath = TConfig.getSystemValue("FileServer.Main.Root")
					+ "\\��没��"
					+ parm.getValue("FILE_PATH").replaceFirst("PDF", "")
					+ "\\" + fileName + ".pdf";
					
					
					try {
						TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
								filePath);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
					byte data[] = TIOM_FileServer.readFile(getFileServerAddress(fileName),
							serverPath );
					
					if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
							filePath, data)) {
						this.messageBox(fileName+".pdf�ļ���没��ʧ��");
						pdfFlg = false;
						break;
					}
					
					
					
					
				}
				
			}
			
			
		}
		if(pdfFlg){
			TWord word = new TWord();
			word.print();
			flg = onUpdate(fileMap);

			if (flg) {
				
				TParm mroParm = new TParm();
				mroParm.setData("CASE_NO", table.getItemString(table.getSelectedRow(), "CASE_NO"));
				mroParm.setData("SEALED_STATUS", "3");
				mroParm.setData("SEALED_USER", Operator.getID());
				mroParm.setData("OPT_USER", Operator.getID());
				mroParm.setData("OPT_TERM", Operator.getIP());
				mroParm.setData("emrParm", updateParm.getData());
				
				TParm result = TIOM_AppServer.executeAction("action.mro.MROSealedAction",
						"updateMroSealed", mroParm);
				if(result.getErrCode() < 0){
					this.messageBox("����ʧ��");
					return;
				}
				
				this.messageBox("���ɹ�");
				clearTable();
				this.onQuery();

			}else{
				this.messageBox("���ʧ��");
				
			}
			
		}
		
		
		
	}
	
	/**
	 * ��¼����
	 */
	public void onRecord(){
		table.acceptText();
		if(table.getSelectedRow() < 0)
			return;
		
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		this.openDialog(
				"%ROOT%\\config\\mro\\MROSealedRecord.x",parm);
		
		
	}
	
	public void onSealedLog(){
		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		
		table.acceptText();
		if(table.getSelectedRow()>0){
			parm.setData("MR_NO", table.getItemString(table.getSelectedRow(), "MR_NO"));
		}
		
		
		this.openDialog(
				"%ROOT%\\config\\mro\\MROSealedLog.x",parm);
	}
	
	
	/**
	 * �ϴ�����
	 */
	public boolean onUpdate(Map fileMap) {
		//System.out.println("----tempPath-----"+tempPath);
		List fileList = this.getAllFile(tempPath);
		//System.out.println("-----fileList size------"+fileList.size());
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) fileList.get(i);
			TParm parm = (TParm) fileMap.get(f.getName().split("\\.")[0]);
			if (parm == null) {
				continue;
			}
			String fileName = parm.getValue("FILE_NAME");
			String filePath = TConfig.getSystemValue("FileServer.Main.Root")
					+ "\\��没��"
					+ parm.getValue("FILE_PATH").replaceFirst("JHW", "")
					+ "\\" + fileName + ".pdf";
			//System.out.println("------fileName------"+fileName);
			//System.out.println("------filePath------"+filePath);
			try {
				TIOM_FileServer.deleteFile(TIOM_FileServer.getSocket(),
						filePath);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				byte data[] = FileTool.getByte(f);

				if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),
						filePath, data)) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}
	
	
	public static List getAllFile(String path) {
		List fileList = new ArrayList();
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		if (!file.isDirectory()) {
			return null;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				fileList.add(temp);
			}
		}
		return fileList;
	}


	
	
	
	// ɾ��ָ���ļ����������ļ�
	// param path �ļ�����������·��
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}
	
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	 /**
     * 
     * @param fileName
     * @return
     */
    public String getEmrDataDir(String fileName){
    	//
    	String strEmrDataDir=TIOM_FileServer.getRoot() + TIOM_FileServer.getPath("EmrData");
    	// 1.�ļ������ǿմ������
		if (fileName != null && !fileName.equals("")) {
			// 2.����-�����
			if (fileName.indexOf("_") != -1) {
				// 3.ȡ��һ��caseNo�µ����ǰ2λ�����
				String sYear = fileName.substring(0, 2);
				//System.out.println("---sYear��---" + sYear);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		        String root = config.getString("","FileServer." + sYear + ".Root");
		        if (root != null && !root.equals("")) {
				// 4.��ָ���������ļ����������
		        	strEmrDataDir = TIOM_FileServer.getRoot(sYear)+ TIOM_FileServer.getPath("EmrData");
		        }
				
			}
		}
    	
    	return strEmrDataDir;
    }
    
    /**
	 * ͨ���ļ�����ȷ���ļ��洢λ��
	 * 
	 * @param fileName
	 * @return
	 */
	public TSocket getFileServerAddress(String fileName) {
		System.out.println("=====fileName:====="+fileName);
		//Ĭ���ļ�����
		TSocket tsocket = TIOM_FileServer.getSocket("Main");
		// 1.�ļ������ǿմ������
		if (fileName != null && !fileName.equals("")) {
			// 2.����-�����
			if (fileName.indexOf("_") != -1) {
				// 3.ȡ��һ��caseNo�µ����ǰ2λ�����
				String sYear = fileName.substring(0, 2);
				System.out.println("---sYear��---" + sYear);
				TConfig config = TConfig.getConfig("WEB-INF/config/system/TConfig.x");
		        String ip = config.getString("","FileServer." + sYear + ".IP");
		        System.out.println("====IP��======"+ip);
		        if (ip != null && !ip.equals("")) {
				// 4.��ָ���������ļ����������
		        	tsocket = TIOM_FileServer.getSocket(sYear);
		        }
				
			}
		}
		//   	
		return tsocket;
	}

}
