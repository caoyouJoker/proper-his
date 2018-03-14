package action.device;
import org.apache.commons.lang.StringUtils;

import jdo.device.client.CamsCommServiceSoap_Client;

import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;

/**
 * 呼叫业务接口
 * 
 * @author lixiang
 * 
 */
public class CallNoAction extends TAction {

	public final static String RTN_PARM = "RTN_RESULT";
	public final boolean isDebug = false;
	public boolean isCall = false;

	public boolean isCall() {
		String strCallNo = TConfig.getSystemValue("IsCallNO");//
		if (strCallNo.equalsIgnoreCase("Y")) {
			this.isCall = true;
		} else {
			this.isCall = false;
		}
		return isCall;
	}

	public CallNoAction() {
	}

	/**
	 * 病人挂号叫号
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doReg(TParm parm) {

		if (isDebug) {
			System.out.println("====doReg=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		/**
		 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|" +
		 * Dr_Code.trim() + "|" + drName.trim() + "|" + clinicTypeDesc.trim() +
		 * "|" + clinicRoomDesc.trim() + "|" + Mr_No.trim() + "|" +
		 * patName.trim() + "|" + sex + "|" + birthday + "|" + QueNo.trim() +
		 * "|" + maxQue.trim() + "|" + curtQueNo.trim() + "|" +
		 * sessionDesc.trim(); System.out.println("Reg_sendString--->" +
		 * sendString);
		 **/
		if (isCall()) {
			// inter.CallNo.SendMsg(2, sendString);
			CamsCommServiceSoap_Client.getInstance().sendMsg((short) 2, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;

	}

	/**
	 * 病人退挂号叫号
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doUNReg(TParm parm) {
		if (isDebug) {
			System.out.println("====doUNReg=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		/**
		 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|" +
		 * Dr_Code.trim() + "|" + drName.trim() + "|" + clinicTypeDesc.trim() +
		 * "|" + clinicRoomDesc.trim() + "|" + Mr_No.trim() + "|" +
		 * patName.trim() + "|" + sex + "|" + birthday + "|" + Que_No.trim() +
		 * "|" + maxQue.trim() + "|" + curtQueNo.trim() + "|" +
		 * sessionDesc.trim();
		 **/
		// inter.CallNo.SendMsg(4, sendString);
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short) 4, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;
	}

	/**
	 * 医师日班表呼叫
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doRegSchDay(TParm parm) {
		if (isDebug) {
			System.out.println("====doRegSchDay=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)1, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;

	}

	/**
	 * 看诊重叫号
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doReCall(TParm parm) {
		if (isDebug) {
			System.out.println("====doReCall=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)6, parm.getValue("msg"));
			if (StringUtils.isNotEmpty(parm.getValue("msg"))
					&& parm.getValue("msg").contains("|")) {
				// 获取当前叫号病患信息
				result = CamsCommServiceSoap_Client.getInstance()
						.getCurrentCallCamsInfo(
								parm.getValue("msg").split("\\|")[1]);
			}
		}
		result.setData(RTN_PARM, "true");
		return result;

	}

	/**
	 * 看诊下一个
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doNextCall(TParm parm) {
		if (isDebug) {
			System.out.println("====doNextCall=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)5, parm.getValue("msg"));
			if (StringUtils.isNotEmpty(parm.getValue("msg"))
					&& parm.getValue("msg").contains("|")) {
				// 获取当前叫号病患信息
				result = CamsCommServiceSoap_Client.getInstance()
						.getCurrentCallCamsInfo(
								parm.getValue("msg").split("\\|")[1]);
			}
		}
		result.setData(RTN_PARM, "true");
		return result;
	}

	/**
	 * 标本采集排队叫号排队
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doLabQueueCall(TParm parm) {
		if (isDebug) {
			System.out.println("====doLabQueueCall======="
					+ parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)7, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;
	}

	/**
	 * 标本采集重叫
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doLabReCall(TParm parm) {
		if (isDebug) {
			System.out.println("====doLabReCall=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)6, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;

	}

	/**
	 * 标本采集下－个
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doLabNextCall(TParm parm) {
		if (isDebug) {
			System.out.println("====doLabNextCall======="
					+ parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)5, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;
	}

	/**
	 * 发药叫号
	 * 
	 * @return
	 */
	public TParm doPHACallNo(TParm parm) {
		if (isDebug) {
			System.out.println("====doPHACallNo=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)8, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;
	}
	/**
	 * 药房已报到
	 * @param parm
	 * @return
	 */
	public TParm doPHAArriveCallNo(TParm parm){
		if (isDebug) {
			System.out.println("====doPHAArriveCallNo=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)10, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;
		
	}

	/**
	 * 电生理报到
	 * 
	 * @param parm
	 * @return
	 */
	public TParm doExmCallNo(TParm parm) {
		if (isDebug) {
			System.out.println("====doExmCallNo=======" + parm.getValue("msg"));
		}
		TParm result = new TParm();
		if (isCall()) {
			CamsCommServiceSoap_Client.getInstance().sendMsg((short)9, parm.getValue("msg"));
		}
		result.setData(RTN_PARM, "true");
		return result;

	}

}
