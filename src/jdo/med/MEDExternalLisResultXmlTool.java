package jdo.med;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.javahis.bsm.Prescription;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 外送检验结果XML数据处理工具类
 * </p>
 * 
 * <p>
 * Description: 外送检验结果XML数据处理工具类
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
 * @author wangb 2017.1.19
 * @version 1.0
 */
public class MEDExternalLisResultXmlTool {
	
	/**
     * 实例
     */
    public static MEDExternalLisResultXmlTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MEDExternalLisResultXmlTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEDExternalLisResultXmlTool();
        return instanceObject;
    }
	
	/**
	 * 解析外送检验接口回传的XML数据并转化为TParm对象
	 * 
	 * @return result
	 */
	public TParm parseExternalLisResultToParm(String data) {
		TParm result = new TParm();
		try {
			StringReader read;
			// 由于返回的XML数据并非标准XML格式，因此需要分开解析处理
			if (data.indexOf("<Error>") >= 0) {
				read = new StringReader(data.substring(0, data.lastIndexOf("</Error>") + 8));
				MEDExternalLisResultStatus resultStatus = JAXB.unmarshal(read,
						MEDExternalLisResultStatus.class);
				result.setData("CODE", resultStatus.getCode());
				result.setData("MESSAGE", resultStatus.getDescript());
			}
			
			if (data.indexOf("<ResultsDataSet>") >= 0) {
				read = new StringReader(data.substring(data.indexOf("<ResultsDataSet>")));
				MEDExternalLisResultDataSet resultDataSet = JAXB.unmarshal(read,
						MEDExternalLisResultDataSet.class);
				List<MEDExternalLisResultDetail> detailList = resultDataSet.getLisResultDetail();
				if (detailList != null) {
					for (MEDExternalLisResultDetail detail : detailList) {
						result.addData("APPLICATION_NO", detail.getClinicid());// 就诊号(HIS申请单号)
						result.addData("ADM_TYPE", detail.getPatientCategory());// 病患类型
						result.addData("PAT_NAME", detail.getPatientName());// 病患姓名
						result.addData("SEX", detail.getSex());// 性别
						result.addData("AGE", detail.getAge()
								+ detail.getAgeUnit());// 年龄
						result.addData("SAMPLE_TYPE", detail.getSampleType());// 样本类型
						result.addData("SAMPLE_CODE", detail.getTestCode());// 样本编码
						result.addData("RELEASE_DATE", detail.getApprDate());// 发布时间
						result.addData("TESTITEM_CODE", detail.getS());// 检测项目编码
						result
								.addData("TESTITEM_CHN_DESC", detail
										.getSinonym());// 检测项目
						result
								.addData("TEST_VALUE",
										xmlSpecialCharacterProcessing(detail
												.getFinal()));// 检测结果
						result.addData("TEST_UNIT", detail.getUnits());// 单位
						result.addData("NORMAL_RANGE",
								xmlSpecialCharacterProcessing(detail
										.getDispLowHigh()));// 参考范围
						result.addData("NORMAL_RANGE_F",
								xmlSpecialCharacterProcessing(detail
										.getDispLowHighF()));// 女参考值
						result.addData("NORMAL_RANGE_M",
								xmlSpecialCharacterProcessing(detail
										.getDispLowHighM()));// 男参考值
						result.addData("OUTLIER_SIGN", detail.getRn20());// 结果异常标记
						
						// 涉及阴阳性的监测项目
						if (StringUtils.isEmpty(detail.getLowB())
								&& StringUtils.isEmpty(detail.getHighB())) {
							result.addData("LOWER_LIMIT",
									xmlSpecialCharacterProcessing(detail
											.getDispLowHigh()));// 正常范围下限
						} else {
							result.addData("LOWER_LIMIT",
									xmlSpecialCharacterProcessing(detail
											.getLowB()));// 正常范围下限
						}

						result
								.addData("UPPE_LIMIT",
										xmlSpecialCharacterProcessing(detail
												.getHighB()));// 正常范围上限
						
						result
								.addData("REMARK",
										xmlSpecialCharacterProcessing(detail
												.getRn10()));// 结果异常标志位（H L）
						result.addData("TESTITEM_ENG_DESC",
								xmlSpecialCharacterProcessing(detail
										.getSynonimEn()));// 项目名称英文
					}
					result.setCount(detailList.size());
				}
			}
		} catch (Exception e) {
			result.setErr(-1, "外检回传数据解析失败");
			System.out.println("外检回传数据解析失败:" + e.toString());
		}

		return result;
	}
	
	/**
	 * XML特殊字符转义处理
	 * 
	 * @param value
	 */
	public String xmlSpecialCharacterProcessing(String value) {
		if (null != value) {
			return value.replace("&amp;", "&").replace("&lt;", "<").replace(
					"&gt;", ">").replace("&apos;", "'").replace("&quot;", "\"");
		} else {
			return "";
		}
		
	}
}
