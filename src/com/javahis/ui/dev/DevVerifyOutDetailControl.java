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
 * <p>Title: 设备出库明细查询</p>
 * 
 * <p>Description:查询</p>
 * 
 * <p>Copyright: Copyright (c) 20120912</p>
 * 
 * <p>Company: BLUECORE </p>
 *     
 * @author  fux
 * 
 * @version 4.0  
 */

public class DevVerifyOutDetailControl extends TControl {
	    private TTable table;
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
	    	
//	无序号管理		  
//	    	  " SELECT B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, 0 DEVSEQ_NO, " +
//			  " B.DEV_CHN_DESC, B.DESCRIPTION, '' SETDEV_CODE , A.QTY STOREQTY, " +
//			  " B.UNIT_CODE, B.UNIT_PRICE, '' CARE_USER, '' USE_USER, '' LOC_CODE, " +
//			  " A.SCRAP_VALUE, TO_CHAR(A.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, TO_CHAR(A.DEP_DATE,'YYYYMMDD') DEP_DATE, A.DEPT_CODE, TO_CHAR(A.MAN_DATE,'YYYYMMDD') MAN_DATE,B.DEV_PYCODE,A.BATCH_CODE,A.SUP_CODE " +
//			  " FROM DEV_STOCKM A,DEV_BASE B " +
//			  " WHERE B.SEQMAN_FLG='N' " +
//			  " <DEV_CODE> " +
//			  " <DEVSEQ_NO> " +//ADD BY LIUC 20070917
//			  " <DEVPRO_CODE> " +
//			  " AND A.HOSP_AREA=B.HOSP_AREA " +
//			  " AND A.DEV_CODE=B.DEV_CODE " +
//			  " AND A.DEPT_CODE='<DEPT_CODE>'" +
//			  " ORDER BY B.DEV_CODE, A.BATCH_SEQ";//ADD BY LIUC 20070816
	    	
//	有序号管理		  
//	    	  " SELECT B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, A.DEVSEQ_NO DEVSEQ_NO, " +
//			  " B.DEV_CHN_DESC, B.DESCRIPTION, A.SETDEV_CODE , A.QTY STOREQTY, " +
//			  " B.UNIT_CODE, A.UNIT_PRICE, A.CARE_USER, A.USE_USER, A.LOC_CODE, " +
//			  " A.SCRAP_VALUE, TO_CHAR(A.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, TO_CHAR(A.DEP_DATE,'YYYYMMDD') DEP_DATE, A.DEPT_CODE, TO_CHAR(A.MAN_DATE,'YYYYMMDD') MAN_DATE,B.DEV_PYCODE,A.BATCH_CODE,A.SUP_CODE " +
//			  " FROM DEV_STOCKD A,DEV_BASE B " +
//			  " WHERE B.SEQMAN_FLG='Y' " +
//			  " <DEV_CODE> " +
////			  " <DEVSEQ_NO> " +//ADD BY LIUC 20070917
//			  " <DEVPRO_CODE> " +
//			  " AND A.HOSP_AREA=B.HOSP_AREA " +
//			  " AND A.DEV_CODE=B.DEV_CODE " +
//			  " AND A.DEPT_CODE='<DEPT_CODE>'" +
//			  " ORDER BY B.DEV_CODE, A.BATCH_SEQ, A.DEVSEQ_NO";//add by liuc 20070816
	    	
//			  " SELECT B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, A.DEVSEQ_NO, " +
//			  " B.DEV_CHN_DESC, B.DESCRIPTION, C.SETDEV_CODE, A.QTY, C.QTY STOREQTY, " +
//			  " B.UNIT_CODE, C.UNIT_PRICE, A.CARE_USER, A.USE_USER, A.LOC_CODE, " +
//			  " C.SCRAP_VALUE, TO_CHAR(C.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, TO_CHAR(C.DEP_DATE,'YYYYMMDD') DEP_DATE,TO_CHAR(C.MAN_DATE,'YYYYMMDD') MAN_DATE, A.SEQ_NO, A.REMARK1, A.REMARK2,A.BATCH_CODE,A.SUP_CODE " +//MOD BY LIUC 200700918
//			  " FROM DEV_EXWAREHOUSEM M, DEV_EXWAREHOUSED A, DEV_BASE B, DEV_STOCKD C  " +
//			  " WHERE M.HOSP_AREA = '<HOSP_AREA>' " +
//			  " AND M.EXWAREHOUSE_NO='<EXWAREHOUSE_NO>' " +
//			  " AND B.SEQMAN_FLG='Y' " +
//			  " AND A.HOSP_AREA=M.HOSP_AREA " +
//			  " AND A.EXWAREHOUSE_NO = M.EXWAREHOUSE_NO " +
//			  " AND B.HOSP_AREA=A.HOSP_AREA " +
//			  " AND A.HOSP_AREA=C.HOSP_AREA " +
//			  " AND B.DEV_CODE=A.DEV_CODE " +
//			  " AND A.DEV_CODE=C.DEV_CODE " +
//			  " AND A.BATCH_SEQ=C.BATCH_SEQ " +
//			  " AND A.DEVSEQ_NO=C.DEVSEQ_NO " +
//			  " AND C.DEPT_CODE=M.INWAREHOUSE_DEPT " +
//			  " UNION " +
//			  " SELECT B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, 0 DEVSEQ_NO, " +
//			  " B.DEV_CHN_DESC, B.DESCRIPTION, '' SETDEV_CODE, A.QTY, C.QTY STOREQTY, " +
//			  " B.UNIT_CODE, C.UNIT_PRICE, '' CARE_USER, '' USE_USER, '' LOC_CODE, " +
//			  " C.SCRAP_VALUE, TO_CHAR(C.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, TO_CHAR(C.DEP_DATE,'YYYYMMDD') DEP_DATE,TO_CHAR(C.MAN_DATE,'YYYYMMDD') MAN_DATE, A.SEQ_NO, A.REMARK1, A.REMARK2,A.BATCH_CODE,A.SUP_CODE " +//MOD BY LIUC 200700918
//			  " FROM DEV_EXWAREHOUSEM M, DEV_EXWAREHOUSED A,DEV_BASE B, DEV_STOCKM C " +
//			  " WHERE M.HOSP_AREA = '<HOSP_AREA>' " +
//			  " AND M.EXWAREHOUSE_NO='<EXWAREHOUSE_NO>' " +
//			  " AND B.SEQMAN_FLG='N' " +
//			  " AND A.HOSP_AREA=M.HOSP_AREA " +
//			  " AND A.EXWAREHOUSE_NO = M.EXWAREHOUSE_NO " +
//			  " AND B.HOSP_AREA=A.HOSP_AREA " +
//			  " AND A.HOSP_AREA=C.HOSP_AREA " +
//			  " AND B.DEV_CODE=A.DEV_CODE " +
//			  " AND A.DEV_CODE=C.DEV_CODE " +
//			  " AND A.BATCH_SEQ=C.BATCH_SEQ " +
//			  " AND C.DEPT_CODE=M.INWAREHOUSE_DEPT ";
	    	
	    	//查询条件：出库单号EXWAREHOUSE_NO，存放地点LOC_CODE，设备种类DEVKIND_CODE，
	    	//出库日期EXWAREHOUSE_DATE，规格DESCRIPTION
	    	//出库科室EXWAREHOUSE_DEPT，设备编码DEV_CODE，库存量STOREQTY，
	    	//单财产价值UNIT_PRICE，生产厂商MAN_DESC
	        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
	        DecimalFormat ff = new DecimalFormat("######0.00");
	        //查询开始日期
	        String s_date = this.getValueString("S_DATE");
	        //查询结束日期
	        String e_date = this.getValueString("E_DATE");
	        //存放地点
	        String loccode = this.getValueString("LOC_CODE");
	        //出库单号
	        String inwarehouse_no = this.getValueString("EXWAREHOUSE_NO");
	        //出库日期
	        String exwarehouse_date = this.getValueString("EXWAREHOUSE_DATE");
	        //出库科室
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
	        //确认表以后修改！
	        String sql = 
//                " SELECT 'N' AS FLG,M.EXWAREHOUSE_NO,M.EXWAREHOUSE_DEPT,D.COST_CENTER_CODE,TO_CHAR(M.EXWAREHOUSE_DATE,'YYYYMMDD') EXWAREHOUSE_DATE ,"+  
//                " B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, A.SEQ_NO,"+   
//	        	" B.DEV_CHN_DESC, B.DESCRIPTION,B.SETDEV_CODE, A.QTY, C.QTY STOREQTY,"+   
//	        	" B.UNIT_CODE, C.UNIT_PRICE,C.UNIT_PRICE*A.QTY AS TOT_PRICE , C.CARE_USER, C.USE_USER, C.LOC_CODE,"+   
//	        	" C.SCRAP_VALUE, TO_CHAR(C.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE,"+   
//	        	" TO_CHAR(C.DEP_DATE,'YYYYMMDD') DEP_DATE,TO_CHAR(C.MAN_DATE,'YYYYMMDD') MAN_DATE,"+   
//	        	" A.SEQ_NO, A.REMARK1, A.REMARK2"+
//	        	" FROM DEV_EXWAREHOUSEM M, DEV_EXWAREHOUSED A,DEV_BASE B, DEV_STOCKD C ,SYS_COST_CENTER D"+   
//	        	" WHERE A.EXWAREHOUSE_NO = M.EXWAREHOUSE_NO"+   
//	        	" AND B.DEV_CODE=A.DEV_CODE "+  
//	        	" AND A.DEV_CODE=C.DEV_CODE "+ 
//	        	" AND A.BATCH_SEQ=C.BATCH_SEQ "+   
//	        	" AND C.DEPT_CODE=M.INWAREHOUSE_DEPT "+  
//	        	" AND D.COST_CENTER_CODE=M.INWAREHOUSE_DEPT "; 
                " SELECT 'N' AS FLG,M.EXWAREHOUSE_NO,M.EXWAREHOUSE_DEPT,D.COST_CENTER_CODE,TO_CHAR(M.EXWAREHOUSE_DATE,'YYYYMMDD') EXWAREHOUSE_DATE ,"+  
                " B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.SEQ_NO,"+   
	        	" B.DEV_CHN_DESC, B.DESCRIPTION,B.SETDEV_CODE, A.QTY, C.QTY STOREQTY,"+   
	        	" B.UNIT_CODE, C.UNIT_PRICE,C.UNIT_PRICE*A.QTY AS TOT_PRICE , C.CARE_USER, C.USE_USER, C.LOC_CODE,"+   
	        	" C.SCRAP_VALUE, " +
	        	//" TO_CHAR(C.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE,"+   
	        	//" TO_CHAR(C.DEP_DATE,'YYYYMMDD') DEP_DATE," +
	        	" TO_CHAR(C.MAN_DATE,'YYYYMMDD') MAN_DATE,"+                     
	        	" A.SEQ_NO, A.REMARK1, A.REMARK2"+
	        	" FROM DEV_EXWAREHOUSEM M, DEV_EXWAREHOUSED A,DEV_BASE B, DEV_STOCKD C ,SYS_COST_CENTER D"+   
	        	" WHERE A.EXWAREHOUSE_NO = M.EXWAREHOUSE_NO"+   
	        	" AND B.DEV_CODE=A.DEV_CODE "+                 
	        	" AND A.DEV_CODE=C.DEV_CODE "+  
	        	" AND C.DEPT_CODE=M.INWAREHOUSE_DEPT "+  
	        	" AND D.COST_CENTER_CODE=M.INWAREHOUSE_DEPT "; 
	        System.out.println("sql==="+sql);                                                               
	        StringBuffer SQL = new StringBuffer();   
	        SQL.append(sql);
	        //System.out.println("SQL==="+SQL);
	        //出库单号
	        if ( !inwarehouse_no.equals("")){
	            SQL.append(" AND A.EXWAREHOUSE_NO='" + inwarehouse_no + "'");
	        }
	        //存放地点
	        if ( !loccode.equals("")){
	            SQL.append(" AND A.LOC_CODE='" + loccode + "'");
	        }
	        //查询开始日期
	        if ( !s_date.equals("")) {
	        	s_date = s_date.substring(0, 19) ;
	        	SQL.append(" AND TO_CHAR(M.EXWAREHOUSE_DATE,'YYYY-MM-DD') >= '"+s_date+"'") ;
	        }
	        //查询结束日期
             if ( !e_date.equals("")) {
            	e_date = e_date.substring(0, 19) ;
 	    		SQL.append(" AND TO_CHAR(M.EXWAREHOUSE_DATE,'YYYY-MM-DD') <= '"+e_date+"'") ;
	        }
	        //出库科室
	        if (!inwarehouse_dept.equals("")) {
	            SQL.append("AND A.EXWAREHOUSE_DEPT='" + inwarehouse_dept + "'");
	        }
	        //设备编码
	        if (!devcode.equals("")) {
	            SQL.append("AND A.DEV_CODE='" + devcode + "'");
	        }
	        //设备种类
	        if (!devkind.equals("")) {
	            SQL.append("AND B.DEVKIND_CODE='" + devkind + "'");
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
	        	SQL.append("AND B.MAN_CODE like '%"+mandesc+"%'");
	        }  
	        TParm result = new TParm(this.getDBTool().select(SQL.toString()));
	        System.out.println("result=="+result);
	        // 判断错误值
	        if (result == null || result.getCount() <= 0) {
	            callFunction("UI|TABLE|removeRowAll");
	            this.messageBox("没有查询数据");
	            return; 
	        }
	        this.callFunction("UI|TABLE|setParmValue", result);
	    }
	   /**
	    * 主表单击事件  
	    */
	   public void onTableClicked() {
	       int row =  this.getTable("TABLE").getClickedRow();
	       TParm parm = this.getTable("TABLE").getParmValue().getRow(row);
	   }
	    /**
	     * 清空数据<br>
	     */
	    public void onClear() {
	        if (this.getTable("TABLE").getRowCount() > 0) {
	            callFunction("UI|TABLE|removeRowAll");
	        }

	        this.clearValue(
	        		 "EXWAREHOUSE_NO;DEPT_CODE;DEV_CODE;DEVKIND_CODE;S_DATE;" +
                     "E_DATE;DEV_CHN_DESC;DESCRIPTION;MAN_DESC;QTY;UNIT_PRICE;LOC_CODE");
	        callFunction("UI|TABLE|removeRowAll");
	    }

	    /**
	     * 导出Excel
	     */
	    public void onExport() {
	        if (this.getTable("TABLE").getRowCount() > 0) {
	            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
	                    "设备出库细项统计报表");
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
	        prtParm.setData("TITLE","TEXT","设备库存余额统计报表");
	        //日期
	        prtParm.setData("PRINT_DATE","TEXT","打印日期：" +
	                        StringTool.getString(StringTool.getTimestamp(new Date()),
	                                             "yyyy年MM月dd日"));
	        String sql="SELECT * FROM SYS_DEPT WHERE DEPT_CODE='"+this.getValue("DEPT_CODE")+"'";
	        TParm result = new TParm(this.getDBTool().select(sql));
	        //科室
	        prtParm.setData("DEPT_CODE","TEXT", "统计部门：" +result.getValue("DEPT_DESC",0));
	        //合计笔数
	        prtParm.setData("NUMBER","TEXT", "合计笔数：" +this.getValueInt("NUMBER"));
	        //差额总计
	        prtParm.setData("BALANCE_VALUE","TEXT", "差额总计：" +this.getValueDouble("BALANCE_VALUE"));
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
	        this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEV_VerifyOutDetail.jhw",
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
