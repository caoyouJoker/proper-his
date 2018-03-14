package action.reg;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import jdo.inv.INVSQL;
import jdo.inv.INVTool;
import jdo.reg.REGAutoBatchPatchTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

public class REGAutoBatchPatch extends Patch {
	private static final String Flag_Y = "Y";
	private static final String Flag_N = "N";
	private static final String Flag_ONE = "1";
	private static final String Flag_ZERO = "0";
	private static final String Miss_Day="180";//爽约天数 6个月
	/**
	 * @param args         
	 */
	public static void main(String[] args){
		
	} 

	
	public boolean run() {

		TConnection connection = TDBPoolManager.getInstance().getConnection();	
		
		//将超过6个月的黑名单用户清除
		String missSql = "SELECT MR_NO FROM SYS_PATINFO WHERE TRUNC(SYSDATE,'DD')-BLACK_DATE >="+Miss_Day+" AND BLACK_FLG = 'Y'";
		TParm mParm = new TParm(TJDODBTool.getInstance().select(missSql));
		System.out.println("mParm==="+mParm);
		for (int i = 0; i < mParm.getCount(); i++) {
			String mrNo=mParm.getValue("MR_NO", i);
			TParm missParm = new TParm();
			missParm.setData("MR_NO", mrNo);
			TParm removeParm = REGAutoBatchPatchTool.getInstance().removeBlackFlg(missParm,connection);
			if(removeParm.getErrCode() < 0){
				connection.rollback();
				connection.close();
				return false;
			}
			
		}
		
		
		  Timestamp date = SystemTool.getInstance().getDate();
		  String dates = date.toString().replace("/", "").replace("-", "").substring(0,9);
		  String sql = "SELECT A.ADM_DATE,A.MR_NO,B.MISS_COUNT,B.BLACK_FLG FROM REG_PATADM A,SYS_PATINFO B " +
				"  WHERE A.ADM_DATE BETWEEN TO_DATE('"+dates+"000000"+"','YYYYMMDDHH24MISS') AND TO_DATE('"+dates+"235959"+"','YYYYMMDDHH24MISS')" +
		  //"  WHERE A.ADM_DATE BETWEEN TO_DATE('"+dates+"+"dates"','YYYYMMDDHH24MISS') AND TO_DATE('20130906235959','YYYYMMDDHH24MISS')" +
				"  AND A.MR_NO = B.MR_NO(+)" +
				"  AND A.APPT_CODE = 'Y'  " +//预约
				"  AND A.ARRIVE_FLG = 'N'"+//未报到
		        "  AND B.BLACK_FLG = 'N'";//不在黑名单
//	     System.out.println("sql----->"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<0){
			connection.commit();
			connection.close();
			return true;
		}
		for(int i = 0;i<parm.getCount();i++){
		    Timestamp adm_date = parm.getTimestamp("ADM_DATE",i);
		    //System.out.println("adm_date--->"+adm_date);
		    //System.out.println("date--->"+date);
		    if(adm_date.compareTo(date)<0){
		    	//System.out.println("进来了。。。");
		    	int miss_count = Integer.parseInt(parm.getData("MISS_COUNT",i).toString());
				String mr_no = parm.getData("MR_NO",i).toString();
				
				TParm updParm = new TParm();
				updParm.setData("MR_NO",mr_no);
				if(miss_count<3){				
					TParm result = REGAutoBatchPatchTool.getInstance().updMissCount(updParm, connection);
					if(result.getErrCode()<0){
						connection.rollback();
						connection.close();
						return false;					
					}				
				}else{
					TParm result = REGAutoBatchPatchTool.getInstance().updBlackFlg(updParm, connection);				
					if(result.getErrCode()<0){
						connection.rollback();
						connection.close();
						return false;					
					}
				}
		    }
			
			
			
		}
		connection.commit();
		connection.close();
		return true;
	}

	


}
