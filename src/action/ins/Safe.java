package action.ins;

import com.dongyang.data.TParm;
import jdo.ins.INSTool;
import java.util.Vector;
import manager.InsManager;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2011</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl
 * @version 1.0
 */

public class Safe {

    public static boolean CallDll_flg = true;

    private TParm getInsParm(String pipeline, String type) {
        TParm parm = new TParm();
        parm.setData("PIPELINE", pipeline);
        parm.setData("PLOT_TYPE", type);
        parm.setData("CITY", "22");
        TParm insParm = INSTool.getInstance().getInsParm(parm);
        if (insParm == null) {
            insParm.setErr( -1, "取得医保参数失败！");
            return insParm;
        }
        if (insParm.getData("IN_COLUMN") == null ||
            insParm.getData("OUT_COLUMN") == null) {
            insParm.setErr( -1, "取得医保参数失败！");
            return insParm;
        }
        TParm result = new TParm();
        result.setData("IN_COLUMN",
                       insParm.getData("IN_COLUMN"));
        result.setData("OUT_COLUMN",
                       insParm.getData("OUT_COLUMN"));
        return result;
    }


    private Vector newDataDown(String pipeline, Vector vParam, String Type) {
        Vector result = new Vector();
        TParm insParm = getInsParm(pipeline, Type);
        if (insParm == null || insParm.getErrCode() != 0) {
            result.add(insParm.getErrCode() + "");
            result.add(insParm.getErrCode());
            result.add(new Vector());
            return result;
        }
        TParm parm = new TParm();
        parm.setData("PIPELINE", pipeline);
        parm.setData("PLOT_TYPE", Type);
        //System.out.println("insParm"+insParm);
        Vector inParm = ( (TParm) insParm.getData("IN_COLUMN")).getVector(
            "COLUMN_NAME");
        //System.out.println("inParm"+inParm);
        Vector outParm = ( (TParm) insParm.getData("OUT_COLUMN")).getVector(
            "COLUMN_NAME");
        //System.out.println("outParm"+outParm);
        inParm = (Vector) inParm.get(0);
        outParm = (Vector) outParm.get(0);
        // 考虑多笔数据情况
        if (vParam.get(0) instanceof Vector) {
            for (int i = 0; i < vParam.size(); i++) {
                Vector row = (Vector) vParam.get(i);
                for (int j = 0; j < inParm.size(); j++) {
                    parm.addData(inParm.get(j) + "", row.get(j) + "");
                }
            }
        }
        else {
            // 单笔上传
            for (int i = 0; i < inParm.size(); i++) {
                parm.addData(inParm.get(i) + "", vParam.get(i) + "");
            }
        }
        TParm rs = InsManager.getInstance().safe(parm);
        if (rs.getErrCode() != 0) {
            result.add(insParm.getErrCode() + "");
            result.add(insParm.getErrCode());
            result.add(new Vector());
            return result;
        }
        int cnt = 0;
        if (outParm.size() > 2) {
            result.add(rs.getData(outParm.get(0) + "", 0));
            result.add(rs.getData(outParm.get(1) + "", 0));
            outParm.remove(0);
            outParm.remove(0);
            cnt = rs.getCount(outParm.get(0) + "");
        }
        else {
            result.add(rs.getData(outParm.get(0) + "", 0));
            return result;
        }
//    Vector  data  = new Vector();
        for (int i = 0; i < cnt; i++) {
            Vector col = new Vector();
            for (int j = 0; j < outParm.size(); j++) {
                col.add(rs.getData(outParm.get(j) + "", i));
            }
            result.add(col);
        }
        return result;
    }

    public Vector newDataDown_rs(Vector vParam, String Type) {
        return newDataDown("DataDown_rs", vParam, Type);
    }

    public Vector newDataDown_sp(Vector vParam, String Type) {
        return newDataDown("DataDown_sp", vParam, Type);
    }

    public Vector newDataUpload(Vector vParam, String Type) {
        return newDataDown("DataUpload", vParam, Type);
    }

    public Vector newDataDown_sp1(Vector vParam, String Type) {
        return newDataDown("DataDown_sp1", vParam, Type);
    }

    public Vector newDataDown_yb(Vector vParam, String Type) {
        return newDataDown("DataDown_yb", vParam, Type);
    }
}
