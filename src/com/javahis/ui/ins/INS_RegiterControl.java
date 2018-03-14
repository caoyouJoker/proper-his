package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 诊疗项目备案管理
 * </p>
 * 
 * <p>
 * Description:诊疗项目备案管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangs 20140414
 * @version 1.0
 */

public class INS_RegiterControl extends TControl {
	private static final String TParm = null;
	private static TTable NeedRegisterItemTable;
	private static TTable RegisterItemTable;
	private static TTable TABLE_FEE;
	private TTabbedPane tabbedPane;
	private static TComboBox CATEGORY;
	private static TComboBox ITEM_CLASSIFICATION;
	private TParm regionParm; // 医保区域代码
	
	public void onInit() {
		NeedRegisterItemTable = (TTable) getComponent("NeedRegisterItem");
		RegisterItemTable = (TTable) getComponent("RegisterItem");
		TABLE_FEE= (TTable) getComponent("TABLE_FEE");
		tabbedPane = (TTabbedPane) getComponent("tTabbedPane_0");
		CATEGORY=(TComboBox)getComponent("CATEGORY");
		ITEM_CLASSIFICATION=(TComboBox)getComponent("ITEM_CLASSIFICATION");
		CATEGORY.setSelectedIndex(0);
		ITEM_CLASSIFICATION.setSelectedIndex(0);
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码	 
	}

	public void onQuery() {
		switch (getChangeTab()) {
		// 0 :需备案目录信息页签 1：诊疗项目备案信息页签 2:HIS医嘱查询
		case 0:
			onNeedRegisterItemTable_Q();
			break;
		case 1:
			onRegisterItemTable_Q();
			break;
		case 2:
			GetOrderInf("");
			break;
		}

	}
	/**
	 * 页签点击事件
	 */
//	public void onChangeTab() {
//
//		switch (tabbedPane.getSelectedIndex()) {
//		// 3 :费用分割前页签 4：费用分割后页签
//		case 0:
//			onNeedRegisterItemTable_Q();
//			break;
//		case 1:
//			onRegisterItemTable_Q();
//			break;
//		}
//	}
	public void onClear() {
		switch (getChangeTab()) {
		// 0 :需备案目录信息页签 1：诊疗项目备案信息页签
		case 0:
			NeedRegisterItemTable.removeRowAll();
			break;
		case 1:
			RegisterItemTable.removeRowAll();
			CATEGORY.setSelectedIndex(0);
			ITEM_CLASSIFICATION.setSelectedIndex(0);
			break;
		}
	}

	/**
	 * 导出Excel
	 * */
	public void onExport() {
		TTable table;
		switch (getChangeTab()) {
		// 0 :需备案目录信息页签 1：诊疗项目备案信息页签

		case 0:
			table = (TTable) callFunction("UI|NeedRegisterItem|getThis");
			ExportExcelUtil.getInstance().exportExcel(table, "需备案目录信息");
			break;
		case 1:
			table = (TTable) callFunction("UI|RegisterItem|getThis");
			ExportExcelUtil.getInstance().exportExcel(table, "诊疗项目备案信息");
			break;
		case 2:
			table = (TTable) callFunction("UI|TABLE_FEE|getThis");
			ExportExcelUtil.getInstance().exportExcel(table, "本院诊疗项目信息");
			break;
		}
	}

	// 取得需备案目录信息
	public void onNeedRegisterItemTable_Q() {

		String Sql = " SELECT A.NHI_CODE,A.NHI_DESC,  "
				+ " CASE WHEN A.CHARGE_CODE='02' THEN '检查费' "
				+ " WHEN A.CHARGE_CODE='03' THEN '治疗费' "
				+ " WHEN A.CHARGE_CODE='04' THEN '手术费' "
				+ " WHEN A.CHARGE_CODE='05' THEN '床位费' "
				+ " WHEN A.CHARGE_CODE='06' THEN '医用材料' "
				+ " WHEN A.CHARGE_CODE='07' THEN '其它' "
				+ " WHEN A.CHARGE_CODE='08' THEN '输全血' "
				+ " WHEN A.CHARGE_CODE='09' THEN '成分输血' "
				+ " ELSE A.CHARGE_CODE END AS CHARGE_CODE "
				+ " FROM JAVAHIS.INS_NEEDREGISTER_ITEM A "
				+ " ORDER BY A.CHARGE_CODE ,A.NHI_CODE ";
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
//		NeedRegisterItemTable.setHeader("收费项目编码,120;收费项目名称,300;统计代码,100");
		NeedRegisterItemTable.setParmMap("NHI_CODE;NHI_DESC;CHARGE_CODE;");
//		NeedRegisterItemTable.setItem("DEPT_CHN_DESC");
		NeedRegisterItemTable.setColumnHorizontalAlignmentData("0,left;1,left;2,left;");
		NeedRegisterItemTable.setParmValue(tabParm);
	} 

	// 取得诊疗项目备案信息
	public void onRegisterItemTable_Q() {
		String Sql = " SELECT 'N' AS CHOOSE,A.NHI_CODE,A.NHI_DESC,A.ADM_TYPE, "
				+ " CASE WHEN A.CATEGORY='1' THEN '诊疗下载' "
				+ " WHEN A.CATEGORY='2' THEN '补充下载' END AS CATEGORY, "
				+ " 		  CASE WHEN A.ITEM_CLASSIFICATION='1' THEN '门诊' "
				+ " WHEN A.ITEM_CLASSIFICATION='2' THEN '门特' "
				+ " WHEN A.ITEM_CLASSIFICATION='3' THEN '住院' END AS ITEM_CLASSIFICATION, "
				+ " A.MODIFY_PROJECT_REASON, "
				+ " CASE WHEN A.ISVERIFY='1' THEN '审核中' "
				+ " WHEN A.ISVERIFY='2' THEN '审核通过' "
				+ " WHEN A.ISVERIFY='3' THEN '审核未通过' "
				+ " WHEN A.ISVERIFY='4' THEN '未备案' END AS ISVERIFY, "
				+ " A.AUDIT_OPINION, "
				+ " CASE WHEN A.UPDATE_FLG='1' THEN '未上传' "
				+ " WHEN A.UPDATE_FLG='2' THEN '已上传' "
				+ " WHEN A.UPDATE_FLG='3' THEN '取消上传' END UPDATE_FLG "
				+ " 		  FROM JAVAHIS.INS_REGISTERITEM A "
				+ " WHERE A.DEL_FLG='N' ";
		String nhi_code=this.getValueString("NHI_CODE").trim();
		if (!StringUtil.isNullString(nhi_code))
			Sql = Sql + " AND A.NHI_CODE='" + nhi_code + "' ";
		Sql = Sql + " ORDER BY A.NHI_CODE ";
		System.out.println("regSql===" + Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
//		RegisterItemTable
//				.setHeader("选,30,boolean;收费项目编码,80,NHI_CODE;收费项目名称,120,NHI_DESC;应用范围,80,ADM_TYPE;类别,80;修改项目及原因,80;审核状态,80;审核意见,80,上传状态,80");
//		RegisterItemTable
//				.setParmMap("NHI_CODE;NHI_DESC;ADM_TYPE;");
//		RegisterItemTable.setItem("NHI_CODE;NHI_DESC;ADM_TYPE");
//		RegisterItemTable
//				.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;");

		RegisterItemTable.setParmValue(tabParm);
	}

	/**
	 * 取得当前选中也签号
	 */
	public int getChangeTab() {
//		System.out.println("tabbedPane==="+tabbedPane.getSelectedIndex());
		return tabbedPane.getSelectedIndex();
	}
	/**
	 * 需备案目录信息下载
	 */
	public void onNeedRegisterItemDown() {
		TParm parm = new TParm();
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.addData("PARM_COUNT", 1);
		System.out.println("onNeedRegisterItemDown:"+parm);
		TParm splitParm = INSTJTool.getInstance().DataDown_zjkd_L(parm);
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			this.messageBox("需备案目录信息下载失败\n"+splitParm.getErrText());
			return;
		}
		splitParm.setData("OPT_USER", Operator.getID());
		splitParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction", "onSaveInsNeedRegisterItem", splitParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}else{
			this.messageBox("需备案目录信息下载成功");
		}
	}

	/**
	 * 诊疗项目备案信息下载
	 */
	public void onRegisterItemDown() {
		if(!onIsNull()){
			return;
		}
		TParm parm = new TParm();
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.addData("TYPE", getValueString("CATEGORY")); // 类别
		parm.addData("NHI_TYPE", getValueString("ITEM_CLASSIFICATION")); // 项目类别
		parm.addData("PARM_COUNT", 3);
		System.out.println("onRegisterItemDown:"+parm);
		TParm splitParm = INSTJTool.getInstance().DataDown_zjkd_J(parm);
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			this.messageBox("诊疗项目备案信息下载失败\n"+splitParm.getErrText());
			return;
		}
		splitParm.setData("OPT_USER", Operator.getID());
		splitParm.setData("OPT_TERM", Operator.getIP());
		splitParm.setData("CATEGORY", parm.getValue("TYPE",0));
		splitParm.setData("ITEM_CLASSIFICATION", parm.getValue("NHI_TYPE",0));
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction", "onUpdateInsRegisterItem", splitParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}else{
			this.messageBox("诊疗项目备案信息下载成功");
		}
	}
	/**
	 * 诊疗项目备案信息取消
	 */
	public void onRegisterItemCancel() {
		if (this.messageBox("信息", "取消被选中的备案记录,是否继续", 2) == 1) {
			return;
		}
		TParm tableParm = null;
		TParm parm = null;
		TParm newParm = new TParm(); // 累计数据
		RegisterItemTable.acceptText();
		TParm parmValue = RegisterItemTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			if(tableParm.getValue("CHOOSE").equals("N")){
				continue;
			}
			parm = new TParm();
			parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
			parm.addData("NHI_CODE", tableParm.getValue("NHI_CODE")); // 项目代码
			parm.addData("NHI_TYPE", tableParm.getValue("ADM_TYPE")); // 应用范围
			parm.addData("PARM_COUNT", 3);
			System.out.println("onRegisterItemCancel:"+parm);
			TParm splitParm = INSTJTool.getInstance().DataDown_zjks_T(parm);
			if (!INSTJTool.getInstance().getErrParm(splitParm)) {
				this.messageBox(tableParm.getValue("NHI_DESC") +tableParm.getValue("ADM_TYPE")+
						"取消失败\n"+splitParm.getErrText());
				continue;
			}
			newParm.addData("NHI_CODE", tableParm.getValue("NHI_CODE"));
			newParm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE"));
		}
		System.out.println("onRegisterItemCancel:"+newParm);
       if(newParm.getCount("NHI_CODE")<=0){
    	   return;
       }
		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_TERM", Operator.getIP());
		newParm.setData("ISVERIFY", "4");
		newParm.setData("UPDATE_FLG", "3");
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction","onCancelInsRegisterItem", newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}else{
			this.messageBox("取消成功");
		}
	}
	/**
	 * 诊疗项目备案信息删除
	 */
	public void onRegisterItemDelete() {
//		System.out.println(this.messageBox("信息", "删除被选中的备案记录,是否继续", 2));
		if (this.messageBox("信息", "删除被选中的备案记录,是否继续", 2) == 1) {
			return;
		}
		TParm tableParm = null;
		TParm newParm = new TParm(); // 累计数据
		String str = "";
		RegisterItemTable.acceptText();
		TParm parmValue = RegisterItemTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
//			System.out.println("CHOOSE:"+tableParm.getValue("CHOOSE"));
			if (tableParm.getValue("CHOOSE").equals("N")) {
				continue;
			}
			str=this.getIsverify(tableParm.getValue("NHI_CODE"));
//			System.out.println("str:"+str);
			if (str.equals("1")|| str.equals("2")) {
				this.messageBox(parmValue.getValue("NHI_DESC", i)
						+ "已经审核或审核中不能删除");
				continue;
			}
			newParm.addData("NHI_CODE", tableParm.getValue("NHI_CODE"));
			newParm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE"));
		}

		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_TERM", Operator.getIP());
		System.out.println("onDeleteInsRegisterItem:"+newParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction", "onDeleteInsRegisterItem",
				newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
	}
	/**
	 * 诊疗项目备案信息新增
	 */
	public void onRegisterItemInsert() {
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INS_RegiterItem.x", null);
	}
	/**
	 * 诊疗项目备案信息修改
	 */
	public void onRegisterItemUpdate() {
		TParm parm=new TParm();
		int row=RegisterItemTable.getSelectedRow();
		TParm tableParm=RegisterItemTable.getParmValue();
//		parm.setData("NHI_CODE", tableParm.getRow(row).getValue("NHI_CODE"));
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INS_RegiterItem.x", tableParm.getRow(row));
	}
	/**
	 * 空值检查
	 */
	public boolean onIsNull() {
		if(getValueString("CATEGORY").equals("")){
			this.messageBox("请选择类别");
			return false;
		}
		if(getValueString("ITEM_CLASSIFICATION").equals("")){
			this.messageBox("请选择项目类别");
			return false;
		}
		return true;
	}

	/**
	 * 返回审核状态
	 */
	public String getIsverify(String nhi_code) {
		String Sql = " SELECT A.ISVERIFY "
				+ " 		  FROM JAVAHIS.INS_REGISTERITEM A "
				+ " WHERE A.NHI_CODE='" + nhi_code + "' " + " AND DEL_FLG='N' ";
		// System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("ISVERIFY") < 0) {
			this.messageBox("项目不存在");
			return "";
		}
		return tabParm.getValue("ISVERIFY",0);
	}

	/**
	 * 诊疗项目备案信息上传
	 */
	public void onRegisterItemUp() {
		TParm tableParm = null;
		TParm newParm = new TParm(); // 累计数据
		RegisterItemTable.acceptText();
		boolean flg=true;
		TParm parmValue = RegisterItemTable.getParmValue();
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
//			System.out.println("CHOOSE:"+tableParm.getValue("CHOOSE"));
			if (tableParm.getValue("CHOOSE").equals("N")) {
				continue;
			}
			TParm parm = onGetUpload(onQuery(tableParm.getValue("NHI_CODE"),tableParm.getValue("ADM_TYPE")));
//			System.out.println("onRegisterItemUp:" + parm);
			TParm splitParm = INSTJTool.getInstance().DataDown_zjks_S(parm);
			if (!INSTJTool.getInstance().getErrParm(splitParm)) {
				this.messageBox(parmValue.getValue("NHI_DESC", i) +"\n"+splitParm.getErrText()+ "\n上传失败");
				flg=false;
				continue;
			}
			newParm.addData("NHI_CODE", tableParm.getValue("NHI_CODE"));
			newParm.addData("ADM_TYPE", tableParm.getValue("ADM_TYPE"));
		}

	       if(newParm.getCount("NHI_CODE")<=0){
	    	   return;
	       }
			newParm.setData("OPT_USER", Operator.getID());
			newParm.setData("OPT_TERM", Operator.getIP());
			newParm.setData("ISVERIFY", "1");
			newParm.setData("UPDATE_FLG", "2");
			TParm result = TIOM_AppServer.executeAction(
					"action.ins.INS_RegiterAction","onCancelInsRegisterItem", newParm);
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}else{
			if(flg){
				this.messageBox("上传成功");
			}
			}
	}

	/**
	 * 查询备案信息
	 * 
	 * @return TParm
	 */
	private TParm onQuery(String nhi_code,String nhi_type) {
		String Sql = "SELECT A.NHI_CODE,A.NHI_DESC,A.ADM_TYPE AS NHI_TYPE,A.MINISTRY_HEALTHNO AS FILE_NO,A.CONNOTATION_PROJECT AS ITEM_DESC, "
				+ " A.UNIT,A.OWT_AMT AS PRICE,A.ICD_DESC_LIST AS ICD_DESC,A.ICD_CODE_LIST AS ICD_CODE,A.CLINICAL_SIGNIFICANCE AS CLINICAL_DESC, "
				+ " A.DEPT_CODE,A.APPARATUS AS DEVICE,A.REMARK,A.OUTSIDE_FLG AS OUTEXM_FLG,A.OUTSIDE_HOSP_CODE AS OUTEXM_HOSP_NO, "
				+ " A.SPECIAL_CASE AS SPECIAL_DESC,A.REAGENT AS DRUG,A.MEDICAL_MATERIALS AS MATERIAL,A.CATEGORY, "
				+ " A.MODIFY_PROJECT_REASON,A.ISVERIFY,A.AUDIT_OPINION,A.UPDATE_FLG,A.ITEM_CLASSIFICATION "
				+ " FROM JAVAHIS.INS_REGISTERITEM A "
				+ " WHERE A.NHI_CODE='"
				+ nhi_code + "' " + " AND A.DEL_FLG='N' AND A.ADM_TYPE='"+nhi_type+"' ";
//		 System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("没有查询到相应记录");
			return null;
		}
		return tabParm;
	}

	private TParm onGetUpload(TParm tabParm) {
		TParm parm = new TParm();
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.addData("NHI_CODE", tabParm.getValue("NHI_CODE", 0)); // 收费项目编码
		parm.addData("NHI_DESC", tabParm.getValue("NHI_DESC", 0)); // 收费项目名称
		parm.addData("NHI_TYPE", tabParm.getValue("NHI_TYPE", 0)); // 应用范围
		parm.addData("FILE_NO", tabParm.getValue("FILE_NO", 0)); // 卫生物价部门颁布的文件依据（文号）
		parm.addData("ITEM_DESC", tabParm.getValue("ITEM_DESC", 0)); // 项目内涵
		parm.addData("UNIT", tabParm.getValue("UNIT", 0)); // 收费单位
		parm.addData("PRICE", tabParm.getValue("PRICE", 0)); // 收费标准
		parm.addData("ICD_DESC", tabParm.getValue("ICD_DESC", 0)); // 临床适应症
		parm.addData("ICD_CODE", tabParm.getValue("ICD_CODE", 0)); // 临床适应症ICD编码
		parm.addData("CLINICAL_DESC", tabParm.getValue("CLINICAL_DESC", 0)); // 临床意义
		parm.addData("DEPT_CODE", tabParm.getValue("DEPT_CODE", 0)); // 临床使用科室
		parm.addData("DEVICE", tabParm.getValue("DEVICE", 0)); // 使用的仪器设备
		parm.addData("REMARK", tabParm.getValue("REMARK", 0)); // 备注
		parm.addData("OUTEXM_FLG", tabParm.getValue("OUTEXM_FLG", 0)); // 外检标志
		parm.addData("OUTEXM_HOSP_NO", tabParm.getValue("OUTEXM_HOSP_NO", 0)); // 外检医院编码
		parm.addData("SPECIAL_DESC", tabParm.getValue("SPECIAL_DESC", 0)); // 特殊情况说明
		parm.addData("DRUG", tabParm.getValue("DRUG", 0)); // 试剂
		parm.addData("MATERIAL", tabParm.getValue("MATERIAL", 0)); // 医用材料
		parm.addData("PARM_COUNT", 19);
		return parm;
	}
	//获得sys_fee数据
	public void GetOrderInf(String smrj) {
		 String now = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd"); //拿到当前的时间

		String py1 ="";
		if(!smrj.equals(""))
		py1 =smrj.toUpperCase();	
		String sql = 
		"SELECT A.ORDER_CODE,A.ORDER_DESC,A.NHI_CODE_I,A.NHI_CODE_O,A.NHI_CODE_E, " +
		"A.NHI_FEE_DESC,A.NHI_PRICE,A.OWN_PRICE,C.DOSE_CHN_DESC, " +
		"A.SPECIFICATION,A.HYGIENE_TRADE_CODE,A.MAN_CODE " +
	    " FROM SYS_FEE A LEFT JOIN PHA_BASE B " +
	    " ON A.ORDER_CODE = B.ORDER_CODE" +
	    " LEFT JOIN PHA_DOSE C ON B.DOSE_CODE = C.DOSE_CODE " +
	    " WHERE A.PY1 LIKE '%" + py1 + "%' " +
	    " AND A.OWN_PRICE !=0 "+
	    " AND A.ORDER_CAT1_CODE NOT LIKE '%PHA%' ";	
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("查询数据有问题");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			result.setData("DATE",i,now);	
			
		}
		TABLE_FEE.setParmValue(result);
	}
}
