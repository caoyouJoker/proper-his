package jdo.spc.bsm;



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
public class SPCBsmTool extends TJDOTool{
	
	/**
	 * 构建实例
	 */
  public static SPCBsmTool instanceObject ;
    /**
     * 得到实例
     * @return
     */
  public static SPCBsmTool getInstance(){
	  if(instanceObject==null)
		  instanceObject = new SPCBsmTool() ;
	  return instanceObject ;
  }
  
  /**
   * 构造器
   */
  public SPCBsmTool(){
	  setModuleName("spc\\SPCBsmModule.x") ;
	  onInit() ;
  }
  
  /**
   * 查询门诊发药机的处方信息
   */
  public TParm query(TParm parm){
	  TParm result = query("query",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查询门诊发药机的发药状态
   * @param parm
   * @return
   */
  public TParm selectOpdOrderFlg(TParm parm){
	  TParm result = query("selectOpdOrderFlg",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
 
  /**
   * 查询住院包药机的数据信息
   * @param parm
   * @return
   */
  public TParm selectOdiDspnm(TParm parm){
	  TParm result = query("selectOdiDspnm",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 门诊处方新增
   * @param parm
   * @return
   */
  public TParm insertData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount("ORDER_CODE");i++){
		  result = update("insertData", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	
	  return result ;
	  
  }
  

  /**
   *  更新，门诊包药机发药状态 003
   * @param parm
   * @return
   */
  
  public TParm updateData(TParm parm ,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("updateData", parm,conn);
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
	  result = update("deleteData", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
  }
  /**
   * 住院处方新增
   * @param parm
   * @return
   */
  public TParm insertOdiDspnm(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount("ORDER_CODE");i++){
		  result = update("insertOdiDspnm", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	
	  return result ;
	  
  }	
  /**
   * 住院处方先删除后增
   * @param parm
   * @return
   */
  public TParm deleteOdiDspnm(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("deleteOdiDspnm", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
  }	
  /**
   * 删除请领信息的细表
   * @param parm
   * @return
   */
  public TParm deleteRequestD(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("deleteRequestD", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	
	  return result ;
  }	
  /**
   * 删除请领信息的主表
   * @param parm
   * @return
   */
  public TParm deleteRequestM(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("deleteRequestM", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;
  }	
  /**
   * 新增请领信息的主表
   * @param parm
   * @return
   */
  public TParm insertRequestM(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertRequestM", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;
  }	
  /**
   * 新增请领信息的细表
   * @param parm
   * @return
   */
  public TParm insertRequestD(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertRequestD", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;
  }	
  /**
   * 删除his药品字典pha_base
   * @param parm
   * @return
   */
  public TParm deletePhaBase(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("deletePhaBase", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
  }	
  /**
   * his 药品字典pha_base新增
   * @param parm
   * @return
   */
  public TParm insertPhaBase(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertPhaBase", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;  
  }	
  /**
   * 删除his sysFee
   * @param parm
   * @return
   */
  public TParm deleteSysfee(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("deleteSysfee", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
  }	
  /**
   * his 药品字典pha_base新增
   * @param parm
   * @return
   */
  public TParm insertSysfee(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertSysfee", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;  
  }	
  /**
   * 删除his 转换率
   * @param parm
   * @return
   */
  public TParm deletePhaTransUnit(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("deletePhaTransUnit", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
  }	
  /**
   * his 药品字典pha_base新增
   * @param parm
   * @return
   */
  public TParm insertPhaTransUnit(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertPhaTransUnit", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;  
  }	
  /**
   * 删除his 科室
   * @param parm
   * @return
   */
  public TParm deleteSysDept(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("deleteSysDept", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
  }	
  /**
   * his 药品字典pha_base新增
   * @param parm
   * @return
   */
  public TParm insertSysDept(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertSysDept", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	  return result ;  
  }	
  /**
   * 得到出库主项
   * @param parm
   * @return
   */
  public TParm getDispenseM(TParm parm){
	  TParm result = query("getDispenseM",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
	  return result ;
  }
  /**
   * 得到出库细项
   * @param parm
   * @return
   */
  public TParm getDispenseD(TParm parm){
	  TParm result = query("getDispenseD",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
	  return result ;
  }
  /**
   * 同步成功之后更新状态为完成（UPDATE_FLG=3）
   * @param parm
   * @return
   */
  public TParm updateDispense(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("updateDispense", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	  }  
	  }
		 
	  return result ;  
  }	
  /**
   * 根据药品编码查询生产厂商编码
   */
  public String getManCode(String orderCode){
	  String  manCode = "" ;
	  String sql = "SELECT MAN_CODE FROM SYS_FEE WHERE ORDER_CODE = '"+orderCode+"'" +
	  		      " AND ACTIVE_FLG = 'Y'  " ;
	  TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
	  if(parm.getCount()>0)
		  manCode = parm.getValue("MAN_CODE", 0) ;
	  return manCode;
  }
}


