package com.javahis.ui.emr;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.sql.Timestamp;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import jdo.emr.EMRCdrTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.javahis.util.OdiUtil;

/**
 * סԺ���ξ���
 * @author Administrator
 *
 */
public class EMRCdrInpMedRecordControl extends TControl{
	private String caseNo="";//�����
	private String mrNo="";//������
	TTable table1;
	TTable table2;
	TTable table3;
	TTable table4;
	TTable table5;
	TTable table6;
	TTable table7;
	TTable table8;
	TTable table9;
	TTable table10;
	TTable table11;
	Window window=null;
	String tempPath;
	public void onInit() {
		/*TLabel LOGO = (TLabel) this.getComponent("LOGO");
		LOGO.setIcon(createImageIcon("cdrlogo.png"));*/
		tempPath = "C:\\JavaHisFile\\temp\\pdf";
		
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		
		intPage();
		table7.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "selectCheckBox");//����--������ҩ �����м໤�����Ĳ�����������������Ų�ѯ,���ܻ��ж���������棩
		table4.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "eventCheckBox");//���鱨��
		table5.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "OpenRisWeb");//���--����Ӱ��
		table11.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "seePdfWord");//��������--���Ĳ���������case_no��ѯ��
		table10.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "seeConsultReport");//���Ļ��ﱨ��
		
		callFunction("UI|TABLE4|addEventListener",
                "TABLE4->" + TTableEvent.CLICKED, this, "onTable4Click");
		callFunction("UI|TABLE5|addEventListener",
                "TABLE5->" + TTableEvent.CLICKED, this, "onTable5Click");
		callFunction("UI|TABLE7|addEventListener",
                "TABLE7->" + TTableEvent.CLICKED, this, "onTable7Click");
		
		//����ҳ�洹ֱ������ start
		TPanel tPanel0 = (TPanel) this.getComponent("tPanel_0");
		TPanel tPanel1 = (TPanel) this.getComponent("tPanel_1");
		JScrollPane scrollPane = new JScrollPane(tPanel1);
		   int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
	        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		scrollPane.setBounds(0, 0,screenWidth-40,screenHeight);
		tPanel1.setPreferredSize(new Dimension(scrollPane.getWidth(),
				scrollPane.getHeight() +900));

		tPanel0.add(scrollPane);
		tPanel1.revalidate();
		//����ҳ�洹ֱ������ end
		
	}
	
	
	/**
	 * ��ʼ��ҳ��
	 */
	public void intPage(){
		Object obj=this.getParameter();
		if(obj instanceof TParm){
			TParm parm =(TParm) obj;
			caseNo=parm.getValue("CASE_NO");
			mrNo=parm.getValue("MR_NO");
		}
		table1=(TTable) this.getComponent("TABLE1");
		table2=(TTable) this.getComponent("TABLE2");
		table3=(TTable) this.getComponent("TABLE3");
		table4=(TTable) this.getComponent("TABLE4");
		table5=(TTable) this.getComponent("TABLE5");
		table6=(TTable) this.getComponent("TABLE6");
		table7=(TTable) this.getComponent("TABLE7");
		table8=(TTable) this.getComponent("TABLE8");
		table9=(TTable) this.getComponent("TABLE9");
		table10=(TTable) this.getComponent("TABLE10");
		table11=(TTable) this.getComponent("TABLE11");
		/*mrNo="000000378907";
		caseNo="120607000543";*/
		getPatInfo(mrNo);//��ȡ������Ϣ
		getAdmInfo(caseNo);//��ȡ������Ϣ
		
		getTable1Data(mrNo);//��������"000000332282"
		getTable2Data(caseNo);//"120331000500"
		getTable3Data(caseNo);//"120401000090"
		getTable4Data(caseNo);//"120405000276"
		getTable5Data(caseNo);//"120405000379"
		getTable6Data(caseNo);//"120408000004"
		getTable7Data(caseNo);//"120607000543"
		getTable8Data(caseNo);//"120401000194"
		getTable9Data(caseNo);//"120507000528"
		getTable10Data(caseNo);//"141218000705"
		getTable11Data(caseNo);//"120405000180"
	}
	
	/**
	 * ��ȡ������Ϣ
	 */
	public void getPatInfo(String mrNo){
		TParm param = new TParm();
		param.setData("MR_NO",mrNo);
		TParm parm=EMRCdrTool.getInstance().getPatInfo(param);
		this.getLabel("PAT_NAME").setText(parm.getValue("PAT_NAME",0));
		this.getLabel("SEX_CODE").setText(parm.getValue("SEX_CODE",0));
		this.getLabel("AGE").setText(patAge(parm.getTimestamp("BIRTH_DATE",0)));
		this.getLabel("SEX_CODE").setText(parm.getValue("SEX_CODE",0));
		this.getLabel("CASE_NO").setText(caseNo);
		
	}
	
	/**
	 * ��ȡ������Ϣ
	 * @param caseNo
	 */
	public void getAdmInfo(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm=EMRCdrTool.getInstance().getAdmInfo(param);
		this.getLabel("ADM_DATE").setText(parm.getValue("ADM_DATE",0).toString().substring(0,10));
		this.getLabel("ADM_TYPE").setText(parm.getValue("ADM_TYPE",0));
		this.getLabel("DEPT_DESC").setText(parm.getValue("DEPT_DESC",0));
		this.getLabel("CLINICAREA_DESC").setText(parm.getValue("STATION_DESC",0));
		this.getLabel("BED_NO").setText(parm.getValue("BED_NO",0));
		this.getLabel("VS_DR_NAME").setText(parm.getValue("VS_DR_NAME",0));
		this.getLabel("SERVICE_LEVEL").setText("����ȼ�:"+parm.getValue("SERVICE_LEVEL",0));
		this.getLabel("DIAGNOSIS").setText(parm.getValue("DIAG_CODE",0)+" "+parm.getValue("DIAG_DESC",0));
		this.getLabel("CLNCPATH_DESC").setText(parm.getValue("CLNCPATH_DESC",0));
		if(parm.getValue("DISCHARGE_DATE",0)!=null && parm.getValue("DISCHARGE_DATE",0).toString().length()>0){
			this.getLabel("DISCHARGE_DATE").setText("��Ժ:"+parm.getValue("DISCHARGE_DATE",0).toString().substring(0,19));
			this.getLabel("ADM_TYPE").setText("��Ժ");
		}else{
			this.getLabel("ADM_TYPE").setText("��Ժ");
		}
	}
	/**
	 * ��ȡtable1���ݣ���ʷ--����ʷ��
	 */
	public void getTable1Data(String mrNo) {
		TParm param = new TParm();
		param.setData("MR_NO",mrNo);
		TParm parm = EMRCdrTool.getInstance().getAllergy(param);
		table1.setParmValue(parm);
	}
	/**
	 * ��ȡtable2�����ݣ���ϣ�
	 * @param caseNo
	 */
	public void getTable2Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getDiagnosisData(param);
		table2.setParmValue(parm);
	}
	
	/**
	 * ��ȡtable3�����ݣ�ҩ����
	 * @param caseNo
	 */
	public void getTable3Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getMedData(param);
		table3.setParmValue(parm);
	}
	/**
	 * ��ȡtable4�����ݣ����飩
	 * @param caseNo
	 */
	public void getTable4Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getLisData(param);
		table4.setParmValue(parm);
		/*for(int i=0;i<table4.getParmValue().getCount();i++){
			if(!table4.getParmValue().getValue("STATUS",i).equals("�������")){
				table4.setLockCell(i, "LIS_WORD", true);
			}
		}*/
	}
	/**
	 * ��ȡtable5�����ݣ���飩
	 * @param caseNo
	 */
	public void getTable5Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getExmData(param);
		table5.setParmValue(parm);
	}
	
	/**
	 * ��ȡtable6�����ݣ�����������
	 * @param caseNo
	 */
	public void getTable6Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getLifeData(param);
		table6.setParmValue(parm);
	}
	
	
	/**
	 * ��ȡtable7�����ݣ�������
	 * @param caseNo
	 */
	public void getTable7Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getOpeData(param);
		table7.setParmValue(parm);
	}
	
	/**
	 * ��ȡtable8�����ݣ����ƣ�
	 * @param caseNo
	 */
	public void getTable8Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getOthData(param);
		table8.setParmValue(parm);
	}
	/**
	 * ��ȡtable8�����ݣ���Ѫ��
	 * @param caseNo
	 */
	public void getTable9Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getBmsData(param);
		table9.setParmValue(parm);
	}
	/**
	 * ���table10�����ݣ����
	 * @param caseNo
	 */
	public void getTable10Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getConsult(param);
		table10.setParmValue(parm);
	}
	/**
	 * ��������
	 * @param caseNo
	 */
	public void getTable11Data(String caseNo){
		TParm param = new TParm();
		param.setData("CASE_NO",caseNo);
		TParm parm = EMRCdrTool.getInstance().getFileData(param);
		table11.setParmValue(parm);
	}
	/**
	 * ���鱨�����ݹ�ѡ����
	 */
	public void eventCheckBox(Object obj){
		TTable table=(TTable) obj;
		table.acceptText();
		TParm tableParm=table.getParmValue();
		int row=table.getSelectedRow();
		if(window!=null){//��������֮ǰҪ �ȹر�֮ǰ�����Ĵ���
			window.dispose();
		}
		TParm parm = new TParm ();
		if("Y".equals(tableParm.getValue("LIS_WORD",row))){
			for(int i = 0;i<tableParm.getCount();i++){//�������checkBoxʱ����֮ǰ�Ĺ��ÿ�
				if(tableParm.getValue("LIS_WORD", i).equals("Y")&&i!=row){
					table.setItem(i, "LIS_WORD", "N");
				}
			}
			parm.setData("CAT1_TYPE",tableParm.getValue("CAT1_TYPE",row));
			parm.setData("APPLY_NO",tableParm.getValue("APPLY_NO",row));
			parm.setData("LAB_TYPE",tableParm.getValue("LAB_TYPE",row));
			window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRLisReport.x", parm);
			//table.setItem(row, "LIS_WORD", "N");
		}
		 window.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width-window.getWidth(), 80);//���õ������ڵ�λ�� ���ڵ�λ���ǿ����Ͻ�
		// AWTUtilities.setWindowOpacity(window, 0.9f);
	}
	/**
	 
     * ��̩��RIS����(����Ӱ��)
 
     * @param mrNo String
 
     */
 
    public void OpenRisWeb(Object obj) {
        TTable table=(TTable) obj;
		table.acceptText();
		TParm tableParm=table.getParmValue();
		int row=table.getSelectedRow();
		TParm parm = new TParm();
		int column = table.getSelectedColumn();
		if(column == 9 && "Y".equals(tableParm.getValue("SEEIMAGE",row))){
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("SEEIMAGE", i).equals("Y")){
					table.setItem(i,"SEEIMAGE","N");//���������
				}
				if(tableParm.getValue("RIS_REPORT", i).equals("Y")){
					table.setItem(i,"RIS_REPORT","N");//���������
				}
			}
			if("Y".equals(tableParm.getValue("IS_PACS",row))){//�ǵ����� ����Ӱ��
            	SystemTool.getInstance().OpenRisByMrNoAndApplyNo(this.mrNo,tableParm.getValue("APPLY_NO", row));
			}else{//���������pdf �ļ�
				
				parm.setData("CASE_NO",tableParm.getValue("CASE_NO", row));
				parm.setData("OPE_BOOK_NO",tableParm.getValue("APPLY_NO", row));
				TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
				TParm fileParm = new TParm();
				for (int i = 0; i < pathData.getCount(); i++) {
					String fileName = pathData.getValue("FILE_NAME", i) + ".pdf";
					String filePath = pathData.getValue("FILE_PATH", i);
					parm.setData("FILE_NAME", fileName);
					Runtime runtime = Runtime.getRuntime();
					// ���ķֲ�ʽ�洢����
					fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
					byte data[] = (byte[]) fileParm.getData("FILE_DATA");
					if (data == null) {
						messageBox_("��������û���ҵ��ļ� " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
						return;
					}
					try {
						FileTool.setByte(tempPath + "\\" + fileName, data);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					try {
						// ���ļ�
						runtime.exec("rundll32 url.dll FileProtocolHandler "
								+ tempPath + "\\" + fileName);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				table.setItem(row, "RIS_REPORT", "N");
			}
			return;
		}
		if(window!=null){//���´���ʱ���ȹر������ĵ�������
			window.dispose();
		}
		if(column == 7 && "Y".equals(tableParm.getValue("RIS_REPORT", row))){
			for(int i = 0;i<tableParm.getCount();i++){//�������checkBoxʱ����֮ǰ�Ĺ��ÿ�
				if(tableParm.getValue("RIS_REPORT", i).equals("Y")&&i!=row){
					table.setItem(i, "RIS_REPORT", "N");
				}
				if(tableParm.getValue("SEEIMAGE", i).equals("Y")){
					table.setItem(i,"SEEIMAGE","N");//���������
				}
			}
			parm.setData("OUTCOME_DESCRIBE",tableParm.getValue("OUTCOME_DESCRIBE", row));
			parm.setData("OUTCOME_CONCLUSION",tableParm.getValue("OUTCOME_CONCLUSION", row));
			parm.setData("OUTCOME_TYPE",tableParm.getValue("OUTCOME_TYPE", row));
			parm.setData("APPLICATION_NO",tableParm.getValue("APPLY_NO", row));
			window = (Window) this.openWindow("%ROOT%\\config\\emr\\EMRRisReport.x",parm);
		}
		window.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width-window.getWidth(), 80);//���õ������ڵ�λ�� ���ڵ�λ���ǿ����Ͻ�
		 //AWTUtilities.setWindowOpacity(window, 0.9f);
    }
	/**
	 * ���� ���--������ҩ �����м໤ �����Ĳ���  ��ѡ�¼�
	 */
	public void selectCheckBox(Object obj){
		TTable table=(TTable) obj;
		table.acceptText();
		TParm tableParm=table.getParmValue();
		int row=table.getSelectedRow();
		TParm parm = new TParm ();
		if(window!=null){//������������ʱ��Ҫ�ȹر���ǰ�����Ĵ���
			window.dispose();
		}
		int column = table.getSelectedColumn();
		if(column == 13 && "Y".equals(tableParm.getValue("OPEING",row))){
			for(int i = 0;i<tableParm.getCount();i++){//�������checkBoxʱ����֮ǰ�Ĺ��ÿ�
				if(tableParm.getValue("OPEING", i).equals("Y")&&i!=row){
					table.setItem(i, "OPEING", "N");
				}
				if(tableParm.getValue("WORD", i).equals("Y")){
					table.setItem(i,"WORD","N");//���������
				}
			}
			parm.setData("APPLY_NO",tableParm.getValue("APPLY_NO",row));
			parm.setData("ADM_TYPE","I");
			window = (Window) this.openWindow("%ROOT%\\config\\emr\\EMROpeLisener.x", parm);
			window.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width-window.getWidth(), 80);//���õ������ڵ�λ�� ���ڵ�λ���ǿ����Ͻ�
			 //AWTUtilities.setWindowOpacity(window, 0.9f);
			//table.setItem(row, "OPEING", "N");
		}else if(column == 14 && "Y".equals(tableParm.getValue("WORD",row))){
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("WORD", i).equals("Y")){
					table.setItem(i,"WORD","N");//���������
				}
				
				if(tableParm.getValue("OPEING", i).equals("Y")){
					table.setItem(i, "OPEING", "N");
				}
			}
			parm.setData("CASE_NO",this.caseNo);
			parm.setData("OPE_BOOK_NO",tableParm.getValue("APPLY_NO",row));
			TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
			TParm fileParm = new TParm();
			for(int i=0;i<pathData.getCount();i++){
				String fileName=pathData.getValue("FILE_NAME",i)+".pdf";
				String filePath=pathData.getValue("FILE_PATH",i);
				parm.setData("FILE_NAME", fileName);
				Runtime runtime = Runtime.getRuntime();
				// ���ķֲ�ʽ�洢����
				fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
				byte data[] = (byte[]) fileParm.getData("FILE_DATA");
				if (data == null) {
					messageBox_("��������û���ҵ��ļ� " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
					return;
				}
				try {
					FileTool.setByte(tempPath + "\\" + fileName, data);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}

				try {
					// ���ļ�
					runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
							+ "\\" + fileName);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
		
	}
	/**
	 * ��������--���Ĳ����¼�
	 * @param obj
	 */
	public void seePdfWord(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		if ("Y".equals(tableParm.getValue("WORD", row))) {
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("WORD", i).equals("Y")){
					table.setItem(i,"WORD","N");//���������
				}
			}
			String fileName = tableParm.getValue("FILE_NAME", row) + ".pdf";
			String filePath = tableParm.getValue("FILE_PATH", row);
			Runtime runtime = Runtime.getRuntime();
			// ���ķֲ�ʽ�洢����
			TParm fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
			byte data[] = (byte[]) fileParm.getData("FILE_DATA");
			if (data == null) {
				messageBox_("��������û���ҵ��ļ� " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
				return;
			}
			try {
				FileTool.setByte(tempPath + "\\" + fileName, data);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			try {
				// ���ļ�
				runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
						+ "\\" + fileName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}
	
	/**
	 * 
	 * ���Ļ��ﱨ��
	 * @param obj
	 */
	public void seeConsultReport(Object obj){
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		TParm parm = new TParm();
		if ("Y".equals(tableParm.getValue("WORD", row))) {
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("WORD", i).equals("Y")){
					table.setItem(i,"WORD","N");//���������
				}
			}
			
			parm.setData("OPE_BOOK_NO",tableParm.getValue("CONSULT_NO",row));
			TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
			TParm fileParm = new TParm();
			for(int i=0;i<pathData.getCount();i++){
				String fileName=pathData.getValue("FILE_NAME",i)+".pdf";
				String filePath=pathData.getValue("FILE_PATH",i);
				parm.setData("FILE_NAME", fileName);
				Runtime runtime = Runtime.getRuntime();
				// ���ķֲ�ʽ�洢����
				fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
				byte data[] = (byte[]) fileParm.getData("FILE_DATA");
				if (data == null) {
					messageBox_("��������û���ҵ��ļ� " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
					return;
				}
				try {
					FileTool.setByte(tempPath + "\\" + fileName, data);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}

				try {
					// ���ļ�
					runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
							+ "\\" + fileName);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
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
	 * ��ȡTLabel ��ǩ�ؼ�
	 * @param tagName
	 * @return
	 */
	public TLabel getLabel(String tagName){
		return (TLabel)this.getComponent(tagName);
	}
	
	
	/**
     * ����ͼƬ
     * @param filename String
     * @return ImageIcon
     */
	private ImageIcon createImageIcon(String filename) {
		if (TIOM_AppServer.SOCKET != null)
			return TIOM_AppServer.getImage("%ROOT%\\image\\ImageIcon\\"
					+ filename);
		ImageIcon icon = FileTool.getImage("image/ImageIcon/" + filename);
		if (icon != null)
			return icon;
		String path = "/image/ImageIcon/" + filename;
		try {
			icon = new ImageIcon(getClass().getResource(path));
		} catch (NullPointerException e) {
			err("û���ҵ�ͼ��" + path);
		}
		return icon;
	}
	/**
	 * ���ذ�ť�¼�
	 */
	public void closeWindow(){
		this.onClosing();
	}
	
	@Override//��д�رմ��ڷ���
	public boolean onClosing() {
		if(window!=null){//�ر������棬ҲҪ�ر� �����Ĵ���
			window.dispose();
		}
		return super.onClosing();
	}
	
	/**
	 * ���������¼�
	 */
	public void onTable4Click(int row){
		if(window!=null){
			window.dispose();
		}
	}
	/**
	 * ��������¼�
	 */
	public void onTable5Click(int row){
		if(window!=null){
			window.dispose();
		}
	}
	/**
	 * ����������¼�
	 */
	public void onTable7Click(int row){
		if(window!=null){
			window.dispose();
		}
	}
	
	/**
	 * ���Ļ����
	 */
	public void seeNisReport(){
		SystemTool.getInstance().OpeNisFormList(caseNo, mrNo);
	}
}
                                                  