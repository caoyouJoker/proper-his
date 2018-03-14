package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import jdo.clp.CLPSingleDiseTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 单病种病例统计</p>
 *
 * <p>Description: 单病种病例统计</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author WangLong 20120926
 * @version 1.0
 */
public class CLPSingleDiseControl extends TControl {

	private final String Header_E = "就诊号,120;病案号,120;姓名,90;性别,60,SEX;身份,120,CTZ1_CODE;挂号时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;单病种,120,PAT_DISE_CODE;科室,100,DEPT_CODE;医生,90,USER_ID";// 设置表格的标题（急诊）
	private final String ParmMap_E = "CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CTZ1_CODE;REG_DATE;DISE_CODE;DEPT_CODE;DR_CODE";// 设置表格的对应Map（急诊）
	private final String Header_I = "就诊号,120;病案号,120;姓名,90;性别,60,SEX;身份,120,CTZ1_CODE;入院时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;出院时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;单病种,120,PAT_DISE_CODE;科室,100,DEPT_CODE;经治医生,90,USER_ID";// 设置表格的标题（住院）
	private final String ParmMap_I = "CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CTZ1_CODE;IN_DATE;OUT_DATE;DISE_CODE;DEPT_CODE;DR_CODE";// 设置表格的对应Map（住院）

	private TParm patParm;
	private TTabbedPane tTabbedPane_0;// 选项卡组件
	private TTable patTable;// 病患信息Table
	private int lastSelectedIndex = 0;// 上次点选的页签索引
	private TMenuItem edit;// 工具栏“调用”按钮
	private TMenuItem export;// 工具栏“导出”按钮
	private String diseCode = "";// 单病种类型
	private String admType = "";// 门级别
	private String caseNo = "";// 就诊号
	
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		Object obj = this.getParameter();
		if (!obj.equals("")) {
			TParm parm = (TParm) obj;
			admType = parm.getValue("ADM_TYPE");
			caseNo = parm.getValue("CASE_NO");
		}
		getAllComponent();// 获取全部输入框组件
		initControler();// 注册监听事件
		onClear();// 清空
		initPage();// 初始化界面
	}

	/**
	 * 获取全部输入框组件
	 */
	public void getAllComponent() {
		edit = (TMenuItem) this.getComponent("editEMR");// 获取工具栏选项
		export = (TMenuItem) this.getComponent("export");
		tTabbedPane_0 = (TTabbedPane) this.getComponent("tTabbedPane_0");// 选项卡组件
		patTable = (TTable) this.getComponent("PAT_TABLE");//
	}

	/**
	 * 注册监听事件
	 */
	public void initControler() {
		callFunction("UI|PAT_TABLE|addEventListener", "PAT_TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		edit.setEnabled(false);
		this.callFunction("UI|export|setEnabled", false);
		this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
		Timestamp date = SystemTool.getInstance().getDate();
		String transDate = StringTool.getString(date, "yyyy/MM/dd");
		this.setValue("PAT_START_DATE", transDate);
		this.setValue("PAT_END_DATE", transDate);
		this.setValue("SD_START_DATE", transDate);
		this.setValue("SD_END_DATE", transDate);
		this.setValue("PAT_ADM_TYPE", admType.equals("") ? "E" : admType);// 默认选择“急诊”
		changeTableTandM();// 更改表格标题和内容映射
		if (admType.equals("E") || admType.equals("I")) {
			tTabbedPane_0.setEnabledAt(1, false);
			if (!caseNo.equals("")) {
				initPat();
			}
		} else {
			this.callFunction("UI|save|setEnabled", false);
			this.callFunction("UI|delete|setEnabled", false);
		}
	}

	/**
	 * 页签切换事件
	 */
	public void onTabChange() {
		int selectedIndex = tTabbedPane_0.getSelectedIndex();// 获取页签索引
		if (selectedIndex <= 0) {// 查询病患页签
			TParm patData = (TParm) callFunction("UI|PAT_TABLE|getShowParmValue");
			export.setEnabled(false);
			if (patData.getCount() > 0) {
				edit.setEnabled(true);
			}
			lastSelectedIndex = selectedIndex;
		} else if (selectedIndex == 1) {// 查询单病种页签
			TParm sdData = (TParm) callFunction("UI|SD_TABLE|getShowParmValue");
			edit.setEnabled(false);
			if (sdData.getCount() > 0) {
				export.setEnabled(true);
			}
			lastSelectedIndex = selectedIndex; // 记录此次点选的页签索引
		}
	}

	/**
	 * Table行单事件
	 */
	public void onTableClicked(int row) {
		if (row < 0) {
			return;
		}
		TParm data = (TParm) callFunction("UI|PAT_TABLE|getParmValue");
		patParm = new TParm();
		patParm = data.getRow(row);
		this.callFunction("UI|PAT_DISE_CODE|setValue", patParm.getData("DISE_CODE"));
		caseNo = patParm.getData("CASE_NO") + "";
		if ((patParm.getData("DISE_CODE") == null) || patParm.getData("DISE_CODE").equals("")) {
			diseCode = "";
			this.setValue("DEPT_CODE", patParm.getValue("DEPT_CODE"));
			this.setValue("MR_NO", patParm.getValue("MR_NO"));
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", true);
			this.callFunction("UI|save|setEnabled", true);
			this.callFunction("UI|delete|setEnabled", false);
			this.callFunction("UI|editEMR|setEnabled", false);
		} else {
			diseCode = patParm.getData("DISE_CODE") + "";
			if (!this.getParameter().equals("")){
				TParm returnParm = new TParm();
				returnParm.setData("DISE_CODE", diseCode);
				this.setReturnValue(returnParm);// 出参
			}
			this.setValue("DEPT_CODE", patParm.getValue("DEPT_CODE"));
			this.setValue("MR_NO", patParm.getValue("MR_NO"));
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
			this.callFunction("UI|save|setEnabled", false);
			this.callFunction("UI|delete|setEnabled", true);
			this.callFunction("UI|editEMR|setEnabled", true);
		}
	}

	/**
	 * 初始化病患信息
	 */
	private void initPat() {
		if (caseNo.equals("")) {
			return;
		}
		TParm parm = new TParm();// 入参
		parm.setData("START_DATE", new Timestamp(0));
		parm.setData("END_DATE", new Timestamp(new Date().getTime()));
		parm.setData("CASE_NO", caseNo);
		TParm patInfo = new TParm();
		if (admType.equals("E")) {
			patInfo = CLPSingleDiseTool.getInstance().queryPatInfoFromE(parm);
		} else if (admType.equals("I")) {
			patInfo = CLPSingleDiseTool.getInstance().queryPatInfoFromI(parm);
		}
		changeTableTandM();
		if (patInfo.getErrCode() < 0) {
			messageBox(patInfo.getErrText());
			return;
		}
		this.callFunction("UI|PAT_TABLE|setParmValue", new TParm());
		if (patInfo.getCount() <= 0) {
			messageBox("E0008");// 查无资料
			return;
		}
		this.callFunction("UI|query|setEnabled", false);
		edit.setEnabled(false);
		this.callFunction("UI|clear|setEnabled", false);
		this.callFunction("UI|PAT_START_DATE|setEnabled", false);
		this.callFunction("UI|PAT_END_DATE|setEnabled", false);
		this.setValue("PAT_ADM_TYPE", admType);
		this.callFunction("UI|PAT_ADM_TYPE|setEnabled", false);
		this.setValue("DEPT_CODE", patInfo.getValue("DEPT_CODE", 0));
		this.callFunction("UI|DEPT_CODE|setEnabled", false);
		this.setValue("MR_NO", patInfo.getValue("MR_NO", 0));
		this.callFunction("UI|MR_NO|setEnabled", false);
		this.callFunction("UI|PAT_COUNT|setValue", patInfo.getCount() + "");
		this.callFunction("UI|PAT_TABLE|setParmValue", patInfo);
		if ((patInfo.getData("DISE_CODE", 0) == null) || patInfo.getData("DISE_CODE", 0).equals("")) {
			diseCode="";
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", true);
			this.callFunction("UI|PAT_DISE_CODE|setValue", "");
			this.callFunction("UI|save|setEnabled", true);
			this.callFunction("UI|delete|setEnabled", false);
			this.callFunction("UI|editEMR|setEnabled", false);
		} else {
			diseCode=patInfo.getData("DISE_CODE", 0)+"";
			if (this.getParameter() != null){
				TParm returnParm = new TParm();
				returnParm.setData("DISE_CODE", diseCode);
				this.setReturnValue(returnParm);// 出参
			}
			this.callFunction("UI|PAT_DISE_CODE|setValue", patInfo.getData("DISE_CODE", 0));
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
			this.callFunction("UI|save|setEnabled", false);
			this.callFunction("UI|delete|setEnabled", true);
			this.callFunction("UI|editEMR|setEnabled", true);
		}
		patParm = patInfo.getRow(0);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (lastSelectedIndex <= 0) {
			admType = this.getValue("PAT_ADM_TYPE") + "";// 门级别
			if (admType.equals("")) {
				messageBox("请选择门级别");
				return;
			}
			queryPat();// 查询病患
		} else {
			querySingleDise();// 查询单病种
		}
	}

	/**
	 * 查询病患信息
	 */
	private void queryPat() {
		Timestamp startDate = (Timestamp) this.getValue("PAT_START_DATE");
		Timestamp endDate = (Timestamp) this.getValue("PAT_END_DATE");
		endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
		Timestamp date = SystemTool.getInstance().getDate();
		String transDate = StringTool.getString(date, "yyyy/MM/dd");
		if (startDate == null) {
			messageBox("开始时间设置不正确");
			this.setValue("PAT_START_DATE", transDate);
			return;
		}
		if (endDate == null) {
			messageBox("结束时间设置不正确");
			this.setValue("PAT_END_DATE", transDate);
			return;
		}
		String deptCode = (this.getValue("DEPT_CODE") + "").trim();
		String mrNo = (this.getValue("MR_NO") + "").trim();
		TParm parm = new TParm();// 入参
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		if (!StringUtil.isNullString(deptCode)) {
			parm.setData("DEPT_CODE", deptCode);
		}
		if (!StringUtil.isNullString(mrNo)) {
			parm.setData("MR_NO", mrNo);
		}
		TParm patInfo = new TParm();
		if (admType.equals("E")) {// 急诊
			patInfo = CLPSingleDiseTool.getInstance().queryPatInfoFromE(parm);
		} else if (admType.equals("I")) {// 住院
			patInfo = CLPSingleDiseTool.getInstance().queryPatInfoFromI(parm);
		}
		changeTableTandM();// 改变表格标题栏和映射
		if (patInfo.getErrCode() < 0) {
			messageBox(patInfo.getErrText());
			return;
		}
		this.callFunction("UI|PAT_TABLE|setParmValue", new TParm());
		if (patInfo.getCount() <= 0) {
			messageBox("E0008");// 查无资料
			return;
		}
		this.callFunction("UI|PAT_COUNT|setValue", patInfo.getCount() + "");
		this.callFunction("UI|PAT_TABLE|setParmValue", patInfo);
	}

	/**
	 * 查询单病种信息
	 */
	private void querySingleDise() {
		String admType = this.getValueString("SD_ADM_TYPE").trim();//门级别
		String diseCode = this.getValueString("SD_DISE_CODE").trim();
		Timestamp startDate = (Timestamp) this.getValue("SD_START_DATE");
		Timestamp endDate = (Timestamp) this.getValue("SD_END_DATE");
		endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);// 到这一天的23:59:59.999
		Timestamp date = SystemTool.getInstance().getDate();
		String transDate = StringTool.getString(date, "yyyy/MM/dd");
		if (startDate == null) {
			messageBox("开始时间设置不正确");
			this.setValue("SD_START_DATE", transDate);
			return;
		}
		if (endDate == null) {
			messageBox("结束时间设置不正确");
			this.setValue("SD_END_DATE", transDate);
			return;
		}
		TParm parm = new TParm();// 入参
		if(!admType.equals("")){
			parm.setData("ADM_TYPE",admType);
		}
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		if(!diseCode.equals("")){
			parm.setData("DISE_CODE",diseCode);
		}
		TParm singleDiseInfo = CLPSingleDiseTool.getInstance().querySDData(parm);

		if (singleDiseInfo.getErrCode() < 0) {
			messageBox(singleDiseInfo.getErrText());
			return;
		}
		this.callFunction("UI|SD_TABLE|setParmValue", new TParm());
		if (singleDiseInfo.getCount() <= 0) {
			messageBox("E0008");// 查无资料
			return;
		}
		this.callFunction("UI|SD_COUNT|setValue", singleDiseInfo.getCount()+ "");
		this.callFunction("UI|SD_TABLE|setParmValue", singleDiseInfo);
		export.setEnabled(true);
	}

	/**
	 * 保存单病种属性
	 */
	public void onSave() {
		diseCode = this.getValue("PAT_DISE_CODE") + "";// 单病种类型
		if (diseCode.trim().equals("")) {
			messageBox("请选择单病种");
			return;
		} else {
			TParm parm = new TParm();// 入参
			parm.setData("DISE_CODE", diseCode);
			parm.setData("CASE_NO", caseNo);
			TParm result = new TParm();
			if (admType.equals("E")) {// 急诊
				result = CLPSingleDiseTool.getInstance().updateREGAdmSDInfo(parm);
			} else if (admType.equals("I")) {// 住院
				result = CLPSingleDiseTool.getInstance().updateADMInpSDInfo(parm);
			}
			if (result.getErrCode() < 0) {
				messageBox(result.getErrText());
				return;
			} else {
				messageBox("P0001");// 保存成功
				this.callFunction("UI|save|setEnabled", false);
				this.callFunction("UI|delete|setEnabled", true);
				this.callFunction("UI|editEMR|setEnabled", true);
				this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
				TParm patInfoParm = (TParm) callFunction("UI|PAT_TABLE|getParmValue");
				if (patInfoParm.getCount("CASE_NO") == 1) {
					patInfoParm.setData("DISE_CODE", 0, diseCode);
					this.callFunction("UI|PAT_TABLE|setParmValue", patInfoParm);
				} else {
					int rowNum = patTable.getSelectedRow();
					patInfoParm.setData("DISE_CODE", rowNum, diseCode);
					this.callFunction("UI|PAT_TABLE|setParmValue", patInfoParm);
					patTable.setSelectedRow(rowNum);
				}
			}
		}
	}

	/**
	 * 删除单病种属性
	 */
	public void onDelete() {
		// 删除后，单病种下拉框可选
		TParm parm = new TParm();// 入参
		parm.setData("CASE_NO", caseNo);
		TParm result = new TParm();
		if (admType.equals("E")) {// 急诊
			result = CLPSingleDiseTool.getInstance().deleteREGAdmSDInfo(parm);
		} else if (admType.equals("I")) {// 住院
			result = CLPSingleDiseTool.getInstance().deleteADMInpSDInfo(parm);
		}
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		} else {
			messageBox("P0003");// 删除成功
			this.callFunction("UI|save|setEnabled", true);
			this.callFunction("UI|delete|setEnabled", false);
			this.callFunction("UI|editEMR|setEnabled", false);
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", true);
			this.callFunction("UI|PAT_DISE_CODE|setValue", "");
			TParm patInfo = (TParm) callFunction("UI|PAT_TABLE|getParmValue");
			if (patInfo.getCount("CASE_NO") == 1) {
				patInfo.setData("DISE_CODE", 0, "");
				this.callFunction("UI|PAT_TABLE|setParmValue", patInfo);
			} else {
				int rowNum = patTable.getSelectedRow();
				patInfo.setData("DISE_CODE", rowNum, "");
				this.callFunction("UI|PAT_TABLE|setParmValue", patInfo);
				patTable.setSelectedRow(rowNum);
			}
		}
	}

	/**
	 * 调出EMR模板编辑器
	 */
	public void onEditEmr() {
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "SD");
		parm.setData("ADM_TYPE", admType);
		parm.setData("CASE_NO", caseNo);
		parm.setData("PAT_NAME", patParm.getValue("PAT_NAME"));
		parm.setData("MR_NO", patParm.getValue("MR_NO"));
		parm.setData("IPD_NO", patParm.getValue("IPD_NO"));
		TParm param = getSDBasicData();//传给病例模板的一些基本用户信息
		parm.setData("diseData", param);
		parm.setData("STYLETYPE","2");//用了就没有左侧的树了
		parm.setData("RULETYPE", "2");//权限控制（权限：1,只读 2,读写 3,部分读写）
		parm.setData("TYPE", "F");//不为F，则可以显示该病患每次就诊的单病种病历；为F，则只显示本次就诊时的单病种病历
	    parm.setData("EMR_DATA_LIST",new TParm());
	    parm.addListener("EMR_LISTENER",this,"emrListener");
	    parm.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
	    Object obj = this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		if (obj != null) {
			TParm acceptParm = (TParm) obj;
			for (int i = 0; i < acceptParm.getCount("FILE_PATH"); i++) {
				TParm emrData = getSDBasicData();//传给病例模板的一些基本用户信息
				if (acceptParm.getValue("FILE_NAME",i).indexOf("AMI") != -1) {
					emrData.setData("DISE_CODE", "AMI");// 病种分类
				} else if (acceptParm.getValue("FILE_NAME",i).indexOf("心力衰竭") != -1) {
					emrData.setData("DISE_CODE", "HF");// 病种分类
				} 
//				else if (acceptParm.getValue("FILE_NAME").indexOf("冠状动脉旁路移植术") != -1) {
//				    emrData.setData("DISE_CODE", "CABG");// 病种分类
//				}
				emrData.setData("ADM_TYPE", admType );//门级别
				emrData.setData("FILE_PATH", acceptParm.getValue("FILE_PATH",i));//病历文件路径
				emrData.setData("FILE_NAME", acceptParm.getValue("FILE_NAME",i));//病历文件名称
			 	TParm action = TIOM_AppServer.executeAction("action.clp.CLPSingleDiseAction", "insertSDData", emrData);

				if (action.getErrCode() < 0) {
					this.messageBox("E0001");//保存失败
					return;
				}
			}
		}
	}
	
	/**
	 * 传给病例模板的一些基本用户信息
	 * @return
	 */
	public TParm getSDBasicData() {
		TParm data = new TParm();
		data.setData("MR_NO", patParm.getValue("MR_NO"));//病案号
		data.setData("CASE_NO", patParm.getValue("CASE_NO"));//就诊号
		if (admType.equals("E")) {
			data.setData("IPD_NO", "");//住院号
		} else if (admType.equals("I")) {
			data.setData("IPD_NO", patParm.getValue("IPD_NO"));//住院号
		}
		data.setData("PAT_NAME",patParm.getValue("PAT_NAME"));//姓名
		data.setData("SEX_CODE",patParm.getValue("SEX_CODE"));//性别
		data.setData("BIRTH_DATE", patParm.getData("BIRTH_DATE"));
		data.setData("AGE", StringTool.CountAgeByTimestamp((Timestamp) patParm.getData("BIRTH_DATE"), SystemTool.getInstance().getDate())[0]);// 年龄
		if (admType.equals("E")) {
//			data.setData("IN_DATE", new TNull(Timestamp.class));
//			data.setData("OUT_DATE", new TNull(Timestamp.class));
			data.setData("STAY_DAYS", 0);
		} else if (admType.equals("I")) {
			data.setData("IN_DATE", StringTool.getTimestamp(StringTool.getString((Timestamp) patParm.getData("IN_DATE"),
							"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
			int stayDays = 0;// 住院天数
			if (patParm.getData("OUT_DATE") != null) {
				data.setData("OUT_DATE", StringTool.getTimestamp(StringTool.getString((Timestamp) patParm.getData("OUT_DATE"),
								"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
				stayDays = StringTool.getDateDiffer(data.getTimestamp("IN_DATE"), data.getTimestamp("OUT_DATE"));
			} else {
				// data.setData("OUT_DATE", new TNull(Timestamp.class));
			}
			data.setData("STAY_DAYS", stayDays);
		}
		data.setData("ICD_CODE", patParm.getValue("ICD_CODE"));
		data.setData("ICD_CHN_DESC", patParm.getValue("ICD_CHN_DESC"));
		if (admType.equals("I")) {
			TParm ASPC = CLPSingleDiseTool.getInstance().queryASandPCFromI(data);// 查询病患来源和病情状态
			if (!ASPC.getValue("ADM_SOURCE", 0).equals("")) {// 病患来源
				data.setData("ADM_SOURCE", ASPC.getValue("ADM_SOURCE", 0));
			}
			if (!ASPC.getValue("PATIENT_CONDITION", 0).equals("")) {// 病情状态
				data.setData("PATIENT_CONDITION", ASPC.getValue("PATIENT_CONDITION", 0));
			}
		}
		data.setData("TBYS", Operator.getID());// 填表医师ID
		data.setData("TBYS_CHN", Operator.getName());// 填表医师NAME
		data.setData("OPT_USER", Operator.getID());
		data.setData("OPT_DATE", SystemTool.getInstance().getDate());
		data.setData("OPT_TERM", Operator.getIP());
		return data;
	}		
	
	/**
	 * 进行单病种统计
	 */
	public void onExport() {
		this.messageBox("还未实现！");
	}

	/**
	 * 更改表格标题和内容映射
	 */
	public void changeTableTandM() {
		if (admType.equals("E")) {// 急诊
			patTable.setHeader(Header_E);// 设置表格标题行
			patTable.setParmMap(ParmMap_E);// 设置表格对应的Map
			this.callFunction("UI|PAT_DATE_LABEL|setText", "挂号时间：");
		} else if (admType.equals("I")) {// 住院
			patTable.setHeader(Header_I);
			patTable.setParmMap(ParmMap_I);
			this.callFunction("UI|PAT_DATE_LABEL|setText", "入院时间：");
		}
	}

	/**
	 * 更改表格标题和内容映射
	 */
	public void onPatChangeTableTandM() {
		admType=this.getValue("PAT_ADM_TYPE")+"";
		if (admType.equals("E")) {// 急诊
			patTable.setHeader(Header_E);// 设置表格标题行
			patTable.setParmMap(ParmMap_E);// 设置表格对应的Map
			this.callFunction("UI|PAT_DATE_LABEL|setText", "挂号时间:");
		} else if (admType.equals("I")) {// 住院
			patTable.setHeader(Header_I);
			patTable.setParmMap(ParmMap_I);
			this.callFunction("UI|PAT_DATE_LABEL|setText", "入院时间:");
		}
	}
	
	/**
	 * 更改表格标题和内容映射
	 */
	public void onSDChangeTableTandM() {
		String admType=this.getValue("SD_ADM_TYPE")+"";
		if (admType.equals("E")) {// 急诊
			this.callFunction("UI|SD_DATE_LABEL|setText", "挂号时间:");
		} else if (admType.equals("I")) {// 住院
			this.callFunction("UI|SD_DATE_LABEL|setText", "入院时间:");
		}else{
			this.callFunction("UI|SD_DATE_LABEL|setText", "起迄时间:");
		}
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		if (lastSelectedIndex <= 0) {// 查询病患页签
			clearValue("DEPT_CODE;MR_NO;PAT_DISE_CODE;PAT_COUNT");
			this.callFunction("UI|PAT_ADM_TYPE|setEnabled", true);
			this.callFunction("UI|DEPT_CODE|setEnabled", true);
			this.callFunction("UI|MR_NO|setEnabled", true);
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
			edit.setEnabled(false);
			((TTable) this.getComponent("PAT_TABLE")).setDSValue();
		} else {// 单病种页签
			clearValue("SD_ADM_TYPE;SD_DISE_CODE;SD_COUNT");
			// this.clearValue("SD_TABLE");
			export.setEnabled(false);
			this.callFunction("UI|SD_TABLE|setParmValue", new TParm());
		}
	}
	
	/**
	 * 去空
	 * 
	 * @param str  String
	 * @return String
	 */
	public String nullToString(String str) {
		return str == null?"":str;
	}

}
