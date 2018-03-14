package com.javahis.ui.med;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import jdo.med.MEDApplyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.CSVUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.FileUtils;

/**
 * <p>
 * Title: 外检样本数据查询
 * </p>
 * 
 * <p>
 * Description: 外检样本数据查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2017.2.24
 * @version 1.0
 */
public class MEDExtrnalLisSampleQueryControl extends TControl {

	private TTable table;
	private String lisTransHospCode;// 外检院所
	private String ip;// 文件交互服务器
	private int port;// 文件交互端口
	private String sampleExportPath;// 标本导出路径

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		table = ((TTable) getComponent("TABLE"));
		lisTransHospCode = TConfig.getSystemValue("LIS_TRANS_HOSP_CODE");
		if (StringUtils.isEmpty(lisTransHospCode)) {
			this.messageBox("配置文件中未设置外送检验院所编码");
			return;
		}
		ip = TConfig.getSystemValue("EXTRNAL_LIS.SERVER_IP");
		if (StringUtils.isEmpty(ip)) {
			this.messageBox("配置文件中未设置外送检验数据交互服务器IP");
			return;
		}
		if (StringUtils.isEmpty(TConfig
				.getSystemValue("EXTRNAL_LIS.SERVER_PORT"))) {
			this.messageBox("配置文件中未设置外送检验数据交互服务器端口号");
			return;
		} else {
			port = StringTool.getInt(TConfig
					.getSystemValue("EXTRNAL_LIS.SERVER_PORT"));
		}
		sampleExportPath = TConfig.getSystemValue("EXTRNAL_LIS.SAMPLE_EXPORT_PATH");
		if (StringUtils.isEmpty(sampleExportPath)) {
			this.messageBox("配置文件中未设置外送检验标本数据CSV导出路径");
			return;
		}
		this.onInitPage();
	}

	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", Timestamp.valueOf(today.toString()
				.substring(0, 10)
				+ " 00:00:00"));
		this.setValue("END_DATE", today);
		getRadioButton("ADM_TYPE_H").setSelected(true);
		getRadioButton("UNFINISHED").setSelected(true);
		table.setParmValue(new TParm());
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		String admType = "";

		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			this.messageBox("起止日期不能为空");
			return;
		}

		startDate = startDate.substring(0, 19).replace("-", "/");
		endDate = endDate.substring(0, 19).replace("-", "/");

		if (getRadioButton("ADM_TYPE_H").isSelected()) {
			admType = "H";
		} else if (getRadioButton("ADM_TYPE_O").isSelected()) {
			admType = "O";
		} else if (getRadioButton("ADM_TYPE_I").isSelected()) {
			admType = "I";
		}

		TParm queryParm = new TParm();
		queryParm.setData("START_DATE", startDate);
		queryParm.setData("END_DATE", endDate);
		queryParm.setData("ADM_TYPE", admType);
		queryParm.setData("CAT1_TYPE", "LIS");
		queryParm.setData("TRANS_HOSP_CODE", lisTransHospCode);
		// 完成状态
		if (getRadioButton("UNFINISHED").isSelected()) {
			queryParm.setData("FINISH_STATUS", "N");
		} else {
			queryParm.setData("FINISH_STATUS", "Y");
		}
		
		// 查询外送检验标本整合后CSV数据
		TParm result = MEDApplyTool.getInstance().queryExtrnalLisSampleData(
				queryParm);
		if (result.getErrCode() < 0) {
			this.messageBox("查询外检样本数据异常");
			System.out.println(result.getErrText());
			return;
		}
		
		int count = result.getCount();
		if (count < 1) {
			this.messageBox("查无数据");
			return;
		}
		
		table.setParmValue(result);
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "外送检验样本");
	}

	/**
	 * CSV文件上传
	 */
	public void onUpLoad() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要上传的数据");
			return;
		}

		String tempPath = "C:\\JavaHisFile\\temp\\csv";
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// 删除本地冗余文件
		FileUtils.getInstance().delAllFile(tempPath);
		file = new File(tempPath + File.separator + "export.csv");
		if (CSVUtils.getInstance().exportCsv(file, table)) {
			try {
				byte[] data = FileTool.getByte(file);
				TSocket socket = new TSocket(ip, port);
				Timestamp optTime = SystemTool.getInstance().getDate();
				if (TIOM_FileServer.writeFile(socket, sampleExportPath
						+ File.separator
						+ StringTool.getString(optTime, "yyyyMMddHHmmss")
						+ ".csv", data)) {
					this.messageBox("上传成功");
					if (getRadioButton("UNFINISHED").isSelected()) {
						// 更新MED_APPLY表的接收信息
						this.updateLisReceiveData(parm);
					}
				} else {
					this.messageBox("上传失败");
					return;
				}
			} catch (IOException e) {
				this.messageBox("取得外检标本CSV文件流异常");
				System.out.println(e.getMessage());
				return;
			}
		} else {
			this.messageBox("CSV文件导出失败");
		}
	}

	/**
	 * 更新MED_APPLY表的接收信息
	 * 
	 * @param parm
	 * @return
	 */
	private void updateLisReceiveData(TParm parm) {
		int count = parm.getCount();
		String user = Operator.getID();
		String term = Operator.getIP();
		TParm result = new TParm();
		TParm updateParm = new TParm();

		StringBuffer errMsg = new StringBuffer();
		for (int i = 0; i < count; i++) {
			updateParm = new TParm();
			updateParm.setData("LIS_RE_USER", "DIAN");
			updateParm.setData("OPT_USER", user);
			updateParm.setData("OPT_TERM", term);
			updateParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
			updateParm.setData("APPLICATION_NO", parm.getValue(
					"APPLICATION_NO", i));
			updateParm.setData("CAT1_TYPE", "LIS");
			// 更新MED_APPLY表的接收信息
			result = MEDApplyTool.getInstance().updateMedApplyLisReceiveData(
					updateParm);

			if (result.getErrCode() < 0) {
				errMsg.append("病患【" + parm.getValue("PAT_NAME", i) + "】条码号为【"
						+ parm.getValue("APPLICATION_NO", i) + "】的标本核收更新失败");
				errMsg.append("\r\n");
				err("ERR:" + result.getErrText());
				continue;
			}
		}

		if (errMsg.toString().length() > 0) {
			this.messageBox(errMsg.toString());
		}
	}

	/**
	 * 得到RadioButton对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

}
