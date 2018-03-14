package action.clp;

import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.clp.CLPEVLStandmTool;
import jdo.clp.CLPEVLStanddTool;
import jdo.clp.CLPDurationTool;

/**
 * <p>Title: 临床路径</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPDuringAction extends TAction{
    public CLPDuringAction() {

    }
    /**
     * 删除时程代码
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteCLPDuring(TParm parm) {
    TParm result = new TParm();
    TConnection conn = getConnection();
    String durationCode=parm.getValue("DURATION_CODE");
    result=deleteDuringAndLeaf(durationCode,conn);
    if (result.getErrCode() < 0) {
        conn.close();
        return result;
    }
    conn.commit();
    conn.close();
    return result;
}

  private TParm deleteDuringAndLeaf(String durationCode,TConnection conn){
    TParm selectParm = new TParm();
    selectParm.setData("PARENT_CODE",durationCode);
    TParm result = CLPDurationTool.getInstance().selectData(selectParm);
    for (int i = 0; i<result.getCount();i++){
        String durCode=result.getValue("DURATION_CODE",i);
        TParm parmdeltmp=deleteDuringAndLeaf(durCode,conn);
        if(parmdeltmp.getErrCode()<0){
            return parmdeltmp;
        }
    }
    TParm delParm = new TParm();
    delParm.setData("DURATION_CODE",durationCode);
    TParm resultdel=CLPDurationTool.getInstance().deleteData(delParm,conn);
    return resultdel;
}

}
