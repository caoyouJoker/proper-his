package com.javahis.ui.reg;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TWord;
import com.javahis.ui.emr.EMRTool;
/**
 * 
 * <p>�������ȱ����ӡԤ��</p>
 * 
 * @author wangqing 20170922
 *
 */
public class REGSavePrtAndPreviewControl extends TControl {

	/**
	 * ����
	 */
	private TWord word;
	/**
	 * ���
	 */
	private TParm sysParm;
	/**
	 * ��������
	 */
	private String caseNo;
	/**
	 * ������
	 */
	private String mrNo;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		// �Զ����ʼ������
		word = (TWord) this.getComponent("WORD");
		Object o = this.getParameter();
		if(o == null || !(o instanceof TParm)){
			this.messageBox("ϵͳ��������");
			return;
		}
		sysParm = (TParm) o;
		System.out.println("======��ӡsysParm="+sysParm);
		caseNo = sysParm.getValue("CASE_NO_TEXT", "TEXT");// add by wangqqing 20170922 ��������
		mrNo = sysParm.getValue("MR_NO_TXT", "TEXT");// add by wangqqing 20170922 ������
		initWord();
	}
	
	/**
	 * ��ʼ������
	 */
	public void initWord(){
		if(checkIfHasNull(new Object[]{word, sysParm})){
			return;
		}
		word.setWordParameter(sysParm);
		word.setFileName("%ROOT%\\config\\prt\\reg\\RegErdVitalsignPrint2.jhw");
	}
	
	/**
	 * ��ӡ�ϴ�
	 */
	public void onPrint(){
		if(checkIfHasNull(new Object[]{caseNo, mrNo})){
			this.messageBox("�˲���δ�Һţ����ܴ�ӡ�ϴ�");
			return;
		}
		EMRTool emrTool = new EMRTool(caseNo, mrNo, this);
		String classCode = TConfig.getSystemValue("REG_SAVE_CLASSCODE");// EMR020006
		String subclassCode = TConfig.getSystemValue("REG_SAVE_SUBCLASSCODE");// EMR02000619
		emrTool.saveEMR(word, "�������ȼ�¼", classCode, subclassCode,true);
		word.print();
	}
	
	/**
	 * У���Ƿ��п�ֵ
	 * @param arr
	 * @return true �п�ֵ     false �޿�ֵ
	 */
	public boolean checkIfHasNull(Object[] arr ){
		for(int i=0; i<arr.length; i++){
			if(arr[i]==null){
				return true;
			}
			if(arr[i] instanceof String && arr[i].toString().trim().equals("")){
				return true;
			}
		}
		return false;
	}
}
