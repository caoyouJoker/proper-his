package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class INDMonitorMedTool extends TJDOTool {

	private static INDMonitorMedTool mInstance;

	public static INDMonitorMedTool getNewInstance() {
		if (mInstance == null) {
			mInstance = new INDMonitorMedTool();
		}
		return mInstance;
	}

	public INDMonitorMedTool() {
		this.setModuleName("ind\\INDMonitorModule.x");
		onInit();
	}

	/**
	 * ��ȡ�ص���ҩƷ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectMonitorMed(TParm parm) {
		parm = this.query("selectMonitorMed", parm);
		return parm;
	}

	/**
	 * ɾ��ҩƷ���
	 * 
	 * @return
	 */
	public TParm delectMonitorMed(TParm parm) {

		parm = this.update("delectMonitorMed", parm);

		return parm;
	}

	/**
	 * ����ҩƷ���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm saveMonitorMed(TParm parm) {
		parm = this.update("saveMonitorMed", parm);
		return parm;

	}

	/**
	 * ����ҩƷ���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateMonitorMed(TParm parm) {

		parm = this.update("updateMonitorMed", parm);
		return parm;
	}

}
