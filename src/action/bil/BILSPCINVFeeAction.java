package action.bil;

import jdo.bil.SPCINVRecordTool;
import jdo.ibs.IBSTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class BILSPCINVFeeAction extends TAction{

	
	/**
	 * 计费方法
	 * */
	public TParm countFee(TParm parm1,TParm parm2){
		System.out.println("计费方法  ws专用  start");
		TParm result = new TParm();
		TParm forIBSParm1 = new TParm();
		//forIBSParm1 = parm.getParm("forIBSParm1");
		forIBSParm1 = parm1;
		System.out.println("forIBSParm1--->"+forIBSParm1);
		TParm IBSOrddParm = new TParm();
		//IBSOrddParm = parm.getParm("IBSOrddParm");
		IBSOrddParm = parm2;
		System.out.println("IBSOrddParm------>"+IBSOrddParm);
		TConnection connection = getConnection();
		TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(
				forIBSParm1);
        System.out.println("resultFromIBS:::===="+resultFromIBS);
		
		TParm forIBSParm2 = new TParm();
		forIBSParm2.setData("DATA_TYPE", "5"); // 耗费记录调用标记5
		forIBSParm2.setData("M", resultFromIBS.getData());
		forIBSParm2.setData("FLG", IBSOrddParm.getData("FLG"));//parm
		System.out.println("forIBSParm2::==="+forIBSParm2);
		//System.out.println();
	
		// 调用IBS提供的Tool继续执行
		result = SPCINVRecordTool.getInstance().insertIBSOrder(forIBSParm2,
				connection);
		if(result.getErrCode()<0){
			System.out.println(result.getErrText());
			connection.rollback();
			connection.close();
		}
		System.out.println("计费方法  ws专用  end");
		return result;
	}
}
