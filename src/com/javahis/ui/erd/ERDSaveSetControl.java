package com.javahis.ui.erd;

import java.sql.Timestamp;
import javax.swing.SwingUtilities;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;
import jdo.ekt.EKTIO;
import jdo.erd.ERDLevelTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: 急诊抢救设定  </p>
 *
 * <p>Description: 急诊抢救设定</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author wangqing 200170623
 *
 * @version 1.0
 */
public class ERDSaveSetControl extends TControl {
    /**
     * 系统参数，默认值为RECORD
     */
	private String runFlg = "RECORD";
	/**
	 * 已检伤TRadioButton
	 */
	private TRadioButton Radio0;
	/**
	 * 已挂号TRadioButton
	 */
	private TRadioButton Radio1;
	/**
	 * 已看诊TRadioButton
	 */
	private TRadioButton Radio2;
	/**
	 * 抢救中TRadioButton
	 */
	private TRadioButton Radio3;
	/**
	 * 已转出TRadioButton
	 */
	private TRadioButton Radio4;
	/**
	 * 查询开始日期TTextFormat
	 */
	private TTextFormat from_Date;
	/**
	 * 查询截止日期TTextFormat
	 */
	private TTextFormat to_Date;
	/**
	 * 病案号TTextField
	 */
	private TTextField MR_NO;
	/**
	 * 检伤号TTextField
	 */
	private TTextField TRIAGE_NO;
	/**
	 * 患者姓名TTextField
	 */
	private TTextField PAT_NAME;
	/**
	 * 床位描述TTextField
	 */
	private TTextField BED_DESC;
	/**
	 * 抢救区域TTextFormat
	 */
	private TTextFormat ERD_REGION_CODE;
	
	private TTable table;
	private TPanel panel;
	private String workPanelTag;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		// 获取系统参数
		Object obj = this.getParameter();// RECORD|CHECK
		if (obj != null && obj instanceof String) {
			runFlg = this.getParameter().toString();
		}
		// 初始化标题
		if(runFlg != null && runFlg.equals("RECORD")){
			this.setTitle("急诊抢救设定");
		}else if(runFlg != null && runFlg.equals("CHECK")){
			this.setTitle("急诊抢救护士站");
		}else{
			
		}
		// 本界面的初始化
		myInitControler();
		// 默认选中Radio3
		Radio3.setSelected(true);
		// 初始化时间区间
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int effDays = 3;// 时间间隔3天
		Timestamp last3day = StringTool.rollDate(now, -effDays);
		from_Date.setValue(StringTool.getDate(StringTool.getString(last3day, "yyyyMMdd")+ "000000", "yyyyMMddHHmmss"));
		to_Date.setValue(now);
		// 执行查询
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					onQuery();
				}
				catch (Exception e) {}
			}
		});		
	}

	/**
	 * 初始化控件，绑定监听事件
	 */
	public void myInitControler() {
		// 得到时间控件
		from_Date = (TTextFormat) this.getComponent("from_Date");
		to_Date = (TTextFormat) this.getComponent("to_Date");
		// 得到table控件
		table = (TTable) this.getComponent("TABLE");
		// 得到查询条件UI的对象
		Radio0 = (TRadioButton) this.getComponent("Radio0");
		Radio1 = (TRadioButton) this.getComponent("Radio1");
		Radio2 = (TRadioButton) this.getComponent("Radio2");
		Radio3 = (TRadioButton) this.getComponent("Radio3");
		Radio4 = (TRadioButton) this.getComponent("Radio4");
		MR_NO = (TTextField) this.getComponent("MR_NO");
		// 
		TRIAGE_NO = (TTextField) this.getComponent("TRIAGE_NO");
		PAT_NAME = (TTextField) this.getComponent("NAME");
		BED_DESC = (TTextField) this.getComponent("BED_DESC");
		ERD_REGION_CODE = (TTextFormat) this.getComponent("ERD_REGION_CODE");
		// 
		panel = (TPanel) this.getComponent("PANEL");
		
		// TABLE单击事件
		callFunction("UI|TABLE|addEventListener", "TABLE" + "->" + TTableEvent.CLICKED, this, "onTableClicked");
		
		// add by wangqing 20171211 table双击事件
		callFunction("UI|TABLE|addEventListener", "TABLE" + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");
	}
	
	/**
	 * 查询
	 * @author wangqing 20171123
	 */
	public void onQuery() {
		if(table==null){
			table = (TTable) this.getComponent("TABLE");
		}
		table.setParmValue(null);// 清空table数据
		// 获取查询条件
		TParm selParm = this.getQueryParm();
		if(selParm==null){
			this.messageBox("获取查询条件失败");
			return;
		}
		// 执行查询
		TParm query = this.query(selParm);
		if(query.getErrCode()<0){
			this.messageBox("查询失败");
			return;
		}
		if (query.getCount() <= 0) {
			this.messageBox("没有相关数据！");
			return;
		}
		table.setParmValue(query);	
	}
	
	/**
	 * 获取查询参数
	 * @author wangqing 20170626
	 * @return TParm
	 */
	public TParm getQueryParm() {
		// FLG 0：已检伤 ；1：已挂号；2：已看诊；3：抢救中；4：已转出
		TParm parm = new TParm();
		if(Radio0.isSelected()){// 已检伤	
			parm.setData("FLG", "0");
		}else if(Radio1.isSelected()){// 已挂号	
			parm.setData("FLG", "1");
		}else if (Radio2.isSelected()) {// 以看诊
			parm.setData("FLG", "2");
		}else if (Radio3.isSelected()) {// 抢救中
			parm.setData("FLG", "3");
		}else if (Radio4.isSelected()) {// 已转出			
			parm.setData("FLG", "4");
		}
		// 查询时间区间
		if (getValueString("from_Date").trim().length() == 0
				|| getValueString("to_Date").trim().length() == 0 
				|| getValueString("from_Date").compareTo(getValueString("to_Date")) > 0) {
			messageBox("时间日期不合法");
			return null;
		}
		// 日期格式处理 2017-06-26 14:12:00.0 -> 2017/06/26 14:12:00
		String fromDate = getValueString("from_Date").replace("-", "/").substring(0, 16);
		String toDate = getValueString("to_Date").replace("-", "/").substring(0, 16);
		parm.setData("FROM_DATE", fromDate);
		parm.setData("TO_DATE", toDate);	
		// 抢救区
		if (getValueString("ERD_REGION").length() != 0) {
			parm.setData("ERD_REGION", getValue("ERD_REGION"));
		}	
		// 病案号
		if (getValueString("MR_NO").length() != 0){
			String mrNo = getValueString("MR_NO");
			Pat pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
			String srcMrNo = PatTool.getInstance().checkMrno(mrNo);
			if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
			}

			this.setValue("MR_NO", pat.getMrNo());
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		} 
		// 检伤号
		if (getValueString("TRIAGE_NO").length() != 0) {
			parm.setData("TRIAGE_NO", getValue("TRIAGE_NO"));
		}
		
		return parm;
	}
	
	/**
	 * 查询数据 
	 * @author wangqing 20171123
	 * @param parm
	 * @return
	 */
	public TParm query(TParm parm){
		// 已检伤（已检伤，未挂号，未抢救）
		String sql0 = " SELECT TRIAGE_NO, CASE_NO, '' AS MR_NO, '' AS PAT_NAME, ERD_REGION_CODE, BED_NO, '' AS BED_DESC "
				+ "FROM ERD_EVALUTION "
				+ "WHERE TRIAGE_TIME BETWEEN TO_DATE('"+parm.getValue("FROM_DATE")+"','yyyy/mm/dd HH24:MI:SS') AND TO_DATE('"+parm.getValue("TO_DATE")+"','yyyy/mm/dd HH24:MI:SS') "
				+ "AND CASE_NO IS NULL "
				+ "AND ERD_REGION_CODE IS NULL "
				+ "AND BED_NO IS NULL "
				+ "AND OUT_DATE IS NULL "
				+ "ORDER BY TRIAGE_NO DESC";// 按检伤号倒序			
		// 已挂号（已检伤，以挂号，未抢救）
		String sql1 = " SELECT A.TRIAGE_NO, A.CASE_NO, A.MR_NO, B.PAT_NAME, C.ERD_REGION_CODE, C.BED_NO, '' AS BED_DESC "
				+ "FROM REG_PATADM A, SYS_PATINFO B, ERD_EVALUTION C "
				+ "WHERE A.MR_NO=B.MR_NO(+) "
				+ "AND A.TRIAGE_NO=C.TRIAGE_NO(+) "
				+ "AND A.MR_NO IS NOT NULL "
				+ "AND A.TRIAGE_NO IS NOT NULL "
				+ "AND A.ADM_STATUS='1' "// 已挂号：1；已看诊：2
				+ "AND A.REG_DATE BETWEEN TO_DATE('"+parm.getValue("FROM_DATE")+"','yyyy/mm/dd HH24:MI:SS') AND TO_DATE('"+parm.getValue("TO_DATE")+"','yyyy/mm/dd HH24:MI:SS') "
				+ "AND C.ERD_REGION_CODE IS NULL "
				+ "AND C.BED_NO IS NULL "
				+ "AND C.OUT_DATE IS NULL "
				+ "ORDER BY A.CASE_NO DESC ";// 按就诊号倒序		
		// 已看诊（已检伤，以挂号，已看诊，未抢救）
		String sql2 = " SELECT A.TRIAGE_NO, A.CASE_NO, A.MR_NO, B.PAT_NAME, C.ERD_REGION_CODE, C.BED_NO, '' AS BED_DESC "
				+ "FROM REG_PATADM A, SYS_PATINFO B, ERD_EVALUTION C "
				+ "WHERE A.MR_NO=B.MR_NO(+) "
				+ "AND A.TRIAGE_NO=C.TRIAGE_NO(+) "
				+ "AND A.MR_NO IS NOT NULL "
				+ "AND A.TRIAGE_NO IS NOT NULL "
				+ "AND A.ADM_STATUS='2' "// 已挂号：1；已看诊：2
				+ "AND A.REG_DATE BETWEEN TO_DATE('"+parm.getValue("FROM_DATE")+"','yyyy/mm/dd HH24:MI:SS') AND TO_DATE('"+parm.getValue("TO_DATE")+"','yyyy/mm/dd HH24:MI:SS') "
				+ "AND C.ERD_REGION_CODE IS NULL "
				+ "AND C.BED_NO IS NULL "
				+ "AND C.OUT_DATE IS NULL "
				+ "ORDER BY A.CASE_NO DESC ";// 按就诊号倒序
		// 抢救中
		String sql3 = " SELECT A.ERD_REGION_CODE, A.BED_NO, A.BED_DESC, A.TRIAGE_NO, B.CASE_NO, B.MR_NO, B.PAT_NAME "
				+ "FROM ERD_BED A, (SELECT A.TRIAGE_NO, A.CASE_NO, B.MR_NO, B.PAT_NAME FROM ERD_EVALUTION A, (SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME FROM REG_PATADM A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO(+) AND A.MR_NO IS NOT NULL)B WHERE A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO IS NOT NULL)B "
				+ "WHERE A.TRIAGE_NO=B.TRIAGE_NO(+) "
				+ "AND A.OCCUPY_FLG='Y' "
				+ "AND A.TRIAGE_NO IS NOT NULL "
				+ "ORDER BY ERD_REGION_CODE, BED_NO ";		
		// 已转出
		String sql4 = "SELECT A.TRIAGE_NO, A.ERD_REGION_CODE, A.BED_NO, A.CASE_NO, B.MR_NO, B.PAT_NAME, C.BED_DESC "
				+ "FROM ERD_EVALUTION A, (SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME FROM REG_PATADM A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO(+))B, ERD_BED C "
				+ "WHERE A.CASE_NO=B.CASE_NO(+) "
				+ "AND A.ERD_REGION_CODE=C.ERD_REGION_CODE(+) "
				+ "AND A.BED_NO=C.BED_NO(+) "
				+ "AND A.CASE_NO IS NOT NULL "
				+ "AND A.ERD_REGION_CODE IS NOT NULL "
				+ "AND A.BED_NO IS NOT NULL "
				+ "AND A.OUT_DATE IS NOT NULL "
				+ "AND A.OUT_DATE BETWEEN TO_DATE('"+parm.getValue("FROM_DATE")+"','yyyy/mm/dd HH24:MI:SS') AND TO_DATE('"+parm.getValue("TO_DATE")+"','yyyy/mm/dd HH24:MI:SS') "
				+ "ORDER BY A.OUT_DATE ";
		TParm result = new TParm();		
		String sql = "";
		// 已检伤
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("0")){
			sql = sql0;
		}
		// 已挂号
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("1")){
			sql = sql1;
		}
		// 已看诊
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("2")){
			sql = sql2;
		}
		// 抢救中
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("3")){
			sql = sql3;
		}
		// 已转出
		if(parm.getValue("FLG")!=null && parm.getValue("FLG").equals("4")){
			sql = sql4;
		}	
		result = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("@test by wangqing@---FLG="+parm.getValue("FLG"));
		System.out.println("@test by wangqing@---sql="+sql);
		System.out.println("@test by wangqing@---result="+result);
		return result;
	}
	
	/**
	 * 切换查询按钮
	 */
	public void onChangeAction(){
		this.onClear();// 清空
		onQuery();
	}
	
	/**
	 * 单击事件
	 * @param row
	 */
	public void onTableClicked(int row){		
		if (row < 0){
			return;
		}
		TParm tableValue = table.getParmValue();
		String caseNo = (String) tableValue.getData("CASE_NO", row);
		String mrNo = (String) tableValue.getData("MR_NO", row);
		String triageNo = (String) tableValue.getData("TRIAGE_NO", row);
		String patName = (String) tableValue.getData("PAT_NAME", row);		
		String  bedNo= (String) tableValue.getData("BED_NO", row);
		String erdRegionCode = (String) tableValue.getData("ERD_REGION_CODE", row);
		String  bedDesc= (String) tableValue.getData("BED_DESC", row);
		// 赋值
		MR_NO.setValue(mrNo);
		TRIAGE_NO.setValue(triageNo);
		PAT_NAME.setValue(patName);	
		BED_DESC.setValue(bedDesc);
		ERD_REGION_CODE.setValue(erdRegionCode);
//		// 打开关联界面
//		TParm parm = new TParm();
//		parm.setData("CASE_NO", caseNo);
//		parm.setData("MR_NO", mrNo);
//		parm.setData("TRIAGE_NO", triageNo);
//		parm.setData("PAT_NAME", patName);	
//		parm.setData("ERD_REGION_CODE", erdRegionCode);
//		parm.setData("BED_NO", bedNo);
//		parm.setData("BED_DESC", bedDesc);		
//		parm.setData("FLG", "NURSE");// 护士调用标记，有别于医生			
//		
//		
//		System.out.println("---caseNo="+caseNo);
//		
//		if(runFlg !=null && runFlg.equals("RECORD")){// 急诊抢救设定		
//			this.openWindow("%ROOT%\\config\\erd\\ERDDynamicRcd2.x", parm, false);				
//		}else if(runFlg !=null && runFlg.equals("CHECK")){// 急诊抢救护士站
//			if(caseNo == null || caseNo.trim().length()<=0){
//				this.messageBox("此病患未挂号！！！");
//				return;
//			}
//			String sql = " SELECT ADM_STATUS FROM REG_PATADM WHERE CASE_NO='"+caseNo+"' ";
//			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//			if(result.getErrCode()<0){
//				this.messageBox("bug:::查询ADM_STATUS错误");
//				return;
//			}
//			if(result.getCount()<=0){
//				this.messageBox("bug:::没有此病患挂号信息！！！");
//				return;
//			}
//			if(result.getValue("ADM_STATUS", 0) == null 
//					|| result.getValue("ADM_STATUS", 0).trim().length()<=0 
//					|| !result.getValue("ADM_STATUS", 0).equals("2")){
//				this.messageBox("此病患未看诊！！！");
//				return;
//			}
//			this.openWindow("%ROOT%\\config\\erd\\ERDOrderExecMain.x", parm, false);	
//		}else{
//			
//		}
	}
	
	/**
	 * 双击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableDoubled(int row) {
		if (row < 0) return;
		TParm tableValue = table.getParmValue();
		String caseNo = (String) tableValue.getData("CASE_NO", row);
		String mrNo = (String) tableValue.getData("MR_NO", row);
		String triageNo = (String) tableValue.getData("TRIAGE_NO", row);
		String patName = (String) tableValue.getData("PAT_NAME", row);		
		String  bedNo= (String) tableValue.getData("BED_NO", row);
		String erdRegionCode = (String) tableValue.getData("ERD_REGION_CODE", row);
		String  bedDesc= (String) tableValue.getData("BED_DESC", row);
		// 初始化控件
		MR_NO.setValue(mrNo);
		TRIAGE_NO.setValue(triageNo);
		PAT_NAME.setValue(patName);	
		BED_DESC.setValue(bedDesc);
		ERD_REGION_CODE.setValue(erdRegionCode);
		if(runFlg!=null && runFlg.equals("RECORD")){
			lockUpContorl(false);
			// 调用界面传参
			TParm parmToErd = new TParm();
			parmToErd.setData("CASE_NO", caseNo);
			parmToErd.setData("MR_NO", mrNo);
			parmToErd.setData("TRIAGE_NO", triageNo);
			parmToErd.setData("PAT_NAME", patName);	
			parmToErd.setData("ERD_REGION_CODE", erdRegionCode);
			parmToErd.setData("BED_NO", bedNo);
			parmToErd.setData("BED_DESC", bedDesc);
			// 护士调用标记，有别于医生
			parmToErd.setData("FLG", "NURSE");
			// 加载ERD记录主界面
			table.setVisible(false);
			panel.addItem("ERDDynamicRcd", "%ROOT%\\config\\erd\\ERDDynamicRcd2.x", parmToErd, false);
			workPanelTag = "ERDDynamicRcd";
		}else if(runFlg!=null && runFlg.equals("CHECK")){
			if(caseNo == null || caseNo.trim().length()<=0){
				this.messageBox("此病患未看诊！！！");
				return;
			}
			String sql = " SELECT ADM_STATUS FROM REG_PATADM WHERE CASE_NO='"+caseNo+"' ";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getErrCode()<0){
				this.messageBox("bug:::查询ADM_STATUS错误");
				return;
			}
			if(result.getCount()<=0){
				this.messageBox("bug:::没有此病患信息！！！");
				return;
			}
			if(result.getValue("ADM_STATUS", 0)!=null && result.getValue("ADM_STATUS", 0).equals("2")){
				
			}else{
				this.messageBox("此病患未看诊！！！");
				return;
			}		
			
			// add by wangqing 20180205 start
			// 急诊抢救护士站，每次进去的时候，判断患者是否处于抢救中并且ERD_RECORD是否有记录。如果患者处于抢救中并且ERD_RECORD没有记录，补录一条数据
			String s = "";
			TParm r = new TParm();
			s = "SELECT A.ERD_REGION_CODE, A.BED_NO, A.BED_DESC, A.OCCUPY_FLG, A.TRIAGE_NO FROM ERD_BED A "
					+ "WHERE A.OCCUPY_FLG='Y' AND A.TRIAGE_NO='"+triageNo+"'";
			r = new TParm(TJDODBTool.getInstance().select(s));	
			if(r.getErrCode()<0){
				this.messageBox(r.getErrText());
				return;
			}				
			TParm erdRecord = getErdRecord(caseNo);
			if(erdRecord.getErrCode()<0){
				this.messageBox(erdRecord.getErrText());
				return;
			}			
			if(r.getCount()>0 && erdRecord.getCount()<=0){
				// 插入ERD_RECORD并且更新ERD_BED
				this.insertErdRecordAndUpdateErdBed(mrNo, caseNo, triageNo);
			}
			// add by wangqing 20180205 end
						
			lockUpContorl(false);
			// 调用界面传参
			TParm parmToExec = new TParm();
			parmToExec.setData("MR_NO", mrNo);
			parmToExec.setData("CASE_NO", caseNo);
			parmToExec.setData("PAT_NAME", patName);			
			parmToExec.setData("TRIAGE_NO", triageNo);
			parmToExec.setData("ERD_REGION_CODE", erdRegionCode);
			parmToExec.setData("BED_NO", bedNo);
			parmToExec.setData("BED_DESC", bedDesc);
			// 加载ERD记录主界面
			table.setVisible(false);
			panel.addItem("ERDDynamicRcd", "%ROOT%\\config\\erd\\ERDOrderExecMain.x", parmToExec, false);
			workPanelTag = "ERDDynamicRcd";
		}else{
			return;
		}
	}
	
	/**
	 * 清空方法
	 */
	public void onClear() {
		setValue("MR_NO", "");
		setValue("TRIAGE_NO", "");
		setValue("PAT_NAME", "");
		setValue("ERD_REGION", "");
		setValue("BED_DESC", "");
		((TTable) getComponent("TABLE")).removeRowAll();
	}
	
	/**
	 * 医疗卡读卡方法
	 */
	public void onEKT() {
		TParm patParm = EKTIO.getInstance().TXreadEKT();
		// TParm patParm = EKTIO.getInstance().getPat();
		if (patParm.getErrCode() < 0) {
			this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
			return;
		}
		setValue("MR_NO", patParm.getValue("MR_NO"));
		onMrNo();
	}
	
	/**
	 * 补齐MR_NO
	 */
	public void onMrNo() {
		String mrNo = MR_NO.getValue();
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
		String srcMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
			this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
		}
		MR_NO.setValue(pat.getMrNo());
		PAT_NAME.setValue(pat.getName());
	}

	/**
	 * 检伤评估表单查看
	 */
	public void onErdTriage(){
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox_("请选择病患！");
			return;
		}
		TParm dataD = table.getParmValue();
		String caseNo = dataD.getValue("CASE_NO",row);
		String mrNo = dataD.getValue("MR_NO",row);
		String triageNo = dataD.getValue("TRIAGE_NO",row);
		if(triageNo.length() == 0){
			this.messageBox("请选择有检伤号的病患！");
			return;
		}
		String[] saveFiles = ERDLevelTool.getInstance().getELFile(triageNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		Pat pat = Pat.onQueryByMrNo(mrNo);
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		parm.setData("ADM_DATE", reg.getAdmDate());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX", pat.getSexString());
		parm.setData("AGE", OdoUtil.showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate())); //年龄
		TParm emrFileData = new TParm();
		emrFileData.setData("FILE_PATH", saveFiles[0]);
		emrFileData.setData("FILE_NAME", saveFiles[1]);
		emrFileData.setData("FLG", true);
		parm.setData("EMR_FILE_DATA", emrFileData);
		parm.setData("SYSTEM_TYPE", "EMG");
		parm.setData("RULETYPE", "1");
		parm.setData("ERD",true); 
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * 测试
	 */
	public void onTest(){
		TParm parm = new TParm();	
		parm.setData("triageNo", "20171123001");
		this.openWindow("%ROOT%\\config\\reg\\REGPreviewingPreedWindow.x", parm);	
	}
	
	/**
	 * 关闭工作页面
	 * 
	 * @return boolean
	 */
	public Object onClose() {
		if (workPanelTag == null || workPanelTag.trim().length() <= 0){ 
			return null;
		}
		TPanel p = (TPanel) getComponent(workPanelTag);
		if(p==null){
			return null;
		}else{
			
		}
		if (!p.getControl().onClosing()) {
			return "OK";
		}
		panel.remove(p);
		workPanelTag = null;
		table.setVisible(true);
		// 移除子UIMenuBar
		callFunction("UI|removeChildMenuBar");
		// 移除子UIToolBar
		callFunction("UI|removeChildToolBar");
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
		lockUpContorl(true);
		onQuery();
		return "OK";
	}
	
	// --------------tool start--------------
	/**
	 * 加锁、解锁上边的控件
	 * 
	 * @param flg
	 *            boolean
	 */
	private void lockUpContorl(boolean flg) {
		Radio0.setEnabled(flg);
		Radio1.setEnabled(flg);
		Radio2.setEnabled(flg);
		Radio3.setEnabled(flg);
		Radio4.setEnabled(flg);
		MR_NO.setEnabled(flg);
		PAT_NAME.setEnabled(flg);
		TRIAGE_NO.setEnabled(flg);
		BED_DESC.setEnabled(flg);
		ERD_REGION_CODE.setEnabled(flg);
	}
	// --------------tool end----------------
	
	
	
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("erd\\ERDMainEnter.x");
	}
	
	// ------------------------------------add by wangqing 2080131 start------------------------------
	
	/**
	 * 插入ERD_RECORD并且更新ERD_BED
	 * @author wangqing 20180131
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 */
	public TParm insertErdRecordAndUpdateErdBed(String mrNo, String caseNo, String triageNo){
		TParm parm = new TParm();
		parm = copyPatDate(mrNo, caseNo, triageNo);
		if(parm.getErrCode()<0){
			return parm;
		}
		parm = TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "insertErdRecordAndUpdateErdBed", parm);
		return parm;
	}
	
	/**
	 * 复制患者基本信息
	 * @param mrNo
	 * @param caseNo
	 * @param inDate
	 * @param erdRegion
	 * @param bedNo
	 * @return
	 */
	public TParm copyPatDate(String mrNo, String caseNo, Timestamp inDate, String erdRegion, String bedNo) {
		// 患者基本信息
		TParm sysPatInfoParm = this.getPatInfo(mrNo);
		if(sysPatInfoParm.getErrCode()<0 || sysPatInfoParm.getCount()<=0){
			sysPatInfoParm.setErrCode(-1);
			sysPatInfoParm.setErrText("获取患者基本信息错误");
			return sysPatInfoParm;
		}	
		// 患者挂号信息
		TParm sysRegPatAdm = this.getPatRegInfo(caseNo);
		if(sysRegPatAdm.getErrCode()<0 || sysRegPatAdm.getCount()<=0){
			sysRegPatAdm.setErrCode(-1);
			sysRegPatAdm.setErrText("获取患者挂号信息错误");
			return sysRegPatAdm;
		}
				
		/*// 抢救信息
		TParm erdParm = getErdInfo(triageNo);
		if(erdParm.getErrCode()<0 || erdParm.getCount()<=0){
			return erdParm;
		}	
		// 开始抢救时间
		TParm erdStartTimeParm = getErdStartTime(triageNo);
		if(erdStartTimeParm.getErrCode()<0 || erdStartTimeParm.getCount()<=0){
			return erdStartTimeParm;
		}*/	
				
		TParm result = new TParm();
		
		result.setData("CASE_NO", caseNo);
		
		result.setData("MR_NO", mrNo);
		
		result.setData("ERD_NO", "");// 暂留

		result.setData("STATUS", "0");// 0、抢救中；1、出院；2、转住院；3、召回
		
		setNull(result, "DISCHG_TYPE;DISCHG_DATE;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE;RETURN_DATE");

		// 设置患者基本信息
		setValue(sysPatInfoParm, result, "PAT_NAME;SEX;AGE;BIRTH_DATE;MARRIGE;OCCUPATION;RESID_PROVICE;RESID_PROVICE_DESC;RESID_COUNTRY;"
				+ "FOLK;NATION;IDNO;CTZ1_CODE;OFFICE;CONTACTER;RELATIONSHIP;CONT_ADDRESS;CONT_TEL");
		
		setNull(result, "O_ADDRESS;O_TEL;O_POSTNO;H_ADDRESS;H_TEL;H_POSTNO");

		/*if(erdStartTimeParm.getData("IN_DATE", 0)!=null && erdStartTimeParm.getData("IN_DATE", 0).toString().trim().length()>0){
			result.setData("IN_DATE", erdStartTimeParm.getData("IN_DATE", 0));// 入抢救日期
		}else{
			result.setData("IN_DATE", "");// 入抢救日期
		}			
		result.setData("ERD_REGION", erdParm.getValue("ERD_REGION_CODE", 0));// 入抢救区
		// 抢救床
		result.setData("BED_NO", erdParm.getValue("BED_NO", 0));*/	
		result.setData("IN_DATE", inDate);// 入抢救日期
		
		result.setData("ERD_REGION", erdRegion);// 入抢救区
		
		result.setData("BED_NO", bedNo);// 抢救床
				
		result.setData("IN_DEPT", sysRegPatAdm.getValue("DEPT_CODE", 0));// 人抢救科室
		
		setNull(result, "OUT_DATE;OUT_ERD_REGION;OUT_DEPT");

		setNull(result, "OUT_DIAG_CODE;CODE_REMARK;CODE_STATUS;HEAL_LV");

		setNull(result, "OP_CODE;OP_DATE;MAIN_SUGEON;OP_LEVEL");

		setNull(result, "GET_TIMES;SUCCESS_TIMES");

		result.setData("DR_CODE", sysRegPatAdm.getValue("REALDR_CODE", 0));

		result.setData("REAL_STAY_DAYS", 1);// 当第一次COPY数据到ERD_RECORD表中的时候默认天数-‘1’

		setNull(result, "ACCOMPANY_WEEK;ACCOMPANY_MONTH;ACCOMPANY_YEAR;ACCOMP_DATE");

		result.setData("OPT_USER", Operator.getID());

		result.setData("OPT_TERM", Operator.getIP());
		return result;
	}
	
	/**
	 * 复制患者基本信息
	 * @author WangQing
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 * @return
	 */
	public TParm copyPatDate(String mrNo, String caseNo, String triageNo) {
		// 患者基本信息
		TParm sysPatInfoParm = this.getPatInfo(mrNo);
		if(sysPatInfoParm.getErrCode()<0 || sysPatInfoParm.getCount()<=0){
			sysPatInfoParm.setErrCode(-1);
			sysPatInfoParm.setErrText("获取患者基本数据错误");
			return sysPatInfoParm;
		}	
		// 患者挂号信息
		TParm sysRegPatAdm = this.getPatRegInfo(caseNo);
		if(sysRegPatAdm.getErrCode()<0 || sysRegPatAdm.getCount()<=0){
			sysRegPatAdm.setErrCode(-1);
			sysRegPatAdm.setErrText("获取患者挂号数据错误");
			return sysRegPatAdm;
		}		
		// 抢救信息
		TParm erdParm = getErdInfo(triageNo);
		if(erdParm.getErrCode()<0 || erdParm.getCount()<=0){
			erdParm.setErrCode(-1);
			erdParm.setErrText("获取患者抢救信息错误");
			return erdParm;
		}	
		// 开始抢救时间
		TParm erdStartTimeParm = getErdStartTime(triageNo);
		if(erdStartTimeParm.getErrCode()<0 || erdStartTimeParm.getCount()<=0){
			erdStartTimeParm.setErrCode(-1);
			erdStartTimeParm.setErrText("获取患者开始抢救时间错误");
			return erdStartTimeParm;
		}	
		
		TParm result = new TParm();
		result.setData("CASE_NO", caseNo);
		result.setData("MR_NO", mrNo);
		
		result.setData("TRIAGE_NO", triageNo);// 检伤号
		
		result.setData("ERD_NO", "");// 暂留
		result.setData("STATUS", "0");// 0、抢救中；1、出院；2、转住院；3、召回
		setNull(result, "DISCHG_TYPE;DISCHG_DATE;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE;RETURN_DATE");
		
		// 基本信息
		setValue(sysPatInfoParm, result, "PAT_NAME;SEX;AGE;BIRTH_DATE;MARRIGE;OCCUPATION;RESID_PROVICE;RESID_PROVICE_DESC;RESID_COUNTRY;"
				+ "FOLK;NATION;IDNO;CTZ1_CODE;OFFICE;CONTACTER;RELATIONSHIP;CONT_ADDRESS;CONT_TEL");
		setNull(result, "O_ADDRESS;O_TEL;O_POSTNO;H_ADDRESS;H_TEL;H_POSTNO");

		// 抢救入床数据
		if(erdStartTimeParm.getData("IN_DATE", 0)!=null && erdStartTimeParm.getData("IN_DATE", 0).toString().trim().length()>0){
			result.setData("IN_DATE", erdStartTimeParm.getData("IN_DATE", 0));// 入抢救日期
		}else{
			result.setData("IN_DATE", "");// 入抢救日期
		}			
		result.setData("ERD_REGION", erdParm.getValue("ERD_REGION_CODE", 0));// 入抢救区
		
		result.setData("BED_NO", erdParm.getValue("BED_NO", 0));// 抢救床
				
		/*result.setData("IN_DATE", inDate);// 入抢救日期
		result.setData("ERD_REGION", erdRegion);// 入抢救区
		result.setData("BED_NO", bedNo);*/
		
		result.setData("IN_DEPT", sysRegPatAdm.getValue("DEPT_CODE", 0));// 人抢救科室
		
		setNull(result, "OUT_DATE;OUT_ERD_REGION;OUT_DEPT");

		setNull(result, "OUT_DIAG_CODE;CODE_REMARK;CODE_STATUS;HEAL_LV");

		setNull(result, "OP_CODE;OP_DATE;MAIN_SUGEON;OP_LEVEL");

		setNull(result, "GET_TIMES;SUCCESS_TIMES");

		result.setData("DR_CODE", sysRegPatAdm.getValue("REALDR_CODE", 0));

		result.setData("REAL_STAY_DAYS", 1);// 当第一次COPY数据到ERD_RECORD表中的时候默认天数-‘1’

		setNull(result, "ACCOMPANY_WEEK;ACCOMPANY_MONTH;ACCOMPANY_YEAR;ACCOMP_DATE");

		result.setData("OPT_USER", Operator.getID());
		
		result.setData("OPT_TERM", Operator.getIP());

		return result;
	}

	/**
	 * 获取患者基本信息
	 * @param mrNo
	 * @return
	 */
	public TParm getPatInfo(String mrNo){
		String sql = "SELECT * FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()<0 || parm.getCount()<=0){
			parm.setErrCode(-1);
			parm.setErrText("getPatInfo err");			
			return parm;
		}
		// 处理字段，换一下名称
		parm.setData("SEX", 0, parm.getData("SEX_CODE", 0));
		parm.setData("MARRIGE", 0, parm.getData("MARRIAGE_CODE", 0));
		parm.setData("OCCUPATION", 0, parm.getData("OCC_CODE", 0));

		// 
		if(parm.getData("BIRTH_DATE", 0)!=null){
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			parm.setData("AGE", 0, StringUtil.showAge(parm.getTimestamp("BIRTH_DATE", 0), now));
		}else{
			parm.setData("AGE", 0, "");
		}

		// RESID_PROVICE省编号
		if(parm.getValue("RESID_POST_CODE", 0)!=null && parm.getValue("RESID_POST_CODE", 0).trim().length()>=2){
			parm.setData("RESID_PROVICE", 0, parm.getValue("RESID_POST_CODE", 0).substring(0, 2));
		}else{
			parm.setData("RESID_PROVICE", 0, "");
		}
		// RESID_PROVICE_DESC省描述
		if(parm.getValue("RESID_PROVICE", 0)!=null && parm.getValue("RESID_PROVICE", 0).trim().length()>0){
			parm.setData("RESID_PROVICE_DESC", 0, getPatHome(parm.getValue("RESID_PROVICE", 0)).getValue("HOMEPLACE_DESC", 0));		
		}else{
			parm.setData("RESID_PROVICE_DESC", 0, "");	
		}
		// 户籍编号	
		parm.setData("RESID_COUNTRY", 0, parm.getValue("RESID_POST_CODE", 0));
		
		parm.setData("FOLK", 0, parm.getValue("SPECIES_CODE", 0));
		parm.setData("NATION", 0, parm.getValue("NATION_CODE", 0));
		parm.setData("OFFICE", 0, parm.getValue("COMPANY_DESC", 0));

		parm.setData("CONTACTER", 0, parm.getValue("CONTACTS_NAME", 0));
		parm.setData("RELATIONSHIP", 0, parm.getValue("RELATION_CODE", 0));
		parm.setData("CONT_ADDRESS", 0, parm.getValue("CONTACTS_ADDRESS", 0));
		parm.setData("CONT_TEL", 0, parm.getValue("CONTACTS_TEL", 0));
		return parm;
	}

	/**
	 * 获取患者挂号信息
	 * @param caseNo
	 * @return
	 */
	public TParm getPatRegInfo(String caseNo) {
		String sql = " SELECT REALDR_CODE,ADM_DATE FROM REG_PATADM WHERE CASE_NO='" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	/**
	 * 校验患者是否占床
	 * @param traigeNo 检伤号
	 * @return result：如果result.getCount()>0，占床；否则，没有占床
	 */
	public TParm getErdInfo(String triageNo){
		String sql =" SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO FROM ERD_BED WHERE TRIAGE_NO='"+triageNo+"' AND OCCUPY_FLG='Y' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 查询抢救开始时间
	 * @param triageNo
	 * @return
	 */
	public TParm getErdStartTime(String triageNo){
		String sql = " SELECT MIN(S_M_TIME) AS IN_DATE FROM AMI_E_S_RECORD WHERE TRIAGE_NO='"+triageNo+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 批量赋值
	 * @param parm
	 * @param names
	 * @return
	 */
	public boolean setNull(TParm parm, String names){
		if(parm==null){
			return false;
		}
		if(names==null || names.trim().length()<=0){
			return false;
		}
		String[] nameArr = names.split(";");
		for(int i=0; i<nameArr.length; i++){
			parm.setData(nameArr[i], "");
		}
		return true;
	}

	/**
	 * 批量赋值
	 * @author wangqing 20171205
	 * @param parm0
	 * @param parm1
	 * @param names
	 * @return
	 */
	public boolean setValue(TParm parm0, TParm parm1, String names){
		if(parm0==null){
			return false;
		}
		if(parm1==null){
			return false;
		}
		if(names==null || names.trim().length()<=0){
			return false;
		}
		String[] nameArr = names.split(";");
		for(int i=0; i<nameArr.length; i++){
			parm1.setData(nameArr[i], parm0.getData(nameArr[i], 0));
		}
		return true;
	}
	
	/**
	 * 获取省市名称
	 * @param code
	 * @return
	 */
	public TParm getPatHome(String code) {
		String sql = "SELECT HOMEPLACE_DESC FROM SYS_HOMEPLACE WHERE HOMEPLACE_CODE='" + code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	

	/**
	 * 获取ERD_RECORD数据
	 * @param caseNo
	 * @return
	 */
	public TParm getErdRecord(String caseNo){ 
		String sql = "SELECT * FROM ERD_RECORD WHERE CASE_NO='" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	
	
	// ------------------------------------add by wangqing 2080131 end------------------------------


	
	
	

}
