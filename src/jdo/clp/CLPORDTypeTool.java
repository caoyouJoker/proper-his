package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 临床路径项目</p>
 *
 * <p>Description: 临床路径项目</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPORDTypeTool extends TJDOTool{
    private static CLPORDTypeTool instanceObject;
     public CLPORDTypeTool() {
         setModuleName("clp\\CLPORDTypeModule.x");
         onInit();
     }

     public static CLPORDTypeTool getInstance() {
         if (instanceObject == null) {
             instanceObject = new CLPORDTypeTool();
         }
         return instanceObject;
     }

     /**
      * 查询
      * @param parm TParm
      * @return TParm
      */
     public TParm selectData(TParm parm) {
         TParm result = new TParm();
         result = query("selectData", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 删除数据
      * @param ACIRecordNo String
      * @return TParm
      */
     public TParm deleteData(TParm parm) {
         TParm result = update("deleteData", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 判断数据是否存在
      * @param parm TParm
      * @return TParm
      */
     public TParm checkDataExist(TParm parm) {
         TParm result = this.query("checkDataExist", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 修改数据
      * @param parm TParm
      * @return TParm
      */
     public TParm updateData(TParm parm) {
         TParm result = this.update("updateData", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }

         return result;
     }


     /**
      * 插入信息
      * @param parm TParm
      * @return TParm
      */
     public TParm insertData(TParm parm) {
         TParm result = this.update("insertData", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

}
