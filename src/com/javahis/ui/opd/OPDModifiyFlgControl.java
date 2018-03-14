/**
 * @className OPDSearchDailyRecorder.java 
 * @author litong
 * @Date 2013-4-7 
 * @version V 1.0 
 */
package com.javahis.ui.opd;

import java.sql.Timestamp;
import jdo.bil.BIL;
import jdo.ekt.EKTIO;
import jdo.opb.OPB; //import jdo.opd.OPDModifiyTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool; //import action.opd.OPDAction;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * @author yanjing
 * @Date 2013-4-7
 */
public class OPDModifiyFlgControl extends TControl {
	private TParm EKTTemp;// 医疗卡最初值
	private TTable table;
	Pat pat;// pat对象
	// reg对象
	Reg reg;
	// BIL 对象
	BIL opbbil;
	// 传入界面传参caseNo
	String caseNoPost;
	// 界面唯一caseNo
	String onlyCaseNo;
	// OPB对象
	OPB opb;
	// 传入界面传参mrNo
	String mrNoPost;
	int drOrderCount = -1;
	int drOrderCountTemp = 0;
	boolean drOrderCountFalse = false;
	boolean addOrder = true;
	private static final String SQL = "SELECT A.RX_NO,B.PAT_NAME,A.ORDER_CODE,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,'Y'AS USE,A.MR_NO,A.ORDER_DESC,A.EXEC_FLG FROM OPD_ORDER A,SYS_PATINFO B WHERE A.MR_NO = B.MR_NO AND CASE_NO = '#' AND ORDERSET_CODE=ORDER_CODE AND SETMAIN_FLG = 'Y'";
	private static final String SELBYCACE_NO = "SELECT SEE_DR_FLG,REG_DATE FROM REG_PATADM WHERE CASE_NO = '$'";

	/**
	 * 初始化方法
	 * 
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		// 初始化就诊时间
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("STARTTIME", date);
		this.setValue("EXEC_FLG", "N");
	}

	/**
	 * 得到TABLE对象
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 读卡操作
	 * 
	 * @return boolean
	 */
	public void onReadEKT() {
		// 读取医疗卡操作
		EKTTemp = EKTIO.getInstance().readEkt();
		if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
			this.messageBox(EKTTemp.getErrText());
			return;
		}
		this.setValue("MR_NO", EKTTemp.getValue("MR_NO"));
		
		//modify by huangtt 20160927 EMPI患者查重提示  start
		 String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
		 Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
	            this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
	        }
		//modify by huangtt 20160927 EMPI患者查重提示  end
		
		this.setValue("MR_NO", pat.getMrNo());
		
		return;
	}

	/**
	 * 检验该就诊号是否开立医嘱
	 */
	public boolean checkOrderCount() {
		if (opb == null) {
			return true;
		}
		if (opb.checkOrderCount()) {
			// this.messageBox("此病患没就诊！");
			return true;
		}
		return false;
	}

	/**
	 * 根据病案号带出所有信息
	 */
	public void onQurey() {
		table.removeRowAll();
		if (pat != null)
			PatTool.getInstance().unLockPat(pat.getMrNo());

		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.grabFocus("PAT_NAME");
			return;
		}
		
		//modify by huangtt 20160927 EMPI患者查重提示  start
		 String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
	            this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
	        }
		//modify by huangtt 20160927 EMPI患者查重提示  end
		
		
		// 界面赋值
		setValueForParm("MR_NO;PAT_NAME;SEX_CODE;", pat.getParm());
		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate()); // showAge(Timestamp birth,
		// Timestamp sysdate);
		setValue("AGE", age);
		// 查找就诊记录
		String regionCode = Operator.getRegion();
		TParm parm = PatAdmTool.getInstance().selDateByMrNo(pat.getMrNo(),
				(Timestamp) getValue("STARTTIME"),
				(Timestamp) getValue("STARTTIME"), regionCode);
		// 查找错误
		if (parm.getCount() < 0) {
			messageBox_("就诊序号选择错误!");
			return;
		}
		// 若挂号信息为0
		if (parm.getCount() == 0) {
			// this.messageBox("无今日挂号信息!");
			// 就诊序号选择界面
			onRecord();
			return;
		}
		// 如果今天只有一次挂号信息
		if (parm.getCount() == 1) {
			// 初始化reg
			String caseNo = parm.getValue("CASE_NO", 0);
			this.setValue("CASE_NO", caseNo);
			reg = Reg.onQueryByCaseNo(pat, caseNo);
			// 判断挂号信息
			if (reg == null) {
				return;
			}
			// 通过reg和caseNo得到pat
			opb = OPB.onQueryByCaseNo(reg);
			onlyCaseNo = opb.getReg().caseNo();
			// 给界面上部分地方赋值
			if (opb == null || checkOrderCount()) {
				this.messageBox("此病人尚未就诊!");
				onRecord();
				return;
			} else {
				String medSql = SQL.replace("#", onlyCaseNo);
				TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
				if (med.getCount() < 1) {
					this.messageBox("没有符合要求的医嘱！");
					return;
				}
				if (med.getErrCode() < 0) {
					this.messageBox_("查询医嘱信息失败");
					return;
				}
				table.setParmValue(med);
			}
			return;
		}
		onRecord();
	}

	/**
	 * 就诊记录选择
	 */
	public void onRecord() {
		// 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号!");
			// 若无此病案号则不能查找挂号信息
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", this.getValue("AGE"));
		// 判断是否从明细点开的就诊号选择
		parm.setData("count", "0");
		String caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		// 根据就诊号查询是否看诊及就诊时间
		String dateSql = SELBYCACE_NO.replace("$", caseNo);
		TParm dateParm = new TParm(TJDODBTool.getInstance().select(dateSql));
		String seeDDate = dateParm.getData("REG_DATE", 0).toString();
		String seeDate1 = seeDDate.substring(0, 10);
		String seeFlg1 = (String) dateParm.getData("SEE_DR_FLG", 0);
		String seeDate2 = seeDate1.replace("-", "/");
		this.setValue("STARTTIME", seeDate2);
		if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
			return;
		}
		reg = Reg.onQueryByCaseNo(pat, caseNo);
		if (reg == null) {
			messageBox("挂号信息错误!");
			return;
		}
		// 通过reg和caseNo得到opb
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = opb.getReg().caseNo();
		if (opb == null || seeFlg1.equals("N")) {
			this.messageBox_("此就诊号尚未就诊!");
			return;

		}
		String medSql = SQL.replace("#", caseNo);
		TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
		if (med.getCount() < 1) {
			messageBox("没有符合要求的医嘱！");
			return;
		}
		if (med.getErrCode() < 0) {
			this.messageBox_("查询医嘱信息失败");
			return;
		}
		this.setValue("CASE_NO", caseNo);
		table.setParmValue(med);
	}

	/**
	 * 保存操作
	 */
	public void onSave() {
		TTable table = getTable("TABLE");
		if (this.getValue("MR_NO").equals("") || null == this.getValue("MR_NO")) {
			this.messageBox("请输入病案号！");
			return;
		}
		if (table == null || "".equals(table)) {
			this.messageBox("没有要保存的医嘱！");
			return;
		}
		table.acceptText();
		TParm parm = new TParm();
		TParm result = new TParm();
		TParm tableParm=table.getParmValue();
		int count=tableParm.getCount();
		int index=0;
		for (int i = 0; i < count; i++) {
			// 是否选择标记
			if(tableParm.getValue("USE", i).equals("Y")){
				index++;//勾选累计需要操作的数据条数
			}else{
				continue;
			}
			parm.addData("USE", tableParm.getValue("USE", i));
			//String rx_no = (String) table.getParmValue().getData("RX_NO", i);// 处方号
			parm.addData("CASE_NO", this.getValue("CASE_NO"));//就诊号
			parm.addData("EXEC_FLG", this.getValue("EXEC_FLG"));//执行状态
			parm.addData("ORDERSET_CODE", tableParm.getValue(
					"ORDERSET_CODE", i));// 集合医嘱主项代码
			parm.addData("ORDERSET_GROUP_NO", table.getParmValue().getInt(
					"ORDERSET_GROUP_NO", i));// 组号
			parm.addData("RX_NO", tableParm.getValue("RX_NO", i));
		}
		if (index<=0) {
			this.messageBox("没有要更新的数据！");
			return;
		}
		parm.setCount(index);
		result = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"updateEXEC_FLG", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("数据错误！");
			return;
		}
		this.messageBox("修改成功！");
		String medSql = SQL.replace("#", onlyCaseNo);
		TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
		table.setParmValue(med);
	}

	/**
	 * 全选事件
	 */
	public void onSelAll() {
		String select = getValueString("CheckAll");
		table.acceptText();
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			parm.setData("USE", i, select);
		}
		table.setParmValue(parm);
	}

	/**
	 * 清空操作
	 */
	public void onClear() {
		EKTTemp = null;
		String string = "MR_NO;PAT_NAME;SEX_CODE";
		this.clearValue(string);
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("STARTTIME", date);
		this.setValue("EXEC_FLG", "N");
		table.removeRowAll();

	}
}
