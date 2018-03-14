package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 医保卡结算档</p>
 *
 * <p>Description: 医保卡结算档</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 20111107
 * @version 1.0
 */
public class INSOpdTXTool extends TJDOTool{
    /**
   * 实例
   */
  public static INSOpdTXTool instanceObject;
  /**
   * 得到实例
   * @return INSOpdApproveTool
   */
  public static INSOpdTXTool getInstance() {
      if (instanceObject == null)
          instanceObject = new INSOpdTXTool();
      return instanceObject;
  }

  /**
   * 构造器
   */
  public INSOpdTXTool() {
      setModuleName("ins\\INSOpdTXModule.x");
      onInit();
  }

  /**
   * 插入结算主档
   * @param parm TParm
   * @return TParm
   */
  public TParm insertINSOpd(TParm parm, TConnection conn) {
      TParm result = update("insertINSOpd", parm, conn);
      return result;
  }
  /**
   * INSAMT_FLG对账标志为3
   * @param parm
   * @param conn
   * @return
   */
  public TParm updateINSOpd(TParm parm, TConnection conn){
	  TParm result = update("updateINSOpd", parm, conn);
      return result;
	  
  }
  /**
   * 删除操作
   * @param parm
   * @param conn
   * @return
   */
  public TParm deleteINSOpd(TParm parm, TConnection conn) {
  	TParm result = update("deleteINSOpd", parm, conn);
      return result;
  }
  /**
   * 查询最大SEQ_NO
   * @param parm
   * @return
   */
  public TParm selectMAXSeqNo(TParm parm){
	  TParm result = query("selectMAXSeqNo", parm);
      return result;
  }
  
}
