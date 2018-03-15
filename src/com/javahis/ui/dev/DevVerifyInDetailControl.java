package com.javahis.ui.dev;
import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.jdo.TJDODBTool;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import com.javahis.util.ExportExcelUtil;
import java.util.Date;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.dev.DEVTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;

/**
 * <p>Title: 设备入库明细查询</p>
 * 
 * <p>Description:查询</p>
 * 
 * <p>Copyright: Copyright (c) 20130412</p>
 * 
 * <p>Company: ProperSoft </p>
 *  
 * @author  fux
 * 
 * @version 4.0
 */             

public class DevVerifyInDetailControl extends TControl {
	    private TTable table;
	    private static String TABLE = "TABLE";
	    public void onInit() {  
	        super.init();
	        callFunction("UI|DEV_CODE|setPopupMenuParameter", "aaa",
	                     "%ROOT%\\config\\sys\\DEVBASEPopupUI.x");
	        //textfield接受回传值
	        callFunction("UI|DEV_CODE|addEventListener",
	                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	        initpage();
	    }
	    /**
	     * 初始化控件 
	     */      
	    public void initpage() {
	    	String now = StringTool.getString(SystemTool.getInstance().getDate(),
			"yyyyMMdd");
	        this.setValue("S_DATE", StringTool.getTimestamp(now ,
			"yyyyMMdd"));// 开始时间
	        this.setValue("E_DATE", StringTool.getTimestamp(now ,
			"yyyyMMdd"));// 结束时间   
	        String ID = Operator.getID();
	    }

	    /**
	     * 编码接受返回值方法
	     * @param tag String
	     * @param obj Object
	     */
	    public void popReturn(String tag, Object obj) {
	        TParm parm = (TParm) obj;
	        String dev_code = parm.getValue("DEV_CODE");
	        if (!dev_code.equals("")) {
	            getTextField("DEV_CODE").setValue(dev_code);
	        }
	        String dev_desc = parm.getValue("DEV_CHN_DESC");
	        if (!dev_desc.equals("")) {
	            getTextField("DEV_CHN_DESC").setValue(dev_desc);
	        }
	    }
	    /**
	     * 锁住表格不可编辑栏位
	     */
	    public void setTableLock(){
//	        ((TTable)getComponent("TABLE")).setLockColumns("3,4,5,6,8,"+
//	                                                       "9,10,12,16,17,18,"+
//	                                                       "19,20,21,22,"+
//	                                                       "24,25,26,27,28");
	    }
	    
	    
	    /**
	     * 查询数据<br>
	     *
	     */

	    public void onQuery() {
	  //查询条件：入库单号INWAREHOUSE_NO，入库日期：EXWAREHOUSE_DATE between S_DATE and E_DATE，
	  //设备种类DEVKIND_CODE，规格DESCRIPTION，
	  //生产厂商MAN_DESC，入库科室INWAREHOUSE_DEPT，设备编码DEV_CODE，
	  //入库数量QTY，单财产价值UNIT_PRICE	    	
	        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
	        DecimalFormat ff = new DecimalFormat("######0.00");
	        //查询开始日期
	        String s_date = this.getValueString("S_DATE");
	        //查询结束日期
	        String e_date = this.getValueString("E_DATE");
	        //入库单号
	        String inwarehouse_no = this.getValueString("INWAREHOUSE_NO");
	        //入库日期
	        String exwarehouse_date = this.getValueString("INWAREHOUSE_DATE");
	        //入库科室
	        String inwarehouse_dept = this.getValueString("DEPT_CODE");
	        //设备编码
	        String devcode = this.getValueString("DEV_CODE");
	        //设备种类
	        String devkind = this.getValueString("DEVKIND_CODE");
	        //规格
	        String description = this.getValueString("DESCRIPTION");
	        //单财产价值
	        String price = this.getValueString("UNIT_PRICE");
	        //入库数量
	        String qty = this.getValueString("QTY");
	        //生产厂商
	        String mandesc = this.getValueString("MAN_DESC");
	        String sql =
	    		  " SELECT DISTINCT 'N' AS FLG,A.INWAREHOUSE_NO,C.INWAREHOUSE_DEPT," +
	    		  " A.UNIT_PRICE*A.QTY AS TOT_PRICE, " +
	    		  " TO_CHAR(C.INWAREHOUSE_DATE,'YYYYMMDD') INWAREHOUSE_DATE,B.DEVPRO_CODE, " +
	    		  " A.DEV_CODE, B.DEV_CHN_DESC, B.DESCRIPTION, B.MAN_CODE, D.MAN_CHN_DESC, " +
	    		  " A.QTY, 0 SUM_QTY, 0 RECEIPT_QTY, B.UNIT_CODE, A.UNIT_PRICE, " +
	    		  " TO_CHAR(A.MAN_DATE,'YYYYMMDD') MAN_DATE, A.SCRAP_VALUE, " +
	    		  " TO_CHAR(A.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, B.USE_DEADLINE, " +
	    		  " TO_CHAR(A.DEP_DATE,'YYYYMMDD') DEP_DATE, E.CHN_DESC AS MAN_NATION, " +
	    		  " B.SEQMAN_FLG, B.MEASURE_FLG, B.BENEFIT_FLG, " +
	    		  " A.FILES_WAY, A.SEQ_NO,B.SPECIFICATION" +   
	    		  " FROM DEV_INWAREHOUSED A, DEV_BASE B ,DEV_INWAREHOUSEM C ,SYS_MANUFACTURER D,SYS_DICTIONARY E " +
	    		  " WHERE  A.DEV_CODE=B.DEV_CODE " + 
	    		  " AND A.INWAREHOUSE_NO = C.INWAREHOUSE_NO " +      
	    		  " AND  B.MAN_CODE  = D.MAN_CODE " +
	    		  " AND  B.MAN_NATION = E.ID";  
	        StringBuffer SQL = new StringBuffer(); 
	        //System.out.println("sql===="+sql);
	        SQL.append(sql);
	           
	        //入库单号
	        if ( !inwarehouse_no.equals("")){
	            SQL.append(" AND A.INWAREHOUSE_NO='" + inwarehouse_no + "'");
	        }
	        //查询开始日期
	        if ( !s_date.equals("")) {
	        	s_date = s_date.substring(0, 19) ;
	        	SQL.append(" AND TO_CHAR(INWAREHOUSE_DATE,'YYYY-MM-DD ') >= '"+s_date+"'") ;
	        }
	        //查询结束日期
             if ( !e_date.equals("")) {
            	e_date = e_date.substring(0, 19) ;
 	    		SQL.append(" AND TO_CHAR(INWAREHOUSE_DATE,'YYYY-MM-DD  ') <= '"+e_date+"'") ;
	        }
	        //入库科室
	        if (!inwarehouse_dept.equals("")) {
	            SQL.append(" AND C.INWAREHOUSE_DEPT='" + inwarehouse_dept + "'");
	        }
	        //设备编码
	        if (!devcode.equals("")) {
	            SQL.append(" AND A.DEV_CODE='" + devcode + "'");
	        }
	        //设备种类
	        if (!devkind.equals("")) {
	            SQL.append(" AND B.DEVKIND_CODE='" + devkind + "'");
	        }
	        //规格
	        if(!description.equals("")){
	        	SQL.append("AND　B.DESCRIPTION='"+description+"'");
	        }
	        //单财产价值
	        if(!price.equals("")){   
	        	SQL.append("AND A.UNIT_PRICE like '%"+price+"%'");
	        }
	        //库存量
	        if(!qty.equals("")){
	        	SQL.append("AND A.QTY like '%"+qty+"%'"); 
	        }
	        //生产厂商
	        if(!mandesc.equals("")){
	        	SQL.append("AND E.MAN_CHN_DESC like '"+mandesc+"'");
	        } 
	        System.out.println("SQl===="+SQL);
	        //TParm result1 = new TParm(TJDODBTool.getInstance().select(SQL.toString()));
	        TParm result = new TParm(this.getDBTool().select(SQL.toString()));
	        //System.out.println("result===="+result);
	        // 判断错误值
	        if (result == null || result.getCount() <= 0) {
	            callFunction("UI|TABLE|removeRowAll");
	            this.messageBox("没有查询数据");
	            return;          
	        }
	        this.callFunction("UI|TABLE|setParmValue", result);
	    }
	   
	    /** 
	     * 清空数据<br>
	     *
	     */
	    public void onClear() {
	        if (this.getTable("TABLE").getRowCount() > 0) {
	            callFunction("UI|TABLE|removeRowAll");
	        }

	        this.clearValue(
	                "INWAREHOUSE_NO;DEPT_CODE;DEV_CODE;DEVKIND_CODE;S_DATE;" +
	                "E_DATE;DEV_CHN_DESC;DESCRIPTION;MAN_DESC;QTY;UNIT_PRICE");
	        callFunction("UI|TABLE|removeRowAll");
	    }

	    /**
	     * 导出Excel
	     */
	    public void onExport() {
	        if (this.getTable("TABLE").getRowCount() > 0) {
	            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
	                    "设备入库细项统计报表");
	        }
	    }

	    /**
	     * 打印 
	     */
	    public void onPrint() {
	        if (this.getTable("TABLE").getRowCount() <= 0) {
	            this.messageBox("没有要打印的数据");
	            return;
	        }
	        TParm prtParm = new TParm();
	        //表头
	        prtParm.setData("TITLE","TEXT","设备入库细项统计报表");
	        //日期
	        prtParm.setData("PRINT_DATE","TEXT","打印日期：" +
	                        StringTool.getString(StringTool.getTimestamp(new Date()),
	                                             "yyyy年MM月dd日"));
	        TParm parm = this.getTable("TABLE").getParmValue();
	        TParm prtTableParm=new TParm();
	        for(int i=0;i<parm.getCount("DEV_CODE");i++){
	            prtTableParm.addData("DEV_CHN_DESC",parm.getRow(i).getValue("DEV_CHN_DESC"));
	            prtTableParm.addData("DESCRIPTION",parm.getRow(i).getValue("DESCRIPTION"));
	            prtTableParm.addData("QTY",parm.getRow(i).getValue("QTY"));
	            prtTableParm.addData("UNIT_DESC",parm.getRow(i).getValue("UNIT_DESC"));
	            prtTableParm.addData("UNIT_PRICE",parm.getRow(i).getValue("UNIT_PRICE"));
	            prtTableParm.addData("SCRAP_VALUE",parm.getRow(i).getValue("SCRAP_VALUE"));
	            prtTableParm.addData("LOC_CODE",parm.getRow(i).getValue("LOC_CODE"));
	            prtTableParm.addData("TOT",parm.getRow(i).getValue("TOT"));
	            prtTableParm.addData("DEP_TOT",parm.getRow(i).getValue("DEP_TOT"));
	            prtTableParm.addData("BALANCE_TOT",parm.getRow(i).getValue("BALANCE_TOT"));
	        }
	        prtTableParm.setCount(prtTableParm.getCount("DEV_CHN_DESC"));
	        prtTableParm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "QTY");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "UNIT_DESC");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "SCRAP_VALUE");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "LOC_CODE");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "TOT");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "DEP_TOT");
	        prtTableParm.addData("SYSTEM", "COLUMNS", "BALANCE_TOT");
	        prtParm.setData("prtTABLE", prtTableParm.getData());
	        //表尾
	        prtParm.setData("USER","TEXT", "制表人：" + Operator.getName());
	        this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEV_VerifyInDetail.jhw",
	                             prtParm);
	    }

	    /** 
	     * 得到表控件
	     * @param tagName String
	     * @return TTable
	     */
	    private TTable getTable(String tagName) {
	        return (TTable) getComponent(tagName);
	    }

	    /**
	     * 得到文本控件
	     * @param tagName String
	     * @return TTextField
	     */
	    private TTextField getTextField(String tagName) {
	        return (TTextField) getComponent(tagName);
	    }

	    /**
	     * getDBTool
	     * 数据库工具实例
	     * @return TJDODBTool
	     */
	    public TJDODBTool getDBTool() {
	        return TJDODBTool.getInstance();
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
	   /**
	     * 得到TNumberTextField对象
	     * @param tagName String
	     * @return TNumberTextField
	     */
	    private TNumberTextField getNumberTextField(String tagName) {
	        return (TNumberTextField) getComponent(tagName);
	    }
	    /**
	     * 得到TTextFormat对象
	     * @param tagName String
	     * @return TCheckBox
	     */
	    private TTextFormat getTextFormat (String tagName) {
	        return (TTextFormat) getComponent(tagName);
	    }
	    /**
	    * 得到TCheckBox对象
	    * @param tagName String
	    * @return TCheckBox
	    */
	   private TCheckBox getCheckBox(String tagName) {
	       return (TCheckBox) getComponent(tagName);
	   }

}
