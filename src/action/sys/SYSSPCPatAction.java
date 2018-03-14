package action.sys;

import jdo.spc.SYSPatinfoClientTool;
import jdo.spc.spcPatInfoSyncClient.SpcPatInfoService_SpcPatInfoServiceImplPort_Client;
import jdo.spc.spcPatInfoSyncClient.SysPatinfo;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;

public class SYSSPCPatAction extends TAction {
	public TParm getPatName(TParm parm) {
		String mrNo = parm.getValue("MR_NO");
		SYSPatinfoClientTool sysPatinfoClientTool = new SYSPatinfoClientTool(
				mrNo);
		SysPatinfo syspat = sysPatinfoClientTool.getSysPatinfo();
		SpcPatInfoService_SpcPatInfoServiceImplPort_Client serviceSpcPatInfoServiceImplPortClient = new SpcPatInfoService_SpcPatInfoServiceImplPort_Client();
		String msg = serviceSpcPatInfoServiceImplPortClient
				.onSaveSpcPatInfo(syspat);
		if (!msg.equals("OK")) {
			System.out.println(msg);
		}
		return new TParm();
	}
}
