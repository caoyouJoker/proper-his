package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 内嵌式医保程序</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class InsSxKa003Tool extends TJDOTool {
    /**
     * 实例
     */
    public static InsSxKa003Tool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static InsSxKa003Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new InsSxKa003Tool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public InsSxKa003Tool() {
        setModuleName("ins\\InsSxKa003Module.x");
        onInit();
    }

    /**
     * 查询INS_SX_KA002
     * @param parm TParm
     * @return TParm
     */
    public TParm selInSxka003(TParm parm, TConnection connection) {
       // System.out.println("查询");

        TParm result = new TParm();
        result = query("query", parm);
       // System.out.println("result=" + result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    /**
 * 查询sys_fee中医保没有的药品
 * @param parm TParm
 * @return TParm
 */
public TParm query_Ins_Sys(TParm parm){
   // System.out.println("《--------查询--Sx003------------》");
    TParm result = new TParm();
       result = query("query_sys_ins", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;

}
   /**
    * 汇入sys_fee中存在医保中不存在的药品
    * @param parm TParm
    * @param conn TConnection
    * @return TParm
    */
   public TParm importOrder(TParm parm,TConnection conn){
      // System.out.println("《--------新增--------------》");
       TParm result = new TParm();
       result = update("importOrder", parm,conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;


   }
   /**
 * 查询出新插入的数据
 * @return TParm
 */
public TParm queryMaxDate(){
    //System.out.println("《--------查询--------------》");
       TParm result = new TParm();
          result = query("onQueryMaxSxka003");
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText()
                  + result.getErrName());
              return result;
          }
          return result;

}
      /**
* 查询INS_SX_KA002
* @param parm TParm
* @return TParm
*/
public TParm onQueryMaxSxka003(TParm parm, TConnection connection) {
//System.out.println("002 Tool 查询=====》");

TParm result = new TParm();
result = query("onQueryMaxSxka003", parm);
//System.out.println("result=" + result);
if (result.getErrCode() < 0) {
    err("ERR:" + result.getErrCode() + result.getErrText() +
        result.getErrName());
    return result;
}
return result;

}





    /**
     * 保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onSave(TParm parm, TConnection connection) {
        //System.out.println("tool onsave");
        TParm result = new TParm();
        result = onUpdate(parm.getParm(InsSxKa003List.MODIFIED));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = onDelete(parm.getParm(InsSxKa003List.DELETED));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = onInsert(parm.getParm(InsSxKa003List.NEW));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;

    }

    /**
     * 更新
     * @param parm
     * @return result
     */
    public TParm onUpdate(TParm parm) {
        //System.out.println(" tool updata");
        int count = parm.getCount();
        TParm result = new TParm();
        for (int i = 0; i < count; i++) {
            TParm inParm = new TParm();
            inParm.setRowData( -1, parm, i);
            result = this.updatedata(inParm);
            if (result.getErrCode() < 0)
                return result;
        }
        return result;
    }


    /**
     * 更新医保药品
     * @param nhiCompany String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
       // System.out.println("<-----003Sx update----------->");
        TParm result = update("update", parm);
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
     * @return result
     */
    public TParm onDelete(TParm parm) {
        int count = parm.getCount();
        TParm result = new TParm();
        for (int i = 0; i < count; i++) {
            TParm inParm = new TParm();
            inParm.setRowData( -1, parm, i);
            result = this.deletedata(inParm);
            if (result.getErrCode() < 0)
                return result;
        }

        return result;
    }

    /**
     * 删除数据
     * @param parm
     * @return
     */
    public TParm deletedata(TParm parm) {
       // System.out.println("删除数据");
        TParm result = new TParm();
        result = update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入
     * @param parm
     * @return result
     */
    public TParm onInsert(TParm parm) {
        int count = parm.getCount();
        TParm result = new TParm();
        for (int i = 0; i < count; i++) {
            TParm inParm = new TParm();
            inParm.setRowData( -1, parm, i);
            result = this.insertdata(inParm);
            if (result.getErrCode() < 0)
                return result;
        }
        return result;
    }



    /**
     * 新增
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
       // System.out.println("《--------新增--------------》");
        TParm result = new TParm();
        result = update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
 * 新增
 * @param parm TParm
 * @return TParm
 */
public TParm insertdata(TParm parm,TConnection conn) {
  //  System.out.println("《--------新增--------------》");
    TParm result = new TParm();
    result = update("insert", parm,conn);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}



}
