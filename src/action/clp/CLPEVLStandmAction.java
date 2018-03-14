package action.clp;

import com.dongyang.action.TAction;
import jdo.adm.ADMTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.clp.CLPEVLStandmTool;
import java.util.Map;
import jdo.clp.CLPEVLStanddTool;
import com.javahis.ui.clp.CLPCHKItemControl;
import jdo.clp.CLPCHKItemTool;

/**
 * <p>Title: 临床路径评估项目维护Action</p>
 *
 * <p>Description:临床路径评估项目维护Action </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPEVLStandmAction extends TAction {
    public CLPEVLStandmAction() {
    }

    public TParm deleteCLPEVLStandm(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        result = CLPEVLStandmTool.getInstance().deleteData(parm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //更新类型信息前先将类型删除
        result = CLPEVLStanddTool.getInstance().deleteData(parm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    public TParm updateCLPEVLStandm(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        Map basicParmMap = (Map) parm.getData("basicInfoParm");
        TParm basicParm = new TParm(basicParmMap);
        result = CLPEVLStandmTool.getInstance().updateData(basicParm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //更新类型信息前先将类型删除
        result = CLPEVLStanddTool.getInstance().deleteData(basicParm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //更新类型信息
        Map typeListMap = (Map) parm.getData("typeList");
        TParm typeListParm = new TParm(typeListMap);
        int total = typeListParm.getCount();
        for (int i = 0; i < total; i++) {
            result = CLPEVLStanddTool.getInstance().insertData(typeListParm.
                    getRow(i), conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }

    public TParm insertCLPEVLStandm(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        Map basicParmMap = (Map) parm.getData("basicInfoParm");
        TParm basicParm = new TParm(basicParmMap);
        result = CLPEVLStandmTool.getInstance().insertData(basicParm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //更新类型信息前先将类型删除
        result = CLPEVLStanddTool.getInstance().deleteData(basicParm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //更新类型信息
        Map typeListMap = (Map) parm.getData("typeList");
        TParm typeListParm = new TParm(typeListMap);
        int total = typeListParm.getCount();
        for (int i = 0; i < total; i++) {
            result = CLPEVLStanddTool.getInstance().insertData(typeListParm.
                    getRow(i), conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }


}
