package com.javahis.ui.emr;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.adm.ADMXMLTool;
import jdo.erd.ERDCISVitalSignTool;
import jdo.ope.OPEOpBookTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.IBlock;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.dongyang.util.TList;


/**
 * 交接单
 * @author yufh
 */
public class EMRTransferWordControl extends TControl implements DMessageIO {
	private String hospAreaName = "";//医院全称
	private String hospEngAreaName = "";//英文全称
	private static final String TWORD = "WORD";//WORD对象
	private String caseNo;//就诊号
	private String mrNo;//病案号	
	private String patName;//姓名
	private String onlyEditType;// 当前编辑状态
	//	private Timestamp admDate;//就诊日期
	//	private String ipdNo;//住院号
	private String deptCode;//部门
	private TParm emrChildParm = new TParm();//子面板数据 
	private String subFileName;//依赖文件名称
	private String yearStr="";//年
	private String mouthStr="";//月
	private TWord word;//WORD对象
	private String transfer_no ="";//交接单号
	private String fromUser;//交班人
	private String toUser;//接收人
	private String opBookSeq;//手术申请单号
	private String dayOpeFlg;//日间手术标记



	public String getDayOpeFlg() {
		return dayOpeFlg;
	}

	public void setDayOpeFlg(String dayOpeFlg) {
		this.dayOpeFlg = dayOpeFlg;
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
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
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
	public String getOnlyEditType() {
		return onlyEditType;
	}

	public void setOnlyEditType(String onlyEditType) {
		this.onlyEditType = onlyEditType;
	}
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}
	public String getSubFileName() {
		return subFileName;
	}

	public void setSubFileName(String subFileName) {
		this.subFileName = subFileName;
	}
	//	public String getTransferNo() {
	//		return transferNo;
	//	}
	//
	//	public void setTransferNo(String transferNo) {
	//		this.transferNo = transferNo;
	//	}
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
	}

	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
	}
	/**
	 * 得到WORD对象
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}
	/**
	 * 初始化界面
	 */
	public void initPage() {
		Object obj = this.getParameter();
		if (obj != null) {
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.setPatName(((TParm) obj).getValue("PAT_NAME"));
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			this.setDayOpeFlg(((TParm) obj).getValue("DAY_OPE_FLG"));
			opBookSeq = ((TParm) obj).getValue("OPBOOK_SEQ");

			//this.messageBox("2."+getDayOpeFlg());
			//打开病历
			openfile(obj);
		}
		yearStr = caseNo.substring(0, 2);
		mouthStr = caseNo.substring(2, 4);
	}
	/**
	 * 打开病历
	 */
	public void openfile(Object obj) {
		TParm action = (TParm) obj;
		
//		this.messageBox("===flg: "+action.getBoolean("FLG"));
		//打开交接单病历
		if (action.getBoolean("FLG")) {
			String filePath = action.getValue("TRANSFER_FILE_PATH");
			String fileName = action.getValue("TRANSFER_FILE_NAME");
			//			System.out.println("filePath====="+filePath);
			//			System.out.println("fileName====="+fileName);
			this.getWord().onOpen(filePath,fileName, 3, false);
			TParm allParm = new TParm();
			allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
			allParm.setData("FILE_TITLEENG_TEXT", "TEXT", this.hospEngAreaName);
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
			allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
			allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
			this.getWord().setWordParameter(allParm);
			// 设置编辑状态
			this.setOnlyEditType("ONLYONE");
			//抓取时间
			EComponent com = this.getWord().getPageManager().findObject(
					"交接时间", EComponent.FIXED_TYPE);
			EFixed d =(EFixed) com;
			action.setData("D",d.getText());
			//编辑
			onEdit();	      
			// 设置当前编辑数据
			this.setEmrChildParm(action);
		}else {
			// 打开交接单模版
			String templetPath = action.getValue("TEMPLET_PATH");
			String templetName = action.getValue("EMT_FILENAME");
			this.getWord().onOpen(templetPath, templetName, 2, false);
			// 设置转入科室
			word.setMicroField("转入科室", this.getDeptDesc(action
					.getValue("TO_DEPT")));
			word.setMicroField("术式", action.getValue("OPT_CHN_DESC"));
			// 抓取时间
			EComponent com = this.getWord().getPageManager().findObject("交接时间",
					EComponent.FIXED_TYPE);
			EFixed d = (EFixed) com;
			action.setData("D", d.getText());
			this.getWord().onEditWord();
			setMicroField();
			// 设置编辑状态
			this.setOnlyEditType("NEW");
			// 编辑
			onEdit();

			// 设置当前编辑数据
			this.setEmrChildParm(action);
			

			// add by wangb 2016/1/25  急诊生成的交接单标题
			if ("ET,EO,EW".contains(action.getValue("TRANSFER_CLASS"))) {
				EFixed title = (EFixed) this.getWord().getPageManager().findObject(
						"标题", EComponent.FIXED_TYPE);
				if (title != null) {
					String strTitle = "";
					if ("ET".equals(action.getValue("TRANSFER_CLASS"))) {
						strTitle = "急诊与介入交接单";
					} else if ("EO".equals(action.getValue("TRANSFER_CLASS"))) {
						strTitle = "急诊与手术室交接单";
					} else if ("EW".equals(action.getValue("TRANSFER_CLASS"))) {
						strTitle = "急诊与病区交接单";
					}

					if (StringUtils.isNotEmpty(strTitle)) {
						title.setText(strTitle);
						this.getWord().update();
					}

					// 向交接单中代入病患最新的体征监测数据信息
					this.setCISVitalsignData(action.getValue("REG_CASE_NO"));
					// 向交接单中代入病患门急诊诊断
					this.setOpdDiagData(action.getValue("REG_CASE_NO"));
				}
			}
		}
	}
	/**
	 * 是否编辑
	 */
	private void onEdit(){
		// 可编辑
		this.getWord().setCanEdit(true);
	}
	/**
	 * 设置宏
	 */
	private void setMicroField() {
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getPatName());
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		allParm.addListener("onDoubleClicked", this,"onDoubleClicked");
		this.getWord().setWordParameter(allParm);
		this.setCaptureValue("DAY_OPE_FLG", this.getDayOpeFlg());
		setMicroFieldOne(true);
	}
	private void setMicroFieldOne(boolean falg){

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
				parm.addData("科室", this.getDeptDesc(Operator.getDept()));
				parm.addData("操作者", Operator.getName());
				parm.addData("申请日期", dateStr);
				parm.addData("日期", StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyy/MM/dd"));
				parm.addData("时间", StringTool.getString(SystemTool.getInstance()
						.getDate(), "HH:mm:ss"));	
				parm.addData("病历时间", dateStr);
				parm.addData("出院时间", StringTool.getString(new java.sql.Timestamp(System
						.currentTimeMillis()), "yyyy/MM/dd"));

				String sqldept = " SELECT B.DEPT_CHN_DESC FROM ADM_INP A,SYS_DEPT B"+
						" WHERE A.MR_NO = '" + this.getMrNo()+ "'"+
						" AND A.CASE_NO = '" + this.getCaseNo()+ "'"+
						" AND A.DEPT_CODE = B.DEPT_CODE";
				TParm result = new TParm(TJDODBTool.getInstance().select(sqldept)); 		
				parm.addData("调用科室", result.getValue("DEPT_CHN_DESC",0));
				parm.addData("SYSTEM", "COLUMNS", "年龄");
				parm.addData("SYSTEM", "COLUMNS", "就诊号");
				parm.addData("SYSTEM", "COLUMNS", "病案号");
				parm.addData("SYSTEM", "COLUMNS", "住院号");
				parm.addData("SYSTEM", "COLUMNS", "科室");
				parm.addData("SYSTEM", "COLUMNS", "操作者");
				parm.addData("SYSTEM", "COLUMNS", "申请日期");
				parm.addData("SYSTEM", "COLUMNS", "日期");
				parm.addData("SYSTEM", "COLUMNS", "时间");
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
				// 过敏史(MR_NO);
				StringBuffer drugStr = new StringBuffer();
				TParm drugParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT A.CASE_NO,A.MR_NO,CASE A.DRUG_TYPE "
								// MODIFIED BY WANGQING 20170411
										+ " WHEN 'A' THEN TO_CHAR((SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='PHA_INGREDIENT' AND B.ID=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'B' THEN TO_CHAR((SELECT B.ORDER_DESC FROM SYS_FEE B WHERE B.ORDER_CODE=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'C' THEN TO_CHAR((SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID='SYS_ALLERGYTYPE' AND B.ID=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'D' THEN TO_CHAR((SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'E' THEN TO_CHAR((SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE='PHA_RULE' AND B.CATEGORY_CODE=A.DRUGORINGRD_CODE)) "
										+ " WHEN 'N' THEN TO_CHAR('无') "
										+ " ELSE TO_CHAR('') END AS ALLERGY_NAME,OPT_DATE  "
										+ " FROM OPD_DRUGALLERGY A "
										+ " WHERE A.MR_NO='" + this.getMrNo()
										+ "'" + " ORDER BY A.ADM_DATE,A.OPT_DATE "));
				if (drugParm.getCount() > 0) {
					drugStr.append("过敏物质:");
					int rowCount = drugParm.getCount();
					for (int i = 0; i < rowCount; i++) {
						TParm temp = drugParm.getRow(i);
						drugStr.append(temp.getValue("ALLERGY_NAME") + ",");
					}
					String allergy = drugStr.toString();
					allergy = allergy.substring(0, allergy.length() - 1);
					parm.addData("过敏史", allergy);
				} else {
					parm.addData("过敏史", "-");
				}
				parm.addData("SYSTEM", "COLUMNS", "过敏史");
				// 查询新增宏列表视图		
				List<String> macroNameList = new ArrayList<String>();
				String sql = "SELECT MACRO_NAME,MACRO_VALUE,INFECT_FLG FROM MACRO_PHYSIDX_VIEW A, EMR_MICRO_CONVERT B WHERE CASE_NO = '"
						+ this.getCaseNo()
						+ "' AND A.MACRO_NAME = B.MICRO_NAME AND A.MACRO_CODE = B.MACRO_CODE ORDER BY EPISODE_DATE DESC ";
				TParm macroViewParm = new TParm(this.getDBTool().select(sql));
				for (int i = 0; i < macroViewParm.getCount(); i++) {
					if (!macroNameList
							.contains(macroViewParm.getValue("MACRO_NAME", i))) {
						macroNameList.add(macroViewParm.getValue("MACRO_NAME", i));
						parm.addData(macroViewParm.getValue("MACRO_NAME", i),
								macroViewParm.getValue("MACRO_VALUE", i));
						parm.addData("SYSTEM", "COLUMNS", macroViewParm.getValue(
								"MACRO_NAME", i));
					}
				}
				//筛查结果
				this.setInfectResult(parm, macroViewParm);
				String names[] = parm.getNames();
				TParm obj = (TParm) this.getWord().getFileManager().getParameter();
				TParm macroCodeParm = new TParm(
						this
						.getDBTool()
						.select(
								"SELECT MICRO_NAME,HIS_ATTR,HIS_TABLE_NAME FROM EMR_MICRO_CONVERT WHERE CODE_FLG='Y'"));
				for (String temp : names) {
					// 赋值标志;
					boolean flag = false;
					// ?泛晟柚?对存编号的处理)
					for (int j = 0; j < macroCodeParm.getCount(); j++) {
						// 字典类型 P 公用的,D自定义的 字典;
						String dictionaryType = macroCodeParm.getValue("HIS_ATTR", j);
						// 对应的表名;
						String tableName = macroCodeParm.getValue("HIS_TABLE_NAME", j);
						if (macroCodeParm.getValue("MICRO_NAME", j).equals(temp)) {
							if ("性别".equals(temp)) {
								if (parm.getInt(temp, 0) == 9) {
									this.getWord().setSexControl(0);
								} else {
									// 1.男 2.女
									this.getWord().setSexControl(parm.getInt(temp, 0));
								}
							}
							if (falg) {
								// 设置宏的中文显示名
								this.getWord()
								.setMicroFieldCode(
										temp,
										getDictionary(tableName, parm.getValue(
												temp, 0)),
										this.getEMRCode(dictionaryType,
												tableName, parm.getValue(temp,
														0)));
								// 设置抓取框值;
								this.setCaptureValueArray(temp, getDictionary(
										tableName, parm.getValue(temp, 0)));

								obj.setData(temp, "TEXT", getDictionary(tableName, parm
										.getValue(temp, 0)));

							} else {
								obj.setData(temp, "TEXT", getDictionary(tableName, parm
										.getValue(temp, 0)));
							}
							// 已赋值;
							flag = true;
							break;

						}
					}
					// 已经赋值,继续循环下一个宏
					if (flag) {
						continue;
					}

					String tempValue = parm.getValue(temp, 0);
					if (tempValue == null) {
						continue;
					}
					if (falg) {
						this.getWord().setMicroField(temp, tempValue);
						this.setCaptureValueArray(temp, tempValue);
						obj.setData(temp, "TEXT", tempValue);
					} else {
						obj.setData(temp, "TEXT", tempValue);
					}
				}

				this.getWord().setWordParameter(obj);		
	}

	/**
	 * 拿到字典信息
	 *
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getDictionary(String groupId, String id) {
		String result = "";
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "' AND ID='" + id + "'"));
		result = parm.getValue("CHN_DESC", 0);
		return result;
	}
	/**
	 * 获取国家标准的code，如不存在返回his系统code;
	 *
	 * @param HisAttr
	 *            String P:pattern公用字典|D：dictionary单独字典
	 * @param hisTableName
	 *            String his系统表名
	 * @param hisCode
	 *            String his系统编码
	 * @return String 对应的国家标准的code
	 */
	private String getEMRCode(String HisAttr, String hisTableName,
			String hisCode) {

		String sql = "SELECT EMR_CODE FROM EMR_CODESYSTEM_D";
		sql += " WHERE HIS_ATTR='" + HisAttr + "'";
		sql += " AND HIS_TABLE_NAME='" + hisTableName + "'";
		sql += " AND HIS_CODE='" + hisCode + "'";
		TParm emrCodeParm = new TParm(getDBTool().select(sql));
		int count = emrCodeParm.getCount();
		// 有对应
		if (count > 0) {
			return emrCodeParm.getValue("EMR_CODE", 0);
		}

		return hisCode;
	}
	/**
	 * 设定传筛结果宏
	 * 
	 * @param parm
	 * @param macroViewParm
	 * @author wangb
	 */
	private void setInfectResult(TParm parm, TParm macroViewParm) {
		// 传筛结果
		String infectResult = "有;";
		int infectCount = 0;
		int count = 0;
		TParm result = new TParm();

		for (int m = 0; m < macroViewParm.getCount(); m++) {
			if ("Y".equals(macroViewParm.getValue("INFECT_FLG", m))) {
				result.addData("MACRO_NAME", macroViewParm.getValue("MACRO_NAME", m));
				result.addData("MACRO_VALUE", macroViewParm.getValue("MACRO_VALUE", m));
				infectCount++;
			}
		}

		result.setCount(infectCount);

		// 未查到传筛数据说明尚未回传
		if (result.getCount() == 0) {
			parm.addData("传筛结果", "无");
			return;
		}

		for (int i = 0; i < result.getCount(); i++) {
			// 由于传筛回传结果为文本，暂且只能根据是否以阳性的阳字开头来判断
			if (result.getValue("MACRO_VALUE", i).startsWith("阳")) {
				infectResult = infectResult
						+ result.getValue("MACRO_NAME", i) + ";";
			} else if (result.getValue("MACRO_VALUE", i).startsWith("阴")) {
				count++;
			}
		}

		// 回传数据全为阴性
		if (count == result.getCount()) {
			parm.addData("传筛结果", "正常");
		} else {
			parm.addData("传筛结果", infectResult.substring(0, infectResult
					.length() - 1));
		}

		parm.addData("SYSTEM", "COLUMNS", "传筛结果");
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
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}
	/**
	 * 拿到交班人和接班人姓名
	 */
	public String getUserName(String userId) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID ='"
						+ userId + "'"));
		return parm.getValue("USER_NAME", 0);
	}
	/**
	 * 拿到术式名称
	 */
	public String getOPDesc(String opCode) {
		TParm parm = new TParm(this.getDBTool().select(
				" SELECT OPT_CHN_DESC FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"
						+ opCode + "'"));
		return parm.getValue("OPT_CHN_DESC", 0);
	}
	/**
	 * 点击右键事件
	 */
	public void onMouseRightPressed() {
		EComponent e = this.getWord().getFocusManager().getFocus();
		if (e == null) {
			return;
		}
		if (!this.getWord().canEdit()) {
			return;
		}
		// 抓取框
		if (e instanceof ECapture) {
			return;
		}
	}

	/**
	 * 保存
	 */
	public boolean onSave() {
		this.getWord().setMessageBoxSwitch(false);		
		this.getWord().setFileAuthor(Operator.getID());
		String dateStr = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		// 创建时间
		this.getWord().setFileCreateDate(dateStr);
		// 最后修改人
		this.getWord().setFileLastEditUser(Operator.getID());
		// 最后修改日期
		this.getWord().setFileLastEditDate(dateStr);
		// 最后修改IP
		this.getWord().setFileLastEditIP(Operator.getIP());
		String fileName ="";//文件名称
		String filePath ="";//文件路径
		String transferNo="";//交接单号
		TParm asSaveParm=new TParm();
		//生成交接单
		if (this.getOnlyEditType().equals("NEW")) {
			if(getEmrChildParm().getValue("TRANSFER_CLASS").equals("WT")){
				String Dept = getEmrChildParm().getValue("TO_DEPT");//转入科室
				//非小儿外科
				if(!Dept.equals("030202")){
					ESingleChoose com1 = (ESingleChoose)word.findObject(
							"皮肤准备", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com2 = (ESingleChoose)word.findObject(
							"留置针", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com3 = (ESingleChoose)word.findObject(
							"活动牙齿", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com4 = (ESingleChoose)word.findObject(
							"义齿", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com5 = (ESingleChoose)word.findObject(
							"皮试", EComponent.SINGLE_CHOOSE_TYPE);
					//			 System.out.println("com1====="+com1.getText());
					//			 System.out.println("com2====="+com2.getText());
					//			 System.out.println("com3====="+com3.getText());
					//			 System.out.println("com4====="+com4.getText());
					//			 System.out.println("com5====="+com5.getText());
					if(com1.getText().equals("单选")||
							com2.getText().equals("单选")||
							com3.getText().equals("单选")||
							com4.getText().equals("单选")||
							com5.getText().equals("单选")){
						this.messageBox("请核实查检内容,有未选择的项目");
						return false;	 
					}
				}
				else{//小儿外科
					ESingleChoose com6 = (ESingleChoose)word.findObject(
							"禁食水", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com7 = (ESingleChoose)word.findObject(
							"足背动脉标记", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com8 = (ESingleChoose)word.findObject(
							"氧气袋", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com9 = (ESingleChoose)word.findObject(
							"儿科活动牙齿", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com10 = (ESingleChoose)word.findObject(
							"儿科义齿", EComponent.SINGLE_CHOOSE_TYPE);
					ESingleChoose com11 = (ESingleChoose)word.findObject(
							"儿科皮试", EComponent.SINGLE_CHOOSE_TYPE);
					//			  System.out.println("com6====="+com6.getText());
					//			  System.out.println("com7====="+com7.getText());
					//			  System.out.println("com8====="+com8.getText());
					//			  System.out.println("com9====="+com9.getText());
					//			  System.out.println("com10====="+com10.getText());
					//			  System.out.println("com11====="+com11.getText());
					if(com6.getText().equals("单选")||
							com7.getText().equals("单选")||
							com8.getText().equals("单选")||
							com9.getText().equals("单选")||
							com10.getText().equals("单选")||
							com11.getText().equals("单选")){
						this.messageBox("请核实查检内容,有未选择的项目");
						return false;	 
					}
				}
			}
			//取号原则得到交接单号
			transferNo = SystemTool.getInstance().getNo("ALL", "MRO",
					"TRANSFER_NO","TRANSFER_NO");
			String name = getEmrChildParm().getValue("EMT_FILENAME");
			if(transfer_no.equals(""))
				fileName = caseNo + "_" + name + "_" + transferNo;
			else
				fileName = caseNo + "_" + name + "_" + transfer_no;	
			filePath = "JHW" + "\\" + yearStr + "\\" + mouthStr + "\\"
					+ this.getMrNo();
			//			System.out.println("fileName======"+fileName);
			//			System.out.println("filePath======"+filePath);
			asSaveParm.setData("STATUS_FLG","4");//待接收
			asSaveParm.setData("FROM_DEPT",getEmrChildParm().getValue("FROM_DEPT"));//转出科室
			asSaveParm.setData("TO_DEPT",getEmrChildParm().getValue("TO_DEPT"));//转入科室
		}
		//接收交接单(保存修改数据)
		else{
			//			System.out.println("D======"+getEmrChildParm().getValue("D"));			
			//			System.out.println("fromUser======"+fromUser);
			//			System.out.println("toUser===="+toUser);
			if(getEmrChildParm().getValue("D").equals("交接时间")){
				if(fromUser==null){
					this.messageBox("交班人未录入,不能保存");
					return false;
				}
				if(toUser==null){
					this.messageBox("接班人未录入,不能保存");
					return false;
				}
				if(fromUser.equals(toUser)){
					this.messageBox("交班人和接班人是同一人,不能保存");
					return false;
				} 
				if (this.messageBox("询问", "保存后的交接单将不能修改,是否保存", 2) != 0)
					return false;	
			}else{
				this.messageBox("交接单已生成,不可修改");
				return false;
			}
			transferNo = getEmrChildParm().getValue("TRANSFER_CODE");
			String name = getEmrChildParm().getValue("TRANSFER_FILE_NAME");
			fileName = name;
			filePath = "JHW" + "\\" + yearStr + "\\" + mouthStr + "\\"
					+ this.getMrNo();
			asSaveParm.setData("STATUS_FLG","5");//已接收		
		}
		boolean success = this.getWord().onSaveAs(filePath, fileName, 3);
		if(success){
			asSaveParm.setData("TRANSFER_CODE",transferNo);
			asSaveParm.setData("TRANSFER_FILE_PATH",filePath);
			asSaveParm.setData("TRANSFER_FILE_NAME",fileName);
			asSaveParm.setData("CASE_NO",this.getCaseNo());
			asSaveParm.setData("MR_NO",this.getMrNo());
			asSaveParm.setData("PAT_NAME",this.getPatName());
			asSaveParm.setData("OPBOOK_SEQ",this.opBookSeq);
			asSaveParm.setData("TRANSFER_CLASS",
					getEmrChildParm().getValue("TRANSFER_CLASS"));//交接单类型
			asSaveParm.setData("ONLY_EDIT_TYPE", this.getOnlyEditType());
			asSaveParm.setData("TRANSFER_NO", this.transfer_no);
			asSaveParm.setData("OPT_USER", Operator.getID());//操作人员
			asSaveParm.setData("OPT_TERM", Operator.getIP());//操作终端
			asSaveParm.setData("FROM_USER", this.fromUser);
			asSaveParm.setData("TO_USER", this.toUser);
			asSaveParm.setData("OP_DESC", word.getCaptureValue("术式"));
			TParm resultSave = TIOM_AppServer.executeAction("action.emr.EMRTransferWordAction", "saveTransferFile",asSaveParm);
			if(resultSave.getErrCode() < 0){
				this.messageBox("保存失败");
				return false;
			}
			this.messageBox("保存成功");
			// add by wangb 2015/12/18 介入手术交接单交接时更新手术状态并发送大屏消息 START
			// 查询当前交接信息
			TParm inwTransInfo = this.selectInwTransInfo(transferNo);
			if (inwTransInfo.getErrCode() < 0) {
				return true;
			} else if (inwTransInfo.getCount() > 0) {
				String opeStatus = "";
				// 如果手术消息未发送过
				if (StringUtils.equals("N", inwTransInfo.getValue(
						"OPE_MSG_SEND_FLG", 0))
						&& StringUtils.isNotEmpty(opBookSeq)) {
					// 如果交接单为病区_介入且接班人非空，则更新手术状态并发送大屏消息
					if (StringUtils.equals("WT", inwTransInfo.getValue(
							"TRANSFER_CLASS", 0))
							&& StringUtils.isNotEmpty(inwTransInfo.getValue(
									"TO_USER", 0))) {
						// 4_介入手术等待
						opeStatus = "4";

						// 如果交接单为介入_CCU/病区且交接科室非空，则更新手术状态并发送大屏消息
					} else if (StringUtils.equals("TC/TW", inwTransInfo.getValue(
							"TRANSFER_CLASS", 0))
							&& StringUtils.isNotEmpty(inwTransInfo.getValue(
									"FROM_DEPT", 0))) {
						// 4_介入手术结束
						opeStatus = "7";
					}

					if (StringUtils.isNotEmpty(opeStatus)) {
						// 更新手术状态
						OPEOpBookTool.getInstance().updateOpeStatus(opBookSeq,
								opeStatus, null);

						// 向大屏接口发送消息
						TParm xmlParm = ADMXMLTool.getInstance()
								.creatOPEStateXMLFile(
										inwTransInfo.getValue("CASE_NO", 0),
										opBookSeq);
						if (xmlParm.getErrCode() < 0) {
							this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
						} else {
							// 更新交接单消息发送注记
							this.updateInwTransMsgSendFlg(transferNo);
						}
					}
				}
				
				//add by huangtt 20170503  start 手术室-ICU交接单接班人签字保存时，发送病患消息文件，以交接时间作为入ICU时间
				EComponent com1 = this.getWord().getPageManager().findObject(
						"交接时间", EComponent.FIXED_TYPE);
				EFixed d1 =(EFixed) com1;
				if (StringUtils.equals("OI", inwTransInfo.getValue(
						"TRANSFER_CLASS", 0)) && toUser != null){
					String TransferToICUTime = "";
					if(d1 != null){
//						System.out.println("1-----"+d1.getText());
						
						TransferToICUTime =d1.getText().replaceAll("/", "-");

//						System.out.println("TransferToICUTime--"+TransferToICUTime);
						if(TransferToICUTime.length() > 0){
							TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFileTime(caseNo,TransferToICUTime);
					        if (xmlParm.getErrCode() < 0) {
					            this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
					        }
							
						}
						
					}
					
					//传送手术信息
					
//					System.out.println("术式--------"+word.getCaptureValue("术式"));
					// add by wangb 2017/09/04 若交接单中术式未填则不发送手术消息，避免使用空的术式内容覆盖大屏原有信息
					if (StringUtils.isNotEmpty(word.getCaptureValue("术式"))) {
						TParm xmlParm = ADMXMLTool.getInstance()
								.creatOPEInfoXMLFile(this.getCaseNo(),
										this.opBookSeq,
										word.getCaptureValue("术式"));
						if (xmlParm.getErrCode() < 0) {
							this
									.messageBox("电视屏接口发送失败 "
											+ xmlParm.getErrText());
						}
					}
				}
				
				//add by huangtt 20170503  end 手术室-ICU交接单接班人签字保存时，发送病患消息文件，以交接时间作为入ICU时间
				
				// modify by wangb 2017/09/04 只要有手术单号即存入交接单表中，避免用户在创建时不填写手术导致与手术单号关联不上的问题
				// add by wangb 2017/05/31 与手术相关的交接单关联手术单号和术式，存入数据库中(交接单中的术式相对最准确) START
				//modify by wangjc 20171207 归入一个事务，写到action中
//				if (StringUtils.isNotEmpty(opBookSeq)) {
//					String sql = "UPDATE INW_TRANSFERSHEET SET OPBOOK_SEQ = '"
//							+ opBookSeq + "',OP_DESC = '"
//							+ word.getCaptureValue("术式")
//							+ "' WHERE TRANSFER_CODE = '" + transferNo + "'";
//					TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//				}
				// add by wangb 2017/05/31 与手术相关的交接单关联手术单号和术式，存入数据库中(交接单中的术式相对最准确) END
			}
			// add by wangb 2015/12/18 病区-介入交接单交接时更新手术状态并发送大屏消息 END
		
		}
		return true;
	}
	/**
	 * 交班人签字
	 */
	public void onFromuser() {
		String type = "transfer";	
		TParm Parm  = new TParm();
		Parm.setData("TYPE",type);
		Parm.addListener("onReturnfromuser", this,
				"onReturnfromuser");		
		TFrame frame = new TFrame();
		frame.init(getConfigParm().newConfig(
				"%ROOT%\\config\\inw\\passWordCheck.x"));		
		frame.setParameter(Parm);
		frame.onInit();
		frame.setLocation(500, 300);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);			
	} 
	/**
	 * 接班人签字
	 */
	public void onTouser() {
		String type = "transfer";
		TParm Parm  = new TParm();
		Parm.setData("TYPE",type);
		Parm.addListener("onReturntouser", this,
				"onReturntouser");
		TFrame frame = new TFrame();
		frame.init(getConfigParm().newConfig(
				"%ROOT%\\config\\inw\\passWordCheck.x"));		
		frame.setParameter(Parm);
		frame.onInit();
		frame.setLocation(500, 300);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);	
	}
	public void onReturnfromuser(TParm inParm) {
		//		System.out.println("onReturnfromuser====="+inParm);
		String OK = inParm.getValue("RESULT");		
		if (!OK.equals("OK")) {
			return;
		}				
		fromUser =inParm.getValue("USER_ID");
		//	      System.out.println("fromUser====="+fromUser);
		EComponent com = this.getWord().getPageManager().findObject(
				"交班人", EComponent.FIXED_TYPE);
		EFixed d =(EFixed) com;
		//	     System.out.println("EFixed交班人====="+d);
		if (d != null) {
			d.setText(this.getUserName(fromUser));
			this.getWord().update();	
		}   
	}
	public void onReturntouser(TParm inParm) {
		//		System.out.println("onReturntouser====="+inParm);
		String OK = inParm.getValue("RESULT");		
		if (!OK.equals("OK")) {
			return;
		}				
		toUser =inParm.getValue("USER_ID");
		//	      System.out.println("toUser====="+toUser);
		EComponent com = this.getWord().getPageManager().findObject(
				"接班人", EComponent.FIXED_TYPE);
		EFixed d =(EFixed) com;
		//			System.out.println("EFixed接班人====="+d);
		if (d != null) {
			d.setText(this.getUserName(toUser));
			this.getWord().update();
		}
		EComponent com1 = this.getWord().getPageManager().findObject(
				"交接时间", EComponent.FIXED_TYPE);
		EFixed d1 =(EFixed) com1;		
		if (d1 != null) {
			String dateStr = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyy/MM/dd HH:mm:ss");
			d1.setText(dateStr);
			this.getWord().update();
		}
	}
	/**
	 * 保存交接单到数据库
	 */
	public boolean saveTransferFile(TParm parm) {
		//		System.out.println("saveTransferFile======"+parm);
		TParm result = new TParm();
		String optUser = Operator.getID();//操作人员
		String optTerm = Operator.getIP();//操作终端
		String transferCode = parm.getValue("TRANSFER_CODE");//交接单号
		String transferFilePath = parm.getValue("TRANSFER_FILE_PATH");//文件路径
		String transferFileName = parm.getValue("TRANSFER_FILE_NAME");//文件名称
		String mrNo =  parm.getValue("MR_NO");//病案号
		String caseNo =  parm.getValue("CASE_NO");//就诊号
		String patName =  parm.getValue("PAT_NAME");//姓名
		String fromDept =  parm.getValue("FROM_DEPT");//转出科室
		String toDept =  parm.getValue("TO_DEPT");//转入科室
		String statusFlg = parm.getValue("STATUS_FLG");//接收状态
		String transferClass = parm.getValue("TRANSFER_CLASS");//交接单类型
		TConnection conn= TDBPoolManager.getInstance().getConnection();
		if (this.getOnlyEditType().equals("NEW")) {
			if(transfer_no.equals("")){
				String sql= " INSERT INTO INW_TRANSFERSHEET(TRANSFER_CODE,TRANSFER_FILE_PATH," +
						" TRANSFER_FILE_NAME,MR_NO,CASE_NO,PAT_NAME,FROM_DEPT,TO_DEPT," +
						" STATUS_FLG,TRANSFER_CLASS,CRE_USER,CRE_DATE," +
						" OPT_USER,OPT_DATE,OPT_TERM,OPBOOK_SEQ)"+ 
						" VALUES ('"+ transferCode+ "','"+ transferFilePath+"','"+transferFileName+ "'," +
						" '"+ mrNo+ "','"+ caseNo+ "','"+ patName+ "','"+ fromDept+ "','"+ toDept+ "'," +
						" '"+ statusFlg+ "','"+ transferClass+ "'," +
						" '"+optUser+ "',SYSDATE,'"+optUser+ "',SYSDATE,'"+optTerm+ "','"+parm.getValue("OPBOOK_SEQ")+"')";
				//            System.out.println("sql=========="+sql); 	
				result = new TParm(TJDODBTool.getInstance().update(sql,conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return false;
				}
				String unlockRoomSql = "UPDATE OPE_IPROOM SET OPBOOK_SEQ= NULL WHERE OPBOOK_SEQ='"+parm.getValue("OPBOOK_SEQ")+"'";
				result = new TParm(TJDODBTool.getInstance().update(unlockRoomSql,conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return false;
				}
				transfer_no = transferCode;
			}
		}else if (this.getOnlyEditType().equals("ONLYONE")) {
			String sql= " UPDATE INW_TRANSFERSHEET SET STATUS_FLG ='"+ statusFlg + "'," +
					" FROM_USER = '"+fromUser+ "'," +
					" TO_USER = '"+toUser+ "'," +
					" TRANSFER_DATE =SYSDATE" +
					" WHERE TRANSFER_CODE = '"+transferCode+ "'";
			result = new TParm(TJDODBTool.getInstance().update(sql,conn));
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return false;
			}
		}
		conn.commit();
		conn.close();
		return true;
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
			for (int j = 0; j < ePage.getComponentList().size(); j++) {
				EPanel ePanel = (EPanel) ePage.getComponentList().get(j);
				if (ePanel != null) {
					for (int z = 0; z < ePanel.getBlockSize(); z++) {
						IBlock block = (IBlock) ePanel.get(z);
						// 9为抓取框;
						if (block != null) {
							if (block.getObjectType() == EComponent.CAPTURE_TYPE) {
								EComponent com = block;
								ECapture capture = (ECapture) com;

								if (capture.getMicroName().equals(macroName)) {
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
							//固定文本赋值
							if(block.getObjectType() == EComponent.FIXED_TYPE){
								EComponent com = block;
								EFixed efix = (EFixed) com;
								if("DAY_OPE_FLG".equals(efix.getName()) && efix.getName().equals(macroName)){
									efix.setText(value);
									isSetValue = true;
									break;
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
	 * 查询当前交接信息
	 * 
	 * @param transferCode 交接单号
	 * @return TParm
	 * @author wangb 2015/12/18
	 */
	private TParm selectInwTransInfo(String transferCode) {
		String sql = "SELECT * FROM INW_TRANSFERSHEET WHERE TRANSFER_CODE = '"
				+ transferCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新交接单消息发送注记
	 * 
	 * @param transferCode 交接单号
	 * @return TParm
	 * @author wangb 2015/12/18
	 */
	private TParm updateInwTransMsgSendFlg(String transferCode) {
		String sql = "UPDATE INW_TRANSFERSHEET SET OPE_MSG_SEND_FLG = 'Y' WHERE TRANSFER_CODE = '"
				+ transferCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 向交接单中代入病患最新的体征监测数据信息
	 * 
	 * @param erdCaseNo 就诊号
	 */
	private void setCISVitalsignData(String erdCaseNo) {
		TParm result = ERDCISVitalSignTool.getInstance().queryERDCISVitalSign(erdCaseNo);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + ":" + result.getErrText());
			return;
		}

		int count = result.getCount();
		// modify by wangb 急诊到病区/CCU交接单增加血氧饱和度
		String[] itemArrays = {"BT2","HR","NBPD","NBPM","NBPS","PULSE","RR","SPO2"};
		int itemArrayLength = itemArrays.length;
		Map<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < itemArrayLength; i++) {
			for (int j = 0; j < count; j++) {
				if (itemArrays[i].equals(result.getValue("MONITOR_ITEM_EN", j))) {
					map.put(itemArrays[i], result.getValue("MONITOR_VALUE", j));
					break;
				}
			}
		}

		// modify by wangb 急诊体征监测取不到数据的情况下交接单数据置空，避免取住院体温单数据作为交接数据
		if (StringUtils.isNotEmpty(map.get("BT2"))) {
			this.getWord().setMicroField("体温", map.get("BT2") + " ℃");
		} else {
			this.getWord().setMicroField("体温", "");
		}
		if (StringUtils.isNotEmpty(map.get("HR"))) {
			this.getWord().setMicroField("心率", map.get("HR") + " 次/分");
		} else {
			this.getWord().setMicroField("心率", "");
		}
		if (StringUtils.isNotEmpty(map.get("RR"))) {
			this.getWord().setMicroField("呼吸", map.get("RR") + " 次/分");
		} else {
			this.getWord().setMicroField("呼吸", "");
		}
		if (StringUtils.isNotEmpty(map.get("NBPS")) && StringUtils.isNotEmpty(map.get("NBPD"))) {
			this.getWord().setMicroField("血压", map.get("NBPS") + "/" + map.get("NBPD") + " mmHg");
		} else {
			this.getWord().setMicroField("血压", "");
		}
		if (StringUtils.isNotEmpty(map.get("SPO2"))) {
			this.getWord().setMicroField("血氧饱和度", map.get("SPO2") + " %");
		} else {
			this.getWord().setMicroField("血氧饱和度", "");
		}
	}

	/**
	 * 向交接单中代入病患门急诊诊断
	 * 
	 * @param erdCaseNo 就诊号
	 */
	private void setOpdDiagData(String erdCaseNo) {
		String sql = "SELECT CASE_NO,A.ICD_TYPE,A.ICD_CODE,B.ICD_CHN_DESC FROM OPD_DIAGREC A, SYS_DIAGNOSIS B "
				+ " WHERE A.CASE_NO = '"
				+ erdCaseNo
				+ "' AND A.ICD_CODE = B.ICD_CODE AND A.MAIN_DIAG_FLG = 'Y' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + " " + result.getErrText());
			return;
		} else {
			this.getWord().setMicroField("门急诊诊断",
					result.getValue("ICD_CHN_DESC", 0));
		}
	}
}
