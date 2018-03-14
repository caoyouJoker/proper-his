package com.javahis.ui.odi;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.mro.MROTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.bluecore.ca.pdf.CaPdfUtil;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


public class DocQueryControlMain extends TControl {

	/**
	 * 已审核
	 */
	private static int PDF_FLG_YSH = 2;
	/**
	 * 已提交
	 */
	private static int PDF_FLG_YTJ = 1;     
	/**
	 * 未提交
	 */
	private static int PDF_FLG_WTJ = -1;       
	/**
	 * 审核退回
	 */
	private static int PDF_FLG_SHTH = -2;
	/**
	 * 归档退回
	 */
	private static int PDF_FLG_GDTH = -3;
	/**
	 * 已归档
	 */
	private static int PDF_FLG_YGD = 3;
	/**
	 * 审核通过UPDATASQL
	 */
	private String UPDATE_EXAMINE = "";
	/**
	 * 审核退回UPDATASQL
	 */
	private String UPDATE_EXAMINECANCEL = "";
	/**
	 * 已归档UPDATASQL
	 */
	private String UPDATE_FILEOK = "";
	/**
	 * 归档退回UPDATASQL
	 */
	private String UPDATE_FILECANCEL = "";
	/**
	 * 临时目录
	 */
	String tempPath = "C:\\JavaHisFile\\temp\\pdf";
	String CAPath = "C:\\JavaHisFile\\temp\\CA";
	private String KEYPATH = "C:\\CA\\"+Operator.getID()+"\\Key" ;
	private String IMAGEPATH = "C:\\CA\\"+Operator.getID()+"\\Image" ;
	/**String tempPath = TConfig.getSystemValue("FileServer.Main.Root")
			+ "\\temp\\pdf";**/
	
//	String tempPath ="C:\\JavaHisFile\\temp\\pdf";

	// =================排序辅助==============add by wanglong 20120921
	private BILComparator compare = new BILComparator();
	private int sortColumn = -1;
	private boolean ascending = false;
	private String caSwitch;// 电子签章开关
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		 callFunction("UI|PRINT|setEnabled", false);
		 callFunction("UI|PRINT_DR_CODE|setEnabled", false);
		 callFunction("UI|PRINT_DATE|setEnabled", false);
		 callFunction("UI|PrintSave|setEnabled", false);  
		if(this.getPopedem("MEDICALRECORD")){//病案室
//			((TRadioButton)this.getComponent("radioStatusWTJ")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusYTJ")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusSHTH")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusYSH")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusGDTH")).setVisible(true);
//			((TRadioButton) this.getComponent("radioStatusYGD")).setVisible(true);
			((TMenuItem)this.getComponent("examine")).setVisible(false);
			((TMenuItem)this.getComponent("examineCancel")).setVisible(false);
			((TRadioButton) this.getComponent("radioStatusYSH")).setSelected(true);
//			((TLabel) this.getComponent("CA_SIGN_FLG_LABEL")).setVisible(true);
//			((TComboBox) this.getComponent("CA_SIGN_FLG_COMBO")).setVisible(true);
		}
		if(this.getPopedem("DR")){//医师
			((TRadioButton) this.getComponent("radioStatusYTJ")).setSelected(true);
//			((TRadioButton) this.getComponent("radioStatusYSH")).setVisible(false);
//			((TRadioButton) this.getComponent("radioStatusGDTH")).setVisible(fFalse);
			((TRadioButton) this.getComponent("radioStatusYGD")).setVisible(false);
			((TMenuItem)this.getComponent("fileOK")).setVisible(false);
			((TMenuItem)this.getComponent("fileCancel")).setVisible(false);
			((TMenuItem)this.getComponent("addFile")).setVisible(false);
			((TLabel) this.getComponent("CA_SIGN_FLG_LABEL")).setVisible(false);
			((TComboBox) this.getComponent("CA_SIGN_FLG_COMBO")).setVisible(false);
			((TLabel) this.getComponent("HP_MERGE_STATUS_LABEL")).setVisible(false);
			((TComboBox) this.getComponent("HP_MERGE_STATUS_COMBO")).setVisible(false);
			
			((TMenuItem)this.getComponent("emr")).setVisible(false);
			
		}
		              
		// 初始化页面
		UPDATE_EXAMINE = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_YSH
				+ ", CHECK_CODE='" + Operator.getID() + "' ,CHECK_DATE=SYSDATE";
		UPDATE_EXAMINECANCEL = "UPDATE MRO_MRV_TECH SET CHECK_FLG="
				+ PDF_FLG_SHTH + ", CHECK_CODE='" + Operator.getID()
				+ "' ,CHECK_DATE=SYSDATE";
		UPDATE_FILEOK = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_YGD
				+ ", ARCHIVE_CODE='" + Operator.getID()
				+ "' ,ARCHIVE_DATE=SYSDATE";
		// modify by wangbin 20150424 归档退回时不覆盖审核人员数据，只更新操作者信息
		UPDATE_FILECANCEL = "UPDATE MRO_MRV_TECH SET CHECK_FLG=" + PDF_FLG_GDTH
				+ ", OPT_USER='" + Operator.getID() + "' ,OPT_DATE=SYSDATE,OPT_TERM='"+ Operator.getIP() +"'";
		TTable table = (TTable) this.getComponent("TABLE");//add by wanglong 20120921 加排序
		addSortListener(table);//add by wanglong 20120921 加排序
//		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
//		"onCheckBox");    //    add  by  chenx  添加复选框选择事件
		caSwitch = TConfig.getSystemValue("CA.SWITCH");
	}

	/**     
	 * 查询
	 */
	public void onQuery() {
		String mrNo = getValueString("MR_NO");
		if (mrNo.length() > 0) {
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			setValue("MR_NO", mrNo);
			// modify by huangtt 20160929 EMPI患者查重提示 start
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				mrNo= pat.getMrNo();
				setValue("MR_NO", mrNo);
			}	
			// modify by huangtt 20160929 EMPI患者查重提示 start
			
		}
		TTable table = (TTable) this.getComponent("TABLE");
		// 公用参数
		//MRO_RECORD B,MRO_MRV_TECH C
		String SQL = "select 'N' AS FLG,A.MR_NO,B.PAT_NAME,B.SEX SEX_CODE,B.BIRTH_DATE,B.IN_DATE,B.OUT_DATE AS DS_DATE," + //modify by wanglong 20120921 修改日期从mro_record表中查询
				"A.DS_DEPT_CODE IN_DEPT_CODE,A.VS_DR_CODE,A.IPD_NO,"
				+ "A.CASE_NO,C.CHECK_FLG,C.MERGE_CODE,C.MERGE_DATE,C.SUBMIT_CODE,C.SUBMIT_DATE,C.CHECK_CODE,C.CHECK_DATE,C.ARCHIVE_CODE,"
				+ "C.ARCHIVE_DATE ,TRUNC(C.CHECK_DATE, 'DD')   -   TRUNC(A.DS_DATE, 'DD') AS DAYS,C.PRINT_USER AS OPT_USER,C.PRINT_DATE  AS OPT_DATE,C.CA_SIGN_FLG,"
				+ "C.HP_MERGE_CODE,C.HP_MERGE_DATE,C.COPYING_RESERVATION_FLG "
				+ " FROM ADM_INP A,MRO_RECORD B,MRO_MRV_TECH C where A.MR_NO=B.MR_NO " +
				  " AND  A.MR_NO=C.MR_NO(+) AND A.CASE_NO=B.CASE_NO AND A.CASE_NO=C.CASE_NO(+) AND CANCEL_FLG ='N' ";
		String MR_NO = ((TTextField) this.getComponent("MR_NO")).getValue();
		String sreachType = ((TComboBox) this.getComponent("sreachType"))
				.getValue();     
		String inStartDate = ((TTextFormat) this.getComponent("inStartDate"))
				.getText();
		String inEndDate = ((TTextFormat) this.getComponent("inEndDate")).getText();
		String outStartDate = ((TTextFormat) this.getComponent("outStartDate"))
		.getText();
		String outEndDate = ((TTextFormat) this.getComponent("outEndDate")).getText();
		String DEPT_CODE = ((TTextFormat) this.getComponent("DEPT_CODE"))
				.getComboValue();
		String filterSQL = "  ";
		if (!StringUtil.isNullString(MR_NO)) {
			filterSQL += " AND A.MR_NO='" + MR_NO + "'";
		}

		if (!StringUtil.isNullString(inStartDate)) {
			filterSQL += "  AND B.IN_DATE>=TO_DATE('" + inStartDate //modify by wanglong 20120921 修改日期从mro_record表中查询
					+ " 00:00:00', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(inEndDate)) {
			filterSQL += "  AND B.IN_DATE<=TO_DATE('" + inEndDate //modify by wanglong 20120921 修改日期从mro_record表中查询
					+ " 23:59:59', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(outStartDate)) {
			filterSQL += "  AND B.OUT_DATE>=TO_DATE('" + outStartDate //modify by wanglong 20120921 修改日期从mro_record表中查询
			+ " 00:00:00', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(outEndDate)) {
			filterSQL += "  AND B.OUT_DATE<=TO_DATE('" + outEndDate //modify by wanglong 20120921 修改日期从mro_record表中查询
			+ " 23:59:59', 'YYYY/MM/DD  HH24:MI:SS')";
		}
		if (!StringUtil.isNullString(DEPT_CODE)) {
			filterSQL += "  AND A.DS_DEPT_CODE='" + DEPT_CODE + "'";
		}

		if (sreachType.equals("0")) {// 未出院
			filterSQL += " AND B.OUT_DATE IS NULL ";
		} else if (sreachType.equals("1")) {// 出院/未完成
			filterSQL += " AND B.OUT_DATE IS NOT NULL "
					+ " AND (B.ADMCHK_FLG<>'Y' OR B.DIAGCHK_FLG<>'Y' OR B.BILCHK_FLG<>'Y' OR B.QTYCHK_FLG<>'Y')";
		} else if (sreachType.equals("2")) {// 出院/已完成
			filterSQL += " AND B.OUT_DATE IS NOT NULL "
					+ " AND B.ADMCHK_FLG='Y' " + " AND B.DIAGCHK_FLG='Y' "
					+ " AND B.BILCHK_FLG='Y' " + " AND B.QTYCHK_FLG='Y' ";
		}
         
		int status = getStatusFlg();
		if (status != 0) {
			if(status==PDF_FLG_WTJ){
				filterSQL += "  AND C.CHECK_FLG IS NULL ";
			}else{
				filterSQL += "  AND C.CHECK_FLG=" + status + "";
			}
		}
		
		// add by wangb 2016/5/17 电子签章过滤 START
		String caSignFlg = ((TComboBox)this.getComponent("CA_SIGN_FLG_COMBO")).getSelectedID();
		if (StringUtils.isNotEmpty(caSignFlg)) {
			filterSQL += "  AND C.CA_SIGN_FLG = '" + caSignFlg + "' ";
		}
		// add by wangb 2016/5/17 电子签章过滤 END
		
		// add by wangb 2017/7/10 病案首页状态 START
		String hpMergeStatus = ((TComboBox)this.getComponent("HP_MERGE_STATUS_COMBO")).getSelectedID();
		if ("Y".equals(hpMergeStatus)) {
			filterSQL += "  AND C.HP_MERGE_CODE IS NOT NULL ";
		} else if ("N".equals(hpMergeStatus)) {
			filterSQL += "  AND C.HP_MERGE_CODE IS NULL ";
		}
		// add by wangb 2017/7/10 病案首页状态 END
		
//		System.out.println("===sql==="+SQL + filterSQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(
				SQL + filterSQL));
		int count=result.getCount("CASE_NO");
        if (count < 0) {
            ((TTextField) this.getComponent("diseaseCount")).setValue(0 + "");
        } else
		((TTextField) this.getComponent("diseaseCount")).setValue(count	+ "");
		for (int i = 0; i < count; i++) {
			String indatestr = result.getValue("ARCHIVE_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
						"yyyy-MM-dd");
				result.setData("ARCHIVE_DATE", i, date);
			}
			indatestr = result.getValue("CHECK_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
						"yyyy-MM-dd");
				result.setData("CHECK_DATE", i, date);
			}
			indatestr = result.getValue("MERGE_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("MERGE_DATE", i, date);
			}
			indatestr = result.getValue("SUBMIT_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("SUBMIT_DATE", i, date);
			}	
			indatestr = result.getValue("OPT_DATE", i);
			if(indatestr.length()>1){
				Timestamp date = StringTool.getTimestamp(indatestr,
				"yyyy-MM-dd");
				result.setData("OPT_DATE", i, date);
			}	
		}
		table.setParmValue(result);
		
		TCheckBox checkAll = (TCheckBox)this.getComponent("FLG_ALL");
		checkAll.setSelected(false);
	}
	
	/**       
	 * 复印按钮点击事件  
	 * 
	 * @return  void 
	 */    
	public void PrintClick() {
		if (Selected("PRINT")) {
			// 得到当前时间
			Timestamp date = SystemTool.getInstance().getDate();
			// 初始化查询区间
			setValue("PRINT_DATE", date.toString().substring(0, 10).replace(
					'-', '/'));
			setValue("PRINT_DR_CODE", Operator.getID());
		} else {
			setValue("PRINT_DATE", "");
			setValue("PRINT_DR_CODE", "");
		}

	}
	
	/**
	 * 病案号回车查询
	 */
	public void onPush(){
		this.onQuery() ;
	}
	
     //==================  add  by  chenxi 
	/**    
	 * 复印保存事件  
	 * 
	 * @return  void 
	 */            
	public void onPrintSave() {
		TTable table = (TTable) this.getComponent("TABLE");
		if (table == null || table.getRowCount() <= 0) {
			this.messageBox("没有保存的数据");
			return;
		}
		table.acceptText();
		TParm tableParm = table.getParmValue();
		
		// add by wangb 2017/12/14
		if (!tableParm.getValue("FLG").contains("Y")) {
			this.messageBox("请勾选要操作的数据");
			return;
		}
		
		String optUser = this.getValueString("PRINT_DR_CODE");
		String optDate = this.getValueString("PRINT_DATE").substring(0,
				10);
		String caseNo = "";
		String updateSql = "";
		
		for (int i = 0; i < tableParm.getCount("MR_NO"); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				caseNo = tableParm.getValue("CASE_NO", i);
				updateSql = " UPDATE MRO_MRV_TECH " + " SET  PRINT_USER='"
						+ optUser + "', " + " PRINT_DATE=TO_DATE('" + optDate
						+ "','YYYY-MM-DD HH24MISS') " + " WHERE CASE_NO = '"
						+ caseNo + "'" + " AND CHECK_FLG = " + PDF_FLG_YGD
						+ " ";
				TParm result = new TParm(TJDODBTool.getInstance().update(
						updateSql));
				if (result.getErrCode() < 0) {
					this.messageBox("E0005");
					return;
				}
			}
		}

		this.messageBox("P0005");
		this.onQuery();

	}
	/**
	 * 复选框选中事件
	 * 
	 * @param obj
	 * @return
	 */
	public void onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int row =table.getSelectedRow() ;
		TParm parm =table.getParmValue() ;
		parm.setData("FLG", row, "Y");
	}
	//===========================  add  by  chenx  end 
	/**
	 * 复选框全选事件
	 * 
	 * 2017.3.27
	 */
	public void onSelectAll() {
		TTable table = (TTable) this.getComponent("TABLE");
		String select=getValueString("FLG_ALL");
		TParm parm=table.getParmValue();
		int count =parm.getCount();
		for(int i=0;i<count ;i++){
			parm.setData("FLG",i,select);
		}
		table.setParmValue(parm);
	}
	//===========================  add  by  chenhj  end 	
	
	
	/**
	 * 返回查询状态
	 * 
	 * @return
	 */
	private int getStatusFlg() {
		if (isSelected("radioStatusWTJ")) {
			return PDF_FLG_WTJ;
		} else if (isSelected("radioStatusYTJ")) {
			return PDF_FLG_YTJ;
		} else if (isSelected("radioStatusYSH")) {
			return PDF_FLG_YSH;
		} else if (isSelected("radioStatusSHTH")) {
			return PDF_FLG_SHTH;
		} else if (isSelected("radioStatusYGD")) {
			return PDF_FLG_YGD;
		} else if (isSelected("radioStatusGDTH")) {
			return PDF_FLG_GDTH;
		}
		return 0;
	}

	/**
	 * TRadioButton判断是否选中
	 * 
	 * @param tagName
	 * @return
	 */
	private boolean isSelected(String tagName) {
		return ((TRadioButton) this.getComponent(tagName)).isSelected();
	}
	/**
	 * CheckBox判断是否选中
	 * 
	 * @param tagName
	 * @return
	 */
	private boolean Selected(String tagName) {
		return ((TCheckBox) this.getComponent(tagName)).isSelected();
	}

	/**
	 * 审核通过
	 * 
	 * @return
	 */
	public void onExamine() {
		update(UPDATE_EXAMINE, PDF_FLG_YSH);
	}

	/**
	 * 审核退回
	 * 
	 * @return
	 */
	public void onexamineCancel() {
		update(UPDATE_EXAMINECANCEL, PDF_FLG_SHTH);
	}

	/**
	 * 归档通过
	 * 
	 * @return
	 */
	public void onFileOK() {
		TTable table = (TTable) this.getComponent("TABLE");
		table.acceptText();
		TParm parm = table.getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			if (StringUtils.equals("Y", parm.getValue("FLG", i))) {
				// 已经完成加签的文件不可重复加签
				if (PDF_FLG_YGD == parm.getInt("CHECK_FLG", i)) {
					this.messageBox("病案号:【" + parm.getValue("MR_NO", i)
							+ "】病患的病历已归档，不可重复归档");
					return;
				}
			}
		}
		
		update(UPDATE_FILEOK, PDF_FLG_YGD);
		
		// 开启电子签章开关则进行加签处理
		if ("Y".equalsIgnoreCase(caSwitch)) {
			// 归档通过时加签电子病历
			TParm result = this.signPdf("Y");
			
			if (result.getErrCode() < 0) {
				return;
			}
			
			String caseNoStr = "";
			int count = result.getCount("CASE_NO");
			for (int i = 0; i < count; i++) {
				caseNoStr = caseNoStr + "'" + result.getValue("CASE_NO", i) + "'";
				if (i < count - 1) {
					caseNoStr = caseNoStr + ",";
				}
			}
			
			if (StringUtils.isNotEmpty(caseNoStr)) {
				String sql = "UPDATE MRO_MRV_TECH SET CA_SIGN_FLG = 'Y' WHERE CASE_NO IN ("
						+ caseNoStr + ")";
				parm = new TParm(TJDODBTool.getInstance().update(sql));
				if (parm.getErrCode() < 0) {
					this.messageBox("更新签章状态失败");
					return;
				}
			}
		}
		
		this.messageBox("操作成功");
		this.onQuery();
	}

	/**
	 * 归档退回
	 * 
	 * @return
	 */
	public void onFileCancel() {
		update(UPDATE_FILECANCEL, PDF_FLG_GDTH);
	}

	/**
	 * 合并病案首页
	 * 
	 * @return
	 */
	public void onAddFile() {
		TTable table = (TTable) this.getComponent("TABLE");
		// 强制失去表格编辑焦点
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		TParm parm = table.getParmValue();
		if (table.getRowCount() < 1 || parm == null
				|| !parm.getValue("FLG").contains("Y")) {
			this.messageBox("请勾选需要合并的数据行");
			return;
		}

		int selCount = 0;
		int count = parm.getCount();
		int selRow = 0;

		TParm selParm = new TParm();
		String[] parmNames = parm.getNames();
		for (int i = 0; i < count; i++) {
			if ("Y".equals(parm.getValue("FLG", i))) {
				selRow = i;
				selCount++;
				
				for (int j = 0; j < parmNames.length; j++) {
					selParm.addData(parmNames[j], parm.getValue(parmNames[j], i));
				}
			}
		}

		selParm.setCount(selCount);
		if (selCount == 1) {
			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO", selParm.getValue("CASE_NO", 0));
			// 查询病案首页合并信息
			TParm result = MROTool.getInstance().queryMergeHomePageInfo(queryParm);
			if (result.getErrCode() < 0) {
				this.messageBox("查询病案首页合并信息错误");
				return;
			} else if (result.getCount() > 0) {
				this.messageBox("病案首页已合并，不可重复合并");
				return;
			}
			
			// 单一病患的病案首页合并维持原样
			result = (TParm)this.openDialog("%ROOT%\\config\\odi\\ODIDocMergeHome.x", table
					.getParmValue().getRow(selRow), false);
			if (null != result && "SUCCESS".equals(result.getValue("FLG"))) {
				this.onQuery();
			}
		} else {
			String caseNo = "";
			StringBuffer errMsg = new StringBuffer();

			for (int i = 0; i < selCount; i++) {
				caseNo = caseNo + "'" + selParm.getValue("CASE_NO", i) + "'";
				if (i < selCount - 1) {
					caseNo = caseNo + ",";
				}
			}

			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO_LIST", caseNo);

			// 查询病案首页数据
			TParm result = MROTool.getInstance().queryHomePageInfo(queryParm);
			if (result.getErrCode() < 0) {
				this.messageBox("查询病案首页数据错误");
				return;
			}
			
			boolean flg = false;
			for (int i = selCount - 1; i > -1; i--) {
				flg = false;
				for (int j = 0; j < result.getCount(); j++) {
					if (StringUtils.equals(selParm.getValue("CASE_NO", i),
							result.getValue("CASE_NO", j))) {
						flg = true;
						break;
					}
				}

				if (!flg) {
					errMsg.append("病案号【" + selParm.getValue("MR_NO", i)
							+ "】尚未打印病案首页，本次不予处理\r\n");
					selParm.removeRow(i);
				}
			}

			// 查询病案首页合并信息,已完成病案首页合并的病历不能再次合并
			result = MROTool.getInstance().queryMergeHomePageInfo(queryParm);
			if (result.getErrCode() < 0) {
				this.messageBox("查询病案首页合并信息错误");
				return;
			} else if (result.getCount() > 0) {
				// 本次处理筛除掉已经完成合并的数据
				for (int i = 0; i < result.getCount(); i++) {
					errMsg.append("病案号【" + result.getValue("MR_NO", i)
							+ "】已完成病案首页合并，本次不予处理\r\n");
					for (int j = selCount - 1; j > -1; j--) {
						if (StringUtils.equals(selParm.getValue("CASE_NO", j),
								result.getValue("CASE_NO", i))) {
							selParm.removeRow(j);
							break;
						}
					}
				}
			}

			if (errMsg.length() > 0) {
				this.messageBox(errMsg.toString());
			}

			if (selParm.getCount() > 0) {
				result = (TParm)this.openDialog("%ROOT%\\config\\mro\\MROHomePageBatchMerge.x",
						selParm, false);
				if (null != result && "SUCCESS".equals(result.getValue("FLG"))) {
					this.onQuery();
				}
			}
		}
	}

	/**
	 * 更新状态
	 * 
	 * @return
	 */
	public void update(String updateType, int flg) {
		TTable table = (TTable) this.getComponent("TABLE");
		table.acceptText();
		String filterSQL = "";
		TParm parm = table.getParmValue();
		int count = parm.getCount("FLG");
		String caseNo = "";
		
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				if (filterSQL.equals("")) {
					filterSQL += " WHERE (MR_NO='" + parm.getValue("MR_NO", i)
							+ "' AND CASE_NO='" + parm.getValue("CASE_NO", i)
							+ "')";
				} else {
					filterSQL += " OR (MR_NO='" + parm.getValue("MR_NO", i)
							+ "' AND CASE_NO='" + parm.getValue("CASE_NO", i)
							+ "')";
				}
				
				caseNo = caseNo + "'" + parm.getValue("CASE_NO", i) + "',";
			}
		}
		if (filterSQL.equals("")) {
			this.messageBox("请您选择需要操作的病患。");
			return;
		}
		parm = new TParm(TJDODBTool.getInstance()
				.update(updateType + filterSQL));
		if (parm.getErrCode() < 0) {
			this.messageBox_("更新状态失败。");
			return;
		}
		
		if (caseNo.length() > 0) {
			caseNo = caseNo.substring(0, caseNo.length() - 1);
		}
		// add by wangb 2017/07/10 归档退回时清除合并信息
		if (PDF_FLG_GDTH == flg) {
			String optId = Operator.getID();
			String optIp = Operator.getIP();
			TParm updateParm = new TParm();
			updateParm.setData("CASE_NO_LIST", caseNo);
			updateParm.setData("HP_MERGE_CODE", "");
			updateParm.setData("HP_MERGE_DATE", null);
			updateParm.setData("OPT_USER", optId);
			updateParm.setData("OPT_TERM", optIp);
			// 清除病案首页合并信息
			updateParm = MROTool.getInstance()
					.updateMergeHomePageInfo(updateParm);

			if (updateParm.getErrCode() < 0) {
				this.messageBox("更新病案首页合并信息失败");
			}
		}
		
		// 归档通过不刷新
		if (PDF_FLG_YGD == flg) {
			return;
		}
		
		this.messageBox_("操作成功。");
		onQuery();
	}

	/**
	 * 浏览
	 */
	public void onReaderSubmitPDF() {
		TTable table = (TTable) this.getComponent("TABLE");
		int col = table.getSelectedColumn();
		if (col == 0) {
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		String MR_NO = parm.getValue("MR_NO");
		String caseNo = parm.getValue("CASE_NO");
		String bigFilePath = TConfig
		.getSystemValue("FileServer.Main.Root")
				+ "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
				+ caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(),bigFilePath);
		if (data == null) {
			messageBox_("尚未提交PDF");
			return;
		}
		try {
			FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime runtime = Runtime.getRuntime();
		try {
			// 打开文件
			runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
					+ "\\" + caseNo + ".pdf");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	/**
	 * 导出EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE"),
				"病案归档");
	}/**
	 * 得到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {    
		return (TTable) this.getComponent(tag);
	}
	
	// ====================排序功能begin======================add by wanglong 20120921
	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 返回指定列在列名数组中的index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * 根据列名数据，将Vector转成Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */

	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================排序功能end======================
//=====================================================   chenxi modify  20130410  CA数字签名
	//上传
	public void onUpLoad()
	{
		String caseNo;
		String MR_NO;
		TTable table = (TTable)getComponent("TABLE");
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		if (table.getSelectedRow() < 0 || table == null)
		{
			messageBox("没有选中病患");
			return;
		}
		String sreachType = ((TComboBox)getComponent("sreachType")).getValue();
		caseNo = parm.getValue("CASE_NO");
		MR_NO = parm.getValue("MR_NO");
		int status = getStatusFlg(); 
		if (sreachType.equals("2") || status == 3){	
		String dir;
		String localPath = CAPath + "\\" + caseNo + ".pdf" ;   //签名之后保存的本地路劲
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), localPath);
		String dirpath = TConfig.getSystemValue("FileServer.Main.Root")
		                   + "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO ;  //上传到服务器的路劲
			  dir = dirpath + "\\"+caseNo + ".pdf" ;
		File filepath = new File(dirpath);
		filepath = new File(localPath);
		if (!filepath.exists())
		{
			messageBox("该病患pdf文件尚未签名,不可上传");
			return;
		}
		if (!filepath.exists())
			filepath.mkdirs();
		try
		{
			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir, data);
			messageBox("上传成功");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		}
		else {
			messageBox("已完成,已归档的pdf文件才能上传");
			return;
		}
		
	}
   //  数字签名认证
	@SuppressWarnings("static-access")
	public void onInPassword()
	{	
		TTable table = (TTable)getComponent("TABLE");
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		if (table.getSelectedRow() < 0 || table == null)
		{
			messageBox("没有选中病患");
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		String MR_NO = parm.getValue("MR_NO");
		CaPdfUtil caPdf = new CaPdfUtil();
		String keyPath = KEYPATH+"\\"+"key.pfx";//-=====密钥路径
		String  sourcePdfPath = tempPath + "\\" + caseNo + ".pdf" ; ;//需签名PDF路径
		String signPdfPath = CAPath + "\\" + caseNo + ".pdf" ; ;//已签名的PDF路径
		String signImagePath = IMAGEPATH+"\\"+"Image.gif";//签名使用的图片路径
		String reason = Operator.getName();//文字描述1
		String location = SystemTool.getInstance().getDate().toString().substring(0, 19);  //文字描述2
		String bigFilePath = TConfig
		.getSystemValue("FileServer.Main.Root")
				+ "\\正式病历\\" + MR_NO.substring(0, 7) + "\\" + MR_NO + "\\"
				+ caseNo + ".pdf";
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
				.getSocket(),bigFilePath);
		if (data == null) {
			messageBox_("尚未提交PDF,不可签名");
			return;
		}
		String password = (String)openDialog("%ROOT%\\config\\mro\\MROInPassword.x");//	密码    
		//  =====创建文件夹
		File file = new File(KEYPATH) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		file = new File(IMAGEPATH) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		file = new File(CAPath) ;
		if(!file.exists()){
			file.mkdirs() ;
		}
		//判断图片和秘钥是否存在，并给出相应的提示
		file =  new File(keyPath) ;
		if(!file.exists()){
			this.messageBox("密钥不存在，请将其拷贝到"+KEYPATH+"并命名为key.pfx") ;
			return ;
		}
		file =  new File(signImagePath) ;
		if(!file.exists()){
			this.messageBox("图片不存在，请将其拷贝到"+KEYPATH+"并命名为Image.pfx") ;
			return ;
		}  
		if (password == null)
			return;
		try
		{
			caPdf.doPdfSign(keyPath, password, sourcePdfPath, signPdfPath, signImagePath, reason, location);
			messageBox("签名成功");
		}
		catch (Exception e)
		{
			System.out.println(e.toString());   
		}
		return;
	}
//	public static void main(String[] args) {
//		String keyPath = "C:\\test20121015.pfx";//-=====密钥路径
//		String  sourcePdfPath = "C:\\1.pdf" ; ;//需签名PDF路径
//		String signPdfPath = "C:\\2.pdf" ; ;//已签名的PDF路径
//		String signImagePath = "C:\\1.gif";//签名使用的图片路径
//		String reason = "1";//文字描述1
//		String location = "2";  //文字描述2           
//		String password = "111111";//	密码
//		System.out.println("1111111111====222222=密钥路径====="+keyPath);
//		System.out.println("1111111112=====需签名PDF路径====="+sourcePdfPath);     
//		System.out.println("1111111113=====已签名的PDF路径====="+signPdfPath);     
//		System.out.println("1111111114===签名使用的图====片路径======="+signImagePath);
//		System.out.println("1111111115====文字描述1==5ewewe==="+reason);   
//		System.out.println("1111111116====文字描述2======"+location);  
//		System.out.println("1111111116===3232==密码==rerer==="+password);  
//		System.out.println("1111111111=====密钥路径====="+keyPath);
//		System.out.println("1111111112=====需签名PDF路径====="+sourcePdfPath);
//		System.out.println("1111111113=====已签名的PDF路径====="+signPdfPath);
//		System.out.println("1111111114===签名使用的图片路径======="+signImagePath);
//		System.out.println("1111111115====文字描述1====="+reason);    
//		System.out.println("1111111116====文字描述2======"+location);
//		System.out.println("1111111116=====密码====="+password);      
//		CaPdfUtil.doPdfSign(keyPath, password, sourcePdfPath, signPdfPath, signImagePath, reason, location);
//	}
	
	/**
	 * 病历加签CA电子签章
	 * 
	 * @author wangbin
	 */
	public TParm signPdf(String flag) {
		TParm result = new TParm();
		if (!"Y".equalsIgnoreCase(caSwitch)) {
			result.setErr(-1, "电子签章开关未开启");
			this.messageBox(result.getErrText());
			return result;
		}
		
		TTable table = (TTable) this.getComponent("TABLE");
		table.acceptText();
		TParm parm = table.getParmValue();
		if (!parm.getValue("FLG").contains("Y")) {
			this.messageBox("请勾选相应的数据行");
			return result;
		}
		
		if (StringUtils.isEmpty(flag)) {
			for (int i = 0; i < parm.getCount(); i++) {
				if (StringUtils.equals("Y", parm.getValue("FLG", i))) {
					// 已经完成加签的文件不可重复加签
					if ("Y".equals(parm.getValue("CA_SIGN_FLG", i))) {
						this.messageBox("病案号:【" + parm.getValue("MR_NO", i)
								+ "】病患的病历已加签，不可重复加签");
						return result;
					}
					
					// 只有已归档的病历才可以单独加签
					if (StringUtils.isEmpty(parm.getValue("ARCHIVE_CODE", i))
							|| (PDF_FLG_YGD != parm.getInt("CHECK_FLG", i))) {
						this.messageBox("病案号:【" + parm.getValue("MR_NO", i)
								+ "】病患的病历未归档通过不可加签");
						return result;
					}
				}
			}
		}
		
		parm.setData("X", 5);
		parm.setData("Y", 760);
		parm.setData("W", 85);
		parm.setData("H", 840);
		
		// 调用CA时间戳接口
		result = TIOM_AppServer.executeAction(
				"action.emr.EMRSignPdfAction", "signPdf", parm);
		
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		} else {
			// 点击签章按钮时给予提示
			if (StringUtils.isEmpty(flag)) {
				this.messageBox("签章成功");
			}
		}
		
		return result;
	}
	
	public void onSealed() {
		String MR_NO = "";
		TTable table = (TTable) getComponent("TABLE");
		// System.out.println("selectrow====="+table.getSelectedRow());
		table.acceptText();
		if (table.getRowCount() > 0) {
			TParm parm = table.getParmValue().getRow(table.getSelectedRow());

			if (table.getSelectedRow() < 0) {
				MR_NO = "";
			} else {
				MR_NO = parm.getValue("MR_NO");
			}
		}

		TParm re = new TParm();
		re.setData("MR_NO", MR_NO);
		// System.out.println(re);
		this.openDialog("%ROOT%\\config\\mro\\MROSealedMain.x", re);
	}
	
	/**
	 * 签章病历展现
	 */
	public void showSignPdf() {
		TTable table = (TTable) this.getComponent("TABLE");
		
		// 强制失去编辑焦点
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		TParm parm = table.getParmValue();
		if (parm == null || parm.getCount() < 1) {
			this.messageBox("请勾选一行数据");
			return;
		}
		
		if (!parm.getValue("FLG").contains("Y")) {
			this.messageBox("请勾选一行数据");
			return;
		}

		int count = parm.getCount();
		// CA签章完毕存放服务器IP
		String serverIp = TConfig.getSystemValue("CA.STORE.SERVER_IP");
		if (StringUtils.isEmpty(serverIp)) {
			this.messageBox("CA电子签章完毕PDF存放服务器地址配置错误");
			return;
		}
		// CA签章完毕存放服务器磁盘路径
		String serverPath = TConfig.getSystemValue("CA.STORE.SIGN_PDF_PATH");
		if (StringUtils.isEmpty(serverPath)) {
			this.messageBox("CA电子签章完毕PDF存放文件位置配置错误");
			return;
		}
		// CA签章完毕存放服务器端口
		String port = TConfig.getSystemValue("CA.STORE.FILE_SERVER_PORT");
		int serverPort = 8103;
		if (StringUtils.isNotEmpty(port)) {
			serverPort = StringTool.getInt(port);
		}
		String mrNo = "";
		String caseNo = "";
		String filePath = "";
		TSocket socket = new TSocket(serverIp, serverPort);
		byte data[] = null;
		Runtime runtime = null;

		for (int i = 0; i < count; i++) {
			if ("Y".equals(parm.getValue("FLG", i))) {
				mrNo = parm.getValue("MR_NO", i);
				caseNo = parm.getValue("CASE_NO", i);
				filePath = serverPath + File.separator + mrNo.substring(0, 7)
						+ "\\" + mrNo + "\\" + caseNo + ".pdf";
				data = TIOM_FileServer.readFile(socket, filePath);

				if (data == null) {
					messageBox("签章病历不存在");
					return;
				}

				try {
					FileTool.setByte(tempPath + "\\" + caseNo + ".pdf", data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				runtime = Runtime.getRuntime();
				try {
					// 打开文件
					runtime.exec("rundll32 url.dll FileProtocolHandler "
							+ tempPath + "\\" + caseNo + ".pdf");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**    
	 * 复印预约保存事件  
	 */            
	public void onCopingReservationSave() {
		TTable table = (TTable) this.getComponent("TABLE");
		if (table == null || table.getRowCount() <= 0) {
			this.messageBox("没有保存的数据");
			return;
		}
		table.acceptText();
		TParm tableParm = table.getParmValue();

		if (!tableParm.getValue("FLG").contains("Y")) {
			this.messageBox("请勾选要操作的数据");
			return;
		}

		String caseNolist = "";
		for (int i = 0; i < tableParm.getCount("CASE_NO"); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				caseNolist += tableParm.getValue("CASE_NO", i) + "','";
			}
		}

		if (caseNolist.length() > 0) {
			caseNolist = caseNolist.substring(0, caseNolist.length() - 3);
		}

		String checkValue = ((TCheckBox) this
				.getComponent("COPYING_RESERVATION_CHECKBOX")).getValue();
		String sql = "UPDATE MRO_MRV_TECH SET COPYING_RESERVATION_FLG = '"
				+ checkValue + "' WHERE CASE_NO IN ('" + caseNolist + "')";

		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}

		this.messageBox("P0005");
		this.onQuery();
	}
	
	/**
	 * 病历状态切换
	 * 
	 * @param flg 病历状态
	 */
	public void onStatusRadioBtnChange(int flg) {
		// 病案归档
		if (flg == 3) {
			 callFunction("UI|PRINT|setEnabled", true);
			 callFunction("UI|PRINT_DR_CODE|setEnabled", true);
			 callFunction("UI|PRINT_DATE|setEnabled", true);
			 callFunction("UI|PrintSave|setEnabled", true);
			 callFunction("UI|COPYING_RESERVATION_CHECKBOX|setEnabled", true);
			 callFunction("UI|COPYING_RESERVATION_BUTTON|setEnabled", true);
		} else {
			 callFunction("UI|PRINT|setEnabled", false);
			 callFunction("UI|PRINT_DR_CODE|setEnabled", false);
			 callFunction("UI|PRINT_DATE|setEnabled", false);
			 callFunction("UI|PrintSave|setEnabled", false);
			 callFunction("UI|COPYING_RESERVATION_CHECKBOX|setEnabled", false);
			 callFunction("UI|COPYING_RESERVATION_BUTTON|setEnabled", false);
		}
	}
}
