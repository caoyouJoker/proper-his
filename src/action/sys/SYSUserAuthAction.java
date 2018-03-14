package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.wcomponent.util.TiString;



/**
 * @author whaosoft
 * 
 */
public class SYSUserAuthAction extends TAction{


	/**
	 *ÊÇ
	 */
	public TParm onSaveAuth(TParm parm){

	    TParm result = new TParm();

	    //
        TConnection connection = getConnection();

        TParm insertData = parm.getParm("INSERT");
        TParm deleteData = parm.getParm("DELETE");
        String userId = parm.getValue("USERID");

        //
        result = this.onDeleteAuth(connection, deleteData,userId);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        result = this.onSaveAuth(connection, insertData,userId);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        //
        connection.commit();
        connection.close();

        return result;
	}

    /**
     *
     * @param connection
     * @param deleteData
     * @param userId
     * @return
     */
	private TParm onDeleteAuth(TConnection connection,TParm deleteData,String userId){

		TParm result = new TParm();

		int count = deleteData.getCount();
		for( int i = 0;i < count;i++ ){

		    String groupCode = deleteData.getValue("GROUP_CODE",i);
		    String code = deleteData.getValue("CODE",i);

		    if( TiString.isNotEmpty(groupCode) && TiString.isNotEmpty(code) ){

		    	StringBuilder sb = new StringBuilder("delete from SYS_USER_AUTH WHERE ");

	            sb.append(" USER_ID = '"+userId+"' ");
	            sb.append(" AND AUTH_CODE = '"+code+"' ");
	            sb.append(" AND GROUP_CODE = '"+groupCode+"' ");

	            result = new TParm( TJDODBTool.getInstance().update(sb.toString(),connection) );
	            if(result.getErrCode() < 0)
	                return result;
		    }
		}

		return result;
	}


    /**
     *
     * @param connection
     * @param insertData
     * @param userId
     * @return
     */
	private TParm onSaveAuth(TConnection connection,TParm insertData,String userId){

		TParm result = new TParm();

		int count = insertData.getCount();
		for( int i = 0;i < count;i++ ){

		    String groupCode = insertData.getValue("GROUP_CODE",i);
		    String code = insertData.getValue("CODE",i);

		    if( TiString.isNotEmpty(groupCode) && TiString.isNotEmpty(code) ){

				StringBuilder sb = new StringBuilder("INSERT INTO SYS_USER_AUTH ( USER_ID,AUTH_CODE,GROUP_CODE ) VALUES ( ");

	            sb.append(" '"+userId+"' ,");
	            sb.append(" '"+code+"' , ");
	            sb.append(" '"+groupCode+"' ");
	            sb.append(" ) ");

	            result = new TParm( TJDODBTool.getInstance().update(sb.toString(),connection) );
	            if(result.getErrCode() < 0)
	                return result;
		    }
		}

		return result;
	}


}
