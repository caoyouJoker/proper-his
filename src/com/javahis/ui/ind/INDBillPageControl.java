package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jdo.ind.INDBillPageTool;
import jdo.ind.INDSQL;
import jdo.sys.Operator;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
/**
 *
 * <strong>Title : INDBillPageControl<br></strong>
 * <strong>Description : </strong>药库帐页<br>
 * <strong>Create on : 2012-1-25<br></strong>
 * <p>
 * <strong>Copyright (C) <br></strong>
 * <p>
 * @author luhai<br>
 * @version <strong>ProperSoft</strong><br>
 * <br>
 * <strong>修改历史:</strong><br>
 * 修改人		修改日期		修改描述<br>
 * -------------------------------------------<br>
 * <br>
 * <br>
 */
public class INDBillPageControl extends TControl {
	//页面控件
	private TComboBox orgCombo;
	private TTextFormat startDate;
	private TTextFormat endDate;
	private TTable medTable;
	private TTable billTable;
	private TTextField orderCode;
	private TTextField orderDesc;
	private TTextField orderDescription;
	private TTextField orderUnitDesc;
	//日期格式化
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	//regionCode
	private String regionCode=Operator.getRegion();
	//帐页数据
	private List<BillPageBean> billPageList;
	
	private final static String LAST_AMT_DESC = "上年结存";
	private final static String CURRENT_AMT_DESC = "本月结存";
    /**
     * 初始化方法
     */
    public void onInit() {
//    	System.out.println("系统初始化");
    	initComponent();
    	initPage();
//    	System.out.println("系统初始化完毕");
    }
    /**
     *
     * 初始化控件默认值
     */
    public void initPage(){
    	//设置科室下拉框值begin
//    	this.orderCode.setse
        TParm parmCbo = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgComobo("='A'","",Operator.getRegion())));
        this.orgCombo.setParmValue(parmCbo);
    	//设置科室下拉框值end
		// 重新处理开始时间和结束时间 begin luhai 2011-12-07
		Calendar cd = Calendar.getInstance();
		Calendar cdto = Calendar.getInstance();
		cd.add(Calendar.MONTH, -1);
		cd.set(Calendar.DAY_OF_MONTH, 26);
		cdto.set(Calendar.DAY_OF_MONTH, 25);
		String format = formateDate.format(cd.getTime());
		this.startDate.setValue(formateDate.format(cd.getTime()));
		this.endDate.setValue(formateDate.format(cdto.getTime()));
		// 重新处理开始时间和结束时间 begin luhai 2011-12-07
		//初始化默认药库
		orgCombo.setSelectedIndex(1);
		//初始化药品列表的数据 begin
		initMedTable(orgCombo.getValue(),this.regionCode);
		//初始化药品列表的数据 end
		//初始化医嘱的textField
        //只有text有这个方法，调用sys_fee弹出框
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 1);
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x",parm);

        //textfield接受回传值
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }
    public void popReturn(String tag, Object obj) {
        TParm parmrtn = (TParm) obj;
        this.setValue("ORDER_CODE", parmrtn.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", parmrtn.getValue("ORDER_DESC"));
        //带入药品的相关信息
    	TParm parm = new TParm();
    	parm.setData("ORDER_CODE",parmrtn.getValue("ORDER_CODE"));
    	parm.setData("ORG_CODE",this.orgCombo.getValue());
    	parm.setData("REGION_CODE",regionCode);
    	TParm selectSysFeeMed = INDBillPageTool.getInstance().selectSysFeeMed(parm);
    	if(selectSysFeeMed.getCount("ORDER_CODE")<=0){
    		return;
    	}
    	String orderCode = selectSysFeeMed.getValue("ORDER_CODE",0);
    	String orderDesc=selectSysFeeMed.getValue("ORDER_DESC",0);
    	String orderDescription=selectSysFeeMed.getValue("SPECIFICATION",0);
    	String orderUnitDesc=selectSysFeeMed.getValue("UNIT_CHN_DESC",0);
    	this.orderCode.setValue(orderCode);
    	this.orderDesc.setValue(orderDesc);
    	this.orderDescription.setValue(orderDescription);
    	this.orderUnitDesc.setValue(orderUnitDesc);

    }
    public void queryMedList(){
    	String orgValue = orgCombo.getValue();
    	if("".equals(orgValue)){
    		this.messageBox("请选择查询药库！");
    		return ;
    	}
		//初始化药品列表的数据 begin
		initMedTable(orgCombo.getValue(),this.regionCode);
		//初始化药品列表的数据 end
    }
    /**
     *
     * 查询帐页的详细信息
     * @throws ParseException
     */
    public void onQuery() throws ParseException{
//    	StringBuffer sqlbf = new StringBuffer("");
//    	sqlbf.append(" select ORDER_CODE from  sys_fee where  (order_code like 'Y01%'  OR order_code like 'Y02%' or order_code like 'Y03%')  ");
//    	Map select = TJDODBTool.getInstance().select(sqlbf.toString());
//    	TParm resultsorce=new TParm(select);
//    	for(int i=0;i<resultsorce.getCount("ORDER_CODE");i++){
//    		String orderCode=resultsorce.getValue("ORDER_CODE",i);
//    		StringBuffer sqlbforder=new StringBuffer();
//    		sqlbforder.append(" SELECT B.CHARGE_HOSP_CODE FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND  ORDERSET_CODE='"+orderCode+"' ");
//    		Map selectcharge = TJDODBTool.getInstance().select(sqlbforder.toString());
//    		TParm chargeCodeParm = new TParm(selectcharge);
//    		String chargeCode=chargeCodeParm.getValue("CHARGE_HOSP_CODE",0);
//    		System.out.println("chargeCode:"+chargeCode);
//    		StringBuffer updateSql=new StringBuffer();
//    		updateSql.append(" update sys_fee set CHARGE_HOSP_CODE='"+chargeCode+"' where order_code='"+orderCode+"'");
//    		System.out.println(updateSql);
//    		Map update = TJDODBTool.getInstance().update(updateSql.toString());
//    		TParm result = new TParm (update);
//    		if(result.getErrCode()<=0){
//    			System.out.println("error"+result.getErrText());
//    		}else {
//    			System.out.println("00000000ok");
//    		}
//    		updateSql.delete(0, updateSql.toString().length());
//    		updateSql.append(" update sys_fee_history set CHARGE_HOSP_CODE='"+chargeCode+"' where order_code='"+orderCode+"'");
//    		System.out.println(updateSql);
//    		update = TJDODBTool.getInstance().update(updateSql.toString());
//    		result = new TParm (update);
//    		if(result.getErrCode()<=0){
//    			System.out.println("error"+result.getErrText());
//    		}else {
//    			System.out.println("00000000ok");
//    		}
//    	}
    	String orgCode=this.orgCombo.getValue();
    	String orderCode=this.orderCode.getValue();
    	if("".equals(orgCode)){
    		this.messageBox("请选择查询部门！");
    		return;
    	}
    	if("".equals(orderCode)){
    		this.messageBox("请输入查询药品！");
    		return;
    	}
    	if("".equals(startDate.getValue())){
    		this.messageBox("请输入开始时间！");
    		return;
    	}
    	if("".equals(endDate.getValue())){
    		this.messageBox("请输入结束时间！");
    		return;
    	}
    	initBillDetailTable(orgCode,orderCode,this.regionCode);
    }
    /**
     *
     * 药品列表click事件
     */
    public void onMedTableClick(){
    	int selectedIndx=this.medTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	TParm tableparm=this.medTable.getParmValue();
    	//在页面上带出药品的相关属性
    	String orderCode = tableparm.getValue("ORDER_CODE",selectedIndx);
    	String orderDesc=tableparm.getValue("ORDER_DESC",selectedIndx);
    	String orderDescription=tableparm.getValue("SPECIFICATION",selectedIndx);
    	String orderUnitDesc=tableparm.getValue("UNIT_CHN_DESC",selectedIndx);
    	this.orderCode.setValue(orderCode);
    	this.orderDesc.setValue(orderDesc);
    	this.orderDescription.setValue(orderDescription);
    	this.orderUnitDesc.setValue(orderUnitDesc);
    }
    /**
     * 打印
     */
    public void onPrint() {
        if (this.billTable.getRowCount() <= 0) {
            this.messageBox("没有要打印的数据");
            return;
        }
        TParm prtParm = new TParm();
        //表头
        prtParm.setData("TITLE", "TEXT",Manager.getOrganization().
                getHospitalCHNFullName(Operator.getRegion()) +
                        "药库帐页");
        String startDate = this.startDate.getValue().toString();
        String endDate = this.endDate.getValue().toString();
            startDate = startDate.substring(0, 10).replace("-", "/");
            endDate = endDate.substring(0, 10).replace("-", "/");
        prtParm.setData("START_DATE", "TEXT",
                        startDate);
        prtParm.setData("END_DATE", "TEXT", endDate);
        prtParm.setData("UNIT", "TEXT", this.orderUnitDesc.getValue());
        prtParm.setData("MED_NAME", "TEXT", this.orderDesc.getValue());
        prtParm.setData("TYPE", "TEXT", this.orderDescription.getValue());
        prtParm.setData("UNIT", "TEXT", this.orderUnitDesc.getValue());
        TParm tableparm = this.billTable.getParmValue();
        tableparm.setCount(tableparm.getCount("DESC"));
        //设置总行数
        tableparm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
        tableparm.addData("SYSTEM", "COLUMNS", "BILL_NO");
        tableparm.addData("SYSTEM", "COLUMNS", "DESC");
        tableparm.addData("SYSTEM", "COLUMNS", "IN_NUM");
        tableparm.addData("SYSTEM", "COLUMNS", "IN_PRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "IN_AMT");
        tableparm.addData("SYSTEM", "COLUMNS", "OUT_NUM");
        tableparm.addData("SYSTEM", "COLUMNS", "OUT_PRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "OUT_AMT");
        tableparm.addData("SYSTEM", "COLUMNS", "LAST_NUM");
        tableparm.addData("SYSTEM", "COLUMNS", "LAST_PRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "LAST_AMT");
        prtParm.setData("TABLE", tableparm.getData());
        //表尾
        prtParm.setData("USER", "TEXT", "制表人：" + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDBillPage.jhw",
                             prtParm);
    }
    public void onExport(){
        if (billTable.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(billTable,
            "药库帐页");

    }
    /**
     * 初始化帐页详细新
     *
     * @param orgCode
     * @param regionCode
     * @throws ParseException
     */
    private void initBillDetailTable(String orgCode,String orderCode,String regionCode) throws ParseException{
    	TParm parm = new TParm();
    	parm.setData("ORG_CODE",orgCode);
    	parm.setData("REGION_CODE",regionCode);
    	parm.setData("ORDER_CODE",orderCode);
    	parm.setData("START_DATE",(startDate.getValue()+"").substring(0,10).replace("-", "")+"000000");
    	parm.setData("END_DATE",(endDate.getValue()+"").substring(0,10).replace("-", "")+"235959");
    	this.billPageList=new ArrayList<INDBillPageControl.BillPageBean>();
    	//得到入库的parm
    	TParm dispenseINParm = INDBillPageTool.getInstance().selectDispenseIN(parm);
    	this.billPageList.addAll(getBillPageBeanList(dispenseINParm));
    	//得到出库的parm
    	TParm dispenseOUTParm = INDBillPageTool.getInstance().selectDispenseOUT(parm);
    	this.billPageList.addAll(getBillPageBeanList(dispenseOUTParm));
    	//得到验收的parm
    	TParm verifyinParm=INDBillPageTool.getInstance().selectVerifyin(parm);
    	this.billPageList.addAll(getBillPageBeanList(verifyinParm));
    	//得到退货的Parm
    	TParm regressParm=INDBillPageTool.getInstance().selectRegress(parm);
    	this.billPageList.addAll(getBillPageBeanList(regressParm));
    	for (BillPageBean tmp: billPageList) {
			System.out.println(tmp.getBillDate());
		}
    	//根据时间排序biiPageList
    	Collections.sort(billPageList,new Comparator<BillPageBean>(){
    		   public int compare(BillPageBean o1, BillPageBean o2) {
    			   long time1 = o1.getBillDate().getTime();
    			   long time2 = o2.getBillDate().getTime();
    			   long diff= time1-time2;
    		       int diffint = (int) diff;
    		       return diffint;
    		    }
    		});
    	for (BillPageBean tmp: billPageList) {
    		System.out.println(tmp.getBillDate()+tmp.getDesc());
    	}
    	//处理累计和总计 begin
    	List<BillPageBean> newBillPageBeanList = new ArrayList<BillPageBean>();
    	BillPageBean eachMonth=new BillPageBean();
    	eachMonth.setDesc("合计");
    	BillPageBean totalBean= new BillPageBean();
    	totalBean.setDesc("累计");
    	Date lastDate=null;
    	for (BillPageBean billPage : billPageList) {
    		eachMonth.setInAmt(eachMonth.getInAmt()+billPage.getInAmt());
    		eachMonth.setOutAmt(eachMonth.getOutAmt()+billPage.getOutAmt());
    		totalBean.setInAmt(totalBean.getInAmt()+billPage.getInAmt());
    		totalBean.setOutAmt(totalBean.getOutAmt()+billPage.getOutAmt());
    		newBillPageBeanList.add(billPage);
    		if(lastDate!=null&&lastDate.getMonth()!=billPage.getBillDate().getMonth()){
    	    	BillPageBean tmpEachMonth=new BillPageBean();
    	    	tmpEachMonth.setDesc("合计");
    	    	tmpEachMonth.setInAmt(eachMonth.getInAmt());
    	    	tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
    	    	BillPageBean tmpTotalBean= new BillPageBean();
    	    	tmpTotalBean.setDesc("累计");
    	    	tmpTotalBean.setInAmt(totalBean.getInAmt());
    	    	tmpTotalBean.setOutAmt(totalBean.getOutAmt());
    			newBillPageBeanList.add(tmpEachMonth);
    			newBillPageBeanList.add(tmpTotalBean);
    			//清空合计
    			eachMonth.setInAmt(0);
    			eachMonth.setOutAmt(0);
    		}
    		lastDate=billPage.getBillDate();
		}
    	//列表最后加入合计行begin
    	BillPageBean tmpEachMonth=new BillPageBean();
    	tmpEachMonth.setDesc("合计");
    	tmpEachMonth.setInAmt(eachMonth.getInAmt());
    	tmpEachMonth.setOutAmt(eachMonth.getOutAmt());
    	BillPageBean tmpTotalBean= new BillPageBean();
    	tmpTotalBean.setDesc("累计");
    	tmpTotalBean.setInAmt(totalBean.getInAmt());
    	tmpTotalBean.setOutAmt(totalBean.getOutAmt());
		newBillPageBeanList.add(tmpEachMonth);
		newBillPageBeanList.add(tmpTotalBean);
    	//列表最后加入合计行end
    	this.billPageList=newBillPageBeanList;
    	//清空临时变量
    	newBillPageBeanList=null;
    	//处理累计和总计 end
    	//加入上年结存
    	BillPageBean lastYear = new BillPageBean();
    	Date startDate = this.formateDate.parse((this.startDate.getValue()+"").substring(0,10).replace("-", "/"));
    	Calendar lastYearCd=Calendar.getInstance();
    	lastYearCd.setTime(startDate);
    	lastYearCd.add(Calendar.DAY_OF_YEAR, -1);
    	lastYear.setBillDate(new Timestamp(lastYearCd.getTimeInMillis()));
    	lastYear.setDesc(this.LAST_AMT_DESC);
    	TParm selectParm = new TParm();
    	selectParm.setData("ORDER_CODE",this.orderCode.getValue());
    	selectParm.setData("ORG_CODE",this.orgCombo.getValue());
    	selectParm.setData("TRANDATE",this.formateDate.format(lastYearCd.getTime()));
    	TParm selectStockQty = INDBillPageTool.getInstance().selectStockQty(selectParm);
    	//定义默认值 by liyh 20120823 七夕 如果为结存 默认值为0
    	int defaultNum = 0;
    	System.out.println("selectStockQty.getCount(STOCK_QTY): "+selectStockQty.getCount("STOCK_QTY"));
    	System.out.println("getDouble(STOCK_QTY): "+selectStockQty.getInt("STOCK_QTY",0));
    	if(null != selectStockQty && selectStockQty.getCount("STOCK_QTY")>0){
    		lastYear.setLastNum(selectStockQty.getInt("STOCK_QTY",0));
//    		lastYear.setLastPrice(selectStockQty.getDouble("VERIFYIN_PRICE",0));
    		lastYear.setLastAmt(selectStockQty.getDouble("STOCK_AMT",0));
    	}else{//by liyh 20120823 七夕 如果为结存 默认值为0
    		lastYear.setLastNum(defaultNum);
//    		lastYear.setLastPrice(selectStockQty.getDouble("VERIFYIN_PRICE",0));
    		lastYear.setLastAmt(defaultNum);
    	}
    	billPageList.add(0,lastYear);
    	//加入本月结存
    	BillPageBean currentMonth = new BillPageBean();
    	Date endDate = this.formateDate.parse((this.endDate.getValue()+"").substring(0,10).replace("-", "/"));
    	Calendar endMonthCd=Calendar.getInstance();
    	endMonthCd.setTime(endDate);
    	currentMonth.setBillDate(new Timestamp(endMonthCd.getTimeInMillis()));
    	currentMonth.setDesc(this.CURRENT_AMT_DESC);
    	selectParm = new TParm();
    	selectParm.setData("ORDER_CODE",this.orderCode.getValue());
    	selectParm.setData("ORG_CODE",this.orgCombo.getValue());
    	selectParm.setData("TRANDATE",this.formateDate.format(endMonthCd.getTime()));
    	selectStockQty = INDBillPageTool.getInstance().selectStockQty(selectParm);
    	if(null != selectStockQty && selectStockQty.getCount("STOCK_QTY")>0){
    		currentMonth.setLastNum(selectStockQty.getInt("STOCK_QTY",0));
//    		currentMonth.setLastPrice(selectStockQty.getDouble("VERIFYIN_PRICE",0));
    		currentMonth.setLastAmt(selectStockQty.getDouble("STOCK_AMT",0));
    	}else{//by liyh 20120823 七夕 如果为结存 默认值为0
    		currentMonth.setLastNum(defaultNum);
//    		currentMonth.setLastPrice(selectStockQty.getDouble("VERIFYIN_PRICE",0));
    		currentMonth.setLastAmt(defaultNum);
    	}
    	billPageList.add(currentMonth);
//    	this.medTable.setParmValue(selectSysFeeMed);
//    	System.out.println("查询出的list-size："+billPageList.size());
    	//将list转换成Tparm并加入累计信息
    	TParm tableParm = getTParmFromBeanList(billPageList);
    	this.billTable.setParmValue(tableParm);
    }
    private List<BillPageBean> getBillPageBeanList(TParm parm){
    	List<BillPageBean> listBean = new ArrayList<INDBillPageControl.BillPageBean>();
    	for(int i=0;i<parm.getCount();i++){
    		BillPageBean billBean = new BillPageBean();
    		String type=parm.getValue("TYPE_CODE",i);
    		billBean.setBillDate(parm.getTimestamp("IN_DATE",i));
    		billBean.setBillNo(parm.getValue("IND_NO",i));
    		if("REGRESS".equals(type)||"DEP".equals(type)||"WAS".equals(type)||"THO".equals(type)){
    			billBean.setOutPrice(parm.getDouble("VERIFYIN_PRICE",i));
    			billBean.setOutNum(parm.getDouble("QTY",i));
    			billBean.setOutAmt(parm.getDouble("VERIFYIN_AMT",i));
    		}
    		if("VERIFY".equals(type)||"RET".equals(type)||"THI".equals(type)){
    			billBean.setInPrice(parm.getDouble("VERIFYIN_PRICE",i));
    			billBean.setInNum(parm.getDouble("QTY",i));
    			billBean.setInAmt(parm.getDouble("VERIFYIN_AMT",i));
    		}
    		billBean.setDesc(type);
    		//desc
    		if("REGRESS".equals(type)){
    			billBean.setDesc("退货");
    		}
    		if("DEP".equals(type)){
    			billBean.setDesc("请领");
    		}
    		if("WAS".equals(type)){
    			billBean.setDesc("损耗");
    		}
    		if("THO".equals(type)){
    			billBean.setDesc("其他出库");
    		}
    		if("REGRESS".equals(type)){
    			billBean.setDesc("退货");
    		}
    		if("VERIFY".equals(type)){
    			billBean.setDesc("验收");
    		}
    		if("COS".equals(type)){
    			billBean.setDesc("卫耗材领用");
    		}
    		if("RET".equals(type)){
    			billBean.setDesc("退库");
    		}
    		if("THI".equals(type)){
    			billBean.setDesc("其他入库");
    		}
    		listBean.add(billBean);
    	}
    	return listBean;
    }
    /**
     *
     * 将bean转换成TParm
     * @return
     */
    private TParm getTParmFromBeanList(List<BillPageBean> billPageBeanList){
    	TParm tableParm = new TParm();
    	for (BillPageBean billPageBean : billPageBeanList) {
    		String billDate="";
    		if(billPageBean.getBillDate()!=null){
    			billDate=this.formateDate.format(billPageBean.getBillDate());
    		}
    		tableParm.addData("BILL_DATE",billDate);
    		tableParm.addData("BILL_NO", billPageBean.getBillNo());
    		tableParm.addData("DESC", billPageBean.getDesc());
    		tableParm.addData("IN_NUM", nullToEmptyStr(billPageBean.getInNum()));
    		tableParm.addData("IN_PRICE", nullToEmptyStr(billPageBean.getInPrice()));
    		tableParm.addData("IN_AMT", nullToEmptyStr(billPageBean.getInAmt()));
    		tableParm.addData("OUT_NUM", nullToEmptyStr(billPageBean.getOutNum()));
    		tableParm.addData("OUT_PRICE", nullToEmptyStr(billPageBean.getOutPrice()));
    		tableParm.addData("OUT_AMT", nullToEmptyStr(billPageBean.getOutAmt()));
    		if(this.LAST_AMT_DESC.equals(billPageBean.getDesc()) || this.CURRENT_AMT_DESC.equals(billPageBean.getDesc())){//by liyh 20120823 七夕 如果为结存 默认值为0
    			tableParm.addData("LAST_NUM", nullToZero(nullToEmptyStr(billPageBean.getLastNum())));
    		}else{
    			tableParm.addData("LAST_NUM", nullToEmptyStr(billPageBean.getLastNum()));
    		}
    		tableParm.addData("LAST_PRICE", nullToEmptyStr(billPageBean.getLastPrice()));
    		if(this.LAST_AMT_DESC.equals(billPageBean.getDesc()) || this.CURRENT_AMT_DESC.equals(billPageBean.getDesc())){//by liyh 20120823 七夕 如果为结存 默认值为0
    			tableParm.addData("LAST_AMT", nullToZero(nullToEmptyStr(billPageBean.getLastAmt())));
    		}else{
    			tableParm.addData("LAST_AMT", nullToEmptyStr(billPageBean.getLastAmt()));
    		}
		}
    	return tableParm;
    }
    private String nullToEmptyStr(double num){
    	if((""+num).equals("0.0")){
    		return "";
    	}else{
    		return num+"";
    	}
    }
    
    private String nullToZero(String num){
    	if(null == num || "".equals(num)){
    		return "0";
    	}else{
    		return num;
    	}
    }
    /**
     * 初始化药品列表
     * 方法描述
     * @param orgCode
     * @param regionCode
     */
    private void initMedTable(String orgCode,String regionCode){
    	TParm parm = new TParm();
    	parm.setData("ORG_CODE",orgCode);
    	parm.setData("REGION_CODE",regionCode);
    	TParm selectSysFeeMed = INDBillPageTool.getInstance().selectSysFeeMed(parm);
    	this.medTable.setParmValue(selectSysFeeMed);
    }
    /**
     *
     * 初始化页面控件便于程序调用
     */
    private void initComponent(){
    	orgCombo=(TComboBox)this.getComponent("ORG_COMB0");
    	startDate=(TTextFormat)this.getComponent("START_DATE");
    	endDate=(TTextFormat)this.getComponent("END_DATE");
    	this.medTable=(TTable)this.getComponent("MED_TABLE");
    	this.orderCode=(TTextField)this.getComponent("ORDER_CODE");
    	this.orderDesc=(TTextField)this.getComponent("ORDER_DESC");
    	this.orderDescription=(TTextField)this.getComponent("DESCRIPTION");
    	this.orderUnitDesc=(TTextField)this.getComponent("ORDER_UNIT_DESC");
    	this.billTable=(TTable)this.getComponent("BIL_TABLE");
    }
    class BillPageBean{
    	private Timestamp billDate;
    	private String billNo;
    	private String desc;
    	private double inNum;
    	private double inPrice;
    	private double inAmt;
    	private double outNum;
    	private double outPrice;
    	private double outAmt;
    	private double lastNum;
    	private double lastPrice;
    	private double lastAmt;
		public Timestamp getBillDate() {
			return billDate;
		}
		public void setBillDate(Timestamp billDate) {
			this.billDate = billDate;
		}
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public double getInNum() {
			return inNum;
		}
		public void setInNum(double inNum) {
			this.inNum = inNum;
		}
		public double getInPrice() {
			return inPrice;
		}
		public void setInPrice(double inPrice) {
			this.inPrice = inPrice;
		}
		public double getInAmt() {
			return inAmt;
		}
		public void setInAmt(double inAmt) {
			this.inAmt = inAmt;
		}
		public double getOutNum() {
			return outNum;
		}
		public void setOutNum(double outNum) {
			this.outNum = outNum;
		}
		public double getOutPrice() {
			return outPrice;
		}
		public void setOutPrice(double outPrice) {
			this.outPrice = outPrice;
		}
		public double getOutAmt() {
			return outAmt;
		}
		public void setOutAmt(double outAmt) {
			this.outAmt = outAmt;
		}
		public double getLastNum() {
			return lastNum;
		}
		public void setLastNum(double lastNum) {
			this.lastNum = lastNum;
		}
		public double getLastPrice() {
			return lastPrice;
		}
		public void setLastPrice(double lastPrice) {
			this.lastPrice = lastPrice;
		}
		public double getLastAmt() {
			return lastAmt;
		}
		public void setLastAmt(double lastAmt) {
			this.lastAmt = lastAmt;
		}

    }
}
