package com.javahis.ui.reg;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TWord;
import com.javahis.ui.emr.EMRTool;
/**
 * 
 * <p>急诊抢救报表打印预览</p>
 * 
 * @author wangqing 20170922
 *
 */
public class REGSavePrtAndPreviewControl extends TControl {

	/**
	 * 报表
	 */
	private TWord word;
	/**
	 * 入参
	 */
	private TParm sysParm;
	/**
	 * 急诊就诊号
	 */
	private String caseNo;
	/**
	 * 病案号
	 */
	private String mrNo;
	
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		// 自定义初始化操作
		word = (TWord) this.getComponent("WORD");
		Object o = this.getParameter();
		if(o == null || !(o instanceof TParm)){
			this.messageBox("系统参数错误");
			return;
		}
		sysParm = (TParm) o;
		System.out.println("======打印sysParm="+sysParm);
		caseNo = sysParm.getValue("CASE_NO_TEXT", "TEXT");// add by wangqqing 20170922 急诊就诊号
		mrNo = sysParm.getValue("MR_NO_TXT", "TEXT");// add by wangqqing 20170922 病案号
		initWord();
	}
	
	/**
	 * 初始化报表
	 */
	public void initWord(){
		if(checkIfHasNull(new Object[]{word, sysParm})){
			return;
		}
		word.setWordParameter(sysParm);
		word.setFileName("%ROOT%\\config\\prt\\reg\\RegErdVitalsignPrint2.jhw");
	}
	
	/**
	 * 打印上传
	 */
	public void onPrint(){
		if(checkIfHasNull(new Object[]{caseNo, mrNo})){
			this.messageBox("此病患未挂号，不能打印上传");
			return;
		}
		EMRTool emrTool = new EMRTool(caseNo, mrNo, this);
		String classCode = TConfig.getSystemValue("REG_SAVE_CLASSCODE");// EMR020006
		String subclassCode = TConfig.getSystemValue("REG_SAVE_SUBCLASSCODE");// EMR02000619
		emrTool.saveEMR(word, "急诊抢救记录", classCode, subclassCode,true);
		word.print();
	}
	
	/**
	 * 校验是否有空值
	 * @param arr
	 * @return true 有空值     false 无空值
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
