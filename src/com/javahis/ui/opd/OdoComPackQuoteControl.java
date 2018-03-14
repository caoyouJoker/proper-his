package com.javahis.ui.opd;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.bcel.generic.NEWARRAY;

import jdo.clp.intoPathStatisticsTool;
import jdo.odo.OpdComPackQuoteTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.bcel.internal.generic.NEW;
/**
 * 
 * <p> Title: 门诊医生工作站常用组套调用 </p>
 * 
 * <p> Description:门诊医生工作站组套调用控制类 </p>
 * 
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 * 
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui 2009.05.03
 * 
 * 
 * @version 1.0
 */
public class OdoComPackQuoteControl extends TControl {
	//门急住别
	private String admtype;//yanjing 20130614
	//科别医师分类
	private String deptOrDr;
	//科别或医师代码
	private String code,value="",DEPT="1",DR="2";
	//初始化SQL
	private String INIT_SQL,INIT_DIAG_SQL,INIT_ORDER_SQL;
	//主DATASTORE
	private TDataStore dsMain,dsDiag,dsOrder,dsChn;
	//TABLE
	private TTable tableDiag,tableExa,tableMed,tableChn;
	//中医处方签combo
	private TComboBox rxNo;
	//模板代码combo
	private TComboBox combo;
	//进入界面的参数
	private TParm parm,chnParm;
	
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		//初始化参数
		initParameter();
		//初始化事件
		initEvent();
		//初始化TABLE
		initData();
		//初始化combo
		initCombo();
	}
	/**
	 * 初始化参数
	 */
	public void initParameter(){
		parm=(TParm)this.getParameter();
		deptOrDr=parm.getValue("DEPT_OR_DR");
		code=parm.getValue("DEPTORDR_CODE");
		admtype = parm.getValue("ADM_TYPE");
		Vector v=new Vector();
		Vector title=new Vector();
		title.add("id,name");
		v.add(title);
		Vector data=new Vector();
		v.add(data);
		TComboBox deptCom=(TComboBox)this.getComponent("INITDEPT");
		TComboBox operatorCom=(TComboBox)this.getComponent("INITOPERATOR");
		TLabel label=(TLabel)this.getComponent("LABEL");
		
		if(DEPT.equalsIgnoreCase(deptOrDr)){
			label.setEnText("Dept.");
			label.setZhText("科室");
			deptCom.setSelectedID(code);
			deptCom.setVisible(true);
			operatorCom.setVisible(false);
		}else{
			label.setEnText("Dr.");
			label.setZhText("医师");
			operatorCom.setSelectedID(code);
			operatorCom.setVisible(true);
			deptCom.setVisible(false);
		}
		//取得初始化SQL
		INIT_SQL=OpdComPackQuoteTool.getInstance().initQuote(parm);
	}
	/**
	 * 初始化控件
	 */
	public void initEvent(){
		//模板combo
		combo=(TComboBox)this.getComponent("PACK_CODE");
		//诊断TABLE
		tableDiag=(TTable)this.getComponent("TABLE_DIAG");
		//检验检查TABLE
		tableExa=(TTable)this.getComponent("TABLE_EXA");
		//药品TABLE
		tableMed=(TTable)this.getComponent("TABLE_MED");
		//中药TABLE
		tableChn=(TTable)this.getComponent("TABLE_CHN");
		//中医处方签combo
		rxNo=(TComboBox)this.getComponent("RX_NO");
	}
	/**
	 * 初始化主界面
	 */
	public void initData(){
		dsMain=new TDataStore();
		dsMain.setSQL(INIT_SQL);
		dsMain.retrieve();
	}
	/**
	 * 初始化主Combo
	 */
	public void initCombo(){
		//取得combo用数据
		TParm result = OpdComPackQuoteTool.getInstance().initCombo(parm);
		if(result.getErrCode()!=0){
//			this.messageBox_(result.getErrText());
			this.messageBox_("找不到数据");
			return;
		}
		combo.setParmValue(result);
		combo.onInit();
	}
	/**
	 * 主combo点选事件
	 */
	public void onComboSelect(){
		String packCode=combo.getSelectedID();
		filtDs(packCode);
		onClear();
		this.setValue("SUBJ_TEXT", dsMain.getItemString(0, "SUBJ_TEXT"));
		this.setValue("OBJ_TEXT", dsMain.getItemString(0, "OBJ_TEXT"));
		this.setValue("PHYSEXAM_REC", dsMain.getItemString(0, "PHYSEXAM_REC"));
		TParm diagParm=new TParm();
		diagParm.setData("DEPT_OR_DR",deptOrDr);
		diagParm.setData("DEPTORDR_CODE",code);
		diagParm.setData("PACK_CODE",packCode);
		//初始化诊断
		TParm diagResult=OpdComPackQuoteTool.getInstance().initQuoteDiag(diagParm);
		tableDiag.setParmValue(diagResult);
		//初始化医嘱
		TParm orderResult=OpdComPackQuoteTool.getInstance().initOrderForPackQuote(diagParm);
		// System.out.println("orderResult="+orderResult);
		tableMed.setParmValue(orderResult);
		//初始化中医
		chnParm=OpdComPackQuoteTool.getInstance().initChnForPackQuote(diagParm);
		initChn(chnParm);
		//初始化集合医嘱
		TParm exaResult=OpdComPackQuoteTool.getInstance().initExaForPackQuote(diagParm);
		tableExa.setParmValue(exaResult);
	}
	/**
	 * 初始化中医相关信息
	 * @param parm
	 * @return
	 */
	private boolean initChn(TParm parm){
		if(!initChnComobo(parm)){
//			this.messageBox_("初始化处方签失败");
			return false;
		}
		if(!initChnTable(parm)){
//			this.messageBox_("初始化中医数据失败");
			return false;
		}
		return true;
	}
	/**
	 * 初始化中医处方签数据，并选择最后一张
	 * @param parm
	 * @return
	 */
	private boolean initChnComobo(TParm parm){
		TParm comboParm=OpdComPackQuoteTool.getInstance().initChnCombo(parm);
		if(comboParm==null){
//			this.messageBox_("初始化处方签失败");
			return false;
		}
		
		rxNo.setParmValue(comboParm);
		String id=comboParm.getValue("ID",comboParm.getCount("ID")-1);
		rxNo.setValue(id);
		return true;
	}
	/**
	 * 初始化中医界面
	 * @param parm
	 * @return
	 */
	private boolean initChnTable(TParm parm){
		String rxNo=this.getValueString("RX_NO");
		if(StringUtil.isNullString(rxNo)){
//			this.messageBox_("取得处方签号失败");
			return false;
		}
		TParm tableParm=OpdComPackQuoteTool.getInstance().getChnTableParm(parm, rxNo);
		tableChn.setParmValue(tableParm);
		this.setValue("TAKE_DAYS", tableParm.getValue("TAKE_DAYS",0));
		this.setValue("DCT_TAKE_QTY", tableParm.getValue("DCT_TAKE_QTY",0));
		this.setValue("CHN_FREQ_CODE", tableParm.getValue("FREQ_CODE",0));
		this.setValue("CHN_ROUTE_CODE", tableParm.getValue("ROUTE_CODE",0));
		this.setValue("DCTAGENT_CODE", tableParm.getValue("DCTAGENT_CODE",0));
		this.setValue("DESCRIPTION", tableParm.getValue("DESCRIPTION",0));
		return true;
	}
	/**
	 * ds根据code过滤事件
	 * @param code String :PACK_CODE
	 */
	public void filtDs(String code){
		String filterString="PACK_CODE='" +code+"'";
		dsMain.setFilter(filterString);
		dsMain.filter();
	}
	/**
	 * 处方签改变事件
	 */
	public void onRxChange(){
		initChnTable(chnParm);
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.setValue("SUBJ_TEXT",value);
		this.setValue("OBJ_TEX", value);
		this.setValue("PHYSEXAM_REC", value);
		this.setValue("SUB_CHECK", true);
		this.setValue("OBJ_CHECK", true);
		this.setValue("PSY_CHECK", true);
		this.setValue("ORDER_CHECK", true);
		this.setValue("DIAG_CHECK", true);
		this.setValue("EXA_CHECK", true);
		this.tableMed.removeRowAll();
		this.tableExa.removeRowAll();
		this.tableChn.removeRowAll();
		this.tableDiag.removeRowAll();
	}
	/**
	 * 传回 根据是否传回的界面选择，传回该控件的值
	 */
	public void onFetch(){
		TParm result=new TParm();
		//yanjing 2014-04-16 取诊断表中的数据
		tableDiag.acceptText();
		tableExa.acceptText();
		TParm diag = tableDiag.getParmValue();
		TParm exa1 = tableExa.getParmValue();
		//yanjing 20130614 取药嘱表中数据
		tableMed.acceptText();
		TParm med = tableMed.getParmValue();
		//主诉
		if(TypeTool.getBoolean(this.getValue("SUB_CHECK"))){
			result.setData("SUBJ_TEXT",this.getValue("SUBJ_TEXT"));
		}
		//客诉
		if(TypeTool.getBoolean(this.getValue("OBJ_CHECK"))){
			result.setData("OBJ_TEXT",this.getValue("OBJ_TEXT"));
		}
		//体征
		if(TypeTool.getBoolean(this.getValue("PSY_CHECK"))){
			result.setData("PHYSEXAM_REC",this.getValue("PHYSEXAM_REC"));
		}
		//医嘱
		//yanjing2013-04-16 注
//		if(TypeTool.getBoolean(this.getValue("ORDER_CHECK"))){
			TParm order=new TParm();
			TParm ctrl=new TParm();
//			TParm exa=new TParm();//yanjing注2013—04-16
			TParm op=new TParm();
//			TParm chn=new TParm();//yanjing注2013—04-16
			tableMed.acceptText();
			TParm data=tableMed.getParmValue();
			String[] column=data.getNames();
			int count=column.length;
			int index=data.getCount("RX_TYPE");
		for (int i = 0; i < index; i++) {
			// yanjing2013-04-16添加是否勾选的校验
			if (data.getValue("CHOOSE", i).equals("Y")) {
				//药嘱在字典档是否存在校验，yanjing，2013/7/1
				if(med.getValue("ORDER_CODE",i).equals("")){
					this.messageBox(med.getValue("ORDER_DESC", i)+",在字典档不存在！");
					return;
				}
				//药嘱是否停用校验
				if(med.getValue("ACTIVE_FLG",i).equals("N")){
					this.messageBox((med.getValue("ORDER_DESC", i)+",已停用！"));
					return;
				}
				// add by yanj 2013/06/14门急诊适用校验
				// 门诊
				if ("O".equalsIgnoreCase(admtype)) {
					// 判断是否门急诊适用医嘱
					if (!("Y".equals(med.getValue("OPD_FIT_FLG",i)))) {
						// 不是门诊适用医嘱！
						this.messageBox(med.getValue("ORDER_DESC", i)+",不是门诊适用医嘱。");
						return;
					}
				}
				// 急诊
				if ("E".equalsIgnoreCase(admtype)) {
					if (!("Y".equals(med.getValue("EMG_FIT_FLG",i)))) {
						// 不是门诊适用医嘱！
						this.messageBox(med.getValue("ORDER_DESC", i)+",不是急诊适用医嘱。");
						return;
					}
				}
				// $$===========add by yanj 2013/06/14门急诊适用校验
				if ("1".equalsIgnoreCase(data.getValue("RX_TYPE", i))) {
					for (int j = 0; j < count; j++) {
						order.addData(column[j], data.getValue(column[j], i));
					}
				} else if ("2".equalsIgnoreCase(data.getValue("RX_TYPE", i))) {
					for (int j = 0; j < count; j++) {
						ctrl.addData(column[j], data.getValue(column[j], i));
					}
				} else if ("4".equalsIgnoreCase(data.getValue("RX_TYPE", i))) {
					for (int j = 0; j < count; j++) {
						op.addData(column[j], data.getValue(column[j], i));
					}
				}
			}
		}
			result.setData("ORDER",order);
			result.setData("CTRL",ctrl);
			result.setData("OP",op);
			
			result.setData("CHN",getChnMap());
//		}
		//诊断
//		if(TypeTool.getBoolean(this.getValue("DIAG_CHECK"))){
//			result.setData("DIAG",tableDiag.getParmValue());
//		}
		//=====yanj 2014-04-16
		TParm medParm=new TParm();
		int medCount=0;//累计个数
			//=====2013-04-16 yanj 添加要传回诊断的校验
			for (int i = 0; i < diag.getCount(); i++) {
				if (diag.getValue("CHOOSE",i).equals("Y")){						
					medParm.setRowData(medCount, diag,i);
					medCount++;
					
					}
			}
			medParm.setCount(medCount);
				result.setData("DIAG",medParm);
		//处置及其他
//		if(TypeTool.getBoolean(this.getValue("EXA_CHECK"))){
//			result.setData("EXA",tableExa.getParmValue());
//		}
				//=====yanj 2014-04-16
				TParm exaParm=new TParm();
				int exaCount=0;//累计个数
					//=====2013-04-16 yanj 添加要传回检验检查的校验
//				System.out.println("检验检查 exa1 is：：："+exa1);
					for (int i = 0; i < exa1.getCount(); i++) {
						if (exa1.getValue("CHOOSE",i).equals("Y")){	
							//药嘱在字典档是否存在校验，yanjing，2013/7/1
							if(exa1.getValue("ORDER_CODE",i).equals("")){
								this.messageBox(exa1.getValue("ORDER_DESC", i)+",在字典档不存在！");
								return;
							}
							//药嘱是否停用校验
							if(exa1.getValue("ACTIVE_FLG",i).equals("N")){
								this.messageBox((exa1.getValue("ORDER_DESC", i)+",已停用！"));
								return;
							}
							
							// add by yanj 2013/07/17门急诊适用校验
							// 门诊
							if ("O".equalsIgnoreCase(admtype)) {
								// 判断是否门急诊适用医嘱
								if (!("Y".equals(exa1.getValue("OPD_FIT_FLG",i)))) {
									// 不是门诊适用医嘱！
									this.messageBox(exa1.getValue("ORDER_DESC", i)+",不是门诊适用检验检查。");
									return;
								}
							}
							// 急诊
							if ("E".equalsIgnoreCase(admtype)) {
								if (!("Y".equals(exa1.getValue("EMG_FIT_FLG",i)))) {
									// 不是门诊适用医嘱！
									this.messageBox(exa1.getValue("ORDER_DESC", i)+",不是急诊适用检验检查。");
									return;
								}
							}
							// $$===========add by yanj 2013/06/14门急诊适用校验
							exaParm.setRowData(exaCount, exa1,i);
							exaCount++;
							
							}
					}
					exaParm.setCount(exaCount);
						result.setData("EXA",exaParm);
		this.setReturnValue(result);
		this.closeWindow();
		}	
	/**
	 * 将中医数据整合正键是处方号，值是TParm的同调用就诊记录形式相同的回传数据以便医生站插入住对象
	 * @return
	 */
	private Map getChnMap(){
		Map result=new HashMap();
		if(chnParm==null){
			return null;
		}
		int count=chnParm.getCount();
		if(count<1){
			return null;
		}
		Vector prspNo=chnParm.getVectorValue("PRESRT_NO");
		if(prspNo==null){
			return null;
		}
		if(prspNo.size()<1){
			return null;
		}
		TParm parm=new TParm();
		String[] names=chnParm.getNames();
		StringBuffer nameSb=new StringBuffer();
		for(String temp:names){
			nameSb.append(temp).append(";");
		}
		String name=nameSb.deleteCharAt(nameSb.length()-1).toString();
		// System.out.println("names=========="+name);
		int nameLength=names.length;
		for(int i=0;i<count;i++){
			String presrtNo=chnParm.getValue("PRESRT_NO",i);
			if(result.get(presrtNo)==null){
				parm=new TParm();
				result.put(presrtNo, parm);
			}
			parm.addRowData(chnParm, i, name);
		}
		return result;
	}
	//yabjing 20130416添加全选功能
			/**
			 * 全选事件
			 * @param parm
			 */
			public void onSelAll(int tag) {
				TParm parm = new TParm();
				TParm tableParm = new TParm();
				switch (tag) {
				case 0:
					tableParm = tableDiag.getParmValue();
					parm.setData("CHECK", getValue("DIAG_CHECK"));
					break;
				case 1:
					tableParm = tableExa.getParmValue();
					parm.setData("CHECK", getValue("EXA_CHECK"));
					break;
				case 2:	
					tableParm = tableMed.getParmValue();
					parm.setData("CHECK", getValue("ORDER_CHECK"));
					break;
				default:
					break;
				}
		        for (int i = 0; i < tableParm.getCount(); i++) {
		            tableParm.setData("CHOOSE", i, parm.getValue("CHECK"));
		        }
		        if (tag == 0) {
					tableDiag.setParmValue(tableParm);
				}else if (tag == 1) {
					tableExa.setParmValue(tableParm);
				}else if (tag == 2) {
					tableMed.setParmValue(tableParm);
				}
		        return ;
			}
			
}
