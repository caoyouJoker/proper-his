package jdo.emr;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

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
public class EMRClassTool extends TJDOTool {
    //使这个对象是单例的,只能初始化一个对象
     private static EMRClassTool instance = null;
//把类访问权限设为私有,不能通过构造方法实例化,只能通过静态工厂方法得到该类的对象,并且该对象只会被实例化一次
     private EMRClassTool() {

         //加载Module文件,文件格式在下面说明
         this.setModuleName("emr\\EMRClassModule.x");
         onInit();
     }

     /**
      * 静态工厂方法只产生一个实例
      * @return MyJDOTool
      */
     public static EMRClassTool getNewInstance() {
         if (instance == null) {
             instance = new EMRClassTool();
         }
         return instance;
     }
     /**
         *根节点查询
      */
     public TParm Select(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.query("select", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * 子节点查询
      */
     public TParm onQuery(TParm parm) {
         TParm result = this.query("query", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
     }


     /**
      * Insert
      *
      *
      */
     public TParm Insert(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.update("insert", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * onUpdate
      *
      *
      */
     public TParm onUpdate(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.update("update", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }



}
