package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

public class INVPublicTool  extends TJDOTool {
    /**
     * 实例
     */
    public static INVPublicTool instanceObject;
    /**
     * 得到实例
     * @return INVVerifyinTool
     */
    public static INVPublicTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVPublicTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INVPublicTool() {
        setModuleName("inv\\PublicModule.x");
        onInit();
    }
    /**
     * 得到物资科室的所有人员
     * @return TParm
     */
    public TParm getInvOperator(){
        TParm result=this.query("getInvOperator");
        if(result.getErrCode()<0)
            out(result.getErrText());
        return result;
    }
    /**
     * 得到当前人员的默认科室
     * @return TParm
     */
    public TParm getOperatorDept(String userId){
        TParm result=new TParm();
        if(userId==null||userId.length()==0)
            return result.newErrParm(-1,"参数不能为空");
        result.setData("USER_ID",userId);
        result=this.query("getOperatorDept",result);
       if(result.getErrCode()<0)
           out(result.getErrText());
       return result;
    }
    /**
     * 查找物资的科室
     * @param orgType String
     * @return TParm
     */
    public TParm getOrgCode(String orgType) {
        TParm result = new TParm();
        if (orgType == null || orgType.length() == 0)
            return result.newErrParm( -1, "参数不能为空");
        result.setData("ORG_TYPE", orgType);
        result = this.query("getOrgCode", result);
        if (result.getErrCode() < 0)
            out(result.getErrText());
        return result;
    }
    /**
     * 得到供应室的名称
     * @return TParm
     */
    public TParm getOrgDesc(){
        TParm  result = this.query("getOrgDesc");
       if (result.getErrCode() < 0)
           out(result.getErrText());
       return result;

    }
}
