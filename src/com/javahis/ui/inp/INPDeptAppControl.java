package com.javahis.ui.inp;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import jdo.emr.GetWordValue;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.IBlock;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TList;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * 科室间会诊申请
 * 
 * @author lixiang
 * 
 */
public class INPDeptAppControl extends TControl implements DMessageIO {

	/**
	 * 
	 */
	public static final String TN_TEMPLET_PATH = "JHW\\住院病程记录\\会诊记录";
	/**
	 * 医院全称
	 */
	private String hospAreaName = "";
	/**
	 * 英文全称
	 */
	private String hospEngAreaName = "";
	/**
	 * 树根
	 */
	private TTreeNode treeRoot;
	/**
	 * 
	 */
	public static final String TN_EMT_FILENAME = "特殊使用级抗菌药物会诊申请单";
	private String COM = getEktPort();// 泰心会诊名称参数
	/**
	 * 知识库前缀
	 */
	private static final String KNOWLEDGE_STORE_PREFIX = "EMR9";
	/**
	 * WORD对象
	 */
	private static final String TWORD = "WORD";
	/**
	 * 树的名字
	 */
	private static final String TREE_NAME = "TREE";

	private String caseNo;
	private String mrNo;
	/**
	 * 调用会诊申请单标记：会诊申请为false，会诊报告为true
	 * yanjing，20131101
	 */
	private boolean flg = true;
	/**
	 * 动作类名字
	 */
	private static final String actionName = "action.odi.ODIAction";
	
	private String FILE_SEQ;
	/**
	 * 当前编辑状态
	 */
	private String onlyEditType;
	public String getOnlyEditType() {
		return onlyEditType;
	}

	public void setOnlyEditType(String onlyEditType) {
		this.onlyEditType = onlyEditType;
	}

	/**
	 * 得到树
	 * 
	 * @param tag
	 *            String
	 * @return TTree
	 */
	public TTree getTTree(String tag) {
		return (TTree) this.getComponent(tag);
	}

	/**
	 * 就诊日期
	 */
	private Timestamp admDate;

	/**
	 * 住院号
	 */
	private String ipdNo;

	/**
	 * 部门
	 */
	private String deptCode;

	private String str = "";
	/**
	 * 子面板数据 add caoyong 20130929
	 */
	private TParm emrChildParm = new TParm();
	/**
	 * 依赖文件名称
	 */
	private String subFileName;
	public String getSubFileName() {
		return subFileName;
	}

	public void setSubFileName(String subFileName) {
		this.subFileName = subFileName;
	}
	/**
	 * 年
	 */
	private String yearStr="";
	/**
	 * 月
	 */
	private String mouthStr="";
	/**
	 * WORD对象
	 */
	private TWord word;

	public void onInit() {
		super.onInit();
		this.hospAreaName = Manager.getOrganization().getHospitalCHNFullName(
				Operator.getRegion());
		this.hospEngAreaName = Manager.getOrganization()
				.getHospitalENGFullName(Operator.getRegion());
		// 初始化WORD
		initWord();
		// 初始化界面
		initPage();
		loadTree();
		initEven();
	}

	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
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
	
	public void setFlg(boolean flg) {
		this.flg = flg;
	}

	public boolean getFlg() {
		return this.flg;
	}

	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}

	public Timestamp getAdmDate() {
		return admDate;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public TParm getEmrChildParm() {
		return emrChildParm;
	}

	public void setEmrChildParm(TParm emrChildParm) {
		this.emrChildParm = emrChildParm;
	}

	/**
	 * 得到WORD对象
	 * 
	 * @param tag
	 *            String
	 * @return TWord
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}

	/**
	 * 
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		//
		Object obj = this.getParameter();
		if (obj != null) {
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			this.setAdmDate(((TParm) obj).getTimestamp("ADM_DATE"));
			this.setIpdNo(((TParm) obj).getValue("IPD_NO"));
			this.setFlg(((TParm) obj).getBoolean("FLG"));
		} else {
			// test
			this.setMrNo("000000159780");
			this.setCaseNo("130716000433");
		}
		yearStr = caseNo.substring(0, 2);
		// this.setAdmYear(yearStr);
		mouthStr = caseNo.substring(2, 4);
		// 1.通过就诊号取 REG_PATADM 表中 DISEASE_HISTORY， MED_HISTORY
		// ，ASSISTANT_EXAMINE，ASSSISTANT_STUFF
		// 假如不空
		// 取数据， 构造结构化文档;
		// 设置不可编辑
		setMicroField();
		this.getWord().setCanEdit(true);
		// this.messageBox("需要");
		// 编辑状态(非整洁)
		this.getWord().onEditWord();

		// word.clearCapture("estimate");
		// word.pasteString("aaaa1111");
		// 是否存在记录 临床医生 开立的抗菌药物
		// 存在
		// word.clearCapture("txt");
		// this.getWord().
		// word.pasteString(str.substring(0,str.lastIndexOf("\r")));
		//TParm parm = getPhaAntiInfo(this.getMrNo(), this.getCaseNo());
		// if (parm != null) {
		// // 存在 同取对应的值， 赋给抓取框
		//
		// }
	}
	/**
	 * 是否药品已开入的药品信息
	 * 
	 * @param mrNo
	 * @param caseNo
	 * @return
	 */
	private TParm getPhaAntiInfo(String mrNo, String caseNo) {
		String sql = "select ORDER_DESC,SPECIFICATION,MEDI_QTY,FREQ_CODE from PHA_ANTI where case_no='" + caseNo
				+ "' and approve_flg='Y' GROUP BY ORDER_DESC,SPECIFICATION,MEDI_QTY,FREQ_CODE";
		TParm parm = new TParm(this.getDBTool().select(sql));
		str = "";
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				str += parm.getValue("ORDER_DESC", i) + "("
						+ parm.getValue("SPECIFICATION", i) + ")";
				str += " " + parm.getValue("MEDI_QTY", i);
				str += "克";// parm.getValue("MEDI_QTY", i)
				str += " " + parm.getValue("FREQ_CODE", i);
				str += " 静脉点滴 ";// +parm.getValue("FREQ_CODE", i);
				str += "\n";

			}
			word.clearCapture("confirm");
			word.pasteString(str.substring(0, str.lastIndexOf("\n")));
		}
		// word.focusInCaptue("estimate")

		return null;
	}
	/**
	 * 获得药敏实验数据
	 * @return
	 */
	private boolean getMedAntValue(String caseNo){
		String sql="SELECT A.ORDER_CODE, A.MED_APPLY_NO,C.ORDER_DESC, B.CULTURE_CHN_DESC,B.ANTI_CHN_DESC," +
				   " B.OPT_DATE,TO_CHAR(B.OPT_DATE,'YYYY/MM/DD') AS ANT_DATE "+
		           " FROM ODI_ORDER A, MED_LIS_ANTITEST B, SYS_FEE C "+
		           " WHERE A.MED_APPLY_NO = B.APPLICATION_NO AND A.ORDER_CODE = C.ORDER_CODE "+
		           " AND A.CASE_NO='"+caseNo+"' AND A.HIDE_FLG='N' AND A.ORDER_CODE=ORDERSET_CODE AND B.SENS_LEVEL <>'I' "+
		           " ORDER BY B.OPT_DATE,A.MED_APPLY_NO DESC ";
		TParm parm = new TParm(this.getDBTool().select(sql));
		String str = "";
		String antDate="";
		String medApplyNo="";
		if (parm.getCount() > 0) {
			antDate=parm.getValue("ANT_DATE",0);
			medApplyNo=parm.getValue("MED_APPLY_NO",0);
			str += parm.getValue("ANT_DATE", 0)+"\n"+parm.getValue("ORDER_DESC", 0) + " "
			+ parm.getValue("CULTURE_CHN_DESC", 0) ;
			str += " 过敏医嘱:" + parm.getValue("ANTI_CHN_DESC", 0)+";";
			for (int i = 0; i < parm.getCount(); i++) {
				if (antDate.equals(parm.getValue("ANT_DATE",i))
						&&medApplyNo.equals(parm.getValue("MED_APPLY_NO",i))) {
					str += "\t"+ parm.getValue("ANTI_CHN_DESC", i)+";";
				}else{
					str += "\n" +parm.getValue("ANT_DATE", i)+"\n"+parm.getValue("ORDER_DESC", i) + " "
					+ parm.getValue("CULTURE_CHN_DESC", i) ;
					str += " 过敏医嘱:" + parm.getValue("ANTI_CHN_DESC", i)+";";
					antDate=parm.getValue("ANT_DATE",i);
					medApplyNo=parm.getValue("MED_APPLY_NO",i);
				}
			}
			word.clearCapture("medAntValue");
			word.pasteString(str);
			return true;
		}
		return false;
	}
	/**
	 * 微生物送检数据结果
	 * @param caseNo
	 * @return
	 */
	private boolean getMedCulValue(String caseNo){
		String sql="SELECT A.ORDER_CODE, A.MED_APPLY_NO,C.ORDER_DESC, B.CULTURE_CHN_DESC," +
		   " B.OPT_DATE,TO_CHAR(B.OPT_DATE,'YYYY/MM/DD') AS ANT_DATE "+
        " FROM ODI_ORDER A, MED_LIS_CULRPT B, SYS_FEE C "+
        " WHERE A.MED_APPLY_NO = B.APPLICATION_NO AND A.ORDER_CODE = C.ORDER_CODE "+
        " AND A.CASE_NO='"+caseNo+"' AND A.HIDE_FLG='N' AND A.ORDER_CODE=ORDERSET_CODE "+
        " ORDER BY B.OPT_DATE,A.MED_APPLY_NO DESC ";
		TParm parm = new TParm(this.getDBTool().select(sql));
		String str = "";
		String antDate = "";
		String medApplyNo = "";
		if (parm.getCount() > 0) {
			antDate = parm.getValue("ANT_DATE", 0);
			medApplyNo = parm.getValue("MED_APPLY_NO", 0);
			str += parm.getValue("ANT_DATE", 0) + "\n"
					+ parm.getValue("ORDER_DESC", 0) + " "
					+ parm.getValue("CULTURE_CHN_DESC", 0);
			for (int i = 0; i < parm.getCount(); i++) {
				if (antDate.equals(parm.getValue("ANT_DATE", i))
						&& medApplyNo.equals(parm.getValue("MED_APPLY_NO", i))) {
					str += "\t" + parm.getValue("CULTURE_CHN_DESC", i) + ";";
				} else {
					str += "\n" +parm.getValue("ANT_DATE", i) + "\n"
							+ parm.getValue("ORDER_DESC", i) + " "
							+ parm.getValue("CULTURE_CHN_DESC", i)+ ";";
					antDate = parm.getValue("ANT_DATE", i);
					medApplyNo = parm.getValue("MED_APPLY_NO", i);
				}
			}
			word.clearCapture("medCulValue");
			word.pasteString(str);
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 
	 * @param isNull
	 *            是否空数据
	 */
	public void onInsertOrder(boolean isNull) {
		TParm inParm = new TParm();
		inParm.setData("CASE_NO", this.getCaseNo());
		inParm.setData("MR_NO", this.getMrNo());
		// inParm.addListener("onReturnContent", this, "onReturnContent");
		// 空数据
		Object rtn = this.openDialog("%ROOT%\\config\\inp\\INPOrder.x", inParm,
				true);
		onReturnContent(rtn.toString());
		/*
		 * window.setX(ImageTool.getScreenWidth() - window.getWidth());
		 * window.setY(0); window.setVisible(true);
		 */
	}

	public void onReturnContent(String value) {
		ECapture ecap = this.getWord().findCapture("confirm");
		if (ecap == null) {
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		this.getWord().pasteString(value);
		/*
		 * if (!this.getWord().pasteString(value)) { // 执行失败
		 * this.messageBox("E0005"); }
		 */
	}

	/**
	 * 设置宏
	 */
	private void setMicroField() {
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		// allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		// allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		// allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		// allParm.addListener("onMouseLeftPressed", this,
		// "onMouseLeftPressed");
		// CLICKED

		this.getWord().setWordParameter(allParm);
		setMicroFieldOne();
	}
	private void setMicroFieldOne(){

		Map map = this.getDBTool().select(
				"SELECT * FROM MACRO_PATINFO_VIEW WHERE 1=1 AND MR_NO='"
						+ this.getMrNo() + "'");
		TParm parm = new TParm(map);
		if (parm.getErrCode() < 0) {
			// 取得病人基本资料失败
			this.messageBox("E0110");
			return;
		}

		Timestamp tempBirth = parm.getValue("出生日期", 0).length() == 0 ? SystemTool
				.getInstance().getDate()
				: StringTool.getTimestamp(parm.getValue("出生日期", 0),
						"yyyy-MM-dd");
		// 计算年龄
		String age = "0";
		if (this.getAdmDate() != null) {
			age = OdiUtil.getInstance().showAge(tempBirth, this.getAdmDate());
		} else {
			age = "";
		}

		if (parm.getCount() > 0) {
			for (String parmName : parm.getNames()) {
				parm.addData(parmName, parm.getValue(parmName, 0));
			}

		} else {
			for (String parmName : parm.getNames()) {
				parm.addData(parmName, "");
			}

		}
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		parm.addData("年龄", age);
		parm.addData("就诊号", this.getCaseNo());
		parm.addData("病案号", this.getMrNo());
		parm.addData("住院号", this.getIpdNo());
		parm.addData("科室", this.getDeptDesc(Operator.getDept()));
		parm.addData("操作者", Operator.getName());
		parm.addData("申请日期", dateStr);
		parm.addData("日期", StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd"));
		parm.addData("时间", StringTool.getString(SystemTool.getInstance()
				.getDate(), "HH:mm:ss"));
		// parm.addData("EMR_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ssS"));
		parm.addData("病历时间", dateStr);
		parm.addData("入院时间", StringTool.getString(this.getAdmDate(),
				"yyyy/MM/dd HH:mm:ss"));

		parm.addData("出院时间", StringTool.getString(new java.sql.Timestamp(System
				.currentTimeMillis()), "yyyy/MM/dd"));

		parm.addData("调用科室", this.getDeptDesc(this.getDeptCode()));
		parm.addData("SYSTEM", "COLUMNS", "年龄");
		parm.addData("SYSTEM", "COLUMNS", "就诊号");
		parm.addData("SYSTEM", "COLUMNS", "病案号");
		parm.addData("SYSTEM", "COLUMNS", "住院号");
		parm.addData("SYSTEM", "COLUMNS", "科室");
		parm.addData("SYSTEM", "COLUMNS", "操作者");
		parm.addData("SYSTEM", "COLUMNS", "申请日期");
		parm.addData("SYSTEM", "COLUMNS", "日期");
		parm.addData("SYSTEM", "COLUMNS", "时间");
		// parm.addData("SYSTEM","COLUMNS","EMR_DATE");
		parm.addData("SYSTEM", "COLUMNS", "病历时间");
		parm.addData("SYSTEM", "COLUMNS", "入院时间");
		parm.addData("SYSTEM", "COLUMNS", "调用科室");

		// 查询住院基本信息(床号，住院诊断)
		TParm odiParm = new TParm(this.getDBTool().select(
				"SELECT * FROM MACRO_ADMINP_VIEW WHERE CASE_NO='"
						+ this.getCaseNo() + "'"));

		if (odiParm.getCount() > 0) {
			for (String parmName : odiParm.getNames()) {
				parm.addData(parmName, odiParm.getValue(parmName, 0));
			}

		} else {
			for (String parmName : odiParm.getNames()) {
				parm.addData(parmName, "");
			}

		}
		//
		String names[] = parm.getNames();
		TParm obj = (TParm) this.getWord().getFileManager().getParameter();
		String strSex = "";
		for (String temp : names) {
			if ("性别".equals(temp)) {
				if (parm.getInt(temp, 0) == 0) {
					strSex = "未说明";
				} else if (parm.getInt(temp, 0) == 1) {
					strSex = "男";
					// 1.男 2.女
					// this.getWord().setSexControl(parm.getInt(temp, 0));
				} else if (parm.getInt(temp, 0) == 2) {
					strSex = "女";

				} else if (parm.getInt(temp, 0) == 9) {
					strSex = "未说明";
				}

				this.getWord().setMicroField(temp, strSex);
			} else {
				obj.setData(temp, "TEXT", parm.getValue(temp, 0));
				// System.out.println("===temp==="+temp);

				this.getWord().setMicroField(temp, parm.getValue(temp, 0));
				this.getWord().setWordParameter(obj);

			}

		}

	}
	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 拿到科室
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * 结构化病历界面双击事件
	 * 
	 * @param pageIndex
	 *            int
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public void onDoubleClicked(int pageIndex, int x, int y) {
		/*
		 * System.out.println("----------弹出临床医生医嘱开立界面-------------");
		 * System.out.println("pageIndex"+pageIndex); System.out.println("x"+x);
		 * System.out.println("y"+y);
		 */
		String str = "estimate";
		if (word.focusInCaptue("estimate")) {
			// this.messageBox("弹出临床医生医嘱开立界面");
			str = "estimate";
		} else if (word.focusInCaptue("confirm")) {
			// this.messageBox("弹出感染科医生医嘱开立界面");
			str = "confirm";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if ("confirm".equalsIgnoreCase(str)) {
			if (flg) {
				onInsertOrder(true);
			} else {
				this.messageBox("会诊申请不可开立医嘱。");
				return;
			}
		}
	}

	/**
	 * 
	 * @param pageIndex
	 * @param x
	 * @param y
	 */
	/*
	 * public void onMouseLeftPressed(){
	 * System.out.println("------onClicked--------");
	 * 
	 * }
	 */

	/**
	 * 
	 */
	public void onMouseRightPressed() {
		ECheckBoxChoose agree = (ECheckBoxChoose) word.findObject("agree",
				EComponent.CHECK_BOX_CHOOSE_TYPE);
		boolean ss = agree.isChecked();
		if (ss) {
			word.clearCapture("confirm");
			word.pasteString(str);
			// String
			// sql="select * from PHA_ANTI where case_no='"+caseNo+"' and mr_no='"+mrNo+"' and use_flg='N' and approve_flg='N'";
			String sql = "update PHA_ANTI set approve_flg='Y' where case_no='"
					+ this.getCaseNo() + "' and mr_no='" + this.getMrNo()
					+ "' and use_flg='N' and approve_flg='N'";
			this.getDBTool().update(sql);

		} else {
			word.clearCapture("confirm");
		}

	}

	/**
	 * 保存
	 */
	public boolean onSave() {
		//boolean flg = onsaveEmr(false);

		this.getWord().setMessageBoxSwitch(false);
		
		// this.setAdmMouth(mouthStr);
		// 作者(第一次保存)
		// if(this.word.getFileAuthor()==null||this.word.getFileAuthor().length()==0||"admin".equals(this.word.getFileAuthor()))
		this.getWord().setFileAuthor(Operator.getID());
		// 公司
		// TParm asSaveParm = this.getEmrChildParm();
		// System.out.println("asSaveParm::::"+asSaveParm);
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		// this.getWord().setFileCo("BlueCore");
		// // 标题
		// this.getWord().setFileTitle(asSaveParm.getValue("DESIGN_NAME"));
		// // 备注
		// this.getWord().setFileRemark(
		// asSaveParm.getValue("CLASS_CODE") + "|"
		// + asSaveParm.getValue("FILE_PATH") + "|"
		// + asSaveParm.getValue("FILE_NAME"));
		// 创建时间
		this.getWord().setFileCreateDate(dateStr);
		// 最后修改人
		this.getWord().setFileLastEditUser(Operator.getID());
		// 最后修改日期`
		this.getWord().setFileLastEditDate(dateStr);
		// 最后修改IP
		this.getWord().setFileLastEditIP(Operator.getIP());
		// /*
		// * System.out.println("==save filePath==" +
		// * asSaveParm.getValue("FILE_PATH"));
		// * System.out.println("==save fileName==" +
		// * asSaveParm.getValue("FILE_NAME"));
		// */
		// // 另存为
		// asSaveParm=new TParm();
		// asSaveParm.setData("FILE_PATH",TN_TEMPLET_PATH);
		// asSaveParm.setData("FILE_NAME",TN_EMT_FILENAME);
		String emrName ="";
		String fileName ="";
		TParm asSaveParm=new TParm();
		if (this.getOnlyEditType().equals("NEW")) {
			TParm action = new TParm(this.getDBTool().select(
					"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO"
							+ " FROM EMR_FILE_INDEX" + " WHERE CASE_NO='"
							+ this.getCaseNo() + "'"));
			int index = action.getInt("MAXFILENO", 0);
			emrName = caseNo + "_" + TN_EMT_FILENAME + "_" + index;
			fileName = "JHW" + "\\" + yearStr + "\\" + mouthStr + "\\"
					+ this.getMrNo();
			asSaveParm.setData("FILE_SEQ",index);
		}else{
			emrName = caseNo + "_" + TN_EMT_FILENAME + "_" + FILE_SEQ;
			fileName = "JHW" + "\\" + yearStr + "\\" + mouthStr + "\\"
					+ this.getMrNo();
			asSaveParm.setData("FILE_SEQ",FILE_SEQ);
		}
		boolean success = this.getWord().onSaveAs(fileName, emrName, 3);
		asSaveParm.setData("FILE_PATH",fileName);
		asSaveParm.setData("FILE_NAME",emrName);
		asSaveParm.setData("CASE_NO",caseNo);
		asSaveParm.setData("MR_NO",mrNo);
		asSaveParm.setData("IPD_NO",ipdNo);
		asSaveParm.setData("CLASS_CODE",getEmrChildParm().getValue("CLASS_CODE"));
		asSaveParm.setData("SUBCLASS_CODE", getEmrChildParm().getData("SUBCLASS_CODE"));
		asSaveParm.setData("DISPOSAC_FLG","N");
		asSaveParm.setData("DESIGN_NAME", getEmrChildParm().getValue("EMT_FILENAME")+ "(" + dateStr + ")");
		
		if (saveEmrFile(asSaveParm)) {
			if (success) {
				this.messageBox("保存成功");
				loadTree();
			}else{
				this.messageBox("保存失败");
			}
		}else{
			if (success) {
				this.messageBox("保存失败");
			}
		}
		return true;
	}
	/**
	 * 保存EMR文件
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
//		// $$=================假如是值班医生=======================$$//
//		if (isDutyDrList()) {
//			parm.setData("CURRENT_USER", Operator.getID());
//			parm.setData("CHK_USER1", "");
//			parm.setData("CHK_DATE1", "");
//			parm.setData("CHK_USER2", "");
//			parm.setData("CHK_DATE2", "");
//			parm.setData("CHK_USER3", "");
//			parm.setData("CHK_DATE3", "");
//			parm.setData("COMMIT_USER", "");
//			parm.setData("COMMIT_DATE", "");
//		}
//		// $$=================假如是值班医生=======================$$//
		if (this.getOnlyEditType().equals("NEW")) {
			parm.setData("CREATOR_USER", Operator.getID());
			TParm result = TIOM_AppServer.executeAction(actionName,
					"saveNewEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
			return falg;
		}
		// this.messageBox("type"+this.getOnlyEditType());

		if (this.getOnlyEditType().equals("ONLYONE")) {
			parm.setData("CANPRINT_FLG","N");
			parm.setData("MODIFY_FLG","Y");
			parm.setData("CURRENT_USER","");
			parm.setData("CHK_USER1","");
			parm.setData("CHK_DATE1",SystemTool.getInstance().getDate());
			parm.setData("CHK_USER1","");
			parm.setData("CHK_USER2","");
			parm.setData("CHK_DATE2",SystemTool.getInstance().getDate());
			parm.setData("CHK_USER3","");
			parm.setData("CHK_DATE3",SystemTool.getInstance().getDate());
			parm.setData("COMMIT_USER","");
			parm.setData("COMMIT_DATE",SystemTool.getInstance().getDate());
			parm.setData("IN_EXAMINE_USER","");
			parm.setData("IN_EXAMINE_DATE",SystemTool.getInstance().getDate());
			parm.setData("DS_EXAMINE_USER","");
			parm.setData("DS_EXAMINE_DATE",SystemTool.getInstance().getDate());
			parm.setData("PDF_CREATOR_USER","");
			parm.setData("PDF_CREATOR_DATE",SystemTool.getInstance().getDate());
			TParm result = TIOM_AppServer.executeAction(actionName,
					"writeEmrFile", parm);
			if (result.getErrCode() < 0) {
				falg = false;
			}
		}
		return falg;
	}

	/**
	 * TREE树初始化
	 */
	public void loadTree() {
		treeRoot = this.getTTree(TREE_NAME).getRoot();
		treeRoot.setText("住院");
		treeRoot.setEnText("Hospital");
		treeRoot.setType("Root");
		treeRoot.removeAllChildren();
		boolean queryFlg = false;
		// 拿到节点数据
		TParm parm = this.getRootNodeData();
		if (parm.getInt("ACTION", "COUNT") == 0) {
			// 没有设置EMR模板！请检查数据库设置
			this.messageBox("E0105");
			return;
		}
		int rowCount = parm.getInt("ACTION", "COUNT");
		for (int i = 0; i < rowCount; i++) {
			TParm mainParm = parm.getRow(i);
			// 加节点 EMR_CLASS表中的STYLE字段
			TTreeNode node = new TTreeNode();
			node.setID(mainParm.getValue("CLASS_CODE"));
			node.setText(mainParm.getValue("CLASS_DESC"));
			node.setEnText(mainParm.getValue("ENG_DESC"));
			// 设置菜单显示样式
			node.setType("Root");
			node.setGroup(mainParm.getValue("CLASS_CODE"));
			node.setValue(mainParm.getValue("SYS_PROGRAM"));
			node.setData(mainParm);

			// 加入树根
			treeRoot.add(node);
			TTreeNode childrenNode = new TTreeNode(mainParm
					.getValue("CLASS_DESC"), mainParm.getValue("CLASS_STYLE"));
			childrenNode.setID(mainParm.getValue("CLASS_CODE"));
			childrenNode.setGroup(mainParm.getValue("CLASS_CODE"));
			// // mainParm.getValue("SYS_PROGRAM")?????暂时没有
			treeRoot.findNodeForID(mainParm.getValue("CLASS_CODE")).add(
					childrenNode);
			//
			// // 是叶节点的加入子文件
			// if (((String) allChildsParm.getValue("LEAF_FLG", j))
			// .equalsIgnoreCase("Y")) {
			// $$=====Add by lx 2012/10/10 start=======$$//
			TParm childParm = new TParm();
			// 判断是否是临床知识库，
			// 假如是则,加入对应的对应的知识库文件
			if (isKnowLedgeStore(mainParm.getValue("CLASS_CODE"))) {
				childParm = getChildNodeDataByKnw(mainParm
						.getValue("CLASS_CODE"));
				// $$=====Add by lx 2012/10/10 end=======$$//
			} else {
				// 查询子文件
				childParm = this.getRootChildNodeData(mainParm
						.getValue("CLASS_CODE"), queryFlg);
			}
			int rowCldCount = childParm.getInt("ACTION", "COUNT");
			for (int z = 0; z < rowCldCount; z++) {
				TParm chlidParmTemp = childParm.getRow(z);
				TTreeNode nodeChlid = new TTreeNode();
				nodeChlid.setID(chlidParmTemp.getValue("FILE_NAME"));
				nodeChlid.setText(chlidParmTemp.getValue("DESIGN_NAME"));
				nodeChlid.setType("4");
				nodeChlid.setGroup(chlidParmTemp.getValue("CLASS_CODE"));
				nodeChlid.setValue(chlidParmTemp.getValue("SUBCLASS_CODE"));
				nodeChlid.setData(chlidParmTemp);
				childrenNode.add(nodeChlid);
			}
			// }

			// }
			// // $$=============end add by lx 病历模版分类树修改; ================$$//
		}
		this.getTTree(TREE_NAME).update();
	}

	/**
	 * 是否是知识库
	 * 
	 * @param classcode
	 * @return
	 */
	private boolean isKnowLedgeStore(String classcode) {
		if (classcode.startsWith(KNOWLEDGE_STORE_PREFIX)) {
			return true;
		}
		return false;
	}

	public TParm getRootNodeData() {
		TParm result = new TParm(
				this.getDBTool().select(
								"SELECT B.CLASS_DESC,B.ENG_DESC,B.CLASS_STYLE,B.CLASS_CODE,A.SYS_PROGRAM,A.SEQ,B.PARENT_CLASS_CODE "
										+ "FROM EMR_TREE A,EMR_CLASS B "
										+ " WHERE  B.PARENT_CLASS_CODE = 'EMR10' AND B.CLASS_CODE='"
										+ COM
										+ "' AND SYSTEM_CODE='ODI' AND A.CLASS_CODE=B.PARENT_CLASS_CODE ORDER BY B.SEQ,A.CLASS_CODE"));
		return result;
	}

	/**
	 * 通过知识库叶节点取对应知识库模版
	 * 
	 * @param classCode
	 * @return
	 */
	public TParm getChildNodeDataByKnw(String classCode) {
		TParm result = new TParm();
		String sql = "SELECT   SUBCLASS_CODE, CLASS_CODE, EMT_FILENAME FILE_NAME,";
		sql += "TEMPLET_PATH FILE_PATH, SUBCLASS_DESC DESIGN_NAME ";
		sql += "FROM EMR_TEMPLET ";
		sql += "WHERE CLASS_CODE = '" + classCode + "' ";
		sql += "ORDER BY SEQ";
		result = new TParm(this.getDBTool().select(sql));
		return result;
	}
	/**
	 * 注册事件
	 */
	public void initEven() {
		// 单击选中树项目
		addEventListener(TREE_NAME + "->" + TTreeEvent.DOUBLE_CLICKED,
				"onTreeDoubled");
	}
	/**
	 * 拿到根节点数据
	 * 
	 * @return TParm
	 */
	public TParm getRootChildNodeData(String classCode, boolean queryFlg) {
		TParm result = new TParm();
		String myTemp =
		// 公共模板
		" AND ((B.DEPT_CODE IS NULL AND B.USER_ID IS NULL) "
				// 科室模板
				+ " OR (B.DEPT_CODE = '" + Operator.getDept()
				+ "' AND B.USER_ID IS NULL) "
				// 个人模板
				+ " OR B.USER_ID = '" + Operator.getID() + "') ";
		String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,"
				+ " A.CREATOR_USER,A.CREATOR_DATE,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,"
				+ " A.CHK_USER1,A.CHK_DATE1,A.CHK_USER2,A.CHK_DATE2,A.CHK_USER3,A.CHK_DATE3,"
				+ " A.COMMIT_USER,A.COMMIT_DATE,A.IN_EXAMINE_USER,A.IN_EXAMINE_DATE,A.DS_EXAMINE_USER,A.DS_EXAMINE_DATE,A.PDF_CREATOR_USER,A.PDF_CREATOR_DATE,"
				+ " B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG,A.REPORT_FLG ";
		sql += " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"
				+ this.getCaseNo()
				+ "' AND A.CLASS_CODE='"
				+ classCode
				+ "' AND A.DISPOSAC_FLG<>'Y' AND IPD_FLG='Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE "
				+ myTemp + " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
		result = new TParm(this.getDBTool().select(sql));

		return result;
	}

	/**
	 * 得到debug标记
	 * 
	 * @return
	 */
	public String getEktPort() {
		String com = "";
		com = getProp().getString("", "inp.name");
		if (com == null || com.trim().length() <= 0) {
			// System.out.println("配置文件医疗卡com标记错误！");
		}
		return com;
	}

	/**
	 * 读取 TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			// System.out.println("TConfig.x 文件没有找到！");
		}
		return config;
	}
	/**
	 * 点击树
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeDoubled(Object parm) {	
		//初始化，追加标记
		//this.setApplend(false);	
		TTreeNode node = (TTreeNode) parm;
		TParm emrParm = (TParm) node.getData();
		if (emrParm != null && emrParm.getValue("REPORT_FLG").equals("Y")) {
			// 打开病历
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			FILE_SEQ=emrParm.getValue("FILE_SEQ");
			// 设置不可编辑
			this.getWord().setCanEdit(true);
			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);

		} else {
			//System.out.println("======双击打开======"+emrParm);
			if (!this.checkInputObject(parm)) {
				return;
			}
//			// 1.病历查看权限控制：
//			if (!checkDrForOpen()) {
//				this.messageBox("E0011"); // 权限不足
//				return;
//			}

			if (!node.getType().equals("4")) {
				return;
			}
			//=====add by lx 2012/10/10加入是临床知识库 ====start//
			if(this.isKnowLedgeStore(emrParm.getValue("SUBCLASS_CODE"))){
				//打开知识库模版
			    if (!this.getWord().onOpen(
							emrParm.getValue("FILE_PATH"),
							emrParm.getValue("FILE_NAME"), 2, false))
				// 设置可编辑
				this.getWord().setCanEdit(true);
			    this.getTMenuItem("save").setEnabled(false);
			    //setTMenuItem(false);
			    this.getWord().onPreviewWord();
				return;
			}
			//=====add by lx 2012/10/10加入临床知识库 ====End//
			// 打开病历
			if (!this.getWord().onOpen(emrParm.getValue("FILE_PATH"),
					emrParm.getValue("FILE_NAME"), 3, true)) {
				return;
			}
			FILE_SEQ=emrParm.getValue("FILE_SEQ");
			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			this.getWord().setWordParameter(allParm);
			getPhaAntiInfo(mrNo, caseNo);//会诊医嘱数据
			// 设置宏
			//setMicroField(false);
			TParm sexP = new TParm(this.getDBTool().select(
					"SELECT SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"
							+ this.getMrNo() + "'"));

			if (sexP.getInt("SEX_CODE", 0) == 9) {
				this.getWord().setSexControl(0);
			} else {
				this.getWord().setSexControl(sexP.getInt("SEX_CODE", 0));
			}
			// 设置编辑状态
			this.setOnlyEditType("ONLYONE");
			// 设置当前编辑数据
			this.setEmrChildParm(emrParm);
			//可编辑
			this.getWord().setCanEdit(true);
			//编辑状态(非整洁)
			this.getWord().onEditWord();
			//setTMenuItem(true);
			//setTMenuItem(false);
			//$$===========add by lx 2012-06-18 加入病历已经提交不可修改start===============$$//
//			if(getSubmitFLG()){
//				this.getWord().onPreviewWord();
//				LogOpt(emrParm);
//				setPrintAndMfyFlg(emrParm);
//				//this.messageBox("已提交病历，不可修改");
//			}
			//$$===========add by lx 2012-06-18加入病历已经提交不可修改 end===============$$//
			// 申请编辑
//			else if (ruleType.equals("2") || ruleType.equals("3")) {
//				onEdit();
//				setCanEdit(emrParm);
//				LogOpt(emrParm);
//				setPrintAndMfyFlg(emrParm);
//			} else {
//				this.getWord().onPreviewWord();
//				LogOpt(emrParm);
//				setPrintAndMfyFlg(emrParm);
//			}

		}
	}
	/**
	 * 对象非空验证
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public boolean checkInputObject(Object obj) {
		if (obj == null) {
			return false;
		}
		String str = String.valueOf(obj);
		if (str == null) {
			return false;
		} else if ("".equals(str.trim())) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * 拿到菜单
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}
	/**
	 * 新增病历(可以追加)
	 */
	public void onAddMenu() {
		// 部分读写权限管控
		openChildDialog();
		this.getWord().fixedTryReset(this.getMrNo(), this.getCaseNo());
	}
	/**
	 * 打开子窗口
	 */
	public void openChildDialog() {

		// 模板类型
		String emrClass = this.getTTree(TREE_NAME).getSelectNode().getGroup();
		String nodeName = this.getTTree(TREE_NAME).getSelectNode().getText();
		String emrType = this.getTTree(TREE_NAME).getSelectNode().getType();
		String programName = this.getTTree(TREE_NAME).getSelectNode()
				.getValue();
		// 如果是调用HIS程序则这样处理
		if (programName.length() != 0) {
			TParm hisParm = new TParm();
			hisParm.setData("CASE_NO", this.getCaseNo());
			this.openDialog(programName, hisParm);
			return;
		}
		String myTemp =
			// 公共模板
			" AND ((DEPT_CODE IS NULL AND USER_ID IS NULL) "
					// 科室模板
					+ " OR (DEPT_CODE = '" + Operator.getDept()
					+ "' AND USER_ID IS NULL) "
					// 个人模板
					+ " OR USER_ID = '" + Operator.getID() + "') ";
		TParm parm = new TParm(
				this.getDBTool().select(
								"SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE" +
								" FROM EMR_TEMPLET" +
								" WHERE CLASS_CODE='"
										+ emrClass
										+ "' AND IPD_FLG='Y' AND (SYSTEM_CODE='ODI' OR SYSTEM_CODE IS NULL) "
										+ myTemp
										+ " ORDER BY SEQ"));

		parm.setData("SYSTEM_CODE", "ODI");
		parm.setData("NODE_NAME", nodeName);
		parm.setData("TYPE", emrType);
		parm.setData("DEPT_CODE", Operator.getDept());
		Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRChildUI.x", parm);
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm action = (TParm) obj;
		// 预启动程序名
		//String runProgarmName = action.getValue("RUN_PROGARM");

		TParm runParm = new TParm();
		// 判断类型是否可以打开
		//String styleClass = action.getValue("CLASS_STYLE");
		//System.out.println("styleClass:::sDF:::"+action);
		
		String templetPath = action.getValue("TEMPLET_PATH");
		String templetName = action.getValue("EMT_FILENAME");
		//this.messageBox("===templetName==="+templetName);
        try{
        	openTempletNoEdit(templetPath, templetName, runParm);
        }catch (Exception e) {

		}
		this.setEmrChildParm(action);
		// 可能出问题
		//this.setEmrChildParm(this.getFileServerEmrName());
		// 设置允许打印和允许修改标识（隐藏）
		//setCanPrintFlgAndModifyFlg("", "", false);
	}
	/**
	 * 拿到出院时间
	 * 
	 * @return TParm
	 */
	public TParm getAdmInpDSData() {
		TParm admParm = new TParm(this.getDBTool().select(
				"SELECT DS_DATE FROM ADM_INP WHERE CASE_NO='"
						+ this.getCaseNo() + "'"));
		if (admParm.getCount() < 0) {
			return null;
		}
		return admParm;
	}
	/**
	 * 打开模版
	 * 
	 * @param templetPath
	 *            String
	 * @param templetName
	 *            String
	 */
	public void openTempletNoEdit(String templetPath, String templetName,
			TParm parm) {
		// 新建清除修改标志;
		
		this.getWord().onOpen(templetPath, templetName, 2, false);
		// 设置不可编辑
		//setMicroField();
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		
		// allParm.addListener("onMouseLeftPressed", this,
		// "onMouseLeftPressed");
		// CLICKED
		
		
		this.getWord().setWordParameter(allParm);
		setMicroFieldOne();
		// 设置宏
		//setMicroField(true);
		String comlunName[] = parm.getNames();
		for (String temp : comlunName) {
			this.getWord().setMicroField(temp, parm.getValue(temp));
			this.setCaptureValueArray(temp, parm.getValue(temp));
		}
		//setSingleDiseBasicData();//add by wanglong 20121115  设置单病种基本信息
        //setOPEData();//add by wanglong 20130514 设置手术基本信息
		// 设置编辑状态
		this.setOnlyEditType("NEW");
		// 设置不可以编辑
		//this.getWord().setCanEdit(false);
		//setTMenuItem(false);
		onEdit();
		// 申请编辑
//		if (ruleType.equals("2") || ruleType.equals("3")) {
//			
//		}
	}
	private void onEdit(){
		// 可编辑
		this.getWord().setCanEdit(true);
	}
	/**
	 * 设置抓取框
	 * 
	 * @param name
	 *            String
	 * @param value
	 *            String
	 */
	public void setCaptureValueArray(String name, String value) {
		//原来名子无重复的,现在需要在Tword类中加个方法 通过宏名取控件方法， 加值； 同名会覆盖以前的值；
		boolean isSetCaptureValue = this.setCaptureValue(name, value);
		if (!isSetCaptureValue) {
			ECapture ecap = this.getWord().findCapture(name);
			if (ecap == null) {
				return;
			}
			ecap.setFocusLast();
			ecap.clear();
			this.getWord().pasteString(value);

		}

	}
	/**
	 * 通过宏名设置抓取框值；
	 * 
	 * @param macroName
	 *            String
	 * @param value
	 *            String
	 */
	private boolean setCaptureValue(String macroName, String value) {
		boolean isSetValue = false;
		TList components = this.getWord().getPageManager().getComponentList();
		int size = components.size();
		for (int i = 0; i < size; i++) {
			EPage ePage = (EPage) components.get(i);
			// EComponent
			// this.messageBox("EPanel size"+ePage.getComponentList().size());
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				if (ePanel != null) {
					for (int z = 0; z < ePanel.getBlockSize(); z++) {
						IBlock block = (IBlock) ePanel.get(z);
						// this.messageBox("type"+block.getObjectType());
						// this.messageBox("value"+block.getBlockValue());
						// 9为抓取框;
						if (block != null) {
							if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
								EComponent com = block;
								ECapture capture = (ECapture) com;

								if (capture.getMicroName().equals(macroName)) {
									// this.messageBox("microName"+capture.getMicroName());
									// 是开始，则赋值;
									if (capture.getCaptureType() == 0) {
										capture.setFocusLast();
										capture.clear();
										this.getWord().pasteString(value);
										isSetValue = true;
										break;
									}
								}
							}
						}
						if (isSetValue) {
							break;
						}
					}
					if (isSetValue) {
						break;
					}

				}

			}

		}
		return isSetValue;
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
		TParm childParm = this.getEmrChildParm();
		String templetName = childParm.getValue("EMT_FILENAME");
		//System.out.println("=============getFileServerEmrName templetName================"+templetName);
		
		TParm action = new TParm(
				this.getDBTool().select(
								"SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO" +
								" FROM EMR_FILE_INDEX" +
								" WHERE CASE_NO='"
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
		emrParm.setData("FILE_PATH", "JHW" + "\\" + yearStr + "\\"
				+ mouthStr + "\\" + this.getMrNo());
		emrParm.setData("DESIGN_NAME", templetName + "(" + dateStr + ")");
		emrParm.setData("DISPOSAC_FLG", "N");
		emrParm.setData("TYPEEMR", childParm.getValue("TYPEEMR"));
		emrParm.setData("EMT_FILENAME", childParm.getValue("EMT_FILENAME"));
		return emrParm;
	}
	/**
	 * 打印
	 */
	public void onPrintXDDialog() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().print();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}
	public void onMedApply(){
		if (caseNo==null || word==null) {
			this.messageBox("请选择要导入的病历");
			return ;
		}
		boolean flg=getMedCulValue(caseNo);
		boolean flgOne=getMedAntValue(caseNo);
		if (!flgOne&&!flg) {
			this.messageBox("没有需要导入的数据");
			return;
		}
		this.messageBox("导入成功");
		
	}
}
