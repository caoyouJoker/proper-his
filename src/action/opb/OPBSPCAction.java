package action.opb;

import jdo.pha.client.PHADosageWsImplService_Client;
import jdo.pha.client.SpcOpdOrderReturnDto;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title: 门诊收费物联网动作类
 * </p>
 * 
 * <p>
 * Description:门诊收费物联网动作类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author zhangp 2013.5.9
 * @version 1.0
 */
public class OPBSPCAction extends TAction {

	public TParm getPhaStateReturn(TParm spcParm) {
		String caseNo = spcParm.getValue("CASE_NO");
		String rxNo = spcParm.getValue("RX_NO");
		String seqNo = spcParm.getValue("SEQ_NO");
		PHADosageWsImplService_Client phaDosageWsImplServiceClient = new PHADosageWsImplService_Client();
		SpcOpdOrderReturnDto spcOpdOrderReturnDto = phaDosageWsImplServiceClient.getPhaStateReturn(caseNo, rxNo, seqNo);
		TParm result = new TParm();
		if(spcOpdOrderReturnDto == null){
			result.setErrCode(-2);
			return result;
		}
		result.setData("PhaCheckCode", spcOpdOrderReturnDto.getPhaCheckCode());
		result.setData("PhaRetnCode", spcOpdOrderReturnDto.getPhaRetnCode());
		result.setData("PhaDosageCode", spcOpdOrderReturnDto.getPhaDosageCode());
		return result;
	}
}
