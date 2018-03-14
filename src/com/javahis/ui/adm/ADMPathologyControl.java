package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.cxf.common.util.StringUtils;

import jdo.adm.ADMInpTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 病理类型窗口控制类</p>
 * 
 * <p>Description:病理类型窗口控制类</p>
 * 
 * <p>Copyright: Copyright (c) 2016</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author wukai 2016.05.24
 * @version 1.0
 */
public class ADMPathologyControl extends TControl{
	TParm acceptData = new TParm(); // 接参
	TParm initParm = new TParm(); // 初始数据
	
	@Override
	public void onInit() {
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			this.initUI(acceptData);
		}
	}
	
	/**
	 * 利用传来的参数初始化页面
	 * 从ADM_INP中查出相应的病理Code和病理备注
	 * @param acceptData
	 */
	private void initUI(TParm parm) {
		this.initQuery();
	}
	
	/**
	 * 初始化查询
	 */
	public void initQuery() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", acceptData.getData("CASE_NO"));
		parm.setData("MR_NO", acceptData.getData("MR_NO"));
		parm.setData("IPD_NO", acceptData.getData("IPD_NO"));
		// 查询病患住院信息
		TParm result = ADMInpTool.getInstance().selectall(parm);
		Object code = result.getData("PATLOGY_PRO_CODE",0);
		if(code == null) {  //没有查询到相关数据,登陆数据
			setValue("PATLOGY_DEPT_CODE",Operator.getDept());   //入组科室
			setValue("PATLOGY_DOC_CODE",Operator.getID());    //入组医生
			setValue("PATLOGY_PRO_DATE",StringTool.getTimestamp(new Date()));   //入组时间
			return;
		}
		initParm.setRowData(result);
		// 获取病患基本信息
		setValue("PATLOGY_PRO_CODE",code);  //入组项目
		setValue("PATLOGY_DEPT_CODE",result.getData("PATLOGY_DEPT_CODE", 0));   //入组科室
		setValue("PATLOGY_PRO_REMARK",result.getData("PATLOGY_PRO_REMARK",0)); //备注
		setValue("PATLOGY_DOC_CODE",result.getData("PATLOGY_DOC_CODE",0));    //入组医生
		setValue("PATLOGY_PRO_DATE",result.getData("PATLOGY_PRO_DATE",0));   //入组时间
		
	}
	
	/**
	 * 保存...就是更新ADM_INP  的相应的三个字段
	 * 更新相应的字段
	 */
	public void onSave() {
		if(checkData()) {
			TParm parm = new TParm();
			Timestamp time = (Timestamp) getValue("PATLOGY_PRO_DATE");
			if(time != null) {
				time = StringTool.getTimestamp(time.toString().substring(0, 10) + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
			}
			parm.setData("MR_NO", acceptData.getData("MR_NO").toString());
			parm.setData("DEPT_CODE",acceptData.getData("DEPT_CODE").toString());
			parm.setData("STATION_CODE", acceptData.getData("STATION_CODE").toString());
			parm.setData("PATLOGY_PRO_CODE", getValue("PATLOGY_PRO_CODE").toString());
			parm.setData("PATLOGY_PRO_DATE", (time == null) ? "" : time);
			parm.setData("PATLOGY_PRO_REMARK", getText("PATLOGY_PRO_REMARK"));
			parm.setData("PATLOGY_DEPT_CODE",getValue("PATLOGY_DEPT_CODE").toString());
			parm.setData("PATLOGY_DOC_CODE", getValue("PATLOGY_DOC_CODE").toString());
			//this.messageBox(parm.toString());
			if(ADMInpTool.getInstance().updatePatPro(parm)) {
				this.messageBox("保存成功");
			} else {
				this.messageBox("保存失败");
			}
		}
	}
	
	/**
	 * 检查元素
	 */
	public boolean checkData() {
		if(getValue("PATLOGY_PRO_CODE") == null || getText("PATLOGY_PRO_CODE").length() <= 0) {
			this.messageBox("请添加项目");
			return false; 
		}
		if(getValue("PATLOGY_PRO_DATE") == null || getText("PATLOGY_PRO_DATE").length() <= 0) {
			this.messageBox("请添加入组时间");
			return false;
		}
		return true;
	}
	/**
	 * 清空所有已填的项目
	 */
	public void onClear() {
		String linkedNames="PATLOGY_PRO_CODE;PATLOGY_DEPT_CODE;PATLOGY_DOC_CODE;PATLOGY_PRO_DATE;PATLOGY_PRO_REMARK";
		this.clearValue(linkedNames);
		setValue("PATLOGY_PRO_DATE",StringTool.getTimestamp(new Date()));   //入组时间还原
	}
}
