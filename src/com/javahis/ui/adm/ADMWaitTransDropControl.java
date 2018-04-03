package com.javahis.ui.adm;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.wcomponent.ui.WMatrix;
import com.dongyang.wcomponent.ui.WPanel;
import com.dongyang.wcomponent.ui.model.DefaultWMatrixModel;
import com.dongyang.wcomponent.util.DropTargetUtil;
import com.dongyang.wcomponent.util.IDropTarget;
import com.dongyang.wcomponent.util.TiString;
import com.javahis.component.BaseCard;
import com.javahis.component.S_Card;
import com.javahis.component.T_Card;
import com.javahis.util.DateUtil;
import com.tiis.ui.TiPanel;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMSQLTool;
import jdo.adm.ADMTransLogTool;
import jdo.adm.ADMWaitTransTool;
import jdo.adm.ADMXMLTool;
import jdo.bil.BILPayTool;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSOrdermTool;
import jdo.inw.InwForOutSideTool;
import jdo.odi.OdiOrderTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;



/**   
 * 类描述：入出转科管理
 * 创建人：zhutong   
 * 创建时间：2018年3月30日 下午3:52:53   
 */
public class ADMWaitTransDropControl extends TControl {


	TParm patInfo = null;// 待转入的病患信息

	/** ## del

	TParm admPat = null;// 在院的病患信息

	## */

	/** */
	TParm admInfo;// 记录病区床位状态

	/** */
	private TParm newBaby =new TParm();

	//
	public void onInit() {
		super.onInit();
		pageInit();
	}

	/**
	 * 页面初始化
	 */
	private void pageInit() {

		//
		onQuery();
		onInit_Dept_Station();
		initInStation();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					chose();
				} catch (Exception e) {
				}
			}
		});

		//## 拖拽修改初始化

		//初始化床头卡
		onQueryData();

		//
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		waitIn.getTable().setDragEnabled(true);
		//

	}

	/***********  新加入床头卡功能  *************/

	//##private TPanel tiPanel3 = new TPanel();
	private JScrollPane jScrollPane1 = new JScrollPane();

	// 病区
	private TComboBox stationCode;
	/** 简卡单选 */
	private TRadioButton sCard;
	/** 细卡单选  */
	private TRadioButton tCard;
	/** 病患总数 */
	private TTextField sickCount;
	/** 床位总数 */
	private TTextField bedTotal;
	private TPanel tiPanel1;
	private TiPanel tiPanel2 = new TiPanel();


	/**
	 *
	 */
	public void onQueryData() {
		this.initBedCard();
	}

	/**
	 * 安排病患入床
	 */
	public void onCheckin(int selectRow) {

		// 得到待转入table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");

		// 得到待转入DS
		TDataStore ds = waitIn.getDataStore();

		/** ## del in
			// 得到在院病患table
			//TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
			//checkIn.setSelectedRow(selectRow);
		 ## */

		int waitIndex = waitIn.getSelectedRow();// 待转入表选中行号
		if (waitIndex < 0) {
			this.messageBox_("请选择要入住的病患!");
			return;
		}

		/** ## del in
		int checkIndex = checkIn.getSelectedRow();// 在院病患列表选中行号
		if (checkIndex < 0) {
			this.messageBox_("请选择要入住的床位!");
			return;
		}
		## */

		//刷新检核
		if (!check()) {// shibl 20130117 add
			return;
		}
		// 此患者待转入病区并非本病区
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("此患者待转入病区并非本病区");
			return;
		}
		// 得到待转入选中行的数据
		TParm updata = new TParm();

		/** ## del in
		// 床位号
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 0));
		## */

		// 预约注记
		updata.setData("APPT_FLG", "N");
		// 占床注记
		updata.setData("ALLO_FLG", "Y");
		// 待转入病案号
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// 待转入就诊号
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// 待转入住院号
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// 占床注记
		updata.setData("BED_STATUS", "1");
		// 床位所在病区
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));

		/** ## del in
		// 床位号
		updata.setData("BED_NO",
				checkIn.getValueAt(checkIn.getSelectedRow(), 1));
		## */

		// 科室
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex,
						"IN_DEPT_CODE"));
		// dataStore

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// 检查病患是否包床
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// 表示该病患进行过包床操作

			/** ## del in
				// 如果该病患有包床 那么判断病患入住的床位是不是该病患指定的床位，如果不是要进行提醒，包床信息会被取消
				// 如果转入的床位的MR_NO为空或者与病患的MR_NO不相同 表示该床位不是病患指定的床位
				if (checkIn.getValueAt(checkIndex, 3) == null
						|| "".equals(checkIn.getValueAt(checkIndex, 3))
						|| !waitIn
								.getValueAt(waitIndex, 0)
								.toString()
								.equals(checkIn.getValueAt(checkIndex, 2)
										.toString())) {
					int check = this.messageBox("消息",
							"此病患已包床，不入住指定床位会取消该病患的包床，是否继续？", 0);
					if (check != 0) {
						return;
					}
					updata.setData("CHANGE_FLG", "Y");// 表示病患不入住到指定床位，清空该病患的包床信息
				} else {
					updata.setData("CHANGE_FLG", "N");// 表示病患入住到指定床位
				}
			 ## */

		} else {
			updata.setData("OCCU_FLG", "N");// 表示该病患没有包床
		}
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());
		// =========pangben modify 20110617 start
		updata.setData("REGION_CODE", Operator.getRegion());
		//		System.out.println("----------------updata----------"+updata);
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // 入床保存
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		} else {
			this.messageBox("P0005");
			//## 暂时不调用  sendHL7Mes(updata);
			initInStation();
			chose();
		}
	}

	/**
	 * 更新所有信息
	 */
	public void onReload() {

		pageInit();
	}

	/**
	 * 查询病患的姓名，性别，出生日期
	 */
	public void onQuery() {

		TParm parm = new TParm();
		// =============pangben modify 20110512 start 添加区域查询
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		// ===pangben modify 添加参数
		patInfo = ADMWaitTransTool.getInstance().selpatInfo(parm); // 待转入的病患信息

		/** ## del

		admPat = ADMWaitTransTool.getInstance().selAdmPat(parm); // 在院的病患信息

		##*/

	}

	/**
	 * 待转入转出 科室combo 点选事件
	 */
	public void chose() {

		this.onSelectIn();
		this.onSelectOut(false);
	}

	/**
	 * 待转入转出TABLE 显示
	 *
	 * @param tag
	 *            String
	 */
	public void creatDataStore(String tag) {

		Pat pat = null;
		TParm parm = new TParm();
		TParm result = new TParm();
		if (patInfo == null)
			return;
		TTable table = (TTable) this.callFunction("UI|" + tag + "|getThis");
		int rowCount = table.getRowCount();
		String mrNo = "";//
		String caseNo = "";//
		//System.out.println("row count===="+table.getRowCount());
		Timestamp date = SystemTool.getInstance().getDate(); //=======  chenxi modify 20130228
		if(tag.equals("WAIT_IN")){
			newBaby=new TParm();
		}
		//循环table 显示病患姓名，性别，年龄
		for (int i = 0; i < table.getRowCount(); i++) {
			// 拿到table中的值
			mrNo = table.getValueAt(i, 0).toString().trim();
			caseNo = table.getValueAt(i, 1).toString().trim();
			parm = new TParm();
			result = new TParm();
			pat = new Pat();
			// 得到pat对象拿到生日
			pat = pat.onQueryByMrNo(mrNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("CASE_NO", caseNo);
			result = ADMInpTool.getInstance().selectBedNo(parm);
			// 得到病患年龄
			String[] AGE = StringTool.CountAgeByTimestamp(pat.getBirthday(),
					date);
			//=================  chenxi modify 20130228
			// 向table赋值
			if (tag.equals("WAIT_IN")) {

				table.setValueAt(pat.getName(), i, 2);
				table.setValueAt(pat.getSexCode(), i, 3);
				table.setValueAt(result.getValue("BED_NO_DESC", 0), i, 4); //=====预约床号
				table.setValueAt(AGE[0], i, 5);

				//
				newBaby.addData("NEW_BORN_FLG", this.getNewBornFlg(mrNo, caseNo));
				newBaby.addData("MRNO", mrNo);
				newBaby.addData("ACTION","COUNT",newBaby.getCount("MRNO"));

			} else {
				table.setValueAt(pat.getName(), i, 3);
				table.setValueAt(pat.getSexCode(), i, 4);
				table.setValueAt(AGE[0], i, 5);
			}

		}
//           System.out.println(newBaby);
	}

	/**
	 * 得到新生儿注记
	 * @param mrNo
	 * @param caseNo
	 * @return
	 */
	private String getNewBornFlg(String mrNo,String caseNo){

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT A.NEW_BORN_FLG FROM ADM_INP A ");
		sb.append("   WHERE  A.CASE_NO = '"+caseNo+"'");
		sb.append("   AND A.MR_NO = '"+mrNo+"'");
//       System.out.println("===+++====sb.toString() sb.toString() is :"+sb.toString());
        return new TParm(TJDODBTool.getInstance().select(sb.toString())).getValue("NEW_BORN_FLG", 0);
	}

	/**
	 * 在院病患点选事件
	 */
	public void onInStation() {

		//queryInStation_old();

		//启用新方法
		queryInStation_new();
	}

	/**
	 *  病患信息弹出层   <##停止使用###>
	 */
	private void queryInStation_old(){

		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
			this.messageBox_("请选择病患！");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病床是包床，请选择病患实住病床！");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病患未入住！");
			return;
		}
		TParm parm = table.getParmValue();
		TParm sendParm = new TParm();
		// 配参
		sendParm.setData("ADM", "ADM");
		// 病案号
		sendParm.setData("MR_NO", parm.getData("MR_NO", selectRow));
		// 住院号
		sendParm.setData("IPD_NO", parm.getData("IPD_NO", selectRow));
		// 就诊号
		sendParm.setData("CASE_NO", parm.getData("CASE_NO", selectRow));
		// 姓名
		sendParm.setData("PAT_NAME", parm.getData("PAT_NAME", selectRow));
		// 性别
		sendParm.setData("SEX_CODE", parm.getData("SEX_CODE", selectRow));
		// 年龄
		sendParm.setData("AGE", parm.getData("AGE", selectRow));
		// 床号
		sendParm.setData("BED_NO", parm.getData("BED_NO", selectRow));
		// 科室
		sendParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		// 病区
		sendParm.setData("STATION_CODE", parm
				.getData("STATION_CODE", selectRow));
		// 经治医师
		sendParm.setData("VS_DR_CODE", parm.getData("VS_DR_CODE", selectRow));
		// 主治医师
		sendParm.setData("ATTEND_DR_CODE", parm.getData("ATTEND_DR_CODE",
				selectRow));
		// 科主任
		sendParm.setData("DIRECTOR_DR_CODE", parm.getData("DIRECTOR_DR_CODE",
				selectRow));
		// 主管护士
		sendParm.setData("VS_NURSE_CODE", parm.getData("VS_NURSE_CODE",
				selectRow));
		// 入院状态
		sendParm.setData("PATIENT_CONDITION", parm.getData("PATIENT_CONDITION",
				selectRow));
		// 科主任
		sendParm.setData("DIRECTOR_DR_CODE", parm.getData("DIRECTOR_DR_CODE",
				selectRow));
		// BED_OCCU_FLG
		sendParm.setData("BED_OCCU_FLG", parm
				.getData("BED_OCCU_FLG", selectRow));
		// 保存按钮状态
		sendParm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\AdmPatinfo.x", sendParm);
		initInStation();
	}

	/**
	 * 在院病患信息查询
	 */
	public void initInStation() {

		TParm parm = new TParm();
		parm.setData("STATION_CODE",
				getValue("STATION_CODE").toString() == null ? "" : getValue(
						"STATION_CODE").toString());
		//==================shibl
		//		parm.setData("DEPT_CODE", getValue("DEPT_CODE").toString()==null?"":getValue("DEPT_CODE").toString());
		// =============pangben modify 20110512 start 添加参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop
		//		System.out.println("-----------parm-----"+parm);
		admInfo = ADMInpTool.getInstance().queryInStation(parm);
		//		System.out.println("-------admInfo--1------------"+admInfo);
		if (admInfo.getErrCode() < 0) {
			this.messageBox_(admInfo.getErrText());
			return;
		}

		//initInStationBedCard_old();
	}

	/**
	 * [启用新床头卡以后此方法已经封装在床头卡类中] 暂停使用
	 */
	public void initInStationBedCard_old(){

		/** ## del in
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		// 如果病床没有住人 把年龄的0去掉
		for (int i = 0; i < admInfo.getCount(); i++) {

			if (admInfo.getInt("AGE", i) == 0) {
				// 如果住有病患并且此病床不是包床 年龄为0岁 那么自动加一
				if (admInfo.getValue("MR_NO", i).length() > 0
						&& !admInfo.getValue("BED_STATUS", i).equals("3"))
					admInfo.setData("AGE", i, "1");
				else
					// 没有病患在床将0改为空
					admInfo.setData("AGE", i, "");
			} else {
				// 得到病患年龄========pangb 2011-11-18 获得年龄一致
				String[] AGE = StringTool.CountAgeByTimestamp(
						admInfo.getTimestamp("BIRTH_DATE", i),
						admInfo.getTimestamp("IN_DATE", i));
				admInfo.setData("AGE", i, AGE[0]);
			}
			if (admInfo.getData("IN_DATE", i) != null
					&& admInfo.getValue("MR_NO", i).length() > 0
					&& !admInfo.getValue("BED_STATUS", i).equals("3")) {
				int days = StringTool.getDateDiffer(SystemTool.getInstance()
						.getDate(), admInfo.getTimestamp("IN_DATE", i));
				if (days > 0) {
					admInfo.setData("DAYNUM", i, days);
				} else {
					admInfo.setData("DAYNUM", i, "1");
				}
			} else
				admInfo.setData("DAYNUM", i, "");

		}
		//		System.out.println("-------admInfo--------------"+admInfo);
		table.setParmValue(admInfo);

		 	##*/
	}

	/**
	 * 待转入科室COMBO点选事件
	 */
	public void onSelectIn() {
		//=========modify lim 20120323 begin
		//		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		//		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", ""));
		//		WAIT_IN.retrieve();
		//=========modify lim 20120323 end

		String filter = "";
		if (this.getValueString("IN_STATION_CODE").length() > 0)
			filter += " IN_STATION_CODE ='"
					+ this.getValueString("IN_STATION_CODE") + "'";
		if (this.getValueString("IN_DEPT_CODE").length() > 0)
			filter += " AND IN_DEPT_CODE ='"
					+ this.getValueString("IN_DEPT_CODE") + "'";
		TTable table = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		table.getDataStore().showDebug();
		creatDataStore("WAIT_IN");

	}

	/**
	 * 待转出科室COMBO点选事件
	 */
	public void onSelectOut(boolean isInit) {
		String filter = "";

		if( isInit ){
			//初始化登录人-科室病区
			filter += " OUT_STATION_CODE ='"+ Operator.getStation() + "'";
		    filter += " AND OUT_DEPT_CODE ='"+ Operator.getDept() + "'";

		    //
            this.setValue("OUT_STATION_CODE", Operator.getStation());

		}else{

			if (this.getValueString("OUT_STATION_CODE").length() > 0)
				filter += " OUT_STATION_CODE ='"
						+ this.getValueString("OUT_STATION_CODE") + "'";
			if (this.getValueString("OUT_DEPT_CODE").length() > 0)
				filter += " AND OUT_DEPT_CODE ='"
						+ this.getValueString("OUT_DEPT_CODE") + "'";
		}

		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		table.setFilter(filter);
		table.filter();
		table.setDSValue();
		creatDataStore("WAIT_OUT");
	}

	/**
	 * 安排病患入床   <##停止使用###>
	 */
	public void onCheckin() {
		// 得到待转入table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// 得到待转入DS
		TDataStore ds = waitIn.getDataStore();
		// 得到在院病患table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		int waitIndex = waitIn.getSelectedRow();// 待转入表选中行号
		if (waitIndex < 0) {
			this.messageBox_("请选择要入住的病患!");
			return;
		}
		int checkIndex = checkIn.getSelectedRow();// 在院病患列表选中行号
		if (checkIndex < 0) {
			this.messageBox_("请选择要入住的床位!");
			return;
		}
		//刷新检核
		if (!check()) {// shibl 20130117 add
			return;
		}
		// 此患者待转入病区并非本病区
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("此患者待转入病区并非本病区");
			return;
		}
		//========================  chenxi modify 20130228
		//		// 得到待转入入病患数据
		//		if ("1".equals(checkIn.getValueAt(checkIn.getSelectedRow(), 0))) {
		//			this.messageBox_("此床以占用！");
		//			return;
		//		}
		//		// 判断选中的床号是否已经被预定
		//		if (admInfo.getBoolean("APPT_FLG", checkIndex)) {
		//			int check = this.messageBox("消息", "此床已被预订，是否进继续？", 0);
		//			if (check != 0) {
		//				return;
		//			}
		//		}
		//		// 判断选中的床号是否被包床
		//		if (admInfo.getBoolean("BED_OCCU_FLG", checkIndex)) {
		//			this.messageBox_("此床位已被包床，不可入住！");
		//			return;
		//		}
		// 得到待转入选中行的数据
		TParm updata = new TParm();
		// 床位号
		updata.setData("BED_NO", checkIn
				.getValueAt(checkIn.getSelectedRow(), 0));
		// 预约注记
		updata.setData("APPT_FLG", "N");
		// 占床注记
		updata.setData("ALLO_FLG", "Y");
		// 待转入病案号
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// 待转入就诊号
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// 待转入住院号
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// 占床注记
		updata.setData("BED_STATUS", "1");
		// 床位所在病区
		updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		// 床位号
		updata.setData("BED_NO", checkIn
				.getValueAt(checkIn.getSelectedRow(), 1));
		// 科室
		updata.setData("DEPT_CODE", ds.getItemString(waitIndex,
						"IN_DEPT_CODE"));
		// dataStore

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// 检查病患是否包床
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// 表示该病患进行过包床操作
			// 如果该病患有包床 那么判断病患入住的床位是不是该病患指定的床位，如果不是要进行提醒，包床信息会被取消
			// 如果转入的床位的MR_NO为空或者与病患的MR_NO不相同 表示该床位不是病患指定的床位
			if (checkIn.getValueAt(checkIndex, 3) == null
					|| "".equals(checkIn.getValueAt(checkIndex, 3))
					|| !waitIn.getValueAt(waitIndex, 0).toString().equals(
							checkIn.getValueAt(checkIndex, 2).toString())) {
				int check = this.messageBox("消息",
						"此病患已包床，不入住指定床位会取消该病患的包床，是否继续？", 0);
				if (check != 0) {
					return;
				}
				updata.setData("CHANGE_FLG", "Y");// 表示病患不入住到指定床位，清空该病患的包床信息
			} else {
				updata.setData("CHANGE_FLG", "N");// 表示病患入住到指定床位
			}
		} else {
			updata.setData("OCCU_FLG", "N");// 表示该病患没有包床
		}
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());
		// =========pangben modify 20110617 start
		updata.setData("REGION_CODE", Operator.getRegion());
		//		System.out.println("----------------updata----------"+updata);
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // 入床保存
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		} else {
			this.messageBox("P0005");
			sendHL7Mes(updata);
			initInStation();
			chose();
		}
	}

	/**
	 * 检查某一病患是否包床
	 *
	 * @param caseNo
	 *            String
	 * @return boolean true：包床 false：未包床
	 */
	public boolean checkOccu(String caseNo) {
		TParm qParm = new TParm();
		qParm.setData("CASE_NO", caseNo);
		TParm occu = SYSBedTool.getInstance().queryAll(qParm);
		int count = occu.getCount("BED_OCCU_FLG");
		String check = "N";
		for (int i = 0; i < count; i++) {
			if ("Y".equals(occu.getData("BED_OCCU_FLG", i))) {
				check = "Y";
			}
		}
		if ("Y".equals(check)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检核床位   <##停止使用###>
	 * @return boolean
	 */
	public boolean check() {
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		TTable waitTable = (TTable) this.callFunction("UI|WAIT_IN|getThis"); //chenxi modify 20130308
		if (table.getSelectedRow() < 0) {
			this.messageBox("未选床位");
			return false;
		}
		//=============shibl 20130106 add======多人点同一个床未刷新页面=============================
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		TParm inParm = new TParm();
		inParm.setData("BED_NO", parm.getValue("BED_NO"));
		TParm result = ADMInpTool.getInstance().QueryBed(inParm);
		String APPT_FLG = result.getCount() > 0 ? result
				.getValue("APPT_FLG", 0) : "";
		String ALLO_FLG = result.getCount() > 0 ? result
				.getValue("ALLO_FLG", 0) : "";
		String BED_STATUS = result.getCount() > 0 ? result.getValue(
				"BED_STATUS", 0) : "";
		if (ALLO_FLG.equals("Y")) {
			this.messageBox("此床已占用");
			onReload();
			return false;
		}
		if (BED_STATUS.equals("1")) {
			this.messageBox("此床已被包床");
			onReload();
			return false;
		}
		//=================  chenxi modify 20130308
		if (APPT_FLG.equals("Y")) {
			if (!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(
					parm.getValue("BED_NO_DESC"))) {
				int check = this.messageBox("消息", "此床已被预订，是否进继续？", 0);
				if (check != 0) {
					onReload();
					return false;
				}
				return true;
			}

		}
		return true;
	}

	/**
	 * 病患入住检核
	 *
	 * @return boolean
	 */
	public boolean checkSysBed() {
		// 得到待转入table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// 得到在院病患table
		TTable checkIn = (TTable) this.callFunction("UI|in|getThis");
		String waitMr = waitIn.getValueAt(waitIn.getSelectedRow(), 0)
				.toString();
		String bedMr = checkIn.getValueAt(checkIn.getSelectedRow(), 3) == null ? ""
				: checkIn.getValueAt(checkIn.getSelectedRow(), 3).toString();
		if (bedMr == null || "".equals(bedMr))
			return true;
		else if (waitMr.equals(bedMr)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 转科管理
	 */
	public void onOutDept() {

		/// onOutDept_old();

		//启用新方法
		onOutDept_new();
	}

	/**
	 *  转科管理   <##停止使用###>
	 */
	private void onOutDept_old() {

		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		if (selectRow == -1) {
			this.messageBox("请选择在院病患！");
			return;
		}
		if (table.getValueAt(selectRow, 3) == null) {
			this.messageBox("请选择在院病患！");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病床是包床，请选择病患实住病床！");
			return;
		}

		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox("此病患未入住！");
			return;
		}
		TParm parm = table.getParmValue();
		TParm sendParm = new TParm();
		// 病案号
		sendParm.setData("MR_NO", parm.getData("MR_NO", selectRow));
		// 住院号
		sendParm.setData("IPD_NO", parm.getData("IPD_NO", selectRow));
		// 就诊号
		sendParm.setData("CASE_NO", parm.getData("CASE_NO", selectRow));
		// 姓名
		sendParm.setData("PAT_NAME", parm.getData("PAT_NAME", selectRow));
		// 性别
		sendParm.setData("SEX_CODE", parm.getData("SEX_CODE", selectRow));
		// 年龄
		sendParm.setData("AGE", parm.getData("AGE", selectRow));
		// 床号
		sendParm.setData("BED_NO", parm.getData("BED_NO", selectRow));
		// 科室
		sendParm.setData("OUT_DEPT_CODE", parm.getData("DEPT_CODE", selectRow));
		// 病区
		sendParm.setData("OUT_STATION_CODE", parm.getData("STATION_CODE",
				selectRow));
		// 入院时间
		sendParm.setData("IN_DATE", parm.getData("IN_DATE", selectRow));

		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMOutInp.x", sendParm);
		initInStation();
		TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		//System.out.println("=======WAIT_OUT======"+outTable.getFilter());
		//$$=========add by lx===========$$//
		//outTable.retrieve();
		//onSelectOut();
		outTable.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT(
				this.getValueString("OUT_DEPT_CODE"),
				this.getValueString("OUT_STATION_CODE")));
		outTable.retrieve();
		//$$==================$$//
		creatDataStore("WAIT_OUT");
	}

	/**
	 * 包床管理
	 */
	public void onBed() {

		// doOnBed_old();

		//启用新方法
		doOnBed_new();
	}

	/**
	 * 包床管理   <##停止使用###>
	 */
	private void doOnBed_old(){

		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
			this.messageBox_("请选择病患！");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病患未入住！");
			return;
		}
		if ("3".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病床是包床，请选择病患实住病床！");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", admInfo.getValue("CASE_NO", selectRow));
		sendParm.setData("MR_NO", admInfo.getValue("MR_NO", selectRow));
		sendParm.setData("IPD_NO", admInfo.getValue("IPD_NO", selectRow));
		sendParm.setData("DEPT_CODE", admInfo.getValue("DEPT_CODE", selectRow));
		sendParm.setData("STATION_CODE", admInfo.getValue("STATION_CODE",
				selectRow));
		sendParm.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm bed = new TParm();
		bed.setData("BED_NO", admInfo.getValue("BED_NO", selectRow));
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = admInfo.getValue("CASE_NO", selectRow);
		int count = check.getCount("BED_NO");
		boolean flg = false;
		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				flg = true;
			}
		}
		if (flg == true) {
			int checkFlg = this.messageBox("消息", "此病房已有其他病患!是否继续包床？", 0);
			if (checkFlg != 0)
				return;
		}

		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);
		initInStation();
		chose();
	}

	/**
	 * 初始化带入默认科室 诊区
	 */
	public void onInit_Dept_Station() {
		String userId = Operator.getID();
		String station = Operator.getStation();
		String dept = Operator.getDept();
		TComboBox admstation = (TComboBox) this.getComponent("STATION_CODE");
		TParm Station = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		admstation.setParmValue(Station);
		admstation.onQuery();
		TComboBox admdept = (TComboBox) this.getComponent("DEPT_CODE");
		TParm Dept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		admdept.setParmValue(Dept);
		admdept.onQuery();
		TComboBox in_station = (TComboBox) this.getComponent("IN_STATION_CODE");
		TParm inStation = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationList(userId)));
		in_station.setParmValue(inStation);
		in_station.onQuery();
		TComboBox out_station = (TComboBox) this.getComponent("OUT_STATION_CODE");
		//===========modify lim  begin
		//TParm outStaion = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserStationList(userId)));
		TParm outStaion = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserStationListForDynaSch()));
		//===========modify lim  end
		out_station.setParmValue(outStaion);
		out_station.onQuery();
		TComboBox in_dept = (TComboBox) this.getComponent("IN_DEPT_CODE");
		TParm inDept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserDeptList(userId)));
		in_dept.setParmValue(inDept);
		in_dept.onQuery();
		TComboBox out_dept = (TComboBox) this.getComponent("OUT_DEPT_CODE");
		TParm outDept = new TParm(TJDODBTool.getInstance().select(
				ADMSQLTool.getInstance().getUserDeptList(userId)));
		//TParm outDept = new TParm(TJDODBTool.getInstance().select(ADMSQLTool.getInstance().getUserDeptListForDynaSch()));
		out_dept.setParmValue(outDept);
		out_dept.onQuery();

		// 待转入和待转出 Grid 赋值
		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		TTable WAIT_OUT = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", station));
		WAIT_IN.retrieve();
		WAIT_OUT.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT("", ""));
		WAIT_OUT.retrieve();

		// 根据用户设置默认科室和病区
		setValue("IN_DEPT_CODE", "");
		setValue("DEPT_CODE", dept);
		setValue("OUT_DEPT_CODE", "");
		setValue("IN_STATION_CODE", station);
		setValue("STATION_CODE", station);
		setValue("OUT_STATION_CODE", station);
	}

	/**
	 * 取消包床
	 */
	public void onCancelBed() {

		//doOnCancelBed_old();

		//启用新方法
		doOnCancelBed_new();
	}

	/**
	 * 取消包床   <##停止使用###>
	 */
	private void doOnCancelBed_old(){

		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
			this.messageBox_("请选择病患！");
			return;
		}
		if ("0".equals(table.getValueAt(selectRow, 0))) {
			this.messageBox_("此病患未入住！");
			return;
		}
		int re = this.messageBox("提示", "确认要取消该病患的包床吗？", 0);
		if (re != 0) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", admInfo.getValue("CASE_NO", selectRow));
		TParm result = SYSBedTool.getInstance().clearOCCUBed(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		initInStation();
		chose();
	}


	//$$==========liuf==========$$//
	/**
	 * CIS，血糖病患入住发送
	 * @param parm
	 */
	private void sendHL7Mes(TParm parm) {

		//System.out.println("sendHL7Mes()");

		// ICU、CCU注记
		String caseNO = parm.getValue("CASE_NO");
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		String type = "ADM_IN";
		parm.setData("ADM_TYPE", "I");
		//CIS
		if (IsICU || IsCCU) {
			List list = new ArrayList();
			parm.setData("SEND_COMP", "CIS");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(
					list, type);
			if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		//血糖
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
				type);
		if (resultParm.getErrCode() < 0)
			messageBox(resultParm.getErrText());
	}

	//$$==========liuf==========$$//

	/**
	 * 取消转科( 添加刷新床头卡功能 )
	 * @param parm
	 */
	public void onCancelTrans() {

		//启用新方法
		doOnCancelTrans_new();
	}


	/**
	 * 取消住院
	 */
	public void onCancelInHospital() {

		// doOnCancelInHospital_old();

		//启用新方法
		doOnCancelInHospital_new();
	}

	/**
	 * 取消住院   <##停止使用###>   chenxi   modify  20130417
	 */
	private void doOnCancelInHospital_old(){

		if (!checkDate())
			return;
		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString();
		TParm result = new TParm();
		//=================执行取消住院操作
		int check = this.messageBox("消息", "是否取消？", 0);
		if (check == 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("PSF_KIND", "INC");
			parm.setData("PSF_HOSP", "");
			parm.setData("CANCEL_FLG", "Y");
			parm.setData("CANCEL_DATE", SystemTool.getInstance().getDate());
			parm.setData("CANCEL_USER", Operator.getID());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				parm.setData("REGION_CODE", Operator.getRegion());
			}
			result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
					"ADMCanInp", parm); //
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");
				initInStation();
				chose();
			}
		}
	}

	/**
	 * 检核数据  <##停止使用###>
	 *
	 * @return boolean
	 */
	public boolean checkDate() {

		TTable table = (TTable) this.callFunction("UI|in|getThis");
		int selectRow = table.getSelectedRow();

		if (selectRow < 0 || table.getValueAt(selectRow, 3) == null) {
			this.messageBox_("请选择病患！");
			return false;
		}
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getData("CASE_NO", selectRow).toString();
		//==============预交金未退,不可取消住院
		TParm result = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrName());
			return false;
		}
		if (result.getDouble("PRE_AMT", 0) > 0) {
			this.messageBox("此病患还有预交金未退,不可取消住院");
			return false;
		}
		//==================以计费不可取消住院
		boolean checkflg = IBSOrdermTool.getInstance().existFee(
				tableParm.getRow(selectRow));
		if (!checkflg) {
			messageBox("已产生费用,不可取消住院");
			return false;
		}
		// 检查医生是否开立医嘱
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		if (this.checkOrderisEXIST(parm)) {
			this.messageBox("该病患已开立医嘱，不可取消住院！");
			callFunction("UI|save|setEnabled", false);
			return false;
		}

		return true;
	}

	/**
	 * 检查该病人是否开立医嘱
	 *
	 *
	 */
	public boolean checkOrderisEXIST(TParm Parm) {

		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND DC_DATE IS NULL ";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		// 如果没有为执行的数据返回数据集数量为0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}

	//#####

	/**
	 *
	 */
	private void initBedCard() {

		tiPanel1 = ((TPanel) getComponent("tPanel_3"));
		//testBorder3 = new TitledBorder(BorderFactory.createEtchedBorder(new Color(228, 255, 255),new Color(112, 154, 161)),"测试重画");
		//tiPanel2.setBounds(new Rectangle(0, 1, 1000, 530));
		tiPanel2.setSize(1010, 680);
		tiPanel2.setLayout(null);
		jScrollPane1.setBounds(new Rectangle(3, 3, 992, 502));
		tiPanel1.add(tiPanel2, null);
		tiPanel2.add(jScrollPane1, null);

		//##jScrollPane1.getViewport().add(tiPanel3, null);
		sickCount = (TTextField) this.getComponent("tTextField_1");
		bedTotal = (TTextField) this.getComponent("bedTotal_00");

		sCard = (TRadioButton) this.getComponent("S_CARD");
		tCard = (TRadioButton) this.getComponent("T_CARD");

		//病区
		this.stationCode = (TComboBox) this.getComponent("STATION_CODE");

		//初始化床位
		buildBedData();
	}


	/** 床头卡 */
	private WMatrix jList = null;

	/**
	 * 动态构造床位数据
	 */
	private void buildBedData() {

		//@@
		DefaultWMatrixModel model = new DefaultWMatrixModel();

		//##tiPanel3.removeAll();
		int j = 0;
		// this.messageBox("sCard"+sCard);

		//this.messageBox("Operator.getRegion()  " + Operator.getRegion());
		//this.messageBox("this.stationCode.getValue()  " +this.stationCode.getValue());

		//服务器时间
		Timestamp serverTime = TJDODBTool.getInstance().getDBTime();

		// 判断 该病区在系统中未设床位
		String bedsSql = " SELECT BED_NO,BED_NO_DESC,ALLO_FLG,BABY_BED_FLG  "  //新增婴儿床
				+ " FROM SYS_BED  " + " WHERE REGION_CODE='"
				+ Operator.getRegion() + "' " + " AND STATION_CODE='"
				+ this.stationCode.getValue() + "' " + " AND ACTIVE_FLG='Y' "
				+ " ORDER BY REGION_CODE,BED_NO ";
		//System.out.println("==bedsSql=="+bedsSql);
		TParm bedParm = new TParm(TJDODBTool.getInstance().select(bedsSql));
		int count = 0;
		int occuBedCount = 0;
		//this.messageBox("bedParm" + bedParm.getCount());
		if (bedParm.getCount() == 0 || bedParm.getCount() == -1) {
			this.messageBox("该病区在系统中未设床位");
		} else {

			// 简卡
			if (sCard.isSelected()) {//
				occuBedCount = 0;

				//this.messageBox("显示简卡");

				// 查询住院病人资料
				String sPatSql = "Select A.bed_no_desc,C.pat_name,B.mr_no,B.nursing_class,";
				sPatSql += "D.CHN_DESC,E.colour_red,E.colour_green,E.colour_blue,F.colour_red as colour_red1,";
				sPatSql += "F.colour_green as colour_green1,F.colour_blue as colour_blue1,B.PATIENT_STATUS,A.BED_OCCU_FLG,";
				sPatSql += "B.REGION_CODE,B.case_no,C.BIRTH_DATE,C.SEX_CODE,'' ins_status,B.CLNCPATH_CODE,B.DEPT_CODE,B.IPD_NO,";
				sPatSql += "A.ROOM_CODE,G.BEDTYPE_DESC, ";
				sPatSql += "to_number(to_char(sysdate,'yyyy'))-to_number(to_char(C.BIRTH_DATE,'yyyy')) age,TO_CHAR(B.in_date,'yyyy/MM/dd HH24:mm:ss') IN_DATE";
				//## --拖拽功能添加--
				sPatSql += " ,A.bed_no,A.BED_STATUS," ;
				sPatSql += " B.VS_DR_CODE,B.ATTEND_DR_CODE,B.DIRECTOR_DR_CODE,B.VS_NURSE_CODE,B.PATIENT_CONDITION ";
				//--新生儿
				sPatSql += " ,B.NEW_BORN_FLG,A.BABY_BED_FLG ";
				//--新生日算法
				sPatSql += " ,B.in_date as IN_DATE_2 ";
				//##
				sPatSql += "from SYS_BED A,ADM_INP B,SYS_PATINFO C,(select * from sys_dictionary where group_id='SYS_SEX') D,";
				sPatSql += "ADM_NURSING_CLASS E,ADM_PATIENT_STATUS F,SYS_BED_TYPE G ";
				sPatSql += "Where A.REGION_CODE='"+Operator.getRegion()+"' ";
				sPatSql += "and A.station_code='"+this.stationCode.getValue()+"' ";
				sPatSql += "and A.REGION_CODE=B.REGION_CODE ";
				sPatSql += "and A.case_no=B.case_no ";
				sPatSql += "and B.mr_no=C.mr_no ";
				sPatSql += "and C.SEX_CODE=D.ID ";
				sPatSql += "and E.NURSING_CLASS_Code(+)=B.NURSING_CLASS ";
				sPatSql += "and F.PATIENT_STATUS_code(+)=B.PATIENT_STATUS ";
				sPatSql += "and A.BED_TYPE_CODE=G.BED_TYPE_CODE(+) ";
//                sPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "; //与下面的一致
                sPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' "; //如不去掉床位状态条件 有的包床显示不出来
				sPatSql += "ORDER BY A.REGION_CODE,A.BED_NO";

				//				System.out.println("=====sPatSql====="+sPatSql);
				TParm sPatParm = new TParm(TJDODBTool.getInstance().select(
						sPatSql));
				//this.messageBox("++sPatParm+"+sPatParm);
				int result_count = sPatParm.getCount();

				//this.messageBox("SB SBL ++result_count  +"+result_count);
				//this.messageBox("sCard"+result_count);
				// 床位总数
				int row = Integer.parseInt(String.valueOf(bedParm.getCount()));
				bedTotal.setText(row+"");
				//(int) TiMath.ceil(row / 5.0, 0)

				//System.out.println("=============简卡长度================"+row);

				for (int i = 0; i < row; i++) {
					S_Card s_card[] = new S_Card[row];
					//this.messageBox("==i=="+i);
					// 床号相同，说明有人使用床位
					// String.valueOf(((Vector)Sta_bed.get(1)).get(i)).equals(String.valueOf(((Vector)result.get(0)).get(j)))
					if (result_count != 0
							&& bedParm.getValue("BED_NO_DESC", i).equals(
									sPatParm.getValue("BED_NO_DESC", j))) {

						String s[] = new String[31];
						//this.messageBox("COLOUR_RED====="+sPatParm.getValue("COLOUR_RED", j));
						s[0] = sPatParm.getValue("COLOUR_RED", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_RED", j);
						s[1] = sPatParm.getValue("COLOUR_GREEN", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_GREEN", j);
						s[2] = sPatParm.getValue("COLOUR_BLUE", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_BLUE", j);
						s[3] = sPatParm.getValue("COLOUR_RED1", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_RED1", j);
						s[4] = sPatParm.getValue("COLOUR_GREEN1", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_GREEN1", j);
						s[5] = sPatParm.getValue("COLOUR_BLUE1", j).equals("") ? "255"
								: sPatParm.getValue("COLOUR_BLUE1", j);
						// PATIENT_STATUS
						s[6] = sPatParm.getValue("PATIENT_STATUS", j);
						// BED_OCCU_FLG
						for (int k = 0; k < sPatParm.getCount("MR_NO"); k++) {
							if (sPatParm.getValue("MR_NO", j).equals(
									sPatParm.getValue("MR_NO", k))) {
								if (sPatParm.getValue("BED_OCCU_FLG", j)
										.equals("Y")) {
									s[7] = "Y";
									occuBedCount++;
									break;
								}
							}
						}
						if (s[7] == null || s[7].length() <= 0)
							s[7] = sPatParm.getValue("BED_OCCU_FLG", j);
						// REGION_CODE
						s[8] = sPatParm.getValue("REGION_CODE", j);
						// case_no
						s[9] = sPatParm.getValue("CASE_NO", j);
						// BIRTH_DATE
						s[10] = sPatParm.getValue("BIRTH_DATE", j);
						// SEX_CODE
						s[11] = sPatParm.getValue("SEX_CODE", j);
						// ins_status
						s[12] = sPatParm.getValue("INS_STATUS", j);
						// CLNCPATH_CODE
						s[13] = sPatParm.getValue("CLNCPATH_CODE", j);
						s[14] = sPatParm.getValue("DEPT_CODE", j);
						s[15] = sPatParm.getValue("IPD_NO", j);
						s[16] = sPatParm.getValue("ROOM_CODE", j);
						s[17] = sPatParm.getValue("BEDTYPE_DESC", j);
                        //
						s[18] = sPatParm.getValue("AGE", j);
						s[19] = sPatParm.getValue("IN_DATE", j);

						//##
						s[20] = sPatParm.getValue("BED_NO", j);
						s[21] = sPatParm.getValue("BED_STATUS", j);
						s[22] = sPatParm.getValue("VS_DR_CODE", j);
						s[23] = sPatParm.getValue("ATTEND_DR_CODE", j);
						s[24] = sPatParm.getValue("DIRECTOR_DR_CODE", j);
						s[25] = sPatParm.getValue("VS_NURSE_CODE", j);
						s[26] = sPatParm.getValue("PATIENT_CONDITION", j);
						s[27] = sPatParm.getValue("NEW_BORN_FLG", j);
						s[28] = sPatParm.getValue("PATIENT_STATUS", j);

						//新生日算法
						//Timestamp indate = (Timestamp)sPatParm.getData("IN_DATE_2", j);
						Timestamp birth = (Timestamp)sPatParm.getData("BIRTH_DATE", j);
						//s[29] = DateUtil.showAge( birth, indate);
						s[29] = DateUtil.showAge( birth,serverTime);

						//婴儿床
						s[30] = sPatParm.getValue("BABY_BED_FLG", j);

						//
						//this.messageBox("BED_NO"+sPatParm.getValue("BED_NO", j));
						s_card[i] = new S_Card( sPatParm.getValue("BED_NO_DESC", j),
								                sPatParm.getValue("PAT_NAME", j),
								                sPatParm .getValue("MR_NO", j),
								                sPatParm.getValue("NURSING_CLASS", j) == null ? "" : sPatParm.getValue("NURSING_CLASS", j),
								                (String) stationCode.getValue(),
								                sPatParm.getValue("CHN_DESC", j) == null ? "": sPatParm.getValue("CHN_DESC", j),
								                		
								                s );

						if (j < result_count - 1) {
							j++;
						}
						count++;
						// 是空床的情况
					} else {
						// String.valueOf(((Vector)Sta_bed.get(1)).get(i))
						s_card[i] = new S_Card( bedParm.getValue("BED_NO_DESC", i),
								                "",
								                bedParm.getValue("BED_NO", i),
								                bedParm.getValue("BABY_BED_FLG", i) );
					}
					//s_card[i].setPreferredSize(new Dimension(150, 60));
					s_card[i].setPreferredSize(new Dimension(159, 71));
					//this.messageBox("=====s_card["+i+"]"+s_card[i].sBed_no);
					// s_card[i].addMouseListener(this) ;
					//TButton btest=new TButton();
					//btest.setLabel("ok");
					//tiPanel3.add(btest,null);

					//##tiPanel3.add(s_card[i], null);
					//@@
					WPanel jp = new WPanel();
					jp.setId("jp_" + i);
					jp.add(s_card[i], null);
					model.addElement(jp);
					jp.setRoundedBorder();

				}
				count -= occuBedCount;
				// 细卡
			} else if (tCard.isSelected()) {
				occuBedCount = 0;

				//this.messageBox("显示细卡");

				// 查询住院病人资料
				String tPatSql = "Select A.bed_no_desc,C.pat_name,B.mr_no,B.nursing_class,D.CHN_DESC,";
				tPatSql += /*"case NHI_CTZ_FLG when 'Y' then '医保' when 'N' then '自费' end*/"E.CTZ_DESC CTZ,B.PATIENT_STATUS,G.USER_NAME,'' BLANK,";//modify by sunqy 20140526
				tPatSql += "to_number(to_char(sysdate,'yyyy'))-to_number(to_char(C.BIRTH_DATE,'yyyy')) age,TO_CHAR(B.in_date,'yyyy/MM/dd HH24:mm:ss') IN_DATE,A.case_no,";
				tPatSql += "I.colour_red,I.colour_green,I.colour_blue,J.colour_red  as colour_red1,J.colour_green as colour_green1,J.colour_blue as colour_blue1,A.BED_OCCU_FLG,";
				tPatSql += "B.REGION_CODE,B.case_no patCaseNo,C.BIRTH_DATE,C.SEX_CODE,'' ins_status,B.CLNCPATH_CODE,B.DEPT_CODE,B.IPD_NO ";
				//## --拖拽功能添加--
				tPatSql += " ,A.bed_no,A.BED_STATUS," ;
				tPatSql += " B.VS_DR_CODE,B.ATTEND_DR_CODE,B.DIRECTOR_DR_CODE,B.VS_NURSE_CODE,B.PATIENT_CONDITION ";
				//--新生儿
				tPatSql += " ,B.NEW_BORN_FLG,A.BABY_BED_FLG ";
				//--新生日算法
				tPatSql += " ,B.in_date as IN_DATE_2 ";
				//##
				tPatSql +="from SYS_BED A,ADM_INP B,SYS_PATINFO C,(select * from sys_dictionary where group_id='SYS_SEX') D,";
				tPatSql +="SYS_CTZ E,SYS_OPERATOR G,ADM_NURSING_CLASS I,ADM_PATIENT_STATUS J ";
				tPatSql +="Where A.REGION_CODE='"+Operator.getRegion()+"' ";
				tPatSql +="and A.station_code='"+this.stationCode.getValue()+"' ";
				tPatSql +="and A.REGION_CODE=B.REGION_CODE ";
				tPatSql +="and A.case_no=B.case_no ";
				tPatSql +="and B.mr_no=C.mr_no ";
				tPatSql +="and C.SEX_CODE=D.ID ";
				tPatSql +="and B.ctz1_code=E.ctz_code(+) ";//add 添加(+)
				tPatSql +="and B.VS_DR_CODE=G.user_id(+) ";// modified by WangQing 20170516 add(+)
				tPatSql +="AND b.nursing_class=i.nursing_class_code(+) ";
				tPatSql +="AND b.patient_status=j.patient_status_code(+) ";
//				tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' ";
				tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y'";

				//tPatSql +="AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' ";
				tPatSql +="ORDER BY A.REGION_CODE,A.BED_NO";

				//				System.out.println("=====tPatSql====="+tPatSql);
				//
				TParm tPatParm = new TParm(TJDODBTool.getInstance().select(
						tPatSql));
				int result_count = tPatParm.getCount();
				//this.messageBox("tCard"+result_count);
				int row = bedParm.getCount();
				bedTotal.setText(row+"");
				//(int)TiMath.ceil(row/5.0,0)

				T_Card t_card[] = new T_Card[row];
				for (int i = 0; i < row; i++) {
					String s[] = new String[33];
					//已占用床位
					if (result_count != 0
							&& result_count != 0
							&& bedParm.getValue("BED_NO_DESC", i).equals(
									tPatParm.getValue("BED_NO_DESC", j))) {

						//this.messageBox("case no"+tPatParm.getValue("CASE_NO",j));
						//构造细卡数据
						s[0] = tPatParm.getValue("CHN_DESC", j);
						s[1] = tPatParm.getValue("CTZ", j);
						s[2] = tPatParm.getValue("PATIENT_STATUS", j);
						s[3] = tPatParm.getValue("USER_NAME", j);
						s[4] = tPatParm.getValue("BLANK", j);
						s[5] = tPatParm.getValue("AGE", j);
						s[6] = tPatParm.getValue("IN_DATE", j);
						//20150126 wangjingchun 注释掉 817
						//取诊断内容  ADM_INP ADM_INPDIAG
//						String diagSql = "Select b.icd_chn_desc ";
//						diagSql += "from ADM_INP a ,SYS_DIAGNOSIS b ";
//						diagSql += "where a.CASE_NO='"
//								+ tPatParm.getValue("CASE_NO", j) + "' ";
//						//diagSql+="and a.maindiag_flg = 'Y' ";
//						diagSql += "and b.icd_code = a.MAINDIAG ";
//						//diagSql+="order by a.io_type desc";
//
//						//		                System.out.println("=====diagSql====="+diagSql);
//						TParm diagParm = new TParm(TJDODBTool.getInstance()
//								.select(diagSql));
//						if (diagParm.getCount() == 0
//								|| diagParm.getCount() == -1) {
//							s[7] = "";
//						} else {
//							s[7] = diagParm.getValue("ICD_CHN_DESC", 0);
//						}
						
						//20150126 wangjingchun add 817
						s[7] = this.getIcdChnDesc(tPatParm.getValue("CASE_NO", j));
						//colour_red
						//this.messageBox("colour_red====="+tPatParm.getValue("colour_red", j));
						//System.out.println("++++colour red++++"+tPatParm.getValue("COLOUR_RED", j));
						s[8] = tPatParm.getValue("COLOUR_RED", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_RED", j);
						s[9] = tPatParm.getValue("COLOUR_GREEN", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_GREEN", j);
						s[10] = tPatParm.getValue("COLOUR_BLUE", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_BLUE", j);

						s[11] = tPatParm.getValue("COLOUR_RED1", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_RED1", j);
						s[12] = tPatParm.getValue("COLOUR_GREEN1", j)
								.equals("") ? "255" : tPatParm.getValue(
								"COLOUR_GREEN1", j);
						s[13] = tPatParm.getValue("COLOUR_BLUE1", j).equals("") ? "255"
								: tPatParm.getValue("COLOUR_BLUE1", j);
						//BED_OCCU_FLG
						for (int k = 0; k < tPatParm.getCount("MR_NO"); k++) {
							if (tPatParm.getValue("MR_NO", j).equals(
									tPatParm.getValue("MR_NO", k))) {
								if (tPatParm.getValue("BED_OCCU_FLG", j)
										.equals("Y")) {
									s[14] = "Y";
									occuBedCount++;
									break;
								}
							}
						}
						if (s[14] == null || s[14].length() <= 0)
							s[14] = tPatParm.getValue("BED_OCCU_FLG", j);
						s[15] = tPatParm.getValue("REGION_CODE", j);
						s[16] = tPatParm.getValue("CASE_NO", j);
						s[17] = tPatParm.getValue("BIRTH_DATE", j);
						s[18] = tPatParm.getValue("SEX_CODE", j);
						s[19] = tPatParm.getValue("INS_STATUS", j);
						s[20] = tPatParm.getValue("CLNCPATH_CODE", j);
						s[21] = tPatParm.getValue("DEPT_CODE", j);
						s[22] = tPatParm.getValue("IPD_NO", j);

						//##
						s[23] = tPatParm.getValue("BED_NO", j);
						s[24] = tPatParm.getValue("BED_STATUS", j);
						s[25] = tPatParm.getValue("VS_DR_CODE", j);
						s[26] = tPatParm.getValue("ATTEND_DR_CODE", j);
						s[27] = tPatParm.getValue("DIRECTOR_DR_CODE", j);
						s[28] = tPatParm.getValue("VS_NURSE_CODE", j);
						s[29] = tPatParm.getValue("PATIENT_CONDITION", j);
						s[30] = tPatParm.getValue("NEW_BORN_FLG", j);

						//新生日算法
						//Timestamp indate = (Timestamp)tPatParm.getData("IN_DATE_2", j);
						Timestamp birth = (Timestamp)tPatParm.getData("BIRTH_DATE", j);
						//s[31] = DateUtil.showAge( birth, indate);
						s[31] = DateUtil.showAge( birth,serverTime);

						//婴儿床
						s[32] = tPatParm.getValue("BABY_BED_FLG", j);

						t_card[i] = new T_Card( tPatParm.getValue("BED_NO_DESC",j),
								                tPatParm.getValue("PAT_NAME", j),
								                tPatParm.getValue("MR_NO", j),
								                tPatParm.getValue("NURSING_CLASS", j),
								                (String) stationCode.getValue(),
								                s );

						if (j < result_count - 1) {
							j++;
						}
						count++;
					} else {
						//空床位
						t_card[i] = new T_Card( bedParm.getValue("BED_NO_DESC",i),
								                "", bedParm.getValue("BED_NO", i),
								                bedParm.getValue("BABY_BED_FLG", i) );
					}
					t_card[i].setPreferredSize(new Dimension(153, 180));
					//t_card[i].addMouseListener(this) ;

					//##tiPanel3.add(t_card[i], null);
					//@@
					T_Card tc = t_card[i];
					tc.tiL_ipdno.setVisible(false);
					WPanel jp = new WPanel();
					jp.add(tc, null);
					jp.setId("jp_" + i);
					model.addElement(jp);
					jp.setRoundedBorder();

				}
				count -= occuBedCount;
			}
		}
		
		//this.messageBox("重画panel3");
		//重新载入面版数据
		//## jScrollPane1.setViewportView(tiPanel3);

		//@@
		//
		TTable waitOut = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		TTable waitIn = (TTable) callFunction("UI|WAIT_IN|getThis");
		//
		jList = new WMatrix(model);
		jList.doProcessRowCount(6);
		jScrollPane1.setViewportView(jList);
		//
		doTuoZhuai2ListInit(waitIn, jList);
		doTuoZhuai2TableInit(jList, waitOut);
		//
		this.doMouseClick(jList);

		//设置床位总数;
		sickCount.setText(String.valueOf(count));
		//tiPanel3.repaint();

	}
	
	/**
	 * 获取诊断名称
	 * 20150126 wangjinghchun add 817
	 * @param caseNo
	 * @return
	 */
	public String getIcdChnDesc(String caseNo){
		String icdChnDesc = "";
		String icdCodeSql = "SELECT A.DESCRIPTION,B.ICD_CHN_DESC FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE A.CASE_NO = '"
						+caseNo
						+"' AND A.MAINDIAG_FLG = 'Y' AND A.ICD_CODE = B.ICD_CODE ORDER BY DECODE (A.IO_TYPE, 'O',1,'M',2,'I',3) ";
//		System.out.println(icdCodeSql);
		TParm icdCodeParm = new TParm(TJDODBTool.getInstance().select(icdCodeSql));
		if(icdCodeParm.getValue("ICD_CHN_DESC", 0).equals("")){
			icdChnDesc = icdCodeParm.getValue("DESCRIPTION", 0);
		}else{
			icdChnDesc = icdCodeParm.getValue("ICD_CHN_DESC", 0);
		}
		return icdChnDesc;
	}

	/**
	 *
	 * @param jList
	 */
	private void doMouseClick(final WMatrix jList) {

		jList.addMouseListener(new MouseAdapter() {

			//点击
			public void mouseClicked(MouseEvent e) {

				if (jList.getSelectedIndex() != -1) {

					//单击
					if (e.getClickCount() == 1) {

						singleClick(jList);
					}

					//双击
					if (e.getClickCount() == 2) {

						doubleClick(jList.getSelectedValue());
					}
				}
			}

			//单击
			private void singleClick(WMatrix jList) {

				doProcessList2ListMouse(jList);
			}

			//双击
			private void doubleClick(Object value) {
     
				jList.setDoubleClick(value);
				jList.setSelectedIndex(-1);

				//
/*				WPanel dcvalue = (WPanel) jList.getDoubleClick();
				BaseCard tc = (BaseCard)dcvalue.getComponent(0);
				dcvalue.setToolTipText( tc.sName );*/
			}
		});
	}

	/**
	 * 从床头卡拖拽到第二个Table(转出列表)
	 * @param jList
	 * @param tb
	 */
	private void doTuoZhuai2TableInit(final WMatrix jList, final TTable tb) {

		//
		DropTargetUtil.doDropTarget(jList, tb, new IDropTarget() {

			@Override
			public void doDrop(String res, DropTargetDropEvent dtde) {

				// messageBox_(" ###  " + res);

				//Object obj = ((WPanel) jList.getSelectedValue()).getComponent(0);

				//选中床头卡拖拽
				//if( null!=obj ){


					if (null == jList.getDoubleClick()) {

						messageBox_(" 请双击选择一个床位!  ");
						return;
					}

					//双击的数据
					WPanel dcvalue = (WPanel) jList.getDoubleClick();
					BaseCard tc = (BaseCard)dcvalue.getComponent(0);

					//验证空床
			        if (TiString.isEmpty(tc.sMr_no)) {
			             messageBox_("请选择在院病患！");
			             doCancelClick(jList);
			             return;
			        }

	                //
		        	int check = messageBox("消息", "确定"+tc.sBed_no+"床病患转出科室吗？", 0);
					if (check!= 0) {
						//
						doCancelClick(jList);
						return;
					}

					//
                    inOutDept(tc);

					//
	       /*         inOutDept(tc.sCase_no,tc.sMr_no,tc.sCode,tc.sIpdNo,
	                		tc.sDeptCode,tc.sStation_code);*/

	               // messageBox_(" ###  " + tc.sDeptCode);
	               // messageBox_(" ###  " + tc.sStation_code);
				//}
			}
		});
	}

	/**
	 * 1. 从第一个table(待入床列表) 拖拽到 床头卡
	 * 2. 床头卡拖拽到床头卡
	 * @param list
	 */
	private void doTuoZhuai2ListInit(final TTable waitIn, final WMatrix list) {

		list.setDragEnabled(true);

		//
		DropTargetUtil.doDropTarget(waitIn, list, new IDropTarget() {

			@Override
			public void doDrop(String res, DropTargetDropEvent dtde) {

				//ListModel model = list.getModel();

				//messageBox_(  " ###2  " + res );

				//messageBox_(  " ###2-1  " + waitIn.getItemString(waitIn.getSelectedRow(),1));

				//messageBox_( " ^^^^^^^^ DoubleClick " +list.getDoubleClick() );

				if (null == list.getDoubleClick()) {

					messageBox_(" 请双击选择一个床位!  ");
					return;
				}

				/*
				messageBox_(  " ###选中的table行  " + waitIn.getSelectedRow() );

				waitIn.getModel().removeRow(waitIn.getSelectedRow());
				String [] str= {"21","32"+Math.random()};
				waitIn.getModel().addRow( str );
				 */

				//
				doBedCardDropTarget( waitIn,list );
			}
		});
	}

	/**
	 * 床头卡拖拽处理
	 * @param dcvalue
	 */
	private void doBedCardDropTarget(TTable waitIn,WMatrix list){

		//双击选中的Panel
		WPanel dcvalue = (WPanel) list.getDoubleClick();

		//当前单击选中的Panel
		//WPanel svalue = (WPanel) list.getSelectedValue();

		//当前点击选中的Table索引
		int tIndex = waitIn.getSelectedRow();

		//messageBox_(  " ###tIndex  " + tIndex );

		//双击的床头卡
		BaseCard c = (BaseCard) dcvalue.getComponent(0);
        if( TiString.isNotEmpty(c.sMr_no) ){
        	messageBox_(  " 请选择一个空床!  "  );
        	return;
        }

        //单击的床头卡
        // BaseCard sc = (BaseCard) svalue.getComponent(0);

        // messageBox_(  " tIndex "  +tIndex);
        // messageBox_(  " svalue " +svalue );


        //单击表格拖拽 (优先)
        if( tIndex>=0 ){

    		//
			String flg = newBaby.getValue("NEW_BORN_FLG", tIndex);

	        //新生儿的验证 #1
	        if(  flg.equals("Y") && !c.baby_bed_flg.equals("Y") ){
	        	 this.messageBox_("新生儿请选择婴儿床！");
	        	 this.doCancelClick(jList);
	        	 return;
	        }
	        // #2
//	        System.out.println("====newBabynewBaby is ::"+newBaby);
//	        System.out.println("1111flg flg flg is :"+flg);
//	        System.out.println("2222flg flg flg is :"+c.baby_bed_flg);
	        	if( !flg.equals("Y") && c.baby_bed_flg.equals("Y") ){
		        	 this.messageBox_("成人不可以入婴儿床！");
		        	 this.doCancelClick(jList);
		        	 return;
	        }
	        

        	//
        	int check = this.messageBox("消息", "确定此病患入到"+c.sBed_no+"床吗？", 0);
			if (check == 0) {
				doProcessTable2List(waitIn, list, dcvalue);
			}else{

				this.doCancelClick(list);
			}

        	return;
        }

        /**
        //其次同床头卡中拖拽
        if( null!=sc ){

        	int check = this.messageBox("消息", "确定此病患转床吗？", 0);
			if (check == 0) {
				doProcessList2List( (BaseCard)svalue.getComponent(0), c );
			}else{

				this.doCancelClick(list);
			}

        	return;
        }
        **/

	}


	/**
	 * 从表格托入床头卡
	 * @param waitIn
	 */
	private void doProcessTable2List(TTable waitIn, WMatrix list, WPanel dcvalue) {

		//String mrNo = waitIn.getItemString(waitIn.getSelectedRow(),0);
		//String caseNo = waitIn.getItemString(waitIn.getSelectedRow(),1);
		//String ipdNo = waitIn.getItemString(waitIn.getSelectedRow(),6);

		//
		BaseCard c = (BaseCard) dcvalue.getComponent(0);
		String bedNo = c.sBed_no;
		String mrNo = c.sMr_no;
		String code = c.sCode;

		//入床
		this.doInBed(mrNo, bedNo, code);
	}

	/**
	 * 床头卡某项托到床头卡另一项
	 * @param source
	 * @param target
	 */
	private void doProcessList2List(BaseCard source,BaseCard target){


		if (!doCheckBed_2(target.sCode)) {
			this.messageBox_("入床不成功!");
			return;
		}

        this.doChangeBed(source, target);
	}

	/**
	 * 床头卡某项翻牌到床头卡另一项 ( 双击目标+单击完成 )
	 * @param source
	 * @param target
	 */
	private void doProcessList2ListMouse(WMatrix jList){

		//双击选中的Panel
		WPanel dcvalue = (WPanel) jList.getDoubleClick();

		//当前单击选中的Panel
		WPanel svalue = (WPanel) jList.getSelectedValue();

		//双击首先不等于空时才起作用
		if( null!= dcvalue ){

			//单击的床头卡
			BaseCard sbc = (BaseCard)svalue.getComponent(0);
			//双击的床头卡
			BaseCard dbc = (BaseCard) dcvalue.getComponent(0);

			String MESSAGE_1 = "执行'入床'或'转床'操作时,应先选择空床位！";

		    //
	        if ( TiString.isEmpty(sbc.sMr_no) && TiString.isNotEmpty(dbc.sMr_no) ) {
	        	//双击的已经有人单击的再为空,应该先报先选空床位
	            this.messageBox_(MESSAGE_1);
	            this.doCancelClick(jList);
	            return;
	        }
	        if ( TiString.isNotEmpty(sbc.sMr_no) && TiString.isNotEmpty(dbc.sMr_no) ) {
	        	//双击和单击的双头卡不应该都有人,应该先报先选空床位
	            this.messageBox_(MESSAGE_1);
	            this.doCancelClick(jList);
	            return;
	        }
	        if ( TiString.isEmpty(sbc.sMr_no) ) {
	            this.messageBox_("请选择在院病患！");
	            this.doCancelClick(jList);
	            return;
	        }
	        if ( dbc.sMr_no.equals(sbc.sMr_no) ) {
	            this.messageBox_("请选择不同的病患！");
	            this.doCancelClick(jList);
	            return;
	        }

	        //新生儿的验证 #1
	        if( sbc.new_born_flg.equals("Y") && !dbc.baby_bed_flg.equals("Y") ){
	        	 this.messageBox_("新生儿请选择婴儿床！");
	        	 this.doCancelClick(jList);
	        	 return;
	        }
	        // #2
	        if( !sbc.new_born_flg.equals("Y") && dbc.baby_bed_flg.equals("Y") ){
	        	 this.messageBox_("成人不可以转到婴儿床！");
	        	 this.doCancelClick(jList);
	        	 return;
	        }
	        
	        //fux modify 20180111 id:5746 患者换床后长嘱调配药品信息出现两条
	        TParm parm = new TParm();

	        parm = new TParm();
			parm.setData("CASE_NO", sbc.sCase_no);
	        // 检查长期医嘱
	        if (OdiOrderTool.getInstance().getUDOrder(sbc.sCase_no)) {
	        	this.messageBox("该病患有未停用的长期医嘱，不允许转床。");
		        this.doCancelClick(jList);
		        return;
	            
	        }
	        // 检查护士审核
	        parm = new TParm();
	        parm.setData("CASE_NO", sbc.sCase_no);
	        if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
	        	this.messageBox("该病患有未审核的医嘱，不允许转床。");
		        this.doCancelClick(jList);
		        return;
	        }
	        // 检查护士执行
	        parm = new TParm();
	        parm.setData("CASE_NO", sbc.sCase_no);
	        if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
	        	this.messageBox("该病患有未执行的医嘱，不允许转床。");
		        	 this.doCancelClick(jList);
		        	 return;
	        }
	        parm = new TParm();
	        parm.setData("CASE_NO", sbc.sCase_no);
	        // 检查审药执行 true：有未审的药,false:没有未审的药
			if(InwForOutSideTool.getInstance().checkDrug(parm)){
				 this.messageBox( "药房有该病患未审核的医嘱，不允许转床。");
	        	 this.doCancelClick(jList);
	        	 return;
			}
			// 检查配药执行 true：有未配的药,false:没有未配的药
			parm = new TParm();
			parm.setData("CASE_NO", sbc.sCase_no);
			if(InwForOutSideTool.getInstance().exeDrug(parm)){
				this.messageBox( "药房有该病患未完成的配药，不允许转床。");
	        	this.doCancelClick(jList);
	        	return;
			}
			if (OdiOrderTool.getInstance().getRtnCfmM(sbc.sCase_no)) {
				this.messageBox( "该病患有药房退药未确认的信息，不允许转床。");
	        	this.doCancelClick(jList);
	        	return;
			}
			//
        	int check = this.messageBox("消息", "确定"+sbc.sBed_no+"床病患转床到"+dbc.sBed_no+"床吗？", 0);
			if (check == 0) {
				this.doProcessList2List( sbc , dbc );
			}else{
				this.doCancelClick(jList);
			}
		}

	}

	/**
	 * 安排病患入床
	 */
	private void doInBed(String mrNo, String bedNo, String code) {

		// messageBox_(  " ### caseNo  " + caseNo );
		// messageBox_(  " ### mrNo  " + mrNo );
		// messageBox_(  " ### ipdNo  " + ipdNo );
		// messageBox_(  " ### bedNo  " + bedNo );
		// messageBox_(  " ### code  " + code );

		// 得到待转入table
		TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		// 得到待转入DS
		TDataStore ds = waitIn.getDataStore();

		int waitIndex = waitIn.getSelectedRow();// 待转入表选中行号
		if (waitIndex < 0) {
			this.messageBox_("请选择要入住的病患!");
			return;
		}

		//System.out.println( newBaby.getValue("NEW_BORN_FLG", waitIndex) );


		//刷新检核
		/**
		if(!check()){
			return;
		}
		 **/
		if (!doCheckBed(bedNo, code)) {
			this.messageBox_("入床不成功!");
			return;
		}

		// 此患者待转入病区并非本病区
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase(
				ds.getItemString(waitIndex, "IN_STATION_CODE"))) {
			this.messageBox_("此患者待转入病区并非本病区");
			return;
		}

		// 得到待转入选中行的数据
		TParm updata = new TParm();

		/* ## */
		// 真正的床号
		updata.setData("BED_NO", code);
		/* ## */

		// 预约注记
		updata.setData("APPT_FLG", "N");
		// 占床注记
		updata.setData("ALLO_FLG", "Y");
		// 待转入病案号
		updata.setData("MR_NO", waitIn.getValueAt(waitIndex, 0));
		// 待转入就诊号
		updata.setData("CASE_NO", waitIn.getValueAt(waitIndex, 1));
		// 待转入住院号
		updata.setData("IPD_NO", waitIn.getValueAt(waitIndex, 6));
		// 占床注记
		updata.setData("BED_STATUS", "1");
        // 病患转出病区 wanglong add 20140728
        updata.setData("OUT_STATION_CODE", ds.getItemString(waitIndex, "OUT_STATION_CODE"));
        // 床位所在病区
        updata.setData("STATION_CODE", this.getValueString("STATION_CODE"));
        // 病患转出科室 wanglong add 20140728
        updata.setData("OUT_DEPT_CODE", ds.getItemString(waitIndex, "OUT_DEPT_CODE"));
        // 床位所在科室
        updata.setData("DEPT_CODE", ds.getItemString(waitIndex, "IN_DEPT_CODE"));

		updata.setData("OPT_USER", Operator.getID());
		updata.setData("OPT_TERM", Operator.getIP());
		// 检查病患是否包床
		if (checkOccu(waitIn.getValueAt(waitIndex, 1).toString())) {
			updata.setData("OCCU_FLG", "Y");// 表示该病患进行过包床操作
			// 如果该病患有包床 那么判断病患入住的床位是不是该病患指定的床位，如果不是要进行提醒，包床信息会被取消
			// 如果转入的床位的MR_NO为空或者与病患的MR_NO不相同 表示该床位不是病患指定的床位

			/**
			  // checkIn.getValueAt(checkIndex, 3)  病案号
			  // checkIn.getValueAt(checkIndex, 2)  床位号
			if (checkIn.getValueAt(checkIndex, 3) == null
					|| "".equals(checkIn.getValueAt(checkIndex, 3))
					|| !waitIn
							.getValueAt(waitIndex, 0)
							.toString()
							.equals(checkIn.getValueAt(checkIndex, 2)
									.toString()))
			 **/
			if (mrNo == null
					|| "".equals(mrNo)
					|| !waitIn.getValueAt(waitIndex, 0).toString()
							.equals(bedNo))

			{
				int check = this.messageBox("消息",
						"此病患已包床，不入住指定床位会取消该病患的包床，是否继续？", 0);
				if (check != 0) {
					return;
				}
				updata.setData("CHANGE_FLG", "Y");// 表示病患不入住到指定床位，清空该病患的包床信息
			} else {
				updata.setData("CHANGE_FLG", "N");// 表示病患入住到指定床位
			}
		} else {
			updata.setData("OCCU_FLG", "N");// 表示该病患没有包床
		}
		waitIn.removeRow(waitIndex);
		updata.setData("UPDATE", ds.getUpdateSQL());

		updata.setData("REGION_CODE", Operator.getRegion());

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onInSave", updata); // 入床保存
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			waitIn.retrieve();
			return;
		} else {
			this.messageBox("P0005");
			//## 暂时不调用  sendHL7Mes(updata);
			initInStation();

			//
			doRefreshWaitOutAll(false);
		}
	}

	/**
	 * 安排病患转床
	 * @param source 源数据
	 * @param target 目标数据
	 */
	private void doChangeBed(BaseCard source,BaseCard target) {

		// 此患者待转入病区并非本病区
		if (!this.getValueString("STATION_CODE").equalsIgnoreCase( source.sStation_code )) {
			this.messageBox_("此患者待转入病区并非本病区");
			return;
		}

		//
        TParm parm = new TParm();
        parm.setData("CASE_NO",  source.sCase_no );
        parm.setData("MR_NO", source.sMr_no );
        parm.setData("IPD_NO", source.sIpdNo );
        //查询病患住院信息
        TParm in_result = ADMInpTool.getInstance().selectall(parm);
        TParm initParm = new TParm();
        initParm.setRowData(in_result);

        //
        TParm newParm = new TParm();

        //##

        newParm.setData("MR_NO", source.sMr_no);
        newParm.setData("IPD_NO", source.sIpdNo);
        newParm.setData("PAT_NAME", source.sName);
        newParm.setData("SEX_CODE", source.sSex_code);
        newParm.setData("AGE", source.sAge);
        newParm.setData("DEPT_CODE", source.sDeptCode);
        newParm.setData("STATION_CODE", source.sStation_code);
        newParm.setData("VS_DR_CODE", source.sVs_dr_code);
        newParm.setData("ATTEND_DR_CODE", source.sAttend_dr_code);
        newParm.setData("DIRECTOR_DR_CODE", source.sDirector_dr_code);
        newParm.setData("PATIENT_CONDITION", source.sPatient_condition);
        newParm.setData("NURSING_CLASS", source.sNURSING_CLASS);
        //有为空的时候
        String vs_nurse_code = TiString.isNotEmpty(source.sVs_nurse_code)?source.sVs_nurse_code:"";
        newParm.setData("VS_NURSE_CODE", vs_nurse_code );

        //
        newParm.setData("PATIENT_STATUS", "");
        newParm.setData("DIE_CONDITION", "");
        newParm.setData("CARE_NUM", "");
        newParm.setData("IO_MEASURE", "");
        newParm.setData("ISOLATION", "");
        newParm.setData("TOILET", "");
        newParm.setData("ALLERGY","");

        //##

        newParm.setData("CASE_NO", source.sCase_no );
        newParm.setData("BED_NO", source.sCode  );

        //是否转床
        newParm.setData("BED", "Y");
        //新床号
        newParm.setData("TRAN_BED", target.sCode );
        //老床号
        newParm.setData("BED_NO_DESC",source.sCode  );

        //
        /**
        //是否过敏
        if(this.getValueBoolean("ALLERGY_Y")){
            newParm.setData("ALLERGY","Y");
        }else{
            newParm.setData("ALLERGY","N");
        }
        */

        newParm.setData("OPT_USER", Operator.getID());
        newParm.setData("OPT_TERM", Operator.getIP());
        TParm DATA = new TParm();
        DATA.setData("OLD_DATA", initParm.getData());
        DATA.setData("NEW_DATA", newParm.getData());
//        System.out.println("data=====转床保存的数据==========="+DATA);

        if (null != Operator.getRegion() &&
                Operator.getRegion().length() > 0){
            DATA.setData("REGION_CODE", Operator.getRegion());
        }
        TParm result = TIOM_AppServer.executeAction(
            "action.adm.ADMWaitTransAction",
            "changeDcBed", DATA); // 保存
        if (result.getErrCode() < 0){
            this.messageBox("执行失败！！"+result.getErrName()); //抢床错误提示
        }
        else
        {
            this.messageBox("P0005");
            //===liuf 传送给CIS转床的消息===
            //## 暂时不调用   sendMessage(newParm);

            ADMXMLTool.getInstance().creatXMLFile( source.sCase_no );

            //
            doRefreshWaitOutAll(false);
        }
	}

	/**
	 * 检核床位
	 * @return boolean
	 */
	private boolean doCheckBed(String bedNo, String code) {

		TTable waitTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");

		TParm inParm = new TParm();

		/** ## */
		inParm.setData("BED_NO", code);
		/** ## */

		TParm result = ADMInpTool.getInstance().QueryBed(inParm);
		String APPT_FLG = result.getCount() > 0 ? result
				.getValue("APPT_FLG", 0) : "";
		String ALLO_FLG = result.getCount() > 0 ? result
				.getValue("ALLO_FLG", 0) : "";
		String BED_STATUS = result.getCount() > 0 ? result.getValue(
				"BED_STATUS", 0) : "";
		if (ALLO_FLG.equals("Y")) {
			this.messageBox("此床已占用");
			onReload();
			return false;
		}
		if (BED_STATUS.equals("1")) {
			this.messageBox("此床已被包床");
			onReload();
			return false;
		}

		if (APPT_FLG.equals("Y")) {

			/**##
			if (!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(
					parm.getValue("BED_NO_DESC")))
			 **/
			if (!waitTable.getValueAt(waitTable.getSelectedRow(), 4).equals(
					bedNo)) {
				int check = this.messageBox("消息", "此床已被预订，是否进继续？", 0);
				if (check != 0) {
					onReload();
					return false;
				}
				return true;
			}

		}
		return true;
	}

	/**
	 * 检核床位
	 * @return boolean
	 */
	private boolean doCheckBed_2(String code) {

		TParm inParm = new TParm();

		/** ## */
		inParm.setData("BED_NO", code);
		/** ## */

		TParm result = ADMInpTool.getInstance().QueryBed(inParm);
		String APPT_FLG = result.getCount() > 0 ? result
				.getValue("APPT_FLG", 0) : "";
		String ALLO_FLG = result.getCount() > 0 ? result
				.getValue("ALLO_FLG", 0) : "";
		String BED_STATUS = result.getCount() > 0 ? result.getValue(
				"BED_STATUS", 0) : "";
		if (ALLO_FLG.equals("Y")) {
			this.messageBox("此床已占用");
			onReload();
			return false;
		}
		if (BED_STATUS.equals("1")) {
			this.messageBox("此床已被包床");
			onReload();
			return false;
		}


		return true;
	}

	/**
	 * 得到双击的床头卡并验证
	 * @return
	 */
	private BaseCard getBedCard(){

		if( null==jList ){
			return null;
		}

		//当前选中的床头卡
		WPanel dcvalue = (WPanel) jList.getDoubleClick();

		//
		if ( null == dcvalue ) {
			this.messageBox("请双击选择在院病患！");
			return null;
		}

		//
		BaseCard bc = (BaseCard) dcvalue.getComponent(0);
		//String bedNo = c.sBed_no;
		//String mrNo = c.sMr_no;
		String bedStatus = bc.sBedStatus;

        //
		if ( TiString.isEmpty( bc.sMr_no ) ) {
			this.messageBox("请选择在院病患！");
			return null;
		}

		if ( "3".equals(bedStatus) ) {
			this.messageBox_("此病床是包床，请选择病患实住病床！");
			return null;
		}

		if ( "0".equals(bedStatus) ) {
			this.messageBox("此病患未入住！");
			return null;
		}

		//
		return bc;
	}

	/**
	 * 转科管理
	 */
	private void onOutDept_new() {

		BaseCard c = this.getBedCard();
		if( null==c ){ return; }

		//
		TParm sendParm = new TParm();
		// 病案号
		sendParm.setData("MR_NO", c.sMr_no);
		// 住院号
		sendParm.setData("IPD_NO", c.sIpdNo );
		// 就诊号
		sendParm.setData("CASE_NO", c.sCase_no );
		// 姓名
		sendParm.setData("PAT_NAME", c.sName );
		// 性别
		sendParm.setData("SEX_CODE", c.sSex_code );
		// 年龄
		sendParm.setData("AGE", c.sAge );
		// 床号  update by sunqy 20140609 ----start----
//		String sql = "SELECT bed_no_desc FROM SYS_BED WHERE MR_NO = '"+c.sMr_no+"' AND BED_NO = '"+c.sBed_no+"'";
//		TParm parm = new TParm(TJDODBTool.getInstance().select(
//				sql));
//		System.out.println("------------sql-------------"+sql);
		sendParm.setData("BED_NO", c.sBed_no);
		// update by sunqy 20140609 ----end----
		// 科室
		sendParm.setData("OUT_DEPT_CODE", c.sDeptCode );
		// 病区
		sendParm.setData("OUT_STATION_CODE", c.sStation_code );
		//
		//this.messageBox("--sInDate111--"+c.sInDate);
		// 入院时间
		sendParm.setData("IN_DATE", StringTool.getTimestamp(c.sInDate,"yyyy/MM/dd HH:mm:ss") );

		this.openDialog("%ROOT%\\config\\adm\\ADMOutInp.x", sendParm);
		initInStation();
		TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");

		outTable.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT(
				this.getValueString("OUT_DEPT_CODE"),
				this.getValueString("OUT_STATION_CODE")));
		outTable.retrieve();

		creatDataStore("WAIT_OUT");

		//执行完成
		doRefreshBedCard();
	}

    /**
     * 转科保存(转出床位-新科室)
     */
    private void inOutDept(BaseCard tc){

    	if (!checkOutDate( tc.sCase_no,tc.sMr_no,
        		tc.sDeptCode,tc.sStation_code,true )) return;

    	TParm tp = new TParm();
    	tp.setData("CASE_NO",tc.sCase_no);
    	tp.setData("MR_NO", tc.sMr_no);
    	tp.setData("IPD_NO", tc.sIpdNo);
    	tp.setData("BED_NO",tc.sCode);

        //出的科室 是本身的科室
    	tp.setData("OUT_DEPT_CODE", tc.sDeptCode );
    	tp.setData("OUT_STATION_CODE", tc.sStation_code );

    	tp.setData("OUT_DATE", SystemTool.getInstance().getDate());//当前日期
    	tp.setData("PSF_KIND", "INDP");
    	tp.setData("OPT_USER", Operator.getID());
    	tp.setData("OPT_TERM", Operator.getIP());
    	tp.setData("REGION_CODE", Operator.getRegion());

        //
    	openDialog("%ROOT%\\config\\adm\\ADMOutBed.x",tp);

    	doRefreshWaitOutAll(true);
    }


	/**
	 * <暂停使用 -- 转到弹出层中>
     * 转科保存(转出床位-新科室)
     */
    private void inOutDept(String caseNo,String sMr_no,String bedNo,String ipdNo,
    		String sDeptCode,String sStationCode) {

    	//this.messageBox("验证"+sMr_no );
    	//this.messageBox("验证"+caseNo );
        //this.messageBox("验证"+checkOutDate( caseNo,sMr_no ));

        if (!checkOutDate( caseNo,sMr_no,sDeptCode,sStationCode,false )) return;

        //
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("MR_NO", sMr_no);
        parm.setData("IPD_NO", ipdNo);
        parm.setData("BED_NO",bedNo);
        //##
        //现在的逻辑是要入的科室(拖拽进去的) 是选的代储的病区和科室
        parm.setData("IN_DEPT_CODE", this.getValue("OUT_DEPT_CODE") );
        parm.setData("IN_STATION_CODE",this.getValue("OUT_STATION_CODE") );
        //出的科室 是本身的科室
        parm.setData("OUT_DEPT_CODE", sDeptCode );
        parm.setData("OUT_STATION_CODE", sStationCode );
        //##
        parm.setData("OUT_DATE", SystemTool.getInstance().getDate());//当前日期
        parm.setData("PSF_KIND", "INDP");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());

        parm.setData("REGION_CODE", Operator.getRegion());


 		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNo);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNo);
		parm.setData("ICU_FLG", IsICU);
		parm.setData("CCU_FLG", IsCCU);

        TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "onInOutSave", parm);
        if ("F".equals(result.getData("CHECK"))) {
            this.messageBox("次病患暂不能转科！");
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
        } else {
            this.messageBox("P0005");
            /*********************** HL7 *******************************/
            sendHL7Mes(parm, "ADM_TRAN");

            //
            doRefreshWaitOutAll(true);
        }
    }

    /**
     * 检核转科数据
     *
     * @return boolean
     */
    private boolean checkOutDate(String caseNo,String sMr_no,String sDeptCode,String sStationCode,boolean isPop) {

        if (TiString.isEmpty(sMr_no)) {
                this.messageBox_("请选择在院病患！");
                return false;
        }

        //
        if( !isPop ){
            if (this.getValue("OUT_DEPT_CODE") == null
                    || this.getValue("OUT_DEPT_CODE").equals("")) {
                    this.messageBox_("请选择转入科室！");
                    return false;
                }
                if (this.getValue("OUT_STATION_CODE") == null
                    || this.getValue("OUT_STATION_CODE").equals("")) {
                    this.messageBox_("请选择转入病区！");
                    return false;
                }
        }

        if ( TiString.isEmpty(sDeptCode)) {
            this.messageBox_(" 病患转出科室为空！");
            return false;
        }

        if ( TiString.isEmpty(sStationCode) ) {
            this.messageBox_(" 病患转出病区为空！");
            return false;
        }

        //

        TParm parm = new TParm();
        int re = 0;
        // 检查长期医嘱
        if (OdiOrderTool.getInstance().getUDOrder( caseNo )) {
            re = this.messageBox("提示", "该病患有未停用的长期医嘱，确认出科吗？", 0);
            if (re == 1) {
                return false;
            }
        }
        // 检查护士审核
        parm = new TParm();
        parm.setData("CASE_NO", caseNo );
        if (InwForOutSideTool.getInstance().checkOrderisCHECKTool(parm)) {
            re = this.messageBox("提示", "该病患有未审核的医嘱，确认出科吗?", 0);
            if (re == 1) {
                return false;
            }
        }
        // 检查护士执行
        parm = new TParm();
        parm.setData("CASE_NO",caseNo );
        if (InwForOutSideTool.getInstance().checkOrderisEXETool(parm)) {
            re = this.messageBox("提示", "该病患有未执行的医嘱，确认出科吗?", 0);
            if (re == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * hl7接口
     * @param parm TParm
     * @param type String
     */
    private void sendHL7Mes(TParm parm, String type) {

    	//System.out.println("parm:"+parm);
        String caseNo = parm.getValue("CASE_NO");
        //转科
        if (type.equals("ADM_TRAN"))
        {
            String InDeptCode = parm.getValue("IN_DEPT_CODE");
     		boolean IsICU = parm.getBoolean("ICU_FLG");
    		boolean IsCCU = parm.getBoolean("CCU_FLG");
    		//CIS
            if (InDeptCode.equals("0303")||InDeptCode.equals("0304")||IsICU||IsCCU)
            {
                List list = new ArrayList();
                parm.setData("ADM_TYPE", "I");
                parm.setData("SEND_COMP", "CIS");
                list.add(parm);
                TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
                if (resultParm.getErrCode() < 0)
                    this.messageBox(resultParm.getErrText());
            }
            //血糖
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());
        }
        //出院
        if (type.equals("ADM_OUT"))
        {
     		boolean IsICU = parm.getBoolean("ICU_FLG");
    		boolean IsCCU = parm.getBoolean("CCU_FLG");
    		//CIS
    		if (IsICU||IsCCU)
    		{
              List list = new ArrayList();
              parm.setData("ADM_TYPE", "I");
              parm.setData("SEND_COMP", "CIS");
              list.add(parm);
              TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
              if (resultParm.getErrCode() < 0)
                messageBox(resultParm.getErrText());
    		}
            //血糖
            List list = new ArrayList();
            parm.setData("ADM_TYPE", "I");
            parm.setData("SEND_COMP", "NOVA");
            list.add(parm);
            TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list, type);
            if (resultParm.getErrCode() < 0)
                this.messageBox(resultParm.getErrText());
        }
    }

    /**
     * 刷新待入待出列表
     */
    private void doRefreshWaitOutInTable(boolean isInit){

    	String station = Operator.getStation();
		// 待转入和待转出 Grid 赋值
		TTable WAIT_IN = (TTable) this.callFunction("UI|WAIT_IN|getThis");
		TTable WAIT_OUT = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		WAIT_IN.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_IN("", station));
		WAIT_IN.retrieve();
		WAIT_OUT.setSQL(ADMSQLTool.getInstance().getWAIT_TRANS_OUT("", ""));
		WAIT_OUT.retrieve();

		//
		this.onSelectIn();
		this.onSelectOut(isInit);
    }

    /**
	 *
	 */
	private void doRefreshWaitOutAll(boolean isInit) {

        //
        doRefreshWaitOutInTable(isInit);

        //
        doRefreshBedCard();
	}

	/**
	 * 刷新床头卡
	 */
	private void doRefreshBedCard() {

        jList.setDoubleClick(null);
		buildBedData();
	}

	/**
	 * 病患信息弹出层
	 */
	private void queryInStation_new(){

		BaseCard c = this.getBedCard();
		if( null==c ){
			this.doCancelClick(jList);
			return;
		}

		//
		TParm sendParm = new TParm();
		// 配参
		sendParm.setData("ADM", "ADM");
		// 病案号
		sendParm.setData("MR_NO", c.sMr_no );
		// 住院号
		sendParm.setData("IPD_NO", c.sIpdNo );
		// 就诊号
		sendParm.setData("CASE_NO", c.sCase_no );
		// 姓名
		sendParm.setData("PAT_NAME", c.sName );
		// 性别
		sendParm.setData("SEX_CODE", c.sSex_code );
		// 年龄
		sendParm.setData("AGE", c.sAge );
		// 床号
		sendParm.setData("BED_NO",c.sCode );
		// 科室
		sendParm.setData("DEPT_CODE",  c.sDeptCode );
		// 病区
		sendParm.setData("STATION_CODE", c.sStation_code );

		//特殊饮食 add by sunqy 20140527 
		String sql = "SELECT SPECIAL_DIET FROM SYS_PATINFO WHERE MR_NO='"+c.sMr_no+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		sendParm.setData("SPECIAL_DIET", parm.getValue("SPECIAL_DIET", 0));
		
		// 经治医师
		sendParm.setData("VS_DR_CODE", c.sVs_dr_code );

		// 主治医师
		sendParm.setData("ATTEND_DR_CODE", c.sAttend_dr_code );
		// 科主任
		sendParm.setData("DIRECTOR_DR_CODE", c.sDirector_dr_code );
		// 主管护士
		sendParm.setData("VS_NURSE_CODE", c.sVs_nurse_code );
		// 入院状态
		sendParm.setData("PATIENT_CONDITION", c.sPatient_condition );

		// BED_OCCU_FLG
		sendParm.setData("BED_OCCU_FLG", c.occupy_bed_flg );

		// 保存按钮状态
		sendParm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		this.openWindow(
				"%ROOT%\\config\\adm\\AdmPatinfo.x", sendParm);
		initInStation();
		//
		this.doCancelClick(jList);
	}

	/**
	 * 包床管理
	 */
	private void doOnBed_new(){

		BaseCard c = this.getBedCard();
		if( null==c ){
		    this.doCancelClick(jList);
			return;
		}

		//
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", c.sCase_no );
		sendParm.setData("MR_NO", c.sMr_no );
		sendParm.setData("IPD_NO", c.sIpdNo );
		sendParm.setData("DEPT_CODE", c.sDeptCode );
		sendParm.setData("STATION_CODE", c.sStation_code );
		sendParm.setData("BED_NO", c.sCode );
		TParm bed = new TParm();
		bed.setData("BED_NO", c.sCode );
		TParm check = SYSBedTool.getInstance().queryRoomBed(bed);
		String caseNo = c.sCase_no ;
		int count = check.getCount("BED_NO");
		boolean flg = false;
		for (int i = 0; i < count; i++) {
			if ("Y".equals(check.getData("ALLO_FLG", i))
					&& !caseNo.equals(check.getData("CASE_NO", i))) {
				flg = true;
			}
		}
		if (flg == true) {//modify by sunqy 20140522
			this.messageBox("此病房已有其他病患!");
			return;
//			int checkFlg = this.messageBox("消息", "此病房已有其他病患!是否继续包床？", 0);
//			if (checkFlg != 0){
//				this.doCancelClick(jList);
//				return;
//			}
		}

		this.openDialog("%ROOT%\\config\\adm\\ADMSysBedAllo.x", sendParm);//modify by huangjw 20140730

		//执行完成
		initInStation();
		chose();
		doRefreshBedCard();
	}

	/**
	 * 取消包床
	 */
	private void doOnCancelBed_new(){

		if( null==jList ){
			return ;
		}

		//当前选中的床头卡
		WPanel dcvalue = (WPanel) jList.getDoubleClick();

		//
		if ( null == dcvalue ) {
			this.messageBox("请双击选择在院病患！");
			return;
		}

		//
		BaseCard c = (BaseCard) dcvalue.getComponent(0);

        //
		if ( TiString.isEmpty( c.sMr_no ) ) {
			this.messageBox("请选择在院病患！");
			this.doCancelClick(jList);
			return;
		}


		if ( "0".equals(c.sBedStatus) ) {
			this.messageBox("此病患未入住！");
			return;
		}

		if ( !"3".equals(c.sBedStatus) ) {
			this.messageBox_("请选择被包床的病床！");
			this.doCancelClick(jList);
			return;
		}

		int re = this.messageBox("提示", "确认要取消该病患的包床吗？", 0);
		if (re != 0) {
			this.doCancelClick(jList);
			return;
		}
		TParm parm = new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", c.sCase_no );
		TParm result = SYSBedTool.getInstance().clearOCCUBed(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");

		//执行完成
		initInStation();
		chose();
		doRefreshBedCard();
	}

	/**
	 * 取消转科
	 */
	private void doOnCancelTrans_new() {

		TTable table = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
		int selectRow = table.getSelectedRow();
		if (selectRow < 0) {
			this.messageBox("请选择要取消转科的病患.");
			return;
		}
		String caseNo = (String) table.getValueAt(selectRow, 2);
		TParm parm = new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("DATE", SystemTool.getInstance().getDate());
		parm.setData("CASE_NO", caseNo);

		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMWaitTransAction", "onUpdateTransAndLog", parm);
		if (result.getErrCode() < 0) {
			messageBox("取消转科失败.");
		} else {
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");
			messageBox("取消转科成功.");

			//
			doRefreshBedCard();
		}
	}

	/**
	 * 取消住院
	 */
	private void doOnCancelInHospital_new(){

		if (!checkDate_BedCard()) return;

		//
		BaseCard c = this.getBedCard();

        //
		TParm result = new TParm();
		//=================执行取消住院操作
		int check = this.messageBox("消息", "是否取消？", 0);
		if (check == 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", c.sCase_no);
			parm.setData("PSF_KIND", "INC");
			parm.setData("PSF_HOSP", "");
			parm.setData("CANCEL_FLG", "Y");
			parm.setData("CANCEL_DATE", SystemTool.getInstance().getDate());
			parm.setData("CANCEL_USER", Operator.getID());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				parm.setData("REGION_CODE", Operator.getRegion());
			}
			result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
					"ADMCanInp", parm); //
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");
				initInStation();
				chose();

				//
				doRefreshBedCard();
			}
		}
	}

	/**
	 * 检核数据
	 *
	 * @return boolean
	 */
	private boolean checkDate_BedCard() {

		BaseCard c = this.getBedCard();
		if( null==c ){ return false; }

		//
		String caseNo = c.sCase_no;

		//
		TParm tpC = new TParm();
		tpC.setData("CASE_NO", caseNo);

		//==============预交金未退,不可取消住院,yanjing 20140728  start 
		TParm result = BILPayTool.getInstance().selBilPayLeft(caseNo);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrName());
			return false;
		}
		if (result.getDouble("PRE_AMT", 0) > 0) {
//			this.messageBox("此病患还有预交金未退,不可取消住院");
			if (messageBox("提示信息 Tips", "此病患还有预交金未退,是否继续取消住院? \n Are you cancel?",
					this.YES_NO_OPTION) != 0) {
			return false;
			}
		}
		//==============预交金未退,不可取消住院,yanjing 20140728  end 
		//==================以计费不可取消住院
		boolean checkflg = IBSOrdermTool.getInstance().existFee( tpC );
		if (!checkflg) {
			messageBox("已发生费用,请先退费");
			return false;
		}
		// 检查医生是否开立医嘱
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		if (this.checkOrderisEXIST(parm)) {
			this.messageBox("该病患已开立医嘱，不可取消住院！");
			callFunction("UI|save|setEnabled", false);
			return false;
		}

		return true;
	}

    /**
     * 传送CIS和血糖转床消息
     * @param parm
     */
    private void sendMessage(TParm parm) {

		// ICU、CCU注记
		String caseNO = parm.getValue("CASE_NO");
		boolean IsICU = SYSBedTool.getInstance().checkIsICU(caseNO);
		boolean IsCCU = SYSBedTool.getInstance().checkIsCCU(caseNO);
		// 转床
		String type = "ADM_TRAN_BED";
		parm.setData("ADM_TYPE", "I");
		// CIS
		if (IsICU || IsCCU) {
			List list = new ArrayList();
			parm.setData("SEND_COMP", "CIS");
			list.add(parm);
			TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(
					list, type);
			if (resultParm.getErrCode() < 0)
				messageBox(resultParm.getErrText());
		}
		// 血糖
		List list = new ArrayList();
		parm.setData("SEND_COMP", "NOVA");
		list.add(parm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
				type);
		if (resultParm.getErrCode() < 0)
			messageBox(resultParm.getErrText());
	}

    /**
     * 取消单击/双击
     * @param jList
     */
    private void doCancelClick(WMatrix jList){

		jList.setDoubleClick(null);
		jList.setSelectedIndex(-1);
    }

    /**
     * 单击待入表格入床
     */
    public void doInBed(){

    	TTable waitIn = (TTable) this.callFunction("UI|WAIT_IN|getThis");

    	this.doBedCardDropTarget(waitIn, jList);
    }
    
    /**
     * 调用患者保险信息维护页面 add by sunqy 20140516
     */
    public void onInsureInfo(){
    	BaseCard c = this.getBedCard();
    	TParm parm = new TParm();
    	parm.setData("MR_NO", c.sMr_no );
    	parm.setData("EDIT", "N");
    	this.openDialog(
				"%ROOT%\\config\\mem\\MEMInsureInfo.x", parm);
    }
    /**
     * 护理记录 add by sunqy 20140708
     */
    public void onNursingRec() {
    	BaseCard base = getBedCard();
    	//this.messageBox("===sInDate==="+base.sInDate);
    	//
    	Timestamp inDate = StringTool.getTimestamp(base.sInDate,"yyyy/MM/dd HH:mm:ss");
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "INW");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", base.sCase_no);
        parm.setData("PAT_NAME", base.sName);
        parm.setData("MR_NO", base.sMr_no);
        parm.setData("IPD_NO", base.sIpdNo);
        // parm.setData("CASE_NO",this.getCaseNo());
        // parm.setData("PAT_NAME",this.getPatName());
        // parm.setData("MR_NO",this.getMrNo());
        // parm.setData("IPD_NO",this.getIpdNo());
        parm.setData("ADM_DATE", inDate);
        parm.setData("DEPT_CODE", base.sDeptCode);
        parm.setData("RULETYPE", "2");
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.addListener("EMR_LISTENER", this, "emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);

    }
    /**
     * 体温表 add by xiongwg 20150128
     */
    public void onSelTWD() {
    	BaseCard twd = getBedCard();
    	TParm parm = new TParm();
        parm.setData("SUM","ADM_TYPE", "I");
        parm.setData("SUM","CASE_NO", twd.sCase_no);
        parm.setData("SUM","PAT_NAME", twd.sName);
        parm.setData("SUM","MR_NO", twd.sMr_no);
        parm.setData("SUM","IPD_NO", twd.sIpdNo);
        parm.setData("SUM","BED_NO", twd.sBed_no);
        parm.setData("SUM","STATION_CODE", twd.sStation_code);
        //System.out.println("parm::==="+parm);
        this.openWindow("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);

    }

    /**
	 * 取消入科
	 * add by yangjj 20150526
	 */
	public void  onCancleInDP(){
		
		BaseCard c = this.getBedCard();
		if( null==c ){
			return; 
		}
		
		String caseNo = c.sCase_no;
		TParm parm=new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm tranLogDept = ADMTransLogTool.getInstance().getTranDeptData(parm);
		if(tranLogDept.getCount()<=0){
			this.messageBox("查询转科记录错误");
			return;
		}
		if(!isEnableCancleInDP(tranLogDept.getRow(0))){
			return;
		}
		
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", c.sMr_no);
		sendParm.setData("IPD_NO", c.sIpdNo );
		sendParm.setData("CASE_NO", c.sCase_no );
		sendParm.setData("PAT_NAME", c.sName );
		sendParm.setData("SEX_CODE", c.sSex_code );
		sendParm.setData("AGE", c.sAge );
		sendParm.setData("BED_NO", c.sBed_no);
		sendParm.setData("OUT_DEPT_CODE", c.sDeptCode );
		sendParm.setData("OUT_STATION_CODE", c.sStation_code );
		sendParm.setData("IN_DATE", StringTool.getTimestamp(c.sInDate,"yyyy/MM/dd HH:mm:ss") );
		
		TParm trandParm=sendParm;
		trandParm.setData("OPT_USER", Operator.getID());
		trandParm.setData("OPT_TERM", Operator.getIP());
		TParm result = TIOM_AppServer.executeAction(
                "action.adm.ADMWaitTransAction", "onCancleInDP", trandParm);
		if(result.getErrCode()<0){
			this.messageBox("执行失败");
			return;
		}else{
			initInStation();
			TTable outTable = (TTable) this.callFunction("UI|WAIT_OUT|getThis");
			outTable.retrieve();
			creatDataStore("WAIT_OUT");	
			TTable inTable = (TTable) this.callFunction("UI|WAIT_IN|getThis");
			inTable.retrieve();
			creatDataStore("WAIT_IN");	
		    this.messageBox("执行成功");
		}
		onQueryData();
	}
	/**
	 * 验证取消入科
	 * @param caseNo
	 * @return
	 * add by yangjj 20150526
	 */
	public  boolean  isEnableCancleInDP(TParm parm){
		/*
		if(InwForOutSideTool.getInstance().checkOrderisExistExec(parm)){
			this.messageBox("存在入科后已执行的医嘱,不能取消入科");
			return false;
		}
		if(InwForOutSideTool.getInstance().checkOrderisExistCheck(parm)){
			this.messageBox("存在入科后已审核的医嘱,不能取消入科");
			return false;
		}
		*/
		if(InwForOutSideTool.getInstance().checkOrderisExist(parm)){
			this.messageBox("存在入科后已开立的医嘱,不能取消入科");
			return false;
		}
		TParm parmfee=InwForOutSideTool.getInstance().checkOrderFee(parm);
		if(parmfee.getDouble("TOT_AMT", 0)!=0){
			this.messageBox("入科后的总费用为:"+parmfee.getDouble("TOT_AMT", 0)+",不能取消入科");
			return false;
		}
		return true;
	}
	
	public void onChangeQuery(){
		this.messageBox("选择查询");
	}
	
	
}
