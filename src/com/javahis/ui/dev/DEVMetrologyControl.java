package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;

import jdo.sys.SystemTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 设备计量
 * </p>
 * 
 * <p>
 * Description: 设备计量
 * </p>
 * <p>
 * Copyright: BlueCore 2015
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class DEVMetrologyControl extends TControl {
	
	/**
	 * 初始化
	 */
	public void onInit(){
		this.getComboBox("MTN_KIND").setValue(2);
		TParm parm = new TParm();
		//sysID.equals("MRO")
//		((TMenuItem) getComponent("save")).setEnabled(false);
		getTextField("DEV_CODE").setPopupMenuParameter("",
	            getConfigParm().newConfig("%ROOT%\\config\\dev\\DEVBasePopup.x"),
	            parm);
	    // 定义接受返回值方法
	    getTextField("DEV_CODE").addEventListener(
	            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	    TParm parm1 = new TParm();
	    parm1.setData("DEV_CODE", this.getValue("DEV_CODE"));
	    parm1.setData("MTN_KIND", this.getValue("MTN_KIND"));
	    getTextField("MTN_TYPE_CODE").setPopupMenuParameter("",
	            getConfigParm().newConfig("%ROOT%\\config\\dev\\DEVMtnListPopup.x"),
	            parm1);
	    // 定义接受返回值方法
	    getTextField("MTN_TYPE_CODE").addEventListener(
	            TPopupMenuEvent.RETURN_VALUE, this, "devMtnListPopReturn");
	    //初始化查询区间
	    this.setValue("START_DATE", StringTool.rollDate(SystemTool
				.getInstance().getDate(), -7));
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("NEXT_MTN_DATE", SystemTool.getInstance().getDate());
	}
	
	/**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String dev_code = parm.getValue("DEV_CODE");
        if (!StringUtil.isNullString(dev_code)){
        	this.setValue("DEV_CODE", dev_code);
        }
        String dev_desc = parm.getValue("DEV_CHN_DESC");
        if (!StringUtil.isNullString(dev_desc)){
        	this.setValue("DEV_DESC", dev_desc);
        }
    }
    
	/**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void devMtnListPopReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String mtn_type_code = parm.getValue("MTN_TYPE_CODE");
        if (!StringUtil.isNullString(mtn_type_code)){
        	this.setValue("MTN_TYPE_CODE", mtn_type_code);
        }
        String mtn_type_desc = parm.getValue("MTN_TYPE_DESC");
        if (!StringUtil.isNullString(mtn_type_desc)){
        	this.setValue("MTN_TYPE_DESC", mtn_type_desc);
        }
    }
	
	/**
	 * 计量保存
	 */
	public void onSave(){
		TTable tableM = this.getTable("TABLEM");
		TParm mParm = tableM.getParmValue();
		TParm parm = new TParm();
//		int n=1;
//		for(int i=0;i<mParm.getCount();i++){
//			if(mParm.getValue("SELECT_FLG", i).equals("Y")){
//				parm.addData("SAVE_FLG", "Y");//计量保存
//				parm.addData("DEV_CODE", mParm.getValue("DEV_CODE", i));
//				parm.addData("MTN_KIND", mParm.getValue("MTN_KIND", i));
//				parm.addData("MTN_TYPE_CODE", mParm.getValue("MTN_TYPE_CODE", i));
//				parm.addData("DEVSEQ_NO", mParm.getValue("DEVSEQ_NO", i));
//				parm.addData("NEXT_MTN_DATE", this.getNextMtnDate(mParm.getValue("NEXT_MTN_DATE", i),
//																	mParm.getInt("MEASUREM_CYCLE", i),
//																	mParm.getInt("MEASUREM_UNIT", i)));
//				parm.addData("OPT_USER", Operator.getID());
//				parm.addData("OPT_TERM", Operator.getIP());
//				parm.setCount(n);
//				n++;
//			}
//		}
		String mtn_no = SystemTool.getInstance().getNo("ALL", "DEV",
                "MTN_NO", "MTN_NO");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
		for(int i=0;i<mParm.getCount();i++){
			if(mParm.getValue("SELECT_FLG", i).equals("Y")){
	    		parm.addData("MTN_NO", mtn_no);//维护编号
	    		parm.addData("SEQ", i+1);//顺序号
	    		parm.addData("DEPT_CODE", mParm.getValue("DEPT_CODE", i));//科室编码
	    		parm.addData("DEV_CODE", mParm.getValue("DEV_CODE", i));//设备代码
	    		parm.addData("MTN_KIND", mParm.getValue("MTN_KIND", i));//维护种类(0_保养,1_质控,2_计量)
	    		parm.addData("MTN_TYPE_CODE", mParm.getValue("MTN_TYPE_CODE", i));//维护类型代码
	    		parm.addData("MTN_DATE", sdf.format(new Date()));//维护日期
	    		parm.addData("MTN_HOUR", 0);//维护用时(单位:小时)
	    		parm.addData("MTN_ENGINEER", Operator.getID());//维护工程师
	    		parm.addData("MTN_RESULT", "");//维护结果(0_正常,1_异常)
	    		parm.addData("MTN_EVALUATION", "");//维护评价及说明
	    		parm.addData("FILE_PATH", "");//维护记录文件路径
	    		parm.addData("FILE_NAME", "");//维护记录文件名
	    		parm.addData("OPT_USER", Operator.getID());
	    		parm.addData("OPT_DATE", "");
	    		parm.addData("OPT_TERM", Operator.getIP());
	    		parm.addData("DEV_CODE_DETAIL", mParm.getValue("DEVSEQ_NO", i));
	    		parm.addData("NEXT_MTN_DATE", this.getNextMtnDate(mParm.getValue("NEXT_MTN_DATE", i),
																	mParm.getInt("MEASUREM_CYCLE", i),
																	mParm.getInt("MEASUREM_UNIT", i)));
	    		parm.setCount(i+1);
			}
    	}
		
//		System.out.println("parm=="+parm);
		if(parm.getCount() <= 0){
			this.messageBox("没有要保存的数据！");
			return;
		}
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onInsertMaintenanceRecord", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败");
			err(result.getErrText());
			return;
		}
		this.messageBox("保存成功");
		this.onClear();
	}
	
    /**
     * 获取下次维护时间
     * @param nowMtnDate 本次维护日期
     * @param mtnCycle 维护周期
     * @param mtnUnit 维护周期单位(0,年;1,月;2,周;3,日)
     * @return
     */
    public String getNextMtnDate(String nowMtnDate,int mtnCycle,int mtnUnit){
    	String nextMtnDate = "";
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");//小写的mm表示的是分钟  
    	try {
			Date date=sdf.parse(nowMtnDate.substring(0, 10).replace("-", "/"));
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			switch (mtnUnit) {
			case 0:
				cal.add(Calendar.YEAR, mtnCycle);
				break;
			case 1:
				cal.add(Calendar.MONTH, mtnCycle);
				break;
			case 2:
				cal.add(Calendar.DATE, mtnCycle*7);
				break;
			case 3:
				cal.add(Calendar.DATE, mtnCycle);
				break;
			}
			nextMtnDate = sdf.format(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return nextMtnDate;
    }
	
	/**
	 * 修改计量时间
	 */
	public void onUpdateDate(){
		TTable tableM = this.getTable("TABLEM");
//		System.out.println("SelectedRow:"+tableM.getSelectedRow());
		String nextMtnDate = this.getValueString("NEXT_MTN_DATE");
		if(nextMtnDate.equals("")){
			this.messageBox("下次保养日期不能为空!");
			return;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
		try {
			Date date1=sdf.parse(nextMtnDate.substring(0, 10));
			Date date2=new Date();
			date2=sdf.parse(sdf.format(date2));
			if(date2.after(date1)){
				this.messageBox("下次维护日期不能小于当前时间");
				return;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("nextMtnDate:"+nextMtnDate);
		TParm mParm = tableM.getParmValue();
//		System.out.println("mParm:"+mParm);
		TParm parm = new TParm();
		int n=1;
		for(int i=0;i<mParm.getCount();i++){
			if(mParm.getValue("SELECT_FLG", i).equals("Y")){
				parm.addData("SAVE_FLG", "N");//修改计量时间
				parm.addData("DEV_CODE", mParm.getValue("DEV_CODE", i));
				parm.addData("MTN_KIND", mParm.getValue("MTN_KIND", i));
				parm.addData("MTN_TYPE_CODE", mParm.getValue("MTN_TYPE_CODE", i));
				parm.addData("DEVSEQ_NO", mParm.getValue("DEVSEQ_NO", i));
				parm.addData("NEXT_MTN_DATE", nextMtnDate.substring(0, 19).replace("-", "/"));
				parm.addData("OPT_USER", Operator.getID());
				parm.addData("OPT_TERM", Operator.getIP());
				parm.setCount(n);
				n++;
			}
		}
//		System.out.println("parm=="+parm);
		if(parm.getCount() <= 0){
			this.messageBox("没有要保存的数据！");
			return;
		}
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onUpdateMaintenance", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败");
			err(result.getErrText());
			return;
		}
		this.messageBox("计量时间修改成功");
		this.onClear();
	}
	
	/**
	 * 查询
	 */
    public void onQuery(){
    	this.setValue("SELECT_ALL", "N");
    	String sql = "SELECT 'N' AS SELECT_FLG,A.DEV_CODE,A.MTN_KIND,A.MTN_TYPE_CODE,A.DEVSEQ_NO,A.NEXT_MTN_DATE, "
    			+ " A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.DEPT_CODE,C.DEV_CHN_DESC,D.MEASUREM_DESC, "
    			+ " D.MEASUREM_CYCLE,D.MEASUREM_UNIT,D.MEASUREM_CODE "
    			+ " FROM DEV_MAINTENANCE_DATE A, DEV_STOCKDD B,DEV_BASE C,DEV_MEASURE D "
    			+ " WHERE A.DEV_CODE = B.DEV_CODE "
    			+ " AND A.DEVSEQ_NO = B.DEV_CODE_DETAIL "
    			+ " AND A.DEV_CODE=C.DEV_CODE "
//    			+ " AND A.MTN_KIND='"+this.getComboBox("MTN_KIND").getValue()+"' "
    			+ " AND A.MTN_TYPE_CODE=D.MEASUREM_CODE "
    			+ " AND D.ACTIVE_FLG='Y' ";
    	String startDate = this.getValueString("START_DATE");//开始时间
		String endDate = this.getValueString("END_DATE");//结束时间
		String deptCode = this.getValueString("DEPT_CODE");
		String devseqNo = this.getValueString("DEVSEQ_NO");//资产编号
		String dev_code = this.getValueString("DEV_CODE");//设备代码
		String mtnKind = this.getValueString("MTN_KIND");
		String mtnTypeCode = this.getValueString("MTN_TYPE_CODE");
		if(!dev_code.equals("")){
			sql += " AND A.DEV_CODE='"+dev_code+"' ";
		}
		if(!deptCode.equals("")){
			sql += " AND B.DEPT_CODE='"+deptCode+"' ";
		}
		if(!startDate.equals("") && !endDate.equals("")){
			sql += " AND A.NEXT_MTN_DATE BETWEEN TO_DATE('"
						+startDate.substring(0, 10)
						+"','YYYY/MM/DD') AND TO_DATE('"
						+ endDate.substring(0, 10)
						+ "','YYYY/MM/DD') ";
		}
		if(!devseqNo.equals("")){
			sql += " AND A.DEVSEQ_NO='"+devseqNo+"' ";
		}
//		if(!mtnKind.equals("")){
//			sql += " AND A.MTN_KIND='"+mtnKind+"' ";
//		}
		if(!mtnTypeCode.equals("")){
			sql += " AND A.MTN_TYPE_CODE='"+mtnTypeCode+"' ";
		}
		sql += " ORDER BY A.DEV_CODE ";
//    	System.out.println("sql>>>>>>>>"+sql);
    	TTable tableM = this.getTable("TABLEM");
    	TParm mParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(mParm.getCount() <= 0){
    		this.messageBox("未查询到数据");
    		tableM.removeRowAll();
    		return;
    	}
    	for(int i=0;i<mParm.getCount("DEV_CODE");i++){
    		mParm.setData("OPT_DATE", i, mParm.getValue("OPT_DATE", i).substring(0, 19));
    	}
//    	System.out.println("mParm>>>>>>"+mParm);
    	tableM.setParmValue(mParm);
    }
    
    /**
     * 清空
     */
    public void onClear(){
    	this.clearValue("DEPT_CODE;DEV_CODE;DEV_DESC;DEVSEQ_NO;MTN_TYPE_CODE;MTN_TYPE_DESC");
    	//初始化查询区间
	    this.setValue("START_DATE", StringTool.rollDate(SystemTool
				.getInstance().getDate(), -7));
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("NEXT_MTN_DATE", SystemTool.getInstance().getDate());
		this.setValue("SELECT_ALL", "N");
    	TTable tableM = this.getTable("TABLEM");
    	tableM.removeRowAll();
    }
    
    public void onAddEmrWrite(){
//    	openPrintWindow("%ROOT%\\config\\prt\\2222_检验汇总报告.jhw","");
//    	if(true){
//    		return;
//    	}
//    	TParm parm = new TParm();
//    	parm.setData("AAA","TEXT", "11111");
//    	parm.addData("NAME2", "11111");
//    	parm.setCount(parm.getCount("NAME")); 
//    	parm.addData("SYSTEM", "COLUMNS", "NAME");
//    	parm.addData("SYSTEM", "COLUMNS", "NAME2");
//        TParm printParm = new TParm();         
//        
//        printParm.setData("TABLE_MES",parm.getData());  
//    	EMRTool emrTool = new EMRTool("2222","1111", this);
//		Object obj = new Object();
//		obj = this.openPrintDialog("%ROOT%\\config\\prt\\DEV\\1111.jhw",
//				parm);
////		emrTool.saveEMR(obj, "检验汇总报告", "EMR040002", "EMR04000204", false);
//		emrTool.saveEMR(obj, "检验汇总报告", "EMR100002", "EMR10000202", false);
//    	if(true){
//    		return;
//    	}
    	
    	TParm devMesParm = new TParm();//设备信息
    	TParm devMaiParm = new TParm();//设备维护细项
    	TParm result = new TParm();
    	TTable tableM = this.getTable("TABLEM");
    	TParm mParm = new TParm();
    	mParm = tableM.getParmValue();
    	int n=1;
		for(int i=0;i<mParm.getCount();i++){
			if(mParm.getValue("SELECT_FLG", i).equals("Y")){
				devMesParm.addData("DEV_CODE", mParm.getValue("DEV_CODE", i));//设备编码
				devMesParm.addData("DEV_CODE_DETAIL", mParm.getValue("DEVSEQ_NO", i));//资产编号
				String devSql = "SELECT BRAND,MAN_NATION,SPECIFICATION,MAN_NATION FROM DEV_BASE WHERE DEV_CODE='"+mParm.getValue("DEV_CODE", i)+"' ";
				TParm devParm = new TParm(TJDODBTool.getInstance().select(devSql));
				devMesParm.addData("BRAND", devParm.getValue("BRAND", 0));
				devMesParm.addData("MAN_NATION", devParm.getValue("MAN_NATION", 0));//国别品牌
				devMesParm.addData("SPECIFICATION", devParm.getValue("SPECIFICATION", 0));//规格
				devMesParm.addData("MTN_KIND", mParm.getValue("MTN_KIND", i));
				devMesParm.addData("MTN_TYPE_CODE", mParm.getValue("MTN_TYPE_CODE", i));
				devMesParm.addData("DEV_CHN_DESC", mParm.getValue("DEV_CHN_DESC", i));//设备名称
				String deptSql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+mParm.getValue("DEPT_CODE", i)+"' ";
				TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
				devMesParm.addData("DEPT_CHN_DESC", deptParm.getValue("DEPT_CHN_DESC", 0));
				devMesParm.addData("DEPT_CODE", mParm.getValue("DEPT_CODE", i));
				devMesParm.addData("NEXT_MTN_DATE", mParm.getValue("NEXT_MTN_DATE", i));
//				devMesParm.addRowData(mParm.getRow(i), n);
				devMesParm.setCount(n);
				n++;
			}
		}
		if(n>2){//多个设备
			result.setData("MORE_FLG", "Y");
			for(int j=0;j<devMesParm.getCount();j++){
				if(!devMesParm.getValue("DEV_CODE", 0).equals(devMesParm.getValue("DEV_CODE", j))
						|| !devMesParm.getValue("MTN_TYPE_CODE", 0).equals(devMesParm.getValue("MTN_TYPE_CODE", j))){
					this.messageBox("请选择同一类设备和同一类维护类别！");
					return;
				}
			}
		}else{//单个设备
			result.setData("MORE_FLG", "N");
		}
		result.setData("DEV_MES_PARM", devMesParm.getData());
		String sql = "SELECT MTN_DETAIL_DESC FROM DEV_MAINTENANCED WHERE DEV_CODE='"
				+devMesParm.getValue("DEV_CODE", 0)
				+ "' AND MTN_KIND='"
				+devMesParm.getValue("MTN_KIND", 0)
				+ "' AND MTN_TYPE_CODE='"
				+devMesParm.getValue("MTN_TYPE_CODE", 0)
//				+ "' AND MTN_DETAIL_CODE='"
//				+devMesParm.getValue("DEVSEQ_NO", 0)
				+ "' ";
//		System.out.println("sql--"+sql);
		devMaiParm = new TParm(TJDODBTool.getInstance().select(sql));
		result.setData("DEV_MAI_PARM", devMaiParm.getData());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		result.setData("DATE", df.format(new Date())+"");
//		System.out.println("result:"+result);
    	this.openDialog("%ROOT%\\config\\dev\\DEVMaintenanceRecord.x",result);
//    	TTable table = this.getTable("TABLEM");
//    	int row = table.getSelectedRow();
//    	TParm parm = table.getParmValue().getRow(row);
////    	System.out.println("parm:"+parm);
//    	TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
//				"onDeleteMeasuremDic", parm);
//		if (result.getErrCode() < 0) {
//			this.messageBox("删除失败");
//			err(result.getErrText());
//			return;
//		}
//		this.messageBox("删除成功");
//		this.onClear();
//		this.onQuery();
    }
    
    /**
     * M表单击事件，单击查询细表信息
     */
    public void onTableMClick(){
    	TTable tableM = this.getTable("TABLEM");
    	int row = tableM.getSelectedRow();
    	int col = tableM.getSelectedColumn();
    	if(row < 0){
    		return;
    	}else if(col == 0){
    		if ("N".equals(tableM.getItemString(row, "SELECT_FLG"))) {
    			tableM.setItem(row, "SELECT_FLG", "Y");
			} else if ("Y".equals(tableM.getItemString(row, "SELECT_FLG"))){
				tableM.setItem(row, "SELECT_FLG", "N");
			}
    	}
    }
    
	/**
	 * 全选
	 */
	public void onSelectAll() {
		TTable tableM = this.getTable("TABLEM");
		tableM.acceptText();
		if (tableM.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		for (int i = 0; i < tableM.getRowCount(); i++) {
			tableM.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
		}
	}
	
    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    /**
     * 得到Table对象
     * @param tagName
     * @return
     */
    private TTable getTable(String tagName){
    	return (TTable) this.getComponent(tagName);
    }
    
    /**
     * 得到ComboBox对象
     * @param tagName
     * @return
     */
    private TComboBox getComboBox(String tagName){
    	return (TComboBox) this.getComponent(tagName);
    }
    
    /**
     * 得到checkbox对象
     * @param tagName
     * @return
     */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
