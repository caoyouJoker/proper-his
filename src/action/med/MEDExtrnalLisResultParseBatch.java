package action.med;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.med.MEDApplyTool;
import jdo.med.MEDExternalLisResultXmlTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 外送检验结果解析批次程序
 * </p>
 * 
 * <p>
 * Description: 外送检验结果解析批次程序
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
 * @author wangb 2017.4.17
 * @version 1.0
 */
public class MEDExtrnalLisResultParseBatch extends Patch {

	private String ip = TConfig.getSystemValue("EXTRNAL_LIS.SERVER_IP");// 外送检验数据交互服务器IP
	private int port;// 外送检验数据交互服务器端口号
	private String resultParsePath = TConfig
			.getSystemValue("EXTRNAL_LIS.RESULT_PARSE_PATH");// 外送检验结果数据XML解析路径
	private String resultBackupPath = TConfig
			.getSystemValue("EXTRNAL_LIS.RESULT_BACKUP_PATH");// 外送检验结果数据XML备份路径

    /**
     * 批次线程
     * @return boolean
     */
	public boolean run() {
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

		if (StringUtils.isEmpty(resultParsePath)) {
			System.out.println("配置文件中未设置外送检验结果解析路径");
			return false;
		}

		if (StringUtils.isEmpty(resultBackupPath)) {
			System.out.println("配置文件中未设置外送检验结果数据XML备份路径");
			return false;
		}

		TSocket socket = new TSocket(ip, port);
		// 获得XMl文件列表
		String[] files = TIOM_FileServer.listFile(socket, resultParsePath);

		if (files != null) {
			try {
				String dataStr = "";
				String applicationNo = "";
				int length = files.length;
				int seqNo = 0;
				byte[] data = null;
				TParm result = null;
				TParm queryParm = null;
				TParm medLisRptResult = null;
				Map<String, Integer> rpdtlSeqMap = null;
				Map<String, String> orderNoMap = null;
				Map<String, String> seqNoMap = null;
				List<String> applicationNoList = null;

				queryParm = new TParm();
				// 查询外送检验字典数据
				TParm externalParam = MEDApplyTool.getInstance()
						.queryMedLisExternalParam(queryParm);
				if (externalParam.getErrCode() < 0) {
					System.out.println("查询外送检验字典数据错误:"
							+ externalParam.getErrText());
					return false;
				}
				int externalParamCount = externalParam.getCount();

				for (int i = 0; i < length; i++) {
					applicationNoList = new ArrayList<String>();
					data = TIOM_FileServer.readFile(socket, resultParsePath
							+ File.separator + files[i]);

					if (data != null) {
						dataStr = new String(data, "UTF-8");
						// 解析外送检验接口回传的XML数据并转化为TParm对象
						result = MEDExternalLisResultXmlTool.getInstance()
								.parseExternalLisResultToParm(dataStr);

						if (result.getErrCode() < 0 || result.getCount() < 1) {
							System.out.println("XML转TParm失败，文件名：" + files[i]);
							continue;
						}

						int count = result.getCount();
						rpdtlSeqMap = new HashMap<String, Integer>();
						orderNoMap = new HashMap<String, String>();
						seqNoMap = new HashMap<String, String>();

						for (int j = 0; j < count; j++) {
							result.addData("CAT1_TYPE", "LIS");
							result.addData("RPDTL_SEQ", 1);
							result.addData("CRTCLUPLMT", "");
							result.addData("CRTCLLWLMT", "");
							result.addData("EXEC_DEV_CODE", "");
							result.addData("OPT_USER", "DIAN");
							result.addData("OPT_TERM", "127.0.0.1");
							result.addData("ORDER_NO", "");
							result.addData("SEQ_NO", "");
							result.addData("EXEC_DEV_DESC", "");
							result.addData("PY1", "");

							applicationNo = result
									.getValue("APPLICATION_NO", j);

							if (StringUtils.isEmpty(applicationNo)) {
								System.out.print("文件【" + files[i]
										+ "】中存在申请单号为空的数据。");
								System.out.println("病患姓名:"
										+ result.getValue("PAT_NAME", j)
										+ "，项目名称："
										+ result.getValue("TESTITEM_CHN_DESC",
												j));
								continue;
							}

							queryParm = new TParm();
							queryParm.setData("APPLICATION_NO", applicationNo);
							queryParm.setData("CAT1_TYPE", "LIS");

							if (!orderNoMap.containsKey(applicationNo)) {
								// 查询检验医嘱数据
								medLisRptResult = MEDApplyTool.getInstance()
										.queryMedApplyInfo(queryParm);

								if (medLisRptResult.getErrCode() < 0) {
									System.out.println("文件【" + files[i]
											+ "】中条码号:" + applicationNo
											+ "查询检验医嘱数据错误："
											+ medLisRptResult.getErrText());
									continue;
								}

								if (medLisRptResult.getCount() < 1) {
									System.out.println("文件【" + files[i]
											+ "】中条码号:" + applicationNo
											+ "未查询到检验医嘱数据");
									continue;
								}

								orderNoMap.put(applicationNo, medLisRptResult
										.getValue("ORDER_NO", 0));
								seqNoMap.put(applicationNo, medLisRptResult
										.getValue("SEQ_NO", 0));
							}

							// 为提高性能，重复的申请单号只校验一次避免主键冲突即可
							if (!applicationNoList.contains(applicationNo)) {
								applicationNoList.add(applicationNo);
								// 查询临检结果数据
								medLisRptResult = MEDApplyTool.getInstance()
										.queryMedLisRpt(queryParm);

								if (medLisRptResult.getErrCode() < 0) {
									System.out.println("文件【" + files[i]
											+ "】中条码号:" + applicationNo
											+ "查询临检结果数据错误:"
											+ medLisRptResult.getErrText());
									continue;
								}

								if (medLisRptResult.getCount() > 0) {
									// 删除临检结果数据
									medLisRptResult = MEDApplyTool
											.getInstance().deleteMedLisRpt(
													queryParm);
									if (medLisRptResult.getErrCode() < 0) {
										System.out.println("文件【" + files[i]
												+ "】中条码号:" + applicationNo
												+ "删除临检结果数据错误:"
												+ medLisRptResult.getErrText());
										continue;
									}
								}
							}

							if (rpdtlSeqMap.containsKey(applicationNo)) {
								seqNo = (Integer) rpdtlSeqMap
										.get(applicationNo) + 1;
								rpdtlSeqMap.put(applicationNo, seqNo);
							} else {
								rpdtlSeqMap.put(applicationNo, 1);
							}

							result.setData("RPDTL_SEQ", j, rpdtlSeqMap
									.get(applicationNo));
							// 合码医嘱暂时无法根据检验细项区分具体医嘱，因此seq_no只能取合码医嘱中的一个
							result.setData("ORDER_NO", j, orderNoMap
									.get(applicationNo));
							result.setData("SEQ_NO", j, seqNoMap
									.get(applicationNo));

							// 部分检验项目的参考值需要使用本院自定义数据
							for (int k = 0; k < externalParamCount; k++) {
								if (StringUtils.equals(result.getValue(
										"TESTITEM_CODE", j), externalParam
										.getValue("TEST_CODE", k))) {
									result.setData("LOWER_LIMIT", j,
											externalParam.getValue(
													"NORMAL_LOW", k));
									result.setData("UPPE_LIMIT", j,
											externalParam.getValue(
													"NORMAL_HIGH", k));
									result.setData("REMARK", j, "");
								}
							}

							// 插入临检结果数据
							medLisRptResult = MEDApplyTool.getInstance()
									.insertMedLisRpt(result.getRow(j));

							if (medLisRptResult.getErrCode() < 0) {
								System.out.println("文件【" + files[i] + "】中条码号:"
										+ applicationNo + "插入临检结果数据错误:"
										+ medLisRptResult.getErrText());
								continue;
							}
						}

						// 将文件备份
						if (TIOM_FileServer.writeFile(socket, resultBackupPath
								+ File.separator + files[i], data)) {
							// 删除该文件
							if (!TIOM_FileServer
									.deleteFile(socket, resultParsePath
											+ File.separator + files[i])) {
								System.out.println("文件【" + files[i] + "】删除失败");
							}
						} else {
							System.out.println("文件【" + files[i] + "】备份失败");
						}
					} else {
						System.out.println("文件【" + files[i] + "】读取失败");
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}

		return true;
	}
}
