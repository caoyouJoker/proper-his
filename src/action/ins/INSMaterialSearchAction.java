package action.ins;

import jdo.ins.INSMaterialSearchTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class INSMaterialSearchAction extends TAction {
	
	/**
	 * 
	 * �˲�ѡ���ʸ�ȷ����
	 * @param admSeq
	 */
	public TParm checkSelectedAction(TParm inParm){
		if(!inParm.getValue("IN_STATUS", 0).equals("0")){
			err("ERR:" + "ֻ��״̬Ϊ���ʸ�ȷ������¼�롱�ļ�¼�ſ����");
 			return new TParm() ;
		}
		TConnection conn = getConnection();
		TParm parm = INSMaterialSearchTool.getInstance().checkedSelected(conn, inParm) ;

		if(parm.getErrCode()<0){//���δͨ��.
			conn.rollback() ;
			conn.close() ;
            err("ERR:" + parm.getErrCode() + parm.getErrText()
                    + parm.getErrName());			
			return parm ;
		}
		conn.commit() ;
		conn.close() ;
		return parm ;

	}

	/**
	 * 
	 * �˲�ȫ���ʸ�ȷ����
	 * @param admSeq
	 */	
	public TParm checkAllAction(TParm inParm){

		TConnection conn = getConnection();
		String nhiCode = inParm.getValue("HOSP_NHI_NO", 0) ;
		for (int i = 0; i < inParm.getCount(); i++) {
			if(!inParm.getValue("IN_STATUS", i).equals("0")){
//				err("ERR:" + "ֻ��״̬Ϊ���ʸ�ȷ������¼�롱�ļ�¼�ſ����");
	 			continue;
			}
			if(inParm.getValue("LOCAL_FLG", i).equals("N")){
	 			continue;
			}
			TParm parm = new TParm() ;
			parm.addData("ADM_SEQ", inParm.getValue("ADM_SEQ", i)) ;
			parm.addData("HOSP_NHI_NO", nhiCode) ;
			parm.addData("CONFIRM_NO", inParm.getValue("CONFIRM_NO", i)) ;	
			parm.setData("INS_TYPE", inParm.getValue("INS_CROWD_TYPE",i)) ;	
			TParm result = INSMaterialSearchTool.getInstance().checkedSelected(conn, parm) ;
			if(result.getErrCode()<0){//���δͨ��.
				conn.rollback() ;
				conn.close() ;
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                    + result.getErrName());			
				return result ;
			}
		}
		conn.commit() ;
		conn.close() ;
		return new TParm() ;
	}

	
	public TParm cancelSelectedAction(TParm inParm){
		TConnection conn = getConnection();
		TParm parm = INSMaterialSearchTool.getInstance().cancelSelected(conn, inParm) ;
		if(parm.getErrCode()<0){//���δͨ��.
			conn.rollback() ;
			conn.close() ;
            err("ERR:" + parm.getErrCode() + parm.getErrText()
                    + parm.getErrName());			
			return parm ;
		}
		conn.commit() ;
		conn.close() ;
		return parm ;		
	}
}
