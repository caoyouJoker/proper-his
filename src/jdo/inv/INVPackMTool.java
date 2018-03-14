package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: 手术包字典主档</p>
 *
 * <p>Description: 手术包字典主档</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-06-01
 * @version 1.0
 */
public class INVPackMTool  extends TJDOTool {
    /**
  * 实例
  */
 public static INVPackMTool instanceObject;
 /**
  * 得到实例
  * @return INVVerifyinTool
  */
 public static INVPackMTool getInstance() {
     if (instanceObject == null)
         instanceObject = new INVPackMTool();
     return instanceObject;
 }

 /**
  * 构造器
  */
 public INVPackMTool() {
     setModuleName("inv\\INVPackMModule.x");
     onInit();
 }

 /**
  * 得到手术包包名GYSUsed
  * @param packCode String
  * @return TParm
  */
 public TParm getPackDesc(String packCode) {
     TParm result = new TParm();
     if(packCode==null||packCode.length()<=0)
         return result.newErrParm(-1,"参数不能为空");
     result.setData("IPACK_CODE", packCode);
     result = query("getPackDesc", result);
     if (result.getErrCode() < 0)
         err(result.getErrCode() + " " + result.getErrText());

     return result;
 }
}
