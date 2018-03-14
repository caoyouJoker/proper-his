package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
//----------
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InvBaseTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvBaseTool instanceObject;

    /**
     * 构造器
     */
    public InvBaseTool() {
        setModuleName("inv\\INVBaseModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static InvBaseTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvBaseTool();
        return instanceObject;
    }

    /**
     * 更新加权平均成本价
     *
     * @param parm
     * @return
     */
    public TParm onUpdateCostPrice(TParm parm, TConnection conn) {
        TParm result = this.update("updateCostPrice", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 物资查询
     */
    public String queryInvBase(TParm parm){
    	 StringBuffer sb = new StringBuffer();
    	 if(parm.getValue("INV_CODE").length()>0)
    		 sb.append(" INV_CODE = '"+parm.getValue("INV_CODE")+"'") ;
    	 if(parm.getValue("INV_CHN_DESC").length()>0){
    		 if(sb.length()>0)
    			 sb.append(" AND") ;
    		 sb.append(" INV_CHN_DESC LIKE '"+parm.getValue("INV_CHN_DESC")+"'") ; 
    	 }
    	 if(parm.getValue("SUP_CODE").length()>0){
    		 if(sb.length()>0)
    			 sb.append(" AND") ;
    		 sb.append(" SUP_CODE = '"+parm.getValue("SUP_CODE")+"'") ; 
    	 }
    	 if(parm.getValue("INVKIND_CODE").length()>0){
    		 if(sb.length()>0)
    			 sb.append(" AND") ;
    		 sb.append(" INVKIND_CODE = '"+parm.getValue("INVKIND_CODE")+"'") ; 
    	 }
    	 if(parm.getValue("PY1").length()>0){
    		 if(sb.length()>0)
    			 sb.append(" AND") ;
    		 sb.append(" LOWER(PY1) LIKE '"+parm.getValue("PY1")+"' OR " +
    		 		" UPPER(PY1) LIKE '"+parm.getValue("PY1")+"' ") ; 
    	 }
  	  String sql = "SELECT * FROM INV_BASE " ;
  	  if(sb.length()>0)
  		  sql += " WHERE" + sb.toString() ;
  	  else sql += " WHERE INV_CODE = '##'" ;
  	  return sql ;
    }

    /**
     * 新增物资
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新物资
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
       TParm result = this.update("update", parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }

   /**
     * 删除物资
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
       TParm result = this.update("delete", parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
    //-----------------供应室start--------------------
    /**
     * 查找物资费用
     * @param invCode String
     * @param orgCode String
     * @return TParm
     */
    public TParm getCostPrice(String invCode) {
        TParm result = new TParm();
        //检核入参不能为空
        if (invCode == null || invCode.length() == 0)
            return result.newErrParm( -1, "进参错误");
        //物资代码
        result.setData("INV_CODE", invCode);
        //查询物资
        result = query("getCostPrice", result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 得到名称
     * @param invCode String
     * @return TParm
     */
    public TParm getInvChnDesc(String invCode) {
        TParm result = new TParm();
        //检核入参不能为空
        if (invCode == null || invCode.length() == 0)
            return result.newErrParm( -1, "进参错误");
        //物资代码
        result.setData("INV_CODE", invCode);
        //查询物资
        result = query("getInvChnDesc", result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    
    
    
    
    //-----------------供应室end--------------------


}
