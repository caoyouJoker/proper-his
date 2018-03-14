package jdo.aci;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;

/**
 * <p>Title: 差错与事故管理工具类</p>
 *
 * <p>Description: 差错与事故管理工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.05
 * @version 1.0
 */
public class ACIRecordTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ACIRecordTool instanceObject;
    /**
     * 得到实例
     * @return ACIRecordTool
     */
    public static ACIRecordTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ACIRecordTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ACIRecordTool() {
        setModuleName("aci\\ACIRecordModule.x");
        onInit();
    }

    /**
     * 新增差错与事故
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String recordNo = SystemTool.getInstance().getNo("ALL", "ACI",
            "RECORD_NO",
            "RECORD_NO"); //调用取号原则
        parm.setData("ACI_RECORD_NO", recordNo);
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新差错与事故
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询差错与事故
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除差错与事故
     * @param ACIRecordNo String
     * @return TParm
     */
    public TParm deletedata(String ACIRecordNo) {
        TParm parm = new TParm();
        parm.setData("ACI_RECORD_NO", ACIRecordNo);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public static void main(String args[]) {
        com.dongyang.util.TDebug.initClient();
        //System.out.println(ClinicRoomTool.getInstance().queryTree("1"));
//        System.out.println(ClinicRoomTool.getInstance().getClinicRoomForAdmType(
//            "E"));
    }
}
