package com.javahis.ui.reg;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

import jdo.emr.EMRPublicTool;
import jdo.erd.ERDLevelTool;
import jdo.hl7.Hl7Communications;
import jdo.opd.OrderTool;
import jdo.reg.REGTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import org.apache.commons.lang.StringUtils;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.SelectResult;

/**
 * <p>急诊检伤评估</p>
 * 
 * @version 1.0 htt
 * 
 * @version 2.0 wangqing
 * 
 */

//20170821
//1、院内首次心电时间问题修改sql

public class REGPreviewingTriageControl extends TControl{
	TTable table;
	private TWord word;
	/**
	 *  病历存储信息
	 */
	private String[] saveFiles;

	private String caseNo = "";
	private String mrNo = "";
	private String patName = "";
	private String idNo = "";
	private String sexCode = "";
	private String triageNo = "";
	private String erdLevel = "";
	/**
	 * 首诊心电PDF存放路径
	 */
	private String firstEcgPdfPath;

	/**
	 * pdf本地缓存路径
	 */
	private String tempPath = "C:\\JavaHisFile\\temp\\pdf";

	/**
	 * Map：<检伤等级，检伤等级描述>
	 */
	Map<String, String> levelCode =new HashMap<String, String>();

	/**
	 * <p>insert or update</p>
	 * <p>0 : insert; 1 : update</p>
	 */
	private int myFlg = 0;

	/**
	 * 系统传入参数
	 */
	public TParm sysParm;

	private final String classCodeConfig = "ERDLevelCLASSCODE";
	private final String subClassCodeConfig = "ERDLevelSUBCLASSCODE";


	// add by wangqing 20180119 rebuild emergency triage emr
	// the start
	/**
	 * the id of word
	 */
	private final String WORD_TAG = "TWORD";

	/**
	 * the count of one level selected
	 */
	private int LEVEL_ONE_COUNT = 0;
	/**
	 * the count of two level selected
	 */
	private int LEVEL_TWO_COUNT = 0;
	/**
	 * the count of three level selected
	 */
	private int LEVEL_THREE_COUNT = 0;
	/**
	 * the count of four level selected
	 */
	private int LEVEL_FOUR_COUNT = 0;
	
	private static int LEVEL_ONE_ITEM_COUNT = 16;
	
	private static int LEVEL_TWO_ITEM_COUNT = 10;
	
	private static int LEVEL_THREE_ITEM_COUNT = 4;
	
	private static int LEVEL_FOUR_ITEM_COUNT = 7;
	
	/**
	 * 是否在关闭时自动保存，应用于病案号补录true，自动保存；false，不自动保存
	 */
	private boolean isAutoSave = false;

	/**
	 * 检伤分区
	 */
	private static Map<String, String> TRIAGE_ZONE=new HashMap<String, String>();
	// the end
	


	/**
	 * 初始化
	 * @author Eric
	 */
	public void onInit(){
		super.onInit();
		// 自定义初始化操作
		this.callFunction("UI|SAVE|setEnabled", false);
		this.callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");
		table = (TTable) this.getComponent("TABLE");
		word = (TWord) this.getComponent("TWORD");
		getLevelCode();
		firstEcgPdfPath = TConfig.getSystemValue("FIRST_ECG_PDF_PATH");
		if (StringUtils.isEmpty(firstEcgPdfPath)) {
			this.messageBox("配置文件中未找到首诊心电PDF报告存放路径");
		}
		// 确保本地存在pdf缓存路径
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		this.setValue("ADM_TYPE", "E");
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("CLINICAREA_CODE", Operator.getStation());
		//		this.setValue("TRIAGE_USER", Operator.getID());

		// 初始化到大门时间（当前时间-1min）
		this.setValue("ADM_DATE", getNowTimeStamp());
		this.setValue("COME_TIME", getNowTimestampShift(-1*60*1000));
		
		TRIAGE_ZONE.put("", ""); 
		TRIAGE_ZONE.put("A区", "1");  
		TRIAGE_ZONE.put("C区", "2");
		
		Object obj = this.getParameter();
		if(obj != null && obj instanceof TParm){
			sysParm = (TParm) obj;
			if(sysParm.getValue("FLG")==null ||sysParm.getValue("FLG").trim().equals("")){
				this.messageBox("FLG不能为空！！！");
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						closeWindow();
					}
				});
				return;
			}
			if(sysParm.getValue("FLG").equals("NEW_ONW")){// 急诊护士站->检伤补录
				mrNo = sysParm.getValue("MR_NO");
				caseNo = sysParm.getValue("CASE_NO");
				patName = sysParm.getValue("PAT_NAME");
				idNo = sysParm.getValue("IDNO");
				sexCode = sysParm.getValue("SEX_CODE");
				this.setValue("PAT_NAME", patName);
				this.setValue("IDNO", idNo);
				this.setValue("SEX_CODE", sexCode);
				if(sysParm.getValue("BIRTH_DATE").length()>0){
					this.setValue("BIRTH_DATE", sysParm.getValue("BIRTH_DATE").replace("-", "/").subSequence(0, 10));
				}
				if(sysParm.getData("IDNO").toString().length() > 0 || sysParm.getValue("BIRTH_DATE").length()>0){
					Timestamp birthDate = TypeTool.getTimestamp(getValue("BIRTH_DATE"));
					String age = OdoUtil.showAge( birthDate,SystemTool.getInstance().getDate());
					this.setValue("AGE", age);
				}
				onNew();	
				word.update();	
			}else if(sysParm.getValue("FLG").equals("QUERY")){// 检伤一览
				if(sysParm.getValue("TRIAGE_NO")==null || sysParm.getValue("TRIAGE_NO").trim().equals("")){
					this.messageBox("检伤号不能为空！！！");
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							closeWindow();
						}
					});
					return;
				}
				this.setValue("TRIAGE_NO", sysParm.getValue("TRIAGE_NO"));
				onTriageNo();
			}
		}else{			
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					onQuery();
				}
			});
		}
		// add by wangqing 20170625 start
		// 检伤号控件获取焦点
		final TTextField tt = (TTextField)this.getComponent("TRIAGE_NO");
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				tt.requestFocusInWindow();
			}
		});
		// add by wangqing 20170626 end
	}

	/**
	 * 新增
	 * @author Eric
	 */
	public void onNew(){	
		this.onClear();// 先清空数据
		
		myFlg = 0;
		callFunction("UI|SAVE|setEnabled", true);

		this.setValue("ADM_DATE", this.getNowTimeStamp());
		this.setValue("COME_TIME", this.getNowTimestampShift(-1*60*1000));
		this.setValue("TRIAGE_TIME", this.getNowTimeStamp());
		
		String triageNo=this.getNowTimeStamp().toString().substring(0, 2) + SystemTool.getInstance().getNo("ALL", "REG", "TRIAGE_NO", "TRIAGE_NO");		
		this.setValue("TRIAGE_NO", triageNo);
		this.triageNo = triageNo;

		this.setValue("ADM_TYPE", "E");	

		/** this code commented out was used to create a new case history in the past. */

		/*//		saveFiles =ERDLevelTool.getInstance().getErdLevelTemplet();
		saveFiles =EMRPublicTool.getInstance().getEmrTemplet(subClassCodeConfig);
		if(saveFiles == null){
			this.messageBox("没有病历文件");
			return;
		}	
		if(!word.onOpen(saveFiles[0], saveFiles[1], 2, false)){
			return;
		}		
		word.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");// add by wangqing 20170814			
		if(mrNo != null && !mrNo.trim().equals("")){
			Pat pat = Pat.onQueryByMrNo(mrNo);		
			word.setMicroField("姓名", patName);
			word.setMicroField("性别", pat.getSexString());
			word.setMicroField("年龄", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));	
		}
		TParm allParm = new TParm();
//		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
		word.setWordParameter(allParm);	
		EFixed erd = word.findFixed("triageNo");
		erd.clearString();
		erd.addString(triageNo);
		word.setCanEdit(true);
		word.update();*/

		/** the new code to create a case history wrote by wangqing at 20180129 */
		// the start
		//		word = (TWord) this.getComponent(WORD_TAG);
		word.onNewFile();
		word.update();		
		saveFiles =EMRPublicTool.getInstance().getEmrTemplet(subClassCodeConfig);
		if(saveFiles == null){
			this.messageBox("没有找到病历模板文件");
			return;
		}
		if(!word.onOpen(saveFiles[0], saveFiles[1], 2, false)){
			return;
		}	

		LEVEL_ONE_COUNT = 0;
		LEVEL_TWO_COUNT = 0;
		LEVEL_THREE_COUNT = 0;
		LEVEL_FOUR_COUNT = 0;

		// binding events for word
		// ECheckBoxChoose选择事件
		word.addListener(TWordBase.CHECK_BOX_CHOOSE_CLICKED, this, "checkBoxChooseClicked");

		// ESingleChoose选择事件
		word.addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected");

		// word双击事件
		word.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==2){
					//					messageBox("word双击事件");

				}
			}
		});

		// assign triage_no a value
		this.setEFixedValue(word, "triageNo", triageNo);

		/*// assign mr_no、 pat_name、pat_sex、pat_age values if mr_no is not null
		if(mrNo != null && !mrNo.trim().equals("")){
			// assign mr_no a value
			TParm allParm = new TParm();
			//		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			word.setWordParameter(allParm);	

			// assign pat_name、pat_sex、pat_age values
			Pat pat = Pat.onQueryByMrNo(mrNo);		
			word.setMicroField("姓名", pat.getName());
			word.setMicroField("性别", pat.getSexString());
			if(pat.getBirthday()!=null){
				word.setMicroField("年龄", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));
			}		
		}
		word.update();*/

		/*// 为保证消息及时性，新增时发送首诊心电Hl7消息
		TParm result = Hl7Communications.getInstance().sendFirstEcg(triageNo);
		if (result.getErrCode() < 0) {
			this.messageBox("首诊心电消息发送失败:" + result.getErrText());
		}else{
			// modified by wangqing 20170822 院内首次心电时间，修改sql
			String insertSql = "INSERT INTO AMI_ERD_NS_RECORD (TRIAGE_NO, FIRST_IN_ECG_TIME, OPT_USER, OPT_DATE, OPT_TERM) "
					+ "VALUES('" + triageNo + "', SYSDATE," 
					+ "'"+ Operator.getID() + "', "
					+ "SYSDATE,"
					+ "'" + Operator.getIP() + "')";
			result = new TParm(TJDODBTool.getInstance().update(insertSql));
		}*/


	}

	/**
	 * 查询
	 */
	public void onQuery(){
		// 查询时，设置保存不可用，新增可用 start
		callFunction("UI|SAVE|setEnabled", false);
		callFunction("UI|NEW|setEnabled", true);
		// end
		
		table.removeRowAll();
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo.length() > 0){
			this.onTriageNo();
			return;
		}
		if(this.getValue("ADM_DATE")==null || this.getValueString("ADM_DATE").trim().length()<=0){
			this.messageBox("到大门时间不能为空！！！");
			return;
		}	
		String admDate = this.timestampToString((Timestamp) this.getValue("ADM_DATE"), "yyyyMMdd");					
		//		String triageUser = this.getValueString("TRIAGE_USER");
		String deptCode = this.getValueString("DEPT_CODE");
		String clinicreaCode = this.getValueString("CLINICAREA_CODE");

		String sql="SELECT A.TRIAGE_NO, TO_CHAR(A.COME_TIME, 'HH24:MI:SS') COME_TIME, B.LEVEL_DESC LEVEL_CODE," +
				"A.SEX_CODE, A.ADM_TYPE, A.IDNO, A.PAT_NAME, A.MR_NO, A.ADM_DATE, A.CASE_NO,"+ 
				"A.DEPT_CODE, A.CLINICAREA_CODE, A.TRIAGE_USER, TO_CHAR(A.TRIAGE_TIME, 'yyyy/MM/dd HH24:MI:SS') TRIAGE_TIME, C.ENTER_ROUTE, C.PATH_KIND " +
				" FROM ERD_EVALUTION A, REG_ERD_LEVEL B, REG_PATADM C " +
				" WHERE A.LEVEL_CODE = B.LEVEL_CODE(+) "
				+ "AND A.CASE_NO = C.CASE_NO(+) "
				+ "AND A.ADM_TYPE='E' "
				+ "AND A.ADM_DATE=TO_DATE ('"+admDate+"','YYYYMMDD') ";
		if(deptCode.length() > 0){
			sql += " AND A.DEPT_CODE='"+deptCode+"'";
		}
		if(clinicreaCode.length() > 0){
			sql += " AND A.CLINICAREA_CODE='"+clinicreaCode+"'";
		}
		//		if(triageUser.length() > 0){
		//			sql += " AND A.TRIAGE_USER='"+triageUser+"'";
		//		}
		sql += " ORDER BY A.TRIAGE_TIME DESC";
		//		System.out.println(sql);
		TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(tableParm);
		//		System.out.println("tableParm="+tableParm);

		// add by wangqing 20170815 start
		// 胸痛病患提示
		Color red = new Color(255, 0, 0);
		Color pink =new Color(255,170,255);
		HashMap map = new HashMap();
		HashMap wmap = new HashMap();
		SelectResult sr = new SelectResult(tableParm);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && er.equals("E02")){
				map.put(i, pink);
			}
			if(pk != null && pk.equals("P01")){
				wmap.put(i, red);
			}
		}
		table.setRowColorMap(map);
		//				table.setRowTextColorMap(wmap);
		// add by wangqing 20170815 end

	}

	/**
	 * 检伤号回车查询
	 */
	public void onTriageNo(){
		// 查询时，设置保存不可用，新增可用 start
		callFunction("UI|SAVE|setEnabled", false);
		callFunction("UI|NEW|setEnabled", true);
		// end
		
		table.removeRowAll();
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo==null || triageNo.trim().equals("")){
			this.messageBox("检伤号不能为空！！！");
			return;
		}	
		String sql="SELECT A.TRIAGE_NO, A.COME_TIME AS COME_TIME_T, TO_CHAR(A.COME_TIME, 'HH24:MI:SS') COME_TIME, B.LEVEL_DESC LEVEL_CODE," +
				"A.SEX_CODE,A.ADM_TYPE,A.IDNO,A.PAT_NAME,A.MR_NO, A.ADM_DATE,A.CASE_NO,"+ 
				"A.DEPT_CODE, A.CLINICAREA_CODE,A.TRIAGE_USER, TO_CHAR(A.TRIAGE_TIME, 'yyyy/MM/dd HH24:MI:SS') TRIAGE_TIME, C.ENTER_ROUTE, C.PATH_KIND " +
				" FROM ERD_EVALUTION A, REG_ERD_LEVEL B, REG_PATADM C " +
				" WHERE A.LEVEL_CODE = B.LEVEL_CODE(+) "
				+ "AND A.CASE_NO = C.CASE_NO(+) "
				+ "AND A.ADM_TYPE='E' "
				+ "AND A.TRIAGE_NO='"+this.getValueString("TRIAGE_NO")+"'";
		TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		//	 System.out.println(tableParm);
		if(tableParm.getErrCode()<0){
			this.messageBox("ERR:" + tableParm.getErrCode() + tableParm.getErrText() +
					tableParm.getErrName());
			return;
		}
		if(tableParm.getCount()<=0){
			this.messageBox("没有此检伤号数据！！！");
			return;
		}
		table.setParmValue(tableParm);

		// add by wangqing 20170815 start
		// 胸痛病患提示
		Color red = new Color(255, 0, 0);
		Color pink =new Color(255,170,255);
		HashMap map = new HashMap();
		HashMap wmap = new HashMap();
		SelectResult sr = new SelectResult(tableParm);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && er.equals("E02")){
				map.put(i, pink);
			}
			if(pk != null && pk.equals("P01")){
				wmap.put(i, red);
			}
		}
		table.setRowColorMap(map);
		//		table.setRowTextColorMap(wmap);
		// add by wangqing 20170815 end

		table.setSelectedRow(0);
		onTABLEClicked(table.getSelectedRow());
	}

	/**
	 * 左侧列表单击事件
	 * @author Eric
	 * @param row
	 */
	public void onTABLEClicked(int row){	
		if (row < 0) {
			return;
		}
		myFlg = 1;
		this.callFunction("UI|new|setEnabled", false);
		this.callFunction("UI|save|setEnabled", true);

		TParm tableParm = table.getParmValue();
		setValueForParm("PAT_NAME;IDNO;SEX_CODE;TRIAGE_NO;ADM_TYPE;ADM_DATE;DEPT_CODE;CLINICAREA_CODE;TRIAGE_USER",tableParm,row);
		this.setValue("COME_TIME", this.stringToTimestamp(tableParm.getValue("COME_TIME", row), "HH:mm:ss"));
		this.setValue("TRIAGE_TIME", this.stringToTimestamp(tableParm.getValue("TRIAGE_TIME", row), "yyyy/MM/dd HH:mm:ss"));
		caseNo = tableParm.getValue("CASE_NO", row);
		mrNo=tableParm.getValue("MR_NO", row);
		triageNo = table.getItemString(row, "TRIAGE_NO");
		patName = table.getItemString(row, "PAT_NAME");
		idNo = table.getItemString(row, "IDNO");
		sexCode = table.getItemString(row, "SEX_CODE");

		/** this code commented out was used to open a new emergency triage emr in the past. */

		/*//		saveFiles = ERDLevelTool.getInstance().getELFile(triageNo);
		saveFiles = EMRPublicTool.getInstance().getEmrFile(triageNo, classCodeConfig, subClassCodeConfig);
		word.onOpen(saveFiles[0], saveFiles[1], 3, false);
		word.setCanEdit(true);
		word.update();

		// add by wangqing 20170814 start
		word.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");
		// add by wangqing 20170814 end

		// modified by wangqing 注意各版本的兼容性问题
		try{
			if(mrNo.length() > 0){
				Pat pat = Pat.onQueryByMrNo(mrNo);
				this.setValue("BIRTH_DATE", pat.getBirthday());
				this.setValue("AGE", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));
				word.setMicroField("姓名", patName);
				word.setMicroField("性别", pat.getSexString());
				word.setMicroField("年龄", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));
				TParm mrNoParm = new TParm();
				mrNoParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);	
				word.setWordParameter(mrNoParm);
			}
		}catch(Exception e){

		}*/	
		
		LEVEL_ONE_COUNT = 0;
		LEVEL_TWO_COUNT = 0;
		LEVEL_THREE_COUNT = 0;
		LEVEL_FOUR_COUNT = 0;
		
		/** the new code to open a emergency triage emr，differentiate the old and new versions　*/
		// the start
		// clear the content of word
		word.onNewFile();
		word.update();		
		saveFiles = EMRPublicTool.getInstance().getEmrFileRebuild(triageNo, classCodeConfig, subClassCodeConfig);
		//		word.onOpen(saveFiles[0], saveFiles[1], 3, false);
		if(!word.onOpen(saveFiles[0], saveFiles[1], 3, false)){
			this.messageBox("打开病历失败");
			return;
		}	
		if(getECheckBoxChoose(word, "ONW_LEVEL_1")!=null){// the new version		
			for(int i=1; i<=LEVEL_ONE_ITEM_COUNT; i++){
				if(getECheckBoxChooseChecked(word, "ONW_LEVEL_"+i)){
					LEVEL_ONE_COUNT++;
				}		
			}
			for(int i=1; i<=LEVEL_TWO_ITEM_COUNT; i++){
				if(getECheckBoxChooseChecked(word, "TWO_LEVEL_"+i)){
					LEVEL_TWO_COUNT++;
				}		
			}
			for(int i=1; i<=LEVEL_THREE_ITEM_COUNT; i++){
				if(getECheckBoxChooseChecked(word, "THREE_LEVEL_"+i)){
					LEVEL_THREE_COUNT++;
				}		
			}
			for(int i=1; i<=LEVEL_FOUR_ITEM_COUNT; i++){
				if(getECheckBoxChooseChecked(word, "FOUR_LEVEL_"+i)){
					LEVEL_FOUR_COUNT++;
				}		
			}
			// binding events for word
			// ECheckBoxChoose选择事件
			word.addListener(TWordBase.CHECK_BOX_CHOOSE_CLICKED, this, "checkBoxChooseClicked");

			// ESingleChoose选择事件
			word.addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected");

			// word双击事件
			word.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					if(e.getClickCount()==2){
						//					messageBox("word双击事件");

					}
				}
			});
			// check whether if the mr_no of word is null.if null and the mr_no now is not null, update it and auto save when the page closes;else do nothing.
			if(saveFiles[3]==null || saveFiles[3].trim().length()<=0){// the mr_no of word is null
				if(mrNo != null && !mrNo.trim().equals("")){
					// assign mr_no a value
					TParm allParm = new TParm();
					//		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
					allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
					word.setWordParameter(allParm);	

					// assign pat_name、pat_sex、pat_age values
					Pat pat = Pat.onQueryByMrNo(mrNo);		
					word.setMicroField("姓名", pat.getName());
					word.setMicroField("性别", pat.getSexString());
					if(pat.getBirthday()!=null){
						word.setMicroField("年龄", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));
					}	
					isAutoSave = true;
				}	
			}else{// the mr_no of word is not null

			}
			word.update();
		}else{// the old version
			word.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");
			// check whether if the mr_no of word is null.if null and the mr_no now is not null, update it and auto save when the page closes;else do nothing.
			if(saveFiles[3]==null || saveFiles[3].trim().length()<=0){// the mr_no of word is null
				if(mrNo != null && !mrNo.trim().equals("")){
					// assign mr_no a value
					TParm allParm = new TParm();
					//		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
					allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
					word.setWordParameter(allParm);	

					// assign pat_name、pat_sex、pat_age values
					Pat pat = Pat.onQueryByMrNo(mrNo);		
					word.setMicroField("姓名", pat.getName());
					word.setMicroField("性别", pat.getSexString());
					if(pat.getBirthday()!=null){
						word.setMicroField("年龄", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));
					}	
					isAutoSave = true;
				}	
			}else{// the mr_no of word is not null

			}
			word.update();
		}
		// the end




	}

	/**
	 * 保存
	 * @author Eric
	 */
	public void onsave(){
		if(myFlg==0){
			// 保存病历索引
			TParm parm1 = new TParm();
			parm1.setData("CASE_NO", triageNo);// 检伤号
			parm1.setData("CLASS_CODE_CONFIG", classCodeConfig);// 
			parm1.setData("SUB_CLASS_CODE_CONFIG", subClassCodeConfig);//
			parm1.setData("SUB_CLASS_DESC", saveFiles[1]);// 
			parm1.setData("MR_NO", mrNo);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(parm1);
			if (erdParm.getErrCode() < 0) {
				this.messageBox(erdParm.getErrText());
				return;
			}
			// 保存病历文件
			String path = erdParm.getValue("PATH");
			String fileName = erdParm.getValue("FILENAME");
			word.setMessageBoxSwitch(false);
			word.onSaveAs(path, fileName, 3);

			// erd_evalution insert
			TParm parm = new TParm();
			String levelDesc = this.getLevelDesc();
			if(levelCode.get(levelDesc) != null){
				parm.setData("LEVEL_CODE", levelCode.get(levelDesc));
			}else{
				parm.setData("LEVEL_CODE", "");
			}	
			parm.setData("TRIAGE_NO",triageNo);
			parm.setData("CASE_NO", caseNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
			parm.setData("CLINICAREA_CODE", this.getValueString("CLINICAREA_CODE"));
			parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
			parm.setData("REGION_CODE", Operator.getRegion());
			parm.setData("ADM_DATE", this.timestampToString((Timestamp) getValue("ADM_DATE"), "yyyyMMdd"));
			parm.setData("COME_TIME", this.timestampToString((Timestamp) getValue("COME_TIME"), "HHmmss"));
			parm.setData("TRIAGE_TIME", this.timestampToString((Timestamp) getValue("ADM_DATE"), "yyyy/MM/dd")
					+" "+this.timestampToString((Timestamp) getValue("TRIAGE_TIME"), "HH:mm:ss"));
						
			parm.setData("TRIAGE_USER", Operator.getID());
			parm.setData("PAT_NAME", patName);
			parm.setData("IDNO", idNo);
			parm.setData("SEX_CODE", sexCode);
			parm.setData("FILE_PATH", "");
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("OPT_USER", Operator.getID());		
			// 新增检伤区域
			String triageZone = this.getEFixedValue(word, "TRIAGE_ZONE");
			if(triageZone!=null && triageZone.trim().length()>0){
				triageZone = TRIAGE_ZONE.get(triageZone);
			}
			parm.setData("TRIAGE_ZONE", triageZone);
					
			TParm result = new TParm();
			result = TIOM_AppServer.executeAction("action.reg.REGAction","onSaveErdEvalution", parm);
			if(result.getErrCode()<0){
				this.messageBox(result.getErrText());
				return;
			}
		}
		if(myFlg==1){
			// 保存病历文件
			word.setMessageBoxSwitch(false);
			word.onSaveAs(saveFiles[0], saveFiles[1], 3);
			// erd_evalution update
			TParm parm = new TParm();
			String levelDesc = this.getLevelDesc();
			if(levelCode.get(levelDesc) != null){
				parm.setData("AFTER_LEVEL_CODE", levelCode.get(levelDesc));
			}else{
				parm.setData("AFTER_LEVEL_CODE", "");
			}
			parm.setData("TRIAGE_NO", triageNo);
			String levelCodeSql = " SELECT LEVEL_CODE FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' ";
			TParm  levelCodeResult = new TParm(TJDODBTool.getInstance().select(levelCodeSql));
			String levelCodeOld = levelCodeResult.getValue("LEVEL_CODE", 0);
			String levelCodeNew= (String)( levelCode.get(levelDesc));
			// add by wangqing 20171205 start
			// 解决reg_patadm检伤等级为null的问题
			String sql11 = " SELECT TRIAGE_NO, CASE_NO FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' ";
			TParm result11 = new TParm(TJDODBTool.getInstance().select(sql11));
			if(result11.getErrCode()<0){
				this.messageBox(result11.getErrText());
				return;
			}
			if(result11.getCount()<=0){
				this.messageBox("没有此检伤号数据");
				return;
			}
			caseNo = result11.getValue("CASE_NO", 0);
			// add by wangqing 20171205 end
			if(!levelCodeOld.equals(levelCodeNew)){
				parm.setData("BEFORE_LEVEL_CODE", levelCodeOld);
				TParm parmLog = new TParm();
				parmLog.setData("ID", REGTool.getInstance().getSystemTime());
				parmLog.setData("TRIAGE_NO", triageNo);
				parmLog.setData("CASE_NO", caseNo);
				parmLog.setData("MR_NO", mrNo);
				parmLog.setData("BEFORE_LEVEL_CODE", levelCodeOld);
				parmLog.setData("AFTER_LEVEL_CODE", levelCodeNew);
				parmLog.setData("REASON", "");
				parmLog.setData("OPT_TERM", Operator.getIP());
				parmLog.setData("OPT_USER", Operator.getID());
				parm.setData("erdLevelLog", parmLog.getData());
			}else{
				parm.setData("BEFORE_LEVEL_CODE", "");
			}
			// add by wangqing 20170626 start
			parm.setData("CASE_NO", caseNo);
			parm.setData("LEVEL_CODE", levelCodeNew);
			// add by wangqing 20170626 end
			
			// 新增检伤区域
			String triageZone = this.getEFixedValue(word, "TRIAGE_ZONE");
			if(triageZone!=null && triageZone.trim().length()>0){
				triageZone = TRIAGE_ZONE.get(triageZone);
			}
			parm.setData("TRIAGE_ZONE", triageZone);
			
			TParm result = new TParm();
			result = TIOM_AppServer.executeAction("action.reg.REGAction","updateErdEvalution", parm);
			if(result.getErrCode()<0){
				this.messageBox(result.getErrText());
				return;
			}
		}
		this.messageBox("保存成功！！！");
		this.onClear();
		this.onQuery();
	}

	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;AGE;TRIAGE_NO;ADM_TYPE;ADM_DATE;COME_TIME;TRIAGE_USER;DEPT_CODE;CLINICAREA_CODE");
		// add by wangqing 20170704
		this.clearValue("TRIAGE_TIME");
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("CLINICAREA_CODE", Operator.getStation());
		//		this.setValue("TRIAGE_USER", Operator.getID());

		Timestamp today = SystemTool.getInstance().getDate();
		long currentTime = System.currentTimeMillis() ;
		currentTime -= 1*60*1000;
		Date date=new Date(currentTime);

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String gateTime = dateFormat.format(date);		
		this.setValue("ADM_DATE", today);
		this.setValue("COME_TIME", gateTime);
		this.setValue("ADM_TYPE", "E");

		callFunction("UI|SAVE|setEnabled", false);
		callFunction("UI|NEW|setEnabled", true);
		table.removeRowAll();
		caseNo="";
		mrNo="";
		patName = "";
		idNo = "";
		sexCode = "";
		triageNo = "";
		word.onNewFile();
		word.update();

	}

	/**
	 * 界面关闭事件
	 */
	public boolean onClosing(){
		if(isAutoSave){
			onUpdateMrNo();
		}
		return true;
	}

	/**
	 * 更新病案号
	 */
	public void onUpdateMrNo(){
		String sql = "UPDATE EMR_FILE_INDEX "
				+ "SET MR_NO='"+mrNo+"' "
				+ "WHERE CASE_NO='"+triageNo+"' "
				+ "AND SUBCLASS_CODE='"+TConfig.getSystemValue(subClassCodeConfig)+"' "
				+ "AND CLASS_CODE='"+TConfig.getSystemValue(classCodeConfig)+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			return;
		}

		String path = "";
		String fileName = "";
		path = saveFiles[0];
		fileName = saveFiles[1];
		word.setMessageBoxSwitch(false);
		word.onSaveAs(path, fileName, 3);
		word.setCanEdit(true);
		word.update();
	}


	/**
	 * 胸痛急诊护士记录
	 */
	public void onErdNurse(){
		if(table.getSelectedRow()<0){
			this.messageBox("请选择一行数据！！！");
			return;
		}
		TParm parm = new TParm();	
		String triageNo = this.getValueString("TRIAGE_NO");
		parm.setData("triageNo",triageNo);
		parm.setData("caseNo",caseNo);
		parm.setData("mrNo",mrNo);
		this.openWindow("%ROOT%\\config\\reg\\REGPreviewingPreedWindow.x", parm);	
	}

	/**
	 * <p>急诊抢救</p>
	 * 
	 * @author wangqing 20170627
	 */
	public void onErdSave(){
		if(table.getSelectedRow()<0){
			this.messageBox("请选择一行数据！！！");
			return;
		}
		TParm tableParm = table.getParmValue();
		TParm parm = new TParm();
		parm.setData("CASE_NO", tableParm.getValue("CASE_NO", table.getSelectedRow()));
		parm.setData("MR_NO", tableParm.getValue("MR_NO", table.getSelectedRow()));
		parm.setData("PAT_NAME", tableParm.getValue("PAT_NAME", table.getSelectedRow()));
		parm.setData("TRIAGE_NO", tableParm.getValue("TRIAGE_NO", table.getSelectedRow()));
		parm.setData("FLG", "NURSE");		
		this.openDialog("%ROOT%\\config\\erd\\ERDDynamicRcd2.x", parm);
	}

	/**
	 * 打印
	 */
	public void onWrist(){
		table.acceptText();
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择要打印的检伤号");
			return;
		}
		TParm tableParm = table.getParmValue();
		TParm print = new TParm();
		print.setData("BARCODE", "TEXT", tableParm.getValue("TRIAGE_NO", row));
		//        System.out.println(tableParm.getValue("PAT_NAME", row));
		print.setData("PAT_NAME", "TEXT", tableParm.getValue("PAT_NAME", row));
		print.setData("SEQ", "TEXT", tableParm.getValue("TRIAGE_NO", row).substring(8));
		// 检伤等级
		if(tableParm.getValue("LEVEL_CODE", row) != null && !tableParm.getValue("LEVEL_CODE", row).trim().equals("")){
			print.setData("LEVEL_CODE", "TEXT", tableParm.getValue("LEVEL_CODE", row).substring(1));
		}else{
			print.setData("LEVEL_CODE", "TEXT", "");
		}

		this.openPrintDialog("%ROOT%\\config\\prt\\ERD\\ERDEvalutionWist", print);

	}

	/**
	 * 跌倒、疼痛评估
	 * @author wangqing 20180124
	 */
	public void onFallAndPainAssessment(){

		TTable table = (TTable) this.getComponent("TABLE");
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择一行数据");
			return;
		}
		TParm tblParm = table.getParmValue();
		String triageNo = tblParm.getValue("TRIAGE_NO", row);
		if(triageNo==null || triageNo.trim().length()<=0){
			return;
		}
		String mrNo = tblParm.getValue("MR_NO", row);
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);
		parm.setData("MR_NO", mrNo);
		this.openWindow("%ROOT%\\config\\reg\\REGFallAndPainAssessment.x", parm);	
	}

	/**
	 * 监护仪
	 */
	public void onMonitor(){
		this.messageBox("接口尚未开放");
	}

	/**
	 * 首诊心电
	 */
	public void onFirstECG() {
		// 检伤号
		String triageNo = this.getValueString("TRIAGE_NO");

		if (StringUtils.isEmpty(triageNo)) {
			return;
		}

		try {
			TSocket tsocket = TIOM_FileServer.getSocket("Main");
			String pdfPath = TIOM_FileServer.getRoot() + firstEcgPdfPath;
			byte[] data = null;
			String[] fileArray;
			boolean existFlg = false;
			String pdfFileName = "";

			// 如果存在就诊号则先去病患个人文件夹中寻找文件
			if (!StringUtils.isEmpty(caseNo)) {
				TParm parm = new TParm();
				parm.setData("CASE_NO", caseNo);
				// 查询已开立医嘱
				parm = OrderTool.getInstance().query(parm);
				// 胸痛中心心电医嘱代码
				String cpcEcgOrderCode = TConfig.getSystemValue("CPC_ECG_ORDER");
				String applicationNo = "";
				int orderCount = parm.getCount();

				for (int i = 0; i < orderCount; i++) {
					if (cpcEcgOrderCode.contains(parm.getValue("ORDER_CODE", i))) {
						applicationNo = parm.getValue("MED_APPLY_NO", i);
						break;
					}
				}

				if (StringUtils.isEmpty(applicationNo)) {
					existFlg = false;
				} else {
					tsocket = this.getTSocket(caseNo);
					pdfPath = this.getEmrDataDir(caseNo) + "PDF"
							+ File.separator + caseNo.substring(0, 2)
							+ File.separator + caseNo.substring(2, 4)
							+ File.separator + mrNo;
					// 去服务器上指定的文件夹扫描文件
					fileArray = TIOM_FileServer.listFile(tsocket, pdfPath);
					if (fileArray == null) {
						existFlg = false;
					} else {
						int count = fileArray.length;
						for (int i = 0; i < count; i++) {
							pdfFileName = fileArray[i];
							if (pdfFileName.contains(applicationNo)) {
								data = TIOM_FileServer.readFile(tsocket, pdfPath
										+ File.separator + pdfFileName);
								if (data == null) {
									messageBox("读取文件错误:" + pdfPath
											+ File.separator + pdfFileName);
									return;
								}
								existFlg = true;
								break;
							}
						}
					}
				}
			}

			// 如果以上步骤没有找到文件则去临时文件夹中查找
			if (!existFlg) {
				tsocket = TIOM_FileServer.getSocket("Main");
				pdfPath = TIOM_FileServer.getRoot() + firstEcgPdfPath;

				// 去服务器上指定的文件夹扫描文件
				fileArray = TIOM_FileServer.listFile(tsocket, pdfPath);
				if (fileArray == null) {
					existFlg = false;
				} else {
					int count = fileArray.length;
					for (int i = 0; i < count; i++) {
						pdfFileName = fileArray[i];
						if (pdfFileName.contains(triageNo)) {
							data = TIOM_FileServer.readFile(tsocket, pdfPath
									+ File.separator + pdfFileName);
							if (data == null) {
								messageBox("读取文件错误:" + pdfPath
										+ File.separator + pdfFileName);
								return;
							}
							existFlg = true;
							break;
						}
					}
				}
			}

			// 打开本地文件
			if (existFlg) {
				// 将服务器上的文件写入本地缓存路径
				FileTool.setByte(tempPath + File.separator + pdfFileName, data);
				Runtime runtime = Runtime.getRuntime();
				// 打开文件
				runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
						+ "\\" + pdfFileName);
			} else {
				// 经过以上两个路径的查找，如果再没有，则提示文件未找到
				this.messageBox("未找到心电报告");
				return;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


	/**
	 * 计算表达式
	 * @param wordName
	 */
	public void calculateExpression(String wordTag){
		String source_ = this.getEFixedValue(word, "source");
		if(source_ == null){
			return;
		}
		if(source_.trim().equals("")){
			erdLevel = "";
		}
		double source =Double.parseDouble(source_);
		if(source>=7){
			erdLevel="一级";
		}else if(source>=4){
			erdLevel="二级";
		}else if(source>=0.5){
			erdLevel="三级";
		}else if(source>=0){
			erdLevel = "四级";
		}
		EFixed erd = word.findFixed("erdLevel");
		erd.clearString();
		erd.addString(erdLevel);
		word.update();
	}

	/**
	 * 选择框单击事件
	 * @param wordTag
	 * @param checkBoxChooseName
	 * @author wangqing 20180119
	 */
	public void checkBoxChooseClicked(String wordTag, String checkBoxChooseName){
		// check the version of word
		if(getECheckBoxChoose(word, "ONW_LEVEL_1")!=null){// new version
//						this.messageBox("选择框单击事件");

			if(wordTag!=null && wordTag.equals(WORD_TAG)){
				if(checkBoxChooseName!=null && checkBoxChooseName.trim().length()>0){
					if(checkBoxChooseName.startsWith("ONW_LEVEL")){
						//					this.messageBox("一级");
						if(this.getECheckBoxChooseChecked(word, checkBoxChooseName)){
							//						this.messageBox("一级 is selected");
							this.LEVEL_ONE_COUNT++;	
						}else{
							//						this.messageBox("一级 is unselected");
							this.LEVEL_ONE_COUNT--;
						}	
					}else if(checkBoxChooseName.startsWith("TWO_LEVEL")){
						//					this.messageBox("二级");
						if(this.getECheckBoxChooseChecked(word, checkBoxChooseName)){
							//						this.messageBox("二级 is selected");
							this.LEVEL_TWO_COUNT++;					
						}else{
							//						this.messageBox("二级 is unselected");
							this.LEVEL_TWO_COUNT--;
						}	
					}else if(checkBoxChooseName.startsWith("THREE_LEVEL")){
						//					this.messageBox("三级");
						if(this.getECheckBoxChooseChecked(word, checkBoxChooseName)){
							//						this.messageBox("三级 is selected");
							this.LEVEL_THREE_COUNT++;					
						}else{
							//						this.messageBox("三级 is unselected");
							this.LEVEL_THREE_COUNT--;
						}				
					}else if(checkBoxChooseName.startsWith("FOUR_LEVEL")){
						//					this.messageBox("四级");
						if(this.getECheckBoxChooseChecked(word, checkBoxChooseName)){
							//						this.messageBox("四级 is selected");
							this.LEVEL_FOUR_COUNT++;					
						}else{
							//						this.messageBox("四级 is unselected");
							this.LEVEL_FOUR_COUNT--;
						}					
					}else{
						return;
					}
					this.evaluateTriageLevel();			
				}else{
					return;
				}		
			}else{
				return;
			}			
		}else{// old version

		}	
	}

	/**
	 * 计算检伤等级、计算分区
	 * @author wangqing 20180119
	 */
	public void evaluateTriageLevel(){
		if(this.LEVEL_ONE_COUNT>0){
			this.setEFixedValue(word, "erdLevel", "一级");
			this.setEFixedValue(word, "TRIAGE_ZONE", "A区");
		}else if(this.LEVEL_TWO_COUNT>0){
			this.setEFixedValue(word, "erdLevel", "二级");
			this.setEFixedValue(word, "TRIAGE_ZONE", "A区");
		}else if(this.LEVEL_THREE_COUNT>0){
			this.setEFixedValue(word, "erdLevel", "三级");
			this.setEFixedValue(word, "TRIAGE_ZONE", "C区");
		}else if(this.LEVEL_FOUR_COUNT>0){
			this.setEFixedValue(word, "erdLevel", "四级");
			this.setEFixedValue(word, "TRIAGE_ZONE", "C区");
		}else{
			this.setEFixedValue(word, "erdLevel", "");
			this.setEFixedValue(word, "TRIAGE_ZONE", "");
		}
	}

	/**
	 * ESingleChoose选择事件
	 * @param wordTag
	 * @param singleChooseName
	 * @author wangqing 20180119
	 */
	public void singleChooseSelected(String wordTag, String singleChooseName){

	}

	// -------------------------------急诊检伤修订 by wangqing end---------------------------------


	/**
	 * 获取当前时间的java.sql.Date格式
	 * @return
	 */
	public java.sql.Date getNowDate(){
		long time = System.currentTimeMillis() ;
		Date now = new java.sql.Date(time);		
		return now;
	}

	/**
	 * 获取当前时间java.sql.Timestamp格式
	 * @return
	 */
	public java.sql.Timestamp getNowTimeStamp(){
		long time = System.currentTimeMillis() ;
		Timestamp now = new java.sql.Timestamp(time);
		return now;
	}

	/**
	 * java.sql.Date转java.sql.Timestamp
	 * @param date
	 * @return
	 */
	public java.sql.Timestamp dateToTimestamp(java.sql.Date date){
		java.sql.Timestamp now = new java.sql.Timestamp(date.getTime());
		return now;
	}

	/**
	 * java.sql.Date转String
	 * @param date
	 * @param format
	 * @return
	 */
	public String dateToString(java.sql.Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = sdf.format(date);
		return s;       
	}

	/**
	 * java.sql.Timestamp转String
	 * @param time
	 * @param format
	 * @return
	 */
	public String timestampToString(java.sql.Timestamp time, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = sdf.format(time);
		return s; 
	}

	/**
	 * String转java.sql.Date
	 * @param s
	 * @param format
	 * @return
	 */
	public java.sql.Date stringToDate(String s, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		java.util.Date date;
		try {
			date = sdf.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		java.sql.Date d = new java.sql.Date(date.getTime());
		return d;
	}

	/**
	 * String转java.sql.Timestamp
	 * @param s
	 * @param format
	 * @return
	 */
	public java.sql.Timestamp stringToTimestamp(String s, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		java.util.Date date;
		try {
			date = sdf.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		java.sql.Timestamp time = new java.sql.Timestamp(date.getTime());
		return time;
	}

	/**
	 * 获取当前时间的偏移
	 * @param ms
	 * @return
	 */
	public java.sql.Timestamp getNowTimestampShift(long ms){
		long time = System.currentTimeMillis() ;
		time+=ms;
		Timestamp t = new java.sql.Timestamp(time);
		return t;
	}

	/**
	 * levelCode 赋值
	 */
	public void getLevelCode(){
		String sql="SELECT LEVEL_CODE,LEVEL_DESC FROM REG_ERD_LEVEL ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount(); i++) {
			//			System.out.println(parm.getValue("LEVEL_DESC", i).substring(1, 3));
			levelCode.put(parm.getValue("LEVEL_DESC", i).substring(1, 3),parm.getValue("LEVEL_CODE", i));
		}
	}

	/**
	 * 通过就诊号确认文件存储服务器
	 * 
	 * @param caseNo 就诊号
	 * @return TSocket
	 */
	private TSocket getTSocket(String caseNo) {
		// 默认文件服器
		TSocket tsocket = TIOM_FileServer.getSocket("Main");
		if (!StringUtils.isNotEmpty(caseNo)) {
			String sYear = caseNo.substring(0, 2);
			String ip = TConfig.getSystemValue("FileServer." + sYear + ".IP");
			if (!StringUtils.isNotEmpty(ip)) {
				tsocket = TIOM_FileServer.getSocket(sYear);
			}
		}
		return tsocket;
	}

	/**
	 * 通过就诊号确定文件存储路径
	 * 
	 * @param caseNo 就诊号
	 * @return 
	 */
	private String getEmrDataDir(String caseNo) {
		// 默认文件存储路径
		String strEmrDataDir = TIOM_FileServer.getRoot()
				+ TIOM_FileServer.getPath("EmrData");
		if (!StringUtils.isNotEmpty(caseNo)) {
			String sYear = caseNo.substring(0, 2);
			String root = TConfig.getSystemValue("FileServer." + sYear
					+ ".Root");
			if (!StringUtils.isNotEmpty(root)) {
				strEmrDataDir = TIOM_FileServer.getRoot(sYear)
						+ TIOM_FileServer.getPath("EmrData");
			}

		}
		return strEmrDataDir;
	}

	/**
	 * 获取检伤等级描述
	 * @return
	 */
	public String getLevelDesc(){
		EFixed erd = word.findFixed("erdLevel");
		String levelDesc = erd.getString();
		if(levelDesc==null){
			levelDesc = "";
		}
		return levelDesc;
	}

	/**
	 * 获取固定文本值
	 * @param word
	 * @param name
	 * @return
	 */
	public String getEFixedValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// 固定文本
		if(f == null){ 
			System.out.println("word--->name控件不存在");
			return null;	
		}
		return f.getText();
	}

	/**
	 * <p>设置固定文本值</p>
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setEFixedValue(TWord word, String name, String value){
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(name == null){
			System.out.println("name is null");
			return;
		}
		if(value == null){
			System.out.println("value is null");
			return;
		}
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// 固定文本
		if(f == null){ 
			System.out.println("word--->name控件不存在");
			return;	
		}
		f.setText(value);
		// add by wangqing 20180129 update the word after changing it
		word.update();
	}

	/**
	 * 获取选择框选中状态
	 * @param word
	 * @param name
	 * @return
	 * @author wangqing 20180119
	 */
	public boolean getECheckBoxChooseChecked(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return false;
		}
		if(name == null){
			System.out.println("name is null");
			return false;
		}
		ECheckBoxChoose cbc=(ECheckBoxChoose)word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);//单选框
		if(cbc == null){
			System.out.println(word.getTag()+"的"+name+"控件不存在");
			return false;
		}
		return cbc.isChecked();		
	}

	/**
	 * 获取数字控件值
	 * @param name
	 * @param word
	 * @return
	 */
	public String getNumberChooseValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ENumberChoose nc = (ENumberChoose) word.findObject(name, EComponent.NUMBER_CHOOSE_TYPE);
		if(nc == null) {
			System.out.println("word--->name控件不存在");
			return null;
		}
		return nc.getText();
	}

	/**
	 * 获取单选text
	 * @param word
	 * @param name
	 * @return
	 */
	public String getESingleChooseText(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ESingleChoose sc=(ESingleChoose)word.findObject(name, EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if(sc == null){ 
			System.out.println("word--->name控件不存在");
			return null;		
		}
		return sc.getText();
	}

	/**
	 * 获取复选框控件
	 * @param word
	 * @param name
	 * @return
	 */
	public ECheckBoxChoose getECheckBoxChoose(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ECheckBoxChoose cbc=(ECheckBoxChoose)word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);//单选框
		if(cbc == null){
			System.out.println("word--->name控件不存在");
		}
		return cbc;
	}


}
