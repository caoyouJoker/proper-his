package com.javahis.ui.adm;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import jdo.adm.ADMReferralTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 转诊转出申请
 * </p>
 * 
 * <p>
 * Description: 转诊转出申请
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2015.8.24
 * @version 1.0
 */
public class ADMReferralApplyOutControl extends TControl {
	
	private TTable table;
	private String tempPath = "C:\\JavaHisFile\\temp\\hl7";
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
    	
    	File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
    	
    	this.onInitPageControl();
    }
    
    /**
     * 初始化界面控件
     */
    private void onInitPageControl() {
    	Timestamp today = SystemTool.getInstance().getDate();
    	this.setValue("REF_APPLY_DATE_S", StringTool.rollDate(today, -7));
    	this.setValue("REF_APPLY_DATE_E", today);
    }
    
    
    /**
     * 查询方法
     */
	public void onQuery() {

		table.setParmValue(new TParm());

		// 获取查询条件数据
		TParm queryParm = this.getQueryParm();

		if (queryParm.getErrCode() < 0) {
			this.messageBox(queryParm.getErrText());
			return;
		}
		
		// 查询转诊转出申请表
		TParm result = ADMReferralTool.getInstance().queryAdmReferralOut(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询转诊转出申请数据失败");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		if (result.getCount() < 1) {
			this.messageBox("查无数据");
			return;
		}
		
		table.setParmValue(result);
	}
    
    /**
     * 获得界面查询条件
     */
    private TParm getQueryParm() {
    	TParm parm = new TParm();
    	if (StringUtils.isEmpty(this.getValueString("REF_APPLY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("REF_APPLY_DATE_E"))) {
			table.setParmValue(new TParm());
			parm.setErr(-1, "请输入查询时间");
    		return parm;
    	}
    	
		parm.setData("REF_APPLY_DATE_S", this.getValueString("REF_APPLY_DATE_S")
				.substring(0, 10).replace("-", "/"));
		parm.setData("REF_APPLY_DATE_E", this.getValueString("REF_APPLY_DATE_E")
				.substring(0, 10).replace("-", "/"));
		
		if (StringUtils
				.isNotEmpty(this.getValueString("ACCEPT_HOSP_CODE").trim())) {
			parm.setData("ACCEPT_HOSP_CODE", this
					.getValueString("ACCEPT_HOSP_CODE").trim());
		}

		if (StringUtils.isNotEmpty(this.getValueString("MR_NO").trim())) {
			parm.setData("MR_NO", this.getValueString("MR_NO").trim());
		}
		
    	return parm;
    }
    
    /**
     * 清空方法
     */
    public void onClear() {
		clearValue("ACCEPT_HOSP_CODE;MR_NO");
		this.onInitPageControl();
		table.setParmValue(new TParm());
    }
    
    /**
     * 打开转诊申请单
     */
    public void onShowReferral() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选中一行数据");
			return;
		}
    	TParm selectedParm = table.getParmValue().getRow(row);
    	
    	this.openDialog("%ROOT%/config/adm/ADMReferralApply.x", selectedParm);
    }
    
    /**
     * 打开转诊原始HL7文件
     */
    public void onOpenRefHl7() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选中一行数据");
			return;
		}
		
    	TParm selectedParm = table.getParmValue().getRow(row);
    	// 从配置文件获取BpelData原始文件的存放路径
    	String rootPath = TIOM_FileServer.getPath("BpelData");
    	
    	if (StringUtils.isEmpty(rootPath)) {
    		this.messageBox("读取BpelData原始文件的存放路径错误");
    		return;
    	}
    	
    	TSocket socket = TIOM_FileServer.getSocket();
    	String applyDate = selectedParm.getValue("APPLY_DATE");
    	// 拼接文件完整存放路径
		String filePath = rootPath
				+ applyDate.substring(0, 10).replaceAll("-", "\\\\") + "\\"
				+ selectedParm.getValue("CASE_NO");
    	
    	String fileNames[] = TIOM_FileServer.listFile(socket, filePath);
    	if (null == fileNames) {
    		this.messageBox(filePath + "路径下未找到文件");
    		return;
    	}
    	int fileCount = fileNames.length;
    	String fileName = "";
    	
    	for (int i = 0; i < fileCount; i++) {
    		// 找到该申请单号的文件
    		if (fileNames[i].contains("REF_" + selectedParm.getValue("REFERRAL_NO"))) {
    			fileName = fileNames[i];
    			break;
    		}
    	}
    	
    	if (StringUtils.isEmpty(fileName)) {
    		this.messageBox("未找到指定文件");
    		return;
    	}
    	
		byte data[] = TIOM_FileServer.readFile(socket, filePath + "\\" + fileName);
		if (data == null) {
			messageBox("服务器上没有找到文件 " + filePath + "\\" + fileName);
			return;
		}
		
    	try {
    		FileTool.setByte(tempPath + "\\" + fileName, data);
    		// 调用cmd打开文件
			Runtime.getRuntime().exec("cmd.exe /c start "+ tempPath + "\\" + fileName);
		} catch (IOException e) {
			this.messageBox("打开文件错误");
			err("打开转诊HL7文件错误:" + e.getMessage());
		}
    }
    
    /**
	 * 根据病案号查询
	 */
	public void onQueryByMrNo() {
		// 取得病案号
		String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("查无此病案号");
				return;
			}
			
			// modify by huangtt 20160928 EMPI患者查重提示 start
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());

			}
			// modify by huangtt 20160928 EMPI患者查重提示 end
			
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}
    
	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
}
