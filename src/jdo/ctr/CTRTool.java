package jdo.ctr;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.ctr.CTRMainTool;
import jdo.sys.SYSFeeTool;

/**
 * <p>Title: 医令管控</p>
 *
 * <p>Description:医令管控</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author shibl
 * @version 1.0
 */


public class CTRTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static CTRTool instanceObject;
    /**
     * 得到实例
     *
     * @return CTRTool
     */
    public static CTRTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new CTRTool();
        }
        return instanceObject;
    }

    /**
     * 删除主表
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onMDelete(TParm parm, TConnection conn) {
        TParm mParm = new TParm();
        mParm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        mParm.setData("CONTROL_ID", parm.getValue("CONTROL_ID"));
        //删除主表数据
        TParm result = CTRMainTool.getNewInstance().onMDelete(mParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        TParm deleteparm = new TParm();
        deleteparm.setData("ORDER_CODE",
                           parm.getValue("ORDER_CODE"));
        deleteparm.setData("CONTROL_ID",
                           parm.getValue("CONTROL_ID"));
        //删除与主表相关的细表数据
        result = CTRMainTool.getNewInstance().onDDelete(deleteparm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        TParm inparm = new TParm();
//        System.out.println("删除入参" + parm.getValue("ORDER_CODE"));
        inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        inparm.setData("CRT_FLG", "N");
        //更改Sys_fee的管控标识
        result = SYSFeeTool.getInstance().onUpdateCtrflg(inparm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * 删除细表
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDDelete(TParm parm, TConnection conn) {
        TParm deleteparm = new TParm();
        deleteparm.setData("ORDER_CODE",
                           parm.getValue("ORDER_CODE_1"));
        deleteparm.setData("CONTROL_ID",
                           parm.getValue("CONTROL_ID_1"));
        deleteparm.setData("SERIAL_NO",
                           parm.getValue("SERIAL_NO"));
        TParm result = CTRMainTool.getNewInstance().onDDelete(deleteparm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入主表
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onMInsert(TParm parm, TConnection conn) {
        //插入主表数据
//         System.out.println("入参》》》》"+parm.getValue("ORDER_CODE"));
        TParm result = CTRMainTool.getNewInstance().onMInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        TParm inparm = new TParm();
        inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        inparm.setData("CRT_FLG", "Y");
//          System.out.println("出参》》》》"+inparm);
        //更改sys_fee的管控标识
        result = SYSFeeTool.getInstance().onUpdateCtrflg(inparm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
//            System.out.println("标识改变");
        return result;
    }
}
