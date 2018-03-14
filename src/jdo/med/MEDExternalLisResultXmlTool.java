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
 * Title: ���ͼ�����XML���ݴ�������
 * </p>
 * 
 * <p>
 * Description: ���ͼ�����XML���ݴ�������
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
     * ʵ��
     */
    public static MEDExternalLisResultXmlTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MEDExternalLisResultXmlTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEDExternalLisResultXmlTool();
        return instanceObject;
    }
	
	/**
	 * �������ͼ���ӿڻش���XML���ݲ�ת��ΪTParm����
	 * 
	 * @return result
	 */
	public TParm parseExternalLisResultToParm(String data) {
		TParm result = new TParm();
		try {
			StringReader read;
			// ���ڷ��ص�XML���ݲ��Ǳ�׼XML��ʽ�������Ҫ�ֿ���������
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
						result.addData("APPLICATION_NO", detail.getClinicid());// �����(HIS���뵥��)
						result.addData("ADM_TYPE", detail.getPatientCategory());// ��������
						result.addData("PAT_NAME", detail.getPatientName());// ��������
						result.addData("SEX", detail.getSex());// �Ա�
						result.addData("AGE", detail.getAge()
								+ detail.getAgeUnit());// ����
						result.addData("SAMPLE_TYPE", detail.getSampleType());// ��������
						result.addData("SAMPLE_CODE", detail.getTestCode());// ��������
						result.addData("RELEASE_DATE", detail.getApprDate());// ����ʱ��
						result.addData("TESTITEM_CODE", detail.getS());// �����Ŀ����
						result
								.addData("TESTITEM_CHN_DESC", detail
										.getSinonym());// �����Ŀ
						result
								.addData("TEST_VALUE",
										xmlSpecialCharacterProcessing(detail
												.getFinal()));// �����
						result.addData("TEST_UNIT", detail.getUnits());// ��λ
						result.addData("NORMAL_RANGE",
								xmlSpecialCharacterProcessing(detail
										.getDispLowHigh()));// �ο���Χ
						result.addData("NORMAL_RANGE_F",
								xmlSpecialCharacterProcessing(detail
										.getDispLowHighF()));// Ů�ο�ֵ
						result.addData("NORMAL_RANGE_M",
								xmlSpecialCharacterProcessing(detail
										.getDispLowHighM()));// �вο�ֵ
						result.addData("OUTLIER_SIGN", detail.getRn20());// ����쳣���
						
						// �漰�����Եļ����Ŀ
						if (StringUtils.isEmpty(detail.getLowB())
								&& StringUtils.isEmpty(detail.getHighB())) {
							result.addData("LOWER_LIMIT",
									xmlSpecialCharacterProcessing(detail
											.getDispLowHigh()));// ������Χ����
						} else {
							result.addData("LOWER_LIMIT",
									xmlSpecialCharacterProcessing(detail
											.getLowB()));// ������Χ����
						}

						result
								.addData("UPPE_LIMIT",
										xmlSpecialCharacterProcessing(detail
												.getHighB()));// ������Χ����
						
						result
								.addData("REMARK",
										xmlSpecialCharacterProcessing(detail
												.getRn10()));// ����쳣��־λ��H L��
						result.addData("TESTITEM_ENG_DESC",
								xmlSpecialCharacterProcessing(detail
										.getSynonimEn()));// ��Ŀ����Ӣ��
					}
					result.setCount(detailList.size());
				}
			}
		} catch (Exception e) {
			result.setErr(-1, "���ش����ݽ���ʧ��");
			System.out.println("���ش����ݽ���ʧ��:" + e.toString());
		}

		return result;
	}
	
	/**
	 * XML�����ַ�ת�崦��
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
