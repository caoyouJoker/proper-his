package action.clp;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.clp.CLPCHKItemTool;
import jdo.clp.CLPORDERTypeTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: 关键诊疗项目action</p>
 *
 * <p>Description:关键诊疗项目action </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPChkItemAction extends TAction {
    public CLPChkItemAction() {

    }

    public TParm addCheckItem(TParm inParm) {
        TParm result = null;
        TConnection conn = getConnection();
        result=CLPCHKItemTool.getInstance().insertData(this.cloneTParm(inParm),conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        TParm erParm=this.cloneTParm(inParm);
        erParm.setData("ORDER_CODE",erParm.getValue("CHKITEM_CODE"));
        erParm.setData("TYPE_CODE",erParm.getValue("ORDTYPE_CODE"));
        erParm.setData("TYPE_CODE",erParm.getValue("CLP_QTY"));
        erParm.setData("TYPE_CODE",erParm.getValue("CLP_RATE"));
        erParm.setData("TYPE_CODE",erParm.getValue("CLP_UNIT"));
        erParm.setData("ORDER_FLG","N");
        result=CLPORDERTypeTool.getInstance().saveORDERType(erParm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }


    public TParm editCheckItem(TParm inParm) {
        TParm result = null;
        TConnection conn = getConnection();
        result= CLPCHKItemTool.getInstance().updateData(this.cloneTParm(inParm),conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        TParm erParm = this.cloneTParm(inParm);
        erParm.setData("ORDER_CODE", erParm.getValue("CHKITEM_CODE"));
        erParm.setData("TYPE_CODE", erParm.getValue("ORDTYPE_CODE"));
        erParm.setData("ORDER_FLG", "N");
        result=CLPORDERTypeTool.getInstance().saveORDERType(erParm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    public TParm delCheckItem(TParm inParm) {
        TParm result = null;
        TConnection conn = getConnection();
        result = CLPCHKItemTool.getInstance().deleteData(this.cloneTParm(inParm),conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        TParm delParm = this.cloneTParm(inParm);
        delParm.setData("ORDER_CODE",delParm.getValue("CHKITEM_CODE"));
        delParm.setData("ORDER_FLG","N");
        result=CLPORDERTypeTool.getInstance().delORDERType(delParm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 拷贝TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * 克隆对象
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getData(from.getNames()[i]));
        }
        return returnTParm;
    }


}
