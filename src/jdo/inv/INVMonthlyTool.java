package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

public class INVMonthlyTool extends TJDOTool
{
  public static INVMonthlyTool instanceObject;
  
  public static INVMonthlyTool getInstance()
  {  
    if (instanceObject == null)
      instanceObject = new INVMonthlyTool();
    return instanceObject;
  }

  public INVMonthlyTool()  
  {
    onInit();
  } 

  public TParm onSaveMonthlyInfo(TParm parm, TConnection conn)
  {
    String year = parm.getData("YEAR", 0).toString();
    String month = parm.getData("MONTH", 0).toString();

    String lastYear = "";
    String lastMonth = "";
    
    if (month.equals("01")) {
      lastYear = String.valueOf(Integer.parseInt(year) - 1);
      lastMonth = "12";
    } else {
      lastYear = year;
      if (Integer.parseInt(month) <= 10)
        lastMonth = "0" + String.valueOf(Integer.parseInt(month) - 1);
      else {
        lastMonth = String.valueOf(Integer.parseInt(month) - 1);
      }
    }

    Timestamp date = SystemTool.getInstance().getDate();

    String startDate = lastYear + "-" + lastMonth + "-" + "26 00:00:00 ";
    String endDate = year + "-" + month + "-" + "25 23:59:59 ";

    TParm incomeTP = new TParm(TJDODBTool.getInstance().select(getIncomeAMTSQL(startDate, endDate)));
    //System.out.println("111111"+getExpAMTSQL(startDate, endDate));
    for (int i = 0; i < incomeTP.getCount("ORG_CODE"); i++) {
      incomeTP.setData("OPT_DATE", i, date.toString().substring(0, 19));
      incomeTP.setData("OPT_USER", i, Operator.getID());
      incomeTP.setData("OPT_TERM", i, Operator.getIP()); 
      incomeTP.setData("ACC_TYPE", i, "IN");
      incomeTP.setData("ACC_DATE", i, year + month);
    }
    
    //获得编码？？？
    TParm accCode = new TParm(TJDODBTool.getInstance().select(getMainAccSubjectCodeSql()));  
    //卫生材料
    TParm expTP = new TParm(TJDODBTool.getInstance().select(getExpAMTSQL(startDate, endDate)));
    //System.out.println("222222"+getExpAMTSQL(startDate, endDate));
    for (int i = 0; i < expTP.getCount("ORG_CODE"); i++) {
      expTP.setData("OPT_DATE", i, date.toString().substring(0, 19));
      expTP.setData("OPT_USER", i, Operator.getID());
      expTP.setData("OPT_TERM", i, Operator.getIP());
      expTP.setData("ACC_TYPE", i, "OUT");
      expTP.setData("ACC_DATE", i, year + month);
    }
    System.out.println("expTP"+expTP);
    //其他材料(02),维修材料(03),低值易耗品(04)  的科室是写死的？
    //其他材料
    TParm expResearchTP = new TParm(TJDODBTool.getInstance().select(getExpResearchAMTSQL(startDate, endDate)));
    //System.out.println("333333"+getExpResearchAMTSQL(startDate, endDate));
    for (int i = 0; i < expResearchTP.getCount("MONTH_SUM"); i++) {
      expResearchTP.setData("OPT_DATE", i, date.toString().substring(0, 19));
      expResearchTP.setData("OPT_USER", i, Operator.getID());
      expResearchTP.setData("OPT_TERM", i, Operator.getIP());
      expResearchTP.setData("ACC_TYPE", i, "OUT");
      expResearchTP.setData("ACC_DATE", i, year + month);
      expResearchTP.setData("ORG_CODE", i, "KYZC");
    }
    
    //维修材料
    TParm expDonationTP = new TParm(TJDODBTool.getInstance().select(getExpDonationAMTSQL(startDate, endDate)));
    //System.out.println("444444"+getExpDonationAMTSQL(startDate, endDate)); 
    for (int i = 0; i < expResearchTP.getCount("MONTH_SUM"); i++) {
      expResearchTP.setData("OPT_DATE", i, date.toString().substring(0, 19));
      expResearchTP.setData("OPT_USER", i, Operator.getID());
      expResearchTP.setData("OPT_TERM", i, Operator.getIP());
      expResearchTP.setData("ACC_TYPE", i, "OUT");
      expResearchTP.setData("ACC_DATE", i, year + month);
      expResearchTP.setData("ORG_CODE", i, "JZZC");
    }  
    
    //低值易耗品
    TParm LowerTP = new TParm(TJDODBTool.getInstance().select(getExpLowerAMTSQL(startDate, endDate)));
    //System.out.println("555555"+getExpLowerAMTSQL(startDate, endDate));  
    for (int i = 0; i < LowerTP.getCount("MONTH_SUM"); i++) { 
    	LowerTP.setData("OPT_DATE", i, date.toString().substring(0, 19));
    	LowerTP.setData("OPT_USER", i, Operator.getID());
    	LowerTP.setData("OPT_TERM", i, Operator.getIP());
    	LowerTP.setData("ACC_TYPE", i, "OUT");
    	LowerTP.setData("ACC_DATE", i, year + month); 
    	LowerTP.setData("ORG_CODE", i, "LVCG"); 
    }  
    
    TParm result = new TParm();
    //System.out.println("count1:"+ incomeTP.getCount("ORG_CODE"));
    //有数据方可 进行
    if(incomeTP.getCount("ORG_CODE")>0){
    for (int i = 0; i < incomeTP.getCount("ORG_CODE"); i++) {  
    	//System.out.println("插入月结语句1"+getInsertDeptDDAccSQL(incomeTP.getRow(i)));
      result = new TParm(TJDODBTool.getInstance().update(getInsertDeptDDAccSQL(incomeTP.getRow(i)), conn));
      if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
        return result; 
      }  
    }  
    }
    //System.out.println("count2:"+expTP.getCount("ORG_CODE"));
    //有数据方可 进行
    if(expTP.getCount("ORG_CODE")>0){
    for (int i = 0; i < expTP.getCount("ORG_CODE"); i++) { 
    	//System.out.println("插入月结语句2"+getInsertDeptDDAccSQL(expTP.getRow(i)));
      result = new TParm(TJDODBTool.getInstance().update(getInsertDeptDDAccSQL(expTP.getRow(i)), conn));
      if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
        return result;
      }  
    }  
    }
    //System.out.println("count3:"+expResearchTP.getCount("MONTH_SUM"));
    //有数据方可 进行
    if(expResearchTP.getCount("MONTH_SUM")>0){
    for (int i = 0; i < expResearchTP.getCount("MONTH_SUM"); i++) {
    	//System.out.println("插入月结语句3"+getInsertDeptDDAccSQL(expResearchTP.getRow(i)));
      result = new TParm(TJDODBTool.getInstance().update(getInsertDeptDDAccSQL(expResearchTP.getRow(i)), conn));
      if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
        return result;
      }
    }   
      }
    //System.out.println("count4:"+expDonationTP.getCount("MONTH_SUM"));
    //有数据方可 进行
    if(expDonationTP.getCount("MONTH_SUM")>0){
    for (int i = 0; i < expDonationTP.getCount("MONTH_SUM"); i++) {  
    	//System.out.println("插入月结语句4"+getInsertDeptDDAccSQL(expDonationTP.getRow(i)));
      result = new TParm(TJDODBTool.getInstance().update(getInsertDeptDDAccSQL(expDonationTP.getRow(i)), conn));
      if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
        return result;
      }
    }
    }
  
    //有数据方可 进行
    if(LowerTP.getCount("MONTH_SUM")>0){
    for (int i = 0; i < LowerTP.getCount("MONTH_SUM"); i++) {  
    	//System.out.println("插入月结语句5"+getInsertDeptDDAccSQL(LowerTP.getRow(i)));
      result = new TParm(TJDODBTool.getInstance().update(getInsertDeptDDAccSQL(LowerTP.getRow(i)), conn));
      if (result.getErrCode() < 0) {  
        err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
        return result;
      }
    }
    }
    
    
    TParm lastDDACCTP = new TParm(TJDODBTool.getInstance().select(getLastBalanceSQL(lastYear + lastMonth)));

    TParm accCodeTP = new TParm(TJDODBTool.getInstance().select(getAccSubjectCodeSql()));

    TParm ddTP = new TParm();
     //ACC_SUBJECT_CODE 即财务分类   在INV_BASE中FI_CODE 
     //INV_BASE中有5136笔    excel中6822笔
    for (int i = 0; i < accCodeTP.getCount("CATEGORY_CODE"); i++)
    {
      ddTP.addData("ACC_DATE", year + month);
      //fux modify  
      ddTP.addData("ACC_SUBJECT_CODE", accCodeTP.getRow(i).getData("CATEGORY_CODE"));

      boolean tag = false;    

      for (int j = 0; j < lastDDACCTP.getCount("ACC_DATE"); j++) {
        if (accCodeTP.getRow(i).getData("CATEGORY_CODE").equals(lastDDACCTP.getRow(j).getData("ACC_SUBJECT_CODE"))) {
          ddTP.addData("LASTMONTHBALANCE", lastDDACCTP.getRow(j).getData("LASTMONTHBALANCE"));
          tag = true;
        }
      }
      if (!tag)
        ddTP.addData("LASTMONTHBALANCE", Integer.valueOf(0));
      else {
        tag = false;
      }

      for (int j = 0; j < incomeTP.getCount("ACC_SUBJECT_CODE"); j++) {
        if (accCodeTP.getRow(i).getData("CATEGORY_CODE").equals(incomeTP.getRow(j).getData("ACC_SUBJECT_CODE"))) {
          ddTP.addData("MONTHINCOME", incomeTP.getRow(j).getData("MONTH_SUM"));
          tag = true;
        }
      }
      if (!tag)
        ddTP.addData("MONTHINCOME", Integer.valueOf(0));
      else {
        tag = false;
      } 
 
      double sum = 0.0D;
      for (int j = 0; j < expTP.getCount("ACC_SUBJECT_CODE"); j++) {
        if ((accCodeTP.getRow(i).getData("CATEGORY_CODE").equals(expTP.getRow(j).getData("ACC_SUBJECT_CODE"))) && (!expTP.getRow(j).getData("ORG_CODE").toString().equals("0411")) && (!expTP.getRow(j).getData("ORG_CODE").toString().equals("0412"))) {
          sum += expTP.getRow(j).getDouble("MONTH_SUM");
        }
      } 
      ddTP.addData("MONTHEXPENDITURE", Double.valueOf(sum));

      sum = 0.0D;
      for (int j = 0; j < expTP.getCount("ACC_SUBJECT_CODE"); j++) {
        if ((accCodeTP.getRow(i).getData("CATEGORY_CODE").equals(expTP.getRow(j).getData("ACC_SUBJECT_CODE"))) && (expTP.getRow(j).getData("ORG_CODE").toString().equals("0411"))) {
          sum += expTP.getRow(j).getDouble("MONTH_SUM");
        }
      }
      ddTP.addData("CLINICALEXP", Double.valueOf(sum));

      sum = 0.0D;
      for (int j = 0; j < expTP.getCount("ACC_SUBJECT_CODE"); j++) {
        if ((accCodeTP.getRow(i).getData("CATEGORY_CODE").equals(expTP.getRow(j).getData("ACC_SUBJECT_CODE"))) && (expTP.getRow(j).getData("ORG_CODE").toString().equals("0412"))) {
          sum += expTP.getRow(j).getDouble("MONTH_SUM");
        }
      }
      ddTP.addData("LABORATORYEXP", Double.valueOf(sum));

      sum = 0.0D;
      for (int j = 0; j < expResearchTP.getCount("ACC_SUBJECT_CODE"); j++) {
        if (accCodeTP.getRow(i).getData("CATEGORY_CODE").equals(expResearchTP.getRow(j).getData("ACC_SUBJECT_CODE"))) {
          sum += expResearchTP.getRow(j).getDouble("MONTH_SUM");
        }
      }
      ddTP.addData("RESEARCHFUNDINGEXP", Double.valueOf(sum));

      sum = 0.0D;
      for (int j = 0; j < expDonationTP.getCount("ACC_SUBJECT_CODE"); j++) {
        if (accCodeTP.getRow(i).getData("CATEGORY_CODE").equals(expDonationTP.getRow(j).getData("ACC_SUBJECT_CODE"))) {
          sum += expDonationTP.getRow(j).getDouble("MONTH_SUM");
        }
      }
      ddTP.addData("DONATIONEXP", Double.valueOf(sum));

      ddTP.addData("MEDICALSERVICEEXP", Integer.valueOf(0)); 
   
      ddTP.addData("OPT_DATE", date.toString().substring(0, 19));
      ddTP.addData("OPT_USER", Operator.getID());
      ddTP.addData("OPT_TERM", Operator.getIP());
    }

    for (int i = 0; i < ddTP.getCount("ACC_DATE"); i++) {
      double d = ddTP.getDouble("LASTMONTHBALANCE", i) + ddTP.getDouble("MONTHINCOME", i) - 
        ddTP.getDouble("MONTHEXPENDITURE", i) - ddTP.getDouble("CLINICALEXP", i) - ddTP.getDouble("LABORATORYEXP", i) - 
        ddTP.getDouble("RESEARCHFUNDINGEXP", i) - ddTP.getDouble("DONATIONEXP", i) - ddTP.getDouble("MEDICALSERVICEEXP", i);
      ddTP.setData("MONTHBALANCE", i, Double.valueOf(d));
    }
    
    for (int i = 0; i < ddTP.getCount("ACC_SUBJECT_CODE"); i++) {
    	//System.out.println("最终插入语句"+getInsertDDAccSQL(ddTP.getRow(i)));
      result = new TParm(TJDODBTool.getInstance().update(getInsertDDAccSQL(ddTP.getRow(i)), conn));
      if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
        return result;
      }
  
    }

    return result;
  }

  /**
   * @return 查询主物资财务分类
   */
private String getAccSubjectCodeSql()
  { 
    return " SELECT CATEGORY_CODE FROM SYS_CATEGORY WHERE RULE_TYPE = 'INV_ACC_D' OR RULE_TYPE = 'INV_ACC_M'  ";
  }  
  /**
   * @return  查询物资财务主分类
   */
  private String getMainAccSubjectCodeSql()
  { 
    return " SELECT CATEGORY_CODE FROM SYS_CATEGORY WHERE RULE_TYPE = 'INV_ACC_M'  ";
  }  
  
     
  /**
   * @param 查询上期结存
   * @return
   */
private String getLastBalanceSQL(String accDate) 
  {
    return " SELECT ACC_DATE, ACC_SUBJECT_CODE, LASTMONTHBALANCE, MONTHINCOME, MONTHEXPENDITURE, RESEARCHFUNDINGEXP, CLINICALEXP, LABORATORYEXP, MEDICALSERVICEEXP, DONATIONEXP, MONTHBALANCE  FROM INV_DDACC WHERE ACC_DATE = '" + 
      accDate + "' ";
  }
/**
 * @param 查询入库相关数据
 * @return
 */
  private String getIncomeAMTSQL(String startDate, String endDate)
  {
    return " SELECT M.VERIFYIN_DEPT ORG_CODE,  SUM(D.QTY*D.UNIT_PRICE) + SUM(D.GIFT_QTY*D.UNIT_PRICE) MONTH_SUM, B.FI_CODE ACC_SUBJECT_CODE  FROM INV_VERIFYIND D LEFT JOIN INV_VERIFYINM M ON D.VERIFYIN_NO = M.VERIFYIN_NO LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE  WHERE M.VERIFYIN_DATE BETWEEN TO_DATE('" + 
      startDate + "','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('" + endDate + "','YYYY/MM/DD HH24:MI:SS') GROUP BY M.VERIFYIN_DEPT, B.FI_CODE ";
  }
  
  /** 
   * @param 查询卫生耗材出库相关数据
   * @return
   */
  //INV_BASE中的USE_CODE = 01？
  private String getExpAMTSQL(String startDate, String endDate)
  {
    return " SELECT M.TO_ORG_CODE ORG_CODE, SUM(D.QTY*D.COST_PRICE) MONTH_SUM, B.FI_CODE ACC_SUBJECT_CODE  FROM INV_DISPENSED D LEFT JOIN INV_DISPENSEM M ON D.DISPENSE_NO = M.DISPENSE_NO LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE  WHERE B.USE_CODE = '01' AND M.DISPENSE_DATE BETWEEN TO_DATE('" + 
      startDate + "','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('" + endDate + "','YYYY/MM/DD HH24:MI:SS') " + 
      " GROUP BY M.TO_ORG_CODE,B.FI_CODE ";
  }  
  /**
   * @param 查询其他分类出库相关数据
   * @return
   */
  //INV_BASE中的USE_CODE = 02？
  private String getExpResearchAMTSQL(String startDate, String endDate)
  {
    return " SELECT  M.TO_ORG_CODE ORG_CODE,SUM(D.QTY*D.COST_PRICE) MONTH_SUM, B.FI_CODE ACC_SUBJECT_CODE FROM INV_DISPENSED D LEFT JOIN INV_DISPENSEM M ON D.DISPENSE_NO = M.DISPENSE_NO LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE  WHERE B.USE_CODE = '02' AND M.DISPENSE_DATE BETWEEN TO_DATE('" + 
      startDate + "','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('" + endDate + "','YYYY/MM/DD HH24:MI:SS') " + 
      " GROUP BY M.TO_ORG_CODE,B.FI_CODE ";
  }   
  /** 
   * @param 查询维护分类出库相关数据
   * @return
   */
  //INV_BASE中的USE_CODE = 03？
  private String getExpDonationAMTSQL(String startDate, String endDate)
  {
    return " SELECT  M.TO_ORG_CODE ORG_CODE,SUM(D.QTY*D.COST_PRICE) MONTH_SUM, B.FI_CODE ACC_SUBJECT_CODE FROM INV_DISPENSED D LEFT JOIN INV_DISPENSEM M ON D.DISPENSE_NO = M.DISPENSE_NO LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE  WHERE B.USE_CODE = '03' AND M.DISPENSE_DATE BETWEEN TO_DATE('" + 
      startDate + "','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('" + endDate + "','YYYY/MM/DD HH24:MI:SS') " + 
      " GROUP BY M.TO_ORG_CODE,B.FI_CODE ";
  }
  /**
   * @param 查询低值易耗品出库相关数据
   * @return
   */
  //INV_BASE中的USE_CODE = 04？    
  private String getExpLowerAMTSQL(String startDate, String endDate)
  {
    return " SELECT  M.TO_ORG_CODE ORG_CODE,SUM(D.QTY*D.COST_PRICE) MONTH_SUM, B.FI_CODE ACC_SUBJECT_CODE FROM INV_DISPENSED D LEFT JOIN INV_DISPENSEM M ON D.DISPENSE_NO = M.DISPENSE_NO LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE  WHERE B.USE_CODE = '04' AND M.DISPENSE_DATE BETWEEN TO_DATE('" + 
      startDate + "','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('" + endDate + "','YYYY/MM/DD HH24:MI:SS') " + 
      " GROUP BY M.TO_ORG_CODE,B.FI_CODE ";
  }
  
     
  
  /**
   * @param tp 插入科室(出入)月结数据
   * @return        
   */
private String getInsertDeptDDAccSQL(TParm tp)
  {
    return " INSERT INTO INV_DEPTDDACC (ACC_DATE, ORG_CODE, ACC_SUBJECT_CODE, ACC_TYPE, MONTH_SUM, OPT_USER, OPT_DATE, OPT_TERM)  VALUES ('" + 
      tp.getValue("ACC_DATE") + "','" + tp.getValue("ORG_CODE") + "','" + tp.getValue("ACC_SUBJECT_CODE") + "','" + tp.getValue("ACC_TYPE") + "'," + tp.getValue("MONTH_SUM") + ",'" + tp.getValue("OPT_USER") + "',SYSDATE,'" + tp.getValue("OPT_TERM") + "') ";
  }
/**
 * @param tp 插入月结数据(DDACC)
 * @return
 */
  private String getInsertDDAccSQL(TParm tp)
  {
    return " INSERT INTO INV_DDACC ( ACC_DATE, ACC_SUBJECT_CODE, LASTMONTHBALANCE, MONTHINCOME, MONTHEXPENDITURE, RESEARCHFUNDINGEXP, CLINICALEXP, LABORATORYEXP, MEDICALSERVICEEXP, DONATIONEXP, MONTHBALANCE, OPT_USER, OPT_DATE, OPT_TERM )  VALUES ( '" + 
      tp.getValue("ACC_DATE") + "','" + tp.getValue("ACC_SUBJECT_CODE") + "'," + tp.getValue("LASTMONTHBALANCE") + "," + tp.getValue("MONTHINCOME") + "," + tp.getValue("MONTHEXPENDITURE") + 
      "," + tp.getValue("RESEARCHFUNDINGEXP") + "," + tp.getValue("CLINICALEXP") + "," + tp.getValue("LABORATORYEXP") + "," + tp.getValue("MEDICALSERVICEEXP") + "," + tp.getValue("DONATIONEXP") + "," + tp.getValue("MONTHBALANCE") + ",'" + tp.getValue("OPT_USER") + "',SYSDATE,'" + tp.getValue("OPT_TERM") + "' ) ";
  }
}