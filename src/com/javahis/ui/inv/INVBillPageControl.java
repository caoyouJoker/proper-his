package com.javahis.ui.inv;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.ind.INDBillPageTool;
import jdo.inv.INVBillPageTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;

/**
*
* <strong>Title : INVBillPageControl<br></strong>
* <strong>Description : </strong>物资库帐页<br>
* <strong>Create on : 2013-11-11<br></strong>
* <p>
* <strong>Copyright (C) <br></strong>
* <p>
* @author duzhw<br>
* @version <strong>ProperSoft</strong><br>
* <br>
* <strong>修改历史:</strong><br>
* 修改人		修改日期		修改描述<br>
* -------------------------------------------<br>
* <br>
* <br>
*/
public class INVBillPageControl extends TControl {
	//页面控件
	private TComboBox orgCombo;
	private TTextFormat startDate;
	private TTextFormat endDate;
	private TTable medTable;
	private TTable billTable;
	private TTextField invCode;
	private TTextField invDesc;
	private TTextField invDescription;
	private TTextField invUnitDesc;
	
	private Map<String, String> inPriceMap;
	private Map<String, String> outPriceMap;
	private Map<String, String> inOutQtyMap;
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
    	initComponent();
    	initPage();
    }
    /**
     *
     * 初始化控件默认值
     */
    public void initPage(){
    	//设置科室下拉框值begin
//    	this.orderCode.setse
    	String sql = getInvOrgComobo("='A'","",Operator.getRegion());
        TParm parmCbo = new TParm(TJDODBTool.getInstance().select(sql));
    	//设置科室下拉框值end
		// 重新处理开始时间和结束时间 begin duzhw 2013-11-12
		Calendar cd = Calendar.getInstance();
		Calendar cdto = Calendar.getInstance();
		cd.add(Calendar.MONTH, -1);
		cd.set(Calendar.DAY_OF_MONTH, 26);
		cdto.set(Calendar.DAY_OF_MONTH, 25);
		String format = formateDate.format(cd.getTime());
		this.startDate.setValue(formateDate.format(cd.getTime()));
		this.endDate.setValue(formateDate.format(cdto.getTime()));
		// 重新处理开始时间和结束时间 begin duzhw 2013-11-12
		//初始化默认物资库
		orgCombo.setSelectedIndex(2);
		//初始化物资列表的数据 begin
		initMedTable(orgCombo.getValue(),this.regionCode);
		//初始化物资列表的数据 end
		//初始化医嘱的textField
        //只有text有这个方法，调用sys_fee弹出框
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 1);
        callFunction("UI|INV_CODE|setPopupMenuParameter", "UD",
                     "%ROOT%\\config\\inv\\INVBasePopup.x",parm);

        //textfield接受回传值
        callFunction("UI|INV_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        inPriceMap=getInprice();
		outPriceMap=getOutprice();

    }
    public void popReturn(String tag, Object obj) {
        TParm parmrtn = (TParm) obj;
        this.setValue("INV_CODE", parmrtn.getValue("INV_CODE"));
        this.setValue("INV_DESC", parmrtn.getValue("INV_CHN_DESC"));
        this.setValue("DESCRIPTION", parmrtn.getValue("DESCRIPTION"));
        this.setValue("INV_UNIT_DESC", "");
        //带入物资的相关信息
    	TParm parm = new TParm();
    	parm.setData("INV_CODE",parmrtn.getValue("INV_CODE"));
    	parm.setData("ORG_CODE",this.orgCombo.getValue());
    	parm.setData("REGION_CODE",regionCode);
    	TParm selectSysFeeMed = INVBillPageTool.getInstance().selectSysFeeMed(parm);
    	if(selectSysFeeMed.getCount("INV_CODE")<=0){
    		return;
    	}
    	String invCode = selectSysFeeMed.getValue("INV_CODE",0);
    	String invDesc=selectSysFeeMed.getValue("INV_CHN_DESC",0);
    	String invDescription=selectSysFeeMed.getValue("DESCRIPTION",0);
    	String invUnitDesc=selectSysFeeMed.getValue("UNIT_CHN_DESC",0);
    	this.invCode.setValue(invCode);
    	this.invDesc.setValue(invDesc);
    	this.invDescription.setValue(invDescription);
    	this.invUnitDesc.setValue(invUnitDesc);

    }
    /**
     * 根据条件填充物资库部门列表-duzhw
     * @param condition String
     * @param flg String
     * @return String
     */
    public static String getInvOrgComobo(String condition, String flg, String region_code) {
        String type = "";
        if (!"".equals(condition)) {
            type = " WHERE ORG_TYPE " + condition;
        }
        if (!"".equals(flg)) {
            type += " STATION_FLG = '" + flg + "' ";
        }
        return
            "SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME FROM INV_ORG " + type
            + " AND REGION_CODE = '" + region_code + "'  "
            + " ORDER BY ORG_CODE,SEQ";
    }
    public void queryMedList(){
    	String orgValue = orgCombo.getValue();
    	if("".equals(orgValue)){
    		this.messageBox("请选择查询物资库！");
    		return ;
    	}
		//初始化物资列表的数据 begin
		initMedTable(orgCombo.getValue(),this.regionCode);
		//初始化物资列表的数据 end
    }
    
    private Map<String, String> getInprice(){
		Map<String, String>  map=new HashMap<String, String>();
		String sql="select a.inv_code,b.CONTRACT_PRICE from inv_base a left join inv_agent b on a.inv_code=b.inv_code ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			map.put(parm.getValue("INV_CODE", i), parm.getValue("CONTRACT_PRICE", i));
		}
		return map;
		
	}
	
	private Map<String, String> getOutprice(){
		Map<String, String>  map=new HashMap<String, String>();
		String sql="select a.inv_code,b.OWN_PRICE from inv_base a left join sys_fee b on a.order_code=b.order_code ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			map.put(parm.getValue("INV_CODE", i), parm.getValue("CONTRACT_PRICE", i));
		}
		return map;
	}
    private Map<String, String> getInOutQty(String s,String e){
		Map<String, String>  map=new HashMap<String, String>();
		String sql="select  ORG_CODE  , INV_CODE , sum(DD_IN_QTY)  as inq,  sum(DD_IN_QTY) as outq   from inv_ddstock";
		sql+=" where TRANDATE>'"+s+"' and TRANDATE<='"+e+"'";
		sql+="	group by   ORG_CODE  , INV_CODE     ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			map.put(parm.getValue("INV_CODE", i)+"::"+parm.getValue("ORG_CODE", i), parm.getValue("INQ", i)+"::"+parm.getValue("OUTQ", i));
		}
		return map;
	}
    
    /**
     *
     * 查询帐页的详细信息
     * @throws ParseException
     */
    public void onQuery() throws ParseException{

    	String orgCode=this.orgCombo.getValue();
    	String invCode=this.invCode.getValue();
    	if("".equals(orgCode)){
    		this.messageBox("请选择查询部门！");
    		return;
    	}
    	if("".equals(invCode)){
    		this.messageBox("请输入查询物资！");
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
    	String s=getValueString("START_DATE").substring(0, 10).replaceAll("-", "");
		String e=getValueString("END_DATE").substring(0, 10).replaceAll("-", "");
		inOutQtyMap=getInOutQty(s, e);
		String sql="SELECT a.inv_code,a.stock_qty,b.INV_CHN_DESC,b.DESCRIPTION " +
				" from inv_stockm a " +
				" left join inv_base b on a.inv_code=b.inv_code where a.org_code='"+getValueString("ORG_COMB0")+"'" +
				" and a.inv_code='"+invCode+"'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm mParm=new TParm();   
        //System.out.println("=========dddd========"+inOutQtyMap.get("08.04.0023009::0306"));
        for (int i = 0; i < selParm.getCount("INV_CODE"); i++) {
        	String inString=inOutQtyMap.get(selParm.getValue("INV_CODE",i)+"::"+getValueString("ORG_COMB0"));
        	String in="0";
        	String out="0";
        	if (inString!=null&&inString.length()>2) {
        		in=inString.split("::")[0];
        		out=inString.split("::")[1];
			}
        	
        	String inprice=inPriceMap.get(selParm.getValue("INV_CODE",i));
        	if (inprice==null) {
        		inprice="0";
			}
        	String outprice=outPriceMap.get(selParm.getValue("INV_CODE",i));
        	if (outprice==null||outprice.length()<1) {
        		outprice="0";
			}
        	mParm.setData("INV_CODE", i, selParm.getValue("INV_CODE",i));
        	mParm.setData("INV_CHN_DESC", i, selParm.getValue("INV_CHN_DESC",i));
        	mParm.setData("DESCRIPTION", i, selParm.getValue("DESCRIPTION",i));
        	mParm.setData("INQTY", i, in);
        	mParm.setData("INPRICE", i, inprice);
        	mParm.setData("INALL", i, new BigDecimal(in).multiply(new BigDecimal(inprice)).toString());
        	mParm.setData("OUTQTY", i, out);
        	mParm.setData("OUTPRICE", i, outprice);
        	mParm.setData("OUTALL", i, new BigDecimal(out).multiply(new BigDecimal(outprice)).toString());
        	
        	
        	mParm.setData("MQTY", i, selParm.getValue("STOCK_QTY",i));
        	mParm.setData("MPRICE", i, inprice);
        	mParm.setData("MALL", i, new BigDecimal(selParm.getValue("STOCK_QTY",i)).multiply(new BigDecimal(inprice)).toString());
        	
		}
        billTable.setParmValue(mParm);
    	//initBillDetailTable(orgCode,invCode,this.regionCode);
    }
    /**
     *
     * 物资列表click事件
     */
    public void onMedTableClick(){
    	int selectedIndx=this.medTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	TParm tableparm=this.medTable.getParmValue();
    	//在页面上带出物资的相关属性
    	String invCode = tableparm.getValue("INV_CODE",selectedIndx);
    	String invDesc=tableparm.getValue("INV_CHN_DESC",selectedIndx);
    	String invDescription=tableparm.getValue("DESCRIPTION",selectedIndx);
    	String invUnitDesc=tableparm.getValue("UNIT_CHN_DESC",selectedIndx);
    	this.invCode.setValue(invCode);
    	this.invDesc.setValue(invDesc);
    	this.invDescription.setValue(invDescription);
    	this.invUnitDesc.setValue(invUnitDesc);
    }
    /**
     * 清空方法
     */
    public void onClear() {
    	// 重新处理开始时间和结束时间 begin duzhw 2013-11-12
		Calendar cd = Calendar.getInstance();
		Calendar cdto = Calendar.getInstance();
		cd.add(Calendar.MONTH, -1);
		cd.set(Calendar.DAY_OF_MONTH, 26);
		cdto.set(Calendar.DAY_OF_MONTH, 25);
		String format = formateDate.format(cd.getTime());
		this.startDate.setValue(formateDate.format(cd.getTime()));
		this.endDate.setValue(formateDate.format(cdto.getTime()));
        this.clearValue("INV_CODE;INV_DESC;DESCRIPTION;INV_UNIT_DESC");
        billTable.removeRowAll();
        
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
                        "物资帐页报表");
        String startDate = this.startDate.getValue().toString();
        String endDate = this.endDate.getValue().toString();
            startDate = startDate.substring(0, 10).replace("-", "/");
            endDate = endDate.substring(0, 10).replace("-", "/");
        String date = startDate + " ~ " + endDate;
        String orgCode=this.getComboBox("ORG_COMB0").getSelectedName();
        String invCode = this.getValueString("INV_CODE");
        String invName = this.getValueString("INV_DESC");
        String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
//        prtParm.setData("START_DATE", "TEXT",
//                        startDate);
//        prtParm.setData("END_DATE", "TEXT", endDate);
        prtParm.setData("DATE", "TEXT", "时间：" + date);
        prtParm.setData("ORG_CODE", "TEXT", "部门：" + orgCode);
        prtParm.setData("INV_CODE", "TEXT", "物资名称：" + invCode+" "+invName);
        TParm tableparm = this.billTable.getParmValue();
        tableparm.setCount(tableparm.getCount("INV_CODE"));
        //设置总行数
        tableparm.addData("SYSTEM", "COLUMNS", "INV_CODE");
        tableparm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
        tableparm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        tableparm.addData("SYSTEM", "COLUMNS", "INQTY");
        tableparm.addData("SYSTEM", "COLUMNS", "INPRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "INALL");
        tableparm.addData("SYSTEM", "COLUMNS", "OUTQTY");
        tableparm.addData("SYSTEM", "COLUMNS", "OUTPRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "OUTALL");
        tableparm.addData("SYSTEM", "COLUMNS", "MQTY");
        tableparm.addData("SYSTEM", "COLUMNS", "MPRICE");
        tableparm.addData("SYSTEM", "COLUMNS", "MALL");
        prtParm.setData("TABLE", tableparm.getData());
        //表尾
        prtParm.setData("OPT_DATE", "TEXT", "制表日期：" + now);
        prtParm.setData("OPT_USER", "TEXT", "制表人：" + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVBillPageList.jhw",
                             prtParm);
    }
    public void onExport(){
        if (billTable.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(billTable,
            "物资帐页");

    }
    /**
     * 初始化帐页详细新
     *
     * @param orgCode
     * @param regionCode
     * @throws ParseException
     */
    private void initBillDetailTable(String orgCode,String invCode,String regionCode) throws ParseException{
    	TParm parm = new TParm();
    	parm.setData("ORG_CODE",orgCode);
    	parm.setData("REGION_CODE",regionCode);
    	parm.setData("INV_CODE",invCode);
    	parm.setData("START_DATE",(startDate.getValue()+"").substring(0,10).replace("-", "")+"000000");
    	parm.setData("END_DATE",(endDate.getValue()+"").substring(0,10).replace("-", "")+"235959");
    	//获取明细数据
    	TParm resultParm = INVBillPageTool.getInstance().selectDispenseALL(parm);
    	
    	this.billPageList=new ArrayList<INVBillPageControl.BillPageBean>();
    	//得到入库的parm
    	TParm dispenseINParm = INVBillPageTool.getInstance().selectDispenseIN(parm);
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
    	selectParm.setData("ORDER_CODE",this.invCode.getValue());
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
    	selectParm.setData("ORDER_CODE",this.invCode.getValue());
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
    	List<BillPageBean> listBean = new ArrayList<INVBillPageControl.BillPageBean>();
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
     * 初始化物资列表
     * 方法描述
     * @param orgCode
     * @param regionCode
     */
    private void initMedTable(String orgCode,String regionCode){
    	TParm parm = new TParm();
    	parm.setData("ORG_CODE",orgCode);
    	parm.setData("REGION_CODE",regionCode);
    	TParm selectSysFeeMed = INVBillPageTool.getInstance().selectSysFeeMed(parm);
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
    	this.invCode=(TTextField)this.getComponent("INV_CODE");
    	this.invDesc=(TTextField)this.getComponent("INV_DESC");
    	this.invDescription=(TTextField)this.getComponent("DESCRIPTION");
    	this.invUnitDesc=(TTextField)this.getComponent("INV_UNIT_DESC");
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
    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }
}
