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
	 * ����ʵ��
	 */
  public static SPCBsmTool instanceObject ;
    /**
     * �õ�ʵ��
     * @return
     */
  public static SPCBsmTool getInstance(){
	  if(instanceObject==null)
		  instanceObject = new SPCBsmTool() ;
	  return instanceObject ;
  }
  
  /**
   * ������
   */
  public SPCBsmTool(){
	  setModuleName("spc\\SPCBsmModule.x") ;
	  onInit() ;
  }
  
  /**
   * ��ѯ���﷢ҩ���Ĵ�����Ϣ
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
   * ��ѯ���﷢ҩ���ķ�ҩ״̬
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
   * ��ѯסԺ��ҩ����������Ϣ
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
   * ���ﴦ������
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
   *  ���£������ҩ����ҩ״̬ 003
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
   * ɾ��
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
   * סԺ��������
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
   * סԺ������ɾ������
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
   * ɾ��������Ϣ��ϸ��
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
   * ɾ��������Ϣ������
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
   * ����������Ϣ������
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
   * ����������Ϣ��ϸ��
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
   * ɾ��hisҩƷ�ֵ�pha_base
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
   * his ҩƷ�ֵ�pha_base����
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
   * ɾ��his sysFee
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
   * his ҩƷ�ֵ�pha_base����
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
   * ɾ��his ת����
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
   * his ҩƷ�ֵ�pha_base����
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
   * ɾ��his ����
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
   * his ҩƷ�ֵ�pha_base����
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
   * �õ���������
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
   * �õ�����ϸ��
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
   * ͬ���ɹ�֮�����״̬Ϊ��ɣ�UPDATE_FLG=3��
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
   * ����ҩƷ�����ѯ�������̱���
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


