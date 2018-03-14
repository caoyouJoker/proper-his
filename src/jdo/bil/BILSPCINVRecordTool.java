package jdo.bil;

import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSOrdermTool;
import jdo.sys.DeptTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SYSStationTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

public class BILSPCINVRecordTool extends TJDOTool{

	 /**
     * 实例
     */
    public static BILSPCINVRecordTool instanceObject;
    /**
     * 得到实例
     * @return SPCINVRecordTool
     */
    public static BILSPCINVRecordTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILSPCINVRecordTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILSPCINVRecordTool() {
    	  setModuleName("bil\\SPCINVRecordModule.x");
	      onInit();
    }
    
    /**
     * 插入数据
     * */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = this.update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 回写数据
     * */
    public TParm updSpcInvRecord(TParm parm, TConnection connection){
    	 TParm result = this.update("updData", parm, connection);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
      /**
       * 新增费用(For INW , UDD,PATCH)
       * @param parm TParm
       * @param connection TConnection
       * @return TParm dataType=3 护士站,dataType=2 药房,dataType=0 批次,dataType=4 膳食
       */
      public TParm insertIBSOrder(TParm parm, TConnection connection) {
          System.out.println("新增费用(For INW , UDD,PATCH)"+parm);
          TParm result = new TParm();
          TParm mParm = parm.getParm("M");
          Timestamp billDate;
          System.out.println("计价类别》》》》》》》》》》》"+parm.getValue("DATA_TYPE"));
          if ("0".equals(parm.getValue("DATA_TYPE")))
              billDate = parm.getTimestamp("BILL_DATE");
          else
              billDate = SystemTool.getInstance().getDate();
          System.out.println("计费日期"+billDate);
          TParm inMParm = new TParm();
          inMParm.setData("CASE_NO", mParm.getData("CASE_NO", 0));
          inMParm.setData("CASE_NO_SEQ", mParm.getData("CASE_NO_SEQ", 0));
          inMParm.setData("BILL_DATE", billDate);
          inMParm.setData("IPD_NO", mParm.getData("IPD_NO", 0));
          inMParm.setData("MR_NO", mParm.getData("MR_NO", 0));
          inMParm.setData("DEPT_CODE", mParm.getData("DEPT_CODE", 0));
          inMParm.setData("STATION_CODE", mParm.getData("STATION_CODE", 0));
          inMParm.setData("BED_NO", mParm.getData("BED_NO", 0) == null ? new TNull(String.class):mParm.getData("BED_NO", 0));
          inMParm.setData("DATA_TYPE", parm.getData("DATA_TYPE"));
          inMParm.setData("BILL_NO", "");
          inMParm.setData("OPT_USER", mParm.getData("OPT_USER", 0));
          inMParm.setData("OPT_TERM", mParm.getData("OPT_TERM", 0));
          TParm stationInfo = SYSStationTool.getInstance().selStationRegion(mParm.
                  getValue("STATION_CODE", 0));
          inMParm.setData("REGION_CODE", stationInfo.getData("REGION_CODE", 0));
          System.out.println("插入OrderM数据" + inMParm);
          result = IBSOrdermTool.getInstance().insertdata(inMParm, connection);
          TParm results = new TParm();
          if (result.getErrCode() < 0) {
              err(result.getErrName() + " " + result.getErrText());
              return result;
          }
          System.out.println("插入orderM数据成功！！HHHHHHHHHH");
          int dCount = mParm.getCount("CASE_NO");
          double totalAmtForADM = 0.00;
          for (int j = 0; j < dCount; j++) {
              TParm inDParm = new TParm();
              inDParm.setData("CASE_NO",
                              mParm.getData("CASE_NO", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("CASE_NO", j));
              inDParm.setData("CASE_NO_SEQ",
                              mParm.getData("CASE_NO_SEQ", j) == null ? -1 :
                              mParm.getData("CASE_NO_SEQ", j));
              results.addData("CASE_NO_SEQ",  mParm.getData("CASE_NO_SEQ", j) == null ? -1 :
                  mParm.getData("CASE_NO_SEQ", j));
              inDParm.setData("SEQ_NO",
                              mParm.getData("SEQ_NO", j) == null ? -1 :
                              mParm.getData("SEQ_NO", j));
              results.addData("SEQ_NO", mParm.getData("SEQ_NO", j) == null ? -1 :
                  mParm.getData("SEQ_NO", j));
              inDParm.setData("BILL_DATE", billDate);
              inDParm.setData("ORDER_NO",
                              mParm.getData("ORDER_NO", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("ORDER_NO", j));
              inDParm.setData("ORDER_SEQ",
                              mParm.getData("ORDER_SEQ", j) == null ?
                              "0" :
                              mParm.getData("ORDER_SEQ", j));
              inDParm.setData("ORDER_CODE",
                              mParm.getData("ORDER_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("ORDER_CODE", j));
              TParm sysFeeInfo = SYSFeeTool.getInstance().getFeeAllData(mParm.
                      getValue("ORDER_CODE", j));
              inDParm.setData("ORDER_CHN_DESC",
                              sysFeeInfo.getData("ORDER_DESC", 0) == null ?
                              new TNull(String.class) :
                              sysFeeInfo.getData("ORDER_DESC", 0));
              inDParm.setData("ORDER_CAT1_CODE",
                              mParm.getData("ORDER_CAT1_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("ORDER_CAT1_CODE", j));
              inDParm.setData("CAT1_TYPE",
                              mParm.getData("CAT1_TYPE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("CAT1_TYPE", j));
              inDParm.setData("ORDERSET_GROUP_NO",
                              mParm.getData("ORDERSET_GROUP_NO", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("ORDERSET_GROUP_NO", j));
              inDParm.setData("ORDERSET_CODE",
                              mParm.getData("ORDERSET_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("ORDERSET_CODE", j));
              inDParm.setData("INDV_FLG",
                              mParm.getData("HIDE_FLG", j) == null ?
                              "N" :
                              mParm.getData("HIDE_FLG", j));
              inDParm.setData("DS_FLG",
                              mParm.getData("DS_FLG", j) == null ?
                              "N" :
                              mParm.getData("DS_FLG", j));
              inDParm.setData("DEPT_CODE",
                              mParm.getData("ORDER_DEPT_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("ORDER_DEPT_CODE", j));
              inDParm.setData("STATION_CODE",
                              mParm.getData("STATION_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("STATION_CODE", j));
              inDParm.setData("DR_CODE",
                              mParm.getData("ORDER_DR_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("ORDER_DR_CODE", j));
              System.out.println("mParm-------->"+mParm);
              System.out.println("执行科室---〉"+mParm.getData("EXEC_DEPT_CODE",0));
              inDParm.setData("EXE_DEPT_CODE",
                              mParm.getData("EXE_DEPT_CODE",0) == null ?
                              new TNull(String.class) :
                              mParm.getData("EXE_DEPT_CODE",0));
              inDParm.setData("EXE_STATION_CODE",
                              mParm.getData("STATION_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("STATION_CODE", j));
              inDParm.setData("COST_CENTER_CODE",mParm.getData("EXE_DEPT_CODE",0) == null ?
                      new TNull(String.class) :
                          mParm.getData("EXE_DEPT_CODE",0));

              inDParm.setData("EXE_DR_CODE",
                              mParm.getData("OPT_USER", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("OPT_USER", j));
              inDParm.setData("MEDI_QTY",
                              mParm.getData("MEDI_QTY", j) == null ? 0.00 :
                              mParm.getData("MEDI_QTY", j));
              inDParm.setData("MEDI_UNIT",
                              mParm.getData("MEDI_UNIT", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("MEDI_UNIT", j));
              inDParm.setData("DOSE_CODE", mParm.getData("DOSE_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("DOSE_CODE", j)); //剂型;剂型类型
              inDParm.setData("FREQ_CODE",
                              mParm.getData("FREQ_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("FREQ_CODE", j));
              inDParm.setData("TAKE_DAYS",
                              mParm.getData("TAKE_DAYS", j) == null ? 0 :
                              mParm.getData("TAKE_DAYS", j));
              inDParm.setData("DOSAGE_UNIT",
                              mParm.getData("DOSAGE_UNIT", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("DOSAGE_UNIT", j));
              inDParm.setData("OWN_PRICE",
                              mParm.getData("OWN_PRICE", j) == null ? 0.00 :
                              mParm.getData("OWN_PRICE", j));
              inDParm.setData("NHI_PRICE",
                              mParm.getData("NHI_PRICE", j) == null ? 0.00 :
                              mParm.getDouble("NHI_PRICE", j));
              inDParm.setData("OWN_FLG", "Y");
              inDParm.setData("BILL_FLG", "Y");
              inDParm.setData("REXP_CODE",
                              mParm.getData("REXP_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("REXP_CODE", j));
              inDParm.setData("BILL_NO", new TNull(String.class));
              inDParm.setData("HEXP_CODE",
                              mParm.getData("HEXP_CODE", j) == null ?
                              new TNull(String.class) :
                              mParm.getData("HEXP_CODE", j));
              Timestamp startDate = null;
              Timestamp endDate = null;
              if(mParm.getData("DISPENSE_EFF_DATE",j) instanceof String){
            	   startDate  = StringTool.getTimestamp(mParm.getData("DISPENSE_EFF_DATE",j).toString().substring(0,19), "yyyy-MM-dd HH:ss:mm");
              }else{
            	   startDate = mParm.getTimestamp("DISPENSE_EFF_DATE", j);
              }
              if(mParm.getData("DISPENSE_END_DATE",j) instanceof String){
            	   endDate = StringTool.getTimestamp(mParm.getData("DISPENSE_END_DATE",j).toString().substring(0,19), "yyyy-MM-dd HH:ss:mm");
              }else{
            	   endDate = mParm.getTimestamp("DISPENSE_END_DATE", j);
              }
//              Timestamp startDate = mParm.getTimestamp("DISPENSE_EFF_DATE", j);
//              Timestamp endDate = mParm.getTimestamp("DISPENSE_END_DATE", j);
              System.out.println("开始时间" + startDate);
              System.out.println("结束时间" + endDate);
              inDParm.setData("BEGIN_DATE",
                              startDate == null ? new TNull(Timestamp.class) :
                              startDate);
              inDParm.setData("END_DATE",
                              endDate == null ? new TNull(Timestamp.class) :
                              endDate);
              if ("0".equals(parm.getValue("DATA_TYPE"))) {
                  billDate = parm.getTimestamp("BILL_DATE");
                  inDParm.setData("BEGIN_DATE", billDate);
                  inDParm.setData("END_DATE", billDate);

              }
             // String flg = parm.getValue("FLG",j);
//              System.out.println("flg----->"+flg);
//              if (flg.equals("ADD")) {
                  inDParm.setData("DOSAGE_QTY",

                                  mParm.getData("DOSAGE_QTY", j) == null ? 0.00 :
                                  mParm.getDouble("DOSAGE_QTY", j));
                  inDParm.setData("COST_AMT",
                                  mParm.getData("COST_AMT", j) == null ? 0.00 :
                                  mParm.getDouble("COST_AMT", j));
                  inDParm.setData("OWN_AMT",
                                  StringTool.round(mParm.getDouble("OWN_AMT", j),
                                                   4));
                  inDParm.setData("TOT_AMT",
                                  StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                   2));
                  totalAmtForADM = totalAmtForADM +
                                   StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                    2);


              inDParm.setData("OWN_RATE",
                              mParm.getData("OWN_RATE", j) == null ? 0.00 :
                              mParm.getData("OWN_RATE", j));
              inDParm.setData("REQUEST_FLG", "N");
              inDParm.setData("REQUEST_NO", new TNull(String.class));
              inDParm.setData("INV_CODE", new TNull(String.class));
              inDParm.setData("OPT_USER", mParm.getData("OPT_USER", j));
              inDParm.setData("OPT_TERM", mParm.getData("OPT_TERM", j));
              System.out.println("插入第" + j + "条细表数据" + inDParm);
              result = IBSOrderdTool.getInstance().insertdata(inDParm, connection);
              if (result.getErrCode() < 0) {
                  err(result.getErrName() + " " + result.getErrText());
                  return result;
              }
          }
          String caseNoForADM = mParm.getValue("CASE_NO", 0);
          TParm selADMAll = new TParm();
          selADMAll.setData("CASE_NO", caseNoForADM);
          TParm selADMAllData = ADMInpTool.getInstance().selectall(selADMAll);
          double totalAmt = selADMAllData.getDouble("TOTAL_AMT", 0);
          double curAmt = selADMAllData.getDouble("CUR_AMT", 0);
          String flg = parm.getValue("FLG");
          double patchAmt = 0.00;
          if (parm.getData("PATCH_AMT") != null) {
              patchAmt = parm.getDouble("PATCH_AMT");
              System.out.println("批次金额"+patchAmt);
          }
//          if (flg.equals("ADD")) {
              if ("0".equals(parm.getValue("DATA_TYPE"))) {
                  //  System.out.println("批次执行医疗总金额"+(totalAmt+totalAmtForADM-patchAmt));
                  result = ADMTool.getInstance().updateTOTAL_AMT("" +
                          (totalAmt + totalAmtForADM - patchAmt), caseNoForADM,
                          connection); //更新ADM中医疗总金额
                  if (result.getErrCode() < 0) {
                      err(result.getErrName() + " " + result.getErrText());
                      return result;
                  }
                  // System.out.println("批次执行目前余额"+(curAmt - totalAmtForADM+patchAmt));
                  result = ADMTool.getInstance().updateCUR_AMT("" +
                          (curAmt - totalAmtForADM + patchAmt), caseNoForADM,
                          connection); //更新ADM中目前余额
                  if (result.getErrCode() < 0) {
                      err(result.getErrName() + " " + result.getErrText());
                      return result;
                  }

              } else {
                  System.out.println("医疗总金额"+(totalAmt+totalAmtForADM));
                  result = ADMTool.getInstance().updateTOTAL_AMT("" +
                          (totalAmt + totalAmtForADM), caseNoForADM, connection); //更新ADM中医疗总金额
                  if (result.getErrCode() < 0) {
                      err(result.getErrName() + " " + result.getErrText());
                      return result;
                  }
                  System.out.println("行目前余额"+(curAmt - totalAmtForADM));
                  result = ADMTool.getInstance().updateCUR_AMT("" +
                          (curAmt - totalAmtForADM), caseNoForADM, connection); //更新ADM中目前余额
                  if (result.getErrCode() < 0) {
                      err(result.getErrName() + " " + result.getErrText());
                      return result;
                  }

              }
          System.out.println("result--->"+result);
          result.setData(results.getData());
          System.out.println("计价结束》》》》》》》》》》》");
          return result;
      }

    /**
     * 扣库 
     * INV_STOCKM
     * */   
    public TParm updInvStockM(TParm parm, TConnection connection) {
    	System.out.println("updInvStockM-----parm"+parm);
        TParm result = this.update("updInvStockM", parm, connection);//11111
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 扣库
     * INV_STOCKD
     * */
    public TParm updInvStockD(TParm parm, TConnection connection) {
    	System.out.println("updInvStockD"+parm);
        TParm result = this.update("updInvStockD", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    /**
     * 扣库 
     * INV_STOCKDD
     * */
    public TParm updInvStockDD(TParm parm, TConnection connection) {
    	System.out.println("updInvStockDD"+parm);
        TParm result = update("updInvStockDD", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
}
