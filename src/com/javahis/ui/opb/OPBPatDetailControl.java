/**
 * 门诊病患明细报表
 * 
 * @param mrno
 * String 病案号
 * @return TParm <PAT_NAME>,<IDNO>
 */
package com.javahis.ui.opb;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

public class OPBPatDetailControl extends TControl{
	private TTable table;
	/**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    
	/**
     * 初始画面数据
     */ 
    private void initPage() {
        //Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("S_DATE",
                      getDateForInit(queryFirstDayOfLastMonth(StringTool.
                      getString(SystemTool.getInstance().getDate(), "yyyyMMdd"))));
                      Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()), -1);
        this.setValue("E_DATE", rollDay);
        // 初始化TABLE
        table = getTable("TABLE");
       
    }
    public void onQuery(){
    	String mrno = this.getValueString("MR_NO");
    	String sDate = this.getValueString("S_DATE");
    	String eDate = this.getValueString("E_DATE");
    	String CHARGE_DATE = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("-", "").
          replace(":", "");
    	CHARGE_DATE +=  " AND CHARGE_DATE > TO_DATE(" + sDate +
        ",'YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("-", "").
          replace(":", "");
    	CHARGE_DATE +=  " AND CHARGE_DATE < TO_DATE(" + eDate +
        ",'YYYYMMDDHH24MISS') ";
    	String selSql = " SELECT B.PAT_NAME,A.MR_NO,CHARGE_DATE,TOT_AMT  " +
    			        " FROM BIL_OPB_RECP A,SYS_PATINFO B " +
    			        " WHERE A.MR_NO = B.MR_NO           " +
    			          CHARGE_DATE+
    			        " AND RESET_RECEIPT_NO IS NULL                   " +
    			        " AND TOT_AMT >0 " ;
    	System.out.println("selSql==="+selSql); 
    	TParm result = new TParm(TJDODBTool.getInstance().select(selSql));
    	//System.out.println("result==="+result);
    	if(result.getErrCode() < 0 ){
    		this.messageBox(result.getErrText());
    		return;
    	}
        if(result.getCount() <= 0)
        {
        	this.messageBox("查无数据");
        }
        this.callFunction("UI|TABLE|setParmValue",result);
    }
    
    
    
    
    
    /**
     * 查询
     */ 
      public void onQuery2(){                   
    	  String mrno = this.getValueString("MR_NO");            
    	  String sDate = this.getValueString("S_DATE");
    	  String eDate = this.getValueString("E_DATE");
    	  String sql = "SELECT B.PAT_NAME,A.MR_NO,CHARGE_DATE,TOT_AMT          "+
    	               "FROM BIL_OPB_RECP A,SYS_PATINFO B                      "+
    	               "WHERE  A.MR_NO =B.MR_NO                                "+
//    	               "AND CHARGE_DATE   BETWEEN TO_DATE ('20120326 00:00:00', "+
//    	               "             'yyyymmdd hh24:mi:ss ') "+
//    	               "                      AND TO_DATE ('20120425 23:59:59', "+
//    	               "             'yyyymmdd hh24:mi:ss ') "+
    	               "AND RESET_RECEIPT_NO IS NULL  "+
    	               "AND TOT_AMT >0                ";
    	  StringBuilder sbuilder = new StringBuilder(sql);
    	  System.out.println("sql=="+sql);
    	  if(mrno!=null && !"".equals(mrno)){//病案号
      		sbuilder.append(" AND A.MR_NO= '"+mrno+"' ") ;
          	}  
    	  if((sDate!=null && !"".equals(sDate)))
    	  { sDate = sDate.substring(0, 19) ;
    	    sbuilder.append(" AND TO_CHAR(CHARGE_DATE,'YYYY-MM-DD HH:mm:ss') >= '"+sDate+"'");
    	  }
    	  if((eDate!=null && !"".equals(eDate)))
    	  { eDate = eDate.substring(0, 19) ;
    	    sbuilder.append(" AND TO_CHAR(CHARGE_DATE,'YYYY-MM-DD HH:mm:ss') <= '"+eDate+"'");
    	  }
    	    sbuilder.append(" ORDER BY CHARGE_DATE ") ;
    	  TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.toString()));
    	//   获得错误信息消息
      	  if (result.getErrCode() < 0) {
      	      messageBox(result.getErrText());
      	      return;      
      	   }
      	   if (result.getCount() <= 0) {
               messageBox("查无数据");
               this.callFunction("UI|TABLE|setParmValue", new TParm());
               return;
           }       	
    	  this.callFunction("UI|TABLE|setParmValue", result); 
    	  //TTable table =this.getTTable("TABLE");
    	  //table = (TTable) getComponent("TABLE");
    	  //table.setParmValue(result);
  }

     /**
      * 打印
      */
 	public void onPrint () {
 		//系统日期
    	Timestamp P_DATE = SystemTool.getInstance().getDate();
    	//系统日期加入具体时间
 		String P_DATE_FORMAT = StringTool.getString(P_DATE,"yyyy/MM/dd HH:mm:ss") ;
        if (this.table.getRowCount() <= 0) {
            this.messageBox("没有要打印的数据");
            return;
        }
        double allMoney = 0.00;
        //parm 取table值
        TParm parm = table.getParmValue();
        TParm printData = new TParm();
        for(int i=0 ; i<parm.getCount("MR_NO");i++)
        {  
        //PAT_NAME;MR_NO;CHARGE_DATE;TOT_AMT
        //printData.addData("APPLY_AMT",parm.getValue("APPLY_AMT",i));
        	printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
            printData.addData("MR_NO", parm.getValue("MR_NO", i));
        	printData.addData("CHARGE_DATE",StringTool.getString(parm.getTimestamp("CHARGE_DATE", i), "yyyy/MM/dd"));
        	printData.addData("TOT_AMT", parm.getValue("TOT_AMT", i));
        	allMoney += parm.getDouble("TOT_AMT",i);
        	//保留两位小数
        	allMoney = StringTool.round(allMoney, 2);
        } 
        //加入一行
        printData.addData("PAT_NAME", "总计");
    	printData.addData("MR_NO", "");
    	printData.addData("CHARGE_DATE","");
    	printData.addData("TOT_AMT",allMoney);
    	//设置总行数（i行加1）
    	printData.setCount(parm.getCount()+1);
    	//行赋值
    	printData.addData("SYSTEM", "COLUMNS","PAT_NAME");
    	printData.addData("SYSTEM", "COLUMNS","MR_NO");
    	printData.addData("SYSTEM", "COLUMNS","CHARGE_DATE");
    	printData.addData("SYSTEM", "COLUMNS","TOT_AMT");
    	TParm result = new TParm();
    	result.setData("T1", printData.getData());
    	result.setData("TITLE","TEXT","门诊病患明细报表");
        result.setData("P_DATE","TEXT",P_DATE_FORMAT);
    	result.setData("USER", "TEXT", "制表人：" + Operator.getName());
    	//System.out.println("result==="+result);
        this.openPrintWindow("%ROOT%\\config\\prt\\opb\\OPBPatDetail.jhw",result);
  	}  
      /**
       * 清空方法   
       */
      public void onClear() {
          String clearString ="S_DATE;E_DATE;MR_NO" ;
          this.clearValue(clearString);
          Timestamp date = StringTool.getTimestamp(new Date());
          // 初始化查询区间 
          this.setValue("S_DATE",
                  getDateForInit(queryFirstDayOfLastMonth(StringTool.
                  getString(SystemTool.getInstance().getDate(), "yyyyMMdd"))));
                  Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()), -1);
          this.setValue("E_DATE", rollDay);
          table.removeRowAll(); 
          }
      /**
       * 表格单击事件
       */ 
       public void onTableClicked() {
    	 int row = table.getSelectedRow();
         if (row != -1) {
             this.setValue("MR_NO", table.getItemData(row, "MR_NO"));
             }
         }  
    /**
     * 汇出Excel
     */
      public void onExport() {

          //得到UI对应控件对象的方法（UI|XXTag|getThis）
          TTable table = (TTable) callFunction("UI|Table|getThis");
          ExportExcelUtil.getInstance().exportExcel(table, "门诊病患明细报表");
      }

  	/**
  	 * 得到Table对象
  	 *
  	 * @param tagName
  	 *            元素TAG名称
  	 * @return
  	 */
  	private TTable getTable(String tagName) {
  	  return (TTable) getComponent(tagName);
  	}
  	
  	/**
  	 * 拿到TABLE
  	 * 
  	 * @param tag
  	 *            String
  	 * @return TTable
  	 */
  	public TTable getTTable(String tag) {
      return (TTable) this.getComponent(tag);
  	
  	
  	}
	/**
  	 * 病案号补零
  	 */
  	public void onMrNoAction(){
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        this.setValue("MR_NO", mr_no) ;
	}
//	/**
//	 * 检测病案号长度
//	 * 
//	 * @param mrno
//	 *            String
//	 * @return String
//	 */
//	public String checkMrno(String mrno) {
//		int mrnoLength = getMrNoLength();
//		if (mrnoLength <= 0)
//			mrnoLength = mrno.length();
//		return StringTool.fill("0", mrnoLength - mrno.length()) + mrno;
//	}
//
//	/**
//	 * 得到病案号的长度
//	 * 
//	 * @return int
//	 */
//	public int getMrNoLength() {
//		return getResultInt(query("getMrNoLength"), "MRNO_LENGTH");
//	}
    /**
     * 得到上个月
     * @param dateStr String
     * @return Timestamp
     */
    public Timestamp queryFirstDayOfLastMonth(String dateStr) {
        DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
        Date d = null;
        try {
            d = defaultFormatter.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return StringTool.getTimestamp(cal.getTime());
    }
	
	
    /**
     * 初始化时间整理
     * @param date Timestamp
     * @return Timestamp
     */
    public Timestamp getDateForInit(Timestamp date) {
        String dateStr = StringTool.getString(date, "yyyyMMdd");
        TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
        int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
        String monThCycle = "" + monthM;
        dateStr = dateStr.substring(0, 6) + monThCycle;
        Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
        return result;
    }
  }

      
      

