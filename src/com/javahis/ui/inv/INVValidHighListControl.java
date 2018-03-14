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
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 近效期高值报表</p>
 *
 * <p>Description: 近效期高值报表</p>   
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw 20131108
 * @version 1.0
 */
public class INVValidHighListControl extends TControl  {
	
	
	public INVValidHighListControl() {
		
	}
	private static TTable detailTable;
	private static TTable mainTable;
	
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
		detailTable = (TTable) getComponent("Table");
		mainTable = (TTable) getComponent("MAIN_TABLE");
		
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
    	mainTable.removeRowAll();//清除主表
    	detailTable.removeRowAll();//清除细表
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
        //fux modify 20140313
        if (getRadioButton("VALID_DATE_E").isSelected()) {
        	parm.setData("VALID_DATE",getMonthDay(1));
        	this.setValue("VALID_DATE_VALUE", getMonthDay(1));
        }
        else if (getRadioButton("VALID_DATE_A").isSelected()) {
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
//        String sql = "SELECT D.RFID,T.INV_CODE,T.INV_CHN_DESC,T.DESCRIPTION,G.UNIT_CHN_DESC,T.COST_PRICE," +
//        		"E.SUP_CHN_DESC,F.SUP_CHN_DESC AS UP_SUP_CHN_DESC,I.SUP_CHN_DESC AS MAN_CODE,H.CATEGORY_CHN_DESC AS INV_KIND,B.VALID_DATE " +
//        		" FROM INV_BASE T, INV_DDSTOCK B, SYS_DEPT C, INV_STOCKDD D, SYS_SUPPLIER E, SYS_SUPPLIER F, SYS_UNIT G, SYS_CATEGORY H, SYS_SUPPLIER I " +
//        		" WHERE T.SEQMAN_FLG = 'Y' AND T.EXPENSIVE_FLG = 'Y' " +
//        		" AND T.INV_CODE = B.INV_CODE AND B.ORG_CODE = C.DEPT_CODE " +
//        		" AND T.INV_CODE = D.INV_CODE AND T.SUP_CODE = E.SUP_CODE AND T.UP_SUP_CODE = F.SUP_CODE" +
//        		" AND T.DISPENSE_UNIT = G.UNIT_CODE AND T.INV_KIND = H.CATEGORY_CODE " +
//        		" AND T.MAN_CODE = I.SUP_CODE";
        String sql = "		SELECT  T.INV_CODE,									" +
        		"					T.INV_CHN_DESC,								" +
        		"					C.DEPT_CHN_DESC,							" +
        		"					B.ORG_CODE,									" +
        		"					E.SUP_CHN_DESC,								" +
        		"					G.UNIT_CHN_DESC,							" +
        		"					T.SUP_CODE,									" +
        		"					F.SUP_CHN_DESC      AS UP_SUP_CHN_DESC,		" +
        		"					T.UP_SUP_CODE,								" +
        		"					I.SUP_CHN_DESC      AS MAN_CODE,			" +
        		"					T.MAN_CODE,									" +
        		//"					H.CATEGORY_CHN_DESC AS INV_KIND,			" +
        		//#4655 liuyl 2016/12/29
        		"					A.CHN_DESC AS INV_KIND,			            " +
        		"					T.INV_KIND AS INV_KIND_CODE,				" +
        		"					COUNT(T.INV_CODE) AS SUM					" +
        		"			FROM 	INV_BASE     T,								" +
        		"					INV_STOCKDD  B,								" +
        		"					SYS_DEPT     C,								" +
        		"					SYS_SUPPLIER E,								" +
        		"					SYS_SUPPLIER F,								" +
        		//"					SYS_CATEGORY H, 							" +
        		"					SYS_SUPPLIER I,								" +
        		"					SYS_UNIT     G,								" +
        		//#4655 liuyl 2016/12/29
        		"					SYS_DICTIONARY A							" +
        		"			WHERE 	T.SEQMAN_FLG = 'Y'							" +
        		"					AND T.EXPENSIVE_FLG = 'Y'					" +
        		"					AND B.ORG_CODE = C.DEPT_CODE				" +
        		"					AND B.INV_CODE = T.INV_CODE(+)				" +
        		"					AND T.SUP_CODE = E.SUP_CODE(+)				" +
        		"					AND T.UP_SUP_CODE = F.SUP_CODE(+)			" +
        		//"					AND T.INV_KIND = H.CATEGORY_CODE(+) 		" +
        		//#4655 liuyl 2016/12/29
        		"					AND T.INV_KIND = A.ID(+) 		            " +
        		"					AND T.DISPENSE_UNIT = G.UNIT_CODE			" +
        		//#4655 liuyl 2016/12/29
        		//"					AND H.RULE_TYPE = 'INV_BASE'				" +
        		"					AND A.GROUP_ID = 'INV_BASE_KIND'			" +
        		//fux modify 20160901
        		"                   AND B.WAST_FLG= 'N'                         " +
        		"					AND T.MAN_CODE = I.SUP_CODE(+)				";
        		
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
        
        
        sql += " GROUP BY 	T.INV_CODE,T.INV_CHN_DESC,C.DEPT_CHN_DESC,B.ORG_CODE, " +
        		" B.ORG_CODE,E.SUP_CHN_DESC,G.UNIT_CHN_DESC,T.SUP_CODE,F.SUP_CHN_DESC,T.UP_SUP_CODE,"+
        		//"H.CATEGORY_CHN_DESC,"+
        		//#4655 liuyl 2016/12/29
        		" A.CHN_DESC,"+
        		"I.SUP_CHN_DESC,T.MAN_CODE,T.INV_KIND " +
        		" ORDER BY 	T.INV_CODE,C.DEPT_CHN_DESC,E.SUP_CHN_DESC,F.SUP_CHN_DESC ";
        System.out.println("近效期高值sql--->"+sql);
        TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        if(resultParm.getCount()<0){   
        	this.messageBox("没有要查询的数据");
        	return;
        }
          
        mainTable.setParmValue(resultParm);
    	
    }
    public void onMainTableClick(){
    	int selectedIndx=this.mainTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;  
    	}
    	TParm tableparm=this.mainTable.getParmValue();
    	String invCode = tableparm.getValue("INV_CODE",selectedIndx);
    	String orgCode = tableparm.getValue("ORG_CODE",selectedIndx);
    	String supCode = tableparm.getValue("SUP_CODE",selectedIndx);
    	String upSupCode = tableparm.getValue("UP_SUP_CODE",selectedIndx);
    	String manCode = tableparm.getValue("MAN_CODE",selectedIndx);
    	String invKindCode = tableparm.getValue("INV_KIND_CODE",selectedIndx);
    	String validDate = "";
    	//效期 
    	//fux modify 20140313
        if (getRadioButton("VALID_DATE_E").isSelected()) {
        	validDate = getMonthDay(1);
        } 
        else if (getRadioButton("VALID_DATE_A").isSelected()) {
        	validDate = getMonthDay(3);
        }
        else if (getRadioButton("VALID_DATE_B").isSelected()) {
        	validDate = getMonthDay(6);
        }else if (getRadioButton("VALID_DATE_D").isSelected()){
        	validDate = "";
        }
        else {
        	String valid_date = getValueString("VALID_DATE");
        	validDate = valid_date.substring(0, 4) +
					valid_date.substring(5, 7) + valid_date.substring(8, 10);
        	
        }
    	
    	String detailSql = "SELECT  B.RFID,									" +
				"					B.BATCH_NO,								" + //增加批号20131229
    			"					T.INV_CODE,								" +
    			"					T.INV_CHN_DESC,							" +
    			"					T.DESCRIPTION,							" +
    			"					G.UNIT_CHN_DESC,						" +
    			"					T.COST_PRICE,							" +
    			"					E.SUP_CHN_DESC,							" +
    			"					F.SUP_CHN_DESC      AS UP_SUP_CHN_DESC,	" +
    			"					I.SUP_CHN_DESC      AS MAN_CODE,		" +
    			//"					H.CATEGORY_CHN_DESC AS INV_KIND,		" +
    			//#4655 liuyl 2016/12/29
    			"					A.CHN_DESC          AS INV_KIND,	    " +
    			"					B.VALID_DATE							" +
    			"			FROM 	INV_BASE     T,							" +
    			"					INV_STOCKDD  B,							" +
    			"					SYS_DEPT     C,							" +
    			"					SYS_SUPPLIER E,							" +
    			"					SYS_SUPPLIER F,							" +
    			"					SYS_UNIT     G,							" +
    			//"					SYS_CATEGORY H,							" +
    			//#4655 liuyl 2016/12/29
    			"					SYS_DICTIONARY A,     					" +
    			"					SYS_SUPPLIER I							" +
    			"			WHERE 	T.SEQMAN_FLG = 'Y'						" +
    			"					AND T.EXPENSIVE_FLG = 'Y'				" +
    			"					AND B.ORG_CODE = C.DEPT_CODE			" +
    			"					AND B.INV_CODE = T.INV_CODE(+)			" +
    			"					AND T.SUP_CODE = E.SUP_CODE(+)			" +
    			"					AND T.UP_SUP_CODE = F.SUP_CODE(+)		" +
    			"					AND T.DISPENSE_UNIT = G.UNIT_CODE		" +
    			//"					AND T.INV_KIND = H.CATEGORY_CODE(+)		" +
    			//#4655 liuyl 2016/12/29
    			"					AND T.INV_KIND = A.ID(+) 		        " +
    			//#4655 liuyl 2016/12/29
    			//"					AND H.RULE_TYPE = 'INV_BASE'			" +
    			"					AND A.GROUP_ID = 'INV_BASE_KIND'		" +
    			"					AND T.MAN_CODE = I.SUP_CODE(+)			" +
        		//fux modify 20160901
        		"                   AND B.WAST_FLG= 'N'                     " +
    			"					AND B.INV_CODE = '"+invCode+"' ";
    	
    	//部门
        if(!"".equals(orgCode)){
        	detailSql += " AND B.ORG_CODE = '"+orgCode+"'";
        }
        //物资编码
//        if(!"".equals(inv_code)){  
//        	detailSql += " AND T.INV_CODE = '"+inv_code+"'";
//        }
        //供应商
        if(!"".equals(supCode)){
        	detailSql += " AND T.SUP_CODE = '"+supCode+"'";
        }
        //上级供应商
        if(!"".equals(upSupCode)){
        	detailSql += " AND T.UP_SUP_CODE = '"+upSupCode+"'";
        }
        //物资分类
        if(!"".equals(invKindCode)){
        	detailSql += " AND T.INV_KIND = '"+invKindCode+"'";
        }
        //效期
        if(!"".equals(validDate)){
        	detailSql += " AND B.VALID_DATE <= TO_DATE('"+validDate+"','YYYYMMDD')";
        }
      //#4655 liuyl 2016/12/29
        //detailSql += " ORDER BY T.INV_CODE,H.CATEGORY_CHN_DESC ";
        detailSql += " ORDER BY T.INV_CODE,A.CHN_DESC ";
        //测试
        System.out.println("detailSql="+detailSql);
        
        TParm resultParm = new TParm(TJDODBTool.getInstance().select(detailSql));
        
    	//统计条数、总金额
        double allFee = 0.00;
        int count = resultParm.getCount();
        setValue("ALLCOUNT",String.valueOf(count));
        for (int i = 0; i < count; i++) {
        	allFee  += Double.parseDouble(resultParm.getValue("COST_PRICE", i));
		}
        
        DecimalFormat df = new DecimalFormat("0.00");
        String newAllFee = df.format(allFee);
        
//        int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
//        BigDecimal bd = new BigDecimal((double)allFee); 
//        bd = bd.setScale(2,roundingMode); 
//        allFee = bd.doubleValue(); 

		newAllFee = feeConversion(newAllFee);//加逗号处理
        this.setValue("ALLFEE",newAllFee);
        detailTable.setParmValue(resultParm);
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
    	now = now.replaceAll("-", "/");
    	int selectedIndx=this.mainTable.getSelectedRow();
    	//System.out.println("selectedIndx="+selectedIndx);
    	TParm mainTableParm = mainTable.getParmValue();
    	TParm tableParm = detailTable.getParmValue() ;
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
    	
    	if(!"".equals(orgCode) && orgCode!=null){
    		orgCode = mainTableParm.getValue("DEPT_CHN_DESC", selectedIndx);
    	}
    	if(!"".equals(supCode) && supCode!=null){
    		supCode = mainTableParm.getValue("SUP_CHN_DESC", selectedIndx);
    	}
    	if(!"".equals(upSupCode) && upSupCode!=null){
    		upSupCode = mainTableParm.getValue("UP_SUP_CHN_DESC", selectedIndx);
    	}
    	if(!"".equals(kind) && kind!=null){
    		kind = mainTableParm.getValue("INV_KIND_CODE", selectedIndx);
    	}
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		//打印数据
		TParm data = new TParm();
		//表头数据
		data.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"近效期高值报表");
		data.setData("ORG_CODE", "TEXT", "部门名称：" + orgCode);
		data.setData("INV_DESC", "TEXT", "物资名称：" + invDesc);
		data.setData("SUP_CODE", "TEXT", "供应商：" + supCode);
		data.setData("UP_SUP_CODE", "TEXT", "上级供应商：" + upSupCode);
		data.setData("KIND", "TEXT", "物资类别：" + kind);
		data.setData("VALID_DATE", "TEXT", "效期：" + validDate);
		data.setData("ALLCOUNT", "TEXT", "合计笔数：" + allCount);
		data.setData("ALLFEE", "TEXT", "总金额：" + allFee);
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("RFID", tableParm.getValue("RFID", i)); //赋值 
			result.addData("INV_CODE", tableParm.getValue("INV_CODE", i)); 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)+ 
					"("+tableParm.getValue("DESCRIPTION", i)+")"); 
			//result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
			result.addData("COST_PRICE", df.format(tableParm.getDouble("COST_PRICE", i)));
			result.addData("SUP_CHN_DESC", tableParm.getValue("SUP_CHN_DESC", i));
			result.addData("UP_SUP_CHN_DESC", tableParm.getValue("UP_SUP_CHN_DESC", i));
			result.addData("MAN_CODE", tableParm.getValue("MAN_CODE", i));
			result.addData("INV_KIND", tableParm.getValue("INV_KIND", i));
			result.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).substring(0, 10));

		}
		result.setCount(tableParm.getCount("RFID")) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "RFID");//排序
		result.addData("SYSTEM", "COLUMNS", "INV_CODE");
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		//result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
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
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVValidHighList.jhw", data);
    }
    
    /**
     * 汇出方法
     */
    public void onExcel(){
    	//TTable tTable = getTable("tTable");
    	if(detailTable.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(detailTable, "近效期高值报表统计");
    	}else {
         this.messageBox("没有汇出数据");
         return;
     }
    }
    /**
     * 清空方法
     */
    public void onClear() { 
    	//fux modify 20140313
    	getRadioButton("VALID_DATE_E").setSelected(true);
        getTextFormat("VALID_DATE").setEnabled(false);
        this.clearValue("VALID_DATE;INV_CODE;INV_DESC;SUP_CODE;UP_SUP_CODE;KIND;ALLCOUNT;ALLFEE;VALID_DATE_VALUE");
        detailTable.removeRowAll();
        mainTable.removeRowAll();
        
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
