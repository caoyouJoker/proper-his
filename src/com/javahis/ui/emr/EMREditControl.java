package com.javahis.ui.emr;

import java.awt.Image;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.emr.EMRCreateXMLTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ESign;
import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.MModifyNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TList;
import com.javahis.util.ClipboardTool;
import com.sun.awt.AWTUtilities;

/**
 * ���̼�¼�༭��
 * 
 * @author lix@bluecore.com.cn
 *
 */
public class EMREditControl  extends TControl implements DMessageIO{
	/**
	 * ���Կ���
	 */
	private boolean isDebug=false;
	
	/**
	 * ����������
	 */
	private static final String actionName = "action.odi.ODIAction";
	/**
	 * WORD�ؼ�
	 */
	private static final String TWORD = "WORD";
	/**
	 * ������ͼģʽ
	 */
	private static final String NEW_MODE="NEW";
	/**
	 * �༭��ͼģʽ
	 */
	private static final String EDIT_MODE="EDIT";
	
	/**
	 * �ճ�������ʷ��¼ģ��
	 */
	private static final String SUBCLASS_CODE="EMR10001701";
	private static final String CLASS_CODE="EMR100017";
	/**
	 * ���
	 */
	TParm inParm;
	
	/**
	 * WORD����
	 */
	private TWord word;
	TParm emrParm = new TParm();
	
	//�������;
	private String captureName="";
	private String viewMode="";
	private String refFileName="";
	private String caseNo;
	private String mrNo;
	private String ipdNo;
	private String admYear;
	private String admMouth;
	private String patName;
	/**
	 * ���ÿ���
	 */
	private String deptCode;
	
	/**
	 * ����ҽʦ
	 */
	private String vsDrCode;
	/**
	 * ����ҽʦ
	 */

	private String attendDrCode;
	/**
	 * ������
	 */
	private String directorDrCode;
	
	/**
	 * 
	 * @return
	 */
	public String getVsDrCode() {
		return vsDrCode;
	}

	public void setVsDrCode(String vsDrCode) {
		this.vsDrCode = vsDrCode;
	}

	public String getAttendDrCode() {
		return attendDrCode;
	}

	public void setAttendDrCode(String attendDrCode) {
		this.attendDrCode = attendDrCode;
	}

	public String getDirectorDrCode() {
		return directorDrCode;
	}

	public void setDirectorDrCode(String directorDrCode) {
		this.directorDrCode = directorDrCode;
	}

	public void onInit() {
		super.onInit();		
		initWord();
		inParm= (TParm)this.getParameter();
		initPage();
		isCheckUserDr();
		// ��������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/��½
/*		if (this.isCheckUserDr() || this.isDutyDrList()) {
			TParm emrParm = new TParm();
			emrParm.setData("FILE_SEQ", "0");
			emrParm.setData("FILE_NAME", "");
			TParm result = OptLogTool.getInstance().writeOptLog(
					inParm, "L", emrParm);

		}
		//
		// ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/����
		else {
			TParm emrParm = new TParm();
			emrParm.setData("FILE_SEQ", "0");
			emrParm.setData("FILE_NAME", "");
			TParm result = OptLogTool.getInstance().writeOptLog(
					inParm, "R", emrParm);
		}*/
		//
		this.getWord().getPM().setUser(Operator.getID(), Operator.getName());
		if(inParm.getValue("AUTO_SIGN").equals("YES")){
			this.onSignOp(false) ;
		}
	}	
	
	/**
	 * ��ʼ������
	 */
	public void initPage() {
		//�õ������
		captureName=inParm.getValue("CAPTURE_NAME");
		this.setViewMode(inParm.getValue("MODE"));
		this.setCaseNo(inParm.getValue("CASENO"));
		this.setMrNo(inParm.getValue("MRNO"));
		this.setIpdNo(inParm.getValue("IPDNO"));
		this.setAdmMouth(inParm.getValue("ADM_MOUTH"));
		this.setAdmYear(inParm.getValue("ADM_YEAR"));
		this.setPatName(inParm.getValue("PAT_NAME"));
		this.setDeptCode(inParm.getValue("DEPT_CODE"));
		
		
		refFileName=inParm.getValue("REF_FILE_NAME");
		if(isDebug){
			System.out.println("==captureName=="+captureName);
			System.out.println("==viewMode=="+this.getViewMode());
			System.out.println("==refFileName=="+refFileName);
		}
		//����
		if(this.getViewMode().equals(NEW_MODE)){
			String sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE,REF_FLG FROM EMR_TEMPLET WHERE CLASS_CODE='"
					+ CLASS_CODE
					+ "' AND SEQ=1"
					+ " AND SUBCLASS_CODE='"
					+ SUBCLASS_CODE + "'";
			TParm currParm = new TParm();
			//System.out.println("=====sql11111111111111====="+sql);
			TParm parm = new TParm(this.getDBTool().select(sql));			
			currParm=parm.getRow(0);
			//
			String templetPath = currParm.getValue("TEMPLET_PATH");
			//this.messageBox("templetPath" + templetPath);
			String templetName = currParm.getValue("EMT_FILENAME");
			try {
				this.getWord().onOpen(templetPath, templetName, 2, false);
			} catch (Exception e) {
				System.out.println("--------�½���ģ���ļ�����------------");
			}
			this.setEmrParm(currParm);
			
		//�򿪱༭	
		}else if(this.getViewMode().equals(EDIT_MODE)){
			if(isDebug){
				System.out.println("==========�������ļ�==============");
			}
			String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,"
				+ " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,"
				+ " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3,"
				+ " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE,"
				+ " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG,A.REF_FLG "
			+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.FILE_NAME='"
			+ refFileName
			+ "' AND A.DISPOSAC_FLG<>'Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE(+) "
			+ " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
				TParm currParm = new TParm();
				System.out.println("=====sql2222222222222222222222222222222====="+sql);
				TParm parm = new TParm(this.getDBTool().select(sql));			
				currParm=parm.getRow(0);
			
				this.setEmrParm(currParm);
			// �򿪲���
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			
			
		}

		TParm allParm = new TParm();
		//allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		//allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
//		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this,"onMouseRightPressed");
//
		this.getWord().setWordParameter(allParm);
		//
		//�ж��Ƿ���Ա༭
		//TODO
		if(this.onEdit()){
			// �ɱ༭
			this.getWord().setCanEdit(true);
			// �༭״̬(������)
			this.getWord().onEditWord();
		}
				
	}
	
	/**
	 * ��ʼ��WORD
	 */
	public void initWord() {
		//��ȡ�ؼ�
		word = this.getTWord(TWORD);
		//
		this.setWord(word);
		// ����
		this.getWord().setFontComboTag("ModifyFontCombo");
		// ����
		this.getWord().setFontSizeComboTag("ModifyFontSizeCombo");

		// ���
		this.getWord().setFontBoldButtonTag("FontBMenu");

		// б��
		this.getWord().setFontItalicButtonTag("FontIMenu");

	}


	public TWord getWord() {
		return word;
	}


	public void setWord(TWord word) {
		this.word = word;
	}
	
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}
	
	
	/**
	 * ����
	 */
	public void onSave() {
		if(isDebug){
			System.out.println("====EMREditControl onSave start=====");
		}
		onSaveEmr(true,true);
	}
	
	/**
	 * ���没��
	 * @param isShow  �Ƿ���ʾ��ʾ��
	 * @return
	 */
	private void onSaveEmr(boolean isShow,boolean isClose) {
		
		TParm asSaveParm=null;
		// ����
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy��MM��dd�� HHʱmm��ss��");
		if ("NEW".equals(this.getViewMode())) {
			this.setEmrParm(this.getFileServerEmrName());
			// ��浽�û��ļ�
			asSaveParm = this.getEmrParm();
			//
		    //System.out.println("==asSaveParm=="+asSaveParm);
		    //
			this.getWord().setMessageBoxSwitch(false);
			this.getWord().setFileAuthor(Operator.getID());
			// ��˾
			this.getWord().setFileCo("JAVAHIS");
			// ����
			this.getWord().setFileTitle(asSaveParm.getValue("DESIGN_NAME"));
			// ��ע
			this.getWord().setFileRemark(
					asSaveParm.getValue("CLASS_CODE") + "|"
							+ asSaveParm.getValue("FILE_PATH") + "|"
							+ asSaveParm.getValue("FILE_NAME"));
			// ����ʱ��
			this.getWord().setFileCreateDate(dateStr);
			// ����޸���
			this.getWord().setFileLastEditUser(Operator.getID());
			// ����޸�����
			this.getWord().setFileLastEditDate(dateStr);
			// ����޸�IP
			this.getWord().setFileLastEditIP(Operator.getIP());
			/*System.out.println("==save filePath=="
					+ asSaveParm.getValue("FILE_PATH"));
			System.out.println("==save fileName=="
					+ asSaveParm.getValue("FILE_NAME"));*/
			// ���Ϊ
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);

			EMRCreateXMLTool.getInstance()
					.createXML(asSaveParm.getValue("FILE_PATH"),
							asSaveParm.getValue("FILE_NAME"), "EmrData",
							this.getWord());
			
			if (!success) {
				// �ļ��������쳣
				this.messageBox("E0103");
				this.getWord().setMessageBoxSwitch(true);
				//return false;
				return;
		    
			//�ļ�����ɹ�.
			}
			this.getWord().setMessageBoxSwitch(true);
			
			
			// �������ݿ�����
			if (saveEmrFile(asSaveParm)) {
				if (isShow) {
					// ����ɹ�
					this.messageBox("P0001");
				}
			}else {
				if (isShow) {
					// ����ʧ��
					this.messageBox("E0001");
				}
				//return false;
				return;
			}
		
		}
		
		if ("EDIT".equals(this.getViewMode())) {
			asSaveParm = this.getEmrParm();
			
			//System.out.println("-------asSaveParm-----"+asSaveParm);
			// ������ʾ������
			this.getWord().setMessageBoxSwitch(false);
			// ����޸���
			this.getWord().setFileLastEditUser(Operator.getID());
			// ����޸�����
			this.getWord().setFileLastEditDate(dateStr);
			// ����޸�IP
			this.getWord().setFileLastEditIP(Operator.getIP());
			
			// ����
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);
			EMRCreateXMLTool.getInstance()
					.createXML(asSaveParm.getValue("FILE_PATH"),
							asSaveParm.getValue("FILE_NAME"), "EmrData",
							this.getWord());
			
			if (!success) {
				// �ļ��������쳣
				this.messageBox("E0103");
				// ������ʾ��Ϊ����ʾ
				this.getWord().setMessageBoxSwitch(true);
				return;
			}
			this.getWord().setMessageBoxSwitch(true);
			// ��������
			if (saveEmrFile(asSaveParm)) {
				if (isShow) {
					// ����ɹ�
					this.messageBox("P0001");
				}
			} else {
				if (isShow) {
					// ����ʧ��
					this.messageBox("E0001");
				}
				return;
			}
			
		}
		if(isClose){		
			inParm.runListener("onReturnEMRContent",asSaveParm.getValue("FILE_NAME"),this.captureName);		
		    this.closeWindow();
		}
		//return true;		
	}
	
	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}


	public TParm getEmrParm() {
		return emrParm;
	}


	public void setEmrParm(TParm emrParm) {
		this.emrParm = emrParm;
	}	
	
	/**
	 * �õ��ļ�������·��
	 * 
	 * @param rootPath
	 *            String
	 * @param fileServerPath
	 *            String
	 * @return String
	 */
	public TParm getFileServerEmrName() {
		TParm emrParm = new TParm();
		String emrName = "";
		TParm childParm = this.getEmrParm();
		String templetName = childParm.getValue("EMT_FILENAME");
		TParm action = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"
										+ this.getCaseNo() + "'"));
		int index = action.getInt("MAXFILENO", 0);
		emrName = this.getCaseNo() + "_" + templetName + "_" + index;
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		emrParm.setData("FILE_SEQ", index);
		emrParm.setData("FILE_NAME", emrName);
		emrParm.setData("CLASS_CODE", childParm.getData("CLASS_CODE"));
		emrParm.setData("SUBCLASS_CODE", childParm.getData("SUBCLASS_CODE"));
		emrParm.setData("CASE_NO", this.getCaseNo());
		emrParm.setData("MR_NO", this.getMrNo());
		emrParm.setData("IPD_NO", this.getIpdNo());
		emrParm.setData("FILE_PATH", "JHW" + "\\" + this.getAdmYear() + "\\"
				+ this.getAdmMouth() + "\\" + this.getMrNo());
		emrParm.setData("DESIGN_NAME", templetName + "(" + dateStr + ")");
		emrParm.setData("DISPOSAC_FLG", "N");
		emrParm.setData("TYPEEMR", childParm.getValue("TYPEEMR"));
		emrParm.setData("REF_FLG", childParm.getData("REF_FLG"));
		return emrParm;
	}


	public String getCaseNo() {
		return caseNo;
	}


	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}


	public String getMrNo() {
		return mrNo;
	}


	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}


	public String getAdmYear() {
		return admYear;
	}


	public void setAdmYear(String admYear) {
		this.admYear = admYear;
	}


	public String getAdmMouth() {
		return admMouth;
	}


	public void setAdmMouth(String admMouth) {
		this.admMouth = admMouth;
	}


	public String getIpdNo() {
		return ipdNo;
	}


	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}


	public String getViewMode() {
		return viewMode;
	}


	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}
	
	/**
	 * ����EMR�ļ������ݿ�
	 * 
	 * @param parm
	 *            TParm
	 */
	public boolean saveEmrFile(TParm parm) {
		boolean falg = true;
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", this.getDBTool().getDBTime());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REPORT_FLG", "N");
		parm.setData("CURRENT_USER", Operator.getID());
		//
		
		if (this.getViewMode().equals("NEW")) {
			parm.setData("CREATOR_USER", Operator.getID());
			TParm result = TIOM_AppServer.executeAction(actionName,
					"saveNewEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
			return falg;
		}
		

		if (this.getViewMode().equals("EDIT")) {
			parm.setData("CHK_USER1", "");
			parm.setData("CHK_DATE1", "");
			parm.setData("CHK_USER2", "");
			parm.setData("CHK_DATE2", "");
			parm.setData("CHK_USER3", "");
			parm.setData("CHK_DATE3", "");
			parm.setData("COMMIT_USER", "");
			parm.setData("COMMIT_DATE", "");		
			parm.setData("PDF_CREATOR_USER", "");
			parm.setData("PDF_CREATOR_DATE", "");
			parm.setData("IN_EXAMINE_USER", "");
			parm.setData("IN_EXAMINE_DATE", "");
			parm.setData("DS_EXAMINE_USER", "");			
			parm.setData("DS_EXAMINE_DATE", "");

			TParm result = TIOM_AppServer.executeAction(actionName,
					"writeEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
		}
		return falg;
	}
	
	/**
	 * ɾ���̶��ı�
	 */
	public void onDelFixText() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteFixed();
		} else {
			this.messageBox("��ѡ��ģ�棡");
		}
	}
	
	/**
	 * ���ϵͳ������
	 */
	public void onClearMenu() {
		CopyOperator.clearComList();
	}
	
	/**
	 * ������Ϣ�ղع���
	 */
	public void onSavePatInfo() {
		this.getWord().onCopy();
		// ��ϵͳ���а����Ƿ�������ݣ�
		if (CopyOperator.getComList() == null
				|| CopyOperator.getComList().size() == 0) {
			this.messageBox("����ѡ��Ҫ����Ĳ�����Ϣ��");
			return;
		}
		TParm inParm = new TParm();
		inParm.setData("MR_NO", this.getMrNo());
		inParm.setData("PAT_NAME", this.getPatName());
		inParm.setData("OP_TYPE", "SavePatInfo");
		this.openDialog("%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		/**
		 * TWindow window = (TWindow)this.openWindow(
		 * "%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		 * window.setX(ImageTool.getScreenWidth() - window.getWidth());
		 * window.setY(0); window.setVisible(true);
		 **/

	}


	public String getPatName() {
		return patName;
	}


	public void setPatName(String patName) {
		this.patName = patName;
	}
	
	/**
	 * ���뵱ǰʱ��
	 */
	public void onInsertCurrentTime() {
		// ��ȡʱ��
		Timestamp sysDate = StringTool.getTimestamp(new Date());
		String strSysDate = StringTool
				.getString(sysDate, "yyyy/MM/dd HH:mm:ss");
		// ���㴦����ʱ��;
		// EComponent e = word.getFocusManager().getFocus();
		this.getWord().pasteString(strSysDate);
	}
	
	/**
	 * Ƭ��
	 */
	public void onInsertPY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		// this.openWindow("%ROOT%\\config\\emr\\EMRComPhraseQuote.x",inParm);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}


	public String getDeptCode() {
		return deptCode;
	}


	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	
	/**
	 * �ٴ�����
	 */
	public void onInsertLCSJ() {
		TParm inParm = new TParm();
		inParm.setData("CASE_NO", this.getCaseNo());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		// this.openWindow("%ROOT%\\config\\emr\\EMRMEDDataUI.x",inParm);
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}
	
	/**
	 * ����ģ��Ƭ��
	 */
	public void onInsertTemplatePY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		// this.messageBox("this.getDeptCode()"+this.getDeptCode());
		inParm.setData("DEPT_CODE", this.getDeptCode());
		inParm.setData("TWORD", this.getWord());
		inParm.addListener("onReturnTemplateContent", this,
				"onReturnTemplateContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRTemplateComPhraseQuote.x", inParm,
				true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);

	}
	
	/**
	 * ���벡����Ϣ;
	 */
	public void onInsertPatInfo() {
		TParm inParm = new TParm();
		inParm.setData("MR_NO", this.getMrNo());
		inParm.setData("PAT_NAME", this.getPatName());
		inParm.setData("OP_TYPE", "InsertPatInfo");
		inParm.setData("TWORD", this.getWord());
		// inParm.addListener("onReturnContent", this, "onReturnContent");

		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRPatPhrase.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}
	
    /**
     * �������±깦��
     */
	public void onInsertMarkText() {
		//
		if (this.getWord().getFileOpenName() != null) {
			word.insertFixed();
			word.onOpenMarkProperty();
			//
		} else {
			this.messageBox("��ѡ������");
		}
	}
    
    /**
     * ���±��ı�����
     */
    public void onMarkTextProperty() {
        if (this.getWord().getFileOpenName() != null) {
        	word.onOpenMarkProperty();
        }
        else {
            this.messageBox("��ѡ������");
        }
    }
    
    /**
	 * ������
	 */
	public void onInsertTable() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertBaseTableDialog();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ɾ�����
	 */
	public void onDelTable() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteTable();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ��������
	 */
	public void onInsertTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertTR();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ׷�ӱ����
	 */
	public void onAddTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().appendTR();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}

	/**
	 * ɾ�������
	 */
	public void onDelTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteTR();
		} else {
			// ��ѡ����
			this.messageBox("E0099");
		}
	}
	
	/**
	 * ճ��ͼƬ
	 */
	public void onPastePicture() {
		ClipboardTool tool = new ClipboardTool();
		try {
			Image img = tool.getImageFromClipboard();
			// �ж�ͼƬ��ʽ�Ƿ���ȷ
			if (img == null || img.getWidth(null) == -1) {
				this.messageBox("���а���û��ͼƬ����,����ץȡ!");
				return;

			}
			this.getWord().onPaste();

		} catch (Exception ex) {
		}

	}
	
	/**
	 * ǩ��
	 */
	public void onSign() {
		onSignOp(true);
	}
	/**
	 * 
	 * @param isShow �Ƿ���ʾ��ʾ��
	 */
	public void onSignOp(boolean isShow) {
		//1.У��
		if (this.getWord().getFileOpenName() == null) {
			// û����Ҫ�༭���ļ�
			this.messageBox("E0100");
			return;
		}
		//�ж�ҽʦ�Ƿ��Ѿ�ǩ��
		ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
    	//this.messageBox("--sign1---"+sign1);
    	String signName=Operator.getID();
    	if(sign1!=null){
    		this.messageBox("����ǩ������Ҫ�༭��ȡ��ǩ����");
    		//
    		if(!isShow){
    			this.closeWindow();
    		}
    		return;
    		
    	}
    	List<ESign>  signs=getObjecct();
    	//���� SIGN�����
    	EFixed  fixed=(EFixed)this.getWord().findObject("SIGN", EComponent.FIXED_TYPE);
    	fixed.setFocus(0);
    	fixed.onFocusToRight();
    	String text=Operator.getName();
    	//������  ǩ��;
    	if(signs!=null&&signs.size()>0){
    		text+="/";
    	}
		// 1. ����ǩ�� �ؼ�
		Timestamp nowDate = SystemTool.getInstance().getDate();
		word.getFocusManager().insertSign("H.09",signName, Operator.getID(), text, Long.toString(nowDate.getTime()));
		
		//
		//�����ļ�
		onSaveEmr(false,false); 

		//�����ύ����
		setEditLevel();
		//
		TParm emrParm = this.getEmrParm();
		//
		//2������ǩ�����ύ
		this.setOptDataParm(emrParm, 2);
		//
		if (this.saveEmrFile(emrParm)) {
			this.setEmrParm(emrParm);
			if(isShow){
				this.messageBox("ǩ���ɹ���");
			}
			/*this.getWord().setCanEdit(false);
			this.getWord().onPreviewWord();*/
			onSaveEmr(false,true);			
			return;
		} else {
			if(isShow){
				this.messageBox("ǩ��ʧ�ܣ�");
			}
			return;
		}		
	}
	
	/**
	 * 
	 * ȡ��ǩ��
	 * 
	 */
	public void onSignCancel(){
		if (this.getWord().getFileOpenName() == null) {
			// û����Ҫ�༭���ļ�
			this.messageBox("E0100");
			return;
		}
		
		//�Ƿ������ǩ��
		//û��
		ESign sign=this.getSign();
		if(sign==null){
			this.messageBox("��û��ǩ��������ȡ��ǩ����");
			return;
		}		
		//�Ƿ�����һ����ǩ��
		//1.�ҵ�SIGN�Ĳ���㣬�������
		//���� SIGN�����
    	EFixed  fixed=(EFixed)this.getWord().findObject("SIGN", EComponent.FIXED_TYPE);
    	fixed.setFocusLast();
    	fixed.onFocusToRight();
    	EComponent comp=this.getWord().getFocusManager().getFocus();
    	if(comp!=null){
    		if(comp instanceof ESign){
		    	String s1=((ESign)comp).getName();
		    	//this.messageBox("s1===="+s1);
		    	//this.messageBox("--name--"+Operator.getID());
		    	if(!s1.equals(Operator.getID())){
		    		this.messageBox("���ϼ�ҽʦȡ��ǩ�������ſ���ȡ��ǩ����");
		    		return;
		    	}
    		}		
    	}
		//
		//��ǩ�������ɾ����
		sign.setFocus(1);
		sign.deleteFixed();
		//
		//���没��
		onSaveEmr(false,false); 
		//ȡ���ύ����
		// 5������ȡ���ύȨ�޿��ƣ�
		/*if (!this.checkDrForSubmitCancel()) {
			this.messageBox("E0011"); // Ȩ�޲���
			return;
		}*/
		TParm emrParm = this.getEmrParm();
		
		//setEditLevelCancel();
		
		if (this.saveEmrFile(emrParm)) {
			this.setEmrParm(emrParm);
			this.getWord().setCanEdit(true);
			this.getWord().onEditWord();
			this.messageBox("ȡ��ǩ���ɹ���");
			return;
		} else {
			this.messageBox("ȡ��ǩ��ʧ�ܣ�");
			return;
		}
		
	}
	
	
    /**
     * 
     * @return
     */
    private ESign getSign(){
    	//
    	ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
    	return sign1;
    	//
    }
      
    /**
     * �ж��Ƿ���Ա༭
     * @return
     */
    public boolean onEdit(){
    	//(1)	ǩ�ֺ󣬱��ͬ��ҽ�������޸ģ��ϼ�ҽ���޸�����
    	
    	//(2)	ûǩ�֣����ͬ��ҽ���޸����ۣ�������ǩ���˶�С���̸���
    	
    	// ��סԺϵͳ�Ľ�����������;
/*		if ( !this.checkDrForEdit()) {
			// 3�������༭����Ȩ�޿��ƣ�
			this.messageBox("E0102"); // ��û�б༭Ȩ��
			this.getWord().onPreviewWord();
			return false; //modify by wanglong 20121205
		}*/
    	//��ǩ��
		if(this.isSign()){
			this.messageBox("����ǩ�������ɱ༭������ȡ��ǩ����"); // ��û�б༭Ȩ��
			this.getWord().setCanEdit(false);
			this.getWord().onPreviewWord();
			return false;
		}
		
		
		return true;
    	
    }
    
    /**
     * 
     * �Ƿ��Ѿ�ǩ��
     * @return
     */
    private boolean isSign(){
    	boolean flg=false;   	
    	//ͬ�����    2����ͬǩ��
    	ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
    	if(sign1!=null){
    		flg=true;
    	}
    	return flg;
    }
    
	/**
	 * ��������ҽ���༭Ȩ��
	 */
	private void setEditLevel() {
		int stuts = this.getWord().getNodeIndex();
		//int stutsOnly = this.getWord().getNodeIndex();
			// ����ҽʦ
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == -1) {
				stuts = 0;
			}
			// ����ҽʦ
			if (this.getAttendDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 0) {
				stuts = 1;
			}
			// ����ҽʦ
			if (this.getDirectorDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 1) {
				stuts = 2;
			}
			// ���Ǿ���ҽʦ��������ҽʦ
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())) {
				stuts = 1;
			}
			// ���Ǿ���ҽʦ��������ҽʦ��������ҽʦ
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())
					&& this.getDirectorDrCode().equals(Operator.getID())) {
				stuts = 2;
			}
		//
		// �޸ļ�¼
		MModifyNode modifyNode = this.getWord().getPM().getModifyNodeManager();
		//
		if( null==modifyNode ){
			this.getWord().getPM().setModifyNodeManager( new MModifyNode() );
		}
		
		if (stuts != this.getWord().getNodeIndex()) {
			this.getWord().setNodeIndex(stuts);
		}
		saveWord();
	}
	
	/**
	 * 
	 */
	private void saveWord() {
		boolean mSwitch = this.getWord().getMessageBoxSwitch();
		// this.messageBox("mSwitch" + mSwitch);
		this.getWord().setMessageBoxSwitch(false);
		this.getWord().onSave();
		this.getWord().setMessageBoxSwitch(mSwitch);
	}
	
	/**
	 * �Ƿ�����������ҽʦ
	 */
	public boolean isCheckUserDr() {
		// �ж��Ƿ���ת��
		if (isAdmChg(this.getCaseNo())) {
			// ��ǰ������ҽʦ����
			this.setVsDrCode(this.getDr(this.getCaseNo(), this.getDeptCode(),
					"VS_DR_CODE"));
			// ����ҽʦ
			this.setAttendDrCode(this.getDr(this.getCaseNo(), this
					.getDeptCode(), "ATTEND_DR_CODE"));
			// ������
			this.setDirectorDrCode(this.getDr(this.getCaseNo(), this
					.getDeptCode(), "DIRECTOR_DR_CODE"));
			if (Operator.getID().equals(this.getVsDrCode())) {
				return true;
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				return true;
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				return true;
			}

		// ��ת�ƴ�ADM_INP���ж�
		} else {
			String sql = " SELECT VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE "
					+ " FROM ADM_INP " + " WHERE CASE_NO = '"
					+ this.getCaseNo() + "'";

			TParm parm = new TParm(this.getDBTool().select(sql));
			// ����ҽʦ
			this.setVsDrCode(parm.getValue("VS_DR_CODE", 0));
			// ����ҽʦ
			this.setAttendDrCode(parm.getValue("ATTEND_DR_CODE", 0));
			// ������
			this.setDirectorDrCode(parm.getValue("DIRECTOR_DR_CODE", 0));
			if (Operator.getID().equals(parm.getValue("VS_DR_CODE", 0))) {
				return true;
			}
			if (Operator.getID().equals(parm.getValue("ATTEND_DR_CODE", 0))) {
				return true;
			}
			if (Operator.getID().equals(parm.getValue("DIRECTOR_DR_CODE", 0))) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * �Ƿ���ֵ��ҽ��
	 *
	 * @return boolean
	 */
	public boolean isDutyDrList() {
		boolean falg = false;
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT * FROM ODI_DUTYDRLIST WHERE DR_CODE='"
						+ Operator.getID() + "'"));
		if (parm.getCount() > 0) {
			falg = true;
		}
		return falg;
	}
	
	/**
	 * �Ƿ����ת��
	 *
	 * @param caseNo
	 * @return
	 */
	private boolean isAdmChg(String caseNo) {
		boolean flg = false;
		// ������Ʋ���,˵��INDP;
		String sql = "SELECT COUNT(*) FROM ADM_CHG";
		sql += " WHERE PSF_KIND='INDP' AND CASE_NO='" + caseNo + "'";
		sql += " AND CANCEL_FLG='N'";
		//System.out.println("==sql----=="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		// >1,˵����ת����
		if (parm.getCount() > 1) {
			flg = true;
		}

		return flg;
	}
	
	/**
	 * ��ȡ�������ڿ���ʱ�Ķ�Ӧҽʦ(����,����,������)
	 *
	 * @param caseNo
	 * @param dept
	 * @param drType
	 *            (VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE)
	 * @return ҽ������
	 */
	private String getDr(String caseNo, String dept, String drType) {
		String sql = "SELECT " + drType + " FROM ADM_CHG";
		sql += " WHERE CASE_NO='" + caseNo + "' AND DEPT_CODE='" + dept
				+ "' AND " + drType + " IS NOT NULL";
		sql += " ORDER BY SEQ_NO DESC";
		// System.out.println("====sql===="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		// �ÿ��Ҵ��� ������ҽʦ
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				if (Operator.getID().equals(parm.getValue(drType, i))) {
					return parm.getValue(drType, i);
				}
			}
		}
		return "";
	}
	
    /**
     * ǩ���ؼ�����
     * @param name
     * @return
     */
   private List<ESign> getObjecct(){
    	List<ESign> list=new ArrayList<ESign>();
    	TList components = this.getWord().getPageManager().getComponentList();
		int size = components.size();
		//
		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			// this.messageBox("EPanel size" + ePage.getComponentList().size());
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				//
				for (int k = 0; k < ePanel.getBlockSize(); k++) {
					IBlock block = (IBlock) ePanel.get(k);
					if (block != null) {
						if (block.getObjectType() == EComponent.SIGN_TYPE) {
							list.add(((ESign)block));
						}						
					}
				}

			}
		}
		return list;
    }
   
	public void setOptDataParm(TParm parm, int optType) {
		TParm data = this.getEmrParm();
		int operDuty = this.getUserDuty(Operator.getID());
		// ����ʱ
		if (optType == 1) {
			// ������CANPRINT_FLG��MODIFY_FLG
/*			TCheckBox CANPRINT_FLG = (TCheckBox) this
					.getComponent("CANPRINT_FLG");
			TCheckBox MODIFY_FLG = (TCheckBox) this.getComponent("MODIFY_FLG");
			parm.setData("CANPRINT_FLG", CANPRINT_FLG.getValue());
			parm.setData("MODIFY_FLG", MODIFY_FLG.getValue());*/
		}
		// �ύʱ
		if (optType == 2) {
			// �����ӡ�������޸�
			/*parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");*/
			// CURRENT_USERΪ�ϼ�ҽʦ����ǰ�û�Ϊ����ҽʦʱΪ�㣩��ǩ���󣬽��뵽�ϼ�ҽʦ
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", "0");
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "0");
			}
			// ��д���������˺�ʱ��
			if (Operator.getID().equals(this.getVsDrCode())) {
				parm.setData("CHK_USER1", Operator.getID());
				parm.setData("CHK_DATE1", this.getDBTool().getDBTime());
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				parm.setData("CHK_USER2", Operator.getID());
				parm.setData("CHK_DATE2", this.getDBTool().getDBTime());
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				parm.setData("CHK_USER3", Operator.getID());
				parm.setData("CHK_DATE3", this.getDBTool().getDBTime());
			}
			// ��д�ύ�˺�ʱ��
			parm.setData("COMMIT_USER", Operator.getID());
			parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
		}
		// ȡ���ύ(��ǰ�û����ύ)   ��Ӧ����  ���3��ֻ��ȡ�����ѵ�ǩ��
		if (optType == 3) {
			// �����ӡ�������޸�
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USERΪ��ǰ�û�����ǰ�û�Ϊ����ҽʦʱ��գ����ύ�˺�ʱ��Ϊ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦʱ��գ�
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", this.getDirectorDrCode());
				parm.setData("COMMIT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
			}
			// ���������˺�ʱ�����
			if (Operator.getID().equals(this.getVsDrCode())) {
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (Operator.getID().equals(this.getAttendDrCode())) {
				parm.setData("CHK_USER2", "");
				parm.setData("CHK_DATE2", "");
			}
			if (Operator.getID().equals(this.getDirectorDrCode())) {
				parm.setData("CHK_USER3", "");
				parm.setData("CHK_DATE3", "");
			}
		}
		// ȡ���ύ(��ǰ�û�δ�ύ)  ǩ�����
		if (optType == 4) {
			// �����ӡ�������޸�
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USERΪ�¼�ҽʦ����ǰ�û�Ϊ����ҽʦ������ҽʦʱ��գ����ύ�˺�ʱ��Ϊ�¼�ҽʦ���¼�ҽʦ����ǰ�û�Ϊ����ҽʦ������ҽʦʱ��գ����¼������˺�ʱ�����
			if (operDuty == 1) {
			}
			if (operDuty == 2) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 3) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 4) {
				parm.setData("CURRENT_USER", this.getAttendDrCode());
				parm.setData("COMMIT_USER", this.getVsDrCode());
				parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
				parm.setData("CHK_USER2", "");
				parm.setData("CHK_DATE2", "");
			}
			if (operDuty == 5) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 6) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 7) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
			if (operDuty == 8) {
				parm.setData("CURRENT_USER", "");
				parm.setData("COMMIT_USER", "");
				parm.setData("COMMIT_DATE", "");
				parm.setData("CHK_USER1", "");
				parm.setData("CHK_DATE1", "");
			}
		}

		// һ��������
		if (!parm.existData("CHK_USER1")) {
			parm.setData("CHK_USER1", data.getValue("CHK_USER1"));
		}
		// һ������ʱ��
		if (!parm.existData("CHK_DATE1")) {
			parm.setData("CHK_DATE1", data.getValue("CHK_DATE1"));
		}
		// ����������
		if (!parm.existData("CHK_USER2")) {
			parm.setData("CHK_USER2", data.getValue("CHK_USER2"));
		}
		// ��������ʱ��
		if (!parm.existData("CHK_DATE2")) {
			parm.setData("CHK_DATE2", data.getValue("CHK_DATE2"));
		}
		// ����������
		if (!parm.existData("CHK_USER3")) {
			parm.setData("CHK_USER3", data.getValue("CHK_USER3"));
		}
		// ��������ʱ��
		if (!parm.existData("CHK_DATE3")) {
			parm.setData("CHK_DATE3", data.getValue("CHK_DATE3"));
		}
		// �ύ��
		if (!parm.existData("COMMIT_USER")) {
			parm.setData("COMMIT_USER", data.getValue("COMMIT_USER"));
		}
		// �ύʱ��
		if (!parm.existData("COMMIT_DATE")) {
			parm.setData("COMMIT_DATE", data.getValue("COMMIT_DATE"));
		}
		// ��Ժ�����
		if (!parm.existData("IN_EXAMINE_USER")) {
			parm.setData("IN_EXAMINE_USER", data.getValue("IN_EXAMINE_USER"));
		}
		// ��Ժ���ʱ��
		if (!parm.existData("IN_EXAMINE_DATE")) {
			parm.setData("IN_EXAMINE_DATE", data.getValue("IN_EXAMINE_DATE"));
		}
		// ��Ժ�����
		if (!parm.existData("DS_EXAMINE_USER")) {
			parm.setData("DS_EXAMINE_USER", data.getValue("DS_EXAMINE_USER"));
		}
		// ��Ժ���ʱ��
		if (!parm.existData("DS_EXAMINE_DATE")) {
			parm.setData("DS_EXAMINE_DATE", data.getValue("DS_EXAMINE_DATE"));
		}
		// PDF������
		if (!parm.existData("PDF_CREATOR_USER")) {
			parm.setData("PDF_CREATOR_USER", data.getValue("PDF_CREATOR_USER"));
		}
		// PDF����ʱ��
		if (!parm.existData("PDF_CREATOR_DATE")) {
			parm.setData("PDF_CREATOR_DATE", data.getValue("PDF_CREATOR_DATE"));
		}
	}
	
	/**
	 * ��ȡ�û���ְ����� 
	 *   
	 * 
	 */
	public int getUserDuty(String user) {
		// 1���Ǿ���ҽʦ ������ҽʦ ������ҽʦ
		if (!user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 1;
		}
		//2������ҽʦ ������ҽʦ ������ҽʦ
		if (user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 2;
		}
		// 3���Ǿ���ҽʦ   ������ҽʦ     ������ҽʦ
		if (!user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 3;
		}
		//4���Ǿ���ҽʦ  ������ҽʦ   ����ҽʦ
		if (!user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 4;
		}
		//5������ҽʦ      ����ҽʦ       ������ҽʦ
		if (user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 5;
		}
		//6������ҽʦ    ������ҽʦ     ����ҽʦ
		if (user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 6;
		}
		//7���Ǿ���ҽʦ   ����ҽʦ     ����ҽʦ
		if (!user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 7;
		}
		//8������ҽʦ  ����ҽʦ  ����ҽʦ
		if (user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 8;
		}
		return 0;
	}
	
    /**
     * �������ַ��������¼�
     */
    public void onSpecialChars() {
        if (!word.canEdit()) {
            messageBox("��ѡ����ģ��!");
            return;
        } 
        TParm parm = new TParm();
        parm.addListener("onReturnContent", this, "onReturnContent");
        TWindow window = (TWindow)openWindow("%ROOT%\\config\\emr\\EMRSpecialChars.x", parm, true);// wanglong add 20140908

        /*TPanel wordPanel = ((TPanel) this.getComponent("PANEL"));
        window.setX(wordPanel.getX() + 10);
        window.setY(130);*/
        //
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
        //
        AnimationWindowUtils.show(window);
        AWTUtilities.setWindowOpacity(window, 0.9f);
        window.setVisible(true);
    }
    
	/**
	 * Ƭ���¼
	 *
	 * @param value
	 *            String
	 */
	public void onReturnContent(String value) {
		if (!this.getWord().pasteString(value)) {
			// ִ��ʧ��
			this.messageBox("E0005");
		}
	}
	
	/**
	 * ������
	 */
	private EFixed fixed;
	//private EMacroroutine macroroutine;
	/**
	 * �ṹ�������Ҽ�����
	 */
	public void onMouseRightPressed() {
		
		EComponent e = this.getWord().getFocusManager().getFocus();
		if (e == null) {
			return;
		}
		// �Ƿ�ɱ༭
		if(!this.onEdit()){ //modify by wanglong 20121205
			return;
		}

		if (!this.getWord().canEdit()) {
			return;
		}

		// ץȡ��
		if (e instanceof ECapture) {
			return;
		}
		// ��
		if (e instanceof EFixed) {
			fixed = (EFixed) e;
			this.getWord().popupMenu(fixed.getName() + "�޸�,onModify"+";���±��޸�,onMarkTextProperty", this);
		}
		// ͼƬ
/*		if (e instanceof EMacroroutine) {
			macroroutine = (EMacroroutine) e;
			this.getWord().popupMenu(
					macroroutine.getName() + "�༭,onModifyMacroroutine", this);

		}*/

	}
	
	/**
	 * ���޸�
	 */
	public void onModify() {
		if (fixed == null) {
			return;
		}
		Object obj = this.openDialog("%ROOT%\\config\\emr\\ModifUI.x", fixed
				.getText());
		if (obj != null) {
			fixed.setText(obj.toString());
		}
	}
	
	

	

}
