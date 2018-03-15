package com.javahis.ui.mem;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JOptionPane;

import jdo.mem.MEMCtzTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>Title:身份设置 </p>
 *
 * <p>Description:身份设置 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author duzhw 2013.12.30
 * @version 1.0
 */
public class MEMCTZControl extends TControl {
	TParm delParm = new TParm();//医嘱删除
	TParm updateParm = new TParm();//医嘱修改
	String ctzCodeOper = "";//全局 刷新 
	double rate = 0.0000;//折扣率
	
	/**
     * 拿到所有的收据类别
     */
    TParm allCode = SYSChargeHospCodeTool.getInstance().selectalldata();
    
	/**
     * 身份表
     */
	private TTable tableCtz;
	/**
	 * 项目分类折扣表
	 */
	private TTable mainTable;
	/**
	 * 项目明细折扣表
	 */
	private TTable detailTable;
	
	//项目主表单击行数
	private int mainRow;
	//适用范围
	TCheckBox typeO;
	TCheckBox typeE;
	TCheckBox typeI;
	TCheckBox typeH;
	
	TCheckBox typeCtzDept;
	TCheckBox typeMainCtz;
	TCheckBox typeNhiCtz;
	TCheckBox typeMrctzUpd;
	
	TCheckBox mainCtzFlg;//主身份
	TCheckBox qUseFlg;//启用时间
	
	TCheckBox typeUse;
	TComboBox deptCode1;//科室下拉框
	TComboBox deptCode;//科室下拉框-右边
	TTextFormat ctzCode1;//身份类型下拉框
    
    
    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        
        initComponent();
        initData();
        // 给明细detailTable添加侦听事件
//        addEventListener("DETILTABLE->" + TTableEvent.CHANGE_VALUE,
//                         "onTableChangeValue");
        //折扣默认为1,不打折
    	this.setValue("DISCOUNT", "1.0000");
        }
    
    /**
     * 初始化数据
     */
    public void initData(){
    	
    	//查询开始结束时间置灰
    	callFunction("UI|S_DATE|setEnabled", false);
		callFunction("UI|E_DATE|setEnabled", false);
    	
    	String sql = "SELECT * FROM SYS_CTZ ORDER BY CTZ_CODE,SEQ ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	//System.out.println("result==="+result);
    	tableCtz.setParmValue(result);
    	ifUseFlg();//初始化是否启用状态
    }
    /**
	 * 初始化控件
	 */
    private void initComponent(){
    	tableCtz = getTable("TABLECTZ");
        mainTable = getTable("MAINTABLE");
        detailTable = getTable("DETILTABLE");
        
        TParm parm = new TParm();
        mainTable.setParmValue(parm);
        detailTable.setParmValue(parm);
    	
    	// 得到适用范围控件
    	typeO = (TCheckBox) this.getComponent("OPD_FIT_FLG");
    	typeE = (TCheckBox) this.getComponent("EMG_FIT_FLG");
    	typeI = (TCheckBox) this.getComponent("IPD_FIT_FLG");
    	typeH = (TCheckBox) this.getComponent("HRM_FIT_FLG");
    	
    	typeCtzDept = (TCheckBox) this.getComponent("CTZ_DEPT_FLG");
    	typeMainCtz = (TCheckBox) this.getComponent("MAIN_CTZ_FLG");
    	typeNhiCtz = (TCheckBox) this.getComponent("NHI_CTZ_FLG");
    	typeMrctzUpd = (TCheckBox) this.getComponent("MRCTZ_UPD_FLG");
    	mainCtzFlg = (TCheckBox) this.getComponent("MAINCTZFLG");
    	qUseFlg = (TCheckBox) this.getComponent("Q_USE_FLG");
    	
    	typeUse = (TCheckBox) this.getComponent("USE_FLG");
    	deptCode1 = (TComboBox) this.getComponent("DEPT_CODE1");//科室下拉框1
    	deptCode = (TComboBox) this.getComponent("DEPT_CODE");//科室下拉框-右边
    	ctzCode1 = (TTextFormat) this.getComponent("CTZCODE");//身份类型下拉框
    	
        // 套餐细相新增医嘱事件
    	detailTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                     "onDetailCreateEditComponent");
        // 套餐细相值改变事件
    	detailTable.addEventListener("DETILTABLE->" + TTableEvent.CHANGE_VALUE, this,
                                     "onTableChangeValue");
		
    	
    }
    /**
	 * 添加SYS_FEE弹出窗口
	 * @param com
	 * @param row
	 * @param column
	 */
    public void onDetailCreateEditComponent(Component com, int row, int column) {
        // 求出当前列号，只允许在医嘱名称列新增一条医嘱
        column = detailTable.getColumnModel().getColumnIndex(column);
        String columnName = detailTable.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        // String packCode = main.getItemString(this.mainRow, "PACKAGE_CODE");
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("HRM_TYPE", "ANYCHAR");
        // 给table上的新text增加sys_fee弹出窗口
        textfield.setPopupMenuParameter("ORDER",
                                        getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                        parm);
        // 给新text增加接受sys_fee弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }
    /**
     * 返回值处理
     * @param tag
     * @param obj
     */
    public void popOrderReturn(String tag, Object obj) {
    	TParm parm = (TParm) obj;
        // 判断是否是药品(PHA_W药品)
        String orderCat1Code = parm.getValue("ORDER_CAT1_CODE");
        int row = detailTable.getSelectedRow();
        int oldRow = row;
//        if (!StringUtil.isNullString(orderTable.getItemString(row, "ORDER_CODE"))) {
//            return;
//        }
        String order_code = parm.getValue("ORDER_CODE");
        String order_desc = parm.getValue("ORDER_DESC");
        // 判断是否有重复数据
        for (int i = 0; i < detailTable.getDataStore().rowCount(); i++) {
            if (i == detailTable.getSelectedRow()) {
                continue;
            }
            if (order_code.equals(detailTable.getDataStore().getItemData(i, "ORDER_CODE"))
                    && detailTable.getDataStore().getItemData(i, "SETMAIN_FLG").equals("Y")) {
                this.messageBox(order_desc + "(" + order_code + ") 已存在");
                return;
            }
        }
        detailTable.acceptText();
        detailTable.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        detailTable.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
       
        parm = null;
    }
    
    
    /**
	 * 主TABLE点击事件
	 */
    public void onCtzClick() {
    	//身份代码状态置无效
    	callFunction("UI|CTZ_CODE|setEnabled", false);
    	
    	int selectedIndx=tableCtz.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	TParm tableparm=tableCtz.getParmValue();
    	
    	String ctzCode = tableparm.getValue("CTZ_CODE",selectedIndx);
    	String ctzDesc = tableparm.getValue("CTZ_DESC",selectedIndx);
    	String memType = tableparm.getValue("MEM_TYPE",selectedIndx);
    	String memCode = tableparm.getValue("MEM_CODE",selectedIndx);//会员种类
    	String deptCode = tableparm.getValue("DEPT_CODE",selectedIndx);
    	String discountRate = tableparm.getValue("DISCOUNT_RATE",selectedIndx);
    	String py1 = tableparm.getValue("PY1",selectedIndx);
    	String descript = tableparm.getValue("DESCRIPT",selectedIndx);
    	String useFlg = tableparm.getValue("USE_FLG",selectedIndx);
    	String seq = tableparm.getValue("SEQ",selectedIndx);
    	String overdraft = tableparm.getValue("OVERDRAFT",selectedIndx);
    	String mroCtz = tableparm.getValue("MRO_CTZ",selectedIndx);
    	if("Y".equals(useFlg)){
    		typeUse.setSelected(true);
    	}else{
    		typeUse.setSelected(false);
    	}
    	ifUseFlg();//判断是否启用状态
    	String startDate = tableparm.getValue("START_DATE",selectedIndx);
    	if(startDate.length()>0){
    		startDate = startDate.replaceAll("-", "/").substring(0, 10);
    	}
    	String endDate = tableparm.getValue("END_DATE",selectedIndx);
    	if(endDate.length()>0){
    		endDate = endDate.replaceAll("-", "/").substring(0, 10);
    	}
    	String ctzDeptFlg = tableparm.getValue("CTZ_DEPT_FLG",selectedIndx);
    	if("Y".equals(ctzDeptFlg)){
    		typeCtzDept.setSelected(true);
    	}else{
    		typeCtzDept.setSelected(false);
    	}
    	String mainCtzFlg = tableparm.getValue("MAIN_CTZ_FLG",selectedIndx);
    	if("Y".equals(mainCtzFlg)){
    		typeMainCtz.setSelected(true);
    	}else{
    		typeMainCtz.setSelected(false);
    	}
    	String nhiCtzFlg = tableparm.getValue("NHI_CTZ_FLG",selectedIndx);
    	if("Y".equals(nhiCtzFlg)){
    		typeNhiCtz.setSelected(true);
    	}else{
    		typeNhiCtz.setSelected(false);
    	}
    	String mrctzUpdFlg = tableparm.getValue("MRCTZ_UPD_FLG",selectedIndx);
    	if("Y".equals(mrctzUpdFlg)){
    		typeMrctzUpd.setSelected(true);
    	}else{
    		typeMrctzUpd.setSelected(false);
    	}
    	String flgO = tableparm.getValue("OPD_FIT_FLG",selectedIndx);
    	if("Y".equals(flgO)){
    		typeO.setSelected(true);
    	}else{
    		typeO.setSelected(false);
    	}
    	String flgE = tableparm.getValue("EMG_FIT_FLG",selectedIndx);
    	if("Y".equals(flgE)){
    		typeE.setSelected(true);
    	}else{
    		typeE.setSelected(false);
    	}
    	String flgI = tableparm.getValue("IPD_FIT_FLG",selectedIndx);
    	if("Y".equals(flgI)){
    		typeI.setSelected(true);
    	}else{
    		typeI.setSelected(false);
    	}
    	String flgH = tableparm.getValue("HRM_FIT_FLG",selectedIndx);
    	if("Y".equals(flgH)){
    		typeH.setSelected(true);
    	}else{
    		typeH.setSelected(false);
    	}
    	
    	this.setValue("CTZ_CODE", ctzCode);
    	this.setValue("CTZ_DESC", ctzDesc);
    	this.setValue("MEM_TYPE", memType);
    	this.setValue("MEM_CODE", memCode);
    	this.setValue("DEPT_CODE", deptCode);
    	this.setValue("DISCOUNT", discountRate);
    	this.setValue("PY1", py1);
    	this.setValue("DESCRIPT", descript);
    	this.setValue("START_DATE", startDate);
    	this.setValue("END_DATE", endDate);
    	this.setValue("SEQ", seq);
    	this.setValue("OVERDRAFT", overdraft);
    	this.setValue("MRO_CTZ", mroCtz);
    	
    	this.setValue("CTZ_CODE3", ctzCode);
    	this.setValue("DEPT_CODE3", deptCode);
    	
    	rate = Double.parseDouble(discountRate);//全局设置
    	
    	ctzCodeOper = ctzCode;
    	queryDetailData(ctzCode);
    	
    		
    }
    /**
     * 明细表查询
     */
    public void queryDetailData(String ctzCode) {
    	String sql ="SELECT 'Y' AS EXEC,B.DEPT_CODE,B.CTZ_DESC,A.CTZ_CODE,A.CHARGE_HOSP_CODE," +
		" A.DISCOUNT_RATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM " +
		" FROM SYS_CHARGE_DETAIL A,sys_ctz B " +
		" WHERE A.CTZ_CODE = B.CTZ_CODE AND A.CTZ_CODE = '"+ctzCode+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	//System.out.println("sql="+sql);
    	//System.out.println("result="+result);
    	mainTable.setParmValue(result);
    	//mainTable.acceptText();
    	//-------------明细列表-----------------
    	String sql2 = "SELECT 'Y' AS EXEC, B.DEPT_CODE,B.CTZ_DESC,A.CTZ_CODE,A.ORDER_CODE," +
    			" A.ORDER_DESC,A.DISCOUNT_RATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,ORDERSET_CODE,HIDE_FLG " +
    			" FROM SYS_CTZ_ORDER_DETAIL A,SYS_CTZ B " +
    			" WHERE A.CTZ_CODE = B.CTZ_CODE AND A.HIDE_FLG = 'N' AND A.CTZ_CODE = '"+ctzCode+"'";
    	TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    	//detailTable.removeRowAll();
    	detailTable.setParmValue(result2);
    	//System.out.println("result2="+result2);
    	insertRow("DETILTABLE");//插入一行空数据
    }
    
    /**
     * 查询操作
     */
    public void onQuery(){
    	boolean mainCtzFlg1 = false;//主身份是否点选
    	if (mainCtzFlg.isSelected()){
    		mainCtzFlg1 = true;
    	}
    	boolean qUseFlg1 = false;//启用时间是否点选
    	if(qUseFlg.isSelected()){
    		qUseFlg1 = true;
    	}
    	String sDate = "";
    	String eDate = "";
    	sDate = this.getValueString("S_DATE");
    	eDate = this.getValueString("E_DATE");
    	if(qUseFlg1){
    		//校验开始结束时间
    		if(sDate.length()>0){
    			sDate = sDate.replaceAll("-", "/").substring(0, 10) + " 00:00:00";
    		}
    		if(eDate.length()>0){
    			eDate = eDate.replaceAll("-", "/").substring(0, 10) + " 23:59:59";
    		}
    	}
    	String deptCode = this.getComboBox("DEPT_CODE1").getSelectedID();
    	String ctzCode = getValueString("CTZCODE");
    	
    	//System.out.println("mainCtzFlg1="+mainCtzFlg1+" deptCode="+deptCode+" ctzCode="+ctzCode);
    	String querySql = "SELECT A.* FROM SYS_CTZ A " +
    			" WHERE 1=1 ";
    	if(mainCtzFlg1){
    		querySql += " AND A.MAIN_CTZ_FLG = 'Y' ";
    	}
    	if(qUseFlg1){
    		querySql += " AND A.USE_FLG = 'Y' ";
    		if(sDate.length()>0 && eDate.length()>0){
    			querySql += " AND A.START_DATE BETWEEN TO_DATE('"+sDate+ "','yyyy/mm/dd hh24:mi:ss') " +
    					" AND TO_DATE('"+ eDate +"','yyyy/mm/dd hh24:mi:ss')";
    		}
    	}
    	if(deptCode.length()>0){
    		querySql += " AND A.DEPT_CODE = '"+deptCode+"' ";
    	}
    	if(ctzCode.length()>0){
    		querySql += " AND A.CTZ_CODE = '"+ctzCode+"' ";
    	}
    	querySql += " ORDER BY CTZ_CODE,SEQ ";
    	//System.out.println("查询sql="+querySql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(querySql));
    	if(result.getCount()<=0){
    		this.messageBox("查无数据！");
    		tableCtz.removeRowAll();
    		onClear();//清除操作
    		return;
    	}
    	tableCtz.setParmValue(result);
    	
    }
    
    /**
     * 主身份单选框点击事件
     */
    public void clickCTZMain() {
    	onQuery();
    }
    /**
     * 效期是否启用单选框点击事件
     */
    public void clickQUseFlg() {
    	if(qUseFlg.isSelected()){
    		//清空开始结束时间并置有效
    		this.setValue("S_DATE", "");
    		this.setValue("E_DATE", "");
    		callFunction("UI|S_DATE|setEnabled", true);
    		callFunction("UI|E_DATE|setEnabled", true);
    	}else{
    		//清空开始结束时间并置灰
    		this.setValue("S_DATE", "");
    		this.setValue("E_DATE", "");
    		callFunction("UI|S_DATE|setEnabled", false);
    		callFunction("UI|E_DATE|setEnabled", false);
    	}
    	onQuery();
    }
    
    /**
     * 保存操作
     */
    public void onSave(){
    	// 保存数据
		TParm update = new TParm();
		TParm result = new TParm();
		
    	TParm insertOrderParm = new TParm();
    	TParm insertOrderData = new TParm();
    	TParm delData = new TParm();
    	TParm updateParm = new TParm();
    	TParm updateData = new TParm();
    	TParm mainUpdateParm = new TParm();//分类主表
    	TParm mainUpdateData = new TParm();
    	//detailTable.acceptText();
    	detailTable.acceptText();
    	mainTable.acceptText();
    	TParm detailParm = detailTable.getParmValue();//明细表
    	TParm mainParm = mainTable.getParmValue();    //分类主表
    	//System.out.println("--detailParm.getCount()="+detailParm.getCount("ORDER_CODE"));
    	if(mainParm.getCount()>0){//先判断分类主表是否有数据
    		for (int m = 0; m < mainParm.getCount(); m++){
    			String exec = mainParm.getValue("EXEC", m);
    			if("U".equals(exec)){//1：修改数据-分类主表
    				mainUpdateParm.addData("CTZ_CODE", this.getValue("CTZ_CODE3"));
    				mainUpdateParm.addData("CHARGE_HOSP_CODE", mainTable.getItemString(m, "CHARGE_HOSP_CODE"));
    				mainUpdateParm.addData("DISCOUNT_RATE", mainTable.getItemString(m, "DISCOUNT_RATE"));
    				mainUpdateParm.addData("OPT_USER", Operator.getID());
    				mainUpdateParm.addData("OPT_TERM", Operator.getIP());
    			}
    		}
    		//循环遍历分类主表修改数据
    		for (int k = 0; k < mainUpdateParm.getCount("CTZ_CODE"); k++) {
    			mainUpdateData.addRowData(mainUpdateParm, k);
    		}
    		//System.out.println("分类主表修改数据：--->"+mainUpdateData);
    		update.setData("MAINUPDATEDATA", mainUpdateData.getData());
    		
    		if(detailParm.getCount()>0){//2:再判断细项表是否有数据
        		for (int i = 0; i < detailParm.getCount(); i++) {
        			String exec = detailParm.getValue("EXEC", i);
    				if("Y".equals(exec)){//老数据
    					
    				}else if("U".equals(exec)){//修改数据-明细表
    					updateParm.addData("CTZ_CODE", this.getValue("CTZ_CODE3"));
    					updateParm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE", i));
    					updateParm.addData("ORDER_DESC", detailTable.getItemString(i, "ORDER_DESC"));
    					updateParm.addData("DISCOUNT_RATE", detailTable.getItemString(i, "DISCOUNT_RATE"));
    					updateParm.addData("OPT_DATE", detailTable.getItemString(i, "OPT_DATE"));
    					updateParm.addData("OPT_USER", detailTable.getItemString(i, "OPT_USER"));
    					updateParm.addData("OPT_TERM", detailTable.getItemString(i, "OPT_TERM"));
    				}else{//新增
    					if(detailParm.getValue("ORDER_DESC", i).length()>0){
    						insertOrderParm.addData("CTZ_CODE", this.getValue("CTZ_CODE3"));
    						insertOrderParm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE", i));
    						insertOrderParm.addData("ORDER_DESC", detailTable.getItemString(i, "ORDER_DESC"));
    						insertOrderParm.addData("DISCOUNT_RATE", detailTable.getItemString(i, "DISCOUNT_RATE"));
    						insertOrderParm.addData("OPT_DATE", detailTable.getItemString(i, "OPT_DATE"));
    						insertOrderParm.addData("OPT_USER", detailTable.getItemString(i, "OPT_USER"));
    						insertOrderParm.addData("OPT_TERM", detailTable.getItemString(i, "OPT_TERM"));
    						insertOrderParm.addData("ORDERSET_CODE", detailParm.getValue("ORDER_CODE", i));
    						insertOrderParm.addData("HIDE_FLG", "N");
    						//判断是集合医嘱主项的设置setmain_flg
    						TParm mainParm2 = getDetailOrderSql(detailParm.getValue("ORDER_CODE", i));
    						if(mainParm2.getCount()>0){
    							insertOrderParm.addData("SETMAIN_FLG", "Y");
    						}
    						
    						
    					}
    				
    				}
        		}
        		//循环遍历医嘱表新增数据
        		//System.out.println("新增：insertOrderParm--》"+insertOrderParm);
            	for (int j = 0; j < insertOrderParm.getCount("ORDER_DESC"); j++) {
            		
            		insertOrderData.addRowData(insertOrderParm, j);
        		}
            	TParm orderDetailParm = new TParm();//细项
            	orderDetailParm = getOrderDetail(insertOrderData);
            	//System.out.println("orderDetailParm="+orderDetailParm);
            	
            	for (int p = 0; p < orderDetailParm.getCount("ORDER_CODE"); p++) {
            		insertOrderData.addRowData(orderDetailParm, p);
				}
            	
            	
            	//循环遍历医嘱表删除数据
            	for (int k = 0; k < delParm.getCount("CTZ_CODE"); k++) {
            		delData.addRowData(delParm, k);
        		}
            	//循环遍历医嘱表修改数据
            	for (int m = 0; m < updateParm.getCount("ORDER_CODE"); m++) {
            		updateData.addRowData(updateParm, m);
        		}
            	update.setData("INSERTDATA", insertOrderData.getData());
            	update.setData("DELDATA", delData.getData());
            	update.setData("UPDATEDATA", updateData.getData());
            	//System.out.println("---update:"+update);
            	
        	}
//        	result = MEMCtzTool.getInstance().onSave(update);
        	result = TIOM_AppServer.executeAction("action.mem.MEMCtzAction",
    				"onSave", update);
    		if(result.getErrCode()<0){
    			this.messageBox("保存失败！");
    			return;
    		}
    		
    	}
    	
    	
    	
    	
    	//-----------------------------------------
    	String oper = "UPDATE";
    	
    	TTextField ctzCode = (TTextField)this.getComponent("CTZ_CODE");
    	boolean flag = ctzCode.isEnabled();
    	if(flag){
    		oper = "ADD";
    	}
    	System.out.println("oper===="+oper);
    	//页面数据校验
    	if(checkData(oper)){
    		//TParm result = new TParm();
    		//获取页面数据
        	TParm ctzParm = getData();
        	ctzParm.setData("OPER", oper);
        	//System.out.println("ctzParm="+ctzParm);
        	if("ADD".equals(oper)){
        		result = MEMCtzTool.getInstance().onSaveMemCtzInfoData(ctzParm);
        		if(result.getErrCode()>=0){
        			//遍历循环插入SYS_CHARGE_DETAIL表
        			insertDataDetail(ctzParm);
        		}
        		
            	
        	}else if("UPDATE".equals(oper)){
        		result = MEMCtzTool.getInstance().updateMemCtzInfoData(ctzParm);
        		if (result.getErrCode() < 0) {
        			this.messageBox("更新失败！" + result.getErrName()
        							+ result.getErrText());
        			return;
        		}
        		//修改折扣率-判断当前折扣率是否等于全局折扣率
        		double currRate = this.getValueDouble("DISCOUNT");
        		if(currRate!=rate){
        			result = MEMCtzTool.getInstance().updateDiscountRate(ctzParm);
        		}
        		
        		deptDataSame();
        	}
        	
        	if (result.getErrCode() < 0) {
    			this.messageBox("更新失败！" + result.getErrName()
    							+ result.getErrText());
    			return;
    		}
        	this.messageBox("保存成功！");
        	initData(); 
        	//身份代码状态置无效
        	callFunction("UI|CTZ_CODE|setEnabled", false);
        	//清除delParm
        	delParm.removeData("CTZ_CODE");
        	delParm.removeData("ORDER_CODE");
        	//初始化
        	queryDetailData(ctzCodeOper);
        	
    	}
    	
    	
    	
    }
    /**
     * 删除操作
     */
    public void onDelete(){
    	TParm result = new TParm();
    	TParm parm = new TParm();
    	int selectedIndx=tableCtz.getSelectedRow();
    	if(selectedIndx<0){
    		this.messageBox("没有选中删除数据！");
    		return;
    	}
    	int detailIndx = detailTable.getSelectedRow();
    	if(detailIndx>=0){
    		//将删除的数据放到delParm中
    		String ctzCode = this.getValueString("CTZ_CODE3");
    		String orderCode = detailTable.getItemString(detailIndx, "ORDER_CODE");
    		delParm.addData("CTZ_CODE", ctzCode);
    		delParm.addData("ORDER_CODE", orderCode);
    		detailTable.removeRow(detailIndx);
    	}else{
    		String ctzCode = tableCtz.getItemString(selectedIndx, "CTZ_CODE");
        	
        	//检查是否可以删除
        	if(checkDelCtz(ctzCode)){
        		if (JOptionPane.showConfirmDialog(null, "是否删除选中数据？", "信息",
        				JOptionPane.YES_NO_OPTION) == 0) {
        			parm.setData("CTZ_CODE", ctzCode);
        			result = MEMCtzTool.getInstance().delCtzCode(parm);
        			if(result.getErrCode()<0){
        	    		this.messageBox("删除失败！");
        	    	}else{
        	    		this.messageBox("删除成功！");
        	    		onInit();
        	    		onClear();
        	    	}
        		}

        	}else{
        		this.messageBox("项目分类/明细表有数据，不能删除！");
        		return;
        	}
    	}
    	
    	
    }
    /**
     * 清除操作
     */
    public void onClear(){
    	//身份状态置有效
    	callFunction("UI|CTZ_CODE|setEnabled", true);
    	callFunction("UI|START_DATE|setEnabled", false);
    	callFunction("UI|END_DATE|setEnabled", false);
    	this.clearValue("CTZ_CODE;CTZ_DESC;MEM_TYPE;MEM_CODE;DEPT_CODE;DISCOUNT;PY1;SEQ;DESCRIPT;" +
    			"START_DATE;END_DATE;OVERDRAFT;MRO_CTZ"+/*add by sunqy 20140523*/";CTZCODE;DEPT_CODE1");
    	//折扣默认为1,不打折
    	this.setValue("DISCOUNT", "1.0000");
    	//单选框设置
    	typeUse.setSelected(false);			//是否启用
    	typeCtzDept.setSelected(false);		//是否判断科室
    	typeMainCtz.setSelected(false);		//主身份
    	typeNhiCtz.setSelected(false);		//医保身份
    	typeMrctzUpd.setSelected(false);	//更新病例
    	typeO.setSelected(false);			//门诊适用
    	typeE.setSelected(false);			//急诊适用
    	typeI.setSelected(false);			//住院适用
    	typeH.setSelected(false);			//健检适用
    	
    	detailTable.removeRowAll();
    	mainTable.removeRowAll();
    	
    	initData();
    }
    /**
     * 页面数据校验
     */
    public boolean checkData(String oper){
    	boolean flag = true;
    	TParm result = new TParm();
    	TParm parm = new TParm();
    	if("ADD".equals(oper)){//新增数据需校验以下数据
    		if(this.getValueString("CTZ_CODE").length()==0){
        		this.messageBox("身份代码不能为空！");
        		return flag = false;
        	}else{
        		//校验输入身份代码的是否可用
        		parm.setData("CTZ_CODE", this.getValueString("CTZ_CODE"));
        		result = MEMCtzTool.getInstance().checkCtzCode(parm);
        	}
        	if(result.getCount()>0){
        		this.messageBox("该身份代码已存在！");
        		return flag = false;
        	}
    	}
    	
    	//折扣不能大于1
    	double discount = this.getValueDouble("DISCOUNT");
    	if(discount>1){
    		this.messageBox("折扣率不能大于1");
    		this.grabFocus("DISCOUNT");
    		return flag = false;
    	}
    	
    	//当科室选框选中时，检验科室下拉框不能为空
    	if(typeCtzDept.isSelected()){
    		String deptCode = this.getComboBox("DEPT_CODE").getSelectedID(); 
    		if(deptCode.length()<=0){
    			this.messageBox("科室选框选中时科室下拉框不能为空！");
    			this.grabFocus("DEPT_CODE");
        		return flag = false;
    		}
    	}
    	
    	//透支额度不能为负
    	String overdraft = this.getValueString("OVERDRAFT");
    	double fee = Double.parseDouble(overdraft);
    	if(fee<0){
    		this.messageBox("透支额度不能为负！");
    		this.grabFocus("OVERDRAFT");
    		return flag = false;
    	}
    	
    	return flag;
    	
    }
    /**
     * 检查身份表是否可以删除
     */
    public boolean checkDelCtz(String ctzCode){
    	boolean flag = true;
    	TParm result1 = new TParm();
    	TParm result2 = new TParm();
    	TParm parm = new TParm();
    	parm.setData("CTZ_CODE", ctzCode);
    	//项目分类
    	result1 = MEMCtzTool.getInstance().checkDelCtz(parm);
    	//System.out.println("result1.getCount()="+result1.getCount());
    	if(result1.getCount()>0){//项目分类表有数据不能删除
    		return flag = false;
    	}
    	//项目明细
    	result2 = MEMCtzTool.getInstance().checkFeeCtzCode(parm);
    	//System.out.println("result2.getCount()="+result2.getCount());
    	if(result2.getCount()>0){//项目明细表有数据不能删除
    		return flag = false;
    	}
    	return flag;
    }
    /**
     * 获取页面数据
     */
    public TParm getData(){
    	TParm parm = new TParm();
    	//获取页面数据
    	String ctzCode1 = this.getValueString("CTZ_CODE");
    	String ctzDesc = this.getValueString("CTZ_DESC");
    	String memType = this.getValueString("MEM_TYPE");
    	String memCode = this.getValueString("MEM_CODE");
    	String deptCode = this.getValueString("DEPT_CODE");
    	String discount = this.getValueString("DISCOUNT");
    	String py1 = this.getValueString("PY1");
    	String seq = this.getValueString("SEQ");
    	String overdraft = this.getValueString("OVERDRAFT");
    	String descript = this.getValueString("DESCRIPT");
    	String startDate = this.getValueString("START_DATE");
    	String endDate = this.getValueString("END_DATE");  	
    	String mroCtz = this.getValueString("MRO_CTZ"); 
    	
    	String typeCtzDept1 = "";//是否判断科室
    	if(typeCtzDept.isSelected()){
    		typeCtzDept1 = "Y";
    	}else{
    		typeCtzDept1 = "N";
    	}
    	String typeMainCtz1 = "";//主身份
    	if(typeMainCtz.isSelected()){
    		typeMainCtz1 = "Y";
    	}else{
    		typeMainCtz1 = "N";
    	}
    	String typeNhiCtz1 = "";//医保身份
    	if(typeNhiCtz.isSelected()){
    		typeNhiCtz1 = "Y";
    	}else{
    		typeNhiCtz1 = "N";
    	}
    	String typeMrctzUpd1 = "";//更新病例
    	if(typeMrctzUpd.isSelected()){
    		typeMrctzUpd1 = "Y";
    	}else{
    		typeMrctzUpd1 = "N";
    	}
    	String typeO1 = "";//门诊适用
    	if(typeO.isSelected()){
    		typeO1 = "Y";
    	}else{
    		typeO1 = "N";
    	}
    	String typeE1 = "";//急诊适用
    	if(typeE.isSelected()){
    		typeE1 = "Y";
    	}else{
    		typeE1 = "N";
    	}
    	String typeI1 = "";//住院适用
    	if(typeI.isSelected()){
    		typeI1 = "Y";
    	}else{
    		typeI1 = "N";
    	}
    	String typeH1 = "";//健检适用
    	if(typeH.isSelected()){
    		typeH1 = "Y";
    	}else{
    		typeH1 = "N";
    	}
    	String typeUse1 = "";//是否启用
    	if(typeUse.isSelected()){
    		typeUse1 = "Y";
    	}else{
    		typeUse1 = "N";
    	}
    	parm.setData("CTZ_CODE", ctzCode1);
    	parm.setData("CTZ_DESC", ctzDesc);
    	parm.setData("MEM_TYPE", memType);
    	parm.setData("MEM_CODE", memCode);
    	parm.setData("DEPT_CODE", deptCode);
    	parm.setData("DISCOUNT_RATE", discount);
    	parm.setData("PY1", py1);
    	parm.setData("SEQ", seq);
    	parm.setData("OVERDRAFT", overdraft);
    	parm.setData("DESCRIPT", descript);
    	parm.setData("MRO_CTZ", mroCtz);
    	if(startDate.length()>0){
    		parm.setData("START_DATE", startDate.replaceAll("-", "/").substring(0, 10));
    	}else{
    		parm.setData("START_DATE", "");
    	}
    	if(endDate.length()>0){
    		parm.setData("END_DATE", endDate.replaceAll("-", "/").substring(0, 10));
    	}else{
    		parm.setData("END_DATE", "");
    	}
    	
    	parm.setData("CTZ_DEPT_FLG", typeCtzDept1);
    	parm.setData("MAIN_CTZ_FLG", typeMainCtz1);
    	parm.setData("NHI_CTZ_FLG", typeNhiCtz1);
    	parm.setData("MRCTZ_UPD_FLG", typeMrctzUpd1);
    	parm.setData("OPD_FIT_FLG", typeO1);
    	parm.setData("EMG_FIT_FLG", typeE1);
    	parm.setData("IPD_FIT_FLG", typeI1);
    	parm.setData("HRM_FIT_FLG", typeH1);
    	parm.setData("USE_FLG", typeUse1);
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("OPT_TERM", Operator.getIP());
    	
    	
    	return parm;
    }
    /**
     * 是否勾选是否启用选框
     */
    public void ifUseFlg(){
    	if(typeUse.isSelected()){
    		//this.messageBox("已选中");
    		//启用时间置有效
    		callFunction("UI|START_DATE|setEnabled", true);
    		callFunction("UI|END_DATE|setEnabled", true);
    	}else{
    		//this.messageBox("未选");
    		//启用时间置无效
    		callFunction("UI|START_DATE|setEnabled", false);
    		callFunction("UI|END_DATE|setEnabled", false);
    	}
    }
    /**
     * 插入一行数据-项目明细折扣列表
     */
    public void insertRow(String opertable) {
		TTable operTable = (TTable) callFunction("UI|"+opertable+"|getThis");
		operTable.acceptText();
		//int oldrow = table.getRowCount() - 1;
		int row = operTable.addRow();
		//operTable.setItem(row, "CTZ_CODE", ctzCode);
		operTable.setItem(row, "DISCOUNT_RATE", "1.0000");
		operTable.setItem(row, "OPT_USER", Operator.getID());
		Timestamp date = StringTool.getTimestamp(new Date());
		operTable.setItem(row, "OPT_DATE", date);
		operTable.setItem(row, "OPT_TERM", Operator.getIP());
    	
    	
		
	}
    
    /**
     * 项目折扣分类主表监听事件
     */
    public void onMainTableChangeValue(){
    	//修改老数据
		TParm mainParm=mainTable.getParmValue();
		String exec = mainParm.getValue("EXEC",mainTable.getSelectedRow());
		if("Y".equals(exec)){
			mainParm.setData("EXEC", mainTable.getSelectedRow(), "U");
			mainTable.setParmValue(mainParm);
		}
    }
    
    /**
     * 项目明细表监听事件
     */
    public void onTableChangeValue(){
    	int selectedIndx=tableCtz.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
//    	TParm tableparm=tableCtz.getParmValue();
//    	String ctzCode = tableparm.getValue("CTZ_CODE",selectedIndx);
//    	String deptCode = tableparm.getValue("DEPT_CODE",selectedIndx);
    	detailTable.acceptText();
		// 添加一行新数据
		int oldrow = detailTable.getRowCount() - 1;
		if (detailTable.getSelectedRow() == oldrow) {
			String orderDesc = detailTable.getItemString(oldrow, "ORDER_DESC");
			if(orderDesc.length()>0){
				insertRow("DETILTABLE");
				//String orderIdNo = SystemTool.getInstance().getNo("ALL", "SYS", "PACKAGESECTIOND", "PACKAGESECTIOND");
//				detailTable.setItem(oldrow, 2, String.valueOf(ctzCode));//设置类型
//				detailTable.setItem(oldrow, 3, String.valueOf(deptCode));//设置科室
				
			}
		}
		//修改老数据
		TParm detailparm=detailTable.getParmValue();
		String exec = detailparm.getValue("EXEC",detailTable.getSelectedRow());
		if("Y".equals(exec)){
			detailparm.setData("EXEC", detailTable.getSelectedRow(), "U");
			detailTable.setParmValue(detailparm);
		}
    }
    
    /**
     * 右击MENU弹出事件
     * @param tableName
     */
    public void showPopMenu() {
    	detailTable.acceptText();
    	//TParm orderParm=detailTable.getParmValue();
    	//String exec = orderParm.getValue("EXEC",detailTable.getSelectedRow());
    	//String setmainFlg = orderParm.getValue("SETMAIN_FLG",detailTable.getSelectedRow());
    	detailTable.setPopupMenuSyntax("显示集合医嘱细项,onOrderSetShow");
    	
    }
    /**
     * 查询显示医嘱细项TABLE右击事件，调出细项列表
     */
    public void onOrderSetShow() {
    	TParm parm = new TParm();
    	TParm orderparm=detailTable.getParmValue();
        int row = detailTable.getSelectedRow();
        if (row < 0) {
            return;
        }
    	//String orderCode = orderTable.getItemString(row, "ORDER_CODE");
    	String orderCode = orderparm.getValue("ORDER_CODE", row);
    	parm.setData("ORDERSET_CODE", orderCode);
    	Object obj = this.openDialog("%ROOT%\\config\\mem\\MEMCTZOrderSetShow.x", parm);
    }
    
    /**
     * 循环遍历插入表SYS_CHARGE_DETAIL
     */
    public void insertDataDetail(TParm ctzParm){
    	TParm result = new TParm();
    	for (int i = 0; i < allCode.getCount(); i++) {
    		String hospChargeCode = allCode.getValue("CHARGE_HOSP_CODE", i);
    		String sql = "insert into SYS_CHARGE_DETAIL(ctz_code,charge_hosp_code,discount_rate," +
			"opt_user,opt_date,opt_term) values('"+ctzParm.getValue("CTZ_CODE")+"'," +
					"'"+hospChargeCode+"','"+ctzParm.getValue("DISCOUNT_RATE")+"','"+Operator.getID()+"',sysdate,'"+Operator.getIP()+"')";
    		//System.out.println("===i-->"+i+" sql="+sql);
    		result = new TParm(TJDODBTool.getInstance().update(sql));
		}
    	
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
	 * 得到页面中下拉框对象
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
	
	/**
	 * 科室数据同步
	 */
	public void deptDataSame() {
		setValue("DEPT_CODE3", this.getValue("DEPT_CODE"));
	}
	
	/**
	 * 查询集合医嘱-细项添加到result中
	 */
	public TParm getOrderDetail(TParm insertOrderParm){
		TParm parm = new TParm();
		TParm result = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		int num = 0;
		//System.out.println("insertOrderParm.getCount()="+insertOrderParm.getCount());
		for (int i = 0; i < insertOrderParm.getCount(); i++) {
//			String sql = "SELECT * FROM SYS_ORDERSETDETAIL WHERE HIDE_FLG = 'Y' AND " +
//					" ORDERSET_CODE = '"+insertOrderParm.getValue("ORDER_CODE")+"' ";
//			parm = new TParm(TJDODBTool.getInstance().select(sql));
			parm = getDetailOrderSql(insertOrderParm.getValue("ORDER_CODE", i));
			//System.out.println("i="+i+" parm="+parm);
			if(parm.getCount()>0){
				for (int j = 0; j < parm.getCount(); j++) {
					result.setData("ORDERSET_CODE", num, parm.getValue("ORDERSET_CODE", j));
					result.setData("OPT_USER", num, Operator.getID());
					result.setData("HIDE_FLG", num, "Y");
					result.setData("ORDER_CODE", num, parm.getValue("ORDER_CODE", j));
					result.setData("OPT_TERM", num, Operator.getIP());
					result.setData("ORDER_DESC", num, parm.getValue("ORDER_DESC", j));
					result.setData("OPT_DATE", date);
					result.setData("DISCOUNT_RATE", num, insertOrderParm.getValue("DISCOUNT_RATE", i));
					result.setData("CTZ_CODE", num, insertOrderParm.getValue("CTZ_CODE", i));
					result.setData("SETMAIN_FLG", num, "N");
					num++;
				}
			}
		}
		//System.out.println("查询的所有细项："+result);
		return result;
	}
	
	/**
	 * 获取集合医嘱明细sql
	 */
	public TParm getDetailOrderSql(String order) {
		TParm result = new TParm();
		String sql = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION, DOSAGE_QTY," +
				" UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY " +
				" AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE " +
				" FROM SYS_FEE A, SYS_ORDERSETDETAIL B " +
				" WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' " +
				" AND B.ORDERSET_CODE = '"+order+"'";
		//System.out.println("sql====>"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
     * 根据折扣计算实收金额
     */
    public void queryRate(){
    	//校验折扣不能大于1
    	double rate = this.getValueDouble("DISCOUNT");
    	if(rate>1){
    		this.messageBox("折扣率不能大于1！");
    		this.setValue("DISCOUNT", "1.0000");
    		return;
    	}
    }
    



}
