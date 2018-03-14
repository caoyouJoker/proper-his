package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 * 急诊抢救记录
 * @author WangQing 20170327
 *
 */
public class REGSaveLabelControl extends TControl{
	TTable table;// 体征表格
	TTable orderT;// 口头医嘱表格（隐藏）

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		orderT = (TTable) this.getComponent("ORDER");
		// table单击事件
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");
		// table之checkBox事件
		this.callFunction("UI|TABLE|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxChangeValue");		
	}

	/**
	 * 检伤号回车查询
	 * 
	 * @author wangqing 20170627
	 */
	public void onTriageNo(){
		// 初始化查询参数
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("请输入检伤号！！！");
			return;
		}
		String mrNo = this.getValueString("MR_NO");	
		String vsTimeS = this.getValueString("VS_TIME_S");// 起始查询区间
		String vsTimeE = this.getValueString("VS_TIME_E");// 结束查询区间	
		this.onClear();
		this.setValue("TRIAGE_NO", triageNo);
		this.setValue("MR_NO", mrNo);
		if(vsTimeS != null && vsTimeS.trim().length()>0){
			vsTimeS = vsTimeS.replace("-", "/").substring(0, 16);
			this.setValue("VS_TIME_S", vsTimeS);
		}
		if(vsTimeE != null && vsTimeE.trim().length()>0){
			vsTimeE = vsTimeE.replace("-", "/").substring(0, 16);
			this.setValue("VS_TIME_E", vsTimeE);
		}
		// 查询
		String sql = "";
		TParm result = new TParm();
		sql = " SELECT A.TRIAGE_NO, A.CASE_NO, A.MR_NO, A.LEVEL_CODE, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE FROM ERD_EVALUTION A, SYS_PATINFO B WHERE A.MR_NO = B.MR_NO(+) AND A.TRIAGE_NO='"+triageNo+"' ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("没有此检伤号数据");
			return;
		}	
		this.setValue("CASE_NO", result.getValue("CASE_NO", 0));// add by wangqing 20170922 新增急诊就诊号
		this.setValue("MR_NO", result.getValue("MR_NO", 0));
		this.setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		this.setValue("SEX_CODE", result.getValue("SEX_CODE", 0));
		this.setValue("AGE", this.getAge(result.getTimestamp("BIRTH_DATE", 0)));	
		this.setValue("DISEASE_CLASS", result.getValue("LEVEL_CODE", 0));// add by wangqing 20170921 病情分级取检伤等级
		// 初始化生命体征数据
		onSelectVSData();
	}

	/**
	 * 病案号回车查询
	 * 
	 * @author wangqing 20170627
	 */
	public void onMrNo(){
		// 初始化参数
		String mrNo = this.getValueString("MR_NO");
		if(mrNo == null || mrNo.trim().length()==0){
			this.messageBox("请输入病案号！！！");
			return;
		}
		String triageNo = this.getValueString("TRIAGE_NO");
		String vsTimeS = this.getValueString("VS_TIME_S");	
		String vsTimeE = this.getValueString("VS_TIME_E");
		this.onClear();
		this.setValue("TRIAGE_NO", triageNo);
		//		this.setValue("MR_NO", mrNo);
		if(vsTimeS != null && vsTimeS.trim().length()>0){
			vsTimeS = vsTimeS.replace("-", "/").substring(0, 16);
			this.setValue("VS_TIME_S", vsTimeS);
		}
		if(vsTimeE != null && vsTimeE.trim().length()>0){
			vsTimeE = vsTimeE.replace("-", "/").substring(0, 16);
			this.setValue("VS_TIME_E", vsTimeE);
		}
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
		if(pat == null){
			this.messageBox("没有此病患信息！！！");
			return;
		}
		String srcMrNo = PatTool.getInstance().checkMrno(mrNo);// 补零
		if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());			
		}
		this.setValue("MR_NO", pat.getMrNo());
		// 查询
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		Object obj = this.openDialog("%ROOT%\\config\\erd\\ERDSavePat.x", parm);
		if(obj != null && obj instanceof TParm){
			TParm result = (TParm) obj;
			this.setValue("TRIAGE_NO", result.getValue("TRIAGE_NO"));
		}else{
			return;
		}
		onTriageNo();
	}

	/**
	 * 查询（检伤号查询优先）
	 */
	public void onQuery(){
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo != null && triageNo.trim().length()>0){
			this.onTriageNo();
			return;
		}
		String mrNo = this.getValueString("MR_NO");
		if(mrNo != null && mrNo.trim().length()>0){
			this.onMrNo();
			return;
		}
		this.messageBox("请输入检伤号或者病案号查询！！！");	
	}

	/**
	 * 刷新
	 */
	public void onResets(){
		this.onQuery();
	}

	/**
	 * 查询体征数据
	 * @param triageNo
	 */
	public void onSelectVSData(){
		TParm result = new TParm();
		result = this.queryVSData();
		if(result == null){
//			this.messageBox("result is null");
			return;
		}
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return;
		}
		TablePublicTool.setParmValue(table, result);	
	}

	/**
	 * 查询生命体征数据
	 */
	public TParm queryVSData(){
		// 1、先刷新数据
		transferVSData();
		// 2、再更新数据
		String triageNo = this.getValueString("TRIAGE_NO");
		String startTime = this.getValueString("VS_TIME_S");
		String endTime = this.getValueString("VS_TIME_E");

		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("请输入检伤号！！！");
			return null;
		}
		String sql ="";
		TParm result = new TParm();
		sql = "SELECT 'N' AS SEL_FLG, TRIAGE_NO, BED_NO, VS_TIME, TEMPERATURE, CARDIOTACH, RESPIRATORY_RATE, SPO2, PAIN,NBPS,NBPD, OXY_SUPPLY_TYPE, OXY_SUPPLY_RATE, CONDITION, SIGN FROM AMI_ERD_VTS_RECORD WHERE TRIAGE_NO = '" + triageNo + "' @ ORDER BY VS_TIME DESC ";// 降序
		String where = "";
		if(startTime != null && startTime.trim().length()>0){
			startTime = startTime.replace("-", "").replace(" ", "").replace(":", "").substring(0, 12);// 201706281507
			where += " AND VS_TIME >= '"+startTime+"' " ;
		}
		if(endTime != null && endTime.trim().length()>0){
			endTime = endTime.replace("-", "").replace(" ", "").replace(":", "").substring(0, 12);// 201706281507
			where += " AND VS_TIME <= '"+endTime+"' ";
		}
		if(startTime != null && startTime.trim().length()>0 && endTime != null && endTime.trim().length()>0){
			if(startTime.compareTo(endTime)>0){
				this.messageBox("开始时间不能大于结束时间！！！");
				return null;
			}			
			where += " AND '"+startTime+"' <= '"+endTime+"' ";
		}
		sql = sql.replace("@", where);


		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return result;
		}
		if(result.getCount()<=0){
//			this.messageBox("result.getCount()<=0");
			return null;
		}
		return result;
	}

	/**
	 * 交换生命体征数据
	 */
	public void transferVSData(){
		String triageNo = this.getValueString("TRIAGE_NO");
		String sql ="";
		TParm result = new TParm();
		sql = " SELECT TRIAGE_NO, BED_NO, TO_CHAR (S_M_TIME, 'yyyyMMddHH24MI') AS S_M_TIME, TO_CHAR (E_M_TIME, 'yyyyMMddHH24MI') AS E_M_TIME FROM AMI_E_S_RECORD WHERE TRIAGE_NO='"+triageNo+"' ORDER BY S_M_TIME ASC ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("交换生命体征数据err!!!");
			return;
		}
		if(result.getCount()<=0){
			return;
		}
		// 遍历每一条数据，符合条件的插入到AMI_ERD_VTS_RECORD表中
		// 1、获取AMI_ERD_VTS_RECORD.MAX(VS_TIME)
		// 2、如果MAX(VS_TIME)为空，flg1=true;否则，flg1=false
		// 3.执行循环
		// 3、如果flg1=true，直接插入此区间的所有数据；如果flg1=false，继续判断flg2
		// 4、如果MAX(VS_TIME)在此区间内，则flg2=true；否则，flg2=false
		// 5、如果flg2=true，插入从MAX(VS_TIME)到结束检测时间内的所有记录；否则，不插入数据

		boolean flg1 = false;
		boolean flg2= false;
		String sTime = "";
		String sql2 ="";
		TParm result2 = new TParm();
		sql2 = "SELECT MAX(VS_TIME) VS_TIME FROM AMI_ERD_VTS_RECORD WHERE TRIAGE_NO = '" + triageNo + "'";
		result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if(result2.getErrCode()<0){
			this.messageBox("交换生命体征数据err!!!");
			return;
		}
		if(result2.getValue("VS_TIME", 0) == null || result2.getValue("VS_TIME", 0).trim().length()<=0){
			flg1 = true;
			sTime = "";
		}else{
			flg1 = false;
			sTime = result2.getValue("VS_TIME", 0);// 201706271516
		}	
		String now = this.dateToString(new Date(), "yyyyMMddHHmm");

		for(int i=0; i<result.getCount(); i++){			
			String bedNo = result.getValue("BED_NO", i);
			String startMontorTime = result.getValue("S_M_TIME", i);
			String endMontorTime = result.getValue("E_M_TIME", i);
			if(endMontorTime == null || endMontorTime.trim().length()<=0){
				endMontorTime = now;
			}			
			if(flg1){				
				//直接插入此区间的所有数据 
				insertVSData(startMontorTime, endMontorTime, triageNo, bedNo, true);
				continue;
			}
			if(startMontorTime.compareTo(sTime) <=0 &&  endMontorTime.compareTo(sTime)>=0 && startMontorTime.compareTo(endMontorTime)<=0){
				flg1 = true;
				flg2 = true;
			}else{
				flg1 = false;
				flg2 = false; 
			}
			if(flg2){
				// 插入从MAX(VS_TIME)到结束检测时间内的所有记录
				insertVSData(sTime, endMontorTime, triageNo, bedNo, false);
			}
		}

	}

	/**
	 * 插入生命体征数据
	 * @author wangqing 20170627
	 * @param startTime
	 * @param endTime
	 * @param triageNo
	 * @param bedNo
	 * @param flg 
	 */
	public void insertVSData(String startTime, String endTime, String triageNo, String bedNo, boolean flg){
		if(startTime == null || startTime.trim().length()<=0 
				|| endTime == null || endTime.trim().length()<=0 
				|| triageNo == null || triageNo.trim().length()<=0 
				|| bedNo == null || bedNo.trim().length()<=0){
			return;
		}
		String sql = "";
		String where = "";
		TParm result = new TParm();
		sql = "INSERT INTO AMI_ERD_VTS_RECORD (TRIAGE_NO, BED_NO, VS_TIME, TEMPERATURE, "

					+ "CARDIOTACH, RESPIRATORY_RATE, SPO2, PAIN,NBPS,NBPD, OXY_SUPPLY_TYPE, OXY_SUPPLY_RATE) "

					+ "SELECT DISTINCT '" + triageNo + "' AS TRIAGE_NO, A.BED_NO, A.MONITOR_TIME AS VS_TIME, "

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='BT2' ) AS TEMPERATURE, "// 温度

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='HR' ) AS CARDIOTACH, "// 心率

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='RR' ) AS RESPIRATORY_RATE, "// 呼吸频率

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='SPO2' ) AS SPO2, "// 氧饱和度

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='PAIN' ) AS PAIN, "// 疼痛

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='NBPS' ) AS NBPS, "// NBPS 收缩压

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='NBPD' ) AS NBPD, "// NBPD 舒张压

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='OXY_SUPPLY_TYPE' ) AS OXY_SUPPLY_TYPE, "// 供氧方式

	                + "(SELECT B.MONITOR_VALUE FROM ERD_CISVITALSIGN B WHERE A.BED_NO = B.BED_NO AND A.MONITOR_TIME = B.MONITOR_TIME AND B.MONITOR_ITEM_EN ='OXY_SUPPLY_RATE' ) AS OXY_SUPPLY_RATE " // 供氧(升/分)

	                + "FROM ERD_CISVITALSIGN A "

	                //					+ "WHERE A.MONITOR_TIME >= '" + startTime + "' "

					+ " @ "

					+ "AND A.MONITOR_TIME <= '" + endTime + "' "

					+ "AND "+startTime + "<="+endTime+" "

					+ "AND A.BED_NO = '" + bedNo + "' "

					//					+ "AND A.BED_NO = 'E002' "

					+ "ORDER BY A.MONITOR_TIME"; 
		if(flg){// >=
			where += "WHERE A.MONITOR_TIME >= '" + startTime + "' ";
		}else{// >
			where += "WHERE A.MONITOR_TIME > '" + startTime + "' ";
		}		
		sql = sql.replace("@", where);
		result = new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 病情摘要词组
	 */
	public void onCondition(){
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		//inParm.setData("DR_CODE", "000498");
		//inParm.setData("DEPT_CODE", "000498");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onConditionReturn");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setVisible(true);
	}

	/**
	 * 片语记录界面回传值
	 * 
	 * @param value
	 *            String
	 */
	public void onConditionReturn(String value) {
		TTextArea CONDITION = (TTextArea) this.getComponent("CONDITION");
		CONDITION.setText(value);
		this.onQuery();
	}

	/**
	 * table单击事件
	 */
	public void onTABLEClicked(int row){
		if(row<0){
			this.messageBox("row<0");
			return;
		}
		if(table == null){
			this.messageBox("table is null");
			return;

		}
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		TParm selectRowParm = tableParm.getRow(row);
		setValueForParm("VS_TIME;TEMPERATURE;CARDIOTACH;RESPIRATORY_RATE;NBPS;NBPD;SPO2;PAIN;OXY_SUPPLY_TYPE;OXY_SUPPLY_RATE;CONDITION",selectRowParm);	
	}

	/**
	 * 预览，应从数据库中查询
	 */
	public void onPrint(){
		// 体征数据
		this.onQuery();
		TParm data = new TParm();
		data.setData("TITLE", "TEXT", "急诊抢救记录");
		data.setData("PAT_NAME_TXT", "TEXT", this.getValue("PAT_NAME"));
		data.setData("SEX_CODE_TXT", "TEXT", this.getSex(getValueString("SEX_CODE")));
		data.setData("AGE_TXT", "TEXT", getValue("AGE"));
		data.setData("MR_NO_TXT", "TEXT", getValue("MR_NO"));
		data.setData("TRIAGE_NO_TXT", "TEXT", getValue("TRIAGE_NO"));
		data.setData("DISEASE_CLASS_TXT", "TEXT", getValue("DISEASE_CLASS"));// 病情分级
		data.setData("CASE_NO_TEXT", "TEXT", getValue("CASE_NO"));// 急诊就诊号
		TParm tableParm = table.getShowParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		TParm parm = new TParm();
		int count = 0;
		for(int i=0; i<table.getRowCount(); i++){
			// 有护士签名的打印
			if(tableParm.getValue("SIGN", i) != null && tableParm.getValue("SIGN", i).trim().length()>0){	
				parm.addData("VS_TIME", tableParm.getValue("VS_TIME",i).substring(0, 4)+"/"+tableParm.getValue("VS_TIME",i).substring(4, 6)+"/"+tableParm.getValue("VS_TIME",i).substring(6, 8)+" "+tableParm.getValue("VS_TIME",i).substring(8, 10)+":"+tableParm.getValue("VS_TIME",i).substring(10, 12));//时间
				parm.addData("TEMPERATURE", tableParm.getValue("TEMPERATURE",i));//体温（℃）
				parm.addData("CARDIOTACH", tableParm.getValue("CARDIOTACH",i));//心率 （次/分）
				parm.addData("RESPIRATORY_RATE", tableParm.getValue("RESPIRATORY_RATE",i));//呼吸（次/分）
				parm.addData("BP", tableParm.getValue("NBPS",i)+"/"+tableParm.getValue("NBPD",i));// 血压 （收缩压 /舒张压）			
				parm.addData("SPO2", tableParm.getValue("SPO2",i));// 氧饱和度（%）
				parm.addData("PAIN", tableParm.getValue("PAIN",i));// 疼痛（分）
				parm.addData("OXY_SUPPLY_TYPE", tableParm.getValue("OXY_SUPPLY_TYPE",i));//供氧方式
				parm.addData("OXY_SUPPLY_RATE", tableParm.getValue("OXY_SUPPLY_RATE",i));//供氧(升/分)
				parm.addData("CONDITION", tableParm.getValue("CONDITION",i));//病情摘要
				parm.addData("SIGN", tableParm.getValue("SIGN",i));//签字
				count++;
			}
		}
		parm.setCount(count);
		parm.addData("SYSTEM", "COLUMNS", "VS_TIME");
		parm.addData("SYSTEM", "COLUMNS", "TEMPERATURE");
		parm.addData("SYSTEM", "COLUMNS", "CARDIOTACH");
		parm.addData("SYSTEM", "COLUMNS", "RESPIRATORY_RATE");
		parm.addData("SYSTEM", "COLUMNS", "BP");		
		parm.addData("SYSTEM", "COLUMNS", "SPO2");
		parm.addData("SYSTEM", "COLUMNS", "PAIN");
		parm.addData("SYSTEM", "COLUMNS", "OXY_SUPPLY_TYPE");
		parm.addData("SYSTEM", "COLUMNS", "OXY_SUPPLY_RATE");
		parm.addData("SYSTEM", "COLUMNS", "CONDITION");
		parm.addData("SYSTEM", "COLUMNS", "SIGN");
		data.setData("TABLE",parm.getData());
		// 口头医嘱
		TParm orderResult = this.onQueryOrder();
		TablePublicTool.setParmValue(orderT, orderResult);
		TParm orderP = orderT.getShowParmValue();// 注意
		TParm parm2 = new TParm();
		int count2 = 0;
		for(int i=0; i<orderT.getRowCount(); i++){
			if(orderP.getValue("NOTE_DATE", i) != null && orderP.getValue("NOTE_DATE", i).trim().length()>16){
				parm2.addData("NOTE_DATE", orderP.getValue("NOTE_DATE",i).substring(0, 16));//日期时间
			}else{
				parm2.addData("NOTE_DATE", "");//日期时间
			}
			int mediQty = orderP.getInt("MEDI_QTY", i);
			if(mediQty != 0){
				parm2.addData("ORDER_DESC", orderP.getValue("ORDER_DESC",i)+" "+orderP.getValue("MEDI_QTY",i)+" "+orderP.getValue("MEDI_UNIT",i)+" "+orderP.getValue("ROUTE_CODE",i));//用药、处置（口头医嘱）
			}else{
				parm2.addData("ORDER_DESC", orderP.getValue("ORDER_DESC",i)+" "+orderP.getValue("ROUTE_CODE",i));//用药、处置（口头医嘱）);//用药、处置（口头医嘱）
			}
			parm2.addData("SIGN_DR", orderP.getValue("SIGN_DR",i));//医生签字
			parm2.addData("SIGN_NS", orderP.getValue("SIGN_NS",i));//护士签字
			count2++;
		}
		parm2.setCount(count2);
		parm2.addData("SYSTEM", "COLUMNS", "NOTE_DATE");
		parm2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm2.addData("SYSTEM", "COLUMNS", "SIGN_DR");
		parm2.addData("SYSTEM", "COLUMNS", "SIGN_NS");
		data.setData("TABLE_ORDER",parm2.getData());
		
		data.setData("PRINT_USER", Operator.getName());// 打印人员
		data.setData("PRINT_DATE", StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));// 打印日期
		data.setData("PRINT_DEPT", "TEXT", this.getDeptName(Operator.getDept()));// 打印部门
		
		
		TFrame f = (TFrame)this.openWindow("%ROOT%\\config\\reg\\REGSavePrtAndPreview.x", data, false);
		f.showMaxWindow();
	}

	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("CASE_NO;MR_NO;TRIAGE_NO;PAT_NAME;VS_TIME;TEMPERATURE;"
				+ "CARDIOTACH;RESPIRATORY_RATE;NBPS;NBPD;SPO2;PAIN;OXY_SUPPLY_TYPE;"
				+ "OXY_SUPPLY_RATE;SEX_CODE;AGE;DISEASE_CLASS;VS_TIME_S;VS_TIME_E");
		// table数据初始化
		TParm parmValue1 = new TParm();
		parmValue1.setCount(0);
		table.setParmValue(parmValue1);
		// orderT数据初始化
		TParm parmValue2 = new TParm();
		parmValue2.setCount(0);
		orderT.setParmValue(parmValue2);
	}

	/**
	 * table上的checkBox注册监听
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxChangeValue(Object obj) {
		TTable table = (TTable)obj;		
		table.acceptText();
	}

	/**
	 * Timestamp-->String
	 * @param ts
	 * @param format
	 * @return
	 */
	public String timestampToString(Timestamp ts, String format){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat(format);// yyyy/MM/dd HH:mm
		try {
			tsStr = sdf.format(ts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	/**
	 * Date-->String
	 * @author wangqing 20170627
	 * @param date
	 * @return
	 */
	public String dateToString(Date date, String format){
		if(date == null){
			return null;
		}
		//		Date date = new Date();
		String dateStr = "";
		//format的格式可以任意
		DateFormat sdf2 = new SimpleDateFormat(format);// yyyy/MM/dd HH/mm/ss
		try {
			dateStr = sdf2.format(date);
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	public String getAge(Timestamp birthDate){
		//		Timestamp birthDate = TypeTool.getTimestamp(getValue("BIRTH_DATE"));
		String age = OdoUtil.showAge( birthDate,SystemTool.getInstance().getDate());
		return age;
	}

	public String getSex(String sexCode){
		if(sexCode == null || sexCode.trim().length()<=0){
			return "";
		}
		if(sexCode.equals("0")){
			return "";
		}
		if(sexCode.equals("1")){
			return "男";
		}
		if(sexCode.equals("2")){
			return "女";
		}
		return "";
	}

	/**
	 * 获取科室描述
	 * @param deptCode
	 * @return
	 */
	public String getDeptName(String deptCode){
		String sql = " SELECT DEPT_CODE, DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0 || result.getCount()<=0){
			return "";
		}
		return result.getValue("DEPT_CHN_DESC", 0);
	}
	
	
	/**
	 * 护士签名
	 */
	public void onSign(){
		if(table == null){
			this.messageBox("table is null");
			return;
		}
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		boolean flg = false;// 标记是否有勾选行
		for(int i=0; i<tableParm.getCount(); i++){
			// 已经签名的不能签名
			if(tableParm.getValue("SIGN", i) != null 
					&& tableParm.getValue("SIGN", i).trim().length()>0 
					&& tableParm.getValue("SEL_FLG", i) != null 
					&& tableParm.getValue("SEL_FLG", i).equals("Y") ){
				this.messageBox("已经签名的行不可签名！！！");		
				return;
			}
			for(int j=0; j<tableParm.getCount(); j++){
				if(tableParm.getValue("SEL_FLG", j) != null 
						&& tableParm.getValue("SEL_FLG", j).equals("Y")){
					flg = true;					
				}
			}		
		}
		if(!flg){
			this.messageBox("请选择签名行！！！");
			return;
		}
		TParm parm = new TParm();
		Object obj = this.openDialog("%ROOT%\\config\\reg\\REGSavePassWordCheck.x", parm);
		if(obj != null && obj instanceof TParm){
			parm = (TParm) obj;
			// 取消签名
			if(parm.getValue("RESULT") != null && parm.getValue("RESULT").equals("CANCLE")){
				// 取消勾选
				for(int i=0; i<tableParm.getCount(); i++){
					if(tableParm.getValue("SEL_FLG", i) != null 
							&& tableParm.getValue("SEL_FLG", i).equals("Y")){
						TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", tableParm.getValue("SEL_FLG", i), "N");
					}			
				}
				this.messageBox("签名失败！！！");
				return;
			}
			for(int i=0; i<tableParm.getCount(); i++){ 
				if(tableParm.getValue("SEL_FLG", i) != null 
						&& tableParm.getValue("SEL_FLG", i).equals("Y")){
					TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", tableParm.getValue("SEL_FLG", i), "N");// 取消勾选 
					TablePublicTool.modifyRow(table, i, 13, "SIGN", tableParm.getValue("SIGN", i), parm.getValue("USER_ID"));// 护士签名 
				}			
			}	
			// 保存
			if(this.onSave1()){
				this.messageBox("签名成功！！！");
			}else{
				this.messageBox("签名失败！！！");
			}			
		}else{
			// 取消勾选
			for(int i=0; i<tableParm.getCount(); i++){
				if(tableParm.getValue("SEL_FLG", i) != null 
						&& tableParm.getValue("SEL_FLG", i).equals("Y")){
					TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", tableParm.getValue("SEL_FLG", i), "N");
				}			
			}	
			this.messageBox("签名失败！！！");
			return;
		}
	}

	/**
	 * 取消签名
	 */
	public void onCancelSign(){
		if(table == null){
			this.messageBox("table is null");
			return;
		}
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		boolean flg = false;// 标记是否有勾选行
		for(int i=0; i<tableParm.getCount(); i++){
			// 未签名的不能取消签名
			if( (tableParm.getValue("SIGN", i) == null 
					|| tableParm.getValue("SIGN", i).trim().length()==0) 
					&& tableParm.getValue("SEL_FLG", i) != null 
					&& tableParm.getValue("SEL_FLG", i).equals("Y") ){
				this.messageBox("未签名的不能取消签名");			
				return;
			}
			for(int j=0; j<tableParm.getCount(); j++){
				if(tableParm.getValue("SEL_FLG", j) != null 
						&& tableParm.getValue("SEL_FLG", j).equals("Y")){
					flg = true;					
				}
			}		
		}
		if(!flg){
			this.messageBox("请选择取消签名行！！！");
			return;
		}
		for(int i=0; i<tableParm.getCount(); i++){
			if(tableParm.getValue("SEL_FLG", i) != null 
					&& tableParm.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", tableParm.getValue("SEL_FLG", i), "N");// 取消勾选 
				TablePublicTool.modifyRow(table, i, 13, "SIGN", tableParm.getValue("SIGN", i), "");// 护士签名 
			}
		}		
		// 保存
		if(this.onSave2()){
			this.messageBox("取消签名成功！！！");
		}else{
			this.messageBox("取消签名失败！！！");
		}		
	}

	/**
	 * 查询口头医嘱列表
	 */
	public TParm onQueryOrder(){
		TParm result = new TParm();
		// 初始化查询参数
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("请输入检伤号！！！");
			return result;
		}
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);		
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "selectOnwOrder", parm);
		if(result.getErrCode()<0){
			System.out.println("查询口头医嘱列表err!!!");
		}
		return result;
	}

	/**
	 * 保存体征数据
	 */
	public void onSave(){
		if(table == null){
			this.messageBox("table is null");
			return;
		}	
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return;
		}
		int row= table.getSelectedRow();
		if(row<0){
			this.messageBox("row<0");
			return;
		}
		TablePublicTool.modifyRow(table, row, 3, "TEMPERATURE", 
				tableParm.getValue("TEMPERATURE", row), this.getValue("TEMPERATURE"));// 体温
		TablePublicTool.modifyRow(table, row, 4, "CARDIOTACH", 
				tableParm.getValue("CARDIOTACH", row), this.getValue("CARDIOTACH"));// 心率
		TablePublicTool.modifyRow(table, row, 5, "RESPIRATORY_RATE", 
				tableParm.getValue("RESPIRATORY_RATE", row), this.getValue("RESPIRATORY_RATE"));// 呼吸
		TablePublicTool.modifyRow(table, row, 6, "NBPS", 
				tableParm.getValue("NBPS", row), this.getValue("NBPS"));// 收缩压
		TablePublicTool.modifyRow(table, row, 7, "NBPD", 
				tableParm.getValue("NBPD", row), this.getValue("NBPD"));// 收缩压
		TablePublicTool.modifyRow(table, row, 8, "SPO2", 
				tableParm.getValue("SPO2", row), this.getValue("SPO2"));// 血氧饱和度
		TablePublicTool.modifyRow(table, row, 9, "PAIN", 
				tableParm.getValue("PAIN", row), this.getValue("PAIN"));// 疼痛评分
		TablePublicTool.modifyRow(table, row, 10, "OXY_SUPPLY_TYPE", 
				tableParm.getValue("OXY_SUPPLY_TYPE", row), this.getValue("OXY_SUPPLY_TYPE"));// 供养方式
		TablePublicTool.modifyRow(table, row, 11, "OXY_SUPPLY_RATE", 
				tableParm.getValue("OXY_SUPPLY_RATE", row), this.getValue("OXY_SUPPLY_RATE"));// 供氧量
		TablePublicTool.modifyRow(table, row, 12, "CONDITION", 
				tableParm.getValue("CONDITION", row), this.getValue("CONDITION"));// 病情摘要
		// 校验数据
		checkData(tableParm, "TRIAGE_NO;VS_TIME");
		TParm saveResult= new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSaveVSData", tableParm);
		if(saveResult==null 
				|| saveResult.getErrCode()<0){
			this.messageBox("保存失败！！！");
			return;
		}
		this.messageBox("保存成功！！！");	
		// 刷新体征数据
		onSelectVSData();		
	}

	/**
	 * 校验数据
	 */
	public void checkData(TParm parm, String names1, String names2){
		String [] nameArr1 = names1.split(";");
		String [] nameArr2 = names2.split(";");	
		for(int i=parm.getCount()-1; i>=0; i--){
			for(int j=0; j<nameArr1.length; j++){
				if(parm.getData(nameArr1[j], i)==null){
					parm.setData(nameArr1[j], i, "");
				}
			}
			for(int k=0; k<nameArr2.length; k++){
				if(parm.getData(nameArr2[k], i)==null || parm.getData(nameArr2[k], i).toString().trim().length()==0){
					parm.removeRow(i);
					break;
				}
			}	   
		}
	}

	/**
	 * 校验数据
	 * @param parm
	 * @param names2
	 */
	public void checkData(TParm parm, String names2){
		String[] names = parm.getNames(TParm.DEFAULT_GROUP);
		StringBuffer namesStr = new StringBuffer();
		for(int i=0; i<names.length; i++){
			if(namesStr.length()>0){
				namesStr.append(";");
			}
			namesStr.append(names[i]);
		}	
		String names1 = namesStr.toString();
		this.checkData(parm, names1, names2);
	}

	/**
	 * 口头医嘱
	 */
	public void onOrder(){
		// 初始化查询参数
		String triageNo = this.getValueString("TRIAGE_NO");
		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("请输入检伤号！！！");
			return;
		}
		
		// 查询
		String sql = "";
		TParm result = new TParm();
		sql = " SELECT A.TRIAGE_NO, A.CASE_NO, A.MR_NO, A.LEVEL_CODE, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE FROM ERD_EVALUTION A, SYS_PATINFO B WHERE A.MR_NO = B.MR_NO(+) AND A.TRIAGE_NO='"+triageNo+"' ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("没有此检伤号信息");
			return;
		}	
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);// 检伤号
		parm.setData("MR_NO", result.getValue("MR_NO", 0));// 病案号
		parm.setData("PAT_NAME", result.getValue("PAT_NAME", 0));// 患者姓名
		parm.setData("PAT_SEX", result.getValue("SEX_CODE", 0));// 患者性别
		parm.setData("PAT_AGE", this.getAge(result.getTimestamp("BIRTH_DATE", 0)));// 患者年龄
		this.openWindow("%ROOT%\\config\\onw\\ONWOrder.x", parm);
		//		this.openDialog("%ROOT%\\config\\onw\\ONWOrder.x", parm);
	}

	/**
	 * 护士签名保存
	 * @return
	 */
	public boolean onSave1(){ 
		if(table == null){
			this.messageBox("table is null");
			return false;
		}	
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return false;
		}
		// 校验数据
		checkData(tableParm, "TRIAGE_NO;VS_TIME;SIGN");
		TParm saveResult= new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "updateAmiErdVtsRecord1", tableParm);
		if(saveResult==null 
				|| saveResult.getErrCode()<0){
			return false;
		}	
		// 刷新体征数据
		onSelectVSData();	
		return true;
	}

	/**
	 * 护士取消签名保存
	 * @return
	 */
	public boolean onSave2(){ 
		if(table == null){
			this.messageBox("table is null");
			return false;
		}	
		TParm tableParm = table.getParmValue();
		if(tableParm == null){
			this.messageBox("tableParm is null");
			return false;
		}
		// 校验数据
		checkData(tableParm, "TRIAGE_NO;VS_TIME");
		TParm saveResult= new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "updateAmiErdVtsRecord2", tableParm);
		if(saveResult==null 
				|| saveResult.getErrCode()<0){
			return false;
		}
		// 刷新体征数据
		onSelectVSData();	
		return true;
	}







}
