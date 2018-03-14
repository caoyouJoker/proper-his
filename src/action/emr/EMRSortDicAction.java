package action.emr;

import jdo.mro.EMRSortDicTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class EMRSortDicAction extends TAction {
	TConnection connection;
    public EMRSortDicAction() {
    	
    }
    /**
     * ɾ������
     * @param parm TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
    	TParm result = new TParm();
        //�����񷢲�����
        TConnection connection = getConnection();
        //����Tool���淽����
        result = EMRSortDicTool.getInstance().onDelete(parm, connection);

        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ����
     */
    public TParm onSaveEmrSortDic(TParm parm){
    	TConnection connection = getConnection();
		TParm result = new TParm();
    	
		TParm emrSortDic = parm.getParm("EMRSORTDIC");
		result = EMRSortDicTool.getInstance().onSaveEmrSortDic(emrSortDic, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		
		TParm emrSortDicDetail = parm.getParm("EMRSORTDICDETAIL");
		result = EMRSortDicTool.getInstance().onSaveEmrSortDicDetail(emrSortDicDetail, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
    	
		connection.commit();
		connection.close();
		return result;
    }
    /**
     * �޸�
     */
    public TParm onUpdateEmrSortDic(TParm parm){
    	TConnection connection = getConnection();
		TParm result = new TParm();
		
		TParm emrSortDic = parm.getParm("EMRSORTDIC");
		result = EMRSortDicTool.getInstance().onUpdateEmrSortDic(emrSortDic, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		
		//TParm emrSortDicDetail = parm.getParm("EMRSORTDICDETAIL");
		//result = EMRSortDicTool.getInstance().onUpdateEmrSortDicDetail(emrSortDicDetail, connection);
		TParm emrSortDicDetail = parm.getParm("EMRSORTDICDETAIL");
		result = EMRSortDicTool.getInstance().onSaveEmrSortDicDetail(emrSortDicDetail, connection);

		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
    	
		connection.commit();
		connection.close();
		return result;
    }
}
