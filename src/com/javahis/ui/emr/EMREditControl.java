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
 * 病程记录编辑器
 * 
 * @author lix@bluecore.com.cn
 *
 */
public class EMREditControl  extends TControl implements DMessageIO{
	/**
	 * 调试开关
	 */
	private boolean isDebug=false;
	
	/**
	 * 动作类名字
	 */
	private static final String actionName = "action.odi.ODIAction";
	/**
	 * WORD控件
	 */
	private static final String TWORD = "WORD";
	/**
	 * 新增视图模式
	 */
	private static final String NEW_MODE="NEW";
	/**
	 * 编辑视图模式
	 */
	private static final String EDIT_MODE="EDIT";
	
	/**
	 * 日常病程历史记录模版
	 */
	private static final String SUBCLASS_CODE="EMR10001701";
	private static final String CLASS_CODE="EMR100017";
	/**
	 * 入参
	 */
	TParm inParm;
	
	/**
	 * WORD对象
	 */
	private TWord word;
	TParm emrParm = new TParm();
	
	//输入参数;
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
	 * 调用科室
	 */
	private String deptCode;
	
	/**
	 * 经治医师
	 */
	private String vsDrCode;
	/**
	 * 主治医师
	 */

	private String attendDrCode;
	/**
	 * 科主任
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
		// 三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/登陆
/*		if (this.isCheckUserDr() || this.isDutyDrList()) {
			TParm emrParm = new TParm();
			emrParm.setData("FILE_SEQ", "0");
			emrParm.setData("FILE_NAME", "");
			TParm result = OptLogTool.getInstance().writeOptLog(
					inParm, "L", emrParm);

		}
		//
		// 非三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/调阅
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
	 * 初始化界面
	 */
	public void initPage() {
		//得到入参数
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
		//新增
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
				System.out.println("--------新建打开模版文件出错------------");
			}
			this.setEmrParm(currParm);
			
		//打开编辑	
		}else if(this.getViewMode().equals(EDIT_MODE)){
			if(isDebug){
				System.out.println("==========打开已有文件==============");
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
			// 打开病历
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
		//判断是否可以编辑
		//TODO
		if(this.onEdit()){
			// 可编辑
			this.getWord().setCanEdit(true);
			// 编辑状态(非整洁)
			this.getWord().onEditWord();
		}
				
	}
	
	/**
	 * 初始化WORD
	 */
	public void initWord() {
		//获取控件
		word = this.getTWord(TWORD);
		//
		this.setWord(word);
		// 字体
		this.getWord().setFontComboTag("ModifyFontCombo");
		// 字体
		this.getWord().setFontSizeComboTag("ModifyFontSizeCombo");

		// 变粗
		this.getWord().setFontBoldButtonTag("FontBMenu");

		// 斜体
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
	 * 保存
	 */
	public void onSave() {
		if(isDebug){
			System.out.println("====EMREditControl onSave start=====");
		}
		onSaveEmr(true,true);
	}
	
	/**
	 * 保存病历
	 * @param isShow  是否显示提示框
	 * @return
	 */
	private void onSaveEmr(boolean isShow,boolean isClose) {
		
		TParm asSaveParm=null;
		// 日期
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy年MM月dd日 HH时mm分ss秒");
		if ("NEW".equals(this.getViewMode())) {
			this.setEmrParm(this.getFileServerEmrName());
			// 另存到用户文件
			asSaveParm = this.getEmrParm();
			//
		    //System.out.println("==asSaveParm=="+asSaveParm);
		    //
			this.getWord().setMessageBoxSwitch(false);
			this.getWord().setFileAuthor(Operator.getID());
			// 公司
			this.getWord().setFileCo("JAVAHIS");
			// 标题
			this.getWord().setFileTitle(asSaveParm.getValue("DESIGN_NAME"));
			// 备注
			this.getWord().setFileRemark(
					asSaveParm.getValue("CLASS_CODE") + "|"
							+ asSaveParm.getValue("FILE_PATH") + "|"
							+ asSaveParm.getValue("FILE_NAME"));
			// 创建时间
			this.getWord().setFileCreateDate(dateStr);
			// 最后修改人
			this.getWord().setFileLastEditUser(Operator.getID());
			// 最后修改日期
			this.getWord().setFileLastEditDate(dateStr);
			// 最后修改IP
			this.getWord().setFileLastEditIP(Operator.getIP());
			/*System.out.println("==save filePath=="
					+ asSaveParm.getValue("FILE_PATH"));
			System.out.println("==save fileName=="
					+ asSaveParm.getValue("FILE_NAME"));*/
			// 另存为
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);

			EMRCreateXMLTool.getInstance()
					.createXML(asSaveParm.getValue("FILE_PATH"),
							asSaveParm.getValue("FILE_NAME"), "EmrData",
							this.getWord());
			
			if (!success) {
				// 文件服务器异常
				this.messageBox("E0103");
				this.getWord().setMessageBoxSwitch(true);
				//return false;
				return;
		    
			//文件保存成功.
			}
			this.getWord().setMessageBoxSwitch(true);
			
			
			// 插入数据库数据
			if (saveEmrFile(asSaveParm)) {
				if (isShow) {
					// 保存成功
					this.messageBox("P0001");
				}
			}else {
				if (isShow) {
					// 保存失败
					this.messageBox("E0001");
				}
				//return false;
				return;
			}
		
		}
		
		if ("EDIT".equals(this.getViewMode())) {
			asSaveParm = this.getEmrParm();
			
			//System.out.println("-------asSaveParm-----"+asSaveParm);
			// 设置提示不可用
			this.getWord().setMessageBoxSwitch(false);
			// 最后修改人
			this.getWord().setFileLastEditUser(Operator.getID());
			// 最后修改日期
			this.getWord().setFileLastEditDate(dateStr);
			// 最后修改IP
			this.getWord().setFileLastEditIP(Operator.getIP());
			
			// 保存
			boolean success = this.getWord().onSaveAs(
					asSaveParm.getValue("FILE_PATH"),
					asSaveParm.getValue("FILE_NAME"), 3);
			EMRCreateXMLTool.getInstance()
					.createXML(asSaveParm.getValue("FILE_PATH"),
							asSaveParm.getValue("FILE_NAME"), "EmrData",
							this.getWord());
			
			if (!success) {
				// 文件服务器异常
				this.messageBox("E0103");
				// 设置提示框为可提示
				this.getWord().setMessageBoxSwitch(true);
				return;
			}
			this.getWord().setMessageBoxSwitch(true);
			// 保存数据
			if (saveEmrFile(asSaveParm)) {
				if (isShow) {
					// 保存成功
					this.messageBox("P0001");
				}
			} else {
				if (isShow) {
					// 保存失败
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
	 * 返回数据库操作工具
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
	 * 拿到文件服务器路径
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
	 * 保存EMR文件到数据库
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
	 * 删除固定文本
	 */
	public void onDelFixText() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteFixed();
		} else {
			this.messageBox("请选择模版！");
		}
	}
	
	/**
	 * 清空系统剪贴板
	 */
	public void onClearMenu() {
		CopyOperator.clearComList();
	}
	
	/**
	 * 病患信息收藏功能
	 */
	public void onSavePatInfo() {
		this.getWord().onCopy();
		// 本系统剪切板中是否存在数据；
		if (CopyOperator.getComList() == null
				|| CopyOperator.getComList().size() == 0) {
			this.messageBox("请先选择要保存的病患信息！");
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
	 * 插入当前时间
	 */
	public void onInsertCurrentTime() {
		// 获取时间
		Timestamp sysDate = StringTool.getTimestamp(new Date());
		String strSysDate = StringTool
				.getString(sysDate, "yyyy/MM/dd HH:mm:ss");
		// 焦点处加入时间;
		// EComponent e = word.getFocusManager().getFocus();
		this.getWord().pasteString(strSysDate);
	}
	
	/**
	 * 片语
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
	 * 临床数据
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
	 * 插入模版片语
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
	 * 插入病患信息;
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
     * 插入上下标功能
     */
	public void onInsertMarkText() {
		//
		if (this.getWord().getFileOpenName() != null) {
			word.insertFixed();
			word.onOpenMarkProperty();
			//
		} else {
			this.messageBox("请选择病历！");
		}
	}
    
    /**
     * 上下标文本属性
     */
    public void onMarkTextProperty() {
        if (this.getWord().getFileOpenName() != null) {
        	word.onOpenMarkProperty();
        }
        else {
            this.messageBox("请选择病历！");
        }
    }
    
    /**
	 * 插入表格
	 */
	public void onInsertTable() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertBaseTableDialog();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 删除表格
	 */
	public void onDelTable() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteTable();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 插入表格行
	 */
	public void onInsertTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().insertTR();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 追加表格行
	 */
	public void onAddTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().appendTR();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

	/**
	 * 删除表格行
	 */
	public void onDelTableRow() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().deleteTR();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}
	
	/**
	 * 粘帖图片
	 */
	public void onPastePicture() {
		ClipboardTool tool = new ClipboardTool();
		try {
			Image img = tool.getImageFromClipboard();
			// 判断图片格式是否正确
			if (img == null || img.getWidth(null) == -1) {
				this.messageBox("剪切板中没有图片内容,请先抓取!");
				return;

			}
			this.getWord().onPaste();

		} catch (Exception ex) {
		}

	}
	
	/**
	 * 签名
	 */
	public void onSign() {
		onSignOp(true);
	}
	/**
	 * 
	 * @param isShow 是否显示提示框
	 */
	public void onSignOp(boolean isShow) {
		//1.校验
		if (this.getWord().getFileOpenName() == null) {
			// 没有需要编辑的文件
			this.messageBox("E0100");
			return;
		}
		//判断医师是否已经签名
		ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
    	//this.messageBox("--sign1---"+sign1);
    	String signName=Operator.getID();
    	if(sign1!=null){
    		this.messageBox("您已签名，需要编辑请取消签名！");
    		//
    		if(!isShow){
    			this.closeWindow();
    		}
    		return;
    		
    	}
    	List<ESign>  signs=getObjecct();
    	//查找 SIGN插入点
    	EFixed  fixed=(EFixed)this.getWord().findObject("SIGN", EComponent.FIXED_TYPE);
    	fixed.setFocus(0);
    	fixed.onFocusToRight();
    	String text=Operator.getName();
    	//假如有  签名;
    	if(signs!=null&&signs.size()>0){
    		text+="/";
    	}
		// 1. 插入签名 控件
		Timestamp nowDate = SystemTool.getInstance().getDate();
		word.getFocusManager().insertSign("H.09",signName, Operator.getID(), text, Long.toString(nowDate.getTime()));
		
		//
		//保存文件
		onSaveEmr(false,false); 

		//再做提交操作
		setEditLevel();
		//
		TParm emrParm = this.getEmrParm();
		//
		//2：代表签名并提交
		this.setOptDataParm(emrParm, 2);
		//
		if (this.saveEmrFile(emrParm)) {
			this.setEmrParm(emrParm);
			if(isShow){
				this.messageBox("签名成功！");
			}
			/*this.getWord().setCanEdit(false);
			this.getWord().onPreviewWord();*/
			onSaveEmr(false,true);			
			return;
		} else {
			if(isShow){
				this.messageBox("签名失败！");
			}
			return;
		}		
	}
	
	/**
	 * 
	 * 取消签名
	 * 
	 */
	public void onSignCancel(){
		if (this.getWord().getFileOpenName() == null) {
			// 没有需要编辑的文件
			this.messageBox("E0100");
			return;
		}
		
		//是否有你的签名
		//没有
		ESign sign=this.getSign();
		if(sign==null){
			this.messageBox("您没有签名，不用取消签名！");
			return;
		}		
		//是否有上一级的签名
		//1.找到SIGN的插入点，光标右移
		//查找 SIGN插入点
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
		    		this.messageBox("请上级医师取消签名后，您才可以取消签名！");
		    		return;
		    	}
    		}		
    	}
		//
		//有签名情况，删除掉
		sign.setFocus(1);
		sign.deleteFixed();
		//
		//保存病历
		onSaveEmr(false,false); 
		//取消提交操作
		// 5、病历取消提交权限控制：
		/*if (!this.checkDrForSubmitCancel()) {
			this.messageBox("E0011"); // 权限不足
			return;
		}*/
		TParm emrParm = this.getEmrParm();
		
		//setEditLevelCancel();
		
		if (this.saveEmrFile(emrParm)) {
			this.setEmrParm(emrParm);
			this.getWord().setCanEdit(true);
			this.getWord().onEditWord();
			this.messageBox("取消签名成功！");
			return;
		} else {
			this.messageBox("取消签名失败！");
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
     * 判断是否可以编辑
     * @return
     */
    public boolean onEdit(){
    	//(1)	签字后，别的同级医生不能修改，上级医生修改留痕
    	
    	//(2)	没签字，别的同级医生修改留痕，最终由签字人对小病程负责
    	
    	// 是住院系统的进行三级检诊;
/*		if ( !this.checkDrForEdit()) {
			// 3、病历编辑保存权限控制：
			this.messageBox("E0102"); // 您没有编辑权限
			this.getWord().onPreviewWord();
			return false; //modify by wanglong 20121205
		}*/
    	//已签名
		if(this.isSign()){
			this.messageBox("您已签名，不可编辑，请先取消签名！"); // 您没有编辑权限
			this.getWord().setCanEdit(false);
			this.getWord().onPreviewWord();
			return false;
		}
		
		
		return true;
    	
    }
    
    /**
     * 
     * 是否已经签名
     * @return
     */
    private boolean isSign(){
    	boolean flg=false;   	
    	//同名情况    2个相同签名
    	ESign  sign1=(ESign)this.getWord().findObject(Operator.getID(), EComponent.SIGN_TYPE);
    	if(sign1!=null){
    		flg=true;
    	}
    	return flg;
    }
    
	/**
	 * 三级检诊医生编辑权限
	 */
	private void setEditLevel() {
		int stuts = this.getWord().getNodeIndex();
		//int stutsOnly = this.getWord().getNodeIndex();
			// 经治医师
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == -1) {
				stuts = 0;
			}
			// 主治医师
			if (this.getAttendDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 0) {
				stuts = 1;
			}
			// 主任医师
			if (this.getDirectorDrCode().equals(Operator.getID())
					&& this.getWord().getNodeIndex() == 1) {
				stuts = 2;
			}
			// 即是经治医师又是主治医师
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())) {
				stuts = 1;
			}
			// 即是经治医师又是主治医师又是主任医师
			if (this.getVsDrCode().equals(Operator.getID())
					&& this.getAttendDrCode().equals(Operator.getID())
					&& this.getDirectorDrCode().equals(Operator.getID())) {
				stuts = 2;
			}
		//
		// 修改记录
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
	 * 是否是三级检诊医师
	 */
	public boolean isCheckUserDr() {
		// 判断是否有转科
		if (isAdmChg(this.getCaseNo())) {
			// 当前操作者医师科室
			this.setVsDrCode(this.getDr(this.getCaseNo(), this.getDeptCode(),
					"VS_DR_CODE"));
			// 主治医师
			this.setAttendDrCode(this.getDr(this.getCaseNo(), this
					.getDeptCode(), "ATTEND_DR_CODE"));
			// 科主任
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

		// 非转科从ADM_INP中判断
		} else {
			String sql = " SELECT VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE "
					+ " FROM ADM_INP " + " WHERE CASE_NO = '"
					+ this.getCaseNo() + "'";

			TParm parm = new TParm(this.getDBTool().select(sql));
			// 经治医师
			this.setVsDrCode(parm.getValue("VS_DR_CODE", 0));
			// 主治医师
			this.setAttendDrCode(parm.getValue("ATTEND_DR_CODE", 0));
			// 科主任
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
	 * 是否是值班医生
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
	 * 是否存在转科
	 *
	 * @param caseNo
	 * @return
	 */
	private boolean isAdmChg(String caseNo) {
		boolean flg = false;
		// 存在入科操作,说明INDP;
		String sql = "SELECT COUNT(*) FROM ADM_CHG";
		sql += " WHERE PSF_KIND='INDP' AND CASE_NO='" + caseNo + "'";
		sql += " AND CANCEL_FLG='N'";
		//System.out.println("==sql----=="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		// >1,说明是转过科
		if (parm.getCount() > 1) {
			flg = true;
		}

		return flg;
	}
	
	/**
	 * 获取病患所在科室时的对应医师(经治,主治,科主任)
	 *
	 * @param caseNo
	 * @param dept
	 * @param drType
	 *            (VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE)
	 * @return 医生代码
	 */
	private String getDr(String caseNo, String dept, String drType) {
		String sql = "SELECT " + drType + " FROM ADM_CHG";
		sql += " WHERE CASE_NO='" + caseNo + "' AND DEPT_CODE='" + dept
				+ "' AND " + drType + " IS NOT NULL";
		sql += " ORDER BY SEQ_NO DESC";
		// System.out.println("====sql===="+sql);
		TParm parm = new TParm(this.getDBTool().select(sql));
		// 该科室存在 三级检医师
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
     * 签名控件集合
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
		// 保存时
		if (optType == 1) {
			// 需设置CANPRINT_FLG和MODIFY_FLG
/*			TCheckBox CANPRINT_FLG = (TCheckBox) this
					.getComponent("CANPRINT_FLG");
			TCheckBox MODIFY_FLG = (TCheckBox) this.getComponent("MODIFY_FLG");
			parm.setData("CANPRINT_FLG", CANPRINT_FLG.getValue());
			parm.setData("MODIFY_FLG", MODIFY_FLG.getValue());*/
		}
		// 提交时
		if (optType == 2) {
			// 允许打印、允许修改
			/*parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");*/
			// CURRENT_USER为上级医师（当前用户为主任医师时为零），签名后，进入到上级医师
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
			// 填写本级检诊人和时间
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
			// 填写提交人和时间
			parm.setData("COMMIT_USER", Operator.getID());
			parm.setData("COMMIT_DATE", this.getDBTool().getDBTime());
		}
		// 取消提交(当前用户已提交)   都应该走  这个3，只能取消自已的签名
		if (optType == 3) {
			// 允许打印、允许修改
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USER为当前用户（当前用户为经治医师时清空），提交人和时间为下级医师（当前用户为经治医师时清空）
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
			// 本级检诊人和时间清空
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
		// 取消提交(当前用户未提交)  签名情况
		if (optType == 4) {
			// 允许打印、允许修改
			parm.setData("CANPRINT_FLG", "Y");
			parm.setData("MODIFY_FLG", "Y");
			// CURRENT_USER为下级医师（当前用户为经治医师或主治医师时清空），提交人和时间为下级医师的下级医师（当前用户为经治医师或主治医师时清空），下级检诊人和时间清空
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

		// 一级检诊人
		if (!parm.existData("CHK_USER1")) {
			parm.setData("CHK_USER1", data.getValue("CHK_USER1"));
		}
		// 一级检诊时间
		if (!parm.existData("CHK_DATE1")) {
			parm.setData("CHK_DATE1", data.getValue("CHK_DATE1"));
		}
		// 二级检诊人
		if (!parm.existData("CHK_USER2")) {
			parm.setData("CHK_USER2", data.getValue("CHK_USER2"));
		}
		// 二级检诊时间
		if (!parm.existData("CHK_DATE2")) {
			parm.setData("CHK_DATE2", data.getValue("CHK_DATE2"));
		}
		// 三级检诊人
		if (!parm.existData("CHK_USER3")) {
			parm.setData("CHK_USER3", data.getValue("CHK_USER3"));
		}
		// 三级检诊时间
		if (!parm.existData("CHK_DATE3")) {
			parm.setData("CHK_DATE3", data.getValue("CHK_DATE3"));
		}
		// 提交人
		if (!parm.existData("COMMIT_USER")) {
			parm.setData("COMMIT_USER", data.getValue("COMMIT_USER"));
		}
		// 提交时间
		if (!parm.existData("COMMIT_DATE")) {
			parm.setData("COMMIT_DATE", data.getValue("COMMIT_DATE"));
		}
		// 在院审核人
		if (!parm.existData("IN_EXAMINE_USER")) {
			parm.setData("IN_EXAMINE_USER", data.getValue("IN_EXAMINE_USER"));
		}
		// 在院审核时间
		if (!parm.existData("IN_EXAMINE_DATE")) {
			parm.setData("IN_EXAMINE_DATE", data.getValue("IN_EXAMINE_DATE"));
		}
		// 出院审核人
		if (!parm.existData("DS_EXAMINE_USER")) {
			parm.setData("DS_EXAMINE_USER", data.getValue("DS_EXAMINE_USER"));
		}
		// 出院审核时间
		if (!parm.existData("DS_EXAMINE_DATE")) {
			parm.setData("DS_EXAMINE_DATE", data.getValue("DS_EXAMINE_DATE"));
		}
		// PDF生成人
		if (!parm.existData("PDF_CREATOR_USER")) {
			parm.setData("PDF_CREATOR_USER", data.getValue("PDF_CREATOR_USER"));
		}
		// PDF生成时间
		if (!parm.existData("PDF_CREATOR_DATE")) {
			parm.setData("PDF_CREATOR_DATE", data.getValue("PDF_CREATOR_DATE"));
		}
	}
	
	/**
	 * 获取用户的职责代码 
	 *   
	 * 
	 */
	public int getUserDuty(String user) {
		// 1：非经治医师 非主治医师 非主任医师
		if (!user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 1;
		}
		//2：经治医师 非主治医师 非主任医师
		if (user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 2;
		}
		// 3：非经治医师   是主治医师     非主任医师
		if (!user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 3;
		}
		//4：非经治医师  非主治医师   主任医师
		if (!user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 4;
		}
		//5：经治医师      主治医师       非主任医师
		if (user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& !user.equals(this.getDirectorDrCode())) {
			return 5;
		}
		//6：经治医师    非主治医师     主任医师
		if (user.equals(this.getVsDrCode())
				&& !user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 6;
		}
		//7：非经治医师   主治医师     主任医师
		if (!user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 7;
		}
		//8：经治医师  主治医师  主任医师
		if (user.equals(this.getVsDrCode())
				&& user.equals(this.getAttendDrCode())
				&& user.equals(this.getDirectorDrCode())) {
			return 8;
		}
		return 0;
	}
	
    /**
     * “特殊字符”单击事件
     */
    public void onSpecialChars() {
        if (!word.canEdit()) {
            messageBox("先选择病例模版!");
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
	 * 片语记录
	 *
	 * @param value
	 *            String
	 */
	public void onReturnContent(String value) {
		if (!this.getWord().pasteString(value)) {
			// 执行失败
			this.messageBox("E0005");
		}
	}
	
	/**
	 * 宏名称
	 */
	private EFixed fixed;
	//private EMacroroutine macroroutine;
	/**
	 * 结构化病历右键调用
	 */
	public void onMouseRightPressed() {
		
		EComponent e = this.getWord().getFocusManager().getFocus();
		if (e == null) {
			return;
		}
		// 是否可编辑
		if(!this.onEdit()){ //modify by wanglong 20121205
			return;
		}

		if (!this.getWord().canEdit()) {
			return;
		}

		// 抓取框
		if (e instanceof ECapture) {
			return;
		}
		// 宏
		if (e instanceof EFixed) {
			fixed = (EFixed) e;
			this.getWord().popupMenu(fixed.getName() + "修改,onModify"+";上下标修改,onMarkTextProperty", this);
		}
		// 图片
/*		if (e instanceof EMacroroutine) {
			macroroutine = (EMacroroutine) e;
			this.getWord().popupMenu(
					macroroutine.getName() + "编辑,onModifyMacroroutine", this);

		}*/

	}
	
	/**
	 * 宏修改
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
