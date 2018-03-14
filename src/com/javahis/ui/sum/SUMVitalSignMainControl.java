package com.javahis.ui.sum;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import jdo.adm.ADMChgTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.erd.ERDForSUMTool;
import jdo.odi.ODICISVitalSignTool;
import jdo.sum.SUMVitalSignTool;
import jdo.sum.SUMXmlTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.base.TFrameBase;
import com.dongyang.ui.base.TTableCellEditor;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.javahis.ui.emr.AnimationWindowUtils;
import com.javahis.ui.emr.EMRTool;
import com.javahis.util.DateUtil;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;
import com.sun.awt.AWTUtilities;

/**
 * <p>
 * Title: 成人体温单
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: javahis
 * </p>
 * 
 * @author WangQing 20170306
 * 
 * @version 1.0
 */
public class SUMVitalSignMainControl extends TControl {

	TTable masterTable;// 体温记录table（主）
	TTable detailTable; // 体温明细table（细）
	int masterRow = -1;// 主table选中行号
	int detailRow = -1;// 细table选中行号
	TParm patInfo = new TParm();// 患者信息
	String admType = "I"; // 门急住别
	String caseNo = ""; // 看诊号
	TParm tprDtl = new TParm(); // 体温表数据
	TParm inParm = new TParm(); // 入参
	boolean isMroFlg = false;// 病案首页调用
	TParm p = new TParm();// 传参

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		// 20170531 liuyalin add
		Object object = this.getParameter();
		if (object instanceof TParm) {
			p = (TParm) object;
			// this.messageBox(p.getData("SUM", "CASE_NO")+"");
		}
		// 20170531 liuyalin add end
		masterTable = (TTable) this.getComponent("masterTable");// 初始化组件
		detailTable = (TTable) this.getComponent("detailTable");
		this.callFunction("UI|masterTable|addEventListener", "masterTable->"
				+ TTableEvent.CLICKED, this, "onMasterTableClicked");// table点击事件
		// this.callFunction(e, parameters);

		inParm = this.getInputParm();
		if (inParm != null) {
			admType = inParm.getValue("SUM", "ADM_TYPE");
			caseNo = inParm.getValue("SUM", "CASE_NO");
			onQuery();
			if (inParm.getValue("SUM", "FLG").equals("MRO")) {
				isMroFlg = true;
				hideFrame();// 隐藏界面
			}
		}
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// 记录时间
		this.setValue("TMPTRKINDCODE", "4");// 体温种类：腋温
	}

	/**
	 * 如果是MRO调用隐藏主面板
	 */
	public void hideFrame() {
		if (!(getComponent() instanceof TFrameBase))
			return;
		TFrame frame = (TFrame) getComponent();
		frame.setOpenShow(false);
		onPrint();
		frame.onClosed();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 1. 整理查询参数
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", admType);
		// 2.1 查询患者数据
		if ("E".equals(admType)) {
			patInfo = ERDForSUMTool.getInstance().selERDPatInfo(parm); // 病患信息
			patInfo.setData("ADM_DAYS", 0, this.getInHospDays(
					patInfo.getTimestamp("IN_DATE", 0),
					patInfo.getTimestamp("OUT_DATE", 0)));
		} else if ("I".equals(admType)) {
			patInfo = ADMTool.getInstance().getADM_INFO(parm);
			patInfo.setData("ADM_DAYS", 0,
					ADMTool.getInstance().getAdmDays(caseNo));
		}
		// 2.2 查询体温数据
		TParm result = SUMVitalSignTool.getInstance().selectExmDateUser(parm);// 体温记录
		// 3.更新页面
		// 初始化体温记录table
		for (int row = 0; row < result.getCount(); row++) {
			result.setData("EXAMINE_DATE", row, StringTool.getString(StringTool
					.getDate(result.getValue("EXAMINE_DATE", row), "yyyyMMdd"),
					"yyyy/MM/dd"));
			if ((row + 1) % 7 == 0) {// 每七天设置一个黄颜色
				masterTable.setRowColor(row, new Color(255, 255, 132));
			}
		}
		masterTable.getTable().repaint();
		masterTable.setParmValue(result);
		if (masterTable.getRowCount() > 0) {
			masterTable.setSelectedRow(masterTable.getRowCount() - 1);// 默认选中最后一行
			onMasterTableClicked(masterTable.getSelectedRow()); // 手动执行点击事件
		}
	}

	/**
	 * 获得住院天数
	 * 
	 * @param inDate
	 * @param outDate
	 * @return
	 */
	private int getInHospDays(Timestamp inDate, Timestamp outDate) {
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		Timestamp endDate = null;
		if (outDate == null)
			endDate = now;
		else
			endDate = outDate;
		int days = StringTool.getDateDiffer(endDate, inDate);
		if (days == 0)
			return 1;
		return days;
	}

	/**
	 * 行转列
	 * 
	 * @param tprDtl
	 *            TParm
	 * @return TParm
	 */
	public TParm rowToColumn(TParm tprDtl) {
		TParm result = new TParm();
		for (int i = 0; i < tprDtl.getCount(); i++) {
			result.addData("" + i, clearZero(tprDtl.getData("TEMPERATURE", i)));// 体温
			result.addData("" + i, clearZero(tprDtl.getData("PLUSE", i)));// 脉搏

			// modified by WangQing 20170228
			// 如果使用呼吸机，则不显示次数；否则，显示呼吸次数
			if (tprDtl.getData("SPECIALRESPIRENOTE", i) != null
					&& tprDtl.getValue("SPECIALRESPIRENOTE", i).equals("R")) {// 如果使用呼吸机
				result.addData("" + i, "R");// 使用呼吸机
			} else {
				result.addData("" + i, clearZero(tprDtl.getData("RESPIRE", i)));// 呼吸
			}
			/****************** shibl 20120330 modify ***************************/
			// result.addData("" + i,
			// clearZero(tprDtl.getData("SYSTOLICPRESSURE", i))); // 收缩压
			//
			// 2017.05.17 chenhj
			if (tprDtl.getData("SYSTOLICPRESSURE", i) == null) {
				result.addData("" + i, "");
			} else {
				result.addData("" + i, tprDtl.getValue("SYSTOLICPRESSURE", i));
			}
			if (tprDtl.getData("DIASTOLICPRESSURE", i) == null) {
				result.addData("" + i, "");
			} else {
				result.addData("" + i, tprDtl.getValue("DIASTOLICPRESSURE", i));
			}

			// result.addData("" + i,
			// clearZero(tprDtl.getData("DIASTOLICPRESSURE", i))); // 舒张压

			// modify by yangjj 20161219 项目管理4512//modify by machao 20170217
			// 项目管理4512
			if (tprDtl.getInt("HEART_RATE", i) == -9999) {
				result.addData("" + i, "");
			} else {
				result.addData("" + i, tprDtl.getData("HEART_RATE", i)); // 心率
			}

		}
		return result;
	}

	/**
	 * 新建
	 */
	public void onNew() {
		// 拿到服务器/数据库当前时间
		// String today =
		// StringTool.getString(TJDODBTool.getInstance().getDBTime(),
		// "yyyyMMdd");

		String today = (String) openDialog("%ROOT%\\config\\sum\\SUMTemperatureDateChoose.x");
		if (today.length() == 0) {
			messageBox("未选择测量时间");
			return;
		}
		// 判断是否已有当日数据
		for (int i = 0; i < masterTable.getRowCount(); i++) {
			if (today.equals(StringTool.getString(
					StringTool.getDate(
							masterTable.getItemString(i, "EXAMINE_DATE"),
							"yyyy/MM/dd"), "yyyyMMdd"))) {
				this.messageBox("已存在今天数据\n不可以新建");
				return;
			}
		}
		// 插入一条数据
		TParm MData = new TParm();
		MData.setData("EXAMINE_DATE",
				today.substring(0, 4) + "/" + today.substring(4, 6) + "/"
						+ today.substring(6));
		MData.setData("USER_ID", Operator.getName());
		// 默认选中新增行
		int newRow = masterTable.addRow(MData);
		masterTable.setSelectedRow(newRow);
		onMasterTableClicked(newRow); // 手动执行单击事件
		// 新建的时候写入当前时间
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// 住院天数
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// 记录时间
		this.setValue("TMPTRKINDCODE", "4");// 体温种类：腋温
		this.setValue("STOOL", "0");// 大便

		getOpeDays();
	}

	/**
	 * 体温记录table单击事件
	 */
	public void onMasterTableClicked(int row) {
		masterRow = row; // 主table选中行号
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", admType);
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXAMINE_DATE", StringTool.getString(StringTool.getDate(
				masterTable.getItemString(row, "EXAMINE_DATE"), "yyyy/MM/dd"),
				"yyyyMMdd"));
		// =========================主表数据
		TParm master = SUMVitalSignTool.getInstance().selectOneDateDtl(parm);
		this.clearComponent();// 清空组件
		// 向控件翻值
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// 住院天数
		this.setValue("OPE_DAYS", master.getValue("OPE_DAYS", 0));// 手术第几天
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);
		this.setValue("TMPTRKINDCODE", "4");// 体温种类：腋温
		// =========================细表数据

		String sql = "SELECT   B.*, CASE WHEN A.HEART_RATE IS NULL THEN -9999 ELSE A.HEART_RATE END AS HEART_RATE, "
				+ "A.ADM_TYPE,A.CASE_NO,A.EXAMINE_DATE,A.EXAMINESESSION,A.PHYSIATRICS,A.RECTIME,A.SPCCONDCODE,A.TMPTRKINDCODE,A.TEMPERATURE, "
				+
				// modified by WangQing 20170228
				// 新增 A.SPECIALRESPIRENOTE字段，代表使用呼吸机
				"A.PLUSE,A.RESPIRE, A.SPECIALRESPIRENOTE, TO_CHAR(A.SYSTOLICPRESSURE) AS SYSTOLICPRESSURE,TO_CHAR(A.DIASTOLICPRESSURE) AS DIASTOLICPRESSURE ,"
				+ "A.NOTPRREASONCODE,A.PTMOVECATECODE,A.PTMOVECATEDESC,A.USER_ID,A.OPT_USER, "
				+ "A.OPT_DATE,A.OPT_TERM "
				+ "FROM   SUM_VTSNTPRDTL A, SUM_VITALSIGN B "
				+ "WHERE  A.ADM_TYPE = '"
				+ admType
				+ "' "
				+ "AND A.CASE_NO = '"
				+ caseNo
				+ "' "
				+ "AND A.EXAMINE_DATE = '"
				+ StringTool.getString(StringTool.getDate(
						masterTable.getItemString(row, "EXAMINE_DATE"),
						"yyyy/MM/dd"), "yyyyMMdd")
				+ "' "
				+ "AND A.ADM_TYPE = B.ADM_TYPE "
				+ "AND A.CASE_NO = B.CASE_NO "
				+ "AND A.EXAMINE_DATE = B.EXAMINE_DATE "
				+ "AND B.DISPOSAL_FLG IS NULL " + "ORDER BY EXAMINESESSION ";
		// System.out.println("ma:::"+sql);
		tprDtl = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println(tprDtl);
		// 如果没有该天的数据插入空白行
		if (tprDtl.getCount("CASE_NO") <= 0) {
			// this.messageBox("======没有当日数据======");
			detailTable.removeRowAll();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			// return;
		} else {
			detailTable.setParmValue(rowToColumn(tprDtl));
		}

		// =========================细表下面的组件
		String stool = master.getValue("STOOL", 0);
		if (StringUtils.isEmpty(stool)) {
			stool = "0";
		}
		this.setValue("STOOL", stool);// 大便

		// modified by WangQing 20170238
		this.setValue("PB_TYPE", master.getValue("SPECIALSTOOLNOTE", 0));// 特殊排便
		// this.setValue("AUTO_STOOL", master.getValue("AUTO_STOOL", 0));// 自行排便

		this.setValue("ENEMA", master.getValue("ENEMA", 0));// 灌肠
		this.setValue("DRAINAGE", master.getValue("DRAINAGE", 0));// 引流
		this.setValue("INTAKEFLUIDQTY", master.getValue("INTAKEFLUIDQTY", 0));// 入量
		this.setValue("OUTPUTURINEQTY", master.getValue("OUTPUTURINEQTY", 0));// 出量
		this.setValue("WEIGHT", master.getValue("WEIGHT", 0));// 体重
		this.setValue("HEIGHT", master.getValue("HEIGHT", 0));// 身高
		this.setValue("USER_DEFINE_1", master.getValue("USER_DEFINE_1", 0));// 自定义一
		this.setValue("USER_DEFINE_1_VALUE",
				master.getValue("USER_DEFINE_1_VALUE", 0));
		this.setValue("USER_DEFINE_2", master.getValue("USER_DEFINE_2", 0));// 自定义二
		this.setValue("USER_DEFINE_2_VALUE",
				master.getValue("USER_DEFINE_2_VALUE", 0));
		this.setValue("USER_DEFINE_3", master.getValue("USER_DEFINE_3", 0));// 自定义三
		this.setValue("USER_DEFINE_3_VALUE",
				master.getValue("USER_DEFINE_3_VALUE", 0));
	}

	/**
	 * 体温明细table单击事件(通过界面注册法)
	 */
	public void onDTableFocusChange() {
		// PS:由于存在矩阵转换的问题，所以选中的列为数据PARM的行
		// 初始化细table被选中的行（即是table的列号）
		detailRow = detailTable.getSelectedColumn();
		((TComboBox) this.getComponent("EXAMINESESSION"))
				.setSelectedIndex(detailRow + 1);
		this.setValue("RECTIME", tprDtl.getValue("RECTIME", detailRow));// 记录时间
		if (tprDtl.getValue("RECTIME", detailRow).equals("")) {
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			this.setValue("RECTIME", now);// 记录时间
		}
		this.setValue("SPCCONDCODE", tprDtl.getValue("SPCCONDCODE", detailRow));// 体温变化特殊情况
		this.setValue("PHYSIATRICS", tprDtl.getValue("PHYSIATRICS", detailRow));// 物理降温
		this.setValue("TMPTRKINDCODE",
				tprDtl.getValue("TMPTRKINDCODE", detailRow));// 体温种类
		if (tprDtl.getValue("TMPTRKINDCODE", detailRow).equals("")) {
			this.setValue("TMPTRKINDCODE", "4");// 体温种类：腋温
		}
		this.setValue("NOTPRREASONCODE",
				tprDtl.getValue("NOTPRREASONCODE", detailRow));// 未量原因
		this.setValue("PTMOVECATECODE",
				tprDtl.getValue("PTMOVECATECODE", detailRow));// 病人动态
		this.setValue("PTMOVECATEDESC",
				tprDtl.getValue("PTMOVECATEDESC", detailRow));// 病人动态附注
	}

	/**
	 * === add by wukai on 20170210 自动带入 手术第几天 病人动态点击事件
	 */
	public void onPtmovecateSelected() {
		this.setValue("PTMOVECATEDESC", "");
		String code = this.getValueString("PTMOVECATECODE");
		// add by wangb 2017/05/25 入院于、转科于，时间自动带入
		if ("01".equals(code)) {
			this.setPatientTrends(code);
		} else if ("02".equals(code)) { // 手术
			getOpeDays();
		} else if ("03".equals(code)) {
			this.setPatientTrends(code);
		}
	}

	/**
	 * 获取手术天数
	 */
	private void getOpeDays() {
		// liuyalin modify 20170531
		// 1.获取masterTable中的检查日期，根据检查日期计算手术天数
		int row = masterTable.getSelectedRow();
		// this.messageBox_(row);
		if (row < 0) {
			return;
		}
		String vitalDate = masterTable.getItemString(row, "EXAMINE_DATE")
				.replaceAll("/", "");//this.messageBox(vitalDate+"");
		// this.messageBox_(vitalDate);
		if (StringUtils.isEmpty(vitalDate)) {
			return;
		}
		// 2.选择病人动态：“02手术” 获取时间
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", admType);
		parm.setData("STATE", "5");
		String code = this.getValueString("PTMOVECATECODE");
		String sql1 = "SELECT DISTINCT EXAMINE_DATE "
				+ "FROM SUM_VTSNTPRDTL A " + "WHERE PTMOVECATECODE = '02' "
				+ "" + "AND CASE_NO ='" + p.getData("SUM", "CASE_NO") + "' ORDER BY EXAMINE_DATE DESC";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		//System.out.println("22222222"+sql1);
		String vitalDate1="";
		String vitalDate2 ="";
		vitalDate2 = parm1.getValue("EXAMINE_DATE",0);
		if ("02".equals(code)) { // 手术
			parm.setData("DYNA_DATE", vitalDate2);
//			this.messageBox_(vitalDate2);
		} else {
			for (int i = 0; i < parm1.getCount("EXAMINE_DATE"); i++) {
				vitalDate1 = parm1.getValue("EXAMINE_DATE",i);
				parm.setData("DYNA_DATE",i, vitalDate1);
			}
//			this.messageBox_(parm.getValue("DYNA_DATE"));
		}//this.messageBox(parm+"");
		// 3.计算天数差
//		Timestamp t1 = Timestamp.valueOf(vitalDate);
//		Timestamp t2 = null;
		String t1 = vitalDate ;
		String t2 = "" ;
		//this.messageBox((Integer.parseInt("20170908") - Integer.parseInt("20170907"))+"");
		StringBuilder dayStr = new StringBuilder();
		for (int i = 0; i < parm.getCount("DYNA_DATE"); i++) {
			t2 = parm.getValue("DYNA_DATE", i);
			//int day = Integer.parseInt(t1) - Integer.parseInt(t2);
			int day = daysBetween(t2,t1);
			if (day > 0 && day <= 14) {
				dayStr.append(day+"/");
			}
		}
		String str = dayStr.toString();
		if (!StringUtils.isEmpty(str.toString())) {
			this.setValue("OPE_DAYS", str.substring(0, str.lastIndexOf("/")));
		}
		// // 3.计算天数差
		// Timestamp t1 = Timestamp.valueOf(vitalDate);
		// Timestamp t2 = null;
		// StringBuilder dayStr = new StringBuilder();
		// t2 = Timestamp.valueOf(parm.getValue("DYNA_DATE").substring(0, 10)
		// + " 00:00:00");
		// int day = StringTool.getDateDiffer(t1, t2);
		// // 判断术后时间是否在14天内
		// if (day >= 0 && day <= 14) {
		// // 判断是否为多台手术
		// if (StringUtils.isNotEmpty((String) this.getValue("OPE_DAYS"))) {
		// if ((day + "").equals(this.getValue("OPE_DAYS"))) {
		// this.setValue("OPE_DAYS", this.getValue("OPE_DAYS"));
		// } else {
		// dayStr.append(day + "/" + this.getValue("OPE_DAYS"));
		// String str = dayStr.toString();
		// if (!StringUtils.isEmpty(str.toString())) {
		// this.setValue("OPE_DAYS", str);
		// }
		// }
		// } else {
		// this.setValue("OPE_DAYS", day + "");
		// }
		// }
		// // 2.获取手术时间
		// TParm parm = new TParm();
		// parm.setData("CASE_NO", caseNo);
		// parm.setData("ADM_TYPE", admType);
		// parm.setData("STATE", "5");
		// parm = OPEOpBookTool.getInstance().selectOpBookForSum(parm);
		// // this.messageBox(parm+"");
		// if (parm.getCount("OP_DATE") <= 0 || parm.getErrCode() < 0) {
		// return;
		// }
		// // 3.计算天数差
		// Timestamp t1 = Timestamp.valueOf(vitalDate);
		// Timestamp t2 = null;
		// StringBuilder dayStr = new StringBuilder();
		// for (int i = 0; i < parm.getCount("OP_DATE"); i++) {
		// t2 = Timestamp.valueOf(parm.getValue("OP_DATE", i).substring(0, 10)
		// + " 00:00:00");
		// int day = StringTool.getDateDiffer(t1, t2);
		// if (day > 0 && day <= 14) {
		// dayStr.append(day + "/");
		// }
		// }
		// String str = dayStr.toString();
		// if (!StringUtils.isEmpty(str.toString())) {
		// this.setValue("OPE_DAYS", str.substring(0, str.lastIndexOf("/")));
		// }
	}

	public static final int daysBetween(String earlyStr, String lateStr) { 
	    Date early = new Date();
	    Date late =  new Date();
	    DateFormat df = DateFormat.getDateInstance(); 
	    
       try {    	   
    	   early = df.parse(new StringBuilder(earlyStr).insert(4, "-").insert(7, "-").toString());   
    	   late = df.parse(new StringBuilder(lateStr).insert(4, "-").insert(7, "-").toString());   
		} catch (ParseException e) {   
		        e.printStackTrace();   
		} 
	    
        java.util.Calendar calst = java.util.Calendar.getInstance();   
        java.util.Calendar caled = java.util.Calendar.getInstance();   
        calst.setTime(early);   
        caled.setTime(late);   
        //设置时间为0时   
        calst.set(java.util.Calendar.HOUR_OF_DAY, 0);   
        calst.set(java.util.Calendar.MINUTE, 0);   
        calst.set(java.util.Calendar.SECOND, 0);   
        caled.set(java.util.Calendar.HOUR_OF_DAY, 0);   
        caled.set(java.util.Calendar.MINUTE, 0);   
        caled.set(java.util.Calendar.SECOND, 0);   
        //得到两个日期相差的天数   
        int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst   
                .getTime().getTime() / 1000)) / 3600 / 24;   
         
        return days;   
   }
	
	// liuyalin modify 20170531 end

	/**
	 * 保存
	 */
	public boolean onSave() {
		masterTable.acceptText();
		detailTable.acceptText();
		
		// if (masterRow < 0 || masterTable.getSelectedRow() < 0) {
		// this.messageBox("请选择一条记录！");
		// return false;
		// }
		// this.messageBox("masterRow:::"+masterRow);
		//校验体重是数字
		String currentWeight = getValueString("WEIGHT");
		boolean flgg = true;
		if(!isNumeric(currentWeight)){
			flgg = false;
//			this.messageBox("体重请输入数字");
//			((TTextField) getComponent("WEIGHT")).grabFocus();
//			return false;
		}
		// 获取要保存的数据
		TParm saveParm = getValueFromUI();
		if (saveParm.getErrCode() < 0) {
			this.messageBox(saveParm.getErrText());
			return false;
		}
		
		// 身高、体重和体重变化-start
		String sql = " SELECT HEIGHT,WEIGHT FROM ADM_INP WHERE CASE_NO='"
				+ caseNo + "' ";
		TParm HWeightParmadm = new TParm(TJDODBTool.getInstance().select(sql));
		// 上一次的体重

		// 单独得到身高体重回写ADM_INP -start
		TParm HWeightParm = new TParm();
		try {
			if (!this.getValueString("HEIGHT").equals("")
					&& this.getValueDouble("HEIGHT") > 0) {
				HWeightParm.setData("HEIGHT", this.getValueDouble("HEIGHT"));
			} else {
				HWeightParm.setData("HEIGHT",
						HWeightParmadm.getDouble("HEIGHT", 0));
			}
		} catch (NumberFormatException e) {
			HWeightParm
					.setData("HEIGHT", HWeightParmadm.getDouble("WEIGHT", 0));
		}

		try {
			if (!this.getValueString("WEIGHT").equals("")
					&& this.getValueDouble("WEIGHT") > 0) {
				HWeightParm.setData("WEIGHT", this.getValueString("WEIGHT"));
			} else {
				HWeightParm.setData("WEIGHT",
						HWeightParmadm.getDouble("WEIGHT", 0));
			}
		} catch (NumberFormatException e) {
			HWeightParm
					.setData("WEIGHT", HWeightParmadm.getDouble("WEIGHT", 0));
		}
		// 单独得到身高体重回写ADM_INP -end
		HWeightParm.setData("CASE_NO", caseNo);
		// 身高、体重和体重变化-end

		// 年龄-start
		// add by wangqing 20180302 -start 解决门急诊体温单不能保存的问题 
		/*String sqlAge = "SELECT A.BIRTH_DATE "
				+ "FROM SYS_PATINFO A, ADM_INP B "
				+ " WHERE A.MR_NO = B.MR_NO AND B.CASE_NO = '" + caseNo + "'";*/
		String sqlAge = "";
		if(admType != null || admType.trim().length()>0){
			if(admType.equals("O") || admType.equals("E")){
				sqlAge = "SELECT A.BIRTH_DATE "
						+ "FROM SYS_PATINFO A, REG_PATADM B "
						+ " WHERE A.MR_NO = B.MR_NO AND B.CASE_NO = '" + caseNo + "'";
			}else if(admType.equals("I")){
				sqlAge = "SELECT A.BIRTH_DATE "
						+ "FROM SYS_PATINFO A, ADM_INP B "
						+ " WHERE A.MR_NO = B.MR_NO AND B.CASE_NO = '" + caseNo + "'";
			}else{
				this.messageBox("门急住类型为"+admType+"，尚不支持此类型的体温单保存操作");
				return false;
			}
		}else{
			this.messageBox("门急住类型不能为空");
			return false;
		}
		System.out.println("sqlAge="+sqlAge);
		// add by wangqing 20180302 -end

		Timestamp birth = new TParm(TJDODBTool.getInstance().select(sqlAge))
				.getTimestamp("BIRTH_DATE", 0);
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// 计算年龄
		String age = "0";
		if (birth != null)
			age = OdiUtil.showAge(birth, sysDate);
		else
			age = "";
		String currentAge = age.split("岁")[0];// String 的split()
		// 方法，按指定字符分割字符串，返回分割后的数组
		// 年龄-end
		
		// 判断体重是否是数字
		//boolean weightNumFlg = false;

		saveParm.setData("HW", HWeightParm.getData());
		// 根据保存日期、就诊号和门急住类型来判断是否已有该数据插入/更新
		String saveDate = saveParm.getParm("MASET").getValue("EXAMINE_DATE");
		// 得到左边table的数据
		TParm checkDate = new TParm();
		checkDate.setData("CASE_NO", caseNo);
		checkDate.setData("ADM_TYPE", admType);
		checkDate.setData("EXAMINE_DATE", saveDate);
		TParm existParm = SUMVitalSignTool.getInstance()
				.checkIsExist(checkDate);

		// ==================================================以上是整理数据，下面是插入或者更新数据=============================================
		// 1.没有该天数据，插入
		// 2.更新

		if (existParm.getCount() == 0) {// 插入
			String sqlWeight = "SELECT EXAMINE_DATE,WEIGHT,DISPOSAL_FLG "
					+ "FROM SUM_VITALSIGN " + "WHERE CASE_NO = '" + caseNo + "' "
					+ "AND (DISPOSAL_FLG <> 'Y' or DISPOSAL_FLG is null) "
					+ "ORDER BY EXAMINE_DATE DESC";//倒序排列
			
			TParm weightParm = new TParm(TJDODBTool.getInstance().select(sqlWeight));
			//System.out.println("aaaaaaaa"+sqlWeight);
			String lastWeight = "";
			//获取按照检查日期倒序排列中   有值并且是数字的数
			for (int i = 0; i < weightParm.getCount(); i++) {
				if (StringUtils.isEmpty((weightParm.getValue("WEIGHT", i))) 
						|| !isNumeric((weightParm.getValue("WEIGHT", i)))) {
					continue;
				}
				lastWeight = weightParm.getValue("WEIGHT", i);
				break;
			}
			
			//this.messageBox(lastWeight);
			boolean flg = true;
			if (StringUtils.isEmpty(lastWeight)) {
				flg = false;
				lastWeight = "0";
			}
			Double weightSub = 0.0;
			boolean flgNum = false;
			if(flgg && !(currentWeight == null || currentWeight.length() <= 0)){
				flgNum = true;
				weightSub = Double.parseDouble(lastWeight)
						- this.getValueDouble("WEIGHT");
			}
			
//			if (isNumeric(lastWeight)
//					&& isNumeric(this.getValueString("WEIGHT") + "")) {
//				// 是数字
//				weightNumFlg = true;
//				weightSub = Double.parseDouble(lastWeight)
//						- this.getValueDouble("WEIGHT");
//			} else {
//				// 不是数字
//				weightNumFlg = false;
//			}
			
			
			// Double 的 parseDouble() 方法 ，把字符串转换为Double
			if (flgg&& (Double.parseDouble(currentAge) <= 14)
					&& flg//上一个数为空  不走下面方法
					&& flgNum//填写的数为空  不走下面方法
					&& (weightSub >= 1.0 || weightSub <= -1.0)
					&& !(0 == this.messageBox("",
							"该患者未满14岁,并且该次体重与上次相比超过1kg，是否保存？",
							this.YES_NO_OPTION))) {
				return false;
			} else {
				saveParm.setData("I", true);
				// 调用action执行事务
				TParm result = TIOM_AppServer.executeAction(
						"action.sum.SUMVitalSignAction", "onSave", saveParm);
				// 调用保存
				if (result.getErrCode() < 0) {
					this.messageBox_(result);
					this.messageBox("插入失败！！！");
					return false;
				}
				this.messageBox("插入成功！！！");
				// 电视屏接口 wanglong add 20150527
				TParm xmlParm = ADMXMLTool.getInstance()
						.creatPatXMLFile(caseNo);
				if (xmlParm.getErrCode() < 0) {
					this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
				}
				onClear();
				return true;
			}

		} else {// 更新
			String sqlWei = "SELECT EXAMINE_DATE,WEIGHT,DISPOSAL_FLG "
					+ "FROM SUM_VITALSIGN "
					+ "WHERE CASE_NO = '"
					+ caseNo
					+ "' "
					+ "AND EXAMINE_DATE = '"
					+ StringTool.getString(StringTool.getDate(masterTable
							.getItemString(masterRow, "EXAMINE_DATE"),
							"yyyy/MM/dd"), "yyyyMMdd") + "' ";
			TParm p = new TParm(TJDODBTool.getInstance().select(sqlWei));
			Double weight = 0.0;

			//上次录入体重时为空
			boolean flgEmpty = true;
			boolean flgNum = false;
			if(StringUtil.isNullString(p.getValue("WEIGHT",0)) || !isNumeric(p.getValue("WEIGHT",0))){
				flgEmpty = false;
			}else{			
				//this.messageBox("1");
				if(flgg && !(currentWeight == null || currentWeight.length() <= 0)){
					flgNum = true;
					weight = Double.parseDouble(p.getValue("WEIGHT",0)) - this.getValueDouble("WEIGHT");
				}
			}
			//证明已经作废了
			boolean disFlg = true;
			Double weightDis = 0.0;
			if(!StringUtils.isEmpty(p.getData("DISPOSAL_FLG", 0) + "")){
				disFlg = false;

				String sqlWeight = "SELECT EXAMINE_DATE,WEIGHT,DISPOSAL_FLG "
						+ "FROM SUM_VITALSIGN " + "WHERE CASE_NO = '" + caseNo + "' "
						+ "AND (DISPOSAL_FLG <> 'Y' or DISPOSAL_FLG is null) "
						//+ "AND TRIM (TRANSLATE (NVL (WEIGHT, 'x'), '0123456789', ' ')) IS NULL "
						+ "AND REGEXP_LIKE (WEIGHT, '^-?[[:digit:],.]*$')"
						//+ "AND TRIM (WEIGHT) not like '% %'"
						+ "ORDER BY EXAMINE_DATE DESC";//倒序排列
				System.out.println("wwwww"+sqlWeight);
				TParm pDis = new TParm(TJDODBTool.getInstance().select(sqlWeight));
				
//				this.messageBox(flgg+"");
//				this.messageBox(!StringUtil.isNullString(pDis.getValue("WEIGHT",0))+"");
//				this.messageBox(isNumeric(pDis.getValue("WEIGHT",0))+"");
//				this.messageBox(weightDis+"");
				
				if(flgg && !(currentWeight == null || currentWeight.length() <= 0) && !StringUtil.isNullString(pDis.getValue("WEIGHT",0)) && (isNumeric(pDis.getValue("WEIGHT",0)))){
					
					weightDis = Double.parseDouble(pDis.getValue("WEIGHT",0)) - this.getValueDouble("WEIGHT");
				}
			}
			
				saveParm.setData("I", false);
				// 不等于0说明已经有存在的改天数据了--作废、没作废--更新动作
				if (existParm.getData("DISPOSAL_FLG", 0) != null
						&& existParm.getData("DISPOSAL_FLG", 0).equals("Y")
						&& !(0 == this.messageBox("", "该数据已经作废过，\n是否再确定保存？",
								this.YES_NO_OPTION))) {
					return false;
				} else {
					//没有作废的
					if(disFlg){
						if (flgg&& (Double.parseDouble(currentAge) <= 14)
								&& flgEmpty//上次为空的话  不走下面方法
								&& flgNum//本次写的还是空  不走下面方法
								&& (weight >= 1.0 || weight <= -1.0)
								&& !(0 == this.messageBox("",
										"该患者未满14岁,并且该次体重与上次相比超过1kg，是否保存？",
										this.YES_NO_OPTION))) {
							return false;
						}
						// 直接更新--DISPOSAL_FLG==null或者N
						TParm result = TIOM_AppServer
								.executeAction("action.sum.SUMVitalSignAction",
										"onSave", saveParm);
						// 调用保存
						if (result.getErrCode() < 0) {
							this.messageBox_(result);
							this.messageBox("更新失败！");
							return false;
						}
						this.messageBox("更新成功！");
						// 电视屏接口 wanglong add 20150527
						TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(
								caseNo);
						if (xmlParm.getErrCode() < 0) {
							this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
						}
						String nisActivety = TConfig.getSystemValue("NisActivety");
						if (nisActivety.equals("Y")) {
							TParm inParm = new TParm();
							inParm.setData("ADM_TYPE", admType);
							inParm.setData("CASE_NO", caseNo);
							inParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
							inParm.setData("EXAMINE_DATE", saveDate);
							TParm hl7Xml = SUMXmlTool.getInstance().onAssembleData(
									inParm);
							if (hl7Xml.getErrCode() < 0) {
								this.messageBox("NIS接口发送失败: " + hl7Xml.getErrText());
							}
						}
						onClear();
						return true;
					}else{//作废的
						
//						this.messageBox((Double.parseDouble(currentAge) <= 14)+"");
//						this.messageBox(flgNum+"");
//						this.messageBox(weightDis+"");
						if ((Double.parseDouble(currentAge) <= 14)
								//&& flgEmpty//上次为空的话  不走下面方法
								&& flgNum//本次写的还是空  不走下面方法
								&& (weightDis >= 1.0 || weightDis <= -1.0)
								&& !(0 == this.messageBox("",
										"该患者未满14岁,并且该次体重与上次相比超过1kg，是否保存？",
										this.YES_NO_OPTION))) {
							return false;
						}
						// 直接更新--DISPOSAL_FLG==null或者N
						TParm result = TIOM_AppServer
								.executeAction("action.sum.SUMVitalSignAction",
										"onSave", saveParm);
						// 调用保存
						if (result.getErrCode() < 0) {
							this.messageBox_(result);
							this.messageBox("更新失败！");
							return false;
						}
						this.messageBox("更新成功！");
						// 电视屏接口 wanglong add 20150527
						TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(
								caseNo);
						if (xmlParm.getErrCode() < 0) {
							this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
						}
						String nisActivety = TConfig.getSystemValue("NisActivety");
						if (nisActivety.equals("Y")) {
							TParm inParm = new TParm();
							inParm.setData("ADM_TYPE", admType);
							inParm.setData("CASE_NO", caseNo);
							inParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
							inParm.setData("EXAMINE_DATE", saveDate);
							TParm hl7Xml = SUMXmlTool.getInstance().onAssembleData(
									inParm);
							if (hl7Xml.getErrCode() < 0) {
								this.messageBox("NIS接口发送失败: " + hl7Xml.getErrText());
							}
						}
						onClear();
						return true;
					}
				}
		}

	}

	/**
	 * 判断是否为数字
	 */
	public static boolean isNumeric(String str) {
		String s = str;
		String regex = "^[+-]?\\d+(\\.\\d+)?$";
		if (s == null || s.length() <= 0 || s.matches(regex) == true) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存：从控件上面获得值，放入大对象，以被两个TDS使用
	 */
	public TParm getValueFromUI() {
		TParm saveData = new TParm();
		TParm masterParm = new TParm();
		TParm detailParm = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String examineDate = StringTool.getString(StringTool.getDate(
				masterTable.getItemString(masterRow, "EXAMINE_DATE"),
				"yyyy/MM/dd"), "yyyyMMdd");
		masterParm.setData("ADM_TYPE", admType);
		masterParm.setData("CASE_NO", caseNo);
		masterParm.setData("EXAMINE_DATE", examineDate);// 检查日期
		masterParm.setData("IPD_NO", patInfo.getValue("IPD_NO", 0));
		masterParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
		masterParm
				.setData("INHOSPITALDAYS", this.getValueInt("INHOSPITALDAYS"));// 住院天数
		masterParm.setData("OPE_DAYS", this.getValue("OPE_DAYS"));// 术后天数
		masterParm.setData("ECTTIMES", "");// 目前ECT次数（暂时没用）
		masterParm.setData("MCFLG", "");// 月经（暂时没用）
		masterParm.setData("HOURSOFSLEEP", "");// 睡眠时数-小时（暂时没用）
		masterParm.setData("MINUTESOFSLEEP", "");// 睡眠时数-分（暂时没用）
		masterParm.setData("INTAKEDIETQTY", "");// 输入-饮食量（暂时没用）
		masterParm.setData("OUTPUTDRAINQTY", "");// 排出-引流量（暂时没用）
		masterParm.setData("OUTPUTOTHERQTY", "");// 排出-其它（暂时没用）
		masterParm.setData("BATH", "");// 洗澡（暂时没用）
		masterParm.setData("GUESTKIND", "");// 会客（暂时没用）
		masterParm.setData("STAYOUTSIDE", "");// 外宿（暂时没用）
		masterParm.setData("LEAVE", "");// 外出（暂时没用）
		masterParm.setData("LEAVEREASONCODE", "");// 外出原因代码（暂时没用）
		masterParm.setData("NOTE", "");// 备注（暂时没用）
		masterParm.setData("STATUS_CODE", "");// 病历表单状态（暂时没用）
		masterParm.setData("DISPOSAL_FLG", "");// 作废注记
		masterParm.setData("DISPOSAL_REASON", "");// 作废理由
		// modify by yangjj 20151111
		masterParm.setData("STOOL", this.getValueString("STOOL"));// 大便
		// modified by WangQing 20170228
		masterParm.setData("SPECIALSTOOLNOTE", this.getValueString("PB_TYPE"));// 特殊排便情况
		masterParm.setData("AUTO_STOOL", this.getValue("AUTO_STOOL"));// 自行排便
		masterParm.setData("ENEMA", this.getValue("ENEMA"));// 灌肠

		masterParm.setData("DRAINAGE", this.getValue("DRAINAGE"));// 引流
		masterParm.setData("INTAKEFLUIDQTY",
				this.getValueDouble("INTAKEFLUIDQTY"));// 入量-注射
		masterParm.setData("OUTPUTURINEQTY",
				this.getValueDouble("OUTPUTURINEQTY"));// 出量-小便量
		masterParm.setData("WEIGHT", this.getValue("WEIGHT"));// 体重
		masterParm.setData("HEIGHT", this.getValueString("HEIGHT"));// 身高
		masterParm.setData("USER_DEFINE_1", this.getValue("USER_DEFINE_1"));// 自定义一
		masterParm.setData("USER_DEFINE_1_VALUE",
				this.getValue("USER_DEFINE_1_VALUE"));
		masterParm.setData("USER_DEFINE_2", this.getValue("USER_DEFINE_2"));// 自定义二
		masterParm.setData("USER_DEFINE_2_VALUE",
				this.getValue("USER_DEFINE_2_VALUE"));
		masterParm.setData("USER_DEFINE_3", this.getValue("USER_DEFINE_3"));// 自定义三
		masterParm.setData("USER_DEFINE_3_VALUE",
				this.getValue("USER_DEFINE_3_VALUE"));
		masterParm.setData("USER_ID", Operator.getID());// 记录人员
		masterParm.setData("OPT_USER", Operator.getID());
		masterParm.setData("OPT_DATE", now);
		masterParm.setData("OPT_TERM", Operator.getIP());
		String columnIndex = this.getValueString("EXAMINESESSION");
		for (int i = 0; i < 6; i++) {// 时段有6个
			TParm oneParm = new TParm();
			oneParm.setData("ADM_TYPE", admType);
			oneParm.setData("CASE_NO", caseNo);
			oneParm.setData("EXAMINE_DATE", examineDate);
			oneParm.setData("EXAMINESESSION", i);
			if (("" + i).equals(columnIndex)) {
				oneParm.setData("RECTIME", this.getText("RECTIME"));// 记录时间
				oneParm.setData("SPCCONDCODE", this.getValue("SPCCONDCODE"));// 体温变化特殊情况
				oneParm.setData("PHYSIATRICS", this.getValue("PHYSIATRICS"));// 物理降温
				oneParm.setData("TMPTRKINDCODE", this.getValue("TMPTRKINDCODE"));// 体温种类
				oneParm.setData("NOTPRREASONCODE",
						this.getValue("NOTPRREASONCODE"));// 未量原因
				oneParm.setData("PTMOVECATECODE",
						this.getValue("PTMOVECATECODE"));// 病人动态
				// liuyalin 20170531 modify
				// if (!StringUtil.isNullString(this
				// .getValueString("PTMOVECATECODE"))
				// && StringUtil.isNullString(this
				// .getValueString("PTMOVECATEDESC"))) {
				// TParm errParm = new TParm();
				// errParm.setErr(-1, "请填写病人动态附注");
				// return errParm;
				// }
				// liuyalin 20170531 modify end
				oneParm.setData("PTMOVECATEDESC",
						this.getValue("PTMOVECATEDESC"));// 病人动态附注
			} else {
				oneParm.setData("RECTIME", tprDtl.getValue("RECTIME", i));// 记录时间
				oneParm.setData("SPCCONDCODE",
						tprDtl.getValue("SPCCONDCODE", i));// 体温变化特殊情况
				oneParm.setData("PHYSIATRICS",
						tprDtl.getValue("PHYSIATRICS", i));// 物理降温
				// wanglong modify 20140428
				oneParm.setData(
						"TMPTRKINDCODE",
						tprDtl.getValue("TMPTRKINDCODE", i).equals("") ? this
								.getValue("TMPTRKINDCODE") : tprDtl.getValue(
								"TMPTRKINDCODE", i));
				oneParm.setData("NOTPRREASONCODE",
						tprDtl.getValue("NOTPRREASONCODE", i));// 未量原因
				oneParm.setData("PTMOVECATECODE",
						tprDtl.getValue("PTMOVECATECODE", i));// 病人动态
				oneParm.setData("PTMOVECATEDESC",
						tprDtl.getValue("PTMOVECATEDESC", i));// 病人动态附注
			}
			// 得到table上的主数据
			oneParm.setData("TEMPERATURE",
					TCM_Transform.getDouble(detailTable.getValueAt(0, i)));// 体温
			oneParm.setData("PLUSE",
					TCM_Transform.getDouble(detailTable.getValueAt(1, i)));// 脉搏
			// add by chenhj start
			oneParm.setData("SYSTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(3, i)));// 收缩压
			oneParm.setData("DIASTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(4, i)));// 舒张压
			// add by chenhj end
			// modified by WangQing 20170228 -start
			// 新增呼吸机标志
			if (TCM_Transform.getString(detailTable.getValueAt(2, i)).equals(
					"R")
					|| TCM_Transform.getString(detailTable.getValueAt(2, i))
							.equals("r")) {
				oneParm.setData("SPECIALRESPIRENOTE", "R");// 使用呼吸机
				oneParm.setData("RESPIRE", 0.0);// 呼吸
			} else {
				oneParm.setData("RESPIRE",
						TCM_Transform.getDouble(detailTable.getValueAt(2, i)));// 呼吸
				oneParm.setData("SPECIALRESPIRENOTE", "");// 使用呼吸机
			}
			// modified by WangQing 20170228 -end

			/****************** shibl 20120330 modify ***************************/
			double systolicPressure = TCM_Transform.getDouble(detailTable
					.getValueAt(3, i));
			double diastolicPressure = TCM_Transform.getDouble(detailTable
					.getValueAt(4, i));
			if (diastolicPressure > systolicPressure) {// wanglong add 20141022
				return TParm.newErrParm(-1, "舒张压不能大于收缩压");
			}
			oneParm.setData("SYSTOLICPRESSURE", systolicPressure);// 收缩压
			oneParm.setData("DIASTOLICPRESSURE", diastolicPressure);// 舒张压

			// this.messageBox((String)(detailTable.getValueAt(5, i)+""));
			// add by machao 20170227 页面心率不填时，保存为空值
			if (!StringUtils
					.isEmpty((String) (detailTable.getValueAt(5, i) + ""))) {
				// this.messageBox("非空");
				oneParm.setData("HEART_RATE",
						TCM_Transform.getDouble(detailTable.getValueAt(5, i)));// 心率
			} else {
				// this.messageBox("心率空");
				oneParm.setData("HEART_RATE", new TNull(Double.class));// 心率
			}
			// add by chenhj 20170512 页面血压不填时，保存为空值
			if (!StringUtils
					.isEmpty((String) (detailTable.getValueAt(3, i) + ""))) {// 收缩压
				oneParm.setData("SYSTOLICPRESSURE",
						TCM_Transform.getDouble(detailTable.getValueAt(3, i)));
			} else {
				// this.messageBox("空");
				oneParm.setData("SYSTOLICPRESSURE", new TNull(Double.class));
			}
			if (!StringUtils
					.isEmpty((String) (detailTable.getValueAt(4, i) + ""))) {// 舒张压

				oneParm.setData("DIASTOLICPRESSURE",
						TCM_Transform.getDouble(detailTable.getValueAt(4, i)));
			} else {
				// this.messageBox("空");
				oneParm.setData("DIASTOLICPRESSURE", new TNull(Double.class));
			}

			// oneParm.setData("HEART_RATE",
			// TCM_Transform.getDouble(detailTable.getValueAt(5, i)));// 心率
			// System.out.println("ma123:"+detailTable.getValueAt(5, i));
			oneParm.setData("USER_ID", Operator.getID());
			oneParm.setData("OPT_USER", Operator.getID());
			oneParm.setData("OPT_DATE", now);
			oneParm.setData("OPT_TERM", Operator.getIP());
			detailParm.setData(i + "PARM", oneParm.getData());
			detailParm.setCount(i + 1);
		}
		saveData.setData("MASET", masterParm.getData());
		saveData.setData("DETAIL", detailParm.getData());
		return saveData;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		// 重新设置全局变量
		masterRow = -1;
		detailRow = -1;
		this.clearComponent();
		detailTable.removeRowAll();
		onQuery();// 执行查询
	}

	/**
	 * 清空组件
	 */
	public void clearComponent() {
		// 清理上半部分
		this.clearValue("EXAMINESESSION;RECTIME;" // INHOSPITALDAYS住院天数不清空;OPE_DAYS术后天数不清空
				+ "SPCCONDCODE;PHYSIATRICS;"// TMPTRKINDCODE体温种类不清空
				+ "NOTPRREASONCODE;PTMOVECATECODE;PTMOVECATEDESC");
		// 清理下半部分
		this.clearValue("STOOL;INTAKEFLUIDQTY;OUTPUTURINEQTY;WEIGHT;HEIGHT;"
				+ "USER_DEFINE_1;USER_DEFINE_2;USER_DEFINE_3;USER_DEFINE_1_VALUE;USER_DEFINE_2_VALUE;USER_DEFINE_3_VALUE");
	}

	/**
	 * 作废
	 */
	public void onDefeasance() {
		int selRow = masterTable.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请选中作废数据！");
			return;
		}
		// 作废原因
		String value = (String) this
				.openDialog("%ROOT%\\config\\sum\\SUMDefeasance.x");
		if (value == null)
			return;
		// 得到选中行的EXAMINE_DATE
		String examineDate = StringTool.getString(
				StringTool.getDate(
						masterTable.getItemString(selRow, "EXAMINE_DATE"),
						"yyyy/MM/dd"), "yyyyMMdd");
		String defSel = "SELECT * FROM SUM_VITALSIGN WHERE ADM_TYPE = '"
				+ admType + "' AND CASE_NO = '" + caseNo
				+ "' AND EXAMINE_DATE = '" + examineDate + "'";
		TDS defData = new TDS();
		defData.setSQL(defSel);
		defData.retrieve();
		defData.setItem(0, "DISPOSAL_REASON", value);
		defData.setItem(0, "DISPOSAL_FLG", "Y");
		if (!defData.update()) {
			this.messageBox("作废失败！");
			return;
		}
		this.messageBox("作废成功！");
		onClear();
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (masterTable.getRowCount() <= 0) {
			this.messageBox("没有打印数据！");
			return;
		}
		// 暂时没用
		TParm prtForSheetParm = new TParm();
		// 获得打印的区间
		TParm parmDate = new TParm();
		// 入院日期时间
		Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
		parmDate.setData("IN_DATE", inDate);
		TParm value = (TParm) this.openDialog(
				"%ROOT%\\config\\sum\\SUMChoiceDate.x", parmDate);
		if (value == null) {
			prtForSheetParm.setData("STOP", "取消打印！");
			return;
		}
		// 得到选择时间之间的‘天数差’+1===>打印的天数
		int differCount = StringTool.getDateDiffer(
				value.getTimestamp("END_DATE"),
				value.getTimestamp("START_DATE")) + 1;
		if (differCount <= 0) {
			prtForSheetParm.setData("STOP", "查询区域错误！");
			return;
		}
		// 打印的报表数
		int pageCount = differCount / 7 + 1;
		if (differCount % 7 == 0)
			pageCount = differCount / 7;

		Timestamp forDate = null;
		int pageNo = 1;
		for (int i = 0; i < pageCount; i++) {
			// 封装了开始日期和结束日期
			TParm parm = new TParm();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (i == 0)
				startDate = value.getTimestamp("START_DATE");
			else
				startDate = StringTool.rollDate(forDate, 1);
			int dif = 6;
			// if(i % 7 == 0)
			// dif = 6;
			// else
			// dif = 7 - (i * 7 - differCount) - 1;
			endDate = StringTool.rollDate(startDate, dif);
			forDate = endDate;
			parm.setData("START_DATE", startDate);
			parm.setData("END_DATE", endDate);
			// 打印的数据
			TParm printData = getValueForPrt(parm, dif + 1, i + 1);// 关键步骤1
			if (printData == null)
				continue;
			printData.setData("PAGENO", "TEXT", pageNo++);
			if (printData.getData("STOP") != null) {
				this.messageBox(printData.getValue("STOP"));
				return;
			}
			// 加入体温单上传EMR
			Object returnObj = null;
			if ("E".equals(admType))
				returnObj = openPrintDialog(
						"%ROOT%\\config\\prt\\sum\\SUMVitalSign_PrtSheetE.jhw",
						printData);
			else if ("I".equals(admType))
				returnObj = this.openPrintDialog(
						"%ROOT%\\config\\prt\\sum\\SUMVitalSign_PrtSheet.jhw",
						printData);
			if (returnObj != null) {
				String mr_no = patInfo.getValue("MR_NO", 0);
				EMRTool emrTool = new EMRTool(this.caseNo, mr_no, this);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String fileName = "体温单" + format.format(startDate) + "-"
						+ format.format(endDate);
				// ======== modify by chenxi 20120702 false表示体温单打印时只保存一笔
				emrTool.saveEMR(returnObj, fileName, "EMR100002",
						"EMR10000202", false);
			}
		}
	}

	/**
	 * 得到UI上的参数给打印程序
	 * 
	 * @param value
	 *            TParm 封装了开始日期和结束日期
	 * @param differCount
	 *            int 几列
	 * @param pageNo
	 *            int 页码
	 * @return
	 */
	private TParm getValueForPrt(TParm value, int differCount, int pageNo) {
		TParm prtForSheetParm = new TParm();
		// 获得生命标记数据
		Vector tprSign = getVitalSignDate(value);// 关键步骤2
		if (((TParm) tprSign.get(0)).getCount() <= 0)
			return null;
		// 打印核心算法，将数据转化成坐标
		prtForSheetParm = dataToCoordinate(tprSign, differCount);// 关键步骤3
		String stationString = "";
		if ("E".equals(admType))
			stationString = patInfo.getValue("ERD_REGION_DESC", 0);
		else if ("I".equals(admType)) {
			String stationCode = patInfo.getValue("STATION_CODE", 0);
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
							+ stationCode + "'"));
			stationString = parm.getValue("STATION_DESC", 0);
		}
		String mrNo = patInfo.getValue("MR_NO", 0);
		// 通过MR_NO拿到性别
		Pat pat = Pat.onQueryByMrNo(mrNo);
		String sex = pat.getSexString();
		String ipdNo = patInfo.getValue("IPD_NO", 0);
		String bedNo = patInfo.getValue("BED_NO", 0);
		String bedString = "";
		if ("E".equals(admType))
			bedString = patInfo.getValue("BED_DESC", 0);
		else if ("I".equals(admType)) {
			TParm bedParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT BED_NO_DESC FROM SYS_BED WHERE BED_NO='" + bedNo
							+ "'"));
			bedString = bedParm.getValue("BED_NO_DESC", 0);
		}
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ patInfo.getValue("IN_DEPT_CODE", 0) + "'"));
		String Name = pat.getName();
		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				patInfo.getTimestamp("IN_DATE", 0));
		prtForSheetParm.setData("MR_NO", "TEXT", mrNo);
		prtForSheetParm.setData("IPD_NO", "TEXT", ipdNo);
		prtForSheetParm.setData("DEPT", "TEXT",
				deptParm.getValue("DEPT_CHN_DESC", 0));
		prtForSheetParm.setData("BED_NO", "TEXT", bedString);
		prtForSheetParm.setData("STATION", "TEXT", stationString);
		prtForSheetParm.setData("NAME", "TEXT", Name);
		prtForSheetParm.setData("SEX", "TEXT", sex);
		prtForSheetParm.setData("AGE", "TEXT", age);
		prtForSheetParm.setData("IN_DATE", "TEXT", StringTool.getString(
				patInfo.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日"));
		return prtForSheetParm;
	}

	/**
	 * 打印核心算法，将数据转化成坐标
	 * 
	 * @param tprSign
	 *            Vector 主要数据
	 * @param differCount
	 *            int 要打印的天数（endDate-startDate）
	 */
	public TParm dataToCoordinate(Vector tprSign, int differCount) {
		TParm mainPrtData = new TParm();
		// 主细表数据
		TParm master = (TParm) tprSign.get(0); // 下面的数据
		TParm detail = (TParm) tprSign.get(1); // (点线)
		int countDays = detail.getCount("SYSTOLICPRESSURE") / 6;
		Vector finalSystol = new Vector();
		Vector finalDiastol = new Vector();

		for (int i = 0; i < countDays; i++) {
			String systol = "";
			String diastol = "";
			for (int j = 0; j < 6; j++) {
				if (!StringUtil.isNullString(detail.getValue(
						"SYSTOLICPRESSURE", i * 6 + j))
						&& detail.getDouble("SYSTOLICPRESSURE", i * 6 + j) != 0) {
					systol = detail.getValue("SYSTOLICPRESSURE", i * 6 + j);
				}
				if (!StringUtil.isNullString(detail.getValue(
						"DIASTOLICPRESSURE", i * 6 + j))
						&& detail.getDouble("DIASTOLICPRESSURE", i * 6 + j) != 0) {
					diastol = detail.getValue("DIASTOLICPRESSURE", i * 6 + j);
				}
			}
			finalSystol.add(systol);
			finalDiastol.add(diastol);
		}

		// 得到所有日期
		Vector examineDate = (Vector) master.getData("EXAMINE_DATE");
		// 得到报表下面的数据
		// modified by WangQing 20170301
		// 新增specialStoolNote
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c3S = 0, c3D = 0, c8 = 0, c9 = 0, c10 = 0, opeDaysVC = 0, cDrainage = 0, cEnema = 0, cAutoStool = 0, specialStoolNote = 0;
		int Sh1 = 0;
		// 计数依次拿出常量
		int countWord = 0;
		// 如果选择区间的天数>数据/6说明有作废数据，以数据/6为“新”天数--体重的有效数据/6
		int newDates = detail.getCount("TEMPERATURE") / 6;
		if (differCount > newDates)
			differCount = newDates;
		// 根据（天数/7）得到需要花的总页数
		int pageCount = differCount / 7 + 1;
		if (differCount % 7 == 0)
			pageCount = differCount / 7;
		TParm controlPage = new TParm();

		// 外层控制页
		for (int i = 1; i <= pageCount; i++) {
			ArrayList dotList_T = new ArrayList();// 体温
			ArrayList dotList_PL = new ArrayList();// 脉搏
			ArrayList dotList_R = new ArrayList();// 呼吸
			ArrayList dotList_P = new ArrayList();// 物理降温
			ArrayList dotList_H = new ArrayList();// 心率
			ArrayList lineList_T = new ArrayList();
			ArrayList lineList_PL = new ArrayList();
			ArrayList lineList_R = new ArrayList();
			ArrayList lineList_P = new ArrayList();
			ArrayList lineList_H = new ArrayList();
			// 设置页数
			controlPage.addData("PAGE", "" + i);
			// 嵌套子循环控制天----------------------start-------------------------
			// int date = differCount - (i * 7) % 7;
			int date;
			if (i * 7 <= differCount)
				date = 7;
			else
				date = 7 - (i * 7 - differCount);
			int xT = -1;
			int yT = -1;
			int xPL = -1;
			int yPL = -1;
			int xR = -1;
			int yR = -1;
			int xH = -1;
			int yH = -1;
			String notForward = "";
			String notForward2 = "";
			for (int j = 1; j <= date; j++) {
				// 最内层控制时段
				// 呼吸值
				Vector respireValue = new Vector();
				// double temForward = (Double) ( (Vector) temperatureV.get( (1
				// + (j - 1) * 6) - 1)).get(0);
				double temper1 = detail.getDouble("TEMPERATURE",
						(1 + (j - 1) * 6) - 1);
				String not1 = nullToEmptyStr(detail.getValue("NOTPRREASONCODE",
						(1 + (j - 1) * 6) - 1));
				if (temper1 != 0
						|| (temper1 == 0 && !StringUtil.isNullString(not1))) {
					notForward = detail.getValue("NOTPRREASONCODE",
							(1 + (j - 1) * 6) - 1) + "";
					notForward2 = detail.getValue("NOTPRREASONCODE",
							(1 + (j - 1) * 6) - 1) + "";
				}
				int temperHorizontal = 0;
				int temperVertical = 0;
				for (int exa = 1; exa <= 6; exa++) {
					// 得到体温-------------------start---------------------------
					double temper = detail.getDouble("TEMPERATURE", exa
							+ (j - 1) * 6 - 1);
					double temperBak = temper;
					String tempKindCode = nullToEmptyStr(detail.getValue(
							"TMPTRKINDCODE", exa + (j - 1) * 6 - 1));
					// 当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点
					if (temper != 0.0 && !StringUtil.isNullString(tempKindCode)) {
						// continue;
						// 当温度<=35的时候写“体温不升”
						if (temper <= 35) {
							// 最低边界35度
							temper = 35;
							controlPage.addData(
									"NORAISE" + (exa + (j - 1) * 6), "体温不升");
						}
						// 得到体温的横纵坐标（点）
						temperHorizontal = countHorizontal(j, exa);
						temperVertical = (int) (getVertical(temper, "T") + 0.5); // 取整
						int dataTemper[] = new int[] {};
						if (temperBak >= 35) {// shibl modify 温度种类不能为空
							if (tempKindCode.equals("4")) {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, temperHorizontal + 6,
										temperVertical + 6, 7 };
							} else if (tempKindCode.equals("3")) {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 6 };
							} else {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 4 };
							}
						} else if (temperBak < 35) {
							if (tempKindCode.equals("4")) {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, temperHorizontal + 6,
										temperVertical + 6, 7, 1 };
							} else if (tempKindCode.equals("3")) {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 6, 1 };
							} else {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 4, 1 };
							}
						}
						// 存入所有点
						dotList_T.add(dataTemper);
					}
					// --------------------------end-----------------------------

					// 得到脉搏的点----------------start--------------------------
					double pluse = detail.getDouble("PLUSE", exa + (j - 1) * 6
							- 1);
					// 当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点
					int pluseHorizontal = 0;
					int pluseVertical = 0;
					if (pluse != 0) {
						// continue;
						// 得到脉搏的横纵坐标（点）
						pluseHorizontal = countHorizontal(j, exa);
						pluseVertical = (int) (getVertical(pluse, "PL") + 0.5); // 取整
						int dataPluse[] = new int[] {};
						// 得到一个点的坐标
						dataPluse = new int[] { pluseHorizontal, pluseVertical,
								6, 6, 4 };
						for (int k = 0; k < dotList_T.size(); k++) {
							if (pluseHorizontal == ((int[]) dotList_T.get(k))[0]
									&& pluseVertical == ((int[]) dotList_T
											.get(k))[1]) {
								// 得到一个点的坐标
								dataPluse = new int[] { pluseHorizontal,
										pluseVertical, 6, 6, 6 };
								break;
							}
						}
						// 存入所有点
						dotList_PL.add(dataPluse);
					}
					// ---------------------------end----------------------------

					// 得到呼吸--------------------start--------------------------
					// modified by WangQing 20170301 -start
					if (detail.getValue("SPECIALRESPIRENOTE",
							exa + (j - 1) * 6 - 1).equals("R")) {
						respireValue.add(detail.getValue("SPECIALRESPIRENOTE",
								exa + (j - 1) * 6 - 1));
					} else {
						respireValue.add(detail.getValue("RESPIRE", exa
								+ (j - 1) * 6 - 1));
					}
					// modified by WangQing 20170301 -end

					/*
					 * Double respire = Double.parseDouble( ( (Vector)
					 * respireV.get( (exa + (j - 1) * 6) - 1)).get(0) + "");
					 * //当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点 if (respire == 0)
					 * continue; //得到呼吸的横纵坐标（点） int respireHorizontal =
					 * countHorizontal(j, exa); int respireVertical = (int)
					 * (getVertical(respire,"R") + 0.5); //取整 //得到一个点的坐标 int
					 * dataRespire[] = new int[] { respireHorizontal,
					 * respireVertical, 6, 6, 4}; //存入所有点
					 * dotList_R.add(dataRespire);
					 */
					// ----------------------------end---------------------------

					// 得到心率的点----------------start--------------------------
					double heartRate = detail.getDouble("HEART_RATE",
							(exa + (j - 1) * 6) - 1);
					// 当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点
					int heartRateHorizontal = 0;
					int heartRateVertical = 0;
					if (heartRate != 0) {
						// continue;
						// 得到心率的横纵坐标（点）
						heartRateHorizontal = countHorizontal(j, exa);
						heartRateVertical = (int) (getVertical(heartRate, "H") + 0.5); // 取整
						// 得到一个点的坐标
						int dataHeartRate[] = new int[] { heartRateHorizontal,
								heartRateVertical, 6, 6, 6 };
						// 存入所有点
						dotList_H.add(dataHeartRate);
					}
					// ---------------------------end----------------------------

					// 得到物理降温-----------------start--------------------------
					String tempPhsi = detail.getValue("PHYSIATRICS", exa
							+ (j - 1) * 6 - 1);
					if (!StringUtil.isNullString(tempPhsi)) {
						// 得到数字类型的
						double phsiatrics = TCM_Transform.getDouble(tempPhsi);
						if (phsiatrics <= 35) {
							// 最低边界35度
							phsiatrics = 35;
						}
						// 得到体温的横纵坐标（点）
						int phsiHorizontal = countHorizontal(j, exa);
						int phsiVertical = (int) (getVertical(phsiatrics, "P") + 0.5); // 取整
						// 得到一个点的坐标
						int dataPhsi[] = new int[] { phsiHorizontal,
								phsiVertical, 6, 6, 6 };
						// 存入所有点
						dotList_P.add(dataPhsi);
						// 得到物理降温线-----------start--------------------------
						int dataTempLine[] = new int[] { temperHorizontal + 3,
								temperVertical + 3, phsiHorizontal + 3,
								phsiVertical + 3, 1 };
						lineList_P.add(dataTempLine);
					}
					// ----------------------------end---------------------------

					// 得到为测量原因
					String not = nullToEmptyStr(detail.getValue(
							"NOTPRREASONCODE", (exa + (j - 1) * 6) - 1));
					if (!StringUtil.isNullString(not)) {
						String sql1 = " SELECT CHN_DESC "
								+ " FROM SYS_DICTIONARY"
								+ " WHERE GROUP_ID='SUM_NOTMPREASON'"
								+ " AND   ID = '" + not + "'";
						TParm result1 = new TParm(TJDODBTool.getInstance()
								.select(sql1));
						controlPage.addData("REASON" + (exa + (j - 1) * 6),
								result1.getValue("CHN_DESC", 0));
					}

					// 得到体温的线----------------start--------------------------
					if (temper != 0.0 && !StringUtil.isNullString(tempKindCode)) {
						// if(countWord ==0 ||
						// StringTool.getDateDiffer(StringTool.getTimestamp(""+examineDate.get(countWord),"yyyyMMdd"),
						// StringTool.getTimestamp(""+examineDate.get(countWord
						// - 1),"yyyyMMdd")) <= 1 ||
						// exa != 1)
						if (xT != -1 && yT != -1
								&& StringUtil.isNullString(not)
								&& StringUtil.isNullString(notForward)) {
							int dataTempLine[] = new int[] { xT + 3, yT + 3,
									temperHorizontal + 3, temperVertical + 3, 1 };
							lineList_T.add(dataTempLine);
						}
						xT = temperHorizontal;
						yT = temperVertical;
					}
					// temForward = temper;
					if (temper != 0
							|| (temper == 0 && !StringUtil.isNullString(not))) {
						notForward = not;
					}
					// --------------------------end----------------------------

					// 得到脉搏的线----------------start--------------------------
					if (pluse != 0) {
						if (xPL != -1 && yPL != -1
								&& StringUtil.isNullString(not)
								&& StringUtil.isNullString(notForward2)) {
							int dataPluseLine[] = new int[] { xPL + 3, yPL + 3,
									pluseHorizontal + 3, pluseVertical + 3, 1 };
							lineList_PL.add(dataPluseLine);
						}
						xPL = pluseHorizontal;
						yPL = pluseVertical;
					}
					// temForward = temper;
					if (pluse != 0
							|| (pluse == 0 && !StringUtil.isNullString(not))) {
						notForward2 = not;
					}
					// --------------------------end----------------------------

					// 得到呼吸的线----------------start--------------------------
					/*
					 * if (xR != -1 && yR != -1 && "null".equals(not)) { int
					 * dataRespireLine[] = new int[] { xR + 3, yR + 3,
					 * respireHorizontal + 3, respireVertical + 3, 1};
					 * lineList_R.add(dataRespireLine); } xR =
					 * respireHorizontal; yR = respireVertical;
					 */
					// --------------------------end----------------------------

					// 得到心率的线----------------start--------------------------
					if (heartRate != 0) {
						if (xH != -1 && yH != -1
								&& StringUtil.isNullString(not)) {
							int dataHeartRateLine[] = new int[] { xH + 3,
									yH + 3, heartRateHorizontal + 3,
									heartRateVertical + 3, 1 };
							lineList_H.add(dataHeartRateLine);
						}
						xH = heartRateHorizontal;
						yH = heartRateVertical;
					}
					// --------------------------end----------------------------

					// 病人动态信息----------------start--------------------------
					String ptMoveCode = nullToEmptyStr(detail.getValue(
							"PTMOVECATECODE", exa + (j - 1) * 6 - 1));
					if (!StringUtil.isNullString(ptMoveCode)) {
						String ptMoveDesc = nullToEmptyStr(detail.getValue(
								"PTMOVECATEDESC", exa + (j - 1) * 6 - 1));
						controlPage.addData("MOVE" + (exa + (j - 1) * 6),
								ptMoveCode + "||" + ptMoveDesc);
					}
				}

				// 得到日期-------------------------start-------------------------
				String tenmpDate = examineDate.get(countWord++).toString();
				String fomatDate = "";
				if (countWord - 1 == 0) {
					fomatDate = tenmpDate.substring(0, 4) + "."
							+ tenmpDate.substring(4, 6) + "."
							+ tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				} else {
					String tenmpDateForward = examineDate.get(countWord - 2)
							.toString();
					if (!tenmpDateForward.substring(2, 4).equals(
							tenmpDate.substring(2, 4)))
						fomatDate = tenmpDate.substring(0, 4) + "."
								+ tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else if (!tenmpDateForward.substring(4, 6).equals(
							tenmpDate.substring(4, 6)))
						fomatDate = tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else
						fomatDate = tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				}
				controlPage.addData("OPEDAY" + j,
						master.getData("OPE_DAYS", opeDaysVC++));
				// 入院日期时间
				Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
				// 得到该出生天数（该日子-出生日子）-------------------------------
				int dates = getBornDateDiffer(tenmpDate,
						StringTool.getTimestampDate(inDate)) + 1;
				// dates = getInHospDaysE();
				controlPage.addData("INDATE" + j, dates == 0 ? "" : dates);
				// 手术后期OPEDAYn
				// -----------------------------------

				// 得到报表下面的数据----------------------start------------------------
				for (int k = 0; k < respireValue.size(); k++) {
					try {
						if (respireValue.get(k).toString().equals("R")) {
							controlPage.addData("L1_" + j + (k + 1), "R");
						} else {
							controlPage.addData(
									"L1_" + j + (k + 1),
									(int) Double.parseDouble(""
											+ clearZero(respireValue.get(k))));
						}
					} catch (Exception e) {
						controlPage.addData("L1_" + j + (k + 1),
								clearZero(respireValue.get(k)));
					}
				}

				// ==============================排便 -start==============

				// if (StringUtil.isNullString(master.getValue("STOOL",
				// cAutoStool))
				// && StringUtil.isNullString(master.getValue("ENEMA",cEnema)))
				// {
				// controlPage.addData("L2" + j, "");
				// controlPage.addData("L12" + j, "");
				// controlPage.addData("L13" + j, "");
				// controlPage.addData("L14" + j, "");
				// } else {
				// // modify by wukai 20160606
				// // controlPage.addData("L2" + j, master.getData("STOOL",
				// // cAutoStool));
				// Object stool = master.getData("STOOL", cAutoStool);
				//
				// // Object specialStoolNote = master.getData("STOOL",
				// cAutoStool);
				//
				//
				//
				// Object ename = master.getData("ENEMA", cEnema);
				// if ((stool == null || "0".equals(stool)) && (ename != null))
				// {
				// controlPage.addData("L2" + j, "");
				// } else {
				// controlPage.addData("L2" + j, stool);
				// }
				// if (StringUtil.isNullString(master.getValue("ENEMA",
				// cEnema))) {
				// controlPage.addData("L12" + j, "");
				// controlPage.addData("L13" + j, "");
				// controlPage.addData("L14" + j, "");
				// } else {
				// controlPage.addData("L12" + j, master.getValue("ENEMA",
				// cEnema));
				// controlPage.addData("L13" + j, "/");
				// controlPage.addData("L14" + j, "E");
				// }
				// }
				// cAutoStool++;
				// cEnema++;
				//

				// modified by WangQing 20170301 -start
				// 如果有特殊排便，则只显示特殊排便；否则显示自行排便+灌肠
				if (!(StringUtil.isNullString(master.getValue(
						"SPECIALSTOOLNOTE", specialStoolNote)))) {// 特殊排便
					String sql = "select DESCRIPTION from SYS_DICTIONARY where GROUP_ID='PB_TYPE' and ID='"
							+ master.getValue("SPECIALSTOOLNOTE",
									specialStoolNote) + "' ";
					TParm parm = new TParm();
					parm.setData(TJDODBTool.getInstance().select(sql));
					controlPage.addData("L2" + j,
							parm.getValue("DESCRIPTION", 0));// 特殊排便
				} else {
					if ((!(StringUtil.isNullString(master.getValue("STOOL",
							cAutoStool))))
							|| (!(master.getValue("STOOL", cAutoStool)
									.equals("0")))) {// 自行排便
						controlPage.addData("L2" + j,
								master.getValue("STOOL", cAutoStool));// 自行排便
					}
					if ((!(StringUtil.isNullString(master.getValue("ENEMA",
							cEnema))))
							&& (!(master.getValue("ENEMA", cEnema).equals("0")))) {// 灌肠
						controlPage.addData("L12" + j,
								master.getValue("ENEMA", cEnema));// 灌肠次数
						controlPage.addData("L13" + j, "/");
						controlPage.addData("L14" + j, "E");
					}
				}
				cAutoStool++;
				specialStoolNote++;
				cEnema++;
				// modified by WangQing 20170301 -end

				// ==============================排便
				// -end=================================

				if ((clearZero(finalSystol.get(c3S)) + "").length() == 0
						&& (clearZero(finalDiastol.get(c3D)) + "").length() == 0)
					controlPage.addData("L3", "");
				else
					controlPage.addData("L3" + j, finalSystol.get(c3S) + "/"
							+ finalDiastol.get(c3D));
				c3S++;
				c3D++;
				controlPage.addData("L4" + j,
						clearZero(master.getData("INTAKEFLUIDQTY", c4++)));
				controlPage.addData("L5" + j,
						clearZero(master.getData("OUTPUTURINEQTY", c5++)));
				controlPage.addData("L7" + j,
						clearZero(master.getData("WEIGHT", c6++)));
				controlPage.addData("L6" + j,
						clearZero(master.getData("HEIGHT", c7++)));
				controlPage.addData("L11" + j,
						clearZero(master.getData("DRAINAGE", cDrainage++)));
				for (int l = 0; l < master.getCount("USER_DEFINE_1") && j == 1; l++) {
					if (StringUtil.isNullString(master.getValue(
							"USER_DEFINE_1", l)))
						continue;
					controlPage.addData("L80",
							master.getData("USER_DEFINE_1", l));
					break;
				}
				Object obj8 = master.getData("USER_DEFINE_1_VALUE", c8++);
				if (obj8 == null || obj8.equals(""))
					controlPage.addData("L8" + j, "");
				else
					controlPage.addData("L8" + j, obj8);
				for (int l = 0; l < master.getCount("USER_DEFINE_2") && j == 1; l++) {
					if (StringUtil.isNullString(master.getValue(
							"USER_DEFINE_2", l)))
						continue;
					controlPage.addData("L90",
							master.getData("USER_DEFINE_2", l));
					break;
				}
				Object obj9 = master.getData("USER_DEFINE_2_VALUE", c9++);
				if (obj9 == null || obj9.equals(""))
					controlPage.addData("L9" + j, "");
				else
					controlPage.addData("L9" + j, obj9);
				for (int l = 0; l < master.getCount("USER_DEFINE_3") && j == 1; l++) {
					if (StringUtil.isNullString(master.getValue(
							"USER_DEFINE_3", l)))
						continue;
					controlPage.addData("L100",
							master.getData("USER_DEFINE_3", l));
					break;
				}
				Object obj10 = master.getData("USER_DEFINE_3_VALUE", c10++);
				;
				if (obj10 == null || obj10.equals(""))
					controlPage.addData("L10" + j, "");
				else
					controlPage.addData("L10" + j, obj10);
			}

			// 体温点
			int pageDataForT[][] = new int[dotList_T.size()][5];
			for (int j = 0; j < dotList_T.size(); j++)
				pageDataForT[j] = (int[]) dotList_T.get(j);
			// 脉搏点
			int pageDataForPL[][] = new int[dotList_PL.size()][5];
			for (int j = 0; j < dotList_PL.size(); j++)
				pageDataForPL[j] = (int[]) dotList_PL.get(j);
			// 呼吸
			int pageDataForR[][] = new int[dotList_R.size()][5];
			for (int j = 0; j < dotList_R.size(); j++)
				pageDataForR[j] = (int[]) dotList_R.get(j);
			// 心率
			int pageDataForH[][] = new int[dotList_H.size()][5];
			for (int j = 0; j < dotList_H.size(); j++)
				pageDataForH[j] = (int[]) dotList_H.get(j);
			// 物理降温点
			int pageDataForP[][] = new int[dotList_P.size()][5];
			for (int j = 0; j < dotList_P.size(); j++)
				pageDataForP[j] = (int[]) dotList_P.get(j);
			// 体温线
			int pageDataForTLine[][] = new int[lineList_T.size()][5];
			for (int j = 0; j < lineList_T.size(); j++) {
				pageDataForTLine[j] = (int[]) lineList_T.get(j);
			}
			// 脉搏线
			int pageDataForPLLine[][] = new int[lineList_PL.size()][5];
			for (int j = 0; j < lineList_PL.size(); j++)
				pageDataForPLLine[j] = (int[]) lineList_PL.get(j);
			// 呼吸线
			int pageDataForRLine[][] = new int[lineList_R.size()][5];
			for (int j = 0; j < lineList_R.size(); j++)
				pageDataForRLine[j] = (int[]) lineList_R.get(j);
			// 体重线
			int pageDataForPLine[][] = new int[lineList_P.size()][5];
			for (int j = 0; j < lineList_P.size(); j++)
				pageDataForPLine[j] = (int[]) lineList_P.get(j);
			// 心率线
			int pageDataForHLine[][] = new int[lineList_H.size()][5];
			for (int j = 0; j < lineList_H.size(); j++) {
				pageDataForHLine[j] = (int[]) lineList_H.get(j);
			}
			controlPage.addData("TEMPDOT", pageDataForT);
			controlPage.addData("PLUSEDOT", pageDataForPL);
			// controlPage.addData("RESPIREDOT", pageDataForR);
			controlPage.addData("PHSIDOT", pageDataForP);
			controlPage.addData("RESPIREDOT", pageDataForH);
			controlPage.addData("TEMPLINE", pageDataForTLine);
			controlPage.addData("PLUSELINE", pageDataForPLLine);
			// controlPage.addData("RESPIRELINE", pageDataForRLine);
			controlPage.addData("PHSILINE", pageDataForPLine);
			controlPage.addData("RESPIRELINE", pageDataForHLine);
			// ----------------------------end----------------------------------
		}

		// 设置页数
		controlPage.setCount(pageCount);
		controlPage.addData("SYSTEM", "COLUMNS", "PAGE");
		mainPrtData.setData("TABLE", controlPage.getData());
		return mainPrtData;
	}

	public Object clearZero(Object obj) {
		try {
			if (obj == null)
				return "";
			double mun = Double.parseDouble("" + obj);
			if (mun == 0)
				return "";
			else
				return obj;
		} catch (NumberFormatException e) {
			return obj;
		}
	}

	/**
	 * 得到与出生天数的差
	 * 
	 * @param nowDate
	 *            String
	 * @return int
	 */
	public int getBornDateDiffer(String date, Timestamp bornDate) {
		Timestamp nowDate = StringTool.getTimestamp(date, "yyyyMMdd");
		return StringTool.getDateDiffer(nowDate, bornDate);
	}

	/**
	 * 计算横向坐标位置
	 * 
	 * @param date
	 *            int
	 * @param examineSession
	 *            int
	 * @return int
	 */
	private int countHorizontal(int date, int examineSession) {
		// int adaptX = 7;
		// return 10 * ( (date - 1) * 6 + examineSession) - adaptX;
		int adaptX = 7;
		return 8 * ((date - 1) * 6 + examineSession) - adaptX;
	}

	/**
	 * 结算体温单的纵向坐标
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	private double getVertical(double value, String flag) {
		int adaptY = 8;
		// 体温或者物理降温
		if ("T".equals(flag) || "P".equals(flag))
			return (445 - countVertical(value, 42, 34, 40) * 10) - adaptY;
		// 脉搏
		if ("PL".equals(flag))
			return (445 - countVertical(value, 180, 20, 40) * 10) - adaptY;
		// 心率
		if ("H".equals(flag))
			return (445 - countVertical(value, 180, 20, 40) * 10) - adaptY;
		return -1;
	}

	/**
	 * 计算纵坐标的位置--体温
	 * 
	 * @param value
	 *            int 数据库中记录的数据
	 * @param topValue
	 *            int 表格中最大的值--顶
	 * @param butValue
	 *            int 表格中最小的值--底
	 * @param level
	 *            int 最大与最小之间的有多少等级-行数
	 * @return int
	 */
	private double countVertical(double value, double topValue,
			double butValue, int level) {
		return (value - butValue) / ((topValue - butValue) / level) - 1;
	}

	/**
	 * 得到需要打印的主数据
	 * 
	 * @param date
	 *            TParm
	 */
	public Vector getVitalSignDate(TParm date) {
		Vector tprSign = new Vector();
		date.setData("ADM_TYPE", admType);
		date.setData("CASE_NO", caseNo);
		// 体温主表
		TParm vitalSignMstParm = SUMVitalSignTool.getInstance().selectdataMst(
				date);
		// 体温细表
		TParm vitalSignDtlParm = SUMVitalSignTool.getInstance().selectdataDtl(
				date);
		// 生命标记结果：0-主表信息 1-细表信息
		tprSign.add(vitalSignMstParm);
		tprSign.add(vitalSignDtlParm);
		return tprSign;
	}

	/**
	 * TParm中“null”串转为空字符串
	 * 
	 * @param str
	 * @return
	 */
	public String nullToEmptyStr(String str) {
		if (str == null || str.equalsIgnoreCase("null")) {
			return "";
		}
		return str;
	}

	/**
	 * 调用NIS生理监测
	 */
	public void onNisVitalSign() {
		SystemTool.getInstance().OpenNisVitalSign(caseNo,
				patInfo.getValue("MR_NO", 0));
	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		if (isMroFlg)
			return true;
		return true;
	}

	public static void main(String[] args) {
		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sum\\SUMVitalSign.x");
	}

	/**
	 * 全选勾选
	 */
	public void onCheckAll() {
		boolean flag = true;
		if (getCheckBox("CHECK_ALL").isSelected()) {
			flag = true;
		} else {
			flag = false;
		}
		getCheckBox("CHECK_2").setSelected(flag);
		getCheckBox("CHECK_6").setSelected(flag);
		getCheckBox("CHECK_10").setSelected(flag);
		getCheckBox("CHECK_14").setSelected(flag);
		getCheckBox("CHECK_18").setSelected(flag);
		getCheckBox("CHECK_22").setSelected(flag);
	}

	/**
	 * 同步CIS视图数据
	 */
	public void extractCisData() {
		TParm selParm = masterTable.getParmValue().getRow(masterRow);
		String selDate = selParm.getValue("EXAMINE_DATE");
		TParm parm = new TParm();
		parm.setData("START_POOLING_TIME", selDate + " 00:00");
		parm.setData("END_POOLING_TIME", selDate + " 23:59");
		parm.setData("CASE_NO", caseNo);

		// 查询病患当前在院信息
		TParm admResult = ADMInpTool.getInstance().queryCaseNo(parm);

		if (admResult.getErrCode() < 0) {
			this.messageBox("查询病患住院信息错误");
			err("ERR:" + admResult.getErrText());
			return;
		} else if (admResult.getCount("CASE_NO") < 1) {
			this.messageBox("查无病患住院信息");
			return;
		} else {
			// 取得病患当前病区
			String stationCode = admResult.getValue("STATION_CODE", 0);
			String viewName = "";
			String databaseName = "";

			if (StringUtils.isNotEmpty(stationCode)) {
				// 根据病患所在病区从CIS对应的视图中取得体温单数据
				if ("I01,I02,I03,I04".contains(stationCode)) {
					databaseName = "javahisICU";
					viewName = "dbo.V_ICU_Vitalsigns";
				} else if ("C01,C02".contains(stationCode)) {
					databaseName = "javahisCCU";
					viewName = "dbo.V_CCU_Vitalsigns";
				} else {
					databaseName = "javahisWard";
					viewName = "dbo.V_Ward_Vitalsigns";
				}

				// 查询病区CIS数据
				TParm result = ODICISVitalSignTool.getInstance()
						.queryODICISData(parm, viewName, databaseName);

				if (result.getErrCode() < 0) {
					this.messageBox("查询CIS体征数据错误");
					err("ERR:" + admResult.getErrText());
					return;
				} else if (result.getCount("CASE_NO") < 1) {
					this.messageBox("查无CIS体征数据");
					return;
				} else {
					this.setCisData(result);
				}
			}
		}
	}

	/**
	 * 将CIS取得的体征数据放到体温单界面对应控件上
	 */
	private void setCisData(TParm parm) {
		TParm cisFilterResult = new TParm();
		int count = parm.getCount();
		// 监测项目需按照升序排列，这样可减少数据遍历次数，优化性能
		String[] monitorItemArray = { "ABPD", "ABPS", "BT2", "HR", "NBPD",
				"NBPS", "PULSE", "RR" };
		String[] monitorTimeArray = { "02", "06", "10", "14", "18", "22" };
		int monitorItemArrayLen = monitorItemArray.length;
		int monitorTimeArrayLen = monitorTimeArray.length;
		List<String> dataList = new ArrayList<String>();
		String key = "";
		// 入量
		String inTake = "";
		// 出量
		String outPut = "";

		// 根据体温当的固定时间段，分别按照不同监测项目取各自时间段监测结果，构建出数据对象cisFilterResult
		for (int i = 0; i < monitorItemArrayLen; i++) {
			for (int j = 0; j < monitorTimeArrayLen; j++) {
				key = monitorItemArray[i] + "_" + monitorTimeArray[j];
				for (int k = 0; k < count; k++) {
					if ((parm.getValue("MONITOR_ITEM_EN", k) + "_" + parm
							.getValue("MONITOR_TIME", k).substring(11, 13))
							.equals(key)
							&& !dataList.contains(key)) {
						dataList.add(key);
						cisFilterResult.addData(monitorItemArray[i],
								parm.getValue("MONITOR_VALUE", k));
						break;
					} else if (k == count - 1) {
						cisFilterResult.addData(monitorItemArray[i], "");
					}
				}
			}
		}

		// 取得入量、出量
		for (int l = 0; l < count; l++) {
			if (parm.getValue("MONITOR_ITEM_EN", l).equals("INTAKE")
					&& StringUtils.isEmpty(inTake)) {
				inTake = parm.getValue("MONITOR_VALUE", l);
			}
			if (parm.getValue("MONITOR_ITEM_EN", l).equals("OUTPUT")
					&& StringUtils.isEmpty(outPut)) {
				outPut = parm.getValue("MONITOR_VALUE", l);
			}
			if (StringUtils.isNotEmpty(inTake)
					&& StringUtils.isNotEmpty(outPut)) {
				break;
			}
		}

		cisFilterResult.setCount(cisFilterResult.getCount("RR"));
		// ICU优先取有创血压其次取无创血压
		for (int m = 0; m < cisFilterResult.getCount(); m++) {
			if (cisFilterResult.getValue("ABPS").replace("[", "")
					.replace("]", "").replace(" ", "").length() < 6) {
				cisFilterResult.addData("BPS",
						cisFilterResult.getValue("NBPS", m));
			}
			if (cisFilterResult.getValue("ABPD").replace("[", "")
					.replace("]", "").replace(" ", "").length() < 6) {
				cisFilterResult.addData("BPD",
						cisFilterResult.getValue("NBPD", m));
			}
		}

		// 同步前表格原有数据
		TParm oldTableParm = detailTable.getParmValue();

		TParm newTableParm = new TParm();
		String[] showTableItemArray = { "BT2", "PULSE", "RR", "BPS", "BPD",
				"HR" };
		// 按照表格定好的从上之下的顺序构建显示表格数据showTableParm
		for (int n = 0; n < 6; n++) {
			for (int p = 0; p < 6; p++) {
				newTableParm.addData("" + p,
						cisFilterResult.getValue(showTableItemArray[n], p));
			}
		}

		if (getCheckBox("CHECK_2").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "0");
		}
		if (getCheckBox("CHECK_6").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "1");
		}
		if (getCheckBox("CHECK_10").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "2");
		}
		if (getCheckBox("CHECK_14").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "3");
		}
		if (getCheckBox("CHECK_18").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "4");
		}
		if (getCheckBox("CHECK_22").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "5");
		}

		// 使用整合好的表格数据显示到页面
		detailTable.setParmValue(oldTableParm);
		// 代入出入量
		this.setValue("INTAKEFLUIDQTY", inTake);
		this.setValue("OUTPUTURINEQTY", outPut);

		this.messageBox("提取成功");
	}

	/**
	 * 表格数据替换
	 */
	private void tableDataReplace(TParm oldTableParm, TParm newTableParm,
			String group) {
		String value = "";
		for (int i = 0; i < 6; i++) {
			value = newTableParm.getValue(group, i);
			if (StringUtils.isNotEmpty(value)) {
				oldTableParm.setData(group, i, value);
			}
		}
	}

	/**
	 * 得到TCheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * 特殊字符按钮单击事件
	 * 
	 * @author wangqing 2016.11.21
	 */
	public void onSpecialChars() {
		TParm parm = new TParm();
		parm.addListener("onReturnContent", this, "onReturnContent");
		// this.messageBox("======123123========");
		TWindow window = (TWindow) openWindow(
				"%ROOT%\\config\\emr\\EMRSpecialChars.x", parm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		AnimationWindowUtils.show(window);
		AWTUtilities.setWindowOpacity(window, 0.9f);
		window.setVisible(true);
	}

	/**
	 * 插入字符串
	 * 
	 * @param String
	 *            value
	 * @author Eric 2016.11.21
	 * 
	 */
	public void onReturnContent(String value) {
		// 得到detailTable的选中列
		int selectColumn = this.detailTable.getSelectedColumn();
		// 得到选中列的编辑器
		TTableCellEditor editor = detailTable.getCellEditor(selectColumn);
		// 获取当前编辑的cell
		TTextField c = (TTextField) editor.getDelegate().getComponent();
		// System.out.println(c.getSelectionStart()+"-"+c.getSelectionEnd());
		// 获取cell的值
		String v = c.getValue();
		// 值替换
		String v2 = v.substring(0, c.getSelectionStart()) + value
				+ v.substring(c.getSelectionEnd(), v.length());
		c.setValue(v2);
		// 使光标放在字符串的末尾
		c.setCaretPosition(c.getText().length());
	}

	/**
	 * 设置病患动态
	 * 
	 * @param code
	 *            病患动态编码
	 */
	private void setPatientTrends(String code) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// 查询住院信息
		TParm admResult = ADMInpTool.getInstance().selectall(parm);
		// 入院于
		if ("01".equals(code)) {
			parm.setData("START_DATE", admResult.getTimestamp("ADM_DATE", 0));
			parm.setData("END_DATE", SystemTool.getInstance().getDate());
		} else if ("03".equals(code)) {
			int row = masterTable.getSelectedRow();
			if (row < 0) {
				return;
			}
			String date = masterTable.getItemString(row, "EXAMINE_DATE")
					.replaceAll("/", "-").substring(0, 10);

			int col = detailTable.getSelectedColumn();

			if (col < 0) {
				return;
			}

			int hour = col * 4 + 2;
			// 转科于
			parm.setData("START_DATE", StringTool.getTimestamp(date + " "
					+ (hour - 2) + ":00:00", "yyyy-MM-dd HH:mm:ss"));
			parm.setData(
					"END_DATE",
					StringTool.getTimestamp(date + " " + (hour + 1)
							+ ":59:59.0", "yyyy-MM-dd HH:mm:ss"));
		}

		// 动态记录查询
		TParm admChgResult = ADMChgTool.getInstance().ADMQueryChgLog(parm);
		if (admChgResult.getErrCode() < 0) {
			this.messageBox("查询动态转移记录异常:" + admChgResult.getErrText());
			return;
		}

		if ("01".equals(code)) {
			// 按照时间升序排列后，取第一笔入床数据作为入院时间
			for (int i = 0; i < admChgResult.getCount(); i++) {
				if ("INBD".equals(admChgResult.getValue("PSF_KIND", i))) {
					this.setValue("PTMOVECATEDESC", DateUtil
							.transferHMToChinese(admChgResult.getValue(
									"CHG_DATE", i)));
					return;
				}
			}
		} else if ("03".equals(code)) {
			// 转科时间
			String transferDeptTime = "";
			int seq = 1;

			// 按照时间升序排列后，取最后一笔入床数据
			for (int i = 0; i < admChgResult.getCount(); i++) {
				if ("INBD".equals(admChgResult.getValue("PSF_KIND", i))) {
					transferDeptTime = admChgResult.getValue("CHG_DATE", i);
					seq = admChgResult.getInt("SEQ_NO", i);
				}
			}

			// 如果当前选择的时间段内入床数据在一笔以上，取最后一笔入床数据作为转科时间
			if (StringUtils.isNotEmpty(transferDeptTime)) {
				// 入床前的上一笔转移记录如果是入科或取消入科，则本次入床视为转科
				parm = new TParm();
				parm.setData("CASE_NO", caseNo);
				parm.setData("SEQ_NO", seq - 1);
				parm.setData("START_DATE",
						admResult.getTimestamp("ADM_DATE", 0));
				parm.setData("END_DATE", SystemTool.getInstance().getDate());

				// 动态记录查询
				admChgResult = ADMChgTool.getInstance().ADMQueryChgLog(parm);

				if (admChgResult.getCount() > 0
						&& ("INDP".equals(admChgResult.getValue("PSF_KIND", 0)) || "CANCELINDP"
								.equals(admChgResult.getValue("CANCELINDP", 0)))) {
					this.setValue("PTMOVECATEDESC",
							DateUtil.transferHMToChinese(transferDeptTime));
				}
			}
		}
	}
}
