package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import java.util.Map;

/**
 * <p>Title: 临床路径EMR工具类</p>
 *
 * <p>Description: 临床路径EMR工具类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEMRTool extends TJDOTool{
    /**
     * 构造方法私有化，加载SQL语句配置文件
     */
    private CLPEMRTool() {
        this.setModuleName("clp\\CLPEMRToolModule.x");
        onInit();
    }

    /**
     * 声明静态数据库工具类实例
     */
    private static CLPEMRTool instance = null;
    /**
     * 声明静态方法，返回此类的实例
     * @return BscInfoTool
     */
    public static CLPEMRTool getInstance() {
        if (instance == null) {
            instance = new CLPEMRTool();
        }
        return instance;
    }
    /**
     * 得到临床路径表单需要的数据
     * @param parm TParm
     * @return TParm
     * TParm  CASE_NO CLNCPATH_CODE
     *
     */
    public TParm getEMRManagedData(TParm parm) {
       String case_no=parm.getValue("CASE_NO");
       TParm result = new TParm();
       //得到标准数据
       TParm standardParm=getStandardTParm(parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       //遍历标准数据，对标准数据是否执行进行处理
       for(int i=0;i<standardParm.getCount();i++){
           TParm rowParm = standardParm.getRow(i);
           boolean flag= checkStandardIsExec(rowParm,case_no);
           if(flag){
               result.setData(rowParm.getValue("SIGN_CODE"),"Y");
           }
       }
//       result.setData("A03D01N0011","Y");
//       System.out.println("返回的parm:"+result);
       return result;
   }

   /**
    * 得到标准的临床路径数据
    * @return TParm
    */
   private TParm getStandardTParm(TParm parm){
       TParm result = new TParm();
       String regionCode=parm.getValue("REGION_CODE");
       String case_no=parm.getValue("CASE_NO");
       StringBuffer sqlbf = new StringBuffer();
       sqlbf.append("SELECT CLNCPATH_CODE||SCHD_CODE||ORDER_FLG||ORDER_CODE||ORDER_SEQ_NO AS SIGN_CODE,  ");
       sqlbf.append(" CLNCPATH_CODE, ");
       sqlbf.append(" SCHD_CODE, ");
       sqlbf.append(" ORDER_FLG, ");
       sqlbf.append(" ORDER_CODE, ");
       sqlbf.append(" REGION_CODE, ");
       sqlbf.append(" ORDER_SEQ_NO ");
       sqlbf.append(" FROM CLP_PACK  ");
       sqlbf.append(" WHERE REGION_CODE='"+regionCode+"' ");
       sqlbf.append(" AND CLNCPATH_CODE IN ( ");
       sqlbf.append(" SELECT CLNCPATH_CODE FROM CLP_MANAGEM WHERE REGION_CODE='"+regionCode+"' ");
       sqlbf.append(" AND CASE_NO = '"+case_no+"' ");
       sqlbf.append(")");
//       System.out.println("执行sql:"+sqlbf.toString());
       Map mapresult=TJDODBTool.getInstance().select(sqlbf.toString());
       result=new TParm(mapresult);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 判断标准是否执行方法
    * @return boolean
    */
   private boolean checkStandardIsExec(TParm parm,String caseNo){
       boolean flag=false;
       String regionCode=parm.getValue("REGION_CODE");
       String clncPathCode=parm.getValue("CLNCPATH_CODE");
       String scheCode=parm.getValue("SCHD_CODE");
       String orderFlg=parm.getValue("ORDER_FLG");
       String orderCode=parm.getValue("ORDER_CODE");
       String orderSeq=parm.getValue("ORDER_SEQ_NO");
       StringBuffer sqlbf=new StringBuffer("");
       sqlbf.append(" SELECT COUNT(*) AS TOTAL FROM CLP_MANAGED ");
       sqlbf.append(" WHERE REGION_CODE='"+regionCode+"' ");
       sqlbf.append(" AND CLNCPATH_CODE='"+clncPathCode+"' ");
       sqlbf.append(" AND SCHD_CODE='"+scheCode+"' ");
       sqlbf.append(" AND ORDER_FLG='"+orderFlg+"' ");
       sqlbf.append(" AND ORDER_CODE='"+orderCode+"' ");
       sqlbf.append(" AND ORDER_SEQ_NO='"+orderSeq+"' ");
       sqlbf.append(" AND ((MAINORD_CODE IS NOT NULL AND ORDER_FLG <>'N')OR (PROGRESS_CODE LIKE '%A%' AND ORDER_FLG ='N'))");
       sqlbf.append(" AND CASE_NO='"+caseNo+"'");
//       System.out.println("执行判断标准是否执行sql:"+sqlbf.toString());
       Map mapresult=TJDODBTool.getInstance().select(sqlbf.toString());
       TParm result = new TParm(mapresult);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return false;
       }
       if(result.getInt("TOTAL",0)>0){
           flag=true;
       }else{
           flag=false;
       }
       return flag;
   }

}
