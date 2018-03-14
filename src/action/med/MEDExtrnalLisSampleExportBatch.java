package action.med;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.med.MEDApplyTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.CSVUtils;
import com.javahis.util.FileUtils;

/**
 * <p>
 * Title: 外送检验样本批次程序
 * </p>
 * 
 * <p>
 * Description: 外送检验样本批次程序
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
 * @author wangb 2017.4.10
 * @version 1.0
 */
public class MEDExtrnalLisSampleExportBatch extends Patch {

	private String lisTransHospCode = TConfig
			.getSystemValue("LIS_TRANS_HOSP_CODE");// 外包检验院所编码
	private String ip = TConfig.getSystemValue("EXTRNAL_LIS.SERVER_IP");// 外送检验数据交互服务器IP
	private int port;// 外送检验数据交互服务器端口号
	private String sampleExportPath = TConfig
			.getSystemValue("EXTRNAL_LIS.SAMPLE_EXPORT_PATH");// 外送检验标本数据CSV导出路径
	private String sampleExportTime = TConfig
			.getSystemValue("EXTRNAL_LIS.SAMPLE_EXPORT_TIME");// 外送检验标本数据批次导出截止时间点

    /**
     * 批次线程
     * @return boolean
     */
	public boolean run() {
		// 送检院所编码
		if (StringUtils.isEmpty(lisTransHospCode)) {
			System.out.println("配置文件中未设置外送检验院所编码");
			return false;
		}
		if (StringUtils.isEmpty(ip)) {
			System.out.println("配置文件中未设置外送检验数据交互服务器IP");
			return false;
		}
		if (StringUtils.isEmpty(TConfig
				.getSystemValue("EXTRNAL_LIS.SERVER_PORT"))) {
			System.out.println("配置文件中未设置外送检验数据交互服务器端口号");
			return false;
		} else {
			port = StringTool.getInt(TConfig
					.getSystemValue("EXTRNAL_LIS.SERVER_PORT"));
		}

		if (StringUtils.isEmpty(sampleExportPath)) {
			System.out.println("配置文件中未设置外送检验标本数据CSV导出路径");
			return false;
		}

		if (StringUtils.isEmpty(sampleExportTime)) {
			System.out.println("配置文件中未设置外送检验标本数据批次导出截止时间点");
			return false;
		}

		Timestamp endDate = SystemTool.getInstance().getDate();
		Timestamp startDate = StringTool.rollDate(endDate, -1);

		TParm queryParm = new TParm();
		queryParm.setData("START_DATE", startDate.toString().substring(0, 11)
				+ sampleExportTime);
		queryParm.setData("END_DATE", endDate.toString().substring(0, 11)
				+ sampleExportTime);
		queryParm.setData("ADM_TYPE", "H");
		queryParm.setData("CAT1_TYPE", "LIS");
		queryParm.setData("TRANS_HOSP_CODE", lisTransHospCode);
		queryParm.setData("FINISH_STATUS", "N");

		// 查询外送检验标本整合后CSV数据
		TParm result = MEDApplyTool.getInstance().queryExtrnalLisSampleData(
				queryParm);
		
		if (result.getErrCode() < 0) {
			System.out.println("查询外送检验标本整合后CSV数据错误");
			return false;
		} else if (result.getCount() < 1) {
			return true;
		}

		int count = result.getCount();
		String tempPath = "C:\\JavaHisFile\\temp\\csv";
		File file = new File(tempPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// 删除本地冗余文件
		FileUtils.getInstance().delAllFile(tempPath);
		file = new File(tempPath + File.separator + "export.csv");
		
		String header = "医院条码,住院门诊号,病人姓名,性别,年龄,样本类型,床号,开单时间,"
				+ "送检医生,开单科室,临场诊断,项目编码,项目中文名,标本号,送检日期,采样时间,"
				+ "标本个数,备注,迪安条码,病人联系方式,医生联系方式";
		List<String> dataList = new ArrayList<String>();
		dataList.add(header);
		
		// 根据表格列显示顺序依次拼接每行csv格式数据
		String[] nameArray = { "BAR_CODE", "APPLICATION_NO", "PAT_NAME", "SEX",
				"AGE", "SAMPLE_TYPE", "BED_NO_DESC", "ORDER_DATE",
				"ORDER_DR_DESC", "DEPT_DESC", "DIAGNOSIS", "ORDER_CODE",
				"ORDER_DESC", "SAMPLE_NO", "LIS_RE_DATE", "BLOOD_DATE",
				"SAMPLE_COUNT", "REMARKS", "DA_BAR_CODE", "PAT_TEL", "DR_TEL" };
		int nameArrayLength = nameArray.length;
		String dataStr = "";
		String tempStr = "";
		for (int j = 0; j < count; j++) {
			dataStr = "";
			for (int k = 0; k < nameArrayLength; k++) {
				tempStr = result.getValue(nameArray[k], j);
				// CSV的一个列中包含逗号的话，需要使用双引号进行修饰
				if (tempStr.contains(",")) {
					tempStr = "\"" + tempStr + "\"";
				}
				dataStr = dataStr + tempStr;
				if (k < nameArrayLength - 1) {
					dataStr = dataStr + ",";
				}
			}
			dataList.add(dataStr);
		}
		
		
		if (CSVUtils.getInstance().exportCsv(file, dataList)) {
			try {
				byte[] data = FileTool.getByte(file);
				TSocket socket = new TSocket(ip, port);
				Timestamp optTime = SystemTool.getInstance().getDate();
				if (TIOM_FileServer.writeFile(socket, sampleExportPath
						+ File.separator
						+ StringTool.getString(optTime, "yyyyMMddHHmmss")
						+ ".csv", data)) {
					// 更新MED_APPLY表的接收信息
					this.updateLisReceiveData(result);
				} else {
					System.out.println("外检标本CSV文件【"
							+ StringTool.getString(optTime, "yyyyMMddHHmmss")
							+ ".csv】上传失败");
					return false;
				}
			} catch (IOException e) {
				System.out.println("取得外检标本CSV文件流异常");
				System.out.println(e.getMessage());
				return false;
			}
		} else {
			System.out.println("外检标本CSV文件导出失败");
		}

		return true;
	}
	
	/**
	 * 更新MED_APPLY表的接收信息
	 * 
	 * @param parm
	 * @return
	 */
	private void updateLisReceiveData(TParm parm) {
		int count = parm.getCount();
		TParm result = new TParm();
		TParm updateParm = new TParm();
		
		StringBuffer errMsg = new StringBuffer();
		for (int i = 0; i < count; i++) {
			updateParm = new TParm();
			updateParm.setData("LIS_RE_USER", "DIAN");
			updateParm.setData("OPT_USER", "BATCH");
			updateParm.setData("OPT_TERM", "127.0.0.1");
			updateParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
			updateParm.setData("APPLICATION_NO", parm.getValue("APPLICATION_NO", i));
			updateParm.setData("CAT1_TYPE", "LIS");
			// 更新MED_APPLY表的接收信息
			result = MEDApplyTool.getInstance().updateMedApplyLisReceiveData(updateParm);
			
			if (result.getErrCode() < 0) {
				errMsg.append("病患【" + parm.getValue("PAT_NAME", i) + "】条码号为【"
						+ parm.getValue("APPLICATION_NO", i) + "】的标本核收更新失败");
				errMsg.append("\r\n");
				System.out.println("ERR:" + result.getErrText());
				continue;
			}
		}
		
		if (errMsg.toString().length() > 0) {
			System.out.println(errMsg.toString());
		}
	}
}
