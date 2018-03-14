package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 近效期低值报表</p>
 *
 * <p>Description: 近效期低值报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw 20131108
 * @version 1.0
 */
public class INVValidLowListControl extends TControl  {
	
	public INVValidLowListControl(){
		
	}
	
private static TTable tTable;
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	super.init();
		initPage();
    	
        this.setValue("ORG_CODE", Operator.getDept());
        
        if (this.getPopedem("ALL")) {
			this.callFunction("UI|ORG_CODE|setEnabled", false);
		}else{
			this.callFunction("UI|ORG_CODE|setEnabled", true);
		}
        
    }
    /**
     * 初始画面数据
     */
	private void initPage() {
		//获得TABLE对象
		tTable = (TTable) getComponent("Table");
		
		TParm parm = new TParm();
		// 设置弹出菜单
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// 定义接受返回值方法
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
    
    /**
     * 查询方法
     */
    public void onQuery() {
    	tTable.removeRowAll();//清除主表
    	this.clearValue("ALLCOUNT;ALLFEE");
    	TParm parm = new TParm();
        String org_code = "";
        String inv_code = "";
        
        //部门代码
        org_code = getValueString("ORG_CODE");
        if (org_code == null || org_code.length() <= 0) {
            this.messageBox("请选择查询部门");
            return;
        }
        //物资编码
        inv_code = getValueString("INV_CODE");
//        if (inv_code == null || inv_code.length() <= 0) {
//            this.messageBox("请选择查询物资");
//            return;
//        }
        //供应商
        String sup_code = getValueString("SUP_CODE");
        //上级供应商
        String up_sup_code = getValueString("UP_SUP_CODE");
        //物资分类
        String kind = getValueString("KIND");
        String date = StringTool.getString(SystemTool.getInstance().getDate(),
        "yyyyMMdd");
        //效期
        if (getRadioButton("VALID_DATE_A").isSelected()) {
        	parm.setData("VALID_DATE",getMonthDay(3));
        	this.setValue("VALID_DATE_VALUE", getMonthDay(3));
        }
        else if (getRadioButton("VALID_DATE_B").isSelected()) {
        	parm.setData("VALID_DATE",getMonthDay(6));
        	this.setValue("VALID_DATE_VALUE", getMonthDay(6));
        }else if (getRadioButton("VALID_DATE_D").isSelected()){
        	parm.setData("VALID_DATE","");
        	this.setValue("VALID_DATE_VALUE", "99991231");
        }
        else {
        	String valid_date = getValueString("VALID_DATE");
        	parm.setData("VALID_DATE", valid_date.substring(0, 4) +
        			valid_date.substring(5, 7) + valid_date.substring(8, 10));
        	this.setValue("VALID_DATE_VALUE", valid_date.substring(0, 4) +
        			valid_date.substring(5, 7) + valid_date.substring(8, 10));
        }
        
//        System.out.println("org_code="+org_code);
//        System.out.println("inv_code="+inv_code);
//        System.out.println("sup_code="+sup_code);
//        System.out.println("up_sup_code="+up_sup_code);
//        System.out.println("kind="+kind);
//        System.out.println("valid_date="+parm.getValue("VALID_DATE"));
        
        //查询sql
//        String sql = "SELECT T.INV_CODE,T.INV_CHN_DESC,T.DESCRIPTION,G.UNIT_CHN_DESC,D.STOCK_QTY,T.COST_PRICE," +
//        		"E.SUP_CHN_DESC,F.SUP_CHN_DESC AS UP_SUP_CHN_DESC,I.SUP_CHN_DESC AS MAN_CODE,H.CATEGORY_CHN_DESC AS INV_KIND,B.VALID_DATE " +
//        		" FROM INV_BASE T LEFT JOIN SYS_SUPPLIER E ON T.SUP_CODE = E.SUP_CODE" +
//        		" LEFT JOIN SYS_SUPPLIER F ON T.UP_SUP_CODE = F.SUP_CODE " +
//        		" LEFT JOIN SYS_SUPPLIER I ON  T.MAN_CODE = I.SUP_CODE " +
//        		" LEFT JOIN SYS_CATEGORY H ON T.INV_KIND = H.CATEGORY_CODE," +
//        		" INV_STOCKDD B, SYS_DEPT C, INV_STOCKDD D,  SYS_UNIT G " +
//        		" WHERE (T.SEQMAN_FLG != 'Y' OR T.EXPENSIVE_FLG != 'Y') " +
//        		" AND T.INV_CODE = B.INV_CODE AND B.ORG_CODE = C.DEPT_CODE " +
//        		" AND T.INV_CODE = D.INV_CODE " +
//        		" AND T.DISPENSE_UNIT = G.UNIT_CODE " ;
        String sql = " SELECT T.INV_CODE, " +
        		" T.INV_CHN_DESC, " +
        		" T.DESCRIPTION, " +
        		" G.UNIT_CHN_DESC, " +
        		" B.STOCK_QTY, " +
        		" IA.CONTRACT_PRICE, " +
        		" E.SUP_CHN_DESC, " +
        		" F.SUP_CHN_DESC AS UP_SUP_CHN_DESC, " +
        		" I.SUP_CHN_DESC AS MAN_CODE, " +
        		" H.CATEGORY_CHN_DESC AS INV_KIND, " +
        		" C.DEPT_CHN_DESC, " +
        		" B.VALID_DATE " +
        		" FROM INV_BASE T, INV_STOCKD B, SYS_DEPT C, SYS_SUPPLIER E, SYS_SUPPLIER F, SYS_SUPPLIER I, SYS_CATEGORY H, SYS_UNIT G, INV_AGENT IA " +
        		" WHERE (T.SEQMAN_FLG <> 'Y' OR T.SEQMAN_FLG IS NULL OR T.EXPENSIVE_FLG <> 'Y' OR T.EXPENSIVE_FLG IS NULL) " +
        		" AND B.INV_CODE = T.INV_CODE(+) " +
        		" AND B.ORG_CODE = C.DEPT_CODE " +
        		" AND T.DISPENSE_UNIT = G.UNIT_CODE " +
        		" AND T.SUP_CODE = E.SUP_CODE(+) " +
        		" AND T.UP_SUP_CODE = F.SUP_CODE(+) " +
        		" AND T.MAN_CODE = I.SUP_CODE(+) " +
        		" AND T.INV_KIND = H.CATEGORY_CODE(+) " +
        		" AND H.RULE_TYPE = 'INV_BASE' " + 
        		" AND T.INV_CODE = IA.INV_CODE(+) ";
        		
        //部门
        if(!"".equals(org_code)){
        	sql += " AND B.ORG_CODE = '"+org_code+"'";
        }
        //物资编码
        if(!"".equals(inv_code)){
        	sql += " AND T.INV_CODE = '"+inv_code+"'";
        }
        //供应商
        if(!"".equals(sup_code)){
        	sql += " AND T.SUP_CODE = '"+sup_code+"'";
        }
        //上级供应商
        if(!"".equals(up_sup_code)){
        	sql += " AND T.UP_SUP_CODE = '"+up_sup_code+"'";
        }
        //物资分类
        if(!"".equals(kind)){
        	sql += " AND T.INV_KIND = '"+kind+"'";
        }
        //效期
        if(!"".equals(parm.getValue("VALID_DATE"))){
        	sql += " AND B.VALID_DATE <= TO_DATE('"+parm.getValue("VALID_DATE")+"','YYYYMMDD')";
        }
        
        
        sql += " ORDER BY T.INV_CODE,T.INV_CHN_DESC,T.SUP_CODE," +
        		"T.UP_SUP_CODE,T.INV_KIND,B.VALID_DATE";
        //System.out.println("近效期低值sql--->"+sql);
        TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        if(resultParm.getCount("INV_CODE")<0){
        	this.messageBox("没有要查询的数据");
        	return;
        }
//        //统计条数、总金额
//        TParm newparm = new TParm(); 
//        String inv_code1 = "";
//        String inv_code2 = "";
//        int qty1 = 0;
//        int qty2 = 0;
//        int allqty = 0;
//        String flg = "";
//        
//        String inv_chn_desc1 = "";
//        String description1 = "";
//        String unit_chn_desc1 = "";
//        String cost_price1 = "";
//        String dept_chn_desc1 = "";
//        String sup_chn_desc1 = "";
//        String up_sup_chn_desc1 = "";
//        String man_code1 = "";
//        String inv_kind1 = "";
//        String valid_date1 = "";
//        String inv_chn_desc2 = "";
//        String description2 = "";
//        String unit_chn_desc2 = "";
//        String cost_price2 = "";
//        String dept_chn_desc2 = "";
//        String sup_chn_desc2 = "";
//        String up_sup_chn_desc2 = "";
//        String man_code2 = "";
//        String inv_kind2 = "";
//        String valid_date2 = "";
//        int j = 0;//newparm的行数
        double allFee = 0.00;
        int count = resultParm.getCount();
        setValue("ALLCOUNT",String.valueOf(count));
        for (int i = 0; i < count; i++) {
        	double d  = Double.parseDouble(resultParm.getValue("STOCK_QTY", i));
        	allFee  = allFee + ( Double.parseDouble(resultParm.getValue("CONTRACT_PRICE", i))*d );
//wangming20140109注掉start        	
//        	inv_code1 = resultParm.getValue("INV_CODE", i);
//        	inv_code2 = resultParm.getValue("INV_CODE", i+1);
//        	qty1 = resultParm.getInt("STOCK_QTY", i);
//        	qty2 = resultParm.getInt("STOCK_QTY", i+1);
//        	
//        	inv_chn_desc1 = resultParm.getValue("INV_CHN_DESC", i);
//        	description1 = resultParm.getValue("DESCRIPTION", i);
//            unit_chn_desc1 = resultParm.getValue("UNIT_CHN_DESC", i);
//            cost_price1 = resultParm.getValue("COST_PRICE", i);
//            dept_chn_desc1 = resultParm.getValue("DEPT_CHN_DESC", i);
//            sup_chn_desc1 = resultParm.getValue("SUP_CHN_DESC", i);
//            up_sup_chn_desc1 = resultParm.getValue("UP_SUP_CHN_DESC", i);
//            man_code1 = resultParm.getValue("MAN_CODE", i);
//            inv_kind1 = resultParm.getValue("INV_KIND", i);
//            valid_date1 = resultParm.getValue("VALID_DATE", i);
//            
//            inv_chn_desc2 = resultParm.getValue("INV_CHN_DESC", i+1);
//        	description2 = resultParm.getValue("DESCRIPTION", i+1);
//            unit_chn_desc2 = resultParm.getValue("UNIT_CHN_DESC", i+1);
//            cost_price2 = resultParm.getValue("COST_PRICE", i+1);
//            dept_chn_desc2 = resultParm.getValue("DEPT_CHN_DESC", i+1);
//            sup_chn_desc2 = resultParm.getValue("SUP_CHN_DESC", i+1);
//            up_sup_chn_desc2 = resultParm.getValue("UP_SUP_CHN_DESC", i+1);
//            man_code2 = resultParm.getValue("MAN_CODE", i+1);
//            inv_kind2 = resultParm.getValue("INV_KIND", i+1);
//            valid_date2 = resultParm.getValue("VALID_DATE", i+1);
//        	if(i == 0){
//        		allqty = qty1;
//        	}
//        	if(i < count-1){
//        		if(inv_code1.equals(inv_code2)){
//            		allqty += qty2;
//            		flg = "true";
//            	}else{
//            		//组装新parm
//            		newparm.setData("INV_CODE", j++, inv_code1);
//            		newparm.setData("INV_CHN_DESC", j++, inv_chn_desc1);
//            		newparm.setData("DESCRIPTION", j++, description1);
//            		newparm.setData("UNIT_CHN_DESC", j++, unit_chn_desc1);
//            		newparm.setData("INV_NUM", j++, allqty);
//            		newparm.setData("COST_PRICE", j++, cost_price1);
//            		newparm.setData("DEPT_CHN_DESC", j++, dept_chn_desc1);
//            		newparm.setData("SUP_CHN_DESC", j++, sup_chn_desc1);
//            		newparm.setData("UP_SUP_CHN_DESC", j++, up_sup_chn_desc1);
//            		newparm.setData("MAN_CODE", j++, man_code1);
//            		newparm.setData("INV_KIND", j++, inv_kind1);
//            		newparm.setData("VALID_DATE", j++, valid_date1.substring(0, 10));
//            		allqty = qty2;//置为下一个不同物资编码的数量
//            		flg = "false";
//            	}
//        	}
//        	if(i == count-1){
//        		if(flg.equals("true")){
//        			//包含最后一条数据组装parm
//        			newparm.setData("INV_CODE", j++, inv_code1);
//            		newparm.setData("INV_CHN_DESC", j++, inv_chn_desc1);
//            		newparm.setData("DESCRIPTION", j++, description1);
//            		newparm.setData("UNIT_CHN_DESC", j++, unit_chn_desc1);
//            		newparm.setData("INV_NUM", j++, allqty);
//            		newparm.setData("COST_PRICE", j++, cost_price1);
//            		newparm.setData("DEPT_CHN_DESC", j++, dept_chn_desc1);
//            		newparm.setData("SUP_CHN_DESC", j++, sup_chn_desc1);
//            		newparm.setData("UP_SUP_CHN_DESC", j++, up_sup_chn_desc1);
//            		newparm.setData("MAN_CODE", j++, man_code1);
//            		newparm.setData("INV_KIND", j++, inv_kind1);
//            		newparm.setData("VALID_DATE", j++, valid_date1.substring(0, 10));
//        		}else if(flg.equals("false")){
//        			//最后一条单独组装parm
//        			newparm.setData("INV_CODE", j++, inv_code2);
//            		newparm.setData("INV_CHN_DESC", j++, inv_chn_desc2);
//            		newparm.setData("DESCRIPTION", j++, description2);
//            		newparm.setData("UNIT_CHN_DESC", j++, unit_chn_desc2);
//            		newparm.setData("INV_NUM", j++, qty2);
//            		newparm.setData("COST_PRICE", j++, cost_price2);
//            		newparm.setData("DEPT_CHN_DESC", j++, dept_chn_desc2);
//            		newparm.setData("SUP_CHN_DESC", j++, sup_chn_desc2);
//            		newparm.setData("UP_SUP_CHN_DESC", j++, up_sup_chn_desc2);
//            		newparm.setData("MAN_CODE", j++, man_code2);
//            		newparm.setData("INV_KIND", j++, inv_kind2);
//            		String sss = valid_date2.substring(0, 10);
//            		newparm.setData("VALID_DATE", j++, valid_date2.substring(0, 10));
//        		}
//        	}
        	//wangming20140109注掉end           	
		}

        DecimalFormat df = new DecimalFormat("0.00");
        String newAllFee = df.format(allFee);
        newAllFee = feeConversion(newAllFee);//加逗号处理
        this.setValue("ALLFEE",newAllFee);
//2014-01-09注掉        tTable.setParmValue(newparm);
        tTable.setParmValue(resultParm);
    	
    }
    /**
     * 每三位加逗号处理
     */
    public String feeConversion(String fee){
    	String str1 = ""; 
    	String[] s = fee.split("\\.");//以"."来分割
    	
        str1 = new StringBuilder(s[0].toString()).reverse().toString();     //先将字符串颠倒顺序  
        String str2 = "";  
        for(int i=0;i<str1.length();i++){  
            if(i*3+3>str1.length()){  
                str2 += str1.substring(i*3, str1.length());  
                break;  
            }  
            str2 += str1.substring(i*3, i*3+3)+",";  
        }  
        if(str2.endsWith(",")){  
            str2 = str2.substring(0, str2.length()-1);  
        }  
        //最后再将顺序反转过来  
        String str3 = new StringBuilder(str2).reverse().toString();
        //加上小数点后的数
        StringBuffer str4 = new StringBuffer(str3);
        str4 = str4.append(".").append(s[1]);
    	return str4.toString();
    }
    /**
     * 打印方法
     */
    public void onPrint(){
    	String now = SystemTool.getInstance().getDate().toString().substring(0, 10);
    	
    	DecimalFormat df = new DecimalFormat("0.0000");
    	
    	String orgCode = getValueString("ORG_CODE");		//部门
    	String invDesc = getValueString("INV_DESC");		//物资
    	String supCode = getValueString("SUP_CODE");		//供应商
    	String upSupCode = getValueString("UP_SUP_CODE");	//上级供应商
    	String kind = getValueString("KIND");				//物资分类
    	String validDate = getValueString("VALID_DATE_VALUE");//效期
    	validDate = validDate.substring(0, 4) + "/" + validDate.substring(4, 6) + "/"
			+ validDate.substring(6, 8);
    	String allCount = getValueString("ALLCOUNT");		//合计笔数
    	String allFee = getValueString("ALLFEE");			//总金额
    	
    	TParm tableParm = tTable.getParmValue() ;
    	
    	if(!"".equals(orgCode) && orgCode!=null){
    		orgCode = tableParm.getValue("DEPT_CHN_DESC", 0);
    	}
    	if(!"".equals(supCode) && supCode!=null){
    		supCode = tableParm.getValue("SUP_CHN_DESC", 0);
    	}
    	if(!"".equals(upSupCode) && upSupCode!=null){
    		upSupCode = tableParm.getValue("UP_SUP_CHN_DESC", 0);
    	}
    	if(!"".equals(kind) && kind!=null){
    		kind = tableParm.getValue("INV_KIND_CODE", 0);
    	}
    	
    	
    	//System.out.println("打印：tableParm="+tableParm);
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount("INV_CODE")<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		//打印数据
		TParm data = new TParm();
		//表头数据
		data.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"近效期低值报表");
		data.setData("ORG_CODE", "TEXT", "部门名称：" + orgCode);
		data.setData("INV_DESC", "TEXT", "物资名称：" + invDesc);
		data.setData("SUP_CODE", "TEXT", "供应商：" + supCode);
		data.setData("UP_SUP_CODE", "TEXT", "上级供应商：" + upSupCode);
		data.setData("KIND", "TEXT", "物资类别：" + kind);
		data.setData("VALID_DATE", "TEXT", "效期：" + validDate);
		data.setData("ALLCOUNT", "TEXT", "合计笔数：" + allCount);
		data.setData("ALLFEE", "TEXT", "总金额：" + allFee);
		for(int i=0;i<tableParm.getCount("INV_CODE");i++){
			//result.addData("RFID", tableParm.getValue("RFID", i)); //赋值 
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)+
					"("+tableParm.getValue("DESCRIPTION", i)+")"); 
			//result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
			result.addData("INV_NUM", tableParm.getValue("INV_NUM", i));
			result.addData("COST_PRICE", df.format(tableParm.getDouble("COST_PRICE", i)));
			result.addData("SUP_CHN_DESC", tableParm.getValue("SUP_CHN_DESC", i));
			result.addData("UP_SUP_CHN_DESC", tableParm.getValue("UP_SUP_CHN_DESC", i));
			result.addData("MAN_CODE", tableParm.getValue("MAN_CODE", i));
			result.addData("INV_KIND", tableParm.getValue("INV_KIND", i));
			result.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).substring(0, 10));

		}
		result.setCount(tableParm.getCount("INV_CODE")) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");//排序
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		//result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "INV_NUM");
		result.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		result.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "UP_SUP_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "MAN_CODE");
		result.addData("SYSTEM", "COLUMNS", "INV_KIND");
		result.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		
		data.setData("TABLE", result.getData()) ; 
		//表尾数据
		data.setData("OPT_DATE", "TEXT", "制作时间："+now);
		data.setData("OPT_USER", "TEXT", "制作人："+Operator.getName());
		
		//out日志输出data信息-用于调试
		//System.out.println("data=="+data);
		
		//调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVValidLowList.jhw", data);
    }
    
    /**
     * 汇出方法
     */
    public void onExcel(){
    	//TTable tTable = getTable("tTable");
    	if(tTable.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(tTable, "近效期低值表统计");
    	}else {
         this.messageBox("没有汇出数据");
         return;
     }
    }
    
    /**
     * 清空方法
     */
    public void onClear() {
    	getRadioButton("VALID_DATE_A").setSelected(true);
        getTextFormat("VALID_DATE").setEnabled(false);
        this.clearValue("VALID_DATE;INV_CODE;INV_DESC;SUP_CODE;UP_SUP_CODE;KIND;ALLCOUNT;ALLFEE;VALID_DATE_VALUE");
        tTable.removeRowAll();
        
    }
    
    
    /**
     * 变更单选框
     */
    public void onChangeRadioButton() {
        if (getRadioButton("VALID_DATE_C").isSelected()) {
            getTextFormat("VALID_DATE").setEnabled(true);
        }
        else {
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE");
        }
    }
	/**
	 * 得到TextField对象
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
	 /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
    /**
     * 根据指定的月份和天数加减计算需要的月份和天数
     * @param Month String 制定月份 格式:yyyyMM
     * @param Day String 制定月份 格式:dd
     * @param num String 加减的数量 以月为单位
     * @return String
     */
    public String rollMonth(String Month, String Day,int num){
        if(Month.trim().length()<=0){
            return "";
        }
        Timestamp time = StringTool.getTimestamp(Month,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // 当前月＋num
        cal.add(cal.MONTH, num);
        // 将下个月1号作为日期初始值
        cal.set(cal.DATE, 1);
        Timestamp month = new Timestamp(cal.getTimeInMillis());
        String result = StringTool.getString(month, "yyyyMM");
        String lastDayOfMonth = getLastDayOfMonth(result);
        if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
            result += lastDayOfMonth;
        }
        else {
            result += Day;
        }
        return result;
    }
    /**
     * 获取指定月份的最后一天的日期
     * @param date String 格式 YYYYMM
     * @return Timestamp
     */
    public String getLastDayOfMonth(String date) {
        if (date.trim().length() <= 0) {
            return "";
        }
        Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // 当前月＋1，即下个月
        cal.add(cal.MONTH, 1);
        // 将下个月1号作为日期初始值
        cal.set(cal.DATE, 1);
        // 下个月1号减去一天，即得到当前月最后一天
        cal.add(cal.DATE, -1);
        Timestamp result = new Timestamp(cal.getTimeInMillis());
        return StringTool.getString(result, "dd");
    }
    /**
     * 获取指定n个月前的日期
     */
    public String getMonthDay(int no) {
    	String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		//lastDate.set(Calendar.DATE, no);// 设为当前月的n号
		lastDate.add(Calendar.MONTH, +no);// 减n个月，变为下月的1号
		// lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天

		str = sdf.format(lastDate.getTime());
		return str;
    }
	/**
	 * 接受返回值方法
	 *
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
	    TParm parm = (TParm) obj;
	    if (parm == null) {
	            return;
	    }
	    String order_code = parm.getValue("INV_CODE");
	      if (!StringUtil.isNullString(order_code))
	          getTextField("INV_CODE").setValue(order_code);
	      String order_desc = parm.getValue("INV_CHN_DESC");
	      if (!StringUtil.isNullString(order_desc))
	            getTextField("INV_DESC").setValue(order_desc);
	}

}
