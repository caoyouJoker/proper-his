package jdo.bms;
import java.sql.Timestamp;

import jdo.adm.ADMTool;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSOrdermTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 
 * @version 4.0
 */
public class BMSFeeTool extends TJDOTool{
	private String type ;
	
	/**
	 * 构建实例
	 */
  public static BMSFeeTool instanceObject ;
    /**
     * 得到实例
     * @return
     */
  public static BMSFeeTool getInstance(){
	  if(instanceObject==null)
		  instanceObject = new BMSFeeTool() ;
	  return instanceObject ;
  }
  
  /**
   * 构造器
   */
  public BMSFeeTool(){
	  setModuleName("bms\\BMSFeeModule.x") ;
	  onInit() ;
  }
  
  /**
   * 查询
   */
  public TParm selectData(TParm parm){
	  TParm result = query("query",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  
  /**
   * 查询血品规格数据
   */
  public TParm selectBmsSubCat(TParm parm){
	  TParm result = query("selectSubcat",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 新增
   * @param parm
   * @return
   */
  public TParm insertData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("insert", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
	  
  }
  /**
   * 新增细表  ibsordd
   * @param parm
   * @return
   */
  public TParm insertDdata(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount("CASE_NO");i++){
		  result = update("insertDdata", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;
	  
  }
  /**
   * 新增细表  ibsordd
   * @param parm
   * @return
   */
  public TParm insertDdataNew(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount("CASE_NO");i++){
		  result = update("insertDdataNew", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;
	  
  }
  /**
   *  更新
   * @param parm
   * @return
   */
  
  public TParm updateData(TParm parm ,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("update", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }
  /**
   *  更新adm_inp的金额
   * @param parm
   * @return
   */
  
  public TParm updateAdmInp(TParm parm ,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("updateAdmInp", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }

  /**
   * 删除
   * @param parm
   * @return
   */
  public TParm deleteData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("delete", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
  }
  /**
   * 查询费用代码
   */
  public TParm selectOrdercode(TParm parm){
	  TParm result = query("selectOrdercode",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 获取医嘱明细selectAdmInpFee
   */
  public TParm selectOrderDetail(TParm parm){
	  TParm result = query("selectOrderDetail",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 获取ADM_INP的金额
   */
  public TParm selectAdmInpFee(TParm parm){
	  TParm result = query("selectAdmInpFee",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 获得ibs_ordd 数据
   */
  public TParm  getIbsData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  TParm ibsOrdd = new TParm() ;
	  TParm ibsOrdm = new TParm() ;
	  String caseNo =  parm.getValue("CASE_NO", 0);
	  //获得病患住院信息
	  TParm adm_info = new TParm();
      adm_info.setData("CASE_NO", parm.getValue("CASE_NO", 0));
      TParm ADMParm = ADMTool.getInstance().getADM_INFO(adm_info);
      //获取最大序号
      int caseNoSeq = selMaxCaseNoSeq(caseNo).getInt("CASE_NO_SEQ",0) == 0 ? 
              1:selMaxCaseNoSeq(caseNo).getInt("CASE_NO_SEQ",0)+1 ;
      //回去系统时间
      Timestamp date =SystemTool.getInstance().getDate() ;
      int count = 1;
      for(int i=0;i<parm.getCount("BLOOD_NO");i++){
    	  //==========获取需要计费的医嘱
    	  TParm  subParm =  new TParm() ;
    	  subParm.setData("SUBCAT_CODE", parm.getValue("BLD_SUBCAT", i)) ;
    	  subParm.setData("BLD_CODE",parm.getValue("BLD_CODE", i)) ;
    	  TParm orderParm =selectOrdercode(subParm) ;
    	  if(orderParm.getCount()<=0){
    		  continue  ;
    	  }
    		  type = "yes" ;   //判断细表是否插入费用
    	  for(int j=0;j<orderParm.getCount("ORDER_CODE");j++){
    	  //=============根据需要计费的医嘱查询医嘱的属性
    		  TParm orderDetail = selectOrderDetail(orderParm.getRow(j)) ;
    	  ibsOrdd.addData("CASE_NO", caseNo) ;
    	  ibsOrdd.addData("CASE_NO_SEQ", caseNoSeq) ;
    	  ibsOrdd.addData("SEQ_NO", count) ;
    	  ibsOrdd.addData("BILL_DATE", date) ;
    	  ibsOrdd.addData("ORDER_NO", "") ;  //医嘱顺序号
    	  ibsOrdd.addData("ORDER_SEQ", count) ; 
    	  ibsOrdd.addData("ORDER_CODE", orderDetail.getValue("ORDER_CODE", 0)) ;
    	  ibsOrdd.addData("ORDER_CAT1_CODE", orderDetail.getValue("ORDER_CAT1_CODE", 0)) ;
    	  ibsOrdd.addData("CAT1_TYPE", orderDetail.getValue("ORDER_CAT1_CODE", 0)) ;
    	  ibsOrdd.addData("ORDERSET_GROUP_NO", "") ;
    	  ibsOrdd.addData("ORDERSET_CODE", "") ;
    	  ibsOrdd.addData("INDV_FLG", "") ;
    	  ibsOrdd.addData("DEPT_CODE", ADMParm.getValue("DEPT_CODE", 0)) ;
    	  ibsOrdd.addData("STATION_CODE", ADMParm.getValue("STATION_CODE", 0)) ;
    	  ibsOrdd.addData("DR_CODE", ADMParm.getValue("VS_DR_CODE", 0)) ;
    	  ibsOrdd.addData("EXE_DEPT_CODE",parm.getValue("DEPT", 0)) ; //执行科室
    	  ibsOrdd.addData("EXE_STATION_CODE", parm.getValue("STATION", 0)) ;
    	  ibsOrdd.addData("EXE_DR_CODE", parm.getValue("OPT_USER", 0)) ;
    	  ibsOrdd.addData("MEDI_QTY", 1) ;
    	  ibsOrdd.addData("MEDI_UNIT", orderDetail.getValue("UNIT_CODE", 0)) ;// 开药单位
    	  ibsOrdd.addData("DOSE_CODE", "") ;
    	  ibsOrdd.addData("FREQ_CODE", "STAT") ;
    	  ibsOrdd.addData("TAKE_DAYS", 1) ;//天数
    	  ibsOrdd.addData("DOSAGE_QTY", 1) ;//总量
    	  ibsOrdd.addData("DOSAGE_UNIT", orderDetail.getValue("UNIT_CODE", 0)) ;// 配药单位
    	  ibsOrdd.addData("OWN_PRICE", orderDetail.getValue("OWN_PRICE", 0)) ;
    	  ibsOrdd.addData("NHI_PRICE", orderDetail.getValue("NHI_PRICE", 0)) ;
    	  ibsOrdd.addData("TOT_AMT", orderDetail.getValue("OWN_PRICE", 0)) ;
    	  ibsOrdd.addData("OWN_FLG", "Y") ;
    	  ibsOrdd.addData("BILL_FLG", "Y") ;
    	// 查询收据费用代码
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					orderDetail.getRow(0));
    	  ibsOrdd.addData("REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE", 0)) ; //住院费用
    	  ibsOrdd.addData("BILL_NO", "") ;
    	  ibsOrdd.addData("HEXP_CODE", orderDetail.getValue("CHARGE_HOSP_CODE", 0)) ; //院内费用
    	  ibsOrdd.addData("BEGIN_DATE", date) ;
    	  ibsOrdd.addData("END_DATE", date) ;
    	  ibsOrdd.addData("OWN_AMT", orderDetail.getValue("OWN_PRICE", 0)) ; //自费金额
    	  ibsOrdd.addData("OWN_RATE", 1) ;//默认为1 自付比例
    	  ibsOrdd.addData("REQUEST_FLG", "N") ;
    	  ibsOrdd.addData("REQUEST_NO", "") ;
    	  ibsOrdd.addData("INV_CODE", "") ;
    	  ibsOrdd.addData("OPT_USER", parm.getValue("OPT_USER", 0)) ;
    	  ibsOrdd.addData("OPT_TERM", parm.getValue("OPT_TERM", 0)) ;
    	  ibsOrdd.addData("COST_AMT", 0) ;
    	  ibsOrdd.addData("ORDER_CHN_DESC", orderDetail.getValue("ORDER_DESC", 0)) ;
    	  ibsOrdd.addData("COST_CENTER_CODE", orderDetail.getValue("EXEC_DEPT_CODE", 0)) ; //成本中心
    	  ibsOrdd.addData("SCHD_CODE",null!=parm.getValue("SCHD_CODE")&&
    			  parm.getValue("SCHD_CODE").length()>0?parm.getValue("SCHD_CODE"):"") ;
    	  ibsOrdd.addData("CLNCPATH_CODE", null!=parm.getValue("CLNCPATH_CODE")&&
    			  parm.getValue("CLNCPATH_CODE").length()>0?parm.getValue("CLNCPATH_CODE"):"") ;
    	  ibsOrdd.addData("DS_FLG", "N") ;
    	  ibsOrdd.addData("KN_FLG", "N") ;
    	  count++ ; 
          }
      }
      if(ibsOrdd.getCount("CASE_NO")<=0){
    	  result.setData("FEE", 0) ;
    	  return  result ;
      }
      result = insertDdataNew(ibsOrdd, conn);
	  if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
          }
	  //====================================================更新adm_inp的金额
	   TParm  admParm = this.selectAdmInpFee(adm_info);
	   //======================老总价
		double oleTotalAmt = admParm.getDouble("TOTAL_AMT", 0);
		//=================老剩余金额
		double oleCurAmt = admParm.getDouble("CUR_AMT", 0);
		double newAmt = 0.00 ; //新产生的费用
		for(int i=0;i<ibsOrdd.getCount("CASE_NO");i++){
		newAmt +=ibsOrdd.getDouble("OWN_AMT", i) ;
		}
		//==============新总金额
		double newTotalAmt = oleTotalAmt +newAmt ;
		//============新剩余金额
		double  newCurAmt = oleCurAmt-newAmt ;
		adm_info.setData("TOTAL_AMT", newTotalAmt) ;
		adm_info.setData("CUR_AMT", newCurAmt) ;
		result = this.updateAdmInp(adm_info, conn) ;
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	          }
      //组装ibs 主项  ================begin
      ibsOrdm.setData("CASE_NO", caseNo) ;
      ibsOrdm.setData("CASE_NO_SEQ", caseNoSeq) ;
      ibsOrdm.setData("BILL_DATE", date) ;
      ibsOrdm.setData("IPD_NO", ADMParm.getValue("IPD_NO", 0)) ;
      ibsOrdm.setData("MR_NO", ADMParm.getValue("MR_NO", 0)) ;
      ibsOrdm.setData("DEPT_CODE", ADMParm.getValue("DEPT_CODE", 0)) ;
      ibsOrdm.setData("STATION_CODE", ADMParm.getValue("STATION_CODE", 0)) ;
      ibsOrdm.setData("BED_NO", ADMParm.getValue("BED_NO", 0)) ;
      ibsOrdm.setData("DATA_TYPE", "1") ;
      ibsOrdm.setData("BILL_NO", "") ;
      ibsOrdm.setData("OPT_USER", parm.getValue("OPT_USER", 0)) ;
      ibsOrdm.setData("OPT_TERM", parm.getValue("OPT_TERM", 0)) ;
      ibsOrdm.setData("REGION_CODE", parm.getValue("REGION", 0)) ;
      ibsOrdm.setData("COST_CENTER_CODE", "") ;
      if(type.equals("yes")){
      result = IBSOrdermTool.getInstance().insertdata(ibsOrdm, conn);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      }
      result.addData("FEE", newAmt) ;
	  return result ;	  
  }
  /**
	 * 查询最大账务序号
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}

