package com.javahis.ui.erd;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;

/**
 * <p>Title: 急诊抢救床位绑定及解绑</p>
 *
 * <p>Description: 急诊抢救床位绑定及解绑</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author wangqing 20170626
 *
 * @version 1.0
 */
public class ERDDynamicRcdMainContol2 extends TControl {
	/**
	 * 检伤号
	 */
	private String triageNo = "";
	/**
	 * 就诊号
	 */
	private String caseNo = "";
	/**
	 * 病案号
	 */
	private String mrNo = "";

	/**
	 * 医生调用、护士调用标记：NURSE：护士 ；OPD、OPD_OUT：急诊医生
	 */
	private String flg = "";
	
	// 每次初始化都需要初始化这三个 
	// the start
	/**
	 * 是否绑定床位
	 */
	private boolean isInsert = false;
	/**
	 * 如果入床，绑定的抢救区域
	 */
	private String erdregionCode = "";
	/**
	 * 如果入床，绑定的床位
	 */
	private String bedNo = "";	
	// the end
	
	
	/**
	 * tab面板
	 */
	private TTabbedPane tabPanel;
	// 第一页签的控件
	/**
	 * 床位TTable
	 */
	private TTable table;	

	//第二个页签的控件
	/**
	 * 离院TRadioButton
	 */
	private TRadioButton Radio0;
	/**
	 * 转住院TRadioButton
	 */
	private TRadioButton Radio1;
	/**
	 * 召回TRadioButton
	 */
	private TRadioButton Radio2;
	/**
	 * 离院方式TComboBox
	 */
	private TComboBox DISCHG_TYPE;
	/**
	 * 离院日期TTextFormat
	 */
	private TTextFormat DISCHG_DATE_DAY;
	/**
	 * 转至医院TComboBox
	 */
	private TComboBox TRAN_HOSP;
	/**
	 * 住院科别TTextFormat
	 */
	private TTextFormat IPD_IN_DEPT;
	/**
	 * 转入日期TTextFormat
	 */
	private TTextFormat IPD_IN_DATE_DAY;
	/**
	 * 召回日期
	 */
	private TTextFormat RETURN_DATE;

	private TTextField DISCHG_DATE_TIME;
	private TTextField IPD_IN_DATE_TIME;


	// 患者基本信息
	/**
	 * 患者姓名TTextField
	 */
	private TTextField PAT_NAME;
	/**
	 * 性别TComboBox
	 */
	private TComboBox SEX;
	/**
	 * 年龄TTextField
	 */
	private TTextField AGE;
	/**
	 * 婚姻状况TComboBox
	 */
	private TComboBox MARRIGE;
	/**
	 * 职业TTextFormat
	 */
	private TTextFormat OCCUPATION;
	/**
	 * 民族TTextFormat
	 */
	private TTextFormat FOLK;
	/**
	 * 国籍TTextFormat
	 */
	private TTextFormat NATION;
	/**
	 * 生日TTextFormat
	 */
	private TTextFormat BIRTH_DATE;
	/**
	 * 出生省市TTextField（隐藏）
	 */
	private TTextField RESID_PROVICE;
	/**
	 * 出生省市描述
	 */
	private TTextField RESID_PROVICE_DESC;
	/**
	 * 出生县市TTextFormat
	 */
	private TTextFormat RESID_COUNTRY;	
	/**
	 * 身份证号TTextField
	 */
	private TTextField IDNO;
	/**
	 * 身份类别TComboBox
	 */
	private TComboBox CTZ1_CODE;
	/**
	 * 联系人TTextField
	 */
	private TTextField CONTACTER;
	/**
	 * 关系TTextFormat
	 */
	private TTextFormat RELATIONSHIP;
	/**
	 * 联系人电话TTextField
	 */
	private TTextField CONT_TEL;
	/**
	 * 联系人地址TTextField
	 */
	private TTextField CONT_ADDRESS;
	/**
	 * 工作单位TTextField
	 */
	private TTextField OFFICE;
	/**
	 * 单位电话TTextField
	 */
	private TTextField O_TEL;
	/**
	 * 单位邮编TTextField
	 */
	private TTextField O_POSTNO;
	/**
	 * 单位地址TTextField
	 */
	private TTextField O_ADDRESS;
	/**
	 * 户口住址TTextField
	 */
	private TTextField H_ADDRESS;
	/**
	 * 户口邮编TTextField
	 */
	private TTextField H_POSTNO;


	/**
	 * 入抢救日TTextFormat
	 */
	private TTextFormat IN_DATE;
	/**
	 * 出抢救日TTextFormat
	 */
	private TTextFormat OUT_DATE;
	/**
	 * 入抢救区TTextFormat
	 */
	private TTextFormat ERD_REGION;
	/**
	 * 出抢救区TTextFormat
	 */
	private TTextFormat OUT_ERD_REGION;
	/**
	 * 入抢救科室TTextFormat
	 */
	private TTextFormat IN_DEPT;
	/**
	 * 出抢救科室TTextFormat
	 */
	private TTextFormat OUT_DEPT;
	/**
	 * 抢救主诊断code TTextField，隐藏控件保存ICD_CODE使用
	 */
	private TTextField HIDE_CODE;
	/**
	 * 抢救主诊断desc TTextField
	 */
	private TTextField OUT_DIAG_CODE;
	/**
	 * 诊断备注TTextField
	 */
	private TTextField CODE_REMARK;
	/**
	 * 诊断转归TComboBox
	 */
	private TComboBox CODE_STATUS;
	/**
	 * 愈合等级TTextFormat
	 */
	private TTextFormat HEAL_LV;
	/**
	 * 主手术ICD TTextFormat
	 */
	private TTextFormat OP_CODE;
	/**
	 * 手术日期TTextFormat
	 */
	private TTextFormat OP_DATE;
	/**
	 * 手术人员TTextFormat
	 */
	private TTextFormat MAIN_SUGEON;
	/**
	 * 主手术等级TComboBox
	 */
	private TComboBox OP_LEVEL;
	/**
	 * 抢救次数TNumberTextField
	 */
	private TNumberTextField GET_TIMES;
	/**
	 * 成功次数TNumberTextField
	 */
	private TNumberTextField SUCCESS_TIMES;
	/**
	 * 经治医师TTextFormat
	 */
	private TTextFormat DR_CODE;
	/**
	 * 实留天数TNumberTextField
	 */
	private TNumberTextField REAL_STAY_DAYS;
	/**
	 * 随诊周数TNumberTextField
	 */
	private TNumberTextField ACCOMPANY_WEEK;
	/**
	 * 随诊月数TNumberTextField
	 */
	private TNumberTextField ACCOMPANY_MONTH;
	/**
	 * 随诊年数TNumberTextField
	 */
	private TNumberTextField ACCOMPANY_YEAR;
	/**
	 * 随诊日期
	 */	
	private TTextFormat ACCOMP_DATE;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		// 获取系统参数
		Object o = this.getParameter();
		if(o!=null && o instanceof TParm){
			TParm sysParm = (TParm) o;
			triageNo = sysParm.getValue("TRIAGE_NO");
			flg = sysParm.getValue("FLG");
			// 医生打开时需要传入就诊号和病案号
			caseNo = sysParm.getValue("CASE_NO");
			mrNo = sysParm.getValue("MR_NO");
			// 校验检伤号和FLG
			if(triageNo==null || triageNo.trim().length()<=0 || flg==null || flg.trim().length()<=0){
				this.messageBox("系统参数错误2");
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						closeWindow();
					}
				});
				return;
			}
			// 如果时医生调用，校验就诊号和病案号
			if((flg.equals("OPD") || flg.equals("OPD_OUT")) 
					&& (caseNo==null || caseNo.trim().length()<=0 || mrNo==null || mrNo.trim().length()<=0)){
				this.messageBox("系统参数错误3");
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						closeWindow();
					}
				});
				return;
			}	
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});
			this.messageBox("系统参数错误1");
			return;
		}		
		// tab面板
		tabPanel = (TTabbedPane) this.getComponent("Tab");
		// 根据系统参数决定打开哪个页签、初始化哪个页签
		if(flg.equals("OPD") || flg.equals("OPD_OUT")){// 急诊医生调用->急诊留观 或 ->离院
			tabPanel.setSelectedIndex(1);
//			tabPanel.setEnabledAt(0, false);// 急诊医生屏蔽床位设定功能
			
			// 页签1初始化操作
			this.myInitControler();
			// 设置界面数据
			TParm erdRecordParm = getErdRecord(mrNo, caseNo, triageNo);
			if(erdRecordParm.getErrCode()<0){	
				this.messageBox(erdRecordParm.getErrText());
				return;
			}
			setErdRecord(erdRecordParm);
		}else if(flg.equals("NURSE")){// 急诊护士调用
			tabPanel.setSelectedIndex(0);
			tabPanel.setEnabledAt(1, false);// 急诊护士屏蔽床位解绑功能
			
			// 页签0初始化操作
			table = (TTable) this.getComponent("TABLE");
			// 给table注册CHECK_BOX_CLICKED点击监听事件
			this.callFunction("UI|TABLE|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxChangeValue");
			// 查询床位数据
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					onQuery();
				}
			});			
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});
			this.messageBox("FLG传入错误");
			return;
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if(table==null){
			table = (TTable) this.getComponent("TABLE");
		}
		table.setParmValue(null);// 清空table数据
		// 获取查询条件
		TParm selParm = this.getQueryParm();
		// 执行查询
		TParm query = this.query(selParm);
		if(query.getErrCode()<0){
			this.messageBox(query.getErrText());
			return;
		}
		if (query.getCount() <= 0) {
			this.messageBox("没有抢救床数据！");
			return;
		}
		table.setParmValue(query);
	}

	/**
	 * 得到查询参数
	 * 
	 * @return TParm
	 */
	public TParm getQueryParm() {
		TParm parm = new TParm();
		// 抢救区
		String erdRegion = this.getValueString("ERD_REGION_CODE");		
		if(erdRegion!=null && erdRegion.trim().length()>0){
			parm.setData("ERD_REGION_CODE", erdRegion);
		}
		// 占床注记
		if (this.getValueBoolean("OCCUPY_FLG")) {
			parm.setData("OCCUPY_FLG", "Y");
		}
		return parm;
	}

	/**
	 * 查询抢救床
	 * @author wangqing 20170626
	 * @param parm
	 * @return
	 */
	public TParm query(TParm parm){
		StringBuffer buffer = new StringBuffer();
		// OCCUPY_FLG_2用来监视OCCUPY_FLG是否变化	
		String sql = " SELECT A.OCCUPY_FLG, A.OCCUPY_FLG AS OCCUPY_FLG_2, A.ERD_REGION_CODE, A.BED_NO, A.BED_DESC, A.TRIAGE_NO, "
				+ "B.CASE_NO, B.MR_NO, B.PAT_NAME "
				+ "FROM ERD_BED A, "
				+ "(SELECT A.TRIAGE_NO, A.CASE_NO, B.MR_NO, B.PAT_NAME "
				+ "FROM ERD_EVALUTION A, "
				+ "(SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME "
				+ "FROM REG_PATADM A, SYS_PATINFO B "
				+ "WHERE A.MR_NO=B.MR_NO(+) AND A.MR_NO IS NOT NULL)B "
				+ "WHERE A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO IS NOT NULL)B "
				+ "WHERE A.TRIAGE_NO=B.TRIAGE_NO(+) ";
		String sql2 = "";
		if(parm.getValue("ERD_REGION_CODE") != null && parm.getValue("ERD_REGION_CODE").trim().length()>0){
			sql2 = "AND A.ERD_REGION_CODE = '"+parm.getValue("ERD_REGION_CODE")+"' ";
		}
		String sql3 = "";
		if(parm.getValue("OCCUPY_FLG") != null && parm.getValue("OCCUPY_FLG").trim().length()>0){
			sql3 = "AND NOT(A.OCCUPY_FLG IS NOT NULL AND A.OCCUPY_FLG='Y') ";
		}
		String sql4 = "ORDER BY ERD_REGION_CODE, BED_NO ";
		buffer.append(sql);
		buffer.append(sql2);
		buffer.append(sql3);
		buffer.append(sql4);
		TParm result = new TParm(TJDODBTool.getInstance().select(buffer.toString()));    
		return result;
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
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();	
		TParm tblParm = table.getParmValue();	
		String sql;
		TParm result;	
		if (col == 0) {
			boolean flg = tblParm.getBoolean("OCCUPY_FLG", row);
			boolean flg2 = tblParm.getBoolean("OCCUPY_FLG_2", row);
			if(flg==flg2){// 点击checkBox没有发生变化
				return;
			}
			if (flg) {// 如果勾选
				// 校验病患是否占床，且已经保存
				result = getErdInfo(triageNo);
				if(result.getErrCode()<0){
					this.messageBox("bug:::查询病患占床数据出错");					
					return;
				}
				if(result.getCount()>0){// 占床且已经保存
					this.messageBox("该病人已有床位");
					table.setValueAt("N", row, col);
					tblParm.setData("OCCUPY_FLG", row, "N");
					return;
				}				
				// 校验病患是否转出
				result = this.getErdOutData(triageNo);
				if(result.getErrCode()<0){
					this.messageBox("bug:::查询病患转出数据出错");
					return;
				}
				if(result.getCount()>0){
					this.messageBox("该病人已转出");
					table.setValueAt("N", row, col);
					tblParm.setData("OCCUPY_FLG", row, "N");
					return;
				}				
				// 校验患者是否已经占床，但尚未保存
				for(int i=0; i<tblParm.getCount(); i++){
					if(tblParm.getValue("TRIAGE_NO", i)!=null && tblParm.getValue("TRIAGE_NO", i).equals(triageNo)){
						this.messageBox("该病人已有床位\n不可重复设置！");
						table.setValueAt("N", row, col);
						tblParm.setData("OCCUPY_FLG", row, "N");
						return;
					}
				}
				// 绑定床位，更新数据
				isInsert = true;
				erdregionCode = tblParm.getValue("ERD_REGION_CODE", row);
				bedNo = tblParm.getValue("BED_NO", row);	
				// 更新OCCUPY_FLG_2
				tblParm.setData("OCCUPY_FLG_2", row, flg);

				// 更新检伤号
				table.setValueAt(triageNo, row, 5);
				tblParm.setData("TRIAGE_NO", row, triageNo);
				// 根据检伤号查询就诊号、病案号、患者姓名
				sql = "SELECT A.TRIAGE_NO, A.CASE_NO, B.MR_NO, B.PAT_NAME FROM ERD_EVALUTION A, (SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME FROM REG_PATADM A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO(+) AND A.MR_NO IS NOT NULL)B WHERE A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO IS NOT NULL AND A.TRIAGE_NO='"+triageNo+"' ";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getErrCode()<0){
					this.messageBox("bug:::查询患者数据错误");
					return;
				}
				// 更新病案号、就诊号、患者姓名
				if(result.getCount()>0){
					// 病案号
					table.setValueAt(result.getData("MR_NO", 0), row, 4);
					tblParm.setData("MR_NO", row, result.getData("MR_NO", 0));
					// 就诊号
					tblParm.setData("CASE_NO", row, result.getData("CASE_NO", 0));
					// 患者姓名
					table.setValueAt(result.getData("PAT_NAME", 0), row, 6);
					tblParm.setData("PAT_NAME", row, result.getData("PAT_NAME", 0));
				}	
			} else {// 取消勾选
				// 该病床已经有病患，且已经保存
				sql = "SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO FROM ERD_BED WHERE OCCUPY_FLG='Y' AND TRIAGE_NO IS NOT NULL AND BED_NO='"+tblParm.getValue("BED_NO", row)+"' ";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getErrCode()<0){
					this.messageBox("bug:::查询病床数据错误");
					return;
				}
				if(result.getCount()>0){
					this.messageBox("该病床已经有病患,不可操作");
					table.setValueAt("Y", row, col);
					tblParm.setData("OCCUPY_FLG", row, "Y");
					return;
				}
				// 更新数据
				isInsert = false;
				erdregionCode = "";
				bedNo = "";
				// 更新OCCUPY_FLG_2
				tblParm.setData("OCCUPY_FLG_2", row, flg);
				// 更新检伤号
				table.setValueAt("", row, 5);
				tblParm.setData("TRIAGE_NO", row, "");
				// 病案号
				table.setValueAt("", row, 4);
				tblParm.setData("MR_NO", row, "");
				// 就诊号
				tblParm.setData("CASE_NO", row, "");
				// 患者姓名
				table.setValueAt("", row, 6);
				tblParm.setData("PAT_NAME", row, "");
			}
		}
	}

	/**
	 * 保存
	 */
	public void onSave() {
		switch (tabPanel.getSelectedIndex()) {
		case 0:// 第一个页签
			if(isInsert){
				TParm parm = new TParm();
				parm.setData("TRIAGE_NO", triageNo);// 检伤号
				parm.setData("ERD_REGION_CODE", erdregionCode);// 抢救区
				parm.setData("BED_NO", bedNo);// 抢救床
				
				// add by wangqing 20180201 start 
				// 入床保存时，如果这时患者已经挂号，则在更新ERD_BED的时候也得更新CASE_NO和MR_NO，同时向ERD_RECORD表中插入一笔数据；如果患者没有挂号，维持原逻辑
				parm.setData("CASE_NO", "");
				parm.setData("MR_NO", "");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
				TParm regP = getPatRegInfoByTriageNo(triageNo); 
				if(regP.getErrCode()<0){
					this.messageBox(regP.getErrText());
					return;
				}
				if(regP.getCount()>0){// 说明患者已挂号
					parm.setData("CASE_NO", regP.getValue("CASE_NO", 0));
					parm.setData("MR_NO", regP.getValue("MR_NO", 0));
					// 收集待插入erd_record的数据					
					long currentTime = System.currentTimeMillis() ;
					Date date=new Date(currentTime);
					Timestamp inDate = new Timestamp(date.getTime());
					TParm erdP = this.copyPatDate(regP.getValue("MR_NO", 0), regP.getValue("CASE_NO", 0), inDate, erdregionCode, bedNo);
					if(erdP.getErrCode()<0){
						this.messageBox(erdP.getErrText());
						return;
					}
					parm.setData("erdP", erdP.getData());	
				}
				// add by wangqing 20180201 end
				
				TParm result =
						TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "setBedFinal", parm);
				if(result.getErrCode()<0){
					this.messageBox(result.getErrText());
					return;
				}
				this.messageBox("绑定床位成功");
			}	
			break;
		case 1:// 第二个页签
			TParm parm1 = getPage1Data();
			TParm parm = new TParm();
			if(Radio0.isSelected() || Radio1.isSelected()){// 转出或转住院
				parm = getErdInfo(triageNo);
				if(parm.getErrCode()<0){
					this.messageBox(parm.getErrText());
					return;
				}
				if(parm.getCount()<=0){
					parm = getErdOutData(triageNo);
					if(parm.getErrCode()<0){
						this.messageBox(parm.getErrText());
						return;
					}
					if(parm.getCount()>0){// 已转出
						this.messageBox("此病人已经转出");
						return;
					}else{// 未绑定床位
						this.messageBox("此病人未绑定床位");
						return;
					}
				}	
				if(this.getValueString("OUT_DATE").trim().length()<=0){
					this.messageBox("请输入出抢救日期");
					return;
				}
				if(Radio0.isSelected()){
					
					if(this.getValueString("DISCHG_TYPE").trim().length()<=0){
						this.messageBox("请输入离院方式");
						return;
					}
					if(this.getValueString("DISCHG_DATE_DAY").trim().length()<=0){
						this.messageBox("请输入离院日期");
						return;
					}
				}else if(Radio1.isSelected()){
					if(this.getValueString("IPD_IN_DEPT").trim().length()<=0){
						this.messageBox("请输入住院科别");
						return;
					}
					if(this.getValueString("IPD_IN_DATE_DAY").trim().length()<=0){
						this.messageBox("请输入转入日期");
						return;
					}	
				}
					
				parm1.setData("OUT_FLG", "Y");// 转出或转住院标记
				parm1.setData("ERD_REGION_CODE", parm.getValue("ERD_REGION_CODE", 0));// 抢救区
				parm1.setData("BED_NO", parm.getValue("BED_NO", 0));// 抢救床		
				parm1.setData("TRIAGE_NO", parm.getValue("TRIAGE_NO", 0));// 检伤号	
			}else if(Radio2.isSelected()){// 召回
				parm = getErdOutData(triageNo);
				if(parm.getErrCode()<0){
					this.messageBox(parm.getErrText());
					return;
				}
				if(parm.getCount()<=0){
					parm = getErdInfo(triageNo);
					if(parm.getErrCode()<0){
						this.messageBox(parm.getErrText());
						return;
					}
					if(parm.getCount()>0){
						this.messageBox("此病患正在抢救中");
						return;
					}else{
						this.messageBox("此病人未绑定床位");
						return;
					}
				}
				if(this.getValueString("RETURN_DATE").trim().length()<=0){
					this.messageBox("请输入召回日期");
					return;
				}
				Object obj = openDialog("%ROOT%\\config\\erd\\ERDBedSelUI2.x");
				if (obj == null){
					return;
				}else {
					parm1.setData("RETURN_FLG", "Y");// 召回标记
					parm1.setData("ERD_REGION_CODE", ((TParm) obj).getValue("ERD_REGION_CODE"));// 召回抢救区
					parm1.setData("BED_NO", ((TParm) obj).getValue("BED_NO"));// 召回床位
					parm1.setData("TRIAGE_NO", triageNo);
					parm1.setData("OUT_DATE", new TNull(Timestamp.class));// 转出日期置空
					parm1.setData("CASE_NO", caseNo);// 就诊号
					parm1.setData("MR_NO", mrNo);// 病案号
					
					// 召回时，将离院方式、离院时间、转至医院、住院科别、住院日期、出抢救日、出抢救区、出抢救科室置空
					parm1.setData("DISCHG_TYPE", new TNull(String.class));// 离院方式
					parm1.setData("DISCHG_DATE", new TNull(Timestamp.class));// 离院时间
					parm1.setData("TRAN_HOSP", new TNull(String.class));// 转至医院
					parm1.setData("IPD_IN_DEPT", new TNull(String.class));// 住院科别					
					parm1.setData("IPD_IN_DATE", new TNull(Timestamp.class));// 住院日期	
					parm1.setData("OUT_DATE", new TNull(Timestamp.class));// 出抢救日
					parm1.setData("OUT_ERD_REGION", new TNull(String.class));// 出抢救区
					parm1.setData("OUT_DEPT", new TNull(String.class));// 出抢救科室	
				}		
			}	
			// 处理时间格式，解决sql不统一的问题
			parm1.setData("OUT_DATE_2", StringTool.getString(TypeTool.getTimestamp(getValue("OUT_DATE")), "yyyy/MM/dd HH:mm:ss"));	
			parm1.setData("RETURN_DATE_2", StringTool.getString(TypeTool.getTimestamp(getValue("RETURN_DATE")), "yyyy/MM/dd HH:mm:ss"));	
			
			parm1 = TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "cancelBedFinal", parm1);
			if (parm1.getErrCode() < 0) {
				this.messageBox(parm1.getErrText());
				return;
			}
			this.messageBox("保存成功");		
			break;
		default:
			break;
		}
	}

	/**
	 * 转床动作
	 */
	public void onTransfer() {
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择一个空床");
			return;
		}
		// 判断是否是空床
		TParm tblParm = table.getParmValue();	
		boolean isOccupyFlg = tblParm.getBoolean("OCCUPY_FLG", row);
		if(isOccupyFlg){
			this.messageBox("请选择一个空床");
			return;
		}
		String sql;
		TParm result;		
		// 判断是否已经绑定床位，且已经保存
		sql = " SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO "
				+ "FROM ERD_BED "
				+ "WHERE OCCUPY_FLG='Y' "
				+ "AND TRIAGE_NO='"+triageNo+"' ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
			return;
		}
		if(result.getCount()>0){// 已经绑定床位，且已经保存
			TParm parm = new TParm();
			parm.setData("TRIAGE_NO", triageNo);
			parm.setData("ERD_REGION_CODE_FROM", result.getData("ERD_REGION_CODE", 0));// 原抢救区
			parm.setData("BED_NO_FROM", result.getData("BED_NO", 0));// 原床位
			parm.setData("ERD_REGION_CODE_TO", tblParm.getData("ERD_REGION_CODE", row));// 目地抢救区
			parm.setData("BED_NO_TO", tblParm.getData("BED_NO", row));// 目地床位
			
			// add by wangqing 20180201 start 
			// 转床时，更新原ERD_BED的CASE_NO和MR_NO为null;若患者已经挂号，则更新新床位ERD_BED的CASE_NO和MR_NO；若ERD_RECORD没有数据，则插入
			parm.setData("CASE_NO", "");
			parm.setData("MR_NO", "");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			TParm regP = getPatRegInfoByTriageNo(triageNo); 
			if(regP.getErrCode()<0){
				this.messageBox(regP.getErrText());
				return;
			}		
			if(regP.getCount()>0){// 说明患者已挂号
				parm.setData("CASE_NO", regP.getValue("CASE_NO", 0));
				parm.setData("MR_NO", regP.getValue("MR_NO", 0));
				TParm erdRecord = getErdRecord(regP.getValue("CASE_NO", 0));
				if(erdRecord.getErrCode()<0){
					this.messageBox(erdRecord.getErrText());
					return;
				}
				if(erdRecord.getCount()<=0){
					// 收集待插入erd_record的数据
					TParm erdP = this.copyPatDate(regP.getValue("MR_NO", 0), regP.getValue("CASE_NO", 0), triageNo);
					if(erdP.getErrCode()<0){
						this.messageBox(erdP.getErrText());
						return;
					}
					parm.setData("erdP", erdP.getData());
				}				
			}
			// add by wangqing 20180201 end
			
			result = TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "onTransferFinal", parm);
			if(result.getErrCode()<0){
				this.messageBox("转床失败");
				return;
			}
			this.messageBox("转床成功");	
			// 转床成功后重新查询，刷新数据
			this.onQuery();
		}else{
			sql = " SELECT TRIAGE_NO, CASE_NO, ERD_REGION_CODE, BED_NO, OUT_DATE "
					+ "FROM ERD_EVALUTION "
					+ "WHERE TRIAGE_NO='"+triageNo+"' "
					+ "AND CASE_NO IS NOT NULL "
					+ "AND ERD_REGION_CODE IS NOT NULL "
					+ "AND BED_NO IS NOT NULL "
					+ "AND OUT_DATE IS NOT NULL ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getErrCode()<0){
				this.messageBox(result.getErrText());
				return;
			}
			if(result.getCount()>0){// 此病患已经转出
				this.messageBox("此病患已转出，若需再抢救，医生可以执行召回操作");
				return;
			}else{// 此病患尚未绑定床位
				this.messageBox("此病患尚未绑定床位，不能转床");
			}	
		}		
	}

	/**
	 * 清空
	 */
	public void onClear() {
		switch (tabPanel.getSelectedIndex()) {
		case 0:// 第一个页签
			this.clearValue("ERD_REGION_CODE;OCCUPY_FLG");
			table.removeRowAll();		
			isInsert = false;
			erdregionCode = "";
			bedNo = "";		
			break;
		case 1:// 第二个页签
			break;
		default:
			break;
		}
	}

	/**
	 * 查询空床
	 */
	public void onEmptyBed() {
		isInsert = false;
		erdregionCode = "";
		bedNo = "";
		onQuery();
	}

	/**
	 * 切换Tab页的时候动作，目前只有医生有权限
	 */
	public void onChangeTab() {
		switch (tabPanel.getSelectedIndex()) {
		case 0:// 第一个页签
			// 页签0初始化操作
			table = (TTable) this.getComponent("TABLE");
			// 给table注册CHECK_BOX_CLICKED点击监听事件
			this.callFunction("UI|TABLE|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxChangeValue");
			// note：erd_record有效字段：就诊号、病案号、转出方式（出院|转住院|召回）、出院日期（若出院）、转至医院、住院科室、住院日期、召回日期
			// 查询床位数据
			onQuery();
			// 屏蔽保存按钮和转床按钮
			callFunction("UI|save|setVisible", false);
			callFunction("UI|TRANSFER|setVisible", false);
			break;
		case 1:// 第二个页签
			// 屏蔽保存按钮和转床按钮
			callFunction("UI|save|setVisible", true);
			callFunction("UI|TRANSFER|setVisible", false);
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化页签1控件，并绑定监听
	 */
	public void myInitControler() {
		Radio0 = (TRadioButton) this.getComponent("Radio0");
		// 出院时，默认选择离院
		Radio0.setSelected(true);
		onOutInReturn("Out");
		
		Radio1 = (TRadioButton) this.getComponent("Radio1");
		Radio2 = (TRadioButton) this.getComponent("Radio2");
		DISCHG_TYPE = (TComboBox) this.getComponent("DISCHG_TYPE");
		DISCHG_DATE_DAY = (TTextFormat) this.getComponent("DISCHG_DATE_DAY");
		TRAN_HOSP = (TComboBox) this.getComponent("TRAN_HOSP");
		IPD_IN_DEPT = (TTextFormat) this.getComponent("IPD_IN_DEPT");
		IPD_IN_DATE_DAY = (TTextFormat) this.getComponent("IPD_IN_DATE_DAY");
		RETURN_DATE = (TTextFormat) this.getComponent("RETURN_DATE");

		DISCHG_DATE_TIME = (TTextField) this.getComponent("DISCHG_DATE_TIME");
		IPD_IN_DATE_TIME = (TTextField) this.getComponent("IPD_IN_DATE_TIME");

		PAT_NAME = (TTextField) this.getComponent("PAT_NAME");
		SEX = (TComboBox) this.getComponent("SEX");
		AGE = (TTextField) this.getComponent("AGE");
		MARRIGE = (TComboBox) this.getComponent("MARRIGE");
		OCCUPATION = (TTextFormat) this.getComponent("OCCUPATION");
		FOLK = (TTextFormat) this.getComponent("FOLK");
		NATION = (TTextFormat) this.getComponent("NATION");
		BIRTH_DATE = (TTextFormat) this.getComponent("BIRTH_DATE");
		RESID_PROVICE = (TTextField) this.getComponent("RESID_PROVICE");
		RESID_PROVICE_DESC = (TTextField) this.getComponent("RESID_PROVICE_DESC");
		RESID_COUNTRY = (TTextFormat) this.getComponent("RESID_COUNTRY");
		IDNO = (TTextField) this.getComponent("IDNO");
		CTZ1_CODE = (TComboBox) this.getComponent("CTZ1_CODE");

		CONTACTER = (TTextField) this.getComponent("CONTACTER");
		RELATIONSHIP = (TTextFormat) this.getComponent("RELATIONSHIP");
		CONT_TEL = (TTextField) this.getComponent("CONT_TEL");
		CONT_ADDRESS = (TTextField) this.getComponent("CONT_ADDRESS");		
		OFFICE = (TTextField) this.getComponent("OFFICE");
		O_TEL = (TTextField) this.getComponent("O_TEL");
		O_POSTNO = (TTextField) this.getComponent("O_POSTNO");
		O_ADDRESS = (TTextField) this.getComponent("O_ADDRESS");
		H_ADDRESS = (TTextField) this.getComponent("H_ADDRESS");
		H_POSTNO = (TTextField) this.getComponent("H_POSTNO");

		IN_DATE = (TTextFormat) this.getComponent("IN_DATE");
		ERD_REGION = (TTextFormat) this.getComponent("ERD_REGION");
		IN_DEPT = (TTextFormat) this.getComponent("IN_DEPT");
		OUT_DATE = (TTextFormat) this.getComponent("OUT_DATE");
		OUT_ERD_REGION = (TTextFormat) this.getComponent("OUT_ERD_REGION");		
		OUT_DEPT = (TTextFormat) this.getComponent("OUT_DEPT");

		OUT_DIAG_CODE = (TTextField) this.getComponent("OUT_DIAG_CODE");
		HIDE_CODE = (TTextField) this.getComponent("HIDE_CODE");
		CODE_REMARK = (TTextField) this.getComponent("CODE_REMARK");
		CODE_STATUS = (TComboBox) this.getComponent("CODE_STATUS");
		HEAL_LV = (TTextFormat) this.getComponent("HEAL_LV");

		OP_CODE = (TTextFormat) this.getComponent("OP_CODE");
		OP_DATE = (TTextFormat) this.getComponent("OP_DATE");
		MAIN_SUGEON = (TTextFormat) this.getComponent("MAIN_SUGEON");
		OP_LEVEL = (TComboBox) this.getComponent("OP_LEVEL");

		GET_TIMES = (TNumberTextField) this.getComponent("GET_TIMES");
		SUCCESS_TIMES = (TNumberTextField) this.getComponent("SUCCESS_TIMES");
		DR_CODE = (TTextFormat) this.getComponent("DR_CODE");
		REAL_STAY_DAYS = (TNumberTextField) this.getComponent("REAL_STAY_DAYS");

		ACCOMPANY_WEEK = (TNumberTextField) this.getComponent("ACCOMPANY_WEEK");
		ACCOMPANY_MONTH = (TNumberTextField) this.getComponent("ACCOMPANY_MONTH");
		ACCOMPANY_YEAR = (TNumberTextField) this.getComponent("ACCOMPANY_YEAR");
		ACCOMP_DATE = (TTextFormat) this.getComponent("ACCOMP_DATE");

		// 抢救主诊断设置弹出菜单
		OUT_DIAG_CODE.setPopupMenuParameter("", getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 抢救主诊断定义接受返回值方法
		OUT_DIAG_CODE.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// 调用出生地弹出框
		callFunction("UI|RESID_PROVICE_DESC|setPopupMenuParameter", "aaa", "%ROOT%\\config\\sys\\SYSHOMEPLACEPopup.x");
		// textfield接受回传值
		callFunction("UI|RESID_PROVICE_DESC|addEventListener", TPopupMenuEvent.RETURN_VALUE, this, "popReturn1");
	}

	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icd_desc = parm.getValue("ICD_CHN_DESC");
		String icd_code = parm.getValue("ICD_CODE");
		OUT_DIAG_CODE.setValue("");
		HIDE_CODE.setValue("");
		if (!StringUtil.isNullString(icd_code)) {
			OUT_DIAG_CODE.setValue(icd_desc);
			HIDE_CODE.setValue(icd_code);		
		}
	}

	public void popReturn1(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("RESID_PROVICE", parm.getValue("HOMEPLACE_CODE"));
		this.setValue("RESID_PROVICE_DESC", parm.getValue("HOMEPLACE_DESC"));
	}

	/**
	 * 界面2赋值
	 */
	public void setErdRecord(TParm pat){
		// STATUS: 0、抢救中；1、出院；2、转住院；3、召回
		if(pat.getValue("STATUS", 0)!=null){
			if(pat.getValue("STATUS", 0).equals("0")){

			}else if(pat.getValue("STATUS", 0).equals("1")){
				onOutInReturn("Out");
				Radio0.setSelected(true);
				DISCHG_TYPE.setValue((String) pat.getData("DISCHG_TYPE", 0));
				DISCHG_DATE_DAY.setValue((Timestamp) pat.getData("DISCHG_DATE", 0));
				TRAN_HOSP.setValue((String) pat.getData("TRAN_HOSP", 0));
				DISCHG_DATE_TIME.setValue((String) pat.getData("DISCHG_DATE_TIME", 0));
			}else if(pat.getValue("STATUS", 0).equals("2")){
				onOutInReturn("In");
				Radio1.setSelected(true);
				IPD_IN_DEPT.setValue((String) pat.getData("IPD_IN_DEPT", 0));
				IPD_IN_DATE_DAY.setValue((Timestamp) pat.getData("IPD_IN_DATE", 0));
				IPD_IN_DATE_TIME.setValue((String) pat.getData("IPD_IN_DATE_TIME", 0));
			}else if(pat.getValue("STATUS", 0).equals("3")){
				onOutInReturn("Return");
				Radio2.setSelected(true);
				RETURN_DATE.setValue((Timestamp) pat.getData("RETURN_DATE", 0));
			}	
		}else{
			return;
		}			
		PAT_NAME.setValue((String) pat.getData("PAT_NAME", 0));// 患者姓名
		SEX.setValue((String) pat.getData("SEX", 0));// 性别
		AGE.setValue((String) pat.getData("AGE", 0));// 年龄
		MARRIGE.setValue((String) pat.getData("MARRIGE", 0));// 婚姻状况
		OCCUPATION.setValue((String) pat.getData("OCCUPATION", 0));// 职业
		FOLK.setValue((String) pat.getData("FOLK", 0));// 民族
		NATION.setValue((String) pat.getData("NATION", 0));// 国籍
		BIRTH_DATE.setValue((Timestamp) pat.getData("BIRTH_DATE", 0));// 生日
		
		RESID_PROVICE.setValue((String) pat.getData("RESID_PROVICE", 0));// 省市（隐藏）
		RESID_PROVICE_DESC.setValue(getPatHome(getValueString("RESID_PROVICE")).getValue("HOMEPLACE_DESC", 0));// 省市描述
				
		RESID_COUNTRY.setValue((String) pat.getData("RESID_COUNTRY", 0));// 县市		
		IDNO.setValue((String) pat.getData("IDNO", 0));// 身份证号
		CTZ1_CODE.setValue((String) pat.getData("CTZ1_CODE", 0));// 身份类别
		CONTACTER.setValue((String) pat.getData("CONTACTER", 0));// 联系人
		RELATIONSHIP.setValue((String) pat.getData("RELATIONSHIP", 0));// 联系人关系
		CONT_TEL.setValue((String) pat.getData("CONT_TEL", 0));// 联系人电话
		CONT_ADDRESS.setValue((String) pat.getData("CONT_ADDRESS", 0));// 联系人地址
		OFFICE.setValue((String) pat.getData("OFFICE", 0));// 工作单位	
		O_TEL.setValue((String) pat.getData("O_TEL", 0));// 单位电话
		O_POSTNO.setValue((String) pat.getData("O_POSTNO", 0));// 单位邮编
		O_ADDRESS.setValue((String) pat.getData("O_ADDRESS", 0));// 单位地址
		H_ADDRESS.setValue((String) pat.getData("H_ADDRESS", 0));// 家庭住址
		H_POSTNO.setValue((String) pat.getData("H_POSTNO", 0));// 家庭邮编
		IN_DATE.setValue((Timestamp) pat.getData("IN_DATE", 0));// 入抢救日
		ERD_REGION.setValue((String) pat.getData("ERD_REGION", 0));// 入抢救区
		IN_DEPT.setValue((String) pat.getData("IN_DEPT", 0));// 入抢救科室
		OUT_DATE.setValue((Timestamp) pat.getData("OUT_DATE", 0));// 出抢救日
		OUT_ERD_REGION.setValue((String) pat.getData("OUT_ERD_REGION", 0));// 出抢救区		
		OUT_DEPT.setValue((String) pat.getData("OUT_DEPT", 0));// 出抢救科室
		HIDE_CODE.setValue((String) pat.getData("OUT_DIAG_CODE", 0));// 抢救主诊断code
		if ( pat.getValue("OUT_DIAG_CODE", 0)!=null && pat.getValue("OUT_DIAG_CODE", 0).trim().length()>0) {
			String sql =
					" SELECT ICD_CHN_DESC FROM  SYS_DIAGNOSIS WHERE ICD_CODE='"
							+ pat.getValue("OUT_DIAG_CODE", 0) + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			OUT_DIAG_CODE.setValue(parm.getValue("ICD_CHN_DESC", 0));// 抢救主诊断描述
		} else {
			OUT_DIAG_CODE.setValue("");// 抢救主诊断描述
		}
		CODE_REMARK.setValue((String) pat.getData("CODE_REMARK", 0));// 诊断备注
		CODE_STATUS.setValue((String) pat.getData("CODE_STATUS", 0));// 诊断转归
		HEAL_LV.setValue((String) pat.getData("HEAL_LV", 0));// 愈合等级

		OP_CODE.setValue((String) pat.getData("OP_CODE", 0));// 主手术ICD
		OP_DATE.setValue((Timestamp) pat.getData("OP_DATE", 0));// 手术日期
		MAIN_SUGEON.setValue((String) pat.getData("MAIN_SUGEON", 0));// 手术人员
		OP_LEVEL.setValue((String) pat.getData("OP_LEVEL", 0));// 主手术等级

		GET_TIMES.setValue(pat.getData("GET_TIMES", 0) + ""); // 抢救次数
		SUCCESS_TIMES.setValue(pat.getData("SUCCESS_TIMES", 0) + "");// 成功次数
		DR_CODE.setValue((String) pat.getData("DR_CODE", 0));// 经治医生
		// 设置实际留观天数：未出->当前时间-入留观日；已出->出院时间-入留观日
		REAL_STAY_DAYS.setValue(getRealInDays(pat.getData("OUT_DATE", 0) + "", pat));

		ACCOMPANY_WEEK.setValue(pat.getData("ACCOMPANY_WEEK", 0) + "");// 随诊周数
		ACCOMPANY_MONTH.setValue(pat.getData("ACCOMPANY_MONTH", 0) + "");// 随诊月数
		ACCOMPANY_YEAR.setValue(pat.getData("ACCOMPANY_YEAR", 0) + "");	// 随诊年数
		ACCOMP_DATE.setValue((Timestamp) pat.getData("ACCOMP_DATE", 0));// 随诊日期	
	}

	/**
	 * 得到实际留观天数
	 * 
	 * @param startDate
	 *            Timestamp
	 * @param endDate
	 *            Timestamp
	 * @return int
	 */
	public int getRealInDays(String outDate, TParm data) {
		Timestamp endDate = TJDODBTool.getInstance().getDBTime();
		// 当没有入院日的时候IN_DATE=当天
		Timestamp startDate =
				data.getTimestamp("IN_DATE", 0) == null ? endDate : data.getTimestamp("IN_DATE", 0);
		// 当没有转出住标记的的时候
		if (!(outDate.trim().length() == 0 || "null".equals(outDate))) {
			endDate = data.getTimestamp("OUT_DATE", 0);
		}
		int diff = StringTool.getDateDiffer(endDate, startDate);
		return diff;
	}

	/**
	 * 转出、转住院、召回radio选择事件
	 * @param status
	 */
	public void onOutInReturn(String status) {
		this.clearValue("DISCHG_TYPE;DISCHG_DATE_DAY;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE_DAY;RETURN_DATE");
		this.setEnabled("DISCHG_TYPE;DISCHG_DATE_DAY;TRAN_HOSP;IPD_IN_DEPT;IPD_IN_DATE_DAY;RETURN_DATE", false);	
		if (status.equals("Out")) {// 出院
			this.setEnabled("DISCHG_TYPE;DISCHG_DATE_DAY;TRAN_HOSP", true);
		} else if (status.equals("In")) {// 转住院
			this.setEnabled("IPD_IN_DEPT;IPD_IN_DATE_DAY", true);
		} else if (status.equals("Return")) {// 召回
			this.setEnabled("RETURN_DATE", true);
		}
	}

	/**
	 * 得到号码为1的页签的参数
	 * 
	 * @return TParm
	 */
	public TParm getPage1Data() {
		TParm result = new TParm();
		if(Radio0.isSelected()){
			result.setData("STATUS", "1");
		}else if(Radio1.isSelected()){
			result.setData("STATUS", "2");
		}else if(Radio2.isSelected()){
			result.setData("STATUS", "3");
		}else{
			result.setData("STATUS", "0");
		}
		result.setData("CASE_NO", caseNo);
		result.setData("MR_NO", mrNo);
		result.setData("ERD_NO", ""); // 暂时保留
		result.setData("PAT_NAME", PAT_NAME.getValue() == null ? new TNull(String.class) : PAT_NAME
				.getValue());
		result.setData("AGE", AGE.getValue() == null ? new TNull(String.class) : AGE.getValue());
		result.setData("IDNO", IDNO.getValue() == null ? new TNull(String.class) : IDNO.getValue());
		result.setData("MARRIGE", MARRIGE.getValue() == null ? new TNull(String.class) : MARRIGE
				.getValue());
		result.setData("O_TEL", O_TEL.getValue() == null ? new TNull(String.class) : O_TEL
				.getValue());
		// result.setData("H_TEL",
		// H_TEL.getValue() == null ? new TNull(String.class) :
		// H_TEL.getValue());
		result.setData("O_POSTNO", O_POSTNO.getValue() == null ? new TNull(String.class) : O_POSTNO
				.getValue());
		result.setData("OFFICE", OFFICE.getValue() == null ? new TNull(String.class) : OFFICE
				.getValue());
		result.setData("O_ADDRESS", O_ADDRESS.getValue() == null ? new TNull(String.class)
				: O_ADDRESS.getValue());
		result.setData("H_ADDRESS", H_ADDRESS.getValue() == null ? new TNull(String.class)
				: H_ADDRESS.getValue());
		result.setData("H_POSTNO", H_POSTNO.getValue() == null ? new TNull(String.class) : H_POSTNO
				.getValue());
		result.setData("CONTACTER", CONTACTER.getValue() == null ? new TNull(String.class)
				: CONTACTER.getValue());
		result.setData("CONT_TEL", CONT_TEL.getValue() == null ? new TNull(String.class) : CONT_TEL
				.getValue());
		result.setData("CONT_ADDRESS", CONT_ADDRESS.getValue() == null ? new TNull(String.class)
				: CONT_ADDRESS.getValue());
		// 从隐藏控件中拿到
		result.setData("OUT_DIAG_CODE", HIDE_CODE.getValue() == null ? new TNull(String.class)
				: HIDE_CODE.getValue());
		result.setData("CODE_REMARK", CODE_REMARK.getValue() == null ? new TNull(String.class)
				: CODE_REMARK.getValue());
		result.setData("CODE_STATUS", CODE_STATUS.getValue() == null ? new TNull(String.class)
				: CODE_STATUS.getValue());
		result.setData("OP_CODE", OP_CODE.getValue() == null ? new TNull(String.class) : OP_CODE
				.getValue());
		result.setData("OP_LEVEL", OP_LEVEL.getValue() == null ? new TNull(String.class) : OP_LEVEL
				.getValue());
		result.setData("HEAL_LV", HEAL_LV.getValue() == null ? new TNull(String.class) : HEAL_LV
				.getValue());
		result.setData("SEX", SEX.getValue() == null ? new TNull(String.class) : SEX.getValue());
		result.setData("CTZ1_CODE", CTZ1_CODE.getValue() == null ? new TNull(String.class)
				: CTZ1_CODE.getValue());
		result.setData("OCCUPATION", OCCUPATION.getValue() == null ? new TNull(String.class)
				: OCCUPATION.getValue());
		result.setData("RESID_PROVICE", RESID_PROVICE.getValue() == null ? new TNull(String.class)
				: RESID_PROVICE.getValue());
		result.setData("RESID_COUNTRY", RESID_COUNTRY.getValue() == null ? new TNull(String.class)
				: RESID_COUNTRY.getValue());
		result.setData("FOLK", FOLK.getValue() == null ? new TNull(String.class) : FOLK.getValue());
		result.setData("RELATIONSHIP", RELATIONSHIP.getValue() == null ? new TNull(String.class)
				: RELATIONSHIP.getValue());
		result.setData("NATION", NATION.getValue() == null ? new TNull(String.class) : NATION
				.getValue());
		result.setData("ERD_REGION", ERD_REGION.getValue() == null ? new TNull(String.class)
				: ERD_REGION.getValue());
		result.setData("OUT_ERD_REGION", OUT_ERD_REGION.getValue() == null
				? new TNull(String.class) : OUT_ERD_REGION.getValue());// 出抢救区
		result.setData("IN_DEPT", IN_DEPT.getValue() == null ? new TNull(String.class) : IN_DEPT
				.getValue());
		result.setData("OUT_DEPT", OUT_DEPT.getValue() == null ? new TNull(String.class) : OUT_DEPT
				.getValue());// 出抢救科室
		result.setData("DR_CODE", DR_CODE.getValue() == null ? new TNull(String.class) : DR_CODE
				.getValue());
		result.setData("MAIN_SUGEON", MAIN_SUGEON.getValue() == null ? new TNull(String.class)
				: MAIN_SUGEON.getValue());
		result.setData("REAL_STAY_DAYS", REAL_STAY_DAYS.getValue() == null ? 0.00 : REAL_STAY_DAYS
				.getValue());
		result.setData("GET_TIMES", GET_TIMES.getValue() == null ? 0.00 : GET_TIMES.getValue());
		result.setData("SUCCESS_TIMES", SUCCESS_TIMES.getValue() == null ? 0.00 : SUCCESS_TIMES
				.getValue());
		result.setData("ACCOMPANY_WEEK", ACCOMPANY_WEEK.getValue() == null ? 0.00 : ACCOMPANY_WEEK
				.getValue());
		result.setData("ACCOMPANY_YEAR", ACCOMPANY_YEAR.getValue() == null ? 0.00 : ACCOMPANY_YEAR
				.getValue());
		result.setData("ACCOMPANY_MONTH", ACCOMPANY_MONTH.getValue() == null ? 0.00
				: ACCOMPANY_MONTH.getValue());
		result.setData("BIRTH_DATE", BIRTH_DATE.getValue() == null ? new TNull(Timestamp.class)
				: BIRTH_DATE.getValue());
		result.setData("IN_DATE", IN_DATE.getValue() == null ? new TNull(Timestamp.class) : IN_DATE
				.getValue());
		result.setData("OUT_DATE", OUT_DATE.getValue() == null ? new TNull(Timestamp.class)
				: OUT_DATE.getValue());// 出抢救日
		result.setData("OP_DATE", OP_DATE.getValue() == null ? new TNull(Timestamp.class) : OP_DATE
				.getValue());
		result.setData("ACCOMP_DATE", ACCOMP_DATE.getValue() == null ? new TNull(Timestamp.class)
				: ACCOMP_DATE.getValue());
		result.setData("DISCHG_TYPE", DISCHG_TYPE.getValue() == null ? new TNull(String.class)
				: DISCHG_TYPE.getValue());// 离院方式
		result.setData("IPD_IN_DEPT", IPD_IN_DEPT.getValue() == null ? new TNull(String.class)
				: IPD_IN_DEPT.getValue());// 住院科别
		result.setData("TRAN_HOSP", TRAN_HOSP.getValue() == null ? new TNull(String.class)
				: TRAN_HOSP.getValue());// 转至医院
		String dischDateString = DISCHG_DATE_DAY.getText();
		result.setData("DISCHG_DATE", dischDateString.equals("") ? new TNull(Timestamp.class)
				: StringTool.getTimestamp(dischDateString, "yyyy/MM/dd HH:mm:ss"));// 离院时间
		String ipdInDateString = IPD_IN_DATE_DAY.getText();
		result.setData("IPD_IN_DATE", ipdInDateString.equals("") ? new TNull(Timestamp.class)
				: StringTool.getTimestamp(ipdInDateString, "yyyy/MM/dd HH:mm"));// 住院日期
		TTextFormat RETURN_DATE = (TTextFormat) this.getComponent("RETURN_DATE");
		String returnDate = RETURN_DATE.getText();
		result.setData("RETURN_DATE", returnDate.length() == 0 ? new TNull(Timestamp.class)
				: getValue("RETURN_DATE"));
		result.setData("OPT_USER", Operator.getID());
		result.setData("OPT_TERM", Operator.getIP());
		return result;
	}

	public static void main(String[] args) {
		//		JavaHisDebug.initClient();
		//		// JavaHisDebug.TBuilder();
		//		// JavaHisDebug.TBuilder();
		//		JavaHisDebug.runFrame("erd\\ERDDynamicRcd.x");
	}

	/**
	 * 测试
	 */
	public void onTest(){
		System.out.println("---result="+this.getErdStartTime("111"));
	}
	
	
	// ------------------------------------add by wangqing 2080131 start------------------------------

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
	
	
	
	/**
	 * 获取erd_record数据
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 * @return
	 */
	public TParm getErdRecord(String mrNo, String caseNo, String triageNo){
		String sql = "SELECT * FROM ERD_RECORD WHERE CASE_NO='" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()<0){
			return parm;
		}
		String s =" SELECT ERD_REGION_CODE, BED_NO, BED_DESC, OCCUPY_FLG, TRIAGE_NO "
				+ "FROM ERD_BED "
				+ "WHERE TRIAGE_NO='"+triageNo+"' AND OCCUPY_FLG='Y' ";
		TParm r = new TParm(TJDODBTool.getInstance().select(s));
		if(r.getErrCode()<0){
			return r;
		}
		// 打开出院界面时，为了兼容老数据，
		// 先判断ERD_RECORD是否有患者数据，并且患者是否在床;
		// 若ERD_RECORD没有数据并且患者在床，则插入一笔
		// 待系统稳定后，屏蔽掉这块代码；
		if(parm.getCount()<=0 && r.getCount()>0){
			parm = this.insertErdRecord(mrNo, caseNo, triageNo);
			if(parm.getErrCode()<0){
				return parm;
			}
		}
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 向ERD_RECORD补录数据
	 * @author wangqing 20180131
	 * @param mrNo
	 * @param caseNo
	 * @param triageNo
	 */
	public TParm insertErdRecord(String mrNo, String caseNo, String triageNo){
		TParm parm = new TParm();
		parm = copyPatDate(mrNo, caseNo, triageNo);
		if(parm.getErrCode()<0){
			return parm;
		}
		parm = TIOM_AppServer.executeAction("action.erd.ERDDynamicRcdAction", "insertErdRecordFinal", parm);
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
	/*	if(sysPatInfoParm.getErrCode()<0){
			return sysPatInfoParm;
		}*/
		
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
	 * 批量设置是否可编辑
	 * @param names
	 * @param isEnabled
	 */
	public void setEnabled(String names, boolean isEnabled){
		if(names==null || names.trim().length()<=0){
			return;
		}
		String[] nameArr = names.split(";");
		for(int i=0; i<nameArr.length; i++){
			JComponent c = (JComponent)this.getComponent(nameArr[i]);
			if(c!=null){
				c.setEnabled(isEnabled);	
			}else{

			}			
		}
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
	 * 根据检伤号查询患者挂号信息
	 * @param triageNo
	 * @return
	 */
	public TParm getPatRegInfoByTriageNo(String triageNo){
		String sql = "SELECT * FROM REG_PATADM WHERE TRIAGE_NO='"+triageNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 校验患者是否转出
	 * @param triageNo
	 * @return
	 */
	public TParm getErdOutData(String triageNo){
		String sql = " SELECT TRIAGE_NO, CASE_NO, ERD_REGION_CODE, BED_NO, OUT_DATE FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' "
				+ "AND ERD_REGION_CODE IS NOT NULL AND BED_NO IS NOT NULL AND OUT_DATE IS NOT NULL AND CASE_NO IS NOT NULL ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	
	
	// ------------------------------------add by wangqing 2080131 end------------------------------
	
	
	
	
	
	
	

}
