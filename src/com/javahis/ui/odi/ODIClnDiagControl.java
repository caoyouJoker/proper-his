package com.javahis.ui.odi;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;
import com.javahis.util.OdiUtil;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMXMLTool;
import jdo.clp.BscInfoTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * 
 * <p>
 * Title: 诊断
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.9.16
 * @version 1.0
 */

public class ODIClnDiagControl extends TControl {

	private TParm parm;
	// 科室代码
	private String dept_code;
	// 医师代码
	private String dr_code;

	private String code;
	// 就诊序号
	private String onlyCaseNo;
	// 性别
	private String sexCode;
	// 年龄
	private Timestamp birthday;
	// 入院时间
	private Timestamp inDate;
	// 诊断类型
	private String ioType;
	
	TParm delparm = new TParm();

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 取得传入参数
		Object obj = this.getParameter();
		String case_no = "";
		if (obj != null) {
			parm = (TParm) obj;
			case_no = parm.getValue("CASE_NO");
			onlyCaseNo = case_no;
			sexCode = parm.getValue("SEX_CODE");
			birthday = parm.getTimestamp("BIRTHDAY");
			inDate = parm.getTimestamp("IN_DATE");
			// 科室代码
			dept_code = parm.getValue("DEPT_CODE");
			// 医师代码
			dr_code = parm.getValue("DR_CODE");
		}
		// 中西医诊断过滤
		onChangeICD_TYPE();
		// 初始化按钮状态
		//callFunction("UI|delete|setEnabled", false);
		callFunction("UI|card|setEnabled", false);
		// 注册激发SYSICDPopup弹出的事件
		getTable("CLNDIAG_TABLE").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponentUD");
		getTable("CLNDIAG_TABLE").addEventListener(
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onClnDiagTableCheckBoxClicked");
		// 添加侦听事件
		addEventListener("CLNDIAG_TABLE->" + TTableEvent.CHANGE_VALUE,
				"onTableDChangeValue");
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		TFrame f = ((TFrame) this.getComponent("UI"));
		if (table.getRowCount() >= 4) {
			int height = 496 + (table.getRowCount() * 10);
			f.setHeight(height);
			f.repaint();
		}
	}

	/**
	 * 保存方法
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		// 检查ICD_CODE是否重复
		Map map = new HashMap();
		String value = "";
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			value = (String) table.getItemString(i, "IO_TYPE") + ":"
					+ (String) table.getItemString(i, "ICD_CODE");
			if (map.containsValue(value)) {
				// 同一诊断类别存在相同诊断
				this.messageBox("E0041");
				return false;
			}
			map.put(i, value);
		}
		//=======检查是否已经医生提交或者病案室提交 
        String check = // wanglong add 20140416
                "SELECT * FROM MRO_RECORD WHERE CASE_NO='#' AND NVL(DIAGCHK_FLG,'N')='N' AND NVL(QTYCHK_FLG,'N')='N'";
        check = check.replaceFirst("#", parm.getValue("CASE_NO"));
        TParm checkParm = new TParm(TJDODBTool.getInstance().select(check));
        if (checkParm.getErrCode() < 0) {
            this.messageBox(checkParm.getErrText());
            return false;
        }
        if (checkParm.getCount() < 1) {
            this.messageBox("操作无法完成，请先取消医生提交或者病案室提交");
            return false;
        }
		// 判断是否存在主诊断
		String io_type = "";
		String main_flg = "";
		String icd_code = "";
		Map main_I = new HashMap();
		Map main_M = new HashMap();
		Map main_O = new HashMap();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			io_type = (String) table.getItemString(i, "IO_TYPE");
            if (StringUtil.isNullString(io_type)) {
                this.messageBox("请填写诊断类别");
                return false;//add by wanglong 20140404
            }
			main_flg = (String) table.getItemString(i, "MAINDIAG_FLG");
			icd_code = (String) table.getItemString(i, "ICD_CODE");
			if ("I".equals(io_type))
				main_I.put(main_flg, icd_code);
			if ("M".equals(io_type))
				main_M.put(main_flg, icd_code);
			if ("O".equals(io_type))
				main_O.put(main_flg, icd_code);
		}
		if (!main_I.isEmpty()) {
			if (!main_I.containsKey("Y")) {
				// 门急诊诊断不存在主诊断
				this.messageBox("门急诊诊断不存在主诊断");
				//onInit();
				delparm.removeData("CASE_NO");
				delparm.removeData("SEQ_NO");
				return false;
			}
		}
		if (!main_M.isEmpty()) {
			if (!main_M.containsKey("Y")) {
				// 住院诊断不存在主诊断
				this.messageBox("住院诊断不存在主诊断");
				//onInit();
				delparm.removeData("CASE_NO");
				delparm.removeData("SEQ_NO");
				return false;
			}
		}
		if (!main_O.isEmpty()) {
			if (!main_O.containsKey("Y")) {
				// 出院诊断不存在主诊断
				this.messageBox("出院诊断不存在主诊断");
//				//删除主诊断后不允许保存，解锁最后一行表格 duzhw add 20131018
//				StringBuffer lockRow = new StringBuffer();
//				for(int j = 0; j < table.getRowCount()-1; j++){
//					if(j==0){
//						lockRow.append(Integer.toString(j));
//					}else{
//						lockRow.append(",").append(Integer.toString(j));
//					}
//				}
//				//System.out.println("lockRow="+lockRow.toString());
//				table.setLockRows(lockRow.toString());//锁定行，不允许修改
				//onInit();
				delparm.removeData("CASE_NO");
				delparm.removeData("SEQ_NO");
				
				return false;
			}
		}
		String ticd_type = "";
		if (getRadioButton("RadioButton1").isSelected())
			ticd_type = "W";
		else
			ticd_type = "C";

		// 保存数据
		TParm update = new TParm();
		// CASE_NO：病患case_no
		update.setData("CASE_NO", parm.getValue("CASE_NO"));
		TParm result = new TParm();
		int Outcount = 2;
		int Oecount = 2;
		int incount = 2;
		int count = 0;
		
		//获取病患初始化诊断信息result_old --duzhw add 20131013
//		TParm result_old = getAdm_InpDiag(parm.getValue("CASE_NO"));
		
		//获取保存下（新）诊断信息 result
		// 诊断ICD替换中文
		OrderList orderDesc = new OrderList();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			result.addData("CASE_NO", parm.getValue("CASE_NO"));// CASE_NO必须参数
			result.addData("MR_NO", parm.getValue("MR_NO"));// MR_NO必须参数
			result.addData("IPD_NO", parm.getValue("IPD_NO"));// IPD_NO必须参数
			result.addData("IO_TYPE", table.getItemString(i, "IO_TYPE"));// 类型
			result.addData("ICD_KIND", ticd_type);// 诊断编码
			result.addData("MAIN_FLG", "Y".equals(table.getItemString(i,
					"MAINDIAG_FLG")) ? table.getItemString(i, "MAINDIAG_FLG")
					: "N");// 主
			result.addData("ICD_CODE", table.getItemString(i, "ICD_CODE"));// 诊断编码
			result.addData("SEQ_NO", count);// 序号
			result.addData("ICD_DESC", orderDesc.getTableShowValue(table
					.getItemString(i, "ICD_CODE")));// 诊断名称
			result.addData("ICD_DATE", table.getItemTimestamp(i, "ICD_DATE"));// 诊断时间
			result.addData("ICD_REMARK", table.getItemString(i, "DESCRIPTION"));// 备注
			result.addData("DESCRIPTION", table.getItemString(i, "DESCRIPTION"));// 备注
			// wanglong add 20140416
			result.addData("STATUS", table.getItemString(i, "STATUS"));// 状态
			result.addData("ICD_STATUS", table.getItemString(i, "ICD_STATUS"));// 转归
			result.addData("IN_PAT_CONDITION", table.getItemString(i, "IN_PAT_CONDITION"));// 入院病情
			result.addData("ADDITIONAL_CODE", table.getItemString(i, "ADDITIONAL_CODE"));// 附加码
			result.addData("ADDITIONAL_DESC", table.getItemString(i, "ADDITIONAL_DESC"));// 附加诊断名称
			//add end
			result.addData("OPT_USER", Operator.getID());
			result.addData("OPT_TERM", Operator.getIP());
			result.addData("EXEC", "Y".equals(table.getItemString(i,
					"EXEC")) ? table.getItemString(i,"EXEC") 
					: "N");//判断标识->Y：老数据（不处理） N：新数据（插入） --duzhw add 20131010
			count++;
		}
		
		//筛选出新增数据：即EXEC=='N'的数据  result2为当前新增数据
		int insertCount = 0;
		TParm result2 = new TParm();
		//TParm old_parm = new TParm();
		//String seq_sql = " select seq_no.nextVal from dual ";
		String new_seq_no = getMaxSeqNo(result.getValue("CASE_NO", 0));
		int seqCount = Integer.parseInt(new_seq_no);
		for (int k = 0; k < result.getCount("CASE_NO"); k++){
			String exec = result.getValue("EXEC", k);
			//System.out.println("k="+k+" exec="+exec);
			if("N".equals(exec)){
				//TParm seq_parm = new TParm(TJDODBTool.getInstance().select(seq_sql));
				//new_seq_no = seq_parm.getValue("NEXTVAL",0);
				String icdCode = result.getValue("ICD_CODE", k);
				result.setData("SEQ_NO", k, seqCount);
				result2.addRowData(result, k);
				seqCount++;
				//System.out.println("新组装的parm[result2]="+result2);
			}
//			else{//之前的老数据 - 用于比较  不做任何处理 
//				old_parm.addRowData(result, k);
//			}
			
		}
		
		//循环比较目前老数据old_parm 和初始化老数据 result_old 差异的部分为删除的数据
//		TParm del_parm = new TParm();
//		boolean exit = false;
//		for (int j = 0; j < result_old.getCount("CASE_NO"); j++) {
//			String caseNo1 = result_old.getValue("CASE_NO", j);
//			//String ioType1 = result_old.getValue("IO_TYPE", j);
//			String icdCode1 = result_old.getValue("ICD_CODE", j);
//			String mainDiagFlg1 = result_old.getValue("MAINDIAG_FLG", j);
//			System.out.println("caseNo1="+caseNo1+" icdCode1="+icdCode1+" mainDiagFlg1="+mainDiagFlg1);
//			for (int j2 = 0; j2 < old_parm.getCount("CASE_NO"); j2++) {
//				String caseNo2 = old_parm.getValue("CASE_NO", j2);
//				//String ioType2 = result_old.getValue("IO_TYPE", j2);
//				String icdCode2 = old_parm.getValue("ICD_CODE", j2);
//				String mainDiagFlg2 = old_parm.getValue("MAIN_FLG", j2);
//				System.out.println("caseNo2="+caseNo2+" icdCode2="+icdCode2+" mainDiagFlg2="+mainDiagFlg2);
//				if(caseNo1.equals(caseNo2) && icdCode1.equals(icdCode2) && mainDiagFlg1.equals(mainDiagFlg2)){
//					//System.out.println("相同的数据为保留下来的老数据，不做任何处理！");
//					exit = true;
//				}
//			}
//			if(!exit){//该条在新数据中不存在-即为删除数据
//				del_parm.addRowData(result_old, j);//把准备删除的数据放到del_parm中
//			}
//			exit = false;//置回初始状态
//		}
		//操作处理-新增数据：result2  删除数据：del_parm
	
		//System.out.println("新增数据 result2="+result2);
		
		// 最新诊断
		String maindiag = "";
		if (!main_I.isEmpty()) {
			maindiag = main_I.get("Y").toString();
		}
		if (!main_M.isEmpty()) {
			maindiag = main_M.get("Y").toString();
		}
		if (!main_O.isEmpty()) {
			maindiag = main_O.get("Y").toString();
		}
		// MAINDIAG
		update.setData("MAINDIAG", maindiag);
		// OPT_USER
		update.setData("OPT_USER", Operator.getID());
		// OPT_TERM
		update.setData("OPT_TERM", Operator.getIP());
		String caseNo = onlyCaseNo;
		parm.setData("CASE_NO", caseNo);
		//
		update.setData("CASE_NO", caseNo);
		TParm clpPathParm = ADMInpTool.getInstance().selectall(parm);
		// System.out.println("临床路径信息"+clpPathParm);
		// int clpCount = clpPathParm.getCount("CLNCPATH_CODE");
		// 入院主诊断
		String oldMDiagCode = "";
		// 出院主诊断
		String oldODiagCode = "";
		// 临床路径是否存在诊断
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			if ("M".equals(table.getItemString(i, "IO_TYPE"))
					&& "Y".equals(table.getItemString(i, "MAINDIAG_FLG")))
				oldMDiagCode = table.getItemString(i, "ICD_CODE");
			if ("O".equals(table.getItemString(i, "IO_TYPE"))
					&& "Y".equals(table.getItemString(i, "MAINDIAG_FLG")))
				oldODiagCode = table.getItemString(i, "ICD_CODE");
		}
		String clpBscInfoSql = "";
		String clpBscInfoDesc = "";
		TParm clpBscInfoParm = new TParm();
		// 是否存在临床路径
		if (clpPathParm.getData("CLNCPATH_CODE", 0) != null
				&& clpPathParm.getValue("CLNCPATH_CODE", 0).length() > 0) {
			// System.out.println("存在临床路径》》》》》》》》》》》》》");
			clpBscInfoSql = " SELECT CLNCPATH_CHN_DESC FROM CLP_BSCINFO "
					+ "  WHERE CLNCPATH_CODE = '"
					+ clpPathParm.getValue("CLNCPATH_CODE", 0) + "' ";
			clpBscInfoParm = new TParm(TJDODBTool.getInstance().select(
					clpBscInfoSql));
			clpBscInfoDesc = clpBscInfoParm.getValue("CLNCPATH_CHN_DESC", 0);
			TParm inBscParm = new TParm();
			inBscParm.setData("CLNCPATH_CODE", clpPathParm.getValue(
					"CLNCPATH_CODE", 0));
			TParm bscParm = BscInfoTool.getInstance().existBscinfo(inBscParm);
			if (bscParm.getCount("ICD_CODE") > 0) {
				// System.out.println("存在临床路径ICD"+bscParm);
				if (oldMDiagCode.length() > 0) {
					// System.out.println("入院主诊断"+oldMDiagCode);
					// IO_TYPE;MAINDIAG_FLG
					if (!oldMDiagCode.equals(bscParm.getValue("ICD_CODE", 0))) {
						// System.out.println("1111111111111111111111");
						this.messageBox("入院主诊断,不符合" + clpBscInfoDesc
								+ "临床路径,建议溢出");
					}
				}
				if (oldODiagCode.length() > 0) {
					// System.out.println("出院主诊断"+oldODiagCode);
					// IO_TYPE;MAINDIAG_FLG
					if (!oldODiagCode.equals(bscParm.getValue("ICD_CODE", 0))) {
						// System.out.println("22222222222222222222");
						this.messageBox("出院主诊断,不符合" + clpBscInfoDesc
								+ "临床路径,建议溢出");
					}
				}

			}
		} else {
			// System.out.println("没有临床路径》》》》》》》》》》》》》");
			if (oldMDiagCode.length() > 0) {
				// System.out.println("入院主诊断");
				TParm inBscParmNew = new TParm();
				inBscParmNew.setData("ICD_CODE", oldMDiagCode);
				TParm bscParmNew = BscInfoTool.getInstance().existBscinfo(
						inBscParmNew);
				// System.out.println("入新数据"+bscParmNew);
				if (bscParmNew.getData("CLNCPATH_CODE") != null
						&& bscParmNew.getValue("CLNCPATH_CODE", 0).length() > 0)
					this.messageBox("建议进入"
							+ bscParmNew.getValue("CLNCPATH_CODE", 0) + "临床路径");
			}
			if (oldODiagCode.length() > 0) {
				// System.out.println("出院主诊断");
				TParm inBscParmNew = new TParm();
				inBscParmNew.setData("ICD_CODE", oldODiagCode);
				TParm bscParmNew = BscInfoTool.getInstance().existBscinfo(
						inBscParmNew);
				// System.out.println("出新数据"+bscParmNew);
				if (bscParmNew.getData("CLNCPATH_CODE") != null
						&& bscParmNew.getValue("CLNCPATH_CODE", 0).length() > 0)
					this.messageBox("建议进入"
							+ bscParmNew.getValue("CLNCPATH_CODE", 0) + "临床路径");
			}
		}
		update.setData("DIAGDATA", result2.getData());
		update.setData("DELDATA", delparm.getData()); // duzhw add by 20131016
		result2 = TIOM_AppServer.executeAction("action.odi.ODIClnDiagAction",
				"onSave", update);
		if (result2.getErrCode() < 0) {
			this.messageBox("E0001" + result2.getErrName()
							+ result2.getErrText());
			return false;
		}
		this.messageBox("P0001");
		// 判断是否有传染病
		Set setCHLR = new HashSet();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			String icd_type = "W";
			if (!getRadioButton("RadioButton1").isSelected())
				icd_type = "C";
			icd_code = (String) table.getValueAt(i, 2);
			String sql = "SELECT CHLR_FLG FROM SYS_DIAGNOSIS";
			sql += " WHERE ICD_TYPE = '" + icd_type + "' AND ICD_CODE = '"
					+ icd_code + "'";
			TDataStore ds = new TDataStore();
			ds.setSQL(sql);
			ds.retrieve();
			String chlr_flg = ds.getItemString(0, "CHLR_FLG");
			if ("Y".equals(chlr_flg))
				setCHLR.add(icd_code);
		}
		// 调用传染病报告卡
		if (setCHLR.size() > 0) {
			Iterator iterator = setCHLR.iterator();
			while (iterator.hasNext()) {
				onTransferMROInfect(parm.getValue("MR_NO"), parm
						.getValue("CASE_NO"), TypeTool.getString(iterator
						.next()));
			}
		}
		
		// add by wangb 2015/10/15 医生开立诊断时向大屏发送消息 START
		// 床旁接口 
        TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
        if (xmlParm.getErrCode() < 0) {
            this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
        }
		// add by wangb 2015/10/15 医生开立诊断时向大屏发送消息 END
		
		//清除delparm duzhw add 20131021
		delparm.removeData("CASE_NO");
		delparm.removeData("SEQ_NO");
		
		onInit();
		return true;
	}

	/**
	 * 取MRO_RECORD_DIAG表和ADM_INPDIAG表中最大的seq_no加1 duzhw 20131129
	 * @return
	 */
	public String getMaxSeqNo(String caseNo){
		int seqNo1 = 0;
		int seqNo2 = 0;
		int maxSeqNo = 0;
		String seqNoStr1 = "";
		String seqNoStr2 = "";
		boolean flag = false;
		String sql1 = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM ADM_INPDIAG WHERE CASE_NO = '"+caseNo+"'";
		String sql2 = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM MRO_RECORD_DIAG WHERE CASE_NO = '"+caseNo+"'";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		seqNoStr1 = result1.getValue("SEQ_NO", 0);
		seqNoStr2 = result2.getValue("SEQ_NO", 0);
		if(!"".equals(seqNoStr1) && seqNoStr1!=null){
			seqNo1 = Integer.parseInt(seqNoStr1);
		}
		if(!"".equals(seqNoStr2) || seqNoStr2!=null){
			seqNo2 = Integer.parseInt(seqNoStr2);
		}
		//判断比较
		if(seqNo1 >= seqNo2){
			maxSeqNo = seqNo1;
		}else {
			maxSeqNo = seqNo2;
		}
		return  Integer.toString(maxSeqNo+1);
		
	}

	/**
	 * 模糊查询（内部类）
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		TTable table = getTable("CLNDIAG_TABLE");
		//update by duzhw 20131011（删除操作不走页面级再点保存，需要走后台）
		int row = table.getTable().getSelectedRow();
		if(row < 0){
			this.messageBox("请选中要删除的数据！");
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		String seqNo = table.getItemString(row, "SEQ_NO");
		//System.out.println("准备删除的caseNo="+caseNo+" seqNo="+seqNo);
		delparm.addData("CASE_NO", caseNo);
		delparm.addData("SEQ_NO", seqNo);
		//System.out.println("delparm="+delparm);
//		
//		TParm delete = new TParm();
//		TParm result = new TParm();
//		result.setData("CASE_NO", caseNo);
//		result.setData("SEQ_NO", seqNo);
//		delete.setData("DIAGDATA", result.getData());
//		result = TIOM_AppServer.executeAction("action.odi.ODIClnDiagAction",
//				"onDelete", delete);
//		if (result.getErrCode() < 0) {
//			this.messageBox("E0001" + result.getErrName()
//							+ result.getErrText());
//		}else{
//			this.messageBox("删除成功！");
//		}
		
		table.removeRow(row);
		lockRow();//锁定老数据
	}

	/**
	 * 锁定行方法duzhw 20131210
	 */
	public void lockRow(){
		TTable table = getTable("CLNDIAG_TABLE");
		StringBuffer lockRow = new StringBuffer();
		for(int i = 0; i < table.getRowCount()-1; i++){
			String seqNo = table.getItemString(i, "SEQ_NO");
			String exec = table.getItemString(i, "EXEC");
			if(!seqNo.equals("")||exec.equals("Y")){
				if(i==0){
					lockRow.append(Integer.toString(i));
				}else{
					lockRow.append(",").append(Integer.toString(i));
				}
			}
		}
		table.setLockRows(lockRow.toString());//锁定行，不允许修改
	}

	/**
	 * 传染病报告卡
	 */
	public void onCard() {
		TTable table = getTable("CLNDIAG_TABLE");
		int row = table.getTable().getSelectedRow();
		String icd_type = "W";
		if (!getRadioButton("RadioButton1").isSelected())
			icd_type = "C";
		String icd_code = (String) table.getValueAt(row, 2);
		String sql = "SELECT CHLR_FLG FROM SYS_DIAGNOSIS";
		sql += " WHERE ICD_TYPE = '" + icd_type + "' AND ICD_CODE = '"
				+ icd_code + "'";
		TDataStore ds = new TDataStore();
		ds.setSQL(sql);
		ds.retrieve();
		String chlr_flg = ds.getItemString(0, "CHLR_FLG");
		if ("Y".equals(chlr_flg)) {
			onTransferMROInfect(parm.getValue("MR_NO"), parm
					.getValue("CASE_NO"), icd_code);
		} else {
			// 此诊断不是传染病
			this.messageBox("此诊断不是传染病");
		}
	}

	/**
	 * 调用传染病报告卡
	 * 
	 * @param mr_no
	 *            String
	 * @param case_no
	 *            String
	 * @param icd_code
	 *            String
	 */
	public void onTransferMROInfect(String mr_no, String case_no,
			String icd_code) {
		TParm can = new TParm();
		can.setData("MR_NO", mr_no);
		can.setData("CASE_NO", case_no);
		can.setData("ICD_CODE", icd_code);
		can.setData("DEPT_CODE", Operator.getDept());
		can.setData("USER_NAME", Operator.getName());
		this.openDialog("%ROOT%/config/mro/MROInfect.x", can);
	}

	/**
	 * 表格(CLNDIAG_TABLE)单击事件
	 */
	public void onTableClicked() {
		String rule_type = parm.getValue("RULE_TYPE");
		TTable table = getTable("CLNDIAG_TABLE");
		int row = table.getSelectedRow();
		String io_type = (String) table.getValueAt(row, 0);
		// 根据区分调用接口，设置删除按钮的状态
		if (rule_type.equals("I")) {
			if (io_type.equals("M") || io_type.equals("O"))
				callFunction("UI|delete|setEnabled", true);
			else
				callFunction("UI|delete|setEnabled", false);
		} else {
			if (io_type.equals("I"))
				callFunction("UI|delete|setEnabled", true);
			else
				callFunction("UI|delete|setEnabled", false);
		}
		// 根据ICD_TYPE和ICD_CODE判断是否为传染病
		String icd_type = "W";
		if (!getRadioButton("RadioButton1").isSelected())
			icd_type = "C";
		String icd_code = (String) table.getValueAt(row, 2);
		String sql = "SELECT CHLR_FLG FROM SYS_DIAGNOSIS";
		sql += " WHERE ICD_TYPE = '" + icd_type + "' AND ICD_CODE = '"
				+ icd_code + "'";
		TDataStore ds = new TDataStore();
		ds.setSQL(sql);
		ds.retrieve();
		String chlr_flg = ds.getItemString(0, "CHLR_FLG");
		if ("Y".equals(chlr_flg))
			callFunction("UI|card|setEnabled", true);
		else
			callFunction("UI|card|setEnabled", false);
	}

	/**
	 * 诊断类型(RadioButton)改变事件
	 */
	public void onChangeICD_TYPE() {
		// 西医诊断为 true，中医诊断为 false
		boolean icd_type = getRadioButton("RadioButton1").isSelected();
		if (icd_type) {
			ioType = "ICD_TYPE = 'W'";
		} else {
			ioType = "ICD_TYPE = 'C'";
		}
		showAdm_InpDiag(onlyCaseNo);
		// 添加一行新数据
		insertRow();
		autoHeight();
	}

	/**
	 * 表格值改变事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableDChangeValue(Object obj) {
		// 值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 判断数据改变
		// if (node.getValue().equals(node.getOldValue()))
		// return false;
		// Table的列名
		TTable table = node.getTable();
		String columnName = table.getDataStoreColumnName(node.getColumn());
		int row = node.getRow();
		if ("IO_TYPE".equals(columnName)) {
			// 判断是否存在主诊断
			String io_type = (String) node.getValue();
			// System.out.println("====io_type====="+io_type);
			boolean flg = false;
			for (int i = 0; i < table.getRowCount(); i++) {
				if (i != row) {
					if (io_type.equals(table.getItemString(i, "IO_TYPE"))) {
						if ("Y".equals(table.getItemString(i, "MAINDIAG_FLG")))
							flg = true;
					}
				}
			}
			// System.out.println("====flg====="+flg);
			// 不存在主诊断
			if (!flg) {
				table.setItem(row, "MAINDIAG_FLG", "Y");
			} else {
				table.setItem(row, "MAINDIAG_FLG", "N");
			}

		}
		if ("MAINDIAG_FLG".equals(columnName)) {
			// 判断是否存在主诊断
			String mianFlg = (String) node.getValue();
			String ioType = table.getItemString(row, "IO_TYPE");
			boolean flg = false;
			for (int i = 0; i < table.getRowCount(); i++) {
				if (i != row) {
					if (mianFlg.equals(table.getItemString(i, "MAINDIAG_FLG"))) {
						if (ioType.equals(table.getItemString(i, "IO_TYPE")))
							flg = true;
					}
				}
			}
			// 不存在主诊断
			if (!flg) {
				table.setItem(row, "MAINDIAG_FLG", "Y");
			} else {
				this.messageBox("同一个诊断,只有一个主诊断");
				table.setItem(row, "MAINDIAG_FLG", "N");
			}

		}

		/**
		 * if ("Y".equals(table.getDataStore().getItemString(row,
		 * "MAINDIAG_FLG"))) { boolean flg = true; for (int i = 0; i
		 * < table
		 * .getRowCount(); i++) { if (i == row) continue; if
		 * ("Y".equals(table.getItemString(i, "MAINDIAG_FLG"))) flg = false; }
		 * if (!flg) { // 同一诊断类别，主诊断只有一项 this.messageBox("同一诊断类别，主诊断只有一项");
		 * 
		 * table.setItem(row, "MAINDIAG_FLG", "N"); return false; } } return
		 * false;
		 **/
		// }
		return false;
	}

	/**
	 * 当TABLE创建编辑控件时长期
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentUD(Component com, int row, int column) {// wanglong modify 20140416
		if (column != 3 && column != 9) {
			return;
		}
		if (!(com instanceof TTextField))
			return;
		String icd_type = "";
		if (getRadioButton("RadioButton1").isSelected())
			icd_type = "W";//西医诊断
		else
			icd_type = "C";//中医诊断
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", icd_type);
		if (icd_type.equals("W")) {
			parm.setData("ICD_EXCLUDE", "Y");
			parm.setData("ICD_MIN_EX", "M80000/0");
			parm.setData("ICD_MAX_EX", "M99890/1");
			parm.setData("ICD_START_EX", "V");
			parm.setData("ICD_END_EX", "Y");
		}
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// 定义接受返回值方法
        if (column == 3) textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                                     "popReturn");
        else if (column == 9) textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                                          "newAdditionalOrder");
		
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		// System.out.println("诊断信息"+parm);
		// System.out.println("病患性别"+sexCode);
		// System.out.println("病患生日"+birthday);
		// System.out.println("入园日"+inDate);
		// 诊断ICD替换中文
		OrderList orderDesc = new OrderList();
		String ageStr = (StringUtil.showAge(birthday, inDate)).split("岁")[0];
		// System.out.println("年龄"+ageStr);
		int age = TypeTool.getInt(ageStr);
		if (parm.getValue("LIMIT_SEX_CODE").length() > 0) {
			if (!sexCode.equals(parm.getValue("LIMIT_SEX_CODE"))) {
				this.messageBox("诊断性别不符");
				return;
			}
		}
		if (!(parm.getInt("START_AGE") == 0 && parm.getInt("END_AGE") == 0)) {
			// System.out.println("11111111111111111"+parm.getInt("START_AGE")+"jieshu"+parm.getInt("END_AGE"));
			if (!(parm.getInt("START_AGE") <= age && age <= parm
					.getInt("END_AGE"))) {
				this.messageBox("诊断年龄不符");
				return;
			}
		}
		Timestamp now=SystemTool.getInstance().getDate();
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		String icd_code = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icd_code)) {
			table.setItem(table.getSelectedRow(), "ICD_DESC", orderDesc
					.getTableShowValue(icd_code));
			table.setItem(table.getSelectedRow(), "ICD_CODE", icd_code);
			table.setItem(table.getSelectedRow(), "ICD_DATE", now);
			table.setItem(table.getSelectedRow(), "STATUS", "1");//状态：确认 wanglong add 20140416
		}
		table.getTable().grabFocus();  //  chenxi modify 20130322  诊断获取焦点，可以回车方便操作
		// 添加一行新数据
		if (table.getSelectedRow() == table.getRowCount() - 1) {
			insertRow();
			autoHeight();
		}
	}
	
    /**
     * 取得ICD10返回值(附加码)
     * @param tag
     * @param obj
     */
    public void newAdditionalOrder(String tag, Object obj) {// wanglong add 20140416
        TTable table = (TTable) this.callFunction("UI|CLNDIAG_TABLE|getThis");
        // sys_diagnosis返回的数据包
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("ICD_CODE");
        table.setItem(table.getSelectedRow(), "ADDITIONAL_CODE", orderCode);
        table.setItem(table.getSelectedRow(), "ADDITIONAL_DESC", parm.getValue("ICD_CHN_DESC"));
    }

	/**
	 * 诊断信息表格(CLNDIAGTABLE)复选框改变事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onClnDiagTableCheckBoxClicked(Object obj) {
		// this.messageBox("come in");
		TTable tableDown = (TTable) obj;
		tableDown.acceptText();
		int row = tableDown.getSelectedRow();
		String io_type = tableDown.getItemString(row, "IO_TYPE");
		if ("".equals(tableDown.getItemString(row, "ICD_CODE"))) {
			tableDown.setItem(row, "MAINDIAG_FLG", "N");
			// 不存在诊断
			this.messageBox("不存在诊断");
			return false;
		}

		//
		boolean flg = false;
		for (int i = 0; i < tableDown.getRowCount(); i++) {
			if (i == row)
				continue;
			if (io_type.equals(tableDown.getItemString(i, "IO_TYPE"))) {
				if ("Y".equals(tableDown.getItemString(i, "MAINDIAG_FLG")))
					flg = true;
			}
		}
		// 存在主诊断
		if (flg) {
			/** ----- duzhw 20131210 modify start -----
			// $$ add by lx 2012/3/13其它同类型诊断设置成不选
			for (int i = 0; i < tableDown.getRowCount(); i++) {
				if (i != row) {
					if (io_type.equals(tableDown.getItemString(i, "IO_TYPE"))) {
						tableDown.setItem(i, "MAINDIAG_FLG", "N");
					}
				}
			}
			// $$
			return false;
			**/
			tableDown.setItem(row, "MAINDIAG_FLG", "N"); 
			//----- duzhw 20131210 modify end -----
			// 不存在主诊断
		} else {
			tableDown.setItem(row, "MAINDIAG_FLG", "Y");
		}
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TParm parm = new TParm();
		parm.setData("CASE_NO", "1");
		parm.setData("IPD_NO", "0000002");
		parm.setData("MR_NO", "0000001");
		// RULE_TYPE-> I:住院医生；O:门诊医生；E:急诊医生
		parm.setData("RULE_TYPE", "I");
		JavaHisDebug.runFrame("odi\\ODIClnDiagUI.x", parm);
	}

	/**
	 * 显示病患的诊断信息
	 * 
	 * @param case_no
	 *            String
	 */
	public void showAdm_InpDiag(String case_no) {
		//System.out.println("显示病患诊断信息----------");
		TTable table = getTable("CLNDIAG_TABLE");
		String sql = "SELECT 'Y' AS EXEC,CASE_NO,IO_TYPE,ICD_CODE,MAINDIAG_FLG,ICD_TYPE,"
				+ "SEQ_NO,MR_NO,IPD_NO,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM,ICD_DATE "
				   //wanglong add 20140416 增加状态字段
				+ ",CASE WHEN STATUS IS NULL THEN '1' ELSE STATUS END STATUS,ICD_STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE,ADDITIONAL_DESC "
				+ " FROM ADM_INPDIAG WHERE 1=1 ";
		if (!case_no.equals("")) {
			sql += " AND CASE_NO = '" + case_no + "' ";
		}
		if (!this.ioType.equals("")) {
			sql += " AND " + ioType;
		}
		sql += "ORDER BY IO_TYPE ,MAINDIAG_FLG DESC , SEQ_NO";
		//System.out.println("诊断sql->"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			table.removeRowAll();
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm parm = result.getRow(i);
			String icdDesc = this.getICDCode(parm.getValue("ICD_CODE"));
			result.setData("ICD_DESC", i, icdDesc);
		}
		//查询的诊断数据行需要锁定，不允许修改--duzhw add 20131010
		StringBuffer lockRow = new StringBuffer();
		for(int j = 0; j < result.getCount(); j++){
			if(j==0){
				lockRow.append(Integer.toString(j));
			}else{
				lockRow.append(",").append(Integer.toString(j));
			}
		}
		//System.out.println("lockRow="+lockRow.toString());
		table.setLockRows(lockRow.toString());//锁定行，不允许修改
		
		table.setParmValue(result); 
		
		
		
	}

	/**
	 * 锁定指定行
	 * 
	 * @param rule_type
	 *            String
	 */
	public void LockDiagRow(String rule_type) {
		TTable table = getTable("CLNDIAG_TABLE");
		String lockRows = "";
		if (rule_type.equals("I")) {
			for (int i = 0; i < table.getRowCount(); i++) {
				String type = (String) table.getValueAt(i, 0);
				if ("I".equals(type))
					lockRows += i + ",";
			}
		} else {
			for (int i = 0; i < table.getRowCount(); i++) {
				String type = (String) table.getValueAt(i, 0);
				if (!"I".equals(type))
					lockRows += i + ",";
			}
		}
		if (lockRows.length() > 0) {
			lockRows = lockRows.substring(0, lockRows.length() - 1);
		}
		table.setLockRows(lockRows);
	}

	/**
	 * 添加一行新数据
	 */
	public void insertRow() {
		TTable table = getTable("CLNDIAG_TABLE");
        table.acceptText();
        String io_type = "";
        if (table.getRowCount() > 0) {//modify by wanglong 20140404
            int oldrow = table.getRowCount() - 1;
            io_type = table.getItemString(oldrow, "IO_TYPE");
        }
        String rule_type = parm.getValue("RULE_TYPE");
        String type = "I";
        int row = table.addRow();
        if ("I".equals(rule_type)) {
            if ("I".equals(io_type)) type = "M";
            if ("M".equals(io_type)) type = "M";
            if ("O".equals(io_type)) type = "O";
        }
		table.setItem(row, "IO_TYPE", type);
		table.setItem(row, "MAINDIAG_FLG", "N");
		table.setItem(row, "STATUS", "1");//状态：0待查，1确认 wanglong add 20140416
//		table.setItem(row, "OPT_USER", Operator.getID());//wanglong delete 20140416
//		Timestamp date = StringTool.getTimestamp(new Date());
//		table.setItem(row, "OPT_DATE", date);
//		table.setItem(row, "OPT_TERM", Operator.getIP());
	}

	/**
	 * 返回诊断类别(中医，西医诊断)
	 * 
	 * @return String
	 */
	public String getICDType() {
		this.getTable("CLNDIAG_TABLE").acceptText();
		return this.getRadioButton("RadioButton1").isSelected() ? "W" : "C";
	}

	/**
	 * 外部使用：添加多行新数据
	 * 
	 * @param list
	 *            List
	 */
	public void insertRow(List list) {
		TParm parm = new TParm();
		OrderList orderDesc = new OrderList();
		TTable table = this.getTable("CLNDIAG_TABLE");
		int last_row = table.getRowCount() - 1;
		for (int i = 0; i < list.size(); i++) {
			parm = (TParm) list.get(i);
			table.setItem(last_row, "ICD_CODE", parm.getValue("ICD_CODE"));
			table.setItem(last_row, "ICD_DESC", orderDesc
					.getTableShowValue(parm.getValue("ICD_CODE")));
			insertRow();
			last_row++;
		}
		autoHeight();
	}

	/**
	 * 得到页面中Table对象
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}

	/**
	 * 获得单选按钮对象
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getRadioButton(String tag) {
		return (TRadioButton) callFunction("UI|" + tag + "|getThis");
	}

	/**
	 * 调用科诊断
	 */
	public void onDeptCln() {
		// 判断是中医还是西医
		String param = "1," + Operator.getDept() + "," + getICDType();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ICD_CODE");
		List diagList = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			TParm diagParm = new TParm();
			diagParm = OdiUtil.getInstance().getDiagNosis(
					result.getValue("ICD_CODE", i));
			diagList.add(diagParm);
		}
		insertRow(diagList);
	}

	/**
	 * 调用医师诊断
	 */
	public void onDrCln() {
		// 判断是中医还是西医
		String param = "2," + Operator.getDept() + "," + getICDType();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ICD_CODE");
		List diagList = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			TParm diagParm = new TParm();
			diagParm = OdiUtil.getInstance().getDiagNosis(
					result.getValue("ICD_CODE", i));
			diagList.add(diagParm);
		}
		insertRow(diagList);
	}

	/**
	 * 拿到SYS_DIAGNOSIS简称
	 * 
	 * @param icdCode
	 *            String
	 * @return String
	 */
	public synchronized String getICDCode(String icdCode) {
		TDataStore d = com.dongyang.manager.TIOM_Database
				.getLocalTable("SYS_DIAGNOSIS");
		this.code = icdCode;
		d.filterObject(this, "filter");
		if (d.rowCount() == 0)
			return null;
		if ("en".equals(this.getLanguage())) {
			return d.getRowParm(0).getValue("ICD_ENG_DESC");
		} else {
			return d.getRowParm(0).getValue("ICD_CHN_DESC");
		}
	}

	/**
	 * 过滤方法
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filter(TParm parm, int row) {
		return parm.getValue("ICD_CODE", row).equals(this.code);
	}

	/**
	 * 
	 * <p>
	 * Title:
	 * </p>
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2008
	 * </p>
	 * 
	 * <p>
	 * Company:
	 * </p>
	 * 
	 * @author not attributable
	 * @version 1.0
	 */
	public class ODIIcdCode extends TObserverAdapter {
		/**
		 * 得到其他列数据
		 * 
		 * @param ds
		 *            TDS
		 * @param parm
		 *            TParm
		 * @param row
		 *            int
		 * @param column
		 *            String
		 * @return Object
		 */
		public Object getOtherColumnValue(TDS ds, TParm parm, int row,
				String column) {
			if ("ICD_DESC".equals(column)) {
				// System.out.println("---"+parm.getValue("ICD_CODE", row));
				return getICDCode(parm.getValue("ICD_CODE", row));
			}
			return "";
		}
	}

	/**
	 * 判断是否存在主诊断
	 * 
	 * @param ioType
	 * @param table
	 * @return
	 */
	/**
	 * private boolean isExistMainDiag(String ioType, TTable table) { boolean
	 * flg = false; for (int i = 0; i
	 * < table
	 * .getRowCount(); i++) { String theIoType =
	 * table.getDataStore().getItemString(i, "IO_TYPE"); String theMainFlg =
	 * table.getDataStore().getItemString(i, "MAINDIAG_FLG"); // 同样诊断，已有主诊断 if
	 * (theIoType.equals(ioType) && theMainFlg.equals("Y")) { flg = true; break;
	 * }
	 * 
	 * } return flg;
	 * 
	 * }
	 **/

	private void autoHeight() {
		// $$============ add by lx 2012/03/05
		// 解决重叠后无法开立诊断的问题start====================$$//
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		TFrame f = ((TFrame) this.getComponent("UI"));
		if (table.getRowCount() >= 4) {
			int height = 496 + (table.getRowCount() * 20);
			f.setHeight(height);
			f.repaint();
		}
		// $$============ add by lx 2012/03/05
		// 解决重叠后无法开立诊断的问题END====================$$//
	}
	
	/**
	 * 显示病患的诊断信息
	 * 
	 * @param case_no
	 *            String duzhw add 20131016
	 */
	public TParm getAdm_InpDiag(String case_no) {
		TTable table = getTable("CLNDIAG_TABLE");
		String sql = "SELECT 'Y' AS EXEC,CASE_NO,IO_TYPE,ICD_CODE,MAINDIAG_FLG,ICD_TYPE,"
				+ "SEQ_NO,MR_NO,IPD_NO,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM "
				+ " FROM ADM_INPDIAG WHERE 1=1 ";
		if (!case_no.equals("")) {
			sql += " AND CASE_NO = '" + case_no + "' ";
		}
		if (!this.ioType.equals("")) {
			sql += " AND " + ioType;
		}
		sql += "ORDER BY IO_TYPE ,MAINDIAG_FLG DESC , SEQ_NO";
		//System.out.println("诊断sql->"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			table.removeRowAll();
			return result;
		}

		return result;
		
		
	}
	
	/**
	 * 查看操作记录
	 */
    public void onOpHistory() {// wanlong add 20140418
        TParm parm = new TParm();
        parm.setData("CASE_NO", onlyCaseNo);
        this.openDialog("%ROOT%\\config\\odi\\ODIClnDiagHistory.x", parm);
    }
}
