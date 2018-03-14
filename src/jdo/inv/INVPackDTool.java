package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: 手术包组成明细表</p>
 *
 * <p>Description: 手术包组成明细表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-4-30
 * @version 1.0
 */
public class INVPackDTool extends TJDOTool {
    /**
  * 实例
  */
 public static INVPackDTool instanceObject;
 /**
  * 得到实例
  * @return INVVerifyinTool
  */
 public static INVPackDTool getInstance() {
     if (instanceObject == null)
         instanceObject = new INVPackDTool();
     return instanceObject;
 }

 /**
  * 构造器
  */
 public INVPackDTool() {
     setModuleName("inv\\INVPackDModule.x");
     onInit();
 }
 /**
  * 查找手术包明细
  * @param packCode String
  * @return TParm
  */
 public TParm getPackDetial(String packCode){
    if(packCode==null||packCode.length()==0)
        return null;
    TParm parm=new TParm();
    parm.setData("PACK_CODE",packCode);
    TParm result = query("getPackD", parm);
     if (result.getErrCode() < 0)
         err(result.getErrCode() + " " + result.getErrText());
     return result;
}
 /**
   * 查找物资明细的使用类型
   * @param packCode String
   * @return TParm
   */
  public TParm getPackType(String packCode,String invCode){
     if(packCode==null||packCode.length()==0||invCode==null||invCode.length()==0)
         return null;
     TParm parm=new TParm();
     parm.setData("PACK_CODE",packCode);
     parm.setData("INV_CODE",invCode);
     TParm result = query("getPackType", parm);
      if (result.getErrCode() < 0)
          err(result.getErrCode() + " " + result.getErrText());
      return result;
 }


}
